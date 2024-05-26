package nc.bs.aim.equipmaintain.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.aim.equipmaintain.AggEquipmaintain;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;

/**
 * 标准单据收回的BP
 */
public class AceEquipmaintainUnSendApproveBP {

	public AggEquipmaintain[] unSend(AggEquipmaintain[] clientBills,
			AggEquipmaintain[] originBills) {
		// 把VO持久化到数据库中
		this.setHeadVOStatus(clientBills);
		BillUpdate<AggEquipmaintain> update = new BillUpdate<AggEquipmaintain>();
		AggEquipmaintain[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

	private void setHeadVOStatus(AggEquipmaintain[] clientBills) {
		for (AggEquipmaintain clientBill : clientBills) {
			clientBill.getParentVO().setAttributeValue(
					"${vmObject.billstatus}", BillStatusEnum.FREE.value());
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
	}
}
