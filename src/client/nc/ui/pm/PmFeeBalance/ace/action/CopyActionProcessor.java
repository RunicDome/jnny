package nc.ui.pm.PmFeeBalance.ace.action;

import nc.ui.pubapp.uif2app.actions.intf.ICopyActionProcessor;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pm.feebalance.PmFeebalanceBVO;
import nc.vo.pm.feebalance.PmFeebalanceHVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pubapp.AppContext;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.ArrayUtils;

public class CopyActionProcessor<E extends AggPmFeebalance> implements
		ICopyActionProcessor<E> {
	public CopyActionProcessor() {
	}

	public void processVOAfterCopy(AggPmFeebalance billVO, LoginContext context) {
		setHeadValue(billVO, context);
		setBodyValue(billVO);
	}

	private void setBodyValue(AggPmFeebalance billVO) {
		if (null == billVO) {
			return;
		}
		PmFeebalanceBVO[] itemVOs = billVO.getChildrenVO();
		if (ArrayUtils.isEmpty(itemVOs)) {
			return;
		}
		for (int i = 0; i < itemVOs.length; i++) {
			itemVOs[i].setPm_feebalance_b(null);
		}
	}

	private void setHeadValue(AggPmFeebalance billVO, LoginContext context) {
		if (null == billVO) {
			return;
		}
		PmFeebalanceHVO headerVO = billVO.getParent();

		if (null == headerVO) {
			return;
		}

		headerVO.setPm_feebalance(null);
		UFDate busidate = AppContext.getInstance().getBusiDate();
		headerVO.setDbilldate(busidate);
		headerVO.setTs(null);
		headerVO.setBillmaker(context.getPk_loginUser());
		headerVO.setFstatusflag(-1);
		headerVO.setAuditor(null);
		headerVO.setAudittime(null);
		headerVO.setBill_code(null);
	}
}