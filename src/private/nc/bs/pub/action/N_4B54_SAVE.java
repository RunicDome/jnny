package nc.bs.pub.action;

import nc.bs.aim.equipinsp.plugin.bpplugin.EquipinspPluginPoint;
import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.CommitStatusCheckRule;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.aim.IEquipinspMaintain;
import nc.vo.aim.equipinsp.AggEquipinsp;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

@SuppressWarnings({ "unchecked" })
public class N_4B54_SAVE extends AbstractPfAction<AggEquipinsp> {

	protected CompareAroundProcesser<AggEquipinsp> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggEquipinsp> processor = new CompareAroundProcesser<AggEquipinsp>(
				EquipinspPluginPoint.SEND_APPROVE);
		// TODO 在此处添加审核前后规则
		IRule<AggEquipinsp> rule = new CommitStatusCheckRule();
		processor.addBeforeRule(rule);
		return processor;
	}

	@Override
	protected AggEquipinsp[] processBP(Object userObj,
			AggEquipinsp[] clientFullVOs, AggEquipinsp[] originBills) {
		IEquipinspMaintain operator = NCLocator.getInstance().lookup(
				IEquipinspMaintain.class);
		AggEquipinsp[] bills = null;
		try {
			bills = operator.save(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
