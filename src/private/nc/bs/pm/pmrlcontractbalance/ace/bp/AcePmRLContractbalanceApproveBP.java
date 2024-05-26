package nc.bs.pm.pmrlcontractbalance.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pub.VOStatus;
import nc.vo.pm.rlcontractbalance.AggRLContractbalanceHVO;

/**
 * ��׼������˵�BP
 */
public class AcePmRLContractbalanceApproveBP {

	/**
	 * ��˶���
	 * 
	 * @param vos
	 * @param script
	 * @return
	 */
	public AggRLContractbalanceHVO[] approve(AggRLContractbalanceHVO[] clientBills,
			AggRLContractbalanceHVO[] originBills) {
		for (AggRLContractbalanceHVO clientBill : clientBills) {
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<AggRLContractbalanceHVO> update = new BillUpdate<AggRLContractbalanceHVO>();
		AggRLContractbalanceHVO[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

}
