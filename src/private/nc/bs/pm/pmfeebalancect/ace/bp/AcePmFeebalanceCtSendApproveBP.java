package nc.bs.pm.pmfeebalancect.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;

/**
 * ��׼���������BP
 */
public class AcePmFeebalanceCtSendApproveBP {
	/**
	 * ������
	 * 
	 * @param vos
	 *            ����VO����
	 * @param script
	 *            ���ݶ����ű�����
	 * @return �����ĵ���VO����
	 */

	public AggPmFeebalance[] sendApprove(AggPmFeebalance[] clientBills,
			AggPmFeebalance[] originBills) {
		for (AggPmFeebalance clientFullVO : clientBills) {
			clientFullVO.getParentVO().setAttributeValue("${vmObject.billstatus}",
					BillStatusEnum.COMMIT.value());
			clientFullVO.getParentVO().setStatus(VOStatus.UPDATED);
		}
		// ���ݳ־û�
		AggPmFeebalance[] returnVos = new BillUpdate<AggPmFeebalance>().update(
				clientBills, originBills);
		return returnVos;
	}
}
