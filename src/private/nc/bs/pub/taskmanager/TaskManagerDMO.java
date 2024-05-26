package nc.bs.pub.taskmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.ITimeService;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pf.pub.PFRequestDataCacheProxy;
import nc.bs.pf.pub.PfDataCache;
import nc.bs.pf.pub.cache.CondStringKey;
import nc.bs.pf.pub.cache.ICacheDataQueryCallback;
import nc.bs.pf.pub.cache.IRequestDataCacheKey;
import nc.bs.pf.pub.cache.WFTaskCacheKey;
import nc.bs.pub.pf.IMessagePriorityCallback;
import nc.bs.pub.pf.PfMessageUtil;
import nc.bs.pub.pf.PfUtilTools;
import nc.bs.pub.taskmanager.workitem.IWorkitemDistributor;
import nc.bs.pub.workflownote.WorknoteManager;
import nc.bs.pub.workflowpsn.WFAgentTaskTransfer;
import nc.bs.pub.workflowpsn.WorkflowPersonDAO;
import nc.bs.wfengine.engine.ext.TaskTopicResolver;
import nc.itf.uap.ml.DataMultiLangAccessor;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.message.MsgContentCreatorInAppLayer.NcMessageInfo;
import nc.message.templet.itf.IMsgtempletquery;
import nc.message.templet.vo.MsgtempletVO;
import nc.message.templet.vo.MsgtmptypeVO;
import nc.message.vo.NCMessage;
import nc.uap.bd.util.BDInSqlUtil;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pf.pub.util.ArrayUtil;
import nc.vo.pf.sql.ParameterizedBatchCaller;
import nc.vo.pf.sql.ParameterizedBatchCaller.Callback;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype2.Billtype2VO;
import nc.vo.pub.billtype2.ExtendedClassEnum;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.pf.Pfi18nTools;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.wfengine.pub.WFTask;
import nc.vo.wfengine.pub.WFTaskMappingMeta;
import nc.vo.wfengine.pub.WfTaskOrInstanceStatus;
import nc.vo.wfengine.pub.WfTaskType;
import nc.vo.wfengine.pub.WorkitemMsgContext;
import uap.apppf.util.SQLTransferMeaningUtil;

/**
 * 任务处理数据持久类
 * 
 * @author wzhy
 * @modifier leijun 2005-10-14 适配到NC5
 * @author yanke1 2013-1-6 改用WFTaskMappingMeta进行WFTask的持久化
 */
public class TaskManagerDMO {
	
	private final static WFTaskMappingMeta mappingMeta = new WFTaskMappingMeta();

	public TaskManagerDMO() {}
	
	/**
	 * 获取流程中的前一个Task
	 * @param curTask
	 * @return
	 */
	public WFTask getPrevTask(WFTask curTask) throws DbException{
		if(curTask==null || curTask.getTaskPK()==null){
			return null;
		}
		String whereCond = "pk_wf_actinstance in ( "
				+ " select src_actinstance from pub_wf_actinstancesrc"
				+ " where target_actinstance in ("
				+ " select pk_wf_actinstance from pub_wf_task "
				+ " where pk_wf_task ='"+curTask.getTaskPK()+"'))";
		return queryTaskByCondition(whereCond);
	}
	
	public WFTask getNextTask(WFTask curTask) throws DbException{
		if(curTask==null || curTask.getTaskPK()==null){
			return null;
		}
		String whereCond = "pk_wf_actinstance in ( "
				+ " select target_actinstance from pub_wf_actinstancesrc"
				+ " where src_actinstance in ("
				+ " select pk_wf_actinstance from pub_wf_task "
				+ " where pk_wf_task ='"+curTask.getTaskPK()+"'))";
		return queryTaskByCondition(whereCond);
	}
	
	/**
	 * 根据PK从数据库中查找任务对象
	 * 
	 * @param taskPK
	 * @return WFTask
	 */
	public WFTask getTaskByPK(final String taskPK) throws DbException {
		PersistenceManager persist = null;
		try {
			persist = PersistenceManager.getInstance();
			WFTask task = (WFTask) persist.retrieveByPK(WFTask.class, mappingMeta, taskPK);
			
			return task;
		} finally {
			if (persist != null)
				persist.release();
		}

	}
	
	/**
	 * 根据多个PK从数据库中查找任务对象
	 * 
	 * @param taskpks
	 * @return List<WFTask>
	 */
	public List<WFTask> getTaskByPKs(List<String> taskpks) throws DbException {
		
		List<WFTask> tasks=new ArrayList<WFTask>();
		PersistenceManager persist = null;
		
		String condition= " pk_wf_task in "+BDInSqlUtil.getInSql(taskpks.toArray(new String[0]), false);;
		
		try {
			persist = PersistenceManager.getInstance();
			Collection<WFTask> taskCollection=persist.retrieveByClause(WFTask.class, mappingMeta, condition);
			if(taskCollection!=null){
				for (Iterator<WFTask> iterator = taskCollection.iterator(); iterator.hasNext();) {
					WFTask task = iterator.next();
					if(task!=null){
						tasks.add(task);
					}
				}
			}
			
			return tasks;
		} finally {
			if (persist != null)
				persist.release();
		}

	}

