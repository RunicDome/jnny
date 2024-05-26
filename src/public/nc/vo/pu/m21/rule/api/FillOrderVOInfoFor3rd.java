/*    */ package nc.vo.pu.m21.rule.api;
/*    */ 
/*    */ import nc.vo.pu.m21.entity.OrderVO;
/*    */ import nc.vo.pu.m21.rule.api.fill.Ctrantperid;
/*    */ import nc.vo.pu.m21.rule.api.fill.FillDefaultValueRule;
/*    */ import nc.vo.pu.m21.rule.api.fill.FillPaymentInfo;
/*    */ import nc.vo.pu.m21.rule.api.fill.FillPuMaterialUnitRule;
/*    */ import nc.vo.pu.m21.rule.api.fill.Vtrantypecode;
/*    */ import nc.vo.pu.m23.rule.api.fill.PUAssUnitAndChangeRate;
/*    */ import nc.vo.pub.BusinessException;
/*    */ import nc.vo.scmpub.check.billvalidate.BillVOsCheckRule;
/*    */ import nc.vo.scmpub.fill.BillVOsFillRule;
/*    */ import nc.vo.scmpub.fill.billfill.OrgAndGroupBillValueFill;
/*    */ import nc.vo.scmpub.fill.billfill.RowNoBillFill;
/*    */ import nc.vo.scmpub.res.billtype.POBillType;
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
/*    */ public class FillOrderVOInfoFor3rd
/*    */ {
/*    */   public OrderVO[] fillInfo(OrderVO[] orders) throws BusinessException {
/* 34 */     BillVOsCheckRule checkRule = new BillVOsCheckRule(new PUOrderVOValidator());
/* 35 */     checkRule.check(orders);
/*    */     
/* 37 */     BillVOsFillRule fillrule = new BillVOsFillRule();
/*    */     
/* 39 */     fillrule.addBillFillRule(new OrgAndGroupBillValueFill());
/*    */     
///* 41 */     fillrule.addBillFillRule(new Vtrantypecode(POBillType.Order.getCode()));
///* 42 */     fillrule.addBillFillRule(new Ctrantperid());
/*    */     
/* 44 */     fillrule.addBillFillRule(new RowNoBillFill());
/*    */     
/* 46 */     fillrule.addBillFillRule(new FillPaymentInfo());
/* 47 */     fillrule.addBillFillRule(new FillPuMaterialUnitRule());
/* 48 */     fillrule.addBillFillRule(new PUAssUnitAndChangeRate());
/*    */     
/* 50 */     fillrule.addBillFillRule(new FillDefaultValueRule());
/* 51 */     fillrule.fillValue(orders);
/* 52 */     return orders;
/*    */   }
/*    */ }


/* Location:              E:\NC\nchome\modules\pu\lib\pubpu_api.jar!/nc/vo/pu/m21/rule/api/FillOrderVOInfo.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.0.7
 */