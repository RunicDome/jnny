package nc.bs.aim.equipinsp.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.aim.equipinsp.AggEquipinsp;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;

/**
 * 标准单据收回的BP
 */
public class AceEquipinspUnSendApproveBP {

	public AggEquipinsp[] unSend(AggEquipinsp[] clientBills,
			AggEquipinsp[] originBills) {
		// 把VO持久化到数据库中
		this.setHeadVOStatus(clientBills);
		BillUpdate<AggEquipinsp> update = new BillUpdate<AggEquipinsp>();
		AggEquipinsp[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

	private void setHeadVOStatus(AggEquipinsp[] clientBills) {
		for (AggEquipinsp clientBill : clientBills) {
			clientBill.getParentVO().setAttributeValue(
					"${vmObject.billstatus}", BillStatusEnum.FREE.value());
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
	}
}
