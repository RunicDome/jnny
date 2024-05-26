package nc.bs.wfengine.engine;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.ITimeService;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.ml.NCLangResOnserver;
import nc.bs.pub.pf.PfUtilTools;
import nc.bs.pub.pf.plugin.PFActivityMsgExecutor;
import nc.bs.pub.taskmanager.WfTaskManager;
import nc.bs.pub.wfengine.impl.ActionEnvironment;
import nc.bs.pub.wfengine.impl.ActivityPathInfo;
import nc.bs.pub.wfengine.impl.WfDispatchUtils;
import nc.bs.trade.business.HYPubBO;
import nc.bs.wfengine.definition.WorkflowDefinitionDAO;
import nc.bs.wfengine.engine.ext.ActivityRunExt;
import nc.bs.wfengine.engine.ext.DefaultParticipantFilter;
import nc.bs.wfengine.engine.ext.TaskTopicResolver;
import nc.bs.wfengine.engine.runtime.ApplicationTaskMaker;
import nc.bs.wfengine.engine.script.ExpressContext;
import nc.bs.wfengine.engine.script.ExpressExcutor;
import nc.impl.uap.pf.WorkflowDefineImpl;
import nc.jdbc.framework.exception.DbException;
import nc.uif.pub.exception.UifException;
import nc.vo.arap.gathering.AggGatheringBillVO;
import nc.vo.arap.gathering.GatheringBillVO;
import nc.vo.arap.pay.AggPayBillVO;
import nc.vo.arap.pay.PayBillVO;
import nc.vo.arap.payable.AggPayableBillVO;
import nc.vo.arap.payable.PayableBillVO;
import nc.vo.arap.receivable.AggReceivableBillVO;
import nc.vo.arap.receivable.ReceivableBillVO;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.org.OrgVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.uap.pf.PFBusinessException;
import nc.vo.uap.pf.PFRuntimeException;
import nc.vo.wfengine.core.activity.Activity;
import nc.vo.wfengine.core.activity.ActivityEx;
import nc.vo.wfengine.core.activity.GenericActivityEx;
import nc.vo.wfengine.core.activity.Implementation;
import nc.vo.wfengine.core.activity.SubFlow;
import nc.vo.wfengine.core.activity.Tool;
import nc.vo.wfengine.core.activity.ToolSet;
import nc.vo.wfengine.core.activity.ToolType;
import nc.vo.wfengine.core.application.IWorkflowGadget;
import nc.vo.wfengine.core.application.WfGadgetContext;
import nc.vo.wfengine.core.application.WorkflowgadgetVO;
import nc.vo.wfengine.core.condition.Condition;
import nc.vo.wfengine.core.condition.ConditionType;
import nc.vo.wfengine.core.transition.SplitJoinType;
import nc.vo.wfengine.core.transition.Transition;
import nc.vo.wfengine.core.util.CoreUtilities;
import nc.vo.wfengine.core.workflow.WorkflowProcess;
import nc.vo.wfengine.definition.ActivityTypeEnum;
import nc.vo.wfengine.definition.WorkflowTypeEnum;
import nc.vo.wfengine.engine.exception.DefinitionError;
import nc.vo.wfengine.engine.exception.EngineException;
import nc.vo.wfengine.engine.exception.ScriptExecuteException;
import nc.vo.wfengine.engine.exception.TaskInvalidateException;
import nc.vo.wfengine.pub.BillSubmitModelType;
import nc.vo.wfengine.pub.WFTask;
import nc.vo.wfengine.pub.WfTaskOrInstanceStatus;
import nc.vo.wfengine.pub.WfTaskType;
import nc.ws.intf.BillLogVO;
import nc.ws.intf.HttpClient;
import nc.ws.intf.I8LogVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

// 流程引擎，可监控流程审批状态 
/*
 * 可以在流程引擎中先判断某个单据类型下的流程环节，获取到环节名称，然后加入判断触发动作脚本进行第三方接口调用。 
 * 流程引擎判断：nc.bs.wfengine.engine.WorkflowRunner.completeActivityExecute() 获取流程当前环节名称：
 * wfActContext.getCurrentActivity().getName()
 * */
@SuppressWarnings({ "unused", "unchecked", "rawtypes", "restriction" })
public class WorkflowRunner {
	private static final int[] Special_Activity_Types = {
			ActivityTypeEnum.Makebill.getIntValue(),
			ActivityTypeEnum.Checkbill.getIntValue(),
			ActivityTypeEnum.Manual.getIntValue() };

	private WFActivityContext wfActContext;

	private Vector<WFTask> inputTaskList = new Vector();

	private Vector<WFTask> tmpOutputTaskList = new Vector();

	private Vector<WFTask> messageList = new Vector();

	private Vector<WFTask> outputTaskList = new Vector();

	private Vector inefficientActInstancePKs;

	private Vector<ActivityInstance> ignoreActInstancePKs;

	private ProcessInstance curr_procInst;

	public WorkflowRunner(WFActivityContext context) {
		this.wfActContext = context;
	}

	public void execute() throws BusinessException {
		Logger.debug(">>流程引擎：WorkflowRunner.execute() 开始");
		executeTask();

		this.wfActContext.commit();
		Logger.debug(">>流程引擎：WorkflowRunner.execute() 结束");
	}

	private boolean executeTask() throws BusinessException {
		int iTaskType = this.wfActContext.getCurrentTask().getTaskType();
		if (iTaskType == WfTaskType.Backward.getIntValue()) {
			executeBackwardTask();
		} else if (iTaskType == WfTaskType.Withdraw.getIntValue()) {
			executeWithdrawTask();
		} else {
			executeNormalTask();
		}
		return true;
	}

	private void executeNormalTask() throws BusinessException {
		Logger.error("###WorkflowRunner executeNormalTask 开始 "
				+ System.currentTimeMillis() + "ms");
		if (isStartProcessInstance(this.wfActContext.getCurrentTask())) {
			startProcessInstance();
		} else {
			completeActivityExecute();
			this.wfActContext.beforeLeaveCurrentActivity();

			Map mapTransitions = this.wfActContext.getCurrentActivity()
					.getEfferentTransitions();
			ArrayList<Transition> alTransitions = new ArrayList(
					mapTransitions.values());

			if (!this.wfActContext.getCurrentActivity().isExitActivity()) {
				if (this.wfActContext.getCurrentTask()
						.getRejectTacheActivityID() != null) {
					executeJumpTask();
				} else {
					executeTransitions(alTransitions);
				}
			}
		}
		Logger.error("###WorkflowRunner executeNormalTask 结束  "
				+ System.currentTimeMillis() + "ms");
	}

	private void executeJumpTask() throws BusinessException {
		this.wfActContext.beforeLeaveCurrentActivity();
		jumpToActivity();

		this.wfActContext.getCurrentTask().setRejectTacheActivityID(null);
	}

	private void jumpToActivity() throws BusinessException {
		String rejectTacheActId = this.wfActContext.getCurrentTask()
				.getRejectTacheActivityID();
		ProcessInstance processInstance = this.wfActContext
				.getWfProcessInstance();
		this.wfActContext.setTargetActivityID(rejectTacheActId);
		Activity jumpAct = this.wfActContext.getWfProcessDef()
				.findActivityByID(rejectTacheActId);

		ActivityInstance tacheActInst = this.wfActContext
				.getWfProcessInstance().findRejectActivityInstance(
						rejectTacheActId);

		if ((WorkflowTypeEnum.isMainFlow(processInstance.getWorkflowType()))
				&& (jumpAct != null) && (jumpAct.isInSubflow())
				&& (tacheActInst == null)) {
			Vector<ActivityInstance> instances = processInstance
					.getActInstVector();
			for (ActivityInstance instance : instances) {
				if (((instance.getActivity().getImplementation() instanceof SubFlow))
						&& (tacheActInst == null)) {
					ProcessInstance subflowinstance = WfInstancePool
							.getInstance().findSubrocessInstance(
									processInstance.getProcessInstancePK(),
									instance.getActivityInstancePK());
					tacheActInst = subflowinstance
							.findWorkActivityInstanceByID(rejectTacheActId);
				}
			}
		}

		if ((WorkflowTypeEnum.isSubFlow(this.wfActContext.getWfProcessDef()
				.getWorkflowType())) && (tacheActInst == null)) {
			String activityInstanceGUID = this.wfActContext
					.getParentActivityInstancePK();
			ProcessInstance parentProcessInstance = WfInstancePool
					.getInstance().getProcessInstance(
							this.wfActContext.getParentWfProcessInstancePK());
			while ((tacheActInst == null)
					&& (WorkflowTypeEnum.isSubFlow(parentProcessInstance
							.getWfProcessDef().getWorkflowType()))) {
				activityInstanceGUID = parentProcessInstance
						.getSrcActivityInstancePK();
				parentProcessInstance = WfInstancePool.getInstance()
						.getProcessInstance(
								parentProcessInstance
										.getParentWfProcessInstancePK());
				tacheActInst = parentProcessInstance
						.findRejectActivityInstance(rejectTacheActId);
			}
			WFTask parentBackTask = WfTaskManager.getInstance().createTask(
					this.wfActContext.getCurrentTask().getPk_org(),
					this.wfActContext.getWfProcessDef().getWorkflowType());
			parentBackTask.setWfProcessDefPK(parentProcessInstance
					.getWfProcessDefPK());
			parentBackTask.setWfProcessInstancePK(parentProcessInstance
					.getProcessInstancePK());
			parentBackTask.setActivityInstancePK(activityInstanceGUID);

			ActivityInstance activityInstance = parentProcessInstance
					.findActivityInstanceByPK(activityInstanceGUID);
			parentBackTask.setActivityID(activityInstance.getActivityID());

			parentBackTask.setModifyTime(((ITimeService) NCLocator
					.getInstance().lookup(ITimeService.class)).getUFDateTime());
			parentBackTask.setOutObject(this.wfActContext.getCurrentTask()
					.getOutObject());
			parentBackTask.setOperator(this.wfActContext.getCurrentTask()
					.getOperator());
			parentBackTask
					.setNote(this.wfActContext.getCurrentTask().getNote());

			parentBackTask.setBackToFirstActivity(this.wfActContext
					.getCurrentTask().isBackToFirstActivity());
			parentBackTask.setJumpToActivity(this.wfActContext.getCurrentTask()
					.getJumpToActivity());
			parentBackTask.setStatus(WfTaskOrInstanceStatus.Finished
					.getIntValue());
			parentBackTask.setTaskType(WfTaskType.Forward.getIntValue());

			parentBackTask.setRejectTacheActivityID(rejectTacheActId);

			copyBusiInfo(parentBackTask, this.wfActContext.getCurrentTask());
			parentBackTask.setSenderman(this.wfActContext.getCurrentTask()
					.getSenderman());

			this.inputTaskList.add(parentBackTask);

			this.wfActContext.changeOtherProcessInstanceStatus(processInstance,
					WfTaskOrInstanceStatus.Finished.getIntValue());
			return;
		}

		this.wfActContext.changeStatusOfActivityInstance(
				this.wfActContext.getCurrentActivityInstance(),
				WfTaskOrInstanceStatus.Finished);

		Vector<String> srcActInstancePks = tacheActInst
				.getSrcActivityInstancePKs();
		int index = 0;
		for (int end = srcActInstancePks == null ? 0 : srcActInstancePks.size(); index < end; index++) {
			this.wfActContext
					.addTargetActivityInstanceSrcActivityInstanceGUID((String) srcActInstancePks
							.get(index));
		}

		Vector<ActivityInstance> actInstanceVec = this.wfActContext
				.getWfProcessInstance().getActInstVector();
		ActivityInstance currentIns = this.wfActContext
				.getCurrentActivityInstance();
		int j = 0;
		for (int total = actInstanceVec.size(); j < total; j++) {
			ActivityInstance actInstance = (ActivityInstance) actInstanceVec
					.get(j);
			if (currentIns.isSource(actInstance)) {
				actInstance.addSrcActivityInstancePK(currentIns
						.getActivityInstancePK());
				this.wfActContext.changeActivityInstance(actInstance);
			}
		}

		if (!this.wfActContext.isSelfStartup())
			putInBizObject();
		startupActivityInstance(
				this.wfActContext.getTransferTargetActivityInstance(), jumpAct);
	}

	private void executeWithdrawTask() throws BusinessException {
		if ((this.wfActContext.getCurrentActivity().getImplementation() != null)
				&& ((this.wfActContext.getCurrentActivity().getImplementation() instanceof SubFlow))) {
			throw new TaskInvalidateException(NCLangResOnserver.getInstance()
					.getStrByID("pfworkflow", "UPPpfworkflow-000321"));
		}

		ProcessInstance currentProcessInstance = this.wfActContext
				.getWfProcessInstance();
		String currentActivityInstancePK = this.wfActContext.getCurrentTask()
				.getActivityInstancePK();
		Vector allNextSpecialActivityInstances = new Vector();
		checkWithdrawableInThisProcess(currentProcessInstance,
				currentActivityInstancePK, allNextSpecialActivityInstances);

		Vector vecParentProcessInstancesNeedRestart = new Vector();
		while ((currentProcessInstance.getStatus() == WfTaskOrInstanceStatus.Finished
				.getIntValue())
				&& (currentProcessInstance.getSrcActivityInstancePK() != null)) {
			String srcActInstancePK = currentProcessInstance
					.getSrcActivityInstancePK();
			String parentProcessInstancePK = currentProcessInstance
					.getParentWfProcessInstancePK();
			ProcessInstance parentProcessInstance = WfInstancePool
					.getInstance().getProcessInstance(parentProcessInstancePK);

			checkWithdrawableInThisProcess(parentProcessInstance,
					srcActInstancePK, allNextSpecialActivityInstances);

			this.wfActContext.changeStatusOfActivityInstance(
					parentProcessInstance
							.findActivityInstanceByPK(srcActInstancePK),
					WfTaskOrInstanceStatus.Started);
			if (parentProcessInstance.getStatus() == WfTaskOrInstanceStatus.Finished
					.getIntValue()) {
				vecParentProcessInstancesNeedRestart.add(parentProcessInstance);
			}
			currentProcessInstance = parentProcessInstance;
		}

		for (int i = 0; i < vecParentProcessInstancesNeedRestart.size(); i++) {
			this.wfActContext.changeOtherProcessInstanceStatus(
					(ProcessInstance) vecParentProcessInstancesNeedRestart
							.get(i), WfTaskOrInstanceStatus.Started
							.getIntValue());
		}

		withdrawNextActivityInstances(allNextSpecialActivityInstances);

		withdrawSelf();
	}

	private boolean checkWithdrawableInThisProcess(
			ProcessInstance processInstance, String activityInstancePK,
			Vector allNextSpecialActivityInstances) {
		ArrayList<ActivityInstance> tempNextActivityInstances = processInstance
				.findNextActInstancesByActInstancePK(activityInstancePK);
		Vector<ActivityInstance> activityInstVec = new Vector();
		activityInstVec.addAll(tempNextActivityInstances);
		updateToEffectiveActivityInstance(processInstance, activityInstVec);
		boolean isWithdrawable = true;
		for (ActivityInstance actInst : activityInstVec) {
			if (!isWithdrawable(processInstance, actInst,
					allNextSpecialActivityInstances))
				isWithdrawable = false;
		}
		if (!isWithdrawable) {
			throw new TaskInvalidateException(NCLangResOnserver.getInstance()
					.getStrByID("pfworkflow", "UPPpfworkflow-000322"));
		}
		return isWithdrawable;
	}

