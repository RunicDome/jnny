package nc.ui.ct.purdaily.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class purdaily_config extends AbstractJavaBeanDefinition{
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
  nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getListView());  context.put("actionsOfList",bean);
  bean.setActions(getManagedList1());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList1(){  List list = new ArrayList();  list.add(getAddMenuAction());  list.add(getEditAction());  list.add(getDeleteAction());  list.add(getCopyAction());  list.add(getSeparatorAction());  list.add(getQueryAction());  list.add(getRefreshAction());  list.add(getSeparatorAction());  list.add(getCommitMenuAction());  list.add(getApproveMenuAction());  list.add(getTransactMenuAction());  list.add(getAssistMenuAction());  list.add(getSeparatorAction());  list.add(getAsstQueryMenuAction());  list.add(getSeparatorAction());  list.add(getPayplanAction());  list.add(getSeparatorAction());  list.add(getPayplannewAction());  list.add(getSeparatorAction());  list.add(getPrintMenuAction());  list.add(getGetFileAction());  return list;}

public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getActionsOfCard(){
 if(context.get("actionsOfCard")!=null)
 return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("actionsOfCard");
  nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getBillFormEditor());  context.put("actionsOfCard",bean);
  bean.setModel(getManageAppModel());
  bean.setActions(getManagedList2());
  bean.setEditActions(getManagedList3());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList2(){  List list = new ArrayList();  list.add(getAddMenuAction());  list.add(getEditAction());  list.add(getDeleteAction());  list.add(getCopyAction());  list.add(getSeparatorAction());  list.add(getQueryAction());  list.add(getCardRefreshAction());  list.add(getSeparatorAction());  list.add(getCommitMenuAction());  list.add(getApproveMenuAction());  list.add(getTransactMenuAction());  list.add(getAssistMenuAction());  list.add(getSeparatorAction());  list.add(getAsstQueryMenuAction());  list.add(getSeparatorAction());  list.add(getPayplanAction());  list.add(getSeparatorAction());  list.add(getPayplannewAction());  list.add(getSeparatorAction());  list.add(getPrintMenuAction());  list.add(getGetFileAction());  return list;}

private List getManagedList3(){  List list = new ArrayList();  list.add(getSaveAction());  list.add(getSaveApproveAction());  list.add(getSeparatorAction());  list.add(getCancelAction());  list.add(getSeparatorAction());  list.add(getRelatingCtAction());  list.add(getSeparatorAction());  list.add(getLinkCtPriceInfoAction());  return list;}

public nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor getFormInterceptor(){
 if(context.get("formInterceptor")!=null)
 return (nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor)context.get("formInterceptor");
  nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor();
  context.put("formInterceptor",bean);
  bean.setShowUpComponent(getBillFormEditor());
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
  bean.setCardForm(getBillFormEditor());
  bean.setModel(getManageAppModel());
  bean.setInterceptor(getCompositeActionInterceptor_14c8710());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor getCompositeActionInterceptor_14c8710(){
 if(context.get("nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor#14c8710")!=null)
 return (nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor)context.get("nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor#14c8710");
  nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor();
  context.put("nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor#14c8710",bean);
  bean.setInterceptors(getManagedList4());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList4(){  List list = new ArrayList();  list.add(getShowUpComponentInterceptor_65bcc6());  return list;}

private nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor getShowUpComponentInterceptor_65bcc6(){
 if(context.get("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#65bcc6")!=null)
 return (nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor)context.get("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#65bcc6");
  nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor();
  context.put("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#65bcc6",bean);
  bean.setShowUpComponent(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuRelatingCtAction getRelatingCtAction(){
 if(context.get("relatingCtAction")!=null)
 return (nc.ui.ct.purdaily.action.PuRelatingCtAction)context.get("relatingCtAction");
  nc.ui.ct.purdaily.action.PuRelatingCtAction bean = new nc.ui.ct.purdaily.action.PuRelatingCtAction();
  context.put("relatingCtAction",bean);
  bean.setModel(getManageAppModel());
  bean.setCardForm(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuLinkCtPriceInfoAction getLinkCtPriceInfoAction(){
 if(context.get("linkCtPriceInfoAction")!=null)
 return (nc.ui.ct.purdaily.action.PuLinkCtPriceInfoAction)context.get("linkCtPriceInfoAction");
  nc.ui.ct.purdaily.action.PuLinkCtPriceInfoAction bean = new nc.ui.ct.purdaily.action.PuLinkCtPriceInfoAction();
  context.put("linkCtPriceInfoAction",bean);
  bean.setModel(getManageAppModel());
  bean.setCardForm(getBillFormEditor());
  bean.setListView(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuEditAction getEditAction(){
 if(context.get("editAction")!=null)
 return (nc.ui.ct.purdaily.action.PuEditAction)context.get("editAction");
  nc.ui.ct.purdaily.action.PuEditAction bean = new nc.ui.ct.purdaily.action.PuEditAction();
  context.put("editAction",bean);
  bean.setCardForm(getBillFormEditor());
  bean.setModel(getManageAppModel());
  bean.setInterceptor(getShowUpComponentInterceptor_1375522());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor getShowUpComponentInterceptor_1375522(){
 if(context.get("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#1375522")!=null)
 return (nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor)context.get("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#1375522");
  nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor();
  context.put("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#1375522",bean);
  bean.setShowUpComponent(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.CGHTFileAction getGetFileAction(){
 if(context.get("getFileAction")!=null)
 return (nc.ui.ct.purdaily.action.CGHTFileAction)context.get("getFileAction");
  nc.ui.ct.purdaily.action.CGHTFileAction bean = new nc.ui.ct.purdaily.action.CGHTFileAction();
  context.put("getFileAction",bean);
  bean.setModel(getManageAppModel());
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
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
  bean.setQryCondDLGInitializer(getPurQryCondDLGInitializer());
  bean.setTemplateContainer(getQueryTemplateContainer());
  bean.setShowUpComponent(getListView());
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
  bean.setModel(getManageAppModel());
  bean.setEditor(getBillFormEditor());
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
  bean.setSingleBillService(getPuModiDeleteService_1030318());
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.purdaily.service.PuModiDeleteService getPuModiDeleteService_1030318(){
 if(context.get("nc.ui.ct.purdaily.service.PuModiDeleteService#1030318")!=null)
 return (nc.ui.ct.purdaily.service.PuModiDeleteService)context.get("nc.ui.ct.purdaily.service.PuModiDeleteService#1030318");
  nc.ui.ct.purdaily.service.PuModiDeleteService bean = new nc.ui.ct.purdaily.service.PuModiDeleteService();
  context.put("nc.ui.ct.purdaily.service.PuModiDeleteService#1030318",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuSaveAction getSaveAction(){
 if(context.get("saveAction")!=null)
 return (nc.ui.ct.purdaily.action.PuSaveAction)context.get("saveAction");
  nc.ui.ct.purdaily.action.PuSaveAction bean = new nc.ui.ct.purdaily.action.PuSaveAction();
  context.put("saveAction",bean);
  bean.setModel(getManageAppModel());
  bean.setEditor(getBillFormEditor());
  bean.setCardForm(getBillFormEditor());
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
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction getRefreshAction(){
 if(context.get("refreshAction")!=null)
 return (nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction)context.get("refreshAction");
  nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction bean = new nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction();
  context.put("refreshAction",bean);
  bean.setDataManager(getModelDataManager());
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.RefreshSingleAction getCardRefreshAction(){
 if(context.get("cardRefreshAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.RefreshSingleAction)context.get("cardRefreshAction");
  nc.ui.pubapp.uif2app.actions.RefreshSingleAction bean = new nc.ui.pubapp.uif2app.actions.RefreshSingleAction();
  context.put("cardRefreshAction",bean);
  bean.setModel(getManageAppModel());
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
  bean.setModel(getManageAppModel());
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
  bean.setModel(getManageAppModel());
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
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.CGHTPuCommitScriptAction getCommitAction(){
 if(context.get("commitAction")!=null)
 return (nc.ui.ct.purdaily.action.CGHTPuCommitScriptAction)context.get("commitAction");
  nc.ui.ct.purdaily.action.CGHTPuCommitScriptAction bean = new nc.ui.ct.purdaily.action.CGHTPuCommitScriptAction();
  context.put("commitAction",bean);
  bean.setModel(getManageAppModel());
  bean.setEditor(getBillFormEditor());
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
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuTakeBackAction getTakeBackAction(){
 if(context.get("takeBackAction")!=null)
 return (nc.ui.ct.purdaily.action.PuTakeBackAction)context.get("takeBackAction");
  nc.ui.ct.purdaily.action.PuTakeBackAction bean = new nc.ui.ct.purdaily.action.PuTakeBackAction();
  context.put("takeBackAction",bean);
  bean.setModel(getManageAppModel());
  bean.setEditor(getBillFormEditor());
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
  bean.setModel(getManageAppModel());
  bean.setEditor(getBillFormEditor());
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
  bean.setModel(getManageAppModel());
  bean.setEditor(getBillFormEditor());
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
  bean.setModel(getManageAppModel());
  bean.setEditor(getBillFormEditor());
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
  bean.setModel(getManageAppModel());
  bean.setEditor(getBillFormEditor());
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
  bean.setModel(getManageAppModel());
  bean.setEditor(getBillFormEditor());
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
  bean.setModel(getManageAppModel());
  bean.setBillForm(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuPayPlanAction getPayplanAction(){
 if(context.get("payplanAction")!=null)
 return (nc.ui.ct.purdaily.action.PuPayPlanAction)context.get("payplanAction");
  nc.ui.ct.purdaily.action.PuPayPlanAction bean = new nc.ui.ct.purdaily.action.PuPayPlanAction();
  context.put("payplanAction",bean);
  bean.setModel(getManageAppModel());
  bean.setBillForm(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuAppStateAction getAppStateAction(){
 if(context.get("appStateAction")!=null)
 return (nc.ui.ct.purdaily.action.PuAppStateAction)context.get("appStateAction");
  nc.ui.ct.purdaily.action.PuAppStateAction bean = new nc.ui.ct.purdaily.action.PuAppStateAction();
  context.put("appStateAction",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.action.CtChangeAction getChangeAction(){
 if(context.get("changeAction")!=null)
 return (nc.ui.ct.action.CtChangeAction)context.get("changeAction");
  nc.ui.ct.action.CtChangeAction bean = new nc.ui.ct.action.CtChangeAction();
  context.put("changeAction",bean);
  bean.setModel(getManageAppModel());
  bean.setCardForm(getBillFormEditor());
  bean.setListForm(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuContrlAction getContrlAction(){
 if(context.get("contrlAction")!=null)
 return (nc.ui.ct.purdaily.action.PuContrlAction)context.get("contrlAction");
  nc.ui.ct.purdaily.action.PuContrlAction bean = new nc.ui.ct.purdaily.action.PuContrlAction();
  context.put("contrlAction",bean);
  bean.setModel(getManageAppModel());
  bean.setCardForm(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuBatchContrlAction getBatchContrlAction(){
 if(context.get("batchContrlAction")!=null)
 return (nc.ui.ct.purdaily.action.PuBatchContrlAction)context.get("batchContrlAction");
  nc.ui.ct.purdaily.action.PuBatchContrlAction bean = new nc.ui.ct.purdaily.action.PuBatchContrlAction();
  context.put("batchContrlAction",bean);
  bean.setModel(getManageAppModel());
  bean.setCardForm(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.action.PuModifyAction getModifyAction(){
 if(context.get("modifyAction")!=null)
 return (nc.ui.ct.purdaily.action.PuModifyAction)context.get("modifyAction");
  nc.ui.ct.purdaily.action.PuModifyAction bean = new nc.ui.ct.purdaily.action.PuModifyAction();
  context.put("modifyAction",bean);
  bean.setModel(getManageAppModel());
  bean.setCardForm(getBillFormEditor());
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
  bean.setModel(getManageAppModel());
  bean.setEditor(getBillFormEditor());
  bean.setCardForm(getBillFormEditor());
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
  bean.setModel(getManageAppModel());
  bean.setEditor(getBillFormEditor());
  bean.setCardForm(getBillFormEditor());
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
  bean.setModel(getManageAppModel());
  bean.setEditor(getBillFormEditor());
  bean.setCardForm(getBillFormEditor());
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
  bean.setModel(getManageAppModel());
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
  bean.setModel(getManageAppModel());
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
  bean.setSourceBillName(getI18nFB_c0400d());
  bean.setFlowBillType(false);
  bean.setModel(getManageAppModel());
  bean.setEditor(getBillFormEditor());
  bean.setTransferViewProcessor(getTransferViewProcessor1());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_c0400d(){
 if(context.get("nc.ui.uif2.I18nFB#c0400d")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#c0400d");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#c0400d",bean);  bean.setResDir("4001002_0");
  bean.setResId("04001002-0506");
  bean.setDefaultValue("价格审批单");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#c0400d",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

public nc.ui.ct.purdaily.action.PuUnValidateAction getUnValidateAction(){
 if(context.get("unValidateAction")!=null)
 return (nc.ui.ct.purdaily.action.PuUnValidateAction)context.get("unValidateAction");
  nc.ui.ct.purdaily.action.PuUnValidateAction bean = new nc.ui.ct.purdaily.action.PuUnValidateAction();
  context.put("unValidateAction",bean);
  bean.setModel(getManageAppModel());
  bean.setEditor(getBillFormEditor());
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
  bean.setModel(getManageAppModel());
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
  bean.setModel(getManageAppModel());
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
  bean.setModel(getManageAppModel());
  bean.setParent(getBillFormEditor());
  bean.setBeforePrintDataProcess(getPrintProcessor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pm.action.NewPMAttachmentAction getAccessoriesAction(){
 if(context.get("accessoriesAction")!=null)
 return (nc.ui.pm.action.NewPMAttachmentAction)context.get("accessoriesAction");
  nc.ui.pm.action.NewPMAttachmentAction bean = new nc.ui.pm.action.NewPMAttachmentAction();
  context.put("accessoriesAction",bean);
  bean.setModel(getManageAppModel());
  bean.setApproveNoDelete(true);
  bean.setCanUpload(true);
  bean.setCanDelete(true);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.oba.action.bill.OBADocManageBillAction getObadocmanageAction(){
 if(context.get("obadocmanageAction")!=null)
 return (nc.ui.oba.action.bill.OBADocManageBillAction)context.get("obadocmanageAction");
  nc.ui.oba.action.bill.OBADocManageBillAction bean = new nc.ui.oba.action.bill.OBADocManageBillAction();
  context.put("obadocmanageAction",bean);
  bean.setModel(getManageAppModel());
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
  bean.setSourceBillName(getI18nFB_1ef0929());
  bean.setFlowBillType(false);
  bean.setModel(getManageAppModel());
  bean.setEditor(getBillFormEditor());
  bean.setTransferViewProcessor(getTransferViewProcessor1());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_1ef0929(){
 if(context.get("nc.ui.uif2.I18nFB#1ef0929")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#1ef0929");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#1ef0929",bean);  bean.setResDir("4001002_0");
  bean.setResId("04001002-0496");
  bean.setDefaultValue("请购单");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#1ef0929",product);
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
  bean.setModel(getManageAppModel());
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
  bean.setView(getListViewHistory());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getActionsOfHistory(){
 if(context.get("actionsOfHistory")!=null)
 return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("actionsOfHistory");
  nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getListViewHistory());  context.put("actionsOfHistory",bean);
  bean.setActions(getManagedList13());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList13(){  List list = new ArrayList();  list.add(getClosehistoryAction());  return list;}

public nc.ui.pubapp.uif2app.model.AppEventHandlerMediator getAppEventHandlerMediator(){
 if(context.get("appEventHandlerMediator")!=null)
 return (nc.ui.pubapp.uif2app.model.AppEventHandlerMediator)context.get("appEventHandlerMediator");
  nc.ui.pubapp.uif2app.model.AppEventHandlerMediator bean = new nc.ui.pubapp.uif2app.model.AppEventHandlerMediator();
  context.put("appEventHandlerMediator",bean);
  bean.setModel(getManageAppModel());
  bean.setHandlerMap(getManagedMap0());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private Map getManagedMap0(){  Map map = new HashMap();  map.put("nc.ui.pubapp.uif2app.event.list.ListHeadRowChangedEvent",getManagedList14());  map.put("nc.ui.pubapp.uif2app.event.OrgChangedEvent",getManagedList15());  map.put("nc.ui.pubapp.uif2app.event.card.CardHeadTailBeforeEditEvent",getManagedList16());  map.put("nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent",getManagedList17());  map.put("nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent",getManagedList18());  map.put("nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent",getManagedList19());  return map;}

private List getManagedList14(){  List list = new ArrayList();  list.add(getHeadrowchghandler());  return list;}

private List getManagedList15(){  List list = new ArrayList();  list.add(getMainorgchghandler());  return list;}

private List getManagedList16(){  List list = new ArrayList();  list.add(getBefore_headtail_edithandler());  return list;}

private List getManagedList17(){  List list = new ArrayList();  list.add(getAfter_headtail_edithandler());  return list;}

private List getManagedList18(){  List list = new ArrayList();  list.add(getBefore_body_edithandler());  return list;}

private List getManagedList19(){  List list = new ArrayList();  list.add(getAfter_body_edithandler());  list.add(getRelationCalculate());  return list;}

public nc.ui.ct.editor.rowchange.HeadRowChangeHandler getHeadrowchghandler(){
 if(context.get("headrowchghandler")!=null)
 return (nc.ui.ct.editor.rowchange.HeadRowChangeHandler)context.get("headrowchghandler");
  nc.ui.ct.editor.rowchange.HeadRowChangeHandler bean = new nc.ui.ct.editor.rowchange.HeadRowChangeHandler();
  context.put("headrowchghandler",bean);
  bean.setTableCode("pk_ct_pu_b");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.editor.org.PuOrgChangedEventHandler getMainorgchghandler(){
 if(context.get("mainorgchghandler")!=null)
 return (nc.ui.ct.purdaily.editor.org.PuOrgChangedEventHandler)context.get("mainorgchghandler");
  nc.ui.ct.purdaily.editor.org.PuOrgChangedEventHandler bean = new nc.ui.ct.purdaily.editor.org.PuOrgChangedEventHandler();
  context.put("mainorgchghandler",bean);
  bean.setCardForm(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.editor.before.PuHeadTailBeforeEventHandler getBefore_headtail_edithandler(){
 if(context.get("before_headtail_edithandler")!=null)
 return (nc.ui.ct.purdaily.editor.before.PuHeadTailBeforeEventHandler)context.get("before_headtail_edithandler");
  nc.ui.ct.purdaily.editor.before.PuHeadTailBeforeEventHandler bean = new nc.ui.ct.purdaily.editor.before.PuHeadTailBeforeEventHandler();
  context.put("before_headtail_edithandler",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.editor.after.PuHeadTailAfterEventHandler getAfter_headtail_edithandler(){
 if(context.get("after_headtail_edithandler")!=null)
 return (nc.ui.ct.purdaily.editor.after.PuHeadTailAfterEventHandler)context.get("after_headtail_edithandler");
  nc.ui.ct.purdaily.editor.after.PuHeadTailAfterEventHandler bean = new nc.ui.ct.purdaily.editor.after.PuHeadTailAfterEventHandler();
  context.put("after_headtail_edithandler",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.editor.before.PuBodyBeforeEventHandler getBefore_body_edithandler(){
 if(context.get("before_body_edithandler")!=null)
 return (nc.ui.ct.purdaily.editor.before.PuBodyBeforeEventHandler)context.get("before_body_edithandler");
  nc.ui.ct.purdaily.editor.before.PuBodyBeforeEventHandler bean = new nc.ui.ct.purdaily.editor.before.PuBodyBeforeEventHandler();
  context.put("before_body_edithandler",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.editor.after.PuBodyAfterEventHandler getAfter_body_edithandler(){
 if(context.get("after_body_edithandler")!=null)
 return (nc.ui.ct.purdaily.editor.after.PuBodyAfterEventHandler)context.get("after_body_edithandler");
  nc.ui.ct.purdaily.editor.after.PuBodyAfterEventHandler bean = new nc.ui.ct.purdaily.editor.after.PuBodyAfterEventHandler();
  context.put("after_body_edithandler",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.util.RelationCalculate getRelationCalculate(){
 if(context.get("relationCalculate")!=null)
 return (nc.ui.ct.util.RelationCalculate)context.get("relationCalculate");
  nc.ui.ct.util.RelationCalculate bean = new nc.ui.ct.util.RelationCalculate();
  context.put("relationCalculate",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener getInitDataListener(){
 if(context.get("InitDataListener")!=null)
 return (nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener)context.get("InitDataListener");
  nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener bean = new nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener();
  context.put("InitDataListener",bean);
  bean.setContext(getContext());
  bean.setModel(getManageAppModel());
  bean.setQueryAction(getQueryAction());
  bean.setVoClassName("nc.vo.ct.purdaily.entity.AggCtPuVO");
  bean.setAutoShowUpComponent(getBillFormEditor());
  bean.setProcessorMap(getManagedMap1());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private Map getManagedMap1(){  Map map = new HashMap();  map.put("28",getCtPuInitDataProcessor_11d7510());  map.put("3",getCTLinkQueryInitDataProcessor_f08286());  map.put("47",getCtPuInitDataProcessorForEC_952e0());  map.put("8",getCtPuOpenNodeInitDataProcessor_2011fe());  return map;}

private nc.ui.ct.purdaily.billref.CtPuInitDataProcessor getCtPuInitDataProcessor_11d7510(){
 if(context.get("nc.ui.ct.purdaily.billref.CtPuInitDataProcessor#11d7510")!=null)
 return (nc.ui.ct.purdaily.billref.CtPuInitDataProcessor)context.get("nc.ui.ct.purdaily.billref.CtPuInitDataProcessor#11d7510");
  nc.ui.ct.purdaily.billref.CtPuInitDataProcessor bean = new nc.ui.ct.purdaily.billref.CtPuInitDataProcessor();
  context.put("nc.ui.ct.purdaily.billref.CtPuInitDataProcessor#11d7510",bean);
  bean.setTransferProcessor(getTransferViewProcessor1());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.view.CTLinkQueryInitDataProcessor getCTLinkQueryInitDataProcessor_f08286(){
 if(context.get("nc.ui.ct.view.CTLinkQueryInitDataProcessor#f08286")!=null)
 return (nc.ui.ct.view.CTLinkQueryInitDataProcessor)context.get("nc.ui.ct.view.CTLinkQueryInitDataProcessor#f08286");
  nc.ui.ct.view.CTLinkQueryInitDataProcessor bean = new nc.ui.ct.view.CTLinkQueryInitDataProcessor();
  context.put("nc.ui.ct.view.CTLinkQueryInitDataProcessor#f08286",bean);
  bean.setFunNodeInitDataListener(getInitDataListener());
  bean.setView(getListViewHistory());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.purdaily.billref.CtPuInitDataProcessorForEC getCtPuInitDataProcessorForEC_952e0(){
 if(context.get("nc.ui.ct.purdaily.billref.CtPuInitDataProcessorForEC#952e0")!=null)
 return (nc.ui.ct.purdaily.billref.CtPuInitDataProcessorForEC)context.get("nc.ui.ct.purdaily.billref.CtPuInitDataProcessorForEC#952e0");
  nc.ui.ct.purdaily.billref.CtPuInitDataProcessorForEC bean = new nc.ui.ct.purdaily.billref.CtPuInitDataProcessorForEC();
  context.put("nc.ui.ct.purdaily.billref.CtPuInitDataProcessorForEC#952e0",bean);
  bean.setTransferProcessor(getTransferViewProcessor1());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.purdaily.view.CtPuOpenNodeInitDataProcessor getCtPuOpenNodeInitDataProcessor_2011fe(){
 if(context.get("nc.ui.ct.purdaily.view.CtPuOpenNodeInitDataProcessor#2011fe")!=null)
 return (nc.ui.ct.purdaily.view.CtPuOpenNodeInitDataProcessor)context.get("nc.ui.ct.purdaily.view.CtPuOpenNodeInitDataProcessor#2011fe");
  nc.ui.ct.purdaily.view.CtPuOpenNodeInitDataProcessor bean = new nc.ui.ct.purdaily.view.CtPuOpenNodeInitDataProcessor();
  context.put("nc.ui.ct.purdaily.view.CtPuOpenNodeInitDataProcessor#2011fe",bean);
  bean.setFunNodeInitDataListener(getInitDataListener());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public java.util.ArrayList getPasteClearItem_ct_pu_b(){
 if(context.get("pasteClearItem_ct_pu_b")!=null)
 return (java.util.ArrayList)context.get("pasteClearItem_ct_pu_b");
  java.util.ArrayList bean = new java.util.ArrayList(getManagedList20());  context.put("pasteClearItem_ct_pu_b",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList20(){  List list = new ArrayList();  list.add("nordnum");  list.add("nordsum");  list.add("pk_ct_pu_b");  list.add("noritotalgpmny");  list.add("nschedulernum");  list.add("pk_ct_price");  list.add("pk_ct_price.vname");  list.add("pk_ctrelating");  list.add("pk_ctrelating_b");  list.add("vctbillcode");  list.add("pk_origctb");  list.add("pk_praybill");  list.add("pk_praybill_b");  list.add("vpraybillcode");  list.add("pk_ecmct");  list.add("pk_ecmct_b");  list.add("vecmctbillcode");  list.add("ts");  return list;}

public java.util.ArrayList getPasteClearItem_ct_pu_term(){
 if(context.get("pasteClearItem_ct_pu_term")!=null)
 return (java.util.ArrayList)context.get("pasteClearItem_ct_pu_term");
  java.util.ArrayList bean = new java.util.ArrayList(getManagedList21());  context.put("pasteClearItem_ct_pu_term",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList21(){  List list = new ArrayList();  list.add("pk_ct_pu_term");  list.add("ts");  return list;}

public java.util.ArrayList getPasteClearItem_ct_pu_exp(){
 if(context.get("pasteClearItem_ct_pu_exp")!=null)
 return (java.util.ArrayList)context.get("pasteClearItem_ct_pu_exp");
  java.util.ArrayList bean = new java.util.ArrayList(getManagedList22());  context.put("pasteClearItem_ct_pu_exp",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList22(){  List list = new ArrayList();  list.add("pk_ct_pu_exp");  list.add("ts");  return list;}

public java.util.ArrayList getPasteClearItem_ct_pu_memora(){
 if(context.get("pasteClearItem_ct_pu_memora")!=null)
 return (java.util.ArrayList)context.get("pasteClearItem_ct_pu_memora");
  java.util.ArrayList bean = new java.util.ArrayList(getManagedList23());  context.put("pasteClearItem_ct_pu_memora",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList23(){  List list = new ArrayList();  list.add("pk_ct_pu_memora");  list.add("ts");  return list;}

public java.util.ArrayList getPasteClearItem_ct_pu_payment(){
 if(context.get("pasteClearItem_ct_pu_payment")!=null)
 return (java.util.ArrayList)context.get("pasteClearItem_ct_pu_payment");
  java.util.ArrayList bean = new java.util.ArrayList(getManagedList24());  context.put("pasteClearItem_ct_pu_payment",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList24(){  List list = new ArrayList();  list.add("pk_ct_pu_payment");  list.add("ts");  return list;}

public nc.vo.uif2.LoginContext getContext(){
 if(context.get("context")!=null)
 return (nc.vo.uif2.LoginContext)context.get("context");
  nc.vo.uif2.LoginContext bean = new nc.vo.uif2.LoginContext();
  context.put("context",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.model.PurdailyModelService getManageModelService(){
 if(context.get("ManageModelService")!=null)
 return (nc.ui.ct.purdaily.model.PurdailyModelService)context.get("ManageModelService");
  nc.ui.ct.purdaily.model.PurdailyModelService bean = new nc.ui.ct.purdaily.model.PurdailyModelService();
  context.put("ManageModelService",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.view.value.AggVOMetaBDObjectAdapterFactory getBoadatorfactory(){
 if(context.get("boadatorfactory")!=null)
 return (nc.ui.pubapp.uif2app.view.value.AggVOMetaBDObjectAdapterFactory)context.get("boadatorfactory");
  nc.ui.pubapp.uif2app.view.value.AggVOMetaBDObjectAdapterFactory bean = new nc.ui.pubapp.uif2app.view.value.AggVOMetaBDObjectAdapterFactory();
  context.put("boadatorfactory",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.model.CTModel getManageAppModel(){
 if(context.get("ManageAppModel")!=null)
 return (nc.ui.ct.model.CTModel)context.get("ManageAppModel");
  nc.ui.ct.model.CTModel bean = new nc.ui.ct.model.CTModel();
  context.put("ManageAppModel",bean);
  bean.setService(getManageModelService());
  bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
  bean.setContext(getContext());
  bean.setBillType("Z2");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.scmpub.page.model.SCMBillPageModelDataManager getModelDataManager(){
 if(context.get("modelDataManager")!=null)
 return (nc.ui.scmpub.page.model.SCMBillPageModelDataManager)context.get("modelDataManager");
  nc.ui.scmpub.page.model.SCMBillPageModelDataManager bean = new nc.ui.scmpub.page.model.SCMBillPageModelDataManager();
  context.put("modelDataManager",bean);
  bean.setModel(getManageAppModel());
  bean.setPageQuery(getPageQuery());
  bean.setPageDelegator(getPageDelegator());
  bean.setPagePanel(getQueryInfo());
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
  bean.setBillTemplateMender(getBillTemplateMender());
  bean.setNodeKeies(getManagedList25());
  bean.load();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList25(){  List list = new ArrayList();  list.add("Z206");  return list;}

public nc.ui.pubapp.uif2app.funcnode.trantype.TrantypeBillTemplateMender getBillTemplateMender(){
 if(context.get("billTemplateMender")!=null)
 return (nc.ui.pubapp.uif2app.funcnode.trantype.TrantypeBillTemplateMender)context.get("billTemplateMender");
  nc.ui.pubapp.uif2app.funcnode.trantype.TrantypeBillTemplateMender bean = new nc.ui.pubapp.uif2app.funcnode.trantype.TrantypeBillTemplateMender(getContext());  context.put("billTemplateMender",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.view.BillOrgPanel getOrgPanel(){
 if(context.get("orgPanel")!=null)
 return (nc.ui.pubapp.uif2app.view.BillOrgPanel)context.get("orgPanel");
  nc.ui.pubapp.uif2app.view.BillOrgPanel bean = new nc.ui.pubapp.uif2app.view.BillOrgPanel();
  context.put("orgPanel",bean);
  bean.setModel(getManageAppModel());
  bean.setType(getI18nFB_152b696());
  bean.setOnlyLeafCanSelected(false);
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_152b696(){
 if(context.get("nc.ui.uif2.I18nFB#152b696")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#152b696");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#152b696",bean);  bean.setResDir("4020003_0");
  bean.setResId("04020003-0430");
  bean.setDefaultValue("采购组织");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#152b696",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

public nc.ui.ct.purdaily.view.PurdailyListView getListView(){
 if(context.get("listView")!=null)
 return (nc.ui.ct.purdaily.view.PurdailyListView)context.get("listView");
  nc.ui.ct.purdaily.view.PurdailyListView bean = new nc.ui.ct.purdaily.view.PurdailyListView();
  context.put("listView",bean);
  bean.setModel(getManageAppModel());
  bean.setMultiSelectionEnable(true);
  bean.setShowTotalLineTabcodes(getManagedList26());
  bean.setTemplateContainer(getTemplateContainer());
  bean.setUserdefitemListPreparator(getUserdefAndMarAsstListPreparator());
  bean.setPaginationBar(getPageBar());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList26(){  List list = new ArrayList();  list.add("pk_ct_pu_b");  list.add("pk_ct_pu_exp");  list.add("pk_ct_pu_payment");  return list;}

public nc.ui.pubapp.uif2app.linkquery.LinkQueryHyperlinkMediator getCcontractidMediator(){
 if(context.get("ccontractidMediator")!=null)
 return (nc.ui.pubapp.uif2app.linkquery.LinkQueryHyperlinkMediator)context.get("ccontractidMediator");
  nc.ui.pubapp.uif2app.linkquery.LinkQueryHyperlinkMediator bean = new nc.ui.pubapp.uif2app.linkquery.LinkQueryHyperlinkMediator();
  context.put("ccontractidMediator",bean);
  bean.setModel(getManageAppModel());
  bean.setSrcBillIdField("pk_ctrelating");
  bean.setSrcBillNOField("vctbillcode");
  bean.setSrcBillType("Z2");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.linkquery.LinkQueryHyperlinkMediator getPraybillMediator(){
 if(context.get("praybillMediator")!=null)
 return (nc.ui.pubapp.uif2app.linkquery.LinkQueryHyperlinkMediator)context.get("praybillMediator");
  nc.ui.pubapp.uif2app.linkquery.LinkQueryHyperlinkMediator bean = new nc.ui.pubapp.uif2app.linkquery.LinkQueryHyperlinkMediator();
  context.put("praybillMediator",bean);
  bean.setModel(getManageAppModel());
  bean.setSrcBillIdField("pk_praybill");
  bean.setSrcBillNOField("vpraybillcode");
  bean.setSrcBillType("20");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.linkquery.LinkQueryHyperlinkMediator getVsourcecodeMediator(){
 if(context.get("vsourcecodeMediator")!=null)
 return (nc.ui.pubapp.uif2app.linkquery.LinkQueryHyperlinkMediator)context.get("vsourcecodeMediator");
  nc.ui.pubapp.uif2app.linkquery.LinkQueryHyperlinkMediator bean = new nc.ui.pubapp.uif2app.linkquery.LinkQueryHyperlinkMediator();
  context.put("vsourcecodeMediator",bean);
  bean.setModel(getManageAppModel());
  bean.setSrcBillIdField("csrcid");
  bean.setSrcBillNOField("vsrccode");
  bean.setSrcBillTypeField("vsrctype");
  bean.setSrcBillTypeFieldPos(1);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.view.PurdailyBillForm getBillFormEditor(){
 if(context.get("billFormEditor")!=null)
 return (nc.ui.ct.purdaily.view.PurdailyBillForm)context.get("billFormEditor");
  nc.ui.ct.purdaily.view.PurdailyBillForm bean = new nc.ui.ct.purdaily.view.PurdailyBillForm();
  context.put("billFormEditor",bean);
  bean.setModel(getManageAppModel());
  bean.setTemplateContainer(getTemplateContainer());
  bean.setShowTotalLineTabcodes(getManagedList27());
  bean.setUserdefitemPreparator(getUserdefAndMarAsstCardPreparator());
  bean.setTemplateNotNullValidate(true);
  bean.setAutoAddLine(true);
  bean.setBlankChildrenFilter(getBlankitemfilter());
  bean.setBodyActionMap(getManagedMap2());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList27(){  List list = new ArrayList();  list.add("pk_ct_pu_b");  list.add("pk_ct_pu_exp");  list.add("pk_ct_pu_payment");  return list;}

private Map getManagedMap2(){  Map map = new HashMap();  map.put("pk_ct_pu_b",getManagedList28());  map.put("pk_ct_pu_term",getManagedList29());  map.put("pk_ct_pu_exp",getManagedList30());  map.put("pk_ct_pu_memora",getManagedList31());  map.put("pk_ct_pu_payment",getManagedList32());  return map;}

private List getManagedList28(){  List list = new ArrayList();  list.add(getCtPuAddLineAction_1516f0d());  list.add(getCtPuInsertLineAction_151eb44());  list.add(getPuDeletRowAction_12ccf79());  list.add(getManageBCopyLineAction_e93da5());  list.add(getManageBPasteLineAction_ce3be7());  list.add(getManageBPasteToTailLineAction_1bedcf4());  list.add(getActionsBar_ActionsBarSeparator_19c5183());  list.add(getManageRearrangeRowLineAction_1ab84cc());  list.add(getBodyLineEditAction_19b9904());  list.add(getActionsBar_ActionsBarSeparator_5b0eda());  list.add(getDefaultBodyZoomAction_2c336d());  return list;}

private nc.ui.ct.purdaily.action.CtPuAddLineAction getCtPuAddLineAction_1516f0d(){
 if(context.get("nc.ui.ct.purdaily.action.CtPuAddLineAction#1516f0d")!=null)
 return (nc.ui.ct.purdaily.action.CtPuAddLineAction)context.get("nc.ui.ct.purdaily.action.CtPuAddLineAction#1516f0d");
  nc.ui.ct.purdaily.action.CtPuAddLineAction bean = new nc.ui.ct.purdaily.action.CtPuAddLineAction();
  context.put("nc.ui.ct.purdaily.action.CtPuAddLineAction#1516f0d",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.purdaily.action.CtPuInsertLineAction getCtPuInsertLineAction_151eb44(){
 if(context.get("nc.ui.ct.purdaily.action.CtPuInsertLineAction#151eb44")!=null)
 return (nc.ui.ct.purdaily.action.CtPuInsertLineAction)context.get("nc.ui.ct.purdaily.action.CtPuInsertLineAction#151eb44");
  nc.ui.ct.purdaily.action.CtPuInsertLineAction bean = new nc.ui.ct.purdaily.action.CtPuInsertLineAction();
  context.put("nc.ui.ct.purdaily.action.CtPuInsertLineAction#151eb44",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.purdaily.action.PuDeletRowAction getPuDeletRowAction_12ccf79(){
 if(context.get("nc.ui.ct.purdaily.action.PuDeletRowAction#12ccf79")!=null)
 return (nc.ui.ct.purdaily.action.PuDeletRowAction)context.get("nc.ui.ct.purdaily.action.PuDeletRowAction#12ccf79");
  nc.ui.ct.purdaily.action.PuDeletRowAction bean = new nc.ui.ct.purdaily.action.PuDeletRowAction();
  context.put("nc.ui.ct.purdaily.action.PuDeletRowAction#12ccf79",bean);
  bean.setTableCode("pk_ct_pu_b");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.bodyaction.manage.ManageBCopyLineAction getManageBCopyLineAction_e93da5(){
 if(context.get("nc.ui.ct.bodyaction.manage.ManageBCopyLineAction#e93da5")!=null)
 return (nc.ui.ct.bodyaction.manage.ManageBCopyLineAction)context.get("nc.ui.ct.bodyaction.manage.ManageBCopyLineAction#e93da5");
  nc.ui.ct.bodyaction.manage.ManageBCopyLineAction bean = new nc.ui.ct.bodyaction.manage.ManageBCopyLineAction();
  context.put("nc.ui.ct.bodyaction.manage.ManageBCopyLineAction#e93da5",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.bodyaction.manage.ManageBPasteLineAction getManageBPasteLineAction_ce3be7(){
 if(context.get("nc.ui.ct.bodyaction.manage.ManageBPasteLineAction#ce3be7")!=null)
 return (nc.ui.ct.bodyaction.manage.ManageBPasteLineAction)context.get("nc.ui.ct.bodyaction.manage.ManageBPasteLineAction#ce3be7");
  nc.ui.ct.bodyaction.manage.ManageBPasteLineAction bean = new nc.ui.ct.bodyaction.manage.ManageBPasteLineAction();
  context.put("nc.ui.ct.bodyaction.manage.ManageBPasteLineAction#ce3be7",bean);
  bean.setClearItems(getPasteClearItem_ct_pu_b());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.bodyaction.manage.ManageBPasteToTailLineAction getManageBPasteToTailLineAction_1bedcf4(){
 if(context.get("nc.ui.ct.bodyaction.manage.ManageBPasteToTailLineAction#1bedcf4")!=null)
 return (nc.ui.ct.bodyaction.manage.ManageBPasteToTailLineAction)context.get("nc.ui.ct.bodyaction.manage.ManageBPasteToTailLineAction#1bedcf4");
  nc.ui.ct.bodyaction.manage.ManageBPasteToTailLineAction bean = new nc.ui.ct.bodyaction.manage.ManageBPasteToTailLineAction();
  context.put("nc.ui.ct.bodyaction.manage.ManageBPasteToTailLineAction#1bedcf4",bean);
  bean.setClearItems(getPasteClearItem_ct_pu_b());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pub.beans.ActionsBar.ActionsBarSeparator getActionsBar_ActionsBarSeparator_19c5183(){
 if(context.get("nc.ui.pub.beans.ActionsBar.ActionsBarSeparator#19c5183")!=null)
 return (nc.ui.pub.beans.ActionsBar.ActionsBarSeparator)context.get("nc.ui.pub.beans.ActionsBar.ActionsBarSeparator#19c5183");
  nc.ui.pub.beans.ActionsBar.ActionsBarSeparator bean = new nc.ui.pub.beans.ActionsBar.ActionsBarSeparator();
  context.put("nc.ui.pub.beans.ActionsBar.ActionsBarSeparator#19c5183",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.bodyaction.manage.ManageRearrangeRowLineAction getManageRearrangeRowLineAction_1ab84cc(){
 if(context.get("nc.ui.ct.bodyaction.manage.ManageRearrangeRowLineAction#1ab84cc")!=null)
 return (nc.ui.ct.bodyaction.manage.ManageRearrangeRowLineAction)context.get("nc.ui.ct.bodyaction.manage.ManageRearrangeRowLineAction#1ab84cc");
  nc.ui.ct.bodyaction.manage.ManageRearrangeRowLineAction bean = new nc.ui.ct.bodyaction.manage.ManageRearrangeRowLineAction();
  context.put("nc.ui.ct.bodyaction.manage.ManageRearrangeRowLineAction#1ab84cc",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.actions.BodyLineEditAction getBodyLineEditAction_19b9904(){
 if(context.get("nc.ui.pubapp.uif2app.actions.BodyLineEditAction#19b9904")!=null)
 return (nc.ui.pubapp.uif2app.actions.BodyLineEditAction)context.get("nc.ui.pubapp.uif2app.actions.BodyLineEditAction#19b9904");
  nc.ui.pubapp.uif2app.actions.BodyLineEditAction bean = new nc.ui.pubapp.uif2app.actions.BodyLineEditAction();
  context.put("nc.ui.pubapp.uif2app.actions.BodyLineEditAction#19b9904",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pub.beans.ActionsBar.ActionsBarSeparator getActionsBar_ActionsBarSeparator_5b0eda(){
 if(context.get("nc.ui.pub.beans.ActionsBar.ActionsBarSeparator#5b0eda")!=null)
 return (nc.ui.pub.beans.ActionsBar.ActionsBarSeparator)context.get("nc.ui.pub.beans.ActionsBar.ActionsBarSeparator#5b0eda");
  nc.ui.pub.beans.ActionsBar.ActionsBarSeparator bean = new nc.ui.pub.beans.ActionsBar.ActionsBarSeparator();
  context.put("nc.ui.pub.beans.ActionsBar.ActionsBarSeparator#5b0eda",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.actions.DefaultBodyZoomAction getDefaultBodyZoomAction_2c336d(){
 if(context.get("nc.ui.pubapp.uif2app.actions.DefaultBodyZoomAction#2c336d")!=null)
 return (nc.ui.pubapp.uif2app.actions.DefaultBodyZoomAction)context.get("nc.ui.pubapp.uif2app.actions.DefaultBodyZoomAction#2c336d");
  nc.ui.pubapp.uif2app.actions.DefaultBodyZoomAction bean = new nc.ui.pubapp.uif2app.actions.DefaultBodyZoomAction();
  context.put("nc.ui.pubapp.uif2app.actions.DefaultBodyZoomAction#2c336d",bean);
  bean.setPos(1);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList29(){  List list = new ArrayList();  list.add(getTermBAddLineAction_1e43e52());  list.add(getTermBInsertLineAction_1b7a89f());  list.add(getTermBDelLineAction_9f92e4());  list.add(getTermBCopyLineAction_64f6a7());  list.add(getTermBPasteLineAction_7d772d());  return list;}

private nc.ui.ct.bodyaction.term.TermBAddLineAction getTermBAddLineAction_1e43e52(){
 if(context.get("nc.ui.ct.bodyaction.term.TermBAddLineAction#1e43e52")!=null)
 return (nc.ui.ct.bodyaction.term.TermBAddLineAction)context.get("nc.ui.ct.bodyaction.term.TermBAddLineAction#1e43e52");
  nc.ui.ct.bodyaction.term.TermBAddLineAction bean = new nc.ui.ct.bodyaction.term.TermBAddLineAction();
  context.put("nc.ui.ct.bodyaction.term.TermBAddLineAction#1e43e52",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.bodyaction.term.TermBInsertLineAction getTermBInsertLineAction_1b7a89f(){
 if(context.get("nc.ui.ct.bodyaction.term.TermBInsertLineAction#1b7a89f")!=null)
 return (nc.ui.ct.bodyaction.term.TermBInsertLineAction)context.get("nc.ui.ct.bodyaction.term.TermBInsertLineAction#1b7a89f");
  nc.ui.ct.bodyaction.term.TermBInsertLineAction bean = new nc.ui.ct.bodyaction.term.TermBInsertLineAction();
  context.put("nc.ui.ct.bodyaction.term.TermBInsertLineAction#1b7a89f",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.bodyaction.term.TermBDelLineAction getTermBDelLineAction_9f92e4(){
 if(context.get("nc.ui.ct.bodyaction.term.TermBDelLineAction#9f92e4")!=null)
 return (nc.ui.ct.bodyaction.term.TermBDelLineAction)context.get("nc.ui.ct.bodyaction.term.TermBDelLineAction#9f92e4");
  nc.ui.ct.bodyaction.term.TermBDelLineAction bean = new nc.ui.ct.bodyaction.term.TermBDelLineAction();
  context.put("nc.ui.ct.bodyaction.term.TermBDelLineAction#9f92e4",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.bodyaction.term.TermBCopyLineAction getTermBCopyLineAction_64f6a7(){
 if(context.get("nc.ui.ct.bodyaction.term.TermBCopyLineAction#64f6a7")!=null)
 return (nc.ui.ct.bodyaction.term.TermBCopyLineAction)context.get("nc.ui.ct.bodyaction.term.TermBCopyLineAction#64f6a7");
  nc.ui.ct.bodyaction.term.TermBCopyLineAction bean = new nc.ui.ct.bodyaction.term.TermBCopyLineAction();
  context.put("nc.ui.ct.bodyaction.term.TermBCopyLineAction#64f6a7",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.bodyaction.term.TermBPasteLineAction getTermBPasteLineAction_7d772d(){
 if(context.get("nc.ui.ct.bodyaction.term.TermBPasteLineAction#7d772d")!=null)
 return (nc.ui.ct.bodyaction.term.TermBPasteLineAction)context.get("nc.ui.ct.bodyaction.term.TermBPasteLineAction#7d772d");
  nc.ui.ct.bodyaction.term.TermBPasteLineAction bean = new nc.ui.ct.bodyaction.term.TermBPasteLineAction();
  context.put("nc.ui.ct.bodyaction.term.TermBPasteLineAction#7d772d",bean);
  bean.setClearItems(getPasteClearItem_ct_pu_term());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList30(){  List list = new ArrayList();  list.add(getExpBAddLineAction_11b35dd());  list.add(getExpBInsertLineAction_16d5d75());  list.add(getExpBDelLineAction_112c192());  list.add(getExpBCopyLineAction_627ebf());  list.add(getExpBPasteLineAction_6f4c91());  return list;}

private nc.ui.ct.bodyaction.exp.ExpBAddLineAction getExpBAddLineAction_11b35dd(){
 if(context.get("nc.ui.ct.bodyaction.exp.ExpBAddLineAction#11b35dd")!=null)
 return (nc.ui.ct.bodyaction.exp.ExpBAddLineAction)context.get("nc.ui.ct.bodyaction.exp.ExpBAddLineAction#11b35dd");
  nc.ui.ct.bodyaction.exp.ExpBAddLineAction bean = new nc.ui.ct.bodyaction.exp.ExpBAddLineAction();
  context.put("nc.ui.ct.bodyaction.exp.ExpBAddLineAction#11b35dd",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.bodyaction.exp.ExpBInsertLineAction getExpBInsertLineAction_16d5d75(){
 if(context.get("nc.ui.ct.bodyaction.exp.ExpBInsertLineAction#16d5d75")!=null)
 return (nc.ui.ct.bodyaction.exp.ExpBInsertLineAction)context.get("nc.ui.ct.bodyaction.exp.ExpBInsertLineAction#16d5d75");
  nc.ui.ct.bodyaction.exp.ExpBInsertLineAction bean = new nc.ui.ct.bodyaction.exp.ExpBInsertLineAction();
  context.put("nc.ui.ct.bodyaction.exp.ExpBInsertLineAction#16d5d75",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.bodyaction.exp.ExpBDelLineAction getExpBDelLineAction_112c192(){
 if(context.get("nc.ui.ct.bodyaction.exp.ExpBDelLineAction#112c192")!=null)
 return (nc.ui.ct.bodyaction.exp.ExpBDelLineAction)context.get("nc.ui.ct.bodyaction.exp.ExpBDelLineAction#112c192");
  nc.ui.ct.bodyaction.exp.ExpBDelLineAction bean = new nc.ui.ct.bodyaction.exp.ExpBDelLineAction();
  context.put("nc.ui.ct.bodyaction.exp.ExpBDelLineAction#112c192",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.bodyaction.exp.ExpBCopyLineAction getExpBCopyLineAction_627ebf(){
 if(context.get("nc.ui.ct.bodyaction.exp.ExpBCopyLineAction#627ebf")!=null)
 return (nc.ui.ct.bodyaction.exp.ExpBCopyLineAction)context.get("nc.ui.ct.bodyaction.exp.ExpBCopyLineAction#627ebf");
  nc.ui.ct.bodyaction.exp.ExpBCopyLineAction bean = new nc.ui.ct.bodyaction.exp.ExpBCopyLineAction();
  context.put("nc.ui.ct.bodyaction.exp.ExpBCopyLineAction#627ebf",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.bodyaction.exp.ExpBPasteLineAction getExpBPasteLineAction_6f4c91(){
 if(context.get("nc.ui.ct.bodyaction.exp.ExpBPasteLineAction#6f4c91")!=null)
 return (nc.ui.ct.bodyaction.exp.ExpBPasteLineAction)context.get("nc.ui.ct.bodyaction.exp.ExpBPasteLineAction#6f4c91");
  nc.ui.ct.bodyaction.exp.ExpBPasteLineAction bean = new nc.ui.ct.bodyaction.exp.ExpBPasteLineAction();
  context.put("nc.ui.ct.bodyaction.exp.ExpBPasteLineAction#6f4c91",bean);
  bean.setClearItems(getPasteClearItem_ct_pu_exp());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList31(){  List list = new ArrayList();  list.add(getMemoraBAddLineAction_10bf821());  list.add(getMemoraBInsertLineAction_cf4952());  list.add(getMemoraBDelLineAction_d6c9c9());  list.add(getMemoraBCopyLineAction_159ad9a());  list.add(getMemoraBPasteLineAction_9472c5());  return list;}

private nc.ui.ct.bodyaction.memora.MemoraBAddLineAction getMemoraBAddLineAction_10bf821(){
 if(context.get("nc.ui.ct.bodyaction.memora.MemoraBAddLineAction#10bf821")!=null)
 return (nc.ui.ct.bodyaction.memora.MemoraBAddLineAction)context.get("nc.ui.ct.bodyaction.memora.MemoraBAddLineAction#10bf821");
  nc.ui.ct.bodyaction.memora.MemoraBAddLineAction bean = new nc.ui.ct.bodyaction.memora.MemoraBAddLineAction();
  context.put("nc.ui.ct.bodyaction.memora.MemoraBAddLineAction#10bf821",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.bodyaction.memora.MemoraBInsertLineAction getMemoraBInsertLineAction_cf4952(){
 if(context.get("nc.ui.ct.bodyaction.memora.MemoraBInsertLineAction#cf4952")!=null)
 return (nc.ui.ct.bodyaction.memora.MemoraBInsertLineAction)context.get("nc.ui.ct.bodyaction.memora.MemoraBInsertLineAction#cf4952");
  nc.ui.ct.bodyaction.memora.MemoraBInsertLineAction bean = new nc.ui.ct.bodyaction.memora.MemoraBInsertLineAction();
  context.put("nc.ui.ct.bodyaction.memora.MemoraBInsertLineAction#cf4952",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.bodyaction.memora.MemoraBDelLineAction getMemoraBDelLineAction_d6c9c9(){
 if(context.get("nc.ui.ct.bodyaction.memora.MemoraBDelLineAction#d6c9c9")!=null)
 return (nc.ui.ct.bodyaction.memora.MemoraBDelLineAction)context.get("nc.ui.ct.bodyaction.memora.MemoraBDelLineAction#d6c9c9");
  nc.ui.ct.bodyaction.memora.MemoraBDelLineAction bean = new nc.ui.ct.bodyaction.memora.MemoraBDelLineAction();
  context.put("nc.ui.ct.bodyaction.memora.MemoraBDelLineAction#d6c9c9",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.bodyaction.memora.MemoraBCopyLineAction getMemoraBCopyLineAction_159ad9a(){
 if(context.get("nc.ui.ct.bodyaction.memora.MemoraBCopyLineAction#159ad9a")!=null)
 return (nc.ui.ct.bodyaction.memora.MemoraBCopyLineAction)context.get("nc.ui.ct.bodyaction.memora.MemoraBCopyLineAction#159ad9a");
  nc.ui.ct.bodyaction.memora.MemoraBCopyLineAction bean = new nc.ui.ct.bodyaction.memora.MemoraBCopyLineAction();
  context.put("nc.ui.ct.bodyaction.memora.MemoraBCopyLineAction#159ad9a",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.bodyaction.memora.MemoraBPasteLineAction getMemoraBPasteLineAction_9472c5(){
 if(context.get("nc.ui.ct.bodyaction.memora.MemoraBPasteLineAction#9472c5")!=null)
 return (nc.ui.ct.bodyaction.memora.MemoraBPasteLineAction)context.get("nc.ui.ct.bodyaction.memora.MemoraBPasteLineAction#9472c5");
  nc.ui.ct.bodyaction.memora.MemoraBPasteLineAction bean = new nc.ui.ct.bodyaction.memora.MemoraBPasteLineAction();
  context.put("nc.ui.ct.bodyaction.memora.MemoraBPasteLineAction#9472c5",bean);
  bean.setClearItems(getPasteClearItem_ct_pu_memora());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList32(){  List list = new ArrayList();  list.add(getPaymentBAddLineAction_1fb2055());  list.add(getPaymentBInsertLineAction_7b298());  list.add(getPaymentBDelLineAction_15e236e());  list.add(getPaymentBCopyLineAction_1680d82());  list.add(getPaymentBPasteLineAction_4f06d7());  return list;}

private nc.ui.ct.bodyaction.payment.PaymentBAddLineAction getPaymentBAddLineAction_1fb2055(){
 if(context.get("nc.ui.ct.bodyaction.payment.PaymentBAddLineAction#1fb2055")!=null)
 return (nc.ui.ct.bodyaction.payment.PaymentBAddLineAction)context.get("nc.ui.ct.bodyaction.payment.PaymentBAddLineAction#1fb2055");
  nc.ui.ct.bodyaction.payment.PaymentBAddLineAction bean = new nc.ui.ct.bodyaction.payment.PaymentBAddLineAction();
  context.put("nc.ui.ct.bodyaction.payment.PaymentBAddLineAction#1fb2055",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.bodyaction.payment.PaymentBInsertLineAction getPaymentBInsertLineAction_7b298(){
 if(context.get("nc.ui.ct.bodyaction.payment.PaymentBInsertLineAction#7b298")!=null)
 return (nc.ui.ct.bodyaction.payment.PaymentBInsertLineAction)context.get("nc.ui.ct.bodyaction.payment.PaymentBInsertLineAction#7b298");
  nc.ui.ct.bodyaction.payment.PaymentBInsertLineAction bean = new nc.ui.ct.bodyaction.payment.PaymentBInsertLineAction();
  context.put("nc.ui.ct.bodyaction.payment.PaymentBInsertLineAction#7b298",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.bodyaction.payment.PaymentBDelLineAction getPaymentBDelLineAction_15e236e(){
 if(context.get("nc.ui.ct.bodyaction.payment.PaymentBDelLineAction#15e236e")!=null)
 return (nc.ui.ct.bodyaction.payment.PaymentBDelLineAction)context.get("nc.ui.ct.bodyaction.payment.PaymentBDelLineAction#15e236e");
  nc.ui.ct.bodyaction.payment.PaymentBDelLineAction bean = new nc.ui.ct.bodyaction.payment.PaymentBDelLineAction();
  context.put("nc.ui.ct.bodyaction.payment.PaymentBDelLineAction#15e236e",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.bodyaction.payment.PaymentBCopyLineAction getPaymentBCopyLineAction_1680d82(){
 if(context.get("nc.ui.ct.bodyaction.payment.PaymentBCopyLineAction#1680d82")!=null)
 return (nc.ui.ct.bodyaction.payment.PaymentBCopyLineAction)context.get("nc.ui.ct.bodyaction.payment.PaymentBCopyLineAction#1680d82");
  nc.ui.ct.bodyaction.payment.PaymentBCopyLineAction bean = new nc.ui.ct.bodyaction.payment.PaymentBCopyLineAction();
  context.put("nc.ui.ct.bodyaction.payment.PaymentBCopyLineAction#1680d82",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ct.bodyaction.payment.PaymentBPasteLineAction getPaymentBPasteLineAction_4f06d7(){
 if(context.get("nc.ui.ct.bodyaction.payment.PaymentBPasteLineAction#4f06d7")!=null)
 return (nc.ui.ct.bodyaction.payment.PaymentBPasteLineAction)context.get("nc.ui.ct.bodyaction.payment.PaymentBPasteLineAction#4f06d7");
  nc.ui.ct.bodyaction.payment.PaymentBPasteLineAction bean = new nc.ui.ct.bodyaction.payment.PaymentBPasteLineAction();
  context.put("nc.ui.ct.bodyaction.payment.PaymentBPasteLineAction#4f06d7",bean);
  bean.setClearItems(getPasteClearItem_ct_pu_payment());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator getMouseClickShowPanelMediator(){
 if(context.get("mouseClickShowPanelMediator")!=null)
 return (nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator)context.get("mouseClickShowPanelMediator");
  nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator bean = new nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator();
  context.put("mouseClickShowPanelMediator",bean);
  bean.setListView(getListView());
  bean.setShowUpComponent(getBillFormEditor());
  bean.setHyperLinkColumn("vbillcode");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.view.CardPanelOrgSetterForAllRefMediator getCardPanelOrgSetterForAllRefMediator(){
 if(context.get("cardPanelOrgSetterForAllRefMediator")!=null)
 return (nc.ui.pubapp.uif2app.view.CardPanelOrgSetterForAllRefMediator)context.get("cardPanelOrgSetterForAllRefMediator");
  nc.ui.pubapp.uif2app.view.CardPanelOrgSetterForAllRefMediator bean = new nc.ui.pubapp.uif2app.view.CardPanelOrgSetterForAllRefMediator(getBillFormEditor());  context.put("cardPanelOrgSetterForAllRefMediator",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.scmpub.listener.BillCodeEditMediator getBillCodeMediator(){
 if(context.get("billCodeMediator")!=null)
 return (nc.ui.scmpub.listener.BillCodeEditMediator)context.get("billCodeMediator");
  nc.ui.scmpub.listener.BillCodeEditMediator bean = new nc.ui.scmpub.listener.BillCodeEditMediator();
  context.put("billCodeMediator",bean);
  bean.setBillForm(getBillFormEditor());
  bean.setBillCodeKey("vbillcode");
  bean.setBillType("Z2");
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell getQueryArea(){
 if(context.get("queryArea")!=null)
 return (nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell)context.get("queryArea");
  nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell bean = new nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell();
  context.put("queryArea",bean);
  bean.setQueryAreaCreator(getQueryAction());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel getQueryInfo(){
 if(context.get("queryInfo")!=null)
 return (nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel)context.get("queryInfo");
  nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel bean = new nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel();
  context.put("queryInfo",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

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

public nc.ui.uif2.editor.UIF2RemoteCallCombinatorCaller getRemoteCallCombinatorCaller(){
 if(context.get("remoteCallCombinatorCaller")!=null)
 return (nc.ui.uif2.editor.UIF2RemoteCallCombinatorCaller)context.get("remoteCallCombinatorCaller");
  nc.ui.uif2.editor.UIF2RemoteCallCombinatorCaller bean = new nc.ui.uif2.editor.UIF2RemoteCallCombinatorCaller();
  context.put("remoteCallCombinatorCaller",bean);
  bean.setRemoteCallers(getManagedList33());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList33(){  List list = new ArrayList();  list.add(getQueryTemplateContainer());  list.add(getTemplateContainer());  list.add(getUserdefitemContainer());  list.add(getPfAddInfoLoader());  return list;}

public nc.ui.uif2.editor.UserdefitemContainerPreparator getUserdefitemPreparator(){
 if(context.get("userdefitemPreparator")!=null)
 return (nc.ui.uif2.editor.UserdefitemContainerPreparator)context.get("userdefitemPreparator");
  nc.ui.uif2.editor.UserdefitemContainerPreparator bean = new nc.ui.uif2.editor.UserdefitemContainerPreparator();
  context.put("userdefitemPreparator",bean);
  bean.setContainer(getUserdefitemContainer());
  bean.setParams(getManagedList34());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList34(){  List list = new ArrayList();  list.add(getUserdefQueryParam_4a3ea6());  list.add(getUserdefQueryParam_5737ba());  return list;}

private nc.ui.uif2.editor.UserdefQueryParam getUserdefQueryParam_4a3ea6(){
 if(context.get("nc.ui.uif2.editor.UserdefQueryParam#4a3ea6")!=null)
 return (nc.ui.uif2.editor.UserdefQueryParam)context.get("nc.ui.uif2.editor.UserdefQueryParam#4a3ea6");
  nc.ui.uif2.editor.UserdefQueryParam bean = new nc.ui.uif2.editor.UserdefQueryParam();
  context.put("nc.ui.uif2.editor.UserdefQueryParam#4a3ea6",bean);
  bean.setMdfullname("ct.ct_pu");
  bean.setPos(0);
  bean.setPrefix("vdef");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.editor.UserdefQueryParam getUserdefQueryParam_5737ba(){
 if(context.get("nc.ui.uif2.editor.UserdefQueryParam#5737ba")!=null)
 return (nc.ui.uif2.editor.UserdefQueryParam)context.get("nc.ui.uif2.editor.UserdefQueryParam#5737ba");
  nc.ui.uif2.editor.UserdefQueryParam bean = new nc.ui.uif2.editor.UserdefQueryParam();
  context.put("nc.ui.uif2.editor.UserdefQueryParam#5737ba",bean);
  bean.setMdfullname("ct.ct_pu_b");
  bean.setPos(1);
  bean.setPrefix("vbdef");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.view.material.assistant.MarAsstPreparator getMarAsstPreparator(){
 if(context.get("marAsstPreparator")!=null)
 return (nc.ui.pubapp.uif2app.view.material.assistant.MarAsstPreparator)context.get("marAsstPreparator");
  nc.ui.pubapp.uif2app.view.material.assistant.MarAsstPreparator bean = new nc.ui.pubapp.uif2app.view.material.assistant.MarAsstPreparator();
  context.put("marAsstPreparator",bean);
  bean.setModel(getManageAppModel());
  bean.setContainer(getUserdefitemContainer());
  bean.setPrefix("vfree");
  bean.setMaterialField("pk_material");
  bean.setProjectField("cbprojectid");
  bean.setCustomerField("casscustid");
  bean.setProductorField("cproductorid");
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
  bean.setParams(getManagedList35());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList35(){  List list = new ArrayList();  list.add(getQueryParam_bb86a());  list.add(getQueryParam_1783b46());  list.add(getQueryParam_7b8e24());  return list;}

private nc.ui.uif2.userdefitem.QueryParam getQueryParam_bb86a(){
 if(context.get("nc.ui.uif2.userdefitem.QueryParam#bb86a")!=null)
 return (nc.ui.uif2.userdefitem.QueryParam)context.get("nc.ui.uif2.userdefitem.QueryParam#bb86a");
  nc.ui.uif2.userdefitem.QueryParam bean = new nc.ui.uif2.userdefitem.QueryParam();
  context.put("nc.ui.uif2.userdefitem.QueryParam#bb86a",bean);
  bean.setMdfullname("ct.ct_pu");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.userdefitem.QueryParam getQueryParam_1783b46(){
 if(context.get("nc.ui.uif2.userdefitem.QueryParam#1783b46")!=null)
 return (nc.ui.uif2.userdefitem.QueryParam)context.get("nc.ui.uif2.userdefitem.QueryParam#1783b46");
  nc.ui.uif2.userdefitem.QueryParam bean = new nc.ui.uif2.userdefitem.QueryParam();
  context.put("nc.ui.uif2.userdefitem.QueryParam#1783b46",bean);
  bean.setMdfullname("ct.ct_pu_b");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.userdefitem.QueryParam getQueryParam_7b8e24(){
 if(context.get("nc.ui.uif2.userdefitem.QueryParam#7b8e24")!=null)
 return (nc.ui.uif2.userdefitem.QueryParam)context.get("nc.ui.uif2.userdefitem.QueryParam#7b8e24");
  nc.ui.uif2.userdefitem.QueryParam bean = new nc.ui.uif2.userdefitem.QueryParam();
  context.put("nc.ui.uif2.userdefitem.QueryParam#7b8e24",bean);
  bean.setRulecode("materialassistant");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel getCardInfoPnl(){
 if(context.get("cardInfoPnl")!=null)
 return (nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel)context.get("cardInfoPnl");
  nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel bean = new nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel();
  context.put("cardInfoPnl",bean);
  bean.setTitleAction(getReturnaction());
  bean.setRightExActions(getManagedList36());
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.actions.UEReturnAction getReturnaction(){
 if(context.get("returnaction")!=null)
 return (nc.ui.pubapp.uif2app.actions.UEReturnAction)context.get("returnaction");
  nc.ui.pubapp.uif2app.actions.UEReturnAction bean = new nc.ui.pubapp.uif2app.actions.UEReturnAction();
  context.put("returnaction",bean);
  bean.setGoComponent(getListView());
  bean.setSaveAction(getSaveAction());
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList36(){  List list = new ArrayList();  list.add(getActionsBar_ActionsBarSeparator_1b0dfc8());  list.add(getHeadZoomAction());  return list;}

private nc.ui.pub.beans.ActionsBar.ActionsBarSeparator getActionsBar_ActionsBarSeparator_1b0dfc8(){
 if(context.get("nc.ui.pub.beans.ActionsBar.ActionsBarSeparator#1b0dfc8")!=null)
 return (nc.ui.pub.beans.ActionsBar.ActionsBarSeparator)context.get("nc.ui.pub.beans.ActionsBar.ActionsBarSeparator#1b0dfc8");
  nc.ui.pub.beans.ActionsBar.ActionsBarSeparator bean = new nc.ui.pub.beans.ActionsBar.ActionsBarSeparator();
  context.put("nc.ui.pub.beans.ActionsBar.ActionsBarSeparator#1b0dfc8",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.actions.DefaultHeadZoomAction getHeadZoomAction(){
 if(context.get("headZoomAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.DefaultHeadZoomAction)context.get("headZoomAction");
  nc.ui.pubapp.uif2app.actions.DefaultHeadZoomAction bean = new nc.ui.pubapp.uif2app.actions.DefaultHeadZoomAction();
  context.put("headZoomAction",bean);
  bean.setBillForm(getBillFormEditor());
  bean.setModel(getManageAppModel());
  bean.setPos(0);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.TangramContainer getContainer(){
 if(context.get("container")!=null)
 return (nc.ui.uif2.TangramContainer)context.get("container");
  nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
  context.put("container",bean);
  bean.setModel(getManageAppModel());
  bean.setTangramLayoutRoot(getTBNode_1ab10d());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_1ab10d(){
 if(context.get("nc.ui.uif2.tangramlayout.node.TBNode#1ab10d")!=null)
 return (nc.ui.uif2.tangramlayout.node.TBNode)context.get("nc.ui.uif2.tangramlayout.node.TBNode#1ab10d");
  nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
  context.put("nc.ui.uif2.tangramlayout.node.TBNode#1ab10d",bean);
  bean.setShowMode("CardLayout");
  bean.setTabs(getManagedList37());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList37(){  List list = new ArrayList();  list.add(getHSNode_1e2dd());  list.add(getVSNode_91dc5e());  list.add(getListviewhistornode());  return list;}

private nc.ui.uif2.tangramlayout.node.HSNode getHSNode_1e2dd(){
 if(context.get("nc.ui.uif2.tangramlayout.node.HSNode#1e2dd")!=null)
 return (nc.ui.uif2.tangramlayout.node.HSNode)context.get("nc.ui.uif2.tangramlayout.node.HSNode#1e2dd");
  nc.ui.uif2.tangramlayout.node.HSNode bean = new nc.ui.uif2.tangramlayout.node.HSNode();
  context.put("nc.ui.uif2.tangramlayout.node.HSNode#1e2dd",bean);
  bean.setLeft(getCNode_133ea4a());
  bean.setRight(getVSNode_164f8ab());
  bean.setDividerLocation(0.22f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_133ea4a(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#133ea4a")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#133ea4a");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#133ea4a",bean);
  bean.setComponent(getQueryArea());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_164f8ab(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#164f8ab")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#164f8ab");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#164f8ab",bean);
  bean.setUp(getCNode_15ac5d1());
  bean.setDown(getCNode_a14b84());
  bean.setDividerLocation(25f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_15ac5d1(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#15ac5d1")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#15ac5d1");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#15ac5d1",bean);
  bean.setComponent(getQueryInfo());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_a14b84(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#a14b84")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#a14b84");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#a14b84",bean);
  bean.setName(getI18nFB_715574());
  bean.setComponent(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_715574(){
 if(context.get("nc.ui.uif2.I18nFB#715574")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#715574");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#715574",bean);  bean.setResDir("common");
  bean.setResId("UC001-0000107");
  bean.setDefaultValue("列表");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#715574",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_91dc5e(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#91dc5e")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#91dc5e");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#91dc5e",bean);
  bean.setUp(getCNode_1d5549e());
  bean.setDown(getCNode_a2ed6e());
  bean.setDividerLocation(25f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_1d5549e(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#1d5549e")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#1d5549e");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#1d5549e",bean);
  bean.setComponent(getCardInfoPnl());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_a2ed6e(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#a2ed6e")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#a2ed6e");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#a2ed6e",bean);
  bean.setName(getI18nFB_a65723());
  bean.setComponent(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_a65723(){
 if(context.get("nc.ui.uif2.I18nFB#a65723")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#a65723");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#a65723",bean);  bean.setResDir("common");
  bean.setResId("UC001-0000106");
  bean.setDefaultValue("卡片");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#a65723",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

public nc.ui.scmbd.linkquery.ScmBDLinkQueryMediator getDiscountMediator(){
 if(context.get("discountMediator")!=null)
 return (nc.ui.scmbd.linkquery.ScmBDLinkQueryMediator)context.get("discountMediator");
  nc.ui.scmbd.linkquery.ScmBDLinkQueryMediator bean = new nc.ui.scmbd.linkquery.ScmBDLinkQueryMediator();
  context.put("discountMediator",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.view.RowNoMediator getRowNoMediator(){
 if(context.get("rowNoMediator")!=null)
 return (nc.ui.pubapp.uif2app.view.RowNoMediator)context.get("rowNoMediator");
  nc.ui.pubapp.uif2app.view.RowNoMediator bean = new nc.ui.pubapp.uif2app.view.RowNoMediator();
  context.put("rowNoMediator",bean);
  bean.setModel(getManageAppModel());
  bean.setEditor(getBillFormEditor());
  bean.setRowNoKey("crowno");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.common.validateservice.ClosingCheck getClosingListener(){
 if(context.get("ClosingListener")!=null)
 return (nc.ui.pubapp.common.validateservice.ClosingCheck)context.get("ClosingListener");
  nc.ui.pubapp.common.validateservice.ClosingCheck bean = new nc.ui.pubapp.common.validateservice.ClosingCheck();
  context.put("ClosingListener",bean);
  bean.setModel(getManageAppModel());
  bean.setSaveAction(getSaveAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.view.ChildrenFilter getBlankitemfilter(){
 if(context.get("blankitemfilter")!=null)
 return (nc.ui.ct.view.ChildrenFilter)context.get("blankitemfilter");
  nc.ui.ct.view.ChildrenFilter bean = new nc.ui.ct.view.ChildrenFilter();
  context.put("blankitemfilter",bean);
  bean.setFilterMap(getManagedMap3());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private Map getManagedMap3(){  Map map = new HashMap();  map.put("pk_ct_pu_b",getManagedList38());  map.put("pk_ct_pu_term",getManagedList39());  map.put("pk_ct_pu_exp",getManagedList40());  map.put("pk_ct_pu_memora",getManagedList41());  map.put("pk_ct_pu_change",getManagedList42());  map.put("pk_ct_pu_payment",getManagedList43());  return map;}

private List getManagedList38(){  List list = new ArrayList();  list.add("pk_material");  list.add("pk_marbasclass");  list.add("cunitid");  list.add("nnum");  list.add("nqtorigprice");  list.add("nqtorigtaxprice");  list.add("norigtaxmny");  return list;}

private List getManagedList39(){  List list = new ArrayList();  list.add("vtermcode");  list.add("vtermcontent");  list.add("votherinfo");  list.add("vmemo");  return list;}

private List getManagedList40(){  List list = new ArrayList();  list.add("vexpcode");  list.add("vexpsum");  list.add("vmemo");  return list;}

private List getManagedList41(){  List list = new ArrayList();  list.add("vmemoracode");  list.add("vmemo");  return list;}

private List getManagedList42(){  List list = new ArrayList();  list.add("vchangecode");  return list;}

private List getManagedList43(){  List list = new ArrayList();  list.add("accrate");  list.add("pk_payperiod");  list.add("pk_balatype");  return list;}

public nc.ui.pubapp.uif2app.view.FractionFixMediator getFractionFixMediator(){
 if(context.get("fractionFixMediator")!=null)
 return (nc.ui.pubapp.uif2app.view.FractionFixMediator)context.get("fractionFixMediator");
  nc.ui.pubapp.uif2app.view.FractionFixMediator bean = new nc.ui.pubapp.uif2app.view.FractionFixMediator(getManagedList44(),getManagedList45());  context.put("fractionFixMediator",bean);
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList44(){  List list = new ArrayList();  list.add(getBillFormEditor());  return list;}

private List getManagedList45(){  List list = new ArrayList();  list.add(getListView());  return list;}

public nc.ui.pubapp.billref.dest.TransferViewProcessor getTransferViewProcessor1(){
 if(context.get("transferViewProcessor1")!=null)
 return (nc.ui.pubapp.billref.dest.TransferViewProcessor)context.get("transferViewProcessor1");
  nc.ui.pubapp.billref.dest.TransferViewProcessor bean = new nc.ui.pubapp.billref.dest.TransferViewProcessor();
  context.put("transferViewProcessor1",bean);
  bean.setList(getListView());
  bean.setActionContainer(getActionsOfList());
  bean.setCardActionContainer(getActionsOfCard());
  bean.setSaveAction(getSaveAction());
  bean.setCommitAction(getCommitAction());
  bean.setCancelAction(getCancelAction());
  bean.setQueryInfoToolbarPanel(getQueryInfo());
  bean.setQueryAreaShell(getQueryArea());
  bean.setBillForm(getBillFormEditor());
  bean.setTransferLogic(getDefaultBillDataLogic_5dd7d1());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.billref.dest.DefaultBillDataLogic getDefaultBillDataLogic_5dd7d1(){
 if(context.get("nc.ui.pubapp.billref.dest.DefaultBillDataLogic#5dd7d1")!=null)
 return (nc.ui.pubapp.billref.dest.DefaultBillDataLogic)context.get("nc.ui.pubapp.billref.dest.DefaultBillDataLogic#5dd7d1");
  nc.ui.pubapp.billref.dest.DefaultBillDataLogic bean = new nc.ui.pubapp.billref.dest.DefaultBillDataLogic();
  context.put("nc.ui.pubapp.billref.dest.DefaultBillDataLogic#5dd7d1",bean);
  bean.setBillForm(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.model.BillBodySortMediator getBillBodySortMediator(){
 if(context.get("billBodySortMediator")!=null)
 return (nc.ui.pubapp.uif2app.model.BillBodySortMediator)context.get("billBodySortMediator");
  nc.ui.pubapp.uif2app.model.BillBodySortMediator bean = new nc.ui.pubapp.uif2app.model.BillBodySortMediator(getManageAppModel(),getBillFormEditor(),getListView());  context.put("billBodySortMediator",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.view.CompositeBillDataPrepare getUserdefAndMarAsstCardPreparator(){
 if(context.get("userdefAndMarAsstCardPreparator")!=null)
 return (nc.ui.pubapp.uif2app.view.CompositeBillDataPrepare)context.get("userdefAndMarAsstCardPreparator");
  nc.ui.pubapp.uif2app.view.CompositeBillDataPrepare bean = new nc.ui.pubapp.uif2app.view.CompositeBillDataPrepare();
  context.put("userdefAndMarAsstCardPreparator",bean);
  bean.setBillDataPrepares(getManagedList46());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList46(){  List list = new ArrayList();  list.add(getUserdefitemPreparator());  list.add(getMarAsstPreparator());  return list;}

public nc.ui.pubapp.uif2app.view.CompositeBillListDataPrepare getUserdefAndMarAsstListPreparator(){
 if(context.get("userdefAndMarAsstListPreparator")!=null)
 return (nc.ui.pubapp.uif2app.view.CompositeBillListDataPrepare)context.get("userdefAndMarAsstListPreparator");
  nc.ui.pubapp.uif2app.view.CompositeBillListDataPrepare bean = new nc.ui.pubapp.uif2app.view.CompositeBillListDataPrepare();
  context.put("userdefAndMarAsstListPreparator",bean);
  bean.setBillListDataPrepares(getManagedList47());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList47(){  List list = new ArrayList();  list.add(getUserdefitemlistPreparator());  list.add(getMarAsstPreparator());  return list;}

public nc.ui.uif2.editor.UserdefitemContainerListPreparator getUserdefitemlistPreparator(){
 if(context.get("userdefitemlistPreparator")!=null)
 return (nc.ui.uif2.editor.UserdefitemContainerListPreparator)context.get("userdefitemlistPreparator");
  nc.ui.uif2.editor.UserdefitemContainerListPreparator bean = new nc.ui.uif2.editor.UserdefitemContainerListPreparator();
  context.put("userdefitemlistPreparator",bean);
  bean.setContainer(getUserdefitemContainer());
  bean.setParams(getManagedList48());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList48(){  List list = new ArrayList();  list.add(getUserdefQueryParam_103f42c());  list.add(getUserdefQueryParam_938cd1());  return list;}

private nc.ui.uif2.editor.UserdefQueryParam getUserdefQueryParam_103f42c(){
 if(context.get("nc.ui.uif2.editor.UserdefQueryParam#103f42c")!=null)
 return (nc.ui.uif2.editor.UserdefQueryParam)context.get("nc.ui.uif2.editor.UserdefQueryParam#103f42c");
  nc.ui.uif2.editor.UserdefQueryParam bean = new nc.ui.uif2.editor.UserdefQueryParam();
  context.put("nc.ui.uif2.editor.UserdefQueryParam#103f42c",bean);
  bean.setMdfullname("ct.ct_pu");
  bean.setPos(0);
  bean.setPrefix("vdef");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.editor.UserdefQueryParam getUserdefQueryParam_938cd1(){
 if(context.get("nc.ui.uif2.editor.UserdefQueryParam#938cd1")!=null)
 return (nc.ui.uif2.editor.UserdefQueryParam)context.get("nc.ui.uif2.editor.UserdefQueryParam#938cd1");
  nc.ui.uif2.editor.UserdefQueryParam bean = new nc.ui.uif2.editor.UserdefQueryParam();
  context.put("nc.ui.uif2.editor.UserdefQueryParam#938cd1",bean);
  bean.setMdfullname("ct.ct_pu_b");
  bean.setPos(1);
  bean.setTabcode("pk_ct_pu_b");
  bean.setPrefix("vbdef");
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

public nc.ui.ct.lazilyload.CtCardPanelLazilyLoad getCardLazySupport(){
 if(context.get("cardLazySupport")!=null)
 return (nc.ui.ct.lazilyload.CtCardPanelLazilyLoad)context.get("cardLazySupport");
  nc.ui.ct.lazilyload.CtCardPanelLazilyLoad bean = new nc.ui.ct.lazilyload.CtCardPanelLazilyLoad();
  context.put("cardLazySupport",bean);
  bean.setBillform(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.lazilyload.CtListPanelLazilyLoad getListLazySupport(){
 if(context.get("listLazySupport")!=null)
 return (nc.ui.ct.lazilyload.CtListPanelLazilyLoad)context.get("listLazySupport");
  nc.ui.ct.lazilyload.CtListPanelLazilyLoad bean = new nc.ui.ct.lazilyload.CtListPanelLazilyLoad();
  context.put("listLazySupport",bean);
  bean.setListView(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager getLasilyLodadMediator(){
 if(context.get("lasilyLodadMediator")!=null)
 return (nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager)context.get("lasilyLodadMediator");
  nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager bean = new nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager();
  context.put("lasilyLodadMediator",bean);
  bean.setModel(getManageAppModel());
  bean.setLoader(getBillLazilyLoader());
  bean.setLazilyLoadSupporter(getManagedList49());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList49(){  List list = new ArrayList();  list.add(getCardLazySupport());  list.add(getListLazySupport());  list.add(getLazyActions());  return list;}

public nc.ui.pubapp.uif2app.lazilyload.ActionLazilyLoad getLazyActions(){
 if(context.get("lazyActions")!=null)
 return (nc.ui.pubapp.uif2app.lazilyload.ActionLazilyLoad)context.get("lazyActions");
  nc.ui.pubapp.uif2app.lazilyload.ActionLazilyLoad bean = new nc.ui.pubapp.uif2app.lazilyload.ActionLazilyLoad();
  context.put("lazyActions",bean);
  bean.setModel(getManageAppModel());
  bean.setActionList(getManagedList50());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList50(){  List list = new ArrayList();  list.add(getPrintAction());  list.add(getPreviewAction());  list.add(getOutputAction());  return list;}

public nc.ui.uif2.components.pagination.PaginationBar getPageBar(){
 if(context.get("pageBar")!=null)
 return (nc.ui.uif2.components.pagination.PaginationBar)context.get("pageBar");
  nc.ui.uif2.components.pagination.PaginationBar bean = new nc.ui.uif2.components.pagination.PaginationBar();
  context.put("pageBar",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.pagination.BillModelPaginationDelegator getPageDelegator(){
 if(context.get("pageDelegator")!=null)
 return (nc.ui.pubapp.uif2app.actions.pagination.BillModelPaginationDelegator)context.get("pageDelegator");
  nc.ui.pubapp.uif2app.actions.pagination.BillModelPaginationDelegator bean = new nc.ui.pubapp.uif2app.actions.pagination.BillModelPaginationDelegator(getManageAppModel());  context.put("pageDelegator",bean);
  bean.setPaginationQuery(getPageQuery());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.model.PurdailyModelPageService getPageQuery(){
 if(context.get("pageQuery")!=null)
 return (nc.ui.ct.purdaily.model.PurdailyModelPageService)context.get("pageQuery");
  nc.ui.ct.purdaily.model.PurdailyModelPageService bean = new nc.ui.ct.purdaily.model.PurdailyModelPageService();
  context.put("pageQuery",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.scmpub.page.model.SCMBillPageMediator getPageMediator(){
 if(context.get("pageMediator")!=null)
 return (nc.ui.scmpub.page.model.SCMBillPageMediator)context.get("pageMediator");
  nc.ui.scmpub.page.model.SCMBillPageMediator bean = new nc.ui.scmpub.page.model.SCMBillPageMediator();
  context.put("pageMediator",bean);
  bean.setListView(getListView());
  bean.setRecordInPage(10);
  bean.setCachePages(10);
  bean.setPageDelegator(getPageDelegator());
  bean.init();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ct.purdaily.view.HistiryListForm getListViewHistory(){
 if(context.get("listViewHistory")!=null)
 return (nc.ui.ct.purdaily.view.HistiryListForm)context.get("listViewHistory");
  nc.ui.ct.purdaily.view.HistiryListForm bean = new nc.ui.ct.purdaily.view.HistiryListForm();
  context.put("listViewHistory",bean);
  bean.setModel(getManageAppModel());
  bean.setMultiSelectionEnable(true);
  bean.setTemplateContainer(getTemplateContainer());
  bean.setUserdefitemListPreparator(getUserdefAndMarAsstListPreparator());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.tangramlayout.node.CNode getListviewhistornode(){
 if(context.get("listviewhistornode")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("listviewhistornode");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("listviewhistornode",bean);
  bean.setComponent(getListViewHistory());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.scmpub.listener.crossrule.CrossRuleMediator getCrossRuleMediator(){
 if(context.get("crossRuleMediator")!=null)
 return (nc.ui.scmpub.listener.crossrule.CrossRuleMediator)context.get("crossRuleMediator");
  nc.ui.scmpub.listener.crossrule.CrossRuleMediator bean = new nc.ui.scmpub.listener.crossrule.CrossRuleMediator();
  context.put("crossRuleMediator",bean);
  bean.setModel(getManageAppModel());
  bean.setBillType("Z2");
  bean.init();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

}
