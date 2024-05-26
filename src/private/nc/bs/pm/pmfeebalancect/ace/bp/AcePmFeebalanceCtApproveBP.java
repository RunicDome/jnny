package nc.bs.pm.pmfeebalancect.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;
import nc.vo.pm.feebalance.AggPmFeebalance;

/**
 * ��׼������˵�BP
 */
public class AcePmFeebalanceCtApproveBP {

	/**
	 * ��˶���
	 * 
	 * @param vos
	 * @param script
	 * @return
	 */
	public AggPmFeebalance[] approve(AggPmFeebalance[] clientBills,
			AggPmFeebalance[] originBills) {
		for (AggPmFeebalance clientBill : clientBills) {
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<AggPmFeebalance> update = new BillUpdate<AggPmFeebalance>();
		AggPmFeebalance[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

}
