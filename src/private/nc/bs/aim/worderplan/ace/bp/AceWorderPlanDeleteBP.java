package nc.bs.aim.worderplan.ace.bp;

import nc.bs.aim.worderplan.plugin.bpplugin.WorderPlanPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.aim.worderplan.AggWorderplan;

/**
 * 标准单据删除BP
 */
@SuppressWarnings("unchecked")
public class AceWorderPlanDeleteBP {

	public void delete(AggWorderplan[] bills) {

		DeleteBPTemplate<AggWorderplan> bp = new DeleteBPTemplate<AggWorderplan>(
				WorderPlanPluginPoint.DELETE);
		// 增加执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 增加执行后业务规则
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggWorderplan> processer) {
		// TODO 前规则
		IRule<AggWorderplan> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggWorderplan> processer) {
		// TODO 后规则

	}
}
