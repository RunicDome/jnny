package nc.impl.pbm.materialplan;

//物资及服务需求单
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pbm.materialplan.rule.DeleteLineRule;
import nc.bs.pbm.materialplan.rule.MaterialPlanCalPriceRule;
import nc.bs.pbm.materialplan.rule.SetBizScopeInfoRule;
import nc.bs.pbm.materialplan.rule.WriteBackMaterialStockRule;
import nc.bs.pmpub.rule.AppendBusiTypeBeforeRule;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pbm.materialplan.bp.rule.DeleteHisVersionRule;
import nc.impl.pbm.materialplan.bp.rule.OnlyProjectRule;
import nc.impl.pbm.materialplan.bp.rule.ReleaseRule;
import nc.impl.pbm.materialplan.bp.rule.StockOrgValidateRule;
import nc.impl.pbm.materialplan.bp.rule.UnRealeaseRule;
import nc.impl.pbm.materialplan.bp.rule.UnRealeaseVaildateRule;
import nc.impl.pm.billrule.ApproveStatusCheckRule;
import nc.impl.pm.billrule.CreateOldVersionRule;
import nc.impl.pm.billrule.WBSPretaskCompleteCheckRule;
import nc.impl.pm.billrule.WriteBackToProjectBeforeRule;
import nc.impl.pm.docrule.PMFireEventRule;
import nc.impl.pmpub.servicebase.BillBaseImpl;
import nc.impl.pmpub.servicebase.action.ApproveAction;
import nc.impl.pmpub.servicebase.action.CommitAction;
import nc.impl.pmpub.servicebase.action.DeleteAction;
import nc.impl.pmpub.servicebase.action.InsertAction;
import nc.impl.pmpub.servicebase.action.UnApproveAction;
import nc.impl.pmpub.servicebase.action.UpdateAction;
import nc.itf.pbm.materialplan.pvt.IMaterialPlanInterface;
import nc.ui.pcm.utils.GetDao;
import nc.vo.bd.material.MaterialVO;
import nc.vo.bd.material.MaterialVersionVO;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.org.DeptVO;
import nc.vo.org.ItemOrgVO;
import nc.vo.org.OrgVO;
import nc.vo.pbm.materialplan.MaterialPlanBillVO;
import nc.vo.pbm.materialplan.MaterialPlanBodyVO;
import nc.vo.pbm.materialplan.MaterialPlanHeadVO;
import nc.vo.pim.project.ProjectHeadVO;
import nc.vo.pm.util.app.ModuleInfoQueryUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

