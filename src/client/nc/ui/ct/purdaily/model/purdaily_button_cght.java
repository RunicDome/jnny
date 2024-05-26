package nc.ui.ct.purdaily.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class purdaily_button_cght extends AbstractJavaBeanDefinition{
	private Map<String, Object> context = new HashMap();
public nc.ui.uif2.actions.ActionContributors getToftpanelActionContributors(){
 if(context.get("toftpanelActionContributors")!=null)
 return (nc.ui.uif2.actions.ActionContributors)context.get("toftpanelActionContributors");
  nc.ui.uif2.actions.ActionContributors bean = new nc.ui.uif2.actions.ActionContributors();
  context.put("toftpanelActionContributors",bean);
  bean.setContributors(getManagedList0());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList0(){  List list = new ArrayList();  list.add(getActionsOfList());  list.add(getActionsOfCard());  list.add(getActionsOfHistory());  return list;}

public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getActionsOfList(){
 if(context.get("actionsOfList")!=null)
 return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("actionsOfList");
  nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer((nc.ui.uif2.components.ITabbedPaneAwareComponent)findBeanInUIF2BeanFactory("listView"));  context.put("actionsOfList",bean);
  bean.setActions(getManagedList1());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList1(){  List list = new ArrayList();  list.add(getAddMenuAction());  list.add(getEditAction());  list.add(getDeleteAction());  list.add(getCopyAction());  list.add(getSeparatorAction());  list.add(getQueryAction());  list.add(getRefreshAction());  list.add(getSeparatorAction());  list.add(getCommitMenuAction());  list.add(getApproveMenuAction());  list.add(getTransactMenuAction());  list.add(getAssistMenuAction());  list.add(getSeparatorAction());  list.add(getAsstQueryMenuAction());  list.add(getSeparatorAction());  list.add(getPayplanAction());  list.add(getSeparatorAction());  list.add(getPayplannewAction());  list.add(getSeparatorAction());  list.add(getPrintMenuAction());  return list;}

public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getActionsOfCard(){
 if(context.get("actionsOfCard")!=null)
 return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("actionsOfCard");
  nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer((nc.ui.uif2.components.ITabbedPaneAwareComponent)findBeanInUIF2BeanFactory("billFormEditor"));  context.put("actionsOfCard",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setActions(getManagedList2());
  bean.setEditActions(getManagedList3());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList2(){  List list = new ArrayList();  list.add(getAddMenuAction());  list.add(getEditAction());  list.add(getDeleteAction());  list.add(getCopyAction());  list.add(getSeparatorAction());  list.add(getQueryAction());  list.add(getCardRefreshAction());  list.add(getSeparatorAction());  list.add(getCommitMenuAction());  list.add(getApproveMenuAction());  list.add(getTransactMenuAction());  list.add(getAssistMenuAction());  list.add(getSeparatorAction());  list.add(getAsstQueryMenuAction());  list.add(getSeparatorAction());  list.add(getPayplanAction());  list.add(getSeparatorAction());  list.add(getPayplannewAction());  list.add(getSeparatorAction());  list.add(getPrintMenuAction());  return list;}

private List getManagedList3(){  List list = new ArrayList();  list.add(getSaveAction());  list.add(getSaveApproveAction());  list.add(getSeparatorAction());  list.add(getCancelAction());  list.add(getSeparatorAction());  list.add(getRelatingCtAction());  list.add(getSeparatorAction());  list.add(getLinkCtPriceInfoAction());  return list;}

public nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor getFormInterceptor(){
 if(context.get("formInterceptor")!=null)
 return (nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor)context.get("formInterceptor");
  nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor();
  context.put("formInterceptor",bean);
  bean.setShowUpComponent((nc.ui.uif2.components.IAutoShowUpComponent)findBeanInUIF2BeanFactory("billFormEditor"));
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

public nc.ui.ct.purdaily.action.CGHTPuAddAction getAddAction(){
 if(context.get("addAction")!=null)
 return (nc.ui.ct.purdaily.action.CGHTPuAddAction)context.get("addAction");
  nc.ui.ct.purdaily.action.CGHTPuAddAction bean = new nc.ui.ct.purdaily.action.CGHTPuAddAction();
  context.put("addAction",bean);
  bean.setCardForm((nc.ui.pubapp.uif2app.view.ShowUpableBillForm)findBeanInUIF2BeanFactory("billFormEditor"));
  bean.setModel((nc.ui.uif2.model.AbstractAppModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setInterceptor(getCompositeActionInterceptor_1730d86());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor getCompositeActionInterceptor_1730d86(){
 if(context.get("nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor#1730d86")!=null)
 return (nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor)context.get("nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor#1730d86");
  nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor();
  context.put("nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor#1730d86",bean);
  bean.setInterceptors(getManagedList4());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList4(){  List list = new ArrayList();  list.add(getShowUpComponentInterceptor_6f8405());  return list;}

private nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor getShowUpComponentInterceptor_6f8405(){
 if(context.get("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#6f8405")!=null)
 return (nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor)context.get("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#6f8405");
  nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor();
  context.put("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#6f8405",bean);
  bean.setShowUpComponent((nc.ui.uif2.components.IAutoShowUpComponent)findBeanInUIF2BeanFactory("billFormEditor"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuRelatingCtAction getRelatingCtAction(){
 if(context.get("relatingCtAction")!=null)
 return (nc.ui.ct.purdaily.action.PuRelatingCtAction)context.get("relatingCtAction");
  nc.ui.ct.purdaily.action.PuRelatingCtAction bean = new nc.ui.ct.purdaily.action.PuRelatingCtAction();
  context.put("relatingCtAction",bean);
  bean.setModel((nc.ui.pubapp.uif2app.model.BillManageModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setCardForm((nc.ui.pubapp.uif2app.view.ShowUpableBillForm)findBeanInUIF2BeanFactory("billFormEditor"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuLinkCtPriceInfoAction getLinkCtPriceInfoAction(){
 if(context.get("linkCtPriceInfoAction")!=null)
 return (nc.ui.ct.purdaily.action.PuLinkCtPriceInfoAction)context.get("linkCtPriceInfoAction");
  nc.ui.ct.purdaily.action.PuLinkCtPriceInfoAction bean = new nc.ui.ct.purdaily.action.PuLinkCtPriceInfoAction();
  context.put("linkCtPriceInfoAction",bean);
  bean.setModel((nc.ui.pubapp.uif2app.model.BillManageModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setCardForm((nc.ui.pubapp.uif2app.view.ShowUpableBillForm)findBeanInUIF2BeanFactory("billFormEditor"));
  bean.setListView((nc.ui.pubapp.uif2app.view.ShowUpableBillListView)findBeanInUIF2BeanFactory("listView"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuEditAction getEditAction(){
 if(context.get("editAction")!=null)
 return (nc.ui.ct.purdaily.action.PuEditAction)context.get("editAction");
  nc.ui.ct.purdaily.action.PuEditAction bean = new nc.ui.ct.purdaily.action.PuEditAction();
  context.put("editAction",bean);
  bean.setCardForm((nc.ui.pubapp.uif2app.view.ShowUpableBillForm)findBeanInUIF2BeanFactory("billFormEditor"));
  bean.setModel((nc.ui.uif2.model.AbstractAppModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setInterceptor(getShowUpComponentInterceptor_1526291());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor getShowUpComponentInterceptor_1526291(){
 if(context.get("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#1526291")!=null)
 return (nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor)context.get("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#1526291");
  nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor();
  context.put("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#1526291",bean);
  bean.setShowUpComponent((nc.ui.uif2.components.IAutoShowUpComponent)findBeanInUIF2BeanFactory("billFormEditor"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.CGHTFileAction getGetFileAction(){
 if(context.get("getFileAction")!=null)
 return (nc.ui.ct.purdaily.action.CGHTFileAction)context.get("getFileAction");
  nc.ui.ct.purdaily.action.CGHTFileAction bean = new nc.ui.ct.purdaily.action.CGHTFileAction();
  context.put("getFileAction",bean);
  bean.setModel((nc.ui.pubapp.uif2app.model.BillManageModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setActionCode("GetFileAction");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuQueryAction getQueryAction(){
 if(context.get("queryAction")!=null)
 return (nc.ui.ct.purdaily.action.PuQueryAction)context.get("queryAction");
  nc.ui.ct.purdaily.action.PuQueryAction bean = new nc.ui.ct.purdaily.action.PuQueryAction();
  context.put("queryAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setDataManager((nc.ui.pubapp.uif2app.query2.model.IModelDataManager)findBeanInUIF2BeanFactory("modelDataManager"));
  bean.setQryCondDLGInitializer(getPurQryCondDLGInitializer());
  bean.setTemplateContainer((nc.ui.uif2.editor.QueryTemplateContainer)findBeanInUIF2BeanFactory("queryTemplateContainer"));
  bean.setShowUpComponent((nc.ui.uif2.components.IAutoShowUpComponent)findBeanInUIF2BeanFactory("listView"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.query.PurQryCondDLGInitializer getPurQryCondDLGInitializer(){
 if(context.get("purQryCondDLGInitializer")!=null)
 return (nc.ui.ct.purdaily.query.PurQryCondDLGInitializer)context.get("purQryCondDLGInitializer");
  nc.ui.ct.purdaily.query.PurQryCondDLGInitializer bean = new nc.ui.ct.purdaily.query.PurQryCondDLGInitializer();
  context.put("purQryCondDLGInitializer",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuDeleteAction getDeleteAction(){
 if(context.get("deleteAction")!=null)
 return (nc.ui.ct.purdaily.action.PuDeleteAction)context.get("deleteAction");
  nc.ui.ct.purdaily.action.PuDeleteAction bean = new nc.ui.ct.purdaily.action.PuDeleteAction();
  context.put("deleteAction",bean);
  bean.setModel((nc.ui.pubapp.uif2app.model.BillManageModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setEditor((nc.ui.uif2.editor.IEditor)findBeanInUIF2BeanFactory("billFormEditor"));
  bean.setActionName("DELETE");
  bean.setBillType("Z2");
  bean.setValidationService(getDelpowervalidservice());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuModiDeleteAction getModiDeleteAction(){
 if(context.get("modiDeleteAction")!=null)
 return (nc.ui.ct.purdaily.action.PuModiDeleteAction)context.get("modiDeleteAction");
  nc.ui.ct.purdaily.action.PuModiDeleteAction bean = new nc.ui.ct.purdaily.action.PuModiDeleteAction();
  context.put("modiDeleteAction",bean);
  bean.setSingleBillService(getPuModiDeleteService_7f3cec());
  bean.setModel((nc.ui.uif2.model.AbstractAppModel)findBeanInUIF2BeanFactory("ManageAppModel"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.purdaily.service.PuModiDeleteService getPuModiDeleteService_7f3cec(){
 if(context.get("nc.ui.ct.purdaily.service.PuModiDeleteService#7f3cec")!=null)
 return (nc.ui.ct.purdaily.service.PuModiDeleteService)context.get("nc.ui.ct.purdaily.service.PuModiDeleteService#7f3cec");
  nc.ui.ct.purdaily.service.PuModiDeleteService bean = new nc.ui.ct.purdaily.service.PuModiDeleteService();
  context.put("nc.ui.ct.purdaily.service.PuModiDeleteService#7f3cec",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuSaveAction getSaveAction(){
 if(context.get("saveAction")!=null)
 return (nc.ui.ct.purdaily.action.PuSaveAction)context.get("saveAction");
  nc.ui.ct.purdaily.action.PuSaveAction bean = new nc.ui.ct.purdaily.action.PuSaveAction();
  context.put("saveAction",bean);
  bean.setModel((nc.ui.pubapp.uif2app.model.BillManageModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setEditor((nc.ui.uif2.editor.IEditor)findBeanInUIF2BeanFactory("billFormEditor"));
  bean.setCardForm((nc.ui.pubapp.uif2app.view.ShowUpableBillForm)findBeanInUIF2BeanFactory("billFormEditor"));
  bean.setActionName("SAVEBASE");
  bean.setBillType("Z2");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.CancelAction getCancelAction(){
 if(context.get("cancelAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.CancelAction)context.get("cancelAction");
  nc.ui.pubapp.uif2app.actions.CancelAction bean = new nc.ui.pubapp.uif2app.actions.CancelAction();
  context.put("cancelAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractAppModel)findBeanInUIF2BeanFactory("ManageAppModel"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction getRefreshAction(){
 if(context.get("refreshAction")!=null)
 return (nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction)context.get("refreshAction");
  nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction bean = new nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction();
  context.put("refreshAction",bean);
  bean.setDataManager((nc.ui.pubapp.uif2app.query2.model.IModelDataManager)findBeanInUIF2BeanFactory("modelDataManager"));
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("ManageAppModel"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.RefreshSingleAction getCardRefreshAction(){
 if(context.get("cardRefreshAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.RefreshSingleAction)context.get("cardRefreshAction");
  nc.ui.pubapp.uif2app.actions.RefreshSingleAction bean = new nc.ui.pubapp.uif2app.actions.RefreshSingleAction();
  context.put("cardRefreshAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractAppModel)findBeanInUIF2BeanFactory("ManageAppModel"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction getPreviewAction(){
 if(context.get("previewAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction)context.get("previewAction");
  nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction bean = new nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction();
  context.put("previewAction",bean);
  bean.setPreview(true);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setBeforePrintDataProcess(getPrintProcessor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuPrintAction getPrintAction(){
 if(context.get("printAction")!=null)
 return (nc.ui.ct.purdaily.action.PuPrintAction)context.get("printAction");
  nc.ui.ct.purdaily.action.PuPrintAction bean = new nc.ui.ct.purdaily.action.PuPrintAction();
  context.put("printAction",bean);
  bean.setPreview(false);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setBeforePrintDataProcess(getPrintProcessor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.processor.CtPuPrintProcessor getPrintProcessor(){
 if(context.get("printProcessor")!=null)
 return (nc.ui.ct.purdaily.action.processor.CtPuPrintProcessor)context.get("printProcessor");
  nc.ui.ct.purdaily.action.processor.CtPuPrintProcessor bean = new nc.ui.ct.purdaily.action.processor.CtPuPrintProcessor();
  context.put("printProcessor",bean);
  bean.setModel((nc.ui.pubapp.uif2app.model.BillManageModel)findBeanInUIF2BeanFactory("ManageAppModel"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.CGHTPuCommitScriptAction getCommitAction(){
 if(context.get("commitAction")!=null)
 return (nc.ui.ct.purdaily.action.CGHTPuCommitScriptAction)context.get("commitAction");
  nc.ui.ct.purdaily.action.CGHTPuCommitScriptAction bean = new nc.ui.ct.purdaily.action.CGHTPuCommitScriptAction();
  context.put("commitAction",bean);
  bean.setModel((nc.ui.pubapp.uif2app.model.BillManageModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setEditor((nc.ui.uif2.editor.IEditor)findBeanInUIF2BeanFactory("billFormEditor"));
  bean.setActionName("SAVE");
  bean.setBillType("Z2");
  bean.setPreActionNames(getManagedList5());
  bean.setFilledUpInFlow(true);
  bean.setValidationService(getComitpowervalidservice());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList5(){  List list = new ArrayList();  list.add("SAVEBASE");  return list;}

public nc.ui.pubapp.uif2app.actions.pflow.SaveAndCommitScriptAction getSaveApproveAction(){
 if(context.get("saveApproveAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.pflow.SaveAndCommitScriptAction)context.get("saveApproveAction");
  nc.ui.pubapp.uif2app.actions.pflow.SaveAndCommitScriptAction bean = new nc.ui.pubapp.uif2app.actions.pflow.SaveAndCommitScriptAction(getSaveAction(),getCommitAction());  context.put("saveApproveAction",bean);
  bean.setModel((nc.ui.pubapp.uif2app.model.BillManageModel)findBeanInUIF2BeanFactory("ManageAppModel"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuTakeBackAction getTakeBackAction(){
 if(context.get("takeBackAction")!=null)
 return (nc.ui.ct.purdaily.action.PuTakeBackAction)context.get("takeBackAction");
  nc.ui.ct.purdaily.action.PuTakeBackAction bean = new nc.ui.ct.purdaily.action.PuTakeBackAction();
  context.put("takeBackAction",bean);
  bean.setModel((nc.ui.pubapp.uif2app.model.BillManageModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setEditor((nc.ui.uif2.editor.IEditor)findBeanInUIF2BeanFactory("billFormEditor"));
  bean.setActionName("UNSAVEBILL");
  bean.setFilledUpInFlow(true);
  bean.setBillType("Z2");
  bean.setValidationService(getUncomitpowervalidservice());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.CGHTPuApproveAction getApproveAction(){
 if(context.get("approveAction")!=null)
 return (nc.ui.ct.purdaily.action.CGHTPuApproveAction)context.get("approveAction");
  nc.ui.ct.purdaily.action.CGHTPuApproveAction bean = new nc.ui.ct.purdaily.action.CGHTPuApproveAction();
  context.put("approveAction",bean);
  bean.setModel((nc.ui.pubapp.uif2app.model.BillManageModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setEditor((nc.ui.uif2.editor.IEditor)findBeanInUIF2BeanFactory("billFormEditor"));
  bean.setActionName("APPROVE");
  bean.setFilledUpInFlow(false);
  bean.setBillType("Z2");
  bean.setValidationService(getPowervalidservice());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuUnApproveAction getUnApproveAction(){
 if(context.get("unApproveAction")!=null)
 return (nc.ui.ct.purdaily.action.PuUnApproveAction)context.get("unApproveAction");
  nc.ui.ct.purdaily.action.PuUnApproveAction bean = new nc.ui.ct.purdaily.action.PuUnApproveAction();
  context.put("unApproveAction",bean);
  bean.setModel((nc.ui.pubapp.uif2app.model.BillManageModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setEditor((nc.ui.uif2.editor.IEditor)findBeanInUIF2BeanFactory("billFormEditor"));
  bean.setActionName("UNAPPROVE");
  bean.setFilledUpInFlow(false);
  bean.setBillType("Z2");
  bean.setValidationService(getUnapprovepowervalidservice());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.action.CtCopyAction getCopyAction(){
 if(context.get("copyAction")!=null)
 return (nc.ui.ct.action.CtCopyAction)context.get("copyAction");
  nc.ui.ct.action.CtCopyAction bean = new nc.ui.ct.action.CtCopyAction();
  context.put("copyAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractAppModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setEditor((nc.ui.pubapp.uif2app.view.BillForm)findBeanInUIF2BeanFactory("billFormEditor"));
  bean.setCopyActionProcessor(getCopyActionProcessor());
  bean.setInterceptor(getFormInterceptor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuCopyActionProcessor getCopyActionProcessor(){
 if(context.get("copyActionProcessor")!=null)
 return (nc.ui.ct.purdaily.action.PuCopyActionProcessor)context.get("copyActionProcessor");
  nc.ui.ct.purdaily.action.PuCopyActionProcessor bean = new nc.ui.ct.purdaily.action.PuCopyActionProcessor();
  context.put("copyActionProcessor",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuFrozenAction getFrozenAction(){
 if(context.get("frozenAction")!=null)
 return (nc.ui.ct.purdaily.action.PuFrozenAction)context.get("frozenAction");
  nc.ui.ct.purdaily.action.PuFrozenAction bean = new nc.ui.ct.purdaily.action.PuFrozenAction();
  context.put("frozenAction",bean);
  bean.setModel((nc.ui.pubapp.uif2app.model.BillManageModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setEditor((nc.ui.uif2.editor.IEditor)findBeanInUIF2BeanFactory("billFormEditor"));
  bean.setActionName("FREEZE");
  bean.setFilledUpInFlow(false);
  bean.setBillType("Z2");
  bean.setValidationService(getFrozenpowervalidservice());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuUnFrozenAction getUnFrozenAction(){
 if(context.get("unFrozenAction")!=null)
 return (nc.ui.ct.purdaily.action.PuUnFrozenAction)context.get("unFrozenAction");
  nc.ui.ct.purdaily.action.PuUnFrozenAction bean = new nc.ui.ct.purdaily.action.PuUnFrozenAction();
  context.put("unFrozenAction",bean);
  bean.setModel((nc.ui.pubapp.uif2app.model.BillManageModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setEditor((nc.ui.uif2.editor.IEditor)findBeanInUIF2BeanFactory("billFormEditor"));
  bean.setActionName("UNFREEZE");
  bean.setFilledUpInFlow(false);
  bean.setBillType("Z2");
  bean.setValidationService(getUnfrozenpowervalidservice());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuPayPlanNewAction getPayplannewAction(){
 if(context.get("payplannewAction")!=null)
 return (nc.ui.ct.purdaily.action.PuPayPlanNewAction)context.get("payplannewAction");
  nc.ui.ct.purdaily.action.PuPayPlanNewAction bean = new nc.ui.ct.purdaily.action.PuPayPlanNewAction();
  context.put("payplannewAction",bean);
  bean.setModel((nc.ui.pubapp.uif2app.model.BillManageModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setBillForm((nc.ui.pubapp.uif2app.view.BillForm)findBeanInUIF2BeanFactory("billFormEditor"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuPayPlanAction getPayplanAction(){
 if(context.get("payplanAction")!=null)
 return (nc.ui.ct.purdaily.action.PuPayPlanAction)context.get("payplanAction");
  nc.ui.ct.purdaily.action.PuPayPlanAction bean = new nc.ui.ct.purdaily.action.PuPayPlanAction();
  context.put("payplanAction",bean);
  bean.setModel((nc.ui.pubapp.uif2app.model.BillManageModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setBillForm((nc.ui.pubapp.uif2app.view.BillForm)findBeanInUIF2BeanFactory("billFormEditor"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuAppStateAction getAppStateAction(){
 if(context.get("appStateAction")!=null)
 return (nc.ui.ct.purdaily.action.PuAppStateAction)context.get("appStateAction");
  nc.ui.ct.purdaily.action.PuAppStateAction bean = new nc.ui.ct.purdaily.action.PuAppStateAction();
  context.put("appStateAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("ManageAppModel"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.action.CtChangeAction getChangeAction(){
 if(context.get("changeAction")!=null)
 return (nc.ui.ct.action.CtChangeAction)context.get("changeAction");
  nc.ui.ct.action.CtChangeAction bean = new nc.ui.ct.action.CtChangeAction();
  context.put("changeAction",bean);
  bean.setModel((nc.ui.pubapp.uif2app.model.BillManageModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setCardForm((nc.ui.pubapp.uif2app.view.ShowUpableBillForm)findBeanInUIF2BeanFactory("billFormEditor"));
  bean.setListForm((nc.ui.pubapp.uif2app.view.ShowUpableBillListView)findBeanInUIF2BeanFactory("listView"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuContrlAction getContrlAction(){
 if(context.get("contrlAction")!=null)
 return (nc.ui.ct.purdaily.action.PuContrlAction)context.get("contrlAction");
  nc.ui.ct.purdaily.action.PuContrlAction bean = new nc.ui.ct.purdaily.action.PuContrlAction();
  context.put("contrlAction",bean);
  bean.setModel((nc.ui.pubapp.uif2app.model.BillManageModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setCardForm((nc.ui.ct.purdaily.view.PurdailyBillForm)findBeanInUIF2BeanFactory("billFormEditor"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuBatchContrlAction getBatchContrlAction(){
 if(context.get("batchContrlAction")!=null)
 return (nc.ui.ct.purdaily.action.PuBatchContrlAction)context.get("batchContrlAction");
  nc.ui.ct.purdaily.action.PuBatchContrlAction bean = new nc.ui.ct.purdaily.action.PuBatchContrlAction();
  context.put("batchContrlAction",bean);
  bean.setModel((nc.ui.pubapp.uif2app.model.BillManageModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setCardForm((nc.ui.ct.purdaily.view.PurdailyBillForm)findBeanInUIF2BeanFactory("billFormEditor"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuModifyAction getModifyAction(){
 if(context.get("modifyAction")!=null)
 return (nc.ui.ct.purdaily.action.PuModifyAction)context.get("modifyAction");
  nc.ui.ct.purdaily.action.PuModifyAction bean = new nc.ui.ct.purdaily.action.PuModifyAction();
  context.put("modifyAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractAppModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setCardForm((nc.ui.pubapp.uif2app.view.ShowUpableBillForm)findBeanInUIF2BeanFactory("billFormEditor"));
  bean.setEditAction(getEditAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuTerminateAction getTerminateAction(){
 if(context.get("terminateAction")!=null)
 return (nc.ui.ct.purdaily.action.PuTerminateAction)context.get("terminateAction");
  nc.ui.ct.purdaily.action.PuTerminateAction bean = new nc.ui.ct.purdaily.action.PuTerminateAction();
  context.put("terminateAction",bean);
  bean.setModel((nc.ui.pubapp.uif2app.model.BillManageModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setEditor((nc.ui.uif2.editor.IEditor)findBeanInUIF2BeanFactory("billFormEditor"));
  bean.setCardForm((nc.ui.pubapp.uif2app.view.ShowUpableBillForm)findBeanInUIF2BeanFactory("billFormEditor"));
  bean.setActionName("TERMINATE");
  bean.setFilledUpInFlow(false);
  bean.setBillType("Z2");
  bean.setValidationService(getTerminatepowervalidservice());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuUnTerminateAction getUnTerminateAction(){
 if(context.get("unTerminateAction")!=null)
 return (nc.ui.ct.purdaily.action.PuUnTerminateAction)context.get("unTerminateAction");
  nc.ui.ct.purdaily.action.PuUnTerminateAction bean = new nc.ui.ct.purdaily.action.PuUnTerminateAction();
  context.put("unTerminateAction",bean);
  bean.setModel((nc.ui.pubapp.uif2app.model.BillManageModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setEditor((nc.ui.uif2.editor.IEditor)findBeanInUIF2BeanFactory("billFormEditor"));
  bean.setCardForm((nc.ui.pubapp.uif2app.view.ShowUpableBillForm)findBeanInUIF2BeanFactory("billFormEditor"));
  bean.setActionName("UNTERMINATE");
  bean.setFilledUpInFlow(false);
  bean.setBillType("Z2");
  bean.setValidationService(getUnterminatepowervalidservice());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuValidateAction getValidateAction(){
 if(context.get("validateAction")!=null)
 return (nc.ui.ct.purdaily.action.PuValidateAction)context.get("validateAction");
  nc.ui.ct.purdaily.action.PuValidateAction bean = new nc.ui.ct.purdaily.action.PuValidateAction();
  context.put("validateAction",bean);
  bean.setModel((nc.ui.pubapp.uif2app.model.BillManageModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setEditor((nc.ui.uif2.editor.IEditor)findBeanInUIF2BeanFactory("billFormEditor"));
  bean.setCardForm((nc.ui.pubapp.uif2app.view.ShowUpableBillForm)findBeanInUIF2BeanFactory("billFormEditor"));
  bean.setActionName("VALIDATE");
  bean.setFilledUpInFlow(false);
  bean.setBillType("Z2");
  bean.setValidationService(getValidatepowervalidservice());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PurdailyLinkQueryAction getLinkBillAction(){
 if(context.get("linkBillAction")!=null)
 return (nc.ui.ct.purdaily.action.PurdailyLinkQueryAction)context.get("linkBillAction");
  nc.ui.ct.purdaily.action.PurdailyLinkQueryAction bean = new nc.ui.ct.purdaily.action.PurdailyLinkQueryAction();
  context.put("linkBillAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setBillType("Z2");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.trade.billgraph.billflow.control.DefaultBillGraphListener getBillGraphListener(){
 if(context.get("billGraphListener")!=null)
 return (nc.ui.trade.billgraph.billflow.control.DefaultBillGraphListener)context.get("billGraphListener");
  nc.ui.trade.billgraph.billflow.control.DefaultBillGraphListener bean = new nc.ui.trade.billgraph.billflow.control.DefaultBillGraphListener();
  context.put("billGraphListener",bean);
  bean.setOpenMode(1);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuPayMnyStateAction getPayMnyStateAction(){
 if(context.get("payMnyStateAction")!=null)
 return (nc.ui.ct.purdaily.action.PuPayMnyStateAction)context.get("payMnyStateAction");
  nc.ui.ct.purdaily.action.PuPayMnyStateAction bean = new nc.ui.ct.purdaily.action.PuPayMnyStateAction();
  context.put("payMnyStateAction",bean);
  bean.setModel((nc.ui.pubapp.uif2app.model.BillManageModel)findBeanInUIF2BeanFactory("ManageAppModel"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuAddFromM28Action getPriceDeterAction(){
 if(context.get("priceDeterAction")!=null)
 return (nc.ui.ct.purdaily.action.PuAddFromM28Action)context.get("priceDeterAction");
  nc.ui.ct.purdaily.action.PuAddFromM28Action bean = new nc.ui.ct.purdaily.action.PuAddFromM28Action();
  context.put("priceDeterAction",bean);
  bean.setSourceBillType("28");
  bean.setSourceBillName(getI18nFB_1cde0a());
  bean.setFlowBillType(false);
  bean.setModel((nc.ui.uif2.model.AbstractAppModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setEditor((nc.ui.pubapp.uif2app.view.ShowUpableBillForm)findBeanInUIF2BeanFactory("billFormEditor"));
  bean.setTransferViewProcessor((nc.ui.pubapp.billref.dest.TransferViewProcessor)findBeanInUIF2BeanFactory("transferViewProcessor1"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_1cde0a(){
 if(context.get("nc.ui.uif2.I18nFB#1cde0a")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#1cde0a");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#1cde0a",bean);  bean.setResDir("4001002_0");
  bean.setResId("04001002-0506");
  bean.setDefaultValue("价格审批单");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#1cde0a",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

public nc.ui.ct.purdaily.action.PuUnValidateAction getUnValidateAction(){
 if(context.get("unValidateAction")!=null)
 return (nc.ui.ct.purdaily.action.PuUnValidateAction)context.get("unValidateAction");
  nc.ui.ct.purdaily.action.PuUnValidateAction bean = new nc.ui.ct.purdaily.action.PuUnValidateAction();
  context.put("unValidateAction",bean);
  bean.setModel((nc.ui.pubapp.uif2app.model.BillManageModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setEditor((nc.ui.uif2.editor.IEditor)findBeanInUIF2BeanFactory("billFormEditor"));
  bean.setActionName("UNVALIDATE");
  bean.setFilledUpInFlow(false);
  bean.setBillType("Z2");
  bean.setValidationService(getUnvalidatepowervalidservice());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.AddMenuAction getAddMenuAction(){
 if(context.get("addMenuAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.AddMenuAction)context.get("addMenuAction");
  nc.ui.pubapp.uif2app.actions.AddMenuAction bean = new nc.ui.pubapp.uif2app.actions.AddMenuAction();
  context.put("addMenuAction",bean);
  bean.setBillType("Z2");
  bean.setActions(getManagedList6());
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setPfAddInfoLoader(getPfAddInfoLoader());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList6(){  List list = new ArrayList();  list.add(getAddAction());  list.add(getSeparatorAction());  list.add(getAddFrom20Action());  list.add(getPriceDeterAction());  return list;}

public nc.ui.pubapp.uif2app.actions.PfAddInfoLoader getPfAddInfoLoader(){
 if(context.get("pfAddInfoLoader")!=null)
 return (nc.ui.pubapp.uif2app.actions.PfAddInfoLoader)context.get("pfAddInfoLoader");
  nc.ui.pubapp.uif2app.actions.PfAddInfoLoader bean = new nc.ui.pubapp.uif2app.actions.PfAddInfoLoader();
  context.put("pfAddInfoLoader",bean);
  bean.setBillType("Z2");
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("ManageAppModel"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.GroupAction getCommitMenuAction(){
 if(context.get("commitMenuAction")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("commitMenuAction");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("commitMenuAction",bean);
  bean.setCode("commitMenuAction");
  bean.setActions(getManagedList7());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList7(){  List list = new ArrayList();  list.add(getCommitAction());  list.add(getTakeBackAction());  return list;}

public nc.funcnode.ui.action.GroupAction getApproveMenuAction(){
 if(context.get("approveMenuAction")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("approveMenuAction");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("approveMenuAction",bean);
  bean.setCode("approveMenuAction");
  bean.setActions(getManagedList8());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList8(){  List list = new ArrayList();  list.add(getApproveAction());  list.add(getUnApproveAction());  list.add(getSeparatorAction());  list.add(getAppStateAction());  return list;}

public nc.ui.ct.action.TransactMenuAction getTransactMenuAction(){
 if(context.get("transactMenuAction")!=null)
 return (nc.ui.ct.action.TransactMenuAction)context.get("transactMenuAction");
  nc.ui.ct.action.TransactMenuAction bean = new nc.ui.ct.action.TransactMenuAction();
  context.put("transactMenuAction",bean);
  bean.setCode("transactMenuAction");
  bean.setActions(getManagedList9());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList9(){  List list = new ArrayList();  list.add(getValidateAction());  list.add(getUnValidateAction());  list.add(getFrozenAction());  list.add(getUnFrozenAction());  list.add(getTerminateAction());  list.add(getUnTerminateAction());  list.add(getModifyAction());  list.add(getModiDeleteAction());  return list;}

public nc.ui.ct.action.AsstQueryMenuAction getAsstQueryMenuAction(){
 if(context.get("asstQueryMenuAction")!=null)
 return (nc.ui.ct.action.AsstQueryMenuAction)context.get("asstQueryMenuAction");
  nc.ui.ct.action.AsstQueryMenuAction bean = new nc.ui.ct.action.AsstQueryMenuAction();
  context.put("asstQueryMenuAction",bean);
  bean.setCode("asstQueryMenuAction");
  bean.setActions(getManagedList10());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList10(){  List list = new ArrayList();  list.add(getLinkBillAction());  list.add(getSeparatorAction());  list.add(getPayMnyStateAction());  list.add(getSeparatorAction());  list.add(getLinkCtPriceInfoAction());  return list;}

public nc.ui.ct.action.AssistMenuAction getAssistMenuAction(){
 if(context.get("assistMenuAction")!=null)
 return (nc.ui.ct.action.AssistMenuAction)context.get("assistMenuAction");
  nc.ui.ct.action.AssistMenuAction bean = new nc.ui.ct.action.AssistMenuAction();
  context.put("assistMenuAction",bean);
  bean.setCode("assistMenuAction");
  bean.setActions(getManagedList11());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList11(){  List list = new ArrayList();  list.add(getContrlAction());  list.add(getBatchContrlAction());  list.add(getSeparatorAction());  list.add(getObadocmanageAction());  list.add(getSeparatorAction());  list.add(getAccessoriesAction());  list.add(getSeparatorAction());  list.add(getChangeAction());  return list;}

public nc.funcnode.ui.action.GroupAction getPrintMenuAction(){
 if(context.get("printMenuAction")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("printMenuAction");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("printMenuAction",bean);
  bean.setCode("print");
  bean.setActions(getManagedList12());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList12(){  List list = new ArrayList();  list.add(getPrintAction());  list.add(getPreviewAction());  list.add(getOutputAction());  list.add(getPrintCountQueryAction());  return list;}

public nc.ui.pubapp.uif2app.actions.OutputAction getOutputAction(){
 if(context.get("outputAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.OutputAction)context.get("outputAction");
  nc.ui.pubapp.uif2app.actions.OutputAction bean = new nc.ui.pubapp.uif2app.actions.OutputAction();
  context.put("outputAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setParent((java.awt.Container)findBeanInUIF2BeanFactory("billFormEditor"));
  bean.setBeforePrintDataProcess(getPrintProcessor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.FileDocManageAction getAccessoriesAction(){
 if(context.get("accessoriesAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.FileDocManageAction)context.get("accessoriesAction");
  nc.ui.pubapp.uif2app.actions.FileDocManageAction bean = new nc.ui.pubapp.uif2app.actions.FileDocManageAction();
  context.put("accessoriesAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("ManageAppModel"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.oba.action.bill.OBADocManageBillAction getObadocmanageAction(){
 if(context.get("obadocmanageAction")!=null)
 return (nc.ui.oba.action.bill.OBADocManageBillAction)context.get("obadocmanageAction");
  nc.ui.oba.action.bill.OBADocManageBillAction bean = new nc.ui.oba.action.bill.OBADocManageBillAction();
  context.put("obadocmanageAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("ManageAppModel"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuAddFromSourceAction getAddFrom20Action(){
 if(context.get("addFrom20Action")!=null)
 return (nc.ui.ct.purdaily.action.PuAddFromSourceAction)context.get("addFrom20Action");
  nc.ui.ct.purdaily.action.PuAddFromSourceAction bean = new nc.ui.ct.purdaily.action.PuAddFromSourceAction();
  context.put("addFrom20Action",bean);
  bean.setSourceBillType("20");
  bean.setSourceBillName(getI18nFB_1eef872());
  bean.setFlowBillType(false);
  bean.setModel((nc.ui.uif2.model.AbstractAppModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setEditor((nc.ui.pubapp.uif2app.view.ShowUpableBillForm)findBeanInUIF2BeanFactory("billFormEditor"));
  bean.setTransferViewProcessor((nc.ui.pubapp.billref.dest.TransferViewProcessor)findBeanInUIF2BeanFactory("transferViewProcessor1"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_1eef872(){
 if(context.get("nc.ui.uif2.I18nFB#1eef872")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#1eef872");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#1eef872",bean);  bean.setResDir("4001002_0");
  bean.setResId("04001002-0496");
  bean.setDefaultValue("请购单");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#1eef872",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

public nc.ui.pubapp.pub.power.PowerValidateService getComitpowervalidservice(){
 if(context.get("comitpowervalidservice")!=null)
 return (nc.ui.pubapp.pub.power.PowerValidateService)context.get("comitpowervalidservice");
  nc.ui.pubapp.pub.power.PowerValidateService bean = new nc.ui.pubapp.pub.power.PowerValidateService();
  context.put("comitpowervalidservice",bean);
  bean.setActionCode("commit");
  bean.setBillCodeFiledName("vbillcode");
  bean.setPermissionCode("Z2");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.pub.power.PowerValidateService getPowervalidservice(){
 if(context.get("powervalidservice")!=null)
 return (nc.ui.pubapp.pub.power.PowerValidateService)context.get("powervalidservice");
  nc.ui.pubapp.pub.power.PowerValidateService bean = new nc.ui.pubapp.pub.power.PowerValidateService();
  context.put("powervalidservice",bean);
  bean.setActionCode("approve");
  bean.setBillCodeFiledName("vbillcode");
  bean.setPermissionCode("Z2");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.pub.power.PowerValidateService getUnapprovepowervalidservice(){
 if(context.get("unapprovepowervalidservice")!=null)
 return (nc.ui.pubapp.pub.power.PowerValidateService)context.get("unapprovepowervalidservice");
  nc.ui.pubapp.pub.power.PowerValidateService bean = new nc.ui.pubapp.pub.power.PowerValidateService();
  context.put("unapprovepowervalidservice",bean);
  bean.setActionCode("unapprove");
  bean.setBillCodeFiledName("vbillcode");
  bean.setPermissionCode("Z2");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.pub.power.PowerValidateService getUncomitpowervalidservice(){
 if(context.get("uncomitpowervalidservice")!=null)
 return (nc.ui.pubapp.pub.power.PowerValidateService)context.get("uncomitpowervalidservice");
  nc.ui.pubapp.pub.power.PowerValidateService bean = new nc.ui.pubapp.pub.power.PowerValidateService();
  context.put("uncomitpowervalidservice",bean);
  bean.setActionCode("uncommit");
  bean.setBillCodeFiledName("vbillcode");
  bean.setPermissionCode("Z2");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.pub.power.PowerValidateService getDelpowervalidservice(){
 if(context.get("delpowervalidservice")!=null)
 return (nc.ui.pubapp.pub.power.PowerValidateService)context.get("delpowervalidservice");
  nc.ui.pubapp.pub.power.PowerValidateService bean = new nc.ui.pubapp.pub.power.PowerValidateService();
  context.put("delpowervalidservice",bean);
  bean.setActionCode("delete");
  bean.setBillCodeFiledName("vbillcode");
  bean.setPermissionCode("Z2");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.pub.power.PowerValidateService getFrozenpowervalidservice(){
 if(context.get("frozenpowervalidservice")!=null)
 return (nc.ui.pubapp.pub.power.PowerValidateService)context.get("frozenpowervalidservice");
  nc.ui.pubapp.pub.power.PowerValidateService bean = new nc.ui.pubapp.pub.power.PowerValidateService();
  context.put("frozenpowervalidservice",bean);
  bean.setActionCode("frozen");
  bean.setBillCodeFiledName("vbillcode");
  bean.setPermissionCode("Z2");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.pub.power.PowerValidateService getUnfrozenpowervalidservice(){
 if(context.get("unfrozenpowervalidservice")!=null)
 return (nc.ui.pubapp.pub.power.PowerValidateService)context.get("unfrozenpowervalidservice");
  nc.ui.pubapp.pub.power.PowerValidateService bean = new nc.ui.pubapp.pub.power.PowerValidateService();
  context.put("unfrozenpowervalidservice",bean);
  bean.setActionCode("unfrozen");
  bean.setBillCodeFiledName("vbillcode");
  bean.setPermissionCode("Z2");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.pub.power.PowerValidateService getTerminatepowervalidservice(){
 if(context.get("terminatepowervalidservice")!=null)
 return (nc.ui.pubapp.pub.power.PowerValidateService)context.get("terminatepowervalidservice");
  nc.ui.pubapp.pub.power.PowerValidateService bean = new nc.ui.pubapp.pub.power.PowerValidateService();
  context.put("terminatepowervalidservice",bean);
  bean.setActionCode("terminate");
  bean.setBillCodeFiledName("vbillcode");
  bean.setPermissionCode("Z2");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.pub.power.PowerValidateService getUnterminatepowervalidservice(){
 if(context.get("unterminatepowervalidservice")!=null)
 return (nc.ui.pubapp.pub.power.PowerValidateService)context.get("unterminatepowervalidservice");
  nc.ui.pubapp.pub.power.PowerValidateService bean = new nc.ui.pubapp.pub.power.PowerValidateService();
  context.put("unterminatepowervalidservice",bean);
  bean.setActionCode("unterminate");
  bean.setBillCodeFiledName("vbillcode");
  bean.setPermissionCode("Z2");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.pub.power.PowerValidateService getValidatepowervalidservice(){
 if(context.get("validatepowervalidservice")!=null)
 return (nc.ui.pubapp.pub.power.PowerValidateService)context.get("validatepowervalidservice");
  nc.ui.pubapp.pub.power.PowerValidateService bean = new nc.ui.pubapp.pub.power.PowerValidateService();
  context.put("validatepowervalidservice",bean);
  bean.setActionCode("validate");
  bean.setBillCodeFiledName("vbillcode");
  bean.setPermissionCode("Z2");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.pub.power.PowerValidateService getUnvalidatepowervalidservice(){
 if(context.get("unvalidatepowervalidservice")!=null)
 return (nc.ui.pubapp.pub.power.PowerValidateService)context.get("unvalidatepowervalidservice");
  nc.ui.pubapp.pub.power.PowerValidateService bean = new nc.ui.pubapp.pub.power.PowerValidateService();
  context.put("unvalidatepowervalidservice",bean);
  bean.setActionCode("unvalidate");
  bean.setBillCodeFiledName("vbillcode");
  bean.setPermissionCode("Z2");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.scmpub.action.SCMPrintCountQueryAction getPrintCountQueryAction(){
 if(context.get("printCountQueryAction")!=null)
 return (nc.ui.scmpub.action.SCMPrintCountQueryAction)context.get("printCountQueryAction");
  nc.ui.scmpub.action.SCMPrintCountQueryAction bean = new nc.ui.scmpub.action.SCMPrintCountQueryAction();
  context.put("printCountQueryAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractAppModel)findBeanInUIF2BeanFactory("ManageAppModel"));
  bean.setBilldateFieldName("dbilldate");
  bean.setBillType("Z2");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.action.CtCloseHistoryDlgAction getClosehistoryAction(){
 if(context.get("closehistoryAction")!=null)
 return (nc.ui.ct.action.CtCloseHistoryDlgAction)context.get("closehistoryAction");
  nc.ui.ct.action.CtCloseHistoryDlgAction bean = new nc.ui.ct.action.CtCloseHistoryDlgAction();
  context.put("closehistoryAction",bean);
  bean.setView((nc.ui.pubapp.uif2app.view.ShowUpableBillListView)findBeanInUIF2BeanFactory("listViewHistory"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getActionsOfHistory(){
 if(context.get("actionsOfHistory")!=null)
 return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("actionsOfHistory");
  nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer((nc.ui.uif2.components.ITabbedPaneAwareComponent)findBeanInUIF2BeanFactory("listViewHistory"));  context.put("actionsOfHistory",bean);
  bean.setActions(getManagedList13());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList13(){  List list = new ArrayList();  list.add(getClosehistoryAction());  return list;}

}
