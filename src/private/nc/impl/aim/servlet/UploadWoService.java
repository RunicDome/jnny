package nc.impl.aim.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.bs.framework.adaptor.IHttpServletAdaptor;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.comn.NetStreamContext;
import nc.bs.framework.server.ISecurityTokenCallback;
import nc.bs.trade.business.HYPubBO;
import nc.itf.ewm.prv.IWorkOrderService;
import nc.itf.pubapp.pub.smart.IBillQueryService;
import nc.ui.pcm.utils.GetDao;
import nc.vo.ewm.workorder.AggWorkOrderVO;
import nc.vo.ewm.workorder.WOHisVO;
import nc.vo.ewm.workorder.WorkOrderHeadVO;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.ws.intf.BillLogVO;

import org.apache.commons.lang.StringUtils;

import uap.iweb.log.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

// 对接神思系统，上传工单
@SuppressWarnings({ "rawtypes", "unchecked", "restriction" })
public class UploadWoService implements IHttpServletAdaptor {
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);

	@Override
	public void doAction(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		setToken(req, resp);
		// 获取参数
		BufferedReader br = req.getReader();
		String body = "";
		String line = null;
		while ((line = br.readLine()) != null) {
			body = body + line;
		}
		Debug.error("=============upload Wo msg ：" + body);
		// 上传工单
		String request = upload(body);
		// 保存日志
		try {
			request += SaveBillLog(body, request);
		} catch (BusinessException e) {
			e.printStackTrace();
			Logger.error("保存日志报错：" + e.getMessage());
		}
		// 返回参数
		resp.setStatus(200);
		resp.getWriter().write(request);
	}

	private String upload(String body) {
		// TODO Auto-generated method stub
		InvocationInfoProxy.getInstance().setGroupId("0001A1100000000001QS");
		String errorMsg = "";
		try {
			JSONObject jsonObj = JSONObject.parseObject(body);
			JSONArray arr = jsonObj.getJSONArray("data");
			for (Object woObj : arr) {
				if (woObj instanceof JSONObject) {
					JSONObject woJson = (JSONObject) woObj;
					String checkError = checkParam(woJson);
					if (checkError == null) {
						String pk_work_apply = woJson
								.getString("pk_work_apply");// 工单主键
						String task_progress = woJson
								.getString("task_progress");// 工作情况
						String service_step_desc = woJson
								.getString("service_step_desc");// 维修措施说明
						AggWorkOrderVO woAggvo = getWoAggvo(pk_work_apply);
						WorkOrderHeadVO hvo = woAggvo.getParentVO();
						// 工单
						if ("4B36-01".equals(hvo.getTransi_type())) {
							if (StringUtils.isEmpty(task_progress)) {
								errorMsg += "["+pk_work_apply+"]工单类型为工单时，工作情况不能为空！";
								continue;
							}
							hvo.setTask_progress(task_progress);
						} else if ("4B36-03".equals(hvo.getTransi_type())) {
							// 维修工单
							if (StringUtils.isEmpty(service_step_desc)) {
								errorMsg += "["+pk_work_apply+"]工单类型为维修工单时，维修措施说明不能为空！";
								continue;
							}
							hvo.setService_step_desc(service_step_desc);
						}
						hvo.setStatus(VOStatus.UPDATED);
						new HYPubBO().update(hvo);
						IWorkOrderService itf = NCLocator.getInstance().lookup(
								IWorkOrderService.class);
						itf.updateWorkOrderStatus(getWoAggvo(pk_work_apply),
								getWOHisVO(), Boolean.TRUE);
					} else {
						errorMsg += checkError;
						continue;
					}
				} else {
					errorMsg += body + "解析失败！";
					continue;
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			errorMsg += "工单上传失败！系统出现内部错误！错误信息：" + e.getMessage();
		}
		if (StringUtils.isNotEmpty(errorMsg)) {
			return createMsg(false, "-1", errorMsg);
		} else {
			return createMsg(true, "0", errorMsg);
		}
	}

	private WOHisVO getWOHisVO() {
		WOHisVO woHisVO = new WOHisVO();
		woHisVO.setWo_statustype(1);
		woHisVO.setPk_wostatus("0001A110000000000HNK");
		return woHisVO;
	}

	// 校验参数
	private String checkParam(JSONObject woJson) {
		// TODO Auto-generated method stub
		String err = "Json解析失败，请检查参数！";
		try {
			if (woJson.getString("pk_work_apply") == null
					|| (woJson.getString("task_progress") == null && woJson
							.getString("service_step_desc") == null)) {
				return err;
			}
			String pk_work_apply = woJson.getString("pk_work_apply");
			AggWorkOrderVO aggvo = getWoAggvo(pk_work_apply);
			if (aggvo == null) {
				return "工单主键[" + pk_work_apply + "]查询失败！";
			}
			WorkOrderHeadVO hvo = aggvo.getParentVO();
			if (hvo.getTask_progress() != null
					|| hvo.getService_step_desc() != null) {
				return "工单主键[" + pk_work_apply + "]已完成上报！";
			}
		} catch (Exception e) {
			// TODO: handle exception
			return "Json解析失败，请检查参数！";
		}
		return null;
	}

	public void setToken(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String ds = "RLJT";
		// String ds = "design";
		InvocationInfoProxy.getInstance().setUserDataSource(ds);
		InvocationInfoProxy.getInstance().setGroupId("0001A1100000000001QS");// 设置默认的集团主键
		InvocationInfoProxy.getInstance().setUserCode("liuli");// 设置接口默认操作员编码
		InvocationInfoProxy.getInstance().setUserId("1001A110000000000HV8");// 设置接口默认操作员主键
		ISecurityTokenCallback tc = NCLocator.getInstance().lookup(
				ISecurityTokenCallback.class);
		byte[] token = tc.token("NCSystem".getBytes(), "pfxx".getBytes());
		NetStreamContext.setToken(token);
		req.setCharacterEncoding("utf-8");
		setResponseContentType(resp);
	}

	private void setResponseContentType(HttpServletResponse resp) {
		resp.setContentType("application/json; charset=utf-8");
	}

	// 记录日志
	private String SaveBillLog(String recdata, String rdtdata)
			throws BusinessException {
		// TODO Auto-generated method stub
		BillLogVO vo = new BillLogVO();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		vo.setCreationtime(df.format(new Date()));// 创建时间
		vo.setTransi_type("WO"); // 交易类型
		vo.setRecdata(recdata);// 接收参数
		vo.setRdtdata(rdtdata);// 返回参数
		vo.setPk_group("0001A1100000000001QS");
		try {
			new HYPubBO().insert(vo);
		} catch (BusinessException e) {
			// TODO: handle exception
			Logger.error("生成工单日志表保存失败：" + e.getMessage());
		}
		return "";
	}

	private String createMsg(boolean isSuccess, String code, String msg) {
		Map msgMap = new HashMap();
		msgMap.put("success", Boolean.valueOf(isSuccess));
		msgMap.put("code", code);
		msgMap.put("msg", msg);
		Debug.error("上传工单" + isSuccess + "; 返回信息：" + msg);
		return JSON.toJSONString(msgMap);
	}

	private AggWorkOrderVO getWoAggvo(String pk_work_apply) {
		IBillQueryService billquery = NCLocator.getInstance().lookup(
				IBillQueryService.class);
		AggWorkOrderVO aggvo = billquery.querySingleBillByPk(
				AggWorkOrderVO.class, pk_work_apply);
		return aggvo;
	}
}
