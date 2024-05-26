package nc.ui.pm.action;

import java.awt.event.ActionEvent;

import nc.bs.trade.business.HYPubBO;
import nc.oa.web.GetDBMHTVO;
import nc.vo.org.OrgVO;
import nc.vo.pcm.feebalance.FeeBalanceBillVO;
import nc.vo.pcm.feebalance.FeeBalanceHeadVO;

@SuppressWarnings("restriction")
public class FeeCommitAction extends CommitAction {
	private static final long serialVersionUID = 1L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		// TODO 自动生成的方法存根
		super.doAction(e);
		FeeBalanceBillVO fee = null;
		fee = (FeeBalanceBillVO) editor.getValue();
		if (fee.getPrimaryKey() == null) {
			fee = (FeeBalanceBillVO) getModel().getSelectedData();
		}
		FeeBalanceHeadVO headvo = (FeeBalanceHeadVO) fee.getParentVO();
		String ifblht = headvo.getDef4() + "";
		if ("1001A2100000000B68C1".equals(ifblht)) {
		} else {
			OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
					headvo.getPk_org());
			if (orgVO.getDef2() == null) {
				GetDBMHTVO.getgetDBMHTVOMsg(fee);
			}
		}
	}
}
