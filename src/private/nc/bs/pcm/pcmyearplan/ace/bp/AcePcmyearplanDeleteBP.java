package nc.bs.pcm.pcmyearplan.ace.bp;

import nc.bs.pcm.pcmyearplan.plugin.bpplugin.PcmyearplanPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.pcm.pcmyearplan.AggPcmYearplan;

/**
 * 标准单据删除BP
 */
@SuppressWarnings("unchecked")
public class AcePcmyearplanDeleteBP {

	public void delete(AggPcmYearplan[] bills) {

		DeleteBPTemplate<AggPcmYearplan> bp = new DeleteBPTemplate<AggPcmYearplan>(
				PcmyearplanPluginPoint.DELETE);
		// 增加执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 增加执行后业务规则
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggPcmYearplan> processer) {
		// TODO 前规则
		IRule<AggPcmYearplan> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggPcmYearplan> processer) {
		// TODO 后规则

	}
}
