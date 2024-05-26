package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pm.pmrlcontractbalance.plugin.bpplugin.PmRLContractbalancePluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.pubitf.pmr.pm.IPmRLContractbalanceMaintain;
import nc.vo.pm.rlcontractbalance.AggRLContractbalanceHVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class N_4Z02_DELETE extends AbstractPfAction<AggRLContractbalanceHVO> {

	@Override
	protected CompareAroundProcesser<AggRLContractbalanceHVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggRLContractbalanceHVO> processor = new CompareAroundProcesser<AggRLContractbalanceHVO>(
				PmRLContractbalancePluginPoint.SCRIPT_DELETE);
		// TODO 在此处添加前后规则
		return processor;
	}

	@Override
	protected AggRLContractbalanceHVO[] processBP(Object userObj,
			AggRLContractbalanceHVO[] clientFullVOs, AggRLContractbalanceHVO[] originBills) {
		IPmRLContractbalanceMaintain operator = NCLocator.getInstance().lookup(
				IPmRLContractbalanceMaintain.class);
		try {
			operator.delete(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return clientFullVOs;
	}

}
