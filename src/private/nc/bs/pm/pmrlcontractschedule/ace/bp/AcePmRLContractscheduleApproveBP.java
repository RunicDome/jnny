package nc.bs.pm.pmrlcontractschedule.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pub.VOStatus;
import nc.vo.pm.rlcontractschedule.AggRLContractschedule;

/**
 * 标准单据审核的BP
 */
public class AcePmRLContractscheduleApproveBP {

	/**
	 * 审核动作
	 * 
	 * @param vos
	 * @param script
	 * @return
	 */
	public AggRLContractschedule[] approve(AggRLContractschedule[] clientBills,
			AggRLContractschedule[] originBills) {
		for (AggRLContractschedule clientBill : clientBills) {
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<AggRLContractschedule> update = new BillUpdate<AggRLContractschedule>();
		AggRLContractschedule[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

}
