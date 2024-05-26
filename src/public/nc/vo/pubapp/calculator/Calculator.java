package nc.vo.pubapp.calculator;

import java.util.HashMap;
import java.util.Map;

import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pubapp.calculator.data.CacheDataSet;
import nc.vo.pubapp.calculator.data.IDataSetDollarForCal;
import nc.vo.pubapp.calculator.data.IDataSetForCal;
import nc.vo.pubapp.calculator.data.IRelationDollarForItems;
import nc.vo.pubapp.calculator.data.IRelationForItems;
import nc.vo.pubapp.calculator.data.VODataSetForCal;
import nc.vo.pubapp.calculator.formula.DollarAndPriceCalculator;
import nc.vo.pubapp.calculator.formula.DollarNorigPriceAndSummnyCalculator;
import nc.vo.pubapp.calculator.formula.DollarPriceAndSummnyCalculator;
import nc.vo.pubapp.calculator.strategy.AssistNumCalculator;
import nc.vo.pubapp.calculator.strategy.DiscountRateCalculator;
import nc.vo.pubapp.calculator.strategy.GlobalMoneyCalculator;
import nc.vo.pubapp.calculator.strategy.GroupMoneyCalculator;
import nc.vo.pubapp.calculator.strategy.IStrategyForCal;
import nc.vo.pubapp.calculator.strategy.LocalCalculator;
import nc.vo.pubapp.calculator.strategy.LocalGlobalGroupCalculator;
import nc.vo.pubapp.calculator.strategy.NchangeRateCalculator;
import nc.vo.pubapp.calculator.strategy.NumAssNumQtNumCalculator;
import nc.vo.pubapp.calculator.strategy.NumberCalculator;
import nc.vo.pubapp.calculator.strategy.OnlyPriceCalculator;
import nc.vo.pubapp.calculator.strategy.OrigMnyCalculator;
import nc.vo.pubapp.calculator.strategy.OrigNetPriceCalculator;
import nc.vo.pubapp.calculator.strategy.OrigPriceCalculator;
import nc.vo.pubapp.calculator.strategy.OrigTaxMnyCalculator;
import nc.vo.pubapp.calculator.strategy.OrigTaxNetPriceCalculator;
import nc.vo.pubapp.calculator.strategy.OrigTaxPriceCalculator;
import nc.vo.pubapp.calculator.strategy.QtNetPriceCalculator;
import nc.vo.pubapp.calculator.strategy.QtPriceCalculator;
import nc.vo.pubapp.calculator.strategy.QtTaxNetPriceCalculator;
import nc.vo.pubapp.calculator.strategy.QtTaxPriceCalculator;
import nc.vo.pubapp.calculator.strategy.QtUnitNumCalculator;
import nc.vo.pubapp.calculator.strategy.QtUnitRateCalculator;
import nc.vo.pubapp.calculator.strategy.TaxRateCalculator;
import nc.vo.pubapp.calculator.strategy.local.LocalAssistNumCalculator;
import nc.vo.pubapp.calculator.strategy.local.LocalCalTaxMnyCalculator;
import nc.vo.pubapp.calculator.strategy.local.LocalDeductibleTaxCalculator;
import nc.vo.pubapp.calculator.strategy.local.LocalDeductibleTaxRateCalculator;
import nc.vo.pubapp.calculator.strategy.local.LocalMnyCalculator;
import nc.vo.pubapp.calculator.strategy.local.LocalNchangeRateCalculator;
import nc.vo.pubapp.calculator.strategy.local.LocalNetPriceCalculator;
import nc.vo.pubapp.calculator.strategy.local.LocalNumberCalculator;
import nc.vo.pubapp.calculator.strategy.local.LocalPriceCalculator;
import nc.vo.pubapp.calculator.strategy.local.LocalQtNetPriceCalculator;
import nc.vo.pubapp.calculator.strategy.local.LocalQtPriceCalculator;
import nc.vo.pubapp.calculator.strategy.local.LocalQtTaxNetPriceCalculator;
import nc.vo.pubapp.calculator.strategy.local.LocalQtTaxPriceCalculator;
import nc.vo.pubapp.calculator.strategy.local.LocalQtUnitNumCalculator;
import nc.vo.pubapp.calculator.strategy.local.LocalQtUnitRateCalculator;
import nc.vo.pubapp.calculator.strategy.local.LocalTaxCalculator;
import nc.vo.pubapp.calculator.strategy.local.LocalTaxMnyCalculator;
import nc.vo.pubapp.calculator.strategy.local.LocalTaxNetPriceCalculator;
import nc.vo.pubapp.calculator.strategy.local.LocalTaxPriceCalculator;
import nc.vo.pubapp.calculator.strategy.local.LocalTaxRateCalculator;
import nc.vo.pubapp.calculator.strategy.local.LocalToOriCalculator;
import nc.vo.pubapp.scale.ScaleUtils;

