package nc.bs.pub.wfengine.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.ITimeService;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.ml.NCLangResOnserver;
import nc.bs.pf.pub.PfDataCache;
import nc.bs.pub.pf.IPFClientBizProcessBS;
import nc.bs.pub.pf.IPfBeforeAction;
import nc.bs.pub.pf.PfMessageUtil;
import nc.bs.pub.pf.PfUtilTools;
import nc.bs.pub.pflock.PfBusinessLock;
import nc.bs.pub.pflock.VOConsistenceCheck;
import nc.bs.pub.pflock.VOLockData;
import nc.bs.pub.taskmanager.TaskManagerDMO;
import nc.bs.pub.taskmanager.WfTaskManager;
import nc.bs.pub.workflownote.WorknoteManager;
import nc.bs.wfengine.engine.ActivityInstance;
import nc.bs.wfengine.engine.EngineService;
import nc.bs.wfengine.engine.ProcessInstance;
import nc.bs.wfengine.engine.WfInstancePool;
import nc.bs.wfengine.engine.persistence.EnginePersistence;
import nc.impl.uap.pf.PFConfigImpl;
import nc.impl.uap.pf.WorkflowAdminImpl;
import nc.itf.uap.pf.IPFBusiAction;
import nc.itf.uap.pf.IWorkflowAdmin;
import nc.itf.uap.pf.IWorkflowMachine;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.BaseProcessor;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.message.attachment.AttachmentReturnExcutor;
import nc.message.vo.AttachmentVO;
import nc.ui.pf.pub.PFClientBizRetObj;
import nc.ui.pub.pf.IPFClientBizProcess;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pf.change.PfUtilBaseTools;
import nc.vo.pf.mobileapp.MobileAppUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype2.Billtype2VO;
import nc.vo.pub.billtype2.ExtendedClassEnum;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.msg.AbstractMsgL10NCallback;
import nc.vo.pub.msg.IMsgL10NCallback;
import nc.vo.pub.pf.AssignableInfo;
import nc.vo.pub.pf.PFClientBizRetVO;
import nc.vo.pub.pf.PfClientBizProcessContext;
import nc.vo.pub.pf.TransitionSelectableInfo;
import nc.vo.pub.workflownote.WorkflownoteAttVO;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.uap.pf.PFBusinessException;
import nc.vo.uap.pf.RetBackWfVo;
import nc.vo.wfengine.core.activity.Activity;
import nc.vo.wfengine.core.parser.XPDLParserException;
import nc.vo.wfengine.core.workflow.WorkflowProcess;
import nc.vo.wfengine.definition.ActivityTypeEnum;
import nc.vo.wfengine.definition.WorkflowTypeEnum;
import nc.vo.wfengine.engine.ExecuteResult;
import nc.vo.wfengine.pub.WFTask;
import nc.vo.wfengine.pub.WfTaskOrInstanceStatus;
import nc.vo.wfengine.pub.WfTaskType;

