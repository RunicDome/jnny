package nc.itf.file.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.bs.framework.adaptor.IHttpServletAdaptor;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.comn.NetStreamContext;
import nc.bs.framework.server.ISecurityTokenCallback;
import nc.bs.pub.filesystem.IFileSystemService;
import nc.fi.arap.pubutil.RuntimeEnv;
import nc.vo.pub.BusinessException;
import nc.vo.pub.filesystem.NCFileNode;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@SuppressWarnings({"restriction","hiding"})
public class FileUploadForBill implements IHttpServletAdaptor {
	static BASE64Decoder decoder = new sun.misc.BASE64Decoder();
	static BASE64Encoder encoder = new sun.misc.BASE64Encoder();

	public void doAction(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String ds = "RLJT";
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
		PrintWriter printWriter = resp.getWriter();
		JSONObject jsonMain = new JSONObject();
		// 获取传输数据
		String filedata = readJSONString(req);
		JSONObject filejson = JSONObject.fromObject(filedata);
		String billpk = filejson.getString("billpk");// 单据主键
		String urldt = filejson.getString("files");// 附件数据
		JSONArray urlarr = JSONArray.fromObject(urldt);// 附件数组
		String savePath = RuntimeEnv.getNCHome() + "\\TempFile\\";// 附件保存路径
		String mes = "";// 提示信息
		int fg = 0;
		for (int i = 0; i < urlarr.size(); i++) {
			JSONObject dtjson = (JSONObject) urlarr.get(i);
			String filename = dtjson.getString("filename");// 附件名称
			String fileurl = dtjson.getString("fileurl");// 附件下载地址
			try {
				downLoadFromUrl(fileurl, filename, savePath);
				String upflag = Upload(billpk, savePath, filename,
						"1001A110000000000HV8");

				if (upflag == null) {
					// mes += "合同主键：" + billpk + "附件名称：" + filename + "上传成功！\n";
				} else {
					mes += filename+" 上传失败！["+upflag+"]\n";
					fg = 1;
				}
			} catch (Exception e) {
				fg = 1;
				// TODO Auto-generated catch block
				mes += e.getMessage();
				e.printStackTrace();
			}
		}
		if (fg == 0) {
			jsonMain.put("status", true);
			jsonMain.put("message", "附件上传成功！");
			resp.setStatus(200);
		} else {
			jsonMain.put("status", false);
			jsonMain.put("message", mes);
			resp.setStatus(500);
		}
		printWriter.write(jsonMain.toString());
	}

	public static void base64ToFile(String base64, String fileName,
			String savePath) {
		// 创建文件目录
		byte[] buffer = base64.getBytes();
		FileOutputStream out;
		try {
			out = new FileOutputStream(savePath + fileName);
			out.write(buffer);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unused")
	public String Upload(String parentPath, String filepath, String filename,
			String user_code) {
		String errmsg = null;
		File file = new File(filepath + filename);
		Long fileLen = file.length();
		String creator = user_code;
		NCFileNode node = null;
		InputStream fileinput = null;
		try {
			fileinput = new FileInputStream(file);
			IFileSystemService service = (IFileSystemService) NCLocator
					.getInstance().lookup(IFileSystemService.class);
			node = service.createNewFileNodeWithStream(parentPath, filename,
					creator, fileinput, fileLen);
		} catch (FileNotFoundException e) {
			errmsg = e.getMessage();
			e.printStackTrace();
		} catch (IOException e) {
			errmsg = e.getMessage();
			e.printStackTrace();
		} catch (BusinessException e) {
			errmsg = e.getMessage();
			e.printStackTrace();
		} finally {
			try {
				fileinput.close();
			} catch (IOException e) {
				errmsg = e.getMessage();
				e.printStackTrace();
			}
		}
		return errmsg;
	}

	public static void downLoadFromUrl(String urlStr, String fileName,
			String savePath) throws IOException, Exception {

		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// 设置超时间为3秒
		conn.setConnectTimeout(3 * 1000);
		// 防止屏蔽程序抓取而返回403错误
		conn.setRequestProperty("User-Agent",
				"Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
		// 得到输入流
		Thread.sleep(500);
		InputStream inputStream = conn.getInputStream();

		// 获取自己数组
		byte[] getData = readInputStream(inputStream);
		// 文件保存位置
		File saveDir = new File(savePath);
		if (!saveDir.exists()) {
			saveDir.mkdir();
		}
		File file = new File(saveDir + File.separator + fileName);
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(getData);
		if (fos != null) {
			fos.close();
		}
		if (inputStream != null) {
			inputStream.close();

		}
		System.out.println("info:" + url + " download success");
	}

	/**
	 * 
	 * 从输入流中获取字节数组
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */

	public static byte[] readInputStream(InputStream inputStream)
			throws IOException {
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((len = inputStream.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		bos.close();
		return bos.toByteArray();
	}

	private String readJSONString(HttpServletRequest request) {
		StringBuffer json = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				json.append(line);
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return json.toString();
	}
	// 转UTF-8
	public static String toUTF8(String str) {
		if (isEmpty(str)) {
			return "";
		}
		try {
			if (str.equals(new String(str.getBytes("GB2312"), "GB2312"))) {
				str = new String(str.getBytes("GB2312"), "utf-8");
				return str;
			}
		} catch (Exception exception) {
		}
		try {
			if (str.equals(new String(str.getBytes("ISO-8859-1"), "ISO-8859-1"))) {
				str = new String(str.getBytes("ISO-8859-1"), "utf-8");
				return str;
			}
		} catch (Exception exception1) {
		}
		try {
			if (str.equals(new String(str.getBytes("GBK"), "GBK"))) {
				str = new String(str.getBytes("GBK"), "utf-8");

				return str;
			}
		} catch (Exception exception3) {
		}
		return str;
	}

	/**
	 * 判断是否为空
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		// 如果字符串不为null，去除空格后值不与空字符串相等的话，证明字符串有实质性的内容
		if (str != null && !str.trim().isEmpty()) {
			return false;// 不为空
		}
		return true;// 为空
	}
}
