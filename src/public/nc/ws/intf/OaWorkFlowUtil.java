package nc.ws.intf;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.holders.StringHolder;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.trade.business.HYPubBO;
import nc.ui.pcm.utils.GetDao;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.hrss.pub.FileNodeVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.sm.UserVO;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import uap.pub.fs.client.FileStorageClient;
import weaver.rsa.security.RSA;

import com.tchzt.tims.webservice.ContentInfoServiceSoapBindingStub;


@SuppressWarnings({ "restriction", "unused", "rawtypes" })
public class OaWorkFlowUtil {

	private static String YXURL = "http://172.18.128.94:2333/webShowImage/";
	/* 热力OA地址 */
	public static String URL_RL = "http://172.18.128.41:8082";
	/* 能投OA地址 */
	public static String URL_NT = "http://172.18.159.12:70";
	public static HYPubBO hyPubBO;

	public static String APPID = "EEAA5436-7577-4BE0-8C6C-89E9D88805EA";

	// OA中台
	public static String ZTAPPID = "EEAA5436-7577-4BEO-8c6c-89E9D88805EA";

	/* 热力OA登录用户Id */
	public static String USER_CODE = "15769";
	/* 能投OA登录用户Id */
	public static String USER_CODE_NT = "1160";

	public static Integer sendOaData(JSONArray mainData, JSONArray bodyData,
			WorkFlowBill workFlowBill) throws BusinessException {
		if (!"2".equals(workFlowBill.getBill_status())) {
			String oaUrl = (String) new HYPubBO().findColValue("org_orgs",
					"def1",
					"nvl(dr,0) = 0 and pk_org='" + workFlowBill.getPk_org()
							+ "'");
			if ("ZT".equals(workFlowBill.getDef3())) {
				oaUrl = (String) new HYPubBO().findColValue("sys_config",
						"config_value", "nvl(dr,0) = 0 and config_id = 50");
			}
			if (oaUrl != null) {
				String url = oaUrl + "/api/workflow/paService/doCreateRequest";
				Map<String, String> inMap = new HashMap<String, String>();
				if (null != workFlowBill.getPk_workflow_bill()) {
					url = oaUrl + "/api/workflow/paService/submitRequest";
					inMap.put("requestId", workFlowBill.getRequestid() + "");
				}
				getFiles(mainData, workFlowBill);
				if (null != workFlowBill.getPk_bill()) {
					Map<String, Object> pk_bill = new HashMap<String, Object>();
					pk_bill.put("fieldName", "ncdjzj");
					pk_bill.put("fieldValue", workFlowBill.getPk_bill());
					mainData.add(pk_bill);
				}
				// 通过人员身份证号和NC单位主键获取OA人员id
				String usd = "";
				if (getOAID(workFlowBill.getDef5(), workFlowBill.getPk_org()) != null
						&& !"".equals(getOAID(workFlowBill.getDef5(),
								workFlowBill.getPk_org()))) {
					Map<String, Object> usid = new HashMap<String, Object>();
					usid.put("fieldName", "usid");
					usid.put(
							"fieldValue",
							getOAID(workFlowBill.getDef5(),
									workFlowBill.getPk_org()));
					usd = getOAID(workFlowBill.getDef5(),
							workFlowBill.getPk_org());
					mainData.add(usid);
				}
				inMap.put(
						"mainData",
						mainData.toString()
								.replace("127.0.0.1:8066", "172.18.128.32:8080")
								.replace("117.0.0.1:8066", "172.18.128.32:8080")
								.toString());
				String day = UFDate.getDate(new Date()).getYear() + "-"
						+ UFDate.getDate(new Date()).getStrMonth() + "-"
						+ UFDate.getDate(new Date()).getStrDay();
				inMap.put("requestName", workFlowBill.getWorkflowName() + "_"
						+ workFlowBill.getUser_name() + "_" + day);
				inMap.put("workflowId",
						workFlowBill.getWorkflowId().replace(" ", ""));
				Map<String, String> otherParams = new HashMap<String, String>();
				otherParams.put("isnextflow", "1");
				otherParams.put("delReqFlowFaild", "1");
				otherParams.put("isVerifyPer", "0");
				JSONObject otherObject = JSONObject.fromObject(otherParams);
				inMap.put("otherParams", otherObject.toString());

				if (null != bodyData) {
					inMap.put("detailData", bodyData.toString());
				}
				JSONObject jsonObject = JSONObject.fromObject(inMap);
				// getHeads(workFlowBill, oaUrl, usd);
				// throw new BusinessException(inMap.toString());
				String back = HttpClient.httpPostForm(url, inMap,
						getHeads(workFlowBill, oaUrl, usd), "utf-8");
				JSONObject res = JSONObject.fromObject(back);
				String code = res.getString("code");
				if (!"SUCCESS".equals(code)) {
					Logger.error("调用OA接口报错，单据主键："
							+ workFlowBill.getPrimaryKey());
					Logger.error("返回错误信息：" + back);
					Logger.error("生成报文信息：" + jsonObject.toString());
					// throw new BusinessException("调用OA接口报错，请联系OA管理员，错误信息：" +
					// back);
					throw new BusinessException("调用OA接口报错，请联系OA管理员，错误信息："
							+ back);
				}
				JSONObject data = res.getJSONObject("data");
				if (data.containsKey("requestid")) {
					Integer requestid = data.getInt("requestid");
					workFlowBill.setRequestid(requestid);
				}
				workFlowBill.setSend_data(jsonObject.toString());
				// workFlowBill.setReturn_data(back);
				workFlowBill.setDef1(null);
				if (null == workFlowBill.getPk_workflow_bill()) {
					new HYPubBO().insert(workFlowBill);
				} else {
					new HYPubBO().update(workFlowBill);
				}
				// getOALog(workFlowBill, dt, back);// 记录OA日志 XBX
				return workFlowBill.getRequestid();
			}
		}
		return workFlowBill.getRequestid();
	}

