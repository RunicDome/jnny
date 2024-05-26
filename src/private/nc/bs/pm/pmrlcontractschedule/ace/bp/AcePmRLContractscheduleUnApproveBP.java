package nc.bs.pm.pmrlcontractschedule.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pm.rlcontractschedule.AggRLContractschedule;
import nc.vo.pub.VOStatus;

/**
 * 标准单据弃审的BP
 */
public class AcePmRLContractscheduleUnApproveBP {

	public AggRLContractschedule[] unApprove(AggRLContractschedule[] clientBills,
			AggRLContractschedule[] originBills) {
		for (AggRLContractschedule clientBill : clientBills) {
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<AggRLContractschedule> update = new BillUpdate<AggRLContractschedule>();
		AggRLContractschedule[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}
}
