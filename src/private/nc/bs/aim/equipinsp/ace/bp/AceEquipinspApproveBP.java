package nc.bs.aim.equipinsp.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.aim.equipinsp.AggEquipinsp;
import nc.vo.pub.VOStatus;

/**
 * 标准单据审核的BP
 */
public class AceEquipinspApproveBP {

	/**
	 * 审核动作
	 * 
	 * @param vos
	 * @param script
	 * @return
	 */
	public AggEquipinsp[] approve(AggEquipinsp[] clientBills,
			AggEquipinsp[] originBills) {
		for (AggEquipinsp clientBill : clientBills) {
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<AggEquipinsp> update = new BillUpdate<AggEquipinsp>();
		AggEquipinsp[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

}
