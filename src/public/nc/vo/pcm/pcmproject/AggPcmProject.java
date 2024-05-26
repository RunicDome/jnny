package nc.vo.pcm.pcmproject;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.pcm.pcmproject.PcmProject")
public class AggPcmProject extends AbstractBill {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public IBillMeta getMetaData() {
		IBillMeta billMeta = BillMetaFactory.getInstance().getBillMeta(
				AggPcmProjectMeta.class);
		return billMeta;
	}

	@Override
	public PcmProject getParentVO() {
		return (PcmProject) this.getParent();
	}

}