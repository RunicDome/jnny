package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.UncommitStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import nc.bs.pm.pmrlcontractalter.plugin.bpplugin.PmRLContractalterPluginPoint;
import nc.vo.pm.rlcontractalter.AggRLContractalterHVO;
import nc.itf.pm.IPmRLContractalterMaintain;

public class N_4Z05_UNSAVEBILL extends AbstractPfAction<AggRLContractalterHVO> {

	@Override
	protected CompareAroundProcesser<AggRLContractalterHVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggRLContractalterHVO> processor = new CompareAroundProcesser<AggRLContractalterHVO>(
				PmRLContractalterPluginPoint.UNSEND_APPROVE);
		// TODO 在此处添加前后规则
		processor.addBeforeRule(new UncommitStatusCheckRule());

		return processor;
	}

	@Override
	protected AggRLContractalterHVO[] processBP(Object userObj,
			AggRLContractalterHVO[] clientFullVOs, AggRLContractalterHVO[] originBills) {
		IPmRLContractalterMaintain operator = NCLocator.getInstance().lookup(
				IPmRLContractalterMaintain.class);
		AggRLContractalterHVO[] bills = null;
		try {
			bills = operator.unsave(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
