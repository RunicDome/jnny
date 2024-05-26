package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pm.pmfeebalancect.plugin.bpplugin.PmFeebalanceCtPluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.ApproveStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.pubitf.pmr.pm.IPmFeebalanceCtMaintain;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class N_4Z01_APPROVE extends AbstractPfAction<AggPmFeebalance> {

	public N_4Z01_APPROVE() {
		super();
	}

	@Override
	protected CompareAroundProcesser<AggPmFeebalance> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggPmFeebalance> processor = new CompareAroundProcesser<AggPmFeebalance>(
				PmFeebalanceCtPluginPoint.APPROVE);
		processor.addBeforeRule(new ApproveStatusCheckRule());
		return processor;
	}

	@Override
	protected AggPmFeebalance[] processBP(Object userObj,
			AggPmFeebalance[] clientFullVOs, AggPmFeebalance[] originBills) {
		AggPmFeebalance[] bills = null;
		IPmFeebalanceCtMaintain operator = NCLocator.getInstance().lookup(
				IPmFeebalanceCtMaintain.class);
		try {
			bills = operator.approve(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
