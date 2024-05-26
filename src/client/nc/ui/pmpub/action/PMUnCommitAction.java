package nc.ui.pmpub.action;

import java.awt.event.ActionEvent;

import nc.bs.logging.Logger;
import nc.md.data.access.NCObject;
import nc.oa.web.NCService;
import nc.oa.web.NCServiceSoap;
import nc.ui.pm.action.ActionInitializer;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pubapp.uif2app.AppUiState;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pcm.feebalance.FeeBalanceBillVO;
import nc.vo.pcm.feebalance.FeeBalanceHeadVO;
import nc.vo.pub.AggregatedValueObject;
import org.apache.commons.lang.StringUtils;

@SuppressWarnings({ "restriction", "unused" })
public class PMUnCommitAction extends PMScriptPFlowAction {

	private static final long serialVersionUID = 1L;

	public PMUnCommitAction() {
		ActionInitializer.initializeAction(this, "RECALL");
		setActionName("UNSAVEBILL");
		setTitle(NCLangRes4VoTransl.getNCLangRes().getStrByID("pmpub_0",
				"04801000-0258"));
	}

	protected String getErrorMsg() {
		return NCLangRes4VoTransl.getNCLangRes().getStrByID("pmpub_0",
				"04801000-0259");
	}

	protected boolean isActionEnable() {
		Object[] seldatas = getModel().getSelectedOperaDatas();
		if ((model.getAppUiState() == AppUiState.NOT_EDIT)
				&& (null != seldatas) && (seldatas.length > 1)) {
			return true;
		}

		AggregatedValueObject selectedData = (AggregatedValueObject) getModel()
				.getSelectedData();
		int status = -1;
		String approver = null;
		if (selectedData != null) {
			status = extractApproveStatus(selectedData).intValue();
			approver = extractApprover(selectedData);
		}

		boolean isEnable = ((selectedData != null) && (status == 2) && (StringUtils
				.isEmpty(approver))) || (status == 3);

		return isEnable;
	}
	@Override
	public void doAction(ActionEvent e) throws Exception {
		// TODO Auto-generated method stub
		super.doAction(e);
	}
}
