package nc.itf.portal.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.bs.framework.adaptor.IHttpServletAdaptor;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.comn.NetStreamContext;
import nc.bs.framework.server.ISecurityTokenCallback;
import nc.bs.trade.business.HYPubBO;
import nc.itf.arap.pay.IArapPayBillQueryService;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.ui.pcm.utils.GetDao;
import nc.vo.arap.gathering.GatheringBillItemVO;
import nc.vo.arap.gathering.GatheringBillVO;
import nc.vo.arap.pay.AggPayBillVO;
import nc.vo.arap.pay.PayBillItemVO;
import nc.vo.arap.pay.PayBillVO;
import nc.vo.arap.payable.PayableBillItemVO;
import nc.vo.arap.payable.PayableBillVO;
import nc.vo.arap.receivable.ReceivableBillItemVO;
import nc.vo.arap.receivable.ReceivableBillVO;
import nc.vo.bd.cust.CustSupplierVO;
import nc.vo.bd.material.MaterialVO;
import nc.vo.bd.supplier.SupplierVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKHeaderVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.hrss.pub.FileNodeVO;
import nc.vo.org.FinanceOrgVO;
import nc.vo.org.OrgVO;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.sm.UserVO;
import nc.ws.intf.HttpClient;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import uap.pub.fs.client.FileStorageClient;

@SuppressWarnings({ "restriction", "unused", "rawtypes", "unchecked" })
public class GetZTPortalDataImpl implements IHttpServletAdaptor {
	private static String TableName;
	private HYPubBO hyPubBO;
	private static String YXURL = "http://172.18.128.94:2333/webShowImage/";

