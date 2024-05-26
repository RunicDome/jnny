package nc.vo.pcm.pcmyearplan;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.pcm.pcmyearplan.PcmYearplan")
public class AggPcmYearplan extends AbstractBill {

	private static final long serialVersionUID = 1L;

	@Override
	public IBillMeta getMetaData() {
		IBillMeta billMeta = BillMetaFactory.getInstance().getBillMeta(
				AggPcmYearplanMeta.class);
		return billMeta;
	}

	@Override
	public PcmYearplan getParentVO() {
		return (PcmYearplan) this.getParent();
	}

}