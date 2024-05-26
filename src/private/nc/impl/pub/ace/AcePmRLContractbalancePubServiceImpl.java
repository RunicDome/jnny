package nc.impl.pub.ace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.pm.pmrlcontractbalance.ace.bp.AcePmRLContractbalanceApproveBP;
import nc.bs.pm.pmrlcontractbalance.ace.bp.AcePmRLContractbalanceDeleteBP;
import nc.bs.pm.pmrlcontractbalance.ace.bp.AcePmRLContractbalanceInsertBP;
import nc.bs.pm.pmrlcontractbalance.ace.bp.AcePmRLContractbalanceSendApproveBP;
import nc.bs.pm.pmrlcontractbalance.ace.bp.AcePmRLContractbalanceUnApproveBP;
import nc.bs.pm.pmrlcontractbalance.ace.bp.AcePmRLContractbalanceUnSendApproveBP;
import nc.bs.pm.pmrlcontractbalance.ace.bp.AcePmRLContractbalanceUpdateBP;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.uif.pub.exception.UifException;
import nc.vo.org.OrgVO;
import nc.vo.pm.feebalance.PmFeebalanceBVO;
import nc.vo.pm.rlcontractalter.AggRLContractalterHVO;
import nc.vo.pm.rlcontractalter.RLContractalterBVO;
import nc.vo.pm.rlcontractalter.RLContractalterHVO;
import nc.vo.pm.rlcontractbalance.AggRLContractbalanceHVO;
import nc.vo.pm.rlcontractbalance.RLContractbalanceBVO;
import nc.vo.pm.rlcontractbalance.RLContractbalanceHVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

//多编码合同结算单
public abstract class AcePmRLContractbalancePubServiceImpl {
	
	private String billType = "4Z02";
	private String workFId;
	private String tableName;
	
	
	// 新增
	public AggRLContractbalanceHVO[] pubinsertBills(AggRLContractbalanceHVO[] clientFullVOs,
			AggRLContractbalanceHVO[] originBills) throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggRLContractbalanceHVO> transferTool = new BillTransferTool<AggRLContractbalanceHVO>(
					clientFullVOs);
			// 调用BP
			AcePmRLContractbalanceInsertBP action = new AcePmRLContractbalanceInsertBP();
			AggRLContractbalanceHVO[] retvos = action.insert(clientFullVOs);
			// 构造返回数据
			/*反写多编码合同表体累计拉数量*/
			updateAggPmFeebalance(clientFullVOs);
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	private void updateAggPmFeebalance(AggRLContractbalanceHVO[] clientFullVOs) throws BusinessException {
		Map<String, String> sourceId = new HashMap<String, String>();
		for(AggRLContractbalanceHVO temp : clientFullVOs){
			RLContractbalanceBVO[] bvos = temp.getChildrenVO();
			for(RLContractbalanceBVO bvo : bvos){
				sourceId.put(bvo.getCsourcebillbid(), bvo.getCsourcebillbid());
			}
		}
		List<PmFeebalanceBVO> updatePmFeebalanceBVO = new ArrayList<PmFeebalanceBVO>();
        for (String key : sourceId.keySet()) {
        	PmFeebalanceBVO bvo = (PmFeebalanceBVO) getHyPubBO().queryByPrimaryKey(PmFeebalanceBVO.class, key);
        	RLContractbalanceBVO[] bvos = (RLContractbalanceBVO[]) getHyPubBO().queryByCondition(RLContractbalanceBVO.class, "nvl(dr,0) = 0 and csourcebillbid='" + key + "'");
        	UFDouble number = UFDouble.ZERO_DBL;
        	for(RLContractbalanceBVO bvo1 : bvos){
        		/*报送金额*/
        		number = number.add(bvo1.getMoney());
        	}
        	if(bvo.getMoney().compareTo(number) < 0){
        		throw new BusinessException("累计结算金额大于付款金额");
        	}
        	bvo.setStatus(VOStatus.UPDATED);
        	/*累计结算金额赋值*/
        	if(number.doubleValue() == 0){
        		bvo.setContractbalance_mny(null);
        	}else{
        		bvo.setContractbalance_mny(number);
        	}
        	updatePmFeebalanceBVO.add(bvo);
        }
        if(updatePmFeebalanceBVO.size() > 0){
        	getHyPubBO().updateAry(updatePmFeebalanceBVO.toArray(new PmFeebalanceBVO[0]));
        }
	}

	private HYPubBO hyPubBO;

