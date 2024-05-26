package nc.bs.pm.pmrlcontractalter.ace.bp;

import nc.bs.pm.pmrlcontractalter.plugin.bpplugin.PmRLContractalterPluginPoint;
import nc.vo.pm.rlcontractalter.AggRLContractalterHVO;

import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;


/**
 * 标准单据删除BP
 */
public class AcePmRLContractalterDeleteBP {

	public void delete(AggRLContractalterHVO[] bills) {

		DeleteBPTemplate<AggRLContractalterHVO> bp = new DeleteBPTemplate<AggRLContractalterHVO>(
				PmRLContractalterPluginPoint.DELETE);
		// 增加执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 增加执行后业务规则
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggRLContractalterHVO> processer) {
		// TODO 前规则
		IRule<AggRLContractalterHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggRLContractalterHVO> processer) {
		// TODO 后规则

	}
}
