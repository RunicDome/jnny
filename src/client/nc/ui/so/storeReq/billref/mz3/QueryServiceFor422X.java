package nc.ui.so.storeReq.billref.mz3;
/*    */ 
/*    */ import nc.bs.framework.common.NCLocator;
import nc.bs.pu.m422x.query.QueryForZ3BP;
import nc.itf.so.salequotation.ISalequotationQry;
import nc.pubitf.pu.m422x.ic.z3.IQuery422xForZ3;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pu.m422x.entity.StoreReqAppVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.so.salequotation.entity.AggSalequotationHVO;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class QueryServiceFor422X implements nc.ui.pubapp.uif2app.query2.model.IRefQueryService
/*    */ {
/*    */   private ISalequotationQry bsQryService;
/*    */   
/*    */   public QueryServiceFor422X() {}
/*    */   
/*    */   public Object[] queryByQueryScheme(IQueryScheme queryScheme) throws Exception
/*    */   {
/* 17 */      IQuery422xForZ3 service = (IQuery422xForZ3)NCLocator.getInstance().lookup(IQuery422xForZ3.class);
/*    */     
/* 30 */     StoreReqAppVO[] page = null;
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
/*    */   public AggSalequotationHVO[] queryByWhereSql(String whereSql)
/*    */     throws Exception
/*    */   {
/* 31 */     AggSalequotationHVO[] rets = null;
/* 32 */     return rets;
/*    */   }
/*    */ }