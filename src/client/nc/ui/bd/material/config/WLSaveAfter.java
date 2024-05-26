package nc.ui.bd.material.config;

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
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.bd.material.MaterialVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.sm.UserVO;
import nc.ws.intf.DCLogVO;
import nc.ws.intf.OaWorkFlowUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

//物料档案集团推送电采按钮
@SuppressWarnings({ "unused", "restriction" })
public class WLSaveAfter extends NCAction {
	private static final long serialVersionUID = -777777777781L;

	private AbstractAppModel model;
	private IEditor editor;

	public WLSaveAfter() {
		super.setBtnName("推送电采");
	}

	public void doAction(ActionEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		if (getModel() == null || getModel().getSelectedData() == null) {
			MessageDialog.showHintDlg((Container) editor, "提示", "请先选择一行数据！");
			return;
		}
		MaterialVO wlvo = (MaterialVO) getModel().getSelectedData();// 档案信息
		String pk_material = wlvo.getPk_material().toString();// pk_material档案主键
		System.out.println("物料档案主键：" + pk_material);
		String where = " pk_bill = '" + pk_material + "' and success = 'Y'";
		DCLogVO[] dcvos = (DCLogVO[]) getHyPubBO().queryByCondition(
				DCLogVO.class, where);
		if (dcvos == null || dcvos.length <= 0) {
			JSONArray arrjson = new JSONArray();
			JSONObject wljson = new JSONObject();
			wljson.put("name", wlvo.getName().toString());// 物料名称
			wljson.put("code", wlvo.getCode().toString());// 物料编码
			// 计量单位
			String dwstr = "";
			if (null != wlvo.getPk_measdoc()) {
				dwstr = (String) getHyPubBO().findColValue(
						"bd_measdoc",
						"code",
						"nvl(dr,0) = 0 and pk_measdoc  = '"
								+ wlvo.getPk_measdoc() + "'");
			}
			wljson.put("unit", dwstr.toString());// 计量单位
			// 物料类别
			String lbstr = "";
			if (null != wlvo.getPk_marbasclass()) {
				lbstr = (String) getHyPubBO().findColValue(
						"bd_marbasclass",
						"code",
						"nvl(dr,0) = 0 and pk_marbasclass  = '"
								+ wlvo.getPk_marbasclass() + "'");
			}
			wljson.put("categoryCode", lbstr.toString());// 物料类别编码
			wljson.put("spec", wlvo.getMaterialspec().toString());// 规格
			arrjson.add(wljson);
			// 电采物料接口url
			String dcUrl = (String) getHyPubBO().findColValue("sys_config",
					"config_value", "nvl(dr,0) = 0 and config_id = 51");
			// 调用接口
			JSONObject rtndata = sendPost(dcUrl, arrjson.toString());

			if (rtndata != null) {
				Boolean success = rtndata.getBoolean("success");
				DCLogVO vo = new DCLogVO();
				vo.setBilltype("物料");
				vo.setPk_bill(pk_material);
				vo.setPk_org(wlvo.getPk_org());
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
										+ wlvo.getPk_org() + "'");
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
			MessageDialog.showHintDlg((Container) editor, "提示", "该物料档案已同步至电采平台，"
					+ "操作时间为[" + dcvos[0].getTs() + "]");
			return;
		}

	}

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

	public AbstractAppModel getModel() {
		return this.model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
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

	protected boolean isActionEnable() {
		MaterialVO vo = (MaterialVO) getModel().getSelectedData();// 档案信息
		if (null == vo) {
			return false;
		}
		return true;
	}
}
