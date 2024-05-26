package nc.vo.pubapp.calculator.strategy;

import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.calculator.CalculatorUtil;
import nc.vo.pubapp.calculator.Condition;
import nc.vo.pubapp.calculator.data.CacheDataSet;
import nc.vo.pubapp.calculator.data.IRelationForItems;
import nc.vo.pubapp.calculator.formula.CqtUnitToCunitcalculator;
import nc.vo.pubapp.calculator.formula.CunitToCqtunitCalculator;
import nc.vo.pubapp.calculator.formula.DiscountMnyFormula;
import nc.vo.pubapp.calculator.formula.NetPriceMoneyNumFormula;
import nc.vo.pubapp.calculator.formula.NumConvertRateFormula;
import nc.vo.pubapp.calculator.formula.NumQualifiedNumFormula;
import nc.vo.pubapp.calculator.formula.SummnyMoneyTaxFormula;
import nc.vo.pubapp.calculator.formula.TaxNetPriceSummnyNumFormula;
import nc.vo.pubapp.pattern.pub.MathTool;
import nc.vo.pubapp.scale.ScaleUtils;

/**
 * 辅数量策略
 * 
 * @author zhuhz
 */
public class AssistNumCalculator implements IStrategyForCal {

	private CacheDataSet data;

	private IRelationForItems item;

	private ScaleUtils scale;

	public AssistNumCalculator(CacheDataSet data, ScaleUtils scale) {
		this.data = data;
		this.scale = scale;
		this.item = this.data.getRelationForItem();

	}

	private boolean isQtunitNumZero() {
		boolean isZero = false;
		UFDouble qtNum = (UFDouble) this.data.getAttributeValue(this.data
				.getRelationForItem().getNqtunitnumKey());
		isZero = MathTool.compareTo(qtNum, UFDouble.ZERO_DBL) == 0;
		return isZero;
	}

	public boolean isNumNulled() {
		boolean isNumNulled = this.data.getNassistnum() == null
				&& this.data.getRelationForItem().getNassistnumKey().equals(
						this.data.getCondition().getChangeKey());
		return isNumNulled;
	}

