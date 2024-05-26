/*     */ package nc.vo.ct.vochange;
/*     */ 
/*     */ import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.AppBsContext;
import nc.bs.trade.business.HYPubBO;
import nc.itf.scmpub.reference.uap.bd.currency.CurrencyRate;
import nc.itf.scmpub.reference.uap.bd.supplier.SupplierPubService;
import nc.itf.scmpub.reference.uap.org.DeptPubService;
import nc.itf.scmpub.reference.uap.org.OrgUnitPubService;
import nc.itf.scmpub.reference.uap.para.SysParaInitQuery;
import nc.itf.scmpub.reference.uap.pf.PfServiceScmUtil;
import nc.itf.scmpub.reference.uap.rbac.UserManageQuery;
import nc.pubitf.ct.business.IBusinessTypeService;
import nc.pubitf.uapbd.IPsndocPubService;
import nc.vo.bd.supplier.finance.SupFinanceVO;
import nc.vo.bd.taxcode.TaxrateVO;
import nc.vo.ct.business.entity.BusinessSetVO;
import nc.vo.ct.enumeration.CtFlowEnum;
import nc.vo.ct.rule.SaleRelationCalculate;
import nc.vo.ct.saledaily.entity.AggCtSaleVO;
import nc.vo.ct.saledaily.entity.CtSaleBVO;
import nc.vo.ct.saledaily.entity.CtSaleVO;
import nc.vo.ct.uitl.ValueUtil;
import nc.vo.org.FinanceOrgVO;
import nc.vo.org.OrgVO;
import nc.vo.org.SalesOrgVO;
import nc.vo.pf.change.ChangeVOAdjustContext;
import nc.vo.pf.change.IChangeVOAdjust;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.scmpub.res.para.NCPara;
import nc.vo.scmpub.util.TimeUtils;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SaleChangeVOAdjust422X0ToZ3
/*     */   implements IChangeVOAdjust
/*     */ {
/*     */   public SaleChangeVOAdjust422X0ToZ3() {}
/*     */   
/*     */   public AggregatedValueObject adjustAfterChange(AggregatedValueObject srcVO, AggregatedValueObject destVO, ChangeVOAdjustContext adjustContext)
/*     */     throws BusinessException
/*     */   {
/*  56 */     if (null == destVO) {
/*  57 */       return null;
/*     */     }
/*     */     
/*  60 */     return batchAdjustAfterChange(new AggregatedValueObject[] { srcVO }, new AggregatedValueObject[] { destVO }, adjustContext)[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AggregatedValueObject adjustBeforeChange(AggregatedValueObject srcVO, ChangeVOAdjustContext adjustContext)
/*     */     throws BusinessException
/*     */   {
/*  70 */     if (null == srcVO) {
/*  71 */       return null;
/*     */     }
/*     */     
/*  74 */     return batchAdjustBeforeChange(new AggregatedValueObject[] { srcVO }, adjustContext)[0];
/*     */   }
/*     */   
/*     */
//TODO 单据同步OA功能  start create by zwh
private HYPubBO hyPubBO;

public HYPubBO getHyPubBO() {
	if(null == hyPubBO){
		hyPubBO = new HYPubBO();
	}
	return hyPubBO;
}

public void setHyPubBO(HYPubBO hyPubBO) {
	this.hyPubBO = hyPubBO;
}
/*     */ 
/*     */ 
/*     */   public AggregatedValueObject[] batchAdjustAfterChange(AggregatedValueObject[] srcVOs, AggregatedValueObject[] destVOs, ChangeVOAdjustContext adjustContext)
/*     */     throws BusinessException
/*     */   {
/*  83 */     if ((ArrayUtils.isEmpty(srcVOs)) || (ArrayUtils.isEmpty(destVOs))) {
/*  84 */       return null;
/*     */     }
/*  86 */     AggCtSaleVO[] vos = (AggCtSaleVO[])destVOs;
/*     */     

for(AggCtSaleVO temp:vos){
	HYPubBO hyPubBo = new HYPubBO();
	String pk_stock_org = temp.getParentVO().getPk_org();
	SalesOrgVO[]  salesOrgVOs = (SalesOrgVO[]) hyPubBo.queryByCondition(SalesOrgVO.class, "nvl(dr,0) = 0 and pk_salesorg = '" + pk_stock_org + "'");
	String pk_org = salesOrgVOs[0].getPk_salesorg();
	String pk_org_v = salesOrgVOs[0].getPk_vid();
	String cbilltypecode = (String) hyPubBo.findColValue("bd_billtype", "pk_billtypeid", " nvl(dr,0) = 0 and pk_billtypecode ='Z3-01' and pk_group in (SELECT pk_group FROM org_group WHERE code = '001' and nvl(dr,0) = 0)");
	temp.getParentVO().setCtrantypeid(cbilltypecode);
	temp.getParentVO().setVtrantypecode("Z3-01");
	temp.getParentVO().setCbilltypecode("Z3");
	temp.getParentVO().setPk_org(pk_org);
	temp.getParentVO().setPk_org_v(pk_org_v);
	CtSaleBVO[] bos=temp.getCtSaleBVO();
	FinanceOrgVO[] financeOrgVO= (FinanceOrgVO[])hyPubBo.queryByCondition(FinanceOrgVO.class, "nvl(dr,0) = 0 and pk_org = '" + pk_org_v + "'");
	 for(CtSaleBVO vo :bos){
		vo.setPk_org(pk_org);
		vo.setPk_org_v(pk_org_v);
		String ctaxcodeid  = (String)getHyPubBO().findColValue("bd_material", "pk_mattaxes", "nvl(dr,0) = 0 and pk_material = '" + vo.getPk_material() + "'");
		
		String  pk_taxcode = (String)getHyPubBO().findColValue("bd_taxcode", "pk_taxcode", "nvl(dr,0) = 0 and mattaxes = '" + ctaxcodeid  + "'");
		
		vo.setCtaxcodeid(pk_taxcode);
		TaxrateVO[] taxrateVO =  (TaxrateVO[]) getHyPubBO().queryByCondition(TaxrateVO.class, "nvl(dr,0) = 0 and pk_taxcode = '" + pk_taxcode + "'");
		if(taxrateVO.length > 0){
			vo.setNtaxrate(taxrateVO[0].getTaxrate());
		}
		vo.setPk_financeorg(pk_org);
		vo.setPk_financeorg_v(pk_org_v);
	}
}



/*  95 */     return vos;
/*     */   }
/*     */   
/*     */ 
/*     */   public AggregatedValueObject[] batchAdjustBeforeChange(AggregatedValueObject[] srcVOs, ChangeVOAdjustContext adjustContext)
/*     */     throws BusinessException
/*     */   {
/* 102 */     return srcVOs;
/*     */   }
/*     */   
/*     */   private Map<String, BusinessSetVO> getBusinessSetVOs(String[] ctrantypeids) {
/*     */     try {
/* 107 */       IBusinessTypeService iBusiness = (IBusinessTypeService)NCLocator.getInstance().lookup(IBusinessTypeService.class.getName());
/*     */       
/*     */ 
/*     */ 
/* 111 */       return iBusiness.queryBusinessMaps(ctrantypeids);
/*     */     }
/*     */     catch (Exception e) {
/* 114 */       ExceptionUtils.wrappException(e);
/*     */     }
/* 116 */     return null;
/*     */   }
/*     */   
/*     */   private String[] prepareTranType(AggCtSaleVO[] vos) {
/* 120 */     Set<String> trantypes = new HashSet();
/* 121 */     for (AggCtSaleVO aggvo : vos) {
/* 122 */       String type = aggvo.getParentVO().getVtrantypecode();
/* 123 */       if (!ValueUtil.isEmpty(type))
/*     */       {
/*     */ 
/* 126 */         trantypes.add(type); }
/*     */     }
/* 128 */     return (String[])trantypes.toArray(new String[0]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setDefault(AggCtSaleVO[] vos)
/*     */   {
/* 144 */     UFDate busiDate = AppBsContext.getInstance().getBusiDate();
/* 145 */     UFDate date = TimeUtils.getStartDate(busiDate);
/* 146 */     for (AggCtSaleVO aggCtSaleVO : vos) {
/* 147 */       CtSaleVO headVo = aggCtSaleVO.getParentVO();
/*     */       
/* 149 */       String pk_currtype = OrgUnitPubService.queryOrgCurrByPk(headVo.getPk_org());
/*     */       
/* 151 */       headVo.setCcurrencyid(pk_currtype);
/* 152 */       String customer = headVo.getPk_customer();
/* 153 */       String[] fields = { "pk_currtype" };
/*     */       
/*     */ 
/* 156 */       if (StringUtils.isNotBlank(customer)) {
/* 157 */         SupFinanceVO[] supvos = SupplierPubService.getSupFinanceVO(new String[] { customer }, headVo.getPk_org(), fields);
/*     */         
/*     */ 
/*     */ 
/* 161 */         if (!ArrayUtils.isEmpty(supvos)) {
/* 162 */           String pk_curr = supvos[0].getPk_currtype();
/* 163 */           if (StringUtils.isNotBlank(pk_curr)) {
/* 164 */             headVo.setCorigcurrencyid(pk_curr);
/*     */           }
/*     */         }
/*     */       }
/* 168 */       setExchangerate(date, headVo, pk_currtype);
/*     */       
/* 170 */       headVo.setSubscribedate(date);
/* 171 */       headVo.setFstatusflag((Integer)CtFlowEnum.Free.value());
/*     */       
/* 173 */       CtSaleBVO[] bvos = aggCtSaleVO.getCtSaleBVO();
/* 174 */       setFinanceVo(bvos, aggCtSaleVO.getParentVO().getPk_org());
/*     */       
/*     */ 
/* 177 */       SaleRelationCalculate calculate = new SaleRelationCalculate();
/* 178 */       calculate.calculate(aggCtSaleVO, "nexchangerate");
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setExchangerate(UFDate date, CtSaleVO headVo, String pk_currtype)
/*     */   {
/* 203 */     UFDouble nexchangerate = CurrencyRate.getCurrencyBuyRateByOrg(headVo.getPk_org(), headVo.getCorigcurrencyid(), pk_currtype, date);
/*     */     
/*     */ 
/* 206 */     headVo.setNexchangerate(nexchangerate);
/* 207 */     UFDouble globalChangeRate = null;
/*     */     
/* 209 */     String nc002 = SysParaInitQuery.getParaString("GLOBLE00000000000000", "NC002");
/* 210 */     if (NCPara.NC002_CALCULATEBYORIGCURRTYPE.getName().equals(nc002)) {
/* 211 */       globalChangeRate = CurrencyRate.getGlobalLocalCurrencyBuyRate(headVo.getCorigcurrencyid(), date);
/*     */ 
/*     */ 
/*     */     }
/* 215 */     else if (NCPara.NC002_CALCULATEBYCURRTYPE.getName().equals(nc002)) {
/* 216 */       globalChangeRate = CurrencyRate.getGlobalLocalCurrencyBuyRate(pk_currtype, date);
/*     */     }
/*     */     
/* 219 */     if (globalChangeRate != null) {
/* 220 */       headVo.setNglobalexchgrate(globalChangeRate);
/*     */     }
/*     */     
/*     */ 
/* 224 */     UFDouble groupChangeRate = null;
/*     */     
/* 226 */     String nc001 = SysParaInitQuery.getParaString(headVo.getPk_group(), "NC001");
/*     */     
/* 228 */     if (NCPara.NC001_CALCULATEBYORIGCURRTYPE.getName().equals(nc001)) {
/* 229 */       groupChangeRate = CurrencyRate.getGroupLocalCurrencyBuyRate(headVo.getCorigcurrencyid(), date);
/*     */ 
/*     */ 
/*     */     }
/* 233 */     else if (NCPara.NC001_CALCULATEBYCURRTYPE.getName().equals(nc001)) {
/* 234 */       groupChangeRate = CurrencyRate.getGroupLocalCurrencyBuyRate(pk_currtype, date);
/*     */     }
/*     */     
/* 237 */     if (groupChangeRate != null) {
/* 238 */       headVo.setNgroupexchgrate(groupChangeRate);
/*     */     }
/*     */   }
/*     */   
/*     */   private void setFinanceVo(CtSaleBVO[] bvos, String pk_org) {
/* 243 */     if (ArrayUtils.isEmpty(bvos)) {
/* 244 */       return;
/*     */     }
/* 246 */     for (CtSaleBVO bvo : bvos) {
/* 247 */       String pk_org_v = null;
/* 248 */       boolean blen = false;
/* 249 */       if (!ValueUtil.isEmpty(pk_org)) {
/* 250 */         pk_org_v = OrgUnitPubService.getOrgVid(pk_org);
/*     */       }
/*     */       
/*     */ 
/* 254 */       blen = OrgUnitPubService.isTypeOf(pk_org, "FINANCEORGTYPE000000");
/* 255 */       if (blen) {
/* 256 */         bvo.setPk_financeorg(pk_org);
/* 257 */         bvo.setPk_financeorg_v(pk_org_v);
/*     */       }
/*     */       else {
/* 260 */         OrgVO vo = OrgUnitPubService.getOrg(pk_org);
/* 261 */         String pk_corp = vo.getPk_corp();
/* 262 */         String pk_corp_v = OrgUnitPubService.getOrgVid(pk_corp);
/* 263 */         bvo.setPk_financeorg(pk_corp);
/* 264 */         bvo.setPk_financeorg_v(pk_corp_v);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void setPsnAndDept(AggCtSaleVO[] vos) {
/* 270 */     Set<String> psnSet = new HashSet();
/* 271 */     String userid = AppContext.getInstance().getPkUser();
/* 272 */     String psn = UserManageQuery.queryPsndocByUserid(userid);
/* 273 */     for (AggCtSaleVO aggvo : vos) {
/* 274 */       CtSaleVO headVo = aggvo.getParentVO();
/* 275 */       String psnid = headVo.getPersonnelid();
/* 276 */       if (psnid == null) {
/* 277 */         psnid = psn;
/* 278 */         if (psnid != null)
/*     */         {
/*     */ 
/*     */ 
/* 282 */           headVo.setPersonnelid(psnid); }
/*     */       } else {
/* 284 */         psnSet.add(psnid);
/*     */       }
/*     */     }
/* 287 */     Set<String> depSet = new HashSet();
/* 288 */     Map<String, List<String>> results = null;
/* 289 */     if (psnSet.size() > 0) {
/*     */       try {
/* 291 */         results = ((IPsndocPubService)NCLocator.getInstance().lookup(IPsndocPubService.class)).queryDeptIDByPsndocIDs((String[])psnSet.toArray(new String[0]));
/*     */ 
/*     */       }
/*     */       catch (BusinessException e)
/*     */       {
/* 296 */         ExceptionUtils.wrappException(e);
/*     */       }
/* 298 */       if ((results != null) && (results.size() > 0)) {
/* 299 */         for (String psnid : psnSet)
/*     */         {
/* 301 */           String depid = (String)((List)results.get(psnid)).get(0);
/* 302 */           depSet.add(depid);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 307 */     if (depSet.size() > 0) {
/* 308 */       Map<String, String> vidMap = DeptPubService.getLastVIDSByDeptIDS((String[])depSet.toArray(new String[0]));
/*     */       
/* 310 */       for (AggCtSaleVO aggvo : vos) {
/* 311 */         CtSaleVO headVo = aggvo.getParentVO();
/* 312 */         String psnid = headVo.getPersonnelid();
/* 313 */         if (psnid != null)
/*     */         {
/*     */ 
/* 316 */           String depid = (String)((List)results.get(psnid)).get(0);
/*     */           
/* 318 */           headVo.setDepid(depid);
/* 319 */           headVo.setDepid_v((String)vidMap.get(depid));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void setTransType(AggCtSaleVO[] vos) {
/* 326 */     String[] vtrantypecodes = prepareTranType(vos);
/* 327 */     if (ArrayUtils.isEmpty(vtrantypecodes)) {
/* 328 */       return;
/*     */     }
/*     */     
/* 331 */     Map<String, String> trantypeids = PfServiceScmUtil.getTrantypeidByCode(vtrantypecodes);
/*     */     
/*     */ 
/* 334 */     Map<String, BusinessSetVO> businessVOMap = getBusinessSetVOs((String[])trantypeids.values().toArray(new String[0]));
/*     */     
/* 336 */     if ((ValueUtil.isEmpty(trantypeids)) || (null == businessVOMap) || (businessVOMap.size() == 0))
/*     */     {
/* 338 */       return;
/*     */     }
/*     */     
/* 341 */     for (AggCtSaleVO aggvo : vos) {
/* 342 */       CtSaleVO head = aggvo.getParentVO();
/* 343 */       String ctrantypeid = (String)trantypeids.get(head.getVtrantypecode());
/* 344 */       BusinessSetVO businessvo = (BusinessSetVO)businessVOMap.get(ctrantypeid);
/*     */       
/* 346 */       head.setCtrantypeid(ctrantypeid);
/*     */       
/* 348 */       head.setNinvctlstyle(businessvo.getNinvctlstyle());
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\HYJG-MJ\MJ-NC-JNRL\Rlnchome\modules\ct\lib\pubct_bill.jar
 * Qualified Name:     nc.vo.ct.vochange.SaleChangeVOAdjust4310ToZ3
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.0.1
 */