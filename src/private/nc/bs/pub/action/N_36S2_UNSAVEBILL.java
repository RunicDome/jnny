package nc.bs.pub.action;

import nc.bs.cmp.cash.bpplugin.CashBasePluginPoint;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.script.CmpScriptChecker;
import nc.bs.pub.tools.CmpPrivateUtil;
import nc.bs.pub.tools.CmpPrivateVOAdapter;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.pubitf.cmp.bankaccbook.IAccQueryService;
import nc.vo.cmp.cash.AggCashDrawVO;
import nc.vo.cmp.cash.BillStatusEnum;
import nc.vo.cmp.cash.CashDrawVO;
import nc.vo.cmp.exception.ExceptionHandler;
import nc.vo.pbm.budget.BudgetBillVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.tmpub.util.TMBillOrgVUtils;
import nc.ws.intf.OaWorkFlowUtil;

@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
public class N_36S2_UNSAVEBILL extends AbstractPfAction<AggCashDrawVO> {
	public N_36S2_UNSAVEBILL() {
	}

	protected CompareAroundProcesser<AggCashDrawVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggCashDrawVO> processor = new CompareAroundProcesser(
				CashBasePluginPoint.UNSEND_APPROVE);

		processor.addBeforeRule(new IRule() {
			public void process(AggCashDrawVO[] vos) {
				for (AggCashDrawVO aggCashDrawVO : vos) {
					try {
						CmpScriptChecker.getInstance().check4UnSave(
								aggCashDrawVO);
					} catch (BusinessException e) {
						ExceptionHandler.handleRuntimeException(e);
					}
				}
			}

			@Override
			public void process(Object[] paramArrayOfE) {
				// TODO Auto-generated method stub

			}
		});
		return processor;
	}

	protected AggCashDrawVO[] processBP(Object userObj,
			AggCashDrawVO[] clientFullVOs, AggCashDrawVO[] originBills) {
		for (AggCashDrawVO aggCashDrawVO : clientFullVOs) {
			try {
				IAccQueryService accQueryService = (IAccQueryService) NCLocator
						.getInstance().lookup(IAccQueryService.class);
				accQueryService.checkSettleAccount(
						(String) aggCashDrawVO.getParentVO().getAttributeValue(
								"pk_org"),
						(UFDate) aggCashDrawVO.getParentVO().getAttributeValue(
								"commitdate"));
			} catch (BusinessException e) {
				ExceptionHandler.handleRuntimeException(e);
			}
			CashDrawVO cashDrawVo = aggCashDrawVO.getParentVO();

			cashDrawVo.setAttributeValue("billstatus",
					BillStatusEnum.SAVE.value());

			cashDrawVo.setAttributeValue("vbillstatus", Integer.valueOf(-1));

			cashDrawVo.setAttributeValue("commiter", null);

			cashDrawVo.setAttributeValue("commitdate", null);

			cashDrawVo.setAttributeValue("approver", null);

			cashDrawVo.setAttributeValue("approvedate", null);
			cashDrawVo.setAttributeValue("approvetime", null);

			cashDrawVo.setStatus(1);

			UFDate buzDate = CmpPrivateUtil.getBuzDate(cashDrawVo);
			try {
				TMBillOrgVUtils.setOrgV(aggCashDrawVO, buzDate);

				CmpPrivateVOAdapter.prepareVOForCashDepositInfo(aggCashDrawVO);
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		}

		AggCashDrawVO[] returnVos = (AggCashDrawVO[]) new BillUpdate().update(
				clientFullVOs, originBills);
		for (AggCashDrawVO temp : returnVos) {
			try {
				OaWorkFlowUtil.backOaWorkFlow(temp.getPrimaryKey());
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				ExceptionHandler.handleRuntimeException(e);
			}
		}
		return returnVos;
	}
}
