package nc.vo.aim.equipinsp;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.aim.equipinsp.Equipinsp")
public class AggEquipinsp extends AbstractBill {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public IBillMeta getMetaData() {
		IBillMeta billMeta = BillMetaFactory.getInstance().getBillMeta(
				AggEquipinspMeta.class);
		return billMeta;
	}

	@Override
	public Equipinsp getParentVO() {
		return (Equipinsp) this.getParent();
	}

	@Override
	public Equipinsp getParent() {
		return (Equipinsp) super.getParent();
	}

	@Override
	public Equipinspb[] getChildrenVO() {
		// TODO Auto-generated method stub
		return (Equipinspb[]) super.getChildrenVO();
	}
}