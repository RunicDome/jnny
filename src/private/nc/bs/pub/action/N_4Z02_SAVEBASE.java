package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pm.pmrlcontractbalance.plugin.bpplugin.PmRLContractbalancePluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.pubitf.pmr.pm.IPmRLContractbalanceMaintain;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pm.rlcontractbalance.AggRLContractbalanceHVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class N_4Z02_SAVEBASE extends AbstractPfAction<AggRLContractbalanceHVO> {

	@Override
	protected CompareAroundProcesser<AggRLContractbalanceHVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggRLContractbalanceHVO> processor = null;
		AggRLContractbalanceHVO[] clientFullVOs = (AggRLContractbalanceHVO[]) this.getVos();
		try {
			if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
					.getPrimaryKey())) {
				processor = new CompareAroundProcesser<AggRLContractbalanceHVO>(
						PmRLContractbalancePluginPoint.SCRIPT_UPDATE);
			} else {
				processor = new CompareAroundProcesser<AggRLContractbalanceHVO>(
						PmRLContractbalancePluginPoint.SCRIPT_INSERT);
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO 在此处添加前后规则
		IRule<AggRLContractbalanceHVO> rule = null;

		return processor;
	}

	@Override
	protected AggRLContractbalanceHVO[] processBP(Object userObj,
			AggRLContractbalanceHVO[] clientFullVOs, AggRLContractbalanceHVO[] originBills) {

		AggRLContractbalanceHVO[] bills = null;
		try {
			IPmRLContractbalanceMaintain operator = NCLocator.getInstance()
					.lookup(IPmRLContractbalanceMaintain.class);
			if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
					.getPrimaryKey())) {
				bills = operator.update(clientFullVOs, originBills);
			} else {
				bills = operator.insert(clientFullVOs, originBills);
			}
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}
}