	private void updateToEffectiveActivityInstance(
			ProcessInstance processInstance,
			Vector<ActivityInstance> specialActivityInstances) {
		int total = specialActivityInstances.size();
		ActivityInstance instance = null;

		Vector<ActivityInstance> vRet = new Vector();
		for (int j = 0; j < total; j++) {
			instance = (ActivityInstance) specialActivityInstances.remove(0);
			instance = processInstance.findEffectiveActivityInstance(instance
					.getActivityID());
			if (instance != null) {
				vRet.add(instance);
			}
		}
		specialActivityInstances.clear();
		specialActivityInstances.addAll(vRet);
	}

	private boolean isWithdrawable(ProcessInstance processInstance,
			ActivityInstance actInstance, Vector allNextSpecialActivityInstances) {
		allNextSpecialActivityInstances.add(actInstance);
		boolean isWithdrawable = true;

		String strCode = CoreUtilities.findFiltercodeOfAct(actInstance
				.getActivity());
		if (DefaultParticipantFilter.CODE_SUPERIOR.equals(strCode)) {
			return true;
		}

		if (isType(actInstance.getActivity().getActivityType(),
				Special_Activity_Types)) {
			if (isAutoComplete(actInstance)) {
				isWithdrawable = checkWithdrawableInThisProcess(
						processInstance, actInstance.getActivityInstancePK(),
						allNextSpecialActivityInstances);
			} else if ((actInstance.getStatus() == WfTaskOrInstanceStatus.Finished
					.getIntValue())
					&& (allNextSpecialActivityInstances.size() == 0)) {
				isWithdrawable = false;
			} else {
				isWithdrawable = true;
			}
		} else if (ActivityTypeEnum.Subflow.getIntValue() == actInstance
				.getActivity().getActivityType()) {

			ProcessInstance subInstance = WfInstancePool.getInstance()
					.findSubrocessInstance(
							actInstance.getWfProcessInstancePK(),
							actInstance.getActivityInstancePK());
			if (subInstance != null) {
				ActivityInstance startActInstance = subInstance
						.findStartActivityInstance();

				if (isWithdrawable(subInstance, startActInstance,
						allNextSpecialActivityInstances)) {
					if (actInstance.getStatus() == WfTaskOrInstanceStatus.Finished
							.getIntValue()) {
						isWithdrawable = checkWithdrawableInThisProcess(
								processInstance,
								actInstance.getActivityInstancePK(),
								allNextSpecialActivityInstances);
					} else
						isWithdrawable = true;
				} else {
					isWithdrawable = false;
				}
			}
		} else {
			isWithdrawable = checkWithdrawableInThisProcess(processInstance,
					actInstance.getActivityInstancePK(),
					allNextSpecialActivityInstances);
		}
		return isWithdrawable;
	}

	private boolean isAutoComplete(ActivityInstance actInstance) {
		boolean isActivityAutoApprove = ((GenericActivityEx) actInstance
				.getActivity()).getAutoApprove();

		if (isActivityAutoApprove) {
			try {
				return new EngineService()
						.isActInstanceAutoCompleted2(actInstance
								.getActivityInstancePK());
			} catch (DAOException e) {
				throw new PFRuntimeException(e.getMessage(), e);
			}
		}
		return false;
	}

	private boolean isType(int type, int[] types) {
		for (int i = 0; i < types.length; i++) {
			if (type == types[i])
				return true;
		}
		return false;
	}

	private void withdrawNextActivityInstances(Vector specialActivityInstances) {
		for (int i = specialActivityInstances.size() - 1; i > -1; i--) {
			ActivityInstance actInstance = (ActivityInstance) specialActivityInstances
					.get(i);

			withdrawNextActInstance(actInstance);
			getInefficientActInstancePKs().add(
					actInstance.getActivityInstancePK());

			if (actInstance.getActivity().getActivityType() == ActivityTypeEnum.Subflow
					.getIntValue()) {
				ProcessInstance subInstance = WfInstancePool.getInstance()
						.findSubrocessInstance(
								actInstance.getWfProcessInstancePK(),
								actInstance.getActivityInstancePK());
				withdrawNextSubflow(subInstance);
			}
		}
	}

	private void withdrawNextActInstance(ActivityInstance activityInstance) {
		String billType = this.wfActContext.getCurrentTask().getBillType();
		String billID = this.wfActContext.getCurrentTask().getBillID();
		String billVersionPK = this.wfActContext.getCurrentTask()
				.getBillversionPK();
		PfParameterVO paraVO = ActionEnvironment.getInstance().getParaVo(
				billVersionPK);

		EngineService es = new EngineService();
		if (activityInstance.getStatus() == WfTaskOrInstanceStatus.Finished
				.getIntValue()) {
			es.undoGadget(billID, billType, paraVO.m_preValueVo,
					activityInstance.getActivity(), true);
		} else if (activityInstance.getStatus() == WfTaskOrInstanceStatus.Started
				.getIntValue()) {
			es.undoGadget(billID, billType, paraVO.m_preValueVo,
					activityInstance.getActivity(), false);
		}

		activityInstance.decreaseReachJoin();
		if (activityInstance.getReachJoins() <= 0) {
			this.wfActContext.changeStatusOfActivityInstance(activityInstance,
					WfTaskOrInstanceStatus.Inefficient);
		} else {
			this.wfActContext.changeStatusOfActivityInstance(activityInstance,
					WfTaskOrInstanceStatus.Started);

			activityInstance.removeSrcActivityInstancePK(activityInstance
					.getActivityInstancePK());
		}
		this.wfActContext.changeActivityInstance(activityInstance);
	}

	private void withdrawNextSubflow(ProcessInstance subInstance) {
		if (subInstance == null) {
			return;
		}
		withdrawNextActivityInstances(subInstance.getActInstVector());

		this.wfActContext.changeOtherProcessInstanceStatus(subInstance,
				WfTaskOrInstanceStatus.Inefficient.getIntValue());
	}

	private void withdrawSelf() throws BusinessException {
		ActivityInstance oldToActivityInstance = this.wfActContext
				.getCurrentActivityInstance();

		this.wfActContext.changeStatusOfActivityInstance(oldToActivityInstance,
				WfTaskOrInstanceStatus.Inefficient);

		if (this.wfActContext.getWfProcessInstance().getStatus() == WfTaskOrInstanceStatus.Finished
				.getIntValue()) {
			this.wfActContext
					.changeCurrentProcessInstanceStatus(WfTaskOrInstanceStatus.Started
							.getIntValue());
		}

		this.wfActContext.setTargetActivityID(oldToActivityInstance
				.getActivityID());

		this.wfActContext
				.addTargetActivityInstanceSrcActivityInstanceGUID(this.wfActContext
						.getCurrentActivityInstance().getActivityInstancePK());

		this.wfActContext
				.addTargetActivityInstanceInObject(oldToActivityInstance
						.getInObject());
		this.wfActContext
				.setTargetActivityInstanceOutObject(oldToActivityInstance
						.getOutObject());

		Activity toActivity = this.wfActContext.getWfProcessDef()
				.findActivityByID(oldToActivityInstance.getActivityID());
		this.wfActContext.getTransferTargetActivityInstance().setReachJoins(
				oldToActivityInstance.getReachJoins());

		startupWithdraw(this.wfActContext.getTransferTargetActivityInstance(),
				toActivity, oldToActivityInstance);
	}

	private void startupWithdraw(ActivityInstance toActivityInstance,
			Activity toActivity, ActivityInstance oldToActivityInstance)
			throws BusinessException {
		this.wfActContext.changeStatusOfActivityInstance(toActivityInstance,
				WfTaskOrInstanceStatus.Started);
		this.wfActContext.beforeEnterTargetActivity();
		getInefficientActInstancePKs().add(
				oldToActivityInstance.getActivityInstancePK());

		if (toActivity.getImplementation() != null) {
			executeWithdrawImplementation(toActivityInstance, toActivity,
					oldToActivityInstance);
		} else if (toActivity.getRoute() != null) {
			executeBackRoute(toActivityInstance, oldToActivityInstance);
		}
		if (this.tmpOutputTaskList.size() > 0) {
			TaskTopicResolver.fillNotifyInfo(this.wfActContext,
					this.tmpOutputTaskList, false);
		}
	}

	private void executeWithdrawImplementation(
			ActivityInstance toActivityInstance, Activity toActivity,
			ActivityInstance oldToActivityInstance) throws BusinessException {
		Implementation implementation = toActivity.getImplementation();
		this.tmpOutputTaskList.clear();
		if ((implementation instanceof ToolSet)) {
			executeToolSet((ToolSet) implementation, toActivityInstance,
					toActivity);

			fillBackTaskContent(toActivityInstance, oldToActivityInstance);

			String billType = this.wfActContext.getCurrentTask().getBillType();
			String billID = this.wfActContext.getCurrentTask().getBillID();
			String billVersionPK = this.wfActContext.getCurrentTask()
					.getBillversionPK();
			PfParameterVO paraVO = ActionEnvironment.getInstance().getParaVo(
					billVersionPK);

			new EngineService().undoGadgetAfter(billID, billType,
					paraVO.m_preValueVo, toActivity);
		} else if ((implementation instanceof SubFlow)) {
			throw new TaskInvalidateException(NCLangResOnserver.getInstance()
					.getStrByID("pfworkflow", "UPPpfworkflow-000321"));
		}

		this.outputTaskList.addAll(this.tmpOutputTaskList);
	}

	private void executeBackwardTask() throws BusinessException {
		executeActivityPlugin(this.wfActContext.getCurrentActivity());

		String currentActivityID = this.wfActContext.getCurrentActivityID();

		WorkflowProcess wfDef = this.wfActContext.getWfProcessDef();
		String targetActivityID = this.wfActContext.getCurrentTask()
				.getJumpToActivity();
		if ((targetActivityID != null) && (targetActivityID != "")) {
			Activity targetActivity = wfDef.findActivityByID(targetActivityID);
			if ((WorkflowTypeEnum.isMainFlow(this.wfActContext
					.getWfProcessDef().getWorkflowType()))
					&& ((targetActivity == null) || (targetActivity
							.isInSubflow()))) {
				throw new TaskInvalidateException(NCLangResOnserver
						.getInstance().getStrByID("pfworkflow",
								"UPPpfworkflow-000900"));
			}

			if ((WorkflowTypeEnum.isSubFlow(this.wfActContext.getWfProcessDef()
					.getWorkflowType())) && (targetActivity == null)) {
				String activityInstanceGUID = this.wfActContext
						.getParentActivityInstancePK();
				ProcessInstance parentProcessInstance = WfInstancePool
						.getInstance().getProcessInstance(
								this.wfActContext
										.getParentWfProcessInstancePK());
				while (WorkflowTypeEnum.isSubFlow(parentProcessInstance
						.getWfProcessDef().getWorkflowType())) {
					activityInstanceGUID = parentProcessInstance
							.getSrcActivityInstancePK();

					parentProcessInstance = WfInstancePool.getInstance()
							.getProcessInstance(
									parentProcessInstance
											.getParentWfProcessInstancePK());
				}
				WFTask parentBackTask = WfTaskManager.getInstance().createTask(
						this.wfActContext.getCurrentTask().getPk_org(),
						this.wfActContext.getWfProcessDef().getWorkflowType());
				parentBackTask.setWfProcessDefPK(parentProcessInstance
						.getWfProcessDefPK());
				parentBackTask.setWfProcessInstancePK(parentProcessInstance
						.getProcessInstancePK());
				parentBackTask.setActivityInstancePK(activityInstanceGUID);

				ActivityInstance activityInstance = parentProcessInstance
						.findActivityInstanceByPK(activityInstanceGUID);
				parentBackTask.setActivityID(activityInstance.getActivityID());

				parentBackTask.setModifyTime(((ITimeService) NCLocator
						.getInstance().lookup(ITimeService.class))
						.getUFDateTime());
				parentBackTask.setOutObject(this.wfActContext.getCurrentTask()
						.getOutObject());
				parentBackTask.setOperator(this.wfActContext.getCurrentTask()
						.getOperator());
				parentBackTask.setNote(this.wfActContext.getCurrentTask()
						.getNote());

				parentBackTask.setBackToFirstActivity(this.wfActContext
						.getCurrentTask().isBackToFirstActivity());
				parentBackTask.setJumpToActivity(this.wfActContext
						.getCurrentTask().getJumpToActivity());
				parentBackTask.setStatus(WfTaskOrInstanceStatus.Finished
						.getIntValue());
				parentBackTask.setTaskType(WfTaskType.Backward.getIntValue());
				parentBackTask.setRejectTacheActivityID(currentActivityID);
				parentBackTask.setSubmit2RjectTache(isSubmit2RejectTache());

				copyBusiInfo(parentBackTask, this.wfActContext.getCurrentTask());
				parentBackTask.setSenderman(this.wfActContext.getCurrentTask()
						.getSenderman());

				this.inputTaskList.add(parentBackTask);

				return;
			}
		}

		this.wfActContext.beforeLeaveCurrentActivity();

		if (!this.wfActContext.getCurrentTask().isBackToFirstActivity()) {
			backTask(false);
			if (isSubmit2RejectTache()) {
				for (int i = 0; i < this.tmpOutputTaskList.size(); i++) {
					((WFTask) this.tmpOutputTaskList.get(i))
							.setRejectTacheActivityID(currentActivityID);
				}
			}
		} else {
			backToFirstActivity();
		}
	}

	private boolean isSubmit2RejectTache() {
		Activity currentActivity = this.wfActContext.getCurrentActivity();

		if ((currentActivity instanceof GenericActivityEx)) {

			boolean flag = (((GenericActivityEx) currentActivity)
					.getSubmitRejectBillMode() == BillSubmitModelType.SUBMIT2REJECTTACHE
					.getValue())
					|| (this.wfActContext.getCurrentTask()
							.isSubmit2RjectTache());

			this.wfActContext.getCurrentTask().setSubmit2RjectTache(flag);
			return flag;
		}
		if ((currentActivity.getImplementation() instanceof SubFlow)) {
			return this.wfActContext.getCurrentTask().isSubmit2RjectTache();
		}
		return false;
	}

	private void backToFirstActivity() throws BusinessException {
		Activity toActivity = this.wfActContext.getWfProcessDef()
				.findStartActivity();
		this.wfActContext.setTargetActivityID(toActivity.getId());

		if (isSubmit2RejectTache()) {
			ActivityInstance toActInstance = this.wfActContext
					.getWfProcessInstance().findEffectiveActivityInstance(
							toActivity.getId());
			invalidateActivityInstanceWhenSubmit2RejectTache(this.wfActContext
					.getWfProcessInstance());
			Vector<String> srcActInstancePks = toActInstance
					.getSrcActivityInstancePKs();
			if ((srcActInstancePks != null) && (srcActInstancePks.size() != 0)) {
				for (String srcActInstancePk : srcActInstancePks) {
					this.wfActContext
							.addTargetActivityInstanceSrcActivityInstanceGUID(srcActInstancePk);
				}
			}
		} else {
			invalidateAllActivityInstanceWhenBack(this.wfActContext
					.getWfProcessInstance());
			this.wfActContext
					.addTargetActivityInstanceSrcActivityInstanceGUID(this.wfActContext
							.getCurrentActivityInstance()
							.getActivityInstancePK());
		}
		startupActivityInstance(
				this.wfActContext.getTransferTargetActivityInstance(),
				toActivity);

		if (isSubmit2RejectTache()) {
			for (int i = 0; i < this.tmpOutputTaskList.size(); i++) {
				((WFTask) this.tmpOutputTaskList.get(i))
						.setRejectTacheActivityID(this.wfActContext
								.getCurrentActivityID());
			}
		}
	}

