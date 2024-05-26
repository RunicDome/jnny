package nc.vo.pm.rlcontractbalance;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;

/**
 * <b>�˴���Ҫ�������๦�� </b>
 * <p>
 *   �˴�������������Ϣ
 * </p>
 *  ��������:2021-8-26
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