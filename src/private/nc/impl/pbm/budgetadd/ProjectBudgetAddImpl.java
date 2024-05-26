package nc.impl.pbm.budgetadd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nc.bs.pbm.budget.pubrule.CheckMnyByIncomeExpnesesOfFactor;
import nc.bs.pbm.budgetadd.rule.ApproveBudgetAddRule;
import nc.bs.pbm.budgetadd.rule.ApprovedBudgetAddBefRule;
import nc.bs.pbm.budgetadd.rule.BudgetAddDateRule;
import nc.bs.pbm.budgetadd.rule.BudgetAddFlagUpdateNAfterRule;
import nc.bs.pbm.budgetadd.rule.BudgetAddFlagUpdateYAfterRule;
import nc.bs.pbm.budgetadd.rule.InsertBudgetAddValidate;
import nc.bs.pbm.budgetadd.rule.UpdateBudgetAddApproveVersionRule;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pm.billrule.AddAuditInfoRule;
import nc.impl.pm.billrule.CheckDateTimeRule;
import nc.impl.pm.billrule.SetBizScopeInfoRule;
import nc.impl.pmpub.servicebase.BillBaseImpl;
import nc.impl.pmpub.servicebase.action.ApproveAction;
import nc.impl.pmpub.servicebase.action.DeleteAction;
import nc.impl.pmpub.servicebase.action.InsertAction;
import nc.impl.pmpub.servicebase.action.UnApproveAction;
import nc.impl.pmpub.servicebase.action.UpdateAction;
import nc.itf.pbm.budgetadd.pvt.IProjectBudgetAdd;
import nc.vo.bd.material.MaterialVO;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.org.OrgVO;
import nc.vo.pbm.budget.BudgetHeadVO;
import nc.vo.pbm.budgetadd.BudgetAddBillVO;
import nc.vo.pbm.budgetadd.BudgetAddBodyVO;
import nc.vo.pbm.budgetadd.BudgetAddHeadVO;
import nc.vo.pcm.contractalter.ContrAlterBillVO;
import nc.vo.pcm.contractalter.ContrAlterBodyVO;
import nc.vo.pcm.contractalter.ContrAlterHeadVO;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

