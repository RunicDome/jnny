package nc.vo.aim.equipmaintain;

import nc.vo.pub.ISuperVO;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.aim.equipmaintain.Equipmaintain")
public class AggEquipmaintain extends AbstractBill {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public IBillMeta getMetaData() {
		IBillMeta billMeta = BillMetaFactory.getInstance().getBillMeta(
				AggEquipmaintainMeta.class);
		return billMeta;
	}

	@Override
	public Equipmaintain getParentVO() {
		return (Equipmaintain) this.getParent();
	}

	@Override
	public ISuperVO getParent() {
		// TODO Auto-generated method stub
		return (Equipmaintain) super.getParent();
	}

	@Override
	public EquipmaintainB[] getChildrenVO() {
		// TODO Auto-generated method stub
		return (EquipmaintainB[]) super.getChildrenVO();
	}
}