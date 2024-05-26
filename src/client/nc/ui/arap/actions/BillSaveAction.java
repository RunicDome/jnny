/**
 *
 */
package nc.ui.arap.actions;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import nc.bs.arap.bill.ArapBillPubUtil;
import nc.bs.arap.util.ArapFlowUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.trade.business.HYPubBO;
import nc.bs.uap.bd.notetype.INotetypeConst;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.funcnode.ui.AbstractFunclet;
import nc.itf.arap.fieldmap.IBillFieldGet;
import nc.itf.arap.prv.IArapBillConfirmService;
import nc.itf.arap.web.IWebPubService;
import nc.itf.cmp.busi.ISettleinfoCarrier;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.uap.pf.IPFBusiAction;
import nc.itf.uap.pf.IplatFormEntry;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.arap.bill.ArapBillUIUtil;
import nc.ui.arap.bill.BillCardPanelUtil;
import nc.ui.arap.tbb.TbbMessageDialog;
import nc.ui.arap.view.ArapBillCardForm;
import nc.ui.pcm.utils.GetDao;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.actions.SaveAction;
import nc.ui.pubapp.uif2app.view.MutilTransBillForm;
import nc.ui.uif2.UIState;
import nc.utils.crosscheckrule.CrossControlMsgException;
import nc.vo.arap.basebill.BaseAggVO;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.arap.basebill.BaseItemVO;
import nc.vo.arap.bill.util.BillEventHandlerUtil;
import nc.vo.arap.exception.ArapTbbException;
import nc.vo.arap.pay.PayBillItemVO;
import nc.vo.arap.payable.PayableBillItemVO;
import nc.vo.arap.pub.BillEnumCollection;
import nc.vo.arap.utils.ArrayUtil;
import nc.vo.cmp.settlement.SettlementAggVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.workflow.IPFActionName;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.pubapp.pflow.PfUserObject;
import nc.vo.pubapp.util.NCPfServiceUtils;
import nc.vo.wfengine.definition.WorkflowTypeEnum;

import org.apache.commons.lang.StringUtils;

/**
 * 单据保存
 * 
 * @see
 * @author guodw
 * @version V6.0
 * @since V6.0 创建时间：2009-8-26 上午08:39:32
 */
@SuppressWarnings({ "restriction", "unused" })
public class BillSaveAction extends SaveAction implements IUserObjGetter {

	private static final long serialVersionUID = 1L;
	private PfUserObject userObj;
	private String refNode;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		BillCardPanel billCardPanel = ((MutilTransBillForm) getEditor())
				.getBillCardPanel();
		String tableCode = (billCardPanel.getBillData().getBodyTableCodes())[0];
		BillCardPanelUtil.delBlankLine(billCardPanel, tableCode);
		if (!"200805EPB".equals(getModel().getContext().getNodeCode())) {
			if (this.getEditor() instanceof MutilTransBillForm) {
				((MutilTransBillForm) getEditor()).getBillCardPanel()
						.dataNotNullValidate();
				boolean resultPass = ((MutilTransBillForm) getEditor())
						.getBillCardPanel().getBillData()
						.execValidateFormulas();

				if (!resultPass)
					return;
			}
		}
		BaseAggVO bill = (BaseAggVO) this.getEditor().getValue();

		validate(bill);