	/**
	 * 将活动实例对应的任务置为无效状态; 同时把任务产生的工作项也置为无效状态. XXX:不包括已经正常审批完成的工作项
	 * @modifier yanke1 2012-7-4 如果不包括已审批完成的工作项，那么a-b-a(auto)情况下，b弃审时将无法废弃a(auto)工作项，从而导致b弃审后a无法弃审
	 * leijun@2007-9-14
	 * 
	 * @param actInstPKs
	 *            活动实例的PK,格式'PK1','PK2',..
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public void inefficientTasksByActInstPKs(String actInstPKs)
			throws DbException, BusinessException {
		
		//
//		String sqlWorkflow = "approvestatus <>"
//				+ WfTaskOrInstanceStatus.Finished.getIntValue()
//				+ " and pk_wf_task in (select pk_wf_task from pub_wf_task where pk_wf_actinstance in ("
//				+ actInstPKs + "))";
		
		// yanke1 2012-7-4 如果不包括已审批完成的工作项，那么a-b-a(auto)情况下，b弃审时将无法废弃a(auto)工作项，从而导致b弃审后a无法弃审
		String sqlWorkflow = " pk_wf_task in (select pk_wf_task from pub_wf_task where pk_wf_actinstance in ("
				+ actInstPKs + "))";
		
		BaseDAO dao = new BaseDAO();
		Collection<WorkflownoteVO> colWorknote = dao.retrieveByClause(
				WorkflownoteVO.class, sqlWorkflow);
		
		inefficientWorkitems(colWorknote);

		String sql = "update pub_wf_task set taskstatus ="
				+ WfTaskOrInstanceStatus.Inefficient.getIntValue()
				+ " where pk_wf_actinstance in(" + actInstPKs + ")";

		PersistenceManager persist = null;
		try {
			persist = PersistenceManager.getInstance();
			JdbcSession jdbc = persist.getJdbcSession();
			jdbc.executeUpdate(sql);
		} finally {
			if (persist != null)
				persist.release();
		}
	}

	/**
	 * 将活动实例对应的任务置为终止状态; 同时把任务产生的工作项也置为终止状态.
	 * 
	 * @param actInstPKs
	 *            活动实例的PK,格式'PK1','PK2',..
	 */
	// public void terminateTasksByActInstPKs(String actInstPKs) throws
	// DbException {
	// String sqlWorkflow = "update pub_workflownote set
	// ischeck='X',approvestatus=" +
	// WfTaskOrInstanceStatus.Terminated.getIntValue()
	// + " where pk_wf_task in(select pk_wf_task from pub_wf_task where
	// pk_wf_actinstance in(" + actInstPKs + "))";
	// String sql = "update pub_wf_task set taskstatus =" +
	// WfTaskOrInstanceStatus.Terminated.getIntValue() + " where
	// pk_wf_actinstance in(" + actInstPKs + ")";
	//
	// PersistenceManager persist = null;
	// try {
	// persist = PersistenceManager.getInstance();
	// JdbcSession jdbc = persist.getJdbcSession();
	// jdbc.executeUpdate(sqlWorkflow);
	// jdbc.executeUpdate(sql);
	//
	// } finally {
	// if (persist != null)
	// persist.release();
	// }
	// }
	/**
	 * 更新某条工作项的审批批语
	 * 
	 * @param pkCheckflow
	 * @param note
	 * @throws DbException
	 */
	public void updateChecknote(String pkCheckflow, String note)
			throws DbException {
		String sqlWorkflow = "update pub_workflownote set checknote= ? where pk_checkflow=?";
		PersistenceManager persist = null;
		try {
			persist = PersistenceManager.getInstance();
			JdbcSession jdbc = persist.getJdbcSession();
			SQLParameter para = new SQLParameter();
			para.addParam(note);
			para.addParam(pkCheckflow);
			jdbc.executeUpdate(sqlWorkflow, para);
		} finally {
			if (persist != null)
				persist.release();
		}
	}
	
	
	private WorkflownoteVO createWorkitem(WFTask task, String cuserid) throws DbException {
		Logger.error("###TaskManagerDMO createWorkitem 开始");
		int iWfType = task.getWorkflowType();
		WorkitemMsgContext context = task.getContext().clone();
		WorkflownoteVO noteVO = new WorkflownoteVO();
		noteVO.setMsgContext(context);
		noteVO.setFuncode(context.getFuncode());
		noteVO.setPk_billtype(task.getBillType());
		noteVO.setBillno(task.getBillNO());
		//资金工作流执行经办组件后，发送人为null.这种情况取经办人，即当前登录系统人PK
		noteVO.setSenderman(StringUtil.isEmptyWithTrim(task.getSenderman())?InvocationInfoProxy.getInstance().getUserId():task.getSenderman());
		noteVO.setIscheck("N");
		noteVO.setSenddate(task.getCreateTime());
		
		//工作项中填充原处理人(单人模式下）
		if (task.getParticipantProcessMode() == WFTask.ProcessMode_Single_Together
				|| task.getParticipantProcessMode() == WFTask.ProcessMode_Single_Race) {
			if(task.getParticipantID().equals(context.getAgent())){
				//将WfAgentUtil的getAgentInfo_BS()
				if(!task.getParticipantID().equals(task.getApproveAgent())){
					noteVO.setAgencyuser(task.getApproveAgent());
				}
			}
			//实际找到代理还是自己（没有找到代理）
			if(task.getParticipantID().equals(task.getApproveAgent())){
				context.setAgent("");
			}
		}
		
		// 生成代理人信息
		String checkman = cuserid;
		// 根据checkman找其代理人
		// 返回值第0列为代理人pk，第1列为代理人名称 
		// 若checkman没有代理人，则返回空
		String[] agentInfo = getAgentInfo(task, checkman);
		if (agentInfo != null && !checkman.equals(agentInfo[0])) {
			//如果是多人处理的模式，将原checkman填入到noteVO中
			noteVO.setAgencyuser(checkman);
			
			checkman = agentInfo[0];
			String agentName = agentInfo[1];

			// 补完MsgitemMsgContext中的代理人信息
			context.setCheckman(checkman);
			context.setAgent(agentName);
		}
		
		context.setCheckman(checkman);

		// 根据WorkitemMsgContext生成NCMsg, 其中包含消息标题（若采用了消息模板，则还包括消息内容）
		NCMessage ncmsg = TaskTopicResolver.constructNCMsg(context);
		
		task.setTopic(ncmsg.getMessage().getSubject());

		// 将ncmsg传入noteVO，后续向消息中心发送代办项时会用到该NCMsg
		noteVO.setNcMsg(ncmsg);
		noteVO.setCheckman(checkman);
		if (task.getParticipantProcessMode() == WFTask.ProcessMode_Single_Together
				|| task.getParticipantProcessMode() == WFTask.ProcessMode_Single_Together) {
			noteVO.setObserver(task.getApproveAgent());
		} else if (!String.valueOf(checkman).equals(cuserid)) {
			noteVO.setObserver(cuserid);
		}
		
		// 为noteVO设置标题
		noteVO.setMessagenote(task.getTopic());

		noteVO.setReceivedeleteflag(UFBoolean.FALSE);
		noteVO.setPk_org(task.getPk_org());
		// TODO:需要查流程定义上的集团吗？
		noteVO.setPk_group(InvocationInfoProxy.getInstance().getGroupId());
		noteVO.setBillid(task.getBillID());
		noteVO.setBillVersionPK(task.getBillversionPK());
		noteVO.setIsmsgbind("N");
		noteVO.setPk_wf_task(task.getTaskPK());
		noteVO.setApprovestatus(task.getStatus());
		if (task.getTaskType() == WfTaskType.Makebill.getIntValue()) {
			noteVO.setActiontype(WorkflownoteVO.WORKITEM_TYPE_MAKEBILL);
		} else {
			noteVO.setActiontype(WorkflownoteVO.WORKITEM_TYPE_APPROVE);
		}
		// 工作流类型，子流程类型都作为主流程考虑

		noteVO.setWorkflow_type(iWfType);
		noteVO.setPriority(getMessagePriority(task.getBillType(), task.getBillversionPK()));
		Logger.error("###TaskManagerDMO createWorkitem 结束");
		return noteVO;
	}
	
