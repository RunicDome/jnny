package nc.so.servlet;

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
import nc.bs.pub.pf.PfUtilTools;
import nc.bs.trade.business.HYPubBO;
import nc.itf.uap.pf.IplatFormEntry;
import nc.pub.billcode.itf.IBillcodeManage;
import nc.vo.ic.general.define.MetaNameConst;
// import nc.vo.ic.general.define.MetaNameConst;
import nc.vo.ic.m4c.entity.SaleOutBodyVO;
import nc.vo.ic.m4c.entity.SaleOutVO;
import nc.vo.ic.pub.calc.BusiCalculator;
// import nc.vo.ic.pub.calc.BusiCalculator;
import nc.vo.logging.Debug;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFDouble;
import nc.vo.so.m30.entity.SaleOrderBVO;
import nc.vo.so.m30.entity.SaleOrderHVO;
import nc.vo.so.m30.entity.SaleOrderVO;
import nc.ws.intf.BillLogVO;

import org.codehaus.jettison.json.JSONObject;

import com.alibaba.fastjson.JSON;

//对接二维码，燃气销售出库JSON（关联销售订单）导入
@SuppressWarnings({ "restriction", "rawtypes", "unchecked" })
public class SaleOutServlet extends HttpServlet implements
		IHttpServletAdaptor {
	private static final long serialVersionUID = 1L;
	private static String BODYTABLE = "so_saleorder_b";
	private static String HEADTABLE = "so_saleorder";
	private static HYPubBO hypubBo = new HYPubBO();
	IplatFormEntry iplatFormEntry = NCLocator.getInstance().lookup(
			IplatFormEntry.class);

	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doAction(req, res);
	}

	public void doAction(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		ISecurityTokenCallback sc = (ISecurityTokenCallback) NCLocator
				.getInstance().lookup(ISecurityTokenCallback.class);
		byte[] token = sc.token("NCSystem".getBytes(), "pfxx".getBytes());

		ISecurityTokenCache tokencache = (ISecurityTokenCache) NCLocator
				.getInstance().lookup(ISecurityTokenCache.class);
		tokencache.isContain(token);
		req.setCharacterEncoding("utf-8");
		BufferedReader br = req.getReader();
		String body = "";
		String line = null;
		while ((line = br.readLine()) != null) {
			body = body + line;
		}
		Debug.error("=============upload saleorder msg ：" + body);
		String request = createVO(body);
		try {
			request += SaveBillLog(body, request);// 保存日志
		} catch (BusinessException e) {
			e.printStackTrace();
			request += "保存日志报错：" + e.getMessage();
		}
		res.setCharacterEncoding("utf-8");
		System.out.println("=================" + request);
		res.setStatus(200);
		res.getWriter().write(request);
	}

	private String createVO(String body) {
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

				String csaleorderid = (String) headInfo.get("csaleorderid");
				if (isEmpty(csaleorderid)) {
					errorMsg = "请传入销售订单主键！";
					return createMsg(false, "-1", errorMsg);
				}

				SaleOrderHVO saleOrderHVO = (SaleOrderHVO) hypubBo
						.queryByPrimaryKey(SaleOrderHVO.class, csaleorderid);
				if (saleOrderHVO == null) {
					errorMsg = "未找到此销售订单，请确认信息后重试！";
					return createMsg(false, "-1", errorMsg);
				}
				String busidate= (String) headInfo.get("busidate");// 业务日期
				if (isEmpty(busidate)) {
					errorMsg = "请传入业务日期！";
					return createMsg(false, "-1", errorMsg);
				}
				String creator = (String) headInfo.get("creator");// 制单人/签字人/创建人
				if (isEmpty(creator)) {
					errorMsg = "请传入制单人主键！";
					return createMsg(false, "-1", errorMsg);
				}
				InvocationInfoProxy.getInstance().setUserId(creator);
				SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date busitime = timeFormat.parse(busidate);
				// 设置业务日期
				InvocationInfoProxy.getInstance().setBizDateTime(busitime.getTime());
				SaleOrderBVO[] saleOrderBVOArray = (SaleOrderBVO[]) hypubBo
						.queryByCondition(SaleOrderBVO.class, " csaleorderid='"
								+ csaleorderid + "'");

				SaleOrderVO saleOrderVO = new SaleOrderVO();
				saleOrderVO.setParentVO(saleOrderHVO);
				saleOrderVO.setChildrenVO(saleOrderBVOArray);
				SaleOutVO saleOutVO = (SaleOutVO) PfUtilTools.runChangeData(
						"30", "4C", saleOrderVO);
				if (saleOutVO == null) {
					errorMsg = "单据转换失败，请确认信息后重试！";
					return createMsg(false, "-1", errorMsg);
				}

				String cwarehouseid = (String) headInfo.get("cwarehouseid");
				if (isEmpty(cwarehouseid)) {
					errorMsg = "请传入仓库！";
					return createMsg(false, "-1", errorMsg);
				}
				saleOutVO.getParentVO().setCwarehouseid(cwarehouseid);

				SaleOutBodyVO[] bodyVOs = saleOutVO.getBodys();
				List returnBodyVOList = new ArrayList();
				SaleOutBodyVO bodyVO = null;
				// BusiCalculator calc = BusiCalculator.getBusiCalculatorAtBS();
				for (Map bodyMap : bodyInfo) {
					for (int i = 0; i < bodyVOs.length; ++i) {
						bodyVO = bodyVOs[i];

						if (!bodyVO.getCsourcebillbid().equals(
								bodyMap.get("csourcebillbid"))) {
							continue;
						}
						// XBX新增批次号
						if (!isEmpty((String) bodyMap.get("vbatchcode"))) {
							String pk_batchcode = (String) bodyMap.get("pk_batchcode");
							if (!isEmpty(pk_batchcode)) {
								bodyVO.setPk_batchcode(pk_batchcode);// 批次号主键
							}
							bodyVO.setVbatchcode((String) bodyMap
									.get("vbatchcode"));// 批次号
						}
						// 寄存供应商
						if (!isEmpty((String) bodyMap.get("cvmivenderid"))) {
							bodyVO.setCvmivenderid((String) bodyMap.get("cvmivenderid"));
						}
						bodyVO.setCbodywarehouseid(cwarehouseid);
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
						UFDouble nassistnum = new UFDouble(
								(String) bodyMap.get("nassistnum"));
						bodyVO.setNassistnum(nassistnum);
						bodyVO.setNnum(nassistnum);
						bodyVO.setNqtunitnum(nassistnum);
						bodyVO.setNshouldassistnum(bodyVO.getNshouldnum());
						// 2023-08-25 接口不传单价数据
//						bodyVO.setNcostprice(new UFDouble((String) bodyMap
//								.get("ncostprice")));
//						bodyVO.setNcostmny(bodyVO.getNassistnum().multiply(
//								bodyVO.getNcostprice()));
//						bodyVO.setNbdiscountrate(new UFDouble(100.00));// 整单折扣
//						calc.calcMny(new SaleOutBodyVO[] { bodyVO },
//								MetaNameConst.NNUM);
						BusiCalculator calc = BusiCalculator.getBusiCalculatorAtBS();
						calc.calcMny(new SaleOutBodyVO[] { bodyVO },
								MetaNameConst.NNUM);
						returnBodyVOList.add(bodyVO);
					}
				}
				saleOutVO
						.setChildrenVO((CircularlyAccessibleValueObject[]) returnBodyVOList
								.toArray(new SaleOutBodyVO[0]));

				// ((ISaleOutMaintain) NCLocator.getInstance().lookup(
				// ISaleOutMaintain.class))
				// .insert(new SaleOutVO[] { saleOutVO });
				// 保存销售出库
				String standardCode = null;
				IBillcodeManage iBillcodeManage = (IBillcodeManage) NCLocator
						.getInstance().lookup(IBillcodeManage.class.getName());
				standardCode = iBillcodeManage.getBillCode_RequiresNew("4C",
						saleOutVO.getParentVO().getPk_group(), saleOutVO.getParentVO().getPk_org(), saleOutVO.getParentVO());
				saleOutVO.getParentVO().setVbillcode(standardCode);// 单据号
				Object save = iplatFormEntry.processAction("WRITE", "4C", null,
						saleOutVO, null, null);
				SaleOutVO[] savevos = (SaleOutVO[]) save;
				// 签字
				iplatFormEntry.processAction("SIGN", "4C", null, savevos[0],
						null, null);
				errorMsg = "生成成功！销售出库单号：" + standardCode;
			}
			return createMsg(true, "0", errorMsg);
		} catch (Exception e) {
			System.out.println(e);
			errorMsg = "生成失败！系统出现内部错误！错误信息：" + e.getMessage();
		}
		return createMsg(false, "-1", errorMsg);
	}

	private String createMsg(boolean isSuccess, String code, String msg) {
		Map msgMap = new HashMap();
		msgMap.put("success", Boolean.valueOf(isSuccess));
		msgMap.put("code", code);
		msgMap.put("msg", msg);
		Debug.error("销售订单转销售出库单返回结果：" + isSuccess + "; 返回信息：" + msg);
		return JSON.toJSONString(msgMap);
	}

	private String SaveBillLog(String recdata, String rdtdata)
			throws BusinessException {
		// TODO Auto-generated method stub
		BillLogVO vo = new BillLogVO();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		vo.setCreationtime(df.format(new Date()));// 创建时间
		vo.setTransi_type("4C"); // 交易类型
		vo.setRecdata(recdata);// 接收参数
		vo.setRdtdata(rdtdata);// 返回参数
		vo.setPk_group("0001A1100000000001QS");
		try {
			new HYPubBO().insert(vo);
		} catch (BusinessException e) {
			// TODO: handle exception
			return "生成销售出库库日志表保存失败：" + e.getMessage();
		}
		return "";
	}

	private static boolean isEmpty(String str) {
		return (str == null) || (str.length() == 0) || ("null".equals(str));
	}
}