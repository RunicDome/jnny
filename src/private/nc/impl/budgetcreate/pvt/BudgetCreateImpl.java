package nc.impl.budgetcreate.pvt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.pbm.budget.action.BudgetCreateReleasedAction;
import nc.bs.pbm.budget.pubrule.CheckMnyByIncomeExpnesesOfFactor;
import nc.bs.pbm.budget.rule.CBSBodyRepeatValidateRule;
import nc.bs.pbm.budget.rule.CleanExeAndPreMnyRule;
import nc.bs.pbm.budget.rule.ClearDataOfBudgetDetailRule;
import nc.bs.pbm.budget.rule.CompBudgetMnyAndExeMnyRule;
import nc.bs.pbm.budget.rule.CompBudgetMnyAndFloatMnyRule;
import nc.bs.pbm.budget.rule.CompBudgetMnyAndZoreRule;
import nc.bs.pbm.budget.rule.CompCBSForTabsRule;
import nc.bs.pbm.budget.rule.CompHeadAndRootCBS4FloatRule;
import nc.bs.pbm.budget.rule.CreateOldVersionRule;
import nc.bs.pbm.budget.rule.DeleteHisVersionRule;
import nc.bs.pbm.budget.rule.FactorBodyRepeatValidateRule;
import nc.bs.pbm.budget.rule.FactorNotNullValidateRule;
import nc.bs.pbm.budget.rule.FillHeadBudgetRule;
import nc.bs.pbm.budget.rule.InitMutiCurrTypeBeforeRule;
import nc.bs.pbm.budget.rule.IsFullCBSTreeValidateRule;
import nc.bs.pbm.budget.rule.IsLeafCBSValidateRule;
import nc.bs.pbm.budget.rule.ProjectClosedValidateRule;
import nc.bs.pbm.budget.rule.UnReleaseValidateRule;
import nc.bs.pbm.budget.rule.UpdateBudget4ReleasedBudgetAfterRule;
import nc.bs.pbm.budget.sum.rule.CalBalaMnyBodyRule;
import nc.bs.pbm.budget.sum.rule.CompSumCBSAndUpNodeRule;
import nc.bs.pbm.budget.sum.rule.CompSumFactorAndLeafCBSRule;
import nc.bs.pbm.budget.sum.rule.SumCBSToUpNodeRule;
import nc.bs.pbm.budget.sum.rule.SumFactorBodyToLeafCBSRule;
import nc.bs.pmpub.rule.SetAddStatusBodyOrgRule;
import nc.bs.pmpub.util.VoStatusUtils;
import nc.bs.trade.business.HYPubBO;
import nc.impl.budgetcreate.pub.BudgetResourceUtil;
import nc.impl.pm.billrule.OnlyOneBillForConditionRule;
import nc.impl.pm.billrule.ProjectStatusCheckBeforeRule;
import nc.impl.pm.billrule.SetBizScopeInfoRule;
import nc.impl.pm.billrule.SupplyProjectInfoBeforeRule;
import nc.impl.pm.billrule.SupplyStatusByTransiRuleBeforeRule;
import nc.impl.pm.billrule.UpdateBillSatusRule;
import nc.impl.pm.billrule.WBSPretaskCompleteCheckRule;
import nc.impl.pm.billrule.WriteBackToProjectBeforeRule;
import nc.impl.pmpub.servicebase.BillBaseImpl;
import nc.impl.pmpub.servicebase.action.ApproveAction;
import nc.impl.pmpub.servicebase.action.CommitAction;
import nc.impl.pmpub.servicebase.action.DeleteAction;
import nc.impl.pmpub.servicebase.action.InsertAction;
import nc.impl.pmpub.servicebase.action.UnApproveAction;
import nc.impl.pmpub.servicebase.action.UpdateAction;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.itf.budget.IMaterialPlanForBudgetService;
import nc.itf.pbm.budget.pvt.IBudgetCreate;
import nc.itf.pbm.materialplan.pvt.IMaterialPlanCommonService;
import nc.vo.bd.cbs.CBSNodeVO;
import nc.vo.bd.material.MaterialVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.org.OrgVO;
import nc.vo.pbm.budget.BudgetBillVO;
import nc.vo.pbm.budget.BudgetCBSBodyVO;
import nc.vo.pbm.budget.BudgetFactorBodyVO;
import nc.vo.pbm.budget.BudgetHeadVO;
import nc.vo.pbm.budget.util.FillCBSBodyUtil;
import nc.vo.pbm.budget.util.PublicBudgetUtil;
import nc.vo.pbm.budgetexe.BudgetExeBodyVO;
import nc.vo.pbm.materialplan.MaterialPlanBillVO;
import nc.vo.pbm.materialplan.MaterialPlanBodyVO;
import nc.vo.pim.project.ProjectHeadVO;
import nc.vo.pm.proxy.PMProxy;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.resa.factor.FactorAsoaVO;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

