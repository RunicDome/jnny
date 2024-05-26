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
import nc.itf.ic.m4a.IGeneralInMaintain;
import nc.itf.ic.m4i.IGeneralOutMaintain;
import nc.pub.billcode.itf.IBillcodeManage;
import nc.vo.ic.m4a.entity.GeneralInBodyVO;
import nc.vo.ic.m4a.entity.GeneralInVO;
import nc.vo.ic.m4i.entity.GeneralOutBodyVO;
import nc.vo.ic.m4i.entity.GeneralOutVO;
import nc.vo.ic.m4k.entity.WhsTransBillBodyVO;
import nc.vo.ic.m4k.entity.WhsTransBillHeaderVO;
import nc.vo.ic.m4k.entity.WhsTransBillVO;
import nc.vo.logging.Debug;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDouble;
import nc.ws.intf.BillLogVO;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONObject;

import com.alibaba.fastjson.JSON;

//对接二维码，其他出入库单JSON（关联转库单）导入
@SuppressWarnings({ "rawtypes", "unchecked", "restriction" })
public class WhsTransBillServlet extends HttpServlet implements
		IHttpServletAdaptor {
	private static final long serialVersionUID = 1L;
	private static String BODYTABLE_ZC = "ic_whstrans_b_zc";
	private static String BODYTABLE_ZR = "ic_whstrans_b_zr";
	private static String HEADTABLE = "ic_whstrans_h";
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
		Debug.error("=============upload ic_whstrans_b msg ：" + body);
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
				if (!map.containsKey(HEADTABLE)) {
					errorMsg = "数据格式不正确！";
					return createMsg(false, "-1", errorMsg);
				}
				Map headInfo = (Map) map.get(HEADTABLE);

				String cspecialhid = (String) headInfo.get("cspecialhid");
				if (isEmpty(cspecialhid)) {
					errorMsg = "请传入主键！";
					return createMsg(false, "-1", errorMsg);
				}

				WhsTransBillHeaderVO hvo = (WhsTransBillHeaderVO) hypubBo
						.queryByPrimaryKey(WhsTransBillHeaderVO.class,
								cspecialhid);
				if (hvo == null) {
					errorMsg = "未找到此单据，请确认信息后重试！";
					return createMsg(false, "-1", errorMsg);
				}

				String pkOrg = hvo.getPk_org();
				String pkOrgV = hvo.getPk_org_v();

				WhsTransBillBodyVO[] bvoArr = (WhsTransBillBodyVO[]) hypubBo
						.queryByCondition(WhsTransBillBodyVO.class,
								" cspecialhid='" + cspecialhid + "'");

				WhsTransBillVO vo = new WhsTransBillVO();
				vo.setParentVO(hvo);
				vo.setChildrenVO(bvoArr);
				String creator = (String) headInfo.get("creator");// 制单人/签字人/创建人
				if (isEmpty(creator)) {
					errorMsg = "请传入制单人主键！";
					return createMsg(false, "-1", errorMsg);
				}
				String busidate = (String) headInfo.get("busidate");// 业务日期
				InvocationInfoProxy.getInstance().setUserId(creator);
				SimpleDateFormat timeFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date busitime = timeFormat.parse(busidate);
				// 设置业务日期
				InvocationInfoProxy.getInstance().setBizDateTime(
						busitime.getTime());
				GeneralOutVO outBodyVO = (GeneralOutVO) PfUtilTools
						.runChangeData("4K", "4I", vo);
				if (outBodyVO == null) {
					errorMsg = "单据转换其他出库单失败，请确认信息后重试！";
					return createMsg(false, "-1", errorMsg);
				}
				if (isEmpty(busidate)) {
					errorMsg = "请传入业务日期！";
					return createMsg(false, "-1", errorMsg);
				}
				outBodyVO.getParentVO().setCtrantypeid("0001A21000000000PFY7");
				outBodyVO.getParentVO().setVtrantypecode("4I-02");
				outBodyVO.getParentVO().setPk_org(pkOrg);
				outBodyVO.getParentVO().setPk_org_v(pkOrgV);

				GeneralOutBodyVO[] bodyVOs = outBodyVO.getBodys();
				GeneralOutBodyVO outBillBodyVO = null;
				List outBillList = new ArrayList();
				// 转出表体
				List<Map> zcbodyInfo = (List) map.get(BODYTABLE_ZC);
				// 转入表体
				List<Map> zrbodyInfo = (List) map.get(BODYTABLE_ZR);
				// 明细表校验
				errorMsg = checkDetail(zcbodyInfo, zrbodyInfo);
				if (StringUtils.isNotEmpty(errorMsg)) {
					return createMsg(false, "-1", errorMsg);
				}
				// 转出
				for (int i = 0; i < bodyVOs.length; ++i) {
					outBillBodyVO = bodyVOs[i];
					for (Map bodyMap : zcbodyInfo) {
						if (outBillBodyVO.getCsourcebillbid().equals(
								bodyMap.get("cspecialbid"))) {
							if (!isEmpty((String) bodyMap.get("vbatchcode"))) {
								outBillBodyVO.setVbatchcode((String) bodyMap
										.get("vbatchcode"));// 批次号
								String pk_batchcode = (String) bodyMap
										.get("pk_batchcode");
								if (!isEmpty(pk_batchcode)) {
									outBillBodyVO.setPk_batchcode(pk_batchcode);// 批次号主键
								}
							}
							// 仓库是否有货位管理
							String cwarehouseid = outBodyVO.getParentVO()
									.getCwarehouseid();// 仓库
							WarehouseArrCheck check = new WarehouseArrCheck();
							boolean iswhspace = check.checkCsFlag(cwarehouseid);
							if (iswhspace
									&& isEmpty((String) bodyMap
											.get("clocationid"))) {
								errorMsg = NCLangRes4VoTransl.getNCLangRes()
										.getStrByID(
												"4008001_0",
												"04008001-0687",
												null,
												new String[] { outBillBodyVO
														.getCrowno() });
								return createMsg(false, "-1", errorMsg);
							}
							if (!isEmpty((String) bodyMap.get("clocationid"))) {
								outBillBodyVO.setClocationid((String) bodyMap
										.get("clocationid"));// 货位
							}
							UFDouble nassistnum = new UFDouble(
									(String) bodyMap.get("nassistnum"));// 传入数量
							outBillBodyVO.setNassistnum(nassistnum);
							outBillBodyVO.setNnum(nassistnum);
							outBillBodyVO.setCrowno(outBillBodyVO
									.getVsourcerowno());
							outBillBodyVO.setStatus(VOStatus.NEW);
							outBillList.add(outBillBodyVO);
						}
					}
				}
				outBodyVO
						.setChildrenVO((CircularlyAccessibleValueObject[]) outBillList
								.toArray(new GeneralOutBodyVO[0]));
				// 转入
				GeneralInVO inBodyVO = (GeneralInVO) PfUtilTools.runChangeData(
						"4K", "4A", vo);
				if (inBodyVO == null) {
					errorMsg = "单据转换其他入库单失败，请确认信息后重试！";
					return createMsg(false, "-1", errorMsg);
				}

				inBodyVO.getParentVO().setCtrantypeid("0001A21000000000PFXK");
				inBodyVO.getParentVO().setVtrantypecode("4A-02");
				inBodyVO.getParentVO().setPk_org(pkOrg);
				inBodyVO.getParentVO().setPk_org_v(pkOrgV);

				GeneralInBodyVO[] inBodyVOs = inBodyVO.getBodys();
				GeneralInBodyVO inBillBodyVO = null;
				List inBillList = new ArrayList();
				for (int n = 0; n < inBodyVOs.length; ++n) {
					inBillBodyVO = inBodyVOs[n];
					for (Map bodyMap : zrbodyInfo) {
						if (inBillBodyVO.getCsourcebillbid().equals(
								bodyMap.get("cspecialbid"))) {
							if (!isEmpty((String) bodyMap.get("vbatchcode"))) {
								inBillBodyVO.setVbatchcode((String) bodyMap
										.get("vbatchcode"));// 批次号
								if (bodyMap.get("pk_batchcode") != null) {
									String pk_batchcode = (String) bodyMap
											.get("pk_batchcode");
									inBillBodyVO.setPk_batchcode(pk_batchcode);// 批次号主键
								}
							}
							// 仓库是否有货位管理
							String cwarehouseid = inBodyVO.getParentVO()
									.getCwarehouseid();// 仓库
							WarehouseArrCheck check = new WarehouseArrCheck();
							boolean iswhspace = check.checkCsFlag(cwarehouseid);
							if (iswhspace
									&& isEmpty((String) bodyMap
											.get("clocationid"))) {
								errorMsg = NCLangRes4VoTransl.getNCLangRes()
										.getStrByID(
												"4008001_0",
												"04008001-0687",
												null,
												new String[] { inBillBodyVO
														.getCrowno() });
								return createMsg(false, "-1", errorMsg);
							}
							if (!isEmpty((String) bodyMap.get("clocationid"))) {
								inBillBodyVO.setClocationid((String) bodyMap
										.get("clocationid"));// 货位
							}
							UFDouble nassistnum = new UFDouble(
									(String) bodyMap.get("nassistnum"));// 传入数量
							inBillBodyVO.setNassistnum(nassistnum);
							inBillBodyVO.setNnum(nassistnum);
							inBillBodyVO.setCrowno(inBillBodyVO
									.getVsourcerowno());
							inBillBodyVO.setStatus(VOStatus.NEW);
							inBillList.add(inBillBodyVO);
						}
					}
				}
				inBodyVO.setChildrenVO((CircularlyAccessibleValueObject[]) inBillList
						.toArray(new GeneralInBodyVO[0]));
				Boolean bool = Boolean.TRUE;
				try {
					String standardCode = null;
					IBillcodeManage iBillcodeManage = (IBillcodeManage) NCLocator
							.getInstance().lookup(
									IBillcodeManage.class.getName());
					standardCode = iBillcodeManage.getBillCode_RequiresNew(
							"4I", hvo.getPk_group(), hvo.getPk_org(),
							outBodyVO.getParentVO());
					outBodyVO.getParentVO().setVbillcode(standardCode);// 单据号
					((IGeneralOutMaintain) NCLocator.getInstance().lookup(
							IGeneralOutMaintain.class))
							.insert(new GeneralOutVO[] { outBodyVO });
					errorMsg = "其他出库生成成功，单号" + standardCode;
				} catch (Exception e) {
					// TODO: handle exception
					bool = Boolean.FALSE;
					errorMsg = "其他出库生成失败！错误信息：" + e.getMessage();
				}
				try {
					String standardCode = null;
					IBillcodeManage iBillcodeManage = (IBillcodeManage) NCLocator
							.getInstance().lookup(
									IBillcodeManage.class.getName());
					standardCode = iBillcodeManage.getBillCode_RequiresNew(
							"4A", hvo.getPk_group(), hvo.getPk_org(),
							inBodyVO.getParentVO());
					inBodyVO.getParentVO().setVbillcode(standardCode);// 单据号
					((IGeneralInMaintain) NCLocator.getInstance().lookup(
							IGeneralInMaintain.class))
							.insert(new GeneralInVO[] { inBodyVO });
					errorMsg += ";其他入库生成成功，单号" + standardCode;
				} catch (Exception e) {
					// TODO: handle exception
					bool = Boolean.FALSE;
					errorMsg += ";其他入库生成失败！错误信息：" + e.getMessage();
				}
				if (bool) {
					return createMsg(bool, "0", errorMsg);
				} else {
					return createMsg(bool, "-1", errorMsg);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			errorMsg = "生成失败！系统出现内部错误！错误信息：" + e.getMessage();
		}
		return createMsg(false, "-1", errorMsg);
	}

	// 校验明细数据是否合规
	private String checkDetail(List<Map> zcbodyInfo, List<Map> zrbodyInfo) {
		// TODO Auto-generated method stub
		String errorMsg = "";
		if (zcbodyInfo == null || zrbodyInfo == null
				|| zrbodyInfo.size() != zcbodyInfo.size()) {
			errorMsg = "明细参数有误，请检查！";
			return errorMsg;
		}
		int flag = 0;
		for (Map zcmap : zcbodyInfo) {
			if (zcmap.get("cspecialbid") == null) {
				errorMsg = "请传入转出对应出库明细子表主键，字段cspecialbid！";
				return errorMsg;
			}
			String zccspecialbid = zcmap.get("cspecialbid") + "";
			for (Map zrmap : zrbodyInfo) {
				if (zrmap.get("cspecialbid") == null) {
					errorMsg = "请传入转入对应出库明细子表主键，字段cspecialbid！";
					return errorMsg;
				}
				String zrcspecialbid = zrmap.get("cspecialbid") + "";
				if (StringUtils.equals(zccspecialbid, zrcspecialbid)) {
					flag = 1;
				}
			}
			if (flag == 0) {
				errorMsg = "转出对应出库明细子表主键[cspecialbid]未对应转出对应入库明细子表主键[cspecialbid]！";
				return errorMsg;
			}
			flag = 0;
		}
		return errorMsg;
	}

	private String createMsg(boolean isSuccess, String code, String msg) {
		Map msgMap = new HashMap();
		msgMap.put("success", Boolean.valueOf(isSuccess));
		msgMap.put("code", code);
		msgMap.put("msg", msg);
		Debug.error("转库单自动生成其他入库单返回结果：" + isSuccess + "; 返回信息：" + msg);
		return JSON.toJSONString(msgMap);
	}

	private String SaveBillLog(String recdata, String rdtdata)
			throws BusinessException {
		// TODO Auto-generated method stub
		BillLogVO vo = new BillLogVO();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		vo.setCreationtime(df.format(new Date()));// 创建时间
		vo.setTransi_type("4C"); // 交易类型
		if (recdata.length() <= 4000) {
			vo.setRecdata(recdata);// 接收参数
		}
		vo.setRdtdata(rdtdata);// 返回参数
		vo.setPk_group("0001A1100000000001QS");
		try {
			new HYPubBO().insert(vo);
		} catch (BusinessException e) {
			// TODO: handle exception
			return "生成转库单日志表保存失败：" + e.getMessage();
		}
		return "";
	}

	private static boolean isEmpty(String str) {
		return (str == null) || (str.length() == 0) || ("null".equals(str));
	}
}