//预算调整单
@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
public class ProjectBudgetAddImpl extends BillBaseImpl<BudgetAddBillVO>
		implements IProjectBudgetAdd {
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表明

	public ProjectBudgetAddImpl() {
	}

	public BudgetAddBillVO[] insertBudgetAdd(BudgetAddBillVO[] billVOs)
			throws BusinessException {
		return (BudgetAddBillVO[]) insert(billVOs);
	}

	protected void initInsertAction(InsertAction<BudgetAddBillVO> action) {
		super.initInsertAction(action);
		action.addBeforeRule(new InsertBudgetAddValidate());
		action.addBeforeRule(new CheckMnyByIncomeExpnesesOfFactor("pk_factor",
				"add_this_mny", "add_th_in", "4D20", BudgetAddBodyVO.class));
		action.addBeforeRule(new AddAuditInfoRule());
		action.addBeforeRule(new BudgetAddDateRule());
		action.addBeforeRule(new SetBizScopeInfoRule());
		action.addAfterRule(new BudgetAddFlagUpdateYAfterRule());
	}

	public BudgetAddBillVO[] updateBudgetAdd(BudgetAddBillVO[] billVOs,
			BudgetAddBillVO[] originBillVOs) throws BusinessException {
		return (BudgetAddBillVO[]) update(billVOs, originBillVOs);
	}

	protected void initUpdateAction(UpdateAction<BudgetAddBillVO> action) {
		super.initUpdateAction(action);
		action.addBeforeRule(new InsertBudgetAddValidate());
		action.addBeforeRule(new CheckMnyByIncomeExpnesesOfFactor("pk_factor",
				"add_this_mny", "add_th_in", "4D20", BudgetAddBodyVO.class));
		action.addBeforeRule(new BudgetAddDateRule());
	}

	public BudgetAddBillVO[] deleteBudgetAdd(BudgetAddBillVO[] billVOs)
			throws BusinessException {
		return (BudgetAddBillVO[]) delete(billVOs);
	}

	protected void initDeleteAction(DeleteAction<BudgetAddBillVO> action) {
		super.initDeleteAction(action);
		action.addAfterRule(new BudgetAddFlagUpdateNAfterRule());
	}

	public Object approveBudgetAdd(BudgetAddBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		return approve(billVOs, pfParamVO);
	}

	protected void initApproveAction(ApproveAction<BudgetAddBillVO> action) {
		super.initApproveAction(action);
		action.addBeforeRule(new CheckDateTimeRule());
		action.addBeforeRule(new ApprovedBudgetAddBefRule());
		action.addAfterRule(new UpdateBudgetAddApproveVersionRule());
		action.addAfterRule(new ApproveBudgetAddRule("approve"));
	}

	public BudgetAddBillVO[] unapproveBudgetAdd(BudgetAddBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		return (BudgetAddBillVO[]) unApprove(billVOs, pfParamVO);
	}

	protected void initUnApproveAction(UnApproveAction<BudgetAddBillVO> action) {
		super.initUnApproveAction(action);
		action.addAfterRule(new ApproveBudgetAddRule("unapprove"));
	}

	public BudgetAddBillVO[] commitBudgetAdd(BudgetAddBillVO[] billVOs)
			throws BusinessException {
		BudgetAddBillVO[] aggVO = (BudgetAddBillVO[]) commit(billVOs);
		sendOaData(aggVO);
		return aggVO;
	}

	private String billType = "4D20";

	private void sendOaData(BudgetAddBillVO[] billVOs) throws BusinessException {
		for (BudgetAddBillVO temp : billVOs) {
			if ((temp.getParentVO().getTransi_type()).contains("4D20-01")) {
				// 新增针对港华
				if ("4".equals(getDef2(temp.getParentVO().getPk_org()))) {
					BudgetAddHeadVO hVO = temp.getParentVO();
					OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(
							hVO.getPk_org(), "4D20");
					if (oaVo != null && oaVo.getIsdr() == 0) {
						WorkFId = oaVo.getFlowid();
						TableName = oaVo.getTablename();
						// 获取主表数据
						JSONArray headData = getMainMapNew(hVO);
						// 获取子表数据
						JSONArray bodyData = getDtaileDataMapNew(temp);
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
						workFlowVO.setWorkflowName("预算调整");
						WorkFlowBill bill = OaWorkFlowUtil
								.getWorkFlowBill(workFlowVO);
						// 调用OA工具类同步数据至OA
						bill.setDef3("ZT");
						// 制单人身份证号
						UserVO userVO = (UserVO) getHyPubBO()
								.queryByPrimaryKey(UserVO.class,
										temp.getParentVO().getBillmaker());
						String idCard = (String) getHyPubBO().findColValue(
								"bd_psndoc",
								"id",
								"nvl(dr,0) = 0 and pk_psndoc='"
										+ userVO.getPk_psndoc() + "'");
						bill.setDef5(idCard);
						OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
					}
				} else {
					String def2 = getDef2(temp.getParentVO().getPk_org());
					if (def2 != null && ("1".equals(def2) || "2".equals(def2))) {
						JSONArray headData = null;
						JSONArray bodyData = null;
						headData = getMainMap(temp.getParentVO());
						// bodyData = getDtaileDataMap(temp);
						OaWorkFlowUtil.sendOaData(headData, bodyData,
								getWorkFlowBill(temp));
					}
				}
			}
		}
	}

	private JSONArray getDtaileDataMap(BudgetAddBillVO aggVO)
			throws BusinessException {
		Map ctPuBMap = getBudgetAddBillBody(aggVO);
		JSONArray dtlistString = JSONArray.fromObject(ctPuBMap);
		return dtlistString;
	}

	// 项目预算调整单子表数据
	private Map getBudgetAddBillBody(BudgetAddBillVO aggVO)
			throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName",  "formtable_main_163_dt1");
		List workflowRequestTableRecords = new ArrayList();
		BudgetAddBodyVO[] bvos = aggVO.getBudgetAddBodyVO();
		for (BudgetAddBodyVO temp : bvos) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();
			/*
			 * Map xh = OaWorkFlowUtil.listAdd("xh", temp.getRowno());
			 * workflowRequestTableFields.add(xh); String ysbzxhstr = (String)
			 * getHyPubBO().findColValue( "pm_budgetfactor", "rowno",
			 * "nvl(dr,0) = 0 and pk_budgetfactor ='" +
			 * temp.getPk_budgetfactor() + "'"); Map ysbzxh =
			 * OaWorkFlowUtil.listAdd("ysbzxh", ysbzxhstr);
			 * workflowRequestTableFields.add(ysbzxh);
			 */
			// CBS编码
			String cbscode = (String) getHyPubBO().findColValue(
					"bd_cbsnode",
					"code",
					"nvl(dr,0) = 0 and pk_cbsnode  = '" + temp.getPk_cbs_node()
							+ "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("cbsbm",
					cbscode));
			// CBS名称
			String cbsName = (String) getHyPubBO().findColValue(
					"bd_cbsnode",
					"name",
					"nvl(dr,0) = 0 and pk_cbsnode  = '" + temp.getPk_cbs_node()
							+ "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("cbsmc",
					cbsName));
			// UserVO userVO = (UserVO)
			// getHyPubBO().queryByPrimaryKey(UserVO.class,
			// temp.getBillmaker());
			// if(null !=userVO){
			//
			// Map zdrmc =OaWorkFlowUtil.listAdd("zdrmc",userVO.getUser_name()
			// );
			// list.add(zdrmc);
			// }
			// 核算要素
			String hsysmc = (String) getHyPubBO().findColValue(
					"resa_factorasoa",
					"factorname",
					"nvl(dr,0) = 0 and pk_factorasoa = '" + temp.getPk_factor()
							+ "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("hsys",
					hsysmc));

			workflowRequestTableFields.add(hsysmc);
			String pk_factor = (String) getHyPubBO().findColValue(
					"resa_factorasoa",
					"pk_factor ",
					"nvl(dr,0) = 0 and pk_factorasoa   ='"
							+ temp.getPk_factor() + "'");
			String incomeexpenses = (String) getHyPubBO().findColValue(
					"resa_factor", "incomeexpenses",
					"nvl(dr,0) = 0 and   pk_factor   ='" + pk_factor + "'");
			Map szzx = OaWorkFlowUtil.listAdd("szzx", incomeexpenses);
			workflowRequestTableFields.add(szzx);
			Map ysje = OaWorkFlowUtil.listAdd("ysje", temp.getCurr_this_mny()
					.setScale(2, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(ysje);
			Map ysye = OaWorkFlowUtil.listAdd("ysye", temp.getBal_this_mny()
					.setScale(2, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(ysye);
			Map dzje = OaWorkFlowUtil.listAdd("dzje", temp.getAdd_this_mny()
					.setScale(2, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(dzje);
			Map dzhysye = OaWorkFlowUtil.listAdd("dzhysye", temp
					.getAdd_bal_mny().setScale(2, UFDouble.ROUND_HALF_UP)
					.toString());
			workflowRequestTableFields.add(dzhysye);
			Map dzhysje = OaWorkFlowUtil.listAdd("dzhysje", temp
					.getAdd_gather_mny().setScale(2, UFDouble.ROUND_HALF_UP)
					.toString());
			workflowRequestTableFields.add(dzhysje);
			Map zyysdz = OaWorkFlowUtil.listAdd("zyysdz", temp.getRes_mny()
					.setScale(2, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(zyysdz);
			Map srys = OaWorkFlowUtil.listAdd("srys", temp.getCurr_th_in()
					.setScale(2, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(srys);
			Map srce = OaWorkFlowUtil.listAdd("srce", temp.getBal_th_in()
					.setScale(2, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(srce);
			Map srdz = OaWorkFlowUtil.listAdd("srdz", temp.getAdd_th_in()
					.setScale(2, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(srdz);
			Map dzhsrys = OaWorkFlowUtil.listAdd("dzhsrys", temp.getAdd_ga_in()
					.setScale(2, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(dzhsrys);
			Map fdbfb = OaWorkFlowUtil.listAdd("fdbfb", temp.getFloat_per()
					.setScale(2, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(fdbfb);
			Map fdje = OaWorkFlowUtil.listAdd("fdje", temp.getFloat_mny()
					.setScale(2, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(fdje);
			Map bz = OaWorkFlowUtil.listAdd("bz", temp.getMemo());
			workflowRequestTableFields.add(bz);
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	// 项目预算调整单主表数据
	private JSONArray getMainMap(BudgetAddHeadVO parentVO)
			throws BusinessException {
		List list = new ArrayList();
		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				parentVO.getPk_org());
		if (null != orgVO) {
			Map szgs = OaWorkFlowUtil.listAdd("szgs", orgVO.getCode());
			list.add(szgs);
		}
		Map djzj = OaWorkFlowUtil.listAdd("djzj", parentVO.getPk_budgetadd());
		list.add(djzj);
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		if (null != userVO) {
			Map zdrzj = OaWorkFlowUtil.listAdd("zdrzj", userVO.getUser_code());
			list.add(zdrzj);
			Map zdrmc = OaWorkFlowUtil.listAdd("zdrmc", userVO.getUser_name());
			list.add(zdrmc);
		}
		Map zdrq = OaWorkFlowUtil.listAdd("zdrq", parentVO.getBillmaketime()
				.getYear()
				+ "-"
				+ parentVO.getBillmaketime().getStrMonth()
				+ "-" + parentVO.getBillmaketime().getStrDay());
		list.add(zdrq);
		Map djh = OaWorkFlowUtil.listAdd("djh", parentVO.getBill_code());
		list.add(djh);
		String project_code = (String) getHyPubBO().findColValue(
				"bd_project",
				"project_code",
				"nvl(dr,0) = 0 and   pk_project  ='" + parentVO.getPk_project()
						+ "'");
		Map xmbm = OaWorkFlowUtil.listAdd("xmbm", project_code);
		list.add(xmbm);
		String project_name = (String) getHyPubBO().findColValue(
				"bd_project",
				"project_name",
				"nvl(dr,0) = 0 and   pk_project  ='" + parentVO.getPk_project()
						+ "'");
		Map xmmc = OaWorkFlowUtil.listAdd("xmmc", project_name);
		list.add(xmmc);
		Map dzbmzj = OaWorkFlowUtil.listAdd("dzbmzj", parentVO.getPk_adddept());
		list.add(dzbmzj);
		String pk_adddept_name = (String) getHyPubBO().findColValue(
				"org_dept",
				"name",
				"nvl(dr,0) = 0 and    pk_dept  ='" + parentVO.getPk_adddept()
						+ "'");
		Map dzbmmc = OaWorkFlowUtil.listAdd("dzbmmc", pk_adddept_name);
		list.add(dzbmmc);
		Map dzrzj = OaWorkFlowUtil.listAdd("dzrzj", parentVO.getPk_adder());
		list.add(dzrzj);
		String pk_adder_name = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc  ='" + parentVO.getPk_adder()
						+ "'");
		Map dzrmc = OaWorkFlowUtil.listAdd("dzrmc", pk_adder_name);
		list.add(dzrmc);
		Map ysdzrq = OaWorkFlowUtil.listAdd("ysdzrq", parentVO.getAdd_date()
				.getYear()
				+ "-"
				+ parentVO.getAdd_date().getStrMonth()
				+ "-"
				+ parentVO.getAdd_date().getStrDay());
		list.add(ysdzrq);
		Map yszje = OaWorkFlowUtil.listAdd("yszje", parentVO.getBudget_mny()
				.setScale(2, UFDouble.ROUND_HALF_UP).toString());
		list.add(yszje);
		Map dzje = OaWorkFlowUtil.listAdd("dzje", parentVO.getAdd_to_mny()
				.setScale(2, UFDouble.ROUND_HALF_UP).toString());
		list.add(dzje);
		Map dzhje = OaWorkFlowUtil.listAdd("dzhje", parentVO.getAddafter_mny()
				.setScale(2, UFDouble.ROUND_HALF_UP).toString());
		list.add(dzhje);
		Map bz = OaWorkFlowUtil.listAdd("bz", parentVO.getMemo());
		list.add(bz);
		Map fgldzj = OaWorkFlowUtil.listAdd("fgldzj", parentVO.getHdef2());
		list.add(fgldzj);
		if (null != parentVO.getHdef2()) {
			String where = " pk_defdoc = '"
					+ parentVO.getHdef2()
					+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'fgldmc' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
			String def2 = "";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					where)) {
				def2 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where);
			}
			Map fgldmc = OaWorkFlowUtil.listAdd("fgldmc", def2);
			list.add(fgldmc);
		}
		Map xmlx = OaWorkFlowUtil.listAdd("xmlx", parentVO.getHdef3());
		list.add(xmlx);
		String pk_budget = parentVO.getPk_budget();
		String where = " pk_budget = '" + parentVO.getPk_budget()
				+ "' and nvl(dr,0) = 0";
		BudgetHeadVO budgetHeadVO = (BudgetHeadVO) getHyPubBO()
				.queryByPrimaryKey(BudgetHeadVO.class, parentVO.getPk_budget());
		int kzf = budgetHeadVO.getCtrl_model();
		String kzfstr = "金额控制";
		if (0 == kzf) {
			kzfstr = "金额控制";
		} else if (1 == kzf) {
			kzfstr = "百分比控制";
		} else if (2 == kzf) {
			kzfstr = "金额与百分比";
		} else if (3 == kzf) {
			kzfstr = "金额或百分比";
		}
		Map kzfs = OaWorkFlowUtil.listAdd("kzfs", kzfstr);
		list.add(kzfs);
		if (null != parentVO.getHdef1()) {
			String sfsdlsgstr = " pk_defdoc = '"
					+ parentVO.getHdef1()
					+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'sf' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
			String hdef1 = "";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					sfsdlsgstr)) {
				hdef1 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						sfsdlsgstr);
			}
			Map sfsdlsg = OaWorkFlowUtil.listAdd("sfsdlsg", hdef1);
			list.add(sfsdlsg);
		}
		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	private WorkFlowBill getWorkFlowBill(BudgetAddBillVO temp)
			throws BusinessException {
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
			workFlowBill.setWorkflowName("预算调整单");
		} else {
			UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
					UserVO.class, temp.getParentVO().getBillmaker());
			workFlowBill.setWorkflowId("29");
			workFlowBill.setDef5(userVO.getUser_code());
			if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
				PsndocVO psndoc = (PsndocVO) getHyPubBO().queryByPrimaryKey(
						PsndocVO.class, userVO.getPk_psndoc());
				workFlowBill.setDef5(psndoc.getId());
				workFlowBill.setWorkflowId("0");
			}
			workFlowBill.setPk_group(temp.getParentVO().getPk_group());
			workFlowBill.setPk_org(temp.getParentVO().getPk_org());
			workFlowBill.setPk_bill(temp.getPrimaryKey());
			workFlowBill.setUser_name(userVO.getUser_name());
			workFlowBill.setUser_code(userVO.getUser_code());
			workFlowBill.setBill_status("1");
			workFlowBill.setCreationtime(new UFDate().toStdString());
			workFlowBill.setCreator(temp.getParentVO().getCreator());
			workFlowBill.setBill_code("4D20");
			workFlowBill.setWorkflowName("预算调整单");
		}
		return workFlowBill;
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

	private JSONArray getMainMapNew(BudgetAddHeadVO parentVO)
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
		// 订单类型名称
		String billTypeName = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and pk_billtypeid  = '"
						+ parentVO.getBill_type() + "'");
		list.add(OaWorkFlowUtil.listAddObj("billTypeName", billTypeName));
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
		// 预算调整人
		String apppsnhName = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc = '" + parentVO.getPk_adder()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("ystzr", apppsnhName));
		// 预算调整部门
		String sqbmName = (String) getHyPubBO().findColValue(
				"org_dept_v",
				"name",
				"nvl(dr,0) = 0 and pk_vid  = '" + parentVO.getPk_adddept_v()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("ystzbm", sqbmName));
		
		if (null != parentVO.getPk_project()) {
			// 项目
			ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
					.queryByPrimaryKey(ProjectHeadVO.class,
							parentVO.getPk_project());
			// 项目编码
			list.add(OaWorkFlowUtil.listAddObj("project_code",
					projectVO.getProject_code()));
			// 项目名称
			list.add(OaWorkFlowUtil.listAddObj("project_name",
					projectVO.getProject_name()));
		}
		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	private JSONArray getDtaileDataMapNew(BudgetAddBillVO temp)
			throws BusinessException {
		// 获取详细信息
		BudgetAddBodyVO[] bvos = (BudgetAddBodyVO[]) temp.getChildrenVO();
		Map bodyMap = getBody(bvos);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
		return dtlistString;
	}

	private Map getBody(BudgetAddBodyVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (BudgetAddBodyVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			// CBS
			String cbscode = (String) getHyPubBO().findColValue(
					"bd_cbsnode",
					"code",
					"nvl(dr,0) = 0 and pk_cbsnode = '" + temp.getPk_cbs_node()
							+ "'");
			// CBS名称
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("cbsbm",
					cbscode));
			String cbsName = (String) getHyPubBO().findColValue(
					"bd_cbsnode",
					"name",
					"nvl(dr,0) = 0 and pk_cbsnode = '" + temp.getPk_cbs_node()
							+ "'");
			// CBS名称
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("cbsmc",
					cbsName));
			// 核算要素
			String hsysmc = (String) getHyPubBO().findColValue(
					"resa_factorasoa",
					"factorname",
					"nvl(dr,0) = 0 and pk_factorasoa = '" + temp.getPk_factor()
							+ "'");
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

	public BudgetAddBillVO[] unCommitBudgetAdd(BudgetAddBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		BudgetAddBillVO[] aggVO = (BudgetAddBillVO[]) unCommit(billVOs,
				pfParamVO);
		unOaCommit(aggVO);
		return aggVO;
	}

	private void unOaCommit(BudgetAddBillVO[] aggVO) throws BusinessException {
		for (BudgetAddBillVO temp : aggVO) {
			if ((temp.getParentVO().getTransi_type()).contains("4D20-01")) {
				OaWorkFlowUtil.backOaWorkFlow(temp.getPrimaryKey());
			}
		}
	}
}
