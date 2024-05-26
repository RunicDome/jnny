package nc.impl.pmr;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.adaptor.IHttpServletAdaptor;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.comn.NetStreamContext;
import nc.bs.framework.server.ISecurityTokenCallback;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pm.util.db.BillQueryByCond;
import nc.impl.pub.filesystem.FileExAttrDAO;
import nc.impl.pubapp.pattern.database.DBTool;
import nc.itf.arap.pay.IArapPayBillQueryService;
import nc.itf.arap.pub.IBXBillPublic;
import nc.itf.hr.wa.IPayrollQueryService;
import nc.itf.pm.IPmRLContractalterMaintain;
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
import nc.ws.intf.OALogVO;
import nc.ws.intf.Result;
import nc.ws.intf.WorkFlowBill;

@SuppressWarnings({ "restriction", "rawtypes", "unchecked", "unused" })
public class BillworkImpl implements IHttpServletAdaptor {

	@Override
	public void doAction(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String ds = "RLJT";
		InvocationInfoProxy.getInstance().setUserDataSource(ds);
		InvocationInfoProxy.getInstance().setGroupId("0001A1100000000001QS");// 设置默认的集团主键
		InvocationInfoProxy.getInstance().setUserCode("liuli");// 设置接口默认操作员编码
		InvocationInfoProxy.getInstance().setUserId("1001A110000000000HV8");// 设置接口默认操作员主键
		ISecurityTokenCallback tc = NCLocator.getInstance().lookup(
				ISecurityTokenCallback.class);
		byte[] token = tc.token("NCSystem".getBytes(), "pfxx".getBytes());
		NetStreamContext.setToken(token);
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charset=utf-8");
		req.setCharacterEncoding("utf-8");
		// 调用审批或者驳回

		String type = req.getParameter("type");// 类型
		int requestid = Integer.parseInt(req.getParameter("requestid"));// 流程ID
		String strWhere = " nvl(dr,0) = 0 and requestid = " + requestid
				+ " and def3 <> 'ZT'";
		if (requestid < 0) {
			strWhere = " nvl(dr,0) = 0 and requestid = " + requestid * -1
					+ " and def3 = 'ZT'";
		}

		try {
			WorkFlowBill[] workFlowBill = (WorkFlowBill[]) new HYPubBO()
					.queryByCondition(WorkFlowBill.class, strWhere);
			if (null == workFlowBill || workFlowBill.length != 1) {
				throw new BusinessException("未查询到流程日志");
			}
			InvocationInfoProxy.getInstance().setUserId(
					workFlowBill[0].getCreator());// 设置默认操作员主键
			workFlowBill[0].setStatus(VOStatus.UPDATED);
			String errmsg = "";
			switch (workFlowBill[0].getBill_code()) {
			/* 物资及服务需求单 */
			case "4D14":
				errmsg = apprMaterialPlanHeadVO(type, workFlowBill[0]);
				/* 采购合同 */
			case "Z2":
				errmsg = apprCtPuVO(type, workFlowBill[0]);
				/* 物资及服务需求单调整单 */
			case "4D16":
				errmsg = apprMaterialPlanAddBillVO(type, workFlowBill[0]);
				/* 清单发包合同（设计，施工，监理） */
			case "4D42":
				errmsg = apprContractBillVO(type, workFlowBill[0]);
				/* 付款合同 */
			case "FCT1":
				errmsg = appCtApVO(type, workFlowBill[0]);

				/* 收款合同 */
			case "FCT2":
				errmsg = appCtArVO(type, workFlowBill[0]);

				// /*入职审批 */
				// case "6101":
				// errmsg = appEntryapplyVO (type, workFlowBill[0]);

				/* 预算调整单XBX修改 */
			case "4D20":
				errmsg = appBudgetAddHeadVO(type, workFlowBill[0]);

				/* 补充协议 */
			case "4D44":
				errmsg = appContrAlterHeadVO(type, workFlowBill[0]);

				/* 多编码合同 */
			case "4D83":
				errmsg = appFeeBalanceHeadVO(type, workFlowBill[0]);

				/* // 勘察申请单 // 设计申请单 //刨掘申请单 //控制价委托申请单 */
			case "4D15":
				errmsg = appProjectProposalHeadVO(type, workFlowBill[0]);

				// 追加项目申请单
			case "4D11":
				errmsg = appProjectRegisterHeadVO(type, workFlowBill[0]);

				// 竣工验收
			case "4D36":
				errmsg = appProjectCheckHeadVO(type, workFlowBill[0]);

				// 开工单
			case "4D23":
				errmsg = appStartReportHeadVO(type, workFlowBill[0]);

				// 采购订单
			case "21":
				errmsg = appOrderHeaderVO(type, workFlowBill[0]);

				// 付款单
			case "F3":
				errmsg = aggPayBillVO(type, workFlowBill[0]);

				// 应付单
			case "F1":
				errmsg = aggPayableBillVO(type, workFlowBill[0]);

				// 应收单
			case "F0":
				errmsg = aggReceivableBillVO(type, workFlowBill[0]);

				// 薪资发放申请单
			case "6302":
				errmsg = aggPayrollVO(type, workFlowBill[0]);

				// 加班申请单
			case "6405":
				errmsg = aggOvertimeVO(type, workFlowBill[0]);

				// 休假申请单
			case "6404":
				errmsg = aggLeaveVO(type, workFlowBill[0]);

				// 离职申请单
			case "6115":
				errmsg = aggStapply(type, workFlowBill[0]);
				// 调配申请单
			case "6113":
				errmsg = aggStapply(type, workFlowBill[0]);
				// 加班费申请单、差旅费申请单
			case "261X":
				errmsg = matterAppVO(type, workFlowBill[0]);
				// 报销单
			case "264X":
				errmsg = bXVO(type, workFlowBill[0]);
				// 借款单
			case "263X":
				errmsg = JKVO(type, workFlowBill[0]);
				// 收款单
			case "F2":
				errmsg = aggGatheringBillVO(type, workFlowBill[0]);
				// 多编码合同（新）
			case "4Z01":
				errmsg = appFeeBalanceVO(type, workFlowBill[0]);
				// 多编码合同补充协议
			case "4Z05":
				errmsg = appFeeBalanceAlterVO(type, workFlowBill[0]);
				// 物资需求申请单
			case "422X":
				errmsg = appStoreReqAppVO(type, workFlowBill[0]);
				// 请购单
			case "20":
				errmsg = appPraybillVO(type, workFlowBill[0]);
				// 采购发票
			case "25":
				errmsg = appInvoiceVO(type, workFlowBill[0]);
				// 销售合同
			case "Z3":
				errmsg = appAggCtSaleVO(type, workFlowBill[0]);
				// 销售订单
			case "30":
				errmsg = appSaleOrderVO(type, workFlowBill[0]);
				// 转库
			case "4K":
				errmsg = appWhsTransBillVO(type, workFlowBill[0]);
				// 项目预算
			case "4D18":
				errmsg = appBudgetBillVO(type, workFlowBill[0]);
				// 项目销售合同
			case "4D60":
				errmsg = appContractBillVO(type, workFlowBill[0]);
				// 补充协议(项目销售合同)
			case "4D61":
				errmsg = appMContr_AlterBillVO(type, workFlowBill[0]);
				// 进度款单
			case "4D48":
				errmsg = appContractScheduleBillVO(type, workFlowBill[0]);
				// 结算单
			case "4D50":
				errmsg = appContractBalanceBillVO(type, workFlowBill[0]);
				// 预算调整单
			case "36D1":
				errmsg = appAggApplyVO(type, workFlowBill[0]);
				// 出库申请单
			case "4455":
				errmsg = appSapplyBillVO(type, workFlowBill[0]);
				// 预付款单
			case "4D46":
				errmsg = appContractPrepayBill(type, workFlowBill[0]);
				// 材料结算
			case "4D39":
				errmsg = appAggMaterialaccVO(type, workFlowBill[0]);
				// 费用预提单
			case "262X":
				errmsg = appAggAccruedBill(type, workFlowBill[0]);
				// 现金支取
			case "36S2":
				errmsg = appAggCashDrawVO(type, workFlowBill[0]);
				// 调拨订单
			case "5X":
				errmsg = appBillVO(type, workFlowBill[0]);
				// 付款结算
			case "F5":
				errmsg = appBillAggVO(type, workFlowBill[0]);
			default:
				errmsg = Result.error("审批处理出现异常");
			}
			resp.setStatus(200);
			PrintWriter writer = resp.getWriter();
			writer.write(errmsg);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 应收单
	private String aggReceivableBillVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		AggReceivableBillVO aggvo = new AggReceivableBillVO();
		ReceivableBillVO hvo = (ReceivableBillVO) new HYPubBO()
				.queryByPrimaryKey(ReceivableBillVO.class,
						workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		ReceivableBillItemVO[] bvos = (ReceivableBillItemVO[]) new HYPubBO()
				.queryByCondition(
						ReceivableBillItemVO.class,
						"nvl(dr,0) = 0 and pk_recbill = '"
								+ workFlowBill.getPk_bill() + "'");
		aggvo.setChildrenVO(bvos);
		SaveOALog(hvo.getBillno(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		StoreReqAppHeaderVO hvo = (StoreReqAppHeaderVO) new HYPubBO()
				.queryByPrimaryKey(StoreReqAppHeaderVO.class,
						workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		StoreReqAppItemVO[] bvos = (StoreReqAppItemVO[]) new HYPubBO()
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
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		PraybillHeaderVO hvo = (PraybillHeaderVO) new HYPubBO()
				.queryByPrimaryKey(PraybillHeaderVO.class,
						workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		PraybillItemVO[] bvos = (PraybillItemVO[]) new HYPubBO()
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
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		InvoiceHeaderVO hvo = (InvoiceHeaderVO) new HYPubBO()
				.queryByPrimaryKey(InvoiceHeaderVO.class,
						workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		InvoiceItemVO[] bvos = (InvoiceItemVO[]) new HYPubBO()
				.queryByCondition(
						InvoiceItemVO.class,
						"nvl(dr,0) = 0 and pk_invoice = '"
								+ workFlowBill.getPk_bill() + "'");
		aggvo.setTableVO("po_invoice_b", bvos);
		SaveOALog(hvo.getVbillcode(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				/*
				 * getIplatFormEntry().processAction("APPROVE", "25", null,
				 * aggvo, null, getEparam());
				 */
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		CtSaleVO hvo = (CtSaleVO) new HYPubBO().queryByPrimaryKey(
				CtSaleVO.class, workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		CtSaleBVO[] bvos = (CtSaleBVO[]) new HYPubBO().queryByCondition(
				CtSaleBVO.class, "nvl(dr,0) = 0 and pk_ct_sale = '"
						+ workFlowBill.getPk_bill() + "'");
		aggvo.setCtSaleBVO(bvos);
		SaveOALog(hvo.getVbillcode(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				/*
				 * getIplatFormEntry().processAction("APPROVE", "Z3", null,
				 * aggvo, null, getEparam());
				 */
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		SaleOrderHVO hvo = (SaleOrderHVO) new HYPubBO().queryByPrimaryKey(
				SaleOrderHVO.class, workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		SaleOrderBVO[] bvos = (SaleOrderBVO[]) new HYPubBO().queryByCondition(
				SaleOrderBVO.class, "nvl(dr,0) = 0 and csaleorderid = '"
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
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		SapplyBillHeadVO hvo = (SapplyBillHeadVO) new HYPubBO()
				.queryByPrimaryKey(SapplyBillHeadVO.class,
						workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		SapplyBillBodyVO[] bvos = (SapplyBillBodyVO[]) new HYPubBO()
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
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		WhsTransBillHeaderVO hvo = (WhsTransBillHeaderVO) new HYPubBO()
				.queryByPrimaryKey(WhsTransBillHeaderVO.class,
						workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		WhsTransBillBodyVO[] bvos = (WhsTransBillBodyVO[]) new HYPubBO()
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
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		 * (BudgetHeadVO) new HYPubBO().queryByPrimaryKey( BudgetHeadVO.class,
		 * workFlowBill.getPk_bill()); aggvo.setParent(hvo);
		 * BudgetFactorBodyVO[] bvos = (BudgetFactorBodyVO[]) new HYPubBO()
		 * .queryByCondition( BudgetFactorBodyVO.class,
		 * "nvl(dr,0) = 0 and pk_budget = '" + workFlowBill.getPk_bill() + "'");
		 * aggvo.setTableVO("pm_budgetfactor", bvos); BudgetCBSBodyVO[] cbsbvos
		 * = (BudgetCBSBodyVO[]) new HYPubBO() .queryByCondition(
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
				String adopter = (String) new HYPubBO().findColValue(
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
					new HYPubBO().update(workFlowBill);
				} catch (BusinessException e) {
					// TODO: handle exception
					e.printStackTrace();
					System.out.println(e.getMessage());
				}

			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		nc.vo.pcm.marketcontract.ContractHeadVO hvo = (nc.vo.pcm.marketcontract.ContractHeadVO) new HYPubBO()
				.queryByPrimaryKey(
						nc.vo.pcm.marketcontract.ContractHeadVO.class,
						workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		nc.vo.pcm.marketcontract.ContrWorksVO[] bvos = (nc.vo.pcm.marketcontract.ContrWorksVO[]) new HYPubBO()
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
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		MContr_AlterHeadVO hvo = (MContr_AlterHeadVO) new HYPubBO()
				.queryByPrimaryKey(MContr_AlterHeadVO.class,
						workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		MContr_AlterBodyVO[] bvos = (MContr_AlterBodyVO[]) new HYPubBO()
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
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		ContractScheduleHeadVO hvo = (ContractScheduleHeadVO) new HYPubBO()
				.queryByPrimaryKey(ContractScheduleHeadVO.class,
						workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		ContractScheduleBodyVO[] bvos = (ContractScheduleBodyVO[]) new HYPubBO()
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
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		ContractBalanceHeadVO hvo = (ContractBalanceHeadVO) new HYPubBO()
				.queryByPrimaryKey(ContractBalanceHeadVO.class,
						workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		ContractBalanceBodyVO[] bvos = (ContractBalanceBodyVO[]) new HYPubBO()
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
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		ApplyVO hvo = (ApplyVO) new HYPubBO().queryByPrimaryKey(ApplyVO.class,
				workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		ApplyBVO[] bvos = (ApplyBVO[]) new HYPubBO().queryByCondition(
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
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("UNSAVEBILL", "4Z05", null,
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("RECALL", "6302", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	private String JKVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		JKVO aggvo = new JKVO();
		JKHeaderVO hvo = (JKHeaderVO) new HYPubBO().queryByPrimaryKey(
				JKHeaderVO.class, workFlowBill.getPk_bill());
		JKBusItemVO[] bvos = (JKBusItemVO[]) new HYPubBO()
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
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("RECALL", "263X", null,
						aggvo, null, getEparam());
			}
		}
		return Result.success();
	}

	private String bXVO(String type, WorkFlowBill workFlowBill)
			throws BusinessException {
		BXVO aggvo = new BXVO();
		BXHeaderVO hvo = (BXHeaderVO) new HYPubBO().queryByPrimaryKey(
				BXHeaderVO.class, workFlowBill.getPk_bill());
		BXBusItemVO[] bvos = (BXBusItemVO[]) new HYPubBO()
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
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
				getIplatFormEntry().processAction("RECALL", "264X", null,
						aggvo, null, getEparam());
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
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		PayableBillVO hvo = (PayableBillVO) new HYPubBO().queryByPrimaryKey(
				PayableBillVO.class, workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		PayableBillItemVO[] bvos = (PayableBillItemVO[]) new HYPubBO()
				.queryByCondition(
						PayableBillItemVO.class,
						"nvl(dr,0) = 0 and pk_payablebill = '"
								+ workFlowBill.getPk_bill() + "'");
		aggvo.setChildrenVO(bvos);
		SaveOALog(hvo.getBillno(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		GatheringBillVO hvo = (GatheringBillVO) new HYPubBO()
				.queryByPrimaryKey(GatheringBillVO.class,
						workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		GatheringBillItemVO[] bvos = (GatheringBillItemVO[]) new HYPubBO()
				.queryByCondition(
						GatheringBillItemVO.class,
						"nvl(dr,0) = 0 and pk_gatherbill = '"
								+ workFlowBill.getPk_bill() + "'");
		aggvo.setChildrenVO(bvos);
		SaveOALog(hvo.getBillno(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		 * new HYPubBO().queryByPrimaryKey( PayBillVO.class,
		 * workFlowBill.getPk_bill()); aggvo.setParent(hvo); PayBillItemVO[]
		 * bvos = (PayBillItemVO[]) new HYPubBO().queryByCondition(
		 * PayBillItemVO.class, "nvl(dr,0) = 0 and pk_paybill = '" +
		 * workFlowBill.getPk_bill() + "'"); aggvo.setChildrenVO(bvos);
		 */
		SaveOALog(hvo.getBillno(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		ContractBillVO aggvo = (ContractBillVO) new HYPubBO()
				.queryBillVOByPrimaryKey(userObj, workFlowBill.getPk_bill());
		SaveOALog(aggvo.getParentVO().getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		StartReportBillVO aggvo = (StartReportBillVO) new HYPubBO()
				.queryBillVOByPrimaryKey(userObj, workFlowBill.getPk_bill());
		SaveOALog(aggvo.getParentVO().getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) new HYPubBO().findColValue(
						"sys_config", "config_value",
						" config_key='oaadopt_cuser'");
				InvocationInfoProxy.getInstance().setUserId(adopter);
				getIplatFormEntry().processAction("APPROVE", "4D23", null,
						aggvo, null, getEparam());
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		ProjectCheckBillVO aggvo = (ProjectCheckBillVO) new HYPubBO()
				.queryBillVOByPrimaryKey(userObj, workFlowBill.getPk_bill());
		SaveOALog(aggvo.getParentVO().getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		ProjectRegisterBillVO aggvo = (ProjectRegisterBillVO) new HYPubBO()
				.queryBillVOByPrimaryKey(userObj, workFlowBill.getPk_bill());
		SaveOALog(aggvo.getParentVO().getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		ProjectProposalHeadVO hvo = (ProjectProposalHeadVO) new HYPubBO()
				.queryByPrimaryKey(ProjectProposalHeadVO.class,
						workFlowBill.getPk_bill());
		ProjectProposalBillVO aggvo = new ProjectProposalBillVO();
		aggvo.setParent(hvo);
		SaveOALog(hvo.getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		FeeBalanceBillVO aggvo = (FeeBalanceBillVO) new HYPubBO()
				.queryBillVOByPrimaryKey(userObj, workFlowBill.getPk_bill());
		FeeBalanceHeadVO headvo = (FeeBalanceHeadVO) aggvo.getParentVO();
		SaveOALog(headvo.getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				if (headvo.getBill_status() == -1) {
					throw new BusinessException("单据状态不正确，不能收回！");
				}
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		ContrAlterBillVO aggvo = (ContrAlterBillVO) new HYPubBO()
				.queryBillVOByPrimaryKey(userObj, workFlowBill.getPk_bill());
		SaveOALog(aggvo.getParentVO().getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		BudgetAddBillVO aggvo = (BudgetAddBillVO) new HYPubBO()
				.queryBillVOByPrimaryKey(userObj, workFlowBill.getPk_bill());
		SaveOALog(aggvo.getParentVO().getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		AggCtArVO aggvo = (AggCtArVO) new HYPubBO().queryBillVOByPrimaryKey(
				userObj, workFlowBill.getPk_bill());
		SaveOALog(aggvo.getParentVO().getVbillcode(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) new HYPubBO().findColValue(
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
				AggCtArVO aggvonew = (AggCtArVO) new HYPubBO()
						.queryBillVOByPrimaryKey(userObj,
								workFlowBill.getPk_bill());
				getIplatFormEntry().processAction("VALIDATE", "FCT2", null,
						aggvonew, null, getEparam());
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		AggCtApVO aggvo = (AggCtApVO) new HYPubBO().queryBillVOByPrimaryKey(
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
				if ("FCT1-Cxx-GCFKHT".equals(jylx)) {
					// 查询是否已生成，如生成对应报销单则返回报错
					BXHeaderVO[] hvo = (BXHeaderVO[]) new HYPubBO()
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
					String adopter = (String) new HYPubBO().findColValue(
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
					AggCtApVO aggvonew = (AggCtApVO) new HYPubBO()
							.queryBillVOByPrimaryKey(userObj,
									workFlowBill.getPk_bill());
					getIplatFormEntry().processAction("VALIDATE", "FCT1", null,
							aggvonew, null, getEparam());
					new HYPubBO().update(workFlowBill);
				}
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		MaterialPlanAddBillVO aggvo = (MaterialPlanAddBillVO) new HYPubBO()
				.queryBillVOByPrimaryKey(userObj, workFlowBill.getPk_bill());
		SaveOALog(aggvo.getParentVO().getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		CtPuVO hvo = (CtPuVO) new HYPubBO().queryByPrimaryKey(CtPuVO.class,
				workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		CtPuBVO[] bvos = (CtPuBVO[]) new HYPubBO().queryByCondition(
				CtPuBVO.class,
				"nvl(dr,0) = 0 and pk_ct_pu = '" + workFlowBill.getPk_bill()
						+ "'");
		aggvo.setCtPuBVO(bvos);
		SaveOALog(hvo.getVbillcode(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		MaterialPlanBillVO materialPlanBillVO = (MaterialPlanBillVO) new HYPubBO()
				.queryBillVOByPrimaryKey(userObj, workFlowBill.getPk_bill());
		SaveOALog(materialPlanBillVO.getParentVO().getBill_code(),
				workFlowBill, type);
		if (null != materialPlanBillVO) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setBill_status("0");
				workFlowBill.setDef1("return");
				new HYPubBO().update(workFlowBill);
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
		ContractPrepayHeadVO hvo = (ContractPrepayHeadVO) new HYPubBO()
				.queryByPrimaryKey(ContractPrepayHeadVO.class,
						workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		SaveOALog(hvo.getBill_code(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		MaterialaccHeadVO hvo = (MaterialaccHeadVO) new HYPubBO()
				.queryByPrimaryKey(MaterialaccHeadVO.class,
						workFlowBill.getPk_bill());
		aggvo.setParent(hvo);
		MaterialaccBodyVO[] bvos = (MaterialaccBodyVO[]) new HYPubBO()
				.queryByCondition(
						MaterialaccBodyVO.class,
						"nvl(dr,0) = 0 and pk_materialacc = '"
								+ workFlowBill.getPk_bill() + "'");
		aggvo.setTableVO("pm_materialacc_b", bvos);
		SecondMaterialBodyVO[] bvos2 = (SecondMaterialBodyVO[]) new HYPubBO()
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
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		AccruedVO hvo = (AccruedVO) new HYPubBO().queryByPrimaryKey(
				AccruedVO.class, workFlowBill.getPk_bill());
		aggvo.setParentVO(hvo);
		AccruedDetailVO[] bvos = (AccruedDetailVO[]) new HYPubBO()
				.queryByCondition(
						AccruedDetailVO.class,
						"nvl(dr,0) = 0 and pk_accrued_bill = '"
								+ workFlowBill.getPk_bill() + "'");
		aggvo.setTableVO("er_accrued_detail", bvos);
		AccruedVerifyVO[] bvos2 = (AccruedVerifyVO[]) new HYPubBO()
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
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		CashDrawVO hvo = (CashDrawVO) new HYPubBO().queryByPrimaryKey(
				CashDrawVO.class, workFlowBill.getPk_bill());
		aggvo.setParentVO(hvo);
		SaveOALog(hvo.getBillno(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		BillHeaderVO hvo = (BillHeaderVO) new HYPubBO().queryByPrimaryKey(
				BillHeaderVO.class, workFlowBill.getPk_bill());
		aggvo.setParentVO(hvo);
		BillItemVO[] bvos = (BillItemVO[]) new HYPubBO().queryByCondition(
				BillItemVO.class, "nvl(dr,0) = 0 and cbillid = '"
						+ workFlowBill.getPk_bill() + "'");
		aggvo.setChildrenVO(bvos);
		SaveOALog(hvo.getVbillcode(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
		nc.vo.cmp.bill.BillVO hvo = (nc.vo.cmp.bill.BillVO) new HYPubBO()
				.queryByPrimaryKey(nc.vo.cmp.bill.BillVO.class,
						workFlowBill.getPk_bill());
		aggvo.setParentVO(hvo);
		BillDetailVO[] bvos = (BillDetailVO[]) new HYPubBO().queryByCondition(
				BillDetailVO.class, "nvl(dr,0) = 0 and pk_paybill = '"
						+ workFlowBill.getPk_bill() + "'");
		aggvo.setChildrenVO(bvos);
		SaveOALog(hvo.getBill_no(), workFlowBill, type);
		if (null != aggvo) {
			if ("adopt".equals(type)) {
				workFlowBill.setBill_status("2");
				String adopter = (String) new HYPubBO().findColValue(
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
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				if (workFlowBill.getDef1() == null
						&& !"return".equals(workFlowBill.getDef1())) {
					workFlowBill.setDef1("return");
					workFlowBill.setBill_status("0");
					new HYPubBO().update(workFlowBill);
					String sql = "UPDATE CMP_PAYBILL SET DEF3 = '~' WHERE "
							+ "PK_PAYBILL = '" + workFlowBill.getPk_bill()
							+ "'";
					new BaseDAO().executeUpdate(sql);
					/*
					 * hvo.setDef3(UFBoolean.FALSE + ""); hvo.setDr(0);
					 * hvo.setStatus(VOStatus.UPDATED); new
					 * HYPubBO().update(hvo);
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
				String adopter = (String) new HYPubBO().findColValue(
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

				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
					String adopter = (String) new HYPubBO().findColValue(
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
					new HYPubBO().update(workFlowBill);
				}
				// 审批完生效
				/*
				 * AggStapply aggvonew = (AggStapply) new
				 * HYPubBO().queryBillVOByPrimaryKey( userObj,
				 * workFlowBill.getPk_bill());
				 * getIplatFormEntry().processAction("VALIDATE", "6404", null,
				 * aggvonew, null, getEparam()); new
				 * HYPubBO().update(workFlowBill);
				 */
				/*
				 * getIplatFormEntry().processAction("APPROVE", "6404", null,
				 * aggvo, null, getEparam());
				 */
				new HYPubBO().update(workFlowBill);
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
				String adopter = (String) new HYPubBO().findColValue(
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
				 * aggvonew, null, getEparam()); new
				 * HYPubBO().update(workFlowBill);
				 */

				/*
				 * getIplatFormEntry().processAction("APPROVE", "6115", null,
				 * aggvo, null, getEparam()); new
				 * HYPubBO().update(workFlowBill);
				 */
			} else if ("return".equals(type)) {
				workFlowBill.setDef1("return");
				workFlowBill.setBill_status("0");
				new HYPubBO().update(workFlowBill);
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
			UserVO userVO = (UserVO) new HYPubBO().queryByPrimaryKey(
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
		new HYPubBO().insert(vo);
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
