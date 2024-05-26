package nc.impl.arap.file;

import java.util.HashMap;
import java.util.Map;

import nc.bs.logging.Logger;
import nc.itf.arap.file.IArapServiceForFilePreview;
import nc.ws.intf.HttpClient;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;

public class ArapServiceForFilePreviewImpl implements
		IArapServiceForFilePreview {
	public static String YZUrl = "http://172.18.130.125:8080/fcscloud/composite/httpfile";

	@Override
	public JSONObject getYZViewUrl(JSONObject json) {
		// TODO Auto-generated method stub
		JSONObject rtnData = new JSONObject();
		String fileUrl = json.getString("urlstring");// 附件路径
		String fileName = json.getString("filename");// 附件名称
		if (StringUtils.isEmpty(fileUrl) || StringUtils.isEmpty(fileName)) {
			rtnData.put("success", Boolean.FALSE);
			rtnData.put("mes", "附件信息为空！");
			return rtnData;
		}
		String[] fileTypes = fileName.split("\\.");
		String fileType = fileTypes[1];
		String convertType = null;
		// 入参convertType的选择：
		// 0：word，excel，ppt转html；
		// 20：pdf 转html；
		// 21：ofd格式的转html;
		// 23：图片转html；
		if (fileType.indexOf("doc") != -1 || fileType.indexOf("xls") != -1
				|| fileType.indexOf("ppt") != -1) {
			convertType = "0";
		} else if (fileType.indexOf("pdf") != -1) {
			convertType = "20";
		} else if (fileType.indexOf("ofd") != -1) {
			convertType = "21";
		} else {
			convertType = "23";
		}
		String time = "600";// 过期时间
		String htmlTitle = fileTypes[0];// 浏览器显示标题
		String htmlName = fileTypes[0];// 文档标题
		// 调用接口返回
		Map<String, String> params = new HashMap<>();
		params.put("convertType", convertType);
		params.put("time", time);
		params.put("fileUrl", fileUrl);
		params.put("htmlName", htmlName);
		params.put("htmlTitle", htmlTitle);
		params.put("isDownload", "1");
		params.put("noCache", "1");
		params.put("convertType", convertType);
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");

		// 调用神思永中附件预览接口
		String back = HttpClient.httpPostForm(YZUrl, params, headers, "utf-8");
		JSONObject rtnjson = JSONObject.parseObject(back);
		if (rtnjson != null && rtnjson.getInteger("errorcode") == 0) {
			String viewUrl = rtnjson.getJSONObject("data").getString("viewUrl");
			rtnData.put("success", Boolean.TRUE);
			rtnData.put("viewUrl", viewUrl);
			return rtnData;
		} else {
			rtnData.put("success", Boolean.FALSE);
			rtnData.put("mes", "获取永中附件URL信息失败" + json.getString("msg"));
			Logger.error("获取永中附件URL信息失败：" + json.getString("msg"));
			return rtnData;
		}
	}
}
