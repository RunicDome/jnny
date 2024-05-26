package nc.bs.pm.pmrlcontractbalance.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pm.rlcontractbalance.AggRLContractbalanceHVO;
import nc.vo.pub.VOStatus;

/**
 * 标准单据弃审的BP
 */
public class AcePmRLContractbalanceUnApproveBP {

	public AggRLContractbalanceHVO[] unApprove(AggRLContractbalanceHVO[] clientBills,
			AggRLContractbalanceHVO[] originBills) {
		for (AggRLContractbalanceHVO clientBill : clientBills) {
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<AggRLContractbalanceHVO> update = new BillUpdate<AggRLContractbalanceHVO>();
		AggRLContractbalanceHVO[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}
}
