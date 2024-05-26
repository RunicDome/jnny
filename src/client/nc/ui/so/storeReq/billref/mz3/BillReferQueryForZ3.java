package nc.ui.so.storeReq.billref.mz3;
/*    */ 
/*    */ import java.awt.Container;

import nc.bs.framework.common.NCLocator;
/*    */ import nc.itf.scmpub.reference.uap.setting.defaultdata.DefaultDataSettingAccessor;
/*    */ import nc.ui.pubapp.billref.src.DefaultBillReferQuery;
/*    */ import nc.ui.pubapp.uif2app.query2.QueryConditionDLGDelegator;
/*    */ import nc.ui.pubapp.uif2app.query2.totalvo.MarAssistantDealer;
/*    */ import nc.ui.scmpub.query.refregion.QDeptFilter;
/*    */ import nc.ui.scmpub.query.refregion.QPsndocFilter;
/*    */ import nc.ui.scmpub.query.refregion.QTransTypeFilter;
/*    */ import nc.ui.scmpub.query.refregion.RefCommonFilterListener;
/*    */ import nc.vo.pubapp.pattern.exception.ExceptionUtils;
/*    */ import nc.vo.querytemplate.TemplateInfo;
/*    */ import nc.vo.scmpub.res.billtype.SOBillType;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BillReferQueryForZ3
/*    */   extends DefaultBillReferQuery
/*    */ {
	/*    */   public BillReferQueryForZ3(Container c, TemplateInfo info)
	/*    */   {
	/* 27 */     super(c, info);
	/*    */   }
	/*    */   
	/*    */ 
	/*    */   protected void initQueryConditionDLG(QueryConditionDLGDelegator dlgDelegator)
	/*    */   {
	/* 33 */     setDefaultPk_org(dlgDelegator);
	/*    */     
	/* 35 */     initFilterRef(dlgDelegator);
	/*    */     
	/* 37 */     processBodyItem(dlgDelegator);
	/*    */     
	/*    */ 
	/* 40 */     dlgDelegator.registerNeedPermissionOrgFieldCodes(new String[] { "pk_org" });
	/*    */     
	/*    */ 
	/*    */ 
	/* 44 */     dlgDelegator.addQueryCondVODealer(new MarAssistantDealer());
	/*    */   }
	/*    */   
	/*    */   private void initFilterRef(QueryConditionDLGDelegator condDLGDelegator)
	/*    */   {
	/* 49 */     QTransTypeFilter trantype = new QTransTypeFilter(condDLGDelegator, "422X");
	/*    */     
	/*    */ 
	/* 52 */     trantype.filter();
	/*    */     
	/* 54 */     RefCommonFilterListener filterUtil = new RefCommonFilterListener(condDLGDelegator, "pk_org");
	/*    */     
	/* 56 */     filterUtil.addFilterMapsListeners();
	/*    */     
	/*    */ 
	/*    */ 
	/* 60 */     QDeptFilter deptFilter = QDeptFilter.createDeptFilterOfSO(condDLGDelegator, "pk_dept");
	/*    */     
	/*    */ 
	/* 63 */     deptFilter.setPk_orgCode("pk_org");
	/* 64 */     deptFilter.addEditorListener();
	/*    */     
	/*    */ 
	/* 67 */     QPsndocFilter psnFilter = QPsndocFilter.createQPsndocFilterOfSO(condDLGDelegator, "cemployeeid");
	/*    */     
	/*    */ 
	/* 70 */     psnFilter.setPk_orgCode("pk_org");
	/* 71 */     psnFilter.addEditorListener();
	/*    */   }
	/*    */   
	/*    */   private void processBodyItem(QueryConditionDLGDelegator condDLGDelegator)
	/*    */   {
//	/* 76 */     condDLGDelegator.addRedundancyInfo("pk_org", "salequotationdetail.pk_org");
	/*    */   }
	/*    */   
	/*    */ 
	/*    */ 
	/*    */ 
	/*    */   private void setDefaultPk_org(QueryConditionDLGDelegator condDLGDelegator)
	/*    */   {
	/* 84 */     String defaultOrg = null;
	/*    */     try {
	/* 86 */       defaultOrg = DefaultDataSettingAccessor.getDefaultOrgUnit();
	/*    */     }
	/*    */     catch (Exception ex) {
	/* 89 */       ExceptionUtils.wrappException(ex);
	/*    */     }
	/* 91 */     if ((defaultOrg != null) && (defaultOrg.trim().length() > 0)) {
	/* 92 */       condDLGDelegator.setDefaultValue("po_storereq.pk_org", defaultOrg);
	/*    */     }
	/*    */   }
/*    */ }