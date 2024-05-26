package nc.ic.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.bs.framework.adaptor.IHttpServletAdaptor;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.security.token.ISecurityTokenCache;
import nc.bs.framework.server.ISecurityTokenCallback;
import nc.bs.ic.warehouse.check.WarehouseArrCheck;
import nc.bs.logging.Logger;
import nc.bs.trade.business.HYPubBO;
import nc.itf.ic.m45.self.IPurchaseInMaintain;
import nc.itf.uap.pf.IPfExchangeService;
import nc.itf.uap.pf.IplatFormEntry;
import nc.pub.billcode.itf.IBillcodeManage;
import nc.pubitf.pu.m21.api.IOrderQueryAPI;
import nc.ui.querytemplate.operator.EqOperator;
import nc.vo.ic.general.define.MetaNameConst;
import nc.vo.ic.m45.entity.PurchaseInBodyVO;
import nc.vo.ic.m45.entity.PurchaseInHeadVO;
import nc.vo.ic.m45.entity.PurchaseInVO;
import nc.vo.ic.m45.vochange.POTO45ChangeVOAdjust;
import nc.vo.ic.pub.calc.BusiCalculator;
import nc.vo.ic.pub.util.VOEntityUtil;
import nc.vo.logging.Debug;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pfxx.util.ArrayUtils;
import nc.vo.pu.m21.entity.OrderHeaderVO;
import nc.vo.pu.m21.entity.OrderItemVO;
import nc.vo.pu.m21.entity.OrderVO;
import nc.vo.pu.m21.rule.api.IOrderVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.scmpub.util.QuerySchemeBuilder;
import nc.ws.intf.BillLogVO;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

