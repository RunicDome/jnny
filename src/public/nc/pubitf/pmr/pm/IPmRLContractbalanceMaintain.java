package nc.pubitf.pmr.pm;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pm.rlcontractbalance.AggRLContractbalanceHVO;
import nc.vo.pub.BusinessException;

public interface IPmRLContractbalanceMaintain {

	public String[] queryPks(IQueryScheme queryScheme) throws BusinessException;
	public Object[] queryObjectByPks(String[] pks) throws BusinessException;
	public void delete(AggRLContractbalanceHVO[] clientFullVOs,
			AggRLContractbalanceHVO[] originBills) throws BusinessException;

	public AggRLContractbalanceHVO[] insert(AggRLContractbalanceHVO[] clientFullVOs,
			AggRLContractbalanceHVO[] originBills) throws BusinessException;

	public AggRLContractbalanceHVO[] update(AggRLContractbalanceHVO[] clientFullVOs,
			AggRLContractbalanceHVO[] originBills) throws BusinessException;

	public AggRLContractbalanceHVO[] query(IQueryScheme queryScheme)
			throws BusinessException;

	public AggRLContractbalanceHVO[] save(AggRLContractbalanceHVO[] clientFullVOs,
			AggRLContractbalanceHVO[] originBills) throws BusinessException;

	public AggRLContractbalanceHVO[] unsave(AggRLContractbalanceHVO[] clientFullVOs,
			AggRLContractbalanceHVO[] originBills) throws BusinessException;

	public AggRLContractbalanceHVO[] approve(AggRLContractbalanceHVO[] clientFullVOs,
			AggRLContractbalanceHVO[] originBills) throws BusinessException;

	public AggRLContractbalanceHVO[] unapprove(AggRLContractbalanceHVO[] clientFullVOs,
			AggRLContractbalanceHVO[] originBills) throws BusinessException;
}
