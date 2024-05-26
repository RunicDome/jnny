package nc.ui.pm.pmrlcontractalter.ace.action;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import nc.itf.uap.pf.busiflow.PfButtonClickContext;
import nc.ui.ct.util.CardEditorHelper;
import nc.ui.ct.util.SalePayTermUtil;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.pf.PfUtilClient;
import nc.ui.pubapp.uif2app.actions.AbstractReferenceAction;
import nc.ui.pubapp.uif2app.funcnode.trantype.TrantypeFuncUtils;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.ct.business.entity.BusinessSetVO;
import nc.vo.ct.util.CtTransBusitypes;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pf.change.ChangeVOAdjustContext;
import nc.vo.pm.rlcontractalter.AggRLContractalterHVO;
import nc.vo.pm.rlcontractschedule.AggRLContractschedule;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.ArrayUtils;

@SuppressWarnings("restriction")
public class AddFromSource4Z05Action extends AbstractReferenceAction {

	private ShowUpableBillForm editor;
	private AbstractAppModel model;

	public AddFromSource4Z05Action() {
		putValue("AcceleratorKey", KeyStroke.getKeyStroke(75, 8));
		putValue("ShortDescription", getBtnName());
	}

	public void doAction(ActionEvent e) throws Exception {
		PfUtilClient.childButtonClickedNew(createPfButtonClickContext());
		if (PfUtilClient.isCloseOK()) {
			AggRLContractalterHVO[] vos = (AggRLContractalterHVO[]) PfUtilClient
					.getRetVos();
			if (ArrayUtils.isEmpty(vos)) {
				return;
			}
			getTransferViewProcessor().processBillTransfer(vos);
			BillCardPanel cardPanel = getTransferViewProcessor().getBillForm()
					.getBillCardPanel();
			setPayTermTabVisible(cardPanel);
		}
	}

	public ShowUpableBillForm getEditor() {
		return this.editor;
	}

	public AbstractAppModel getModel() {
		return this.model;
	}

	public void setEditor(ShowUpableBillForm editor) {
		this.editor = editor;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
	}

	private PfButtonClickContext createPfButtonClickContext() {
		PfButtonClickContext context = new PfButtonClickContext();
		context.setParent(getModel().getContext().getEntranceUI());
		context.setSrcBillType(getSourceBillType());
		context.setPk_group(getModel().getContext().getPk_group());
		context.setUserId(getModel().getContext().getPk_loginUser());
		String vtrantype = TrantypeFuncUtils.getTrantype(getModel()
				.getContext());
		if (StringUtil.isEmptyWithTrim(vtrantype)) {
			context.setCurrBilltype("4Z05");
		} else {
			context.setCurrBilltype(vtrantype);
		}
		context.setUserObj(null);
		context.setSrcBillId(null);
		context.setBusiTypes(getBusitypes());
		context.setTransTypes(getTranstypes());
		context.setClassifyMode(1);
		return context;
	}

	private void setPayTermTabVisible(BillCardPanel cardPanel) {
		CardEditorHelper util = CardEditorHelper.getInstance(cardPanel);
		String ctrantypeid = util.getHeadStringValue("ctrantypeid");
		if (ctrantypeid == null) {
			cardPanel.setTabEnabled(1, "pk_ct_sale_payterm", false);
		} else {
			BusinessSetVO businessVO = CtTransBusitypes
					.getBusinessSetVO(ctrantypeid);
			SalePayTermUtil.setTabVisible(businessVO, cardPanel);
		}
	}

	public AggregatedValueObject[] batchAdjustAfterChange(
			AggregatedValueObject[] srcVOs, AggregatedValueObject[] destVOs,
			ChangeVOAdjustContext adjustContext) throws BusinessException {
		return destVOs;

	}

}
