package nc.impl.pcm.contractalter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.pcm.contractalter.bp.rule.ContrAlterNoPassRule;
import nc.bs.pcm.contractalter.bp.rule.ContrOprAfterApprove4Alt;
import nc.bs.pcm.contractalter.bp.rule.ContrOprAfterDel4Alt;
import nc.bs.pcm.contractalter.bp.rule.ContrOprBeforeApproveRule;
import nc.bs.pcm.contractalter.bp.rule.ContrOprBeforeSave4Alt;
import nc.bs.pcm.contractalter.bp.rule.ContrOprBeforeUnApprove4Alt;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pmpub.servicebase.BillBaseImpl;
import nc.impl.pmpub.servicebase.action.ApproveAction;
import nc.impl.pmpub.servicebase.action.DeleteAction;
import nc.impl.pmpub.servicebase.action.InsertAction;
import nc.impl.pmpub.servicebase.action.UnApproveAction;
import nc.impl.pmpub.servicebase.action.UpdateAction;
import nc.itf.pcm.contract.pub.ContrOperTypeEnum;
import nc.itf.pcm.contractalter.prv.IContractAlter;
import nc.vo.bd.material.MaterialVO;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.bd.supplier.SupplierVO;
import nc.vo.org.OrgVO;
import nc.vo.pbm.budgetadd.BudgetAddHeadVO;
import nc.vo.pcm.contract.ContrHeadVO;
import nc.vo.pcm.contract.ContrWorksVO;
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
import nc.ws.intf.ContractUtils;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.apache.commons.lang.StringUtils;