	private void invalidateActivityInstanceWhenSubmit2RejectTache(
			ProcessInstance processinstance) {
		String activityId = this.wfActContext.getWfProcessDef()
				.findStartActivity().getId();
		ActivityInstance toActInstance = this.wfActContext
				.getWfProcessInstance().findEffectiveActivityInstance(
						activityId);
		inefficientTargetActinst(this.wfActContext.getWfProcessInstance(),
				toActInstance, processinstance.getActInstVector());

		Vector vec = new Vector();
		vec.add(toActInstance);
		vec.add(this.wfActContext.getCurrentActivityInstance());
		invalidateActivityInstanceWhenBack(vec);
	}

	private void invalidateAllActivityInstanceWhenBack(
			ProcessInstance processInstance) {
		invalidateActivityInstanceWhenBack(processInstance.getActInstVector());
	}

	private void invalidateAllActivityInstanceWhenComplete(
			ProcessInstance processInstance) {
		invalidateActivityInstanceWhenComplete(processInstance
				.getActInstVector());
	}

	private void invalidateActivityInstanceWhenBack(Vector activityInstances) {
		ActivityInstance instance = null;
		for (int i = 0; i < activityInstances.size(); i++) {
			instance = (ActivityInstance) activityInstances.get(i);
			if ((instance.getActivity().getPrecondition() != null)
					&& (instance.getActivity().getPrecondition().getValue() == 0)) {
				this.wfActContext.changeStatusOfActivityInstance(instance,
						WfTaskOrInstanceStatus.Inefficient);
				instance.decreaseReachJoin();
				instance.setResult(null);
			} else {
				WfInstancePool.getInstance()
						.getProcessInstance(instance.getWfProcessInstancePK())
						.findStartActivityInstance().equals(instance);
				if ((isSubmit2RejectTache())
						&& (instance.getActivity().getActivityType() == ActivityTypeEnum.Subflow
								.getIntValue())
						&& (!(instance.getActivity().getImplementation() instanceof SubFlow))) {

					this.wfActContext.changeStatusOfActivityInstance(instance,
							WfTaskOrInstanceStatus.Started);
					ProcessInstance subInstance = WfInstancePool.getInstance()
							.findSubrocessInstance(
									instance.getWfProcessInstancePK(),
									instance.getActivityInstancePK());
					if (subInstance != null)
						this.wfActContext.changeOtherProcessInstanceStatus(
								subInstance,
								WfTaskOrInstanceStatus.Started.getIntValue());
				} else {
					this.wfActContext.changeStatusOfActivityInstance(instance,
							WfTaskOrInstanceStatus.Inefficient);
					getInefficientActInstancePKs().add(
							instance.getActivityInstancePK());
				}
			}
			this.wfActContext.beforeLeaveActivity(instance.getActivity());

			if (instance.getActivity().getActivityType() == ActivityTypeEnum.Subflow
					.getIntValue()) {
				ProcessInstance subInstance = WfInstancePool.getInstance()
						.findSubrocessInstance(
								instance.getWfProcessInstancePK(),
								instance.getActivityInstancePK());
				if (isSubmit2RejectTache()) {
					ActivityInstance substartInst = subInstance
							.findStartActivityInstance();
					this.wfActContext.changeStatusOfActivityInstance(
							substartInst, WfTaskOrInstanceStatus.Inefficient);
					getInefficientActInstancePKs().add(
							substartInst.getActivityInstancePK());
				} else {
					invalidateSubProcessWhenBack(subInstance);
				}
			}
		}
	}

	private void invalidateActivityInstanceWhenForward(Vector activityInstances) {
		ActivityInstance instance = null;
		for (int i = 0; i < activityInstances.size(); i++) {
			instance = (ActivityInstance) activityInstances.get(i);
			this.wfActContext.changeStatusOfActivityInstance(instance,
					WfTaskOrInstanceStatus.Inefficient);
			this.wfActContext.beforeLeaveActivity(instance.getActivity());
			getInefficientActInstancePKs()
					.add(instance.getActivityInstancePK());
			if (instance.getActivity().getActivityType() == ActivityTypeEnum.Subflow
					.getIntValue()) {
				ProcessInstance subInstance = WfInstancePool.getInstance()
						.findSubrocessInstance(
								instance.getWfProcessInstancePK(),
								instance.getActivityInstancePK());
				invalidateSubProcessWhenForward(subInstance);
			}
		}
	}

	private void invalidateActivityInstanceWhenComplete(Vector activityInstances) {
		ActivityInstance instance = null;
		for (int i = 0; i < activityInstances.size(); i++) {
			instance = (ActivityInstance) activityInstances.get(i);
			this.wfActContext.changeStatusOfActivityInstance(instance,
					WfTaskOrInstanceStatus.Inefficient);
			this.wfActContext.beforeLeaveActivity(instance.getActivity());
			getInefficientActInstancePKs()
					.add(instance.getActivityInstancePK());
			if (instance.getActivity().getActivityType() == ActivityTypeEnum.Subflow
					.getIntValue()) {
				ProcessInstance subInstance = WfInstancePool.getInstance()
						.findSubrocessInstance(
								instance.getWfProcessInstancePK(),
								instance.getActivityInstancePK());
				invalidateSubProcessWhenComplete(subInstance);
			}
		}
	}

	private void invalidateSubProcessWhenBack(ProcessInstance subInstance) {
		if (subInstance == null)
			return;
		invalidateAllActivityInstanceWhenBack(subInstance);
		this.wfActContext.changeOtherProcessInstanceStatus(subInstance,
				WfTaskOrInstanceStatus.Inefficient.getIntValue());
	}

	private void invalidateSubProcessWhenComplete(ProcessInstance subInstance) {
		if (subInstance == null)
			return;
		invalidateAllActivityInstanceWhenComplete(subInstance);
		this.wfActContext.changeOtherProcessInstanceStatus(subInstance,
				WfTaskOrInstanceStatus.Inefficient.getIntValue());
	}

	private void invalidateSubProcessWhenForward(ProcessInstance subInstance) {
		if (subInstance == null)
			return;
		invalidateActivityInstanceWhenForward(subInstance
				.findAllUnfinishedActivityInstances());
		this.wfActContext.changeOtherProcessInstanceStatus(subInstance,
				WfTaskOrInstanceStatus.Inefficient.getIntValue());
	}

	private void backTask(boolean is2firstAct) throws BusinessException {
		String activityId = null;
		if (is2firstAct) {
			activityId = this.wfActContext.getWfProcessDef()
					.findStartActivity().getId();
		} else {
			activityId = this.wfActContext.getCurrentTask().getJumpToActivity();
		}
		boolean flag = canBackActivityInstance2(
				this.wfActContext.getCurrentActivityInstance(), activityId,
				this.wfActContext.getWfProcessInstance());
		if (!flag) {
			throw new TaskInvalidateException(NCLangResOnserver.getInstance()
					.getStrByID("pfworkflow", "UPPpfworkflow-000901"));
		}
		ProcessInstance processInstance = this.wfActContext
				.getWfProcessInstance();
		Vector activityInstances = processInstance.getActInstVector();
		ActivityInstance toActInstance = this.curr_procInst
				.findEffectiveActivityInstance(activityId);

		if (toActInstance.getActivity().isInSubflow()) {
			ActivityInstance subFlowActInstance = null;
			for (int i = 0; i < activityInstances.size(); i++) {
				ActivityInstance activityInstance = (ActivityInstance) activityInstances
						.get(i);
				if (activityInstance.getActivity().getActivityType() == ActivityTypeEnum.Subflow
						.getIntValue()) {
					subFlowActInstance = processInstance
							.findEffectiveActivityInstance(activityInstance
									.getActivityID());
					break;
				}
			}

			if (subFlowActInstance == null) {
				subFlowActInstance = toActInstance;
			}

			inefficientTargetActinst(this.wfActContext.getWfProcessInstance(),
					subFlowActInstance, activityInstances);

			backToActivityInstance(subFlowActInstance);
		} else {
			inefficientTargetActinst(this.wfActContext.getWfProcessInstance(),
					toActInstance, activityInstances);
			backToActivityInstance(toActInstance);
		}
	}

	private void backToSubflowActivityInstance(ActivityInstance toActInstance,
			ActivityInstance subFlowActInstance, ProcessInstance subProcInst)
			throws BusinessException {
		if (toActInstance.getStatus() == WfTaskOrInstanceStatus.Inefficient
				.getIntValue()) {
			throw new TaskInvalidateException(NCLangResOnserver.getInstance()
					.getStrByID("pfworkflow", "UPPpfworkflow-000902"));
		}

		this.wfActContext.changeStatusOfActivityInstance(subFlowActInstance,
				WfTaskOrInstanceStatus.Inefficient);

		this.wfActContext.setTargetActivityID(subFlowActInstance
				.getActivityID());

		this.wfActContext
				.addTargetActivityInstanceSrcActivityInstanceGUID(this.wfActContext
						.getCurrentActivityInstance().getActivityInstancePK());

		this.wfActContext.addTargetActivityInstanceInObject(subFlowActInstance
				.getInObject());
		this.wfActContext.setTargetActivityInstanceOutObject(subFlowActInstance
				.getOutObject());

		Activity toActivity = subFlowActInstance.getActivity();
		startupBack(toActInstance, toActivity, subFlowActInstance);
	}

	private void inefficientTargetActinst(ProcessInstance procInst,
			ActivityInstance toActInstance, Vector activityInstances) {
		ActivityInstance activityInstance = null;
		ActivityInstance effectiveActivityInstance = null;

		boolean isSubmit2RejctTache = isSubmit2RejectTache();
		for (int i = 0; i < activityInstances.size(); i++) {
			activityInstance = (ActivityInstance) activityInstances.get(i);

			if (toActInstance.isSource(activityInstance)) {
				effectiveActivityInstance = procInst
						.findEffectiveActivityInstance(activityInstance
								.getActivityID());
				if (effectiveActivityInstance != null) {

					if (isSubmit2RejctTache) {
						if (this.wfActContext.getCurrentActivityID().equals(
								effectiveActivityInstance.getActivityID())) {
							Vector vec = new Vector();
							vec.add(effectiveActivityInstance);

							invalidateActivityInstanceWhenBack(vec);
						} else {
							getIgnoreActInstancePKs().add(
									effectiveActivityInstance);
						}
						inefficientTargetActinst(procInst,
								effectiveActivityInstance, activityInstances);
					} else if (effectiveActivityInstance.getReachJoins() > 1) {
						effectiveActivityInstance.decreaseReachJoin();
						Vector vec = new Vector();
						vec.add(effectiveActivityInstance);
						int status = WfTaskOrInstanceStatus.Started
								.getIntValue();
						this.wfActContext.changeStatusOfActivityInstance(
								effectiveActivityInstance,
								WfTaskOrInstanceStatus.Started);
					} else {
						Vector vec = new Vector();
						vec.add(effectiveActivityInstance);

						invalidateActivityInstanceWhenBack(vec);
						inefficientTargetActinst(procInst,
								effectiveActivityInstance, activityInstances);
					}
				}
			}
		}
	}

	private ActivityInstance canBackActivityInstance(
			ActivityInstance activityInstance, ProcessInstance procInst)
			throws BusinessException {
		Vector dataSrcActivityInstanceGUIDs = activityInstance
				.getSrcActivityInstancePKs();

		if ((dataSrcActivityInstanceGUIDs == null)
				|| (dataSrcActivityInstanceGUIDs.size() == 0)) {
			throw new TaskInvalidateException(NCLangResOnserver.getInstance()
					.getStrByID("pfworkflow", "UPPpfworkflow-000323"));
		}

		ActivityInstance toActInstance = null;
		String activityId = this.wfActContext.getCurrentTask()
				.getJumpToActivity();
		if ((this.wfActContext.getCurrentActivity().isStartActivity())
				&& (WorkflowTypeEnum.isSubFlow(this.wfActContext
						.getWfProcessDef().getWorkflowType()))) {
			ProcessInstance parentProcessInstance = WfInstancePool
					.getInstance().getProcessInstance(
							this.wfActContext.getParentWfProcessInstancePK());

			String srcActInstPK = this.wfActContext
					.getParentActivityInstancePK();
			ActivityInstance oldToActivityInstance = parentProcessInstance
					.findActivityInstanceByPK(srcActInstPK);
			toActInstance = canBackActivityInstance(oldToActivityInstance,
					this.wfActContext.getWfProcessInstance());
		} else {
			for (int i = 0; i < dataSrcActivityInstanceGUIDs.size(); i++) {
				String srcActivityInstanceGUID = (String) dataSrcActivityInstanceGUIDs
						.get(i);
				ActivityInstance actInst = procInst
						.findActivityInstanceByPK(srcActivityInstanceGUID);

				if (actInst.getActivity().getActivityType() == ActivityTypeEnum.Subflow
						.getIntValue()) {
					ProcessInstance subProcInst = WfInstancePool.getInstance()
							.findSubrocessInstance(
									actInst.getWfProcessInstancePK(),
									actInst.getActivityInstancePK());

					toActInstance = canBackActivityInstanceInSubProcess(
							subProcInst.findEndActivityInstance(), subProcInst);
					if (toActInstance == null) {
						toActInstance = canBackActivityInstance(actInst,
								procInst);
					}
				} else if (activityId.equals(actInst.getActivity().getId())) {
					toActInstance = procInst
							.findEffectiveActivityInstance(activityId);
				} else {
					toActInstance = canBackActivityInstance(actInst, procInst);
				}

				if (toActInstance != null) {
					break;
				}
			}
		}

		return toActInstance;
	}