	@Override
	public void calculate(Condition cond) {
		boolean isQtunitNumZero = isQtunitNumZero();

		// 计算主数量
		this.calculateNum();
		// 计算报价单位数量
		this.calculateQtunitnum(cond);
		// 计算合格数量的
		this.calculateQulifiedNum();
		boolean isTaxOrNet = cond.getIsTaxOrNet();
		Object attributeValue = this.data.getAttributeValue("nnum");
		Object attributeValue1 = this.data.getAttributeValue("nassistnum");
		// 如果浮动报价换算率，并且报价优先，则金额不变，反算主单位单价系列
		if ((!cond.getIsFixNqtunitrate() && !isQtunitNumZero)
				&& cond.getUnitPriorType() == Condition.QT_PRIOR
				&& !this.isQtUinitEquelsAssUnit() && !isNumNulled()) {

			this.setNoPermitModifyColumn();
			if (isTaxOrNet) {
				cond.setAllowedClonePrice(true);
				new CqtUnitToCunitcalculator(this.data, this.scale)
						.calculateFromSummny(cond);
			} else {
				cond.setAllowedClonePrice(true);
				new CqtUnitToCunitcalculator(this.data, this.scale)
						.calculateFromMoney(cond);
			}
			return;
		}
		if (cond.getUnitPriorType() == Condition.QT_PRIOR) {
			if (this.isNeedCalPrice(cond)) {
				if (isTaxOrNet) {
					cond.setAllowedClonePrice(false);
					new CunitToCqtunitCalculator(this.data, this.scale)
							.calculateFromSummny(cond);
					cond.setAllowedClonePrice(true);
					new CqtUnitToCunitcalculator(this.data, this.scale)
							.calculateFromSummny(cond);
				} else {
					cond.setAllowedClonePrice(false);
					new CunitToCqtunitCalculator(this.data, this.scale)
							.calculateFromMoney(cond);
					cond.setAllowedClonePrice(true);
					new CqtUnitToCunitcalculator(this.data, this.scale)
							.calculateFromMoney(cond);
				}
			} else {
				new QtUnitNumCalculator(this.data, this.scale)
						.calculateMny(cond);
			}
			if (CalculatorUtil.isMainPriceNull(cond, this.data)) {
				if (isTaxOrNet) {
					cond.setAllowedClonePrice(false);
					new CqtUnitToCunitcalculator(this.data, this.scale)
							.calculateFromSummny(cond);
				} else {
					cond.setAllowedClonePrice(false);
					new CqtUnitToCunitcalculator(this.data, this.scale)
							.calculateFromMoney(cond);
				}
			}
		} else if (cond.getUnitPriorType() == Condition.MAIN_PRIOR) {

			if (isTaxOrNet) {
				if (this.isNeedCalPrice(cond)) {
					cond.setAllowedClonePrice(false);
					new CqtUnitToCunitcalculator(this.data, this.scale)
							.calculateFromSummny(cond);
					cond.setAllowedClonePrice(true);
					new CunitToCqtunitCalculator(this.data, this.scale)
							.calculateFromSummny(cond);
				} else {
					// 含税优先
					this.calculateFromTaxMny();
					if (CalculatorUtil.isQcPriceNull(cond, this.data)
							|| !cond.getIsFixNqtunitrate()) {
						cond.setAllowedClonePrice(true);
						new CunitToCqtunitCalculator(this.data, this.scale)
								.calculateFromSummny(cond);
					}
				}

			} else {
				if (this.isNeedCalPrice(cond)) {
					cond.setAllowedClonePrice(false);
					new CqtUnitToCunitcalculator(this.data, this.scale)
							.calculateFromMoney(cond);
					cond.setAllowedClonePrice(true);
					new CunitToCqtunitCalculator(this.data, this.scale)
							.calculateFromMoney(cond);
				} else {
					// 无税优先
					this.calculateFromMny();
					if (CalculatorUtil.isQcPriceNull(cond, this.data)
							|| !cond.getIsFixNqtunitrate()) {
						cond.setAllowedClonePrice(true);
						new CunitToCqtunitCalculator(this.data, this.scale)
								.calculateFromMoney(cond);
					}
				}

			}
		}
		this.setNoPermitModifyColumn();
		Object attributeValue2 = this.data.getAttributeValue("nnum");
		Object attributeValue3 = this.data.getAttributeValue("nassistnum");
	}

	/*
	 * 无税优先：主单位原币无税金额=主单位原币无税净价*主数量
	 */
	private void caculatePriorMny() {
		// 主单位原币无税净价对应的key
		String netPriceKey = this.item.getNorignetpriceKey();
		// 主数量对应的key
		String numKey = this.item.getNnumKey();
		// 主单位原币无税金额对应的key
		String moneyKey = this.item.getNorigmnyKey();
		// 主单位原币币种对应的key
		String curridKey = this.item.getCorigcurrencyidKey();
		Object currid = this.data.getAttributeValue(curridKey);
		boolean flag = this.data.hasItemKey(moneyKey)
				&& this.data.hasItemKey(numKey) && currid != null;
		if (!flag) {
			return;
		}
		// 主单位原币无税金额、主单位原币无税净价、主数量公式的实例对象
		NetPriceMoneyNumFormula formula = new NetPriceMoneyNumFormula(
				this.data, this.scale, netPriceKey, numKey, moneyKey,
				curridKey, this.item.getCunitidKey());
		formula.calculateMoney();
	}

	/*
	 * 主单位原币折扣额=主单位原币含税单价*主数量-价税合计
	 */
	private void calculateDiscountMny() {
		String taxPriceKey = this.item.getNorigtaxpriceKey();
		String numKey = this.item.getNnumKey();
		String summnyKey = this.item.getNorigtaxmnyKey();
		String discountMnyKey = this.item.getNorigdiscountKey();
		String curridKey = this.item.getCorigcurrencyidKey();
		Object currid = this.data.getAttributeValue(curridKey);
		boolean flag = this.data.hasItemKey(discountMnyKey)
				&& this.data.hasItemKey(summnyKey)
				&& this.data.hasItemKey(numKey) && currid != null;
		if (!flag) {
			return;
		}
		DiscountMnyFormula formula = new DiscountMnyFormula(this.data,
				this.scale, taxPriceKey, numKey, summnyKey, discountMnyKey,
				curridKey);
		formula.calculateDiscountMny();
	}

