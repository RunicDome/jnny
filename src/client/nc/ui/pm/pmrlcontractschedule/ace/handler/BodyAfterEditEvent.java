package nc.ui.pm.pmrlcontractschedule.ace.handler;

import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
import nc.vo.pub.lang.UFDouble;

//表体编辑后事件类
@SuppressWarnings("restriction")
public class BodyAfterEditEvent implements
		IAppEventHandler<CardBodyAfterEditEvent> {

	// 表体单价*数量=无税金额
	@Override
	public void handleAppEvent(CardBodyAfterEditEvent e) {

		BillCardPanel panel = e.getBillCardPanel();
		String key = e.getKey();
		// 表体数量变化时，判断无税单价不空时，计算无税金额(无税单价*数量)
		if ("num".equals(key)) {
			UFDouble notaxmoney = UFDouble.ZERO_DBL;
			UFDouble notaxprice = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"notaxprice");// 无税单价
			UFDouble num = (UFDouble) panel.getBodyValueAt(e.getRow(), "num");// 数量
			if(num == null){
				num = new UFDouble(0);
				panel.setBodyValueAt(num, e.getRow(), "num");// 数量
			}
			UFDouble taxrate = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"taxrate");// 税率
			UFDouble taxamount = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"taxamount");// 税额

			if (null != notaxprice) {
				notaxmoney = notaxprice.multiply(num);
				panel.setBodyValueAt(notaxmoney, e.getRow(), "notaxmoney");
			}
			UFDouble notaxmoney1 = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"notaxmoney");
			UFDouble currentpaymentamount = UFDouble.ZERO_DBL;

			if (null != taxrate) {
				taxamount = notaxmoney1.multiply((taxrate.div(100)).add(1))
						.sub(notaxmoney1);
				panel.setBodyValueAt(taxamount, e.getRow(), "taxamount");

				currentpaymentamount = taxamount.add(notaxmoney1);
				panel.setBodyValueAt(currentpaymentamount, e.getRow(),
						"currentpaymentamount");
			}
			UFDouble hsdj = notaxprice.multiply((taxrate.div(100)
					.add(new UFDouble(1))));
			panel.setBodyValueAt(hsdj, e.getRow(), "taxprice");// 含税单价
		}
		// 表体无税单价变化时，判断数量不空时，计算无税金额(无税单价*数量)
		if ("notaxprice".equals(key)) {
			UFDouble notaxmoney = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"notaxmoney");
			UFDouble notaxprice = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"notaxprice");
			if(notaxprice == null){
				notaxprice = new UFDouble(0);
				panel.setBodyValueAt(notaxprice, e.getRow(), "notaxprice");// 
			}
			UFDouble num = (UFDouble) panel.getBodyValueAt(e.getRow(), "num");
			UFDouble taxrate = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"taxrate");
			UFDouble taxamount = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"taxamount");
			if (null != num) {
				notaxmoney = notaxprice.multiply(num);
				panel.setBodyValueAt(notaxmoney, e.getRow(), "notaxmoney");
			}
			UFDouble notaxmoney1 = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"notaxmoney");
			UFDouble currentpaymentamount = UFDouble.ZERO_DBL;
			if (null != taxrate) {
				taxamount = notaxmoney.multiply((taxrate.div(100)).add(1)).sub(
						notaxmoney);
				panel.setBodyValueAt(taxamount, e.getRow(), "taxamount");

				currentpaymentamount = taxamount.add(notaxmoney1);
				panel.setBodyValueAt(currentpaymentamount, e.getRow(),
						"currentpaymentamount");
				UFDouble hsdj = notaxprice.multiply((taxrate.div(100)
						.add(new UFDouble(1))));
				panel.setBodyValueAt(hsdj, e.getRow(), "taxprice");// 含税单价
			}
		}
		// 税率变化时，判断无税金额不为空时，根据currentpaymentamount->notaxmoney*(1+taxrate/100)公式计算价税合计
		if ("taxrate".equals(key)) {
			UFDouble taxamount = UFDouble.ZERO_DBL;
			UFDouble notaxmoney = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"notaxmoney");
			UFDouble taxrate = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"taxrate");
			UFDouble currentpaymentamount = (UFDouble) panel.getBodyValueAt(
					e.getRow(), "currentpaymentamount");
			if(currentpaymentamount == null){
				currentpaymentamount = new UFDouble(0);
				panel.setBodyValueAt(currentpaymentamount, e.getRow(), "currentpaymentamount");// 
			}
			if (null != taxrate) {
				notaxmoney = currentpaymentamount.div(taxrate.div(100).add(1));
				panel.setBodyValueAt(notaxmoney, e.getRow(), "notaxmoney");

			}
			if (null != currentpaymentamount) {
				taxamount = currentpaymentamount.sub(notaxmoney);
				panel.setBodyValueAt(taxamount, e.getRow(), "taxamount");
			}
			UFDouble num = (UFDouble) panel.getBodyValueAt(e.getRow(), "num");
			UFDouble notaxprice = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"notaxprice");
			if (null != num) {
				notaxprice = notaxmoney.div(num);
				panel.setBodyValueAt(notaxprice, e.getRow(), "notaxprice");
			}
			UFDouble hsdj = notaxprice.multiply((taxrate.div(100)
					.add(new UFDouble(1))));
			panel.setBodyValueAt(hsdj, e.getRow(), "taxprice");// 含税单价
		}
		// 无税金额变化时，判断数量不为空时，计算无税单价（无税金额/数量）
		if ("notaxmoney".equals(key)) {
			UFDouble notaxmoney = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"notaxmoney");
			if(notaxmoney == null){
				notaxmoney = new UFDouble(0);
				panel.setBodyValueAt(notaxmoney, e.getRow(), "notaxmoney");// 
			}
			UFDouble num = (UFDouble) panel.getBodyValueAt(e.getRow(), "num");
			UFDouble notaxprice = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"notaxprice");
			if (null != num) {
				notaxprice = notaxmoney.div(num);
				panel.setBodyValueAt(notaxprice, e.getRow(), "notaxprice");
			}
			UFDouble taxrate = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"taxrate");
			UFDouble taxamount = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"taxamount");
			UFDouble currentpaymentamount = (UFDouble) panel.getBodyValueAt(
					e.getRow(), "currentpaymentamount");

			if (null != taxrate) {
				taxamount = notaxmoney.multiply((taxrate.div(100)).add(1)).sub(
						notaxmoney);
				panel.setBodyValueAt(taxamount, e.getRow(), "taxamount");

				currentpaymentamount = taxamount.add(notaxmoney);
				panel.setBodyValueAt(currentpaymentamount, e.getRow(),
						"currentpaymentamount");
			}
			UFDouble hsdj = notaxprice.multiply((taxrate.div(100)
					.add(new UFDouble(1))));
			panel.setBodyValueAt(hsdj, e.getRow(), "taxprice");// 含税单价
		}
		// 税额变化时，判断无税金额不为空时，计算价税合计（无税金额+税额）
		if ("taxamount".equals(key)) {
			UFDouble currentpaymentamount = (UFDouble) panel.getBodyValueAt(
					e.getRow(), "currentpaymentamount");
			UFDouble notaxmoney = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"notaxmoney");
			UFDouble taxamount = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"taxamount");
			if(taxamount == null){
				taxamount = new UFDouble(0);
				panel.setBodyValueAt(taxamount, e.getRow(), "taxamount");// 
			}
			UFDouble taxrate = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"taxrate");

			// 税额变化时，判断价税合计不为空时，计算无税金额（价税合计-税额）
			if (null != currentpaymentamount) {
				notaxmoney = currentpaymentamount.sub(taxamount);
				panel.setBodyValueAt(notaxmoney, e.getRow(), "notaxmoney");
			}
			if (null != notaxmoney) {
				currentpaymentamount = taxamount.add(notaxmoney);
				panel.setBodyValueAt(currentpaymentamount, e.getRow(),
						"currentpaymentamount");
			}
			UFDouble notaxprice = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"notaxprice");// 无税单价
			UFDouble hsdj = notaxprice.multiply((taxrate.div(100)
					.add(new UFDouble(1))));
			panel.setBodyValueAt(hsdj, e.getRow(), "taxprice");// 含税单价
		}
		// 价税合计变化时，判断无税金额不为空时，计算无税金额（无税金额=本次付款金额/（1+税率））UFDouble.ZERO_DBL;
		if ("currentpaymentamount".equals(key)) {
			UFDouble taxamount = UFDouble.ZERO_DBL;
			UFDouble notaxmoney = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"notaxmoney");
			UFDouble taxrate = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"taxrate");
			UFDouble currentpaymentamount = (UFDouble) panel.getBodyValueAt(
					e.getRow(), "currentpaymentamount");
			if(currentpaymentamount == null){
				currentpaymentamount = new UFDouble(0);
				panel.setBodyValueAt(currentpaymentamount, e.getRow(), "currentpaymentamount");// 
			}
			if (null != taxrate) {
				notaxmoney = currentpaymentamount.div(taxrate.div(100).add(1));
				panel.setBodyValueAt(notaxmoney, e.getRow(), "notaxmoney");

			}
			if (null != currentpaymentamount) {
				taxamount = currentpaymentamount.sub(notaxmoney);
				panel.setBodyValueAt(taxamount, e.getRow(), "taxamount");
			}
			UFDouble num = (UFDouble) panel.getBodyValueAt(e.getRow(), "num");
			UFDouble notaxprice = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"notaxprice");
			if (null != num) {
				notaxprice = notaxmoney.div(num);
				panel.setBodyValueAt(notaxprice, e.getRow(), "notaxprice");
			}
			UFDouble hsdj = notaxprice.multiply((taxrate.div(100)
					.add(new UFDouble(1))));
			panel.setBodyValueAt(hsdj, e.getRow(), "taxprice");// 含税单价
		}
		// 含税单价变化时
		if ("taxprice".equals(key)) {
			UFDouble taxprice = panel.getBodyValueAt(e.getRow(), "taxprice") == null?new UFDouble(0):new UFDouble(panel.getBodyValueAt(e.getRow(), "taxprice").toString());// 含税单价
			panel.setBodyValueAt(taxprice, e.getRow(), "taxprice");
			UFDouble num = (UFDouble) panel.getBodyValueAt(e.getRow(), "num") == null?new UFDouble(0):(UFDouble) panel.getBodyValueAt(e.getRow(), "num");// 数量
			panel.setBodyValueAt(taxprice.multiply(num), e.getRow(), "currentpaymentamount");// 价税合计
			UFDouble currentpaymentamount = (UFDouble) panel.getBodyValueAt(
					e.getRow(), "currentpaymentamount");// 价税合计
			UFDouble taxamount = UFDouble.ZERO_DBL;
			UFDouble notaxmoney = panel.getBodyValueAt(e.getRow(), "notaxmoney") == null?new UFDouble(0):new UFDouble(panel.getBodyValueAt(e.getRow(), "notaxmoney").toString());
			UFDouble taxrate = panel.getBodyValueAt(e.getRow(), "taxrate") == null?new UFDouble(0):new UFDouble(Double.parseDouble(panel.getBodyValueAt(e.getRow(), "taxrate").toString()));
			if (null != taxrate) {
				notaxmoney = currentpaymentamount.div(taxrate.div(100).add(1));
				panel.setBodyValueAt(notaxmoney, e.getRow(), "notaxmoney");

			}
			if (null != currentpaymentamount) {
				taxamount = currentpaymentamount.sub(notaxmoney);
				panel.setBodyValueAt(taxamount, e.getRow(), "taxamount");
			}
			// UFDouble num = panel.getBodyValueAt(e.getRow(), "num") == null?new UFDouble(0):new UFDouble(panel.getBodyValueAt(e.getRow(), "num").toString());;
			UFDouble notaxprice = panel.getBodyValueAt(e.getRow(), "notaxprice") == null?new UFDouble(0):new UFDouble(panel.getBodyValueAt(e.getRow(), "notaxprice").toString());;
			if (null != num) {
				notaxprice = notaxmoney.div(num);
				panel.setBodyValueAt(notaxprice, e.getRow(), "notaxprice");
			}
		}
	}
}