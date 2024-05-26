package nc.vo.pubapp.calculator.strategy;

import nc.bs.logging.Logger;
import nc.pubitf.uapbd.CurrencyRateUtil;
import nc.pubitf.uapbd.CurrencyRateUtilHelper;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.calculator.CalculatorUtil;
import nc.vo.pubapp.calculator.Condition;
import nc.vo.pubapp.calculator.data.CacheDataSet;
import nc.vo.pubapp.calculator.data.IRelationForItems;
import nc.vo.pubapp.calculator.formula.SummnyMoneyTaxFormula;
import nc.vo.pubapp.calculator.formula.TaxTaxRateMoneyFormula;
import nc.vo.pubapp.calculator.formula.TaxTaxRateSummnyFormula;
import nc.vo.pubapp.calculator.strategy.local.LocalMnyCalculator;
import nc.vo.pubapp.calculator.strategy.local.LocalTaxMnyCalculator;
import nc.vo.pubapp.scale.ScaleUtils;

import org.apache.commons.lang.StringUtils;

public class LocalCalculator implements IStrategyForCal {
  private CacheDataSet data;

  private IRelationForItems item;

  private ScaleUtils scale;

  // private Condition cond;

  public LocalCalculator(CacheDataSet data, ScaleUtils scale) {
    this.data = data;
    this.item = this.data.getRelationForItem();
    this.scale = scale;
  }

  public void calTaxRelation() {
    // 记税金额
    this.calculateNCalTaxMny();
    // 主单位本币税额
    this.calculateNTax();
    // 本币不可抵扣税额
    this.calculateNDeductibleTax();
    // 记成本金额
    this.calculateNCostmny();

  }

  public void calculateNCostmny() {
    String summnyKey = this.item.getNtaxmnyKey();
    String moneyKey = this.item.getNmnyKey();
    String taxKey = this.item.getNtaxKey();
    String costMnyKey = this.item.getNcostmnyKey();
    String curridKey = this.item.getCcurrencyidKey();
    Object currid = this.data.getAttributeValue(curridKey);
    boolean flag =
        this.data.hasItemKey(taxKey) && this.data.hasItemKey(moneyKey)
            && this.data.hasItemKey(summnyKey) && (currid != null)
            && this.data.hasItemKey(costMnyKey);
    if (!flag) {
      return;
    }
    SummnyMoneyTaxFormula formula = new SummnyMoneyTaxFormula(this.data, this.scale, false);
    formula.calculateCalCostMny();
  }

  public void calculateNCalTaxMny() {
    String summnyKey = this.item.getNtaxmnyKey();
    String moneyKey = this.item.getNmnyKey();
    String taxKey = this.item.getNtaxKey();
    String calTaxMny = this.item.getNcaltaxmnyKey();
    String curridKey = this.item.getCcurrencyidKey();
    Object currid = this.data.getAttributeValue(curridKey);
    boolean flag =
        this.data.hasItemKey(taxKey) && this.data.hasItemKey(moneyKey)
            && this.data.hasItemKey(summnyKey) && (currid != null)
            && this.data.hasItemKey(calTaxMny);
    if (!flag) {
      return;
    }
    SummnyMoneyTaxFormula formula = new SummnyMoneyTaxFormula(this.data, this.scale, false);
    if (this.data.getCondition().getIsTaxOrNet()) {
      formula.calculateCalMnyFromSummny();
    } else {
      formula.calculateCalMnyFromMny();
    }
  }

  public void calculateNDeductibleTax() {
    String summnyKey =
        this.data.getCondition().getIsTaxOrNet() ? this.item.getNtaxmnyKey() : this.item
            .getNmnyKey();
    String taxKey = this.item.getNdeductibletaxKey();
    String taxRateKey = this.item.getNdeductibleTaxrateKey();
    String ftaxtypeflag = this.item.getFtaxtypeflagKey();
    String curridKey = this.item.getCcurrencyidKey();
    Object currid = this.data.getAttributeValue(curridKey);
    boolean flag =
        this.data.hasItemKey(summnyKey) && this.data.hasItemKey(taxRateKey) && (currid != null);
    if (!flag) {
      return;
    }


    if (this.data.getCondition().getIsTaxOrNet()) {
      // 3.5.4.2 金额-根据本币价税合计计算其他本币金额
      TaxTaxRateSummnyFormula formula =
          new TaxTaxRateSummnyFormula(this.data, this.scale, summnyKey, taxKey, taxRateKey,
              ftaxtypeflag, curridKey);
      formula.calculateDeductibleTax();
    } else {
      // 3.5.4.1 金额-根据本币无税金额计算其他本币金额
      TaxTaxRateMoneyFormula formula =
          new TaxTaxRateMoneyFormula(this.data, this.scale, summnyKey, taxKey, taxRateKey,
              ftaxtypeflag, curridKey);
      formula.calculateDeductibleTax();
    }
  }

