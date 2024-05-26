package nc.bs.cmp.pfxx;

import java.util.HashMap;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pfxx.ISwapContext;
import nc.bs.pfxx.plugin.AbstractPfxxPlugin;
import nc.bs.trade.business.HYPubBO;
import nc.cmp.utils.InterfaceLocator;
import nc.itf.cmp.IApplyService;
import nc.itf.uap.pf.IWorkflowMachine;
import nc.itf.uap.pf.IplatFormEntry;
import nc.uif.pub.exception.UifException;
import nc.vo.cmp.apply.AggApplyVO;
import nc.vo.pfxx.auxiliary.AggxsysregisterVO;
import nc.vo.pfxx.util.PfxxPluginUtils;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;

@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
public class CMPApplyPlugin extends AbstractPfxxPlugin {
	public CMPApplyPlugin() {
	}

	protected Object processBill(Object vo, ISwapContext swapContext,
			AggxsysregisterVO aggxsysvo) throws BusinessException {
		if (vo == null) {
			return null;
		}

		AbstractBill aggvo = null;

		if ((vo instanceof AggApplyVO)) {
			aggvo = (AggApplyVO) vo;
		}

		if (aggvo != null) {
			String vopk = PfxxPluginUtils.queryBillPKBeforeSaveOrUpdate(
					swapContext.getBilltype(), swapContext.getDocID());

			if (vopk != null) {
				throw new BusinessException("暂不支持修改功能！");
			} else {
				Logger.info("如果此单据没有导入过,保存新单据...");
				InvocationInfoProxy.getInstance().setGroupId(
						aggvo.getParentVO().getAttributeValue("pk_group") + "");
				InvocationInfoProxy.getInstance()
						.setUserId(
								aggvo.getParentVO().getAttributeValue(
										"billmaker")
										+ "");
				AggregatedValueObject[] vos = null;

				aggvo.getParentVO().setStatus(VOStatus.NEW);
				vos = (AggregatedValueObject[]) doWorkFlow(aggvo);

				vopk = vos[0].getParentVO().getPrimaryKey();
				// 付款申请 提交单据
				AggregatedValueObject afterAggVo = ((AggregatedValueObject[]) getIplatFormEntry()
						.processAction("SAVE", "36D1", null, aggvo, null, null))[0];

				// 付款申请审批单据
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("APPROVE", "36D1", afterAggVo,
								hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				getIplatFormEntry().processAction("APPROVE", "36D1",
						worknoteVO, afterAggVo, null, getEparam());
				PfxxPluginUtils.addDocIDVsPKContrast(swapContext.getBilltype(),
						swapContext.getDocID(), vopk);
			}

			return vopk;
		} else {
			return null;
		}
	}

	private HashMap<String, Object> getEparam() {
		HashMap<String, Object> eParam = new HashMap<String, Object>();
		eParam.put("notechecked", "notechecked");
		return eParam;
	}


	private IplatFormEntry getIplatFormEntry() {
		return NCLocator.getInstance().lookup(IplatFormEntry.class);
	}

	private Object doWorkFlow(AggregatedValueObject aggvo)
			throws BusinessException {
		IplatFormEntry iplatFormEntry = InterfaceLocator.getIplatFormEntry();
		HashMap<String, String> paramMap = new HashMap();
		paramMap.put("silently", "silently");
		AggApplyVO[] agg1 = { (AggApplyVO) aggvo };
		AggApplyVO[] agg2 = agg1.clone();
		return NCLocator.getInstance().lookup(IApplyService.class)
				.insert(agg1, agg2);
		// return iplatFormEntry.processAction("WRITE","36D1",null,aggvo, null,
		// null);
	}
}
