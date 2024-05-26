package nc.ui.pm.action;

import java.awt.event.ActionEvent;
import java.util.List;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.ui.pcm.utils.GetDao;
import nc.ui.pm.model.BillManageModel;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.view.BillOrgPanel;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;

@SuppressWarnings("unused")
public class AddAction extends nc.ui.pubapp.uif2app.actions.AddAction {
	private static final long serialVersionUID = 3849057892342453364L;

	private ShowUpableBillForm billForm;

	public AddAction() {
	}

	public ShowUpableBillForm getBillForm() {
		return this.billForm;
	}

	public void setBillForm(ShowUpableBillForm billForm) {
		this.billForm = billForm;
	}

	public void doAction(ActionEvent e) throws Exception {
		if (null != getBillForm().getBillOrgPanel()) {
			getBillForm().getBillOrgPanel().setPkOrg(null);
		}
		super.doAction(e);
	}
}