	public HYPubBO getHyPubBO() {
		if(null == hyPubBO){
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	public void setHyPubBO(HYPubBO hyPubBO) {
		this.hyPubBO = hyPubBO;
	}

	// 删除
	public void pubdeleteBills(AggRLContractbalanceHVO[] clientFullVOs,
			AggRLContractbalanceHVO[] originBills) throws BusinessException {
		try {
			// 调用BP
			new AcePmRLContractbalanceDeleteBP().delete(clientFullVOs);
			/*反写多编码合同表体累计拉数量*/
			updateAggPmFeebalance(clientFullVOs);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggRLContractbalanceHVO[] pubupdateBills(AggRLContractbalanceHVO[] clientFullVOs,
			AggRLContractbalanceHVO[] originBills) throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggRLContractbalanceHVO> transferTool = new BillTransferTool<AggRLContractbalanceHVO>(
					clientFullVOs);
			AcePmRLContractbalanceUpdateBP bp = new AcePmRLContractbalanceUpdateBP();
			AggRLContractbalanceHVO[] retvos = bp.update(clientFullVOs, originBills);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggRLContractbalanceHVO[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggRLContractbalanceHVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggRLContractbalanceHVO> query = new BillLazyQuery<AggRLContractbalanceHVO>(
					AggRLContractbalanceHVO.class);
			bills = query.query(queryScheme, null);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return bills;
	}

	/**
	 * 由子类实现，查询之前对queryScheme进行加工，加入自己的逻辑
	 * 
	 * @param queryScheme
	 */
	protected void preQuery(IQueryScheme queryScheme) {
		// 查询之前对queryScheme进行加工，加入自己的逻辑
	}

	// 提交
	public AggRLContractbalanceHVO[] pubsendapprovebills(
			AggRLContractbalanceHVO[] clientFullVOs, AggRLContractbalanceHVO[] originBills)
			throws BusinessException {
		AcePmRLContractbalanceSendApproveBP bp = new AcePmRLContractbalanceSendApproveBP();
		AggRLContractbalanceHVO[] retvos = bp.sendApprove(clientFullVOs, originBills);
		sendOA(retvos);
		return retvos;
	}
	
	private JSONArray getDtaileDataMap(RLContractbalanceBVO[] temp, String def2)
			throws BusinessException {
		// TODO Auto-generated method stub
		Map ctPuBMap = getRLContractbalanceHVO(temp, def2);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(ctPuBMap, jsonConfig);
		return dtlistString;
	}

	private Map getRLContractbalanceHVO(RLContractbalanceBVO[] bodyVOS, String def2)
			throws BusinessException {
		// TODO Auto-generated method stub
		Map dtMap = new HashMap();

		/*
		 * if ("2".equals(getDef2(hvo.getPk_org()))) { dtMap.put("tableDBName",
		 * ); }else{ dtMap.put("tableDBName", ); }
		 */
		if ("4".equals(def2)) {
			dtMap.put("tableDBName", tableName + "_dt1");
		} else if ("1".equals(def2)) {
			dtMap.put("tableDBName", "formtable_main_273_dt1");
		}
		List workflowRequestTableRecords = new ArrayList();
		for (RLContractbalanceBVO temp : bodyVOS) {
			// 构造数据
			List workflowRequestTableFields = new ArrayList();
			if ("4".equals(def2)) {
				workflowRequestTableFields = OaWorkFlowUtil.transBean2Map(temp);
			}
			Map workflowRequestTableFieldsMap = new HashMap();

			/* 子表主键 */
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("zbzj",
					temp.getPk_rl_contr_bal_b()));

			/* 行号 */
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("rowno",
					temp.getRowno()));

			/* 项目编码 */
			String project_code = (String) getHyPubBO().findColValue(
					"bd_project",
					"project_code",
					"nvl(dr,0) = 0 and   pk_project  ='" + temp.getPk_project()
							+ "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("xmbm",
					project_code));

			/* 项目名称 */
			String project_name = (String) getHyPubBO().findColValue(
					"bd_project",
					"project_name",
					"nvl(dr,0) = 0 and   pk_project  ='" + temp.getPk_project()
							+ "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("xmmc",
					project_name));

			/* CBS编码 */
			String cbsbmstr = (String) getHyPubBO().findColValue(
					"bd_cbsnode",
					"code",
					"nvl(dr,0) = 0 and   pk_cbsnode  ='" + temp.getPk_cbsnode()
							+ "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("cbsbm",
					cbsbmstr));

			/* CBS */
			String cbs = (String) getHyPubBO().findColValue(
					"bd_cbsnode",
					"name",
					"nvl(dr,0) = 0 and   pk_cbsnode  ='" + temp.getPk_cbsnode()
							+ "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("cbsmc",
					cbs));