  public void calculateNTax() {
    String summnyKey =
        this.data.getCondition().getIsTaxOrNet() ? this.item.getNtaxmnyKey() : this.item
            .getNmnyKey();
    String taxKey = this.item.getNtaxKey();
    String taxRateKey = this.item.getNtaxrateKey();
    String ftaxtypeflag = this.item.getFtaxtypeflagKey();
    String curridKey = this.item.getCcurrencyidKey();
    Object currid = this.data.getAttributeValue(curridKey);
    boolean flag =
        this.data.hasItemKey(summnyKey) && this.data.hasItemKey(taxRateKey)
            && this.data.hasItemKey(taxRateKey) && (currid != null);
    if (!flag) {
      return;
    }

    if (this.data.getCondition().getIsTaxOrNet()) {
      // 3.5.4.2 金额-根据本币价税合计计算其他本币金额
      TaxTaxRateSummnyFormula formula =
          new TaxTaxRateSummnyFormula(this.data, this.scale, summnyKey, taxKey, taxRateKey,
              ftaxtypeflag, curridKey);
      formula.calculateTax();
    } else {
      // 3.5.4.1 金额-根据本币无税金额计算其他本币金额
      TaxTaxRateMoneyFormula formula =
          new TaxTaxRateMoneyFormula(this.data, this.scale, summnyKey, taxKey, taxRateKey,
              ftaxtypeflag, curridKey);
      formula.calculateTax();
    }
  }

  @Override
  public void calculate(Condition cond) {
      Object attributeValue1 = this.data.getAttributeValue("nnum");

    if (this.isSameCurrencyId()) {
      this.data.commit();
      this.cloneValueFromOrigValue();
      Object attributeValue = this.data.getAttributeValue("nnum");
      // // 记税金额
      // this.calculateNCalTaxMny();
      // // 主单位本币税额
      // this.calculateNTax();

      // 需求变更2012-3-23
      if (this.data.getCondition().getIsTaxOrNet()) {
        // 无税金额 = 本币无税金额（见本币金额的算法）
        new LocalTaxMnyCalculator(this.data, this.scale).calculate(cond);
        this.data.setAttributeValue(this.item.getNorigmnyKey(), this.data.getNmny());
      } else {
        // 价税合计 = 本币价税合计（见本币金额的算法）
        new LocalMnyCalculator(this.data, this.scale).calculate(cond);
        this.data.setAttributeValue(this.item.getNorigtaxmnyKey(), this.data.getNtaxmny());
      }
      Object attributeValue2 = this.data.getAttributeValue("nassitnum");
      // 本币不可抵扣税额
      // this.calculateNDeductibleTax();
      // 记成本金额
      // this.calculateNCostmny();
      return;
    }
    if (cond.getIsTaxOrNet()) {
      this.calculateNtaxMny();
      // this.calculateLocalPriceFromOrigPrize(this.data.getNtaxmny(), cond);
      new LocalTaxMnyCalculator(this.data, this.scale).calculate(cond);
    } else {
      this.calculateNmy();
      // this.calculateLocalPriceFromOrigPrize(this.data.getNmny(), cond);
      new LocalMnyCalculator(this.data, this.scale).calculate(cond);
    }
    // if (item.getNorigtaxKey().equals(cond.getChangeKey())
    // || item.getNtaxKey().equals(cond.getChangeKey())){
    // this.calculateNtax();
    // calculateFormTax(cond);
    // }else{
    // this.cond = cond;
    // // 计算本币无税金额
    // this.calculateNmy();
    // // 计算本币价税合计
    // this.calculateNtaxMny();
    // // 计算本质折扣额
    // this.calculateNdisCount();
    // // 计算本币税额
    // this.calculateNtax();
    // // 计算本币无税单价
    // this.calculateNprice();
    // // 计算本币含税单价
    // this.calculateNtaxPrice();
    // // 计算本币无税净价
    // this.calculateNnetprice();
    // // 计算本币含税净价
    // this.calculateNtaxNetPrice();
    // this.calculateQtUnit();
    // }

  }