	private static void getOALog(WorkFlowBill workFlowBill, String inMap,
			String back) throws BusinessException {
		// TODO Auto-generated method stub
		OALogVO vo = new OALogVO();
		vo.setRequestid(workFlowBill.getRequestid() + "");
		vo.setBill_code(workFlowBill.getDef4());// 单据编号
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		vo.setCreationtime(df.format(new Date()));// 创建时间
		vo.setPk_bill(workFlowBill.getPk_bill());// 单据主键
		vo.setTransi_type(workFlowBill.getBill_code()); // 单据类型
		// 当前登录人ID
		String userID = InvocationInfoProxy.getInstance().getUserId();
		UserVO userVO = (UserVO) HYPubBO_Client.queryByPrimaryKey(UserVO.class,
				userID);
		vo.setUserid(userID);
		vo.setDef1(userVO.getUser_name());
		vo.setDef2(back);
		HYPubBO_Client.insert(vo);
	}

	/**
	 * 获取附件列表
	 *
	 * @param headData
	 * @param workFlowBill
	 */
	@SuppressWarnings("deprecation")
	private static void getFiles(JSONArray headData, WorkFlowBill workFlowBill)
			throws BusinessException {
		String strWhere = "filepath like '" + workFlowBill.getPk_bill()
				+ "/%' order by ts";
		FileNodeVO[] fileNodeVOs = (FileNodeVO[]) new HYPubBO()
				.queryByCondition(FileNodeVO.class, strWhere);
		Map<String, Object> djzj = new HashMap<String, Object>();
		Map<String, Object> gzdjzj = new HashMap<String, Object>();// "需盖章"
		List<Map<?, ?>> list = new ArrayList<Map<?, ?>>();
		List<Map<?, ?>> gzlist = new ArrayList<Map<?, ?>>();// "需盖章"放到"pdfhtwj"字段
		/*
		 * if(StringUtils.equals("ZT", workFlowBill.getDef3()) &&
		 * headData.toString().indexOf("1001A110000000266P4Z") != -1){
		 * djzj.put("fieldName", "pdfhtwj"); }else{ djzj.put("fieldName", "fj");
		 * }
		 */
		if (null != fileNodeVOs && fileNodeVOs.length > 0) {
			/* 主表主键 */
			for (FileNodeVO temp : fileNodeVOs) {
				Map<String, String> file = new HashMap<String, String>();
				file.put(
						"filePath",
						FileStorageClient
								.getInstance()
								.getDownloadURL(null, temp.getPk_doc())
								.replace("172.18.130.232:9099",
										"172.18.128.32:8080"));
				String fileName = temp.getFilepath().substring(
						temp.getFilepath().lastIndexOf("/") + 1);// 附件名称
				file.put("fileName", fileName);
				// 如果包含 需盖章
				if (fileName.indexOf("需盖章") != -1
						&& fileName.indexOf("无需盖章") == -1
						&& fileName.indexOf("不需盖章") == -1) {
					gzlist.add(file);
				} else {
					// 如果包含 盖章文件
					if (fileName.indexOf("盖章文件") != -1) {
						gzlist.add(file);
					} else {
						list.add(file);
					}
				}
			}
		}
		/* 获取影像附件 */
		getFileList(list, workFlowBill.getPk_bill());
		if (list.size() > 0) {
			djzj.put("fieldName", "fj");
			djzj.put("fieldValue", list);
		}
		if (gzlist.size() > 0) {
			gzdjzj.put("fieldName", "pdfhtwj");
			gzdjzj.put("fieldValue", gzlist);
		}

		if (list.size() > 0) {
			djzj.put("fieldValue", list);
			headData.add(djzj);
		}
		if (StringUtils.equals("ZT", workFlowBill.getDef3())
				&& gzlist.size() > 0) {
			gzdjzj.put("fieldValue", gzlist);
			headData.add(gzdjzj);
		}
	}

