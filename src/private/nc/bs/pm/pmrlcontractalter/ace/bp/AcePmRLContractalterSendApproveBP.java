package nc.bs.pm.pmrlcontractalter.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pm.rlcontractalter.AggRLContractalterHVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;

/**
 * ��׼���������BP
 */
public class AcePmRLContractalterSendApproveBP {
	/**
	 * ������
	 * 
	 * @param vos
	 *            ����VO����
	 * @param script
	 *            ���ݶ����ű�����
	 * @return �����ĵ���VO����
	 */

	public AggRLContractalterHVO[] sendApprove(AggRLContractalterHVO[] clientBills,
			AggRLContractalterHVO[] originBills) {
		for (AggRLContractalterHVO clientFullVO : clientBills) {
			clientFullVO.getParentVO().setAttributeValue("${vmObject.billstatus}",
					BillStatusEnum.COMMIT.value());
			clientFullVO.getParentVO().setStatus(VOStatus.UPDATED);
		}
		// ���ݳ־û�
		AggRLContractalterHVO[] returnVos = new BillUpdate<AggRLContractalterHVO>().update(
				clientBills, originBills);
		return returnVos;
	}
}
