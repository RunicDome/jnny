/*      */ package nc.ui.arap.config;
/*      */ 
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import nc.funcnode.ui.action.MenuAction;
/*      */ import nc.ui.uif2.I18nFB;
/*      */ 
/*      */ public class gatheringbill_record extends nc.ui.uif2.factory.AbstractJavaBeanDefinition
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
/*  146 */   private List getManagedList0() { List list = new ArrayList();list.add(getQueryParam_1b8e3());list.add(getQueryParam_1115ac8());return list;
/*      */   }
/*      */   
/*  149 */   private nc.ui.uif2.userdefitem.QueryParam getQueryParam_1b8e3() { if (context.get("nc.ui.uif2.userdefitem.QueryParam#1b8e3") != null)
/*  150 */       return (nc.ui.uif2.userdefitem.QueryParam)context.get("nc.ui.uif2.userdefitem.QueryParam#1b8e3");
/*  151 */     nc.ui.uif2.userdefitem.QueryParam bean = new nc.ui.uif2.userdefitem.QueryParam();
/*  152 */     context.put("nc.ui.uif2.userdefitem.QueryParam#1b8e3", bean);
/*  153 */     bean.setMdfullname("arap.recbill");
/*  154 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  155 */     invokeInitializingBean(bean);
/*  156 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.uif2.userdefitem.QueryParam getQueryParam_1115ac8() {
/*  160 */     if (context.get("nc.ui.uif2.userdefitem.QueryParam#1115ac8") != null)
/*  161 */       return (nc.ui.uif2.userdefitem.QueryParam)context.get("nc.ui.uif2.userdefitem.QueryParam#1115ac8");
/*  162 */     nc.ui.uif2.userdefitem.QueryParam bean = new nc.ui.uif2.userdefitem.QueryParam();
/*  163 */     context.put("nc.ui.uif2.userdefitem.QueryParam#1115ac8", bean);
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
/*  323 */     bean.setBlankChildrenFilter(getSingleFieldBlankChildrenFilter_5f168f());
/*  324 */     bean.setDefValueItf(getBillDefVauleItf());
/*  325 */     bean.setBodyLineActions(getManagedList2());
/*  326 */     bean.setActions(getManagedList5());
/*  327 */     bean.setUserdefitemPreparator(getUserdefitemContainerPreparator_4ef62f());
/*  328 */     bean.initRealUI();
/*  329 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  330 */     invokeInitializingBean(bean);
/*  331 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.pubapp.uif2app.view.value.SingleFieldBlankChildrenFilter getSingleFieldBlankChildrenFilter_5f168f() {
/*  335 */     if (context.get("nc.ui.pubapp.uif2app.view.value.SingleFieldBlankChildrenFilter#5f168f") != null)
/*  336 */       return (nc.ui.pubapp.uif2app.view.value.SingleFieldBlankChildrenFilter)context.get("nc.ui.pubapp.uif2app.view.value.SingleFieldBlankChildrenFilter#5f168f");
/*  337 */     nc.ui.pubapp.uif2app.view.value.SingleFieldBlankChildrenFilter bean = new nc.ui.pubapp.uif2app.view.value.SingleFieldBlankChildrenFilter();
/*  338 */     context.put("nc.ui.pubapp.uif2app.view.value.SingleFieldBlankChildrenFilter#5f168f", bean);
/*  339 */     bean.setFieldName(getDefMoneyField());
/*  340 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  341 */     invokeInitializingBean(bean);
/*  342 */     return bean;
/*      */   }
/*      */   
/*  345 */   private List getManagedList2() { List list = new ArrayList();list.add(getAddLineAction());list.add(getInsertLineAction_c20679());list.add(getBodyDelLineAction_372496());list.add(getBodyCopyLineAction_430c94());list.add(getBodyPasteLineAction_1ef042d());list.add(getBodyPasteToTailAction_fb5688());list.add(getActionsBarSeparator());list.add(getBodyLineEditAction_ee37be());list.add(getActionsBarSeparator());list.add(getBillBodyZoomAction_18dd3d7());return list;
/*      */   }
/*      */   
/*  348 */   private nc.ui.arap.actions.InsertLineAction getInsertLineAction_c20679() { if (context.get("nc.ui.arap.actions.InsertLineAction#c20679") != null)
/*  349 */       return (nc.ui.arap.actions.InsertLineAction)context.get("nc.ui.arap.actions.InsertLineAction#c20679");
/*  350 */     nc.ui.arap.actions.InsertLineAction bean = new nc.ui.arap.actions.InsertLineAction();
/*  351 */     context.put("nc.ui.arap.actions.InsertLineAction#c20679", bean);
/*  352 */     bean.setHbrealtion(getRelationEditHandler());
/*  353 */     bean.setEditor(getBillFormEditor());
/*  354 */     bean.setIArapLineDefValUtil(getIArapLineDefValUtil());
/*  355 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  356 */     invokeInitializingBean(bean);
/*  357 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.arap.actions.BodyDelLineAction getBodyDelLineAction_372496() {
/*  361 */     if (context.get("nc.ui.arap.actions.BodyDelLineAction#372496") != null)
/*  362 */       return (nc.ui.arap.actions.BodyDelLineAction)context.get("nc.ui.arap.actions.BodyDelLineAction#372496");
/*  363 */     nc.ui.arap.actions.BodyDelLineAction bean = new nc.ui.arap.actions.BodyDelLineAction();
/*  364 */     context.put("nc.ui.arap.actions.BodyDelLineAction#372496", bean);
/*  365 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  366 */     invokeInitializingBean(bean);
/*  367 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.arap.actions.BodyCopyLineAction getBodyCopyLineAction_430c94() {
/*  371 */     if (context.get("nc.ui.arap.actions.BodyCopyLineAction#430c94") != null)
/*  372 */       return (nc.ui.arap.actions.BodyCopyLineAction)context.get("nc.ui.arap.actions.BodyCopyLineAction#430c94");
/*  373 */     nc.ui.arap.actions.BodyCopyLineAction bean = new nc.ui.arap.actions.BodyCopyLineAction();
/*  374 */     context.put("nc.ui.arap.actions.BodyCopyLineAction#430c94", bean);
/*  375 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  376 */     invokeInitializingBean(bean);
/*  377 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.arap.actions.BodyPasteLineAction getBodyPasteLineAction_1ef042d() {
/*  381 */     if (context.get("nc.ui.arap.actions.BodyPasteLineAction#1ef042d") != null)
/*  382 */       return (nc.ui.arap.actions.BodyPasteLineAction)context.get("nc.ui.arap.actions.BodyPasteLineAction#1ef042d");
/*  383 */     nc.ui.arap.actions.BodyPasteLineAction bean = new nc.ui.arap.actions.BodyPasteLineAction();
/*  384 */     context.put("nc.ui.arap.actions.BodyPasteLineAction#1ef042d", bean);
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
/*  395 */   private nc.ui.arap.actions.BodyPasteToTailAction getBodyPasteToTailAction_fb5688() { if (context.get("nc.ui.arap.actions.BodyPasteToTailAction#fb5688") != null)
/*  396 */       return (nc.ui.arap.actions.BodyPasteToTailAction)context.get("nc.ui.arap.actions.BodyPasteToTailAction#fb5688");
/*  397 */     nc.ui.arap.actions.BodyPasteToTailAction bean = new nc.ui.arap.actions.BodyPasteToTailAction();
/*  398 */     context.put("nc.ui.arap.actions.BodyPasteToTailAction#fb5688", bean);
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
/*  409 */   private nc.ui.arap.actions.BodyLineEditAction getBodyLineEditAction_ee37be() { if (context.get("nc.ui.arap.actions.BodyLineEditAction#ee37be") != null)
/*  410 */       return (nc.ui.arap.actions.BodyLineEditAction)context.get("nc.ui.arap.actions.BodyLineEditAction#ee37be");
/*  411 */     nc.ui.arap.actions.BodyLineEditAction bean = new nc.ui.arap.actions.BodyLineEditAction();
/*  412 */     context.put("nc.ui.arap.actions.BodyLineEditAction#ee37be", bean);
/*  413 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  414 */     invokeInitializingBean(bean);
/*  415 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.arap.actions.BillBodyZoomAction getBillBodyZoomAction_18dd3d7() {
/*  419 */     if (context.get("nc.ui.arap.actions.BillBodyZoomAction#18dd3d7") != null)
/*  420 */       return (nc.ui.arap.actions.BillBodyZoomAction)context.get("nc.ui.arap.actions.BillBodyZoomAction#18dd3d7");
/*  421 */     nc.ui.arap.actions.BillBodyZoomAction bean = new nc.ui.arap.actions.BillBodyZoomAction();
/*  422 */     context.put("nc.ui.arap.actions.BillBodyZoomAction#18dd3d7", bean);
/*  423 */     setBeanFacotryIfBeanFacatoryAware(bean);
/*  424 */     invokeInitializingBean(bean);
/*  425 */     return bean;
/*      */   }
/*      */   
/*  428 */   private List getManagedList5() { List list = new ArrayList();list.add(getFirstLineAction());list.add(getPreLineAction());list.add(getNextLineAction());list.add(getLastLineAction());return list;
/*      */   }
/*      */   
/*  431 */   private nc.ui.uif2.editor.UserdefitemContainerPreparator getUserdefitemContainerPreparator_4ef62f() { if (context.get("nc.ui.uif2.editor.UserdefitemContainerPreparator#4ef62f") != null)
/*  432 */       return (nc.ui.uif2.editor.UserdefitemContainerPreparator)context.get("nc.ui.uif2.editor.UserdefitemContainerPreparator#4ef62f");
/*  433 */     nc.ui.uif2.editor.UserdefitemContainerPreparator bean = new nc.ui.uif2.editor.UserdefitemContainerPreparator();
/*  434 */     context.put("nc.ui.uif2.editor.UserdefitemContainerPreparator#4ef62f", bean);
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
/* 1259 */   public nc.ui.uif2.components.widget.BesideWidget getMultiBesideWidget() { if (context.get("multiBesideWidget") != null)
/* 1260 */       return (nc.ui.uif2.components.widget.BesideWidget)context.get("multiBesideWidget");
/* 1261 */     nc.ui.uif2.components.widget.BesideWidget bean = new nc.ui.uif2.components.widget.BesideWidget();
/* 1262 */     context.put("multiBesideWidget", bean);
/* 1263 */     bean.setBesideWidgetlets(getManagedList8());
/* 1264 */     bean.setContext(getContext());
/* 1265 */     bean.initUI();
/* 1266 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1267 */     invokeInitializingBean(bean);
/* 1268 */     return bean;
/*      */   }
/*      */   
/* 1271 */   private List getManagedList8() { List list = new ArrayList();list.add(getCreditForm());return list;
/*      */   }
/*      */   
/* 1274 */   public nc.ui.arap.sideforms.ShowAllAction getShowAllAction() { if (context.get("showAllAction") != null)
/* 1275 */       return (nc.ui.arap.sideforms.ShowAllAction)context.get("showAllAction");
/* 1276 */     nc.ui.arap.sideforms.ShowAllAction bean = new nc.ui.arap.sideforms.ShowAllAction();
/* 1277 */     context.put("showAllAction", bean);
/* 1278 */     bean.setMediator(getSideFormMediator());
/* 1279 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1280 */     invokeInitializingBean(bean);
/* 1281 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.sideforms.ArapSideFormMediator getSideFormMediator() {
/* 1285 */     if (context.get("sideFormMediator") != null)
/* 1286 */       return (nc.ui.arap.sideforms.ArapSideFormMediator)context.get("sideFormMediator");
/* 1287 */     nc.ui.arap.sideforms.ArapSideFormMediator bean = new nc.ui.arap.sideforms.ArapSideFormMediator();
/* 1288 */     context.put("sideFormMediator", bean);
/* 1289 */     bean.setSideFormList(getManagedList9());
/* 1290 */     bean.setCardPanel(getBillFormEditor());
/* 1291 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1292 */     invokeInitializingBean(bean);
/* 1293 */     return bean;
/*      */   }
/*      */   
/* 1296 */   private List getManagedList9() { List list = new ArrayList();list.add(getTermForm());list.add(getCreditForm());return list;
/*      */   }
/*      */   
/* 1299 */   public nc.ui.arap.sideforms.TermForm getTermForm() { if (context.get("termForm") != null)
/* 1300 */       return (nc.ui.arap.sideforms.TermForm)context.get("termForm");
/* 1301 */     nc.ui.arap.sideforms.TermForm bean = new nc.ui.arap.sideforms.TermForm();
/* 1302 */     context.put("termForm", bean);
/* 1303 */     bean.setModel(getManageAppModel());
/* 1304 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1305 */     invokeInitializingBean(bean);
/* 1306 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.sideforms.CreditForm getCreditForm() {
/* 1310 */     if (context.get("creditForm") != null)
/* 1311 */       return (nc.ui.arap.sideforms.CreditForm)context.get("creditForm");
/* 1312 */     nc.ui.arap.sideforms.CreditForm bean = new nc.ui.arap.sideforms.CreditForm();
/* 1313 */     context.put("creditForm", bean);
/* 1314 */     bean.setModel(getManageAppModel());
/* 1315 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1316 */     invokeInitializingBean(bean);
/* 1317 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.sideforms.ShowAllActionForm getShowAllActionForm() {
/* 1321 */     if (context.get("showAllActionForm") != null)
/* 1322 */       return (nc.ui.arap.sideforms.ShowAllActionForm)context.get("showAllActionForm");
/* 1323 */     nc.ui.arap.sideforms.ShowAllActionForm bean = new nc.ui.arap.sideforms.ShowAllActionForm();
/* 1324 */     context.put("showAllActionForm", bean);
/* 1325 */     bean.setShowAllAction(getShowAllAction());
/* 1326 */     bean.setModel(getManageAppModel());
/* 1327 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1328 */     invokeInitializingBean(bean);
/* 1329 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.sideforms.ArapBesideApproveForm getBesideApproveForm() {
/* 1333 */     if (context.get("besideApproveForm") != null)
/* 1334 */       return (nc.ui.arap.sideforms.ArapBesideApproveForm)context.get("besideApproveForm");
/* 1335 */     nc.ui.arap.sideforms.ArapBesideApproveForm bean = new nc.ui.arap.sideforms.ArapBesideApproveForm();
/* 1336 */     context.put("besideApproveForm", bean);
/* 1337 */     bean.setModel(getManageAppModel());
/* 1338 */     bean.setBesideapproveAction(getBillApproveAction());
/* 1339 */     bean.setBesideunapproveaction(getBillUnApproveAction());
/* 1340 */     bean.setBillSourceEditor(getBillFormEditor());
/* 1341 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1342 */     invokeInitializingBean(bean);
/* 1343 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.sideforms.ArapBesideHistoryForm getBesideHistoryForm() {
/* 1347 */     if (context.get("besideHistoryForm") != null)
/* 1348 */       return (nc.ui.arap.sideforms.ArapBesideHistoryForm)context.get("besideHistoryForm");
/* 1349 */     nc.ui.arap.sideforms.ArapBesideHistoryForm bean = new nc.ui.arap.sideforms.ArapBesideHistoryForm();
/* 1350 */     context.put("besideHistoryForm", bean);
/* 1351 */     bean.setModel(getManageAppModel());
/* 1352 */     bean.setBillSourceEditor(getBillFormEditor());
/* 1353 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1354 */     invokeInitializingBean(bean);
/* 1355 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.sideforms.ArapBesideHintMessageForm getBesideHintMessageForm() {
/* 1359 */     if (context.get("besideHintMessageForm") != null)
/* 1360 */       return (nc.ui.arap.sideforms.ArapBesideHintMessageForm)context.get("besideHintMessageForm");
/* 1361 */     nc.ui.arap.sideforms.ArapBesideHintMessageForm bean = new nc.ui.arap.sideforms.ArapBesideHintMessageForm();
/* 1362 */     context.put("besideHintMessageForm", bean);
/* 1363 */     bean.setModel(getManageAppModel());
/* 1364 */     bean.setBillSourceEditor(getBillFormEditor());
/* 1365 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1366 */     invokeInitializingBean(bean);
/* 1367 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillApproveAction getBillApproveAction() {
/* 1371 */     if (context.get("BillApproveAction") != null)
/* 1372 */       return (nc.ui.arap.actions.BillApproveAction)context.get("BillApproveAction");
/* 1373 */     nc.ui.arap.actions.BillApproveAction bean = new nc.ui.arap.actions.BillApproveAction();
/* 1374 */     context.put("BillApproveAction", bean);
/* 1375 */     bean.setModel(getManageAppModel());
/* 1376 */     bean.setEditor(getBillFormEditor());
/* 1377 */     bean.setInterceptor(getInterceptor());
/* 1378 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1379 */     invokeInitializingBean(bean);
/* 1380 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillUnApproveAction getBillUnApproveAction() {
/* 1384 */     if (context.get("BillUnApproveAction") != null)
/* 1385 */       return (nc.ui.arap.actions.BillUnApproveAction)context.get("BillUnApproveAction");
/* 1386 */     nc.ui.arap.actions.BillUnApproveAction bean = new nc.ui.arap.actions.BillUnApproveAction();
/* 1387 */     context.put("BillUnApproveAction", bean);
/* 1388 */     bean.setModel(getManageAppModel());
/* 1389 */     bean.setEditor(getBillFormEditor());
/* 1390 */     bean.setInterceptor(getInterceptor());
/* 1391 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1392 */     invokeInitializingBean(bean);
/* 1393 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.pub.remote.RetAddLoader getRetAddLoader() {
/* 1397 */     if (context.get("retAddLoader") != null)
/* 1398 */       return (nc.ui.arap.pub.remote.RetAddLoader)context.get("retAddLoader");
/* 1399 */     nc.ui.arap.pub.remote.RetAddLoader bean = new nc.ui.arap.pub.remote.RetAddLoader();
/* 1400 */     context.put("retAddLoader", bean);
/* 1401 */     bean.setBilltype(getDefBillType());
/* 1402 */     bean.setTranstype(getNodeKeyQry());
/* 1403 */     bean.prepare();
/* 1404 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1405 */     invokeInitializingBean(bean);
/* 1406 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.ArapAddFlowMenuAciton getAddActionGroup() {
/* 1410 */     if (context.get("addActionGroup") != null)
/* 1411 */       return (nc.ui.arap.actions.ArapAddFlowMenuAciton)context.get("addActionGroup");
/* 1412 */     nc.ui.arap.actions.ArapAddFlowMenuAciton bean = new nc.ui.arap.actions.ArapAddFlowMenuAciton(getNodeKeyQry(), getBillFormEditor(), getInterceptor());context.put("addActionGroup", bean);
/* 1413 */     bean.setList(getListView());
/* 1414 */     bean.setLoader(getRetAddLoader());
/* 1415 */     bean.setModel(getManageAppModel());
/* 1416 */     bean.setTransferBillViewProcessor(getTransferProcessor());
/* 1417 */     bean.setOrgChangedImpl(getOrgchange());
/* 1418 */     bean.setCode("add");
/* 1419 */     bean.setName(getI18nFB_73c255());
/* 1420 */     bean.refreshChildBtns();
/* 1421 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1422 */     invokeInitializingBean(bean);
/* 1423 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_73c255() {
/* 1427 */     if (context.get("nc.ui.uif2.I18nFB#73c255") != null)
/* 1428 */       return (String)context.get("nc.ui.uif2.I18nFB#73c255");
/* 1429 */     I18nFB bean = new I18nFB();
/* 1430 */     context.put("&nc.ui.uif2.I18nFB#73c255", bean);bean.setResDir("pubapp_0");
/* 1431 */     bean.setResId("0pubapp-0121");
/* 1432 */     bean.setDefaultValue("����");
/* 1433 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1434 */     invokeInitializingBean(bean);
/*      */     try {
/* 1436 */       Object product = bean.getObject();
/* 1437 */       context.put("nc.ui.uif2.I18nFB#73c255", product);
/* 1438 */       return (String)product;
/*      */     } catch (Exception e) {
/* 1440 */       throw new RuntimeException(e);
/*      */     } }
/*      */   
/* 1443 */   public nc.ui.arap.view.ArapTransferBillViewProcessor getTransferProcessor() { if (context.get("transferProcessor") != null)
/* 1444 */       return (nc.ui.arap.view.ArapTransferBillViewProcessor)context.get("transferProcessor");
/* 1445 */     nc.ui.arap.view.ArapTransferBillViewProcessor bean = new nc.ui.arap.view.ArapTransferBillViewProcessor();
/* 1446 */     context.put("transferProcessor", bean);
/* 1447 */     bean.setList(getListView());
/* 1448 */     bean.setTransferLogic(getTransferLogic());
/* 1449 */     bean.setActionContainer(getListActions());
/* 1450 */     bean.setBillForm(getBillFormEditor());
/* 1451 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1452 */     invokeInitializingBean(bean);
/* 1453 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.pubapp.billref.dest.DefaultBillDataLogic getTransferLogic() {
/* 1457 */     if (context.get("transferLogic") != null)
/* 1458 */       return (nc.ui.pubapp.billref.dest.DefaultBillDataLogic)context.get("transferLogic");
/* 1459 */     nc.ui.pubapp.billref.dest.DefaultBillDataLogic bean = new nc.ui.pubapp.billref.dest.DefaultBillDataLogic();
/* 1460 */     context.put("transferLogic", bean);
/* 1461 */     bean.setBillForm(getBillFormEditor());
/* 1462 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1463 */     invokeInitializingBean(bean);
/* 1464 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.importable.interceptor.ExportActionInterceptor getExportActionInterceptor() {
/* 1468 */     if (context.get("exportActionInterceptor") != null)
/* 1469 */       return (nc.ui.arap.importable.interceptor.ExportActionInterceptor)context.get("exportActionInterceptor");
/* 1470 */     nc.ui.arap.importable.interceptor.ExportActionInterceptor bean = new nc.ui.arap.importable.interceptor.ExportActionInterceptor();
/* 1471 */     context.put("exportActionInterceptor", bean);
/* 1472 */     bean.setModel(getManageAppModel());
/* 1473 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1474 */     invokeInitializingBean(bean);
/* 1475 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.interceptor.CompositeActionInterceptor getInterceptor() {
/* 1479 */     if (context.get("interceptor") != null)
/* 1480 */       return (nc.ui.arap.actions.interceptor.CompositeActionInterceptor)context.get("interceptor");
/* 1481 */     nc.ui.arap.actions.interceptor.CompositeActionInterceptor bean = new nc.ui.arap.actions.interceptor.CompositeActionInterceptor();
/* 1482 */     context.put("interceptor", bean);
/* 1483 */     bean.setInterceptors(getManagedList10());
/* 1484 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1485 */     invokeInitializingBean(bean);
/* 1486 */     return bean;
/*      */   }
/*      */   
/* 1489 */   private List getManagedList10() { List list = new ArrayList();list.add(getCaActionInterceptor());list.add(getPauseTransactActionInterceptor());list.add(getBillVersionActionInterceptor());list.add(getOperPowerInterceptor());list.add(getBillVerifyActionInterceptor());list.add(getOrgCheckActionInterceptor());return list;
/*      */   }
/*      */   
/* 1492 */   public nc.ui.arap.model.ArapPageModelDataManager getModelDataManager() { if (context.get("modelDataManager") != null)
/* 1493 */       return (nc.ui.arap.model.ArapPageModelDataManager)context.get("modelDataManager");
/* 1494 */     nc.ui.arap.model.ArapPageModelDataManager bean = new nc.ui.arap.model.ArapPageModelDataManager();
/* 1495 */     context.put("modelDataManager", bean);
/* 1496 */     bean.setModel(getManageAppModel());
/* 1497 */     bean.setService(getManageModelService());
/* 1498 */     bean.setPaginationDelegator(getPaginationDelegator());
/* 1499 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1500 */     invokeInitializingBean(bean);
/* 1501 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.uif2.actions.ActionContributors getToftpanelActionContributors() {
/* 1505 */     if (context.get("toftpanelActionContributors") != null)
/* 1506 */       return (nc.ui.uif2.actions.ActionContributors)context.get("toftpanelActionContributors");
/* 1507 */     nc.ui.uif2.actions.ActionContributors bean = new nc.ui.uif2.actions.ActionContributors();
/* 1508 */     context.put("toftpanelActionContributors", bean);
/* 1509 */     bean.setContributors(getManagedList11());
/* 1510 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1511 */     invokeInitializingBean(bean);
/* 1512 */     return bean;
/*      */   }
/*      */   
/* 1515 */   private List getManagedList11() { List list = new ArrayList();list.add(getListActions());list.add(getCardActions());list.add(getVerifyActions());list.add(getMakeupActions());return list;
/*      */   }
/*      */   
/* 1518 */   public nc.ui.uif2.FunNodeClosingHandler getClosingListener() { if (context.get("ClosingListener") != null)
/* 1519 */       return (nc.ui.uif2.FunNodeClosingHandler)context.get("ClosingListener");
/* 1520 */     nc.ui.uif2.FunNodeClosingHandler bean = new nc.ui.uif2.FunNodeClosingHandler();
/* 1521 */     context.put("ClosingListener", bean);
/* 1522 */     bean.setModel(getManageAppModel());
/* 1523 */     bean.setSaveaction(getSaveAction());
/* 1524 */     bean.setCancelaction(getCancelAction());
/* 1525 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1526 */     invokeInitializingBean(bean);
/* 1527 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.scale.ArapBillCardPanelScaleProcessor getScaleProcessor() {
/* 1531 */     if (context.get("scaleProcessor") != null)
/* 1532 */       return (nc.ui.arap.scale.ArapBillCardPanelScaleProcessor)context.get("scaleProcessor");
/* 1533 */     nc.ui.arap.scale.ArapBillCardPanelScaleProcessor bean = new nc.ui.arap.scale.ArapBillCardPanelScaleProcessor();
/* 1534 */     context.put("scaleProcessor", bean);
/* 1535 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1536 */     invokeInitializingBean(bean);
/* 1537 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.scale.ArapBillListPanelScaleProcessor getListScaleProcessor() {
/* 1541 */     if (context.get("listScaleProcessor") != null)
/* 1542 */       return (nc.ui.arap.scale.ArapBillListPanelScaleProcessor)context.get("listScaleProcessor");
/* 1543 */     nc.ui.arap.scale.ArapBillListPanelScaleProcessor bean = new nc.ui.arap.scale.ArapBillListPanelScaleProcessor();
/* 1544 */     context.put("listScaleProcessor", bean);
/* 1545 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1546 */     invokeInitializingBean(bean);
/* 1547 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.ArapOrgChanged getOrgchange() {
/* 1551 */     if (context.get("orgchange") != null)
/* 1552 */       return (nc.ui.arap.viewhandler.ArapOrgChanged)context.get("orgchange");
/* 1553 */     nc.ui.arap.viewhandler.ArapOrgChanged bean = new nc.ui.arap.viewhandler.ArapOrgChanged();
/* 1554 */     context.put("orgchange", bean);
/* 1555 */     bean.setBcpsp(getScaleProcessor());
/* 1556 */     bean.setHtob(getArapH2B());
/* 1557 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1558 */     invokeInitializingBean(bean);
/* 1559 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.uif2.TangramContainer getContainer() {
/* 1563 */     if (context.get("container") != null)
/* 1564 */       return (nc.ui.uif2.TangramContainer)context.get("container");
/* 1565 */     nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
/* 1566 */     context.put("container", bean);
/* 1567 */     bean.setTangramLayoutRoot(getTBNode_d0b236());
/* 1568 */     bean.initUI();
/* 1569 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1570 */     invokeInitializingBean(bean);
/* 1571 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_d0b236() {
/* 1575 */     if (context.get("nc.ui.uif2.tangramlayout.node.TBNode#d0b236") != null)
/* 1576 */       return (nc.ui.uif2.tangramlayout.node.TBNode)context.get("nc.ui.uif2.tangramlayout.node.TBNode#d0b236");
/* 1577 */     nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
/* 1578 */     context.put("nc.ui.uif2.tangramlayout.node.TBNode#d0b236", bean);
/* 1579 */     bean.setTabs(getManagedList12());
/* 1580 */     bean.setShowMode("CardLayout");
/* 1581 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1582 */     invokeInitializingBean(bean);
/* 1583 */     return bean;
/*      */   }
/*      */   
/* 1586 */   private List getManagedList12() { List list = new ArrayList();list.add(getVSNode_1ae208b());list.add(getHSNode_114ca93());list.add(getCNode_7d3776());list.add(getCNode_19f523f());return list;
/*      */   }
/*      */   
/* 1589 */   private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_1ae208b() { if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#1ae208b") != null)
/* 1590 */       return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#1ae208b");
/* 1591 */     nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
/* 1592 */     context.put("nc.ui.uif2.tangramlayout.node.VSNode#1ae208b", bean);
/* 1593 */     bean.setUp(getCNode_78d236());
/* 1594 */     bean.setDown(getCNode_1fcbc4b());
/* 1595 */     bean.setShowMode("NoDivider");
/* 1596 */     bean.setDividerLocation(30.0F);
/* 1597 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1598 */     invokeInitializingBean(bean);
/* 1599 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.uif2.tangramlayout.node.CNode getCNode_78d236() {
/* 1603 */     if (context.get("nc.ui.uif2.tangramlayout.node.CNode#78d236") != null)
/* 1604 */       return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#78d236");
/* 1605 */     nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
/* 1606 */     context.put("nc.ui.uif2.tangramlayout.node.CNode#78d236", bean);
/* 1607 */     bean.setComponent(getCardInfoPnl());
/* 1608 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1609 */     invokeInitializingBean(bean);
/* 1610 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.uif2.tangramlayout.node.CNode getCNode_1fcbc4b() {
/* 1614 */     if (context.get("nc.ui.uif2.tangramlayout.node.CNode#1fcbc4b") != null)
/* 1615 */       return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#1fcbc4b");
/* 1616 */     nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
/* 1617 */     context.put("nc.ui.uif2.tangramlayout.node.CNode#1fcbc4b", bean);
/* 1618 */     bean.setName(getI18nFB_310bc9());
/* 1619 */     bean.setComponent(getBillFormEditor());
/* 1620 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1621 */     invokeInitializingBean(bean);
/* 1622 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_310bc9() {
/* 1626 */     if (context.get("nc.ui.uif2.I18nFB#310bc9") != null)
/* 1627 */       return (String)context.get("nc.ui.uif2.I18nFB#310bc9");
/* 1628 */     I18nFB bean = new I18nFB();
/* 1629 */     context.put("&nc.ui.uif2.I18nFB#310bc9", bean);bean.setResDir("common");
/* 1630 */     bean.setResId("arapcommonv6-0136");
/* 1631 */     bean.setDefaultValue("��Ƭ");
/* 1632 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1633 */     invokeInitializingBean(bean);
/*      */     try {
/* 1635 */       Object product = bean.getObject();
/* 1636 */       context.put("nc.ui.uif2.I18nFB#310bc9", product);
/* 1637 */       return (String)product;
/*      */     } catch (Exception e) {
/* 1639 */       throw new RuntimeException(e);
/*      */     } }
/*      */   
/* 1642 */   private nc.ui.uif2.tangramlayout.node.HSNode getHSNode_114ca93() { if (context.get("nc.ui.uif2.tangramlayout.node.HSNode#114ca93") != null)
/* 1643 */       return (nc.ui.uif2.tangramlayout.node.HSNode)context.get("nc.ui.uif2.tangramlayout.node.HSNode#114ca93");
/* 1644 */     nc.ui.uif2.tangramlayout.node.HSNode bean = new nc.ui.uif2.tangramlayout.node.HSNode();
/* 1645 */     context.put("nc.ui.uif2.tangramlayout.node.HSNode#114ca93", bean);
/* 1646 */     bean.setLeft(getCNode_dab4b3());
/* 1647 */     bean.setRight(getVSNode_12300f8());
/* 1648 */     bean.setDividerLocation(0.2F);
/* 1649 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1650 */     invokeInitializingBean(bean);
/* 1651 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.uif2.tangramlayout.node.CNode getCNode_dab4b3() {
/* 1655 */     if (context.get("nc.ui.uif2.tangramlayout.node.CNode#dab4b3") != null)
/* 1656 */       return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#dab4b3");
/* 1657 */     nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
/* 1658 */     context.put("nc.ui.uif2.tangramlayout.node.CNode#dab4b3", bean);
/* 1659 */     bean.setComponent(getQueryAreaShell());
/* 1660 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1661 */     invokeInitializingBean(bean);
/* 1662 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_12300f8() {
/* 1666 */     if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#12300f8") != null)
/* 1667 */       return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#12300f8");
/* 1668 */     nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
/* 1669 */     context.put("nc.ui.uif2.tangramlayout.node.VSNode#12300f8", bean);
/* 1670 */     bean.setUp(getCNode_8d5642());
/* 1671 */     bean.setDown(getCNode_6b6e7d());
/* 1672 */     bean.setShowMode("NoDivider");
/* 1673 */     bean.setDividerLocation(30.0F);
/* 1674 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1675 */     invokeInitializingBean(bean);
/* 1676 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.uif2.tangramlayout.node.CNode getCNode_8d5642() {
/* 1680 */     if (context.get("nc.ui.uif2.tangramlayout.node.CNode#8d5642") != null)
/* 1681 */       return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#8d5642");
/* 1682 */     nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
/* 1683 */     context.put("nc.ui.uif2.tangramlayout.node.CNode#8d5642", bean);
/* 1684 */     bean.setComponent(getQueryInfo());
/* 1685 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1686 */     invokeInitializingBean(bean);
/* 1687 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.uif2.tangramlayout.node.CNode getCNode_6b6e7d() {
/* 1691 */     if (context.get("nc.ui.uif2.tangramlayout.node.CNode#6b6e7d") != null)
/* 1692 */       return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#6b6e7d");
/* 1693 */     nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
/* 1694 */     context.put("nc.ui.uif2.tangramlayout.node.CNode#6b6e7d", bean);
/* 1695 */     bean.setName(getI18nFB_11a20fb());
/* 1696 */     bean.setComponent(getListView());
/* 1697 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1698 */     invokeInitializingBean(bean);
/* 1699 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_11a20fb() {
/* 1703 */     if (context.get("nc.ui.uif2.I18nFB#11a20fb") != null)
/* 1704 */       return (String)context.get("nc.ui.uif2.I18nFB#11a20fb");
/* 1705 */     I18nFB bean = new I18nFB();
/* 1706 */     context.put("&nc.ui.uif2.I18nFB#11a20fb", bean);bean.setResDir("common");
/* 1707 */     bean.setResId("arapcommonv6-0135");
/* 1708 */     bean.setDefaultValue("�б�");
/* 1709 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1710 */     invokeInitializingBean(bean);
/*      */     try {
/* 1712 */       Object product = bean.getObject();
/* 1713 */       context.put("nc.ui.uif2.I18nFB#11a20fb", product);
/* 1714 */       return (String)product;
/*      */     } catch (Exception e) {
/* 1716 */       throw new RuntimeException(e);
/*      */     } }
/*      */   
/* 1719 */   private nc.ui.uif2.tangramlayout.node.CNode getCNode_7d3776() { if (context.get("nc.ui.uif2.tangramlayout.node.CNode#7d3776") != null)
/* 1720 */       return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#7d3776");
/* 1721 */     nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
/* 1722 */     context.put("nc.ui.uif2.tangramlayout.node.CNode#7d3776", bean);
/* 1723 */     bean.setName(getI18nFB_1ffb3eb());
/* 1724 */     bean.setComponent(getVerifyui());
/* 1725 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1726 */     invokeInitializingBean(bean);
/* 1727 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_1ffb3eb() {
/* 1731 */     if (context.get("nc.ui.uif2.I18nFB#1ffb3eb") != null)
/* 1732 */       return (String)context.get("nc.ui.uif2.I18nFB#1ffb3eb");
/* 1733 */     I18nFB bean = new I18nFB();
/* 1734 */     context.put("&nc.ui.uif2.I18nFB#1ffb3eb", bean);bean.setResDir("common");
/* 1735 */     bean.setResId("arapcommonv6-0140");
/* 1736 */     bean.setDefaultValue("��������");
/* 1737 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1738 */     invokeInitializingBean(bean);
/*      */     try {
/* 1740 */       Object product = bean.getObject();
/* 1741 */       context.put("nc.ui.uif2.I18nFB#1ffb3eb", product);
/* 1742 */       return (String)product;
/*      */     } catch (Exception e) {
/* 1744 */       throw new RuntimeException(e);
/*      */     } }
/*      */   
/* 1747 */   private nc.ui.uif2.tangramlayout.node.CNode getCNode_19f523f() { if (context.get("nc.ui.uif2.tangramlayout.node.CNode#19f523f") != null)
/* 1748 */       return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#19f523f");
/* 1749 */     nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
/* 1750 */     context.put("nc.ui.uif2.tangramlayout.node.CNode#19f523f", bean);
/* 1751 */     bean.setName(getI18nFB_c435aa());
/* 1752 */     bean.setComponent(getMakeupEditor());
/* 1753 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1754 */     invokeInitializingBean(bean);
/* 1755 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_c435aa() {
/* 1759 */     if (context.get("nc.ui.uif2.I18nFB#c435aa") != null)
/* 1760 */       return (String)context.get("nc.ui.uif2.I18nFB#c435aa");
/* 1761 */     I18nFB bean = new I18nFB();
/* 1762 */     context.put("&nc.ui.uif2.I18nFB#c435aa", bean);bean.setResDir("common");
/* 1763 */     bean.setResId("arapcommonv6-0141");
/* 1764 */     bean.setDefaultValue("�������");
/* 1765 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1766 */     invokeInitializingBean(bean);
/*      */     try {
/* 1768 */       Object product = bean.getObject();
/* 1769 */       context.put("nc.ui.uif2.I18nFB#c435aa", product);
/* 1770 */       return (String)product;
/*      */     } catch (Exception e) {
/* 1772 */       throw new RuntimeException(e);
/*      */     } }
/*      */   
/* 1775 */   public nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel getQueryInfo() { if (context.get("queryInfo") != null)
/* 1776 */       return (nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel)context.get("queryInfo");
/* 1777 */     nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel bean = new nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel();
/* 1778 */     context.put("queryInfo", bean);
/* 1779 */     bean.setModel(getManageAppModel());
/* 1780 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1781 */     invokeInitializingBean(bean);
/* 1782 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell getQueryAreaShell() {
/* 1786 */     if (context.get("queryAreaShell") != null)
/* 1787 */       return (nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell)context.get("queryAreaShell");
/* 1788 */     nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell bean = new nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell();
/* 1789 */     context.put("queryAreaShell", bean);
/* 1790 */     bean.setQueryAreaCreator(getQueryAction());
/* 1791 */     bean.initUI();
/* 1792 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1793 */     invokeInitializingBean(bean);
/* 1794 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel getCardInfoPnl() {
/* 1798 */     if (context.get("cardInfoPnl") != null)
/* 1799 */       return (nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel)context.get("cardInfoPnl");
/* 1800 */     nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel bean = new nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel();
/* 1801 */     context.put("cardInfoPnl", bean);
/* 1802 */     bean.setActions(getManagedList13());
/* 1803 */     bean.setTitleAction(getReturnaction());
/* 1804 */     bean.setModel(getManageAppModel());
/* 1805 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1806 */     invokeInitializingBean(bean);
/* 1807 */     return bean;
/*      */   }
/*      */   
/* 1810 */   private List getManagedList13() { List list = new ArrayList();list.add(getDocumentManage());list.add(getActionsBarSeparator());list.add(getFirstLineAction());list.add(getPreLineAction());list.add(getNextLineAction());list.add(getLastLineAction());list.add(getActionsBarSeparator());list.add(getBillHeadZoomAction_3101ef());return list;
/*      */   }
/*      */   
/* 1813 */   private nc.ui.arap.actions.BillHeadZoomAction getBillHeadZoomAction_3101ef() { if (context.get("nc.ui.arap.actions.BillHeadZoomAction#3101ef") != null)
/* 1814 */       return (nc.ui.arap.actions.BillHeadZoomAction)context.get("nc.ui.arap.actions.BillHeadZoomAction#3101ef");
/* 1815 */     nc.ui.arap.actions.BillHeadZoomAction bean = new nc.ui.arap.actions.BillHeadZoomAction();
/* 1816 */     context.put("nc.ui.arap.actions.BillHeadZoomAction#3101ef", bean);
/* 1817 */     bean.setModel(getManageAppModel());
/* 1818 */     bean.setBillForm(getBillFormEditor());
/* 1819 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1820 */     invokeInitializingBean(bean);
/* 1821 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.arap.actions.ArapReturnAction getReturnaction() {
/* 1825 */     if (context.get("returnaction") != null)
/* 1826 */       return (nc.ui.arap.actions.ArapReturnAction)context.get("returnaction");
/* 1827 */     nc.ui.arap.actions.ArapReturnAction bean = new nc.ui.arap.actions.ArapReturnAction();
/* 1828 */     context.put("returnaction", bean);
/* 1829 */     bean.setGoComponent(getListView());
/* 1830 */     bean.setSaveAction(getSaveAction());
/* 1831 */     bean.setModel(getManageAppModel());
/* 1832 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1833 */     invokeInitializingBean(bean);
/* 1834 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.commom.OppUIContainer getVerifyui() {
/* 1838 */     if (context.get("verifyui") != null)
/* 1839 */       return (nc.ui.arap.commom.OppUIContainer)context.get("verifyui");
/* 1840 */     nc.ui.arap.commom.OppUIContainer bean = new nc.ui.arap.commom.OppUIContainer();
/* 1841 */     context.put("verifyui", bean);
/* 1842 */     bean.setClosingListener(getClosingListener());
/* 1843 */     bean.initUI();
/* 1844 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1845 */     invokeInitializingBean(bean);
/* 1846 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.pubapp.uif2app.model.BillManageModel getMakeupAppModel() {
/* 1850 */     if (context.get("makeupAppModel") != null)
/* 1851 */       return (nc.ui.pubapp.uif2app.model.BillManageModel)context.get("makeupAppModel");
/* 1852 */     nc.ui.pubapp.uif2app.model.BillManageModel bean = new nc.ui.pubapp.uif2app.model.BillManageModel();
/* 1853 */     context.put("makeupAppModel", bean);
/* 1854 */     bean.setService(getManageModelService());
/* 1855 */     bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
/* 1856 */     bean.setContext(getContext());
/* 1857 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1858 */     invokeInitializingBean(bean);
/* 1859 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.pubapp.uif2app.view.BillCardAllDataValueStrategy getMakeupValueManager() {
/* 1863 */     if (context.get("makeupValueManager") != null)
/* 1864 */       return (nc.ui.pubapp.uif2app.view.BillCardAllDataValueStrategy)context.get("makeupValueManager");
/* 1865 */     nc.ui.pubapp.uif2app.view.BillCardAllDataValueStrategy bean = new nc.ui.pubapp.uif2app.view.BillCardAllDataValueStrategy();
/* 1866 */     context.put("makeupValueManager", bean);
/* 1867 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1868 */     invokeInitializingBean(bean);
/* 1869 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.view.NodeKeyQry getMakeupNodeKeyQry() {
/* 1873 */     if (context.get("makeupNodeKeyQry") != null)
/* 1874 */       return (nc.ui.arap.view.NodeKeyQry)context.get("makeupNodeKeyQry");
/* 1875 */     nc.ui.arap.view.NodeKeyQry bean = new nc.ui.arap.view.NodeKeyQry();
/* 1876 */     context.put("makeupNodeKeyQry", bean);
/* 1877 */     bean.setModel(getMakeupAppModel());
/* 1878 */     bean.setDefNodekey(getDefNodeKey());
/* 1879 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1880 */     invokeInitializingBean(bean);
/* 1881 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.view.ArapBillCardForm getMakeupEditor() {
/* 1885 */     if (context.get("makeupEditor") != null)
/* 1886 */       return (nc.ui.arap.view.ArapBillCardForm)context.get("makeupEditor");
/* 1887 */     nc.ui.arap.view.ArapBillCardForm bean = new nc.ui.arap.view.ArapBillCardForm();
/* 1888 */     context.put("makeupEditor", bean);
/* 1889 */     bean.setModel(getMakeupAppModel());
/* 1890 */     bean.setComponentValueManager(getMakeupValueManager());
/* 1891 */     bean.setNodekeyQry(getMakeupNodeKeyQry());
/* 1892 */     bean.setClosingListener(getClosingListener());
/* 1893 */     bean.setAutoAddLine(false);
/* 1894 */     bean.setTemplateNotNullValidate(true);
/* 1895 */     bean.setUserdefitemPreparator(getUserdefitemContainerPreparator_1762132());
/* 1896 */     bean.initRealUI();
/* 1897 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1898 */     invokeInitializingBean(bean);
/* 1899 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.uif2.editor.UserdefitemContainerPreparator getUserdefitemContainerPreparator_1762132() {
/* 1903 */     if (context.get("nc.ui.uif2.editor.UserdefitemContainerPreparator#1762132") != null)
/* 1904 */       return (nc.ui.uif2.editor.UserdefitemContainerPreparator)context.get("nc.ui.uif2.editor.UserdefitemContainerPreparator#1762132");
/* 1905 */     nc.ui.uif2.editor.UserdefitemContainerPreparator bean = new nc.ui.uif2.editor.UserdefitemContainerPreparator();
/* 1906 */     context.put("nc.ui.uif2.editor.UserdefitemContainerPreparator#1762132", bean);
/* 1907 */     bean.setContainer(getUserdefitemContainer());
/* 1908 */     bean.setParams(getManagedList14());
/* 1909 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1910 */     invokeInitializingBean(bean);
/* 1911 */     return bean;
/*      */   }
/*      */   
/* 1914 */   private List getManagedList14() { List list = new ArrayList();list.add(getCardUserdefitemQueryParam());list.add(getCardUserdefitemQueryParam1());return list;
/*      */   }
/*      */   
/* 1917 */   public nc.ui.arap.actions.BodyVerifyAction getOnBodyVerify() { if (context.get("onBodyVerify") != null)
/* 1918 */       return (nc.ui.arap.actions.BodyVerifyAction)context.get("onBodyVerify");
/* 1919 */     nc.ui.arap.actions.BodyVerifyAction bean = new nc.ui.arap.actions.BodyVerifyAction();
/* 1920 */     context.put("onBodyVerify", bean);
/* 1921 */     bean.setInterceptor(getInterceptor());
/* 1922 */     bean.setModel(getManageAppModel());
/* 1923 */     bean.setCardPanel(getBillFormEditor());
/* 1924 */     bean.setContext(getContext());
/* 1925 */     bean.setOppui(getVerifyui());
/* 1926 */     bean.setListView(getListView());
/* 1927 */     bean.setVerifyui(getVerifyui());
/* 1928 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1929 */     invokeInitializingBean(bean);
/* 1930 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.WholeBillVerifyAction getOnWholeBillVerifyAction() {
/* 1934 */     if (context.get("onWholeBillVerifyAction") != null)
/* 1935 */       return (nc.ui.arap.actions.WholeBillVerifyAction)context.get("onWholeBillVerifyAction");
/* 1936 */     nc.ui.arap.actions.WholeBillVerifyAction bean = new nc.ui.arap.actions.WholeBillVerifyAction();
/* 1937 */     context.put("onWholeBillVerifyAction", bean);
/* 1938 */     bean.setInterceptor(getInterceptor());
/* 1939 */     bean.setModel(getManageAppModel());
/* 1940 */     bean.setCardPanel(getBillFormEditor());
/* 1941 */     bean.setContext(getContext());
/* 1942 */     bean.setListView(getListView());
/* 1943 */     bean.setOppui(getVerifyui());
/* 1944 */     bean.setVerifyui(getVerifyui());
/* 1945 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1946 */     invokeInitializingBean(bean);
/* 1947 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getVerifyActions() {
/* 1951 */     if (context.get("verifyActions") != null)
/* 1952 */       return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("verifyActions");
/* 1953 */     nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getVerifyui());context.put("verifyActions", bean);
/* 1954 */     bean.setActions(getManagedList15());
/* 1955 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1956 */     invokeInitializingBean(bean);
/* 1957 */     return bean;
/*      */   }
/*      */   
/* 1960 */   private List getManagedList15() { List list = new ArrayList();list.add(getVerifyMakeupAction());list.add(getVerifyAllocationAction());list.add(getVerifyIntimeAction());list.add(getVerifyFilterAction());list.add(getVerifyAllSelectedAction());list.add(getVerifyAllCancelAction());list.add(getVerifyGoBackAction());list.add(getVerifyLinkedQueryAction());return list;
/*      */   }
/*      */   
/* 1963 */   public nc.ui.arap.actions.VerifyMakeupAction getVerifyMakeupAction() { if (context.get("verifyMakeupAction") != null)
/* 1964 */       return (nc.ui.arap.actions.VerifyMakeupAction)context.get("verifyMakeupAction");
/* 1965 */     nc.ui.arap.actions.VerifyMakeupAction bean = new nc.ui.arap.actions.VerifyMakeupAction();
/* 1966 */     context.put("verifyMakeupAction", bean);
/* 1967 */     bean.setInterceptor(getInterceptor());
/* 1968 */     bean.setMakeupEditor(getMakeupEditor());
/* 1969 */     bean.setContext(getContext());
/* 1970 */     bean.setModel(getMakeupAppModel());
/* 1971 */     bean.setOrgChangedImpl(getOrgchange());
/* 1972 */     bean.setCardPanel(getBillFormEditor());
/* 1973 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1974 */     invokeInitializingBean(bean);
/* 1975 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.VerifyAllocationAction getVerifyAllocationAction() {
/* 1979 */     if (context.get("verifyAllocationAction") != null)
/* 1980 */       return (nc.ui.arap.actions.VerifyAllocationAction)context.get("verifyAllocationAction");
/* 1981 */     nc.ui.arap.actions.VerifyAllocationAction bean = new nc.ui.arap.actions.VerifyAllocationAction();
/* 1982 */     context.put("verifyAllocationAction", bean);
/* 1983 */     bean.setInterceptor(getInterceptor());
/* 1984 */     bean.setModel(getMakeupAppModel());
/* 1985 */     bean.setCardPanel(getBillFormEditor());
/* 1986 */     bean.setContext(getContext());
/* 1987 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 1988 */     invokeInitializingBean(bean);
/* 1989 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.VerifyIntimeAction getVerifyIntimeAction() {
/* 1993 */     if (context.get("verifyIntimeAction") != null)
/* 1994 */       return (nc.ui.arap.actions.VerifyIntimeAction)context.get("verifyIntimeAction");
/* 1995 */     nc.ui.arap.actions.VerifyIntimeAction bean = new nc.ui.arap.actions.VerifyIntimeAction();
/* 1996 */     context.put("verifyIntimeAction", bean);
/* 1997 */     bean.setInterceptor(getInterceptor());
/* 1998 */     bean.setModel(getMakeupAppModel());
/* 1999 */     bean.setCardPanel(getBillFormEditor());
/* 2000 */     bean.setContext(getContext());
/* 2001 */     bean.setBillFormEditor(getBillFormEditor());
/* 2002 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2003 */     invokeInitializingBean(bean);
/* 2004 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.VerifyFilterAction getVerifyFilterAction() {
/* 2008 */     if (context.get("verifyFilterAction") != null)
/* 2009 */       return (nc.ui.arap.actions.VerifyFilterAction)context.get("verifyFilterAction");
/* 2010 */     nc.ui.arap.actions.VerifyFilterAction bean = new nc.ui.arap.actions.VerifyFilterAction();
/* 2011 */     context.put("verifyFilterAction", bean);
/* 2012 */     bean.setInterceptor(getInterceptor());
/* 2013 */     bean.setModel(getMakeupAppModel());
/* 2014 */     bean.setCardPanel(getBillFormEditor());
/* 2015 */     bean.setContext(getContext());
/* 2016 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2017 */     invokeInitializingBean(bean);
/* 2018 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.VerifyAllSelectedAction getVerifyAllSelectedAction() {
/* 2022 */     if (context.get("verifyAllSelectedAction") != null)
/* 2023 */       return (nc.ui.arap.actions.VerifyAllSelectedAction)context.get("verifyAllSelectedAction");
/* 2024 */     nc.ui.arap.actions.VerifyAllSelectedAction bean = new nc.ui.arap.actions.VerifyAllSelectedAction();
/* 2025 */     context.put("verifyAllSelectedAction", bean);
/* 2026 */     bean.setInterceptor(getInterceptor());
/* 2027 */     bean.setModel(getMakeupAppModel());
/* 2028 */     bean.setCardPanel(getBillFormEditor());
/* 2029 */     bean.setContext(getContext());
/* 2030 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2031 */     invokeInitializingBean(bean);
/* 2032 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.VerifyAllCancelAction getVerifyAllCancelAction() {
/* 2036 */     if (context.get("verifyAllCancelAction") != null)
/* 2037 */       return (nc.ui.arap.actions.VerifyAllCancelAction)context.get("verifyAllCancelAction");
/* 2038 */     nc.ui.arap.actions.VerifyAllCancelAction bean = new nc.ui.arap.actions.VerifyAllCancelAction();
/* 2039 */     context.put("verifyAllCancelAction", bean);
/* 2040 */     bean.setInterceptor(getInterceptor());
/* 2041 */     bean.setModel(getMakeupAppModel());
/* 2042 */     bean.setCardPanel(getBillFormEditor());
/* 2043 */     bean.setContext(getContext());
/* 2044 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2045 */     invokeInitializingBean(bean);
/* 2046 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.VerifyGoBackAction getVerifyGoBackAction() {
/* 2050 */     if (context.get("verifyGoBackAction") != null)
/* 2051 */       return (nc.ui.arap.actions.VerifyGoBackAction)context.get("verifyGoBackAction");
/* 2052 */     nc.ui.arap.actions.VerifyGoBackAction bean = new nc.ui.arap.actions.VerifyGoBackAction();
/* 2053 */     context.put("verifyGoBackAction", bean);
/* 2054 */     bean.setInterceptor(getInterceptor());
/* 2055 */     bean.setBillFormEditor(getBillFormEditor());
/* 2056 */     bean.setModel(getMakeupAppModel());
/* 2057 */     bean.setCardPanel(getBillFormEditor());
/* 2058 */     bean.setContext(getContext());
/* 2059 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2060 */     invokeInitializingBean(bean);
/* 2061 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.VerifyLinkedQueryAction getVerifyLinkedQueryAction() {
/* 2065 */     if (context.get("verifyLinkedQueryAction") != null)
/* 2066 */       return (nc.ui.arap.actions.VerifyLinkedQueryAction)context.get("verifyLinkedQueryAction");
/* 2067 */     nc.ui.arap.actions.VerifyLinkedQueryAction bean = new nc.ui.arap.actions.VerifyLinkedQueryAction();
/* 2068 */     context.put("verifyLinkedQueryAction", bean);
/* 2069 */     bean.setInterceptor(getInterceptor());
/* 2070 */     bean.setModel(getMakeupAppModel());
/* 2071 */     bean.setCardPanel(getBillFormEditor());
/* 2072 */     bean.setContext(getContext());
/* 2073 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2074 */     invokeInitializingBean(bean);
/* 2075 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.MakeupCanelAction getMakeupCanelAction() {
/* 2079 */     if (context.get("makeupCanelAction") != null)
/* 2080 */       return (nc.ui.arap.actions.MakeupCanelAction)context.get("makeupCanelAction");
/* 2081 */     nc.ui.arap.actions.MakeupCanelAction bean = new nc.ui.arap.actions.MakeupCanelAction();
/* 2082 */     context.put("makeupCanelAction", bean);
/* 2083 */     bean.setInterceptor(getInterceptor());
/* 2084 */     bean.setVerifyui(getVerifyui());
/* 2085 */     bean.setModel(getMakeupAppModel());
/* 2086 */     bean.setMakeupEditor(getMakeupEditor());
/* 2087 */     bean.setContext(getContext());
/* 2088 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2089 */     invokeInitializingBean(bean);
/* 2090 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.MakeupConfirmAction getMakeupConfirmAction() {
/* 2094 */     if (context.get("makeupConfirmAction") != null)
/* 2095 */       return (nc.ui.arap.actions.MakeupConfirmAction)context.get("makeupConfirmAction");
/* 2096 */     nc.ui.arap.actions.MakeupConfirmAction bean = new nc.ui.arap.actions.MakeupConfirmAction();
/* 2097 */     context.put("makeupConfirmAction", bean);
/* 2098 */     bean.setInterceptor(getInterceptor());
/* 2099 */     bean.setBillFormEditor(getBillFormEditor());
/* 2100 */     bean.setModel(getMakeupAppModel());
/* 2101 */     bean.setMakeupEditor(getMakeupEditor());
/* 2102 */     bean.setContext(getContext());
/* 2103 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2104 */     invokeInitializingBean(bean);
/* 2105 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getMakeupActions() {
/* 2109 */     if (context.get("makeupActions") != null)
/* 2110 */       return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("makeupActions");
/* 2111 */     nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getMakeupEditor());context.put("makeupActions", bean);
/* 2112 */     bean.setActions(getManagedList16());
/* 2113 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2114 */     invokeInitializingBean(bean);
/* 2115 */     return bean;
/*      */   }
/*      */   
/* 2118 */   private List getManagedList16() { List list = new ArrayList();list.add(getMakeupConfirmAction());list.add(getMakeupCanelAction());return list;
/*      */   }
/*      */   
/* 2121 */   public nc.funcnode.ui.action.SeparatorAction getCurrNullAction() { if (context.get("currNullAction") != null)
/* 2122 */       return (nc.funcnode.ui.action.SeparatorAction)context.get("currNullAction");
/* 2123 */     nc.funcnode.ui.action.SeparatorAction bean = new nc.funcnode.ui.action.SeparatorAction();
/* 2124 */     context.put("currNullAction", bean);
/* 2125 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2126 */     invokeInitializingBean(bean);
/* 2127 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.CreditCheckDecoratorAction getSaveAction() {
/* 2131 */     if (context.get("saveAction") != null)
/* 2132 */       return (nc.ui.arap.actions.CreditCheckDecoratorAction)context.get("saveAction");
/* 2133 */     nc.ui.arap.actions.CreditCheckDecoratorAction bean = new nc.ui.arap.actions.CreditCheckDecoratorAction(getSaveActionReal());context.put("saveAction", bean);
/* 2134 */     bean.setModel(getManageAppModel());
/* 2135 */     bean.setInterceptor(getInterceptor());
/* 2136 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2137 */     invokeInitializingBean(bean);
/* 2138 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillEditAction getEditAction() {
/* 2142 */     if (context.get("editAction") != null)
/* 2143 */       return (nc.ui.arap.actions.BillEditAction)context.get("editAction");
/* 2144 */     nc.ui.arap.actions.BillEditAction bean = new nc.ui.arap.actions.BillEditAction();
/* 2145 */     context.put("editAction", bean);
/* 2146 */     bean.setModel(getManageAppModel());
/* 2147 */     bean.setEditor(getBillFormEditor());
/* 2148 */     bean.setListView(getListView());
/* 2149 */     bean.setInterceptor(getInterceptor());
/* 2150 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2151 */     invokeInitializingBean(bean);
/* 2152 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.CreditCheckDecoratorAction getDeleteAction() {
/* 2156 */     if (context.get("deleteAction") != null)
/* 2157 */       return (nc.ui.arap.actions.CreditCheckDecoratorAction)context.get("deleteAction");
/* 2158 */     nc.ui.arap.actions.CreditCheckDecoratorAction bean = new nc.ui.arap.actions.CreditCheckDecoratorAction(getDeleteActionReal());context.put("deleteAction", bean);
/* 2159 */     bean.setModel(getManageAppModel());
/* 2160 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2161 */     invokeInitializingBean(bean);
/* 2162 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillCancelAction getCancelAction() {
/* 2166 */     if (context.get("cancelAction") != null)
/* 2167 */       return (nc.ui.arap.actions.BillCancelAction)context.get("cancelAction");
/* 2168 */     nc.ui.arap.actions.BillCancelAction bean = new nc.ui.arap.actions.BillCancelAction();
/* 2169 */     context.put("cancelAction", bean);
/* 2170 */     bean.setModel(getManageAppModel());
/* 2171 */     bean.setEditor(getBillFormEditor());
/* 2172 */     bean.setInterceptor(getInterceptor());
/* 2173 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2174 */     invokeInitializingBean(bean);
/* 2175 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillConferCancelAction getConferCancelAction() {
/* 2179 */     if (context.get("conferCancelAction") != null)
/* 2180 */       return (nc.ui.arap.actions.BillConferCancelAction)context.get("conferCancelAction");
/* 2181 */     nc.ui.arap.actions.BillConferCancelAction bean = new nc.ui.arap.actions.BillConferCancelAction();
/* 2182 */     context.put("conferCancelAction", bean);
/* 2183 */     bean.setModel(getManageAppModel());
/* 2184 */     bean.setEditor(getBillFormEditor());
/* 2185 */     bean.setInterceptor(getInterceptor());
/* 2186 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2187 */     invokeInitializingBean(bean);
/* 2188 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillAddAction getAddAction() {
/* 2192 */     if (context.get("addAction") != null)
/* 2193 */       return (nc.ui.arap.actions.BillAddAction)context.get("addAction");
/* 2194 */     nc.ui.arap.actions.BillAddAction bean = new nc.ui.arap.actions.BillAddAction();
/* 2195 */     context.put("addAction", bean);
/* 2196 */     bean.setModel(getManageAppModel());
/* 2197 */     bean.setShowUpComponent(getBillFormEditor());
/* 2198 */     bean.setInterceptor(getInterceptor());
/* 2199 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2200 */     invokeInitializingBean(bean);
/* 2201 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.uif2.actions.AddLineAction getAddline() {
/* 2205 */     if (context.get("addline") != null)
/* 2206 */       return (nc.ui.uif2.actions.AddLineAction)context.get("addline");
/* 2207 */     nc.ui.uif2.actions.AddLineAction bean = new nc.ui.uif2.actions.AddLineAction();
/* 2208 */     context.put("addline", bean);
/* 2209 */     bean.setModel(getManageAppModel());
/* 2210 */     bean.setCardpanel(getBillFormEditor());
/* 2211 */     bean.setInterceptor(getInterceptor());
/* 2212 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2213 */     invokeInitializingBean(bean);
/* 2214 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.query.ArapBillQueryConditionDLGInitializer getQueryDLGInitializer() {
/* 2218 */     if (context.get("queryDLGInitializer") != null)
/* 2219 */       return (nc.ui.arap.query.ArapBillQueryConditionDLGInitializer)context.get("queryDLGInitializer");
/* 2220 */     nc.ui.arap.query.ArapBillQueryConditionDLGInitializer bean = new nc.ui.arap.query.ArapBillQueryConditionDLGInitializer();
/* 2221 */     context.put("queryDLGInitializer", bean);
/* 2222 */     bean.setModel(getManageAppModel());
/* 2223 */     bean.setBillType(getDefBillType());
/* 2224 */     bean.setIsInit("N");
/* 2225 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2226 */     invokeInitializingBean(bean);
/* 2227 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.view.ArapQueryTemplateContainer getQueryTemplateContainer() {
/* 2231 */     if (context.get("queryTemplateContainer") != null)
/* 2232 */       return (nc.ui.arap.view.ArapQueryTemplateContainer)context.get("queryTemplateContainer");
/* 2233 */     nc.ui.arap.view.ArapQueryTemplateContainer bean = new nc.ui.arap.view.ArapQueryTemplateContainer();
/* 2234 */     context.put("queryTemplateContainer", bean);
/* 2235 */     bean.setContext(getContext());
/* 2236 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2237 */     invokeInitializingBean(bean);
/* 2238 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.uif2.editor.UIF2RemoteCallCombinatorCaller getRemoteCallCombinatorCaller() {
/* 2242 */     if (context.get("remoteCallCombinatorCaller") != null)
/* 2243 */       return (nc.ui.uif2.editor.UIF2RemoteCallCombinatorCaller)context.get("remoteCallCombinatorCaller");
/* 2244 */     nc.ui.uif2.editor.UIF2RemoteCallCombinatorCaller bean = new nc.ui.uif2.editor.UIF2RemoteCallCombinatorCaller();
/* 2245 */     context.put("remoteCallCombinatorCaller", bean);
/* 2246 */     bean.setRemoteCallers(getManagedList17());
/* 2247 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2248 */     invokeInitializingBean(bean);
/* 2249 */     return bean;
/*      */   }
/*      */   
/* 2252 */   private List getManagedList17() { List list = new ArrayList();list.add(getQueryTemplateContainer());list.add(getUserdefitemContainer());list.add(getTemplateContainer());list.add(getRetAddLoader());return list;
/*      */   }
/*      */   
/* 2255 */   public nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor getListInterceptor() { if (context.get("listInterceptor") != null)
/* 2256 */       return (nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor)context.get("listInterceptor");
/* 2257 */     nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor();
/* 2258 */     context.put("listInterceptor", bean);
/* 2259 */     bean.setShowUpComponent(getListView());
/* 2260 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2261 */     invokeInitializingBean(bean);
/* 2262 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction getQueryAction() {
/* 2266 */     if (context.get("queryAction") != null)
/* 2267 */       return (nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction)context.get("queryAction");
/* 2268 */     nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction bean = new nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction();
/* 2269 */     context.put("queryAction", bean);
/* 2270 */     bean.setDataManager(getModelDataManager());
/* 2271 */     bean.setQryCondDLGInitializer(getQueryDLGInitializer());
/* 2272 */     bean.setModel(getManageAppModel());
/* 2273 */     bean.setTemplateContainer(getQueryTemplateContainer());
/* 2274 */     bean.setInterceptor(getListInterceptor());
/* 2275 */     bean.setShowUpComponent(getListView());
/* 2276 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2277 */     invokeInitializingBean(bean);
/* 2278 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillLinkQueryAction getLinkQueryAction() {
/* 2282 */     if (context.get("linkQueryAction") != null)
/* 2283 */       return (nc.ui.arap.actions.BillLinkQueryAction)context.get("linkQueryAction");
/* 2284 */     nc.ui.arap.actions.BillLinkQueryAction bean = new nc.ui.arap.actions.BillLinkQueryAction();
/* 2285 */     context.put("linkQueryAction", bean);
/* 2286 */     bean.setModel(getManageAppModel());
/* 2287 */     bean.setInterceptor(getInterceptor());
/* 2288 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2289 */     invokeInitializingBean(bean);
/* 2290 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.LinkVoucharQueryAction getLinkVoucharQueryAction() {
/* 2294 */     if (context.get("linkVoucharQueryAction") != null)
/* 2295 */       return (nc.ui.arap.actions.LinkVoucharQueryAction)context.get("linkVoucharQueryAction");
/* 2296 */     nc.ui.arap.actions.LinkVoucharQueryAction bean = new nc.ui.arap.actions.LinkVoucharQueryAction();
/* 2297 */     context.put("linkVoucharQueryAction", bean);
/* 2298 */     bean.setModel(getManageAppModel());
/* 2299 */     bean.setInterceptor(getInterceptor());
/* 2300 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2301 */     invokeInitializingBean(bean);
/* 2302 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.LinkBConferQueryAction getBconferQueryAction() {
/* 2306 */     if (context.get("bconferQueryAction") != null)
/* 2307 */       return (nc.ui.arap.actions.LinkBConferQueryAction)context.get("bconferQueryAction");
/* 2308 */     nc.ui.arap.actions.LinkBConferQueryAction bean = new nc.ui.arap.actions.LinkBConferQueryAction();
/* 2309 */     context.put("bconferQueryAction", bean);
/* 2310 */     bean.setModel(getManageAppModel());
/* 2311 */     bean.setContainer(getBillFormEditor());
/* 2312 */     bean.setInterceptor(getInterceptor());
/* 2313 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2314 */     invokeInitializingBean(bean);
/* 2315 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillFlowStateQueryAction getFlowStateQueryAction() {
/* 2319 */     if (context.get("flowStateQueryAction") != null)
/* 2320 */       return (nc.ui.arap.actions.BillFlowStateQueryAction)context.get("flowStateQueryAction");
/* 2321 */     nc.ui.arap.actions.BillFlowStateQueryAction bean = new nc.ui.arap.actions.BillFlowStateQueryAction();
/* 2322 */     context.put("flowStateQueryAction", bean);
/* 2323 */     bean.setModel(getManageAppModel());
/* 2324 */     bean.setContainer(getBillFormEditor());
/* 2325 */     bean.setInterceptor(getInterceptor());
/* 2326 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2327 */     invokeInitializingBean(bean);
/* 2328 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillBalanceLinkQueryAction getBalanceLinkQueryAction() {
/* 2332 */     if (context.get("balanceLinkQueryAction") != null)
/* 2333 */       return (nc.ui.arap.actions.BillBalanceLinkQueryAction)context.get("balanceLinkQueryAction");
/* 2334 */     nc.ui.arap.actions.BillBalanceLinkQueryAction bean = new nc.ui.arap.actions.BillBalanceLinkQueryAction();
/* 2335 */     context.put("balanceLinkQueryAction", bean);
/* 2336 */     bean.setModel(getManageAppModel());
/* 2337 */     bean.setEditor(getBillFormEditor());
/* 2338 */     bean.setInterceptor(getInterceptor());
/* 2339 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2340 */     invokeInitializingBean(bean);
/* 2341 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillTbbLinkAction getTbbLinkAction() {
/* 2345 */     if (context.get("tbbLinkAction") != null)
/* 2346 */       return (nc.ui.arap.actions.BillTbbLinkAction)context.get("tbbLinkAction");
/* 2347 */     nc.ui.arap.actions.BillTbbLinkAction bean = new nc.ui.arap.actions.BillTbbLinkAction();
/* 2348 */     context.put("tbbLinkAction", bean);
/* 2349 */     bean.setModel(getManageAppModel());
/* 2350 */     bean.setInterceptor(getInterceptor());
/* 2351 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2352 */     invokeInitializingBean(bean);
/* 2353 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillDealLinkQueryAction getDealLinkQueryAction() {
/* 2357 */     if (context.get("dealLinkQueryAction") != null)
/* 2358 */       return (nc.ui.arap.actions.BillDealLinkQueryAction)context.get("dealLinkQueryAction");
/* 2359 */     nc.ui.arap.actions.BillDealLinkQueryAction bean = new nc.ui.arap.actions.BillDealLinkQueryAction();
/* 2360 */     context.put("dealLinkQueryAction", bean);
/* 2361 */     bean.setModel(getManageAppModel());
/* 2362 */     bean.setContainer(getContainer());
/* 2363 */     bean.setInterceptor(getInterceptor());
/* 2364 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2365 */     invokeInitializingBean(bean);
/* 2366 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.LinkTermQueryAction getTermLinkQueryAction() {
/* 2370 */     if (context.get("termLinkQueryAction") != null)
/* 2371 */       return (nc.ui.arap.actions.LinkTermQueryAction)context.get("termLinkQueryAction");
/* 2372 */     nc.ui.arap.actions.LinkTermQueryAction bean = new nc.ui.arap.actions.LinkTermQueryAction();
/* 2373 */     context.put("termLinkQueryAction", bean);
/* 2374 */     bean.setModel(getManageAppModel());
/* 2375 */     bean.setEditor(getBillFormEditor());
/* 2376 */     bean.setInterceptor(getInterceptor());
/* 2377 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2378 */     invokeInitializingBean(bean);
/* 2379 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.InitBillCancelCloseAction getInitCancelCloseAction() {
/* 2383 */     if (context.get("initCancelCloseAction") != null)
/* 2384 */       return (nc.ui.arap.actions.InitBillCancelCloseAction)context.get("initCancelCloseAction");
/* 2385 */     nc.ui.arap.actions.InitBillCancelCloseAction bean = new nc.ui.arap.actions.InitBillCancelCloseAction();
/* 2386 */     context.put("initCancelCloseAction", bean);
/* 2387 */     bean.setModel(getManageAppModel());
/* 2388 */     bean.setInterceptor(getInterceptor());
/* 2389 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2390 */     invokeInitializingBean(bean);
/* 2391 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillRefreshSingAction getRefreshSingAction() {
/* 2395 */     if (context.get("refreshSingAction") != null)
/* 2396 */       return (nc.ui.arap.actions.BillRefreshSingAction)context.get("refreshSingAction");
/* 2397 */     nc.ui.arap.actions.BillRefreshSingAction bean = new nc.ui.arap.actions.BillRefreshSingAction();
/* 2398 */     context.put("refreshSingAction", bean);
/* 2399 */     bean.setModel(getManageAppModel());
/* 2400 */     bean.setInterceptor(getInterceptor());
/* 2401 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2402 */     invokeInitializingBean(bean);
/* 2403 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.CreditCheckDecoratorAction getApproveAction() {
/* 2407 */     if (context.get("ApproveAction") != null)
/* 2408 */       return (nc.ui.arap.actions.CreditCheckDecoratorAction)context.get("ApproveAction");
/* 2409 */     nc.ui.arap.actions.CreditCheckDecoratorAction bean = new nc.ui.arap.actions.CreditCheckDecoratorAction(getApproveActionReal());context.put("ApproveAction", bean);
/* 2410 */     bean.setModel(getManageAppModel());
/* 2411 */     bean.setInterceptor(getInterceptor());
/* 2412 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2413 */     invokeInitializingBean(bean);
/* 2414 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillUnApproveAction getUnApproveAction() {
/* 2418 */     if (context.get("UnApproveAction") != null)
/* 2419 */       return (nc.ui.arap.actions.BillUnApproveAction)context.get("UnApproveAction");
/* 2420 */     nc.ui.arap.actions.BillUnApproveAction bean = new nc.ui.arap.actions.BillUnApproveAction();
/* 2421 */     context.put("UnApproveAction", bean);
/* 2422 */     bean.setModel(getManageAppModel());
/* 2423 */     bean.setEditor(getBillFormEditor());
/* 2424 */     bean.setInterceptor(getInterceptor());
/* 2425 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2426 */     invokeInitializingBean(bean);
/* 2427 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.CreditCheckDecoratorAction getApproveListBatchAction() {
/* 2431 */     if (context.get("ApproveListBatchAction") != null)
/* 2432 */       return (nc.ui.arap.actions.CreditCheckDecoratorAction)context.get("ApproveListBatchAction");
/* 2433 */     nc.ui.arap.actions.CreditCheckDecoratorAction bean = new nc.ui.arap.actions.CreditCheckDecoratorAction(getApproveListBatchActionReal());context.put("ApproveListBatchAction", bean);
/* 2434 */     bean.setModel(getManageAppModel());
/* 2435 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2436 */     invokeInitializingBean(bean);
/* 2437 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillListUnApproveAction getUnApproveListBatchAction() {
/* 2441 */     if (context.get("UnApproveListBatchAction") != null)
/* 2442 */       return (nc.ui.arap.actions.BillListUnApproveAction)context.get("UnApproveListBatchAction");
/* 2443 */     nc.ui.arap.actions.BillListUnApproveAction bean = new nc.ui.arap.actions.BillListUnApproveAction();
/* 2444 */     context.put("UnApproveListBatchAction", bean);
/* 2445 */     bean.setModel(getManageAppModel());
/* 2446 */     bean.setEditor(getListView());
/* 2447 */     bean.setIsbatch("Y");
/* 2448 */     bean.setInterceptor(getInterceptor());
/* 2449 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2450 */     invokeInitializingBean(bean);
/* 2451 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.CreditCheckDecoratorAction getApproveListAction() {
/* 2455 */     if (context.get("ApproveListAction") != null)
/* 2456 */       return (nc.ui.arap.actions.CreditCheckDecoratorAction)context.get("ApproveListAction");
/* 2457 */     nc.ui.arap.actions.CreditCheckDecoratorAction bean = new nc.ui.arap.actions.CreditCheckDecoratorAction(getApproveListActionReal());context.put("ApproveListAction", bean);
/* 2458 */     bean.setModel(getManageAppModel());
/* 2459 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2460 */     invokeInitializingBean(bean);
/* 2461 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillListUnApproveAction getUnApproveListAction() {
/* 2465 */     if (context.get("UnApproveListAction") != null)
/* 2466 */       return (nc.ui.arap.actions.BillListUnApproveAction)context.get("UnApproveListAction");
/* 2467 */     nc.ui.arap.actions.BillListUnApproveAction bean = new nc.ui.arap.actions.BillListUnApproveAction();
/* 2468 */     context.put("UnApproveListAction", bean);
/* 2469 */     bean.setModel(getManageAppModel());
/* 2470 */     bean.setEditor(getListView());
/* 2471 */     bean.setIsbatch("N");
/* 2472 */     bean.setInterceptor(getInterceptor());
/* 2473 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2474 */     invokeInitializingBean(bean);
/* 2475 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillTempSaveAction getTempSaveAction() {
/* 2479 */     if (context.get("tempSaveAction") != null)
/* 2480 */       return (nc.ui.arap.actions.BillTempSaveAction)context.get("tempSaveAction");
/* 2481 */     nc.ui.arap.actions.BillTempSaveAction bean = new nc.ui.arap.actions.BillTempSaveAction();
/* 2482 */     context.put("tempSaveAction", bean);
/* 2483 */     bean.setModel(getManageAppModel());
/* 2484 */     bean.setEditor(getBillFormEditor());
/* 2485 */     bean.setInterceptor(getInterceptor());
/* 2486 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2487 */     invokeInitializingBean(bean);
/* 2488 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillCopyAction getCopyAction() {
/* 2492 */     if (context.get("copyAction") != null)
/* 2493 */       return (nc.ui.arap.actions.BillCopyAction)context.get("copyAction");
/* 2494 */     nc.ui.arap.actions.BillCopyAction bean = new nc.ui.arap.actions.BillCopyAction();
/* 2495 */     context.put("copyAction", bean);
/* 2496 */     bean.setModel(getManageAppModel());
/* 2497 */     bean.setEditor(getBillFormEditor());
/* 2498 */     bean.setListView(getListView());
/* 2499 */     bean.setInterceptor(getInterceptor());
/* 2500 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2501 */     invokeInitializingBean(bean);
/* 2502 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillWriteBackAction getWriteBack() {
/* 2506 */     if (context.get("writeBack") != null)
/* 2507 */       return (nc.ui.arap.actions.BillWriteBackAction)context.get("writeBack");
/* 2508 */     nc.ui.arap.actions.BillWriteBackAction bean = new nc.ui.arap.actions.BillWriteBackAction();
/* 2509 */     context.put("writeBack", bean);
/* 2510 */     bean.setModel(getManageAppModel());
/* 2511 */     bean.setEditor(getBillFormEditor());
/* 2512 */     bean.setListView(getListView());
/* 2513 */     bean.setInterceptor(getInterceptor());
/* 2514 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2515 */     invokeInitializingBean(bean);
/* 2516 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.DocumentManageAction getDocumentManage() {
/* 2520 */     if (context.get("documentManage") != null)
/* 2521 */       return (nc.ui.arap.actions.DocumentManageAction)context.get("documentManage");
/* 2522 */     nc.ui.arap.actions.DocumentManageAction bean = new nc.ui.arap.actions.DocumentManageAction();
/* 2523 */     context.put("documentManage", bean);
/* 2524 */     bean.setModel(getManageAppModel());
/* 2525 */     bean.setEditor(getBillFormEditor());
/* 2526 */     bean.setInterceptor(getInterceptor());
/* 2527 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2528 */     invokeInitializingBean(bean);
/* 2529 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.RentAffiliatedAction getRentAffiliated() {
/* 2533 */     if (context.get("rentAffiliated") != null)
/* 2534 */       return (nc.ui.arap.actions.RentAffiliatedAction)context.get("rentAffiliated");
/* 2535 */     nc.ui.arap.actions.RentAffiliatedAction bean = new nc.ui.arap.actions.RentAffiliatedAction();
/* 2536 */     context.put("rentAffiliated", bean);
/* 2537 */     bean.setModel(getManageAppModel());
/* 2538 */     bean.setEditor(getBillFormEditor());
/* 2539 */     bean.setInterceptor(getInterceptor());
/* 2540 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2541 */     invokeInitializingBean(bean);
/* 2542 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.NoteRegisterAction getNoteRegister() {
/* 2546 */     if (context.get("noteRegister") != null)
/* 2547 */       return (nc.ui.arap.actions.NoteRegisterAction)context.get("noteRegister");
/* 2548 */     nc.ui.arap.actions.NoteRegisterAction bean = new nc.ui.arap.actions.NoteRegisterAction();
/* 2549 */     context.put("noteRegister", bean);
/* 2550 */     bean.setModel(getManageAppModel());
/* 2551 */     bean.setEditor(getBillFormEditor());
/* 2552 */     bean.setInterceptor(getInterceptor());
/* 2553 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2554 */     invokeInitializingBean(bean);
/* 2555 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.MakeAlterBillAction getMakeAlterBill() {
/* 2559 */     if (context.get("makeAlterBill") != null)
/* 2560 */       return (nc.ui.arap.actions.MakeAlterBillAction)context.get("makeAlterBill");
/* 2561 */     nc.ui.arap.actions.MakeAlterBillAction bean = new nc.ui.arap.actions.MakeAlterBillAction();
/* 2562 */     context.put("makeAlterBill", bean);
/* 2563 */     bean.setModel(getManageAppModel());
/* 2564 */     bean.setEditor(getBillFormEditor());
/* 2565 */     bean.setInterceptor(getInterceptor());
/* 2566 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2567 */     invokeInitializingBean(bean);
/* 2568 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.PauseTransactAction getPauseTransact() {
/* 2572 */     if (context.get("pauseTransact") != null)
/* 2573 */       return (nc.ui.arap.actions.PauseTransactAction)context.get("pauseTransact");
/* 2574 */     nc.ui.arap.actions.PauseTransactAction bean = new nc.ui.arap.actions.PauseTransactAction();
/* 2575 */     context.put("pauseTransact", bean);
/* 2576 */     bean.setModel(getManageAppModel());
/* 2577 */     bean.setEditor(getBillFormEditor());
/* 2578 */     bean.setListView(getListView());
/* 2579 */     bean.setInterceptor(getInterceptor());
/* 2580 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2581 */     invokeInitializingBean(bean);
/* 2582 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.CancelPauseTransactAction getCancelPauseTransact() {
/* 2586 */     if (context.get("cancelPauseTransact") != null)
/* 2587 */       return (nc.ui.arap.actions.CancelPauseTransactAction)context.get("cancelPauseTransact");
/* 2588 */     nc.ui.arap.actions.CancelPauseTransactAction bean = new nc.ui.arap.actions.CancelPauseTransactAction();
/* 2589 */     context.put("cancelPauseTransact", bean);
/* 2590 */     bean.setModel(getManageAppModel());
/* 2591 */     bean.setEditor(getBillFormEditor());
/* 2592 */     bean.setListView(getListView());
/* 2593 */     bean.setInterceptor(getInterceptor());
/* 2594 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2595 */     invokeInitializingBean(bean);
/* 2596 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillPrintAction getPrintBill() {
/* 2600 */     if (context.get("printBill") != null)
/* 2601 */       return (nc.ui.arap.actions.BillPrintAction)context.get("printBill");
/* 2602 */     nc.ui.arap.actions.BillPrintAction bean = new nc.ui.arap.actions.BillPrintAction();
/* 2603 */     context.put("printBill", bean);
/* 2604 */     bean.setModel(getManageAppModel());
/* 2605 */     bean.setInterceptor(getInterceptor());
/* 2606 */     bean.setTaxForm(getTaxForm());
/* 2607 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2608 */     invokeInitializingBean(bean);
/* 2609 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.OfficialPrintAction getOfficialPrint() {
/* 2613 */     if (context.get("officialPrint") != null)
/* 2614 */       return (nc.ui.arap.actions.OfficialPrintAction)context.get("officialPrint");
/* 2615 */     nc.ui.arap.actions.OfficialPrintAction bean = new nc.ui.arap.actions.OfficialPrintAction();
/* 2616 */     context.put("officialPrint", bean);
/* 2617 */     bean.setModel(getManageAppModel());
/* 2618 */     bean.setInterceptor(getInterceptor());
/* 2619 */     bean.setTaxForm(getTaxForm());
/* 2620 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2621 */     invokeInitializingBean(bean);
/* 2622 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.CancelPrintAction getCancelPrint() {
/* 2626 */     if (context.get("cancelPrint") != null)
/* 2627 */       return (nc.ui.arap.actions.CancelPrintAction)context.get("cancelPrint");
/* 2628 */     nc.ui.arap.actions.CancelPrintAction bean = new nc.ui.arap.actions.CancelPrintAction();
/* 2629 */     context.put("cancelPrint", bean);
/* 2630 */     bean.setModel(getManageAppModel());
/* 2631 */     bean.setEditor(getBillFormEditor());
/* 2632 */     bean.setInterceptor(getInterceptor());
/* 2633 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2634 */     invokeInitializingBean(bean);
/* 2635 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.PrintListAction getPrintList() {
/* 2639 */     if (context.get("printList") != null)
/* 2640 */       return (nc.ui.arap.actions.PrintListAction)context.get("printList");
/* 2641 */     nc.ui.arap.actions.PrintListAction bean = new nc.ui.arap.actions.PrintListAction();
/* 2642 */     context.put("printList", bean);
/* 2643 */     bean.setModel(getManageAppModel());
/* 2644 */     bean.setInterceptor(getInterceptor());
/* 2645 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2646 */     invokeInitializingBean(bean);
/* 2647 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.MadeBillAction getMadeBillAction() {
/* 2651 */     if (context.get("madeBillAction") != null)
/* 2652 */       return (nc.ui.arap.actions.MadeBillAction)context.get("madeBillAction");
/* 2653 */     nc.ui.arap.actions.MadeBillAction bean = new nc.ui.arap.actions.MadeBillAction();
/* 2654 */     context.put("madeBillAction", bean);
/* 2655 */     bean.setModel(getManageAppModel());
/* 2656 */     bean.setEditor(getBillFormEditor());
/* 2657 */     bean.setListView(getListView());
/* 2658 */     bean.setLoginContext(getContext());
/* 2659 */     bean.setInterceptor(getInterceptor());
/* 2660 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2661 */     invokeInitializingBean(bean);
/* 2662 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillPrepayAction getPrepayAction() {
/* 2666 */     if (context.get("prepayAction") != null)
/* 2667 */       return (nc.ui.arap.actions.BillPrepayAction)context.get("prepayAction");
/* 2668 */     nc.ui.arap.actions.BillPrepayAction bean = new nc.ui.arap.actions.BillPrepayAction();
/* 2669 */     context.put("prepayAction", bean);
/* 2670 */     bean.setModel(getManageAppModel());
/* 2671 */     bean.setEditor(getBillFormEditor());
/* 2672 */     bean.setInterceptor(getInterceptor());
/* 2673 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2674 */     invokeInitializingBean(bean);
/* 2675 */     return bean;
/*      */   }
/*      */   
/*      */   public MenuAction getBillOperateActionGroup() {
/* 2679 */     if (context.get("billOperateActionGroup") != null)
/* 2680 */       return (MenuAction)context.get("billOperateActionGroup");
/* 2681 */     MenuAction bean = new MenuAction();
/* 2682 */     context.put("billOperateActionGroup", bean);
/* 2683 */     bean.setCode("billOperate");
/* 2684 */     bean.setName(getI18nFB_a831cc());
/* 2685 */     bean.setActions(getManagedList18());
/* 2686 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2687 */     invokeInitializingBean(bean);
/* 2688 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_a831cc() {
/* 2692 */     if (context.get("nc.ui.uif2.I18nFB#a831cc") != null)
/* 2693 */       return (String)context.get("nc.ui.uif2.I18nFB#a831cc");
/* 2694 */     I18nFB bean = new I18nFB();
/* 2695 */     context.put("&nc.ui.uif2.I18nFB#a831cc", bean);bean.setResDir("common");
/* 2696 */     bean.setResId("arapcommonv6-0137");
/* 2697 */     bean.setDefaultValue("���ݲ���");
/* 2698 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2699 */     invokeInitializingBean(bean);
/*      */     try {
/* 2701 */       Object product = bean.getObject();
/* 2702 */       context.put("nc.ui.uif2.I18nFB#a831cc", product);
/* 2703 */       return (String)product;
/*      */     } catch (Exception e) {
/* 2705 */       throw new RuntimeException(e); } }
/*      */   
/* 2707 */   private List getManagedList18() { List list = new ArrayList();list.add(getDeleteAction());list.add(getEditAction());list.add(getTempSaveAction());list.add(getWriteBack());list.add(getDocumentManage());list.add(getPrepayAction());return list;
/*      */   }
/*      */   
/* 2710 */   public MenuAction getBillOperateActionGroup_Record() { if (context.get("billOperateActionGroup_Record") != null)
/* 2711 */       return (MenuAction)context.get("billOperateActionGroup_Record");
/* 2712 */     MenuAction bean = new MenuAction();
/* 2713 */     context.put("billOperateActionGroup_Record", bean);
/* 2714 */     bean.setCode("billOperate");
/* 2715 */     bean.setName(getI18nFB_121ac53());
/* 2716 */     bean.setActions(getManagedList19());
/* 2717 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2718 */     invokeInitializingBean(bean);
/* 2719 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_121ac53() {
/* 2723 */     if (context.get("nc.ui.uif2.I18nFB#121ac53") != null)
/* 2724 */       return (String)context.get("nc.ui.uif2.I18nFB#121ac53");
/* 2725 */     I18nFB bean = new I18nFB();
/* 2726 */     context.put("&nc.ui.uif2.I18nFB#121ac53", bean);bean.setResDir("common");
/* 2727 */     bean.setResId("arapcommonv6-0137");
/* 2728 */     bean.setDefaultValue("���ݲ���");
/* 2729 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2730 */     invokeInitializingBean(bean);
/*      */     try {
/* 2732 */       Object product = bean.getObject();
/* 2733 */       context.put("nc.ui.uif2.I18nFB#121ac53", product);
/* 2734 */       return (String)product;
/*      */     } catch (Exception e) {
/* 2736 */       throw new RuntimeException(e); } }
/*      */   
/* 2738 */   private List getManagedList19() { List list = new ArrayList();list.add(getDeleteAction());list.add(getEditAction());list.add(getTempSaveAction());list.add(getDocumentManage());return list;
/*      */   }
/*      */   
/* 2741 */   public MenuAction getBillOperateActionGroup_Init() { if (context.get("billOperateActionGroup_Init") != null)
/* 2742 */       return (MenuAction)context.get("billOperateActionGroup_Init");
/* 2743 */     MenuAction bean = new MenuAction();
/* 2744 */     context.put("billOperateActionGroup_Init", bean);
/* 2745 */     bean.setCode("billOperate");
/* 2746 */     bean.setName(getI18nFB_1b8b104());
/* 2747 */     bean.setActions(getManagedList20());
/* 2748 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2749 */     invokeInitializingBean(bean);
/* 2750 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_1b8b104() {
/* 2754 */     if (context.get("nc.ui.uif2.I18nFB#1b8b104") != null)
/* 2755 */       return (String)context.get("nc.ui.uif2.I18nFB#1b8b104");
/* 2756 */     I18nFB bean = new I18nFB();
/* 2757 */     context.put("&nc.ui.uif2.I18nFB#1b8b104", bean);bean.setResDir("common");
/* 2758 */     bean.setResId("arapcommonv6-0137");
/* 2759 */     bean.setDefaultValue("���ݲ���");
/* 2760 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2761 */     invokeInitializingBean(bean);
/*      */     try {
/* 2763 */       Object product = bean.getObject();
/* 2764 */       context.put("nc.ui.uif2.I18nFB#1b8b104", product);
/* 2765 */       return (String)product;
/*      */     } catch (Exception e) {
/* 2767 */       throw new RuntimeException(e); } }
/*      */   
/* 2769 */   private List getManagedList20() { List list = new ArrayList();list.add(getDeleteAction());list.add(getEditAction());list.add(getDocumentManage());return list;
/*      */   }
/*      */   
/* 2772 */   public MenuAction getBillOperateActionGroup_List() { if (context.get("billOperateActionGroup_List") != null)
/* 2773 */       return (MenuAction)context.get("billOperateActionGroup_List");
/* 2774 */     MenuAction bean = new MenuAction();
/* 2775 */     context.put("billOperateActionGroup_List", bean);
/* 2776 */     bean.setCode("billOperate_List");
/* 2777 */     bean.setName(getI18nFB_6d808b());
/* 2778 */     bean.setActions(getManagedList21());
/* 2779 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2780 */     invokeInitializingBean(bean);
/* 2781 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_6d808b() {
/* 2785 */     if (context.get("nc.ui.uif2.I18nFB#6d808b") != null)
/* 2786 */       return (String)context.get("nc.ui.uif2.I18nFB#6d808b");
/* 2787 */     I18nFB bean = new I18nFB();
/* 2788 */     context.put("&nc.ui.uif2.I18nFB#6d808b", bean);bean.setResDir("common");
/* 2789 */     bean.setResId("arapcommonv6-0137");
/* 2790 */     bean.setDefaultValue("���ݲ���");
/* 2791 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2792 */     invokeInitializingBean(bean);
/*      */     try {
/* 2794 */       Object product = bean.getObject();
/* 2795 */       context.put("nc.ui.uif2.I18nFB#6d808b", product);
/* 2796 */       return (String)product;
/*      */     } catch (Exception e) {
/* 2798 */       throw new RuntimeException(e); } }
/*      */   
/* 2800 */   private List getManagedList21() { List list = new ArrayList();list.add(getDeleteAction());list.add(getDocumentManage());return list;
/*      */   }
/*      */   
/* 2803 */   public MenuAction getCloseOperateAction() { if (context.get("closeOperateAction") != null)
/* 2804 */       return (MenuAction)context.get("closeOperateAction");
/* 2805 */     MenuAction bean = new MenuAction();
/* 2806 */     context.put("closeOperateAction", bean);
/* 2807 */     bean.setCode("billClose");
/* 2808 */     bean.setName(getI18nFB_112364e());
/* 2809 */     bean.setActions(getManagedList22());
/* 2810 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2811 */     invokeInitializingBean(bean);
/* 2812 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_112364e() {
/* 2816 */     if (context.get("nc.ui.uif2.I18nFB#112364e") != null)
/* 2817 */       return (String)context.get("nc.ui.uif2.I18nFB#112364e");
/* 2818 */     I18nFB bean = new I18nFB();
/* 2819 */     context.put("&nc.ui.uif2.I18nFB#112364e", bean);bean.setResDir("common");
/* 2820 */     bean.setResId("arapcommonv6-0138");
/* 2821 */     bean.setDefaultValue("�رղ���");
/* 2822 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2823 */     invokeInitializingBean(bean);
/*      */     try {
/* 2825 */       Object product = bean.getObject();
/* 2826 */       context.put("nc.ui.uif2.I18nFB#112364e", product);
/* 2827 */       return (String)product;
/*      */     } catch (Exception e) {
/* 2829 */       throw new RuntimeException(e); } }
/*      */   
/* 2831 */   private List getManagedList22() { List list = new ArrayList();return list;
/*      */   }
/*      */   
/* 2834 */   public nc.funcnode.ui.action.GroupAction getBillApproveActionGroup() { if (context.get("billApproveActionGroup") != null)
/* 2835 */       return (nc.funcnode.ui.action.GroupAction)context.get("billApproveActionGroup");
/* 2836 */     nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
/* 2837 */     context.put("billApproveActionGroup", bean);
/* 2838 */     bean.setCode("billApproveActionGroup");
/* 2839 */     bean.setName(getI18nFB_ab76ed());
/* 2840 */     bean.setActions(getManagedList23());
/* 2841 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2842 */     invokeInitializingBean(bean);
/* 2843 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_ab76ed() {
/* 2847 */     if (context.get("nc.ui.uif2.I18nFB#ab76ed") != null)
/* 2848 */       return (String)context.get("nc.ui.uif2.I18nFB#ab76ed");
/* 2849 */     I18nFB bean = new I18nFB();
/* 2850 */     context.put("&nc.ui.uif2.I18nFB#ab76ed", bean);bean.setResDir("common");
/* 2851 */     bean.setResId("arapcommonv6-0142");
/* 2852 */     bean.setDefaultValue("����");
/* 2853 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2854 */     invokeInitializingBean(bean);
/*      */     try {
/* 2856 */       Object product = bean.getObject();
/* 2857 */       context.put("nc.ui.uif2.I18nFB#ab76ed", product);
/* 2858 */       return (String)product;
/*      */     } catch (Exception e) {
/* 2860 */       throw new RuntimeException(e); } }
/*      */   
/* 2862 */   private List getManagedList23() { List list = new ArrayList();list.add(getApproveAction());list.add(getUnApproveAction());list.add(getFlowStateQueryAction());return list;
/*      */   }
/*      */   
/* 2865 */   public nc.funcnode.ui.action.GroupAction getBillApproveActionGroup_List() { if (context.get("billApproveActionGroup_List") != null)
/* 2866 */       return (nc.funcnode.ui.action.GroupAction)context.get("billApproveActionGroup_List");
/* 2867 */     nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
/* 2868 */     context.put("billApproveActionGroup_List", bean);
/* 2869 */     bean.setCode("billApproveActionGroup_List");
/* 2870 */     bean.setName(getI18nFB_10d0293());
/* 2871 */     bean.setActions(getManagedList24());
/* 2872 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2873 */     invokeInitializingBean(bean);
/* 2874 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_10d0293() {
/* 2878 */     if (context.get("nc.ui.uif2.I18nFB#10d0293") != null)
/* 2879 */       return (String)context.get("nc.ui.uif2.I18nFB#10d0293");
/* 2880 */     I18nFB bean = new I18nFB();
/* 2881 */     context.put("&nc.ui.uif2.I18nFB#10d0293", bean);bean.setResDir("common");
/* 2882 */     bean.setResId("arapcommonv6-0142");
/* 2883 */     bean.setDefaultValue("����");
/* 2884 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2885 */     invokeInitializingBean(bean);
/*      */     try {
/* 2887 */       Object product = bean.getObject();
/* 2888 */       context.put("nc.ui.uif2.I18nFB#10d0293", product);
/* 2889 */       return (String)product;
/*      */     } catch (Exception e) {
/* 2891 */       throw new RuntimeException(e); } }
/*      */   
/* 2893 */   private List getManagedList24() { List list = new ArrayList();list.add(getApproveListAction());list.add(getUnApproveListAction());list.add(getApproveListBatchAction());list.add(getUnApproveListBatchAction());list.add(getFlowStateQueryAction());return list;
/*      */   }
/*      */   
/* 2896 */   public nc.funcnode.ui.action.GroupAction getBillVerifyActionGroup() { if (context.get("billVerifyActionGroup") != null)
/* 2897 */       return (nc.funcnode.ui.action.GroupAction)context.get("billVerifyActionGroup");
/* 2898 */     nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
/* 2899 */     context.put("billVerifyActionGroup", bean);
/* 2900 */     bean.setCode("billVerify");
/* 2901 */     bean.setName(getI18nFB_12416f9());
/* 2902 */     bean.setActions(getManagedList25());
/* 2903 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2904 */     invokeInitializingBean(bean);
/* 2905 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_12416f9() {
/* 2909 */     if (context.get("nc.ui.uif2.I18nFB#12416f9") != null)
/* 2910 */       return (String)context.get("nc.ui.uif2.I18nFB#12416f9");
/* 2911 */     I18nFB bean = new I18nFB();
/* 2912 */     context.put("&nc.ui.uif2.I18nFB#12416f9", bean);bean.setResDir("common");
/* 2913 */     bean.setResId("arapcommonv6-0143");
/* 2914 */     bean.setDefaultValue("��ʱ����");
/* 2915 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2916 */     invokeInitializingBean(bean);
/*      */     try {
/* 2918 */       Object product = bean.getObject();
/* 2919 */       context.put("nc.ui.uif2.I18nFB#12416f9", product);
/* 2920 */       return (String)product;
/*      */     } catch (Exception e) {
/* 2922 */       throw new RuntimeException(e); } }
/*      */   
/* 2924 */   private List getManagedList25() { List list = new ArrayList();list.add(getOnBodyVerify());list.add(getOnWholeBillVerifyAction());return list;
/*      */   }
/*      */   
/* 2927 */   public nc.funcnode.ui.action.GroupAction getPauseTransactActionGroup() { if (context.get("pauseTransactActionGroup") != null)
/* 2928 */       return (nc.funcnode.ui.action.GroupAction)context.get("pauseTransactActionGroup");
/* 2929 */     nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
/* 2930 */     context.put("pauseTransactActionGroup", bean);
/* 2931 */     bean.setCode("pauseTransact");
/* 2932 */     bean.setName(getI18nFB_606848());
/* 2933 */     bean.setActions(getManagedList26());
/* 2934 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2935 */     invokeInitializingBean(bean);
/* 2936 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_606848() {
/* 2940 */     if (context.get("nc.ui.uif2.I18nFB#606848") != null)
/* 2941 */       return (String)context.get("nc.ui.uif2.I18nFB#606848");
/* 2942 */     I18nFB bean = new I18nFB();
/* 2943 */     context.put("&nc.ui.uif2.I18nFB#606848", bean);bean.setResDir("2006pub_0");
/* 2944 */     bean.setResId("02006pub-0644");
/* 2945 */     bean.setDefaultValue("�������");
/* 2946 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2947 */     invokeInitializingBean(bean);
/*      */     try {
/* 2949 */       Object product = bean.getObject();
/* 2950 */       context.put("nc.ui.uif2.I18nFB#606848", product);
/* 2951 */       return (String)product;
/*      */     } catch (Exception e) {
/* 2953 */       throw new RuntimeException(e); } }
/*      */   
/* 2955 */   private List getManagedList26() { List list = new ArrayList();list.add(getPauseTransact());list.add(getCancelPauseTransact());return list;
/*      */   }
/*      */   
/* 2958 */   public nc.funcnode.ui.action.GroupAction getPrintOperateActionGroup() { if (context.get("printOperateActionGroup") != null)
/* 2959 */       return (nc.funcnode.ui.action.GroupAction)context.get("printOperateActionGroup");
/* 2960 */     nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
/* 2961 */     context.put("printOperateActionGroup", bean);
/* 2962 */     bean.setCode("printOperateActionGroup");
/* 2963 */     bean.setName(getI18nFB_15e7b45());
/* 2964 */     bean.setActions(getManagedList27());
/* 2965 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2966 */     invokeInitializingBean(bean);
/* 2967 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_15e7b45() {
/* 2971 */     if (context.get("nc.ui.uif2.I18nFB#15e7b45") != null)
/* 2972 */       return (String)context.get("nc.ui.uif2.I18nFB#15e7b45");
/* 2973 */     I18nFB bean = new I18nFB();
/* 2974 */     context.put("&nc.ui.uif2.I18nFB#15e7b45", bean);bean.setResDir("common");
/* 2975 */     bean.setResId("arapcommonv6-0139");
/* 2976 */     bean.setDefaultValue("��ӡ");
/* 2977 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2978 */     invokeInitializingBean(bean);
/*      */     try {
/* 2980 */       Object product = bean.getObject();
/* 2981 */       context.put("nc.ui.uif2.I18nFB#15e7b45", product);
/* 2982 */       return (String)product;
/*      */     } catch (Exception e) {
/* 2984 */       throw new RuntimeException(e); } }
/*      */   
/* 2986 */   private List getManagedList27() { List list = new ArrayList();list.add(getPrintBill());list.add(getPrintPreview());list.add(getPrintOutput());list.add(getSeparatorAction());list.add(getOfficialPrint());list.add(getCancelPrint());return list;
/*      */   }
/*      */   
/* 2989 */   public nc.funcnode.ui.action.GroupAction getPrintOperateActionGroup_List() { if (context.get("printOperateActionGroup_List") != null)
/* 2990 */       return (nc.funcnode.ui.action.GroupAction)context.get("printOperateActionGroup_List");
/* 2991 */     nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
/* 2992 */     context.put("printOperateActionGroup_List", bean);
/* 2993 */     bean.setCode("printOperateActionGroup_List");
/* 2994 */     bean.setName(getI18nFB_1ef19c5());
/* 2995 */     bean.setActions(getManagedList28());
/* 2996 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 2997 */     invokeInitializingBean(bean);
/* 2998 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_1ef19c5() {
/* 3002 */     if (context.get("nc.ui.uif2.I18nFB#1ef19c5") != null)
/* 3003 */       return (String)context.get("nc.ui.uif2.I18nFB#1ef19c5");
/* 3004 */     I18nFB bean = new I18nFB();
/* 3005 */     context.put("&nc.ui.uif2.I18nFB#1ef19c5", bean);bean.setResDir("common");
/* 3006 */     bean.setResId("arapcommonv6-0139");
/* 3007 */     bean.setDefaultValue("��ӡ");
/* 3008 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3009 */     invokeInitializingBean(bean);
/*      */     try {
/* 3011 */       Object product = bean.getObject();
/* 3012 */       context.put("nc.ui.uif2.I18nFB#1ef19c5", product);
/* 3013 */       return (String)product;
/*      */     } catch (Exception e) {
/* 3015 */       throw new RuntimeException(e); } }
/*      */   
/* 3017 */   private List getManagedList28() { List list = new ArrayList();list.add(getPrintBill());list.add(getPrintPreview());list.add(getPrintOutput());list.add(getSeparatorAction());list.add(getOfficialPrint());list.add(getCancelPrint());list.add(getSeparatorAction());list.add(getPrintList());return list;
/*      */   }
/*      */   
/* 3020 */   public MenuAction getRelatedQueryActionGroup() { if (context.get("relatedQueryActionGroup") != null)
/* 3021 */       return (MenuAction)context.get("relatedQueryActionGroup");
/* 3022 */     MenuAction bean = new MenuAction();
/* 3023 */     context.put("relatedQueryActionGroup", bean);
/* 3024 */     bean.setCode("relatedQuery");
/* 3025 */     bean.setName(getI18nFB_316ec4());
/* 3026 */     bean.setActions(getManagedList29());
/* 3027 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3028 */     invokeInitializingBean(bean);
/* 3029 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_316ec4() {
/* 3033 */     if (context.get("nc.ui.uif2.I18nFB#316ec4") != null)
/* 3034 */       return (String)context.get("nc.ui.uif2.I18nFB#316ec4");
/* 3035 */     I18nFB bean = new I18nFB();
/* 3036 */     context.put("&nc.ui.uif2.I18nFB#316ec4", bean);bean.setResDir("pubapp_0");
/* 3037 */     bean.setResId("0pubapp-0025");
/* 3038 */     bean.setDefaultValue("����");
/* 3039 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3040 */     invokeInitializingBean(bean);
/*      */     try {
/* 3042 */       Object product = bean.getObject();
/* 3043 */       context.put("nc.ui.uif2.I18nFB#316ec4", product);
/* 3044 */       return (String)product;
/*      */     } catch (Exception e) {
/* 3046 */       throw new RuntimeException(e); } }
/*      */   
/* 3048 */   private List getManagedList29() { List list = new ArrayList();list.add(getLinkQueryAction());list.add(getLinkSettleInfoAction());list.add(getLinkInformerAction());list.add(getFlowStateQueryAction());list.add(getBalanceLinkQueryAction());list.add(getDealLinkQueryAction());list.add(getLinkVoucharQueryAction());list.add(getBconferQueryAction());list.add(getTbbLinkAction());return list;
/*      */   }
/*      */   
/* 3051 */   public nc.ui.uif2.actions.FirstLineAction getFirstLineAction() { if (context.get("firstLineAction") != null)
/* 3052 */       return (nc.ui.uif2.actions.FirstLineAction)context.get("firstLineAction");
/* 3053 */     nc.ui.uif2.actions.FirstLineAction bean = new nc.ui.uif2.actions.FirstLineAction();
/* 3054 */     context.put("firstLineAction", bean);
/* 3055 */     bean.setModel(getManageAppModel());
/* 3056 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3057 */     invokeInitializingBean(bean);
/* 3058 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.uif2.actions.NextLineAction getNextLineAction() {
/* 3062 */     if (context.get("nextLineAction") != null)
/* 3063 */       return (nc.ui.uif2.actions.NextLineAction)context.get("nextLineAction");
/* 3064 */     nc.ui.uif2.actions.NextLineAction bean = new nc.ui.uif2.actions.NextLineAction();
/* 3065 */     context.put("nextLineAction", bean);
/* 3066 */     bean.setModel(getManageAppModel());
/* 3067 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3068 */     invokeInitializingBean(bean);
/* 3069 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.uif2.actions.PreLineAction getPreLineAction() {
/* 3073 */     if (context.get("preLineAction") != null)
/* 3074 */       return (nc.ui.uif2.actions.PreLineAction)context.get("preLineAction");
/* 3075 */     nc.ui.uif2.actions.PreLineAction bean = new nc.ui.uif2.actions.PreLineAction();
/* 3076 */     context.put("preLineAction", bean);
/* 3077 */     bean.setModel(getManageAppModel());
/* 3078 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3079 */     invokeInitializingBean(bean);
/* 3080 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.uif2.actions.LastLineAction getLastLineAction() {
/* 3084 */     if (context.get("lastLineAction") != null)
/* 3085 */       return (nc.ui.uif2.actions.LastLineAction)context.get("lastLineAction");
/* 3086 */     nc.ui.uif2.actions.LastLineAction bean = new nc.ui.uif2.actions.LastLineAction();
/* 3087 */     context.put("lastLineAction", bean);
/* 3088 */     bean.setModel(getManageAppModel());
/* 3089 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3090 */     invokeInitializingBean(bean);
/* 3091 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.ref.FiBillTypeRefModel getTransTypeRefModel() {
/* 3095 */     if (context.get("transTypeRefModel") != null)
/* 3096 */       return (nc.ui.arap.ref.FiBillTypeRefModel)context.get("transTypeRefModel");
/* 3097 */     nc.ui.arap.ref.FiBillTypeRefModel bean = new nc.ui.arap.ref.FiBillTypeRefModel();
/* 3098 */     context.put("transTypeRefModel", bean);
/* 3099 */     bean.setNodeKeyQry(getNodeKeyQry());
/* 3100 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3101 */     invokeInitializingBean(bean);
/* 3102 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.ReceiptCheckAction getReceiptCheckAction() {
/* 3106 */     if (context.get("receiptCheckAction") != null)
/* 3107 */       return (nc.ui.arap.actions.ReceiptCheckAction)context.get("receiptCheckAction");
/* 3108 */     nc.ui.arap.actions.ReceiptCheckAction bean = new nc.ui.arap.actions.ReceiptCheckAction();
/* 3109 */     context.put("receiptCheckAction", bean);
/* 3110 */     bean.setModel(getManageAppModel());
/* 3111 */     bean.setPk_billtype("F2");
/* 3112 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3113 */     invokeInitializingBean(bean);
/* 3114 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.ReceiptScanAction getReceiptScanAction() {
/* 3118 */     if (context.get("receiptScanAction") != null)
/* 3119 */       return (nc.ui.arap.actions.ReceiptScanAction)context.get("receiptScanAction");
/* 3120 */     nc.ui.arap.actions.ReceiptScanAction bean = new nc.ui.arap.actions.ReceiptScanAction();
/* 3121 */     context.put("receiptScanAction", bean);
/* 3122 */     bean.setModel(getManageAppModel());
/* 3123 */     bean.setPk_billtype("F2");
/* 3124 */     bean.setCheckscanway("swing");
/* 3125 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3126 */     invokeInitializingBean(bean);
/* 3127 */     return bean;
/*      */   }
/*      */   
/*      */   public MenuAction getReceiptMenuAction() {
/* 3131 */     if (context.get("receiptMenuAction") != null)
/* 3132 */       return (MenuAction)context.get("receiptMenuAction");
/* 3133 */     MenuAction bean = new MenuAction();
/* 3134 */     context.put("receiptMenuAction", bean);
/* 3135 */     bean.setCode("Image");
/* 3136 */     bean.setName(getI18nFB_13a5799());
/* 3137 */     bean.setActions(getManagedList30());
/* 3138 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3139 */     invokeInitializingBean(bean);
/* 3140 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_13a5799() {
/* 3144 */     if (context.get("nc.ui.uif2.I18nFB#13a5799") != null)
/* 3145 */       return (String)context.get("nc.ui.uif2.I18nFB#13a5799");
/* 3146 */     I18nFB bean = new I18nFB();
/* 3147 */     context.put("&nc.ui.uif2.I18nFB#13a5799", bean);bean.setResDir("common");
/* 3148 */     bean.setResId("arapcommonv6-0180");
/* 3149 */     bean.setDefaultValue("Ӱ��");
/* 3150 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3151 */     invokeInitializingBean(bean);
/*      */     try {
/* 3153 */       Object product = bean.getObject();
/* 3154 */       context.put("nc.ui.uif2.I18nFB#13a5799", product);
/* 3155 */       return (String)product;
/*      */     } catch (Exception e) {
/* 3157 */       throw new RuntimeException(e); } }
/*      */   
/* 3159 */   private List getManagedList30() { List list = new ArrayList();list.add(getReceiptCheckAction());list.add(getReceiptScanAction());return list;
/*      */   }
/*      */   
/* 3162 */   public String getDefMoneyField() { if (context.get("defMoneyField") != null)
/* 3163 */       return (String)context.get("defMoneyField");
/* 3164 */     String bean = new String("money_cr");context.put("defMoneyField", bean);
/* 3165 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3166 */     invokeInitializingBean(bean);
/* 3167 */     return bean;
/*      */   }
/*      */   
/*      */   public String getDefBillType() {
/* 3171 */     if (context.get("defBillType") != null)
/* 3172 */       return (String)context.get("defBillType");
/* 3173 */     String bean = new String("F2");context.put("defBillType", bean);
/* 3174 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3175 */     invokeInitializingBean(bean);
/* 3176 */     return bean;
/*      */   }
/*      */   
/*      */   public String getDefNodeKey() {
/* 3180 */     if (context.get("defNodeKey") != null)
/* 3181 */       return (String)context.get("defNodeKey");
/* 3182 */     String bean = new String("D2");context.put("defNodeKey", bean);
/* 3183 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3184 */     invokeInitializingBean(bean);
/* 3185 */     return bean;
/*      */   }
/*      */   
/*      */   public String getMdFullnameHead() {
/* 3189 */     if (context.get("mdFullnameHead") != null)
/* 3190 */       return (String)context.get("mdFullnameHead");
/* 3191 */     String bean = new String("arap.gatherbill");context.put("mdFullnameHead", bean);
/* 3192 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3193 */     invokeInitializingBean(bean);
/* 3194 */     return bean;
/*      */   }
/*      */   
/*      */   public String getMdFullnameBody() {
/* 3198 */     if (context.get("mdFullnameBody") != null)
/* 3199 */       return (String)context.get("mdFullnameBody");
/* 3200 */     String bean = new String("arap.gatheritem");context.put("mdFullnameBody", bean);
/* 3201 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3202 */     invokeInitializingBean(bean);
/* 3203 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.GatherLineDefValUtil getIArapLineDefValUtil() {
/* 3207 */     if (context.get("iArapLineDefValUtil") != null)
/* 3208 */       return (nc.ui.arap.actions.GatherLineDefValUtil)context.get("iArapLineDefValUtil");
/* 3209 */     nc.ui.arap.actions.GatherLineDefValUtil bean = new nc.ui.arap.actions.GatherLineDefValUtil();
/* 3210 */     context.put("iArapLineDefValUtil", bean);
/* 3211 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3212 */     invokeInitializingBean(bean);
/* 3213 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.model.GatheringBillAppModelService getManageModelService() {
/* 3217 */     if (context.get("ManageModelService") != null)
/* 3218 */       return (nc.ui.arap.model.GatheringBillAppModelService)context.get("ManageModelService");
/* 3219 */     nc.ui.arap.model.GatheringBillAppModelService bean = new nc.ui.arap.model.GatheringBillAppModelService();
/* 3220 */     context.put("ManageModelService", bean);
/* 3221 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3222 */     invokeInitializingBean(bean);
/* 3223 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.view.GatheringBillDefValue getBillDefVauleItf() {
/* 3227 */     if (context.get("billDefVauleItf") != null)
/* 3228 */       return (nc.ui.arap.view.GatheringBillDefValue)context.get("billDefVauleItf");
/* 3229 */     nc.ui.arap.view.GatheringBillDefValue bean = new nc.ui.arap.view.GatheringBillDefValue();
/* 3230 */     context.put("billDefVauleItf", bean);
/* 3231 */     bean.setEditor(getBillFormEditor());
/* 3232 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3233 */     invokeInitializingBean(bean);
/* 3234 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.model.GatheringBillPaginationQueryService getPaginationQueryService() {
/* 3238 */     if (context.get("paginationQueryService") != null)
/* 3239 */       return (nc.ui.arap.model.GatheringBillPaginationQueryService)context.get("paginationQueryService");
/* 3240 */     nc.ui.arap.model.GatheringBillPaginationQueryService bean = new nc.ui.arap.model.GatheringBillPaginationQueryService();
/* 3241 */     context.put("paginationQueryService", bean);
/* 3242 */     bean.setPageSize(Integer.valueOf(10));
/* 3243 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3244 */     invokeInitializingBean(bean);
/* 3245 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.DefRelationAfterEditHandler getDefRelationAfterEditHandler() {
/* 3249 */     if (context.get("DefRelationAfterEditHandler") != null)
/* 3250 */       return (nc.ui.arap.viewhandler.DefRelationAfterEditHandler)context.get("DefRelationAfterEditHandler");
/* 3251 */     nc.ui.arap.viewhandler.DefRelationAfterEditHandler bean = new nc.ui.arap.viewhandler.DefRelationAfterEditHandler();
/* 3252 */     context.put("DefRelationAfterEditHandler", bean);
/* 3253 */     bean.setBillform(getBillFormEditor());
/* 3254 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3255 */     invokeInitializingBean(bean);
/* 3256 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.viewhandler.cardafter.BodyDefRelationAfterEditHandler getBodyDefRelationAfterEditHandler() {
/* 3260 */     if (context.get("BodyDefRelationAfterEditHandler") != null)
/* 3261 */       return (nc.ui.arap.viewhandler.cardafter.BodyDefRelationAfterEditHandler)context.get("BodyDefRelationAfterEditHandler");
/* 3262 */     nc.ui.arap.viewhandler.cardafter.BodyDefRelationAfterEditHandler bean = new nc.ui.arap.viewhandler.cardafter.BodyDefRelationAfterEditHandler();
/* 3263 */     context.put("BodyDefRelationAfterEditHandler", bean);
/* 3264 */     bean.setBillform(getBillFormEditor());
/* 3265 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3266 */     invokeInitializingBean(bean);
/* 3267 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.pubapp.uif2app.model.AppEventHandlerMediator getAppEventHandlerMediator() {
/* 3271 */     if (context.get("AppEventHandlerMediator") != null)
/* 3272 */       return (nc.ui.pubapp.uif2app.model.AppEventHandlerMediator)context.get("AppEventHandlerMediator");
/* 3273 */     nc.ui.pubapp.uif2app.model.AppEventHandlerMediator bean = new nc.ui.pubapp.uif2app.model.AppEventHandlerMediator();
/* 3274 */     context.put("AppEventHandlerMediator", bean);
/* 3275 */     bean.setModel(getManageAppModel());
/* 3276 */     bean.setHandlerMap(getManagedMap0());
/* 3277 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3278 */     invokeInitializingBean(bean);
/* 3279 */     return bean;
/*      */   }
/*      */   
/* 3282 */   private Map getManagedMap0() { Map map = new java.util.HashMap();map.put("nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent", getManagedList31());map.put("nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent", getManagedList32());map.put("nc.ui.arap.viewhandler.CardBodyAfterEditEvent", getManagedList33());map.put("nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent", getManagedList34());map.put("nc.ui.pubapp.uif2app.event.card.CardHeadTailBeforeEditEvent", getManagedList35());map.put("nc.ui.pubapp.uif2app.event.card.CardBodyAfterRowEditEvent", getManagedList36());map.put("nc.ui.pubapp.uif2app.event.card.CardBodyRowChangedEvent", getManagedList37());map.put("nc.ui.pubapp.uif2app.event.card.CardPanelLoadEvent", getManagedList38());map.put("nc.ui.pubapp.uif2app.event.list.ListPanelLoadEvent", getManagedList39());map.put("nc.ui.pubapp.uif2app.event.list.ListHeadRowChangedEvent", getManagedList40());map.put("nc.ui.pubapp.uif2app.event.list.ListHeadDataChangedEvent", getManagedList41());map.put("nc.ui.pubapp.uif2app.mediator.mutiltrans.NodekeyEvent", getManagedList42());return map; }
/*      */   
/* 3284 */   private List getManagedList31() { List list = new ArrayList();list.add(getBodyBankAccBeforeEditHandler());list.add(getBodyAccountRefBeforeEditHandler());list.add(getBodyFreeCustBeforeEditHandler());list.add(getBodyProjectBeforeEditHandler());list.add(getPayTermBodyBeforeEditHandler());list.add(getBodyChecNoRefBeforeEditHandler());list.add(getBodySummaryBeforeEditHandler());list.add(getBodyVersionRefBeforeEditHandler());list.add(getOtherOrgBodyBeforeEditHandler());list.add(getBodySoBilltypeBeforeEditHandler());list.add(getBodyCrossCheckBeforeHandler());list.add(getBodyCostCenterRefBeforeEditHandler());list.add(getBodyObjTypeBeforeEditHandler());list.add(getBodyEuroBeforeEditHandler());list.add(getBodyTaxcodeBeforeEditHandler());list.add(getBodyPsnDocBeforeEditHandler());return list; }
/*      */   
/* 3286 */   private List getManagedList32() { List list = new ArrayList();list.add(getBodyMaterialAfterEditHandler());list.add(getCardAfterEditCalculateHandler());list.add(getBodyPsnDocAfterEditHandler());list.add(getBodyCheckNoAfterEditHandler());list.add(getBodyLocalMoneyAfterEditHandler());list.add(getBodyRateAfterEditHandler());list.add(getOtherOrgBodyAfterEditHandler());list.add(getBodyCurrTypeAfterEditHandler());list.add(getBodyMoneyBalanceAfterEditHandler());list.add(getEnableCtrlBodyAfterEditHandler());list.add(getBodyFundplanAfterEditHandler());list.add(getBodyCuspAfterEditHandler());list.add(getBodyBankAccAfterEditHandler());list.add(getBodyDeptAfterEditHandler());list.add(getBodyEuroAfterEditHandler());list.add(getBodyCostCenterAfterEditHandler());list.add(getBodyAutoAddLineBeforeEditHandler());list.add(getBodyDefRelationAfterEditHandler());return list; }
/*      */   
/* 3288 */   private List getManagedList33() { List list = new ArrayList();list.add(getBodyEuroAfterEditHandler());list.add(getBodyCuspAfterEditHandler());return list; }
/*      */   
/* 3290 */   private List getManagedList34() { List list = new ArrayList();list.add(getHBRelationAfterEditHandler());list.add(getHeadCurrTypeAfterEditHandler());list.add(getHeadBankAccAfterEditHandler());list.add(getHeadPsnDocAfterEditHandler());list.add(getOtherOrgHeadAfterEdithandler());list.add(getHeadFundplanAfterEditHandler());list.add(getObjTypeHeadAfterEdithandler());list.add(getHeadCuspAfterEditHandler());list.add(getHeadDeptAfterEditHandler());list.add(getHeadVersionRefAfterEditHandler());list.add(getHeadEuroAfterEditHandler());list.add(getHeadCostCenterAfterEditHandler());list.add(getHeadBillDateAfterEditHandler());list.add(getDefRelationAfterEditHandler());return list; }
/*      */   
/* 3292 */   private List getManagedList35() { List list = new ArrayList();list.add(getHeadBankAccBeforeEditHandler());list.add(getHeadSummaryBeforeEditHandler());list.add(getHeadVersionRefBeforeEditHandler());list.add(getOtherOrgHeadBeforeEditHandler());list.add(getHeadCrossCheckBeforeHandler());list.add(getBodyVersionRefAfterEditHandler());list.add(getHeadCostCenterRefBeforeEditHandler());list.add(getHeadObjTypeBeforeEditHandler());list.add(getHeadEuroBeforeEditHandler());list.add(getHeadAccountRefBeforeEditHandler());list.add(getHeadPsnDocBeforeEditHandler());return list; }
/*      */   
/* 3294 */   private List getManagedList36() { List list = new ArrayList();list.add(getCardBodyAfterRowEditHandler());return list; }
/*      */   
/* 3296 */   private List getManagedList37() { List list = new ArrayList();list.add(getCardBodyRowChangeHandler());list.add(getSideFormMediator());return list; }
/*      */   
/* 3298 */   private List getManagedList38() { List list = new ArrayList();list.add(getLoadBillCardTemplate());return list; }
/*      */   
/* 3300 */   private List getManagedList39() { List list = new ArrayList();list.add(getLoadBillListTemplate());return list; }
/*      */   
/* 3302 */   private List getManagedList40() { List list = new ArrayList();list.add(getListHeadRowChangeListener());list.add(getSideFormMediator());return list; }
/*      */   
/* 3304 */   private List getManagedList41() { List list = new ArrayList();list.add(getListHeadModelListener());return list; }
/*      */   
/* 3306 */   private List getManagedList42() { List list = new ArrayList();list.add(getAddActionGroup());list.add(getTranstype());return list;
/*      */   }
/*      */   
/* 3309 */   public nc.ui.arap.actions.BillSaveAction getSaveActionReal() { if (context.get("saveActionReal") != null)
/* 3310 */       return (nc.ui.arap.actions.BillSaveAction)context.get("saveActionReal");
/* 3311 */     nc.ui.arap.actions.BillSaveAction bean = new nc.ui.arap.actions.BillSaveAction();
/* 3312 */     context.put("saveActionReal", bean);
/* 3313 */     bean.setModel(getManageAppModel());
/* 3314 */     bean.setEditor(getBillFormEditor());
/* 3315 */     bean.setInterceptor(getInterceptor());
/* 3316 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3317 */     invokeInitializingBean(bean);
/* 3318 */     return bean;
/*      */   }
		    // libin
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
/*      */   public nc.ui.arap.actions.BillApproveAction getApproveActionReal() {
/* 3322 */     if (context.get("ApproveActionReal") != null)
/* 3323 */       return (nc.ui.arap.actions.BillApproveAction)context.get("ApproveActionReal");
/* 3324 */     nc.ui.arap.actions.BillApproveAction bean = new nc.ui.arap.actions.BillApproveAction();
/* 3325 */     context.put("ApproveActionReal", bean);
/* 3326 */     bean.setModel(getManageAppModel());
/* 3327 */     bean.setEditor(getBillFormEditor());
/* 3328 */     bean.setInterceptor(getInterceptor());
/* 3329 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3330 */     invokeInitializingBean(bean);
/* 3331 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillListApproveAction getApproveListActionReal() {
/* 3335 */     if (context.get("ApproveListActionReal") != null)
/* 3336 */       return (nc.ui.arap.actions.BillListApproveAction)context.get("ApproveListActionReal");
/* 3337 */     nc.ui.arap.actions.BillListApproveAction bean = new nc.ui.arap.actions.BillListApproveAction();
/* 3338 */     context.put("ApproveListActionReal", bean);
/* 3339 */     bean.setModel(getManageAppModel());
/* 3340 */     bean.setEditor(getListView());
/* 3341 */     bean.setIsbatch("N");
/* 3342 */     bean.setInterceptor(getInterceptor());
/* 3343 */     bean.setTpaProgressUtil(getTPAProgressUtil_14d30b4());
/* 3344 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3345 */     invokeInitializingBean(bean);
/* 3346 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.uif2.components.progress.TPAProgressUtil getTPAProgressUtil_14d30b4() {
/* 3350 */     if (context.get("nc.ui.uif2.components.progress.TPAProgressUtil#14d30b4") != null)
/* 3351 */       return (nc.ui.uif2.components.progress.TPAProgressUtil)context.get("nc.ui.uif2.components.progress.TPAProgressUtil#14d30b4");
/* 3352 */     nc.ui.uif2.components.progress.TPAProgressUtil bean = new nc.ui.uif2.components.progress.TPAProgressUtil();
/* 3353 */     context.put("nc.ui.uif2.components.progress.TPAProgressUtil#14d30b4", bean);
/* 3354 */     bean.setContext(getContext());
/* 3355 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3356 */     invokeInitializingBean(bean);
/* 3357 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillListApproveAction getApproveListBatchActionReal() {
/* 3361 */     if (context.get("ApproveListBatchActionReal") != null)
/* 3362 */       return (nc.ui.arap.actions.BillListApproveAction)context.get("ApproveListBatchActionReal");
/* 3363 */     nc.ui.arap.actions.BillListApproveAction bean = new nc.ui.arap.actions.BillListApproveAction();
/* 3364 */     context.put("ApproveListBatchActionReal", bean);
/* 3365 */     bean.setModel(getManageAppModel());
/* 3366 */     bean.setEditor(getListView());
/* 3367 */     bean.setIsbatch("Y");
/* 3368 */     bean.setInterceptor(getInterceptor());
/* 3369 */     bean.setTpaProgressUtil(getTPAProgressUtil_b87f37());
/* 3370 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3371 */     invokeInitializingBean(bean);
/* 3372 */     return bean;
/*      */   }
/*      */   
/*      */   private nc.ui.uif2.components.progress.TPAProgressUtil getTPAProgressUtil_b87f37() {
/* 3376 */     if (context.get("nc.ui.uif2.components.progress.TPAProgressUtil#b87f37") != null)
/* 3377 */       return (nc.ui.uif2.components.progress.TPAProgressUtil)context.get("nc.ui.uif2.components.progress.TPAProgressUtil#b87f37");
/* 3378 */     nc.ui.uif2.components.progress.TPAProgressUtil bean = new nc.ui.uif2.components.progress.TPAProgressUtil();
/* 3379 */     context.put("nc.ui.uif2.components.progress.TPAProgressUtil#b87f37", bean);
/* 3380 */     bean.setContext(getContext());
/* 3381 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3382 */     invokeInitializingBean(bean);
/* 3383 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.BillDeleteAction getDeleteActionReal() {
/* 3387 */     if (context.get("deleteActionReal") != null)
/* 3388 */       return (nc.ui.arap.actions.BillDeleteAction)context.get("deleteActionReal");
/* 3389 */     nc.ui.arap.actions.BillDeleteAction bean = new nc.ui.arap.actions.BillDeleteAction();
/* 3390 */     context.put("deleteActionReal", bean);
/* 3391 */     bean.setEditor(getBillFormEditor());
/* 3392 */     bean.setModel(getManageAppModel());
/* 3393 */     bean.setListView(getListView());
/* 3394 */     bean.setInterceptor(getInterceptor());
/* 3395 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3396 */     invokeInitializingBean(bean);
/* 3397 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.vo.arappub.calculator.data.RelationItemForCal_Credit getRelationItemForCal() {
/* 3401 */     if (context.get("relationItemForCal") != null)
/* 3402 */       return (nc.vo.arappub.calculator.data.RelationItemForCal_Credit)context.get("relationItemForCal");
/* 3403 */     nc.vo.arappub.calculator.data.RelationItemForCal_Credit bean = new nc.vo.arappub.calculator.data.RelationItemForCal_Credit();
/* 3404 */     context.put("relationItemForCal", bean);
/* 3405 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3406 */     invokeInitializingBean(bean);
/* 3407 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.LinkSettleInfoAction getLinkSettleInfoAction() {
/* 3411 */     if (context.get("linkSettleInfoAction") != null)
/* 3412 */       return (nc.ui.arap.actions.LinkSettleInfoAction)context.get("linkSettleInfoAction");
/* 3413 */     nc.ui.arap.actions.LinkSettleInfoAction bean = new nc.ui.arap.actions.LinkSettleInfoAction();
/* 3414 */     context.put("linkSettleInfoAction", bean);
/* 3415 */     bean.setModel(getManageAppModel());
/* 3416 */     bean.setInterceptor(getInterceptor());
/* 3417 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3418 */     invokeInitializingBean(bean);
/* 3419 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.LinkInformerAction getLinkInformerAction() {
/* 3423 */     if (context.get("linkInformerAction") != null)
/* 3424 */       return (nc.ui.arap.actions.LinkInformerAction)context.get("linkInformerAction");
/* 3425 */     nc.ui.arap.actions.LinkInformerAction bean = new nc.ui.arap.actions.LinkInformerAction();
/* 3426 */     context.put("linkInformerAction", bean);
/* 3427 */     bean.setModel(getManageAppModel());
/* 3428 */     bean.setInterceptor(getInterceptor());
/* 3429 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3430 */     invokeInitializingBean(bean);
/* 3431 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.TransTypeAction getTranstype() {
/* 3435 */     if (context.get("transtype") != null)
/* 3436 */       return (nc.ui.arap.actions.TransTypeAction)context.get("transtype");
/* 3437 */     nc.ui.arap.actions.TransTypeAction bean = new nc.ui.arap.actions.TransTypeAction();
/* 3438 */     context.put("transtype", bean);
/* 3439 */     bean.setEditor(getBillFormEditor());
/* 3440 */     bean.setModel(getManageAppModel());
/* 3441 */     bean.setTransTypeRefModel(getTransTypeRefModel());
/* 3442 */     bean.setWherepart("  bd_billtype.parentbilltype ='F2' and pk_group != 'global00000000000000'");
/* 3443 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3444 */     invokeInitializingBean(bean);
/* 3445 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.listener.GatherBillRelationQueryDataListener getInitDataListener() {
/* 3449 */     if (context.get("InitDataListener") != null)
/* 3450 */       return (nc.ui.arap.listener.GatherBillRelationQueryDataListener)context.get("InitDataListener");
/* 3451 */     nc.ui.arap.listener.GatherBillRelationQueryDataListener bean = new nc.ui.arap.listener.GatherBillRelationQueryDataListener();
/* 3452 */     context.put("InitDataListener", bean);
/* 3453 */     bean.setBillFormEditor(getBillFormEditor());
/* 3454 */     bean.setListview(getListView());
/* 3455 */     bean.setContext(getContext());
/* 3456 */     bean.setModel(getManageAppModel());
/* 3457 */     bean.setVoClassName("nc.vo.arap.gathering.AggGatheringBillVO");
/* 3458 */     bean.setAutoShowUpComponent(getBillFormEditor());
/* 3459 */     bean.setQueryAction(getQueryAction());
/* 3460 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3461 */     invokeInitializingBean(bean);
/* 3462 */     return bean;
/*      */   }
/*      */   
/*      */   public MenuAction getBillAssistantActionGroup() {
/* 3466 */     if (context.get("billAssistantActionGroup") != null)
/* 3467 */       return (MenuAction)context.get("billAssistantActionGroup");
/* 3468 */     MenuAction bean = new MenuAction();
/* 3469 */     context.put("billAssistantActionGroup", bean);
/* 3470 */     bean.setCode("billAssistant");
/* 3471 */     bean.setName(getI18nFB_5d54b9());
/* 3472 */     bean.setActions(getManagedList43());
/* 3473 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3474 */     invokeInitializingBean(bean);
/* 3475 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_5d54b9() {
/* 3479 */     if (context.get("nc.ui.uif2.I18nFB#5d54b9") != null)
/* 3480 */       return (String)context.get("nc.ui.uif2.I18nFB#5d54b9");
/* 3481 */     I18nFB bean = new I18nFB();
/* 3482 */     context.put("&nc.ui.uif2.I18nFB#5d54b9", bean);bean.setResDir("2006pub_0");
/* 3483 */     bean.setResId("02006pub-0643");
/* 3484 */     bean.setDefaultValue("��������");
/* 3485 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3486 */     invokeInitializingBean(bean);
/*      */     try {
/* 3488 */       Object product = bean.getObject();
/* 3489 */       context.put("nc.ui.uif2.I18nFB#5d54b9", product);
/* 3490 */       return (String)product;
/*      */     } catch (Exception e) {
/* 3492 */       throw new RuntimeException(e); } }
/*      */   
/* 3494 */   private List getManagedList43() { List list = new ArrayList();list.add(getRentAffiliated());list.add(getNoteRegister());list.add(getDocumentManage());list.add(getAssociateSettInfoAction());list.add(getDirectDebitRetAction());list.add(getDirectDebitCancelRetAction());return list;
/*      */   }
/*      */   
/* 3497 */   public nc.ui.arap.actions.AssociateSettInfoAction getAssociateSettInfoAction() { if (context.get("associateSettInfoAction") != null)
/* 3498 */       return (nc.ui.arap.actions.AssociateSettInfoAction)context.get("associateSettInfoAction");
/* 3499 */     nc.ui.arap.actions.AssociateSettInfoAction bean = new nc.ui.arap.actions.AssociateSettInfoAction();
/* 3500 */     context.put("associateSettInfoAction", bean);
/* 3501 */     bean.setModel(getManageAppModel());
/* 3502 */     bean.setBilltype("F2");
/* 3503 */     bean.setBillform(getBillFormEditor());
/* 3504 */     bean.setInterceptor(getInterceptor());
/* 3505 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3506 */     invokeInitializingBean(bean);
/* 3507 */     return bean;
/*      */   }
/*      */   
/*      */   public MenuAction getBillAssistantActionGroup_List() {
/* 3511 */     if (context.get("billAssistantActionGroup_List") != null)
/* 3512 */       return (MenuAction)context.get("billAssistantActionGroup_List");
/* 3513 */     MenuAction bean = new MenuAction();
/* 3514 */     context.put("billAssistantActionGroup_List", bean);
/* 3515 */     bean.setCode("billAssistant");
/* 3516 */     bean.setName(getI18nFB_16563d3());
/* 3517 */     bean.setActions(getManagedList44());
/* 3518 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3519 */     invokeInitializingBean(bean);
/* 3520 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_16563d3() {
/* 3524 */     if (context.get("nc.ui.uif2.I18nFB#16563d3") != null)
/* 3525 */       return (String)context.get("nc.ui.uif2.I18nFB#16563d3");
/* 3526 */     I18nFB bean = new I18nFB();
/* 3527 */     context.put("&nc.ui.uif2.I18nFB#16563d3", bean);bean.setResDir("2006pub_0");
/* 3528 */     bean.setResId("02006pub-0643");
/* 3529 */     bean.setDefaultValue("��������");
/* 3530 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3531 */     invokeInitializingBean(bean);
/*      */     try {
/* 3533 */       Object product = bean.getObject();
/* 3534 */       context.put("nc.ui.uif2.I18nFB#16563d3", product);
/* 3535 */       return (String)product;
/*      */     } catch (Exception e) {
/* 3537 */       throw new RuntimeException(e); } }
/*      */   
/* 3539 */   private List getManagedList44() { List list = new ArrayList();list.add(getRentAffiliated());list.add(getNoteRegister());list.add(getDocumentManage());list.add(getAssociateSettInfoAction());return list;
/*      */   }
/*      */   
/* 3542 */   public nc.ui.arap.actions.DirectDebitRetAction getDirectDebitRetAction() { if (context.get("directDebitRetAction") != null)
/* 3543 */       return (nc.ui.arap.actions.DirectDebitRetAction)context.get("directDebitRetAction");
/* 3544 */     nc.ui.arap.actions.DirectDebitRetAction bean = new nc.ui.arap.actions.DirectDebitRetAction();
/* 3545 */     context.put("directDebitRetAction", bean);
/* 3546 */     bean.setModel(getManageAppModel());
/* 3547 */     bean.setInterceptor(getInterceptor());
/* 3548 */     bean.setBillform(getBillFormEditor());
/* 3549 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3550 */     invokeInitializingBean(bean);
/* 3551 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.actions.DirectDebitCancelRetAction getDirectDebitCancelRetAction() {
/* 3555 */     if (context.get("directDebitCancelRetAction") != null)
/* 3556 */       return (nc.ui.arap.actions.DirectDebitCancelRetAction)context.get("directDebitCancelRetAction");
/* 3557 */     nc.ui.arap.actions.DirectDebitCancelRetAction bean = new nc.ui.arap.actions.DirectDebitCancelRetAction();
/* 3558 */     context.put("directDebitCancelRetAction", bean);
/* 3559 */     bean.setModel(getManageAppModel());
/* 3560 */     bean.setInterceptor(getInterceptor());
/* 3561 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3562 */     invokeInitializingBean(bean);
/* 3563 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.importable.GatheringRecordImportablePanel getImportableEditor() {
/* 3567 */     if (context.get("importableEditor") != null)
/* 3568 */       return (nc.ui.arap.importable.GatheringRecordImportablePanel)context.get("importableEditor");
/* 3569 */     nc.ui.arap.importable.GatheringRecordImportablePanel bean = new nc.ui.arap.importable.GatheringRecordImportablePanel();
/* 3570 */     context.put("importableEditor", bean);
/* 3571 */     bean.setUiEditor(getBillFormEditor());
/* 3572 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3573 */     invokeInitializingBean(bean);
/* 3574 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.importable.action.ArapImportAction getImportAction() {
/* 3578 */     if (context.get("importAction") != null)
/* 3579 */       return (nc.ui.arap.importable.action.ArapImportAction)context.get("importAction");
/* 3580 */     nc.ui.arap.importable.action.ArapImportAction bean = new nc.ui.arap.importable.action.ArapImportAction();
/* 3581 */     context.put("importAction", bean);
/* 3582 */     bean.setModel(getManageAppModel());
/* 3583 */     bean.setImportableEditor(getImportableEditor());
/* 3584 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3585 */     invokeInitializingBean(bean);
/* 3586 */     return bean;
/*      */   }
/*      */   
/*      */   public nc.ui.arap.importable.ArapExportAction getExportAction() {
/* 3590 */     if (context.get("exportAction") != null)
/* 3591 */       return (nc.ui.arap.importable.ArapExportAction)context.get("exportAction");
/* 3592 */     nc.ui.arap.importable.ArapExportAction bean = new nc.ui.arap.importable.ArapExportAction();
/* 3593 */     context.put("exportAction", bean);
/* 3594 */     bean.setImportableEditor(getImportableEditor());
/* 3595 */     bean.setModel(getManageAppModel());
/* 3596 */     bean.setInterceptor(getExportActionInterceptor());
/* 3597 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3598 */     invokeInitializingBean(bean);
/* 3599 */     return bean;
/*      */   }
/*      */   
/*      */   public MenuAction getImportexportActionGroup() {
/* 3603 */     if (context.get("importexportActionGroup") != null)
/* 3604 */       return (MenuAction)context.get("importexportActionGroup");
/* 3605 */     MenuAction bean = new MenuAction();
/* 3606 */     context.put("importexportActionGroup", bean);
/* 3607 */     bean.setCode("ImportExportMenu");
/* 3608 */     bean.setName(getI18nFB_16426d5());
/* 3609 */     bean.setActions(getManagedList45());
/* 3610 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3611 */     invokeInitializingBean(bean);
/* 3612 */     return bean;
/*      */   }
/*      */   
/*      */   private String getI18nFB_16426d5() {
/* 3616 */     if (context.get("nc.ui.uif2.I18nFB#16426d5") != null)
/* 3617 */       return (String)context.get("nc.ui.uif2.I18nFB#16426d5");
/* 3618 */     I18nFB bean = new I18nFB();
/* 3619 */     context.put("&nc.ui.uif2.I18nFB#16426d5", bean);bean.setResDir("2006pub_0");
/* 3620 */     bean.setResId("02006pub-0654");
/* 3621 */     bean.setDefaultValue("���뵼��");
/* 3622 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3623 */     invokeInitializingBean(bean);
/*      */     try {
/* 3625 */       Object product = bean.getObject();
/* 3626 */       context.put("nc.ui.uif2.I18nFB#16426d5", product);
/* 3627 */       return (String)product;
/*      */     } catch (Exception e) {
/* 3629 */       throw new RuntimeException(e); } }
/*      */   
/* 3631 */   private List getManagedList45() { List list = new ArrayList();list.add(getImportAction());list.add(getCurrNullAction());list.add(getExportAction());return list;
/*      */   }
/*      */   
/* 3634 */   public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getListActions() { if (context.get("listActions") != null)
/* 3635 */       return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("listActions");
/* 3636 */     nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getListView());context.put("listActions", bean);
/* 3637 */     bean.setActions(getManagedList46());
/* 3638 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3639 */     invokeInitializingBean(bean);
/* 3640 */     return bean;
/*      */   }
/*      */   
/* 3643 */   private List getManagedList46() { List list = new ArrayList();list.add(getAddActionGroup());list.add(getEditAction());list.add(getDeleteAction());list.add(getCopyAction());list.add(getCurrNullAction());list.add(getQueryAction());list.add(getRefreshAllAction());list.add(getCurrNullAction());list.add(getTranstype());list.add(getBillAssistantActionGroup_List());list.add(getCurrNullAction());list.add(getReceiptMenuAction());list.add(getCurrNullAction());list.add(getRelatedQueryActionGroup());list.add(getCurrNullAction());list.add(getImportexportActionGroup());list.add(getPrintOperateActionGroup_List());return list;
/*      */   }
/*      */   
/* 3646 */   public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getCardActions() { if (context.get("cardActions") != null)
/* 3647 */       return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("cardActions");
/* 3648 */     nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getBillFormEditor());context.put("cardActions", bean);
/* 3649 */     bean.setActions(getManagedList47());
/* 3650 */     bean.setEditActions(getManagedList48());
/* 3651 */     bean.setModel(getManageAppModel());
/* 3652 */     setBeanFacotryIfBeanFacatoryAware(bean);
/* 3653 */     invokeInitializingBean(bean);
/* 3654 */     return bean;
/*      */   }
/*      */     //libin ����ԭ���水ť��Ϊ �ύ��ť   list.add(getCommitAction())  list.add(getRecallAction())
/* 3657 */   private List getManagedList47() { List list = new ArrayList();list.add(getAddActionGroup());list.add(getEditAction());list.add(getDeleteAction());list.add(getCommitAction()); list.add(getRecallAction());list.add(getCopyAction());list.add(getCurrNullAction());list.add(getQueryAction());list.add(getRefreshSingAction());list.add(getCurrNullAction());list.add(getTranstype());list.add(getBillAssistantActionGroup());list.add(getCurrNullAction());list.add(getReceiptMenuAction());list.add(getCurrNullAction());list.add(getRelatedQueryActionGroup());list.add(getCurrNullAction());list.add(getImportexportActionGroup());list.add(getPrintOperateActionGroup());return list; }
/*      */   
/* 3659 */   private List getManagedList48() { List list = new ArrayList();list.add(getSaveAction());list.add(getTempSaveAction());list.add(getCurrNullAction());list.add(getCancelAction());list.add(getCurrNullAction());list.add(getDocumentManage());list.add(getReceiptMenuAction());return list;
/*      */   }
/*      */ }

/* Location:           E:\work\Yonyou\home\sdgshome20201022\modules\arap\client\classes
 * Qualified Name:     nc.ui.arap.config.gatheringbill_record
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */