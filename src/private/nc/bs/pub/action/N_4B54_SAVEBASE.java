package nc.bs.pub.action;

import nc.bs.aim.equipinsp.plugin.bpplugin.EquipinspPluginPoint;
import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.aim.IEquipinspMaintain;
import nc.vo.aim.equipinsp.AggEquipinsp;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class N_4B54_SAVEBASE extends AbstractPfAction<AggEquipinsp> {

	@Override
	protected CompareAroundProcesser<AggEquipinsp> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggEquipinsp> processor = null;
		AggEquipinsp[] clientFullVOs = (AggEquipinsp[]) this.getVos();
		if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
				.getPrimaryKey())) {
			processor = new CompareAroundProcesser<AggEquipinsp>(
					EquipinspPluginPoint.SCRIPT_UPDATE);
		} else {
			processor = new CompareAroundProcesser<AggEquipinsp>(
					EquipinspPluginPoint.SCRIPT_INSERT);
		}
		// TODO 在此处添加前后规则
		// IRule<AggEquipinsp> rule = null;

		return processor;
	}

	@Override
	protected AggEquipinsp[] processBP(Object userObj,
			AggEquipinsp[] clientFullVOs, AggEquipinsp[] originBills) {

		AggEquipinsp[] bills = null;
		try {
			IEquipinspMaintain operator = NCLocator.getInstance().lookup(
					IEquipinspMaintain.class);
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
