package nc.bs.pcm.pcmproject.ace.bp;

import nc.bs.pcm.pcmproject.plugin.bpplugin.PcmprojectPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.pcm.pcmproject.AggPcmProject;

/**
 * 标准单据新增BP
 */
@SuppressWarnings("unchecked")
public class AcePcmprojectInsertBP {

	public AggPcmProject[] insert(AggPcmProject[] bills) {

		InsertBPTemplate<AggPcmProject> bp = new InsertBPTemplate<AggPcmProject>(
				PcmprojectPluginPoint.INSERT);
		this.addBeforeRule(bp.getAroundProcesser());
		this.addAfterRule(bp.getAroundProcesser());
		return bp.insert(bills);

	}

	/**
	 * 新增后规则
	 * 
	 * @param processor
	 */
	private void addAfterRule(AroundProcesser<AggPcmProject> processor) {
		// TODO 新增后规则
		IRule<AggPcmProject> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillCodeCheckRule();
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCbilltype("4D90");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCodeItem("code");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule)
				.setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setOrgItem("pk_org");
		processor.addAfterRule(rule);
	}

	/**
	 * 新增前规则
	 * 
	 * @param processor
	 */
	private void addBeforeRule(AroundProcesser<AggPcmProject> processer) {
		// TODO 新增前规则
		IRule<AggPcmProject> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
		processer.addBeforeRule(rule);
		rule = new nc.bs.pubapp.pub.rule.CreateBillCodeRule();
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setCbilltype("4D90");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setCodeItem("code");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule)
				.setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setOrgItem("pk_org");
		processer.addBeforeRule(rule);
	}
}
