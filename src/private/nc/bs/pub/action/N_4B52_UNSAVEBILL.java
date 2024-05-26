package nc.bs.pub.action;

import nc.bs.aim.worderplan.plugin.bpplugin.WorderPlanPluginPoint;
import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.UncommitStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.aim.IWorderPlanMaintain;
import nc.vo.aim.worderplan.AggWorderplan;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

@SuppressWarnings("unchecked")
public class N_4B52_UNSAVEBILL extends AbstractPfAction<AggWorderplan> {

	@Override
	protected CompareAroundProcesser<AggWorderplan> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggWorderplan> processor = new CompareAroundProcesser<AggWorderplan>(
				WorderPlanPluginPoint.UNSEND_APPROVE);
		// TODO 在此处添加前后规则
		processor.addBeforeRule(new UncommitStatusCheckRule());

		return processor;
	}

	@Override
	protected AggWorderplan[] processBP(Object userObj,
			AggWorderplan[] clientFullVOs, AggWorderplan[] originBills) {
		IWorderPlanMaintain operator = NCLocator.getInstance().lookup(
				IWorderPlanMaintain.class);
		AggWorderplan[] bills = null;
		try {
			bills = operator.unsave(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
