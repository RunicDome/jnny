package nc.bs.aim.equipmaintain.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pub.VOStatus;
import nc.vo.aim.equipmaintain.AggEquipmaintain;

/**
 * 标准单据审核的BP
 */
public class AceEquipmaintainApproveBP {

	/**
	 * 审核动作
	 * 
	 * @param vos
	 * @param script
	 * @return
	 */
	public AggEquipmaintain[] approve(AggEquipmaintain[] clientBills,
			AggEquipmaintain[] originBills) {
		for (AggEquipmaintain clientBill : clientBills) {
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<AggEquipmaintain> update = new BillUpdate<AggEquipmaintain>();
		AggEquipmaintain[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

}