	/**
	 * 重写createWorkitem方法（带有属性）
	 * @param task
	 * @param cuserid
	 * @param propertyMap
	 * @return
	 * @throws DbException
	 */
	private WorkflownoteVO createWorkitem(WFTask task, String cuserid,Map<String,UFBoolean> propertyMap,String langcode,MsgtempletVO[] msgTemVOs,HashMap<String,MsgtmptypeVO> msgtmptypeVOHash,HashMap<String,NcMessageInfo>  ncMessageInfoHash) throws DbException {
		Logger.error("###TaskManagerDMO createWorkitem有属性 开始");
		int iWfType = task.getWorkflowType();
		WorkitemMsgContext context = task.getContext().clone();
		WorkflownoteVO noteVO = new WorkflownoteVO();
		noteVO.setMsgContext(context);
		noteVO.setFuncode(context.getFuncode());
		noteVO.setPk_billtype(task.getBillType());
		noteVO.setBillno(task.getBillNO());
		//资金工作流执行经办组件后，发送人为null.这种情况取经办人，即当前登录系统人PK
		noteVO.setSenderman(StringUtil.isEmptyWithTrim(task.getSenderman())?InvocationInfoProxy.getInstance().getUserId():task.getSenderman());
		noteVO.setIscheck("N");
		noteVO.setSenddate(task.getCreateTime());
		
		//工作项中填充原处理人(单人模式下）
		if (task.getParticipantProcessMode() == WFTask.ProcessMode_Single_Together
				|| task.getParticipantProcessMode() == WFTask.ProcessMode_Single_Race) {
			if(task.getParticipantID().equals(context.getAgent())){
				//将WfAgentUtil的getAgentInfo_BS()
				if(!task.getParticipantID().equals(task.getApproveAgent())){
					noteVO.setAgencyuser(task.getApproveAgent());
				}
			}
			//实际找到代理还是自己（没有找到代理）
			if(task.getParticipantID().equals(task.getApproveAgent())){
				context.setAgent("");
			}
		}
		
		// 生成代理人信息
		String checkman = cuserid;
		// 根据checkman找其代理人
		// 返回值第0列为代理人pk，第1列为代理人名称 
		// 若checkman没有代理人，则返回空
		String[] agentInfo = getAgentInfo(task, checkman,propertyMap);
		if (agentInfo != null && !checkman.equals(agentInfo[0])) {
			//如果是多人处理的模式，将原checkman填入到noteVO中
			noteVO.setAgencyuser(checkman);
			
			checkman = agentInfo[0];
			String agentName = agentInfo[1];

			// 补完MsgitemMsgContext中的代理人信息
			context.setCheckman(checkman);
			context.setAgent(agentName);
		}
		
		context.setCheckman(checkman);

		// 根据WorkitemMsgContext生成NCMsg, 其中包含消息标题（若采用了消息模板，则还包括消息内容）
		NCMessage ncmsg = TaskTopicResolver.constructNCMsg(context,langcode,msgTemVOs,msgtmptypeVOHash,ncMessageInfoHash);
		
		task.setTopic(ncmsg.getMessage().getSubject());

		// 将ncmsg传入noteVO，后续向消息中心发送代办项时会用到该NCMsg
		noteVO.setNcMsg(ncmsg);
		noteVO.setCheckman(checkman);
		if (task.getParticipantProcessMode() == WFTask.ProcessMode_Single_Together
				|| task.getParticipantProcessMode() == WFTask.ProcessMode_Single_Together) {
			noteVO.setObserver(task.getApproveAgent());
		} else if (!String.valueOf(checkman).equals(cuserid)) {
			noteVO.setObserver(cuserid);
		}
		
		// 为noteVO设置标题
		noteVO.setMessagenote(task.getTopic());

		noteVO.setReceivedeleteflag(UFBoolean.FALSE);
		noteVO.setPk_org(task.getPk_org());
		// TODO:需要查流程定义上的集团吗？
		noteVO.setPk_group(InvocationInfoProxy.getInstance().getGroupId());
		noteVO.setBillid(task.getBillID());
		noteVO.setBillVersionPK(task.getBillversionPK());
		noteVO.setIsmsgbind("N");
		noteVO.setPk_wf_task(task.getTaskPK());
		noteVO.setApprovestatus(task.getStatus());
		if (task.getTaskType() == WfTaskType.Makebill.getIntValue()) {
			noteVO.setActiontype(WorkflownoteVO.WORKITEM_TYPE_MAKEBILL);
		} else {
			noteVO.setActiontype(WorkflownoteVO.WORKITEM_TYPE_APPROVE);
		}
		// 工作流类型，子流程类型都作为主流程考虑

		noteVO.setWorkflow_type(iWfType);
		noteVO.setPriority(getMessagePriority(task.getBillType(), task.getBillversionPK()));
		Logger.error("###TaskManagerDMO createWorkitem有属性 结束");
		return noteVO;
	}
	
	
	/**
	 * 获取具体单据的消息优先级
	 * @param billtype
	 * @param billVersionPK
	 * @return
	 * @throws BusinessException 
	 */
	private Integer getMessagePriority(final String billtype, final String billVersionPK) {
		IRequestDataCacheKey key = new CondStringKey("taskmanagerdmo_get_message_priority",
				new String[] {
					billtype,
					billVersionPK
				});
		ICacheDataQueryCallback<Integer> callback = new ICacheDataQueryCallback<Integer>() {
			@Override
			public Integer queryData() throws BusinessException {
				List<Billtype2VO> b2voList = PfDataCache.getBillType2Info(billtype, ExtendedClassEnum.MESSAGE_PRIORITY_CALLBACK.getIntValue());
				
				if (ArrayUtil.isNotNull(b2voList)) {
					
					Billtype2VO b2vo = b2voList.get(0);

					String className = b2vo.getClassname();
					String realBillType = b2vo.getPk_billtype();

					try {
						IMessagePriorityCallback cb = (IMessagePriorityCallback) PfUtilTools.instantizeObject(realBillType, className);
						return cb.getMessagePriority(billVersionPK);
					} catch (Exception e) {
						// 不应影响流程进行
						Logger.error(e.getMessage(), e);
					}
				}

				return null;
			}
		};
		
		try {
			return PFRequestDataCacheProxy.get(key, callback);
		} catch (Exception e) {
			return null;
		}
	}
	