	private boolean canBackActivityInstance2(ActivityInstance activityInstance,
			String jmpActivityId, ProcessInstance procInst)
			throws BusinessException {
		if (jmpActivityId.equals(activityInstance.getActivity().getId())) {
			this.curr_procInst = procInst;
			return true;
		}
		Vector dataSrcActivityInstanceGUIDs = activityInstance
				.getSrcActivityInstancePKs();
		if ((dataSrcActivityInstanceGUIDs == null)
				|| (dataSrcActivityInstanceGUIDs.size() == 0)) {
			throw new TaskInvalidateException(NCLangResOnserver.getInstance()
					.getStrByID("pfworkflow", "UPPpfworkflow-000323"));
		}

		if ((this.wfActContext.getCurrentActivity().isStartActivity())
				&& (WorkflowTypeEnum.isSubFlow(this.wfActContext
						.getWfProcessDef().getWorkflowType()))) {
			ProcessInstance parentProcessInstance = WfInstancePool
					.getInstance().getProcessInstance(
							this.wfActContext.getParentWfProcessInstancePK());

			String srcActInstPK = this.wfActContext
					.getParentActivityInstancePK();
			ActivityInstance oldToActivityInstance = parentProcessInstance
					.findActivityInstanceByPK(srcActInstPK);
			return canBackActivityInstance2(oldToActivityInstance,
					jmpActivityId, this.wfActContext.getWfProcessInstance());
		}
		for (int i = 0; i < dataSrcActivityInstanceGUIDs.size(); i++) {
			String srcActivityInstanceGUID = (String) dataSrcActivityInstanceGUIDs
					.get(i);
			ActivityInstance actInst = procInst
					.findActivityInstanceByPK(srcActivityInstanceGUID);

			if (jmpActivityId.equals(actInst.getActivity().getId())) {
				this.curr_procInst = procInst;
				return true;
			}
			if (actInst.getActivity().getActivityType() == ActivityTypeEnum.Subflow
					.getIntValue()) {
				ProcessInstance subProcInst = WfInstancePool.getInstance()
						.findSubrocessInstance(
								actInst.getWfProcessInstancePK(),
								actInst.getActivityInstancePK());

				if ((subProcInst != null)
						&& (subProcInst.getProcessInstancePK().equals(procInst
								.getProcessInstancePK()))) {
					ProcessInstance parentProcInst = WfInstancePool
							.getInstance().getProcessInstance(
									procInst.getParentWfProcessInstancePK());
					if (canBackActivityInstance2(actInst, jmpActivityId,
							parentProcInst)) {
						return true;
					}
				}

				if ((subProcInst != null)
						&& (canBackActivityInstance2(
								subProcInst.findEndActivityInstance(),
								jmpActivityId, subProcInst))) {
					return true;
				}
				if (canBackActivityInstance2(actInst, jmpActivityId, procInst)) {
					return true;
				}
			}
		}

		for (int i = 0; i < dataSrcActivityInstanceGUIDs.size(); i++) {
			String srcActivityInstanceGUID = (String) dataSrcActivityInstanceGUIDs
					.get(i);
			ActivityInstance actInst = procInst
					.findActivityInstanceByPK(srcActivityInstanceGUID);
			if (canBackActivityInstance2(actInst, jmpActivityId, procInst)) {
				return true;
			}
		}

		return false;
	}

	private ActivityInstance canBackActivityInstanceInSubProcess(
			ActivityInstance activityInstance, ProcessInstance procInst)
			throws BusinessException {
		return canBackActivityInstance(activityInstance, procInst);
	}

	private void backToParentProcessInstance() {
		this.wfActContext
				.changeCurrentProcessInstanceStatus(WfTaskOrInstanceStatus.Inefficient
						.getIntValue());
		WFTask subflowBacktask = WfTaskManager.getInstance().createTask(
				this.wfActContext.getCurrentTask().getPk_org(),
				this.wfActContext.getWfProcessDef().getWorkflowType());
		ProcessInstance parentProcessInstance = WfInstancePool.getInstance()
				.getProcessInstance(
						this.wfActContext.getParentWfProcessInstancePK());
		subflowBacktask.setWfProcessDefPK(parentProcessInstance
				.getWfProcessDefPK());
		subflowBacktask.setWfProcessInstancePK(parentProcessInstance
				.getProcessInstancePK());
		subflowBacktask.setActivityID(parentProcessInstance
				.findActivityInstanceByPK(
						this.wfActContext.getParentActivityInstancePK())
				.getActivityID());
		subflowBacktask.setActivityInstancePK(this.wfActContext
				.getParentActivityInstancePK());
		subflowBacktask
				.setStatus(WfTaskOrInstanceStatus.Finished.getIntValue());
		subflowBacktask.setTaskType(WfTaskType.Backward.getIntValue());
		subflowBacktask.setSenderman(this.wfActContext.getCurrentTask()
				.getSenderman());
		this.inputTaskList.add(subflowBacktask);
	}

	private void backToActivityInstance(ActivityInstance oldToActivityInstance)
			throws BusinessException {
		if (oldToActivityInstance.getStatus() == WfTaskOrInstanceStatus.Inefficient
				.getIntValue()) {
			throw new TaskInvalidateException(NCLangResOnserver.getInstance()
					.getStrByID("pfworkflow", "UPPpfworkflow-000902"));
		}

		Vector vec = new Vector();
		vec.add(oldToActivityInstance);
		invalidateActivityInstanceWhenBack(vec);

		this.wfActContext.setTargetActivityID(oldToActivityInstance
				.getActivityID());

		if (!isSubmit2RejectTache()) {
			this.wfActContext
					.addTargetActivityInstanceSrcActivityInstanceGUID(this.wfActContext
							.getCurrentActivityInstance()
							.getActivityInstancePK());
		} else {
			if ((oldToActivityInstance.getActivity().getImplementation() instanceof SubFlow)) {
				ProcessInstance subflowInst = WfInstancePool.getInstance()
						.findSubrocessInstance(
								oldToActivityInstance.getWfProcessInstancePK(),
								oldToActivityInstance.getActivityInstancePK());
				this.wfActContext.setWfProcessDefPK(subflowInst
						.getWfProcessDefPK());
				this.wfActContext.setWfProcessInstance(subflowInst);
				this.wfActContext.setWfProcessInstancePK(subflowInst
						.getProcessInstancePK());
				this.wfActContext
						.setParentWfProcessInstancePK(oldToActivityInstance
								.getWfProcessInstancePK());
				this.wfActContext
						.setParentActivityInstanceGUID(oldToActivityInstance
								.getActivityInstancePK());
				this.wfActContext.setParentSrcActivity(oldToActivityInstance
						.getActivity());

				this.wfActContext.changeStatusOfActivityInstance(
						oldToActivityInstance, WfTaskOrInstanceStatus.Started);
				oldToActivityInstance = subflowInst.findStartActivityInstance();
				this.wfActContext.changeStatusOfActivityInstance(
						oldToActivityInstance,
						WfTaskOrInstanceStatus.Inefficient);
				this.wfActContext.changeOtherProcessInstanceStatus(subflowInst,
						WfTaskOrInstanceStatus.Started.getIntValue());

				this.wfActContext.setTargetActivityID(oldToActivityInstance
						.getActivityID());
				Vector<ActivityInstance> actInsts = subflowInst
						.getActInstVector();
				int j = 0;
				for (int total = actInsts == null ? 0 : actInsts.size(); j < total; j++) {
					if (!((ActivityInstance) actInsts.get(j))
							.getActivityInstancePK().equals(
									oldToActivityInstance
											.getActivityInstancePK())) {

						getIgnoreActInstancePKs().add(actInsts.get(j));
					}
				}
			}

			Vector<String> srcActInstancePks = oldToActivityInstance
					.getSrcActivityInstancePKs();

			int j = 0;
			for (int total = srcActInstancePks == null ? 0 : srcActInstancePks
					.size(); j < total; j++) {
				this.wfActContext
						.addTargetActivityInstanceSrcActivityInstanceGUID((String) srcActInstancePks
								.get(j));
			}

			Vector<ActivityInstance> actInstanceVec = this.wfActContext
					.getWfProcessInstance().getActInstVector();
			// int j = 0;
			for (int total = actInstanceVec.size(); j < total; j++) {
				ActivityInstance actInstance = (ActivityInstance) actInstanceVec
						.get(j);
				if (oldToActivityInstance.isSource(actInstance)) {
					actInstance.addSrcActivityInstancePK(this.wfActContext
							.getTransferTargetActivityInstance()
							.getActivityInstancePK());
					this.wfActContext.changeActivityInstance(actInstance);
				}
			}
		}

		boolean canBack = canBackToActivity(
				this.wfActContext.getTransferTargetActivityInstance(),
				oldToActivityInstance);
		if (canBack) {
			this.wfActContext
					.addTargetActivityInstanceInObject(oldToActivityInstance
							.getInObject());
			this.wfActContext
					.setTargetActivityInstanceOutObject(oldToActivityInstance
							.getOutObject());

			Activity toActivity = this.wfActContext.getWfProcessDef()
					.findActivityByID(oldToActivityInstance.getActivityID());
			startupBack(this.wfActContext.getTransferTargetActivityInstance(),
					toActivity, oldToActivityInstance);
		}
	}

	private void startupBack(ActivityInstance toActivityInstance,
			Activity toActivity, ActivityInstance oldToActivityInstance)
			throws BusinessException {
		this.wfActContext.changeStatusOfActivityInstance(toActivityInstance,
				WfTaskOrInstanceStatus.Started);
		this.wfActContext.beforeEnterTargetActivity();
		getInefficientActInstancePKs().add(
				oldToActivityInstance.getActivityInstancePK());
		if (toActivity.getImplementation() != null) {
			executeBackImplementation(toActivityInstance, toActivity,
					oldToActivityInstance);
		} else if (toActivity.getRoute() != null) {
			executeBackRoute(toActivityInstance, oldToActivityInstance);
		}
		if (this.tmpOutputTaskList.size() > 0) {
			TaskTopicResolver.fillNotifyInfo(this.wfActContext,
					this.tmpOutputTaskList, false);
		}
	}

	private void executeBackRoute(ActivityInstance activityInstance,
			ActivityInstance oldToActivityInstance) {
		oldToActivityInstance.setStatus(WfTaskOrInstanceStatus.Started
				.getIntValue());

		WFTask routeBacktask = WfTaskManager.getInstance().createTask(
				this.wfActContext.getCurrentTask().getPk_org(),
				this.wfActContext.getWfProcessDef().getWorkflowType());
		routeBacktask.setWfProcessDefPK(activityInstance.getWfProcessDefPK());
		routeBacktask.setWfProcessInstancePK(activityInstance
				.getWfProcessInstancePK());
		routeBacktask.setActivityID(activityInstance.getActivityID());

		routeBacktask.setActivityInstancePK(activityInstance
				.getActivityInstancePK());
		copyBusiInfo(routeBacktask, this.wfActContext.getCurrentTask());
		routeBacktask.setStatus(WfTaskOrInstanceStatus.Finished.getIntValue());
		routeBacktask.setTaskType(WfTaskType.Backward.getIntValue());
		this.inputTaskList.add(routeBacktask);
	}

	private void executeBackSubFlow(ActivityInstance toActivityInstance,
			SubFlow subflow, ActivityInstance oldToActivityInstance)
			throws BusinessException {
		WFTask subflowTask = WfTaskManager.getInstance().createTask(
				this.wfActContext.getCurrentTask().getPk_org(),
				this.wfActContext.getWfProcessDef().getWorkflowType());
		String subflowPK = new WorkflowDefineImpl()
				.findSubProcDefVIDBySourceActInsID(oldToActivityInstance
						.getActivityInstancePK());

		subflowTask.setWfProcessDefPK(subflowPK);

		if (isSubmit2RejectTache()) {
			String subProcessInstpk = new WorkflowDefinitionDAO()
					.findsubProcessInstancePKByDefPK(subflowPK);
			subflowTask.setWfProcessInstancePK(subProcessInstpk);
		}

		subflowTask.setInObject(toActivityInstance.getInObject());
		copyBusiInfo(subflowTask, this.wfActContext.getCurrentTask());

		HashMap transferInfo = new HashMap();
		transferInfo.put("ACT_Check_Result", this.wfActContext
				.getWfProcessInstance().getRelevantData("ACT_Check_Result"));
		HashMap relevantDatas = this.wfActContext.getDatas();
		exchangeAssignInfo(this.wfActContext.getWfProcessDefPK(), transferInfo,
				relevantDatas);

		subflowTask.setTranferInfo(transferInfo);
		subflowTask.setSenderman(this.wfActContext.getCurrentTask()
				.getSenderman());

		WFActivityContext subContext = new WFActivityContext(subflowTask);
		subContext.setParentWfProcessInstancePK(toActivityInstance
				.getWfProcessInstancePK());
		subContext.setParentActivityInstanceGUID(toActivityInstance
				.getActivityInstancePK());
		subContext.setParentSrcActivity(toActivityInstance.getActivity());
		WorkflowRunner runner = new WorkflowRunner(subContext);
		this.wfActContext.addSubflowContext(subContext);

		ActivityRunExt.tranferRevelantDataBetweenProcess(subContext);

		runner.execute();

		this.wfActContext.addExecuteResult(subContext.getExecuteResult());
		this.tmpOutputTaskList.addAll(runner.getOutputTaskList());
		this.inputTaskList.addAll(runner.getInputTaskList());
	}

	private boolean canBackToActivity(ActivityInstance newToActivityInstance,
			ActivityInstance oldActivityInstance) {
		boolean result = true;
		ProcessInstance processInstance = this.wfActContext
				.getWfProcessInstance();
		Vector activityInstances = processInstance.getActInstVector();
		ActivityInstance activityInstance = null;
		ActivityInstance effectiveActivityInstance = null;

		activityInstances.removeAll(getIgnoreActInstancePKs());
		for (int i = 0; i < activityInstances.size(); i++) {
			activityInstance = (ActivityInstance) activityInstances.get(i);

			if (oldActivityInstance.isSource(activityInstance)) {
				effectiveActivityInstance = this.wfActContext
						.getWfProcessInstance().findEffectiveActivityInstance(
								activityInstance.getActivityID());
				if ((effectiveActivityInstance != null)
						&& (effectiveActivityInstance.getStatus() == WfTaskOrInstanceStatus.Finished
								.getIntValue()))
					return false;
			}
		}
		return result;
	}

	private boolean isStartProcessInstance(WFTask task) {
		if (task == null) {
			return true;
		}
		return (task.getActivityID() == null)
				&& (task.getActivityInstancePK() == null);
	}

	private void startProcessInstance() throws BusinessException {
		Logger.debug("开始启动流程实例");
		Logger.debug("*** 流程定义PK="
				+ this.wfActContext.getWfProcessDef().getProcessDefinitionId());
		Logger.debug("*** 流程定义Name="
				+ this.wfActContext.getWfProcessDef().getName());

		Activity startActivity = this.wfActContext.getWfProcessDef()
				.findStartActivity();
		if (startActivity == null) {
			throw new DefinitionError(NCLangResOnserver.getInstance()
					.getStrByID("pfworkflow", "UPPpfworkflow-000325")
					+ this.wfActContext.getWfProcessDef().getName()
					+ NCLangResOnserver.getInstance().getStrByID("pfworkflow",
							"UPPpfworkflow-000326"));
		}
		transferToActive(startActivity, null);
	}

	private WFTask createNotifyMainFlowTask() {
		WFTask notityMainProcesstask = WfTaskManager.getInstance().createTask(
				this.wfActContext.getCurrentTask().getPk_org(),
				this.wfActContext.getWfProcessDef().getWorkflowType());

		String parentProcInstPK = this.wfActContext
				.getParentWfProcessInstancePK();

		String parentActInstPK = this.wfActContext
				.getParentActivityInstancePK();
		String processDefID = null;
		String activityID = null;

		if (parentProcInstPK != null) {
			ProcessInstance mainProcessInstance = WfInstancePool.getInstance()
					.getProcessInstance(parentProcInstPK);
			processDefID = mainProcessInstance.getWfProcessDefPK();
			activityID = mainProcessInstance.findActivityInstanceByPK(
					parentActInstPK).getActivityID();
		} else {
			ActivityInstance activityInstance = WfInstancePool.getInstance()
					.getERPActivityInstance(parentActInstPK);
			processDefID = activityInstance.getWfProcessDefPK();
			activityID = activityInstance.getActivityID();
		}

		notityMainProcesstask.setWfProcessDefPK(processDefID);
		notityMainProcesstask.setWfProcessInstancePK(parentProcInstPK);
		notityMainProcesstask.setActivityID(activityID);
		notityMainProcesstask.setActivityInstancePK(parentActInstPK);
		notityMainProcesstask.setOutObject(this.wfActContext
				.getCurrentActivityInstance().getOutObject());
		notityMainProcesstask.setSenderman(getInformerId(this.wfActContext));
		if (notityMainProcesstask.getOperator() == null) {
			notityMainProcesstask.setOperator(this.wfActContext
					.getCurrentTask().getOperator());
		}
		copyBusiInfo(notityMainProcesstask, this.wfActContext.getCurrentTask());

		HashMap transferInfo = new HashMap();
		transferInfo.put("ACT_Check_Result", this.wfActContext
				.getWfProcessInstance().getRelevantData("ACT_Check_Result"));
		notityMainProcesstask.setTranferInfo(transferInfo);
		HashMap relevantDatas = this.wfActContext.getDatas();
		exchangeAssignInfo(this.wfActContext.getWfProcessDefPK(), transferInfo,
				relevantDatas);

		notityMainProcesstask.setStatus(WfTaskOrInstanceStatus.Finished
				.getIntValue());
		return notityMainProcesstask;
	}

