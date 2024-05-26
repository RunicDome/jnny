package nc.bs.ct.purdaily.delete;

import nc.bs.businessevent.IEventType;
import nc.bs.ct.purdaily.base.BPPlugInPoint;
import nc.bs.ct.purdaily.delete.rule.CheckVOStatus;
import nc.bs.ct.purdaily.delete.rule.DealOldVersionDrRule;
import nc.bs.ct.purdaily.delete.rule.DeleteCTScope;
import nc.bs.ct.purdaily.delete.rule.PurdailyDeleteWriteBackRule;
import nc.bs.ct.purdaily.delete.rule.ReturnBillCodeRule;
import nc.bs.ct.purdaily.delete.rule.WriteTotalcontractnum;
import nc.bs.ct.rule.billcode.GeneralBillAdaptor;
import nc.impl.pubapp.pattern.data.bill.BillDelete;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.ct.purdaily.entity.AggCtPuVO;
import nc.vo.ct.rule.PurdailyEventRule;
import nc.vo.pubapp.pattern.log.TimeLog;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;

/**
 * <p>
 * <b>本类主要完成以下功能：</b>
 * <ul>
 * <li>
 * </ul>
 * <p>
 * <p>
 * 
 * @version 6.0
 * @since 6.0
 * @author liuchx
 * @time 2010-4-8 下午07:30:27
 */
public class PurdailyDeleteBP {
  public void delete(AggCtPuVO[] origVos) {

    AroundProcesser<AggCtPuVO> processer =
        new AroundProcesser<AggCtPuVO>(BPPlugInPoint.DeleteBP);
    // 增加执行前业务规则
    this.addBeforeRule(processer);
    // 增加执行后业务规则
    this.addAfterRule(processer, origVos);
    TimeLog.logStart();
    processer.before(origVos);
    TimeLog.info("删除前执行业务规则");/* -=notranslate=- */

    TimeLog.logStart();
    BillDelete<IBill> bo = new BillDelete<IBill>();
    bo.delete(origVos);

    TimeLog.logStart();
    processer.after(origVos);
    TimeLog.info("删除后执行业务规则");/* -=notranslate=- */
  }

  /**
   * 方法功能描述：
   * <p>
   * <b>参数说明</b>
   * 
   * @param processer
   *          <p>
   * @since 6.0
   * @author liuchx
   * @param bills
   * @time 2010-5-24 下午08:27:47
   */
  private void addAfterRule(AroundProcesser<AggCtPuVO> processer,
      AggCtPuVO[] bills) {
    // 加入回写请购单，和价格审批单
    processer.addAfterRule(new PurdailyDeleteWriteBackRule());
    // 回写请购单表体“累计合同主数量” add by yechd5 at 2017-8-21 
    processer.addAfterRule(new WriteTotalcontractnum());
    // 回退单据号 new GeneralBillAdaptor(vos[0])
    processer.addAfterRule(new ReturnBillCodeRule(new GeneralBillAdaptor(
        bills[0])));
    // add by liangchen1
    processer.addAfterRule(new DealOldVersionDrRule());
    // 删除后事件处理
    processer.addAfterRule(new PurdailyEventRule(IEventType.TYPE_DELETE_AFTER));
  }

  /**
   * 方法功能描述：
   * <p>
   * <b>参数说明</b>
   * 
   * @param processer
   *          <p>
   * @since 6.0
   * @author liuchx
   * @time 2010-6-13 下午04:59:02
   */
  private void addBeforeRule(AroundProcesser<AggCtPuVO> processer) {
    // 删除合同控制范围
    processer.addBeforeRule(new DeleteCTScope());
    // 只有是自由状态的单据才能删除
    processer.addBeforeRule(new CheckVOStatus());
    // 删除前事件处理
    processer
        .addBeforeRule(new PurdailyEventRule(IEventType.TYPE_DELETE_BEFORE));

  }
}
