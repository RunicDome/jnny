package nc.itf.pm;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pm.rlcontractalter.AggRLContractalterHVO;
import nc.vo.pub.BusinessException;

public interface IPmRLContractalterMaintain {

	public void delete(AggRLContractalterHVO[] clientFullVOs,
			AggRLContractalterHVO[] originBills) throws BusinessException;

	public AggRLContractalterHVO[] insert(AggRLContractalterHVO[] clientFullVOs,
			AggRLContractalterHVO[] originBills) throws BusinessException;

	public AggRLContractalterHVO[] update(AggRLContractalterHVO[] clientFullVOs,
			AggRLContractalterHVO[] originBills) throws BusinessException;

	public AggRLContractalterHVO[] query(IQueryScheme queryScheme)
			throws BusinessException;

	public AggRLContractalterHVO[] save(AggRLContractalterHVO[] clientFullVOs,
			AggRLContractalterHVO[] originBills) throws BusinessException;

	public AggRLContractalterHVO[] unsave(AggRLContractalterHVO[] clientFullVOs,
			AggRLContractalterHVO[] originBills) throws BusinessException;

	public AggRLContractalterHVO[] approve(AggRLContractalterHVO[] clientFullVOs,
			AggRLContractalterHVO[] originBills) throws BusinessException;

	public AggRLContractalterHVO[] unapprove(AggRLContractalterHVO[] clientFullVOs,
			AggRLContractalterHVO[] originBills) throws BusinessException;

	public String[] queryPks(IQueryScheme queryScheme) throws BusinessException;
	public Object[] queryObjectByPks(String[] pks) throws BusinessException;
}
