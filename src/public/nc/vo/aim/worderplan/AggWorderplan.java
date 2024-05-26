package nc.vo.aim.worderplan;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.aim.worderplan.Worderplan")
public class AggWorderplan extends AbstractBill {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public IBillMeta getMetaData() {
		IBillMeta billMeta = BillMetaFactory.getInstance().getBillMeta(
				AggWorderplanMeta.class);
		return billMeta;
	}

	@Override
	public Worderplan getParentVO() {
		return (Worderplan) this.getParent();
	}

	@Override
	public Worderplan getParent() {
		return (Worderplan) super.getParent();
	}

	@Override
	public Worderplanb[] getChildrenVO() {
		// TODO Auto-generated method stub
		return (Worderplanb[]) super.getChildrenVO();
	}
}