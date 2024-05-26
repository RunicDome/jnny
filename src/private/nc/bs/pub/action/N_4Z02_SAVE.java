package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pm.pmrlcontractbalance.plugin.bpplugin.PmRLContractbalancePluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.CommitStatusCheckRule;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.pubitf.pmr.pm.IPmRLContractbalanceMaintain;
import nc.vo.pm.rlcontractbalance.AggRLContractbalanceHVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class N_4Z02_SAVE extends AbstractPfAction<AggRLContractbalanceHVO> {

	protected CompareAroundProcesser<AggRLContractbalanceHVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggRLContractbalanceHVO> processor = new CompareAroundProcesser<AggRLContractbalanceHVO>(
				PmRLContractbalancePluginPoint.SEND_APPROVE);
		// TODO 在此处添加审核前后规则
		IRule<AggRLContractbalanceHVO> rule = new CommitStatusCheckRule();
		processor.addBeforeRule(rule);
		return processor;
	}

	@Override
	protected AggRLContractbalanceHVO[] processBP(Object userObj,
			AggRLContractbalanceHVO[] clientFullVOs, AggRLContractbalanceHVO[] originBills) {
		IPmRLContractbalanceMaintain operator = NCLocator.getInstance().lookup(
				IPmRLContractbalanceMaintain.class);
		AggRLContractbalanceHVO[] bills = null;
		try {
			bills = operator.save(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
