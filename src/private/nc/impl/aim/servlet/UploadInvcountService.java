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
import nc.ui.pcm.utils.GetDao;
import nc.vo.ic.m4r.entity.InvCountBodyVO;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDouble;
import nc.ws.intf.BillLogVO;

import org.apache.commons.lang.StringUtils;

import uap.iweb.log.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

// 对接神思系统，上传盘点结果
@SuppressWarnings({ "rawtypes", "unchecked", "restriction" })
public class UploadInvcountService implements IHttpServletAdaptor {
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
	private static HYPubBO hypubBo = new HYPubBO();

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
		// 上传盘点记录
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
						String cspecialhid = woJson.getString("cspecialhid");// 盘点主键
						String cspecialbid = woJson.getString("cspecialbid");// 盘点明细主键
						UFDouble num = new UFDouble(woJson.getDouble("num"));// 盘点数量
						InvCountBodyVO bodyVO = getInvCountBodyVO(cspecialhid,
								cspecialbid);
						bodyVO.setNcountastnum(num);
						bodyVO.setNcountnum(num);
						bodyVO.setStatus(VOStatus.UPDATED);
						hypubBo.update(bodyVO);
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
			errorMsg += "盘点上传失败！系统出现内部错误！错误信息：" + e.getMessage();
		}
		if (StringUtils.isNotEmpty(errorMsg)) {
			return createMsg(false, "-1", errorMsg);
		} else {
			return createMsg(true, "0", errorMsg);
		}
	}

	// 校验参数
	private String checkParam(JSONObject woJson) {
		// TODO Auto-generated method stub
		String err = "参数异常，请检查！";
		try {
			if (woJson.getString("cspecialhid") == null
					|| woJson.getString("cspecialbid") == null
					|| woJson.getDouble("num") == null) {
				return err;
			}
			String cspecialhid = woJson.getString("cspecialhid");// 盘点主键
			String cspecialbid = woJson.getString("cspecialbid");// 盘点明细主键
			InvCountBodyVO bodyVO = getInvCountBodyVO(cspecialhid, cspecialbid);
			if (bodyVO == null) {
				return "盘点主键[" + cspecialhid + "],明细主键[" + cspecialbid
						+ "]查询失败！";
			}

			if (bodyVO.getNcountastnum() != null
					&& bodyVO.getNcountastnum().getDouble() != 0) {
				return "盘点主键[" + cspecialhid + "],明细主键[" + cspecialbid
						+ "]已完成！";
			}
		} catch (Exception e) {
			// TODO: handle exception
			return "Json解析失败，请检查参数！";
		}
		return null;
	}

	public void setToken(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// String ds = "RLJT";
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
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		vo.setCreationtime(df.format(new Date()));// 创建时间
		vo.setTransi_type("Invcount"); // 交易类型
		vo.setRecdata(recdata);// 接收参数
		vo.setRdtdata(rdtdata);// 返回参数
		vo.setPk_group("0001A1100000000001QS");
		try {
			hypubBo.insert(vo);
		} catch (BusinessException e) {
			// TODO: handle exception
			Logger.error("生成盘点日志表保存失败：" + e.getMessage());
		}
		return "";
	}

	private String createMsg(boolean isSuccess, String code, String msg) {
		Map msgMap = new HashMap();
		msgMap.put("success", Boolean.valueOf(isSuccess));
		msgMap.put("code", code);
		msgMap.put("msg", msg);
		Debug.error("上传盘点" + isSuccess + "; 返回信息：" + msg);
		return JSON.toJSONString(msgMap);
	}

	private InvCountBodyVO getInvCountBodyVO(String cspecialhid,
			String cspecialbid) throws BusinessException {
		InvCountBodyVO[] bvoArr = (InvCountBodyVO[]) hypubBo.queryByCondition(
				InvCountBodyVO.class, " dr = 0 and cspecialbid = '"
						+ cspecialbid + "' and cspecialhid='" + cspecialhid
						+ "'");
		if (bvoArr != null && bvoArr.length > 0) {
			return bvoArr[0];
		}
		return null;
	}
}
