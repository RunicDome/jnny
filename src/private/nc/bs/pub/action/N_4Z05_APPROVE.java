package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.ApproveStatusCheckRule;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import nc.bs.pm.pmrlcontractalter.plugin.bpplugin.PmRLContractalterPluginPoint;
import nc.vo.pm.rlcontractalter.AggRLContractalterHVO;
import nc.itf.pm.IPmRLContractalterMaintain;

public class N_4Z05_APPROVE extends AbstractPfAction<AggRLContractalterHVO> {

	public N_4Z05_APPROVE() {
		super();
	}

	@Override
	protected CompareAroundProcesser<AggRLContractalterHVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggRLContractalterHVO> processor = new CompareAroundProcesser<AggRLContractalterHVO>(
				PmRLContractalterPluginPoint.APPROVE);
		processor.addBeforeRule(new ApproveStatusCheckRule());
		return processor;
	}

	@Override
	protected AggRLContractalterHVO[] processBP(Object userObj,
			AggRLContractalterHVO[] clientFullVOs, AggRLContractalterHVO[] originBills) {
		AggRLContractalterHVO[] bills = null;
		IPmRLContractalterMaintain operator = NCLocator.getInstance().lookup(
				IPmRLContractalterMaintain.class);
		try {
			bills = operator.approve(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
