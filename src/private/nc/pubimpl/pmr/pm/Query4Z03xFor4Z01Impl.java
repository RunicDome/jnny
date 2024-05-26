package nc.pubimpl.pmr.pm;

import nc.bs.pm.pmfeebalancect.ace.bp.QueryFor4Z01To4Z03BP;
import nc.pubitf.pmr.pm.IQuery4Z03For4Z01;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

/*    */public class Query4Z03xFor4Z01Impl
/*    */implements IQuery4Z03For4Z01
/*    */{
	/*    */public Query4Z03xFor4Z01Impl() {
	}

	/*    */
	/*    */public AggPmFeebalance[] queryStoreReqApps(IQueryScheme queryScheme)
	/*    */throws BusinessException
	/*    */{
		/*    */try
		/*    */{
			return new QueryFor4Z01To4Z03BP(queryScheme).queryStoreReqVOs();
			/*    */}
		/*    */catch (Exception e) {
			/* 33 */ExceptionUtils.marsh(e);
			/*    */}
		/* 35 */return null;
		/*    */}
	/*    */
}