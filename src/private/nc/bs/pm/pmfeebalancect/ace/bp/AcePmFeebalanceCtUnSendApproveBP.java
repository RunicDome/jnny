package nc.bs.pm.pmfeebalancect.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;

/**
 * ��׼�����ջص�BP
 */
public class AcePmFeebalanceCtUnSendApproveBP {

	public AggPmFeebalance[] unSend(AggPmFeebalance[] clientBills,
			AggPmFeebalance[] originBills) {
		// ��VO�־û������ݿ���
		this.setHeadVOStatus(clientBills);
		BillUpdate<AggPmFeebalance> update = new BillUpdate<AggPmFeebalance>();
		AggPmFeebalance[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

	private void setHeadVOStatus(AggPmFeebalance[] clientBills) {
		for (AggPmFeebalance clientBill : clientBills) {
			clientBill.getParentVO().setAttributeValue("${vmObject.billstatus}",
					BillStatusEnum.FREE.value());
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
	}
}
