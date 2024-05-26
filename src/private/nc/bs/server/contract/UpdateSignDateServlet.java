package nc.bs.server.contract;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
import nc.bs.logging.Logger;
import nc.bs.trade.business.HYPubBO;
import nc.ui.pcm.utils.GetDao;
import nc.uif.pub.exception.UifException;
import nc.vo.ct.purdaily.entity.CtPuVO;
import nc.vo.fct.ap.entity.CtApVO;
import nc.vo.fct.ar.entity.CtArVO;
import nc.vo.logging.Debug;
import nc.vo.pcm.contract.ContrHeadVO;
import nc.vo.pcm.contractalter.ContrAlterHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.ws.intf.BillLogVO;
import nc.ws.intf.WorkFlowBill;

import org.codehaus.jettison.json.JSONObject;

import com.alibaba.fastjson.JSON;

// OA回传合同签订日期，更新合同签订日期，自由态不可更新
@SuppressWarnings({ "rawtypes", "unchecked", "restriction" })
public class UpdateSignDateServlet extends HttpServlet implements
		IHttpServletAdaptor {
	private static final long serialVersionUID = 1L;
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
		Debug.error("=============OA Push SingDate msg ：" + body);
		String request = updateSignDate(body);
		res.setCharacterEncoding("utf-8");
		System.out.println("=================" + request);
		res.setStatus(200);
		res.getWriter().write(request);
	}

	private String updateSignDate(String body) {
		InvocationInfoProxy.getInstance().setGroupId("0001A1100000000001QS");
		String errorMsg = "";
		UFDate htqdrq = null;// OA传入的合同签订日期
		UFDate topSignDate = null;// 修改前合同签订日期
		String pk_org = null;// 日志记录-所属组织
		String updSignSql = "";// 更新合同签订日期SQL，执行UPDATE
		String bill_code = "";// 合同编码
		try {
			JSONObject jsonObj = new JSONObject(body);
			String requestid = jsonObj.getString("requestid");// OA流程ID
			String sign_date = jsonObj.getString("sign_date");// 合同签订日期
			// 查询单据流程记录
			String strWhere = " nvl(dr,0) = 0 and requestid = " + requestid
					+ " and def3 = 'ZT'";
			WorkFlowBill[] workFlowBill = (WorkFlowBill[]) getHyPubBO()
					.queryByCondition(WorkFlowBill.class, strWhere);
			if (null == workFlowBill || workFlowBill.length != 1) {
				errorMsg = "未查询到流程日志";
			}
			if (isEmpty(errorMsg)) {
				WorkFlowBill flowBill = workFlowBill[0];
				String pk_bill = flowBill.getPk_bill();// 合同主键
				String bill_type = flowBill.getBill_code();// 合同类型
				if (isEmpty(pk_bill) || isEmpty(sign_date)
						|| isEmpty(bill_type)) {
					errorMsg = "传入参数有误，请检查！";
				}
				if (isEmpty(errorMsg)) {
					try {
						htqdrq = new UFDate(sign_date);
						// 清单合同
						if ("4D42".equals(bill_type)) {
							ContrHeadVO hvo = (ContrHeadVO) getHyPubBO()
									.queryByPrimaryKey(ContrHeadVO.class,
											pk_bill);
							if (hvo == null) {
								errorMsg = "根据[" + body + "]查询合同失败！";
							}
							if (isEmpty(errorMsg)) {
								if (hvo.getBill_status() != -1) {
									// 修改前合同签订日期
									topSignDate = hvo.getPromisetime();
									bill_code = hvo.getBill_code();
									updSignSql = "update pm_contr set promisetime = '"
											+ htqdrq
											+ "' where pk_contr = '"
											+ pk_bill + "' and dr = 0";
								} else {
									errorMsg = "合同状态为自由态，不允许修改！";
								}
							}
						} else if ("4D44".equals(bill_type)) {
							// 清单发包合同补充协议
							ContrAlterHeadVO hvo = (ContrAlterHeadVO) getHyPubBO()
									.queryByPrimaryKey(ContrAlterHeadVO.class,
											pk_bill);
							if (hvo == null) {
								errorMsg = "根据[" + body + "]查询合同失败！";
							}
							if (isEmpty(errorMsg)) {
								if (hvo.getBill_status() != -1) {
									// 修改前 协议日期
									topSignDate = hvo.getAlter_date();
									bill_code = hvo.getBill_code();
									updSignSql = "update pm_contr_alter set alter_date = '"
											+ htqdrq
											+ "' where pk_contr_alter = '"
											+ pk_bill + "' and dr = 0";
								} else {
									errorMsg = "合同状态为自由态，不允许修改！";
								}
							}
						} else if ("Z2".equals(bill_type)) {
							// 采购合同
							CtPuVO hvo = (CtPuVO) getHyPubBO()
									.queryByPrimaryKey(CtPuVO.class, pk_bill);
							if (hvo == null) {
								errorMsg = "根据[" + body + "]查询合同失败！";
							}
							if (isEmpty(errorMsg)) {
								if (hvo.getFstatusflag() != 0) {
									// 修改前合同签订日期
									topSignDate = hvo.getSubscribedate();
									bill_code = hvo.getVbillcode();
									updSignSql = "update ct_pu set subscribedate = '"
											+ htqdrq
											+ "' where pk_ct_pu = '"
											+ pk_bill + "' and dr = 0";
								} else {
									errorMsg = "合同状态为自由态，不允许修改！";
								}
							}
						} else if ("FCT1".equals(bill_type)) {
							// 付款合同
							CtApVO hvo = (CtApVO) getHyPubBO()
									.queryByPrimaryKey(CtApVO.class, pk_bill);
							if (hvo == null) {
								errorMsg = "根据[" + body + "]查询合同失败！";
							}
							if (isEmpty(errorMsg)) {
								if (hvo.getFstatusflag() != 0) {
									// 修改前合同签订日期
									topSignDate = hvo.getSubscribedate();
									bill_code = hvo.getVbillcode();
									updSignSql = "update fct_ap set subscribedate = '"
											+ htqdrq
											+ "' where pk_fct_ap = '"
											+ pk_bill + "' and dr = 0";
								} else {
									errorMsg = "合同状态为自由态，不允许修改！";
								}
							}
						} else if ("FCT2".equals(bill_type)) {
							// 付款合同
							CtArVO hvo = (CtArVO) getHyPubBO()
									.queryByPrimaryKey(CtArVO.class, pk_bill);
							if (hvo == null) {
								errorMsg = "根据[" + body + "]查询合同失败！";
							}
							if (isEmpty(errorMsg)) {
								if (hvo.getFstatusflag() != 0) {
									// 修改前合同签订日期
									topSignDate = hvo.getSubscribedate();
									bill_code = hvo.getVbillcode();
									updSignSql = "update fct_ar set subscribedate = '"
											+ htqdrq
											+ "' where pk_fct_ar = '"
											+ pk_bill + "' and dr = 0";
								} else {
									errorMsg = "合同状态为自由态，不允许修改！";
								}
							}
						}else {
							errorMsg = "[" + bill_type + "]非指定合同类型！";
						}
						if (isEmpty(errorMsg)) {
							GetDao getDao = NCLocator.getInstance().lookup(
									GetDao.class);
							try {
								getDao.executeUpdate(updSignSql);
							} catch (BusinessException e) {
								// TODO: handle exception
								e.printStackTrace();
								Logger.error(e);
								errorMsg = "更新合同签订日期失败!" + e.getMessage();
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						Logger.error(e);
						errorMsg = "合同签订日期sign_date[" + sign_date + "]日期解析失败!";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e);
			errorMsg = "系统异常：" + e.getMessage();
		}
		// 保存日志
		BillLogVO logVO = getBillLog();
		logVO.setRecdata(body);// 接收参数
		if (isEmpty(errorMsg)) {
			logVO.setRdtdata(createMsg(true, "0", errorMsg));// 返回参数
		} else {
			logVO.setRdtdata(createMsg(false, "-1", errorMsg));// 返回参数
		}
		if (!isEmpty(pk_org)) {
			logVO.setPk_org(pk_org);// 组织
		}
		if (!isEmpty(bill_code)) {
			logVO.setBill_code(bill_code);// 合同编码/补充协议号
		}
		if (topSignDate != null) {
			logVO.setDef1(topSignDate.toString());// 修改前合同签订日期
		}
		logVO.setDef2(updSignSql);// 更新SQL语句
		try {
			new HYPubBO().insert(logVO);
		} catch (UifException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.error(e);
			return createMsg(false, "-1", "保存日志失败：" + e.getMessage());
		}
		// 保存日志结束
		if (isEmpty(errorMsg)) {
			return createMsg(true, "0", errorMsg);
		} else {
			return createMsg(false, "-1", errorMsg);
		}
	}

	private BillLogVO getBillLog() {
		// TODO Auto-generated method stub
		BillLogVO vo = new BillLogVO();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		vo.setCreationtime(df.format(new Date()));// 创建时间
		vo.setTransi_type("HTQDRQ"); // 类型
		vo.setPk_group("0001A1100000000001QS");
		return vo;
	}

	private String createMsg(boolean isSuccess, String code, String msg) {
		Map msgMap = new HashMap();
		msgMap.put("success", Boolean.valueOf(isSuccess));
		msgMap.put("code", code);
		msgMap.put("msg", msg);
		Debug.error("返回OA结果：" + isSuccess + "; 返回信息：" + msg);
		return JSON.toJSONString(msgMap);
	}

	private static boolean isEmpty(String str) {
		return (str == null) || (str.length() == 0)
				|| ("null".equals(str) || "".equals(str));
	}
}