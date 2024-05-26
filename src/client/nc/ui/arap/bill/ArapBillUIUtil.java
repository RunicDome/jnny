/*      */ package nc.ui.arap.bill;
/*      */ 
/*      */ import java.awt.Container;
/*      */ import java.lang.reflect.Field;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import javax.swing.JComponent;
/*      */ import nc.bs.arap.util.CheckException;
/*      */ import nc.bs.framework.common.NCLocator;
/*      */ import nc.bs.logging.Log;
/*      */ import nc.desktop.ui.WorkbenchEnvironment;
/*      */ import nc.itf.arap.bill.IArapBillBaseService;
/*      */ import nc.itf.arap.initbill.IInitBillCloseService;
/*      */ import nc.itf.arap.prv.ICheckStatusCallBack;
/*      */ import nc.pubitf.arap.bill.IArapBillPubService;
/*      */ import nc.pubitf.org.cache.IOrgUnitPubService_C;
/*      */ import nc.pubitf.rbac.IFunctionPermissionPubService;
/*      */ import nc.ui.arap.pub.ArapUiUtil;
/*      */ import nc.ui.arap.query.ArapBillTypeRefFilter;
/*      */ import nc.ui.arap.query.ArapTransTypeRefFilter;
/*      */ import nc.ui.arap.query.BaseDocWithPermissionOrgFilter;
/*      */ import nc.ui.arap.query.QueryTempCurrtypeFilterBankacc;
/*      */ import nc.ui.arap.query.QueryTempCurrtypeFilterBankaccAp;
/*      */ import nc.ui.arap.query.QueryTempCustomAndSupplierFilterFreeCustom;
/*      */ import nc.ui.arap.query.QueryTempMainOrgFilterBaseDoc;
/*      */ import nc.ui.arap.query.QueryTempSuppCusFilterBankacc;
/*      */ import nc.ui.arap.scale.ArapBillCardPanelScaleProcessor;
/*      */ import nc.ui.bd.ref.AbstractRefModel;
/*      */ import nc.ui.pub.beans.UIRefPane;
/*      */ import nc.ui.pub.bill.BillCardPanel;
/*      */ import nc.ui.pub.bill.BillData;
/*      */ import nc.ui.pub.bill.BillItem;
/*      */ import nc.ui.pub.pf.PfUtilClient;
/*      */ import nc.ui.pubapp.uif2app.query2.QueryConditionDLGDelegator;
/*      */ import nc.ui.uif2.editor.BillForm;
/*      */ import nc.ui.uif2.editor.TemplateContainer;
/*      */ import nc.ui.uif2.model.AbstractAppModel;
/*      */ import nc.ui.uif2.model.BillManageModel;
/*      */ import nc.vo.arap.basebill.BaseAggVO;
/*      */ import nc.vo.arap.basebill.BaseBillVO;
/*      */ import nc.vo.arap.basebill.BaseItemVO;
/*      */ import nc.vo.arap.billstatus.ARAPBillStatus;
/*      */ import nc.vo.arap.pf.PFCheckVO;
/*      */ import nc.vo.arap.pub.ArapBillTypeInfo;
import nc.vo.arap.pub.BillEnumCollection;
/*      */ import nc.vo.arap.pub.BillEnumCollection.ApproveStatus;
/*      */ import nc.vo.arap.pub.IArapBilltypeInfo;
/*      */ import nc.vo.fipub.exception.ExceptionHandler;
/*      */ import nc.vo.ml.AbstractNCLangRes;
/*      */ import nc.vo.ml.NCLangRes4VoTransl;
/*      */ import nc.vo.org.GroupVO;
/*      */ import nc.vo.pub.AggregatedValueObject;
/*      */ import nc.vo.pub.BusinessException;
/*      */ import nc.vo.pub.CircularlyAccessibleValueObject;
/*      */ import nc.vo.pub.bill.BillTempletVO;
/*      */ import nc.vo.pub.lang.UFDate;
/*      */ import nc.vo.pub.lang.UFDateTime;
/*      */ import nc.vo.pub.lang.UFDouble;
/*      */ import nc.vo.pubapp.pagination.PaginationQueryVO;
/*      */ import nc.vo.pubapp.pflow.PfUserObject;
/*      */ import nc.vo.sm.UserVO;
/*      */ import nc.vo.uif2.LoginContext;
/*      */ import nc.vo.wfengine.definition.WorkflowTypeEnum;
/*      */ import org.apache.commons.lang.ArrayUtils;
/*      */ import org.apache.commons.lang.StringUtils;
/*      */ 
/*      */ public class ArapBillUIUtil
/*      */ {
/*      */   public static final String START = "START";
/*      */   public static final String SIGNAL = "SIGNAL";
/*      */   public static final String ROLLBACK = "ROLLBACK";
/*      */   public static final String RECALL = "RECALL";
/*      */   public static final String MODIFY = "MODIFY";
/*      */   public static final String AUDIT = "APPROVE";
/*      */   public static final String EDIT = "EDIT";
/*      */   public static final String DELETE = "DELETE";
/*      */   public static final String CANCEL_AUDIT = "UNAPPROVE";
/*      */   public static final String SAVETEMP = "SAVETEMP";
/*      */   public static final String EDITTEMPSAVE = "EDITTEMPSAVE";
/*      */   public static final String UPDATE = "EDIT";
/*      */   public static final String SAVE = "SAVE";
/*      */   public static volatile HashMap<String, Boolean> initClose_map;
/*      */   private static final String PRE_BODYS = "bodys.";
/*      */   private static final String DEF = "def";
/*      */   private static final int DEF_NUM = 30;
/*      */   
/*      */   public ArapBillUIUtil() {}
/*      */   
/*      */   public static void reloadBillCardTemplate(BillForm billform2, Map<String, BillTempletVO> cache)
/*      */   {
/*   94 */     TemplateContainer templateContainer = billform2.getTemplateContainer();
/*   95 */     BillTempletVO template = null;
/*   96 */     BillCardPanel billCardPanel = billform2.getBillCardPanel();
/*   97 */     if (billCardPanel == null) {
/*   98 */       billCardPanel = new BillCardPanel();
/*      */     }
/*      */     
/*  101 */     AbstractAppModel model = billform2.getModel();
/*      */     
/*  103 */     if (cache.get(billform2.getNodekey()) == null) {
/*  104 */       if (templateContainer == null) {
/*  105 */         template = getTemplate(billform2, billCardPanel, model);
/*      */       } else {
/*  107 */         template = templateContainer.getTemplate(billform2.getNodekey());
/*  108 */         template = template == null ? getTemplate(billform2, billCardPanel, model) : template;
/*      */       }
/*  110 */       cache.put(billform2.getNodekey(), template);
/*      */     } else {
/*  112 */       template = (BillTempletVO)cache.get(billform2.getNodekey());
/*      */     }
/*      */     
/*  115 */     if (template == null) {
/*  116 */       nc.bs.logging.Logger.error("没有找到nodekey：" + billform2.getNodekey() + "对应的卡片模板");
/*  117 */       throw new IllegalArgumentException(NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0", "02006pub-0001"));
/*      */     }
/*      */     
/*  120 */     String beanId = template.getPKBillTemplet() + "_BillForm";
/*  121 */     billform2.setBeanId(beanId);
/*      */     
/*  123 */     billCardPanel.setBillData(new BillData(template), billform2.getUserdefitemPreparator(), true);
/*  124 */     new ArapBillCardPanelScaleProcessor().addADecimalListener(billCardPanel);
/*  125 */     billCardPanel.setEnabled(false);
/*      */     
/*  127 */     billform2.repaint();
/*  128 */     billform2.setEnabled(false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static BillTempletVO getTemplate(BillForm billform2, BillCardPanel billCardPanel, AbstractAppModel model)
/*      */   {
/*  139 */     billCardPanel.setBillType(model.getContext().getNodeCode());
/*  140 */     billCardPanel.setBusiType(null);
/*  141 */     billCardPanel.setOperator(model.getContext().getPk_loginUser());
/*  142 */     billCardPanel.setCorp(model.getContext().getPk_group());
/*  143 */     BillTempletVO template = billCardPanel.getDefaultTemplet(billCardPanel.getBillType(), null, billCardPanel.getOperator(), billCardPanel.getCorp(), billform2.getNodekey(), null);
/*  144 */     return template;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  167 */   private static final String[] baseDocRefs = { "bodys.supplier", "bodys.customer", "bodys.pk_psndoc", "bodys.pk_deptid", "bodys.pk_recproject", "bodys.payaccount", "bodys.recaccount", "subjcode", "bodys.subjcode", "bodys.pk_subjcode", "pk_subjcode", "bodys.pk_payterm", "bodys.material", "bodys.scomment", "bodys.cashitem", "bodys.bankrollprojet", "bodys.project", "scomment", "bodys.costcenter", "bodys.cashaccount" };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void initBusiDataQueryConditionDLG(QueryConditionDLGDelegator condDLGDelegator, BaseAggVO bill)
/*      */   {
/*  194 */     condDLGDelegator.setRefFilter("pk_billtype", new ArapBillTypeRefFilter((String)bill.getParentVO().getAttributeValue("pk_billtype")));
/*      */     
/*  196 */     QueryTempMainOrgFilterBaseDoc soOrgByOrgFileter = new QueryTempMainOrgFilterBaseDoc(condDLGDelegator, "pk_org", "so_org");
/*      */     
/*  198 */     soOrgByOrgFileter.addEditorListener();
/*      */     
/*  200 */     QueryTempMainOrgFilterBaseDoc puOrgByOrgFileter = new QueryTempMainOrgFilterBaseDoc(condDLGDelegator, "pk_org", "pu_org");
/*      */     
/*  202 */     puOrgByOrgFileter.addEditorListener();
/*      */     
/*  204 */     QueryTempMainOrgFilterBaseDoc soDeptByOrgFileter = new QueryTempMainOrgFilterBaseDoc(condDLGDelegator, "so_org", "so_deptid");
/*      */     
/*  206 */     soDeptByOrgFileter.addEditorListener();
/*      */     
/*  208 */     QueryTempMainOrgFilterBaseDoc soPsndocByOrgFileter = new QueryTempMainOrgFilterBaseDoc(condDLGDelegator, "so_org", "so_psndoc");
/*      */     
/*  210 */     soPsndocByOrgFileter.addEditorListener();
/*      */     
/*  212 */     QueryTempMainOrgFilterBaseDoc puDeptByOrgFileter = new QueryTempMainOrgFilterBaseDoc(condDLGDelegator, "pu_org", "pu_deptid");
/*      */     
/*  214 */     puDeptByOrgFileter.addEditorListener();
/*      */     
/*  216 */     QueryTempMainOrgFilterBaseDoc puPsndocByOrgFileter = new QueryTempMainOrgFilterBaseDoc(condDLGDelegator, "pu_org", "pu_psndoc");
/*      */     
/*  218 */     puPsndocByOrgFileter.addEditorListener();
/*      */   }
/*      */   
/*      */   public static void initQueryConditionDLG(QueryConditionDLGDelegator condDLGDelegator, String nodeCode, BillManageModel model, String billType2)
/*      */   {
/*  223 */     condDLGDelegator.setRefFilter("pk_org", new nc.ui.arap.query.MainOrgWithPermissionOrgFilter(model));
/*  224 */     condDLGDelegator.setRefFilter("pk_billtype", new ArapBillTypeRefFilter(billType2));
/*  225 */     condDLGDelegator.setRefFilter("bodys.pk_billtype", new ArapBillTypeRefFilter(billType2));
/*  226 */     condDLGDelegator.setRefFilter("pk_tradetypeid", new ArapTransTypeRefFilter(billType2));
/*  227 */     condDLGDelegator.setRefFilter("bodys.pk_tradetypeid", new ArapTransTypeRefFilter(billType2));
/*      */     
/*      */ 
/*  230 */     for (String baseDoc : baseDocRefs) {
/*  231 */       condDLGDelegator.setRefFilter(baseDoc, new BaseDocWithPermissionOrgFilter(model));
/*  232 */       QueryTempMainOrgFilterBaseDoc baseDocByOrgFileter = new QueryTempMainOrgFilterBaseDoc(condDLGDelegator, "pk_org", baseDoc);
/*  233 */       baseDocByOrgFileter.addEditorListener();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  238 */     QueryTempMainOrgFilterBaseDoc baseDocByOrgFileter = new QueryTempMainOrgFilterBaseDoc(condDLGDelegator, "pk_org", "bodys.costcenter", "pk_financeorg");
/*      */     
/*  240 */     baseDocByOrgFileter.addEditorListener();
/*      */     
/*  242 */     for (int i = 1; i <= 30; i++) {
/*  243 */       condDLGDelegator.setRefFilter("def" + i, new BaseDocWithPermissionOrgFilter(model));
/*  244 */       QueryTempMainOrgFilterBaseDoc headDefByOrgFileter = new QueryTempMainOrgFilterBaseDoc(condDLGDelegator, "pk_org", "def" + i);
/*  245 */       headDefByOrgFileter.addEditorListener();
/*  246 */       condDLGDelegator.setRefFilter("bodys.def" + i, new BaseDocWithPermissionOrgFilter(model));
/*  247 */       QueryTempMainOrgFilterBaseDoc bodyDefByOrgFileter = new QueryTempMainOrgFilterBaseDoc(condDLGDelegator, "pk_org", "bodys.def" + i);
/*  248 */       bodyDefByOrgFileter.addEditorListener();
/*      */     }
/*      */     
/*      */ 
/*  252 */     QueryTempMainOrgFilterBaseDoc soOrgByOrgFileter = new QueryTempMainOrgFilterBaseDoc(condDLGDelegator, "pk_org", "bodys.so_org");
/*      */     
/*  254 */     soOrgByOrgFileter.addEditorListener();
/*      */     
/*  256 */     QueryTempMainOrgFilterBaseDoc puOrgByOrgFileter = new QueryTempMainOrgFilterBaseDoc(condDLGDelegator, "pk_org", "bodys.pu_org");
/*      */     
/*  258 */     puOrgByOrgFileter.addEditorListener();
/*      */     
/*  260 */     QueryTempMainOrgFilterBaseDoc soDeptByOrgFileter = new QueryTempMainOrgFilterBaseDoc(condDLGDelegator, "bodys.so_org", "bodys.so_deptid");
/*      */     
/*  262 */     soDeptByOrgFileter.addEditorListener();
/*      */     
/*  264 */     QueryTempMainOrgFilterBaseDoc soPsndocByOrgFileter = new QueryTempMainOrgFilterBaseDoc(condDLGDelegator, "bodys.so_org", "bodys.so_psndoc");
/*      */     
/*  266 */     soPsndocByOrgFileter.addEditorListener();
/*      */     
/*  268 */     QueryTempMainOrgFilterBaseDoc puDeptByOrgFileter = new QueryTempMainOrgFilterBaseDoc(condDLGDelegator, "bodys.pu_org", "bodys.pu_deptid");
/*      */     
/*  270 */     puDeptByOrgFileter.addEditorListener();
/*      */     
/*  272 */     QueryTempMainOrgFilterBaseDoc puPsndocByOrgFileter = new QueryTempMainOrgFilterBaseDoc(condDLGDelegator, "bodys.pu_org", "bodys.pu_psndoc");
/*      */     
/*  274 */     puPsndocByOrgFileter.addEditorListener();
/*      */     
/*  276 */     QueryTempMainOrgFilterBaseDoc checkElementFileter = new QueryTempMainOrgFilterBaseDoc(condDLGDelegator, "bodys.pk_pcorg", "bodys.checkelement");
/*      */     
/*  278 */     checkElementFileter.addEditorListener();
/*      */     
/*      */ 
/*  281 */     QueryTempCustomAndSupplierFilterFreeCustom freeCustFileter1 = new QueryTempCustomAndSupplierFilterFreeCustom(condDLGDelegator, "customer", "bodys.freecust");
/*      */     
/*  283 */     freeCustFileter1.addEditorListener();
/*  284 */     QueryTempCustomAndSupplierFilterFreeCustom freeCustFileter2 = new QueryTempCustomAndSupplierFilterFreeCustom(condDLGDelegator, "bodys.customer", "bodys.freecust");
/*      */     
/*  286 */     freeCustFileter2.addEditorListener();
/*      */     
/*      */ 
/*  289 */     if (("F0".equals(billType2)) || ("F2".equals(billType2))) {
/*  290 */       QueryTempCurrtypeFilterBankaccAp payAccByCurrFilter = new QueryTempCurrtypeFilterBankaccAp(condDLGDelegator, "bodys.pk_currtype", "bodys.payaccount");
/*      */       
/*  292 */       payAccByCurrFilter.addEditorListener();
/*  293 */       QueryTempSuppCusFilterBankacc payAccByCusFilter = new QueryTempSuppCusFilterBankacc(condDLGDelegator, "bodys.customer", "bodys.payaccount");
/*      */       
/*  295 */       payAccByCusFilter.addEditorListener();
/*      */       
/*  297 */       QueryTempCurrtypeFilterBankacc recAccByCurrFilter = new QueryTempCurrtypeFilterBankacc(condDLGDelegator, "bodys.pk_currtype", "bodys.recaccount");
/*      */       
/*  299 */       recAccByCurrFilter.addEditorListener();
/*      */     }
/*  301 */     if (("F1".equals(billType2)) || ("F3".equals(billType2))) {
/*  302 */       QueryTempCurrtypeFilterBankacc payAccByCurrFilter = new QueryTempCurrtypeFilterBankacc(condDLGDelegator, "bodys.pk_currtype", "bodys.payaccount");
/*      */       
/*  304 */       payAccByCurrFilter.addEditorListener();
/*      */       
/*  306 */       QueryTempSuppCusFilterBankacc recAccBySupplierFilter = new QueryTempSuppCusFilterBankacc(condDLGDelegator, "bodys.supplier", "bodys.recaccount");
/*      */       
/*  308 */       recAccBySupplierFilter.addEditorListener();
/*  309 */       QueryTempCurrtypeFilterBankaccAp recAccByCurrFilter = new QueryTempCurrtypeFilterBankaccAp(condDLGDelegator, "bodys.pk_currtype", "bodys.recaccount");
/*      */       
/*  311 */       recAccByCurrFilter.addEditorListener();
/*      */     }
/*      */     
/*      */ 
/*  315 */     setDefaultPk_org(condDLGDelegator);
/*      */     
/*  317 */     condDLGDelegator.setPowerEnable(true);
/*      */   }
/*      */   
/*      */ 
/*      */   public static AggregatedValueObject executeBatchPM(String actionType, Container container, AggregatedValueObject bill) throws Exception {
/*  298 */     if (bill == null)
/*  299 */       return bill;
/*  300 */     String pk_tradetype = (String)bill.getParentVO().getAttributeValue("pk_tradetype");
/*  301 */     Object[] executePM = PfUtilClient.runBatch(container, actionType, pk_tradetype, new AggregatedValueObject[] { bill }, new PfUserObject[] { new PfUserObject() }, null, null);
/*  302 */     AggregatedValueObject billvo = (AggregatedValueObject)nc.vo.arap.utils.ArrayUtil.getFirstInArrays((Object[])executePM);
/*  303 */     return billvo;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void setDefaultPk_org(QueryConditionDLGDelegator condDLGDelegator)
/*      */   {
/*  326 */     String defaultOrg = null;
/*      */     try {
/*  328 */       defaultOrg = ArapUiUtil.getDefaultOrgUnit();
/*      */     } catch (Exception ex) {
/*  330 */       nc.vo.pubapp.pattern.exception.ExceptionUtils.wrappException(ex);
/*      */     }
/*  332 */     if ((defaultOrg != null) && (defaultOrg.trim().length() > 0)) {
/*  333 */       condDLGDelegator.setDefaultValue("pk_org", defaultOrg);
/*      */     }
/*      */   }
/*      */   
/*      */   public static Object executeBatchPM(String actionType, Container container, AggregatedValueObject... bills) throws Exception {
/*  338 */     return executeBatchPM(actionType, container, bills, new PfUserObject());
/*      */   }
/*      */   
/*      */   public static Object executeBatchPM(String actionType, Container container, AggregatedValueObject[] bills, PfUserObject userObj) throws Exception {
/*  342 */     return executeBatchPM(actionType, container, bills, userObj, null);
/*      */   }
/*      */   
/*      */   public static Object executeBatchPM(String actionType, Container container, AggregatedValueObject[] bills, PfUserObject userObj, HashMap pfParam) throws Exception {
/*  346 */     if ((null == bills) || (bills.length == 0)) {
/*  347 */       return bills;
/*      */     }
/*  349 */     String pk_tradetype = null;
/*  350 */     List<AggregatedValueObject> list = new ArrayList();
/*  351 */     for (AggregatedValueObject agg : bills) {
/*  352 */       if (agg != null) {
/*  353 */         list.add(agg);
/*      */       }
/*      */     }
/*      */     
/*  357 */     bills = (AggregatedValueObject[])list.toArray(new AggregatedValueObject[0]);
/*  358 */     for (AggregatedValueObject agg : bills) {
/*  359 */       if ((null == pk_tradetype) || (pk_tradetype.equals("")))
/*  360 */         pk_tradetype = (String)agg.getParentVO().getAttributeValue("pk_tradetype");
/*  361 */       if ("APPROVE".equals(actionType)) {
/*  362 */         agg.getParentVO().setAttributeValue("approver", WorkbenchEnvironment.getInstance().getLoginUser().getCuserid());
/*      */       }
/*      */     }
/*      */     
/*  366 */     PfUserObject[] userObjAry = new PfUserObject[bills.length];
/*  367 */     for (int i = 0; i < bills.length; i++) {
/*  368 */       userObjAry[i] = userObj;
/*      */     }
/*      */     
/*  371 */     if ((actionType.equals("SIGNAL")) || (actionType.equals("ROLLBACK"))) {
/*  372 */       userObjAry = null;
/*      */     }
/*      */     
/*  375 */     if (((actionType.equals("SIGNAL")) || (actionType.equals("ROLLBACK"))) && 
/*  376 */       (((ICheckStatusCallBack)NCLocator.getInstance().lookup(ICheckStatusCallBack.class)).isContainApproveFlowStartup(getBillIds(bills), getBillType(bills)))) {
/*  377 */       throw new BusinessException("流程类型组织参数和单据流程不一致，不能处理。请修改参数。");
/*      */     }
/*      */     
/*      */ 
/*  381 */     if (((actionType.equals("APPROVE")) || (actionType.equals("UNAPPROVE"))) && 
/*  382 */       (((ICheckStatusCallBack)NCLocator.getInstance().lookup(ICheckStatusCallBack.class)).isContainWorkFlowStartup(getBillIds(bills), getBillType(bills)))) {
/*  383 */       throw new BusinessException("流程类型组织参数和单据流程不一致，不能处理，请修改参数。");
/*      */     }
/*      */     
/*      */ 
/*  387 */     if (("APPROVE".equals(actionType)) || ("UNAPPROVE".equals(actionType)) || ("DELETE".equals(actionType))) {
/*  388 */       boolean hasValidProcessDef = bills.length == 1;
/*  389 */       hasValidProcessDef = hasValidProcessDef ? true : ((IArapBillPubService)NCLocator.getInstance().lookup(IArapBillPubService.class)).messageDrive(getPfCheckVO(actionType, bills));
/*      */       
/*  391 */       String billtype = (String)bills[0].getParentVO().getAttributeValue("pk_billtype");
/*      */       
/*  393 */       Class<? extends IArapBillBaseService> billService = ArapBillTypeInfo.getInstance(billtype).getBillService();
/*      */       BaseAggVO[] bills2;
/*      */       try {
/*  396 */         bills2 = (BaseAggVO[])bills;
/*      */       } catch (ClassCastException e) {
/*  398 */         bills2 = new BaseAggVO[bills.length];
/*  399 */         System.arraycopy(bills, 0, bills2, 0, bills.length);
/*      */       }
/*  401 */       if ((!hasValidProcessDef) && ("APPROVE".equals(actionType))) {
/*  402 */         BaseAggVO[] returnVOS = ((IArapBillBaseService)NCLocator.getInstance().lookup(billService)).approve(cleanInfoForCleanVOs(bills2));
/*  403 */         return fixInfoForCleanVOs(returnVOS, bills2); }
/*  404 */       if ((!hasValidProcessDef) && ("UNAPPROVE".equals(actionType))) {
/*  405 */         BaseAggVO[] waitForUnApprvBills = getWaitForUnApprvBills(bills2);
/*  406 */         if (waitForUnApprvBills.length != 0) {
/*  407 */           BaseAggVO[] returnVOS = ((IArapBillBaseService)NCLocator.getInstance().lookup(billService)).unapprove(cleanInfoForCleanVOs(waitForUnApprvBills));
/*  408 */           return fixInfoForCleanVOs(returnVOS, waitForUnApprvBills);
/*      */         }
/*  410 */         return bills2; }
/*  411 */       if ((!hasValidProcessDef) && ("DELETE".equals(actionType))) {
/*  412 */         ((IArapBillBaseService)NCLocator.getInstance().lookup(billService)).delete(bills2);
/*  413 */         return null;
/*      */       }
/*  415 */       return PfUtilClient.runBatch(container, actionType, pk_tradetype, bills, userObjAry, null, pfParam);
/*      */     }
/*      */     
/*  418 */     if (((actionType.equals("START")) || (actionType.equals("SAVE"))) && (isHc(bills))) {
/*  419 */       String billtype = (String)bills[0].getParentVO().getAttributeValue("pk_billtype");
/*  420 */       Class<? extends IArapBillBaseService> billService = ArapBillTypeInfo.getInstance(billtype).getBillService();
/*      */       BaseAggVO[] bills2;
/*      */       try {
/*  423 */         bills2 = (BaseAggVO[])bills;
/*      */       } catch (ClassCastException e) {
/*  425 */         bills2 = new BaseAggVO[bills.length];
/*  426 */         System.arraycopy(bills, 0, bills2, 0, bills.length);
/*      */       }
/*  428 */       BaseAggVO[] returnVOS = ((IArapBillBaseService)NCLocator.getInstance().lookup(billService)).save(bills2);
/*  429 */       return returnVOS;
/*      */     }
/*  431 */     return PfUtilClient.runBatch(container, actionType, pk_tradetype, bills, userObjAry, null, pfParam);
/*      */   }
/*      */   
/*      */   private static boolean isHc(AggregatedValueObject[] bills) {
/*  435 */     boolean isHc = false;
/*  436 */     for (AggregatedValueObject object : bills) {
/*  437 */       BaseAggVO aggVO = (BaseAggVO)object;
/*  438 */       BaseBillVO pvo = (BaseBillVO)aggVO.getParentVO();
/*  439 */       if (pvo.getApprovestatus().intValue() == BillEnumCollection.ApproveStatus.PASSING.VALUE.intValue()) {
/*  440 */         for (CircularlyAccessibleValueObject cObject : aggVO.getChildrenVO()) {
/*  441 */           BaseItemVO vo = (BaseItemVO)cObject;
/*  442 */           if (((vo.getMoney_de().toDouble().doubleValue() < 0.0D) || (vo.getMoney_cr().toDouble().doubleValue() < 0.0D)) && (vo.getPk_billtype().equals(vo.getTop_billtype()))) {
/*  443 */             isHc = true;
/*  444 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  449 */     return isHc;
/*      */   }
/*      */   
/*      */   private static String[] getBillIds(AggregatedValueObject[] bills) {
/*  453 */     ArrayList<String> larr_billIds = new ArrayList();
/*  454 */     for (AggregatedValueObject object : bills) {
/*  455 */       larr_billIds.add(((BaseBillVO)((BaseAggVO)object).getParentVO()).getPrimaryKey());
/*      */     }
/*      */     
/*  458 */     return (String[])larr_billIds.toArray(new String[larr_billIds.size()]);
/*      */   }
/*      */   
/*      */   private static String getBillType(AggregatedValueObject[] bills) {
/*  462 */     String billType = "";
/*  463 */     AggregatedValueObject[] arr$ = bills;int len$ = arr$.length;int i$ = 0; if (i$ < len$) { AggregatedValueObject object = arr$[i$];
/*  464 */       billType = ((BaseBillVO)((BaseAggVO)object).getParentVO()).getPk_billtype();
/*      */     }
/*      */     
/*      */ 
/*  468 */     return billType;
/*      */   }
/*      */   
/*      */   public static Object executeBatchPMforBesideApprove(String actionType, Container container, AggregatedValueObject[] bills, PfUserObject userObj, HashMap pfParam)
/*      */     throws Exception
/*      */   {
/*  474 */     return executeBatchPM(actionType, container, bills, userObj, pfParam);
/*      */   }
/*      */   
/*      */   private static BaseAggVO[] cleanInfoForCleanVOs(BaseAggVO[] aggs) throws BusinessException {
/*  478 */     List<BaseAggVO> ret = new ArrayList();
/*  479 */     for (BaseAggVO bill : aggs) {
/*  480 */       IArapBilltypeInfo billtype = ArapBillTypeInfo.getInstance(bill);
/*  481 */       BaseAggVO cleanVO = null;
/*  482 */       BaseBillVO head = null;
/*      */       try {
/*  484 */         cleanVO = (BaseAggVO)billtype.getAggvoClass().newInstance();
/*  485 */         head = (BaseBillVO)billtype.getBillvoClass().newInstance();
/*  486 */         head.setPrimaryKey(bill.getHeadVO().getPrimaryKey());
/*  487 */         head.setTs(bill.getHeadVO().getTs());
/*  488 */         head.setPk_billtype(bill.getHeadVO().getPk_billtype());
/*  489 */         head.setApprovedate(bill.getHeadVO().getApprovedate());
/*  490 */         head.setApprover(bill.getHeadVO().getApprover());
/*  491 */         head.setAuthAccount(bill.getHeadVO().isAuthAccount());
/*  492 */         cleanVO.setParentVO(head);
/*  493 */         cleanVO.setNeedFixInfo(true);
/*      */       } catch (Exception e) {
/*  495 */         ExceptionHandler.handleException(e);
/*      */       }
/*  497 */       ret.add(cleanVO);
/*      */     }
/*  499 */     return (BaseAggVO[])ret.toArray(new BaseAggVO[0]);
/*      */   }
/*      */   
/*      */   private static BaseAggVO[] fixInfoForCleanVOs(BaseAggVO[] returnInfo, BaseAggVO[] oldInfo) {
/*  503 */     int i = 0;
/*  504 */     List<String> loadRelationItems = returnInfo[0].getLoadRelationItems();
/*      */     
/*  506 */     for (BaseAggVO bill : oldInfo) {
/*  507 */       BaseAggVO returnVO = returnInfo[(i++)];
/*      */       
/*  509 */       if (!returnVO.isNeedFixInfo()) {
/*  510 */         return returnInfo;
/*      */       }
/*  512 */       BaseBillVO headVOOld = bill.getHeadVO();
/*  513 */       BaseBillVO headVOReturn = returnVO.getHeadVO();
/*      */       
/*  515 */       BaseItemVO[] bodyVOOld = bill.getItems();
/*  516 */       BaseItemVO[] bodyVOReturn = returnVO.getItems();
/*      */       
/*  518 */       if (loadRelationItems != null) {
/*  519 */         for (String key : loadRelationItems) {
/*  520 */           headVOOld.setAttributeValue(key, headVOReturn.getAttributeValue(key));
/*      */         }
/*      */         
/*  523 */         Map<String, BaseItemVO> map = new HashMap();
/*  524 */         for (BaseItemVO item : bodyVOReturn)
/*  525 */           map.put(item.getPrimaryKey(), item);
/*      */         ;
/*      */         BaseItemVO returnItem;
/*  528 */         for ( BaseItemVO oldItem : bodyVOOld) {
/*  529 */           returnItem = (BaseItemVO)map.get(oldItem.getPrimaryKey());
/*  530 */           for (String key : loadRelationItems) {
/*  531 */             oldItem.setAttributeValue(key, returnItem.getAttributeValue(key));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  536 */     return oldInfo;
/*      */   }
/*      */   
/*      */   private static BaseAggVO[] getWaitForUnApprvBills(BaseAggVO[] aggs) {
/*  540 */     List<BaseAggVO> ret = new ArrayList();
/*  541 */     Integer billstatus = null;
/*  542 */     for (BaseAggVO bill : aggs) {
/*  543 */       billstatus = bill.getHeadVO().getBillstatus();
/*  544 */       if ((billstatus.intValue() == ARAPBillStatus.AUDIT.VALUE.intValue()) || (billstatus.intValue() == ARAPBillStatus.SIGN.VALUE.intValue())) {
/*  545 */         ret.add(bill);
/*      */       }
/*      */     }
/*  548 */     return (BaseAggVO[])ret.toArray(new BaseAggVO[0]);
/*      */   }
/*      */   
/*      */   private static PFCheckVO[] getPfCheckVO(String actionType, AggregatedValueObject[] bills) {
/*  552 */     PFCheckVO[] checks = new PFCheckVO[bills.length];
/*  553 */     int workflowtype = 0;
/*      */     
/*  555 */     if (("APPROVE".equals(actionType)) || ("UNAPPROVE".equals(actionType)) || ("DELETE".equals(actionType))) {
/*  556 */       workflowtype = WorkflowTypeEnum.Approveflow.getIntValue();
/*  557 */     } else if (("SIGNAL".equals(actionType)) || ("ROLLBACK".equals(actionType))) {
/*  558 */       workflowtype = WorkflowTypeEnum.Workflow.getIntValue();
/*      */     } else {
/*  560 */       throw new nc.vo.pub.BusinessRuntimeException(NCLangRes4VoTransl.getNCLangRes().getStrByID("2006v61008_0", "02006v61008-0249"));
/*      */     }
/*  562 */     for (int i = 0; i < bills.length; i++) {
/*  563 */       PFCheckVO check = new PFCheckVO();
/*  564 */       BaseBillVO bill = (BaseBillVO)bills[i].getParentVO();
/*  565 */       check.setPk_busitype(bill.getPk_busitype());
/*  566 */       check.setPk_group(bill.getPk_group());
/*  567 */       check.setPk_org(bill.getPk_org());
/*  568 */       check.setPk_tradetype(bill.getPk_tradetype());
/*  569 */       check.setOperator(((BaseBillVO)bills[i].getParentVO()).getCreator());
/*  570 */       check.setWorkflowtype(workflowtype);
/*  571 */       checks[i] = check;
/*      */     }
/*  573 */     return checks;
/*      */   }
/*      */   
/*      */   public static BaseAggVO[] convertObjetcsToAggs(Object... values) {
/*  577 */     BaseAggVO[] aggs = new BaseAggVO[values.length];
/*  578 */     int index = 0;
/*  579 */     for (Object o : values) {
/*  580 */       aggs[(index++)] = ((BaseAggVO)o);
/*      */     }
/*  582 */     return aggs;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void refreshChildVO2HeadVO(AggregatedValueObject... aggVOs)
/*      */   {
/*  594 */     nc.bs.arap.bill.ArapBillPubUtil.refreshChildVO2HeadVO(aggVOs);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void checkBatchActionState(BillManageModel model, String action)
/*      */     throws BusinessException
/*      */   {
/*  605 */     Object[] values = model.getSelectedOperaDatas();
/*  606 */     CheckException.checkArgument((values == null) || (values.length == 0), NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0", "02006pub-0113"));
/*      */     
/*  608 */     AggregatedValueObject[] aggs = (AggregatedValueObject[])nc.vo.arap.utils.ArrayUtil.convertSupers2Subs(values, AggregatedValueObject.class);
/*      */     
/*  610 */     Integer billstatus = null;
/*  611 */     String bill_no = null;
/*  612 */     LinkedList<String> list = new LinkedList();
/*      */     
/*      */ 
/*  615 */     if (action.equalsIgnoreCase("APPROVE")) {
/*  616 */       if (model.getUiState() == nc.ui.uif2.UIState.NOT_EDIT) {
/*  617 */         for (int index = 0; index < aggs.length; index++) {
/*  618 */           billstatus = (Integer)aggs[index].getParentVO().getAttributeValue("billstatus");
/*  619 */           bill_no = (String)aggs[index].getParentVO().getAttributeValue("billno");
/*      */           
/*  621 */           if ((!billstatus.equals(ARAPBillStatus.SAVE.VALUE)) && (!billstatus.equals(ARAPBillStatus.AUDITING.VALUE))) {
/*  622 */             list.add(bill_no);
/*      */           }
/*      */         }
/*      */       }
/*  626 */     } else if (action.equalsIgnoreCase("UNAPPROVE"))
/*      */     {
/*  628 */       for (int index = 0; index < aggs.length; index++) {
/*  629 */         billstatus = (Integer)aggs[index].getParentVO().getAttributeValue("billstatus");
/*  630 */         bill_no = (String)aggs[index].getParentVO().getAttributeValue("billno");
/*      */         
/*  632 */         if ((billstatus.intValue() != ARAPBillStatus.AUDIT.VALUE.intValue()) && (billstatus.intValue() != ARAPBillStatus.AUDITING.VALUE.intValue())) {
/*  633 */           list.add(bill_no);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  639 */     if (list.size() > 0) {
/*  640 */       StringBuilder sb = new StringBuilder();
/*  641 */       for (String billno : list) {
/*  642 */         sb.append(NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0000814")).append(billno).append(",");
/*      */       }
/*  644 */       sb.deleteCharAt(sb.length() - 1);
/*  645 */       sb.append(NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0", "02006pub-0114", null, new String[] { action }));
/*  646 */       CheckException.checkArgument(true, sb.toString());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  653 */   public static String[] ArAPBillItems = { "objtype", "supplier", "customer", "pk_deptid", "pk_psndoc", "pk_currtype", "rate", "busidate", "payaccount", "recaccount", "pu_deptid", "pu_org", "pu_psndoc", "so_deptid", "so_org", "so_psndoc", "pk_balatype", "bankrollprojet", "scomment", "checkelement" };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Object[] refreshChildVO2HeadVO(Object[] bills)
/*      */   {
/*  682 */     if ((bills instanceof AggregatedValueObject[])) {
/*  683 */       refreshChildVO2HeadVO((AggregatedValueObject[])bills);
/*      */     }
/*  685 */     return bills;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void refreshChildVO2HeadVO(PaginationQueryVO queryVO)
/*      */   {
/*  698 */     if (null == queryVO)
/*  699 */       return;
/*  700 */     AggregatedValueObject[] bills = (AggregatedValueObject[])queryVO.getBillmap().values().toArray(new AggregatedValueObject[0]);
/*  701 */     refreshChildVO2HeadVO(bills);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T extends AggregatedValueObject> void setNewClientDateAndUser(T[] bills)
/*      */   {
/*  715 */     setClientDateAndUser(bills, 2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T extends AggregatedValueObject> void setEditClientDateAndUser(T[] bills)
/*      */   {
/*  730 */     setClientDateAndUser(bills, 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T extends AggregatedValueObject> void setDeleteClientDateAndUser(T[] bills)
/*      */   {
/*  745 */     setClientDateAndUser(bills, 3);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T extends AggregatedValueObject> void setApproveClientDateAndUser(T[] bills)
/*      */   {
/*  760 */     setClientDateAndUser(bills, 4);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T extends AggregatedValueObject> void setUnApproveClientDateAndUser(T[] bills)
/*      */   {
/*  775 */     setClientDateAndUser(bills, 5);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T extends AggregatedValueObject> void setNewEffectClientDateAndUser(T[] bills)
/*      */   {
/*  791 */     setClientDateAndUser(bills, 6);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T extends AggregatedValueObject> void setEditEffectClientDateAndUser(T[] bills)
/*      */   {
/*  807 */     setClientDateAndUser(bills, 7);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T extends AggregatedValueObject> void setDeleteUnEfectClientDateAndUser(T[] bills)
/*      */   {
/*  823 */     setClientDateAndUser(bills, 8);
/*      */   }
/*      */   
/*      */   private static <T extends AggregatedValueObject> void setClientDateAndUser(T[] bills, int vostatus) {
/*  827 */     LinkedList<CircularlyAccessibleValueObject> childrenVOs = new LinkedList();
/*      */     
/*  829 */     for (T aggvo : bills) {
/*  830 */       childrenVOs.addAll(java.util.Arrays.asList(aggvo.getChildrenVO()));
/*  831 */       setClientDateAndUser(aggvo.getParentVO(), vostatus);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static <T extends CircularlyAccessibleValueObject> void setClientDateAndUser(T bill, int vostatus)
/*      */   {
/*  847 */     UFDate busisDate = ArapUiUtil.getBusisDate();
/*  848 */     UFDateTime serverTime = WorkbenchEnvironment.getServerTime();
/*  849 */     switch (vostatus) {
/*      */     case 2: 
/*  851 */       bill.setAttributeValue("billmaker", getUserID());
/*  852 */       bill.setAttributeValue("creator", getUserID());
/*  853 */       if (bill.getAttributeValue("creationtime") == null) {
/*  854 */         bill.setAttributeValue("creationtime", serverTime);
/*      */       }
/*  856 */       bill.setStatus(vostatus);
/*  857 */       break;
/*      */     case 1: 
/*  859 */       bill.setStatus(vostatus);
/*  860 */       bill.setAttributeValue("modifier", getUserID());
/*  861 */       break;
/*      */     case 3: 
/*  863 */       bill.setAttributeValue("modifier", getUserID());
/*  864 */       if (bill.getAttributeValue("modifiedtime") == null) {
/*  865 */         bill.setAttributeValue("modifiedtime", serverTime);
/*      */       }
/*      */       break;
/*      */     case 4: 
/*  869 */       bill.setAttributeValue("approver", getUserID());
/*  870 */       bill.setAttributeValue("approvedate", busisDate);
/*  871 */       break;
/*      */     case 5: 
/*      */       break;
/*      */     case 6: 
/*  875 */       bill.setAttributeValue("creator", getUserID());
/*  876 */       if (bill.getAttributeValue("creationtime") == null) {
/*  877 */         bill.setAttributeValue("creationtime", serverTime);
/*      */       }
/*  879 */       bill.setAttributeValue("effect_year", getUserID());
/*  880 */       bill.setAttributeValue("effectdate", busisDate);
/*  881 */       break;
/*      */     case 7: 
/*  883 */       bill.setAttributeValue("modifier", getUserID());
/*  884 */       if (bill.getAttributeValue("modifiedtime") == null) {
/*  885 */         bill.setAttributeValue("modifiedtime", serverTime);
/*      */       }
/*  887 */       bill.setAttributeValue("effect_year", getUserID());
/*  888 */       bill.setAttributeValue("effectdate", busisDate);
/*  889 */       break;
/*      */     case 8: 
/*  891 */       bill.setAttributeValue("modifier", getUserID());
/*  892 */       if (bill.getAttributeValue("modifiedtime") == null) {
/*  893 */         bill.setAttributeValue("modifiedtime", serverTime);
/*      */       }
/*  895 */       bill.setAttributeValue("effect_year", null);
/*  896 */       bill.setAttributeValue("effectdate", null);
/*  897 */       break;
/*      */     }
/*      */     
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String getUserID()
/*      */   {
/*  922 */     return WorkbenchEnvironment.getInstance().getLoginUser().getCuserid();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isInitBillClosed(String nodeCode, String pk_org)
/*      */   {
/*  933 */     if (initClose_map == null) {
/*  934 */       initClose_map = new HashMap();
/*      */     }
/*  936 */     String key = nodeCode + pk_org;
/*  937 */     Boolean result = (Boolean)initClose_map.get(key);
/*      */     
/*  939 */     if (result == null) {
/*      */       try {
/*  941 */         result = Boolean.valueOf(((IInitBillCloseService)NCLocator.getInstance().lookup(IInitBillCloseService.class)).isInitBillClosed(nodeCode, pk_org));
/*  942 */         initClose_map.put(key, result);
/*      */       } catch (BusinessException e) {
/*  944 */         result = Boolean.FALSE;
/*  945 */         ExceptionHandler.consume(e);
/*      */       }
/*      */     }
/*      */     
/*  949 */     return result.booleanValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void updateInitCloseBuffer(String nodeCode, String pk_org)
/*      */     throws BusinessException
/*      */   {
/*  961 */     if (initClose_map == null) {
/*  962 */       initClose_map = new HashMap();
/*      */     }
/*  964 */     String key = nodeCode + pk_org;
/*  965 */     boolean result = ((IInitBillCloseService)NCLocator.getInstance().lookup(IInitBillCloseService.class)).isInitBillClosed(nodeCode, pk_org);
/*  966 */     initClose_map.put(key, Boolean.valueOf(result));
/*      */   }
/*      */   
/*      */   public static void fillRateInfo(AggregatedValueObject bill) {
/*  970 */     String pk_currtype = (String)bill.getParentVO().getAttributeValue("pk_currtype");
/*  971 */     String pk_billtype = (String)bill.getParentVO().getAttributeValue("pk_billtype");
/*  972 */     String pk_group = WorkbenchEnvironment.getInstance().getGroupVO().getPk_group();
/*  973 */     String pk_org = (String)bill.getParentVO().getAttributeValue("pk_org");
/*      */     
/*  975 */     if ((null != pk_org) && (null != pk_currtype) && (null != pk_billtype)) {
/*  976 */       UFDate date = ArapUiUtil.getBusisDate();
/*  977 */       UFDouble[] rates = nc.bs.arap.bill.ArapBillCalUtil.getRate(pk_currtype, pk_org, pk_group, date, pk_billtype);
/*      */       
/*  979 */       UFDouble rate = rates[0];
/*      */       
/*  981 */       UFDouble grouprate = rates[1];
/*      */       
/*  983 */       UFDouble globalrate = rates[2];
/*      */       
/*  985 */       bill.getParentVO().setAttributeValue("rate", rate);
/*  986 */       bill.getParentVO().setAttributeValue("grouprate", grouprate);
/*  987 */       bill.getParentVO().setAttributeValue("globalrate", globalrate);
/*      */       
/*  989 */       for (CircularlyAccessibleValueObject item : bill.getChildrenVO()) {
/*  990 */         item.setAttributeValue("rate", rate);
/*  991 */         item.setAttributeValue("grouprate", grouprate);
/*  992 */         item.setAttributeValue("globalrate", globalrate);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void showTbbMessage(AggregatedValueObject[] aggVOs, LoginContext ctx)
/*      */   {
/* 1001 */     if ((aggVOs == null) || (aggVOs.length == 0)) {
/* 1002 */       return;
/*      */     }
/*      */     
/* 1005 */     StringBuffer tbbMsg = new StringBuffer();
/* 1006 */     for (AggregatedValueObject aggVO : aggVOs) {
/* 1007 */       if ((aggVO != null) && (((BaseAggVO)aggVO).getTbbMsg() != null)) {
/* 1008 */         tbbMsg.append(((BaseAggVO)aggVO).getTbbMsg());
/* 1009 */         tbbMsg.append("\n");
/*      */       }
/*      */     }
/*      */     
/* 1013 */     if ((tbbMsg != null) && (tbbMsg.toString() != null) && (!tbbMsg.toString().equals(""))) {
/* 1014 */       nc.ui.uif2.ShowStatusBarMsgUtil.showErrorMsg(nc.ui.uif2.IShowMsgConstant.getSaveSuccessInfo(), tbbMsg.toString(), ctx);
/*      */     }
/*      */   }
/*      */   
/*      */   public static String[] getFuncCodePermissionOrgs(String funcode) {
/* 1019 */     String userid = WorkbenchEnvironment.getInstance().getLoginUser().getPrimaryKey();
/* 1020 */     String pk_group = WorkbenchEnvironment.getInstance().getGroupVO().getPk_group();
/*      */     try {
/* 1022 */       return ((IFunctionPermissionPubService)NCLocator.getInstance().lookup(IFunctionPermissionPubService.class)).getUserPermissionPkOrgs(userid, funcode, pk_group);
/*      */     } catch (BusinessException e) {
/* 1024 */       Log.getInstance("arapExceptionLog").error(e); }
/* 1025 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String[] getPermissionOrgVs(LoginContext context, UFDate date)
/*      */   {
/* 1038 */     String nodeCode = context.getNodeCode();
/* 1039 */     if (nodeCode.equals("20110CBSG")) {
/* 1040 */       nodeCode = "20110CBS";
/*      */     }
/*      */     
/* 1043 */     if (date == null) {
/* 1044 */       date = getSysdate();
/*      */     }
/* 1046 */     String cacheKey = nodeCode + date.toStdString() + "PERMISSION_PK_ORG_V" + ArapUiUtil.getPk_user() + ArapUiUtil.getPk_group();
/*      */     
/* 1048 */     String[] values = (String[])WorkbenchEnvironment.getInstance().getClientCache(cacheKey);
/* 1049 */     if (values == null) {
/*      */       try {
/* 1051 */         String[] permissionOrgs = null;
/* 1052 */         if ("20110CBSG".equals(context.getNodeCode())) {
/* 1053 */           permissionOrgs = getFuncCodePermissionOrgs(nodeCode);
/*      */         } else {
/* 1055 */           permissionOrgs = context.getFuncInfo().getFuncPermissionPkorgs();
/*      */         }
/*      */         
/* 1058 */         if (!ArrayUtils.isEmpty(permissionOrgs)) {
/* 1059 */           HashMap<String, String> newVIDSByOrgIDSAndDate = ((IOrgUnitPubService_C)NCLocator.getInstance().lookup(IOrgUnitPubService_C.class)).getNewVIDSByOrgIDSAndDate(permissionOrgs, date);
/*      */           
/* 1061 */           values = (String[])newVIDSByOrgIDSAndDate.values().toArray(new String[0]);
/*      */         }
/*      */         
/* 1064 */         if (values == null) {
/* 1065 */           values = new String[0];
/*      */         }
/*      */         
/* 1068 */         WorkbenchEnvironment.getInstance().putClientCache(cacheKey, values);
/*      */       } catch (BusinessException e) {
/* 1070 */         ExceptionHandler.consume(e);
/*      */       }
/*      */     }
/* 1073 */     return values;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static UFDate getSysdate()
/*      */   {
/* 1083 */     return WorkbenchEnvironment.getServerTime().getDate();
/*      */   }
/*      */   
/*      */ 
/*      */   public static void loadHeadScomment(BaseAggVO aggVO, BillItem scommentBodyItem)
/*      */   {
/* 1089 */     setRefTypeByReflectMethod(scommentBodyItem, 0);
/*      */     
/* 1091 */     if (aggVO == null) {
/* 1092 */       return;
/*      */     }
/* 1094 */     BaseBillVO parentVO = (BaseBillVO)aggVO.getParentVO();
/* 1095 */     String scomment = parentVO.getScomment();
/* 1096 */     BaseItemVO firstLine = ArrayUtils.isEmpty(aggVO.getItems()) ? null : aggVO.getItems()[0];
/*      */     
/* 1098 */     if (StringUtils.isEmpty(scomment)) {
/* 1099 */       scomment = firstLine == null ? null : firstLine.getScomment();
/*      */     }
/*      */     
/* 1102 */     if (StringUtils.isNotEmpty(scomment)) {
/* 1103 */       scommentBodyItem.setValue(scomment);
/*      */     }
/*      */   }
/*      */   
/*      */   public static void setRefTypeByReflectMethod(BillItem item, int ufref) {
/*      */     try {
/* 1109 */       nc.vo.pub.bill.IMetaDataProperty metaDataProperty = item.getMetaDataProperty();
/* 1110 */       Field field = metaDataProperty.getClass().getDeclaredField("type");
/* 1111 */       field.setAccessible(true);
/* 1112 */       field.setInt(metaDataProperty, ufref);
/*      */       
/* 1114 */       item.setDataType(ufref);
/*      */     } catch (Exception e) {
/* 1116 */       ExceptionHandler.consume(e);
/*      */     }
/*      */   }
/*      */   
/*      */   public static void setRefPk(JComponent component, String value) {
/* 1121 */     if ((component instanceof UIRefPane)) {
/* 1122 */       UIRefPane ref = (UIRefPane)component;
/* 1123 */       if ((!nc.vo.arap.utils.StringUtil.isEmpty(value)) && (ref != null)) {
/* 1124 */         ref.setPK(value);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static AbstractRefModel getHeadRefModel(BillData data, String fieldcode) {
/* 1130 */     UIRefPane refpane = getHeadRefPane(data, fieldcode);
/* 1131 */     return refpane == null ? null : refpane.getRefModel();
/*      */   }
/*      */   
/*      */   public static AbstractRefModel getBodyRefModel(BillData data, String fieldcode) {
/* 1135 */     UIRefPane refpane = getBodyRefPane(data, fieldcode);
/* 1136 */     return refpane == null ? null : refpane.getRefModel();
/*      */   }
/*      */   
/*      */   public static UIRefPane getHeadRefPane(BillData data, String fieldcode) {
/* 1140 */     BillItem billItem = data.getHeadItem(fieldcode);
/* 1141 */     if (billItem == null) return null;
/* 1142 */     JComponent component = billItem.getComponent();
/* 1143 */     return (component != null) && ((component instanceof UIRefPane)) ? (UIRefPane)component : null;
/*      */   }
/*      */   
/*      */   public static UIRefPane getBodyRefPane(BillData data, String fieldcode) {
/* 1147 */     BillItem billItem = data.getBodyItem(fieldcode);
/* 1148 */     if (billItem == null) return null;
/* 1149 */     JComponent component = billItem.getComponent();
/* 1150 */     return (component != null) && ((component instanceof UIRefPane)) ? (UIRefPane)component : null;
/*      */   }
/*      */   
/*      */   private static abstract interface VOExtStatus
/*      */     extends nc.vo.pub.VOStatus
/*      */   {
/*      */     public static final int APPROVE = 4;
/*      */     public static final int UNAPPROVE = 5;
/*      */     public static final int NEWEFFECT = 6;
/*      */     public static final int EDITEFFECT = 7;
/*      */     public static final int DELETEUNEFFECT = 8;
/*      */   }
/*      */ }
