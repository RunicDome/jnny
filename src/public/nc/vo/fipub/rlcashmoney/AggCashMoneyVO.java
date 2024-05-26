package nc.vo.fipub.rlcashmoney;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.fipub.rlcashmoney.CashMoneyVO")

public class AggCashMoneyVO extends AbstractBill {
	
	  @Override
	  public IBillMeta getMetaData() {
	  	IBillMeta billMeta =BillMetaFactory.getInstance().getBillMeta(AggCashMoneyVOMeta.class);
	  	return billMeta;
	  }
	    
	  @Override
	  public CashMoneyVO getParentVO(){
	  	return (CashMoneyVO)this.getParent();
	  }
	  
}