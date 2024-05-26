package nc.vo.pm.rlcontractalter;

import nc.vo.pm.rlcontractbalance.RLContractBalanceHVOMeta;
import nc.vo.pm.rlcontractbalance.RLContractbalanceBVO;
import nc.vo.pm.rlcontractbalance.RLContractbalanceHVO;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;
import nc.vo.trade.pub.HYBillVO;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;


/**
 * <b>此处简要描述此类功能 </b>
 * <p>
 *   此处添加类的描述信息
 * </p>
 *  创建日期:2021-9-13
 * @author developer
 * @version NCPrj ??
 */
 @SuppressWarnings("serial") @nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.pm.rlcontractalter.RLContractalterHVO") 
public class AggRLContractalterHVO extends AbstractBill{
	 
	 @Override
		public IBillMeta getMetaData() {
			// TODO Auto-generated method stub
			IBillMeta billMeta = (IBillMeta) BillMetaFactory.getInstance().getBillMeta(RLContractalterHVOMeta.class);
			return billMeta;
		}

		@Override
		public RLContractalterHVO getParent() {
			// TODO Auto-generated method stub
			return (RLContractalterHVO) super.getParent();
		}
		
		@Override
		public RLContractalterBVO[] getChildrenVO() {
			// TODO Auto-generated method stub
			return (RLContractalterBVO[]) super.getChildrenVO();
		}
}