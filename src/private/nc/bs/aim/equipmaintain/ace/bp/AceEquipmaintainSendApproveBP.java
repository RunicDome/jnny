package nc.bs.aim.equipmaintain.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.aim.equipmaintain.AggEquipmaintain;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;

/**
 * 标准单据送审的BP
 */
public class AceEquipmaintainSendApproveBP {
	/**
	 * 送审动作
	 * 
	 * @param vos
	 *            单据VO数组
	 * @param script
	 *            单据动作脚本对象
	 * @return 送审后的单据VO数组
	 */

	public AggEquipmaintain[] sendApprove(AggEquipmaintain[] clientBills,
			AggEquipmaintain[] originBills) {
		for (AggEquipmaintain clientFullVO : clientBills) {
			clientFullVO.getParentVO().setAttributeValue(
					"${vmObject.billstatus}", BillStatusEnum.COMMIT.value());
			clientFullVO.getParentVO().setStatus(VOStatus.UPDATED);
		}
		// 数据持久化
		AggEquipmaintain[] returnVos = new BillUpdate<AggEquipmaintain>()
				.update(clientBills, originBills);
		return returnVos;
	}
}
