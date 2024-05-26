package nc.impl.pcm.martcontalter.pvt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.pcm.martcontalter.bp.rule.ApproveWriteMContrRule;
import nc.bs.pcm.martcontalter.bp.rule.CheckPorjectStatusRule;
import nc.bs.pcm.martcontalter.bp.rule.MContrSaveValidaterRule;
import nc.bs.pcm.martcontalter.bp.rule.OnlyOneBillRule;
import nc.bs.pcm.martcontalter.bp.rule.UnApproveWriteMContrRule;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pmpub.servicebase.BillBaseImpl;
import nc.impl.pmpub.servicebase.action.ApproveAction;
import nc.impl.pmpub.servicebase.action.CommitAction;
import nc.impl.pmpub.servicebase.action.DeleteAction;
import nc.impl.pmpub.servicebase.action.InsertAction;
import nc.impl.pmpub.servicebase.action.UnApproveAction;
import nc.impl.pmpub.servicebase.action.UpdateAction;
import nc.itf.pcm.martcontalter.pvt.IMContrAlter;
import nc.vo.org.OrgVO;
import nc.vo.pcm.marketcontract.ContractHeadVO;
import nc.vo.pcm.martcontalter.MContr_AlterBillVO;
import nc.vo.pcm.martcontalter.MContr_AlterBodyVO;
import nc.vo.pcm.martcontalter.MContr_AlterHeadVO;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

