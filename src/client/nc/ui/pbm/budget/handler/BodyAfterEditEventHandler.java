package nc.ui.pbm.budget.handler;

import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.pbm.budget.utils.BudgetCBSUtil;
import nc.ui.pm.handler.PMDefaultCardBodyafterHandler;
import nc.ui.pm.util.BillCardPanelUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.vo.pbm.budget.BudgetFactorBodyVO;
import nc.vo.pbm.budget.BudgetTableCodeConst;
import nc.vo.pm.util.ArrayUtil;
import nc.vo.pm.util.StringUtil;

// 项目预算 表体编辑事件
@SuppressWarnings({ "restriction", "unused" })
public class BodyAfterEditEventHandler extends PMDefaultCardBodyafterHandler {
	protected ShowUpableBillForm cardForm;

	public BodyAfterEditEventHandler() {
	}

	public void handleAppEvent(CardBodyAfterEditEvent e) {
		super.handleAppEvent(e);
		String key = e.getKey();
		BillCardPanel cardPanel = e.getBillCardPanel();
		String tabCode = e.getBillCardPanel().getCurrentBodyTableCode();
		if ((BudgetTableCodeConst.BUDGETCBS.equals(tabCode))
				&& ("pk_cbsnode".equals(key))) {
			cbsAfterEdit(cardPanel, (String) e.getOldValue(), e.getRow());
		}
		if ("pk_factor".equals(key)) {
			factorAfterEdit(cardPanel, e.getRow());
		} else if ("bdef19".equals(key)) {// bdef19 == 含税收入预算
			BudgetFactorBodyVO[] itemvos = (BudgetFactorBodyVO[]) cardPanel
					.getBillModel().getBodyValueVOs(
							BudgetFactorBodyVO.class.getName());
			double totalhssrys = 0;
			for(BudgetFactorBodyVO item:itemvos){
				double hssrys = Double.parseDouble((item.getBdef19() == null ? "0.0" : 
					item.getBdef19().toString()));// 含税收入预算
				totalhssrys += hssrys;
			}
			BillCardPanelUtil.setHeadValue(cardPanel, "hdef31", totalhssrys);
		}

	}

	private void factorAfterEdit(BillCardPanel cardPanel, int curRow) {
		UIRefPane refPanel = (UIRefPane) cardPanel.getBodyItem("pk_factor")
				.getComponent();
		AbstractRefModel refModel = refPanel.getRefModel();
		String[] pk_facotrs = refModel.getPkValues();
		if (ArrayUtil.isEmpty(pk_facotrs)) {
			return;
		}
		String pk_cbsNode = (String) BillCardPanelUtil.getBodyValue(cardPanel,
				BudgetTableCodeConst.BUDGETFACTOR, "pk_cbsnode", curRow);
		for (int i = 0; i < pk_facotrs.length; i++) {
			BillCardPanelUtil.setBodyValue(cardPanel, i + curRow, "pk_cbsnode",
					pk_cbsNode, BudgetTableCodeConst.BUDGETFACTOR);
			cardPanel.getBillModel().loadLoadRelationItemValue(i + curRow);
		}
		Integer incomeexpenses = (Integer) BillCardPanelUtil.getBodyValue(
				cardPanel, BudgetTableCodeConst.BUDGETFACTOR,
				"pk_factor.pk_factor.incomeexpenses", curRow);
		if (incomeexpenses != null) {
			if (incomeexpenses.intValue() == 1) {
				BillCardPanelUtil.setBodyValue(cardPanel, curRow, "budget_mny",
						Integer.valueOf(0));
				cardPanel.getBillModel().setCellEditable(curRow, "in_budget",
						true);
				cardPanel.getBillModel().setCellEditable(curRow, "budget_mny",
						true);
			}
			if (incomeexpenses.intValue() == 2) {
				BillCardPanelUtil.setBodyValue(cardPanel, curRow, "in_budget",
						Integer.valueOf(0));
				cardPanel.getBillModel().setCellEditable(curRow, "in_budget",
						true);
				cardPanel.getBillModel().setCellEditable(curRow, "budget_mny",
						true);
			}
		} else {
			BillCardPanelUtil.setBodyValue(cardPanel, curRow, "budget_mny",
					Integer.valueOf(0));
			BillCardPanelUtil.setBodyValue(cardPanel, curRow, "in_budget",
					Integer.valueOf(0));
			cardPanel.getBillModel().setCellEditable(curRow, "in_budget", true);
			cardPanel.getBillModel()
					.setCellEditable(curRow, "budget_mny", true);
		}
	}

	private void cbsAfterEdit(BillCardPanel cardPanel, String old_pk_cbsNode,
			int row) {
		if (StringUtil.isNotEmpty(old_pk_cbsNode)) {
			BudgetCBSUtil.delCBSNodesAfter(cardPanel,
					new String[] { old_pk_cbsNode });
		}
		BillItem pk_cbsNodeItem = cardPanel.getBodyItem(
				BudgetTableCodeConst.BUDGETCBS, "pk_cbsnode");
		AbstractRefModel refModel = ((UIRefPane) pk_cbsNodeItem.getComponent())
				.getRefModel();
		String[] pk_cbsNodes = refModel.getPkValues();
		if (ArrayUtil.isEmpty(pk_cbsNodes)) {
			return;
		}
		BudgetCBSUtil.addCBSNodeAfter(cardPanel, pk_cbsNodes);
	}

	public ShowUpableBillForm getCardForm() {
		return this.cardForm;
	}

	public void setCardForm(ShowUpableBillForm cardForm) {
		this.cardForm = cardForm;
	}
}