		BaseItemVO[] children = (BaseItemVO[]) bill.getChildrenVO();
		if (null == children || children.length == 0) {
			throw ExceptionHandler
					.createException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
							.getStrByID("2006pub_0", "02006pub-0012")/*
																	 * @res
																	 * " 表格不能为空,保存失败 !"
																	 */);
		}
		this.getModel().getSelectedData();
		if (children[0] instanceof PayBillItemVO) {
			Map<String, String> supplierMap = new HashMap<String, String>();
			Map<String, String> recaccountMap = new HashMap<String, String>();
			for (int i = 0, size = billCardPanel.getRowCount(); i < size; i++) {
				DefaultConstEnum recaccount = (DefaultConstEnum) billCardPanel
						.getBillModel().getValueObjectAt(i,
								IBillFieldGet.RECACCOUNT);
				DefaultConstEnum sup = (DefaultConstEnum) billCardPanel
						.getBillModel().getValueObjectAt(i,
								IBillFieldGet.SUPPLIER);
				DefaultConstEnum freecust = (DefaultConstEnum) billCardPanel
						.getBillModel().getValueObjectAt(i,
								IBillFieldGet.FREECUST);
				if (freecust != null) { // 如果是散户，不对银行账户校验
					continue;
				}
				if (sup != null) {
					supplierMap.put(sup.getValue().toString(), sup.getName());
				}
				if (recaccount != null) {
					recaccountMap.put(recaccount.getValue().toString(),
							recaccount.getName());
				}
			}
			ArapBillPubUtil.checkSupplierAndrecaccount(supplierMap,
					recaccountMap);
		}
		for (BaseItemVO item : children) {
			if (StringUtils.isNotEmpty(item.getChecktype())
					&& INotetypeConst.NOTETYPE_CLASS_BUSIPO != BillEventHandlerUtil
							.getNoteClassByPK(item.getChecktype())
					&& StringUtils.isEmpty(item.getCheckno())) {
				throw ExceptionHandler
						.createException(nc.vo.ml.NCLangRes4VoTransl
								.getNCLangRes().getStrByID("2006arappub0523_0",
										"02006arappub0523-0000")/*
																 * @res
																 * "票据类型空票票据必须录入空票号"
																 */);
			}
		}
		bill.getHeadVO().setBillstatus(BillEnumCollection.BillSatus.Save.VALUE);
		AggregatedValueObject aggVO = null;
		// 来源于协同单据
		if (refNode != null && refNode.equals("isCoorcomfirm")) {
			bill.getHeadVO().setCoordflag(1);
			ArapBillUIUtil
					.setNewClientDateAndUser(new AggregatedValueObject[] { bill });
			bill.getHeadVO().setStatus(VOStatus.UPDATED);
			for (CircularlyAccessibleValueObject item : bill.getChildrenVO()) {
				item.setStatus(VOStatus.UPDATED);
			}
			// 更新单据状态，并删除协同信息
			bill = (BaseAggVO) (NCLocator.getInstance().lookup(
					IArapBillConfirmService.class).updateConfirmInfo(bill));
		}
		if (this.getModel().getUiState() == UIState.ADD) {

			// 独立结算信息
			SettlementAggVO settlementInfo = ((ISettleinfoCarrier) this
					.getEditor()).getSettlementInfo();
			if (null != settlementInfo) {
				// 设置字段可编辑
				BillCardPanelUtil.contorlFeildEditable(billCardPanel, true,
						new String[] { IBillFieldGet.OBJTYPE,
								IBillFieldGet.CUSTOMER, IBillFieldGet.SUPPLIER,
								IBillFieldGet.RECACCOUNT,
								IBillFieldGet.PAYACCOUNT,
								IBillFieldGet.PK_PSNDOC,
								IBillFieldGet.MONEY_DE, IBillFieldGet.MONEY_CR,
								IBillFieldGet.PK_CURRTYPE });
			}
			if (bill instanceof ISettleinfoCarrier)
				((ISettleinfoCarrier) bill).setSettlementInfo(settlementInfo);

			ArapBillUIUtil
					.setNewClientDateAndUser(new AggregatedValueObject[] { bill });

			// 如果预算预警控制，截获异常，与用户交互。若用户点确定，则通过，否则取消
			try {
				aggVO = executeBatchPM(ArapFlowUtil.getCommitActionCode(bill
						.getHeadVO().getPk_org(), bill.getHeadVO()
						.getPk_billtype()), bill, getUserObj());
			} catch (ArapTbbException tbbEx) {
				TbbMessageDialog dialog = new TbbMessageDialog(
						tbbEx.getMessage());
				if (UIDialog.ID_OK != dialog.showModal()) {
					return;
				}
				bill.setAlarmPassed(true);
				aggVO = executeBatchPM(ArapFlowUtil.getCommitActionCode(bill
						.getHeadVO().getPk_org(), bill.getHeadVO()
						.getPk_billtype()), bill, getUserObj());

			} catch (CrossControlMsgException ex) {
				TbbMessageDialog dialog = new TbbMessageDialog(ex.getMessage());
				if (UIDialog.ID_OK != dialog.showModal()) {
					return;
				}
				bill.setCrossCheckPassed(true);
				aggVO = executeBatchPM(ArapFlowUtil.getCommitActionCode(bill
						.getHeadVO().getPk_org(), bill.getHeadVO()
						.getPk_billtype()), bill, getUserObj());

			}
			if (aggVO == null) {
				return;
			}
			ArapBillUIUtil.refreshChildVO2HeadVO(aggVO);
		} else if (this.getModel().getUiState() == UIState.EDIT) {
			if (NCLocator
					.getInstance()
					.lookup(nc.itf.arap.prv.ICheckStatusCallBack.class)
					.isWorkFlowStartup(bill.getHeadVO().getPrimaryKey(),
							bill.getHeadVO().getPk_billtype())) {
				// throw ExceptionHandler
				// .createException("流程类型组织参数已经切换为审批流，已经启动工作流的单据不能修改。请修改参数或终止工作流。");
			}
			ArapBillUIUtil
					.setEditClientDateAndUser(new AggregatedValueObject[] { bill });

			// 如果预算预警控制，截获异常，与用户交互。若用户点确定，则通过，否则取消
			try {
				aggVO = executeBatchPM(ArapBillUIUtil.UPDATE, bill,
						getUserObj());
			} catch (ArapTbbException tbbEx) {
				TbbMessageDialog dialog = new TbbMessageDialog(
						tbbEx.getMessage());
				if (UIDialog.ID_OK != dialog.showModal()) {
					return;
				}
				bill.setAlarmPassed(true);
				aggVO = executeBatchPM(ArapBillUIUtil.UPDATE, bill,
						getUserObj());
			} catch (CrossControlMsgException ex) {
				TbbMessageDialog dialog = new TbbMessageDialog(ex.getMessage());
				if (UIDialog.ID_OK != dialog.showModal()) {
					return;
				}
				bill.setCrossCheckPassed(true);
				aggVO = executeBatchPM(ArapBillUIUtil.UPDATE, bill,
						getUserObj());
			}
			if (aggVO == null) {
				return;
			}
			ArapBillUIUtil.refreshChildVO2HeadVO(aggVO);
		}

		if (SwingUtilities.isEventDispatchThread()) {
			refreshUI(bill, aggVO);
		} else {
			final BaseAggVO bill1 = bill;
			final AggregatedValueObject aggVO1 = aggVO;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					refreshUI(bill1, aggVO1);
				}
			});
		}

		// 清空独立结算信息
		((ISettleinfoCarrier) getEditor()).setSettlementInfo(null);
	}

	private AggregatedValueObject reCallWorkFlowEdit(BaseAggVO billvo,
			String billtype, String primaryKey, WorkflownoteVO noteVO)
			throws BusinessException {
		AggregatedValueObject aggVO;
		Object[] returnvos;

		boolean doStartFlag = true;
		boolean deleteApproveFlowFlag = false;
		if (NCLocator
				.getInstance()
				.lookup(nc.itf.arap.prv.ICheckStatusCallBack.class)
				.isApproveFlowStartup(billvo.getHeadVO().getPrimaryKey(),
						billtype)) {
			if (Integer.valueOf(2)
					.equals(billvo.getHeadVO().getApprovestatus())) {
				// 审批中时不重启工作流(由于审批通过及以后状态时没有修改功能.所以不用控制)
				doStartFlag = false;
			} else {
				// 由审批流切换成工作流时.需要删除不用的审批流
				deleteApproveFlowFlag = true;
			}
		}

		if (noteVO != null && ("MAKEBILL").equals(noteVO.getActiontype())) {
			noteVO.setApproveresult("Y");
			billvo.isUpdateForSignal = true;
			Object returnObj = NCLocator
					.getInstance()
					.lookup(IPFBusiAction.class)
					.processAction(IPFActionName.SIGNAL, billtype, noteVO,
							billvo, null, null);
			aggVO = (AggregatedValueObject) ((Object[]) returnObj)[0];
		} else if (doStartFlag) {
			if (deleteApproveFlowFlag) {
				try {
					NCLocator
							.getInstance()
							.lookup(IWebPubService.class)
							.deleteWorkflow(billvo.getHeadVO().getPrimaryKey(),
									billtype, false,
									WorkflowTypeEnum.Approveflow.getIntValue());
					NCLocator
							.getInstance()
							.lookup(IWebPubService.class)
							.deleteWorkflow(
									billvo.getHeadVO().getPrimaryKey(),
									billtype,
									false,
									WorkflowTypeEnum.SubApproveflow
											.getIntValue());
				} catch (Exception e) {
					ExceptionHandler.handleException(e);
				}
			}
			// returnvos = (Object[]) NCPfServiceUtils.processBatch(
			// ArapBillUIUtil.START, billtype, new BaseAggVO[] { billvo },
			// null, new WorkflownoteVO());
			returnvos = (Object[]) NCPfServiceUtils.processBatch(
					ArapBillUIUtil.SAVE, billtype, new BaseAggVO[] { billvo },
					null, new WorkflownoteVO());
			aggVO = (AggregatedValueObject) returnvos[0];
		} else {
			returnvos = (Object[]) NCPfServiceUtils.processBatch(
					ArapBillUIUtil.MODIFY, billtype,
					new BaseAggVO[] { billvo }, null, new WorkflownoteVO());
			aggVO = (AggregatedValueObject) returnvos[0];
		}
		return aggVO;
	}

	private void refreshUI(BaseAggVO bill, AggregatedValueObject aggVO) {
		if (this.getModel().getUiState() == UIState.EDIT) {
			getModel().directlyUpdate(aggVO);
		} else if (this.getModel().getUiState() == UIState.ADD) {
			getModel().directlyAdd(aggVO);
		}

		this.getModel().setUiState(UIState.NOT_EDIT);
		JComponent entranceUI = this.getModel().getContext().getEntranceUI();
		ArapBillCardForm.resetMenuAction(entranceUI);

		// @see BillOrgPanel.fireChangedEvent
		// this.getModel().getContext().setPk_org(newPkOrg);
		// 需要在单据保存或者取消的时候将context中的pk_org设置为空或者默认组织

		WorkbenchEnvironment.getInstance().putClientCache(
				IBillFieldGet.ARAP_DEFAULT_ORG
						+ aggVO.getParentVO().getAttributeValue(
								IBillFieldGet.PK_BILLTYPE),
				aggVO.getParentVO().getAttributeValue(IBillFieldGet.PK_ORG));
		this.getModel().getContext().setPk_org(null);
		((AbstractFunclet) this.getModel().getContext().getEntranceUI())
				.fireFuncletLinkEvent(bill, 0);
		showSuccessInfo();

		// 显示预算控制信息
		// 2015-7-30注销，因为在CreditCheckDecoratorAction.showSuccessInfo()会覆盖下面提示的信息
		// by chenshuaia
		// ArapBillUIUtil.showTbbMessage(new AggregatedValueObject[] { aggVO },
		// this.getModel().getContext());
	}

	private AggregatedValueObject executeBatchPM(String type, BaseAggVO bill,
			PfUserObject userObj) throws Exception {
		// Object executePM = ArapBillUIUtil.executeBatchPM(type, getModel()
		// .getContext().getEntranceUI(),
		// new AggregatedValueObject[] { bill }, userObj);

		IplatFormEntry action = (IplatFormEntry) NCLocator.getInstance()
				.lookup(IplatFormEntry.class.getName());
		Object executePM = action.processAction("SAVEBASE", bill.getHeadVO()
				.getPk_billtype(), null, bill, userObj, null);
		AggregatedValueObject aggvo = (AggregatedValueObject) ArrayUtil
				.getFirstInArrays((Object[]) executePM);
		return aggvo;
	}

	public PfUserObject getUserObj() {
		if (userObj == null) {
			userObj = new PfUserObject();
		}
		return userObj;
	}

	public void setUserObj(PfUserObject userObj) {
		this.userObj = userObj;
	}

	public String getRefNode() {
		return refNode;
	}

	public void setRefNode(String refNode) {
		this.refNode = refNode;
	}

	// TODO 单据同步OA功能 start create by zwh
	private HYPubBO hyPubBO;

	public HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}
}