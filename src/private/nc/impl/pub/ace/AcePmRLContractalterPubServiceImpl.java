package nc.impl.pub.ace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import nc.bs.framework.common.NCLocator;
import nc.bs.pm.pmrlcontractalter.ace.bp.AcePmRLContractalterApproveBP;
import nc.bs.pm.pmrlcontractalter.ace.bp.AcePmRLContractalterDeleteBP;
import nc.bs.pm.pmrlcontractalter.ace.bp.AcePmRLContractalterInsertBP;
import nc.bs.pm.pmrlcontractalter.ace.bp.AcePmRLContractalterSendApproveBP;
import nc.bs.pm.pmrlcontractalter.ace.bp.AcePmRLContractalterUnApproveBP;
import nc.bs.pm.pmrlcontractalter.ace.bp.AcePmRLContractalterUnSendApproveBP;
import nc.bs.pm.pmrlcontractalter.ace.bp.AcePmRLContractalterUpdateBP;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.itf.pcm.contract.DbmSplitToContr;
import nc.itf.pcm.contract.pvt.IContract;
import nc.itf.pcm.contractalter.prv.IContractAlter;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.org.OrgVO;
import nc.vo.pcm.contract.ContrHeadVO;
import nc.vo.pcm.contract.ContrWorksVO;
import nc.vo.pcm.contract.ContractBillVO;
import nc.vo.pcm.contractalter.ContrAlterBillVO;
import nc.vo.pcm.contractalter.ContrAlterBodyVO;
import nc.vo.pcm.contractalter.ContrAlterHeadVO;
import nc.vo.pcm.materialacc.SecondMaterialBodyVO;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pm.feebalance.PmFeebalanceBVO;
import nc.vo.pm.feebalance.PmFeebalanceHVO;
import nc.vo.pm.rlcontractalter.AggRLContractalterHVO;
import nc.vo.pm.rlcontractalter.RLContractalterBVO;
import nc.vo.pm.rlcontractalter.RLContractalterHVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDate;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.sm.UserVO;
import nc.ws.intf.ContractUtils;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

										   