	/**
	 * 为多人产生工作项
	 * 
	 * @param userIds
	 *            操作员PK数组
	 * @param task
	 *            任务对象
	 * @throws Exception
	 */
	public void insertWorkitemsOfTask(String[] userIds, WFTask task, IWorkitemDistributor... dists)
			throws Exception {
		Logger.error("###TaskManagerDMO insertWorkitemsOfTask 开始");
		Logger.error("进入insertWorkitemsOfTask billno="+task.getBillNO());
		// 在线程缓存里放置一下task 后面生成MessageMeta时会用到
		PFRequestDataCacheProxy.put(new WFTaskCacheKey(task.getTaskPK()), task);
		
		ArrayList<WorkflownoteVO> al = new ArrayList<WorkflownoteVO>();
		
		//批量获取外出用户的ISUSEROUT和ISTRANSWORK属性
		WorkflowPersonDAO wpDao = new WorkflowPersonDAO();
		HashMap<String,Map<String,UFBoolean>> userMap=wpDao.isUserOutAndTransWorks(userIds);
		//批量获取用户的langcode
		HashMap<String,String> userLangCode=Pfi18nTools.getLangcodesOfUserFromDb(userIds);
		
		WorkitemMsgContext context = task.getContext();
		String tempcode = context.getMsgtempcode();
		String pk_org = InvocationInfoProxy.getInstance().getGroupId();
		String[] langcodeary = userLangCode.values().toArray(new String[0]);
		List<String> langcodeList=new ArrayList<String>();
		if(langcodeary==null||langcodeary.length==0){
			String langcode=DataMultiLangAccessor.getInstance().getDefaultLang().getLangcode();
			langcodeary=new String[]{langcode};
		}
		else{
			for(String langcode:langcodeary){
				if(!langcodeList.contains(langcode)){
					langcodeList.add(langcode);
				}
			}
		}
		//批量获取langcodeary对应的MsgtempletVO[]
		MsgtempletVO[] msgTemVOs=NCLocator.getInstance().lookup(IMsgtempletquery.class).qryTempletsByCodeLangPk_org(tempcode, pk_org, langcodeList.toArray(new String[0]));
		HashMap<String,List<MsgtempletVO>> msgTemVOHash=new HashMap<String,List<MsgtempletVO>>();
		List<String> typecodes=new ArrayList<String>();
		
		if(msgTemVOs!=null&&msgTemVOs.length>0){
			for(MsgtempletVO vo:msgTemVOs){
				String langcode=vo.getLangcode();
				List<MsgtempletVO> msgTemVOList= msgTemVOHash.get(langcode);
				if(msgTemVOList==null){
					msgTemVOList=new ArrayList<MsgtempletVO>();
					msgTemVOHash.put(langcode, msgTemVOList);
				}
				msgTemVOList.add(vo);
				typecodes.add(SQLTransferMeaningUtil.tmsql(vo.getTypecode()));
			}
		}
		//批量获取MsgtempletVO对应的MsgtmptypeVO
		HashMap<String,MsgtmptypeVO> msgtmptypeVOHash=new HashMap<String,MsgtmptypeVO>();
		
		if(typecodes.size()>0){
			try
			{
				IMsgtempletquery query = NCLocator.getInstance().lookup(IMsgtempletquery.class);
				MsgtmptypeVO[] msgTemTypeVOs= query.getTemptypeVOByCodes(typecodes.toArray(new String[0]));
				if(msgTemTypeVOs!=null&&msgTemTypeVOs.length>0){
					for(MsgtmptypeVO vo:msgTemTypeVOs){
						String votempcode=vo.getTempcode();
						msgtmptypeVOHash.put(votempcode, vo);
					}
				}
			} catch (BusinessException e)
			{
				Logger.error(e.getMessage(), e);
			}
		}
		HashMap<String,NcMessageInfo>  ncMessageInfoHash =new HashMap<String,NcMessageInfo>();
		for (int i = 0; i < userIds.length; i++) {
			
			Map<String,UFBoolean> propertyMap=userMap.get(userIds[i]);
			if(propertyMap==null){
				propertyMap= new HashMap<String,UFBoolean>();
			}
			String langcode=userLangCode.get(userIds[i]);
			if(langcode==null||langcode.equalsIgnoreCase("")){
				langcode=DataMultiLangAccessor.getInstance().getDefaultLang().getLangcode();
			}
			List<MsgtempletVO> msgTemVOList=msgTemVOHash.get(langcode);
			if(msgTemVOList==null){
				msgTemVOList=new ArrayList<MsgtempletVO>();
			}
			WorkflownoteVO noteVO = createWorkitem(task, userIds[i],propertyMap,langcode,msgTemVOList.toArray(new MsgtempletVO[0]),msgtmptypeVOHash,ncMessageInfoHash);
			//WorkflownoteVO noteVO = createWorkitem(task, userIds[i]);
			al.add(noteVO);
		}
		ncMessageInfoHash.clear();
		ncMessageInfoHash=null;
		WorkflownoteVO[] notes = al.toArray(new WorkflownoteVO[0]);
		
		new BaseDAO().insertVOArray(notes);
		PfMessageUtil.sendMessageOfWorknoteBatch(notes);
		
		if (!ArrayUtil.isNull(dists)) {
			for (IWorkitemDistributor d : dists) {
				d.distributeWorkitem(notes);
			}
		}
		Logger.error("###TaskManagerDMO insertWorkitemsOfTask 结束");
	}
	
	private String[] getAgentInfo(WFTask task, String checkman)
			throws DbException {
		if (task.getParticipantProcessMode() == WFTask.ProcessMode_Single_Together
				|| task.getParticipantProcessMode() == WFTask.ProcessMode_Single_Race) {
			// 参与者为操作员的活动由于已经设置了代理人信息，这里不再设置，见TaskTopicResolver.constructTopic()方法

			return null;
		} else {
			// 为参与者非操作员的活动设置代理人信息
			// 获取动态代理人+代理信息串
			String[] agentInfos = WFAgentTaskTransfer.getAgentInfosAndTransOldTask(checkman, task.getBillType());
			return agentInfos;
		}
	}
	
