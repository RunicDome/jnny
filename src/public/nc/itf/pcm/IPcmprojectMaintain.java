package nc.itf.pcm;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pcm.pcmproject.AggPcmProject;
import nc.vo.pub.BusinessException;

public interface IPcmprojectMaintain {

	public void delete(AggPcmProject[] clientFullVOs,
			AggPcmProject[] originBills) throws BusinessException;

	public AggPcmProject[] insert(AggPcmProject[] clientFullVOs,
			AggPcmProject[] originBills) throws BusinessException;

	public AggPcmProject[] update(AggPcmProject[] clientFullVOs,
			AggPcmProject[] originBills) throws BusinessException;

	public AggPcmProject[] query(IQueryScheme queryScheme)
			throws BusinessException;

	public AggPcmProject[] save(AggPcmProject[] clientFullVOs,
			AggPcmProject[] originBills) throws BusinessException;

	public AggPcmProject[] unsave(AggPcmProject[] clientFullVOs,
			AggPcmProject[] originBills) throws BusinessException;

	public AggPcmProject[] approve(AggPcmProject[] clientFullVOs,
			AggPcmProject[] originBills) throws BusinessException;

	public AggPcmProject[] unapprove(AggPcmProject[] clientFullVOs,
			AggPcmProject[] originBills) throws BusinessException;
}
