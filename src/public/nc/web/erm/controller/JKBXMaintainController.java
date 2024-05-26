package nc.web.erm.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.bs.logging.Logger;
import nc.bs.trade.business.HYPubBO;
import nc.itf.arap.file.IArapServiceForFilePreview;
import nc.itf.arap.forthird.IArapForDGSWService;
import nc.itf.arap.gathering.IArapGatheringBillServiceForZQ;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.ysyf.IArapServiceForI8;
import nc.itf.arap.ysyf.IArapServiceForSC;
import nc.itf.er.pub.IArapBillTypePublic;
import nc.itf.erm.fieldmap.IBillFieldGet;
import nc.itf.erm.fprelation.IErmFPRelation;
import nc.itf.erm.jkbx.IErmServiceForI8;
import nc.itf.erm.web.IWebPubService;
import nc.itf.fct.ap.IApMaintain;
import nc.itf.fct.ar.IArMaintain;
import nc.itf.pcm.contract.pub.IContractService;
import nc.itf.pubapp.pub.smart.IBillQueryService;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.nweb.erm.pub.ERMWebConst;
import nc.nweb.erm.util.CheckIsFreeCust;
import nc.nweb.erm.util.OrgChangedLoadDefData;
import nc.ssc.fiweb.pub.RequestParam;
import nc.ui.pcm.utils.GetDao;
import nc.uif.pub.exception.UifException;
import nc.vo.ct.purdaily.entity.AggCtPuVO;
import nc.vo.ct.purdaily.entity.CtPuBVO;
import nc.vo.ct.purdaily.entity.CtPuTermVO;
import nc.vo.ct.purdaily.entity.CtPuVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.erm.BXBillVO;
import nc.vo.erm.ErmFpRelationVO;
import nc.vo.erm.JKBillVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.fct.ap.entity.AggCtApVO;
import nc.vo.fct.ap.entity.CtApBVO;
import nc.vo.fct.ap.entity.CtApVO;
import nc.vo.fct.ar.entity.AggCtArVO;
import nc.vo.fct.ar.entity.CtArBVO;
import nc.vo.fct.ar.entity.CtArVO;
import nc.vo.pcm.contract.ContrHeadVO;
import nc.vo.pcm.contract.ContrItemsVO;
import nc.vo.pcm.contract.ContrWorksVO;
import nc.vo.pcm.contract.ContractBillVO;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pm.feebalance.PmFeebalanceBVO;
import nc.vo.pm.feebalance.PmFeebalanceHVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.sm.UserVO;
import nc.web.datatrans.itf.ITranslateDataService;
import nc.web.datatrans.itf.ITranslateVODataService;
import nc.web.erm.utils.ERMBillVOUtil;
import nc.web.erm.utils.ERMPfUtil;
import nc.web.erm.utils.ERMValueCheck;
import nc.web.erm.utils.ERMWEBUtil;
import nc.web.erm.utils.JKBXUtils;
import nc.web.erm.utils.JsonMessageUtil;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.OracleCodec;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import uap.iweb.exception.WebRuntimeException;
import uap.iweb.pub.bill.BillItem;
import uap.iweb.pub.bill.util.BillTemplateWebUtil;
import uap.pub.fs.client.FileStorageClient;

@SuppressWarnings({ "unused", "restriction", "deprecation" })
@Controller
@RequestMapping("/jkbx_maintain_ctr")
public class JKBXMaintainController {
	@RequestMapping(value = "/isFreecust", method = RequestMethod.GET)
	public @ResponseBody
	void isFreecust(HttpServletRequest request, HttpServletResponse response)
			throws JSONException {
		JSONObject json = new JSONObject();
		String field = request.getParameter("field");
		String pk = request.getParameter("pk");
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		try {
			boolean isfreecust = false;
			if (ERMValueCheck.isNotEmpty(pk)) {
				isfreecust = CheckIsFreeCust.checkcustsuppIsFreeCust(field,
						new String[] { pk });
			}
			if (isfreecust) {
				json.put("flag", "true");
			} else {
				json.put("flag", "false");
			}
			json.put("success", "true");
			response.getWriter().write(json.toString());
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/handleurl", method = RequestMethod.POST)
	public @ResponseBody
	void handleUrl(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String orgi_url = request.getParameter("orgi_url");// 初始的URL
		String tradeType = orgi_url.split("/")[3];
		String pk_billtemplet = orgi_url.split("/")[5];
		boolean needHandle = false;
		BillTempletVO btvo = BillTemplateWebUtil.getBTVOByPK(pk_billtemplet);
		if (null != btvo) {
			String pk_billtypecode = btvo.getHeadVO().getPkBillTypeCode();
			if (pk_billtypecode.indexOf("_WEB") == -1) {
				needHandle = true;
			}
		}
		if (needHandle && null != tradeType) {
			if ("2647".equals(tradeType)) {// 还款单
				orgi_url = orgi_url.replaceAll(pk_billtemplet,
						"1001Z310000000007OGU");
			} else if ("264X-Cxx-TYBXD".equals(tradeType)) {// 通用报销单
				orgi_url = orgi_url.replaceAll(pk_billtemplet,
						"1001Z310000000009MEX");
			} else if ("263X-Cxx-TYJKD".equals(tradeType)) {// 通用借款单
				orgi_url = orgi_url.replaceAll(pk_billtemplet,
						"1001Z310000000009M7T");
			} else if (tradeType.startsWith("264")) {// 报销类单据
				orgi_url = orgi_url.replaceAll(pk_billtemplet,
						"1001Z310000000007BC7");
			} else if (tradeType.startsWith("263")) {// 借款类单据
				orgi_url = orgi_url.replaceAll(pk_billtemplet,
						"1001Z310000000007B4B");
			} else if (tradeType.startsWith("261")) {// 申请类单据
				orgi_url = orgi_url.replaceAll(pk_billtemplet,
						"1001AA10000000000G7K");
			}
		}
		JSONObject json = new JSONObject();
		String jsonStr = "";
		try {
			json.put("success", "true");
			json.put("url", orgi_url);
			jsonStr = json.toString();
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonStr);
		response.flushBuffer();
	}

	@RequestMapping(value = "/copybill", method = RequestMethod.GET)
	public @ResponseBody
	void copyBill(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String openbillid = request.getParameter("openbillid");
		String pk_tradetype = request.getParameter("tradetype");
		String pk_billtemplet = request.getParameter("pk_billtemplet");
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		List<BillItem> billItems = BillTemplateWebUtil
				.getBillItems(pk_billtemplet);
		String jsonStr = "";
		if (StringUtils.isNotBlank(openbillid)) {
			String[] pks = { openbillid };
			JSONObject json = new JSONObject();
			try {
				IArapBillTypePublic arapBillType = NCLocator.getInstance()
						.lookup(IArapBillTypePublic.class);
				DjLXVO djlxvo = arapBillType.getDjlxvoByDjlxbm(pk_tradetype,
						pk_group);
				if (djlxvo.getIs_mactrl() != null
						&& djlxvo.getIs_mactrl().booleanValue()) {
					json.put("success", "false");
					json.put("message", "拉单的单据不能进行复制！");
					response.getWriter().write(json.toString());
					return;
				}
				List<JKBXVO> jkbxvoList = null;
				if (pk_tradetype.startsWith("264a")) {// 调整单
					jkbxvoList = getIBXBillPrivate().queryVOsByPrimaryKeys(pks,
							"bx");
					if (ERMValueCheck.isEmpty(jkbxvoList)) {
						throw new nc.vo.pub.BusinessException("未查找到相应单据！");
					}
					JKBXVO copyVO = (JKBXVO) jkbxvoList.get(0).clone();
					ERMBillVOUtil.doCopy4Adjust(copyVO);
					copyVO.setAccruedVerifyVO(null);// 不复制核销信息
					// ERMBillVOUtil.dealVODigit(copyVO);//处理精度
					json = NCLocator
							.getInstance()
							.lookup(ITranslateVODataService.class)
							.transAggvoToJSON(
									BXBillVO.getBXBillVO((BXVO) copyVO),
									billItems);
				} else if (pk_tradetype.startsWith("264")) {// 报销单
					jkbxvoList = getIBXBillPrivate().queryVOsByPrimaryKeys(pks,
							"bx");
					if (ERMValueCheck.isEmpty(jkbxvoList)) {
						throw new nc.vo.pub.BusinessException("未查找到相应单据！");
					}
					JKBXVO copyVO = (JKBXVO) jkbxvoList.get(0).clone();
					ERMBillVOUtil.doCopy(copyVO);
					copyVO.setAccruedVerifyVO(null);// 不复制核销信息
					// ERMBillVOUtil.dealVODigit(copyVO);//处理精度
					json = NCLocator
							.getInstance()
							.lookup(ITranslateVODataService.class)
							.transAggvoToJSON(
									BXBillVO.getBXBillVO((BXVO) copyVO),
									billItems);
				} else if (pk_tradetype.startsWith("263")) {// 借款单
					jkbxvoList = getIBXBillPrivate().queryVOsByPrimaryKeys(pks,
							"jk");
					if (ERMValueCheck.isEmpty(jkbxvoList)) {
						throw new nc.vo.pub.BusinessException("未查找到相应单据！");
					}
					JKBXVO copyVO = (JKBXVO) jkbxvoList.get(0).clone();
					ERMBillVOUtil.doCopy(copyVO);
					// ERMBillVOUtil.dealVODigit(copyVO);//处理精度
					json = NCLocator
							.getInstance()
							.lookup(ITranslateVODataService.class)
							.transAggvoToJSON(
									JKBillVO.getJKBillVO((JKVO) copyVO),
									billItems);
				}
				json.put("success", "true");
				jsonStr = json.toString();
			} catch (nc.vo.pub.BusinessException e) {
				Logger.error(e.getMessage(), e);
				throw new RuntimeException(e.getMessage());
			} catch (WebRuntimeException e) {
				Logger.error(e.getMessage(), e);
				throw e;
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
				throw new RuntimeException(e.getMessage());
			}
		}
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonStr);
		response.flushBuffer();
	}

	@RequestMapping(value = "/viewbill", method = RequestMethod.GET)
	public @ResponseBody
	void viewBill(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String openbillid = request.getParameter("openbillid");
		String pk_tradetype = request.getParameter("tradetype");
		String pk_billtemplet = request.getParameter("pk_billtemplet");
		List<BillItem> billItems = BillTemplateWebUtil
				.getBillItems(pk_billtemplet);
		String jsonStr = "";
		// 核验
		String uid = request.getParameter("uid");
		if (StringUtils.isNotBlank(openbillid) && StringUtils.isNotBlank(uid)
				&& uid.equals(InvocationInfoProxy.getInstance().getUserId())) {
			String[] pks = { openbillid };
			JSONObject json = new JSONObject();
			try {
				JKBXVO jkbxvo = null;
				List<JKBXVO> jkbxvoList = null;
				if (pk_tradetype.startsWith("264")) {// 报销单
					jkbxvoList = getIBXBillPrivate().queryVOsByPrimaryKeys(pks,
							"bx");
					if (ERMValueCheck.isEmpty(jkbxvoList)) {
						throw new nc.vo.pub.BusinessException("未查找到相应单据！");
					}
					jkbxvo = jkbxvoList.get(0);
					ERMBillVOUtil.dealVODigit(jkbxvo);// 处理精度
					ERMBillVOUtil.dealImageState(jkbxvo);// 处理表头影像状态
					json = NCLocator
							.getInstance()
							.lookup(ITranslateVODataService.class)
							.transAggvoToJSON(
									BXBillVO.getBXBillVO((BXVO) jkbxvo),
									billItems);
				} else if (pk_tradetype.startsWith("263")) {// 借款单
					jkbxvoList = getIBXBillPrivate().queryVOsByPrimaryKeys(pks,
							"jk");
					if (ERMValueCheck.isEmpty(jkbxvoList)) {
						throw new nc.vo.pub.BusinessException("未查找到相应单据！");
					}
					jkbxvo = jkbxvoList.get(0);
					ERMBillVOUtil.dealVODigit(jkbxvoList.get(0));// 处理精度
					json = NCLocator
							.getInstance()
							.lookup(ITranslateVODataService.class)
							.transAggvoToJSON(
									JKBillVO.getJKBillVO((JKVO) jkbxvo),
									billItems);
				}
				// 散户在后台有缓存，散户维护后因缓存存在不能及时更新单据信息，特从数据库查询保证准确
				JSONObject jsonObject = json.getJSONObject("head");
				JSONObject jsonObject2 = jsonObject.getJSONObject("freecust");
				if (jsonObject2.has("pk")) {
					String pk = (String) jsonObject2.get("pk");
					String sql = "select name from bd_freecustom where pk_freecustom = '"
							+ pk + "'";
					String name = (String) new BaseDAO().executeQuery(sql,
							new ColumnProcessor());
					jsonObject2.put("name", name);
				}
				json.put("success", "true");
				jsonStr = json.toString();
			} catch (nc.vo.pub.BusinessException e) {
				Logger.error(e.getMessage(), e);
				throw new RuntimeException(e.getMessage());
			} catch (WebRuntimeException e) {
				Logger.error(e.getMessage(), e);
				throw e;
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
				throw new RuntimeException(e.getMessage());
			}
		}
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonStr);
		response.flushBuffer();
	}

