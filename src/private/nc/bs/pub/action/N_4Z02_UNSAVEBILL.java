package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pm.pmrlcontractbalance.plugin.bpplugin.PmRLContractbalancePluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.UncommitStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.pubitf.pmr.pm.IPmRLContractbalanceMaintain;
import nc.vo.pm.rlcontractbalance.AggRLContractbalanceHVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class N_4Z02_UNSAVEBILL extends AbstractPfAction<AggRLContractbalanceHVO> {

	@Override
	protected CompareAroundProcesser<AggRLContractbalanceHVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggRLContractbalanceHVO> processor = new CompareAroundProcesser<AggRLContractbalanceHVO>(
				PmRLContractbalancePluginPoint.UNSEND_APPROVE);
		// TODO 在此处添加前后规则
		processor.addBeforeRule(new UncommitStatusCheckRule());

		return processor;
	}

	@Override
	protected AggRLContractbalanceHVO[] processBP(Object userObj,
			AggRLContractbalanceHVO[] clientFullVOs, AggRLContractbalanceHVO[] originBills) {
		IPmRLContractbalanceMaintain operator = NCLocator.getInstance().lookup(
				IPmRLContractbalanceMaintain.class);
		AggRLContractbalanceHVO[] bills = null;
		try {
			bills = operator.unsave(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
