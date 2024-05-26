package nc.imag.scan.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pf.pub.PfDataCache;
import nc.imag.itf.IImagUtil;
import nc.imag.itf.service.IImageService;
import nc.imag.pub.uitl.ImagBasePubUtil;
import nc.imag.pub.util.ImageLogger;
import nc.imag.pub.util.ImagePubUtil;
import nc.imag.pub.util.ImageServiceUtil;
import nc.imag.scan.service.ScanFieldConvertService;
import nc.imag.util.ws.ImageFactoryConfigUtil;
import nc.itf.uap.rbac.IUserManageQuery;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.BaseProcessor;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.uap.cpb.log.CpLogger;
import nc.ui.pcm.utils.GetDao;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.sm.UserVO;
import nc.web.datatrans.itf.ITranslateDataService;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import uap.iweb.action.BaseController;
import uap.lfw.core.locator.ServiceLocator;
import uap.lfw.dbl.cpdocaggvo.itf.IMetaDataBaseAggVOQry;
import uap.lfw.dbl.vo.CpDocVO;
import uap.lfw.dbl.vo.MetaDataBaseAggVO;
import uap.web.util.Coder;

@SuppressWarnings({ "restriction", "unused", "unchecked", "rawtypes" })
@Controller
@RequestMapping({ "/image_ctr" })
public class ImageScanController extends BaseController {
	public ImageScanController() {
	}

