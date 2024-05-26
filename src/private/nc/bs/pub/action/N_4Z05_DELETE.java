package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import nc.bs.pm.pmrlcontractalter.plugin.bpplugin.PmRLContractalterPluginPoint;
import nc.vo.pm.rlcontractalter.AggRLContractalterHVO;
import nc.itf.pm.IPmRLContractalterMaintain;

public class N_4Z05_DELETE extends AbstractPfAction<AggRLContractalterHVO> {

	@Override
	protected CompareAroundProcesser<AggRLContractalterHVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggRLContractalterHVO> processor = new CompareAroundProcesser<AggRLContractalterHVO>(
				PmRLContractalterPluginPoint.SCRIPT_DELETE);
		// TODO 在此处添加前后规则
		return processor;
	}

	@Override
	protected AggRLContractalterHVO[] processBP(Object userObj,
			AggRLContractalterHVO[] clientFullVOs, AggRLContractalterHVO[] originBills) {
		IPmRLContractalterMaintain operator = NCLocator.getInstance().lookup(
				IPmRLContractalterMaintain.class);
		try {
			operator.delete(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return clientFullVOs;
	}

}
