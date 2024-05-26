package nc.bs.pub.action;

import nc.bs.aim.equipmaintain.plugin.bpplugin.EquipmaintainPluginPoint;
import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.CommitStatusCheckRule;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.aim.IEquipmaintainMaintain;
import nc.vo.aim.equipmaintain.AggEquipmaintain;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

@SuppressWarnings("unchecked")
public class N_4A56_SAVE extends AbstractPfAction<AggEquipmaintain> {

	protected CompareAroundProcesser<AggEquipmaintain> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggEquipmaintain> processor = new CompareAroundProcesser<AggEquipmaintain>(
				EquipmaintainPluginPoint.SEND_APPROVE);
		// TODO 在此处添加审核前后规则
		IRule<AggEquipmaintain> rule = new CommitStatusCheckRule();
		processor.addBeforeRule(rule);
		return processor;
	}

	@Override
	protected AggEquipmaintain[] processBP(Object userObj,
			AggEquipmaintain[] clientFullVOs, AggEquipmaintain[] originBills) {
		IEquipmaintainMaintain operator = NCLocator.getInstance().lookup(
				IEquipmaintainMaintain.class);
		AggEquipmaintain[] bills = null;
		try {
			bills = operator.save(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
