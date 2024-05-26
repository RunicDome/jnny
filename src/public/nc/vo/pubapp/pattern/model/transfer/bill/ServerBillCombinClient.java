/*     */ package nc.vo.pubapp.pattern.model.transfer.bill;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import nc.vo.pub.ISuperVO;
/*     */ import nc.vo.pub.IVOMeta;
/*     */ import nc.vo.pubapp.pattern.model.entity.bill.IBill;
/*     */ import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;
/*     */ import nc.vo.pubapp.pattern.model.tool.VOTool;
/*     */ import nc.vo.pubapp.pattern.pub.ListToArrayTool;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServerBillCombinClient<E extends IBill>
/*     */ {
/*     */   public ServerBillCombinClient() {}
/*     */   
/*     */   private void combine(E bill, E clientBill)
/*     */   {
/*  28 */     IBillMeta billMeta = bill.getMetaData();
/*  29 */     IVOMeta parentMeta = billMeta.getParent();
/*  30 */     VOTool tool = new VOTool();
/*  31 */     if (parentMeta != null) {
/*  32 */       tool.combine(bill.getParent(), clientBill.getParent());
/*     */     }
/*  34 */     IVOMeta[] children = billMeta.getChildren();
/*  35 */     if (children == null) {
/*  36 */       return;
/*     */     }
/*  38 */     for (IVOMeta child : children) {
/*  39 */       combine(bill, clientBill, child);
/*     */     }
/*     */   }
/*     */   
/*     */   private void combine(E bill, E clientBill, IVOMeta voMeta) {
/*  44 */     ISuperVO[] childrenVO = clientBill.getChildren(voMeta);
/*  45 */     if (childrenVO == null) {
/*  46 */       return;
/*     */     }
/*  48 */     Map<String, ISuperVO> changedIndex = new HashMap();
/*  49 */     List<ISuperVO> newList = new ArrayList();
/*  50 */     orgnizeData(childrenVO, changedIndex, newList);
/*  51 */     boolean flag = check(changedIndex, newList, bill, clientBill, voMeta);
/*  52 */     if (!flag) {
/*  53 */       return;
/*     */     }
/*  55 */     VOTool tool = new VOTool();
/*  56 */     List<ISuperVO> oldList = new ArrayList();
/*  57 */     childrenVO = bill.getChildren(voMeta);
/*  58 */     for (ISuperVO child : childrenVO) {
/*  59 */       String pk = child.getPrimaryKey();
/*  60 */       ISuperVO vo = (ISuperVO)changedIndex.get(pk);
/*  61 */       if (vo != null) {
/*  62 */         tool.combine(child, vo);
/*     */       }
/*  64 */       oldList.add(child);
/*     */     }
/*  66 */     oldList.addAll(newList);
/*  67 */     ListToArrayTool<ISuperVO> arrayTool = new ListToArrayTool();
/*  68 */     ISuperVO[] vos = (ISuperVO[])arrayTool.convertToArray(oldList);
/*  69 */     bill.setChildren(voMeta, vos);
/*     */   }
/*     */   
/*     */   private void orgnizeData(ISuperVO[] childrenVO, Map<String, ISuperVO> changedIndex, List<ISuperVO> newList)
/*     */   {
/*  74 */     for (ISuperVO vo : childrenVO)
/*     */     {
/*  76 */       if (vo != null)
/*     */       {
/*     */ 
/*  79 */         String pk = vo.getPrimaryKey();
/*  80 */         if ((pk != null) && (vo.getStatus() != 2)) {
/*  81 */           changedIndex.put(pk, vo);
/*     */         }
/*     */         else {
/*  84 */           newList.add(vo);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean check(Map<String, ISuperVO> changedIndex, List<ISuperVO> newList, E bill, E clientBill, IVOMeta voMeta) {
/*  91 */     boolean flag = true;
/*     */     
/*  93 */     if ((changedIndex.size() == 0) && (newList.size() == 0)) {
/*  94 */       return false;
/*     */     }
/*  96 */     ISuperVO[] childrenVO = bill.getChildren(voMeta);
/*     */     
/*  98 */     if (childrenVO == null) {
/*  99 */       bill.setChildren(voMeta, clientBill.getChildren(voMeta));
/* 100 */       flag = false;
/*     */     }
/* 102 */     return flag;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void combine(E[] bills, E[] clientBills)
/*     */   {
/* 112 */     int length = bills.length;
/* 113 */     for (int i = 0; i < length; i++) {
/* 114 */       combine(bills[i], clientBills[i]);
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\nchome\modules\pubapputil\lib\pubpubapputil_pubapputilLevel-1.jar
 * Qualified Name:     nc.vo.pubapp.pattern.model.transfer.bill.ServerBillCombinClient
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.0.1
 */