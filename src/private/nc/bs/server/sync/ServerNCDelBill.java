package nc.bs.server.sync;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.bs.framework.adaptor.IHttpServletAdaptor;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.server.ISecurityTokenCallback;
import nc.bs.ml.NCLangResOnserver;
import nc.bs.pfxx.xxconfig.FileConfigInfoReadFacade;
import nc.bs.trade.business.HYPubBO;
import nc.itf.pubapp.pub.smart.IBillQueryService;
import nc.itf.uap.pf.IWorkflowAdmin;
import nc.itf.uap.pf.IplatFormEntry;
import nc.vo.ct.purdaily.entity.AggCtPuVO;
import nc.vo.ct.purdaily.entity.CtPuVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.obm.log.ObmLog;
import nc.vo.pfxx.exception.EnvInitException;
import nc.vo.pfxx.exception.FileConfigException;
import nc.vo.pfxx.xxconfig.SysConfigInfo;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.scmpub.api.rest.utils.RestUtils;
import nc.vo.wfengine.definition.WorkflowTypeEnum;

import org.json.JSONString;

import com.alibaba.fastjson.JSONObject;
import com.yonyou.iuap.system.utils.MessageResult;

// OA调用NC接口，实现NC单据   取消生效->取消审批->删除 功能
@SuppressWarnings({ "restriction" })
public class ServerNCDelBill implements IHttpServletAdaptor {

	@Override
	public void doAction(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		InvocationInfoProxy.getInstance().setGroupId("0001A1100000000001QS");// 设置集团
		// InvocationInfoProxy.getInstance().setUserDataSource("RLJT");// 设置数据源
		// InvocationInfoProxy.getInstance().setUserDataSource("RLJT");// 设置数据源
		// 模拟用户登录，不受单点登录控制
		ISecurityTokenCallback sc = (ISecurityTokenCallback) NCLocator
				.getInstance().lookup(ISecurityTokenCallback.class);
		sc.token("NCSystem".getBytes(), "pfxx".getBytes());
		String clientAddress = null;// 客户端IP
		SysConfigInfo globalParameter = null;
		String billid = "";// 单据主键
		String billtype = "";// 单据类型
		MessageResult result = new MessageResult();
		setResponseContentType(response);
		PrintWriter out = response.getWriter();
		try {
			globalParameter = FileConfigInfoReadFacade.getGlobalParameter();
			request.getCharacterEncoding();
			clientAddress = request.getRemoteAddr();
			ObmLog.info("网关收到来自" + clientAddress + "传送数据请求,请求处理开始......",
					getClass(),
					Thread.currentThread().getStackTrace()[1].getMethodName());
			ObmLog.info("校验发送方客户端地址", getClass(), "doAction");
			checkClientAddress(clientAddress, globalParameter);
			ObmLog.info("提取URL请求中的参数", getClass(), "doAction");
			// 接收的数据
			billid = request.getParameter("billid");
			billtype = request.getParameter("billtype");
			// reqstr = GetRequestJsonUtils.getRequestJsonString(request);
			result = this.distribute(billid, billtype);
		} catch (Exception e) {
			result.setStatusCode(MessageResult.STATUS_ERROR);
			result.setMessage("错误信息:" + e.getMessage());
			e.printStackTrace();
		}
		JSONString jsonString = RestUtils.toJSONString(result);
		ObmLog.info("返回的JSON", getClass(), "doAction");
		ObmLog.info(jsonString.toJSONString(), getClass(), "doAction");
		JSONObject parseObject = JSONObject.parseObject(jsonString
				.toJSONString());
		out.print(parseObject);
		out.close();
	}

	private MessageResult distribute(String billid, String billtype)
			throws Exception {
		// ObmLog.info("接收到参数。。。" + reqstr, getClass(), "distribute");
		if (StringUtil.isEmpty(billid)) {
			return new MessageResult(MessageResult.STATUS_ERROR, "异常：billid为空！");
		}
		if (StringUtil.isEmpty(billtype)) {
			return new MessageResult(MessageResult.STATUS_ERROR,
					"异常：billtype为空！");
		}
		// 采购合同
		if ("Z2".equals(billtype)) {
			IBillQueryService billquery = NCLocator.getInstance().lookup(
					IBillQueryService.class);
			AggCtPuVO aggCtPuVO = billquery.querySingleBillByPk(
					AggCtPuVO.class, billid + "");
			if (aggCtPuVO != null) {
				CtPuVO headvo = aggCtPuVO.getParentVO();// 表头VO
				// 单据状态 0=自由，1=生效，2=审批中，3=审批通过，4=审批未通过，5=冻结，6=终止，7=提交
				String billstatus = "";
				int status = headvo.getBill_status();
				if(status == 0){
					billstatus = "自由";
				}else if(status == 1){
					billstatus = "生效";
				}else if(status == 2){
					billstatus = "审批中";
				}else if(status == 3){
					billstatus = "审批通过";
				}else if(status == 4){
					billstatus = "审批未通过";
				}else if(status == 7){
					billstatus = "提交";
				}
				delBillPub(billstatus, billtype, headvo.getVbillcode(), headvo.getPrimaryKey(), aggCtPuVO);
			} else {
				return new MessageResult(MessageResult.STATUS_ERROR,
						"异常：根据单据主键[" + billid + "]未找到对应采购合同！");
			}
		}
		// DataInfoSyncDoMain domain = new DataInfoSyncDoMain();
		return new MessageResult(MessageResult.STATUS_SUCCESS, "");
	}

