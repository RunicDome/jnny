package nc.bs.pm.pmrlcontractalter.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pub.VOStatus;
import nc.vo.pm.rlcontractalter.AggRLContractalterHVO;

/**
 * 标准单据审核的BP
 */
public class AcePmRLContractalterApproveBP {

	/**
	 * 审核动作
	 * 
	 * @param vos
	 * @param script
	 * @return
	 */
	public AggRLContractalterHVO[] approve(AggRLContractalterHVO[] clientBills,
			AggRLContractalterHVO[] originBills) {
		for (AggRLContractalterHVO clientBill : clientBills) {
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<AggRLContractalterHVO> update = new BillUpdate<AggRLContractalterHVO>();
		AggRLContractalterHVO[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

}
