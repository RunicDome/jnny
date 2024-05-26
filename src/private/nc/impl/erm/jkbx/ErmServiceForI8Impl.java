package nc.impl.erm.jkbx;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.trade.business.HYPubBO;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.pub.IBXBillPublic;
import nc.itf.erm.jkbx.IErmServiceForI8;
import nc.itf.pubapp.pub.smart.IBillQueryService;
import nc.itf.uap.pf.IWorkflowAdmin;
import nc.uif.pub.exception.UifException;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.fct.ap.entity.AggCtApVO;
import nc.vo.fct.ap.entity.CtApBVO;
import nc.vo.fct.ap.entity.CtApVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.sm.UserVO;
import nc.ws.intf.HttpClient;
import nc.ws.intf.I8LogVO;
import nc.ws.intf.OALogVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import weaver.rsa.security.RSA;

// import nc.impl.fct.ap.action.ApDeleteAction;

@SuppressWarnings("unused")
public class ErmServiceForI8Impl implements IErmServiceForI8 {
	// OA中台
	public static String ZTAPPID = "EEAA5436-7577-4BEO-8c6c-89E9D88805EA";

	@Override
	public String rollAggBillVO(String pk, String billtype, String reason)
			throws BusinessException {
		// TODO Auto-generated method stub
		if (StringUtils.isEmpty(pk) || StringUtils.isEmpty(billtype)) {
			return "请检查前端数据传输，pk[" + pk + "] billtype[" + billtype + "] ";
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String ts = df.format(new Date());// 操作时间
		// 报销单
		if ("264X".equals(billtype)) {
			// 将数据库中数据改为自由态
			String sql = "UPDATE ER_BXZB SET DJZT = '1',SPZT = '-1',TS = '"
					+ ts + "' WHERE " + "PK_JKBX = '" + pk + "'";
			new BaseDAO().executeUpdate(sql);

			String sqlwhere = "where nvl(dr,0) = 0 and pk_jkbx='" + pk + "'";
			IBXBillPrivate queryservice = (IBXBillPrivate) NCLocator
					.getInstance().lookup(IBXBillPrivate.class);
			List<JKBXVO> list = queryservice.queryVOsByWhereSql(sqlwhere, "bx");

			IBXBillPublic delservice = (IBXBillPublic) NCLocator.getInstance()
					.lookup(IBXBillPublic.class);
			if (list != null && list.size() > 0) {
				JKBXHeaderVO headvo = list.get(0).getParentVO();
				String jylx = headvo.getDjlxbm();// 交易类型编码
				String pk_item = headvo.getPk_item();// 付款合同主键
				String srctype = headvo.getSrctype();// 上游单据类型
				String vbillcode = (String) headvo.getZyx31();//上游合同编码
				if ("264X-Cxx-gcfkht".equals(jylx)
						|| "264X-Cxx-HFCGHT".equals(jylx)
						|| "264X-Cxx-HFXSHT".equals(jylx)
						|| "264X-Cxx-gcskht".equals(jylx)) {
					if (pk_item == null) {
						return "上游单据主键为空，请检查！";
					} else {
						// Map<String, String> mes =
						// sendOARollBack(aggvo.getParentVO(),reason);//
						// 调用OA接口驳回至制单人
						// if (mes.get("mes") != null &&
						// !"".equals(mes.get("mes"))){
						// return mes.get("mes");
						// }
						// 修改费用类合同状态为自由态
						IWorkflowAdmin admin = NCLocator.getInstance().lookup(
								IWorkflowAdmin.class);
						try {
							admin.terminateWorkflow(pk_item, srctype,
									vbillcode, 4);
						} catch (Exception e) {
							System.out.println("终止流程报错：" + e.getMessage());
							throw new BusinessException("终止流程报错："
									+ e.getMessage());
						}
						// 修改付款合同单据状态为自由态
						String updsql = "";
						if ("FCT1".equals(srctype)) {
							updsql = "UPDATE FCT_AP SET VDEF12 = '" + reason
									+ "',TS = '" + ts + "' WHERE "
									+ "PK_FCT_AP = '" + pk_item + "'";
						} else if ("FCT2".equals(srctype)) {
							updsql = "UPDATE FCT_AR SET VDEF12 = '" + reason
									+ "',TS = '" + ts + "' WHERE "
									+ "PK_FCT_AR = '" + pk_item + "'";
						}
						if (StringUtils.isNotEmpty(updsql)) {
							try {
								new BaseDAO().executeUpdate(updsql);
								JKBXVO[] vos = { list.get(0) };
								delservice.deleteBills(vos);// 删除报销单
								delssc(headvo.getDjbh());// 删除共享单据
							} catch (BusinessException e) {
								// TODO: handle exception
								return "删除报错，" + e.getMessage();
							}
						}
					}

				} else {
					if (headvo.getZyx29() == null) {
						return "报销单I8单据号为空！";
					}
					/*
					 * terminate(headvo.getPrimaryKey(),
					 * headvo.getPk_billtype(), headvo.getDjbh());
					 */
					try {

						Map<String, String> mes = sendRollBack(
								headvo.getZyx29(), headvo.getDjlxbm(), reason,
								headvo.getDjbh());
						if ((mes.get("mes") != null && !"".equals(mes
								.get("mes")))
								|| (mes.get("i8nomes") != null && !""
										.equals(mes.get("i8nomes")))) {
							return mes.get("mes");
						}
						String logmes = SaveI8Log(headvo.getDjbh(), pk,
								headvo.getDjlxbm(), headvo.getCreator(),
								reason, mes.get("i8mes"), headvo.getZyx29(),
								mes.get("sendata"), headvo.getPk_org());// 保存记录
						if (!"".equals(logmes)) {
							return logmes;
						}
						JKBXVO[] vos = { list.get(0) };
						delservice.deleteBills(vos);
						delssc(headvo.getDjbh());// 删除共享单据
					} catch (Exception e) {
						return "删除单据报错：" + e.getMessage();
					}
				}
			}
		}
		return "";
	}

	// 保存记录
	private String SaveI8Log(String bllno, String pk, String tradetype,
			String creator, String reason, String rdtdata, String i8djh,
			String sendata, String pk_org) throws BusinessException {
		// TODO Auto-generated method stub
		String mes = "";
		I8LogVO vo = new I8LogVO();
		vo.setBill_code(bllno);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		vo.setCreationtime(df.format(new Date()));// 创建时间
		vo.setPk_bill(pk);// 单据主键
		vo.setTransi_type(tradetype); // 交易类型
		vo.setUserid(creator);// 创建人
		vo.setReason(reason);// 驳回原因
		vo.setRdtdata(rdtdata);// I8返回数据
		vo.setI8djh(i8djh);// I8单据号
		vo.setDef1(sendata);// 发送I8数据
		vo.setDef2("驳回");// 类型
		vo.setPk_org(pk_org);// 组织主键
		vo.setPk_group("0001A1100000000001QS");// 集团主键
		try {
			new HYPubBO().insert(vo);
		} catch (BusinessException e) {
			// TODO: handle exception
			return e.getMessage();
		}
		return mes;
	}

	/*
	 * public void terminate(String billid, String billtype, String billno)
	 * throws BusinessException { //
	 * InvocationInfoProxy.getInstance().setUserId("1001A11000000026YI1E");// //
	 * 设置默认操作员主键 int iWorkflowtype = WorkflowTypeEnum.Workflow.getIntValue();
	 * IWorkflowAdmin admin = NCLocator.getInstance().lookup(
	 * IWorkflowAdmin.class); try { admin.terminateWorkflow(billid, billtype,
	 * billno, iWorkflowtype); } catch (Exception e) {
	 * System.out.println("终止流程报错：" + e.getMessage()); } }
	 */
	// 调用I8驳回接口
	private Map<String, String> sendRollBack(String i8no, String djlxbm,
			String reason, String billno) throws UifException,
			BusinessException {
		String userID = InvocationInfoProxy.getInstance().getUserId();// 获取当前系统登录用户
		UserVO userVO = (UserVO) new HYPubBO().queryByPrimaryKey(UserVO.class,
				userID);
		Map<String, String> map = new HashMap<>();
		String url = (String) new HYPubBO().findColValue("sys_config",
				"config_value", "config_key='i8-url'");
		if (url != null) {
			String bill_tag = (String) new HYPubBO().findColValue("bd_defdoc",
					"code", "name='" + djlxbm + "' and shortname <> '~'");
			if (bill_tag == null || "".equals(bill_tag)) {
				map.put("mes", "根据[" + djlxbm + "]未获取到I8业务标识！");
				return map;
			}
			JSONObject json = new JSONObject();
			json.put("bill_no", i8no);
			// json.put("user_ncbillcode", billno);// NC单据号
			json.put("user_ncrejectmsg", userVO + reason);// 驳回原因
			json.put("bill_status", "3");// 状态 目前传 3 驳回
			json.put("bill_tag", bill_tag);
			Map<String, String> headers = new HashMap<>();
			// headers.put("Zoomkey-Auth-Token", zoomkeyAuthToken);
			Logger.error("发送I8数据：" + json.toString());
			map.put("sendata", json.toString());
			String mes = HttpClient.httpPostRaw(url, json.toString(), headers,
					"utf-8");
			if (mes == null || "".equals(mes)) {
				map.put("mes", "调用I8系统接口返回数据为空，请检查！");
				return map;
			}
			map.put("i8mes", mes);
			Logger.error("I8返回数据：" + mes);
			JSONObject res = JSONObject.fromObject(mes);
			String status = res.getString("status");
			if (!"S".equals(status)) {
				map.put("mes", res.getString("message"));
				// return res.getString("message");
				// throw new BusinessException("调用I8系统接口出错:" + info);
			}
		} else {
			map.put("mes", "URL地址为空,请配置SYS_CONFIG数据表！");
			return map;
			// throw new BusinessException("URL地址为空,请配置SYS_CONFIG数据表！");
		}
		return map;
	}

	// 删除共享单据
	public String delssc(String billno) {
		BaseDAO dao = new BaseDAO();
		String mes = null;// 报错信息
		String sql1 = "delete from ssc_activetask where pk_ssctask in( select pk_ssctask from ssc_ssctask where billcode='"
				+ billno + "')";
		String sql2 = "delete from ssc_persontask where pk_ssctask in( select pk_ssctask from ssc_ssctask where billcode='"
				+ billno + "')";
		String sql3 = "delete from ssc_posttask where pk_ssctask in( select pk_ssctask from ssc_ssctask where billcode='"
				+ billno + "')";
		String sql4 = "delete from ssc_ssctasklog where pk_ssctask in( select pk_ssctask from ssc_ssctask where billcode='"
				+ billno + "')";
		String sql5 = "delete from ssc_ssctask where billcode='" + billno + "'";
		try {
			dao.executeUpdate(sql1);
			dao.executeUpdate(sql2);
			dao.executeUpdate(sql3);
			dao.executeUpdate(sql4);
			dao.executeUpdate(sql5);
		} catch (BusinessException e) {
			// TODO: handle exception
			e.printStackTrace();
			mes = e.getMessage();
		}
		return mes;
	}

	// 调用驳回OA接口
	private Map<String, String> sendOARollBack(CtApVO hvo, String reason)
			throws BusinessException {
		Map<String, String> map = new HashMap<>();
		String url = (String) new HYPubBO().findColValue("sys_config",
				"config_value", "nvl(dr,0) = 0 and config_id = 63");
		if (url != null) {
			url = url + "/api/workflow/paService/rejectRequest";
			// JSONObject json = new JSONObject();
			// 退回到指定节点
			// {"RejectToType":0,"RejectToNodeid":3974}，如果不传这2个参数默认是按出口退回
			JSONObject othjson = new JSONObject();
			Map<String, String> inMap = new HashMap<String, String>();
			othjson.put("RejectToType", 0);
			othjson.put("RejectToNodeid", 30916);
			// inMap.put("otherParams", "");
			String strWhere = " nvl(dr,0) = 0 and pk_bill = '"
					+ hvo.getPrimaryKey() + "' and def3 = 'ZT'";
			WorkFlowBill[] workFlowBill = (WorkFlowBill[]) new HYPubBO()
					.queryByCondition(WorkFlowBill.class, strWhere);
			if (null == workFlowBill || workFlowBill.length != 1) {
				throw new BusinessException("未查询到流程日志");
			}
			inMap.put("remark", reason);// 签字意见
			// 退回的流程请求ID
			inMap.put("requestId", workFlowBill[0].getRequestid() + "");

			// Map<String, String> headers = new HashMap<>();
			// headers.put("Zoomkey-Auth-Token", zoomkeyAuthToken);
			Logger.error("发送OA数据：" + inMap.toString());
			// map.put("sendata", json.toString());
			String zcurl = (String) new HYPubBO().findColValue("sys_config",
					"config_value", "nvl(dr,0) = 0 and config_id = 50");
			String mes = HttpClient.httpPostForm(url, inMap,
					getHeads(workFlowBill[0], zcurl), "utf-8");
			if (mes == null || "".equals(mes)) {
				map.put("mes", "调用OA系统接口返回数据为空，请检查！");
				SaveOALog(hvo, workFlowBill[0].getRequestid(),
						inMap.toString(), mes);// 生成OA日志
				return map;
			}
			// map.put("oames", mes);
			Logger.error("OA返回数据：" + mes);
			JSONObject res = JSONObject.fromObject(mes);
			String status = res.getString("code");
			if (!"SUCCESS".equals(status)) {
				map.put("mes", "调用OA接口报错，返回信息[" + res + "]");
			}
			SaveOALog(hvo, workFlowBill[0].getRequestid(), inMap.toString(),
					mes);// 生成OA日志
		} else {
			map.put("mes", "OA系统URL地址为空,请配置SYS_CONFIG数据表！");
			return map;
		}
		return map;
	}

	// 记录日志VO
	private void SaveOALog(CtApVO hvo, int requestid, String sendata,
			String rtndata) throws BusinessException {
		OALogVO vo = new OALogVO();
		vo.setBill_code(hvo.getVbillcode());// 单据编号
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		vo.setCreationtime(df.format(new Date()));// 创建时间
		vo.setRequestid(requestid + "");// OA流程ID
		vo.setPk_bill(hvo.getPrimaryKey());// 单据主键
		vo.setTransi_type(hvo.getVtrantypecode()); // 交易类型
		String userID = InvocationInfoProxy.getInstance().getUserId();// 获取当前系统登录用户
		UserVO userVO = (UserVO) new HYPubBO().queryByPrimaryKey(UserVO.class,
				userID);
		vo.setUserid(userID);
		if (userVO != null) {
			vo.setUsername(userVO.getUser_name());
		}
		vo.setSend_data(sendata);// 调用OA发送数据
		vo.setDef1("NC调用OA驳回接口");
		vo.setDef2(hvo.getPk_org());
		vo.setDef3(hvo.getPk_group());
		vo.setDef4(rtndata);// 调用OA返回数据
		vo.setDr(0);
		try {
			InetAddress addr = InetAddress.getLocalHost();
			String Ip = addr.getHostAddress(); // 客户端IP
			vo.setUserip(Ip);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new HYPubBO().insert(vo);
	}

	private static Map<String, String> getHeads(WorkFlowBill workFlowBill,
			String url) throws BusinessException {
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
				+ "/mobile/weaverjn/api/nccode_to_userid.jsp?nccode=58971",
				null, heads, "utf-8");
		JSONObject object1 = JSONObject.fromObject(res);
		String userCode = object1.getString("oauid");
		if (null == userCode) {
			throw new BusinessException("在OA系统中未查找相应用户信息");
		}
		String userid = rsa.encrypt(null, "58971", null, "utf8", spk, false);
		heads.put("token", token);
		heads.put("appid", ZTAPPID);
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
		}
		heads.put("secret", secret_2);
		String data = HttpClient.httpPostForm(url
				+ "/api/ec/dev/auth/applytoken", null, heads, "utf-8");
		return data;
	}
}