// 补充协议(项目销售合同)
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MContrAlterImpl extends BillBaseImpl<MContr_AlterBillVO> implements
		IMContrAlter {
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表明

	public MContr_AlterBillVO[] insertAlter(MContr_AlterBillVO[] billVOs)
			throws BusinessException {
		return (MContr_AlterBillVO[]) insert(billVOs);
	}

	public MContr_AlterBillVO[] deleteAlter(MContr_AlterBillVO[] billVOs)
			throws BusinessException {
		return (MContr_AlterBillVO[]) delete(billVOs);
	}

	public MContr_AlterBillVO[] updateAlter(MContr_AlterBillVO[] billVOs,
			MContr_AlterBillVO[] originBillVOs) throws BusinessException {
		return (MContr_AlterBillVO[]) update(billVOs, originBillVOs);
	}

	public MContr_AlterBillVO[] commitAlter(MContr_AlterBillVO[] billVOs)
			throws BusinessException {
		billVOs = (MContr_AlterBillVO[]) commit(billVOs);
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

	private String billType = "4D61";

	private void senOaData(MContr_AlterBillVO[] billVOs)
			throws BusinessException {
		for (MContr_AlterBillVO temp : billVOs) {
			// 补充协议主表(项目销售合同)
			MContr_AlterHeadVO hVO = (MContr_AlterHeadVO) temp.getParentVO();
			if ("4".equals(getDef2(hVO.getPk_org()))
					&& hVO.getBill_type().contains(billType)) {
				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hVO.getPk_org(),
						"4D61");
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
					workFlowVO.setWorkflowName("补充协议（项目销售合同）");
					WorkFlowBill bill = OaWorkFlowUtil
							.getWorkFlowBill(workFlowVO);
					bill.setDef3("ZT");
					// 制单人身份证号
					UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
							UserVO.class, hVO.getBillmaker());
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

	private JSONArray getMainMap(MContr_AlterHeadVO parentVO)
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
		// 合同信息
		ContractHeadVO contVO = (ContractHeadVO) getHyPubBO()
				.queryByPrimaryKey(ContractHeadVO.class,
						parentVO.getPk_mcontr());
		if (null != contVO) {
			// 编码
			list.add(OaWorkFlowUtil.listAddObj("pk_contr_code",
					contVO.getBill_code()));
			// 名称
			list.add(OaWorkFlowUtil.listAddObj("pk_contr_name",
					contVO.getBill_name()));
			// 合同类型
			String billTypeName = (String) getHyPubBO().findColValue(
					"pm_contracttype",
					"type_name",
					"nvl(dr,0) = 0 and pk_contracttype  = '"
							+ contVO.getPk_contracttype() + "'");
			list.add(OaWorkFlowUtil.listAddObj("typename", billTypeName));
		}

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
		// 获取销售合同中的项目
		if (null != contVO.getPk_project()) {
			ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
					.queryByPrimaryKey(ProjectHeadVO.class,
							contVO.getPk_project());
			// 项目编码
			list.add(OaWorkFlowUtil.listAddObj("project_code",
					projectVO.getProject_code()));
			// 项目名称
			list.add(OaWorkFlowUtil.listAddObj("project_name",
					projectVO.getProject_name()));
		}
		// 经办人
		String apppsnhName = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc  = '"
						+ parentVO.getDispose_person() + "'");
		list.add(OaWorkFlowUtil.listAddObj("jbr", apppsnhName));
		// 经办部门
		String sqbmName = (String) getHyPubBO().findColValue(
				"org_dept_v",
				"name",
				"nvl(dr,0) = 0 and pk_vid  = '" + parentVO.getDispose_dept_v()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("jbbm", sqbmName));
		// 客户
		String customerName = (String) getHyPubBO().findColValue(
				"bd_customer",
				"name ",
				"nvl(dr,0) = 0 and  pk_customer  = '"
						+ parentVO.getPk_customer() + "'");
		list.add(OaWorkFlowUtil.listAddObj("kh", customerName));
		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	private JSONArray getDtaileDataMap(MContr_AlterBillVO temp)
			throws BusinessException {
		// 获取补充协议明细
		MContr_AlterBodyVO[] bvos = (MContr_AlterBodyVO[]) getHyPubBO()
				.queryByCondition(
						MContr_AlterBodyVO.class,
						"nvl(dr,0) = 0 and pk_mcontr_alter = '"
								+ temp.getPrimaryKey() + "'");
		Map bodyMap = getBody(bvos);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
		return dtlistString;
	}

	private Map getBody(MContr_AlterBodyVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (MContr_AlterBodyVO temp : bvo) {
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

	public MContr_AlterBillVO[] unCommitAlter(MContr_AlterBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		billVOs = (MContr_AlterBillVO[]) unCommit(billVOs, pfParamVO);
		unOaCommit(billVOs);
		return billVOs;
	}

	private void unOaCommit(MContr_AlterBillVO[] aggVO)
			throws BusinessException {
		for (MContr_AlterBillVO temp : aggVO) {
			MContr_AlterHeadVO hVO = (MContr_AlterHeadVO) temp.getParentVO();
			if (hVO.getBill_type().contains(billType)) {
				OaWorkFlowUtil.backOaWorkFlow(temp.getPrimaryKey());
			}
		}
	}

	public Object approveAlter(MContr_AlterBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		return approve(billVOs, pfParamVO);
	}

	public MContr_AlterBillVO[] unapproveAlter(MContr_AlterBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		return (MContr_AlterBillVO[]) unApprove(billVOs, pfParamVO);
	}

	protected void initApproveAction(ApproveAction<MContr_AlterBillVO> action) {
		super.initApproveAction(action);
		action.addAfterRule(new CheckPorjectStatusRule());
		action.addAfterRule(new ApproveWriteMContrRule());
	}

	protected void initUnApproveAction(
			UnApproveAction<MContr_AlterBillVO> action) {
		super.initUnApproveAction(action);
		action.addBeforeRule(new OnlyOneBillRule());
		action.addAfterRule(new CheckPorjectStatusRule());
		action.addAfterRule(new UnApproveWriteMContrRule());
	}

	protected void initInsertAction(InsertAction<MContr_AlterBillVO> action) {
		super.initInsertAction(action);
		action.addAfterRule(new CheckPorjectStatusRule());
		action.addBeforeRule(new OnlyOneBillRule());
		action.addBeforeRule(new MContrSaveValidaterRule());
	}

	protected void initCommitAction(CommitAction<MContr_AlterBillVO> action) {
		super.initCommitAction(action);
		action.addAfterRule(new CheckPorjectStatusRule());
	}

	protected void initDeleteAction(DeleteAction<MContr_AlterBillVO> action) {
		super.initDeleteAction(action);
		action.addAfterRule(new CheckPorjectStatusRule());
	}

	protected void initUpdateAction(UpdateAction<MContr_AlterBillVO> action) {
		super.initUpdateAction(action);
		action.addBeforeRule(new MContrSaveValidaterRule());
		action.addAfterRule(new CheckPorjectStatusRule());
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
