package nc.impl.to.m5x.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.pub.compiler.AbstractCompiler2;
import nc.bs.to.m5x.plugin.ActionPlugInPoint;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pubapp.pattern.data.bill.BillQuery;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.to.m5x.action.rule.sendapprove.ApproveFlowCheckRule;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.cust.CustSupplierVO;
import nc.vo.bd.material.MaterialVO;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.org.OrgVO;
import nc.vo.pcm.materialacc.AggMaterialaccVO;
import nc.vo.pcm.materialacc.MaterialaccBodyVO;
import nc.vo.pcm.materialacc.MaterialaccHeadVO;
import nc.vo.pim.project.ProjectHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pubapp.pattern.log.TimeLog;
import nc.vo.sm.UserVO;
import nc.vo.to.m5x.entity.BillHeaderVO;
import nc.vo.to.m5x.entity.BillItemVO;
import nc.vo.to.m5x.entity.BillVO;
import nc.vo.to.m5x.pub.M5XVOBusiRuleUtil;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

// 调拨订单提交后台类
@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
public class SendApproveAction {
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表名

	public SendApproveAction() {
	}

	private void addRule(AroundProcesser<BillVO> processer) {
		processer.addBeforeFinalRule(new ApproveFlowCheckRule());

		processer
				.addBeforeFinalRule(new nc.impl.to.m5x.action.rule.sendapprove.SetAuditingStateRule());
	}

	private BillVO[] queryNewVO(BillVO[] inCurVOs) {
		int len = inCurVOs.length;
		String[] ids = new String[len];
		for (int i = 0; i < len; i++) {
			ids[i] = inCurVOs[i].getPrimaryKey();
		}
		BillQuery<BillVO> query = new BillQuery(BillVO.class);
		BillVO[] bills = (BillVO[]) query.query(ids);
		return bills;
	}

	public BillVO[] sendApprove(AbstractCompiler2 script) {
		BillVO[] ret = null;
		try {
			Object[] inCurObject = script.getPfParameterVO().m_preValueVos;
			if (inCurObject == null) {
				return new BillVO[0];
			}
			BillVO[] inCurVOs = new BillVO[inCurObject.length];
			int len = inCurObject.length;
			for (int i = 0; i < len; i++) {
				inCurVOs[i] = ((BillVO) inCurObject[i]);
			}
			AroundProcesser<BillVO> processer = new AroundProcesser(
					ActionPlugInPoint.SendApproveAction);

			addRule(processer);

			TimeLog.logStart();
			processer.before(inCurVOs);
			TimeLog.info(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"4009011_0", "04009011-0201"));

			ret = queryNewVO(inCurVOs);

			for (BillVO bill : ret) {
				M5XVOBusiRuleUtil.calculateOnWayOnwer(bill);
			}
			// 提交OA
			senOaData(ret);
		} catch (Exception ex) {
			nc.vo.pubapp.pattern.exception.ExceptionUtils.wrappException(ex);
		}
		return ret;
	}

	private void senOaData(BillVO[] ret) throws BusinessException {
		// TODO Auto-generated method stub
		for (BillVO temp : ret) {
			// 调拨订单提交
			BillHeaderVO hVO = temp.getParentVO();
			if ("4".equals(getDef2(hVO.getPk_org()))) {
				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hVO.getPk_org(),
						"5X");
				if (oaVo.getIsdr() == 0) {
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
					workFlowVO.setBillCode("5X");
					// 接口获取
					workFlowVO.setWorkflowId(OaWorkFlowUtil
							.getOAFlowID(WorkFId));
					workFlowVO.setWorkflowName("调拨订单");
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

	private JSONArray getDtaileDataMap(BillVO temp) throws BusinessException {
		// 获取详细信息
		Map bodyMap = getBody(temp.getChildrenVO());
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
		return dtlistString;
	}

	private Map getBody(BillItemVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (BillItemVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			// 调出仓库
			String dcck = (String) getHyPubBO().findColValue(
					"bd_stordoc",
					"name",
					"nvl(dr,0) = 0 and pk_stordoc = '"
							+ temp.getCoutstordocid() + "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("dcck",
					dcck));
			// 调入仓库
			String drck = (String) getHyPubBO().findColValue(
					"bd_stordoc",
					"name",
					"nvl(dr,0) = 0 and pk_stordoc = '" + temp.getCinstordocid()
							+ "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("drck",
					drck));
			// 物料信息
			MaterialVO materialVO = (MaterialVO) getHyPubBO()
					.queryByPrimaryKey(MaterialVO.class,
							temp.getCinventoryvid());
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
			String zdwstr = "";
			if (null != temp.getCunitid()) {
				zdwstr = (String) getHyPubBO().findColValue(
						"bd_measdoc",
						"name",
						"nvl(dr,0) = 0 and pk_measdoc = '" + temp.getCunitid()
								+ "'");
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("zdw",
					zdwstr));
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

	private JSONArray getMainMap(BillHeaderVO parentVO)
			throws BusinessException {

		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// ---------其他字段begin
		// 调出库存组织
		String stockName = (String) getHyPubBO().findColValue("org_stockorg_v",
				"name",
				"nvl(dr,0) = 0 and pk_vid = '" + parentVO.getPk_org_v() + "'");
		list.add(OaWorkFlowUtil.listAddObj("zzmc", stockName));

		// 调入库存组织
		String drstockName = (String) getHyPubBO().findColValue(
				"org_stockorg_v",
				"name",
				"nvl(dr,0) = 0 and pk_vid = '" + parentVO.getCinstockorgvid()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("drzzmc", drstockName));

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
		// 交易类型
		String billTypeName = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and pk_billtypeid  = '"
						+ parentVO.getCtrantypeid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("typename", billTypeName));
		// 调出人员
		String dcry = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc  = '" + parentVO.getCoutpsnid()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("dcry", dcry));
		// 调出部门
		String dcbm = (String) getHyPubBO().findColValue(
				"org_dept_v",
				"name",
				"nvl(dr,0) = 0 and pk_vid = '" + parentVO.getCoutdeptvid()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("dcbm", dcbm));

		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
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
}
