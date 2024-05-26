package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pm.pmrlcontractschedule.plugin.bpplugin.PmRLContractschedulePluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.CommitStatusCheckRule;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.pubitf.pmr.pm.IPmRLContractscheduleMaintain;
import nc.vo.pm.rlcontractschedule.AggRLContractschedule;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class N_4Z03_SAVE extends AbstractPfAction<AggRLContractschedule> {

	protected CompareAroundProcesser<AggRLContractschedule> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggRLContractschedule> processor = new CompareAroundProcesser<AggRLContractschedule>(
				PmRLContractschedulePluginPoint.SEND_APPROVE);
		// TODO 在此处添加审核前后规则
		IRule<AggRLContractschedule> rule = new CommitStatusCheckRule();
		processor.addBeforeRule(rule);
		return processor;
	}

	@Override
	protected AggRLContractschedule[] processBP(Object userObj,
			AggRLContractschedule[] clientFullVOs, AggRLContractschedule[] originBills) {
		IPmRLContractscheduleMaintain operator = NCLocator.getInstance().lookup(
				IPmRLContractscheduleMaintain.class);
		AggRLContractschedule[] bills = null;
		try {
			bills = operator.save(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