	/**
	 * 重写getAgentInfo(带有属性)
	 * @param task
	 * @param checkman
	 * @param propertyMap
	 * @return
	 * @throws DbException
	 */
	private String[] getAgentInfo(WFTask task, String checkman,Map<String,UFBoolean> propertyMap)
			throws DbException {
		if (task.getParticipantProcessMode() == WFTask.ProcessMode_Single_Together
				|| task.getParticipantProcessMode() == WFTask.ProcessMode_Single_Race) {
			// 参与者为操作员的活动由于已经设置了代理人信息，这里不再设置，见TaskTopicResolver.constructTopic()方法

			return null;
		} else {
			// 为参与者非操作员的活动设置代理人信息
			// 获取动态代理人+代理信息串
			String[] agentInfos = WFAgentTaskTransfer.getAgentInfosAndTransOldTask(checkman, task.getBillType(),propertyMap);
			return agentInfos;
		}
	}

//	@SuppressWarnings("unused")
//	private String resolveTopicAndReturnCheckman(WFTask task, String checkman)
//			throws DbException {
//		if (task.getParticipantProcessMode() == WFTask.ProcessMode_Single_Together
//				|| task.getParticipantProcessMode() == WFTask.ProcessMode_Single_Race) {
//			// 参与者为操作员的活动由于已经设置了代理人信息，这里不再设置，见TaskTopicResolver.constructTopic()方法
//			/*
//			 * @modifier yanke1 2011-3-18 将工作任务标题替换为消息模板
//			 */
//			// task.setTopic(linkTaskTopic(task.getTopicList()));
//		} else {
//			// 为参与者非操作员的活动设置代理人信息
//			// 获取动态代理人+代理信息串
//			String[] agentInfos = TaskTopicResolver
//					.queryDynamicAgentOfCheckman(checkman, task.getBillType());
//			checkman = agentInfos[0];
//			String strAgentDesc = agentInfos[1];
//
//			task.getContext().setAgent(strAgentDesc);
//			/*
//			 * @modifier yanke1 2011-3-18 将工作任务标题替换为消息模板
//			 */
//			// ArrayList<WorkitemconfigVO> wiConfig = task.getTopicList();
//			// for (WorkitemconfigVO wcVO : wiConfig) {
//			// if (wcVO.getItemtype() == WorkitemconfigVO.ITEM_TYPE_APPROVEINFO
//			// && wcVO.getItem().equals(WorkitemApproveInfo.TAG_AGENT)) {
//			// wcVO.setItemValue(strAgentDesc);
//			// }
//			// }
//			// task.setTopic(linkTaskTopic(wiConfig));
//		}
//
//		NCMessage ncmsg = TaskTopicResolver.constructNCMsg(task.getContext());
//		task.setTopic(ncmsg.getMessage().getSubject());
//		return checkman;
//	}

	/**
	 * 根据任务对象来更新某个工作项
	 * 
	 * @param task
	 *            任务对象
	 * @throws DAOException
	 */
	public void updateWorkitemByTask(WFTask task) throws DAOException {
		// String UpdateSql = "update pub_workflownote set pk_billtype =?,
		// billno=?, senderman=?, checkman=?," + "ischeck=?, checknote=?,
		// senddate=?, dealdate=?, messagenote=?,"
		// + " pk_org=?, billid=?, " +
		// "priority=?,pk_wf_task=?,approvestatus=?,approveresult=? where
		// pk_checkflow=?";

		BaseDAO dao = new BaseDAO();
		WorkflownoteVO worknote = (WorkflownoteVO) dao.retrieveByPK(
				WorkflownoteVO.class, task.getWorknoteVO().getPk_checkflow());
		if (worknote != null) {
			worknote.setPk_billtype(task.getBillType());
			worknote.setBillno(task.getBillNO());
			worknote.setBillid(task.getBillID());
			worknote.setBillVersionPK(task.getBillversionPK());
			worknote.setSenderman(task.getWorknoteVO().getSenderman());
			worknote.setCheckman(task.getOperator());
			worknote.setCiphertext(task.getWorknoteVO().getCiphertext());
			String newIscheck = null;
			if (task.getTaskType() == WfTaskType.Makebill.getIntValue()
					|| task.getApproveResult() == null) {
				if (task.getStatus() == WfTaskOrInstanceStatus.Finished
						.getIntValue()) {
					newIscheck = "Y";
				} else {
					newIscheck = "N";
				}
			} else {
				if (task.getApproveResult().equals("Y")) {
					newIscheck = "Y";
				} else {
					newIscheck = "X";
				}
			}
			worknote.setIscheck(newIscheck);
			worknote.setChecknote(task.getWorknoteVO().getChecknote());
			//worknote.setSenddate(NCLocator.getInstance().lookup(ITimeService.class).getUFDateTime());
			worknote.setDealdate(task.getModifyTime());
			worknote.setMessagenote(task.getTopic());
			worknote.setPk_org(task.getPk_org());
			worknote.setPriority(0);// priority
			worknote.setPk_wf_task(task.getTaskPK());
			worknote.setApprovestatus(task.getStatus());
			worknote.setApproveresult(task.getApproveResult());
		}
		dao.updateVO(worknote);
	}

	/**
	 * 使某个工作项无效
	 * 
	 * @param pk_checkflow
	 *            工作项PK
	 * @throws BusinessException
	 */
	public void inefficientWorkitemByPK(String pk_checkflow)
			throws BusinessException {
		// 使无效 本工作项
		BaseDAO dao = new BaseDAO();
		WorkflownoteVO worknote = (WorkflownoteVO) dao.retrieveByPK(
				WorkflownoteVO.class, pk_checkflow);
		Collection<WorkflownoteVO> col = new ArrayList<WorkflownoteVO>();
		col.add(worknote);
		
		inefficientWorkitems(col);
		
//		inefficientWorkitems(col, "pk_detail='" + pk_checkflow + "'");
	}

