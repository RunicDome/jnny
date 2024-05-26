package nc.itf.portal.utils;

import java.io.IOException;
import java.io.PrintWriter;
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
import nc.vo.pub.BusinessException;
import nc.ws.intf.HttpClient;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONObject;

@SuppressWarnings("restriction")
public class UpdPortalDataImpl implements IHttpServletAdaptor {
	private HYPubBO hyPubBO;

	public HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	public void setHyPubBO(HYPubBO hyPubBO) {
		this.hyPubBO = hyPubBO;
	}
	@Override
	public void doAction(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		String ds = "design";
		InvocationInfoProxy.getInstance().setUserDataSource(ds);
		InvocationInfoProxy.getInstance().setGroupId("0001A1100000000001QS");// 设置默认的集团主键
		InvocationInfoProxy.getInstance().setUserCode("liuli");// 设置接口默认操作员编码
		InvocationInfoProxy.getInstance().setUserId("1001A110000000000HV8");// 设置接口默认操作员主键
		ISecurityTokenCallback tc = NCLocator.getInstance().lookup(
				ISecurityTokenCallback.class);
		byte[] token = tc.token("NCSystem".getBytes(), "pfxx".getBytes());
		NetStreamContext.setToken(token);
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charset=utf-8");
		req.setCharacterEncoding("utf-8");
		String billnos = req.getParameter("billno");// 单据编号
		JSONObject jsonMain = new JSONObject();
		if(billnos == null){
			resp.setStatus(500);
			PrintWriter writer = resp.getWriter();
			jsonMain.put("status", false);
			jsonMain.put("message", "获取单据号为空！" );
			writer.write(jsonMain.toString());
		}else{
			String[] billnoarr = billnos.split(",");
			String errmsg = "";// 错误信息
			String truemsg = "";// 正确信息
			for(String billno:billnoarr){
				// 报销单
				if(billno.length() > 3 && "264".equals(billno.substring(0,3))){
					try {
						String url = (String) new HYPubBO().findColValue("sys_config",
								"config_value", "config_key='oa-url'");
						String pk_bill = (String) getHyPubBO().findColValue("er_bxzb", "pk_jkbx", "nvl(dr,0) = " +
								"0 and djbh = '"+billno+"'");
						WorkFlowBill[] vo = (WorkFlowBill[]) new HYPubBO()
						.queryByCondition(WorkFlowBill.class,
								"pk_bill='" + pk_bill + "'");
						if(vo == null || vo.length <= 0){
							errmsg += "未获取到WorkFlowBillVO！";
						}
						String requestid = vo[0].getRequestid() + "";
						Map<String, String> params = new HashMap<>();
						params.put("requestid", requestid);
						Map<String, String> headers = new HashMap<>();
						headers.put("Content-Type", "application/x-www-form-urlencoded");
						String back = HttpClient.httpPostForm(url, params, headers,
								"utf-8");
						JSONObject res = JSONObject.fromObject(back);
						String success = res.getString("error_code");
						if (!"0".equals(success)) {
							errmsg += "["+billno+"]调用OA获取NC更新数据通知接口出错："
									+ res.getString("error_msg")+"\r\n";
						}else{
							truemsg += "["+billno+"]单据调用成功！\t";
						}
					} catch (BusinessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if(!"".equals(errmsg)){
				resp.setStatus(500);
				PrintWriter writer = resp.getWriter();
				jsonMain.put("status", false);
				jsonMain.put("message", errmsg);
				writer.write(jsonMain.toString());
			}else{
				jsonMain.put("status", true);
				jsonMain.put("message", truemsg);
			}
		}
		resp.setStatus(200);
	}

}
