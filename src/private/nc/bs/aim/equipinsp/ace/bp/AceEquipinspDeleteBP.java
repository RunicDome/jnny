package nc.bs.aim.equipinsp.ace.bp;

import nc.bs.aim.equipinsp.plugin.bpplugin.EquipinspPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.aim.equipinsp.AggEquipinsp;

/**
 * 标准单据删除BP
 */
@SuppressWarnings("unchecked")
public class AceEquipinspDeleteBP {

	public void delete(AggEquipinsp[] bills) {

		DeleteBPTemplate<AggEquipinsp> bp = new DeleteBPTemplate<AggEquipinsp>(
				EquipinspPluginPoint.DELETE);
		// 增加执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 增加执行后业务规则
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggEquipinsp> processer) {
		// TODO 前规则
		IRule<AggEquipinsp> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggEquipinsp> processer) {
		// TODO 后规则

	}
}
