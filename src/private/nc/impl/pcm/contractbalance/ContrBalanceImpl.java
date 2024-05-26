package nc.impl.pcm.contractbalance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.pcm.contract.bp.rule.CheckPayApplyRule;
import nc.bs.pcm.contract.bp.rule.ValidateContrBeginRule;
import nc.bs.pcm.contractbalance.bp.rule.ApproveAfterRule;
import nc.bs.pcm.contractbalance.bp.rule.ContrOprBeforeUnApprove;
import nc.bs.pcm.contractbalance.bp.rule.DeleteAfterRule;
import nc.bs.pcm.contractbalance.bp.rule.SaveBeforeRule;
import nc.bs.pcm.contractbalance.bp.rule.UnApproveForDeductRule;
import nc.bs.pcm.contractbalance.bp.rule.UpdateBeforeRule;
import nc.bs.pmpub.rule.AppendBusiTypeBeforeRule;
import nc.bs.pmpub.rule.Approve4SendFipRule;
import nc.bs.pmpub.rule.UnApproveDelFipMesRule;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pcm.billrule.DeletePayableBillRule;
import nc.impl.pmpub.servicebase.BillBaseImpl;
import nc.impl.pmpub.servicebase.action.ApproveAction;
import nc.impl.pmpub.servicebase.action.DeleteAction;
import nc.impl.pmpub.servicebase.action.InsertAction;
import nc.impl.pmpub.servicebase.action.UnApproveAction;
import nc.impl.pmpub.servicebase.action.UpdateAction;
import nc.itf.pbm.commonrule.BillDelOrUnApprove4BudgetRule;
import nc.itf.pbm.commonrule.BillSaveOrApprove4BudgetRule;
import nc.itf.pbm.commonrule.BillUpdate4BudgetRule;
import nc.itf.pcm.contractbalance.prv.IContrBalance;
import nc.itf.pmbd.pub.IMaterialaccPubService;
import nc.vo.bd.material.MaterialVO;
import nc.vo.bd.supplier.SupplierVO;
import nc.vo.org.OrgVO;
import nc.vo.pcm.contract.ContrHeadVO;
import nc.vo.pcm.contractbalance.ContractBalanceBillVO;
import nc.vo.pcm.contractbalance.ContractBalanceBodyVO;
import nc.vo.pcm.contractbalance.ContractBalanceHeadVO;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.sm.UserVO;
import nc.ws.intf.ContractUtils;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