	/**
	 * 无税优先：计算主单位原币金额
	 * 
	 * @author zhuhz
	 */
	private void calculateFromMny() {
		// 主单位原币无税金额=主单位原币无税净价*主数量
		this.caculatePriorMny();
		// // 主单位原币税额=主单位原币无税金额*税率
		// this.calculatePriorTax();
		// 主单位原币价税合计=主单位原币无税金额+主单位原币税额
		this.calculatePriorTaxMny();
		// 主单位原币折扣额=主单位原币含税单价*主数量-价税合计
		this.calculateDiscountMny();
	}

	/*
	 * 含税优先：计算主单位原币金额
	 */
	private void calculateFromTaxMny() {
		// 主单位原币价税合计=主单位原币含税净价*主数量
		this.calculateTaxPriorTaxMny();
		// // 主单位原币税额
		// this.calculateTaxPriorTax();
		// 主单位原币无税金额=主单位原币价税合计-主单位原币税额
		this.calculateTaxPriorMny();
		// 主单位原币折扣额=主单位原币含税单价*主数量-价税合计
		this.calculateDiscountMny();
	}

	/*
	 * 主数量=数量*换算率
	 */
	private void calculateNum() {
		// 主单位对应的key
		String unitKey = this.item.getCunitidKey();
		// 主数量对应的key
		String numKey = this.item.getNnumKey();
		// 数量对应的key
		String otherNumKey = this.item.getNassistnumKey();
		// 换算率对应的key
		String convertRateKey = this.item.getNchangerateKey();
		Object unitid = this.data.getAttributeValue(unitKey);
		boolean flag = this.data.hasItemKey(unitKey)
				&& this.data.hasItemKey(convertRateKey)
				&& this.data.hasItemKey(otherNumKey)
				&& this.data.hasItemKey(numKey) && unitid != null
				&& this.data.getAttributeValue(convertRateKey) != null;
		if (!flag) {
			return;
		}
		// 主数量、数量、换算率公式的实例对象
		NumConvertRateFormula formula = new NumConvertRateFormula(this.data,
				this.scale, numKey, otherNumKey, convertRateKey, unitKey, null,null);
		// 是否固定换算率
		// boolean isFix = cond.getIsFixNchangerate();
		// if (!isFix) {
		// formula.calculateConvertRate();
		// }
		// else {
		formula.calculateNum();
		// }
	}

	// private void calculatePriorTax() {
	// // 主单位原币无税金额对应的key
	// String moneyKey = this.item.getNorigmnyKey();
	// // 主单位原币税额对应的key
	// String taxKey = this.item.getNorigtaxKey();
	// // 税率key
	// String taxRateKey = this.item.getNtaxrateKey();
	// // 扣税类别key
	// String ftaxtypeflag = this.item.getFtaxtypeflagKey();
	// // 主单位原币币种key
	// String curridKey = this.item.getCorigcurrencyidKey();
	// Object currid = this.data.getAttributeValue(curridKey);
	// boolean flag = this.data.hasItemKey(moneyKey)
	// && this.data.hasItemKey(taxRateKey)
	// && this.data.hasItemKey(taxRateKey) && currid != null;
	// if (!flag) {
	// return;
	// }
	// // 主单位原币税额、税率 、主单位原币无税金额公式实例对象
	// TaxTaxRateMoneyFormula formula = new TaxTaxRateMoneyFormula(this.data,
	// this.scale, moneyKey, taxKey, taxRateKey, ftaxtypeflag,
	// curridKey);
	// formula.calculateTax();
	// }

