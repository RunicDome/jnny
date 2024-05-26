package nc.ui.bd.supplier.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class supplier_tsdc extends AbstractJavaBeanDefinition{
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

public nc.ui.bd.uitabextend.ExinfoLoader getExinfoloader(){
 if(context.get("exinfoloader")!=null)
 return (nc.ui.bd.uitabextend.ExinfoLoader)context.get("exinfoloader");
  nc.ui.bd.uitabextend.ExinfoLoader bean = new nc.ui.bd.uitabextend.ExinfoLoader();
  context.put("exinfoloader",bean);
  bean.setCurrent_md_ID("720dcc7c-ff19-48f4-b9c5-b90906682f45");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.uitabextend.UITabExtManager getUiTabExtMnger(){
 if(context.get("uiTabExtMnger")!=null)
 return (nc.ui.bd.uitabextend.UITabExtManager)context.get("uiTabExtMnger");
  nc.ui.bd.uitabextend.UITabExtManager bean = new nc.ui.bd.uitabextend.UITabExtManager();
  context.put("uiTabExtMnger",bean);
  bean.setTargetComponent(getBaseinfoCardView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.editor.UIF2RemoteCallCombinatorCaller getRemoteCallCombinatorCaller(){
 if(context.get("remoteCallCombinatorCaller")!=null)
 return (nc.ui.uif2.editor.UIF2RemoteCallCombinatorCaller)context.get("remoteCallCombinatorCaller");
  nc.ui.uif2.editor.UIF2RemoteCallCombinatorCaller bean = new nc.ui.uif2.editor.UIF2RemoteCallCombinatorCaller();
  context.put("remoteCallCombinatorCaller",bean);
  bean.setRemoteCallers(getManagedList0());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList0(){  List list = new ArrayList();  list.add(getTemplateContainer());  list.add(getQueryTemplateContainer());  list.add(getUserdefitemContainer());  return list;}

public nc.ui.uif2.editor.TemplateContainer getTemplateContainer(){
 if(context.get("templateContainer")!=null)
 return (nc.ui.uif2.editor.TemplateContainer)context.get("templateContainer");
  nc.ui.uif2.editor.TemplateContainer bean = new nc.ui.uif2.editor.TemplateContainer();
  context.put("templateContainer",bean);
  bean.setContext(getContext());
  bean.setNodeKeies(getManagedList1());
  bean.load();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList1(){  List list = new ArrayList();  list.add("base");  list.add("share");  list.add("createCust");  list.add("associcust");  list.add("approve");  return list;}

public nc.ui.uif2.editor.QueryTemplateContainer getQueryTemplateContainer(){
 if(context.get("queryTemplateContainer")!=null)
 return (nc.ui.uif2.editor.QueryTemplateContainer)context.get("queryTemplateContainer");
  nc.ui.uif2.editor.QueryTemplateContainer bean = new nc.ui.uif2.editor.QueryTemplateContainer();
  context.put("queryTemplateContainer",bean);
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.userdefitem.UserDefItemContainer getUserdefitemContainer(){
 if(context.get("userdefitemContainer")!=null)
 return (nc.ui.uif2.userdefitem.UserDefItemContainer)context.get("userdefitemContainer");
  nc.ui.uif2.userdefitem.UserDefItemContainer bean = new nc.ui.uif2.userdefitem.UserDefItemContainer();
  context.put("userdefitemContainer",bean);
  bean.setContext(getContext());
  bean.setParams(getManagedList2());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList2(){  List list = new ArrayList();  list.add(getQueryParam_15cf5e4());  list.add(getQueryParam_1162c3e());  list.add(getQueryParam_672dc2());  list.add(getQueryParam_603c51());  return list;}

private nc.ui.uif2.userdefitem.QueryParam getQueryParam_15cf5e4(){
 if(context.get("nc.ui.uif2.userdefitem.QueryParam#15cf5e4")!=null)
 return (nc.ui.uif2.userdefitem.QueryParam)context.get("nc.ui.uif2.userdefitem.QueryParam#15cf5e4");
  nc.ui.uif2.userdefitem.QueryParam bean = new nc.ui.uif2.userdefitem.QueryParam();
  context.put("nc.ui.uif2.userdefitem.QueryParam#15cf5e4",bean);
  bean.setMdfullname("uap.supplier");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.userdefitem.QueryParam getQueryParam_1162c3e(){
 if(context.get("nc.ui.uif2.userdefitem.QueryParam#1162c3e")!=null)
 return (nc.ui.uif2.userdefitem.QueryParam)context.get("nc.ui.uif2.userdefitem.QueryParam#1162c3e");
  nc.ui.uif2.userdefitem.QueryParam bean = new nc.ui.uif2.userdefitem.QueryParam();
  context.put("nc.ui.uif2.userdefitem.QueryParam#1162c3e",bean);
  bean.setMdfullname("uap.supfinance");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.userdefitem.QueryParam getQueryParam_672dc2(){
 if(context.get("nc.ui.uif2.userdefitem.QueryParam#672dc2")!=null)
 return (nc.ui.uif2.userdefitem.QueryParam)context.get("nc.ui.uif2.userdefitem.QueryParam#672dc2");
  nc.ui.uif2.userdefitem.QueryParam bean = new nc.ui.uif2.userdefitem.QueryParam();
  context.put("nc.ui.uif2.userdefitem.QueryParam#672dc2",bean);
  bean.setMdfullname("uap.supstock");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.userdefitem.QueryParam getQueryParam_603c51(){
 if(context.get("nc.ui.uif2.userdefitem.QueryParam#603c51")!=null)
 return (nc.ui.uif2.userdefitem.QueryParam)context.get("nc.ui.uif2.userdefitem.QueryParam#603c51");
  nc.ui.uif2.userdefitem.QueryParam bean = new nc.ui.uif2.userdefitem.QueryParam();
  context.put("nc.ui.uif2.userdefitem.QueryParam#603c51",bean);
  bean.setMdfullname("uap.suplinkman");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.ActionContributors getToftpanelActionContributors(){
 if(context.get("toftpanelActionContributors")!=null)
 return (nc.ui.uif2.actions.ActionContributors)context.get("toftpanelActionContributors");
  nc.ui.uif2.actions.ActionContributors bean = new nc.ui.uif2.actions.ActionContributors();
  context.put("toftpanelActionContributors",bean);
  bean.setContributors(getManagedList3());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList3(){  List list = new ArrayList();  list.add(getBaseinfoListViewActions());  list.add(getBaseinfoCardViewActions());  return list;}

public nc.ui.bd.supplier.baseinfo.model.SupplierBaseInfoModel getBaseinfoModel(){
 if(context.get("baseinfoModel")!=null)
 return (nc.ui.bd.supplier.baseinfo.model.SupplierBaseInfoModel)context.get("baseinfoModel");
  nc.ui.bd.supplier.baseinfo.model.SupplierBaseInfoModel bean = new nc.ui.bd.supplier.baseinfo.model.SupplierBaseInfoModel();
  context.put("baseinfoModel",bean);
  bean.setBusinessObjectAdapterFactory(getBoadapterfacotry());
  bean.setService(getService());
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.supplier.baseinfo.model.SupplierAppModelService getService(){
 if(context.get("service")!=null)
 return (nc.ui.bd.supplier.baseinfo.model.SupplierAppModelService)context.get("service");
  nc.ui.bd.supplier.baseinfo.model.SupplierAppModelService bean = new nc.ui.bd.supplier.baseinfo.model.SupplierAppModelService();
  context.put("service",bean);
  bean.setContext(getContext());
  bean.setExtendContext(getBaseExtendContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.vo.bd.meta.GeneralBDObjectAdapterFactory getBoadapterfacotry(){
 if(context.get("boadapterfacotry")!=null)
 return (nc.vo.bd.meta.GeneralBDObjectAdapterFactory)context.get("boadapterfacotry");
  nc.vo.bd.meta.GeneralBDObjectAdapterFactory bean = new nc.vo.bd.meta.GeneralBDObjectAdapterFactory();
  context.put("boadapterfacotry",bean);
  bean.setMode("MD");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.supplier.baseinfo.model.SupplierBaseInfoDataManager getModelDataManager(){
 if(context.get("modelDataManager")!=null)
 return (nc.ui.bd.supplier.baseinfo.model.SupplierBaseInfoDataManager)context.get("modelDataManager");
  nc.ui.bd.supplier.baseinfo.model.SupplierBaseInfoDataManager bean = new nc.ui.bd.supplier.baseinfo.model.SupplierBaseInfoDataManager();
  context.put("modelDataManager",bean);
  bean.setManageModel(getBaseinfoModel());
  bean.setPaginationModel(getPaginationModel());
  bean.setDelegator(getPaginationDelegator());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.supplier.baseinfo.model.SupplierFuncNodeInitDataListener getInitDataListener(){
 if(context.get("InitDataListener")!=null)
 return (nc.ui.bd.supplier.baseinfo.model.SupplierFuncNodeInitDataListener)context.get("InitDataListener");
  nc.ui.bd.supplier.baseinfo.model.SupplierFuncNodeInitDataListener bean = new nc.ui.bd.supplier.baseinfo.model.SupplierFuncNodeInitDataListener();
  context.put("InitDataListener",bean);
  bean.setDataManager(getModelDataManager());
  bean.setQueryAction(getList_searchAction());
  bean.setEditor(getBaseinfoListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.components.pagination.BillManagePaginationDelegator getPaginationDelegator(){
 if(context.get("paginationDelegator")!=null)
 return (nc.ui.uif2.components.pagination.BillManagePaginationDelegator)context.get("paginationDelegator");
  nc.ui.uif2.components.pagination.BillManagePaginationDelegator bean = new nc.ui.uif2.components.pagination.BillManagePaginationDelegator();
  context.put("paginationDelegator",bean);
  bean.setPaginationModel(getPaginationModel());
  bean.setBillModel(getBaseinfoModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.TangramContainer getContainer(){
 if(context.get("container")!=null)
 return (nc.ui.uif2.TangramContainer)context.get("container");
  nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
  context.put("container",bean);
  bean.setTangramLayoutRoot(getTBNode_376c75());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_376c75(){
 if(context.get("nc.ui.uif2.tangramlayout.node.TBNode#376c75")!=null)
 return (nc.ui.uif2.tangramlayout.node.TBNode)context.get("nc.ui.uif2.tangramlayout.node.TBNode#376c75");
  nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
  context.put("nc.ui.uif2.tangramlayout.node.TBNode#376c75",bean);
  bean.setTabs(getManagedList4());
  bean.setShowMode("CardLayout");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList4(){  List list = new ArrayList();  list.add(getHSNode_1b891bd());  list.add(getVSNode_22961b());  return list;}

private nc.ui.uif2.tangramlayout.node.HSNode getHSNode_1b891bd(){
 if(context.get("nc.ui.uif2.tangramlayout.node.HSNode#1b891bd")!=null)
 return (nc.ui.uif2.tangramlayout.node.HSNode)context.get("nc.ui.uif2.tangramlayout.node.HSNode#1b891bd");
  nc.ui.uif2.tangramlayout.node.HSNode bean = new nc.ui.uif2.tangramlayout.node.HSNode();
  context.put("nc.ui.uif2.tangramlayout.node.HSNode#1b891bd",bean);
  bean.setLeft(getCNode_f30c6c());
  bean.setRight(getCNode_92bd90());
  bean.setDividerLocation(0.2f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_f30c6c(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#f30c6c")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#f30c6c");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#f30c6c",bean);
  bean.setComponent(getQueryAreaShell());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_92bd90(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#92bd90")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#92bd90");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#92bd90",bean);
  bean.setComponent(getBaseinfoListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_22961b(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#22961b")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#22961b");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#22961b",bean);
  bean.setUp(getCNode_8bbbef());
  bean.setDown(getVSNode_1895bb6());
  bean.setShowMode("NoDivider");
  bean.setDividerLocation(30f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_8bbbef(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#8bbbef")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#8bbbef");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#8bbbef",bean);
  bean.setComponent(getCardInfoPnl());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_1895bb6(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#1895bb6")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#1895bb6");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#1895bb6",bean);
  bean.setUp(getCNode_534d7e());
  bean.setDown(getTBNode_137d293());
  bean.setDividerLocation(30f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_534d7e(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#534d7e")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#534d7e");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#534d7e",bean);
  bean.setComponent(getSharepanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_137d293(){
 if(context.get("nc.ui.uif2.tangramlayout.node.TBNode#137d293")!=null)
 return (nc.ui.uif2.tangramlayout.node.TBNode)context.get("nc.ui.uif2.tangramlayout.node.TBNode#137d293");
  nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
  context.put("nc.ui.uif2.tangramlayout.node.TBNode#137d293",bean);
  bean.setTabs(getManagedList5());
  bean.setTabbedPaneFetcher(getTabbedPaneFetcher());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList5(){  List list = new ArrayList();  list.add(getCNode_6a4872());  return list;}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_6a4872(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#6a4872")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#6a4872");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#6a4872",bean);
  bean.setName(getI18nFB_17e0d59());
  bean.setComponent(getBaseinfoCardView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_17e0d59(){
 if(context.get("nc.ui.uif2.I18nFB#17e0d59")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#17e0d59");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#17e0d59",bean);  bean.setResDir("10140sub");
  bean.setDefaultValue("基本信息");
  bean.setResId("010140sub0062");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#17e0d59",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

public nc.ui.bd.pub.DefaultTabbedPaneEditableControl getTabbedPaneFetcher(){
 if(context.get("tabbedPaneFetcher")!=null)
 return (nc.ui.bd.pub.DefaultTabbedPaneEditableControl)context.get("tabbedPaneFetcher");
  nc.ui.bd.pub.DefaultTabbedPaneEditableControl bean = new nc.ui.bd.pub.DefaultTabbedPaneEditableControl();
  context.put("tabbedPaneFetcher",bean);
  bean.setModel(getBaseinfoModel());
  bean.setTargetComponent(getBaseinfoCardView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.attach.AttachAction getAccessoryShowAction(){
 if(context.get("accessoryShowAction")!=null)
 return (nc.ui.bd.attach.AttachAction)context.get("accessoryShowAction");
  nc.ui.bd.attach.AttachAction bean = new nc.ui.bd.attach.AttachAction();
  context.put("accessoryShowAction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setPrefix("uapbd/720dcc7c-ff19-48f4-b9c5-b90906682f45");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel getCardInfoPnl(){
 if(context.get("cardInfoPnl")!=null)
 return (nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel)context.get("cardInfoPnl");
  nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel bean = new nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel();
  context.put("cardInfoPnl",bean);
  bean.setActions(getManagedList6());
  bean.setTitleAction(getReturnaction());
  bean.setModel(getBaseinfoModel());
  bean.setDataTemplateAction(getDataTempletAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList6(){  List list = new ArrayList();  list.add(getAccessoryShowAction());  list.add(getActionsBarSeparator());  list.add(getFirstLineAction());  list.add(getPreLineAction());  list.add(getNextLineAction());  list.add(getLastLineAction());  return list;}

private nc.ui.pub.beans.ActionsBar.ActionsBarSeparator getActionsBarSeparator(){
 if(context.get("actionsBarSeparator")!=null)
 return (nc.ui.pub.beans.ActionsBar.ActionsBarSeparator)context.get("actionsBarSeparator");
  nc.ui.pub.beans.ActionsBar.ActionsBarSeparator bean = new nc.ui.pub.beans.ActionsBar.ActionsBarSeparator();
  context.put("actionsBarSeparator",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.actions.FirstLineAction getFirstLineAction(){
 if(context.get("firstLineAction")!=null)
 return (nc.ui.uif2.actions.FirstLineAction)context.get("firstLineAction");
  nc.ui.uif2.actions.FirstLineAction bean = new nc.ui.uif2.actions.FirstLineAction();
  context.put("firstLineAction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setExceptionHandler(getExceptionhandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.actions.PreLineAction getPreLineAction(){
 if(context.get("preLineAction")!=null)
 return (nc.ui.uif2.actions.PreLineAction)context.get("preLineAction");
  nc.ui.uif2.actions.PreLineAction bean = new nc.ui.uif2.actions.PreLineAction();
  context.put("preLineAction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setExceptionHandler(getExceptionhandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.actions.NextLineAction getNextLineAction(){
 if(context.get("nextLineAction")!=null)
 return (nc.ui.uif2.actions.NextLineAction)context.get("nextLineAction");
  nc.ui.uif2.actions.NextLineAction bean = new nc.ui.uif2.actions.NextLineAction();
  context.put("nextLineAction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setExceptionHandler(getExceptionhandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.actions.LastLineAction getLastLineAction(){
 if(context.get("lastLineAction")!=null)
 return (nc.ui.uif2.actions.LastLineAction)context.get("lastLineAction");
  nc.ui.uif2.actions.LastLineAction bean = new nc.ui.uif2.actions.LastLineAction();
  context.put("lastLineAction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setExceptionHandler(getExceptionhandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.bd.attach.UpdateAccAction getReturnaction(){
 if(context.get("returnaction")!=null)
 return (nc.ui.bd.attach.UpdateAccAction)context.get("returnaction");
  nc.ui.bd.attach.UpdateAccAction bean = new nc.ui.bd.attach.UpdateAccAction();
  context.put("returnaction",bean);
  bean.setGoComponent(getBaseinfoListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pub.datatemplet.action.DataTempletAction getDataTempletAction(){
 if(context.get("dataTempletAction")!=null)
 return (nc.ui.pub.datatemplet.action.DataTempletAction)context.get("dataTempletAction");
  nc.ui.pub.datatemplet.action.DataTempletAction bean = new nc.ui.pub.datatemplet.action.DataTempletAction();
  context.put("dataTempletAction",bean);
  bean.setTemplet(getDataTemplet());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pub.datatemplet.DataTemplet getDataTemplet(){
 if(context.get("dataTemplet")!=null)
 return (nc.ui.pub.datatemplet.DataTemplet)context.get("dataTemplet");
  nc.ui.pub.datatemplet.DataTemplet bean = new nc.ui.pub.datatemplet.DataTemplet();
  context.put("dataTemplet",bean);
  bean.setContext(getContext());
  bean.setConvert(getDataTempletConvert());
  bean.setValidation(getDataTempletValidation());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pub.datatemplet.DataTempletConvert getDataTempletConvert(){
 if(context.get("dataTempletConvert")!=null)
 return (nc.ui.pub.datatemplet.DataTempletConvert)context.get("dataTempletConvert");
  nc.ui.pub.datatemplet.DataTempletConvert bean = new nc.ui.pub.datatemplet.DataTempletConvert();
  context.put("dataTempletConvert",bean);
  bean.setEditor(getBaseinfoCardView());
  bean.setSetValueStrategy("one_to_many");
  bean.setBeforeImportHandlers(getManagedList7());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList7(){  List list = new ArrayList();  list.add(getBeforeImportHandler());  return list;}

public nc.ui.bd.pub.datatemplet.SharePanelBeforeImport getBeforeImportHandler(){
 if(context.get("beforeImportHandler")!=null)
 return (nc.ui.bd.pub.datatemplet.SharePanelBeforeImport)context.get("beforeImportHandler");
  nc.ui.bd.pub.datatemplet.SharePanelBeforeImport bean = new nc.ui.bd.pub.datatemplet.SharePanelBeforeImport();
  context.put("beforeImportHandler",bean);
  bean.setEditor(getBaseinfoCardView());
  bean.setImportModel(getBaseinfoModel());
  bean.setContext(getContext());
  bean.setSharePanel(getSharepanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pub.datatemplet.DataTempletValidation getDataTempletValidation(){
 if(context.get("dataTempletValidation")!=null)
 return (nc.ui.pub.datatemplet.DataTempletValidation)context.get("dataTempletValidation");
  nc.ui.pub.datatemplet.DataTempletValidation bean = new nc.ui.pub.datatemplet.DataTempletValidation();
  context.put("dataTempletValidation",bean);
  bean.setEditor(getBaseinfoCardView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.QueryAreaShell getQueryAreaShell(){
 if(context.get("queryAreaShell")!=null)
 return (nc.ui.uif2.actions.QueryAreaShell)context.get("queryAreaShell");
  nc.ui.uif2.actions.QueryAreaShell bean = new nc.ui.uif2.actions.QueryAreaShell();
  context.put("queryAreaShell",bean);
  bean.setQueryArea(getBdqueryActionBaseMediator_created_14d89ea());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.queryarea.QueryArea getBdqueryActionBaseMediator_created_14d89ea(){
 if(context.get("bdqueryActionBaseMediator.created#14d89ea")!=null)
 return (nc.ui.queryarea.QueryArea)context.get("bdqueryActionBaseMediator.created#14d89ea");
  nc.ui.queryarea.QueryArea bean = getBdqueryActionBaseMediator().createQueryArea();
  context.put("bdqueryActionBaseMediator.created#14d89ea",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.supplier.baseinfo.view.SupplierShareInfoView getSharepanel(){
 if(context.get("sharepanel")!=null)
 return (nc.ui.bd.supplier.baseinfo.view.SupplierShareInfoView)context.get("sharepanel");
  nc.ui.bd.supplier.baseinfo.view.SupplierShareInfoView bean = new nc.ui.bd.supplier.baseinfo.view.SupplierShareInfoView();
  context.put("sharepanel",bean);
  bean.setTemplateContainer(getTemplateContainer());
  bean.setNodekey("share");
  bean.setModel(getBaseinfoModel());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.components.pagination.PaginationBar getPaginationBar(){
 if(context.get("paginationBar")!=null)
 return (nc.ui.uif2.components.pagination.PaginationBar)context.get("paginationBar");
  nc.ui.uif2.components.pagination.PaginationBar bean = new nc.ui.uif2.components.pagination.PaginationBar();
  context.put("paginationBar",bean);
  bean.setPaginationModel(getPaginationModel());
  bean.setContext(getContext());
  bean.registeCallbak();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.components.pagination.PaginationModel getPaginationModel(){
 if(context.get("paginationModel")!=null)
 return (nc.ui.uif2.components.pagination.PaginationModel)context.get("paginationModel");
  nc.ui.uif2.components.pagination.PaginationModel bean = new nc.ui.uif2.components.pagination.PaginationModel();
  context.put("paginationModel",bean);
  bean.setPaginationQueryService(getService());
  bean.init();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.supplier.baseinfo.view.SupplierBaseInfoListView getBaseinfoListView(){
 if(context.get("baseinfoListView")!=null)
 return (nc.ui.bd.supplier.baseinfo.view.SupplierBaseInfoListView)context.get("baseinfoListView");
  nc.ui.bd.supplier.baseinfo.view.SupplierBaseInfoListView bean = new nc.ui.bd.supplier.baseinfo.view.SupplierBaseInfoListView();
  context.put("baseinfoListView",bean);
  bean.setModel(getBaseinfoModel());
  bean.setTemplateContainer(getTemplateContainer());
  bean.setPos("head");
  bean.setNodekey("base");
  bean.setMultiSelectionEnable(true);
  bean.setUserdefitemListPreparator(getUserdefitemContainerListPreparator_b221de());
  bean.setSouth(getPaginationBar());
  bean.setNorth(getListInfoPnl());
  bean.setBccAction(getBatchcreateCustAction());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.editor.UserdefitemContainerListPreparator getUserdefitemContainerListPreparator_b221de(){
 if(context.get("nc.ui.uif2.editor.UserdefitemContainerListPreparator#b221de")!=null)
 return (nc.ui.uif2.editor.UserdefitemContainerListPreparator)context.get("nc.ui.uif2.editor.UserdefitemContainerListPreparator#b221de");
  nc.ui.uif2.editor.UserdefitemContainerListPreparator bean = new nc.ui.uif2.editor.UserdefitemContainerListPreparator();
  context.put("nc.ui.uif2.editor.UserdefitemContainerListPreparator#b221de",bean);
  bean.setContainer(getUserdefitemContainer());
  bean.setParams(getManagedList8());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList8(){  List list = new ArrayList();  list.add(getListUserdefitemQueryParam());  return list;}

private nc.ui.uif2.editor.UserdefQueryParam getListUserdefitemQueryParam(){
 if(context.get("listUserdefitemQueryParam")!=null)
 return (nc.ui.uif2.editor.UserdefQueryParam)context.get("listUserdefitemQueryParam");
  nc.ui.uif2.editor.UserdefQueryParam bean = new nc.ui.uif2.editor.UserdefQueryParam();
  context.put("listUserdefitemQueryParam",bean);
  bean.setMdfullname("uap.supplier");
  bean.setPos(0);
  bean.setPrefix("def");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel getListInfoPnl(){
 if(context.get("listInfoPnl")!=null)
 return (nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel)context.get("listInfoPnl");
  nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel bean = new nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel();
  context.put("listInfoPnl",bean);
  bean.setModel(getBaseinfoModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.supplier.baseinfo.view.SupplierBaseInfoCardView getBaseinfoCardView(){
 if(context.get("baseinfoCardView")!=null)
 return (nc.ui.bd.supplier.baseinfo.view.SupplierBaseInfoCardView)context.get("baseinfoCardView");
  nc.ui.bd.supplier.baseinfo.view.SupplierBaseInfoCardView bean = new nc.ui.bd.supplier.baseinfo.view.SupplierBaseInfoCardView();
  context.put("baseinfoCardView",bean);
  bean.setTemplateContainer(getTemplateContainer());
  bean.setModel(getBaseinfoModel());
  bean.setNodekey("base");
  bean.setUserdefitemPreparator(getUserdefitemContainerPreparator_6aff9d());
  bean.setExceptionHandler(getExceptionhandler());
  bean.setShareInfoView(getSharepanel());
  bean.setClosingListener(getClosingListener());
  bean.setBodyActionMap(getManagedMap0());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.editor.UserdefitemContainerPreparator getUserdefitemContainerPreparator_6aff9d(){
 if(context.get("nc.ui.uif2.editor.UserdefitemContainerPreparator#6aff9d")!=null)
 return (nc.ui.uif2.editor.UserdefitemContainerPreparator)context.get("nc.ui.uif2.editor.UserdefitemContainerPreparator#6aff9d");
  nc.ui.uif2.editor.UserdefitemContainerPreparator bean = new nc.ui.uif2.editor.UserdefitemContainerPreparator();
  context.put("nc.ui.uif2.editor.UserdefitemContainerPreparator#6aff9d",bean);
  bean.setContainer(getUserdefitemContainer());
  bean.setParams(getManagedList9());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList9(){  List list = new ArrayList();  list.add(getCardUserdefitemQueryParam());  list.add(getCardBodyUserdefitemQueryParam());  return list;}

private nc.ui.uif2.editor.UserdefQueryParam getCardUserdefitemQueryParam(){
 if(context.get("cardUserdefitemQueryParam")!=null)
 return (nc.ui.uif2.editor.UserdefQueryParam)context.get("cardUserdefitemQueryParam");
  nc.ui.uif2.editor.UserdefQueryParam bean = new nc.ui.uif2.editor.UserdefQueryParam();
  context.put("cardUserdefitemQueryParam",bean);
  bean.setMdfullname("uap.supplier");
  bean.setPos(0);
  bean.setPrefix("def");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.editor.UserdefQueryParam getCardBodyUserdefitemQueryParam(){
 if(context.get("cardBodyUserdefitemQueryParam")!=null)
 return (nc.ui.uif2.editor.UserdefQueryParam)context.get("cardBodyUserdefitemQueryParam");
  nc.ui.uif2.editor.UserdefQueryParam bean = new nc.ui.uif2.editor.UserdefQueryParam();
  context.put("cardBodyUserdefitemQueryParam",bean);
  bean.setMdfullname("uap.suplinkman");
  bean.setPos(1);
  bean.setPrefix("def");
  bean.setTabcode("suplinkman");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private Map getManagedMap0(){  Map map = new HashMap();  map.put("suplinkman",getManagedList10());  map.put("supcountrytaxes",getManagedList11());  map.put("supvat",getManagedList12());  return map;}

private List getManagedList10(){  List list = new ArrayList();  list.add(getAddlineAction());  list.add(getInserlineAction());  list.add(getDellineAction());  return list;}

private List getManagedList11(){  List list = new ArrayList();  list.add(getAddLineAction_1e76461());  list.add(getInsertLineAction_32af2e());  list.add(getDelLineAction_10fc8a7());  return list;}

private nc.ui.uif2.actions.AddLineAction getAddLineAction_1e76461(){
 if(context.get("nc.ui.uif2.actions.AddLineAction#1e76461")!=null)
 return (nc.ui.uif2.actions.AddLineAction)context.get("nc.ui.uif2.actions.AddLineAction#1e76461");
  nc.ui.uif2.actions.AddLineAction bean = new nc.ui.uif2.actions.AddLineAction();
  context.put("nc.ui.uif2.actions.AddLineAction#1e76461",bean);
  bean.setModel(getBaseinfoModel());
  bean.setCardpanel(getBaseinfoCardView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.actions.InsertLineAction getInsertLineAction_32af2e(){
 if(context.get("nc.ui.uif2.actions.InsertLineAction#32af2e")!=null)
 return (nc.ui.uif2.actions.InsertLineAction)context.get("nc.ui.uif2.actions.InsertLineAction#32af2e");
  nc.ui.uif2.actions.InsertLineAction bean = new nc.ui.uif2.actions.InsertLineAction();
  context.put("nc.ui.uif2.actions.InsertLineAction#32af2e",bean);
  bean.setModel(getBaseinfoModel());
  bean.setCardpanel(getBaseinfoCardView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.actions.DelLineAction getDelLineAction_10fc8a7(){
 if(context.get("nc.ui.uif2.actions.DelLineAction#10fc8a7")!=null)
 return (nc.ui.uif2.actions.DelLineAction)context.get("nc.ui.uif2.actions.DelLineAction#10fc8a7");
  nc.ui.uif2.actions.DelLineAction bean = new nc.ui.uif2.actions.DelLineAction();
  context.put("nc.ui.uif2.actions.DelLineAction#10fc8a7",bean);
  bean.setModel(getBaseinfoModel());
  bean.setCardpanel(getBaseinfoCardView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList12(){  List list = new ArrayList();  list.add(getAddLineAction_94538c());  list.add(getInsertLineAction_e8e093());  list.add(getDelLineAction_162787f());  return list;}

private nc.ui.uif2.actions.AddLineAction getAddLineAction_94538c(){
 if(context.get("nc.ui.uif2.actions.AddLineAction#94538c")!=null)
 return (nc.ui.uif2.actions.AddLineAction)context.get("nc.ui.uif2.actions.AddLineAction#94538c");
  nc.ui.uif2.actions.AddLineAction bean = new nc.ui.uif2.actions.AddLineAction();
  context.put("nc.ui.uif2.actions.AddLineAction#94538c",bean);
  bean.setModel(getBaseinfoModel());
  bean.setCardpanel(getBaseinfoCardView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.actions.InsertLineAction getInsertLineAction_e8e093(){
 if(context.get("nc.ui.uif2.actions.InsertLineAction#e8e093")!=null)
 return (nc.ui.uif2.actions.InsertLineAction)context.get("nc.ui.uif2.actions.InsertLineAction#e8e093");
  nc.ui.uif2.actions.InsertLineAction bean = new nc.ui.uif2.actions.InsertLineAction();
  context.put("nc.ui.uif2.actions.InsertLineAction#e8e093",bean);
  bean.setModel(getBaseinfoModel());
  bean.setCardpanel(getBaseinfoCardView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.actions.DelLineAction getDelLineAction_162787f(){
 if(context.get("nc.ui.uif2.actions.DelLineAction#162787f")!=null)
 return (nc.ui.uif2.actions.DelLineAction)context.get("nc.ui.uif2.actions.DelLineAction#162787f");
  nc.ui.uif2.actions.DelLineAction bean = new nc.ui.uif2.actions.DelLineAction();
  context.put("nc.ui.uif2.actions.DelLineAction#162787f",bean);
  bean.setModel(getBaseinfoModel());
  bean.setCardpanel(getBaseinfoCardView());
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

public nc.ui.bd.supplier.baseinfo.action.SupBaseInfoAddAction getList_baseinfoaddaction(){
 if(context.get("list_baseinfoaddaction")!=null)
 return (nc.ui.bd.supplier.baseinfo.action.SupBaseInfoAddAction)context.get("list_baseinfoaddaction");
  nc.ui.bd.supplier.baseinfo.action.SupBaseInfoAddAction bean = new nc.ui.bd.supplier.baseinfo.action.SupBaseInfoAddAction();
  context.put("list_baseinfoaddaction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setDataTempletAction(getDataTempletAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.supplier.baseinfo.action.SupBaseInfoCopyAddAction getList_copyaddaction(){
 if(context.get("list_copyaddaction")!=null)
 return (nc.ui.bd.supplier.baseinfo.action.SupBaseInfoCopyAddAction)context.get("list_copyaddaction");
  nc.ui.bd.supplier.baseinfo.action.SupBaseInfoCopyAddAction bean = new nc.ui.bd.supplier.baseinfo.action.SupBaseInfoCopyAddAction();
  context.put("list_copyaddaction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setEditor(getBaseinfoCardView());
  bean.setShareEditor(getSharepanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.MenuAction getFilterAction(){
 if(context.get("filterAction")!=null)
 return (nc.funcnode.ui.action.MenuAction)context.get("filterAction");
  nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
  context.put("filterAction",bean);
  bean.setCode("filter");
  bean.setName(getI18nFB_83d604());
  bean.setActions(getManagedList13());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_83d604(){
 if(context.get("nc.ui.uif2.I18nFB#83d604")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#83d604");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#83d604",bean);  bean.setResDir("common");
  bean.setDefaultValue("过滤");
  bean.setResId("UCH069");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#83d604",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList13(){  List list = new ArrayList();  list.add(getShowDisableAction());  return list;}

public nc.ui.uif2.actions.ShowDisableDataAction getShowDisableAction(){
 if(context.get("showDisableAction")!=null)
 return (nc.ui.uif2.actions.ShowDisableDataAction)context.get("showDisableAction");
  nc.ui.uif2.actions.ShowDisableDataAction bean = new nc.ui.uif2.actions.ShowDisableDataAction();
  context.put("showDisableAction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.cust.baseinfo.action.CustEditAction getList_baseinfoeditaction(){
 if(context.get("list_baseinfoeditaction")!=null)
 return (nc.ui.bd.cust.baseinfo.action.CustEditAction)context.get("list_baseinfoeditaction");
  nc.ui.bd.cust.baseinfo.action.CustEditAction bean = new nc.ui.bd.cust.baseinfo.action.CustEditAction();
  context.put("list_baseinfoeditaction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setResourceCode("supplier");
  bean.setMdOperateCode("edit");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.pub.actions.BDAnsyDelAction getList_baseinfodeleteaction(){
 if(context.get("list_baseinfodeleteaction")!=null)
 return (nc.ui.bd.pub.actions.BDAnsyDelAction)context.get("list_baseinfodeleteaction");
  nc.ui.bd.pub.actions.BDAnsyDelAction bean = new nc.ui.bd.pub.actions.BDAnsyDelAction();
  context.put("list_baseinfodeleteaction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setResourceCode("supplier");
  bean.setMdOperateCode("delete");
  bean.setService(getService());
  bean.setRefreshaction(getList_refreshAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.QueryAction getList_searchAction(){
 if(context.get("list_searchAction")!=null)
 return (nc.ui.uif2.actions.QueryAction)context.get("list_searchAction");
  nc.ui.uif2.actions.QueryAction bean = new nc.ui.uif2.actions.QueryAction();
  context.put("list_searchAction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setDataManager(getModelDataManager());
  bean.setQueryDelegator(getAutoShowDisableDataQueryDelagator_f1eceb());
  bean.setTemplateContainer(getQueryTemplateContainer());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.bd.pub.query.AutoShowDisableDataQueryDelagator getAutoShowDisableDataQueryDelagator_f1eceb(){
 if(context.get("nc.ui.bd.pub.query.AutoShowDisableDataQueryDelagator#f1eceb")!=null)
 return (nc.ui.bd.pub.query.AutoShowDisableDataQueryDelagator)context.get("nc.ui.bd.pub.query.AutoShowDisableDataQueryDelagator#f1eceb");
  nc.ui.bd.pub.query.AutoShowDisableDataQueryDelagator bean = new nc.ui.bd.pub.query.AutoShowDisableDataQueryDelagator();
  context.put("nc.ui.bd.pub.query.AutoShowDisableDataQueryDelagator#f1eceb",bean);
  bean.setContext(getContext());
  bean.setAction(getShowDisableAction());
  bean.setDatamanager(getModelDataManager());
  bean.setTemplateContainer(getQueryTemplateContainer());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.RefreshAction getList_refreshAction(){
 if(context.get("list_refreshAction")!=null)
 return (nc.ui.uif2.actions.RefreshAction)context.get("list_refreshAction");
  nc.ui.uif2.actions.RefreshAction bean = new nc.ui.uif2.actions.RefreshAction();
  context.put("list_refreshAction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.supplier.baseinfo.action.SupBaseInfoCardRefreshAction getCard_refreshAction(){
 if(context.get("card_refreshAction")!=null)
 return (nc.ui.bd.supplier.baseinfo.action.SupBaseInfoCardRefreshAction)context.get("card_refreshAction");
  nc.ui.bd.supplier.baseinfo.action.SupBaseInfoCardRefreshAction bean = new nc.ui.bd.supplier.baseinfo.action.SupBaseInfoCardRefreshAction();
  context.put("card_refreshAction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setService(getService());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.GroupAction getList_approveActionGroup(){
 if(context.get("list_approveActionGroup")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("list_approveActionGroup");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("list_approveActionGroup",bean);
  bean.setActions(getManagedList14());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList14(){  List list = new ArrayList();  list.add(getList_approveAction());  list.add(getList_disapproveAction());  return list;}

public nc.ui.bd.supplier.baseinfo.action.SupBatchApproveAction getList_approveAction(){
 if(context.get("list_approveAction")!=null)
 return (nc.ui.bd.supplier.baseinfo.action.SupBatchApproveAction)context.get("list_approveAction");
  nc.ui.bd.supplier.baseinfo.action.SupBatchApproveAction bean = new nc.ui.bd.supplier.baseinfo.action.SupBatchApproveAction();
  context.put("list_approveAction",bean);
  bean.setModel(getBaseinfoModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.supplier.baseinfo.action.SupBatchUnApproveAction getList_disapproveAction(){
 if(context.get("list_disapproveAction")!=null)
 return (nc.ui.bd.supplier.baseinfo.action.SupBatchUnApproveAction)context.get("list_disapproveAction");
  nc.ui.bd.supplier.baseinfo.action.SupBatchUnApproveAction bean = new nc.ui.bd.supplier.baseinfo.action.SupBatchUnApproveAction();
  context.put("list_disapproveAction",bean);
  bean.setModel(getBaseinfoModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.GroupAction getList_entireFreezeActionGroup(){
 if(context.get("list_entireFreezeActionGroup")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("list_entireFreezeActionGroup");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("list_entireFreezeActionGroup",bean);
  bean.setActions(getManagedList15());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList15(){  List list = new ArrayList();  list.add(getFreezeAction());  list.add(getUnFreezeAction());  return list;}

public nc.funcnode.ui.action.GroupAction getAssignActionGroup(){
 if(context.get("assignActionGroup")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("assignActionGroup");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("assignActionGroup",bean);
  bean.setActions(getManagedList16());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList16(){  List list = new ArrayList();  list.add(getAssignAction());  list.add(getCancelAssignAction());  list.add(getAssignWizardAction());  list.add(getAssignStatusAction());  return list;}

public nc.ui.bd.cust.baseinfo.action.CustAssignAction getAssignAction(){
 if(context.get("AssignAction")!=null)
 return (nc.ui.bd.cust.baseinfo.action.CustAssignAction)context.get("AssignAction");
  nc.ui.bd.cust.baseinfo.action.CustAssignAction bean = new nc.ui.bd.cust.baseinfo.action.CustAssignAction();
  context.put("AssignAction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setAssignContext(getAssignContext());
  bean.setInterceptor(getManageModeActionInterceptor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.cust.baseinfo.action.CustCancelAssignAction getCancelAssignAction(){
 if(context.get("CancelAssignAction")!=null)
 return (nc.ui.bd.cust.baseinfo.action.CustCancelAssignAction)context.get("CancelAssignAction");
  nc.ui.bd.cust.baseinfo.action.CustCancelAssignAction bean = new nc.ui.bd.cust.baseinfo.action.CustCancelAssignAction();
  context.put("CancelAssignAction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setAssignContext(getAssignContext());
  bean.setInterceptor(getManageModeActionInterceptor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.cust.baseinfo.action.CustAssignWizardAction getAssignWizardAction(){
 if(context.get("AssignWizardAction")!=null)
 return (nc.ui.bd.cust.baseinfo.action.CustAssignWizardAction)context.get("AssignWizardAction");
  nc.ui.bd.cust.baseinfo.action.CustAssignWizardAction bean = new nc.ui.bd.cust.baseinfo.action.CustAssignWizardAction();
  context.put("AssignWizardAction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setAssignContext(getAssignContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.pub.actions.BDAssignStatusAction getAssignStatusAction(){
 if(context.get("AssignStatusAction")!=null)
 return (nc.ui.bd.pub.actions.BDAssignStatusAction)context.get("AssignStatusAction");
  nc.ui.bd.pub.actions.BDAssignStatusAction bean = new nc.ui.bd.pub.actions.BDAssignStatusAction();
  context.put("AssignStatusAction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setQueryService(getAssignService());
  bean.setFunnode("10140SASTAT");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.pub.assign.AssignContext getAssignContext(){
 if(context.get("assignContext")!=null)
 return (nc.ui.bd.pub.assign.AssignContext)context.get("assignContext");
  nc.ui.bd.pub.assign.AssignContext bean = new nc.ui.bd.pub.assign.AssignContext();
  context.put("assignContext",bean);
  bean.setAssignService(getAssignService());
  bean.setBillTempNodekey("assign");
  bean.setBillTemplatePkItemkey("pk_supplier");
  bean.setLogincontext(getContext());
  bean.setOrgTypeIDs(getManagedList17());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.bd.supplier.baseinfo.assign.SupplierAssignService getAssignService(){
 if(context.get("assignService")!=null)
 return (nc.ui.bd.supplier.baseinfo.assign.SupplierAssignService)context.get("assignService");
  nc.ui.bd.supplier.baseinfo.assign.SupplierAssignService bean = new nc.ui.bd.supplier.baseinfo.assign.SupplierAssignService();
  context.put("assignService",bean);
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList17(){  List list = new ArrayList();  list.add("BUSINESSUNIT00000000");  return list;}

public nc.funcnode.ui.action.GroupAction getBatchupdategroupaction(){
 if(context.get("batchupdategroupaction")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("batchupdategroupaction");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("batchupdategroupaction",bean);
  bean.setCode("batchupdate");
  bean.setActions(getManagedList18());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList18(){  List list = new ArrayList();  list.add(getBatchUpdateAction());  list.add(getBatchUpdateWizardAction());  return list;}

public nc.ui.bd.pub.actions.BDBatchUpdateWizardAction getBatchUpdateWizardAction(){
 if(context.get("batchUpdateWizardAction")!=null)
 return (nc.ui.bd.pub.actions.BDBatchUpdateWizardAction)context.get("batchUpdateWizardAction");
  nc.ui.bd.pub.actions.BDBatchUpdateWizardAction bean = new nc.ui.bd.pub.actions.BDBatchUpdateWizardAction();
  context.put("batchUpdateWizardAction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setMdId("720dcc7c-ff19-48f4-b9c5-b90906682f45");
  bean.setQryTempNodeKey("");
  bean.setBillTempNodekey("assign");
  bean.setBillTemplatePkField("pk_supplier");
  bean.setQryService(getService());
  bean.setTabs(getManagedList19());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList19(){  List list = new ArrayList();  list.add(getSupBaseInfoBatchUpdateTab_13a761e());  list.add(getSupFinanceBatchUpdateTab_1295d06());  list.add(getSupStockBatchUpdateTab_10e8451());  return list;}

private nc.ui.bd.supplier.config.SupBaseInfoBatchUpdateTab getSupBaseInfoBatchUpdateTab_13a761e(){
 if(context.get("nc.ui.bd.supplier.config.SupBaseInfoBatchUpdateTab#13a761e")!=null)
 return (nc.ui.bd.supplier.config.SupBaseInfoBatchUpdateTab)context.get("nc.ui.bd.supplier.config.SupBaseInfoBatchUpdateTab#13a761e");
  nc.ui.bd.supplier.config.SupBaseInfoBatchUpdateTab bean = new nc.ui.bd.supplier.config.SupBaseInfoBatchUpdateTab();
  context.put("nc.ui.bd.supplier.config.SupBaseInfoBatchUpdateTab#13a761e",bean);
  bean.setSupBaseInfoService(getService());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.bd.supplier.finance.SupFinanceBatchUpdateTab getSupFinanceBatchUpdateTab_1295d06(){
 if(context.get("nc.ui.bd.supplier.finance.SupFinanceBatchUpdateTab#1295d06")!=null)
 return (nc.ui.bd.supplier.finance.SupFinanceBatchUpdateTab)context.get("nc.ui.bd.supplier.finance.SupFinanceBatchUpdateTab#1295d06");
  nc.ui.bd.supplier.finance.SupFinanceBatchUpdateTab bean = new nc.ui.bd.supplier.finance.SupFinanceBatchUpdateTab();
  context.put("nc.ui.bd.supplier.finance.SupFinanceBatchUpdateTab#1295d06",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.bd.supplier.stock.SupStockBatchUpdateTab getSupStockBatchUpdateTab_10e8451(){
 if(context.get("nc.ui.bd.supplier.stock.SupStockBatchUpdateTab#10e8451")!=null)
 return (nc.ui.bd.supplier.stock.SupStockBatchUpdateTab)context.get("nc.ui.bd.supplier.stock.SupStockBatchUpdateTab#10e8451");
  nc.ui.bd.supplier.stock.SupStockBatchUpdateTab bean = new nc.ui.bd.supplier.stock.SupStockBatchUpdateTab();
  context.put("nc.ui.bd.supplier.stock.SupStockBatchUpdateTab#10e8451",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.cust.baseinfo.action.CustBatchUpdateAction getBatchUpdateAction(){
 if(context.get("batchUpdateAction")!=null)
 return (nc.ui.bd.cust.baseinfo.action.CustBatchUpdateAction)context.get("batchUpdateAction");
  nc.ui.bd.cust.baseinfo.action.CustBatchUpdateAction bean = new nc.ui.bd.cust.baseinfo.action.CustBatchUpdateAction();
  context.put("batchUpdateAction",bean);
  bean.setWizardAction(getBatchUpdateWizardAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.GroupAction getList_enablegroupaction(){
 if(context.get("list_enablegroupaction")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("list_enablegroupaction");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("list_enablegroupaction",bean);
  bean.setActions(getManagedList20());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList20(){  List list = new ArrayList();  list.add(getBaseinfoenableaction());  list.add(getBaseinfodisableaction());  return list;}

public nc.ui.bd.supplier.baseinfo.action.SupBaseInfoBatchDisableAction getBaseinfodisableaction(){
 if(context.get("baseinfodisableaction")!=null)
 return (nc.ui.bd.supplier.baseinfo.action.SupBaseInfoBatchDisableAction)context.get("baseinfodisableaction");
  nc.ui.bd.supplier.baseinfo.action.SupBaseInfoBatchDisableAction bean = new nc.ui.bd.supplier.baseinfo.action.SupBaseInfoBatchDisableAction();
  context.put("baseinfodisableaction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setResourceCode("supplier");
  bean.setMdOperateCode("disable");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.supplier.baseinfo.action.SupBaseInfoBatchEnableAction getBaseinfoenableaction(){
 if(context.get("baseinfoenableaction")!=null)
 return (nc.ui.bd.supplier.baseinfo.action.SupBaseInfoBatchEnableAction)context.get("baseinfoenableaction");
  nc.ui.bd.supplier.baseinfo.action.SupBaseInfoBatchEnableAction bean = new nc.ui.bd.supplier.baseinfo.action.SupBaseInfoBatchEnableAction();
  context.put("baseinfoenableaction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setResourceCode("supplier");
  bean.setMdOperateCode("enable");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.pub.query.OrgBrowseAction getBrowseByOrgAction(){
 if(context.get("browseByOrgAction")!=null)
 return (nc.ui.bd.pub.query.OrgBrowseAction)context.get("browseByOrgAction");
  nc.ui.bd.pub.query.OrgBrowseAction bean = new nc.ui.bd.pub.query.OrgBrowseAction();
  context.put("browseByOrgAction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setFunnode("10140SOB");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.pub.assign.multiorg.MultiOrgAssignVisitAction getOrgvisitAction(){
 if(context.get("orgvisitAction")!=null)
 return (nc.ui.bd.pub.assign.multiorg.MultiOrgAssignVisitAction)context.get("orgvisitAction");
  nc.ui.bd.pub.assign.multiorg.MultiOrgAssignVisitAction bean = new nc.ui.bd.pub.assign.multiorg.MultiOrgAssignVisitAction();
  context.put("orgvisitAction",bean);
  bean.setCode("suporgvisitaction");
  bean.setModel(getBaseinfoModel());
  bean.setFunnode("10140SORG");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.supplier.baseinfo.action.SupBaseAssociatePfAction getAssociateSupPfAction(){
 if(context.get("associateSupPfAction")!=null)
 return (nc.ui.bd.supplier.baseinfo.action.SupBaseAssociatePfAction)context.get("associateSupPfAction");
  nc.ui.bd.supplier.baseinfo.action.SupBaseAssociatePfAction bean = new nc.ui.bd.supplier.baseinfo.action.SupBaseAssociatePfAction();
  context.put("associateSupPfAction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setApproveModelName("approvePfModel");
  bean.setApproveXmlFilePath("nc/ui/bd/supplier/config/supplier_associate_pf.xml");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.pub.actions.AccessoryAction getAccessoryAction(){
 if(context.get("accessoryAction")!=null)
 return (nc.ui.bd.pub.actions.AccessoryAction)context.get("accessoryAction");
  nc.ui.bd.pub.actions.AccessoryAction bean = new nc.ui.bd.pub.actions.AccessoryAction();
  context.put("accessoryAction",bean);
  bean.setWindowListener(getAccessoryShowAction());
  bean.setModel(getBaseinfoModel());
  bean.setMetaDataID("720dcc7c-ff19-48f4-b9c5-b90906682f45");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.GroupAction getList_printActionGroup(){
 if(context.get("list_printActionGroup")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("list_printActionGroup");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("list_printActionGroup",bean);
  bean.setCode("printgroup");
  bean.setActions(getManagedList21());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList21(){  List list = new ArrayList();  list.add(getList_templatePrint());  list.add(getList_templatePreview());  list.add(getList_outoutAction());  return list;}

public nc.ui.bd.pub.actions.print.BDTemplatePaginationPreviewAction getList_templatePreview(){
 if(context.get("list_templatePreview")!=null)
 return (nc.ui.bd.pub.actions.print.BDTemplatePaginationPreviewAction)context.get("list_templatePreview");
  nc.ui.bd.pub.actions.print.BDTemplatePaginationPreviewAction bean = new nc.ui.bd.pub.actions.print.BDTemplatePaginationPreviewAction();
  context.put("list_templatePreview",bean);
  bean.setPrintAction(getList_templatePrint());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.pub.actions.print.BDTemplatePaginationPrintAction getList_templatePrint(){
 if(context.get("list_templatePrint")!=null)
 return (nc.ui.bd.pub.actions.print.BDTemplatePaginationPrintAction)context.get("list_templatePrint");
  nc.ui.bd.pub.actions.print.BDTemplatePaginationPrintAction bean = new nc.ui.bd.pub.actions.print.BDTemplatePaginationPrintAction();
  context.put("list_templatePrint",bean);
  bean.setModel(getBaseinfoModel());
  bean.setNodeKey("supplierlist");
  bean.setPaginationModel(getPaginationModel());
  bean.setPrintFactory(getPrintFactory());
  bean.setPrintDlgParentConatiner(getBaseinfoListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.pub.actions.print.BDPaginationOutputAction getList_outoutAction(){
 if(context.get("list_outoutAction")!=null)
 return (nc.ui.bd.pub.actions.print.BDPaginationOutputAction)context.get("list_outoutAction");
  nc.ui.bd.pub.actions.print.BDPaginationOutputAction bean = new nc.ui.bd.pub.actions.print.BDPaginationOutputAction();
  context.put("list_outoutAction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setNodeKey("supplierlist");
  bean.setPaginationModel(getPaginationModel());
  bean.setPrintFactory(getPrintFactory());
  bean.setPrintDlgParentConatiner(getBaseinfoListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.pub.actions.print.DefaultTemplatePagePrintFactory getPrintFactory(){
 if(context.get("printFactory")!=null)
 return (nc.ui.bd.pub.actions.print.DefaultTemplatePagePrintFactory)context.get("printFactory");
  nc.ui.bd.pub.actions.print.DefaultTemplatePagePrintFactory bean = new nc.ui.bd.pub.actions.print.DefaultTemplatePagePrintFactory();
  context.put("printFactory",bean);
  bean.setMdId("720dcc7c-ff19-48f4-b9c5-b90906682f45");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.GroupAction getCard_printActionGroup(){
 if(context.get("card_printActionGroup")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("card_printActionGroup");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("card_printActionGroup",bean);
  bean.setCode("printgroup");
  bean.setActions(getManagedList22());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList22(){  List list = new ArrayList();  list.add(getCard_templatePrint());  list.add(getCard_templatePreview());  list.add(getCard_outputAction());  return list;}

public nc.ui.uif2.actions.TemplatePreviewAction getCard_templatePreview(){
 if(context.get("card_templatePreview")!=null)
 return (nc.ui.uif2.actions.TemplatePreviewAction)context.get("card_templatePreview");
  nc.ui.uif2.actions.TemplatePreviewAction bean = new nc.ui.uif2.actions.TemplatePreviewAction();
  context.put("card_templatePreview",bean);
  bean.setModel(getBaseinfoModel());
  bean.setNodeKey("suppliercard");
  bean.setDatasource(getSingleDataSource());
  bean.setPrintDlgParentConatiner(getBaseinfoCardView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.TemplatePrintAction getCard_templatePrint(){
 if(context.get("card_templatePrint")!=null)
 return (nc.ui.uif2.actions.TemplatePrintAction)context.get("card_templatePrint");
  nc.ui.uif2.actions.TemplatePrintAction bean = new nc.ui.uif2.actions.TemplatePrintAction();
  context.put("card_templatePrint",bean);
  bean.setModel(getBaseinfoModel());
  bean.setNodeKey("suppliercard");
  bean.setDatasource(getSingleDataSource());
  bean.setPrintDlgParentConatiner(getBaseinfoCardView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.OutputAction getCard_outputAction(){
 if(context.get("card_outputAction")!=null)
 return (nc.ui.uif2.actions.OutputAction)context.get("card_outputAction");
  nc.ui.uif2.actions.OutputAction bean = new nc.ui.uif2.actions.OutputAction();
  context.put("card_outputAction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setNodeKey("suppliercard");
  bean.setDatasource(getSingleDataSource());
  bean.setPrintDlgParentConatiner(getBaseinfoCardView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.pub.actions.print.MetaDataSingleSelectDataSource getSingleDataSource(){
 if(context.get("singleDataSource")!=null)
 return (nc.ui.bd.pub.actions.print.MetaDataSingleSelectDataSource)context.get("singleDataSource");
  nc.ui.bd.pub.actions.print.MetaDataSingleSelectDataSource bean = new nc.ui.bd.pub.actions.print.MetaDataSingleSelectDataSource();
  context.put("singleDataSource",bean);
  bean.setModel(getBaseinfoModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.MenuAction getAssistantFuncMenu(){
 if(context.get("assistantFuncMenu")!=null)
 return (nc.funcnode.ui.action.MenuAction)context.get("assistantFuncMenu");
  nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
  context.put("assistantFuncMenu",bean);
  bean.setName(getI18nFB_67b82f());
  bean.setCode("AssistantFuncMenu");
  bean.setActions(getManagedList23());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_67b82f(){
 if(context.get("nc.ui.uif2.I18nFB#67b82f")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#67b82f");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#67b82f",bean);  bean.setResDir("common");
  bean.setDefaultValue("辅助功能");
  bean.setResId("UC001-0000137");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#67b82f",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList23(){  List list = new ArrayList();  list.add(getCreateCustAction());  list.add(getAssociCustAction());  list.add(getAccessoryAction());  return list;}

public nc.ui.bd.supplier.baseinfo.action.SupBaseInfoSaveAction getCard_baseinfosaveaction(){
 if(context.get("card_baseinfosaveaction")!=null)
 return (nc.ui.bd.supplier.baseinfo.action.SupBaseInfoSaveAction)context.get("card_baseinfosaveaction");
  nc.ui.bd.supplier.baseinfo.action.SupBaseInfoSaveAction bean = new nc.ui.bd.supplier.baseinfo.action.SupBaseInfoSaveAction();
  context.put("card_baseinfosaveaction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setEditor(getBaseinfoCardView());
  bean.setValidationService(getSaveValidation());
  bean.setInterceptor(getCompositeActionInterceptor());
  bean.setService(getService());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.supplier.baseinfo.action.SupBaseInfoSaveAddAction getCard_saveaddAction(){
 if(context.get("card_saveaddAction")!=null)
 return (nc.ui.bd.supplier.baseinfo.action.SupBaseInfoSaveAddAction)context.get("card_saveaddAction");
  nc.ui.bd.supplier.baseinfo.action.SupBaseInfoSaveAddAction bean = new nc.ui.bd.supplier.baseinfo.action.SupBaseInfoSaveAddAction();
  context.put("card_saveaddAction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setEditor(getBaseinfoCardView());
  bean.setSaveAction(getCard_baseinfosaveaction());
  bean.setAddAction(getList_baseinfoaddaction());
  bean.setValidationService(getSaveValidation());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.pub.actions.CompositeActionInterceptor getCompositeActionInterceptor(){
 if(context.get("compositeActionInterceptor")!=null)
 return (nc.ui.bd.pub.actions.CompositeActionInterceptor)context.get("compositeActionInterceptor");
  nc.ui.bd.pub.actions.CompositeActionInterceptor bean = new nc.ui.bd.pub.actions.CompositeActionInterceptor();
  context.put("compositeActionInterceptor",bean);
  bean.setInterceptors(getManagedList24());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList24(){  List list = new ArrayList();  list.add(getManageModeActionInterceptor());  list.add(getBdSaveActionInterceptor());  return list;}

public nc.ui.bd.pub.actions.BDSaveActionInterceptor getBdSaveActionInterceptor(){
 if(context.get("bdSaveActionInterceptor")!=null)
 return (nc.ui.bd.pub.actions.BDSaveActionInterceptor)context.get("bdSaveActionInterceptor");
  nc.ui.bd.pub.actions.BDSaveActionInterceptor bean = new nc.ui.bd.pub.actions.BDSaveActionInterceptor();
  context.put("bdSaveActionInterceptor",bean);
  bean.setEditor(getBaseinfoCardView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.pub.actions.ManageModeActionInterceptor getManageModeActionInterceptor(){
 if(context.get("manageModeActionInterceptor")!=null)
 return (nc.ui.bd.pub.actions.ManageModeActionInterceptor)context.get("manageModeActionInterceptor");
  nc.ui.bd.pub.actions.ManageModeActionInterceptor bean = new nc.ui.bd.pub.actions.ManageModeActionInterceptor();
  context.put("manageModeActionInterceptor",bean);
  bean.setModel(getBaseinfoModel());
  bean.setEditor(getBaseinfoCardView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.supplier.baseinfo.action.SupplierValidationService getValidation(){
 if(context.get("validation")!=null)
 return (nc.ui.bd.supplier.baseinfo.action.SupplierValidationService)context.get("validation");
  nc.ui.bd.supplier.baseinfo.action.SupplierValidationService bean = new nc.ui.bd.supplier.baseinfo.action.SupplierValidationService();
  context.put("validation",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.bs.uif2.validation.DefaultValidationService getSaveValidation(){
 if(context.get("saveValidation")!=null)
 return (nc.bs.uif2.validation.DefaultValidationService)context.get("saveValidation");
  nc.bs.uif2.validation.DefaultValidationService bean = new nc.bs.uif2.validation.DefaultValidationService();
  context.put("saveValidation",bean);
  bean.setValidators(getManagedList25());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList25(){  List list = new ArrayList();  list.add(getSupplierNotNullValidator_69b57b());  return list;}

private nc.bs.bd.supplier.baseinfo.validator.SupplierNotNullValidator getSupplierNotNullValidator_69b57b(){
 if(context.get("nc.bs.bd.supplier.baseinfo.validator.SupplierNotNullValidator#69b57b")!=null)
 return (nc.bs.bd.supplier.baseinfo.validator.SupplierNotNullValidator)context.get("nc.bs.bd.supplier.baseinfo.validator.SupplierNotNullValidator#69b57b");
  nc.bs.bd.supplier.baseinfo.validator.SupplierNotNullValidator bean = new nc.bs.bd.supplier.baseinfo.validator.SupplierNotNullValidator();
  context.put("nc.bs.bd.supplier.baseinfo.validator.SupplierNotNullValidator#69b57b",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.supplier.baseinfo.action.SupBaseInfoCancelAction getCard_baseinfocancelaction(){
 if(context.get("card_baseinfocancelaction")!=null)
 return (nc.ui.bd.supplier.baseinfo.action.SupBaseInfoCancelAction)context.get("card_baseinfocancelaction");
  nc.ui.bd.supplier.baseinfo.action.SupBaseInfoCancelAction bean = new nc.ui.bd.supplier.baseinfo.action.SupBaseInfoCancelAction();
  context.put("card_baseinfocancelaction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setEditor(getBaseinfoCardView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.GroupAction getCard_approveActionGroup(){
 if(context.get("card_approveActionGroup")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("card_approveActionGroup");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("card_approveActionGroup",bean);
  bean.setActions(getManagedList26());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList26(){  List list = new ArrayList();  list.add(getCard_approveaction());  list.add(getCard_unapproveaction());  return list;}

public nc.ui.bd.supplier.baseinfo.action.SupApproveAction getCard_approveaction(){
 if(context.get("card_approveaction")!=null)
 return (nc.ui.bd.supplier.baseinfo.action.SupApproveAction)context.get("card_approveaction");
  nc.ui.bd.supplier.baseinfo.action.SupApproveAction bean = new nc.ui.bd.supplier.baseinfo.action.SupApproveAction();
  context.put("card_approveaction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setCode("Approve");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.supplier.baseinfo.action.SupUnApproveAction getCard_unapproveaction(){
 if(context.get("card_unapproveaction")!=null)
 return (nc.ui.bd.supplier.baseinfo.action.SupUnApproveAction)context.get("card_unapproveaction");
  nc.ui.bd.supplier.baseinfo.action.SupUnApproveAction bean = new nc.ui.bd.supplier.baseinfo.action.SupUnApproveAction();
  context.put("card_unapproveaction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setCode("UnApprove");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.GroupAction getCard_entireFreezeActionGroup(){
 if(context.get("card_entireFreezeActionGroup")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("card_entireFreezeActionGroup");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("card_entireFreezeActionGroup",bean);
  bean.setActions(getManagedList27());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList27(){  List list = new ArrayList();  list.add(getFreezeAction());  list.add(getUnFreezeAction());  return list;}

public nc.ui.bd.supplier.baseinfo.action.SupBaseInfoFreezeAction getFreezeAction(){
 if(context.get("freezeAction")!=null)
 return (nc.ui.bd.supplier.baseinfo.action.SupBaseInfoFreezeAction)context.get("freezeAction");
  nc.ui.bd.supplier.baseinfo.action.SupBaseInfoFreezeAction bean = new nc.ui.bd.supplier.baseinfo.action.SupBaseInfoFreezeAction();
  context.put("freezeAction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setInterceptor(getInterceptor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.supplier.baseinfo.action.SupBaseInfoUnFreezeAction getUnFreezeAction(){
 if(context.get("unFreezeAction")!=null)
 return (nc.ui.bd.supplier.baseinfo.action.SupBaseInfoUnFreezeAction)context.get("unFreezeAction");
  nc.ui.bd.supplier.baseinfo.action.SupBaseInfoUnFreezeAction bean = new nc.ui.bd.supplier.baseinfo.action.SupBaseInfoUnFreezeAction();
  context.put("unFreezeAction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setInterceptor(getInterceptor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.pub.actions.ManageModeActionInterceptor getInterceptor(){
 if(context.get("interceptor")!=null)
 return (nc.ui.bd.pub.actions.ManageModeActionInterceptor)context.get("interceptor");
  nc.ui.bd.pub.actions.ManageModeActionInterceptor bean = new nc.ui.bd.pub.actions.ManageModeActionInterceptor();
  context.put("interceptor",bean);
  bean.setModel(getBaseinfoModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.GroupAction getCard_enableActionGroup(){
 if(context.get("card_enableActionGroup")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("card_enableActionGroup");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("card_enableActionGroup",bean);
  bean.setActions(getManagedList28());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList28(){  List list = new ArrayList();  list.add(getCard_enableaction());  list.add(getCard_disableaction());  return list;}

public nc.ui.bd.supplier.baseinfo.action.SupBaseInfoDisableAction getCard_disableaction(){
 if(context.get("card_disableaction")!=null)
 return (nc.ui.bd.supplier.baseinfo.action.SupBaseInfoDisableAction)context.get("card_disableaction");
  nc.ui.bd.supplier.baseinfo.action.SupBaseInfoDisableAction bean = new nc.ui.bd.supplier.baseinfo.action.SupBaseInfoDisableAction();
  context.put("card_disableaction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setResourceCode("supplier");
  bean.setMdOperateCode("disable");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.supplier.baseinfo.action.SupBaseInfoEnableAction getCard_enableaction(){
 if(context.get("card_enableaction")!=null)
 return (nc.ui.bd.supplier.baseinfo.action.SupBaseInfoEnableAction)context.get("card_enableaction");
  nc.ui.bd.supplier.baseinfo.action.SupBaseInfoEnableAction bean = new nc.ui.bd.supplier.baseinfo.action.SupBaseInfoEnableAction();
  context.put("card_enableaction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setResourceCode("supplier");
  bean.setMdOperateCode("enable");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.cust.baseinfo.action.BankaccAction getBankaccaction(){
 if(context.get("bankaccaction")!=null)
 return (nc.ui.bd.cust.baseinfo.action.BankaccAction)context.get("bankaccaction");
  nc.ui.bd.cust.baseinfo.action.BankaccAction bean = new nc.ui.bd.cust.baseinfo.action.BankaccAction();
  context.put("bankaccaction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setFunnode("10140SBA");
  bean.setRefreshSingleAction(getCard_refreshAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.cust.baseinfo.action.CustAddressAction getSupaddressAction(){
 if(context.get("supaddressAction")!=null)
 return (nc.ui.bd.cust.baseinfo.action.CustAddressAction)context.get("supaddressAction");
  nc.ui.bd.cust.baseinfo.action.CustAddressAction bean = new nc.ui.bd.cust.baseinfo.action.CustAddressAction();
  context.put("supaddressAction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setFunnode("10140SAD");
  bean.setRefreshSingleAction(getCard_refreshAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.supupgrade.action.SupUpgradeAction getUpgradeAction(){
 if(context.get("upgradeAction")!=null)
 return (nc.ui.bd.supupgrade.action.SupUpgradeAction)context.get("upgradeAction");
  nc.ui.bd.supupgrade.action.SupUpgradeAction bean = new nc.ui.bd.supupgrade.action.SupUpgradeAction();
  context.put("upgradeAction",bean);
  bean.setModel(getBaseinfoModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.supplier.baseinfo.action.CreateCustAction getCreateCustAction(){
 if(context.get("createCustAction")!=null)
 return (nc.ui.bd.supplier.baseinfo.action.CreateCustAction)context.get("createCustAction");
  nc.ui.bd.supplier.baseinfo.action.CreateCustAction bean = new nc.ui.bd.supplier.baseinfo.action.CreateCustAction();
  context.put("createCustAction",bean);
  bean.setSupplierModel(getBaseinfoModel());
  bean.setCreateCustModel(getCreateViewModel());
  bean.setExceptionHandler(getExceptionhandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.supplier.baseinfo.action.AssociateCustAction getAssociCustAction(){
 if(context.get("associCustAction")!=null)
 return (nc.ui.bd.supplier.baseinfo.action.AssociateCustAction)context.get("associCustAction");
  nc.ui.bd.supplier.baseinfo.action.AssociateCustAction bean = new nc.ui.bd.supplier.baseinfo.action.AssociateCustAction();
  context.put("associCustAction",bean);
  bean.setSupplierModel(getBaseinfoModel());
  bean.setAssociateModel(getAssociCustModel());
  bean.setAssociCustView(getAssociCustView());
  bean.setSaveAction(getAssociCustSaveAction());
  bean.setCancelAction(getAssociCustCancelAction());
  bean.setExceptionHandler(getExceptionhandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.supplier.baseinfo.view.SupAssociateCustEditor getAssociCustView(){
 if(context.get("associCustView")!=null)
 return (nc.ui.bd.supplier.baseinfo.view.SupAssociateCustEditor)context.get("associCustView");
  nc.ui.bd.supplier.baseinfo.view.SupAssociateCustEditor bean = new nc.ui.bd.supplier.baseinfo.view.SupAssociateCustEditor();
  context.put("associCustView",bean);
  bean.setTemplateContainer(getTemplateContainer());
  bean.setNodekey("associcust");
  bean.setModel(getAssociCustModel());
  bean.setBaseModel(getBaseinfoModel());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.pub.view.BDEditorOkCancelDialogMediator getAssociateCustDlgBaseMediator(){
 if(context.get("associateCustDlgBaseMediator")!=null)
 return (nc.ui.bd.pub.view.BDEditorOkCancelDialogMediator)context.get("associateCustDlgBaseMediator");
  nc.ui.bd.pub.view.BDEditorOkCancelDialogMediator bean = new nc.ui.bd.pub.view.BDEditorOkCancelDialogMediator();
  context.put("associateCustDlgBaseMediator",bean);
  bean.setName(getI18nFB_107085d());
  bean.setModel(getAssociCustModel());
  bean.setEditor(getAssociCustView());
  bean.setSaveAction(getAssociCustSaveAction());
  bean.setCancelAction(getCancelAction_ae2ca0());
  bean.setWidth(350);
  bean.setHeight(400);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_107085d(){
 if(context.get("nc.ui.uif2.I18nFB#107085d")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#107085d");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#107085d",bean);  bean.setResDir("10140sub");
  bean.setDefaultValue("关联客户");
  bean.setResId("110140sub0012");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#107085d",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private nc.ui.uif2.actions.CancelAction getCancelAction_ae2ca0(){
 if(context.get("nc.ui.uif2.actions.CancelAction#ae2ca0")!=null)
 return (nc.ui.uif2.actions.CancelAction)context.get("nc.ui.uif2.actions.CancelAction#ae2ca0");
  nc.ui.uif2.actions.CancelAction bean = new nc.ui.uif2.actions.CancelAction();
  context.put("nc.ui.uif2.actions.CancelAction#ae2ca0",bean);
  bean.setModel(getAssociCustModel());
  bean.setEditor(getAssociCustView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.model.BillManageModel getAssociCustModel(){
 if(context.get("associCustModel")!=null)
 return (nc.ui.uif2.model.BillManageModel)context.get("associCustModel");
  nc.ui.uif2.model.BillManageModel bean = new nc.ui.uif2.model.BillManageModel();
  context.put("associCustModel",bean);
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.supplier.baseinfo.action.AssociateCustSaveAction getAssociCustSaveAction(){
 if(context.get("associCustSaveAction")!=null)
 return (nc.ui.bd.supplier.baseinfo.action.AssociateCustSaveAction)context.get("associCustSaveAction");
  nc.ui.bd.supplier.baseinfo.action.AssociateCustSaveAction bean = new nc.ui.bd.supplier.baseinfo.action.AssociateCustSaveAction();
  context.put("associCustSaveAction",bean);
  bean.setSupBaseinfoModel(getBaseinfoModel());
  bean.setAssociCustView(getAssociCustView());
  bean.setModel(getAssociCustModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.CancelAction getAssociCustCancelAction(){
 if(context.get("associCustCancelAction")!=null)
 return (nc.ui.uif2.actions.CancelAction)context.get("associCustCancelAction");
  nc.ui.uif2.actions.CancelAction bean = new nc.ui.uif2.actions.CancelAction();
  context.put("associCustCancelAction",bean);
  bean.setModel(getAssociCustModel());
  bean.setEditor(getAssociCustView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.pub.view.BDEditorOkCancelDialogMediator getCreateVCustDlgBaseMediator(){
 if(context.get("createVCustDlgBaseMediator")!=null)
 return (nc.ui.bd.pub.view.BDEditorOkCancelDialogMediator)context.get("createVCustDlgBaseMediator");
  nc.ui.bd.pub.view.BDEditorOkCancelDialogMediator bean = new nc.ui.bd.pub.view.BDEditorOkCancelDialogMediator();
  context.put("createVCustDlgBaseMediator",bean);
  bean.setName(getI18nFB_13b5e5a());
  bean.setModel(getCreateViewModel());
  bean.setEditor(getCreateCustView());
  bean.setSaveAction(getCreateCustSaveAction());
  bean.setCancelAction(getCreateCustCancelAction());
  bean.setWidth(350);
  bean.setHeight(400);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_13b5e5a(){
 if(context.get("nc.ui.uif2.I18nFB#13b5e5a")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#13b5e5a");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#13b5e5a",bean);  bean.setResDir("10140sub");
  bean.setDefaultValue("生成客户");
  bean.setResId("110140sub0013");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#13b5e5a",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

public nc.ui.bd.supplier.baseinfo.view.SupCreateCustView getCreateCustView(){
 if(context.get("createCustView")!=null)
 return (nc.ui.bd.supplier.baseinfo.view.SupCreateCustView)context.get("createCustView");
  nc.ui.bd.supplier.baseinfo.view.SupCreateCustView bean = new nc.ui.bd.supplier.baseinfo.view.SupCreateCustView();
  context.put("createCustView",bean);
  bean.setNodekey("createCust");
  bean.setModel(getCreateViewModel());
  bean.setBaseModel(getBaseinfoModel());
  bean.setTemplateContainer(getTemplateContainer());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.model.BillManageModel getCreateViewModel(){
 if(context.get("createViewModel")!=null)
 return (nc.ui.uif2.model.BillManageModel)context.get("createViewModel");
  nc.ui.uif2.model.BillManageModel bean = new nc.ui.uif2.model.BillManageModel();
  context.put("createViewModel",bean);
  bean.setBusinessObjectAdapterFactory(getBoadapterfacotry());
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.supplier.baseinfo.action.CreateCustSaveAction getCreateCustSaveAction(){
 if(context.get("createCustSaveAction")!=null)
 return (nc.ui.bd.supplier.baseinfo.action.CreateCustSaveAction)context.get("createCustSaveAction");
  nc.ui.bd.supplier.baseinfo.action.CreateCustSaveAction bean = new nc.ui.bd.supplier.baseinfo.action.CreateCustSaveAction();
  context.put("createCustSaveAction",bean);
  bean.setEditor(getCreateCustView());
  bean.setResourceSupModel(getBaseinfoModel());
  bean.setModel(getCreateViewModel());
  bean.setValidationService(getSupplierValidationService_3ee32b());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.bd.supplier.baseinfo.model.SupplierValidationService getSupplierValidationService_3ee32b(){
 if(context.get("nc.ui.bd.supplier.baseinfo.model.SupplierValidationService#3ee32b")!=null)
 return (nc.ui.bd.supplier.baseinfo.model.SupplierValidationService)context.get("nc.ui.bd.supplier.baseinfo.model.SupplierValidationService#3ee32b");
  nc.ui.bd.supplier.baseinfo.model.SupplierValidationService bean = new nc.ui.bd.supplier.baseinfo.model.SupplierValidationService();
  context.put("nc.ui.bd.supplier.baseinfo.model.SupplierValidationService#3ee32b",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.supplier.baseinfo.action.CreateCustCancelAction getCreateCustCancelAction(){
 if(context.get("createCustCancelAction")!=null)
 return (nc.ui.bd.supplier.baseinfo.action.CreateCustCancelAction)context.get("createCustCancelAction");
  nc.ui.bd.supplier.baseinfo.action.CreateCustCancelAction bean = new nc.ui.bd.supplier.baseinfo.action.CreateCustCancelAction();
  context.put("createCustCancelAction",bean);
  bean.setModel(getCreateViewModel());
  bean.setEditor(getCreateCustView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.SwitchAction getSwitchBaseMediator(){
 if(context.get("switchBaseMediator")!=null)
 return (nc.ui.uif2.actions.SwitchAction)context.get("switchBaseMediator");
  nc.ui.uif2.actions.SwitchAction bean = new nc.ui.uif2.actions.SwitchAction();
  context.put("switchBaseMediator",bean);
  bean.setContext(getContext());
  bean.setComponent1(getBaseinfoListView());
  bean.setComponent2(getBaseinfoCardView());
  bean.init();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.supplier.baseinfo.action.SupBaseLinkmanAddLineAction getAddlineAction(){
 if(context.get("addlineAction")!=null)
 return (nc.ui.bd.supplier.baseinfo.action.SupBaseLinkmanAddLineAction)context.get("addlineAction");
  nc.ui.bd.supplier.baseinfo.action.SupBaseLinkmanAddLineAction bean = new nc.ui.bd.supplier.baseinfo.action.SupBaseLinkmanAddLineAction();
  context.put("addlineAction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setCardpanel(getBaseinfoCardView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.InsertLineAction getInserlineAction(){
 if(context.get("inserlineAction")!=null)
 return (nc.ui.uif2.actions.InsertLineAction)context.get("inserlineAction");
  nc.ui.uif2.actions.InsertLineAction bean = new nc.ui.uif2.actions.InsertLineAction();
  context.put("inserlineAction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setCardpanel(getBaseinfoCardView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.DelLineAction getDellineAction(){
 if(context.get("dellineAction")!=null)
 return (nc.ui.uif2.actions.DelLineAction)context.get("dellineAction");
  nc.ui.uif2.actions.DelLineAction bean = new nc.ui.uif2.actions.DelLineAction();
  context.put("dellineAction",bean);
  bean.setModel(getBaseinfoModel());
  bean.setCardpanel(getBaseinfoCardView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.DefaultExceptionHanler getExceptionhandler(){
 if(context.get("exceptionhandler")!=null)
 return (nc.ui.uif2.DefaultExceptionHanler)context.get("exceptionhandler");
  nc.ui.uif2.DefaultExceptionHanler bean = new nc.ui.uif2.DefaultExceptionHanler();
  context.put("exceptionhandler",bean);
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.FunNodeClosingHandler getClosingListener(){
 if(context.get("ClosingListener")!=null)
 return (nc.ui.uif2.FunNodeClosingHandler)context.get("ClosingListener");
  nc.ui.uif2.FunNodeClosingHandler bean = new nc.ui.uif2.FunNodeClosingHandler();
  context.put("ClosingListener",bean);
  bean.setModel(getBaseinfoModel());
  bean.setSaveaction(getCard_baseinfosaveaction());
  bean.setCancelaction(getCard_baseinfocancelaction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.pub.extend.ExtendContext getBaseExtendContext(){
 if(context.get("baseExtendContext")!=null)
 return (nc.ui.bd.pub.extend.ExtendContext)context.get("baseExtendContext");
  nc.ui.bd.pub.extend.ExtendContext bean = new nc.ui.bd.pub.extend.ExtendContext();
  context.put("baseExtendContext",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.pub.extend.ExtendOrgBaseUIProcessMediator getBaseExtendUIPrcBaseMediator(){
 if(context.get("baseExtendUIPrcBaseMediator")!=null)
 return (nc.ui.bd.pub.extend.ExtendOrgBaseUIProcessMediator)context.get("baseExtendUIPrcBaseMediator");
  nc.ui.bd.pub.extend.ExtendOrgBaseUIProcessMediator bean = new nc.ui.bd.pub.extend.ExtendOrgBaseUIProcessMediator();
  context.put("baseExtendUIPrcBaseMediator",bean);
  bean.setBaseEditor(getBaseinfoCardView());
  bean.setExtendContext(getBaseExtendContext());
  bean.setBaseTabName(getI18nFB_1664c11());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_1664c11(){
 if(context.get("nc.ui.uif2.I18nFB#1664c11")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#1664c11");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#1664c11",bean);  bean.setResDir("10140cub");
  bean.setDefaultValue("基本信息");
  bean.setResId("010140cub0037");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#1664c11",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

public nc.ui.bd.pub.tools.BDPubQueryActionMediator getBdqueryActionBaseMediator(){
 if(context.get("bdqueryActionBaseMediator")!=null)
 return (nc.ui.bd.pub.tools.BDPubQueryActionMediator)context.get("bdqueryActionBaseMediator");
  nc.ui.bd.pub.tools.BDPubQueryActionMediator bean = new nc.ui.bd.pub.tools.BDPubQueryActionMediator();
  context.put("bdqueryActionBaseMediator",bean);
  bean.setQueryAction(getList_searchAction());
  bean.setOrgFieldCode(getManagedList29());
  bean.process();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList29(){  List list = new ArrayList();  list.add("pk_org");  return list;}

public nc.ui.bd.supplier.baseinfo.action.BatchCreateCustAction getBatchcreateCustAction(){
 if(context.get("batchcreateCustAction")!=null)
 return (nc.ui.bd.supplier.baseinfo.action.BatchCreateCustAction)context.get("batchcreateCustAction");
  nc.ui.bd.supplier.baseinfo.action.BatchCreateCustAction bean = new nc.ui.bd.supplier.baseinfo.action.BatchCreateCustAction();
  context.put("batchcreateCustAction",bean);
  bean.setSupplierModel(getBaseinfoModel());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getBaseinfoListViewActions(){
 if(context.get("baseinfoListViewActions")!=null)
 return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("baseinfoListViewActions");
  nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getBaseinfoListView());  context.put("baseinfoListViewActions",bean);
  bean.setActions(getManagedList30());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList30(){  List list = new ArrayList();  list.add(getList_baseinfoaddaction());  list.add(getList_baseinfoeditaction());  list.add(getList_baseinfodeleteaction());  list.add(getList_copyaddaction());  list.add(getBatchupdategroupaction());  list.add(getSeparatorAction());  list.add(getList_searchAction());  list.add(getList_refreshAction());  list.add(getFilterAction());  list.add(getSeparatorAction());  list.add(getAssignActionGroup());  list.add(getBankaccaction());  list.add(getSupaddressAction());  list.add(getList_approveActionGroup());  list.add(getList_entireFreezeActionGroup());  list.add(getList_enablegroupaction());  list.add(getAssistantFuncMenu());  list.add(getSeparatorAction());  list.add(getBrowseByOrgAction());  list.add(getOrgvisitAction());  list.add(getAssociateSupPfAction());  list.add(getSeparatorAction());  list.add(getList_printActionGroup());  list.add(getTuisong());  return list;}

public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getBaseinfoCardViewActions(){
 if(context.get("baseinfoCardViewActions")!=null)
 return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("baseinfoCardViewActions");
  nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getBaseinfoCardView());  context.put("baseinfoCardViewActions",bean);
  bean.setActions(getManagedList31());
  bean.setEditActions(getManagedList32());
  bean.setModel(getBaseinfoModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList31(){  List list = new ArrayList();  list.add(getList_baseinfoaddaction());  list.add(getList_baseinfoeditaction());  list.add(getList_baseinfodeleteaction());  list.add(getList_copyaddaction());  list.add(getBatchupdategroupaction());  list.add(getSeparatorAction());  list.add(getList_searchAction());  list.add(getCard_refreshAction());  list.add(getSeparatorAction());  list.add(getAssignActionGroup());  list.add(getBankaccaction());  list.add(getSupaddressAction());  list.add(getCard_approveActionGroup());  list.add(getCard_entireFreezeActionGroup());  list.add(getCard_enableActionGroup());  list.add(getAssistantFuncMenu());  list.add(getSeparatorAction());  list.add(getBrowseByOrgAction());  list.add(getOrgvisitAction());  list.add(getAssociateSupPfAction());  list.add(getSeparatorAction());  list.add(getCard_printActionGroup());  return list;}

private List getManagedList32(){  List list = new ArrayList();  list.add(getCard_baseinfosaveaction());  list.add(getCard_saveaddAction());  list.add(getSeparatorAction());  list.add(getCard_baseinfocancelaction());  return list;}

public nc.ui.bd.supplier.action.GYSTSDCAction getTuisong(){
 if(context.get("tuisong")!=null)
 return (nc.ui.bd.supplier.action.GYSTSDCAction)context.get("tuisong");
  nc.ui.bd.supplier.action.GYSTSDCAction bean = new nc.ui.bd.supplier.action.GYSTSDCAction();
  context.put("tuisong",bean);
  bean.setModel(getBaseinfoModel());
  bean.setEditor(getBaseinfoCardView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

}
