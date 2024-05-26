package nc.itf.aim;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.aim.worderplan.AggWorderplan;
import nc.vo.pub.BusinessException;

public interface IWorderPlanMaintain {

	public void delete(AggWorderplan[] clientFullVOs,
			AggWorderplan[] originBills) throws BusinessException;

	public AggWorderplan[] insert(AggWorderplan[] clientFullVOs,
			AggWorderplan[] originBills) throws BusinessException;

	public AggWorderplan[] update(AggWorderplan[] clientFullVOs,
			AggWorderplan[] originBills) throws BusinessException;

	public AggWorderplan[] query(IQueryScheme queryScheme)
			throws BusinessException;

	public AggWorderplan[] save(AggWorderplan[] clientFullVOs,
			AggWorderplan[] originBills) throws BusinessException;

	public AggWorderplan[] unsave(AggWorderplan[] clientFullVOs,
			AggWorderplan[] originBills) throws BusinessException;

	public AggWorderplan[] approve(AggWorderplan[] clientFullVOs,
			AggWorderplan[] originBills) throws BusinessException;

	public AggWorderplan[] unapprove(AggWorderplan[] clientFullVOs,
			AggWorderplan[] originBills) throws BusinessException;

	public String[] queryPks(IQueryScheme queryScheme) throws BusinessException;

	public Object[] queryObjectByPks(String[] pks) throws BusinessException;
}
