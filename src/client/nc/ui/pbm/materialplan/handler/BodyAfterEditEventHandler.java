/*     */ package nc.ui.pbm.materialplan.handler;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;

/*     */ import org.apache.commons.lang.StringUtils;

/*     */ import nc.bs.framework.common.NCLocator;
/*     */ import nc.itf.pbm.materialstock.pub.IMaterialStockCommonService;
/*     */ import nc.itf.pbm.materialstock.pub.MaterialPlanForSupplierVO;
/*     */ import nc.pubitf.org.IOrgUnitPubService;
/*     */ import nc.ui.pm.handler.PMDefaultCardBodyafterHandler;
/*     */ import nc.ui.pm.util.BillCardPanelUtil;
/*     */ import nc.ui.pub.bill.BillCardPanel;
/*     */ import nc.ui.pubapp.uif2app.event.IAppEventHandler;
/*     */ import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
/*     */ import nc.ui.pubapp.uif2app.model.BillManageModel;
/*     */ import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
/*     */ import nc.ui.pubapp.uif2app.view.util.RefMoreSelectedUtils;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.taxcode.TaxrateVO;
/*     */ import nc.vo.ml.NCLangRes4VoTransl;
/*     */ import nc.vo.pm.util.ArrayUtil;
/*     */ import nc.vo.pm.util.StringUtil;
/*     */ import nc.vo.pm.util.UFDoubleUtils;
/*     */ import nc.vo.pub.BusinessException;
/*     */ import nc.vo.pub.lang.UFDouble;
/*     */ import nc.vo.pubapp.pattern.exception.ExceptionUtils;
/*     */ import nc.vo.pubapp.pattern.log.Log;
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
/*     */ public class BodyAfterEditEventHandler
/*     */   extends PMDefaultCardBodyafterHandler
/*     */   implements IAppEventHandler<CardBodyAfterEditEvent>
/*     */ {
/*     */   private ShowUpableBillForm cardForm;
/*     */   
/*     */   public void handleAppEvent(CardBodyAfterEditEvent e) {
/*  62 */     BillCardPanel billCardPanel = this.cardForm.getBillCardPanel();
/*  63 */     if ("pk_cbs_node".equals(e.getKey())) {
/*     */       
/*     */       try {
/*  66 */         cbsAfterEdit(e);
/*     */       }
/*  68 */       catch (BusinessException e1) {
/*  69 */         Log.error(e1);
/*  70 */         ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("projectmaterial_0", "04815004-0027"));
/*     */ 
/*     */       
/*     */       }
/*     */ 
/*     */ 
/*     */     
/*     */     }
/*  78 */     else if ("pk_material_v".equals(e.getKey())) {
/*     */       
/*     */       try {
/*  81 */         materialAfterEdit(e);
/*     */       
/*     */       }
/*  84 */       catch (BusinessException e1) {
/*  85 */         ExceptionUtils.wrappBusinessException(e1.getMessage());
/*     */       }
/*     */     
/*  88 */     } else if ("mater_unit_price".equals(e.getKey())) {
/*     */       
/*  90 */       UFDouble mater_unit_price = UFDoubleUtils.getNullValue((UFDouble)billCardPanel.getBillModel().getValueAt(e.getRow(), "mater_unit_price"));
/*     */ 
/*     */       
/*  93 */       if (UFDoubleUtils.isEqual(UFDouble.ZERO_DBL, mater_unit_price)) {
/*  94 */         BillCardPanelUtil.setBodyValue(this.cardForm.getBillCardPanel(), e.getRow(), "mater_unit_price", null);
/*     */       
/*     */       }
/*     */     }
/*  98 */     else if ("fix_unit_price".equals(e.getKey())) {
/*     */       
/* 100 */       UFDouble fix_unit_price = UFDoubleUtils.getNullValue((UFDouble)billCardPanel.getBillModel().getValueAt(e.getRow(), "fix_unit_price"));
/*     */ 
/*     */       
/* 103 */       if (UFDoubleUtils.isEqual(UFDouble.ZERO_DBL, fix_unit_price)) {
/* 104 */         BillCardPanelUtil.setBodyValue(this.cardForm.getBillCardPanel(), e.getRow(), "fix_unit_price", null);
/*     */       
/*     */       }
/*     */     }
/* 108 */     else if ("other_unit_price".equals(e.getKey())) {
/*     */       
/* 110 */       UFDouble other_unit_price = UFDoubleUtils.getNullValue((UFDouble)billCardPanel.getBillModel().getValueAt(e.getRow(), "other_unit_price"));
/*     */ 
/*     */       
/* 113 */       if (UFDoubleUtils.isEqual(UFDouble.ZERO_DBL, other_unit_price)) {
/* 114 */         BillCardPanelUtil.setBodyValue(this.cardForm.getBillCardPanel(), e.getRow(), "other_unit_price", null);
/*     */       
/*     */       }
/*     */     }
/* 118 */     else if ("pk_stockorg_v".equals(e.getKey())) {
/*     */       
/*     */       try {
/* 121 */         setDefaultSupplier(new int[] { e.getRow() });
/*     */ 
/*     */       
/*     */       }
/* 125 */       catch (BusinessException e1) {
/* 126 */         Log.error(e1);
/* 127 */         ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("projectmaterial_0", "04815004-0027"));
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
/*     */ 
/*     */ 
/*     */   
/*     */   private void cbsAfterEdit(CardBodyAfterEditEvent e) throws BusinessException {
/* 145 */     BillCardPanel billCardPanel = this.cardForm.getBillCardPanel();
/* 146 */     String pk_material_v = (String)BillCardPanelUtil.getBodyValue(billCardPanel, "pk_material_v", e.getRow());
/*     */ 
/*     */ 
/*     */     
/* 150 */     RefMoreSelectedUtils util = new RefMoreSelectedUtils(billCardPanel);
/*     */     
/* 152 */     int[] affectRows = util.refMoreSelected(e.getRow(), "pk_cbs_node", Boolean.TRUE.booleanValue());
/*     */ 
/*     */ 
/*     */     
/* 156 */     if (!ArrayUtil.isEmpty(affectRows)) {
/*     */       
/* 158 */       setRefPk(affectRows, pk_material_v, billCardPanel, "pk_material_v");
/*     */ 
/*     */       
/* 161 */       setDefaultStockOrg(affectRows);
/*     */       
/* 163 */       setDefaultSupplier(affectRows);
/*     */       
/* 165 */       setDefaultPray(affectRows);
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
/*     */   private void materialAfterEdit(CardBodyAfterEditEvent e) throws BusinessException {
/* 178 */     BillCardPanel billCardPanel = this.cardForm.getBillCardPanel();
/* 179 */     String pk_cbs = (String)BillCardPanelUtil.getBodyValue(billCardPanel, "pk_cbs_node", e.getRow());
/*     */ 
/*     */ 
/*     */     
/* 183 */     RefMoreSelectedUtils util = new RefMoreSelectedUtils(this.cardForm.getBillCardPanel());
/*     */ 
/*     */     
/* 186 */     int[] affectRows = util.refMoreSelected(e.getRow(), "pk_material_v", Boolean.TRUE.booleanValue());
/*     */ 
/*     */     
/* 189 */     if (!ArrayUtil.isEmpty(affectRows)) {
/*     */ 
/*     */       
/* 192 */       for (int num : affectRows) {
/* 193 */         this.cardForm.getBillCardPanel().getBillModel().setValueAt(Integer.valueOf(1), num, "service_prc_ratio");
/*     */         
/* 195 */         billCardPanel.getBillModel().loadEditRelationItemValue(num, "pk_measdoc");
/*     */       } 
/*     */ 
/*     */       
/* 199 */       setRefPk(affectRows, pk_cbs, billCardPanel, "pk_cbs_node");
/*     */ 
/*     */       
/* 202 */       setDefaultStockOrg(affectRows);
/*     */       
/* 204 */       setDefaultSupplier(affectRows);
/*     */       
/* 206 */       setDefaultPray(affectRows);
/*     */       
/* 208 */       setNums(affectRows);

				setDefaultTax(affectRows);
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
/*     */   private void setRefPk(int[] affectRows, String pk_cbs, BillCardPanel billCardPanel, String cloName) {
/* 221 */     for (int i = 0; i < affectRows.length; i++) {
/* 222 */       BillCardPanelUtil.setBodyValue(billCardPanel, affectRows[i], cloName, pk_cbs, "bodyvos");
/*     */       
/* 224 */       billCardPanel.getBillModel().loadLoadRelationItemValue(affectRows[i], cloName);
/*     */       
/* 226 */       billCardPanel.getBillModel().loadEditRelationItemValue(affectRows[i], cloName);
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
/*     */   private void setNums(int[] affectRows) {
/* 239 */     BillCardPanel billCardPanel = this.cardForm.getBillCardPanel();
/* 240 */     for (int row : affectRows) {
/* 241 */       BillCardPanelUtil.setBodyValue(billCardPanel, row, "nnum", billCardPanel.getBillModel().getValueAt(row, "nnum"));
/*     */     }
/*     */   }

			private void setDefaultTax(int[] affectRows) {
/* 239 */     BillCardPanel panel = this.cardForm.getBillCardPanel();
/* 240 */     
				String pk_org_v = panel.getHeadItem("pk_org_v").getValueObject().toString();
				try {
					String def7 = (String) HYPubBO_Client.findColValue("org_orgs", "def7", "nvl(dr,0) = 0 and pk_vid='" + pk_org_v + "'");
					if(null == def7 || !def7.equals("1")) {
						return;
					}
				} catch (UifException e2) {
					e2.printStackTrace();
				}
			for (int row : affectRows) {
				String pk_material_v = panel.getBodyValueAt(row, "pk_material_v").toString();
				try {
					String pk_mattaxes = (String) HYPubBO_Client.findColValue("bd_material", "pk_mattaxes", "nvl(dr,0) = 0 and pk_material='"
							+ pk_material_v + "'");
					String pk_taxcode = (String) HYPubBO_Client.findColValue("bd_taxcode", "pk_taxcode", "nvl(dr,0) = 0 and mattaxes='"
							+ pk_mattaxes + "'");
					panel.setBodyValueAt(pk_taxcode, row, "bdef6");
					TaxrateVO[] taxrateVO = (TaxrateVO[]) HYPubBO_Client.queryByCondition(TaxrateVO.class, "nvl(dr,0) = 0 and pk_taxcode='"+pk_taxcode+"'");
					if(null != taxrateVO && taxrateVO.length >= 1) {
						UFDouble taxrate = taxrateVO[0].getTaxrate();
						panel.setBodyValueAt(taxrate, row, "bdef7");
					}
					
				} catch (UifException e1) {
					e1.printStackTrace();
				}
///* 241 */       BillCardPanelUtil.setBodyValue(billCardPanel, row, "nnum", billCardPanel.getBillModel().getValueAt(row, "nnum"));
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
/*     */   private void setDefaultStockOrg(int[] affectRows) throws BusinessException {
/* 260 */     BillCardPanel billCardPanel = this.cardForm.getBillCardPanel();
/*     */ 
/*     */     
/* 263 */     String pk_org = (String)BillCardPanelUtil.getHeadItemValue(billCardPanel, "pk_org");
/*     */ 
/*     */ 
/*     */     
/* 267 */     String pk_projectclass = (String)BillCardPanelUtil.getHeadItemValue(billCardPanel, "pk_project.pk_projectclass");
/*     */ 
/*     */     
/* 270 */     billCardPanel.getBillModel().loadLoadRelationItemValue(affectRows[0], "pk_material");
/*     */ 
/*     */     
/* 273 */     Map<String, String> marClStockOrgMap = new HashMap<String, String>();
/* 274 */     for (int row : affectRows) {
/* 275 */       billCardPanel.getBillModel().setCellEditable(row, "pk_stockorg_v", true);
/* 276 */       billCardPanel.getBillModel().setValueAt(null, row, "pk_stockorg_v");
/* 277 */       billCardPanel.getBillModel().setCellEditable(row, "require_date", true);
/* 278 */       billCardPanel.getBillModel().setValueAt(null, row, "require_date");
/*     */       
/* 280 */       billCardPanel.getBillModel().setCellEditable(row, "pray_param", true);
/* 281 */       billCardPanel.getBillModel().setValueAt(null, row, "pray_param");
/*     */       
/* 283 */       String pk_marClass = (String)billCardPanel.getBillModel().getValueAt(row, "pk_material_v.pk_marbasclass_ID");
/*     */ 
/*     */       
/* 286 */       marClStockOrgMap.put(pk_marClass, pk_marClass);
/*     */     } 
/* 288 */     for (String marClass : marClStockOrgMap.keySet()) {
/*     */       
/* 290 */       String stockOrg = getIOrgUnitPubService().getDefaultStockOrgFromItemStockRelation(pk_org, pk_projectclass, marClass);
/*     */ 
/*     */       
/* 293 */       marClStockOrgMap.put(marClass, stockOrg);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 303 */     for (int num : affectRows) {
/* 304 */       Boolean fee = (Boolean)billCardPanel.getBillModel().getValueAt(num, "pk_material_v.fee");
/* 305 */       billCardPanel.getBillModel().setCellEditable(num, "service_prc_ratio", true);
/*     */       
/* 307 */       if (fee != null && fee.booleanValue() != true) {
/*     */         
/* 309 */         UFDouble mater_price = UFDoubleUtils.multiply((UFDouble)billCardPanel.getBillModel().getValueAt(num, "nnum"), (UFDouble)billCardPanel.getBillModel().getValueAt(num, "mater_unit_price"));
/*     */ 
/*     */         
/* 312 */         BillCardPanelUtil.setBodyValue(billCardPanel, num, "mater_price", mater_price);
/*     */ 
/*     */         
/* 315 */         String pk_marClass = (String)billCardPanel.getBillModel().getValueAt(num, "pk_material_v.pk_marbasclass_ID");
/*     */ 
/*     */         
/* 318 */         BillCardPanelUtil.setBodyValue(billCardPanel, num, "pk_stockorg", marClStockOrgMap.get(pk_marClass));
/*     */         
/* 320 */         billCardPanel.getBillModel().loadEditRelationItemValue(num, "pk_stockorg");
/*     */ 
/*     */         
/* 323 */         billCardPanel.getBillModel().setValueAt(null, num, "service_prc_ratio");
/*     */         
/* 325 */         billCardPanel.getBillModel().setCellEditable(num, "service_prc_ratio", false);
/*     */       }
/* 327 */       else if (fee != null && fee.booleanValue() == true) {
/*     */         
/* 329 */         billCardPanel.getBillModel().setCellEditable(num, "oppo_material", true);
/*     */         
/* 331 */         UFDouble mater_price = UFDoubleUtils.multiply(new UFDouble[] { (UFDouble)billCardPanel.getBillModel().getValueAt(num, "nnum"), (UFDouble)billCardPanel.getBillModel().getValueAt(num, "mater_unit_price"), (UFDouble)billCardPanel.getBillModel().getValueAt(num, "service_prc_ratio") });
/*     */ 
/*     */ 
/*     */         
/* 335 */         BillCardPanelUtil.setBodyValue(billCardPanel, num, "mater_price", mater_price);
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
/*     */   private void setDefaultSupplier(int[] affectRows) throws BusinessException {
/* 349 */     BillCardPanel billCardPanel = this.cardForm.getBillCardPanel();
/*     */     
/* 351 */     MaterialPlanForSupplierVO[] paramVOs = new MaterialPlanForSupplierVO[affectRows.length];
/*     */ 
/*     */     
/* 354 */     for (int i = 0; i < affectRows.length; i++) {
/* 355 */       MaterialPlanForSupplierVO tempVO = new MaterialPlanForSupplierVO();
/*     */       
/* 357 */       tempVO.setPk_stockOrg((String)BillCardPanelUtil.getBodyValue(billCardPanel, "pk_stockorg", affectRows[i]));
/*     */ 
/*     */       
/* 360 */       tempVO.setPk_material_o((String)BillCardPanelUtil.getBodyValue(billCardPanel, "pk_material", affectRows[i]));
/*     */ 
/*     */       
/* 363 */       tempVO.setPk_material_v((String)BillCardPanelUtil.getBodyValue(billCardPanel, "pk_material_v", affectRows[i]));
/*     */ 
/*     */       
/* 366 */       tempVO.setPk_org((String)BillCardPanelUtil.getHeadItemValue(billCardPanel, "pk_org"));
/*     */ 
/*     */       
/* 369 */       tempVO.setPk_group((String)BillCardPanelUtil.getHeadItemValue(billCardPanel, "pk_group"));
/*     */ 
/*     */       
/* 372 */       paramVOs[i] = tempVO;
/*     */     } 
/* 374 */     Map<String, String> pk_supplier = getStockCommonService().getDefaultSupplier(paramVOs);
/*     */     
/* 376 */     for (int i = 0; i < affectRows.length; i++) {
/* 377 */       if (StringUtil.isNotEmpty((String)pk_supplier.get(paramVOs[i].getPk_material_v() + paramVOs[i].getPk_stockOrg())))
/*     */       {
/* 379 */         BillCardPanelUtil.setBodyValue(billCardPanel, affectRows[i], "pk_supplier", pk_supplier.get(paramVOs[i].getPk_material_v() + paramVOs[i].getPk_stockOrg()));
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
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDefaultPray(int[] affectRows) {
/* 397 */     BillCardPanel billCardPanel = this.cardForm.getBillCardPanel();
/*     */     
/* 399 */     List<String> projectsList = new ArrayList<String>();
/*     */     
/* 401 */     List<String> materialList = new ArrayList<String>();
/*     */     
/* 403 */     List<Integer> affectRowsList = new ArrayList<Integer>();
/*     */ 
/*     */     
/* 406 */     for (int i = 0; i < affectRows.length; i++) {
/*     */       
/* 408 */       String pk_material = (String)billCardPanel.getBillModel().getValueAt(affectRows[i], "pk_material_ID");
/*     */ 
/*     */       
/* 411 */       String pk_project = (String)BillCardPanelUtil.getHeadValue(billCardPanel, "pk_project");
/*     */ 
/*     */ 
/*     */       
/* 415 */       if (StringUtils.isNotBlank(pk_material)) {
/* 416 */         projectsList.add(pk_project);
/* 417 */         materialList.add(pk_material);
/* 418 */         affectRowsList.add(Integer.valueOf(affectRows[i]));
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
/*     */ 
/*     */   
/* 434 */   public ShowUpableBillForm getCardForm() { return this.cardForm; }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 443 */   public BillManageModel getModel(ShowUpableBillForm billForm) { return (BillManageModel)billForm.getModel(); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 452 */   public void setCardForm(ShowUpableBillForm cardForm) { this.cardForm = cardForm; }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 461 */   private IMaterialStockCommonService getStockCommonService() { return (IMaterialStockCommonService)NCLocator.getInstance().lookup(IMaterialStockCommonService.class); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 470 */   private IOrgUnitPubService getIOrgUnitPubService() { return (IOrgUnitPubService)NCLocator.getInstance().lookup(IOrgUnitPubService.class); }
/*     */ }


/* Location:              E:\NC\nchome\modules\pmr\client\li\\uipmr_projectmaterial.jar!/nc/ui/pbm/materialplan/handler/BodyAfterEditEventHandler.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.0.7
 */