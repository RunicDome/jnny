package nc.ui.pcm.contractbalance.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
import nc.itf.pmbd.pub.IMaterialaccPubService;
import nc.ui.pcm.contractbalance.util.ContrBalaReCalculateMnyUtil;
import nc.ui.pm.util.BudgetAlterInfoUtil;
import nc.ui.pm.util.ReCalculateMnyUtil;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.actions.pflow.SaveScriptAction;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.uif2.editor.IEditor;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pcm.contract.ContrHeadVO;
import nc.vo.pcm.contractbalance.ContractBalanceHeadVO;
import nc.vo.pm.util.UFDoubleUtils;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;

// 结算单保存
@SuppressWarnings({ "restriction", "unused" })
public class SaveAction extends SaveScriptAction {
	private static final long serialVersionUID = 6705958983817835662L;

	public SaveAction() {
	}

	protected void processReturnObj(Object[] pretObj) throws Exception {
		super.processReturnObj(pretObj);

		BudgetAlterInfoUtil.processCheckResulrObj(pretObj, getModel());
	}

	protected String getErrorMsg() {
		return NCLangRes4VoTransl.getNCLangRes().getStrByID("subcontract_0",
				"04820003-0230");
	}

	public void doAction(ActionEvent e) throws Exception {
		AbstractBill billVO = (AbstractBill) this.editor.getValue();

		ContractBalanceHeadVO headVO = (ContractBalanceHeadVO) billVO
				.getParentVO();
		if ((!UFDoubleUtils.isNullOrZero(headVO.getQual_mny()))
				&& (headVO.getQual_mny_deadline() == null)) {
			ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl
					.getNCLangRes()
					.getStrByID("subcontract_0", "04820003-0267"));
		}
		String[] mnyFieldsInBody = { "curr_bala_mny", "bala_mny_group",
				"bala_mny_gloab", "curr_dif_mny", "dif_mny_group",
				"dif_mny_gloab" };

		String[] mnyFieldsInHead = { "curr_bal_mny", "bal_mny_group",
				"bal_mny_global", "curr_bal_dif_mny", "bal_dif_mny_group",
				"bal_dif_mny_gloab" };

		ReCalculateMnyUtil.reCalculateMnyFieldsInHead(billVO, mnyFieldsInBody,
				mnyFieldsInHead);

		for (String itemString : mnyFieldsInHead) {
			((ShowUpableBillForm) this.editor).getBillCardPanel().setHeadItem(
					itemString, headVO.getAttributeValue(itemString));
		}

		ContrBalaReCalculateMnyUtil.reCalculateBalaPayMny(billVO);
		String[] payMnyFieldsInBody = { "curr_bal_pay_mny",
				"bal_pay_mny_group", "bal_pay_mny_gloab" };

		for (String itemString : payMnyFieldsInBody) {
			((ShowUpableBillForm) this.editor).getBillCardPanel().setHeadItem(
					itemString, headVO.getAttributeValue(itemString));
		}

		Boolean begin_flag = Boolean.valueOf(((ContractBalanceHeadVO) billVO
				.getParent()).getBegin_flag().booleanValue());
		if (begin_flag.booleanValue()) {
			String[] flagMnyFieldsInBody = { "verify_mny", "verify_group",
					"verify_global", "qual_verify_mny", "qual_verify_group",
					"qual_verify_gloab" };

			String[] flagMnyFieldsInHead = { "verify_mny", "verify_group",
					"verify_global", "qual_verify_mny", "qual_verify_group",
					"qual_verify_gloab" };

			ReCalculateMnyUtil.reCalculateMnyFieldsInHead(billVO,
					flagMnyFieldsInBody, flagMnyFieldsInHead);
			for (String itemString : flagMnyFieldsInHead) {
				((ShowUpableBillForm) this.editor).getBillCardPanel()
						.setHeadItem(itemString,
								headVO.getAttributeValue(itemString));
			}
		}
		// XBX合同结算单保存校验未完成材料结算单仅做提醒允许保存，提示“xxx未完成材料结算”
		String htpk = headVO.getPk_contr();// 清单发包合同主键
		String bill_code = "";// 合同编码
		String pk_org = (String) new HYPubBO().findColValue("sys_config",
				"config_value", "nvl(dr,0) = 0 and config_id = 53");
		// String pk_project = hvo.getPk_project();// 项目主键
		// String pk_supplier = ""; // 供应商
		if (pk_org.contains(headVO.getPk_org())) {
			if (htpk != null) {
				ContrHeadVO contVO = (ContrHeadVO) new HYPubBO()
						.queryByPrimaryKey(ContrHeadVO.class, htpk);// 清单发包合同VO
				bill_code = contVO.getBill_code();// 合同编码
				// pk_supplier = contVO.getPk_supplier();// 供应商
			}
			Boolean ifexist = NCLocator.getInstance()
					.lookup(IMaterialaccPubService.class)
					.querIfExistBillByPKContr(htpk);
			if (!ifexist) {
				// 返回 是=4 ，否=8
				MessageDialog.showHintDlg(null, "提示", "合同编码[" + bill_code
						+ "]未完成材料结算");
			}
		}
		super.doAction(e);
	}
}
