package nc.bs.pm.pmrlcontractbalance.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pm.rlcontractbalance.AggRLContractbalanceHVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;

/**
 * 标准单据收回的BP
 */
public class AcePmRLContractbalanceUnSendApproveBP {

	public AggRLContractbalanceHVO[] unSend(AggRLContractbalanceHVO[] clientBills,
			AggRLContractbalanceHVO[] originBills) {
		// 把VO持久化到数据库中
		this.setHeadVOStatus(clientBills);
		BillUpdate<AggRLContractbalanceHVO> update = new BillUpdate<AggRLContractbalanceHVO>();
		AggRLContractbalanceHVO[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

	private void setHeadVOStatus(AggRLContractbalanceHVO[] clientBills) {
		for (AggRLContractbalanceHVO clientBill : clientBills) {
			clientBill.getParentVO().setAttributeValue("${vmObject.billstatus}",
					BillStatusEnum.FREE.value());
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
	}
}