	private String getInformerId(WFActivityContext context) {
		String userId = context.getCurrentTask().getOperator();
		if (userId == null) {
			userId = context.getCurrentTask().getSenderman();
		}
		return userId;
	}

	private void completeActivityExecute() throws BusinessException {
		Logger.error("###WorkflowRunner completeActivityExecute 开始 "
				+ System.currentTimeMillis() + "ms");
		Logger.debug("开始结束活动="
				+ this.wfActContext.getCurrentActivity().getName());

		Iterator<String> anokeys = this.wfActContext.getCurrentTask()
				.getAssignNextOperatorsKeys();
		if (anokeys != null) {
			while (anokeys.hasNext()) {
				String strActAssignKey = (String) anokeys.next();
				this.wfActContext.getDatas().put(
						strActAssignKey,
						this.wfActContext.getCurrentTask()
								.getAssignNextOperators(strActAssignKey));
			}
		}

		Iterator<String> antkeys = this.wfActContext.getCurrentTask()
				.getAssignNextTransitionKeys();
		if (antkeys != null) {
			while (antkeys.hasNext()) {
				String strTransAssignKey = (String) antkeys.next();
				this.wfActContext.getDatas().put(
						strTransAssignKey,
						this.wfActContext.getCurrentTask()
								.getAssignNextTransition(strTransAssignKey));
			}
		}

		this.wfActContext.setCurrentActivityInstanceOutObject(this.wfActContext
				.getCurrentTask().getOutObject());

		if ((this.wfActContext.getCurrentActivity().getImplementation() != null)
				&& ((this.wfActContext.getCurrentActivity().getImplementation() instanceof SubFlow))) {
			ActivityRunExt.tranferRevelantDataBetweenProcess(this.wfActContext);
		}

		afterActivityRunned(this.wfActContext.getCurrentActivity(),
				this.wfActContext.getCurrentActivityInstance());

		this.wfActContext
				.changeCurrentActivityInstanceStatus(WfTaskOrInstanceStatus.Finished);

		if (this.wfActContext.getCurrentActivity().isExitActivity()) {
			completeWFProcessInstance();
		}
		Logger.debug("结束结束活动="
				+ this.wfActContext.getCurrentActivity().getName());
		Logger.error("###WorkflowRunner completeActivityExecute 结束 "
				+ System.currentTimeMillis() + "ms");
		// XBX 监控 工程集团共享调用I8系统接口
		String ss = this.wfActContext.getCurrentActivityInstance()
				.getOperators();
		String wfname = wfActContext.getCurrentActivity().getName();
		WFTask task = this.wfActContext.getCurrentTask();
		if (task == null) {
			throw new BusinessException("task为空！");
		}
		String user_name = "";// 当前审批人
		String userID = InvocationInfoProxy.getInstance().getUserId();// 获取当前系统登录用户
		if (userID != null && StringUtils.isNotEmpty(userID)) {
			user_name = (String) new HYPubBO().findColValue("sm_user",
					"user_name", " cuserid = '" + userID + "'");
		}
		// 所有应收单对接税务
		if (task.getBillType() != null
				&& ("F0".equals(task.getBillType().substring(0, 2)) || "D0"
						.equals(task.getBillType().substring(0, 2)))) {
			AggReceivableBillVO aggVO = (AggReceivableBillVO) task
					.getOutObject();// 应收单 AggVO
			ReceivableBillVO headvo = (ReceivableBillVO) aggVO.getParentVO();// 表头VO
			String gxtask_name = (String) new HYPubBO().findColValue(
					"bd_defdoc", "code",
					"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
							+ "FROM BD_DEFDOCLIST WHERE CODE = 'DGTASKNAME')"
							+ " and name = '" + wfname + "'");// 流程节点名称
			if (gxtask_name != null) {
				pushDG(headvo);
			}
			// 热力下属公司指定节点后调用OA归档接口
			String strWhere = " nvl(dr,0) = 0  and pk_bill ='"
					+ headvo.getPrimaryKey() + "'";
			WorkFlowBill[] workFlowBills = (WorkFlowBill[]) new HYPubBO()
					.queryByCondition(WorkFlowBill.class, strWhere);
			// 热力下属公司应收单流程节点名称
			String rl_rectaskname = (String) new HYPubBO().findColValue(
					"bd_defdoc", "code",
					"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
							+ "FROM BD_DEFDOCLIST WHERE CODE = 'RLYSD')"
							+ " and name = '" + wfname + "'");// 流程节点名称
			if (null != workFlowBills && rl_rectaskname != null) {
				appr(headvo);
			}
		}
		if (StringUtils.equals(task.getBillType(), "F3-Cxx-gcgxfk-zyfb")
				|| StringUtils.equals(task.getBillType(), "F3-Cxx-I8FYJKD")
				|| StringUtils.equals(task.getBillType(), "F3-Cxx-I8CLJKD")
				|| StringUtils.equals(task.getBillType(), "F3-Cxx-I8FYBXD")
				|| StringUtils.equals(task.getBillType(), "F3-Cxx-I8CLBXD")) {
			AggPayBillVO aggVO = (AggPayBillVO) task.getOutObject();// 付款单 AggVO
			PayBillVO headvo = (PayBillVO) aggVO.getParentVO();// 表头VO
			String bill_tag = (String) new HYPubBO().findColValue("bd_defdoc",
					"code", "name='" + task.getBillType()
							+ "' and shortname <> '~'");
			if (headvo.getDef89() != null) {
				bill_tag = headvo.getDef89();
			}
			if (bill_tag == null || "".equals(bill_tag)) {
				throw new BusinessException("根据[" + task.getBillType()
						+ "]未获取到I8业务标识！");
			}
			JSONObject json = new JSONObject();
			json.put("bill_no", headvo.getDef65()); // I8单据号
			json.put("bill_tag", bill_tag); // I8单据状态
			json.put("user_ncbillcode", headvo.getBillno());// NC单据号
			json.put("wfname", wfname);// 当前节点名称
			json.put("pk_primarykey", headvo.getPrimaryKey());// 单据主键
			json.put("billtype", task.getBillType());// 单据类型
			json.put("creator", headvo.getCreator());// 创建人
			json.put("user_nccheckpsn", user_name);// 当前节点审批人
			pushI8(json);
		} else if (StringUtils.equals(task.getBillType(),
				"264X-Cxx-gcgxbx-xmbx")
				|| StringUtils.equals(task.getBillType(),
						"264X-Cxx-gcgxht-zbht")
				|| StringUtils.equals(task.getBillType(),
						"264X-Cxx-gcgxht-cght")
				|| StringUtils.equals(task.getBillType(),
						"264X-Cxx-gcgxht-zlfb")
				|| StringUtils.equals(task.getBillType(),
						"264X-Cxx-gcgxht-lwfb")
				|| StringUtils.equals(task.getBillType(),
						"264X-Cxx-gcgxht-zyfb")
				|| StringUtils.equals(task.getBillType(),
						"264X-Cxx-GCCLZJH")) {
			BXVO bxVO = (BXVO) task.getOutObject();// 报销单 AggVO
			JKBXHeaderVO headvo = (JKBXHeaderVO) bxVO.getParentVO();// 表头VO
			String bill_tag = (String) new HYPubBO().findColValue("bd_defdoc",
					"code", "name='" + task.getBillType()
							+ "' and shortname <> '~'");
			if (bill_tag == null || "".equals(bill_tag)) {
				throw new BusinessException("根据[" + task.getBillType()
						+ "]未获取到I8业务标识！");
			}
			JSONObject json = new JSONObject();
			json.put("bill_no", headvo.getZyx29()); // I8单据号
			json.put("bill_tag", bill_tag); // I8单据状态
			json.put("user_ncbillcode", headvo.getDjbh());// NC单据号
			json.put("wfname", wfname);// 当前节点名称
			json.put("pk_primarykey", headvo.getPrimaryKey());// 单据主键
			json.put("billtype", task.getBillType());// 单据类型
			json.put("creator", headvo.getCreator());// 创建人
			json.put("user_nccheckpsn", user_name);// 当前节点审批人
			pushI8(json);
		} else if (StringUtils.equals(task.getBillType(), "F1-Cxx-gcgxyf-zyfb")
				|| StringUtils.equals(task.getBillType(), "F1-Cxx-gcgxyf-lwfb")
				|| StringUtils.equals(task.getBillType(), "F1-Cxx-gcgxyf-zlht")
				|| StringUtils.equals(task.getBillType(), "F1-Cxx-gcgxyf-cght")
				|| StringUtils.equals(task.getBillType(), "F1-Cxx-gcgxfpgz")) {
			AggPayableBillVO aggVO = (AggPayableBillVO) task.getOutObject();// 应付单
																			// AggVO
			PayableBillVO headvo = (PayableBillVO) aggVO.getParentVO();// 表头VO
			String bill_tag = (String) new HYPubBO().findColValue("bd_defdoc",
					"code", "name='" + task.getBillType()
							+ "' and shortname <> '~'");
			if (bill_tag == null || "".equals(bill_tag)) {
				throw new BusinessException("根据[" + task.getBillType()
						+ "]未获取到I8业务标识！");
			}
			JSONObject json = new JSONObject();
			json.put("bill_no", headvo.getDef65()); // I8单据号
			json.put("bill_tag", bill_tag); // I8单据状态
			json.put("user_ncbillcode", headvo.getBillno());// NC单据号
			json.put("wfname", wfname);// 当前节点名称
			json.put("pk_primarykey", headvo.getPrimaryKey());// 单据主键
			json.put("billtype", task.getBillType());// 单据类型
			json.put("creator", headvo.getCreator());// 创建人
			// json.put("spr", user_name);// 当前节点审批人
			pushI8(json);
		} else if (StringUtils.equals(task.getBillType(), "F2-Cxx-gcgxsk-skd")
				|| StringUtils.equals(task.getBillType(), "F2-Cxx-I8HKD")) {
			AggGatheringBillVO aggVO = (AggGatheringBillVO) task.getOutObject();// 收款单
																				// AggVO
			GatheringBillVO headvo = (GatheringBillVO) aggVO.getParentVO();// 表头VO
			String bill_tag = (String) new HYPubBO().findColValue("bd_defdoc",
					"code", "name='" + task.getBillType()
							+ "' and shortname <> '~'");
			if (headvo.getDef89() != null) {
				bill_tag = headvo.getDef89();
			}
			if (bill_tag == null || "".equals(bill_tag)) {
				throw new BusinessException("根据[" + task.getBillType()
						+ "]未获取到I8业务标识！");
			}
			JSONObject json = new JSONObject();
			json.put("bill_no", headvo.getDef65()); // I8单据号
			json.put("bill_tag", bill_tag); // I8单据状态
			json.put("user_ncbillcode", headvo.getBillno());// NC单据号
			json.put("wfname", wfname);// 当前节点名称
			json.put("pk_primarykey", headvo.getPrimaryKey());// 单据主键
			json.put("billtype", task.getBillType());// 单据类型
			json.put("creator", headvo.getCreator());// 创建人
			// json.put("spr", user_name);// 当前节点审批人
			pushI8(json);
		} else if (StringUtils.equals(task.getBillType(), "F0-Cxx-gcgxys-fpsq")) {
			AggReceivableBillVO aggVO = (AggReceivableBillVO) task
					.getOutObject();// 应收单 AggVO
			ReceivableBillVO headvo = (ReceivableBillVO) aggVO.getParentVO();// 表头VO
			String bill_tag = (String) new HYPubBO().findColValue("bd_defdoc",
					"code", "name='" + task.getBillType()
							+ "' and shortname <> '~'");
			if (bill_tag == null || "".equals(bill_tag)) {
				throw new BusinessException("根据[" + task.getBillType()
						+ "]未获取到I8业务标识！");
			}
			JSONObject json = new JSONObject();
			json.put("bill_no", headvo.getDef65()); // I8单据号
			json.put("bill_tag", bill_tag); // I8单据状态
			json.put("user_ncbillcode", headvo.getBillno());// NC单据号
			json.put("wfname", wfname);// 当前节点名称
			json.put("pk_primarykey", headvo.getPrimaryKey());// 单据主键
			json.put("billtype", task.getBillType());// 单据类型
			json.put("creator", headvo.getCreator());// 创建人
			// json.put("spr", user_name);// 当前节点审批人
			pushI8(json);
		} else {
			if (task.getPk_org() != null) {
				OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(
						OrgVO.class, task.getPk_org());// 组织VO
				if (orgVO != null
						&& (StringUtils.equals("1", orgVO.getDef2()) || StringUtils
								.equals("HR", orgVO.getDef2()))) {
					// 热力付款单共享复核调用OA归档接口
					String fktype = (String) new HYPubBO().findColValue(
							"sys_config", "config_value",
							"nvl(dr,0) = 0 and config_id='62'");
					if (fktype != null && fktype.contains(task.getBillType())) {
						if (StringUtils.equals(wfname, "共享复核")
								|| (fktype.indexOf("F3-Cxx-XCFKSPD") != -1 && wfname
										.indexOf("财务确认") != -1)) {
							AggPayBillVO aggfkVO = (AggPayBillVO) task
									.getOutObject();// 付款单 AggVO
							String mes = appr(aggfkVO);
							if (!"".equals(mes)) {
								throw new BusinessException("热力付款共享同步OA报错："
										+ mes);
							}
						}
					}
					String billtype = task.getBillType();
					if (StringUtils.equals(wfname, "共享复核")) {
						if ("D1".equals(billtype)
								|| "F1-Cxx-01".equals(billtype)
								|| "F1-Cxx-ZCBZ".equals(billtype)
								|| "F1-Cxx-FYFPHZBZD".equals(billtype)) {
							AggPayableBillVO ykvo = (AggPayableBillVO) task
									.getOutObject();// 应付VO
							PayableBillVO hvo = (PayableBillVO) ykvo
									.getParent();
							String mes = appr2(ykvo);
							if (!"".equals(mes)) {
								throw new BusinessException("热力应付共享同步OA报错："
										+ mes);
							}
						}
					}
				} else if (orgVO != null
						&& StringUtils.equals("4", orgVO.getDef2())) {
					if ("F3-Cxx-XCFKSPD".equals(task.getBillType())
							&& wfname.indexOf("财务确认") != -1) {
						apprZT(task.getBillID());
					} else {
						String billtype = (String) new HYPubBO().findColValue(
								"sys_config", "config_value",
								"nvl(dr,0) = 0 and config_id='64'");// 交易类型
						if (billtype != null
								&& billtype.contains(task.getBillType())) {
							String gxtask_name = (String) new HYPubBO()
									.findColValue(
											"bd_defdoc",
											"code",
											"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
													+ "FROM BD_DEFDOCLIST WHERE CODE = 'ZTGX_TASKNAME')"
													+ " and name = '" + wfname
													+ "'");// 流程节点名称
							if (gxtask_name != null) {
								// 工程的 报销单的工程付款合同转为付款合同合同的工程付款合同
								String mes = apprZT(task.getBillID());
								if (!"".equals(mes)) {
									throw new BusinessException("调用OA中台共享报错："
											+ mes);
								}
							}
						}
					}
				}
			} else {
				throw new BusinessException("流程获取组织错误！");
			}
		}
	}