import org.apache.commons.lang.StringUtils;

/**
 * Description: 单价金额计算时，调用的接口
 * 
 * @author zhuhz
 * @version 1.0
 */
public class Calculator {
  private CircularlyAccessibleValueObject[] currVO;

  // 计算要用的数据源
  private CacheDataSet data;

  private IRelationForItems item;

  // 存储变化itemkey和其对应的处理策略的实例对象（本币系列）
  private Map<String, IStrategyForCal> localmap = new HashMap<String, IStrategyForCal>();

  // 存储变化itemkey和其对应的处理策略的实例对象（原币系列）
  private Map<String, IStrategyForCal> map = new HashMap<String, IStrategyForCal>();

  // 计算中要用的精度
  private ScaleUtils scale;

  public Calculator(CircularlyAccessibleValueObject[] currVO, IRelationForItems item) {
    this.currVO = currVO;
    this.item = item;
  }

  public Calculator(IDataSetForCal data, ScaleUtils scale) {
    this.data = new CacheDataSet(data);
    this.scale = scale;
    // 初始化变化的itemkey对应的策略
    this.initStrategy();
    this.initLocalStrategy();
  }

  /**
   * 方法功能描述：用于单价金额计算的入口方法
   * 
   * @param cond初始化参数条件
   * @param key变化的itemkey
   * @author zhuhz
   */
  public void calculate(Condition cond, String key) {
    this.wrapCondtion(cond, key);
    IRelationForItems lItem = this.data.getRelationForItem();
    // cond.setUnitPriorType(Condition.MAIN_PRIOR);
    IStrategyForCal strategy = this.map.get(key);
    if ((strategy != null || ((StringUtils.equalsIgnoreCase(key, lItem.getNexchangerateKey()))
        || (StringUtils.equalsIgnoreCase(key, lItem.getNglobalexchgrateKey())) || (StringUtils
          .equalsIgnoreCase(key, lItem.getNgroupexchgrateKey())))) && !cond.isCalLocalPior()) {
      if (!StringUtils.equalsIgnoreCase(key, lItem.getNexchangerateKey()) && strategy != null) {
        // cond.setIsChgPriceOrDiscount(cond.getIsChgPriceOrDiscount()&&cond.is)
        strategy.calculate(cond);
      }
      this.setNoPermitModifyColumn(key);
      if (StringUtils.equalsIgnoreCase(key, lItem.getNglobalexchgrateKey())) {

        GlobalMoneyCalculator cal = new GlobalMoneyCalculator(this.data, this.scale);
        cal.calculate(cond);
        this.data.commit();
        return;
      }
      if (StringUtils.equalsIgnoreCase(key, lItem.getNgroupexchgrateKey())) {
        GroupMoneyCalculator cal = new GroupMoneyCalculator(this.data, this.scale);
        cal.calculate(cond);
        this.data.commit();
        return;
      }
      this.calculateLocalGlobalGroup(cond);
    } else {
      strategy = this.localmap.get(key);
      if (strategy != null && !StringUtils.equalsIgnoreCase(key, lItem.getNexchangerateKey())) {
        // if (!key.equals(lItem.getNexchangerateKey())) {
        strategy.calculate(cond);
        // }

      }

      // 币种相同并且（有策略或者这本汇率变）
      if (strategy != null || StringUtils.equalsIgnoreCase(key, lItem.getNexchangerateKey())) {
        boolean isSame =
            (this.data.getCorigcurrencyid() != null)
                && (this.data.getCcurrencyid() != null)
                && StringUtils.equalsIgnoreCase(this.data.getCorigcurrencyid(),
                    this.data.getCcurrencyid());

        if (isSame
            || (cond.isCalOrigCurr()
                && !StringUtils.equalsIgnoreCase(key, lItem.getNdeductibletaxKey())
                && !StringUtils.equalsIgnoreCase(key, lItem.getNdeductibleTaxrateKey())
                && !StringUtils.equalsIgnoreCase(key, lItem.getNcaltaxmnyKey()) && !StringUtils
                  .equalsIgnoreCase(key, lItem.getNtaxKey()))) {
          this.setNoPermitModifyColumn(key);
          this.calculateOrigGlobalGroup(cond);
        }
      }

    }

    if ((this.data.getRelationForItem() instanceof IRelationDollarForItems)
        && (this.data.getDataset() instanceof IDataSetDollarForCal)) {

      // 如果key是美元相关字段，则不再重复进行美元计算
      IRelationDollarForItems dollarItem = (IRelationDollarForItems) this.data.getRelationForItem();
      boolean isDollarKey =
          (StringUtils.equalsIgnoreCase(key, dollarItem.getNusdmnyKey()))
              || (StringUtils.equalsIgnoreCase(key, dollarItem.getNqtusdpriceKey()))
              || (StringUtils.equalsIgnoreCase(key, dollarItem.getNusdpriceKey()));
      if (!isDollarKey) {
        this.calculateDollar(cond);
      }
    }
    // DollarAndPriceFormula daf = new
    // DollarAndPriceFormula(this.data,this.scale);
    // daf.calculate(cond);
    // CheckIsResultRight check = new CheckIsResultRight(data);
    // check.checkResult();
    // 没有问题，将数据写入到真正的数据源中
    this.data.commit();
  }

