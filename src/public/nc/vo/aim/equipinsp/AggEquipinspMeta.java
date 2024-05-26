package nc.vo.aim.equipinsp;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class AggEquipinspMeta extends AbstractBillMeta {

	public AggEquipinspMeta() {
		this.init();
	}

	private void init() {
		this.setParent(nc.vo.aim.equipinsp.Equipinsp.class);
		this.addChildren(nc.vo.aim.equipinsp.Equipinspb.class);
	}
}