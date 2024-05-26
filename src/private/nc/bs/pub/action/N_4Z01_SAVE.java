package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pm.pmfeebalancect.plugin.bpplugin.PmFeebalanceCtPluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.CommitStatusCheckRule;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.pubitf.pmr.pm.IPmFeebalanceCtMaintain;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class N_4Z01_SAVE extends AbstractPfAction<AggPmFeebalance> {

	protected CompareAroundProcesser<AggPmFeebalance> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggPmFeebalance> processor = new CompareAroundProcesser<AggPmFeebalance>(
				PmFeebalanceCtPluginPoint.SEND_APPROVE);
		// TODO 在此处添加审核前后规则
		IRule<AggPmFeebalance> rule = new CommitStatusCheckRule();
		processor.addBeforeRule(rule);
		return processor;
	}

	@Override
	protected AggPmFeebalance[] processBP(Object userObj,
			AggPmFeebalance[] clientFullVOs, AggPmFeebalance[] originBills) {
		IPmFeebalanceCtMaintain operator = NCLocator.getInstance().lookup(
				IPmFeebalanceCtMaintain.class);
		AggPmFeebalance[] bills = null;
		try {
			bills = operator.save(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