  public void calculate(Condition cond, String key, ScaleUtils lscale) {
    for (CircularlyAccessibleValueObject element : this.currVO) {
      IDataSetForCal dataset = new VODataSetForCal(element, this.item);
      this.data = new CacheDataSet(dataset);
      this.scale = lscale;
      if (0 == this.map.size()) {
        this.initStrategy();
        this.initLocalStrategy();
      }
      this.calculate(cond, key);
    }
  }

  public void commit() {
    this.data.commit();
  }

  /**
   * 只进行全局本位币计算时 调用的方法
   */
  public void calculateGlobalMny(Condition cond) {
    if (cond.getChangeKey() == null) {
      cond.setChangeKey(this.data.getRelationForItem().getNexchangerateKey());
    }
    cond.setRelationItem(this.data.getRelationForItem());
    this.data.setCondition(cond);
    IStrategyForCal global = new GlobalMoneyCalculator(this.data, this.scale);
    global.calculate(cond);
    this.data.commit();
  }

  /**
   * 只进行集团本位币计算时 调用的方法
   */
  public void calculateGroupMny(Condition cond) {
    if (cond.getChangeKey() == null) {
      cond.setChangeKey(this.data.getRelationForItem().getNexchangerateKey());
    }
    cond.setRelationItem(this.data.getRelationForItem());
    this.data.setCondition(cond);
    IStrategyForCal group = new GroupMoneyCalculator(this.data, this.scale);
    group.calculate(cond);
    this.data.commit();
  }

