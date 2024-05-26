package nc.vo.pcm.pcmyearplan;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class AggPcmYearplanMeta extends AbstractBillMeta {

	public AggPcmYearplanMeta() {
		this.init();
	}

	private void init() {
		this.setParent(nc.vo.pcm.pcmyearplan.PcmYearplan.class);
		this.addChildren(nc.vo.pcm.pcmyearplan.PcmYearplanB.class);
	}
}