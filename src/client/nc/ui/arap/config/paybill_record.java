/*      */ package nc.ui.arap.config;
/*      */ 
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import nc.funcnode.ui.action.MenuAction;
/*      */ import nc.ui.uif2.I18nFB;
/*      */ 
/*      */ public class paybill_record extends nc.ui.uif2.factory.AbstractJavaBeanDefinition
/*      */ {
/*   11 */   private Map<String, Object> context = new java.util.HashMap();
/*      */   
/*   13 */   public nc.funcnode.ui.action.SeparatorAction getSeparatorAction() { if (context.get("separatorAction") != null)
/*   14 */       return (nc.funcnode.ui.action.SeparatorAction)context.get("separatorAction");
/*   15 */     nc.funcnode.ui.action.SeparatorAction bean = new nc.funcnode.ui.action.SeparatorAction();
/*   16 */     context.put("separatorAction", bean);
/*   17 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*   18 */     invokeInitializingBean(bean);
/*   19 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.pubapp.uif2app.mediator.modelevent.OrgChangedMediator getOrgChangedMediator() {
/*   23 */     if (context.get("OrgChangedMediator") != null)
/*   24 */       return (nc.ui.pubapp.uif2app.mediator.modelevent.OrgChangedMediator)context.get("OrgChangedMediator");
/*   25 */     nc.ui.pubapp.uif2app.mediator.modelevent.OrgChangedMediator bean = new nc.ui.pubapp.uif2app.mediator.modelevent.OrgChangedMediator();
/*   26 */     context.put("OrgChangedMediator", bean);
/*   27 */     bean.setBillform(getBillFormEditor());
/*   28 */     bean.setModel(getManageAppModel());
/*   29 */     bean.setOrgChangedImpl(getOrgchange());
/*   30 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*   31 */     invokeInitializingBean(bean);
/*   32 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.pubapp.uif2app.view.BillCardAllDataValueStrategy getComponentValueManager() {
/*   36 */     if (context.get("componentValueManager") != null)
/*   37 */       return (nc.ui.pubapp.uif2app.view.BillCardAllDataValueStrategy)context.get("componentValueManager");
/*   38 */     nc.ui.pubapp.uif2app.view.BillCardAllDataValueStrategy bean = new nc.ui.pubapp.uif2app.view.BillCardAllDataValueStrategy();
/*   39 */     context.put("componentValueManager", bean);
/*   40 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*   41 */     invokeInitializingBean(bean);
/*   42 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.HBRelationAfterEditHandler getRelationEditHandler() {
/*   46 */     if (context.get("RelationEditHandler") != null)
/*   47 */       return (nc.ui.arap.viewhandler.HBRelationAfterEditHandler)context.get("RelationEditHandler");
/*   48 */     nc.ui.arap.viewhandler.HBRelationAfterEditHandler bean = new nc.ui.arap.viewhandler.HBRelationAfterEditHandler();
/*   49 */     context.put("RelationEditHandler", bean);
/*   50 */     bean.setHtob(getArapH2B());
/*   51 */     bean.setModel(getManageAppModel());
/*   52 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*   53 */     invokeInitializingBean(bean);
/*   54 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.pubapp.uif2app.actions.pagination.BillModelPaginationDelegator getPaginationDelegator() {
/*   58 */     if (context.get("paginationDelegator") != null)
/*   59 */       return (nc.ui.pubapp.uif2app.actions.pagination.BillModelPaginationDelegator)context.get("paginationDelegator");
/*   60 */     nc.ui.pubapp.uif2app.actions.pagination.BillModelPaginationDelegator bean = new nc.ui.pubapp.uif2app.actions.pagination.BillModelPaginationDelegator(getManageAppModel());context.put("paginationDelegator", bean);
/*   61 */     bean.setPaginationQuery(getPaginationQueryService());
/*   62 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*   63 */     invokeInitializingBean(bean);
/*   64 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.interceptor.PauseTransactActionInterceptor getPauseTransactActionInterceptor() {
/*   68 */     if (context.get("pauseTransactActionInterceptor") != null)
/*   69 */       return (nc.ui.arap.actions.interceptor.PauseTransactActionInterceptor)context.get("pauseTransactActionInterceptor");
/*   70 */     nc.ui.arap.actions.interceptor.PauseTransactActionInterceptor bean = new nc.ui.arap.actions.interceptor.PauseTransactActionInterceptor();
/*   71 */     context.put("pauseTransactActionInterceptor", bean);
/*   72 */     bean.setModel(getManageAppModel());
/*   73 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*   74 */     invokeInitializingBean(bean);
/*   75 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.interceptor.BillVersionActionInterceptor getBillVersionActionInterceptor() {
/*   79 */     if (context.get("billVersionActionInterceptor") != null)
/*   80 */       return (nc.ui.arap.actions.interceptor.BillVersionActionInterceptor)context.get("billVersionActionInterceptor");
/*   81 */     nc.ui.arap.actions.interceptor.BillVersionActionInterceptor bean = new nc.ui.arap.actions.interceptor.BillVersionActionInterceptor();
/*   82 */     context.put("billVersionActionInterceptor", bean);
/*   83 */     bean.setModel(getManageAppModel());
/*   84 */     bean.setHtob(getArapH2B());
/*   85 */     bean.setBillform(getBillFormEditor());
/*   86 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*   87 */     invokeInitializingBean(bean);
/*   88 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.interceptor.OperPowerActionInterceptor getOperPowerInterceptor() {
/*   92 */     if (context.get("operPowerInterceptor") != null)
/*   93 */       return (nc.ui.arap.actions.interceptor.OperPowerActionInterceptor)context.get("operPowerInterceptor");
/*   94 */     nc.ui.arap.actions.interceptor.OperPowerActionInterceptor bean = new nc.ui.arap.actions.interceptor.OperPowerActionInterceptor();
/*   95 */     context.put("operPowerInterceptor", bean);
/*   96 */     bean.setModel(getManageAppModel());
/*   97 */     bean.setBillform(getBillFormEditor());
/*   98 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*   99 */     invokeInitializingBean(bean);
/*  100 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.interceptor.CaActionInterceptor getCaActionInterceptor() {
/*  104 */     if (context.get("caActionInterceptor") != null)
/*  105 */       return (nc.ui.arap.actions.interceptor.CaActionInterceptor)context.get("caActionInterceptor");
/*  106 */     nc.ui.arap.actions.interceptor.CaActionInterceptor bean = new nc.ui.arap.actions.interceptor.CaActionInterceptor();
/*  107 */     context.put("caActionInterceptor", bean);
/*  108 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  109 */     invokeInitializingBean(bean);
/*  110 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.interceptor.BillVerifyActionInterceptor getBillVerifyActionInterceptor() {
/*  114 */     if (context.get("billVerifyActionInterceptor") != null)
/*  115 */       return (nc.ui.arap.actions.interceptor.BillVerifyActionInterceptor)context.get("billVerifyActionInterceptor");
/*  116 */     nc.ui.arap.actions.interceptor.BillVerifyActionInterceptor bean = new nc.ui.arap.actions.interceptor.BillVerifyActionInterceptor();
/*  117 */     context.put("billVerifyActionInterceptor", bean);
/*  118 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  119 */     invokeInitializingBean(bean);
/*  120 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.interceptor.OrgCheckActionInterceptor getOrgCheckActionInterceptor() {
/*  124 */     if (context.get("orgCheckActionInterceptor") != null)
/*  125 */       return (nc.ui.arap.actions.interceptor.OrgCheckActionInterceptor)context.get("orgCheckActionInterceptor");
/*  126 */     nc.ui.arap.actions.interceptor.OrgCheckActionInterceptor bean = new nc.ui.arap.actions.interceptor.OrgCheckActionInterceptor();
/*  127 */     context.put("orgCheckActionInterceptor", bean);
/*  128 */     bean.setBillform(getBillFormEditor());
/*  129 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  130 */     invokeInitializingBean(bean);
/*  131 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.uif2.userdefitem.UserDefItemContainer getUserdefitemContainer() {
/*  135 */     if (context.get("userdefitemContainer") != null)
/*  136 */       return (nc.ui.uif2.userdefitem.UserDefItemContainer)context.get("userdefitemContainer");
/*  137 */     nc.ui.uif2.userdefitem.UserDefItemContainer bean = new nc.ui.uif2.userdefitem.UserDefItemContainer();
/*  138 */     context.put("userdefitemContainer", bean);
/*  139 */     bean.setContext(getContext());
/*  140 */     bean.setParams(getManagedList0());
/*  141 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  142 */     invokeInitializingBean(bean);
/*  143 */     return bean;
/*      */   }
/*      */   
/*  146 */   private List getManagedList0() { List list = new ArrayList();list.add(getQueryParam_12da294());list.add(getQueryParam_10cc2f3());return list;
/*      */   }
/*      */   
/*  149 */   private nc.ui.uif2.userdefitem.QueryParam getQueryParam_12da294() { if (context.get("nc.ui.uif2.userdefitem.QueryParam#12da294") != null)
/*  150 */       return (nc.ui.uif2.userdefitem.QueryParam)context.get("nc.ui.uif2.userdefitem.QueryParam#12da294");
/*  151 */     nc.ui.uif2.userdefitem.QueryParam bean = new nc.ui.uif2.userdefitem.QueryParam();
/*  152 */     context.put("nc.ui.uif2.userdefitem.QueryParam#12da294", bean);
/*  153 */     bean.setMdfullname("arap.recbill");
/*  154 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  155 */     invokeInitializingBean(bean);
/*  156 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.uif2.userdefitem.QueryParam getQueryParam_10cc2f3() {
/*  160 */     if (context.get("nc.ui.uif2.userdefitem.QueryParam#10cc2f3") != null)
/*  161 */       return (nc.ui.uif2.userdefitem.QueryParam)context.get("nc.ui.uif2.userdefitem.QueryParam#10cc2f3");
/*  162 */     nc.ui.uif2.userdefitem.QueryParam bean = new nc.ui.uif2.userdefitem.QueryParam();
/*  163 */     context.put("nc.ui.uif2.userdefitem.QueryParam#10cc2f3", bean);
/*  164 */     bean.setMdfullname("arap.recitem");
/*  165 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  166 */     invokeInitializingBean(bean);
/*  167 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction getRefreshAllAction() {
/*  171 */     if (context.get("refreshAllAction") != null)
/*  172 */       return (nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction)context.get("refreshAllAction");
/*  173 */     nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction bean = new nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction();
/*  174 */     context.put("refreshAllAction", bean);
/*  175 */     bean.setModel(getManageAppModel());
/*  176 */     bean.setDataManager(getModelDataManager());
/*  177 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  178 */     invokeInitializingBean(bean);
/*  179 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.vo.arap.bill.util.ArapH2BMapping getArapH2B() {
/*  183 */     if (context.get("ArapH2B") != null)
/*  184 */       return (nc.vo.arap.bill.util.ArapH2BMapping)context.get("ArapH2B");
/*  185 */     nc.vo.arap.bill.util.ArapH2BMapping bean = new nc.vo.arap.bill.util.ArapH2BMapping();
/*  186 */     context.put("ArapH2B", bean);
/*  187 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  188 */     invokeInitializingBean(bean);
/*  189 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.impl.BillCardCalculator getBillCardCalculator() {
/*  193 */     if (context.get("BillCardCalculator") != null)
/*  194 */       return (nc.ui.arap.impl.BillCardCalculator)context.get("BillCardCalculator");
/*  195 */     nc.ui.arap.impl.BillCardCalculator bean = new nc.ui.arap.impl.BillCardCalculator();
/*  196 */     context.put("BillCardCalculator", bean);
/*  197 */     bean.setCalItem(getRelationItemForCal());
/*  198 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  199 */     invokeInitializingBean(bean);
/*  200 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.view.NodeKeyQry getNodeKeyQry() {
/*  204 */     if (context.get("NodeKeyQry") != null)
/*  205 */       return (nc.ui.arap.view.NodeKeyQry)context.get("NodeKeyQry");
/*  206 */     nc.ui.arap.view.NodeKeyQry bean = new nc.ui.arap.view.NodeKeyQry();
/*  207 */     context.put("NodeKeyQry", bean);
/*  208 */     bean.setModel(getManageAppModel());
/*  209 */     bean.setDefNodekey(getDefNodeKey());
/*  210 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  211 */     invokeInitializingBean(bean);
/*  212 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.view.ArapBillListView getListView() {
/*  216 */     if (context.get("listView") != null)
/*  217 */       return (nc.ui.arap.view.ArapBillListView)context.get("listView");
/*  218 */     nc.ui.arap.view.ArapBillListView bean = new nc.ui.arap.view.ArapBillListView();
/*  219 */     context.put("listView", bean);
/*  220 */     bean.setModel(getManageAppModel());
/*  221 */     bean.setNodeKeyQry(getNodeKeyQry());
/*  222 */     bean.setPageSize(10000);
/*  223 */     bean.setTemplateContainer(getTemplateContainer());
/*  224 */     bean.setMultiSelectionEnable(true);
/*  225 */     bean.setPaginationDelegate(getPaginationDelegator());
/*  226 */     bean.setUserdefitemListPreparator(getUserdefitemListPreparator());
/*  227 */     bean.setMouseMediator(getMouseClickShowPanelMediator());
/*  228 */     bean.setLoadBillListTemplate(getLoadBillListTemplate());
/*  229 */     bean.initRealUI();
/*  230 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  231 */     invokeInitializingBean(bean);
/*  232 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.uif2.editor.UserdefitemContainerListPreparator getUserdefitemListPreparator() {
/*  236 */     if (context.get("userdefitemListPreparator") != null)
/*  237 */       return (nc.ui.uif2.editor.UserdefitemContainerListPreparator)context.get("userdefitemListPreparator");
/*  238 */     nc.ui.uif2.editor.UserdefitemContainerListPreparator bean = new nc.ui.uif2.editor.UserdefitemContainerListPreparator();
/*  239 */     context.put("userdefitemListPreparator", bean);
/*  240 */     bean.setContainer(getUserdefitemContainer());
/*  241 */     bean.setParams(getManagedList1());
/*  242 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  243 */     invokeInitializingBean(bean);
/*  244 */     return bean;
/*      */   }
/*      */   
/*  247 */   private List getManagedList1() { List list = new ArrayList();list.add(getListUserdefitemQueryParam());list.add(getListUserdefitemQueryParam1());return list;
/*      */   }
/*      */   
/*  250 */   private nc.ui.uif2.editor.UserdefQueryParam getListUserdefitemQueryParam() { if (context.get("listUserdefitemQueryParam") != null)
/*  251 */       return (nc.ui.uif2.editor.UserdefQueryParam)context.get("listUserdefitemQueryParam");
/*  252 */     nc.ui.uif2.editor.UserdefQueryParam bean = new nc.ui.uif2.editor.UserdefQueryParam();
/*  253 */     context.put("listUserdefitemQueryParam", bean);
/*  254 */     bean.setMdfullname("arap.recbill");
/*  255 */     bean.setPos(0);
/*  256 */     bean.setPrefix("def");
/*  257 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  258 */     invokeInitializingBean(bean);
/*  259 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.uif2.editor.UserdefQueryParam getListUserdefitemQueryParam1() {
/*  263 */     if (context.get("listUserdefitemQueryParam1") != null)
/*  264 */       return (nc.ui.uif2.editor.UserdefQueryParam)context.get("listUserdefitemQueryParam1");
/*  265 */     nc.ui.uif2.editor.UserdefQueryParam bean = new nc.ui.uif2.editor.UserdefQueryParam();
/*  266 */     context.put("listUserdefitemQueryParam1", bean);
/*  267 */     bean.setMdfullname("arap.recitem");
/*  268 */     bean.setPos(1);
/*  269 */     bean.setTabcode("bodys");
/*  270 */     bean.setPrefix("def");
/*  271 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  272 */     invokeInitializingBean(bean);
/*  273 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.pub.beans.ActionsBar.ActionsBarSeparator getActionsBarSeparator() {
/*  277 */     if (context.get("actionsBarSeparator") != null)
/*  278 */       return (nc.ui.pub.beans.ActionsBar.ActionsBarSeparator)context.get("actionsBarSeparator");
/*  279 */     nc.ui.pub.beans.ActionsBar.ActionsBarSeparator bean = new nc.ui.pub.beans.ActionsBar.ActionsBarSeparator();
/*  280 */     context.put("actionsBarSeparator", bean);
/*  281 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  282 */     invokeInitializingBean(bean);
/*  283 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator getMouseClickShowPanelMediator() {
/*  287 */     if (context.get("MouseClickShowPanelMediator") != null)
/*  288 */       return (nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator)context.get("MouseClickShowPanelMediator");
/*  289 */     nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator bean = new nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator();
/*  290 */     context.put("MouseClickShowPanelMediator", bean);
/*  291 */     bean.setShowUpComponent(getBillFormEditor());
/*  292 */     bean.setHyperLinkColumn("billno");
/*  293 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  294 */     invokeInitializingBean(bean);
/*  295 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.view.ArapBillCardTemplateContainer getTemplateContainer() {
/*  299 */     if (context.get("templateContainer") != null)
/*  300 */       return (nc.ui.arap.view.ArapBillCardTemplateContainer)context.get("templateContainer");
/*  301 */     nc.ui.arap.view.ArapBillCardTemplateContainer bean = new nc.ui.arap.view.ArapBillCardTemplateContainer();
/*  302 */     context.put("templateContainer", bean);
/*  303 */     bean.setContext(getContext());
/*  304 */     bean.setNodeKeyQry(getNodeKeyQry());
/*  305 */     bean.load();
/*  306 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  307 */     invokeInitializingBean(bean);
/*  308 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.view.ArapBillCardForm getBillFormEditor() {
/*  312 */     if (context.get("billFormEditor") != null)
/*  313 */       return (nc.ui.arap.view.ArapBillCardForm)context.get("billFormEditor");
/*  314 */     nc.ui.arap.view.ArapBillCardForm bean = new nc.ui.arap.view.ArapBillCardForm();
/*  315 */     context.put("billFormEditor", bean);
/*  316 */     bean.setModel(getManageAppModel());
/*  317 */     bean.setTemplateContainer(getTemplateContainer());
/*  318 */     bean.setComponentValueManager(getComponentValueManager());
/*  319 */     bean.setNodekeyQry(getNodeKeyQry());
/*  320 */     bean.setClosingListener(getClosingListener());
/*  321 */     bean.setAutoAddLine(false);
/*  322 */     bean.setTemplateNotNullValidate(true);
/*  323 */     bean.setBlankChildrenFilter(getSingleFieldBlankChildrenFilter_d3070a());
/*  324 */     bean.setDefValueItf(getBillDefVauleItf());
/*  325 */     bean.setBodyLineActions(getManagedList2());
/*  326 */     bean.setActions(getManagedList5());
/*  327 */     bean.setUserdefitemPreparator(getUserdefitemContainerPreparator_1219841());
/*  328 */     bean.initRealUI();
/*  329 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  330 */     invokeInitializingBean(bean);
/*  331 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.pubapp.uif2app.view.value.SingleFieldBlankChildrenFilter getSingleFieldBlankChildrenFilter_d3070a() {
/*  335 */     if (context.get("nc.ui.pubapp.uif2app.view.value.SingleFieldBlankChildrenFilter#d3070a") != null)
/*  336 */       return (nc.ui.pubapp.uif2app.view.value.SingleFieldBlankChildrenFilter)context.get("nc.ui.pubapp.uif2app.view.value.SingleFieldBlankChildrenFilter#d3070a");
/*  337 */     nc.ui.pubapp.uif2app.view.value.SingleFieldBlankChildrenFilter bean = new nc.ui.pubapp.uif2app.view.value.SingleFieldBlankChildrenFilter();
/*  338 */     context.put("nc.ui.pubapp.uif2app.view.value.SingleFieldBlankChildrenFilter#d3070a", bean);
/*  339 */     bean.setFieldName(getDefMoneyField());
/*  340 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  341 */     invokeInitializingBean(bean);
/*  342 */     return bean;
/*      */   }
/*      */   
/*  345 */   private List getManagedList2() { List list = new ArrayList();list.add(getAddLineAction());list.add(getInsertLineAction_7321f2());list.add(getBodyDelLineAction_b9a67c());list.add(getBodyCopyLineAction_612392());list.add(getBodyPasteLineAction_1666950());list.add(getBodyPasteToTailAction_8c26c8());list.add(getActionsBarSeparator());list.add(getBodyLineEditAction_14638a7());list.add(getActionsBarSeparator());list.add(getBillBodyZoomAction_13169ee());return list;
/*      */   }
/*      */   
/*  348 */   private nc.ui.arap.actions.InsertLineAction getInsertLineAction_7321f2() { if (context.get("nc.ui.arap.actions.InsertLineAction#7321f2") != null)
/*  349 */       return (nc.ui.arap.actions.InsertLineAction)context.get("nc.ui.arap.actions.InsertLineAction#7321f2");
/*  350 */     nc.ui.arap.actions.InsertLineAction bean = new nc.ui.arap.actions.InsertLineAction();
/*  351 */     context.put("nc.ui.arap.actions.InsertLineAction#7321f2", bean);
/*  352 */     bean.setHbrealtion(getRelationEditHandler());
/*  353 */     bean.setEditor(getBillFormEditor());
/*  354 */     bean.setIArapLineDefValUtil(getIArapLineDefValUtil());
/*  355 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  356 */     invokeInitializingBean(bean);
/*  357 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.arap.actions.BodyDelLineAction getBodyDelLineAction_b9a67c() {
/*  361 */     if (context.get("nc.ui.arap.actions.BodyDelLineAction#b9a67c") != null)
/*  362 */       return (nc.ui.arap.actions.BodyDelLineAction)context.get("nc.ui.arap.actions.BodyDelLineAction#b9a67c");
/*  363 */     nc.ui.arap.actions.BodyDelLineAction bean = new nc.ui.arap.actions.BodyDelLineAction();
/*  364 */     context.put("nc.ui.arap.actions.BodyDelLineAction#b9a67c", bean);
/*  365 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  366 */     invokeInitializingBean(bean);
/*  367 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.arap.actions.BodyCopyLineAction getBodyCopyLineAction_612392() {
/*  371 */     if (context.get("nc.ui.arap.actions.BodyCopyLineAction#612392") != null)
/*  372 */       return (nc.ui.arap.actions.BodyCopyLineAction)context.get("nc.ui.arap.actions.BodyCopyLineAction#612392");
/*  373 */     nc.ui.arap.actions.BodyCopyLineAction bean = new nc.ui.arap.actions.BodyCopyLineAction();
/*  374 */     context.put("nc.ui.arap.actions.BodyCopyLineAction#612392", bean);
/*  375 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  376 */     invokeInitializingBean(bean);
/*  377 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.arap.actions.BodyPasteLineAction getBodyPasteLineAction_1666950() {
/*  381 */     if (context.get("nc.ui.arap.actions.BodyPasteLineAction#1666950") != null)
/*  382 */       return (nc.ui.arap.actions.BodyPasteLineAction)context.get("nc.ui.arap.actions.BodyPasteLineAction#1666950");
/*  383 */     nc.ui.arap.actions.BodyPasteLineAction bean = new nc.ui.arap.actions.BodyPasteLineAction();
/*  384 */     context.put("nc.ui.arap.actions.BodyPasteLineAction#1666950", bean);
/*  385 */     bean.setClearItems(getManagedList3());
/*  386 */     bean.setBillType(getDefBillType());
/*  387 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  388 */     invokeInitializingBean(bean);
/*  389 */     return bean;
/*      */   }
/*      */   
/*  392 */   private List getManagedList3() { List list = new ArrayList();list.add("ts");return list;
/*      */   }
/*      */   
/*  395 */   private nc.ui.arap.actions.BodyPasteToTailAction getBodyPasteToTailAction_8c26c8() { if (context.get("nc.ui.arap.actions.BodyPasteToTailAction#8c26c8") != null)
/*  396 */       return (nc.ui.arap.actions.BodyPasteToTailAction)context.get("nc.ui.arap.actions.BodyPasteToTailAction#8c26c8");
/*  397 */     nc.ui.arap.actions.BodyPasteToTailAction bean = new nc.ui.arap.actions.BodyPasteToTailAction();
/*  398 */     context.put("nc.ui.arap.actions.BodyPasteToTailAction#8c26c8", bean);
/*  399 */     bean.setClearItems(getManagedList4());
/*  400 */     bean.setBillType(getDefBillType());
/*  401 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  402 */     invokeInitializingBean(bean);
/*  403 */     return bean;
/*      */   }
/*      */   
/*  406 */   private List getManagedList4() { List list = new ArrayList();list.add("ts");return list;
/*      */   }
/*      */   
/*  409 */   private nc.ui.arap.actions.BodyLineEditAction getBodyLineEditAction_14638a7() { if (context.get("nc.ui.arap.actions.BodyLineEditAction#14638a7") != null)
/*  410 */       return (nc.ui.arap.actions.BodyLineEditAction)context.get("nc.ui.arap.actions.BodyLineEditAction#14638a7");
/*  411 */     nc.ui.arap.actions.BodyLineEditAction bean = new nc.ui.arap.actions.BodyLineEditAction();
/*  412 */     context.put("nc.ui.arap.actions.BodyLineEditAction#14638a7", bean);
/*  413 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  414 */     invokeInitializingBean(bean);
/*  415 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.arap.actions.BillBodyZoomAction getBillBodyZoomAction_13169ee() {
/*  419 */     if (context.get("nc.ui.arap.actions.BillBodyZoomAction#13169ee") != null)
/*  420 */       return (nc.ui.arap.actions.BillBodyZoomAction)context.get("nc.ui.arap.actions.BillBodyZoomAction#13169ee");
/*  421 */     nc.ui.arap.actions.BillBodyZoomAction bean = new nc.ui.arap.actions.BillBodyZoomAction();
/*  422 */     context.put("nc.ui.arap.actions.BillBodyZoomAction#13169ee", bean);
/*  423 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  424 */     invokeInitializingBean(bean);
/*  425 */     return bean;
/*      */   }
/*      */   
/*  428 */   private List getManagedList5() { List list = new ArrayList();list.add(getFirstLineAction());list.add(getPreLineAction());list.add(getNextLineAction());list.add(getLastLineAction());return list;
/*      */   }
/*      */   
/*  431 */   private nc.ui.uif2.editor.UserdefitemContainerPreparator getUserdefitemContainerPreparator_1219841() { if (context.get("nc.ui.uif2.editor.UserdefitemContainerPreparator#1219841") != null)
/*  432 */       return (nc.ui.uif2.editor.UserdefitemContainerPreparator)context.get("nc.ui.uif2.editor.UserdefitemContainerPreparator#1219841");
/*  433 */     nc.ui.uif2.editor.UserdefitemContainerPreparator bean = new nc.ui.uif2.editor.UserdefitemContainerPreparator();
/*  434 */     context.put("nc.ui.uif2.editor.UserdefitemContainerPreparator#1219841", bean);
/*  435 */     bean.setContainer(getUserdefitemContainer());
/*  436 */     bean.setParams(getManagedList6());
/*  437 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  438 */     invokeInitializingBean(bean);
/*  439 */     return bean;
/*      */   }
/*      */   
/*  442 */   private List getManagedList6() { List list = new ArrayList();list.add(getCardUserdefitemQueryParam());list.add(getCardUserdefitemQueryParam1());return list;
/*      */   }
/*      */   
/*  445 */   private nc.ui.uif2.editor.UserdefQueryParam getCardUserdefitemQueryParam() { if (context.get("cardUserdefitemQueryParam") != null)
/*  446 */       return (nc.ui.uif2.editor.UserdefQueryParam)context.get("cardUserdefitemQueryParam");
/*  447 */     nc.ui.uif2.editor.UserdefQueryParam bean = new nc.ui.uif2.editor.UserdefQueryParam();
/*  448 */     context.put("cardUserdefitemQueryParam", bean);
/*  449 */     bean.setMdfullname("arap.recbill");
/*  450 */     bean.setPos(0);
/*  451 */     bean.setPrefix("def");
/*  452 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  453 */     invokeInitializingBean(bean);
/*  454 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.uif2.editor.UserdefQueryParam getCardUserdefitemQueryParam1() {
/*  458 */     if (context.get("cardUserdefitemQueryParam1") != null)
/*  459 */       return (nc.ui.uif2.editor.UserdefQueryParam)context.get("cardUserdefitemQueryParam1");
/*  460 */     nc.ui.uif2.editor.UserdefQueryParam bean = new nc.ui.uif2.editor.UserdefQueryParam();
/*  461 */     context.put("cardUserdefitemQueryParam1", bean);
/*  462 */     bean.setMdfullname("arap.recitem");
/*  463 */     bean.setPos(1);
/*  464 */     bean.setPrefix("def");
/*  465 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  466 */     invokeInitializingBean(bean);
/*  467 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.AddLineAction getAddLineAction() {
/*  471 */     if (context.get("AddLineAction") != null)
/*  472 */       return (nc.ui.arap.actions.AddLineAction)context.get("AddLineAction");
/*  473 */     nc.ui.arap.actions.AddLineAction bean = new nc.ui.arap.actions.AddLineAction();
/*  474 */     context.put("AddLineAction", bean);
/*  475 */     bean.setHbrealtion(getRelationEditHandler());
/*  476 */     bean.setEditor(getBillFormEditor());
/*  477 */     bean.setIArapLineDefValUtil(getIArapLineDefValUtil());
/*  478 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  479 */     invokeInitializingBean(bean);
/*  480 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.vo.uif2.LoginContext getContext() {
/*  484 */     if (context.get("context") != null)
/*  485 */       return (nc.vo.uif2.LoginContext)context.get("context");
/*  486 */     nc.vo.uif2.LoginContext bean = new nc.vo.uif2.LoginContext();
/*  487 */     context.put("context", bean);
/*  488 */     bean.setNodeType(nc.vo.bd.pub.NODE_TYPE.ORG_NODE);
/*  489 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  490 */     invokeInitializingBean(bean);
/*  491 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.vo.bd.meta.BDObjectAdpaterFactory getBoadatorfactory() {
/*  495 */     if (context.get("boadatorfactory") != null)
/*  496 */       return (nc.vo.bd.meta.BDObjectAdpaterFactory)context.get("boadatorfactory");
/*  497 */     nc.vo.bd.meta.BDObjectAdpaterFactory bean = new nc.vo.bd.meta.BDObjectAdpaterFactory();
/*  498 */     context.put("boadatorfactory", bean);
/*  499 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  500 */     invokeInitializingBean(bean);
/*  501 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.model.ArapBillManageModel getManageAppModel() {
/*  505 */     if (context.get("ManageAppModel") != null)
/*  506 */       return (nc.ui.arap.model.ArapBillManageModel)context.get("ManageAppModel");
/*  507 */     nc.ui.arap.model.ArapBillManageModel bean = new nc.ui.arap.model.ArapBillManageModel();
/*  508 */     context.put("ManageAppModel", bean);
/*  509 */     bean.setService(getManageModelService());
/*  510 */     bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
/*  511 */     bean.setContext(getContext());
/*  512 */     bean.setSupportLazilyLoad(true);
/*  513 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  514 */     invokeInitializingBean(bean);
/*  515 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.PrintPreviewAction getPrintPreview() {
/*  519 */     if (context.get("printPreview") != null)
/*  520 */       return (nc.ui.arap.actions.PrintPreviewAction)context.get("printPreview");
/*  521 */     nc.ui.arap.actions.PrintPreviewAction bean = new nc.ui.arap.actions.PrintPreviewAction();
/*  522 */     context.put("printPreview", bean);
/*  523 */     bean.setModel(getManageAppModel());
/*  524 */     bean.setInterceptor(getInterceptor());
/*  525 */     bean.setTaxForm(getTaxForm());
/*  526 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  527 */     invokeInitializingBean(bean);
/*  528 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.PrintOutputAction getPrintOutput() {
/*  532 */     if (context.get("printOutput") != null)
/*  533 */       return (nc.ui.arap.actions.PrintOutputAction)context.get("printOutput");
/*  534 */     nc.ui.arap.actions.PrintOutputAction bean = new nc.ui.arap.actions.PrintOutputAction();
/*  535 */     context.put("printOutput", bean);
/*  536 */     bean.setModel(getManageAppModel());
/*  537 */     bean.setInterceptor(getInterceptor());
/*  538 */     bean.setTaxForm(getTaxForm());
/*  539 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  540 */     invokeInitializingBean(bean);
/*  541 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.card.LoadBillCardTemplate getLoadBillCardTemplate() {
/*  545 */     if (context.get("LoadBillCardTemplate") != null)
/*  546 */       return (nc.ui.arap.viewhandler.card.LoadBillCardTemplate)context.get("LoadBillCardTemplate");
/*  547 */     nc.ui.arap.viewhandler.card.LoadBillCardTemplate bean = new nc.ui.arap.viewhandler.card.LoadBillCardTemplate();
/*  548 */     context.put("LoadBillCardTemplate", bean);
/*  549 */     bean.setBspsp(getScaleProcessor());
/*  550 */     bean.setBillform(getBillFormEditor());
/*  551 */     bean.setModel(getManageAppModel());
/*  552 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  553 */     invokeInitializingBean(bean);
/*  554 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.card.CardAfterEditCalculateHandler getCardAfterEditCalculateHandler() {
/*  558 */     if (context.get("CardAfterEditCalculateHandler") != null)
/*  559 */       return (nc.ui.arap.viewhandler.card.CardAfterEditCalculateHandler)context.get("CardAfterEditCalculateHandler");
/*  560 */     nc.ui.arap.viewhandler.card.CardAfterEditCalculateHandler bean = new nc.ui.arap.viewhandler.card.CardAfterEditCalculateHandler();
/*  561 */     context.put("CardAfterEditCalculateHandler", bean);
/*  562 */     bean.setCalitf(getBillCardCalculator());
/*  563 */     bean.setModel(getManageAppModel());
/*  564 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  565 */     invokeInitializingBean(bean);
/*  566 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.BodyCurrTypeAfterEditHandler getBodyCurrTypeAfterEditHandler() {
/*  570 */     if (context.get("BodyCurrTypeAfterEditHandler") != null)
/*  571 */       return (nc.ui.arap.viewhandler.cardafter.BodyCurrTypeAfterEditHandler)context.get("BodyCurrTypeAfterEditHandler");
/*  572 */     nc.ui.arap.viewhandler.cardafter.BodyCurrTypeAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.BodyCurrTypeAfterEditHandler();
/*  573 */     context.put("BodyCurrTypeAfterEditHandler", bean);
/*  574 */     bean.setBcpsp(getScaleProcessor());
/*  575 */     bean.setModel(getManageAppModel());
/*  576 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  577 */     invokeInitializingBean(bean);
/*  578 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.OtherOrgBodyAfterEditHandler getOtherOrgBodyAfterEditHandler() {
/*  582 */     if (context.get("OtherOrgBodyAfterEditHandler") != null)
/*  583 */       return (nc.ui.arap.viewhandler.cardafter.OtherOrgBodyAfterEditHandler)context.get("OtherOrgBodyAfterEditHandler");
/*  584 */     nc.ui.arap.viewhandler.cardafter.OtherOrgBodyAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.OtherOrgBodyAfterEditHandler();
/*  585 */     context.put("OtherOrgBodyAfterEditHandler", bean);
/*  586 */     bean.setHtob(getArapH2B());
/*  587 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  588 */     invokeInitializingBean(bean);
/*  589 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.OtherOrgHeadAfterEdithandler getOtherOrgHeadAfterEdithandler() {
/*  593 */     if (context.get("OtherOrgHeadAfterEdithandler") != null)
/*  594 */       return (nc.ui.arap.viewhandler.cardafter.OtherOrgHeadAfterEdithandler)context.get("OtherOrgHeadAfterEdithandler");
/*  595 */     nc.ui.arap.viewhandler.cardafter.OtherOrgHeadAfterEdithandler bean = new nc.ui.arap.viewhandler.cardafter.OtherOrgHeadAfterEdithandler();
/*  596 */     context.put("OtherOrgHeadAfterEdithandler", bean);
/*  597 */     bean.setHtob(getArapH2B());
/*  598 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  599 */     invokeInitializingBean(bean);
/*  600 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.OtherOrgBodyBeforeEditHandler getOtherOrgBodyBeforeEditHandler() {
/*  604 */     if (context.get("OtherOrgBodyBeforeEditHandler") != null)
/*  605 */       return (nc.ui.arap.viewhandler.cardbefore.OtherOrgBodyBeforeEditHandler)context.get("OtherOrgBodyBeforeEditHandler");
/*  606 */     nc.ui.arap.viewhandler.cardbefore.OtherOrgBodyBeforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.OtherOrgBodyBeforeEditHandler();
/*  607 */     context.put("OtherOrgBodyBeforeEditHandler", bean);
/*  608 */     bean.setHtob(getArapH2B());
/*  609 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  610 */     invokeInitializingBean(bean);
/*  611 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.OtherOrgHeadBeforeEditHandler getOtherOrgHeadBeforeEditHandler() {
/*  615 */     if (context.get("OtherOrgHeadBeforeEditHandler") != null)
/*  616 */       return (nc.ui.arap.viewhandler.cardbefore.OtherOrgHeadBeforeEditHandler)context.get("OtherOrgHeadBeforeEditHandler");
/*  617 */     nc.ui.arap.viewhandler.cardbefore.OtherOrgHeadBeforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.OtherOrgHeadBeforeEditHandler();
/*  618 */     context.put("OtherOrgHeadBeforeEditHandler", bean);
/*  619 */     bean.setHtob(getArapH2B());
/*  620 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  621 */     invokeInitializingBean(bean);
/*  622 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.HeadBankAccBeforeEditHandler getHeadBankAccBeforeEditHandler() {
/*  626 */     if (context.get("HeadBankAccBeforeEditHandler") != null)
/*  627 */       return (nc.ui.arap.viewhandler.cardbefore.HeadBankAccBeforeEditHandler)context.get("HeadBankAccBeforeEditHandler");
/*  628 */     nc.ui.arap.viewhandler.cardbefore.HeadBankAccBeforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.HeadBankAccBeforeEditHandler();
/*  629 */     context.put("HeadBankAccBeforeEditHandler", bean);
/*  630 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  631 */     invokeInitializingBean(bean);
/*  632 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.BodyObjTypeBeforeEditHandler getBodyObjTypeBeforeEditHandler() {
/*  636 */     if (context.get("BodyObjTypeBeforeEditHandler") != null)
/*  637 */       return (nc.ui.arap.viewhandler.cardbefore.BodyObjTypeBeforeEditHandler)context.get("BodyObjTypeBeforeEditHandler");
/*  638 */     nc.ui.arap.viewhandler.cardbefore.BodyObjTypeBeforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.BodyObjTypeBeforeEditHandler();
/*  639 */     context.put("BodyObjTypeBeforeEditHandler", bean);
/*  640 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  641 */     invokeInitializingBean(bean);
/*  642 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.HeadEuroBeforeEditHandler getHeadEuroBeforeEditHandler() {
/*  646 */     if (context.get("HeadEuroBeforeEditHandler") != null)
/*  647 */       return (nc.ui.arap.viewhandler.cardbefore.HeadEuroBeforeEditHandler)context.get("HeadEuroBeforeEditHandler");
/*  648 */     nc.ui.arap.viewhandler.cardbefore.HeadEuroBeforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.HeadEuroBeforeEditHandler();
/*  649 */     context.put("HeadEuroBeforeEditHandler", bean);
/*  650 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  651 */     invokeInitializingBean(bean);
/*  652 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.BodyEuroBeforeEditHandler getBodyEuroBeforeEditHandler() {
/*  656 */     if (context.get("BodyEuroBeforeEditHandler") != null)
/*  657 */       return (nc.ui.arap.viewhandler.cardbefore.BodyEuroBeforeEditHandler)context.get("BodyEuroBeforeEditHandler");
/*  658 */     nc.ui.arap.viewhandler.cardbefore.BodyEuroBeforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.BodyEuroBeforeEditHandler();
/*  659 */     context.put("BodyEuroBeforeEditHandler", bean);
/*  660 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  661 */     invokeInitializingBean(bean);
/*  662 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.HeadObjTypeBeforeEditHandler getHeadObjTypeBeforeEditHandler() {
/*  666 */     if (context.get("HeadObjTypeBeforeEditHandler") != null)
/*  667 */       return (nc.ui.arap.viewhandler.cardbefore.HeadObjTypeBeforeEditHandler)context.get("HeadObjTypeBeforeEditHandler");
/*  668 */     nc.ui.arap.viewhandler.cardbefore.HeadObjTypeBeforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.HeadObjTypeBeforeEditHandler();
/*  669 */     context.put("HeadObjTypeBeforeEditHandler", bean);
/*  670 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  671 */     invokeInitializingBean(bean);
/*  672 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.HeadFundplanAfterEditHandler getHeadFundplanAfterEditHandler() {
/*  676 */     if (context.get("HeadFundplanAfterEditHandler") != null)
/*  677 */       return (nc.ui.arap.viewhandler.cardafter.HeadFundplanAfterEditHandler)context.get("HeadFundplanAfterEditHandler");
/*  678 */     nc.ui.arap.viewhandler.cardafter.HeadFundplanAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.HeadFundplanAfterEditHandler();
/*  679 */     context.put("HeadFundplanAfterEditHandler", bean);
/*  680 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  681 */     invokeInitializingBean(bean);
/*  682 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.BodyFundplanAfterEditHandler getBodyFundplanAfterEditHandler() {
/*  686 */     if (context.get("BodyFundplanAfterEditHandler") != null)
/*  687 */       return (nc.ui.arap.viewhandler.cardafter.BodyFundplanAfterEditHandler)context.get("BodyFundplanAfterEditHandler");
/*  688 */     nc.ui.arap.viewhandler.cardafter.BodyFundplanAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.BodyFundplanAfterEditHandler();
/*  689 */     context.put("BodyFundplanAfterEditHandler", bean);
/*  690 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  691 */     invokeInitializingBean(bean);
/*  692 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.HBRelationAfterEditHandler getHBRelationAfterEditHandler() {
/*  696 */     if (context.get("HBRelationAfterEditHandler") != null)
/*  697 */       return (nc.ui.arap.viewhandler.HBRelationAfterEditHandler)context.get("HBRelationAfterEditHandler");
/*  698 */     nc.ui.arap.viewhandler.HBRelationAfterEditHandler bean = new nc.ui.arap.viewhandler.HBRelationAfterEditHandler();
/*  699 */     context.put("HBRelationAfterEditHandler", bean);
/*  700 */     bean.setHtob(getArapH2B());
/*  701 */     bean.setModel(getManageAppModel());
/*  702 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  703 */     invokeInitializingBean(bean);
/*  704 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.HeadCurrTypeAfterEditHandler getHeadCurrTypeAfterEditHandler() {
/*  708 */     if (context.get("HeadCurrTypeAfterEditHandler") != null)
/*  709 */       return (nc.ui.arap.viewhandler.cardafter.HeadCurrTypeAfterEditHandler)context.get("HeadCurrTypeAfterEditHandler");
/*  710 */     nc.ui.arap.viewhandler.cardafter.HeadCurrTypeAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.HeadCurrTypeAfterEditHandler();
/*  711 */     context.put("HeadCurrTypeAfterEditHandler", bean);
/*  712 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  713 */     invokeInitializingBean(bean);
/*  714 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.ObjTypeHeadAfterEdithandler getObjTypeHeadAfterEdithandler() {
/*  718 */     if (context.get("ObjTypeHeadAfterEdithandler") != null)
/*  719 */       return (nc.ui.arap.viewhandler.cardafter.ObjTypeHeadAfterEdithandler)context.get("ObjTypeHeadAfterEdithandler");
/*  720 */     nc.ui.arap.viewhandler.cardafter.ObjTypeHeadAfterEdithandler bean = new nc.ui.arap.viewhandler.cardafter.ObjTypeHeadAfterEdithandler();
/*  721 */     context.put("ObjTypeHeadAfterEdithandler", bean);
/*  722 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  723 */     invokeInitializingBean(bean);
/*  724 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.BodyCheckNoAfterEditHandler getBodyCheckNoAfterEditHandler() {
/*  728 */     if (context.get("BodyCheckNoAfterEditHandler") != null)
/*  729 */       return (nc.ui.arap.viewhandler.cardafter.BodyCheckNoAfterEditHandler)context.get("BodyCheckNoAfterEditHandler");
/*  730 */     nc.ui.arap.viewhandler.cardafter.BodyCheckNoAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.BodyCheckNoAfterEditHandler();
/*  731 */     context.put("BodyCheckNoAfterEditHandler", bean);
/*  732 */     bean.setModel(getManageAppModel());
/*  733 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  734 */     invokeInitializingBean(bean);
/*  735 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.BodyMaterialAfterEditHandler getBodyMaterialAfterEditHandler() {
/*  739 */     if (context.get("BodyMaterialAfterEditHandler") != null)
/*  740 */       return (nc.ui.arap.viewhandler.cardafter.BodyMaterialAfterEditHandler)context.get("BodyMaterialAfterEditHandler");
/*  741 */     nc.ui.arap.viewhandler.cardafter.BodyMaterialAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.BodyMaterialAfterEditHandler();
/*  742 */     context.put("BodyMaterialAfterEditHandler", bean);
/*  743 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  744 */     invokeInitializingBean(bean);
/*  745 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.BodyCrossCheckBeforeHandler getBodyCrossCheckBeforeHandler() {
/*  749 */     if (context.get("BodyCrossCheckBeforeHandler") != null)
/*  750 */       return (nc.ui.arap.viewhandler.cardbefore.BodyCrossCheckBeforeHandler)context.get("BodyCrossCheckBeforeHandler");
/*  751 */     nc.ui.arap.viewhandler.cardbefore.BodyCrossCheckBeforeHandler bean = new nc.ui.arap.viewhandler.cardbefore.BodyCrossCheckBeforeHandler();
/*  752 */     context.put("BodyCrossCheckBeforeHandler", bean);
/*  753 */     bean.setModel(getManageAppModel());
/*  754 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  755 */     invokeInitializingBean(bean);
/*  756 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.HeadCrossCheckBeforeHandler getHeadCrossCheckBeforeHandler() {
/*  760 */     if (context.get("HeadCrossCheckBeforeHandler") != null)
/*  761 */       return (nc.ui.arap.viewhandler.cardbefore.HeadCrossCheckBeforeHandler)context.get("HeadCrossCheckBeforeHandler");
/*  762 */     nc.ui.arap.viewhandler.cardbefore.HeadCrossCheckBeforeHandler bean = new nc.ui.arap.viewhandler.cardbefore.HeadCrossCheckBeforeHandler();
/*  763 */     context.put("HeadCrossCheckBeforeHandler", bean);
/*  764 */     bean.setModel(getManageAppModel());
/*  765 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  766 */     invokeInitializingBean(bean);
/*  767 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.list.LoadBillListTemplate getLoadBillListTemplate() {
/*  771 */     if (context.get("LoadBillListTemplate") != null)
/*  772 */       return (nc.ui.arap.viewhandler.list.LoadBillListTemplate)context.get("LoadBillListTemplate");
/*  773 */     nc.ui.arap.viewhandler.list.LoadBillListTemplate bean = new nc.ui.arap.viewhandler.list.LoadBillListTemplate();
/*  774 */     context.put("LoadBillListTemplate", bean);
/*  775 */     bean.setModel(getManageAppModel());
/*  776 */     bean.setBlpsp(getListScaleProcessor());
/*  777 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  778 */     invokeInitializingBean(bean);
/*  779 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.list.ListHeadRowChangeListener getListHeadRowChangeListener() {
/*  783 */     if (context.get("ListHeadRowChangeListener") != null)
/*  784 */       return (nc.ui.arap.viewhandler.list.ListHeadRowChangeListener)context.get("ListHeadRowChangeListener");
/*  785 */     nc.ui.arap.viewhandler.list.ListHeadRowChangeListener bean = new nc.ui.arap.viewhandler.list.ListHeadRowChangeListener();
/*  786 */     context.put("ListHeadRowChangeListener", bean);
/*  787 */     bean.setBlpsp(getListScaleProcessor());
/*  788 */     bean.setBillFormEditor(getBillFormEditor());
/*  789 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  790 */     invokeInitializingBean(bean);
/*  791 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.list.ListHeadModelListener getListHeadModelListener() {
/*  795 */     if (context.get("ListHeadModelListener") != null)
/*  796 */       return (nc.ui.arap.viewhandler.list.ListHeadModelListener)context.get("ListHeadModelListener");
/*  797 */     nc.ui.arap.viewhandler.list.ListHeadModelListener bean = new nc.ui.arap.viewhandler.list.ListHeadModelListener();
/*  798 */     context.put("ListHeadModelListener", bean);
/*  799 */     bean.setBlpsp(getListScaleProcessor());
/*  800 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  801 */     invokeInitializingBean(bean);
/*  802 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.BodyBankAccBeforeEditHandler getBodyBankAccBeforeEditHandler() {
/*  806 */     if (context.get("BodyBankAccBeforeEditHandler") != null)
/*  807 */       return (nc.ui.arap.viewhandler.cardbefore.BodyBankAccBeforeEditHandler)context.get("BodyBankAccBeforeEditHandler");
/*  808 */     nc.ui.arap.viewhandler.cardbefore.BodyBankAccBeforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.BodyBankAccBeforeEditHandler();
/*  809 */     context.put("BodyBankAccBeforeEditHandler", bean);
/*  810 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  811 */     invokeInitializingBean(bean);
/*  812 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.BodySoBilltypeBeforeEditHandler getBodySoBilltypeBeforeEditHandler() {
/*  816 */     if (context.get("BodySoBilltypeBeforeEditHandler") != null)
/*  817 */       return (nc.ui.arap.viewhandler.cardbefore.BodySoBilltypeBeforeEditHandler)context.get("BodySoBilltypeBeforeEditHandler");
/*  818 */     nc.ui.arap.viewhandler.cardbefore.BodySoBilltypeBeforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.BodySoBilltypeBeforeEditHandler();
/*  819 */     context.put("BodySoBilltypeBeforeEditHandler", bean);
/*  820 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  821 */     invokeInitializingBean(bean);
/*  822 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.BodyAccountRefBeforeEditHandler getBodyAccountRefBeforeEditHandler() {
/*  826 */     if (context.get("BodyAccountRefBeforeEditHandler") != null)
/*  827 */       return (nc.ui.arap.viewhandler.cardbefore.BodyAccountRefBeforeEditHandler)context.get("BodyAccountRefBeforeEditHandler");
/*  828 */     nc.ui.arap.viewhandler.cardbefore.BodyAccountRefBeforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.BodyAccountRefBeforeEditHandler();
/*  829 */     context.put("BodyAccountRefBeforeEditHandler", bean);
/*  830 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  831 */     invokeInitializingBean(bean);
/*  832 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.HeadAccountRefBeforeEditHandler getHeadAccountRefBeforeEditHandler() {
/*  836 */     if (context.get("HeadAccountRefBeforeEditHandler") != null)
/*  837 */       return (nc.ui.arap.viewhandler.cardbefore.HeadAccountRefBeforeEditHandler)context.get("HeadAccountRefBeforeEditHandler");
/*  838 */     nc.ui.arap.viewhandler.cardbefore.HeadAccountRefBeforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.HeadAccountRefBeforeEditHandler();
/*  839 */     context.put("HeadAccountRefBeforeEditHandler", bean);
/*  840 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  841 */     invokeInitializingBean(bean);
/*  842 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.BodyCostCenterRefBeforeEditHandler getBodyCostCenterRefBeforeEditHandler() {
/*  846 */     if (context.get("BodyCostCenterRefBeforeEditHandler") != null)
/*  847 */       return (nc.ui.arap.viewhandler.cardbefore.BodyCostCenterRefBeforeEditHandler)context.get("BodyCostCenterRefBeforeEditHandler");
/*  848 */     nc.ui.arap.viewhandler.cardbefore.BodyCostCenterRefBeforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.BodyCostCenterRefBeforeEditHandler();
/*  849 */     context.put("BodyCostCenterRefBeforeEditHandler", bean);
/*  850 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  851 */     invokeInitializingBean(bean);
/*  852 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.HeadCostCenterRefBeforeEditHandler getHeadCostCenterRefBeforeEditHandler() {
/*  856 */     if (context.get("HeadCostCenterRefBeforeEditHandler") != null)
/*  857 */       return (nc.ui.arap.viewhandler.cardbefore.HeadCostCenterRefBeforeEditHandler)context.get("HeadCostCenterRefBeforeEditHandler");
/*  858 */     nc.ui.arap.viewhandler.cardbefore.HeadCostCenterRefBeforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.HeadCostCenterRefBeforeEditHandler();
/*  859 */     context.put("HeadCostCenterRefBeforeEditHandler", bean);
/*  860 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  861 */     invokeInitializingBean(bean);
/*  862 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.BodyFreeCustBeforeEditHandler getBodyFreeCustBeforeEditHandler() {
/*  866 */     if (context.get("BodyFreeCustBeforeEditHandler") != null)
/*  867 */       return (nc.ui.arap.viewhandler.cardbefore.BodyFreeCustBeforeEditHandler)context.get("BodyFreeCustBeforeEditHandler");
/*  868 */     nc.ui.arap.viewhandler.cardbefore.BodyFreeCustBeforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.BodyFreeCustBeforeEditHandler();
/*  869 */     context.put("BodyFreeCustBeforeEditHandler", bean);
/*  870 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  871 */     invokeInitializingBean(bean);
/*  872 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.BodyProjectBeforeEditHandler getBodyProjectBeforeEditHandler() {
/*  876 */     if (context.get("BodyProjectBeforeEditHandler") != null)
/*  877 */       return (nc.ui.arap.viewhandler.cardbefore.BodyProjectBeforeEditHandler)context.get("BodyProjectBeforeEditHandler");
/*  878 */     nc.ui.arap.viewhandler.cardbefore.BodyProjectBeforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.BodyProjectBeforeEditHandler();
/*  879 */     context.put("BodyProjectBeforeEditHandler", bean);
/*  880 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  881 */     invokeInitializingBean(bean);
/*  882 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.HeadSummaryBeforeEditHandler getHeadSummaryBeforeEditHandler() {
/*  886 */     if (context.get("HeadSummaryBeforeEditHandler") != null)
/*  887 */       return (nc.ui.arap.viewhandler.cardbefore.HeadSummaryBeforeEditHandler)context.get("HeadSummaryBeforeEditHandler");
/*  888 */     nc.ui.arap.viewhandler.cardbefore.HeadSummaryBeforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.HeadSummaryBeforeEditHandler();
/*  889 */     context.put("HeadSummaryBeforeEditHandler", bean);
/*  890 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  891 */     invokeInitializingBean(bean);
/*  892 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.BodySummaryBeforeEditHandler getBodySummaryBeforeEditHandler() {
/*  896 */     if (context.get("BodySummaryBeforeEditHandler") != null)
/*  897 */       return (nc.ui.arap.viewhandler.cardbefore.BodySummaryBeforeEditHandler)context.get("BodySummaryBeforeEditHandler");
/*  898 */     nc.ui.arap.viewhandler.cardbefore.BodySummaryBeforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.BodySummaryBeforeEditHandler();
/*  899 */     context.put("BodySummaryBeforeEditHandler", bean);
/*  900 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  901 */     invokeInitializingBean(bean);
/*  902 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.HeadCostCenterAfterEditHandler getHeadCostCenterAfterEditHandler() {
/*  906 */     if (context.get("HeadCostCenterAfterEditHandler") != null)
/*  907 */       return (nc.ui.arap.viewhandler.cardafter.HeadCostCenterAfterEditHandler)context.get("HeadCostCenterAfterEditHandler");
/*  908 */     nc.ui.arap.viewhandler.cardafter.HeadCostCenterAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.HeadCostCenterAfterEditHandler();
/*  909 */     context.put("HeadCostCenterAfterEditHandler", bean);
/*  910 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  911 */     invokeInitializingBean(bean);
/*  912 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.BodyCostCenterAfterEditHandler getBodyCostCenterAfterEditHandler() {
/*  916 */     if (context.get("BodyCostCenterAfterEditHandler") != null)
/*  917 */       return (nc.ui.arap.viewhandler.cardafter.BodyCostCenterAfterEditHandler)context.get("BodyCostCenterAfterEditHandler");
/*  918 */     nc.ui.arap.viewhandler.cardafter.BodyCostCenterAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.BodyCostCenterAfterEditHandler();
/*  919 */     context.put("BodyCostCenterAfterEditHandler", bean);
/*  920 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  921 */     invokeInitializingBean(bean);
/*  922 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.BodyTaxcodeBeforeEditHandler getBodyTaxcodeBeforeEditHandler() {
/*  926 */     if (context.get("BodyTaxcodeBeforeEditHandler") != null)
/*  927 */       return (nc.ui.arap.viewhandler.cardbefore.BodyTaxcodeBeforeEditHandler)context.get("BodyTaxcodeBeforeEditHandler");
/*  928 */     nc.ui.arap.viewhandler.cardbefore.BodyTaxcodeBeforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.BodyTaxcodeBeforeEditHandler();
/*  929 */     context.put("BodyTaxcodeBeforeEditHandler", bean);
/*  930 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  931 */     invokeInitializingBean(bean);
/*  932 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.HeadVersionRefBeforeEditHandler getHeadVersionRefBeforeEditHandler() {
/*  936 */     if (context.get("HeadVersionRefBeforeEditHandler") != null)
/*  937 */       return (nc.ui.arap.viewhandler.cardbefore.HeadVersionRefBeforeEditHandler)context.get("HeadVersionRefBeforeEditHandler");
/*  938 */     nc.ui.arap.viewhandler.cardbefore.HeadVersionRefBeforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.HeadVersionRefBeforeEditHandler();
/*  939 */     context.put("HeadVersionRefBeforeEditHandler", bean);
/*  940 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  941 */     invokeInitializingBean(bean);
/*  942 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.BodyVersionRefBeforeEditHandler getBodyVersionRefBeforeEditHandler() {
/*  946 */     if (context.get("BodyVersionRefBeforeEditHandler") != null)
/*  947 */       return (nc.ui.arap.viewhandler.cardbefore.BodyVersionRefBeforeEditHandler)context.get("BodyVersionRefBeforeEditHandler");
/*  948 */     nc.ui.arap.viewhandler.cardbefore.BodyVersionRefBeforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.BodyVersionRefBeforeEditHandler();
/*  949 */     context.put("BodyVersionRefBeforeEditHandler", bean);
/*  950 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  951 */     invokeInitializingBean(bean);
/*  952 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.HeadVersionRefAfterEditHandler getHeadVersionRefAfterEditHandler() {
/*  956 */     if (context.get("HeadVersionRefAfterEditHandler") != null)
/*  957 */       return (nc.ui.arap.viewhandler.cardafter.HeadVersionRefAfterEditHandler)context.get("HeadVersionRefAfterEditHandler");
/*  958 */     nc.ui.arap.viewhandler.cardafter.HeadVersionRefAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.HeadVersionRefAfterEditHandler();
/*  959 */     context.put("HeadVersionRefAfterEditHandler", bean);
/*  960 */     bean.setHtob(getArapH2B());
/*  961 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  962 */     invokeInitializingBean(bean);
/*  963 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.BodyVersionRefAfterEditHandler getBodyVersionRefAfterEditHandler() {
/*  967 */     if (context.get("BodyVersionRefAfterEditHandler") != null)
/*  968 */       return (nc.ui.arap.viewhandler.cardafter.BodyVersionRefAfterEditHandler)context.get("BodyVersionRefAfterEditHandler");
/*  969 */     nc.ui.arap.viewhandler.cardafter.BodyVersionRefAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.BodyVersionRefAfterEditHandler();
/*  970 */     context.put("BodyVersionRefAfterEditHandler", bean);
/*  971 */     bean.setHtob(getArapH2B());
/*  972 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  973 */     invokeInitializingBean(bean);
/*  974 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.PayTermBodyBeforeEditHandler getPayTermBodyBeforeEditHandler() {
/*  978 */     if (context.get("PayTermBodyBeforeEditHandler") != null)
/*  979 */       return (nc.ui.arap.viewhandler.cardbefore.PayTermBodyBeforeEditHandler)context.get("PayTermBodyBeforeEditHandler");
/*  980 */     nc.ui.arap.viewhandler.cardbefore.PayTermBodyBeforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.PayTermBodyBeforeEditHandler();
/*  981 */     context.put("PayTermBodyBeforeEditHandler", bean);
/*  982 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  983 */     invokeInitializingBean(bean);
/*  984 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.BodyChecNoRefBeforeEditHandler getBodyChecNoRefBeforeEditHandler() {
/*  988 */     if (context.get("BodyChecNoRefBeforeEditHandler") != null)
/*  989 */       return (nc.ui.arap.viewhandler.cardbefore.BodyChecNoRefBeforeEditHandler)context.get("BodyChecNoRefBeforeEditHandler");
/*  990 */     nc.ui.arap.viewhandler.cardbefore.BodyChecNoRefBeforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.BodyChecNoRefBeforeEditHandler();
/*  991 */     context.put("BodyChecNoRefBeforeEditHandler", bean);
/*  992 */     bean.setIsGatherbill(Boolean.valueOf(true));
/*  993 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  994 */     invokeInitializingBean(bean);
/*  995 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.BodyChecNoRefBeforeEditHandler getBodyChecNoRefBeforeEditHandler4Pay() {
/*  999 */     if (context.get("BodyChecNoRefBeforeEditHandler4Pay") != null)
/* 1000 */       return (nc.ui.arap.viewhandler.cardbefore.BodyChecNoRefBeforeEditHandler)context.get("BodyChecNoRefBeforeEditHandler4Pay");
/* 1001 */     nc.ui.arap.viewhandler.cardbefore.BodyChecNoRefBeforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.BodyChecNoRefBeforeEditHandler();
/* 1002 */     context.put("BodyChecNoRefBeforeEditHandler4Pay", bean);
/* 1003 */     bean.setIsGatherbill(Boolean.valueOf(false));
/* 1004 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1005 */     invokeInitializingBean(bean);
/* 1006 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.BodyCuspAfterEditHandler getBodyCuspAfterEditHandler() {
/* 1010 */     if (context.get("BodyCuspAfterEditHandler") != null)
/* 1011 */       return (nc.ui.arap.viewhandler.cardafter.BodyCuspAfterEditHandler)context.get("BodyCuspAfterEditHandler");
/* 1012 */     nc.ui.arap.viewhandler.cardafter.BodyCuspAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.BodyCuspAfterEditHandler();
/* 1013 */     context.put("BodyCuspAfterEditHandler", bean);
/* 1014 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1015 */     invokeInitializingBean(bean);
/* 1016 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.BodyDeptAfterEditHandler getBodyDeptAfterEditHandler() {
/* 1020 */     if (context.get("BodyDeptAfterEditHandler") != null)
/* 1021 */       return (nc.ui.arap.viewhandler.cardafter.BodyDeptAfterEditHandler)context.get("BodyDeptAfterEditHandler");
/* 1022 */     nc.ui.arap.viewhandler.cardafter.BodyDeptAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.BodyDeptAfterEditHandler();
/* 1023 */     context.put("BodyDeptAfterEditHandler", bean);
/* 1024 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1025 */     invokeInitializingBean(bean);
/* 1026 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.BodyPsnDocAfterEditHandler getBodyPsnDocAfterEditHandler() {
/* 1030 */     if (context.get("BodyPsnDocAfterEditHandler") != null)
/* 1031 */       return (nc.ui.arap.viewhandler.cardafter.BodyPsnDocAfterEditHandler)context.get("BodyPsnDocAfterEditHandler");
/* 1032 */     nc.ui.arap.viewhandler.cardafter.BodyPsnDocAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.BodyPsnDocAfterEditHandler();
/* 1033 */     context.put("BodyPsnDocAfterEditHandler", bean);
/* 1034 */     bean.setHtob(getArapH2B());
/* 1035 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1036 */     invokeInitializingBean(bean);
/* 1037 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.BodyLocalMoneyAfterEditHandler getBodyLocalMoneyAfterEditHandler() {
/* 1041 */     if (context.get("BodyLocalMoneyAfterEditHandler") != null)
/* 1042 */       return (nc.ui.arap.viewhandler.cardafter.BodyLocalMoneyAfterEditHandler)context.get("BodyLocalMoneyAfterEditHandler");
/* 1043 */     nc.ui.arap.viewhandler.cardafter.BodyLocalMoneyAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.BodyLocalMoneyAfterEditHandler();
/* 1044 */     context.put("BodyLocalMoneyAfterEditHandler", bean);
/* 1045 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1046 */     invokeInitializingBean(bean);
/* 1047 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.BodyRateAfterEditHandler getBodyRateAfterEditHandler() {
/* 1051 */     if (context.get("BodyRateAfterEditHandler") != null)
/* 1052 */       return (nc.ui.arap.viewhandler.cardafter.BodyRateAfterEditHandler)context.get("BodyRateAfterEditHandler");
/* 1053 */     nc.ui.arap.viewhandler.cardafter.BodyRateAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.BodyRateAfterEditHandler();
/* 1054 */     context.put("BodyRateAfterEditHandler", bean);
/* 1055 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1056 */     invokeInitializingBean(bean);
/* 1057 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.BodyEuroAfterEditHandler getBodyEuroAfterEditHandler() {
/* 1061 */     if (context.get("BodyEuroAfterEditHandler") != null)
/* 1062 */       return (nc.ui.arap.viewhandler.cardafter.BodyEuroAfterEditHandler)context.get("BodyEuroAfterEditHandler");
/* 1063 */     nc.ui.arap.viewhandler.cardafter.BodyEuroAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.BodyEuroAfterEditHandler();
/* 1064 */     context.put("BodyEuroAfterEditHandler", bean);
/* 1065 */     bean.setModel(getManageAppModel());
/* 1066 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1067 */     invokeInitializingBean(bean);
/* 1068 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.HeadEuroAfterEditHandler getHeadEuroAfterEditHandler() {
/* 1072 */     if (context.get("HeadEuroAfterEditHandler") != null)
/* 1073 */       return (nc.ui.arap.viewhandler.cardafter.HeadEuroAfterEditHandler)context.get("HeadEuroAfterEditHandler");
/* 1074 */     nc.ui.arap.viewhandler.cardafter.HeadEuroAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.HeadEuroAfterEditHandler();
/* 1075 */     context.put("HeadEuroAfterEditHandler", bean);
/* 1076 */     bean.setModel(getManageAppModel());
/* 1077 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1078 */     invokeInitializingBean(bean);
/* 1079 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.BodyMoneyBalanceAfterEditHandler getBodyMoneyBalanceAfterEditHandler() {
/* 1083 */     if (context.get("BodyMoneyBalanceAfterEditHandler") != null)
/* 1084 */       return (nc.ui.arap.viewhandler.cardafter.BodyMoneyBalanceAfterEditHandler)context.get("BodyMoneyBalanceAfterEditHandler");
/* 1085 */     nc.ui.arap.viewhandler.cardafter.BodyMoneyBalanceAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.BodyMoneyBalanceAfterEditHandler();
/* 1086 */     context.put("BodyMoneyBalanceAfterEditHandler", bean);
/* 1087 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1088 */     invokeInitializingBean(bean);
/* 1089 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.EnableCtrlBodyAfterEditHandler getEnableCtrlBodyAfterEditHandler() {
/* 1093 */     if (context.get("EnableCtrlBodyAfterEditHandler") != null)
/* 1094 */       return (nc.ui.arap.viewhandler.cardafter.EnableCtrlBodyAfterEditHandler)context.get("EnableCtrlBodyAfterEditHandler");
/* 1095 */     nc.ui.arap.viewhandler.cardafter.EnableCtrlBodyAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.EnableCtrlBodyAfterEditHandler();
/* 1096 */     context.put("EnableCtrlBodyAfterEditHandler", bean);
/* 1097 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1098 */     invokeInitializingBean(bean);
/* 1099 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.HeadBankAccAfterEditHandler getHeadBankAccAfterEditHandler() {
/* 1103 */     if (context.get("HeadBankAccAfterEditHandler") != null)
/* 1104 */       return (nc.ui.arap.viewhandler.cardafter.HeadBankAccAfterEditHandler)context.get("HeadBankAccAfterEditHandler");
/* 1105 */     nc.ui.arap.viewhandler.cardafter.HeadBankAccAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.HeadBankAccAfterEditHandler();
/* 1106 */     context.put("HeadBankAccAfterEditHandler", bean);
/* 1107 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1108 */     invokeInitializingBean(bean);
/* 1109 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.BodyBankAccAfterEditHandler getBodyBankAccAfterEditHandler() {
/* 1113 */     if (context.get("BodyBankAccAfterEditHandler") != null)
/* 1114 */       return (nc.ui.arap.viewhandler.cardafter.BodyBankAccAfterEditHandler)context.get("BodyBankAccAfterEditHandler");
/* 1115 */     nc.ui.arap.viewhandler.cardafter.BodyBankAccAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.BodyBankAccAfterEditHandler();
/* 1116 */     context.put("BodyBankAccAfterEditHandler", bean);
/* 1117 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1118 */     invokeInitializingBean(bean);
/* 1119 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.HeadPsnDocAfterEditHandler getHeadPsnDocAfterEditHandler() {
/* 1123 */     if (context.get("HeadPsnDocAfterEditHandler") != null)
/* 1124 */       return (nc.ui.arap.viewhandler.cardafter.HeadPsnDocAfterEditHandler)context.get("HeadPsnDocAfterEditHandler");
/* 1125 */     nc.ui.arap.viewhandler.cardafter.HeadPsnDocAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.HeadPsnDocAfterEditHandler();
/* 1126 */     context.put("HeadPsnDocAfterEditHandler", bean);
/* 1127 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1128 */     invokeInitializingBean(bean);
/* 1129 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.card.CardBodyAfterRowEditHandler getCardBodyAfterRowEditHandler() {
/* 1133 */     if (context.get("CardBodyAfterRowEditHandler") != null)
/* 1134 */       return (nc.ui.arap.viewhandler.card.CardBodyAfterRowEditHandler)context.get("CardBodyAfterRowEditHandler");
/* 1135 */     nc.ui.arap.viewhandler.card.CardBodyAfterRowEditHandler bean = new nc.ui.arap.viewhandler.card.CardBodyAfterRowEditHandler();
/* 1136 */     context.put("CardBodyAfterRowEditHandler", bean);
/* 1137 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1138 */     invokeInitializingBean(bean);
/* 1139 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.card.CardBodyRowChangeHandler getCardBodyRowChangeHandler() {
/* 1143 */     if (context.get("CardBodyRowChangeHandler") != null)
/* 1144 */       return (nc.ui.arap.viewhandler.card.CardBodyRowChangeHandler)context.get("CardBodyRowChangeHandler");
/* 1145 */     nc.ui.arap.viewhandler.card.CardBodyRowChangeHandler bean = new nc.ui.arap.viewhandler.card.CardBodyRowChangeHandler();
/* 1146 */     context.put("CardBodyRowChangeHandler", bean);
/* 1147 */     bean.setModel(getManageAppModel());
/* 1148 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1149 */     invokeInitializingBean(bean);
/* 1150 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.view.RowNoMediator getRowNoMediator() {
/* 1154 */     if (context.get("RowNoMediator") != null)
/* 1155 */       return (nc.ui.arap.view.RowNoMediator)context.get("RowNoMediator");
/* 1156 */     nc.ui.arap.view.RowNoMediator bean = new nc.ui.arap.view.RowNoMediator();
/* 1157 */     context.put("RowNoMediator", bean);
/* 1158 */     bean.setModel(getManageAppModel());
/* 1159 */     bean.setEditor(getBillFormEditor());
/* 1160 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1161 */     invokeInitializingBean(bean);
/* 1162 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.HeadCuspAfterEditHandler getHeadCuspAfterEditHandler() {
/* 1166 */     if (context.get("HeadCuspAfterEditHandler") != null)
/* 1167 */       return (nc.ui.arap.viewhandler.cardafter.HeadCuspAfterEditHandler)context.get("HeadCuspAfterEditHandler");
/* 1168 */     nc.ui.arap.viewhandler.cardafter.HeadCuspAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.HeadCuspAfterEditHandler();
/* 1169 */     context.put("HeadCuspAfterEditHandler", bean);
/* 1170 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1171 */     invokeInitializingBean(bean);
/* 1172 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.HeadDeptAfterEditHandler getHeadDeptAfterEditHandler() {
/* 1176 */     if (context.get("HeadDeptAfterEditHandler") != null)
/* 1177 */       return (nc.ui.arap.viewhandler.cardafter.HeadDeptAfterEditHandler)context.get("HeadDeptAfterEditHandler");
/* 1178 */     nc.ui.arap.viewhandler.cardafter.HeadDeptAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.HeadDeptAfterEditHandler();
/* 1179 */     context.put("HeadDeptAfterEditHandler", bean);
/* 1180 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1181 */     invokeInitializingBean(bean);
/* 1182 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.BodyAutoAddLineBeforeEditHandler getBodyAutoAddLineBeforeEditHandler() {
/* 1186 */     if (context.get("BodyAutoAddLineBeforeEditHandler") != null)
/* 1187 */       return (nc.ui.arap.viewhandler.cardbefore.BodyAutoAddLineBeforeEditHandler)context.get("BodyAutoAddLineBeforeEditHandler");
/* 1188 */     nc.ui.arap.viewhandler.cardbefore.BodyAutoAddLineBeforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.BodyAutoAddLineBeforeEditHandler();
/* 1189 */     context.put("BodyAutoAddLineBeforeEditHandler", bean);
/* 1190 */     bean.setAddLineAction(getAddLineAction());
/* 1191 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1192 */     invokeInitializingBean(bean);
/* 1193 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.sideforms.TaxForm getTaxForm() {
/* 1197 */     if (context.get("taxForm") != null)
/* 1198 */       return (nc.ui.arap.sideforms.TaxForm)context.get("taxForm");
/* 1199 */     nc.ui.arap.sideforms.TaxForm bean = new nc.ui.arap.sideforms.TaxForm();
/* 1200 */     context.put("taxForm", bean);
/* 1201 */     bean.setModel(getManageAppModel());
/* 1202 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1203 */     invokeInitializingBean(bean);
/* 1204 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.BodyPsnDocBeforeEditHandler getBodyPsnDocBeforeEditHandler() {
/* 1208 */     if (context.get("BodyPsnDocBeforeEditHandler") != null)
/* 1209 */       return (nc.ui.arap.viewhandler.cardbefore.BodyPsnDocBeforeEditHandler)context.get("BodyPsnDocBeforeEditHandler");
/* 1210 */     nc.ui.arap.viewhandler.cardbefore.BodyPsnDocBeforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.BodyPsnDocBeforeEditHandler();
/* 1211 */     context.put("BodyPsnDocBeforeEditHandler", bean);
/* 1212 */     bean.setModel(getManageAppModel());
/* 1213 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1214 */     invokeInitializingBean(bean);
/* 1215 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.HeadPsnDocBeforeEditHandler getHeadPsnDocBeforeEditHandler() {
/* 1219 */     if (context.get("HeadPsnDocBeforeEditHandler") != null)
/* 1220 */       return (nc.ui.arap.viewhandler.cardbefore.HeadPsnDocBeforeEditHandler)context.get("HeadPsnDocBeforeEditHandler");
/* 1221 */     nc.ui.arap.viewhandler.cardbefore.HeadPsnDocBeforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.HeadPsnDocBeforeEditHandler();
/* 1222 */     context.put("HeadPsnDocBeforeEditHandler", bean);
/* 1223 */     bean.setModel(getManageAppModel());
/* 1224 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1225 */     invokeInitializingBean(bean);
/* 1226 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.HeadBillDateAfterEditHandler getHeadBillDateAfterEditHandler() {
/* 1230 */     if (context.get("HeadBillDateAfterEditHandler") != null)
/* 1231 */       return (nc.ui.arap.viewhandler.cardafter.HeadBillDateAfterEditHandler)context.get("HeadBillDateAfterEditHandler");
/* 1232 */     nc.ui.arap.viewhandler.cardafter.HeadBillDateAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.HeadBillDateAfterEditHandler();
/* 1233 */     context.put("HeadBillDateAfterEditHandler", bean);
/* 1234 */     bean.setModel(getManageAppModel());
/* 1235 */     bean.setHtob(getArapH2B());
/* 1236 */     bean.setBillform(getBillFormEditor());
/* 1237 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1238 */     invokeInitializingBean(bean);
/* 1239 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.uif2.components.widget.BesideWidget getBesidewidget() {
/* 1243 */     if (context.get("besidewidget") != null)
/* 1244 */       return (nc.ui.uif2.components.widget.BesideWidget)context.get("besidewidget");
/* 1245 */     nc.ui.uif2.components.widget.BesideWidget bean = new nc.ui.uif2.components.widget.BesideWidget();
/* 1246 */     context.put("besidewidget", bean);
/* 1247 */     bean.setBesideWidgetlets(getManagedList7());
/* 1248 */     bean.setContext(getContext());
/* 1249 */     bean.setShowAllAction(getShowAllAction());
/* 1250 */     bean.initUI();
/* 1251 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1252 */     invokeInitializingBean(bean);
/* 1253 */     return bean;
/*      */   }
/*      */   
/* 1256 */   private List getManagedList7() { List list = new ArrayList();list.add(getTermForm());list.add(getCreditForm());return list;
/*      */   }
/*      */   
/* 1259 */   public nc.ui.uif2.components.widget.MultiBesideWidget getMultiBesideWidget() { if (context.get("multiBesideWidget") != null)
/* 1260 */       return (nc.ui.uif2.components.widget.MultiBesideWidget)context.get("multiBesideWidget");
/* 1261 */     nc.ui.uif2.components.widget.MultiBesideWidget bean = new nc.ui.uif2.components.widget.MultiBesideWidget();
/* 1262 */     context.put("multiBesideWidget", bean);
/* 1263 */     bean.setBesideWidgetInfoMap(getManagedMap0());
/* 1264 */     bean.setContext(getContext());
/* 1265 */     bean.initUI();
/* 1266 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1267 */     invokeInitializingBean(bean);
/* 1268 */     return bean;
/*      */   }
/*      */   
/* 1271 */   private Map getManagedMap0() { Map map = new java.util.HashMap();map.put(getI18nFB_1247d6d(), getManagedList8());map.put(getI18nFB_a6a18b(), getManagedList9());return map;
/*      */   }
/*      */   
/* 1274 */   private String getI18nFB_1247d6d() { if (context.get("nc.ui.uif2.I18nFB#1247d6d") != null)
/* 1275 */       return (String)context.get("nc.ui.uif2.I18nFB#1247d6d");
/* 1276 */     I18nFB bean = new I18nFB();
/* 1277 */     context.put("&nc.ui.uif2.I18nFB#1247d6d", bean);bean.setResDir("uif2");
/* 1278 */     bean.setResId("BesideWidget-000001");
/* 1279 */     bean.setDefaultValue("������Ϣ");
/* 1280 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1281 */     invokeInitializingBean(bean);
/*      */     try {
/* 1283 */       Object product = bean.getObject();
/* 1284 */       context.put("nc.ui.uif2.I18nFB#1247d6d", product);
/* 1285 */       return (String)product;
/*      */     } catch (Exception e) {
/* 1287 */       throw new RuntimeException(e); } }
/*      */   
/* 1289 */   private List getManagedList8() { List list = new ArrayList();list.add(getTermForm());list.add(getCreditForm());list.add(getShowAllActionForm());return list;
/*      */   }
/*      */   
/* 1292 */   private String getI18nFB_a6a18b() { if (context.get("nc.ui.uif2.I18nFB#a6a18b") != null)
/* 1293 */       return (String)context.get("nc.ui.uif2.I18nFB#a6a18b");
/* 1294 */     I18nFB bean = new I18nFB();
/* 1295 */     context.put("&nc.ui.uif2.I18nFB#a6a18b", bean);bean.setResDir("uif2");
/* 1296 */     bean.setResId("ActionRegistry-000055");
/* 1297 */     bean.setDefaultValue("����");
/* 1298 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1299 */     invokeInitializingBean(bean);
/*      */     try {
/* 1301 */       Object product = bean.getObject();
/* 1302 */       context.put("nc.ui.uif2.I18nFB#a6a18b", product);
/* 1303 */       return (String)product;
/*      */     } catch (Exception e) {
/* 1305 */       throw new RuntimeException(e); } }
/*      */   
/* 1307 */   private List getManagedList9() { List list = new ArrayList();list.add(getBesideApproveForm());list.add(getBesideHintMessageForm());list.add(getBesideHistoryForm());return list;
/*      */   }
/*      */   
/* 1310 */   public nc.ui.arap.sideforms.ShowAllAction getShowAllAction() { if (context.get("showAllAction") != null)
/* 1311 */       return (nc.ui.arap.sideforms.ShowAllAction)context.get("showAllAction");
/* 1312 */     nc.ui.arap.sideforms.ShowAllAction bean = new nc.ui.arap.sideforms.ShowAllAction();
/* 1313 */     context.put("showAllAction", bean);
/* 1314 */     bean.setMediator(getSideFormMediator());
/* 1315 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1316 */     invokeInitializingBean(bean);
/* 1317 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.sideforms.ArapSideFormMediator getSideFormMediator() {
/* 1321 */     if (context.get("sideFormMediator") != null)
/* 1322 */       return (nc.ui.arap.sideforms.ArapSideFormMediator)context.get("sideFormMediator");
/* 1323 */     nc.ui.arap.sideforms.ArapSideFormMediator bean = new nc.ui.arap.sideforms.ArapSideFormMediator();
/* 1324 */     context.put("sideFormMediator", bean);
/* 1325 */     bean.setSideFormList(getManagedList10());
/* 1326 */     bean.setCardPanel(getBillFormEditor());
/* 1327 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1328 */     invokeInitializingBean(bean);
/* 1329 */     return bean;
/*      */   }
/*      */   
/* 1332 */   private List getManagedList10() { List list = new ArrayList();list.add(getTermForm());list.add(getCreditForm());return list;
/*      */   }
/*      */   
/* 1335 */   public nc.ui.arap.sideforms.TermForm getTermForm() { if (context.get("termForm") != null)
/* 1336 */       return (nc.ui.arap.sideforms.TermForm)context.get("termForm");
/* 1337 */     nc.ui.arap.sideforms.TermForm bean = new nc.ui.arap.sideforms.TermForm();
/* 1338 */     context.put("termForm", bean);
/* 1339 */     bean.setModel(getManageAppModel());
/* 1340 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1341 */     invokeInitializingBean(bean);
/* 1342 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.sideforms.CreditForm getCreditForm() {
/* 1346 */     if (context.get("creditForm") != null)
/* 1347 */       return (nc.ui.arap.sideforms.CreditForm)context.get("creditForm");
/* 1348 */     nc.ui.arap.sideforms.CreditForm bean = new nc.ui.arap.sideforms.CreditForm();
/* 1349 */     context.put("creditForm", bean);
/* 1350 */     bean.setModel(getManageAppModel());
/* 1351 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1352 */     invokeInitializingBean(bean);
/* 1353 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.sideforms.ShowAllActionForm getShowAllActionForm() {
/* 1357 */     if (context.get("showAllActionForm") != null)
/* 1358 */       return (nc.ui.arap.sideforms.ShowAllActionForm)context.get("showAllActionForm");
/* 1359 */     nc.ui.arap.sideforms.ShowAllActionForm bean = new nc.ui.arap.sideforms.ShowAllActionForm();
/* 1360 */     context.put("showAllActionForm", bean);
/* 1361 */     bean.setShowAllAction(getShowAllAction());
/* 1362 */     bean.setModel(getManageAppModel());
/* 1363 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1364 */     invokeInitializingBean(bean);
/* 1365 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.sideforms.ArapBesideApproveForm getBesideApproveForm() {
/* 1369 */     if (context.get("besideApproveForm") != null)
/* 1370 */       return (nc.ui.arap.sideforms.ArapBesideApproveForm)context.get("besideApproveForm");
/* 1371 */     nc.ui.arap.sideforms.ArapBesideApproveForm bean = new nc.ui.arap.sideforms.ArapBesideApproveForm();
/* 1372 */     context.put("besideApproveForm", bean);
/* 1373 */     bean.setModel(getManageAppModel());
/* 1374 */     bean.setBesideapproveAction(getBillApproveAction());
/* 1375 */     bean.setBesideunapproveaction(getBillUnApproveAction());
/* 1376 */     bean.setBillSourceEditor(getBillFormEditor());
/* 1377 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1378 */     invokeInitializingBean(bean);
/* 1379 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.sideforms.ArapBesideHistoryForm getBesideHistoryForm() {
/* 1383 */     if (context.get("besideHistoryForm") != null)
/* 1384 */       return (nc.ui.arap.sideforms.ArapBesideHistoryForm)context.get("besideHistoryForm");
/* 1385 */     nc.ui.arap.sideforms.ArapBesideHistoryForm bean = new nc.ui.arap.sideforms.ArapBesideHistoryForm();
/* 1386 */     context.put("besideHistoryForm", bean);
/* 1387 */     bean.setModel(getManageAppModel());
/* 1388 */     bean.setBillSourceEditor(getBillFormEditor());
/* 1389 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1390 */     invokeInitializingBean(bean);
/* 1391 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.sideforms.ArapBesideHintMessageForm getBesideHintMessageForm() {
/* 1395 */     if (context.get("besideHintMessageForm") != null)
/* 1396 */       return (nc.ui.arap.sideforms.ArapBesideHintMessageForm)context.get("besideHintMessageForm");
/* 1397 */     nc.ui.arap.sideforms.ArapBesideHintMessageForm bean = new nc.ui.arap.sideforms.ArapBesideHintMessageForm();
/* 1398 */     context.put("besideHintMessageForm", bean);
/* 1399 */     bean.setModel(getManageAppModel());
/* 1400 */     bean.setBillSourceEditor(getBillFormEditor());
/* 1401 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1402 */     invokeInitializingBean(bean);
/* 1403 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillApproveAction getBillApproveAction() {
/* 1407 */     if (context.get("BillApproveAction") != null)
/* 1408 */       return (nc.ui.arap.actions.BillApproveAction)context.get("BillApproveAction");
/* 1409 */     nc.ui.arap.actions.BillApproveAction bean = new nc.ui.arap.actions.BillApproveAction();
/* 1410 */     context.put("BillApproveAction", bean);
/* 1411 */     bean.setModel(getManageAppModel());
/* 1412 */     bean.setEditor(getBillFormEditor());
/* 1413 */     bean.setInterceptor(getInterceptor());
/* 1414 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1415 */     invokeInitializingBean(bean);
/* 1416 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillUnApproveAction getBillUnApproveAction() {
/* 1420 */     if (context.get("BillUnApproveAction") != null)
/* 1421 */       return (nc.ui.arap.actions.BillUnApproveAction)context.get("BillUnApproveAction");
/* 1422 */     nc.ui.arap.actions.BillUnApproveAction bean = new nc.ui.arap.actions.BillUnApproveAction();
/* 1423 */     context.put("BillUnApproveAction", bean);
/* 1424 */     bean.setModel(getManageAppModel());
/* 1425 */     bean.setEditor(getBillFormEditor());
/* 1426 */     bean.setInterceptor(getInterceptor());
/* 1427 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1428 */     invokeInitializingBean(bean);
/* 1429 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.pub.remote.RetAddLoader getRetAddLoader() {
/* 1433 */     if (context.get("retAddLoader") != null)
/* 1434 */       return (nc.ui.arap.pub.remote.RetAddLoader)context.get("retAddLoader");
/* 1435 */     nc.ui.arap.pub.remote.RetAddLoader bean = new nc.ui.arap.pub.remote.RetAddLoader();
/* 1436 */     context.put("retAddLoader", bean);
/* 1437 */     bean.setBilltype(getDefBillType());
/* 1438 */     bean.setTranstype(getNodeKeyQry());
/* 1439 */     bean.prepare();
/* 1440 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1441 */     invokeInitializingBean(bean);
/* 1442 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.ArapAddFlowMenuAciton getAddActionGroup() {
/* 1446 */     if (context.get("addActionGroup") != null)
/* 1447 */       return (nc.ui.arap.actions.ArapAddFlowMenuAciton)context.get("addActionGroup");
/* 1448 */     nc.ui.arap.actions.ArapAddFlowMenuAciton bean = new nc.ui.arap.actions.ArapAddFlowMenuAciton(getNodeKeyQry(), getBillFormEditor(), getInterceptor());context.put("addActionGroup", bean);
/* 1449 */     bean.setList(getListView());
/* 1450 */     bean.setLoader(getRetAddLoader());
/* 1451 */     bean.setModel(getManageAppModel());
/* 1452 */     bean.setTransferBillViewProcessor(getTransferProcessor());
/* 1453 */     bean.setOrgChangedImpl(getOrgchange());
/* 1454 */     bean.setCode("add");
/* 1455 */     bean.setName(getI18nFB_1b7b537());
/* 1456 */     bean.refreshChildBtns();
/* 1457 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1458 */     invokeInitializingBean(bean);
/* 1459 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_1b7b537() {
/* 1463 */     if (context.get("nc.ui.uif2.I18nFB#1b7b537") != null)
/* 1464 */       return (String)context.get("nc.ui.uif2.I18nFB#1b7b537");
/* 1465 */     I18nFB bean = new I18nFB();
/* 1466 */     context.put("&nc.ui.uif2.I18nFB#1b7b537", bean);bean.setResDir("pubapp_0");
/* 1467 */     bean.setResId("0pubapp-0121");
/* 1468 */     bean.setDefaultValue("����");
/* 1469 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1470 */     invokeInitializingBean(bean);
/*      */     try {
/* 1472 */       Object product = bean.getObject();
/* 1473 */       context.put("nc.ui.uif2.I18nFB#1b7b537", product);
/* 1474 */       return (String)product;
/*      */     } catch (Exception e) {
/* 1476 */       throw new RuntimeException(e);
/*      */     } }
/*      */   
/* 1479 */   public nc.ui.arap.view.ArapTransferBillViewProcessor getTransferProcessor() { if (context.get("transferProcessor") != null)
/* 1480 */       return (nc.ui.arap.view.ArapTransferBillViewProcessor)context.get("transferProcessor");
/* 1481 */     nc.ui.arap.view.ArapTransferBillViewProcessor bean = new nc.ui.arap.view.ArapTransferBillViewProcessor();
/* 1482 */     context.put("transferProcessor", bean);
/* 1483 */     bean.setList(getListView());
/* 1484 */     bean.setTransferLogic(getTransferLogic());
/* 1485 */     bean.setActionContainer(getListActions());
/* 1486 */     bean.setBillForm(getBillFormEditor());
/* 1487 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1488 */     invokeInitializingBean(bean);
/* 1489 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.pubapp.billref.dest.DefaultBillDataLogic getTransferLogic() {
/* 1493 */     if (context.get("transferLogic") != null)
/* 1494 */       return (nc.ui.pubapp.billref.dest.DefaultBillDataLogic)context.get("transferLogic");
/* 1495 */     nc.ui.pubapp.billref.dest.DefaultBillDataLogic bean = new nc.ui.pubapp.billref.dest.DefaultBillDataLogic();
/* 1496 */     context.put("transferLogic", bean);
/* 1497 */     bean.setBillForm(getBillFormEditor());
/* 1498 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1499 */     invokeInitializingBean(bean);
/* 1500 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.importable.interceptor.ExportActionInterceptor getExportActionInterceptor() {
/* 1504 */     if (context.get("exportActionInterceptor") != null)
/* 1505 */       return (nc.ui.arap.importable.interceptor.ExportActionInterceptor)context.get("exportActionInterceptor");
/* 1506 */     nc.ui.arap.importable.interceptor.ExportActionInterceptor bean = new nc.ui.arap.importable.interceptor.ExportActionInterceptor();
/* 1507 */     context.put("exportActionInterceptor", bean);
/* 1508 */     bean.setModel(getManageAppModel());
/* 1509 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1510 */     invokeInitializingBean(bean);
/* 1511 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.interceptor.CompositeActionInterceptor getInterceptor() {
/* 1515 */     if (context.get("interceptor") != null)
/* 1516 */       return (nc.ui.arap.actions.interceptor.CompositeActionInterceptor)context.get("interceptor");
/* 1517 */     nc.ui.arap.actions.interceptor.CompositeActionInterceptor bean = new nc.ui.arap.actions.interceptor.CompositeActionInterceptor();
/* 1518 */     context.put("interceptor", bean);
/* 1519 */     bean.setInterceptors(getManagedList11());
/* 1520 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1521 */     invokeInitializingBean(bean);
/* 1522 */     return bean;
/*      */   }
/*      */   
/* 1525 */   private List getManagedList11() { List list = new ArrayList();list.add(getCaActionInterceptor());list.add(getPauseTransactActionInterceptor());list.add(getBillVersionActionInterceptor());list.add(getOperPowerInterceptor());list.add(getBillVerifyActionInterceptor());list.add(getOrgCheckActionInterceptor());return list;
/*      */   }
/*      */   
/* 1528 */   public nc.ui.arap.model.ArapPageModelDataManager getModelDataManager() { if (context.get("modelDataManager") != null)
/* 1529 */       return (nc.ui.arap.model.ArapPageModelDataManager)context.get("modelDataManager");
/* 1530 */     nc.ui.arap.model.ArapPageModelDataManager bean = new nc.ui.arap.model.ArapPageModelDataManager();
/* 1531 */     context.put("modelDataManager", bean);
/* 1532 */     bean.setModel(getManageAppModel());
/* 1533 */     bean.setService(getManageModelService());
/* 1534 */     bean.setPaginationDelegator(getPaginationDelegator());
/* 1535 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1536 */     invokeInitializingBean(bean);
/* 1537 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.uif2.actions.ActionContributors getToftpanelActionContributors() {
/* 1541 */     if (context.get("toftpanelActionContributors") != null)
/* 1542 */       return (nc.ui.uif2.actions.ActionContributors)context.get("toftpanelActionContributors");
/* 1543 */     nc.ui.uif2.actions.ActionContributors bean = new nc.ui.uif2.actions.ActionContributors();
/* 1544 */     context.put("toftpanelActionContributors", bean);
/* 1545 */     bean.setContributors(getManagedList12());
/* 1546 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1547 */     invokeInitializingBean(bean);
/* 1548 */     return bean;
/*      */   }
/*      */   
/* 1551 */   private List getManagedList12() { List list = new ArrayList();list.add(getListActions());list.add(getCardActions());list.add(getVerifyActions());list.add(getMakeupActions());return list;
/*      */   }
/*      */   
/* 1554 */   public nc.ui.uif2.FunNodeClosingHandler getClosingListener() { if (context.get("ClosingListener") != null)
/* 1555 */       return (nc.ui.uif2.FunNodeClosingHandler)context.get("ClosingListener");
/* 1556 */     nc.ui.uif2.FunNodeClosingHandler bean = new nc.ui.uif2.FunNodeClosingHandler();
/* 1557 */     context.put("ClosingListener", bean);
/* 1558 */     bean.setModel(getManageAppModel());
/* 1559 */     bean.setSaveaction(getSaveAction());
/* 1560 */     bean.setCancelaction(getCancelAction());
/* 1561 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1562 */     invokeInitializingBean(bean);
/* 1563 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.scale.ArapBillCardPanelScaleProcessor getScaleProcessor() {
/* 1567 */     if (context.get("scaleProcessor") != null)
/* 1568 */       return (nc.ui.arap.scale.ArapBillCardPanelScaleProcessor)context.get("scaleProcessor");
/* 1569 */     nc.ui.arap.scale.ArapBillCardPanelScaleProcessor bean = new nc.ui.arap.scale.ArapBillCardPanelScaleProcessor();
/* 1570 */     context.put("scaleProcessor", bean);
/* 1571 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1572 */     invokeInitializingBean(bean);
/* 1573 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.scale.ArapBillListPanelScaleProcessor getListScaleProcessor() {
/* 1577 */     if (context.get("listScaleProcessor") != null)
/* 1578 */       return (nc.ui.arap.scale.ArapBillListPanelScaleProcessor)context.get("listScaleProcessor");
/* 1579 */     nc.ui.arap.scale.ArapBillListPanelScaleProcessor bean = new nc.ui.arap.scale.ArapBillListPanelScaleProcessor();
/* 1580 */     context.put("listScaleProcessor", bean);
/* 1581 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1582 */     invokeInitializingBean(bean);
/* 1583 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.ArapOrgChanged getOrgchange() {
/* 1587 */     if (context.get("orgchange") != null)
/* 1588 */       return (nc.ui.arap.viewhandler.ArapOrgChanged)context.get("orgchange");
/* 1589 */     nc.ui.arap.viewhandler.ArapOrgChanged bean = new nc.ui.arap.viewhandler.ArapOrgChanged();
/* 1590 */     context.put("orgchange", bean);
/* 1591 */     bean.setBcpsp(getScaleProcessor());
/* 1592 */     bean.setHtob(getArapH2B());
/* 1593 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1594 */     invokeInitializingBean(bean);
/* 1595 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.uif2.TangramContainer getContainer() {
/* 1599 */     if (context.get("container") != null)
/* 1600 */       return (nc.ui.uif2.TangramContainer)context.get("container");
/* 1601 */     nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
/* 1602 */     context.put("container", bean);
/* 1603 */     bean.setTangramLayoutRoot(getTBNode_8fdfdb());
/* 1604 */     bean.initUI();
/* 1605 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1606 */     invokeInitializingBean(bean);
/* 1607 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_8fdfdb() {
/* 1611 */     if (context.get("nc.ui.uif2.tangramlayout.node.TBNode#8fdfdb") != null)
/* 1612 */       return (nc.ui.uif2.tangramlayout.node.TBNode)context.get("nc.ui.uif2.tangramlayout.node.TBNode#8fdfdb");
/* 1613 */     nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
/* 1614 */     context.put("nc.ui.uif2.tangramlayout.node.TBNode#8fdfdb", bean);
/* 1615 */     bean.setTabs(getManagedList13());
/* 1616 */     bean.setShowMode("CardLayout");
/* 1617 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1618 */     invokeInitializingBean(bean);
/* 1619 */     return bean;
/*      */   }
/*      */   
/* 1622 */   private List getManagedList13() { List list = new ArrayList();list.add(getVSNode_240dc0());list.add(getHSNode_139508a());list.add(getCNode_93bf8());list.add(getCNode_c10678());return list;
/*      */   }
/*      */   
/* 1625 */   private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_240dc0() { if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#240dc0") != null)
/* 1626 */       return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#240dc0");
/* 1627 */     nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
/* 1628 */     context.put("nc.ui.uif2.tangramlayout.node.VSNode#240dc0", bean);
/* 1629 */     bean.setUp(getCNode_13ba3b3());
/* 1630 */     bean.setDown(getCNode_106c7bf());
/* 1631 */     bean.setShowMode("NoDivider");
/* 1632 */     bean.setDividerLocation(30.0F);
/* 1633 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1634 */     invokeInitializingBean(bean);
/* 1635 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.uif2.tangramlayout.node.CNode getCNode_13ba3b3() {
/* 1639 */     if (context.get("nc.ui.uif2.tangramlayout.node.CNode#13ba3b3") != null)
/* 1640 */       return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#13ba3b3");
/* 1641 */     nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
/* 1642 */     context.put("nc.ui.uif2.tangramlayout.node.CNode#13ba3b3", bean);
/* 1643 */     bean.setComponent(getCardInfoPnl());
/* 1644 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1645 */     invokeInitializingBean(bean);
/* 1646 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.uif2.tangramlayout.node.CNode getCNode_106c7bf() {
/* 1650 */     if (context.get("nc.ui.uif2.tangramlayout.node.CNode#106c7bf") != null)
/* 1651 */       return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#106c7bf");
/* 1652 */     nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
/* 1653 */     context.put("nc.ui.uif2.tangramlayout.node.CNode#106c7bf", bean);
/* 1654 */     bean.setName(getI18nFB_7850e4());
/* 1655 */     bean.setComponent(getBillFormEditor());
/* 1656 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1657 */     invokeInitializingBean(bean);
/* 1658 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_7850e4() {
/* 1662 */     if (context.get("nc.ui.uif2.I18nFB#7850e4") != null)
/* 1663 */       return (String)context.get("nc.ui.uif2.I18nFB#7850e4");
/* 1664 */     I18nFB bean = new I18nFB();
/* 1665 */     context.put("&nc.ui.uif2.I18nFB#7850e4", bean);bean.setResDir("common");
/* 1666 */     bean.setResId("arapcommonv6-0136");
/* 1667 */     bean.setDefaultValue("��Ƭ");
/* 1668 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1669 */     invokeInitializingBean(bean);
/*      */     try {
/* 1671 */       Object product = bean.getObject();
/* 1672 */       context.put("nc.ui.uif2.I18nFB#7850e4", product);
/* 1673 */       return (String)product;
/*      */     } catch (Exception e) {
/* 1675 */       throw new RuntimeException(e);
/*      */     } }
/*      */   
/* 1678 */   private nc.ui.uif2.tangramlayout.node.HSNode getHSNode_139508a() { if (context.get("nc.ui.uif2.tangramlayout.node.HSNode#139508a") != null)
/* 1679 */       return (nc.ui.uif2.tangramlayout.node.HSNode)context.get("nc.ui.uif2.tangramlayout.node.HSNode#139508a");
/* 1680 */     nc.ui.uif2.tangramlayout.node.HSNode bean = new nc.ui.uif2.tangramlayout.node.HSNode();
/* 1681 */     context.put("nc.ui.uif2.tangramlayout.node.HSNode#139508a", bean);
/* 1682 */     bean.setLeft(getCNode_102c39b());
/* 1683 */     bean.setRight(getVSNode_1aefc59());
/* 1684 */     bean.setDividerLocation(0.2F);
/* 1685 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1686 */     invokeInitializingBean(bean);
/* 1687 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.uif2.tangramlayout.node.CNode getCNode_102c39b() {
/* 1691 */     if (context.get("nc.ui.uif2.tangramlayout.node.CNode#102c39b") != null)
/* 1692 */       return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#102c39b");
/* 1693 */     nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
/* 1694 */     context.put("nc.ui.uif2.tangramlayout.node.CNode#102c39b", bean);
/* 1695 */     bean.setComponent(getQueryAreaShell());
/* 1696 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1697 */     invokeInitializingBean(bean);
/* 1698 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_1aefc59() {
/* 1702 */     if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#1aefc59") != null)
/* 1703 */       return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#1aefc59");
/* 1704 */     nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
/* 1705 */     context.put("nc.ui.uif2.tangramlayout.node.VSNode#1aefc59", bean);
/* 1706 */     bean.setUp(getCNode_7801f1());
/* 1707 */     bean.setDown(getCNode_176e8d1());
/* 1708 */     bean.setShowMode("NoDivider");
/* 1709 */     bean.setDividerLocation(30.0F);
/* 1710 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1711 */     invokeInitializingBean(bean);
/* 1712 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.uif2.tangramlayout.node.CNode getCNode_7801f1() {
/* 1716 */     if (context.get("nc.ui.uif2.tangramlayout.node.CNode#7801f1") != null)
/* 1717 */       return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#7801f1");
/* 1718 */     nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
/* 1719 */     context.put("nc.ui.uif2.tangramlayout.node.CNode#7801f1", bean);
/* 1720 */     bean.setComponent(getQueryInfo());
/* 1721 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1722 */     invokeInitializingBean(bean);
/* 1723 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.uif2.tangramlayout.node.CNode getCNode_176e8d1() {
/* 1727 */     if (context.get("nc.ui.uif2.tangramlayout.node.CNode#176e8d1") != null)
/* 1728 */       return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#176e8d1");
/* 1729 */     nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
/* 1730 */     context.put("nc.ui.uif2.tangramlayout.node.CNode#176e8d1", bean);
/* 1731 */     bean.setName(getI18nFB_c77781());
/* 1732 */     bean.setComponent(getListView());
/* 1733 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1734 */     invokeInitializingBean(bean);
/* 1735 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_c77781() {
/* 1739 */     if (context.get("nc.ui.uif2.I18nFB#c77781") != null)
/* 1740 */       return (String)context.get("nc.ui.uif2.I18nFB#c77781");
/* 1741 */     I18nFB bean = new I18nFB();
/* 1742 */     context.put("&nc.ui.uif2.I18nFB#c77781", bean);bean.setResDir("common");
/* 1743 */     bean.setResId("arapcommonv6-0135");
/* 1744 */     bean.setDefaultValue("�б�");
/* 1745 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1746 */     invokeInitializingBean(bean);
/*      */     try {
/* 1748 */       Object product = bean.getObject();
/* 1749 */       context.put("nc.ui.uif2.I18nFB#c77781", product);
/* 1750 */       return (String)product;
/*      */     } catch (Exception e) {
/* 1752 */       throw new RuntimeException(e);
/*      */     } }
/*      */   
/* 1755 */   private nc.ui.uif2.tangramlayout.node.CNode getCNode_93bf8() { if (context.get("nc.ui.uif2.tangramlayout.node.CNode#93bf8") != null)
/* 1756 */       return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#93bf8");
/* 1757 */     nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
/* 1758 */     context.put("nc.ui.uif2.tangramlayout.node.CNode#93bf8", bean);
/* 1759 */     bean.setName(getI18nFB_1d6c0c2());
/* 1760 */     bean.setComponent(getVerifyui());
/* 1761 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1762 */     invokeInitializingBean(bean);
/* 1763 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_1d6c0c2() {
/* 1767 */     if (context.get("nc.ui.uif2.I18nFB#1d6c0c2") != null)
/* 1768 */       return (String)context.get("nc.ui.uif2.I18nFB#1d6c0c2");
/* 1769 */     I18nFB bean = new I18nFB();
/* 1770 */     context.put("&nc.ui.uif2.I18nFB#1d6c0c2", bean);bean.setResDir("common");
/* 1771 */     bean.setResId("arapcommonv6-0140");
/* 1772 */     bean.setDefaultValue("��������");
/* 1773 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1774 */     invokeInitializingBean(bean);
/*      */     try {
/* 1776 */       Object product = bean.getObject();
/* 1777 */       context.put("nc.ui.uif2.I18nFB#1d6c0c2", product);
/* 1778 */       return (String)product;
/*      */     } catch (Exception e) {
/* 1780 */       throw new RuntimeException(e);
/*      */     } }
/*      */   
/* 1783 */   private nc.ui.uif2.tangramlayout.node.CNode getCNode_c10678() { if (context.get("nc.ui.uif2.tangramlayout.node.CNode#c10678") != null)
/* 1784 */       return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#c10678");
/* 1785 */     nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
/* 1786 */     context.put("nc.ui.uif2.tangramlayout.node.CNode#c10678", bean);
/* 1787 */     bean.setName(getI18nFB_40231c());
/* 1788 */     bean.setComponent(getMakeupEditor());
/* 1789 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1790 */     invokeInitializingBean(bean);
/* 1791 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_40231c() {
/* 1795 */     if (context.get("nc.ui.uif2.I18nFB#40231c") != null)
/* 1796 */       return (String)context.get("nc.ui.uif2.I18nFB#40231c");
/* 1797 */     I18nFB bean = new I18nFB();
/* 1798 */     context.put("&nc.ui.uif2.I18nFB#40231c", bean);bean.setResDir("common");
/* 1799 */     bean.setResId("arapcommonv6-0141");
/* 1800 */     bean.setDefaultValue("�������");
/* 1801 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1802 */     invokeInitializingBean(bean);
/*      */     try {
/* 1804 */       Object product = bean.getObject();
/* 1805 */       context.put("nc.ui.uif2.I18nFB#40231c", product);
/* 1806 */       return (String)product;
/*      */     } catch (Exception e) {
/* 1808 */       throw new RuntimeException(e);
/*      */     } }
/*      */   
/* 1811 */   public nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel getQueryInfo() { if (context.get("queryInfo") != null)
/* 1812 */       return (nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel)context.get("queryInfo");
/* 1813 */     nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel bean = new nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel();
/* 1814 */     context.put("queryInfo", bean);
/* 1815 */     bean.setModel(getManageAppModel());
/* 1816 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1817 */     invokeInitializingBean(bean);
/* 1818 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell getQueryAreaShell() {
/* 1822 */     if (context.get("queryAreaShell") != null)
/* 1823 */       return (nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell)context.get("queryAreaShell");
/* 1824 */     nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell bean = new nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell();
/* 1825 */     context.put("queryAreaShell", bean);
/* 1826 */     bean.setQueryAreaCreator(getQueryAction());
/* 1827 */     bean.initUI();
/* 1828 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1829 */     invokeInitializingBean(bean);
/* 1830 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel getCardInfoPnl() {
/* 1834 */     if (context.get("cardInfoPnl") != null)
/* 1835 */       return (nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel)context.get("cardInfoPnl");
/* 1836 */     nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel bean = new nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel();
/* 1837 */     context.put("cardInfoPnl", bean);
/* 1838 */     bean.setActions(getManagedList14());
/* 1839 */     bean.setTitleAction(getReturnaction());
/* 1840 */     bean.setModel(getManageAppModel());
/* 1841 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1842 */     invokeInitializingBean(bean);
/* 1843 */     return bean;
/*      */   }
/*      */   
/* 1846 */   private List getManagedList14() { List list = new ArrayList();list.add(getDocumentManage());list.add(getActionsBarSeparator());list.add(getFirstLineAction());list.add(getPreLineAction());list.add(getNextLineAction());list.add(getLastLineAction());list.add(getActionsBarSeparator());list.add(getBillHeadZoomAction_1155335());return list;
/*      */   }
/*      */   
/* 1849 */   private nc.ui.arap.actions.BillHeadZoomAction getBillHeadZoomAction_1155335() { if (context.get("nc.ui.arap.actions.BillHeadZoomAction#1155335") != null)
/* 1850 */       return (nc.ui.arap.actions.BillHeadZoomAction)context.get("nc.ui.arap.actions.BillHeadZoomAction#1155335");
/* 1851 */     nc.ui.arap.actions.BillHeadZoomAction bean = new nc.ui.arap.actions.BillHeadZoomAction();
/* 1852 */     context.put("nc.ui.arap.actions.BillHeadZoomAction#1155335", bean);
/* 1853 */     bean.setModel(getManageAppModel());
/* 1854 */     bean.setBillForm(getBillFormEditor());
/* 1855 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1856 */     invokeInitializingBean(bean);
/* 1857 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.arap.actions.ArapReturnAction getReturnaction() {
/* 1861 */     if (context.get("returnaction") != null)
/* 1862 */       return (nc.ui.arap.actions.ArapReturnAction)context.get("returnaction");
/* 1863 */     nc.ui.arap.actions.ArapReturnAction bean = new nc.ui.arap.actions.ArapReturnAction();
/* 1864 */     context.put("returnaction", bean);
/* 1865 */     bean.setGoComponent(getListView());
/* 1866 */     bean.setSaveAction(getSaveAction());
/* 1867 */     bean.setModel(getManageAppModel());
/* 1868 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1869 */     invokeInitializingBean(bean);
/* 1870 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.commom.OppUIContainer getVerifyui() {
/* 1874 */     if (context.get("verifyui") != null)
/* 1875 */       return (nc.ui.arap.commom.OppUIContainer)context.get("verifyui");
/* 1876 */     nc.ui.arap.commom.OppUIContainer bean = new nc.ui.arap.commom.OppUIContainer();
/* 1877 */     context.put("verifyui", bean);
/* 1878 */     bean.setClosingListener(getClosingListener());
/* 1879 */     bean.initUI();
/* 1880 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1881 */     invokeInitializingBean(bean);
/* 1882 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.pubapp.uif2app.model.BillManageModel getMakeupAppModel() {
/* 1886 */     if (context.get("makeupAppModel") != null)
/* 1887 */       return (nc.ui.pubapp.uif2app.model.BillManageModel)context.get("makeupAppModel");
/* 1888 */     nc.ui.pubapp.uif2app.model.BillManageModel bean = new nc.ui.pubapp.uif2app.model.BillManageModel();
/* 1889 */     context.put("makeupAppModel", bean);
/* 1890 */     bean.setService(getManageModelService());
/* 1891 */     bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
/* 1892 */     bean.setContext(getContext());
/* 1893 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1894 */     invokeInitializingBean(bean);
/* 1895 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.pubapp.uif2app.view.BillCardAllDataValueStrategy getMakeupValueManager() {
/* 1899 */     if (context.get("makeupValueManager") != null)
/* 1900 */       return (nc.ui.pubapp.uif2app.view.BillCardAllDataValueStrategy)context.get("makeupValueManager");
/* 1901 */     nc.ui.pubapp.uif2app.view.BillCardAllDataValueStrategy bean = new nc.ui.pubapp.uif2app.view.BillCardAllDataValueStrategy();
/* 1902 */     context.put("makeupValueManager", bean);
/* 1903 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1904 */     invokeInitializingBean(bean);
/* 1905 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.view.NodeKeyQry getMakeupNodeKeyQry() {
/* 1909 */     if (context.get("makeupNodeKeyQry") != null)
/* 1910 */       return (nc.ui.arap.view.NodeKeyQry)context.get("makeupNodeKeyQry");
/* 1911 */     nc.ui.arap.view.NodeKeyQry bean = new nc.ui.arap.view.NodeKeyQry();
/* 1912 */     context.put("makeupNodeKeyQry", bean);
/* 1913 */     bean.setModel(getMakeupAppModel());
/* 1914 */     bean.setDefNodekey(getDefNodeKey());
/* 1915 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1916 */     invokeInitializingBean(bean);
/* 1917 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.view.ArapBillCardForm getMakeupEditor() {
/* 1921 */     if (context.get("makeupEditor") != null)
/* 1922 */       return (nc.ui.arap.view.ArapBillCardForm)context.get("makeupEditor");
/* 1923 */     nc.ui.arap.view.ArapBillCardForm bean = new nc.ui.arap.view.ArapBillCardForm();
/* 1924 */     context.put("makeupEditor", bean);
/* 1925 */     bean.setModel(getMakeupAppModel());
/* 1926 */     bean.setComponentValueManager(getMakeupValueManager());
/* 1927 */     bean.setNodekeyQry(getMakeupNodeKeyQry());
/* 1928 */     bean.setClosingListener(getClosingListener());
/* 1929 */     bean.setAutoAddLine(false);
/* 1930 */     bean.setTemplateNotNullValidate(true);
/* 1931 */     bean.setUserdefitemPreparator(getUserdefitemContainerPreparator_109d5bf());
/* 1932 */     bean.initRealUI();
/* 1933 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1934 */     invokeInitializingBean(bean);
/* 1935 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.uif2.editor.UserdefitemContainerPreparator getUserdefitemContainerPreparator_109d5bf() {
/* 1939 */     if (context.get("nc.ui.uif2.editor.UserdefitemContainerPreparator#109d5bf") != null)
/* 1940 */       return (nc.ui.uif2.editor.UserdefitemContainerPreparator)context.get("nc.ui.uif2.editor.UserdefitemContainerPreparator#109d5bf");
/* 1941 */     nc.ui.uif2.editor.UserdefitemContainerPreparator bean = new nc.ui.uif2.editor.UserdefitemContainerPreparator();
/* 1942 */     context.put("nc.ui.uif2.editor.UserdefitemContainerPreparator#109d5bf", bean);
/* 1943 */     bean.setContainer(getUserdefitemContainer());
/* 1944 */     bean.setParams(getManagedList15());
/* 1945 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1946 */     invokeInitializingBean(bean);
/* 1947 */     return bean;
/*      */   }
/*      */   
/* 1950 */   private List getManagedList15() { List list = new ArrayList();list.add(getCardUserdefitemQueryParam());list.add(getCardUserdefitemQueryParam1());return list;
/*      */   }
/*      */   
/* 1953 */   public nc.ui.arap.actions.BodyVerifyAction getOnBodyVerify() { if (context.get("onBodyVerify") != null)
/* 1954 */       return (nc.ui.arap.actions.BodyVerifyAction)context.get("onBodyVerify");
/* 1955 */     nc.ui.arap.actions.BodyVerifyAction bean = new nc.ui.arap.actions.BodyVerifyAction();
/* 1956 */     context.put("onBodyVerify", bean);
/* 1957 */     bean.setInterceptor(getInterceptor());
/* 1958 */     bean.setModel(getManageAppModel());
/* 1959 */     bean.setCardPanel(getBillFormEditor());
/* 1960 */     bean.setContext(getContext());
/* 1961 */     bean.setOppui(getVerifyui());
/* 1962 */     bean.setListView(getListView());
/* 1963 */     bean.setVerifyui(getVerifyui());
/* 1964 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1965 */     invokeInitializingBean(bean);
/* 1966 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.WholeBillVerifyAction getOnWholeBillVerifyAction() {
/* 1970 */     if (context.get("onWholeBillVerifyAction") != null)
/* 1971 */       return (nc.ui.arap.actions.WholeBillVerifyAction)context.get("onWholeBillVerifyAction");
/* 1972 */     nc.ui.arap.actions.WholeBillVerifyAction bean = new nc.ui.arap.actions.WholeBillVerifyAction();
/* 1973 */     context.put("onWholeBillVerifyAction", bean);
/* 1974 */     bean.setInterceptor(getInterceptor());
/* 1975 */     bean.setModel(getManageAppModel());
/* 1976 */     bean.setCardPanel(getBillFormEditor());
/* 1977 */     bean.setContext(getContext());
/* 1978 */     bean.setListView(getListView());
/* 1979 */     bean.setOppui(getVerifyui());
/* 1980 */     bean.setVerifyui(getVerifyui());
/* 1981 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1982 */     invokeInitializingBean(bean);
/* 1983 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getVerifyActions() {
/* 1987 */     if (context.get("verifyActions") != null)
/* 1988 */       return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("verifyActions");
/* 1989 */     nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getVerifyui());context.put("verifyActions", bean);
/* 1990 */     bean.setActions(getManagedList16());
/* 1991 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1992 */     invokeInitializingBean(bean);
/* 1993 */     return bean;
/*      */   }
/*      */   
/* 1996 */   private List getManagedList16() { List list = new ArrayList();list.add(getVerifyMakeupAction());list.add(getVerifyAllocationAction());list.add(getVerifyIntimeAction());list.add(getVerifyFilterAction());list.add(getVerifyAllSelectedAction());list.add(getVerifyAllCancelAction());list.add(getVerifyGoBackAction());list.add(getVerifyLinkedQueryAction());return list;
/*      */   }
/*      */   
/* 1999 */   public nc.ui.arap.actions.VerifyMakeupAction getVerifyMakeupAction() { if (context.get("verifyMakeupAction") != null)
/* 2000 */       return (nc.ui.arap.actions.VerifyMakeupAction)context.get("verifyMakeupAction");
/* 2001 */     nc.ui.arap.actions.VerifyMakeupAction bean = new nc.ui.arap.actions.VerifyMakeupAction();
/* 2002 */     context.put("verifyMakeupAction", bean);
/* 2003 */     bean.setInterceptor(getInterceptor());
/* 2004 */     bean.setMakeupEditor(getMakeupEditor());
/* 2005 */     bean.setContext(getContext());
/* 2006 */     bean.setModel(getMakeupAppModel());
/* 2007 */     bean.setOrgChangedImpl(getOrgchange());
/* 2008 */     bean.setCardPanel(getBillFormEditor());
/* 2009 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2010 */     invokeInitializingBean(bean);
/* 2011 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.VerifyAllocationAction getVerifyAllocationAction() {
/* 2015 */     if (context.get("verifyAllocationAction") != null)
/* 2016 */       return (nc.ui.arap.actions.VerifyAllocationAction)context.get("verifyAllocationAction");
/* 2017 */     nc.ui.arap.actions.VerifyAllocationAction bean = new nc.ui.arap.actions.VerifyAllocationAction();
/* 2018 */     context.put("verifyAllocationAction", bean);
/* 2019 */     bean.setInterceptor(getInterceptor());
/* 2020 */     bean.setModel(getMakeupAppModel());
/* 2021 */     bean.setCardPanel(getBillFormEditor());
/* 2022 */     bean.setContext(getContext());
/* 2023 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2024 */     invokeInitializingBean(bean);
/* 2025 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.VerifyIntimeAction getVerifyIntimeAction() {
/* 2029 */     if (context.get("verifyIntimeAction") != null)
/* 2030 */       return (nc.ui.arap.actions.VerifyIntimeAction)context.get("verifyIntimeAction");
/* 2031 */     nc.ui.arap.actions.VerifyIntimeAction bean = new nc.ui.arap.actions.VerifyIntimeAction();
/* 2032 */     context.put("verifyIntimeAction", bean);
/* 2033 */     bean.setInterceptor(getInterceptor());
/* 2034 */     bean.setModel(getMakeupAppModel());
/* 2035 */     bean.setCardPanel(getBillFormEditor());
/* 2036 */     bean.setContext(getContext());
/* 2037 */     bean.setBillFormEditor(getBillFormEditor());
/* 2038 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2039 */     invokeInitializingBean(bean);
/* 2040 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.VerifyFilterAction getVerifyFilterAction() {
/* 2044 */     if (context.get("verifyFilterAction") != null)
/* 2045 */       return (nc.ui.arap.actions.VerifyFilterAction)context.get("verifyFilterAction");
/* 2046 */     nc.ui.arap.actions.VerifyFilterAction bean = new nc.ui.arap.actions.VerifyFilterAction();
/* 2047 */     context.put("verifyFilterAction", bean);
/* 2048 */     bean.setInterceptor(getInterceptor());
/* 2049 */     bean.setModel(getMakeupAppModel());
/* 2050 */     bean.setCardPanel(getBillFormEditor());
/* 2051 */     bean.setContext(getContext());
/* 2052 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2053 */     invokeInitializingBean(bean);
/* 2054 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.VerifyAllSelectedAction getVerifyAllSelectedAction() {
/* 2058 */     if (context.get("verifyAllSelectedAction") != null)
/* 2059 */       return (nc.ui.arap.actions.VerifyAllSelectedAction)context.get("verifyAllSelectedAction");
/* 2060 */     nc.ui.arap.actions.VerifyAllSelectedAction bean = new nc.ui.arap.actions.VerifyAllSelectedAction();
/* 2061 */     context.put("verifyAllSelectedAction", bean);
/* 2062 */     bean.setInterceptor(getInterceptor());
/* 2063 */     bean.setModel(getMakeupAppModel());
/* 2064 */     bean.setCardPanel(getBillFormEditor());
/* 2065 */     bean.setContext(getContext());
/* 2066 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2067 */     invokeInitializingBean(bean);
/* 2068 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.VerifyAllCancelAction getVerifyAllCancelAction() {
/* 2072 */     if (context.get("verifyAllCancelAction") != null)
/* 2073 */       return (nc.ui.arap.actions.VerifyAllCancelAction)context.get("verifyAllCancelAction");
/* 2074 */     nc.ui.arap.actions.VerifyAllCancelAction bean = new nc.ui.arap.actions.VerifyAllCancelAction();
/* 2075 */     context.put("verifyAllCancelAction", bean);
/* 2076 */     bean.setInterceptor(getInterceptor());
/* 2077 */     bean.setModel(getMakeupAppModel());
/* 2078 */     bean.setCardPanel(getBillFormEditor());
/* 2079 */     bean.setContext(getContext());
/* 2080 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2081 */     invokeInitializingBean(bean);
/* 2082 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.VerifyGoBackAction getVerifyGoBackAction() {
/* 2086 */     if (context.get("verifyGoBackAction") != null)
/* 2087 */       return (nc.ui.arap.actions.VerifyGoBackAction)context.get("verifyGoBackAction");
/* 2088 */     nc.ui.arap.actions.VerifyGoBackAction bean = new nc.ui.arap.actions.VerifyGoBackAction();
/* 2089 */     context.put("verifyGoBackAction", bean);
/* 2090 */     bean.setInterceptor(getInterceptor());
/* 2091 */     bean.setBillFormEditor(getBillFormEditor());
/* 2092 */     bean.setModel(getMakeupAppModel());
/* 2093 */     bean.setCardPanel(getBillFormEditor());
/* 2094 */     bean.setContext(getContext());
/* 2095 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2096 */     invokeInitializingBean(bean);
/* 2097 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.VerifyLinkedQueryAction getVerifyLinkedQueryAction() {
/* 2101 */     if (context.get("verifyLinkedQueryAction") != null)
/* 2102 */       return (nc.ui.arap.actions.VerifyLinkedQueryAction)context.get("verifyLinkedQueryAction");
/* 2103 */     nc.ui.arap.actions.VerifyLinkedQueryAction bean = new nc.ui.arap.actions.VerifyLinkedQueryAction();
/* 2104 */     context.put("verifyLinkedQueryAction", bean);
/* 2105 */     bean.setInterceptor(getInterceptor());
/* 2106 */     bean.setModel(getMakeupAppModel());
/* 2107 */     bean.setCardPanel(getBillFormEditor());
/* 2108 */     bean.setContext(getContext());
/* 2109 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2110 */     invokeInitializingBean(bean);
/* 2111 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.MakeupCanelAction getMakeupCanelAction() {
/* 2115 */     if (context.get("makeupCanelAction") != null)
/* 2116 */       return (nc.ui.arap.actions.MakeupCanelAction)context.get("makeupCanelAction");
/* 2117 */     nc.ui.arap.actions.MakeupCanelAction bean = new nc.ui.arap.actions.MakeupCanelAction();
/* 2118 */     context.put("makeupCanelAction", bean);
/* 2119 */     bean.setInterceptor(getInterceptor());
/* 2120 */     bean.setVerifyui(getVerifyui());
/* 2121 */     bean.setModel(getMakeupAppModel());
/* 2122 */     bean.setMakeupEditor(getMakeupEditor());
/* 2123 */     bean.setContext(getContext());
/* 2124 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2125 */     invokeInitializingBean(bean);
/* 2126 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.MakeupConfirmAction getMakeupConfirmAction() {
/* 2130 */     if (context.get("makeupConfirmAction") != null)
/* 2131 */       return (nc.ui.arap.actions.MakeupConfirmAction)context.get("makeupConfirmAction");
/* 2132 */     nc.ui.arap.actions.MakeupConfirmAction bean = new nc.ui.arap.actions.MakeupConfirmAction();
/* 2133 */     context.put("makeupConfirmAction", bean);
/* 2134 */     bean.setInterceptor(getInterceptor());
/* 2135 */     bean.setBillFormEditor(getBillFormEditor());
/* 2136 */     bean.setModel(getMakeupAppModel());
/* 2137 */     bean.setMakeupEditor(getMakeupEditor());
/* 2138 */     bean.setContext(getContext());
/* 2139 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2140 */     invokeInitializingBean(bean);
/* 2141 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getMakeupActions() {
/* 2145 */     if (context.get("makeupActions") != null)
/* 2146 */       return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("makeupActions");
/* 2147 */     nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getMakeupEditor());context.put("makeupActions", bean);
/* 2148 */     bean.setActions(getManagedList17());
/* 2149 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2150 */     invokeInitializingBean(bean);
/* 2151 */     return bean;
/*      */   }
/*      */   
/* 2154 */   private List getManagedList17() { List list = new ArrayList();list.add(getMakeupConfirmAction());list.add(getMakeupCanelAction());return list;
/*      */   }
/*      */   
/* 2157 */   public nc.funcnode.ui.action.SeparatorAction getCurrNullAction() { if (context.get("currNullAction") != null)
/* 2158 */       return (nc.funcnode.ui.action.SeparatorAction)context.get("currNullAction");
/* 2159 */     nc.funcnode.ui.action.SeparatorAction bean = new nc.funcnode.ui.action.SeparatorAction();
/* 2160 */     context.put("currNullAction", bean);
/* 2161 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2162 */     invokeInitializingBean(bean);
/* 2163 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.CreditCheckDecoratorAction getSaveAction() {
/* 2167 */     if (context.get("saveAction") != null)
/* 2168 */       return (nc.ui.arap.actions.CreditCheckDecoratorAction)context.get("saveAction");
/* 2169 */     nc.ui.arap.actions.CreditCheckDecoratorAction bean = new nc.ui.arap.actions.CreditCheckDecoratorAction(getSaveActionReal());context.put("saveAction", bean);
/* 2170 */     bean.setModel(getManageAppModel());
/* 2171 */     bean.setInterceptor(getInterceptor());
/* 2172 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2173 */     invokeInitializingBean(bean);
/* 2174 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.PayBillEditAction getEditAction() {
/* 2178 */     if (context.get("editAction") != null)
/* 2179 */       return (nc.ui.arap.actions.PayBillEditAction)context.get("editAction");
/* 2180 */     nc.ui.arap.actions.PayBillEditAction bean = new nc.ui.arap.actions.PayBillEditAction();
/* 2181 */     context.put("editAction", bean);
/* 2182 */     bean.setModel(getManageAppModel());
/* 2183 */     bean.setEditor(getBillFormEditor());
/* 2184 */     bean.setListView(getListView());
/* 2185 */     bean.setInterceptor(getInterceptor());
/* 2186 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2187 */     invokeInitializingBean(bean);
/* 2188 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillDeleteAction getDeleteAction() {
/* 2192 */     if (context.get("deleteAction") != null)
/* 2193 */       return (nc.ui.arap.actions.BillDeleteAction)context.get("deleteAction");
/* 2194 */     nc.ui.arap.actions.BillDeleteAction bean = new nc.ui.arap.actions.BillDeleteAction();
/* 2195 */     context.put("deleteAction", bean);
/* 2196 */     bean.setEditor(getBillFormEditor());
/* 2197 */     bean.setModel(getManageAppModel());
/* 2198 */     bean.setListView(getListView());
/* 2199 */     bean.setInterceptor(getInterceptor());
/* 2200 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2201 */     invokeInitializingBean(bean);
/* 2202 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillCancelAction getCancelAction() {
/* 2206 */     if (context.get("cancelAction") != null)
/* 2207 */       return (nc.ui.arap.actions.BillCancelAction)context.get("cancelAction");
/* 2208 */     nc.ui.arap.actions.BillCancelAction bean = new nc.ui.arap.actions.BillCancelAction();
/* 2209 */     context.put("cancelAction", bean);
/* 2210 */     bean.setModel(getManageAppModel());
/* 2211 */     bean.setEditor(getBillFormEditor());
/* 2212 */     bean.setInterceptor(getInterceptor());
/* 2213 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2214 */     invokeInitializingBean(bean);
/* 2215 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillConferCancelAction getConferCancelAction() {
/* 2219 */     if (context.get("conferCancelAction") != null)
/* 2220 */       return (nc.ui.arap.actions.BillConferCancelAction)context.get("conferCancelAction");
/* 2221 */     nc.ui.arap.actions.BillConferCancelAction bean = new nc.ui.arap.actions.BillConferCancelAction();
/* 2222 */     context.put("conferCancelAction", bean);
/* 2223 */     bean.setModel(getManageAppModel());
/* 2224 */     bean.setEditor(getBillFormEditor());
/* 2225 */     bean.setInterceptor(getInterceptor());
/* 2226 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2227 */     invokeInitializingBean(bean);
/* 2228 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillAddAction getAddAction() {
/* 2232 */     if (context.get("addAction") != null)
/* 2233 */       return (nc.ui.arap.actions.BillAddAction)context.get("addAction");
/* 2234 */     nc.ui.arap.actions.BillAddAction bean = new nc.ui.arap.actions.BillAddAction();
/* 2235 */     context.put("addAction", bean);
/* 2236 */     bean.setModel(getManageAppModel());
/* 2237 */     bean.setShowUpComponent(getBillFormEditor());
/* 2238 */     bean.setInterceptor(getInterceptor());
/* 2239 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2240 */     invokeInitializingBean(bean);
/* 2241 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.uif2.actions.AddLineAction getAddline() {
/* 2245 */     if (context.get("addline") != null)
/* 2246 */       return (nc.ui.uif2.actions.AddLineAction)context.get("addline");
/* 2247 */     nc.ui.uif2.actions.AddLineAction bean = new nc.ui.uif2.actions.AddLineAction();
/* 2248 */     context.put("addline", bean);
/* 2249 */     bean.setModel(getManageAppModel());
/* 2250 */     bean.setCardpanel(getBillFormEditor());
/* 2251 */     bean.setInterceptor(getInterceptor());
/* 2252 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2253 */     invokeInitializingBean(bean);
/* 2254 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.query.ArapBillQueryConditionDLGInitializer getQueryDLGInitializer() {
/* 2258 */     if (context.get("queryDLGInitializer") != null)
/* 2259 */       return (nc.ui.arap.query.ArapBillQueryConditionDLGInitializer)context.get("queryDLGInitializer");
/* 2260 */     nc.ui.arap.query.ArapBillQueryConditionDLGInitializer bean = new nc.ui.arap.query.ArapBillQueryConditionDLGInitializer();
/* 2261 */     context.put("queryDLGInitializer", bean);
/* 2262 */     bean.setModel(getManageAppModel());
/* 2263 */     bean.setBillType(getDefBillType());
/* 2264 */     bean.setIsInit("N");
/* 2265 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2266 */     invokeInitializingBean(bean);
/* 2267 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.view.ArapQueryTemplateContainer getQueryTemplateContainer() {
/* 2271 */     if (context.get("queryTemplateContainer") != null)
/* 2272 */       return (nc.ui.arap.view.ArapQueryTemplateContainer)context.get("queryTemplateContainer");
/* 2273 */     nc.ui.arap.view.ArapQueryTemplateContainer bean = new nc.ui.arap.view.ArapQueryTemplateContainer();
/* 2274 */     context.put("queryTemplateContainer", bean);
/* 2275 */     bean.setContext(getContext());
/* 2276 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2277 */     invokeInitializingBean(bean);
/* 2278 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.uif2.editor.UIF2RemoteCallCombinatorCaller getRemoteCallCombinatorCaller() {
/* 2282 */     if (context.get("remoteCallCombinatorCaller") != null)
/* 2283 */       return (nc.ui.uif2.editor.UIF2RemoteCallCombinatorCaller)context.get("remoteCallCombinatorCaller");
/* 2284 */     nc.ui.uif2.editor.UIF2RemoteCallCombinatorCaller bean = new nc.ui.uif2.editor.UIF2RemoteCallCombinatorCaller();
/* 2285 */     context.put("remoteCallCombinatorCaller", bean);
/* 2286 */     bean.setRemoteCallers(getManagedList18());
/* 2287 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2288 */     invokeInitializingBean(bean);
/* 2289 */     return bean;
/*      */   }
/*      */   
/* 2292 */   private List getManagedList18() { List list = new ArrayList();list.add(getQueryTemplateContainer());list.add(getUserdefitemContainer());list.add(getTemplateContainer());list.add(getRetAddLoader());return list;
/*      */   }
/*      */   
/* 2295 */   public nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor getListInterceptor() { if (context.get("listInterceptor") != null)
/* 2296 */       return (nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor)context.get("listInterceptor");
/* 2297 */     nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor();
/* 2298 */     context.put("listInterceptor", bean);
/* 2299 */     bean.setShowUpComponent(getListView());
/* 2300 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2301 */     invokeInitializingBean(bean);
/* 2302 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction getQueryAction() {
/* 2306 */     if (context.get("queryAction") != null)
/* 2307 */       return (nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction)context.get("queryAction");
/* 2308 */     nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction bean = new nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction();
/* 2309 */     context.put("queryAction", bean);
/* 2310 */     bean.setDataManager(getModelDataManager());
/* 2311 */     bean.setQryCondDLGInitializer(getQueryDLGInitializer());
/* 2312 */     bean.setModel(getManageAppModel());
/* 2313 */     bean.setTemplateContainer(getQueryTemplateContainer());
/* 2314 */     bean.setInterceptor(getListInterceptor());
/* 2315 */     bean.setShowUpComponent(getListView());
/* 2316 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2317 */     invokeInitializingBean(bean);
/* 2318 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillLinkQueryAction getLinkQueryAction() {
/* 2322 */     if (context.get("linkQueryAction") != null)
/* 2323 */       return (nc.ui.arap.actions.BillLinkQueryAction)context.get("linkQueryAction");
/* 2324 */     nc.ui.arap.actions.BillLinkQueryAction bean = new nc.ui.arap.actions.BillLinkQueryAction();
/* 2325 */     context.put("linkQueryAction", bean);
/* 2326 */     bean.setModel(getManageAppModel());
/* 2327 */     bean.setInterceptor(getInterceptor());
/* 2328 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2329 */     invokeInitializingBean(bean);
/* 2330 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.LinkVoucharQueryAction getLinkVoucharQueryAction() {
/* 2334 */     if (context.get("linkVoucharQueryAction") != null)
/* 2335 */       return (nc.ui.arap.actions.LinkVoucharQueryAction)context.get("linkVoucharQueryAction");
/* 2336 */     nc.ui.arap.actions.LinkVoucharQueryAction bean = new nc.ui.arap.actions.LinkVoucharQueryAction();
/* 2337 */     context.put("linkVoucharQueryAction", bean);
/* 2338 */     bean.setModel(getManageAppModel());
/* 2339 */     bean.setInterceptor(getInterceptor());
/* 2340 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2341 */     invokeInitializingBean(bean);
/* 2342 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.LinkBConferQueryAction getBconferQueryAction() {
/* 2346 */     if (context.get("bconferQueryAction") != null)
/* 2347 */       return (nc.ui.arap.actions.LinkBConferQueryAction)context.get("bconferQueryAction");
/* 2348 */     nc.ui.arap.actions.LinkBConferQueryAction bean = new nc.ui.arap.actions.LinkBConferQueryAction();
/* 2349 */     context.put("bconferQueryAction", bean);
/* 2350 */     bean.setModel(getManageAppModel());
/* 2351 */     bean.setContainer(getBillFormEditor());
/* 2352 */     bean.setInterceptor(getInterceptor());
/* 2353 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2354 */     invokeInitializingBean(bean);
/* 2355 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillFlowStateQueryAction getFlowStateQueryAction() {
/* 2359 */     if (context.get("flowStateQueryAction") != null)
/* 2360 */       return (nc.ui.arap.actions.BillFlowStateQueryAction)context.get("flowStateQueryAction");
/* 2361 */     nc.ui.arap.actions.BillFlowStateQueryAction bean = new nc.ui.arap.actions.BillFlowStateQueryAction();
/* 2362 */     context.put("flowStateQueryAction", bean);
/* 2363 */     bean.setModel(getManageAppModel());
/* 2364 */     bean.setContainer(getBillFormEditor());
/* 2365 */     bean.setInterceptor(getInterceptor());
/* 2366 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2367 */     invokeInitializingBean(bean);
/* 2368 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillBalanceLinkQueryAction getBalanceLinkQueryAction() {
/* 2372 */     if (context.get("balanceLinkQueryAction") != null)
/* 2373 */       return (nc.ui.arap.actions.BillBalanceLinkQueryAction)context.get("balanceLinkQueryAction");
/* 2374 */     nc.ui.arap.actions.BillBalanceLinkQueryAction bean = new nc.ui.arap.actions.BillBalanceLinkQueryAction();
/* 2375 */     context.put("balanceLinkQueryAction", bean);
/* 2376 */     bean.setModel(getManageAppModel());
/* 2377 */     bean.setEditor(getBillFormEditor());
/* 2378 */     bean.setInterceptor(getInterceptor());
/* 2379 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2380 */     invokeInitializingBean(bean);
/* 2381 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillTbbLinkAction getTbbLinkAction() {
/* 2385 */     if (context.get("tbbLinkAction") != null)
/* 2386 */       return (nc.ui.arap.actions.BillTbbLinkAction)context.get("tbbLinkAction");
/* 2387 */     nc.ui.arap.actions.BillTbbLinkAction bean = new nc.ui.arap.actions.BillTbbLinkAction();
/* 2388 */     context.put("tbbLinkAction", bean);
/* 2389 */     bean.setModel(getManageAppModel());
/* 2390 */     bean.setInterceptor(getInterceptor());
/* 2391 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2392 */     invokeInitializingBean(bean);
/* 2393 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillDealLinkQueryAction getDealLinkQueryAction() {
/* 2397 */     if (context.get("dealLinkQueryAction") != null)
/* 2398 */       return (nc.ui.arap.actions.BillDealLinkQueryAction)context.get("dealLinkQueryAction");
/* 2399 */     nc.ui.arap.actions.BillDealLinkQueryAction bean = new nc.ui.arap.actions.BillDealLinkQueryAction();
/* 2400 */     context.put("dealLinkQueryAction", bean);
/* 2401 */     bean.setModel(getManageAppModel());
/* 2402 */     bean.setContainer(getContainer());
/* 2403 */     bean.setInterceptor(getInterceptor());
/* 2404 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2405 */     invokeInitializingBean(bean);
/* 2406 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.LinkTermQueryAction getTermLinkQueryAction() {
/* 2410 */     if (context.get("termLinkQueryAction") != null)
/* 2411 */       return (nc.ui.arap.actions.LinkTermQueryAction)context.get("termLinkQueryAction");
/* 2412 */     nc.ui.arap.actions.LinkTermQueryAction bean = new nc.ui.arap.actions.LinkTermQueryAction();
/* 2413 */     context.put("termLinkQueryAction", bean);
/* 2414 */     bean.setModel(getManageAppModel());
/* 2415 */     bean.setEditor(getBillFormEditor());
/* 2416 */     bean.setInterceptor(getInterceptor());
/* 2417 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2418 */     invokeInitializingBean(bean);
/* 2419 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.InitBillCancelCloseAction getInitCancelCloseAction() {
/* 2423 */     if (context.get("initCancelCloseAction") != null)
/* 2424 */       return (nc.ui.arap.actions.InitBillCancelCloseAction)context.get("initCancelCloseAction");
/* 2425 */     nc.ui.arap.actions.InitBillCancelCloseAction bean = new nc.ui.arap.actions.InitBillCancelCloseAction();
/* 2426 */     context.put("initCancelCloseAction", bean);
/* 2427 */     bean.setModel(getManageAppModel());
/* 2428 */     bean.setInterceptor(getInterceptor());
/* 2429 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2430 */     invokeInitializingBean(bean);
/* 2431 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillRefreshSingAction getRefreshSingAction() {
/* 2435 */     if (context.get("refreshSingAction") != null)
/* 2436 */       return (nc.ui.arap.actions.BillRefreshSingAction)context.get("refreshSingAction");
/* 2437 */     nc.ui.arap.actions.BillRefreshSingAction bean = new nc.ui.arap.actions.BillRefreshSingAction();
/* 2438 */     context.put("refreshSingAction", bean);
/* 2439 */     bean.setModel(getManageAppModel());
/* 2440 */     bean.setInterceptor(getInterceptor());
/* 2441 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2442 */     invokeInitializingBean(bean);
/* 2443 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillApproveAction getApproveAction() {
/* 2447 */     if (context.get("ApproveAction") != null)
/* 2448 */       return (nc.ui.arap.actions.BillApproveAction)context.get("ApproveAction");
/* 2449 */     nc.ui.arap.actions.BillApproveAction bean = new nc.ui.arap.actions.BillApproveAction();
/* 2450 */     context.put("ApproveAction", bean);
/* 2451 */     bean.setModel(getManageAppModel());
/* 2452 */     bean.setEditor(getBillFormEditor());
/* 2453 */     bean.setInterceptor(getInterceptor());
/* 2454 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2455 */     invokeInitializingBean(bean);
/* 2456 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillUnApproveAction getUnApproveAction() {
/* 2460 */     if (context.get("UnApproveAction") != null)
/* 2461 */       return (nc.ui.arap.actions.BillUnApproveAction)context.get("UnApproveAction");
/* 2462 */     nc.ui.arap.actions.BillUnApproveAction bean = new nc.ui.arap.actions.BillUnApproveAction();
/* 2463 */     context.put("UnApproveAction", bean);
/* 2464 */     bean.setModel(getManageAppModel());
/* 2465 */     bean.setEditor(getBillFormEditor());
/* 2466 */     bean.setInterceptor(getInterceptor());
/* 2467 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2468 */     invokeInitializingBean(bean);
/* 2469 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillListApproveAction getApproveListBatchAction() {
/* 2473 */     if (context.get("ApproveListBatchAction") != null)
/* 2474 */       return (nc.ui.arap.actions.BillListApproveAction)context.get("ApproveListBatchAction");
/* 2475 */     nc.ui.arap.actions.BillListApproveAction bean = new nc.ui.arap.actions.BillListApproveAction();
/* 2476 */     context.put("ApproveListBatchAction", bean);
/* 2477 */     bean.setModel(getManageAppModel());
/* 2478 */     bean.setEditor(getListView());
/* 2479 */     bean.setIsbatch("Y");
/* 2480 */     bean.setInterceptor(getInterceptor());
/* 2481 */     bean.setTpaProgressUtil(getTPAProgressUtil_b0278c());
/* 2482 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2483 */     invokeInitializingBean(bean);
/* 2484 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.uif2.components.progress.TPAProgressUtil getTPAProgressUtil_b0278c() {
/* 2488 */     if (context.get("nc.ui.uif2.components.progress.TPAProgressUtil#b0278c") != null)
/* 2489 */       return (nc.ui.uif2.components.progress.TPAProgressUtil)context.get("nc.ui.uif2.components.progress.TPAProgressUtil#b0278c");
/* 2490 */     nc.ui.uif2.components.progress.TPAProgressUtil bean = new nc.ui.uif2.components.progress.TPAProgressUtil();
/* 2491 */     context.put("nc.ui.uif2.components.progress.TPAProgressUtil#b0278c", bean);
/* 2492 */     bean.setContext(getContext());
/* 2493 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2494 */     invokeInitializingBean(bean);
/* 2495 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillListUnApproveAction getUnApproveListBatchAction() {
/* 2499 */     if (context.get("UnApproveListBatchAction") != null)
/* 2500 */       return (nc.ui.arap.actions.BillListUnApproveAction)context.get("UnApproveListBatchAction");
/* 2501 */     nc.ui.arap.actions.BillListUnApproveAction bean = new nc.ui.arap.actions.BillListUnApproveAction();
/* 2502 */     context.put("UnApproveListBatchAction", bean);
/* 2503 */     bean.setModel(getManageAppModel());
/* 2504 */     bean.setEditor(getListView());
/* 2505 */     bean.setIsbatch("Y");
/* 2506 */     bean.setInterceptor(getInterceptor());
/* 2507 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2508 */     invokeInitializingBean(bean);
/* 2509 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillListApproveAction getApproveListAction() {
/* 2513 */     if (context.get("ApproveListAction") != null)
/* 2514 */       return (nc.ui.arap.actions.BillListApproveAction)context.get("ApproveListAction");
/* 2515 */     nc.ui.arap.actions.BillListApproveAction bean = new nc.ui.arap.actions.BillListApproveAction();
/* 2516 */     context.put("ApproveListAction", bean);
/* 2517 */     bean.setModel(getManageAppModel());
/* 2518 */     bean.setEditor(getListView());
/* 2519 */     bean.setIsbatch("N");
/* 2520 */     bean.setInterceptor(getInterceptor());
/* 2521 */     bean.setTpaProgressUtil(getTPAProgressUtil_f49119());
/* 2522 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2523 */     invokeInitializingBean(bean);
/* 2524 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.uif2.components.progress.TPAProgressUtil getTPAProgressUtil_f49119() {
/* 2528 */     if (context.get("nc.ui.uif2.components.progress.TPAProgressUtil#f49119") != null)
/* 2529 */       return (nc.ui.uif2.components.progress.TPAProgressUtil)context.get("nc.ui.uif2.components.progress.TPAProgressUtil#f49119");
/* 2530 */     nc.ui.uif2.components.progress.TPAProgressUtil bean = new nc.ui.uif2.components.progress.TPAProgressUtil();
/* 2531 */     context.put("nc.ui.uif2.components.progress.TPAProgressUtil#f49119", bean);
/* 2532 */     bean.setContext(getContext());
/* 2533 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2534 */     invokeInitializingBean(bean);
/* 2535 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillListUnApproveAction getUnApproveListAction() {
/* 2539 */     if (context.get("UnApproveListAction") != null)
/* 2540 */       return (nc.ui.arap.actions.BillListUnApproveAction)context.get("UnApproveListAction");
/* 2541 */     nc.ui.arap.actions.BillListUnApproveAction bean = new nc.ui.arap.actions.BillListUnApproveAction();
/* 2542 */     context.put("UnApproveListAction", bean);
/* 2543 */     bean.setModel(getManageAppModel());
/* 2544 */     bean.setEditor(getListView());
/* 2545 */     bean.setIsbatch("N");
/* 2546 */     bean.setInterceptor(getInterceptor());
/* 2547 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2548 */     invokeInitializingBean(bean);
/* 2549 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillTempSaveAction getTempSaveAction() {
/* 2553 */     if (context.get("tempSaveAction") != null)
/* 2554 */       return (nc.ui.arap.actions.BillTempSaveAction)context.get("tempSaveAction");
/* 2555 */     nc.ui.arap.actions.BillTempSaveAction bean = new nc.ui.arap.actions.BillTempSaveAction();
/* 2556 */     context.put("tempSaveAction", bean);
/* 2557 */     bean.setModel(getManageAppModel());
/* 2558 */     bean.setEditor(getBillFormEditor());
/* 2559 */     bean.setInterceptor(getInterceptor());
/* 2560 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2561 */     invokeInitializingBean(bean);
/* 2562 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillCopyAction getCopyAction() {
/* 2566 */     if (context.get("copyAction") != null)
/* 2567 */       return (nc.ui.arap.actions.BillCopyAction)context.get("copyAction");
/* 2568 */     nc.ui.arap.actions.BillCopyAction bean = new nc.ui.arap.actions.BillCopyAction();
/* 2569 */     context.put("copyAction", bean);
/* 2570 */     bean.setModel(getManageAppModel());
/* 2571 */     bean.setEditor(getBillFormEditor());
/* 2572 */     bean.setListView(getListView());
/* 2573 */     bean.setInterceptor(getInterceptor());
/* 2574 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2575 */     invokeInitializingBean(bean);
/* 2576 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillWriteBackAction getWriteBack() {
/* 2580 */     if (context.get("writeBack") != null)
/* 2581 */       return (nc.ui.arap.actions.BillWriteBackAction)context.get("writeBack");
/* 2582 */     nc.ui.arap.actions.BillWriteBackAction bean = new nc.ui.arap.actions.BillWriteBackAction();
/* 2583 */     context.put("writeBack", bean);
/* 2584 */     bean.setModel(getManageAppModel());
/* 2585 */     bean.setEditor(getBillFormEditor());
/* 2586 */     bean.setListView(getListView());
/* 2587 */     bean.setInterceptor(getInterceptor());
/* 2588 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2589 */     invokeInitializingBean(bean);
/* 2590 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.DocumentManageAction getDocumentManage() {
/* 2594 */     if (context.get("documentManage") != null)
/* 2595 */       return (nc.ui.arap.actions.DocumentManageAction)context.get("documentManage");
/* 2596 */     nc.ui.arap.actions.DocumentManageAction bean = new nc.ui.arap.actions.DocumentManageAction();
/* 2597 */     context.put("documentManage", bean);
/* 2598 */     bean.setModel(getManageAppModel());
/* 2599 */     bean.setEditor(getBillFormEditor());
/* 2600 */     bean.setInterceptor(getInterceptor());
/* 2601 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2602 */     invokeInitializingBean(bean);
/* 2603 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.RentAffiliatedAction getRentAffiliated() {
/* 2607 */     if (context.get("rentAffiliated") != null)
/* 2608 */       return (nc.ui.arap.actions.RentAffiliatedAction)context.get("rentAffiliated");
/* 2609 */     nc.ui.arap.actions.RentAffiliatedAction bean = new nc.ui.arap.actions.RentAffiliatedAction();
/* 2610 */     context.put("rentAffiliated", bean);
/* 2611 */     bean.setModel(getManageAppModel());
/* 2612 */     bean.setEditor(getBillFormEditor());
/* 2613 */     bean.setInterceptor(getInterceptor());
/* 2614 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2615 */     invokeInitializingBean(bean);
/* 2616 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.NoteRegisterAction getNoteRegister() {
/* 2620 */     if (context.get("noteRegister") != null)
/* 2621 */       return (nc.ui.arap.actions.NoteRegisterAction)context.get("noteRegister");
/* 2622 */     nc.ui.arap.actions.NoteRegisterAction bean = new nc.ui.arap.actions.NoteRegisterAction();
/* 2623 */     context.put("noteRegister", bean);
/* 2624 */     bean.setModel(getManageAppModel());
/* 2625 */     bean.setEditor(getBillFormEditor());
/* 2626 */     bean.setInterceptor(getInterceptor());
/* 2627 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2628 */     invokeInitializingBean(bean);
/* 2629 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.MakeAlterBillAction getMakeAlterBill() {
/* 2633 */     if (context.get("makeAlterBill") != null)
/* 2634 */       return (nc.ui.arap.actions.MakeAlterBillAction)context.get("makeAlterBill");
/* 2635 */     nc.ui.arap.actions.MakeAlterBillAction bean = new nc.ui.arap.actions.MakeAlterBillAction();
/* 2636 */     context.put("makeAlterBill", bean);
/* 2637 */     bean.setModel(getManageAppModel());
/* 2638 */     bean.setEditor(getBillFormEditor());
/* 2639 */     bean.setInterceptor(getInterceptor());
/* 2640 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2641 */     invokeInitializingBean(bean);
/* 2642 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.PauseTransactAction getPauseTransact() {
/* 2646 */     if (context.get("pauseTransact") != null)
/* 2647 */       return (nc.ui.arap.actions.PauseTransactAction)context.get("pauseTransact");
/* 2648 */     nc.ui.arap.actions.PauseTransactAction bean = new nc.ui.arap.actions.PauseTransactAction();
/* 2649 */     context.put("pauseTransact", bean);
/* 2650 */     bean.setModel(getManageAppModel());
/* 2651 */     bean.setEditor(getBillFormEditor());
/* 2652 */     bean.setListView(getListView());
/* 2653 */     bean.setInterceptor(getInterceptor());
/* 2654 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2655 */     invokeInitializingBean(bean);
/* 2656 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.CancelPauseTransactAction getCancelPauseTransact() {
/* 2660 */     if (context.get("cancelPauseTransact") != null)
/* 2661 */       return (nc.ui.arap.actions.CancelPauseTransactAction)context.get("cancelPauseTransact");
/* 2662 */     nc.ui.arap.actions.CancelPauseTransactAction bean = new nc.ui.arap.actions.CancelPauseTransactAction();
/* 2663 */     context.put("cancelPauseTransact", bean);
/* 2664 */     bean.setModel(getManageAppModel());
/* 2665 */     bean.setEditor(getBillFormEditor());
/* 2666 */     bean.setListView(getListView());
/* 2667 */     bean.setInterceptor(getInterceptor());
/* 2668 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2669 */     invokeInitializingBean(bean);
/* 2670 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillPrintAction getPrintBill() {
/* 2674 */     if (context.get("printBill") != null)
/* 2675 */       return (nc.ui.arap.actions.BillPrintAction)context.get("printBill");
/* 2676 */     nc.ui.arap.actions.BillPrintAction bean = new nc.ui.arap.actions.BillPrintAction();
/* 2677 */     context.put("printBill", bean);
/* 2678 */     bean.setModel(getManageAppModel());
/* 2679 */     bean.setInterceptor(getInterceptor());
/* 2680 */     bean.setTaxForm(getTaxForm());
/* 2681 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2682 */     invokeInitializingBean(bean);
/* 2683 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.OfficialPrintAction getOfficialPrint() {
/* 2687 */     if (context.get("officialPrint") != null)
/* 2688 */       return (nc.ui.arap.actions.OfficialPrintAction)context.get("officialPrint");
/* 2689 */     nc.ui.arap.actions.OfficialPrintAction bean = new nc.ui.arap.actions.OfficialPrintAction();
/* 2690 */     context.put("officialPrint", bean);
/* 2691 */     bean.setModel(getManageAppModel());
/* 2692 */     bean.setInterceptor(getInterceptor());
/* 2693 */     bean.setTaxForm(getTaxForm());
/* 2694 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2695 */     invokeInitializingBean(bean);
/* 2696 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.CancelPrintAction getCancelPrint() {
/* 2700 */     if (context.get("cancelPrint") != null)
/* 2701 */       return (nc.ui.arap.actions.CancelPrintAction)context.get("cancelPrint");
/* 2702 */     nc.ui.arap.actions.CancelPrintAction bean = new nc.ui.arap.actions.CancelPrintAction();
/* 2703 */     context.put("cancelPrint", bean);
/* 2704 */     bean.setModel(getManageAppModel());
/* 2705 */     bean.setEditor(getBillFormEditor());
/* 2706 */     bean.setInterceptor(getInterceptor());
/* 2707 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2708 */     invokeInitializingBean(bean);
/* 2709 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.PrintListAction getPrintList() {
/* 2713 */     if (context.get("printList") != null)
/* 2714 */       return (nc.ui.arap.actions.PrintListAction)context.get("printList");
/* 2715 */     nc.ui.arap.actions.PrintListAction bean = new nc.ui.arap.actions.PrintListAction();
/* 2716 */     context.put("printList", bean);
/* 2717 */     bean.setModel(getManageAppModel());
/* 2718 */     bean.setInterceptor(getInterceptor());
/* 2719 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2720 */     invokeInitializingBean(bean);
/* 2721 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.MadeBillAction getMadeBillAction() {
/* 2725 */     if (context.get("madeBillAction") != null)
/* 2726 */       return (nc.ui.arap.actions.MadeBillAction)context.get("madeBillAction");
/* 2727 */     nc.ui.arap.actions.MadeBillAction bean = new nc.ui.arap.actions.MadeBillAction();
/* 2728 */     context.put("madeBillAction", bean);
/* 2729 */     bean.setModel(getManageAppModel());
/* 2730 */     bean.setEditor(getBillFormEditor());
/* 2731 */     bean.setListView(getListView());
/* 2732 */     bean.setLoginContext(getContext());
/* 2733 */     bean.setInterceptor(getInterceptor());
/* 2734 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2735 */     invokeInitializingBean(bean);
/* 2736 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillPrepayAction getPrepayAction() {
/* 2740 */     if (context.get("prepayAction") != null)
/* 2741 */       return (nc.ui.arap.actions.BillPrepayAction)context.get("prepayAction");
/* 2742 */     nc.ui.arap.actions.BillPrepayAction bean = new nc.ui.arap.actions.BillPrepayAction();
/* 2743 */     context.put("prepayAction", bean);
/* 2744 */     bean.setModel(getManageAppModel());
/* 2745 */     bean.setEditor(getBillFormEditor());
/* 2746 */     bean.setInterceptor(getInterceptor());
/* 2747 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2748 */     invokeInitializingBean(bean);
/* 2749 */     return bean;
/*      */   }
/*      */   
/*      */   public MenuAction getBillOperateActionGroup() {
/* 2753 */     if (context.get("billOperateActionGroup") != null)
/* 2754 */       return (MenuAction)context.get("billOperateActionGroup");
/* 2755 */     MenuAction bean = new MenuAction();
/* 2756 */     context.put("billOperateActionGroup", bean);
/* 2757 */     bean.setCode("billOperate");
/* 2758 */     bean.setName(getI18nFB_6a7c8f());
/* 2759 */     bean.setActions(getManagedList19());
/* 2760 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2761 */     invokeInitializingBean(bean);
/* 2762 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_6a7c8f() {
/* 2766 */     if (context.get("nc.ui.uif2.I18nFB#6a7c8f") != null)
/* 2767 */       return (String)context.get("nc.ui.uif2.I18nFB#6a7c8f");
/* 2768 */     I18nFB bean = new I18nFB();
/* 2769 */     context.put("&nc.ui.uif2.I18nFB#6a7c8f", bean);bean.setResDir("common");
/* 2770 */     bean.setResId("arapcommonv6-0137");
/* 2771 */     bean.setDefaultValue("���ݲ���");
/* 2772 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2773 */     invokeInitializingBean(bean);
/*      */     try {
/* 2775 */       Object product = bean.getObject();
/* 2776 */       context.put("nc.ui.uif2.I18nFB#6a7c8f", product);
/* 2777 */       return (String)product;
/*      */     } catch (Exception e) {
/* 2779 */       throw new RuntimeException(e); } }
/*      */   
/* 2781 */   private List getManagedList19() { List list = new ArrayList();list.add(getDeleteAction());list.add(getEditAction());list.add(getTempSaveAction());list.add(getWriteBack());list.add(getDocumentManage());list.add(getPrepayAction());return list;
/*      */   }
/*      */   
/* 2784 */   public MenuAction getBillOperateActionGroup_Record() { if (context.get("billOperateActionGroup_Record") != null)
/* 2785 */       return (MenuAction)context.get("billOperateActionGroup_Record");
/* 2786 */     MenuAction bean = new MenuAction();
/* 2787 */     context.put("billOperateActionGroup_Record", bean);
/* 2788 */     bean.setCode("billOperate");
/* 2789 */     bean.setName(getI18nFB_9a88a6());
/* 2790 */     bean.setActions(getManagedList20());
/* 2791 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2792 */     invokeInitializingBean(bean);
/* 2793 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_9a88a6() {
/* 2797 */     if (context.get("nc.ui.uif2.I18nFB#9a88a6") != null)
/* 2798 */       return (String)context.get("nc.ui.uif2.I18nFB#9a88a6");
/* 2799 */     I18nFB bean = new I18nFB();
/* 2800 */     context.put("&nc.ui.uif2.I18nFB#9a88a6", bean);bean.setResDir("common");
/* 2801 */     bean.setResId("arapcommonv6-0137");
/* 2802 */     bean.setDefaultValue("���ݲ���");
/* 2803 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2804 */     invokeInitializingBean(bean);
/*      */     try {
/* 2806 */       Object product = bean.getObject();
/* 2807 */       context.put("nc.ui.uif2.I18nFB#9a88a6", product);
/* 2808 */       return (String)product;
/*      */     } catch (Exception e) {
/* 2810 */       throw new RuntimeException(e); } }
/*      */   
/* 2812 */   private List getManagedList20() { List list = new ArrayList();list.add(getDeleteAction());list.add(getEditAction());list.add(getTempSaveAction());list.add(getDocumentManage());return list;
/*      */   }
/*      */   
/* 2815 */   public MenuAction getBillOperateActionGroup_Init() { if (context.get("billOperateActionGroup_Init") != null)
/* 2816 */       return (MenuAction)context.get("billOperateActionGroup_Init");
/* 2817 */     MenuAction bean = new MenuAction();
/* 2818 */     context.put("billOperateActionGroup_Init", bean);
/* 2819 */     bean.setCode("billOperate");
/* 2820 */     bean.setName(getI18nFB_1816e80());
/* 2821 */     bean.setActions(getManagedList21());
/* 2822 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2823 */     invokeInitializingBean(bean);
/* 2824 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_1816e80() {
/* 2828 */     if (context.get("nc.ui.uif2.I18nFB#1816e80") != null)
/* 2829 */       return (String)context.get("nc.ui.uif2.I18nFB#1816e80");
/* 2830 */     I18nFB bean = new I18nFB();
/* 2831 */     context.put("&nc.ui.uif2.I18nFB#1816e80", bean);bean.setResDir("common");
/* 2832 */     bean.setResId("arapcommonv6-0137");
/* 2833 */     bean.setDefaultValue("���ݲ���");
/* 2834 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2835 */     invokeInitializingBean(bean);
/*      */     try {
/* 2837 */       Object product = bean.getObject();
/* 2838 */       context.put("nc.ui.uif2.I18nFB#1816e80", product);
/* 2839 */       return (String)product;
/*      */     } catch (Exception e) {
/* 2841 */       throw new RuntimeException(e); } }
/*      */   
/* 2843 */   private List getManagedList21() { List list = new ArrayList();list.add(getDeleteAction());list.add(getEditAction());list.add(getDocumentManage());return list;
/*      */   }
/*      */   
/* 2846 */   public MenuAction getBillOperateActionGroup_List() { if (context.get("billOperateActionGroup_List") != null)
/* 2847 */       return (MenuAction)context.get("billOperateActionGroup_List");
/* 2848 */     MenuAction bean = new MenuAction();
/* 2849 */     context.put("billOperateActionGroup_List", bean);
/* 2850 */     bean.setCode("billOperate_List");
/* 2851 */     bean.setName(getI18nFB_7dbc3a());
/* 2852 */     bean.setActions(getManagedList22());
/* 2853 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2854 */     invokeInitializingBean(bean);
/* 2855 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_7dbc3a() {
/* 2859 */     if (context.get("nc.ui.uif2.I18nFB#7dbc3a") != null)
/* 2860 */       return (String)context.get("nc.ui.uif2.I18nFB#7dbc3a");
/* 2861 */     I18nFB bean = new I18nFB();
/* 2862 */     context.put("&nc.ui.uif2.I18nFB#7dbc3a", bean);bean.setResDir("common");
/* 2863 */     bean.setResId("arapcommonv6-0137");
/* 2864 */     bean.setDefaultValue("���ݲ���");
/* 2865 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2866 */     invokeInitializingBean(bean);
/*      */     try {
/* 2868 */       Object product = bean.getObject();
/* 2869 */       context.put("nc.ui.uif2.I18nFB#7dbc3a", product);
/* 2870 */       return (String)product;
/*      */     } catch (Exception e) {
/* 2872 */       throw new RuntimeException(e); } }
/*      */   
/* 2874 */   private List getManagedList22() { List list = new ArrayList();list.add(getDeleteAction());list.add(getDocumentManage());return list;
/*      */   }
/*      */   
/* 2877 */   public MenuAction getCloseOperateAction() { if (context.get("closeOperateAction") != null)
/* 2878 */       return (MenuAction)context.get("closeOperateAction");
/* 2879 */     MenuAction bean = new MenuAction();
/* 2880 */     context.put("closeOperateAction", bean);
/* 2881 */     bean.setCode("billClose");
/* 2882 */     bean.setName(getI18nFB_d09379());
/* 2883 */     bean.setActions(getManagedList23());
/* 2884 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2885 */     invokeInitializingBean(bean);
/* 2886 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_d09379() {
/* 2890 */     if (context.get("nc.ui.uif2.I18nFB#d09379") != null)
/* 2891 */       return (String)context.get("nc.ui.uif2.I18nFB#d09379");
/* 2892 */     I18nFB bean = new I18nFB();
/* 2893 */     context.put("&nc.ui.uif2.I18nFB#d09379", bean);bean.setResDir("common");
/* 2894 */     bean.setResId("arapcommonv6-0138");
/* 2895 */     bean.setDefaultValue("�رղ���");
/* 2896 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2897 */     invokeInitializingBean(bean);
/*      */     try {
/* 2899 */       Object product = bean.getObject();
/* 2900 */       context.put("nc.ui.uif2.I18nFB#d09379", product);
/* 2901 */       return (String)product;
/*      */     } catch (Exception e) {
/* 2903 */       throw new RuntimeException(e); } }
/*      */   
/* 2905 */   private List getManagedList23() { List list = new ArrayList();return list;
/*      */   }
/*      */   
/* 2908 */   public nc.funcnode.ui.action.GroupAction getBillApproveActionGroup() { if (context.get("billApproveActionGroup") != null)
/* 2909 */       return (nc.funcnode.ui.action.GroupAction)context.get("billApproveActionGroup");
/* 2910 */     nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
/* 2911 */     context.put("billApproveActionGroup", bean);
/* 2912 */     bean.setCode("billApproveActionGroup");
/* 2913 */     bean.setName(getI18nFB_1821662());
/* 2914 */     bean.setActions(getManagedList24());
/* 2915 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2916 */     invokeInitializingBean(bean);
/* 2917 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_1821662() {
/* 2921 */     if (context.get("nc.ui.uif2.I18nFB#1821662") != null)
/* 2922 */       return (String)context.get("nc.ui.uif2.I18nFB#1821662");
/* 2923 */     I18nFB bean = new I18nFB();
/* 2924 */     context.put("&nc.ui.uif2.I18nFB#1821662", bean);bean.setResDir("common");
/* 2925 */     bean.setResId("arapcommonv6-0142");
/* 2926 */     bean.setDefaultValue("����");
/* 2927 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2928 */     invokeInitializingBean(bean);
/*      */     try {
/* 2930 */       Object product = bean.getObject();
/* 2931 */       context.put("nc.ui.uif2.I18nFB#1821662", product);
/* 2932 */       return (String)product;
/*      */     } catch (Exception e) {
/* 2934 */       throw new RuntimeException(e); } }
/*      */   
/* 2936 */   private List getManagedList24() { List list = new ArrayList();list.add(getApproveAction());list.add(getUnApproveAction());list.add(getFlowStateQueryAction());return list;
/*      */   }
/*      */   
/* 2939 */   public nc.funcnode.ui.action.GroupAction getBillApproveActionGroup_List() { if (context.get("billApproveActionGroup_List") != null)
/* 2940 */       return (nc.funcnode.ui.action.GroupAction)context.get("billApproveActionGroup_List");
/* 2941 */     nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
/* 2942 */     context.put("billApproveActionGroup_List", bean);
/* 2943 */     bean.setCode("billApproveActionGroup_List");
/* 2944 */     bean.setName(getI18nFB_18b9345());
/* 2945 */     bean.setActions(getManagedList25());
/* 2946 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2947 */     invokeInitializingBean(bean);
/* 2948 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_18b9345() {
/* 2952 */     if (context.get("nc.ui.uif2.I18nFB#18b9345") != null)
/* 2953 */       return (String)context.get("nc.ui.uif2.I18nFB#18b9345");
/* 2954 */     I18nFB bean = new I18nFB();
/* 2955 */     context.put("&nc.ui.uif2.I18nFB#18b9345", bean);bean.setResDir("common");
/* 2956 */     bean.setResId("arapcommonv6-0142");
/* 2957 */     bean.setDefaultValue("����");
/* 2958 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2959 */     invokeInitializingBean(bean);
/*      */     try {
/* 2961 */       Object product = bean.getObject();
/* 2962 */       context.put("nc.ui.uif2.I18nFB#18b9345", product);
/* 2963 */       return (String)product;
/*      */     } catch (Exception e) {
/* 2965 */       throw new RuntimeException(e); } }
/*      */   
/* 2967 */   private List getManagedList25() { List list = new ArrayList();list.add(getApproveListAction());list.add(getUnApproveListAction());list.add(getApproveListBatchAction());list.add(getUnApproveListBatchAction());list.add(getFlowStateQueryAction());return list;
/*      */   }
/*      */   
/* 2970 */   public nc.funcnode.ui.action.GroupAction getBillVerifyActionGroup() { if (context.get("billVerifyActionGroup") != null)
/* 2971 */       return (nc.funcnode.ui.action.GroupAction)context.get("billVerifyActionGroup");
/* 2972 */     nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
/* 2973 */     context.put("billVerifyActionGroup", bean);
/* 2974 */     bean.setCode("billVerify");
/* 2975 */     bean.setName(getI18nFB_71b380());
/* 2976 */     bean.setActions(getManagedList26());
/* 2977 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2978 */     invokeInitializingBean(bean);
/* 2979 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_71b380() {
/* 2983 */     if (context.get("nc.ui.uif2.I18nFB#71b380") != null)
/* 2984 */       return (String)context.get("nc.ui.uif2.I18nFB#71b380");
/* 2985 */     I18nFB bean = new I18nFB();
/* 2986 */     context.put("&nc.ui.uif2.I18nFB#71b380", bean);bean.setResDir("common");
/* 2987 */     bean.setResId("arapcommonv6-0143");
/* 2988 */     bean.setDefaultValue("��ʱ����");
/* 2989 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2990 */     invokeInitializingBean(bean);
/*      */     try {
/* 2992 */       Object product = bean.getObject();
/* 2993 */       context.put("nc.ui.uif2.I18nFB#71b380", product);
/* 2994 */       return (String)product;
/*      */     } catch (Exception e) {
/* 2996 */       throw new RuntimeException(e); } }
/*      */   
/* 2998 */   private List getManagedList26() { List list = new ArrayList();list.add(getOnBodyVerify());list.add(getOnWholeBillVerifyAction());return list;
/*      */   }
/*      */   
/* 3001 */   public nc.funcnode.ui.action.GroupAction getPauseTransactActionGroup() { if (context.get("pauseTransactActionGroup") != null)
/* 3002 */       return (nc.funcnode.ui.action.GroupAction)context.get("pauseTransactActionGroup");
/* 3003 */     nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
/* 3004 */     context.put("pauseTransactActionGroup", bean);
/* 3005 */     bean.setCode("pauseTransact");
/* 3006 */     bean.setName(getI18nFB_bfaf4b());
/* 3007 */     bean.setActions(getManagedList27());
/* 3008 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3009 */     invokeInitializingBean(bean);
/* 3010 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_bfaf4b() {
/* 3014 */     if (context.get("nc.ui.uif2.I18nFB#bfaf4b") != null)
/* 3015 */       return (String)context.get("nc.ui.uif2.I18nFB#bfaf4b");
/* 3016 */     I18nFB bean = new I18nFB();
/* 3017 */     context.put("&nc.ui.uif2.I18nFB#bfaf4b", bean);bean.setResDir("2006pub_0");
/* 3018 */     bean.setResId("02006pub-0644");
/* 3019 */     bean.setDefaultValue("�������");
/* 3020 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3021 */     invokeInitializingBean(bean);
/*      */     try {
/* 3023 */       Object product = bean.getObject();
/* 3024 */       context.put("nc.ui.uif2.I18nFB#bfaf4b", product);
/* 3025 */       return (String)product;
/*      */     } catch (Exception e) {
/* 3027 */       throw new RuntimeException(e); } }
/*      */   
/* 3029 */   private List getManagedList27() { List list = new ArrayList();list.add(getPauseTransact());list.add(getCancelPauseTransact());return list;
/*      */   }
/*      */   
/* 3032 */   public nc.funcnode.ui.action.GroupAction getPrintOperateActionGroup() { if (context.get("printOperateActionGroup") != null)
/* 3033 */       return (nc.funcnode.ui.action.GroupAction)context.get("printOperateActionGroup");
/* 3034 */     nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
/* 3035 */     context.put("printOperateActionGroup", bean);
/* 3036 */     bean.setCode("printOperateActionGroup");
/* 3037 */     bean.setName(getI18nFB_8d947f());
/* 3038 */     bean.setActions(getManagedList28());
/* 3039 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3040 */     invokeInitializingBean(bean);
/* 3041 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_8d947f() {
/* 3045 */     if (context.get("nc.ui.uif2.I18nFB#8d947f") != null)
/* 3046 */       return (String)context.get("nc.ui.uif2.I18nFB#8d947f");
/* 3047 */     I18nFB bean = new I18nFB();
/* 3048 */     context.put("&nc.ui.uif2.I18nFB#8d947f", bean);bean.setResDir("common");
/* 3049 */     bean.setResId("arapcommonv6-0139");
/* 3050 */     bean.setDefaultValue("��ӡ");
/* 3051 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3052 */     invokeInitializingBean(bean);
/*      */     try {
/* 3054 */       Object product = bean.getObject();
/* 3055 */       context.put("nc.ui.uif2.I18nFB#8d947f", product);
/* 3056 */       return (String)product;
/*      */     } catch (Exception e) {
/* 3058 */       throw new RuntimeException(e); } }
/*      */   
/* 3060 */   private List getManagedList28() { List list = new ArrayList();list.add(getPrintBill());list.add(getPrintPreview());list.add(getPrintOutput());list.add(getSeparatorAction());list.add(getOfficialPrint());list.add(getCancelPrint());return list;
/*      */   }
/*      */   
/* 3063 */   public nc.funcnode.ui.action.GroupAction getPrintOperateActionGroup_List() { if (context.get("printOperateActionGroup_List") != null)
/* 3064 */       return (nc.funcnode.ui.action.GroupAction)context.get("printOperateActionGroup_List");
/* 3065 */     nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
/* 3066 */     context.put("printOperateActionGroup_List", bean);
/* 3067 */     bean.setCode("printOperateActionGroup_List");
/* 3068 */     bean.setName(getI18nFB_11034b3());
/* 3069 */     bean.setActions(getManagedList29());
/* 3070 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3071 */     invokeInitializingBean(bean);
/* 3072 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_11034b3() {
/* 3076 */     if (context.get("nc.ui.uif2.I18nFB#11034b3") != null)
/* 3077 */       return (String)context.get("nc.ui.uif2.I18nFB#11034b3");
/* 3078 */     I18nFB bean = new I18nFB();
/* 3079 */     context.put("&nc.ui.uif2.I18nFB#11034b3", bean);bean.setResDir("common");
/* 3080 */     bean.setResId("arapcommonv6-0139");
/* 3081 */     bean.setDefaultValue("��ӡ");
/* 3082 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3083 */     invokeInitializingBean(bean);
/*      */     try {
/* 3085 */       Object product = bean.getObject();
/* 3086 */       context.put("nc.ui.uif2.I18nFB#11034b3", product);
/* 3087 */       return (String)product;
/*      */     } catch (Exception e) {
/* 3089 */       throw new RuntimeException(e); } }
/*      */   
/* 3091 */   private List getManagedList29() { List list = new ArrayList();list.add(getPrintBill());list.add(getPrintPreview());list.add(getPrintOutput());list.add(getSeparatorAction());list.add(getOfficialPrint());list.add(getCancelPrint());list.add(getSeparatorAction());list.add(getPrintList());return list;
/*      */   }
/*      */   
/* 3094 */   public MenuAction getRelatedQueryActionGroup() { if (context.get("relatedQueryActionGroup") != null)
/* 3095 */       return (MenuAction)context.get("relatedQueryActionGroup");
/* 3096 */     MenuAction bean = new MenuAction();
/* 3097 */     context.put("relatedQueryActionGroup", bean);
/* 3098 */     bean.setCode("relatedQuery");
/* 3099 */     bean.setName(getI18nFB_e85d26());
/* 3100 */     bean.setActions(getManagedList30());
/* 3101 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3102 */     invokeInitializingBean(bean);
/* 3103 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_e85d26() {
/* 3107 */     if (context.get("nc.ui.uif2.I18nFB#e85d26") != null)
/* 3108 */       return (String)context.get("nc.ui.uif2.I18nFB#e85d26");
/* 3109 */     I18nFB bean = new I18nFB();
/* 3110 */     context.put("&nc.ui.uif2.I18nFB#e85d26", bean);bean.setResDir("pubapp_0");
/* 3111 */     bean.setResId("0pubapp-0025");
/* 3112 */     bean.setDefaultValue("����");
/* 3113 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3114 */     invokeInitializingBean(bean);
/*      */     try {
/* 3116 */       Object product = bean.getObject();
/* 3117 */       context.put("nc.ui.uif2.I18nFB#e85d26", product);
/* 3118 */       return (String)product;
/*      */     } catch (Exception e) {
/* 3120 */       throw new RuntimeException(e); } }
/*      */   
/* 3122 */   private List getManagedList30() { List list = new ArrayList();list.add(getLinkQueryAction());list.add(getLinkSettleInfoAction());list.add(getLinkInformerAction());list.add(getFlowStateQueryAction());list.add(getBalanceLinkQueryAction());list.add(getDealLinkQueryAction());list.add(getLinkVoucharQueryAction());list.add(getBconferQueryAction());list.add(getTbbLinkAction());return list;
/*      */   }
/*      */   
/* 3125 */   public nc.ui.uif2.actions.FirstLineAction getFirstLineAction() { if (context.get("firstLineAction") != null)
/* 3126 */       return (nc.ui.uif2.actions.FirstLineAction)context.get("firstLineAction");
/* 3127 */     nc.ui.uif2.actions.FirstLineAction bean = new nc.ui.uif2.actions.FirstLineAction();
/* 3128 */     context.put("firstLineAction", bean);
/* 3129 */     bean.setModel(getManageAppModel());
/* 3130 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3131 */     invokeInitializingBean(bean);
/* 3132 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.uif2.actions.NextLineAction getNextLineAction() {
/* 3136 */     if (context.get("nextLineAction") != null)
/* 3137 */       return (nc.ui.uif2.actions.NextLineAction)context.get("nextLineAction");
/* 3138 */     nc.ui.uif2.actions.NextLineAction bean = new nc.ui.uif2.actions.NextLineAction();
/* 3139 */     context.put("nextLineAction", bean);
/* 3140 */     bean.setModel(getManageAppModel());
/* 3141 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3142 */     invokeInitializingBean(bean);
/* 3143 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.uif2.actions.PreLineAction getPreLineAction() {
/* 3147 */     if (context.get("preLineAction") != null)
/* 3148 */       return (nc.ui.uif2.actions.PreLineAction)context.get("preLineAction");
/* 3149 */     nc.ui.uif2.actions.PreLineAction bean = new nc.ui.uif2.actions.PreLineAction();
/* 3150 */     context.put("preLineAction", bean);
/* 3151 */     bean.setModel(getManageAppModel());
/* 3152 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3153 */     invokeInitializingBean(bean);
/* 3154 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.uif2.actions.LastLineAction getLastLineAction() {
/* 3158 */     if (context.get("lastLineAction") != null)
/* 3159 */       return (nc.ui.uif2.actions.LastLineAction)context.get("lastLineAction");
/* 3160 */     nc.ui.uif2.actions.LastLineAction bean = new nc.ui.uif2.actions.LastLineAction();
/* 3161 */     context.put("lastLineAction", bean);
/* 3162 */     bean.setModel(getManageAppModel());
/* 3163 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3164 */     invokeInitializingBean(bean);
/* 3165 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.ref.FiBillTypeRefModel getTransTypeRefModel() {
/* 3169 */     if (context.get("transTypeRefModel") != null)
/* 3170 */       return (nc.ui.arap.ref.FiBillTypeRefModel)context.get("transTypeRefModel");
/* 3171 */     nc.ui.arap.ref.FiBillTypeRefModel bean = new nc.ui.arap.ref.FiBillTypeRefModel();
/* 3172 */     context.put("transTypeRefModel", bean);
/* 3173 */     bean.setNodeKeyQry(getNodeKeyQry());
/* 3174 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3175 */     invokeInitializingBean(bean);
/* 3176 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.ReceiptCheckAction getReceiptCheckAction() {
/* 3180 */     if (context.get("receiptCheckAction") != null)
/* 3181 */       return (nc.ui.arap.actions.ReceiptCheckAction)context.get("receiptCheckAction");
/* 3182 */     nc.ui.arap.actions.ReceiptCheckAction bean = new nc.ui.arap.actions.ReceiptCheckAction();
/* 3183 */     context.put("receiptCheckAction", bean);
/* 3184 */     bean.setModel(getManageAppModel());
/* 3185 */     bean.setPk_billtype("F3");
/* 3186 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3187 */     invokeInitializingBean(bean);
/* 3188 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.ReceiptScanAction getReceiptScanAction() {
/* 3192 */     if (context.get("receiptScanAction") != null)
/* 3193 */       return (nc.ui.arap.actions.ReceiptScanAction)context.get("receiptScanAction");
/* 3194 */     nc.ui.arap.actions.ReceiptScanAction bean = new nc.ui.arap.actions.ReceiptScanAction();
/* 3195 */     context.put("receiptScanAction", bean);
/* 3196 */     bean.setModel(getManageAppModel());
/* 3197 */     bean.setPk_billtype("F3");
/* 3198 */     bean.setCheckscanway("swing");
/* 3199 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3200 */     invokeInitializingBean(bean);
/* 3201 */     return bean;
/*      */   }
/*      */   
/*      */   public MenuAction getReceiptMenuAction() {
/* 3205 */     if (context.get("receiptMenuAction") != null)
/* 3206 */       return (MenuAction)context.get("receiptMenuAction");
/* 3207 */     MenuAction bean = new MenuAction();
/* 3208 */     context.put("receiptMenuAction", bean);
/* 3209 */     bean.setCode("Image");
/* 3210 */     bean.setName(getI18nFB_3b519a());
/* 3211 */     bean.setActions(getManagedList31());
/* 3212 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3213 */     invokeInitializingBean(bean);
/* 3214 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_3b519a() {
/* 3218 */     if (context.get("nc.ui.uif2.I18nFB#3b519a") != null)
/* 3219 */       return (String)context.get("nc.ui.uif2.I18nFB#3b519a");
/* 3220 */     I18nFB bean = new I18nFB();
/* 3221 */     context.put("&nc.ui.uif2.I18nFB#3b519a", bean);bean.setResDir("common");
/* 3222 */     bean.setResId("arapcommonv6-0180");
/* 3223 */     bean.setDefaultValue("Ӱ��");
/* 3224 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3225 */     invokeInitializingBean(bean);
/*      */     try {
/* 3227 */       Object product = bean.getObject();
/* 3228 */       context.put("nc.ui.uif2.I18nFB#3b519a", product);
/* 3229 */       return (String)product;
/*      */     } catch (Exception e) {
/* 3231 */       throw new RuntimeException(e); } }
/*      */   
/* 3233 */   private List getManagedList31() { List list = new ArrayList();list.add(getReceiptCheckAction());list.add(getReceiptScanAction());return list;
/*      */   }
/*      */   
/* 3236 */   public String getDefMoneyField() { if (context.get("defMoneyField") != null)
/* 3237 */       return (String)context.get("defMoneyField");
/* 3238 */     String bean = new String("money_de");context.put("defMoneyField", bean);
/* 3239 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3240 */     invokeInitializingBean(bean);
/* 3241 */     return bean;
/*      */   }
/*      */   
/*      */   public String getDefBillType() {
/* 3245 */     if (context.get("defBillType") != null)
/* 3246 */       return (String)context.get("defBillType");
/* 3247 */     String bean = new String("F3");context.put("defBillType", bean);
/* 3248 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3249 */     invokeInitializingBean(bean);
/* 3250 */     return bean;
/*      */   }
/*      */   
/*      */   public String getDefNodeKey() {
/* 3254 */     if (context.get("defNodeKey") != null)
/* 3255 */       return (String)context.get("defNodeKey");
/* 3256 */     String bean = new String("D3");context.put("defNodeKey", bean);
/* 3257 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3258 */     invokeInitializingBean(bean);
/* 3259 */     return bean;
/*      */   }
/*      */   
/*      */   public String getMdFullnameHead() {
/* 3263 */     if (context.get("mdFullnameHead") != null)
/* 3264 */       return (String)context.get("mdFullnameHead");
/* 3265 */     String bean = new String("arap.paybill");context.put("mdFullnameHead", bean);
/* 3266 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3267 */     invokeInitializingBean(bean);
/* 3268 */     return bean;
/*      */   }
/*      */   
/*      */   public String getMdFullnameBody() {
/* 3272 */     if (context.get("mdFullnameBody") != null)
/* 3273 */       return (String)context.get("mdFullnameBody");
/* 3274 */     String bean = new String("arap.payitem");context.put("mdFullnameBody", bean);
/* 3275 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3276 */     invokeInitializingBean(bean);
/* 3277 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.PaybillLineDefValUtil getIArapLineDefValUtil() {
/* 3281 */     if (context.get("iArapLineDefValUtil") != null)
/* 3282 */       return (nc.ui.arap.actions.PaybillLineDefValUtil)context.get("iArapLineDefValUtil");
/* 3283 */     nc.ui.arap.actions.PaybillLineDefValUtil bean = new nc.ui.arap.actions.PaybillLineDefValUtil();
/* 3284 */     context.put("iArapLineDefValUtil", bean);
/* 3285 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3286 */     invokeInitializingBean(bean);
/* 3287 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.model.PayBillAppModelService getManageModelService() {
/* 3291 */     if (context.get("ManageModelService") != null)
/* 3292 */       return (nc.ui.arap.model.PayBillAppModelService)context.get("ManageModelService");
/* 3293 */     nc.ui.arap.model.PayBillAppModelService bean = new nc.ui.arap.model.PayBillAppModelService();
/* 3294 */     context.put("ManageModelService", bean);
/* 3295 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3296 */     invokeInitializingBean(bean);
/* 3297 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.view.PayBillDefValue getBillDefVauleItf() {
/* 3301 */     if (context.get("billDefVauleItf") != null)
/* 3302 */       return (nc.ui.arap.view.PayBillDefValue)context.get("billDefVauleItf");
/* 3303 */     nc.ui.arap.view.PayBillDefValue bean = new nc.ui.arap.view.PayBillDefValue();
/* 3304 */     context.put("billDefVauleItf", bean);
/* 3305 */     bean.setEditor(getBillFormEditor());
/* 3306 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3307 */     invokeInitializingBean(bean);
/* 3308 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.model.PayBillPaginationQueryService getPaginationQueryService() {
/* 3312 */     if (context.get("paginationQueryService") != null)
/* 3313 */       return (nc.ui.arap.model.PayBillPaginationQueryService)context.get("paginationQueryService");
/* 3314 */     nc.ui.arap.model.PayBillPaginationQueryService bean = new nc.ui.arap.model.PayBillPaginationQueryService();
/* 3315 */     context.put("paginationQueryService", bean);
/* 3316 */     bean.setPageSize(Integer.valueOf(10));
/* 3317 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3318 */     invokeInitializingBean(bean);
/* 3319 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.PayBillSaveAction getSaveActionReal() {
/* 3323 */     if (context.get("saveActionReal") != null)
/* 3324 */       return (nc.ui.arap.actions.PayBillSaveAction)context.get("saveActionReal");
/* 3325 */     nc.ui.arap.actions.PayBillSaveAction bean = new nc.ui.arap.actions.PayBillSaveAction();
/* 3326 */     context.put("saveActionReal", bean);
/* 3327 */     bean.setModel(getManageAppModel());
/* 3328 */     bean.setEditor(getBillFormEditor());
/* 3329 */     bean.setInterceptor(getInterceptor());
/* 3330 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3331 */     invokeInitializingBean(bean);
/* 3332 */     return bean;
/*      */   }
//libin
			public nc.ui.arap.actions.CommitAction getCommitAction() {
			    if (context.get("commitAction") != null)
			      return (nc.ui.arap.actions.CommitAction)context.get("commitAction");
			    nc.ui.arap.actions.CommitAction bean = new nc.ui.arap.actions.CommitAction();
			    context.put("commitAction", bean);
			    bean.setModel(getManageAppModel());
			    bean.setEditor(getBillFormEditor());
			    bean.setInterceptor(getInterceptor());
			    setBeanFacotryIfBeanFacatoryAware(bean);
			    invokeInitializingBean(bean);
			    return bean;
			  }
			  public nc.ui.arap.actions.RecallAction getRecallAction() {
				      if (context.get("recallAction") != null)
				        return (nc.ui.arap.actions.RecallAction)context.get("recallAction");
				      nc.ui.arap.actions.RecallAction bean = new nc.ui.arap.actions.RecallAction();
				      context.put("recallAction", bean);
				      bean.setModel(getManageAppModel());
				      bean.setEditor(getBillFormEditor());
				      bean.setInterceptor(getInterceptor());
				      setBeanFacotryIfBeanFacatoryAware(bean);
				      invokeInitializingBean(bean);
				      return bean;
				    }   
