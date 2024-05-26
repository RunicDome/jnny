package nc.ui.pbm.budget.action;

import java.awt.event.ActionEvent;
import nc.itf.pbm.budget.pvt.IBudgetCreate;
import nc.ui.pbm.budget.utils.BudgetUIUtil;
import nc.ui.pm.action.ActionInitializer;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.pubapp.uif2app.AppUiState;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pbm.budget.BudgetBillVO;
import nc.vo.pbm.budget.BudgetFactorBodyVO;
import nc.vo.pbm.budget.BudgetHeadVO;
import nc.vo.pbm.budget.BudgetTableCodeConst;
import nc.vo.pm.proxy.PMProxy;
import nc.vo.pm.util.StringUtil;
import nc.vo.pm.util.UFDoubleUtils;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.uif2.LoginContext;

//项目预算--获取资源 按钮
@SuppressWarnings({ "restriction", "unused" })
public class ResourceAction extends NCAction {
	private static final long serialVersionUID = 1L;
	protected ShowUpableBillForm editor;
	protected BillManageModel model;

	public ResourceAction() {
		ActionInitializer.initializeAction(this, "Resource");
	}

	public void doAction(ActionEvent e) throws Exception {
		BudgetBillVO billVO = (BudgetBillVO) this.editor.getValue();
		if (billVO == null) {
			return;
		}

		String pk_project = billVO.getParentVO().getPk_project();
		if (StringUtil.isEmpty(pk_project)) {
			ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl
					.getNCLangRes().getStrByID("projectbudget_0",
							"04815005-0229"));
		}

		BudgetUIUtil.canObtainResourceORExcel(billVO);

		BudgetFactorBodyVO[] factorBodys = billVO.getBudgetFactorBodyVO();
		for (int i = 0; i < factorBodys.length; i++) {
			if (!UFDoubleUtils.isNullOrZero(factorBodys[i].getBudget_mny())) {
				if (8 != MessageDialog.showYesNoDlg(
						getModel().getContext().getEntranceUI(),
						NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"projectbudget_0", "04815005-0007"),
						NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"projectbudget_0", "04815005-0008"), 8)) {
					break;
				}

				return;
			}
		}

		billVO = ((IBudgetCreate) PMProxy.lookup(IBudgetCreate.class))
				.budgetResource(billVO);

		getEditor().setValue(billVO);
		getEditor()
				.getBillCardPanel()
				.getBillModel(BudgetTableCodeConst.BUDGETFACTOR)
				.loadEditRelationItemValue(0,
						billVO.getBudgetFactorBodyVO().length - 1, "pk_cbsnode");
	}

	public ShowUpableBillForm getEditor() {
		return this.editor;
	}

	public void setEditor(ShowUpableBillForm editor) {
		this.editor = editor;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public BillManageModel getModel() {
		return this.model;
	}

	protected boolean isActionEnable() {
		if (getModel().getAppUiState().getUiState() == UIState.ADD) {
			return true;
		}
		if (this.model.getSelectedData() == null) {
			return false;
		}
		if (getModel().getAppUiState().getUiState() == UIState.EDIT) {
			boolean isFree = BudgetUIUtil.isCurrBillSatus(getModel()
					.getSelectedData(), -1);

			boolean isNopass = BudgetUIUtil.isCurrBillSatus(getModel()
					.getSelectedData(), 0);

			return (isFree) || (isNopass);
		}
		return false;
	}
}
