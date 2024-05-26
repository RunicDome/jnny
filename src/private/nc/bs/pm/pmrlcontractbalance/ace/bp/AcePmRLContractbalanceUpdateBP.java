package nc.bs.pm.pmrlcontractbalance.ace.bp;

import nc.bs.pm.pmrlcontractbalance.plugin.bpplugin.PmRLContractbalancePluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.pm.rlcontractbalance.AggRLContractbalanceHVO;

/**
 * �޸ı����BP
 * 
 */
public class AcePmRLContractbalanceUpdateBP {

	public AggRLContractbalanceHVO[] update(AggRLContractbalanceHVO[] bills,
			AggRLContractbalanceHVO[] originBills) {
		// �����޸�ģ��
		UpdateBPTemplate<AggRLContractbalanceHVO> bp = new UpdateBPTemplate<AggRLContractbalanceHVO>(
				PmRLContractbalancePluginPoint.UPDATE);
		// ִ��ǰ����
		this.addBeforeRule(bp.getAroundProcesser());
		// ִ�к����
		this.addAfterRule(bp.getAroundProcesser());
		return bp.update(bills, originBills);
	}

	private void addAfterRule(CompareAroundProcesser<AggRLContractbalanceHVO> processer) {
		// TODO �����
		IRule<AggRLContractbalanceHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillCodeCheckRule();
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCbilltype("4Z02");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule)
				.setCodeItem("bill_code");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule)
				.setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setOrgItem("pk_org");
		processer.addAfterRule(rule);

	}

	private void addBeforeRule(CompareAroundProcesser<AggRLContractbalanceHVO> processer) {
		// TODO ǰ����
		IRule<AggRLContractbalanceHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
		processer.addBeforeRule(rule);
		nc.impl.pubapp.pattern.rule.ICompareRule<AggRLContractbalanceHVO> ruleCom = new nc.bs.pubapp.pub.rule.UpdateBillCodeRule();
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom)
				.setCbilltype("4Z02");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom)
				.setCodeItem("bill_code");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom)
				.setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom)
				.setOrgItem("pk_org");
		processer.addBeforeRule(ruleCom);
	}

}
