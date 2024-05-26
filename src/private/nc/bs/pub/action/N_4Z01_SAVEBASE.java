package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pm.pmfeebalancect.plugin.bpplugin.PmFeebalanceCtPluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.pubitf.pmr.pm.IPmFeebalanceCtMaintain;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class N_4Z01_SAVEBASE extends AbstractPfAction<AggPmFeebalance> {

	@Override
	protected CompareAroundProcesser<AggPmFeebalance> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggPmFeebalance> processor = null;
		AggPmFeebalance[] clientFullVOs = (AggPmFeebalance[]) this.getVos();
		try {
			if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
					.getPrimaryKey())) {
				processor = new CompareAroundProcesser<AggPmFeebalance>(
						PmFeebalanceCtPluginPoint.SCRIPT_UPDATE);
			} else {
				processor = new CompareAroundProcesser<AggPmFeebalance>(
						PmFeebalanceCtPluginPoint.SCRIPT_INSERT);
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO 在此处添加前后规则
		IRule<AggPmFeebalance> rule = null;

		return processor;
	}

	@Override
	protected AggPmFeebalance[] processBP(Object userObj,
			AggPmFeebalance[] clientFullVOs, AggPmFeebalance[] originBills) {

		AggPmFeebalance[] bills = null;
		try {
			IPmFeebalanceCtMaintain operator = NCLocator.getInstance()
					.lookup(IPmFeebalanceCtMaintain.class);
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