	/**
	 * 保存
	 * 
	 * @param request
	 * @param response
	 * @throws JSONException
	 */
	@RequestMapping(value = "/savebill", method = RequestMethod.POST)
	public @ResponseBody
	void saveBill(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		try {
			String result = NCLocator.getInstance()
					.lookup(IWebPubService.class)
					.saveJKBX(new RequestParam(request));
			response.getWriter().write(result);
		} catch (nc.vo.er.exception.BugetAlarmBusinessException e) {
			JSONObject json = new JSONObject();
			json.put("bugetAlarm", e.getMessage());
			response.getWriter().write(json.toString());
		} catch (nc.vo.cmp.exception.ErmException e) {
			JSONObject json = new JSONObject();
			json.put("jkAlarm", e.getMessage());
			response.getWriter().write(json.toString());
		} catch (nc.vo.pub.BusinessException e) {
			response.getWriter().write(JsonMessageUtil.error(e));
			Logger.error(e.getMessage(), e);
		} catch (Exception e) {
			response.getWriter().write(JsonMessageUtil.error(e));
			Logger.error(e.getMessage(), e);
		} finally {
			try {
				response.flushBuffer();
			} catch (IOException e) {
				Logger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 删除
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/deletebill", method = RequestMethod.GET)
	public @ResponseBody
	void deleteBill(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String result = "";
		try {
			result = NCLocator.getInstance().lookup(IWebPubService.class)
					.deleteJKBX(new RequestParam(request));
			response.getWriter().write(result);
		} catch (Exception e) {
			response.getWriter().write(JsonMessageUtil.error(e));
			Logger.error(e.getMessage(), e);
		} finally {
			try {
				response.flushBuffer();
			} catch (IOException e) {
				Logger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 提交
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/sendapprove", method = RequestMethod.POST)
	public @ResponseBody
	void submitBill(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		try {
			String result = NCLocator.getInstance()
					.lookup(IWebPubService.class)
					.submitJKBX(new RequestParam(request));
			response.getWriter().write(result);
		} catch (nc.vo.er.exception.BugetAlarmBusinessException e) {
			JSONObject json = new JSONObject();
			json.put("bugetAlarm", e.getMessage());
			response.getWriter().write(json.toString());
		} catch (nc.vo.cmp.exception.ErmException e) {
			JSONObject json = new JSONObject();
			json.put("jkAlarm", e.getMessage());
			response.getWriter().write(json.toString());

		} catch (nc.vo.pub.BusinessException e) {
			response.getWriter().write(JsonMessageUtil.error(e));
			Logger.error(e.getMessage(), e);
		} catch (Exception e) {
			response.getWriter().write(JsonMessageUtil.error(e));
			Logger.error(e.getMessage(), e);
		} finally {
			try {
				response.flushBuffer();
			} catch (IOException e) {
				Logger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 收回
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/unsendapprove", method = RequestMethod.GET)
	public @ResponseBody
	void recallBill(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String result = "";
		try {
			result = NCLocator.getInstance().lookup(IWebPubService.class)
					.recallJKBX(new RequestParam(request));
			response.getWriter().write(result);
		} catch (Exception e) {
			response.getWriter().write(JsonMessageUtil.error(e));
			Logger.error(e.getMessage(), e);
		} finally {
			try {
				response.flushBuffer();
			} catch (IOException e) {
				Logger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * edit
	 * 
	 * @param request
	 * @param response
	 * @throws JSONException
	 * @throws BusinessException
	 * @throws IOException
	 */
	@RequestMapping(value = "/editright", method = RequestMethod.GET)
	public @ResponseBody
	void editRight(HttpServletRequest request, HttpServletResponse response)
			throws JSONException, BusinessException, IOException {
		JSONObject json = new JSONObject();
		String tradetype = request.getParameter("tradetype");
		// String iseditssc = request.getParameter("iseditssc");// 来源于共享中心
		String iseditbzr = request.getParameter("iseditbzr");// 来源于报账人门户
		String openbillid = ESAPI.encoder().encodeForSQL(new OracleCodec(),
				request.getParameter("openbillid"));
		String pk_user = InvocationInfoProxy.getInstance().getUserId();

		String[] pks = new String[] { openbillid };
		JKBXVO jkbxvo = null;
		List<JKBXVO> jkbxvoList = null;
		if (tradetype.startsWith("264")) {// 报销单
			jkbxvoList = getIBXBillPrivate().queryVOsByPrimaryKeys(pks, "bx");
			if (ERMValueCheck.isEmpty(jkbxvoList)) {
				throw new nc.vo.pub.BusinessException("未查找到相应单据！");
			}
			jkbxvo = jkbxvoList.get(0);
		} else if (tradetype.startsWith("263")) {// 借款单
			jkbxvoList = getIBXBillPrivate().queryVOsByPrimaryKeys(pks, "jk");
			if (ERMValueCheck.isEmpty(jkbxvoList)) {
				throw new nc.vo.pub.BusinessException("未查找到相应单据！");
			}
			jkbxvo = jkbxvoList.get(0);
		}

		if (jkbxvoList == null || jkbxvoList.size() == 0) {
			json.put("success", "false");
			json.put("message", "单据状态已改变,请刷新界面！");
			response.getWriter().write(json.toString());
			return;
		}
		if (jkbxvo != null) {
			CircularlyAccessibleValueObject parentVO = jkbxvo.getParentVO();
			Integer djzt = (Integer) parentVO
					.getAttributeValue(IBillFieldGet.DJZT);
			Integer approvestatus = (Integer) parentVO
					.getAttributeValue(IBillFieldGet.SPZT);
			List<String> checkManList = ERMPfUtil.getCheckmans(openbillid);
			if ("Y".equals(iseditbzr)) {
				if (approvestatus.intValue() == IPfRetCheckInfo.COMMIT
						|| approvestatus.intValue() == IPfRetCheckInfo.GOINGON
						|| approvestatus.intValue() == IPfRetCheckInfo.PASSING
						|| approvestatus.intValue() == IPfRetCheckInfo.NOPASS) {
					// 来源于报账人门户，提交以后的单据不能修改
					json.put("success", "false");
					json.put("message", "已经提交的单据不能修改!");
					response.getWriter().write(json.toString());
					return;
				}
			} else {
				if ((djzt.intValue() != 2) && !checkManList.contains(pk_user)) {
					json.put("success", "false");
					json.put("message", "审批流程中的单据， 只有当前审批人可以修改!");
					response.getWriter().write(json.toString());
					return;
				} else if ((djzt.intValue() == 2)) {
					json.put("success", "false");
					json.put("message", "审批通过的单据不能修改!");
					response.getWriter().write(json.toString());
					return;
				} else if (approvestatus.intValue() == IPfRetCheckInfo.COMMIT
						&& !checkManList.contains(pk_user)) {
					json.put("success", "false");
					json.put("message", "已经提交的单据不能修改!");
					response.getWriter().write(json.toString());
					return;
				} else if ((approvestatus.intValue() == IPfRetCheckInfo.NOPASS)) {
					json.put("success", "false");
					json.put("message", "审批失败的单据不能修改!");
					response.getWriter().write(json.toString());
					return;
				}
			}
		}
		json.put("success", "true");
		response.getWriter().write(json.toString());
	}

	// add by sunjq
	/**
	 * 上传电子发票转换数据
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/changeFp", method = RequestMethod.POST)
	public @ResponseBody
	void changeFp(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		JSONArray rtnJSONArr = new JSONArray();
		try {
			String jsnObj = request.getParameter("bill");
			String openbillid = request.getParameter("openbillid");
			String pk_tradetype = request.getParameter("tradetype");
			String pk_group = InvocationInfoProxy.getInstance().getGroupId();
			String pk_user = InvocationInfoProxy.getInstance().getUserId();
			String[] psnidAndDept = NCLocator
					.getInstance()
					.lookup(IBXBillPrivate.class)
					.queryPsnidAndDeptid(
							InvocationInfoProxy.getInstance().getUserId(),
							pk_group);
			String pk_org = psnidAndDept[2];
			Map<String, String> clientAttrs = new HashMap<String, String>();
			clientAttrs.put("nodecode", request.getParameter("nodecode"));
			clientAttrs.put("tradetype", pk_tradetype);
			clientAttrs.put(ERMWebConst.BILL_TEMPLETID,
					request.getParameter("pk_billtemplet"));
			clientAttrs.put(ERMWebConst.BILL_NODEPATH,
					"/iwebap/pages/20110ETEA");
			// 界面数据
			AbstractBill jkbxBillVO = null;
			// 界面数据
			JKBXVO jkbxvoForClient = null;
			// 结果数据
			JSONObject result = new JSONObject();
			if (pk_tradetype.startsWith("264")) {// 报销单
				// 界面数据
				jkbxBillVO = NCLocator.getInstance()
						.lookup(ITranslateDataService.class)
						.translateJsonToAggvo(BXBillVO.class, jsnObj);
				jkbxvoForClient = ((BXBillVO) jkbxBillVO).toBXVO();
				filterBusitemVO(jkbxvoForClient);
				// 取表体业务行默认值
				BXBillVO billvoForInit = (BXBillVO) OrgChangedLoadDefData
						.dealDefValue(clientAttrs, pk_group, pk_org, pk_user,
								ERMWebConst.INIT_LOADDEF);
				JKBXVO bxvoForInit = billvoForInit.toBXVO();
				// 用来克隆
				BXBusItemVO itemvo = bxvoForInit.getChildrenVO()[0];
				// 电子发票
				ErmFpRelationVO[] fpRelationVOs = NCLocator.getInstance()
						.lookup(IErmFPRelation.class).getRelations(openbillid);
				// 电子发票集合
				Set<String> fpRelationSet = new HashSet<String>();
				for (ErmFpRelationVO relation : fpRelationVOs) {
					fpRelationSet.add(relation.getPk_fprelation());
				}
				// 表体行数据
				List<BXBusItemVO> fpItemList = new ArrayList<BXBusItemVO>();
				// 界面表体行数据，删除了电子发票删除的记录
				Set<String> fpRelationSetClient = new HashSet<String>();
				// 前台数据先添加到集合中
				if (null != jkbxvoForClient.getChildrenVO()) {
					for (BXBusItemVO item : jkbxvoForClient.getChildrenVO()) {
						if (item.getTablecode().equals("arap_bxbusitem")) {
							if (item.getPk_fprelation() != null
									&& !fpRelationSet.contains(item
											.getPk_fprelation())) {
								continue;
							}
							if (item.getPk_fprelation() != null) {
								fpRelationSetClient
										.add(item.getPk_fprelation());
							}
							fpItemList.add(item);
						}
					}
				}
				// 将电子发票中数据添加到集合中
				if (fpRelationVOs != null && fpRelationVOs.length > 0) {
					for (ErmFpRelationVO fpRelation : fpRelationVOs) {
						// 已经存在的记录不添加到集合
						if (fpRelationSetClient.contains(fpRelation
								.getPk_fprelation())) {
							continue;
						}
						BXBusItemVO itemvoClone = (BXBusItemVO) itemvo.clone();
						for (String jeField : BXBusItemVO
								.getBodyJeFieldForDecimal()) {
							itemvoClone.setAttributeValue(jeField,
									UFDouble.ZERO_DBL);
						}
						itemvoClone.setAmount(fpRelation.getJshj());
						itemvoClone.setBbje(fpRelation.getJshj());
						itemvoClone.setBbhl(jkbxvoForClient.getParentVO()
								.getBbhl());
						itemvoClone.setVat_amount(fpRelation.getJshj());
						itemvoClone.setPk_fprelation(fpRelation
								.getPk_fprelation());
						itemvoClone.setFpdm(fpRelation.getFpDm());
						itemvoClone.setFphm(fpRelation.getFpHm());
						itemvoClone.setBzbm(jkbxvoForClient.getParentVO()
								.getBzbm());
						itemvoClone.setDwbm(jkbxvoForClient.getParentVO()
								.getDwbm());
						itemvoClone.setDeptid(jkbxvoForClient.getParentVO()
								.getDeptid());
						itemvoClone.setJkbxr(jkbxvoForClient.getParentVO()
								.getJkbxr());
						itemvoClone.setTablecode("arap_bxbusitem");
						fpItemList.add(itemvoClone);
					}
				}
				ERMBillVOUtil.dealBusiVODigit(pk_group, pk_org,
						fpItemList.toArray(new BXBusItemVO[fpItemList.size()]));// 处理精度
				ITranslateVODataService service = NCLocator.getInstance()
						.lookup(ITranslateVODataService.class);
				JSONArray transJSONArr = service
						.transValueObjectToJSON(fpItemList
								.toArray(new BXBusItemVO[fpItemList.size()]));
				if (transJSONArr.length() > 0) {// 转换为前端显示的JSON格式
					for (int i = 0; i < transJSONArr.length(); i++) {
						rtnJSONArr.put(((JSONObject) transJSONArr.get(i))
								.get("head"));
					}
				}
			}
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			result.put("success", "true");
			response.getWriter().write(rtnJSONArr.toString());
			response.flushBuffer();
		} catch (Exception e) {
			response.getWriter().write(JsonMessageUtil.error(e));
			Logger.error(e.getMessage(), e);
		} finally {
			try {
				response.flushBuffer();
			} catch (IOException e) {
				Logger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 过滤掉特定条件的业务行VO，如：没有tablecode等
	 * 这个处理为了解决界面操作时自动保存的无效数据，如增行后删除全部行，此时保存时会将该页签纳入自动保存的范畴
	 * ，保存空行，空行不会设置tablecode，在此处过滤 前台处理比较麻烦，故在此处理
	 * 
	 * @param bxVO
	 */
	private void filterBusitemVO(JKBXVO bxVO) {
		List<BXBusItemVO> filteredBusiList = new ArrayList<BXBusItemVO>();
		if (null != bxVO.getBxBusItemVOS()) {
			for (BXBusItemVO eachVO : bxVO.getBxBusItemVOS()) {
				if (eachVO.getAmount().compareTo(UFDouble.ZERO_DBL) != 0) {// 报销金额为0的过滤掉
					filteredBusiList.add(eachVO);
				}
			}
		}
		bxVO.setBxBusItemVOS(filteredBusiList
				.toArray(new BXBusItemVO[filteredBusiList.size()]));
		if (ERMValueCheck.isNotEmpty(bxVO.getcShareDetailVo())) {
			List<CShareDetailVO> filteredShareList = new ArrayList<CShareDetailVO>();
			for (CShareDetailVO eachVO : bxVO.getcShareDetailVo()) {
				if (eachVO.getAssume_amount().compareTo(UFDouble.ZERO_DBL) != 0) {// 分摊金额为0的过滤掉
					filteredShareList.add(eachVO);
				}
			}
			bxVO.setcShareDetailVo(filteredShareList
					.toArray(new CShareDetailVO[filteredShareList.size()]));
		}
	}

	/**
	 * 取消时更新电子发票状态为0
	 * 
	 * @param request
	 * @param response
	 * @throws JSONException
	 */
	@RequestMapping(value = "/cancelbill", method = RequestMethod.POST)
	public @ResponseBody
	void cancelBill(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		try {
			NCLocator.getInstance().lookup(IWebPubService.class)
					.cancelBillJKBX(new RequestParam(request));
		} catch (Exception e) {
			response.getWriter().write(JsonMessageUtil.error(e));
			Logger.error(e.getMessage(), e);
		} finally {
			try {
				response.flushBuffer();
			} catch (IOException e) {
				Logger.error(e.getMessage(), e);
			}
		}
	}

	// end add

	protected IBXBillPrivate getIBXBillPrivate() throws ComponentException {
		return (IBXBillPrivate) NCLocator.getInstance().lookup(
				IBXBillPrivate.class.getName());
	}

	/**
	 * 获取表体字段动态显示的参数
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/getVisibleSettingParams", method = RequestMethod.POST)
	public @ResponseBody
	void getVisableSettingParams(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		JSONObject json = new JSONObject();
		String pk_tradetype = request.getParameter("tradetype");// 交易类型
		String pk_org = request.getParameter("pk_org");// 主组织
		String key = pk_tradetype + "@" + pk_org;
		json.put("success", "true");
		// 通过properties配置文件的方式，文件位置/NCHOME/resources/ermwebconfig/fieldVisiable.properties，格式：
		// 交易类型编码@组织PK=控制字段编码@控制字段值1:被控制字段1,被控制字段2|控制字段编码@控制字段值2:被控制字段3,被控制字段4
		// 例如：2641@00011010000000000O5U=szxmid@1001WW100000000008ZY:defitem20,defitem22|szxmid@1001WW10000000000902:defitem27,defitem28
		Properties tProperties = ERMWEBUtil
				.loadNodePropertie("fieldVisiable.properties");
		if (null != tProperties) {
			Map<String, JSONObject> srcFieldConfigMap = new HashMap<String, JSONObject>();// 根据控制字段分组的配置的Map
			Map<String, Set<String>> srcFieldSetMap = new HashMap<String, Set<String>>();// 根据控制字段分组的所有被控制字段的Map
			String fieldVisiableConfig = (null != tProperties.getProperty(key) && tProperties
					.getProperty(key).length() > 0) ? tProperties
					.getProperty(key) : tProperties.getProperty(pk_tradetype);// 先取组织级的配置，取不到取交易类型级的配置
			if (null != fieldVisiableConfig && fieldVisiableConfig.length() > 0) {
				json.put("success", "true");
				String[] fieldVisiableConfigArr = fieldVisiableConfig
						.split("\\|");
				JSONObject srcJson = null;
				Set<String> srcSet = null;
				for (String eachConfig : fieldVisiableConfigArr) {
					String srcField = (eachConfig.split("@"))[0];
					if (!srcFieldConfigMap.containsKey(srcField)) {
						srcFieldConfigMap.put(srcField, new JSONObject());
					}
					srcJson = srcFieldConfigMap.get(srcField);
					String srcValue = ((eachConfig.split("@")[1]).split(":"))[0];// 控制字段的值
					String fields = ((eachConfig.split("@")[1]).split(":"))[1];// 被控制的字段
					srcJson.put(srcValue, fields);
					if (!srcFieldSetMap.containsKey(srcField)) {
						srcFieldSetMap.put(srcField, new HashSet<String>());
					}
					srcSet = srcFieldSetMap.get(srcField);
					String[] fieldArr = fields.split(",");
					for (String eachField : fieldArr) {
						srcSet.add(eachField);
					}
				}
				for (Entry<String, Set<String>> entry : srcFieldSetMap
						.entrySet()) {
					StringBuffer allFields = new StringBuffer();// 所有字段
					for (String eachField : entry.getValue()) {
						allFields.append(",");
						allFields.append(eachField);
					}
					allFields.deleteCharAt(0);
					srcFieldConfigMap.get(entry.getKey()).put("allFields",
							allFields.toString());
				}
			}
			json.put("paramMap", srcFieldConfigMap);
		}
		response.getWriter().write(json.toString());

		// String pk_group = InvocationInfoProxy.getInstance().getGroupId();//集团
		// String pk_org = request.getParameter("pk_org");//主组织
		// Map<String,List<String>> accassitemMap =
		// NCLocator.getInstance().lookup(IWebPubService.class).getAccassitemByDocview(pk_group,
		// pk_org, "01");
		// Map<String, String> specialFieldMap = getSpecialFieldMap();
		// Set<String> allFieldSet = new HashSet<String>();
		// json.put("success", "true");
		// json.put("srcField", "szxmid");
		// for(Entry<String,List<String>> entry : accassitemMap.entrySet()){
		// StringBuffer sb = new StringBuffer();
		// for(String eachField : entry.getValue()){
		// String realFieldName =
		// specialFieldMap.containsKey(eachField)?specialFieldMap.get(eachField):eachField;
		// allFieldSet.add(realFieldName);
		// sb.append(realFieldName + ",");
		// }
		// sb.deleteCharAt(sb.length()-1);
		// json.put(entry.getKey(), sb.toString());
		// }
		// StringBuffer allFields = new StringBuffer();
		// for(String realFieldName : allFieldSet){
		// allFields.append(realFieldName + ",");
		// }
		// allFields.deleteCharAt(allFields.length()-1);
		// json.put("allFields", allFields.toString());
		// response.getWriter().write(json.toString());
	}

	/**
	 * 特殊字段映射
	 * 
	 * @return
	 */

	private Map<String, String> getSpecialFieldMap() {
		Map<String, String> specialFieldMap = new HashMap<String, String>();
		specialFieldMap.put("dept", "defitem20");
		specialFieldMap.put("areaclass", "defitem21");
		specialFieldMap.put("custsupplier", "defitem22");
		specialFieldMap.put("psndoc", "defitem23");
		specialFieldMap.put("cashflow", "defitem24");
		specialFieldMap.put("bankaccsub", "defitem25");
		specialFieldMap.put("material_v", "defitem26");
		specialFieldMap.put("fundplan", "defitem27");
		specialFieldMap.put("project", "defitem28");
		specialFieldMap.put("inoutbusiclass", "defitem29");
		return specialFieldMap;
	}

	/**
	 * XBX推送CBS
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/comitCBS", method = RequestMethod.POST)
	public @ResponseBody
	void comitCBS(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		String billid = req.getParameter("billid");// 单据主键
		String djlxbm = req.getParameter("djlxbm");// 交易类型
		JSONObject json = new JSONObject();
		JKBXUtils controller = new JKBXUtils();
		String mes = controller.nc2cbs(billid, djlxbm);
		if ("".equals(mes)) {
			json.put("success", "true");
		} else {
			json.put("success", "false");
			json.put("mes", mes);
		}
		resp.getWriter().write(json.toString());
	}

	@RequestMapping(value = "/getUserInfo", method = RequestMethod.POST)
	public @ResponseBody
	void getUserInfo(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		String pk_user = InvocationInfoProxy.getInstance().getUserId();
		Logger.error("当前登录用户：" + pk_user);
		String querySql = "SELECT COUNT(CUSERID) AS SL FROM SM_USER_ROLE WHERE PK_ROLE = '1001A2100000000QZX7G' AND CUSERID = '"
				+ pk_user + "'";
		List<Object[]> bxls = getDao.query(querySql);
		if (bxls != null && Integer.parseInt(bxls.get(0)[0] + "") > 0) {
			resp.getWriter().write("Y");
		} else {
			resp.getWriter().write("N");
		}
	}

	// XBX新增流程驳回
	@RequestMapping(value = "/getBHUserInfo", method = RequestMethod.POST)
	public @ResponseBody
	void getBHUserInfo(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		String pk_user = InvocationInfoProxy.getInstance().getUserId();
		Logger.error("当前登录用户：" + pk_user);
		String querySql = "SELECT COUNT(YHJS.CUSERID) AS SL FROM SM_USER_ROLE YHJS LEFT JOIN SM_ROLE JS ON YHJS.PK_ROLE = JS.PK_ROLE WHERE JS.ROLE_CODE = 'FLOWREJECT' AND YHJS.CUSERID = '"
				+ pk_user + "'";
		List<Object[]> bxls = getDao.query(querySql);
		if (bxls != null && Integer.parseInt(bxls.get(0)[0] + "") > 0) {
			resp.getWriter().write("Y");
		} else {
			resp.getWriter().write("N");
		}
	}

	// XBX新增影像补扫角色验证
	@RequestMapping(value = "/getYXUserInfo", method = RequestMethod.POST)
	public @ResponseBody
	void getYXUserInfo(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		String pk_user = InvocationInfoProxy.getInstance().getUserId();
		Logger.error("当前登录用户：" + pk_user);
		String cuserid = req.getParameter("cuserid");// 制单人主键
		String querySql = "SELECT COUNT(YHJS.CUSERID) AS SL FROM SM_USER_ROLE YHJS LEFT JOIN SM_ROLE JS ON YHJS.PK_ROLE = JS.PK_ROLE WHERE JS.ROLE_CODE = 'YXBS' AND YHJS.CUSERID = '"
				+ pk_user + "'";
		List<Object[]> bxls = getDao.query(querySql);
		if (bxls != null && Integer.parseInt(bxls.get(0)[0] + "") > 0) {
			resp.getWriter().write("Y");
			return;
		}
		if (cuserid.equals(pk_user)) {
			UserVO userVO = (UserVO) new HYPubBO().queryByPrimaryKey(
					UserVO.class, pk_user);
			if ("0001A110000000000HYQ".equals(userVO.getPk_org())
					|| "0001A21000000001H7J3".equals(userVO.getPk_org())
					|| "0001A110000000000KYP".equals(userVO.getPk_org())
					|| "0001A210000000005A2B".equals(userVO.getPk_org())) {
				resp.getWriter().write("Y");
				return;
			}
		}
		resp.getWriter().write("N");
		return;
	}

	// XBX新增影像补扫（采暖费退费）角色验证
	@RequestMapping(value = "/getCNFYXUserInfo", method = RequestMethod.POST)
	public @ResponseBody
	void getCNFYXUserInfo(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		String pk_user = InvocationInfoProxy.getInstance().getUserId();
		Logger.error("当前登录用户：" + pk_user);
		String querySql = "SELECT COUNT(YHJS.CUSERID) AS SL FROM SM_USER_ROLE YHJS LEFT JOIN SM_ROLE JS ON YHJS.PK_ROLE = JS.PK_ROLE WHERE JS.ROLE_CODE = 'YXBSCNF' AND YHJS.CUSERID = '"
				+ pk_user + "'";
		List<Object[]> bxls = getDao.query(querySql);
		if (bxls != null && Integer.parseInt(bxls.get(0)[0] + "") > 0) {
			resp.getWriter().write("Y");
		} else {
			resp.getWriter().write("N");
		}

	}

	// XBX根据用户PK获取用户编码
	@RequestMapping(value = "/getUserCode", method = RequestMethod.POST)
	public @ResponseBody
	void getUserCode(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		JSONObject json = new JSONObject();
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		String cuserid = req.getParameter("cuserid");// 用户主键;
		String querySql = "SELECT USER_CODE FROM SM_USER WHERE CUSERID = '"
				+ cuserid + "'";
		List<Object[]> bxls = getDao.query(querySql);
		if (bxls != null && bxls.size() > 0 && bxls.get(0) != null) {
			json.put("success", "true");
			json.put("dt", bxls.get(0)[0]);
		} else {
			json.put("success", "false");
			json.put("dt", "未获取到用户编码！");
		}
		resp.getWriter().write(json.toString());
	}

	// XBX根据用户PK获取用户编码
	@RequestMapping(value = "/getCurUserCode", method = RequestMethod.POST)
	@ResponseBody
	public void getCurUserCode(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		JSONObject json = new JSONObject();
		String user_code = InvocationInfoProxy.getInstance().getUserCode();// 当前登录人编码
		String user_name = InvocationInfoProxy.getInstance().getUserId();// 当前登录人主键
		json.put("user_code", user_code);
		json.put("user_name", user_name);
		resp.getWriter().write(json.toString());
	}

	// XBX新增流程驳回0811
	@RequestMapping(value = "/rejectpost", method = RequestMethod.POST)
	@ResponseBody
	public void rejectpost(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		String tradetype = req.getParameter("tradetype");// 单据类型
		String billid = req.getParameter("billid");// 单据主键
		String reason = req.getParameter("reason");// 驳回原因
		Logger.error("billid：" + billid);
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		JSONObject json = new JSONObject();
		if ("D2".equals(tradetype.substring(0, 2))
				|| "F2".equals(tradetype.substring(0, 2))) {
			// 自定义项DEF66 作为驳回原因
			String sql = "UPDATE AR_GATHERBILL SET DEF66 = '" + reason
					+ "' WHERE PK_GATHERBILL = '" + billid + "' AND DR = 0 ";
			getDao.updatevo(sql);
			try {
				NCLocator.getInstance()
						.lookup(IArapGatheringBillServiceForZQ.class)
						.rollBackAggGatheringBillVO(billid);
				json.put("success", "true");
				json.put("mes", "Y");
			} catch (Exception e) {
				json.put("success", "false");
				json.put("mes", e.getMessage());
			}
		} else if ("D0".equals(tradetype.substring(0, 2))
				|| "F0".equals(tradetype.substring(0, 2))) {
			// 自定义项DEF66 作为驳回原因
			String sql = "UPDATE ar_recbill SET DEF66 = '" + reason
					+ "' WHERE pk_recbill = '" + billid + "' AND DR = 0 ";
			getDao.updatevo(sql);
			try {
				NCLocator.getInstance()
						.lookup(IArapGatheringBillServiceForZQ.class)
						.rollAggReceivableBillVO(billid);
				json.put("success", "true");
				json.put("mes", "Y");
			} catch (Exception e) {
				json.put("success", "false");
				json.put("mes", e.getMessage());
			}
		}
		resp.getWriter().write(json.toString());
	}

	// XBX获取合同信息
	@RequestMapping(value = "/getContract", method = RequestMethod.POST)
	@ResponseBody
	public void getContract(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		String tradetype = req.getParameter("tradetype");// 单据类型
		String billid = req.getParameter("billid");// 单据主键
		Map<String, String> map = getContractNo(tradetype, billid);// 合同编号
		JSONObject json = new JSONObject();
		if (map == null || "".equals(map.get("contractNo"))) {
			json.put("success", "false");
			json.put("dt", "合同编码为空！");
		} else {
			String info = getContractAndFileInfo(map);
			if ("".equals(info)) {
				json.put("success", "false");
				json.put("dt", "根据合同编号未获取到相应合同！");
			} else {
				json.put("success", "true");
				json.put("dt", info);
			}
		}
		resp.getWriter().write(json.toString());
	}

	// XBX获取合同编号
	public Map<String, String> getContractNo(String tradetype, String billid)
			throws DAOException {
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		String contractNo = "";
		String supplier = "";
		String money = "";
		String sql = "";
		if ("D3".equals(tradetype.substring(0, 2))
				|| "F3".equals(tradetype.substring(0, 2))) {
			sql = "select distinct item.contractno,item.supplier,main.def70 from ap_paybill main left join ap_payitem item on main.pk_paybill = item.pk_paybill where "
					+ "main.pk_paybill = '"
					+ billid
					+ "' and main.dr = 0 and item.dr = 0";
		} else if ("D1".equals(tradetype.substring(0, 2))
				|| "F1".equals(tradetype.substring(0, 2))) {
			sql = "select distinct item.contractno,item.supplier,main.def70 from ap_payablebill main left join ap_payableitem item on main.pk_payablebill = item.pk_payablebill where "
					+ "main.pk_payablebill = '"
					+ billid
					+ "' and main.dr = 0 and item.dr = 0";
		} else if ("D2".equals(tradetype.substring(0, 2))
				|| "F2".equals(tradetype.substring(0, 2))) {
			sql = "select distinct item.contractno,item.customer,main.def70 from ar_gatherbill main left join ar_gatheritem item on main.pk_gatherbill = item.pk_gatherbill where "
					+ "main.pk_gatherbill = '"
					+ billid
					+ "' and main.dr = 0 and item.dr = 0";
		} else if ("D0".equals(tradetype.substring(0, 2))) {
			sql = "select distinct item.contractno,item.customer,main.def70 from ar_recbill main left join ar_recitem item on main.pk_recbill = item.pk_recbill where "
					+ "main.pk_recbill = '"
					+ billid
					+ "' and main.dr = 0 and item.dr = 0";
		} else {
			return null;
		}
		Logger.error("SQL====" + sql);
		List<Object[]> bxls = getDao.query(sql);
		if (bxls != null && bxls.size() > 0 && bxls.get(0) != null
				&& bxls.get(0)[0] != null) {
			contractNo = bxls.get(0)[0] + "";
			supplier = bxls.get(0)[1] + "";
			money = bxls.get(0)[2] + "";
		}
		Map<String, String> mp = new HashMap<String, String>();
		mp.put("contractNo", contractNo);
		mp.put("supplier", supplier);
		// mp.put("money", money);
		return mp;
	}

	// XBX获取合同信息及附件信息
	public String getContractAndFileInfo(Map<String, String> mp)
			throws DAOException, JSONException, UifException,
			MalformedURLException {
		Logger.error("合同编号：" + mp.get("contractNo"));
		Logger.error("供应商：" + mp.get("supplier"));
		String htinfo = "";
		// double money = Double.parseDouble(mp.get("money") + "");
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		/*
		 * String qdhtsql = "SELECT PK_CONTR FROM PM_CONTR WHERE bill_code = '"
		 * + mp.get("contractNo") +
		 * "' AND PK_SUPPLIER = '"+mp.get("supplier")+"' AND ncurrent_mny = '"
		 * +money+"' AND nvl(dr,0) = 0 ";
		 */
		String qdhtsql = "SELECT PK_CONTR FROM PM_CONTR WHERE bill_code = '"
				+ mp.get("contractNo")
				+ "' AND PK_SUPPLIER = '"
				+ mp.get("supplier")
				+ "' AND nvl(dr,0) = 0 and last_v_flag = 'Y' AND bill_status = 9";
		List<Object[]> qdpkls = getDao.query(qdhtsql);
		if (qdpkls != null && qdpkls.size() > 0 && qdpkls.get(0) != null
				&& qdpkls.get(0)[0] != null && !"".equals(qdpkls.get(0)[0])) {
			htinfo = getQDJson(qdpkls.get(0)[0] + "");
		} else {
			/*
			 * String cgsql = "SELECT PK_CT_PU FROM CT_PU WHERE VBILLCODE = '" +
			 * mp.get("contractNo") +
			 * "' AND CVENDORID = '"+mp.get("supplier")+"' AND ntotalorigmny = '"
			 * +money+"' AND NVL(DR,0) = 0 ";
			 */
			String cgsql = "SELECT PK_CT_PU FROM CT_PU WHERE VBILLCODE = '"
					+ mp.get("contractNo")
					+ "' AND CVENDORID = '"
					+ mp.get("supplier")
					+ "' AND NVL(DR,0) = 0 AND BSHOWLATEST = 'Y' AND fstatusflag = 1";
			List<Object[]> ctpkls = getDao.query(cgsql);
			if (ctpkls != null && ctpkls.size() > 0 && ctpkls.get(0) != null
					&& ctpkls.get(0)[0] != null && !"".equals(ctpkls.get(0)[0])) {
				Logger.error("ctpkls.get(0)[0]===" + ctpkls.get(0)[0]);
				htinfo = getCGJson(ctpkls.get(0)[0] + "");
			} else {
				/*
				 * String dbmsql =
				 * "SELECT PK_FEEBALANCE FROM PM_FEEBALANCE WHERE DEF2 = '" +
				 * mp.get("contractNo") +
				 * "' AND PK_SUPPLIER = '"+mp.get("supplier"
				 * )+"' AND MONEY = '"+money+"' AND NVL(DR,0) = 0 ";
				 */
				String dbmsql = "SELECT PM_FEEBALANCE FROM PM_FEEBALANCE_CT WHERE BILL_CODE = '"
						+ mp.get("contractNo")
						+ "' AND PK_SUPPLIER_NAME = '"
						+ mp.get("supplier")
						+ "' AND NVL(DR,0) = 0 AND fstatusflag = 1 ";
				Logger.error("dbmsql==" + dbmsql);
				List<Object[]> dbmls = getDao.query(dbmsql);
				if (dbmls != null && dbmls.size() > 0 && dbmls.get(0) != null
						&& dbmls.get(0)[0] != null
						&& !"".equals(dbmls.get(0)[0])) {
					Logger.error("dbmls.get(0)[0]===" + dbmls.get(0)[0]);
					htinfo = getDBMJson(dbmls.get(0)[0] + "");
				} else {
					// 付款合同
					String fksql = "SELECT PK_FCT_AP FROM FCT_AP WHERE VBILLCODE = '"
							+ mp.get("contractNo")
							+ "' AND CVENDORID = '"
							+ mp.get("supplier")
							+ "' AND NVL(DR,0) = 0 AND BLATEST = 'Y' AND fstatusflag = 1";
					Logger.error("fksql==" + fksql);
					List<Object[]> fkls = getDao.query(fksql);
					if (fkls != null && fkls.size() > 0 && fkls.get(0) != null
							&& fkls.get(0)[0] != null
							&& !"".equals(fkls.get(0)[0])) {
						Logger.error("fkls.get(0)[0]===" + fkls.get(0)[0]);
						htinfo = getFKJson(fkls.get(0)[0] + "");
					} else {
						// 付款合同
						String sksql = "SELECT PK_FCT_AR FROM FCT_AR WHERE VBILLCODE = '"
								+ mp.get("contractNo")
								+ "' AND PK_CUSTOMER = '"
								+ mp.get("supplier")
								+ "' AND NVL(DR,0) = 0 AND BLATEST = 'Y' AND fstatusflag = 1";
						Logger.error("sksql==" + sksql);
						List<Object[]> skls = getDao.query(sksql);
						if (sksql != null && skls.size() > 0
								&& skls.get(0) != null
								&& skls.get(0)[0] != null
								&& !"".equals(skls.get(0)[0])) {
							Logger.error("skls.get(0)[0]===" + skls.get(0)[0]);
							htinfo = getSKJson(skls.get(0)[0] + "");
						}
					}
				}
			}
		}
		return htinfo;
	}

	// 获取清单合同信息
	public String getQDJson(String pk_contr) throws DAOException,
			JSONException, UifException, MalformedURLException {
		JSONObject json = new JSONObject();
		JSONObject htJson = new JSONObject();
		JSONArray itemArr = new JSONArray();
		JSONArray httkArr = new JSONArray();
		JSONArray fileArr = new JSONArray();
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		Logger.error("清单合同主键：" + pk_contr);
		IContractService cs = (IContractService) NCLocator.getInstance()
				.lookup(IContractService.class);// 查询单据VO
		ContractBillVO[] wq = cs
				.queryBillVOByPk(new String[] { pk_contr + "" });// 查询清单信息
		if (wq != null && wq.length > 0) {
			ContractBillVO htVO = wq[0];
			// 合同表头
			htJson.put("ly_type", "工程合同");
			ContrHeadVO qdHeadVO = htVO.getParentVO();// 清单表头VO
			htJson.put("bill_code", qdHeadVO.getBill_code());// 合同编码
			htJson.put("bill_name", qdHeadVO.getBill_name());// 合同名称
			String typesql = "select type_name from pm_contracttype where pk_contracttype = '"
					+ qdHeadVO.getPk_contracttype() + "'";// 合同类型
			List<Object[]> typels = getDao.query(typesql);
			if (typels != null && typels.size() > 0 && typels.get(0) != null) {
				htJson.put("bill_type", typels.get(0)[0] + "");// 合同类型
			} else {
				htJson.put("bill_type", "");// 合同类型
			}
			List<String> bcxypk = new ArrayList<>();// 补充协议单据PK
			String bcxysql = "SELECT PK_CONTR_ALTER FROM PM_CONTR_ALTER WHERE PK_CONTR = '"
					+ pk_contr + "' AND DR = 0 ";// 合同类型
			Logger.error("补充协议SQL：" + bcxysql);
			List<Object[]> bcxyls = getDao.query(bcxysql);
			if (bcxyls != null && bcxyls.size() > 0 && bcxyls.get(0) != null
					&& bcxyls.get(0)[0] != null) {
				for (int i = 0; i < bcxyls.size(); i++) {
					bcxypk.add(bcxyls.get(i)[0] + "");// 补充协议单据PK
				}
			}

			String gysql = "select code,name from bd_supplier where pk_supplier = '"
					+ qdHeadVO.getPk_supplier() + "' and nvl(dr,0) = 0 ";// 供应商
			List<Object[]> gysls = getDao.query(gysql);
			if (gysls != null && gysls.size() > 0 && gysls.get(0) != null) {
				htJson.put("supplier_name", gysls.get(0)[1] + "");// 供应商
			} else {
				htJson.put("supplier_name", "");// 供应商
			}
			htJson.put("zbfs_name", getDefdocName(qdHeadVO.getHdef23()) + "");// 招标方式
			htJson.put("promisetime", qdHeadVO.getPromisetime());// 签约日期
			String bmsql = "select name from org_dept_v where pk_vid = '"
					+ qdHeadVO.getPk_pro_dept_v() + "'";// 签约部门
			List<Object[]> bmls = getDao.query(bmsql);
			if (bmls != null && bmls.size() > 0 && bmls.get(0) != null) {
				htJson.put("promisedept", bmls.get(0)[0] + "");// 签约部门
			} else {
				htJson.put("promisedept", "");// 签约部门
			}
			htJson.put("start_date", qdHeadVO.getPlan_validate_time() + "");// 开始日期
			htJson.put("end_date", qdHeadVO.getPlan_teminate_time() + "");// 结束日期
			String qyrsql = "select code,name from bd_psndoc where pk_psndoc = '"
					+ qdHeadVO.getPk_promisepsn() + "' and nvl(dr,0) = 0 ";// 签约人
			List<Object[]> qyrls = getDao.query(qyrsql);
			if (qyrls != null && qyrls.size() > 0 && qyrls.get(0) != null) {
				htJson.put("promisepsn_name", qyrls.get(0)[1] + "");// 签约人
			} else {
				htJson.put("promisepsn_name", "");// 签约人
			}
			String monsql = "SELECT 累计付款金额,累计发票金额 FROM V_OA_GCLHT WHERE 合同主键 = '"
					+ qdHeadVO.getPrimaryKey() + "'";
			List<Object[]> moneyls = getDao.query(monsql);
			if (moneyls != null && moneyls.size() > 0 && moneyls.get(0) != null) {
				htJson.put("ljfkje", moneyls.get(0)[0] + "");// 累计付款金额
				htJson.put("ljyfje", moneyls.get(0)[1] + "");// 累计发票金额
			}
			htJson.put("if_tsht", getDefdocName(qdHeadVO.getHdef21()) + "");// 是否特殊合同
			htJson.put("if_mbht", getDefdocName(qdHeadVO.getHdef22()) + "");// 是否模板合同
			htJson.put("if_blht", getDefdocName(qdHeadVO.getHdef25()) + "");// 是否补录合同
			htJson.put("ncurrent_mny", qdHeadVO.getNcurrent_mny());// 合同金额
			htJson.put("remark", qdHeadVO.getMemo());// 备注
			// 合同基本
			CircularlyAccessibleValueObject[] ChildrenVOs = htVO
					.getTableVO("works");// 合同基本 ContrWorksVO
			if (ChildrenVOs != null && ChildrenVOs.length > 0) {
				for (int qdi = 0; qdi < ChildrenVOs.length; qdi++) {
					ContrWorksVO conWorkVO = (ContrWorksVO) ChildrenVOs[qdi];// 合同基本VO
					JSONObject qdjson = new JSONObject();
					qdjson.put("rowno", conWorkVO.getRowno());// 行号
					String xmsql = "select project_code,project_name from bd_project where pk_project = '"
							+ conWorkVO.getPk_project() + "'";// 项目
					List<Object[]> xmls = getDao.query(xmsql);
					if (xmls != null && xmls.size() > 0 && xmls.get(0) != null) {
						qdjson.put("project_code", xmls.get(0)[0] + "");// 项目编码
						qdjson.put("project_name", xmls.get(0)[1] + "");// 项目名称
					} else {
						qdjson.put("project_code", "");// 项目编码
						qdjson.put("project_name", "");// 项目名称
					}
					String wlsql = "select code,name,materialspec from bd_material where pk_material = '"
							+ conWorkVO.getPk_material_v() + "'";// 物料
					List<Object[]> wlls = getDao.query(wlsql);
					if (wlls != null && wlls.size() > 0 && wlls.get(0) != null) {
						qdjson.put("material_code", wlls.get(0)[0] + "");// 编码
						qdjson.put("material_name", wlls.get(0)[1] + "");// 名称
						qdjson.put("material_spec", wlls.get(0)[2] + "");// 规格
					} else {
						qdjson.put("material_code", "");// 编码
						qdjson.put("material_name", "");// 名称
						qdjson.put("material_spec", "");// 规格
					}
					String dwsql = "select code,name from bd_measdoc where pk_measdoc = '"
							+ conWorkVO.getPk_measdoc()
							+ "' and nvl(dr,0) = 0 ";// 单位
					List<Object[]> dwls = getDao.query(dwsql);
					if (dwls != null && dwls.size() > 0 && dwls.get(0) != null) {
						qdjson.put("measdoc_name", dwls.get(0)[1] + "");// 单位
					} else {
						qdjson.put("measdoc_name", "");// 单位
					}
					qdjson.put("curr_num", conWorkVO.getCurr_num() + "");// 数量
					qdjson.put("nprom_price", conWorkVO.getNprom_price() + "");// 单价
					qdjson.put("curr_mny", conWorkVO.getCurr_mny() + "");// 金额
					itemArr.put(qdjson);
				}
			}
			// 合同条款
			CircularlyAccessibleValueObject[] ChildrenTermVOs = htVO
					.getTableVO("items");// 合同条款 ContrItemsVO
			if (ChildrenTermVOs != null && ChildrenTermVOs.length > 0) {
				for (int tki = 0; tki < ChildrenTermVOs.length; tki++) {
					ContrItemsVO itemvo = (ContrItemsVO) ChildrenTermVOs[tki];// 合同条款VO
					JSONObject tkjson = new JSONObject();
					String tksql = "select vtermcode,vtermname,vtermcontent,pk_ct_termtype from ct_termset where pk_ct_termset = '"
							+ itemvo.getPk_ct_termset()
							+ "' and nvl(dr,0) = 0 ";// 条款
					List<Object[]> tkls = getDao.query(tksql);
					if (tkls != null && tkls.size() > 0 && tkls.get(0) != null) {
						tkjson.put("vtermcode", tkls.get(0)[0] + "");// 条款编码
						tkjson.put("vtermname", tkls.get(0)[1] + "");// 条款名称
						tkjson.put("vtermcontent", tkls.get(0)[2] + "");// 条款内容
						String lxsql = "select termtypecode,termtypename from ct_termtype where pk_ct_termtype = '"
								+ tkls.get(0)[3] + "' and  nvl(dr,0) = 0 ";// 条款类型
						List<Object[]> lxls = getDao.query(lxsql);
						if (lxls != null && lxls.size() > 0
								&& lxls.get(0) != null) {
							tkjson.put("termtypename", lxls.get(0)[1] + "");// 条款类型
						} else {
							tkjson.put("termtypename", "");// 条款类型
						}
					} else {
						tkjson.put("vtermcode", "");// 条款编码
						tkjson.put("vtermname", "");// 条款名称
						tkjson.put("vtermcontent", "");// 条款内容
						tkjson.put("termtypename", "");// 条款类型
					}
					tkjson.put("other_info", itemvo.getOther_info() + "");// 其他信息
					tkjson.put("memo", itemvo.getMemo() + "");// 备注
					httkArr.put(tkjson);
				}
			}
			// 附件信息
			String filesql = "select distinct fh.name,fp.filelength,fp.filedesc,fh.createtime,fp.pk_doc from sm_pub_filesystem fp left join bap_fs_header fh on fp.pk_doc=fh.path"
					+ " left join bap_fs_body fb on fh.GUID = fb.headid"
					+ " where filepath like '" + pk_contr + "/%'";// 完整正确的sql语句
			Logger.error("bcxypk==" + bcxypk);
			if (bcxypk.size() > 0) {
				for (String bcpk : bcxypk) {
					filesql += " OR filepath like '" + bcpk + "/%'";
				}
			}
			Logger.error("filesql：" + filesql);
			List<Object[]> filels = getDao.query(filesql);
			if (filels != null && filels.size() > 0) {
				for (int i = 0; i < filels.size(); i++) {
					JSONObject filejson = new JSONObject();
					filejson.put("filename", filels.get(i)[0] + "");
					filejson.put("filelength", filels.get(i)[1] + "");
					filejson.put("filedesc", filels.get(i)[2] + "");
					filejson.put("createtime", filels.get(i)[3] + "");
					String urlstring = FileStorageClient.getInstance()
							.getDownloadURL(null, filels.get(i)[4] + "");
					Logger.error("附件：" + urlstring);
					// byte data[] = urlTobyte(urlstring);
					// filejson.put("filedt", data);
					filejson.put("fileurl", urlstring);
					fileArr.put(filejson);
				}
			}
		}
		json.put("htinfo", htJson);// 合同信息
		json.put("mxinfo", itemArr);// 合同基本
		json.put("tkinfo", httkArr);// 合同条款
		json.put("fjinfo", fileArr);// 合同附件
		return json.toString();
	}

	// 获取采购合同信息
	public String getCGJson(String pk_ct_pu) throws DAOException,
			JSONException, UifException {
		JSONObject json = new JSONObject();
		JSONObject htJson = new JSONObject();
		JSONArray itemArr = new JSONArray();
		JSONArray httkArr = new JSONArray();
		JSONArray fileArr = new JSONArray();
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		Logger.error("采购合同主键：" + pk_ct_pu);
		IBillQueryService billquery = NCLocator.getInstance().lookup(
				IBillQueryService.class);
		AggCtPuVO aggCtPuVO = billquery.querySingleBillByPk(AggCtPuVO.class,
				pk_ct_pu + "");
		if (aggCtPuVO != null) {
			// 合同信息
			htJson.put("ly_type", "采购合同");
			CtPuVO ctHeadVO = aggCtPuVO.getParentVO();
			htJson.put("bill_code", ctHeadVO.getVbillcode());// 合同编码
			htJson.put("bill_name", ctHeadVO.getCtname());// 合同名称
			/*
			 * String typesql =
			 * "select type_name from pm_contracttype where pk_contracttype = '"
			 * + ctHeadVO.getCtrantypeid() + "'";
			 */// 合同类型
			String billTypeName = (String) new HYPubBO().findColValue(
					"bd_billtype",
					"billtypename",
					"nvl(dr,0) = 0 and pk_billtypeid = '"
							+ ctHeadVO.getCtrantypeid() + "'");
			// List<Object[]> typels = getDao.query(typesql);
			if (billTypeName != null) {
				htJson.put("bill_type", billTypeName);// 合同类型
			} else {
				htJson.put("bill_type", "");// 合同类型
			}
			String gysql = "select code,name from bd_supplier where pk_supplier = '"
					+ ctHeadVO.getCvendorid() + "' and nvl(dr,0) = 0 ";// 供应商
			List<Object[]> gysls = getDao.query(gysql);
			if (gysls != null && gysls.size() > 0 && gysls.get(0) != null) {
				htJson.put("supplier_name", gysls.get(0)[1] + "");// 供应商
			} else {
				htJson.put("supplier_name", "");// 供应商
			}
			htJson.put("zbfs_name", getDefdocName(ctHeadVO.getVdef15()) + "");// 招标方式
			htJson.put("promisetime", ctHeadVO.getSubscribedate());// 签约日期
			String bmsql = "select name from org_dept_v where pk_vid = '"
					+ ctHeadVO.getDepid_v() + "'";// 签约部门
			List<Object[]> bmls = getDao.query(bmsql);
			if (bmls != null && bmls.size() > 0 && bmls.get(0) != null) {
				htJson.put("promisedept", bmls.get(0)[0] + "");// 签约部门
			} else {
				htJson.put("promisedept", "");// 签约部门
			}
			htJson.put("start_date", ctHeadVO.getValdate() + "");// 开始日期
			htJson.put("end_date", ctHeadVO.getInvallidate() + "");// 结束日期
			String qyrsql = "select code,name from bd_psndoc where pk_psndoc = '"
					+ ctHeadVO.getPersonnelid() + "' and nvl(dr,0) = 0 ";// 签约人
			List<Object[]> qyrls = getDao.query(qyrsql);
			if (qyrls != null && qyrls.size() > 0 && qyrls.get(0) != null) {
				htJson.put("promisepsn_name", qyrls.get(0)[1] + "");// 签约人
			} else {
				htJson.put("promisepsn_name", "");// 签约人
			}
			htJson.put("if_tsht", getDefdocName(ctHeadVO.getVdef10()) + "");// 是否特殊合同
			htJson.put("if_mbht", getDefdocName(ctHeadVO.getVdef11()) + "");// 是否模板合同
			htJson.put("if_blht", getDefdocName(ctHeadVO.getVdef16()) + "");// 是否补录合同
			htJson.put("ncurrent_mny", ctHeadVO.getNtotalorigmny());// 合同金额
			String monsql = "SELECT 累计付款金额,累计发票金额 FROM V_OA_CGHT WHERE 合同主键 = '"
					+ ctHeadVO.getPrimaryKey() + "'";
			List<Object[]> moneyls = getDao.query(monsql);
			if (moneyls != null && moneyls.size() > 0 && moneyls.get(0) != null) {
				htJson.put("ljfkje", moneyls.get(0)[0] + "");// 累计付款金额
				htJson.put("ljyfje", moneyls.get(0)[1] + "");// 累计发票金额
			}
			htJson.put("remark", ctHeadVO.getVdef7());// 备注
			// 合同基本
			CircularlyAccessibleValueObject[] ChildrenVOs = aggCtPuVO
					.getTableVO("pk_ct_pu_b");// 合同基本
			if (ChildrenVOs != null && ChildrenVOs.length > 0) {
				for (int cgi = 0; cgi < ChildrenVOs.length; cgi++) {
					CtPuBVO conWorkVO = (CtPuBVO) ChildrenVOs[cgi];
					JSONObject cgjson = new JSONObject();
					cgjson.put("rowno", conWorkVO.getCrowno());// 行号
					String xmsql = "select project_code,project_name from bd_project where pk_project = '"
							+ conWorkVO.getCbprojectid() + "'";// 项目
					List<Object[]> xmls = getDao.query(xmsql);
					if (xmls != null && xmls.size() > 0 && xmls.get(0) != null) {
						cgjson.put("project_code", xmls.get(0)[0] + "");// 项目编码
						cgjson.put("project_name", xmls.get(0)[1] + "");// 项目名称
					} else {
						cgjson.put("project_code", "");// 项目编码
						cgjson.put("project_name", "");// 项目名称
					}
					String wlsql = "select code,name,materialspec from bd_material where pk_material = '"
							+ conWorkVO.getPk_material() + "'";// 物料
					List<Object[]> wlls = getDao.query(wlsql);
					if (wlls != null && wlls.size() > 0 && wlls.get(0) != null) {
						cgjson.put("material_code", wlls.get(0)[0] + "");// 编码
						cgjson.put("material_name", wlls.get(0)[1] + "");// 名称
						cgjson.put("material_spec", wlls.get(0)[2] + "");// 规格
					} else {
						cgjson.put("material_code", "");// 编码
						cgjson.put("material_name", "");// 名称
						cgjson.put("material_spec", "");// 规格
					}
					String dwsql = "select code,name from bd_measdoc where pk_measdoc = '"
							+ conWorkVO.getCastunitid()
							+ "' and nvl(dr,0) = 0 ";// 单位
					List<Object[]> dwls = getDao.query(dwsql);
					if (dwls != null && dwls.size() > 0 && dwls.get(0) != null) {
						cgjson.put("measdoc_name", dwls.get(0)[1] + "");// 单位
					} else {
						cgjson.put("measdoc_name", "");// 单位
					}
					cgjson.put("curr_num", conWorkVO.getNastnum() + "");// 数量
					cgjson.put("nprom_price", conWorkVO.getNqtorigtaxprice()
							+ "");// 单价 含税单价
					cgjson.put("curr_mny", conWorkVO.getNorigtaxmny() + "");// 金额
					itemArr.put(cgjson);
				}
			}
			// 合同条款
			CircularlyAccessibleValueObject[] ChildrenTermVOs = aggCtPuVO
					.getTableVO("pk_ct_pu_term");// 合同条款 ContrItemsVO
			if (ChildrenTermVOs != null && ChildrenTermVOs.length > 0) {
				for (int cgtki = 0; cgtki < ChildrenTermVOs.length; cgtki++) {
					CtPuTermVO itemvo = (CtPuTermVO) ChildrenTermVOs[cgtki];// 合同条款VO
					JSONObject tkjson = new JSONObject();
					String tksql = "select vtermcode,vtermname,vtermcontent,pk_ct_termtype from ct_termset where pk_ct_termset = '"
							+ itemvo.getVtermcode() + "' and nvl(dr,0) = 0 ";// 条款
					List<Object[]> tkls = getDao.query(tksql);
					if (tkls != null && tkls.size() > 0 && tkls.get(0) != null) {
						tkjson.put("vtermcode", tkls.get(0)[0] + "");// 条款编码
						tkjson.put("vtermname", tkls.get(0)[1] + "");// 条款名称
						tkjson.put("vtermcontent", tkls.get(0)[2] + "");// 条款内容
						String lxsql = "select termtypecode,termtypename from ct_termtype where pk_ct_termtype = '"
								+ tkls.get(0)[3] + "' and  nvl(dr,0) = 0 ";// 条款类型
						List<Object[]> lxls = getDao.query(lxsql);
						if (lxls != null && lxls.size() > 0
								&& lxls.get(0) != null) {
							tkjson.put("termtypename", lxls.get(0)[1] + "");// 条款类型
						} else {
							tkjson.put("termtypename", "");// 条款类型
						}
					} else {
						tkjson.put("vtermcode", "");// 条款编码
						tkjson.put("vtermname", "");// 条款名称
						tkjson.put("vtermcontent", "");// 条款内容
						tkjson.put("termtypename", "");// 条款类型
					}
					tkjson.put("other_info", itemvo.getVotherinfo() + "");// 其他信息
					tkjson.put("memo", itemvo.getVmemo() + "");// 备注
					httkArr.put(tkjson);
				}
			}
			// 附件信息
			String filesql = "select distinct fh.name,fp.filelength,fp.filedesc,fh.createtime,fp.pk_doc from sm_pub_filesystem fp left join bap_fs_header fh on fp.pk_doc=fh.path"
					+ " left join bap_fs_body fb on fh.GUID = fb.headid"
					+ " where filepath like '" + pk_ct_pu + "/%'";// 完整正确的sql语句
			List<Object[]> filels = getDao.query(filesql);
			if (filels != null && filels.size() > 0) {
				for (int i = 0; i < filels.size(); i++) {
					JSONObject filejson = new JSONObject();
					filejson.put("filename", filels.get(i)[0] + "");
					filejson.put("filelength", filels.get(i)[1] + "");
					filejson.put("filedesc", filels.get(i)[2] + "");
					filejson.put("createtime", filels.get(i)[3] + "");
					String urlstring = FileStorageClient.getInstance()
							.getDownloadURL(null, filels.get(i)[4] + "");
					Logger.error("附件：" + urlstring);
					// byte data[] = urlTobyte(urlstring);
					// filejson.put("filedt", data);
					filejson.put("fileurl", urlstring);
					fileArr.put(filejson);
				}
			}
		}
		json.put("htinfo", htJson);// 合同信息
		json.put("mxinfo", itemArr);// 合同基本
		json.put("tkinfo", httkArr);// 合同条款
		json.put("fjinfo", fileArr);// 合同附件
		return json.toString();
	}

	// 获取多编码合同信息
	public String getDBMJson(String pk_feebalance) throws DAOException,
			JSONException, UifException {
		JSONObject json = new JSONObject();
		JSONObject htJson = new JSONObject();
		JSONArray itemArr = new JSONArray();
		JSONArray httkArr = new JSONArray();
		JSONArray fileArr = new JSONArray();
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		Logger.error("多编码合同主键：" + pk_feebalance);
		IBillQueryService billquery = NCLocator.getInstance().lookup(
				IBillQueryService.class);
		AggPmFeebalance dbmvo = billquery.querySingleBillByPk(
				AggPmFeebalance.class, pk_feebalance + "");
		if (dbmvo != null) {
			// 合同信息
			htJson.put("ly_type", "工程合同");
			PmFeebalanceHVO fkHeadVO = (PmFeebalanceHVO) dbmvo.getParentVO();
			htJson.put("bill_code", fkHeadVO.getBill_code());// 合同编码
			htJson.put("bill_name", fkHeadVO.getBill_name());// 合同名称
			String typesql = "select type_name from pm_contracttype where pk_contracttype = '"
					+ fkHeadVO.getContracttype() + "'";// 合同类型
			List<Object[]> typels = getDao.query(typesql);
			if (typels != null && typels.size() > 0 && typels.get(0) != null) {
				htJson.put("bill_type", typels.get(0)[0] + "");// 合同类型
			} else {
				htJson.put("bill_type", "");// 合同类型
			}
			String gysql = "select code,name from bd_supplier where pk_supplier = '"
					+ fkHeadVO.getPk_supplier_name() + "' and nvl(dr,0) = 0 ";// 供应商
			List<Object[]> gysls = getDao.query(gysql);
			if (gysls != null && gysls.size() > 0 && gysls.get(0) != null) {
				htJson.put("supplier_name", gysls.get(0)[1] + "");// 供应商
			} else {
				htJson.put("supplier_name", "");// 供应商
			}
			htJson.put("zbfs_name", getDefdocName(fkHeadVO.getZbmethod()) + "");// 招标方式
			htJson.put("promisetime", fkHeadVO.getSigndate());// 签约日期
			String bmsql = "select name from org_dept where pk_dept = '"
					+ fkHeadVO.getPk_qydept() + "'";// 签约部门
			List<Object[]> bmls = getDao.query(bmsql);
			if (bmls != null && bmls.size() > 0 && bmls.get(0) != null) {
				htJson.put("promisedept", bmls.get(0)[0] + "");// 签约部门
			} else {
				htJson.put("promisedept", "");// 签约部门
			}
			String monsql = "SELECT 累计付款金额,累计发票金额 FROM V_OA_GCLHT WHERE 合同主键 = '"
					+ fkHeadVO.getPrimaryKey() + "'";
			List<Object[]> moneyls = getDao.query(monsql);
			if (moneyls != null && moneyls.size() > 0 && moneyls.get(0) != null) {
				htJson.put("ljfkje", moneyls.get(0)[0] + "");// 累计付款金额
				htJson.put("ljyfje", moneyls.get(0)[1] + "");// 累计发票金额
			}
			htJson.put("start_date", fkHeadVO.getStartdate() + "");// 开始日期
			htJson.put("end_date", fkHeadVO.getEnddate() + "");// 结束日期
			String qyrsql = "select code,name from bd_psndoc where pk_psndoc = '"
					+ fkHeadVO.getPk_signer_name() + "' and nvl(dr,0) = 0 ";// 签约人
			List<Object[]> qyrls = getDao.query(qyrsql);
			if (qyrls != null && qyrls.size() > 0 && qyrls.get(0) != null) {
				htJson.put("promisepsn_name", qyrls.get(0)[1] + "");// 签约人
			} else {
				htJson.put("promisepsn_name", "");// 签约人
			}
			htJson.put("if_tsht", "");// 是否特殊合同
			htJson.put("if_mbht", "");// 是否模板合同
			htJson.put("if_blht", "");// 是否补录合同
			htJson.put("ncurrent_mny", fkHeadVO.getContractmoney());// 合同金额
			htJson.put("remark", fkHeadVO.getVmemo());// 备注
			// 合同基本
			PmFeebalanceBVO[] ChildrenVOs = dbmvo.getChildrenVO();// 合同基本
			if (ChildrenVOs != null && ChildrenVOs.length > 0) {
				for (int dbmi = 0; dbmi < ChildrenVOs.length; dbmi++) {
					PmFeebalanceBVO conWorkVO = (PmFeebalanceBVO) ChildrenVOs[dbmi];
					JSONObject dbmjson = new JSONObject();
					dbmjson.put("rowno", conWorkVO.getRowno());// 行号
					String xmsql = "select project_code,project_name from bd_project where pk_project = '"
							+ conWorkVO.getPk_project() + "'";// 项目
					List<Object[]> xmls = getDao.query(xmsql);
					if (xmls != null && xmls.size() > 0 && xmls.get(0) != null) {
						dbmjson.put("project_code", xmls.get(0)[0] + "");// 项目编码
						dbmjson.put("project_name", xmls.get(0)[1] + "");// 项目名称
					} else {
						dbmjson.put("project_code", "");// 项目编码
						dbmjson.put("project_name", "");// 项目名称
					}
					String wlsql = "select code,name,materialspec from bd_material where pk_material = '"
							+ conWorkVO.getMaterial_code() + "'";// 物料
					List<Object[]> wlls = getDao.query(wlsql);
					if (wlls != null && wlls.size() > 0 && wlls.get(0) != null) {
						dbmjson.put("material_code", wlls.get(0)[0] + "");// 编码
						dbmjson.put("material_name", wlls.get(0)[1] + "");// 名称
						dbmjson.put("material_spec", wlls.get(0)[2] + "");// 规格
					} else {
						dbmjson.put("material_code", "");// 编码
						dbmjson.put("material_name", "");// 名称
						dbmjson.put("material_spec", "");// 规格
					}
					String dwsql = "select code,name from bd_measdoc where pk_measdoc = '"
							+ conWorkVO.getPk_measdoc()
							+ "' and nvl(dr,0) = 0 ";// 单位
					List<Object[]> dwls = getDao.query(dwsql);
					if (dwls != null && dwls.size() > 0 && dwls.get(0) != null) {
						dbmjson.put("measdoc_name", dwls.get(0)[1] + "");// 单位
					} else {
						dbmjson.put("measdoc_name", "");// 单位
					}
					dbmjson.put("curr_num", conWorkVO.getNum() + "");// 数量
					dbmjson.put("nprom_price", conWorkVO.getDef2() + "");// 单价
					dbmjson.put("curr_mny", conWorkVO.getMoney() + "");// 金额
					itemArr.put(dbmjson);
				}
			}
			// 合同条款

			// 附件信息
			String filesql = "select distinct fh.name,fp.filelength,fp.filedesc,fh.createtime,fp.pk_doc from sm_pub_filesystem fp left join bap_fs_header fh on fp.pk_doc=fh.path"
					+ " left join bap_fs_body fb on fh.GUID = fb.headid"
					+ " where filepath like '" + pk_feebalance + "/%'";// 完整正确的sql语句
			List<Object[]> filels = getDao.query(filesql);
			if (filels != null && filels.size() > 0) {
				for (int i = 0; i < filels.size(); i++) {
					JSONObject filejson = new JSONObject();
					filejson.put("filename", filels.get(i)[0] + "");
					filejson.put("filelength", filels.get(i)[1] + "");
					filejson.put("filedesc", filels.get(i)[2] + "");
					filejson.put("createtime", filels.get(i)[3] + "");
					String urlstring = FileStorageClient.getInstance()
							.getDownloadURL(null, filels.get(i)[4] + "");
					Logger.error("附件：" + urlstring);
					// byte data[] = urlTobyte(urlstring);
					// filejson.put("filedt", data);
					filejson.put("fileurl", urlstring);
					fileArr.put(filejson);
				}
			}
		}
		json.put("htinfo", htJson);// 合同信息
		json.put("mxinfo", itemArr);// 合同基本
		json.put("tkinfo", httkArr);// 合同条款
		json.put("fjinfo", fileArr);// 合同附件
		return json.toString();
	}

	// 获取付款合同信息
	public String getFKJson(String pk_fct_ap) throws JSONException,
			DAOException {
		JSONObject json = new JSONObject();
		JSONObject htJson = new JSONObject();
		JSONArray itemArr = new JSONArray();
		JSONArray httkArr = new JSONArray();
		JSONArray fileArr = new JSONArray();
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		Logger.error("付款合同主键：" + pk_fct_ap);

		/*
		 * IBillQueryService billquery = NCLocator.getInstance().lookup(
		 * IBillQueryService.class); AggCtApVO fkvo =
		 * billquery.querySingleBillByPk( AggCtApVO.class, pk_fct_ap + "");
		 */

		/*
		 * String[] userObj = { AggCtApVO.class.getName(),
		 * CtApVO.class.getName(), CtApBVO.class.getName() }; AggCtApVO fkvo =
		 * (AggCtApVO) new HYPubBO() .queryBillVOByPrimaryKey(userObj,
		 * pk_fct_ap);
		 */
		IApMaintain cpcs = NCLocator.getInstance().lookup(IApMaintain.class);// 查询单据VO
		try {
			AggCtApVO[] fkvos = cpcs
					.queryCtApVoByIds(new String[] { pk_fct_ap });
			if (fkvos != null && fkvos.length > 0) {
				AggCtApVO fkvo = fkvos[0];
				if (fkvo != null) {
					htJson.put("ly_type", "付款合同");
					CtApVO fkHeadVO = fkvo.getParentVO();
					htJson.put("bill_code", fkHeadVO.getVbillcode());// 合同编码
					htJson.put("bill_name", fkHeadVO.getCtname());// 合同名称
					String typesql = "select name from bd_defdoc where pk_defdoc = '"
							+ fkHeadVO.getVdef18() + "'";// 合同类型
					List<Object[]> typels = getDao.query(typesql);
					if (typels != null && typels.size() > 0
							&& typels.get(0) != null) {
						htJson.put("bill_type", typels.get(0)[0] + "");// 合同类型
					} else {
						htJson.put("bill_type", "");// 合同类型
					}
					String monsql = "SELECT 累计付款金额,累计开票金额 FROM V_OA_FKLHT WHERE 合同主键 = '"
							+ fkHeadVO.getPrimaryKey() + "'";
					List<Object[]> moneyls = getDao.query(monsql);
					if (moneyls != null && moneyls.size() > 0
							&& moneyls.get(0) != null) {
						htJson.put("ljfkje", moneyls.get(0)[0] + "");// 累计付款金额
						htJson.put("ljyfje", moneyls.get(0)[1] + "");// 累计发票金额
					}
					String gysql = "select code,name from bd_supplier where pk_supplier = '"
							+ fkHeadVO.getCvendorid() + "' and nvl(dr,0) = 0 ";// 供应商
					List<Object[]> gysls = getDao.query(gysql);
					if (gysls != null && gysls.size() > 0
							&& gysls.get(0) != null) {
						htJson.put("supplier_name", gysls.get(0)[1] + "");// 供应商
					} else {
						htJson.put("supplier_name", "");// 供应商
					}
					htJson.put("zbfs_name", getDefdocName(fkHeadVO.getVdef3())
							+ "");// 招标方式
					htJson.put("promisetime", fkHeadVO.getSubscribedate());// 签约日期
					String bmsql = "select name from org_dept where pk_dept = '"
							+ fkHeadVO.getDepid() + "'";// 签约部门
					List<Object[]> bmls = getDao.query(bmsql);
					if (bmls != null && bmls.size() > 0 && bmls.get(0) != null) {
						htJson.put("promisedept", bmls.get(0)[0] + "");// 签约部门
					} else {
						htJson.put("promisedept", "");// 签约部门
					}
					htJson.put("start_date", fkHeadVO.getValdate() + "");// 开始日期
					htJson.put("end_date", fkHeadVO.getInvallidate() + "");// 结束日期
					String qyrsql = "select code,name from bd_psndoc where pk_psndoc = '"
							+ fkHeadVO.getPersonnelid()
							+ "' and nvl(dr,0) = 0 ";// 签约人
					List<Object[]> qyrls = getDao.query(qyrsql);
					if (qyrls != null && qyrls.size() > 0
							&& qyrls.get(0) != null) {
						htJson.put("promisepsn_name", qyrls.get(0)[1] + "");// 签约人
					} else {
						htJson.put("promisepsn_name", "");// 签约人
					}
					htJson.put("if_tsht", "");// 是否特殊合同
					htJson.put("if_mbht", "");// 是否模板合同
					htJson.put("if_blht", "");// 是否补录合同
					htJson.put("ncurrent_mny", fkHeadVO.getNtotaltaxmny());// 合同金额
					htJson.put("remark", fkHeadVO.getVdef20());// 备注
					// 合同基本
					CtApBVO[] ChildrenVOs = fkvo.getCtApBVO();// 合同基本
					if (ChildrenVOs != null && ChildrenVOs.length > 0) {
						for (int fki = 0; fki < ChildrenVOs.length; fki++) {
							CtApBVO conWorkVO = (CtApBVO) ChildrenVOs[fki];
							JSONObject fkjson = new JSONObject();
							fkjson.put("rowno", conWorkVO.getCrowno());// 行号
							String xmsql = "select project_code,project_name from bd_project where pk_project = '"
									+ conWorkVO.getProject() + "'";// 项目
							List<Object[]> xmls = getDao.query(xmsql);
							if (xmls != null && xmls.size() > 0
									&& xmls.get(0) != null) {
								fkjson.put("project_code", xmls.get(0)[0] + "");// 项目编码
								fkjson.put("project_name", xmls.get(0)[1] + "");// 项目名称
							} else {
								fkjson.put("project_code", "");// 项目编码
								fkjson.put("project_name", "");// 项目名称
							}
							String yslbsql = "SELECT NAME FROM BD_INOUTBUSICLASS WHERE PK_INOUTBUSICLASS = '"
									+ conWorkVO.getInoutcome()
									+ "' AND DR = 0 ";
							List<Object[]> lbls = getDao.query(yslbsql);
							if (lbls != null && lbls.size() > 0
									&& lbls.get(0) != null) {
								fkjson.put("material_code", lbls.get(0)[0] + "");// 编码
																					// 预算类别
							} else {
								fkjson.put("material_code", "");// 编码
							}
							if (conWorkVO.getVbdef5() == null
									|| conWorkVO.getVbdef5() == "") {
								fkjson.put("material_name",
										conWorkVO.getVbdef4());// 名称
							} else {
								fkjson.put("material_name",
										conWorkVO.getVbdef5());// 名称
							}
							fkjson.put("measdoc_name", "");// 单位
							fkjson.put("material_spec", conWorkVO.getVbdef4()
									+ "");// 规格
							fkjson.put("curr_num", conWorkVO.getNnum() + "");// 数量
							fkjson.put("nprom_price", conWorkVO.getNgtaxprice()
									+ "");// 单价
							fkjson.put("curr_mny", conWorkVO.getNtaxmny() + "");// 金额
							itemArr.put(fkjson);
						}
					}
					// 合同条款

					// 附件信息
					String filesql = "select distinct fh.name,fp.filelength,fp.filedesc,fh.createtime,fp.pk_doc from sm_pub_filesystem fp left join bap_fs_header fh on fp.pk_doc=fh.path"
							+ " left join bap_fs_body fb on fh.GUID = fb.headid"
							+ " where filepath like '" + pk_fct_ap + "/%'";// 完整正确的sql语句
					List<Object[]> filels = getDao.query(filesql);
					if (filels != null && filels.size() > 0) {
						for (int i = 0; i < filels.size(); i++) {
							JSONObject filejson = new JSONObject();
							filejson.put("filename", filels.get(i)[0] + "");
							filejson.put("filelength", filels.get(i)[1] + "");
							filejson.put("filedesc", filels.get(i)[2] + "");
							filejson.put("createtime", filels.get(i)[3] + "");
							String urlstring = FileStorageClient
									.getInstance()
									.getDownloadURL(null, filels.get(i)[4] + "");
							Logger.error("附件：" + urlstring);
							// byte data[] = urlTobyte(urlstring);
							// filejson.put("filedt", data);
							filejson.put("fileurl", urlstring);
							fileArr.put(filejson);
						}
					}
				}
			} else {
				System.out.println("未获取到付款合同！");
				Logger.error("未获取到付款合同！");
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		json.put("htinfo", htJson);// 合同信息
		json.put("mxinfo", itemArr);// 合同基本
		json.put("tkinfo", httkArr);// 合同条款
		json.put("fjinfo", fileArr);// 合同附件
		return json.toString();
	}

	// 获取付款合同信息
	public String getSKJson(String pk_fct_ar) throws JSONException,
			DAOException {
		JSONObject json = new JSONObject();
		JSONObject htJson = new JSONObject();
		JSONArray itemArr = new JSONArray();
		JSONArray httkArr = new JSONArray();
		JSONArray fileArr = new JSONArray();
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		Logger.error("收款合同主键：" + pk_fct_ar);

		/*
		 * IBillQueryService billquery = NCLocator.getInstance().lookup(
		 * IBillQueryService.class); AggCtApVO fkvo =
		 * billquery.querySingleBillByPk( AggCtApVO.class, pk_fct_ap + "");
		 */

		/*
		 * String[] userObj = { AggCtApVO.class.getName(),
		 * CtApVO.class.getName(), CtApBVO.class.getName() }; AggCtApVO fkvo =
		 * (AggCtApVO) new HYPubBO() .queryBillVOByPrimaryKey(userObj,
		 * pk_fct_ap);
		 */
		IArMaintain cpcs = NCLocator.getInstance().lookup(IArMaintain.class);// 查询单据VO
		try {
			AggCtArVO[] skvos = cpcs
					.queryCtArVoByIds(new String[] { pk_fct_ar });
			if (skvos != null && skvos.length > 0) {
				AggCtArVO skvo = skvos[0];
				if (skvo != null) {
					htJson.put("ly_type", "收款合同");
					CtArVO skHeadVO = skvo.getParentVO();
					htJson.put("bill_code", skHeadVO.getVbillcode());// 合同编码
					htJson.put("bill_name", skHeadVO.getCtname());// 合同名称
					String typesql = "select name from bd_defdoc where pk_defdoc = '"
							+ skHeadVO.getVdef18() + "'";// 合同类型
					List<Object[]> typels = getDao.query(typesql);
					if (typels != null && typels.size() > 0
							&& typels.get(0) != null) {
						htJson.put("bill_type", typels.get(0)[0] + "");// 合同类型
					} else {
						htJson.put("bill_type", "");// 合同类型
					}
					String monsql = "SELECT LJSKJE,YSJE FROM VIEW_SKHTTZ WHERE HTZJ = '"
							+ skHeadVO.getPrimaryKey() + "'";
					List<Object[]> moneyls = getDao.query(monsql);
					if (moneyls != null && moneyls.size() > 0
							&& moneyls.get(0) != null) {
						htJson.put("ljskje", moneyls.get(0)[0] + "");// 累计收款金额
						htJson.put("ljyfje", moneyls.get(0)[1] + "");// 累计开票金额
					}
					String gysql = "select code,name from bd_customer where pk_customer = '"
							+ skHeadVO.getPk_customer()
							+ "' and nvl(dr,0) = 0 ";// 客户
					List<Object[]> gysls = getDao.query(gysql);
					if (gysls != null && gysls.size() > 0
							&& gysls.get(0) != null) {
						htJson.put("customer_name", gysls.get(0)[1] + "");// 客户
					} else {
						htJson.put("customer_name", "");// 客户
					}
					htJson.put("zbfs_name", getDefdocName(skHeadVO.getVdef2())
							+ "");// 招标方式
					htJson.put("promisetime", skHeadVO.getSubscribedate());// 合同签订日期
					String bmsql = "select name from org_dept where pk_dept = '"
							+ skHeadVO.getDepid() + "'";// 承办部门
					List<Object[]> bmls = getDao.query(bmsql);
					if (bmls != null && bmls.size() > 0 && bmls.get(0) != null) {
						htJson.put("promisedept", bmls.get(0)[0] + "");// 承办部门
					} else {
						htJson.put("promisedept", "");// 承办部门
					}
					htJson.put("start_date", skHeadVO.getValdate() + "");// 生效日期
					htJson.put("end_date", skHeadVO.getInvallidate() + "");// 终止日期
					String qyrsql = "select code,name from bd_psndoc where pk_psndoc = '"
							+ skHeadVO.getPersonnelid()
							+ "' and nvl(dr,0) = 0 ";// 承办人
					List<Object[]> qyrls = getDao.query(qyrsql);
					if (qyrls != null && qyrls.size() > 0
							&& qyrls.get(0) != null) {
						htJson.put("promisepsn_name", qyrls.get(0)[1] + "");// 承办人
					} else {
						htJson.put("promisepsn_name", "");// 承办人
					}
					htJson.put("if_tsht", "");// 是否特殊合同
					htJson.put("if_mbht", "");// 是否模板合同
					htJson.put("if_blht", "");// 是否补录合同
					htJson.put("ncurrent_mny", skHeadVO.getNtotaltaxmny());// 合同金额
					htJson.put("remark", skHeadVO.getVdef20());// 备注
					// 合同基本
					CtArBVO[] ChildrenVOs = skvo.getCtArBVO();// 合同基本
					if (ChildrenVOs != null && ChildrenVOs.length > 0) {
						for (int ski = 0; ski < ChildrenVOs.length; ski++) {
							CtArBVO conWorkVO = (CtArBVO) ChildrenVOs[ski];
							JSONObject fkjson = new JSONObject();
							fkjson.put("rowno", conWorkVO.getCrowno());// 行号
							String xmsql = "select project_code,project_name from bd_project where pk_project = '"
									+ conWorkVO.getProject() + "'";// 项目
							List<Object[]> xmls = getDao.query(xmsql);
							if (xmls != null && xmls.size() > 0
									&& xmls.get(0) != null) {
								fkjson.put("project_code", xmls.get(0)[0] + "");// 项目编码
								fkjson.put("project_name", xmls.get(0)[1] + "");// 项目名称
							} else {
								fkjson.put("project_code", "");// 项目编码
								fkjson.put("project_name", "");// 项目名称
							}
							String yslbsql = "SELECT NAME FROM BD_INOUTBUSICLASS WHERE PK_INOUTBUSICLASS = '"
									+ conWorkVO.getInoutcome()
									+ "' AND DR = 0 ";
							List<Object[]> lbls = getDao.query(yslbsql);
							if (lbls != null && lbls.size() > 0
									&& lbls.get(0) != null) {
								fkjson.put("material_code", lbls.get(0)[0] + "");// 编码
																					// 预算类别
							} else {
								fkjson.put("material_code", "");// 编码
							}
							if (conWorkVO.getVbdef5() == null
									|| conWorkVO.getVbdef5() == "") {
								fkjson.put("material_name",
										conWorkVO.getVbdef4());// 名称
							} else {
								fkjson.put("material_name",
										conWorkVO.getVbdef5());// 名称
							}
							fkjson.put("measdoc_name", "");// 单位
							fkjson.put("material_spec", conWorkVO.getVbdef4()
									+ "");// 规格
							fkjson.put("curr_num", conWorkVO.getNnum() + "");// 数量
							fkjson.put("nprom_price", conWorkVO.getNgtaxprice()
									+ "");// 单价
							fkjson.put("curr_mny", conWorkVO.getNtaxmny() + "");// 金额
							itemArr.put(fkjson);
						}
					}
					// 合同条款

					// 附件信息
					String filesql = "select distinct fh.name,fp.filelength,fp.filedesc,fh.createtime,fp.pk_doc from sm_pub_filesystem fp left join bap_fs_header fh on fp.pk_doc=fh.path"
							+ " left join bap_fs_body fb on fh.GUID = fb.headid"
							+ " where filepath like '" + pk_fct_ar + "/%'";// 完整正确的sql语句
					List<Object[]> filels = getDao.query(filesql);
					if (filels != null && filels.size() > 0) {
						for (int i = 0; i < filels.size(); i++) {
							JSONObject filejson = new JSONObject();
							filejson.put("filename", filels.get(i)[0] + "");
							filejson.put("filelength", filels.get(i)[1] + "");
							filejson.put("filedesc", filels.get(i)[2] + "");
							filejson.put("createtime", filels.get(i)[3] + "");
							String urlstring = FileStorageClient
									.getInstance()
									.getDownloadURL(null, filels.get(i)[4] + "");
							Logger.error("附件：" + urlstring);
							// byte data[] = urlTobyte(urlstring);
							// filejson.put("filedt", data);
							filejson.put("fileurl", urlstring);
							fileArr.put(filejson);
						}
					}
				}
			} else {
				System.out.println("未获取到收款合同！");
				Logger.error("未获取到收款合同！");
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		json.put("htinfo", htJson);// 合同信息
		json.put("mxinfo", itemArr);// 合同基本
		json.put("tkinfo", httkArr);// 合同条款
		json.put("fjinfo", fileArr);// 合同附件
		return json.toString();
	}

	// // 附件预览
	// @RequestMapping(value = "/FilePrew", method = RequestMethod.POST)
	// @ResponseBody
	// public void FilePrew(HttpServletRequest req, HttpServletResponse
	// response)
	// throws Exception {
	// response.setContentType("text/html");
	// response.setCharacterEncoding("UTF-8");
	// String urlstring = req.getParameter("urlstring");// 附件路径
	// String extName = req.getParameter("filename");// 附件名称
	// extName = new String(extName.getBytes("ISO8859-1"), "UTF-8");
	// // String filelength = req.getParameter("filelength");// 附件长度
	// BufferedOutputStream bos = null;
	// FileOutputStream fos = null;
	// File file = null;
	// try {
	// byte data[] = urlTobyte(urlstring);
	// Logger.error("附件名称：" + extName);
	// // 设置下载文件名等
	// /*
	// * File dirf = new File(RuntimeEnv.getNCHome() + "\\tempf\\"); if
	// * (!dirf.exists() && dirf.isDirectory()) {// 判断文件目录是否存在
	// * dirf.mkdirs(); } File dir = new File(RuntimeEnv.getNCHome() +
	// * "\\tempf\\demo\\"); if (!dir.exists() && dir.isDirectory()) {//
	// * 判断文件目录是否存在 dir.mkdirs(); } deleteFile(dirf); deleteFile(dir);
	// * file = new File(RuntimeEnv.getNCHome() + "\\tempf\\demo\\" +
	// * extName); fos = new FileOutputStream(file); bos = new
	// * BufferedOutputStream(fos); bos.write(data);
	// */
	// // ===========
	// response.getWriter().write(extName);
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// if (bos != null) {
	// try {
	// bos.close();
	// } catch (IOException e1) {
	// e1.printStackTrace();
	// }
	// }
	// if (fos != null) {
	// try {
	// fos.close();
	// } catch (IOException e1) {
	// e1.printStackTrace();
	// }
	// }
	// }
	// }

	/**
	 * 删除文件夹下的所有文件
	 * 
	 * @param oldPath
	 */
	public void deleteFile(File oldPath) {
		if (oldPath.isDirectory()) {
			System.out.println(oldPath + "是文件夹--");
			File[] files = oldPath.listFiles();
			for (File file : files) {
				deleteFile(file);
			}
		} else {
			oldPath.delete();
		}
	}

	// 取自定义档案值
	public String getDefdocName(String pk_defdoc) throws DAOException {
		String name = "";
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		String sql = "select name from bd_defdoc where pk_defdoc = '"
				+ pk_defdoc + "' and nvl(dr,0) = 0 ";// 自定义档案
		List<Object[]> zdyls = getDao.query(sql);
		if (zdyls != null && zdyls.size() > 0 && zdyls.get(0) != null) {
			name = zdyls.get(0)[0] + "";
		}
		return name;
	}

	// url转byte[]
	public static byte[] urlTobyte(String url) throws MalformedURLException {
		URL ur = new URL(url);
		BufferedInputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			in = new BufferedInputStream(ur.openStream());
			out = new ByteArrayOutputStream(1024);
			byte[] temp = new byte[1024];
			int size = 0;
			while ((size = in.read(temp)) != -1) {
				out.write(temp, 0, size);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		byte[] content = out.toByteArray();
		return content;
	}

	// XBX获取合同附件信息
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getFileContract", method = RequestMethod.POST)
	@ResponseBody
	public void getFileContract(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		String fileurl = "";
		String filepath = req.getParameter("filepath");// filepath
		filepath = new String(filepath.getBytes("ISO8859-1"), "UTF-8");
		System.out.println("filepath==" + filepath);
		Logger.error("filepath==" + filepath);
		String sql = "select pk_doc from sm_pub_filesystem where filepath = '"
				+ filepath + "' ";
		IUAPQueryBS bs = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		List<Object> listUU = (List<Object>) bs.executeQuery(sql.toString(),
				new ArrayListProcessor());
		if (listUU != null && listUU.size() > 0) {
			Object[] Obj = (Object[]) listUU.get(0);
			// fileurl = (String) Obj[0];
			fileurl = FileStorageClient.getInstance().getDownloadURL(null,
					(String) Obj[0]);
			// resp.getWriter().write((String) Obj[0]);
		}
		resp.getWriter().write(fileurl.toString());
	}

	// XBX新增影像补扫角色验证
	@RequestMapping(value = "/getI8UserInfo", method = RequestMethod.POST)
	public @ResponseBody
	void getI8UserInfo(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		String pk_user = InvocationInfoProxy.getInstance().getUserId();
		Logger.error("当前登录用户：" + pk_user);
		String cuserid = req.getParameter("cuserid");// 制单人主键
		String querySql = "SELECT COUNT(YHJS.CUSERID) AS SL FROM SM_USER_ROLE YHJS LEFT JOIN SM_ROLE JS ON YHJS.PK_ROLE = JS.PK_ROLE WHERE JS.ROLE_CODE = 'I8WB' AND YHJS.CUSERID = '"
				+ pk_user + "'";
		List<Object[]> bxls = getDao.query(querySql);
		if (bxls != null && Integer.parseInt(bxls.get(0)[0] + "") > 0) {
			resp.getWriter().write("Y");
		} else {
			resp.getWriter().write("N");
		}

	}

	// XBX驳回调用I8接口并删除单据
	@RequestMapping(value = "/I8Reject", method = RequestMethod.POST)
	@ResponseBody
	public void I8Reject(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		String tradetype = req.getParameter("tradetype");// 单据类型
		String billid = req.getParameter("billid");// 单据主键
		String reason = req.getParameter("reason");// 驳回原因
		reason = new String(reason.getBytes("ISO8859-1"), "UTF-8");
		Logger.error("billid：" + billid);
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		JSONObject json = new JSONObject();
		// String sql = ""; // 更新驳回原因
		Logger.error("tradetype：" + tradetype);
		Logger.error("reason：" + reason);
		if ("264".equals(tradetype)) {// 报销单
			// sql =
			// "UPDATE ER_BXZB SET ZYX = '"+reason+"' WHERE PK_JKBX = '"+billid+"' AND DR = 0 ";
			try {
				String mes = NCLocator.getInstance()
						.lookup(IErmServiceForI8.class)
						.rollAggBillVO(billid, "264X", reason);
				if (!"".equals(mes)) {
					json.put("success", false);
					json.put("mes", mes);
				} else {
					json.put("success", true);
					json.put("mes", "Y");
				}
			} catch (Exception e) {
				json.put("success", false);
				json.put("mes", e.getMessage());
			}
		} else {// 应收单 应付 收款 付款
			try {
				Logger.error("tradetype：" + tradetype);
				String mes = NCLocator.getInstance()
						.lookup(IArapServiceForI8.class)
						.rollAggBillVO(billid, tradetype, reason);
				if (!"".equals(mes)) {
					json.put("success", false);
					json.put("mes", mes);
				} else {
					json.put("success", true);
					json.put("mes", "Y");
				}
			} catch (Exception e) {
				json.put("success", false);
				json.put("mes", e.getMessage());
			}
		}
		// getDao.updatevo(sql);

		resp.getWriter().write(json.toString());
	}

	// XBX驳回调用I8接口并删除单据
	@RequestMapping(value = "/OAReject", method = RequestMethod.POST)
	@ResponseBody
	public void OAReject(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		String tradetype = req.getParameter("tradetype");// 单据类型
		String billid = req.getParameter("billid");// 单据主键
		String reason = req.getParameter("reason");// 驳回原因
		reason = new String(reason.getBytes("ISO8859-1"), "UTF-8");
		Logger.error("billid：" + billid);
		JSONObject json = new JSONObject();
		Logger.error("tradetype：" + tradetype);
		Logger.error("reason：" + reason);
		try {
			String mes = NCLocator.getInstance().lookup(IErmServiceForI8.class)
					.rollAggBillVO(billid, "264X", reason);
			if (!"".equals(mes)) {
				json.put("success", false);
				json.put("mes", mes);
			} else {
				json.put("success", true);
				json.put("mes", "Y");
			}
		} catch (Exception e) {
			json.put("success", false);
			json.put("mes", e.getMessage());
		}
		resp.getWriter().write(json.toString());
	}

	// XBX调用东港税务接口打开
	@RequestMapping(value = "/DGITF", method = RequestMethod.POST)
	@ResponseBody
	public void DGITF(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		String tradetype = req.getParameter("tradetype");// 单据类型
		String billid = req.getParameter("billid");// 单据主键
		Logger.error("billid：" + billid);
		net.sf.json.JSONObject json = new net.sf.json.JSONObject();
		try {
			IArapForDGSWService util = (IArapForDGSWService) NCLocator
					.getInstance().lookup(IArapForDGSWService.class);
			json = util.genSWUrl(tradetype, billid);
		} catch (Exception e) {
			json.put("success", "N");
			json.put("errinfo", e.getMessage());
		}

		resp.getWriter().write(json.toString());
	}

	// 附件预览
	@RequestMapping(value = "/FilePrew", method = RequestMethod.POST)
	@ResponseBody
	public void FilePrew(HttpServletRequest req, HttpServletResponse response)
			throws Exception {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		String urlstring = req.getParameter("urlstring");// 附件路径
		String extName = req.getParameter("filename");// 附件名称
		extName = new String(extName.getBytes("ISO8859-1"), "UTF-8");
		com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
		json.put("urlstring", urlstring);
		json.put("filename", extName);
		com.alibaba.fastjson.JSONObject mes = NCLocator.getInstance()
				.lookup(IArapServiceForFilePreview.class).getYZViewUrl(json);
		response.getWriter().write(mes.toString());
	}

	// XBX驳回调用生产系统接口并删除单据
	@RequestMapping(value = "/ScReject", method = RequestMethod.POST)
	@ResponseBody
	public void ScReject(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		String billid = req.getParameter("billid");// 单据主键
		Logger.error("billid：" + billid);
		JSONObject json = new JSONObject();
		try {
			String mes = NCLocator.getInstance()
					.lookup(IArapServiceForSC.class)
					.rollAggBillVO(billid, null);
			if (StringUtils.isNotEmpty(mes)) {
				json.put("success", false);
				json.put("mes", mes);
			} else {
				json.put("success", true);
				json.put("mes", "Y");
			}
		} catch (Exception e) {
			json.put("success", false);
			json.put("mes", e.getMessage());
		}
		resp.getWriter().write(json.toString());
	}
}