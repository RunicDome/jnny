package nc.ui.pm.pmrlcontractalter.ace.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class PmRLContractalter_config extends AbstractJavaBeanDefinition{
	private Map<String, Object> context = new HashMap();
public nc.vo.uif2.LoginContext getContext(){
 if(context.get("context")!=null)
 return (nc.vo.uif2.LoginContext)context.get("context");
  nc.vo.uif2.LoginContext bean = new nc.vo.uif2.LoginContext();
  context.put("context",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pm.pmrlcontractalter.ace.serviceproxy.AcePmRLContractalterMaintainProxy getBmModelModelService(){
 if(context.get("bmModelModelService")!=null)
 return (nc.ui.pm.pmrlcontractalter.ace.serviceproxy.AcePmRLContractalterMaintainProxy)context.get("bmModelModelService");
  nc.ui.pm.pmrlcontractalter.ace.serviceproxy.AcePmRLContractalterMaintainProxy bean = new nc.ui.pm.pmrlcontractalter.ace.serviceproxy.AcePmRLContractalterMaintainProxy();
  context.put("bmModelModelService",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.vo.bd.meta.GeneralBDObjectAdapterFactory getBOAdapterFactory(){
 if(context.get("BOAdapterFactory")!=null)
 return (nc.vo.bd.meta.GeneralBDObjectAdapterFactory)context.get("BOAdapterFactory");
  nc.vo.bd.meta.GeneralBDObjectAdapterFactory bean = new nc.vo.bd.meta.GeneralBDObjectAdapterFactory();
  context.put("BOAdapterFactory",bean);
  bean.setMode("MD");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.model.BillManageModel getBmModel(){
 if(context.get("bmModel")!=null)
 return (nc.ui.pubapp.uif2app.model.BillManageModel)context.get("bmModel");
  nc.ui.pubapp.uif2app.model.BillManageModel bean = new nc.ui.pubapp.uif2app.model.BillManageModel();
  context.put("bmModel",bean);
  bean.setContext(getContext());
  bean.setBusinessObjectAdapterFactory(getBOAdapterFactory());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.model.pagination.PaginationModelDataManager getBmModelModelDataManager(){
 if(context.get("bmModelModelDataManager")!=null)
 return (nc.ui.pubapp.uif2app.model.pagination.PaginationModelDataManager)context.get("bmModelModelDataManager");
  nc.ui.pubapp.uif2app.model.pagination.PaginationModelDataManager bean = new nc.ui.pubapp.uif2app.model.pagination.PaginationModelDataManager();
  context.put("bmModelModelDataManager",bean);
  bean.setModel(getBmModel());
  bean.setPageQueryService(getPageQueryService());
  bean.setPaginationModel(getPaginationModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.model.pagination.PubPaginationModel getPaginationModel(){
 if(context.get("paginationModel")!=null)
 return (nc.ui.pubapp.uif2app.model.pagination.PubPaginationModel)context.get("paginationModel");
  nc.ui.pubapp.uif2app.model.pagination.PubPaginationModel bean = new nc.ui.pubapp.uif2app.model.pagination.PubPaginationModel();
  context.put("paginationModel",bean);
  bean.setPaginationQueryService(getBmModelModelService());
  bean.setPageSize(10);
  bean.init();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.model.pagination.UIPageQueryService getPageQueryService(){
 if(context.get("pageQueryService")!=null)
 return (nc.ui.pubapp.uif2app.model.pagination.UIPageQueryService)context.get("pageQueryService");
  nc.ui.pubapp.uif2app.model.pagination.UIPageQueryService bean = new nc.ui.pubapp.uif2app.model.pagination.UIPageQueryService();
  context.put("pageQueryService",bean);
  bean.setAllPagePkQueryServiceMethod("nc.itf.pm.IPmRLContractalterMaintain.queryPks");
  bean.setDataOfPksQueryServiceMethod("nc.itf.pm.IPmRLContractalterMaintain.queryObjectByPks");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.components.pagination.PaginationBar getPageBar(){
 if(context.get("pageBar")!=null)
 return (nc.ui.uif2.components.pagination.PaginationBar)context.get("pageBar");
  nc.ui.uif2.components.pagination.PaginationBar bean = new nc.ui.uif2.components.pagination.PaginationBar();
  context.put("pageBar",bean);
  bean.setContext(getContext());
  bean.setPaginationModel(getPaginationModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.view.TemplateContainer getTemplateContainer(){
 if(context.get("templateContainer")!=null)
 return (nc.ui.pubapp.uif2app.view.TemplateContainer)context.get("templateContainer");
  nc.ui.pubapp.uif2app.view.TemplateContainer bean = new nc.ui.pubapp.uif2app.view.TemplateContainer();
  context.put("templateContainer",bean);
  bean.setContext(getContext());
  bean.setNodeKeies(getManagedList0());
  bean.load();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList0(){  List list = new ArrayList();  list.add("bt");  return list;}

public nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell getViewa(){
 if(context.get("viewa")!=null)
 return (nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell)context.get("viewa");
  nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell bean = new nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell();
  context.put("viewa",bean);
  bean.setQueryAreaCreator(getDefaultQueryAction());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.view.ShowUpableBillListView getBillListView(){
 if(context.get("billListView")!=null)
 return (nc.ui.pubapp.uif2app.view.ShowUpableBillListView)context.get("billListView");
  nc.ui.pubapp.uif2app.view.ShowUpableBillListView bean = new nc.ui.pubapp.uif2app.view.ShowUpableBillListView();
  context.put("billListView",bean);
  bean.setModel(getBmModel());
  bean.setNodekey("bt");
  bean.setMultiSelectionEnable(false);
  bean.setPaginationBar(getPageBar());
  bean.setTemplateContainer(getTemplateContainer());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel getViewb(){
 if(context.get("viewb")!=null)
 return (nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel)context.get("viewb");
  nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel bean = new nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel();
  context.put("viewb",bean);
  bean.setModel(getBmModel());
  bean.setTitleAction(getReturnAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.actions.UEReturnAction getReturnAction(){
 if(context.get("returnAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.UEReturnAction)context.get("returnAction");
  nc.ui.pubapp.uif2app.actions.UEReturnAction bean = new nc.ui.pubapp.uif2app.actions.UEReturnAction();
  context.put("returnAction",bean);
  bean.setGoComponent(getBillListView());
  bean.setSaveAction(getSaveScriptAction());
  bean.setModel(getBmModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.view.ShowUpableBillForm getBillForm(){
 if(context.get("billForm")!=null)
 return (nc.ui.pubapp.uif2app.view.ShowUpableBillForm)context.get("billForm");
  nc.ui.pubapp.uif2app.view.ShowUpableBillForm bean = new nc.ui.pubapp.uif2app.view.ShowUpableBillForm();
  context.put("billForm",bean);
  bean.setModel(getBmModel());
  bean.setNodekey("bt");
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.TangramContainer getContainer(){
 if(context.get("container")!=null)
 return (nc.ui.uif2.TangramContainer)context.get("container");
  nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
  context.put("container",bean);
  bean.setTangramLayoutRoot(getTBNode_aebe7d());
  bean.setActions(getManagedList2());
  bean.setEditActions(getManagedList3());
  bean.setModel(getBmModel());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_aebe7d(){
 if(context.get("nc.ui.uif2.tangramlayout.node.TBNode#aebe7d")!=null)
 return (nc.ui.uif2.tangramlayout.node.TBNode)context.get("nc.ui.uif2.tangramlayout.node.TBNode#aebe7d");
  nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
  context.put("nc.ui.uif2.tangramlayout.node.TBNode#aebe7d",bean);
  bean.setTabs(getManagedList1());
  bean.setName("cardLayout");
  bean.setShowMode("CardLayout");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList1(){  List list = new ArrayList();  list.add(getHSNode_9e3519());  list.add(getVSNode_11f7e92());  return list;}

private nc.ui.uif2.tangramlayout.node.HSNode getHSNode_9e3519(){
 if(context.get("nc.ui.uif2.tangramlayout.node.HSNode#9e3519")!=null)
 return (nc.ui.uif2.tangramlayout.node.HSNode)context.get("nc.ui.uif2.tangramlayout.node.HSNode#9e3519");
  nc.ui.uif2.tangramlayout.node.HSNode bean = new nc.ui.uif2.tangramlayout.node.HSNode();
  context.put("nc.ui.uif2.tangramlayout.node.HSNode#9e3519",bean);
  bean.setLeft(getCNode_178aec2());
  bean.setRight(getCNode_1981e2());
  bean.setDividerLocation(215.0f);
  bean.setName("列表");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_178aec2(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#178aec2")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#178aec2");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#178aec2",bean);
  bean.setComponent(getViewa());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_1981e2(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#1981e2")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#1981e2");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#1981e2",bean);
  bean.setComponent(getBillListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_11f7e92(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#11f7e92")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#11f7e92");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#11f7e92",bean);
  bean.setUp(getCNode_b02ac9());
  bean.setDown(getCNode_1c91767());
  bean.setDividerLocation(43.0f);
  bean.setName("卡片");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_b02ac9(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#b02ac9")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#b02ac9");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#b02ac9",bean);
  bean.setComponent(getViewb());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_1c91767(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#1c91767")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#1c91767");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#1c91767",bean);
  bean.setComponent(getBillForm());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList2(){  List list = new ArrayList();  list.add(getAddMenuAction());  list.add(getEditAction());  list.add(getDeleteScriptAction());  list.add(getDefaultQueryAction());  list.add(getSeparatorAction());  list.add(getDefaultRefreshAction());  list.add(getSeparatorAction());  list.add(getCommitScriptAction());  list.add(getUnCommitScriptAction());  list.add(getApproveScriptAction());  list.add(getUNApproveScriptAction());  list.add(getSeparatorAction());  list.add(getLinkQueryAction());  list.add(getSeparatorAction());  list.add(getMetaDataBasedPrintAction());  list.add(getMetaDataBasedPrintActiona());  list.add(getOutputAction());  list.add(getSeparatorAction());  list.add(getPFApproveStatusInfoAction());  return list;}

private List getManagedList3(){  List list = new ArrayList();  list.add(getSaveScriptAction());  list.add(getCancelAction());  return list;}

public nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener getInitDataListener(){
 if(context.get("InitDataListener")!=null)
 return (nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener)context.get("InitDataListener");
  nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener bean = new nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener();
  context.put("InitDataListener",bean);
  bean.setModel(getBmModel());
  bean.setContext(getContext());
  bean.setVoClassName("nc.vo.pm.rlcontractalter.AggRLContractalterHVO");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.common.validateservice.ClosingCheck getClosingListener(){
 if(context.get("ClosingListener")!=null)
 return (nc.ui.pubapp.common.validateservice.ClosingCheck)context.get("ClosingListener");
  nc.ui.pubapp.common.validateservice.ClosingCheck bean = new nc.ui.pubapp.common.validateservice.ClosingCheck();
  context.put("ClosingListener",bean);
  bean.setModel(getBmModel());
  bean.setSaveAction(getSaveScriptAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.model.AppEventHandlerMediator getBmModelEventMediator(){
 if(context.get("bmModelEventMediator")!=null)
 return (nc.ui.pubapp.uif2app.model.AppEventHandlerMediator)context.get("bmModelEventMediator");
  nc.ui.pubapp.uif2app.model.AppEventHandlerMediator bean = new nc.ui.pubapp.uif2app.model.AppEventHandlerMediator();
  context.put("bmModelEventMediator",bean);
  bean.setModel(getBmModel());
  bean.setHandlerGroup(getManagedList4());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList4(){  List list = new ArrayList();  list.add(getEventHandlerGroup_13e3b73());  list.add(getEventHandlerGroup_1b80199());  return list;}

private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_13e3b73(){
 if(context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#13e3b73")!=null)
 return (nc.ui.pubapp.uif2app.event.EventHandlerGroup)context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#13e3b73");
  nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
  context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#13e3b73",bean);
  bean.setEvent("nc.ui.pubapp.uif2app.event.OrgChangedEvent");
  bean.setHandler(getAceOrgChangeHandler_5c1462());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pm.pmrlcontractalter.ace.handler.AceOrgChangeHandler getAceOrgChangeHandler_5c1462(){
 if(context.get("nc.ui.pm.pmrlcontractalter.ace.handler.AceOrgChangeHandler#5c1462")!=null)
 return (nc.ui.pm.pmrlcontractalter.ace.handler.AceOrgChangeHandler)context.get("nc.ui.pm.pmrlcontractalter.ace.handler.AceOrgChangeHandler#5c1462");
  nc.ui.pm.pmrlcontractalter.ace.handler.AceOrgChangeHandler bean = new nc.ui.pm.pmrlcontractalter.ace.handler.AceOrgChangeHandler();
  context.put("nc.ui.pm.pmrlcontractalter.ace.handler.AceOrgChangeHandler#5c1462",bean);
  bean.setBillForm(getBillForm());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_1b80199(){
 if(context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1b80199")!=null)
 return (nc.ui.pubapp.uif2app.event.EventHandlerGroup)context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1b80199");
  nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
  context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1b80199",bean);
  bean.setEvent("nc.ui.pubapp.uif2app.event.billform.AddEvent");
  bean.setHandler(getAceAddHandler_b104e3());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pm.pmrlcontractalter.ace.handler.AceAddHandler getAceAddHandler_b104e3(){
 if(context.get("nc.ui.pm.pmrlcontractalter.ace.handler.AceAddHandler#b104e3")!=null)
 return (nc.ui.pm.pmrlcontractalter.ace.handler.AceAddHandler)context.get("nc.ui.pm.pmrlcontractalter.ace.handler.AceAddHandler#b104e3");
  nc.ui.pm.pmrlcontractalter.ace.handler.AceAddHandler bean = new nc.ui.pm.pmrlcontractalter.ace.handler.AceAddHandler();
  context.put("nc.ui.pm.pmrlcontractalter.ace.handler.AceAddHandler#b104e3",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.lazilyload.DefaultBillLazilyLoader getBillLazilyLoader(){
 if(context.get("billLazilyLoader")!=null)
 return (nc.ui.pubapp.uif2app.lazilyload.DefaultBillLazilyLoader)context.get("billLazilyLoader");
  nc.ui.pubapp.uif2app.lazilyload.DefaultBillLazilyLoader bean = new nc.ui.pubapp.uif2app.lazilyload.DefaultBillLazilyLoader();
  context.put("billLazilyLoader",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager getBmModelLasilyLodadMediator(){
 if(context.get("bmModelLasilyLodadMediator")!=null)
 return (nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager)context.get("bmModelLasilyLodadMediator");
  nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager bean = new nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager();
  context.put("bmModelLasilyLodadMediator",bean);
  bean.setModel(getBmModel());
  bean.setLoader(getBillLazilyLoader());
  bean.setLazilyLoadSupporter(getManagedList5());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList5(){  List list = new ArrayList();  list.add(getCardPanelLazilyLoad_170f206());  list.add(getListPanelLazilyLoad_218fa2());  return list;}

private nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad getCardPanelLazilyLoad_170f206(){
 if(context.get("nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad#170f206")!=null)
 return (nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad)context.get("nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad#170f206");
  nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad bean = new nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad();
  context.put("nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad#170f206",bean);
  bean.setBillform(getBillForm());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad getListPanelLazilyLoad_218fa2(){
 if(context.get("nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad#218fa2")!=null)
 return (nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad)context.get("nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad#218fa2");
  nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad bean = new nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad();
  context.put("nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad#218fa2",bean);
  bean.setListView(getBillListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.view.RowNoMediator getRowNoMediator(){
 if(context.get("rowNoMediator")!=null)
 return (nc.ui.pubapp.uif2app.view.RowNoMediator)context.get("rowNoMediator");
  nc.ui.pubapp.uif2app.view.RowNoMediator bean = new nc.ui.pubapp.uif2app.view.RowNoMediator();
  context.put("rowNoMediator",bean);
  bean.setModel(getBmModel());
  bean.setEditor(getBillForm());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator getMouseClickShowPanelMediator(){
 if(context.get("mouseClickShowPanelMediator")!=null)
 return (nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator)context.get("mouseClickShowPanelMediator");
  nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator bean = new nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator();
  context.put("mouseClickShowPanelMediator",bean);
  bean.setListView(getBillListView());
  bean.setShowUpComponent(getBillForm());
  bean.setHyperLinkColumn("pk_contr");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.bill.BillCodeMediator getBillCodeMediator(){
 if(context.get("billCodeMediator")!=null)
 return (nc.ui.pubapp.bill.BillCodeMediator)context.get("billCodeMediator");
  nc.ui.pubapp.bill.BillCodeMediator bean = new nc.ui.pubapp.bill.BillCodeMediator();
  context.put("billCodeMediator",bean);
  bean.setBillForm(getBillForm());
  bean.setBillCodeKey("pk_contr");
  bean.setBillType("4Z05");
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pm.action.container.AssMenuAction getAssMenuAction(){
 if(context.get("assMenuAction")!=null)
 return (nc.ui.pm.action.container.AssMenuAction)context.get("assMenuAction");
  nc.ui.pm.action.container.AssMenuAction bean = new nc.ui.pm.action.container.AssMenuAction();
  context.put("assMenuAction",bean);
  bean.setCode("assMenuAction");
  bean.setActions(getManagedList6());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList6(){  List list = new ArrayList();  list.add(getPmAttachmentAction());  return list;}

public nc.ui.pm.action.NewPMAttachmentAction getPmAttachmentAction(){
 if(context.get("pmAttachmentAction")!=null)
 return (nc.ui.pm.action.NewPMAttachmentAction)context.get("pmAttachmentAction");
  nc.ui.pm.action.NewPMAttachmentAction bean = new nc.ui.pm.action.NewPMAttachmentAction();
  context.put("pmAttachmentAction",bean);
  bean.setModel(getBmModel());
  bean.setApproveNoDelete(true);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.AddAction getAddAction(){
 if(context.get("addAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.AddAction)context.get("addAction");
  nc.ui.pubapp.uif2app.actions.AddAction bean = new nc.ui.pubapp.uif2app.actions.AddAction();
  context.put("addAction",bean);
  bean.setModel(getBmModel());
  bean.setInterceptor(getCompositeActionInterceptor_2704a8());
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor getCompositeActionInterceptor_2704a8(){
 if(context.get("nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor#2704a8")!=null)
 return (nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor)context.get("nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor#2704a8");
  nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor();
  context.put("nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor#2704a8",bean);
  bean.setInterceptors(getManagedList7());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList7(){  List list = new ArrayList();  list.add(getShowUpComponentInterceptor_15cee64());  return list;}

private nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor getShowUpComponentInterceptor_15cee64(){
 if(context.get("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#15cee64")!=null)
 return (nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor)context.get("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#15cee64");
  nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor();
  context.put("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#15cee64",bean);
  bean.setShowUpComponent(getBillForm());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.EditAction getEditAction(){
 if(context.get("editAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.EditAction)context.get("editAction");
  nc.ui.pubapp.uif2app.actions.EditAction bean = new nc.ui.pubapp.uif2app.actions.EditAction();
  context.put("editAction",bean);
  bean.setModel(getBmModel());
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.pflow.DeleteScriptAction getDeleteScriptAction(){
 if(context.get("deleteScriptAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.pflow.DeleteScriptAction)context.get("deleteScriptAction");
  nc.ui.pubapp.uif2app.actions.pflow.DeleteScriptAction bean = new nc.ui.pubapp.uif2app.actions.pflow.DeleteScriptAction();
  context.put("deleteScriptAction",bean);
  bean.setModel(getBmModel());
  bean.setBillType("4Z05");
  bean.setFilledUpInFlow(true);
  bean.setActionName("DELETE");
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.editor.QueryTemplateContainer getDefaultQueryActionQueryTemplateContainer(){
 if(context.get("defaultQueryActionQueryTemplateContainer")!=null)
 return (nc.ui.uif2.editor.QueryTemplateContainer)context.get("defaultQueryActionQueryTemplateContainer");
  nc.ui.uif2.editor.QueryTemplateContainer bean = new nc.ui.uif2.editor.QueryTemplateContainer();
  context.put("defaultQueryActionQueryTemplateContainer",bean);
  bean.setNodeKey("qt");
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction getDefaultQueryAction(){
 if(context.get("defaultQueryAction")!=null)
 return (nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction)context.get("defaultQueryAction");
  nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction bean = new nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction();
  context.put("defaultQueryAction",bean);
  bean.setModel(getBmModel());
  bean.setTemplateContainer(getDefaultQueryActionQueryTemplateContainer());
  bean.setNodeKey("qt");
  bean.setDataManager(getBmModelModelDataManager());
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.CopyAction getCopyAction(){
 if(context.get("copyAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.CopyAction)context.get("copyAction");
  nc.ui.pubapp.uif2app.actions.CopyAction bean = new nc.ui.pubapp.uif2app.actions.CopyAction();
  context.put("copyAction",bean);
  bean.setModel(getBmModel());
  bean.setEditor(getBillForm());
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction getDefaultRefreshAction(){
 if(context.get("defaultRefreshAction")!=null)
 return (nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction)context.get("defaultRefreshAction");
  nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction bean = new nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction();
  context.put("defaultRefreshAction",bean);
  bean.setModel(getBmModel());
  bean.setDataManager(getBmModelModelDataManager());
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.pflow.CommitScriptAction getCommitScriptAction(){
 if(context.get("commitScriptAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.pflow.CommitScriptAction)context.get("commitScriptAction");
  nc.ui.pubapp.uif2app.actions.pflow.CommitScriptAction bean = new nc.ui.pubapp.uif2app.actions.pflow.CommitScriptAction();
  context.put("commitScriptAction",bean);
  bean.setModel(getBmModel());
  bean.setEditor(getBillForm());
  bean.setBillType("4Z05");
  bean.setFilledUpInFlow(true);
  bean.setActionName("SAVE");
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.pflow.UnCommitScriptAction getUnCommitScriptAction(){
 if(context.get("unCommitScriptAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.pflow.UnCommitScriptAction)context.get("unCommitScriptAction");
  nc.ui.pubapp.uif2app.actions.pflow.UnCommitScriptAction bean = new nc.ui.pubapp.uif2app.actions.pflow.UnCommitScriptAction();
  context.put("unCommitScriptAction",bean);
  bean.setModel(getBmModel());
  bean.setEditor(getBillForm());
  bean.setBillType("4Z05");
  bean.setFilledUpInFlow(true);
  bean.setActionName("UNSAVEBILL");
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.pflow.ApproveScriptAction getApproveScriptAction(){
 if(context.get("approveScriptAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.pflow.ApproveScriptAction)context.get("approveScriptAction");
  nc.ui.pubapp.uif2app.actions.pflow.ApproveScriptAction bean = new nc.ui.pubapp.uif2app.actions.pflow.ApproveScriptAction();
  context.put("approveScriptAction",bean);
  bean.setModel(getBmModel());
  bean.setEditor(getBillForm());
  bean.setBillType("4Z05");
  bean.setFilledUpInFlow(true);
  bean.setActionName("APPROVE");
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.pflow.UNApproveScriptAction getUNApproveScriptAction(){
 if(context.get("uNApproveScriptAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.pflow.UNApproveScriptAction)context.get("uNApproveScriptAction");
  nc.ui.pubapp.uif2app.actions.pflow.UNApproveScriptAction bean = new nc.ui.pubapp.uif2app.actions.pflow.UNApproveScriptAction();
  context.put("uNApproveScriptAction",bean);
  bean.setModel(getBmModel());
  bean.setEditor(getBillForm());
  bean.setBillType("4Z05");
  bean.setFilledUpInFlow(true);
  bean.setActionName("UNAPPROVE");
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.LinkQueryAction getLinkQueryAction(){
 if(context.get("linkQueryAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.LinkQueryAction)context.get("linkQueryAction");
  nc.ui.pubapp.uif2app.actions.LinkQueryAction bean = new nc.ui.pubapp.uif2app.actions.LinkQueryAction();
  context.put("linkQueryAction",bean);
  bean.setBillType("4Z05");
  bean.setModel(getBmModel());
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction getMetaDataBasedPrintAction(){
 if(context.get("metaDataBasedPrintAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction)context.get("metaDataBasedPrintAction");
  nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction bean = new nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction();
  context.put("metaDataBasedPrintAction",bean);
  bean.setModel(getBmModel());
  bean.setActioncode("Preview");
  bean.setActionname("预览");
  bean.setPreview(true);
  bean.setNodeKey("ot");
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction getMetaDataBasedPrintActiona(){
 if(context.get("metaDataBasedPrintActiona")!=null)
 return (nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction)context.get("metaDataBasedPrintActiona");
  nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction bean = new nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction();
  context.put("metaDataBasedPrintActiona",bean);
  bean.setModel(getBmModel());
  bean.setActioncode("Print");
  bean.setActionname("打印");
  bean.setPreview(false);
  bean.setNodeKey("ot");
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.OutputAction getOutputAction(){
 if(context.get("outputAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.OutputAction)context.get("outputAction");
  nc.ui.pubapp.uif2app.actions.OutputAction bean = new nc.ui.pubapp.uif2app.actions.OutputAction();
  context.put("outputAction",bean);
  bean.setModel(getBmModel());
  bean.setEditor(getBillForm());
  bean.setNodeKey("ot");
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.pflow.PFApproveStatusInfoAction getPFApproveStatusInfoAction(){
 if(context.get("pFApproveStatusInfoAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.pflow.PFApproveStatusInfoAction)context.get("pFApproveStatusInfoAction");
  nc.ui.pubapp.uif2app.actions.pflow.PFApproveStatusInfoAction bean = new nc.ui.pubapp.uif2app.actions.pflow.PFApproveStatusInfoAction();
  context.put("pFApproveStatusInfoAction",bean);
  bean.setModel(getBmModel());
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.pflow.SaveScriptAction getSaveScriptAction(){
 if(context.get("saveScriptAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.pflow.SaveScriptAction)context.get("saveScriptAction");
  nc.ui.pubapp.uif2app.actions.pflow.SaveScriptAction bean = new nc.ui.pubapp.uif2app.actions.pflow.SaveScriptAction();
  context.put("saveScriptAction",bean);
  bean.setModel(getBmModel());
  bean.setEditor(getBillForm());
  bean.setBillType("4Z05");
  bean.setFilledUpInFlow(true);
  bean.setActionName("SAVEBASE");
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.CancelAction getCancelAction(){
 if(context.get("cancelAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.CancelAction)context.get("cancelAction");
  nc.ui.pubapp.uif2app.actions.CancelAction bean = new nc.ui.pubapp.uif2app.actions.CancelAction();
  context.put("cancelAction",bean);
  bean.setModel(getBmModel());
  bean.setEditor(getBillForm());
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.SeparatorAction getSeparatorAction(){
 if(context.get("separatorAction")!=null)
 return (nc.funcnode.ui.action.SeparatorAction)context.get("separatorAction");
  nc.funcnode.ui.action.SeparatorAction bean = new nc.funcnode.ui.action.SeparatorAction();
  context.put("separatorAction",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.DefaultExceptionHanler getExceptionHandler(){
 if(context.get("exceptionHandler")!=null)
 return (nc.ui.uif2.DefaultExceptionHanler)context.get("exceptionHandler");
  nc.ui.uif2.DefaultExceptionHanler bean = new nc.ui.uif2.DefaultExceptionHanler(getContainer());  context.put("exceptionHandler",bean);
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getActionsOfList(){
 if(context.get("actionsOfList")!=null)
 return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("actionsOfList");
  nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getBillListView());  context.put("actionsOfList",bean);
  bean.setActions(getManagedList8());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList8(){  List list = new ArrayList();  list.add(getAddMenuAction());  list.add(getEditAction());  list.add(getDeleteScriptAction());  list.add(getDefaultQueryAction());  list.add(getSeparatorAction());  list.add(getRefreshAction());  list.add(getSeparatorAction());  list.add(getCommitScriptAction());  list.add(getUnCommitScriptAction());  list.add(getApproveScriptAction());  list.add(getUNApproveScriptAction());  list.add(getSeparatorAction());  list.add(getLinkQueryAction());  list.add(getSeparatorAction());  list.add(getMetaDataBasedPrintAction());  list.add(getMetaDataBasedPrintActiona());  list.add(getOutputAction());  list.add(getSeparatorAction());  list.add(getPFApproveStatusInfoAction());  list.add(getAssMenuAction());  return list;}

public nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction getRefreshAction(){
 if(context.get("refreshAction")!=null)
 return (nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction)context.get("refreshAction");
  nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction bean = new nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction();
  context.put("refreshAction",bean);
  bean.setDataManager(getBmModelModelDataManager());
  bean.setModel(getBmModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.RefreshSingleAction getCardRefreshAction(){
 if(context.get("cardRefreshAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.RefreshSingleAction)context.get("cardRefreshAction");
  nc.ui.pubapp.uif2app.actions.RefreshSingleAction bean = new nc.ui.pubapp.uif2app.actions.RefreshSingleAction();
  context.put("cardRefreshAction",bean);
  bean.setModel(getBmModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getActionsOfCard(){
 if(context.get("actionsOfCard")!=null)
 return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("actionsOfCard");
  nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getBillForm());  context.put("actionsOfCard",bean);
  bean.setModel(getBmModel());
  bean.setActions(getManagedList9());
  bean.setEditActions(getManagedList10());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList9(){  List list = new ArrayList();  list.add(getAddMenuAction());  list.add(getEditAction());  list.add(getDeleteScriptAction());  list.add(getDefaultQueryAction());  list.add(getSeparatorAction());  list.add(getCardRefreshAction());  list.add(getSeparatorAction());  list.add(getCommitScriptAction());  list.add(getUnCommitScriptAction());  list.add(getApproveScriptAction());  list.add(getUNApproveScriptAction());  list.add(getSeparatorAction());  list.add(getLinkQueryAction());  list.add(getSeparatorAction());  list.add(getMetaDataBasedPrintAction());  list.add(getMetaDataBasedPrintActiona());  list.add(getOutputAction());  list.add(getSeparatorAction());  list.add(getPFApproveStatusInfoAction());  list.add(getAssMenuAction());  return list;}

private List getManagedList10(){  List list = new ArrayList();  list.add(getSaveScriptAction());  list.add(getCancelAction());  return list;}

public nc.ui.pubapp.uif2app.actions.AddMenuAction getAddMenuAction(){
 if(context.get("addMenuAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.AddMenuAction)context.get("addMenuAction");
  nc.ui.pubapp.uif2app.actions.AddMenuAction bean = new nc.ui.pubapp.uif2app.actions.AddMenuAction();
  context.put("addMenuAction",bean);
  bean.setBillType("4Z03");
  bean.setActions(getManagedList11());
  bean.setModel(getBmModel());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList11(){  List list = new ArrayList();  list.add(getSeparatorAction());  list.add(getAddAction1());  return list;}

public nc.ui.pm.pmrlcontractalter.ace.action.AddFromSource4Z05Action getAddAction1(){
 if(context.get("addAction1")!=null)
 return (nc.ui.pm.pmrlcontractalter.ace.action.AddFromSource4Z05Action)context.get("addAction1");
  nc.ui.pm.pmrlcontractalter.ace.action.AddFromSource4Z05Action bean = new nc.ui.pm.pmrlcontractalter.ace.action.AddFromSource4Z05Action();
  context.put("addAction1",bean);
  bean.setSourceBillType("4Z01");
  bean.setSourceBillName("多编码合同");
  bean.setFlowBillType(false);
  bean.setModel(getBmModel());
  bean.setEditor(getBillForm());
  bean.setTransferViewProcessor(getTransferViewProcessor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.billref.dest.TransferViewProcessor getTransferViewProcessor(){
 if(context.get("transferViewProcessor")!=null)
 return (nc.ui.pubapp.billref.dest.TransferViewProcessor)context.get("transferViewProcessor");
  nc.ui.pubapp.billref.dest.TransferViewProcessor bean = new nc.ui.pubapp.billref.dest.TransferViewProcessor();
  context.put("transferViewProcessor",bean);
  bean.setList(getBillListView());
  bean.setActionContainer(getActionsOfList());
  bean.setCardActionContainer(getActionsOfCard());
  bean.setSaveAction(getSaveScriptAction());
  bean.setCommitAction(getCommitScriptAction());
  bean.setCancelAction(getCancelAction());
  bean.setBillForm(getBillForm());
  bean.setTransferLogic(getDefaultBillDataLogic_729483());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.billref.dest.DefaultBillDataLogic getDefaultBillDataLogic_729483(){
 if(context.get("nc.ui.pubapp.billref.dest.DefaultBillDataLogic#729483")!=null)
 return (nc.ui.pubapp.billref.dest.DefaultBillDataLogic)context.get("nc.ui.pubapp.billref.dest.DefaultBillDataLogic#729483");
  nc.ui.pubapp.billref.dest.DefaultBillDataLogic bean = new nc.ui.pubapp.billref.dest.DefaultBillDataLogic();
  context.put("nc.ui.pubapp.billref.dest.DefaultBillDataLogic#729483",bean);
  bean.setBillForm(getBillForm());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

}
