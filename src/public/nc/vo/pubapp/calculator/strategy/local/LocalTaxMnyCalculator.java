package nc.vo.pubapp.calculator.strategy.local;

import nc.vo.pubapp.calculator.Condition;
import nc.vo.pubapp.calculator.data.CacheDataSet;
import nc.vo.pubapp.calculator.data.IRelationForItems;
import nc.vo.pubapp.calculator.formula.DiscountMnyFormula;
import nc.vo.pubapp.calculator.formula.SummnyMoneyTaxFormula;
import nc.vo.pubapp.calculator.formula.TaxTaxRateSummnyFormula;
import nc.vo.pubapp.calculator.strategy.IStrategyForCal;
import nc.vo.pubapp.calculator.strategy.LocalCalculator;
import nc.vo.pubapp.scale.ScaleUtils;

/**
 * 本币价税合计策略
 */
public class LocalTaxMnyCalculator implements IStrategyForCal {
	private CacheDataSet data;

	private IRelationForItems item;

	private ScaleUtils scale;

	public LocalTaxMnyCalculator(CacheDataSet data, ScaleUtils scale) {
		this.data = data;
		this.item = this.data.getRelationForItem();
		this.scale = scale;
	}

	@Override
	public void calculate(Condition cond) {
		cond.setIsTaxOrNet(true);
		if (this.data.getCondition().isInternational()) {
			// 记税金额
			calculateNCalTaxMny();
		}
		// 主单位本币税额
		this.calculateNTax();
		// 主单位本币无税金额=主单位本币价税合计-主单位本币税额
		this.calculateNmny();
		// 本币不可抵扣税额
		this.calculateNDeductibleTax();
		// 记成本金额
		calculateNCostmny();
		Object attributeValue1 = this.data.getAttributeValue("nnum");

		if (cond.getUnitPriorType() == Condition.QT_PRIOR) {
			// 由主单位本币无税金额 计算报价单位本币的单价、净单价
			cond.setAllowedClonePrice(false);
			this.calculateQtUnit(cond);
			cond.setAllowedClonePrice(true);
			new LocalCqtUnitToCunitcalculator(this.data, this.scale)
					.calculateFromSummny(cond);
		} else {
			cond.setAllowedClonePrice(false);
			new LocalCqtUnitToCunitcalculator(this.data, this.scale)
					.calculateFromSummny(cond);
			// 由主单位本币无税金额 计算报价单位本币的单价、净单价
			cond.setAllowedClonePrice(true);
			this.calculateQtUnit(cond);
		}
		// // 主单位本币含税净价=主单位本币价税合计/主单位数量
		// calculateNTaxNetPrice();
		// // 主单位本币含税单价=主单位本币含税净价/单品折扣*整单折扣
		// calculateNTaxprice(cond);
		// // 主单位本币无税净价
		// calculateNNetPrice();
		// // 主单位本币无税单价
		// calculateNPrice();

		// // 根据主单位本币计算合计 计算报价计量单位本币单价、净价的值
		// calculateQtUnit(cond);
		// 主单位本币折扣额=主单位本币含税单价*主数量-主单位本币价税合
		Object attributeValue = this.data.getAttributeValue("nassistnum");

		this.calculateNDiscount(cond);
		if (!this.data.getCondition().isInternational()) {
			// 记税金额
			calculateNCalTaxMny();
		}
		// 计算vat相关字段
		new LocalCalculator(this.data, this.scale).calTaxRelation();

	}

	/**
	 * 主单位本币折扣额=主单位本币含税单价*主数量-主单位本币价税合计
	 */
	private void calculateNDiscount(Condition cond) {
		String taxPriceKey = this.item.getNtaxpriceKey();
		String numKey = this.item.getNnumKey();
		if (cond.getUnitPriorType() == Condition.QT_PRIOR) {
			taxPriceKey = this.item.getNqttaxpriceKey();
			numKey = this.item.getNqtunitnumKey();
		} else {
			taxPriceKey = this.item.getNtaxpriceKey();
			numKey = this.item.getNnumKey();
		}
		String summnyKey = this.item.getNtaxmnyKey();
		String discountMnyKey = this.item.getNdiscountKey();
		String curridKey = this.item.getCcurrencyidKey();
		Object currid = this.data.getAttributeValue(curridKey);
		boolean flag = this.data.hasItemKey(discountMnyKey)
				&& this.data.hasItemKey(summnyKey)
				&& this.data.hasItemKey(numKey) && (currid != null);
		if (!flag) {
			return;
		}
		DiscountMnyFormula formula = new DiscountMnyFormula(this.data,
				this.scale, taxPriceKey, numKey, summnyKey, discountMnyKey,
				curridKey);
		formula.calculateDiscountMny();
	}

