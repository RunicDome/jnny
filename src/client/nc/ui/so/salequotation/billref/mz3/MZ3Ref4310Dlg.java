/*    */ package nc.ui.so.salequotation.billref.mz3;
/*    */ 
/*    */ import java.awt.Container;
/*    */ import nc.ui.pub.pf.BillSourceVar;
/*    */ import nc.ui.pubapp.billref.src.view.SourceRefDlg;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MZ3Ref4310Dlg
/*    */   extends SourceRefDlg
/*    */ {
/*    */   private static final long serialVersionUID = -5941749394140338864L;
/*    */   
/*    */   public MZ3Ref4310Dlg(Container parent, BillSourceVar bsVar)
/*    */   {
/* 16 */     super(parent, bsVar, true);
/*    */   }
/*    */   
/*    */   public String getRefBillInfoBeanPath()
/*    */   {
/* 21 */     return "nc/ui/so/salequotation/billref/mz3/MZ3Ref4310Info.xml";
/*    */   }
/*    */ }