	// 应收单调用OA归档接口
	private void appr(ReceivableBillVO hvo) throws BusinessException {
		// TODO Auto-generated method stub
		WorkFlowBill[] vo = (WorkFlowBill[]) new HYPubBO().queryByCondition(
				WorkFlowBill.class, "pk_bill='" + hvo.getPrimaryKey() + "'");
		if (vo != null && vo.length > 0) {
			String url = (String) new HYPubBO().findColValue("sys_config",
					"config_value", "config_key='oa-url'");
			if (hvo.getBilldate().compareTo(new UFDate("2021-10-13")) > 0) {
				Logger.error("hvo.getPrimaryKey()==" + hvo.getPrimaryKey());

				// PM_WORKFLOW_BILL 自定义项2作为判断是否调用接口 XBX 20211209新增
				if (vo[0].getDef2() == null || !"NO".equals(vo[0].getDef2())) {
					String requestid = vo[0].getRequestid() + "";
					Map<String, String> params = new HashMap<>();
					params.put("requestid", requestid);
					Map<String, String> headers = new HashMap<>();
					headers.put("Content-Type",
							"application/x-www-form-urlencoded");
					String back = HttpClient.httpPostForm(url, params, headers,
							"utf-8");
					JSONObject res = JSONObject.fromObject(back);
					String success = res.getString("error_code");
					if (!"0".equals(success)) {
						throw new BusinessException("应收单调用OA获取NC更新数据通知接口出错："
								+ res.getString("error_msg"));
					}
					WorkFlowBill voa = vo[0];
					voa.setDef2("NO");
					voa.setStatus(VOStatus.UPDATED);
					new HYPubBO().update(voa);
				}
			}
		}
	}

