package nc.ui.ct.bodyaction.manage;

import javax.swing.ListSelectionModel;
import nc.ui.ct.model.CTModel;
import nc.ui.ct.util.CardEditorHelper;
import nc.ui.pub.beans.UITable;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pub.bill.BillScrollPane.BillTable;
import nc.ui.pubapp.uif2app.actions.BodyInsertLineAction;
import nc.ui.pubapp.uif2app.actions.IBatchBodyLine;
import nc.ui.pubapp.uif2app.event.card.BodyRowEditType;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterRowEditEvent;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.ct.uitl.ValueUtil;

@SuppressWarnings({ "unused", "restriction" })
public class ManageBInsertLineAction extends BodyInsertLineAction implements
		IBatchBodyLine {
	private static final long serialVersionUID = 5031823575804979154L;
	private int nSelectedRowIndex;
	final String NCHANGERAtE = "1.0000/1.0000";

	public void batchBodyLineOperate(int rowLen) {
		for (int i = rowLen; i > 0; i--) {
			nSelectedRowIndex = getCardPanel().getBillTable().getSelectedRow();

			boolean doOperateFlag = getCardPanel().doLineAction(
					getCardPanel().getCurrentBodyTableCode(), 1);

			if (doOperateFlag) {
				onDataBasicInsert();
				int[] rows = { getCardPanel().getBodyPanel().getTable()
						.getSelectedRow() };

				getModel().fireEvent(
						new CardBodyAfterRowEditEvent(getCardPanel(),
								BodyRowEditType.INSERTLINE, rows));
			}
		}
	}

	public void setEnabled(boolean newValue) {
		ManageSateUtil util = new ManageSateUtil();
		// 4、采购合同表体行 不允许增加行。变更时允许新增。组织：热力公司
		CardEditorHelper utils = CardEditorHelper.getInstance(getCardPanel());
		if(utils.getEditor() == null){
			super.setEnabled(util.manageEnabled(getModel()));
		}else{
			String cbilltypecode = utils.getHeadStringValue("cbilltypecode");
			String pk_org = utils.getHeadStringValue("pk_org");
			// System.out.println("cbilltypecode==="+cbilltypecode);
			String fstatusflag = utils.getHeadStringValue("fstatusflag");
			if("Z2".equals(cbilltypecode) && fstatusflag != null && !"1".equals(fstatusflag) && "0001A110000000000HYQ".equals(pk_org)){
				super.setEnabled(false);
			}else{
				super.setEnabled(util.manageEnabled(getModel()));
			}
		}
	}

	private void setVchangeRate(CardEditorHelper util, int lineNum) {
		getClass();
		util.setBodyValue(lineNum, "vchangerate", "1.0000/1.0000");
		getClass();
		util.setBodyValue(lineNum, "vqtunitrate", "1.0000/1.0000");
	}

	protected void afterLineInsert(int index) {
		basicLineInsert(index);
		setQueryDefaultValue(index);
		getCardPanel().getBillModel().loadLoadRelationItemValue(index);
	}

	protected void basicLineInsert(int index) {
		super.afterLineInsert(index);
		setDefaultValue(index);
	}

	protected void onDataBasicInsert() {
		int seletctIndex = nSelectedRowIndex;
		if (seletctIndex == -1) {
			return;
		}

		getCardPanel().stopEditing();
		basicLineInsert(seletctIndex);
		execLoadFormula(seletctIndex);

		getCardPanel().getBodyPanel().getTable().getSelectionModel()
				.setSelectionInterval(seletctIndex, seletctIndex);

		((BillScrollPane) getCardPanel().getBodySelectedScrollPane())
				.getTable().changeSelection(seletctIndex, 0, false, false);

		((BillScrollPane) getCardPanel().getBodySelectedScrollPane())
				.getTable().requestFocus();
	}

	protected void setDefaultValue(int index) {
		CardEditorHelper util = CardEditorHelper.getInstance(getCardPanel());

		Object pk_org = util.getHeadValue("pk_org");
		Object pk_org_v = util.getHeadValue("pk_org_v");

		Object pk_group = util.getHeadValue("pk_group");

		if (!ValueUtil.isEmpty(pk_org)) {
			util.setBodyValue(index, "pk_org", pk_org);
		}
		if (!ValueUtil.isEmpty(pk_org_v)) {
			util.setBodyValue(index, "pk_org_v", pk_org_v);
		}
		if (!ValueUtil.isEmpty(pk_group)) {
			util.setBodyValue(index, "pk_group", pk_group);
		}

		String[] keys = { "pk_material", "pk_marbasclass", "castunitid" };

		util.setBodyEnabled(keys, true);
		CTModel model = (CTModel) getModel();
		util.setBodyValue(index, "pk_financeorg", model.getPk_financeorg());

		util.setBodyValue(index, "pk_financeorg_v", model.getPk_financeorg_v());

		setVchangeRate(util, index);
	}

	protected void setQueryDefaultValue(int index) {
	}
}
