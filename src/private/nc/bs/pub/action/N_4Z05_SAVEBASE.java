package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import nc.bs.pm.pmrlcontractalter.plugin.bpplugin.PmRLContractalterPluginPoint;
import nc.vo.pm.rlcontractalter.AggRLContractalterHVO;
import nc.itf.pm.IPmRLContractalterMaintain;

public class N_4Z05_SAVEBASE extends AbstractPfAction<AggRLContractalterHVO> {

	@Override
	protected CompareAroundProcesser<AggRLContractalterHVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggRLContractalterHVO> processor = null;
		AggRLContractalterHVO[] clientFullVOs = (AggRLContractalterHVO[]) this.getVos();
		try {
			if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
					.getPrimaryKey())) {
				processor = new CompareAroundProcesser<AggRLContractalterHVO>(
						PmRLContractalterPluginPoint.SCRIPT_UPDATE);
			} else {
				processor = new CompareAroundProcesser<AggRLContractalterHVO>(
						PmRLContractalterPluginPoint.SCRIPT_INSERT);
			}
		} catch (BusinessException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		// TODO 在此处添加前后规则
		IRule<AggRLContractalterHVO> rule = null;

		return processor;
	}

	@Override
	protected AggRLContractalterHVO[] processBP(Object userObj,
			AggRLContractalterHVO[] clientFullVOs, AggRLContractalterHVO[] originBills) {

		AggRLContractalterHVO[] bills = null;
		try {
			IPmRLContractalterMaintain operator = NCLocator.getInstance()
					.lookup(IPmRLContractalterMaintain.class);
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