	@RequestMapping(value = { "/imageupload" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public void imageupload(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject json = new JSONObject();
		// 角色下用户可使用
		try {
			String fg = "0";
			String user = InvocationInfoProxy.getInstance().getUserId();// 当前用户主键
			GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
			String querySql = "select c.cuserid, a.pk_role, a.role_name,c.user_name from sm_role a left join sm_user_role b  on a.pk_role=b.pk_role "
					+ " left join sm_user c on c.cuserid=b.cuserid  where a.role_code = 'YXSM'";
			List<Object[]> resultList = getDao.query(querySql);
			System.out.println("sql===" + querySql);
			if (resultList.size() > 0) {
				for (int i = 0; i < resultList.size(); i++) {
					Object[] item = resultList.get(i);
					if (user.equals(item[0])) {
						fg = "1";
					}
				}
			}
			System.out.println("当前登录用户主键：" + user);
			if (!"1".equals(fg)) {
				try {
					json.put("success", "false");
					json.put("message", "功能关闭，请使用影像上传");
					try {
						response.getWriter().write(json.toString());
						response.flushBuffer();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (JSONException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}else{
				try {
					if (!ImagBasePubUtil.imageIsEnabled()) {
						throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
								.getStrByID("1054003_0", "01054003-0040"));
					}
		
					boolean compression = "true".equals(request
							.getParameter("compression"));
					String compressType = request.getParameter("compressType");
					String checkscanway = request.getParameter("checkscanway");
					String jsnStr = request.getParameter("bill");
					if (compression) {
						jsnStr = Coder.decode(jsnStr, compressType);
					}
		
					String billtypeCode = request.getParameter("tradetype");
					String tradetypeCode = request.getParameter("tradetype");
		
					String userId = InvocationInfoProxy.getInstance().getUserId();
					String pk_group = request.getParameter("pk_group");
					if (pk_group == null) {
						pk_group = getUserVO().getPk_group();
					}
		
					Object dataVO = getDataVO(jsnStr, billtypeCode, pk_group);
					String factoryCode = ImageFactoryConfigUtil.factoryCode;
					String billpk_org = ImagePubUtil.getBillORG(dataVO, billtypeCode);
					if (factoryCode == null) {
						factoryCode = ImageServiceUtil.getImageFactoryCode(billpk_org);
					}
					if (factoryCode == null) {
						throw new BusinessException("NC断网,暂时无法进行影像扫描");
					}
					Map<String, String> fieldMap = null;
					if (factoryCode.equalsIgnoreCase("tchzt")) {
						ScanFieldConvertService service = new ScanFieldConvertService();
						fieldMap = service
								.getFieldMap(dataVO, billtypeCode, billpk_org);
					} else {
						fieldMap = ImageFactoryConfigUtil.getAttr2ValueMap(factoryCode,
								dataVO, billtypeCode);
					}
		
					String pkfield = ((IImageService) NCLocator.getInstance().lookup(
							IImageService.class)).getPKField(factoryCode);
					String billID = (String) fieldMap.get(pkfield);
					if (!factoryCode.equalsIgnoreCase("tchzt")) {
						int scantype = ImageServiceUtil.getImageScanType(billpk_org,
								billtypeCode);
		
						if (scantype == 0) {
							throw new BusinessException(NCLangRes4VoTransl
									.getNCLangRes().getStrByID("1054003_0",
											"01054003-0001"));
						}
		
						ImageServiceUtil.imageScanCheck(dataVO, billtypeCode, billID,
								userId, billpk_org);
						if ((!ImageServiceUtil
								.getIfScanInFlow(billpk_org, billtypeCode))
								&& ((scantype == 1) || (scantype == 2))) {
							((IImageService) NCLocator.getInstance().lookup(
									IImageService.class)).addScanTask(dataVO, scantype,
									billtypeCode, billpk_org);
						}
					}
		
					String ipAddr = ImageFactoryConfigUtil
							.getImageFactoryIP(factoryCode);
		
					String paraStr = "";
					if (factoryCode.equalsIgnoreCase("tchzt")) {
						String scanType = ((IImagUtil) NCLocator.getInstance().lookup(
								IImagUtil.class)).ifImagHasScaned(billID);
						paraStr = getScanParaStr(fieldMap, scanType);
					} else {
						paraStr = ImageServiceUtil.getImageScanURL(dataVO,
								billtypeCode, billID, userId, billpk_org);
					}
					json.put("success", "true");
					json.put("ipaddr", ipAddr);
					json.put("parastr", paraStr);
					json.put("factoryCode", factoryCode);
					return;
				} catch (Exception e) {
					ImageLogger.log(e.getMessage());
					Logger.error(e.getMessage(), e);
					try {
						json.put("success", "false");
						json.put("message", e.getMessage());
					} catch (Exception e1) {
						Logger.error(e.getMessage(), e);
					}
				} finally {
					response.setContentType("text/html");
					response.setCharacterEncoding("UTF-8");
					try {
						response.getWriter().write(json.toString());
						response.flushBuffer();
					} catch (Exception e) {
						ImageLogger.log(e.getMessage());
						Logger.error(e.getMessage(), e);
					}
				}
			}
		} catch (DAOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
//		try {
//			if (!ImagBasePubUtil.imageIsEnabled()) {
//				throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
//						.getStrByID("1054003_0", "01054003-0040"));
//			}
//
//			boolean compression = "true".equals(request
//					.getParameter("compression"));
//			String compressType = request.getParameter("compressType");
//			String checkscanway = request.getParameter("checkscanway");
//			String jsnStr = request.getParameter("bill");
//			if (compression) {
//				jsnStr = Coder.decode(jsnStr, compressType);
//			}
//
//			String billtypeCode = request.getParameter("tradetype");
//			String tradetypeCode = request.getParameter("tradetype");
//
//			String userId = InvocationInfoProxy.getInstance().getUserId();
//			String pk_group = request.getParameter("pk_group");
//			if (pk_group == null) {
//				pk_group = getUserVO().getPk_group();
//			}
//
//			Object dataVO = getDataVO(jsnStr, billtypeCode, pk_group);
//			String factoryCode = ImageFactoryConfigUtil.factoryCode;
//			String billpk_org = ImagePubUtil.getBillORG(dataVO, billtypeCode);
//			if (factoryCode == null) {
//				factoryCode = ImageServiceUtil.getImageFactoryCode(billpk_org);
//			}
//			if (factoryCode == null) {
//				throw new BusinessException("NC断网,暂时无法进行影像扫描");
//			}
//			Map<String, String> fieldMap = null;
//			if (factoryCode.equalsIgnoreCase("tchzt")) {
//				ScanFieldConvertService service = new ScanFieldConvertService();
//				fieldMap = service
//						.getFieldMap(dataVO, billtypeCode, billpk_org);
//			} else {
//				fieldMap = ImageFactoryConfigUtil.getAttr2ValueMap(factoryCode,
//						dataVO, billtypeCode);
//			}
//
//			String pkfield = ((IImageService) NCLocator.getInstance().lookup(
//					IImageService.class)).getPKField(factoryCode);
//			String billID = (String) fieldMap.get(pkfield);
//			if (!factoryCode.equalsIgnoreCase("tchzt")) {
//				int scantype = ImageServiceUtil.getImageScanType(billpk_org,
//						billtypeCode);
//
//				if (scantype == 0) {
//					throw new BusinessException(NCLangRes4VoTransl
//							.getNCLangRes().getStrByID("1054003_0",
//									"01054003-0001"));
//				}
//
//				ImageServiceUtil.imageScanCheck(dataVO, billtypeCode, billID,
//						userId, billpk_org);
//				if ((!ImageServiceUtil
//						.getIfScanInFlow(billpk_org, billtypeCode))
//						&& ((scantype == 1) || (scantype == 2))) {
//					((IImageService) NCLocator.getInstance().lookup(
//							IImageService.class)).addScanTask(dataVO, scantype,
//							billtypeCode, billpk_org);
//				}
//			}
//
//			String ipAddr = ImageFactoryConfigUtil
//					.getImageFactoryIP(factoryCode);
//
//			String paraStr = "";
//			if (factoryCode.equalsIgnoreCase("tchzt")) {
//				String scanType = ((IImagUtil) NCLocator.getInstance().lookup(
//						IImagUtil.class)).ifImagHasScaned(billID);
//				paraStr = getScanParaStr(fieldMap, scanType);
//			} else {
//				paraStr = ImageServiceUtil.getImageScanURL(dataVO,
//						billtypeCode, billID, userId, billpk_org);
//			}
//			json.put("success", "true");
//			json.put("ipaddr", ipAddr);
//			json.put("parastr", paraStr);
//			json.put("factoryCode", factoryCode);
//			return;
//		} catch (Exception e) {
//			ImageLogger.log(e.getMessage());
//			Logger.error(e.getMessage(), e);
//			try {
//				json.put("success", "false");
//				json.put("message", e.getMessage());
//			} catch (Exception e1) {
//				Logger.error(e.getMessage(), e);
//			}
//		} finally {
//			response.setContentType("text/html");
//			response.setCharacterEncoding("UTF-8");
//			try {
//				response.getWriter().write(json.toString());
//				response.flushBuffer();
//			} catch (Exception e) {
//				ImageLogger.log(e.getMessage());
//				Logger.error(e.getMessage(), e);
//			}
//		}
	}

	@RequestMapping(value = { "/imageshow" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public void imageshow(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject json = new JSONObject();
		try {
			if (!ImagBasePubUtil.imageIsEnabled()) {
				throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("1054003_0", "01054003-0041"));
			}

			boolean compression = "true".equals(request
					.getParameter("compression"));
			String compressType = request.getParameter("compressType");
			String permission = request.getParameter("permission");
			String jsnStr = request.getParameter("bill");
			if (compression) {
				jsnStr = Coder.decode(jsnStr, compressType);
			}

			String billtypeCode = request.getParameter("tradetype");
			String tradetypeCode = request.getParameter("tradetype");
			String pk_group = request.getParameter("pk_group");
			if (pk_group == null) {
				pk_group = getUserVO().getPk_group();
			}

			Object dataVO = getDataVO(jsnStr, billtypeCode, pk_group);
			String pk_org = ImagePubUtil.getBillORG(dataVO, billtypeCode);
			String factoryCode = ImageFactoryConfigUtil.factoryCode;
			if (factoryCode == null) {
				factoryCode = ImageServiceUtil.getImageFactoryCode(pk_org);
			}
			Map<String, String> fieldMap = null;
			if (factoryCode.equalsIgnoreCase("tchzt")) {
				ScanFieldConvertService service = new ScanFieldConvertService();
				fieldMap = service.getFieldMap(dataVO, billtypeCode, pk_org);
			} else {
				fieldMap = ImageFactoryConfigUtil.getAttr2ValueMap(factoryCode,
						dataVO, billtypeCode, pk_org);
			}
			String pkfield = ((IImageService) NCLocator.getInstance().lookup(
					IImageService.class)).getPKField(factoryCode);
			String billID = (String) fieldMap.get(pkfield);

			String ipAddr = ImageFactoryConfigUtil
					.getImageFactoryIP(factoryCode);

			String paraStr = "";
			if (factoryCode.equalsIgnoreCase("tchzt")) {
				paraStr = getShowParaStr(fieldMap);
			} else if (permission == null) {
				paraStr = ImageServiceUtil.getImageShowURL(dataVO,
						billtypeCode, billID, InvocationInfoProxy.getInstance()
								.getUserId(), pk_org);
			} else {
				paraStr = ImageServiceUtil.getImageShowURL(dataVO,
						billtypeCode, billID, InvocationInfoProxy.getInstance()
								.getUserId(), Integer.parseInt(permission),
						pk_org);
			}

			json.put("success", "true");
			json.put("ipaddr", ipAddr);
			json.put("parastr", paraStr);
			json.put("factoryCode", factoryCode);
			return;
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			try {
				json.put("success", "false");
				json.put("message", e.getMessage());
			} catch (Exception e1) {
				ImageLogger.log(e1.getMessage());
				Logger.error(e1.getMessage(), e1);
			}
		} finally {
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			try {
				response.getWriter().write(json.toString());
				response.flushBuffer();
			} catch (Exception e) {
				ImageLogger.log(e.getMessage());
				Logger.error(e.getMessage(), e);
			}
		}
	}

	@RequestMapping(value = { "/invoicebindimage" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public void invoicebindimage(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject json = new JSONObject();
		try {
			if (!ImagBasePubUtil.imageIsEnabled()) {
				throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("1054003_0", "01054003-0041"));
			}

			String YF_billtype = request.getParameter("YF_billtype");
			String YF_billid = request.getParameter("YF_billid");
			String FP_billtype = request.getParameter("FP_billtype");
			String FP_transtype = request.getParameter("FP_transtype");
			String FP_billid = request.getParameter("FP_billid");
			String FP_billno = request.getParameter("FP_billno");
			String FP_amount = request.getParameter("FP_amount");
			String FP_billdate = request.getParameter("FP_billdate");
			String FP_pkorg = request.getParameter("FP_pkorg");

			String userid = request.getParameter("userid");

			String url = ImageServiceUtil.invoiceBindImage(YF_billtype,
					YF_billid, FP_billtype, FP_transtype, FP_billid, FP_billno,
					FP_amount, FP_billdate, FP_pkorg, userid);

			json.put("success", "true");
			json.put("bindurl", url);
			return;
		} catch (Exception e) {
			ImageLogger.log(e.getMessage());
			Logger.error(e.getMessage(), e);
			try {
				json.put("success", "false");
				json.put("message", e.getMessage());
			} catch (Exception e1) {
				Logger.error(e.getMessage(), e);
			}
		} finally {
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			try {
				response.getWriter().write(json.toString());
				response.flushBuffer();
			} catch (Exception e) {
				ImageLogger.log(e.getMessage());
				Logger.error(e.getMessage(), e);
			}
		}
	}

	private String getScanParaStr(Map<String, String> fieldMap, String scanType)
			throws Exception {
		StringBuffer paraStr = new StringBuffer();

		paraStr.append("scanType=");
		paraStr.append(scanType);
		paraStr.append("&");
		for (String fieldName : fieldMap.keySet()) {
			paraStr.append(fieldName);
			paraStr.append("=");
			paraStr.append(URLEncoder.encode(
					String.valueOf(fieldMap.get(fieldName)), "UTF-8"));
			paraStr.append("&");
		}
		if (paraStr.toString().endsWith("&")) {
			paraStr.deleteCharAt(paraStr.length() - 1);
		}
		return paraStr.toString();
	}

	private String getShowParaStr(Map<String, String> fieldMap)
			throws Exception {
		StringBuffer paraStr = new StringBuffer();

		paraStr.append("userName=");
		paraStr.append(URLEncoder.encode(
				String.valueOf(getUserVO().getUser_name()), "UTF-8"));
		paraStr.append("&");
		for (String fieldName : fieldMap.keySet()) {
			paraStr.append(fieldName);
			paraStr.append("=");
			paraStr.append(URLEncoder.encode(
					String.valueOf(fieldMap.get(fieldName)), "UTF-8"));
			paraStr.append("&");
		}
		if (paraStr.toString().endsWith("&")) {
			paraStr.deleteCharAt(paraStr.length() - 1);
		}
		return paraStr.toString();
	}

	private UserVO getUserVO() throws BusinessException {
		String userId = InvocationInfoProxy.getInstance().getUserId();
		UserVO userVO = ((IUserManageQuery) NCLocator.getInstance().lookup(
				IUserManageQuery.class)).getUser(userId);

		return userVO;
	}

	private Object getDataVO(String jsnStr, String billtypeCode, String pk_group)
			throws BusinessException {
		Object dataVO = null;
		ScanFieldConvertService service = new ScanFieldConvertService();
		try {
			String voClassPath = service.getVOClassPath(billtypeCode, pk_group);
			if (voClassPath == null) {
				JSONObject json = new JSONObject(jsnStr);
				String pk_billtype = json.getJSONObject("body")
						.getJSONArray("bodys").getJSONObject(0)
						.getString("pk_billtype");
				voClassPath = service.getVOClassPath(pk_billtype, pk_group);
			}

			Class voClass = Class.forName(voClassPath);

			if (AggregatedValueObject.class.isAssignableFrom(voClass)) {
				dataVO = ((ITranslateDataService) NCLocator.getInstance()
						.lookup(ITranslateDataService.class))
						.translateJsonToAggvo(voClass, jsnStr);
			} else if (SuperVO.class.isAssignableFrom(voClass)) {
				dataVO = ((ITranslateDataService) NCLocator.getInstance()
						.lookup(ITranslateDataService.class))
						.transJsonToValueObject(voClass, jsnStr);
			}
			if ((dataVO instanceof MetaDataBaseAggVO)) {
				BilltypeVO billtypevo = PfDataCache.getBillType(billtypeCode);
				JSONObject json = new JSONObject(jsnStr);
				String billid = json.getJSONObject("head").getString(
						"openbillid");
				String billtypecode = billtypevo.getPk_billtypecode();
				String pk_doc = getPK_DOCbyBilltype(billtypecode);

				MetaDataBaseAggVO aggVo = ((IMetaDataBaseAggVOQry) ServiceLocator
						.getService(IMetaDataBaseAggVOQry.class))
						.getAggvosByPkAndPkDoc(billid, pk_doc);
				return aggVo;
			}
		} catch (Exception e) {
			ImageLogger.log(e.getMessage());
			Logger.error(e.getMessage(), e);
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("1054003_0", "01054003-0042"));

		} finally {

			if (dataVO == null) {
				throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("1054003_0", "01054003-0042"));
			}
		}

		return dataVO;
	}

	private String getPK_DOCbyBilltype(String billtypecode) {
		BilltypeVO billtypevo = PfDataCache.getBillType(billtypecode);
		String billtype = billtypevo.getParentbilltype();
		if (billtype != null) {
			billtypecode = billtype;
		}
		String sql = "select * from cp_doc where doc_code='" + billtypecode
				+ "'";
		BaseDAO dao = new BaseDAO();
		SQLParameter para = new SQLParameter();
		BaseProcessor baseProcessor = null;
		baseProcessor = new BeanListProcessor(CpDocVO.class);
		List<CpDocVO> voList = new ArrayList();
		try {
			voList = (List) dao.executeQuery(sql.toString(), para,
					baseProcessor);
		} catch (DAOException e) {
			CpLogger.error(e);
		}
		if ((voList != null) && (voList.size() > 0)) {
			return ((CpDocVO) voList.get(0)).getPk_doc();
		}
		return ((CpDocVO) voList.get(0)).getPk_doc();
	}
}
