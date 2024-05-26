package nc.bs.pcm.pcmproject.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pcm.pcmproject.AggPcmProject;
import nc.vo.pub.VOStatus;

/**
 * 标准单据弃审的BP
 */
public class AcePcmprojectUnApproveBP {

	public AggPcmProject[] unApprove(AggPcmProject[] clientBills,
			AggPcmProject[] originBills) {
		for (AggPcmProject clientBill : clientBills) {
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<AggPcmProject> update = new BillUpdate<AggPcmProject>();
		AggPcmProject[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}
}
