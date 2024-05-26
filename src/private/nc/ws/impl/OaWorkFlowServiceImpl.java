package nc.ws.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pm.util.db.BillQueryByCond;
import nc.impl.pub.filesystem.FileExAttrDAO;
import nc.impl.pubapp.pattern.database.DBTool;
import nc.itf.arap.pay.IArapPayBillQueryService;
import nc.itf.arap.pub.IBXBillPublic;
import nc.itf.erm.jkbx.IErmServiceForI8;
import nc.itf.hr.wa.IPayrollQueryService;
import nc.itf.pm.IPmRLContractalterMaintain;
import nc.itf.portal.IGetIntoPortal;
import nc.itf.pu.m21.IOrderQuery;
import nc.itf.ta.ILeaveApplyQueryMaintain;
import nc.itf.ta.IOvertimeApplyQueryMaintain;
import nc.itf.trn.transmng.ITransmngManageService;
import nc.itf.trn.transmng.ITransmngQueryService;
import nc.itf.uap.pf.IPfExchangeService;
import nc.itf.uap.pf.IWorkflowMachine;
import nc.itf.uap.pf.IplatFormEntry;
import nc.jdbc.framework.exception.DbException;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.pubitf.pmr.pm.IPmFeebalanceCtMaintain;
import nc.pubitf.pmr.pm.IPmRLContractbalanceMaintain;
import nc.ui.pcm.utils.GetDao;
import nc.vo.arap.gathering.AggGatheringBillVO;
import nc.vo.arap.gathering.GatheringBillItemVO;
import nc.vo.arap.gathering.GatheringBillVO;
import nc.vo.arap.pay.AggPayBillVO;
import nc.vo.arap.pay.PayBillVO;
import nc.vo.arap.payable.AggPayableBillVO;
import nc.vo.arap.payable.PayableBillItemVO;
import nc.vo.arap.payable.PayableBillVO;
import nc.vo.arap.receivable.AggReceivableBillVO;
import nc.vo.arap.receivable.ReceivableBillItemVO;
import nc.vo.arap.receivable.ReceivableBillVO;
import nc.vo.cmp.apply.AggApplyVO;
import nc.vo.cmp.apply.ApplyBVO;
import nc.vo.cmp.apply.ApplyVO;
import nc.vo.cmp.bill.BillAggVO;
import nc.vo.cmp.bill.BillDetailVO;
import nc.vo.cmp.bill.TransformBillAggVO;
import nc.vo.cmp.bill.TransformBillVO;
import nc.vo.cmp.cash.AggCashDrawVO;
import nc.vo.cmp.cash.CashDrawVO;
import nc.vo.ct.purdaily.entity.AggCtPuVO;
import nc.vo.ct.purdaily.entity.CtPuBVO;
import nc.vo.ct.purdaily.entity.CtPuVO;
import nc.vo.ct.purdaily.entity.RlPmeFile;
import nc.vo.ct.saledaily.entity.AggCtSaleVO;
import nc.vo.ct.saledaily.entity.CtSaleBVO;
import nc.vo.ct.saledaily.entity.CtSaleVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKBusItemVO;
import nc.vo.ep.bx.JKHeaderVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.erm.accruedexpense.AccruedDetailVO;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AccruedVerifyVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.fct.ap.entity.AggCtApVO;
import nc.vo.fct.ap.entity.CtApBVO;
import nc.vo.fct.ap.entity.CtApVO;
import nc.vo.fct.ar.entity.AggCtArVO;
import nc.vo.fct.ar.entity.CtArTermVO;
import nc.vo.fct.ar.entity.CtArVO;
import nc.vo.hrss.pub.FileNodeVO1;
import nc.vo.ic.m4455.entity.SapplyBillBodyVO;
import nc.vo.ic.m4455.entity.SapplyBillHeadVO;
import nc.vo.ic.m4455.entity.SapplyBillVO;
import nc.vo.ic.m4k.entity.WhsTransBillBodyVO;
import nc.vo.ic.m4k.entity.WhsTransBillHeaderVO;
import nc.vo.ic.m4k.entity.WhsTransBillVO;
import nc.vo.org.OrgVO;
import nc.vo.pbm.budget.BudgetBillVO;
import nc.vo.pbm.budget.BudgetHeadVO;
import nc.vo.pbm.budgetadd.BudgetAddBillVO;
import nc.vo.pbm.budgetadd.BudgetAddBodyVO;
import nc.vo.pbm.budgetadd.BudgetAddHeadVO;
import nc.vo.pbm.materialplan.MaterialPlanBillVO;
import nc.vo.pbm.materialplan.MaterialPlanBodyVO;
import nc.vo.pbm.materialplan.MaterialPlanHeadVO;
import nc.vo.pbm.materialplanadd.MaterialPlanAddBillVO;
import nc.vo.pbm.materialplanadd.MaterialPlanAddBodyVO;
import nc.vo.pbm.materialplanadd.MaterialPlanAddHeadVO;
import nc.vo.pcm.contract.ContrHeadVO;
import nc.vo.pcm.contract.ContrWorksVO;
import nc.vo.pcm.contract.ContractBillVO;
import nc.vo.pcm.contractalter.ContrAlterBillVO;
import nc.vo.pcm.contractalter.ContrAlterBodyVO;
import nc.vo.pcm.contractalter.ContrAlterHeadVO;
import nc.vo.pcm.contractbalance.ContractBalanceBillVO;
import nc.vo.pcm.contractbalance.ContractBalanceBodyVO;
import nc.vo.pcm.contractbalance.ContractBalanceHeadVO;
import nc.vo.pcm.contractprepay.ContractPrepayBillVO;
import nc.vo.pcm.contractprepay.ContractPrepayHeadVO;
import nc.vo.pcm.contractschedule.ContractScheduleBillVO;
import nc.vo.pcm.contractschedule.ContractScheduleBodyVO;
import nc.vo.pcm.contractschedule.ContractScheduleHeadVO;
import nc.vo.pcm.feebalance.FeeBalanceBillVO;
import nc.vo.pcm.feebalance.FeeBalanceBodyVO;
import nc.vo.pcm.feebalance.FeeBalanceHeadVO;
import nc.vo.pcm.martcontalter.MContr_AlterBillVO;
import nc.vo.pcm.martcontalter.MContr_AlterBodyVO;
import nc.vo.pcm.martcontalter.MContr_AlterHeadVO;
import nc.vo.pcm.materialacc.AggMaterialaccVO;
import nc.vo.pcm.materialacc.MaterialaccBodyVO;
import nc.vo.pcm.materialacc.MaterialaccHeadVO;
import nc.vo.pcm.materialacc.SecondMaterialBodyVO;
import nc.vo.phm.projectproposal.ProjectProposalBillVO;
import nc.vo.phm.projectproposal.ProjectProposalHeadVO;
import nc.vo.phm.projectregister.ProjectRegisterBillVO;
import nc.vo.phm.projectregister.ProjectRegisterHeadVO;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pm.rlcontractalter.AggRLContractalterHVO;
import nc.vo.pm.rlcontractbalance.AggRLContractbalanceHVO;
import nc.vo.pmfile.documentcenter.DocumentCenterVO;
import nc.vo.ppm.projectcheck.ProductTransferVO;
import nc.vo.ppm.projectcheck.ProjectCheckBillVO;
import nc.vo.ppm.projectcheck.ProjectCheckHeadVO;
import nc.vo.ppm.startreport.StartReportBillVO;
import nc.vo.ppm.startreport.StartReportHeadVO;
import nc.vo.pu.m20.entity.PraybillHeaderVO;
import nc.vo.pu.m20.entity.PraybillItemVO;
import nc.vo.pu.m20.entity.PraybillVO;
import nc.vo.pu.m21.entity.OrderHeaderVO;
import nc.vo.pu.m21.entity.OrderVO;
import nc.vo.pu.m25.entity.InvoiceHeaderVO;
import nc.vo.pu.m25.entity.InvoiceItemVO;
import nc.vo.pu.m25.entity.InvoiceVO;
import nc.vo.pu.m422x.entity.StoreReqAppHeaderVO;
import nc.vo.pu.m422x.entity.StoreReqAppItemVO;
import nc.vo.pu.m422x.entity.StoreReqAppVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.filesystem.FileExAttrVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.sm.UserVO;
import nc.vo.so.m30.entity.SaleOrderBVO;
import nc.vo.so.m30.entity.SaleOrderHVO;
import nc.vo.so.m30.entity.SaleOrderVO;
import nc.vo.ta.leave.AggLeaveVO;
import nc.vo.ta.leave.LeavehVO;
import nc.vo.ta.overtime.AggOvertimeVO;
import nc.vo.ta.overtime.OvertimehVO;
import nc.vo.to.m5x.entity.BillHeaderVO;
import nc.vo.to.m5x.entity.BillItemVO;
import nc.vo.to.m5x.entity.BillVO;
import nc.vo.trn.transmng.AggStapply;
import nc.vo.trn.transmng.StapplyVO;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.payroll.AggPayrollVO;
import nc.vo.wa.payroll.PayrollVO;
import nc.ws.intf.IOaWorkFlowService;
import nc.ws.intf.IThirdQueryService;
import nc.ws.intf.OALogVO;
import nc.ws.intf.Result;
import nc.ws.intf.WorkFlowBill;

