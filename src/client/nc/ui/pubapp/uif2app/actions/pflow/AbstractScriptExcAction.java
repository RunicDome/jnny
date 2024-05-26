/*     */ package nc.ui.pubapp.uif2app.actions.pflow;
/*     */ 
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import nc.bs.uif2.BusinessExceptionAdapter;
/*     */ import nc.bs.uif2.validation.IValidationService;
/*     */ import nc.bs.uif2.validation.ValidationException;
/*     */ import nc.itf.pubapp.pub.exception.IResumeException;
/*     */ import nc.ui.pf.workitem.beside.BesideApproveContext;
/*     */ import nc.ui.pubapp.pub.common.context.PFlowContext;
/*     */ import nc.ui.pubapp.pub.task.IMultiReturnObjProcessor;
/*     */ import nc.ui.pubapp.uif2app.actions.IActionExecutable;
/*     */ import nc.ui.pubapp.uif2app.model.PFlowProxy;
/*     */ import nc.ui.uif2.NCAction;
/*     */ import nc.ui.uif2.components.progress.ProgressActionInterface;
/*     */ import nc.ui.uif2.components.progress.TPAProgressUtil;
/*     */ import nc.ui.uif2.components.widget.IBesideApproveContext;
/*     */ import nc.vo.ml.AbstractNCLangRes;
/*     */ import nc.vo.ml.NCLangRes4VoTransl;
/*     */ import nc.vo.pubapp.pattern.exception.ExceptionUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractScriptExcAction
/*     */   extends NCAction
/*     */   implements IMultiReturnObjProcessor, IActionExecutable, ProgressActionInterface, IBesideApproveContext
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected List<String> afterActionNames;
/*     */   protected String currentAction;
/*     */   protected PFlowContext flowContext;
/*     */   protected boolean isBactch;
/*  43 */   protected boolean isComposite = true;
/*     */   
/*     */   protected MultiBillScriptRunner multibillScriptRunner;
/*     */   
/*     */   protected List<String> preActionNames;
/*     */   
/*  49 */   private boolean isExecuted = true;
/*     */   
/*  51 */   protected String title = NCLangRes4VoTransl.getNCLangRes().getStrByID("pubapp_0", "0pubapp-0114");
/*     */   
/*     */ 
/*     */   private String actionName;
/*     */   
/*     */   private boolean isPreActionSuccesful;
/*     */   
/*     */   private IValidationService validationService;
/*     */   
/*     */   private TPAProgressUtil tpaProgressUtil;
/*     */   
/*  62 */   private boolean showLogInDialog = false;
/*     */   
/*  64 */   private boolean isTPAMonitor = true;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  69 */   private int processedBillNumOnce = 10;
/*     */   private BesideApproveContext besideApproveContext;
/*     */   
/*  72 */   public int getProcessedBillNumOnce() { return this.processedBillNumOnce; }
/*     */   
/*     */   public void setProcessedBillNumOnce(int processedBillNumOnce)
/*     */   {
/*  76 */     this.processedBillNumOnce = processedBillNumOnce;
/*     */   }
/*     */   
/*     */   public TPAProgressUtil getTpaProgressUtil() {
/*  80 */     return this.tpaProgressUtil;
/*     */   }
/*     */   
/*     */   public void setTpaProgressUtil(TPAProgressUtil tpaProgressUtil) {
/*  84 */     this.tpaProgressUtil = tpaProgressUtil;
/*     */   }
/*     */   
/*     */   public boolean isTPAMonitor() {
/*  88 */     return this.isTPAMonitor;
/*     */   }
/*     */   
/*     */   public void setTPAMonitor(boolean isTPAMonitor) {
/*  92 */     this.isTPAMonitor = isTPAMonitor;
/*     */   }
/*     */   
/*     */   public boolean isShowLogInDialog() {
/*  96 */     return this.showLogInDialog;
/*     */   }
/*     */   
/*     */   public void setShowLogInDialog(boolean showLogInDialog) {
/* 100 */     this.showLogInDialog = showLogInDialog;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public AbstractScriptExcAction() {}
/*     */   
/*     */ 
/*     */   public AbstractScriptExcAction(PFlowContext flowContext)
/*     */   {
/* 110 */     this.flowContext = flowContext;
/*     */   }
/*     */   
/*     */   public boolean isExecuted() {
/* 114 */     return this.isExecuted;
/*     */   }
/*     */   
/*     */   public void doAction(ActionEvent e) throws Exception
/*     */   {
/* 119 */     clear();
/* 120 */     doBeforAction();
/* 121 */     String actionName = getFlowContext().getActionName();
/*     */     try {
/* 123 */       if ((this.preActionNames != null) && (this.isComposite)) {
/* 124 */         for (String preActionName : this.preActionNames) {
/* 125 */           this.currentAction = preActionName;
/* 126 */           getFlowContext().setActionName(preActionName);
/* 127 */           procFlow(getFlowContext());
/*     */         }
/*     */       }
/* 130 */       this.isPreActionSuccesful = true;
/* 131 */       this.currentAction = actionName;
/* 132 */       getFlowContext().setActionName(actionName);
/* 133 */       procFlow(getFlowContext());
/* 134 */       if ((this.afterActionNames != null) && (this.isComposite)) {
/* 135 */         for (String afterActionName : this.afterActionNames) {
/* 136 */           this.currentAction = afterActionName;
/* 137 */           getFlowContext().setActionName(afterActionName);
/* 138 */           procFlow(getFlowContext());
/*     */         }
/*     */       }
/*     */     } catch (Exception ex) {
/* 142 */       throw ex;
/*     */     } finally {
/* 144 */       getFlowContext().setActionName(actionName);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void doBeforAction() {}
/*     */   
/*     */   public List<String> getAfterActionNames()
/*     */   {
/* 153 */     return this.afterActionNames;
/*     */   }
/*     */   
/*     */   public String getCurrentAction() {
/* 157 */     return this.currentAction;
/*     */   }
/*     */   
/*     */   public PFlowContext getFlowContext() {
/* 161 */     if (this.flowContext == null) {
/* 162 */       this.flowContext = new PFlowContext();
/*     */     }
/* 164 */     return this.flowContext;
/*     */   }
/*     */   
/*     */   public boolean getIsPreActionSuccesful() {
/* 168 */     return this.isPreActionSuccesful;
/*     */   }
/*     */   
/*     */   public MultiBillScriptRunner getMultibillScriptRunner() {
/* 172 */     if (this.multibillScriptRunner == null)
/*     */     {
/* 174 */       this.multibillScriptRunner = new MultiBillScriptRunner(this.flowContext);
/* 175 */       this.multibillScriptRunner.setScriptAction(this);
/* 176 */       this.multibillScriptRunner.setProcessedBillNumOnce(getProcessedBillNumOnce());
/*     */     }
/*     */     
/* 179 */     return this.multibillScriptRunner;
/*     */   }
/*     */   
/*     */   public List<String> getPreActionNames() {
/* 183 */     return this.preActionNames;
/*     */   }
/*     */   
/*     */   public String getTitle() {
/* 187 */     return this.title;
/*     */   }
/*     */   
/*     */   public IValidationService getValidationService() {
/* 191 */     return this.validationService;
/*     */   }
/*     */   
/*     */   public boolean isBactch() {
/* 195 */     return this.isBactch;
/*     */   }
/*     */   
/*     */   public void processReturnObjs(Object[] returnObj)
/*     */   {
/*     */     try {
/* 201 */       processReturnObj(returnObj);
/*     */     }
/*     */     catch (Exception e) {
/* 204 */       ExceptionUtils.wrappException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public void procFlow(PFlowContext context) throws Exception {
/* 209 */     boolean isContinue = true;
/* 210 */     while (isContinue) {
/*     */       try {
/* 212 */         fillUpContext(getFlowContext());
/* 213 */         if (getFlowContext().getBillVos() == null) {
/* 214 */           return;
/*     */         }
/* 216 */         Object[] retObjs = null;
/* 217 */         validate();
/* 218 */         if (getFlowContext().isBatch()) {
/* 219 */           if (this.isBactch) {
/* 220 */             batchBeforeCheck();
/* 221 */             retObjs = PFlowProxy.runActionBatch(getFlowContext());
/*     */           }
/*     */           else {
/* 224 */             getMultibillScriptRunner().setContext(getFlowContext());
/* 225 */             getMultibillScriptRunner().setTitle(this.title);
/* 226 */             getMultibillScriptRunner().setParent(context.getParent());
/* 227 */             getMultibillScriptRunner().setMultiReturnObjProcessor(this);
/* 228 */             getMultibillScriptRunner().setTpaProgressUtil(getTpaProgressUtil());
/* 229 */             getMultibillScriptRunner().setTPAMonitor(isTPAMonitor());
/* 230 */             getMultibillScriptRunner().setShowLogInDialog(isShowLogInDialog());
/* 231 */             getMultibillScriptRunner().runTask();
/*     */ 
/*     */ 
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/*     */ 
/* 244 */           setBesideApproveContext();
/* 245 */           Object obj = PFlowProxy.runAction(getFlowContext());
/* 246 */           if ((getFlowContext() != null) && (getBesideApproveContext() != null)) {
/* 247 */             setBesideApproveContext(null);
/*     */           }
/* 249 */           if ((obj != null) && (!obj.getClass().isArray())) {
/* 250 */             retObjs = (Object[])Array.newInstance(obj.getClass(), 1);
/* 251 */             retObjs[0] = obj;
/*     */           }
/*     */           else {
/* 254 */             retObjs = (Object[])obj;
/*     */           }
/*     */         }
/* 257 */         isContinue = false;
/* 258 */         processReturnObjs(retObjs);
/*     */       }
/*     */       catch (Exception e) {
/* 261 */         Throwable ex = ExceptionUtils.unmarsh(e);
/* 262 */         if ((ex instanceof IResumeException)) {
/* 263 */           if (isResume((IResumeException)ex)) {
/* 264 */             isContinue = true;
/* 265 */             this.isExecuted = true;
/*     */           }
/*     */           else {
/* 268 */             isContinue = false;
/* 269 */             this.isExecuted = false;
/*     */           }
/*     */         }
/*     */         else {
/* 273 */           throw e;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setActionName(String actionName)
/*     */   {
/* 285 */     this.actionName = actionName;
/* 286 */     getFlowContext().setActionName(actionName);
/*     */   }
/*     */   
/*     */   public void setAfterActionNames(List<String> afterActionNames) {
/* 290 */     this.afterActionNames = afterActionNames;
/*     */   }
/*     */   
/*     */   public void setBactch(boolean bactch) {
/* 294 */     this.isBactch = bactch;
/*     */   }
/*     */   
/* 297 */   public String getBillType() { return getFlowContext().getBillType(); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setBillType(String billType)
/*     */   {
/* 305 */     getFlowContext().setBillType(billType);
/*     */   }
/*     */   
/*     */   public void setComposite(boolean composite) {
/* 309 */     this.isComposite = composite;
/*     */   }
/*     */   
/*     */   public void setFlowContext(PFlowContext flowContext) {
/* 313 */     this.flowContext = flowContext;
/*     */   }
/*     */   
/*     */   public void setPreActionNames(List<String> preActionNames) {
/* 317 */     this.preActionNames = preActionNames;
/*     */   }
/*     */   
/*     */   public void setTitle(String title) {
/* 321 */     this.title = title;
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
/*     */   public void setValidationService(IValidationService validationService)
/*     */   {
/* 340 */     this.validationService = validationService;
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
/*     */   protected void batchBeforeCheck() {}
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
/*     */   protected void beforeCheck(Object vo) {}
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
/*     */   protected void clear()
/*     */   {
/* 378 */     getFlowContext().clear();
/* 379 */     getFlowContext().setActionName(this.actionName);
/* 380 */     this.isPreActionSuccesful = false;
/* 381 */     this.isExecuted = true;
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
/*     */   protected abstract void fillUpContext(PFlowContext paramPFlowContext);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Object getFullBill(Object vo)
/*     */   {
/* 405 */     return vo;
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
/*     */   protected Object[] getFullBills(Object[] vos)
/*     */   {
/* 422 */     return vos;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean isResume(IResumeException resumeInfo)
/*     */   {
/* 434 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected abstract void processReturnObj(Object[] paramArrayOfObject)
/*     */     throws Exception;
/*     */   
/*     */ 
/*     */   private void combinExceptionResults(Map<Integer, String> exceptionResults)
/*     */   {
/* 445 */     Set<Map.Entry<Integer, String>> entrySet = exceptionResults.entrySet();
/* 446 */     Map<Integer, String> origExceptionResults = getFlowContext().getExceptionResults();
/*     */     
/* 448 */     for (Map.Entry<Integer, String> entry : entrySet) {
/* 449 */       if ((!origExceptionResults.containsKey(entry.getKey())) || (origExceptionResults.get(entry.getKey()) == null))
/*     */       {
/* 451 */         origExceptionResults.put(entry.getKey(), entry.getValue());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void validate() {
/* 457 */     Object[] vos = getFlowContext().getBillVos();
/*     */     
/* 459 */     if ((this.validationService instanceof IBatchOperateValidateService)) {
/* 460 */       Object[] fullVOs = (Object[])Array.newInstance(vos[0].getClass(), vos.length);
/*     */       
/* 462 */       for (int i = 0; i < fullVOs.length; i++) {
/* 463 */         fullVOs[i] = getFullBill(vos[i]);
/*     */       }
/* 465 */       validate(fullVOs);
/* 466 */       beforeCheck(fullVOs);
/* 467 */       return;
/*     */     }
/* 469 */     for (Object vo : vos) {
/* 470 */       Object value = getFullBill(vo);
/* 471 */       validate(value);
/* 472 */       beforeCheck(value);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void validate(Object value)
/*     */   {
/* 482 */     if (this.validationService != null) {
/*     */       try {
/* 484 */         this.validationService.validate(value);
/* 485 */         if ((this.validationService instanceof IBatchOperateValidateService))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 492 */           getFlowContext().combinExceptionResults(((IBatchOperateValidateService)this.validationService).getValidationExceptionResults());
/*     */         }
/*     */         
/*     */       }
/*     */       catch (ValidationException e)
/*     */       {
/* 498 */         throw new BusinessExceptionAdapter(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void setBesideApproveContext()
/*     */   {
/* 505 */     if ((getFlowContext() != null) && (getBesideApproveContext() != null)) {
/* 506 */       getFlowContext().getEParam().put("besideapprove", getBesideApproveContext());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public BesideApproveContext getBesideApproveContext()
/*     */   {
/* 514 */     return this.besideApproveContext;
/*     */   }
/*     */   
/*     */   public void setBesideApproveContext(BesideApproveContext besideApproveContext)
/*     */   {
/* 519 */     this.besideApproveContext = besideApproveContext;
/*     */   }
/*     */   
/*     */   public boolean getActionEnabled()
/*     */   {
/* 524 */     return isActionEnable();
/*     */   }
/*     */ }