	public void calculateNmny() {
		Object attributeValue = this.data.getAttributeValue("nassistnum");
		String summnyKey = this.item.getNtaxmnyKey();
		String moneyKey = this.item.getNmnyKey();
		String taxKey = this.item.getNtaxKey();
		String curridKey = this.item.getCcurrencyidKey();
		Object currid = this.data.getAttributeValue(curridKey);
		boolean flag = this.data.hasItemKey(taxKey)
				&& this.data.hasItemKey(moneyKey)
				&& this.data.hasItemKey(summnyKey) && (currid != null);
		if (!flag) {
			return;
		}
		SummnyMoneyTaxFormula formula = new SummnyMoneyTaxFormula(this.data,
				this.scale, false);
		formula.calculateMoney();
	}

	private void calculateNCostmny() {
		Object attributeValue = this.data.getAttributeValue("nassistnum");

		String summnyKey = this.item.getNtaxmnyKey();
		String moneyKey = this.item.getNmnyKey();
		String taxKey = this.item.getNtaxKey();
		String costMnyKey = this.item.getNcostmnyKey();
		String curridKey = this.item.getCcurrencyidKey();
		Object currid = this.data.getAttributeValue(curridKey);
		boolean flag = this.data.hasItemKey(taxKey)
				&& this.data.hasItemKey(moneyKey)
				&& this.data.hasItemKey(summnyKey) && (currid != null)
				&& this.data.hasItemKey(costMnyKey);
		if (!flag) {
			return;
		}
		SummnyMoneyTaxFormula formula = new SummnyMoneyTaxFormula(this.data,
				this.scale, false);
		formula.calculateCalCostMny();
	}

	private void calculateNCalTaxMny() {
		String summnyKey = this.item.getNtaxmnyKey();
		String moneyKey = this.item.getNmnyKey();
		String taxKey = this.item.getNtaxKey();
		String calTaxMny = this.item.getNcaltaxmnyKey();
		String curridKey = this.item.getCcurrencyidKey();
		Object currid = this.data.getAttributeValue(curridKey);
		boolean flag = this.data.hasItemKey(taxKey)
				&& this.data.hasItemKey(moneyKey)
				&& this.data.hasItemKey(summnyKey) && (currid != null)
				&& this.data.hasItemKey(calTaxMny);
		if (!flag) {
			return;
		}
		SummnyMoneyTaxFormula formula = new SummnyMoneyTaxFormula(this.data,
				this.scale, false);
		formula.calculateCalMnyFromSummny();
	}

	private void calculateNTax() {
		Object attributeValue = this.data.getAttributeValue("nnum");
		String summnyKey = this.item.getNtaxmnyKey();
		String taxKey = this.item.getNtaxKey();
		String taxRateKey = this.item.getNtaxrateKey();
		String ftaxtypeflag = this.item.getFtaxtypeflagKey();
		String curridKey = this.item.getCcurrencyidKey();
		Object currid = this.data.getAttributeValue(curridKey);
		boolean flag = this.data.hasItemKey(summnyKey)
				&& this.data.hasItemKey(taxRateKey)
				&& this.data.hasItemKey(taxRateKey) && (currid != null);
		if (!flag) {
			return;
		}
		TaxTaxRateSummnyFormula formula = new TaxTaxRateSummnyFormula(
				this.data, this.scale, summnyKey, taxKey, taxRateKey,
				ftaxtypeflag, curridKey);
		formula.calculateTax();
	}

	private void calculateNDeductibleTax() {
		String summnyKey = this.item.getNtaxmnyKey();
		String taxKey = this.item.getNdeductibletaxKey();
		String taxRateKey = this.item.getNdeductibleTaxrateKey();
		String ftaxtypeflag = this.item.getFtaxtypeflagKey();
		String curridKey = this.item.getCcurrencyidKey();
		Object currid = this.data.getAttributeValue(curridKey);
		boolean flag = this.data.hasItemKey(summnyKey)
				&& this.data.hasItemKey(taxRateKey) && (currid != null);
		if (!flag) {
			return;
		}
		TaxTaxRateSummnyFormula formula = new TaxTaxRateSummnyFormula(
				this.data, this.scale, summnyKey, taxKey, taxRateKey,
				ftaxtypeflag, curridKey);
		formula.calculateDeductibleTax();
	}

