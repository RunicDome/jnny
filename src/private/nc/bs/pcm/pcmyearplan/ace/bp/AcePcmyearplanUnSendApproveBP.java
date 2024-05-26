package nc.bs.pcm.pcmyearplan.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pcm.pcmyearplan.AggPcmYearplan;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;

/**
 * 标准单据收回的BP
 */
public class AcePcmyearplanUnSendApproveBP {

	public AggPcmYearplan[] unSend(AggPcmYearplan[] clientBills,
			AggPcmYearplan[] originBills) {
		// 把VO持久化到数据库中
		this.setHeadVOStatus(clientBills);
		BillUpdate<AggPcmYearplan> update = new BillUpdate<AggPcmYearplan>();
		AggPcmYearplan[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

	private void setHeadVOStatus(AggPcmYearplan[] clientBills) {
		for (AggPcmYearplan clientBill : clientBills) {
			clientBill.getParentVO().setAttributeValue(
					"${vmObject.billstatus}", BillStatusEnum.FREE.value());
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
	}
}
