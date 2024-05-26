package nc.ui.pm.pmrlcontractalter.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.itf.pm.IPmRLContractalterMaintain;
import nc.pubitf.pmr.pm.IPmFeebalanceCtMaintain;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.ui.uif2.components.pagination.IPaginationQueryService;

/**
 * 示例单据的操作代理
 * 
 * @author author
 * @version tempProject version
 */
public class AcePmRLContractalterMaintainProxy implements IQueryService, IPaginationQueryService {
	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		IPmRLContractalterMaintain query = NCLocator.getInstance().lookup(
				IPmRLContractalterMaintain.class);
		return query.query(queryScheme);
	}

	@Override
	public Object[] queryObjectByPks(String[] paramArrayOfString)
			throws BusinessException {
		// TODO Auto-generated method stub
		return NCLocator.getInstance().lookup(
				IPmRLContractalterMaintain.class).queryObjectByPks(paramArrayOfString);
	}

}