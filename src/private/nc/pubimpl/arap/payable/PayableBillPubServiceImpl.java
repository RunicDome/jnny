/*     */ package nc.pubimpl.arap.payable;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import nc.bs.arap.actions.BillTempSaveBatchAction;
/*     */ import nc.bs.arap.actions.PayablebillApproveBatchBSAction;
/*     */ import nc.bs.arap.actions.PayablebillDeleteBatchBSAction;
/*     */ import nc.bs.arap.actions.PayablebillEditBatchBSAction;
/*     */ import nc.bs.arap.actions.PayablebillSaveBatchBSAction;
/*     */ import nc.bs.arap.util.ArapFlowUtil;
/*     */ import nc.bs.arap.util.SqlUtils;
/*     */ import nc.bs.framework.common.InvocationInfoProxy;
/*     */ import nc.bs.framework.common.NCLocator;
/*     */ import nc.itf.uap.pf.IPFBusiAction;
/*     */ import nc.itf.uap.pf.IWorkflowMachine;
/*     */ import nc.md.persist.framework.IMDPersistenceQueryService;
/*     */ import nc.md.persist.framework.MDPersistenceService;
/*     */ import nc.pubitf.arap.payable.IArapPayableBillPubService;
/*     */ import nc.vo.arap.basebill.BaseAggVO;
/*     */ import nc.vo.arap.basebill.BaseItemVO;
/*     */ import nc.vo.arap.payable.AggPayableBillVO;
/*     */ import nc.vo.arap.payable.PayableBillItemVO;
/*     */ import nc.vo.arap.payable.PayableBillVO;
/*     */ import nc.vo.arap.utils.ArrayUtil;
/*     */ import nc.vo.fipub.exception.ExceptionHandler;
/*     */ import nc.vo.pub.AggregatedValueObject;
/*     */ import nc.vo.pub.BusinessException;
/*     */ import nc.vo.pub.CircularlyAccessibleValueObject;
/*     */ import nc.vo.pub.ISuperVO;
/*     */ 
/*     */ public class PayableBillPubServiceImpl implements IArapPayableBillPubService
/*     */ {
/*     */   public PayableBillPubServiceImpl() {}
/*     */   
/*     */   public AggPayableBillVO[] saveTemp(AggPayableBillVO[] bills) throws BusinessException
/*     */   {
/*  40 */     AggregatedValueObject[] retvos = new BillTempSaveBatchAction().insertVOs(bills);
/*     */     
/*  42 */     return (AggPayableBillVO[])ArrayUtil.convertSupers2Subs(retvos, AggPayableBillVO.class);
/*     */   }
/*     */   
/*     */   public void deleteBillBySourcePK(String[] keys) throws BusinessException
/*     */   {
/*  47 */     String condition = null;
/*  48 */     Collection<AggPayableBillVO> bills = null;
/*     */     try {
/*  50 */       condition = SqlUtils.getInStr("top_billid", keys, new boolean[] { true });
/*  51 */       String sql = "pk_payablebill in( select ap_payableitem.pk_payablebill from ap_payableitem where ap_payableitem.dr=0 and (" + condition + ")) and ap_payablebill.dr=0 ";
/*     */       
/*  53 */       bills = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByCond(AggPayableBillVO.class, sql, false);
/*     */     }
/*     */     catch (SQLException e) {
/*  56 */       throw ExceptionHandler.handleException(e);
/*     */     }
/*  58 */     if ((bills != null) && (bills.size() > 0)) {
/*  59 */       for (BaseAggVO bill : bills) {
/*  60 */         bill.setIsOtherModuleOriginate(Boolean.valueOf(true));
/*     */       }
/*  62 */       delete((AggPayableBillVO[])bills.toArray(new AggPayableBillVO[bills.size()]));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void deleteBillBySourceitemPK(String[] keys)
/*     */     throws BusinessException
/*     */   {
/*  70 */     String condition = null;
/*  71 */     Collection<AggPayableBillVO> bills = null;
/*     */     try {
/*  73 */       condition = SqlUtils.getInStr("top_itemid", keys, new boolean[] { true });
/*  74 */       String sql = "pk_payablebill in( select ap_payableitem.pk_payablebill from ap_payableitem where ap_payableitem.dr=0 and (" + condition + ")) and ap_payablebill.dr=0 ";
/*     */       
/*  76 */       bills = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByCond(AggPayableBillVO.class, sql, false);
/*     */     }
/*     */     catch (SQLException e) {
/*  79 */       throw ExceptionHandler.handleException(e);
/*     */     }
/*  81 */     List<AggPayableBillVO> editlst = new ArrayList();
/*  82 */     List<AggPayableBillVO> dellst = new ArrayList();
/*     */     List<BaseItemVO> rets;
/*  84 */     if ((bills != null) && (bills.size() > 0)) {
/*  85 */       rets = new ArrayList();
/*  86 */       for (Object object : bills) {
/*  87 */         AggregatedValueObject bill = (AggregatedValueObject)object;
/*  88 */         for (CircularlyAccessibleValueObject item : bill.getChildrenVO())
/*     */         {
/*  90 */           if (!Integer.valueOf(1).equals(item.getAttributeValue("dr")))
/*     */           {
/*  92 */             rets.add((BaseItemVO)item);
/*     */           }
/*     */         }
/*  95 */         bill.setChildrenVO((CircularlyAccessibleValueObject[])rets.toArray(new BaseItemVO[0]));
/*  96 */         rets.clear();
/*     */       }
/*     */     }
/*     */     
/* 100 */     if ((bills != null) && (bills.size() > 0)) {
/* 101 */       List<String> key = Arrays.asList(keys);
/*     */       
/* 103 */       for (AggPayableBillVO vo : bills) {
/* 104 */         boolean alldel = true;
/* 105 */         for (PayableBillItemVO item : vo.getBodyVOs()) {
/* 106 */           if (key.contains(item.getTop_itemid())) {
/* 107 */             item.setStatus(3);
/*     */           } else {
/* 109 */             item.setStatus(0);
/* 110 */             alldel = false;
/*     */           }
/*     */         }
/* 113 */         if (alldel) {
/* 114 */           dellst.add(vo);
/*     */         } else {
/* 116 */           editlst.add(vo);
/*     */         }
/*     */       }
/* 119 */       if (dellst.size() > 0) {
/* 120 */         delete((AggPayableBillVO[])dellst.toArray(new AggPayableBillVO[0]));
/*     */       }
/*     */       
/* 123 */       if (editlst.size() > 0) {
/* 124 */         update((AggPayableBillVO[])editlst.toArray(new AggPayableBillVO[0]));
/*     */       }
/*     */     }
/*     */     
/* 128 */     if ((bills != null) && (bills.size() > 0))
/*     */     {
/* 130 */       delete((AggPayableBillVO[])bills.toArray(new AggPayableBillVO[bills.size()]));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void deleteBillByPurchaseInvoices(String[] keys)
/*     */     throws BusinessException
/*     */   {
/* 139 */     String condition = null;
/* 140 */     Collection bills = null;
/*     */     try {
/* 142 */       condition = SqlUtils.getInStr("invoiceno", keys, new boolean[] { true });
/* 143 */       String sql = "pk_payablebill in( select ap_payableitem.pk_payablebill from ap_payableitem where dr=0 and (" + condition + ")) and dr=0 ";
/*     */       
/* 145 */       bills = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByCond(AggPayableBillVO.class, sql, false);
/*     */     }
/*     */     catch (SQLException e) {
/* 148 */       throw ExceptionHandler.handleException(e);
/*     */     }
/* 150 */     if ((bills != null) && (bills.size() > 0)) {
/* 151 */       delete((AggPayableBillVO[])bills.toArray(new AggPayableBillVO[bills.size()]));
/*     */     }
/*     */   }
/*     */   
/*     */   private PayablebillSaveBatchBSAction getSaveBSAction()
/*     */   {
/* 157 */     return new PayablebillSaveBatchBSAction();
/*     */   }
/*     */   
/*     */   private PayablebillEditBatchBSAction getUpdateBSAction() {
/* 161 */     return new PayablebillEditBatchBSAction();
/*     */   }
/*     */   
/*     */   private PayablebillDeleteBatchBSAction getDeleteBSAction() {
/* 165 */     return new PayablebillDeleteBatchBSAction();
/*     */   }
/*     */   
/*     */   private PayablebillApproveBatchBSAction getApproveBSAction() {
/* 169 */     return new PayablebillApproveBatchBSAction();
/*     */   }
/*     */   
/*     */   public void delete(AggPayableBillVO bill) throws BusinessException
/*     */   {
/* 174 */     delete(new AggPayableBillVO[] { bill });
/*     */   }
/*     */   
/*     */   public void delete(AggPayableBillVO[] bills) throws BusinessException
/*     */   {
/* 179 */     getDeleteBSAction().deleteVOs(bills);
/*     */     
/* 181 */     for (AggPayableBillVO bill : bills) {
/* 182 */       ISuperVO parent = bill.getParent();
/* 183 */       ((IWorkflowMachine)NCLocator.getInstance().lookup(IWorkflowMachine.class)).deleteCheckFlow((String)parent.getAttributeValue("pk_tradetype"), parent.getPrimaryKey(), bill, InvocationInfoProxy.getInstance().getUserId());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AggPayableBillVO save(AggPayableBillVO bill)
/*     */     throws BusinessException
/*     */   {
/* 197 */     return (AggPayableBillVO)ArrayUtil.getFirstInArrays(save(new AggPayableBillVO[] { bill }));
/*     */   }
/*     */   
/*     */ 
/*     */   public AggPayableBillVO[] save(AggPayableBillVO[] bills)
/*     */     throws BusinessException
/*     */   {
/* 204 */     return (AggPayableBillVO[])ArrayUtil.convertSupers2Subs(((IPFBusiAction)NCLocator.getInstance().lookup(IPFBusiAction.class)).processBatch(ArapFlowUtil.getCommitActionCode(bills[0].getHeadVO().getPk_org(), bills[0].getHeadVO().getPk_tradetype()), "F1", bills, null, null, null), AggPayableBillVO.class);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public AggPayableBillVO update(AggPayableBillVO bill)
/*     */     throws BusinessException
/*     */   {
/* 212 */     return (AggPayableBillVO)ArrayUtil.getFirstInArrays(update(new AggPayableBillVO[] { bill }));
/*     */   }
/*     */   
/*     */ 
/*     */   public AggPayableBillVO[] update(AggPayableBillVO[] bills)
/*     */     throws BusinessException
/*     */   {
/* 219 */     return (AggPayableBillVO[])ArrayUtil.convertSupers2Subs(getUpdateBSAction().updateVOs(bills), AggPayableBillVO.class);
/*     */   }
/*     */   
/*     */ 
/*     */   public AggPayableBillVO[] approve(AggPayableBillVO[] bills)
/*     */     throws BusinessException
/*     */   {
/* 226 */     return (AggPayableBillVO[])ArrayUtil.convertSupers2Subs(getApproveBSAction().approveVOs(bills), AggPayableBillVO.class);
/*     */   }
/*     */   
/*     */ 
/*     */   public AggPayableBillVO approve(AggPayableBillVO bill)
/*     */     throws BusinessException
/*     */   {
/* 233 */     return (AggPayableBillVO)ArrayUtil.getFirstInArrays(approve(new AggPayableBillVO[] { bill }));
/*     */   }
/*     */ }

/* Location:           D:\nchome\modules\arap\META-INF\classes
 * Qualified Name:     nc.pubimpl.arap.payable.PayableBillPubServiceImpl
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.0.1
 */