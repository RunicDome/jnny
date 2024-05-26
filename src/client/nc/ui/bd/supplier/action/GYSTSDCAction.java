package nc.ui.bd.supplier.action;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.trade.business.HYPubBO;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.IEditor;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.bd.supplier.SupplierVO;
import nc.vo.sm.UserVO;
import nc.ws.intf.DCLogVO;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@SuppressWarnings({ "restriction" })
public class GYSTSDCAction extends NCAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AbstractUIAppModel model;
	private IEditor editor;
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
	public AbstractUIAppModel getModel() {
		return model;
	}

	public void setModel(AbstractUIAppModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}


	public GYSTSDCAction() {
		this.setCode("tuisong");
		this.setBtnName("推送电采");
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		// System.out.println(this.getModel().getSelectedData());
		if (getModel() == null || getModel().getSelectedData() == null) {
			MessageDialog.showHintDlg((Container) editor, "提示", "请先选择一行数据！");
			return;
		}
		SupplierVO gysvo = (SupplierVO) getModel().getSelectedData();// 供应商档案信息
		String pk_supplier = gysvo.getPk_supplier();// pk_supplier档案主键
		System.out.println("供应商档案主键：" + gysvo.getCode());
		String where = " pk_bill = '" + pk_supplier + "' and success = 'Y'";
		DCLogVO[] dcvos = (DCLogVO[]) getHyPubBO().queryByCondition(
				DCLogVO.class, where);
		if (dcvos == null || dcvos.length <= 0) {
			JSONArray arrjson = new JSONArray();
			JSONObject gysjson = new JSONObject();
			gysjson.put("name", gysvo.getName().toString());// 供应商名称
			gysjson.put("mdmId", gysvo.getCode().toString());// 供应商编码
			arrjson.add(gysjson);
			// 供应商物料接口url
			String dcUrl = (String) getHyPubBO().findColValue("sys_config",
					"config_value", "nvl(dr,0) = 0 and config_id = 61");
			// 调用接口
			JSONObject rtndata = sendPost(dcUrl, arrjson.toString());
			if (rtndata != null) {
				Boolean success = rtndata.getBoolean("success");
				DCLogVO vo = new DCLogVO();
				vo.setBilltype("供应商");
				vo.setPk_bill(pk_supplier);
				vo.setPk_org(gysvo.getPk_org());
				vo.setReturn_data(rtndata.toString());
				if (success == true) {
					vo.setSuccess("Y");
				} else {
					vo.setSuccess("N");
				}
				vo.setUrl(dcUrl);
				// 当前登录人ID
				String userID = InvocationInfoProxy.getInstance().getUserId();
				UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
						UserVO.class, userID);
				vo.setUserid(userID);
				vo.setUsername(userVO.getUser_name());
				// 组织
				String zzmc = (String) getHyPubBO()
						.findColValue(
								"org_orgs",
								"name",
								"nvl(dr,0) = 0 and pk_org = '"
										+ gysvo.getPk_org() + "'");
				vo.setZzmc(zzmc);
				getHyPubBO().insert(vo);
				if (success == true) {
					MessageDialog.showHintDlg((Container) editor, "提示", rtndata
							.getString("msg").toString());
				} else {
					MessageDialog.showHintDlg((Container) editor, "提示", "推送失败，"
							+ rtndata.getString("msg").toString());
				}
			} else {
				MessageDialog.showHintDlg((Container) editor, "提示",
						"推送失败，请联系用友工程师检查！");
				return;
			}
		} else {
			MessageDialog.showHintDlg((Container) editor, "提示", "该供应商档案已同步至电采平台，"
					+ "操作时间为[" + dcvos[0].getTs() + "]");
			return;
		}
	}
	
	@Override
	protected boolean isActionEnable() {
		// TODO Auto-generated method stub
		if(this.getModel().getSelectedData() == null){
			return false;
		}
		return true;
	}


	public IEditor getEditor() {
		return this.editor;
	}

	public void setEditor(IEditor editor) {
		this.editor = editor;
	}
	public static JSONObject sendPost(String url, String param) {
		OutputStreamWriter out = null;
		BufferedReader ins = null;
		JSONObject filejson = null;
		String result = "请求失败";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setRequestProperty("Content-type",
					"application/json;charset=utf-8");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);

			// 获取URLConnection对象对应的输出流
			out = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
			// 发送请求参数
			out.write(param);

			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			ins = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			String line;
			while ((line = ins.readLine()) != null) {
				result = line;
			}
			filejson = JSONObject.fromObject(result);
		} catch (Exception e) {
			// println e
			// result=["错误":e,"json":param]
			System.out.println("[POST请求]向地址：" + url + " 发送数据：" + param
					+ " 发生错误!");
			e.printStackTrace();
		} finally {// 使用finally块来关闭输出流、输入流
			try {
				if (out != null) {
					out.close();
				}
				if (ins != null) {
					ins.close();
				}
			} catch (IOException ex) {
				System.out.println("关闭流异常");
			}
		}
		return filejson;
	}
}
