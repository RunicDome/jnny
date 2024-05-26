package nc.bs.pm.pmfeebalancect.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pub.VOStatus;

/**
 * 标准单据弃审的BP
 */
public class AcePmFeebalanceCtUnApproveBP {

	public AggPmFeebalance[] unApprove(AggPmFeebalance[] clientBills,
			AggPmFeebalance[] originBills) {
		for (AggPmFeebalance clientBill : clientBills) {
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<AggPmFeebalance> update = new BillUpdate<AggPmFeebalance>();
		AggPmFeebalance[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}
}
