package nc.bs.pub.action;

import nc.bs.aim.worderplan.plugin.bpplugin.WorderPlanPluginPoint;
import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.ApproveStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.aim.IWorderPlanMaintain;
import nc.vo.aim.worderplan.AggWorderplan;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

@SuppressWarnings("unchecked")
public class N_4B52_APPROVE extends AbstractPfAction<AggWorderplan> {

	public N_4B52_APPROVE() {
		super();
	}

	@Override
	protected CompareAroundProcesser<AggWorderplan> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggWorderplan> processor = new CompareAroundProcesser<AggWorderplan>(
				WorderPlanPluginPoint.APPROVE);
		processor.addBeforeRule(new ApproveStatusCheckRule());
		return processor;
	}

	@Override
	protected AggWorderplan[] processBP(Object userObj,
			AggWorderplan[] clientFullVOs, AggWorderplan[] originBills) {
		AggWorderplan[] bills = null;
		IWorderPlanMaintain operator = NCLocator.getInstance().lookup(
				IWorderPlanMaintain.class);
		try {
			bills = operator.approve(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
