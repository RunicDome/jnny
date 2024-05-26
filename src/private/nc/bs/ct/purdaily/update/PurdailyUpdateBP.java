package nc.bs.ct.purdaily.update;

import nc.bs.businessevent.IEventType;
import nc.bs.ct.purdaily.base.BPPlugInPoint;
import nc.bs.ct.purdaily.update.rule.WriteTotalcontractnum;
import nc.bs.ct.purdaily.insert.rule.CtPaymentNotNullCheckRule;
import nc.bs.ct.purdaily.insert.rule.CtPaymentValueCheckRule;
import nc.bs.ct.purdaily.insert.rule.CtPuBracketOrderCheckRule;
import nc.bs.ct.purdaily.insert.rule.MaxPriceRule;
import nc.bs.ct.purdaily.insert.rule.PuNotNullChkRule;
import nc.bs.ct.purdaily.insert.rule.SupplierIsBlackCheckRule;
import nc.bs.ct.purdaily.rule.PurdailyTypeChkRule;
import nc.bs.ct.purdaily.update.rule.PurdailyUpdateWriteBackRule;
import nc.bs.ct.purdaily.update.rule.TrantypeUpdateCheckRule;
import nc.bs.ct.rule.billcode.FillBillCodeWhenUpdateRule;
import nc.bs.ct.rule.billcode.GeneralBillAdaptor;
import nc.bs.ct.rule.pub.CTRowNoCheckRule;
import nc.bs.ct.rule.pub.FillModifiedInfoRule;
import nc.bs.ct.rule.pub.IsNullChkRule;
import nc.bs.ct.rule.pub.NumValueChkRule;
import nc.bs.ct.rule.pub.SaveVOValidateRule;
import nc.bs.ct.rule.pub.SelfDefItemChkRule;
import nc.bs.scmpub.rule.CrossRuleValidateRule;
import nc.impl.ct.rule.insert.CTCheckCTTypeRule;
import nc.impl.ct.rule.insert.CTNumAndOrigmnySum;
import nc.impl.pubapp.pattern.data.bill.BillOperator;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.ct.purdaily.entity.AggCtPuVO;
import nc.vo.ct.purdaily.entity.CtPuBVO;
import nc.vo.ct.rule.PurdailyEventCompareRule;
import nc.vo.ct.rule.StatusCheckRule;

/**
 * <p>
 * <b>本类主要完成以下功能：</b> 采购合同修改BP类
 * <ul>
 * <li>
 * </ul>
 * <p>
 * <p>
 * 
 * @version 6.0
 * @since 6.0
 * @author liuchx
 * @time 2010-5-9 下午04:50:18
 */
public class PurdailyUpdateBP {

  public AggCtPuVO[] update(AggCtPuVO[] vos, AggCtPuVO[] originVOs,
      boolean isSaveForEC) {

    CompareAroundProcesser<AggCtPuVO> processor =
        new CompareAroundProcesser<AggCtPuVO>(BPPlugInPoint.UpdateBP);
    this.addBeforeRule(vos, originVOs, processor, isSaveForEC);
    this.addAfterRule(processor, isSaveForEC);

    processor.before(vos, originVOs);

    BillOperator<AggCtPuVO> operator = new BillOperator<AggCtPuVO>();
    AggCtPuVO[] resultVOs = operator.update(vos, originVOs);

    processor.after(resultVOs, originVOs);
    return resultVOs;

  }

  private void addAfterRule(CompareAroundProcesser<AggCtPuVO> processor,
      boolean isSaveForEC) {
    // 回写请购单 或者 回写价格审批单
    processor.addAfterRule(new PurdailyUpdateWriteBackRule());
    // 回写请购单表体“累计合同主数量” add by yechd5 at 2017-8-21 
    processor.addAfterRule(new WriteTotalcontractnum());
    // 最高限价控制规则
    processor.addAfterRule(new MaxPriceRule());
    if (!isSaveForEC) {
      // 修改后事件处理
      processor.addAfterRule(new PurdailyEventCompareRule(
          IEventType.TYPE_UPDATE_AFTER));
    }
  }

  private void addBeforeRule(AggCtPuVO[] vos, AggCtPuVO[] originVOs,
      CompareAroundProcesser<AggCtPuVO> processor, boolean isSaveForEC) {
    // 检查主子表VO各个字段的长度
    processor.addBeforeRule(new SaveVOValidateRule<AggCtPuVO>());
    // 检查自定义项
    processor.addBeforeRule(new SelfDefItemChkRule<AggCtPuVO>());
    // 修改保存填充单据号
    processor.addBeforeRule(new FillBillCodeWhenUpdateRule<AggCtPuVO>(
        new GeneralBillAdaptor(vos[0])));
    // 填充“修改人” “修改时间” 因为要保存到数据库，一定要放在保存前校验
    processor.addBeforeRule(new FillModifiedInfoRule<AggCtPuVO>());
    // 检查行号
    processor.addBeforeRule(new CTRowNoCheckRule<AggCtPuVO>(CtPuBVO.class));
    // 非空检查
    processor.addBeforeRule(new PuNotNullChkRule());
    // 数值型检查
    processor.addBeforeRule(new NumValueChkRule<AggCtPuVO>());
    // 检查 “该合同所引用的类别已经被删除，不能保存”
    processor.addBeforeRule(new CTCheckCTTypeRule<AggCtPuVO>());
    // 非空检查
    processor.addBeforeRule(new IsNullChkRule<AggCtPuVO>(CtPuBVO.class));
    // 填充表头总数量总金额、最新版本、为空补0
    processor.addBeforeRule(new CTNumAndOrigmnySum<AggCtPuVO>(CtPuBVO.class));
    // 交易类型
    processor.addBeforeRule(new PurdailyTypeChkRule());
    // 单据状态检查
    processor.addBeforeRule(new StatusCheckRule<AggCtPuVO>());
    // 总括订单检查规则
    processor.addBeforeRule(new CtPuBracketOrderCheckRule());
    // 交易类型变化前后检查校验
    processor.addBeforeRule(new TrantypeUpdateCheckRule());
    // 636付款协议非空检查
    processor.addBeforeRule(new CtPaymentNotNullCheckRule());
    // 636付款协议数值检查
    processor.addBeforeRule(new CtPaymentValueCheckRule());
    // 636供应商黑名单检查
    processor.addBeforeRule(new SupplierIsBlackCheckRule());

    // 交叉校验规则
    processor.addBeforeRule(new CrossRuleValidateRule<AggCtPuVO>());
    if (!isSaveForEC) {
      // 修改前事件处理
      processor.addBeforeRule(new PurdailyEventCompareRule(
          IEventType.TYPE_UPDATE_BEFORE));
    }

  }

}