  public void calculateFormTax(Condition cond) {
    if (StringUtils.equalsIgnoreCase(this.item.getNtaxKey(), cond.getChangeKey())) {
      if (cond.getIsTaxOrNet()) {
        // 计算本币价税合计
        this.calculateNtaxMny();
        this.calculateMny();
      } else {
        // 计算本币无税金额
        this.calculateNmy();
        this.calculateTaxMny();
      }
    }
  }

  public void cloneValueFromOrigValue() {

    if (this.data.getCondition().getIsTaxOrNet()) {
      this.data.setNtaxmny(this.data.getNorigtaxmny());
    } else {
      this.data.setNmny(this.data.getNorigmny());
    }

    this.data.setNdiscount(this.data.getNorigdiscount());
    // this.data.setNtax(this.data.getNorigtax());

    // 报价单价
    this.data.setNqttaxnetprice(this.data.getNqtorigtaxnetprc());
    this.data.setNqttaxprice(this.data.getNqtorigtaxprice());
    this.data.setNqtnetprice(this.data.getNqtorignetprice());
    this.data.setNqtprice(this.data.getNqtorigprice());

    // 主单价
    this.data.setNtaxnetprice(this.data.getNorigtaxnetprice());
    this.data.setNtaxprice(this.data.getNorigtaxprice());
    this.data.setNnetprice(this.data.getNorignetprice());
    this.data.setNprice(this.data.getNorigprice());

  }

  private void calculateMny() {
    String summnyKey = this.item.getNtaxmnyKey();
    String moneyKey = this.item.getNmnyKey();
    String taxKey = this.item.getNtaxKey();
    String curridKey = this.item.getCcurrencyidKey();
    Object currid = this.data.getAttributeValue(curridKey);
    boolean flag =
        this.data.hasItemKey(taxKey) && this.data.hasItemKey(moneyKey)
            && this.data.hasItemKey(summnyKey) && (currid != null);
    if (!flag) {
      return;
    }
    SummnyMoneyTaxFormula formula = new SummnyMoneyTaxFormula(this.data, this.scale, false);
    formula.calculateMoney();
  }

  /*
   * 计算本币无税金额
   */
  private void calculateNmy() {
    String nmyKey = this.item.getNmnyKey();
    String origNmyKey = this.item.getNorigmnyKey();
    boolean flag =
        this.data.hasItemKey(origNmyKey) && this.data.hasItemKey(nmyKey)
            && this.isEnableCalculator();
    if (!flag) {
      return;
    }
    UFDouble nmy = this.data.getNorigmny();
    nmy = this.getCurrenyMoney(nmy);
    this.data.setNmny(nmy);
  }

  /*
   * 计算本币价税合计
   */
  private void calculateNtaxMny() {
    String taxMnyKey = this.item.getNtaxmnyKey();
    String origTaxMnyKey = this.item.getNorigtaxmnyKey();
    boolean flag =
        this.data.hasItemKey(taxMnyKey) && this.data.hasItemKey(origTaxMnyKey)
            && this.isEnableCalculator();
    if (!flag) {
      return;
    }
    UFDouble taxMny = this.data.getNorigtaxmny();
    taxMny = this.getCurrenyMoney(taxMny);
    this.data.setNtaxmny(taxMny);
  }

  private void calculateTaxMny() {
    String moneyKey = this.item.getNmnyKey();
    String taxKey = this.item.getNtaxKey();
    String summnyKey = this.item.getNtaxmnyKey();
    String curridKey = this.item.getCcurrencyidKey();
    Object currid = this.data.getAttributeValue(curridKey);
    boolean flag =
        this.data.hasItemKey(moneyKey) && this.data.hasItemKey(taxKey)
            && this.data.hasItemKey(summnyKey) && (currid != null);
    if (!flag) {
      return;
    }
    SummnyMoneyTaxFormula formula = new SummnyMoneyTaxFormula(this.data, this.scale, false);
    formula.calculateSummny();
  }

