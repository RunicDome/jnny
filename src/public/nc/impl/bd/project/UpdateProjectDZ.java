package nc.impl.bd.project;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.bs.dao.DAOException;
import nc.bs.framework.adaptor.IHttpServletAdaptor;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.comn.NetStreamContext;
import nc.bs.framework.server.ISecurityTokenCallback;
import nc.ui.pcm.utils.GetDao;
import net.sf.json.JSONObject;

@SuppressWarnings("restriction")
public class UpdateProjectDZ implements IHttpServletAdaptor {
	@Override
	public void doAction(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
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
		JSONObject json = new JSONObject();
		// 获取传输数据：合同主键（contractPk）
		String pk_project = req.getParameter("pk_project");// 项目主键
		String def26 = req.getParameter("rec_date");// 项目主键
		String updsql = "UPDATE BD_PROJECT SET DEF17 = '1001A2100000000B68C1',MODIFIER = " +
				"'1001A110000000000HV8',MODIFIEDTIME = '"+getCurrentDate()+"' WHERE PK_PROJECT = " +
						"'"+pk_project+"'";
		if(def26 != null){
			updsql = "UPDATE BD_PROJECT SET DEF17 = '1001A2100000000B68C1',DEF26 = '"+def26+"',MODIFIER = " +
					"'1001A110000000000HV8',MODIFIEDTIME = '"+getCurrentDate()+"' WHERE PK_PROJECT = " +
							"'"+pk_project+"'";
		}
		try {
			getDao.executeUpdate(updsql);
			resp.setStatus(200);
			PrintWriter writer = resp.getWriter();
			json.put("status", true);
			json.put("mes", "项目主键["+pk_project+"]更新是否完成对账成功！");
			writer.write(json.toString());
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			resp.setStatus(500);
			PrintWriter writer = resp.getWriter();
			json.put("status", false);
			json.put("mes", "项目主键["+pk_project+"]更新是否完成对账失败！");
			writer.write(json.toString());
			e.printStackTrace();
		}
	}
	// 取当前系统时间
	public static String getCurrentDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		return sdf.format(date);
	}
}
