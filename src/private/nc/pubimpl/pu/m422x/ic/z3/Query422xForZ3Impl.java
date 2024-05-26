package nc.pubimpl.pu.m422x.ic.z3;

import nc.bs.pu.m422x.query.QueryForZ3BP;
import nc.pubitf.pu.m422x.ic.z3.IQuery422xForZ3;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pu.m422x.entity.StoreReqAppVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

/*    */public class Query422xForZ3Impl
/*    */implements IQuery422xForZ3
/*    */{
	/*    */public Query422xForZ3Impl() {
	}

	/*    */
	/*    */public StoreReqAppVO[] queryStoreReqApps(IQueryScheme queryScheme)
	/*    */throws BusinessException
	/*    */{
		/*    */try
		/*    */{
			/* 30 */return new QueryForZ3BP(queryScheme).queryStoreReqVOs();
			/*    */}
		/*    */catch (Exception e) {
			/* 33 */ExceptionUtils.marsh(e);
			/*    */}
		/* 35 */return null;
		/*    */}
	/*    */
}