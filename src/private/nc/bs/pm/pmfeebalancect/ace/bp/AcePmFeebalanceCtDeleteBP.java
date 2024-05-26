package nc.bs.pm.pmfeebalancect.ace.bp;

import nc.bs.pm.pmfeebalancect.plugin.bpplugin.PmFeebalanceCtPluginPoint;
import nc.vo.pm.feebalance.AggPmFeebalance;

import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;


/**
 * 标准单据删除BP
 */
public class AcePmFeebalanceCtDeleteBP {

	public void delete(AggPmFeebalance[] bills) {

		DeleteBPTemplate<AggPmFeebalance> bp = new DeleteBPTemplate<AggPmFeebalance>(
				PmFeebalanceCtPluginPoint.DELETE);
		// 增加执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 增加执行后业务规则
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggPmFeebalance> processer) {
		// TODO 前规则
		IRule<AggPmFeebalance> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggPmFeebalance> processer) {
		// TODO 后规则

	}
}
