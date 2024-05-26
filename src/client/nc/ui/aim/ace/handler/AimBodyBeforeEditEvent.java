package nc.ui.aim.ace.handler;

import java.util.ArrayList;
import java.util.List;

import nc.bs.logging.Logger;
import nc.itf.am.pub.IStatusService;
import nc.ui.am.util.BillCardPanelUtils;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.vo.am.common.util.StringTools;
import nc.vo.am.proxy.AMProxy;
import nc.vo.pmpub.common.utils.BillCardPanelUtil;
import nc.vo.pub.BusinessException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class AimBodyBeforeEditEvent implements
		IAppEventHandler<CardBodyBeforeEditEvent> {

	private BillForm billForm;

	public AimBodyBeforeEditEvent() {
	}

	public void handleAppEvent(CardBodyBeforeEditEvent e) {
		if ("pk_equip".equals(e.getKey())) {
			BillCardPanel billCardPanel = this.billForm.getBillCardPanel();
			String pk_group = BillCardPanelUtil.getHeadStringValue(
					billCardPanel, "pk_group");
			String pk_org = BillCardPanelUtil.getHeadStringValue(billCardPanel,
					"pk_org");
			beforeEditEquipment(billCardPanel, pk_org, pk_group);
		}
		e.setReturnValue(Boolean.TRUE);
	}

	private void beforeEditEquipment(BillCardPanel billCardPanel,
			String pk_org, String pk_group) {
		UIRefPane equipRef = (UIRefPane) billCardPanel.getBodyItem("pk_equip")
				.getComponent();

		if (equipRef != null) {
			equipRef.getRefModel().clearCacheData();

			List<String> sale_type = new ArrayList();
			try {
				sale_type = ((IStatusService) AMProxy
						.lookup(IStatusService.class)).getStatusPKs(4);
			} catch (BusinessException e) {
				Logger.debug("查询设备状态出错");
			}
			if (equipRef.getRefModel() != null) {

				StringBuffer whereSql = new StringBuffer();

				StringBuilder inSql = new StringBuilder();

				StringTools.setArrayToStrWithQuotes(inSql, sale_type);

				whereSql.append(" (pam_equip.pk_usedorg = '").append(pk_org)
						.append("')");

				whereSql.append(" and pam_equip.pk_used_status not in (")
						.append(inSql.toString()).append(") ");

				whereSql.append(" and (pam_equip.borrow_flag ='~' or pam_equip.borrow_flag='N') ");

				whereSql.append(" and (pam_equip.assign_flag='~' or pam_equip.assign_flag='N')");

				// whereSql.append(" and pam_equip.pk_used_status  in ");
				//
				// String trainsi_type = BillCardPanelUtil.getHeadStringValue(
				// billCardPanel, "transtype");
				//
				// String billType = BillCardPanelUtil.getHeadStringValue(
				// billCardPanel, "billtype");
				//
				// whereSql.append(EquipStatusRefUtils.refInitTransiRule(billType,
				// trainsi_type, pk_group));

				BillCardPanelUtils.setOrgForRef(
						billCardPanel.getBodyItem("pk_equip"), pk_org);

				equipRef.getRefModel().setWherePart(whereSql.toString());
			}
		}
	}

	public BillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(BillForm billForm) {
		this.billForm = billForm;
	}
}