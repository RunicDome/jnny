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
import nc.bs.pub.pf.PfUtilTools;
import nc.bs.trade.business.HYPubBO;
import nc.itf.uap.pf.IPfExchangeService;
import nc.itf.uap.pf.IplatFormEntry;
import nc.pub.billcode.itf.IBillcodeManage;
import nc.vo.ic.m4455.entity.SapplyBillBodyVO;
import nc.vo.ic.m4455.entity.SapplyBillHeadVO;
import nc.vo.ic.m4455.entity.SapplyBillVO;
import nc.vo.ic.m4d.entity.MaterialOutBodyVO;
import nc.vo.ic.m4d.entity.MaterialOutVO;
import nc.vo.logging.Debug;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.pub.MathTool;
import nc.ws.intf.BillLogVO;

import org.codehaus.jettison.json.JSONObject;

import com.alibaba.fastjson.JSON;

//对接二维码，燃气材料出库JSON（关联出库申请单）导入
@SuppressWarnings({ "rawtypes", "unchecked", "restriction" })
public class SapplyBillServlet extends HttpServlet implements
		IHttpServletAdaptor {
	private static final long serialVersionUID = 1L;
	IPfExchangeService iPfExchangeService = NCLocator.getInstance().lookup(
			IPfExchangeService.class);
	IplatFormEntry iplatFormEntry = NCLocator.getInstance().lookup(
			IplatFormEntry.class);
	private static String BODYTABLE = "ic_sapply_b";
	private static String HEADTABLE = "ic_sapply_h";
	private static HYPubBO hypubBo = new HYPubBO();

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
		Debug.error("=============upload SapplyBill msg ：" + body);
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
					errorMsg += "数据格式不正确！";
					return createMsg(false, "-1", errorMsg);
				}
				Map headInfo = (Map) map.get(HEADTABLE);
				List<Map> bodyInfo = (List) map.get(BODYTABLE);

				String cgeneralhid = (String) headInfo.get("cgeneralhid");
				if (isEmpty(cgeneralhid)) {
					errorMsg += "请传入出库申请单主键！";
					return createMsg(false, "-1", errorMsg);
				}

				SapplyBillHeadVO hvo = (SapplyBillHeadVO) hypubBo
						.queryByPrimaryKey(SapplyBillHeadVO.class, cgeneralhid);
				if (hvo == null) {
					errorMsg += "未找到此出库申请单，请确认信息后重试！";
					return createMsg(false, "-1", errorMsg);
				}

				SapplyBillBodyVO[] bvoArr = (SapplyBillBodyVO[]) hypubBo
						.queryByCondition(SapplyBillBodyVO.class,
								" cgeneralhid='" + cgeneralhid + "'");
				String creator = (String) headInfo.get("creator");// 制单人/签字人/创建人
				String busidate= (String) headInfo.get("busidate");// 业务日期
				if (isEmpty(busidate)) {
					errorMsg += "请传入业务日期！";
					return createMsg(false, "-1", errorMsg);
				}
				if (isEmpty(creator)) {
					errorMsg += "请传入制单人主键！";
					return createMsg(false, "-1", errorMsg);
				}
				InvocationInfoProxy.getInstance().setUserId(creator);
				SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date busitime = timeFormat.parse(busidate);
				// 设置业务日期
				InvocationInfoProxy.getInstance().setBizDateTime(busitime.getTime());
				SapplyBillVO vo = new SapplyBillVO();
				vo.setParentVO(hvo);
				vo.setChildrenVO(bvoArr);
				MaterialOutVO outVO = (MaterialOutVO) PfUtilTools
						.runChangeData("4455", "4D", vo);
				if (outVO == null) {
					errorMsg += "单据转换失败，请确认信息后重试！";
					return createMsg(false, "-1", errorMsg);
				}

				String cwarehouseid = (String) headInfo.get("cwarehouseid");
				if (isEmpty(cwarehouseid)) {
					errorMsg += "请传入仓库！";
					return createMsg(false, "-1", errorMsg);
				}
				outVO.getParentVO().setCwarehouseid(cwarehouseid);

				MaterialOutBodyVO[] bodyVOs = outVO.getBodys();
				List returnBodyVOList = new ArrayList();
				MaterialOutBodyVO bodyVO = null;

				for (Map bodyMap : bodyInfo) {
					for (int i = 0; i < bodyVOs.length; ++i) {
						bodyVO = bodyVOs[i];

						if (!bodyVO.getCsourcebillbid().equals(
								bodyMap.get("csourcebillbid"))) {
							continue;
						}
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
						// 仓库是否有货位管理
						WarehouseArrCheck check = new WarehouseArrCheck();
						boolean iswhspace = check.checkCsFlag(cwarehouseid);
						if (iswhspace
								&& isEmpty((String) bodyMap.get("clocationid"))) {
							errorMsg += NCLangRes4VoTransl
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
						SapplyBillBodyVO bvo = (SapplyBillBodyVO) hypubBo
								.queryByPrimaryKey(SapplyBillBodyVO.class,
										(String) bodyMap.get("csourcebillbid"));
						UFDouble oldnassistnum = bvo.getNassistnum();// 出库申请数量
						UFDouble nassistnum = new UFDouble(
								(String) bodyMap.get("nassistnum"));// 传入数量
						if (oldnassistnum.compareTo(UFDouble.ZERO_DBL) > 0
								&& nassistnum.compareTo(UFDouble.ZERO_DBL) < 0) {
							errorMsg += "行号:" + bodyVO.getCrowno()
									+ "不允许为负数!申请数量:" + oldnassistnum + "";
							return createMsg(false, "-1", errorMsg);
						}
						if (oldnassistnum.compareTo(UFDouble.ZERO_DBL) < 0
								&& nassistnum.compareTo(UFDouble.ZERO_DBL) > 0) {
							errorMsg += "行号:" + bodyVO.getCrowno()
									+ "不允许为正数!申请数量:" + oldnassistnum + "";
							return createMsg(false, "-1", errorMsg);
						}
						
						if (MathTool.absCompareTo(oldnassistnum, nassistnum) < 0) {
							errorMsg += "行号:" + bodyVO.getCrowno()
									+ ",超出申请数量!申请数量:" + oldnassistnum
									+ ",出库数量：" + nassistnum + "";
							return createMsg(false, "-1", errorMsg);
						}
						bodyVO.setNassistnum(nassistnum);
						bodyVO.setNnum(nassistnum);
						// 2023-08-25 接口不传单价数据
//						bodyVO.setNcostprice(new UFDouble((String) bodyMap
//								.get("ncostprice")));
//						bodyVO.setNcostmny(bodyVO.getNassistnum().multiply(
//								bodyVO.getNcostprice()));

						returnBodyVOList.add(bodyVO);
					}
				}
				outVO.setChildrenVO((CircularlyAccessibleValueObject[]) returnBodyVOList
						.toArray(new MaterialOutBodyVO[0]));
				String standardCode = null;
				IBillcodeManage iBillcodeManage = (IBillcodeManage) NCLocator
						.getInstance().lookup(IBillcodeManage.class.getName());
				standardCode = iBillcodeManage.getBillCode_RequiresNew("4D",
						hvo.getPk_group(), hvo.getPk_org(), outVO.getParentVO());
				outVO.getParentVO().setVbillcode(standardCode);// 单据号
				// 保存材料出库
				Object save = iplatFormEntry.processAction("WRITE", "4D", null,
						outVO, null, null);
				MaterialOutVO[] savevos = (MaterialOutVO[]) save;
				// 签字
				iplatFormEntry.processAction("SIGN", "4D", null, savevos[0],
						null, null);
				errorMsg += "生成成功！材料出库单号：" + standardCode;
			}
			return createMsg(true, "0", errorMsg);
		} catch (Exception e) {
			System.out.println(e);
			errorMsg += "生成失败！系统出现内部错误！错误信息：" + e.getMessage();
		}
		return createMsg(false, "-1", errorMsg);
	}

	private String SaveBillLog(String recdata, String rdtdata)
			throws BusinessException {
		// TODO Auto-generated method stub
		BillLogVO vo = new BillLogVO();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		vo.setCreationtime(df.format(new Date()));// 创建时间
		vo.setTransi_type("4D"); // 交易类型
		if (recdata.length() <= 4000) {
			vo.setRecdata(recdata);// 接收参数
		}
		vo.setRdtdata(rdtdata);// 返回参数
		vo.setPk_group("0001A1100000000001QS");
		try {
			new HYPubBO().insert(vo);
		} catch (BusinessException e) {
			// TODO: handle exception
			return "生成材料出库日志表保存失败：" + e.getMessage();
		}
		return "";
	}

	private String createMsg(boolean isSuccess, String code, String msg) {
		Map msgMap = new HashMap();
		msgMap.put("success", Boolean.valueOf(isSuccess));
		msgMap.put("code", code);
		msgMap.put("msg", msg);
		Debug.error("出库申请单转材料出库单返回结果：" + isSuccess + "; 返回信息：" + msg);
		return JSON.toJSONString(msgMap);
	}

	private static boolean isEmpty(String str) {
		return (str == null) || (str.length() == 0) || ("null".equals(str));
	}
}