@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class OaWorkFlowServiceImpl implements IOaWorkFlowService {

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

	@Override
	public String oaCallBack(String type, Integer requestid, String pkorg)
			throws BusinessException {
		String pkGroup = (String) getHyPubBO().findColValue("org_group",
				"pk_group ", " code = '" + IThirdQueryService.GROUP_CODE + "'");
		InvocationInfoProxy.getInstance().setGroupId(pkGroup);// 设置默认的集团主键
		InvocationInfoProxy.getInstance().setUserDataSource("RLJT");
		// String userId = (String) getHyPubBO().findColValue("sm_user",
		// "cuserid",
		// " user_code = '" + billmaker + "'");
		String strWhere = " nvl(dr,0) = 0 and requestid = " + requestid
				+ " and def3 <> 'ZT'";
		if (requestid < 0) {
			strWhere = " nvl(dr,0) = 0 and requestid = " + requestid * -1
					+ " and def3 = 'ZT'";
		}
		WorkFlowBill[] workFlowBill = (WorkFlowBill[]) getHyPubBO()
				.queryByCondition(WorkFlowBill.class, strWhere);
		if (null == workFlowBill || workFlowBill.length != 1) {
			throw new BusinessException("未查询到流程日志");
		}
		InvocationInfoProxy.getInstance().setUserId(
				workFlowBill[0].getCreator());// 设置默认操作员主键
		workFlowBill[0].setStatus(VOStatus.UPDATED);
		if (workFlowBill[0].getBill_code() == null
				&& workFlowBill[0].getDef4().indexOf("4Z02") != -1) {
			// 多编码合同结算单
			return apprRLContractbalanceHVO(type, workFlowBill[0]);
		}
		switch (workFlowBill[0].getBill_code()) {
		/* 物资及服务需求单 */
		case "4D14":
			return apprMaterialPlanHeadVO(type, workFlowBill[0]);
			/* 采购合同 */
		case "Z2":
			return apprCtPuVO(type, workFlowBill[0]);
			/* 物资及服务需求单调整单 */
		case "4D16":
			return apprMaterialPlanAddBillVO(type, workFlowBill[0]);
			/* 清单发包合同（设计，施工，监理） */
		case "4D42":
			return apprContractBillVO(type, workFlowBill[0]);
			/* 付款合同 */
		case "FCT1":
			return appCtApVO(type, workFlowBill[0]);

			/* 收款合同 */
		case "FCT2":
			return appCtArVO(type, workFlowBill[0]);

			// /*入职审批 */
			// case "6101":
			// return appEntryapplyVO (type, workFlowBill[0]);

			/* 预算调整单XBX修改 */
		case "4D20":
			return appBudgetAddHeadVO(type, workFlowBill[0]);

			/* 补充协议 */
		case "4D44":
			return appContrAlterHeadVO(type, workFlowBill[0]);

			/* 多编码合同 */
		case "4D83":
			return appFeeBalanceHeadVO(type, workFlowBill[0]);

			/* // 勘察申请单 // 设计申请单 //刨掘申请单 //控制价委托申请单 */
		case "4D15":
			return appProjectProposalHeadVO(type, workFlowBill[0]);

			// 追加项目申请单
		case "4D11":
			return appProjectRegisterHeadVO(type, workFlowBill[0]);

			// 竣工验收
		case "4D36":
			return appProjectCheckHeadVO(type, workFlowBill[0]);

			// 开工单
		case "4D23":
			return appStartReportHeadVO(type, workFlowBill[0]);

			// 采购订单
		case "21":
			return appOrderHeaderVO(type, workFlowBill[0]);

			// 付款单
		case "F3":
			return aggPayBillVO(type, workFlowBill[0]);

			// 应付单
		case "F1":
			return aggPayableBillVO(type, workFlowBill[0]);

			// 应收单
		case "F0":
			return aggReceivableBillVO(type, workFlowBill[0]);

			// 薪资发放申请单
		case "6302":
			return aggPayrollVO(type, workFlowBill[0]);

			// 加班申请单
		case "6405":
			return aggOvertimeVO(type, workFlowBill[0]);

			// 休假申请单
		case "6404":
			return aggLeaveVO(type, workFlowBill[0]);

			// 离职申请单
		case "6115":
			return aggStapply(type, workFlowBill[0]);
			// 调配申请单
		case "6113":
			return aggStapply(type, workFlowBill[0]);
			// 加班费申请单、差旅费申请单
		case "261X":
			return matterAppVO(type, workFlowBill[0]);
			// 报销单
		case "264X":
			return bXVO(type, workFlowBill[0]);
			// 借款单
		case "263X":
			return JKVO(type, workFlowBill[0]);
			// 收款单
		case "F2":
			return aggGatheringBillVO(type, workFlowBill[0]);
			// 多编码合同（新）
		case "4Z01":
			return appFeeBalanceVO(type, workFlowBill[0]);
			// 多编码合同补充协议
		case "4Z05":
			return appFeeBalanceAlterVO(type, workFlowBill[0]);
			// 物资需求申请单
		case "422X":
			return appStoreReqAppVO(type, workFlowBill[0]);
			// 请购单
		case "20":
			return appPraybillVO(type, workFlowBill[0]);
			// 采购发票
		case "25":
			return appInvoiceVO(type, workFlowBill[0]);
			// 销售合同
		case "Z3":
			return appAggCtSaleVO(type, workFlowBill[0]);
			// 销售订单
		case "30":
			return appSaleOrderVO(type, workFlowBill[0]);
			// 转库
		case "4K":
			return appWhsTransBillVO(type, workFlowBill[0]);
			// 项目预算
		case "4D18":
			return appBudgetBillVO(type, workFlowBill[0]);
			// 项目销售合同
		case "4D60":
			return appContractBillVO(type, workFlowBill[0]);
			// 补充协议(项目销售合同)
		case "4D61":
			return appMContr_AlterBillVO(type, workFlowBill[0]);
			// 进度款单
		case "4D48":
			return appContractScheduleBillVO(type, workFlowBill[0]);
			// 结算单
		case "4D50":
			return appContractBalanceBillVO(type, workFlowBill[0]);
			// 预算调整单
		case "36D1":
			return appAggApplyVO(type, workFlowBill[0]);
			// 出库申请单
		case "4455":
			return appSapplyBillVO(type, workFlowBill[0]);
			// 预付款单
		case "4D46":
			return appContractPrepayBill(type, workFlowBill[0]);
			// 材料结算
		case "4D39":
			return appAggMaterialaccVO(type, workFlowBill[0]);
			// 费用预提单
		case "262X":
			return appAggAccruedBill(type, workFlowBill[0]);
			// 现金支取
		case "36S2":
			return appAggCashDrawVO(type, workFlowBill[0]);
			// 调拨订单
		case "5X":
			return appBillVO(type, workFlowBill[0]);
			// 付款结算
		case "F5":
			return appBillAggVO(type, workFlowBill[0]);
			/* 划账结算单 */
		case "36S4":
			return appTransformbillVO(type, workFlowBill[0]);
		default:
			return Result.error("审批处理出现异常");
		}
	}

	// 划账结算单
	private String appTransformbillVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		// TODO Auto-generated method stub
		TransformBillVO hvo = (TransformBillVO) getHyPubBO().queryByPrimaryKey(
				TransformBillVO.class, workFlowBill.getPk_bill());
		TransformBillAggVO aggvo = new TransformBillAggVO();
		aggvo.setParent(hvo);
		SaveOALog(hvo.getVbillno(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "36S4", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "36S4",
						worknoteVO, aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "36S4", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	// 应收单
	private String aggReceivableBillVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		AggReceivableBillVO aggvo = new AggReceivableBillVO();
		ReceivableBillVO hvo = (ReceivableBillVO) getHyPubBO()
				.queryByPrimaryKey(ReceivableBillVO.class,
						workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		ReceivableBillItemVO[] bvos = (ReceivableBillItemVO[]) getHyPubBO()
				.queryByCondition(
						ReceivableBillItemVO.class,
						"nvl(dr,0) = 0 and pk_recbill = '"
								+ workFlowBill.getPk_bill() + "'");
		aggvo.setChildrenVO(bvos);
		SaveOALog(hvo.getBillno(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("SIGNAL", aggvo.getParentVO()
								.getAttributeValue("pk_tradetype").toString(),
								aggvo, hmPfExParams);
				worknoteVO.setChecknote("批准");
				worknoteVO.setApproveresult("Y");
				getIplatFormEntry().processAction("SIGNAL", "F0", worknoteVO,
						aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("RECALL", "F0", null, aggvo,
						null, getEparam());
			}
		}
		return Result.success();
	}

	// 物资需求申请单 po_storereq
	private String appStoreReqAppVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		StoreReqAppVO aggvo = new StoreReqAppVO();
		StoreReqAppHeaderVO hvo = (StoreReqAppHeaderVO) getHyPubBO()
				.queryByPrimaryKey(StoreReqAppHeaderVO.class,
						workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		StoreReqAppItemVO[] bvos = (StoreReqAppItemVO[]) getHyPubBO()
				.queryByCondition(
						StoreReqAppItemVO.class,
						"nvl(dr,0) = 0 and pk_storereq = '"
								+ workFlowBill.getPk_bill() + "'");
		aggvo.setChildrenVO(bvos);
		SaveOALog(hvo.getVbillcode(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				/*
				 * getIplatFormEntry().processAction("APPROVE", "422X", null,
				 * aggvo, null, getEparam());
				 */
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "422X", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "422X",
						worknoteVO, aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "422X", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	// 请购单 po_praybill
	private String appPraybillVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		PraybillVO aggvo = new PraybillVO();
		PraybillHeaderVO hvo = (PraybillHeaderVO) getHyPubBO()
				.queryByPrimaryKey(PraybillHeaderVO.class,
						workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		PraybillItemVO[] bvos = (PraybillItemVO[]) getHyPubBO()
				.queryByCondition(
						PraybillItemVO.class,
						"nvl(dr,0) = 0 and pk_praybill = '"
								+ workFlowBill.getPk_bill() + "'");
		aggvo.setChildrenVO(bvos);
		SaveOALog(hvo.getVbillcode(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				/*
				 * getIplatFormEntry().processAction("APPROVE", "20", null,
				 * aggvo, null, getEparam());
				 */
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "20", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "20", worknoteVO,
						aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "20", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	// 采购发票 po_invoice
	private String appInvoiceVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		InvoiceVO aggvo = new InvoiceVO();
		InvoiceHeaderVO hvo = (InvoiceHeaderVO) getHyPubBO().queryByPrimaryKey(
				InvoiceHeaderVO.class, workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		InvoiceItemVO[] bvos = (InvoiceItemVO[]) getHyPubBO().queryByCondition(
				InvoiceItemVO.class,
				"nvl(dr,0) = 0 and pk_invoice = '" + workFlowBill.getPk_bill()
						+ "'");
		aggvo.setTableVO("po_invoice_b", bvos);
		SaveOALog(hvo.getVbillcode(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				/*
				 * getIplatFormEntry().processAction("APPROVE", "25", null,
				 * aggvo, null, getEparam());
				 */
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "25", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "25", worknoteVO,
						aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "25", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	// 销售合同 ct_sale
	private String appAggCtSaleVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		AggCtSaleVO aggvo = new AggCtSaleVO();
		CtSaleVO hvo = (CtSaleVO) getHyPubBO().queryByPrimaryKey(
				CtSaleVO.class, workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		CtSaleBVO[] bvos = (CtSaleBVO[]) getHyPubBO().queryByCondition(
				CtSaleBVO.class,
				"nvl(dr,0) = 0 and pk_ct_sale = '" + workFlowBill.getPk_bill()
						+ "'");
		aggvo.setCtSaleBVO(bvos);
		SaveOALog(hvo.getVbillcode(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				/*
				 * getIplatFormEntry().processAction("APPROVE", "Z3", null,
				 * aggvo, null, getEparam());
				 */
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "Z3", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "Z3", worknoteVO,
						aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "Z3", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	// 销售订单 so_saleorder
	private String appSaleOrderVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		SaleOrderVO aggvo = new SaleOrderVO();
		SaleOrderHVO hvo = (SaleOrderHVO) getHyPubBO().queryByPrimaryKey(
				SaleOrderHVO.class, workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		SaleOrderBVO[] bvos = (SaleOrderBVO[]) getHyPubBO().queryByCondition(
				SaleOrderBVO.class,
				"nvl(dr,0) = 0 and csaleorderid = '"
						+ workFlowBill.getPk_bill() + "'");
		aggvo.setChildrenVO(bvos);
		SaveOALog(hvo.getVbillcode(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				/*
				 * getIplatFormEntry().processAction("APPROVE", "30", null,
				 * aggvo, null, getEparam());
				 */
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "30", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "30", worknoteVO,
						aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVE", "30", null, aggvo,
						null, getEparam());
			}
		}
		return Result.success();
	}

	// 出库申请单 ic_sapply_h
	private String appSapplyBillVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		SapplyBillVO aggvo = new SapplyBillVO();
		SapplyBillHeadVO hvo = (SapplyBillHeadVO) getHyPubBO()
				.queryByPrimaryKey(SapplyBillHeadVO.class,
						workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		SapplyBillBodyVO[] bvos = (SapplyBillBodyVO[]) getHyPubBO()
				.queryByCondition(
						SapplyBillBodyVO.class,
						"nvl(dr,0) = 0 and cgeneralhid = '"
								+ workFlowBill.getPk_bill() + "'");
		aggvo.setChildrenVO(bvos);
		SaveOALog(hvo.getVbillcode(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				/*
				 * getIplatFormEntry().processAction("APPROVE", "4455", null,
				 * aggvo, null, getEparam());
				 */
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "4455", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "4455",
						worknoteVO, aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVE", "4455", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	// 转库单 ic_whstrans_h
	private String appWhsTransBillVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		WhsTransBillVO aggvo = new WhsTransBillVO();
		WhsTransBillHeaderVO hvo = (WhsTransBillHeaderVO) getHyPubBO()
				.queryByPrimaryKey(WhsTransBillHeaderVO.class,
						workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		WhsTransBillBodyVO[] bvos = (WhsTransBillBodyVO[]) getHyPubBO()
				.queryByCondition(
						WhsTransBillBodyVO.class,
						"nvl(dr,0) = 0 and cspecialhid = '"
								+ workFlowBill.getPk_bill() + "'");
		aggvo.setChildrenVO(bvos);
		SaveOALog(hvo.getVbillcode(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				/*
				 * getIplatFormEntry().processAction("APPROVE", "4K", null,
				 * aggvo, null, getEparam());
				 */
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "4K", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "4K", worknoteVO,
						aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "4K", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	// 项目预算 pm_budget
	private String appBudgetBillVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		/*
		 * BudgetBillVO aggvo = new BudgetBillVO(); /*BudgetHeadVO hvo =
		 * (BudgetHeadVO) getHyPubBO().queryByPrimaryKey( BudgetHeadVO.class,
		 * workFlowBill.getPk_bill()); aggvo.setParent(hvo);
		 * BudgetFactorBodyVO[] bvos = (BudgetFactorBodyVO[]) getHyPubBO()
		 * .queryByCondition( BudgetFactorBodyVO.class,
		 * "nvl(dr,0) = 0 and pk_budget = '" + workFlowBill.getPk_bill() + "'");
		 * aggvo.setTableVO("pm_budgetfactor", bvos); BudgetCBSBodyVO[] cbsbvos
		 * = (BudgetCBSBodyVO[]) getHyPubBO() .queryByCondition(
		 * BudgetCBSBodyVO.class, "nvl(dr,0) = 0 and pk_budget = '" +
		 * workFlowBill.getPk_bill() + "'"); aggvo.setTableVO("pm_budgetcbs",
		 * cbsbvos);
		 */
		// 查询项目预算VO
		BillQueryByCond<AbstractBill> queryTool = new BillQueryByCond(
				BudgetBillVO.class);
		AbstractBill[] aggBillVOS = (AbstractBill[]) queryTool.query(
				"and nvl(dr,0) = 0 and pk_budget = '"
						+ workFlowBill.getPk_bill() + "'", null, true);
		BudgetBillVO aggvo = (BudgetBillVO) aggBillVOS[0];
		BudgetHeadVO hvo = aggvo.getParentVO();
		SaveOALog(hvo.getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				/*
				 * getIplatFormEntry().processAction("APPROVE", "4D18", null,
				 * aggvo, null, getEparam());
				 */
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", hvo.getTransi_type(), aggvo,
								hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				try {
					HashMap<String, Object> eParam = new HashMap<String, Object>();
					eParam.put("notechecked", "notechecked");
					eParam.put("ACTIONTYPE", "RELEASED");
					getIplatFormEntry().processAction("APPROVE",
							hvo.getTransi_type(), worknoteVO, aggvo, null,
							eParam);
					getHyPubBO().update(workFlowBill);
				} catch (BusinessException e) {
					// TODO: handle exception
					e.printStackTrace();
					System.out.println(e.getMessage());
				}

			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "4D18", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	// 项目销售合同 pm_mcontr
	private String appContractBillVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		nc.vo.pcm.marketcontract.ContractBillVO aggvo = new nc.vo.pcm.marketcontract.ContractBillVO();
		nc.vo.pcm.marketcontract.ContractHeadVO hvo = (nc.vo.pcm.marketcontract.ContractHeadVO) getHyPubBO()
				.queryByPrimaryKey(
						nc.vo.pcm.marketcontract.ContractHeadVO.class,
						workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		nc.vo.pcm.marketcontract.ContrWorksVO[] bvos = (nc.vo.pcm.marketcontract.ContrWorksVO[]) getHyPubBO()
				.queryByCondition(
						nc.vo.pcm.marketcontract.ContrWorksVO.class,
						"nvl(dr,0) = 0 and pk_mar_contr = '"
								+ workFlowBill.getPk_bill() + "'");
		aggvo.setTableVO("pm_mcontr_works", bvos);
		SaveOALog(hvo.getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				/*
				 * getIplatFormEntry().processAction("APPROVE", "4D60", null,
				 * aggvo, null, getEparam());
				 */
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "4D60", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "4D60",
						worknoteVO, aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "4D60", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	// 补充协议（项目销售合同） pm_mcontr_alter
	private String appMContr_AlterBillVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		MContr_AlterBillVO aggvo = new MContr_AlterBillVO();
		MContr_AlterHeadVO hvo = (MContr_AlterHeadVO) getHyPubBO()
				.queryByPrimaryKey(MContr_AlterHeadVO.class,
						workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		MContr_AlterBodyVO[] bvos = (MContr_AlterBodyVO[]) getHyPubBO()
				.queryByCondition(
						MContr_AlterBodyVO.class,
						"nvl(dr,0) = 0 and pk_mcontr_alter = '"
								+ workFlowBill.getPk_bill() + "'");
		aggvo.setChildrenVO(bvos);
		SaveOALog(hvo.getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				/*
				 * getIplatFormEntry().processAction("APPROVE", "4D61", null,
				 * aggvo, null, getEparam());
				 */
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "4D61", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "4D61",
						worknoteVO, aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "4D61", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	// 进度款单 pm_contr_sche
	private String appContractScheduleBillVO(String type,
			WorkFlowBill workFlowBill) throws BusinessException {
		ContractScheduleBillVO aggvo = new ContractScheduleBillVO();
		ContractScheduleHeadVO hvo = (ContractScheduleHeadVO) getHyPubBO()
				.queryByPrimaryKey(ContractScheduleHeadVO.class,
						workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		ContractScheduleBodyVO[] bvos = (ContractScheduleBodyVO[]) getHyPubBO()
				.queryByCondition(
						ContractScheduleBodyVO.class,
						"nvl(dr,0) = 0 and pk_contr_sche = '"
								+ workFlowBill.getPk_bill() + "'");
		aggvo.setChildrenVO(bvos);
		SaveOALog(hvo.getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				/*
				 * getIplatFormEntry().processAction("APPROVE", "4D48", null,
				 * aggvo, null, getEparam());
				 */
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "4D48", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "4D48",
						worknoteVO, aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "4D48", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	// 结算单 pm_contr_bal
	private String appContractBalanceBillVO(String type,
			WorkFlowBill workFlowBill) throws BusinessException {
		ContractBalanceBillVO aggvo = new ContractBalanceBillVO();
		ContractBalanceHeadVO hvo = (ContractBalanceHeadVO) getHyPubBO()
				.queryByPrimaryKey(ContractBalanceHeadVO.class,
						workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		ContractBalanceBodyVO[] bvos = (ContractBalanceBodyVO[]) getHyPubBO()
				.queryByCondition(
						ContractBalanceBodyVO.class,
						"nvl(dr,0) = 0 and pk_contr_bal = '"
								+ workFlowBill.getPk_bill() + "'");
		aggvo.setChildrenVO(bvos);
		SaveOALog(hvo.getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				/*
				 * getIplatFormEntry().processAction("APPROVE", "4D50", null,
				 * aggvo, null, getEparam());
				 */
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "4D50", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "4D50",
						worknoteVO, aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "4D50", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	// 付款申请 cmp_apply
	private String appAggApplyVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		AggApplyVO aggvo = new AggApplyVO();
		ApplyVO hvo = (ApplyVO) getHyPubBO().queryByPrimaryKey(ApplyVO.class,
				workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		ApplyBVO[] bvos = (ApplyBVO[]) getHyPubBO().queryByCondition(
				ApplyBVO.class,
				"nvl(dr,0) = 0 and pk_apply = '" + workFlowBill.getPk_bill()
						+ "'");
		aggvo.setChildrenVO(bvos);
		SaveOALog(hvo.getVbillno(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				/*
				 * getIplatFormEntry().processAction("APPROVE", "36D1", null,
				 * aggvo, null, getEparam());
				 */
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "36D1", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "36D1",
						worknoteVO, aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "36D1", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	// 多编码合同（新）
	private String appFeeBalanceVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		AggPmFeebalance[] aggvoS = (AggPmFeebalance[]) NCLocator.getInstance()
				.lookup(IPmFeebalanceCtMaintain.class)
				.queryObjectByPks(new String[] { workFlowBill.getPk_bill() });
		AggPmFeebalance aggvo = aggvoS.length <= 0 ? null : aggvoS[0];
		SaveOALog(aggvo.getParent().getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "4Z01", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "4Z01",
						worknoteVO, aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "4Z01", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	// 多编码合同补充协议
	private String appFeeBalanceAlterVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		AggRLContractalterHVO[] aggvoS = (AggRLContractalterHVO[]) NCLocator
				.getInstance().lookup(IPmRLContractalterMaintain.class)
				.queryObjectByPks(new String[] { workFlowBill.getPk_bill() });
		AggRLContractalterHVO aggvo = aggvoS.length <= 0 ? null : aggvoS[0];
		SaveOALog(aggvo.getParent().getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "4Z05", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "4Z05",
						worknoteVO, aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "4Z05", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	// 多编码合同结算单
	private String apprRLContractbalanceHVO(String type,
			WorkFlowBill workFlowBill) throws BusinessException {
		// TODO Auto-generated method stub
		AggRLContractbalanceHVO[] aggvoS = (AggRLContractbalanceHVO[]) NCLocator
				.getInstance().lookup(IPmRLContractbalanceMaintain.class)
				.queryObjectByPks(new String[] { workFlowBill.getPk_bill() });
		AggRLContractbalanceHVO aggvo = aggvoS.length <= 0 ? null : aggvoS[0];
		SaveOALog(aggvo.getParent().getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "4Z02", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "4Z02",
						worknoteVO, aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "4Z02", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	private String aggPayrollVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		AggPayrollVO aggvo = (AggPayrollVO) NCLocator.getInstance()
				.lookup(IPayrollQueryService.class)
				.queryByPk(workFlowBill.getPk_bill());
		PayrollVO hvo = (PayrollVO) aggvo.getParentVO();
		SaveOALog(hvo.getBillcode(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				getIplatFormEntry().processAction("APPROVE", "6302", null,
						aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("RECALL", "6302", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	private String JKVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		JKVO aggvo = new JKVO();
		JKHeaderVO hvo = (JKHeaderVO) getHyPubBO().queryByPrimaryKey(
				JKHeaderVO.class, workFlowBill.getPk_bill());
		JKBusItemVO[] bvos = (JKBusItemVO[]) getHyPubBO()
				.queryByCondition(
						JKBusItemVO.class,
						"nvl(dr,0) = 0 and pk_jkbx='"
								+ workFlowBill.getPk_bill() + "'");
		aggvo.setParentVO(hvo);
		aggvo.setChildrenVO(bvos);
		SaveOALog(hvo.getDjbh(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("SIGNAL", "263X", aggvo, hmPfExParams);
				worknoteVO.setChecknote("批准");
				worknoteVO.setApproveresult("Y");
				getIplatFormEntry().processAction("SIGNAL", "263X", worknoteVO,
						aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("RECALL", "263X", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	private String bXVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		BXVO aggvo = new BXVO();
		BXHeaderVO hvo = (BXHeaderVO) getHyPubBO().queryByPrimaryKey(
				BXHeaderVO.class, workFlowBill.getPk_bill());
		BXBusItemVO[] bvos = (BXBusItemVO[]) getHyPubBO()
				.queryByCondition(
						BXBusItemVO.class,
						"nvl(dr,0) = 0 and pk_jkbx='"
								+ workFlowBill.getPk_bill() + "'");
		aggvo.setParentVO(hvo);
		aggvo.setChildrenVO(bvos);
		SaveOALog(hvo.getDjbh(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("SIGNAL", "264X", aggvo, hmPfExParams);
				worknoteVO.setChecknote("批准");
				worknoteVO.setApproveresult("Y");
				getIplatFormEntry().processAction("SIGNAL", "264X", worknoteVO,
						aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				// 工程共享驳回删除原单据
				String jylx = hvo.getDjlxbm();// 交易类型编码
				if ("264X-Cxx-gcfkht".equals(jylx)
						|| "264X-Cxx-HFCGHT".equals(jylx)
						|| "264X-Cxx-HFXSHT".equals(jylx)
						|| "264X-Cxx-gcskht".equals(jylx)) {
					String mes = NCLocator.getInstance().lookup(IErmServiceForI8.class)
							.rollAggBillVO(hvo.getPrimaryKey(), "264X", "");
					if (!"".equals(mes)) {
						throw new BusinessException("OA驳回报错："+mes);
					}
				} else {
					workFlowBill.setDef1("return");
					workFlowBill.setBill_status("0");
					getHyPubBO().update(workFlowBill);
					getIplatFormEntry().processAction("RECALL", "264X", null,
							aggvo, null, getEparam());
				}
			}
		}
		return Result.success();
	}

	private String matterAppVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		AggMatterAppVO aggvo = ((IErmMatterAppBillQuery) NCLocator
				.getInstance().lookup(IErmMatterAppBillQuery.class))
				.queryBillByPK(workFlowBill.getPk_bill());
		SaveOALog(aggvo.getParentVO().getBillno(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("SIGNAL", aggvo.getParentVO()
								.getAttributeValue("pk_tradetype").toString(),
								aggvo, hmPfExParams);
				worknoteVO.setChecknote("批准");
				worknoteVO.setApproveresult("Y");
				getIplatFormEntry().processAction(
						"SIGNAL",
						aggvo.getParentVO().getAttributeValue("pk_tradetype")
								.toString(), worknoteVO, aggvo, null,
						getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("RECALL", "261X", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	/**
	 * 应付单
	 * 
	 * @param type
	 * @param workFlowBill
	 * @return
	 * @throws BusinessException
	 */
	private String aggPayableBillVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		AggPayableBillVO aggvo = new AggPayableBillVO();
		PayableBillVO hvo = (PayableBillVO) getHyPubBO().queryByPrimaryKey(
				PayableBillVO.class, workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		PayableBillItemVO[] bvos = (PayableBillItemVO[]) getHyPubBO()
				.queryByCondition(
						PayableBillItemVO.class,
						"nvl(dr,0) = 0 and pk_payablebill = '"
								+ workFlowBill.getPk_bill() + "'");
		aggvo.setChildrenVO(bvos);
		SaveOALog(hvo.getBillno(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("SIGNAL", aggvo.getParentVO()
								.getAttributeValue("pk_tradetype").toString(),
								aggvo, hmPfExParams);
				worknoteVO.setChecknote("批准");
				worknoteVO.setApproveresult("Y");
				getIplatFormEntry().processAction("SIGNAL", "F1", worknoteVO,
						aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("RECALL", "F1", null, aggvo,
						null, getEparam());
			}
		}
		return Result.success();
	}

	/**
	 * 收款单
	 * 
	 * @param type
	 * @param workFlowBill
	 * @return
	 * @throws BusinessException
	 */
	private String aggGatheringBillVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		AggGatheringBillVO aggvo = new AggGatheringBillVO();
		GatheringBillVO hvo = (GatheringBillVO) getHyPubBO().queryByPrimaryKey(
				GatheringBillVO.class, workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		GatheringBillItemVO[] bvos = (GatheringBillItemVO[]) getHyPubBO()
				.queryByCondition(
						GatheringBillItemVO.class,
						"nvl(dr,0) = 0 and pk_gatherbill = '"
								+ workFlowBill.getPk_bill() + "'");
		aggvo.setChildrenVO(bvos);
		SaveOALog(hvo.getBillno(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("SIGNAL", aggvo.getParentVO()
								.getAttributeValue("pk_tradetype").toString(),
								aggvo, hmPfExParams);
				worknoteVO.setChecknote("批准");
				worknoteVO.setApproveresult("Y");
				getIplatFormEntry().processAction(
						"SIGNAL",
						aggvo.getParentVO().getAttributeValue("pk_tradetype")
								.toString(), worknoteVO, aggvo, null,
						getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("RECALL", "F2", null, aggvo,
						null, getEparam());
			}
		}
		return Result.success();
	}

	/**
	 * 付款单
	 * 
	 * @param type
	 * @param workFlowBill
	 * @return
	 * @throws BusinessException
	 */
	private String aggPayBillVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		IArapPayBillQueryService cs = (IArapPayBillQueryService) NCLocator
				.getInstance().lookup(IArapPayBillQueryService.class);
		AggPayBillVO[] wq = cs.queryBillsByWhereSQL(" PK_PAYBILL = '"
				+ workFlowBill.getPk_bill() + "'");

		AggPayBillVO aggvo = wq[0];
		PayBillVO hvo = (PayBillVO) aggvo.getParentVO();
		/*
		 * AggPayBillVO aggvo = new AggPayBillVO(); PayBillVO hvo = (PayBillVO)
		 * getHyPubBO().queryByPrimaryKey( PayBillVO.class,
		 * workFlowBill.getPk_bill()); aggvo.setParent(hvo); PayBillItemVO[]
		 * bvos = (PayBillItemVO[]) getHyPubBO().queryByCondition(
		 * PayBillItemVO.class, "nvl(dr,0) = 0 and pk_paybill = '" +
		 * workFlowBill.getPk_bill() + "'"); aggvo.setChildrenVO(bvos);
		 */
		SaveOALog(hvo.getBillno(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("SIGNAL", aggvo.getParentVO()
								.getAttributeValue("pk_tradetype").toString(),
								aggvo, hmPfExParams);
				worknoteVO.setChecknote("批准");
				worknoteVO.setApproveresult("Y");
				getIplatFormEntry().processAction("SIGNAL", "F3", worknoteVO,
						aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("RECALL", "F3", null, aggvo,
						null, getEparam());
			}
		}
		return Result.success();
	}

	/***
	 * 清单发包合同（设计，施工，监理）
	 * 
	 * @param type
	 * @param workFlowBill
	 * @return
	 * @throws BusinessException
	 */
	private String apprContractBillVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		String[] userObj = { ContractBillVO.class.getName(),
				ContrHeadVO.class.getName(), ContrWorksVO.class.getName() };
		ContractBillVO aggvo = (ContractBillVO) getHyPubBO()
				.queryBillVOByPrimaryKey(userObj, workFlowBill.getPk_bill());
		SaveOALog(aggvo.getParentVO().getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "4D42", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "4D42",
						worknoteVO, aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				try {
					Object obj = getIplatFormEntry().processAction(
							"UNSAVEBILL", "4D42", null, aggvo, null,
							getEparam());
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					String mes = e.getMessage();
					System.out.println(e.getMessage());
				}

			}
		}
		return Result.success();
	}

	/**
	 * 采购订单
	 * 
	 * @param type
	 * @param workFlowBill
	 * @return
	 * @throws BusinessException
	 */
	private String appOrderHeaderVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		String[] ids = { workFlowBill.getPk_bill() };
		IOrderQuery service = NCLocator.getInstance().lookup(IOrderQuery.class);
		OrderVO[] aggvos = service.queryOrderVOsByIds(ids, UFBoolean.TRUE);
		OrderHeaderVO hvo = (OrderHeaderVO) aggvos[0].getParentVO();
		SaveOALog(hvo.getVbillcode(), workFlowBill, type);
		if (null != aggvos && aggvos.length == 1) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "21", aggvos[0], hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "21", worknoteVO,
						aggvos[0], null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "21", null,
						aggvos[0], null, getEparam());
			}
		}
		return Result.success();
	}

	/**
	 * 开工单
	 * 
	 * @param type
	 * @param workFlowBill
	 * @return
	 * @throws BusinessException
	 */
	private String appStartReportHeadVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		String[] userObj = { StartReportBillVO.class.getName(),
				StartReportHeadVO.class.getName(), null };
		StartReportBillVO aggvo = (StartReportBillVO) getHyPubBO()
				.queryBillVOByPrimaryKey(userObj, workFlowBill.getPk_bill());
		SaveOALog(aggvo.getParentVO().getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				getIplatFormEntry().processAction("APPROVE", "4D23", null,
						aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "4D23", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	/**
	 * 竣工验收
	 * 
	 * @param type
	 * @param workFlowBill
	 * @return
	 * @throws BusinessException
	 */
	private String appProjectCheckHeadVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		String[] userObj = { ProjectCheckBillVO.class.getName(),
				ProjectCheckHeadVO.class.getName(),
				ProductTransferVO.class.getName() };
		ProjectCheckBillVO aggvo = (ProjectCheckBillVO) getHyPubBO()
				.queryBillVOByPrimaryKey(userObj, workFlowBill.getPk_bill());
		SaveOALog(aggvo.getParentVO().getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "4D36", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "4D36",
						worknoteVO, aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "4D36", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	/**
	 * 追加项目申请单
	 * 
	 * @param type
	 * @param workFlowBill
	 * @return
	 * @throws BusinessException
	 */
	private String appProjectRegisterHeadVO(String type,
			WorkFlowBill workFlowBill) throws BusinessException {
		String[] userObj = { ProjectRegisterBillVO.class.getName(),
				ProjectRegisterHeadVO.class.getName() };
		ProjectRegisterBillVO aggvo = (ProjectRegisterBillVO) getHyPubBO()
				.queryBillVOByPrimaryKey(userObj, workFlowBill.getPk_bill());
		SaveOALog(aggvo.getParentVO().getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "4D11", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "4D11",
						worknoteVO, aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "4D11", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	/**
	 * // 勘察申请单 // 设计申请单 //刨掘申请单 //控制价委托申请单 //开工单
	 * 
	 * @param type
	 * @param workFlowBill
	 * @return
	 * @throws BusinessException
	 */
	private String appProjectProposalHeadVO(String type,
			WorkFlowBill workFlowBill) throws BusinessException {
		ProjectProposalHeadVO hvo = (ProjectProposalHeadVO) getHyPubBO()
				.queryByPrimaryKey(ProjectProposalHeadVO.class,
						workFlowBill.getPk_bill());
		ProjectProposalBillVO aggvo = new ProjectProposalBillVO();
		aggvo.setParent(hvo);
		SaveOALog(hvo.getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "4D15", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "4D15",
						worknoteVO, aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "4D15", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	/**
	 * 多编码合同
	 * 
	 * @param type
	 * @param workFlowBill
	 * @return
	 * @throws BusinessException
	 */
	private String appFeeBalanceHeadVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		String[] userObj = { FeeBalanceBillVO.class.getName(),
				FeeBalanceHeadVO.class.getName(),
				FeeBalanceBodyVO.class.getName() };
		FeeBalanceBillVO aggvo = (FeeBalanceBillVO) getHyPubBO()
				.queryBillVOByPrimaryKey(userObj, workFlowBill.getPk_bill());
		FeeBalanceHeadVO headvo = (FeeBalanceHeadVO) aggvo.getParentVO();
		SaveOALog(headvo.getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "4D83", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "4D83",
						worknoteVO, aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				if (headvo.getBill_status() == -1) {
					throw new BusinessException("单据状态不正确，不能收回！");
				}
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "4D83", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	/**
	 * 补充协议
	 * 
	 * @param type
	 * @param workFlowBill
	 * @return
	 * @throws BusinessException
	 */
	private String appContrAlterHeadVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		String[] userObj = { ContrAlterBillVO.class.getName(),
				ContrAlterHeadVO.class.getName(),
				ContrAlterBodyVO.class.getName() };
		ContrAlterBillVO aggvo = (ContrAlterBillVO) getHyPubBO()
				.queryBillVOByPrimaryKey(userObj, workFlowBill.getPk_bill());
		SaveOALog(aggvo.getParentVO().getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "4D44", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "4D44",
						worknoteVO, aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "4D44", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	/**
	 * 预算调整单
	 * 
	 * @param type
	 * @param workFlowBill
	 * @return
	 * @throws BusinessException
	 */
	private String appBudgetAddHeadVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		String[] userObj = { BudgetAddBillVO.class.getName(),
				BudgetAddHeadVO.class.getName(),
				BudgetAddBodyVO.class.getName() };
		BudgetAddBillVO aggvo = (BudgetAddBillVO) getHyPubBO()
				.queryBillVOByPrimaryKey(userObj, workFlowBill.getPk_bill());
		SaveOALog(aggvo.getParentVO().getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "4D20", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "4D20",
						worknoteVO, aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "4D20", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	/**
	 * 收款合同
	 * 
	 * @param type
	 * @param workFlowBill
	 * @return
	 * @throws BusinessException
	 */
	private String appCtArVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		String[] userObj = { AggCtArVO.class.getName(), CtArVO.class.getName(),
				CtArTermVO.class.getName() };
		AggCtArVO aggvo = (AggCtArVO) getHyPubBO().queryBillVOByPrimaryKey(
				userObj, workFlowBill.getPk_bill());
		SaveOALog(aggvo.getParentVO().getVbillcode(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "FCT2", aggvo, hmPfExParams);
				worknoteVO.setChecknote("批准");
				worknoteVO.setApproveresult("Y");
				getIplatFormEntry().processAction("APPROVE", "FCT2",
						worknoteVO, aggvo, null, getEparam());
				// 审批完生效
				AggCtArVO aggvonew = (AggCtArVO) getHyPubBO()
						.queryBillVOByPrimaryKey(userObj,
								workFlowBill.getPk_bill());
				getIplatFormEntry().processAction("VALIDATE", "FCT2", null,
						aggvonew, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "FCT2", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	/***
	 * 付款合同
	 * 
	 * @param type
	 * @param workFlowBill
	 * @return
	 * @throws BusinessException
	 * @throws DbException
	 */
	private String appCtApVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		String[] userObj = { AggCtApVO.class.getName(), CtApVO.class.getName(),
				CtApBVO.class.getName() };
		AggCtApVO aggvo = (AggCtApVO) getHyPubBO().queryBillVOByPrimaryKey(
				userObj, workFlowBill.getPk_bill());
		SaveOALog(aggvo.getParentVO().getVbillcode(), workFlowBill, type);
		if (null != aggvo) {
			String jylx = aggvo.getParentVO().getVtrantypecode();// 交易类型
			int fstatusflag = aggvo.getParentVO().getFstatusflag();// 单据状态
			if ("adopt".equals(type)) {
				/*
				 * 工程 OA审批完成后，生成项目报销单 条件：交易类型：FCT1-Cxx-GCFKHT（工程-付款合同）
				 */
				if (fstatusflag == 0) {
					throw new BusinessException("单据状态为自由态，不允许审批！");
				} else if (fstatusflag == 3 || fstatusflag == 1) {
					throw new BusinessException("单据已审批，不允许重复审批！");
				}
				// 单价合同不用生成
				if (!"Y".equals(aggvo.getParentVO().getVdef17())
						&& "FCT1-Cxx-GCFKHT".equals(jylx)) {
					// 查询是否已生成，如生成对应报销单则返回报错
					BXHeaderVO[] hvo = (BXHeaderVO[]) getHyPubBO()
							.queryByCondition(
									BXHeaderVO.class,
									"nvl(dr,0) = 0 and pk_item = '"
											+ aggvo.getPrimaryKey() + "'");
					if (hvo != null && hvo.length > 0) {
						throw new BusinessException("已生成工程-付款合同，单据号["
								+ hvo[0].getDjbh() + "]");
					}
					// 利用单据转换
					IPfExchangeService pf = NCLocator.getInstance().lookup(
							IPfExchangeService.class);
					// FCT1-Cxx-02 原交易类型，264X-Cxx-gcfkht 转换后交易类型
					JKBXVO bxvo = (JKBXVO) pf.runChangeData("FCT1-Cxx-GCFKHT",
							"264X-Cxx-gcfkht", aggvo, null);
					JKBXVO[] vos = new JKBXVO[] { bxvo };
					IBXBillPublic bxutf = NCLocator.getInstance().lookup(
							IBXBillPublic.class);
					JKBXVO[] BXVO = bxutf.save(vos);
					System.out.println("单据编号："
							+ BXVO[0].getParentVO().getDjbh());
					try {
						updatefile(workFlowBill.getPk_bill(),
								BXVO[0].getParentVO());
						IplatFormEntry iIplatFormEntry = NCLocator
								.getInstance().lookup(IplatFormEntry.class);
						iIplatFormEntry.processAction("START", "264X", null,
								BXVO[0], null, null);
						String sql = "update er_bxzb set pk_item = '"
								+ aggvo.getPrimaryKey() + "' where"
								+ " pk_jkbx = '"
								+ BXVO[0].getParentVO().getPrimaryKey() + "'";
						GetDao getDao = NCLocator.getInstance().lookup(
								GetDao.class);
						getDao.executeUpdate(sql);
					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					workFlowBill.setBill_status("2");
					String adopter = (String) getHyPubBO().findColValue(
							"sys_config", "config_value",
							" config_key='oaadopt_cuser'");
					InvocationInfoProxy.getInstance().setUserId(adopter);
					HashMap hmPfExParams = new HashMap();
					WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
							.getInstance().lookup(IWorkflowMachine.class))
							.checkWorkFlow("APPROVE", "FCT1", aggvo,
									hmPfExParams);
					if (worknoteVO != null) {
						worknoteVO.setChecknote("批准");
						worknoteVO.setApproveresult("Y");
					}
					getIplatFormEntry().processAction("APPROVE", "FCT1",
							worknoteVO, aggvo, null, getEparam());
					// 审批完生效
					AggCtApVO aggvonew = (AggCtApVO) getHyPubBO()
							.queryBillVOByPrimaryKey(userObj,
									workFlowBill.getPk_bill());
					getIplatFormEntry().processAction("VALIDATE", "FCT1", null,
							aggvonew, null, getEparam());
					getHyPubBO().update(workFlowBill);
				}
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "FCT1", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	/**
	 * 物资需求申请调整单
	 * 
	 * @param type
	 * @param workFlowBill
	 * @return
	 * @throws BusinessException
	 */
	private String apprMaterialPlanAddBillVO(String type,
			WorkFlowBill workFlowBill) throws BusinessException {
		String[] userObj = { MaterialPlanAddBillVO.class.getName(),
				MaterialPlanAddHeadVO.class.getName(),
				MaterialPlanAddBodyVO.class.getName() };
		MaterialPlanAddBillVO aggvo = (MaterialPlanAddBillVO) getHyPubBO()
				.queryBillVOByPrimaryKey(userObj, workFlowBill.getPk_bill());
		SaveOALog(aggvo.getParentVO().getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "4D16", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "4D16",
						worknoteVO, aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "4D16", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	/**
	 * 采购合同
	 * 
	 * @param type
	 * @param workFlowBill
	 * @return
	 * @throws BusinessException
	 */
	private String apprCtPuVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		// String[] userObj = { workFlowBill.getPk_bill() };
		// AggCtPuVO[] aggvo = NCLocator.getInstance()
		// .lookup(IPurdailyMaintain.class).queryCtPuVoByIds(userObj);

		AggCtPuVO aggvo = new AggCtPuVO();
		CtPuVO hvo = (CtPuVO) getHyPubBO().queryByPrimaryKey(CtPuVO.class,
				workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		CtPuBVO[] bvos = (CtPuBVO[]) getHyPubBO().queryByCondition(
				CtPuBVO.class,
				"nvl(dr,0) = 0 and pk_ct_pu = '" + workFlowBill.getPk_bill()
						+ "'");
		aggvo.setCtPuBVO(bvos);
		SaveOALog(hvo.getVbillcode(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", hvo.getVtrantypecode(),
								aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE",
						hvo.getVtrantypecode(), worknoteVO, aggvo, null,
						getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				InvocationInfoProxy.getInstance().setUserId(
						aggvo.getParentVO().getBillmaker());// 设置默认操作员主键
				getIplatFormEntry().processAction("UNSAVEBILL", "Z2", null,
						aggvo, null, getEparam());
			}
		} else {
			return Result.error("审批处理出现异常");
		}
		return Result.success();
	}

	/**
	 * 物资及服务需求单
	 * 
	 * @param type
	 * @param workFlowBill
	 * @return
	 * @throws BusinessException
	 */
	private String apprMaterialPlanHeadVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {

		String[] userObj = { MaterialPlanBillVO.class.getName(),
				MaterialPlanHeadVO.class.getName(),
				MaterialPlanBodyVO.class.getName() };
		MaterialPlanBillVO materialPlanBillVO = (MaterialPlanBillVO) getHyPubBO()
				.queryBillVOByPrimaryKey(userObj, workFlowBill.getPk_bill());
		SaveOALog(materialPlanBillVO.getParentVO().getBill_code(),
				workFlowBill, type);
		if (null != materialPlanBillVO) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "4D14", materialPlanBillVO,
								hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "4D14",
						worknoteVO, materialPlanBillVO, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setBill_status("0");
				workFlowBill.setDef1("return");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "4D14", null,
						materialPlanBillVO, null, getEparam());
			}
		}
		return Result.success();
	}

	// 预付款单 (pm_contr_prepay)
	private String appContractPrepayBill(String type, WorkFlowBill workFlowBill)
			throws BusinessException {

		ContractPrepayBillVO aggvo = new ContractPrepayBillVO();
		ContractPrepayHeadVO hvo = (ContractPrepayHeadVO) getHyPubBO()
				.queryByPrimaryKey(ContractPrepayHeadVO.class,
						workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		SaveOALog(hvo.getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "4D46", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "4D46",
						worknoteVO, aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "4D46", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	// 材料结算单(pm_materialacc)
	private String appAggMaterialaccVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {

		AggMaterialaccVO aggvo = new AggMaterialaccVO();
		MaterialaccHeadVO hvo = (MaterialaccHeadVO) getHyPubBO()
				.queryByPrimaryKey(MaterialaccHeadVO.class,
						workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		MaterialaccBodyVO[] bvos = (MaterialaccBodyVO[]) getHyPubBO()
				.queryByCondition(
						MaterialaccBodyVO.class,
						"nvl(dr,0) = 0 and pk_materialacc = '"
								+ workFlowBill.getPk_bill() + "'");
		aggvo.setTableVO("pm_materialacc_b", bvos);
		SecondMaterialBodyVO[] bvos2 = (SecondMaterialBodyVO[]) getHyPubBO()
				.queryByCondition(
						SecondMaterialBodyVO.class,
						"nvl(dr,0) = 0 and pk_materialacc = '"
								+ workFlowBill.getPk_bill() + "'");
		if (bvos2 != null && bvos2.length > 0) {
			aggvo.setTableVO("pm_secondmaterial", bvos2);
		}
		SaveOALog(hvo.getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "4D39", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "4D39",
						worknoteVO, aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "4D39", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	// 费用预提单(pm_materialacc)
	private String appAggAccruedBill(String type, WorkFlowBill workFlowBill)
			throws BusinessException {

		AggAccruedBillVO aggvo = new AggAccruedBillVO();
		AccruedVO hvo = (AccruedVO) getHyPubBO().queryByPrimaryKey(
				AccruedVO.class, workFlowBill.getPk_bill());
		aggvo.setParentVO(hvo);
		AccruedDetailVO[] bvos = (AccruedDetailVO[]) getHyPubBO()
				.queryByCondition(
						AccruedDetailVO.class,
						"nvl(dr,0) = 0 and pk_accrued_bill = '"
								+ workFlowBill.getPk_bill() + "'");
		aggvo.setTableVO("er_accrued_detail", bvos);
		AccruedVerifyVO[] bvos2 = (AccruedVerifyVO[]) getHyPubBO()
				.queryByCondition(
						AccruedVerifyVO.class,
						"nvl(dr,0) = 0 and pk_accrued_bill = '"
								+ workFlowBill.getPk_bill() + "'");
		if (bvos2 != null && bvos2.length > 0) {
			aggvo.setTableVO("er_accrued_verify", bvos2);
		}
		SaveOALog(hvo.getBillno(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("SIGNAL", "262X", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("SIGNAL", "262X", worknoteVO,
						aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("RECALL", "262X", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	// 现金支取单 (cmp_cashdraw)
	private String appAggCashDrawVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {

		AggCashDrawVO aggvo = new AggCashDrawVO();
		CashDrawVO hvo = (CashDrawVO) getHyPubBO().queryByPrimaryKey(
				CashDrawVO.class, workFlowBill.getPk_bill());
		aggvo.setParentVO(hvo);
		SaveOALog(hvo.getBillno(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "36S2", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "36S2",
						worknoteVO, aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "36S2", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	// 调拨订单 (to_bill)
	private String appBillVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {

		BillVO aggvo = new BillVO();
		BillHeaderVO hvo = (BillHeaderVO) getHyPubBO().queryByPrimaryKey(
				BillHeaderVO.class, workFlowBill.getPk_bill());
		aggvo.setParentVO(hvo);
		BillItemVO[] bvos = (BillItemVO[]) getHyPubBO().queryByCondition(
				BillItemVO.class,
				"nvl(dr,0) = 0 and cbillid = '" + workFlowBill.getPk_bill()
						+ "'");
		aggvo.setChildrenVO(bvos);
		SaveOALog(hvo.getVbillcode(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "5X", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "5X", worknoteVO,
						aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "5X", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	// 付款结算 (cmp_paybill)
	private String appBillAggVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {

		BillAggVO aggvo = new BillAggVO();
		nc.vo.cmp.bill.BillVO hvo = (nc.vo.cmp.bill.BillVO) getHyPubBO()
				.queryByPrimaryKey(nc.vo.cmp.bill.BillVO.class,
						workFlowBill.getPk_bill());
		aggvo.setParentVO(hvo);
		BillDetailVO[] bvos = (BillDetailVO[]) getHyPubBO().queryByCondition(
				BillDetailVO.class,
				"nvl(dr,0) = 0 and pk_paybill = '" + workFlowBill.getPk_bill()
						+ "'");
		aggvo.setChildrenVO(bvos);
		SaveOALog(hvo.getBill_no(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "F5", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "F5", worknoteVO,
						aggvo, null, getEparam());
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				if (workFlowBill.getDef1() == null
						&& !"return".equals(workFlowBill.getDef1())) {
					workFlowBill.setDef1("return");
					workFlowBill.setBill_status("0");
					getHyPubBO().update(workFlowBill);
					String sql = "UPDATE CMP_PAYBILL SET DEF3 = '~' WHERE "
							+ "PK_PAYBILL = '" + workFlowBill.getPk_bill()
							+ "'";
					new BaseDAO().executeUpdate(sql);
					/*
					 * hvo.setDef3(UFBoolean.FALSE + ""); hvo.setDr(0);
					 * hvo.setStatus(VOStatus.UPDATED);
					 * getHyPubBO().update(hvo);
					 */
				} else {
					throw new BusinessException("单据已退回，请勿重新退回["
							+ workFlowBill.getTs() + "]");
				}
			}
		}
		return Result.success();
	}

	/**
	 * 加班申请单
	 * 
	 * @param type
	 * @param workFlowBill
	 * @return
	 * @throws BusinessException
	 */
	private String aggOvertimeVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		String[] pks = { workFlowBill.getPk_bill() };
		AggOvertimeVO[] aggVOS = ((IOvertimeApplyQueryMaintain) NCLocator
				.getInstance().lookup(IOvertimeApplyQueryMaintain.class))
				.queryByPks(pks);
		AggOvertimeVO aggvo = aggVOS[0];
		OvertimehVO hvo = (OvertimehVO) aggvo.getParentVO();
		SaveOALog(hvo.getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "6405", aggvo, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}

				getIplatFormEntry().processAction("APPROVE", "6405",
						worknoteVO, aggvo, null, getEparam());

				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("RECALL", "6405", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	/**
	 * 休假申请单
	 * 
	 * @param type
	 * @param workFlowBill
	 * @return
	 * @throws BusinessException
	 */
	private String aggLeaveVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		String[] pks = { workFlowBill.getPk_bill() };
		AggLeaveVO[] aggVOS = ((ILeaveApplyQueryMaintain) NCLocator
				.getInstance().lookup(ILeaveApplyQueryMaintain.class))
				.queryByPks(pks);
		AggLeaveVO aggvo = aggVOS[0];
		String[] userObj = { AggStapply.class.getName(),
				AggStapply.class.getName(), AggStapply.class.getName() };
		LeavehVO hvo = (LeavehVO) aggvo.getParentVO();
		SaveOALog(hvo.getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				if ("adopt".equals(type)) {
					workFlowBill.setBill_status("2");
					String adopter = (String) getHyPubBO().findColValue(
							"sys_config", "config_value",
							" config_key='oaadopt_cuser'");
					InvocationInfoProxy.getInstance().setUserId(adopter);
					HashMap hmPfExParams = new HashMap();
					WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
							.getInstance().lookup(IWorkflowMachine.class))
							.checkWorkFlow("APPROVE", "6404", aggvo,
									hmPfExParams);
					if (worknoteVO != null) {
						worknoteVO.setChecknote("批准");
						worknoteVO.setApproveresult("Y");
					}
					getIplatFormEntry().processAction("APPROVE", "6404", null,
							aggvo, null, getEparam());
					getHyPubBO().update(workFlowBill);
				}
				// 审批完生效
				/*
				 * AggStapply aggvonew = (AggStapply)
				 * getHyPubBO().queryBillVOByPrimaryKey( userObj,
				 * workFlowBill.getPk_bill());
				 * getIplatFormEntry().processAction("VALIDATE", "6404", null,
				 * aggvonew, null, getEparam());
				 * getHyPubBO().update(workFlowBill);
				 */
				/*
				 * getIplatFormEntry().processAction("APPROVE", "6404", null,
				 * aggvo, null, getEparam());
				 */
				getHyPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("RECALL", "6404", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	/**
	 * 调配/离职申请单
	 * 
	 * @param type
	 * @param workFlowBill
	 * @return
	 * @throws BusinessException
	 */
	private String aggStapply(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		AggStapply aggvo = ((ITransmngQueryService) NCLocator.getInstance()
				.lookup(ITransmngQueryService.class)).queryByPk(workFlowBill
				.getPk_bill());
		String[] userObj = { AggStapply.class.getName(),
				AggStapply.class.getName(), AggStapply.class.getName() };
		StapplyVO hvo = (StapplyVO) aggvo.getParentVO();
		SaveOALog(hvo.getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) getHyPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", aggvo.getParentVO()
								.getAttributeValue("pk_billtype").toString(),
								aggvo, hmPfExParams);
				worknoteVO.setChecknote("批准");
				worknoteVO.setApproveresult("Y");
				getIplatFormEntry().processAction(
						"APPROVE",
						aggvo.getParentVO().getAttributeValue("pk_billtype")
								.toString(), worknoteVO, aggvo, null,
						getEparam());
				// 审批完生效
				AggStapply aggvonew = ((ITransmngQueryService) NCLocator
						.getInstance().lookup(ITransmngQueryService.class))
						.queryByPk(workFlowBill.getPk_bill());

				LoginContext tempContext = new LoginContext();
				StapplyVO parentVO = (StapplyVO) aggvonew.getParentVO();
				tempContext.setPk_group(parentVO.getPk_group());
				tempContext.setPk_org(parentVO.getPk_org());
				String sxrq = parentVO.getEffectdate().getYear() + "-"
						+ parentVO.getEffectdate().getStrMonth() + "-"
						+ parentVO.getEffectdate().getStrDay();
				UFLiteralDate effectDate = new UFLiteralDate(sxrq);
				HashMap<String, Object> result = getItransmngServiceImpl()
						.manualExecBills(new AggStapply[] { aggvonew },
								tempContext, effectDate);
				if (result != null && result.get("RESULT_MSG") != null) {
					return result.get("RESULT_MSG").toString();
				}
				// getItransmngServiceImpl().execBills(new
				// AggStapply[]{aggvonew});
				int i = 0;
				/*
				 * getIplatFormEntry().processAction("VALIDATE", "6115", null,
				 * aggvonew, null, getEparam());
				 * getHyPubBO().update(workFlowBill);
				 */

				/*
				 * getIplatFormEntry().processAction("APPROVE", "6115", null,
				 * aggvo, null, getEparam()); getHyPubBO().update(workFlowBill);
				 */
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				getHyPubBO().update(workFlowBill);
				getIplatFormEntry().processAction(
						"RECALL",
						aggvo.getParentVO().getAttributeValue("pk_billtype")
								.toString(), null, aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	private HashMap<String, Object> getEparam() {
		HashMap<String, Object> eParam = new HashMap<String, Object>();
		eParam.put("notechecked", "notechecked");
		return eParam;
	}

	private IplatFormEntry getIplatFormEntry() {
		return NCLocator.getInstance().lookup(IplatFormEntry.class);
	}

	private ITransmngManageService getItransmngServiceImpl() {
		return NCLocator.getInstance().lookup(ITransmngManageService.class);
	}

	private static String getDef2(String pk_org) throws BusinessException {
		String def2 = "";
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		if (null != orgVO) {
			def2 = orgVO.getDef2();
		}
		return def2;
	}

	// 记录日志VO
	private void SaveOALog(String bill_code, WorkFlowBill workFlowBill,
			String type) throws BusinessException {
		OALogVO vo = new OALogVO();
		vo.setBill_code(bill_code);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		vo.setCreationtime(df.format(new Date()));// 创建时间
		vo.setRequestid(workFlowBill.getRequestid() + "");
		vo.setPk_bill(workFlowBill.getPk_bill());// 单据主键
		vo.setTransi_type(workFlowBill.getBill_code()); // 单据类型
		// 当前登录人ID
		// String userID = InvocationInfoProxy.getInstance().getUserId();
		if (null != workFlowBill.getCreator()) {
			UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
					UserVO.class, workFlowBill.getCreator());
			vo.setUserid(workFlowBill.getCreator());

			if (userVO != null) {
				vo.setUsername(userVO.getUser_name());
			}
		}

		vo.setRequestid(workFlowBill.getRequestid() + "");
		vo.setDef1(type);
		vo.setDef2(workFlowBill.getPk_org());
		vo.setDef3(workFlowBill.getPk_group());
		vo.setDr(0);
		try {
			InetAddress addr = InetAddress.getLocalHost();
			String Ip = addr.getHostAddress(); // 客户端IP
			String hostname = addr.getHostName(); // 客户端计算机名
			vo.setUserip(Ip);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getHyPubBO().insert(vo);
	}

	private String dsName;

	private String getDsName() {

		return this.dsName;
	}

	// 传附件
	private String updatefile(String pk_fct_ap, JKBXHeaderVO bxhvo)
			throws BusinessException, DbException {
		HYPubBO hyPubBO = new HYPubBO();
		// InvocationInfoProxy.getInstance().setGroupId("0001A1100000000001QS");
		FileExAttrDAO dao = new FileExAttrDAO(getDsName());
		List<String> idss = new ArrayList<String>();
		FileNodeVO1[] fnodes = (FileNodeVO1[]) hyPubBO.queryByCondition(
				FileNodeVO1.class, "nvl(dr,0) = 0 and filepath like '%"
						+ pk_fct_ap + "%' ");
		for (FileNodeVO1 fnode : fnodes) {
			RlPmeFile[] pmes = (RlPmeFile[]) hyPubBO.queryByCondition(
					RlPmeFile.class,
					"nvl(dr,0) = 0 and file_id = '" + fnode.getPrimaryKey()
							+ "' ");
			// fnode.setPrimaryKey(null);
			fnode.setFilepath(fnode.getFilepath().replaceAll(pk_fct_ap,
					bxhvo.getPrimaryKey()));
			fnode.setAttributeValue("pk", createPk());
			fnode.setStatus(VOStatus.NEW);
			String id = hyPubBO.insert(fnode);
			if ("n".equals(fnode.getIsfolder())) {
				idss.add(id);
			}
			// service.insert(fnode);
			// getDao.InsertVOWithPK(fnode);
			if (pmes.length > 0) {
				for (RlPmeFile pme : pmes) {
					// pme.setPk_rl_pme_file(null);
					pme.setFile_id(fnode.getPrimaryKey());
					pme.setStatus(VOStatus.NEW);
					pme.setPk_rl_pme_file(null);
					hyPubBO.insert(pme);
					// getDao.InsertVOWithPK(pme);
				}
			}
			// FileExAttrVO
		}
		DocumentCenterVO[] dcbvos = (DocumentCenterVO[]) hyPubBO
				.queryByCondition(DocumentCenterVO.class,
						"nvl(dr,0) = 0 and pk_bill = '" + pk_fct_ap + "' ");
		List<String> ids = new ArrayList<String>();
		List<String> docs = new ArrayList<String>();
		if (dcbvos.length > 0) {
			for (DocumentCenterVO dcbvo : dcbvos) {
				// dcbvo.setPk_doccenter(null);
				dcbvo.setBill_code(bxhvo.getDjbh());
				dcbvo.setPk_billtype("0000Z30000000000264X");
				dcbvo.setPk_transitype("1001A1100000002FPZ9F");
				dcbvo.setPk_bill(bxhvo.getPrimaryKey());
				dcbvo.setStatus(VOStatus.NEW);
				String id = dcbvo.getPk_doccenter();
				dcbvo.setPk_doccenter(null);
				String s = hyPubBO.insert(dcbvo);
				// getDao.InsertVOWithPK(dcbvo);
				ids.add(id);
				docs.add(s);
			}
		}
		FileExAttrVO[] fattrs = dao.queryFileExAttr(ids);
		for (int i = 0; i < fattrs.length; i++) {
			// FileExAttrVO fattr = (FileExAttrVO) temp.clone();
			fattrs[i].setPk_bill(bxhvo.getPrimaryKey());
			// fattr.setPk_fileexattr(null);
			fattrs[i].setPk_file(idss.get(i));
			fattrs[i].setPk_exattr(docs.get(i));
			// fattr.setStatus(VOStatus.NEW);
			fattrs[i].setPk_fileexattr(null);
			fattrs[i].setPk_billtypecode("4Z01");
			fattrs[i].setPk_fileexattr(createPk());
			// getDao.InsertVOWithPK(fattr);
			dao.insertFileExAttr(fattrs[i]);
		}
		return "";
	}

	private String createPk() {
		DBTool dbTool = new DBTool();
		String[] ids = dbTool.getOIDs(1);
		return ids[0];
	}
}
