package nc.ui.pm.pmrlcontractbalance.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.pubitf.pmr.pm.IPmFeebalanceCtMaintain;
import nc.pubitf.pmr.pm.IPmRLContractbalanceMaintain;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;

/**
 * 示例单据的操作代理
 * 
 * @author author
 * @version tempProject version
 */
public class AcePmRLContractbalanceMaintainProxy implements IQueryService,IPaginationQueryService {
	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		IPmRLContractbalanceMaintain query = NCLocator.getInstance().lookup(
				IPmRLContractbalanceMaintain.class);
		return query.query(queryScheme);
	}

	@Override
	public Object[] queryObjectByPks(String[] pks)
			throws BusinessException {
		// TODO Auto-generated method stub
		return NCLocator.getInstance().lookup(
				IPmRLContractbalanceMaintain.class).queryObjectByPks(pks);
	}

}