// 物资及服务需求单
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MaterialPlanInterfaceImpl extends BillBaseImpl<MaterialPlanBillVO>
		implements IMaterialPlanInterface {

	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表明
	// TODO 单据同步OA功能 start create by zwh
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
	public static String WORKFLOWID_JNRL = "32";
	public static String WORKFLOWID_JNNY = "254";

	public static String BODY_TABLE_NAME_JNRL = "formtable_main_33_dt1";
	public static String BODY_TABLE_NAME_JNNY = "formtable_main_300_dt1";
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

	private MaterialPlanBillVO[] senOaData(MaterialPlanBillVO[] billVOs)
			throws BusinessException {
		for (MaterialPlanBillVO temp : billVOs) {
			MaterialPlanHeadVO hvo = temp.getParentVO();
			OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
					hvo.getPk_org());
			if (hvo.getBill_type().equals("4D14") && null != orgVO.getDef1()) {
				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hvo.getPk_org(),
						"4D14");
				if (oaVo != null && oaVo.getIsdr() == 0) {
					WorkFId = oaVo.getFlowid();
					TableName = oaVo.getTablename();
					// 获取主表数据
					JSONArray headData = getNewMainMap(hvo);
					// 获取子表数据
					JSONArray bodyData = getNewDtaileDataMap(temp);
					// 构造workflow信息
					OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
					workFlowVO.setPrimaryKey(temp.getPrimaryKey());
					workFlowVO.setPkGroup(hvo.getPk_group());
					workFlowVO.setPkOrg(hvo.getPk_org());
					workFlowVO.setBillMaker(hvo.getBillmaker());
					workFlowVO.setCreator(hvo.getCreator());
					workFlowVO.setBillCode("4D14");
					// 接口获取
					workFlowVO.setWorkflowId(OaWorkFlowUtil
							.getOAFlowID(WorkFId));
					workFlowVO.setWorkflowName("物资及服务需求单");
					WorkFlowBill bill = OaWorkFlowUtil
							.getWorkFlowBill(workFlowVO);
					bill.setDef3("ZT");
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
				} else if ("1".equals(getDef2(hvo.getPk_org()))) {
					JSONArray headData = getMainMap(hvo);
					JSONArray bodyData = getDtaileDataMap(temp.getChildrenVO());

					OaWorkFlowUtil.sendOaData(headData, bodyData,
							getWorkFlowBill(temp));
				}
			} else {
				// 港华单据
				if ("4".equals(getDef2(hvo.getPk_org()))
						&& hvo.getBill_type().equals("4D14")) {
					OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(
							hvo.getPk_org(), "4D14");
					if (oaVo != null && oaVo.getIsdr() == 0) {
						WorkFId = oaVo.getFlowid();
						TableName = oaVo.getTablename();
						// 获取主表数据
						JSONArray headData = getNewMainMap(hvo);
						// 获取子表数据
						JSONArray bodyData = getNewDtaileDataMap(temp);
						// 构造workflow信息
						OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
						workFlowVO.setPrimaryKey(temp.getPrimaryKey());
						workFlowVO.setPkGroup(hvo.getPk_group());
						workFlowVO.setPkOrg(hvo.getPk_org());
						workFlowVO.setBillMaker(hvo.getBillmaker());
						workFlowVO.setCreator(hvo.getCreator());
						workFlowVO.setBillCode("4D14");
						// 接口获取
						workFlowVO.setWorkflowId(OaWorkFlowUtil
								.getOAFlowID(WorkFId));
						workFlowVO.setWorkflowName("物资及服务需求单");
						WorkFlowBill bill = OaWorkFlowUtil
								.getWorkFlowBill(workFlowVO);
						bill.setDef3("ZT");
						// 制单人身份证号
						UserVO userVO = (UserVO) getHyPubBO()
								.queryByPrimaryKey(UserVO.class,
										hvo.getBillmaker());
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
		return billVOs;
	}

	private JSONArray getNewDtaileDataMap(MaterialPlanBillVO temp)
			throws BusinessException {
		// 获取详细信息
		MaterialPlanBodyVO[] bvos = (MaterialPlanBodyVO[]) temp.getChildrenVO();
		Map bodyMap = getBody(bvos);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
		return dtlistString;
	}

	private Map getBody(MaterialPlanBodyVO[] bvo) throws BusinessException {
		// TODO Auto-generated method stub
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (MaterialPlanBodyVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			// 物料信息
			MaterialVO materialVO = (MaterialVO) getHyPubBO()
					.queryByPrimaryKey(MaterialVO.class, temp.getPk_material());
			if (null != materialVO) {
				// 物料编码
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"material_code", materialVO.getCode()));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"wlbm", materialVO.getCode()));
				// 物料名称
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"material_name", materialVO.getName()));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"wlmc", materialVO.getName()));
				// 规格
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"materialspec", materialVO.getMaterialspec()));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("gg",
						materialVO.getMaterialspec()));
				// 型号
				String str4 = "";
				if (null != materialVO.getMaterialtype()) {
					str4 = materialVO.getMaterialtype();
				}
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"materialtype", str4));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("fwl",
						materialVO.getFee().booleanValue() ? "是" : "否"));// 服务类
			}
			// 单位
			String dwstr = "";
			if (null != temp.getPk_measdoc()) {
				dwstr = (String) getHyPubBO().findColValue(
						"bd_measdoc",
						"name",
						"nvl(dr,0) = 0 and pk_measdoc  = '"
								+ temp.getPk_measdoc() + "'");
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("dw",
					dwstr));
			// CBS编码
			String cbsBm = (String) getHyPubBO().findColValue(
					"bd_cbsnode",
					"code",
					"nvl(dr,0) = 0 and pk_cbsnode  = '" + temp.getPk_cbs_node()
							+ "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("cbsbm",
					cbsBm));
			// CBS名称
			String cbsName = (String) getHyPubBO().findColValue(
					"bd_cbsnode",
					"name",
					"nvl(dr,0) = 0 and pk_cbsnode  = '" + temp.getPk_cbs_node()
							+ "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("cbsmc",
					cbsName));
			// 22222222能投22222222
			if (null != temp.getRowno()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("xh",
						temp.getRowno()));// 序号
			}
			if (null != temp.getNnum()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sl",
						temp.getNnum() + ""));// 数量
			}
			if (null != temp.getMater_unit_price()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dj",
						null == temp.getMater_unit_price() ? "0" : temp
								.getMater_unit_price() + ""));// 单价
			}
			if (null != temp.getService_prc_ratio()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fwjgxs", temp.getService_prc_ratio() == null ? "0"
								: temp.getService_prc_ratio() + ""));// 服务价格系数
			}
			if (temp.getRequire_date() != null) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"xqrq", temp.getRequire_date().getYear() + "-"
								+ temp.getRequire_date().getStrMonth() + "-"
								+ temp.getRequire_date().getStrDay() + ""));// 需求日期
			}
			if (null != temp.getPk_stockorg()) {
				String pk_stockorg = (String) getHyPubBO().findColValue(
						"org_stockorg",
						"name",
						"nvl(dr,0) = 0 and pk_stockorg = '"
								+ temp.getPk_stockorg() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"kczzmc", pk_stockorg + ""));// 库存组织名称
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bz",
					temp.getMemo() + ""));// 备注
			if (null != temp.getMater_price()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("zj",
						temp.getMater_price() + ""));// 材料费总价
			}
			if (null != temp.getPk_project()) {
				String xm = (String) getHyPubBO().findColValue(
						"bd_project",
						"project_name",
						"nvl(dr,0) = 0 and pk_project = '"
								+ temp.getPk_project() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("xm",
						xm + ""));// 项目名称
			}
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

	private JSONArray getNewMainMap(MaterialPlanHeadVO parentVO)
			throws BusinessException {
		// TODO Auto-generated method stub
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = new ArrayList();
		if (!"1".equals(getDef2(parentVO.getPk_org()))) {
			list = OaWorkFlowUtil.transBean2Map(parentVO);
		}
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
		OrgVO yhorgvo = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				userVO.getPk_org());
		list.add(OaWorkFlowUtil.listAddObj("yhsszzzj", userVO.getPk_org()));
		list.add(OaWorkFlowUtil.listAddObj("yhsszzbm", yhorgvo.getCode()));
		list.add(OaWorkFlowUtil.listAddObj("yhsszzmc", yhorgvo.getName()));
		// 单据类型
		String billTypeName = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and pk_billtypeid  = '"
						+ parentVO.getBill_type() + "'");
		list.add(OaWorkFlowUtil.listAddObj("typename", billTypeName));

		// 项目
		if (null != parentVO.getPk_project()) {
			ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
					.queryByPrimaryKey(ProjectHeadVO.class,
							parentVO.getPk_project());
			// 项目编码
			list.add(OaWorkFlowUtil.listAddObj("project_code",
					projectVO.getProject_code()));
			list.add(OaWorkFlowUtil.listAddObj("xmzj",
					projectVO.getProject_code()));// 项目编码
			// 项目名称
			list.add(OaWorkFlowUtil.listAddObj("project_name",
					projectVO.getProject_name()));
			list.add(OaWorkFlowUtil.listAddObj("xmmc",
					projectVO.getProject_name()));// 项目名称
		}
		// 需求编制人
		PsndocVO psndocVO = (PsndocVO) getHyPubBO().queryByPrimaryKey(
				PsndocVO.class, parentVO.getPk_req_maker());
		list.add(OaWorkFlowUtil.listAddObj("xqbzr", psndocVO.getName()));
		// 需求编制部门
		DeptVO deptVO = (DeptVO) getHyPubBO().queryByPrimaryKey(DeptVO.class,
				parentVO.getPk_req_dept());
		list.add(OaWorkFlowUtil.listAddObj("xqbzbm", deptVO.getName()));
		// 22222222能投22222222
		list.add(OaWorkFlowUtil.listAddObj("djzj", parentVO.getPrimaryKey()));// 单据主键
		ItemOrgVO itemOrgVO = (ItemOrgVO) getHyPubBO().queryByPrimaryKey(
				ItemOrgVO.class, parentVO.getPk_org());
		list.add(OaWorkFlowUtil.listAddObj("szzzzj", parentVO.getPk_org()));// 组织主键
		list.add(OaWorkFlowUtil.listAddObj("szzzmc", itemOrgVO.getName()));// 组织名称
		list.add(OaWorkFlowUtil.listAddObj("zzbm", itemOrgVO.getCode()));// 组织编码
		list.add(OaWorkFlowUtil.listAddObj("xqdh", parentVO.getBill_code()));// 需求单号
		list.add(OaWorkFlowUtil.listAddObj("xqbzbmzj", deptVO.getCode()));// 需求编制部门编码
		list.add(OaWorkFlowUtil.listAddObj("xqbzbmmc", deptVO.getName()));// 需求编制部门名称

		list.add(OaWorkFlowUtil.listAddObj("xqbzrzj", psndocVO.getCode()));// 需求编制人编码
		list.add(OaWorkFlowUtil.listAddObj("xqbzrmc", psndocVO.getName()));// 需求编制人名称

		if (parentVO.getReq_make_date() != null) {
			list.add(OaWorkFlowUtil.listAddObj("xqbzrq", parentVO
					.getReq_make_date().getYear()
					+ "-"
					+ parentVO.getReq_make_date().getStrMonth()
					+ "-"
					+ parentVO.getReq_make_date().getStrDay()));// 需求编制日期
		}
		if (null != userVO.getPrimaryKey()) {
			list.add(OaWorkFlowUtil.listAddObj("zdrzj", userVO.getPrimaryKey()));// 制单人
		}
		list.add(OaWorkFlowUtil.listAddObj("sfzh", idCard));// 制单人身份证号
		if (null != userVO.getUser_code()) {
			list.add(OaWorkFlowUtil.listAddObj("fqr", userVO.getUser_code()));// 发起人
		}
		if (null != userVO.getUser_name()) {
			list.add(OaWorkFlowUtil.listAddObj("zdrmc", userVO.getUser_name()));// 制单人名称
		}
		if (null != parentVO.getBillmaketime()) {
			list.add(OaWorkFlowUtil.listAddObj("zdrq", parentVO
					.getBillmaketime().getYear()
					+ "-"
					+ parentVO.getBillmaketime().getStrMonth()
					+ "-"
					+ parentVO.getBillmaketime().getStrDay()));// 制单日期
		}
		if (parentVO.getHdef7() != null) {
			String xmlx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getHdef7()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("xmlx", xmlx));// 项目类型
		}
		if (parentVO.getHdef5() != null) {
			String wljfwlx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getHdef5()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("wljfwlx", wljfwlx));// 物料及服务类型
		}
		if (parentVO.getHdef6() != null) {
			String fqffgld = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getHdef6()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("fqffgld", fqffgld));// 分管领导
		}
		list.add(OaWorkFlowUtil.listAddObj("bz", parentVO.getMemo()));// 备注
		// 能投新增 物资需求类型 hdef1
		if (parentVO.getHdef1() != null) {
			String wzxqlxbm = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getHdef1()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("wzxqlxbm", wzxqlxbm));// 物资需求类型编码
			String wzxqlx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getHdef1()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("wzxqlx", wzxqlx));// 物资需求类型
		}
		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	private WorkFlowBill getWorkFlowBill(MaterialPlanBillVO temp)
			throws BusinessException {
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				temp.getParentVO().getBillmaker());
		String where = " nvl(dr,0) = 0 and pk_bill = '" + temp.getPrimaryKey()
				+ "'";
		WorkFlowBill[] workFlowBills = (WorkFlowBill[]) getHyPubBO()
				.queryByCondition(WorkFlowBill.class, where);
		WorkFlowBill workFlowBill = new WorkFlowBill();
		if (null != workFlowBills && workFlowBills.length > 1) {
			throw new BusinessException("查询到多条流程记录");
		}
		if (null != workFlowBills && workFlowBills.length == 1) {
			workFlowBill = workFlowBills[0];
			workFlowBill.setWorkflowName("物资及服务需求单");
		} else {
			workFlowBill.setWorkflowId(WORKFLOWID_JNRL);
			UserVO user = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
					temp.getParentVO().getBillmaker());
			workFlowBill.setDef5(user.getUser_code());
			if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
				PsndocVO psndoc = (PsndocVO) getHyPubBO().queryByPrimaryKey(
						PsndocVO.class, user.getPk_psndoc());
				workFlowBill.setDef5(psndoc.getId());
				workFlowBill.setWorkflowId(WORKFLOWID_JNNY);
			}
			workFlowBill.setPk_group(temp.getParentVO().getPk_group());
			workFlowBill.setPk_org(temp.getParentVO().getPk_org());
			workFlowBill.setPk_bill(temp.getPrimaryKey());
			workFlowBill.setBill_code(temp.getParentVO().getBill_type());
			workFlowBill.setUser_name(userVO.getUser_name());
			workFlowBill.setUser_code(userVO.getUser_code());
			workFlowBill.setBill_status("1");
			workFlowBill.setCreationtime(new UFDate().toStdString());
			workFlowBill.setCreator(temp.getParentVO().getCreator());
			workFlowBill.setWorkflowName("物资及服务需求单");
		}
		return workFlowBill;
	}

	private JSONArray getDtaileDataMap(MaterialPlanBodyVO[] bodyVOS)
			throws BusinessException {
		List dtlist = new ArrayList();
		Map dtMap = new HashMap();
		dtMap.put("tableDBName", BODY_TABLE_NAME_JNRL);
		if ("2".equals(getDef2(bodyVOS[0].getPk_org()))) {
			dtMap.put("tableDBName", BODY_TABLE_NAME_JNNY);
		}
		List workflowRequestTableRecords = new ArrayList();
		for (MaterialPlanBodyVO temp : bodyVOS) {
			Map workflowRequestTableFieldsMap = new HashMap();
			List workflowRequestTableFields = new ArrayList();

			Map xh = new HashMap();
			xh.put("fieldName", "xh");
			xh.put("fieldValue", temp.getRowno());
			workflowRequestTableFields.add(xh);
			MaterialVersionVO materialVersionVO = (MaterialVersionVO) getHyPubBO()
					.queryByPrimaryKey(MaterialVersionVO.class,
							temp.getPk_material());

			if (null != materialVersionVO) {
				Map wlbm = new HashMap();
				wlbm.put("fieldName", "wlbm");
				wlbm.put("fieldValue", materialVersionVO.getCode());
				workflowRequestTableFields.add(wlbm);

				Map wlmc = new HashMap();
				wlmc.put("fieldName", "wlmc");
				wlmc.put("fieldValue", materialVersionVO.getName());
				workflowRequestTableFields.add(wlmc);

				Map gg = new HashMap();
				gg.put("fieldName", "gg");
				gg.put("fieldValue", materialVersionVO.getMaterialspec());
				workflowRequestTableFields.add(gg);

				Map fwl = new HashMap();
				fwl.put("fieldName", "fwl");
				fwl.put("fieldValue",
						materialVersionVO.getFee().booleanValue() ? "是" : "否");
				workflowRequestTableFields.add(fwl);
			}
			// 单位
			String dwstr = "";
			if (null != temp.getPk_measdoc()) {
				dwstr = (String) getHyPubBO().findColValue(
						"bd_measdoc",
						"name",
						"nvl(dr,0) = 0 and pk_measdoc  = '"
								+ temp.getPk_measdoc() + "'");
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("dw",
					dwstr));
			
			Map sl = new HashMap();
			sl.put("fieldName", "sl");
			sl.put("fieldValue", temp.getNnum());
			workflowRequestTableFields.add(sl);

			Map dj = new HashMap();
			dj.put("fieldName", "dj");
			dj.put("fieldValue", null == temp.getMater_unit_price() ? "0"
					: temp.getMater_unit_price().toString());
			workflowRequestTableFields.add(dj);

			Map fwjgxs = new HashMap();
			fwjgxs.put("fieldName", "fwjgxs");
			fwjgxs.put("fieldValue", temp.getService_prc_ratio() == null ? "0"
					: temp.getService_prc_ratio().toString());
			workflowRequestTableFields.add(fwjgxs);

			Map xqrq = new HashMap();
			xqrq.put("fieldName", "xqrq");
			if (null != temp.getRequire_date()) {
				xqrq.put("fieldValue", temp.getRequire_date().getYear() + "-"
						+ temp.getRequire_date().getStrMonth() + "-"
						+ temp.getRequire_date().getStrDay());
				workflowRequestTableFields.add(xqrq);
			}

			Map kczzmc = new HashMap();
			kczzmc.put("fieldName", "kczzmc");
			String pk_stockorg = (String) getHyPubBO().findColValue(
					"org_stockorg",
					"name",
					"nvl(dr,0) = 0 and pk_stockorg = '" + temp.getPk_stockorg()
							+ "'");
			kczzmc.put("fieldValue", pk_stockorg);
			workflowRequestTableFields.add(kczzmc);

			Map bz = new HashMap();
			bz.put("fieldName", "bz");
			bz.put("fieldValue", temp.getMemo());
			workflowRequestTableFields.add(bz);

			String sb = (String) getHyPubBO().findColValue(
					"bd_project",
					"project_name",
					"nvl(dr,0) = 0 and pk_project = '" + temp.getPk_project()
							+ "'");
			Map xm = OaWorkFlowUtil.listAdd("xm", sb);
			workflowRequestTableFields.add(xm);

			if (temp.getMater_price() != null) {
				Map zj = OaWorkFlowUtil.listAdd("zj", temp.getMater_price()
						.toString());
				workflowRequestTableFields.add(zj);
			}

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}

		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		dtlist.add(dtMap);
		JSONArray dtlistString = JSONArray.fromObject(dtlist);
		return dtlistString;
	}

	private JSONArray getMainMap(MaterialPlanHeadVO hvo)
			throws BusinessException {
		List list = new ArrayList();

		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				hvo.getPk_org());
		if (null != orgVO) {

			// Map szgs =OaWorkFlowUtil.listAdd("szgs",orgVO.getCode() );
			// list.add(szgs);
		}

		Map djzj = new HashMap();
		djzj.put("fieldName", "djzj");
		djzj.put("fieldValue", hvo.getPk_mater_plan());
		list.add(djzj);

		ItemOrgVO itemOrgVO = (ItemOrgVO) getHyPubBO().queryByPrimaryKey(
				ItemOrgVO.class, hvo.getPk_org());

		Map szzzzj = new HashMap();
		szzzzj.put("fieldName", "szzzzj");
		szzzzj.put("fieldValue", itemOrgVO.getCode());
		list.add(szzzzj);

		Map szzzmc = new HashMap();
		szzzmc.put("fieldName", "szzzmc");
		szzzmc.put("fieldValue", itemOrgVO.getName());
		list.add(szzzmc);

		Map xqdh = new HashMap();
		xqdh.put("fieldName", "xqdh");
		xqdh.put("fieldValue", hvo.getBill_code());
		list.add(xqdh);

		String project_code = (String) getHyPubBO().findColValue("bd_project",
				"project_code",
				"nvl(dr,0) = 0 and pk_project = '" + hvo.getPk_project() + "'");

		Map xmzj = new HashMap();
		xmzj.put("fieldName", "xmzj");
		xmzj.put("fieldValue", project_code);
		list.add(xmzj);

		Map xmmc = new HashMap();
		xmmc.put("fieldName", "xmmc");
		String pkProject = (String) getHyPubBO().findColValue("bd_project",
				"project_name",
				"nvl(dr,0) = 0 and pk_project = '" + hvo.getPk_project() + "'");
		xmmc.put("fieldValue", pkProject);
		list.add(xmmc);

		DeptVO deptVO = (DeptVO) getHyPubBO().queryByPrimaryKey(DeptVO.class,
				hvo.getPk_req_dept());

		Map xqbzbmzj = new HashMap();
		xqbzbmzj.put("fieldName", "xqbzbmzj");
		xqbzbmzj.put("fieldValue", deptVO.getCode());
		list.add(xqbzbmzj);

		Map xqbzbmmc = new HashMap();
		xqbzbmmc.put("fieldName", "xqbzbmmc");
		xqbzbmmc.put("fieldValue", deptVO.getName());
		list.add(xqbzbmmc);

		PsndocVO psndocVO = (PsndocVO) getHyPubBO().queryByPrimaryKey(
				PsndocVO.class, hvo.getPk_req_maker());

		Map xqbzrzj = new HashMap();
		xqbzrzj.put("fieldName", "xqbzrzj");
		xqbzrzj.put("fieldValue", psndocVO.getCode());
		list.add(xqbzrzj);

		Map xqbzrmc = new HashMap();
		xqbzrmc.put("fieldName", "xqbzrmc");
		xqbzrmc.put("fieldValue", psndocVO.getName());
		list.add(xqbzrmc);

		Map xqbzrq = new HashMap();
		xqbzrq.put("fieldName", "xqbzrq");
		xqbzrq.put("fieldValue", hvo.getReq_make_date().getYear() + "-"
				+ hvo.getReq_make_date().getStrMonth() + "-"
				+ hvo.getReq_make_date().getStrDay());
		list.add(xqbzrq);

		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				hvo.getBillmaker());

		Map zdrzj = new HashMap();
		zdrzj.put("fieldName", "zdrzj");
		zdrzj.put("fieldValue", userVO.getUser_code());
		list.add(zdrzj);

		Map sfzh = new HashMap();
		String id = (String) getHyPubBO().findColValue("bd_psndoc", "id",
				"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
		if (null == id) {
			throw new BusinessException("制单人身份证号码未维护");
		}
		sfzh.put("fieldName", "sfzh");
		sfzh.put("fieldValue", id);
		list.add(sfzh);

		Map fqr = new HashMap();
		fqr.put("fieldName", "fqr");
		fqr.put("fieldValue", userVO.getUser_code());
		list.add(fqr);

		Map zdrmc = new HashMap();
		zdrmc.put("fieldName", "zdrmc");
		zdrmc.put("fieldValue", userVO.getUser_name());
		list.add(zdrmc);

		Map zdrq = new HashMap();
		zdrq.put("fieldName", "zdrq");
		zdrq.put("fieldValue", hvo.getBillmaketime().getYear() + "-"
				+ hvo.getBillmaketime().getStrMonth() + "-"
				+ hvo.getBillmaketime().getStrDay());
		list.add(zdrq);

		if ("2".equals(getDef2(hvo.getPk_org()))) {
			Map wljfwlx = new HashMap();
			wljfwlx.put("fieldName", "xmlx");
			String where = " pk_defdoc = '"
					+ hvo.getHdef7()
					+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'xmlx' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
			String def7 = (String) getHyPubBO().findColValue("bd_defdoc",
					"code", where);
			wljfwlx.put("fieldValue", def7);
			list.add(wljfwlx);
		} else {
			if (null != hvo.getHdef5()) {
				Map wljfwlx = new HashMap();
				wljfwlx.put("fieldName", "wljfwlx");
				String where = " pk_defdoc = '"
						+ hvo.getHdef5()
						+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'wlfwlx' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
				String def5 = (String) getHyPubBO().findColValue("bd_defdoc",
						"name", where);
				wljfwlx.put("fieldValue", def5);
				list.add(wljfwlx);
			}
		}

		if (null != hvo.getHdef6()) {
			Map fqffgld = new HashMap();
			fqffgld.put("fieldName", "fqffgld");
			String where = " pk_defdoc = '"
					+ hvo.getHdef6()
					+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'fgld' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
			String def6 = (String) getHyPubBO().findColValue("bd_defdoc",
					"name", where);
			fqffgld.put("fieldValue", def6);
			list.add(fqffgld);
		}

		Map bz = OaWorkFlowUtil.listAdd("bz", hvo.getMemo());
		list.add(bz);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	// TODO 单据同步OA功能 end create by zwh

	public MaterialPlanInterfaceImpl() {
	}

	public MaterialPlanBillVO[] insertMaterialPlanVOS(
			MaterialPlanBillVO[] billVOs) throws BusinessException {
		// beforeCheck(billVOs);
		return (MaterialPlanBillVO[]) insert(billVOs);
	}

	// 校验该项目是否在年度投资计划中并且主材金额大于0，不在年度投资计划中不允许保存；
	public void beforeCheck(MaterialPlanBillVO[] billVOs)
			throws BusinessException {
		// TODO Auto-generated method stub
		if (billVOs.length > 0) {

		} else {
			throw new BusinessException("获取MaterialPlanBillVO失败！");
		}
	}

	public MaterialPlanBillVO[] updateMaterialPlanVOS(
			MaterialPlanBillVO[] billVOs, MaterialPlanBillVO[] originBillVOs)
			throws BusinessException {
		return (MaterialPlanBillVO[]) update(billVOs, originBillVOs);
	}

	public MaterialPlanBillVO[] deleteMaterialPlanVOS(
			MaterialPlanBillVO[] billVOs) throws BusinessException {
		return (MaterialPlanBillVO[]) delete(billVOs);
	}

	public Object approveMaterialPlanVOS(MaterialPlanBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		/*
		 * int flag = checkifdo(); if(flag != 1){ throw new
		 * BusinessException("已提交OA审批！"); }
		 */
		return approve(billVOs, pfParamVO);
	}

	public MaterialPlanBillVO[] unapproveMaterialPlanVOS(
			MaterialPlanBillVO[] billVOs, PfParameterVO pfParamVO)
			throws BusinessException {
		return (MaterialPlanBillVO[]) unApprove(billVOs, pfParamVO);
	}

	public MaterialPlanBillVO[] commitMaterialPlanVOS(
			MaterialPlanBillVO[] billVOs) throws BusinessException {
		MaterialPlanBillVO[] materialPlanBillVOS = (MaterialPlanBillVO[]) commit(billVOs);

		return senOaData(materialPlanBillVOS);
	}

	public MaterialPlanBillVO[] unCommitMaterialPlanVOS(
			MaterialPlanBillVO[] billVOs, PfParameterVO pfParamVO)
			throws BusinessException {
		MaterialPlanBillVO[] aggVO = unCommit(billVOs, pfParamVO);
		unOaCommit(aggVO);
		return aggVO;
	}

	private void unOaCommit(MaterialPlanBillVO[] aggVO)
			throws BusinessException {
		for (MaterialPlanBillVO temp : aggVO) {
			if ("4D14".equals(temp.getParentVO().getBill_type())) {
				OaWorkFlowUtil.backOaWorkFlow(temp.getPrimaryKey());
				/*
				 * String strWhere = " nvl(dr,0) = 0  and pk_bill ='" +
				 * temp.getPrimaryKey() + "'"; WorkFlowBill[] workFlowBills =
				 * (WorkFlowBill[]) new
				 * HYPubBO().queryByCondition(WorkFlowBill.class, strWhere);
				 * if(null != workFlowBills && workFlowBills.length == 1){ throw
				 * new BusinessException("已提交OA系统单据不允许收回！"); }
				 */
			}
		}
	}

	public MaterialPlanBillVO[] freezeMaterialPlanVOS(
			MaterialPlanBillVO[] billVOs, MaterialPlanBillVO[] originBillVOs)
			throws BusinessException {
		UpdateAction<MaterialPlanBillVO> action = createUpdateAction();

		action.addBeforeRule(new PMFireEventRule("1023"));

		action.addAfterRule(new PMFireEventRule("1024"));

		return (MaterialPlanBillVO[]) action.processAction(billVOs,
				originBillVOs);
	}

	public MaterialPlanBillVO[] unfrozenMaterialPlanVOS(
			MaterialPlanBillVO[] billVOs, MaterialPlanBillVO[] originBillVOs)
			throws BusinessException {
		UpdateAction<MaterialPlanBillVO> action = createUpdateAction();

		action.addBeforeRule(new PMFireEventRule("1025"));

		action.addAfterRule(new PMFireEventRule("1026"));

		return (MaterialPlanBillVO[]) action.processAction(billVOs,
				originBillVOs);
	}

	public MaterialPlanBillVO[] releaseMaterialPlanVOS(
			MaterialPlanBillVO[] billVOs, MaterialPlanBillVO[] originBillVOs)
			throws BusinessException {
		UpdateAction<MaterialPlanBillVO> action = createUpdateAction();

		action.addAfterRule(new ReleaseRule());

		action.addBeforeRule(new PMFireEventRule("1090"));

		action.addAfterRule(new PMFireEventRule("1091"));

		return (MaterialPlanBillVO[]) action.processAction(billVOs,
				originBillVOs);
	}

	public MaterialPlanBillVO[] UnreleaseMaterialPlanVOS(
			MaterialPlanBillVO[] billVOs, MaterialPlanBillVO[] originBillVOs)
			throws BusinessException {
		UpdateAction<MaterialPlanBillVO> action = createUpdateAction();

		action.addBeforeRule(new UnRealeaseVaildateRule());

		action.addAfterRule(new UnRealeaseRule());

		action.addBeforeRule(new PMFireEventRule("1094"));

		action.addAfterRule(new PMFireEventRule("1095"));

		return (MaterialPlanBillVO[]) action.processAction(billVOs,
				originBillVOs);
	}

	public MaterialPlanBillVO[] alterMaterialPlanVOS(
			MaterialPlanBillVO[] billVOs, MaterialPlanBillVO[] originBillVOs)
			throws BusinessException {
		UpdateAction<MaterialPlanBillVO> action = createUpdateAction();

		action.addBeforeRule(new SetBizScopeInfoRule());

		action.addBeforeRule(new DeleteLineRule());

		action.addBeforeRule(new CreateOldVersionRule(originBillVOs));

		action.addBeforeRule(new WriteBackMaterialStockRule());

		action.addBeforeRule(new MaterialPlanCalPriceRule());

		action.addBeforeRule(new PMFireEventRule("1092"));

		action.addAfterRule(new PMFireEventRule("1093"));

		return (MaterialPlanBillVO[]) action.processAction(billVOs,
				originBillVOs);
	}

	protected void initInsertAction(InsertAction<MaterialPlanBillVO> action) {
		super.initInsertAction(action);

		action.addBeforeRule(new SetBizScopeInfoRule());

		action.addBeforeRule(new OnlyProjectRule());

		action.addBeforeRule(new StockOrgValidateRule());

		action.addBeforeRule(new MaterialPlanCalPriceRule());

		action.addBeforeRule(new PMFireEventRule("1001"));

		action.addAfterRule(new PMFireEventRule("1002"));

		action.addBeforeRule(new AppendBusiTypeBeforeRule());
	}

	protected void initUpdateAction(UpdateAction<MaterialPlanBillVO> action) {
		super.initUpdateAction(action);

		action.addBeforeRule(new SetBizScopeInfoRule());

		action.addBeforeRule(new OnlyProjectRule());

		action.addBeforeRule(new StockOrgValidateRule());

		action.addBeforeRule(new MaterialPlanCalPriceRule());

		action.addBeforeRule(new PMFireEventRule("1003"));

		action.addAfterRule(new PMFireEventRule("1004"));
	}

	protected void initApproveAction(ApproveAction<MaterialPlanBillVO> action) {
		super.initApproveAction(action);
		action.addBeforeRule(new ApproveStatusCheckRule());

		action.addAfterRule(new WriteBackToProjectBeforeRule(Boolean.TRUE
				.booleanValue()));

		action.addBeforeRule(new PMFireEventRule("1019"));

		action.addAfterRule(new PMFireEventRule("1020"));
	}

	protected void initUnApproveAction(
			UnApproveAction<MaterialPlanBillVO> action) {
		super.initUnApproveAction(action);
		action.addBeforeRule(new WriteBackToProjectBeforeRule(Boolean.FALSE
				.booleanValue()));

		action.addBeforeRule(new PMFireEventRule("1021"));

		action.addAfterRule(new PMFireEventRule("1022"));
	}

	protected void initDeleteAction(DeleteAction<MaterialPlanBillVO> action) {
		super.initDeleteAction(action);

		action.addBeforeRule(new PMFireEventRule("1005"));

		action.addAfterRule(new PMFireEventRule("1006"));

		action.addAfterRule(new DeleteHisVersionRule());
	}

	protected void initCommitAction(CommitAction<MaterialPlanBillVO> action) {
		try {
			if (!ModuleInfoQueryUtil.isPMSCHEnabled())
				return;
		} catch (BusinessException e) {
			ExceptionUtils.wrappException(e);
		}

		action.addBeforeRule(new WBSPretaskCompleteCheckRule());
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

	// 检查是否可以 审批 取消审批 删除
	public int checkifdo() throws BusinessException {
		int flag = 0;
		String userID = InvocationInfoProxy.getInstance().getUserId();// 获取当前系统登录用户
		Logger.error("userID==" + userID);
		String sql = "select c.cuserid, a.pk_role, a.role_name,c.user_name from sm_role a left join sm_user_role b "
				+ "on a.pk_role=b.pk_role left join sm_user c on c.cuserid=b.cuserid where a.role_code = 'DTUSER'";
		List<Object[]> ls = getDao.query(sql);
		if (ls != null && ls.size() > 0) {
			for (int i = 0; i < ls.size(); i++) {
				if (userID.equals(ls.get(i)[0])) {
					flag = 1;
				}
			}
		}
		return flag;
	}
}