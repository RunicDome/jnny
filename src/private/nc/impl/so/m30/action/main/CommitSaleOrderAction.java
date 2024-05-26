package nc.impl.so.m30.action.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.trade.business.HYPubBO;
import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.bd.material.MaterialVO;
import nc.vo.org.OrgVO;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.sm.UserVO;
import nc.vo.so.m30.entity.SaleOrderBVO;
import nc.vo.so.m30.entity.SaleOrderHVO;
import nc.vo.so.m30.entity.SaleOrderVO;
import nc.vo.so.pub.rule.SOPfStatusChgRule;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

// 销售订单维护
@SuppressWarnings({ "unchecked", "rawtypes" })
public class CommitSaleOrderAction {
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表明

	public CommitSaleOrderAction() {
	}

	// public SaleOrderVO[] approve(SaleOrderVO[] Vos, AbstractCompiler2 script)
	// throws BusinessException {
	// try {
	// return (new SaleOrderApproveAction()).approve(Vos, script);
	// } catch (Exception ex) {
	// ExceptionUtils.marsh(ex);
	//
	// return null;
	// }
	// }
	public SaleOrderVO[] sendApprove(SaleOrderVO[] clientBills,
			SaleOrderVO[] originBills) throws BusinessException {
		for (SaleOrderVO newvo : clientBills) {
			SOPfStatusChgRule statuschgrule = new SOPfStatusChgRule();
			statuschgrule.changePfToBillStatus(newvo);
		}
		BillUpdate<SaleOrderVO> update = new BillUpdate();
		SaleOrderVO[] returnVos = (SaleOrderVO[]) update.update(clientBills,
				originBills);
		sendOaData(clientBills);
		return returnVos;
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

	private void sendOaData(SaleOrderVO[] billVOs) throws BusinessException {
		for (SaleOrderVO temp : billVOs) {
			// 销售订单
			SaleOrderHVO hVO = temp.getParentVO();
			if ("4".equals(getDef2(hVO.getPk_org()))
					&& hVO.getVtrantypecode().contains("30")) {
				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hVO.getPk_org(),
						"30");
				if (oaVo != null && oaVo.getIsdr() == 0) {
					WorkFId = oaVo.getFlowid();
					TableName = oaVo.getTablename();
					// 获取主表数据
					JSONArray headData = getMainMap(temp.getParentVO());
					// 获取子表数据
					JSONArray bodyData = getDtaileDataMap(temp);
					// 构造workflow信息
					OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
					workFlowVO.setPrimaryKey(temp.getPrimaryKey());
					workFlowVO.setPkGroup(hVO.getPk_group());
					workFlowVO.setPkOrg(hVO.getPk_org());
					workFlowVO.setBillMaker(hVO.getBillmaker());
					workFlowVO.setCreator(hVO.getCreator());
					workFlowVO.setBillCode("30");
					// 测试临时写死，后期改为接口获取
					workFlowVO.setWorkflowId(OaWorkFlowUtil
							.getOAFlowID(WorkFId));
					workFlowVO.setWorkflowName("销售订单");
					WorkFlowBill bill = OaWorkFlowUtil
							.getWorkFlowBill(workFlowVO);
					// WorkFlowBill的自定义项3 判断是否港华单据
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

	public String getDef2(String pk_org) throws BusinessException {
		String def2 = "";
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		if (null != orgVO) {
			def2 = orgVO.getDef2();
		}
		return def2;
	}

	private JSONArray getMainMap(SaleOrderHVO parentVO)
			throws BusinessException {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// ---------其他字段begin
		// 销售组织
		String orgName = (String) getHyPubBO().findColValue(
				"org_salesorg",
				"name",
				"nvl(dr,0) = 0 and pk_salesorg  = '" + parentVO.getPk_org()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("zzmc", orgName));
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		// 合同类型名称
		String billTypeName = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and pk_billtypeid  = '"
						+ parentVO.getCtrantypeid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("typename", billTypeName));
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

		// 合同类型
		String billType = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename ",
				"nvl(dr,0) = 0 and  pk_billtypeid  = '"
						+ parentVO.getCtrantypeid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("htlx", billType));
		// 客户
		String customerName = (String) getHyPubBO().findColValue(
				"bd_customer",
				"name ",
				"nvl(dr,0) = 0 and  pk_customer  = '"
						+ parentVO.getCcustomerid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("kh", customerName));
		// 部门
		String vDeptName = (String) getHyPubBO().findColValue("org_dept_v",
				"name ",
				"nvl(dr,0) = 0 and pk_vid  = '" + parentVO.getCdeptvid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("bm", vDeptName));
		// 业务员
		String psndocName = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name ",
				"nvl(dr,0) = 0 and  pk_psndoc = '" + parentVO.getCemployeeid()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("ywy", psndocName));
		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	private JSONArray getDtaileDataMap(SaleOrderVO temp)
			throws BusinessException {
		Map bodyMap = getBody(temp.getChildrenVO());
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
		return dtlistString;
	}

	private Map getBody(SaleOrderBVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (SaleOrderBVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			MaterialVO materialVO = (MaterialVO) getHyPubBO()
					.queryByPrimaryKey(MaterialVO.class, temp.getCmaterialvid());
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
			// 主单位
			String zdwstr = "";
			if (null != temp.getCunitid()) {
				zdwstr = (String) getHyPubBO().findColValue(
						"bd_measdoc",
						"name",
						"nvl(dr,0) = 0 and pk_measdoc  = '" + temp.getCunitid()
								+ "'");
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("zdw",
					zdwstr));
			// 单位
			String dwstr = "";
			if (null != temp.getCastunitid()) {
				dwstr = (String) getHyPubBO().findColValue(
						"bd_measdoc",
						"name",
						"nvl(dr,0) = 0 and pk_measdoc  = '"
								+ temp.getCastunitid() + "'");
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("dw",
					dwstr));
			// 项目
			if (null != temp.getCprojectid()) {
				ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
						.queryByPrimaryKey(ProjectHeadVO.class,
								temp.getCprojectid());
				// 项目编码
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bproject_code", projectVO.getProject_code()));
				// 项目名称
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bproject_name", projectVO.getProject_name()));
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
}
// public SaleOrderVO[] unApprove(SaleOrderVO[] Vos, AbstractCompiler2 script)
// throws BusinessException {
// try {
// return (new PraybillUnApproveAction()).unApprove(Vos, script);
// }
// catch (Exception ex) {
// ExceptionUtils.marsh(ex);
//
//
// return null;
// }
// }
// public SaleOrderVO[] unSendapprove(SaleOrderVO[] vos, AbstractCompiler2
// script) throws BusinessException {
// try {
// SaleOrderVO[] billVOs =new SaleOrderUnSendApproveAction().unSendapprove(vos,
// script);
// unOaCommit(billVOs);
// return billVOs;
// }
// catch (Exception e) {
// ExceptionUtils.marsh(e);
//
// return null;
// }
// }
//
// private void unOaCommit(SaleOrderVO[] aggVO) throws BusinessException {
// for (SaleOrderVO temp : aggVO) {
// if ((temp.getParentVO().getVtrantypecode()).contains("20")) {
// OaWorkFlowUtil.backOaWorkFlow(temp.getPrimaryKey());
// }
// }
// }
// }