	/**
	 * 更新某任务产生的所有工作项 状态为无效;
	 * 
	 * @param task
	 *            该工作项所属的任务对象
	 * @throws BusinessException
	 * @modifier leijun 2005-5 增加对approveresult字段的更新
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void inefficientWorkitemsOfTask(WFTask task)
			throws BusinessException {
		String sqlCond = "pk_wf_task=?";
		
		SQLParameter param = new SQLParameter();
		param.addParam(task.getTaskPK());
		
		BaseDAO dao = new BaseDAO();
		Collection<WorkflownoteVO> colWorknote = dao.retrieveByClause(WorkflownoteVO.class, sqlCond, param);
		
		if (ArrayUtil.isNull(colWorknote)) {
			return;
		}
		
		for (Iterator iterator = colWorknote.iterator(); iterator.hasNext();) {
			WorkflownoteVO workflownoteVO = (WorkflownoteVO) iterator.next();
			workflownoteVO.setIscheck("X");
			workflownoteVO.setApprovestatus(WfTaskOrInstanceStatus.Inefficient
					.getIntValue());
			if (workflownoteVO.getPk_checkflow().equals(
					task.getWorknoteVO().getPk_checkflow())) {
				// 本工作项
				workflownoteVO.setDealdate(task.getModifyTime());
				workflownoteVO.setDealtimemillis(String.valueOf(NCLocator.getInstance().lookup(ITimeService.class).getTime()));
				workflownoteVO.setApproveresult(task.getApproveResult());
			}
		}
		WorkflownoteVO[] aryWorknote = colWorknote
				.toArray(new WorkflownoteVO[0]);
		dao.updateVOArray(aryWorknote);

		PfMessageUtil.deleteMessagesOfWorknote(aryWorknote);

	}
	
	
	/**
	 * 插入/更新 一个任务对象到数据库表pub_wf_task
	 * 
	 * @param task
	 * @param isInsert
	 *            ture插入记录;false更新记录
	 */
	public void saveOrUpdateTask(WFTask task, boolean isInsert)
			throws DbException {
		PersistenceManager persist = null;
		try {
			persist = PersistenceManager.getInstance();
			
			if (isInsert) {
				persist.insertObject(task, mappingMeta);
			} else {
				persist.updateObject(task, mappingMeta);
			}
		} finally {
			if (persist != null)
				persist.release();
		}
	}
	
	

	/**
	 * 通过查询工作项表pub_workflownote来判断某任务是否已经完成
	 * 所有人已审批才算完成
	 * 
	 * 加签不算在内
	 * 
	 * @param taskPK
	 *            任务主键
	 * @throws DbException
	 */
	public boolean isTaskComplete(String taskPK) throws DbException {
		int checked = countCheckedWorkitemsOfTask(taskPK);
		int all = countAllWorkitemsOfTask(taskPK);
		
		return checked >= all;
	}
	
	/**
	 * 使用WFTask中记录的完成阈值来判断task是否完成。
	 * @param task
	 * @return
	 * @throws DbException
	 */
	public boolean isTaskCompleteByWFTask(WFTask task) throws DbException {
		String pk_wf_task = task.getTaskPK();
		String finishThreshold = task.getParticipantProcessModeValue();
		
		if (StringUtil.isEmptyWithTrim(finishThreshold)) {
			finishThreshold = "100%";
		}
		
		if (finishThreshold.endsWith("%")) {
			return isTaskCompleteWithPercentModal(pk_wf_task, finishThreshold);
		} else {
			return isTaskCompleteWithCountModal(pk_wf_task, finishThreshold);
		}
	}
	
	/**
	 * 通过查询工作项表pub_workflownote来判断某任务是否已经完成, 两种情况算完成 <li>1.有审批不通过的 <li>2.符合会签策略的
	 * 
	 * @param taskPK
	 *            任务主键
	 * @throws DbException
	 */
	public boolean isTaskCompleteWithCountModal(String taskPK, String countOrPercentValue) throws DbException {
		// 所有工作项
		int allNote = countAllWorkitemsOfTask(taskPK);

		// 已经完成的工作项
		int finishedCount = countCheckedWorkitemsOfTask(taskPK);

		int iCountOrPercentValue = Integer.valueOf(countOrPercentValue);

		if (iCountOrPercentValue > allNote) {
			iCountOrPercentValue = allNote;
		}
		if (finishedCount >= iCountOrPercentValue) {
			return true;
		}
		return false;
	}
	
	private int countBySql(String sql, String pk_wf_task) throws DbException {
		PersistenceManager persist = null;
		try {
			persist = PersistenceManager.getInstance();
			JdbcSession jdbc = persist.getJdbcSession();
			
			SQLParameter parameter = new SQLParameter();
			parameter.addParam(pk_wf_task);
			
			ResultSetProcessor processor = new ColumnProcessor();
			
			Integer count = (Integer) jdbc.executeQuery(sql, parameter, processor);
			return count;
		} finally {
			if (persist != null)
				persist.release();
		}
	}
	
	public int countPassedWorkitemsOfTask(String pk_wf_task) throws DbException {
		// 审批结果为"通过"的工作项
		String passedSql = "select count(*) from pub_workflownote where pk_wf_task=? and approvestatus="
			+ WfTaskOrInstanceStatus.Finished.getIntValue() + " and approveresult='Y' and actiontype not like '%"
			+ WorkflownoteVO.WORKITEM_ADDAPPROVER_SUFFIX + "'";
		
		return countBySql(passedSql, pk_wf_task);
	}
	
	public int countCheckedWorkitemsOfTask(String pk_wf_task) throws DbException {
		// 已完成的工作项
		String checkedSql = "select count(*) from pub_workflownote where pk_wf_task=? and approvestatus="
				+ WfTaskOrInstanceStatus.Finished.getIntValue() + " and actiontype not like '%"
				+ WorkflownoteVO.WORKITEM_ADDAPPROVER_SUFFIX + "'";
		
		return countBySql(checkedSql, pk_wf_task);
	}
	
	public int countAllWorkitemsOfTask(String pk_wf_task) throws DbException {
		// task关联的所有工作项，不计入已废弃的工作项
		String allSql = "select count(*) from pub_workflownote where pk_wf_task=? and approvestatus not in ("
				+ WfTaskOrInstanceStatus.Inefficient.getIntValue() + ") and actiontype not like '%"
				+ WorkflownoteVO.WORKITEM_ADDAPPROVER_SUFFIX + "'";
		
		return countBySql(allSql, pk_wf_task);
	}
	
	/**
	 * 会签情况下，判断某WFTask是否已完成
	 * 通过查询工作项表pub_workflownote来判断某任务是否已经完成
	 * 
	 * 完成情况：已审批工作项数目或比例达到Activity上定义的participantProcessModelValue
	 * 否则为未完成
	 * 
	 * @param taskPK
	 *            任务主键
	 * @throws DbException
	 * 
	 * @modifier yanke1 2012-2-24 不再支持一票否决 而是按照完成阈值来计算是否完成
	 * 							 另外，修复“统计时将弃审后废弃掉的工作项也计算在内”的bug
	 */
	public boolean isTaskCompleteWithPercentModal(String taskPK,
			String countOrPercentValue) throws DbException {
		int allNote = countAllWorkitemsOfTask(taskPK);
		int checkedCount = countCheckedWorkitemsOfTask(taskPK);

		double finishingThreshold = Double.parseDouble(countOrPercentValue.substring(0,
				countOrPercentValue.length() - 1)) * allNote / 100;

		if (finishingThreshold > allNote) {
			finishingThreshold = allNote;
		}
		if (checkedCount >= finishingThreshold) {
			return true;
		}
		return false;
	}
	
