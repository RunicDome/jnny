package nc.bs.pm.pmfeebalancect.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;

/**
 * 标准单据送审的BP
 */
public class AcePmFeebalanceCtSendApproveBP {
	/**
	 * 送审动作
	 * 
	 * @param vos
	 *            单据VO数组
	 * @param script
	 *            单据动作脚本对象
	 * @return 送审后的单据VO数组
	 */

	public AggPmFeebalance[] sendApprove(AggPmFeebalance[] clientBills,
			AggPmFeebalance[] originBills) {
		for (AggPmFeebalance clientFullVO : clientBills) {
			clientFullVO.getParentVO().setAttributeValue("${vmObject.billstatus}",
					BillStatusEnum.COMMIT.value());
			clientFullVO.getParentVO().setStatus(VOStatus.UPDATED);
		}
		// 数据持久化
		AggPmFeebalance[] returnVos = new BillUpdate<AggPmFeebalance>().update(
				clientBills, originBills);
		return returnVos;
	}
}
