package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pm.pmfeebalancect.plugin.bpplugin.PmFeebalanceCtPluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.UnapproveStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.pubitf.pmr.pm.IPmFeebalanceCtMaintain;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class N_4Z01_UNAPPROVE extends AbstractPfAction<AggPmFeebalance> {

	@Override
	protected CompareAroundProcesser<AggPmFeebalance> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggPmFeebalance> processor = new CompareAroundProcesser<AggPmFeebalance>(
				PmFeebalanceCtPluginPoint.UNAPPROVE);
		// TODO 在此处添加前后规则
		processor.addBeforeRule(new UnapproveStatusCheckRule());

		return processor;
	}

	@Override
	protected AggPmFeebalance[] processBP(Object userObj,
			AggPmFeebalance[] clientFullVOs, AggPmFeebalance[] originBills) {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AggPmFeebalance[] bills = null;
		try {
			IPmFeebalanceCtMaintain operator = NCLocator.getInstance()
					.lookup(IPmFeebalanceCtMaintain.class);
			bills = operator.unapprove(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