	@Override
	public void doAction(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// String ds = "RLJT";
		String ds = "design";
		InvocationInfoProxy.getInstance().setUserDataSource(ds);
		InvocationInfoProxy.getInstance().setGroupId("0001A1100000000001QS");// 设置默认的集团主键
		InvocationInfoProxy.getInstance().setUserCode("liuli");// 设置接口默认操作员编码
		InvocationInfoProxy.getInstance().setUserId("1001A110000000000HV8");// 设置接口默认操作员主键
		ISecurityTokenCallback tc = NCLocator.getInstance().lookup(
				ISecurityTokenCallback.class);
		byte[] token = tc.token("NCSystem".getBytes(), "pfxx".getBytes());
		NetStreamContext.setToken(token);
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charset=utf-8");
		req.setCharacterEncoding("utf-8");
		// 获取传输数据：合同主键（contractPk）
		// String billpk = req.getParameter("billpk");// 单据主键
		JSONObject jsonMain = new JSONObject();
		JSONObject json = new JSONObject();
		JSONArray headData = null;// 主表数据拼接
		JSONArray bodyData = null;// 主子表数据拼接
		String requestid = req.getParameter("requestid");// requestid

		String strWhere = " nvl(dr,0) = 0 and requestid = '" + requestid
				+ "' and def3 = 'ZT'";
		try {
			WorkFlowBill[] workFlowBills = (WorkFlowBill[]) getHyPubBO()
					.queryByCondition(WorkFlowBill.class, strWhere);
			if (null == workFlowBills || workFlowBills.length != 1) {
				resp.setStatus(500);
				PrintWriter writer = resp.getWriter();
				jsonMain.put("status", false);
				jsonMain.put("message", "获取流程单据失败，REQUESTID为：" + requestid);
				writer.write(jsonMain.toString());
			} else {
				WorkFlowBill workFlowBill = workFlowBills[0];
				String bill_type = workFlowBill.getBill_code() + "";// 单据类型
				String pk_bill = workFlowBill.getPk_bill() + "";// 单据主键
				if ("F0".equals(bill_type)) {// 应收单
					headData = getF0Main(pk_bill);// 获取主表数据
					bodyData = getF0Body(pk_bill);// 获取子表数据
				} else if ("F1".equals(bill_type)) {// 应付单
					headData = getF1Main(pk_bill);// 获取主表数据
					bodyData = getF1Body(pk_bill);// 获取子表数据
				} else if ("F2".equals(bill_type)) {// 收款单
					headData = getF2Main(pk_bill);// 获取主表数据
					bodyData = getF2Body(pk_bill);// 获取子表数据
				} else if ("F3".equals(bill_type)) {// 付款单
					headData = getF3Main(pk_bill);// 获取主表数据
					bodyData = getF3Body(pk_bill);// 获取子表数据
				} else if ("263X".equals(bill_type) || "264X".equals(bill_type)) {// 借款报销
					headData = getJKBXMain(pk_bill, bill_type);// 获取主表数据
					bodyData = getJKBXBody(pk_bill, bill_type);// 获取子表数据
				}
				if (headData == null) {
					resp.setStatus(500);
					PrintWriter writer = resp.getWriter();
					jsonMain.put("status", false);
					jsonMain.put("message", "REQUESTID[" + requestid
							+ "]获取单据数据报错！");
					writer.write(jsonMain.toString());
				} else {
					getFiles(headData, pk_bill);
					json.put("headData", headData);
					json.put("bodyData", bodyData);
					jsonMain.put("status", true);
					jsonMain.put("message", "");
					jsonMain.put("data", json);
					resp.setStatus(200);
					PrintWriter writer = resp.getWriter();
					writer.write(jsonMain.toString());
				}
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			resp.setStatus(500);
			PrintWriter writer = resp.getWriter();
			jsonMain.put("status", false);
			jsonMain.put("message", "获取流程单据失败，REQUESTID为：" + requestid + "报错："
					+ e.getMessage());
			writer.write(jsonMain.toString());
		}
	}

	// 获取F0应收单主表数据
	private JSONArray getF0Body(String pk_bill) throws BusinessException {
		// TODO Auto-generated method stub
		ReceivableBillItemVO[] bvo = (ReceivableBillItemVO[]) getHyPubBO()
				.queryByCondition(ReceivableBillItemVO.class,
						"nvl(dr,0) = 0 and pk_recbill = '" + pk_bill + "'");
		Map<String, Object> dtMap = new HashMap<String, Object>();
		List<Map<String, Object>> workflowRequestTableRecords = new ArrayList<Map<String, Object>>();
		dtMap.put("tableDBName", TableName + "_dt1");
		for (ReceivableBillItemVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			if (null != temp.getProject()) {
				// 项目
				ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
						.queryByPrimaryKey(ProjectHeadVO.class,
								temp.getProject());
				// 项目编码
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"project_code", projectVO.getProject_code()));
				// 项目名称
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"project_name", projectVO.getProject_name()));
			}
			if (null != temp.getMaterial()) {
				MaterialVO materialVO = (MaterialVO) getHyPubBO()
						.queryByPrimaryKey(MaterialVO.class, temp.getMaterial());
				// 物料编码
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"material_code", materialVO.getCode()));
				// 物料名称
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"material_name", materialVO.getName()));
				// 规格
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"materialspec", materialVO.getMaterialspec()));
				// 型号
				String str4 = "";
				if (null != materialVO.getMaterialtype()) {
					str4 = materialVO.getMaterialtype();
				}
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"materialtype", str4));
			}
			// 税码
			if (temp.getTaxcodeid() != null) {
				String sm = (String) getHyPubBO().findColValue(
						"bd_taxcode",
						"code",
						"nvl(dr,0) = 0 and pk_taxcode = '"
								+ temp.getTaxcodeid() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sm",
						sm));
			}
			// 税率
			if (temp.getDef17() != null) {
				String sl = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef17()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sl",
						sl));
			}
			// 结算方式
			if (temp.getPk_balatype() != null) {
				String jsfsbm = (String) getHyPubBO().findColValue(
						"bd_balatype",
						"code",
						"nvl(dr,0) = 0 and pk_balatype = '"
								+ temp.getPk_balatype() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"jsfsbm", jsfsbm));
				String jsfs = (String) getHyPubBO().findColValue(
						"bd_balatype",
						"name",
						"nvl(dr,0) = 0 and pk_balatype = '"
								+ temp.getPk_balatype() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"jsfs", jsfs));
			}
			// 申请部门
			if (temp.getPk_deptid() != null) {
				String fysqbm = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept = '" + temp.getPk_deptid()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"sqbm", fysqbm));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fsbm", fysqbm));
			}
			// 收支项目
			if (temp.getPk_subjcode() != null) {
				String szxm = (String) getHyPubBO().findColValue(
						"bd_inoutbusiclass",
						"name",
						"nvl(dr,0) = 0 and pk_inoutbusiclass  ='"
								+ temp.getPk_subjcode() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"szxm", szxm));
			}
			// 代控部门
			if (temp.getSo_deptid() != null) {
				/* 代控部门主键 */
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dkbmzj", temp.getSo_deptid()));
				/* 代控部门编码 */
				String dkbmbm = (String) getHyPubBO().findColValue(
						"org_dept",
						"code",
						"nvl(dr,0) = 0 and pk_dept = '" + temp.getSo_deptid()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dkbmbm", dkbmbm));
				/* 代控部门 */
				String dkbm = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept = '" + temp.getSo_deptid()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dkbm", dkbm));
			}

			// 其他字段------end
			Map<String, Object> workflowRequestTableFieldsMap = new HashMap<String, Object>();
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(dtMap, jsonConfig);
		return dtlistString;
	}

	// 获取F0应收单子表数据
	private JSONArray getF0Main(String pk_bill) throws BusinessException {
		ReceivableBillVO parentVO = (ReceivableBillVO) getHyPubBO()
				.queryByPrimaryKey(ReceivableBillVO.class, pk_bill);
		OAFlowVO oaVo = OaWorkFlowUtil
				.getOAFlowInfo(parentVO.getPk_org(), "F0");
		TableName = oaVo.getTablename();
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// ---------其他字段begin
		// 应收财务组织
		String stockName = (String) getHyPubBO().findColValue(
				"org_financeorg",
				"name",
				"nvl(dr,0) = 0 and pk_financeorg = '" + parentVO.getPk_org()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("zzmc", stockName));
		// 申请人
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		// 客户名称
		String custName = (String) getHyPubBO().findColValue(
				"bd_customer",
				"name",
				"nvl(dr,0) = 0 and pk_customer = '" + parentVO.getCustomer()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("custname", custName));
		// 部门
		String sqbmName = (String) getHyPubBO().findColValue(
				"org_dept_v",
				"name",
				"nvl(dr,0) = 0 and pk_vid = '" + parentVO.getPk_deptid_v()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("bm", sqbmName));
		// 申请部门
		String sqbm = (String) getHyPubBO()
				.findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept = '"
								+ parentVO.getPk_deptid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("sqbm", sqbm));
		// 责任部门
		String zrbm = (String) getHyPubBO().findColValue(
				"org_dept",
				"name",
				"nvl(dr,0) = 0 and pk_dept = '" + parentVO.getPk_deptid_res()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("zrbm", zrbm));
		// 申请人
		list.add(OaWorkFlowUtil.listAddObj("sqr", userVO.getUser_name()));
		// 制单人用户编码
		list.add(OaWorkFlowUtil.listAddObj("usercode", userVO.getUser_code()));
		// 制单人名称
		int flag = ifNTOrg(parentVO.getPk_org());
		if (flag == 0) {
			list.add(OaWorkFlowUtil.listAddObj("username",
					userVO.getUser_name()));
		}
		// 制单人身份证号
		String idCard = (String) getHyPubBO().findColValue("bd_psndoc", "id",
				"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
		if (null == idCard) {
			throw new BusinessException("制单人身份证号码未维护");
		}
		list.add(OaWorkFlowUtil.listAddObj("userid", idCard));
		// 代控部门
		if (parentVO.getSo_deptid() != null) {
			/* 代控部门主键 */
			list.add(OaWorkFlowUtil.listAddObj("dkbmzj",
					parentVO.getSo_deptid()));
			/* 代控部门编码 */
			String dkbmbm = (String) getHyPubBO().findColValue(
					"org_dept",
					"code",
					"nvl(dr,0) = 0 and pk_dept = '" + parentVO.getSo_deptid()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbmbm", dkbmbm));

			/* 代控部门 */
			String dkbm = (String) getHyPubBO().findColValue(
					"org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept = '" + parentVO.getSo_deptid()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbm", dkbm));
		}
		// 备注
		if (null != parentVO.getDef12()) {
			list.add(OaWorkFlowUtil.listAddObj("bz", parentVO.getDef12()));
		}
		if (parentVO.getScomment() != null) {
			list.add(OaWorkFlowUtil.listAddObj("scomment",
					parentVO.getScomment()));
		}
		// 合同现用名
		if (null != parentVO.getDef4()) {
			String htxym = (String) getHyPubBO().findColValue(
					"bd_supplier",
					"name",
					"nvl(dr,0) = 0 and pk_supplier = '" + parentVO.getDef4()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("htxym", htxym));
		}
		// 发票类型
		if (parentVO.getDef31() != null) {
			String fplx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getDef31()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("fplx", fplx));
		}
		// 合同类别
		if (parentVO.getDef80() != null) {
			list.add(OaWorkFlowUtil.listAddObj("htlb", parentVO.getDef80()));
		}
		list = getListMap(list, "fieldName");
		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;

	}

	// 获取F1应付单主表数据
	private JSONArray getF1Main(String pk_bill) throws BusinessException {
		// TODO Auto-generated method stub
		PayableBillVO parentVO = (PayableBillVO) getHyPubBO()
				.queryByPrimaryKey(PayableBillVO.class, pk_bill);
		OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(parentVO.getPk_org(),
				parentVO.getPk_tradetype());
		TableName = oaVo.getTablename();
		PayableBillItemVO[] bvos = (PayableBillItemVO[]) getHyPubBO()
				.queryByCondition(PayableBillItemVO.class,
						"nvl(dr,0) = 0 and pk_payablebill = '" + pk_bill + "'");
		String pk_supplier = ((bvos == null || bvos.length <= 0) ? null
				: bvos[0].getSupplier());
		String pu_deptid = ((bvos == null || bvos.length <= 0) ? null : bvos[0]
				.getPu_deptid());
		String pk_deptid = ((bvos == null || bvos.length <= 0) ? null : bvos[0]
				.getPk_deptid());
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// ---------其他字段begin
		String billtype = parentVO.getPk_tradetype();
		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				parentVO.getPk_org());
		// 组织
		String stockName = (String) getHyPubBO().findColValue(
				"org_purchaseorg",
				"name",
				"nvl(dr,0) = 0 and pk_purchaseorg = '" + parentVO.getPk_org()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("zzmc", stockName));
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		// 合同类型名称
		String billTypeName = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and pk_billtypeid = '"
						+ parentVO.getPk_tradetypeid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("typename", billTypeName));
		// 应付款类型
		if (parentVO.getDef57() != null) {
			String yfklxbm = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef57()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("yfklxbm", yfklxbm));
			String yfklx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef57()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("yfklx", yfklx));
		}

		// 部门
		if (null != parentVO.getPk_deptid_v()) {
			String sqbmName = (String) getHyPubBO().findColValue(
					"org_dept_v",
					"name",
					"nvl(dr,0) = 0 and pk_vid = '" + parentVO.getPk_deptid_v()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("bm", sqbmName));
		}

		// 费用申请部门
		if (null != parentVO.getPu_deptid()) {
			String fysqbm = (String) getHyPubBO().findColValue(
					"org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept = '" + parentVO.getPu_deptid()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("fysqbm", fysqbm));
		}

		// 申请人
		if (null != userVO.getUser_name()) {
			list.add(OaWorkFlowUtil.listAddObj("sqr", userVO.getUser_name()));
		}

		// 供应商
		if (null != pk_supplier) {
			SupplierVO supplierVO = (SupplierVO) getHyPubBO()
					.queryByPrimaryKey(SupplierVO.class, pk_supplier);
			list.add(OaWorkFlowUtil.listAddObj("pk_supplier_name",
					supplierVO.getName()));
		}

		// 制单人用户编码
		if (null != userVO.getUser_name()) {
			list.add(OaWorkFlowUtil.listAddObj("usercode",
					userVO.getUser_code()));
		}
		// 制单人名称
		int flag = ifNTOrg(parentVO.getPk_org());
		if (flag == 0) {
			list.add(OaWorkFlowUtil.listAddObj("username",
					userVO.getUser_name()));
		}
		// 制单人身份证号
		String idCard = (String) getHyPubBO().findColValue("bd_psndoc", "id",
				"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
		if (null == idCard) {
			throw new BusinessException("制单人身份证号码未维护");
		}
		list.add(OaWorkFlowUtil.listAddObj("userid", idCard));
		list.add(OaWorkFlowUtil.listAddObj("zdrzj", userVO.getUser_code()));
		// 供应商
		if (pk_supplier != null && !"".equals(pk_supplier)) {
			CustSupplierVO custVO = (CustSupplierVO) getHyPubBO()
					.queryByPrimaryKey(CustSupplierVO.class, pk_supplier);
			// 供应商编码
			list.add(OaWorkFlowUtil.listAddObj("pk_supplier_code",
					custVO.getCode()));
			// 供应商名称
			list.add(OaWorkFlowUtil.listAddObj("pk_supplier_name",
					custVO.getName()));
		}
		// 交易类型名称
		if (null != parentVO.getPk_tradetypeid()) {
			String typeName = (String) getHyPubBO().findColValue(
					"bd_billtype",
					"billtypename",
					"nvl(dr,0) = 0 and pk_billtypeid = '"
							+ parentVO.getPk_tradetypeid() + "'");
			list.add(OaWorkFlowUtil.listAddObj("typename", typeName));
		}
		// 来源系统名称
		Map<Integer, String> sourcesystypeMap = sourcesystypeMap();
		if (sourcesystypeMap.containsKey(parentVO.getSrc_syscode())) {
			list.add(OaWorkFlowUtil.listAddObj("sourcesystypename",
					sourcesystypeMap.get(parentVO.getSrc_syscode())));
		}
		// 备注
		if (null != parentVO.getDef12()) {
			list.add(OaWorkFlowUtil.listAddObj("bz", parentVO.getDef12()));
		}
		if (null != parentVO.getDef20()) {
			String fycdbm = (String) getHyPubBO().findColValue("org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getDef20() + "'");
			list.add(OaWorkFlowUtil.listAddObj("fycdbm", fycdbm));

		}
		if (null != parentVO.getScomment()) {
			list.add(OaWorkFlowUtil.listAddObj("scomment",
					parentVO.getScomment()));
		}
		/* 原OA合同流水号 */
		if (parentVO.getDef10() != null) {
			list.add(OaWorkFlowUtil.listAddObj("yoahtlsh", parentVO.getDef10()));
		}
		/* 发票类型（自定义档案） */
		if (null != parentVO.getDef31()) {
			String fplx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef31()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("fplxzdyda", fplx));
		}

		/* 凭证号 */
		if (null != parentVO.getDef79()) {
			list.add(OaWorkFlowUtil.listAddObj("pzh", parentVO.getDef79()));
		}

		if (null != orgVO) {
			/* 组织 */
			// list.add(OaWorkFlowUtil.listAddObj("szgs", orgVO.getName()));
		}
		/* 单据主键 */
		list.add(OaWorkFlowUtil.listAddObj("djzj", parentVO.getPk_payablebill()));
		/* 组织主键 */
		list.add(OaWorkFlowUtil.listAddObj("zzzj", parentVO.getPk_org()));
		/* 单据号 */
		list.add(OaWorkFlowUtil.listAddObj("djh", parentVO.getBillno()));
		/* 单据日期 */
		list.add(OaWorkFlowUtil.listAddObj("djrq", parentVO.getBilldate()
				.getYear()
				+ "-"
				+ parentVO.getBilldate().getStrMonth()
				+ "-"
				+ parentVO.getBilldate().getStrDay()));
		list.add(OaWorkFlowUtil.listAddObj("bmzj", parentVO.getPk_deptid()));
		/* 代控部门主键 */
		if ("D1".equals(billtype) || "F1-Cxx-01".equals(billtype)) {
			list.add(OaWorkFlowUtil.listAddObj("dkbmzj", pu_deptid));

			/* 代控部门编码 */
			String dkbmbmstr = (String) getHyPubBO().findColValue("org_dept",
					"code", "nvl(dr,0) = 0 and pk_dept = '" + pu_deptid + "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbmbm", dkbmbmstr));

			/* 代控部门 */
			String dname = (String) getHyPubBO().findColValue("org_dept",
					"name", "nvl(dr,0) = 0 and pk_dept = '" + pu_deptid + "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbm", dname));
			/* 工程类别 */
			if (null != parentVO.getDef75()) {
				String gclb = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef75()
								+ "'");
				list.add(OaWorkFlowUtil.listAddObj("gclb", gclb));
			}
		} else {
			list.add(OaWorkFlowUtil.listAddObj("dkbmzj", pk_deptid));

			/* 代控部门编码 */
			String dkbmbmstr = (String) getHyPubBO().findColValue("org_dept",
					"code", "nvl(dr,0) = 0 and pk_dept = '" + pk_deptid + "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbmbm", dkbmbmstr));

			/* 代控部门 */
			String dname = (String) getHyPubBO().findColValue("org_dept",
					"name", "nvl(dr,0) = 0 and pk_dept = '" + pk_deptid + "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbm", dname));

		}
		/* 合同名称 */
		if (null != parentVO.getDef36()) {
			list.add(OaWorkFlowUtil.listAddObj("htmc", parentVO.getDef36()));
		}
		/* 本次发票金额 */
		if (null != parentVO.getLocal_money()) {
			list.add(OaWorkFlowUtil.listAddObj("bckpje",
					parentVO.getLocal_money() + ""));
		}
		/*
		 * 合同名 if (null != parentVO.getDef4()) {
		 * list.add(OaWorkFlowUtil.listAddObj("htmc", parentVO.getDef4())); }
		 */
		/* 发票金额 */
		if (null != parentVO.getLocal_money()) {
			list.add(OaWorkFlowUtil.listAddObj("fpje",
					parentVO.getLocal_money() + ""));
		}
		/* 合同总金额 */
		if (null != parentVO.getDef70()) {
			list.add(OaWorkFlowUtil.listAddObj(
					"htzje",
					new UFDouble(parentVO.getDef70() == null ? "0.00"
							: parentVO.getDef70()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ ""));
		}
		// 合同现用名
		if (null != parentVO.getDef4()) {
			String htxym = (String) getHyPubBO().findColValue(
					"bd_supplier",
					"name",
					"nvl(dr,0) = 0 and pk_supplier  = '" + parentVO.getDef4()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("htxym", htxym));
		}
		/* 供应商 */
		if (null != parentVO.getSupplier()) {
			String name = (String) getHyPubBO().findColValue(
					"bd_supplier",
					"name",
					"nvl(dr,0) = 0 and  pk_supplier  = '"
							+ parentVO.getSupplier() + "'");
			list.add(OaWorkFlowUtil.listAddObj("gys", name));
			list.add(OaWorkFlowUtil.listAddObj("pk_supplier_name", name));
		}
		/* 挂账类型 */
		if (null != parentVO.getDef3()) {
			String gzlx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef3()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("gzlx", gzlx));
		}
		/* 合同号 */
		if (null != parentVO.getContractno()) {
			list.add(OaWorkFlowUtil.listAddObj("hth", parentVO.getContractno()));
		}
		/* 合同编码 */
		if (null != parentVO.getDef35()) {
			list.add(OaWorkFlowUtil.listAddObj("htbm", parentVO.getDef35()));
		}
		/* 金额大写 */
		/*
		 * if (null != parentVO.getLocal_money()) {
		 * list.add(OaWorkFlowUtil.listAddObj("jedx",
		 * ConvertUpMoney.toChinese(parentVO.getLocal_money() + ""))); }
		 */
		// 费用申请部门编码
		if (null != parentVO.getPu_deptid()) {
			String dk = (String) getHyPubBO().findColValue(
					"org_dept",
					"code",
					"nvl(dr,0) = 0 and pk_dept = '" + parentVO.getPu_deptid()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("fysqbmzj", dk));
		}
		/* 附件张数 */
		if (null != parentVO.getAccessorynum()) {
			list.add(OaWorkFlowUtil.listAddObj("fjzs",
					parentVO.getAccessorynum() + ""));
		}
		/* 发票说明 */
		if (null != parentVO.getDef2()) {
			list.add(OaWorkFlowUtil.listAddObj("fpsm", parentVO.getDef2()));
		}
		/* 申请单位 */
		if (null != parentVO.getPk_org()) {
			String sqdw = (String) getHyPubBO().findColValue(
					"org_financeorg",
					"name",
					"nvl(dr,0) = 0 and pk_financeorg = '"
							+ parentVO.getPk_org() + "'");
			list.add(OaWorkFlowUtil.listAddObj("sqdw", sqdw));
		}
		// 往来对象 3=业务员，2=部门，1=供应商
		if (null != parentVO.getObjtype()) {
			list.add(OaWorkFlowUtil.listAddObj("wldx", parentVO.getObjtype()
					+ ""));
			Integer wlduint = parentVO.getObjtype();
			String wldx = ""; // 往来对象
			if (wlduint == 1) {
				wldx = "供应商";
			} else if (wlduint == 2) {
				wldx = "部门";
			} else if (wlduint == 3) {
				wldx = "业务员";
			}
			list.add(OaWorkFlowUtil.listAddObj("wldxmc", wldx + ""));
		}
		/* 业务员 */
		if (null != parentVO.getPk_psndoc()) {
			String ywy = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and pk_psndoc  = '"
							+ parentVO.getPk_psndoc() + "'");
			list.add(OaWorkFlowUtil.listAddObj("ywy", ywy));
		}
		/* 已开发票金额 */
		if (null != parentVO.getDef72()) {
			list.add(OaWorkFlowUtil.listAddObj(
					"ykfpje",
					new UFDouble(parentVO.getDef72() == null ? "0.00"
							: parentVO.getDef72()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ ""));
		}
		/* 本次发票金额 */
		if (null != parentVO.getLocal_money()) {
			list.add(OaWorkFlowUtil.listAddObj("bckpje",
					parentVO.getLocal_money() + ""));
		}
		/* 累计开票金额 */
		if (null != parentVO.getDef30()) {
			if ("D1".equals(billtype) || "F1-Cxx-01".equals(billtype)) {
				list.add(OaWorkFlowUtil.listAddObj(
						"ljkpje",
						new UFDouble(parentVO.getDef30() == null ? "0.00"
								: parentVO.getDef30()).setScale(2,
								UFDouble.ROUND_HALF_UP)
								+ ""));
			}
		}
		/* 税率 */
		if (null != parentVO.getDef32()) {
			list.add(OaWorkFlowUtil.listAddObj("sl", parentVO.getDef32()));
		}

		/* 税额 */
		if (null != parentVO.getDef33()) {
			list.add(OaWorkFlowUtil.listAddObj(
					"se",
					new UFDouble(parentVO.getDef33() == null ? "0.00"
							: parentVO.getDef33()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ ""));
		}
		/* 税额变更后金额 */
		if (null != parentVO.getDef34()) {
			list.add(OaWorkFlowUtil.listAddObj(
					"sebghje",
					new UFDouble(parentVO.getDef34() == null ? "0.00"
							: parentVO.getDef34()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ ""));
		}

		/* 审计金额 */
		if (null != parentVO.getDef36()) {
			if ("D1".equals(billtype) || "F1-Cxx-01".equals(billtype)) {
				list.add(OaWorkFlowUtil.listAddObj(
						"sjje",
						new UFDouble(parentVO.getDef36() == null ? "0.00"
								: parentVO.getDef36()).setScale(2,
								UFDouble.ROUND_HALF_UP)
								+ ""));
			}
		}
		/* 付款类别 */
		if (null != parentVO.getDef23()) {
			String fklb = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef23()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("fklb", fklb));
		}
		/* 核算人员 */
		if (null != parentVO.getDef13()) {
			String hsry = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and pk_psndoc = '" + parentVO.getDef13()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("hsry", hsry));
		}
		list = getListMap(list, "fieldName");
		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;

	}

	// 获取F1应付单子表数据
	private JSONArray getF1Body(String pk_bill) throws BusinessException {
		Map dtMap = new HashMap();
		PayableBillVO hvo1 = (PayableBillVO) getHyPubBO().queryByPrimaryKey(
				PayableBillVO.class, pk_bill);
		String type = hvo1.getPk_tradetype();
		PayableBillItemVO[] bvo = (PayableBillItemVO[]) getHyPubBO()
				.queryByCondition(PayableBillItemVO.class,
						"nvl(dr,0) = 0 and pk_payablebill = '" + pk_bill + "'");
		List workflowRequestTableRecords = new ArrayList();
		dtMap.put("tableDBName", TableName + "_dt1");
		for (PayableBillItemVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			if (null != temp.getProject()) {
				// 项目
				ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
						.queryByPrimaryKey(ProjectHeadVO.class,
								temp.getProject());
				/* 项目 */
				String name = (String) getHyPubBO().findColValue(
						"bd_project",
						"project_name",
						"nvl(dr,0) = 0 and pk_project  = '" + temp.getProject()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("xm",
						name));
				// 项目编码
				String code = (String) getHyPubBO().findColValue(
						"bd_project",
						"project_code",
						"nvl(dr,0) = 0 and pk_project  = '" + temp.getProject()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"xmbm", code));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bproject_code", projectVO.getProject_code()));
				// 项目名称
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bproject_name", projectVO.getProject_name()));
			}
			if (null != temp.getMaterial()) {
				MaterialVO materialVO = (MaterialVO) getHyPubBO()
						.queryByPrimaryKey(MaterialVO.class, temp.getMaterial());
				/* 物料主键 */
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"wlzj", temp.getMaterial()));
				// 物料编码
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"material_code", materialVO.getCode()));
				// 物料名称
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"material_name", materialVO.getName()));
				// 规格
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"materialspec", materialVO.getMaterialspec()));
				// 型号
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"materialtype", materialVO.getMaterialtype()));
			}
			// 税码
			if (temp.getTaxcodeid() != null) {
				String sm = (String) getHyPubBO().findColValue(
						"bd_taxcode",
						"code",
						"nvl(dr,0) = 0 and pk_taxcode = '"
								+ temp.getTaxcodeid() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sm",
						sm));
			}
			// 结算方式
			if (temp.getPk_balatype() != null) {
				String jsfsbm = (String) getHyPubBO().findColValue(
						"bd_balatype",
						"code",
						"nvl(dr,0) = 0 and pk_balatype = '"
								+ temp.getPk_balatype() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"jsfsbm", jsfsbm));
				String jsfs = (String) getHyPubBO().findColValue(
						"bd_balatype",
						"name",
						"nvl(dr,0) = 0 and pk_balatype = '"
								+ temp.getPk_balatype() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"jsfs", jsfs));
			}
			// 费用申请部门
			if (temp.getPu_deptid() != null) {
				String fysqbm = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept = '" + temp.getPu_deptid()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fysqbm", fysqbm));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fyfsbm", fysqbm));
			}
			// 收支项目
			if (temp.getPk_subjcode() != null) {
				String szxm = (String) getHyPubBO().findColValue(
						"bd_inoutbusiclass",
						"name",
						"nvl(dr,0) = 0 and pk_inoutbusiclass  ='"
								+ temp.getPk_subjcode() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"szxm", szxm));
				/* 预算类别 */
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"yslb", szxm));
			}
			// 发票类型
			if (temp.getDef27() != null) {
				String fplx = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc ='" + temp.getDef27()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fplx", fplx));
			}

			/* 票据类型 */
			if (null != temp.getDef30()) {
				String pjlx = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc ='" + temp.getDef30()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"pjlx", pjlx));
			}

			/* 代控部门主键 */
			if ("D1".equals(type) || "F1-Cxx-01".equals(type)) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dkbmzj", temp.getPu_deptid()));
				/* 代控部门编码 */
				String dkbmstr = (String) getHyPubBO().findColValue(
						"org_dept",
						"code",
						"nvl(dr,0) = 0 and pk_dept = '" + temp.getPu_deptid()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dkbmbm", dkbmstr));
				/* 代控部门 */
				String dk = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept = '" + temp.getPu_deptid()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dkbm", dk));
				/* 费用申请部门 */
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fysqbm", dk));
			} else {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dkbmzj", temp.getPk_deptid()));
				/* 代控部门编码 */
				String dkbmstr = (String) getHyPubBO().findColValue(
						"org_dept",
						"code",
						"nvl(dr,0) = 0 and pk_dept = '" + temp.getPk_deptid()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dkbmbm", dkbmstr));
				/* 代控部门 */
				String dk = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept = '" + temp.getPk_deptid()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dkbm", dk));

			}

			/* 合同号 */
			if (null != temp.getContractno()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("hth",
						temp.getContractno()));
			}
			/* 发票号 */
			String str = "0";
			if (null != temp.getInvoiceno()) {
				str = temp.getInvoiceno();
			}
			workflowRequestTableFields.add(OaWorkFlowUtil
					.listAddObj("fph", str));
			/* 摘要 */
			if (null != temp.getScomment()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("zy",
						temp.getScomment()));
			}
			/* 往来对象3=业务员，2=部门，1=供应商 */
			if (null != temp.getObjtype()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"wldx", temp.getObjtype() + ""));
			}
			/* 发票金额 */
			if (null != temp.getLocal_money_cr()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fpje", temp.getLocal_money_cr() + ""));
			}
			/* 单价 */
			if (null != temp.getPrice()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("dj",
						temp.getPrice() + ""));
			}
			/* 含税单价 */
			if (null != temp.getTaxprice()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"hsdj", temp.getTaxprice() + ""));
			}
			/* 贷方数量 */
			if (null != temp.getTaxprice()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dfsl", temp.getQuantity_cr() + ""));
			}
			/* 不含税金额 */
			if (null != temp.getLocal_notax_cr() && temp.getDef15() == null) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bhsje", temp.getLocal_notax_cr() + ""));
			}

			/* 票据类型主键 */
			if (null != temp.getChecktype()) {
				String pjlxstr = (String) getHyPubBO().findColValue(
						"bd_notetype",
						"name",
						"nvl(dr,0) = 0 and pk_notetype  = '"
								+ temp.getChecktype() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"pjlxzj", temp.getChecktype()));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"pjlx", pjlxstr));
			}
			/* 仓库 */
			if (null != temp.getDef26()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("ck",
						temp.getDef26()));
			}
			/* 不可抵扣金额 */
			if (null != temp.getNosubtax()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bkdkje", temp.getNosubtax() + ""));
			}
			/* 物料 */
			if (null != temp.getDef13()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("wl",
						temp.getDef13()));
			}
			/* 规格类型 */
			if (null != temp.getDef50()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"gglx", temp.getDef50()));
			}
			/* 摘要 */
			if (null != temp.getDef22()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("zy",
						temp.getDef22()));
			}
			/* 含税金额 */
			if (null != temp.getLocal_money_cr()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"hsje", temp.getLocal_money_cr() + ""));
			}
			/* 不含税金额 */
			if (null != temp.getDef15()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bhsje", temp.getDef15()));
			}
			/* 换热站 */
			if (null != temp.getDef30()) {
				String hrz = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc ='" + temp.getDef30()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("hrz",
						hrz));
			}
			/* 费用名称 */
			if (null != temp.getDef13()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fymc", temp.getDef13()));
			}
			/* 资产类别 */
			if (temp.getDef23() != null) {
				String zclb = (String) getHyPubBO().findColValue(
						"tb_budgetsub", "objname",
						"nvl(dr,0) = 0 and pk_obj = '" + temp.getDef23() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"zclb", zclb));
			}
			// 材料应付单 工程应付单 其他应付 燃器具类应付 工程材料应付 燃气应付
			if ("D1".equals(type) || "F1-Cxx-01".equals(type)
					|| "F1-Cxx-RQ-01".equals(type)
					|| "F1-Cxx-RQ-02".equals(type)
					|| "F1-Cxx-RQ-03".equals(type)
					|| "F1-Cxx-RQ-04".equals(type)) {
				/* 税率 */
				if (temp.getTaxrate() != null) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"sl", temp.getTaxrate() + ""));
				}
				/* 税额 */
				if (temp.getLocal_tax_cr() != null) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"se", temp.getLocal_tax_cr() + ""));
				}
			} else {
				// 资产发票汇总报账 费用发票汇总报账 特殊事项发票汇总报账 水电燃及外购热发票汇总报账
				// 税率
				if (temp.getDef17() != null) {
					String sl = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc ='" + temp.getDef17()
									+ "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"sl", sl));
				}
				/* 税额 */
				if (temp.getDef19() != null) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"se", temp.getDef19()));
				}
			}
			workflowRequestTableFields = getListMap(workflowRequestTableFields,
					"fieldName");
			// 其他字段------end
			Map workflowRequestTableFieldsMap = new HashMap();
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(dtMap, jsonConfig);
		return dtlistString;
	}

	// 获取F2收款单主表数据
	private JSONArray getF2Main(String pk_bill) throws BusinessException {
		// TODO Auto-generated method stub
		GatheringBillVO parentVO = (GatheringBillVO) getHyPubBO()
				.queryByPrimaryKey(GatheringBillVO.class, pk_bill);
		OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(parentVO.getPk_org(),
				parentVO.getPk_tradetype());
		TableName = oaVo.getTablename();
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// ---------其他字段begin
		FinanceOrgVO orgVO = (FinanceOrgVO) getHyPubBO().queryByPrimaryKey(
				FinanceOrgVO.class, parentVO.getPk_org());
		list.add(OaWorkFlowUtil.listAddObj("zzmc", orgVO.getName()));
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		// 制单人用户编码
		list.add(OaWorkFlowUtil.listAddObj("usercode", userVO.getUser_code()));
		// 制单人名称
		int flag = ifNTOrg(parentVO.getPk_org());
		if (flag == 0) {
			list.add(OaWorkFlowUtil.listAddObj("username",
					userVO.getUser_name()));
		}
		// 制单人身份证号
		String idCard = (String) getHyPubBO().findColValue("bd_psndoc", "id",
				"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
		if (null == idCard) {
			throw new BusinessException("制单人身份证号码未维护");
		}
		list.add(OaWorkFlowUtil.listAddObj("userid", idCard));
		// 原能投字段START---
		// 单据号
		list.add(OaWorkFlowUtil.listAddObj("djh", parentVO.getBillno()));
		// 单据日期
		list.add(OaWorkFlowUtil.listAddObj("djrq", parentVO.getBilldate()
				.getYear()
				+ "-"
				+ parentVO.getBilldate().getStrMonth()
				+ "-"
				+ parentVO.getBilldate().getStrDay()));
		// 部门
		if (parentVO.getPk_deptid() != null) {
			String bm = (String) getHyPubBO().findColValue(
					"org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept = '" + parentVO.getPk_deptid()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("bm", bm));
		}
		// 客户
		if (parentVO.getCustomer() != null) {
			String bm = (String) getHyPubBO().findColValue(
					"bd_customer",
					"name",
					"nvl(dr,0) = 0 and pk_customer = '"
							+ parentVO.getCustomer() + "'");
			list.add(OaWorkFlowUtil.listAddObj("kh", bm));
		}
		// 原币金额
		if (parentVO.getMoney() != null) {
			list.add(OaWorkFlowUtil.listAddObj("ybje", parentVO.getMoney()
					.setScale(2, UFDouble.ROUND_HALF_UP) + ""));
		}
		// 组织本币金额
		if (parentVO.getLocal_money() != null) {
			list.add(OaWorkFlowUtil.listAddObj("zzbbje", parentVO
					.getLocal_money().setScale(2, UFDouble.ROUND_HALF_UP) + ""));
		}
		// 制单人
		list.add(OaWorkFlowUtil.listAddObj("zdr", userVO.getUser_name()));
		list = getListMap(list, "fieldName");
		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	// 获取F2收款单子表数据
	private JSONArray getF2Body(String pk_bill) throws BusinessException {
		// TODO Auto-generated method stub
		GatheringBillItemVO[] bvos = (GatheringBillItemVO[]) getHyPubBO()
				.queryByCondition(GatheringBillItemVO.class,
						"nvl(dr,0) = 0 and pk_gatherbill = '" + pk_bill + "'");

		Map<String, Object> dtMap = new HashMap<String, Object>();
		List<Map<String, Object>> workflowRequestTableRecords = new ArrayList<Map<String, Object>>();
		dtMap.put("tableDBName", TableName + "_dt1");
		for (GatheringBillItemVO temp : bvos) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			// 资金类别
			if (temp.getDef20() != null) {
				String zjlb = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef20()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"zjlb", zjlb));
			}
			// 收款业务类型
			if (temp.getDef19() != null) {
				String skywlx = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef19()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"skywlx", skywlx));
			}
			// 组织本币金额
			if (temp.getLocal_money_cr() != null) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"zzbbje",
						temp.getLocal_money_cr().setScale(2,
								UFDouble.ROUND_HALF_UP)
								+ ""));
			}
			// 客户
			if (temp.getCustomer() != null) {
				String kh = (String) getHyPubBO().findColValue(
						"bd_customer",
						"name",
						"nvl(dr,0) = 0 and pk_customer = '"
								+ temp.getCustomer() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("kh",
						kh));
			}
			workflowRequestTableFields = getListMap(workflowRequestTableFields,
					"fieldName");
			// 其他字段------end
			Map<String, Object> workflowRequestTableFieldsMap = new HashMap<String, Object>();
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(dtMap, jsonConfig);
		return dtlistString;
	}

	// 获取F3付款单主表数据
	private JSONArray getF3Main(String pk_bill) throws BusinessException {

		// TODO Auto-generated method stub
		IArapPayBillQueryService cs = (IArapPayBillQueryService) NCLocator
				.getInstance().lookup(IArapPayBillQueryService.class);
		AggPayBillVO[] wq = cs.queryBillsByWhereSQL(" PK_PAYBILL = '" + pk_bill
				+ "'");
		AggPayBillVO aggvo = wq[0];
		PayBillVO parentVO = (PayBillVO) aggvo.getParentVO();
		OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(parentVO.getPk_org(),
				parentVO.getPk_tradetype());
		TableName = oaVo.getTablename();
		PayBillItemVO[] mxVOs = (PayBillItemVO[]) aggvo.getChildrenVO();
		String recaccount = mxVOs[0].getRecaccount();
		String supplier = mxVOs[0].getSupplier();
		String pk_balatype = mxVOs[0].getPk_balatype();
		String payaccount = mxVOs[0].getPayaccount();
		String pu_deptid = mxVOs[0].getPu_deptid();
		String pk_dept = mxVOs[0].getPk_deptid();

		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// ---------其他字段begin
		// 付款方式
		list.add(OaWorkFlowUtil.listAddObj("local_money", parentVO
				.getLocal_money().toString()));
		list.add(OaWorkFlowUtil.listAddObj("yfje", parentVO.getLocal_money()
				.toString()));
		if (parentVO.getDef35() != null) {
			String fkfs = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef35()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("fkfs", fkfs));
		}
		String fyfsbmname = (String) getHyPubBO().findColValue("org_dept",
				"name", "nvl(dr,0) = 0 and pk_dept = '" + pu_deptid + "'");
		list.add(OaWorkFlowUtil.listAddObj("fyfsbm", fyfsbmname));
		FinanceOrgVO orgVO = (FinanceOrgVO) getHyPubBO().queryByPrimaryKey(
				FinanceOrgVO.class, parentVO.getPk_org());
		// 组织名称
		list.add(OaWorkFlowUtil.listAddObj("zzmc", orgVO.getName()));
		// list.add(OaWorkFlowUtil.listAddObj("szgs", orgVO.getName()));
		list.add(OaWorkFlowUtil.listAddObj("fkcwzz", orgVO.getName()));
		// 申请部门
		if (parentVO.getDef76() != null) {
			String bm = (String) getHyPubBO()
					.findColValue(
							"org_dept",
							"name",
							"nvl(dr,0) = 0 and pk_dept = '"
									+ parentVO.getDef76() + "'");
			list.add(OaWorkFlowUtil.listAddObj("sqbmsqbm", bm));
		}

		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getCreator());
		// 合同现用名
		if (null != parentVO.getDef4()) {
			String htxym = (String) getHyPubBO().findColValue(
					"bd_supplier",
					"name",
					"nvl(dr,0) = 0 and pk_supplier  = '" + parentVO.getDef4()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("htxymhtxym", htxym));
		}
		// 交易类型
		String billTypeName = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and pk_billtypeid  = '"
						+ parentVO.getPk_tradetypeid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("typename", billTypeName));
		/* 往来对象3=业务员，2=部门，1=供应商 */
		if (parentVO.getObjtype() != null) {
			int wldxint = parentVO.getObjtype();
			String wldx = "";
			if (wldxint == 1) {
				wldx = "供应商";
			} else if (wldxint == 2) {
				wldx = "部门";
			} else if (wldxint == 3) {
				wldx = "业务员";
			}
			list.add(OaWorkFlowUtil.listAddObj("wldx", wldx));
		}
		// 供应商
		if (supplier != null && !"".equals(supplier)) {
			String pk_supplier_name = (String) getHyPubBO().findColValue(
					"bd_supplier", "name",
					"nvl(dr,0) = 0 and pk_supplier = '" + supplier + "'");
			list.add(OaWorkFlowUtil.listAddObj("pk_supplier_name",
					pk_supplier_name));
			list.add(OaWorkFlowUtil.listAddObj("gys", pk_supplier_name));
		}
		// 收款银行账户
		String skyhzh = (String) getHyPubBO().findColValue("bd_bankaccsub",
				"accnum",
				"nvl(dr,0) = 0 and pk_bankaccsub = '" + recaccount + "'");
		list.add(OaWorkFlowUtil.listAddObj("skyhzh", skyhzh));
		list.add(OaWorkFlowUtil.listAddObj("skyxzh", skyhzh));
		// 部门
		String bm = (String) getHyPubBO().findColValue(
				"org_dept_v",
				"name",
				"nvl(dr,0) = 0 and pk_vid = '" + parentVO.getPk_deptid_v()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("bm", bm));
		// 代控部门
		if (parentVO.getDef20() != null) {
			list.add(OaWorkFlowUtil.listAddObj("dkbmzj", parentVO.getDef20()
					.toString()));
			String dkbmbm1 = (String) getHyPubBO().findColValue("org_dept",
					"code",
					"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getDef20() + "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbmbm", dkbmbm1));
			String dkbm1 = (String) getHyPubBO().findColValue("org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getDef20() + "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbm", dkbm1));
		} else {
			String dkbmbm = (String) getHyPubBO().findColValue("org_dept",
					"code", "nvl(dr,0) = 0 and pk_dept ='" + pk_dept + "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbmbm", dkbmbm));
			String dkbm = (String) getHyPubBO().findColValue("org_dept",
					"name", "nvl(dr,0) = 0 and pk_dept ='" + pk_dept + "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbm", dkbm));
			list.add(OaWorkFlowUtil.listAddObj("dkbmmc", dkbm));
		}
		// 业务员
		if (null != parentVO.getPk_psndoc()) {
			String ywy = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and pk_psndoc = '" + parentVO.getPk_psndoc()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("ywy", ywy));
		}
		// 付款银行账户
		String fkyhname = (String) getHyPubBO().findColValue("bd_bankaccsub",
				"accnum",
				"nvl(dr,0) = 0 and pk_bankaccsub = '" + payaccount + "'");
		list.add(OaWorkFlowUtil.listAddObj("fkyhzh", fkyhname));
		// 现金流量项目
		String xname = (String) getHyPubBO().findColValue(
				"bd_cashflow",
				"name",
				"nvl(dr,0) = 0 and pk_cashflow = '" + parentVO.getCashitem()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("xjllxm", xname));
		// 结算方式
		if (pk_balatype != null) {
			String jsfs = (String) getHyPubBO().findColValue("bd_balatype",

			"name", "nvl(dr,0) = 0 and pk_balatype = '" + pk_balatype + "'");

			list.add(OaWorkFlowUtil.listAddObj("jsfs", jsfs));
		}
		// 付款类别
		if (null != parentVO.getDef23()) {
			String fklb = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef23()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("fklb", fklb));
		}
		// 单据类型
		if (null != parentVO.getDef22()) {
			String djlx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef22()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("djlx", djlx));
		}
		// 资金类别
		if (parentVO.getDef77() != null) {
			String zjlb = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef77()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("zjlb", zjlb));
		}
		// 申请人
		list.add(OaWorkFlowUtil.listAddObj("sqr", userVO.getUser_name()));
		// ---固定字段
		// 制单人用户编码
		list.add(OaWorkFlowUtil.listAddObj("usercode", userVO.getUser_code()));
		// 制单人名称
		int flag = ifNTOrg(parentVO.getPk_org());
		if (flag == 0) {
			list.add(OaWorkFlowUtil.listAddObj("username",
					userVO.getUser_name()));
		}
		// 制单人身份证号
		String idCard = (String) getHyPubBO().findColValue("bd_psndoc", "id",
				"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
		if (null == idCard) {
			throw new BusinessException("制单人身份证号码未维护");
		}
		list.add(OaWorkFlowUtil.listAddObj("userid", idCard));
		// 能投代码
		/* 凭证号 */
		if (null != parentVO.getDef79()) {
			list.add(OaWorkFlowUtil.listAddObj("pzh", parentVO.getDef79()));
		}

		/* 支付日期 */
		if (null != parentVO.getDef33()) {
			list.add(OaWorkFlowUtil.listAddObj("zfrq", parentVO.getDef33()));
		}

		/* CBS状态 */
		if (null != parentVO.getDef34()) {
			list.add(OaWorkFlowUtil.listAddObj("cbszt", parentVO.getDef34()));
		}
		/* 付款单标识 */
		list.add(OaWorkFlowUtil.listAddObj("fkdbs", parentVO.getPk_paybill()));
		// 单据号
		list.add(OaWorkFlowUtil.listAddObj("djh", parentVO.getBillno()));
		// 单据日期
		list.add(OaWorkFlowUtil.listAddObj("djrq", parentVO.getBilldate() + ""));
		list.add(OaWorkFlowUtil.listAddObj("sqdw", orgVO.getName()));
		/* 转账银行 */
		if (null != parentVO.getDef24()) {
			String zzyh = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef24()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("zzyx", zzyh));
			list.add(OaWorkFlowUtil.listAddObj("zzyh", zzyh));
		}
		if ("F3-Cxx-RZHK".equals(parentVO.getPk_tradetype())) {

			if (null != parentVO.getDef3()) {
				String fklx = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc  ='" + parentVO.getDef3()
								+ "'");
				list.add(OaWorkFlowUtil.listAddObj("fklx", fklx));
			}
			/* 承兑银行 */
			if (null != parentVO.getDef25()) {
				String cdyh = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef25()
								+ "'");
				list.add(OaWorkFlowUtil.listAddObj("cdyx", cdyh));
				list.add(OaWorkFlowUtil.listAddObj("cdyh", cdyh));
			}
		} else if ("D3".equals(parentVO.getPk_tradetype())
				|| "F3-Cxx-01".equals(parentVO.getPk_tradetype())) {
			// 开户行
			list.add(OaWorkFlowUtil.listAddObj("khh",
					getGYSKHH(parentVO.getPk_tradetype(), recaccount)));
			/* 核算人员 */
			if (null != parentVO.getDef13()) {
				String hsry = (String) getHyPubBO().findColValue(
						"bd_psndoc",
						"name",
						"nvl(dr,0) = 0 and pk_psndoc  = '"
								+ parentVO.getDef13() + "'");
				list.add(OaWorkFlowUtil.listAddObj("hsry", hsry));
			}
			/* 付款类型 */
			if (null != parentVO.getPk_tradetypeid()) {
				String fkname = (String) getHyPubBO().findColValue(
						"bd_billtype",
						"billtypename",
						"nvl(dr,0) = 0 and pk_billtypeid  = '"
								+ parentVO.getPk_tradetypeid() + "'");
				list.add(OaWorkFlowUtil.listAddObj("fklx", fkname));
			}
			/* 承兑金额 */
			if (null != parentVO.getDef31()) {
				list.add(OaWorkFlowUtil.listAddObj(
						"cdje",
						new UFDouble(parentVO.getDef31() == null ? "0.00"
								: parentVO.getDef31()).setScale(2,
								UFDouble.ROUND_HALF_UP)
								+ ""));
			}
			/* 分管领导 */
			if (null != parentVO.getDef21()) {
				String fgld = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef21()
								+ "'");
				list.add(OaWorkFlowUtil.listAddObj("fgld", fgld));
			}
			/* 合同总金额 */
			if (null != parentVO.getDef70()) {
				list.add(OaWorkFlowUtil.listAddObj("htzje", parentVO.getDef70()));
			}

			/* 实付金额 */
			if (null != parentVO.getDef26()) {
				list.add(OaWorkFlowUtil.listAddObj("sfje", parentVO.getDef26()));
			}
			/* 本次付款比例 */
			if (null != parentVO.getDef37()) {
				list.add(OaWorkFlowUtil.listAddObj("bcfkbl",
						parentVO.getDef37()));
			}

			/* 已付款金额 */
			if (null != parentVO.getDef71()) {
				list.add(OaWorkFlowUtil.listAddObj("yfkje", parentVO.getDef71()));
			}
			/* 已付款比例 */
			if (null != parentVO.getDef38()) {
				list.add(OaWorkFlowUtil.listAddObj("yfkbl", parentVO.getDef38()));
			}
			/* 累计付款金额 */
			if (null != parentVO.getDef40()) {
				list.add(OaWorkFlowUtil.listAddObj("ljfkje",
						parentVO.getDef40()));
			}
			if (null != parentVO.getDef39()) {
				/* 累计付款比例 */
				list.add(OaWorkFlowUtil.listAddObj("ljfkbl",
						parentVO.getDef39()));
			}
			/* 已开发票金额 */
			if (null != parentVO.getDef72()) {
				list.add(OaWorkFlowUtil.listAddObj("ykfpje",
						parentVO.getDef72()));
			}
			/* 户号/编码 */
			if (null != parentVO.getDef69()) {
				list.add(OaWorkFlowUtil.listAddObj("hhbm", parentVO.getDef69()));
			}
			/* 倍率（电） */
			if (null != parentVO.getDef70()) {
				list.add(OaWorkFlowUtil.listAddObj("bl", parentVO.getDef70()));
			}
			/* 承兑银行 */
			if (null != parentVO.getDef25()) {
				String cdyh = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef25()
								+ "'");
				list.add(OaWorkFlowUtil.listAddObj("cdyh", cdyh));
				list.add(OaWorkFlowUtil.listAddObj("cdyx", cdyh));
			}
			/* 折让金额 */
			if (null != parentVO.getDef32()) {
				list.add(OaWorkFlowUtil.listAddObj("zrje", parentVO.getDef32()));
			}
			/* 付款事由 */
			if (null != parentVO.getDef30()) {
				list.add(OaWorkFlowUtil.listAddObj("fksy", parentVO.getDef30()));
			}
			/* 附件张数 */
			if (null != parentVO.getAccessorynum()) {
				list.add(OaWorkFlowUtil.listAddObj("fjzs",
						parentVO.getAccessorynum() + ""));
			}

			if ("D3".equals(parentVO.getPk_tradetype())) {
				/* 付款合同 */
				if (null != parentVO.getPk_contractno()) {
					list.add(OaWorkFlowUtil.listAddObj("fkht",
							parentVO.getPk_contractno()));
				}

				/* NC发票挂账金额 */
				if (null != parentVO.getDef42()) {
					list.add(OaWorkFlowUtil.listAddObj("ncfpgzje",
							parentVO.getDef42()));
				}
				/* NC累计付款金额 */
				if (null != parentVO.getDef43()) {
					list.add(OaWorkFlowUtil.listAddObj("ncljfkje",
							parentVO.getDef43()));
				}
				/* NC欠款金额 */
				if (null != parentVO.getDef44()) {
					list.add(OaWorkFlowUtil.listAddObj("ncqkje",
							parentVO.getDef44()));
				}
				// 是否原OA单据
				if (parentVO.getDef69() != null) {
					String sfyoadj = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc  = '"
									+ parentVO.getDef69() + "'");
					list.add(OaWorkFlowUtil.listAddObj("sfyoadj", sfyoadj));
				}
			} else {
				/* 审计金额 */
				if (null != parentVO.getDef41()) {
					list.add(OaWorkFlowUtil.listAddObj("sjje",
							parentVO.getDef41()));
				}
			}
			/* 工程类别 */
			if (null != parentVO.getDef75()) {
				String gclb = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef75()
								+ "'");
				list.add(OaWorkFlowUtil.listAddObj("gclb", gclb));
			}
		} else if ("F3-Cxx-FYFKSPD".equals(parentVO.getPk_tradetype())
				|| "F3-Cxx-SDRFKSPD".equals(parentVO.getPk_tradetype())
				|| "F3-Cxx-MTWGRFKSPD".equals(parentVO.getPk_tradetype())) {

			// 开户行
			list.add(OaWorkFlowUtil.listAddObj("khh",
					getGYSKHH(parentVO.getPk_tradetype(), recaccount)));
			/* 承兑银行 */
			if (null != parentVO.getDef25()) {
				String cdyh = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef25()
								+ "'");
				list.add(OaWorkFlowUtil.listAddObj("cdyx", cdyh));
				list.add(OaWorkFlowUtil.listAddObj("cdyh", cdyh));
			}
			/* 承兑金额 */
			if (null != parentVO.getDef27()) {
				list.add(OaWorkFlowUtil.listAddObj(
						"cdje",
						new UFDouble(parentVO.getDef27() == null ? "0.00"
								: parentVO.getDef27()).setScale(2,
								UFDouble.ROUND_HALF_UP)
								+ ""));
			}
			/* 付款事由 */
			if (null != parentVO.getDef2()) {
				list.add(OaWorkFlowUtil.listAddObj("fksy", parentVO.getDef2()));
			}
			// 费用付款单
			if ("F3-Cxx-FYFKSPD".equals(parentVO.getPk_tradetype())) {
				/* 实付金额 */
				if (null != parentVO.getDef26()) {
					list.add(OaWorkFlowUtil.listAddObj(
							"sfje",
							new UFDouble(parentVO.getDef26() == null ? "0.00"
									: parentVO.getDef26()).setScale(2,
									UFDouble.ROUND_HALF_UP)
									+ ""));
				}
				/* 折让金额 */
				if (null != parentVO.getDef31()) {
					list.add(OaWorkFlowUtil.listAddObj(
							"zrje",
							new UFDouble(parentVO.getDef31() == null ? "0.00"
									: parentVO.getDef31()).setScale(2,
									UFDouble.ROUND_HALF_UP)
									+ ""));
				}
				// 能投 费用付款审批单 新增收支项目 关联自定义档案
				if (parentVO.getDef80() != null) {
					/* 收支项目 */
					String szxm = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc = '"
									+ parentVO.getDef80() + "'");
					list.add(OaWorkFlowUtil.listAddObj("szxm", szxm + ""));
				}
				if (null != parentVO.getAccessorynum()) {
					list.add(OaWorkFlowUtil.listAddObj("fjzs",
							parentVO.getAccessorynum() + ""));
				}
			}
			// 水电燃付款单
			if ("F3-Cxx-SDRFKSPD".equals(parentVO.getPk_tradetype())) {
				/* 支付金额 */
				if (null != parentVO.getLocal_money()) {
					list.add(OaWorkFlowUtil.listAddObj("zfje",
							parentVO.getLocal_money() + ""));
				}
				/* 备注 */
				list.add(OaWorkFlowUtil.listAddObj("bz", parentVO.getDef2()));
				// 付款类型
				if (null != parentVO.getDef3()) {
					String fklx = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc = '"
									+ parentVO.getDef3() + "'");
					list.add(OaWorkFlowUtil.listAddObj("fklx", fklx));
				}
			}
			// 煤炭外热购
			if ("F3-Cxx-MTWGRFKSPD".equals(parentVO.getPk_tradetype())) {
				/* 合同名称 */
				if (null != parentVO.getDef80()) {
					list.add(OaWorkFlowUtil.listAddObj("htmc",
							parentVO.getDef80()));
				}
				/*
				 * 金额大写 Map jedx = OaWorkFlowUtil.listAddObj("jedx",
				 * ConvertUpMoney .toChinese(parentVO.getLocal_money() + ""));
				 * list.add(jedx);
				 */

				/* 合同编码 */
				if (null != parentVO.getDef3()) {
					list.add(OaWorkFlowUtil.listAddObj("htbm",
							parentVO.getDef3()));
				}
				/* 应付总金额 */
				if (null != parentVO.getDef7()) {
					list.add(OaWorkFlowUtil.listAddObj(
							"yfzje",
							new UFDouble(parentVO.getDef7() == null ? "0.00"
									: parentVO.getDef7()).setScale(2,
									UFDouble.ROUND_HALF_UP)
									+ ""));
				}

				/* 发票总金额 */
				if (null != parentVO.getDef8()) {
					list.add(OaWorkFlowUtil.listAddObj(
							"fpzje",
							new UFDouble(parentVO.getDef8() == null ? "0.00"
									: parentVO.getDef8()).setScale(2,
									UFDouble.ROUND_HALF_UP)
									+ ""));
				}

				/* 已付金额 */
				if (null != parentVO.getDef12()) {
					list.add(OaWorkFlowUtil.listAddObj(
							"yfje1",
							new UFDouble(parentVO.getDef12() == null ? "0.00"
									: parentVO.getDef12()).setScale(2,
									UFDouble.ROUND_HALF_UP)
									+ ""));
				}
				/* 已付款比例 */
				if (null != parentVO.getDef14()) {
					list.add(OaWorkFlowUtil.listAddObj("yfkbl",
							parentVO.getDef14()));
				}

				/* 转账金额 */
				if (null != parentVO.getDef26()) {
					list.add(OaWorkFlowUtil.listAddObj(
							"zzje",
							new UFDouble(parentVO.getDef26() == null ? "0.00"
									: parentVO.getDef26()).setScale(2,
									UFDouble.ROUND_HALF_UP)
									+ ""));
				}
				/* 支付金额 */
				if (null != parentVO.getLocal_money()) {
					list.add(OaWorkFlowUtil.listAddObj("zfje",
							parentVO.getLocal_money() + ""));
				}
				/* 附件张数 */
				if (null != parentVO.getAccessorynum()) {
					list.add(OaWorkFlowUtil.listAddObj("fjzs",
							parentVO.getAccessorynum() + ""));
				}
			}
		} else if ("F3-Cxx-GDZC".equals(parentVO.getPk_tradetype())) {
			// 开户行
			list.add(OaWorkFlowUtil.listAddObj("khh",
					getGYSKHH(parentVO.getPk_tradetype(), recaccount)));
			/* 承兑金额 */
			if (null != parentVO.getDef27()) {
				list.add(OaWorkFlowUtil.listAddObj(
						"cdje",
						new UFDouble(parentVO.getDef27() == null ? "0.00"
								: parentVO.getDef27()).setScale(2,
								UFDouble.ROUND_HALF_UP)
								+ ""));
			}

			/* 付款事由 */
			if (null != parentVO.getDef2()) {
				list.add(OaWorkFlowUtil.listAddObj("fksy", parentVO.getDef2()));
			}

			/* 折让金额 */
			if (null != parentVO.getDef31()) {
				list.add(OaWorkFlowUtil.listAddObj(
						"zrje",
						new UFDouble(parentVO.getDef31() == null ? "0.00"
								: parentVO.getDef31()).setScale(2,
								UFDouble.ROUND_HALF_UP)
								+ ""));
			}
			/* 实付金额 */
			if (null != parentVO.getDef26()) {
				list.add(OaWorkFlowUtil.listAddObj(
						"sfje",
						new UFDouble(parentVO.getDef26() == null ? "0.00"
								: parentVO.getDef26()).setScale(2,
								UFDouble.ROUND_HALF_UP)
								+ ""));
			}
			/* 承兑银行 */
			if (null != parentVO.getDef25()) {
				String cdyh = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef25()
								+ "'");
				list.add(OaWorkFlowUtil.listAddObj("cdyh", cdyh));
				list.add(OaWorkFlowUtil.listAddObj("cdyx", cdyh));
			}
			/* 附件张数 */
			if (null != parentVO.getAccessorynum()) {
				list.add(OaWorkFlowUtil.listAddObj("fjzs",
						parentVO.getAccessorynum() + ""));
			}
		} else if ("F3-Cxx-TSSXSPD".equals(parentVO.getPk_tradetype())) {
			// 承兑金额
			if (null != parentVO.getDef27()) {
				list.add(OaWorkFlowUtil.listAddObj(
						"cdje",
						new UFDouble(parentVO.getDef27() == null ? "0.00"
								: parentVO.getDef27()).setScale(2,
								UFDouble.ROUND_HALF_UP)
								+ ""));
			}
			/* 付款事由 */
			if (null != parentVO.getDef2()) {
				list.add(OaWorkFlowUtil.listAddObj("fksy", parentVO.getDef2()));
			}
			/* 折让金额 */
			if (null != parentVO.getDef31()) {
				list.add(OaWorkFlowUtil.listAddObj(
						"zrje",
						new UFDouble(parentVO.getDef31() == null ? "0.00"
								: parentVO.getDef31()).setScale(2,
								UFDouble.ROUND_HALF_UP)
								+ ""));
			}
			/* 实付金额 */
			if (null != parentVO.getDef26()) {
				list.add(OaWorkFlowUtil.listAddObj(
						"sfje",
						new UFDouble(parentVO.getDef26() == null ? "0.00"
								: parentVO.getDef26()).setScale(2,
								UFDouble.ROUND_HALF_UP)
								+ ""));
			}
			/* 承兑银行 */
			if (null != parentVO.getDef25()) {
				String cdyh = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef25()
								+ "'");
				list.add(OaWorkFlowUtil.listAddObj("cdyh", cdyh));
				list.add(OaWorkFlowUtil.listAddObj("cdyx", cdyh));
			}
			/* 附件张数 */
			if (null != parentVO.getAccessorynum()) {
				list.add(OaWorkFlowUtil.listAddObj("fjzs",
						parentVO.getAccessorynum() + ""));
			}
			// 付款类型
			if (null != parentVO.getDef55()) {
				String fklx = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef55()
								+ "'");
				list.add(OaWorkFlowUtil.listAddObj("fklx", fklx));
			}

		}
		// 财政性资金
		/*
		 * if (null != parentVO.getDef62()) { String czxzj = (String)
		 * getHyPubBO().findColValue( "bd_defdoc", "name",
		 * "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef62() + "'");
		 * list.add(OaWorkFlowUtil.listAddObj("czxzj", czxzj)); }
		 */
		// 资金性质
		if (null != parentVO.getDef63()) {
			String zjxz = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef63()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("zjxz", zjxz));
		}
		// 区县
		if (null != parentVO.getDef64()) {
			String qx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef64()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("qx", qx));
		}
		// 事由填写规范
		if (null != parentVO.getDef48()) {
			String sytxgf = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef48()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sytxgf", sytxgf));
		}
		// 业务板块
		if (null != parentVO.getDef49()) {
			String ywbk = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef49()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("ywbk", ywbk));
		}
		// ---------其他字段end
		// JSONArray arr = JSONArray.fromObject(list);
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	// 获取F3付款单子表数据
	private JSONArray getF3Body(String pk_bill) throws BusinessException {
		// TODO Auto-generated method stub
		PayBillItemVO[] bvos = (PayBillItemVO[]) getHyPubBO().queryByCondition(
				PayBillItemVO.class,
				"nvl(dr,0) = 0 and pk_paybill = '" + pk_bill + "'");
		PayBillVO parentVO = (PayBillVO) getHyPubBO().queryByPrimaryKey(
				PayBillVO.class, pk_bill);
		String type = parentVO.getPk_tradetype();
		Map dtMap = new HashMap();
		List workflowRequestTableRecords = new ArrayList();
		dtMap.put("tableDBName", TableName + "_dt1");
		for (PayBillItemVO temp : bvos) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			if (temp.getProject() != null && !"F3-Cxx-GDZC".equals(type)) {
				// 项目编码
				String project_code = (String) getHyPubBO().findColValue(
						"bd_project",
						"project_code",
						"nvl(dr,0) = 0 and pk_project = '" + temp.getProject()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"project_code", project_code));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bproject_code", project_code));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"xmbm", project_code));
				// 项目名称
				String project_name = (String) getHyPubBO().findColValue(
						"bd_project",
						"project_name",
						"nvl(dr,0) = 0 and pk_project = '" + temp.getProject()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"project_name", project_name));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bproject_name", project_name));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("xm",
						project_name));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"xmmc", project_name));
			}
			// 预算类别
			String yslbname = (String) getHyPubBO().findColValue(
					"bd_inoutbusiclass",
					"name",
					"nvl(dr,0) = 0 and pk_inoutbusiclass = '"
							+ temp.getPk_subjcode() + "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("yslb",
					yslbname));
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("szxm",
					yslbname));
			// 资金计划项目
			String zjjhxm = (String) getHyPubBO().findColValue(
					"bd_fundplan",
					"name",
					"nvl(dr,0) = 0 and pk_fundplan = '"
							+ temp.getBankrollprojet() + "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("zjjhxm",
					zjjhxm));
			// 供应商
			if (temp.getSupplier() != null) {
				String pk_supplier_name = (String) getHyPubBO().findColValue(
						"bd_supplier",
						"name",
						"nvl(dr,0) = 0 and pk_supplier = '"
								+ temp.getSupplier() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"skdw", pk_supplier_name));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("gys",
						pk_supplier_name));
			}
			// 收款银行账户
			if (temp.getRecaccount() != null) {
				String skyhzh = (String) getHyPubBO().findColValue(
						"bd_bankaccsub",
						"accnum",
						"nvl(dr,0) = 0 and pk_bankaccsub = '"
								+ temp.getRecaccount() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"skyhzh", skyhzh));
			}
			/* 换热站 */
			if (parentVO.getDef23() != null) {
				String hrz = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef23()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("hrz",
						hrz));
			}
			/* 线别 */
			if (parentVO.getDef71() != null) {
				String xb = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef71()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("xb",
						xb));
			}
			// 付款性质
			if (temp.getPrepay() != null) {
				int fkxzint = temp.getPrepay();
				String fkxz = "";
				if (fkxzint == 0) {
					fkxz = "应付款";
				} else if (fkxzint == 1) {
					fkxz = "预付款";
				}
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fkxz", fkxz));
			}
			// 票据类型
			if (null != temp.getDef30()) {
				String pjlx = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef30()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"pjlx", pjlx));
			}
			// 付款款银行账户
			if (temp.getPayaccount() != null) {
				String fkyhzh = (String) getHyPubBO().findColValue(
						"bd_bankaccsub",
						"accnum",
						"nvl(dr,0) = 0 and pk_bankaccsub = '"
								+ temp.getPayaccount() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fkyhzh", fkyhzh));
			}
			// 资金类别
			if (temp.getDef20() != null) {
				String zjlb = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef20()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"zjlb", zjlb));
			}
			// 现金流量项目
			if (temp.getCashitem() != null) {
				String xjllxm = (String) getHyPubBO().findColValue(
						"bd_cashflow",
						"name",
						"nvl(dr,0) = 0 and pk_cashflow  = '"
								+ temp.getCashitem() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"xjllxm", xjllxm));
			}
			// 费用发生部门
			if (temp.getPu_deptid() != null && !"F3-Cxx-TSSXSPD".equals(type)) {
				String fyfsbmname = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept = '" + temp.getPu_deptid()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fyfsbm", fyfsbmname));
				// 费用申请部门
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fysqbm", fyfsbmname));
			} else if (null != temp.getPk_deptid()) {
				String fyfsbmname = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and  pk_dept = '" + temp.getPk_deptid()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fyfsbm", fyfsbmname));
			}
			// 审计报告挂账
			if (temp.getDef14() != null) {
				String sjbggz = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef14()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"sjbggz", sjbggz));
			}
			// 合同号
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("hth",
					temp.getContractno()));
			// 合同金额
			if (temp.getDef1() != null) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"htje", temp.getDef1()));
			}
			// 本次付款金额
			/*workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
					"local_money_de", temp.getLocal_money_de() + ""));*/
			/* 本次付款金额 */
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bcfkje",
					temp.getLocal_money_de() + ""));
			/* 发票号 */
			if (null != temp.getInvoiceno()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("fph",
						temp.getInvoiceno()));
			}
			/* 发票类型 */
			if (null != temp.getDef27()) {
				String fplx = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef27()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fplx", fplx));
			}
			// 部门
			String bmname = (String) getHyPubBO()
					.findColValue(
							"org_dept",
							"name",
							"nvl(dr,0) = 0 and pk_dept = '"
									+ temp.getPk_deptid() + "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bm",
					bmname));
			// 已付金额
			if (temp.getDef3() != null) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"yfkje", temp.getDef3()));
			}
			// 总付款比例
			if (temp.getDef8() != null) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"zfkbl", temp.getDef8()));
			}
			// 不含税金额
			if (temp.getDef15() != null) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bhsje", temp.getDef15()));
			}
			// 折让金额
			if (temp.getDef28() != null) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"zrje", temp.getDef28()));
			}
			// 资金来源
			if (temp.getDef21() != null) {
				String zjly = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef21()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"zjly", zjly));
			}
			// 财政性资金
			if (null != temp.getDef49()) {
				String czxzj = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef49()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"czxzj", czxzj));
			}
			// 税率 税额
			if ("D3".equals(type) || "F3-Cxx-01".equals(type)) {
				/* 税率 */
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sl",
						temp.getDef4()));

				/* 税额 */
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("se",
						temp.getDef5()));
			} else {
				/* 税率 */
				if (null != temp.getDef17()) {
					String sl = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef17()
									+ "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"sl", sl));
				}
				/* 税额 */
				if (null != temp.getDef19()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"se", temp.getDef19()));
				}
				/* 备注 */
				if (null != temp.getDef13()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"bz", temp.getDef13()));
				}
				/* 项目档案 */
				if (temp.getDef24() != null) {
					String xmda = (String) getHyPubBO().findColValue(
							"bd_project",
							"project_name",
							"nvl(dr,0) = 0 and pk_project  = '"
									+ temp.getDef24() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"xmda", xmda));
				}
				/* 本次挂账金额35 */
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bcgzje", temp.getDef35()));

				/* 累计挂账金额36 */
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"ljgzje", temp.getDef36()));
			}
			// 其他字段------end
			Map workflowRequestTableFieldsMap = new HashMap();
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(dtMap, jsonConfig);
		return dtlistString;
	}

	// 获取263X || 264X 借款报销主表数据
	private JSONArray getJKBXMain(String pk_bill, String bill_type)
			throws BusinessException {
		// TODO Auto-generated method stub
		JKBXHeaderVO parentVO = null;
		if (StringUtils.equals("263X", bill_type)) {
			JKHeaderVO hVO = (JKHeaderVO) getHyPubBO().queryByPrimaryKey(
					JKHeaderVO.class, pk_bill);
			parentVO = hVO;
		} else if (StringUtils.equals("264X", bill_type)) {
			BXHeaderVO hVO = (BXHeaderVO) getHyPubBO().queryByPrimaryKey(
					BXHeaderVO.class, pk_bill);
			parentVO = hVO;
		}
		OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(parentVO.getPk_org(),
				parentVO.getDjlxbm());
		TableName = oaVo.getTablename();
		BXBusItemVO[] mxvos = (BXBusItemVO[]) getHyPubBO().queryByCondition(
				BXBusItemVO.class,
				"nvl(dr,0) = 0 and pk_jkbx='" + pk_bill + "'");
		String szxm = "";
		if (mxvos != null && mxvos.length > 0) {
			BXBusItemVO mxvo = mxvos[0];
			// 收支项目
			szxm = (String) getHyPubBO().findColValue(
					"bd_inoutbusiclass",
					"name",
					"nvl(dr,0) = 0 and pk_inoutbusiclass ='" + mxvo.getSzxmid()
							+ "'");
		}
		String billType = parentVO.getPk_billtype();

		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// ---------其他字段begin
		if (StringUtils.isNotEmpty(szxm)) {
			list.add(OaWorkFlowUtil.listAddObj("szxm", szxm));
		}
		// 组织名称
		String shortname = (String) getHyPubBO().findColValue(
				"org_purchaseorg",
				"name",
				"nvl(dr,0) = 0 and pk_purchaseorg ='" + parentVO.getPk_org()
						+ "'");
		if (shortname == null) {
			shortname = (String) getHyPubBO().findColValue(
					"org_financeorg",
					"name",
					"nvl(dr,0) = 0 and pk_financeorg ='" + parentVO.getPk_org()
							+ "'");
		}
		if (shortname == null) {
			shortname = (String) getHyPubBO().findColValue("org_orgs", "name",
					"nvl(dr,0) = 0 and pk_org ='" + parentVO.getPk_org() + "'");
		}
		list.add(OaWorkFlowUtil.listAddObj("zzmc", shortname));
		list.add(OaWorkFlowUtil.listAddObj("jkdw", shortname));
		list.add(OaWorkFlowUtil.listAddObj("bxdw", shortname));
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getCreator());
		// 交易类型
		String billTypeName = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and pk_billtypeid = '"
						+ parentVO.getPk_tradetypeid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("typename", billTypeName));
		// 填单人
		String tdr = (String) getHyPubBO().findColValue("sm_user", "user_name",
				"nvl(dr,0) = 0 and cuserid ='" + parentVO.getOperator() + "'");
		list.add(OaWorkFlowUtil.listAddObj("tdr", tdr));
		// 借款报销人
		String jkbxr = (String) getHyPubBO().findColValue("bd_psndoc", "name",
				"nvl(dr,0) = 0 and pk_psndoc ='" + parentVO.getJkbxr() + "'");
		list.add(OaWorkFlowUtil.listAddObj("jkbxr", jkbxr));
		// 收款银行账户
		if (null != parentVO.getSkyhzh()) {
			String skyhzh = (String) getHyPubBO().findColValue(
					"bd_bankaccsub",
					"accnum",
					"nvl(dr,0) = 0 and pk_bankaccsub ='" + parentVO.getSkyhzh()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("skyhzh", skyhzh));
			list.add(OaWorkFlowUtil.listAddObj("jkryhzh", skyhzh));
			list.add(OaWorkFlowUtil.listAddObj("jkryxzh", skyhzh));
			list.add(OaWorkFlowUtil.listAddObj("skzh", skyhzh));
			list.add(OaWorkFlowUtil.listAddObj("gryhzh", skyhzh));
			list.add(OaWorkFlowUtil.listAddObj("gryxzh", skyhzh));
		}
		/* 收款方开户行 */
		if (null != parentVO.getZyx9()) {
			String khh = (String) getHyPubBO().findColValue(
					"bd_asslinenum",
					"name",
					"nvl(dr,0) = 0 and pk_asslinenum ='" + parentVO.getZyx9()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("skfkhh", khh));
			list.add(OaWorkFlowUtil.listAddObj("skfkhx", khh));
		}
		// 单位银行账户
		if (parentVO.getFkyhzh() != null) {
			String dwyhzh = (String) getHyPubBO().findColValue(
					"bd_bankaccsub",
					"accnum",
					"nvl(dr,0) = 0 and pk_bankaccsub ='" + parentVO.getFkyhzh()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("dwyhzh", dwyhzh));
			list.add(OaWorkFlowUtil.listAddObj("fkyhzh", dwyhzh));

		}
		if (parentVO.getCashitem() != null) {
			// 现金流量项目
			String xjllxm = (String) getHyPubBO().findColValue(
					"bd_cashflow",
					"name",
					"nvl(dr,0) = 0 and pk_cashflow ='" + parentVO.getCashitem()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("xjllxm", xjllxm));
		}
		// 报销金额
		if (null != parentVO.getVat_amount()) {
			list.add(OaWorkFlowUtil.listAddObj("bxje", parentVO.getVat_amount()
					.setScale(2, UFDouble.ROUND_HALF_UP) + ""));
		}
		/* 冲借款金额 */
		if (null != parentVO.getCjkbbje()) {
			String cjkje = ((UFDouble) (parentVO.getCjkbbje() == null ? "0.00"
					: parentVO.getCjkbbje())).setScale(2,
					UFDouble.ROUND_HALF_UP)
					+ "";
			list.add(OaWorkFlowUtil.listAddObj("cjkje", cjkje));
		}
		// 支付金额
		if (null != parentVO.getZfbbje()) {
			list.add(OaWorkFlowUtil.listAddObj("zfje", parentVO.getZfbbje()
					.setScale(2, UFDouble.ROUND_HALF_UP) + ""));
		}
		// 附件张数
		if (null != parentVO.getZfbbje()) {
			list.add(OaWorkFlowUtil.listAddObj("fjzs", parentVO.getFjzs() + ""));
		}
		// 凭证号
		list.add(OaWorkFlowUtil.listAddObj("pzh", parentVO.getZyx18() + ""));
		// 收款对象
		if (parentVO.getPaytarget() != null) {
			String skdxstr = parentVO.getPaytarget() + "";
			int skdxint = Integer.parseInt(skdxstr);
			String skdx = "";
			if (skdxint == 0) {
				skdx = "员工";
			} else if (skdxint == 1) {
				skdx = "供应商";
			} else if (skdxint == 2) {
				skdx = "客户";
			}
			list.add(OaWorkFlowUtil.listAddObj("skdx", skdx));
		}
		// 客商银行账户
		if (parentVO.getCustaccount() != null) {
			String ksyhzh = (String) getHyPubBO().findColValue(
					"bd_bankaccsub",
					"accnum",
					"nvl(dr,0) = 0 and pk_bankaccsub ='"
							+ parentVO.getCustaccount() + "'");
			list.add(OaWorkFlowUtil.listAddObj("ksyhzh", ksyhzh));
		}
		// 申请人员
		if (parentVO.getJkbxr() != null) {
			String sqry = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and   pk_psndoc  ='" + parentVO.getJkbxr()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sqry", sqry));
			list.add(OaWorkFlowUtil.listAddObj("sqr", sqry));
		}
		// 固定资产项目
		if (parentVO.getZyx15() != null) {
			String gdzcxm = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and   pk_defdoc  ='" + parentVO.getZyx15()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("gdzcxm", gdzcxm));
		}
		// 项目明细
		if (parentVO.getZyx11() != null) {
			String xmmx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and   pk_defdoc  ='" + parentVO.getZyx11()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("xmmx", xmmx));
		}
		// 费用类型
		if (parentVO.getZyx15() != null) {
			String fylx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc  ='" + parentVO.getZyx15()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("fylx", fylx));
		}

		if (parentVO.getFydeptid() != null) {
			// 代控部门主键
			list.add(OaWorkFlowUtil.listAddObj("dkbmzj", parentVO.getFydeptid()));
			// 代控部门编码
			String dkbmbm = (String) getHyPubBO().findColValue(
					"org_dept",
					"code",
					"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getFydeptid()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbmbm", dkbmbm));
			// 代控部门名称
			String dkbm = (String) getHyPubBO().findColValue(
					"org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getFydeptid()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbm", dkbm));
		}
		if (parentVO.getZyx15() != null) {
			// 代控部门主键
			list.add(OaWorkFlowUtil.listAddObj("dkbmzj", parentVO.getZyx15()));
			// 代控部门编码
			String dkbmbm = (String) getHyPubBO().findColValue("org_dept",
					"code",
					"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getZyx15() + "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbmbm", dkbmbm));
			// 代控部门名称
			String dkbm = (String) getHyPubBO().findColValue("org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getZyx15() + "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbm", dkbm));
		}
		if (null != parentVO.getZyx14()) {
			String fklx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc  ='" + parentVO.getZyx14()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("fklx", fklx));
		}

		// 还款单 - 还款银行
		if (parentVO.getZyx24() != null) {
			String hkyh = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getZyx24()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("hkyh", hkyh));
			list.add(OaWorkFlowUtil.listAddObj("zzyx", hkyh));// 能投转账银行
			list.add(OaWorkFlowUtil.listAddObj("zzyh", hkyh));// 能投转账银行
		}
		// 借款人
		if (parentVO.getReceiver() != null) {
			String jkr = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and pk_psndoc ='" + parentVO.getReceiver()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("jkr", jkr));
			list.add(OaWorkFlowUtil.listAddObj("skr", jkr));
			list.add(OaWorkFlowUtil.listAddObj("hkr", jkr));
		}
		// 结算方式
		String jsfs = (String) getHyPubBO().findColValue("bd_balatype", "name",
				"nvl(dr,0) = 0 and pk_balatype ='" + parentVO.getJsfs() + "'");
		list.add(OaWorkFlowUtil.listAddObj("jsfs", jsfs));
		// 紧急
		if (parentVO.getIsexpedited() != null) {
			String jj = "否";
			if ("Y".equals(parentVO.getIsexpedited().toString())) {
				jj = "是";
			}
			list.add(OaWorkFlowUtil.listAddObj("jj", jj));
		}
		// 项目编号
		String project_code = (String) getHyPubBO().findColValue("bd_project",
				"project_code",
				"nvl(dr,0) = 0 and pk_project ='" + parentVO.getJobid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("project_code", project_code));
		// 项目名称
		String project_name = (String) getHyPubBO().findColValue("bd_project",
				"project_name",
				"nvl(dr,0) = 0 and pk_project ='" + parentVO.getJobid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("project_name", project_name));
		// 供应商名称
		if (null != parentVO.getHbbm()) {
			SupplierVO supplierVO = (SupplierVO) getHyPubBO()
					.queryByPrimaryKey(SupplierVO.class, parentVO.getHbbm());
			String name = supplierVO.getName();
			list.add(OaWorkFlowUtil.listAddObj("pk_supplier_name", name));
			list.add(OaWorkFlowUtil.listAddObj("skf", name));// 收款方
		}
		// 供应商银行账户
		String gysyhzh = (String) getHyPubBO().findColValue(
				"bd_bankaccsub",
				"accnum",
				"nvl(dr,0) = 0 and pk_bankaccsub ='"
						+ parentVO.getCustaccount() + "'");
		list.add(OaWorkFlowUtil.listAddObj("gysyhzh", gysyhzh));
		// 费用分摊
		if (parentVO.getIscostshare() != null) {
			String fyft = "否";
			if ("Y".equals(parentVO.getIscostshare().toString())) {
				fyft = "是";
			}
			list.add(OaWorkFlowUtil.listAddObj("fyft", fyft));
			list.add(OaWorkFlowUtil.listAddObj("ft", fyft));
		}
		// 支付方式
		if (parentVO.getZyx2() != null) {
			String zffs = (String) getHyPubBO().findColValue(
					"bd_balatype",
					"name",
					"nvl(dr,0) = 0 and pk_balatype ='" + parentVO.getZyx2()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("zffs", zffs));
		}
		// 申请类型
		if (parentVO.getZyx7() != null) {
			String sqlx = (String) getHyPubBO()
					.findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc ='"
									+ parentVO.getZyx7() + "'");
			list.add(OaWorkFlowUtil.listAddObj("sqlx", sqlx));
		}
		// ---固定字段
		// 制单人用户编码
		list.add(OaWorkFlowUtil.listAddObj("usercode", userVO.getUser_code()));
		// 制单人名称
		int flag = ifNTOrg(parentVO.getPk_org());
		if (flag == 0) {
			list.add(OaWorkFlowUtil.listAddObj("username",
					userVO.getUser_name()));
		}
		// 制单人身份证号
		String idCard = (String) getHyPubBO().findColValue("bd_psndoc", "id",
				"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
		if (null == idCard) {
			throw new BusinessException("制单人身份证号码未维护");
		}
		list.add(OaWorkFlowUtil.listAddObj("userid", idCard));
		// 原能投OA字段START
		if (null != userVO.getUser_name()) {
			list.add(OaWorkFlowUtil.listAddObj("zdrmc", userVO.getUser_name()));
		}
		// 身份证
		list.add(OaWorkFlowUtil.listAddObj("zdrzj", idCard));
		/* 单据号 */
		list.add(OaWorkFlowUtil.listAddObj("djh", parentVO.getDjbh()));
		/* 单据日期 */
		list.add(OaWorkFlowUtil.listAddObj("djrq", parentVO.getDjrq().getYear()
				+ "-" + parentVO.getDjrq().getStrMonth() + "-"
				+ parentVO.getDjrq().getStrDay()));
		/* 借款金额 */
		if (null != parentVO.getTotal()) {
			list.add(OaWorkFlowUtil.listAddObj("jkje", parentVO.getTotal()
					.setScale(2, UFDouble.ROUND_HALF_UP) + ""));
		}
		/* 借款金额大写 */
		/*
		 * if (null != parentVO.getTotal()) {
		 * list.add(OaWorkFlowUtil.listAddObj("jkjedx",
		 * ConvertUpMoney.toChinese(parentVO.getTotal().setScale(2,
		 * UFDouble.ROUND_HALF_UP) + ""))); }
		 */
		/* 凭证号 */
		if (null != parentVO.getZyx29()) {
			list.add(OaWorkFlowUtil.listAddObj("pzh", parentVO.getZyx29()));
		}
		/* 支付日期 */
		if (null != parentVO.getZyx21()) {
			list.add(OaWorkFlowUtil.listAddObj("zfrq", parentVO.getZyx21()));
		}
		/* CBS状态 */
		if (null != parentVO.getZyx22()) {
			list.add(OaWorkFlowUtil.listAddObj("cbszt", parentVO.getZyx22()));
		}
		/* 付款事由 */
		if (null != parentVO.getZyx30()) {
			list.add(OaWorkFlowUtil.listAddObj("fksy", parentVO.getZyx30()));
			list.add(OaWorkFlowUtil.listAddObj("ccsy", parentVO.getZyx30()));
			/* 退费说明 */
			list.add(OaWorkFlowUtil.listAddObj("tfsm", parentVO.getZyx30()));
			/* 还款事由 */
			list.add(OaWorkFlowUtil.listAddObj("hksy", parentVO.getZyx30()));
		}
		/* 退费金额大写 */
		/*
		 * if (null != parentVO.getVat_amount()) {
		 * list.add(OaWorkFlowUtil.listAddObj("tfjedx",
		 * ConvertUpMoney.toChinese(parentVO.getVat_amount() + ""))); }
		 */
		/* 附件张数 */
		if (null != parentVO.getFjzs()) {
			list.add(OaWorkFlowUtil.listAddObj("fjzs", parentVO.getFjzs() + ""));
		}
		if ("263X".equals(billType)) {// 借款单
			// 申请单位
			String sqdw = (String) getHyPubBO().findColValue(
					"org_adminorg",
					"name",
					"nvl(dr,0) = 0 and pk_adminorg ='" + parentVO.getDwbm()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sqdw", sqdw));
			list.add(OaWorkFlowUtil.listAddObj("sqrdw", sqdw));
			list.add(OaWorkFlowUtil.listAddObj("fycddw", sqdw));
			// 申请部门
			if (null != parentVO.getDeptid()) {
				String sqbm = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getDeptid()
								+ "'");
				list.add(OaWorkFlowUtil.listAddObj("sqbm", sqbm));
				list.add(OaWorkFlowUtil.listAddObj("sqrbm", sqbm));
				list.add(OaWorkFlowUtil.listAddObj("fycdbm", sqbm));
			}
			list.add(OaWorkFlowUtil.listAddObj("zj", parentVO.getDeptid()));
			list.add(OaWorkFlowUtil.listAddObj("total", parentVO.getTotal()
					.toString()));
		} else if ("264X".equals(billType)) {// 报销单
			// 申请单位
			String sqdw = (String) getHyPubBO().findColValue(
					"org_adminorg",
					"name",
					"nvl(dr,0) = 0 and pk_adminorg ='" + parentVO.getDwbm()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sqdw", sqdw));
			// 费用承担单位
			String fycddw = (String) getHyPubBO().findColValue("org_orgs",
					"name",
					"nvl(dr,0) = 0 and pk_org ='" + parentVO.getFydwbm() + "'");
			list.add(OaWorkFlowUtil.listAddObj("fycddw", fycddw));
			// 申请部门
			String sqbm = (String) getHyPubBO()
					.findColValue(
							"org_dept",
							"name",
							"nvl(dr,0) = 0 and pk_dept ='"
									+ parentVO.getDeptid() + "'");
			list.add(OaWorkFlowUtil.listAddObj("sqbm", sqbm));
			list.add(OaWorkFlowUtil.listAddObj("sqrbm", sqbm));
			// 费用承担部门
			String fycdbm = (String) getHyPubBO().findColValue(
					"org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getFydeptid()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("fycdbm", fycdbm));
			// 申请单号
			if (parentVO.getPk_item() != null) {
				String sqdh = (String) getHyPubBO().findColValue(
						"er_mtapp_bill",
						"billno",
						"nvl(dr,0) = 0 and pk_mtapp_bill ='"
								+ parentVO.getPk_item() + "'");
				list.add(OaWorkFlowUtil.listAddObj("sqdh", sqdh));
			}
			list.add(OaWorkFlowUtil.listAddObj("zj", parentVO.getFydeptid()));
			/* 退费所属部门 */
			if (parentVO.getZyx15() != null) {
				String bm = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getZyx15()
								+ "'");
				list.add(OaWorkFlowUtil.listAddObj("tfszbm", bm));
			}
			/* 退费金额 */
			if (null != parentVO.getVat_amount()) {
				list.add(OaWorkFlowUtil.listAddObj("tfje", parentVO
						.getVat_amount().setScale(2, UFDouble.ROUND_HALF_UP)
						+ ""));
			}
			/* 收款方银行账号 */
			if (null != parentVO.getZyx14()) {
				list.add(OaWorkFlowUtil.listAddObj("skfyxzh",
						parentVO.getZyx14()));
			}
		}
		// 员工借款单
		if ("263X-Cxx-YGJKD".equals(parentVO.getDjlxbm())) {
			list.add(OaWorkFlowUtil.listAddObj("jklx", "1"));
		} else if ("263X-Cxx-GCDWJKD".equals(parentVO.getDjlxbm())) {
			// 单位借款单
			list.add(OaWorkFlowUtil.listAddObj("jklx", "0"));
		}
		list = getListMap(list, "fieldName");
		// ---------其他字段end
		// JSONArray arr = JSONArray.fromObject(list);
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	// 获取263X || 264X 借款报销子表数据
	private JSONArray getJKBXBody(String pk_bill, String bill_type)
			throws BusinessException {
		String djdl = "";
		if ("263X".equals(bill_type)) {
			djdl = "jk";
		} else if ("264X".equals(bill_type)) {
			djdl = "bx";
		}
		IBXBillPrivate queryservice = (IBXBillPrivate) NCLocator.getInstance()
				.lookup(IBXBillPrivate.class);
		List<JKBXVO> temps = queryservice.queryVOsByPrimaryKeys(
				new String[] { pk_bill }, djdl);

		if (temps != null && temps.size() > 0) {
			JKBXVO temp = temps.get(0);
			// 获取详细信息
			List dtlist = new ArrayList();
			Map bodyMap = null;
			String jylx = temp.getParentVO().getDjlxbm();
			if ("2647".equals(jylx)) {
				BXVO bxVO = (BXVO) temp;
				bodyMap = getBody((BXBusItemVO[]) bxVO.getChildrenVO());
				if (bodyMap != null) {
					dtlist.add(bodyMap);
				}
			} else {
				if (bill_type.contains("263X")) {
					JKVO jkVO = (JKVO) temp;
					bodyMap = getBody((BXBusItemVO[]) jkVO.getChildrenVO());
				} else {
					if (bill_type.contains("264X")) {
						BXVO bxVO = (BXVO) temp;
						bodyMap = getBody((BXBusItemVO[]) bxVO.getChildrenVO());
						if (bxVO.getTableVO("er_bxcontrast") != null
								&& bxVO.getTableVO("er_bxcontrast").length > 0
								&& !"264X-Cxx-JBCFBX".equals(jylx)) {
							Map bodyMap2 = getCXBody2(
									(BxcontrastVO[]) bxVO
											.getTableVO("er_bxcontrast"),
									bill_type);
							if (bodyMap2 != null) {
								dtlist.add(bodyMap2);
							}
						}
					}
				}
				if (bodyMap != null) {
					dtlist.add(bodyMap);
				}
				// 差旅费报销单
				if ("264X-Cxx-RQCL".equals(bill_type)) {
					BXVO bxVO = (BXVO) temp;
					Map bodyMap2 = getBody2((BXBusItemVO[]) bxVO
							.getChildrenVO());
					if (bodyMap2 != null) {
						dtlist.add(bodyMap2);
					}
				}
				if (temp.getTableVO("er_cshare_detail") != null
						&& temp.getTableVO("er_cshare_detail").length > 0
						&& !"264X-Cxx-JBCFBX".equals(jylx)) {
					Map bodyFtMap = getFtBody((CShareDetailVO[]) temp
							.getTableVO("er_cshare_detail"));
					dtlist.add(bodyFtMap);
				}
			}
			dtlist = getListMap(dtlist, "fieldName");
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig
					.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			JSONArray dtlistString = JSONArray.fromObject(dtlist, jsonConfig);
			return dtlistString;
		} else {
			return null;
		}
	}

	private Map getFtBody(CShareDetailVO[] bvo) throws BusinessException {
		// TODO Auto-generated method stub
		Map dtMap = new HashMap();
		List workflowRequestTableRecords = new ArrayList();
		for (CShareDetailVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			// 承担单位
			if (null != temp.getAssume_org()) {
				String cddw = (String) getHyPubBO().findColValue(
						"org_orgs",
						"name",
						"nvl(dr,0) = 0 and pk_org ='" + temp.getAssume_org()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"cddw", cddw));
			}
			// 承担部门
			if (null != temp.getAssume_dept()) {
				String cdbm = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept ='" + temp.getAssume_dept()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"cdbm", cdbm));
			}

			// 收支项目
			if (null != temp.getPk_iobsclass()) {
				String szxm = (String) getHyPubBO().findColValue(
						"bd_inoutbusiclass",
						"name",
						"nvl(dr,0) = 0 and pk_inoutbusiclass ='"
								+ temp.getPk_iobsclass() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"szxm", szxm));
			}
			// 其他字段------end
			Map workflowRequestTableFieldsMap = new HashMap();
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	/**
	 * 获取附件列表
	 * 
	 * @param list
	 * @param pk_bill
	 * @throws BusinessException
	 */
	@SuppressWarnings("deprecation")
	private static void getFiles(JSONArray headData, String pk_bill)
			throws BusinessException {
		String strWhere = "filepath like '" + pk_bill + "/%' order by ts";
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
		getFileList(list, pk_bill);
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
		if (gzlist.size() > 0) {
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
			if (json.get("data") != null
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

	public HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	public void setHyPubBO(HYPubBO hyPubBO) {
		this.hyPubBO = hyPubBO;
	}

	// 获取供应商开户行
	private String getGYSKHH(String transi_type, String account)
			throws BusinessException {
		String gyskhh = "";
		String sql = (String) getHyPubBO().findColValue("sys_config",
				"config_value",
				"nvl(dr,0) = 0 and config_key = '" + transi_type + "'");
		sql += " '" + account + "'))";
		List<Object[]> ls = NCLocator.getInstance().lookup(GetDao.class)
				.query(sql);
		if (ls != null && ls.size() > 0 && ls.get(0) != null) {
			gyskhh = ls.get(0)[0] + "";
		}
		return gyskhh;
	}

	private Map getBody(BXBusItemVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		List workflowRequestTableRecords = new ArrayList();
		dtMap.put("tableDBName", TableName + "_dt1");
		for (BXBusItemVO temp : bvo) {
			if ("arap_bxbusitem".equals(temp.getTablecode())
					|| "jk_busitem".equals(temp.getTablecode())
					|| "bx_busitem".equals(temp.getTablecode())
					|| "er_busitem".equals(temp.getTablecode())) {
				// 构造数据
				List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
						.transBean2Map(temp);
				// 其他字段------begin
				// 报销类型
				if (temp.getPk_reimtype() != null) {
					String bxlx = (String) getHyPubBO().findColValue(
							"er_reimtype",
							"name",
							"nvl(dr,0) = 0 and pk_reimtype ='"
									+ temp.getPk_reimtype() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"bxlx", bxlx));
				}
				// 项目
				if (temp.getJobid() != null) {
					// 项目编码
					String project_code = (String) getHyPubBO().findColValue(
							"bd_project",
							"project_code",
							"nvl(dr,0) = 0 and pk_project = '"
									+ temp.getJobid() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"project_code", project_code));
					// 项目名称
					String project_name = (String) getHyPubBO().findColValue(
							"bd_project",
							"project_name",
							"nvl(dr,0) = 0 and pk_project = '"
									+ temp.getJobid() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"project_name", project_name));
				}
				// 出发地点
				if (temp.getDefitem3() != null) {
					String cfdd = (String) getHyPubBO().findColValue(
							"bd_addressdoc",
							"name",
							"nvl(dr,0) = 0 and pk_addressdoc ='"
									+ temp.getDefitem3() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"cfdd", cfdd));
				}
				// 目的地点
				if (temp.getDefitem4() != null) {
					String mddd = (String) getHyPubBO().findColValue(
							"bd_addressdoc",
							"name",
							"nvl(dr,0) = 0 and pk_addressdoc ='"
									+ temp.getDefitem4() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"mddd", mddd));
				}
				// 收款对象
				if (temp.getPaytarget() != null) {
					String skdxstr = temp.getPaytarget() + "";
					int skdxint = Integer.parseInt(skdxstr);
					String skdx = "";
					if (skdxint == 0) {
						skdx = "员工";
					} else if (skdxint == 1) {
						skdx = "供应商";
					} else if (skdxint == 2) {
						skdx = "客户";
					}
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"skdx", skdx));
				}
				/* 开户行 */
				String khh = (String) getHyPubBO().findColValue(
						"bd_asslinenum",
						"name",
						"nvl(dr,0) = 0 and pk_asslinenum ='"
								+ temp.getDefitem7() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("khh",
						khh));
				// 发票类型
				if (temp.getDefitem6() != null) {
					String fplx = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc ='"
									+ temp.getDefitem6() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"fplx", fplx));
				}
				// 不含税金额
				if (temp.getDefitem15() != null) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"bhsje", temp.getDefitem15() + ""));
				}
				// 税率
				if (temp.getDefitem17() != null) {
					String sl = "";
					if (temp.getDefitem17().length() > 15) {
						sl = (String) getHyPubBO().findColValue(
								"bd_defdoc",
								"name",
								"nvl(dr,0) = 0 and pk_defdoc ='"
										+ temp.getDefitem17() + "'");
					} else {
						sl = temp.getDefitem17();
					}
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"sl", sl));
				}
				if (temp.getSzxmid() != null) {
					// 收支项目
					String szxm = (String) getHyPubBO().findColValue(
							"bd_inoutbusiclass",
							"name",
							"nvl(dr,0) = 0 and pk_inoutbusiclass ='"
									+ temp.getSzxmid() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"szxm", szxm));
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"yslb", szxm));
				}
				if (temp.getDefitem11() != null) {
					// 是否属于安全经费
					String sfsyaqjf = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc ='"
									+ temp.getDefitem11() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"sfsyaqjf", sfsyaqjf));
				}
				if (temp.getCustaccount() != null) {
					// 客商银行账户
					String ksyhzh = (String) getHyPubBO().findColValue(
							"bd_bankaccbas",
							"name",
							"nvl(dr,0) = 0 and pk_bankaccbas ='"
									+ temp.getCustaccount() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"ksyhzh", ksyhzh));
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"gysyhzh", ksyhzh));
				}
				if (temp.getDeptid() != null) {
					// 费用发生部门
					String fyfsbm = (String) getHyPubBO().findColValue(
							"org_dept",
							"name",
							"nvl(dr,0) = 0 and pk_dept ='" + temp.getDeptid()
									+ "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"fyfsbm", fyfsbm));
				}
				// 收款人
				if (temp.getReceiver() != null) {
					String skr = (String) getHyPubBO().findColValue(
							"bd_psndoc",
							"name",
							"nvl(dr,0) = 0 and pk_psndoc ='"
									+ temp.getReceiver() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"skr", skr));
				}
				// 个人银行账户
				if (temp.getSkyhzh() != null) {
					String gryhzh = (String) getHyPubBO().findColValue(
							"bd_bankaccsub",
							"accnum",
							"nvl(dr,0) = 0 and pk_bankaccsub ='"
									+ temp.getSkyhzh() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"gryhzh", gryhzh));
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"skzh", gryhzh));
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"jkryhzh", gryhzh));
				}
				// 出差区域类型
				if (temp.getDefitem13() != null) {
					String ccqylx = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc ='"
									+ temp.getDefitem13() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"ccqylx", ccqylx));
				}
				// 供应商名称
				if (null != temp.getHbbm()) {
					SupplierVO supplierVO = (SupplierVO) getHyPubBO()
							.queryByPrimaryKey(SupplierVO.class, temp.getHbbm());
					String name = supplierVO.getName();
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"pk_supplier_name", name));
				}
				// 收款对象
				/*
				 * String paytarget = ""; if (temp.getPaytarget() != null) { int
				 * dx = temp.getPaytarget(); if (dx == 0) { paytarget = "员工"; }
				 * else if (dx == 1) { paytarget = "供应商"; } else if (dx == 2) {
				 * paytarget = "客户"; } }
				 * workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
				 * "paytarget", paytarget));
				 */
				// 客户
				String kh = (String) getHyPubBO().findColValue(
						"bd_customer",
						"name",
						"nvl(dr,0) = 0 and pk_customer ='" + temp.getCustomer()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("kh",
						kh));
				// 资产类别
				if (temp.getDefitem9() != null) {
					String zclb = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc ='"
									+ temp.getDefitem9() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"zclb", zclb));
				}
				// 资金类别
				if (temp.getDefitem10() != null) {
					String zjlb = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc ='"
									+ temp.getDefitem10() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"zjlb", zjlb));
				}
				// 计量单位
				String dwstr = "";
				if (null != temp.getDefitem12()) {
					dwstr = (String) getHyPubBO().findColValue(
							"bd_measdoc",
							"name",
							"nvl(dr,0) = 0 and pk_measdoc = '"
									+ temp.getDefitem12() + "'");
				}
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("dw",
						dwstr));

				// 申请单位
				if (temp.getDwbm() != null) {
					String sqdw = (String) getHyPubBO().findColValue(
							"org_adminorg",
							"name",
							"nvl(dr,0) = 0 and pk_adminorg ='" + temp.getDwbm()
									+ "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"sqdw", sqdw));
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"fycddw", sqdw));
				}
				// 其他业务支付类型
				if (temp.getDefitem21() != null) {
					String qtywzflx = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc ='"
									+ temp.getDefitem21() + "'");

					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"qtywzflx", qtywzflx));
				}
				// 申请部门
				String sqbm = (String) getHyPubBO()
						.findColValue(
								"org_dept",
								"name",
								"nvl(dr,0) = 0 and pk_dept ='"
										+ temp.getDeptid() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"sqbm", sqbm));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fycdbm", sqbm));
				// 交通工具
				if (temp.getDefitem5() != null) {
					String jtgjstr = temp.getDefitem5();
					String jtgj = "";
					if (jtgjstr != null && jtgjstr.length() > 15) {
						jtgj = (String) getHyPubBO().findColValue(
								"bd_defdoc",
								"name",
								"nvl(dr,0) = 0 and pk_defdoc ='"
										+ temp.getDefitem5() + "'");
					} else {
						int jtgjint = Integer.parseInt(jtgjstr);
						if (jtgjint == 0) {
							jtgj = "其他";
						} else if (jtgjint == 1) {
							jtgj = "城铁地铁";
						} else if (jtgjint == 3) {
							jtgj = "公交车";
						} else if (jtgjint == 4) {
							jtgj = "公务车";
						} else if (jtgjint == 5) {
							jtgj = "长途汽车";
						} else if (jtgjint == 6) {
							jtgj = "火车";
						} else if (jtgjint == 7) {
							jtgj = "飞机";
						}
					}
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"jtgj", jtgj));
				}
				// 能投原字段
				/* 借款事由 */
				if (null != temp.getDefitem30()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"jksy", temp.getDefitem30()));
				}
				/* 金额 */
				if (null != temp.getAmount()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"je", temp.getAmount() + ""));
				}
				/* 金额合计 */
				if (null != temp.getVat_amount()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"jehj",
							temp.getVat_amount().setScale(2,
									UFDouble.ROUND_HALF_UP)
									+ ""));

					/* 退费金额 */
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"tfje",
							temp.getVat_amount().setScale(2,
									UFDouble.ROUND_HALF_UP)
									+ ""));
				}
				/* 税额 */
				if (null != temp.getDefitem19()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"se", temp.getDefitem19()));
				}
				/* 退费类型 */
				if (null != temp.getDefitem40()) {
					String tflx = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc ='"
									+ temp.getDefitem40() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"tflx", tflx));
				}
				/* 是否热电用户 */
				if (null != temp.getDefitem50()) {
					String sfrdyh = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc ='"
									+ temp.getDefitem50() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"sfrdyh", sfrdyh));
				}
				/* 出差人数 */
				if (null != temp.getDefitem45()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"ccrs", temp.getDefitem45()));
				}

				/* 出发日期 */
				if (null != temp.getDefitem1()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"cfrq", temp.getDefitem1()));
				}
				/* 返程日期 */
				if (null != temp.getDefitem2()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"fcrq", temp.getDefitem2()));
				}
				/* 出差天数 */
				if (null != temp.getDefitem9()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"ccts", temp.getDefitem9()));
				}
				/* 标准 */
				if (null != temp.getDefitem46()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"bz", temp.getDefitem46() + ""));
				}
				/* 备注 */
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bz1",
						temp.getDefitem31()));
				/* 还款金额 */
				if (null != temp.getCjkybje()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"hkje",
							temp.getCjkybje().setScale(2,
									UFDouble.ROUND_HALF_UP)
									+ ""));
				}
				/* 加班事由 */
				if (null != temp.getDefitem20()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"jbsy", temp.getDefitem20()));
				}
				/* 采暖季 */
				if (null != temp.getDefitem42()) {
					String cnj = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc ='"
									+ temp.getDefitem42() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"cnj", cnj));
				}
				/* 应交金额 */
				if (null != temp.getDefitem43()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"yjje",
							new UFDouble(temp.getDefitem43() == null ? "0.00"
									: temp.getDefitem43()).setScale(2,
									UFDouble.ROUND_HALF_UP)
									+ ""));
				}

				/* 已交金额 */
				if (null != temp.getDefitem44()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"yjje1",
							new UFDouble(temp.getDefitem44() == null ? "0.00"
									: temp.getDefitem44()).setScale(2,
									UFDouble.ROUND_HALF_UP)
									+ ""));
				}
				// 其他字段------end
				Map workflowRequestTableFieldsMap = new HashMap();
				workflowRequestTableFieldsMap.put("recordOrder", "0");
				workflowRequestTableFieldsMap.put("workflowRequestTableFields",
						workflowRequestTableFields);
				workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
			}
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	private Map getCXBody2(BxcontrastVO[] bvo, String transi_type)
			throws BusinessException {
		Map dtMap = new HashMap();
		List workflowRequestTableRecords = new ArrayList();
		dtMap.put("tableDBName", TableName + "_dt3");
		for (BxcontrastVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			// 其他字段------end
			Map workflowRequestTableFieldsMap = new HashMap();
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	// 冲借款明细
	private Map getBody2(BXBusItemVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		List workflowRequestTableRecords = new ArrayList();
		dtMap.put("tableDBName", TableName + "_dt2");
		for (BXBusItemVO temp : bvo) {
			if ("arap_bxbusitem".equals(temp.getTablecode())
					|| "jk_busitem".equals(temp.getTablecode())) {
				// 构造数据
				List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
						.transBean2Map(temp);
				// 其他字段------begin
				// 收支项目
				String szxm = (String) getHyPubBO().findColValue(
						"bd_inoutbusiclass",
						"name",
						"nvl(dr,0) = 0 and pk_inoutbusiclass ='"
								+ temp.getSzxmid() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"szxm", szxm));
				// 收款人
				if (null != temp.getReceiver()) {
					String skr = (String) getHyPubBO().findColValue(
							"bd_psndoc",
							"name",
							"nvl(dr,0) = 0 and pk_psndoc ='"
									+ temp.getReceiver() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"skr", skr));
				}
				// 个人银行账户
				if (null != temp.getSkyhzh()) {
					String gryhzh = (String) getHyPubBO().findColValue(
							"bd_bankaccsub",
							"accnum",
							"nvl(dr,0) = 0 and pk_bankaccsub ='"
									+ temp.getSkyhzh() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"gryhzh", gryhzh));
				}
				// 其他字段------end
				Map workflowRequestTableFieldsMap = new HashMap();
				workflowRequestTableFieldsMap.put("recordOrder", "0");
				workflowRequestTableFieldsMap.put("workflowRequestTableFields",
						workflowRequestTableFields);
				workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
			}
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	// 来源系统名称
	private Map<Integer, String> sourcesystypeMap() {
		Map<Integer, String> map = new HashMap<Integer, String>();
		map.put(0, "应收系统");
		map.put(1, "应付系统");
		map.put(2, "现金管理");
		map.put(3, "销售系统");
		map.put(4, "采购系统");
		map.put(5, "资金结算");
		map.put(6, "网上银行");
		map.put(7, "付款排程");
		map.put(8, "票据管理");
		map.put(9, "协同单据");
		map.put(10, "信贷系统");
		map.put(12, "项目管理");
		map.put(13, "全面预算");
		map.put(16, "内部交易");
		map.put(17, "外部交换平台");
		map.put(18, "资金计息");
		map.put(19, "库存管理");
		map.put(20, "合同");
		map.put(24, "运输");
		map.put(104, "资产管理");
		map.put(105, "费用管理");
		map.put(109, "零售系统");
		map.put(113, "进出口系统");
		map.put(114, "CRM");
		map.put(115, "信用证管理");
		return map;
	}

	/**
	 * List<Map<String, Object>> 去出重复的key
	 * 
	 * @param str
	 *            Map<String, Object> 中对比的key
	 * @return
	 */
	public static List<Map<String, Object>> getListMap(
			List<Map<String, Object>> oldList, String str) {

		List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < oldList.size(); i++) {
			Map<String, Object> oldMap = oldList.get(i);
			if (newList.size() > 0) {
				boolean isContain = false;
				for (int j = 0; j < newList.size(); j++) {
					Map<String, Object> newMap = newList.get(j);
					if (newMap.get(str) != null && oldMap.get(str) != null) {
						if (newMap.get(str).equals(oldMap.get(str))) {
							for (String key : oldMap.keySet()) {
								newMap.put(key, oldMap.get(key));
							}
							isContain = true;
							break;
						}
					}
				}

				if (!isContain) {
					newList.add(oldMap);
				}

			} else {
				newList.add(oldMap);
			}
		}
		return newList;
	}

	public int ifNTOrg(String pk_org) throws BusinessException {
		int flag = 0;
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		if ("0001A21000000003U6L6".equals(pk_org)
				|| "0001A21000000003U6L6".equals(orgVO.getPk_fatherorg())) {
			flag = 1;
		}
		if (flag == 0 && StringUtils.isNotEmpty(orgVO.getPk_fatherorg())) {
			OrgVO fatherorgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(
					OrgVO.class, pk_org);// 上级组织
			if (StringUtils.isNotEmpty(fatherorgVO.getPk_fatherorg())
					&& "0001A21000000003U6L6".equals(fatherorgVO
							.getPk_fatherorg())) {
				flag = 1;
			}
		}
		return flag;
	}
}