//补充协议(清单发包合同)
@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
public class ContractAlterImpl extends BillBaseImpl<ContrAlterBillVO> implements
		IContractAlter {
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表明
	public static String WORKFLOWID_JNRL = "54";
	public static String WORKFLOWID_JNNY = "309";
	public static String BODY_TABLE_NAME_JNRL = "formtable_main_57_dt1";
	public static String BODY_TABLE_NAME_JNNY = "formtable_main_351_dt1";
	private BudgetAddHeadVO parentVO;

	public ContractAlterImpl() {
	}

	public Object approveAlter(ContrAlterBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		return approve(billVOs, pfParamVO);
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

	public ContrAlterBillVO[] commitAlter(ContrAlterBillVO[] billVOs)
			throws BusinessException {
		ContrAlterBillVO[] nvos = (ContrAlterBillVO[]) super.commit(billVOs);
		sendoa(billVOs);
		return nvos;
	}

	private String billType = "4D44";

	private void sendoa(ContrAlterBillVO[] billVOs) throws BusinessException {
		for (ContrAlterBillVO temp : billVOs) {
			OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
					temp.getParentVO().getPk_org());
			// 新增针对港华
			ContrAlterHeadVO hVO = temp.getParentVO();
			if (temp.getParentVO().getHdef7() == null) {
				// 20220810修改针对能投
				if ("4".equals(getDef2(temp.getParentVO().getPk_org()))) {

					OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(
							hVO.getPk_org(), "4D44");
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
						workFlowVO.setWorkflowName("补充协议(清单发包合同)");
						WorkFlowBill bill = OaWorkFlowUtil
								.getWorkFlowBill(workFlowVO);
						bill.setDef3("ZT");
						bill.setDef4(hVO.getBill_code());
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
						// 调用OA工具类同步数据至OA
						OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
					}
				} else if ((temp.getParentVO().getTransi_type())
						.contains("4D44-01") && null != orgVO.getDef1()) {

					OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(
							hVO.getPk_org(), "4D44");
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
						workFlowVO.setWorkflowName("补充协议(清单发包合同)");
						WorkFlowBill bill = OaWorkFlowUtil
								.getWorkFlowBill(workFlowVO);
						bill.setDef3("ZT");
						bill.setDef4(hVO.getBill_code());
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
						// 调用OA工具类同步数据至OA
						OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
					} else if ("1".equals(orgVO.getDef2())) {
						JSONArray headData = null;
						JSONArray bodyData = null;
						headData = getMainMap(temp.getParentVO());
						bodyData = getDtaileDataMap(temp);
						Integer requestid = OaWorkFlowUtil.sendOaData(headData,
								bodyData, getWorkFlowBill(temp));
						ContrAlterHeadVO hvo = (ContrAlterHeadVO) temp
								.getParent();
						hvo.setStatus(VOStatus.UPDATED);
						getHyPubBO().update(hvo);
					}
				}
			}
		}
	}

	// 补充协议主表数据
	private JSONArray getMainMap(ContrAlterHeadVO parentVO)
			throws BusinessException {
		List list = new ArrayList();
		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				parentVO.getPk_org());
		if (null != orgVO && "02".equals(orgVO.getCode())) {
			Map szgs = OaWorkFlowUtil.listAdd("szgs", orgVO.getName());
			list.add(szgs);
		}
		// 所属组织名称
		Map sszzmc = OaWorkFlowUtil.listAdd("sszzmc", orgVO.getName());
		list.add(sszzmc);
		Map djzj = OaWorkFlowUtil.listAdd("djzj", parentVO.getPk_contr_alter());
		list.add(djzj);
		// Map zdrzj =OaWorkFlowUtil.listAdd("zdrzj",parentVO.getBillmaker() );
		// list.add(zdrzj);
		String user_code = (String) getHyPubBO().findColValue(
				"sm_user",
				"user_code",
				"nvl(dr,0) = 0 and   cuserid  ='" + parentVO.getBillmaker()
						+ "'");
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		// 代控部门编码
		String dkbmstr = (String) getHyPubBO().findColValue("org_dept", "code",
				"nvl(dr,0) = 0 and pk_dept = '" + parentVO.getHdef3() + "'");
		Map dkbmbm = OaWorkFlowUtil.listAdd("dkbmbm", dkbmstr);
		list.add(dkbmbm);

		// 代控部门
		String dkbmname = (String) getHyPubBO().findColValue("org_dept",
				"name",
				"nvl(dr,0) = 0 and pk_dept = '" + parentVO.getHdef3() + "'");
		Map dkbm = OaWorkFlowUtil.listAdd("dkbm", dkbmname);
		list.add(dkbm);

		// 代控部门主键
		Map dkbmzj = OaWorkFlowUtil.listAdd("dkbmzj", parentVO.getHdef3());
		list.add(dkbmzj);
		if (null != userVO) {
			Map zdrmc = OaWorkFlowUtil.listAdd("zdrmc", userVO.getUser_name());
			list.add(zdrmc);
			// 身份证号
			Map zdrzj = new HashMap();
			zdrzj.put("fieldName", "zdrzj");
			String id = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"id",
					"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc()
							+ "'");
			if ("2".equals(getDef2(parentVO.getPk_org()))) {
				if (null == id) {
					throw new BusinessException("制单人身份证号码未维护");
				}
				zdrzj.put("fieldValue", id);
				list.add(zdrzj);
			} else {
				zdrzj.put("fieldValue", user_code);
				list.add(zdrzj);
			}
		}
		if ("1".equals(getDef2(parentVO.getPk_org()))) {
			// 是否特殊合同
			if (parentVO.getHdef2() != null) {
				String sftshtstr = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getHdef2()
								+ "'");
				Map sftsht = OaWorkFlowUtil.listAdd("sftsht", sftshtstr);
				list.add(sftsht);
			}
		}
		Map zdrq = OaWorkFlowUtil.listAdd("zdrq", parentVO.getBillmaketime()
				.getYear()
				+ "-"
				+ parentVO.getBillmaketime().getStrMonth()
				+ "-" + parentVO.getBillmaketime().getStrDay());
		list.add(zdrq);
		Map djh = OaWorkFlowUtil.listAdd("djh", parentVO.getBill_code());
		list.add(djh);
		String htbmstr = (String) getHyPubBO().findColValue("pm_contr",
				"bill_code",
				"nvl(dr,0) = 0 and pk_contr='" + parentVO.getPk_contr() + "'");
		Map htbm = OaWorkFlowUtil.listAdd("htbm", htbmstr);
		list.add(htbm);
		String bill_name = (String) getHyPubBO().findColValue(
				"pm_contr",
				"bill_name ",
				"nvl(dr,0) = 0 and   pk_contr  ='" + parentVO.getPk_contr()
						+ "'");
		Map htmc = OaWorkFlowUtil.listAdd("htmc", bill_name);
		list.add(htmc);
		String pk_contracttype = (String) getHyPubBO().findColValue(
				"pm_contr",
				"pk_contracttype ",
				"nvl(dr,0) = 0 and   pk_contr  ='" + parentVO.getPk_contr()
						+ "'");
		Map htlxzj = OaWorkFlowUtil.listAdd("htlxzj", pk_contracttype);
		list.add(htlxzj);
		// 合同类型主键已经获取合同类型的主键
		// String pk_contracttype = (String)
		// getHyPubBO().findColValue("pm_contr", "pk_contracttype ",
		// "nvl(dr,0) = 0 and   pk_contr  ='" + parentVO.getPk_contr() + "'");
		String type_name = (String) getHyPubBO().findColValue(
				"pm_contracttype", "type_name",
				"nvl(dr,0) = 0 and pk_contracttype ='" + pk_contracttype + "'");
		Map htlxmc = OaWorkFlowUtil.listAdd("htlxmc", type_name);
		list.add(htlxmc);
		String pk_wbs = (String) getHyPubBO().findColValue(
				"pm_contr",
				"pk_wbs",
				"nvl(dr,0) = 0 and   pk_contr  ='" + parentVO.getPk_contr()
						+ "'");
		Map xmrw = OaWorkFlowUtil.listAdd("xmrw", pk_wbs);
		list.add(xmrw);
		String pk_supplier = (String) getHyPubBO().findColValue(
				"pm_contr",
				"pk_supplier",
				"nvl(dr,0) = 0 and   pk_contr  ='" + parentVO.getPk_contr()
						+ "'");
		String name = (String) getHyPubBO().findColValue("bd_supplier", "name",
				"nvl(dr,0) = 0 and   pk_supplier  ='" + pk_supplier + "'");
		Map gysmc = OaWorkFlowUtil.listAdd("gysmc", name);
		list.add(gysmc);
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
		Map jbbmzj = OaWorkFlowUtil.listAdd("jbbmzj",
				parentVO.getPk_transact_dept_v());
		list.add(jbbmzj);
		String transact_dept_name = (String) getHyPubBO().findColValue(
				"org_dept_v",
				"name",
				"nvl(dr,0) = 0 and pk_vid ='"
						+ parentVO.getPk_transact_dept_v() + "'");
		Map jbbmmc = OaWorkFlowUtil.listAdd("jbbmmc", transact_dept_name);
		list.add(jbbmmc);
		Map jbrzj = OaWorkFlowUtil
				.listAdd("jbrzj", parentVO.getPk_transactor());
		list.add(jbrzj);
		String pk_transactor_name = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and  pk_psndoc ='" + parentVO.getPk_transactor()
						+ "'");
		Map jbrmc = OaWorkFlowUtil.listAdd("jbrmc", pk_transactor_name);
		list.add(jbrmc);
		Map xyrq = OaWorkFlowUtil.listAdd("xyrq", parentVO.getAlter_date()
				.getYear()
				+ "-"
				+ parentVO.getAlter_date().getStrMonth()
				+ "-"
				+ parentVO.getAlter_date().getStrDay());
		list.add(xyrq);
		String st = "0";
		if (null != parentVO.getAlt_mny()) {
			st = parentVO.getAlt_mny() + "";
		}
		Map bcxyje = OaWorkFlowUtil.listAdd("bcxyje", st);
		list.add(bcxyje);
		Map bz = OaWorkFlowUtil.listAdd("bz", parentVO.getMemo());
		list.add(bz);
		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	private JSONArray getDtaileDataMap(ContrAlterBillVO aggVO)
			throws BusinessException {
		Map ctPuBMap = getContrAlterBodyVO(aggVO);
		JSONArray dtlistString = JSONArray.fromObject(ctPuBMap);
		return dtlistString;
	}

	// 补充协议子表数据
	private Map getContrAlterBodyVO(ContrAlterBillVO aggVO)
			throws BusinessException {
		Map dtMap = new HashMap();
		dtMap.put("tableDBName", BODY_TABLE_NAME_JNRL);
		List workflowRequestTableRecords = new ArrayList();
		ContrAlterBodyVO[] bodyVOS = (ContrAlterBodyVO[]) aggVO.getChildrenVO();
		if ("2".equals(getDef2(bodyVOS[0].getPk_org()))) {
			dtMap.put("tableDBName", BODY_TABLE_NAME_JNNY);
		}
		for (ContrAlterBodyVO temp : bodyVOS) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();
			Map xh = OaWorkFlowUtil.listAdd("xh", temp.getRowno());
			workflowRequestTableFields.add(xh);
			Map wlzj = OaWorkFlowUtil.listAdd("wlzj", temp.getPk_material_v());
			workflowRequestTableFields.add(wlzj);
			String pk_material_code = (String) getHyPubBO().findColValue(
					"bd_material",
					"code",
					"nvl(dr,0) = 0 and   pk_material  ='"
							+ temp.getPk_material() + "'");
			Map wlbm = OaWorkFlowUtil.listAdd("wlbm", pk_material_code);
			workflowRequestTableFields.add(wlbm);
			String pk_material_name = (String) getHyPubBO().findColValue(
					"bd_material",
					"name",
					"nvl(dr,0) = 0 and   pk_material  ='"
							+ temp.getPk_material() + "'");
			Map wlmc = OaWorkFlowUtil.listAdd("wlmc", pk_material_name);
			workflowRequestTableFields.add(wlmc);
			String materialspec = (String) getHyPubBO().findColValue(
					"bd_material",
					"materialspec",
					"nvl(dr,0) = 0 and   pk_material  ='"
							+ temp.getPk_material() + "'");
			String str = "0";
			if (null != materialspec) {
				str = materialspec;
			}
			Map gg = OaWorkFlowUtil.listAdd("gg", str);
			workflowRequestTableFields.add(gg);
			String materialtype = (String) getHyPubBO().findColValue(
					"bd_material",
					"materialtype",
					"nvl(dr,0) = 0 and   pk_material  ='"
							+ temp.getPk_material() + "'");
			Map xh1 = OaWorkFlowUtil.listAdd("xh1", materialtype);
			workflowRequestTableFields.add(xh1);
			Map dwzj = OaWorkFlowUtil.listAdd("dwzj", temp.getPk_measdoc());
			workflowRequestTableFields.add(dwzj);
			String djmcNC = (String) getHyPubBO().findColValue(
					"bd_measdoc",
					"name",
					"nvl(dr,0) = 0 and pk_measdoc = '" + temp.getPk_measdoc()
							+ "'");
			Map dwmc = OaWorkFlowUtil.listAdd("dwmc", djmcNC);
			workflowRequestTableFields.add(dwmc);
			String str1 = "0";
			if (null != temp.getPk_wbs()) {
				str1 = temp.getPk_wbs();
			}
			Map xmrwzj = OaWorkFlowUtil.listAdd("xmrwzj", str1);
			workflowRequestTableFields.add(xmrwzj);
			String wbs_name = (String) getHyPubBO().findColValue("pm_wbs",
					"wbs_name",
					"nvl(dr,0) = 0 and   pk_wbs  ='" + temp.getPk_wbs() + "'");
			String st = "0";
			if (null != temp.getPk_wbs()) {
				st = wbs_name;
			}
			Map xmrwmc = OaWorkFlowUtil.listAdd("xmrwmc", st);
			workflowRequestTableFields.add(xmrwmc);
			Map cbszj = OaWorkFlowUtil.listAdd("cbszj", temp.getPk_cbsnode());
			workflowRequestTableFields.add(cbszj);
			String pk_cbs_name = (String) getHyPubBO().findColValue(
					"bd_cbsnode",
					"name",
					"nvl(dr,0) = 0 and   pk_cbsnode  ='" + temp.getPk_cbsnode()
							+ "'");
			Map cbsmc = OaWorkFlowUtil.listAdd("cbsmc", pk_cbs_name);
			workflowRequestTableFields.add(cbsmc);
			String s = "0";
			if (null != temp.getPrice()) {
				s = temp.getPrice().setScale(2, UFDouble.ROUND_HALF_UP) + "";
			}
			Map dj = OaWorkFlowUtil.listAdd("dj", s);
			workflowRequestTableFields.add(dj);
			Map fwjgxs = OaWorkFlowUtil.listAdd("fwjgxs",
					temp.getService_prc_ratio() + "");
			workflowRequestTableFields.add(fwjgxs);
			if (temp.getPk_contr_works() != null) {
				ContrWorksVO contrWorksVO = (ContrWorksVO) getHyPubBO()
						.queryByPrimaryKey(ContrWorksVO.class,
								temp.getPk_contr_works());
				Map ljwcl = OaWorkFlowUtil.listAdd("ljwcl",
						contrWorksVO.getTot_comp_num() + "");
				workflowRequestTableFields.add(ljwcl);
			}
			String num = "0";
			if (null != temp.getCont_num()) {
				num = temp.getCont_num() + "";
			}
			Map sl = OaWorkFlowUtil.listAdd("sl", num);
			workflowRequestTableFields.add(sl);
			String str2 = "0";
			if (null != temp.getCont_mny()) {
				str2 = temp.getCont_mny().setScale(2, UFDouble.ROUND_HALF_UP)
						+ "";
			}
			Map je = OaWorkFlowUtil.listAdd("je", str2);
			workflowRequestTableFields.add(je);
			String str3 = "0";
			if (null != temp.getAlt_num()) {
				str3 = temp.getAlt_num() + "";
			}
			Map bcxysl = OaWorkFlowUtil.listAdd("bcxysl", str3);
			workflowRequestTableFields.add(bcxysl);
			String str4 = "0";
			if (null != temp.getAlt_mny()) {
				str4 = temp.getAlt_mny().setScale(2, UFDouble.ROUND_HALF_UP)
						+ "";
			}
			Map bcxyje = OaWorkFlowUtil.listAdd("bcxyje", str4);
			workflowRequestTableFields.add(bcxyje);
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	private WorkFlowBill getWorkFlowBill(ContrAlterBillVO temp)
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
			workFlowBill.setWorkflowName("补充协议");
		} else {
			UserVO user = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
					temp.getParentVO().getBillmaker());
			workFlowBill.setDef5(user.getUser_code());
			workFlowBill.setWorkflowId(WORKFLOWID_JNRL);
			if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
				PsndocVO psndoc = (PsndocVO) getHyPubBO().queryByPrimaryKey(
						PsndocVO.class, user.getPk_psndoc());
				workFlowBill.setDef5(psndoc.getId());
				// workFlowBill.setWorkflowId(WORKFLOWID_JNNY);
			}
			UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
					UserVO.class, temp.getParentVO().getBillmaker());
			workFlowBill.setPk_group(temp.getParentVO().getPk_group());
			workFlowBill.setPk_org(temp.getParentVO().getPk_org());
			workFlowBill.setPk_bill(temp.getPrimaryKey());
			workFlowBill.setUser_name(userVO.getUser_name());
			workFlowBill.setUser_code(userVO.getUser_code());
			workFlowBill.setBill_status("1");
			workFlowBill.setDef4(temp.getParentVO().getBill_code());
			workFlowBill.setCreationtime(new UFDate().toStdString());
			workFlowBill.setCreator(temp.getParentVO().getCreator());
			// workFlowBill.setWorkflowId("54");
			workFlowBill.setBill_code(temp.getParentVO().getBill_type());
			workFlowBill.setWorkflowName("补充协议");
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

	private JSONArray getMainMapNew(ContrAlterHeadVO parentVO)
			throws BusinessException {
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
		// 合同信息
		ContrHeadVO contVO = (ContrHeadVO) getHyPubBO().queryByPrimaryKey(
				ContrHeadVO.class, parentVO.getPk_contr());
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
		// 经办人
		if (null != parentVO.getPk_transactor()) {
			String apppsnhName = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and pk_psndoc  = '"
							+ parentVO.getPk_transactor() + "'");
			list.add(OaWorkFlowUtil.listAddObj("jbr", apppsnhName));
		}
		// 经办部门
		String sqbmName = (String) getHyPubBO().findColValue(
				"org_dept_v",
				"name",
				"nvl(dr,0) = 0 and pk_vid  = '"
						+ parentVO.getPk_transact_dept_v() + "'");
		list.add(OaWorkFlowUtil.listAddObj("jbbm", sqbmName));
		// 用印类型
		if (parentVO.getHdef4() != null) {
			String yylx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getHdef4()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("yylx", yylx));
		}
		// 是否使用电子签章
		if (parentVO.getHdef5() != null) {
			String sfsydzqz = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getHdef5()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sfsydzqz", sfsydzqz));
		}
		// 项目审批类型
		if (parentVO.getHdef6() != null) {
			String xmsplxbm = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getHdef6()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("xmsplxbm", xmsplxbm));
			String xmsplx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getHdef6()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("xmsplx", xmsplx));
		}
		// 20220810新增针对能投======================================= begin
		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				parentVO.getPk_org());
		list.add(OaWorkFlowUtil.listAddObj("szgs", orgVO.getName()));
		// 所属组织名称
		list.add(OaWorkFlowUtil.listAddObj("sszzmc", orgVO.getName()));
		list.add(OaWorkFlowUtil.listAddObj("djzj", parentVO.getPk_contr_alter()));
		// Map zdrzj =OaWorkFlowUtil.listAdd("zdrzj",parentVO.getBillmaker() );
		// list.add(zdrzj);
		String user_code = (String) getHyPubBO().findColValue("sm_user",
				"user_code",
				"nvl(dr,0) = 0 and cuserid ='" + parentVO.getBillmaker() + "'");
		UserVO makeruserVO = (UserVO) getHyPubBO().queryByPrimaryKey(
				UserVO.class, parentVO.getBillmaker());
		if (null != parentVO.getHdef3()) {
			// 代控部门编码
			String dkbmstr = (String) getHyPubBO()
					.findColValue(
							"org_dept",
							"code",
							"nvl(dr,0) = 0 and pk_dept = '"
									+ parentVO.getHdef3() + "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbmbm", dkbmstr));

			// 代控部门
			String dkbmname = (String) getHyPubBO()
					.findColValue(
							"org_dept",
							"name",
							"nvl(dr,0) = 0 and pk_dept = '"
									+ parentVO.getHdef3() + "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbm", dkbmname));

			// 代控部门主键
			list.add(OaWorkFlowUtil.listAddObj("dkbmzj", parentVO.getHdef3()));
		}
		if (null != makeruserVO) {
			list.add(OaWorkFlowUtil.listAddObj("zdrmc",
					makeruserVO.getUser_name()));
			// 身份证号
			Map zdrzj = new HashMap();
			zdrzj.put("fieldName", "zdrzj");
			String id = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"id",
					"nvl(dr,0) = 0 and pk_psndoc='"
							+ makeruserVO.getPk_psndoc() + "'");
			if (null == id) {
				throw new BusinessException("制单人身份证号码未维护");
			}
			zdrzj.put("fieldValue", id);
			list.add(zdrzj);
		}
		// 是否特殊合同
		if (parentVO.getHdef2() != null) {
			String sftshtstr = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getHdef2()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sftsht", sftshtstr));
		}
		if (null != parentVO.getBillmaketime()) {
			list.add(OaWorkFlowUtil.listAddObj("zdrq", parentVO
					.getBillmaketime().getYear()
					+ "-"
					+ parentVO.getBillmaketime().getStrMonth()
					+ "-"
					+ parentVO.getBillmaketime().getStrDay()));
		}
		list.add(OaWorkFlowUtil.listAddObj("djh", parentVO.getBill_code()));
		String htbmstr = (String) getHyPubBO().findColValue("pm_contr",
				"bill_code",
				"nvl(dr,0) = 0 and pk_contr='" + parentVO.getPk_contr() + "'");
		list.add(OaWorkFlowUtil.listAddObj("htbm", htbmstr));
		String bill_name = (String) getHyPubBO().findColValue("pm_contr",
				"bill_name ",
				"nvl(dr,0) = 0 and pk_contr ='" + parentVO.getPk_contr() + "'");
		list.add(OaWorkFlowUtil.listAddObj("htmc", bill_name));
		String pk_contracttype = (String) getHyPubBO().findColValue(
				"pm_contr",
				"pk_contracttype ",
				"nvl(dr,0) = 0 and   pk_contr  ='" + parentVO.getPk_contr()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("htlxzj", pk_contracttype));
		// 合同类型主键已经获取合同类型的主键
		// String pk_contracttype = (String)
		// getHyPubBO().findColValue("pm_contr", "pk_contracttype ",
		// "nvl(dr,0) = 0 and   pk_contr  ='" + parentVO.getPk_contr() + "'");
		String type_name = (String) getHyPubBO().findColValue(
				"pm_contracttype", "type_name",
				"nvl(dr,0) = 0 and pk_contracttype ='" + pk_contracttype + "'");
		list.add(OaWorkFlowUtil.listAddObj("htlxmc", type_name));
		String pk_wbs = (String) getHyPubBO().findColValue("pm_contr",
				"pk_wbs",
				"nvl(dr,0) = 0 and pk_contr ='" + parentVO.getPk_contr() + "'");
		list.add(OaWorkFlowUtil.listAddObj("xmrw", pk_wbs));
		if (null != parentVO.getPk_contr()) {
			String pk_supplier = (String) getHyPubBO().findColValue(
					"pm_contr",
					"pk_supplier",
					"nvl(dr,0) = 0 and pk_contr  ='" + parentVO.getPk_contr()
							+ "'");
			String name = (String) getHyPubBO().findColValue("bd_supplier",
					"name",
					"nvl(dr,0) = 0 and pk_supplier ='" + pk_supplier + "'");
			list.add(OaWorkFlowUtil.listAddObj("gysmc", name));
		}
		if (null != parentVO.getPk_project()) {
			String project_code = (String) getHyPubBO().findColValue(
					"bd_project",
					"project_code",
					"nvl(dr,0) = 0 and pk_project ='"
							+ parentVO.getPk_project() + "'");
			list.add(OaWorkFlowUtil.listAddObj("xmbm", project_code));
			String project_name = (String) getHyPubBO().findColValue(
					"bd_project",
					"project_name",
					"nvl(dr,0) = 0 and pk_project  ='"
							+ parentVO.getPk_project() + "'");
			list.add(OaWorkFlowUtil.listAddObj("xmmc", project_name));
		}
		list.add(OaWorkFlowUtil.listAddObj("jbbmzj",
				parentVO.getPk_transact_dept_v()));
		String transact_dept_name = (String) getHyPubBO().findColValue(
				"org_dept_v",
				"name",
				"nvl(dr,0) = 0 and pk_vid ='"
						+ parentVO.getPk_transact_dept_v() + "'");
		list.add(OaWorkFlowUtil.listAddObj("jbbmmc", transact_dept_name));
		list.add(OaWorkFlowUtil.listAddObj("jbrzj", parentVO.getPk_transactor()));
		if (null != parentVO.getPk_transactor()) {
			String pk_transactor_name = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and  pk_psndoc ='"
							+ parentVO.getPk_transactor() + "'");
			list.add(OaWorkFlowUtil.listAddObj("jbrmc", pk_transactor_name));
		}
		if (null != parentVO.getAlter_date()) {
			list.add(OaWorkFlowUtil.listAddObj("xyrq", parentVO.getAlter_date()
					.getYear()
					+ "-"
					+ parentVO.getAlter_date().getStrMonth()
					+ "-" + parentVO.getAlter_date().getStrDay()));
		}
		String st = "0";
		if (null != parentVO.getAlt_mny()) {
			st = parentVO.getAlt_mny() + "";
		}
		list.add(OaWorkFlowUtil.listAddObj("bcxyje", st));
		list.add(OaWorkFlowUtil.listAddObj("bz", parentVO.getMemo()));
		// 20220810新增针对能投======================================= end

		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	private JSONArray getDtaileDataMapNew(ContrAlterBillVO temp)
			throws BusinessException {
		// 获取详细信息
		ContrAlterBodyVO[] bvos = (ContrAlterBodyVO[]) temp.getChildrenVO();
		Map bodyMap = getBody(bvos);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
		return dtlistString;
	}

	private Map getBody(ContrAlterBodyVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (ContrAlterBodyVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = new ArrayList();
			if (!"1".equals(getDef2(temp.getPk_org()))) {
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
			}
			// 单位
			if (null != temp.getPk_measdoc()) {
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
			}
			// CBS名称
			String cbsName = (String) getHyPubBO().findColValue(
					"bd_cbsnode",
					"name",
					"nvl(dr,0) = 0 and pk_cbsnode  = '" + temp.getPk_cbsnode()
							+ "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("cbsmc",
					cbsName));

			// 20220810修改针对能投对接中台 =====================================begin
			if (null != temp.getRowno()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("xh",
						temp.getRowno()));
			}
			if (null != temp.getPk_material_v()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"wlzj", temp.getPk_material_v()));
			}
			if (null != temp.getPk_material()) {
				String pk_material_code = (String) getHyPubBO().findColValue(
						"bd_material",
						"code",
						"nvl(dr,0) = 0 and   pk_material  ='"
								+ temp.getPk_material() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"wlbm", pk_material_code));
				String pk_material_name = (String) getHyPubBO().findColValue(
						"bd_material",
						"name",
						"nvl(dr,0) = 0 and   pk_material  ='"
								+ temp.getPk_material() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"wlmc", pk_material_name));
				String materialspec = (String) getHyPubBO().findColValue(
						"bd_material",
						"materialspec",
						"nvl(dr,0) = 0 and   pk_material  ='"
								+ temp.getPk_material() + "'");
				String str = "0";
				if (null != materialspec) {
					str = materialspec;
				}
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("gg",
						str));

				String materialtype = (String) getHyPubBO().findColValue(
						"bd_material",
						"materialtype",
						"nvl(dr,0) = 0 and   pk_material  ='"
								+ temp.getPk_material() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("xh1",
						materialtype));
			}
			if (null != temp.getPk_measdoc()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dwzj", temp.getPk_measdoc()));
			}
			if (null != temp.getPk_measdoc()) {
				String djmcNC = (String) getHyPubBO().findColValue(
						"bd_measdoc",
						"name",
						"nvl(dr,0) = 0 and pk_measdoc = '"
								+ temp.getPk_measdoc() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dwmc", djmcNC));
			}
			String str1 = "0";
			if (null != temp.getPk_wbs()) {
				str1 = temp.getPk_wbs();
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("xmrwzj",
					str1));
			String wbs_name = (String) getHyPubBO().findColValue("pm_wbs",
					"wbs_name",
					"nvl(dr,0) = 0 and   pk_wbs  ='" + temp.getPk_wbs() + "'");
			String st = "0";
			if (null != temp.getPk_wbs()) {
				st = wbs_name;
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("xmrwmc",
					st));
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("cbszj",
					temp.getPk_cbsnode()));
			String pk_cbs_name = (String) getHyPubBO().findColValue(
					"bd_cbsnode",
					"name",
					"nvl(dr,0) = 0 and   pk_cbsnode  ='" + temp.getPk_cbsnode()
							+ "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("cbsmc",
					pk_cbs_name));
			String s = "0";
			if (null != temp.getPrice()) {
				s = temp.getPrice().setScale(2, UFDouble.ROUND_HALF_UP) + "";
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("dj", s));
			if (null != temp.getService_prc_ratio()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fwjgxs", temp.getService_prc_ratio() + ""));
			}
			if (temp.getPk_contr_works() != null) {
				ContrWorksVO contrWorksVO = (ContrWorksVO) getHyPubBO()
						.queryByPrimaryKey(ContrWorksVO.class,
								temp.getPk_contr_works());
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"ljwcl", contrWorksVO.getTot_comp_num() + ""));
			}
			String num = "0";
			if (null != temp.getCont_num()) {
				num = temp.getCont_num() + "";
			}
			workflowRequestTableFields
					.add(OaWorkFlowUtil.listAddObj("sl", num));
			String str2 = "0";
			if (null != temp.getCont_mny()) {
				str2 = temp.getCont_mny().setScale(2, UFDouble.ROUND_HALF_UP)
						+ "";
			}
			workflowRequestTableFields.add(OaWorkFlowUtil
					.listAddObj("je", str2));
			String str3 = "0";
			if (null != temp.getAlt_num()) {
				str3 = temp.getAlt_num() + "";
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bcxysl",
					str3));
			String str4 = "0";
			if (null != temp.getAlt_mny()) {
				str4 = temp.getAlt_mny().setScale(2, UFDouble.ROUND_HALF_UP)
						+ "";
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bcxyje",
					str4));
			// 20220810修改针对能投对接中台 =====================================end

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

	public ContrAlterBillVO[] deleteAlter(ContrAlterBillVO[] billVOs)
			throws BusinessException {
		return (ContrAlterBillVO[]) delete(billVOs);
	}

	public ContrAlterBillVO[] insertAlter(ContrAlterBillVO[] billVOs)
			throws BusinessException {
		// 保存校验
		beforecheck(billVOs);
		return (ContrAlterBillVO[]) insert(billVOs);
	}

	// XBX新增保存前校验
	public void beforecheck(ContrAlterBillVO[] billVOs)
			throws BusinessException {
		for (ContrAlterBillVO contrAlterBillVO : billVOs) {
			// 查询档案，取校验组织
			Object conforg = (Object) getHyPubBO().findColValue(
					"bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
							+ "FROM BD_DEFDOCLIST WHERE CODE = 'HTSAVEORG')"
							+ " and code = '"
							+ contrAlterBillVO.getParentVO().getPk_org() + "'");// 组织
			if (conforg != null) {
				// 调用工具类校验是否可保存
				ContractUtils utils = new ContractUtils();
				String error = utils.checkIfSave(contrAlterBillVO, 2);
				if (StringUtils.isNotEmpty(error)) {
					throw new BusinessException(error);
				}
			}
		}
	}

	public ContrAlterBillVO[] unapproveAlter(ContrAlterBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		return (ContrAlterBillVO[]) unApprove(billVOs, pfParamVO);
	}

	public ContrAlterBillVO[] unCommitAlter(ContrAlterBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		ContrAlterBillVO[] newAggVO = (ContrAlterBillVO[]) unCommit(billVOs,
				pfParamVO);
		unOaCommit(newAggVO);
		return newAggVO;
	}

	private void unOaCommit(ContrAlterBillVO[] aggVO) throws BusinessException {
		for (ContrAlterBillVO temp : aggVO) {
			if ("4D44".equals(temp.getParentVO().getBill_type())) {
				OaWorkFlowUtil.backOaWorkFlow(temp.getPrimaryKey());
			}
		}
	}

	public ContrAlterBillVO[] updateAlter(ContrAlterBillVO[] billVOs,
			ContrAlterBillVO[] originBillVOs) throws BusinessException {
		// 保存校验
		beforecheck(billVOs);
		return (ContrAlterBillVO[]) update(billVOs, originBillVOs);
	}

	protected void initApproveAction(ApproveAction<ContrAlterBillVO> action) {
		super.initApproveAction(action);
		action.addBeforeRule(new ContrOprBeforeApproveRule());
		action.addAfterRule(new ContrOprAfterApprove4Alt());
		action.addBeforeRuleWhenNotPass(new ContrAlterNoPassRule());
	}

	protected void initDeleteAction(DeleteAction<ContrAlterBillVO> action) {
		super.initDeleteAction(action);
		action.addAfterRule(new ContrOprAfterDel4Alt());
	}

	protected void initInsertAction(InsertAction<ContrAlterBillVO> action) {
		super.initInsertAction(action);
		action.addBeforeRule(new ContrOprBeforeSave4Alt(
				ContrOperTypeEnum.OPER_ADD));
	}

	protected void initUnApproveAction(UnApproveAction<ContrAlterBillVO> action) {
		super.initUnApproveAction(action);
		action.addBeforeRule(new ContrOprBeforeUnApprove4Alt());
	}

	protected void initUpdateAction(UpdateAction<ContrAlterBillVO> action) {
		super.initUpdateAction(action);
		action.addBeforeRule(new ContrOprBeforeSave4Alt(
				ContrOperTypeEnum.OPER_UPDATE));
	}
}