  /**
   * 只进行本币计算时 调用的方法
   */
  public void calculateLocalCurrenyMny(Condition cond) {
    this.data.setCondition(cond);
    if (cond.getChangeKey() == null) {
      cond.setChangeKey(this.data.getRelationForItem().getNexchangerateKey());
    }
    cond.setRelationItem(this.data.getRelationForItem());
    IStrategyForCal local = new LocalCalculator(this.data, this.scale);
    local.calculate(cond);
    this.data.commit();
  }

  /** 计算美元金额 */
  public void calculateDollar(Condition cond) {
    DollarAndPriceCalculator dollarAndPrice = new DollarAndPriceCalculator(this.data, this.scale);
    dollarAndPrice.calculateDollarAllPrice();
  }

  /**
   * 计算本币金额 计算全局本位币金额 计算集团本位币金额
   */
  public void calculateLocalGlobalGroup(Condition cond) {
    if (StringUtils.equalsIgnoreCase(data.getRelationForItem().getNchangerateKey(),
        cond.getChangeKey())
        && !StringUtils.equalsIgnoreCase(this.data.getCastunitid(), data.getCqtunitid())) {
      return;
    }
    // 7.17单价联动
    if (StringUtils.equalsIgnoreCase(cond.getChangeKey(), this.data.getRelationForItem()
        .getNexchangerateKey())) {
      OnlyPriceCalculator onlyPriceCalculator = new OnlyPriceCalculator(this.data, this.scale);
      onlyPriceCalculator.calculate(cond);
    }

    boolean isSame =
        (this.data.getCorigcurrencyid() != null)
            && (this.data.getCcurrencyid() != null)
            && StringUtils.equalsIgnoreCase(this.data.getCorigcurrencyid(),
                this.data.getCcurrencyid());

    // 国际业务并且编辑的是税率，不同步本币
    boolean taxInternationalRate =
        cond.isInternational()
            && StringUtils.equalsIgnoreCase(cond.getChangeKey(), this.data.getRelationForItem()
                .getNtaxrateKey());
    if (taxInternationalRate) {
      return;
    }

    if (cond.getIsCalLocalCurr() || isSame) {
      if (cond.getChangeKey() == null) {
        cond.setChangeKey(this.data.getRelationForItem().getNexchangerateKey());
      }
      this.data.setCondition(cond);
      cond.setRelationItem(this.data.getRelationForItem());
      IStrategyForCal local = new LocalGlobalGroupCalculator(this.data, this.scale);
      Object attributeValue = this.data.getAttributeValue("nnum");
      Object attributeValue2 = this.data.getAttributeValue("nassistnum");
      this.data.commit();
      local.calculate(cond);
      Object attributeValue3 = this.data.getAttributeValue("nnum");
      Object attributeValue4 = this.data.getAttributeValue("nassistnum");
    }
    Object attributeValue5 = this.data.getAttributeValue("nnum");
    Object attributeValue6 = this.data.getAttributeValue("nassistnum");
    this.data.commit();
  }

  /*
   * 只进行数量计算如果是换算率驱动会连带算金额和价格
   */
  public void calculateNumAssNumQtNum(Condition cond, String changeKey) {
    this.wrapCondtion(cond, changeKey);
    IStrategyForCal strategy = new NumAssNumQtNumCalculator(this.data, this.scale, changeKey);
    strategy.calculate(cond);
    this.data.commit();
  }

  /*
   * 只进行数量计算
   */
  public void calculateOnlyNumAssNumQtNum(Condition cond, String changeKey) {
    this.wrapCondtion(cond, changeKey);
    this.data.setCondition(cond);
    NumAssNumQtNumCalculator strategy =
        new NumAssNumQtNumCalculator(this.data, this.scale, changeKey);
    strategy.calculateNumOnly(cond);
    this.data.commit();
  }

