package nc.vo.aim.worderplan;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class AggWorderplanMeta extends AbstractBillMeta {

	public AggWorderplanMeta() {
		this.init();
	}

	private void init() {
		this.setParent(nc.vo.aim.worderplan.Worderplan.class);
		this.addChildren(nc.vo.aim.worderplan.Worderplanb.class);
	}
}