package nc.bs.pcm.pcmproject.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pcm.pcmproject.AggPcmProject;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;

/**
 * 标准单据收回的BP
 */
public class AcePcmprojectUnSendApproveBP {

	public AggPcmProject[] unSend(AggPcmProject[] clientBills,
			AggPcmProject[] originBills) {
		// 把VO持久化到数据库中
		this.setHeadVOStatus(clientBills);
		BillUpdate<AggPcmProject> update = new BillUpdate<AggPcmProject>();
		AggPcmProject[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

	private void setHeadVOStatus(AggPcmProject[] clientBills) {
		for (AggPcmProject clientBill : clientBills) {
			clientBill.getParentVO().setAttributeValue(
					"${vmObject.billstatus}", BillStatusEnum.FREE.value());
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
	}
}
