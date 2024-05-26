package nc.sso.bs;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import nc.bs.framework.adaptor.IHttpServletAdaptor;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.server.ISecurityTokenCallback;
import nc.bs.logging.Logger;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.log.LfwLogger;
import net.sf.json.JSONObject;
import sun.misc.BASE64Encoder;
/**
 * OA单点登录到NC-Portal系统，获取密令
 * @author Xubx
 * @date 2023年03月21日
 */
@SuppressWarnings("restriction")
public class OASSOPortal extends HttpServlet implements IHttpServletAdaptor{
	// 加解密统一使用的编码方式
	@SuppressWarnings("unused")
	private final static String encoding = "utf-8";
	private static final long serialVersionUID = 1L;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doAction(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doAction(req, resp);
	}

	public OASSOPortal(){}

	public void doAction(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		JSONObject jsonMain = new JSONObject();
		Logger.error("获取ip");	
		StringBuffer url1 = request.getRequestURL();
		PrintWriter pw = response.getWriter();
		Logger.error("ip获取结束"+url1);	
		String ssoKey = "";
		String usercode = request.getParameter("userid");
		if(StringUtils.isEmpty(usercode)){
			jsonMain.put("status", false);
			jsonMain.put("message", "获取用户为空！");
			pw.write(jsonMain.toString());
			return ;
		}
		
		//指定数据源为hgjt
		String ds=LfwRuntimeEnvironment.getServerConfig().get("portalds");
		Logger.error("指定数据源为:"+ds);	
		InvocationInfoProxy.getInstance().setUserDataSource(ds);
		ISecurityTokenCallback sc = (ISecurityTokenCallback) NCLocator.getInstance().lookup(ISecurityTokenCallback.class);
		sc.token("NCSystem".getBytes(), "pfxx".getBytes());
		try {
			Logger.error("生成ssoKey");
			ssoKey = registKey(usercode, System.currentTimeMillis(),
					LfwRuntimeEnvironment.getServerConfig().get("MKEY"));
		} catch (Exception ex) {
			Logger.error(ex.getMessage(), ex);
			jsonMain.put("status", false);
			jsonMain.put("message", "ssoKey regist failed!" + ex.getMessage());
			pw.write(jsonMain.toString());
			return;
		}
		if (null == ssoKey || ssoKey.length() == 0) {
			jsonMain.put("status", false);
			jsonMain.put("message", "can't regist to nc,login failed!");
			return;
		}
		jsonMain.put("status", false);
		jsonMain.put("message", "获取成功");
		jsonMain.put("ssoKey", ssoKey);
		pw.print(ssoKey);
	}

	private String registKey(String userid, long timeSpan, String mKey)
			throws Exception {
		try {
			int TSLen = 6;
			try {
				TSLen = Integer.valueOf(LfwRuntimeEnvironment.getServerConfig()
						.get("TSLEN"));
			} catch (Exception ex) {

			}
			String time = (timeSpan + "").substring(0, TSLen);
			String key = userid + time + mKey;
			byte[] codes = MessageDigest.getInstance("SHA-1").digest(
					key.getBytes("UTF-8"));
			return new BASE64Encoder().encode(codes).replace("+", "A"); 
		} catch (Exception e) {
			LfwLogger.error(e);
		}
		return null;
	}

	public static String getURLContent(String url, String encoding) {
		StringBuffer content = new StringBuffer();
		try {
			// 新建URL对象
			URL u = new URL(url);
			InputStream in = new BufferedInputStream(u.openStream());
			InputStreamReader theHTML = new InputStreamReader(in, encoding);
			int c;
			while ((c = theHTML.read()) != -1) {
				content.append((char) c);
			}
		}
		// 处理异常
		catch (MalformedURLException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
		return content.toString();
	}

}
