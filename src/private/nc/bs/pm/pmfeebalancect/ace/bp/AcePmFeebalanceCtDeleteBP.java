package nc.bs.pm.pmfeebalancect.ace.bp;

import nc.bs.pm.pmfeebalancect.plugin.bpplugin.PmFeebalanceCtPluginPoint;
import nc.vo.pm.feebalance.AggPmFeebalance;

import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;


/**
 * ��׼����ɾ��BP
 */
public class AcePmFeebalanceCtDeleteBP {

	public void delete(AggPmFeebalance[] bills) {

		DeleteBPTemplate<AggPmFeebalance> bp = new DeleteBPTemplate<AggPmFeebalance>(
				PmFeebalanceCtPluginPoint.DELETE);
		// ����ִ��ǰ����
		this.addBeforeRule(bp.getAroundProcesser());
		// ����ִ�к�ҵ�����
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggPmFeebalance> processer) {
		// TODO ǰ����
		IRule<AggPmFeebalance> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * ɾ����ҵ�����
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggPmFeebalance> processer) {
		// TODO �����

	}
}
