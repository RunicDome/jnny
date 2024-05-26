package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pm.pmrlcontractschedule.plugin.bpplugin.PmRLContractschedulePluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.UncommitStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.pubitf.pmr.pm.IPmRLContractscheduleMaintain;
import nc.vo.pm.rlcontractschedule.AggRLContractschedule;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class N_4Z03_UNSAVEBILL extends AbstractPfAction<AggRLContractschedule> {

	@Override
	protected CompareAroundProcesser<AggRLContractschedule> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggRLContractschedule> processor = new CompareAroundProcesser<AggRLContractschedule>(
				PmRLContractschedulePluginPoint.UNSEND_APPROVE);
		// TODO 在此处添加前后规则
		processor.addBeforeRule(new UncommitStatusCheckRule());

		return processor;
	}

	@Override
	protected AggRLContractschedule[] processBP(Object userObj,
			AggRLContractschedule[] clientFullVOs, AggRLContractschedule[] originBills) {
		IPmRLContractscheduleMaintain operator = NCLocator.getInstance().lookup(
				IPmRLContractscheduleMaintain.class);
		AggRLContractschedule[] bills = null;
		try {
			bills = operator.unsave(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
