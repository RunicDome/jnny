/*    */ package nc.ui.so.salequotation.billref.mz3;
/*    */ 
/*    */ import nc.bs.framework.common.NCLocator;
/*    */ import nc.itf.so.salequotation.ISalequotationQry;
import nc.pubitf.ct.saledaily.so.IZ3QueryForOrder;
/*    */ import nc.ui.querytemplate.querytree.IQueryScheme;
/*    */ import nc.vo.pub.BusinessException;
/*    */ import nc.vo.so.salequotation.entity.AggSalequotationHVO;
/*    */ 
/*    */ public class QueryServiceForZ3 implements nc.ui.pubapp.uif2app.query2.model.IRefQueryService
/*    */ {
/*    */   private ISalequotationQry bsQryService;
/*    */   
/*    */   public QueryServiceForZ3() {}
/*    */   
/*    */   public Object[] queryByQueryScheme(IQueryScheme queryScheme) throws Exception
/*    */   {
/* 17 */     AggSalequotationHVO[] rets = null;
/*    */     try {
			   IZ3QueryForOrder service= NCLocator.getInstance().lookup(IZ3QueryForOrder.class);
/* 19 */       rets = getBSQryService().queryByQuerySchemeForZ3(queryScheme);
/*    */     }
/*    */     catch (BusinessException e)
/*    */     {
/* 23 */       nc.vo.pubapp.pattern.exception.ExceptionUtils.wrappException(e);
/*    */     }
/* 25 */     return rets;
/*    */   }
/*    */   
/*    */   public AggSalequotationHVO[] queryByWhereSql(String whereSql)
/*    */     throws Exception
/*    */   {
/* 31 */     AggSalequotationHVO[] rets = null;
/* 32 */     return rets;
/*    */   }
/*    */   
/*    */   private ISalequotationQry getBSQryService() {
/* 36 */     if (this.bsQryService == null) {
/* 37 */       this.bsQryService = ((ISalequotationQry)NCLocator.getInstance().lookup(ISalequotationQry.class));
/*    */     }
/*    */     
/* 40 */     return this.bsQryService;
/*    */   }
/*    */ }