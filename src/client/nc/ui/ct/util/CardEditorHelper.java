package nc.ui.ct.util;

import java.util.ArrayList;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillTabbedPane;
import nc.vo.ct.uitl.ArrayTool;
import nc.vo.ct.uitl.ValueUtil;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.data.ValueUtils;
import nc.vo.scmpub.res.billtype.CTBillType;

@SuppressWarnings({ "unused", "restriction", "unchecked", "rawtypes" })
public class CardEditorHelper {
	public CardEditorHelper() {
	}

	private static CardEditorHelper instance = new CardEditorHelper();

	public static CardEditorHelper getInstance(BillCardPanel editor) {
		instance.setEditor(editor);
		return instance;
	}

	public void addBodyLine() {
		this.editor.addLine();
	}

	public void addBodyLine(String tableCode) {
		this.editor.addLine(tableCode);
	}

	public void clearBodyTabValue(String tablecode) {
		this.editor.getBillModel(tablecode).clearBodyData();
	}

	public void clearBodyValue(int row, String key) {
		if (ValueUtil.isEmpty(key)) {
			return;
		}
		setBodyValue(row, key, null);
	}

	public void clearBodyValue(int row, String[] keys) {
		if (ArrayTool.isEmpty(keys)) {
			return;
		}
		for (String key : keys) {
			setBodyValue(row, key, null);
		}
	}

	public void clearBodyValue(int[] rows, String[] keys) {
		if ((ArrayTool.isEmpty(rows)) || (ArrayTool.isEmpty(keys))) {
			return;
		}
		for (int row : rows) {
			clearBodyValue(row, keys);
		}
	}

	public void clearBodyValue(String key) {
		int row = 0;
		int rows = getItemCount(this.editor.getCurrentBodyTableCode());
		for (; row < rows; row++) {
			setBodyValue(row, key, null);
		}
	}

	public void clearBodyValue(String[] keys) {
		int row = 0;
		int rows = getItemCount(this.editor.getCurrentBodyTableCode());
		for (; row < rows; row++) {
			clearBodyValue(row, keys);
		}
	}

	public void clearBodyValue(String[] keys, String tableCode) {
		int rows = getItemCount(tableCode);
		for (String key : keys) {
			for (int i = 0; i < rows; i++) {
				this.editor.setBodyValueAt(null, i, key, tableCode);
			}
		}
	}

	public void clearHeadValue(String key) {
		if (ValueUtil.isEmpty(key)) {
			return;
		}
		setHeadValue(key, null);
	}

	public void clearHeadValue(String[] keys) {
		if (ArrayTool.isEmpty(keys)) {
			return;
		}
		for (String key : keys) {
			clearHeadValue(key);
		}
	}

	public String getBaseTableCode() {
		Object billtype = this.editor.getHeadItem("cbilltypecode")
				.getValueObject();

		if (CTBillType.PurDaily.getCode().equals(billtype)) {
			return "pk_ct_pu_b";
		}
		if (CTBillType.SaleDaily.getCode().equals(billtype)) {
			return "pk_ct_sale_b";
		}
		if (CTBillType.OtherPur.getCode().equals(billtype)) {
			return "pk_ct_ap_b";
		}
		if (CTBillType.OtherSale.getCode().equals(billtype)) {
			return "pk_ct_ar_b";
		}
		return null;
	}

	public BillItem getBodyItem(String tableCode, String itemKey) {
		return this.editor.getBodyItem(tableCode, itemKey);
	}

	public String getBodyStringValue(int row, String itemKey) {
		String itemValue = ValueUtils.getString(this.editor.getBodyValueAt(row,
				itemKey));

		return itemValue;
	}

	public UFDate getBodyUFDateValue(int row, String itemKey) {
		UFDate itemValue = ValueUtils.getUFDate(this.editor.getBodyValueAt(row,
				itemKey));

		return itemValue;
	}

	public UFDouble getBodyUFDoubleValue(int row, String itemKey) {
		UFDouble itemValue = ValueUtils.getUFDouble(this.editor.getBodyValueAt(
				row, itemKey));

		return itemValue;
	}

	public Object getBodyValue(int row, String itemKey) {
		return this.editor.getBodyValueAt(row, itemKey);
	}

	public BillCardPanel getEditor() {
		return this.editor;
	}

	public String[] getHeadItemKeys(boolean isNull) {
		BillItem[] headItems = getEditor().getHeadShowItems();
		ArrayList<String> filterItems = new ArrayList();
		for (BillItem item : headItems) {
			if (item.isNull() == isNull) {
				filterItems.add(item.getKey());
			}
		}
		if (filterItems.size() == 0) {
			return null;
		}
		return (String[]) filterItems.toArray(new String[filterItems.size()]);
	}

