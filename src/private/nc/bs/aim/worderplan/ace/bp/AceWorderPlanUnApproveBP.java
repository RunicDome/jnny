package nc.bs.aim.worderplan.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.aim.worderplan.AggWorderplan;
import nc.vo.pub.VOStatus;

/**
 * 标准单据弃审的BP
 */
public class AceWorderPlanUnApproveBP {

	public AggWorderplan[] unApprove(AggWorderplan[] clientBills,
			AggWorderplan[] originBills) {
		for (AggWorderplan clientBill : clientBills) {
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<AggWorderplan> update = new BillUpdate<AggWorderplan>();
		AggWorderplan[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}
}
