package nc.bs.pm.pmrlcontractalter.ace.bp;

import nc.bs.pm.pmrlcontractalter.plugin.bpplugin.PmRLContractalterPluginPoint;
import nc.vo.pm.rlcontractalter.AggRLContractalterHVO;

import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;


/**
 * ��׼����ɾ��BP
 */
public class AcePmRLContractalterDeleteBP {

	public void delete(AggRLContractalterHVO[] bills) {

		DeleteBPTemplate<AggRLContractalterHVO> bp = new DeleteBPTemplate<AggRLContractalterHVO>(
				PmRLContractalterPluginPoint.DELETE);
		// ����ִ��ǰ����
		this.addBeforeRule(bp.getAroundProcesser());
		// ����ִ�к�ҵ�����
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggRLContractalterHVO> processer) {
		// TODO ǰ����
		IRule<AggRLContractalterHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * ɾ����ҵ�����
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggRLContractalterHVO> processer) {
		// TODO �����

	}
}
