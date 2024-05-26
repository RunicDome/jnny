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
 * <b>������Ҫ������¹��ܣ��ɹ���ͬ</b>
 * <ul>
 * <li>
 * </ul>
 * <p>
 * <p>
 * 
 * @version 6.0
 * @since 6.0
 * @author liuchx
 * @time 2010-4-8 ����10:06:06
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
    // ��д�빺�� ���� ��д�۸�������
    processor.addAfterRule(new PurdailyInsertWriteBackRule());
    // ��д�빺�����塰�ۼƺ�ͬ�������� add by yechd5 at 2017-8-21 
    processor.addAfterRule(new WriteTotalcontractnum());
    // ��鵥�ݺ�Ψһ��
    processor.addAfterFinalRule(new CheckBillCodeUnique<AggCtPuVO>(
        new GeneralBillAdaptor(vos[0])));
    // ����޼ۿ���
    processor.addAfterRule(new MaxPriceRule());
    // ����� ��ӿ��Ʒ�Χ
    processor.addAfterRule(new CtContrlAddRule());
    // �������¼�����
    processor.addAfterRule(new PurdailyEventRule(IEventType.TYPE_INSERT_AFTER));
   
  }

  private void addBeforeRule(AggCtPuVO[] vos,
      CompareAroundProcesser<AggCtPuVO> processor) {
    // ������ӱ�VO�����ֶεĳ���
    processor.addBeforeRule(new SaveVOValidateRule<AggCtPuVO>());
    // ����Զ�����
    processor.addBeforeRule(new SelfDefItemChkRule<AggCtPuVO>());
    // ��������ǰ����䵥�ݺ�
    processor.addBeforeRule(new CTGetBillCodeRule<AggCtPuVO>(
        new GeneralBillAdaptor(vos[0])));
    // ����ʱ�����
    processor.addBeforeRule(new FillCreateInfoRule<AggCtPuVO>());
    // ����Ƶ��ˣ��Ƶ�ʱ��
    processor.addBeforeRule(new AddMakeDateRule<AggCtPuVO>());
    // ����к�
    processor.addBeforeRule(new CTRowNoCheckRule<AggCtPuVO>(CtPuBVO.class));
    // �ǿռ��
    processor.addBeforeRule(new PuNotNullChkRule());
    // �ǿ�����
    processor.addBeforeRule(new IsNullChkRule<AggCtPuVO>(CtPuBVO.class));
    // ��������
    processor.addBeforeRule(new PurdailyTypeChkRule());
    // �ɹ���֯ͣ�ü��
    processor.addBeforeRule(new PurchaseOrgEnableCheckRule<AggCtPuVO>());
    // ��ֵ�ͼ��
    processor.addBeforeRule(new NumValueChkRule<AggCtPuVO>());
    // ����ͷ�������ܽ����°汾��Ϊ�ղ�0
    processor.addBeforeRule(new CTNumAndOrigmnySum<AggCtPuVO>(CtPuBVO.class));
    // ��� ���ú�ͬ�����õ�����Ѿ���ɾ�������ܱ��桱
    processor.addBeforeRule(new CTCheckCTTypeRule<AggCtPuVO>());
    // ��������������
    processor.addBeforeRule(new CtPuBracketOrderCheckRule());
    // 636����Э��ǿռ��
    processor.addBeforeRule(new CtPaymentNotNullCheckRule());
    // 636����Э����ֵ���
    processor.addBeforeRule(new CtPaymentValueCheckRule());
    // 636��Ӧ�̺��������
    processor.addBeforeRule(new SupplierIsBlackCheckRule());

    // ����ǰ�¼�����
    processor
        .addBeforeRule(new PurdailyEventRule(IEventType.TYPE_INSERT_BEFORE));

    // ����У�����
    processor.addBeforeRule(new CrossRuleValidateRule<AggCtPuVO>());
    // У���ۼƺ�ͬ�������Ƿ񳬹��빺�������� add by yechd5 at 2017-08-21
    processor.addAfterRule(new CompTotalNnumWithBuyreqnum());
  }
}