  /*
   * 通过原币金额获得相应的本币金额
   */
  private UFDouble getCurrenyMoney(UFDouble value) {
    try {
      String orgLocalCurrency = this.data.getCcurrencyid();
      String corigccurrencyid = this.data.getCorigcurrencyid();
      String pk_org = this.data.getPk_org();
      UFDate date = this.data.getBillDate();
      UFDouble rate = this.data.getNexchangerate();
      if (rate == null && data.hasItemKey(this.item.getNexchangerateKey())) {
        return null;
      }
      String orgExrateSchemeID =
          CurrencyRateUtilHelper.getInstance().getExrateschemeByOrgID(pk_org);
      if (orgLocalCurrency == null) {
        orgLocalCurrency = CurrencyRateUtilHelper.getInstance().getLocalCurrtypeByOrgID(pk_org);
      }
      CurrencyRateUtil util = CurrencyRateUtil.getInstanceByExrateScheme(orgExrateSchemeID);
      UFDouble[] convertMoney = null;
      try {
        convertMoney =
            util.getAmountsByOpp(corigccurrencyid, orgLocalCurrency, new UFDouble[] {value}, rate,
                date);
      } catch (BusinessException e) {
        Logger.error(e);
      }

      if ((convertMoney != null) && (convertMoney.length > 0)) {
        // value = convertMoney[0];
        return convertMoney[0];
      }

    } catch (Exception e) {
      UFDouble rate = this.data.getNexchangerate();
      if (rate != null) {
        value = CalculatorUtil.multiply(value, rate);
      }
      Logger.error(e);
    }
    return value;
  }