	/*
	 * 无税优先：主单位原币价税合计=主单位原币无税金额+主单位原币税额
	 */
	private void calculatePriorTaxMny() {
		String moneyKey = this.item.getNorigmnyKey();
		String summnyKey = this.item.getNorigtaxmnyKey();
		String curridKey = this.item.getCorigcurrencyidKey();
		Object currid = this.data.getAttributeValue(curridKey);
		boolean flag = this.data.hasItemKey(moneyKey)
				&& this.data.hasItemKey(summnyKey) && currid != null;
		if (!flag) {
			return;
		}
		SummnyMoneyTaxFormula formula = new SummnyMoneyTaxFormula(this.data,
				this.scale, true);
		formula.calculateSummny();
	}

	/*
	 * 报价单位数量=主数量/报价单位换算率
	 */
	private void calculateQtunitnum(Condition cond) {
		// 报价数量单位对应的key
		String otherUnitKey = this.item.getCqtunitidKey();
		// 主数量对应的key
		String numKey = this.item.getNnumKey();
		// 报价单位数量对应的key
		String otherNumKey = this.item.getNqtunitnumKey();
		// 报价单位换算率对应的key
		String convertRateKey = this.item.getNqtunitrateKey();
		Object otherUnitid = this.data.getAttributeValue(otherUnitKey);
		boolean flag = this.data.hasItemKey(otherUnitKey)
				&& this.data.hasItemKey(convertRateKey)
				&& this.data.hasItemKey(otherNumKey)
				&& this.data.hasItemKey(numKey) && otherUnitid != null;
		if (!flag) {
			return;
		}
		// 报价单位数量、主数量、报价单位换算率公式的实例对象
		NumConvertRateFormula formula = new NumConvertRateFormula(this.data,
				this.scale, numKey, otherNumKey, convertRateKey, null,
				otherUnitKey,null);
		boolean isFix = cond.getIsFixNqtunitrate();
		// 是否固定报价换算率
		if (!isFix && !this.isQtUinitEquelsAssUnit()) {
			formula.calculateConvertRate();
		} else {
			if (this.isQtUinitEquelsAssUnit()) {
				this.data.setNqtunitnum(this.data.getNassistnum());
			} else {
				formula.calculateOtherNum();
			}
		}
	}

	/*
	 * 合格数量=主数量-不合格数量
	 */
	private void calculateQulifiedNum() {
		// 主数量对应的key
		String numKey = this.item.getNnumKey();
		// 合格数量对应的key
		String qualifieNumKey = this.item.getQualifiedNumKey();
		// 不合格数量对应的key
		String unQualifiedNumKey = this.item.getUnQualifiedNumKey();
		// 主单位对应的key
		String unitKey = this.item.getCunitidKey();
		Object unitid = this.data.getAttributeValue(unitKey);
		boolean flag = this.data.hasItemKey(unitKey)
				&& this.data.hasItemKey(unQualifiedNumKey)
				&& this.data.hasItemKey(qualifieNumKey)
				&& this.data.hasItemKey(numKey) && unitid != null;
		if (!flag) {
			return;
		}
		// 主数量、合格数量、不合格数量公式的实例对象
		NumQualifiedNumFormula formula = new NumQualifiedNumFormula(this.data,
				this.scale, numKey, qualifieNumKey, unQualifiedNumKey, unitKey);
		formula.calculateQualifiedNum();
	}

	/*
	 * 主单位原币无税金额=主单位原币减税合计-主单位原币无税金额
	 */
	private void calculateTaxPriorMny() {
		String summnyKey = this.item.getNorigtaxmnyKey();
		String moneyKey = this.item.getNorigmnyKey();
		// String taxKey = this.item.getNorigtaxKey();
		String curridKey = this.item.getCorigcurrencyidKey();
		Object currid = this.data.getAttributeValue(curridKey);
		boolean flag = this.data.hasItemKey(moneyKey)
				&& this.data.hasItemKey(summnyKey) && currid != null;
		if (!flag) {
			return;
		}
		SummnyMoneyTaxFormula formula = new SummnyMoneyTaxFormula(this.data,
				this.scale, true);
		formula.calculateMoney();
	}

//	private void calculateTaxPriorTax() {
//		String summnyKey = this.item.getNorigtaxmnyKey();
//		String taxKey = this.item.getNorigtaxKey();
//		String taxRateKey = this.item.getNtaxrateKey();
//		String ftaxtypeflag = this.item.getFtaxtypeflagKey();
//		String curridKey = this.item.getCorigcurrencyidKey();
//		Object currid = this.data.getAttributeValue(curridKey);
//		boolean flag = this.data.hasItemKey(summnyKey)
//				&& this.data.hasItemKey(taxRateKey)
//				&& this.data.hasItemKey(taxRateKey) && currid != null;
//		if (!flag) {
//			return;
//		}
//		TaxTaxRateSummnyFormula formula = new TaxTaxRateSummnyFormula(
//				this.data, this.scale, summnyKey, taxKey, taxRateKey,
//				ftaxtypeflag, curridKey);
//		formula.calculateTax();
//	}

