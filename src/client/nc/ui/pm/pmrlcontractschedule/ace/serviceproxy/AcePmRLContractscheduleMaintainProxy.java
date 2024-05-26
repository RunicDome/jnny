package nc.ui.pm.pmrlcontractschedule.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.pmr.pm.IPmRLContractscheduleMaintain;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.vo.pub.BusinessException;

/**
 * 示例单据的操作代理
 * 
 * @author author
 * @version tempProject version
 */
public class AcePmRLContractscheduleMaintainProxy implements IQueryService, IPaginationQueryService {
	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		IPmRLContractscheduleMaintain query = NCLocator.getInstance().lookup(
				IPmRLContractscheduleMaintain.class);
		return query.query(queryScheme);
	}

	@Override
	public Object[] queryObjectByPks(String[] paramArrayOfString)
			throws BusinessException {
		// TODO Auto-generated method stub
		return NCLocator.getInstance().lookup(
				IPmRLContractscheduleMaintain.class).queryObjectByPks(paramArrayOfString);
	}

}