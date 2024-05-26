package nc.impl.ps.paybill;

import java.util.Map;
import nc.bs.ps.paybill.PayBillMaintainBS;
import nc.itf.ps.paybill.IPayBillService;
import nc.ps.paybill.PayBillOpEnum;
import nc.vo.bd.errorlog.ErrLogReturnValue;
import nc.vo.ps.paybill.AggPayBillVO;
import nc.vo.ps.paybill.PayBillRtnObj;
import nc.vo.ps.paybill.PayBillVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

// 付款单impl
public class PayBillServiceImpl implements IPayBillService {
	public PayBillServiceImpl() {
	}

	public void deletePayBill(PayBillVO[] vos) throws BusinessException {
		new PayBillMaintainBS().deletePayBill(vos);
	}

	public AggPayBillVO savePayBill(AggPayBillVO aggVOs, Object userObj)
			throws BusinessException {
		before(aggVOs);
		return new PayBillMaintainBS().insert(aggVOs);
	}

	// 付款单保存校验
	public void before(AggPayBillVO aggVOs) {
		// TODO Auto-generated method stub
		int i = 0;
	}

	public AggPayBillVO[] approvePayBill(AggPayBillVO[] aggVOs, Object userObj)
			throws BusinessException {
		return new PayBillMaintainBS().approvePayBill(aggVOs, userObj);
	}

	public AggPayBillVO[] unApprovePayBill(AggPayBillVO[] billVO, Object userObj)
			throws BusinessException {
		return new PayBillMaintainBS().unApprovePayBill(billVO, userObj);
	}

	public AggPayBillVO[] commitApprovePayBill(AggPayBillVO[] billVO,
			Object userObj) throws BusinessException {
		return new PayBillMaintainBS().commitApprovePayBill(billVO, userObj);
	}

	public AggPayBillVO[] unCommitApprovePayBill(AggPayBillVO[] billVO,
			Object userObj) throws BusinessException {
		return new PayBillMaintainBS().unCommitApprovePayBill(billVO, userObj);
	}

	public AggPayBillVO updatePayBill(AggPayBillVO updateBillVO, Object userObj)
			throws BusinessException {
		before(updateBillVO);
		return new PayBillMaintainBS().updatePayBill(updateBillVO, userObj);
	}

	public AggPayBillVO frozenPayBill(AggPayBillVO aggVO)
			throws BusinessException {
		return new PayBillMaintainBS().frozenPayBill(aggVO);
	}

	public AggPayBillVO frozenPayBillFromOtherNode(AggPayBillVO aggVO,
			int frozenSrc) throws BusinessException {
		return new PayBillMaintainBS().frozenPayBillFromOtherNode(aggVO,
				frozenSrc);
	}

	public AggPayBillVO unfreezePayBill(AggPayBillVO aggVO)
			throws BusinessException {
		return new PayBillMaintainBS().unfreezePayBill(aggVO);
	}

	public AggPayBillVO unfreezePayBillFromOtherNode(AggPayBillVO aggVO,
			int frozenSrc) throws BusinessException {
		return new PayBillMaintainBS().unfreezePayBillFromOtherNode(aggVO,
				frozenSrc);
	}

	public AggPayBillVO[] generate2ap(AggPayBillVO[] aggVOs, Object userObj)
			throws BusinessException {
		return new PayBillMaintainBS().generate2ap(aggVOs);
	}

	public PayBillRtnObj ungenerate2ap(AggPayBillVO[] aggVOs, Object userObj)
			throws BusinessException {
		return new PayBillMaintainBS().ungenerate2ap(aggVOs);
	}

	public ErrLogReturnValue batchUpdateByPks(String attr,
			Map<String, Object> attr_valueMap, String[] permissionOrgs,
			String[] selectedOrgs, String[] selectedPKs, boolean isNeedReturnVOs)
			throws BusinessException {
		return new PayBillMaintainBS().batchUpdateByPks(attr, attr_valueMap,
				permissionOrgs, selectedOrgs, selectedPKs, isNeedReturnVOs);
	}

	public ErrLogReturnValue batchUpdateByCondition(String attr,
			Map<String, Object> attr_valueMap, String[] permissionOrgs,
			String[] selectedOrgs, String condition) throws BusinessException {
		return new PayBillMaintainBS().batchUpdateByCondition(attr,
				attr_valueMap, permissionOrgs, selectedOrgs, condition);
	}

	public PayBillRtnObj merge2ap(AggPayBillVO[] aggvos)
			throws BusinessException {
		return new PayBillMaintainBS().merge2ap(aggvos);
	}

	public void updateApplyPlanMoney(Map<String, UFDouble> moneyMap,
			PayBillOpEnum opEnum) throws BusinessException {
		AggPayBillVO[] aggPayBillVOS = nc.bs.ps.plan.proxy.PlanProxy
				.getPayBillQueryService().queryPayBillByPKs(
						(String[]) moneyMap.keySet().toArray(new String[0]),
						false);
		new PayBillMaintainBS().pubUpdateMoney(aggPayBillVOS, moneyMap, opEnum);
	}
}
