package nc.bs.pm.pmrlcontractschedule.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pm.rlcontractschedule.AggRLContractschedule;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;

/**
 * ��׼�����ջص�BP
 */
public class AcePmRLContractscheduleUnSendApproveBP {

	public AggRLContractschedule[] unSend(AggRLContractschedule[] clientBills,
			AggRLContractschedule[] originBills) {
		// ��VO�־û������ݿ���
		this.setHeadVOStatus(clientBills);
		BillUpdate<AggRLContractschedule> update = new BillUpdate<AggRLContractschedule>();
		AggRLContractschedule[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

	private void setHeadVOStatus(AggRLContractschedule[] clientBills) {
		for (AggRLContractschedule clientBill : clientBills) {
			clientBill.getParentVO().setAttributeValue("${vmObject.billstatus}",
					BillStatusEnum.FREE.value());
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
	}
}
