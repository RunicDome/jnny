package nc.bs.pm.pmrlcontractschedule.ace.bp;

import nc.bs.pm.pmrlcontractschedule.plugin.bpplugin.PmRLContractschedulePluginPoint;
import nc.vo.pm.rlcontractschedule.AggRLContractschedule;

import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;


/**
 * ��׼����ɾ��BP
 */
public class AcePmRLContractscheduleDeleteBP {

	public void delete(AggRLContractschedule[] bills) {

		DeleteBPTemplate<AggRLContractschedule> bp = new DeleteBPTemplate<AggRLContractschedule>(
				PmRLContractschedulePluginPoint.DELETE);
		// ����ִ��ǰ����
		this.addBeforeRule(bp.getAroundProcesser());
		// ����ִ�к�ҵ�����
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggRLContractschedule> processer) {
		// TODO ǰ����
		IRule<AggRLContractschedule> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * ɾ����ҵ�����
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggRLContractschedule> processer) {
		// TODO �����

	}
}
