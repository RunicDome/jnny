package nc.impl.aim;

import nc.impl.pub.ace.AceEquipmaintainPubServiceImpl;
import nc.itf.aim.IEquipmaintainMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.aim.equipmaintain.AggEquipmaintain;
import nc.vo.pub.BusinessException;

public class EquipmaintainMaintainImpl extends AceEquipmaintainPubServiceImpl
		implements IEquipmaintainMaintain {

	@Override
	public void delete(AggEquipmaintain[] clientFullVOs,
			AggEquipmaintain[] originBills) throws BusinessException {
		super.pubdeleteBills(clientFullVOs, originBills);
	}

	@Override
	public AggEquipmaintain[] insert(AggEquipmaintain[] clientFullVOs,
			AggEquipmaintain[] originBills) throws BusinessException {
		return super.pubinsertBills(clientFullVOs, originBills);
	}

	@Override
	public AggEquipmaintain[] update(AggEquipmaintain[] clientFullVOs,
			AggEquipmaintain[] originBills) throws BusinessException {
		return super.pubupdateBills(clientFullVOs, originBills);
	}

	@Override
	public AggEquipmaintain[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

	@Override
	public AggEquipmaintain[] save(AggEquipmaintain[] clientFullVOs,
			AggEquipmaintain[] originBills) throws BusinessException {
		return super.pubsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggEquipmaintain[] unsave(AggEquipmaintain[] clientFullVOs,
			AggEquipmaintain[] originBills) throws BusinessException {
		return super.pubunsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggEquipmaintain[] approve(AggEquipmaintain[] clientFullVOs,
			AggEquipmaintain[] originBills) throws BusinessException {
		return super.pubapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggEquipmaintain[] unapprove(AggEquipmaintain[] clientFullVOs,
			AggEquipmaintain[] originBills) throws BusinessException {
		return super.pubunapprovebills(clientFullVOs, originBills);
	}

}
