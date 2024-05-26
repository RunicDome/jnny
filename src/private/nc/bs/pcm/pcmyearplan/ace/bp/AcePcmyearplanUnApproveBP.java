package nc.bs.pcm.pcmyearplan.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pcm.pcmyearplan.AggPcmYearplan;
import nc.vo.pub.VOStatus;

/**
 * 标准单据弃审的BP
 */
public class AcePcmyearplanUnApproveBP {

	public AggPcmYearplan[] unApprove(AggPcmYearplan[] clientBills,
			AggPcmYearplan[] originBills) {
		for (AggPcmYearplan clientBill : clientBills) {
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<AggPcmYearplan> update = new BillUpdate<AggPcmYearplan>();
		AggPcmYearplan[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}
}
