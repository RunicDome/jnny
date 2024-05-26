package nc.ui.ct.bodyaction.manage;

import nc.ui.ct.action.CtBodyAddLineAction;
import nc.ui.ct.model.CTModel;
import nc.ui.ct.util.CardEditorHelper;
import nc.vo.ct.uitl.ValueUtil;

@SuppressWarnings("restriction")
/**
 * 所属模块  ct
 * @author XBX
 */
public class ManageBAddLineAction extends CtBodyAddLineAction {
	private static final long serialVersionUID = 1L;
	final String NCHANGERAtE = "1.0000/1.0000";

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
			if(cbilltypecode == null && pk_org == null){
				super.setEnabled(false);
			}else{
				if("Z2".equals(cbilltypecode) && fstatusflag != null && !"1".equals(fstatusflag) && "0001A110000000000HYQ".equals(pk_org)){
					super.setEnabled(false);
				}else{
					super.setEnabled(util.manageEnabled(getModel()));
				}
			}
		}
	}

	private void setVchangeRate(CardEditorHelper util, int lineNum) {
		getClass();
		util.setBodyValue(lineNum, "vchangerate", "1.0000/1.0000");
		getClass();
		util.setBodyValue(lineNum, "vqtunitrate", "1.0000/1.0000");
	}

	protected void setDefaultValue(int index) {
		super.setDefaultValue(index);
		CardEditorHelper util = CardEditorHelper.getInstance(getCardPanel());
		if ((ValueUtil.equals(util.getSelectTableCode(), "pk_ct_sale_b"))
				|| (ValueUtil.equals(util.getSelectTableCode(), "pk_ct_pu_b"))
				|| (ValueUtil.equals(util.getSelectTableCode(), "pk_ct_ar_b"))
				|| (ValueUtil.equals(util.getSelectTableCode(), "pk_ct_ap_b"))) {

			String[] keys = { "pk_material", "pk_marbasclass", "castunitid" };

			util.setBodyEnabled(keys, true);
			CTModel model = (CTModel) getModel();

			util.setBodyValue(index, "pk_financeorg", model.getPk_financeorg());

			util.setBodyValue(index, "pk_financeorg_v",
					model.getPk_financeorg_v());

			setVchangeRate(util, index);
		}
	}
}
