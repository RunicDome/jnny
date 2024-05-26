package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pcm.pcmproject.plugin.bpplugin.PcmprojectPluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.pcm.IPcmprojectMaintain;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pcm.pcmproject.AggPcmProject;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class N_4D90_SAVEBASE extends AbstractPfAction<AggPcmProject> {

	@Override
	protected CompareAroundProcesser<AggPcmProject> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggPcmProject> processor = null;
		AggPcmProject[] clientFullVOs = (AggPcmProject[]) this.getVos();
		if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
				.getPrimaryKey())) {
			processor = new CompareAroundProcesser<AggPcmProject>(
					PcmprojectPluginPoint.SCRIPT_UPDATE);
		} else {
			processor = new CompareAroundProcesser<AggPcmProject>(
					PcmprojectPluginPoint.SCRIPT_INSERT);
		}
		// TODO 在此处添加前后规则
		// IRule<AggPcmProject> rule = null;

		return processor;
	}

	@Override
	protected AggPcmProject[] processBP(Object userObj,
			AggPcmProject[] clientFullVOs, AggPcmProject[] originBills) {

		AggPcmProject[] bills = null;
		try {
			IPcmprojectMaintain operator = NCLocator.getInstance().lookup(
					IPcmprojectMaintain.class);
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
