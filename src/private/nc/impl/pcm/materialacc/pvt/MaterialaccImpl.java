package nc.impl.pcm.materialacc.pvt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.pcm.contract.bp.rule.CheckPayApplyRule;
import nc.bs.pmpub.rule.AppendBusiTypeBeforeRule;
import nc.bs.pmpub.rule.Approve4SendFipRule;
import nc.bs.pmpub.rule.InitCurrTypeBeforeRule;
import nc.bs.pmpub.rule.UnApproveDelFipMesRule;
import nc.bs.pmpub.rule.ValidateServiceRule;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pcm.billrule.DeletePayableBillRule;
import nc.impl.pcm.materialacc.point.MaterialaccPluginPoint;
import nc.impl.pcm.materialacc.rule.BalanceDateCheckRule;
import nc.impl.pcm.materialacc.rule.SaveBeforRule;
import nc.impl.pcm.materialacc.rule.StatusCheckRule;
import nc.impl.pcm.materialacc.validator.BeforUpdateValidator;
import nc.impl.pcm.materialacc.validator.BeforeUnApproveValidator;
import nc.impl.pim.pimpub.util.MaterialStockOrgQueryUtils;
import nc.impl.pmpub.servicebase.BillBaseImpl;
import nc.impl.pmpub.servicebase.action.ApproveAction;
import nc.impl.pmpub.servicebase.action.CommitAction;
import nc.impl.pmpub.servicebase.action.DeleteAction;
import nc.impl.pmpub.servicebase.action.InsertAction;
import nc.impl.pmpub.servicebase.action.UnApproveAction;
import nc.impl.pmpub.servicebase.action.UnCommitAction;
import nc.impl.pmpub.servicebase.action.UpdateAction;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.itf.pbm.commonrule.BillDelOrUnApprove4BudgetRule;
import nc.itf.pbm.commonrule.BillSaveOrApprove4BudgetRule;
import nc.itf.pcm.contract.pub.IContractService;
import nc.itf.pcm.discontr.pub.IDisContrQuery;
import nc.itf.pcm.materialacc.prv.IMaterialacc;
import nc.itf.pcm.materialacc.prv.MaterialaccPriceVO;
import nc.itf.pim.project.pub.IProjectServiceForPu;
import nc.itf.pmbd.pub.IFieldsForInitCurrType;
import nc.itf.uap.pf.IPfExchangeService;
import nc.itf.uap.pf.busiflow.PfButtonClickContext;
import nc.pubitf.ic.m4d.pm.IMaterialQueryServiceForPM;
import nc.pubitf.ic.m4d.pm.QueryVONnum4DForPCM;
import nc.pubitf.ic.m4d.pm.ResultVONnum4DForPCM;
import nc.pubitf.uapbd.IMaterialPubService_C;
import nc.vo.bd.cust.CustSupplierVO;
import nc.vo.bd.material.MaterialVO;
import nc.vo.bd.material.MaterialVersionVO;
import nc.vo.org.OrgVO;
import nc.vo.pbm.budgetctrl.BudgetCtrlPoint;
import nc.vo.pcm.contract.ContrHeadVO;
import nc.vo.pcm.discontr.DiscontrHeadVO;
import nc.vo.pcm.materialacc.AggMaterialaccVO;
import nc.vo.pcm.materialacc.MaterialaccBodyVO;
import nc.vo.pcm.materialacc.MaterialaccConst;
import nc.vo.pcm.materialacc.MaterialaccHeadVO;
import nc.vo.pcm.materialacc.SecondMaterialBodyVO;
import nc.vo.pim.project.ProjectHeadVO;
import nc.vo.pm.constant.BillStatusConst;
import nc.vo.pm.constant.BillTableNameConst;
import nc.vo.pm.constant.BillTypeConst;
import nc.vo.pm.constant.CommonCharConst;
import nc.vo.pm.constant.CommonKeyConst;
import nc.vo.pm.proxy.PMProxy;
import nc.vo.pm.uap.util.app.BizContext;
import nc.vo.pm.util.ArrayUtil;
import nc.vo.pm.util.StringUtil;
import nc.vo.pm.util.UFDoubleUtils;
import nc.vo.pm.util.app.ModuleInfoQueryUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

/**
 * 材料结算单内部服务实现类
 * 
 * @since 2012-6-26
 * @author xiafy1
 * 
 */
