/*     */ package nc.ui.pbm.materialadd.handler;
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
/*     */ import nc.ui.pub.bill.BillModel;
/*     */ import nc.ui.pubapp.uif2app.event.IAppEventHandler;
/*     */ import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
/*     */ import nc.ui.pubapp.uif2app.model.BillManageModel;
/*     */ import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
/*     */ import nc.ui.pubapp.uif2app.view.util.RefMoreSelectedUtils;
/*     */ import nc.vo.ml.NCLangRes4VoTransl;
/*     */ import nc.vo.pbm.materialstock.MaterialStockVO;
/*     */ import nc.vo.pm.util.ArrayUtil;
/*     */ import nc.vo.pm.util.ExceptionUtils;
/*     */ import nc.vo.pm.util.ListUtil;
/*     */ import nc.vo.pm.util.StringUtil;
/*     */ import nc.vo.pm.util.UFDoubleUtils;
/*     */ import nc.vo.pub.BusinessException;
/*     */ import nc.vo.pub.lang.UFDouble;
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
/*     */ public class CardBodyAfterEditHandler
/*     */   extends PMDefaultCardBodyafterHandler
/*     */   implements IAppEventHandler<CardBodyAfterEditEvent>
/*     */ {
/*     */   private ShowUpableBillForm cardForm;
/*     */   
/*  62 */   public ShowUpableBillForm getCardForm() { return this.cardForm; }
/*     */ 
/*     */ 
/*     */   
/*  66 */   public BillManageModel getModel(ShowUpableBillForm billForm) { return (BillManageModel)billForm.getModel(); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleAppEvent(CardBodyAfterEditEvent e) {
/*  73 */     if ("nowadd_num".equals(e.getKey())) {
/*     */       
/*  75 */       CalAfterChangeAddNum(e);
/*     */     }
/*  77 */     else if ("service_prc_ratio".equals(e.getKey())) {
/*     */       
/*  79 */       CalAfterChangeAddNum(e);
/*     */     }
/*  81 */     else if ("pk_mater_plan_b".equals(e.getKey())) {
/*     */       
/*  83 */       handleColEvent(e, "pk_mater_plan_b");
/*     */     }
/*  85 */     else if ("pk_material_v".equals(e.getKey())) {
/*     */       
/*  87 */       handleColEvent(e, "pk_material_v");
/*     */     }
/*  89 */     else if ("pk_cbs_node".equals(e.getKey())) {
/*     */       
/*  91 */       handleColEvent(e, "pk_cbs_node");
/*     */     }
/*  93 */     else if ("mater_unit_price".equals(e.getKey()) || "fix_unit_price".equals(e.getKey()) || "other_unit_price".equals(e.getKey())) {
/*     */ 
/*     */ 
/*     */       
/*  97 */       CalAfterChangeAddNum(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void CalAfterChangeAddNum(CardBodyAfterEditEvent e) {
/* 108 */     BillCardPanel card = e.getBillCardPanel();
/* 109 */     BillModel billModel = card.getBillData().getBillModel();
/* 110 */     int row = e.getRow();
/* 111 */     calRowPrices(row, billModel);
/*     */   }
/*     */   
/*     */   private void calRowPrices(int row, BillModel billModel) {
/* 115 */     String PK_MATER_PLAN_B = (String)billModel.getValueAt(row, "pk_mater_plan_b");
/*     */ 
/*     */ 
/*     */     
/* 119 */     UFDouble nowAdd = UFDoubleUtils.getNullValue((UFDouble)billModel.getValueAt(row, "nowadd_num"));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 124 */     UFDouble service_prc_ratio = UFDoubleUtils.getNullValue((UFDouble)billModel.getValueAt(row, "service_prc_ratio"));
/*     */ 
/*     */ 
/*     */     
/* 128 */     boolean fee = true;
/* 129 */     if (billModel.getValueAt(row, "pk_material_v") != null && billModel.getValueAt(row, "pk_material_v.fee") != null)
/*     */     {
/* 131 */       fee = ((Boolean)billModel.getValueAt(row, "pk_material_v.fee")).booleanValue();
/*     */     }
/*     */     
/* 134 */     if (UFDoubleUtils.isNullOrZero(service_prc_ratio) && !fee) {
/* 135 */       service_prc_ratio = new UFDouble(1);
/*     */     }
/*     */ 
/*     */     
/* 139 */     UFDouble nnum = UFDoubleUtils.getNullValue((UFDouble)billModel.getValueAt(row, "nnum"));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 144 */     UFDouble otherUnitPrice = UFDoubleUtils.getNullValue((UFDouble)billModel.getValueAt(row, "other_unit_price"));
/*     */ 
/*     */     
/* 147 */     if (UFDoubleUtils.isEqual(UFDouble.ZERO_DBL, otherUnitPrice)) {
/* 148 */       BillCardPanelUtil.setBodyValue(this.cardForm.getBillCardPanel(), row, "other_unit_price", null);
/*     */     }
/*     */ 
/*     */     
/* 152 */     UFDouble materUnitPrice = UFDoubleUtils.getNullValue((UFDouble)billModel.getValueAt(row, "mater_unit_price"));
/*     */ 
/*     */     
/* 155 */     if (UFDoubleUtils.isEqual(UFDouble.ZERO_DBL, materUnitPrice)) {
/* 156 */       BillCardPanelUtil.setBodyValue(this.cardForm.getBillCardPanel(), row, "mater_unit_price", null);
/*     */     }
/*     */ 
/*     */     
/* 160 */     UFDouble fixUnitPrice = UFDoubleUtils.getNullValue((UFDouble)billModel.getValueAt(row, "fix_unit_price"));
/*     */ 
/*     */     
/* 163 */     if (UFDoubleUtils.isEqual(UFDouble.ZERO_DBL, fixUnitPrice)) {
/* 164 */       BillCardPanelUtil.setBodyValue(this.cardForm.getBillCardPanel(), row, "fix_unit_price", null);
/*     */     }
/*     */     
/* 167 */     UFDouble otherPrice = UFDoubleUtils.multiply(new UFDouble[] { nowAdd, otherUnitPrice, service_prc_ratio });
/* 168 */     UFDouble materPrice = UFDoubleUtils.multiply(new UFDouble[] { nowAdd, materUnitPrice, service_prc_ratio });
/* 169 */     UFDouble materPrice2 = UFDoubleUtils.multiply(new UFDouble[] { nnum, materUnitPrice, service_prc_ratio });
/* 170 */     UFDouble fixPrice = UFDoubleUtils.multiply(new UFDouble[] { nowAdd, fixUnitPrice, service_prc_ratio });
/*     */     
/* 172 */     if (StringUtil.isEmpty(PK_MATER_PLAN_B)) {
/*     */       
/* 174 */       billModel.setValueAt(otherPrice, row, "other_price");
/*     */       
/* 176 */       billModel.setValueAt(materPrice, row, "mater_price");
/*     */       
/* 178 */       billModel.setValueAt(fixPrice, row, "fix_price");
/*     */       
/* 180 */       billModel.setValueAt(otherPrice, row, "other_margin");
/*     */       
/* 182 */       billModel.setValueAt(materPrice, row, "mater_margin");
/*     */       
/* 184 */       billModel.setValueAt(fixPrice, row, "fix_margin");
/*     */     }
/*     */     else {
/*     */       
/* 188 */       billModel.setValueAt(materPrice2, row, "mater_price");
/*     */       
/* 190 */       billModel.setValueAt(UFDoubleUtils.multiply(new UFDouble[] { nowAdd, otherUnitPrice, service_prc_ratio }), row, "other_margin");
/*     */ 
/*     */       
/* 193 */       billModel.setValueAt(UFDoubleUtils.multiply(new UFDouble[] { nowAdd, materUnitPrice, service_prc_ratio }), row, "mater_margin");
/*     */ 
/*     */       
/* 196 */       billModel.setValueAt(UFDoubleUtils.multiply(new UFDouble[] { nowAdd, fixUnitPrice, service_prc_ratio }), row, "fix_margin");
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
/*     */   private void handleColEvent(CardBodyAfterEditEvent e, String colName) {
/* 211 */     BillCardPanel billCardPanel = this.cardForm.getBillCardPanel();
/* 212 */     String pk_wbs = (String)BillCardPanelUtil.getBodyValue(billCardPanel, "pk_cbs_node", e.getRow());
/*     */ 
/*     */     
/* 215 */     String pk_material_v = (String)BillCardPanelUtil.getBodyValue(billCardPanel, "pk_material_v", e.getRow());
/*     */ 
/*     */     
/* 218 */     RefMoreSelectedUtils utils = new RefMoreSelectedUtils(this.cardForm.getBillCardPanel());
/*     */ 
/*     */     
/* 221 */     int[] affectRows = utils.refMoreSelected(e.getRow(), colName, Boolean.TRUE.booleanValue());
/*     */     try {
/* 223 */       if (StringUtils.equals(colName, "pk_material_v")) {
/*     */         
/* 225 */         setRefPk(affectRows, pk_wbs, billCardPanel, "pk_cbs_node");
/*     */ 
/*     */         
/* 228 */         setDefaultStockOrg(affectRows);
/*     */         
/* 230 */         setDefaultSupplier(affectRows);
/*     */         
/* 232 */         setNums(affectRows);
/*     */         
/* 234 */         setPriceNull(affectRows);
/*     */         
/* 236 */         setService_prc_ratio(affectRows);
/*     */       }
/* 238 */       else if (StringUtils.equals(colName, "pk_cbs_node")) {
/*     */         
/* 240 */         setRefPk(affectRows, pk_material_v, billCardPanel, "pk_material_v");
/*     */       } 
/*     */ 
/*     */       
/* 244 */       setRelatedData(affectRows);
/*     */       
/* 246 */       if (StringUtils.equals(colName, "pk_mater_plan_b")) {
/* 247 */         setRowEdits(e, affectRows);
/*     */       }
/*     */       
/* 250 */       if ("pk_stockorg_v".equals(e.getKey())) {
/* 251 */         setDefaultSupplier(new int[] { e.getRow() });
/*     */ 
/*     */       
/*     */       }
/*     */     
/*     */     }
/* 257 */     catch (BusinessException bodyEditException) {
/* 258 */       Log.error(bodyEditException);
/* 259 */       nc.vo.pubapp.pattern.exception.ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("projectmaterial_0", "04815004-0015"));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setPriceNull(int[] affectRows) {
/* 270 */     BillCardPanel billCardPanel = this.cardForm.getBillCardPanel();
/* 271 */     for (int row : affectRows) {
/* 272 */       BillCardPanelUtil.setBodyValue(billCardPanel, row, "mater_unit_price", null);
/*     */ 
/*     */       
/* 275 */       BillCardPanelUtil.setBodyValue(billCardPanel, row, "nowadd_num", null);
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
/*     */   private void setDefaultSupplier(int[] affectRows) throws BusinessException {
/* 319 */     BillCardPanel billCardPanel = this.cardForm.getBillCardPanel();
/*     */     
/* 321 */     MaterialPlanForSupplierVO[] paramVOs = new MaterialPlanForSupplierVO[affectRows.length];
/*     */ 
/*     */     
/* 324 */     for (int i = 0; i < affectRows.length; i++) {
/* 325 */       MaterialPlanForSupplierVO tempVO = new MaterialPlanForSupplierVO();
/*     */       
/* 327 */       tempVO.setPk_stockOrg((String)BillCardPanelUtil.getBodyValue(billCardPanel, "pk_stockorg", i));
/*     */ 
/*     */       
/* 330 */       tempVO.setPk_material_o((String)BillCardPanelUtil.getBodyValue(billCardPanel, "pk_material", i));
/*     */ 
/*     */       
/* 333 */       tempVO.setPk_material_v((String)BillCardPanelUtil.getBodyValue(billCardPanel, "pk_material_v", i));
/*     */ 
/*     */       
/* 336 */       tempVO.setPk_org((String)BillCardPanelUtil.getHeadItemValue(billCardPanel, "pk_org"));
/*     */ 
/*     */       
/* 339 */       tempVO.setPk_group((String)BillCardPanelUtil.getHeadItemValue(billCardPanel, "pk_group"));
/*     */ 
/*     */       
/* 342 */       paramVOs[i] = tempVO;
/*     */     } 
/* 344 */     Map<String, String> pk_supplier = getStockCommonService().getDefaultSupplier(paramVOs);
/*     */     
/* 346 */     for (int i = 0; i < affectRows.length; i++) {
/* 347 */       if (StringUtil.isNotEmpty((String)pk_supplier.get(paramVOs[i].getPk_material_v() + paramVOs[i].getPk_stockOrg())))
/*     */       {
/* 349 */         BillCardPanelUtil.setBodyValue(billCardPanel, affectRows[i], "pk_supplier", pk_supplier.get(paramVOs[i].getPk_material_v() + paramVOs[i].getPk_stockOrg()));
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
/*     */ 
/*     */   
/*     */   private void setRefPk(int[] affectRows, String value, BillCardPanel billCardPanel, String cloName) {
/* 369 */     for (int i = 0; i < affectRows.length; i++) {
/* 370 */       BillCardPanelUtil.setBodyValue(billCardPanel, affectRows[i], cloName, value, "bodyvos");
/*     */       
/* 372 */       billCardPanel.getBillModel().loadLoadRelationItemValue(affectRows[i], cloName);
/*     */       
/* 374 */       billCardPanel.getBillModel().loadEditRelationItemValue(affectRows[i], cloName);
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
/* 387 */     BillCardPanel billCardPanel = this.cardForm.getBillCardPanel();
/* 388 */     for (int row : affectRows) {
/* 389 */       BillCardPanelUtil.setBodyValue(billCardPanel, row, "nnum", billCardPanel.getBillModel().getValueAt(row, "nnum"));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 396 */       BillCardPanelUtil.setBodyValue(billCardPanel, row, "nowadd_num", billCardPanel.getBillModel().getValueAt(row, "nowadd_num"));
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
/*     */   private void setService_prc_ratio(int[] affectRows) {
/* 412 */     BillCardPanel billCardPanel = this.cardForm.getBillCardPanel();
/* 413 */     for (int row : affectRows) {
/* 414 */       Boolean fee = (Boolean)billCardPanel.getBillModel().getValueAt(row, "pk_material_v.fee");
/* 415 */       if (fee != null && fee.booleanValue() == true) {
/* 416 */         BillCardPanelUtil.setBodyValue(billCardPanel, row, "service_prc_ratio", Integer.valueOf(1));
/*     */       
/*     */       }
/*     */       else {
/*     */ 
/*     */         
/* 422 */         BillCardPanelUtil.setBodyValue(billCardPanel, row, "service_prc_ratio", null);
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
/*     */   
/*     */   private void setRowEdits(CardBodyAfterEditEvent e, int[] affectRows) {
/*     */     try {
/* 442 */       for (int i : affectRows)
/*     */       {
/* 444 */         e.getBillCardPanel().getBillModel().setValueAt(null, i, "nowadd_num");
/*     */         
/* 446 */         if (e.getBillCardPanel().getBillModel().getValueAt(i, "pk_mater_plan_b") != null)
/*     */         {
/* 448 */           setSelectedRowEditable(i, Boolean.FALSE);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 462 */           setSelectedRowEditable(i, Boolean.TRUE);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     }
/* 481 */     catch (Exception ex) {
/* 482 */       ExceptionUtils.asBusinessRuntimeException(ex);
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
/*     */   private void setDefaultStockOrg(int[] affectRows) throws BusinessException {
/* 494 */     BillCardPanel billCardPanel = this.cardForm.getBillCardPanel();
/* 495 */     List<Integer> rowList = new ArrayList<Integer>();
/* 496 */     for (int num : affectRows) {
/* 497 */       if (BillCardPanelUtil.getBodyValue(billCardPanel, "pk_mater_plan_b", num) == null)
/*     */       {
/* 499 */         rowList.add(Integer.valueOf(num));
/*     */       }
/*     */     } 
/*     */     
/* 503 */     if (ListUtil.isEmpty(rowList)) {
/*     */       return;
/*     */     }
/*     */     
/* 507 */     String pk_org = (String)BillCardPanelUtil.getHeadItemValue(billCardPanel, "pk_org");
/*     */ 
/*     */ 
/*     */     
/* 511 */     String pk_projectclass = (String)BillCardPanelUtil.getHeadItemValue(billCardPanel, "pk_project.pk_projectclass");
/*     */ 
/*     */     
/* 514 */     billCardPanel.getBillModel().loadLoadRelationItemValue(affectRows[0], "pk_material");
/*     */ 
/*     */     
/* 517 */     String pk_marbasclass = (String)billCardPanel.getBillModel().getValueAt(affectRows[0], "pk_material_v.pk_marbasclass_ID");
/*     */ 
/*     */ 
/*     */     
/* 521 */     String stockOrg = getIOrgUnitPubService().getDefaultStockOrgFromItemStockRelation(pk_org, pk_projectclass, pk_marbasclass);
/*     */ 
/*     */     
/* 524 */     Map<String, String> marClStockOrgMap = new HashMap<String, String>();
/* 525 */     marClStockOrgMap.put(pk_marbasclass, stockOrg);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 534 */     for (int num : affectRows) {
/* 535 */       Boolean fee = (Boolean)billCardPanel.getBillModel().getValueAt(num, "pk_material_v.fee");
/* 536 */       if (fee != null && fee.booleanValue() != true) {
/*     */         
/* 538 */         billCardPanel.getBillModel().setCellEditable(num, "pk_stockorg_v", true);
/* 539 */         billCardPanel.getBillModel().setValueAt(null, num, "pk_stockorg_v");
/* 540 */         billCardPanel.getBillModel().setCellEditable(num, "require_date", true);
/* 541 */         billCardPanel.getBillModel().setValueAt(null, num, "require_date");
/*     */         
/* 543 */         billCardPanel.getBillModel().setCellEditable(num, "pray_param", true);
/* 544 */         billCardPanel.getBillModel().setValueAt(null, num, "pray_param");
/*     */ 
/*     */         
/* 547 */         String pk_marClass = (String)billCardPanel.getBillModel().getValueAt(num, "pk_material_v.pk_marbasclass_ID");
/*     */ 
/*     */         
/* 550 */         BillCardPanelUtil.setBodyValue(billCardPanel, num, "pk_stockorg", marClStockOrgMap.get(pk_marClass));
/*     */         
/* 552 */         billCardPanel.getBillModel().loadEditRelationItemValue(num, "pk_stockorg");
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
/* 564 */   private IOrgUnitPubService getIOrgUnitPubService() { return (IOrgUnitPubService)NCLocator.getInstance().lookup(IOrgUnitPubService.class); }
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
/*     */   private void setRelatedData(int[] affectRows) throws BusinessException {
/* 577 */     BillCardPanel billCardPanel = this.cardForm.getBillCardPanel();
/*     */     
/* 579 */     List<String> projectForPrayAndApply = new ArrayList<String>();
/*     */     
/* 581 */     List<String> materialForPrayAndApply = new ArrayList<String>();
/*     */     
/* 583 */     List<String> wbsForPrayAndApply = new ArrayList<String>();
/*     */     
/* 585 */     List<Integer> affectRowsForPrayAndApply = new ArrayList<Integer>();
/*     */     
/* 587 */     Boolean needSetPrayAndApply = Boolean.FALSE;
/*     */ 
/*     */ 
/*     */     
/* 591 */     List<String> materialForPlanNo = new ArrayList<String>();
/*     */     
/* 593 */     List<String> wbsForPlanNo = new ArrayList<String>();
/*     */     
/* 595 */     List<Integer> affectRowsForPlanNo = new ArrayList<Integer>();
/* 596 */     for (int i = 0; i < affectRows.length; i++) {
/* 597 */       String pk_material = (String)billCardPanel.getBillModel().getValueAt(affectRows[i], "pk_material_ID");
/*     */ 
/*     */       
/* 600 */       String pk_wbs = (String)billCardPanel.getBillModel().getValueAt(affectRows[i], "pk_cbs_node_ID");
/*     */ 
/*     */       
/* 603 */       String pk_mater_plan_b = (String)billCardPanel.getBillModel().getValueAt(affectRows[i], "pk_mater_plan_b");
/*     */ 
/*     */ 
/*     */       
/* 607 */       String projects = billCardPanel.getHeadTailItem("pk_project").getValue();
/*     */ 
/*     */       
/* 610 */       projectForPrayAndApply.add(projects);
/*     */       
/* 612 */       if (StringUtils.isNotBlank(pk_material) && StringUtils.isNotBlank(pk_wbs))
/*     */       {
/* 614 */         if (StringUtils.isBlank(pk_mater_plan_b)) {
/* 615 */           materialForPlanNo.add(pk_material);
/* 616 */           wbsForPlanNo.add(pk_wbs);
/* 617 */           affectRowsForPlanNo.add(Integer.valueOf(affectRows[i]));
/*     */ 
/*     */ 
/*     */           
/* 621 */           materialForPrayAndApply.add(pk_material);
/* 622 */           wbsForPrayAndApply.add(pk_wbs);
/* 623 */           needSetPrayAndApply = Boolean.TRUE;
/* 624 */           affectRowsForPrayAndApply.add(Integer.valueOf(affectRows[i]));
/*     */         } 
/*     */       }
/* 627 */       calRowPrices(affectRows[i], billCardPanel.getBillModel());
/*     */     } 
/* 629 */     List<Integer> returnList = new ArrayList<Integer>();
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
/* 642 */     if (needSetPrayAndApply.booleanValue()) {
/*     */       
/* 644 */       deleteRowNum(affectRowsForPrayAndApply, returnList);
/*     */       
/* 646 */       setPrayAndApply(affectRowsForPrayAndApply, projectForPrayAndApply, materialForPrayAndApply, wbsForPrayAndApply);
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
/*     */   private void deleteRowNum(List<Integer> affectRowsForPrayAndApply, List<Integer> returnList) {
/* 659 */     if (ListUtil.isEmpty(returnList)) {
/*     */       return;
/*     */     }
/* 662 */     for (Integer deleteRow : returnList) {
/* 663 */       if (affectRowsForPrayAndApply.contains(deleteRow)) {
/* 664 */         affectRowsForPrayAndApply.remove(deleteRow);
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
/*     */   private void setPrayAndApply(List<Integer> affectRows, List<String> projects, List<String> material, List<String> wbs) throws BusinessException {
/* 682 */     BillCardPanel billCardPanel = this.cardForm.getBillCardPanel();
/*     */     
/* 684 */     String[] projectsArray = (String[])projects.toArray(new String[projects.size()]);
/*     */     
/* 686 */     String[] materialsArray = (String[])material.toArray(new String[material.size()]);
/*     */ 
/*     */ 
/*     */     
/* 690 */     MaterialStockVO[] materialStockVOs = getStockCommonService().queryByBzPK(projectsArray, materialsArray);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 695 */     Map<List<String>, UFDouble> totalApplyNumMap = new HashMap<List<String>, UFDouble>();
/*     */ 
/*     */     
/* 698 */     Map<List<String>, UFDouble> totalPrayNumMap = new HashMap<List<String>, UFDouble>();
/*     */ 
/*     */     
/* 701 */     Map<List<String>, UFDouble> outApplyNumMap = new HashMap<List<String>, UFDouble>();
/*     */ 
/*     */     
/* 704 */     Map<List<String>, UFDouble> outPrayNumMap = new HashMap<List<String>, UFDouble>();
/*     */ 
/*     */     
/* 707 */     if (ArrayUtil.isNotEmpty(materialStockVOs))
/*     */     {
/* 709 */       for (MaterialStockVO tempVo : materialStockVOs) {
/*     */         
/* 711 */         List<String> keyList = new ArrayList<String>();
/* 712 */         keyList.add(tempVo.getPk_material());
/*     */ 
/*     */         
/* 715 */         totalApplyNumMap.put(keyList, UFDoubleUtils.add(new UFDouble[] { tempVo.getApply_num() }));
/* 716 */         outApplyNumMap.put(keyList, tempVo.getOpl_apply_num());
/* 717 */         totalPrayNumMap.put(keyList, UFDoubleUtils.add(new UFDouble[] { tempVo.getPray_num() }));
/* 718 */         outPrayNumMap.put(keyList, tempVo.getOpl_pray_num());
/*     */       } 
/*     */     }
/*     */     
/* 722 */     for (int i = 0; i < affectRows.size(); i++) {
/*     */       
/* 724 */       String pk_material = billCardPanel.getBillModel().getValueAt(((Integer)affectRows.get(i)).intValue(), "pk_material_ID").toString();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 730 */       String pk_wbs = billCardPanel.getBillModel().getValueAt(((Integer)affectRows.get(i)).intValue(), "pk_cbs_node_ID").toString();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 736 */       List<String> keyList = new ArrayList<String>();
/* 737 */       keyList.add(pk_material);
/* 738 */       keyList.add(pk_wbs);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 748 */       if (StringUtil.isNotEmpty((String)BillCardPanelUtil.getBodyValue(billCardPanel, "pk_mater_plan_b", ((Integer)affectRows.get(i)).intValue()))) {
/*     */ 
/*     */         
/* 751 */         billCardPanel.setBodyValueAt(totalApplyNumMap.get(keyList), ((Integer)affectRows.get(i)).intValue(), "apply_num");
/*     */         
/* 753 */         billCardPanel.setBodyValueAt(totalPrayNumMap.get(keyList), ((Integer)affectRows.get(i)).intValue(), "pray_num");
/*     */       }
/*     */       else {
/*     */         
/* 757 */         billCardPanel.setBodyValueAt(outApplyNumMap.get(keyList), ((Integer)affectRows.get(i)).intValue(), "apply_num");
/*     */         
/* 759 */         billCardPanel.setBodyValueAt(outPrayNumMap.get(keyList), ((Integer)affectRows.get(i)).intValue(), "pray_num");
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
/* 880 */   private IMaterialStockCommonService getStockCommonService() { return (IMaterialStockCommonService)NCLocator.getInstance().lookup(IMaterialStockCommonService.class); }
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
/*     */   private void setSelectedRowEditable(int row, Boolean isEditable) {
/* 898 */     BillModel billModel = this.cardForm.getBillCardPanel().getBillModel();
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
/* 909 */     BillCardPanelUtil.setBodyEditableByRow(this.cardForm.getBillCardPanel(), row, isEditable.booleanValue());
/* 910 */     billModel.setCellEditable(row, "pk_mater_plan_b", true);
/*     */     
/* 912 */     billModel.setCellEditable(row, "nowadd_num", true);
/*     */     
/* 914 */     billModel.setCellEditable(row, "memo", true);
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
/* 939 */     this.cardForm.getBillCardPanel().getBillModel().loadLoadRelationItemValue();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 944 */   public void setCardForm(ShowUpableBillForm cardForm) { this.cardForm = cardForm; }
/*     */ }


/* Location:              E:\NC\nchome\modules\pmr\client\li\\uipmr_projectmaterial.jar!/nc/ui/pbm/materialadd/handler/CardBodyAfterEditHandler.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.0.7
 */