	/**
	 * 获取影像附件
	 *
	 * @param list
	 * @param pk_bill
	 * @throws BusinessException
	 */
	private static void getFileList(List<Map<?, ?>> list, String pk_bill)
			throws BusinessException {
		String url = YXURL + pk_bill;
		String back = HttpClient.httpGet(url, null, "utf-8");
		if (StringUtils.isNotEmpty(back)) {
			JSONObject json = JSONObject.fromObject(back);
			if (json.containsKey("data")
					&& StringUtils.isNotEmpty(json.getString("data"))) {
				JSONObject data = json.getJSONObject("data");
				JSONArray files = data.getJSONArray("files");
				if (files != null && files.size() > 0) {
					for (Object curfile : files) {
						Map<Object, Object> file = new HashMap<Object, Object>();
						JSONObject evefile = (JSONObject) curfile;
						String filePath = "http://172.18.128.94:2333"
								+ evefile.getString("url");// 正式
						String filename = "影像-" + evefile.getString("fileName");
						if (filename.indexOf(".pdf") != -1) {
							filePath = "http://172.18.128.94:2333"
									+ evefile.getString("purl");// 正式
						}
						file.put("filePath", filePath);
						file.put("fileName", filename);
						list.add(file);
					}
				}
			}
		} else {
			throw new BusinessException("获取影像附件失败，请联系管理员！");
		}
	}

