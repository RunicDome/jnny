package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pm.pmfeebalancect.plugin.bpplugin.PmFeebalanceCtPluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.pubitf.pmr.pm.IPmFeebalanceCtMaintain;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class N_4Z01_DELETE extends AbstractPfAction<AggPmFeebalance> {

	@Override
	protected CompareAroundProcesser<AggPmFeebalance> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggPmFeebalance> processor = new CompareAroundProcesser<AggPmFeebalance>(
				PmFeebalanceCtPluginPoint.SCRIPT_DELETE);
		// TODO 在此处添加前后规则
		return processor;
	}

	@Override
	protected AggPmFeebalance[] processBP(Object userObj,
			AggPmFeebalance[] clientFullVOs, AggPmFeebalance[] originBills) {
		IPmFeebalanceCtMaintain operator = NCLocator.getInstance().lookup(
				IPmFeebalanceCtMaintain.class);
		try {
			operator.delete(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return clientFullVOs;
	}

}
