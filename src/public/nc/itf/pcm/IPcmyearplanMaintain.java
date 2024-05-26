package nc.itf.pcm;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pcm.pcmyearplan.AggPcmYearplan;
import nc.vo.pub.BusinessException;

public interface IPcmyearplanMaintain {

	public void delete(AggPcmYearplan[] clientFullVOs,
			AggPcmYearplan[] originBills) throws BusinessException;

	public AggPcmYearplan[] insert(AggPcmYearplan[] clientFullVOs,
			AggPcmYearplan[] originBills) throws BusinessException;

	public AggPcmYearplan[] update(AggPcmYearplan[] clientFullVOs,
			AggPcmYearplan[] originBills) throws BusinessException;

	public AggPcmYearplan[] query(IQueryScheme queryScheme)
			throws BusinessException;

	public AggPcmYearplan[] save(AggPcmYearplan[] clientFullVOs,
			AggPcmYearplan[] originBills) throws BusinessException;

	public AggPcmYearplan[] unsave(AggPcmYearplan[] clientFullVOs,
			AggPcmYearplan[] originBills) throws BusinessException;

	public AggPcmYearplan[] approve(AggPcmYearplan[] clientFullVOs,
			AggPcmYearplan[] originBills) throws BusinessException;

	public AggPcmYearplan[] unapprove(AggPcmYearplan[] clientFullVOs,
			AggPcmYearplan[] originBills) throws BusinessException;
}
