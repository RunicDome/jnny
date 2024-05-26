package nc.bs.pub.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
import nc.data.param.CommonParam;
import nc.itf.arap.forthird.IArapForDGSWService;
import nc.vo.arap.gathering.GatheringBillItemVO;
import nc.vo.arap.gathering.GatheringBillVO;
import nc.vo.org.FinanceOrgVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

// 收款单
public class N_F2_START extends N_F2_SAVE {

	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表名
	public static String WORKFLOWID_JNRL_QD = "536";
	public static String BODY_TABLE_NAME_JNNY_SG = "formtable_main_566";

	public N_F2_START() {
	}

	public Object runComClass(PfParameterVO pfparametervo)
			throws BusinessException {
		AggregatedValueObject[] obj = (AggregatedValueObject[]) super
				.runComClass(pfparametervo);
		for (AggregatedValueObject temp : obj) {
			GatheringBillVO hvo = (GatheringBillVO) temp.getParentVO();
			GatheringBillItemVO[] bvos = (GatheringBillItemVO[]) temp
					.getChildrenVO();
			String bill_type = hvo.getPk_billtype();// 单据类型
			String transi_type = hvo.getPk_tradetype();// 交易类型
			// 中台
			if ("4".equals(getDef2(hvo.getPk_org()))
					&& StringUtils.isEmpty(hvo.getDef45())) {
				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hvo.getPk_org(),
						transi_type);
				if (oaVo != null && oaVo.getIsdr() == 0) {// 判断流程是否开启
					WorkFId = oaVo.getFlowid();
					TableName = oaVo.getTablename();
					// 获取主表数据
					JSONArray headData = getZTMainMap(hvo,
							bvos[0].getRecaccount(), bvos[0].getPk_deptid_v(),
							bvos[0].getPk_psndoc(), bvos[0].getPayaccount(),
							bvos[0].getCashitem());
					// 获取子表数据
					JSONArray bodyData = getZTDtaileDataMap(temp);
					// 构造workflow信息
					OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
					workFlowVO.setPrimaryKey(hvo.getPrimaryKey());
					workFlowVO.setPkGroup(hvo.getPk_group());
					workFlowVO.setPkOrg(hvo.getPk_org());
					workFlowVO.setBillMaker(hvo.getBillmaker());
					workFlowVO.setCreator(hvo.getCreator());
					workFlowVO.setBillCode(bill_type);// 单据类型
					workFlowVO.setWorkflowId(OaWorkFlowUtil
							.getOAFlowID(WorkFId));
					workFlowVO.setWorkflowName(oaVo.getBilltypename());// 名称
					WorkFlowBill bill = OaWorkFlowUtil
							.getWorkFlowBill(workFlowVO);
					bill.setDef3("ZT");
					bill.setDef4(hvo.getBillno());// 单据编号
					// 制单人身份证号
					UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
							UserVO.class, hvo.getBillmaker());
					String idCard = (String) getHyPubBO().findColValue(
							"bd_psndoc",
							"id",
							"nvl(dr,0) = 0 and pk_psndoc='"
									+ userVO.getPk_psndoc() + "'");
					bill.setDef5(idCard);
					// 调用OA工具类同步数据至OA
					OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
				}
			} else if ("1".equals(getDef2(hvo.getPk_org()))) {
				if ("F2-Cxx-005".equals(((GatheringBillVO) temp.getParentVO())
						.getPk_tradetype())
						|| "D2".equals(((GatheringBillVO) temp.getParentVO())
								.getPk_tradetype())) {
				}
			}
			// 调用东港税务系统同步单据状态
			IArapForDGSWService util = (IArapForDGSWService) NCLocator
					.getInstance().lookup(IArapForDGSWService.class);
			// 单据号，组织主键，事件类型，单据类型
			JSONObject res = util.sendBillByNCBill(hvo.getPrimaryKey(),
					hvo.getPk_org(), CommonParam.COMMIT, hvo.getPk_billtype());
			if (!"Y".equals(res.getString("success"))) {
				throw new BusinessException(res.getString("errinfo"));
			}
		}
		return super.runComClass(pfparametervo);
	}

	private JSONArray getZTDtaileDataMap(AggregatedValueObject temp)
			throws BusinessException {
		// 获取详细信息
		GatheringBillItemVO[] bvos = (GatheringBillItemVO[]) temp
				.getChildrenVO();
		Map<String, Object> bodyMap = getBody(bvos);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
		return dtlistString;
	}

	private Map<String, Object> getBody(GatheringBillItemVO[] bvo)
			throws BusinessException {
		Map<String, Object> dtMap = new HashMap<String, Object>();
		dtMap.put("tableDBName", TableName + "_dt1");
		List<Map<String, Object>> workflowRequestTableRecords = new ArrayList<Map<String, Object>>();
		for (GatheringBillItemVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			// 资金类别
			if (temp.getDef20() != null) {
				String zjlb = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef20()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"zjlb", zjlb));
			}
			// 收款业务类型
			if (temp.getDef19() != null) {
				String skywlx = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef19()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"skywlx", skywlx));
			}
			// 组织本币金额
			if (temp.getLocal_money_cr() != null) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"zzbbje",
						temp.getLocal_money_cr().setScale(2,
								UFDouble.ROUND_HALF_UP)
								+ ""));
			}

			// 客户
			if (temp.getCustomer() != null) {
				String kh = (String) getHyPubBO().findColValue(
						"bd_customer",
						"name",
						"nvl(dr,0) = 0 and pk_customer = '"
								+ temp.getCustomer() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("kh",
						kh));
			}
			// 票据类型
			if (temp.getChecktype() != null) {
				String pjlx = (String) getHyPubBO().findColValue(
						"bd_notetype",
						"name",
						"nvl(dr,0) = 0 and pk_notetype = '"
								+ temp.getChecktype() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"pjlx", pjlx));
			}
			// 现金流量项目
			if (temp.getCashitem() != null) {
				String xjllxm = (String) getHyPubBO().findColValue(
						"bd_cashflow",
						"name",
						"nvl(dr,0) = 0 and pk_cashflow  = '"
								+ temp.getCashitem() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"xjllxm", xjllxm));
			}
			// 预算类别
			if (temp.getPk_subjcode() != null) {
				String yslbname = (String) getHyPubBO().findColValue(
						"bd_inoutbusiclass",
						"name",
						"nvl(dr,0) = 0 and pk_inoutbusiclass = '"
								+ temp.getPk_subjcode() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"yslb", yslbname));
			}
			// 部门
			if (temp.getPk_deptid_v() != null) {
				String bm = (String) getHyPubBO().findColValue(
						"org_dept_v",
						"name",
						"nvl(dr,0) = 0 and pk_vid = '" + temp.getPk_deptid_v()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bm",
						bm));
			}

			// 业务员
			if (temp.getPk_psndoc() != null) {
				String ywy = (String) getHyPubBO().findColValue(
						"bd_psndoc",
						"name",
						"nvl(dr,0) = 0 and pk_psndoc = '" + temp.getPk_psndoc()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("ywy",
						ywy));
			}
			if (temp.getProject() != null) {
				/* 项目编码 */
				String code = (String) getHyPubBO().findColValue(
						"bd_project",
						"project_code",
						"nvl(dr,0) = 0 and pk_project = '" + temp.getProject()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"project_code", code));

				/* 项目名称 */
				String name = (String) getHyPubBO().findColValue(
						"bd_project",
						"project_name",
						"nvl(dr,0) = 0 and pk_project = '" + temp.getProject()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"project_name", name));
				// 收款银行账号
				if (temp.getRecaccount() != null) {
					String skyhname = (String) getHyPubBO().findColValue(
							"bd_bankaccsub",
							"accnum",
							"nvl(dr,0) = 0 and pk_bankaccsub = '"
									+ temp.getRecaccount() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"skyhzh", skyhname));
				}
				// 付款款银行账号
				if (temp.getPayaccount() != null) {
					String fkyhzh = (String) getHyPubBO().findColValue(
							"bd_bankaccsub",
							"accnum",
							"nvl(dr,0) = 0 and pk_bankaccsub = '"
									+ temp.getPayaccount() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"fkyhzh", fkyhzh));
				}
			}
			// 其他字段------end
			Map<String, Object> workflowRequestTableFieldsMap = new HashMap<String, Object>();
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	// 中台主表数据
	private JSONArray getZTMainMap(GatheringBillVO parentVO, String recaccount,
			String pk_dept_v, String pk_psndoc, String payaccount,
			String cashitem) throws BusinessException {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// ---------其他字段begin
		FinanceOrgVO orgVO = (FinanceOrgVO) getHyPubBO().queryByPrimaryKey(
				FinanceOrgVO.class, parentVO.getPk_org());
		list.add(OaWorkFlowUtil.listAddObj("zzmc", orgVO.getName()));
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
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
		// 原能投字段START---
		// 单据号
		list.add(OaWorkFlowUtil.listAddObj("djh", parentVO.getBillno()));
		// 单据日期
		list.add(OaWorkFlowUtil.listAddObj("djrq", parentVO.getBilldate()
				.getYear()
				+ "-"
				+ parentVO.getBilldate().getStrMonth()
				+ "-"
				+ parentVO.getBilldate().getStrDay()));
		// 部门
		if (pk_dept_v != null) {
			String bm = (String) getHyPubBO().findColValue("org_dept_v",
					"name", "nvl(dr,0) = 0 and pk_vid = '" + pk_dept_v + "'");
			list.add(OaWorkFlowUtil.listAddObj("bm", bm));
		}
		// 业务员
		if (pk_psndoc != null) {
			String ywy = (String) getHyPubBO()
					.findColValue("bd_psndoc", "name",
							"nvl(dr,0) = 0 and pk_psndoc = '" + pk_psndoc + "'");
			list.add(OaWorkFlowUtil.listAddObj("ywy", ywy));
		}
		// 收款银行账户
		if (recaccount != null) {
			String skyhname = (String) getHyPubBO().findColValue(
					"bd_bankaccsub", "accnum",
					"nvl(dr,0) = 0 and pk_bankaccsub = '" + recaccount + "'");
			list.add(OaWorkFlowUtil.listAddObj("skyhzh", skyhname));
		}
		// 付款银行账户
		if (payaccount != null) {
			String fkyhname = (String) getHyPubBO().findColValue(
					"bd_bankaccsub", "accnum",
					"nvl(dr,0) = 0 and pk_bankaccsub = '" + payaccount + "'");
			list.add(OaWorkFlowUtil.listAddObj("fkyhzh", fkyhname));
		}
		// 财政性资金
		if (parentVO.getDef62() != null) {
			String czxzj = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef62()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("czxzj", czxzj));
		}
		// 现金流量项目
		if (cashitem != null) {
			String xname = (String) getHyPubBO().findColValue("bd_cashflow",
					"name",
					"nvl(dr,0) = 0 and pk_cashflow = '" + cashitem + "'");
			list.add(OaWorkFlowUtil.listAddObj("xjllxm", xname));
		}
		// 资金性质
		if (parentVO.getDef63() != null) {
			String zjxz = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef63()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("zjxz", zjxz));
		}
		// 区县
		if (parentVO.getDef64() != null) {
			String qx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef64()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("qx", qx));
		}
		// 客户
		if (parentVO.getCustomer() != null) {
			String bm = (String) getHyPubBO().findColValue(
					"bd_customer",
					"name",
					"nvl(dr,0) = 0 and pk_customer = '"
							+ parentVO.getCustomer() + "'");
			list.add(OaWorkFlowUtil.listAddObj("kh", bm));
		}
		// 原币金额
		if (parentVO.getMoney() != null) {
			list.add(OaWorkFlowUtil.listAddObj("ybje", parentVO.getMoney()
					.setScale(2, UFDouble.ROUND_HALF_UP) + ""));
		}
		// 组织本币金额
		if (parentVO.getLocal_money() != null) {
			list.add(OaWorkFlowUtil.listAddObj("zzbbje", parentVO
					.getLocal_money().setScale(2, UFDouble.ROUND_HALF_UP) + ""));
		}
		// 制单人
		list.add(OaWorkFlowUtil.listAddObj("zdr", userVO.getUser_name()));
		// 事由填写规范
		if (null != parentVO.getDef48()) {
			String sytxgf = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef48()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sytxgf", sytxgf));
		}
		// 结算方式
		if (null != parentVO.getPk_balatype()) {
			String jsfs = (String) getHyPubBO().findColValue(
					"bd_balatype",
					"name",
					"nvl(dr,0) = 0 and pk_balatype = '"
							+ parentVO.getPk_balatype() + "'");
			list.add(OaWorkFlowUtil.listAddObj("jsfs", jsfs));
		}
		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	// TODO 单据同步OA功能 start create by zwh
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

	public static String getDef2(String pk_org) throws BusinessException {
		String def2 = "";
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		if (null != orgVO) {
			def2 = orgVO.getDef2();
		}
		return def2;
	}
}