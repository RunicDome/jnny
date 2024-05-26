package nc.ui.pm.pmfeebalancect.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.pmr.pm.IPmFeebalanceCtMaintain;
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
@SuppressWarnings("restriction")
public class AcePmFeebalanceCtMaintainProxy implements IQueryService, IPaginationQueryService {
	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		IPmFeebalanceCtMaintain query = NCLocator.getInstance().lookup(
				IPmFeebalanceCtMaintain.class);
		return query.query(queryScheme);
	}

	@Override
	public Object[] queryObjectByPks(String[] pks)
			throws BusinessException {
		IPmFeebalanceCtMaintain query = NCLocator.getInstance().lookup(
				IPmFeebalanceCtMaintain.class);
		return query.queryObjectByPks(pks);
	}


}