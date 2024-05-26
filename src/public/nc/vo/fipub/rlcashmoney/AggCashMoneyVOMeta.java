package nc.vo.fipub.rlcashmoney;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class AggCashMoneyVOMeta extends AbstractBillMeta{
	
	public AggCashMoneyVOMeta(){
		this.init();
	}
	
	private void init() {
		this.setParent(nc.vo.fipub.rlcashmoney.CashMoneyVO.class);
	}
}