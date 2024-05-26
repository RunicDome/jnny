package nc.bs.aim.equipmaintain.ace.bp;

import nc.bs.aim.equipmaintain.plugin.bpplugin.EquipmaintainPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.aim.equipmaintain.AggEquipmaintain;

/**
 * 标准单据删除BP
 */
@SuppressWarnings("unchecked")
public class AceEquipmaintainDeleteBP {

	public void delete(AggEquipmaintain[] bills) {

		DeleteBPTemplate<AggEquipmaintain> bp = new DeleteBPTemplate<AggEquipmaintain>(
				EquipmaintainPluginPoint.DELETE);
		// 增加执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 增加执行后业务规则
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggEquipmaintain> processer) {
		// TODO 前规则
		IRule<AggEquipmaintain> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggEquipmaintain> processer) {
		// TODO 后规则

	}
}