  // /*
  // * 计算本币税额
  // */
  // private void calculateNtax() {
  // String taxKey = this.item.getNtaxKey();
  // String origTaxKey = this.item.getNorigtaxKey();
  // boolean flag = this.data.hasItemKey(taxKey)
  // && this.data.hasItemKey(origTaxKey)
  // && this.isEnableCalculator();
  // if (!flag) {
  // return;
  // }
  // UFDouble tax = this.data.getNorigtax();
  // tax = this.getCurrenyMoney(tax);
  // this.data.setNtax(tax);
  // }
  //
  // /*
  // * 计算本币折扣额
  // */
  // private void calculateNdisCount() {
  // String discountKey = this.item.getNdiscountKey();
  // String origDiscountKey = this.item.getNorigdiscountKey();
  // boolean flag = this.data.hasItemKey(discountKey)
  // && this.data.hasItemKey(origDiscountKey)
  // && this.isEnableCalculator();
  // if (!flag) {
  // return;
  // }
  // UFDouble discount = this.data.getNorigdiscount();
  // discount = this.getCurrenyMoney(discount);
  // this.data.setNdiscount(discount);
  // }
  //
  // /*
  // * 计算本币无税单价
  // */
  // private void calculateNprice() {
  // String numKey = this.item.getNnumKey();
  // String taxMnyKey = this.item.getNtaxmnyKey();
  // String taxRateKey = this.item.getNtaxrateKey();
  // String priceKey = this.item.getNpriceKey();
  // boolean flag = this.data.hasItemKey(numKey)
  // && this.data.hasItemKey(priceKey)
  // && this.data.hasItemKey(taxRateKey)
  // && this.data.hasItemKey(taxMnyKey);
  // if (!flag) {
  // return;
  // }
  // UFDouble price = new UFDouble(0.0);
  // UFDouble taxMny = this.data.getNtaxmny();
  // if (this.data.hasItemKey(this.item.getNdiscountKey())) {
  // UFDouble discount = this.data.getNdiscount();
  // taxMny = CalculatorUtil.add(taxMny, discount);
  // }
  // UFDouble num = this.data.getNnum();
  // UFDouble taxRate = this.data.getNtaxrate();
  // taxRate = CalculatorUtil.add(CalculatorUtil.div(taxRate, new UFDouble(
  // 100)), new UFDouble(1));
  // taxRate = CalculatorUtil.multiply(taxRate, num);
  // int d = MathTool.compareTo(taxRate, UFDouble.ZERO_DBL);
  // if (d != 0) {
  // price = CalculatorUtil.div(taxMny, taxRate);
  // }
  // price = this.scale.adjustSoPuPriceScale(price);
  // this.data.setNprice(price);
  // }
  //
  // /*
  // * 计算本币含税单价
  // */
  // private void calculateNtaxPrice() {
  // String numKey = this.item.getNnumKey();
  // String taxMnyKey = this.item.getNtaxmnyKey();
  // String taxPriceKey = this.item.getNtaxpriceKey();
  // boolean flag = this.data.hasItemKey(numKey)
  // && this.data.hasItemKey(taxPriceKey)
  // && this.data.hasItemKey(taxMnyKey);
  // if (!flag) {
  // return;
  // }
  // UFDouble taxPrice = new UFDouble(0.0);
  // UFDouble taxMny = this.data.getNtaxmny();
  // if (this.data.hasItemKey(this.item.getNdiscountKey())) {
  // UFDouble discount = this.data.getNdiscount();
  // taxMny = CalculatorUtil.add(taxMny, discount);
  // }
  // UFDouble num = this.data.getNnum();
  // int d = MathTool.compareTo(num, UFDouble.ZERO_DBL);
  // if (d != 0) {
  // taxPrice = CalculatorUtil.div(taxMny, num);
  // }
  // taxPrice = this.scale.adjustSoPuPriceScale(taxPrice);
  // this.data.setNtaxprice(taxPrice);
  // }
  //
  // /*
  // * 计算本币无税净价
  // */
  // private void calculateNnetprice() {
  // String numKey = this.item.getNnumKey();
  // String nmyKey = this.item.getNmnyKey();
  // String netPriceKey = this.item.getNnetpriceKey();
  // boolean flag = this.data.hasItemKey(numKey)
  // && this.data.hasItemKey(netPriceKey)
  // && this.data.hasItemKey(nmyKey);
  // if (!flag) {
  // return;
  // }
  // UFDouble netPrice = new UFDouble(0.0);
  // UFDouble nmy = this.data.getNmny();
  // UFDouble num = this.data.getNnum();
  // int d = MathTool.compareTo(num, UFDouble.ZERO_DBL);
  // if (d != 0) {
  // netPrice = CalculatorUtil.div(nmy, num);
  // }
  // netPrice = this.scale.adjustSoPuPriceScale(netPrice);
  // this.data.setNnetprice(netPrice);
  // }
  //
  // /*
  // * 计算本币含税净价
  // */
  // private void calculateNtaxNetPrice() {
  // String numKey = this.item.getNnumKey();
  // String taxNmyKey = this.item.getNtaxmnyKey();
  // String taxNetPriceKey = this.item.getNtaxnetpriceKey();
  // boolean flag = this.data.hasItemKey(numKey)
  // && this.data.hasItemKey(taxNetPriceKey)
  // && this.data.hasItemKey(taxNmyKey);
  // if (!flag) {
  // return;
  // }
  // UFDouble taxNetPrice = new UFDouble(0.0);
  // UFDouble taxNmy = this.data.getNtaxmny();
  // UFDouble num = this.data.getNnum();
  // int d = MathTool.compareTo(num, UFDouble.ZERO_DBL);
  // if (d != 0) {
  // taxNetPrice = CalculatorUtil.div(taxNmy, num);
  // }
  // taxNetPrice = this.scale.adjustSoPuPriceScale(taxNetPrice);
  // this.data.setNtaxnetprice(taxNetPrice);
  // }
  //
  // /*
  // * 计算报价计量单位相应的价格
  // */
  // private void calculateQtUnit() {
  // // 计算本币报价无税单价
  // this.calculateNqtPrice();
  // // 计算本币报价含税单价
  // this.calculateNqtTaxPrice();
  // // 计算本币报价无税净价
  // this.calculateNqtNetPrice();
  // // 计算本币报价含税净价
  // this.calculateNqtTaxNetPrice();
  // }