  /**
   * 计算原币币金额 计算全局本位币金额 计算集团本位币金额
   */
  public void calculateOrigGlobalGroup(Condition cond) {
    // 7.17单价联动
    if (StringUtils.equalsIgnoreCase(cond.getChangeKey(), this.data.getRelationForItem()
        .getNexchangerateKey())) {
      OnlyPriceCalculator onlyPriceCalculator = new OnlyPriceCalculator(this.data, this.scale);
      onlyPriceCalculator.calculate(cond);
    }

    this.data.setCondition(cond);
    IStrategyForCal local = new LocalToOriCalculator(this.data, this.scale);
    local.calculate(cond);
    // 全局集团暂时uap还不支持，故先注释掉
    local = new GlobalMoneyCalculator(this.data, this.scale);
    local.calculate(cond);
    local = new GroupMoneyCalculator(this.data, this.scale);
    local.calculate(cond);
    this.data.commit();
  }

  public void setStrategy(String key, IStrategyForCal relcal) {
    this.map.put(key, relcal);
  }

  public void wrapCondtion(Condition cond, String key) {
    this.data.setCondition(cond);
    cond.setChangeKey(key);
    cond.setRelationItem(this.data.getRelationForItem());
  }

  private void initLocalStrategy() {
    IRelationForItems lItem = this.data.getRelationForItem();
    // 变换的key是主单位本币无税金额
    String key = lItem.getNmnyKey();
    IStrategyForCal strategy;
    if (null != key) {
      strategy = new LocalMnyCalculator(this.data, this.scale);
      this.localmap.put(key, strategy);
    }
    // 变化的key是主单位本币价税合计
    key = lItem.getNtaxmnyKey();
    if (null != key) {
      strategy = new LocalTaxMnyCalculator(this.data, this.scale);
      this.localmap.put(key, strategy);
    }
    // 变换的key是主单位本币税额
    key = lItem.getNtaxKey();
    if (null != key) {
      strategy = new LocalTaxCalculator(this.data, this.scale);
      this.localmap.put(key, strategy);
    }

    // 变换的key是报价单位本币含税单价
    key = lItem.getNqttaxpriceKey();
    if (null != key) {
      strategy = new LocalQtTaxPriceCalculator(this.data, this.scale);
      this.localmap.put(key, strategy);
    }
    // 变换的key是报价单位本币含税净价
    key = lItem.getNqttaxnetpriceKey();
    if (null != key) {
      strategy = new LocalQtTaxNetPriceCalculator(this.data, this.scale);
      this.localmap.put(key, strategy);
    }
    // 变换的key是报价单位本币无税单价
    key = lItem.getNqtpriceKey();
    if (null != key) {
      strategy = new LocalQtPriceCalculator(this.data, this.scale);
      this.localmap.put(key, strategy);
    }
    // 变换的key是报价单位本币无税净价
    key = lItem.getNqtnetpriceKey();
    if (null != key) {
      strategy = new LocalQtNetPriceCalculator(this.data, this.scale);
      this.localmap.put(key, strategy);
    }

    // 变化的key是主单位本币含税单价
    key = lItem.getNtaxpriceKey();
    if (null != key) {
      strategy = new LocalTaxPriceCalculator(this.data, this.scale);
      this.localmap.put(key, strategy);
    }
    // 变换的key是主单位本币含税净价
    key = lItem.getNtaxnetpriceKey();
    if (null != key) {
      strategy = new LocalTaxNetPriceCalculator(this.data, this.scale);
      this.localmap.put(key, strategy);
    }
    // 变换的key是主单位本币无税单价
    key = lItem.getNpriceKey();
    if (null != key) {
      strategy = new LocalPriceCalculator(this.data, this.scale);
      this.localmap.put(key, strategy);
    }
    // 变换的key是主单位本币无税净价
    key = lItem.getNnetpriceKey();
    if (null != key) {
      strategy = new LocalNetPriceCalculator(this.data, this.scale);
      this.localmap.put(key, strategy);
    }
    // 变化的key是主数量
    key = lItem.getNnumKey();
    if (null != key) {
      strategy = new LocalNumberCalculator(this.data, this.scale);
      this.localmap.put(key, strategy);
    }
    // 变化的key是数量
    key = lItem.getNassistnumKey();
    if (null != key) {
      strategy = new LocalAssistNumCalculator(this.data, this.scale);
      this.localmap.put(key, strategy);
    }

    // 变化的key是报价单位数量
    key = lItem.getNqtunitnumKey();
    if (null != key) {
      strategy = new LocalQtUnitNumCalculator(this.data, this.scale);
      this.localmap.put(key, strategy);
    }

    // 变化的key是换算率
    key = lItem.getNchangerateKey();
    if (null != key) {
      strategy = new LocalNchangeRateCalculator(this.data, this.scale);
      this.localmap.put(key, strategy);
    }
    // 变化的key是报价单位换算率
    key = lItem.getNqtunitrateKey();
    if (null != key) {
      strategy = new LocalQtUnitRateCalculator(this.data, this.scale);
      this.localmap.put(key, strategy);
    }

    // 变化的key是不可抵扣税额
    key = lItem.getNdeductibletaxKey();
    if (null != key) {
      strategy = new LocalDeductibleTaxCalculator(this.data, this.scale);
      this.localmap.put(key, strategy);
    }

    // 变化的key是记税金额
    key = lItem.getNcaltaxmnyKey();
    if (null != key) {
      strategy = new LocalCalTaxMnyCalculator(this.data, this.scale);
      this.localmap.put(key, strategy);
    }

    // 变化的key是不可抵扣税率
    key = lItem.getNdeductibleTaxrateKey();
    if (null != key) {
      strategy = new LocalDeductibleTaxRateCalculator(this.data, this.scale);
      this.localmap.put(key, strategy);
    }

    // 变化的key是税率
    key = lItem.getNtaxrateKey();
    if (null != key) {
      strategy = new LocalTaxRateCalculator(this.data, this.scale);
      this.localmap.put(key, strategy);
    }
  }

