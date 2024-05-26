package nc.impl.pbm.materialadd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pbm.materalterprice.bp.rule.MatePlanAlterApproveRule;
import nc.bs.pbm.materalterprice.bp.rule.MatePlanAlterUnapproveRule;
import nc.bs.pbm.materialadd.bp.rule.MatePlanAddApproveBeforeRule;
import nc.bs.pbm.materialadd.bp.rule.MatePlanAddApproveRule;
import nc.bs.pbm.materialadd.bp.rule.MatePlanAddSaveBaseRule;
import nc.bs.pbm.materialadd.bp.rule.MatePlanAddUnapproveRule;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pbm.materialadd.rule.ApproveForOrderRule;
import nc.impl.pbm.materialadd.rule.CheckHasBudgetRule;
import nc.impl.pbm.materialadd.rule.MaterialAddCalPriceRule;
import nc.impl.pbm.materialadd.rule.UnApproveForOrderRule;
import nc.impl.pm.billrule.SetBizScopeInfoRule;
import nc.impl.pmpub.servicebase.BillBaseImpl;
import nc.impl.pmpub.servicebase.action.ApproveAction;
import nc.impl.pmpub.servicebase.action.InsertAction;
import nc.impl.pmpub.servicebase.action.UnApproveAction;
import nc.impl.pmpub.servicebase.action.UpdateAction;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.pcm.utils.GetDao;
import nc.vo.bd.material.MaterialVO;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.org.DeptVO;
import nc.vo.org.ItemOrgVO;
import nc.vo.org.OrgVO;
import nc.vo.pbm.materialplanadd.MaterialPlanAddBillVO;
import nc.vo.pbm.materialplanadd.MaterialPlanAddBodyVO;
import nc.vo.pbm.materialplanadd.MaterialPlanAddHeadVO;
import nc.vo.pim.project.ProjectHeadVO;
import nc.vo.pub.BusinessException;
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