  // /*
  // * 计算本币报价无税单价
  // */
  // private void calculateNqtPrice() {
  // String qtNumKey = this.item.getNqtunitnumKey();
  // String taxMnyKey = this.item.getNtaxmnyKey();
  // String taxRateKey = this.item.getNtaxrateKey();
  // String qtPriceKey = this.item.getNqtpriceKey();
  // boolean flag = this.data.hasItemKey(qtNumKey)
  // && this.data.hasItemKey(qtPriceKey)
  // && this.data.hasItemKey(taxRateKey)
  // && this.data.hasItemKey(taxMnyKey);
  // if (!flag) {
  // return;
  // }
  // UFDouble qtPrice = new UFDouble(0.0);
  // UFDouble taxMny = this.data.getNtaxmny();
  // if (this.data.hasItemKey(this.item.getNdiscountKey())) {
  // UFDouble discount = this.data.getNdiscount();
  // taxMny = CalculatorUtil.add(taxMny, discount);
  // }
  // UFDouble num = this.data.getNqtunitnum();
  // UFDouble taxRate = this.data.getNtaxrate();
  // taxRate = CalculatorUtil.add(CalculatorUtil.div(taxRate, new UFDouble(
  // 100)), new UFDouble(1));
  // taxRate = CalculatorUtil.multiply(taxRate, num);
  // int d = MathTool.compareTo(taxRate, UFDouble.ZERO_DBL);
  // if (d != 0) {
  // qtPrice = CalculatorUtil.div(taxMny, taxRate);
  // }
  // qtPrice = this.scale.adjustSoPuPriceScale(qtPrice);
  // this.data.setNqtprice(qtPrice);
  // }
  //
  // /*
  // * 本币报价含税单价
  // */
  // private void calculateNqtTaxPrice() {
  // String qtNumKey = this.item.getNqtunitnumKey();
  // String taxMnyKey = this.item.getNtaxmnyKey();
  // String qtTaxPriceKey = this.item.getNqttaxpriceKey();
  // boolean flag = this.data.hasItemKey(qtNumKey)
  // && this.data.hasItemKey(qtTaxPriceKey)
  // && this.data.hasItemKey(taxMnyKey);
  // if (!flag) {
  // return;
  // }
  // UFDouble qtTaxPrice = new UFDouble(0.0);
  // UFDouble taxMny = this.data.getNtaxmny();
  // if (this.data.hasItemKey(this.item.getNdiscountKey())) {
  // UFDouble discount = this.data.getNdiscount();
  // taxMny = CalculatorUtil.add(taxMny, discount);
  // }
  // UFDouble num = this.data.getNqtunitnum();
  // int d = MathTool.compareTo(num, UFDouble.ZERO_DBL);
  // if (d != 0) {
  // qtTaxPrice = CalculatorUtil.div(taxMny, num);
  // }
  // qtTaxPrice = this.scale.adjustSoPuPriceScale(qtTaxPrice);
  // this.data.setNqttaxprice(qtTaxPrice);
  // }
  //
  // /*
  // * 本币报价无税净价
  // */
  // private void calculateNqtNetPrice() {
  // String qtNumKey = this.item.getNqtunitnumKey();
  // String nmyKey = this.item.getNmnyKey();
  // String qtNetPriceKey = this.item.getNqtnetpriceKey();
  // boolean flag = this.data.hasItemKey(qtNumKey)
  // && this.data.hasItemKey(qtNetPriceKey)
  // && this.data.hasItemKey(nmyKey);
  // if (!flag) {
  // return;
  // }
  // UFDouble qtNetPrice = new UFDouble(0.0);
  // UFDouble nmy = this.data.getNmny();
  // UFDouble num = this.data.getNqtunitnum();
  // int d = MathTool.compareTo(num, UFDouble.ZERO_DBL);
  // if (d != 0) {
  // qtNetPrice = CalculatorUtil.div(nmy, num);
  // }
  // qtNetPrice = this.scale.adjustSoPuPriceScale(qtNetPrice);
  // this.data.setNqtnetprice(qtNetPrice);
  // }
  //
  // /*
  // * 本币报价含税净价
  // */
  // private void calculateNqtTaxNetPrice() {
  // String qtNumKey = this.item.getNqtunitnumKey();
  // String taxNmyKey = this.item.getNtaxmnyKey();
  // String qtTaxNetPriceKey = this.item.getNqttaxnetpriceKey();
  // boolean flag = this.data.hasItemKey(qtNumKey)
  // && this.data.hasItemKey(qtTaxNetPriceKey)
  // && this.data.hasItemKey(taxNmyKey);
  // if (!flag) {
  // return;
  // }
  // UFDouble qtTaxNetPrice = new UFDouble(0.0);
  // UFDouble taxNmy = this.data.getNtaxmny();
  // UFDouble qtNum = this.data.getNqtunitnum();
  // int d = MathTool.compareTo(qtNum, UFDouble.ZERO_DBL);
  // if (d != 0) {
  // qtTaxNetPrice = CalculatorUtil.div(taxNmy, qtNum);
  // }
  // qtTaxNetPrice = this.scale.adjustSoPuPriceScale(qtTaxNetPrice);
  // this.data.setNqttaxnetprice(qtTaxNetPrice);
  // }

