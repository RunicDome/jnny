package nc.bs.pub.action;

import nc.bs.aim.worderplan.plugin.bpplugin.WorderPlanPluginPoint;
import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.CommitStatusCheckRule;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.aim.IWorderPlanMaintain;
import nc.vo.aim.worderplan.AggWorderplan;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

@SuppressWarnings("unchecked")
public class N_4B52_SAVE extends AbstractPfAction<AggWorderplan> {

	protected CompareAroundProcesser<AggWorderplan> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggWorderplan> processor = new CompareAroundProcesser<AggWorderplan>(
				WorderPlanPluginPoint.SEND_APPROVE);
		// TODO 在此处添加审核前后规则
		IRule<AggWorderplan> rule = new CommitStatusCheckRule();
		processor.addBeforeRule(rule);
		return processor;
	}

	@Override
	protected AggWorderplan[] processBP(Object userObj,
			AggWorderplan[] clientFullVOs, AggWorderplan[] originBills) {
		IWorderPlanMaintain operator = NCLocator.getInstance().lookup(
				IWorderPlanMaintain.class);
		AggWorderplan[] bills = null;
		try {
			bills = operator.save(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
