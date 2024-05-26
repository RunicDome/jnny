package nc.bs.pub.action;

import nc.bs.aim.equipmaintain.plugin.bpplugin.EquipmaintainPluginPoint;
import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.UnapproveStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.aim.IEquipmaintainMaintain;
import nc.vo.aim.equipmaintain.AggEquipmaintain;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

@SuppressWarnings("unchecked")
public class N_4A56_UNAPPROVE extends AbstractPfAction<AggEquipmaintain> {

	@Override
	protected CompareAroundProcesser<AggEquipmaintain> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggEquipmaintain> processor = new CompareAroundProcesser<AggEquipmaintain>(
				EquipmaintainPluginPoint.UNAPPROVE);
		// TODO 在此处添加前后规则
		processor.addBeforeRule(new UnapproveStatusCheckRule());

		return processor;
	}

	@Override
	protected AggEquipmaintain[] processBP(Object userObj,
			AggEquipmaintain[] clientFullVOs, AggEquipmaintain[] originBills) {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AggEquipmaintain[] bills = null;
		try {
			IEquipmaintainMaintain operator = NCLocator.getInstance().lookup(
					IEquipmaintainMaintain.class);
			bills = operator.unapprove(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
