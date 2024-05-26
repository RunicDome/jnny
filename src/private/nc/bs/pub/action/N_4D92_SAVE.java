package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pcm.pcmyearplan.plugin.bpplugin.PcmyearplanPluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.CommitStatusCheckRule;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.pcm.IPcmyearplanMaintain;
import nc.vo.pcm.pcmyearplan.AggPcmYearplan;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

@SuppressWarnings("unchecked")
public class N_4D92_SAVE extends AbstractPfAction<AggPcmYearplan> {

	protected CompareAroundProcesser<AggPcmYearplan> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggPcmYearplan> processor = new CompareAroundProcesser<AggPcmYearplan>(
				PcmyearplanPluginPoint.SEND_APPROVE);
		// TODO 在此处添加审核前后规则
		IRule<AggPcmYearplan> rule = new CommitStatusCheckRule();
		processor.addBeforeRule(rule);
		return processor;
	}

	@Override
	protected AggPcmYearplan[] processBP(Object userObj,
			AggPcmYearplan[] clientFullVOs, AggPcmYearplan[] originBills) {
		IPcmyearplanMaintain operator = NCLocator.getInstance().lookup(
				IPcmyearplanMaintain.class);
		AggPcmYearplan[] bills = null;
		try {
			bills = operator.save(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