import org.apache.commons.lang.StringUtils;
// 预算调整单
public class WorkflowMachineImpl  implements IWorkflowMachine{
	 public WorkflowMachineImpl() {}
	 /*      */   
	 /*      */   private boolean isCheckAction(String actionName, String billType)
	 /*      */   {
	 /*  108 */     if ((PfUtilBaseTools.isStartFlowAction(actionName, billType)) || (PfUtilBaseTools.isSignalFlowAction(actionName, billType)))
	 /*      */     {
	 /*  110 */       return true; }
	 /*  111 */     return false;
	 /*      */   }
	 /*      */   
	 /*      */   public WorkflownoteVO checkWorkflowActions(String billType, String originBillId) throws BusinessException
	 /*      */   {
	 /*  116 */     String billid = null;
	 /*      */     try {
	 /*  118 */       AggregatedValueObject billvo = MobileAppUtil.queryBillEntity(billType, originBillId);
	 /*      */       
	 /*      */ 
	 /*  121 */       PfParameterVO paraVO = PfUtilBaseTools.getVariableValue(billType, "APPROVE", billvo, null, null, null, null, new HashMap(), new Hashtable());
	 /*      */       
	 /*      */ 
	 /*  124 */       billid = paraVO.m_billVersionPK;
	 /*  125 */       ActionEnvironment.getInstance().putParaVo(billid, paraVO);
	 /*      */       
	 /*  127 */       return new EngineService().checkUnfinishedWorkitem(paraVO, WorkflowTypeEnum.Approveflow.getIntValue());
	 /*      */     }
	 /*      */     catch (Exception e) {
	 /*  130 */       if ((e instanceof BusinessException)) {
	 /*  131 */         throw ((BusinessException)e);
	 /*      */       }
	 /*  133 */       throw new BusinessException(e.getMessage(), e);
	 /*      */     }
	 /*      */     finally {
	 /*  136 */       ActionEnvironment.getInstance().putParaVo(billid, null);
	 /*      */     }
	 /*      */   }
	 /*      */   
	 /*      */   public WorkflownoteVO checkWorkFlow(String actionCode, String billType, AggregatedValueObject billVO, HashMap hmPfExParams)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*  143 */     Logger.init("workflow");
	 /*  144 */     Logger.debug("*流程检查 EngineService.checkWorkFlow开始");
	 /*  145 */     Logger.debug("*********************************************");
	 /*  146 */     Logger.debug("* actionName=" + actionCode);
	 /*  147 */     Logger.debug("* billType=" + billType);
	 /*  148 */     Logger.debug("* billEntity=" + billVO);
	 /*  149 */     Logger.debug("* eParam=" + hmPfExParams);
	 /*  150 */     Logger.debug("*********************************************");
	 /*      */     
	 /*  152 */     long start = System.currentTimeMillis();
	 /*      */     
	 /*      */ 
	 /*  155 */     if (!isCheckAction(actionCode, billType)) {
	 /*  156 */       throw new PFBusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "wfMachineImpl-0000", null, new String[] { actionCode }));
	 /*      */     }
	 /*      */     
	 /*      */ 
	 /*  160 */     PfBusinessLock pfLock = new PfBusinessLock();
	 /*  161 */     String strBillId = null;
	 /*      */     try
	 /*      */     {
	 /*  164 */       if ((hmPfExParams != null) && (hmPfExParams.get("nosendmessage") != null))
	 /*      */       {
	 /*      */ 
	 /*      */ 
	 /*  168 */         return null;
	 /*      */       }
	 /*      */       
	 /*      */ 
	 /*      */ 
	 /*  173 */       Object paramNoLock = hmPfExParams == null ? null : hmPfExParams.get("nolockandconsist");
	 /*      */       
	 /*  175 */       if (paramNoLock == null) {
	 /*  176 */         pfLock.lock(new VOLockData(billVO, billType), new VOConsistenceCheck(billVO, billType));
	 /*      */       }
	 /*      */       
	 /*      */ 
	 /*      */ 
	 /*  181 */       Object paramReloadVO = hmPfExParams == null ? null : hmPfExParams.get("reload_vo");
	 /*      */       
	 /*  183 */       if (paramReloadVO != null) {
	 /*  184 */         String billId = billVO.getParentVO().getPrimaryKey();
	 /*  185 */         billVO = new PFConfigImpl().queryBillDataVO(billType, billId);
	 /*  186 */         if (billVO == null) {
	 /*  187 */           throw new PFBusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "wfMachineImpl-0001", null, new String[] { billType, billId }));
	 /*      */         }
	 /*      */       }
	 /*      */       
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*  199 */       Object checkObj = PfUtilTools.getBizRuleImpl(billType);
	 /*  200 */       AggregatedValueObject completeVO = billVO;
	 /*  201 */       if ((checkObj instanceof IPfBeforeAction)) {
	 /*  202 */         completeVO = ((IPfBeforeAction)checkObj).beforeAction(billVO, null, hmPfExParams);
	 /*      */       }
	 /*      */       
	 /*      */ 
	 /*  206 */       Hashtable hashBilltypeToParavo = new Hashtable();
	 /*  207 */       PfParameterVO paraVO = PfUtilBaseTools.getVariableValue(billType, actionCode, completeVO, null, null, null, null, hmPfExParams, hashBilltypeToParavo);
	 /*      */       
	 /*      */ 
	 /*  210 */       strBillId = paraVO.m_billVersionPK;
	 /*  211 */       ActionEnvironment.getInstance().putParaVo(strBillId, paraVO);
	 /*      */       
	 /*  213 */       Object paramDefPK = hmPfExParams == null ? null : hmPfExParams.get("flowdefpk");
	 /*      */       
	 /*  215 */       paraVO.m_flowDefPK = (paramDefPK == null ? null : String.valueOf(paramDefPK));
	 /*      */       
	 /*  217 */       WorkflownoteVO note = null;
	 /*  218 */       if (PfUtilBaseTools.isSaveAction(actionCode, billType))
	 /*      */       {
	 /*  220 */         note = checkApproveflowWhenSave(paraVO);
	 /*  221 */         setIsAreadyTracked(paraVO, note);
	 /*  222 */       } else if (PfUtilBaseTools.isStartAction(actionCode, billType))
	 /*      */       {
	 /*  224 */         note = checkWorkflowWhenStart(paraVO);
	 /*  225 */         setIsAreadyTracked(paraVO, note);
	 /*  226 */       } else if (PfUtilBaseTools.isApproveAction(actionCode, billType))
	 /*      */       {
	 /*  228 */         note = new EngineService().checkUnfinishedWorkitem(paraVO, WorkflowTypeEnum.Approveflow.getIntValue());
	 /*      */         
	 /*  230 */         setIsAreadyTracked(paraVO, note);
	 /*  231 */       } else if (PfUtilBaseTools.isSignalAction(actionCode, billType))
	 /*      */       {
	 /*  233 */         note = new EngineService().checkUnfinishedWorkitem(paraVO, WorkflowTypeEnum.Workflow.getIntValue());
	 /*      */         
	 /*  235 */         setIsAreadyTracked(paraVO, note);
	 /*      */       }
	 /*  237 */       return note;
	 /*      */     } catch (DbException ex) {
	 /*  239 */       Logger.error(ex.getMessage(), ex);
	 /*  240 */       throw new PFBusinessException(NCLangResOnserver.getInstance().getStrByID("pfworkflow", "UPPpfworkflow-000004") + ex.getMessage());
	 /*      */ 
	 /*      */ 
	 /*      */     }
	 /*      */     finally
	 /*      */     {
	 /*      */ 
	 /*  247 */       Logger.debug("*流程检查 EngineService.checkWorkFlow结束，耗时" + (System.currentTimeMillis() - start) + "ms");
	 /*      */       
	 /*      */ 
	 /*  250 */       ActionEnvironment.getInstance().putParaVo(strBillId, null);
	 /*      */     }
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */   private void setIsAreadyTracked(PfParameterVO paraVO, WorkflownoteVO note)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*  260 */     if (note == null) {
	 /*  261 */       return;
	 /*      */     }
	 /*  263 */     String processInsID = note.getTaskInfo().getTask() == null ? null : note.getTaskInfo() == null ? null : note.getTaskInfo().getTask().getWfProcessInstancePK();
	 /*      */     
	 /*      */ 
	 /*  266 */     if (StringUtils.isNotBlank(processInsID)) {
	 /*  267 */       IWorkflowAdmin workflowAdmin = (IWorkflowAdmin)NCLocator.getInstance().lookup(IWorkflowAdmin.class);
	 /*      */       
	 /*  269 */       boolean isTracked = workflowAdmin.isAlreadyTracked(processInsID, paraVO.m_operator);
	 /*      */       
	 /*  271 */       note.setTrack(isTracked);
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
	 /*      */   private WorkflownoteVO checkApproveflowWhenSave(PfParameterVO paraVO)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*  286 */     EngineService queryDMO = new EngineService();
	 /*      */     int status;
	 /*      */     try {
	 /*  289 */       status = queryDMO.queryApproveflowStatus(paraVO.m_billVersionPK, paraVO.m_billType);
	 /*      */     }
	 /*      */     catch (DbException e) {
	 /*  292 */       Logger.error(e.getMessage(), e);
	 /*  293 */       throw new PFBusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "wfMachineImpl-0002", null, new String[] { e.getMessage() }));
	 /*      */     }
	 /*      */     
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*  300 */     if (paraVO.getCustomProperty("forcestart") == null)
	 /*      */     {
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*  305 */       switch (status)
	 /*      */       {
	 /*      */       case -1: 
	 /*      */       case 0: 
	 /*      */       case 3: 
	 /*      */         break;
	 /*      */       
	 /*      */ 
	 /*      */       case 2: 
	 /*  314 */         return null;
	 /*      */       case 1: default: 
	 /*  316 */         return null;
	 /*      */       }
	 /*      */       
	 /*      */     }
	 /*  320 */     return queryApproveflowOnSave(queryDMO, paraVO, status);
	 /*      */   }
	 /*      */   
	 /*      */   private WorkflownoteVO queryApproveflowOnSave(EngineService queryDMO, PfParameterVO paraVo, int status) throws BusinessException
	 /*      */   {
	 /*      */     try
	 /*      */     {
	 /*  327 */       return queryDMO.queryApproveflowOnSave(paraVo, status);
	 /*      */     } catch (DbException e) {
	 /*  329 */       Logger.error(e.getMessage(), e);
	 /*  330 */       String msg = NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "wfMachineImpl-0003", null, new String[] { e.getMessage() });
	 /*      */       
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*  335 */       throw new PFBusinessException(msg, e);
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
	 /*      */   private WorkflownoteVO checkWorkflowWhenStart(PfParameterVO paraVO)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*  351 */     EngineService queryDMO = new EngineService();
	 /*      */     int status;
	 /*      */     try {
	 /*  354 */       status = queryDMO.queryWorkflowStatus(paraVO.m_billVersionPK, paraVO.m_billType);
	 /*      */     }
	 /*      */     catch (DbException e) {
	 /*  357 */       Logger.error(e.getMessage(), e);
	 /*  358 */       throw new PFBusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "wfMachineImpl-0004", null, new String[] { e.getMessage() }));
	 /*      */     }
	 /*      */     
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*  365 */     if (paraVO.getCustomProperty("forcestart") == null)
	 /*      */     {
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*  370 */       switch (status)
	 /*      */       {
	 /*      */       case -1: 
	 /*      */       case 3: 
	 /*      */         break;
	 /*      */       
	 /*      */       case 2: 
	 /*  377 */         return null;
	 /*      */       case 0: case 1: default: 
	 /*  379 */         return null;
	 /*      */       }
	 /*      */       
	 /*      */     }
	 /*  383 */     return queryWorkflowOnSave(queryDMO, paraVO, status);
	 /*      */   }
	 /*      */   
	 /*      */   private WorkflownoteVO queryWorkflowOnSave(EngineService queryDMO, PfParameterVO paraVO, int status) throws BusinessException
	 /*      */   {
	 /*      */     try {
	 /*  389 */       return queryDMO.queryWorkflowOnSave(paraVO, status);
	 /*      */     } catch (DbException e) {
	 /*  391 */       Logger.error(e.getMessage(), e);
	 /*      */       
	 /*  393 */       String msg = NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "wfMachineImpl-0005", null, new String[] { e.getMessage() });
	 /*      */       
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*  398 */       throw new PFBusinessException(msg, e);
	 /*      */     }
	 /*      */   }
	 /*      */   
	 /*      */   public RetBackWfVo backCheckFlow(PfParameterVO paraVo) throws BusinessException
	 /*      */   {
	 /*  404 */     if (PfUtilBaseTools.isUnapproveAction(paraVo.m_actionName, paraVo.m_billType))
	 /*      */     {
	 /*      */ 
	 /*  407 */       return backWorkflow(paraVo, WorkflowTypeEnum.Approveflow.getIntValue());
	 /*      */     }
	 /*  409 */     if (PfUtilBaseTools.isRollbackAction(paraVo.m_actionName, paraVo.m_billType))
	 /*      */     {
	 /*      */ 
	 /*  412 */       return backWorkflow(paraVo, WorkflowTypeEnum.Workflow.getIntValue()); }
	 /*  413 */     if (PfUtilBaseTools.isUnSaveAction(paraVo.m_actionName, paraVo.m_billType))
	 /*      */     {
	 /*      */ 
	 /*  416 */       return reCallFlow(paraVo, WorkflowTypeEnum.Approveflow.getIntValue());
	 /*      */     }
	 /*  418 */     if (PfUtilBaseTools.isRecallAction(paraVo.m_actionName, paraVo.m_billType))
	 /*      */     {
	 /*      */ 
	 /*  421 */       return reCallFlow(paraVo, WorkflowTypeEnum.Workflow.getIntValue());
	 /*      */     }
	 /*  423 */     throw new PFBusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "wfMachineImpl-0006") + paraVo.m_actionName);
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
	 /*      */   private RetBackWfVo reCallFlow(PfParameterVO paraVo, int wftype)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*  437 */     RetBackWfVo retBackVO = new RetBackWfVo();
	 /*  438 */     ActionEnvironment.getInstance().putParaVo(paraVo.m_billVersionPK, paraVo);
	 /*      */     try
	 /*      */     {
	 /*  441 */       EngineService queryDMO = new EngineService();
	 /*      */       
	 /*  443 */       ProcessInstance pi = queryDMO.queryProcessInstance(paraVo.m_billVersionPK, paraVo.m_billType, wftype);
	 /*      */       
	 /*      */       boolean isFinished;
	 /*  446 */       if (pi == null)
	 /*      */       {
	 /*  448 */         WorkflowProcess wp = queryDMO.findProcessOfBill(paraVo.m_billVersionPK, paraVo.m_billType, wftype);
	 /*      */         
	 /*  450 */         if (wp != null) {
	 /*  451 */           throw new PFBusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "wfMachineImpl-0007"));
	 /*      */         }
	 /*      */       } else {
	 /*  454 */         if ((!paraVo.m_operator.equals(pi.getBillMaker())) && (!paraVo.m_operator.equals(pi.getBillCommiter())))
	 /*      */         {
	 /*  456 */           throw new PFBusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "wfMachineImpl-0008"));
	 /*      */         }
	 /*      */         
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*  463 */         isFinished = queryDMO.isExistFinishedWorkitem(paraVo.m_billVersionPK, paraVo.m_billType, wftype);
	 /*      */         
	 /*  465 */         if (isFinished) {
	 /*  466 */           throw new PFBusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "wfMachineImpl-0009"));
	 /*      */         }
	 /*      */       }
	 /*      */       
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*  473 */       new WorkflowAdminImpl().terminateWorkflow(paraVo, wftype);
	 /*      */       
	 /*      */ 
	 /*      */ 
	 /*  477 */       retBackVO.setBackState(-1);
	 /*  478 */       return retBackVO;
	 /*      */     } catch (DbException e) {
	 /*  480 */       Logger.error(e.getMessage(), e);
	 /*  481 */       throw new PFBusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "wfMachineImpl-0010", null, new String[] { e.getMessage() }));
	 /*      */ 
	 /*      */     }
	 /*      */     finally
	 /*      */     {
	 /*  486 */       ActionEnvironment.getInstance().putParaVo(paraVo.m_billVersionPK, null);
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
	 /*      */   private RetBackWfVo backWorkflow(PfParameterVO paraVo, int wftype)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*  501 */     RetBackWfVo retBackVO = new RetBackWfVo();
	 /*  502 */     ActionEnvironment.getInstance().putParaVo(paraVo.m_billVersionPK, paraVo);
	 /*      */     try
	 /*      */     {
	 /*  505 */       EngineService queryDMO = new EngineService();
	 /*      */       
	 /*  507 */       paraVo.m_workFlow = queryDMO.checkFinishedWorkitem(paraVo.m_billType, paraVo.m_billVersionPK, paraVo.m_operator, wftype);
	 /*      */       
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*  512 */       if (paraVo.m_workFlow == null)
	 /*      */       {
	 /*  514 */         retBackVO.setIsFinish(UFBoolean.TRUE);
	 /*  515 */         retBackVO.setBackState(-1);
	 /*  516 */         return retBackVO;
	 /*      */       }
	 /*      */       
	 /*  519 */       if (wftype == WorkflowTypeEnum.Workflow.getIntValue()) {
	 /*  520 */         canBackWorkflow(paraVo, wftype);
	 /*      */       }
	 /*      */       
	 /*      */ 
	 /*  524 */       boolean isFinished = queryDMO.isFlowFinished(paraVo.m_billVersionPK, paraVo.m_billType, wftype);
	 /*      */       
	 /*  526 */       if (isFinished)
	 /*      */       {
	 /*  528 */         retBackVO.setIsFinish(UFBoolean.TRUE);
	 /*      */       }
	 /*      */       
	 /*      */ 
	 /*  532 */       WFTask currentTask = paraVo.m_workFlow.getTaskInfo().getTask();
	 /*  533 */       currentTask.setModifyTime(((ITimeService)NCLocator.getInstance().lookup(ITimeService.class)).getUFDateTime());
	 /*      */       
	 /*  535 */       currentTask.setOutObject(paraVo.m_preValueVo);
	 /*  536 */       currentTask.setOperator(paraVo.m_operator);
	 /*  537 */       currentTask.setStatus(WfTaskOrInstanceStatus.Finished.getIntValue());
	 /*      */       
	 /*  539 */       currentTask.setTaskType(WfTaskType.Withdraw.getIntValue());
	 /*      */       
	 /*  541 */       WfTaskManager.getInstance().acceptTaskFromBusi(currentTask);
	 /*      */       
	 /*      */ 
	 /*      */ 
	 /*  545 */       boolean isGoing = queryDMO.isExistFinishedWorkitem(paraVo.m_billVersionPK, paraVo.m_billType, paraVo.m_workFlow.getWorkflow_type().intValue());
	 /*      */       
	 /*      */       WorknoteManager noteMgr;
	 /*  548 */       if (isGoing)
	 /*      */       {
	 /*  550 */         retBackVO.setBackState(2);
	 /*  551 */         retBackVO.setPreCheckMan(paraVo.m_workFlow.getSenderman());
	 /*  552 */         if (isFinished)
	 /*      */         {
	 /*  554 */            noteMgr = new WorknoteManager();
	 /*  555 */           noteMgr.sendMessageToBillMaker(new AbstractMsgL10NCallback()
	 /*      */           {
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */             public String getMessage() {
	 /*  561 */               return "{wfMachineImpl-0011}"; } }, paraVo);
	 /*      */         }
	 /*      */         
	 /*      */       }
	 /*      */       else
	 /*      */       {
	 /*  567 */         retBackVO.setBackState(3);
	 /*  568 */         noteMgr = new WorknoteManager();
	 /*  569 */         noteMgr.sendMessageToBillMaker(new AbstractMsgL10NCallback()
	 /*      */         {
	 /*      */ 
	 /*      */ 
	 /*      */           public String getMessage() {
	 /*  574 */             return "{wfMachineImpl-0011}"; } }, paraVo);
	 /*      */       }
	 /*      */       
	 /*      */ 
	 /*      */ 
	 /*  579 */       return retBackVO;
	 /*      */     } catch (DbException e) {
	 /*  581 */       Logger.error(e.getMessage(), e);
	 /*  582 */       throw new PFBusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "wfMachineImpl-0012", null, new String[] { e.getMessage() }));
	 /*      */ 
	 /*      */     }
	 /*      */     finally
	 /*      */     {
	 /*  587 */       ActionEnvironment.getInstance().putParaVo(paraVo.m_billVersionPK, null);
	 /*      */     }
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */ 
	 /*      */   private void canBackWorkflow(PfParameterVO paraVo, int wftype)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*  596 */     EngineService queryDMO = new EngineService();
	 /*  597 */     WorkflownoteVO notFinishedWorkFlow = null;
	 /*      */     try
	 /*      */     {
	 /*  600 */       notFinishedWorkFlow = queryDMO.checkUnfinishedWorkitem(paraVo, wftype);
	 /*      */     }
	 /*      */     catch (DbException e) {
	 /*  603 */       Logger.error(e.getMessage(), e);
	 /*  604 */       throw new PFBusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "wfMachineImpl-0012", null, new String[] { e.getMessage() }));
	 /*      */     }
	 /*      */     catch (PFBusinessException pfbe) {}
	 /*      */     
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*  611 */     if (notFinishedWorkFlow != null)
	 /*      */     {
	 /*  613 */       if ((notFinishedWorkFlow.getWorkflow_type().intValue() == WorkflowTypeEnum.SubApproveflow.getIntValue()) || (notFinishedWorkFlow.getWorkflow_type().intValue() == WorkflowTypeEnum.SubWorkApproveflow.getIntValue()) || (paraVo.m_workFlow.getWorkflow_type().intValue() == WorkflowTypeEnum.SubApproveflow.getIntValue()) || (paraVo.m_workFlow.getWorkflow_type().intValue() == WorkflowTypeEnum.SubWorkApproveflow.getIntValue()))
	 /*      */       {
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*  621 */         return;
	 /*      */       }
	 /*  623 */       ProcessInstance processInstance = WfInstancePool.getInstance().getProcessInstance(notFinishedWorkFlow.getTaskInfo().getTask().getWfProcessInstancePK());
	 /*      */       
	 /*      */ 
	 /*      */ 
	 /*  627 */       ActivityInstance activityInstance = processInstance.findActivityInstanceByPK(notFinishedWorkFlow.getTaskInfo().getTask().getActivityInstancePK());
	 /*      */       
	 /*      */ 
	 /*  630 */       ProcessInstance processInstanceFinished = WfInstancePool.getInstance().getProcessInstance(paraVo.m_workFlow.getTaskInfo().getTask().getWfProcessInstancePK());
	 /*      */       
	 /*      */ 
	 /*      */ 
	 /*  634 */       ActivityInstance activityInstanceFinished = processInstanceFinished.findActivityInstanceByPK(paraVo.m_workFlow.getTaskInfo().getTask().getActivityInstancePK());
	 /*      */       
	 /*      */ 
	 /*  637 */       Vector srcActivityInstanceGUIDs = activityInstance.getSrcActivityInstancePKs();
	 /*      */       
	 /*  639 */       boolean back = false;
	 /*  640 */       for (int i = 0; i < srcActivityInstanceGUIDs.size(); i++) {
	 /*  641 */         String srcActivityInstanceGUID = (String)srcActivityInstanceGUIDs.get(i);
	 /*      */         
	 /*  643 */         ActivityInstance actInst = processInstance.findActivityInstanceByPK(srcActivityInstanceGUID);
	 /*      */         
	 /*  645 */         if ((actInst.getActivity().getActivityType() == ActivityTypeEnum.Auto.getIntValue()) || (actInst.getActivity().getActivityType() == ActivityTypeEnum.Route.getIntValue()))
	 /*      */         {
	 /*      */ 
	 /*      */ 
	 /*  649 */           back = true;
	 /*      */         }
	 /*      */         else
	 /*      */         {
	 /*  653 */           if (actInst.getActivityID().equals(activityInstance.getActivityID()))
	 /*      */           {
	 /*  655 */             back = true;
	 /*      */           }
	 /*      */           
	 /*  658 */           if (activityInstanceFinished.getActivityID().equals(actInst.getActivityID()))
	 /*      */           {
	 /*  660 */             back = true; }
	 /*      */         }
	 /*      */       }
	 /*  663 */       if (!back) {
	 /*  664 */         throw new PFBusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "wfMachineImpl-0020"));
	 /*      */       }
	 /*      */     }
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */   public void deleteCheckFlow(String billType, String billId, AggregatedValueObject billVO, String checkMan)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*      */     try
	 /*      */     {
	 /*  679 */       EngineService es = new EngineService();
	 /*  680 */       int status = es.queryApproveflowStatus(billId, billType);
	 /*      */       
	 /*  682 */       switch (status) {
	 /*      */       case -1: 
	 /*      */       case 0: 
	 /*      */       case 1: 
	 /*      */       case 3: 
	 /*  687 */         es.rollbackWorkflow(billId, billType, billVO, WorkflowTypeEnum.Approveflow.getIntValue());
	 /*      */         
	 /*  689 */         es.deleteWorkflow(billId, billType, false, WorkflowTypeEnum.Approveflow.getIntValue());
	 /*      */         
	 /*  691 */         break;
	 /*      */       
	 /*      */       case 2: 
	 /*  694 */         es.rollbackWorkflow(billId, billType, billVO, WorkflowTypeEnum.Approveflow.getIntValue());
	 /*      */         
	 /*  696 */         es.deleteWorkflow(billId, billType, false, WorkflowTypeEnum.Approveflow.getIntValue());
	 /*      */         
	 /*      */ 
	 /*  699 */         WorknoteManager noteMgr = new WorknoteManager();
	 /*  700 */         noteMgr.sendAllPersonMessage(new AbstractMsgL10NCallback()
	 /*      */         {
	 /*      */ 
	 /*      */           public String getMessage() {
	 /*  704 */             return "{UPPpfworkflow-000272}"; } }, null, billId, billType, checkMan);
	 /*      */       
	 /*      */ 
	 /*      */       }
	 /*      */       
	 /*      */     }
	 /*      */     catch (DbException ex)
	 /*      */     {
	 /*  712 */       Logger.error(ex.getMessage(), ex);
	 /*  713 */       throw new PFBusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "wfMachineImpl-0013", null, new String[] { ex.getMessage() }));
	 /*      */     }
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */   public int forwardCheckFlow(PfParameterVO paraVo)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*  721 */     Logger.error("###WorkflowMachineImpl forwardCheckFlow 开始 " + System.currentTimeMillis() + "ms");
	 /*      */     
	 /*  723 */     ActionEnvironment.getInstance().putParaVo(paraVo.m_billVersionPK, paraVo);
	 /*      */     
	 /*  725 */     WFTask currentTask = paraVo.m_workFlow.getTaskInfo().getTask();
	 /*      */     
	 /*  727 */     List<ActivityInstance> lstActIns = null;
	 /*      */     try {
	 /*  729 */       lstActIns = queryActInsByPrceInsPK(currentTask.getWfProcessInstancePK(), new int[] { 0, 1 });
	 /*      */       
	 /*  731 */       paraVo.setOldActIns(lstActIns);
	 /*      */     } catch (XPDLParserException e) {
	 /*  733 */       Logger.error(e.getMessage(), e);
	 /*      */     } catch (DbException e) {
	 /*  735 */       Logger.error(e.getMessage(), e);
	 /*      */     }
	 /*      */     
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*  741 */     if (currentTask.getTaskType() == WfTaskType.Backward.getIntValue())
	 /*      */     {
	 /*      */ 
	 /*  744 */       if (paraVo.m_workFlow.getActiontype().endsWith("_A"))
	 /*      */       {
	 /*  746 */         throw new PFBusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow61_0", "0pfworkflow61-0087"));
	 /*      */       }
	 /*      */       
	 /*      */ 
	 /*      */ 
	 /*      */       try
	 /*      */       {
	 /*  753 */        int status = new EngineService().queryFlowStatus(paraVo.m_billVersionPK, paraVo.m_billType, currentTask.getWorkflowType(), currentTask.getApproveResult());
	 /*      */       }
	 /*      */       catch (DbException ex)
	 /*      */       {
	 /*      */         int status;
	 /*      */         
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*  766 */         Logger.error(ex.getMessage(), ex);
	 /*      */       }
	 /*      */       
	 /*      */ 
	 /*  770 */       currentTask.setModifyTime(((ITimeService)NCLocator.getInstance().lookup(ITimeService.class)).getUFDateTime());
	 /*      */       
	 /*  772 */       currentTask.setOutObject(paraVo.m_preValueVo);
	 /*  773 */       currentTask.setOperator(paraVo.m_operator);
	 /*  774 */       currentTask.setNote(paraVo.m_workFlow.getChecknote());
	 /*  775 */       currentTask.setStatus(WfTaskOrInstanceStatus.Finished.getIntValue());
	 /*      */       
	 /*  777 */       currentTask.setTaskType(WfTaskType.Backward.getIntValue());
	 /*      */       
	 /*      */ 
	 /*  780 */       currentTask.setApproveResult("R");
	 /*      */     }
	 /*      */     else
	 /*      */     {
	 /*  784 */       fillAssignableInfo(paraVo, currentTask);
	 /*      */       
	 /*  786 */       fillTransitionSelectableInfo(paraVo, currentTask);
	 /*      */       
	 /*  788 */       currentTask.setOutObject(paraVo.m_preValueVo);
	 /*  789 */       currentTask.setOperator(paraVo.m_operator);
	 /*  790 */       if ("Y".equals(paraVo.m_workFlow.getApproveresult())) {
	 /*  791 */         currentTask.setApproveResult("Y");
	 /*      */       } else {
	 /*  793 */         currentTask.setApproveResult("N");
	 /*      */       }
	 /*  795 */       currentTask.setModifyTime(((ITimeService)NCLocator.getInstance().lookup(ITimeService.class)).getUFDateTime());
	 /*      */       
	 /*  797 */       currentTask.setNote(paraVo.m_workFlow.getChecknote());
	 /*  798 */       currentTask.setStatus(WfTaskOrInstanceStatus.Finished.getIntValue());
	 /*      */     }
	 /*      */     
	 /*      */ 
	 /*      */ 
	 /*  803 */     Logger.error("###WorkflowMachineImpl call acceptTaskFromBusi 开始 " + System.currentTimeMillis() + "ms");
	 /*      */     
	 /*  805 */     WfTaskManager.getInstance().acceptTaskFromBusi(currentTask);
	 /*  806 */     Logger.error("###WorkflowMachineImpl call forwardCheckFlow 结束 " + System.currentTimeMillis() + "ms");
	 /*      */     
	 /*      */ 
	 /*  809 */     PfMessageUtil.setHandled(paraVo.m_workFlow);
	 /*      */     
	 /*  811 */     PfParameterVO tmpparaVo = ActionEnvironment.getInstance().getParaVo(paraVo.m_billVersionPK + "@auto");
	 /*      */     
	 /*  813 */     if (tmpparaVo == null) {
	 /*  814 */       tmpparaVo = ActionEnvironment.getInstance().getParaVo(paraVo.m_billVersionPK);
	 /*      */     }
	 /*      */     
	 /*      */ 
	 /*      */ 
	 /*  819 */     WorkflownoteVO curTaskNoteVO = paraVo.m_workFlow;
	 /*      */     
	 /*  821 */     paraVo.m_billType = tmpparaVo.m_billType;
	 /*  822 */     paraVo.emendEnum = tmpparaVo.emendEnum;
	 /*  823 */     paraVo.m_actionName = tmpparaVo.m_actionName;
	 /*  824 */     paraVo.m_autoApproveAfterCommit = tmpparaVo.m_autoApproveAfterCommit;
	 /*  825 */     paraVo.m_billEntities = tmpparaVo.m_billEntities;
	 /*  826 */     paraVo.m_billEntity = tmpparaVo.m_billEntity;
	 /*  827 */     paraVo.m_billId = tmpparaVo.m_billId;
	 /*  828 */     paraVo.m_billNo = tmpparaVo.m_billNo;
	 /*  829 */     paraVo.m_billVersionPK = tmpparaVo.m_billVersionPK;
	 /*  830 */     paraVo.m_businessType = tmpparaVo.m_businessType;
	 /*  831 */     paraVo.m_flowDefPK = tmpparaVo.m_flowDefPK;
	 /*  832 */     paraVo.m_makeBillOperator = tmpparaVo.m_makeBillOperator;
	 /*  833 */     paraVo.m_operator = tmpparaVo.m_operator;
	 /*  834 */     paraVo.m_pkGroup = tmpparaVo.m_pkGroup;
	 /*  835 */     paraVo.m_pkOrg = tmpparaVo.m_pkOrg;
	 /*  836 */     paraVo.m_preValueVo = tmpparaVo.m_preValueVo;
	 /*  837 */     paraVo.m_preValueVos = tmpparaVo.m_preValueVos;
	 /*  838 */     paraVo.m_splitValueVos = tmpparaVo.m_splitValueVos;
	 /*  839 */     paraVo.m_standHeadVo = tmpparaVo.m_standHeadVo;
	 /*  840 */     paraVo.m_userObj = tmpparaVo.m_userObj;
	 /*  841 */     paraVo.m_userObjs = tmpparaVo.m_userObjs;
	 /*  842 */     paraVo.m_workFlow = tmpparaVo.m_workFlow;
	 /*      */     
	 /*      */ 
	 /*  845 */     int status = -10;
	 /*      */     try {
	 /*  847 */       EngineService engine = new EngineService();
	 /*  848 */       int iCurrentWfType = currentTask.getWorkflowType();
	 /*  849 */       status = engine.queryFlowStatus(paraVo.m_billVersionPK, paraVo.m_billType, iCurrentWfType, currentTask.getApproveResult());
	 /*      */       
	 /*      */ 
	 /*  852 */       if ((status == 1) || (status == 0))
	 /*      */       {
	 /*      */ 
	 /*  855 */         engine.updateUnfinishedActInstancesToInefficient(currentTask.getBillversionPK(), currentTask.getBillType(), iCurrentWfType);
	 /*      */ 
	 /*      */       }
	 /*  858 */       else if (status == -1)
	 /*      */       {
	 /*  860 */         if (!currentTask.isSubmit2RjectTache()) {
	 /*  861 */           engine.updateAllActivityInstancesToInefficient(currentTask.getBillID(), currentTask.getBillType(), iCurrentWfType);
	 /*      */         }
	 /*      */       }
	 /*      */       
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*  872 */       if (currentTask.getTaskType() == WfTaskType.Backward.getIntValue()) {
	 /*  873 */         engine.rollbackGadgets(paraVo);
	 /*      */       }
	 /*      */       
	 /*  876 */       WorknoteManager noteMgr = new WorknoteManager();
	 /*  877 */       IMsgL10NCallback msgStr = null;
	 /*      */       
	 /*  879 */       if (!StringUtil.isEmptyWithTrim(paraVo.m_workFlow.getObserver())) {
	 /*  880 */         String actiontype = paraVo.m_workFlow.getActiontype();
	 /*  881 */         if (!StringUtil.isEmptyWithTrim(actiontype)) {
	 /*  882 */           if (actiontype.endsWith("_A"))
	 /*      */           {
	 /*  884 */             msgStr = new AbstractMsgL10NCallback()
	 /*      */             {
	 /*      */ 
	 /*      */               public String getMessage()
	 /*      */               {
	 /*  889 */                 return "{wfMachineImpl-0014}";
	 /*      */               }
	 /*      */             };
	 /*  892 */           } else if (actiontype.endsWith("_D"))
	 /*      */           {
	 /*  894 */             msgStr = new AbstractMsgL10NCallback()
	 /*      */             {
	 /*      */ 
	 /*      */               public String getMessage()
	 /*      */               {
	 /*  899 */                 return "{wfMachineImpl-0015}";
	 /*      */               }
	 /*      */             };
	 /*      */           }
	 /*      */           
	 /*  904 */           if (msgStr != null) {
	 /*  905 */             noteMgr.sendMessage(msgStr, paraVo, paraVo.m_workFlow.getObserver());
	 /*      */           }
	 /*      */         }
	 /*      */       }
	 /*      */       
	 /*      */ 
	 /*  911 */       if (status == 1)
	 /*      */       {
	 /*  913 */         Logger.debug("***流程实例结束之审批通过,则给制单人发送业务消息***");
	 /*  914 */         final boolean isWorkflow = WorkflowTypeEnum.isWorkflowInstance(iCurrentWfType);
	 /*      */         
	 /*      */ 
	 /*  917 */         msgStr = new AbstractMsgL10NCallback()
	 /*      */         {
	 /*      */           public String getMessage()
	 /*      */           {
	 /*  921 */             return isWorkflow ? "{UPPpfworkflow-000701}" : "{UPPpfworkflow-000273}";
	 /*      */           }
	 /*      */           
	 /*  924 */         };
	 /*  925 */         noteMgr.sendMessageToBillMaker(msgStr, paraVo);
	 /*      */       }
	 /*  927 */       else if (status == 0)
	 /*      */       {
	 /*  929 */         Logger.debug("***流程实例结束之审批不通过,则给参与本次流程的人发送业务消息***");
	 /*      */         
	 /*  931 */         msgStr = new AbstractMsgL10NCallback()
	 /*      */         {
	 /*      */           public String getMessage()
	 /*      */           {
	 /*  935 */             return "{UPPpfworkflow-000274}";
	 /*      */           }
	 /*  937 */         };
	 /*  938 */         noteMgr.sendMessageAfterNoPass(msgStr, paraVo);
	 /*      */       }
	 /*      */       
	 /*      */ 
	 /*      */ 
	 /*  943 */       IWorkflowAdmin wfAdmin = (IWorkflowAdmin)NCLocator.getInstance().lookup(IWorkflowAdmin.class);
	 /*      */       
	 /*      */ 
	 /*      */ 
	 /*  947 */       boolean isTrack = paraVo.m_workFlow.isTrack();
	 /*  948 */       wfAdmin.trackWFinstance(paraVo.m_workFlow, paraVo.m_workFlow.getCheckman(), isTrack);
	 /*      */       
	 /*      */ 
	 /*      */ 
	 /*  952 */       Logger.debug("***发抄送信息***");
	 /*  953 */       String result = currentTask.getApproveResult();
	 /*  954 */       String title = "";
	 /*  955 */       if ("Y".equals(result)) {
	 /*  956 */         title = WorkflowTypeEnum.isWorkflowInstance(iCurrentWfType) ? "{UPPpfworkflow-000701}" : "{UPPpfworkflow-000273}";
	 /*      */       }
	 /*  958 */       else if ("N".equals(result)) {
	 /*  959 */         title = "{UPPpfworkflow-000274}";
	 /*  960 */       } else if ("R".equals(result)) {
	 /*  961 */         title = "{UPPpfworkflow-000804}";
	 /*      */       }
	 /*      */       
	 /*  964 */       String note = curTaskNoteVO.getChecknote();
	 /*  965 */       wfAdmin.cpySendByMailAndMsg(curTaskNoteVO, new String[] { title, note });
	 /*      */     }
	 /*      */     catch (DbException ex)
	 /*      */     {
	 /*  969 */       Logger.error(ex.getMessage(), ex);
	 /*  970 */       throw new PFBusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "wfMachineImpl-0016", null, new String[] { ex.getMessage() }), ex);
	 /*      */ 
	 /*      */ 
	 /*      */     }
	 /*      */     finally
	 /*      */     {
	 /*      */ 
	 /*  977 */       ActionEnvironment.getInstance().putParaVo(paraVo.m_billVersionPK, null);
	 /*      */       
	 /*  979 */       ActionEnvironment.getInstance().putParaVo(paraVo.m_billVersionPK + "@auto", null);
	 /*      */     }
	 /*      */     
	 /*  982 */     Logger.error("###WorkflowMachineImpl forwardCheckFlow 结束 " + System.currentTimeMillis() + "ms");
	 /*      */     
	 /*  984 */     return status;
	 /*      */   }
	 /*      */   
	 /*      */   private List<AttachmentVO> getAttachmentOfTask(String pk_wf_task) throws BusinessException
	 /*      */   {
	 /*  989 */     BaseDAO dao = new BaseDAO();
	 /*  990 */     EnginePersistence persist = new EnginePersistence();
	 /*      */     
	 /*  992 */     String sql = null;
	 /*  993 */     SQLParameter param = null;
	 /*      */     
	 /*  995 */     sql = "select pk_wf_instance from pub_wf_task where pk_wf_task=?";
	 /*  996 */     param = new SQLParameter();
	 /*  997 */     param.addParam(pk_wf_task);
	 /*      */     
	 /*  999 */     String pk_wf_instance = (String)dao.executeQuery(sql, param, new ColumnProcessor());
	 /*      */     
	 /*      */ 
	 /* 1002 */     List<String> instList = new ArrayList();
	 /*      */     
	 /* 1004 */     while (!StringUtil.isEmptyWithTrim(pk_wf_instance)) {
	 /* 1005 */       instList.add(pk_wf_instance);
	 /*      */       
	 /* 1007 */       pk_wf_instance = persist.findParentProcessInstancePK(pk_wf_instance);
	 /*      */     }
	 /*      */     
	 /*      */ 
	 /* 1011 */     if (instList.size() == 0) {
	 /* 1012 */       return null;
	 /*      */     }
	 /*      */     
	 /* 1015 */     param = new SQLParameter();
	 /* 1016 */     param.addParam(WfTaskOrInstanceStatus.Inefficient.getIntValue());
	 /* 1017 */     StringBuffer sb = new StringBuffer();
	 /*      */     
	 /* 1019 */     for (String pk : instList) {
	 /* 1020 */       sb.append(",?");
	 /* 1021 */       param.addParam(pk);
	 /*      */     }
	 /*      */     
	 /* 1024 */     sql = "select t.* from pub_workflownote_att t join pub_workflownote n on t.pk_checkflow=n.pk_checkflow join pub_wf_task k on n.pk_wf_task=k.pk_wf_task where n.approvestatus<>? and k.pk_wf_instance in (" + sb.substring(1) + ") order by t.ts desc";
	 /*      */     
	 /*      */ 
	 /* 1027 */     Collection<WorkflownoteAttVO> col = (Collection)dao.executeQuery(sql, param, new BeanListProcessor(WorkflownoteAttVO.class));
	 /*      */     
	 /*      */ 
	 /*      */ 
	 /* 1031 */     List<AttachmentVO> attList = new ArrayList();
	 /*      */     
	 /* 1033 */     for (WorkflownoteAttVO noteAttVO : col) {
	 /* 1034 */       AttachmentVO attVO = new AttachmentVO();
	 /*      */       
	 /* 1036 */       attVO.setPk_file(noteAttVO.getPk_file());
	 /* 1037 */       attVO.setFilename(noteAttVO.getFilename());
	 /* 1038 */       attVO.setFilesize(noteAttVO.getFilesize());
	 /*      */       
	 /* 1040 */       attList.add(attVO);
	 /*      */     }
	 /*      */     
	 /* 1043 */     return attList;
	 /*      */   }
	 /*      */   
	 /*      */   public boolean sendWorkFlowOnSave(PfParameterVO paraVo, Hashtable m_methodReturnHas, HashMap hmPfExParams)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1049 */     Logger.debug("BILLNO**********" + paraVo.m_billNo + "**********");
	 /* 1050 */     Logger.debug("BILLID**********" + paraVo.m_billVersionPK + "**********");
	 /*      */     
	 /* 1052 */     ActionEnvironment.getInstance().putParaVo(paraVo.m_billVersionPK, paraVo);
	 /*      */     
	 /* 1054 */     ActionEnvironment.getInstance().putMethodReturn(paraVo.m_billVersionPK, m_methodReturnHas);
	 /*      */     try
	 /*      */     {
	 /* 1057 */       boolean isWorkflow = PfUtilBaseTools.isStartAction(paraVo.m_actionName, paraVo.m_billType);
	 /*      */       
	 /* 1059 */       if (!isWorkflow)
	 /*      */       {
	 /* 1061 */         boolean[] wfRet = startApproveflow(paraVo, hmPfExParams);
	 /* 1062 */         return wfRet[0];
	 /*      */       }
	 /*      */       
	 /* 1065 */       boolean[] wfRet = startWorkflow(paraVo, hmPfExParams);
	 /* 1066 */       return wfRet[0];
	 /*      */ 
	 /*      */     }
	 /*      */     finally
	 /*      */     {
	 /* 1071 */       ActionEnvironment.getInstance().putParaVo(paraVo.m_billVersionPK, null);
	 /*      */       
	 /* 1073 */       ActionEnvironment.getInstance().putMethodReturn(paraVo.m_billVersionPK, null);
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
	 /*      */   private boolean[] startWorkflow(PfParameterVO paraVo, HashMap hmPfExParams)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1088 */     Logger.debug("动作编码为" + paraVo.m_actionName + "，启动工作流");
	 /*      */     
	 /*      */ 
	 /* 1091 */     deleteWhenStartExceptPassOrGoing(paraVo, WorkflowTypeEnum.Workflow.getIntValue());
	 /*      */     
	 /*      */ 
	 /* 1094 */     if ((paraVo.m_workFlow == null) || (paraVo.m_workFlow.getTaskInfo().getTask() == null))
	 /*      */     {
	 /*      */ 
	 /* 1097 */       Object paramDefPK = hmPfExParams == null ? null : hmPfExParams.get("flowdefpk");
	 /*      */       
	 /* 1099 */       paraVo.m_flowDefPK = (paramDefPK == null ? null : String.valueOf(paramDefPK));
	 /*      */       
	 /*      */ 
	 /* 1102 */       Object noteChecked = hmPfExParams == null ? null : hmPfExParams.get("notechecked");
	 /*      */       
	 /*      */ 
	 /* 1105 */       if (noteChecked == null)
	 /* 1106 */         paraVo.m_workFlow = checkWorkflowWhenStart(paraVo);
	 /* 1107 */       if (paraVo.m_workFlow == null)
	 /*      */       {
	 /* 1109 */         return new boolean[] { false, false };
	 /*      */       }
	 /*      */     }
	 /* 1112 */     WFTask currentTask = paraVo.m_workFlow.getTaskInfo().getTask();
	 /*      */     
	 /* 1114 */     currentTask.setBillNO(paraVo.m_billNo);
	 /* 1115 */     currentTask.setBillID(paraVo.m_billId);
	 /* 1116 */     currentTask.setBillversionPK(paraVo.m_billVersionPK);
	 /*      */     
	 /*      */ 
	 /* 1119 */     fillAssignableInfo(paraVo, currentTask);
	 /*      */     
	 /* 1121 */     fillTransitionSelectableInfo(paraVo, currentTask);
	 /*      */     
	 /*      */ 
	 /* 1124 */     currentTask.setOutObject(paraVo.m_preValueVo);
	 /*      */     
	 /* 1126 */     currentTask.setOperator(paraVo.m_makeBillOperator);
	 /* 1127 */     currentTask.setModifyTime(((ITimeService)NCLocator.getInstance().lookup(ITimeService.class)).getUFDateTime());
	 /*      */     
	 /* 1129 */     currentTask.setStatus(WfTaskOrInstanceStatus.Finished.getIntValue());
	 /*      */     
	 /*      */ 
	 /* 1132 */     ExecuteResult result = WfTaskManager.getInstance().acceptTaskFromBusi(currentTask);
	 /*      */     
	 /* 1134 */     if ((result != null) && (result.isApprovePass()))
	 /*      */     {
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /* 1140 */       return new boolean[] { true, false };
	 /*      */     }
	 /*      */     
	 /* 1143 */     return new boolean[] { true, false };
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */   private void deleteWhenStartExceptPassOrGoing(PfParameterVO paraVo, int iWorkflowOrApproveflow)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1156 */     EngineService es = new EngineService();
	 /*      */     int status;
	 /*      */     try {
	 /* 1159 */       status = iWorkflowOrApproveflow == WorkflowTypeEnum.Workflow.getIntValue() ? es.queryWorkflowStatus(paraVo.m_billVersionPK, paraVo.m_billType) : es.queryApproveflowStatus(paraVo.m_billVersionPK, paraVo.m_billType);
	 /*      */ 
	 /*      */     }
	 /*      */     catch (DbException e)
	 /*      */     {
	 /*      */ 
	 /* 1165 */       Logger.error(e.getMessage(), e);
	 /* 1166 */       throw new PFBusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "wfMachineImpl-0017", null, new String[] { e.getMessage() }));
	 /*      */     }
	 /*      */     
	 /*      */ 
	 /*      */ 
	 /* 1171 */     if (paraVo.getCustomProperty("forcestart") == null)
	 /*      */     {
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /* 1180 */       switch (status) {
	 /*      */       case -1: 
	 /*      */       case 0: 
	 /*      */       case 3: 
	 /*      */         break;
	 /*      */       case 1: 
	 /*      */       case 2: 
	 /*      */       default: 
	 /* 1188 */         return;
	 /*      */       }
	 /*      */       
	 /*      */     }
	 /* 1192 */     deleteWorkflow(es, paraVo, iWorkflowOrApproveflow);
	 /*      */   }
	 /*      */   
	 /*      */   private void deleteWorkflow(EngineService es, PfParameterVO paraVo, int type) throws BusinessException
	 /*      */   {
	 /*      */     try {
	 /* 1198 */       es.rollbackWorkflow(paraVo.m_billVersionPK, paraVo.m_billType, paraVo.m_preValueVo, type);
	 /*      */       
	 /* 1200 */       es.deleteWorkflow(paraVo.m_billVersionPK, paraVo.m_billType, true, type);
	 /*      */     }
	 /*      */     catch (DbException e) {
	 /* 1203 */       Logger.error(e.getMessage(), e);
	 /*      */       
	 /* 1205 */       String msg = NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "wfMachineImpl-0018", null, new String[] { e.getMessage() });
	 /*      */       
	 /*      */ 
	 /* 1208 */       throw new PFBusinessException(msg, e);
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
	 /*      */   private boolean[] startApproveflow(PfParameterVO paraVo, HashMap hmPfExParams)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1222 */     Logger.debug("动作编码为" + paraVo.m_actionName + "，启动审批流");
	 /*      */     
	 /* 1224 */     if ((paraVo.m_workFlow == null) || ((paraVo.m_workFlow.getTaskInfo().getTask() != null) && (StringUtil.isEmptyWithTrim(paraVo.m_workFlow.getTaskInfo().getTask().getRejectTacheActivityID()))))
	 /*      */     {
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /* 1229 */       deleteWhenStartExceptPassOrGoing(paraVo, WorkflowTypeEnum.Approveflow.getIntValue());
	 /*      */     }
	 /*      */     
	 /* 1232 */     if ((paraVo.m_workFlow == null) || (paraVo.m_workFlow.getTaskInfo().getTask() == null))
	 /*      */     {
	 /*      */ 
	 /* 1235 */       Object paramDefPK = hmPfExParams == null ? null : hmPfExParams.get("flowdefpk");
	 /*      */       
	 /* 1237 */       paraVo.m_flowDefPK = (paramDefPK == null ? null : String.valueOf(paramDefPK));
	 /*      */       
	 /*      */ 
	 /* 1240 */       Object noteChecked = hmPfExParams == null ? null : hmPfExParams.get("notechecked");
	 /*      */       
	 /*      */ 
	 /* 1243 */       if (noteChecked == null)
	 /* 1244 */         paraVo.m_workFlow = checkApproveflowWhenSave(paraVo);
	 /* 1245 */       if (paraVo.m_workFlow == null)
	 /*      */       {
	 /* 1247 */         return new boolean[] { false, false };
	 /*      */       }
	 /*      */     }
	 /* 1250 */     WFTask startTask = paraVo.m_workFlow.getTaskInfo().getTask();
	 /*      */     
	 /* 1252 */     startTask.setBillNO(paraVo.m_billNo);
	 /* 1253 */     startTask.setBillID(paraVo.m_billId);
	 /* 1254 */     startTask.setBillversionPK(paraVo.m_billVersionPK);
	 /*      */     
	 /*      */ 
	 /* 1257 */     fillAssignableInfo(paraVo, startTask);
	 /*      */     
	 /*      */ 
	 /* 1260 */     startTask.setOutObject(paraVo.m_preValueVo);
	 /*      */     
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /* 1269 */     startTask.setOperator(paraVo.m_makeBillOperator);
	 /*      */     
	 /* 1271 */     startTask.setModifyTime(((ITimeService)NCLocator.getInstance().lookup(ITimeService.class)).getUFDateTime());
	 /*      */     
	 /* 1273 */     startTask.setStatus(WfTaskOrInstanceStatus.Finished.getIntValue());
	 /*      */     
	 /*      */ 
	 /* 1276 */     ExecuteResult result = WfTaskManager.getInstance().acceptTaskFromBusi(startTask);
	 /*      */     
	 /* 1278 */     if ((result != null) && (result.isApprovePass()))
	 /*      */     {
	 /* 1280 */       return new boolean[] { true, true };
	 /*      */     }
	 /*      */     
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /* 1286 */     return new boolean[] { true, false };
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */   private void fillAssignableInfo(PfParameterVO paraVo, WFTask currentTask)
	 /*      */   {
	 /* 1296 */     Vector assInfos = paraVo.m_workFlow.getTaskInfo().getAssignableInfos();
	 /* 1297 */     for (int i = 0; i < assInfos.size(); i++) {
	 /* 1298 */       AssignableInfo assInfo = (AssignableInfo)assInfos.get(i);
	 /* 1299 */       currentTask.setAssignNextOperators(WFTask.getActAssignID(assInfo.getProcessDefPK(), assInfo.getActivityDefId()), assInfo.getAssignedOperatorPKs());
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
	 /*      */   private void fillTransitionSelectableInfo(PfParameterVO paraVo, WFTask currentTask)
	 /*      */   {
	 /* 1314 */     Vector tsInfos = paraVo.m_workFlow.getTaskInfo().getTransitionSelectableInfos();
	 /*      */     
	 /* 1316 */     for (int i = 0; i < tsInfos.size(); i++) {
	 /* 1317 */       TransitionSelectableInfo tsInfo = (TransitionSelectableInfo)tsInfos.get(i);
	 /*      */       
	 /* 1319 */       if (tsInfo.isChoiced()) {
	 /* 1320 */         Vector vec = new Vector();
	 /* 1321 */         vec.add(tsInfo.getTransitionDefId());
	 /* 1322 */         currentTask.setAssignNextTransition(WFTask.getTransAssignID(tsInfo.getProcessDefPK(), tsInfo.getTransitionDefId()), vec);
	 /*      */       }
	 /*      */     }
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */   public Object processSingleBillFlow_RequiresNew(String actionName, String billOrTranstype, WorkflownoteVO workflowVo, AggregatedValueObject billvo, Object userObj, HashMap param)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*      */     try
	 /*      */     {
	 /* 1335 */       return ((IPFBusiAction)NCLocator.getInstance().lookup(IPFBusiAction.class)).processAction(actionName, billOrTranstype, workflowVo, billvo, userObj, param);
	 /*      */ 
	 /*      */     }
	 /*      */     catch (Exception e)
	 /*      */     {
	 /*      */ 
	 /* 1341 */       throw e;
	 /*      */     } finally {
	 /* 1343 */       new AttachmentReturnExcutor().postProcess();
	 /*      */     }
	 /*      */   }
	 /*      */   
	 /*      */   public boolean[] sendWorkFlowOnSave_RequiresNew(PfParameterVO paraVo, Hashtable returnHas, HashMap hmPfExParams)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1350 */     return sendWorkFlowOnSaveWithFinishJudge(paraVo, returnHas, hmPfExParams);
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
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */   public RetBackWfVo reCallFlow_RequiresNew(PfParameterVO paraVo, int wftype)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1381 */     Logger.debug("BILLNO**********" + paraVo.m_billNo + "**********");
	 /* 1382 */     Logger.debug("BILLID**********" + paraVo.m_billVersionPK + "**********");
	 /*      */     
	 /* 1384 */     return reCallFlow(paraVo, wftype);
	 /*      */   }
	 /*      */   
	 /*      */   public String getBillMaker(String processInstPk) throws BusinessException
	 /*      */   {
	 /* 1389 */     EnginePersistence persistenceDmo = new EnginePersistence();
	 /*      */     try
	 /*      */     {
	 /* 1392 */       ProcessInstance instance = persistenceDmo.loadProcessInstance(processInstPk);
	 /* 1393 */       return instance.getBillMaker();
	 /*      */     } catch (DbException e) {
	 /* 1395 */       Logger.error(e.getMessage(), e);
	 /* 1396 */       throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "wfMachineImpl-0019"));
	 /*      */     }
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */   public String findParentProcessInstancePK(String subProcessInstancePK)
	 /*      */     throws DAOException
	 /*      */   {
	 /* 1406 */     return new EnginePersistence().findParentProcessInstancePK(subProcessInstancePK);
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */   public boolean[] sendWorkFlowOnSaveWithFinishJudge(PfParameterVO paraVo, Hashtable returnHas, HashMap hmPfExParams)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1413 */     Logger.debug("BILLNO**********" + paraVo.m_billNo + "**********");
	 /* 1414 */     Logger.debug("BILLID**********" + paraVo.m_billVersionPK + "**********");
	 /*      */     
	 /* 1416 */     ActionEnvironment.getInstance().putParaVo(paraVo.m_billVersionPK, paraVo);
	 /*      */     
	 /* 1418 */     ActionEnvironment.getInstance().putMethodReturn(paraVo.m_billVersionPK, returnHas);
	 /*      */     try
	 /*      */     {
	 /* 1421 */       boolean isWorkflow = PfUtilBaseTools.isStartAction(paraVo.m_actionName, paraVo.m_billType);
	 /*      */       boolean[] arrayOfBoolean;
	 /* 1423 */       if (!isWorkflow)
	 /*      */       {
	 /* 1425 */         return startApproveflow(paraVo, hmPfExParams);
	 /*      */       }
	 /*      */       
	 /* 1428 */       return startWorkflow(paraVo, hmPfExParams);
	 /*      */     }
	 /*      */     finally
	 /*      */     {
	 /* 1432 */       ActionEnvironment.getInstance().putParaVo(paraVo.m_billVersionPK, null);
	 /*      */       
	 /* 1434 */       ActionEnvironment.getInstance().putMethodReturn(paraVo.m_billVersionPK, null);
	 /*      */     }
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */ 
	 /*      */   public PFClientBizRetVO executeClientBizProcess(AggregatedValueObject billVo, WorkflownoteVO wfVo, boolean isMakeBill)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1443 */     if ((wfVo != null) && (wfVo.getApplicationArgs() != null) && (wfVo.getApplicationArgs().size() > 0))
	 /*      */     {
	 /* 1445 */       String billtype = wfVo.getTaskInfo().getTask().getBillType();
	 /*      */       
	 /* 1447 */       PFClientBizRetVO ret = null;
	 /* 1448 */       Throwable e = null;
	 /*      */       try
	 /*      */       {
	 /* 1451 */         ret = executeClient(billtype, billVo, wfVo, isMakeBill);
	 /*      */         
	 /* 1453 */         if (ret != null) {
	 /* 1454 */           return ret;
	 /*      */         }
	 /*      */       } catch (Throwable ex) {
	 /* 1457 */         Logger.error(ex.getMessage(), ex);
	 /* 1458 */         e = ex;
	 /*      */       }
	 /*      */       try
	 /*      */       {
	 /* 1462 */         Logger.debug("begin execute bs");
	 /* 1463 */         ret = executeBS(billtype, billVo, wfVo, isMakeBill);
	 /* 1464 */         Logger.debug("after execute bs, ret is " + ret);
	 /*      */         
	 /* 1466 */         if (ret != null) {
	 /* 1467 */           return ret;
	 /*      */         }
	 /*      */       } catch (Exception ex) {
	 /* 1470 */         Logger.error(ex.getMessage(), ex);
	 /* 1471 */         if ((ex instanceof BusinessException)) {
	 /* 1472 */           throw ((BusinessException)ex);
	 /*      */         }
	 /* 1474 */         throw new BusinessException(ex.getMessage(), ex);
	 /*      */       }
	 /*      */       
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /* 1480 */       if (e != null) {
	 /* 1481 */         if ((e instanceof BusinessException)) {
	 /* 1482 */           throw ((BusinessException)e);
	 /*      */         }
	 /* 1484 */         throw new BusinessException(e.getMessage(), e);
	 /*      */       }
	 /*      */     }
	 /*      */     
	 /*      */ 
	 /* 1489 */     Logger.debug("no result for client biz process");
	 /* 1490 */     return null;
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */   private PFClientBizRetVO executeBS(String billtype, AggregatedValueObject billVo, WorkflownoteVO wfVo, boolean isMakeBill)
	 /*      */     throws Exception
	 /*      */   {
	 /* 1497 */     ArrayList<Billtype2VO> bt2VOs = PfDataCache.getBillType2Info(billtype, ExtendedClassEnum.PROC_CLIENT_BS.getIntValue());
	 /*      */     
	 /*      */ 
	 /* 1500 */     Logger.debug("billtype: " + billtype + " has been registed " + bt2VOs.size() + " IPFClientBizProcessBS implementations");
	 /*      */     
	 /*      */ 
	 /*      */ 
	 /* 1504 */     Iterator iterator = bt2VOs.iterator(); if (iterator.hasNext()) {
	 /* 1505 */       Billtype2VO bt2VO = (Billtype2VO)iterator.next();
	 /*      */       
	 /* 1507 */       Logger.debug("IPFClientBizProcessBS implementation name: " + bt2VO.getClassname());
	 /*      */       
	 /*      */ 
	 /* 1510 */       Object obj = PfUtilTools.findBizImplOfBilltype(billtype, bt2VO.getClassname());
	 /*      */       
	 /* 1512 */       PfClientBizProcessContext context = new PfClientBizProcessContext();
	 /* 1513 */       context.setBillvo(billVo);
	 /* 1514 */       context.setArgsList(wfVo.getApplicationArgs());
	 /* 1515 */       context.setMakeBill(isMakeBill);
	 /* 1516 */       PFClientBizRetVO retVO = ((IPFClientBizProcessBS)obj).executeBS(context);
	 /*      */       
	 /*      */ 
	 /* 1519 */       return retVO;
	 /*      */     }
	 /*      */     
	 /* 1522 */     return null;
	 /*      */   }
	 /*      */   
	 /*      */   private PFClientBizRetVO executeClient(String billtype, AggregatedValueObject billVo, WorkflownoteVO wfVo, boolean isMakeBill)
	 /*      */     throws Exception
	 /*      */   {
	 /* 1528 */     ArrayList<Billtype2VO> bt2VOs = PfDataCache.getBillType2Info(billtype, ExtendedClassEnum.PROC_CLIENT.getIntValue());
	 /*      */     
	 /*      */ 
	 /*      */     try
	 /*      */     {
	 /* 1533 */       Iterator iterator = bt2VOs.iterator(); if (iterator.hasNext()) {
	 /* 1534 */         Billtype2VO bt2VO = (Billtype2VO)iterator.next();
	 /*      */         
	 /* 1536 */         Object obj = PfUtilTools.findBizImplOfBilltype(billtype, bt2VO.getClassname());
	 /*      */         
	 /* 1538 */         PfClientBizProcessContext context = new PfClientBizProcessContext();
	 /* 1539 */         context.setBillvo(billVo);
	 /* 1540 */         context.setArgsList(wfVo.getApplicationArgs());
	 /* 1541 */         context.setMakeBill(isMakeBill);
	 /* 1542 */         PFClientBizRetObj retObj = ((IPFClientBizProcess)obj).execute(null, context);
	 /*      */         
	 /* 1544 */         PFClientBizRetVO retVO = new PFClientBizRetVO();
	 /*      */         
	 /* 1546 */         retVO.setShowNoPass(retObj.isShowNoPass());
	 /* 1547 */         retVO.setShowPass(retObj.isShowPass());
	 /* 1548 */         retVO.setShowReject(retObj.isShowReject());
	 /* 1549 */         retVO.setStopFlow(retObj.isStopFlow());
	 /* 1550 */         retVO.setHintMessage(retObj.getHintMessage());
	 /*      */         
	 /* 1552 */         return retVO;
	 /*      */       }
	 /*      */       
	 /* 1555 */       return null;
	 /*      */     } catch (Exception e) {
	 /* 1557 */       Logger.error(e.getMessage(), e);
	 /* 1558 */       throw e;
	 /*      */     }
	 /*      */   }
	 /*      */   
	 /*      */   public WFTask getWFTask(String pk_wf_task) throws BusinessException
	 /*      */   {
	 /*      */     try {
	 /* 1565 */       return new TaskManagerDMO().getTaskByPK(pk_wf_task);
	 /*      */     }
	 /*      */     catch (DbException e) {
	 /* 1568 */       throw new BusinessException(e.getMessage(), e);
	 /*      */     }
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */   public WorkflownoteVO checkWorkflowActions(String billType, String originBillId, String pk_checkflow)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1576 */     String billid = null;
	 /*      */     try {
	 /* 1578 */       AggregatedValueObject billvo = MobileAppUtil.queryBillEntity(billType, originBillId);
	 /*      */       
	 /*      */ 
	 /* 1581 */       PfParameterVO paraVO = PfUtilBaseTools.getVariableValue(billType, "APPROVE", billvo, null, null, null, null, new HashMap(), new Hashtable());
	 /*      */       
	 /*      */ 
	 /* 1584 */       billid = paraVO.m_billVersionPK;
	 /* 1585 */       ActionEnvironment.getInstance().putParaVo(billid, paraVO);
	 /*      */       
	 /* 1587 */       return new EngineService().checkUnfinishedWorkitemByPKckeckflow(paraVO, WorkflowTypeEnum.Approveflow.getIntValue(), pk_checkflow);
	 /*      */     }
	 /*      */     catch (Exception e)
	 /*      */     {
	 /* 1591 */       if ((e instanceof BusinessException)) {
	 /* 1592 */         throw ((BusinessException)e);
	 /*      */       }
	 /* 1594 */       throw new BusinessException(e.getMessage(), e);
	 /*      */     }
	 /*      */     finally {
	 /* 1597 */       ActionEnvironment.getInstance().putParaVo(billid, null);
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
	 /*      */   private List<ActivityInstance> queryActInsByPrceInsPK(String prceIns, int[] states)
	 /*      */     throws DbException, XPDLParserException, BusinessException
	 /*      */   {
	 /* 1614 */     PersistenceManager persist = null;
	 /*      */     try {
	 /* 1616 */       persist = PersistenceManager.getInstance();
	 /* 1617 */       JdbcSession jdbc = persist.getJdbcSession();
	 /* 1618 */       String stateIn = "";
	 /*      */       
	 /* 1620 */       for (int s : states) {
	 /* 1621 */         stateIn = stateIn + "," + s;
	 /*      */       }
	 /* 1623 */       if (stateIn.length() > 0)
	 /* 1624 */         stateIn = stateIn.substring(1);
	 /* 1625 */       SQLParameter para = new SQLParameter();
	 /* 1626 */       String sqlActivityQuery = "select a.activitydefid,a.actstatus,a.createtime,a.modifytime,b.processdefid from pub_wf_actinstance a left join pub_wf_instance b on a.pk_wf_instance=b.pk_wf_instance where a.pk_wf_instance=?";
	 /*      */       
	 /* 1628 */       para.addParam(prceIns);
	 /* 1629 */       if (!StringUtil.isEmptyWithTrim(stateIn)) {
	 /* 1630 */         sqlActivityQuery = sqlActivityQuery + " and a.actstatus in (" + stateIn + ")";
	 /*      */       }
	 /* 1632 */       sqlActivityQuery = sqlActivityQuery + " order by a.ts desc";
	 /*      */       
	 /* 1634 */       List<ActivityInstance> alActInstance = (List)jdbc.executeQuery(sqlActivityQuery, para, new BaseProcessor()
	 /*      */       {
	 /* 1636 */         ArrayList<ActivityInstance> al = new ArrayList();
	 /*      */         
	 /*      */         public Object processResultSet(ResultSet rs)
	 /*      */           throws SQLException
	 /*      */         {
	 /* 1641 */           while (rs.next()) {
	 /* 1642 */             ActivityInstance ai = new ActivityInstance();
	 /* 1643 */             int index = 1;
	 /*      */             
	 /* 1645 */             String act_defid = rs.getString(index);
	 /* 1646 */             ai.setActivityID(act_defid == null ? null : act_defid.trim());
	 /*      */             
	 /*      */ 
	 /* 1649 */             index++;
	 /* 1650 */             int status = rs.getInt(index);
	 /* 1651 */             ai.setStatus(status);
	 /*      */             
	 /*      */ 
	 /* 1654 */             index++;
	 /* 1655 */             String ct = rs.getString(index);
	 /* 1656 */             UFDateTime createTime = new UFDateTime(ct);
	 /* 1657 */             ai.setCreateTime(createTime);
	 /*      */             
	 /* 1659 */             index++;
	 /* 1660 */             String mt = rs.getString(index);
	 /* 1661 */             UFDateTime modifyTime = new UFDateTime(mt);
	 /* 1662 */             ai.setModifyTime(modifyTime);
	 /*      */             
	 /*      */ 
	 /* 1665 */             index++;
	 /* 1666 */             String wf_defid = rs.getString(index);
	 /* 1667 */             ai.setWfProcessDefPK(wf_defid == null ? null : wf_defid.trim());
	 /*      */             
	 /* 1669 */             this.al.add(ai);
	 /*      */           }
	 /*      */           
	 /* 1672 */           return this.al;
	 /*      */         }
	 /*      */       });
	 /*      */       
	 /* 1676 */       for (int i = 0; i < alActInstance.size(); i++) {
	 /* 1677 */         ActivityInstance ai = (ActivityInstance)alActInstance.get(i);
	 /* 1678 */         WorkflowProcess wp = PfDataCache.getWorkflowProcess(ai.getWfProcessDefPK(), ai.getWfProcessInstancePK());
	 /*      */         
	 /* 1680 */         Activity act = wp.findActivityByID(ai.getActivityID());
	 /* 1681 */         ai.setActivity(act);
	 /*      */       }
	 /*      */       
	 /* 1684 */       return alActInstance;
	 /*      */     } finally {
	 /* 1686 */       if (persist != null) {
	 /* 1687 */         persist.release();
	 /*      */       }
	 /*      */     }
	 /*      */   }
	 /*      */   
	 /*      */   public List<WFTask> getWFTasks(List<String> wfTaskpks) throws BusinessException
	 /*      */   {
	 /* 1694 */     List<WFTask> tasks = null;
	 /*      */     try {
	 /* 1696 */       tasks = new TaskManagerDMO().getTaskByPKs(wfTaskpks);
	 /*      */     } catch (DbException e) {
	 /* 1698 */       throw new BusinessException(e.getMessage(), e);
	 /*      */     }
	 /*      */     
	 /* 1701 */     return tasks;
	 /*      */   }
		
		public int getStatus(PfParameterVO paraVo) throws BusinessException{
			int status = -99;
			try {
				WFTask currentTask = paraVo.m_workFlow.getTaskInfo().getTask();
				EngineService engine = new EngineService();
				int iCurrentWfType = currentTask.getWorkflowType();
				status = engine.queryFlowStatus(paraVo.m_billVersionPK,paraVo.m_billType, iCurrentWfType,currentTask.getApproveResult());
			} catch (DbException e) {
				Logger.error(e.getMessage(), e);
				throw new PFBusinessException(e.getMessage());
			}
			return status;
		}
		
		
		@Override
		public int queryFlowStatus(String billid, String billtype, int workflowtype) throws DbException {
			nc.bs.wfengine.engine.EngineService engine = new EngineService();
			int	status = engine.queryFlowStatus(billid,billtype, workflowtype,null);
			return status;
		}
}
