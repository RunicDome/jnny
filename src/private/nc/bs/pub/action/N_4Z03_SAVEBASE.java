package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pm.pmrlcontractschedule.plugin.bpplugin.PmRLContractschedulePluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.pubitf.pmr.pm.IPmRLContractscheduleMaintain;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pm.rlcontractschedule.AggRLContractschedule;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class N_4Z03_SAVEBASE extends AbstractPfAction<AggRLContractschedule> {

	@Override
	protected CompareAroundProcesser<AggRLContractschedule> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggRLContractschedule> processor = null;
		AggRLContractschedule[] clientFullVOs = (AggRLContractschedule[]) this.getVos();
		try {
			if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
					.getPrimaryKey())) {
				processor = new CompareAroundProcesser<AggRLContractschedule>(
						PmRLContractschedulePluginPoint.SCRIPT_UPDATE);
			} else {
				processor = new CompareAroundProcesser<AggRLContractschedule>(
						PmRLContractschedulePluginPoint.SCRIPT_INSERT);
			}
		} catch (BusinessException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		// TODO 在此处添加前后规则
		IRule<AggRLContractschedule> rule = null;

		return processor;
	}

	@Override
	protected AggRLContractschedule[] processBP(Object userObj,
			AggRLContractschedule[] clientFullVOs, AggRLContractschedule[] originBills) {

		AggRLContractschedule[] bills = null;
		try {
			IPmRLContractscheduleMaintain operator = NCLocator.getInstance()
					.lookup(IPmRLContractscheduleMaintain.class);
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