			/* 物料编码 */
			String material_code = (String) getHyPubBO().findColValue(
					"bd_material",
					"code",
					"nvl(dr,0) = 0 and   pk_material  ='"
							+ temp.getPk_material_v() + "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("wlbm",
					material_code));

			/* 物料名称 */
			String material_name = (String) getHyPubBO().findColValue(
					"bd_material",
					"name",
					"nvl(dr,0) = 0 and   pk_material  ='"
							+ temp.getPk_material_v() + "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("wlmc",
					material_name));

			/* 合同金额 */
			if(null!= temp.getMoney()){
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("je",
					temp.getMoney() + ""));
			}
			/* 单价 */
			if(null!= temp.getUnitmoney()){
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("dj",
					temp.getUnitmoney() + ""));
			}
			/* 数量 */
			if(null!= temp.getNum()){
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sl",
					temp.getNum() + ""));
			}
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}
	
		private void sendOA(AggRLContractbalanceHVO[] retvos)
			throws BusinessException {
		OAFlowVO oaVo = null;
		for (AggRLContractbalanceHVO temp : retvos) {
			RLContractbalanceHVO hvo = (RLContractbalanceHVO) temp.getParentVO();
			OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
					hvo.getPk_org());
			String def2 = orgVO.getDef2();
			if ((hvo.getBill_type()).equals(billType) && null != orgVO.getDef2()) {
					oaVo = OaWorkFlowUtil.getOAFlowInfo(hvo.getPk_org(),
					billType);
			}		
			// 中台单据
			if ("4".equals(def2) && oaVo != null && oaVo.getIsdr() == 0) {
					//中台
					workFId = oaVo.getFlowid();
					tableName = oaVo.getTablename();
					// 获取主表数据
					JSONArray headData = getMainMap(hvo, def2);
					// 获取子表数据
					JSONArray bodyData = getDtaileDataMap(
							temp.getChildrenVO(), def2);
					// 构造workflow信息
					OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
					workFlowVO.setPrimaryKey(temp.getPrimaryKey());
					workFlowVO.setPkGroup(hvo.getPk_group());
					workFlowVO.setPkOrg(hvo.getPk_org());
					workFlowVO.setBillMaker(hvo.getBillmaker());
					workFlowVO.setCreator(hvo.getCreator());
					// 接口获取
					workFlowVO.setWorkflowId(OaWorkFlowUtil
							.getOAFlowID(workFId));
					workFlowVO.setWorkflowName(oaVo.getBilltypename());
					//创建连接OA对象
					WorkFlowBill bill = OaWorkFlowUtil
							.getWorkFlowBill(workFlowVO);
					bill.setDef3("ZT");
					bill.setDef4(hvo.getBill_code());
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
			else if ("1".equals(def2)) {
				//不走中台
				workFId = oaVo.getFlowid();
				tableName = oaVo.getTablename();
				// 获取主表数据
				JSONArray headData = getMainMap(hvo, def2);
				// 获取子表数据
				JSONArray bodyData = getDtaileDataMap(
						temp.getChildrenVO(), def2);
				// 构造workflow信息
				OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
				workFlowVO.setPrimaryKey(temp.getPrimaryKey());
				workFlowVO.setPkGroup(hvo.getPk_group());
				workFlowVO.setPkOrg(hvo.getPk_org());
				workFlowVO.setBillMaker(hvo.getBillmaker());
				workFlowVO.setCreator(hvo.getCreator());
				workFlowVO.setBillCode(billType);
				// 接口获取
				workFlowVO.setWorkflowId(OaWorkFlowUtil
						.getOAFlowID(workFId));
				workFlowVO.setWorkflowName("多编码合同结算单");
				WorkFlowBill bill = OaWorkFlowUtil
						.getWorkFlowBill(workFlowVO);
				bill.setDef3("ZT");
				bill.setDef4(hvo.getBill_code());
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
	
	private JSONArray getMainMap(RLContractbalanceHVO hvo, String def2)
			throws BusinessException {
		List list = new ArrayList();
		// 全量字段
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		if ("4".equals(def2)) {
			list = OaWorkFlowUtil.transBean2Map(hvo);
			/*项目组织*/		
			OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
					hvo.getPk_org());
			if (null != orgVO) {
				/* 项目组织 */
				list.add(OaWorkFlowUtil.listAddObj("xmzz", orgVO.getName()));
			}
			/* 合同编码 */
			 /*String htbmstr = (String) getHyPubBO().findColValue("pm_feebalance_ct",
					"bill_code",
					"nvl(dr,0) = 0 and pm_feebalance='" + hvo.getPk_contr() + "'");*/
			 list.add(OaWorkFlowUtil.listAddObj("htbm", hvo.getPk_contr()));
		
			/* 合同名称 */
			/*String htmcstr = (String) getHyPubBO().findColValue("pm_feebalance_ct",
					"bill_name",
					"nvl(dr,0) = 0 and pm_feebalance='" + hvo.getPk_contr() + "'");*/
			// list.add(OaWorkFlowUtil.listAddObj("htmc", htmcstr));
			list.add(OaWorkFlowUtil.listAddObj("htmc", hvo.getBill_name()));
			
			/*合同类型*/
			list.add(OaWorkFlowUtil.listAddObj("htlx", hvo.getBill_type()));
			
			/* 供应商主键 */
			/*list.add(OaWorkFlowUtil.listAddObj("gyszj", hvo.getPk_supplier_name()));*/
		
			/* 供应商名称 */
			String gname = (String) getHyPubBO().findColValue(
					"bd_supplier",
					"name",
					"nvl(dr,0) = 0 and pk_supplier  = '"
							+ hvo.getPk_supplier_name() + "'");
			list.add(OaWorkFlowUtil.listAddObj("gysmc", gname));
		
			// 代控部门编码
			String dkbmstr = (String) getHyPubBO().findColValue("org_dept", "code",
					"nvl(dr,0) = 0 and pk_dept = '" + hvo.getPk_dkdept() + "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbmbm", dkbmstr));
		
			// 代控部门
			String dkbmname = (String) getHyPubBO().findColValue("org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept = '" + hvo.getPk_dkdept() + "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbm", dkbmname));
		
		
			/* 签约部门主键 */
			list.add(OaWorkFlowUtil.listAddObj("qybmzj", hvo.getPk_qydept()));
		
			/* 签约部门名称 */
			String qname = (String) getHyPubBO().findColValue("org_dept", "name",
					"nvl(dr,0) = 0 and   pk_dept  = '" + hvo.getPk_qydept() + "'");
			list.add(OaWorkFlowUtil.listAddObj("qybm", qname));
		
			/* 合同金额 */
			if(null!= hvo.getContractmoney()){
			list.add(OaWorkFlowUtil.listAddObj("htje", hvo.getContractmoney() + ""));
			}
		
			/* 签约日期 */
			list.add(OaWorkFlowUtil.listAddObj("qyrq", hvo.getSigndate() + ""));
		
			/* 合同类型 */
			if(null!= hvo.getContracttype()){
			String htlxwhere = " pk_contracttype = '" + hvo.getContracttype()
					+ "' and nvl(dr,0) = 0";
			String contracttype = "0";
			if (null != (String) getHyPubBO().findColValue("pm_contracttype",
					"type_name", htlxwhere)) {
				contracttype = (String) getHyPubBO().findColValue(
						"pm_contracttype", "type_name", htlxwhere);
			}
			list.add(OaWorkFlowUtil.listAddObj("htlx", contracttype));
			}
		
			/* 分管领导主键 */
			list.add(OaWorkFlowUtil.listAddObj("fgldzj", hvo.getPk_lead_name()));
		
			/* 分管领导名称 */
			if (null != hvo.getPk_lead_name()) {
				String where = " pk_defdoc = '"
						+ hvo.getPk_lead_name()
						+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'fgldmc' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
				String lead_name = "0";
				if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where)) {
					lead_name = (String) getHyPubBO().findColValue("bd_defdoc",
							"name", where);
				}
				list.add(OaWorkFlowUtil.listAddObj("fgldmc", lead_name));
			}
		
			/* 项目经理名称 */
			if (null != hvo.getPk_manager_name()) {
				String where = " pk_defdoc = '"
						+ hvo.getPk_manager_name()
						+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'xmjlmc' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
				String manager_name = "0";
				if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where)) {
					manager_name = (String) getHyPubBO().findColValue("bd_defdoc",
							"name", where);
				}
				list.add(OaWorkFlowUtil.listAddObj("xmjlmc", manager_name));
			}
		
			/* 招标方式 */
			if(null!= hvo.getZbmethod()){
			String zbgswhere = " pk_defdoc = '"
					+ hvo.getZbmethod()
					+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'cgfs' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
			String zbmethod = "0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					zbgswhere)) {
				zbmethod = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						zbgswhere);
			}
			list.add(OaWorkFlowUtil.listAddObj("zbfs", zbmethod));
			}
		
			/* 开始日期 */
			list.add(OaWorkFlowUtil.listAddObj("ksrq", hvo.getStartdate() + ""));
		
			/* 结束日期 */
			list.add(OaWorkFlowUtil.listAddObj("jsrq", hvo.getEnddate() + ""));
		
			/* 签约人主键 */
			list.add(OaWorkFlowUtil.listAddObj("qyrzj", hvo.getPk_signer()));
		
			/* 签约人名称 */
			if(null!= hvo.getPk_signer_name()){
			String pk_signer_name = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and pk_psndoc = '" + hvo.getPk_signer_name()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("qyrmc", pk_signer_name));
			}
			/* 创建人 */
			if(null!= hvo.getCreator()){
			list.add(OaWorkFlowUtil.listAddObj("cjr", hvo.getCreator()));
			}
			/* 创建时间 */
			if (null != hvo.getCreationtime()) {
				list.add(OaWorkFlowUtil.listAddObj("cjsj", hvo.getCreationtime()
						+ ""));
			}
		
			/* 报审日期 */
			if(null!= hvo.getDbilldate()){
				list.add(OaWorkFlowUtil.listAddObj("djrq", hvo.getDbilldate() + ""));
			}
			/* 备注 */
			list.add(OaWorkFlowUtil.listAddObj("bz", hvo.getMemo()));
			}	
		// JSONArray arr = JSONArray.fromObject(list);
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
		
	}
	// 收回
	public AggRLContractbalanceHVO[] pubunsendapprovebills(
			AggRLContractbalanceHVO[] clientFullVOs, AggRLContractbalanceHVO[] originBills)
			throws BusinessException {
		for(AggRLContractbalanceHVO aggVO:clientFullVOs){
			RLContractbalanceHVO hvo = aggVO.getParent();
			OaWorkFlowUtil.backOaWorkFlow(hvo.getPrimaryKey());
		}
		AcePmRLContractbalanceUnSendApproveBP bp = new AcePmRLContractbalanceUnSendApproveBP();
		AggRLContractbalanceHVO[] retvos = bp.unSend(clientFullVOs, originBills);
		return retvos;
	};

	// 审批
	public AggRLContractbalanceHVO[] pubapprovebills(AggRLContractbalanceHVO[] clientFullVOs,
			AggRLContractbalanceHVO[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AcePmRLContractbalanceApproveBP bp = new AcePmRLContractbalanceApproveBP();
		AggRLContractbalanceHVO[] retvos = bp.approve(clientFullVOs, originBills);
		updatePmFeebalanceHVO(retvos, "已结算");
		return retvos;
	}


	private void updatePmFeebalanceHVO(AggRLContractbalanceHVO[] retvos, String clearingstate) throws BusinessException {
		HYPubBO hypubBo = new HYPubBO();
		for(AggRLContractbalanceHVO temp : retvos){
			RLContractbalanceBVO[] bvos = temp.getChildrenVO();
			for(RLContractbalanceBVO bvo : bvos){
				PmFeebalanceBVO newVo = (PmFeebalanceBVO) hypubBo.queryByPrimaryKey(PmFeebalanceBVO.class, bvo.getCsourcebillbid());
				if(null != newVo){
					newVo.setStatus(VOStatus.UPDATED);
					newVo.setClearingstate(clearingstate);
					newVo.setExaminationvalue(bvo.getFinancialentrywrit());
					if(null == clearingstate){
						newVo.setExaminationvalue(null);
					}
					hypubBo.update(newVo);
				}
			}
		}
	}

	// 弃审
	public AggRLContractbalanceHVO[] pubunapprovebills(AggRLContractbalanceHVO[] clientFullVOs,
			AggRLContractbalanceHVO[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AcePmRLContractbalanceUnApproveBP bp = new AcePmRLContractbalanceUnApproveBP();
		AggRLContractbalanceHVO[] retvos = bp.unApprove(clientFullVOs, originBills);
		updatePmFeebalanceHVO(retvos, null);
		return retvos;
	}
	


}