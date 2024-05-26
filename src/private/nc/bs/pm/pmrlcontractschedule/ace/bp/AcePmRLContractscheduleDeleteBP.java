package nc.bs.pm.pmrlcontractschedule.ace.bp;

import nc.bs.pm.pmrlcontractschedule.plugin.bpplugin.PmRLContractschedulePluginPoint;
import nc.vo.pm.rlcontractschedule.AggRLContractschedule;

import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;


/**
 * 标准单据删除BP
 */
public class AcePmRLContractscheduleDeleteBP {

	public void delete(AggRLContractschedule[] bills) {

		DeleteBPTemplate<AggRLContractschedule> bp = new DeleteBPTemplate<AggRLContractschedule>(
				PmRLContractschedulePluginPoint.DELETE);
		// 增加执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 增加执行后业务规则
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggRLContractschedule> processer) {
		// TODO 前规则
		IRule<AggRLContractschedule> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggRLContractschedule> processer) {
		// TODO 后规则

	}
}
