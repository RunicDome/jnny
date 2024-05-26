package nc.bs.pm.pmrlcontractalter.ace.bp;

import nc.bs.pm.pmrlcontractalter.plugin.bpplugin.PmRLContractalterPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.pm.rlcontractalter.AggRLContractalterHVO;

/**
 * �޸ı����BP
 * 
 */
public class AcePmRLContractalterUpdateBP {

	public AggRLContractalterHVO[] update(AggRLContractalterHVO[] bills,
			AggRLContractalterHVO[] originBills) {
		// �����޸�ģ��
		UpdateBPTemplate<AggRLContractalterHVO> bp = new UpdateBPTemplate<AggRLContractalterHVO>(
				PmRLContractalterPluginPoint.UPDATE);
		// ִ��ǰ����
		this.addBeforeRule(bp.getAroundProcesser());
		// ִ�к����
		this.addAfterRule(bp.getAroundProcesser());
		return bp.update(bills, originBills);
	}

	private void addAfterRule(CompareAroundProcesser<AggRLContractalterHVO> processer) {
		// TODO �����
		IRule<AggRLContractalterHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillCodeCheckRule();
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCbilltype("4Z05");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule)
				.setCodeItem("pk_contr");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule)
				.setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setOrgItem("pk_org");
		processer.addAfterRule(rule);

	}

	private void addBeforeRule(CompareAroundProcesser<AggRLContractalterHVO> processer) {
		// TODO ǰ����
		IRule<AggRLContractalterHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
		processer.addBeforeRule(rule);
		nc.impl.pubapp.pattern.rule.ICompareRule<AggRLContractalterHVO> ruleCom = new nc.bs.pubapp.pub.rule.UpdateBillCodeRule();
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom)
				.setCbilltype("4Z05");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom)
				.setCodeItem("pk_contr");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom)
				.setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom)
				.setOrgItem("pk_org");
		processer.addBeforeRule(ruleCom);
	}

}
