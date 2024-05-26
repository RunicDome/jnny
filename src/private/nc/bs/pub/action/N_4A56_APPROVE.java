package nc.bs.pub.action;

import nc.bs.aim.equipmaintain.plugin.bpplugin.EquipmaintainPluginPoint;
import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.ApproveStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.aim.IEquipmaintainMaintain;
import nc.vo.aim.equipmaintain.AggEquipmaintain;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

@SuppressWarnings("unchecked")
public class N_4A56_APPROVE extends AbstractPfAction<AggEquipmaintain> {

	public N_4A56_APPROVE() {
		super();
	}

	@Override
	protected CompareAroundProcesser<AggEquipmaintain> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggEquipmaintain> processor = new CompareAroundProcesser<AggEquipmaintain>(
				EquipmaintainPluginPoint.APPROVE);
		processor.addBeforeRule(new ApproveStatusCheckRule());
		return processor;
	}

	@Override
	protected AggEquipmaintain[] processBP(Object userObj,
			AggEquipmaintain[] clientFullVOs, AggEquipmaintain[] originBills) {
		AggEquipmaintain[] bills = null;
		IEquipmaintainMaintain operator = NCLocator.getInstance().lookup(
				IEquipmaintainMaintain.class);
		try {
			bills = operator.approve(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
