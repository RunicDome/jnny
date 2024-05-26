package nc.bs.pub.action;

import nc.bs.aim.equipmaintain.plugin.bpplugin.EquipmaintainPluginPoint;
import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.aim.IEquipmaintainMaintain;
import nc.vo.aim.equipmaintain.AggEquipmaintain;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class N_4A56_SAVEBASE extends AbstractPfAction<AggEquipmaintain> {

	@Override
	protected CompareAroundProcesser<AggEquipmaintain> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggEquipmaintain> processor = null;
		AggEquipmaintain[] clientFullVOs = (AggEquipmaintain[]) this.getVos();
		if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
				.getPrimaryKey())) {
			processor = new CompareAroundProcesser<AggEquipmaintain>(
					EquipmaintainPluginPoint.SCRIPT_UPDATE);
		} else {
			processor = new CompareAroundProcesser<AggEquipmaintain>(
					EquipmaintainPluginPoint.SCRIPT_INSERT);
		}
		// TODO 在此处添加前后规则
		// IRule<AggEquipmaintain> rule = null;

		return processor;
	}

	@Override
	protected AggEquipmaintain[] processBP(Object userObj,
			AggEquipmaintain[] clientFullVOs, AggEquipmaintain[] originBills) {

		AggEquipmaintain[] bills = null;
		try {
			IEquipmaintainMaintain operator = NCLocator.getInstance().lookup(
					IEquipmaintainMaintain.class);
			if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
					.getPrimaryKey())) {
				bills = operator.update(clientFullVOs, originBills);
			} else {
				bills = operator.insert(clientFullVOs, originBills);
			}
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}
}
