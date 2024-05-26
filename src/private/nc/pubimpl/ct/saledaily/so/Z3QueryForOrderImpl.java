/*    */ package nc.pubimpl.ct.saledaily.so;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import nc.impl.pubapp.pattern.data.bill.BillQuery;
/*    */ import nc.pubitf.ct.saledaily.so.IZ3QueryForOrder;
/*    */ import nc.vo.ct.saledaily.entity.AggCtSaleVO;
/*    */ import nc.vo.ct.saledaily.entity.CtSalePayTermVO;
/*    */ import nc.vo.ct.saledaily.entity.CtSaleVO;
/*    */ import nc.vo.ct.uitl.ValueUtil;
/*    */ import nc.vo.pub.BusinessException;
/*    */ import nc.vo.pubapp.pattern.exception.ExceptionUtils;
/*    */ import org.apache.commons.lang.ArrayUtils;
/*    */ 
/*    */ public class Z3QueryForOrderImpl implements IZ3QueryForOrder
/*    */ {
/*    */   public Z3QueryForOrderImpl() {}
/*    */   
/*    */   public Map<String, CtSalePayTermVO[]> queryIsShowPayterm(String[] pk_ct_sales) throws BusinessException
/*    */   {
//	     	 String sql= "SELECT * FROM po_storereq_b WHERE vbdef20='~' or( vbdef20 !='~' and to_number(nastnum) > to_number(vbdef20))   ORDER BY ts DESC";
/* 21 */     Map<String, CtSalePayTermVO[]> map = new HashMap();
/*    */     
/* 23 */     if (ValueUtil.isEmpty(pk_ct_sales)) {
/* 24 */       return map;
/*    */     }
/*    */     try {
/* 27 */       BillQuery<AggCtSaleVO> queryVO = new BillQuery(AggCtSaleVO.class);
/*    */       
/* 29 */       AggCtSaleVO[] bills = (AggCtSaleVO[])queryVO.query(pk_ct_sales);
/* 30 */       if (!ArrayUtils.isEmpty(bills)) {
/* 31 */         for (AggCtSaleVO bill : bills) {
/* 32 */           if (!ArrayUtils.isEmpty(bill.getCtSalePayTermVO())) {
/* 33 */             map.put(bill.getParentVO().getPk_ct_sale(), bill.getCtSalePayTermVO());
/*    */           }
/*    */         }
/*    */         
/*    */ 
/* 38 */         return map;
/*    */       }
/*    */     }
/*    */     catch (Exception e) {
/* 42 */       ExceptionUtils.marsh(e);
/*    */     }
/*    */     
/* 45 */     return map;
/*    */   }
/*    */ }

/* Location:           E:\HYJG-MJ\MJ-NC-JNRL\Rlnchome\modules\ct\META-INF\lib\ct_bill.jar
 * Qualified Name:     nc.pubimpl.ct.saledaily.so.Z3QueryForOrderImpl
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.0.1
 */