	private void setResponseContentType(HttpServletResponse response) {
		try {
			response.setContentType("application/json; charset="
					+ FileConfigInfoReadFacade.getGlobalParameter()
							.getOutputEncoding());
		} catch (FileConfigException e) {
			ObmLog.info(e.getMessage(), getClass(), "doAction");
			try {
				throw new EnvInitException(e);
			} catch (EnvInitException e1) {
				e1.printStackTrace();
			}
		}

	}

	private void checkClientAddress(String clientAddress,
			SysConfigInfo globalParameter) throws EnvInitException {
		boolean isEffective = globalParameter.isEffective();
		String[] addresses = globalParameter.getAddresses();
		if (isEffective) {
			boolean b = false;
			if (addresses != null) {
				for (String addresse : addresses) {
					b = StringUtil.match(addresse, clientAddress) ? true : b;
				}
			}
			if (!b) {
				throw new EnvInitException("-31201", NCLangResOnserver
						.getInstance().getStrByID("pfxx", "UPPpfxx-V50018"));
			}
		}
	}

	private IplatFormEntry getIplatFormEntry() {
		return NCLocator.getInstance().lookup(IplatFormEntry.class);
	}

	private IWorkflowAdmin getIWorkflowAdmin() {
		return NCLocator.getInstance().lookup(IWorkflowAdmin.class);
	}

	private HashMap<String, Object> getEparam() {
		HashMap<String, Object> eParam = new HashMap<String, Object>();
		eParam.put("notechecked", "notechecked");
		return eParam;
	}

	private String delBillPub(String status, String billtype,
			String billno, String billpk, AggregatedValueObject aggvo)
			throws BusinessException {
		String errmsg = "";
		DelBIllLog logVO = new DelBIllLog();
		logVO.setBill_type(billtype);
		logVO.setBill_code(billno);
		logVO.setPk_bill(billpk);
		logVO.setBill_status(status);
		
		if("自由".equals(status)){
			try {
				getIplatFormEntry().processAction("DELETE", billtype, null,
						aggvo, null, getEparam());
				logVO.setDef1("删除成功！");
				logVO.setDef2("Y");
				new HYPubBO().insert(logVO);
			} catch (Exception e) {
				// TODO: handle exception
				logVO.setDef1(e.getMessage());
				new HYPubBO().insert(logVO);
				errmsg = e.getMessage();
			}
		}else if("生效".equals(status)){
			try {
				getIplatFormEntry().processAction("UNVALIDATE", billtype, null,
						aggvo, null, getEparam());
				getIplatFormEntry().processAction("DELETE", billtype, null,
						aggvo, null, getEparam());
				logVO.setDef1("删除成功！");
				logVO.setDef2("Y");
				new HYPubBO().insert(logVO);
			} catch (Exception e) {
				// TODO: handle exception
				logVO.setDef1(e.getMessage());
				new HYPubBO().insert(logVO);
				errmsg = e.getMessage();
			}
		}else if("审批中".equals(status)){
			try {
				// 终止流程 成功后会变为自由态
				getIWorkflowAdmin().terminateWorkflow(billpk, billtype, billno,
						WorkflowTypeEnum.Approveflow.getIntValue());
				// 删除
				getIplatFormEntry().processAction("DELETE", billtype, null,
						aggvo, null, getEparam());
				logVO.setDef1("删除成功！");
				logVO.setDef2("Y");
				new HYPubBO().insert(logVO);
			} catch (Exception e) {
				// TODO: handle exception
				logVO.setDef1(e.getMessage());
				new HYPubBO().insert(logVO);
				errmsg = e.getMessage();
			}
		}else if("审批通过".equals(status)){
			try {
				// 终止流程 成功后会变为自由态
				getIWorkflowAdmin().terminateWorkflow(billpk, billtype, billno,
						WorkflowTypeEnum.Approveflow.getIntValue());
				// 取消审批
				getIplatFormEntry().processAction("UNAPPROVE", billtype, null,
						aggvo, null, getEparam());
				// 删除
				getIplatFormEntry().processAction("DELETE", billtype, null,
						aggvo, null, getEparam());
				logVO.setDef1("删除成功！");
				logVO.setDef2("Y");
				new HYPubBO().insert(logVO);
			} catch (Exception e) {
				// TODO: handle exception
				logVO.setDef1(e.getMessage());
				new HYPubBO().insert(logVO);
				errmsg = e.getMessage();
			}
		}else if("提交".equals(status)){
			try {
				// 终止流程 成功后会变为自由态
				getIWorkflowAdmin().terminateWorkflow(billpk, billtype, billno,
						WorkflowTypeEnum.Approveflow.getIntValue());
				// 取消审批
				/*getIplatFormEntry().processAction("UNAPPROVE", billtype, null,
						aggvo, null, getEparam());*/
				// 删除
				getIplatFormEntry().processAction("DELETE", billtype, null,
						aggvo, null, getEparam());
				logVO.setDef1("删除成功！");
				logVO.setDef2("Y");
				new HYPubBO().insert(logVO);
			} catch (Exception e) {
				// TODO: handle exception
				logVO.setDef1(e.getMessage());
				new HYPubBO().insert(logVO);
				errmsg = e.getMessage();
			}
		}
		return errmsg;
	}
}
