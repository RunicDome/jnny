package nc.ui.ct.saledaily.action;

import java.awt.event.ActionEvent;
import java.util.List;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.itf.scmpub.reference.uap.group.SysInitGroupQuery;
import nc.ui.ct.model.CTModel;
import nc.ui.ct.saledaily.editor.org.SaleOrgChangedEventHandler;
import nc.ui.ct.util.CardEditorHelper;
import nc.ui.ct.util.SalePayTermUtil;
import nc.ui.ct.view.CtUIState;
import nc.ui.pcm.utils.GetDao;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillTabbedPane;
import nc.ui.pubapp.uif2app.actions.AddAction;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.scmpub.action.SCMActionInitializer;
import nc.vo.ct.business.entity.BusinessSetVO;
import nc.vo.ct.util.CtTransBusitypes;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

// 销售合同自制
@SuppressWarnings("unused")
public class SaleAddAction extends AddAction {
	private static final long serialVersionUID = -2245052970909690217L;
	private ShowUpableBillForm cardForm;

	public SaleAddAction() {
		SCMActionInitializer.initializeAction(this, "Manual");
	}

	public void doAction(ActionEvent e) throws Exception {
		if (!SysInitGroupQuery.isSOEnabled()) {
			ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl
					.getNCLangRes().getStrByID("4020003_0", "04020003-0323"));
		}

		// 角色下的用户不允许自制
		String fg = "0";
		String user = InvocationInfoProxy.getInstance().getUserId();// 当前用户主键
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		String querySql = "select c.cuserid, a.pk_role, a.role_name,c.user_name from sm_role a left join sm_user_role b  on a.pk_role=b.pk_role "
				+ " left join sm_user c on c.cuserid=b.cuserid  where a.role_code = 'XSHT001'";
		List<Object[]> resultList = getDao.query(querySql);
		System.out.println("sql===" + querySql);
		if (resultList.size() > 0) {
			for (int i = 0; i < resultList.size(); i++) {
				Object[] item = resultList.get(i);
				if (user.equals(item[0])) {
					fg = "1";
				}
			}
		}
		System.out.println("当前登录用户主键：" + user);
		if ("1".equals(fg)) {
			ExceptionUtils.wrappBusinessException("不允许自制！");
		} else {
			super.doAction(e);

			CTModel ctModel = (CTModel) getModel();
			ctModel.setCtUIState(CtUIState.CTADD);
			SaleOrgChangedEventHandler handler = new SaleOrgChangedEventHandler();
			handler.process(this.cardForm);
			BillCardPanel cardPanel = getCardForm().getBillCardPanel();

			setPayTermTabVisible(cardPanel);
			getCardForm().getBillCardPanel().getBodyTabbedPane()
					.setSelectedIndex(0);
		}
	}

	public ShowUpableBillForm getCardForm() {
		return this.cardForm;
	}

	public void setCardForm(ShowUpableBillForm cardForm) {
		this.cardForm = cardForm;
	}

	private void setPayTermTabVisible(BillCardPanel cardPanel) {
		CardEditorHelper util = CardEditorHelper.getInstance(cardPanel);
		String ctrantypeid = util.getHeadStringValue("ctrantypeid");
		if (ctrantypeid == null) {
			cardPanel.setTabEnabled(1, "pk_ct_sale_payterm", false);

			return;
		}
		BusinessSetVO businessVO = CtTransBusitypes
				.getBusinessSetVO(ctrantypeid);
		SalePayTermUtil.setTabVisible(businessVO, cardPanel);
	}
}