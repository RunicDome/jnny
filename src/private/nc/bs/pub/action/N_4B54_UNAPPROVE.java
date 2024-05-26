package nc.bs.pub.action;

import nc.bs.aim.equipinsp.plugin.bpplugin.EquipinspPluginPoint;
import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.UnapproveStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.aim.IEquipinspMaintain;
import nc.vo.aim.equipinsp.AggEquipinsp;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

@SuppressWarnings({ "unchecked" })
public class N_4B54_UNAPPROVE extends AbstractPfAction<AggEquipinsp> {

	@Override
	protected CompareAroundProcesser<AggEquipinsp> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggEquipinsp> processor = new CompareAroundProcesser<AggEquipinsp>(
				EquipinspPluginPoint.UNAPPROVE);
		// TODO 在此处添加前后规则
		processor.addBeforeRule(new UnapproveStatusCheckRule());

		return processor;
	}

	@Override
	protected AggEquipinsp[] processBP(Object userObj,
			AggEquipinsp[] clientFullVOs, AggEquipinsp[] originBills) {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AggEquipinsp[] bills = null;
		try {
			IEquipinspMaintain operator = NCLocator.getInstance().lookup(
					IEquipinspMaintain.class);
			bills = operator.unapprove(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
