package nc.bs.ct.purdaily.insert;

import nc.bs.businessevent.IEventType;
import nc.bs.ct.purdaily.base.BPPlugInPoint;
import nc.bs.ct.purdaily.insert.rule.CompTotalNnumWithBuyreqnum;
import nc.bs.ct.purdaily.insert.rule.CtContrlAddRule;
import nc.bs.ct.purdaily.insert.rule.CtPaymentNotNullCheckRule;
import nc.bs.ct.purdaily.insert.rule.CtPaymentValueCheckRule;
import nc.bs.ct.purdaily.insert.rule.CtPuBracketOrderCheckRule;
import nc.bs.ct.purdaily.insert.rule.MaxPriceRule;
import nc.bs.ct.purdaily.insert.rule.PuNotNullChkRule;
import nc.bs.ct.purdaily.insert.rule.PurdailyInsertWriteBackRule;
import nc.bs.ct.purdaily.insert.rule.SupplierIsBlackCheckRule;
import nc.bs.ct.purdaily.insert.rule.WriteTotalcontractnum;
import nc.bs.ct.purdaily.rule.PurdailyTypeChkRule;
import nc.bs.ct.rule.billcode.CTGetBillCodeRule;
import nc.bs.ct.rule.billcode.CheckBillCodeUnique;
import nc.bs.ct.rule.billcode.GeneralBillAdaptor;
import nc.bs.ct.rule.pub.AddMakeDateRule;
import nc.bs.ct.rule.pub.CTRowNoCheckRule;
import nc.bs.ct.rule.pub.FillCreateInfoRule;
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
import nc.vo.ct.rule.PurdailyEventRule;
import nc.vo.scmpub.rule.PurchaseOrgEnableCheckRule;

/**
 * <b>本类主要完成以下功能：采购合同</b>
 * <ul>
 * <li>
 * </ul>
 * <p>
 * <p>
 * 
 * @version 6.0
 * @since 6.0
 * @author liuchx
 * @time 2010-4-8 上午10:06:06
 */
public class PurdailyInsertBP {

  public AggCtPuVO[] insert(AggCtPuVO[] vos) {

    CompareAroundProcesser<AggCtPuVO> processor =
        new CompareAroundProcesser<AggCtPuVO>(BPPlugInPoint.InsertBP);
    this.addBeforeRule(vos, processor);
    this.addAfterRule(vos, processor);

    processor.before(vos, null);
    BillOperator<AggCtPuVO> operator = new BillOperator<AggCtPuVO>();
    AggCtPuVO[] resultBills = operator.insert(vos);
    processor.after(resultBills, null);
    return resultBills;
  }

  private void addAfterRule(AggCtPuVO[] vos,
      CompareAroundProcesser<AggCtPuVO> processor) {
    // 回写请购单 或者 回写价格审批单
    processor.addAfterRule(new PurdailyInsertWriteBackRule());
    // 回写请购单表体“累计合同主数量” add by yechd5 at 2017-8-21 
    processor.addAfterRule(new WriteTotalcontractnum());
    // 检查单据号唯一性
    processor.addAfterFinalRule(new CheckBillCodeUnique<AggCtPuVO>(
        new GeneralBillAdaptor(vos[0])));
    // 最高限价控制
    processor.addAfterRule(new MaxPriceRule());
    // 保存后 添加控制范围
    processor.addAfterRule(new CtContrlAddRule());
    // 新增后事件处理
    processor.addAfterRule(new PurdailyEventRule(IEventType.TYPE_INSERT_AFTER));
   
  }

  private void addBeforeRule(AggCtPuVO[] vos,
      CompareAroundProcesser<AggCtPuVO> processor) {
    // 检查主子表VO各个字段的长度
    processor.addBeforeRule(new SaveVOValidateRule<AggCtPuVO>());
    // 检查自定义项
    processor.addBeforeRule(new SelfDefItemChkRule<AggCtPuVO>());
    // 新增保存前，填充单据号
    processor.addBeforeRule(new CTGetBillCodeRule<AggCtPuVO>(
        new GeneralBillAdaptor(vos[0])));
    // 单据时间填充
    processor.addBeforeRule(new FillCreateInfoRule<AggCtPuVO>());
    // 填充制单人，制单时间
    processor.addBeforeRule(new AddMakeDateRule<AggCtPuVO>());
    // 检查行号
    processor.addBeforeRule(new CTRowNoCheckRule<AggCtPuVO>(CtPuBVO.class));
    // 非空检查
    processor.addBeforeRule(new PuNotNullChkRule());
    // 非空项检查
    processor.addBeforeRule(new IsNullChkRule<AggCtPuVO>(CtPuBVO.class));
    // 交易类型
    processor.addBeforeRule(new PurdailyTypeChkRule());
    // 采购组织停用检查
    processor.addBeforeRule(new PurchaseOrgEnableCheckRule<AggCtPuVO>());
    // 数值型检查
    processor.addBeforeRule(new NumValueChkRule<AggCtPuVO>());
    // 填充表头总数量总金额、最新版本、为空补0
    processor.addBeforeRule(new CTNumAndOrigmnySum<AggCtPuVO>(CtPuBVO.class));
    // 检查 “该合同所引用的类别已经被删除，不能保存”
    processor.addBeforeRule(new CTCheckCTTypeRule<AggCtPuVO>());
    // 总括订单检查规则
    processor.addBeforeRule(new CtPuBracketOrderCheckRule());
    // 636付款协议非空检查
    processor.addBeforeRule(new CtPaymentNotNullCheckRule());
    // 636付款协议数值检查
    processor.addBeforeRule(new CtPaymentValueCheckRule());
    // 636供应商黑名单检查
    processor.addBeforeRule(new SupplierIsBlackCheckRule());

    // 新增前事件处理
    processor
        .addBeforeRule(new PurdailyEventRule(IEventType.TYPE_INSERT_BEFORE));

    // 交叉校验规则
    processor.addBeforeRule(new CrossRuleValidateRule<AggCtPuVO>());
    // 校验累计合同主数量是否超过请购单主数量 add by yechd5 at 2017-08-21
    processor.addAfterRule(new CompTotalNnumWithBuyreqnum());
  }
}