  private boolean isEnableCalculator() {
    String ccurrenyid = this.data.getCcurrencyid();
    String corigcurrencyid = this.data.getCorigcurrencyid();
    String pk_org = this.data.getPk_org();
    UFDate date = this.data.getBillDate();
    boolean flag = true;
    if (this.data.getNexchangerate() == null) {
      flag =
          (ccurrenyid != null) && (corigcurrencyid != null) && (pk_org != null) && (date != null);
      if (!flag) {
        // TODO
        throw new RuntimeException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pubapp_0",
            "LocalToOriCalculator-0000")/* 汇率错误！ */);
      }
    }


    return flag;
  }

  private boolean isSameCurrencyId() {
    boolean isSame =
        (this.data.getCorigcurrencyid() != null) && (this.data.getCcurrencyid() != null)
            && this.data.getCorigcurrencyid().equals(this.data.getCcurrencyid());
    return isSame;
  }

  public void calculatePrice(String origPriceKey, String priceKey) {
    boolean flag =
        this.data.hasItemKey(priceKey) && this.data.hasItemKey(origPriceKey)
            && this.isEnableCalculator();
    if (!flag) {
      return;
    }
    UFDouble origPrice = (UFDouble) this.data.getAttributeValue(origPriceKey);
    origPrice = this.getCurrenyPrice(origPrice);
    // 本币精度
    origPrice = this.scale.adjustSoPuPriceScale(origPrice, this.data.getCcurrencyid());
    this.data.setAttributeValue(priceKey, origPrice);
  }


  /*
   * 通过原币单价获得相应的本币单价
   */
  private UFDouble getCurrenyPrice(UFDouble value) {
    try {
      String orgLocalCurrency = this.data.getCcurrencyid();
      String corigccurrencyid = this.data.getCorigcurrencyid();
      String pk_org = this.data.getPk_org();
      UFDate date = this.data.getBillDate();
      UFDouble rate = this.data.getNexchangerate();
      if (rate == null && data.hasItemKey(this.item.getNexchangerateKey())) {
        return null;
      }
      String orgExrateSchemeID =
          CurrencyRateUtilHelper.getInstance().getExrateschemeByOrgID(pk_org);
      if (orgLocalCurrency == null) {
        orgLocalCurrency = CurrencyRateUtilHelper.getInstance().getLocalCurrtypeByOrgID(pk_org);
      }
      CurrencyRateUtil util = CurrencyRateUtil.getInstanceByExrateScheme(orgExrateSchemeID);
      UFDouble[] convertMoney = null;
      try {
        convertMoney =
            util.getPriceByOpp(corigccurrencyid, orgLocalCurrency, new UFDouble[] {value}, rate,
                date);
      } catch (BusinessException e) {
        Logger.error(e);
      }

      if ((convertMoney != null) && (convertMoney.length > 0)) {
        // value = convertMoney[0];
        return convertMoney[0];
      }

    } catch (Exception e) {
      UFDouble rate = this.data.getNexchangerate();
      if (rate != null) {
        value = CalculatorUtil.multiply(value, rate);
      }
      Logger.error(e);
    }
    return value;
  }

}
