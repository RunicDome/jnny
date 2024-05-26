package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pcm.pcmyearplan.plugin.bpplugin.PcmyearplanPluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.UncommitStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.pcm.IPcmyearplanMaintain;
import nc.vo.pcm.pcmyearplan.AggPcmYearplan;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

@SuppressWarnings("unchecked")
public class N_4D92_UNSAVEBILL extends AbstractPfAction<AggPcmYearplan> {

	@Override
	protected CompareAroundProcesser<AggPcmYearplan> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggPcmYearplan> processor = new CompareAroundProcesser<AggPcmYearplan>(
				PcmyearplanPluginPoint.UNSEND_APPROVE);
		// TODO 在此处添加前后规则
		processor.addBeforeRule(new UncommitStatusCheckRule());

		return processor;
	}

	@Override
	protected AggPcmYearplan[] processBP(Object userObj,
			AggPcmYearplan[] clientFullVOs, AggPcmYearplan[] originBills) {
		IPcmyearplanMaintain operator = NCLocator.getInstance().lookup(
				IPcmyearplanMaintain.class);
		AggPcmYearplan[] bills = null;
		try {
			bills = operator.unsave(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
