package nc.ui.pm.PmFeeBalance.ace.action;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import nc.bs.trade.business.HYPubBO;
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
import nc.vo.ct.saledaily.entity.AggCtSaleVO;
import nc.vo.ct.saledaily.entity.CtSaleBVO;
import nc.vo.ct.util.CtTransBusitypes;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.SalesOrgVO;
import nc.vo.org.StockOrgVO;
import nc.vo.pf.change.ChangeVOAdjustContext;
import nc.vo.pm.rlcontractbalance.AggRLContractbalanceHVO;
import nc.vo.pm.rlcontractschedule.AggRLContractschedule;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.ArrayUtils;

@SuppressWarnings("restriction")
public class AddFromSourceNewAction extends AbstractReferenceAction {

	private ShowUpableBillForm editor;
	private AbstractAppModel model;

	public void AddFromSourceNewAction() {
		setSourceBillName("物资服务申请单");
		setBtnName("物资服务申请单");
		putValue("AcceleratorKey", KeyStroke.getKeyStroke(75, 8));
		putValue("ShortDescription", getBtnName());
	}

	public void doAction(ActionEvent e) throws Exception {
		PfUtilClient.childButtonClickedNew(createPfButtonClickContext());
		if (PfUtilClient.isCloseOK()) {
			AggRLContractbalanceHVO[] vos = (AggRLContractbalanceHVO[]) PfUtilClient
					.getRetVos();
			if (ArrayUtils.isEmpty(vos)) {
				return;
			}

			BillManageModel ctModel = (BillManageModel) getModel();
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
			context.setCurrBilltype("4Z01");
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
		for (AggregatedValueObject temp : destVOs) {
			HYPubBO hyPubBo = new HYPubBO();
			AggCtSaleVO aggVO = (AggCtSaleVO) temp;
			String pk_stock_org = aggVO.getParentVO().getPk_org();
			StockOrgVO stockOrgVO = (StockOrgVO) hyPubBo.queryByPrimaryKey(
					StockOrgVO.class, pk_stock_org);
			SalesOrgVO[] salesOrgVOs = (SalesOrgVO[]) hyPubBo.queryByCondition(
					StockOrgVO.class, "nvl(dr,0) = 0 and pk_org = '"
							+ stockOrgVO.getPk_org() + "'");
			String pk_org = salesOrgVOs[0].getPk_salesorg();
			String pk_org_v = salesOrgVOs[0].getPk_vid();
			aggVO.getParentVO().setPk_org(pk_org);

			CtSaleBVO[] bos = aggVO.getCtSaleBVO();
			for (CtSaleBVO vos : bos) {
				String pk_stock_org1 = vos.getPk_org();
				StockOrgVO stockOrgBVO = (StockOrgVO) hyPubBo
						.queryByPrimaryKey(StockOrgVO.class, pk_stock_org1);
				SalesOrgVO[] salesOrgBVOs = (SalesOrgVO[]) hyPubBo
						.queryByCondition(
								StockOrgVO.class,
								"nvl(dr,0) = 0 and pk_org = '"
										+ stockOrgBVO.getPk_org() + "'");
				String pk_org1 = salesOrgBVOs[0].getPk_salesorg();
				String pk_org_v1 = salesOrgBVOs[0].getPk_vid();
				aggVO.getParentVO().setPk_org(pk_org1);
			}
		}
		return destVOs;

	}

}
