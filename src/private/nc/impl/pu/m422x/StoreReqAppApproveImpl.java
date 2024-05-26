package nc.impl.pu.m422x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.pub.compiler.AbstractCompiler2;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pu.m422x.action.StoreReqAppApproveAction;
import nc.impl.pu.m422x.action.StoreReqAppSendApproveAction;
import nc.impl.pu.m422x.action.StoreReqAppUnApproveAction;
import nc.impl.pu.m422x.action.StoreReqAppUnSendApproveAction;
import nc.itf.pu.m422x.IStoreReqAppApprove;
import nc.vo.bd.material.MaterialVO;
import nc.vo.org.OrgVO;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pu.m422x.entity.StoreReqAppHeaderVO;
import nc.vo.pu.m422x.entity.StoreReqAppItemVO;
import nc.vo.pu.m422x.entity.StoreReqAppVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

//物资需求申请单
public class StoreReqAppApproveImpl implements IStoreReqAppApprove {
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表明
	// TODO 单据同步OA功能
	private HYPubBO hyPubBO;

	public HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}
	public StoreReqAppApproveImpl() {
	}

	public StoreReqAppVO[] approve(StoreReqAppVO[] vos, AbstractCompiler2 script)
			throws BusinessException {
		try {
			return new StoreReqAppApproveAction().approve(vos, script);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}
	//提交动作
	public StoreReqAppVO[] sendapprove(StoreReqAppVO[] vos,
			AbstractCompiler2 script) throws BusinessException {
		try {
			StoreReqAppVO[] nvos = new StoreReqAppSendApproveAction().sendapprove(vos, script);
			//调用递交oa
			senOaData(vos);
			return nvos;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public StoreReqAppVO[] unapprove(StoreReqAppVO[] vos,
			AbstractCompiler2 script) throws BusinessException {
		try {
			StoreReqAppVO[] billVOs = new StoreReqAppUnApproveAction()
					.unapprove(vos, script);
			return billVOs;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
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

	public void setHyPubBO(HYPubBO hyPubBO) {
		this.hyPubBO = hyPubBO;
	}
	//发送到OA系统
	private void senOaData(StoreReqAppVO[] billVOs) throws BusinessException {
		for (StoreReqAppVO temp : billVOs) {
			// 物资需求申请单维护
			StoreReqAppHeaderVO hVO = temp.getHVO();
			if ("4".equals(getDef2(hVO.getPk_org()))) {
				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hVO.getPk_org(), "422X");
				if(oaVo != null && oaVo.getIsdr() == 0){
					WorkFId = oaVo.getFlowid();
					TableName = oaVo.getTablename();
					// 获取主表数据
					JSONArray headData = getMainMap(temp.getHVO());
					// 获取子表数据
					JSONArray bodyData = getDtaileDataMap(temp);
					// 构造workflow信息
					OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
					workFlowVO.setPrimaryKey(temp.getPrimaryKey());
					workFlowVO.setPkGroup(hVO.getPk_group());
					workFlowVO.setPkOrg(hVO.getPk_org());
					workFlowVO.setBillMaker(hVO.getBillmaker());
					workFlowVO.setCreator(hVO.getCreator());
					workFlowVO.setBillCode("422X");
					// 测试临时写死，后期改为接口获取
					workFlowVO.setWorkflowId(OaWorkFlowUtil.getOAFlowID(WorkFId));
					workFlowVO.setWorkflowName("物资需求申请单维护");
					WorkFlowBill bill = OaWorkFlowUtil.getWorkFlowBill(workFlowVO);
					// WorkFlowBill的自定义项3 判断是否港华单据
					bill.setDef3("ZT");
					// 制单人身份证号
					UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
							temp.getHVO().getBillmaker());
					String idCard = (String) getHyPubBO().findColValue("bd_psndoc", "id",
							"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
					bill.setDef5(idCard);
					// 调用OA工具类同步数据至OA
					OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
				}
			}
		}
	}
	//获取主表数据（拼接 json）
	private JSONArray getMainMap(StoreReqAppHeaderVO parentVO)
			throws BusinessException {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// ---------其他字段begin
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		// 库存组织
		String stockName = (String) getHyPubBO().findColValue(
				"org_stockorg",
				"name",
				"nvl(dr,0) = 0 and pk_stockorg   = '" + parentVO.getPk_org()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("zzmc", stockName));
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
		// 物资需求申请类型
		String billTypeName = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and pk_billtypeid  = '"
						+ parentVO.getCtrantypeid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("typename", billTypeName));
		// 申请人
		String apppsnhName = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc  = '" + parentVO.getPk_apppsnh()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("sqr", apppsnhName));
		// 申请部门
		String sqbmName = (String) getHyPubBO().findColValue(
				"org_dept_v",
				"name",
				"nvl(dr,0) = 0 and pk_vid  = '" + parentVO.getPk_appdepth_v()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("sqbm", sqbmName));
		// 申请单号
		list.add(OaWorkFlowUtil.listAddObj("vbillcode", parentVO.getVbillcode()));
		// 项目
		ProjectHeadVO project = (ProjectHeadVO) getHyPubBO().queryByPrimaryKey(ProjectHeadVO.class,
				parentVO.getPk_project());
		list.add(OaWorkFlowUtil.listAddObj("project_code", project.getProject_code()));
		list.add(OaWorkFlowUtil.listAddObj("project_name", project.getProject_name()));
		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	@SuppressWarnings({ "rawtypes" })
	private JSONArray getDtaileDataMap(StoreReqAppVO temp)
			throws BusinessException {
		Map bodyMap = getBody(temp.getBVO());
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
		return dtlistString;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map getBody(StoreReqAppItemVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (StoreReqAppItemVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
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
			// 主单位	
			String zdwstr = "";
			if (null != temp.getCunitid()) {
				zdwstr = (String) getHyPubBO().findColValue(
						"bd_measdoc",
						"name",
						"nvl(dr,0) = 0 and pk_measdoc  = '" + temp.getCunitid()
								+ "'");
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
					"zdw", zdwstr));
			
			if (null != temp.getCprojectid()) {
				// 项目
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
			// CBS名称
			String cbsName = (String) getHyPubBO().findColValue("bd_cbsnode",
					"name",
					"nvl(dr,0) = 0 and pk_cbsnode  = '" + temp.getCbs() + "'");
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

	public StoreReqAppVO[] unSendapprove(StoreReqAppVO[] vos,
			AbstractCompiler2 script) throws BusinessException {
		try {
			StoreReqAppVO[] StoreVO = new StoreReqAppUnSendApproveAction()
					.unSendapprove(vos, script);
			unOaCommit(StoreVO);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	private void unOaCommit(StoreReqAppVO[] aggVO) throws BusinessException {
		for (StoreReqAppVO temp : aggVO) {
			if ((temp.getHVO().getVtrantypecode()).contains("422X")) {
				OaWorkFlowUtil.backOaWorkFlow(temp.getPrimaryKey());
			}
		}
	}
}