	public String calculateTaskResult(WFTask task) throws DbException {
		String pk_wf_task = task.getTaskPK();
		String finishThreshold = task.getParticipantProcessModeValue();
		String passingThreshold = task.getParticipantProcessPassingThreshold();
		
		if (isTaskPassed(pk_wf_task, finishThreshold, passingThreshold)) {
			return "Y";
		} else {
			return "N";
		}
	}
	
	public boolean isTaskPassed(String pk_wf_task, String finishThreshold, String passingThreshold) throws DbException {
		int all = countAllWorkitemsOfTask(pk_wf_task);
		int passed = countPassedWorkitemsOfTask(pk_wf_task);
		
		if (StringUtil.isEmptyWithTrim(passingThreshold)) {
			if (StringUtil.isEmptyWithTrim(finishThreshold)) {
				passingThreshold = "100%";
			} else {
				passingThreshold = finishThreshold;
			}
		}
		
		if (passingThreshold.endsWith("%")) {
			// 百分比
			double passingCount = Double.valueOf(passingThreshold.substring(0, passingThreshold.length() - 1)) * all / 100;
			
			if (passingCount > all) {
				passingCount = all;
			}
			
			return passed >= passingCount;
			
		} else {
			// 个数
			int passingCount = Integer.valueOf(passingThreshold);
			if (passingCount > all) {
				passingCount = all;
			}
			
			return passed >= passingCount;
		}
	}

	/**
	 * 查询某任务产生的工作项的个数
	 * 
	 * @param taskPK
	 *            任务主键
	 * @return
	 * @throws DbException
	 */
	public int queryWorkitemCountOfTask(String taskPK) throws DbException {
		String sqlPass = "select count(pk_checkflow) count from pub_workflownote where pk_wf_task = ?";
		int count = 0;
		PersistenceManager persist = null;
		try {
			persist = PersistenceManager.getInstance();
			JdbcSession jdbc = persist.getJdbcSession();
			SQLParameter para = new SQLParameter();
			para.addParam(taskPK);
			Object objRet = jdbc.executeQuery(sqlPass, para,
					new ColumnProcessor(1));
			if (objRet != null)
				count = ((Integer) objRet).intValue();

			return count;
		} finally {
			if (persist != null)
				persist.release();
		}
	}

	/**
	 * 查询某任务产生的已完成的工作项
	 * 
	 * @param taskPK
	 *            任务主键
	 * @return 第一条符合条件的工作项主键
	 * @throws DbException
	 */
	public String queryCompletedWorkitemOfTask(String taskPK)
			throws DbException {
		String sqlPass = "select pk_checkflow from pub_workflownote where pk_wf_task = ? and approvestatus = "
				+ WfTaskOrInstanceStatus.Finished.getIntValue()
				+ " and actiontype not like '%"
				+ WorkflownoteVO.WORKITEM_ADDAPPROVER_SUFFIX + "'";
		PersistenceManager persist = null;
		try {
			persist = PersistenceManager.getInstance();
			JdbcSession jdbc = persist.getJdbcSession();
			SQLParameter para = new SQLParameter();
			para.addParam(taskPK);
			Object obj = jdbc.executeQuery(sqlPass, para,
					new ColumnProcessor(1));
			return obj == null ? null : String.valueOf(obj);
		} finally {
			if (persist != null)
				persist.release();
		}
	}

