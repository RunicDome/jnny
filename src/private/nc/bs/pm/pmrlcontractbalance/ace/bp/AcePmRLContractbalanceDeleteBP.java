package nc.bs.pm.pmrlcontractbalance.ace.bp;

import nc.bs.pm.pmrlcontractbalance.plugin.bpplugin.PmRLContractbalancePluginPoint;
import nc.vo.pm.rlcontractbalance.AggRLContractbalanceHVO;

import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;


/**
 * ��׼����ɾ��BP
 */
public class AcePmRLContractbalanceDeleteBP {

	public void delete(AggRLContractbalanceHVO[] bills) {

		DeleteBPTemplate<AggRLContractbalanceHVO> bp = new DeleteBPTemplate<AggRLContractbalanceHVO>(
				PmRLContractbalancePluginPoint.DELETE);
		// ����ִ��ǰ����
		this.addBeforeRule(bp.getAroundProcesser());
		// ����ִ�к�ҵ�����
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggRLContractbalanceHVO> processer) {
		// TODO ǰ����
		IRule<AggRLContractbalanceHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * ɾ����ҵ�����
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggRLContractbalanceHVO> processer) {
		// TODO �����

	}
}
