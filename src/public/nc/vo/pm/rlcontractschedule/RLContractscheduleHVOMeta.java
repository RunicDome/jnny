package nc.vo.pm.rlcontractschedule;

/*    */ 
/*    */ import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RLContractscheduleHVOMeta
/*    */   extends AbstractBillMeta
/*    */ {
/*    */   public RLContractscheduleHVOMeta()
/*    */   {
/* 14 */     init();
/*    */   }
/*    */   
/*    */   private void init() {
/* 18 */     setParent(RLContractschedule.class);
/* 19 */     addChildren(RLContractscheduleBVO.class);
/*    */   }
/*    */ }
