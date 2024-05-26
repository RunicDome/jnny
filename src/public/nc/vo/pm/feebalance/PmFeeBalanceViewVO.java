package nc.vo.pm.feebalance;

import nc.vo.pubapp.pattern.model.entity.view.AbstractDataView;
import nc.vo.pubapp.pattern.model.meta.entity.view.DataViewMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.view.IDataViewMeta;

public class PmFeeBalanceViewVO extends AbstractDataView {
	private static final long serialVersionUID = 5508564754340511569L;

	public PmFeeBalanceViewVO() {
	}

	public AggPmFeebalance changeTOBill() {
		AggPmFeebalance aggVO = new AggPmFeebalance();
		aggVO.setParent(getVO(PmFeebalanceHVO.class));
		PmFeebalanceBVO bvo = (PmFeebalanceBVO) getVO(PmFeebalanceBVO.class);

		aggVO.setChildren(PmFeebalanceBVO.class,
				new PmFeebalanceBVO[] { bvo });

		return aggVO;
	}

	public IDataViewMeta getMetaData() {
		return DataViewMetaFactory.getInstance().getBillViewMeta(
				AggPmFeebalance.class);
	}
}