  private void initStrategy() {

    IRelationForItems lItem = this.data.getRelationForItem();
    // 变化的key是主单位原币含税单价
    String key = lItem.getNorigtaxpriceKey();
    IStrategyForCal strategy;
    if (null != key) {
      strategy = new OrigTaxPriceCalculator(this.data, this.scale);
      this.map.put(key, strategy);
    }
    // 变换的key是主单位原币含税净价
    key = lItem.getNorigtaxnetpriceKey();
    if (null != key) {
      strategy = new OrigTaxNetPriceCalculator(this.data, this.scale);
      this.map.put(key, strategy);
    }
    // 变换的key是主单位原币无税单价
    key = lItem.getNorigpriceKey();
    if (null != key) {
      strategy = new OrigPriceCalculator(this.data, this.scale);
      this.map.put(key, strategy);
    }
    // 变换的key是主单位原币无税净价
    key = lItem.getNorignetpriceKey();
    if (null != key) {
      strategy = new OrigNetPriceCalculator(this.data, this.scale);
      this.map.put(key, strategy);
    }
    // 变换的key是主单位原币无税金额
    key = lItem.getNorigmnyKey();
    if (null != key) {
      strategy = new OrigMnyCalculator(this.data, this.scale);
      this.map.put(key, strategy);
    }
    // 变化的key是主单位原币价税合计
    key = lItem.getNorigtaxmnyKey();
    if (null != key) {
      strategy = new OrigTaxMnyCalculator(this.data, this.scale);
      this.map.put(key, strategy);
    }
    // // 变换的key是主单位原币税额
    // key = lItem.getNorigtaxKey();
    // if (null != key) {
    // strategy = new OrigTaxCalculator(this.data, this.scale);
    // this.map.put(key, strategy);
    // }
    // 变换的key是报价单位原币含税单价
    key = lItem.getNqtorigtaxpriceKey();
    if (null != key) {
      strategy = new QtTaxPriceCalculator(this.data, this.scale);
      this.map.put(key, strategy);
    }
    // 变换的key是报价单位原币含税净价
    key = lItem.getNqtorigtaxnetprcKey();
    if (null != key) {
      strategy = new QtTaxNetPriceCalculator(this.data, this.scale);
      this.map.put(key, strategy);
    }
    // 变换的key是报价单位原币无税单价
    key = lItem.getNqtorigpriceKey();
    if (null != key) {
      strategy = new QtPriceCalculator(this.data, this.scale);
      this.map.put(key, strategy);
    }
    // 变换的key是报价单位原币无税净价
    key = lItem.getNqtorignetpriceKey();
    if (null != key) {
      strategy = new QtNetPriceCalculator(this.data, this.scale);
      this.map.put(key, strategy);
    }
    // 变化的key是主数量
    key = lItem.getNnumKey();
    if (null != key) {
      strategy = new NumberCalculator(this.data, this.scale);
      this.map.put(key, strategy);
    }
    // 变化的key是数量
    key = lItem.getNassistnumKey();
    if (null != key) {
      strategy = new AssistNumCalculator(this.data, this.scale);
      this.map.put(key, strategy);
    }
    // 变化的key是报价单位数量
    key = lItem.getNqtunitnumKey();
    if (null != key) {
      strategy = new QtUnitNumCalculator(this.data, this.scale);
      this.map.put(key, strategy);
    }
    // 变化的key是税率
    key = lItem.getNtaxrateKey();
    if (null != key) {
      strategy = new TaxRateCalculator(this.data, this.scale);
      this.map.put(key, strategy);
    }
    // 变化的key是扣税类别
    key = lItem.getFtaxtypeflagKey();
    if (null != key) {
      strategy = new TaxRateCalculator(this.data, this.scale);
      this.map.put(key, strategy);
    }
    // 变化的key是单片折扣或者整单折扣
    key = lItem.getNitemdiscountrateKey();
    if (null != key) {
      strategy = new DiscountRateCalculator(this.data, this.scale);
      this.map.put(key, strategy);
    }
    key = lItem.getNdiscountrateKey();
    if (null != key) {
      strategy = new DiscountRateCalculator(this.data, this.scale);
      this.map.put(key, strategy);
    }
    // 变化的key是换算率
    key = lItem.getNchangerateKey();
    if (null != key) {
      strategy = new NchangeRateCalculator(this.data, this.scale);
      this.map.put(key, strategy);
    }
    // 变化的key是报价单位换算率
    key = lItem.getNqtunitrateKey();
    if (null != key) {
      strategy = new QtUnitRateCalculator(this.data, this.scale);
      this.map.put(key, strategy);
    }
    if (lItem instanceof IRelationDollarForItems) {
      // 变化key是美元金额
      key = ((IRelationDollarForItems) lItem).getNusdmnyKey();
      if (null != key) {
        strategy = new DollarAndPriceCalculator(this.data, this.scale);
        this.map.put(key, strategy);
      }
      // 变化key是美元单价
      key = ((IRelationDollarForItems) lItem).getNqtusdpriceKey();
      if (null != key) {
        strategy = new DollarPriceAndSummnyCalculator(this.data, this.scale);
        this.map.put(key, strategy);
      }
      // 变化key是美元的主单价
      key = ((IRelationDollarForItems) lItem).getNusdpriceKey();
      if (null != key) {
        strategy = new DollarNorigPriceAndSummnyCalculator(this.data, this.scale);
        this.map.put(key, strategy);
      }
    }

  }

  public void setNoPermitModifyColumn(String key) {
    IRelationForItems lItem = this.data.getRelationForItem();
    if (StringUtils.equalsIgnoreCase(key, lItem.getNexchangerateKey())) {
      this.data.putNoPermitChangeKey(lItem.getNitemdiscountrateKey());
    }
  }
}
