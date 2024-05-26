package nc.itf.aim;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.aim.equipinsp.AggEquipinsp;
import nc.vo.pub.BusinessException;

public interface IEquipinspMaintain {

	public void delete(AggEquipinsp[] clientFullVOs, AggEquipinsp[] originBills)
			throws BusinessException;

	public AggEquipinsp[] insert(AggEquipinsp[] clientFullVOs,
			AggEquipinsp[] originBills) throws BusinessException;

	public AggEquipinsp[] update(AggEquipinsp[] clientFullVOs,
			AggEquipinsp[] originBills) throws BusinessException;

	public AggEquipinsp[] query(IQueryScheme queryScheme)
			throws BusinessException;

	public AggEquipinsp[] save(AggEquipinsp[] clientFullVOs,
			AggEquipinsp[] originBills) throws BusinessException;

	public AggEquipinsp[] unsave(AggEquipinsp[] clientFullVOs,
			AggEquipinsp[] originBills) throws BusinessException;

	public AggEquipinsp[] approve(AggEquipinsp[] clientFullVOs,
			AggEquipinsp[] originBills) throws BusinessException;

	public AggEquipinsp[] unapprove(AggEquipinsp[] clientFullVOs,
			AggEquipinsp[] originBills) throws BusinessException;

	public String[] queryPks(IQueryScheme queryScheme) throws BusinessException;

	public Object[] queryObjectByPks(String[] pks) throws BusinessException;
}
