/*     */ package nc.vo.pu.m21.rule.api.fill;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import nc.bs.framework.common.NCLocator;
/*     */ import nc.itf.ftpub.reference.uap.bd.DeptPubUtilService;
/*     */ import nc.itf.scmpub.reference.uap.bd.supplier.SupplierPubService;
/*     */ import nc.itf.scmpub.reference.uap.org.FinanceOrgPubService;
/*     */ import nc.itf.scmpub.reference.uap.org.OrgUnitPubService;
/*     */ import nc.itf.scmpub.reference.uap.org.StockOrgPubService;
/*     */ import nc.pubitf.pu.m21transtype.IPoTransTypeQuery;
/*     */ import nc.pubitf.uapbd.CurrencyRateUtilHelper;
/*     */ import nc.vo.ic.material.define.InvBasVO;
/*     */ import nc.vo.ic.material.query.InvInfoQuery;
/*     */ import nc.vo.pu.m21.entity.OrderHeaderVO;
/*     */ import nc.vo.pu.m21.entity.OrderItemVO;
/*     */ import nc.vo.pu.m21.entity.OrderVO;
/*     */ import nc.vo.pu.m21.query.supplier.SupplierInfo;
/*     */ import nc.vo.pu.m21.rule.CurrencyAndExchangerate;
/*     */ import nc.vo.pu.m21.rule.OrganizationDefaultValue;
/*     */ import nc.vo.pu.m21.rule.SupplierDefaultInfo;
/*     */ import nc.vo.pu.m21.rule.SupplierDefaultValue;
/*     */ import nc.vo.pu.m21.rule.api.Calculate;
/*     */ import nc.vo.pu.m21.rule.vat.OrderVatValueFillRule;
/*     */ import nc.vo.pu.m21transtype.entity.PoTransTypeVO;
/*     */ import nc.vo.pu.pub.enumeration.EnumDiscounttaxtype;
/*     */ import nc.vo.pu.pub.enumeration.PricePriority;
/*     */ import nc.vo.pu.pub.rule.SetPeptRule;
/*     */ import nc.vo.pu.pub.util.AggVOHelper;
/*     */ import nc.vo.pu.pub.util.BillHelper;
/*     */ import nc.vo.pu.pub.util.PUSysParamUtil;
/*     */ import nc.vo.pu.pub.util.SupplierInfoUtil;
/*     */ import nc.vo.pub.BusinessException;
/*     */ import nc.vo.pub.lang.UFDate;
/*     */ import nc.vo.pub.lang.UFDouble;
/*     */ import nc.vo.pubapp.AppContext;
/*     */ import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
/*     */ import nc.vo.scmpub.fill.billfill.IBillValueFill;
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
/*     */ public class FillDefaultValueRule
/*     */   implements IBillValueFill
/*     */ {
/*     */   public AbstractBill[] fillValue(AbstractBill[] billVOs) throws BusinessException {
/*  65 */     OrderVO[] vos = (OrderVO[])billVOs;
/*  66 */     Set<String> materials = new HashSet<String>();
/*  67 */     Set<String> pk_depts_v = new HashSet<String>();
/*  68 */     Map<String, List<OrderVO>> map = new HashMap<String, List<OrderVO>>();
/*     */     
/*  70 */     fillSupplierInfo(vos);
/*     */     
/*  72 */     Set<String> orgs = new HashSet<String>();
/*     */     
/*  74 */     for (OrderVO vo : vos) {
/*  75 */       OrderHeaderVO hvo = vo.getHVO();
/*  76 */       hvo.setStatus(2);
/*  77 */       String pk_dept_v = hvo.getPk_dept_v();
/*  78 */       if (pk_dept_v != null) {
/*  79 */         pk_depts_v.add(pk_dept_v);
/*     */       }
/*  81 */       String vtrantypecode = hvo.getVtrantypecode();
/*  82 */       if (vtrantypecode != null) {
/*  83 */         List<OrderVO> list = (List)map.get(vtrantypecode);
/*  84 */         if (list == null) {
/*  85 */           list = new ArrayList<OrderVO>();
/*  86 */           map.put(vtrantypecode, list);
/*     */         } 
/*  88 */         list.add(vo);
/*     */       } 
/*  90 */       Integer fhtaxtypeflag = hvo.getFhtaxtypeflag();
/*  91 */       if (fhtaxtypeflag == null)
/*     */       {
/*  93 */         hvo.setFhtaxtypeflag(EnumDiscounttaxtype.TAXOUT.toInteger());
/*     */       }
/*  95 */       BillHelper billHelper = new BillHelper(vo);
/*  96 */       String pk_org = hvo.getPk_org();
/*     */       
/*  98 */       (new SetPeptRule(billHelper, "cemployeeid", "pk_dept", "pk_dept_v", AppContext.getInstance().getPkUser(), pk_org)).setPsnAndDept();
/*     */ 
/*     */       
/* 101 */       UFDate dbilldate = hvo.getDbilldate();
/* 102 */       if (dbilldate == null) {
/* 103 */         hvo.setDbilldate(AppContext.getInstance().getBusiDate());
/*     */       }
/*     */       
/* 106 */       OrganizationDefaultValue odv = new OrganizationDefaultValue(billHelper);
/* 107 */       odv.setClear(Boolean.FALSE.booleanValue());
/* 108 */       OrderItemVO[] bvo = vo.getBVO();
/* 109 */       int[] rows = new int[bvo.length];
/* 110 */       for (int i = 0; i < rows.length; i++) {
/* 111 */         rows[i] = i;
/*     */       }
/* 113 */       odv.setDefaultOrganizationValue(rows);
/*     */       
/* 115 */       for (OrderItemVO item : bvo) {
/* 116 */         item.setStatus(2);
/* 117 */         String pk_material = item.getPk_material();
/* 118 */         materials.add(pk_material);
/* 119 */         String cqtunitid = item.getCqtunitid();
/* 120 */         if (cqtunitid == null) {
/* 121 */           item.setCqtunitid(item.getCastunitid());
/*     */         }
/* 123 */         String vqtunitrate = item.getVqtunitrate();
/* 124 */         if (vqtunitrate == null) {
/* 125 */           item.setVqtunitrate(item.getVchangerate());
/*     */         }
/* 127 */         UFDouble nqtunitnum = item.getNqtunitnum();
/* 128 */         if (nqtunitnum == null) {
/* 129 */           item.setNqtunitnum(item.getNastnum());
/*     */         }
/* 131 */         String pk_apfinanceorg = item.getPk_apfinanceorg();
/* 132 */         String pk_apfinanceorg_v = item.getPk_apfinanceorg_v();
/* 133 */         if (pk_apfinanceorg == null && pk_apfinanceorg_v != null) {
/* 134 */           orgs.add(pk_apfinanceorg_v);
/*     */         }
/* 136 */         String pk_arrvstoorg_v = item.getPk_arrvstoorg_v();
/* 137 */         String pk_arrvstoorg = item.getPk_arrvstoorg();
/* 138 */         if (pk_arrvstoorg == null && pk_arrvstoorg_v != null) {
/* 139 */           orgs.add(pk_arrvstoorg_v);
/*     */         }
/* 141 */         String pk_psfinanceorg = item.getPk_psfinanceorg();
/* 142 */         String pk_psfinanceorg_v = item.getPk_psfinanceorg_v();
/* 143 */         if (pk_psfinanceorg == null && pk_psfinanceorg_v != null) {
/* 144 */           orgs.add(pk_psfinanceorg_v);
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 149 */       SupplierDefaultInfo supRule = new SupplierDefaultInfo(billHelper, rows);
/* 150 */       supRule.prepare();
/*     */     } 
/*     */     
/* 153 */     setOrgOidByVid(vos, orgs);
/* 154 */     Map<String, String> deptIDS_V = DeptPubUtilService.getDeptOidByVid((String[])pk_depts_v.toArray(new String[0]));
/*     */ 
/*     */     
/* 157 */     setMaterialInfo(vos, materials);
/*     */     
/* 159 */     setCurrInfo(vos);
/*     */     
/* 161 */     setVatInfo(vos);
/*     */     
/* 163 */     processReceivePlan(map);
/*     */     
/* 165 */     if (deptIDS_V != null) {
/* 166 */       for (OrderVO vo : vos) {
/* 167 */         OrderHeaderVO hvo = vo.getHVO();
/* 168 */         if (hvo.getPk_dept() == null) {
/* 169 */           hvo.setPk_dept((String)deptIDS_V.get(hvo.getPk_dept_v()));
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/* 174 */     calculate(vos);
/* 175 */     return vos;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setOrgOidByVid(OrderVO[] vos, Set<String> orgs_v) {
/* 186 */     if (orgs_v == null || orgs_v.isEmpty()) {
/*     */       return;
/*     */     }
/* 189 */     Map<String, String> orgVids_v = OrgUnitPubService.getOrgIDSByVIDS((String[])orgs_v.toArray(new String[orgs_v.size()]));
/*     */     
/* 191 */     for (OrderVO vo : vos) {
/* 192 */       for (OrderItemVO item : vo.getBVO()) {
/* 193 */         if (item.getPk_apfinanceorg() == null) {
/* 194 */           item.setPk_apfinanceorg((String)orgVids_v.get(item.getPk_apfinanceorg_v()));
/*     */         }
/* 196 */         if (item.getPk_arrvstoorg() == null) {
/* 197 */           item.setPk_arrvstoorg((String)orgVids_v.get(item.getPk_arrvstoorg_v()));
/*     */         }
/* 199 */         if (item.getPk_psfinanceorg() == null) {
/* 200 */           item.setPk_psfinanceorg((String)orgVids_v.get(item.getPk_psfinanceorg_v()));
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void calculate(OrderVO[] vos) {
/* 213 */     for (OrderVO vo : vos) {
/* 214 */       OrderHeaderVO hvo = vo.getHVO();
/* 215 */       String pk_org = hvo.getPk_org();
/* 216 */       boolean taxPricePriorToPrice = isTaxPricePriorToPrice(pk_org);
/* 217 */       Calculate calculate = new Calculate();
/* 218 */       if (taxPricePriorToPrice) {
/* 219 */         calculate.calculate(new AggVOHelper(vo), vo, taxPricePriorToPrice);
/*     */       } else {
/*     */         
/* 222 */         calculate.calculate(new AggVOHelper(vo), vo, taxPricePriorToPrice);
/*     */       } 
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
/*     */   private void setMaterialInfo(OrderVO[] vos, Set<String> materials) {
/* 237 */     InvInfoQuery query = new InvInfoQuery();
/* 238 */     Map<String, InvBasVO> basVOs = query.getInvBasVOs((String[])materials.toArray(new String[0]));
/*     */     
/* 240 */     if (basVOs == null) {
/*     */       return;
/*     */     }
/*     */     
/* 244 */     for (OrderVO vo : vos) {
/* 245 */       for (OrderItemVO item : vo.getBVO()) {
/* 246 */         if (item.getPk_srcmaterial() == null) {
/* 247 */           item.setPk_srcmaterial(((InvBasVO)basVOs.get(item.getPk_material())).getPk_source());
/*     */         }
/*     */       } 
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
/*     */   private void setCurrInfo(OrderVO[] vos) {
/* 262 */     Map<String, List<OrderVO>> map = new HashMap<String, List<OrderVO>>();
/* 263 */     for (OrderVO vo : vos) {
/* 264 */       String pk_group = vo.getHVO().getPk_group();
/* 265 */       for (OrderItemVO item : vo.getBVO()) {
/* 266 */         item.setPk_group(pk_group);
/* 267 */         String pk_psfinanceorg = item.getPk_psfinanceorg();
/* 268 */         List<OrderVO> list = (List)map.get(pk_psfinanceorg);
/* 269 */         if (list == null) {
/* 270 */           list = new ArrayList<OrderVO>();
/* 271 */           map.put(pk_psfinanceorg, list);
/*     */         } 
/* 273 */         list.add(vo);
/*     */       } 
/*     */     } 
/*     */     
/* 277 */     for (Map.Entry<String, List<OrderVO>> entry : map.entrySet()) {
/* 278 */       String currtypeByOrgID = CurrencyRateUtilHelper.getInstance().getLocalCurrtypeByOrgID((String)entry.getKey());
/*     */       
/* 280 */       List<OrderVO> list = (List)entry.getValue();
/* 281 */       for (OrderVO vo : list) {
/* 282 */         for (OrderItemVO item : vo.getBVO()) {
/* 283 */           item.setCcurrencyid(currtypeByOrgID);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setVatInfo(OrderVO[] vos) {
/* 296 */     Set<String> psfinanceorgs = new HashSet<String>();
/* 297 */     Set<String> arrvstoorgs = new HashSet<String>();
/* 298 */     Set<String> suppliers = new HashSet<String>();
/* 299 */     for (OrderVO vo : vos) {
/* 300 */       for (OrderItemVO item : vo.getBVO()) {
/* 301 */         String psfinanceorg = item.getPk_psfinanceorg();
/* 302 */         psfinanceorgs.add(psfinanceorg);
/* 303 */         String arrvstoorg = item.getPk_arrvstoorg();
/* 304 */         arrvstoorgs.add(arrvstoorg);
/*     */       } 
/* 306 */       suppliers.add(vo.getHVO().getPk_supplier());
/*     */     } 
/* 308 */     Map<String, String> taxcountrys = FinanceOrgPubService.queryCountryByFinanceOrg((String[])psfinanceorgs.toArray(new String[0]));
/*     */     
/* 310 */     Map<String, String> countryByStockOrg = StockOrgPubService.queryCountryByStockOrg((String[])arrvstoorgs.toArray(new String[0]));
/*     */     
/* 312 */     Map<String, String> countrys = SupplierPubService.queryCountryBySupplier((String[])suppliers.toArray(new String[0]));
/*     */     
/* 314 */     Map<String, String> financeOrgIDByStockOrgID = StockOrgPubService.queryFinanceOrgIDByStockOrgID((String[])arrvstoorgs.toArray(new String[0]));
/*     */     
/* 316 */     for (OrderVO vo : vos) {
/* 317 */       String pk_supplier = vo.getHVO().getPk_supplier();
/* 318 */       int[] rows = new int[vo.getBVO().length];
/* 319 */       for (int i = 0; i < rows.length; i++) {
/* 320 */         rows[i] = i;
/*     */       }
/* 322 */       for (OrderItemVO item : vo.getBVO()) {
/* 323 */         if (item.getCsendcountryid() == null) {
/* 324 */           item.setCsendcountryid((String)countrys.get(pk_supplier));
/*     */         }
/* 326 */         String stockorg = item.getPk_arrvstoorg();
/* 327 */         if (item.getCrececountryid() == null) {
/* 328 */           if (stockorg != null) {
/* 329 */             item.setCrececountryid((String)countryByStockOrg.get(stockorg));
/*     */           } else {
/* 331 */             item.setCrececountryid((String)taxcountrys.get(item.getPk_psfinanceorg()));
/*     */           } 
/*     */         }
/* 334 */         String psfinanceorg = item.getPk_psfinanceorg();
/* 335 */         String financeOrg = (String)financeOrgIDByStockOrgID.get(item.getPk_psfinanceorg());
/*     */         if(null == financeOrg) {
					financeOrg = "";
					}
/* 337 */         String taxcountry = financeOrg.equals(psfinanceorg) ? (String)countryByStockOrg.get(stockorg) : (String)taxcountrys.get(psfinanceorg);
/*     */         
/* 339 */         if (item.getCtaxcountryid() == null) {
/* 340 */           item.setCtaxcountryid(taxcountry);
/*     */         }
/*     */       } 
/* 343 */       AggVOHelper<OrderVO> voHelper = new AggVOHelper<OrderVO>(vo);
/* 344 */       OrderVatValueFillRule rule = new OrderVatValueFillRule(new AggVOHelper[] { voHelper });
/*     */       
/* 346 */       rule.prepare();
/* 347 */       rule.process();
/*     */ 
/*     */       
/* 350 */       CurrencyAndExchangerate exchangerate = new CurrencyAndExchangerate(voHelper);
/* 351 */       exchangerate.setCurrencyAndExchangeRate(rows);
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
/*     */   private void fillSupplierInfo(OrderVO[] list) {
/* 363 */     SupplierInfoUtil util = new SupplierInfoUtil();
/* 364 */     HashMap<String, SupplierInfo> supplierMap = new HashMap<String, SupplierInfo>();
/* 365 */     for (OrderVO vo : list) {
/* 366 */       OrderHeaderVO hvo = vo.getHVO();
/* 367 */       String supplier = hvo.getPk_supplier();
/* 368 */       BillHelper<OrderVO> helper = new BillHelper<OrderVO>(vo);
/* 369 */       SupplierInfo supplierInfo = (SupplierInfo)supplierMap.get(supplier);
/* 370 */       if (supplierInfo == null) {
/* 371 */         supplierInfo = util.getSupplierInfo(helper);
/* 372 */         supplierMap.put(supplier, supplierInfo);
/*     */       } 
/* 374 */       SupplierDefaultValue vendorDefaultValue = new SupplierDefaultValue(helper);
/* 375 */       vendorDefaultValue.setDefaultValueNotClear(supplierInfo);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isTaxPricePriorToPrice(String pk_org) {
/* 386 */     boolean flag = true;
/* 387 */     if (null == pk_org) {
/* 388 */       return flag;
/*     */     }
/* 390 */     PricePriority pricePriority = PUSysParamUtil.getPO28(pk_org);
/* 391 */     if (!PricePriority.TAXPRICE_PRIOR_TO_PRICE.equals(pricePriority)) {
/* 392 */       flag = false;
/*     */     }
/* 394 */     return flag;
/*     */   }
/*     */ 
/*     */   
/*     */   private void processReceivePlan(Map<String, List<OrderVO>> map) throws BusinessException {
/* 399 */     Set<String> transtypecodes = map.keySet();
/* 400 */     IPoTransTypeQuery query = (IPoTransTypeQuery)NCLocator.getInstance().lookup(IPoTransTypeQuery.class);
/*     */     
/* 402 */     Map<String, PoTransTypeVO> transtypeMap = query.queryAttrByTypes((String[])transtypecodes.toArray(new String[transtypecodes.size()]), new String[] { "breceiveplan" });
/*     */ 
/*     */     
/* 405 */     if (transtypeMap == null || transtypeMap.isEmpty()) {
/*     */       return;
/*     */     }
/* 408 */     for (Map.Entry<String, PoTransTypeVO> entry : transtypeMap.entrySet()) {
/* 409 */       String transtypecode = (String)entry.getKey();
/* 410 */       List<OrderVO> list = (List)map.get(transtypecode);
/* 411 */       PoTransTypeVO vo = (PoTransTypeVO)entry.getValue();
/* 412 */       if (vo == null || vo.getBreceiveplan().booleanValue());
/*     */       
/* 414 */       for (OrderVO order : list) {
/* 415 */         for (OrderItemVO item : order.getBVO()) {
/* 416 */           if (item.getPk_arrvstoorg() == null) {
/* 417 */             item.setPk_arrvstoorg(item.getPk_reqstoorg());
/*     */           }
/* 419 */           if (item.getPk_arrvstoorg_v() == null)
/* 420 */             item.setPk_arrvstoorg_v(item.getPk_reqstoorg_v()); 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              E:\NC\nchome\modules\pu\lib\pubpu_api.jar!/nc/vo/pu/m21/rule/api/fill/FillDefaultValueRule.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.0.7
 */