package nc.vo.pm.rlcontractbalance;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;

/**
 * <b>此处简要描述此类功能 </b>
 * <p>
 *   此处添加类的描述信息
 * </p>
 *  创建日期:2021-8-26
 * @author developer
 * @version NCPrj ??
 */
 @SuppressWarnings("serial") @nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.pm.rlcontractbalance.RLContractbalanceHVO") 
public class AggRLContractbalanceHVO extends AbstractBill{

	 
	 @Override
		public IBillMeta getMetaData() {
			// TODO Auto-generated method stub
			IBillMeta billMeta = (IBillMeta) BillMetaFactory.getInstance().getBillMeta(RLContractBalanceHVOMeta.class);
			return billMeta;
		}

		@Override
		public RLContractbalanceHVO getParent() {
			// TODO Auto-generated method stub
			return (RLContractbalanceHVO) super.getParent();
		}
		
		@Override
		public RLContractbalanceBVO[] getChildrenVO() {
			// TODO Auto-generated method stub
			return (RLContractbalanceBVO[]) super.getChildrenVO();
		}
}