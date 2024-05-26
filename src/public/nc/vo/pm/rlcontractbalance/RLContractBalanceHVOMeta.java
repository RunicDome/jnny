package nc.vo.pm.rlcontractbalance;

/*    */ 
/*    */ import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RLContractBalanceHVOMeta
/*    */   extends AbstractBillMeta
/*    */ {
/*    */   public RLContractBalanceHVOMeta()
/*    */   {
/* 14 */     init();
/*    */   }
/*    */   
/*    */   private void init() {
/* 18 */     setParent(RLContractbalanceHVO.class);
/* 19 */     addChildren(RLContractbalanceBVO.class);
/*    */   }
/*    */ }
