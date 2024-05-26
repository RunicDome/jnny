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
 * <b>������Ҫ������¹��ܣ�</b>
 * <ul>
 * <li>
 * </ul>
 * <p>
 * <p>
 * 
 * @version 6.0
 * @since 6.0
 * @author liuchx
 * @time 2010-4-8 ����07:30:27
 */
public class PurdailyDeleteBP {
  public void delete(AggCtPuVO[] origVos) {

    AroundProcesser<AggCtPuVO> processer =
        new AroundProcesser<AggCtPuVO>(BPPlugInPoint.DeleteBP);
    // ����ִ��ǰҵ�����
    this.addBeforeRule(processer);
    // ����ִ�к�ҵ�����
    this.addAfterRule(processer, origVos);
    TimeLog.logStart();
    processer.before(origVos);
    TimeLog.info("ɾ��ǰִ��ҵ�����");/* -=notranslate=- */

    TimeLog.logStart();
    BillDelete<IBill> bo = new BillDelete<IBill>();
    bo.delete(origVos);

    TimeLog.logStart();
    processer.after(origVos);
    TimeLog.info("ɾ����ִ��ҵ�����");/* -=notranslate=- */
  }

  /**
   * ��������������
   * <p>
   * <b>����˵��</b>
   * 
   * @param processer
   *          <p>
   * @since 6.0
   * @author liuchx
   * @param bills
   * @time 2010-5-24 ����08:27:47
   */
  private void addAfterRule(AroundProcesser<AggCtPuVO> processer,
      AggCtPuVO[] bills) {
    // �����д�빺�����ͼ۸�������
    processer.addAfterRule(new PurdailyDeleteWriteBackRule());
    // ��д�빺�����塰�ۼƺ�ͬ�������� add by yechd5 at 2017-8-21 
    processer.addAfterRule(new WriteTotalcontractnum());
    // ���˵��ݺ� new GeneralBillAdaptor(vos[0])
    processer.addAfterRule(new ReturnBillCodeRule(new GeneralBillAdaptor(
        bills[0])));
    // add by liangchen1
    processer.addAfterRule(new DealOldVersionDrRule());
    // ɾ�����¼�����
    processer.addAfterRule(new PurdailyEventRule(IEventType.TYPE_DELETE_AFTER));
  }

  /**
   * ��������������
   * <p>
   * <b>����˵��</b>
   * 
   * @param processer
   *          <p>
   * @since 6.0
   * @author liuchx
   * @time 2010-6-13 ����04:59:02
   */
  private void addBeforeRule(AroundProcesser<AggCtPuVO> processer) {
    // ɾ����ͬ���Ʒ�Χ
    processer.addBeforeRule(new DeleteCTScope());
    // ֻ��������״̬�ĵ��ݲ���ɾ��
    processer.addBeforeRule(new CheckVOStatus());
    // ɾ��ǰ�¼�����
    processer
        .addBeforeRule(new PurdailyEventRule(IEventType.TYPE_DELETE_BEFORE));

  }
}