//项目预算
@SuppressWarnings({ "unchecked", "rawtypes" })
public class BudgetCreateImpl extends BillBaseImpl<BudgetBillVO> implements
		IBudgetCreate {
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表名

	public BudgetCreateImpl() {
	}

	public BudgetBillVO[] insertBudget(BudgetBillVO[] billVOs)
			throws BusinessException {
		for(int i = 0;i< billVOs.length;i++){
			
			BudgetBillVO billVO = billVOs[i];
			BudgetHeadVO hvo = billVO.getParentVO();
			if(hvo.getHdef32() == null){
				UFDouble in_budget = new UFDouble(0);// 无税收入预算
				UFDouble budget_mny = new UFDouble(0);// 无税成本预算
				BudgetFactorBodyVO[] bvos = billVO.getBudgetFactorBodyVO();
				for(BudgetFactorBodyVO bvo : bvos){
					in_budget = in_budget.add(bvo.getIn_budget());
					budget_mny = budget_mny.add(bvo.getBudget_mny());
				}
				UFDouble bzml = in_budget.sub(budget_mny);// 报装毛利
				hvo.setHdef32(bzml.setScale(2, UFDouble.ROUND_HALF_UP).toString());
				UFDouble bzmll = new UFDouble(0);// 报装毛利率
				if(in_budget.getDouble() != 0){
					bzmll = (in_budget.sub(budget_mny)).div(in_budget);// 报装毛利率
				}
				hvo.setHdef33(bzmll.setScale(4, UFDouble.ROUND_HALF_UP).toString());
				if(hvo.getHdef32() != null){
					billVO.setParentVO(hvo);
					billVOs[i] = billVO;
				}
			}
		}
		return (BudgetBillVO[]) insert(billVOs);
	}

	protected void initInsertAction(InsertAction<BudgetBillVO> action) {
		super.initInsertAction(action);

		action.addBeforeRule(new OnlyOneBillForConditionRule(
				new String[] { "pk_project" }, NCLangRes4VoTransl
						.getNCLangRes().getStrByID("projectbudget_0",
								"04815005-0051")));

		action.addBeforeRule(new CBSBodyRepeatValidateRule());

		action.addBeforeRule(new FactorBodyRepeatValidateRule());

		action.addBeforeRule(new SumFactorBodyToLeafCBSRule());

		action.addBeforeRule(new SumCBSToUpNodeRule());

		action.addBeforeRule(new CompBudgetMnyAndZoreRule());

		action.addBeforeRule(new CheckMnyByIncomeExpnesesOfFactor("pk_factor",
				"budget_mny", "in_budget", "4D18", BudgetFactorBodyVO.class));

		action.addBeforeRule(new FillHeadBudgetRule());

		action.addBeforeRule(new InitMutiCurrTypeBeforeRule());

		action.addBeforeRule(new SetBizScopeInfoRule(null, new Class[] {
				BudgetCBSBodyVO.class, BudgetFactorBodyVO.class }));

		action.addBeforeRule(new IsFullCBSTreeValidateRule());
		action.addBeforeRule(new IsLeafCBSValidateRule());
	}

	public BudgetBillVO[] updateBudget(BudgetBillVO[] billVOs,
			BudgetBillVO[] originBillVOs) throws BusinessException {
		for(int i = 0;i< billVOs.length;i++){
			BudgetBillVO billVO = billVOs[i];
			BudgetHeadVO hvo = billVO.getParentVO();
			if(hvo.getHdef32() == null){
				UFDouble in_budget = new UFDouble(0);// 无税收入预算
				UFDouble budget_mny = new UFDouble(0);// 无税收入预算
				BudgetFactorBodyVO[] bvos = billVO.getBudgetFactorBodyVO();
				for(BudgetFactorBodyVO bvo : bvos){
					if(bvo.getDr() == 0){
						in_budget = in_budget.add(bvo.getIn_budget());
						budget_mny = budget_mny.add(bvo.getBudget_mny());
					}
				}
				UFDouble bzml = in_budget.sub(budget_mny);// 报装毛利
				hvo.setHdef32(bzml.setScale(2, UFDouble.ROUND_HALF_UP).toString());
				UFDouble bzmll = new UFDouble(0);// 报装毛利率
				if(in_budget.getDouble() != 0){
					bzmll = (in_budget.sub(budget_mny)).div(in_budget);// 报装毛利率
				}
				hvo.setHdef33(bzmll.setScale(4, UFDouble.ROUND_HALF_UP).toString());
				if(hvo.getHdef32() != null){
					billVO.setParentVO(hvo);
					billVOs[i] = billVO;
					billVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
				}
			}
		}
		return (BudgetBillVO[]) update(billVOs, originBillVOs);
	}

	protected void initUpdateAction(UpdateAction<BudgetBillVO> action) {
		super.initUpdateAction(action);

		action.addBeforeRule(new OnlyOneBillForConditionRule(
				new String[] { "pk_project" }, "last_v_flag",
				NCLangRes4VoTransl.getNCLangRes().getStrByID("projectbudget_0",
						"04815005-0051")));

		action.addBeforeRule(new CBSBodyRepeatValidateRule());

		action.addBeforeRule(new FactorBodyRepeatValidateRule());

		action.addBeforeRule(new ProjectStatusCheckBeforeRule());

		action.addBeforeRule(new SumFactorBodyToLeafCBSRule());

		action.addBeforeRule(new SumCBSToUpNodeRule());

		action.addBeforeRule(new CompBudgetMnyAndZoreRule());

		action.addBeforeRule(new CheckMnyByIncomeExpnesesOfFactor("pk_factor",
				"budget_mny", "in_budget", "4D18", BudgetFactorBodyVO.class));

		action.addBeforeRule(new CalBalaMnyBodyRule());

		action.addBeforeRule(new FillHeadBudgetRule());

		action.addBeforeRule(new InitMutiCurrTypeBeforeRule());

		action.addBeforeRule(new SetBizScopeInfoRule(null, new Class[] {
				BudgetCBSBodyVO.class, BudgetFactorBodyVO.class }));

		action.addBeforeRule(new IsFullCBSTreeValidateRule());
		action.addBeforeRule(new IsLeafCBSValidateRule());
	}

	public BudgetBillVO[] deleteBudget(BudgetBillVO[] billVOs)
			throws BusinessException {
		return (BudgetBillVO[]) delete(billVOs);
	}

	protected void initDeleteAction(DeleteAction<BudgetBillVO> action) {
		super.initDeleteAction(action);
		action.addAfterRule(new DeleteHisVersionRule());
	}

	public Object approveBudget(BudgetBillVO[] billVOs, PfParameterVO pfParamVO)
			throws BusinessException {
		for (int i = 0; (billVOs != null) && (i < billVOs.length); i++) {
			billVOs[i].getParentVO().setStatus(1);
		}
		return approve(billVOs, pfParamVO);
	}

	protected void initApproveAction(ApproveAction<BudgetBillVO> action) {
		super.initApproveAction(action);

		action.addBeforeRule(new ProjectStatusCheckBeforeRule());

		IRule<BudgetBillVO> supplyProjectInfoBeforeRule = new SupplyProjectInfoBeforeRule();
		action.addAfterRule(supplyProjectInfoBeforeRule);

		IRule<BudgetBillVO> rule2 = new SupplyStatusByTransiRuleBeforeRule();
		action.addAfterRule(rule2);

		IRule<BudgetBillVO> rule3 = new WriteBackToProjectBeforeRule(true);

		action.addAfterRule(rule3);
	}

	public BudgetBillVO[] unapproveBudget(BudgetBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		for (int i = 0; (billVOs != null) && (i < billVOs.length); i++) {
			billVOs[i].getParentVO().setStatus(1);
		}
		return (BudgetBillVO[]) unApprove(billVOs, pfParamVO);
	}

	protected void initUnApproveAction(UnApproveAction<BudgetBillVO> action) {
		super.initUnApproveAction(action);

		IRule<BudgetBillVO> writeBackToProject = new WriteBackToProjectBeforeRule(
				false);

		action.addBeforeRule(writeBackToProject);
	}

	public BudgetBillVO[] commitBudget(BudgetBillVO[] billVOs)
			throws BusinessException {
		BudgetBillVO[] nvos = (BudgetBillVO[]) commit(billVOs);
		senOaData(billVOs);
		return nvos;
	}

	// TODO 单据同步OA功能
	private HYPubBO hyPubBO;

	public HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	private void senOaData(BudgetBillVO[] billVOs) throws BusinessException {
		String billType = "4D18";
		for (BudgetBillVO temp : billVOs) {
			// 项目预算
			BudgetHeadVO hVO = temp.getParentVO();
			if ("4".equals(getDef2(hVO.getPk_org()))
					&& hVO.getBill_type().contains(billType)) {
				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hVO.getPk_org(),
						"4D18");
				if (oaVo != null && oaVo.getIsdr() == 0) {
					WorkFId = oaVo.getFlowid();
					TableName = oaVo.getTablename();
					// 获取主表数据
					JSONArray headData = getMainMap(temp.getParentVO(),
							temp.getBudgetCBSBodyVO(),
							temp.getBudgetFactorBodyVO());
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
					// 临时写死，后期改为接口获取
					workFlowVO.setWorkflowId(OaWorkFlowUtil
							.getOAFlowID(WorkFId));
					workFlowVO.setWorkflowName("项目预算");
					WorkFlowBill bill = OaWorkFlowUtil
							.getWorkFlowBill(workFlowVO);
					// WorkFlowBill的自定义项3 判断是否港华单据
					bill.setDef3("ZT");
					bill.setDef4(temp.getParentVO().getBill_code());
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

	private JSONArray getMainMap(BudgetHeadVO parentVO,
			BudgetCBSBodyVO[] budgetCBSBodyVOs,
			BudgetFactorBodyVO[] budgetFactorBodyVOs) throws BusinessException {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// ---------其他字段begin
		// 库存组织
		String stockName = (String) getHyPubBO()
				.findColValue(
						"org_itemorg",
						"name",
						"nvl(dr,0) = 0 and pk_itemorg = '"
								+ parentVO.getPk_org() + "'");
		list.add(OaWorkFlowUtil.listAddObj("zzmc", stockName));
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		/*
		 * // 订单类型名称 String billTypeName = (String) getHyPubBO().findColValue(
		 * "bd_billtype", "billtypename", "nvl(dr,0) = 0 and pk_billtypeid  = '"
		 * + parentVO.getBill_type() + "'");
		 * list.add(OaWorkFlowUtil.listAddObj("billTypeName", billTypeName));
		 */
		// 预算控制
		String yskzmc = (String) getHyPubBO().findColValue(
				"pmbd_budgetctrl",
				"ctrl_name",
				"nvl(dr,0) = 0 and pk_costbgtctrl = '"
						+ parentVO.getPk_costbgtctrl() + "'");
		list.add(OaWorkFlowUtil.listAddObj("yskz", yskzmc));
		String xmtype = "";// 项目类型
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
			// 项目预算打印模板
			// 地点
			list.add(OaWorkFlowUtil.listAddObj("dd", projectVO.getHdef53()));
			// 计划开始日期
			list.add(OaWorkFlowUtil.listAddObj("jhksrq",
					projectVO.getPlan_start_date() + ""));
			// 计划完成日期
			list.add(OaWorkFlowUtil.listAddObj("jhwcrq",
					projectVO.getPlan_finish_date() + ""));
			// 项目类型
			xmtype = (String) getHyPubBO().findColValue(
					"bd_projectclass",
					"type_name",
					"nvl(dr,0) = 0 and pk_projectclass = '"
							+ projectVO.getPk_projectclass() + "'");
			list.add(OaWorkFlowUtil.listAddObj("project_type", xmtype));
			// 工程类别
			if(projectVO.getDef18() != null){
				String gclbbm = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"code",
						"nvl(dr,0) = 0 and pk_defdoc = '"
								+ projectVO.getDef18() + "'");
				list.add(OaWorkFlowUtil.listAddObj("gclbbm", gclbbm));
				String gclb = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '"
								+ projectVO.getDef18() + "'");
				list.add(OaWorkFlowUtil.listAddObj("gclb", gclb));
			}
			// 管材长度
			// 物资及服务需求单VO
			MaterialPlanBillVO wzvo = ((IMaterialPlanCommonService) NCLocator
					.getInstance().lookup(IMaterialPlanCommonService.class))
					.queryMaterialPlanBillVOByPkProjectForBudget(parentVO
							.getPk_project());
			if (wzvo != null) {
				MaterialPlanBodyVO[] wzbodyvos = wzvo.getChildrenVO();
				double glflsl = 0;
				if (wzbodyvos != null && wzbodyvos.length > 0) {
					for (int i = 0; i < wzbodyvos.length; i++) {
						MaterialPlanBodyVO itemvo = wzbodyvos[i];
						MaterialVO wlvo = (MaterialVO) getHyPubBO()
								.queryByPrimaryKey(MaterialVO.class,
										itemvo.getPk_material());
						String wlflbm = (String) getHyPubBO().findColValue(
								"bd_marbasclass",
								"code",
								"nvl(dr,0) = 0 and pk_marbasclass = '"
										+ wlvo.getPk_marbasclass() + "'");
						if (wlflbm.indexOf("RQ12") != -1) {
							glflsl += 1;
							if (glflsl == 1) {
								String gg = wlvo.getMaterialspec();
								if (gg != null) {
									list.add(OaWorkFlowUtil
											.listAddObj("gg", gg));
								}
							}
						}
					}
				}
				double gcld = glflsl / 1000.0;// 管材长度
				list.add(OaWorkFlowUtil.listAddObj("gcld",
						new UFDouble(gcld).setScale(2, UFDouble.ROUND_HALF_UP)
								+ ""));
				UFDouble mgltzcb = UFDouble.ZERO_DBL;
				if (gcld != 0.0) {
					mgltzcb = parentVO.getIn_budget().div(gcld)
							.setScale(2, UFDouble.ROUND_HALF_UP);
				}
				list.add(OaWorkFlowUtil.listAddObj("mgltzcb",
						mgltzcb.setScale(2, UFDouble.ROUND_HALF_UP) + ""));
			}
		}
		// 状态
		list.add(OaWorkFlowUtil.listAddObj("zt", "发布"));

		// 预算编制部门
		String ysbzbm = (String) getHyPubBO().findColValue(
				"org_dept_v",
				"name",
				"nvl(dr,0) = 0 and pk_vid = '" + parentVO.getPk_makedept_v()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("ysbzbm", ysbzbm));
		// 预算编制人
		String ysbzr = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc = '" + parentVO.getPk_maker()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("ysbzr", ysbzr));
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
		// 控制方式
		int kzfsint = parentVO.getCtrl_model();
		String kzfs = "";
		if (kzfsint == 0) {
			kzfs = "金额控制";
		} else if (kzfsint == 0) {
			kzfs = "百分比控制";
		} else if (kzfsint == 2) {
			kzfs = "金额与百分比";
		} else if (kzfsint == 3) {
			kzfs = "金额或百分比";
		}
		list.add(OaWorkFlowUtil.listAddObj("kzfs", kzfs));
		list.add(OaWorkFlowUtil.listAddObj("ceazhs", parentVO.getHdef37()));
		// A表打印
		if ("民用类项目".equals(xmtype)) {
			list.add(OaWorkFlowUtil.listAddObj("jmhbzsr",
					parentVO.getIn_budget() + ""));// 居民户报装收入
		}
		for (int i = 0; i < budgetCBSBodyVOs.length; i++) {
			BudgetCBSBodyVO itemvo = budgetCBSBodyVOs[i];// CBS明细
			CBSNodeVO cbsvo = (CBSNodeVO) getHyPubBO().queryByPrimaryKey(
					CBSNodeVO.class, itemvo.getPk_cbsnode());
			// 项目类型为民用类项目 取数
			if ("民用类项目".equals(xmtype)) {
				if (cbsvo.getName().contains("人机费")) {
					if (itemvo.getBudget_mny() != null
							&& parentVO.getHdef37() != null) {
						UFDouble rjf = itemvo.getBudget_mny().div(
								Double.parseDouble(parentVO.getHdef37()));
						list.add(OaWorkFlowUtil.listAddObj("rjf", rjf + ""));// 人机费
						// 单位数目，也是 CEA总户数
						list.add(OaWorkFlowUtil.listAddObj("rjfdwsm",
								parentVO.getHdef37()));
						// 人机费金额不含税
						list.add(OaWorkFlowUtil.listAddObj("rjfjebhs",
								itemvo.getBudget_mny() + ""));
					}
				} else if (cbsvo.getName().contains("材料费")) {
					// CBS 报装工程-民用户项目-材料费
					if (itemvo.getBudget_mny() != null
							&& parentVO.getHdef37() != null) {
						UFDouble clf = itemvo.getBudget_mny().div(
								Double.parseDouble(parentVO.getHdef37()));
						list.add(OaWorkFlowUtil.listAddObj("clf", clf + ""));// 材料费
						// 单位数目，也是 CEA总户数
						list.add(OaWorkFlowUtil.listAddObj("clfdwsm",
								parentVO.getHdef37()));
						// 材料费金额不含税
						list.add(OaWorkFlowUtil.listAddObj("clfjebhs",
								itemvo.getBudget_mny() + ""));
					}
				} else if (cbsvo.getName().contains("其他")
						|| cbsvo.getName().contains("其它")) {
					// CBS 报装工程-民用户项目-其他（其它）
					if (itemvo.getBudget_mny() != null
							&& parentVO.getHdef37() != null) {
						UFDouble qt = itemvo.getBudget_mny().div(
								Double.parseDouble(parentVO.getHdef37()));
						list.add(OaWorkFlowUtil.listAddObj("qt", qt + ""));// 其他（其它）
						// 单位数目，也是 CEA总户数
						list.add(OaWorkFlowUtil.listAddObj("qtdwsm",
								parentVO.getHdef37()));
						// 其他（其它）金额不含税
						list.add(OaWorkFlowUtil.listAddObj("qtjebhs",
								itemvo.getBudget_mny() + ""));
					}
				}
			} else if ("工商类项目".equals(xmtype)) {
				// 项目类型为工商类项目 取数
				list.add(OaWorkFlowUtil.listAddObj("gsyhbbsr",
						parentVO.getIn_budget() + ""));// 工商用户报装收入
				if (cbsvo.getName().contains("人机费")) {
					if (itemvo.getBudget_mny() != null
							&& parentVO.getHdef37() != null) {
						UFDouble rjf = itemvo.getBudget_mny().div(
								Double.parseDouble(parentVO.getHdef37()));
						// 人机费
						list.add(OaWorkFlowUtil.listAddObj("gsrjf",
								itemvo.getBudget_mny() + ""));
					}
				} else if (cbsvo.getName().contains("材料费")) {
					// CBS 报装工程-民用户项目-材料费
					if (itemvo.getBudget_mny() != null
							&& parentVO.getHdef37() != null) {
						UFDouble clf = itemvo.getBudget_mny().div(
								Double.parseDouble(parentVO.getHdef37()));
						// 材料费
						list.add(OaWorkFlowUtil.listAddObj("gsclf",
								itemvo.getBudget_mny() + ""));
					}
				} else if (cbsvo.getName().contains("其他")) {
					// CBS 报装工程-民用户项目-其他
					if (itemvo.getBudget_mny() != null
							&& parentVO.getHdef37() != null) {
						UFDouble qt = itemvo.getBudget_mny().div(
								Double.parseDouble(parentVO.getHdef37()));
						// 其他
						list.add(OaWorkFlowUtil.listAddObj("gsqt",
								itemvo.getBudget_mny() + ""));
					}
				}
			}
		}
		list.add(OaWorkFlowUtil.listAddObj("yjzcbbhs", parentVO.getIn_budget()
				+ ""));// 预计总成本（不含税）
		list.add(OaWorkFlowUtil.listAddObj("yjzcbhs", parentVO.getHdef30()));// 预计总成本（含税）
		list.add(OaWorkFlowUtil.listAddObj("jmhjcb", parentVO.getHdef38()));// 居民户均成本
		list.add(OaWorkFlowUtil.listAddObj("yjnjyql", parentVO.getHdef34()));// 预计年均用气量M3
		list.add(OaWorkFlowUtil.listAddObj("yjmmml", parentVO.getHdef35()));// 预计每M3毛利
		list.add(OaWorkFlowUtil.listAddObj("yjmnzml", parentVO.getHdef36()));// 预计每年总毛利
		list.add(OaWorkFlowUtil.listAddObj("zsrbhs", parentVO.getIn_budget()
				+ ""));// 总收入（不含税）
		list.add(OaWorkFlowUtil.listAddObj("zsrhs", parentVO.getHdef31() + ""));// 总收入（含税）
		list.add(OaWorkFlowUtil.listAddObj("bzml", parentVO.getHdef32() + ""));// 报装毛利
		list.add(OaWorkFlowUtil.listAddObj("bzmll", parentVO.getHdef33() + ""));// 报装毛利率
		for (int n = 0; n < budgetFactorBodyVOs.length; n++) {
			BudgetFactorBodyVO itemvo = budgetFactorBodyVOs[n];// 核算要素明细
			FactorAsoaVO hsysvo = (FactorAsoaVO) getHyPubBO()
					.queryByPrimaryKey(FactorAsoaVO.class,
							itemvo.getPk_factor());
			if ("主材费".equals(hsysvo.getFactorname())) {
				list.add(OaWorkFlowUtil.listAddObj("zcf",
						itemvo.getBudget_mny() + ""));
			} else if ("施工费".equals(hsysvo.getFactorname())) {
				list.add(OaWorkFlowUtil.listAddObj("sgf",
						itemvo.getBudget_mny() + ""));
			} else if ("土建费".equals(hsysvo.getFactorname())) {
				list.add(OaWorkFlowUtil.listAddObj("tjf",
						itemvo.getBudget_mny() + ""));
			} else if ("安装费(不含土建)".equals(hsysvo.getFactorname())) {
				list.add(OaWorkFlowUtil.listAddObj("azfbhtj",
						itemvo.getBudget_mny() + ""));
			} else if ("安装费(含土建)".equals(hsysvo.getFactorname())) {
				list.add(OaWorkFlowUtil.listAddObj("azfhtj",
						itemvo.getBudget_mny() + ""));
			} else if ("勘察设计费".equals(hsysvo.getFactorname())) {
				list.add(OaWorkFlowUtil.listAddObj("kcsjf",
						itemvo.getBudget_mny() + ""));
			} else if ("监理费".equals(hsysvo.getFactorname())) {
				list.add(OaWorkFlowUtil.listAddObj("jlf",
						itemvo.getBudget_mny() + ""));
			} else if ("监检费".equals(hsysvo.getFactorname())) {
				list.add(OaWorkFlowUtil.listAddObj("jjf",
						itemvo.getBudget_mny() + ""));
			} else if ("工程赔偿费".equals(hsysvo.getFactorname())) {
				list.add(OaWorkFlowUtil.listAddObj("gcpcf",
						itemvo.getBudget_mny() + ""));
			} else if ("其它-报批报建费".equals(hsysvo.getFactorname())) {
				list.add(OaWorkFlowUtil.listAddObj("qtbpbjf",
						itemvo.getBudget_mny() + ""));
			} else if ("不可预见费".equals(hsysvo.getFactorname())) {
				list.add(OaWorkFlowUtil.listAddObj("bkyjf",
						itemvo.getBudget_mny() + ""));
			}
		}
		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	private JSONArray getDtaileDataMap(BudgetBillVO temp)
			throws BusinessException {
		List dtlist = new ArrayList();
		Map bodyMap = getBody(temp.getBudgetFactorBodyVO());
		dtlist.add(bodyMap);
		MaterialPlanBillVO wzvo = ((IMaterialPlanCommonService) NCLocator
				.getInstance().lookup(IMaterialPlanCommonService.class))
				.queryMaterialPlanBillVOByPkProjectForBudget(temp.getParentVO()
						.getPk_project());
		if (wzvo != null && wzvo.getChildrenVO().length > 0) {
			String pk_org = temp.getParentVO().getPk_org();
			OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
					pk_org);
			// 港华先上，后期去掉if条件判断即可
			if ("0001A21000000001ZBFB".equals(pk_org)
					|| "0001A21000000001ZBFB".equals(orgVO.getPk_fatherorg())) {
				Map wlbodyMap = getWLBody(wzvo);
				dtlist.add(wlbodyMap);
			}
		}
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(dtlist, jsonConfig);
		return dtlistString;
	}

	private Map getBody(BudgetFactorBodyVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (BudgetFactorBodyVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			// CBS
			String cbsbm = (String) getHyPubBO().findColValue(
					"bd_cbsnode",
					"code",
					"nvl(dr,0) = 0 and pk_cbsnode = '" + temp.getPk_cbsnode()
							+ "'");
			// cbs编码
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("cbsbm",
					cbsbm));
			String cbsName = (String) getHyPubBO().findColValue(
					"bd_cbsnode",
					"name",
					"nvl(dr,0) = 0 and pk_cbsnode = '" + temp.getPk_cbsnode()
							+ "'");
			// cbs名称
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

	private Map getWLBody(MaterialPlanBillVO wlvo) throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt2");
		List workflowRequestTableRecords = new ArrayList();
		MaterialPlanBodyVO[] wzbodyvos = wlvo.getChildrenVO();
		for (MaterialPlanBodyVO temp : wzbodyvos) {
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
				// 物料名称
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"material_name", materialVO.getName()));
				// 物料分类编码
				String wlflbm = (String) getHyPubBO().findColValue(
						"bd_marbasclass",
						"code",
						"nvl(dr,0) = 0 and pk_marbasclass = '"
								+ materialVO.getPk_marbasclass() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"wlflbm", wlflbm));
				// 物料分类名称
				String wlflmc = (String) getHyPubBO().findColValue(
						"bd_marbasclass",
						"code",
						"nvl(dr,0) = 0 and pk_marbasclass = '"
								+ materialVO.getPk_marbasclass() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"wlflmc", wlflmc));
				// 规格
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"materialspec", materialVO.getMaterialspec()));
				// 型号
				String str4 = "";
				if (null != materialVO.getMaterialtype()) {
					str4 = materialVO.getMaterialtype();
				}
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"materialtype", str4));
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

	public void setHyPubBO(HYPubBO hyPubBO) {
		this.hyPubBO = hyPubBO;
	}

	protected void initCommitAction(CommitAction<BudgetBillVO> action) {
		super.initCommitAction(action);

		action.addBeforeRule(new FactorNotNullValidateRule());

		action.addBeforeRule(new FactorBodyRepeatValidateRule());

		action.addBeforeRule(new CompSumFactorAndLeafCBSRule());

		action.addBeforeRule(new CompSumCBSAndUpNodeRule());

		action.addBeforeRule(new CompHeadAndRootCBS4FloatRule());

		action.addBeforeRule(new CompBudgetMnyAndFloatMnyRule());

		action.addBeforeRule(new WBSPretaskCompleteCheckRule());
	}

	public BudgetBillVO[] unCommitBudget(BudgetBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		unOaCommit(billVOs);
		return (BudgetBillVO[]) unCommit(billVOs, pfParamVO);
	}

	// 收回
	private void unOaCommit(BudgetBillVO[] aggVO) throws BusinessException {
		for (BudgetBillVO temp : aggVO) {
			// if ((temp.getHVO().getVtrantypecode()).contains("25")) {
			OaWorkFlowUtil.backOaWorkFlow(temp.getPrimaryKey());
			// }
		}
	}

	public BudgetBillVO[] freezeBudget(BudgetBillVO[] billVOs,
			BudgetBillVO[] originBillVOs) throws BusinessException {
		for (int i = 0; (billVOs != null) && (i < billVOs.length); i++) {
			billVOs[i].getParentVO().setStatus(1);
		}
		UpdateAction<BudgetBillVO> action = createUpdateAction();

		action.addBeforeRule(new UpdateBillSatusRule(10));

		return (BudgetBillVO[]) action.processAction(billVOs, originBillVOs);
	}

	public BudgetBillVO[] UnFreezeBudget(BudgetBillVO[] billVOs,
			BudgetBillVO[] originBillVOs) throws BusinessException {
		for (int i = 0; (billVOs != null) && (i < billVOs.length); i++) {
			billVOs[i].getParentVO().setStatus(1);
		}
		UpdateAction<BudgetBillVO> action = createUpdateAction();

		action.addBeforeRule(new UpdateBillSatusRule(9));

		return (BudgetBillVO[]) action.processAction(billVOs, originBillVOs);
	}

	public BudgetBillVO[] releasedBudget(BudgetBillVO[] billVOs,
			BudgetBillVO[] originBillVOs) throws BusinessException {
		if (billVOs == null) {
			return null;
		}
		for (int i = 0; i < billVOs.length; i++) {
			billVOs[i].getParentVO().setStatus(1);
		}
		BudgetCreateReleasedAction action = new BudgetCreateReleasedAction();
		return action.released(billVOs, originBillVOs);
	}

	public BudgetBillVO[] UnReleasedBudget(BudgetBillVO[] billVOs,
			BudgetBillVO[] originBillVOs) throws BusinessException {
		VoStatusUtils.setVoStatus(1, billVOs);
		UpdateAction<BudgetBillVO> action = createUpdateAction();

		action.addBeforeRule(new UnReleaseValidateRule());

		action.addBeforeRule(new UpdateBillSatusRule(1));

		action.addBeforeRule(new CleanExeAndPreMnyRule());

		action.addBeforeRule(new CalBalaMnyBodyRule());

		action.addAfterRule(new ClearDataOfBudgetDetailRule());

		return (BudgetBillVO[]) action.processAction(billVOs, originBillVOs);
	}

	public BudgetBillVO[] alterBudget(BudgetBillVO[] billVOs,
			BudgetBillVO[] originBillVOs) throws BusinessException {
		UpdateAction<BudgetBillVO> action = createUpdateAction();
		addRuleForAlteration(action, billVOs, originBillVOs);
		return (BudgetBillVO[]) action.processAction(billVOs, originBillVOs);
	}

	private void addRuleForAlteration(UpdateAction<BudgetBillVO> action,
			BudgetBillVO[] billVOs, BudgetBillVO[] originBillVOs) {
		action.addBeforeRule(new CBSBodyRepeatValidateRule());

		action.addBeforeRule(new FactorBodyRepeatValidateRule());

		action.addBeforeRule(new ProjectClosedValidateRule());

		action.addBeforeRule(new CompCBSForTabsRule());

		action.addBeforeRule(new CreateOldVersionRule(originBillVOs));

		action.addBeforeRule(new SumFactorBodyToLeafCBSRule());

		action.addBeforeRule(new SumCBSToUpNodeRule());

		action.addBeforeRule(new CompBudgetMnyAndExeMnyRule());

		action.addBeforeRule(new CompBudgetMnyAndZoreRule());

		boolean modify_flag = getModifyBudgetCtlFlag(billVOs, originBillVOs);
		if (modify_flag) {
			action.addBeforeRule(new CleanExeAndPreMnyRule());

			action.addAfterRule(new ClearDataOfBudgetDetailRule());

			action.addAfterRule(new UpdateBudget4ReleasedBudgetAfterRule());
		}

		action.addBeforeRule(new CompBudgetMnyAndFloatMnyRule());

		action.addBeforeRule(new CompHeadAndRootCBS4FloatRule());

		action.addBeforeRule(new FillHeadBudgetRule());

		action.addBeforeRule(new InitMutiCurrTypeBeforeRule());

		action.addBeforeRule(new SetAddStatusBodyOrgRule());
	}

	private boolean getModifyBudgetCtlFlag(BudgetBillVO[] billVOs,
			BudgetBillVO[] originBillVOs) {
		String ori_pk_costBGctl = originBillVOs[0].getParentVO()
				.getPk_costbgtctrl();

		String clent_pk_costBGctl = billVOs[0].getParentVO()
				.getPk_costbgtctrl();

		if (ori_pk_costBGctl.equals(clent_pk_costBGctl)) {
			return false;
		}
		return true;
	}

	// 获取资源
	public BudgetBillVO budgetResource(BudgetBillVO billVO)
			throws BusinessException {
		String pk_project = billVO.getParentVO().getPk_project();

		MaterialPlanBillVO materialPlanBillVO = ((IMaterialPlanForBudgetService) PMProxy
				.lookup(IMaterialPlanForBudgetService.class))
				.queryReleasedMaterialPlan(pk_project);

		if (materialPlanBillVO == null) {
			ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl
					.getNCLangRes().getStrByID("projectbudget_0",
							"04815005-0221"));

			return null;
		}
		// 获取含税成本预算
		double hscbtotal = 0;
		for (MaterialPlanBodyVO mxvo : materialPlanBillVO.getChildrenVO()) {
			if(null!=mxvo.getBdef8()){
				double hscb = Double.parseDouble(mxvo.getBdef8());
				hscbtotal += hscb;
			}
		}
		BudgetExeBodyVO[] budgetExeBodyVOsAfterSum = BudgetResourceUtil
				.getBudgetExeBodyVOs(new AbstractBill[] { materialPlanBillVO },
						"4D14-01");

		billVO.getParentVO().setHdef30(hscbtotal + "");// 获取含税成本预算
		billVO.getParentVO().setMal_version(
				materialPlanBillVO.getParentVO().getContr_version());

		BudgetFactorBodyVO[] facotrBodyVOs = generateFactorVOByMTB(budgetExeBodyVOsAfterSum);
		billVO.setChildren(BudgetFactorBodyVO.class, facotrBodyVOs);

		PublicBudgetUtil.fillDefaultCBS(billVO);

		FillCBSBodyUtil.fillCBSBodyByFactorBody(billVO);
		return billVO;
	}

	private BudgetFactorBodyVO[] generateFactorVOByMTB(
			BudgetExeBodyVO[] budgetExeBodyVOs) {
		List<BudgetFactorBodyVO> facotrBodyList = new ArrayList();
		for (BudgetExeBodyVO budgetExeBodyVO : budgetExeBodyVOs) {
			BudgetFactorBodyVO factorBodyVO = new BudgetFactorBodyVO();

			for (int i = 0; i < PublicBudgetUtil.zeroKeys_factor.length; i++) {
				factorBodyVO.setAttributeValue(
						PublicBudgetUtil.zeroKeys_factor[i], UFDouble.ZERO_DBL);
			}

			factorBodyVO.setRowno("" + (facotrBodyList.size() + 1) * 10);
			factorBodyVO.setPk_cbsnode(budgetExeBodyVO.getPk_cbsnode());
			factorBodyVO.setPk_factor(budgetExeBodyVO.getPk_factor());
			factorBodyVO.setBudget_mny(budgetExeBodyVO.getNmoney());
			factorBodyVO.setRes_mny(budgetExeBodyVO.getNmoney());
			factorBodyVO.setFloat_mny(UFDouble.ZERO_DBL);
			factorBodyVO.setFloat_per(UFDouble.ZERO_DBL);

			factorBodyVO.setStatus(2);
			facotrBodyList.add(factorBodyVO);
		}

		return (BudgetFactorBodyVO[]) facotrBodyList
				.toArray(new BudgetFactorBodyVO[0]);
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