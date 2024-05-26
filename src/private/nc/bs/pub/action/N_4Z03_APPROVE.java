package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pm.pmrlcontractschedule.plugin.bpplugin.PmRLContractschedulePluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.ApproveStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.pubitf.pmr.pm.IPmRLContractscheduleMaintain;
import nc.vo.pm.rlcontractschedule.AggRLContractschedule;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class N_4Z03_APPROVE extends AbstractPfAction<AggRLContractschedule> {

	public N_4Z03_APPROVE() {
		super();
	}

	@Override
	protected CompareAroundProcesser<AggRLContractschedule> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggRLContractschedule> processor = new CompareAroundProcesser<AggRLContractschedule>(
				PmRLContractschedulePluginPoint.APPROVE);
		processor.addBeforeRule(new ApproveStatusCheckRule());
		return processor;
	}

	@Override
	protected AggRLContractschedule[] processBP(Object userObj,
			AggRLContractschedule[] clientFullVOs, AggRLContractschedule[] originBills) {
		AggRLContractschedule[] bills = null;
		IPmRLContractscheduleMaintain operator = NCLocator.getInstance().lookup(
				IPmRLContractscheduleMaintain.class);
		try {
			bills = operator.approve(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
