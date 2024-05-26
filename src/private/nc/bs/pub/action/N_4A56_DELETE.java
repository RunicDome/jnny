package nc.bs.pub.action;

import nc.bs.aim.equipmaintain.plugin.bpplugin.EquipmaintainPluginPoint;
import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.aim.IEquipmaintainMaintain;
import nc.vo.aim.equipmaintain.AggEquipmaintain;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class N_4A56_DELETE extends AbstractPfAction<AggEquipmaintain> {

	@Override
	protected CompareAroundProcesser<AggEquipmaintain> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggEquipmaintain> processor = new CompareAroundProcesser<AggEquipmaintain>(
				EquipmaintainPluginPoint.SCRIPT_DELETE);
		// TODO 在此处添加前后规则
		return processor;
	}

	@Override
	protected AggEquipmaintain[] processBP(Object userObj,
			AggEquipmaintain[] clientFullVOs, AggEquipmaintain[] originBills) {
		IEquipmaintainMaintain operator = NCLocator.getInstance().lookup(
				IEquipmaintainMaintain.class);
		try {
			operator.delete(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return clientFullVOs;
	}

}