// 结算单
@SuppressWarnings({ "rawtypes", "unchecked", "restriction", "unused" })
public class ContrBalanceImpl extends BillBaseImpl<ContractBalanceBillVO>
		implements IContrBalance {
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表名

	public ContractBalanceBillVO[] insertBala(ContractBalanceBillVO[] billVOs)
			throws BusinessException {
		before(billVOs);
		return (ContractBalanceBillVO[]) insert(billVOs);
	}

	/*
	 * 需求时间 2023-06-19 请求人 刘力 结算单保存时校验结算金额是否超过年度投资计划对应费用项值，取数关系1、项目+CBS 本次
	 * 结算金额+其他合同（已做结算取结算金额）+其他合同（未做结算取合同金额）组织：使用HTSAVEORG
	 */
	private void before(ContractBalanceBillVO[] billVOs)
			throws BusinessException {
		// TODO Auto-generated method stub
		for (int i = 0; i < billVOs.length; i++) {
			ContractBalanceBillVO contractBalanceBillVO = billVOs[i];
			String pk_org = contractBalanceBillVO.getParentVO().getPk_org();// 组织
			OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
					pk_org);
			// 当前登录用户编码，结算单保存档案，当前档案下可跳出校验
			String user_code = InvocationInfoProxy.getInstance().getUserCode();
			Object confuser = (Object) getHyPubBO().findColValue(
					"bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
							+ "FROM BD_DEFDOCLIST WHERE CODE = 'JSDbc')"
							+ " and code = '" + user_code + "'");
			if (confuser == null) {
				// 查询档案，取校验组织 Object conforg = (Object)
				Object conforg = (Object) getHyPubBO()
						.findColValue(
								"bd_defdoc",
								"code",
								"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
										+ "FROM BD_DEFDOCLIST WHERE CODE = 'HTSAVEORG')"
										+ " and code = '" + pk_org + "'");// 组织
				if (conforg != null) {
					// 调用工具类校验是否可保存
					ContractUtils utils = new ContractUtils();
					String error = utils.checkIfSave(contractBalanceBillVO, 4);
					if (StringUtils.isNotEmpty(error)
							&& !StringUtils.equals("null", error)) {
						throw new BusinessException(error);
					}
				}
			}
		}
	}

	protected void initInsertAction(InsertAction<ContractBalanceBillVO> action) {
		super.initInsertAction(action);
		action.addBeforeRule(new ValidateContrBeginRule());
		action.addBeforeRule(new SaveBeforeRule());
		action.addAfterRule(new BillSaveOrApprove4BudgetRule(0));
		action.addBeforeRule(new AppendBusiTypeBeforeRule());
	}

	public ContractBalanceBillVO[] updateBala(ContractBalanceBillVO[] billVOs,
			ContractBalanceBillVO[] originBillVOs) throws BusinessException {
		before(billVOs);
		return (ContractBalanceBillVO[]) update(billVOs, originBillVOs);
	}

	protected void initUpdateAction(UpdateAction<ContractBalanceBillVO> action) {
		super.initUpdateAction(action);
		action.addBeforeRule(new ValidateContrBeginRule());
		action.addBeforeRule(new UpdateBeforeRule());
		action.addBeforeRule(new BillUpdate4BudgetRule(0));
	}

	public void deleteBala(ContractBalanceBillVO[] billVOs)
			throws BusinessException {
		delete(billVOs);
	}

	protected void initDeleteAction(DeleteAction<ContractBalanceBillVO> action) {
		super.initDeleteAction(action);
		action.addAfterRule(new DeleteAfterRule());
		action.addBeforeRule(new BillDelOrUnApprove4BudgetRule(0));
	}

	public Object approveBala(ContractBalanceBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		return approve(billVOs, pfParamVO);
	}

	protected void initApproveAction(ApproveAction<ContractBalanceBillVO> action) {
		super.initApproveAction(action);
		action.addAfterRule(new BillSaveOrApprove4BudgetRule(1));
		action.addAfterRule(new ApproveAfterRule());
		action.addAfterRule(new Approve4SendFipRule("curr_bal_dif_mny"));
	}

	public ContractBalanceBillVO[] unapproveBala(
			ContractBalanceBillVO[] billVOs, PfParameterVO pfParamVO)
			throws BusinessException {
		return (ContractBalanceBillVO[]) unApprove(billVOs, pfParamVO);
	}

	protected void initUnApproveAction(
			UnApproveAction<ContractBalanceBillVO> action) {
		super.initUnApproveAction(action);
		action.addBeforeRule(new UnApproveForDeductRule());
		action.addBeforeRule(new CheckPayApplyRule());
		action.addBeforeRule(new ContrOprBeforeUnApprove());
		action.addBeforeRule(new BillDelOrUnApprove4BudgetRule(1));
		action.addBeforeRule(new DeletePayableBillRule());
		action.addBeforeRule(new UnApproveDelFipMesRule("curr_bal_dif_mny"));
	}

	public ContractBalanceBillVO[] commitBala(ContractBalanceBillVO[] billVOs)
			throws BusinessException {
		billVOs = (ContractBalanceBillVO[]) commit(billVOs);
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

	private String billType = "4D50";

	private void senOaData(ContractBalanceBillVO[] billVOs)
			throws BusinessException {
		for (ContractBalanceBillVO temp : billVOs) {
			ContractBalanceHeadVO hVO = temp.getParentVO();
			// 期初不传OA Begin_flag
			if ("4".equals(getDef2(temp.getParentVO().getPk_org()))
					&& hVO.getBill_type().contains(billType)
					&& !hVO.getBegin_flag().booleanValue()) {
				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hVO.getPk_org(),
						billType);
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
					workFlowVO.setWorkflowName("结算单");
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

	private JSONArray getMainMap(ContractBalanceHeadVO parentVO)
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
		ContrHeadVO contVO = (ContrHeadVO) getHyPubBO().queryByPrimaryKey(
				ContrHeadVO.class, parentVO.getPk_contr());
		if (null != contVO) {
			// 编码
			list.add(OaWorkFlowUtil.listAddObj("pk_contr_code",
					contVO.getBill_code()));
			// 名称
			list.add(OaWorkFlowUtil.listAddObj("bill_name",
					contVO.getBill_name()));
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
			// 合同金额
			list.add(OaWorkFlowUtil.listAddObj("curr_mny",
					contVO.getNcurrent_mny() + ""));
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
		// 审计经办部门
		String sjjbbm = (String) getHyPubBO().findColValue("bd_defdoc", "name",
				"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getHdef3() + "'");
		list.add(OaWorkFlowUtil.listAddObj("sjjbbm", sjjbbm));
		// 项目审批类型
		if (parentVO.getHdef5() != null) {
			String xmsplxbm = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getHdef5()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("xmsplxbm", xmsplxbm));
			String xmsplx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getHdef5()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("xmsplx", xmsplx));
		}
		// 负责人
		list.add(OaWorkFlowUtil.listAddObj("fzr", parentVO.getHdef1()));
		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	private JSONArray getDtaileDataMap(ContractBalanceBillVO temp)
			throws BusinessException {
		// 获取详细信息
		ContractBalanceBodyVO[] bvos = (ContractBalanceBodyVO[]) temp
				.getChildrenVO();
		Map bodyMap = getBody(bvos);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
		return dtlistString;
	}

	private Map getBody(ContractBalanceBodyVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (ContractBalanceBodyVO temp : bvo) {
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

	public ContractBalanceBillVO[] unCommitBala(
			ContractBalanceBillVO[] billVOs, PfParameterVO pfParamVO)
			throws BusinessException {
		billVOs = (ContractBalanceBillVO[]) unCommit(billVOs, pfParamVO);
		unOaCommit(billVOs);
		return billVOs;
	}

	private void unOaCommit(ContractBalanceBillVO[] aggVO)
			throws BusinessException {
		for (ContractBalanceBillVO temp : aggVO) {
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
