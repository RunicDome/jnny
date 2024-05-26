/*    */ package nc.ui.so.m30.billui.action;
/*    */ 
/*    */ import nc.bs.pu.m20.maintain.rule.ATPAfterUpdateRule;
import nc.bs.pu.m20.maintain.rule.ATPBeforeUpdateRule;
import nc.bs.pu.m20.maintain.rule.approve.ApproveBudgetCtrlRule;
import nc.bs.pu.m20.maintain.rule.approve.CheckApproveRule;
import nc.bs.pu.m20.maintain.rule.approve.SendMsgToUserRule;
import nc.bs.pu.m20.plugin.PraybillPluginPoint;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.bs.scmpub.pf.PfParameterUtil;
import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.impl.pubapp.pattern.rule.IFilterRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.itf.pubapp.pub.exception.IResumeException;
import nc.vo.pu.m20.entity.PraybillVO;
import nc.vo.pu.pub.enumeration.PuBusiLogActionCode;
import nc.vo.pu.pub.enumeration.PuBusiLogPathCode;
import nc.vo.pu.pub.rule.busilog.WriteOperateLogRule;
import nc.vo.pu.pub.rule.pf.ApprovedVOFilterRule;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
/*    */ import nc.vo.so.m30.entity.SaleOrderHVO;
/*    */ import nc.vo.so.m30.entity.SaleOrderVO;
/*    */ import nc.vo.so.pub.enumeration.BillStatus;
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
/*    */ public class SaleOrderApproveAction
/*    */ {
/*    */   public SaleOrderApproveAction() {}
/*    */   
/*    */     public SaleOrderVO[] approve(SaleOrderVO[] vos, AbstractCompiler2 script)
/*    */   {
/* 46 */     PfParameterUtil<SaleOrderVO> util = new PfParameterUtil(script == null ? null : script.getPfParameterVO(), vos);
/*    */     
/*    */ 
/* 49 */     SaleOrderVO[] originBills = (SaleOrderVO[])util.getOrginBills();
/* 50 */     AroundProcesser<SaleOrderVO> processer = new AroundProcesser(PraybillPluginPoint.APPROVE);
/*    */     
/* 52 */     addBeforeRule(processer);
/* 53 */     addAfterRule(processer);
/*    */     
/*    */ 
/* 56 */     processer.before(vos);
/*    */     
/*    */ 
/* 59 */     if (null != script) {
/*    */       try {
/* 61 */         script.procFlowBacth(script.getPfParameterVO());
/*    */       }
/*    */       catch (Exception e) {
/* 64 */         ExceptionUtils.wrappException(e);
/*    */       }
/*    */     }
/*    */     
/*    */ 
/* 69 */     BillUpdate<SaleOrderVO> update = new BillUpdate();
/* 70 */     SaleOrderVO[] returnVos = (SaleOrderVO[])update.update(vos, originBills);
/*    */     
/*    */ 
/* 73 */     processer.after(returnVos);
/* 74 */     return returnVos;
/*    */   }
/*    */   
   private void addAfterRule(AroundProcesser<SaleOrderVO> processer)
/*    */   {
/* 80 */     processer.addAfterRule(new WriteOperateLogRule(PuBusiLogPathCode.prayBillApprovePath.getCode(), PuBusiLogActionCode.approve.getCode()));
/*    */     
/*    */ 
/*    */ 
/* 84 */     processer.addAfterRule((IFilterRule<SaleOrderVO>) new ATPAfterUpdateRule());
/*    */     
/* 86 */     processer.addAfterRule(new ApprovedVOFilterRule());
/*    */     
/* 88 */     processer.addAfterRule((IFilterRule<SaleOrderVO>) new SendMsgToUserRule());
/*    */     
/* 90 */     processer.addAfterRule((IFilterRule<SaleOrderVO>) new ApproveBudgetCtrlRule());
/*    */   }
/*    */  
   
   private void addBeforeRule(AroundProcesser<SaleOrderVO> processer)
   /*    */   {
   /* 96 */     processer.addBeforeRule((IFilterRule<SaleOrderVO>) new CheckApproveRule());
   /*    */     
   /* 98 */     processer.addBeforeRule((IFilterRule<SaleOrderVO>) new ATPBeforeUpdateRule());
   /*    */   }
/*    */ }

