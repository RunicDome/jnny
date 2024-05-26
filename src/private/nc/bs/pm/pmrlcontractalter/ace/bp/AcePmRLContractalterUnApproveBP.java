package nc.bs.pm.pmrlcontractalter.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pm.rlcontractalter.AggRLContractalterHVO;
import nc.vo.pub.VOStatus;

/**
 * 标准单据弃审的BP
 */
public class AcePmRLContractalterUnApproveBP {

	public AggRLContractalterHVO[] unApprove(AggRLContractalterHVO[] clientBills,
			AggRLContractalterHVO[] originBills) {
		for (AggRLContractalterHVO clientBill : clientBills) {
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<AggRLContractalterHVO> update = new BillUpdate<AggRLContractalterHVO>();
		AggRLContractalterHVO[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}
}
