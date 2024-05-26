package nc.ui.cmp.bill.actions;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.exception.ComponentException;
import nc.bs.trade.business.HYPubBO;
import nc.cmp.pub.exception.ExceptionHandler;
import nc.impl.cmp.proxy.Proxy;
import nc.itf.cmp.fieldmap.IBillFieldGet;
import nc.uap.rbac.core.dataperm.DataPermissionFacade;
import nc.ui.cmp.bill.views.CmpBillCard;
import nc.ui.cmp.bill.views.CmpChangeBillCard;
import nc.ui.cmp.bill.views.CmpRecBillCard;
import nc.ui.cmp.view.MutilTransBillListView;
import nc.ui.ml.NCLangRes;
import nc.ui.pubapp.uif2app.actions.EditAction;
import nc.ui.pubapp.uif2app.view.MutilTransBillForm;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.vo.cmp.BusiStatus;
import nc.vo.cmp.bill.BillDetailVO;
import nc.vo.cmp.bill.BillVO;
import nc.vo.cmp.bill.CmpBillFieldGet;
import nc.vo.cmp.fields.IBillFieldAdapter;
import nc.vo.cmp.settlement.CheckException;
import nc.vo.cmp.settlement.SettleEnumCollection;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.uif2.LoginContext;
import nc.ws.intf.WorkFlowBill;

// 付款结算修改按钮
@SuppressWarnings({ "restriction", "unused" })
public class CmpBillEditAction extends EditAction {
	private static final long serialVersionUID = 7409386188335331505L;
	private MutilTransBillForm editor;
	private MutilTransBillListView listView;

	public CmpBillEditAction() {
	}

	public void doAction(ActionEvent e) throws Exception {
		Object[] selectedOperaDatas = getSelectedAggVOs();
		CheckException.checkArgument(
				selectedOperaDatas == null,
				NCLangRes4VoTransl.getNCLangRes().getStrByID("3607mng_0",
						"03607mng-0043"));

		CheckException.checkArgument(
				selectedOperaDatas.length != 1,
				NCLangRes4VoTransl.getNCLangRes().getStrByID("3607mng_0",
						"03607mng-0044"));

		if (checkDataPermission(this.editor, selectedOperaDatas[0])) {
			SuperVO vo = (SuperVO) ((AggregatedValueObject) selectedOperaDatas[0])
					.getParentVO();

			CmpBillFieldGet cmpBillFieldGet = CmpBillFieldGet.getInstance();
			// 是否推送OA
			BillVO headvo = (BillVO) vo;
			String pk_bill = (String) vo.getPrimaryKey();
			String def3 = headvo.getDef3();
			if("Y".equals(def3)){
				String strWhere = " nvl(dr,0) = 0 and pk_bill ='" + pk_bill + "'";
				WorkFlowBill[] workFlowBills = (WorkFlowBill[]) new HYPubBO()
						.queryByCondition(WorkFlowBill.class, strWhere);
				if (null != workFlowBills && workFlowBills.length == 1
						&& null == workFlowBills[0].getDef1()) {
					WorkFlowBill workFlowBill = workFlowBills[0];
					if (workFlowBill != null) {
						throw new BusinessException("该单据已提交至OA系统，请从OA系统中退回后再修改,REQUESTID["+workFlowBill.getRequestid()+"]");
					}
				}
			}
			Integer settleflag = (Integer) cmpBillFieldGet.getAtrrValue(vo,
					"h_settleflag");

			UFBoolean is_cf = (UFBoolean) cmpBillFieldGet.getAtrrValue(vo,
					"is_cf");

			String source_flag = (String) cmpBillFieldGet.getAtrrValue(vo,
					"h_src_syscode");

			if ((getEditor() instanceof CmpRecBillCard)) {
				((CmpRecBillCard) getEditor()).setBill(null);
			} else if ((getEditor() instanceof CmpBillCard)) {
				((CmpBillCard) getEditor()).setBill(null);
			} else if ((getEditor() instanceof CmpChangeBillCard)) {
				((CmpChangeBillCard) getEditor()).setBill(null);
			}

			if ((this.editor != null) && (!this.editor.isShowing())) {
				this.editor.showMeUp();
			}
			super.doAction(e);
			getModel()
					.fireEvent(
							new AppEvent("copyBill", null, getModel()
									.getSelectedData()));

			if (((this.editor instanceof CmpBillCard))
					&& (((CmpBillCard) this.editor)
							.getDefaultRefWherePartHandler() != null)) {
				((CmpBillCard) this.editor).getDefaultRefWherePartHandler()
						.fireAllFilter();
			} else if (((this.editor instanceof CmpRecBillCard))
					&& (((CmpRecBillCard) this.editor)
							.getDefaultRefWherePartHandler() != null)) {

				((CmpRecBillCard) this.editor).getDefaultRefWherePartHandler()
						.fireAllFilter();

			}

		} else {

			ShowStatusBarMsgUtil.showErrorMsg(NCLangRes.getInstance()
					.getStrByID("uif2", "ExceptionHandlerWithDLG-000000"),
					IShowMsgConstant.getDataPermissionInfo(), getModel()
							.getContext());
		}
	}

