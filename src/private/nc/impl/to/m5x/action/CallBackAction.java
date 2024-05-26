package nc.impl.to.m5x.action;

import nc.bs.pub.compiler.AbstractCompiler2;
import nc.bs.to.m5x.plugin.ActionPlugInPoint;
import nc.impl.pubapp.pattern.data.bill.BillQuery;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.to.m5x.action.rule.callback.CheckEnableSendApproveRule;
import nc.impl.to.m5x.action.rule.callback.SetFreeStateRule;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pcm.materialacc.AggMaterialaccVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.to.m5x.entity.BillVO;
import nc.ws.intf.OaWorkFlowUtil;

// 调拨订单收回后台类
@SuppressWarnings({"unused", "unchecked", "rawtypes"})
public class CallBackAction {
	public CallBackAction() {
	}

	public BillVO[] returnBill(AbstractCompiler2 script) throws BusinessException {
		BillVO[] vos = null;
		try {
			Object[] obj = script.getPfParameterVO().m_preValueVos;
			int len = obj.length;
			if (0 == len) {
				ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl
						.getNCLangRes()
						.getStrByID("4009011_0", "04009011-0205"));
			}

			vos = new BillVO[len];
			for (int i = 0; i < len; i++) {
				vos[i] = ((BillVO) obj[i]);
			}

			AroundProcesser<BillVO> processer = new AroundProcesser(
					ActionPlugInPoint.CallBackAction);

			addBeforeRule(processer);
			addAfterRule(processer);
			processer.before(vos);
			script.procRecallFlow(script.getPfParameterVO());
			processer.after(vos);
		} catch (Exception e) {
			ExceptionUtils.wrappException(e);
		}
		// 判断是否可收回
		for (BillVO vo : vos) {
			OaWorkFlowUtil.backOaWorkFlow(vo.getParentVO().getPrimaryKey());
		}
		return queryNewVO(vos);
	}

	private void addAfterRule(AroundProcesser<BillVO> processer) {
		IRule<BillVO> rule = new SetFreeStateRule();
		processer.addAfterRule(rule);
	}

	private void addBeforeRule(AroundProcesser<BillVO> processer) {
		IRule<BillVO> rule = new CheckEnableSendApproveRule();
		processer.addBeforeRule(rule);
	}

	private BillVO[] queryNewVO(BillVO[] vos) {
		int len = vos.length;
		String[] pks = new String[len];
		for (int i = 0; i < len; i++) {
			pks[i] = vos[i].getPrimaryKey();
		}
		BillQuery<BillVO> query = new BillQuery(BillVO.class);
		BillVO[] bills = (BillVO[]) query.query(pks);
		return bills;
	}
}
