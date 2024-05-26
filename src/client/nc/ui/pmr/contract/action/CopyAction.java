package nc.ui.pmr.contract.action;

import java.awt.event.ActionEvent;

import nc.ui.pm.action.PMCopyAction;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pm.feebalance.PmFeebalanceHVO;

@SuppressWarnings("restriction")
public class CopyAction extends PMCopyAction {
	private static final long serialVersionUID = 1L;

	public CopyAction() {
	}

	public void doAction(ActionEvent e) throws Exception {
		super.doAction(e);
		AggPmFeebalance billVO = (AggPmFeebalance) getModel().getSelectedData();
		intBody(billVO.getParent());

	}

	private void intBody(PmFeebalanceHVO hvo) {
		getEditor().getBillCardPanel().getHeadItem("pm_feebalance")
				.setValue(null);
		getEditor().getBillCardPanel().getBodyItem("pm_feebalance_b")
				.setValue(null);
	}
}