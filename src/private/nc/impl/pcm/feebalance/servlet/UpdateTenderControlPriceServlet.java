package nc.impl.pcm.feebalance.servlet;

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
import nc.ui.pcm.utils.GetDao;
import nc.vo.logging.Debug;
import nc.vo.pcm.feebalance.FeeBalanceBodyVO;
import nc.vo.pcm.feebalance.FeeBalanceHeadVO;
import nc.vo.pub.BusinessException;
import nc.ws.intf.BillLogVO;
import uap.iweb.log.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

// 控制价委托申请单 OA回传招标控制价审定金额，自由态不可更新
@SuppressWarnings({ "rawtypes", "unchecked", "restriction" })
public class UpdateTenderControlPriceServlet implements IHttpServletAdaptor {
	private static HYPubBO hypubBo = new HYPubBO();
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
		Debug.error("=============upload KZJWTSQ msg ：" + body);
		// 上传控制价委托申请回写金额结果
		String request = upload(body);
		// 保存日志
		try {
			request += SaveBillLog(body, request);
		} catch (BusinessException e) {
			e.printStackTrace();
			request += "保存日志报错：" + e.getMessage();
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
			String checkError = checkParam(body);
			if (checkError == null) {
				JSONObject jsonObj = JSONObject.parseObject(body);
				JSONArray arr = jsonObj.getJSONArray("data");
				for (Object inspObj : arr) {
					if (inspObj instanceof JSONObject) {
						JSONObject inspJson = (JSONObject) inspObj;
						// 操作时间
						SimpleDateFormat df = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						String updSql = "update pm_feebalance_b set def8 = '"
								+ inspJson.getString("def8") + "',ts = '"
								+ df.format(new Date())
								+ "' where pk_feebalance_b = '"
								+ inspJson.getString("pk_feebalance_b") + "';";
						getDao.executeUpdate(updSql);
					} else {
						errorMsg += body + "解析失败！";
						return createMsg(false, "-1", errorMsg);
					}
				}
				return createMsg(true, "0", errorMsg);
			} else {
				errorMsg += checkError;
			}
		} catch (Exception e) {
			System.out.println(e);
			errorMsg += "控制价委托申请单回写错误，错误信息：" + e.getMessage();
		}
		return createMsg(false, "-1", errorMsg);
	}

	// 校验参数
	private String checkParam(String body) {
		// TODO Auto-generated method stub
		String err = "Json解析失败，请检查参数！";
		try {
			JSONObject jsonObj = JSONObject.parseObject(body);
			JSONArray arr = jsonObj.getJSONArray("data");
			for (Object inspObj : arr) {
				if (inspObj instanceof JSONObject) {
					JSONObject inspJson = (JSONObject) inspObj;
					if (inspJson.getString("pk_feebalance_b") == null
							|| inspJson.getString("def8") == null) {
						return err;
					}
					String pk_feebalance_b = inspJson
							.getString("pk_feebalance_b");
					FeeBalanceBodyVO bvo = (FeeBalanceBodyVO) hypubBo
							.queryByPrimaryKey(FeeBalanceBodyVO.class,
									pk_feebalance_b);
					if (bvo == null) {
						return "控制价委托申请明细主键[" + pk_feebalance_b + "]查询失败！";
					}
					FeeBalanceHeadVO hvo = (FeeBalanceHeadVO) hypubBo
							.queryByPrimaryKey(FeeBalanceHeadVO.class,
									bvo.getPk_feebalance());
					if (!"4D83-Cxx-84".equals(hvo.getTransi_type())) {
						return "单据号：" + hvo.getBill_code() + "的交易类型为："
								+ hvo.getTransi_type() + "，不为控制价委托申请单！";
					}
				} else {
					return err;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			Logger.error(body + "解析异常！");
			return err;
		}
		return null;
	}

	public void setToken(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String ds = "design";
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
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		vo.setCreationtime(df.format(new Date()));// 创建时间
		vo.setTransi_type("4D83-Cxx-84"); // 交易类型
		vo.setRecdata(recdata);// 接收参数
		vo.setRdtdata(rdtdata);// 返回参数
		vo.setPk_group("0001A1100000000001QS");
		try {
			new HYPubBO().insert(vo);
		} catch (BusinessException e) {
			// TODO: handle exception
			return "生成控制价委托申请单回写金额日志表保存失败：" + e.getMessage();
		}
		return "";
	}

	private String createMsg(boolean isSuccess, String code, String msg) {
		Map msgMap = new HashMap();
		msgMap.put("success", Boolean.valueOf(isSuccess));
		msgMap.put("code", code);
		msgMap.put("msg", msg);
		Debug.error("上传巡检结果记录" + isSuccess + "; 返回信息：" + msg);
		return JSON.toJSONString(msgMap);
	}
}
