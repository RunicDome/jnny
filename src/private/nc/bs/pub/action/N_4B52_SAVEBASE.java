package nc.bs.pub.action;

import nc.bs.aim.worderplan.plugin.bpplugin.WorderPlanPluginPoint;
import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.aim.IWorderPlanMaintain;
import nc.vo.aim.worderplan.AggWorderplan;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class N_4B52_SAVEBASE extends AbstractPfAction<AggWorderplan> {

	@Override
	protected CompareAroundProcesser<AggWorderplan> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggWorderplan> processor = null;
		AggWorderplan[] clientFullVOs = (AggWorderplan[]) this.getVos();
		if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
				.getPrimaryKey())) {
			processor = new CompareAroundProcesser<AggWorderplan>(
					WorderPlanPluginPoint.SCRIPT_UPDATE);
		} else {
			processor = new CompareAroundProcesser<AggWorderplan>(
					WorderPlanPluginPoint.SCRIPT_INSERT);
		}
		// TODO 在此处添加前后规则
		// IRule<AggWorderplan> rule = null;

		return processor;
	}

	@Override
	protected AggWorderplan[] processBP(Object userObj,
			AggWorderplan[] clientFullVOs, AggWorderplan[] originBills) {

		AggWorderplan[] bills = null;
		try {
			IWorderPlanMaintain operator = NCLocator.getInstance().lookup(
					IWorderPlanMaintain.class);
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
