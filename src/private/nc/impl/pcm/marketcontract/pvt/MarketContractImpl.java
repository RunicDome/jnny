package nc.impl.pcm.marketcontract.pvt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pcm.contract.bp.rule.CreateOldVersionRule;
import nc.bs.pcm.marketcontract.bp.rule.AlterCheckRule;
import nc.bs.pcm.marketcontract.bp.rule.AlterDateCheckRule;
import nc.bs.pcm.marketcontract.bp.rule.BeginFlagCheckRule;
import nc.bs.pcm.marketcontract.bp.rule.DateCheckRule;
import nc.bs.pcm.marketcontract.bp.rule.DeleteHisVersionRule;
import nc.bs.pcm.marketcontract.bp.rule.FreeIncomeCheckRule;
import nc.bs.pcm.marketcontract.bp.rule.McontrAlterCheckRule;
import nc.bs.pmpub.rule.AppendBusiTypeBeforeRule;
import nc.bs.pmpub.rule.InitCurrTypeBeforeRule;
import nc.bs.pub.filesystem.IFileSystemService;
import nc.bs.trade.business.HYPubBO;
import nc.fi.arap.pubutil.RuntimeEnv;
import nc.impl.pm.billrule.BillQuoteCheckRule;
import nc.impl.pm.billrule.UpdateBillSatusRule;
import nc.impl.pm.billrule.WBSPretaskCompleteCheckRule;
import nc.impl.pmpub.servicebase.BillBaseImpl;
import nc.impl.pmpub.servicebase.action.ApproveAction;
import nc.impl.pmpub.servicebase.action.CommitAction;
import nc.impl.pmpub.servicebase.action.DeleteAction;
import nc.impl.pmpub.servicebase.action.InsertAction;
import nc.impl.pmpub.servicebase.action.OtherButtonUpdateAction;
import nc.impl.pmpub.servicebase.action.UpdateAction;
import nc.impl.pubapp.pattern.data.vo.VOUpdate;
import nc.itf.pcm.marketcontract.pvt.IMarketContract;
import nc.itf.pmbd.pub.IFieldsForInitCurrType;
import nc.ui.pcm.utils.GetDao;
import nc.uif.pub.exception.UifException;
import nc.vo.org.OrgVO;
import nc.vo.pcm.marketcontract.ContrWorksVO;
import nc.vo.pcm.marketcontract.ContractBillVO;
import nc.vo.pcm.marketcontract.ContractHeadVO;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.filesystem.NCFileNode;
import nc.vo.sm.UserVO;
import nc.ws.intf.HttpClient;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.apache.commons.lang.StringUtils;

import sun.misc.BASE64Decoder;

// 项目销售合同
@SuppressWarnings({ "rawtypes", "unchecked", "restriction", "unused" })
public class MarketContractImpl extends BillBaseImpl<ContractBillVO> implements
		IMarketContract {
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表明
	String savePath = RuntimeEnv.getNCHome() + "\\TempFile\\";// 附件保存路径

	public ContractBillVO[] alterContr(ContractBillVO[] billVOs,
			ContractBillVO[] originBillVOs) throws BusinessException {
		OtherButtonUpdateAction<ContractBillVO> action = createUpdateWithoutEventAction(
				"1231", "1232");
		initAlterAction(action, originBillVOs);
		return (ContractBillVO[]) action.processAction(billVOs, originBillVOs);
	}

	public Object approveContr(ContractBillVO[] billVOs, PfParameterVO pfParamVO)
			throws BusinessException {
		// 调用ERP接口，上传附件
		uploadFileByBillCode(billVOs);
		return approve(billVOs, pfParamVO);
	}

	// 调用ERP接口，上传附件
	private void uploadFileByBillCode(ContractBillVO[] billVOs)
			throws BusinessException {
		// TODO Auto-generated method stub
		for (int i = 0; i < billVOs.length; i++) {
			ContractBillVO contractBillVO = billVOs[i];
			ContractHeadVO headvo = contractBillVO.getParentVO();
			Object conforg = (Object) getHyPubBO()
					.findColValue(
							"bd_defdoc",
							"code",
							"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
									+ "FROM BD_DEFDOCLIST WHERE CODE = 'XMXSHT_FILEORG')"
									+ " and code = '" + headvo.getPk_org()
									+ "'");
			if (conforg != null) {
				String bill_code = headvo.getBill_code();// 合同编码
				String hdef9 = headvo.getHdef9();// 是否生成附件
				// 上传附件
				if (!"Y".equals(hdef9)) {
					String mes;
					try {
						mes = uploadByBase64(bill_code, "pdf",
								headvo.getPrimaryKey());
						if (StringUtils.isNotEmpty(mes)) {
							throw new BusinessException(mes);
						}
						deleteFile(new File(savePath));
						GetDao getDao = NCLocator.getInstance().lookup(
								GetDao.class);
						String sql = "UPDATE PM_MCONTR SET HDEF9 = 'Y' WHERE pk_mar_contr = '"
								+ headvo.getPrimaryKey() + "' AND DR = 0 ";
						getDao.updatevo(sql);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw new BusinessException(e.getMessage());
					}
				}
			}
		}
	}

	/**
	 * 删除文件夹下的所有文件
	 * 
	 * @param oldPath
	 */
	public void deleteFile(File oldPath) {
		if (oldPath.isDirectory()) {
			System.out.println(oldPath + "是文件夹--");
			File[] files = oldPath.listFiles();
			for (File file : files) {
				deleteFile(file);
			}
		} else {
			oldPath.delete();
		}
	}

	private String uploadByBase64(String bill_code, String file_type,
			String pk_primarykey) throws BusinessException, IOException {
		String ifsuccess = "";
		try {
			String basecode = getBase64Data(bill_code);
			if (basecode.startsWith("error")) {
				ifsuccess = bill_code + "上传附件失败：" + basecode.substring(5);
				return ifsuccess;
			}
			String filename = bill_code + System.currentTimeMillis() + "."
					+ file_type;// 拼装附件名称
			File file = getFileFromBase64(basecode, savePath, filename);// 下载附件到本地
			String upflag = Upload(pk_primarykey, savePath, filename,
					"1001A110000000000HV8");// 上传附件

			if (upflag == null) {
				// mes += "合同主键：" + billpk + "附件名称：" + filename + "上传成功！\n";
			} else {
				ifsuccess += filename + " 上传失败！[" + upflag + "]\n";
			}
		} catch (BusinessException e) {
			// TODO: handle exception
			e.printStackTrace();
			ifsuccess += bill_code + " 上传附件失败！[" + e.getMessage() + "]\n";
		}

		return ifsuccess;
	}

	public String Upload(String parentPath, String filepath, String filename,
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

	public File getFileFromBase64(String base, String filepath, String filename)
			throws BusinessException, IOException {
		File file = null;
		Map<String, Object> resultMap = new HashMap<String, Object>();

		BASE64Decoder decoder = new BASE64Decoder();
		byte[] b = decoder.decodeBuffer(base);// 去除base64中无用的部分

		File file1 = new File(filepath);
		if (!file1.exists() && !file1.isDirectory()) {// 判断文件路径下的文件夹是否存在，不存在则创建
			file1.mkdirs();
		}
		try {
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {// 调整异常数据
					b[i] += 256;
				}
			}
			file = new File(filepath + filename);
			// 如果要返回file文件这边return就可以了,存到临时文件中
			OutputStream out = new FileOutputStream(file.getPath());
			out.write(b);
			out.flush();
			out.close();
		} catch (Exception e) {
			resultMap.put("resultCode", 0);
			resultMap.put("msg", "存储异常" + e.getMessage());
			Logger.error(resultMap.toString());
			System.out.println(resultMap.toString());
			e.printStackTrace();
		}

		return file;
	}

	// 调用ERP接口，返回Base64编码
	private String getBase64Data(String bill_code) throws BusinessException {
		String base64code = "";
		String url = (String) new HYPubBO()
				.findColValue("sys_config", "config_value",
						" nvl(dr,0) = 0 and config_key='erpurl_mcontr'");
		Logger.error("url==" + url);
		if (StringUtils.isNotEmpty(url)) {
			Map<String, String> headers = new HashMap<>();
			url += ""
					+ bill_code
					+ "/base64/download?j_username=NCxitong&j_password=dcLkmRZqUuR9" +
					"bOktSXNVXaP0VdDFg4Hf7McDXe0Vgr4FG81aPKhV6kgIgEzZk4cLeRB+Ha/fyGFjdX" +
					"nwwriRmfa2oAeMw+T21rObVX2Vw07qZ7uFllJ4RgtiGRm6/c3tSRfkL3MFetbVJAxtD" +
					"VABNON8fXzXMgEplqcNUPMSH/Y=";
			headers.put("Zoomkey-Auth-Token", "9/wjVW5eSr0daNINI0XckQ==");
			Logger.error("发送ERP参数：" + headers.toString());
			try {
				String back = HttpClient.httpGet(url, headers, null);
				Logger.error("ERM返回参数：" + back);
				if (StringUtils.isNotEmpty(back)) {
					JSONObject res = JSONObject.fromObject(back);
					if (res.getBoolean("success")) {
						base64code = res.getString("root");
					} else {
						throw new BusinessException("ERP接口返回："
								+ res.getString("info"));
					}
				} else {
					throw new BusinessException("调用ERP获取附件Base64编码返回空数据，请检查！");
				}
			} catch (Exception e) {
				// TODO: handle exception
				return "error" + e.getMessage();
			}
		}
		return base64code;
	}

	public ContractBillVO[] commitContr(ContractBillVO[] billVOs)
			throws BusinessException {
		billVOs = (ContractBillVO[]) commit(billVOs);
		senOaData(billVOs);
		return billVOs;
	}

	// TODO 单据同步OA功能
	private HYPubBO hyPubBO;

	public HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	public void setHyPubBO(HYPubBO hyPubBO) {
		this.hyPubBO = hyPubBO;
	}

	private String billType = "4D60";

	private void senOaData(ContractBillVO[] billVOs) throws BusinessException {
		for (ContractBillVO temp : billVOs) {
			// 项目销售合同
			ContractHeadVO hVO = temp.getParentVO();
			// 期初不传OA Begin_flag
			if ("4".equals(getDef2(hVO.getPk_org()))
					&& hVO.getBill_type().contains(billType)) {
				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hVO.getPk_org(),
						"4D60");
				if (oaVo != null && oaVo.getIsdr() == 0) {
					WorkFId = oaVo.getFlowid();
					TableName = oaVo.getTablename();
					// 获取主表数据
					JSONArray headData = getMainMap(hVO);
					// 获取子表数据
					JSONArray bodyData = getDtaileDataMap(temp);
					// 构造workflow信息
					OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
					workFlowVO.setPrimaryKey(temp.getPrimaryKey());
					workFlowVO.setPkGroup(hVO.getPk_group());
					workFlowVO.setPkOrg(hVO.getPk_org());
					workFlowVO.setBillMaker(hVO.getBillmaker());
					workFlowVO.setCreator(hVO.getCreator());
					workFlowVO.setBillCode(billType);
					// 测试临时写死，后期改为接口获取
					workFlowVO.setWorkflowId(OaWorkFlowUtil
							.getOAFlowID(WorkFId));
					workFlowVO.setWorkflowName("项目销售合同");
					WorkFlowBill bill = OaWorkFlowUtil
							.getWorkFlowBill(workFlowVO);
					// WorkFlowBill的自定义项3 判断是否港华单据
					bill.setDef3("ZT");
					// 制单人身份证号
					UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
							UserVO.class, temp.getParentVO().getBillmaker());
					String idCard = (String) getHyPubBO().findColValue(
							"bd_psndoc",
							"id",
							"nvl(dr,0) = 0 and pk_psndoc='"
									+ userVO.getPk_psndoc() + "'");
					bill.setDef5(idCard);
					// 调用OA工具类同步数据至OA
					OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
				}
			}
		}
	}

	private JSONArray getMainMap(ContractHeadVO parentVO)
			throws BusinessException {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// ---------其他字段begin
		// 组织
		String stockName = (String) getHyPubBO()
				.findColValue(
						"org_itemorg",
						"name",
						"nvl(dr,0) = 0 and pk_itemorg = '"
								+ parentVO.getPk_org() + "'");
		list.add(OaWorkFlowUtil.listAddObj("zzmc", stockName));
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		// 合同类型名称
		String billTypeName = (String) getHyPubBO().findColValue(
				"pm_contracttype",
				"type_name",
				"nvl(dr,0) = 0 and pk_contracttype = '"
						+ parentVO.getPk_contracttype() + "'");
		list.add(OaWorkFlowUtil.listAddObj("typename", billTypeName));
		// 制单人用户编码
		list.add(OaWorkFlowUtil.listAddObj("usercode", userVO.getUser_code()));
		// 制单人名称
		list.add(OaWorkFlowUtil.listAddObj("username", userVO.getUser_name()));
		// 制单人身份证号
		String idCard = (String) getHyPubBO().findColValue("bd_psndoc", "id",
				"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
		if (null == idCard) {
			throw new BusinessException("制单人身份证号码未维护");
		}
		list.add(OaWorkFlowUtil.listAddObj("userid", idCard));
		// 项目
		if (parentVO.getPk_project() != null) {
			ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
					.queryByPrimaryKey(ProjectHeadVO.class,
							parentVO.getPk_project());
			if (null != projectVO) {
				// 项目编码
				list.add(OaWorkFlowUtil.listAddObj("project_code",
						projectVO.getProject_code()));
				// 项目名称
				list.add(OaWorkFlowUtil.listAddObj("project_name",
						projectVO.getProject_name()));
			}
		}
		// 项目类型名称
		String xmlx = (String) getHyPubBO().findColValue(
				"bd_projectclass",
				"type_name",
				"nvl(dr,0) = 0 and pk_projectclass  = '"
						+ parentVO.getPk_projecttype() + "'");
		list.add(OaWorkFlowUtil.listAddObj("xmlx", xmlx));
		// 客户
		String customerName = (String) getHyPubBO().findColValue(
				"bd_customer",
				"name ",
				"nvl(dr,0) = 0 and  pk_customer  = '"
						+ parentVO.getPk_customer() + "'");
		list.add(OaWorkFlowUtil.listAddObj("kh", customerName));
		// 签约人
		String apppsnhName = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc  = '"
						+ parentVO.getPk_promisepsn() + "'");
		list.add(OaWorkFlowUtil.listAddObj("qyr", apppsnhName));
		// 签约部门
		String sqbmName = (String) getHyPubBO().findColValue(
				"org_dept_v",
				"name",
				"nvl(dr,0) = 0 and pk_vid  = '" + parentVO.getPk_pro_dept_v()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("qybm", sqbmName));
		// 燃气类型
		String rqlx = (String) getHyPubBO().findColValue("bd_defdoc", "name",
				"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getHdef5() + "'");
		list.add(OaWorkFlowUtil.listAddObj("rqlx", rqlx));
		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	private JSONArray getDtaileDataMap(ContractBillVO temp)
			throws BusinessException {
		// 获取合同基本
		ContrWorksVO[] bvos = (ContrWorksVO[]) getHyPubBO().queryByCondition(
				ContrWorksVO.class,
				"nvl(dr,0) = 0 and pk_mar_contr = '" + temp.getPrimaryKey()
						+ "'");
		Map bodyMap = getBody(bvos);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
		return dtlistString;
	}

	private Map getBody(ContrWorksVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (ContrWorksVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			// 核算要素
			String hsysmc = (String) getHyPubBO().findColValue(
					"resa_factorasoa",
					"factorname",
					"nvl(dr,0) = 0 and pk_factorasoa = '"
							+ temp.getPk_checkfactor() + "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("hsys",
					hsysmc));
			// 其他字段------end
			Map workflowRequestTableFieldsMap = new HashMap();
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	public ContractBillVO[] deleteContr(ContractBillVO[] billVOs)
			throws BusinessException {
		return (ContractBillVO[]) delete(billVOs);
	}

	public ContractBillVO[] freeContr(ContractBillVO[] billVOs,
			ContractBillVO[] originBillVOs) throws BusinessException {
		OtherButtonUpdateAction<ContractBillVO> action = createUpdateWithoutEventAction(
				"1225", "1226");
		initFreeAction(action);
		return (ContractBillVO[]) action.processAction(billVOs, originBillVOs);
	}

	public void initAlterAction(UpdateAction<ContractBillVO> action,
			ContractBillVO[] originBillVOs) {
		action.addBeforeRule(new AlterDateCheckRule());
		action.addAfterRule(new AlterCheckRule(originBillVOs));
		action.addBeforeRule(new CreateOldVersionRule(originBillVOs));
		action.addBeforeRule(new InitCurrTypeBeforeRule(
				new FieldsForInitCurrType()));
	}

	public void initFreeAction(UpdateAction<ContractBillVO> action) {
		action.addBeforeRule(new UpdateBillSatusRule(10));
	}

	public void initTerminateAction(UpdateAction<ContractBillVO> action) {
		action.addBeforeRule(new DateCheckRule());
		action.addBeforeRule(new UpdateBillSatusRule(11));
		action.addBeforeRule(new FreeIncomeCheckRule());
		action.addBeforeRule(new McontrAlterCheckRule(1));
	}

	public void initUnFrozenAction(UpdateAction<ContractBillVO> action) {
		action.addBeforeRule(new UpdateBillSatusRule(9));
	}

	public void initUnValidateAction(UpdateAction<ContractBillVO> action) {
		action.addBeforeRule(new BillQuoteCheckRule());
		action.addBeforeRule(new UpdateBillSatusRule(1));
	}

	public void initValidateAction(UpdateAction<ContractBillVO> action) {
		action.addBeforeRule(new UpdateBillSatusRule(9));
	}

	public ContractBillVO[] insertContr(ContractBillVO[] billVOs)
			throws BusinessException {
		return (ContractBillVO[]) insert(billVOs);
	}

	public ContractBillVO[] terminateContr(ContractBillVO[] billVOs,
			ContractBillVO[] originBillVOs) throws BusinessException {
		OtherButtonUpdateAction<ContractBillVO> action = createUpdateWithoutEventAction(
				"1229", "1234");
		initTerminateAction(action);
		return (ContractBillVO[]) action.processAction(billVOs, originBillVOs);
	}

	public ContractBillVO[] unapproveContr(ContractBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		return (ContractBillVO[]) unApprove(billVOs, pfParamVO);
	}

	public ContractBillVO[] unCommitContr(ContractBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		billVOs = (ContractBillVO[]) unCommit(billVOs, pfParamVO);
		unOaCommit(billVOs);
		return billVOs;
	}

	private void unOaCommit(ContractBillVO[] aggVO) throws BusinessException {
		for (ContractBillVO temp : aggVO) {
			ContractHeadVO hVO = temp.getParentVO();
			if (hVO.getBill_type().contains(billType)) {
				OaWorkFlowUtil.backOaWorkFlow(temp.getPrimaryKey());
			}
		}
	}

	public ContractBillVO[] unForzenContr(ContractBillVO[] billVOs,
			ContractBillVO[] originBillVOs) throws BusinessException {
		OtherButtonUpdateAction<ContractBillVO> action = createUpdateWithoutEventAction(
				"1227", "1228");
		initUnFrozenAction(action);
		return (ContractBillVO[]) action.processAction(billVOs, originBillVOs);
	}

	public ContractBillVO[] unValidateContr(ContractBillVO[] billVOs,
			ContractBillVO[] originBillVOs) throws BusinessException {
		OtherButtonUpdateAction<ContractBillVO> action = createUpdateWithoutEventAction(
				"1223", "1224");
		initUnValidateAction(action);
		return (ContractBillVO[]) action.processAction(billVOs, originBillVOs);
	}

	public ContractBillVO[] updateContr(ContractBillVO[] billVOs,
			ContractBillVO[] originBillVOs) throws BusinessException {
		return (ContractBillVO[]) update(billVOs, originBillVOs);
	}

	public void updateMarContr(ContractHeadVO[] headVOs, String[] cols) {
		VOUpdate<ContractHeadVO> update = new VOUpdate<ContractHeadVO>();
		update.update(headVOs, cols);
	}

	public ContractBillVO[] validateContr(ContractBillVO[] billVOs,
			ContractBillVO[] originBillVOs) throws BusinessException {
		OtherButtonUpdateAction<ContractBillVO> action = createUpdateWithoutEventAction(
				"1221", "1222");
		initValidateAction(action);
		return (ContractBillVO[]) action.processAction(billVOs, originBillVOs);
	}

	protected void initApproveAction(ApproveAction<ContractBillVO> action) {
		super.initApproveAction(action);
	}

	protected void initInsertAction(InsertAction<ContractBillVO> action) {
		super.initInsertAction(action);
		action.addBeforeRule(new InitCurrTypeBeforeRule(
				new FieldsForInitCurrType()));
		action.addBeforeRule(new AppendBusiTypeBeforeRule());
		action.addBeforeRule(new BeginFlagCheckRule());
	}

	protected void initDeleteAction(DeleteAction<ContractBillVO> action) {
		super.initDeleteAction(action);
		action.addAfterRule(new DeleteHisVersionRule());
	}

	protected void initUpdateAction(UpdateAction<ContractBillVO> action) {
		super.initUpdateAction(action);
		action.addBeforeRule(new InitCurrTypeBeforeRule(
				new FieldsForInitCurrType()));
		action.addBeforeRule(new BeginFlagCheckRule());
	}

	protected void initCommitAction(CommitAction<ContractBillVO> action) {
		action.addBeforeRule(new WBSPretaskCompleteCheckRule());
	}

	class FieldsForInitCurrType implements IFieldsForInitCurrType {
		public String getPk_currtype_orig() {
			return "pk_currtype";
		}

		public String getExchangeRate() {
			return "nexchangerate";
		}

		public String getPk_currtype() {
			return "pk_org_currtype";
		}

		public String[] getHeadOrigFields() {
			return new String[] { "mny_orig", "shou_mny_orig",
					"actual_mny_orig", "verify_mny_orig" };
		}

		public String[] getHeadFields() {
			return new String[] { "mny_curr", "shou_mny_curr",
					"actual_mny_curr", "verify_mny_curr" };
		}

		public String[] getHeadGroupFields() {
			return new String[] { "mny_group", "shou_mny_group",
					"actual_mny_group", "verify_mny_group" };
		}

		public String[] getHeadGlobalFields() {
			return new String[] { "mny_global", "shou_mny_global",
					"actual_mny_global", "verify_mny_global" };
		}

		public String[] getBodyOrigFields() {
			return new String[] { "mny_orig", "shou_mny_orig", "fact_mny_orig",
					"verify_mny_orig" };
		}

		public String[] getBodyFields() {
			return new String[] { "mny_curr", "shou_mny_curr", "fact_mny_curr",
					"verify_mny_curr" };
		}

		public String[] getBodyGroupFields() {
			return new String[] { "mny_group", "shou_mny_group",
					"fact_mny_group", "verify_mny_group" };
		}

		public String[] getBodyGlobalFields() {
			return new String[] { "mny_global", "shou_mny_global",
					"fact_mny_global", "verify_mny_global" };
		}

		public Class<? extends ISuperVO> getBodyVOClass() {
			return nc.vo.pcm.marketcontract.ContrWorksVO.class;
		}
	}

	public String getDef2(String pk_org) throws BusinessException {
		String def2 = "";
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		if (null != orgVO) {
			def2 = orgVO.getDef2();
		}
		return def2;
	}
}
