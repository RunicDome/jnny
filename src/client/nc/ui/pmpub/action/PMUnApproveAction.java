package nc.ui.pmpub.action;

import java.awt.event.ActionEvent;

import nc.bs.pubapp.pf.util.ApproveFlowUtil;
import nc.md.data.access.NCObject;
import nc.ui.pm.action.ActionInitializer;
import nc.ui.pubapp.uif2app.AppUiState;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pm.common.enums.PMPowerActionEnum;

@SuppressWarnings({ "serial", "restriction" })
public class PMUnApproveAction extends PMScriptPFlowAction {
	public PMUnApproveAction() {
		ActionInitializer.initializeAction(this, "UnApprove");
		setActionName("UNAPPROVE");
		setTitle(NCLangRes4VoTransl.getNCLangRes().getStrByID("pmpub_0",
				"04801000-0256"));

		setDefaultMdOperateCode(PMPowerActionEnum.UNAPPROVE.getActioncode());
	}

	protected String getErrorMsg() {
		return NCLangRes4VoTransl.getNCLangRes().getStrByID("pmpub_0",
				"04801000-0257");
	}

	protected boolean isActionEnable() {
		Object[] objs = getModel().getSelectedOperaDatas();
		if ((objs != null) && (objs.length > 1)) {
			return true;
		}

		Object selectedData = getModel().getSelectedData();
		int status = -1;
		if (selectedData != null) {
			NCObject obj = NCObject.newInstance(selectedData);
			if (obj != null) {
				status = ApproveFlowUtil.getBillStatus(obj).intValue();
			}
		}

		boolean isEnable = (getModel().getAppUiState() == AppUiState.NOT_EDIT)
				&& (selectedData != null)
				&& ((status == 1) || (status == 2) || (0 == status));
		return isEnable;
	}
	@Override
	public void doAction(ActionEvent e) throws Exception {
		// TODO Auto-generated method stub
	
				super.doAction(e);
	}
}
