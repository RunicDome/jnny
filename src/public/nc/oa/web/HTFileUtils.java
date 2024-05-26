package nc.oa.web;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.alibaba.fastjson.JSONObject;

public class HTFileUtils {
	public String afterApprove(String pk_primaryKey,String ifapprove){
		String url = "http://172.18.128.201:8090/zhuomaService/sendAgreeInfo";
		String retdt = "";
		try {
			retdt = updateNotice(url,pk_primaryKey,ifapprove);
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return retdt;
	}
	// 调用HTTP接口
	private static String updateNotice(String url, String pk_primaryKey,String ifapprove) throws Exception {
		// 设置通用的请求属性
		// Post请求的url，与get不同的是不需要带参数
		URL postUrl = new URL(url);
		// 打开连接
		HttpURLConnection connection = (HttpURLConnection) postUrl
				.openConnection();
		// 设置是否向connection输出，因为这个是post请求，参数要放在
		// http正文内，因此需要设为true
		connection.setDoOutput(true);
		// Read from the connection. Default is true.
		connection.setDoInput(true);
		// 默认是 GET方式
		connection.setRequestMethod("POST");
		// Post 请求不能使用缓存
		connection.setUseCaches(false);
		// 设置本次连接是否自动重定向
		connection.setInstanceFollowRedirects(true);
		// 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
		// 意思是正文是urlencoded编码过的form参数
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		// 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
		// 要注意的是connection.getOutputStream会隐含的进行connect。
		connection.connect();
		DataOutputStream out = new DataOutputStream(
				connection.getOutputStream());
		// 正文，正文内容其实跟get的URL中 '? '后的参数字符串一致
		String content = "ctId=" + URLEncoder.encode(pk_primaryKey, "UTF-8")+"&permitRelease="+ URLEncoder.encode(ifapprove, "UTF-8");
		// DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写到流里面
		out.writeBytes(content);
		// 流用完记得关
		out.flush();
		out.close();
		// 获取响应
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream(), "UTF-8"));
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.println("审批后返回结果：" + line);
			JSONObject rtdata = JSONObject.parseObject(line);
			if ("true".equals(rtdata.getString("success"))) {
				return "Y";
			} else {
				return "N";
			}
		}
		reader.close();
		// 结束,记得把连接断了
		connection.disconnect();
		return "";
	}
}
