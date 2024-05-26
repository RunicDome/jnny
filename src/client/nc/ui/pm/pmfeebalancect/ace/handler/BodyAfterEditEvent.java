package nc.ui.pm.pmfeebalancect.ace.handler;

import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.taxcode.TaxrateVO;
import nc.vo.pub.lang.UFDouble;

//表体编辑后事件类
@SuppressWarnings("restriction")
public class BodyAfterEditEvent implements
		IAppEventHandler<CardBodyAfterEditEvent> {

	// 表体单价*数量=金额带进表头合同金额
	@Override
	public void handleAppEvent(CardBodyAfterEditEvent e) {

		BillCardPanel panel = e.getBillCardPanel();
		int rowCount = panel.getRowCount();
		UFDouble sum = UFDouble.ZERO_DBL;
		String key = e.getKey();
		// 表体数量变化时，判断无税单价不空时，计算无税金额(无税单价*数量)
		if ("num".equals(key)) {
			UFDouble def3 = UFDouble.ZERO_DBL;
			UFDouble def2 = panel.getBodyValueAt(e.getRow(), "def2") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "def2")
					.toString());// 无税单价
			UFDouble num = (UFDouble) panel.getBodyValueAt(e.getRow(), "num") == null ? new UFDouble(
					0) : (UFDouble) panel.getBodyValueAt(e.getRow(), "num");// 数量
			UFDouble def5 = panel.getBodyValueAt(e.getRow(), "def5") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "def5")
					.toString());// 税率
			UFDouble def4 = panel.getBodyValueAt(e.getRow(), "def4") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "def4")
					.toString());// 税额
			panel.setBodyValueAt(num, e.getRow(), "num");// 数量
			if (null != def2) {
				def3 = def2.multiply(num);
				panel.setBodyValueAt(def3, e.getRow(), "def3");
			}
			UFDouble def31 = panel.getBodyValueAt(e.getRow(), "def3") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "def3")
					.toString());
			UFDouble money = UFDouble.ZERO_DBL;

			if (null != def5) {
				def4 = def31.multiply((def5.div(100)).add(1)).sub(def31);
				panel.setBodyValueAt(def4, e.getRow(), "def4");

				money = def4.add(def31);
				panel.setBodyValueAt(money, e.getRow(), "money");
			}
			UFDouble hsdj = def2.multiply((def5.div(100).add(new UFDouble(1))));
			panel.setBodyValueAt(hsdj, e.getRow(), "unitmoney");// 含税单价
		}
		// 表体无税单价变化时，判断数量不空时，计算无税金额(无税单价*数量)
		if ("def2".equals(key)) {
			UFDouble def3 = panel.getBodyValueAt(e.getRow(), "def3") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "def3")
					.toString());
			UFDouble def2 = panel.getBodyValueAt(e.getRow(), "def2") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "def2")
					.toString());// 无税单价
			UFDouble num = (UFDouble) panel.getBodyValueAt(e.getRow(), "num") == null ? new UFDouble(
					0) : (UFDouble) panel.getBodyValueAt(e.getRow(), "num");// 数量
			UFDouble def5 = panel.getBodyValueAt(e.getRow(), "def5") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "def5")
					.toString());// 税率
			UFDouble def4 = panel.getBodyValueAt(e.getRow(), "def4") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "def4")
					.toString());// 税额
			panel.setBodyValueAt(def2, e.getRow(), "def2");
			if (null != num) {
				def3 = def2.multiply(num);
				panel.setBodyValueAt(def3, e.getRow(), "def3");
			}
			UFDouble def31 = panel.getBodyValueAt(e.getRow(), "def3") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "def3")
					.toString());
			UFDouble money = UFDouble.ZERO_DBL;
			if (null != def5) {
				def4 = def3.multiply((def5.div(100)).add(1)).sub(def3);
				panel.setBodyValueAt(def4, e.getRow(), "def4");

				money = def4.add(def31);
				panel.setBodyValueAt(money, e.getRow(), "money");
				UFDouble hsdj = def2.multiply((def5.div(100)
						.add(new UFDouble(1))));
				panel.setBodyValueAt(hsdj, e.getRow(), "unitmoney");// 含税单价
			}
		}
		// 税码发生
		if ("def1".equals(key)) {
			// 税率
			try {
				String pk_taxcode = (String) panel.getBodyValueAt(e.getRow(), "def1");// 税码
				TaxrateVO[] taxrateVO = (TaxrateVO[]) HYPubBO_Client.queryByCondition(TaxrateVO.class, "nvl(dr,0) = 0 and pk_taxcode='"+pk_taxcode+"'");
				if(null != taxrateVO && taxrateVO.length >= 1) {
					UFDouble taxrate = taxrateVO[0].getTaxrate();
					panel.setBodyValueAt(taxrate, e.getRow(), "def5");
					
					UFDouble money = panel.getBodyValueAt(e.getRow(), "money") == null ? new UFDouble(
							0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "money")
							.toString());
					UFDouble def4 = UFDouble.ZERO_DBL;
					UFDouble def3 = panel.getBodyValueAt(e.getRow(), "def3") == null ? new UFDouble(
							0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "def3")
							.toString());
					if (null != taxrate) {
						def3 = money.div(taxrate.div(100).add(1));
						panel.setBodyValueAt(def3, e.getRow(), "def3");

					}
					if (null != money) {
						def4 = money.sub(def3);
						panel.setBodyValueAt(def4, e.getRow(), "def4");
					}
					UFDouble num = panel.getBodyValueAt(e.getRow(), "num") == null ? new UFDouble(
							0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "num")
							.toString());
					;
					UFDouble def2 = panel.getBodyValueAt(e.getRow(), "def2") == null ? new UFDouble(
							0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "def2")
							.toString());
					;
					if (null != num) {
						def2 = def3.div(num);
						panel.setBodyValueAt(def2, e.getRow(), "def2");
					}
					UFDouble hsdj = def2.multiply((taxrate.div(100).add(new UFDouble(1))));
					panel.setBodyValueAt(hsdj, e.getRow(), "unitmoney");// 含税单价
				}
			} catch (UifException e1) {
				e1.printStackTrace();
			}
		}
		// 税率变化时，判断无税金额不为空时，根据money->def3*(1+def5/100)公式计算价税合计
		if ("def5".equals(key)) {
			UFDouble money = panel.getBodyValueAt(e.getRow(), "money") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "money")
					.toString());
			UFDouble def4 = UFDouble.ZERO_DBL;
			UFDouble def3 = panel.getBodyValueAt(e.getRow(), "def3") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "def3")
					.toString());
			UFDouble def5 = panel.getBodyValueAt(e.getRow(), "def5") == null ? new UFDouble(
					0) : new UFDouble(Double.parseDouble(panel.getBodyValueAt(
					e.getRow(), "def5").toString()));
			if (null != def5) {
				def3 = money.div(def5.div(100).add(1));
				panel.setBodyValueAt(def3, e.getRow(), "def3");

			}
			if (null != money) {
				def4 = money.sub(def3);
				panel.setBodyValueAt(def4, e.getRow(), "def4");
			}
			UFDouble num = panel.getBodyValueAt(e.getRow(), "num") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "num")
					.toString());
			;
			UFDouble def2 = panel.getBodyValueAt(e.getRow(), "def2") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "def2")
					.toString());
			;
			if (null != num) {
				def2 = def3.div(num);
				panel.setBodyValueAt(def2, e.getRow(), "def2");
			}
			UFDouble hsdj = def2.multiply((def5.div(100).add(new UFDouble(1))));
			panel.setBodyValueAt(hsdj, e.getRow(), "unitmoney");// 含税单价
		}
		// 无税金额变化时，判断数量不为空时，计算无税单价（无税金额/数量）
		if ("def3".equals(key)) {
			UFDouble def3 = panel.getBodyValueAt(e.getRow(), "def3") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "def3")
					.toString());
			panel.setBodyValueAt(def3, e.getRow(), "def3");
			UFDouble num = (UFDouble) panel.getBodyValueAt(e.getRow(), "num") == null ? new UFDouble(
					0) : (UFDouble) panel.getBodyValueAt(e.getRow(), "num");// 数量
			UFDouble def2 = panel.getBodyValueAt(e.getRow(), "def2") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "def2")
					.toString());// 无税单价
			if (null != num) {
				def2 = def3.div(num);
				panel.setBodyValueAt(def2, e.getRow(), "def2");
			}
			UFDouble def5 = panel.getBodyValueAt(e.getRow(), "def5") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "def5")
					.toString());// 税率
			UFDouble def4 = panel.getBodyValueAt(e.getRow(), "def4") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "def4")
					.toString());// 税额
			UFDouble money = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"money");

			if (null != def5) {
				def4 = def3.multiply((def5.div(100)).add(1)).sub(def3);
				panel.setBodyValueAt(def4, e.getRow(), "def4");

				money = def4.add(def3);
				panel.setBodyValueAt(money, e.getRow(), "money");
			}
			UFDouble hsdj = def2.multiply((def5.div(100).add(new UFDouble(1))));
			panel.setBodyValueAt(hsdj, e.getRow(), "unitmoney");// 含税单价
		}
		// 税额变化时，判断无税金额不为空时，计算价税合计（无税金额+税额）
		if ("def4".equals(key)) {
			UFDouble money = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"money");
			UFDouble def3 = panel.getBodyValueAt(e.getRow(), "def3") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "def3")
					.toString());
			UFDouble def4 = panel.getBodyValueAt(e.getRow(), "def4") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "def4")
					.toString());// 税额
			panel.setBodyValueAt(def4, e.getRow(), "def4");//
			UFDouble def5 = panel.getBodyValueAt(e.getRow(), "def5") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "def5")
					.toString());// 税率

			// 税额变化时，判断价税合计不为空时，计算无税金额（价税合计-税额）
			if (null != money) {
				def3 = money.sub(def4);
				panel.setBodyValueAt(def3, e.getRow(), "def3");
			}
			if (null != def3) {
				money = def4.add(def3);
				panel.setBodyValueAt(money, e.getRow(), "money");
			}
			UFDouble def2 = panel.getBodyValueAt(e.getRow(), "def2") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "def2")
					.toString());// 税率;// 无税单价
			UFDouble hsdj = def2.multiply((def5.div(100).add(new UFDouble(1))));
			panel.setBodyValueAt(hsdj, e.getRow(), "unitmoney");// 含税单价
		}
		// 价税合计变化时，判断无税金额不为空时，计算无税金额（无税金额=本次付款金额/（1+税率））UFDouble.ZERO_DBL;
		if ("money".equals(key)) {
			UFDouble def4 = UFDouble.ZERO_DBL;
			UFDouble def3 = panel.getBodyValueAt(e.getRow(), "def3") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "def3")
					.toString());
			UFDouble def5 = panel.getBodyValueAt(e.getRow(), "def5") == null ? new UFDouble(
					0) : new UFDouble(Double.parseDouble(panel.getBodyValueAt(
					e.getRow(), "def5").toString()));
			UFDouble money = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"money");
			if (money == null) {
				money = new UFDouble(0);
				panel.setBodyValueAt(money, e.getRow(), "money");//
			}
			if (null != def5) {
				def3 = money.div(def5.div(100).add(1));
				panel.setBodyValueAt(def3, e.getRow(), "def3");

			}
			if (null != money) {
				def4 = money.sub(def3);
				panel.setBodyValueAt(def4, e.getRow(), "def4");
			}
			UFDouble num = panel.getBodyValueAt(e.getRow(), "num") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "num")
					.toString());
			;
			UFDouble def2 = panel.getBodyValueAt(e.getRow(), "def2") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "def2")
					.toString());
			;
			if (null != num) {
				def2 = def3.div(num);
				panel.setBodyValueAt(def2, e.getRow(), "def2");
			}
			UFDouble hsdj = def2.multiply((def5.div(100).add(new UFDouble(1))));
			panel.setBodyValueAt(hsdj, e.getRow(), "unitmoney");// 含税单价

		}
		// 含税单价变化时
		if ("unitmoney".equals(key)) {
			UFDouble unitmoney = panel.getBodyValueAt(e.getRow(), "unitmoney") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(),
					"unitmoney").toString());// 含税单价
			panel.setBodyValueAt(unitmoney, e.getRow(), "unitmoney");	
			UFDouble num = (UFDouble) panel.getBodyValueAt(e.getRow(), "num") == null ? new UFDouble(
					0) : (UFDouble) panel.getBodyValueAt(e.getRow(), "num");// 数量
			panel.setBodyValueAt(unitmoney.multiply(num), e.getRow(), "money");// 价税合计
			UFDouble money = (UFDouble) panel.getBodyValueAt(e.getRow(),
					"money");// 价税合计
			UFDouble def4 = UFDouble.ZERO_DBL;
			UFDouble def3 = panel.getBodyValueAt(e.getRow(), "def3") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "def3")
					.toString());
			UFDouble def5 = panel.getBodyValueAt(e.getRow(), "def5") == null ? new UFDouble(
					0) : new UFDouble(Double.parseDouble(panel.getBodyValueAt(
					e.getRow(), "def5").toString()));
			if (null != def5) {
				def3 = money.div(def5.div(100).add(1));
				panel.setBodyValueAt(def3, e.getRow(), "def3");

			}
			if (null != money) {
				def4 = money.sub(def3);
				panel.setBodyValueAt(def4, e.getRow(), "def4");
			}
			// UFDouble num = panel.getBodyValueAt(e.getRow(), "num") ==
			// null?new UFDouble(0):new
			// UFDouble(panel.getBodyValueAt(e.getRow(), "num").toString());;
			UFDouble def2 = panel.getBodyValueAt(e.getRow(), "def2") == null ? new UFDouble(
					0) : new UFDouble(panel.getBodyValueAt(e.getRow(), "def2")
					.toString());
			;
			if (null != num) {
				def2 = def3.div(num);
				panel.setBodyValueAt(def2, e.getRow(), "def2");
			}
		}

		for (int row = 0; row < rowCount; row++) {
			UFDouble unitmoney = (UFDouble) panel.getBodyValueAt(row,
					"unitmoney");
			UFDouble num = (UFDouble) panel.getBodyValueAt(row, "num");
			UFDouble money = UFDouble.ZERO_DBL;
			if (null != unitmoney && null != num) {
				money = unitmoney.multiply(num);
				panel.setBodyValueAt(money, row, "money");
			}
			sum = sum.add(money);
		}
		panel.setHeadItem("contractmoney", sum.toString());
	}

}