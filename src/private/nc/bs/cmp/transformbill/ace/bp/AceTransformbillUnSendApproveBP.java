package nc.bs.cmp.transformbill.ace.bp;

import nc.bs.cmp.transformbill.plugin.bpplugin.TransformbillPluginPoint;
import nc.bs.cmp.transformbill.rule.TfbFillUnCommitDataRule;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.cmp.bill.TransformBillAggVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.ws.intf.OaWorkFlowUtil;

//划账结算   --->收回
@SuppressWarnings({ "restriction", "unchecked", "rawtypes" })
public class AceTransformbillUnSendApproveBP {
	public AceTransformbillUnSendApproveBP() {
	}

	public TransformBillAggVO[] unSend(TransformBillAggVO[] clientBills,
			TransformBillAggVO[] originBills) throws BusinessException {
		UpdateBPTemplate<TransformBillAggVO> bp = new UpdateBPTemplate(
				TransformbillPluginPoint.UNSEND_APPROVE);

		addBeforeRule(bp.getAroundProcesser());

		addAfterRule(bp.getAroundProcesser());
		for (int i = 0; i < originBills.length; i++) {
			TransformBillAggVO transformBillAggVO = originBills[i];
			OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
					transformBillAggVO.getParentVO().getPk_org());
			if (null != orgVO.getDef2()) {
				OaWorkFlowUtil.backOaWorkFlow(transformBillAggVO.getParentVO().getPrimaryKey());
			}
		}
		TransformBillAggVO[] returnVos = (TransformBillAggVO[]) bp.update(
				clientBills, originBills);
		return returnVos;
	}

	private void addBeforeRule(
			CompareAroundProcesser<TransformBillAggVO> processer) {
		IRule<TransformBillAggVO> rule = new TfbFillUnCommitDataRule();
		processer.addBeforeRule(rule);
	}

	private void addAfterRule(
			CompareAroundProcesser<TransformBillAggVO> processer) {
	}
}
