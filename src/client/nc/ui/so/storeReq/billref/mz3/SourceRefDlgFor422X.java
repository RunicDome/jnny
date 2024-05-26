package nc.ui.so.storeReq.billref.mz3;

/*    */ import java.awt.Container;
/*    */ import nc.ui.pub.pf.BillSourceVar;
/*    */ import nc.ui.pubapp.billref.src.view.SourceRefDlg;

/*    */ public class SourceRefDlgFor422X
/*    */   extends SourceRefDlg
/*    */ {
/*    */   private static final long serialVersionUID = 5811618879014228342L;
/*    */   
/*    */   public SourceRefDlgFor422X(Container parent, BillSourceVar bsVar)
/*    */   {
/* 39 */     super(parent, bsVar, true);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getRefBillInfoBeanPath()
/*    */   {
/* 49 */     return "nc/ui/so/storeReq/billref/mz3/MZ3Ref422XInfo.xml";
/*    */   }
/*    */ }