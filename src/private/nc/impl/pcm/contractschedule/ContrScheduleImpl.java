package nc.impl.pcm.contractschedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nc.bs.pcm.contract.bp.rule.CheckPayApplyRule;
import nc.bs.pcm.contract.bp.rule.ValidateContrBeginRule;
import nc.bs.pcm.contractschedule.bp.rule.ContrOprAfterApprove;
import nc.bs.pcm.contractschedule.bp.rule.ContrOprAfterDel;
import nc.bs.pcm.contractschedule.bp.rule.ContrOprBeforeApprove;
import nc.bs.pcm.contractschedule.bp.rule.ContrOprBeforeSave;
import nc.bs.pcm.contractschedule.bp.rule.ContrOprBeforeUnApprove;
import nc.bs.pcm.contractschedule.bp.rule.SaveCheckSche_mny_propRule;
import nc.bs.pcm.contractschedule.bp.rule.UpdateContrScheOprnumRule;
import nc.bs.pmpub.rule.AppendBusiTypeBeforeRule;
import nc.bs.pmpub.rule.Approve4SendFipRule;
import nc.bs.pmpub.rule.UnApproveDelFipMesRule;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pcm.billrule.DeletePayableBillRule;
import nc.impl.pm.billrule.BillCodeCheckRule;
import nc.impl.pmpub.servicebase.BillBaseImpl;
import nc.impl.pmpub.servicebase.action.ApproveAction;
import nc.impl.pmpub.servicebase.action.DeleteAction;
import nc.impl.pmpub.servicebase.action.InsertAction;
import nc.impl.pmpub.servicebase.action.UnApproveAction;
import nc.impl.pmpub.servicebase.action.UpdateAction;
import nc.impl.qc.c001.approve.action.ApplyUnSendAction;
import nc.itf.pbm.commonrule.BillDelOrUnApprove4BudgetRule;
import nc.itf.pbm.commonrule.BillSaveOrApprove4BudgetRule;
import nc.itf.pbm.commonrule.BillUpdate4BudgetAfterRule;
import nc.itf.pbm.commonrule.BillUpdate4BudgetBeforeRule;
import nc.itf.pcm.contract.pub.ContrOperTypeEnum;
import nc.itf.pcm.contractschedule.prv.IContrSchedule;
import nc.vo.bd.material.MaterialVO;
import nc.vo.bd.supplier.SupplierVO;
import nc.vo.cmp.apply.ApplyBVO;
import nc.vo.org.OrgVO;
import nc.vo.pcm.contract.ContrHeadVO;
import nc.vo.pcm.contractschedule.ContractScheduleBillVO;
import nc.vo.pcm.contractschedule.ContractScheduleBodyVO;
import nc.vo.pcm.contractschedule.ContractScheduleHeadVO;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.qc.c001.entity.ApplyVO;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

