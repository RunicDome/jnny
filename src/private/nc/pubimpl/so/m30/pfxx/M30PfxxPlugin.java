/*    */ package nc.pubimpl.so.m30.pfxx;
/*    */ 
/*    */ import java.util.ArrayList;
import java.util.List;

import nc.bs.trade.business.HYPubBO;
import nc.impl.pubapp.pattern.data.bill.BillQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillConcurrentTool;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.so.m30.action.main.InsertSaleOrderAction;
import nc.impl.so.m30.action.main.UpdateSaleOrderAction;
import nc.pubimpl.so.pfxx.AbstractSOPfxxPlugin;
import nc.pubimpl.so.pfxx.check.BillFreeStatusCheckRule;
import nc.pubimpl.so.pfxx.check.MnyTaxCheckRule;
import nc.pubimpl.so.pfxx.check.WriteBackInfoCheckRule;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.material.MaterialConvertVO;
import nc.vo.ic.general.define.MetaNameConst;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.so.m30.entity.SaleOrderBVO;
import nc.vo.so.m30.entity.SaleOrderHVO;
import nc.vo.so.m30.entity.SaleOrderVO;
import nc.vo.so.m30.pub.SaleOrderVOCalculator;
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
/*    */ public class M30PfxxPlugin extends AbstractSOPfxxPlugin
/*    */ {
/*    */   public M30PfxxPlugin() {}
/*    */   
/*    */   public List<IRule<AggregatedValueObject>> getCheckers()
/*    */   {
/* 23 */     List<IRule<AggregatedValueObject>> rules = new ArrayList();
/*    */     
/*    */ 
/* 26 */     rules.add(new BillFreeStatusCheckRule());
/*    */     
/* 28 */     rules.add(new MnyTaxCheckRule("ntax", "nmny", "ntaxmny"));
/*    */     
/*    */ 
/* 31 */     rules.add(new WriteBackInfoCheckRule(new String[] { "ntotalsendnum", "ntotalinvoicenum", "ntotaloutnum", "ntotalnotoutnum", "ntotalsignnum", "ntranslossnum", "ntotalrushnum", "ntotalestarnum", "ntotalarnum", "ntotalcostnum", "ntotalestarmny", "ntotalarmny", "ntotalpaymny", "norigsubmny", "narrangescornum", "narrangepoappnum", "narrangetoornum", "narrangetoappnum", "narrangemonum", "narrangeponum", "ntotalplonum", "ntotalreturnnum", "ntotaltradenum" }));
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
/* 57 */     return rules;
/*    */   }
/*    */   
/*    */ 
/*    */   protected AggregatedValueObject insert(AggregatedValueObject vo)
/*    */   {
/* 63 */     SaleOrderVO[] insertvo = { (SaleOrderVO)vo };

			/**
			 * 计算 [报价数量]、[含税单价]、[无税单价]、[含税净价]、[无税净价]、[主无税净价]、[主本币无税净价]、[无税金额]
			 *     [报价数量]、[含税单价]、[无税单价]、[含税净价]、[无税净价]、[无税金额]
			 */
			try {
				calculationSaleOrderVO(vo);
			} catch (UifException e) {
//				return e.getMessage();
			}
/* 66 */     InsertSaleOrderAction insertact = new InsertSaleOrderAction();
/* 67 */     SaleOrderVO[] retvos = insertact.insert(insertvo);
/* 68 */     if ((null == retvos) || (retvos.length == 0)) {
/* 69 */       return null;
/*    */     }
/* 71 */     return retvos[0];
/*    */   }
/**
 * @throws UifException 
 * 1、根据物料档案查询计量单位
 * 2、销售订单导入单价，自动算无税单价、价税合计等内容
 * 3、如果【现结】（表头自定义项3）=’Y’，则订单客户置为“现结客户”，发货客户、开票客户都为XML传入的客户。
 * @param vo
 * @throws  
 */
private void calculationSaleOrderVO(AggregatedValueObject vo) throws UifException{
	HYPubBO hyPubBO = new HYPubBO();
	SaleOrderVO aggVo = (SaleOrderVO)vo;
	SaleOrderBVO[] bvos = aggVo.getChildrenVO();
	SaleOrderVOCalculator soc = new SaleOrderVOCalculator(aggVo);
	soc.getNeedCalKey();
	String bd_currtype = (String) hyPubBO.findColValue("bd_currtype", "pk_currtype", "nvl(dr,0)=0 and code ='CNY'");
	SaleOrderHVO hvo = (SaleOrderHVO) vo.getParentVO();
	hvo.setChreceivecustid(hvo.getCinvoicecustid());
	hvo.setCorigcurrencyid(bd_currtype);
	String pk_group = (String) hyPubBO.findColValue("org_group", "pk_group", "nvl(dr,0) = 0 and code='1'");
	hvo.setPk_group(pk_group);
	String pk_org_v = (String) hyPubBO.findColValue("org_orgs", "pk_vid", "nvl(dr,0)=0 and pk_org ='" + hvo.getPk_org() + "'");
	hvo.setPk_org_v(pk_org_v);
	hvo.setDmakedate(new UFDate());
	String ctrantypeid = (String) hyPubBO.findColValue("bd_billtype", "pk_billtypeid", "nvl(dr,0)=0 and pk_billtypecode='30-Cxx-01'");
	hvo.setCtrantypeid(ctrantypeid);
	hvo.setVtrantypecode("30-Cxx-01");
	hvo.setNdiscountrate(new UFDouble("100"));
	String org_dept_v = (String) hyPubBO.findColValue("org_dept_v", "pk_dept", "nvl(dr,0) = 0 and pk_vid='" + hvo.getCdeptvid() + "'");
	hvo.setCdeptid(org_dept_v);
	/*业务流程：SO08*/
	String bd_busitype = (String) hyPubBO.findColValue("bd_busitype", "pk_busitype", "nvl(dr,0) = 0 and busicode = 'SO08'");
	hvo.setCbiztypeid(bd_busitype);
	if(null != hvo.getVdef3() && "Y".equals(hvo.getVdef3())){
		
	}
	for (int i = 0; i< bvos.length;i++) {
		String csettleorgvid = (String) hyPubBO.findColValue("org_financeorg", "pk_vid", "nvl(dr,0)=0 and pk_financeorg='" + bvos[i].getCsettleorgid() + "'");
		bvos[i].setCsettleorgvid(csettleorgvid);
		bvos[i].setFlargesstypeflag(1);
		bvos[i].setNaccprice(UFDouble.ZERO_DBL);
		bvos[i].setBlrgcashflag(UFBoolean.FALSE);
		bvos[i].setCmaterialid(bvos[i].getCmaterialvid());
		bvos[i].setCsendstockorgid(hvo.getPk_org());
		bvos[i].setCsendstockorgvid(hvo.getPk_org());
		bvos[i].setDbilldate(hvo.getDbilldate());
		bvos[i].setDsenddate(hvo.getDbilldate());
		bvos[i].setDreceivedate(hvo.getDbilldate());
		bvos[i].setNdiscountrate(new UFDouble("100"));
		bvos[i].setNdiscount(new UFDouble("100"));
		bvos[i].setNitemdiscountrate(new UFDouble("100"));
		bvos[i].setPk_group(pk_group);
		bvos[i].setFtaxtypeflag(1);
		bvos[i].setFbuysellflag(1);
		/*收货国家/地区*/
		String bd_countryzone = (String) hyPubBO.findColValue("bd_countryzone", "pk_country", "nvl(dr,0) = 0 and code='CN'");
		bvos[i].setCrececountryid(bd_countryzone);
		bvos[i].setCsendcountryid(bd_countryzone);
		bvos[i].setCtaxcountryid(bd_countryzone);
		bvos[i].setCreceivecustid(hvo.getCcustomerid());
		bvos[i].setCsettleorgvid(hvo.getPk_org());
		bvos[i].setCsettleorgid(hvo.getPk_org());
		bvos[i].setCarorgvid(hvo.getPk_org());
		bvos[i].setCarorgid(hvo.getPk_org());
		/*币种*/
		bvos[i].setCcurrencyid(bd_currtype);
		/*根据物料档案取主单位*/
		String pk_measdoc = (String) hyPubBO.findColValue("bd_material", "pk_measdoc", "nvl(dr,0) = 0 and pk_source='" + bvos[i].getCmaterialvid() + "'");
		bvos[i].setCunitid(pk_measdoc);
		/*单位*/
		MaterialConvertVO[] materialConvertVO = (MaterialConvertVO[]) hyPubBO.queryByCondition(MaterialConvertVO.class, "nvl(dr,0) = 0 and pk_material='" + bvos[i].getCmaterialvid() + "'");
		bvos[i].setCastunitid(materialConvertVO[0].getPk_measdoc());
		bvos[i].setCqtunitid(materialConvertVO[0].getPk_measdoc());
		
//			bvos[i].setNnum(null);
		bvos[i].setVchangerate(materialConvertVO[0].getMeasrate());
		bvos[i].setVqtunitrate(materialConvertVO[0].getMeasrate());
		bvos[i].setNexchangerate(UFDouble.ONE_DBL);
		bvos[i].setNqtunitnum(bvos[i].getNastnum());
		
		soc.calculate(i, MetaNameConst.NORIGTAXMNY);// 
		bvos[i].setNorigmny(bvos[i].getNorigtaxmny().sub(bvos[i].getNtax()));
	}
}
/*    */   
/*    */ 
/*    */   protected AggregatedValueObject update(AggregatedValueObject vo, String vopk)
/*    */   {
/* 77 */     SaleOrderVO[] updatevo = { (SaleOrderVO)vo };
/*    */     
/*    */ 
/* 80 */     BillQuery<SaleOrderVO> billquery = new BillQuery(SaleOrderVO.class);
/*    */     
/* 82 */     SaleOrderVO[] origvos = (SaleOrderVO[])billquery.query(new String[] { vopk });
/*    */     
/*    */ 
/* 85 */     BillConcurrentTool tool = new BillConcurrentTool();
/* 86 */     tool.lockBill(origvos);
/* 87 */     UpdateSaleOrderAction insertact = new UpdateSaleOrderAction();
/* 88 */     SaleOrderVO[] retvos = insertact.update(updatevo, origvos);
/* 89 */     if ((null == retvos) || (retvos.length == 0)) {
/* 90 */       return null;
/*    */     }
/* 92 */     return retvos[0];
/*    */   }
/*    */ }
