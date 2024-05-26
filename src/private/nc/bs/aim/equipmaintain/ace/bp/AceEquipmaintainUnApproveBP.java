package nc.bs.aim.equipmaintain.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.aim.equipmaintain.AggEquipmaintain;
import nc.vo.pub.VOStatus;

/**
 * 标准单据弃审的BP
 */
public class AceEquipmaintainUnApproveBP {

	public AggEquipmaintain[] unApprove(AggEquipmaintain[] clientBills,
			AggEquipmaintain[] originBills) {
		for (AggEquipmaintain clientBill : clientBills) {
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<AggEquipmaintain> update = new BillUpdate<AggEquipmaintain>();
		AggEquipmaintain[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}
}
