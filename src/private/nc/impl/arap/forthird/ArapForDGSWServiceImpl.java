package nc.impl.arap.forthird;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.pf.pub.PfDataCache;
import nc.bs.trade.business.HYPubBO;
import nc.itf.arap.forthird.IArapForDGSWService;
import nc.itf.arap.pub.IArapBillService;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.ui.pcm.utils.GetDao;
import nc.vo.arap.basebill.BaseAggVO;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.arap.basebill.BaseItemVO;
import nc.vo.arap.pay.PayBillVO;
import nc.vo.bd.cust.CustomerVO;
import nc.vo.bd.supplier.SupplierVO;
import nc.vo.cmp.apply.ApplyBVO;
import nc.vo.cmp.apply.ApplyVO;
import nc.vo.cmp.bill.BillVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKHeaderVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.org.OrgVO;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.sm.UserVO;
import nc.ws.intf.BillLogVO;
import nc.ws.intf.HttpClient;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import uap.iweb.log.Logger;

// 所属包为ermweb
@SuppressWarnings("restriction")
public class ArapForDGSWServiceImpl implements IArapForDGSWService {
	private String STATEURL = null;// 单据状态接收接口地址
	private HYPubBO hyPubBO;
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);

	public HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	public void setHyPubBO(HYPubBO hyPubBO) {
		this.hyPubBO = hyPubBO;
	}

	BillLogVO vo = new BillLogVO();

	// 拼装URL
	public JSONObject genSWUrl(String billType, String pk_primarykey)
			throws BusinessException, UnsupportedEncodingException {
		// 返回参数
		JSONObject json = new JSONObject();
		// 拼装参数
		JSONObject jsonObject = new JSONObject();
		// 接口URL
		String dgurl = (String) getHyPubBO()
				.findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and code = 'DGURL' and pk_defdoclist in "
								+ "(select pk_defdoclist from bd_defdoclist where code = 'THIRDRELA' and nvl(dr,0) = 0)");
		if (StringUtils.isEmpty(dgurl)) {
			json.put("success", "N");
			json.put("errinfo", "请先配置接口地址档案（编码[THIRDRELA]，档案编码[DGURL]）！");
			return json;
		}
		// 接口appkey
		String dgAppkey = (String) getHyPubBO()
				.findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and code = 'DGAPPKEY' and pk_defdoclist in "
								+ "(select pk_defdoclist from bd_defdoclist where code = 'THIRDRELA' and nvl(dr,0) = 0)");
		if (StringUtils.isEmpty(dgAppkey)) {
			json.put("success", "N");
			json.put("errinfo", "请先配置接口APPKEY档案（编码[THIRDRELA]，档案编码[DGAPPKEY]）！");
			return json;
		}
		jsonObject.put("appkey", dgAppkey);// Appkey
		// 应收、应付、收款、付款、
		if ("F0".equals(billType) || "F1".equals(billType)
				|| "F2".equals(billType) || "F3".equals(billType)) {
			IArapBillService cs = (IArapBillService) NCLocator.getInstance()
					.lookup(IArapBillService.class);
			BaseAggVO[] arapvos = cs.queryArapBillByPKs(
					new String[] { pk_primarykey }, billType);
			if (arapvos == null || arapvos.length <= 0) {
				json.put("success", "N");
				json.put("errinfo", "根据主键[" + pk_primarykey + "]，单据类型["
						+ billType + "]查询单据失败！");
				return json;
			}
			BaseBillVO hvo = arapvos[0].getHeadVO();
			BaseItemVO[] mxvos = (BaseItemVO[]) arapvos[0].getChildrenVO();
			if (mxvos == null || mxvos.length <= 0) {
				json.put("success", "N");
				json.put("errinfo", "查询明细错误！");
				return json;
			}
			if (hvo.getApprovestatus() == -1) {
				String billmaker = hvo.getBillmaker();
				String pk_org = hvo.getPk_org();
				UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
						UserVO.class, billmaker);
				OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(
						OrgVO.class, pk_org);
				jsonObject.put("statementNo", pk_primarykey);// 单据主键
				jsonObject.put("billNumber", hvo.getBillno());// 单据编号
				jsonObject.put("tradeType", hvo.getPk_tradetype());// 交易类型
				BilltypeVO billtypeVO = PfDataCache.getBillType(hvo
						.getPk_tradetype());
				if (billtypeVO == null) {
					json.put("success", "N");
					json.put("errinfo", "交易类型查询出错:" + hvo.getPk_tradetype()
							+ "-"
							+ InvocationInfoProxy.getInstance().getGroupId());
					return json;
				}
				jsonObject.put("tradeTypeName", billtypeVO.getBilltypename());// 交易类型名称
				jsonObject.put("billType", hvo.getPk_billtype());// 单据类型
				jsonObject.put("billDate", hvo.getBilldate().toString());// 制单日期
				jsonObject.put("userCode", userVO.getUser_code());// 制单人编码
				jsonObject.put("billApplicant", userVO.getUser_name());// 制单人名称
				jsonObject.put("userId", billmaker);// 制单人主键
				jsonObject.put("companyName", orgVO.getName());// 所属公司名称
				jsonObject.put("companyCode", orgVO.getCode());// 所属公司编码
				jsonObject.put("companyID", orgVO.getPk_org());// 所属公司主键
				jsonObject.put("billAmount", hvo.getLocal_money());// 本次付款金额--含税金额
				BaseItemVO mxvo = mxvos[0];
				if (mxvo.getSupplier() != null) {
					SupplierVO gysVO = (SupplierVO) getHyPubBO()
							.queryByPrimaryKey(SupplierVO.class,
									mxvo.getSupplier());
					jsonObject.put("customeCode", gysVO.getCode());// 供应商编码
					jsonObject.put("customeName", gysVO.getName());// 供应商名称
				}
				if (mxvo.getCustomer() != null) {
					CustomerVO khVO = (CustomerVO) getHyPubBO()
							.queryByPrimaryKey(CustomerVO.class,
									mxvo.getCustomer());
					jsonObject.put("customeCode", khVO.getCode());// 客户编码
					jsonObject.put("customeName", khVO.getName());// 客户名称
				}
				// 获取合同信息
				String contract_no = mxvo.getContractno(); // 合同编码
				if (contract_no != null) {
					String contract_name = ""; // 合同名称
					String htQuerySql = "select * from v_allht where htbm = '"
							+ contract_no + "' and pk_org = '"
							+ hvo.getPk_org() + "'";
					List<Object[]> htLs = getDao.query(htQuerySql);
					if (htLs != null && htLs.size() > 0
							&& htLs.get(0)[0] != null) {
						contract_name = (String) htLs.get(0)[0];
					}
					jsonObject.put("contractNo", contract_no);// 合同编码
					jsonObject.put("contractName", contract_name);// 合同名称
				} else {
					jsonObject.put("contractNo", "");// 合同编码
					jsonObject.put("contractName", "");// 合同名称
				}

				// 获取项目信息
				String pk_project = mxvo.getProject();// 项目主键
				if (pk_project != null) {
					ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
							.queryByPrimaryKey(ProjectHeadVO.class, pk_project);
					jsonObject.put("projectCode", projectVO.getProject_code());// 项目编码
					jsonObject.put("projectName", projectVO.getProject_name());// 项目名称
				} else {
					jsonObject.put("projectCode", "");// 项目编码
					jsonObject.put("projectName", "");// 项目名称
				}

				String ddurl = Base64.encodeBase64String(jsonObject.toString()
						.getBytes("utf-8"));
				Logger.error("jsonObject==" + jsonObject.toString());
				json.put("url", dgurl);
				json.put("deurl", ddurl);
			} else {
				json.put("success", "N");
				json.put("errinfo", "单据状态不是自由态，不能进行影像上传！");
				return json;
			}
		} else if ("261X".equals(billType)) {
			// 申请
			AggMatterAppVO aggvo = ((IErmMatterAppBillQuery) NCLocator
					.getInstance().lookup(IErmMatterAppBillQuery.class))
					.queryBillByPK(pk_primarykey);
			MatterAppVO hvo = aggvo.getParentVO();
			if (hvo.getApprstatus() == -1) {
				String creator = hvo.getCreator();
				String pk_org = hvo.getPk_org();
				UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
						UserVO.class, creator);
				OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(
						OrgVO.class, pk_org);
				jsonObject.put("statementNo", pk_primarykey);// 单据主键
				jsonObject.put("billNumber", hvo.getBillno());// 单据编号
				jsonObject.put("tradeType", hvo.getPk_tradetype());// 交易类型
				BilltypeVO billtypeVO = PfDataCache.getBillType(hvo
						.getPk_tradetype());
				if (billtypeVO == null) {
					json.put("success", "N");
					json.put("errinfo", "交易类型查询出错:" + hvo.getPk_tradetype()
							+ "-"
							+ InvocationInfoProxy.getInstance().getGroupId());
					return json;
				}
				jsonObject.put("tradeTypeName", billtypeVO.getBilltypename());// 交易类型名称
				jsonObject.put("billType", hvo.getPk_billtype());// 单据类型
				jsonObject.put("billDate", hvo.getBilldate().toString());// 制单日期
				jsonObject.put("userCode", userVO.getUser_code());// 制单人编码
				jsonObject.put("billApplicant", userVO.getUser_name());// 制单人名称
				jsonObject.put("userId", creator);// 制单人主键
				jsonObject.put("companyName", orgVO.getName());// 所属公司名称
				jsonObject.put("companyCode", orgVO.getCode());// 所属公司编码
				jsonObject.put("companyID", orgVO.getPk_org());// 所属公司主键
				jsonObject.put("billAmount", hvo.getOrg_amount());// 组织本币金额--含税金额
				jsonObject.put("contractNo", "");// 合同编码
				jsonObject.put("contractName", "");// 合同名称
				jsonObject.put("projectCode", "");// 项目编码
				jsonObject.put("projectName", "");// 项目名称
				jsonObject.put("customeCode", "");// 客户编码
				jsonObject.put("customeName", "");// 客户名称
				String ddurl = Base64.encodeBase64String(jsonObject.toString()
						.getBytes("utf-8"));
				Logger.error("jsonObject==" + jsonObject.toString());
				json.put("url", dgurl);
				json.put("deurl", ddurl);
			} else {
				json.put("success", "N");
				json.put("errinfo", "单据状态不是自由态，不能进行影像上传！");
				return json;
			}
		} else if ("263X".equals(billType) || "264X".equals(billType)) {
			// 借款、报销
			// JKVO aggvo = new JKVO();
			JKBXHeaderVO hvo = null;
			if ("263X".equals(billType)) {
				JKHeaderVO jkhvo = (JKHeaderVO) getHyPubBO().queryByPrimaryKey(
						JKHeaderVO.class, pk_primarykey);
				hvo = jkhvo;
			} else if ("264X".equals(billType)) {
				BXHeaderVO bxhvo = (BXHeaderVO) getHyPubBO().queryByPrimaryKey(
						BXHeaderVO.class, pk_primarykey);
				hvo = bxhvo;
			}
			if (hvo.getSpzt() == -1) {
				String billmaker = hvo.getOperator();
				String pk_org = hvo.getPk_org();
				UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
						UserVO.class, billmaker);
				OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(
						OrgVO.class, pk_org);
				jsonObject.put("statementNo", pk_primarykey);// 单据主键
				jsonObject.put("billNumber", hvo.getDjbh());// 单据编号
				jsonObject.put("tradeType", hvo.getDjlxbm());// 交易类型
				BilltypeVO billtypeVO = PfDataCache
						.getBillType(hvo.getDjlxbm());
				if (billtypeVO == null) {
					json.put("success", "N");
					json.put("errinfo", "交易类型查询出错:" + hvo.getDjlxbm() + "-"
							+ InvocationInfoProxy.getInstance().getGroupId());
					return json;
				}
				jsonObject.put("tradeTypeName", billtypeVO.getBilltypename());// 交易类型名称
				jsonObject.put("billType", hvo.getPk_billtype());// 单据类型
				jsonObject.put("billDate", hvo.getDjrq().toString());// 制单日期
				jsonObject.put("userCode", userVO.getUser_code());// 制单人编码
				jsonObject.put("billApplicant", userVO.getUser_name());// 制单人名称
				jsonObject.put("userId", billmaker);// 制单人主键
				jsonObject.put("companyName", orgVO.getName());// 所属公司名称
				jsonObject.put("companyCode", orgVO.getCode());// 所属公司编码
				jsonObject.put("companyID", orgVO.getPk_org());// 所属公司主键
				jsonObject.put("billAmount", hvo.getTotal());// 组织本币金额--含税金额
				jsonObject.put("contractNo", "");// 合同编码
				jsonObject.put("contractName", "");// 合同名称

				BXBusItemVO[] mxvos = (BXBusItemVO[]) getHyPubBO()
						.queryByCondition(
								BXBusItemVO.class,
								"nvl(dr,0) = 0 and pk_jkbx='" + pk_primarykey
										+ "'");
				if (mxvos == null || mxvos.length <= 0) {
					json.put("success", "N");
					json.put("errinfo", "查询明细错误！");
					return json;
				}
				BXBusItemVO mxvo = mxvos[0];
				String pk_project = mxvo.getJobid();// 项目主键
				if (pk_project != null) {
					ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
							.queryByPrimaryKey(ProjectHeadVO.class, pk_project);
					jsonObject.put("projectCode", projectVO.getProject_code());// 项目编码
					jsonObject.put("projectName", projectVO.getProject_name());// 项目名称
				} else {
					jsonObject.put("projectCode", "");// 项目编码
					jsonObject.put("projectName", "");// 项目名称
				}
				jsonObject.put("customeCode", "");// 客户编码
				jsonObject.put("customeName", "");// 客户名称
				String ddurl = Base64.encodeBase64String(jsonObject.toString()
						.getBytes("utf-8"));
				Logger.error("jsonObject==" + jsonObject.toString());
				json.put("url", dgurl);
				json.put("deurl", ddurl);
			} else {
				json.put("success", "N");
				json.put("errinfo", "单据状态不是自由态，不能进行影像上传！");
				return json;
			}
		} else if ("36D1".equals(billType)) {
			ApplyVO hvo = (ApplyVO) getHyPubBO().queryByPrimaryKey(
					ApplyVO.class, pk_primarykey);
			if (hvo.getVbillstatus() == -1) {
				String billmaker = hvo.getBillmaker();
				String pk_org = hvo.getPk_org();
				UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
						UserVO.class, billmaker);
				OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(
						OrgVO.class, pk_org);
				jsonObject.put("statementNo", pk_primarykey);// 单据主键
				jsonObject.put("billNumber", hvo.getVbillno());// 单据编号
				jsonObject.put("tradeType", hvo.getPk_billtypecode());// 交易类型
				BilltypeVO billtypeVO = PfDataCache.getBillType(hvo
						.getPk_billtypecode());
				if (billtypeVO == null) {
					json.put("success", "N");
					json.put("errinfo", "交易类型查询出错:" + hvo.getPk_billtypecode()
							+ "-"
							+ InvocationInfoProxy.getInstance().getGroupId());
					return json;
				}
				jsonObject.put("tradeTypeName", billtypeVO.getBilltypename());// 交易类型名称
				jsonObject.put("billType", "36D1");// 单据类型
				jsonObject.put("billDate", hvo.getBillmakedate().toString());// 制单日期
				jsonObject.put("userCode", userVO.getUser_code());// 制单人编码
				jsonObject.put("billApplicant", userVO.getUser_name());// 制单人名称
				jsonObject.put("userId", billmaker);// 制单人主键
				jsonObject.put("companyName", orgVO.getName());// 所属公司名称
				jsonObject.put("companyCode", orgVO.getCode());// 所属公司编码
				jsonObject.put("companyID", orgVO.getPk_org());// 所属公司主键
				jsonObject.put("billAmount", hvo.getApplysum());// 组织本币金额--含税金额
				jsonObject.put("contractNo", "");// 合同编码
				jsonObject.put("contractName", "");// 合同名称

				ApplyBVO[] mxvos = (ApplyBVO[]) getHyPubBO().queryByCondition(
						ApplyBVO.class,
						"nvl(dr,0) = 0 and pk_apply='" + pk_primarykey + "'");
				if (mxvos == null || mxvos.length <= 0) {
					json.put("success", "N");
					json.put("errinfo", "查询明细错误！");
					return json;
				}
				ApplyBVO mxvo = mxvos[0];
				String pk_project = mxvo.getPk_project();// 项目主键
				if (pk_project != null) {
					ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
							.queryByPrimaryKey(ProjectHeadVO.class, pk_project);
					jsonObject.put("projectCode", projectVO.getProject_code());// 项目编码
					jsonObject.put("projectName", projectVO.getProject_name());// 项目名称
				} else {
					jsonObject.put("projectCode", "");// 项目编码
					jsonObject.put("projectName", "");// 项目名称
				}
				jsonObject.put("customeCode", "");// 客户编码
				jsonObject.put("customeName", "");// 客户名称
				String ddurl = Base64.encodeBase64String(jsonObject.toString()
						.getBytes("utf-8"));
				Logger.error("jsonObject==" + jsonObject.toString());
				json.put("url", dgurl);
				json.put("deurl", ddurl);
			} else {
				json.put("success", "N");
				json.put("errinfo", "单据状态不是自由态，不能进行影像上传！");
				return json;
			}
		} else if ("F5".equals(billType)) {
			BillVO hvo = (BillVO) getHyPubBO().queryByPrimaryKey(
					nc.vo.cmp.bill.BillVO.class, pk_primarykey);
			if (hvo.getBill_status() == -1 || hvo.getBill_status() == -10
					|| hvo.getBill_status() == -99) {
				String billmaker = hvo.getBillmaker();
				String pk_org = hvo.getPk_org();
				UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
						UserVO.class, billmaker);
				OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(
						OrgVO.class, pk_org);
				jsonObject.put("statementNo", pk_primarykey);// 单据主键
				jsonObject.put("billNumber", hvo.getBill_no());// 单据编号
				jsonObject.put("tradeType", hvo.getTrade_type());// 交易类型
				BilltypeVO billtypeVO = PfDataCache.getBillType(hvo
						.getTrade_type());
				if (billtypeVO == null) {
					json.put("success", "N");
					json.put("errinfo", "交易类型查询出错:" + hvo.getTrade_type() + "-"
							+ InvocationInfoProxy.getInstance().getGroupId());
					return json;
				}
				jsonObject.put("tradeTypeName", billtypeVO.getBilltypename());// 交易类型名称
				jsonObject.put("billType", "36D1");// 单据类型
				jsonObject.put("billDate", hvo.getBill_date().toString());// 制单日期
				jsonObject.put("userCode", userVO.getUser_code());// 制单人编码
				jsonObject.put("billApplicant", userVO.getUser_name());// 制单人名称
				jsonObject.put("userId", billmaker);// 制单人主键
				jsonObject.put("companyName", orgVO.getName());// 所属公司名称
				jsonObject.put("companyCode", orgVO.getCode());// 所属公司编码
				jsonObject.put("companyID", orgVO.getPk_org());// 所属公司主键
				jsonObject.put("billAmount", hvo.getLocal_money());// 组织本币金额--含税金额
				jsonObject.put("contractNo", "");// 合同编码
				jsonObject.put("contractName", "");// 合同名称
				jsonObject.put("projectCode", "");// 项目编码
				jsonObject.put("projectName", "");// 项目名称
				jsonObject.put("customeCode", "");// 客户编码
				jsonObject.put("customeName", "");// 客户名称
				String ddurl = Base64.encodeBase64String(jsonObject.toString()
						.getBytes("utf-8"));
				Logger.error("jsonObject==" + jsonObject.toString());
				json.put("url", dgurl);
				json.put("deurl", ddurl);
			} else {
				json.put("success", "N");
				json.put("errinfo", "单据状态不是暂存或自由态，不能进行影像上传！");
				return json;
			}
		} else {
			json.put("success", "N");
			json.put("errinfo", "未找到单据类型[" + billType + "]！");
			return json;
		}
		json.put("success", "Y");
		return json;
	}

	/**
	 * 提交，驳回，删除调用东港税务系统
	 * 
	 * @param billno
	 *            -- 单据号
	 * @param pk_org
	 *            -- 组织主键
	 * @param eventType
	 *            -- 事件类型
	 * @param billType
	 *            -- 单据类型
	 * @return
	 * @throws BusinessException
	 */
	public JSONObject sendBillByNCBill(String billno, String pk_org,
			String eventType, String billType) throws BusinessException {
		// 返回参数
		JSONObject json = new JSONObject();
		STATEURL = getStatusUrl();
		if (StringUtils.isEmpty(STATEURL)) {
			json.put("success", "N");
			json.put("errinfo", "请先配置接口地址档案（编码[THIRDRELA]，档案编码[DGZTURL]）！");
			return json;
		}
		// 申请
		json = billEvent(billno, pk_org, eventType, billType);
		return json;
	}

	// 单据状态调用接口
	private JSONObject billEvent(String billno, String pk_org, String status,
			String bill_type) throws BusinessException {
		// TODO Auto-generated method stub
		// 返回接口结果
		JSONObject resultJson = new JSONObject();
		// 发送参数
		JSONObject sendData = new JSONObject();
		sendData.put("billNumber", billno);// 单据主键
		sendData.put("status", status);
		Map<String, String> heads = new HashMap<String, String>();
		heads.put("appKey", "7VE3lgxCQyH5MLEGhZM7");
		heads.put("serviceId", "S10003");
		String res = "";// 返回参数
		// 接口返回
		try {
			// 调用接口返回
			res = HttpClient.httpPostRaw(STATEURL, sendData.toString(), heads,
					"utf-8");
			JSONObject retData = JSONObject.fromObject(res);
			if (retData.getBoolean("success")) {
				resultJson.put("success", "Y");
				return resultJson;
			}
			SaveBillLog(sendData.toString(), res, pk_org, bill_type, "税务状态");
			if (res == null) {
				resultJson.put("success", "Y");
				resultJson.put("errinfo", "接口返回空数据，请检查！");
				return resultJson;
			}
			resultJson.put("success", "Y");
			resultJson.put("errinfo",
					"税务系统单据状态接口返回信息：" + retData.getString("msg"));
			return resultJson;
		} catch (Exception e) {
			// TODO: handle exception
			resultJson.put("success", "Y");
			resultJson.put("errinfo", "调用税务系统失败！" + e.getMessage());
			SaveBillLog(sendData.toString(), res, pk_org, bill_type, "税务状态");
			return resultJson;
		}
	}

	// 获取单据状态接收接口URL
	private String getStatusUrl() throws BusinessException {
		String dgzturl = (String) getHyPubBO()
				.findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and code = 'DGZTURL' and pk_defdoclist in "
								+ "(select pk_defdoclist from bd_defdoclist where code = 'THIRDRELA' and nvl(dr,0) = 0)");
		return dgzturl;
	}

	// 日志记录
	private String SaveBillLog(String recdata, String rdtdata, String pk_org,
			String bill_type, String def1) throws BusinessException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		vo.setCreationtime(df.format(new Date()));// 创建时间
		vo.setDef1(def1);
		vo.setTransi_type(bill_type); // 单据类型
		vo.setRecdata(recdata);// 接收/发送参数
		vo.setRdtdata(rdtdata);// 返回参数
		vo.setPk_org(pk_org);
		vo.setPk_group("0001A1100000000001QS");
		try {
			new HYPubBO().insert(vo);
		} catch (BusinessException e) {
			// TODO: handle exception
			return "接收税务系统单据状态日志保存失败：" + e.getMessage();
		}
		return "";
	}

	// 薪酬支付审批单（宏景），则调用宏景接口
	@Override
	public JSONObject sendHJBillByNCBill(PayBillVO hvo, String state,
			String reason) throws BusinessException {
		// 返回参数
		JSONObject json = new JSONObject();
		// 发送参数
		JSONArray sendarr = new JSONArray();
		JSONObject sendData = new JSONObject();
		// 接口URL
		String hjurl = (String) getHyPubBO()
				.findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and code = 'HJURL' and pk_defdoclist in "
								+ "(select pk_defdoclist from bd_defdoclist where code = 'THIRDRELA' and nvl(dr,0) = 0)");
		if (StringUtils.isEmpty(hjurl)) {
			json.put("success", "N");
			json.put("errinfo", "请先配置接口地址档案（编码[THIRDRELA]，档案编码[HJURL]）！");
			return json;
		}
		if (hvo.getDef65() == null) {
			json.put("success", "N");
			json.put("errinfo", "自定义项65对应宏景单据号为空！");
			return json;
		}
		sendData.put("bill_id", hvo.getDef65());// 宏景单据号
		sendData.put("state", state);// 驳回：02
		sendData.put("reason", reason);// 原因
		sendarr.add(sendData);
		String res = "";// 返回参数
		// 接口返回
		try {
			// 调用接口返回
			res = HttpClient.httpPostRaw(hjurl, sendarr.toString(), null,
					"utf-8");
			SaveBillLog(sendarr.toString(), res, hvo.getPk_org(), "F3", "宏景状态");
			if (res == null) {
				json.put("success", "N");
				json.put("errinfo", "HR系统接口返回空数据，请检查！");
				return json;
			}
			JSONObject retData = JSONObject.fromObject(res);
			if ("0000001".equals(retData.getString("responseCode"))) {
				json.put("success", "Y");
				return json;
			}
			json.put("success", "N");
			json.put("errinfo", "HR系统单据状态接口返回信息：" + retData.getString("reason"));
			return json;
		} catch (Exception e) {
			// TODO: handle exception
			json.put("success", "N");
			json.put("errinfo", "调用HR系统失败！" + e.getMessage());
			SaveBillLog(sendarr.toString(), res, hvo.getPk_org(), "F3", "宏景状态");
			return json;
		}
	}

	@Override
	public JSONObject sendSSBillByNCBill(String pk_primarykey, int state)
			throws BusinessException {
		// 返回参数
		JSONObject json = new JSONObject();
		// 发送参数
		JSONObject sendData = new JSONObject();
		// 接口URL
		String ssurl = (String) getHyPubBO()
				.findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and code = 'SSURL' and pk_defdoclist in "
								+ "(select pk_defdoclist from bd_defdoclist where code = 'THIRDRELA' and nvl(dr,0) = 0)");
		if (StringUtils.isEmpty(ssurl)) {
			json.put("success", "N");
			json.put("errinfo", "请先配置接口地址档案（编码[THIRDRELA]，档案编码[SSURL]）！");
			return json;
		}
		// 1233333
		sendData.put("billNum", pk_primarykey);// 单据主键
		sendData.put("status", state);// 驳回：02
		String res = "";// 返回参数
		Map<String, String> heads = new HashMap<String, String>();
		heads.put("sign", "timestamp=nywlfw,sign=MmViYTYzMTI4MGZhZjRjZTcxMjRkOGYwNWQ0ZTNlMTRkNzg3MzUxMmI0OWEwN2I2NTM5NGU4YWZiMTk2MWY5NA%3D%3D");
		// 接口返回
		try {
			// 调用接口返回
			res = HttpClient.httpPostRaw(ssurl, sendData.toString(), heads,
					"utf-8");
			SaveBillLog(sendData.toString(), res, "", "WZ", "神思状态");
			if (res == null) {
				json.put("success", "N");
				json.put("errinfo", "神思接口返回空数据，请检查！");
				return json;
			}
			JSONObject retData = JSONObject.fromObject(res);
			if (retData.getInt("code") == 200) {
				json.put("success", "Y");
				return json;
			}
			json.put("success", "N");
			json.put("errinfo", "神思系统单据状态接口返回信息：" + retData.getString("msg"));
			return json;
		} catch (Exception e) {
			// TODO: handle exception
			json.put("success", "Y");
			json.put("errinfo", "调用神思系统失败！" + e.getMessage());
			SaveBillLog(sendData.toString(), res, "", "WZ", "神思状态");
			return json;
		}
	}
}
