package nc.pubitf.pmr.pm;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pm.rlcontractschedule.AggRLContractschedule;
import nc.vo.pub.BusinessException;

public interface IPmRLContractscheduleMaintain {

	public String[] queryPks(IQueryScheme queryScheme) throws BusinessException;
	public Object[] queryObjectByPks(String[] pks) throws BusinessException;
	public void delete(AggRLContractschedule[] clientFullVOs,
			AggRLContractschedule[] originBills) throws BusinessException;

	public AggRLContractschedule[] insert(AggRLContractschedule[] clientFullVOs,
			AggRLContractschedule[] originBills) throws BusinessException;

	public AggRLContractschedule[] update(AggRLContractschedule[] clientFullVOs,
			AggRLContractschedule[] originBills) throws BusinessException;

	public AggRLContractschedule[] query(IQueryScheme queryScheme)
			throws BusinessException;

	public AggRLContractschedule[] save(AggRLContractschedule[] clientFullVOs,
			AggRLContractschedule[] originBills) throws BusinessException;

	public AggRLContractschedule[] unsave(AggRLContractschedule[] clientFullVOs,
			AggRLContractschedule[] originBills) throws BusinessException;

	public AggRLContractschedule[] approve(AggRLContractschedule[] clientFullVOs,
			AggRLContractschedule[] originBills) throws BusinessException;

	public AggRLContractschedule[] unapprove(AggRLContractschedule[] clientFullVOs,
			AggRLContractschedule[] originBills) throws BusinessException;
}
