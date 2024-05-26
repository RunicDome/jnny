package nc.vo.pcm.pcmproject;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class AggPcmProjectMeta extends AbstractBillMeta {

	public AggPcmProjectMeta() {
		this.init();
	}

	private void init() {
		this.setParent(nc.vo.pcm.pcmproject.PcmProject.class);
	}
}