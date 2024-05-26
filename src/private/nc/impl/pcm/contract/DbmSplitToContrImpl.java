package nc.impl.pcm.contract;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pub.filesystem.IFileSystemService;
import nc.bs.trade.business.HYPubBO;
import nc.fi.arap.pubutil.RuntimeEnv;
import nc.itf.pcm.contract.DbmSplitToContr;
import nc.itf.pcm.contract.pvt.IContract;
import nc.itf.pcm.contractalter.prv.IContractAlter;
import nc.itf.uap.pf.IWorkflowMachine;
import nc.itf.uap.pf.IplatFormEntry;
import nc.jdbc.framework.exception.DbException;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.pub.billcode.itf.IBillcodeManage;
import nc.ui.pcm.utils.GetDao;
import nc.vo.pcm.contract.ContrHeadVO;
import nc.vo.pcm.contract.ContrWorksVO;
import nc.vo.pcm.contract.ContractBillVO;
import nc.vo.pcm.contractalter.ContrAlterBillVO;
import nc.vo.pcm.contractalter.ContrAlterBodyVO;
import nc.vo.pcm.contractalter.ContrAlterHeadVO;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pm.feebalance.PmFeebalanceBVO;
import nc.vo.pm.feebalance.PmFeebalanceHVO;
import nc.vo.pm.rlcontractalter.AggRLContractalterHVO;
import nc.vo.pm.rlcontractalter.RLContractalterBVO;
import nc.vo.pm.rlcontractalter.RLContractalterHVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.pub.filesystem.NCFileNode;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.workflownote.WorkflownoteVO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jfree.util.Log;

import uap.pub.fs.client.FileStorageClient;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

