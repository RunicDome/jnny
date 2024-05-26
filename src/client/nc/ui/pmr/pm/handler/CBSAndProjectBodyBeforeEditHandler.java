/*    */ package nc.ui.pmr.pm.handler;
/*    */ 
/*    */ import nc.ui.bd.cbs.CbsNodeOrgAndProjectRefModel;
/*    */ import nc.ui.pm.util.BillCardPanelUtil;
/*    */ import nc.ui.pub.beans.UIRefPane;
/*    */ import nc.ui.pub.bill.BillCardPanel;
/*    */ import nc.ui.pub.bill.BillItem;
/*    */ import nc.ui.pubapp.uif2app.event.IAppEventHandler;
/*    */ import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent;
/*    */ import nc.vo.pm.util.cache.OrgUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CBSAndProjectBodyBeforeEditHandler
/*    */   implements IAppEventHandler<CardBodyBeforeEditEvent>
/*    */ {
/* 31 */   private String CBSNodeFiled = null;
/*    */   
/* 33 */   private String projectIDFeild = null;
/*    */   
/* 35 */   private String projectTypeFeild = null;
/*    */   
/*    */   public CBSAndProjectBodyBeforeEditHandler() {}
/*    */   
/* 39 */   @SuppressWarnings("restriction")
public void handleAppEvent(CardBodyBeforeEditEvent e) { e.setReturnValue(Boolean.valueOf(true));
/*    */     
/* 41 */     if (!getCBSNodeFiled().equals(e.getKey())) {
/* 42 */       return;
/*    */     }
/* 44 */     BillCardPanel cardPanel = e.getBillCardPanel();
/*    */     
/* 46 */     String pk_project = (String)BillCardPanelUtil.getBodyValue(cardPanel, getProjectIDFeild(), e.getRow());
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 51 */     String pk_org = BillCardPanelUtil.getHeadStringValue(cardPanel, "pk_org");
/*    */     
/* 53 */     String pk_FinanceOrg = OrgUtil.getDefaultFinanceOrgByProjectOrg(pk_org);
/*    */     
/*    */ 
/*    */ 
/* 57 */     UIRefPane refPanle = (UIRefPane)cardPanel.getBodyItem(e.getKey()).getComponent();
/*    */     
/* 59 */     CbsNodeOrgAndProjectRefModel refModel = (CbsNodeOrgAndProjectRefModel)refPanle.getRefModel();
/*    */     
/* 61 */     refModel.setPk_project(pk_project);
/* 62 */     refModel.setPk_org(pk_FinanceOrg);
/*    */     
/* 64 */     refPanle.setNotLeafSelectedEnabled(false);
/*    */   }
/*    */   
/*    */   public String getCBSNodeFiled() {
/* 68 */     return this.CBSNodeFiled;
/*    */   }
/*    */   
/*    */   public void setCBSNodeFiled(String cBSNodeFiled) {
/* 72 */     this.CBSNodeFiled = cBSNodeFiled;
/*    */   }
/*    */   
/*    */   public String getProjectIDFeild() {
/* 76 */     return this.projectIDFeild;
/*    */   }
/*    */   
/*    */   public void setProjectIDFeild(String projectIDFeild) {
/* 80 */     this.projectIDFeild = projectIDFeild;
/*    */   }
/*    */   
/*    */   public String getProjectTypeFeild() {
/* 84 */     return this.projectTypeFeild;
/*    */   }
/*    */   
/*    */   public void setProjectTypeFeild(String projectTypeFeild) {
/* 88 */     this.projectTypeFeild = projectTypeFeild;
/*    */   }
/*    */ }
