package com.sscwf.rejectiw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.axis.client.Call;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pf.pub.PfDataCache;
import nc.bs.trade.business.HYPubBO;
import nc.bs.wfengine.engine.ActivityInstance;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.itf.org.ssc.ISSCOrgQryService;
import nc.itf.ssc.task.ssclientage.ISSClientageQryService;
import nc.itf.ssc.wf.IBGYSysInfoFacade;
import nc.itf.ssc.wf.SysWFActivityInfo;
import nc.itf.uap.pf.IWorkflowDefine;
import nc.itf.uap.pf.IWorkflowMachine;
import nc.itf.uap.pf.IplatFormEntry;
import nc.itf.uap.pf.metadata.IFlowBizItf;
import nc.itf.uap.rbac.IUserManageQuery;
import nc.md.data.access.NCObject;
import nc.pubitf.para.SysInitQuery;
import nc.uap.lfw.core.AppInteractionUtil;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.combodata.CombItem;
import nc.uap.lfw.core.combodata.ComboData;
import nc.uap.lfw.core.comp.RadioGroupComp;
import nc.uap.lfw.core.comp.ReferenceComp;
import nc.uap.lfw.core.comp.TextAreaComp;
import nc.uap.lfw.core.comp.text.ComboBoxComp;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DialogEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.event.TextEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.ui.pcm.utils.GetDao;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.OrgVO;
import nc.vo.org.ssc.SSCOrgVO;
import nc.vo.pf.change.PfUtilBaseTools;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.sm.UserVO;
import nc.vo.ssc.task.base.ISSCBusiActivityConst;
import nc.vo.uap.wfmonitor.ProcessRouteRes;
import nc.vo.wfengine.core.activity.Activity;
import nc.vo.wfengine.core.activity.GenericActivityEx;
import nc.vo.wfengine.core.parser.XPDLParserException;
import nc.vo.wfengine.definition.WorkflowTypeEnum;
import nc.vo.wfengine.pub.WfTaskType;
import nc.wf.SSCWFConst;
import nc.wf.SscObject;
import nc.ws.intf.OALogVO;
import nc.ws.intf.WorkFlowBill;
import ssc.wf.util.ISSCWFUtil;
import ssc.wf.util.SSCWFUtil;
import ssccloud.ssccredit.callcloud.SscCreditSave;
import uap.web.bd.pub.AppUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ant.common.utils.WebServiceUtil;

