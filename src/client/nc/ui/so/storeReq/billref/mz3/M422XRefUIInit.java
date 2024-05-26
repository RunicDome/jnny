//package nc.ui.so.storeReq.billref.mz3;
//
///*    */ import nc.ui.pub.bill.BillListPanel;
///*    */ import nc.ui.pubapp.ClientContext;
///*    */ public class M422XRefUIInit implements nc.ui.pubapp.billref.src.IRefPanelInit
///*    */ {
///*    */   public M422XRefUIInit() {}
///*    */   
///*    */   public void refMasterPanelInit(BillListPanel masterPanel)
///*    */   {
///* 13 */     SalequoScaleProcessor.getInstance().setListPrecision(ClientContext.getInstance().getPk_group(), masterPanel);
///*    */   }
///*    */   
///*    */ 
///*    */   public void refSinglePanelInit(BillListPanel singlePanel)
///*    */   {
///* 19 */     String pk_group = ClientContext.getInstance().getPk_group();
///*    */     
///* 21 */     SalequoScaleProcessor.getInstance().setSingleTableScale(pk_group, singlePanel);
///*    */   }
///*    */ }