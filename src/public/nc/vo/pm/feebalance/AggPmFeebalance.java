package nc.vo.pm.feebalance;
//多遍码合同AggVO
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;

/**
 * <b>此处简要描述此类功能 </b>
 * <p>
 *   此处添加类的描述信息
 * </p>
 *  创建日期:2021-7-1
 * @author developer
 * @version NCPrj ??
 */
 @SuppressWarnings("serial") @nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.pm.feebalance.PmFeebalanceHVO") 
public class AggPmFeebalance extends AbstractBill{

	@Override
	public IBillMeta getMetaData() {
		// TODO Auto-generated method stub
		IBillMeta billMeta = (IBillMeta) BillMetaFactory.getInstance().getBillMeta(PmFeebalanceHVOMeta.class);
		return billMeta;
	}

	@Override
	public PmFeebalanceHVO getParent() {
		// TODO Auto-generated method stub
		return (PmFeebalanceHVO) super.getParent();
	}
	
	@Override
	public PmFeebalanceBVO[] getChildrenVO() {
		// TODO Auto-generated method stub
		return (PmFeebalanceBVO[]) super.getChildrenVO();
	}
}