@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
public class MaterialaccImpl extends BillBaseImpl<AggMaterialaccVO> implements
		IMaterialacc {
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表名

	@Override
	public Object approveMaterialacc(AggMaterialaccVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		return this.approve(billVOs, pfParamVO);
	}

	@Override
	// 提交
	public AggMaterialaccVO[] commitMaterialacc(AggMaterialaccVO[] billVOs)
			throws BusinessException {
		senOaData(billVOs);
		return this.commit(billVOs);
	}

	private void senOaData(AggMaterialaccVO[] billVOs) throws BusinessException {
		for (AggMaterialaccVO temp : billVOs) {
			// 材料结算
			MaterialaccHeadVO hVO = temp.getParentVO();
			String transi_type = hVO.getTransi_type();
			if (transi_type != null && "4".equals(getDef2(hVO.getPk_org()))
					&& "4D39-01".equals(transi_type)) {
				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hVO.getPk_org(),
						"4D39");
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
					workFlowVO.setBillCode("4D39");
					// 接口获取
					workFlowVO.setWorkflowId(OaWorkFlowUtil
							.getOAFlowID(WorkFId));
					workFlowVO.setWorkflowName("材料结算");
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

	private JSONArray getMainMap(MaterialaccHeadVO parentVO)
			throws BusinessException {
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
		// 合同编码
		list.add(OaWorkFlowUtil.listAddObj("pk_contr_code",
				parentVO.getContract_code()));
		// 合同名称
		list.add(OaWorkFlowUtil.listAddObj("bill_name",
				parentVO.getContract_name()));
		// 施工单位（供应商）
		CustSupplierVO custVO = (CustSupplierVO) getHyPubBO()
				.queryByPrimaryKey(CustSupplierVO.class,
						parentVO.getPk_supplier());
		if (null != custVO) {
			// 供应商编码
			list.add(OaWorkFlowUtil.listAddObj("pk_supplier_code",
					custVO.getCode()));
			// 供应商名称
			list.add(OaWorkFlowUtil.listAddObj("pk_supplier_name",
					custVO.getName()));
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

	private JSONArray getDtaileDataMap(AggMaterialaccVO temp)
			throws BusinessException {
		// 获取详细信息
		MaterialaccBodyVO[] bvos = (MaterialaccBodyVO[]) getHyPubBO()
				.queryByCondition(
						MaterialaccBodyVO.class,
						"nvl(dr,0) = 0 and pk_materialacc = '"
								+ temp.getPrimaryKey() + "'");
		Map bodyMap = getBody(bvos);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
		return dtlistString;
	}

	private Map getBody(MaterialaccBodyVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (MaterialaccBodyVO temp : bvo) {
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
			if (temp.getPk_cbsnode() != null) {
				String cbsName = (String) getHyPubBO().findColValue(
						"bd_cbsnode",
						"name",
						"nvl(dr,0) = 0 and pk_cbsnode  = '"
								+ temp.getPk_cbsnode() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"cbsmc", cbsName));
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

	public String getDef2(String pk_org) throws BusinessException {
		String def2 = "";
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		if (null != orgVO) {
			def2 = orgVO.getDef2();
		}
		return def2;
	}

	@Override
	public void deleteMaterialacc(AggMaterialaccVO[] billVOs)
			throws BusinessException {
		this.delete(billVOs);

	}

	@Override
	public AggMaterialaccVO[] insertMaterialacc(AggMaterialaccVO[] billVOs)
			throws BusinessException {
		return this.insert(billVOs);
	}

	@Override
	public AggMaterialaccVO[] unApproveMaterialacc(AggMaterialaccVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		return this.unApprove(billVOs, pfParamVO);
	}

	@Override
	// 收回
	public AggMaterialaccVO[] unCommitMaterialacc(AggMaterialaccVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		for (AggMaterialaccVO vo : billVOs) {
			OaWorkFlowUtil.backOaWorkFlow(vo.getParentVO().getPrimaryKey());
		}
		return this.unCommit(billVOs, pfParamVO);
	}

	@Override
	public AggMaterialaccVO[] updateMaterialacc(AggMaterialaccVO[] billVOs,
			AggMaterialaccVO[] originBillVOs) throws BusinessException {
		return this.update(billVOs, originBillVOs);
	}

	@Override
	protected void initInsertAction(InsertAction<AggMaterialaccVO> action) {
		action.setPluginPoint(MaterialaccPluginPoint.INSERT);
		super.initInsertAction(action);

		// 判断表体页签是否都为空
		action.addBeforeRule(new SaveBeforRule());
		action.addBeforeRule(new BalanceDateCheckRule());
		// 判断维度合同编码+项目+供应商内是否存在未审批通过的单据
		action.addBeforeRule(new StatusCheckRule());
		action.addBeforeRule(new InitCurrTypeBeforeRule<AggMaterialaccVO>(
				new FieldsForInitCurrType()));
		action.addBeforeRule(new InitCurrTypeBeforeRule<AggMaterialaccVO>(
				new FieldsForInitCurrType2()));
		// 设置业务流程信息
		action.addBeforeRule(new AppendBusiTypeBeforeRule<AggMaterialaccVO>());
	}

	@Override
	protected void initUpdateAction(UpdateAction<AggMaterialaccVO> action) {
		action.setPluginPoint(MaterialaccPluginPoint.UPDATE);
		super.initUpdateAction(action);
		// 判断表体页签是否都为空
		action.addBeforeRule(new SaveBeforRule());
		action.addBeforeRule(new BalanceDateCheckRule());
		action.addBeforeRule(new InitCurrTypeBeforeRule<AggMaterialaccVO>(
				new FieldsForInitCurrType()));
		action.addBeforeRule(new InitCurrTypeBeforeRule<AggMaterialaccVO>(
				new FieldsForInitCurrType2()));
		action.addBeforeRule(new ValidateServiceRule<AggMaterialaccVO>(
				new nc.bs.uif2.validation.Validator[] { new BeforUpdateValidator() }));
	}

	@Override
	protected void initDeleteAction(DeleteAction<AggMaterialaccVO> action) {
		action.setPluginPoint(MaterialaccPluginPoint.DELETE);
		super.initDeleteAction(action);
	}

	@Override
	protected void initUnCommitAction(UnCommitAction<AggMaterialaccVO> action) {
		action.setPluginPoint(MaterialaccPluginPoint.UNSEND_APPROVE);
		super.initUnCommitAction(action);
	}

	@Override
	protected void initCommitAction(CommitAction<AggMaterialaccVO> action) {
		action.setPluginPoint(MaterialaccPluginPoint.SEND_APPROVE);
		super.initCommitAction(action);
		// 判断表体页签是否都为空
		action.addBeforeRule(new SaveBeforRule());
	}

	@Override
	protected void initApproveAction(ApproveAction<AggMaterialaccVO> action) {
		action.setPluginPoint(MaterialaccPluginPoint.APPROVE);
		super.initApproveAction(action);
		// 审核后预算校验和回写
		// action.addAfterRule(new ApproveForBudgetRule());
		action.addAfterRule(new BillSaveOrApprove4BudgetRule<AggMaterialaccVO>(
				BudgetCtrlPoint.check_control));
		action.addAfterRule(new Approve4SendFipRule<AggMaterialaccVO>(
				MaterialaccHeadVO.ACCDIF_MONEY));

	}

	@Override
	protected void initUnApproveAction(UnApproveAction<AggMaterialaccVO> action) {
		action.setPluginPoint(MaterialaccPluginPoint.UNAPPROVE);
		super.initUnApproveAction(action);
		action.addBeforeRule(new ValidateServiceRule<AggMaterialaccVO>(
				new nc.bs.uif2.validation.Validator[] { new BeforeUnApproveValidator() }));
		// 取消审批时，校验下游是否生成付款申请
		action.addBeforeRule(new CheckPayApplyRule<AggMaterialaccVO>());
		// 只能you一张自由态单据
		action.addBeforeRule(new StatusCheckRule());

		// 逆序弃审规则
		action.addBeforeRule(new BalanceDateCheckRule());
		action.addBeforeRule(new BillDelOrUnApprove4BudgetRule<AggMaterialaccVO>(
				BudgetCtrlPoint.check_control));
		// 弃审后删除应付单
		action.addBeforeRule(new DeletePayableBillRule<AggMaterialaccVO>());
		action.addBeforeRule(new UnApproveDelFipMesRule<AggMaterialaccVO>(
				MaterialaccHeadVO.ACCDIF_MONEY));
	}

	/**
	 * 
	 * 
	 * 根据合同主键查询合同，然后在合同上取项目+项目任务+供应商，调供应链接口加载表体。
	 * 
	 */
	@Override
	public MaterialaccBodyVO[] loadAsignNum(MaterialaccHeadVO headVO)
			throws BusinessException {
		try {
			if (ModuleInfoQueryUtil.isICEnabled()) {
				// 构造取价参数
				QueryVONnum4DForPCM queryVO = getPricePara(headVO);
				// 调供应链接口查出库单数据
				ResultVONnum4DForPCM[] resultVos = PMProxy.lookup(
						IMaterialQueryServiceForPM.class).queryNnum4DForPCM(
						queryVO);
				// 出入库数据计算
				MaterialaccBodyVO[] bodyVOs = getBalanceData(resultVos,
						headVO.getSrc_pk_bill(),
						queryVO.getCconstructvendorid(),
						queryVO.getCprojectid());
				// 去掉没有实际发生数量的物料
				List<MaterialaccBodyVO> returnVOs = new ArrayList<MaterialaccBodyVO>();
				for (MaterialaccBodyVO bodyVO : bodyVOs) {
					UFDouble canceNum = bodyVO.getCurr_cance_num();
					UFDouble drawNum = bodyVO.getCurr_draw_num();
					if (!(UFDoubleUtils.isNullOrZero(drawNum) && UFDoubleUtils
							.isNullOrZero(canceNum))) {
						returnVOs.add(bodyVO);
					}
				}
				return returnVOs.toArray(new MaterialaccBodyVO[0]);

			}
		} catch (BusinessException e1) {
			ExceptionUtils.wrappException(e1);
		}
		return null;
	}

	@Override
	public MaterialaccPriceVO loadAsignPrice(
			MaterialaccPriceVO materialaccPriceVO) throws BusinessException {
		return null;
	}

	@Override
	public AggregatedValueObject[] cancelStock(AggMaterialaccVO billVO)
			throws BusinessException {
		MaterialaccBodyVO[] bodyVOs = (MaterialaccBodyVO[]) billVO
				.getTableVO(MaterialaccConst.FIRSTBODYMATERIAL);
		// 符合退库申请条件的数据
		List<MaterialaccBodyVO> needCancelStockbodyVOs = new ArrayList<MaterialaccBodyVO>();
		// 得到 物料
		List<String> pk_material = new ArrayList<String>();
		for (MaterialaccBodyVO bodyVO : bodyVOs) {
			// 过滤差异量为零的数据
			if (!UFDoubleUtils.isNullOrZero(bodyVO.getDiversity_num())) {
				needCancelStockbodyVOs.add(bodyVO);
				pk_material.add(bodyVO.getPk_material());
			}
		}
		Map<String, String[]> materialStockOrg = MaterialStockOrgQueryUtils
				.queryStockOrg(billVO.getParentVO().getPk_org(), billVO
						.getParentVO().getPk_project(), pk_material
						.toArray(new String[0]));
		// 校验选中行中库存组织是否一致
		for (int i = 0; i < pk_material.size(); i++) {
			if (!materialStockOrg.get(pk_material.get(0))[0]
					.equals(materialStockOrg.get(pk_material.get(i))[0])) {
				ExceptionUtils
						.wrappBusinessException(nc.vo.ml.NCLangRes4VoTransl
								.getNCLangRes().getStrByID("materialbalance_0",
										"04820009-0027")/*
														 * @res
														 * "选中的行中库存组织要一致，才能进行退库申请"
														 */);
			}
			needCancelStockbodyVOs.get(i).setStockorg(
					materialStockOrg.get(pk_material.get(i))[0]);
			needCancelStockbodyVOs.get(i).setStockorg_v(
					materialStockOrg.get(pk_material.get(i))[1]);
		}
		if (needCancelStockbodyVOs.size() == 0) {
			ExceptionUtils.wrappBusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("materialbalance_0",
							"04820009-0014")/* @res "选中物料没有多领，不能进行退库申请" */);
		}
		AggMaterialaccVO materialaccBillVO = new AggMaterialaccVO();
		materialaccBillVO.setParentVO(billVO.getParentVO());
		materialaccBillVO.setChildrenVO(needCancelStockbodyVOs
				.toArray(new MaterialaccBodyVO[0]));
		AggregatedValueObject[] storeReqVos = NCLocator
				.getInstance()
				.lookup(IPfExchangeService.class)
				.runChangeDataAryNeedClassify(BillTypeConst.MATERIALACC,
						BillTypeConst.STOCKOUTAPPLYBILL,
						new AggMaterialaccVO[] { materialaccBillVO }, null,
						PfButtonClickContext.ClassifyByItfdef);
		return storeReqVos;
	}

	@Override
	public AggregatedValueObject[] forTransf(AggMaterialaccVO billVO)
			throws BusinessException {
		MaterialaccBodyVO[] bodyVOs = (MaterialaccBodyVO[]) billVO
				.getTableVO(MaterialaccConst.FIRSTBODYMATERIAL);
		// 符合转料申请条件的数据
		List<MaterialaccBodyVO> needCancelStockbodyVOs = new ArrayList<MaterialaccBodyVO>();
		// 物料PK数组
		List<String> pk_materialList = new ArrayList<String>();
		for (MaterialaccBodyVO bodyVO : bodyVOs) {
			needCancelStockbodyVOs.add(bodyVO);
			pk_materialList.add(bodyVO.getPk_material());
		}
		Map<String, String[]> materialStockOrg = MaterialStockOrgQueryUtils
				.queryStockOrg(billVO.getParentVO().getPk_org(), billVO
						.getParentVO().getPk_project(), pk_materialList
						.toArray(new String[0]));
		// 校验选中行中库存组织是否一致
		for (int i = 0; i < pk_materialList.size(); i++) {
			if (!materialStockOrg.get(pk_materialList.get(0))[0]
					.equals(materialStockOrg.get(pk_materialList.get(i))[0])) {
				ExceptionUtils
						.wrappBusinessException(nc.vo.ml.NCLangRes4VoTransl
								.getNCLangRes().getStrByID("materialbalance_0",
										"04820009-0028")/*
														 * @res
														 * "选中的行中库存组织要一致，才能进行转料申请"
														 */);
			}
			needCancelStockbodyVOs.get(i).setStockorg(
					materialStockOrg.get(pk_materialList.get(i))[0]);
			needCancelStockbodyVOs.get(i).setStockorg_v(
					materialStockOrg.get(pk_materialList.get(i))[1]);
		}
		if (needCancelStockbodyVOs.size() == 0) {
			ExceptionUtils.wrappBusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("materialbalance_0",
							"04820009-0016")/* @res "选中物料没有多领，不能进行转料申请" */);
		}
		AggMaterialaccVO materialaccBillVO = new AggMaterialaccVO();
		materialaccBillVO.setParentVO(billVO.getParentVO());
		materialaccBillVO.setChildrenVO(needCancelStockbodyVOs
				.toArray(new MaterialaccBodyVO[0]));
		AggregatedValueObject[] reqVos = NCLocator
				.getInstance()
				.lookup(IPfExchangeService.class)
				.runChangeDataAryNeedClassify(BillTypeConst.MATERIALACC,
						BillTypeConst.MATERIALTRANSF,
						new AggMaterialaccVO[] { materialaccBillVO }, null,
						PfButtonClickContext.ClassifyByItfdef);
		return reqVos;

	}

	/**
	 * * 获取取价参数
	 * 
	 * @param headVO
	 * @return
	 * @throws BusinessException
	 */
	private QueryVONnum4DForPCM getPricePara(MaterialaccHeadVO headVO)
			throws BusinessException {
		String pk_project = headVO.getPk_project();
		String pk_supplier = headVO.getPk_supplier();
		String pk_group = headVO.getPk_group();
		// 根据合同主键查询合同编码
		// String contrCode = getContrCodeByPk(headVO.getSrc_pk_bill(),
		// headVO.getSrc_bill_type());
		String contrCode = "~";
		QueryVONnum4DForPCM queryVO = new QueryVONnum4DForPCM();
		queryVO.setCprojectid(pk_project);
		queryVO.setGroupid(pk_group);
		queryVO.setCconstructvendorid(pk_supplier);
		queryVO.setVcontractcode(contrCode);
		queryVO.setbIncludeProjTaskId(false);
		try {
			ProjectHeadVO[] projectHeadVOs = getProjectService()
					.queryProHeadVOByPK(new String[] { pk_project });
			// 项目创建日期作为查询开始日期
			queryVO.setBeginDate(projectHeadVOs[0].getCreationtime().getDate());
		} catch (BusinessException e) {
			ExceptionUtils.wrappException(e);
		}
		queryVO.setEndDate(BizContext.getInstance().getBizDate());
		return queryVO;
	}

	/**
	 * 查询历史结算数据
	 * 
	 * @param pk_contr
	 * @param pk_project
	 * @param pk_supplier
	 * @return
	 * @throws BusinessException
	 */
	private Map<String, BalanceData> getMaterBalData(String pk_contr,
			String pk_project, String pk_supplier) throws BusinessException {
		StringBuffer querySql = new StringBuffer();
		if (StringUtil.isEmptyWithTrim(pk_contr)) {
			pk_contr = "~";
		}
		querySql.append("SELECT ").append(MaterialaccBodyVO.PK_MATERIAL)
				.append(" , ").append(MaterialaccBodyVO.PK_CBSNODE)
				.append(",SUM(").append(MaterialaccBodyVO.CURR_DRAW_NUM)
				.append("),SUM(").append(MaterialaccBodyVO.CURR_CANCE_NUM)
				.append("),SUM(").append(MaterialaccBodyVO.DIVERSITY_NUM)
				.append("),SUM(").append(MaterialaccBodyVO.CURR_NET_NUM)
				.append(") FROM ").append(BillTableNameConst.MATERIALCC)
				.append(" T1 ").append(",")
				.append(BillTableNameConst.MATERIALCC_B).append(" T2 ")
				.append(" WHERE T1.").append(MaterialaccBodyVO.PK_MATERIALACC)
				.append("= T2.").append(MaterialaccHeadVO.PK_MATERIALACC)
				.append(" AND T1.").append(MaterialaccHeadVO.PK_PROJECT)
				.append(" = '").append(pk_project).append("' AND T1.")
				.append(MaterialaccHeadVO.PK_SUPPLIER).append(" ='")
				.append(pk_supplier).append("' AND T1.")
				.append(MaterialaccHeadVO.SRC_PK_BILL).append(" ='")
				.append(pk_contr).append("' AND T1.")
				.append(MaterialaccHeadVO.BILL_STATUS).append(" = '")
				.append(BillStatusConst.approved)
				.append("' AND T2.DR = 0 AND T2.DR = 0 ")
				.append(" GROUP BY T2.").append(MaterialaccBodyVO.PK_MATERIAL)
				.append(", T2.").append(MaterialaccBodyVO.PK_CBSNODE);
		DataAccessUtils dao = new DataAccessUtils();
		// 查询数据，按照物料及CBS分组，累计领用 退库量
		IRowSet rowset = dao.query(querySql.toString());
		Map<String, BalanceData> balanceDataMap = new HashMap<String, BalanceData>();
		while (rowset.next()) {
			BalanceData balanceData = new BalanceData();
			String pk_material = (String) rowset.getObject(0);
			String pk_cbsnode = (String) rowset.getObject(1);
			UFDouble drawnumber = UFDoubleUtils.objToUFDouble(rowset
					.getObject(2));
			UFDouble returnnumber = UFDoubleUtils.objToUFDouble(rowset
					.getObject(3));
			UFDouble diveNumber = UFDoubleUtils.objToUFDouble(rowset
					.getObject(4));
			UFDouble netNumber = UFDoubleUtils.objToUFDouble(rowset
					.getObject(5));
			balanceData.setReturnnumber(returnnumber);
			balanceData.setDrawnumber(drawnumber);
			balanceData.setDivenumber(diveNumber);
			balanceData.setPk_cbsnode(pk_cbsnode);
			balanceData.setPk_material(pk_material);
			balanceData.setNetnumber(netNumber);
			balanceDataMap.put(pk_material + pk_cbsnode, balanceData);
		}
		return balanceDataMap;

	}

	/**
	 * 通过合同主键查询合同编码
	 * 
	 * @param pk_contr
	 * @return
	 * @throws BusinessException
	 */
	private String getContrCodeByPk(String pk_contr, String billType)
			throws BusinessException {
		String contrCode = CommonCharConst.DEFAULT_NULL_VALUE;
		if (StringUtil.isNotEmpty(billType)) {
			StringBuffer condition = new StringBuffer();
			String[] fileds = new String[] { CommonKeyConst.BILL_CODE };
			// 根据合同主键查询合同编码
			if (BillTypeConst.CONTR.equals(billType)) {
				// 根据主键查询清单合同数据
				condition.append(ContrHeadVO.PK_CONTR).append(" = '")
						.append(pk_contr).append("' ");
				@SuppressWarnings("deprecation")
				Map<String, ContrHeadVO> contrMap = getContrService()
						.queryContrHeadVOByPK(new String[] { pk_contr });
				contrCode = contrMap.get(pk_contr).getBill_code();
			} else {
				// 根据主键查询总价合同数据
				condition.append(DiscontrHeadVO.PK_DISCONTR).append(" = '")
						.append(pk_contr).append("' ");
				DiscontrHeadVO[] headVOs = getDisContrService()
						.queryHeadVOByCondition(fileds, condition.toString());
				contrCode = headVOs[0].getBill_code();
			}
		}
		return contrCode;
	}

	/**
	 * 本次结算数据
	 * 
	 * @return
	 * @throws BusinessException
	 */
	private MaterialaccBodyVO[] getBalanceData(
			ResultVONnum4DForPCM[] resultVos, String pk_contr,
			String pk_supplier, String pk_project) throws BusinessException {
		// 上次结算数据
		Map<String, BalanceData> historyBalData = getMaterBalData(pk_contr,
				pk_project, pk_supplier);
		List<String> materialList = new ArrayList<String>();
		for (int i = 0; i < resultVos.length; i++) {
			materialList.add(resultVos[i].getCmaterialoid());
		}
		for (String key : historyBalData.keySet()) {
			materialList.add(key.substring(0, 20));
		}
		// 通过物料OID加载物料VID
		Map<String, MaterialVersionVO> materialOid_Vidmap = PMProxy.lookup(
				IMaterialPubService_C.class).queryMaterialBaseInfoByOidPks(
				materialList.toArray(new String[0]),
				new String[] { MaterialVersionVO.PK_SOURCE,
						MaterialVersionVO.PK_MATERIAL });
		MaterialaccBodyVO[] bodyVOs;
		// 没有历史数据
		if (historyBalData.isEmpty()) {
			List<MaterialaccBodyVO> bodyList = new ArrayList<MaterialaccBodyVO>();
			for (ResultVONnum4DForPCM data : resultVos) {
				MaterialaccBodyVO body = new MaterialaccBodyVO();
				// //上期累计实际用量=0
				body.setAttributeValue(MaterialaccBodyVO.PRIOR_ACTUAL_NUM,
						UFDouble.ZERO_DBL);
				// 累计实际用量默认=领用量-退库量
				body.setAttributeValue(MaterialaccBodyVO.TOT_ACTUAL_NUM,
						UFDoubleUtils.add(data.getBlueNum(), data.getRedNum()));
				// 累计领用量=领用量
				body.setAttributeValue(MaterialaccBodyVO.TOT_DRAW_NUM,
						data.getBlueNum());
				// 累计退库量=退库量
				body.setAttributeValue(MaterialaccBodyVO.TOT_CANCE_NUM,
						data.getRedNum());
				// 累计净领用量 = 领用量-退库量
				body.setAttributeValue(MaterialaccBodyVO.TOT_NET_NUM,
						UFDoubleUtils.add(data.getBlueNum(), data.getRedNum()));
				// 本期领用量 = 领用量
				body.setAttributeValue(MaterialaccBodyVO.CURR_DRAW_NUM,
						data.getBlueNum());
				// 本期退库量 = 退库量
				body.setAttributeValue(MaterialaccBodyVO.CURR_CANCE_NUM,
						data.getRedNum());
				// 本期净领用量=领用量-退库量
				body.setAttributeValue(MaterialaccBodyVO.CURR_NET_NUM,
						UFDoubleUtils.add(data.getBlueNum(), data.getRedNum()));
				// 本期实际用量 = 本期领用 - 本期退库
				body.setAttributeValue(MaterialaccBodyVO.CURR_ACTUAL_NUM,
						body.getAttributeValue(MaterialaccBodyVO.CURR_NET_NUM));
				// 差异量 默认=0
				body.setAttributeValue(MaterialaccBodyVO.DIVERSITY_NUM,
						UFDouble.ZERO_DBL);
				body.setPk_material(data.getCmaterialoid());
				String materialPK = materialOid_Vidmap.get(
						body.getPk_material()).getPk_material();
				body.setPk_material_v(materialPK);
				body.setPk_cbsnode(data.getPk_cbsnode());
				body.setPk_wbs(data.getCprojecttaskid());
				bodyList.add(body);
			}
			bodyVOs = bodyList.toArray(new MaterialaccBodyVO[0]);
		} else {
			// 根据历史结算数据，计算本次结算数量
			bodyVOs = computeMaterial(historyBalData, resultVos,
					materialOid_Vidmap);
		}
		return bodyVOs;
	}

	/**
	 * 计算本次领用及退库数量
	 * 
	 * @param historyBalData
	 * @param resultVos
	 * @return
	 */
	private MaterialaccBodyVO[] computeMaterial(
			Map<String, BalanceData> historyBalData,
			ResultVONnum4DForPCM[] resultVos, Map<String, MaterialVersionVO> map) {
		List<MaterialaccBodyVO> bodyList = new ArrayList<MaterialaccBodyVO>();
		// 累计领用量
		// UFDouble tot_draw_num = UFDouble.ZERO_DBL;
		for (ResultVONnum4DForPCM data : resultVos) {
			String pk_cbsnode = data.getPk_cbsnode();
			String pk_material = data.getCmaterialoid();
			// 物料+CBS维度的历史结算数据
			BalanceData balanceData = historyBalData.get(pk_material
					+ pk_cbsnode);
			MaterialaccBodyVO body = new MaterialaccBodyVO();
			if (null == balanceData) {
				body.setPk_material(data.getCmaterialoid());
				String materialPK = map.get(body.getPk_material())
						.getPk_material();
				body.setPk_material_v(materialPK);
				// 上期累计实际用量=0
				body.setAttributeValue(MaterialaccBodyVO.PRIOR_ACTUAL_NUM,
						UFDouble.ZERO_DBL);
				// 累计实际用量默认=领用量-退库量
				body.setAttributeValue(MaterialaccBodyVO.TOT_ACTUAL_NUM,
						UFDoubleUtils.add(data.getBlueNum(), data.getRedNum()));
				// 累计领用量=领用量
				body.setAttributeValue(MaterialaccBodyVO.TOT_DRAW_NUM,
						data.getBlueNum());
				// 累计退库量=退库量
				body.setAttributeValue(MaterialaccBodyVO.TOT_CANCE_NUM,
						data.getRedNum());
				// 累计净领用量 = 领用量-退库量
				body.setAttributeValue(MaterialaccBodyVO.TOT_NET_NUM,
						UFDoubleUtils.add(data.getBlueNum(), data.getRedNum()));
				// 本期领用量 = 领用量
				body.setAttributeValue(MaterialaccBodyVO.CURR_DRAW_NUM,
						data.getBlueNum());
				// 本期退库量 = 退库量
				body.setAttributeValue(MaterialaccBodyVO.CURR_CANCE_NUM,
						data.getRedNum());
				// 本期净领用量=领用量-退库量
				body.setAttributeValue(MaterialaccBodyVO.CURR_NET_NUM,
						UFDoubleUtils.add(data.getBlueNum(), data.getRedNum()));
			} else {
				UFDouble darwNumber = balanceData.getDrawnumber();
				UFDouble returnNumber = balanceData.getReturnnumber();
				UFDouble diveNumber = balanceData.getDivenumber();
				body.setAttributeValue(MaterialaccBodyVO.PK_MATERIAL,
						balanceData.getPk_material());
				String materialPK = map.get(body.getPk_material())
						.getPk_material();
				body.setPk_material_v(materialPK);
				// 累计领用量=blueNum//上次累计领用量+出库量
				body.setAttributeValue(MaterialaccBodyVO.TOT_DRAW_NUM,
						data.getBlueNum());
				// UFDoubleUtils.add(tot_draw_num, data.getBlueNum()));
				// 累计退库量=退库量
				body.setAttributeValue(MaterialaccBodyVO.TOT_CANCE_NUM,
						data.getRedNum());
				// 本期领用量 = 累计领用量-上次累计领用
				// body.setAttributeValue(MaterialaccBodyVO.CURR_DRAW_NUM,
				// UFDoubleUtils.sub(data.getBlueNum(), darwNumber));
				body.setAttributeValue(
						MaterialaccBodyVO.CURR_DRAW_NUM,
						UFDoubleUtils.sub(
								(UFDouble) body
										.getAttributeValue(MaterialaccBodyVO.TOT_DRAW_NUM),
								darwNumber));
				// 本退库用量 = 库用量-累计库用量
				body.setAttributeValue(MaterialaccBodyVO.CURR_CANCE_NUM,
						UFDoubleUtils.sub(data.getRedNum(), returnNumber));
				// 本期净领用量 = 本期领用量 - 本期退库量 = 领用量-累计领用量 - (库用量-累计库用量)
				body.setAttributeValue(
						MaterialaccBodyVO.CURR_NET_NUM,
						UFDoubleUtils.add(
								(UFDouble) body
										.getAttributeValue(MaterialaccBodyVO.CURR_DRAW_NUM),
								(UFDouble) body
										.getAttributeValue(MaterialaccBodyVO.CURR_CANCE_NUM)));
				// 本期实际用量 = 本期领用 - 本期退库
				body.setAttributeValue(MaterialaccBodyVO.CURR_ACTUAL_NUM,
						body.getAttributeValue(MaterialaccBodyVO.CURR_NET_NUM));
				// 累计实际用量 = 上期累计实际用量+本期实际用量
				body.setAttributeValue(
						MaterialaccBodyVO.TOT_ACTUAL_NUM,
						UFDoubleUtils
								.add(darwNumber,
										returnNumber,
										diveNumber,
										(UFDouble) body
												.getAttributeValue(MaterialaccBodyVO.CURR_ACTUAL_NUM)));
				// 上期累计实际用量 = 累计领用-累计退库-累计差异
				body.setAttributeValue(MaterialaccBodyVO.PRIOR_ACTUAL_NUM,
						UFDoubleUtils.add(darwNumber, returnNumber, diveNumber));
				// 累计净领料量
				body.setAttributeValue(
						MaterialaccBodyVO.TOT_NET_NUM,
						UFDoubleUtils.add(
								balanceData.getNetnumber(),
								(UFDouble) body
										.getAttributeValue(MaterialaccBodyVO.CURR_NET_NUM)));
				// 差异量默认 = 0
				body.setAttributeValue(MaterialaccBodyVO.DIVERSITY_NUM,
						UFDouble.ZERO_DBL);

			}
			/*
			 * tot_draw_num = tot_draw_num.add((UFDouble) body
			 * .getAttributeValue(MaterialaccBodyVO.TOT_DRAW_NUM));
			 */
			body.setPk_material(data.getCmaterialoid());
			body.setPk_cbsnode(data.getPk_cbsnode());
			body.setPk_wbs(data.getCprojecttaskid());
			bodyList.add(body);
		}
		if (ArrayUtil.isEmpty(resultVos)) {
			for (String key : historyBalData.keySet()) {
				BalanceData balanceData = historyBalData.get(key);
				MaterialaccBodyVO body = new MaterialaccBodyVO();
				body.setPk_material(key.substring(0, 20));
				body.setPk_material_v(map.get(body.getPk_material())
						.getPk_material());
				body.setAttributeValue(MaterialaccBodyVO.PRIOR_ACTUAL_NUM,
						UFDoubleUtils.sub(balanceData.getDrawnumber(),
								balanceData.getReturnnumber()));
				body.setAttributeValue(MaterialaccBodyVO.TOT_ACTUAL_NUM,
						UFDouble.ZERO_DBL);
				body.setAttributeValue(MaterialaccBodyVO.TOT_DRAW_NUM,
						UFDouble.ZERO_DBL);
				body.setAttributeValue(MaterialaccBodyVO.TOT_CANCE_NUM,
						UFDouble.ZERO_DBL);
				body.setAttributeValue(MaterialaccBodyVO.TOT_NET_NUM,
						UFDouble.ZERO_DBL);
				body.setAttributeValue(
						MaterialaccBodyVO.CURR_DRAW_NUM,
						UFDoubleUtils.sub(UFDouble.ZERO_DBL,
								balanceData.getDrawnumber()));
				body.setAttributeValue(
						MaterialaccBodyVO.CURR_CANCE_NUM,
						UFDoubleUtils.sub(UFDouble.ZERO_DBL,
								balanceData.getReturnnumber()));
				body.setAttributeValue(MaterialaccBodyVO.CURR_NET_NUM,
						UFDoubleUtils.sub(balanceData.getDrawnumber(),
								balanceData.getReturnnumber()));
				body.setAttributeValue(
						MaterialaccBodyVO.CURR_ACTUAL_NUM,
						UFDoubleUtils.add(body.getCurr_draw_num(),
								body.getCurr_cance_num()));
				body.setAttributeValue(MaterialaccBodyVO.DIVERSITY_NUM,
						UFDouble.ZERO_DBL);
				bodyList.add(body);
			}
		}
		return bodyList.toArray(new MaterialaccBodyVO[0]);
	}

	/**
	 * 项目档案查询服务
	 * 
	 * @return
	 */
	private IProjectServiceForPu getProjectService() {
		return PMProxy.lookup(IProjectServiceForPu.class);
	}

	/**
	 * 获取总价合同对外服务
	 * 
	 * @return
	 */
	private IDisContrQuery getDisContrService() {
		return PMProxy.lookup(IDisContrQuery.class);
	}

	/**
	 * 获取清单合同对外服务
	 * 
	 * @return
	 */
	private IContractService getContrService() {
		return PMProxy.lookup(IContractService.class);
	}

	class FieldsForInitCurrType2 extends FieldsForInitCurrType {
		@Override
		public Class<? extends ISuperVO> getBodyVOClass() {
			return SecondMaterialBodyVO.class;
		}
	}

	class FieldsForInitCurrType implements IFieldsForInitCurrType {

		@Override
		public String getPk_currtype_orig() {
			return null;
		}

		@Override
		public String getExchangeRate() {
			return null;
		}

		@Override
		public String getPk_currtype() {
			return MaterialaccHeadVO.PK_CURRTYPE;
		}

		@Override
		public String[] getHeadOrigFields() {
			return null;
		}

		@Override
		public String[] getHeadFields() {
			return new String[] { MaterialaccHeadVO.MATERIAL_MONEY,
					MaterialaccHeadVO.SECONDMATE_MONEY,
					MaterialaccHeadVO.ACTUAL_DEDUCT_MONEY };
		}

		@Override
		public String[] getHeadGroupFields() {
			return new String[] { MaterialaccHeadVO.MATERIAL_MONEY_GROUP,
					MaterialaccHeadVO.SECONDMATE_MONEY_GROUP,
					MaterialaccHeadVO.ACTUAL_DEDUCT_MONEY_GROUP };
		}

		@Override
		public String[] getHeadGlobalFields() {
			return new String[] { MaterialaccHeadVO.MATERIAL_MONEY_GLOBAL,
					MaterialaccHeadVO.SECONDMATE_MONEY_GLOBAL,
					MaterialaccHeadVO.ACTUAL_DEDUCT_MONEY_GLOBAL };
		}

		@Override
		public String[] getBodyOrigFields() {
			return null;
		}

		@Override
		public String[] getBodyFields() {
			return new String[] { MaterialaccBodyVO.MONEY };
		}

		@Override
		public String[] getBodyGroupFields() {
			return new String[] { MaterialaccBodyVO.MONEY_GROUP };
		}

		@Override
		public String[] getBodyGlobalFields() {
			return new String[] { MaterialaccBodyVO.MONEY_GLOBAL };
		}

		@Override
		public Class<? extends ISuperVO> getBodyVOClass() {
			return MaterialaccBodyVO.class;
		}
	}

	class BalanceData {
		/** 物料OID */
		private String pk_material;
		/** 物料VID */
		private String pk_material_v;
		/** 累计领用量 */
		private UFDouble drawnumber;
		/** 累计退库量 */
		private UFDouble returnnumber;
		/** 累计差异量 **/
		private UFDouble divenumber;
		/** 累计净领料量 **/
		private UFDouble netnumber;
		/** CBS */
		private String pk_cbsnode;

		public String getPk_material() {
			return pk_material;
		}

		public void setPk_material(String pk_material) {
			this.pk_material = pk_material;
		}

		public String getPk_material_v() {
			return pk_material_v;
		}

		public void setPk_material_v(String pk_material_v) {
			this.pk_material_v = pk_material_v;
		}

		public UFDouble getDrawnumber() {
			return drawnumber;
		}

		public void setDrawnumber(UFDouble drawnumber) {
			this.drawnumber = drawnumber;
		}

		public UFDouble getReturnnumber() {
			return returnnumber;
		}

		public void setReturnnumber(UFDouble returnnumber) {
			this.returnnumber = returnnumber;
		}

		public UFDouble getDivenumber() {
			return divenumber;
		}

		public void setDivenumber(UFDouble divenumber) {
			this.divenumber = divenumber;
		}

		public UFDouble getNetnumber() {
			return netnumber;
		}

		public void setNetnumber(UFDouble netnumber) {
			this.netnumber = netnumber;
		}

		public String getPk_cbsnode() {
			return pk_cbsnode;
		}

		public void setPk_cbsnode(String pk_cbsnode) {
			this.pk_cbsnode = pk_cbsnode;
		}
	}
}