// 多编码合同拆清单发包合同接口impl
@SuppressWarnings({ "rawtypes", "unchecked", "deprecation", "hiding", "unused" })
public class DbmSplitToContrImpl implements DbmSplitToContr {
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);

	// 多编码合同拆清单发包
	public String SplitToContr(final AggPmFeebalance dbmAggVO)
			throws BusinessException, IOException {
		AggPmFeebalance newAggvo = (AggPmFeebalance) dbmAggVO.clone();
		String mes = "";
		PmFeebalanceHVO dbmhvo = newAggvo.getParent();
		Object conforg = (Object) new HYPubBO().findColValue("bd_defdoc",
				"code",
				"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
						+ "FROM BD_DEFDOCLIST WHERE CODE = 'DBM2QDORG')"
						+ " and code = '" + dbmhvo.getPk_org() + "'");// 组织
		if (conforg == null) {
			return mes;
		}
		if (dbmhvo.getFstatusflag() != 1) {
			return "合同状态未通过！";
		}
		if (dbmhvo.getDef19() != null && "Y".equals(dbmhvo.getDef19())) {
			return "合同已拆分，无需再次拆分！";
		}
		PmFeebalanceBVO[] dbmchildvos = newAggvo.getChildrenVO();
		// 项目主键 Set集合
		Set xmset = new HashSet<>();
		for (PmFeebalanceBVO pmFeebalanceBVO : dbmchildvos) {
			xmset.add(pmFeebalanceBVO.getPk_project());
		}
		// 项目主键，有多少个不重复项目就生成多少个合同
		Object[] projectvos = xmset.toArray();
		for (int j = 0; j < projectvos.length; j++) {
			try {
				List<PmFeebalanceBVO> newChildvos = new ArrayList<PmFeebalanceBVO>();
				String pk_project = (String) projectvos[j];
				// 按项目生成清单发包合同
				UFDouble sdz = UFDouble.ZERO_DBL;// 审定值
				UFDouble yhtje = UFDouble.ZERO_DBL;// 原合同金额
				UFDouble htje = UFDouble.ZERO_DBL;// 合同金额
				UFDouble yfkje = UFDouble.ZERO_DBL;// 已付款金额（取工程合同付款（按项目）累计付款金额）
				String yfksql = "SELECT NVL(LJFKJE,0) LJFKJE FROM VIEW_GCHTBYHTXM WHERE HTBM = '"
						+ newAggvo.getParent().getBill_code()
						+ "' "
						+ "AND PK_PROJECT = '" + pk_project + "'";
				List<Object[]> yfkls = getDao.query(yfksql);
				// 如果有满足条件数据-->执行
				if (yfkls != null && yfkls.size() > 0 && yfkls.get(0) != null
						&& yfkls.get(0)[0] != null) {
					yfkje = new UFDouble(yfkls.get(0)[0].toString());
				}
				for (PmFeebalanceBVO pmFeebalanceBVO : dbmchildvos) {
					if (pmFeebalanceBVO.getPk_project() != null
							&& pk_project.equals(pmFeebalanceBVO
									.getPk_project())) {
						pmFeebalanceBVO.setStatus(VOStatus.NEW);
						sdz = sdz
								.add(pmFeebalanceBVO.getExaminationvalue() == null ? UFDouble.ZERO_DBL
										: pmFeebalanceBVO.getExaminationvalue());// 审定值
						yhtje = yhtje
								.add(pmFeebalanceBVO.getProtocontractmoney() == null ? UFDouble.ZERO_DBL
										: pmFeebalanceBVO
												.getProtocontractmoney());// 原合同金额
						htje = htje.add(pmFeebalanceBVO.getMoney());// 合同金额
						newChildvos.add(pmFeebalanceBVO);
					}
				}
				PmFeebalanceBVO[] bvos = new PmFeebalanceBVO[newChildvos.size()];
				for (int k = 0; k < newChildvos.size(); k++) {
					bvos[k] = newChildvos.get(k);
				}
				newAggvo.setChildrenVO((CircularlyAccessibleValueObject[]) bvos);
				/*
				 * DbmSplitToContr splitItf = NCLocator.getInstance().lookup(
				 * DbmSplitToContr.class);
				 */
				// 生成的清单发包合同
				ContractBillVO qdAggVO = (ContractBillVO) nc.bs.pub.pf.PfUtilTools
						.runChangeData("4Z01", "4D42", newAggvo);
				ContrHeadVO hvo = qdAggVO.getParentVO();
				IBillcodeManage iBillcodeManage = (IBillcodeManage) NCLocator
						.getInstance().lookup(IBillcodeManage.class.getName());
				String standardCode = null;
				standardCode = iBillcodeManage.getBillCode_RequiresNew("4D42",
						hvo.getPk_group(), hvo.getPk_org(), hvo);
				// 修改合同表头数据
				qdAggVO.getParentVO().setBill_code(standardCode);// 合同编码
				qdAggVO.getParentVO().setHdef26(yhtje.toString());// 原合同金额
				qdAggVO.getParentVO().setHdef27(yfkje.toString());// 已付款金额
				qdAggVO.getParentVO().setHdef28(sdz.toString());// 审定值
				qdAggVO.getParentVO().setNcurrent_mny(htje);// 合同金额
				qdAggVO.getParentVO().setStatus(VOStatus.NEW);
				// 修改清单发包合同合同基本的行号
				ContrWorksVO[] worksvo = (ContrWorksVO[]) qdAggVO
						.getChildren(ContrWorksVO.class);
				for (int k = 0; k < worksvo.length; k++) {
					worksvo[k].setRowno(((k + 1) * 10) + "");// 行号 行数*10
					worksvo[k].setStatus(VOStatus.NEW);
				}
				qdAggVO.setChildren(ContrWorksVO.class, worksvo);
				// 保存清单发包合同
				IContract qditf = NCLocator.getInstance().lookup(
						IContract.class);
				try {
					// 保存合同
					ContractBillVO[] vos = qditf
							.insertContr(new ContractBillVO[] { qdAggVO });
					// 上传附件
					mes = createFile(newAggvo.getPrimaryKey(), vos[0]
							.getParentVO().getPrimaryKey());
					if (StringUtils.isNotEmpty(mes)) {
						qditf.deleteContr(vos);
						return mes;
					}
					// 提交合同
					ContractBillVO[] tjhvos = qditf.commitContr(vos);
					// 审批合同
					String adopter = (String) new HYPubBO().findColValue(
							"sys_config", "config_value",
							" config_key='oaadopt_cuser'");
					InvocationInfoProxy.getInstance().setUserId(adopter);
					HashMap hmPfExParams = new HashMap();
					WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
							.getInstance().lookup(IWorkflowMachine.class))
							.checkWorkFlow("APPROVE", "4D42", tjhvos[0],
									hmPfExParams);
					if (worknoteVO != null) {
						worknoteVO.setChecknote("批准");
						worknoteVO.setApproveresult("Y");
					}
					HashMap<String, Object> eParam = new HashMap<String, Object>();
					eParam.put("notechecked", "notechecked");
					IplatFormEntry ipf = NCLocator.getInstance().lookup(
							IplatFormEntry.class);
					ipf.processAction("APPROVE", "4D42", worknoteVO, tjhvos[0],
							null, eParam);
				} catch (JSONException e) {
					mes = "->报错：" + getExceptionStr(e);
				} catch (BusinessException e) {
					mes = "->报错：" + getExceptionStr(e);
				} catch (Exception e) {
					mes = "->报错：" + getExceptionStr(e);
				}
			} catch (BusinessException e) {
				mes = "->报错：" + getExceptionStr(e);
			} catch (Exception e) {
				mes = "->报错：" + getExceptionStr(e);
			}
		}
		// 如果当前多编码合同全部拆分为清单发包合同，修改多编码字段为已拆分
		if (StringUtils.isEmpty(mes)) {
			String updsql = "UPDATE PM_FEEBALANCE_CT SET VDEF19 = 'Y' WHERE PM_FEEBALANCE = '"
					+ newAggvo.getPrimaryKey() + "'";
			try {
				getDao.executeUpdate(updsql);
			} catch (Exception e) {
				// TODO: handle exception
				mes = "->修改记录报错：" + getExceptionStr(e);
			}
		}
		return mes;
	}

	// 将原单据附件同步至新单据
	private String createFile(String pk_oldprimarykey, String pk_newprimarykey)
			throws BusinessException, DbException {
		// 附件信息
		String mes = "";
		String filesql = "select distinct fh.name,fp.filelength,fp.filedesc,fh.createtime,fp.pk_doc from sm_pub_filesystem fp left join bap_fs_header fh on fp.pk_doc=fh.path"
				+ " left join bap_fs_body fb on fh.GUID = fb.headid"
				+ " where filepath like '" + pk_oldprimarykey + "/%'";// 完整正确的sql语句
		Logger.error("filesql：" + filesql);
		List<Object[]> filels = getDao.query(filesql);
		if (filels != null && filels.size() > 0) {
			String savePath = RuntimeEnv.getNCHome() + "\\TempFile\\";// 附件保存路径
			for (int i = 0; i < filels.size(); i++) {
				JSONObject filejson = new JSONObject();
				String filename = filels.get(i)[0] + ""; // 附件名称
				String fileurl = FileStorageClient.getInstance()
						.getDownloadURL(null, filels.get(i)[4] + ""); // 附件下载Url
				try {
					downLoadFromUrl(fileurl, filename, savePath);
					String upflag = Upload(pk_newprimarykey, savePath,
							filename, "1001A110000000000HV8");
					if (upflag == null) {
						// 删除已下载的临时文件
						File directory = new File(savePath);
						FileUtils.cleanDirectory(directory);
					} else {
						mes += filename + " 上传失败！[" + upflag + "]\n";
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					mes += e.getMessage();
					e.printStackTrace();
				}
			}
		}
		Logger.error("mes===" + mes);
		System.out.println("mes===" + mes);
		Log.error("mes===" + mes);
		return mes;
	}

	// 上传
	private String Upload(String parentPath, String filepath, String filename,
			String user_code) {
		String errmsg = null;
		File file = new File(filepath + filename);
		Long fileLen = file.length();
		String creator = user_code;
		NCFileNode node = null;
		InputStream fileinput = null;
		try {
			fileinput = new FileInputStream(file);
			IFileSystemService service = (IFileSystemService) NCLocator
					.getInstance().lookup(IFileSystemService.class);
			node = service.createNewFileNodeWithStream(parentPath, filename,
					creator, fileinput, fileLen);
		} catch (FileNotFoundException e) {
			errmsg = e.getMessage();
			e.printStackTrace();
		} catch (IOException e) {
			errmsg = e.getMessage();
			e.printStackTrace();
		} catch (BusinessException e) {
			errmsg = e.getMessage();
			e.printStackTrace();
		} finally {
			try {
				fileinput.close();
			} catch (IOException e) {
				errmsg = e.getMessage();
				e.printStackTrace();
			}
		}
		return errmsg;
	}

	// 根据下载地址 下载附件
	private static void downLoadFromUrl(String urlStr, String fileName,
			String savePath) throws IOException, Exception {

		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// 设置超时间为3秒
		conn.setConnectTimeout(3 * 1000);
		// 防止屏蔽程序抓取而返回403错误
		conn.setRequestProperty("User-Agent",
				"Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
		// 得到输入流
		Thread.sleep(500);
		InputStream inputStream = conn.getInputStream();

		// 获取自己数组
		byte[] getData = readInputStream(inputStream);
		// 文件保存位置
		File saveDir = new File(savePath);
		if (!saveDir.exists()) {
			saveDir.mkdir();
		}
		File file = new File(saveDir + File.separator + fileName);
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(getData);
		if (fos != null) {
			fos.close();
		}
		if (inputStream != null) {
			inputStream.close();

		}
		System.out.println("info:" + url + " download success");
	}

	/**
	 * 
	 * 从输入流中获取字节数组
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */

	private static byte[] readInputStream(InputStream inputStream)
			throws IOException {
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((len = inputStream.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		bos.close();
		return bos.toByteArray();
	}

	// 获取输出报错参数
	private static String getExceptionStr(Exception e) throws IOException {
		// 读取异常栈信息
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		e.printStackTrace(new PrintStream(arrayOutputStream));
		// 通过ByteArray转换输入输出流
		BufferedReader fr = new BufferedReader(new InputStreamReader(
				new ByteArrayInputStream(arrayOutputStream.toByteArray())));
		String str;
		StringBuilder exceptionStr = new StringBuilder();
		while ((str = fr.readLine()) != null) {
			exceptionStr.append(str);
		}
		// 关闭流
		fr.close();
		return exceptionStr.toString();
	}

	// 多编码合同补充协议拆分清单发包合同补充协议
	public String SplitAlterToContr(final AggRLContractalterHVO aggVO)
			throws BusinessException, IOException {
		// TODO Auto-generated method stub
		AggRLContractalterHVO newAggvo = (AggRLContractalterHVO) aggVO.clone();
		String mes = "";
		RLContractalterHVO dbmhvo = newAggvo.getParent();
		Object conforg = (Object) new HYPubBO().findColValue("bd_defdoc",
				"code",
				"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
						+ "FROM BD_DEFDOCLIST WHERE CODE = 'DBM2QDORG')"
						+ " and code = '" + dbmhvo.getPk_org() + "'");// 组织
		if (conforg == null) {
			return mes;
		}
		// 多编码合同补充协议明细表
		RLContractalterBVO[] mxvos = newAggvo.getChildrenVO();
		String pm_feebalance = mxvos[0].getCsourcebillhid();// 多编码合同主键
		// 项目主键 Set集合
		Set xmset = new HashSet<>();
		for (RLContractalterBVO alterBVO : mxvos) {
			xmset.add(alterBVO.getPk_project());
		}
		// 查询service
		IMDPersistenceQueryService qryService = NCLocator.getInstance().lookup(
				IMDPersistenceQueryService.class);
		// 项目主键，有多少个不重复项目就生成多少个清单发包合同补充协议
		Object[] projectvos = xmset.toArray();
		for (int j = 0; j < projectvos.length; j++) {
			String pk_project = (String) projectvos[j];// 项目主键
			List<RLContractalterBVO> curvos = new ArrayList<RLContractalterBVO>();
			List<ContrWorksVO> newhtmxvos = new ArrayList<ContrWorksVO>();
			for (RLContractalterBVO alterBVO : mxvos) {
				if (pk_project.equals(alterBVO.getPk_project())) {
					curvos.add(alterBVO);
				}
			}
			// 按多编码合同主键+项目，取清单发包合同
			String sqlwhere = " nvl(dr,0) = 0 and src_pk_bill = '"
					+ pm_feebalance + "' and pk_project = '"
					+ pk_project + "'";
			Collection<ContractBillVO> colVOs = qryService.queryBillOfVOByCond(
					ContractBillVO.class, sqlwhere, true, false);
			if (colVOs != null && colVOs.size() > 0) {
				Object[] qdAggvos = colVOs.toArray();
				ContractBillVO aggvo = (ContractBillVO) qdAggvos[0];
				ContrWorksVO[] htmxvos = (ContrWorksVO[]) aggvo
						.getChildren(ContrWorksVO.class);
				// 外循环合同明细，内循环补充协议明细，将合同明细多余的行删除。确保补充协议多少
				for (ContrWorksVO htmxvo : htmxvos) {
					String pk_cbsnode = htmxvo.getPk_cbsnode();// CBS
					// Boolean flag = Boolean.FALSE;
					for (RLContractalterBVO newmxvo : curvos) {
						if (pk_cbsnode.equals(newmxvo.getPk_cbsnode())) {
							// flag = Boolean.TRUE;
							newhtmxvos.add(htmxvo);
						}
					}
				}
				ContrWorksVO[] newmxvos = (ContrWorksVO[]) newhtmxvos.toArray(new ContrWorksVO[0]);
				aggvo.setChildrenVO(newmxvos);
				// 生成的清单发包合同补充协议
				ContrAlterBillVO alterBillVO = (ContrAlterBillVO) nc.bs.pub.pf.PfUtilTools
						.runChangeData("4D42", "4D44", aggvo);
				ContrAlterHeadVO hvo = alterBillVO.getParentVO();
				IBillcodeManage iBillcodeManage = (IBillcodeManage) NCLocator
						.getInstance().lookup(IBillcodeManage.class.getName());
				String standardCode = null;
				standardCode = iBillcodeManage.getBillCode_RequiresNew("4D44",
						hvo.getPk_group(), hvo.getPk_org(), hvo);
				// 修改清单合同补充协议表头数据
				hvo.setBill_code(standardCode);// 合同编码
				hvo.setHdef7(dbmhvo.getBill_code());// 清单发包合同补充协议表头自定义项7记录多编码合同补充协议号
				hvo.setStatus(VOStatus.NEW);
				alterBillVO.setParentVO(hvo);
				ContrAlterBodyVO[] bcxybvos = (ContrAlterBodyVO[]) alterBillVO
						.getChildrenVO();
				UFDouble Alt_mny_all = UFDouble.ZERO_DBL;
				for (int i = 0; i < bcxybvos.length; i++) {
					ContrAlterBodyVO bcxybvo = bcxybvos[i];
					for (RLContractalterBVO contractalterBVO : curvos) {
						if (bcxybvo.getPk_cbsnode().equals(
								contractalterBVO.getPk_cbsnode())) {
							bcxybvo.setAlt_mny(contractalterBVO.getAlt_mny());// 本次协议金额
							bcxybvo.setAlt_num(contractalterBVO
									.getAlt_mny_num());// 本次协议数量
							Alt_mny_all = Alt_mny_all.add(contractalterBVO.getAlt_mny());
							bcxybvo.setStatus(VOStatus.NEW);
						}
					}
				}
				hvo.setAlt_mny(Alt_mny_all);// 表头本次协议金额
				alterBillVO.setChildrenVO(bcxybvos);
				IContractAlter bcxyitf = NCLocator.getInstance().lookup(
						IContractAlter.class);
				try {
					// 保存合同
					ContrAlterBillVO[] vos = bcxyitf
							.insertAlter(new ContrAlterBillVO[] { alterBillVO });
					// 上传附件
					mes += createFile(newAggvo.getPrimaryKey(), vos[0]
							.getParentVO().getPrimaryKey());
					if (StringUtils.isNotEmpty(mes)) {
						bcxyitf.deleteAlter(vos);
						return mes;
					}
					// 提交合同
					ContrAlterBillVO[] tjhvos = bcxyitf.commitAlter(vos);
					// 审批合同
					String adopter = (String) new HYPubBO().findColValue(
							"sys_config", "config_value",
							" config_key='oaadopt_cuser'");
					InvocationInfoProxy.getInstance().setUserId(adopter);
					HashMap hmPfExParams = new HashMap();
					WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
							.getInstance().lookup(IWorkflowMachine.class))
							.checkWorkFlow("APPROVE", "4D44", tjhvos[0],
									hmPfExParams);
					if (worknoteVO != null) {
						worknoteVO.setChecknote("批准");
						worknoteVO.setApproveresult("Y");
					}
					HashMap<String, Object> eParam = new HashMap<String, Object>();
					eParam.put("notechecked", "notechecked");
					IplatFormEntry ipf = NCLocator.getInstance().lookup(
							IplatFormEntry.class);
					ipf.processAction("APPROVE", "4D44", worknoteVO, tjhvos[0],
							null, eParam);
				} catch (JSONException e) {
					mes += "->报错：" + getExceptionStr(e);
				} catch (BusinessException e) {
					mes += "->报错：" + getExceptionStr(e);
				} catch (Exception e) {
					mes += "->报错：" + getExceptionStr(e);
				}
			}
			// 如果当前多编码合同全部拆分为清单发包合同，修改多编码字段为已拆分
			if (StringUtils.isEmpty(mes)) {
				String updsql = "UPDATE PM_RLCONTRACTALTER_CT SET HDEF19 = 'Y' WHERE PK_CONTR_ALTER = '"
						+ newAggvo.getPrimaryKey() + "'";
				try {
					getDao.executeUpdate(updsql);
				} catch (Exception e) {
					// TODO: handle exception
					mes += "->修改记录报错：" + getExceptionStr(e);
				}
			}
		}
		return mes;
	}
}