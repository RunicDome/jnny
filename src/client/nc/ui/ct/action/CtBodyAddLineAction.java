package nc.ui.ct.action;

import nc.ui.ct.util.CardEditorHelper;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pubapp.uif2app.actions.BodyAddLineAction;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.ct.uitl.ValueUtil;
import nc.vo.scmpub.res.billtype.CTBillType;

// 采购合同表体增行
@SuppressWarnings({ "unused", "restriction" })
public class CtBodyAddLineAction extends BodyAddLineAction {
	private static final long serialVersionUID = 1L;

	@Override
	public void doAction() {
		// TODO Auto-generated method stub
		CardEditorHelper util = CardEditorHelper.getInstance(getCardPanel());
		if (util == null || util.getEditor() == null) {
			super.doAction();
		} else {
			String cbilltypecode = util.getHeadValue("cbilltypecode") + "";

			String pk_org = util.getHeadValue("pk_org") + "";

			String fstatusflag = util.getHeadValue("fstatusflag") + "";

			if (CTBillType.PurDaily.getCode().equals(cbilltypecode)
					&& !"1".equals(fstatusflag)) {
				try {
					Object conforg = (Object) HYPubBO_Client
							.findColValue(
									"bd_defdoc",
									"code",
									"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
											+ "FROM BD_DEFDOCLIST WHERE CODE = 'HTSAVEORG')"
											+ " and code = '" + pk_org + "'");
					if (conforg == null) {
						super.doAction();
					}
				} catch (UifException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				super.doAction();
			}
		}
	}

	public CtBodyAddLineAction() {
	}

	public void batchBodyLineOperate(int rowLen) {
		super.getCardPanel().getBodyPanel().addLine(rowLen);
		getCardPanel().stopEditing();

		int row = 0;
		int[] rows = new int[rowLen];
		for (int index = rowLen; index > 0; index--) {
			row = getCardPanel().getBillModel().getRowCount() - index;
			rows[(rowLen - index)] = row;
			basicLineInsert(row);
		}

		batchExecLoadFormula();
		batchFireEvent(rows);
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
	}

	protected void setQueryDefaultValue(int index) {
	}
}