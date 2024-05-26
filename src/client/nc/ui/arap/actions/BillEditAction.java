package nc.ui.arap.actions;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.arap.fieldmap.IBillFieldGet;
import nc.itf.arap.payable.IArapPayableBillQueryService;
import nc.itf.arap.prv.ICheckStatusCallBack;
import nc.ui.arap.pub.ArapUiUtil;
import nc.ui.pubapp.uif2app.view.BillListView;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.EditAction;
import nc.ui.uif2.editor.IEditor;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.arap.basebill.BaseItemVO;
import nc.vo.arap.billstatus.ARAPBillStatus;
import nc.vo.arap.pub.BillEnumCollection;
import nc.vo.arap.pub.BillEnumCollection.ApproveStatus;
import nc.vo.arap.pub.BillEnumCollection.BillCooperate;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

/**
 * 编辑操作
 * 
 * @see
 * @author guodw
 * @version V6.0
 * @since V6.0 创建时间：2009-8-26 上午08:38:15
 */
public class BillEditAction extends EditAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BillEditAction() {
		super();
	}

	private IEditor editor;

	public IEditor getEditor() {
		return editor;
	}

	public void setEditor(IEditor editor) {
		this.editor = editor;
	}

	@Override
	protected boolean isActionEnable() {
		if (listView != null && listView.isShowing()) {
			return false;
		}
		if (null == getModel().getSelectedData())
			return false;
		
		if ((getModel().getUiState() == UIState.NOT_EDIT || getModel().getUiState() == UIState.DISABLE)
				&& null != getModel().getSelectedData()) {
			AggregatedValueObject tmp = (AggregatedValueObject) getModel().getSelectedData();
			BaseBillVO parent = (BaseBillVO) tmp.getParentVO();
			Integer billstatus = parent.getBillstatus();
			Integer approvestatus = parent.getApprovestatus();
			if ((billstatus.equals(ARAPBillStatus.AUDIT.VALUE) || billstatus.equals(ARAPBillStatus.SIGN.VALUE))
					&& parent.getIsinit().equals(UFBoolean.FALSE) ) {
				return false;
			}
			BaseItemVO itemVO = (BaseItemVO) tmp.getChildrenVO()[0];
			
			if(approvestatus == 3){
				return false;
			}
			Integer coordflag = parent.getCoordflag();
			if (BillCooperate.NOSUER.VALUE.equals(coordflag)) {
				return false;
			}
			UFBoolean isforce = parent.getIsforce();

			if (isforce != null && isforce.booleanValue() && IBillFieldGet.FK.equals(parent.getBillclass())
					&& BillEnumCollection.CommissionPayType.ForceCommPayALL.VALUE.equals(itemVO.getCommpaytype())) {
				return true;
			}

			// 直接借记退回的付款单可以修改
			// UFBoolean sddreversalflag = (UFBoolean)
			// parent.getAttributeValue(IBillFieldGet.SDDREVERSALFLAG);
			// if(sddreversalflag != null && sddreversalflag.booleanValue()){
			// return false;
			// }
			if (approvestatus.intValue() == ApproveStatus.COMMIT.VALUE
					&& WorkbenchEnvironment.getInstance().getLoginUser().getCuserid().equals(parent.getCreator())) {
				return true;
			}

		}
		if (getModel().getUiState() == UIState.ADD || getModel().getUiState() == UIState.EDIT) {
			return false;
		}

		return true;
	}

	public void doAction(ActionEvent e) throws Exception {
		AggregatedValueObject tmp = (AggregatedValueObject) getModel().getSelectedData();
		
		//提交态的单据，判断财务组织是否上共享，若上共享则不允许修改。
		AggregatedValueObject[] aggs = {tmp};
		NCLocator.getInstance().lookup(IArapPayableBillQueryService.class).checkCommitBillEdit(aggs,"修改");
		
		// checkTopBilltype((BaseAggVO)tmp);
		BaseBillVO parent = (BaseBillVO) tmp.getParentVO();
		Integer approvestatus = parent.getApprovestatus();
		if ((approvestatus.intValue() == ApproveStatus.GOINGON.VALUE) && parent.getIsinit().equals(UFBoolean.FALSE)) {
			if (!NCLocator.getInstance().lookup(ICheckStatusCallBack.class)
					.isCheckman(parent.getPrimaryKey(), parent.getPk_tradetype(), ArapUiUtil.getPk_user())) {
				throw new BusinessException("审批流程中的单据， 只有当前审批人可以修改!");
			}
		}

		try {
			NCLocator.getInstance().lookup(ICheckStatusCallBack.class)
					.checkUserAfterR(parent.getPrimaryKey(), parent.getPk_tradetype(), parent.getCreator());
		} catch (Exception ex) {
			try {
				Logger.error(ex.getMessage(), ex);
				throw new BusinessException(ex.getMessage());
			} catch (Exception e1) {
				Logger.error(e1.getMessage(), e1);
				throw new BusinessException(e1.getMessage());
			}
		}

		// fireNodeKeyEvent();
		super.doAction(e);

		// 将表头自定义场景设进参照
		ArapUiUtil.setDataPowerForAllRef(((nc.ui.uif2.editor.BillForm) this.getEditor()).getBillCardPanel());
	}

	// private void checkTopBilltype(BaseAggVO bill) throws BusinessException {
	// CircularlyAccessibleValueObject[] childrenVOs = bill.getChildrenVO();
	// for (CircularlyAccessibleValueObject childrenVO : childrenVOs) {
	// Object top_billtype =
	// childrenVO.getAttributeValue(IBillFieldGet.TOP_BILLTYPE);
	// Object top_billId =
	// childrenVO.getAttributeValue(IBillFieldGet.TOP_BILLID);
	// if (null != top_billtype && top_billtype.equals("36D1") && null !=
	// top_billId) {
	// String[] top_billID = { top_billId.toString() };
	// IApplyService applyService =
	// NCLocator.getInstance().lookup(IApplyService.class);
	// if (applyService.isHasOpsrctype(top_billID)) {
	// throw new BusinessException("付款申请审核通过后生成的付款单不允许修改！");
	// }
	// }
	// }
	// }

	BillListView listView = null;

	public BillListView getListView() {
		return listView;
	}

	public void setListView(BillListView listView) {
		this.listView = listView;
	}

	private void fireNodeKeyEvent() {
		AggregatedValueObject tmp = (AggregatedValueObject) getModel().getSelectedData();
		BaseBillVO parent = (BaseBillVO) tmp.getParentVO();
		getModel().getContext().setPk_org(parent.getPk_org());
		getModel().fireEvent(new nc.ui.pubapp.uif2app.event.OrgChangedEvent(null, parent.getPk_org()));
	}
}
