package nc.ui.pm.PmFeeBalance.billref.m4Z01To4Z03;
/*    */ 
/*    */ import nc.bs.framework.common.NCLocator;
import nc.pubitf.pmr.pm.IQuery4Z03For4Z01;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class QueryService4Z01To4Z03 implements nc.ui.pubapp.uif2app.query2.model.IRefQueryService
/*    */ {
/*    */   
/*    */   public QueryService4Z01To4Z03() {}
/*    */   
/*    */   public Object[] queryByQueryScheme(IQueryScheme queryScheme) throws Exception
/*    */   {
/* 17 */      IQuery4Z03For4Z01 service = (IQuery4Z03For4Z01)NCLocator.getInstance().lookup(IQuery4Z03For4Z01.class);
/*    */     
/* 30 */     AggPmFeebalance[] page = null;
/*    */     try {
				page = service.queryStoreReqApps(queryScheme);
/*    */ 
/*    */     }
/*    */     catch (BusinessException ex)
/*    */     {
/*    */ 
/* 38 */       ExceptionUtils.wrappException(ex);
/*    */     }

/* 40 */     return page;
/*    */   }
/*    */   
/*    */   public AggPmFeebalance[] queryByWhereSql(String whereSql)
/*    */     throws Exception
/*    */   {
/* 31 */     AggPmFeebalance[] rets = null;
/* 32 */     return rets;
/*    */   }
/*    */ }