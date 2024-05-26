package nc.vo.pm.feebalance;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class PmFeebalanceHVOMeta extends AbstractBillMeta {
	public PmFeebalanceHVOMeta() {
		init();
	}

	private void init() {
		setParent(PmFeebalanceHVO.class);
		addChildren(PmFeebalanceBVO.class);
	}
}