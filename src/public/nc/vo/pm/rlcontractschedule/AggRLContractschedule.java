package nc.vo.pm.rlcontractschedule;

import nc.vo.pm.rlcontractschedule.RLContractscheduleHVOMeta;
import nc.vo.pm.rlcontractschedule.RLContractschedule;
import nc.vo.pm.rlcontractschedule.RLContractscheduleBVO;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;


/**
 * <b>此处简要描述此类功能 </b>
 * <p>
 *   此处添加类的描述信息
 * </p>
 *  创建日期:2021-8-26
 * @author developer
 * @version NCPrj ??
 */
 @SuppressWarnings("serial") @nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.pm.rlcontractschedule.RLContractschedule") 
public class AggRLContractschedule extends AbstractBill{
	 
	 @Override
		public IBillMeta getMetaData() {
			// TODO Auto-generated method stub
			IBillMeta billMeta = (IBillMeta) BillMetaFactory.getInstance().getBillMeta(RLContractscheduleHVOMeta.class);
			return billMeta;
		}

		@Override
		public RLContractschedule getParent() {
			// TODO Auto-generated method stub
			return (RLContractschedule) super.getParent();
		}
		
		@Override
		public RLContractscheduleBVO[] getChildrenVO() {
			// TODO Auto-generated method stub
			return (RLContractscheduleBVO[]) super.getChildrenVO();
		}

}