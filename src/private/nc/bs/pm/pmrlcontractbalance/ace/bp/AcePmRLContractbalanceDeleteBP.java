package nc.bs.pm.pmrlcontractbalance.ace.bp;

import nc.bs.pm.pmrlcontractbalance.plugin.bpplugin.PmRLContractbalancePluginPoint;
import nc.vo.pm.rlcontractbalance.AggRLContractbalanceHVO;

import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;


/**
 * 标准单据删除BP
 */
public class AcePmRLContractbalanceDeleteBP {

	public void delete(AggRLContractbalanceHVO[] bills) {

		DeleteBPTemplate<AggRLContractbalanceHVO> bp = new DeleteBPTemplate<AggRLContractbalanceHVO>(
				PmRLContractbalancePluginPoint.DELETE);
		// 增加执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 增加执行后业务规则
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggRLContractbalanceHVO> processer) {
		// TODO 前规则
		IRule<AggRLContractbalanceHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggRLContractbalanceHVO> processer) {
		// TODO 后规则

	}
}
