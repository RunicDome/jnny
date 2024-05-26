package nc.ui.arap.viewhandler.cardafter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import nc.bs.logging.Logger;
import nc.ui.arap.view.ArapBillCardForm;
import nc.ui.arap.viewhandler.AbstractBillHandler;
import nc.ui.arap.viewhandler.ViewHandlerUIUtil;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.arap.basebill.IH2BMapping;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;

@SuppressWarnings({ "restriction", "unused", "unchecked", "rawtypes" })
public class HeadBillDateAfterEditHandler extends
		AbstractBillHandler<CardHeadTailAfterEditEvent> {
	private IH2BMapping htob;

	public HeadBillDateAfterEditHandler() {
	}

	public void handle() {
		if (getKey().equals("billdate")) {
			ArapBillCardForm editor = (ArapBillCardForm) getBillform();

			List<String> needRefreshHeadVersionField = new ArrayList();

			List<String> needRefreshHeadVersionFieldMappingNotVersionField = new ArrayList();

			for (int i = 0; i < NOTVERSIONFIELDS.length; i++) {
				BillItem headNotVersionBillItem = getHeadItem(NOTVERSIONFIELDS[i]);
				BillItem headVersionBillItem = getHeadItem(VERSIONFIELDS[i]);

				if ((null != headNotVersionBillItem)
						&& (null != headNotVersionBillItem.getValueObject())
						&& (!StringUtil.isEmpty((String) headNotVersionBillItem
								.getValueObject()))
						&& (null != headVersionBillItem)
						&& (null != headVersionBillItem.getValueObject())
						&& (!StringUtil.isEmpty((String) headVersionBillItem
								.getValueObject())) && (null != getValue())) {
					needRefreshHeadVersionField.add(VERSIONFIELDS[i]);
					needRefreshHeadVersionFieldMappingNotVersionField
							.add(NOTVERSIONFIELDS[i]);
				}
			}

			try {
				if ((null != needRefreshHeadVersionField)
						&& (needRefreshHeadVersionField.size() > 0)) {
					ViewHandlerUIUtil
							.setHeadMultiVersion(
									(String[]) needRefreshHeadVersionField
											.toArray(new String[0]),
									(String[]) needRefreshHeadVersionFieldMappingNotVersionField
											.toArray(new String[0]),
									getBillCardPanel(), editor);
				}
			} catch (BusinessException e) {
				Logger.error(e.getMessage());
			}

			int rowCount = getBillCardPanel().getRowCount();
			for (int row = 0; row < rowCount; row++) {
				List<String> needRefreshBodyVersionField = new ArrayList();

				List<String> needRefreshBodyVersionFieldMappingNotVersionField = new ArrayList();
				for (int i = 0; i < NOTVERSIONFIELDS.length; i++) {
					BillItem bodyNotVersionBillItem = getBodyItem(NOTVERSIONFIELDS[i]);
					BillItem bodyVersionBillItem = getBodyItem(VERSIONFIELDS[i]);
					if ((null != bodyNotVersionBillItem)
							&& (null != getBodyValue(NOTVERSIONFIELDS[i], row))
							&& (!StringUtil.isEmpty((String) getBodyValue(
									NOTVERSIONFIELDS[i], row)))
							&& (null != bodyVersionBillItem)
							&& (null != getBodyValue(VERSIONFIELDS[i], row))
							&& (!StringUtil.isEmpty((String) getBodyValue(
									VERSIONFIELDS[i], row)))
							&& (null != getValue())) {
						needRefreshBodyVersionField.add(VERSIONFIELDS[i]);
						needRefreshBodyVersionFieldMappingNotVersionField
								.add(NOTVERSIONFIELDS[i]);
					}
				}

				if ((null != needRefreshBodyVersionField)
						&& (needRefreshBodyVersionField.size() > 0)) {
					try {
						ViewHandlerUIUtil
								.setBodyMultiVersion(
										(String[]) needRefreshBodyVersionField
												.toArray(new String[0]),
										(String[]) needRefreshBodyVersionFieldMappingNotVersionField
												.toArray(new String[0]),
										getBillCardPanel(), editor, row);
					} catch (BusinessException e) {
						Logger.error(e.getMessage());
					}
				}
			}
		} else if (getKey().equals("def77")) {
			// 表头资金类别联动表体
			String def77 = (String) getHeadValue("def77");
			if (!StringUtils.isEmpty(def77)) {
				Object conforg;
				try {
					conforg = (Object) HYPubBO_Client.findColValue("bd_defdoc",
							"code", "nvl(dr,0) = 0 and pk_defdoc = '" + def77
									+ "'");
					if (conforg != null) {
						setEveryBodyValue(def77, "def20");
					}
				} catch (UifException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	protected List<String> getFilterKey() {
		return Arrays.asList(new String[] { "billdate", "def77" });
	}

	public IH2BMapping getHtob() {
		return this.htob;
	}

	public void setHtob(IH2BMapping htob) {
		this.htob = htob;
	}

	public static final String[] VERSIONFIELDS = { "pk_org_v", "pk_pcorg_v",
			"so_org_v", "pu_org_v", "sett_org_v", "pk_fiorg_v", "pk_deptid_v",
			"so_deptid_v", "pu_deptid_v" };

	public static final String[] NOTVERSIONFIELDS = { "pk_org", "pk_pcorg",
			"so_org", "pu_org", "sett_org", "pk_fiorg", "pk_deptid",
			"so_deptid", "pu_deptid" };
}