	/**
	 * 删除某任务产生的所有工作项,除了指定的不删除
	 * 
	 * @param pk_wf_task
	 *            任务主键
	 * @param pk_checkflow
	 *            不删除的工作项主键
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public void deleteOtherWorkitemsExclude(String pk_wf_task,
			String pk_checkflow) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		String whereCondition = "pk_checkflow!= ? and pk_wf_task = ? and actiontype<>'"
				+ WorkflownoteVO.WORKITEM_TYPE_BIZ
				+ "' and approvestatus in("
				+ WfTaskOrInstanceStatus.getUnfinishedStatusSet()
				+ ")";
		SQLParameter para = new SQLParameter();
		para.addParam(pk_checkflow);
		para.addParam(pk_wf_task);
		Collection<WorkflownoteVO> colWorknotevo = dao.retrieveByClause(
				WorkflownoteVO.class, whereCondition, para);
		if (colWorknotevo == null || colWorknotevo.size() == 0)
			return;

		WorkflownoteVO[] aryWorknote = colWorknotevo
				.toArray(new WorkflownoteVO[0]);
		
		WorknoteManager.deleteWorknoteBatch(aryWorknote);
		
//		WorknoteManager.deleteWorknoteBatch(aryWorknote, 
//				"pk_detail in (select pk_checkflow from pub_workflownote where pk_checkflow!= '"+
//				pk_checkflow +"' and pk_wf_task = '"+ pk_wf_task+"')");
		// PfMessageUtil.deleteOtherWorkitemsExclude(pk_wf_task, pk_checkflow);
	}
	
	/**
	 * 加签抢占的情况下，删除工作项，不包括加签产生的已经完成的工作项
	 * add by liangyub 2013-08-10
	 * */
	@SuppressWarnings("unchecked")
	public void deleteOtherWorkitemsExceptAddAssign(String pk_wf_task,
			String pk_checkflow) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		String whereCondition = "pk_checkflow!= ? and pk_wf_task = ? and actiontype<>'"
				+ WorkflownoteVO.WORKITEM_TYPE_BIZ
				+ "' and approvestatus in("
				+ WfTaskOrInstanceStatus.getUnfinishedStatusSet()
				+ ")";
		SQLParameter para = new SQLParameter();
		para.addParam(pk_checkflow);
		para.addParam(pk_wf_task);
		Collection<WorkflownoteVO> colWorknotevo = dao.retrieveByClause(
				WorkflownoteVO.class, whereCondition, para);
		if (colWorknotevo == null || colWorknotevo.size() == 0)
			return;
		WorkflownoteVO[] aryWorknote = colWorknotevo
				.toArray(new WorkflownoteVO[0]);
		//过滤已经完成的加签产生的工作项
		List<WorkflownoteVO> filterNoteVOs = new ArrayList<WorkflownoteVO>();
		for(WorkflownoteVO vo : aryWorknote){
			if(!(vo.getActiontype().endsWith(
					WorkflownoteVO.WORKITEM_ADDAPPROVER_SUFFIX) 
					&& WfTaskOrInstanceStatus.Finished.getIntValue() == vo.getApprovestatus())){
				filterNoteVOs.add(vo);
			}
		}
		WorknoteManager.deleteWorknoteBatch(filterNoteVOs.toArray(new WorkflownoteVO[0]));
	}

	/**
	 * 更新某任务的尚未处理的工作项为无效
	 * 
	 * @param pk_wf_task
	 *            任务主键
	 * @throws DbException
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public void inefficientUncompletedWorkitemOfTask(String pk_wf_task)
			throws DbException, BusinessException {
		
		String sqlNoPass = "pk_wf_task=?"
				+ " and approvestatus in ("
				+ WfTaskOrInstanceStatus.getUnfinishedStatusSet() + ")";
		
		SQLParameter param = new SQLParameter();
		param.addParam(pk_wf_task);

		BaseDAO dao = new BaseDAO();
		Collection<WorkflownoteVO> colWorknote = dao.retrieveByClause(
				WorkflownoteVO.class, sqlNoPass, param);
		
		inefficientWorkitems(colWorknote);
	}
	
	private void inefficientWorkitems(Collection<WorkflownoteVO> colWorknote) throws BusinessException {
		if (colWorknote == null || colWorknote.size() == 0)
			return;

		for (Iterator<WorkflownoteVO> iterator = colWorknote.iterator(); iterator.hasNext();) {
			WorkflownoteVO workflownoteVO = iterator.next();
			workflownoteVO.setIscheck("X");
			workflownoteVO.setApprovestatus(WfTaskOrInstanceStatus.Inefficient.getIntValue());
		}
		WorkflownoteVO[] aryWorknote = colWorknote.toArray(new WorkflownoteVO[0]);
		new BaseDAO().updateVOArray(aryWorknote);

		PfMessageUtil.deleteMessagesOfWorknote(aryWorknote);
	}

	/**
	 * 根据已办工作项，重新产生一个新工作项 <li>用于弃审或回退
	 * 
	 * @param worknoteVO
	 * @throws BusinessException
	 */
	public void renewWorkitem(WFTask task)
			throws BusinessException {
		WorkflownoteVO worknoteVO = task.getWorknoteVO();
		WorkflownoteVO noteVO = new WorkflownoteVO();
		noteVO.setPk_billtype(worknoteVO.getPk_billtype());
		noteVO.setBillno(worknoteVO.getBillno());
		noteVO.setSenderman(worknoteVO.getSenderman());

		noteVO.setIscheck("N");
		noteVO.setSenddate(NCLocator.getInstance().lookup(ITimeService.class).getUFDateTime());
		noteVO.setCheckman(worknoteVO.getCheckman());
		noteVO.setMessagenote(worknoteVO.getMessagenote());
		noteVO.setReceivedeleteflag(UFBoolean.FALSE);
		noteVO.setPk_org(worknoteVO.getPk_org());
		noteVO.setPk_group(worknoteVO.getPk_group());
		noteVO.setBillid(worknoteVO.getBillid());
		noteVO.setBillVersionPK(worknoteVO.getBillVersionPK());
		noteVO.setPk_wf_task(worknoteVO.getPk_wf_task());
		noteVO.setApprovestatus(WfTaskOrInstanceStatus.Started.getIntValue());
		noteVO.setActiontype(worknoteVO.getActiontype());
		noteVO.setWorkflow_type(worknoteVO.getWorkflow_type());
		noteVO.setIsmsgbind(worknoteVO.getIsmsgbind());
		
		constructNCMsg(task, noteVO);
		
		WorknoteManager.insertWorknote(noteVO);
	}
	
	private void constructNCMsg(WFTask task,WorkflownoteVO noteVO ) throws BusinessException {
		WorkitemMsgContext context = TaskTopicResolver.getMsgContext(task);
		NCMessage ncMsg =  TaskTopicResolver.constructNCMsg(context);
		noteVO.setNcMsg(ncMsg);
	}
	
	@SuppressWarnings("unchecked")
	public WorkflownoteVO[] queryStartedWorkitemsOfTask(String taskPk)
			throws DAOException {
		
		String cond = "pk_wf_task=?" 
			+ " and approvestatus=" 
			+ WfTaskOrInstanceStatus.Started.getIntValue();
		SQLParameter param = new SQLParameter();
		param.addParam(taskPk);

		BaseDAO dao = new BaseDAO();
		Collection<WorkflownoteVO> colWorkflownote = dao.retrieveByClause(
				WorkflownoteVO.class, cond, param);
		if (colWorkflownote == null || colWorkflownote.size() == 0)
			return null;

		return colWorkflownote.toArray(new WorkflownoteVO[0]);
	}

	@SuppressWarnings({ "unchecked" })
	public void inefficientWorkitemsByTaskPKs(ArrayList<String> taskPKs)
			throws BusinessException {
		final BaseDAO dao = new BaseDAO();
		final List<WorkflownoteVO> noteList = new ArrayList<WorkflownoteVO>();
		
		new ParameterizedBatchCaller(taskPKs).execute(new Callback() {
			
			@Override
			public void doInParameter(String inSql, SQLParameter param)
					throws BusinessException {
				String cond = "pk_wf_task in " + inSql;
				Collection<WorkflownoteVO> colWorknote = dao.retrieveByClause(WorkflownoteVO.class, cond, param);
				
				noteList.addAll(colWorknote);
			}
		}) ;
		
		if (ArrayUtil.isNull(noteList)) {
			return;
		}
		
		inefficientWorkitems(noteList);
	}

	@SuppressWarnings("unchecked")
	public WFTask queryTaskByCondition(String condition) throws DbException {
		PersistenceManager persist = null;
		try {
			persist = PersistenceManager.getInstance();
			Collection<WFTask> col = persist.retrieveByClause(WFTask.class, mappingMeta, condition);
			
			if (ArrayUtil.isNull(col)) {
				return null;
			} else {
				return col.iterator().next();
			}
			
		} finally {
			if (persist != null)
				persist.release();
		}

	}

	@SuppressWarnings("unchecked")
	public Collection<WFTask> queryTaskCollectionByCondition(String condition) throws DbException {
		PersistenceManager persist = null;
		try {
			persist = PersistenceManager.getInstance();
			Collection<WFTask> col = persist.retrieveByClause(WFTask.class, mappingMeta, condition);
			return col;
		} finally {
			if (persist != null)
				persist.release();
		}

	}
}