// 共享驳回XBX修改
@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class RejectwivViewController {

	private static final long serialVersionUID = 1L;
	private static final long ID = 5L;
	private Map<String, Integer> actOrderMap = new HashMap<String, Integer>();
	private List<SysWFActivityInfo> extSystemActs = null;
	private static String oaUrl = "http://173.18.159.12:70/";
	// private static String oaUrl = "";

	// 驳回制单人后调用OA接口，发送提醒
	public void onRejectClick(MouseEvent mouseEvent) throws BusinessException {
		AppUtil.addAppAttr("rejectstatus", "init");
		String taskID = (String) AppUtil.getAppAttr("taskID");
		// 从界面获取信息
		LfwView view = AppLifeCycleContext.current().getViewContext().getView();

		// 得到驳回到的活动
		ComboBoxComp cbc = (ComboBoxComp) view.getViewComponents()
				.getComponent("cbRejectAct");
		String targetID = cbc.getValue();

		if (StringUtil.isEmptyWithTrim(targetID))
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("sscworkflowweb",
							"0sscworkflowweb0000")/* @res "请选择要驳回到的环节" */);

		TextAreaComp txtAreaCp = (TextAreaComp) view.getViewComponents()
				.getComponent("txtMemo");

		String txtMemo = txtAreaCp.getValue();
		if (StringUtil.isEmptyWithTrim(txtMemo)) {
			AppInteractionUtil.showMessageDialog(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("sscworkflowweb",
							"0sscworkflowweb0001")/* @res "请输入驳回原因！" */);
			return;
		}

		if (txtMemo.getBytes().length > 400) {
			AppInteractionUtil.showMessageDialog(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("sscworkflowweb",
							"0sscworkflowweb0002")/* @res "驳回原因的长度不能大于400！" */);
			return;
		}

		// 共享中心驳回后不触发自动审批
		InvocationInfoProxy.getInstance().setProperty("sscIsAutoApprove", "N");

		// 执行驳回操作
		// 从缓存得到worknote和单据vo
		WorkflownoteVO noteVO = (WorkflownoteVO) AppUtil
				.getAppAttr("currentWorkNote");

		AggregatedValueObject aggVO = (AggregatedValueObject) AppUtil
				.getAppAttr("billVO");

		// 得到交易类型
		NCObject ncObj = NCObject.newInstance(aggVO);
		IFlowBizItf itf = (IFlowBizItf) ncObj
				.getBizInterface(IFlowBizItf.class);
		String tranType = itf.getTranstype();

		noteVO.getTaskInfo().getTask()
				.setTaskType(WfTaskType.Backward.getIntValue());

		String currentActType = (String) AppUtil.getAppAttr("currentActType");

		// 设置流程控制对象
		SscObject sscObj = new SscObject();
		if (currentActType.equals(SSCWFConst.FirstExamAct)) {
			// 得到是否重走工作流
			RadioGroupComp rgc = (RadioGroupComp) view.getViewComponents()
					.getComponent("rgIfRepeat");
			// 获取是否重走 重走为1 不重走为2
			String ifRepeat = rgc.getValue();

			if (ifRepeat.equals("2")) {
				if (nc.ssc.pub.util.SscIntergrationUtil.isSscDeploy())
					sscObj.setSscifRepeatWorkflow("2");
				else {
					noteVO.getTaskInfo().getTask().setSubmit2RjectTache(true);
				}
			}
		}

		noteVO.setApproveresult("R");

		// 只有和外系统进行集成且审核节点才驳回初审
		if (nc.ssc.pub.util.SscIntergrationUtil.isSscDeploy()
				&& currentActType.equals(SSCWFConst.FirstExamAct))
			noteVO.getTaskInfo().getTask().setBackToFirstActivity(true);
		else
			noteVO.getTaskInfo().getTask().setJumpToActivity(targetID);
		// 设置意见
		noteVO.setChecknote(txtMemo);

		// 当前登录集团PK
		String login_pkgroup = InvocationInfoProxy.getInstance().getGroupId();
		String pk_groupofbill = (String) AppUtil.getAppAttr("pk_groupofbill");
		if (!StringUtil.isEmptyWithTrim(pk_groupofbill)
				&& !login_pkgroup.equals(pk_groupofbill)) {
			// 设定集团PK为提交单据集团PK
			InvocationInfoProxy.getInstance().setGroupId(pk_groupofbill);
		}

		sscObj.setSscTaskID(taskID);
		sscObj.setSscSourceAction("R");
		sscObj.setImageRejectAction(SSCWFConst.ImageReScan);
		sscObj.setSscSourceActiveID(noteVO.getTaskInfo().getTask()
				.getActivityID());
		sscObj.setSscTargetActiveID(targetID);
		sscObj.setChecknote(txtMemo);
		sscObj.setBusActCons((String) (getActMap().get(targetID)));// liningc+得到驳回的目标环节常量code

		String pk_user;
		// 当前处理人 SSC委托关系的处理人
		String curWorker = "", sccWorker = "";
		curWorker = (String) AppUtil.getAppAttr("sscdriverUser");

		// 按委托关系进行查询
		String billType = (String) AppUtil.getAppAttr("billType");
		String biillTypeid = PfDataCache.getBillType(billType)
				.getPk_billtypeid();

		String pk_org = itf.getPkorg();
		ISSClientageQryService q = NCLocator.getInstance().lookup(
				ISSClientageQryService.class);

		String pk_ssc = q.querySSCVOSByFinanceorgAndBilltype(pk_org,
				biillTypeid);
		ISSCOrgQryService sscqs = NCLocator.getInstance().lookup(
				ISSCOrgQryService.class);
		UserVO user = sscqs.queryUserVOBySSCID(pk_ssc);

		if (user != null)
			sccWorker = user.getCuserid();

		pk_user = curWorker;
		// 看当前处理人和委托关系的人是否是一个人
		if (!StringUtil.isEmptyWithTrim(sccWorker)
				&& !curWorker.equals(sccWorker)) {
			// 如果人不相同，需要修改worknote的处理人
			ISSCWFUtil util = NCLocator.getInstance().lookup(ISSCWFUtil.class);
			util.updateWorknoteCheckman(noteVO.getPk_checkflow(), sccWorker);
			noteVO.setCheckman(sccWorker);
			pk_user = sccWorker;
		}

		// zhaojianc 2016-05-19 碧桂园
		if (nc.ssc.pub.util.SscIntergrationUtil.isSscDeploy()) {
			HashMap hmPfExParams = null;
			PfParameterVO paraVo = PfUtilBaseTools.getVariableValue(
					itf.getBilltype(), "SIGNAL" + pk_user, aggVO,
					new AggregatedValueObject[] { aggVO }, sscObj, null,
					noteVO, new HashMap(), new Hashtable()); // new
																// PfParameterVO();
			int ret = ((IWorkflowMachine) NCLocator.getInstance().lookup(
					IWorkflowMachine.class)).forwardCheckFlow(paraVo);
		} else {
			IplatFormEntry iIplatFormEntry = (IplatFormEntry) NCLocator
					.getInstance().lookup(IplatFormEntry.class.getName());
			iIplatFormEntry.processAction("SIGNAL" + pk_user, tranType, noteVO,
					aggVO, sscObj, null);
		}
		// XBX能投调用
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		Boolean bool = Boolean.FALSE;
		if ("0001A21000000003U6L6".equals(pk_org)
				|| "0001A21000000003U6L6".equals(orgVO.getPk_fatherorg())) {
			bool = Boolean.TRUE;
		}
		if (orgVO.getPk_fatherorg() != null) {
			OrgVO fatherOrgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(
					OrgVO.class, orgVO.getPk_fatherorg());
			if ("0001A21000000003U6L6"
					.equals(fatherOrgVO.getPk_fatherorg())) {
				bool = Boolean.TRUE;
			}
		}
		// 如果驳回到非制单人，则不提示
		if(!noteVO.getSenderman().equals(itf.getBillMaker())){
			bool = Boolean.FALSE;
		}
		String oaid = null;
		if ("4".equals(orgVO.getDef2())) {
			if (bool) {
				// 获取OA人员ID
				UserVO userVO = (UserVO) new HYPubBO().queryByPrimaryKey(
						UserVO.class, itf.getBillMaker());
				String idCard = (String) new HYPubBO().findColValue(
						"bd_psndoc", "id", "nvl(dr,0) = 0 and pk_psndoc='"
								+ userVO.getPk_psndoc() + "'");
				// 制单人对应OA系统ID
				oaid = getOAID(idCard, itf.getPkorg());
			}
		}

		if (!StringUtil.isEmptyWithTrim(pk_groupofbill)
				&& !login_pkgroup.equals(pk_groupofbill)) {
			// 设定集团PK为登录集团PK
			InvocationInfoProxy.getInstance().setGroupId(login_pkgroup);
		}
		try {
			JSONObject info = new JSONObject();
			String sql = " select senddate from pub_workflownote where billid = '"
					+ itf.getBillId() + "' order by senddate";
			IRowSet rows = new DataAccessUtils().query(sql);
			String sendDate = "";
			if (rows.next()) {
				sendDate = rows.getUFDateTime(0).toString(null,
						new SimpleDateFormat("yyyy/MM/dd"));
			}
			info.put("sendDate", sendDate);
			info.put("transType", PfDataCache.getBillType(itf.getTranstype())
					.getBilltypenameOfCurrLang());

			// 驳回时记录信用评价信息
			ReferenceComp comp = (ReferenceComp) view.getViewComponents()
					.getComponent("credit");
			String value = comp.getValue();
			if (value != null && value.length() > 0) {
				UserVO[] users = NCLocator
						.getInstance()
						.lookup(IUserManageQuery.class)
						.queryUserByClause(
								" cuserid= '" + itf.getBillMaker() + "'");
				Map<String, Object> saveCreditParams = new HashMap<String, Object>();
				saveCreditParams.put("approver", InvocationInfoProxy
						.getInstance().getUserCode());
				saveCreditParams.put("billmaker", users[0].getUser_code());
				saveCreditParams.put("billid", itf.getBillId());
				saveCreditParams.put("billno", itf.getBillNo());
				saveCreditParams.put("ssccreditrules", value.split(","));
				saveCreditParams.put("info", info.toJSONString());

				SscCreditSave.grading(saveCreditParams);
			}
		} catch (Exception e) {
			Logger.error("驳回时记录信用评价信息出错，不终止业务流程。" + e.getMessage(), e);
		}
		AppUtil.addAppAttr("rejectstatus", "sucess");
		AppLifeCycleContext.current().getWindowContext().closeView("rejectwiv");
		AppInteractionUtil.showShortMessage(nc.vo.ml.NCLangRes4VoTransl
				.getNCLangRes().getStrByID("sscworkflowweb",
						"0sscworkflowweb0003")/* @res "操作成功" */);
		CmdInvoker.invoke(new UifPlugoutCmd("rejectwiv", "rejectwiv_plugout"));
		// XBX驳回后调用能投OA系统通知
		if ("4".equals(orgVO.getDef2())) {
			if (bool) {
				sendOARejectMessage(itf, txtMemo, oaid);// 单据，原因
			}
		}
	}

	private void sendOARejectMessage(IFlowBizItf itf, String txtMemo,
			String oaid) throws BusinessException {
		// TODO Auto-generated method stub
		String result = null;
		try {
			Call call = WebServiceUtil.getCall();
			call.setTargetEndpointAddress(oaUrl
					+ "/services/ServiceMessageCustom");
			call.addParameter("string",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);
			call.setOperationName("sendCustomMessageSingle");// 设置远程调用类中的方法
			call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);
			call.setEncodingStyle("UTF-8");
			Map<String, Object> map = new HashMap();
			map.put("code", "526");
			BilltypeVO billtypeVO = PfDataCache.getBillType(itf.getTranstype());
			if (billtypeVO == null) {
				throw new BusinessException("根据交易类型[" + itf.getTranstype()
						+ "]获取名称失败！");
			}
			// 当前登录人PK
			String userID = InvocationInfoProxy.getInstance().getUserId();
			UserVO curUserVO = (UserVO) new HYPubBO().queryByPrimaryKey(UserVO.class,
					userID);
			map.put("title", itf.getBillNo() + billtypeVO.getBilltypename()
					+ "已被财务共享中心退回");// 标题：单据号+单据类型+财务共享中心退回
			map.put("context", "退回原因："+txtMemo+" 审核人："+curUserVO.getUser_name()); // 内容：退回原因：财务写的驳回内容  审核人：XXX
			Set<String> list = new HashSet();
			UserVO userVO = (UserVO) new HYPubBO().queryByPrimaryKey(
					UserVO.class, itf.getBillMaker());
			String idCard = (String) new HYPubBO().findColValue("bd_psndoc",
					"id",
					"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc()
							+ "'");
			// 制单人对应OA系统ID
			list.add(oaid);
			map.put("userIdList", list);
			result = (String) call
					.invoke(new String[] { JSON.toJSONString(map) });
			// 保存日志
			JSONObject logJson = new JSONObject();
			logJson.put("senddata", JSON.toJSONString(map));
			logJson.put("rtndata", result);
			logJson.put("billid", itf.getBillId());
			logJson.put("billno", itf.getBillNo());
			logJson.put("approver", userVO.getUser_name());
			logJson.put("billtype", itf.getTranstype());
			logJson.put("pk_org", itf.getPkorg());
			logJson.put("pk_group", "0001A1100000000001QS");
			SaveOALog(logJson);
		} catch (Exception e) {
			// TODO: handle exception
			throw new BusinessException(e.getMessage());
		}
	}

	// 通过人员身份证号和NC单位主键获取OA人员id
	public static String getOAID(String id, String pk_org)
			throws BusinessException {
		String userid = "";
		// 调用OA接口获取 流程id
		JSONObject json = new JSONObject();
		HttpURLConnection connection = null;
		BufferedReader reader = null;
		String line = null;
		try {
			try {
				URL url = new URL(oaUrl
						+ "szzt/service/getuseridbyidno.jsp?idno=" + id);
				connection = (HttpURLConnection) url.openConnection();// 根据URL生成HttpURLConnection
				connection.setRequestMethod("POST");// 默认GET请求
				connection.connect();// 建立TCP连接
				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					reader = new BufferedReader(new InputStreamReader(
							connection.getInputStream(), "UTF-8"));// 发送http请求
					StringBuilder result = new StringBuilder();
					// 循环读取流
					while ((line = reader.readLine()) != null) {
						result.append(line).append(
								System.getProperty("line.separator"));// "\n"
					}
					System.out.println(result.toString());
					json = JSONObject.parseObject(result.toString());
					if (json != null && "true".equals(json.getString("ok"))) {
						userid = json.getJSONObject("data").getString("userid");
					} else {
						Logger.error("接口返回信息：" + json.getString("msg"));
						throw new BusinessException("ID[" + id + "]"
								+ "获取OA系统人员ID失败：" + json.getString("msg"));
					}
				}
			} catch (UifException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new BusinessException("ID[" + id + "]" + "获取人员ID失败："
						+ e.getMessage());
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != reader) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			connection.disconnect();
		}
		return userid;
	}

	// 删除共享单据
	public String delssc(String billno) {
		BaseDAO dao = new BaseDAO();
		String mes = null;// 报错信息
		String sql1 = "delete from ssc_activetask where pk_ssctask in( select pk_ssctask from ssc_ssctask where billcode='"
				+ billno + "')";
		String sql2 = "delete from ssc_persontask where pk_ssctask in( select pk_ssctask from ssc_ssctask where billcode='"
				+ billno + "')";
		String sql3 = "delete from ssc_posttask where pk_ssctask in( select pk_ssctask from ssc_ssctask where billcode='"
				+ billno + "')";
		String sql4 = "delete from ssc_ssctasklog where pk_ssctask in( select pk_ssctask from ssc_ssctask where billcode='"
				+ billno + "')";
		String sql5 = "delete from ssc_ssctask where billcode='" + billno + "'";
		try {
			dao.executeUpdate(sql1);
			dao.executeUpdate(sql2);
			dao.executeUpdate(sql3);
			dao.executeUpdate(sql4);
			dao.executeUpdate(sql5);
		} catch (BusinessException e) {
			// TODO: handle exception
			e.printStackTrace();
			mes = e.getMessage();
		}
		return mes;
	}

	// 记录日志VO
	private void SaveOALog(JSONObject logJson) throws BusinessException {
		OALogVO vo = new OALogVO();
		vo.setBill_code(logJson.getString("billno"));
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		vo.setCreationtime(df.format(new Date()));// 创建时间
		vo.setPk_bill(logJson.getString("billid"));// 单据主键
		vo.setTransi_type(logJson.getString("billtype")); // 交易类型
		// 当前登录人ID
		vo.setUsername(logJson.getString("approver"));
		vo.setSend_data(logJson.getString("senddata"));// 发送参数
		vo.setDef1("OA消息提醒");
		vo.setDef2(logJson.getString("pk_org"));
		vo.setDef3(logJson.getString("pk_group"));
		vo.setDr(0);
		vo.setDef4(logJson.getString("rtndata"));
		new HYPubBO().insert(vo);
	}

	public void beforeShow(DialogEvent dialogEvent) throws BusinessException,
			XPDLParserException {
		// ----------------初始化界面及数据--------------------
		initData();

		// 判断如果安装信用模块，则显示信用评价组件
		LfwView view = AppLifeCycleContext.current().getViewContext().getView();
		String cmsgtid = SysInitQuery.getParaString("GLOBLE00000000000000",
				"CCREDITTID");
		String pk_busiact = (String) AppUtil.getAppAttr("pk_busiact");
		// 判断共享环节
		Row[] rows = (Row[]) AppUtil.getAppAttr("rowData");
		Integer index = (Integer) AppUtil.getAppAttr("rowIndex");
		Integer pk_busiactIndex = (Integer) AppLifeCycleContext.current()
				.getApplicationContext().getAppSession()
				.getAttribute("pk_busiactIndex");
		if (index != -1) {
			pk_busiact = rows[index].getValue(pk_busiactIndex).toString();
		}
		if (cmsgtid != null && !"".equals(cmsgtid)
				&& ISSCBusiActivityConst.PK_examine.equals(pk_busiact)) {
			ReferenceComp comp = (ReferenceComp) view.getViewComponents()
					.getComponent("credit");
			comp.setVisible(true);
		}
	}

	private void initData() throws BusinessException, XPDLParserException {
		AppUtil.addAppAttr("RejectwivViewControllerMap", null);
		// 从界面获取信息
		LfwView view = AppLifeCycleContext.current().getViewContext().getView();
		AggregatedValueObject aggVO = (AggregatedValueObject) AppUtil
				.getAppAttr("billVO");

		WorkflownoteVO noteVO = (WorkflownoteVO) AppUtil
				.getAppAttr("currentWorkNote");

		// --需要判断是否是SSC审核节点 // --当前环节ID
		String currentActID = noteVO.getTaskInfo().getTask().getActivityID();
		// --得到流程定义PK
		String wfdefPK = noteVO.getTaskInfo().getTask().getWfProcessDefPK();
		List lstActs = PfDataCache.getWorkflowProcess(wfdefPK).getActivities();
		Activity curAct = PfDataCache.getWorkflowProcess(wfdefPK)
				.findActivityByID(currentActID);
		Map<String, Activity> afterActs = SSCWFUtil.findAfterActivity(curAct);
		// 得到路由信息
		ProcessRouteRes processRoute = null;
		IWorkflowDefine wfDefine = (IWorkflowDefine) NCLocator.getInstance()
				.lookup(IWorkflowDefine.class.getName());
		processRoute = wfDefine.queryProcessRoute(noteVO.getBillid(),
				noteVO.getPk_billtype(), null,
				WorkflowTypeEnum.Workflow.getIntValue());

		// 得到路由信息
		if (processRoute == null)
			return;

		ActivityInstance[] actInsAry = processRoute.getActivityInstance();
		Map<String, String> actRoutes = new HashMap<String, String>();
		for (ActivityInstance actIns : actInsAry) {
			if (!actRoutes.containsKey(actIns.getActivityID())) {
				actRoutes.put(actIns.getActivityID(), actIns.getActivityID());
			}
		}

		// 当前登录集团PK
		String login_pkgroup = InvocationInfoProxy.getInstance().getGroupId();
		String pk_groupofbill = (String) AppUtil.getAppAttr("pk_groupofbill");
		if (!StringUtil.isEmptyWithTrim(pk_groupofbill)
				&& !login_pkgroup.equals(pk_groupofbill)) {
			// 设定集团PK为提交单据集团PK
			InvocationInfoProxy.getInstance().setGroupId(pk_groupofbill);
		}
		String currentActType = "";
		List<ActInfo> actInfos = new ArrayList<ActInfo>();
		ISSCWFUtil util = NCLocator.getInstance().lookup(ISSCWFUtil.class);
		for (Object actObject : lstActs) {
			if (actObject instanceof GenericActivityEx) {
				GenericActivityEx act = (GenericActivityEx) actObject;

				// 如果没有路由信息，则略过
				if (!actRoutes.containsKey(act.getId())
						|| afterActs.containsKey(act.getId()))
					continue;

				if (act.isStartActivity()) {
					actInfos.add(new ActInfo(act.getName()
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("sscworkflowweb",
											"0sscworkflowweb0004")/*
																 * @res "<制单人>"
																 */, act
							.getId(), SSCWFConst.MakeBillAct));
					continue;
				}

				String actType = util.getActivityType(act,
						(String) AppUtil.getAppAttr("billType"));

				if (currentActID.equals(act.getId()))
					currentActType = actType;

				if (actType.equals(SSCWFConst.ImageAct)) {
					actInfos.add(new ActInfo(act.getName()
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("sscworkflowweb",
											"0sscworkflowweb0005")/*
																 * @res "<影像扫描>"
																 */, act
							.getId(), actType));
					continue;
				}

				if (actType.equals(SSCWFConst.AccountExamAct)) {
					actInfos.add(new ActInfo(act.getName()
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("sscworkflowweb",
											"0sscworkflowweb0006")/*
																 * @res "<会计初审>"
																 */, act
							.getId(), actType));
					continue;
				}

				if (actType.equals(SSCWFConst.FirstExamAct)) {
					actInfos.add(new ActInfo(act.getName()
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("sscworkflowweb",
											"0sscworkflowweb0007")/*
																 * @res
																 * "<SSC初审>"
																 */, act
							.getId(), actType));
					continue;
				}
				if (actType.equals(SSCWFConst.ReExamAct)) {
					actInfos.add(new ActInfo(act.getName()
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("sscworkflowweb",
											"0sscworkflowweb0008")/*
																 * @res
																 * "<SSC复审>"
																 */, act
							.getId(), actType));
					continue;
				}
				if (actType.equals(SSCWFConst.SignAct)) {
					actInfos.add(new ActInfo(act.getName()
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("sscworkflowweb",
											"0sscworkflowweb0009")/*
																 * @res
																 * "<SSC签字>"
																 */, act
							.getId(), actType));// liningc+
					continue;
				}
				if (actType.equals(SSCWFConst.SettlementAct)) {
					actInfos.add(new ActInfo(act.getName()
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("sscworkflowweb",
											"0sscworkflowweb0010")/*
																 * @res
																 * "<SSC结算>"
																 */, act
							.getId(), actType));// liningc+
					continue;
				}
			}
		}

		if (!login_pkgroup.equals(pk_groupofbill)) {
			// 设定集团PK为提交单据集团PK
			InvocationInfoProxy.getInstance().setGroupId(login_pkgroup);
		}
		// ---------------------填充----------------------------------------------------
		ComboData cbData = AppLifeCycleContext.current().getViewContext()
				.getView().getViewModels().getComboData("rejectitem");

		AppUtil.addAppAttr("currentActType", currentActType);

		// 得到是否重走工作流
		RadioGroupComp rgc = (RadioGroupComp) view.getViewComponents()
				.getComponent("rgIfRepeat");
		if (currentActType.equals(SSCWFConst.FirstExamAct)) {
			rgc.setVisible(true);
			// 默认重走工作流
			rgc.setValue("1");
		}

		// 如果是审核，就强制读取NC系统单据信息
		if (nc.ssc.pub.util.SscIntergrationUtil.isSscDeploy()
				&& currentActType.equals(SSCWFConst.FirstExamAct))
		// 如果外系统单据,而且当前环节还必须为审核，则读外系统的环节进行填充
		{
			// 得到外系统信息获取接口 TODO 要改
			IBGYSysInfoFacade svr = (IBGYSysInfoFacade) NCLocator.getInstance()
					.lookup(IBGYSysInfoFacade.class);
			this.extSystemActs = svr.getWorkFlowActivity(aggVO);
			fillSysData(cbData, this.extSystemActs, currentActType);
			addSystemActs(this.extSystemActs);
		} else {
			fillData(cbData, actInfos, currentActType);
		}

		ComboBoxComp cbRejectAct = (ComboBoxComp) view.getViewComponents()
				.getComponent("cbRejectAct");
		// 如果只有一条，默认选中
		if (cbRejectAct.getComboData().getAllCombItems() != null
				&& cbRejectAct.getComboData().getAllCombItems().length == 1)
			cbRejectAct
					.setValue(cbRejectAct.getComboData().getAllCombItems()[0]
							.getValue());
	}

	/**
	 * 使用外系统传递过来的流程信息进行填充
	 * 
	 * @param cbData
	 * @param extSystemActs
	 * @param currentActType
	 */
	private void fillSysData(ComboData cbData,
			List<SysWFActivityInfo> extSystemActs, String currentActType) {
		cbData.removeAllComboItems();
		for (SysWFActivityInfo actInfo : extSystemActs) {
			CombItem combItem = new CombItem();
			combItem.setText(actInfo.getDisplay());
			combItem.setValue(actInfo.getId());
			cbData.addCombItem(combItem);
		}
	}

	private void fillData(ComboData cbData, List<ActInfo> actInfos,
			String currentActType) {
		List<String> allowReject = new ArrayList<String>();
		if (currentActType.equals(SSCWFConst.FirstExamAct)) {
			allowReject.add(SSCWFConst.MakeBillAct); // 制单
			allowReject.add(SSCWFConst.ImageAct); // 影像
			allowReject.add(SSCWFConst.AccountExamAct); // 会计初审
		} else if (currentActType.equals(SSCWFConst.ReExamAct)) {
			allowReject.add(SSCWFConst.FirstExamAct); // 初审
		} else if (currentActType.equals(SSCWFConst.SignAct)) {
			allowReject.add(SSCWFConst.FirstExamAct); // 初审 liningc+
			allowReject.add(SSCWFConst.ReExamAct); // 复核 liningc+
		} else if (currentActType.equals(SSCWFConst.SettlementAct)) {
			allowReject.add(SSCWFConst.FirstExamAct); // 初审 liningc+
			allowReject.add(SSCWFConst.ReExamAct); // 复核liningc+
			allowReject.add(SSCWFConst.SignAct); // 签字 liningc+
		}

		cbData.removeAllComboItems();
		for (ActInfo actInfo : actInfos) {
			if (allowReject.contains(actInfo.getActType())) {
				CombItem combItem = new CombItem();
				combItem.setText(actInfo.getActName());
				combItem.setValue(actInfo.getActID());
				cbData.addCombItem(combItem);
			}
		}
	}

	public void onCancelClick(MouseEvent mouseEvent) {
		AppLifeCycleContext.current().getWindowContext().closeView("rejectwiv");
	}

	private Map<String, String> getActMap() {
		Object oMap = AppUtil.getAppAttr("RejectwivViewControllerMap");
		if (oMap == null) {
			Map actID2SSCTypeMap = new HashMap();
			addActToMap(actID2SSCTypeMap);
			AppUtil.addAppAttr("RejectwivViewControllerMap",
					(Serializable) actID2SSCTypeMap);
			oMap = actID2SSCTypeMap;
		}
		return (Map) oMap;
	}

	private void addActToMap(Map<String, String> actID2SSCTypeMap) {
		WorkflownoteVO noteVO = (WorkflownoteVO) AppUtil
				.getAppAttr("currentWorkNote");
		String wfdefPK = noteVO.getTaskInfo().getTask().getWfProcessDefPK();
		List lstActs = null;
		try {
			lstActs = PfDataCache.getWorkflowProcess(wfdefPK).getActivities();
		} catch (XPDLParserException e) {
			return;
		} catch (BusinessException e) {
			return;
		}
		if (lstActs == null) {
			return;
		}
		ISSCWFUtil util = (ISSCWFUtil) NCLocator.getInstance().lookup(
				ISSCWFUtil.class);
		for (int i = 0; i < lstActs.size(); ++i) {
			Object actObject = lstActs.get(i);
			if (actObject instanceof GenericActivityEx) {
				GenericActivityEx act = (GenericActivityEx) actObject;
				if (act.isStartActivity()) {
					actID2SSCTypeMap.put(act.getId(), "sscMakebill");
				} else {
					String actType = util.getActivityType(act,
							(String) AppUtil.getAppAttr("billType"));
					if (actType.equals("sscImage")) {
						actID2SSCTypeMap.put(act.getId(), "sscImage");
					} else if (actType.equals("sscAccountFirstExam")) {
						actID2SSCTypeMap
								.put(act.getId(), "sscAccountFirstExam");
					} else if (actType.equals("sscFirstExam")) {
						actID2SSCTypeMap.put(act.getId(), "examine");
					} else if (actType.equals("sscReview")) {
						actID2SSCTypeMap.put(act.getId(), "reexamine");
					} else if (actType.equals("sscSign")) {
						actID2SSCTypeMap.put(act.getId(), "sign");
					} else if (actType.equals("sscSettlement"))
						actID2SSCTypeMap.put(act.getId(), "settlement");
				}
			}
		}
	}

	private void addSystemActs(List<SysWFActivityInfo> extSystemActs) {
		Map actID2SSCTypeMap = getActMap();
		for (SysWFActivityInfo actInfo : extSystemActs)
			actID2SSCTypeMap.put(actInfo.getId(), actInfo.getCode());
	}

	public void onAfterDataChange(TextEvent valueChanged) {
		if (valueChanged.getSource() instanceof ReferenceComp) {
			ReferenceComp comp = (ReferenceComp) valueChanged.getSource();
			LfwView view = AppLifeCycleContext.current().getViewContext()
					.getView();
			TextAreaComp txtAreaCp = (TextAreaComp) view.getViewComponents()
					.getComponent("txtMemo");
			// 解决选择多个信用,意见变为乱码的问题
			if (txtAreaCp != null && comp.getValue() != null
					&& comp.getShowValue() != null
					&& !comp.getShowValue().equals(comp.getValue())) {
				txtAreaCp.setValue(comp.getShowValue());
			}
		}
	}
}
