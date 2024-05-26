package nc.vo.pubapp.calculator.strategy;

import nc.vo.pubapp.calculator.Condition;
import nc.vo.pubapp.calculator.data.CacheDataSet;
import nc.vo.pubapp.scale.ScaleUtils;

/**
 * 
 * 计算所有本币
 * 
 * @author zhuhz
 * 
 */
public class LocalGlobalGroupCalculator implements IStrategyForCal {
  // 计算本币用的结果集
  private CacheDataSet data;

  private ScaleUtils scale;

  public LocalGlobalGroupCalculator(CacheDataSet data, ScaleUtils scale) {
    this.data = data;
    this.scale = scale;
  }

  @Override
  public void calculate(Condition cond) {
	  Object attributeValue = this.data.getAttributeValue("nnum");
    IStrategyForCal cal = new LocalCalculator(this.data, this.scale);
    cal.calculate(cond);
    // 全局集团暂时uap还不支持，故先注释掉
    cal = new GlobalMoneyCalculator(this.data, this.scale);
    cal.calculate(cond);
    cal = new GroupMoneyCalculator(this.data, this.scale);
    cal.calculate(cond);
  }
}
