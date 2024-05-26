package nc.vo.aim.equipmaintain;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class AggEquipmaintainMeta extends AbstractBillMeta {

	public AggEquipmaintainMeta() {
		this.init();
	}

	private void init() {
		this.setParent(nc.vo.aim.equipmaintain.Equipmaintain.class);
		this.addChildren(nc.vo.aim.equipmaintain.EquipmaintainB.class);
	}
}