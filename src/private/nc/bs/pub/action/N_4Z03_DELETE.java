package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pm.pmrlcontractschedule.plugin.bpplugin.PmRLContractschedulePluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.pubitf.pmr.pm.IPmRLContractscheduleMaintain;
import nc.vo.pm.rlcontractschedule.AggRLContractschedule;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class N_4Z03_DELETE extends AbstractPfAction<AggRLContractschedule> {

	@Override
	protected CompareAroundProcesser<AggRLContractschedule> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggRLContractschedule> processor = new CompareAroundProcesser<AggRLContractschedule>(
				PmRLContractschedulePluginPoint.SCRIPT_DELETE);
		// TODO 在此处添加前后规则
		return processor;
	}

	@Override
	protected AggRLContractschedule[] processBP(Object userObj,
			AggRLContractschedule[] clientFullVOs, AggRLContractschedule[] originBills) {
		IPmRLContractscheduleMaintain operator = NCLocator.getInstance().lookup(
				IPmRLContractscheduleMaintain.class);
		try {
			operator.delete(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return clientFullVOs;
	}

}