	public BillItem[] getHeadItems(boolean isNull) {
		BillItem[] headItems = getEditor().getHeadShowItems();
		ArrayList<BillItem> filterItems = new ArrayList();
		for (BillItem item : headItems) {
			if (item.isNull() == isNull) {
				filterItems.add(item);
			}
		}
		if (filterItems.size() == 0) {
			return null;
		}
		return (BillItem[]) filterItems
				.toArray(new BillItem[filterItems.size()]);
	}

	public String[] getHeadKeys() {
		int length = this.editor.getHeadItems().length;
		String[] keys = new String[length];
		for (int i = 0; i < length; i++) {
			keys[i] = this.editor.getHeadItems()[i].getKey();
		}
		return keys;
	}

	public String getHeadStringValue(String itemKey) {
		String itemValue = ValueUtils.getString(getHeadValue(itemKey));
		return itemValue;
	}

	public BillItem getHeadTailItem(String itemKey) {
		return this.editor.getHeadTailItem(itemKey);
	}

	public UFDate getHeadUFDateValue(String itemKey) {
		UFDate itemValue = ValueUtils.getUFDate(getHeadValue(itemKey));
		return itemValue;
	}

	public UFDouble getHeadUFDoubleValue(String itemKey) {
		UFDouble itemValue = ValueUtils.getUFDouble(getHeadValue(itemKey));
		return itemValue;
	}

	public Object getHeadValue(String itemKey) {
		BillItem item = this.editor.getHeadTailItem(itemKey);
		if (null == item) {
			return null;
		}
		return this.editor.getHeadTailItem(itemKey).getValueObject();
	}

	private BillCardPanel editor;

	public boolean getIsNull(String table, String key) {
		return this.editor.getBodyItem(table, key).isNull();
	}

	public boolean getIsShow(String table, String key) {
		return this.editor.getBodyItem(table, key).isShow();
	}

	public int getItemCount() {
		return this.editor.getRowCount();
	}

	public int getItemCount(String tableCode) {
		return this.editor.getBillData().getBillModel(tableCode).getRowCount();
	}

	public String[] getNotNullHeadItemKeys() {
		return getHeadItemKeys(true);
	}

	public String[] getNullHeadItemKeys() {
		return getHeadItemKeys(false);
	}

	public int[] getRows(int rows) {
		int[] row = new int[rows];
		for (int i = 0; i < rows; i++) {
			row[i] = i;
		}
		return row;
	}

	public int getrowState(int i) {
		return this.editor.getBillModel().getRowState(i);
	}

	public String getSelectTableCode() {
		return this.editor.getBodyTabbedPane().getSelectedTableCode();
	}

	public void setBodyEnabled(String key, boolean isboolean) {
		if (ValueUtil.isEmpty(this.editor.getBodyItem(key))) {
			return;
		}
		this.editor.getBodyItem(key).setEnabled(isboolean);
	}

	public void setBodyEnabled(String[] keys, boolean isboolean) {
		if (ArrayTool.isEmpty(keys)) {
			return;
		}
		for (String key : keys) {
			setBodyEnabled(key, isboolean);
		}
	}

	public void setbodyItemsEnabled(String tableCode, boolean isboolean) {
		this.editor.getBillModel(tableCode).setEnabled(isboolean);
	}

	public void setBodyValue(int row, String itemKey, Object value) {
		setBodyValue(row, itemKey, value, this.editor.getCurrentBodyTableCode());
	}

	public void setBodyValue(int row, String itemKey, Object value,
			String tableCode) {
		this.editor.setBodyValueAt(value, row, itemKey, tableCode);
	}

	public void setBodyValue(String itemKey, Object value) {
		int row = 0;
		for (int rows = getItemCount(); row < rows; row++) {
			setBodyValue(row, itemKey, value,
					this.editor.getCurrentBodyTableCode());
		}
	}

	public void setBodyValue(String itemKey, Object value, String tableCode) {
		int row = 0;
		for (int rows = getItemCount(tableCode); row < rows; row++) {
			setBodyValue(row, itemKey, value, tableCode);
		}
	}

	public void setEditor(BillCardPanel editor) {
		this.editor = editor;
	}

	public void setHeadEdit(String key, boolean isboolean) {
		this.editor.getHeadItem(key).setEdit(isboolean);
	}

	public void setHeadEnabled(String key, boolean isboolean) {

		if (null != this.editor.getHeadItem(key)) {
			this.editor.getHeadItem(key).setEnabled(isboolean);
		}
	}

	public void setHeadEnabled(String[] keys, boolean isboolean) {
		if (ArrayTool.isEmpty(keys)) {
			return;
		}
		for (String key : keys) {
			setHeadEnabled(key, isboolean);
		}
	}

	public void setHeadItemsEnabled(boolean isboolean) {
		String[] keys = getHeadKeys();
		for (String key : keys) {
			setHeadEnabled(key, isboolean);
		}
	}

	public void setHeadValue(String itemKey, Object value) {
		this.editor.getHeadTailItem(itemKey).setValue(value);
	}

	public void setRowState(int i, int modification) {
		this.editor.getBillModel().setRowState(i, modification);
	}
}
