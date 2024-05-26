package nc.bs.server.sync;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.bs.framework.adaptor.IHttpServletAdaptor;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.server.ISecurityTokenCallback;
import nc.bs.ml.NCLangResOnserver;
import nc.bs.pfxx.xxconfig.FileConfigInfoReadFacade;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.obm.log.ObmLog;
import nc.vo.pfxx.exception.EnvInitException;
import nc.vo.pfxx.exception.FileConfigException;
import nc.vo.pfxx.xxconfig.SysConfigInfo;
import nc.vo.scmpub.api.rest.utils.RestUtils;

import org.json.JSONString;

import com.alibaba.fastjson.JSONObject;
import com.yonyou.iuap.system.utils.GetRequestJsonUtils;
import com.yonyou.iuap.system.utils.MessageResult;

public class ServerNCSync implements IHttpServletAdaptor {

	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		InvocationInfoProxy.getInstance().setGroupId("0001A1100000000001QS");
		// InvocationInfoProxy.getInstance().setUserDataSource("RLJT");
		ISecurityTokenCallback sc = (ISecurityTokenCallback) NCLocator.getInstance().lookup(ISecurityTokenCallback.class);
		sc.token("NCSystem".getBytes(), "pfxx".getBytes());
		String clientAddress = null;
		SysConfigInfo globalParameter = null;
		String reqstr = null;
		MessageResult result = new MessageResult();
		setResponseContentType(response);
		PrintWriter out = response.getWriter();
		try {
			globalParameter = FileConfigInfoReadFacade.getGlobalParameter();
			request.getCharacterEncoding();
			clientAddress = request.getRemoteAddr();
			ObmLog.info("网关收到来自" + clientAddress + "传送数据请求,请求处理开始......", getClass(), Thread.currentThread().getStackTrace()[1].getMethodName());
			ObmLog.info("校验发送方客户端地址", getClass(), "doAction");
			checkClientAddress(clientAddress, globalParameter);
			ObmLog.info("提取URL请求中的参数", getClass(), "doAction");
			// 接收的数据
			reqstr = GetRequestJsonUtils.getRequestJsonString(request);
			result = this.distribute(reqstr);
		} catch (Exception e) {
			result.setStatusCode(MessageResult.STATUS_ERROR);
			result.setMessage("错误信息:"+e.getMessage());
			e.printStackTrace();
		}
		JSONString jsonString = RestUtils.toJSONString(result);
		ObmLog.info("返回的JSON", getClass(), "doAction");
		ObmLog.info(jsonString.toJSONString(), getClass(), "doAction");
		JSONObject parseObject = JSONObject.parseObject(jsonString.toJSONString());
		out.print(parseObject); 
		out.close();
	}

	private MessageResult distribute(String reqstr) throws Exception {
		ObmLog.info("接收到参数。。。" + reqstr, getClass(), "distribute");
		JSONObject jsonObject = (JSONObject) JSONObject.parse(reqstr);
		String systemcode = jsonObject.getString("systemcode");
		String billtype = jsonObject.getString("billtype");
		String moduleid = jsonObject.getString("moduleid");
		if (StringUtil.isEmpty(systemcode)) {
			return new MessageResult(MessageResult.STATUS_ERROR, "异常：systemcode为空！");
		}
		if (StringUtil.isEmpty(billtype)) {
			return new MessageResult(MessageResult.STATUS_ERROR, "异常：billtype为空！"); 
		}
		DataInfoSyncDoMain domain = new DataInfoSyncDoMain();
		return domain.doDataInfoSync(billtype, jsonObject);
	}

	private void setResponseContentType(HttpServletResponse response) {
		try {
			response.setContentType("application/json; charset=" + FileConfigInfoReadFacade.getGlobalParameter().getOutputEncoding());
		} catch (FileConfigException e) {
			ObmLog.info(e.getMessage(), getClass(), "doAction");
			try {
				throw new EnvInitException(e);
			} catch (EnvInitException e1) {
				e1.printStackTrace();
			}
		}

	}

	private void checkClientAddress(String clientAddress, SysConfigInfo globalParameter) throws EnvInitException {
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
				throw new EnvInitException("-31201", NCLangResOnserver.getInstance().getStrByID("pfxx", "UPPpfxx-V50018"));
			}
		}

	}
}
