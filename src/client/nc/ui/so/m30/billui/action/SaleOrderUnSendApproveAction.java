package nc.ui.so.m30.billui.action;

import java.awt.event.ActionEvent;

import nc.ui.pubapp.uif2app.AppUiState;

import nc.ui.pubapp.uif2app.actions.pflow.UnCommitScriptAction;

import nc.ui.pubapp.uif2app.model.BillManageModel;

import nc.vo.so.m30.entity.SaleOrderHVO;

import nc.vo.so.m30.entity.SaleOrderVO;

import nc.vo.so.pub.enumeration.BillStatus;
import nc.ws.intf.OaWorkFlowUtil;

@SuppressWarnings({ "restriction", "unused" })
public class SaleOrderUnSendApproveAction extends UnCommitScriptAction {
	private static final long serialVersionUID = 4549098173080532064L;

	public SaleOrderUnSendApproveAction() {
	}

	protected boolean isActionEnable() {
		boolean isEnable = (getModel().getAppUiState() == AppUiState.NOT_EDIT)
				&& (null != getModel().getSelectedData());

		if (isEnable) {
			Object[] selectedRows = getModel().getSelectedOperaDatas();
			SaleOrderVO selectedData = (SaleOrderVO) getModel()
					.getSelectedData();

			Integer billstatus = selectedData.getParentVO().getFstatusflag();
			String approver = selectedData.getParentVO().getApprover();

			isEnable = ((null != selectedRows) && (selectedRows.length > 1))
					|| ((null == approver) && (BillStatus.AUDITING
							.equalsValue(billstatus)));
		}

		return isEnable;
	}
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		// TODO Auto-generated method stub
		SaleOrderVO selectedData = (SaleOrderVO) getModel()
				.getSelectedData();
		OaWorkFlowUtil.backOaWorkFlow(selectedData.getParentVO().getPrimaryKey());
		super.doAction(e);
	}
}