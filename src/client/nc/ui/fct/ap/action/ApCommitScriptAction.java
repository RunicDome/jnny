package nc.ui.fct.ap.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.itf.pmr.pushoautil.IPushOAService;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pubapp.uif2app.actions.pflow.CommitScriptAction;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.uif2.UIState;
import nc.vo.fct.ap.entity.AggCtApVO;
import nc.vo.fct.rule.ActionStateRule;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.uif2.LoginContext;

// 付款合同
@SuppressWarnings({ "unused", "restriction" })
public class ApCommitScriptAction extends CommitScriptAction {
	private static final long serialVersionUID = -8002518623443450294L;

	public ApCommitScriptAction() {
	}

	public void doAction(ActionEvent e) throws Exception {
		if (4 == MessageDialog.showYesNoDlg(getModel().getContext()
				.getEntranceUI(), null, NCLangRes4VoTransl.getNCLangRes()
				.getStrByID("2004003_0", "04020003-0004"))) {

			super.doAction(e);
			/*IPushOAService ss = NCLocator.getInstance().lookup(IPushOAService.class);
			ss.fkhtpushoa((AggCtApVO) this.getModel().getSelectedData());*/
		}
	}

	protected boolean isActionEnable() {
		if ((UIState.ADD == this.model.getUiState())
				|| (UIState.EDIT == this.model.getUiState())) {
			return true;
		}

		if (getModel().getSelectedOperaDatas() == null) {
			return false;
		}
		if (getModel().getSelectedOperaDatas().length > 1) {
			return true;
		}

		ActionStateRule rule = new ActionStateRule();
		return rule.isHaveFree(getModel().getSelectedData());
	}
}