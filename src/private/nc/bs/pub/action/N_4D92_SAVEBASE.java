package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pcm.pcmyearplan.plugin.bpplugin.PcmyearplanPluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.pcm.IPcmyearplanMaintain;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pcm.pcmyearplan.AggPcmYearplan;
import nc.vo.pcm.pcmyearplan.PcmYearplan;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class N_4D92_SAVEBASE extends AbstractPfAction<AggPcmYearplan> {

	@Override
	protected CompareAroundProcesser<AggPcmYearplan> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggPcmYearplan> processor = null;
		AggPcmYearplan[] clientFullVOs = (AggPcmYearplan[]) this.getVos();
		if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
				.getPrimaryKey())) {
			processor = new CompareAroundProcesser<AggPcmYearplan>(
					PcmyearplanPluginPoint.SCRIPT_UPDATE);
		} else {
			processor = new CompareAroundProcesser<AggPcmYearplan>(
					PcmyearplanPluginPoint.SCRIPT_INSERT);
		}
		// TODO 在此处添加前后规则
		// IRule<AggPcmYearplan> rule = null;

		return processor;
	}

	@Override
	protected AggPcmYearplan[] processBP(Object userObj,
			AggPcmYearplan[] clientFullVOs, AggPcmYearplan[] originBills) {

		AggPcmYearplan[] bills = null;
		try {
			IPcmyearplanMaintain operator = NCLocator.getInstance().lookup(
					IPcmyearplanMaintain.class);
			if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
					.getPrimaryKey())) {
				bills = operator.update(clientFullVOs, originBills);
			} else {
				PcmYearplan[] hvos = (PcmYearplan[]) new HYPubBO()
						.queryByCondition(PcmYearplan.class,
								"dr = 0 and plan_year = '"
										+ clientFullVOs[0].getParentVO()
												.getPlan_year()
										+ "' and pk_org = '"
										+ clientFullVOs[0].getParentVO()
												.getPk_org() + "'");
				if (hvos != null && hvos.length > 0) {
					throw new BusinessException("当前年份已经做过计划，请修改！");
				}
				bills = operator.insert(clientFullVOs, originBills);
			}
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}
}