	private void calculateQtUnit(Condition cond) {
		String cqtUnitidKey = this.item.getCqtunitidKey();
		String unitidKey = this.item.getCunitidKey();
		Object cqtUnitid = this.data.getAttributeValue(cqtUnitidKey);
		Object unitid = this.data.getAttributeValue(unitidKey);
		boolean flag = this.data.hasItemKey(cqtUnitidKey)
				&& this.data.hasItemKey(unitidKey) && (cqtUnitid != null)
				&& (unitid != null);
		if (!flag) {
			return;
		}
		LocalCunitToCqtunitCalculator formula = new LocalCunitToCqtunitCalculator(
				this.data, this.scale);
		formula.calculateFromSummny(cond);
	}
	// private void calculateNTaxNetPrice() {
	// String taxNetPricekey = this.item.getNtaxnetpriceKey();
	// String numKey = this.item.getNnumKey();
	// String summnyKey = this.item.getNtaxmnyKey();
	// boolean flag = this.data.hasItemKey(taxNetPricekey)
	// && this.data.hasItemKey(numKey) && this.data.hasItemKey(summnyKey);
	// if (!flag) {
	// return;
	// }
	// TaxNetPriceSummnyNumFormula formula = new
	// TaxNetPriceSummnyNumFormula(this.data,
	// this.scale, taxNetPricekey, numKey, summnyKey, null, null);
	// formula.calculateTaxNetPrice();
	// }
	//
	// private void calculateNTaxprice(Condition cond) {
	// String taxPriceKey = this.item.getNtaxpriceKey();
	// String taxNetPriceKey = this.item.getNtaxnetpriceKey();
	// String discountKey = this.item.getNitemdiscountrateKey();
	// String allDiscountKey = this.item.getNdiscountrateKey();
	// boolean flag = this.data.hasItemKey(taxPriceKey)
	// && this.data.hasItemKey(discountKey);
	// if (!flag) {
	// return;
	// }
	// TaxPriceTaxNetPriceFormula formula = new
	// TaxPriceTaxNetPriceFormula(this.data,
	// this.scale, taxPriceKey, taxNetPriceKey, discountKey, allDiscountKey);
	// // UFDouble taxNetPrice = (UFDouble) this.data
	// // .getAttributeValue(taxNetPriceKey);
	// UFDouble taxPrice = (UFDouble) this.data.getAttributeValue(taxPriceKey);
	// // int intTaxNetPrice = MathTool.compareTo(taxNetPrice,
	// // UFDouble.ZERO_DBL);
	// int intTaxPrice = MathTool.compareTo(taxPrice,
	// UFDouble.ZERO_DBL);
	// flag = (intTaxPrice == 0)
	// || cond.getIsChgPriceOrDiscount()||cond.shouldpriAdjustPrice();
	// if (flag) {
	// // 强制调主单位本币含税单价
	// formula.calculateTaxPrice();
	// }
	// else {
	// formula.calculateDiscountRate();
	// }
	// }
	// private void calculateNNetPrice() {
	// String taxNetPriceKey = this.item.getNtaxnetpriceKey();
	// String netPriceKey = this.item.getNnetpriceKey();
	// // 税率
	// String taxRateKey = this.item.getNtaxrateKey();
	// // 扣税类别
	// String taxtype = this.item.getFtaxtypeflagKey();
	// boolean flag = this.data.hasItemKey(taxNetPriceKey)
	// && this.data.hasItemKey(netPriceKey)
	// && this.data.hasItemKey(taxRateKey);
	// if (!flag) {
	// return;
	// }
	// TaxNetPriceNetPriceFormula formula = new
	// TaxNetPriceNetPriceFormula(this.data,
	// this.scale, taxNetPriceKey, netPriceKey, taxRateKey, taxtype);
	// formula.calculateNetPrice();
	// }
	//
	// private void calculateNPrice() {
	// String taxPriceKey = this.item.getNtaxpriceKey();
	// // 主单位本币无税单价
	// String priceKey = this.item.getNpriceKey();
	// // 税率
	// String taxRateKey = this.item.getNtaxrateKey();
	// // 扣税类别
	// String taxtype = this.item.getFtaxtypeflagKey();
	// boolean flag = this.data.hasItemKey(priceKey)
	// && this.data.hasItemKey(taxRateKey);
	// if (!flag) {
	// return;
	// }
	// // 创建含税单价、无税单价、税率之间的计算公式实例
	// PriceAndTaxPriceFormula formula = new PriceAndTaxPriceFormula(this.data,
	// this.scale, taxPriceKey, priceKey, taxRateKey, taxtype);
	// formula.calculatePrice();
	// }

}
