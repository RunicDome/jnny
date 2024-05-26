package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pcm.pcmyearplan.plugin.bpplugin.PcmyearplanPluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.UnapproveStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.pcm.IPcmyearplanMaintain;
import nc.vo.pcm.pcmyearplan.AggPcmYearplan;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

@SuppressWarnings("unchecked")
public class N_4D92_UNAPPROVE extends AbstractPfAction<AggPcmYearplan> {

	@Override
	protected CompareAroundProcesser<AggPcmYearplan> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggPcmYearplan> processor = new CompareAroundProcesser<AggPcmYearplan>(
				PcmyearplanPluginPoint.UNAPPROVE);
		// TODO 在此处添加前后规则
		processor.addBeforeRule(new UnapproveStatusCheckRule());

		return processor;
	}

	@Override
	protected AggPcmYearplan[] processBP(Object userObj,
			AggPcmYearplan[] clientFullVOs, AggPcmYearplan[] originBills) {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AggPcmYearplan[] bills = null;
		try {
			IPcmyearplanMaintain operator = NCLocator.getInstance().lookup(
					IPcmyearplanMaintain.class);
			bills = operator.unapprove(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