/*      */   public nc.ui.arap.viewhandler.cardafter.BodyCommissionAfterEditHandler getBodyCommissionAfterEditHandler() {
/* 3336 */     if (context.get("BodyCommissionAfterEditHandler") != null)
/* 3337 */       return (nc.ui.arap.viewhandler.cardafter.BodyCommissionAfterEditHandler)context.get("BodyCommissionAfterEditHandler");
/* 3338 */     nc.ui.arap.viewhandler.cardafter.BodyCommissionAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.BodyCommissionAfterEditHandler();
/* 3339 */     context.put("BodyCommissionAfterEditHandler", bean);
/* 3340 */     bean.setModel(getManageAppModel());
/* 3341 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3342 */     invokeInitializingBean(bean);
/* 3343 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardbefore.BodyCommissionBoforeEditHandler getBodyCommissionBoforeEditHandler() {
/* 3347 */     if (context.get("BodyCommissionBoforeEditHandler") != null)
/* 3348 */       return (nc.ui.arap.viewhandler.cardbefore.BodyCommissionBoforeEditHandler)context.get("BodyCommissionBoforeEditHandler");
/* 3349 */     nc.ui.arap.viewhandler.cardbefore.BodyCommissionBoforeEditHandler bean = new nc.ui.arap.viewhandler.cardbefore.BodyCommissionBoforeEditHandler();
/* 3350 */     context.put("BodyCommissionBoforeEditHandler", bean);
/* 3351 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3352 */     invokeInitializingBean(bean);
/* 3353 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.DefRelationAfterEditHandler getDefRelationAfterEditHandler() {
/* 3357 */     if (context.get("DefRelationAfterEditHandler") != null)
/* 3358 */       return (nc.ui.arap.viewhandler.DefRelationAfterEditHandler)context.get("DefRelationAfterEditHandler");
/* 3359 */     nc.ui.arap.viewhandler.DefRelationAfterEditHandler bean = new nc.ui.arap.viewhandler.DefRelationAfterEditHandler();
/* 3360 */     context.put("DefRelationAfterEditHandler", bean);
/* 3361 */     bean.setBillform(getBillFormEditor());
/* 3362 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3363 */     invokeInitializingBean(bean);
/* 3364 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.BodyDefRelationAfterEditHandler getBodyDefRelationAfterEditHandler() {
/* 3368 */     if (context.get("BodyDefRelationAfterEditHandler") != null)
/* 3369 */       return (nc.ui.arap.viewhandler.cardafter.BodyDefRelationAfterEditHandler)context.get("BodyDefRelationAfterEditHandler");
/* 3370 */     nc.ui.arap.viewhandler.cardafter.BodyDefRelationAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.BodyDefRelationAfterEditHandler();
/* 3371 */     context.put("BodyDefRelationAfterEditHandler", bean);
/* 3372 */     bean.setBillform(getBillFormEditor());
/* 3373 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3374 */     invokeInitializingBean(bean);
/* 3375 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.pubapp.uif2app.model.AppEventHandlerMediator getAppEventHandlerMediator() {
/* 3379 */     if (context.get("AppEventHandlerMediator") != null)
/* 3380 */       return (nc.ui.pubapp.uif2app.model.AppEventHandlerMediator)context.get("AppEventHandlerMediator");
/* 3381 */     nc.ui.pubapp.uif2app.model.AppEventHandlerMediator bean = new nc.ui.pubapp.uif2app.model.AppEventHandlerMediator();
/* 3382 */     context.put("AppEventHandlerMediator", bean);
/* 3383 */     bean.setModel(getManageAppModel());
/* 3384 */     bean.setHandlerMap(getManagedMap1());
/* 3385 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3386 */     invokeInitializingBean(bean);
/* 3387 */     return bean;
/*      */   }
/*      */   
/* 3390 */   private Map getManagedMap1() { Map map = new java.util.HashMap();map.put("nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent", getManagedList32());map.put("nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent", getManagedList33());map.put("nc.ui.arap.viewhandler.CardBodyAfterEditEvent", getManagedList34());map.put("nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent", getManagedList35());map.put("nc.ui.pubapp.uif2app.event.card.CardBodyAfterRowEditEvent", getManagedList36());map.put("nc.ui.pubapp.uif2app.event.card.CardBodyRowChangedEvent", getManagedList37());map.put("nc.ui.pubapp.uif2app.event.card.CardHeadTailBeforeEditEvent", getManagedList38());map.put("nc.ui.pubapp.uif2app.event.card.CardPanelLoadEvent", getManagedList39());map.put("nc.ui.pubapp.uif2app.event.list.ListPanelLoadEvent", getManagedList40());map.put("nc.ui.pubapp.uif2app.event.list.ListHeadRowChangedEvent", getManagedList41());map.put("nc.ui.pubapp.uif2app.event.list.ListHeadDataChangedEvent", getManagedList42());map.put("nc.ui.pubapp.uif2app.mediator.mutiltrans.NodekeyEvent", getManagedList43());return map; }
/*      */   
/* 3392 */   private List getManagedList32() { List list = new ArrayList();list.add(getBodyBankAccBeforeEditHandler());list.add(getBodyFreeCustBeforeEditHandler());list.add(getBodyProjectBeforeEditHandler());list.add(getBodyAccountRefBeforeEditHandler());list.add(getBodyChecNoRefBeforeEditHandler4Pay());list.add(getBodyCommissionBoforeEditHandler());list.add(getBodyObjTypeBeforeEditHandler());list.add(getBodySummaryBeforeEditHandler());list.add(getBodyVersionRefBeforeEditHandler());list.add(getOtherOrgBodyBeforeEditHandler());list.add(getBodyCrossCheckBeforeHandler());list.add(getBodyCostCenterRefBeforeEditHandler());list.add(getBodyEuroBeforeEditHandler());list.add(getBodyTaxcodeBeforeEditHandler());list.add(getBodyPsnDocBeforeEditHandler());return list; }
/*      */   
/* 3394 */   private List getManagedList33() { List list = new ArrayList();list.add(getBodyAutoAddLineBeforeEditHandler());list.add(getBodyMaterialAfterEditHandler());list.add(getCardAfterEditCalculateHandler());list.add(getBodyPsnDocAfterEditHandler());list.add(getBodyCurrTypeAfterEditHandler());list.add(getBodyLocalMoneyAfterEditHandler());list.add(getBodyRateAfterEditHandler());list.add(getBodyMoneyBalanceAfterEditHandler());list.add(getEnableCtrlBodyAfterEditHandler());list.add(getBodyCheckNoAfterEditHandler());list.add(getBodyCommissionAfterEditHandler());list.add(getOtherOrgBodyAfterEditHandler());list.add(getBodyFundplanAfterEditHandler());list.add(getBodyCuspAfterEditHandler());list.add(getBodyBankAccAfterEditHandler());list.add(getBodyDeptAfterEditHandler());list.add(getBodyVersionRefAfterEditHandler());list.add(getBodyCostCenterAfterEditHandler());list.add(getBodyDefRelationAfterEditHandler());return list; }
/*      */   
/* 3396 */   private List getManagedList34() { List list = new ArrayList();list.add(getBodyEuroAfterEditHandler());list.add(getBodyCuspAfterEditHandler());return list; }
/*      */   
/* 3398 */   private List getManagedList35() { List list = new ArrayList();list.add(getHBRelationAfterEditHandler());list.add(getHeadCurrTypeAfterEditHandler());list.add(getHeadPsnDocAfterEditHandler());list.add(getOtherOrgHeadAfterEdithandler());list.add(getHeadFundplanAfterEditHandler());list.add(getObjTypeHeadAfterEdithandler());list.add(getHeadCuspAfterEditHandler());list.add(getHeadDeptAfterEditHandler());list.add(getHeadVersionRefAfterEditHandler());list.add(getHeadCostCenterAfterEditHandler());list.add(getHeadEuroAfterEditHandler());list.add(getHeadBillDateAfterEditHandler());list.add(getDefRelationAfterEditHandler());return list; }
/*      */   
/* 3400 */   private List getManagedList36() { List list = new ArrayList();list.add(getCardBodyAfterRowEditHandler());return list; }
/*      */   
/* 3402 */   private List getManagedList37() { List list = new ArrayList();list.add(getCardBodyRowChangeHandler());list.add(getSideFormMediator());return list; }
/*      */   
/* 3404 */   private List getManagedList38() { List list = new ArrayList();list.add(getHeadBankAccBeforeEditHandler());list.add(getHeadSummaryBeforeEditHandler());list.add(getHeadVersionRefBeforeEditHandler());list.add(getOtherOrgHeadBeforeEditHandler());list.add(getHeadCrossCheckBeforeHandler());list.add(getHeadObjTypeBeforeEditHandler());list.add(getHeadEuroBeforeEditHandler());list.add(getHeadCostCenterRefBeforeEditHandler());list.add(getHeadAccountRefBeforeEditHandler());list.add(getHeadPsnDocBeforeEditHandler());return list; }
/*      */   
/* 3406 */   private List getManagedList39() { List list = new ArrayList();list.add(getLoadBillCardTemplate());return list; }
/*      */   
/* 3408 */   private List getManagedList40() { List list = new ArrayList();list.add(getLoadBillListTemplate());return list; }
/*      */   
/* 3410 */   private List getManagedList41() { List list = new ArrayList();list.add(getListHeadRowChangeListener());list.add(getSideFormMediator());return list; }
/*      */   
/* 3412 */   private List getManagedList42() { List list = new ArrayList();list.add(getListHeadModelListener());return list; }
/*      */   
/* 3414 */   private List getManagedList43() { List list = new ArrayList();list.add(getAddActionGroup());list.add(getTranstype());return list;
/*      */   }
/*      */   
/* 3417 */   public nc.vo.arappub.calculator.data.RelationItemForCal_Debit getRelationItemForCal() { if (context.get("relationItemForCal") != null)
/* 3418 */       return (nc.vo.arappub.calculator.data.RelationItemForCal_Debit)context.get("relationItemForCal");
/* 3419 */     nc.vo.arappub.calculator.data.RelationItemForCal_Debit bean = new nc.vo.arappub.calculator.data.RelationItemForCal_Debit();
/* 3420 */     context.put("relationItemForCal", bean);
/* 3421 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3422 */     invokeInitializingBean(bean);
/* 3423 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.LinkSettleInfoAction getLinkSettleInfoAction() {
/* 3427 */     if (context.get("linkSettleInfoAction") != null)
/* 3428 */       return (nc.ui.arap.actions.LinkSettleInfoAction)context.get("linkSettleInfoAction");
/* 3429 */     nc.ui.arap.actions.LinkSettleInfoAction bean = new nc.ui.arap.actions.LinkSettleInfoAction();
/* 3430 */     context.put("linkSettleInfoAction", bean);
/* 3431 */     bean.setModel(getManageAppModel());
/* 3432 */     bean.setInterceptor(getInterceptor());
/* 3433 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3434 */     invokeInitializingBean(bean);
/* 3435 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.LinkInformerAction getLinkInformerAction() {
/* 3439 */     if (context.get("linkInformerAction") != null)
/* 3440 */       return (nc.ui.arap.actions.LinkInformerAction)context.get("linkInformerAction");
/* 3441 */     nc.ui.arap.actions.LinkInformerAction bean = new nc.ui.arap.actions.LinkInformerAction();
/* 3442 */     context.put("linkInformerAction", bean);
/* 3443 */     bean.setModel(getManageAppModel());
/* 3444 */     bean.setInterceptor(getInterceptor());
/* 3445 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3446 */     invokeInitializingBean(bean);
/* 3447 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.DebitBillCalculatorFields getCalFldsItf() {
/* 3451 */     if (context.get("calFldsItf") != null)
/* 3452 */       return (nc.ui.arap.viewhandler.DebitBillCalculatorFields)context.get("calFldsItf");
/* 3453 */     nc.ui.arap.viewhandler.DebitBillCalculatorFields bean = new nc.ui.arap.viewhandler.DebitBillCalculatorFields();
/* 3454 */     context.put("calFldsItf", bean);
/* 3455 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3456 */     invokeInitializingBean(bean);
/* 3457 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.TransTypeAction getTranstype() {
/* 3461 */     if (context.get("transtype") != null)
/* 3462 */       return (nc.ui.arap.actions.TransTypeAction)context.get("transtype");
/* 3463 */     nc.ui.arap.actions.TransTypeAction bean = new nc.ui.arap.actions.TransTypeAction();
/* 3464 */     context.put("transtype", bean);
/* 3465 */     bean.setModel(getManageAppModel());
/* 3466 */     bean.setTransTypeRefModel(getTransTypeRefModel());
/* 3467 */     bean.setWherepart(" bd_billtype.parentbilltype ='F3' and pk_group != 'global00000000000000'");
/* 3468 */     bean.setEditor(getBillFormEditor());
/* 3469 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3470 */     invokeInitializingBean(bean);
/* 3471 */     return bean;
/*      */   }
/*      */   
/*      */   public MenuAction getBillAssistantActionGroup() {
/* 3475 */     if (context.get("billAssistantActionGroup") != null)
/* 3476 */       return (MenuAction)context.get("billAssistantActionGroup");
/* 3477 */     MenuAction bean = new MenuAction();
/* 3478 */     context.put("billAssistantActionGroup", bean);
/* 3479 */     bean.setCode("billAssistant");
/* 3480 */     bean.setName(getI18nFB_117b674());
/* 3481 */     bean.setActions(getManagedList44());
/* 3482 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3483 */     invokeInitializingBean(bean);
/* 3484 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_117b674() {
/* 3488 */     if (context.get("nc.ui.uif2.I18nFB#117b674") != null)
/* 3489 */       return (String)context.get("nc.ui.uif2.I18nFB#117b674");
/* 3490 */     I18nFB bean = new I18nFB();
/* 3491 */     context.put("&nc.ui.uif2.I18nFB#117b674", bean);bean.setResDir("2006pub_0");
/* 3492 */     bean.setResId("02006pub-0643");
/* 3493 */     bean.setDefaultValue("��������");
/* 3494 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3495 */     invokeInitializingBean(bean);
/*      */     try {
/* 3497 */       Object product = bean.getObject();
/* 3498 */       context.put("nc.ui.uif2.I18nFB#117b674", product);
/* 3499 */       return (String)product;
/*      */     } catch (Exception e) {
/* 3501 */       throw new RuntimeException(e); } }
/*      */   
/* 3503 */   private List getManagedList44() { List list = new ArrayList();list.add(getDocumentManage());list.add(getAssociateSettInfoAction());return list;
/*      */   }
/*      */   
/* 3506 */   public nc.ui.arap.actions.AssociateSettInfoAction getAssociateSettInfoAction() { if (context.get("associateSettInfoAction") != null)
/* 3507 */       return (nc.ui.arap.actions.AssociateSettInfoAction)context.get("associateSettInfoAction");
/* 3508 */     nc.ui.arap.actions.AssociateSettInfoAction bean = new nc.ui.arap.actions.AssociateSettInfoAction();
/* 3509 */     context.put("associateSettInfoAction", bean);
/* 3510 */     bean.setModel(getManageAppModel());
/* 3511 */     bean.setBilltype("F3");
/* 3512 */     bean.setBillform(getBillFormEditor());
/* 3513 */     bean.setInterceptor(getInterceptor());
/* 3514 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3515 */     invokeInitializingBean(bean);
/* 3516 */     return bean;
/*      */   }
/*      */   
/*      */   public MenuAction getBillAssistantActionGroup_List() {
/* 3520 */     if (context.get("billAssistantActionGroup_List") != null)
/* 3521 */       return (MenuAction)context.get("billAssistantActionGroup_List");
/* 3522 */     MenuAction bean = new MenuAction();
/* 3523 */     context.put("billAssistantActionGroup_List", bean);
/* 3524 */     bean.setCode("billAssistant");
/* 3525 */     bean.setName(getI18nFB_1cb847d());
/* 3526 */     bean.setActions(getManagedList45());
/* 3527 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3528 */     invokeInitializingBean(bean);
/* 3529 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_1cb847d() {
/* 3533 */     if (context.get("nc.ui.uif2.I18nFB#1cb847d") != null)
/* 3534 */       return (String)context.get("nc.ui.uif2.I18nFB#1cb847d");
/* 3535 */     I18nFB bean = new I18nFB();
/* 3536 */     context.put("&nc.ui.uif2.I18nFB#1cb847d", bean);bean.setResDir("2006pub_0");
/* 3537 */     bean.setResId("02006pub-0643");
/* 3538 */     bean.setDefaultValue("��������");
/* 3539 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3540 */     invokeInitializingBean(bean);
/*      */     try {
/* 3542 */       Object product = bean.getObject();
/* 3543 */       context.put("nc.ui.uif2.I18nFB#1cb847d", product);
/* 3544 */       return (String)product;
/*      */     } catch (Exception e) {
/* 3546 */       throw new RuntimeException(e); } }
/*      */   
/* 3548 */   private List getManagedList45() { List list = new ArrayList();list.add(getDocumentManage());list.add(getAssociateSettInfoAction());return list;
/*      */   }
/*      */   
/* 3551 */   public nc.funcnode.ui.action.GroupAction getBillCommissionGroup() { if (context.get("billCommissionGroup") != null)
/* 3552 */       return (nc.funcnode.ui.action.GroupAction)context.get("billCommissionGroup");
/* 3553 */     nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
/* 3554 */     context.put("billCommissionGroup", bean);
/* 3555 */     bean.setCode("billCommissionGroup");
/* 3556 */     bean.setName(getI18nFB_674f5e());
/* 3557 */     bean.setActions(getManagedList46());
/* 3558 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3559 */     invokeInitializingBean(bean);
/* 3560 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_674f5e() {
/* 3564 */     if (context.get("nc.ui.uif2.I18nFB#674f5e") != null)
/* 3565 */       return (String)context.get("nc.ui.uif2.I18nFB#674f5e");
/* 3566 */     I18nFB bean = new I18nFB();
/* 3567 */     context.put("&nc.ui.uif2.I18nFB#674f5e", bean);bean.setResDir("common");
/* 3568 */     bean.setResId("arapcommonv6-0144");
/* 3569 */     bean.setDefaultValue("�и�");
/* 3570 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3571 */     invokeInitializingBean(bean);
/*      */     try {
/* 3573 */       Object product = bean.getObject();
/* 3574 */       context.put("nc.ui.uif2.I18nFB#674f5e", product);
/* 3575 */       return (String)product;
/*      */     } catch (Exception e) {
/* 3577 */       throw new RuntimeException(e); } }
/*      */   
/* 3579 */   private List getManagedList46() { List list = new ArrayList();list.add(getBillCommisionPaySaveAction());list.add(getBillCancelCommisionPayAction());return list;
/*      */   }
/*      */   
/* 3582 */   public nc.ui.arap.listener.PayBillRelationQueryDataListener getInitDataListener() { if (context.get("InitDataListener") != null)
/* 3583 */       return (nc.ui.arap.listener.PayBillRelationQueryDataListener)context.get("InitDataListener");
/* 3584 */     nc.ui.arap.listener.PayBillRelationQueryDataListener bean = new nc.ui.arap.listener.PayBillRelationQueryDataListener();
/* 3585 */     context.put("InitDataListener", bean);
/* 3586 */     bean.setBillFormEditor(getBillFormEditor());
/* 3587 */     bean.setListview(getListView());
/* 3588 */     bean.setContext(getContext());
/* 3589 */     bean.setModel(getManageAppModel());
/* 3590 */     bean.setVoClassName("nc.vo.arap.pay.AggPayBillVO");
/* 3591 */     bean.setAutoShowUpComponent(getBillFormEditor());
/* 3592 */     bean.setQueryAction(getQueryAction());
/* 3593 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3594 */     invokeInitializingBean(bean);
/* 3595 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillCommisionPaySaveAction getBillCommisionPaySaveAction() {
/* 3599 */     if (context.get("BillCommisionPaySaveAction") != null)
/* 3600 */       return (nc.ui.arap.actions.BillCommisionPaySaveAction)context.get("BillCommisionPaySaveAction");
/* 3601 */     nc.ui.arap.actions.BillCommisionPaySaveAction bean = new nc.ui.arap.actions.BillCommisionPaySaveAction();
/* 3602 */     context.put("BillCommisionPaySaveAction", bean);
/* 3603 */     bean.setInterceptor(getInterceptor());
/* 3604 */     bean.setListView(getListView());
/* 3605 */     bean.setContext(getContext());
/* 3606 */     bean.setModel(getManageAppModel());
/* 3607 */     bean.setBillFormEditor(getBillFormEditor());
/* 3608 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3609 */     invokeInitializingBean(bean);
/* 3610 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillCancelCommisionPayAction getBillCancelCommisionPayAction() {
/* 3614 */     if (context.get("BillCancelCommisionPayAction") != null)
/* 3615 */       return (nc.ui.arap.actions.BillCancelCommisionPayAction)context.get("BillCancelCommisionPayAction");
/* 3616 */     nc.ui.arap.actions.BillCancelCommisionPayAction bean = new nc.ui.arap.actions.BillCancelCommisionPayAction();
/* 3617 */     context.put("BillCancelCommisionPayAction", bean);
/* 3618 */     bean.setListView(getListView());
/* 3619 */     bean.setModel(getManageAppModel());
/* 3620 */     bean.setBillFormEditor(getBillFormEditor());
/* 3621 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3622 */     invokeInitializingBean(bean);
/* 3623 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.importable.PayRecordImportablePanel getImportableEditor() {
/* 3627 */     if (context.get("importableEditor") != null)
/* 3628 */       return (nc.ui.arap.importable.PayRecordImportablePanel)context.get("importableEditor");
/* 3629 */     nc.ui.arap.importable.PayRecordImportablePanel bean = new nc.ui.arap.importable.PayRecordImportablePanel();
/* 3630 */     context.put("importableEditor", bean);
/* 3631 */     bean.setUiEditor(getBillFormEditor());
/* 3632 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3633 */     invokeInitializingBean(bean);
/* 3634 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.importable.action.ArapImportAction getImportAction() {
/* 3638 */     if (context.get("importAction") != null)
/* 3639 */       return (nc.ui.arap.importable.action.ArapImportAction)context.get("importAction");
/* 3640 */     nc.ui.arap.importable.action.ArapImportAction bean = new nc.ui.arap.importable.action.ArapImportAction();
/* 3641 */     context.put("importAction", bean);
/* 3642 */     bean.setModel(getManageAppModel());
/* 3643 */     bean.setImportableEditor(getImportableEditor());
/* 3644 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3645 */     invokeInitializingBean(bean);
/* 3646 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.importable.ArapExportAction getExportAction() {
/* 3650 */     if (context.get("exportAction") != null)
/* 3651 */       return (nc.ui.arap.importable.ArapExportAction)context.get("exportAction");
/* 3652 */     nc.ui.arap.importable.ArapExportAction bean = new nc.ui.arap.importable.ArapExportAction();
/* 3653 */     context.put("exportAction", bean);
/* 3654 */     bean.setImportableEditor(getImportableEditor());
/* 3655 */     bean.setModel(getManageAppModel());
/* 3656 */     bean.setInterceptor(getExportActionInterceptor());
/* 3657 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3658 */     invokeInitializingBean(bean);
/* 3659 */     return bean;
/*      */   }
/*      */   
/*      */   public MenuAction getImportexportActionGroup() {
/* 3663 */     if (context.get("importexportActionGroup") != null)
/* 3664 */       return (MenuAction)context.get("importexportActionGroup");
/* 3665 */     MenuAction bean = new MenuAction();
/* 3666 */     context.put("importexportActionGroup", bean);
/* 3667 */     bean.setCode("ImportExportMenu");
/* 3668 */     bean.setName(getI18nFB_21f33d());
/* 3669 */     bean.setActions(getManagedList47());
/* 3670 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3671 */     invokeInitializingBean(bean);
/* 3672 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_21f33d() {
/* 3676 */     if (context.get("nc.ui.uif2.I18nFB#21f33d") != null)
/* 3677 */       return (String)context.get("nc.ui.uif2.I18nFB#21f33d");
/* 3678 */     I18nFB bean = new I18nFB();
/* 3679 */     context.put("&nc.ui.uif2.I18nFB#21f33d", bean);bean.setResDir("2006pub_0");
/* 3680 */     bean.setResId("02006pub-0654");
/* 3681 */     bean.setDefaultValue("���뵼��");
/* 3682 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3683 */     invokeInitializingBean(bean);
/*      */     try {
/* 3685 */       Object product = bean.getObject();
/* 3686 */       context.put("nc.ui.uif2.I18nFB#21f33d", product);
/* 3687 */       return (String)product;
/*      */     } catch (Exception e) {
/* 3689 */       throw new RuntimeException(e); } }
/*      */   
/* 3691 */   private List getManagedList47() { List list = new ArrayList();list.add(getImportAction());list.add(getCurrNullAction());list.add(getExportAction());return list;
/*      */   }
/*      */   
/* 3694 */   public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getListActions() { if (context.get("listActions") != null)
/* 3695 */       return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("listActions");
/* 3696 */     nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getListView());context.put("listActions", bean);
/* 3697 */     bean.setActions(getManagedList48());
/* 3698 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3699 */     invokeInitializingBean(bean);
/* 3700 */     return bean;
/*      */   }
/*      */   
/* 3703 */   private List getManagedList48() { List list = new ArrayList();list.add(getAddActionGroup());list.add(getEditAction());list.add(getDeleteAction());list.add(getCopyAction());list.add(getCurrNullAction());list.add(getQueryAction());list.add(getRefreshAllAction());list.add(getCurrNullAction());list.add(getTranstype());list.add(getBillAssistantActionGroup_List());list.add(getCurrNullAction());list.add(getReceiptMenuAction());list.add(getCurrNullAction());list.add(getRelatedQueryActionGroup());list.add(getCurrNullAction());list.add(getImportexportActionGroup());list.add(getPrintOperateActionGroup_List());return list;
/*      */   }
/*      */   
/* 3706 */   public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getCardActions() { if (context.get("cardActions") != null)
/* 3707 */       return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("cardActions");
/* 3708 */     nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getBillFormEditor());context.put("cardActions", bean);
/* 3709 */     bean.setActions(getManagedList49());
/* 3710 */     bean.setEditActions(getManagedList50());
/* 3711 */     bean.setModel(getManageAppModel());
/* 3712 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3713 */     invokeInitializingBean(bean);
/* 3714 */     return bean;
/*      */   }
/*      */   // libin ����ԭ���水ť��Ϊ �ύ��ť   list.add(getCommitAction());list.add(getRecallAction())
/* 3717 */   private List getManagedList49() { List list = new ArrayList();list.add(getAddActionGroup());list.add(getEditAction());list.add(getDeleteAction());list.add(getCommitAction());list.add(getRecallAction());list.add(getCopyAction());list.add(getCurrNullAction());list.add(getQueryAction());list.add(getRefreshSingAction());list.add(getCurrNullAction());list.add(getTranstype());list.add(getBillAssistantActionGroup());list.add(getCurrNullAction());list.add(getReceiptMenuAction());list.add(getCurrNullAction());list.add(getRelatedQueryActionGroup());list.add(getCurrNullAction());list.add(getImportexportActionGroup());list.add(getPrintOperateActionGroup());return list; }
/*      */   
/* 3719 */   private List getManagedList50() { List list = new ArrayList();list.add(getSaveAction());list.add(getTempSaveAction());list.add(getCurrNullAction());list.add(getCancelAction());list.add(getCurrNullAction());list.add(getDocumentManage());list.add(getReceiptMenuAction());return list;
/*      */   }
/*      */ }

/* Location:           E:\work\Yonyou\home\sdgshome20201022\modules\arap\client\classes
 * Qualified Name:     nc.ui.arap.config.paybill_record
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */