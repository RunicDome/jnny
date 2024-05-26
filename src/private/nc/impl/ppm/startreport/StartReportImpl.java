package nc.impl.ppm.startreport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.trade.business.HYPubBO;
import nc.impl.pm.billrule.OnlyOneBillForConditionRule;
import nc.impl.pm.billrule.SupplyProjectInfoBeforeRule;
import nc.impl.pm.billrule.SupplyStatusByTransiRuleBeforeRule;
import nc.impl.pm.billrule.WBSPretaskCompleteCheckRule;
import nc.impl.pm.billrule.WriteBackToProjectBeforeRule;
import nc.impl.pmpub.servicebase.BillBaseImpl;
import nc.impl.pmpub.servicebase.action.ApproveAction;
import nc.impl.pmpub.servicebase.action.CommitAction;
import nc.impl.pmpub.servicebase.action.DeleteAction;
import nc.impl.pmpub.servicebase.action.InsertAction;
import nc.impl.pmpub.servicebase.action.UnApproveAction;
import nc.impl.pmpub.servicebase.action.UnCommitAction;
import nc.impl.pmpub.servicebase.action.UpdateAction;
import nc.impl.ppm.startreport.point.StartReportPoint;
import nc.impl.ppm.startreport.rule.AcceptCheckRepeatRule;
import nc.impl.ppm.startreport.rule.WriteBackToProjectRule;
import nc.itf.ppm.startreport.prv.IStartReportInterface;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.org.OrgVO;
import nc.vo.pbm.materialplan.MaterialPlanBillVO;
import nc.vo.pbm.materialplan.MaterialPlanHeadVO;
import nc.vo.pcm.contractalter.ContrAlterBillVO;
import nc.vo.pcm.contractalter.ContrAlterHeadVO;
import nc.vo.ppm.startreport.StartReportBillVO;
import nc.vo.ppm.startreport.StartReportBodyVO;
import nc.vo.ppm.startreport.StartReportHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

// 开工单
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
public class StartReportImpl extends BillBaseImpl<StartReportBillVO> implements
		IStartReportInterface {
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表名
	public static String WORKFLOWID_JNRL = "53";
	public static String WORKFLOWID_JNNY = "304";

	public String getDef2(String pk_org) throws BusinessException {
		String def2 = "";
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		if (null != orgVO) {
			def2 = orgVO.getDef2();
		}
		return def2;
	}

	public StartReportBillVO[] insertStartReportVOS(StartReportBillVO[] billVOs)
			throws BusinessException {
		return (StartReportBillVO[]) super.insert(billVOs);
	}

	public StartReportBillVO[] updateStartReportVOS(
			StartReportBillVO[] billVOs, StartReportBillVO[] originBillVOs)
			throws BusinessException {
		return (StartReportBillVO[]) super.update(billVOs, originBillVOs);
	}

	public void deleteStartReportVOS(StartReportBillVO[] billVOs)
			throws BusinessException {
		super.delete(billVOs);
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

	// 提交
	public StartReportBillVO[] commitStartReportVOS(StartReportBillVO[] billVOs)
			throws BusinessException {
		StartReportBillVO[] bvos = (StartReportBillVO[]) super.commit(billVOs);
		sendoa(bvos);// 提交OA
		return bvos;
	}

	private void sendoa(StartReportBillVO[] billVOs) throws BusinessException {
		for (StartReportBillVO temp : billVOs) {
			StartReportHeadVO hVO = temp.getParentVO();
			if ("4".equals(getDef2(hVO.getPk_org()))) {
				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hVO.getPk_org(),
						"4D23");
				// ----------------------
				if (oaVo != null && oaVo.getIsdr() == 0) {
					WorkFId = oaVo.getFlowid();
					TableName = oaVo.getTablename();
					JSONArray headData = getMainMap(hVO);
					
					// 构造workflow信息
					WorkFlowBill bill = getFlowBill(temp, hVO, "4D23",
							WorkFId, "开工单");
					// 调用OA工具类同步数据至OA
					OaWorkFlowUtil.sendOaData(headData, null, bill);
				}
			}
		}
	}

	// 开工单主表数据
	private JSONArray getMainMap(StartReportHeadVO parentVO)
			throws BusinessException {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);

		Map djzj = OaWorkFlowUtil.listAdd("djzj", parentVO.getPk_startreport());
		list.add(djzj);

		Map zdrzj = OaWorkFlowUtil.listAdd("zdrzj", parentVO.getBillmaker());
		list.add(zdrzj);

		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		if (null != userVO) {

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
				"nvl(dr,0) = 0 and pk_project ='" + parentVO.getPk_project()
						+ "'");
		Map xmbm = OaWorkFlowUtil.listAdd("xmbm", project_code);
		list.add(xmbm);

		String project_name = (String) getHyPubBO().findColValue(
				"bd_project",
				"project_name",
				"nvl(dr,0) = 0 and pk_project ='" + parentVO.getPk_project()
						+ "'");
		Map xmmc = OaWorkFlowUtil.listAdd("xmmc", project_name);
		list.add(xmmc);

		String pk_projectclass = (String) getHyPubBO().findColValue(
				"bd_project",
				"pk_projectclass",
				"nvl(dr,0) = 0 and   pk_project  ='" + parentVO.getPk_project()
						+ "'");
		Map xmlx = OaWorkFlowUtil.listAdd("xmlx", pk_projectclass);
		list.add(xmlx);

		String pk_eps = (String) getHyPubBO().findColValue(
				"bd_project",
				"pk_eps",
				"nvl(dr,0) = 0 and   pk_project  ='" + parentVO.getPk_project()
						+ "'");
		Map eps = OaWorkFlowUtil.listAdd("eps", pk_eps);
		list.add(eps);

		Map ksrq = OaWorkFlowUtil.listAdd("ksrq", parentVO.getStart_work_date()
				.getYear()
				+ "-"
				+ parentVO.getStart_work_date().getStrMonth()
				+ "-" + parentVO.getStart_work_date().getStrDay());
		list.add(ksrq);

		if (null != parentVO.getDef1()) {
			String where = " pk_defdoc = '"
					+ parentVO.getDef1()
					+ "' and nvl(dr,0) = 0";
			String def1 = "0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					where)) {
				def1 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where);
			}

			Map sqbmbh = OaWorkFlowUtil.listAdd("sqbmbh", def1);
			list.add(sqbmbh);
		}

		if (null != parentVO.getDef1()) {
			String where = " pk_defdoc = '"
					+ parentVO.getDef1()
					+ "' and nvl(dr,0) = 0";
			String def1 = "0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					where)) {
				def1 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where);
			}

			Map sqbmmc = OaWorkFlowUtil.listAdd("sqbmmc", def1);
			list.add(sqbmmc);
		}

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}
	private WorkFlowBill getFlowBill(StartReportBillVO temp, StartReportHeadVO hvo,
			String billCode, String workFId, String flowName)
			throws BusinessException {
		OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
		workFlowVO.setPrimaryKey(hvo.getPrimaryKey());
		workFlowVO.setPkGroup(hvo.getPk_group());
		workFlowVO.setPkOrg(hvo.getPk_org());
		workFlowVO.setBillMaker(hvo.getCreator());
		workFlowVO.setCreator(hvo.getCreator());
		workFlowVO.setBillCode(billCode);
		// 接口获取
		// workFlowVO.setWorkflowId(OaWorkFlowUtil.getOAFlowID(workFId));
		workFlowVO.setWorkflowId(workFId);
		workFlowVO.setWorkflowName(flowName);
		WorkFlowBill bill = OaWorkFlowUtil.getWorkFlowBill(workFlowVO);
		bill.setDef3("ZT");
		bill.setDef4(hvo.getBill_code());// 单据编号
		// 制单人身份证号
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				temp.getParentVO().getCreator());
		PsndocVO psndoc = (PsndocVO) getHyPubBO().queryByPrimaryKey(
				PsndocVO.class, userVO.getPk_psndoc());
		bill.setDef5(psndoc.getId());
		return bill;
	}
	private WorkFlowBill getWorkFlowBill(StartReportBillVO temp)
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
			workFlowBill.setWorkflowName("开工单");

		} else {
			workFlowBill.setWorkflowId("WORKFLOWID_JNRL");
			if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
				workFlowBill.setWorkflowId(WORKFLOWID_JNNY);
			}
			workFlowBill.setPk_group(temp.getParentVO().getPk_group());
			workFlowBill.setPk_org(temp.getParentVO().getPk_org());
			workFlowBill.setPk_bill(temp.getPrimaryKey());
			workFlowBill.setUser_name(userVO.getUser_name());
			workFlowBill.setUser_code(userVO.getUser_code());
			workFlowBill.setBill_status("1");
			workFlowBill.setCreationtime(new UFDate().toStdString());
			workFlowBill.setCreator(temp.getParentVO().getCreator());
			// workFlowBill.setWorkflowId("53");
			workFlowBill.setBill_code(temp.getParentVO().getBill_type());
			workFlowBill.setWorkflowName("开工单");
		}
		return workFlowBill;
	}

	public StartReportBillVO[] unCommitStartReportVOS(
			StartReportBillVO[] billVOs, PfParameterVO pfParamVO)
			throws BusinessException {
		StartReportBillVO[] newAggVO = (StartReportBillVO[]) unCommit(billVOs,
				pfParamVO);
		unOaCommit(newAggVO);
		return newAggVO;
	}

	private void unOaCommit(StartReportBillVO[] aggVO) throws BusinessException {
		for (StartReportBillVO temp : aggVO) {
			if ("4D23-01".equals(temp.getParentVO().getBill_type())) {
				OaWorkFlowUtil.backOaWorkFlow(temp.getPrimaryKey());
			}
		}
	}

	public Object approveStartReportVOS(StartReportBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		return super.approve(billVOs, pfParamVO);
	}

	public StartReportBillVO[] unapproveStartReportVOS(
			StartReportBillVO[] billVOs, PfParameterVO pfParamVO)
			throws BusinessException {
		return (StartReportBillVO[]) super.unApprove(billVOs, pfParamVO);
	}

	protected void initApproveAction(ApproveAction<StartReportBillVO> action) {
		action.addBeforeRule(new WBSPretaskCompleteCheckRule());

		action.addAfterRule(new WriteBackToProjectRule());

		action.addAfterRule(new SupplyProjectInfoBeforeRule());

		action.addAfterRule(new SupplyStatusByTransiRuleBeforeRule());

		action.addAfterRule(new WriteBackToProjectBeforeRule(true));

		action.setPluginPoint(StartReportPoint.APPROVE);
		super.initApproveAction(action);
	}

	protected void initCommitAction(CommitAction<StartReportBillVO> action) {
		action.setPluginPoint(StartReportPoint.SEND_APPROVE);

		action.addBeforeRule(new WBSPretaskCompleteCheckRule());
		super.initCommitAction(action);
	}

	protected void initDeleteAction(DeleteAction<StartReportBillVO> action) {
		action.setPluginPoint(StartReportPoint.DELETE);
		super.initDeleteAction(action);
	}

	protected void initInsertAction(InsertAction<StartReportBillVO> action) {
		action.setPluginPoint(StartReportPoint.INSERT);

		action.addBeforeRule(new OnlyOneBillForConditionRule(
				new String[] { "pk_project" }, NCLangRes4VoTransl
						.getNCLangRes().getStrByID("startreport_0",
								"04810016-0003")));

		action.addBeforeRule(new AcceptCheckRepeatRule());
		super.initInsertAction(action);
	}

	protected void initUnApproveAction(UnApproveAction<StartReportBillVO> action) {
		action.setPluginPoint(StartReportPoint.UNAPPROVE);

		action.addBeforeRule(new WriteBackToProjectBeforeRule(false));

		action.addAfterRule(new WriteBackToProjectRule());
		super.initUnApproveAction(action);
	}

	protected void initUnCommitAction(UnCommitAction<StartReportBillVO> action) {
		action.setPluginPoint(StartReportPoint.UNSEND_APPROVE);
		super.initUnCommitAction(action);
	}

	protected void initUpdateAction(UpdateAction<StartReportBillVO> action) {
		action.setPluginPoint(StartReportPoint.UPDATE);

		action.addBeforeRule(new OnlyOneBillForConditionRule(
				new String[] { "pk_project" }, NCLangRes4VoTransl
						.getNCLangRes().getStrByID("startreport_0",
								"04810016-0003")));

		action.addBeforeRule(new AcceptCheckRepeatRule());
		super.initUpdateAction(action);
	}

}
