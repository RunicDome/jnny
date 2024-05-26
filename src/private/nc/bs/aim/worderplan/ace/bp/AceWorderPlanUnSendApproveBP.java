package nc.bs.aim.worderplan.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.aim.worderplan.AggWorderplan;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;

/**
 * 标准单据收回的BP
 */
public class AceWorderPlanUnSendApproveBP {

	public AggWorderplan[] unSend(AggWorderplan[] clientBills,
			AggWorderplan[] originBills) {
		// 把VO持久化到数据库中
		this.setHeadVOStatus(clientBills);
		BillUpdate<AggWorderplan> update = new BillUpdate<AggWorderplan>();
		AggWorderplan[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

	private void setHeadVOStatus(AggWorderplan[] clientBills) {
		for (AggWorderplan clientBill : clientBills) {
			clientBill.getParentVO().setAttributeValue(
					"${vmObject.billstatus}", BillStatusEnum.FREE.value());
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
	}
}
