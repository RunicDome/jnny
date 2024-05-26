package nc.bs.pcm.pcmproject.ace.bp;

import nc.bs.pcm.pcmproject.plugin.bpplugin.PcmprojectPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.pcm.pcmproject.AggPcmProject;

/**
 * 标准单据删除BP
 */
@SuppressWarnings("unchecked")
public class AcePcmprojectDeleteBP {

	public void delete(AggPcmProject[] bills) {

		DeleteBPTemplate<AggPcmProject> bp = new DeleteBPTemplate<AggPcmProject>(
				PcmprojectPluginPoint.DELETE);
		// 增加执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 增加执行后业务规则
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggPcmProject> processer) {
		// TODO 前规则
		IRule<AggPcmProject> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggPcmProject> processer) {
		// TODO 后规则

	}
}