	protected boolean checkDataPermission(MutilTransBillForm editor,
			Object aggVO) {
		setMdOperateCode("edit");

		if ((getEditor() instanceof CmpRecBillCard)) {
			setResourceCode("cmprecbill");
		} else if ((getEditor() instanceof CmpBillCard)) {
			setResourceCode("cmppaybill");
		} else if ((getEditor() instanceof CmpChangeBillCard)) {
			setResourceCode("cmpchangebill");
		}

		if (((StringUtil.isEmptyWithTrim(getOperateCode())) && (StringUtil
				.isEmptyWithTrim(getMdOperateCode())))
				|| (StringUtil.isEmptyWithTrim(getResourceCode()))) {
			return true;
		}

		LoginContext context = getModel().getContext();
		String userId = context.getPk_loginUser();
		String pkgroup = context.getPk_group();
		Object data = aggVO;
		boolean hasp = true;
		if (!StringUtil.isEmptyWithTrim(getMdOperateCode())) {
			hasp = DataPermissionFacade.isUserHasPermissionByMetaDataOperation(
					userId, getResourceCode(), getMdOperateCode(), pkgroup,
					data);
		} else
			hasp = DataPermissionFacade.isUserHasPermission(userId,
					getResourceCode(), getOperateCode(), pkgroup, data);
		return hasp;
	}

	protected boolean isActionEnable() {
		AggregatedValueObject[] aggs = getSelectedAggVOs();
		if ((aggs == null) || (aggs.length > 1)) {
			return false;
		}

		AggregatedValueObject aggvo = aggs[0];

		CircularlyAccessibleValueObject parentVO = aggvo.getParentVO();
		IBillFieldGet iBillFieldGet = ((IBillFieldAdapter) aggvo)
				.getIBillFieldGet();
		Integer billstuts = (Integer) iBillFieldGet.getAtrrValue(parentVO,
				"h_billstatus");

		UFBoolean is_cf = (UFBoolean) iBillFieldGet.getAtrrValue(parentVO,
				"is_cf");
		String billtype = (String) iBillFieldGet.getAtrrValue(parentVO,
				"h_pk_billtype");

		if (("F5".equals(billtype))
				&& (UFBoolean.TRUE.equals(is_cf))
				&& (billstuts.equals(Integer.valueOf(BusiStatus.Tempeorary
						.getBillStatusKind())))) {
			BillDetailVO[] children = (BillDetailVO[]) aggvo.getChildrenVO();
			for (BillDetailVO billDetailVO : children) {
				Integer cf_type = billDetailVO.getCf_type();
				if ((cf_type == null)
						|| (!cf_type
								.equals(SettleEnumCollection.CommissionPayType.ForceCommPayALL.VALUE))) {
					return false;
				}

				if ((cf_type != null)
						&& (cf_type
								.equals(SettleEnumCollection.CommissionPayType.ForceCommPayALL.VALUE))) {
					return true;
				}
			}
		}

		if ((billstuts.equals(Integer.valueOf(BusiStatus.Tempeorary
				.getBillStatusKind())))
				|| (billstuts.equals(Integer.valueOf(BusiStatus.Save
						.getBillStatusKind())))) {
			return true;
		}

		String approver = (String) iBillFieldGet.getAtrrValue(parentVO,
				"h_approver");
		Object approvedate = iBillFieldGet.getAtrrValue(parentVO,
				"h_approvedate");

		if (billstuts.intValue() == BusiStatus.AuditHandlFail
				.getBillStatusSubKind()) {
			return true;
		}

		if (((BusiStatus.Save.getBillStatusKind() == billstuts.intValue()) || (BusiStatus.AuditHandling
				.getBillStatusSubKind() == billstuts.intValue()))
				&& (!StringUtil.isEmptyWithTrim(approver))
				&& (null != approvedate)) {
			try {
				String pk_bill = parentVO.getPrimaryKey();
				String pk_tradetype = (String) iBillFieldGet.getAtrrValue(
						parentVO, "trade_type");
				String userId = InvocationInfoProxy.getInstance().getUserId();
				if (Proxy.getIPFWorkflowQry().isCheckman(pk_bill, pk_tradetype,
						userId)) {
					return true;
				}
			} catch (ComponentException e) {
				ExceptionHandler.consume(e);
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		}

		return false;
	}

	private AggregatedValueObject[] getSelectedAggVOs() {
		Object[] value = null;
		if (this.editor.isVisible()) {
			value = new Object[1];
			value[0] = this.editor.getModel().getSelectedData();
			if (value[0] == null) {
				return null;
			}
		} else if ((this.listView != null) && (this.listView.isShowing())) {
			value = this.listView.getModel().getSelectedOperaDatas();
		}

		if ((null == value) || (value.length == 0)) {
			return null;
		}
		AggregatedValueObject[] aggs = new AggregatedValueObject[value.length];
		System.arraycopy(value, 0, aggs, 0, aggs.length);
		return aggs;
	}

	public MutilTransBillForm getEditor() {
		return this.editor;
	}

	public void setEditor(MutilTransBillForm editor) {
		this.editor = editor;
	}

	public MutilTransBillListView getListView() {
		return this.listView;
	}

	public void setListView(MutilTransBillListView listView) {
		this.listView = listView;
	}

}