package nc.ui.arap.viewhandler.cardafter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import nc.ui.arap.viewhandler.AbstractBillHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
import nc.ui.trade.business.HYPubBO_Client;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

// 付款单表体金额编辑后事件
@SuppressWarnings("restriction")
public class BodyLocalMoneyAfterEditHandler extends
		AbstractBillHandler<CardBodyAfterEditEvent> {
	public BodyLocalMoneyAfterEditHandler() {
	}

	public void handle() {
		if (getKey().startsWith("money")) {
			setBillCardPanel(getBillCardPanel());
			setRow(getRow());

			String fieldcode = getKey();
			getBodyAmountValue(fieldcode);
			setHeadValue(getBodyAmountValue(fieldcode), "money");
			setHeadValue(getBodyAmountValue("local_money_de"), "local_money");
		} else if ((getKey().equals("local_money_de"))
				|| (getKey().equals("notax_de"))
				|| (getKey().equals("local_notax_de"))) {

			calculate();
		} else {
			// XBX新增监听
			calculateHead();
		}
	}

	public void calculate() {
		setHeadValue(getBodyAmountValue("money_de"), "money");

		setHeadValue(getBodyAmountValue("local_money_de"), "local_money");
		try {
			Object conforg = (Object) HYPubBO_Client.findColValue("bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
							+ "FROM BD_DEFDOCLIST WHERE CODE = 'HTSAVEORG')"
							+ " and code = '" + getHeadValue("pk_org") + "'");
			if (conforg != null) {
				// 本次付款金额
				UFDouble local_money_de = getBodyColValue("local_money_de");
				// 合同金额
				UFDouble def1 = getBodyColValue("def1");
				// 已付款比例
				UFDouble def6 = getBodyColValue("def6");
				// 审计金额
				UFDouble def2 = getBodyColValue("def2");
				// 本次付款比例
				UFDouble def7 = UFDouble.ZERO_DBL;
				if (def2.compareTo(UFDouble.ZERO_DBL) > 0) {
					def7 = local_money_de.div(def2).multiply(new UFDouble(100));
				} else {
					def7 = local_money_de.div(def1).multiply(new UFDouble(100));
				}
				setBodyValue(def7, "def7");// 本次付款比例 -- 表体
				setHeadValue(def7, "def37");// 本次付款比例 -- 表头
				// 累计付款比例 = 本次+已付款
				UFDouble def8 = def7.add(def6);
				setBodyValue(def8, "def8");// 累计付款比例 -- 表体
				setHeadValue(def8, "def39");// 累计付款比例 -- 表头
				UFDouble def32 = getHeadColValue("def32");// 表头折让金额
				setHeadValue(local_money_de.sub(def32), "def26");// 表头实付金额
				// 已付款金额
				UFDouble def3 = getBodyColValue("def3");
				// 累计付款金额
				UFDouble def40 = def3.add(local_money_de);
				setHeadValue(def40, "def40");// 表头实付金额
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// XBX新增监听
	public void calculateHead() {
		try {
			Object conforg = (Object) HYPubBO_Client.findColValue("bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
							+ "FROM BD_DEFDOCLIST WHERE CODE = 'HTSAVEORG')"
							+ " and code = '" + getHeadValue("pk_org") + "'");
			if (conforg != null) {
				// 已付款金额
				setHeadValue(getBodyColValue("def3"), "def71");
				// 审计金额
				setHeadValue(getBodyValue("def2"), "def41");
				// 本次付款金额
				UFDouble local_money_de = getBodyColValue("local_money_de");
				// 合同金额
				UFDouble def1 = getBodyColValue("def1");
				// 审计金额
				UFDouble def2 = getBodyColValue("def2");
				// 本次付款比例
				UFDouble def7 = UFDouble.ZERO_DBL;
				if (def2.compareTo(UFDouble.ZERO_DBL) > 0) {
					def7 = local_money_de.div(def2).multiply(new UFDouble(100));
				} else {
					def7 = local_money_de.div(def1).multiply(new UFDouble(100));
				}
				setBodyValue(def7, "def7");// 本次付款比例 -- 表体
				setHeadValue(def7, "def37");// 本次付款比例 -- 表头
				// 折让金额
				UFDouble def28 = getBodyColValue("def28");
				setHeadValue(def28, "def32");// 折让金额 -- 表头
				// 实付金额
				UFDouble def26 = local_money_de.sub(def28);
				setHeadValue(def26, "def26");// 实付金额 -- 表头
				// 已付款比例
				UFDouble def6 = getBodyColValue("def6");
				// 累计付款比例 = 本次+已付款
				UFDouble def8 = def7.add(def6);
				setBodyValue(def8, "def8");// 累计付款比例 -- 表体
				setHeadValue(def8, "def39");// 累计付款比例 -- 表头
				UFDouble def32 = getHeadColValue("def32");// 表头折让金额
				setHeadValue(local_money_de.sub(def32), "def26");// 表头实付金额
				setHeadValue(getBodyColValue("def2"), "def41");// 表头审计金额
				setHeadValue(getBodyColValue("def9"), "def72");// 表头已开发票金额
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * private final List<String> keys = Arrays.asList(new String[] { "money",
	 * "local_money_de", "notax_de", "local_notax_de" });
	 */

	private UFDouble getBodyColValue(String column) {
		if (getBodyValue(column) != null
				&& !"".equals(getBodyValue(column).toString())) {
			return new UFDouble(getBodyValue(column).toString());
		}
		return UFDouble.ZERO_DBL;
	}

	private UFDouble getHeadColValue(String column) {
		if (getHeadValue(column) != null
				&& !"".equals(getHeadValue(column).toString())) {
			return new UFDouble(getHeadValue(column).toString());
		}
		return UFDouble.ZERO_DBL;
	}

	protected Collection<String> getFilterKey() {
		/**
		 * def3 已付款金额 def28 本次付款比例 def7 本次付款比例 def2 审计金额
		 */
		List<String> addkeys = Arrays.asList(new String[] { "money",
				"local_money_de", "notax_de", "local_notax_de", "def3",
				"def28", "def7", "def2" });
		// return this.keys;
		return addkeys;
	}
}
