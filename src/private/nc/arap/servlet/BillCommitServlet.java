package nc.arap.servlet;

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
import nc.bs.trade.business.HYPubBO;
import nc.itf.arap.pay.IArapPayBillQueryService;
import nc.itf.uap.pf.IWorkflowMachine;
import nc.itf.uap.pf.IplatFormEntry;
import nc.vo.arap.pay.AggPayBillVO;
import nc.vo.arap.pay.PayBillVO;
import nc.vo.arap.utils.ArrayUtil;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.pubapp.pflow.PfUserObject;
import nc.vo.pubapp.util.NCPfServiceUtils;
import nc.ws.intf.BillLogVO;
import nc.ws.intf.WorkFlowBill;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONObject;

import uap.iweb.log.Logger;

import com.alibaba.fastjson.JSON;

// 薪酬付款审批单提交--宏景
@SuppressWarnings({ "rawtypes", "unchecked", "restriction" })
public class BillCommitServlet extends HttpServlet implements
		IHttpServletAdaptor {
	private static final long serialVersionUID = 1L;
	private PfUserObject[] userObjs;
	private String pk_tradetype = null;
	
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
		Debug.error("=============commit paybill msg :" + body);
		String request = updatePaybill(body);
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

	private String updatePaybill(String body) {
		// TODO Auto-generated method stub
		InvocationInfoProxy.getInstance().setGroupId("0001A1100000000001QS");
		String errorMsg = "";
		try {
			JSONObject jsonObj = new JSONObject(body);
			String pk_paybill = jsonObj.getString("pk_paybill");
			String state = jsonObj.getString("state");// 提交：01、删除：02
			if (StringUtils.isEmpty(pk_paybill)) {
				return createMsg(false, "-1", "pk_paybill付款单主键不能为空！");
			}
			IArapPayBillQueryService cs = (IArapPayBillQueryService) NCLocator
					.getInstance().lookup(IArapPayBillQueryService.class);
			AggPayBillVO[] wq = cs.queryBillsByWhereSQL(" PK_PAYBILL = '"
					+ pk_paybill + "' AND DR = 0");
			if (wq == null || wq.length <= 0) {
				if ("02".equals(state)) {
					return createMsg(true, "0", "[" + pk_paybill
							+ "]对应单据不存在！");
				}
				return createMsg(false, "-1", "[" + pk_paybill
						+ "]对应单据不存在！");
			}
			AggPayBillVO aggvo = wq[0];
			PayBillVO hvo = (PayBillVO) aggvo.getParentVO();
			pk_tradetype = hvo.getPk_tradetype();
			if (hvo.getApprovestatus().intValue() != -1) {
				return createMsg(false, "-1", "付款单号[" + hvo.getBillno()
						+ "]单据状态不为自由态，不能操作！");
			}
			InvocationInfoProxy.getInstance().setUserId(hvo.getBillmaker());
			String actionName = "START";
			if ("02".equals(state)) {
				actionName = "DELETE";
				ArrayUtil.getFirstInArrays((Object[]) NCPfServiceUtils
						.processBatch(actionName, hvo.getPk_billtype(),
								new AggPayBillVO[] { aggvo }, getUserObj(),
								new WorkflownoteVO()));
				return createMsg(true, "0", "操作成功！");
			} else {
				String strWhere = " nvl(dr,0) = 0 and pk_bill = '" + pk_paybill
						+ "'";
				WorkFlowBill[] workFlowBill = (WorkFlowBill[]) new HYPubBO()
						.queryByCondition(WorkFlowBill.class, strWhere);
				if (null == workFlowBill || workFlowBill.length != 1
						|| "return".equals(workFlowBill[0].getDef1())) {
					ArrayUtil.getFirstInArrays((Object[]) NCPfServiceUtils
							.processBatch(actionName, hvo.getPk_billtype(),
									new AggPayBillVO[] { aggvo }, getUserObj(),
									new WorkflownoteVO()));
				} else {
					HashMap hmPfExParams = new HashMap();
					WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
							.getInstance().lookup(IWorkflowMachine.class))
							.checkWorkFlow("SIGNAL", hvo.getPk_tradetype()
									.toString(), aggvo, hmPfExParams);
					if (worknoteVO != null) {
						getIplatFormEntry().processAction("SIGNAL", "F3",
								worknoteVO, aggvo, null, getEparam());
					}
				}
				return createMsg(true, "0", "操作成功！");
			}
		} catch (Exception e) {
			System.out.println(e);
			Logger.error(e.getCause());
			errorMsg = "操作失败！系统出现内部错误！错误信息：" + e.getMessage();
		}
		return createMsg(false, "-1", errorMsg);
	}

	private IplatFormEntry getIplatFormEntry() {
		return NCLocator.getInstance().lookup(IplatFormEntry.class);
	}

	private HashMap<String, Object> getEparam() {
		HashMap<String, Object> eParam = new HashMap<String, Object>();
		eParam.put("notechecked", "notechecked");
		return eParam;
	}

	public PfUserObject[] getUserObj() {
		if (userObjs == null) {
			userObjs = new PfUserObject[] { new PfUserObject() };
		}
		return userObjs;
	}

	private String SaveBillLog(String recdata, String rdtdata)
			throws BusinessException {
		// TODO Auto-generated method stub
		BillLogVO vo = new BillLogVO();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		vo.setCreationtime(df.format(new Date()));// 创建时间
		vo.setTransi_type(pk_tradetype); // 交易类型
		vo.setRecdata(recdata);// 接收参数
		vo.setRdtdata(rdtdata);// 返回参数
		vo.setPk_group("0001A1100000000001QS");
		try {
			new HYPubBO().insert(vo);
		} catch (BusinessException e) {
			// TODO: handle exception
			return "提交付款单日志记录保存失败：" + e.getMessage();
		}
		return "";
	}

	private String createMsg(boolean isSuccess, String code, String msg) {
		Map msgMap = new HashMap();
		msgMap.put("success", Boolean.valueOf(isSuccess));
		msgMap.put("code", code);
		msgMap.put("msg", msg);
		Debug.error("提交付款单返回结果：" + isSuccess + "; 返回信息：" + msg);
		return JSON.toJSONString(msgMap);
	}
}