package nc.impl.cmp.cash;

import nc.bs.cmp.proxy.CmpPrivateServiceProxy;
import nc.bs.pub.tools.CmpPrivateUtil;
import nc.itf.cmp.cash.ICashDrawManageService;
import nc.md.persist.framework.IMDPersistenceService;
import nc.pub.billcode.itf.IBillcodeManage;
import nc.vo.cmp.cash.AggCashDrawVO;
import nc.vo.cmp.cash.CashDrawVO;
import nc.vo.cmp.pub.constant.CmpBusConstant;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;

// 现金支取
@SuppressWarnings("unused")
public class CashDrawManageServiceImpl implements ICashDrawManageService {
	public CashDrawManageServiceImpl() {
	}

	public AggCashDrawVO updateWithoutStatusControl(AggCashDrawVO aggvo)
			throws BusinessException {
		aggvo.getParentVO().setStatus(1);

		CmpPrivateServiceProxy.getMDService().saveBillWithRealDelete(aggvo);

		return (AggCashDrawVO) CmpPrivateUtil.queryAggvoByPK(aggvo);
	}

	public AggCashDrawVO insert(AggCashDrawVO aggvo) throws BusinessException {
		if (aggvo != null) {
			CashDrawVO vo = aggvo.getParentVO();

			String billno = CmpPrivateServiceProxy.getBillcodeManage()
					.getBillCode_RequiresNew(vo.getBilltypecode(),
							vo.getPk_group(), vo.getPk_org(), aggvo);

			vo.setAttributeValue("billno", billno);

			if (vo.getAttributeValue("systemcode") == null) {
				vo.setAttributeValue("systemcode",
						CmpBusConstant.SYSTEMCODE_CMP);
			}

			AggCashDrawVO retAggVO = (AggCashDrawVO) CmpPrivateUtil
					.updateAggVO(aggvo, 2, false);

			return retAggVO;
		}

		throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
				.getStrByID("3607cash_0", "03607cash-0091"));
	}

	public AggCashDrawVO update(AggCashDrawVO aggvo) throws BusinessException {
		if (aggvo != null) {
			CashDrawVO vo = aggvo.getParentVO();

			AggCashDrawVO retaggvo = (AggCashDrawVO) CmpPrivateUtil
					.updateAggVO(aggvo, 1, false);

			return retaggvo;
		}

		throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
				.getStrByID("3607cash_0", "03607cash-0091"));
	}

	public void delete(AggCashDrawVO aggvo) throws BusinessException {
		CmpPrivateServiceProxy.getMDService().deleteBillFromDB(aggvo);
	}

	public void delete(AggCashDrawVO[] aggvos) throws BusinessException {
		if (aggvos == null) {
			return;
		}
		CircularlyAccessibleValueObject[] cashDrawVOs = new CashDrawVO[aggvos.length];
		for (int i = 0; i < aggvos.length; i++) {
			cashDrawVOs[i] = aggvos[i].getParentVO();
		}
		CmpPrivateServiceProxy.getMDService().deleteBillFromDB(cashDrawVOs);
	}

	public AggCashDrawVO approve(AggCashDrawVO aggvo, boolean isEndStep)
			throws BusinessException {
		return (AggCashDrawVO) CmpPrivateUtil.updateAggVO(aggvo, 1, true);
	}

	public AggCashDrawVO unApprove(AggCashDrawVO aggvo, boolean isEndStep)
			throws BusinessException {
		return (AggCashDrawVO) CmpPrivateUtil.updateAggVO(aggvo, 1, true);
	}

	public AggCashDrawVO submit(AggCashDrawVO aggvo) throws BusinessException {
		AggCashDrawVO vo = (AggCashDrawVO) CmpPrivateUtil.updateAggVO(aggvo, 1, true);
		return vo;
	}

	public AggCashDrawVO unSubmit(AggCashDrawVO aggvo) throws BusinessException {
		return (AggCashDrawVO) CmpPrivateUtil.updateAggVO(aggvo, 1, true);
	}

	public AggCashDrawVO settle(AggCashDrawVO aggvo) throws BusinessException {
		return (AggCashDrawVO) CmpPrivateUtil.updateAggVO(aggvo, 1, true);
	}

	public AggCashDrawVO unSettle(AggCashDrawVO aggvo) throws BusinessException {
		return (AggCashDrawVO) CmpPrivateUtil.updateAggVO(aggvo, 1, true);
	}

	public AggCashDrawVO makeBill(AggCashDrawVO aggvo) throws BusinessException {
		return (AggCashDrawVO) CmpPrivateUtil.updateAggVO(aggvo, 1, true);
	}
}