// 对接二维码，燃气采购入库JSON（关联采购订单）导入
@SuppressWarnings({ "restriction", "unchecked", "rawtypes" })
public class RQPurchaseInByOrder extends HttpServlet implements
		IHttpServletAdaptor {
	private static final long serialVersionUID = 1L;
	IPfExchangeService iPfExchangeService = NCLocator.getInstance().lookup(
			IPfExchangeService.class);
	IplatFormEntry iplatFormEntry = NCLocator.getInstance().lookup(
			IplatFormEntry.class);
	private static String BODYTABLE = "ic_purchasein_b";
	private static String HEADTABLE = "ic_purchasein_h";

	// 实现IHttpServletAdaptor
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		// 实现post请求，调用IHttpServletAdaptor下的doAction方法
		doAction(req, res);
	}

	@Override
	public void doAction(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		ISecurityTokenCallback sc = NCLocator.getInstance().lookup(
				ISecurityTokenCallback.class);
		byte[] token = sc.token("NCSystem".getBytes(), "pfxx".getBytes());
		// token存入NC系统的缓存
		ISecurityTokenCache tokencache = (ISecurityTokenCache) NCLocator
				.getInstance().lookup(ISecurityTokenCache.class);
		tokencache.isContain(token);
		req.setCharacterEncoding("utf-8");// 设置编码规则
		BufferedReader br = req.getReader();// 读取HttpServletRequest对象里的数据
		String body = "";
		String line = null;
		while ((line = br.readLine()) != null) {
			body += line;
		}
		Debug.error("=============rqm45upload order msg ：" + body);
		res.setCharacterEncoding("utf-8");
		res.setStatus(200);
		JSONObject jsonObject = null;
		String request = "";
		try {
			jsonObject = (JSONObject) JSONObject.parse(body);
		} catch (JSONException e) {
			request = createMsg(false, "-1", "JSON解析失败！" + e.getMessage());
		}
		if (isEmpty(request)) {
			request = createVO(jsonObject);
		}
		try {
			request += SaveBillLog(body, request);// 保存日志
		} catch (BusinessException e) {
			e.printStackTrace();
			Logger.error("保存日志报错：" + e.getMessage());
			if (request == null) {
				request += "保存日志报错：" + e.getMessage();
			}
		}
		System.out.println("=================" + request);
		res.getWriter().write(request);// 输出body
		return;
	}

	private String createVO(JSONObject body) {
		InvocationInfoProxy.getInstance().setGroupId("0001A1100000000001QS");
		String errorMsg = "";
		try {
			JSONObject jsonObj = new JSONObject(body);
			String data = jsonObj.getString("data");
			List<Map> paramList = JSON.parseArray(data, Map.class);
			for (Map map : paramList) {
				if ((!map.containsKey(HEADTABLE))
						|| (!map.containsKey(BODYTABLE))) {
					errorMsg = "数据格式不正确！";
					return createMsg(false, "-1", errorMsg);
				}
				Map headInfo = (Map) map.get(HEADTABLE);
				List<Map> bodyInfo = (List) map.get(BODYTABLE);
				String pk_order = (String) headInfo.get("pk_order");
				String creator = (String) headInfo.get("creator");// 制单人/签字人/创建人
				String busidate = (String) headInfo.get("busidate");// 业务日期
				if (isEmpty(busidate)) {
					errorMsg = "请传入业务日期！";
					return createMsg(false, "-1", errorMsg);
				}
				if (isEmpty(creator)) {
					errorMsg = "请传入制单人主键！";
					return createMsg(false, "-1", errorMsg);
				}
				InvocationInfoProxy.getInstance().setUserId(creator);
				SimpleDateFormat timeFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date busitime = timeFormat.parse(busidate);
				// 设置业务日期
				InvocationInfoProxy.getInstance().setBizDateTime(
						busitime.getTime());
				if (isEmpty(pk_order)) {
					errorMsg = "请传入采购订单主键！";
					return createMsg(false, "-1", errorMsg);
				}
				// 查询采购订单
				IOrderQueryAPI query = NCLocator.getInstance().lookup(
						IOrderQueryAPI.class);
				QuerySchemeBuilder builder = QuerySchemeBuilder
						.buildByFullClassName(OrderHeaderVO.class.getName());
				builder.append(IOrderVO.PK_ORDER, EqOperator.getInstance(),
						new String[] { pk_order });
				OrderVO[] vos = query.queryVOByScheme(builder.create());
				if (ArrayUtils.isEmpty(vos)) {
					errorMsg = "根据" + pk_order + "查不到采购订单!";
					return createMsg(false, "-1", errorMsg);
				}
				OrderVO orderVO = vos[0];
				// 采购订单→采购入库
				PurchaseInVO purchaseInVO = (PurchaseInVO) iPfExchangeService
						.runChangeData("21", "45", orderVO, null);
				if (purchaseInVO == null) {
					errorMsg = "采购订单->采购入库转换失败，请确认信息后重试！";
					return createMsg(false, "-1", errorMsg);
				}
				PurchaseInHeadVO head = purchaseInVO.getHead();// 采购入库表头
				PurchaseInBodyVO[] bodyVOs = purchaseInVO.getBodys();// 采购入库表体
				head.setStatus(VOStatus.NEW);
				String cwarehouseid = (String) headInfo.get("cwarehouseid");// 仓库主键
				if (StringUtils.isEmpty(cwarehouseid)) {
					errorMsg = "cwarehouseid表头仓库主键不能为空!";
					return createMsg(false, "-1", errorMsg);
				}
				head.setCwarehouseid(cwarehouseid);// 仓库
				String freplenishflag = (String) headInfo.get("freplenishflag"); // 采购退库
				if ("Y".equals(freplenishflag)) {
					head.setFreplenishflag(UFBoolean.TRUE);
				}
				ArrayList<PurchaseInBodyVO> list = new ArrayList<PurchaseInBodyVO>();
				for (Map bodyMap : bodyInfo) {
					for (int i = 0; i < bodyVOs.length; ++i) {
						PurchaseInBodyVO bodyVO = bodyVOs[i];
						String pk_order_b = (String) bodyMap
								.get("csourcebillbid");
						if (!bodyVO.getCsourcebillbid().equals(pk_order_b)) {
							continue;
						}
						if (!isEmpty((String) bodyMap.get("vbatchcode"))) {
							String pk_batchcode = (String) bodyMap
									.get("pk_batchcode");
							bodyVO.setVbatchcode((String) bodyMap
									.get("vbatchcode"));// 批次号
							if (!isEmpty(pk_batchcode)) {
								bodyVO.setPk_batchcode(pk_batchcode);// 批次号主键
							}
						}
						// 仓库是否有货位管理
						WarehouseArrCheck check = new WarehouseArrCheck();
						boolean iswhspace = check.checkCsFlag(cwarehouseid);
						if (iswhspace
								&& isEmpty((String) bodyMap.get("clocationid"))) {
							errorMsg = NCLangRes4VoTransl
									.getNCLangRes()
									.getStrByID("4008001_0", "04008001-0687",
											null,
											new String[] { bodyVO.getCrowno() });
							return createMsg(false, "-1", errorMsg);
						}
						if (!isEmpty((String) bodyMap.get("clocationid"))) {
							bodyVO.setClocationid((String) bodyMap
									.get("clocationid"));// 货位
						}
						if (isEmpty((String) bodyMap.get("nassistnum"))) {
							errorMsg = "nassistnum表体实收数量不能为空!";
							return createMsg(false, "-1", errorMsg);
						}
						bodyVO.setCbodywarehouseid(cwarehouseid);// 库存仓库
						OrderItemVO[] orderItemVOs = (OrderItemVO[]) VOEntityUtil
								.filterVOWhenFieldEquals(orderVO.getBVO(),
										new String[] { "pk_order_b" },
										new String[] { pk_order_b });
						bodyVO.setNshouldnum(orderItemVOs[0].getNastnum());// 应收主数量
						bodyVO.setNshouldassistnum(orderItemVOs[0].getNastnum());// 应收数量
						// if ("Y".equals(freplenishflag)) {
						// bodyVO.setNshouldnum(orderItemVOs[0].getNastnum()
						// .multiply(-1));// 应收主数量
						// bodyVO.setNshouldassistnum(orderItemVOs[0]
						// .getNastnum().multiply(-1));// 应收数量
						// }
						bodyVO.setNassistnum(new UFDouble((String) bodyMap
								.get("nassistnum")));// 实收数量
						bodyVO.setNnum(new UFDouble((String) bodyMap
								.get("nassistnum")));// 实收主数量
						bodyVO.setNqtunitnum(bodyVO.getNnum());// 报价数量
						if (!isEmpty((String) bodyMap.get("vbatchcode"))) {
							bodyVO.setVbatchcode((String) bodyMap
									.get("vbatchcode"));// 批次号
						}
						list.add(bodyVO);
					}
				}
				PurchaseInBodyVO[] purchaseInBodyVOs = list
						.toArray(new PurchaseInBodyVO[list.size()]);
				BusiCalculator calc = BusiCalculator.getBusiCalculatorAtBS();
				calc.calcMny(purchaseInBodyVOs, MetaNameConst.NASSISTNUM);
				purchaseInVO.setChildrenVO(purchaseInBodyVOs);
				String standardCode = null;
				IBillcodeManage iBillcodeManage = (IBillcodeManage) NCLocator
						.getInstance().lookup(IBillcodeManage.class.getName());
				standardCode = iBillcodeManage.getBillCode_RequiresNew("45",
						purchaseInVO.getParentVO().getPk_group(), purchaseInVO
								.getParentVO().getPk_org(), purchaseInVO
								.getParentVO());
				purchaseInVO.getParentVO().setVbillcode(standardCode);// 单据号

				POTO45ChangeVOAdjust adjust = new POTO45ChangeVOAdjust();
				adjust.batchAdjustAfterChange(vos,
						new PurchaseInVO[] { purchaseInVO }, null);
				// 保存采购入库
				IPurchaseInMaintain ipm = NCLocator.getInstance().lookup(
						IPurchaseInMaintain.class);
				PurchaseInVO[] aggobject = ipm
						.insert(new PurchaseInVO[] { purchaseInVO });
				ipm.sign(aggobject);
				// Object save = iplatFormEntry.processAction("WRITE", "45",
				// null,
				// purchaseInVO, null, null);
				// PurchaseInVO[] savevos = (PurchaseInVO[]) save;
				// // 签字
				// iplatFormEntry.processAction("SIGN", "45", null, savevos[0],
				// null, null);
				// if (ArrayUtils.isEmpty(savevos)) {
				// errorMsg = "返回值有误，保存采购入库单失败!";
				// return createMsg(false, "-1", errorMsg);
				// }
				errorMsg = "生成成功！采购入库单单号：" + standardCode;
				return createMsg(true, "0", errorMsg);
			}
		} catch (Exception e) {
			System.out.println(e);
			errorMsg = "生成失败！系统出现内部错误！错误信息：" + e.getMessage();
		}
		return createMsg(false, "-1", errorMsg);
	}

	/**
	 * 生成返回信息统一方法
	 * 
	 * @param isSuccess
	 * @param code
	 * @param msg
	 * @return
	 */
	private String createMsg(boolean isSuccess, String code, String msg) {
		Map<String, Object> msgMap = new HashMap<String, Object>();
		msgMap.put("success", isSuccess);
		msgMap.put("code", code);
		msgMap.put("msg", msg);
		Debug.error("上传采购订单返回结果：" + isSuccess + "; 返回信息：" + msg);
		Logger.error("上传采购订单返回结果：" + isSuccess + "; 返回信息：" + msg);
		return JSON.toJSONString(msgMap);
	}

	private String SaveBillLog(String recdata, String rdtdata)
			throws BusinessException {
		// TODO Auto-generated method stub
		BillLogVO vo = new BillLogVO();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		vo.setCreationtime(df.format(new Date()));// 创建时间
		vo.setTransi_type("45"); // 交易类型
		if (recdata.length() <= 4000) {
			vo.setRecdata(recdata);// 接收参数
		}
		vo.setRdtdata(rdtdata);// 返回参数
		vo.setPk_group("0001A1100000000001QS");
		try {
			new HYPubBO().insert(vo);
		} catch (BusinessException e) {
			// TODO: handle exception
			return "生成采购入库日志表保存失败：" + e.getMessage();
		}
		return "";
	}

	private static boolean isEmpty(String str) {
		return (str == null) || (str.length() == 0) || ("null".equals(str));
	}
}
