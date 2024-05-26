package nc.bs.pm.pmrlcontractalter.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pm.rlcontractalter.AggRLContractalterHVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;

/**
 * 标准单据收回的BP
 */
public class AcePmRLContractalterUnSendApproveBP {

	public AggRLContractalterHVO[] unSend(AggRLContractalterHVO[] clientBills,
			AggRLContractalterHVO[] originBills) {
		// 把VO持久化到数据库中
		this.setHeadVOStatus(clientBills);
		BillUpdate<AggRLContractalterHVO> update = new BillUpdate<AggRLContractalterHVO>();
		AggRLContractalterHVO[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

	private void setHeadVOStatus(AggRLContractalterHVO[] clientBills) {
		for (AggRLContractalterHVO clientBill : clientBills) {
			clientBill.getParentVO().setAttributeValue("${vmObject.billstatus}",
					BillStatusEnum.FREE.value());
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
	}
}
