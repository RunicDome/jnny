package nc.bs.pub.action;

import nc.bs.aim.equipinsp.plugin.bpplugin.EquipinspPluginPoint;
import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.ApproveStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.aim.IEquipinspMaintain;
import nc.vo.aim.equipinsp.AggEquipinsp;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

@SuppressWarnings({ "unchecked" })
public class N_4B54_APPROVE extends AbstractPfAction<AggEquipinsp> {

	public N_4B54_APPROVE() {
		super();
	}

	@Override
	protected CompareAroundProcesser<AggEquipinsp> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggEquipinsp> processor = new CompareAroundProcesser<AggEquipinsp>(
				EquipinspPluginPoint.APPROVE);
		processor.addBeforeRule(new ApproveStatusCheckRule());
		return processor;
	}

	@Override
	protected AggEquipinsp[] processBP(Object userObj,
			AggEquipinsp[] clientFullVOs, AggEquipinsp[] originBills) {
		AggEquipinsp[] bills = null;
		IEquipinspMaintain operator = NCLocator.getInstance().lookup(
				IEquipinspMaintain.class);
		try {
			bills = operator.approve(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