	/*
	 * 主单位原币价税合计=主单位原币含税净价*主数量
	 */
	private void calculateTaxPriorTaxMny() {
		String taxNetPricekey = this.item.getNorigtaxnetpriceKey();
		String numKey = this.item.getNnumKey();
		String summnyKey = this.item.getNorigtaxmnyKey();
		String curridKey = this.item.getCorigcurrencyidKey();
		String cunitidKey = this.item.getCunitidKey();
		Object currid = this.data.getAttributeValue(cunitidKey);
		Object cunitid = this.data.getAttributeValue(curridKey);
		boolean flag = this.data.hasItemKey(taxNetPricekey)
				&& this.data.hasItemKey(numKey)
				&& this.data.hasItemKey(summnyKey) && currid != null
				&& cunitid != null;
		if (!flag) {
			return;
		}
		TaxNetPriceSummnyNumFormula formula = new TaxNetPriceSummnyNumFormula(
				this.data, this.scale, taxNetPricekey, numKey, summnyKey,
				curridKey, cunitidKey);
		formula.calculateSummny();
	}

	private boolean isNeedCalPrice(Condition cond) {
		if (cond.getUnitPriorType() == Condition.QT_PRIOR) {
			boolean isTaxOrNet = cond.getIsTaxOrNet();
			if (isTaxOrNet) {
				UFDouble summny = (UFDouble) this.data.getAttributeValue(cond
						.getRelationItem().getNorigtaxmnyKey());
				UFDouble taxNetPrice = (UFDouble) this.data
						.getAttributeValue(cond.getRelationItem()
								.getNqtorigtaxnetprcKey());
				if (taxNetPrice == null && summny != null) {
					return true;
				}
			} else {
				UFDouble mny = (UFDouble) this.data.getAttributeValue(cond
						.getRelationItem().getNorigmnyKey());
				UFDouble netPrice = (UFDouble) this.data.getAttributeValue(cond
						.getRelationItem().getNqtorignetpriceKey());
				if (netPrice == null && mny != null) {
					return true;
				}
			}

		} else {
			boolean isTaxOrNet = cond.getIsTaxOrNet();
			if (isTaxOrNet) {
				UFDouble summny = (UFDouble) this.data.getAttributeValue(cond
						.getRelationItem().getNorigtaxmnyKey());
				UFDouble taxNetPrice = (UFDouble) this.data
						.getAttributeValue(cond.getRelationItem()
								.getNorigtaxnetpriceKey());
				if (taxNetPrice == null && summny != null) {
					return true;
				}
			} else {
				UFDouble mny = (UFDouble) this.data.getAttributeValue(cond
						.getRelationItem().getNorigmnyKey());
				UFDouble netPrice = (UFDouble) this.data.getAttributeValue(cond
						.getRelationItem().getNorignetpriceKey());
				if (netPrice == null && mny != null) {
					return true;
				}
			}

		}
		return false;
	}

	private boolean isQtUinitEquelsAssUnit() {
		return this.data.getCqtunitid() != null
				&& this.data.getCastunitid() != null
				&& this.data.getCqtunitid().equals(this.data.getCastunitid());
	}

	private void setNoPermitModifyColumn() {
		this.data.putNoPermitChangeKey(this.item.getNitemdiscountrateKey());
	}
}