// 进度款单
@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
public class ContrScheduleImpl extends BillBaseImpl<ContractScheduleBillVO>
		implements IContrSchedule {
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表明

	public ContractScheduleBillVO[] insertSchedule(
			ContractScheduleBillVO[] billVOs) throws BusinessException {
		return (ContractScheduleBillVO[]) insert(billVOs);
	}

	protected void initInsertAction(InsertAction<ContractScheduleBillVO> action) {
		super.initInsertAction(action);
		action.addBeforeRule(new ValidateContrBeginRule());
		action.addBeforeRule(new AppendBusiTypeBeforeRule());
		action.addBeforeRule(new ContrOprBeforeSave(ContrOperTypeEnum.OPER_ADD));
		action.addAfterRule(new BillCodeCheckRule());
		action.addAfterRule(new BillSaveOrApprove4BudgetRule(0));
		action.addBeforeRule(new SaveCheckSche_mny_propRule());
	}

	public ContractScheduleBillVO[] updateSchedule(
			ContractScheduleBillVO[] billVOs,
			ContractScheduleBillVO[] originBillVOs) throws BusinessException {
		return (ContractScheduleBillVO[]) update(billVOs, originBillVOs);
	}

	protected void initUpdateAction(UpdateAction<ContractScheduleBillVO> action) {
		super.initUpdateAction(action);
		action.addBeforeRule(new ValidateContrBeginRule());
		action.addBeforeRule(new ContrOprBeforeSave(
				ContrOperTypeEnum.OPER_UPDATE));
		action.addBeforeRule(new BillUpdate4BudgetBeforeRule(0));
		action.addAfterRule(new BillUpdate4BudgetAfterRule(0));
		action.addBeforeRule(new SaveCheckSche_mny_propRule());
	}

	public void deleteSchedule(ContractScheduleBillVO[] billVOs)
			throws BusinessException {
		delete(billVOs);
	}

	protected void initDeleteAction(DeleteAction<ContractScheduleBillVO> action) {
		super.initDeleteAction(action);
		action.addBeforeRule(new BillDelOrUnApprove4BudgetRule(0));
		action.addBeforeRule(new ContrOprAfterDel());
	}

	public Object approveSchedule(ContractScheduleBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		return approve(billVOs, pfParamVO);
	}

	protected void initApproveAction(
			ApproveAction<ContractScheduleBillVO> action) {
		super.initApproveAction(action);
		action.addBeforeRule(new ContrOprBeforeApprove());
		action.addAfterRule(new UpdateContrScheOprnumRule());
		action.addAfterRule(new BillSaveOrApprove4BudgetRule(1));
		action.addAfterRule(new ContrOprAfterApprove());
		action.addAfterRule(new Approve4SendFipRule("curr_shou_pay"));
	}

	public ContractScheduleBillVO[] unapproveSchedule(
			ContractScheduleBillVO[] billVOs, PfParameterVO pfParamVO)
			throws BusinessException {
		return (ContractScheduleBillVO[]) unApprove(billVOs, pfParamVO);
	}

	protected void initUnApproveAction(
			UnApproveAction<ContractScheduleBillVO> action) {
		action.addBeforeRule(new CheckPayApplyRule());
		super.initUnApproveAction(action);
		action.addBeforeRule(new ContrOprBeforeUnApprove());
		action.addBeforeRule(new BillDelOrUnApprove4BudgetRule(1));
		action.addBeforeRule(new DeletePayableBillRule());
		action.addBeforeRule(new UnApproveDelFipMesRule("curr_shou_pay"));
	}

	public ContractScheduleBillVO[] commitSchedule(
			ContractScheduleBillVO[] billVOs) throws BusinessException {
		billVOs = (ContractScheduleBillVO[]) commit(billVOs);
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

	private String billType = "4D48";

	private void senOaData(ContractScheduleBillVO[] billVOs)
			throws BusinessException {
		for (ContractScheduleBillVO temp : billVOs) {
			ContractScheduleHeadVO hVO = temp.getParentVO();
			// 期初不传OA Begin_flag
			if ("4".equals(getDef2(temp.getParentVO().getPk_org()))
					&& hVO.getBill_type().contains(billType) && !hVO.getBegin_flag().booleanValue()) {
				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hVO.getPk_org(),
						"4D48");
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
					workFlowVO.setWorkflowName("进度款单");
					WorkFlowBill bill = OaWorkFlowUtil
							.getWorkFlowBill(workFlowVO);
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

	private JSONArray getMainMap(ContractScheduleHeadVO parentVO)
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
		/*
		 * String billTypeName = (String) getHyPubBO().findColValue(
		 * "bd_billtype", "billtypename", "nvl(dr,0) = 0 and pk_billtypeid  = '"
		 * + parentVO.getBill_type() + "'");
		 * list.add(OaWorkFlowUtil.listAddObj("billTypeName", billTypeName));
		 */
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
		// 合同信息
		ContrHeadVO contVO = (ContrHeadVO) getHyPubBO().queryByPrimaryKey(
				ContrHeadVO.class, parentVO.getPk_contr());
		if (null != contVO) {
			// 编码
			list.add(OaWorkFlowUtil.listAddObj("pk_contr_code",
					contVO.getBill_code()));
			// 名称
			list.add(OaWorkFlowUtil.listAddObj("bill_name",
					contVO.getBill_name()));
			// 合同金额
			list.add(OaWorkFlowUtil.listAddObj("ncurrent_mny",
					contVO.getNcurrent_mny() + ""));
			// 合同类型
			String billTypeName = (String) getHyPubBO().findColValue(
					"pm_contracttype",
					"type_name",
					"nvl(dr,0) = 0 and pk_contracttype  = '"
							+ contVO.getPk_contracttype() + "'");
			list.add(OaWorkFlowUtil.listAddObj("typename", billTypeName));
			// 供应商名称
			SupplierVO supplierVO = (SupplierVO) getHyPubBO()
					.queryByPrimaryKey(SupplierVO.class,
							contVO.getPk_supplier());
			String name = "";
			if (null != supplierVO.getPk_supplier()) {
				name = supplierVO.getName();
			}
			list.add(OaWorkFlowUtil.listAddObj("pk_supplier_name", name));
		}
		// 项目
		if (null != parentVO.getPk_project()) {
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
		// 经办人
		String apppsnhName = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc  = '"
						+ parentVO.getPk_transactor() + "'");
		list.add(OaWorkFlowUtil.listAddObj("jbr", apppsnhName));
		// 经办部门
		String sqbmName = (String) getHyPubBO().findColValue(
				"org_dept_v",
				"name",
				"nvl(dr,0) = 0 and pk_vid  = '"
						+ parentVO.getPk_transact_dept_v() + "'");
		list.add(OaWorkFlowUtil.listAddObj("jbbm", sqbmName));
		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	private JSONArray getDtaileDataMap(ContractScheduleBillVO temp)
			throws BusinessException {
		// 获取详细信息
		ContractScheduleBodyVO[] bvos = temp.getChildrenVO();
		Map bodyMap = getBody(bvos);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
		return dtlistString;
	}

	private Map getBody(ContractScheduleBodyVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (ContractScheduleBodyVO temp : bvo) {
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
			// 税率
			Map sl = OaWorkFlowUtil.listAdd(
					"sl",
					new UFDouble(temp.getBdef1() == null ? "0.00"
							: temp.getBdef1()).setScale(2,
							UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(sl);
			// CBS名称
			String cbsName = (String) getHyPubBO().findColValue(
					"bd_cbsnode",
					"name",
					"nvl(dr,0) = 0 and pk_cbsnode  = '" + temp.getPk_cbsnode()
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

	public ContractScheduleBillVO[] unCommitSchedule(
			ContractScheduleBillVO[] billVOs, PfParameterVO pfParamVO)
			throws BusinessException {
		billVOs = (ContractScheduleBillVO[]) unCommit(billVOs, pfParamVO);
		unOaCommit(billVOs);
		return billVOs;
	}

	private void unOaCommit(ContractScheduleBillVO[] aggVO)
			throws BusinessException {
		for (ContractScheduleBillVO temp : aggVO) {
			if ((temp.getParentVO().getBill_type()).contains(billType)) {
				OaWorkFlowUtil.backOaWorkFlow(temp.getPrimaryKey());
			}
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
