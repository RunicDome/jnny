package nc.bs.pm.pmrlcontractbalance.ace.bp;

import nc.bs.pm.pmrlcontractbalance.plugin.bpplugin.PmRLContractbalancePluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.pm.rlcontractbalance.AggRLContractbalanceHVO;

/**
 * ��׼��������BP
 */
public class AcePmRLContractbalanceInsertBP {

	public AggRLContractbalanceHVO[] insert(AggRLContractbalanceHVO[] bills) {

		InsertBPTemplate<AggRLContractbalanceHVO> bp = new InsertBPTemplate<AggRLContractbalanceHVO>(
				PmRLContractbalancePluginPoint.INSERT);
		this.addBeforeRule(bp.getAroundProcesser());
		this.addAfterRule(bp.getAroundProcesser());
		return bp.insert(bills);

	}

	/**
	 * ���������
	 * 
	 * @param processor
	 */
	private void addAfterRule(AroundProcesser<AggRLContractbalanceHVO> processor) {
		// TODO ���������
		IRule<AggRLContractbalanceHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillCodeCheckRule();
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCbilltype("4Z02");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule)
				.setCodeItem("bill_code");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule)
				.setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setOrgItem("pk_org");
		processor.addAfterRule(rule);
	}

	/**
	 * ����ǰ����
	 * 
	 * @param processor
	 */
	private void addBeforeRule(AroundProcesser<AggRLContractbalanceHVO> processer) {
		// TODO ����ǰ����
		IRule<AggRLContractbalanceHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
		processer.addBeforeRule(rule);
		rule = new nc.bs.pubapp.pub.rule.CreateBillCodeRule();
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setCbilltype("4Z02");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule)
				.setCodeItem("bill_code");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule)
				.setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setOrgItem("pk_org");
		processer.addBeforeRule(rule);
	}
}