	public static Map<String, String> listAdd(String name, String val) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("fieldName", name);
		/*
		 * if (val == null) { val = "0"; }
		 */
		if (val == null) {
			val = "";
		}
		map.put("fieldValue", val);
		return map;
	}

	public static void backOaWorkFlow(String pk_bill) throws BusinessException {

		String strWhere = " nvl(dr,0) = 0  and pk_bill ='" + pk_bill + "'";
		WorkFlowBill[] workFlowBills = (WorkFlowBill[]) new HYPubBO()
				.queryByCondition(WorkFlowBill.class, strWhere);
		if (null != workFlowBills && workFlowBills.length == 1
				&& null == workFlowBills[0].getDef1()) {
			WorkFlowBill workFlowBill = workFlowBills[0];
			if (workFlowBill != null) {
				throw new BusinessException("该单据已提交至OA系统，请从OA系统中退回,REQUESTID["
						+ workFlowBill.getRequestid() + "]");
			}
			/*
			 * Map<String, String> inMap = new HashMap<String, String>();
			 * inMap.put("requestId", workFlowBill.getRequestid() + "");
			 * JSONObject jsonObject = JSONObject.fromObject(inMap); String
			 * oaUrl = (String) new HYPubBO().findColValue("org_orgs", "def1",
			 * "nvl(dr,0) = 0 and pk_org='" + workFlowBill.getPk_org() + "'");
			 * String url = oaUrl + "/api/workflow/paService/submitRequest";
			 * String back = HttpClient.httpPostForm(url, inMap,
			 * getHeads(workFlowBill, oaUrl), "utf-8"); JSONObject res =
			 * JSONObject.fromObject(back); String code = res.getString("code");
			 * if (!"SUCCESS".equals(code)) { throw new
			 * BusinessException("调用OA接口报错，错误信息：" + back); }
			 * workFlowBill.setSend_data(jsonObject.toString());
			 * workFlowBill.setReturn_data(back);
			 * workFlowBill.setBill_status("0"); if (null !=
			 * workFlowBill.getPk_workflow_bill()) { new
			 * HYPubBO().insert(workFlowBill); } else { new
			 * HYPubBO().update(workFlowBill); } throw new
			 * BusinessException("该单据已提交至OA系统，请从OA系统中退回");
			 */
		} else {

		}

	}

	private static Map<String, String> getHeads(WorkFlowBill workFlowBill,
			String url, String usd) throws BusinessException {
		String tokenBack = getRegist(url, workFlowBill.getDef3());
		JSONObject object = JSONObject.fromObject(tokenBack);
		String secrit = object.getString("secrit");
		String spk = object.getString("spk");
		String bak2 = applytoken(secrit, spk, url, workFlowBill.getDef3());
		JSONObject objecttoken = JSONObject.fromObject(bak2);
		String token = objecttoken.getString("token");
		Map<String, String> heads = new HashMap<String, String>();
		RSA rsa = new RSA();
		String res = HttpClient.httpPostForm(url
				+ "/mobile/weaverjn/api/nccode_to_userid.jsp?nccode="
				+ workFlowBill.getDef5(), null, heads, "utf-8");
		JSONObject object1 = JSONObject.fromObject(res);
		String userCode = object1.getString("oauid");
		if (null == userCode) {
			throw new BusinessException("在OA系统中未查找到编码为："
					+ workFlowBill.getDef5() + "的用户信息");
		}
		String ui = "".equals(usd) ? userCode : usd;
		if (!"ZT".equals(workFlowBill.getDef3())) {
			ui = userCode;
		}
		String userid = rsa.encrypt(null, ui, null, "utf8", spk, false);
		heads.put("token", token);

		if ("ZT".equals(workFlowBill.getDef3())) {
			heads.put("appid", ZTAPPID);
		} else {
			heads.put("appid", APPID);
		}
		// userid = "".equals(usd) ? userid : usd;
		heads.put("userid", userid);
		return heads;
	}

	/**
	 * 注册
	 *
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static String getRegist(String url, String type) {
		Map<String, String> heads = new HashMap<String, String>();
		String cpk = new RSA().getRSA_PUB();
		if ("ZT".equals(type)) {
			heads.put("appid", ZTAPPID);
		} else {
			heads.put("appid", APPID);
		}
		heads.put("cpk", cpk);
		String data = HttpClient.httpPostForm(url + "/api/ec/dev/auth/regist",
				null, heads, "utf-8");
		return data;
	}

	/**
	 * 获取token
	 *
	 * @param secrit
	 * @param spk
	 * @return
	 */
	public static String applytoken(String secrit, String spk, String url,
			String type) {
		Map<String, String> heads = new HashMap<String, String>();
		RSA rsa = new RSA();
		String secret_2 = rsa.encrypt(null, secrit, null, "utf-8", spk, false);
		if ("ZT".equals(type)) {
			heads.put("appid", ZTAPPID);
		} else {
			heads.put("appid", APPID);
		}
		heads.put("secret", secret_2);
		String data = HttpClient.httpPostForm(url
				+ "/api/ec/dev/auth/applytoken", null, heads, "utf-8");
		return data;
	}

	private static String getdocString(String pk) {
		Document document = DocumentHelper.createDocument();
		// 创建根节点
		Element root = document.addElement("CMDATA");
		// 添加子节点
		Element username = root.addElement("TRADETYPE");
		Element password = root.addElement("CIP");
		Element SYSTEM_CODE = root.addElement("SYSTEM_CODE");
		Element BRANCH_NO = root.addElement("BRANCH_NO");
		Element USER_NO = root.addElement("USER_NO");
		Element BUSI_SERIAL_NO = root.addElement("BUSI_SERIAL_NO");
		BUSI_SERIAL_NO.addText(pk);
		Element BATCH = root.addElement("BATCH");
		Element BATCHID = BATCH.addElement("BATCHID");
		Element DOCUMENTS = BATCH.addElement("DOCUMENTS");
		Element DOCUMENT = DOCUMENTS.addElement("DOCUMENT");
		Element DOCNAME = DOCUMENT.addElement("DOCNAME");
		Element DESC = DOCUMENT.addElement("DESC");
		Element FILES = DOCUMENT.addElement("FILES");
		Element FILE = FILES.addElement("FILE");
		Element VERSION = FILE.addElement("VERSION");
		Element FILE_SEQ = FILE.addElement("FILE_SEQ");
		Element FILE_TYPE = FILE.addElement("FILE_TYPE");
		Element FILE_NAME = FILE.addElement("FILE_NAME");
		Element FILE_FORMAT = FILE.addElement("FILE_FORMAT");
		Element FILE_SIZE = FILE.addElement("FILE_SIZE");
		Element FILE_MD5 = FILE.addElement("FILE_MD5");
		return document.asXML();
	}

	public static Map<String, Object> listAddObj(String name, String val) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("fieldName", name);
		/*
		 * if (val == null) { val = "0"; }
		 */
		if (val == null) {
			val = "";
		}
		map.put("fieldValue", val);
		return map;
	}

	/**
	 * 将javabean转成List<Map<String, Object>>
	 *
	 * @param obj
	 * @return
	 */
	public static List<Map<String, Object>> transBean2Map(Object obj) {
		if (obj == null) {
			return null;
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo
					.getPropertyDescriptors();
			PropertyDescriptor property = null;
			// for (PropertyDescriptor property : propertyDescriptors) {
			String key = "";
			for (int i = 0; i < propertyDescriptors.length; i++) {
				property = propertyDescriptors[i];
				map = new HashMap<String, Object>();
				key = property.getName();
				if ("summary".equals(key)) {
					int in = 0;
					Class type = property.getPropertyType();
				}
				// 过滤class等多余属性
				if (!key.equals("class") && !key.equals("serialVersionUID")
						&& !key.equals("metaData")
						&& !key.equals("attributeNames")
						&& !key.equals("entityName")) {
					// 特殊字段处理
					Class type = property.getPropertyType();
					if (type.getName().endsWith("UFDate")) {
						Method getter = property.getReadMethod();
						Object object = getter.invoke(obj);
						String fieldValue = "";
						if (null != object) {
							UFDate value = (UFDate) object;
							fieldValue = value.toStdString();
						}

						map.put("fieldName", key);
						map.put("fieldValue", fieldValue);
						list.add(map);
						continue;
					}

					if (type.getName().endsWith("UFDateTime")) {
						Method getter = property.getReadMethod();
						Object object = getter.invoke(obj);
						String fieldValue = "";
						if (null != object) {
							UFDateTime value = (UFDateTime) object;
							fieldValue = value.toStdString();
						}

						map.put("fieldName", key);
						map.put("fieldValue", fieldValue);
						list.add(map);
						continue;
					}
					if (type.getName().endsWith("UFBoolean")) {
						Method getter = property.getReadMethod();
						Object object = getter.invoke(obj);
						String fieldValue = "";
						if (null != object) {
							UFBoolean value = (UFBoolean) object;
							fieldValue = value.toString();
						}
						if (!StringUtils.equals(key, "jkbxr")) {
							map.put("fieldName", key);
							map.put("fieldValue", fieldValue);
							list.add(map);
						}
						continue;
					}

					// 得到property对应的getter方法
					Method getter = property.getReadMethod();
					Object value = "";
					if (null != getter) {
						value = getter.invoke(obj) == null ? "" : getter
								.invoke(obj);
					}
					if (!StringUtils.equals(key, "jkbxr")) {
						map.put("fieldName", key.toLowerCase());
						/*
						 * if (value == null) { value = "0"; }
						 */
						// if (value == null) {
						// value = "";
						// }
						map.put("fieldValue", value + "");
						list.add(map);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static WorkFlowBill getWorkFlowBill(OaWorkFlowVO temp)
			throws BusinessException {
		HYPubBO hyPubBO = new HYPubBO();
		String where = " nvl(dr,0) = 0 and pk_bill = '" + temp.getPrimaryKey()
				+ "'";
		WorkFlowBill[] workFlowBills = (WorkFlowBill[]) hyPubBO
				.queryByCondition(WorkFlowBill.class, where);
		WorkFlowBill workFlowBill = new WorkFlowBill();
		if (null != workFlowBills && workFlowBills.length > 1) {
			throw new BusinessException("查询到多条流程记录");
		}
		if (null != workFlowBills && workFlowBills.length == 1) {
			workFlowBill = workFlowBills[0];
			workFlowBill.setWorkflowName(temp.getWorkflowName());
		} else {
			workFlowBill.setWorkflowId(temp.getWorkflowId());
			UserVO userVO = (UserVO) hyPubBO.queryByPrimaryKey(UserVO.class,
					temp.getBillMaker());
			workFlowBill.setDef5(userVO.getUser_code());
			if ("2".equals(getDef2(temp.getPkOrg()))) {
				PsndocVO psndoc = (PsndocVO) hyPubBO.queryByPrimaryKey(
						PsndocVO.class, userVO.getPk_psndoc());
				workFlowBill.setDef5(psndoc.getId());
				workFlowBill.setWorkflowId("0");
			}
			workFlowBill.setPk_group(temp.getPkGroup());
			workFlowBill.setPk_org(temp.getPkOrg());
			workFlowBill.setPk_bill(temp.getPrimaryKey());
			workFlowBill.setUser_name(userVO.getUser_name());
			workFlowBill.setUser_code(userVO.getUser_code());
			workFlowBill.setCreationtime(new UFDate().toStdString());
			workFlowBill.setCreator(temp.getCreator());
			workFlowBill.setBill_code(temp.getBillCode());
			workFlowBill.setBill_status("1");
			workFlowBill.setWorkflowName(temp.getWorkflowName());
		}
		return workFlowBill;
	}

	public static String getDef2(String pk_org) throws BusinessException {
		String def2 = "";
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		if (null != orgVO) {
			def2 = orgVO.getDef2();
		}
		return def2;
	}

	// 判断是否有收回，审批，取消审批权限
	public static String ifchange() {
		String fg = "0";
		String user = InvocationInfoProxy.getInstance().getUserId();// 当前用户主键
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		String querySql = "select c.cuserid, a.pk_role, a.role_name,c.user_name from sm_role a left join sm_user_role b  on a.pk_role=b.pk_role "
				+ " left join sm_user c on c.cuserid=b.cuserid  where a.role_code = 'BILLACTION'";
		System.out.println("查询是否角色下用户：" + querySql);
		try {
			List<Object[]> resultList = getDao.query(querySql);
			if (resultList.size() > 0) {
				for (int i = 0; i < resultList.size(); i++) {
					Object[] item = resultList.get(i);
					if (user.equals(item[0])) {
						fg = "1";
					}
				}
			}
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fg;
	}

	public static String getOAFlowID(String workflowid) {
		String flowid = "";
		// 调用OA接口获取 流程id
		com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
		HttpURLConnection connection = null;
		BufferedReader reader = null;
		String line = null;
		try {
			try {
				String oaUrl = (String) new HYPubBO().findColValue(
						"sys_config", "config_value",
						"nvl(dr,0) = 0 and config_id = 50");
				URL url = new URL(oaUrl
						+ "szzt/service/getactiveworkflowid.jsp?workflowid="
						+ workflowid + "");
				connection = (HttpURLConnection) url.openConnection();// 根据URL生成HttpURLConnection
				connection.setRequestMethod("POST");// 默认GET请求
				connection.connect();// 建立TCP连接
				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					reader = new BufferedReader(new InputStreamReader(
							connection.getInputStream(), "UTF-8"));// 发送http请求
					StringBuilder result = new StringBuilder();
					// 循环读取流
					while ((line = reader.readLine()) != null) {
						result.append(line).append(
								System.getProperty("line.separator"));// "\n"
					}
					System.out.println(result.toString());
					json = com.alibaba.fastjson.JSONObject.parseObject(result
							.toString());
					flowid = json.getString("activeversionid");
				}
			} catch (UifException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			connection.disconnect();
		}
		return flowid;
	}

	// 通过人员身份证号和NC单位主键获取OA人员id
	public static String getOAID(String id, String pk_org)
			throws BusinessException {
		String userid = "";
		// 调用OA接口获取 流程id
		com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
		HttpURLConnection connection = null;
		BufferedReader reader = null;
		String line = null;
		try {
			try {
				String oaUrl = (String) new HYPubBO().findColValue(
						"sys_config", "config_value",
						"nvl(dr,0) = 0 and config_id = 50");
				URL url = new URL(oaUrl
						+ "szzt/service/getuseridbyidno.jsp?idno=" + id
						+ "&nccorg=" + pk_org + "");
				connection = (HttpURLConnection) url.openConnection();// 根据URL生成HttpURLConnection
				connection.setRequestMethod("POST");// 默认GET请求
				connection.connect();// 建立TCP连接
				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					reader = new BufferedReader(new InputStreamReader(
							connection.getInputStream(), "UTF-8"));// 发送http请求
					StringBuilder result = new StringBuilder();
					// 循环读取流
					while ((line = reader.readLine()) != null) {
						result.append(line).append(
								System.getProperty("line.separator"));// "\n"
					}
					System.out.println(result.toString());
					json = com.alibaba.fastjson.JSONObject.parseObject(result
							.toString());
					if (json != null && "true".equals(json.getString("ok"))) {
						userid = json.getJSONObject("data").getString("userid");
					} else {
						Logger.error("接口返回信息：" + json.getString("msg"));
					}
				}
			} catch (UifException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != reader) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			connection.disconnect();
		}
		return userid;
	}

	public static OAFlowVO getOAFlowInfo(String pk_org, String billtype)
			throws BusinessException {
		// 获取OA的信息
		String where = " pk_org = '" + pk_org + "' and billtype = '" + billtype
				+ "'";
		Logger.error("查询OAWORLSQL语句：" + where);
		OAFlowVO oaVo = null;
		OAFlowVO[] oavos = (OAFlowVO[]) getHyPubBO().queryByCondition(
				OAFlowVO.class, where);
		if (null != oavos && oavos.length >= 1) {
			if (oavos.length > 1) {
				throw new BusinessException("RL_GHOAWORK表查询OA表名和ID出错，请检查["
						+ where + "]");
			} else {
				oaVo = oavos[0];
			}
		}
		return oaVo;
	}

	public static HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}
}
