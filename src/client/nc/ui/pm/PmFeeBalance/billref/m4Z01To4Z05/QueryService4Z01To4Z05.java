package nc.ui.pm.PmFeeBalance.billref.m4Z01To4Z05;
 
 import nc.bs.framework.common.NCLocator;
import nc.pubitf.pmr.pm.IQuery4Z05For4Z01;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

 public class QueryService4Z01To4Z05 implements nc.ui.pubapp.uif2app.query2.model.IRefQueryService
 {
   public QueryService4Z01To4Z05() {}

   public Object[] queryByQueryScheme(IQueryScheme queryScheme) throws Exception
   {     IQuery4Z05For4Z01 service = (IQuery4Z05For4Z01)NCLocator.getInstance().lookup(IQuery4Z05For4Z01.class);
          AggPmFeebalance[] page = null;
     try {
				page = service.queryStoreReqApps(queryScheme);
     }
     catch (BusinessException ex)
     {
       ExceptionUtils.wrappException(ex);
     }
     return page;
   }
   
   public AggPmFeebalance[] queryByWhereSql(String whereSql)
     throws Exception
   {
	   AggPmFeebalance[] rets = null;
     return rets;
   }
 }