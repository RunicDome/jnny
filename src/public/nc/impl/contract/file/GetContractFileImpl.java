package nc.impl.contract.file;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONArray;

import nc.bs.dao.DAOException;
import nc.bs.framework.adaptor.IHttpServletAdaptor;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.comn.NetStreamContext;
import nc.bs.framework.server.ISecurityTokenCallback;
import nc.ui.pcm.utils.GetDao;
import uap.pub.fs.client.FileStorageClient;

import com.alibaba.fastjson.JSONObject;

@SuppressWarnings({"restriction"})
public class GetContractFileImpl implements IHttpServletAdaptor {

	@Override
	public void doAction(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		JSONArray fileArr = new JSONArray();
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
		// 获取传输数据：合同主键（contractPk）
		String contractPk = req.getParameter("contractPk");// 合同主键
		String filesql = "select fh.name,fp.filelength,fp.filedesc,fh.createtime,fp.pk_doc from sm_pub_filesystem fp left join bap_fs_header fh on fp.pk_doc=fh.path"
				+ " left join bap_fs_body fb on fh.GUID = fb.headid"
				+ " where filepath like '" + contractPk + "/%'";// 完整正确的sql语句
		try {
			List<Object[]> filels = getDao.query(filesql);
			if (filels != null && filels.size() > 0) {
				for (int i = 0; i < filels.size(); i++) {
					JSONObject filejson = new JSONObject();
					filejson.put("filename", filels.get(i)[0] + "");
					filejson.put("filelength", filels.get(i)[1] + "");
					filejson.put("filedesc", filels.get(i)[2] + "");
					filejson.put("createtime", filels.get(i)[3] + "");
					String urlstring = FileStorageClient.getInstance()
							.getDownloadURL(null, filels.get(i)[4] + "",null);
					// byte data[] = urlTobyte(urlstring);
					// filejson.put("filedt", data);
					filejson.put("fileurl",urlstring);// URL
					// filejson.put("fileurl", urlstring.replaceAll("false", "true"));
					fileArr.put(filejson);
				}
			}
			resp.setStatus(200);
			PrintWriter writer = resp.getWriter();
			writer.write(fileArr.toString());
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