@SuppressWarnings({ "unchecked", "rawtypes", "restriction" })
// 物资及服务需求单调整单
public class MaterialPlanAddInterfaceImpl extends
		BillBaseImpl<MaterialPlanAddBillVO> implements
		nc.itf.pbm.materialadd.pvt.IMaterialPlanAddInterface {
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表明
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
	public static String WORKFLOWID_JNRL = "120";
	public static String WORKFLOWID_JNNY = "255";

	public static String BODY_TABLE_NAME_JNRL = "formtable_main_36_dt1";
	public static String BODY_TABLE_NAME_JNNY = "formtable_main_301_dt1";

	public String getDef2(String pk_org) throws BusinessException {
		String def2 = "";
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		if (null != orgVO) {
			def2 = orgVO.getDef2();
		}
		return def2;
	}

	public MaterialPlanAddInterfaceImpl() {
	}

	public MaterialPlanAddBillVO[] insertMaterialPlanAddVOS(
			MaterialPlanAddBillVO[] billVOs) throws BusinessException {
		doBeforeCheck(billVOs);
		return (MaterialPlanAddBillVO[]) insert(billVOs);
	}

	public void doBeforeCheck(MaterialPlanAddBillVO[] billVOs)
			throws BusinessException {
		// TODO Auto-generated method stub
		int nosave = 0;
		MaterialPlanAddBillVO billVO = (MaterialPlanAddBillVO) billVOs[0];
		MaterialPlanAddHeadVO headVO = (MaterialPlanAddHeadVO) billVO
				.getParentVO();
		String pk_mate = headVO.getPrimaryKey();
		String pk_project = headVO.getPk_project();// 项目编码
		// MaterialPlanAddBodyVO[] bodyvo = (MaterialPlanAddBodyVO[])
		// billVO.getChildrenVO();
		String tzdept = headVO.getPk_plandept();// 需求调整部门主键
		if (tzdept == null || "".equals(tzdept)) {
			throw new BusinessException("需求调整部门不能为空！");
		} else {
			String searchSql = "select * from pm_mateplanadd where pk_project = '"
					+ pk_project
					+ "' and dr = 0 and (bill_status  = "
					+ " -1 or bill_status = 0 or bill_status = 2 or bill_status = 3)";
			if (pk_mate != null) {
				searchSql += " and pk_matplanadd <> '" + pk_mate + "'";
			}
			IUAPQueryBS queryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(
					IUAPQueryBS.class.getName());
			System.out.println("输出sql：" + searchSql);
			List<MaterialPlanAddBillVO> tzlist = (List<MaterialPlanAddBillVO>) queryBS
					.executeQuery(searchSql, new BeanListProcessor(
							MaterialPlanAddBillVO.class));
			nosave = tzlist.size();
			if (nosave > 0) {
				throw new BusinessException("物资及服务调整单还有未审批的单据，不允许保存！");
			}
		}
	}

	public MaterialPlanAddBillVO[] updateMaterialPlanAddVOS(
			MaterialPlanAddBillVO[] billVOs,
			MaterialPlanAddBillVO[] originBillVOs) throws BusinessException {
		return (MaterialPlanAddBillVO[]) update(billVOs, originBillVOs);
	}

	public MaterialPlanAddBillVO[] deleteMaterialPlanAddVOS(
			MaterialPlanAddBillVO[] billVOs) throws BusinessException {
		return (MaterialPlanAddBillVO[]) delete(billVOs);
	}

	public Object approveMaterialPlanAddVOS(MaterialPlanAddBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		Object obj = null;
		/*
		 * int flag = checkifdo(); if(flag != 1){ throw new
		 * BusinessException("已提交OA审批！"); }
		 */
		if (billVOs[0].getChildrenVO()[0].getNowadd_num() == null) {
			obj = approveAlertMaterialPlanAddVOS(billVOs, pfParamVO);
		} else {
			obj = approveAddMaterialPlanAddVOS(billVOs, pfParamVO);
		}
		return obj;
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

	public Object approveAlertMaterialPlanAddVOS(
			MaterialPlanAddBillVO[] billVOs, PfParameterVO paraVo)
			throws BusinessException {
		ApproveAction<MaterialPlanAddBillVO> action = createApproveAction();
		initApproveAction(action);

		action.addBeforeRule(new MatePlanAddApproveBeforeRule());

		action.addBeforeRule(new ApproveForOrderRule());

		action.addBeforeRule(new MatePlanAlterApproveRule());

		return action.processAction(billVOs, paraVo);
	}

	public Object approveAddMaterialPlanAddVOS(MaterialPlanAddBillVO[] billVOs,
			PfParameterVO paraVo) throws BusinessException {
		ApproveAction<MaterialPlanAddBillVO> action = createApproveAction();
		initApproveAction(action);

		action.addBeforeRule(new MatePlanAddApproveBeforeRule());

		action.addAfterRule(new ApproveForOrderRule());

		action.addAfterRule(new MatePlanAddApproveRule());

		return action.processAction(billVOs, paraVo);
	}

	public MaterialPlanAddBillVO[] unapproveMaterialPlanAddVOS(
			MaterialPlanAddBillVO[] billVOs, PfParameterVO pfParamVO)
			throws BusinessException {
		/*
		 * int flag = checkifdo(); if(flag != 1){ throw new
		 * BusinessException("OA审批的单据不允许取消审批！"); }
		 */
		if (billVOs[0].getChildrenVO()[0].getNowadd_num() == null) {
			return unapproveAlterMaterialPlanAddVOS(billVOs, pfParamVO);
		}

		return unapproveAddMaterialPlanAddVOS(billVOs, pfParamVO);
	}

	public MaterialPlanAddBillVO[] commitMaterialPlanAddVOS(
			MaterialPlanAddBillVO[] billVOs) throws BusinessException {
		MaterialPlanAddBillVO[] billVOs2 = commit(billVOs);

		return senOaData(billVOs2);
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

	private MaterialPlanAddBillVO[] senOaData(MaterialPlanAddBillVO[] billVOs)
			throws BusinessException {
		for (MaterialPlanAddBillVO temp : billVOs) {
			MaterialPlanAddHeadVO hvo = temp.getParentVO();
			OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
					hvo.getPk_org());
			DeptVO deptVO = (DeptVO) getHyPubBO().queryByPrimaryKey(
					DeptVO.class, hvo.getPk_plandept());
			if ("0001A110000000000HYQ".equals(orgVO.getPk_org())
					&& "119".equals(deptVO.getCode())) {
			} else if ((hvo.getBill_type()).equals("4D16")
					&& null != orgVO.getDef1()) {
				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hvo.getPk_org(),
						"4D16");
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
					workFlowVO.setBillCode("4D16");
					// 接口获取
					workFlowVO.setWorkflowId(OaWorkFlowUtil
							.getOAFlowID(WorkFId));
					workFlowVO.setWorkflowName("物资及服务需求调整单");
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
					JSONArray bodyData = getDtaileDataMap(temp);
					OaWorkFlowUtil.sendOaData(headData, bodyData,
							getWorkFlowBill(temp));
				}
			} else {
				// 港华单据
				if ("4".equals(getDef2(hvo.getPk_org()))
						&& hvo.getBill_type().equals("4D16")) {
					OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(
							hvo.getPk_org(), "4D16");
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
						workFlowVO.setBillCode("4D16");
						// 接口获取
						workFlowVO.setWorkflowId(OaWorkFlowUtil
								.getOAFlowID(WorkFId));
						workFlowVO.setWorkflowName("物资及服务需求调整单");
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

	private JSONArray getNewDtaileDataMap(MaterialPlanAddBillVO temp)
			throws BusinessException {
		// 获取详细信息
		MaterialPlanAddBodyVO[] bvos = (MaterialPlanAddBodyVO[]) temp
				.getChildrenVO();
		Map bodyMap = getBody(bvos, getDef2(temp.getParentVO().getPk_org()));
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
		return dtlistString;
	}

	private Map getBody(MaterialPlanAddBodyVO[] bvos, String def2)
			throws BusinessException {
		// TODO Auto-generated method stub
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (MaterialPlanAddBodyVO temp : bvos) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = new ArrayList();
			if (!"1".equals(def2)) {
				workflowRequestTableFields = OaWorkFlowUtil.transBean2Map(temp);
			}
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
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"wlbm", materialVO.getCode()));// 物料编码
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"wlmc", materialVO.getName()));// 物料名称
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("gg",
						materialVO.getMaterialspec()));// 规格
				String name = (String) getHyPubBO().findColValue(
						"bd_measdoc",
						"name",
						"nvl(dr,0) = 0 and pk_measdoc = '"
								+ materialVO.getPk_measdoc() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("dw",
						name));// 单位
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
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("cbsmc",
					cbsName));
			// 能投
			if (null != temp.getRowno()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("xh",
						temp.getRowno()));// 型号
			}
			if (null != temp.getPk_mater_plan_b()) {
				String s = (String) getHyPubBO().findColValue(
						"pm_mater_plan_b",
						"rowno",
						"nvl(dr,0) = 0 and pk_mater_plan_b = '"
								+ temp.getPk_mater_plan_b() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"xqdxh", s));//
			}
			if (null != temp.getPk_cbs_node()) {
				String cbss = (String) getHyPubBO().findColValue(
						"bd_cbsnode",
						"name",
						"nvl(dr,0) = 0 and pk_cbsnode = '"
								+ temp.getPk_cbs_node() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("cbs",
						cbss));
			}

			if (null != temp.getNnum()) {
				String t = temp.getNnum() + "";
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sl",
						t));// 税率
			}
			if (null != temp.getMater_unit_price()) {
				String d = temp.getMater_unit_price().setScale(2,
						UFDouble.ROUND_HALF_UP)
						+ "";
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("dj",
						d));// 单价
			}

			if (null != temp.getService_prc_ratio()) {
				String r = temp.getService_prc_ratio() + "";
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fwjgxs", r));//
			}
			if (null != temp.getMater_price()) {
				String zj = temp.getMater_price() == null ? "0.0" : temp
						.getMater_price().setScale(2, UFDouble.ROUND_HALF_UP)
						+ "";
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("zj",
						zj));
			}
			if (null != temp.getNowadd_num()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bcdzl", temp.getNowadd_num() + ""));//
			}
			if (null != temp.getMater_margin()) {
				String dzje = temp.getMater_margin() == null ? "0.0" : temp
						.getMater_margin().setScale(2, UFDouble.ROUND_HALF_UP)
						+ "";
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dzje", dzje));//
			}
			if (null != temp.getRequire_date()) {
				String str = temp.getRequire_date().getYear() + "-"
						+ temp.getRequire_date().getStrMonth() + "-"
						+ temp.getRequire_date().getStrDay();
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"xqrq", str));//
			}
			if (null != temp.getPk_stockorg()) {
				String name = (String) getHyPubBO().findColValue(
						"org_stockorg",
						"name",
						"nvl(dr,0) = 0 and  pk_stockorg  = '"
								+ temp.getPk_stockorg() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"kczzmc", name));//
			}
			if (null != temp.getOppo_material()) {
				String wls = (String) getHyPubBO().findColValue(
						"bd_material_v",
						"name",
						"nvl(dr,0) = 0 and  pk_source  = '"
								+ temp.getOppo_material() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dywl", wls));//
			}

			if (null != temp.getMemo()) {
				String o = temp.getMemo();
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bz",
						o));// 备注
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

	private JSONArray getNewMainMap(MaterialPlanAddHeadVO parentVO)
			throws BusinessException {
		// TODO Auto-generated method stub
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = new ArrayList();
		// 构造数据
		if (!"1".equals(getDef2(parentVO.getPk_org()))) {
			list = OaWorkFlowUtil.transBean2Map(parentVO);
		}
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
		OrgVO yhorgvo = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				userVO.getPk_org());
		list.add(OaWorkFlowUtil.listAddObj("yhsszzzj", userVO.getPk_org()));
		list.add(OaWorkFlowUtil.listAddObj("yhsszzbm", yhorgvo.getCode()));
		list.add(OaWorkFlowUtil.listAddObj("yhsszzmc", yhorgvo.getName()));
		// 单据类型
		if (null != parentVO.getBill_type()) {
			String billTypeName = (String) getHyPubBO().findColValue(
					"bd_billtype",
					"billtypename",
					"nvl(dr,0) = 0 and pk_billtypeid  = '"
							+ parentVO.getBill_type() + "'");
			list.add(OaWorkFlowUtil.listAddObj("typename", billTypeName));
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
			// 项目名称
			list.add(OaWorkFlowUtil.listAddObj("xmmc",
					projectVO.getProject_name()));
			// 项目编码
			list.add(OaWorkFlowUtil.listAddObj("xmbm",
					projectVO.getProject_code()));
		}
		// 需求调整人
		if (null != parentVO.getPk_planuser()) {
			String bzrmc = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and pk_psndoc = '"
							+ parentVO.getPk_planuser() + "'");
			list.add(OaWorkFlowUtil.listAddObj("xqtzr", bzrmc));
		}
		// 需求调整部门
		if (null != parentVO.getPk_plandept()) {
			String bzbmmc = (String) getHyPubBO().findColValue(
					"org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept = '" + parentVO.getPk_plandept()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("xqtzbm", bzbmmc));
		}

		// 能投
		if (null != parentVO.getPk_matplanadd()) {
			list.add(OaWorkFlowUtil.listAddObj("djzj",
					parentVO.getPk_matplanadd()));
		}
		if (null != parentVO.getPk_org()) {
			list.add(OaWorkFlowUtil.listAddObj("xmzzzj", parentVO.getPk_org()));// 项目组织主键
		}
		ItemOrgVO itemOrgVO = (ItemOrgVO) getHyPubBO().queryByPrimaryKey(
				ItemOrgVO.class, parentVO.getPk_org());
		if (null != parentVO.getPk_org()) {
			list.add(OaWorkFlowUtil.listAddObj("xmzzmc", itemOrgVO.getName()));// 项目组织名称
		}
		if (null != parentVO.getBill_code()) {
			list.add(OaWorkFlowUtil.listAddObj("dzdh", parentVO.getBill_code()));
		}
		if (null != parentVO.getPk_plandept_v()) {
			list.add(OaWorkFlowUtil.listAddObj("xqdzbmzj",
					parentVO.getPk_plandept_v()));
		}
		if (null != parentVO.getPk_planuser()) {
			list.add(OaWorkFlowUtil.listAddObj("xqdzrzj",
					parentVO.getPk_planuser()));
		}
		list.add(OaWorkFlowUtil.listAddObj("zdrzj", userVO.getUser_code()));// 制单人主键
		list.add(OaWorkFlowUtil.listAddObj("zdrmc", userVO.getUser_name()));// 制单人名称
		list.add(OaWorkFlowUtil.listAddObj("djzt", "2"));// 单据状态
		if (null != parentVO.getPk_plandept_v()) {
			String dname = (String) getHyPubBO().findColValue(
					"org_dept_v",
					"name",
					"nvl(dr,0) = 0 and pk_vid = '"
							+ parentVO.getPk_plandept_v() + "'");
			list.add(OaWorkFlowUtil.listAddObj("xqdzbmmc", dname));// 部门名称
		}
		if (null != parentVO.getPk_planuser()) {
			String uname = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and pk_psndoc = '"
							+ parentVO.getPk_planuser() + "'");
			list.add(OaWorkFlowUtil.listAddObj("xqdzrmc", uname));//
		}
		if (null != parentVO.getPk_plandept_v()) {
			list.add(OaWorkFlowUtil.listAddObj("xqzjrq", parentVO
					.getPlanadddate().getYear()
					+ "-"
					+ parentVO.getPlanadddate().getStrMonth()
					+ "-"
					+ parentVO.getPlanadddate().getStrDay()));// 日期
		}
		String id = (String) getHyPubBO().findColValue("bd_psndoc", "id",
				"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
		list.add(OaWorkFlowUtil.listAddObj("sfzh", id));// 身份证号
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
			list.add(OaWorkFlowUtil.listAddObj("wljfwlx", wljfwlx));// 项目类型
		}

		list.add(OaWorkFlowUtil.listAddObj("bz", parentVO.getMemo()));// 备注
		if (parentVO.getHdef6() != null) {
			String fqffgld = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getHdef6()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("fqffgld", fqffgld));// 分管领导
		}
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

	private WorkFlowBill getWorkFlowBill(MaterialPlanAddBillVO temp)
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
			workFlowBill.setWorkflowName("物资及服务需求调整单");
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
			workFlowBill.setWorkflowName("物资及服务需求调整单");
		}
		return workFlowBill;
	}

	private JSONArray getDtaileDataMap(MaterialPlanAddBillVO temp)
			throws BusinessException {
		Map ctPuBMap = getMaterialPlanAddBody(temp.getChildrenVO());
		JSONArray dtlistString = JSONArray.fromObject(ctPuBMap);
		return dtlistString;
	}

	private Map getMaterialPlanAddBody(MaterialPlanAddBodyVO[] childrenVO)
			throws BusinessException {
		Map dtMap = new HashMap();
		dtMap.put("tableDBName", BODY_TABLE_NAME_JNRL);
		if ("2".equals(getDef2(childrenVO[0].getPk_org()))) {
			dtMap.put("tableDBName", BODY_TABLE_NAME_JNNY);
		}
		List workflowRequestTableRecords = new ArrayList();
		for (MaterialPlanAddBodyVO temp : childrenVO) {
			Map workflowRequestTableFieldsMap = new HashMap();
			List workflowRequestTableFields = new ArrayList();

			Map xh = new HashMap();
			xh.put("fieldName", "xh");
			xh.put("fieldValue", temp.getRowno());
			workflowRequestTableFields.add(xh);

			String s = "0";
			if (null != temp.getPk_mater_plan_b()) {
				s = (String) getHyPubBO().findColValue(
						"pm_mater_plan_b",
						"rowno",
						"nvl(dr,0) = 0 and pk_mater_plan_b = '"
								+ temp.getPk_mater_plan_b() + "'");
			}
			Map xqdxh = new HashMap();
			xqdxh.put("fieldName", "xqdxh");
			xqdxh.put("fieldValue", s);
			workflowRequestTableFields.add(xqdxh);

			Map cbs = new HashMap();
			String cbss = "0";
			if (null != temp.getPk_cbs_node()) {
				cbss = (String) getHyPubBO().findColValue(
						"bd_cbsnode",
						"name",
						"nvl(dr,0) = 0 and pk_cbsnode = '"
								+ temp.getPk_cbs_node() + "'");
			}
			cbs.put("fieldName", "cbs");
			cbs.put("fieldValue", cbss);
			workflowRequestTableFields.add(cbs);

			MaterialVO materialVO = (MaterialVO) getHyPubBO()
					.queryByPrimaryKey(MaterialVO.class,
							temp.getPk_material_v());
			if (null != materialVO) {
				Map wlbm = new HashMap();
				wlbm.put("fieldName", "wlbm");
				wlbm.put("fieldValue", materialVO.getCode());
				workflowRequestTableFields.add(wlbm);

				Map wlmc = new HashMap();
				wlmc.put("fieldName", "wlmc");
				wlmc.put("fieldValue", materialVO.getName());
				workflowRequestTableFields.add(wlmc);

				Map gg = new HashMap();
				gg.put("fieldName", "gg");
				gg.put("fieldValue", materialVO.getMaterialspec());
				workflowRequestTableFields.add(gg);

				String name = (String) getHyPubBO().findColValue(
						"bd_measdoc",
						"name",
						"nvl(dr,0) = 0 and pk_measdoc = '"
								+ materialVO.getPk_measdoc() + "'");
				Map dw = new HashMap();
				dw.put("fieldName", "dw");
				dw.put("fieldValue", name);
				workflowRequestTableFields.add(dw);
			}

			String t = "0";
			if (null != temp.getNnum()) {
				t = temp.getNnum() + "";
			}
			Map sl = new HashMap();
			sl.put("fieldName", "sl");
			sl.put("fieldValue", t);
			workflowRequestTableFields.add(sl);

			String d = "0";
			if (null != temp.getMater_unit_price()) {
				d = temp.getMater_unit_price().setScale(2,
						UFDouble.ROUND_HALF_UP)
						+ "";
			}
			Map dj = new HashMap();
			dj.put("fieldName", "dj");
			dj.put("fieldValue", d);
			workflowRequestTableFields.add(dj);

			String r = "0";
			if (null != temp.getService_prc_ratio()) {
				r = temp.getService_prc_ratio() + "";
			}
			Map fwjgxs = new HashMap();
			fwjgxs.put("fieldName", "fwjgxs");
			fwjgxs.put("fieldValue", r);
			workflowRequestTableFields.add(fwjgxs);

			if (null != temp.getMater_price()) {
				Map zj = new HashMap();
				zj.put("fieldName", "zj");
				zj.put("fieldValue",
						temp.getMater_price().setScale(2,
								UFDouble.ROUND_HALF_UP)
								+ "");
				workflowRequestTableFields.add(zj);
			}

			Map bcdzl = new HashMap();
			bcdzl.put("fieldName", "bcdzl");
			bcdzl.put("fieldValue", temp.getNowadd_num() + "");
			workflowRequestTableFields.add(bcdzl);

			if (null != temp.getMater_margin()) {
				Map dzje = new HashMap();
				dzje.put("fieldName", "dzje");
				dzje.put(
						"fieldValue",
						temp.getMater_margin().setScale(2,
								UFDouble.ROUND_HALF_UP)
								+ "");
				workflowRequestTableFields.add(dzje);
			}
			String str = "0";
			if (null != temp.getRequire_date()) {
				str = temp.getRequire_date().getYear() + "-"
						+ temp.getRequire_date().getStrMonth() + "-"
						+ temp.getRequire_date().getStrDay();
			}
			Map xqrq = new HashMap();
			xqrq.put("fieldName", "xqrq");
			xqrq.put("fieldValue", str);
			workflowRequestTableFields.add(xqrq);

			String name = (String) getHyPubBO().findColValue(
					"org_stockorg",
					"name",
					"nvl(dr,0) = 0 and  pk_stockorg  = '"
							+ temp.getPk_stockorg() + "'");
			Map kczzmc = new HashMap();
			kczzmc.put("fieldName", "kczzmc");
			kczzmc.put("fieldValue", name);
			workflowRequestTableFields.add(kczzmc);

			String wls = "0";
			if (null != temp.getOppo_material()) {
				wls = (String) getHyPubBO().findColValue(
						"bd_material_v",
						"name",
						"nvl(dr,0) = 0 and  pk_source  = '"
								+ temp.getOppo_material() + "'");
			}
			Map dywl = new HashMap();
			dywl.put("fieldName", "dywl");
			dywl.put("fieldValue", wls);
			workflowRequestTableFields.add(dywl);

			String o = "0";
			if (null != temp.getMemo()) {
				o = temp.getMemo();
			}
			Map bz = new HashMap();
			bz.put("fieldName", "bz");
			bz.put("fieldValue", o);
			workflowRequestTableFields.add(bz);

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	private JSONArray getMainMap(MaterialPlanAddHeadVO parentVO)
			throws BusinessException {
		List list = new ArrayList();

		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				parentVO.getPk_org());
		if (null != orgVO) {

			// Map szgs =OaWorkFlowUtil.listAdd("szgs",orgVO.getCode() );
			// list.add(szgs);
		}

		Map djzj = new HashMap();
		djzj.put("fieldName", "djzj");
		// djzj.put("fieldValue", parentVO.getPk_matplanadd());
		djzj.put("fieldValue", "1");
		list.add(djzj);

		Map xmzzzj = new HashMap();
		xmzzzj.put("fieldName", "xmzzzj");
		xmzzzj.put("fieldValue", parentVO.getPk_org());
		// xmzzzj.put("fieldValue", "1");
		list.add(xmzzzj);

		ItemOrgVO itemOrgVO = (ItemOrgVO) getHyPubBO().queryByPrimaryKey(
				ItemOrgVO.class, parentVO.getPk_org());
		if (null != itemOrgVO) {
			Map xmzzmc = new HashMap();
			xmzzmc.put("fieldName", "xmzzmc");
			xmzzmc.put("fieldValue", itemOrgVO.getName());
			list.add(xmzzmc);
		}

		Map dzdh = new HashMap();
		dzdh.put("fieldName", "dzdh");
		dzdh.put("fieldValue", parentVO.getBill_code());
		list.add(dzdh);

		Map xmbm = new HashMap();
		String project_code = (String) getHyPubBO().findColValue(
				"bd_project",
				"project_code",
				"nvl(dr,0) = 0 and pk_project     = '"
						+ parentVO.getPk_project() + "'");

		xmbm.put("fieldName", "xmbm");
		// xmbm.put("fieldValue", parentVO.getPk_project());
		xmbm.put("fieldValue", project_code);
		list.add(xmbm);

		String project_name = (String) getHyPubBO().findColValue(
				"bd_project",
				"project_name",
				"nvl(dr,0) = 0 and pk_project     = '"
						+ parentVO.getPk_project() + "'");
		Map xmmc = new HashMap();
		xmmc.put("fieldName", "xmmc");
		xmmc.put("fieldValue", project_name);
		list.add(xmmc);

		Map djzt = new HashMap();
		djzt.put("fieldName", "djzt");
		djzt.put("fieldValue", "2");
		list.add(djzt);

		Map xqdzbmzj = new HashMap();
		xqdzbmzj.put("fieldName", "xqdzbmzj");
		xqdzbmzj.put("fieldValue", parentVO.getPk_plandept_v());
		// xqdzbmzj.put("fieldValue", "1");
		list.add(xqdzbmzj);

		String dname = (String) getHyPubBO().findColValue(
				"org_dept_v",
				"name",
				"nvl(dr,0) = 0 and pk_vid = '" + parentVO.getPk_plandept_v()
						+ "'");
		Map xqdzbmmc = new HashMap();
		xqdzbmmc.put("fieldName", "xqdzbmmc");
		xqdzbmmc.put("fieldValue", dname);
		list.add(xqdzbmmc);

		Map xqdzrzj = new HashMap();
		xqdzrzj.put("fieldName", "xqdzrzj");
		xqdzrzj.put("fieldValue", parentVO.getPk_planuser());
		// xqdzrzj.put("fieldValue", "1");
		list.add(xqdzrzj);

		String uname = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc = '" + parentVO.getPk_planuser()
						+ "'");
		Map xqdzrmc = new HashMap();
		xqdzrmc.put("fieldName", "xqdzrmc");
		xqdzrmc.put("fieldValue", uname);
		list.add(xqdzrmc);

		Map xqzjrq = new HashMap();
		xqzjrq.put("fieldName", "xqzjrq");
		xqzjrq.put("fieldValue", parentVO.getPlanadddate().getYear() + "-"
				+ parentVO.getPlanadddate().getStrMonth() + "-"
				+ parentVO.getPlanadddate().getStrDay());
		list.add(xqzjrq);

		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		Map zdrzj = new HashMap();
		zdrzj.put("fieldName", "zdrzj");
		zdrzj.put("fieldValue", userVO.getUser_code());
		// zdrzj.put("fieldValue", "1");
		list.add(zdrzj);

		if (null != userVO) {

			Map zdrmc = new HashMap();
			zdrmc.put("fieldName", "zdrmc");
			zdrmc.put("fieldValue", userVO.getUser_name());
			list.add(zdrmc);
		}

		Map sfzh = new HashMap();
		String id = (String) getHyPubBO().findColValue("bd_psndoc", "id",
				"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
		if (null == id) {
			throw new BusinessException("制单人身份证号码未维护");
		}
		sfzh.put("fieldName", "sfzh");
		sfzh.put("fieldValue", id);
		list.add(sfzh);

		Map zdrq = new HashMap();
		zdrq.put("fieldName", "zdrq");
		zdrq.put("fieldValue", parentVO.getBillmaketime().getYear() + "-"
				+ parentVO.getBillmaketime().getStrMonth() + "-"
				+ parentVO.getBillmaketime().getStrDay());
		list.add(zdrq);

		if ("2".equals(getDef2(parentVO.getPk_org()))) {
			Map wljfwlx = new HashMap();
			wljfwlx.put("fieldName", "xmlx");
			String where = " pk_defdoc = '" + parentVO.getHdef7()
					+ "' and nvl(dr,0) = 0";
			String def7 = "0";
			if (null != def7) {
				def7 = (String) getHyPubBO().findColValue("bd_defdoc", "code",
						where);
			}
			wljfwlx.put("fieldValue", def7);
			list.add(wljfwlx);
		} else {
			if (null != parentVO.getHdef5()) {
				Map wljfwlx = new HashMap();
				wljfwlx.put("fieldName", "wljfwlx");
				String where = " pk_defdoc = '" + parentVO.getHdef5()
						+ "' and nvl(dr,0) = 0";
				String def5 = (String) getHyPubBO().findColValue("bd_defdoc",
						"name", where);
				wljfwlx.put("fieldValue", def5);
				list.add(wljfwlx);
			}
		}

		Map bz = OaWorkFlowUtil.listAdd("bz", parentVO.getMemo());
		list.add(bz);

		if (null != parentVO.getHdef6()) {
			Map fqffgld = new HashMap();
			fqffgld.put("fieldName", "fqffgld");
			String where = " pk_defdoc = '"
					+ parentVO.getHdef6()
					+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'fqffgld' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
			String def6 = "0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					where)) {
				def6 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where);
			}
			fqffgld.put("fieldValue", def6);
			list.add(fqffgld);
		}

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	public MaterialPlanAddBillVO[] unCommitMaterialPlanAddVOS(
			MaterialPlanAddBillVO[] billVOs, PfParameterVO pfParamVO)
			throws BusinessException {
		MaterialPlanAddBillVO[] aggVO = unCommit(billVOs, pfParamVO);
		unOaCommit(aggVO);
		return aggVO;
	}

	private void unOaCommit(MaterialPlanAddBillVO[] aggVO)
			throws BusinessException {
		for (MaterialPlanAddBillVO temp : aggVO) {
			if ("4D16".equals(temp.getParentVO().getBill_type())) {
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

	protected void initInsertAction(InsertAction<MaterialPlanAddBillVO> action) {
		super.initInsertAction(action);

		action.addBeforeRule(new SetBizScopeInfoRule());

		action.addBeforeRule(new MatePlanAddSaveBaseRule());

		action.addBeforeRule(new MaterialAddCalPriceRule());
	}

	protected void initUpdateAction(UpdateAction<MaterialPlanAddBillVO> action) {
		super.initUpdateAction(action);

		action.addBeforeRule(new SetBizScopeInfoRule());

		action.addBeforeRule(new MatePlanAddSaveBaseRule());

		action.addBeforeRule(new MaterialAddCalPriceRule());
	}

	public MaterialPlanAddBillVO[] unapproveAlterMaterialPlanAddVOS(
			MaterialPlanAddBillVO[] billVOs, PfParameterVO paraVo)
			throws BusinessException {
		UnApproveAction<MaterialPlanAddBillVO> action = createUnApproveAction();
		initUnApproveAction(action);

		action.addBeforeRule(new UnApproveForOrderRule());

		action.addBeforeRule(new CheckHasBudgetRule());

		action.addBeforeRule(new MatePlanAlterUnapproveRule());
		return (MaterialPlanAddBillVO[]) action.processAction(billVOs, paraVo);
	}

	public MaterialPlanAddBillVO[] unapproveAddMaterialPlanAddVOS(
			MaterialPlanAddBillVO[] billVOs, PfParameterVO paraVo)
			throws BusinessException {
		UnApproveAction<MaterialPlanAddBillVO> action = createUnApproveAction();
		initUnApproveAction(action);

		action.addBeforeRule(new CheckHasBudgetRule());

		action.addBeforeRule(new UnApproveForOrderRule());

		if (billVOs[0].getParentVO().getBill_status().intValue() != 0) {
			action.addAfterRule(new MatePlanAddUnapproveRule());
		}
		return (MaterialPlanAddBillVO[]) action.processAction(billVOs, paraVo);
	}

}
