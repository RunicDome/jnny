package nc.bs.pub.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.cmp.apply.plugin.bpplugin.Cmp_applyPluginPoint;
import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
import nc.imag.pub.util.ImageServiceUtil;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.cmp.IApplyService;
import nc.vo.bd.cust.CustSupplierVO;
import nc.vo.cmp.apply.AggApplyVO;
import nc.vo.cmp.apply.ApplyBVO;
import nc.vo.cmp.apply.ApplyVO;
import nc.vo.org.OrgVO;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.sm.UserVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

//付款申请
@SuppressWarnings({ "rawtypes", "unchecked" })
public class N_36D1_START extends MyAbstractPfAction<AggApplyVO> {
	public N_36D1_START() {
	}

	protected AggApplyVO[] processBP(Object userObj,
			AggApplyVO[] clientFullVOs, AggApplyVO[] originBills) {
		IApplyService operator = (IApplyService) NCLocator.getInstance()
				.lookup(IApplyService.class);

		AggApplyVO[] bills = null;
		try {
			bills = operator.save(clientFullVOs, originBills);
			senOaData(originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappException(e);
		}
		return bills;
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

	private String billType = "36D1";

	private void senOaData(AggApplyVO[] billVOs) throws BusinessException {
		for (AggApplyVO temp : billVOs) {
			// 请购单
			ApplyVO hVO = temp.getParentVO();
			if ("4".equals(getDef2(hVO.getPk_org()))
					&& hVO.getPk_billtypecode().contains(billType)) {
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
				workFlowVO.setWorkflowId("76");
				workFlowVO.setWorkflowName("付款申请");
				WorkFlowBill bill = OaWorkFlowUtil.getWorkFlowBill(workFlowVO);
				bill.setDef3("ZT");
				// 制单人身份证号
				UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
						UserVO.class, hVO.getBillmaker());
				String idCard = (String) getHyPubBO().findColValue(
						"bd_psndoc",
						"id",
						"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc()
								+ "'");
				bill.setDef5(idCard);
				// 调用OA工具类同步数据至OA
				OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
			}
		}
	}

	private JSONArray getMainMap(ApplyVO parentVO) throws BusinessException {
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
		// 供应商
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
		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	private JSONArray getDtaileDataMap(AggApplyVO temp)
			throws BusinessException {
		// 获取详细信息
		ApplyBVO[] bvos = (ApplyBVO[]) getHyPubBO().queryByCondition(
				ApplyBVO.class,
				"nvl(dr,0) = 0 and pk_apply = '" + temp.getPrimaryKey() + "'");
		Map bodyMap = getBody(bvos);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
		return dtlistString;
	}

	private Map getBody(ApplyBVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", "formtable_main_76_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (ApplyBVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			// 合同属性
			if (null != temp.getVdef19()) {
				String htsx = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '"
								+ temp.getVdef19() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"htsx", htsx));
			}
			// 项目
			ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
					.queryByPrimaryKey(ProjectHeadVO.class,
							temp.getPk_project());
			if (null != projectVO) {
				// 项目编码
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bproject_code", projectVO.getProject_code()));
				// 项目名称
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bproject_name", projectVO.getProject_name()));
			}
			// 收款单位
			String skdwname = (String) getHyPubBO().findColValue(
					"bd_cust_supplier",
					"name",
					"nvl(dr,0) = 0 and pk_cust_sup = '"
							+ temp.getPk_receiveunit() + "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("skdw",
					skdwname));
			// 经办人
			String apppsnhName = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and pk_psndoc  = '" + temp.getPk_resuser()
							+ "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("jbr",
					apppsnhName));
			// 经办部门
			String sqbmName = (String) getHyPubBO().findColValue(
					"org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept  = '" + temp.getPk_decidedept()
							+ "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("jbbm",
					sqbmName));
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

	protected CompareAroundProcesser<AggApplyVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggApplyVO> processor = new CompareAroundProcesser(
				Cmp_applyPluginPoint.SEND_APPROVE);

		return processor;
	}

	protected void beforeProcess(PfParameterVO paraVo) {
		CircularlyAccessibleValueObject headvo = paraVo.m_preValueVos[0]
				.getParentVO();
		try {
			String primaryKey = headvo.getPrimaryKey();
			if ((primaryKey == null) || (primaryKey == "")) {
				primaryKey = "VirtualPrimaryKey";
			}
			ImageServiceUtil.commitCheckWithImage(
					headvo.getAttributeValue("pk_trantypecode").toString(),
					primaryKey, headvo.getAttributeValue("pk_org").toString());

		} catch (Exception e) {
			ExceptionUtils.wrappException(e);
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