package nc.impl.pu.m20;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pu.m20.action.PraybillApproveAction;
import nc.impl.pu.m20.action.PraybillSendApproveAction;
import nc.impl.pu.m20.action.PraybillUnApproveAction;
import nc.impl.pu.m20.action.PraybillUnSendApproveAction;
import nc.itf.pu.m20.IPraybillApprove;
import nc.vo.bd.material.MaterialVO;
import nc.vo.org.OrgVO;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pu.m20.entity.PraybillHeaderVO;
import nc.vo.pu.m20.entity.PraybillItemVO;
import nc.vo.pu.m20.entity.PraybillVO;
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

public class PraybillApproveImpl implements IPraybillApprove {
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表明
	public PraybillVO[] approve(PraybillVO[] Vos, AbstractCompiler2 script)
			throws BusinessException {
		try {
			return (new PraybillApproveAction()).approve(Vos, script);
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
			return null;
		}
	}

	public PraybillVO[] sendapprove(PraybillVO[] vos, AbstractCompiler2 script)
			throws BusinessException {
		try {
			PraybillVO[] billVOs = new PraybillSendApproveAction().sendapprove(
					vos, script);
			senOaData(billVOs);
			return billVOs;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
			return null;
		}
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

	private void senOaData(PraybillVO[] billVOs) throws BusinessException {
		for (PraybillVO temp : billVOs) {
			// 请购单
			PraybillHeaderVO hVO = temp.getHVO();
			if ("4".equals(getDef2(temp.getHVO().getPk_org())) && hVO.getVtrantypecode().contains("20")) {
				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hVO.getPk_org(), "20");
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
					workFlowVO.setBillCode("20");
					// 测试临时写死，后期改为接口获取
					workFlowVO.setWorkflowId(OaWorkFlowUtil.getOAFlowID(WorkFId));
					workFlowVO.setWorkflowName("请购单");
					WorkFlowBill bill = OaWorkFlowUtil.getWorkFlowBill(workFlowVO);
					bill.setDef3("ZT");
					// 制单人身份证号
					UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
							UserVO.class, temp.getHVO().getBillmaker());
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
	}

	private JSONArray getMainMap(PraybillHeaderVO parentVO)
			throws BusinessException {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// ---------其他字段begin
		// 库存组织
		String stockName = (String) getHyPubBO().findColValue(
				"org_stockorg",
				"name",
				"nvl(dr,0) = 0 and pk_stockorg   = '" + parentVO.getPk_org()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("zzmc", stockName));
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		// 订单类型名称
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
		// 计划员
		String jhy = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc = '" + parentVO.getPk_planpsn()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("jhy", jhy));
		// 计划需求部门
		String jhxqbm = (String) getHyPubBO().findColValue(
				"org_dept_v",
				"name",
				"nvl(dr,0) = 0 and pk_vid = '" + parentVO.getPk_plandept_v()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("jhxqbm", jhxqbm));
		// 采购类型
		if(parentVO.getVdef9() != null){
			String cglx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getVdef9()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("cglxbm", cglx));
			String cglxmc = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getVdef9()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("cglx", cglxmc));
		}
		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	@SuppressWarnings({ "rawtypes" })
	private JSONArray getDtaileDataMap(PraybillVO temp)
			throws BusinessException {
		Map bodyMap = getBody(temp.getBVO());
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
		return dtlistString;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map getBody(PraybillItemVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (PraybillItemVO temp : bvo) {
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
						"nvl(dr,0) = 0 and pk_measdoc = '" + temp.getCunitid()
								+ "'");
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("zdw",
					zdwstr));
			//需求部门
			if(null!= temp.getPk_reqdept_v()){
				String xqbm=(String) getHyPubBO().findColValue(
						"org_dept_v",
						"name",
						"nvl(dr,0) = 0 and   pk_vid  = '" + temp.getPk_reqdept_v()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("xqbm",
						xqbm));
			}	
			//参考单价		
			if(null!= temp.getNtaxprice()){
			Map ckdj=OaWorkFlowUtil.listAdd("ckdj",temp.getNtaxprice()+"" );
			workflowRequestTableFields.add(ckdj);
			}
			//参考金额	
			if(null!= temp.getNtaxmny()){
			Map ckje=OaWorkFlowUtil.listAdd("ckje",temp.getNtaxmny()+"" );
			workflowRequestTableFields.add(ckje);
			}
			
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

	public PraybillVO[] unApprove(PraybillVO[] Vos, AbstractCompiler2 script)
			throws BusinessException {
		try {
			return (new PraybillUnApproveAction()).unApprove(Vos, script);
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
			return null;
		}
	}

	public PraybillVO[] unSendapprove(PraybillVO[] vos, AbstractCompiler2 script)
			throws BusinessException {
		try {
			PraybillVO[] billVOs = new PraybillUnSendApproveAction()
					.unSendapprove(vos, script);
			unOaCommit(billVOs);
			return billVOs;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
			return null;
		}
	}

	private void unOaCommit(PraybillVO[] aggVO) throws BusinessException {
		for (PraybillVO temp : aggVO) {
			if ((temp.getHVO().getVtrantypecode()).contains("20")) {
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
