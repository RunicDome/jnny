package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pcm.pcmyearplan.plugin.bpplugin.PcmyearplanPluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.ApproveStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.pcm.IPcmyearplanMaintain;
import nc.vo.pcm.pcmyearplan.AggPcmYearplan;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

@SuppressWarnings("unchecked")
public class N_4D92_APPROVE extends AbstractPfAction<AggPcmYearplan> {

	public N_4D92_APPROVE() {
		super();
	}

	@Override
	protected CompareAroundProcesser<AggPcmYearplan> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggPcmYearplan> processor = new CompareAroundProcesser<AggPcmYearplan>(
				PcmyearplanPluginPoint.APPROVE);
		processor.addBeforeRule(new ApproveStatusCheckRule());
		return processor;
	}

	@Override
	protected AggPcmYearplan[] processBP(Object userObj,
			AggPcmYearplan[] clientFullVOs, AggPcmYearplan[] originBills) {
		AggPcmYearplan[] bills = null;
		IPcmyearplanMaintain operator = NCLocator.getInstance().lookup(
				IPcmyearplanMaintain.class);
		try {
			bills = operator.approve(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
