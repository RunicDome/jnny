package nc.itf.aim;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.aim.equipmaintain.AggEquipmaintain;
import nc.vo.pub.BusinessException;

public interface IEquipmaintainMaintain {

	public void delete(AggEquipmaintain[] clientFullVOs,
			AggEquipmaintain[] originBills) throws BusinessException;

	public AggEquipmaintain[] insert(AggEquipmaintain[] clientFullVOs,
			AggEquipmaintain[] originBills) throws BusinessException;

	public AggEquipmaintain[] update(AggEquipmaintain[] clientFullVOs,
			AggEquipmaintain[] originBills) throws BusinessException;

	public AggEquipmaintain[] query(IQueryScheme queryScheme)
			throws BusinessException;

	public AggEquipmaintain[] save(AggEquipmaintain[] clientFullVOs,
			AggEquipmaintain[] originBills) throws BusinessException;

	public AggEquipmaintain[] unsave(AggEquipmaintain[] clientFullVOs,
			AggEquipmaintain[] originBills) throws BusinessException;

	public AggEquipmaintain[] approve(AggEquipmaintain[] clientFullVOs,
			AggEquipmaintain[] originBills) throws BusinessException;

	public AggEquipmaintain[] unapprove(AggEquipmaintain[] clientFullVOs,
			AggEquipmaintain[] originBills) throws BusinessException;
}
