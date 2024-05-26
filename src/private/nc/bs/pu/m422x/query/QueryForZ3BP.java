package nc.bs.pu.m422x.query;

/*    */ 
/*    */ import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pu.m422x.entity.StoreReqAppItemVO;
import nc.vo.pu.m422x.entity.StoreReqAppVO;
import nc.vo.pu.pub.enumeration.POEnumBillStatus;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.pub.SqlBuilder;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 

/*    */ public class QueryForZ3BP
/*    */   extends Abstract422XRefQueryBP
/*    */ {
/*    */   public QueryForZ3BP(IQueryScheme queryScheme)
/*    */   {
/* 20 */     super(queryScheme);
/*    */   }
/*    */   
/*    */   public StringBuilder makeGetPKSql()
/*    */   {
/* 25 */     String mainTableAlias = getHeadtb();
/* 26 */     String itemTableAlias = getItemtb();
/*    */     
/* 28 */     SqlBuilder whereSql = new SqlBuilder();
/* 29 */     whereSql.append(" and ");
/* 30 */     whereSql.append(mainTableAlias + ".fbillstatus", (Integer)POEnumBillStatus.APPROVE.value());
/*    */     
/* 32 */     whereSql.append(" and ");
/* 33 */     whereSql.append(mainTableAlias + ".dr = 0");
/* 34 */     whereSql.append(" and " + itemTableAlias + ".dr = 0");
/* 36 */     whereSql.append(" and (");
/* 37 */     whereSql.append(" " + itemTableAlias + ".vbdef20='~'");
/* 38 */     whereSql.append(" or (" + itemTableAlias + ".vbdef20 != '~' and to_number(" + itemTableAlias + ".nastnum) > to_number(" + itemTableAlias + ".vbdef20)))");
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 43 */     FilterForPlanPosUtil.filterMaterialByPos(this.psor, whereSql);
/*    */     
/* 45 */     this.psor.appendWhere(whereSql.toString());
/*    */     
/* 47 */     StringBuilder wholeSql = new StringBuilder();
/* 48 */     wholeSql.append(" select distinct " + mainTableAlias + ".pk_storereq,");
/* 49 */     wholeSql.append(itemTableAlias + ".pk_storereq_b ");
/* 50 */     wholeSql.append(this.psor.getFinalFromWhere());
/*    */     
/* 52 */     return wholeSql;
/*    */   }
/*    */   
/*    */   protected StoreReqAppVO[] processQueryResult(StoreReqAppVO[] queryResult)
/*    */   {
/* 57 */     for (StoreReqAppVO vo : queryResult) {
/* 58 */       for (StoreReqAppItemVO item : vo.getBVO()) {
/* 59 */         UFDouble naccumbuyreqnum = item.getNaccumbuyreqnum();
/* 60 */         if (naccumbuyreqnum == null) {
/* 61 */           naccumbuyreqnum = UFDouble.ZERO_DBL;
/*    */         }
/* 63 */         item.setNcanbuyreqnnum(item.getNnum().sub(naccumbuyreqnum));
/*    */       }
/*    */     }
/* 66 */     return super.processQueryResult(queryResult);
/*    */   }
/*    */ }