	// 应收单调用东港税务接口
	private void pushDG(ReceivableBillVO headvo) throws BusinessException {
		// TODO Auto-generated method stub
		String dgzturl = (String) new HYPubBO()
				.findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and code = 'DGZTURL' and pk_defdoclist in "
								+ "(select pk_defdoclist from bd_defdoclist where code = 'THIRDRELA' and nvl(dr,0) = 0)");
		JSONObject json = new JSONObject();
		// 发送参数
		JSONObject sendData = new JSONObject();
		sendData.put("pkorg", headvo.getPk_org());// 组织主键
		sendData.put("billno", headvo.getBillno());// 单据编号
		Map<String, String> heads = new HashMap<String, String>();
		heads.put("appKey", "7VE3lgxCQyH5MLEGhZM7");
		heads.put("serviceId", "S20001");
		String res = "";// 返回参数
		// 调用接口返回
		res = HttpClient.httpPostRaw(dgzturl, sendData.toString(), heads,
				"utf-8");
		JSONObject retData = JSONObject.fromObject(res);
		SaveBillLog(sendData.toString(), res, headvo.getPk_org(),
				headvo.getPk_billtype());
	}

	// 东港日志记录
	private String SaveBillLog(String recdata, String rdtdata, String pk_org,
			String bill_type) throws BusinessException {
		BillLogVO vo = new BillLogVO();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		vo.setCreationtime(df.format(new Date()));// 创建时间
		vo.setDef1("税务状态");
		vo.setTransi_type(bill_type); // 单据类型
		vo.setRecdata(recdata);// 接收/发送参数
		vo.setRdtdata(rdtdata);// 返回参数
		vo.setPk_org(pk_org);
		vo.setPk_group("0001A1100000000001QS");
		try {
			new HYPubBO().insert(vo);
		} catch (BusinessException e) {
			// TODO: handle exception
			return "接收税务系统单据状态日志保存失败：" + e.getMessage();
		}
		return "";
	}

	private void pushI8(JSONObject jsdt) throws BusinessException {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		if (StringUtils.equals(jsdt.getString("wfname"), "共享服务用户<手工活动>")) {
			// 4--共享已结束，待收单
			// json.put("spr", jsdt.getString("curapprover"));// 节点审批人
			if (jsdt.has("bill_no")) {
				json.put("bill_no", jsdt.getString("bill_no"));// I8单据号
			}
			if (jsdt.has("bill_tag")) {
				json.put("bill_tag", jsdt.getString("bill_tag"));// I8状态
			}
			if (jsdt.has("user_ncbillcode")) {
				json.put("user_ncbillcode", jsdt.getString("user_ncbillcode"));// NC单据号
			}
			if (jsdt.has("user_nccheckpsn")) {
				json.put("user_nccheckpsn", jsdt.getString("user_nccheckpsn"));// 当前审核人
			}
			String url = (String) new HYPubBO().findColValue("sys_config",
					"config_value", "config_key='i8-url'");
			if (url == null) {
				throw new BusinessException("I8接口URL地址为空,请配置SYS_CONFIG数据表！");
			}
			json.put("bill_status", "4");// 状态
			Map<String, String> mes = sendRollBack(url, json);
			SaveI8Log(jsdt.getString("bill_no"),
					jsdt.getString("pk_primarykey"),
					jsdt.getString("billtype"), jsdt.getString("creator"),
					mes.get("i8mes"), jsdt.getString("bill_no"),
					json.toString(), "共享审核");
			Logger.error("发送I8数据：" + json);
			if ((mes.get("mes") != null && !"".equals(mes.get("mes")))) {
				throw new BusinessException("I8返回错误提示：" + mes.get("mes"));
			}
		} else if (StringUtils.contains(jsdt.getString("wfname"), "收单人")) {// 等于改为包含
			// 5--已收单
			if (jsdt.has("bill_no")) {
				json.put("bill_no", jsdt.getString("bill_no"));// I8单据号
			}
			if (jsdt.has("bill_tag")) {
				json.put("bill_tag", jsdt.getString("bill_tag"));// I8状态
			}
			if (jsdt.has("user_ncbillcode")) {
				json.put("user_ncbillcode", jsdt.getString("user_ncbillcode"));// NC单据号
			}
			/*
			 * if(jsdt.has("user_nccheckpsn")){ json.put("user_nccheckpsn",
			 * jsdt.getString("user_nccheckpsn"));// 当前审核人 }
			 */
			String url = (String) new HYPubBO().findColValue("sys_config",
					"config_value", "config_key='i8-url'");
			if (url == null) {
				throw new BusinessException("I8接口URL地址为空,请配置SYS_CONFIG数据表！");
			}
			json.put("bill_status", "5");// 状态
			Map<String, String> mes = sendRollBack(url, json);
			SaveI8Log(jsdt.getString("bill_no"),
					jsdt.getString("pk_primarykey"),
					jsdt.getString("billtype"), jsdt.getString("creator"),
					mes.get("i8mes"), jsdt.getString("bill_no"),
					json.toString(), "收单审核");
			Logger.error("发送I8数据：" + json);
			if ((mes.get("mes") != null && !"".equals(mes.get("mes")))) {
				throw new BusinessException("I8收单返回报错：" + mes.get("mes"));
			}

		}
	}

	// 调用I8驳回接口
	private Map<String, String> sendRollBack(String url, JSONObject json)
			throws UifException, BusinessException {
		Map<String, String> map = new HashMap<>();
		Map<String, String> headers = new HashMap<>();
		// headers.put("Zoomkey-Auth-Token", zoomkeyAuthToken);
		Logger.error("发送I8数据：" + json.toString());
		String mes = HttpClient.httpPostRaw(url, json.toString(), headers,
				"utf-8");
		if (mes == null || "".equals(mes)) {
			map.put("mes", "调用I8系统接口返回数据为空，请检查！");
			return map;
		}
		map.put("i8mes", mes);
		Logger.error("I8返回数据：" + mes);
		JSONObject res = JSONObject.fromObject(mes);
		String status = res.getString("status");
		if (!"S".equals(status)) {
			map.put("mes", res.getString("message"));
			// return res.getString("message");
			// throw new BusinessException("调用I8系统接口出错:" + info);
		}
		return map;
	}

	// 调用OA共享复核审批接口
	private String appr(AggregatedValueObject aggVO) throws BusinessException {
		// TODO Auto-generated method stub
		String mes = "";
		AggPayBillVO fkvo = (AggPayBillVO) aggVO;
		PayBillVO hvo = (PayBillVO) fkvo.getParent();
		WorkFlowBill[] vo = (WorkFlowBill[]) new HYPubBO().queryByCondition(
				WorkFlowBill.class, "pk_bill='" + hvo.getPrimaryKey() + "'");
		if (vo != null && vo.length > 0) {
			String url = (String) new HYPubBO().findColValue("sys_config",
					"config_value", "config_key='oa-url'");
			Logger.error("url==" + url);
			if (hvo.getBilldate().compareTo(new UFDate("2021-10-13")) > 0) {
				Logger.error("hvo.getPrimaryKey()==" + hvo.getPrimaryKey());

				OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(
						OrgVO.class, hvo.getPk_org());
				if (orgVO.getDef2() == null) {
					return "";
				}

				// PM_WORKFLOW_BILL 自定义项2作为判断是否调用接口 XBX 20211209新增
				if (vo[0].getDef2() == null || !"NO".equals(vo[0].getDef2())) {
					String requestid = vo[0].getRequestid() + "";
					Map<String, String> params = new HashMap<>();
					params.put("requestid", requestid);
					Map<String, String> headers = new HashMap<>();
					headers.put("Content-Type",
							"application/x-www-form-urlencoded");
					String back = HttpClient.httpPostForm(url, params, headers,
							"utf-8");
					JSONObject res = JSONObject.fromObject(back);
					String success = res.getString("error_code");
					if (!"0".equals(success)) {
						mes = "调用OA获取NC更新数据通知接口出错："
								+ res.getString("error_msg");
					} else {
						WorkFlowBill voa = vo[0];
						voa.setDef2("NO");
						voa.setStatus(VOStatus.UPDATED);
						new HYPubBO().update(voa);
					}
				}
			}
		}
		return mes;
	}

	private String appr2(AggregatedValueObject aggVO) throws BusinessException {
		// TODO Auto-generated method stub
		String mes = "";
		AggPayableBillVO ykvo = (AggPayableBillVO) aggVO;
		PayableBillVO hvo = (PayableBillVO) ykvo.getParent();
		WorkFlowBill[] vo = (WorkFlowBill[]) new HYPubBO().queryByCondition(
				WorkFlowBill.class, "pk_bill='" + hvo.getPrimaryKey() + "'");
		if (vo != null && vo.length > 0) {
			String url = (String) new HYPubBO().findColValue("sys_config",
					"config_value", "config_key='oa-url'");
			if (hvo.getBilldate().compareTo(new UFDate("2021-10-13")) > 0) {
				// PM_WORKFLOW_BILL 自定义项2作为判断是否调用接口 XBX 20211209新增
				if (vo[0].getDef2() == null || !"NO".equals(vo[0].getDef2())) {
					String requestid = vo[0].getRequestid() + "";
					Map<String, String> params = new HashMap<>();
					params.put("requestid", requestid);
					Map<String, String> headers = new HashMap<>();
					headers.put("Content-Type",
							"application/x-www-form-urlencoded");
					String back = HttpClient.httpPostForm(url, params, headers,
							"utf-8");
					JSONObject res = JSONObject.fromObject(back);
					String success = res.getString("error_code");
					if (!"0".equals(success)) {
						mes = "调用OA获取NC更新数据通知接口出错："
								+ res.getString("error_msg");
					}
					WorkFlowBill voa = vo[0];
					voa.setDef2("NO");
					voa.setStatus(VOStatus.UPDATED);
					new HYPubBO().update(voa);
				}
			}
		}
		return mes;
	}

	// 调用中台OA共享复核审批接口
	private String apprZT(String pk_bill) throws BusinessException {
		// TODO Auto-generated method stub
		String mes = "";
		WorkFlowBill[] vo = (WorkFlowBill[]) new HYPubBO().queryByCondition(
				WorkFlowBill.class, "pk_bill='" + pk_bill + "'");
		if (vo != null && vo.length > 0) {
			String url = (String) new HYPubBO().findColValue("sys_config",
					"config_value", "config_key='zt-url'");
			Logger.error("url==" + url);
			// PM_WORKFLOW_BILL 自定义项2作为判断是否调用接口 XBX 20211209新增
			if (vo[0].getDef2() == null || !"NO".equals(vo[0].getDef2())) {
				String requestid = vo[0].getRequestid() + "";
				Map<String, String> params = new HashMap<>();
				String user_name = "";// 当前审批人
				String userID = InvocationInfoProxy.getInstance().getUserId();// 获取当前系统登录用户
				if (userID != null && StringUtils.isNotEmpty(userID)) {
					user_name = (String) new HYPubBO().findColValue("sm_user",
							"user_name", " cuserid = '" + userID + "'");
				}
				if (StringUtils.isNotEmpty(user_name)) {
					params.put("gxshr", user_name);
				}
				params.put("requestid", requestid);
				Logger.error("发送参数：" + params.toString());
				System.out.println("发送参数：" + params.toString());
				Map<String, String> headers = new HashMap<>();
				headers.put("Content-Type", "application/x-www-form-urlencoded");
				String back = HttpClient.httpPostForm(url, params, headers,
						"utf-8");
				if (StringUtils.isEmpty(back)) {
					mes = "调用OA中台共享归档后返回空数据，参数为：" + params.toString();
				} else {
					JSONObject res = JSONObject.fromObject(back);
					String success = res.getString("error_code");
					if (!"0".equals(success)) {
						mes = "调用OA中台共享归档出错：" + res.getString("error_msg");
					} else {
						WorkFlowBill voa = vo[0];
						voa.setDef2("NO");
						voa.setBill_type(params.toString());
						voa.setStatus(VOStatus.UPDATED);
						new HYPubBO().update(voa);
					}
				}
			}
		}
		return mes;
	}

	// 保存调用I8系统接口记录
	private String SaveI8Log(String bllno, String primarykey, String tradetype,
			String creator, String rdtdata, String i8djh, String sendata,
			String type) throws BusinessException {
		// TODO Auto-generated method stub
		String mes = "";
		I8LogVO vo = new I8LogVO();
		vo.setBill_code(bllno);
		vo.setDef1(sendata);// 发送数据
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		vo.setCreationtime(df.format(new Date()));// 创建时间
		vo.setPk_bill(primarykey);// 单据主键
		vo.setTransi_type(tradetype); // 交易类型
		vo.setUserid(creator);// 创建人
		vo.setRdtdata(rdtdata);// I8返回数据
		vo.setI8djh(i8djh);// I8单据号
		vo.setDef2(type);// 类型
		try {
			new HYPubBO().insert(vo);
		} catch (BusinessException e) {
			// TODO: handle exception
			return e.getMessage();
		}
		return mes;
	}

	private void completeWFProcessInstance() {
		Logger.debug("流程实例 正常结束");
		invalidateActivityInstanceWhenComplete(this.wfActContext
				.getWfProcessInstance().findAllUnfinishedActivityInstances());
		this.wfActContext
				.changeCurrentProcessInstanceStatus(WfTaskOrInstanceStatus.Finished
						.getIntValue());

		if ((this.wfActContext.getParentWfProcessInstancePK() != null)
				|| (this.wfActContext.getParentActivityInstancePK() != null)) {
			this.inputTaskList.add(createNotifyMainFlowTask());
		}
	}

	private void executePreScript(ActivityInstance toActivityInstance,
			Activity toActivity) {
		if (!(this.wfActContext.getCurrentActivity() instanceof ActivityEx))
			return;
		ActivityEx act = (ActivityEx) this.wfActContext.getCurrentActivity();

		String script = act.getPreScript();
		if (StringUtil.isEmptyWithTrim(script)) {
			return;
		}
		try {
			Object inObject = toActivityInstance.getInObject();
			ExpressContext expressContext = createExpressContext(inObject);
			if (!this.tmpOutputTaskList.isEmpty())
				expressContext.AddInherentObject("Task",
						this.tmpOutputTaskList.get(0));
			this.wfActContext.executeRelevantDataExchange(script.trim(),
					expressContext);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new ScriptExecuteException(NCLangResOnserver.getInstance()
					.getStrByID("pfworkflow", "UPPpfworkflow-000327")
					+ toActivity.getName()
					+ NCLangResOnserver.getInstance().getStrByID("pfworkflow",
							"UPPpfworkflow-000328") + e.getMessage());
		}
	}

	private void executePostScript() {
		Logger.debug("活动结束时，执行其后置脚本=WorkflowRunner.executePostScript() called");
		if (!(this.wfActContext.getCurrentActivity() instanceof ActivityEx))
			return;
		ActivityEx act = (ActivityEx) this.wfActContext.getCurrentActivity();
		String script = act.getPostScript();

		if ((script != null) && (script.trim().length() > 0)) {
			try {
				Object outObject = this.wfActContext
						.getCurrentActivityInstance().getOutObject();
				ExpressContext expressContext = createExpressContext(outObject);
				expressContext.AddInherentObject("Task",
						this.wfActContext.getCurrentTask());

				this.wfActContext.executeRelevantDataExchange(script,
						expressContext);
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
				throw new ScriptExecuteException(NCLangResOnserver
						.getInstance().getStrByID("pfworkflow",
								"UPPpfworkflow-000327")
						+ act.getName()
						+ NCLangResOnserver.getInstance().getStrByID(
								"pfworkflow", "UPPpfworkflow-000329")
						+ e.getMessage());
			}
		}
	}

	private ExpressContext createExpressContext(Object bizObject) {
		ExpressContext expContext = new ExpressContext();
		expContext.setBizObject(bizObject);
		expContext.setDataFields(this.wfActContext.getDataFields());
		expContext.setDatas(this.wfActContext.getDatas());
		return expContext;
	}

	@SuppressWarnings("deprecation")
	private boolean isConditionTrue(Condition condition)
			throws BusinessException {
		Logger.error("###WorkflowRunner executeTransitions 开始  condition "
				+ condition.getValue() + " " + System.currentTimeMillis()
				+ "ms");
		Object outObject = this.wfActContext.getCurrentActivityInstance()
				.getOutObject();
		ExpressContext ec = createExpressContext(outObject);

		ec.setBillId(this.wfActContext.getCurrentTask().getBillversionPK());
		ec.setStrCheckResult(this.wfActContext.getCurrentTask()
				.getApproveResult());

		ec.getDatas().put("ACT_Check_Result",
				this.wfActContext.getCurrentTask().getApproveResult());
		return ExpressExcutor.isConditionTrue(condition, ec);
	}

	private ArrayList<Activity> getVirtualConnectedActivities(Activity activity) {
		ArrayList<Activity> trans = getVirtualNeighborActivities(activity);
		HashSet<Activity> set = new HashSet();
		set.addAll(trans);
		return new ArrayList(set);
	}

	private ArrayList<Activity> getVirtualNeighborActivities(Activity activity) {
		ArrayList<Activity> retVal = new ArrayList();
		Map mapTransitions = activity.getEfferentTransitions();
		ArrayList<Transition> alTransitions = new ArrayList(
				mapTransitions.values());

		for (Transition t : alTransitions) {
			Activity toActivity = t.getToActivity();
			retVal.add(toActivity);
		}

		return retVal;
	}

	private void executeTransitions(ArrayList<Transition> alTransitions)
			throws BusinessException {
		Logger.error("###WorkflowRunner executeTransitions 开始 "
				+ System.currentTimeMillis() + "ms");
		Logger.debug(">>>WorkflowRunner.executeTransitions() 开始流转");
		SplitJoinType splitType = this.wfActContext.getCurrentActivity()
				.getPostcondition();

		String postconditionValue = this.wfActContext.getCurrentActivity()
				.getPostconditionValue();

		if (isNeedSortTransition(this.wfActContext.getCurrentActivity())) {
			new WfDispatchUtils();
			WfDispatchUtils.sortTransitions(alTransitions);

			boolean isWorkflow = this.wfActContext.getWfProcessDef()
					.getWorkflowType() == WorkflowTypeEnum.Workflow
					.getIntValue();
			if (isWorkflow) {
				alTransitions = resortByChoicedTransition(alTransitions);
			} else {
				alTransitions = resortByDispatchedActivity(alTransitions);
			}
		}

		int firedCount = 0;
		Transition otherwise = null;

		double postThreshold = fetchPreAndPostThreshold(
				this.wfActContext.getCurrentActivity(), false);
		for (int i = 0; i < alTransitions.size(); i++) {
			Transition transition = (Transition) alTransitions.get(i);
			Condition condition = transition.getCondition();
			ConditionType type = condition == null ? null : condition.getType();
			if ((condition == null)
					|| ((type == ConditionType.CONDITION) && (isConditionTrue(condition)))) {
				executeTransition(transition);
				firedCount++;

				if ((splitType == SplitJoinType.AND)
						&& (firedCount >= postThreshold)) {
					break;
				}

				if (splitType == SplitJoinType.XOR) {
					break;
				}
			} else if (type == ConditionType.OTHERWISE) {
				otherwise = transition;
			}
		}

		if ((firedCount == 0) && (otherwise != null)) {
			executeTransition(otherwise);
			firedCount++;
		}

		if (firedCount == 0) {
			throw new PFBusinessException(NCLangResOnserver.getInstance()
					.getStrByID("pfworkflow", "UPPpfworkflow-000330"));
		}

		Logger.debug(">>>WorkflowRunner.executeTransitions() 结束流转");
		Logger.error("###WorkflowRunner executeTransitions 结束 "
				+ System.currentTimeMillis() + "ms");
	}

	private boolean isNeedSortTransition(Activity currentActivity) {
		SplitJoinType splitType = currentActivity.getPostcondition();

		if (splitType == null) {
			return false;
		}
		if (splitType == SplitJoinType.XOR) {
			return true;
		}
		double postThreshold = fetchPreAndPostThreshold(currentActivity, false);
		int postTransitionSize = currentActivity.getEfferentTransitions()
				.size();

		return postTransitionSize > postThreshold;
	}

	private double fetchPreAndPostThreshold(Activity currentActivity,
			boolean isprecondition) {
		if (isprecondition) {
			String preconditionValue = currentActivity.getPreconditionValue();

			int afferentSize = currentActivity.getAfferentTransitions().size();
			double preThresholdSize = 0.0D;
			if (isPercentcheck(preconditionValue)) {
				preThresholdSize = Double.valueOf(
						preconditionValue.substring(0,
								preconditionValue.length() - 1)).doubleValue()
						* afferentSize / 100.0D;
			} else
				preThresholdSize = Integer.valueOf(preconditionValue)
						.intValue();
			if (preThresholdSize > afferentSize)
				return afferentSize;
			return preThresholdSize;
		}
		String postconditionvalue = currentActivity.getPostconditionValue();

		int efferentSize = currentActivity.getEfferentTransitions().size();
		double postThresholdSize = 0.0D;
		if (isPercentcheck(postconditionvalue)) {
			postThresholdSize = Double.valueOf(
					postconditionvalue.substring(0,
							postconditionvalue.length() - 1)).doubleValue()
					* efferentSize / 100.0D;
		} else {
			postThresholdSize = Integer.valueOf(postconditionvalue).intValue();
		}
		if (postThresholdSize > efferentSize)
			return efferentSize;
		return postThresholdSize;
	}

	private ArrayList resortByChoicedTransition(ArrayList sortTransitions) {
		ArrayList alChoiced = new ArrayList();
		ArrayList alNoChoiced = new ArrayList();
		for (Iterator iter = sortTransitions.iterator(); iter.hasNext();) {
			Transition tran = (Transition) iter.next();

			Vector choicedTrans = (Vector) this.wfActContext
					.getDatas()
					.get(WFTask.getTransAssignID(
							this.wfActContext.getWfProcessDefPK(), tran.getId()));
			if ((choicedTrans != null) && (choicedTrans.size() > 0)) {
				alChoiced.add(tran);
			} else {
				alNoChoiced.add(tran);
			}
		}
		alChoiced.addAll(alNoChoiced);
		return alChoiced;
	}

	private ArrayList resortByDispatchedActivity(ArrayList sortTransitions) {
		ArrayList alDispatched = new ArrayList();
		ArrayList alNoDispatched = new ArrayList();
		for (Iterator iter = sortTransitions.iterator(); iter.hasNext();) {
			Transition tran = (Transition) iter.next();

			boolean isAlreadyDispatched = IsTargetActivityDispatched(tran);
			if (isAlreadyDispatched) {
				alDispatched.add(tran);
			} else {
				alNoDispatched.add(tran);
			}
		}
		alDispatched.addAll(alNoDispatched);
		return alDispatched;
	}

	private boolean IsTargetActivityDispatched(Transition tran) {
		LinkedList<ActivityPathInfo> llPendingCheckActs = new LinkedList();
		LinkedList<Transition> llTransitionPath = new LinkedList();
		WfDispatchUtils.getCheckActivitiesRecursive(tran.getToActivity()
				.getWorkflowProcess(), this.wfActContext
				.getWfProcessInstancePK(), llPendingCheckActs,
				llTransitionPath, tran.getToActivity(), null, this.wfActContext
						.getCurrentTask().getBillID());

		for (ActivityPathInfo api : llPendingCheckActs) {
			Activity act = api.getActivity();

			Vector operators = (Vector) this.wfActContext.getDatas().get(
					WFTask.getActAssignID(act.getProcessDefPK(), act.getId()));
			if ((operators != null) && (operators.size() > 0))
				return true;
		}
		return false;
	}

	private void executeTransition(Transition transition)
			throws BusinessException {
		Activity toActivty = transition.getToActivity();

		String actAssignKeyOfCurrentTrans = WFTask.getTransAssignID(
				this.wfActContext.getWfProcessDefPK(), transition.getId());
		this.wfActContext.getDatas().remove(actAssignKeyOfCurrentTrans);

		transferToActive(toActivty, transition);
	}

	private boolean canExecuteToActive(ActivityInstance toActivityInstance,
			Activity toActivity) throws BusinessException {
		if (toActivity.getAfferentTransitions().size() < 2) {
			return true;
		}
		SplitJoinType joinType = toActivity.getPrecondition();
		if (joinType.getValue() == 1) {
			return true;
		}

		if (toActivity.getActivityType() == ActivityTypeEnum.Makebill
				.getIntValue()) {
			return true;
		}

		int reachJoins = toActivity.getAfferentTransitions().size();

		double preThreshold = fetchPreAndPostThreshold(toActivity, true);
		return toActivityInstance.getReachJoins() >= preThreshold;
	}

	private boolean isPercentcheck(String value) {
		return (value != null) && (value.endsWith("%"));
	}

	private void transferToActive(Activity toActivity, Transition trans)
			throws BusinessException {
		if (toActivity.getActivityType() == ActivityTypeEnum.Makebill
				.getIntValue()) {
			invalidateActivityInstanceWhenForward(this.wfActContext
					.getWfProcessInstance()
					.findAllUnfinishedActivityInstances());
		}

		this.wfActContext.setTargetActivityID(toActivity.getId());

		if (!this.wfActContext.isSelfStartup()) {
			putInBizObject();
			this.wfActContext
					.addTargetActivityInstanceSrcActivityInstanceGUID(this.wfActContext
							.getCurrentActivityInstance()
							.getActivityInstancePK());
		} else if (this.wfActContext.getParentActivityInstancePK() != null) {
			this.wfActContext
					.addTargetActivityInstanceInObject(this.wfActContext
							.getCurrentTask().getInObject());
			this.wfActContext
					.addTargetActivityInstanceSrcActivityInstanceGUID(this.wfActContext
							.getParentActivityInstancePK());
		}

		invalidateOtherActivityInstance(this.wfActContext
				.getTransferTargetActivityInstance());

		this.wfActContext.increaseTargetAcitvityInstanceReachJoins();

		if (canExecuteToActive(
				this.wfActContext.getTransferTargetActivityInstance(),
				toActivity)) {
			startupActivityInstance(
					this.wfActContext.getTransferTargetActivityInstance(),
					toActivity);
		}
	}

	private void invalidateOtherActivityInstance(
			ActivityInstance activityInsance) {
		Vector instances = this.wfActContext.getWfProcessInstance()
				.findOtherEffectiveActivityInstance(
						activityInsance.getActivityID(),
						activityInsance.getActivityInstancePK());
		ActivityInstance instance = null;
		for (int i = 0; i < instances.size(); i++) {
			instance = (ActivityInstance) instances.get(i);
			instance.setStatus(WfTaskOrInstanceStatus.Inefficient.getIntValue());
			getInefficientActInstancePKs()
					.add(instance.getActivityInstancePK());
		}
	}

	private void startupActivityInstance(ActivityInstance toActivityInstance,
			Activity toActivity) throws BusinessException {
		Logger.error("###WorkflowRunner startupActivityInstance 开始"
				+ System.currentTimeMillis() + "ms");

		this.wfActContext.changeStatusOfActivityInstance(toActivityInstance,
				WfTaskOrInstanceStatus.Started);
		this.wfActContext.beforeEnterTargetActivity();
		if (toActivity.getImplementation() != null) {
			executeImplementation(toActivityInstance, toActivity);
		} else if (toActivity.getRoute() != null) {
			executeRoute(toActivityInstance);
		}

		beforeActiveRunning(toActivity, toActivityInstance);
		Logger.error("###WorkflowRunner startupActivityInstance 结束 "
				+ System.currentTimeMillis() + "ms");
	}

	private void putInBizObject() {
		if (this.wfActContext.getCurrentActivityInstance().getOutObject() != null) {
			this.wfActContext
					.addTargetActivityInstanceInObject(this.wfActContext
							.getCurrentActivityInstance().getOutObject());
		} else {
			this.wfActContext
					.addTargetActivityInstanceInObject(this.wfActContext
							.getCurrentTask().getInObject());
		}
	}

	private void executeToolSet(ToolSet toolSet,
			ActivityInstance toActivityInstance, Activity activity)
			throws BusinessException {
		Iterator i = toolSet.getTools().iterator();
		while (i.hasNext()) {
			Tool tool = (Tool) i.next();
			executeTool(tool, toActivityInstance, activity);
		}
	}

	private boolean executeTool(Tool tool, ActivityInstance ai,
			Activity activity) throws BusinessException {
		ToolType tt = tool.getToolType();
		if (tt == ToolType.PROCEDURE) {
			executeRoute(ai);
		} else {
			ApplicationTaskMaker taskMaker = new ApplicationTaskMaker(tool,
					activity, this.wfActContext);
			WFTask task = taskMaker.invoke();

			this.tmpOutputTaskList.add(task);
		}

		return true;
	}

	private void executeBackImplementation(ActivityInstance toActivityInstance,
			Activity toActivity, ActivityInstance oldToActivityInstance)
			throws BusinessException {
		Implementation implementation = toActivity.getImplementation();
		this.tmpOutputTaskList.clear();
		if ((implementation instanceof ToolSet)) {
			executeToolSet((ToolSet) implementation, toActivityInstance,
					toActivity);

			fillBackTaskContent(toActivityInstance, oldToActivityInstance);
		} else if ((implementation instanceof SubFlow)) {
			executeBackSubFlow(toActivityInstance, (SubFlow) implementation,
					oldToActivityInstance);
		}

		this.outputTaskList.addAll(this.tmpOutputTaskList);
	}

	private void executeImplementation(ActivityInstance toActivityInstance,
			Activity toActivity) throws BusinessException {
		Implementation implementation = toActivity.getImplementation();
		this.tmpOutputTaskList.clear();
		if ((implementation instanceof ToolSet)) {
			ToolSet ts = (ToolSet) implementation;
			executeToolSet(ts, toActivityInstance, toActivity);

			fillTaskContent(toActivityInstance);
		} else if ((implementation instanceof SubFlow)) {
			executeSubFlow(toActivityInstance, (SubFlow) implementation);
		}

		this.outputTaskList.addAll(this.tmpOutputTaskList);
	}

	private void executeSubFlow(ActivityInstance toActivityInstance,
			SubFlow subflow) throws BusinessException {
		WFTask subflowTask = WfTaskManager.getInstance().createTask(
				this.wfActContext.getCurrentTask().getPk_org(),
				this.wfActContext.getWfProcessDef().getWorkflowType());
		String subflowPK = null;
		if ((this.wfActContext.getWfProcessInstance() != null)
				&& (this.wfActContext.getWfProcessInstance()
						.getProcessSubflows() != null)) {
			List<ProcessSubflow> list = this.wfActContext
					.getWfProcessInstance().getProcessSubflows();
			for (ProcessSubflow subf : list) {
				if ((subf.getSubDefPK() != null)
						&& (subf.getSubDefPK().length() != 0)
						&& (subf.getProcessInstance().equals(this.wfActContext
								.getWfProcessInstancePK()))
						&& (subf.getSubProcessDefPK().equals(subflow.getId()))) {
					subflowPK = subf.getSubDefPK();
				}
			}
		}
		if (subflowPK == null) {
			subflowPK = new WorkflowDefineImpl()
					.findPrimaryKeyByGuid(subflow.getId(),
							this.wfActContext.getWfProcessInstancePK());
		}
		subflowTask.setWfProcessDefPK(subflowPK);
		subflowTask.setInObject(toActivityInstance.getInObject());

		copyBusiInfo(subflowTask, this.wfActContext.getCurrentTask());

		HashMap transferInfo = new HashMap();
		transferInfo.put("ACT_Check_Result", this.wfActContext
				.getWfProcessInstance().getRelevantData("ACT_Check_Result"));
		HashMap relevantDatas = this.wfActContext.getDatas();
		exchangeAssignInfo(this.wfActContext.getWfProcessDefPK(), transferInfo,
				relevantDatas);

		subflowTask.setTranferInfo(transferInfo);
		subflowTask.setSenderman(this.wfActContext.getCurrentTask()
				.getSenderman());
		subflowTask.setOperator(this.wfActContext.getCurrentTask()
				.getOperator());

		WFActivityContext subContext = new WFActivityContext(subflowTask);
		subContext.setParentWfProcessInstancePK(toActivityInstance
				.getWfProcessInstancePK());
		subContext.setParentActivityInstanceGUID(toActivityInstance
				.getActivityInstancePK());
		subContext.setParentSrcActivity(toActivityInstance.getActivity());
		WorkflowRunner runner = new WorkflowRunner(subContext);
		this.wfActContext.addSubflowContext(subContext);

		ActivityRunExt.tranferRevelantDataBetweenProcess(subContext);

		runner.execute();

		this.wfActContext.addExecuteResult(subContext.getExecuteResult());
		this.tmpOutputTaskList.addAll(runner.getOutputTaskList());
		this.inputTaskList.addAll(runner.getInputTaskList());
	}

	private void exchangeAssignInfo(String processDefPK, HashMap targetDatas,
			HashMap srcDatas) {
		Iterator keys = srcDatas.keySet().iterator();
		Object key = null;
		String strKey = null;
		while (keys.hasNext()) {
			key = keys.next();
			if ((key != null) && ((key instanceof String))) {
				strKey = (String) key;
				if ((!strKey.startsWith(processDefPK))
						&& (strKey.endsWith("Assign"))) {
					targetDatas.put(strKey, srcDatas.get(strKey));
				}
			}
		}
		keys = targetDatas.keySet().iterator();
		while (keys.hasNext()) {
			key = keys.next();
			if ((key != null) && ((key instanceof String))) {
				strKey = (String) key;
				if (strKey.endsWith("Assign")) {
					srcDatas.remove(strKey);
				}
			}
		}
	}

	private void executeRoute(ActivityInstance activityInstance) {
		WFTask task4Route = WfTaskManager.getInstance().createTask(
				this.wfActContext.getCurrentTask().getPk_org(),
				this.wfActContext.getWfProcessDef().getWorkflowType());
		task4Route.setWfProcessDefPK(activityInstance.getWfProcessDefPK());
		task4Route.setWfProcessInstancePK(activityInstance
				.getWfProcessInstancePK());
		task4Route.setActivityID(activityInstance.getActivityID());

		task4Route.setActivityInstancePK(activityInstance
				.getActivityInstancePK());
		task4Route.setInObject(activityInstance.getInObject());
		task4Route.setOutObject(activityInstance.getInObject());
		task4Route.setStatus(WfTaskOrInstanceStatus.Finished.getIntValue());
		task4Route.setTaskType(WfTaskType.Forward.getIntValue());
		copyBusiInfo(task4Route, this.wfActContext.getCurrentTask());
		task4Route.setSenderman(this.wfActContext.getCurrentTask()
				.getSenderman());
		if (this.wfActContext.getCurrentTask().getOperator() != null) {
			task4Route.setSenderman(this.wfActContext.getCurrentTask()
					.getOperator());
		} else {
			task4Route.setSenderman(this.wfActContext.getCurrentTask()
					.getSenderman());
			task4Route.setOperator(this.wfActContext.getCurrentTask()
					.getOperator());
		}
		this.inputTaskList.add(task4Route);
	}

	private void fillBackTaskContent(ActivityInstance toActivityInstance,
			ActivityInstance oldToActivityInstance) {
		boolean isSubmit2RejectTache = isSubmit2RejectTache();
		for (int i = 0; i < this.tmpOutputTaskList.size(); i++) {
			WFTask task = (WFTask) this.tmpOutputTaskList.get(i);
			task.setWfProcessDefPK(toActivityInstance.getWfProcessDefPK());
			task.setWfProcessInstancePK(toActivityInstance
					.getWfProcessInstancePK());
			task.setActivityInstancePK(toActivityInstance
					.getActivityInstancePK());

			task.setInObject(oldToActivityInstance.getOutObject());

			task.setOutObject(oldToActivityInstance.getOutObject());
			task.setStatus(WfTaskOrInstanceStatus.Started.getIntValue());
			if (this.wfActContext.getCurrentTask().getTaskType() == WfTaskType.Withdraw
					.getIntValue()) {
				task.setWithdrawGenerated(true);
			}
			copyBusiInfo(task, this.wfActContext.getCurrentTask());

			try {
				EngineService queryDMO = new EngineService();
				if (oldToActivityInstance.getActivity().getActivityType() != ActivityTypeEnum.Makebill
						.getIntValue()) {
					task.setOperator(queryDMO
							.queryOperatorsByActinstancePK(oldToActivityInstance
									.getActivityInstancePK()));
				}
			} catch (DbException ex) {
				Logger.error(ex.getMessage(), ex);
				throw new EngineException(NCLangResOnserver.getInstance()
						.getStrByID("pfworkflow", "UPPpfworkflow-000332")
						+ ex.getMessage());
			}
		}
	}

	private void fillTaskContent(ActivityInstance toActivityInstance) {
		for (int i = 0; i < this.tmpOutputTaskList.size(); i++) {
			WFTask task = (WFTask) this.tmpOutputTaskList.get(i);
			task.setWfProcessDefPK(toActivityInstance.getWfProcessDefPK());
			task.setWfProcessInstancePK(toActivityInstance
					.getWfProcessInstancePK());
			task.setActivityInstancePK(toActivityInstance
					.getActivityInstancePK());

			task.setInObject(this.wfActContext
					.getTransferTargetActivityInstance().getInObject());

			task.setOutObject(this.wfActContext
					.getTransferTargetActivityInstance().getOutObject());

			task.setStatus(WfTaskOrInstanceStatus.Started.getIntValue());

			if (this.wfActContext.getCurrentTask().isBackToFirstActivity()) {
				task.setNote(this.wfActContext.getCurrentTask().getNote());
			} else if ((this.wfActContext.getCurrentTask().getApproveResult() != null)
					&& (this.wfActContext.getCurrentTask().getApproveResult()
							.equals("N"))) {
				task.setNote(this.wfActContext.getCurrentTask().getNote());
			}
			copyBusiInfo(task, this.wfActContext.getCurrentTask());
		}
	}

	public Vector<WFTask> getOutputTaskList() {
		return this.outputTaskList;
	}

	public Vector<WFTask> getInputTaskList() {
		return this.inputTaskList;
	}

	public Vector<String> getInefficientActInstancePKs() {
		if (this.inefficientActInstancePKs == null) {
			this.inefficientActInstancePKs = new Vector();
		}
		return this.inefficientActInstancePKs;
	}

	private Vector<ActivityInstance> getIgnoreActInstancePKs() {
		if (this.ignoreActInstancePKs == null)
			this.ignoreActInstancePKs = new Vector();
		return this.ignoreActInstancePKs;
	}

	private void beforeActiveRunning(Activity toActivity,
			ActivityInstance instance) throws BusinessException {
		if (this.tmpOutputTaskList.size() > 0) {
			TaskTopicResolver.fillNotifyInfo(this.wfActContext,
					this.tmpOutputTaskList, true);
		}
		executePreScript(instance, toActivity);

		doGadget(toActivity, true);
	}

	private void afterActivityRunned(Activity activity,
			ActivityInstance instance) throws BusinessException {
		Logger.error("###WorkflowRunner afterActivityRunned 开始 "
				+ System.currentTimeMillis() + "ms");
		ActivityRunExt.beforeExecutePostScript(this.wfActContext);
		executePostScript();
		executeActivityPlugin(activity);

		doGadget(activity, false);
		Logger.error("###WorkflowRunner afterActivityRunned 结束 "
				+ System.currentTimeMillis() + "ms");
	}

	private void doGadget(Activity activity, boolean isBefore)
			throws BusinessException {
		Logger.error("###WorkflowRunner doGadget 开始 "
				+ System.currentTimeMillis() + "ms");
		Logger.debug(isBefore ? "活动启动前"
				: "活动结束时，执行其工作流组件=WorkflowRunner.doGadget() called");

		String strBilltype = this.wfActContext.getCurrentTask().getBillType();
		String bill1VersionPK = this.wfActContext.getCurrentTask()
				.getBillversionPK();
		PfParameterVO paraVO = ActionEnvironment.getInstance().getParaVo(
				bill1VersionPK);

		EngineService wfQuery = new EngineService();
		WorkflowgadgetVO foundGadgetVO = wfQuery.findGadget(activity,
				strBilltype);
		if (foundGadgetVO == null) {
			Logger.debug("该活动没有配置工作流组件，无需执行。活动名称=" + activity.getName());
			return;
		}

		String clzName = foundGadgetVO.getClassname();
		if (StringUtil.isEmptyWithTrim(clzName)) {
			throw new EngineException(
					NCLangRes4VoTransl.getNCLangRes()
							.getStrByID(
									"pfworkflow",
									"EngineService-0001",
									null,
									new String[] { foundGadgetVO
											.getPk_workflowgadget() }));
		}

		boolean isExitAct = this.wfActContext.getCurrentActivity()
				.isExitActivity();
		boolean isToFinish = false;
		if ((this.wfActContext.getParentWfProcessInstancePK() == null)
				&& (this.wfActContext.getParentActivityInstancePK() == null)
				&& (isExitAct)) {
			isToFinish = true;
		}

		Object obj = PfUtilTools.findBizImplOfBilltype(strBilltype,
				clzName.trim());
		if ((obj instanceof IWorkflowGadget)) {
			IWorkflowGadget gadgetObj = (IWorkflowGadget) obj;
			WfGadgetContext gc = new WfGadgetContext();
			gc.setGadgetVO(foundGadgetVO);
			gc.setBillEntity(paraVO.m_preValueVo == null ? paraVO.m_preValueVos
					: paraVO.m_preValueVo);
			gc.setProcessStatus(isToFinish ? WfTaskOrInstanceStatus.Finished
					: WfTaskOrInstanceStatus.Started);
			gc.setTaskStatus(WfTaskOrInstanceStatus
					.fromIntValue(this.wfActContext.getCurrentTask()
							.getStatus()));
			gc.setAutoActivity(activity.getActivityType() == ActivityTypeEnum.Auto
					.getIntValue());
			gc.setActivity(activity);
			gc.setUserObj(paraVO.m_userObj);
			gc.setPfParameterVO(paraVO.clone());

			if (isBefore) {
				gadgetObj.doBeforeActive(gc);
			} else
				gadgetObj.doAfterRunned(gc);
		} else {
			throw new PFBusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("pfworkflow", "EngineService-0002", null,
							new String[] { (String) obj }));
		}

		Logger.error("###WorkflowRunner doGadget 结束 "
				+ System.currentTimeMillis() + "ms");
	}

	private void executeActivityPlugin(Activity activity)
			throws BusinessException {
		WFTask task = this.wfActContext.getCurrentTask();

		new PFActivityMsgExecutor(this.wfActContext, activity, task).execute();
	}

	private void copyBusiInfo(WFTask targetTask, WFTask srcTask) {
		targetTask.setBillNO(srcTask.getBillNO());
		targetTask.setBillID(srcTask.getBillID());
		targetTask.setBillversionPK(srcTask.getBillversionPK());

		targetTask.setBillType(srcTask.getBillType());
		targetTask.setPk_org(srcTask.getPk_org());
		if (targetTask.getWorknoteVO() == null) {
			targetTask.setWorknoteVO(srcTask.getWorknoteVO());
		}
		targetTask.setApproveResult(srcTask.getApproveResult());
	}
}
