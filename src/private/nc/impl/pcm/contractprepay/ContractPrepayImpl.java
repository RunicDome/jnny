package nc.impl.pcm.contractprepay;

import java.util.List;
import java.util.Map;

import nc.bs.pcm.contract.bp.rule.CheckPayApplyRule;
import nc.bs.pcm.contractprepay.bp.rule.ApproveAfterRule;
import nc.bs.pcm.contractprepay.bp.rule.ApproveValidateRule;
import nc.bs.pcm.contractprepay.bp.rule.DeletePayBillRule;
import nc.bs.pcm.contractprepay.bp.rule.SaveValidateRule;
import nc.bs.pcm.contractprepay.bp.rule.UnApproveAfterRule;
import nc.bs.pcm.contractprepay.bp.rule.UnApproveValidateRule;
import nc.bs.pmpub.rule.AppendBusiTypeBeforeRule;
import nc.bs.pmpub.rule.Approve4SendFipRule;
import nc.bs.pmpub.rule.UnApproveDelFipMesRule;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pcm.contractprepay.rule.InitMutiCurrTypeBeforeRule;
import nc.impl.pmpub.servicebase.BillBaseImpl;
import nc.impl.pmpub.servicebase.action.ApproveAction;
import nc.impl.pmpub.servicebase.action.InsertAction;
import nc.impl.pmpub.servicebase.action.UnApproveAction;
import nc.impl.pmpub.servicebase.action.UpdateAction;
import nc.itf.pcm.contractprepay.prv.IContractprepay;
import nc.vo.bd.supplier.SupplierVO;
import nc.vo.org.OrgVO;
import nc.vo.pcm.contract.ContrHeadVO;
import nc.vo.pcm.contractprepay.ContractPrepayBillVO;
import nc.vo.pcm.contractprepay.ContractPrepayHeadVO;
import nc.vo.pim.project.ProjectHeadVO;
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

// 预付款单（清单发包合同）
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ContractPrepayImpl extends BillBaseImpl<ContractPrepayBillVO>
		implements IContractprepay {
	private static String WorkFId;// OA WorkflowId 流程ID

	// private static String TableName;// OA TableName 表明

	public ContractPrepayImpl() {
	}

	public ContractPrepayBillVO[] insertContractPrepay(
			ContractPrepayBillVO[] billVOs) throws BusinessException {
		return (ContractPrepayBillVO[]) insert(billVOs);
	}

	protected void initInsertAction(InsertAction<ContractPrepayBillVO> action) {
		super.initInsertAction(action);
		action.addBeforeRule(new AppendBusiTypeBeforeRule());
		action.addBeforeRule(new InitMutiCurrTypeBeforeRule());
		action.addBeforeRule(new SaveValidateRule());
	}

	public ContractPrepayBillVO[] updateContractPrepay(
			ContractPrepayBillVO[] billVOs, ContractPrepayBillVO[] originBillVOs)
			throws BusinessException {
		return (ContractPrepayBillVO[]) update(billVOs, originBillVOs);
	}

	protected void initUpdateAction(UpdateAction<ContractPrepayBillVO> action) {
		super.initUpdateAction(action);
		action.addBeforeRule(new InitMutiCurrTypeBeforeRule());
	}

	public void deleteContractPrepay(ContractPrepayBillVO[] billVOs)
			throws BusinessException {
		delete(billVOs);
	}

	public Object approveContractPrepay(ContractPrepayBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		return approve(billVOs, pfParamVO);
	}

	protected void initApproveAction(ApproveAction<ContractPrepayBillVO> action) {
		super.initApproveAction(action);
		action.addBeforeRule(new ApproveValidateRule());
		action.addAfterRule(new ApproveAfterRule());
		action.addAfterRule(new Approve4SendFipRule("curr_prepay_mny"));
	}

	public ContractPrepayBillVO[] unapproveContractPrepay(
			ContractPrepayBillVO[] billVOs, PfParameterVO pfParamVO)
			throws BusinessException {
		return (ContractPrepayBillVO[]) unApprove(billVOs, pfParamVO);
	}

	protected void initUnApproveAction(
			UnApproveAction<ContractPrepayBillVO> action) {
		super.initUnApproveAction(action);
		action.addBeforeRule(new CheckPayApplyRule());
		action.addBeforeRule(new UnApproveValidateRule());
		action.addBeforeRule(new UnApproveAfterRule());
		action.addAfterRule(new DeletePayBillRule());
		action.addBeforeRule(new UnApproveDelFipMesRule("curr_prepay_mny"));
	}

	// 提交
	public ContractPrepayBillVO[] commitContractPrepay(
			ContractPrepayBillVO[] billVOs) throws BusinessException {
		senOaData(billVOs);
		return (ContractPrepayBillVO[]) commit(billVOs);
	}

	private void senOaData(ContractPrepayBillVO[] billVOs)
			throws BusinessException {
		for (ContractPrepayBillVO temp : billVOs) {
			// 材料结算
			ContractPrepayHeadVO hVO = temp.getParentVO();
			String transi_type = hVO.getTransi_type();
			if (transi_type != null && "4".equals(getDef2(hVO.getPk_org()))
					&& "4D46-01".equals(transi_type)) {
				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hVO.getPk_org(),
						"4D46");
				if (oaVo != null && oaVo.getIsdr() == 0) {
					WorkFId = oaVo.getFlowid();
					// TableName = oaVo.getTablename();
					// 获取主表数据
					JSONArray headData = getMainMap(hVO);
					// 获取子表数据
					JSONArray bodyData = null;
					// 构造workflow信息
					OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
					workFlowVO.setPrimaryKey(temp.getPrimaryKey());
					workFlowVO.setPkGroup(hVO.getPk_group());
					workFlowVO.setPkOrg(hVO.getPk_org());
					workFlowVO.setBillMaker(hVO.getBillmaker());
					workFlowVO.setCreator(hVO.getCreator());
					workFlowVO.setBillCode("4D46");
					// 接口获取
					workFlowVO.setWorkflowId(OaWorkFlowUtil
							.getOAFlowID(WorkFId));
					workFlowVO.setWorkflowName("预付款单");
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

	private JSONArray getMainMap(ContractPrepayHeadVO parentVO)
			throws BusinessException {
		// TODO Auto-generated method stub
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// ---------其他字段begin
		// 组织
		String stockName = (String) getHyPubBO().findColValue("org_orgs",
				"name",
				"nvl(dr,0) = 0 and pk_org = '" + parentVO.getPk_org() + "'");
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
		// 单据类型
		String billTypeName = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and pk_billtypeid  = '"
						+ parentVO.getBill_type() + "'");
		list.add(OaWorkFlowUtil.listAddObj("typename", billTypeName));
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
		// 组织币种
		String bbmc = (String) getHyPubBO().findColValue(
				"bd_currtype",
				"name",
				"nvl(dr,0) = 0 and pk_currtype = '"
						+ parentVO.getPk_currtype_org() + "'");
		list.add(OaWorkFlowUtil.listAddObj("zzbz", bbmc));
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

	// 收回
	public ContractPrepayBillVO[] unCommitContractPrepay(
			ContractPrepayBillVO[] billVOs, PfParameterVO pfParamVO)
			throws BusinessException {
		for (ContractPrepayBillVO vo : billVOs) {
			OaWorkFlowUtil.backOaWorkFlow(vo.getParentVO().getPrimaryKey());
		}
		return (ContractPrepayBillVO[]) unCommit(billVOs, pfParamVO);
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
}