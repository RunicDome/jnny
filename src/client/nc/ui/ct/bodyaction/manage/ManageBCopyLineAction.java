package nc.ui.ct.bodyaction.manage;

import nc.ui.ct.util.CardEditorHelper;
import nc.ui.pubapp.uif2app.actions.BodyCopyLineAction;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.scmpub.res.billtype.CTBillType;

//采购合同复制行
@SuppressWarnings("restriction")
public class ManageBCopyLineAction extends BodyCopyLineAction {
	private static final long serialVersionUID = 2466477079576316643L;

	@Override
	public void doAction() {
		// TODO Auto-generated method stub
		CardEditorHelper newutil = CardEditorHelper.getInstance(getCardPanel());
		if (newutil == null || newutil.getEditor() == null) {
			super.doAction();
		} else {
			String cbilltypecode = newutil.getHeadValue("cbilltypecode") + "";

			String pk_org = newutil.getHeadValue("pk_org") + "";

			String fstatusflag = newutil.getHeadValue("fstatusflag") + "";

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
			}else{
				super.doAction();
			}
		}
	}
	public ManageBCopyLineAction() {
	}

	public void setEnabled(boolean newValue) {
		ManageSateUtil util = new ManageSateUtil();
		super.setEnabled(util.manageEnabled(getModel()));
	}
}