@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class AcePmRLContractalterPubServiceImpl {

	private String billType = "4Z05";
	private String workFId;
	private String tableName;

	// 新增
	public AggRLContractalterHVO[] pubinsertBills(
			AggRLContractalterHVO[] clientFullVOs,
			AggRLContractalterHVO[] originBills) throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggRLContractalterHVO> transferTool = new BillTransferTool<AggRLContractalterHVO>(
					clientFullVOs);
			// 调用BP
			AcePmRLContractalterInsertBP action = new AcePmRLContractalterInsertBP();
			// XBX多编码合同保存校验 年度投资计划控制 项目类型--技改类项目
			if (checkIFSave(clientFullVOs)) {
				AggRLContractalterHVO[] retvos = action.insert(clientFullVOs);
				// 构造返回数据
				/* 反写多编码合同表体累计拉数量 */
				return transferTool.getBillForToClient(retvos);
			}
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	/* 反写多编码合同表体累计拉数量 */
	private void updateAggPmFeebalance(AggRLContractalterHVO[] clientFullVOs,
			boolean flag) throws BusinessException {
		for (AggRLContractalterHVO agg : clientFullVOs) {
			RLContractalterBVO[] bvos = agg.getChildrenVO();
			// RLContractalterHVO hvo = agg.getParent();
			String dbmhtzj = bvos[0].getCsourcebillhid();// 多编码合同主表主键
			double bcxyje = 0;// 补充协议总金额
			// 回填主表 合同金额
			PmFeebalanceHVO feebalanceHVO = (PmFeebalanceHVO) getHyPubBO()
					.queryByPrimaryKey(PmFeebalanceHVO.class, dbmhtzj);
												 
											  
																  
	
														 
																
			for (RLContractalterBVO bvo : bvos) {
				if (null != bvo.getCsourcebillbid()) {
					PmFeebalanceBVO pmFeebalanceBVO = (PmFeebalanceBVO) getHyPubBO()
							.queryByPrimaryKey(PmFeebalanceBVO.class,
									bvo.getCsourcebillbid());
					if (null != pmFeebalanceBVO) {
						if (null != bvo.getAlt_mny()) {
							bcxyje += bvo.getAlt_mny().toDouble();
							if (flag) {
								if (feebalanceHVO.getDef20() == null
										|| "N".equals(feebalanceHVO.getDef20()))
									pmFeebalanceBVO.setMoney(pmFeebalanceBVO
											.getMoney().add(bvo.getAlt_mny()));
							} else {
								if (feebalanceHVO.getDef20() != null
										&& "Y".equals(feebalanceHVO.getDef20()))
									pmFeebalanceBVO.setMoney(pmFeebalanceBVO
											.getMoney().sub(bvo.getAlt_mny()));
							}
						}
						if (null != bvo.getAlt_mny_num()) {
							if (flag) {
								if (feebalanceHVO.getDef20() == null
										|| "N".equals(feebalanceHVO.getDef20()))
									pmFeebalanceBVO
											.setNum(pmFeebalanceBVO.getNum()
													.add(bvo.getAlt_mny_num()));
							} else {
								if (feebalanceHVO.getDef20() != null
										&& "Y".equals(feebalanceHVO.getDef20()))
									pmFeebalanceBVO
											.setNum(pmFeebalanceBVO.getNum()
													.sub(bvo.getAlt_mny_num()));
							}
						}
															  
									
				   
								  
									  
												
						pmFeebalanceBVO.setStatus(VOStatus.UPDATED);
						getHyPubBO().update(pmFeebalanceBVO);
								   
					}
				}
			}
		
			if (flag) {
				if (feebalanceHVO.getDef20() == null
						|| "N".equals(feebalanceHVO.getDef20())) {
					feebalanceHVO.setContractmoney(feebalanceHVO
							.getContractmoney().add(bcxyje));
					feebalanceHVO.setDef20("Y");
				}
			} else {
				if (feebalanceHVO.getDef20() != null
						&& "Y".equals(feebalanceHVO.getDef20())) {
					feebalanceHVO.setContractmoney(feebalanceHVO
							.getContractmoney().sub(bcxyje));
					feebalanceHVO.setDef20("N");
	  
				}
																  
														
								
								   
											
												
										 
								 
								  
							 
						
																	
			}
			feebalanceHVO.setStatus(VOStatus.UPDATED);
			getHyPubBO().update(feebalanceHVO);
		}
	}

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

	// 删除
	public void pubdeleteBills(AggRLContractalterHVO[] clientFullVOs,
			AggRLContractalterHVO[] originBills) throws BusinessException {
		try {
			// 调用BP
			new AcePmRLContractalterDeleteBP().delete(clientFullVOs);
			/* 反写多编码合同表体累计拉数量 */
			updateAggPmFeebalance(clientFullVOs, false);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggRLContractalterHVO[] pubupdateBills(
			AggRLContractalterHVO[] clientFullVOs,
			AggRLContractalterHVO[] originBills) throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggRLContractalterHVO> transferTool = new BillTransferTool<AggRLContractalterHVO>(
					clientFullVOs);
			AcePmRLContractalterUpdateBP bp = new AcePmRLContractalterUpdateBP();
			// XBX多编码合同保存校验 年度投资计划控制 项目类型--技改类项目
			if (checkIFSave(clientFullVOs)) {
				AggRLContractalterHVO[] retvos = bp.update(clientFullVOs,
						originBills);
				// 构造返回数据
				return transferTool.getBillForToClient(retvos);
			}
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggRLContractalterHVO[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggRLContractalterHVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggRLContractalterHVO> query = new BillLazyQuery<AggRLContractalterHVO>(
					AggRLContractalterHVO.class);
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
	public AggRLContractalterHVO[] pubsendapprovebills(
			AggRLContractalterHVO[] clientFullVOs,
			AggRLContractalterHVO[] originBills) throws BusinessException {
		AcePmRLContractalterSendApproveBP bp = new AcePmRLContractalterSendApproveBP();
		AggRLContractalterHVO[] retvos = bp.sendApprove(clientFullVOs,
				originBills);
		sendOA(retvos);
		return retvos;
	}

	private void sendOA(AggRLContractalterHVO[] retvos)
			throws BusinessException {
		OAFlowVO oaVo = null;
		for (AggRLContractalterHVO temp : retvos) {
			RLContractalterHVO hvo = (RLContractalterHVO) temp.getParentVO();
			OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
					hvo.getPk_org());
			String def2 = orgVO.getDef2();
			if ((hvo.getBill_type()).equals(billType)
					&& null != orgVO.getDef1() && "1".equals(def2)) {
				oaVo = OaWorkFlowUtil.getOAFlowInfo(hvo.getPk_org(), billType);
				if (oaVo == null || oaVo.getIsdr() == 1) {
					// 不走中台，写死
					// tableName = "formtable_main_273";
					JSONArray headData = getMainMap(hvo, def2);
					JSONArray bodyData = getDtaileDataMap(temp.getChildrenVO(),
							def2);
					WorkFlowBill bill = getWorkFlowBill(temp);
					OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
				} else {
					workFId = oaVo.getFlowid();
					tableName = oaVo.getTablename();
					// 获取主表数据
					JSONArray headData = getMainMap(hvo, def2);
					// 获取子表数据
					JSONArray bodyData = getDtaileDataMap(temp.getChildrenVO(),
							def2);
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
					workFlowVO.setWorkflowName("多编码合同补充协议");
					WorkFlowBill bill = OaWorkFlowUtil
							.getWorkFlowBill(workFlowVO);
					bill.setDef3("ZT");
					bill.setDef4(hvo.getBill_code());
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
				}

			} else {

				// 中台单据
				if ("4".equals(def2) && hvo.getBill_type().equals(billType)) {
					if (null == oaVo) {
						oaVo = OaWorkFlowUtil.getOAFlowInfo(hvo.getPk_org(),
								billType);
						workFId = oaVo.getFlowid();
						tableName = oaVo.getTablename();
					}
					if (oaVo != null && oaVo.getIsdr() == 0) {
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
						workFlowVO.setWorkflowName("多编码合同补充协议");
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

		}
	}

	private WorkFlowBill getWorkFlowBill(AggRLContractalterHVO temp)
			throws BusinessException {
		// TODO Auto-generated method stub
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
			workFlowBill.setWorkflowName("多编码合同补充协议");
		} else {
			RLContractalterHVO hvo = (RLContractalterHVO) temp.getParentVO();
			UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
					UserVO.class, hvo.getBillmaker());
			workFlowBill.setDef5(userVO.getUser_code());
			if ("2".equals(getDef2(hvo.getPk_org()))) {
				PsndocVO psndoc = (PsndocVO) getHyPubBO().queryByPrimaryKey(
						PsndocVO.class, userVO.getPk_psndoc());
				workFlowBill.setDef5(psndoc.getId());
			}

			// 如果是中台，则设置走中台接口

			workFlowBill.setPk_group(hvo.getPk_group());
			workFlowBill.setPk_org(hvo.getPk_org());
			workFlowBill.setPk_bill(temp.getPrimaryKey());
			workFlowBill.setUser_name(userVO.getUser_name());
			workFlowBill.setUser_code(userVO.getUser_code());
			workFlowBill.setBill_status("1");
			workFlowBill.setDef4(hvo.getBill_code());
			workFlowBill.setCreationtime(new UFDate().toStdString());
			workFlowBill.setCreator(hvo.getCreator());
			/*
			 * if ("2".equals(getDef2(hvo.getPk_org()))) {
			 * workFlowBill.setWorkflowId(); }else{
			 * workFlowBill.setWorkflowId("205"); }
			 */
			workFlowBill.setWorkflowId("279");
			workFlowBill.setBill_code(hvo.getBill_type());
			workFlowBill.setWorkflowName("多编码合同补充协议");
		}
		return workFlowBill;
	}

	private JSONArray getDtaileDataMap(RLContractalterBVO[] temp, String def2)
			throws BusinessException {
		// TODO Auto-generated method stub
		Map ctPuBMap = getRLContractalterBVO(temp, def2);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(ctPuBMap, jsonConfig);
		return dtlistString;
	}

	private Map getRLContractalterBVO(RLContractalterBVO[] bodyVOS, String def2)
			throws BusinessException {
		// TODO Auto-generated method stub
		Map dtMap = new HashMap();

		/*
		 * if ("2".equals(getDef2(hvo.getPk_org()))) { dtMap.put("tableDBName",
		 * ); }else{ dtMap.put("tableDBName", ); }
		 */
		if ("4".equals(def2)) {
			dtMap.put("tableDBName", tableName + "dt1");
		} else if ("1".equals(def2)) {
			dtMap.put("tableDBName", "formtable_main_273_dt1");
		}
		List workflowRequestTableRecords = new ArrayList();
		for (RLContractalterBVO temp : bodyVOS) {
			// 构造数据
			List workflowRequestTableFields = new ArrayList();
			if ("4".equals(def2)) {
				workflowRequestTableFields = OaWorkFlowUtil.transBean2Map(temp);
			}
			Map workflowRequestTableFieldsMap = new HashMap();

			/* 子表主键 */
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("zbzj",
					temp.getPk_contr_alter_b()));

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

			/* 金额 */
			if (null != temp.getMoney()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("je",
						temp.getMoney() + ""));
			}
			/* 单价 */
			if (null != temp.getUnitmoney()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("dj",
						temp.getUnitmoney() + ""));
			}
			/* 数量 */
			if (null != temp.getNum()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sl",
						temp.getNum() + ""));
			}
			/* 本次协议数量 */
			if (null != temp.getAlt_mny_num()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bcxysl", temp.getAlt_mny_num() + ""));
			}
			/* 本次协议金额 */
			if (null != temp.getAlt_mny()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAdd("bcxyje",
						temp.getAlt_mny() + ""));
			}

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	private JSONArray getMainMap(RLContractalterHVO hvo, String def2)
			throws BusinessException {
		List list = new ArrayList();
		// 全量字段
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		if ("4".equals(def2)) {
			list = OaWorkFlowUtil.transBean2Map(hvo);
			// 项目审批类型
			if (hvo.getHdef11() != null) {
				String xmsplxbm = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"code",
						"nvl(dr,0) = 0 and pk_defdoc = '" + hvo.getHdef11()
								+ "'");
				list.add(OaWorkFlowUtil.listAddObj("xmsplxbm", xmsplxbm));
				String xmsplx = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + hvo.getHdef11()
								+ "'");
				list.add(OaWorkFlowUtil.listAddObj("xmsplx", xmsplx));
			}
		}
		// ---------其他字段begin

		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				hvo.getPk_org());
		if (null != orgVO) {
			/* 组织 */
			list.add(OaWorkFlowUtil.listAddObj("szgs", orgVO.getCode()));
		}
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				hvo.getBillmaker());
		if (null != userVO) {
			/* 制单人名称 */
			list.add(OaWorkFlowUtil.listAddObj("zdrmc", userVO.getUser_name()));

			/* 制单人编码 */
			list.add(OaWorkFlowUtil.listAddObj("zdrzj", userVO.getUser_code()));

			/* 制单日期 */
			list.add(OaWorkFlowUtil.listAddObj("zdrq", null != hvo
					.getBillmaketime() ? hvo.getBillmaketime() + "" : ""));

			/* 制单人身份证 */
			Map sfzh = new HashMap();
			String id = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"id",
					"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc()
							+ "'");
			if (null == id) {
				throw new BusinessException("制单人身份证号码未维护");
			}
			sfzh.put("fieldName", "sfzh");
			sfzh.put("fieldValue", id);
			list.add(sfzh);
		}
		/* 本次协议金额 */
		list.add(OaWorkFlowUtil.listAddObj("bcxyje", hvo.getHdef2()));
		/* 补充协议名称 */
		list.add(OaWorkFlowUtil.listAddObj("bcxymc", hvo.getHdef3()));
		/* 主表主键 */
		list.add(OaWorkFlowUtil.listAddObj("zbzj", hvo.getPk_contr_alter()));
		/* 补充协议号 */
		list.add(OaWorkFlowUtil.listAddObj("bcxyh", hvo.getBill_code()));
		/* 合同编码 */
		/*
		 * String htbmstr = (String)
		 * getHyPubBO().findColValue("pm_feebalance_ct", "bill_code",
		 * "nvl(dr,0) = 0 and pm_feebalance='" + hvo.getPk_contr() + "'");
		 */
		// list.add(OaWorkFlowUtil.listAddObj("htbm", htbmstr));
		list.add(OaWorkFlowUtil.listAddObj("htbm", hvo.getPk_contr()));

		/* 合同名称 */
		/*
		 * String htmcstr = (String)
		 * getHyPubBO().findColValue("pm_feebalance_ct", "bill_name",
		 * "nvl(dr,0) = 0 and pm_feebalance='" + hvo.getPk_contr() + "'");
		 */
		// list.add(OaWorkFlowUtil.listAddObj("htmc", htmcstr));
		list.add(OaWorkFlowUtil.listAddObj("htmc", hvo.getBill_name()));

		/* 供应商主键 */
		list.add(OaWorkFlowUtil.listAddObj("gyszj", hvo.getPk_supplier_name()));

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

		// 代控部门主键
		list.add(OaWorkFlowUtil.listAddObj("dkbmzj", hvo.getPk_dkdept()));

		/* 签约部门主键 */
		list.add(OaWorkFlowUtil.listAddObj("qybmzj", hvo.getPk_qydept()));

		/* 签约部门名称 */
		String qname = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and   pk_dept  = '" + hvo.getPk_qydept() + "'");
		list.add(OaWorkFlowUtil.listAddObj("qybm", qname));

		/* 合同金额 */
		if (null != hvo.getContractmoney()) {
			list.add(OaWorkFlowUtil.listAddObj("htje", hvo.getContractmoney()
					+ ""));
		}

		/* 签约日期 */
		list.add(OaWorkFlowUtil.listAddObj("qyrq", hvo.getSigndate() + ""));

		/* 合同类型 */
		if (null != hvo.getContracttype()) {
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
		if (null != hvo.getZbmethod()) {
			String zbgswhere = " pk_defdoc = '"
					+ hvo.getZbmethod()
					+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'cgfs' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
			String zbmethod = "0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					zbgswhere)) {
				zbmethod = (String) getHyPubBO().findColValue("bd_defdoc",
						"name", zbgswhere);
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
		if (null != hvo.getPk_signer_name()) {
			String pk_signer_name = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and pk_psndoc = '" + hvo.getPk_signer_name()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("qyrmc", pk_signer_name));
		}

		/* 集团 */
		if (null != hvo.getPk_group()) {
			list.add(OaWorkFlowUtil.listAddObj("jt", hvo.getPk_group()));
		}
		/* 项目组织 */
		if (null != hvo.getPk_org()) {
			list.add(OaWorkFlowUtil.listAddObj("xmzz", hvo.getPk_org()));
		}
		/* 创建人 */
		if (null != hvo.getCreator()) {
			list.add(OaWorkFlowUtil.listAddObj("cjr", hvo.getCreator()));
		}
		/* 创建时间 */
		if (null != hvo.getCreationtime()) {
			list.add(OaWorkFlowUtil.listAddObj("cjsj", hvo.getCreationtime()
					+ ""));
		}

		/* 单据日期 */
		if (null != hvo.getDbilldate()) {
			list.add(OaWorkFlowUtil.listAddObj("djrq", hvo.getDbilldate() + ""));
		}
		/* 备注 */
		list.add(OaWorkFlowUtil.listAddObj("bz", hvo.getVmemo()));

		// 是否特殊合同
		if (hvo.getHdef1() != null) {
			String sftsht = (String) getHyPubBO().findColValue("bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc ='" + hvo.getHdef1() + "'");
			if (sftsht != null && sftsht.equals("是")) {
				sftsht = "0";
			} else if (sftsht != null && sftsht.equals("否")) {
				sftsht = "1";
			}
			list.add(OaWorkFlowUtil.listAddObj("sftsht", sftsht));
		}

		// JSONArray arr = JSONArray.fromObject(list);
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	// 收回
	public AggRLContractalterHVO[] pubunsendapprovebills(
			AggRLContractalterHVO[] clientFullVOs,
			AggRLContractalterHVO[] originBills) throws BusinessException {
													 
											  
													  
   
		AcePmRLContractalterUnSendApproveBP bp = new AcePmRLContractalterUnSendApproveBP();
		AggRLContractalterHVO[] retvos = bp.unSend(clientFullVOs, originBills);
		return retvos;
	};

	// 审批
	public AggRLContractalterHVO[] pubapprovebills(
			AggRLContractalterHVO[] clientFullVOs,
			AggRLContractalterHVO[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AcePmRLContractalterApproveBP bp = new AcePmRLContractalterApproveBP();
		AggRLContractalterHVO[] retvos = bp.approve(clientFullVOs, originBills);
		updateAggPmFeebalance(clientFullVOs, true);
		// 审批完成后自动拆分为清单发包合同补充协议
		Set bill_codes = new HashSet<>();// 记录已生成合同
		Set err_bill_codes = new HashSet<>();// 记录报错合同
		for (AggRLContractalterHVO aggRLContractalterHVO : retvos) {
			DbmSplitToContr split = NCLocator.getInstance().lookup(
					DbmSplitToContr.class);
			try {
				String res = split.SplitAlterToContr(aggRLContractalterHVO);
				if (StringUtils.isEmpty(res)) {
					bill_codes.add(aggRLContractalterHVO.getParent()
							.getBill_code() + "拆分成功！");
				} else {
					err_bill_codes.add(aggRLContractalterHVO.getParent()
							.getBill_code() + res);
					String[] userObj = { ContrAlterBillVO.class.getName(),
							ContrAlterHeadVO.class.getName(),
							ContrAlterBodyVO.class.getName() };
					String strWhere = " NVL(DR,0) = 0 AND HDEF7 = '"
							+ aggRLContractalterHVO.getParent().getBill_code()
							+ "'";
					ContrAlterBillVO[] aggvos = (ContrAlterBillVO[]) new HYPubBO()
							.queryBillVOByCondition(userObj, strWhere);
					if (aggvos != null && aggvos.length > 0) {
						IContractAlter bcxyitf = NCLocator.getInstance()
								.lookup(IContractAlter.class);
						bcxyitf.deleteAlter(aggvos);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return retvos;
	}

	// 弃审

	public AggRLContractalterHVO[] pubunapprovebills(
			AggRLContractalterHVO[] clientFullVOs,
			AggRLContractalterHVO[] originBills) throws BusinessException {
		List<String> hasCodes = new ArrayList<>();
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
			// XBX修改，判断是否已经拆分，如果已经拆分，则不允许取消审批
			// 按多编码合同补充协议单号，取清单发包合同
			String sqlwhere = " nvl(dr,0) = 0 and hdef7 = '"
					+ clientFullVOs[i].getParent().getBill_code() + "'";
			// 查询service
			ContrAlterHeadVO[] hvos = (ContrAlterHeadVO[]) getHyPubBO()
					.queryByCondition(ContrAlterHeadVO.class, sqlwhere);
			if (hvos != null && hvos.length > 0) {
				for (ContrAlterHeadVO hvo : hvos) {
					hasCodes.add(hvo.getBill_code());
				}
			}
		}
		if(hasCodes.size() > 0){
			throw new BusinessException("已拆分清单发包合同补充协议，单据号"+hasCodes+",请先删除！");
						   
		}
		AcePmRLContractalterUnApproveBP bp = new AcePmRLContractalterUnApproveBP();
		AggRLContractalterHVO[] retvos = bp.unApprove(clientFullVOs,
				originBills);

		updateAggPmFeebalance(clientFullVOs, false);
		return retvos;
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

	// XBX多编码合同补充协议保存校验 年度投资计划控制 项目类型--技改类项目
	private boolean checkIFSave(AggRLContractalterHVO[] clientFullVOs)
			throws BusinessException {
		// TODO Auto-generated method stub
		boolean flag = Boolean.TRUE;
		for (AggRLContractalterHVO aggPmFeebalance : clientFullVOs) {
			// 查询档案，取校验组织
			Object conforg = (Object) getHyPubBO().findColValue(
					"bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
							+ "FROM BD_DEFDOCLIST WHERE CODE = 'HTSAVEORG')"
							+ " and code = '"
							+ aggPmFeebalance.getParent().getPk_org() + "'");// 组织
			if (conforg != null) {
				// 调用工具类校验是否可保存
				ContractUtils utils = new ContractUtils();
				String error = utils.checkIfSave(aggPmFeebalance, 5);
				if (StringUtils.isNotEmpty(error)) {
					throw new BusinessException(error);
				}
			}
		}
		return flag;
	}
}