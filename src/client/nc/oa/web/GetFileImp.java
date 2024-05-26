package nc.oa.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.adaptor.IHttpServletAdaptor;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.comn.NetStreamContext;
import nc.bs.framework.server.ISecurityTokenCallback;
import nc.bs.logging.Logger;
import nc.bs.pub.filesystem.IFileSystemService;
import nc.fi.arap.pubutil.RuntimeEnv;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.filesystem.NCFileNode;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.google.gdata.util.common.base.Nullable;

public class GetFileImp implements IHttpServletAdaptor {
	static BASE64Decoder decoder = new sun.misc.BASE64Decoder();
	static BASE64Encoder encoder = new sun.misc.BASE64Encoder();
	// FTP服务器的用户名
	private static String ftpName = "ncAdmin";
	// FTP服务器的密码
	private static String ftpPass = "JnrlCt1015";
	static String charset = "ISO-8859-1";

	@Override
	public void doAction(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO 自动生成的方法存根
		// 初始化NC
		String ds = "RLJT";
		InvocationInfoProxy.getInstance().setUserDataSource(ds);
		InvocationInfoProxy.getInstance().setGroupId("0001A1100000000001QS");// 设置默认的集团主键
		InvocationInfoProxy.getInstance().setUserCode("liuli");// 设置接口默认操作员编码
		InvocationInfoProxy.getInstance().setUserId("1001A110000000000HV8");// 设置接口默认操作员主键
		// 1001A110000000000HV8
		ISecurityTokenCallback tc = NCLocator.getInstance().lookup(
				ISecurityTokenCallback.class);
		byte[] token = tc.token("NCSystem".getBytes(), "pfxx".getBytes());
		NetStreamContext.setToken(token);

		// NCLocator.getInstance().lookup(ISecurityTokenCallback.class).token("NCSystem".getBytes(),"pfxx".getBytes());
		// 输出到前台
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charset=utf-8");
		req.setCharacterEncoding("utf-8");
		PrintWriter printWriter = resp.getWriter();
		// 获取数据
		// String billType = req.getParameter("billType");// 单据类型
		//String contractPk = new String(req.getParameter("contractPk").getBytes("ISO-8859-1"),"UTF-8"); // 合同主键 //
		//String fileName =req.getParameter("fileName"); // 附件名称 
		//String fileName = new String(req.getParameter("fileName").getBytes("ISO-8859-1"),"UTF-8");// 附件名称
		BaseDAO dao = new BaseDAO();
		try {
			String contractPk = new String(req.getParameter("contractPk").getBytes(
					"ISO-8859-1"), "UTF-8"); // 合同主键
			// String fileName = req.getParameter("fileName"); // 附件名称
			String fjName = new String(req.getParameter("fileName"));// 附件名称
			String filemc[] = fjName.split(";");
			for(int i = 0;i < filemc.length;i++){
				String fileName = filemc[i];// 附件名称
				String filePath = RuntimeEnv.getNCHome() + "\\TempFile\\";
				downLoadFileByftp(fileName, filePath);
				String curDate = new SimpleDateFormat("yyyyMMdd").format(new Date())
						.toString();
				String curDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date()).toString();
				String beforeSql = "insert into RL_DZHTLOGC values('" + curDateTime
						+ "','" + contractPk + "','" + fileName + "','')";
				try {
					dao.executeUpdate(beforeSql);// 记录获取后数据
				} catch (DAOException e1) {
					// TODO 自动生成的 catch 块
					e1.printStackTrace();
				}// 记录日志
				try {
					int flag = QueryFile(contractPk, fileName);
					String arr[] = fileName.split("\\.");
					if (flag == 1) {
						fileName = arr[0] + curDate + "." + arr[1];
					}
				} catch (BusinessException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
				File file = new File(filePath);
				if (!file.exists()) {
					file.mkdirs();
				}
				int upflag = Upload(contractPk, filePath, fileName,
						"1001A110000000000HV8");
				String mes = "";
				if (upflag == 0) {
					mes = "合同主键：" + contractPk + "附件名称：" + fileName + "上传成功！";
				} else {
					mes = "合同主键：" + contractPk + "附件名称：" + fileName + "上传失败！";
				}
				printWriter.write(mes);
				String sql = "insert into RL_DZHTLOG values('" + curDateTime + "','"
						+ contractPk + "','" + fileName + "','','" + mes + "')";
				try {
					dao.executeUpdate(sql);// 记录日志
				} catch (DAOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}	
		} catch (DAOException e2) {
			// TODO 自动生成的 catch 块
			e2.printStackTrace();
		}
		
		/*
		 * String contractPk = new
		 * String(req.getParameter("contractPk").getBytes
		 * ("ISO-8859-1"),"UTF-8"); // 合同主键 //String fileName =
		 * req.getParameter("fileName"); // 附件名称 String fileName = new
		 * String(req
		 * .getParameter("fileName").getBytes("ISO-8859-1"),"UTF-8");// 附件名称
		 * String fileMsg = req.getParameter("fileMsg"); // 附件数据 int filength =
		 * fileMsg.length(); //String fileMsg = new
		 * String(req.getParameter("fileMsg").getBytes("ISO-8859-1"),"UTF-8");
		 * // 附件数据 //String sa = getPDFBinary(new File("D:\\1.pdf")); //fileMsg
		 * = fileMsg.replaceAll("\r|\n", "");//去掉换行 String curDate = new
		 * SimpleDateFormat("yyyyMMdd").format(new Date()).toString(); String
		 * curDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new
		 * Date()).toString(); //String base64String = getPDFBinary(new
		 * File("清单发包合同Json说明.pdf")); //printWriter.write(base64String); BaseDAO
		 * dao = new BaseDAO(); String beforeSql =
		 * "insert into RL_DZHTLOGC values('"
		 * +curDateTime+"','"+contractPk+"','"+fileName+"','"+fileMsg+"')"; try
		 * { dao.executeUpdate(beforeSql);//记录获取后数据 } catch (DAOException e1) {
		 * // TODO 自动生成的 catch 块 e1.printStackTrace(); }//记录日志 try { int flag =
		 * QueryFile(contractPk,fileName); String arr[] = fileName.split("\\.");
		 * if(flag == 1){ fileName = arr[0]+curDate+"."+arr[1]; } } catch
		 * (BusinessException e) { // TODO 自动生成的 catch 块 e.printStackTrace(); }
		 * 
		 * String filePath = RuntimeEnv.getNCHome() + "\\TempFile\\"; File file
		 * = new File(filePath); if(!file.exists()){ file.mkdirs(); } //String
		 * sa = getPDFBinary(new File("D:\\1.pdf"));
		 * base64StringToPDF(fileMsg,filePath+fileName); //单据主键,文件路径,文件名,创建人
		 * @return 0表示上传成功,-1表示失败 int upflag =
		 * Upload(contractPk,filePath,fileName,"1001A110000000000HV8"); String
		 * mes = ""; if(upflag == 0){ mes =
		 * "合同主键："+contractPk+"附件名称："+fileName+"上传成功！"; }else{ mes =
		 * "合同主键："+contractPk+"附件名称："+fileName+"上传失败！"; }
		 * printWriter.write(mes); String sql =
		 * "insert into RL_DZHTLOG values('"
		 * +curDateTime+"','"+contractPk+"','"+fileName
		 * +"','"+fileMsg+"','"+mes+"')"; try { dao.executeUpdate(sql);//记录日志 }
		 * catch (DAOException e) { // TODO 自动生成的 catch 块 e.printStackTrace(); }
		 */
	}

	/**
	 * 将base64编码转换成PDF
	 * 
	 * @param base64String
	 *            1.使用BASE64Decoder对编码的字符串解码成字节数组
	 *            2.使用底层输入流ByteArrayInputStream对象从字节数组中获取数据；
	 *            3.建立从底层输入流中读取数据的BufferedInputStream缓冲输出流对象；
	 *            4.使用BufferedOutputStream和FileOutputSteam输出数据到指定的文件中
	 */
	public static void base64StringToPDF(String base64String, String filePath) {
		BufferedInputStream bin = null;
		FileOutputStream fout = null;
		BufferedOutputStream bout = null;
		try {
			// 将base64编码的字符串解码成字节数组
			byte[] bytes = decoder.decodeBuffer(base64String);
			// apache公司的API
			// byte[] bytes = Base64.decodeBase64(base64sString);
			// 创建一个将bytes作为其缓冲区的ByteArrayInputStream对象
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			// 创建从底层输入流中读取数据的缓冲输入流对象
			bin = new BufferedInputStream(bais);
			// 指定输出的文件
			File file = new File(filePath);
			// 创建到指定文件的输出流
			fout = new FileOutputStream(file);
			// 为文件输出流对接缓冲输出流对象
			bout = new BufferedOutputStream(fout);

			byte[] buffers = new byte[1024];
			int len = bin.read(buffers);
			while (len != -1) {
				bout.write(buffers, 0, len);
				len = bin.read(buffers);
			}
			// 刷新此输出流并强制写出所有缓冲的输出字节，必须这行代码，否则有可能有问题
			bout.flush();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bin.close();
				fout.close();
				bout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 防止重复上传相同名称的附件
	 * 
	 * @param billpk
	 *            单据主键
	 * @param filename
	 *            文件名
	 * @throws nc.vo.pub.BusinessException
	 * @throws nc.vo.pub.BusinessException
	 */
	@SuppressWarnings("unchecked")
	public int QueryFile(String billpk, String filename)
			throws BusinessException {
		int flag = 0;
		try {
			String path = billpk + "/" + filename;
			BaseDAO dao = new BaseDAO();
			String sql = "select filepath from sm_pub_filesystem where filepath = '"
					+ path + "'";
			List<Object[]> filelist = (List<Object[]>) dao.executeQuery(sql,
					new ArrayListProcessor());
			if (filelist != null && filelist.size() > 0) {// 如果有附件
				flag = 1;
				/*
				 * IFileSystemService service = (IFileSystemService) NCLocator
				 * .getInstance().lookup(IFileSystemService.class); String[] arg
				 * = new String[] { path }; service.deleteNCFileNodes(arg);
				 */
			}
		} catch (DAOException e) {
			Logger.debug(e.getMessage());
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 文件上传
	 * 
	 * @param parentPath
	 *            单据主键pk_jkbx
	 * @param newFile
	 *            文件路径
	 * @param filename
	 *            文件名
	 * @param user_code
	 *            创建人
	 * @return 0表示上传成功,-1表示失败
	 */
	@SuppressWarnings("hiding")
	public int Upload(String parentPath, String filepath, String filename,
			String user_code) {
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BusinessException e) {
			e.printStackTrace();
		} finally {
			try {
				fileinput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (node == null) {
			return -1;
		}
		return 0;
	}

	public static String doPost(String httpUrl, @Nullable String param) {
		StringBuffer result = new StringBuffer();
		// 连接
		HttpURLConnection connection = null;
		OutputStream os = null;
		InputStream is = null;
		BufferedReader br = null;
		try {
			// 创建连接对象
			URL url = new URL(httpUrl);
			// 创建连接
			connection = (HttpURLConnection) url.openConnection();
			// 设置请求方法
			connection.setRequestMethod("POST");
			// 设置连接超时时间
			connection.setConnectTimeout(15000);
			// 设置读取超时时间
			connection.setReadTimeout(15000);
			// DoOutput设置是否向httpUrlConnection输出，DoInput设置是否从httpUrlConnection读入，此外发送post请求必须设置这两个
			// 设置是否可读取
			connection.setDoOutput(true);
			connection.setDoInput(true);
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			connection.setRequestProperty("Content-Type",
					"text/html;charset=utf-8");

			// 拼装参数
			if (null != param && param.equals("")) {
				// 设置参数
				os = connection.getOutputStream();
				// 拼装参数
				os.write(param.getBytes("UTF-8"));
			}
			// 设置权限
			// 设置请求头等
			// 开启连接
			// connection.connect();
			// 读取响应
			if (connection.getResponseCode() == 200) {
				is = connection.getInputStream();
				if (null != is) {
					br = new BufferedReader(new InputStreamReader(is, "GBK"));
					String temp = null;
					while (null != (temp = br.readLine())) {
						result.append(temp);
						result.append("\r\n");
					}
				}
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭连接
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// 关闭连接
			connection.disconnect();
		}
		return result.toString();
	}

	public static String getPDFBinary(File file) {
		FileInputStream fin = null;
		BufferedInputStream bin = null;
		ByteArrayOutputStream baos = null;
		BufferedOutputStream bout = null;
		try {
			// 建立读取文件的文件输出流
			fin = new FileInputStream(file);
			// 在文件输出流上安装节点流（更大效率读取）
			bin = new BufferedInputStream(fin);
			// 创建一个新的 byte 数组输出流，它具有指定大小的缓冲区容量
			baos = new ByteArrayOutputStream();
			// 创建一个新的缓冲输出流，以将数据写入指定的底层输出流
			bout = new BufferedOutputStream(baos);
			byte[] buffer = new byte[1024];
			int len = bin.read(buffer);
			while (len != -1) {
				bout.write(buffer, 0, len);
				len = bin.read(buffer);
			}
			// 刷新此输出流并强制写出所有缓冲的输出字节，必须这行代码，否则有可能有问题
			bout.flush();
			byte[] bytes = baos.toByteArray();
			// sun公司的API
			return encoder.encodeBuffer(bytes).trim();
			// apache公司的API
			// return Base64.encodeBase64String(bytes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fin.close();
				bin.close();
				// 关闭 ByteArrayOutputStream 无效。此类中的方法在关闭此流后仍可被调用，而不会产生任何
				// IOException
				// baos.close();
				bout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 通过FTP方式获得文件
	 * 
	 * @param fileName
	 *            要取的文件的文件名
	 * @param localDir
	 * 取到之后存在本地的目录（不含文件名）
	 * @return
	 * @throws DAOException 
	 */
	@SuppressWarnings("unchecked")
	private static boolean downLoadFileByftp(String fileName, String localDir) throws DAOException {
		FTPClient ftpClient = new FTPClient();
		BaseDAO dao = new BaseDAO();
		String selsql = "SELECT * FROM RL_CBSNAME WHERE ID = 31";
		List<Object[]> sels = (List<Object[]>) dao.executeQuery(selsql,
				new ArrayListProcessor());
		String ftpIp = sels.get(0)[1] + "";
		int ftpPort = Integer.parseInt(sels.get(0)[2] + "");
		Logger.error("服务器IP："+ftpIp + "》服务器端口号："+ftpPort);
		try {
			// 连接服务器登录服务器
			ftpClient.connect(ftpIp, ftpPort);
			// ftpClient.login(ftpName, ftpPass);
			//ftpClient.login("anonymous", null);// 匿名访问
			ftpClient.login(ftpName, ftpPass);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			// 是否成功登录服务器
			int replyCode = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(replyCode)) {
				Logger.error("connect failed...ftp:" + ftpIp);
				return false;
			} else {
				Logger.error("connect successfully...ftp:" + ftpIp);
				// 更改当前工作目录为文件所在的目录
				ftpClient.enterLocalPassiveMode();
				ftpClient.changeWorkingDirectory("/");
				FileOutputStream fos = new FileOutputStream(new File(localDir
						+ File.separator + fileName));// 文件下载后放置的路径
				String targetFile = new String(fileName.getBytes("GBK"),
						"ISO-8859-1");// 文件在FTP服务器上的路径
				Logger.error("targetFile==="+targetFile);
				boolean success = ftpClient.retrieveFile(targetFile, fos);
				System.out.println(ftpClient.getReplyString());
				fos.close();// 关闭路径
				ftpClient.logout();
				ftpClient.disconnect();// 关闭连接
				return success;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
