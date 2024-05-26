package nc.pubitf.pmr.pm;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pub.BusinessException;

public interface IPmFeebalanceCtMaintain {

	public String[] queryPks(IQueryScheme queryScheme) throws BusinessException;
	public Object[] queryObjectByPks(String[] pks) throws BusinessException;

	public void delete(AggPmFeebalance[] clientFullVOs,
			AggPmFeebalance[] originBills) throws BusinessException;

	public AggPmFeebalance[] insert(AggPmFeebalance[] clientFullVOs,
			AggPmFeebalance[] originBills) throws BusinessException;

	public AggPmFeebalance[] update(AggPmFeebalance[] clientFullVOs,
			AggPmFeebalance[] originBills) throws BusinessException;

	public AggPmFeebalance[] query(IQueryScheme queryScheme)
			throws BusinessException;

	public AggPmFeebalance[] save(AggPmFeebalance[] clientFullVOs,
			AggPmFeebalance[] originBills) throws BusinessException;

	public AggPmFeebalance[] unsave(AggPmFeebalance[] clientFullVOs,
			AggPmFeebalance[] originBills) throws BusinessException;

	public AggPmFeebalance[] approve(AggPmFeebalance[] clientFullVOs,
			AggPmFeebalance[] originBills) throws BusinessException;

	public AggPmFeebalance[] unapprove(AggPmFeebalance[] clientFullVOs,
			AggPmFeebalance[] originBills) throws BusinessException;
}
