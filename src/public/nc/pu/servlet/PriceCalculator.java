package nc.pu.servlet;

import nc.itf.scmpub.reference.uap.bd.vat.BuySellFlagEnum;
import nc.vo.ct.purdaily.entity.CtPuBVO;
import nc.vo.pu.m21.entity.OrderItemVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.calculator.AmountCalculator;
import nc.vo.pubapp.calculator.HslParseUtil;
import nc.vo.uap.taxcode.TaxType;

public class PriceCalculator {
	public void relationCalculatePrice(OrderItemVO view) {
		boolean isNumZero = false;
		UFDouble nnum = view.getNnum();
		if (null == nnum || UFDouble.ZERO_DBL.equals(nnum)) {
			isNumZero = true;
			view.setNnum(UFDouble.ONE_DBL);
			view.setNqtunitnum(HslParseUtil.hslDivUFDouble(view.getVchangerate(), view.getNnum()));
		}
		Integer fbuysellflag = view.getFbuysellflag();
		UFDouble nqtorigprice = UFDouble.ZERO_DBL;
		UFDouble norigprice = UFDouble.ZERO_DBL;
		UFDouble ngprice = UFDouble.ZERO_DBL;
		UFDouble nqtorigtaxprice = view.getNqtorigtaxprice();
		UFDouble norigtaxprice = calculateNorigtaxprice(view);
		UFDouble ngtaxprice = calculateNgtaxprice(view);
		if (BuySellFlagEnum.IMPORT.value().equals(fbuysellflag)
				|| BuySellFlagEnum.OUTPUT.value().equals(fbuysellflag)) {
			nqtorigprice = nqtorigtaxprice;
			norigprice = norigtaxprice;
			ngprice = ngtaxprice;
		} else {
			nqtorigprice = calculateNoTaxPrice(nqtorigtaxprice, view);
			norigprice = calculateNoTaxPrice(norigtaxprice, view);
			ngprice = calculateNoTaxPrice(ngtaxprice, view);
		}
		UFDouble norigtaxmny = nqtorigtaxprice.multiply(view.getNqtunitnum());
		view.setNqtorigprice(nqtorigprice);
		view.setNorigtaxprice(norigtaxprice);
		view.setNorigprice(norigprice);
//		view.setNgtaxprice(ngtaxprice);
//		view.setNgprice(ngprice);
		view.setNorigtaxmny(norigtaxmny);
		if (isNumZero) {
			view.setNnum(UFDouble.ZERO_DBL);
			view.setNqtunitnum(UFDouble.ZERO_DBL);
		}
	}

	private UFDouble calculateNgtaxprice(OrderItemVO vo) {
		UFDouble norigtaxmny = vo.getNqtorigtaxprice().multiply(vo.getNqtunitnum());
		UFDouble nexchangerate = vo.getNtaxrate();
		UFDouble ntaxmny = AmountCalculator.calAmountByDateRate(norigtaxmny, "1002Z0100000000001K1",
			"1002Z0100000000001K1", new UFDate().toStdString(), nexchangerate, vo.getPk_org());
		return ntaxmny.div(vo.getNnum());
	}

	private UFDouble calculateNorigtaxprice(OrderItemVO vo) {
		UFDouble nqtunitnum = vo.getNqtunitnum();

		UFDouble nnum = vo.getNnum();

		UFDouble nqtorigtaxprice = vo.getNqtorigtaxprice();

		UFDouble norigtaxmny = nqtorigtaxprice.multiply(nqtunitnum);

		return norigtaxmny.div(nnum);
	}

	private UFDouble calculateNoTaxPrice(UFDouble taxPrice, OrderItemVO vo) {
		UFDouble noTaxPrice = UFDouble.ZERO_DBL;

		Integer ftaxtypeflag = vo.getFtaxtypeflag();

		UFDouble ntaxrate = vo.getNtaxrate();

		if (TaxType.TAXABLE_CONTAINING.value().equals(ftaxtypeflag)) {

			noTaxPrice = taxPrice.multiply(UFDouble.ONE_DBL.sub(ntaxrate.div(new UFDouble(100))));

		} else {

			noTaxPrice = taxPrice.div(UFDouble.ONE_DBL.add(ntaxrate.div(new UFDouble(100))));
		}

		return noTaxPrice;
	}
}
