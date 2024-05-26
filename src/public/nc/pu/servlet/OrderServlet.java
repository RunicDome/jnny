package nc.pu.servlet;

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
import nc.bs.trade.business.HYPubBO;
import nc.itf.pu.m21.IOrderMaintain;
import nc.itf.uap.pf.IWorkflowMachine;
import nc.itf.uap.pf.IplatFormEntry;
import nc.vo.bd.material.MaterialVO;
// import nc.vo.bd.material.measdoc.MeasdocVO;
import nc.vo.bd.taxcode.TaxrateVO;
import nc.vo.logging.Debug;
import nc.vo.org.FinanceOrgVO;
import nc.vo.org.OrgVO;
import nc.vo.org.StockOrgVO;
import nc.vo.pu.m21.context.OrderContext;
import nc.vo.pu.m21.entity.OrderHeaderVO;
import nc.vo.pu.m21.entity.OrderItemVO;
import nc.vo.pu.m21.entity.OrderVO;
import nc.vo.pu.m21.rule.api.Calculate;
import nc.vo.pu.m21.rule.api.FillOrderVOInfoFor3rd;
import nc.vo.pu.pub.enumeration.PricePriority;
import nc.vo.pu.pub.util.PUSysParamUtil;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.scmpub.res.billtype.POBillType;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;

import nc.bs.logging.Logger;
import com.alibaba.fastjson.JSON;

//采购订单JSON导入
@SuppressWarnings({ "restriction", "unused", "unchecked", "rawtypes" })
public class OrderServlet extends HttpServlet implements IHttpServletAdaptor {

	private static final long serialVersionUID = 1L;

	private static HYPubBO hypubBo = new HYPubBO();

	// 继承HttpServlet
	// 实现IHttpServletAdaptor
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		// 实现post请求，调用IHttpServletAdaptor下的doAction方法
		doAction(req, res);
	}

	@Override
	public void doAction(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
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
		Debug.error("=============upload order msg ：" + body);
		String request = createVO(body);
		res.setCharacterEncoding("utf-8");
		System.out.println("=================" + request);
		res.setStatus(200);
		res.getWriter().write(request);// 输出body
	}

	private String createVO(String body) {
		InvocationInfoProxy.getInstance().setGroupId("0001A1100000000001QS");

		String errorMsg = "";
		List<Map> paramList = new ArrayList<Map>();
		JSONObject jsonObj;
		int flag = 0;
		try {
			jsonObj = new JSONObject(body);
			String arr = jsonObj.getString("data");
			paramList = JSON.parseArray(arr, Map.class);
			List<OrderVO> orderVOs = new ArrayList<OrderVO>();
			boolean taxPricePriorToPrice = true;
			Calculate calculate = new Calculate();
			OrderVO orderVO;
			for (int k = 0; k < paramList.size(); k++) {
				Map<String, Object> map = paramList.get(k);
				// 获取表头信息
				Map<String, Object> headInfo = (Map) map.get("bill");
				orderVO = new OrderVO();
				OrderHeaderVO hVO = new OrderHeaderVO();
				String vbillcode = headInfo.get("vbillcode") + "";
				// 查询重复
				String unic_vbillcode = (String) hypubBo.findColValue(
						"PO_ORDER", "vbillcode",
						"nvl(dr,0) = 0 and vbillcode = '" + vbillcode + "'");
				System.out.println("订单编号：" + vbillcode);
				Logger.error("订单编号：" + vbillcode);
				System.out.println("当前时间：" + new DateTime());
				Logger.error("当前时间：" + new DateTime());
				System.out.println("单据日期：" + headInfo.get("dbilldate") + "");
				Logger.error("单据日期：" + headInfo.get("dbilldate") + "");
				if (!isEmpty(unic_vbillcode)) {
					errorMsg = "订单编号：" + vbillcode + "已经存在，不允许重复提交！";
					return createMsg(false, "-1", errorMsg);
				}
				hVO.setVbillcode(vbillcode);
				// 采购组织
				String pk_code = headInfo.get("pk_org") + "";
				if (!"410".equals(pk_code)) {
					// 自定义参数：采购类型
					String vdef9 = headInfo.get("vdef9") + "";
					String pk_vdef9 = (String) hypubBo.findColValue(
							"BD_DEFDOC", "PK_DEFDOC",
							"nvl(dr,0) = 0 AND PK_DEFDOCLIST = '1001A1100000001PVWPS' AND CODE = '"
									+ vdef9 + "'");
					hVO.setVdef9(pk_vdef9);
				}
				String pk_purchaseorg = (String) hypubBo.findColValue(
						"org_purchaseorg", "pk_purchaseorg",
						"nvl(dr,0) = 0 and code = '" + pk_code + "'");
				OrgVO orgVO = (OrgVO) hypubBo.queryByPrimaryKey(OrgVO.class,
						pk_purchaseorg);
				if (null == orgVO) {
					errorMsg = "采购组织编码：" + pk_code + "在NC中不存在！";
					return createMsg(false, "-1", errorMsg);
				}
				String pk_org = orgVO.getPk_org();
				taxPricePriorToPrice = isTaxPricePriorToPrice(pk_org);
				hVO.setPk_org(pk_org);
				hVO.setPk_group(orgVO.getPk_group());
				// 数据库查询组织版本
				hVO.setPk_org_v(orgVO.getPk_vid());
				SimpleDateFormat timeFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date approvetime = timeFormat.parse(headInfo.get("dbilldate")
						+ "");
				InvocationInfoProxy.getInstance().setBizDateTime(
						approvetime.getTime());
				hVO.setDbilldate(new UFDate(timeFormat.parse(headInfo
						.get("dbilldate") + "")));
				hVO.setCreationtime(new UFDateTime(timeFormat.parse(headInfo
						.get("dbilldate") + "")));
				hVO.setDmakedate(new UFDate(timeFormat.parse(headInfo
						.get("dbilldate") + "")));
				String supplier_code = headInfo.get("pk_supplier") + "";
				if (StringUtils.isEmpty(supplier_code)
						|| StringUtils.equals("null", supplier_code)) {
					errorMsg = "[pk_supplier]为空，请填写供应商编码信息！";
					return createMsg(false, "-1", errorMsg);
				}
				String pk_supplier = (String) hypubBo.findColValue(
						"bd_supplier", "pk_supplier",
						"nvl(dr,0) = 0 and code = '" + supplier_code + "'");
				if (isEmpty(pk_supplier)) {
					errorMsg = "供应商：" + supplier_code + "在NC中不存在！";
					return createMsg(false, "-1", errorMsg);
				}
				hVO.setPk_supplier(pk_supplier);
				String dept = headInfo.get("pk_dept") + "";
				String pk_dept = (String) hypubBo.findColValue("org_dept",
						"pk_dept", "nvl(dr,0) = 0 and code = '" + dept
								+ "' and pk_org = '" + pk_org + "'");
				if (isEmpty(pk_dept)) {
					errorMsg = "部门：" + dept + "在NC中不存在！";
					return createMsg(false, "-1", errorMsg);
				}
				hVO.setPk_dept(pk_dept);
				String dept_v = (String) hypubBo.findColValue("ORG_DEPT",
						"pk_vid", "nvl(dr,0) = 0 and pk_dept = '" + pk_dept
								+ "'");
				hVO.setPk_dept_v(dept_v);
				String cemployeeid = headInfo.get("cemployeeid") + "";
				String pk_psndoc = (String) hypubBo.findColValue("bd_psndoc",
						"pk_psndoc", "nvl(dr,0) = 0 and code = '" + cemployeeid
								+ "'");
				if (isEmpty(pk_psndoc)) {
					errorMsg = "采购员：" + cemployeeid + "在NC中不存在！";
					return createMsg(false, "-1", errorMsg);
				}
				hVO.setCemployeeid(pk_psndoc);
				String vmemo = "";
				if (null != headInfo.get("vmemo")) {
					vmemo = headInfo.get("vmemo") + "";
				}
				hVO.setVmemo(vmemo);
				String billmaker_code = headInfo.get("billmaker") + "";
				String billmaker = (String) hypubBo
						.findColValue("sm_user", "cuserid",
								"nvl(dr,0) = 0 and user_code = '"
										+ billmaker_code + "' "
										+ "and enablestate = 2");
				if (isEmpty(billmaker)) {
					errorMsg = "制单人：" + billmaker_code + "在NC中不存在！";
					return createMsg(false, "-1", errorMsg);
				}
				InvocationInfoProxy.getInstance().setUserId(billmaker);
				hVO.setBillmaker(billmaker);
				hVO.setCreator(billmaker);
				// 设置默认值
				if (StringUtils.equals(pk_code, "410")) {
					flag = 1;
					hVO.setVtrantypecode("21-Cxx-YQ—410");
					String jylxzj = (String) hypubBo
							.findColValue(
									"bd_billtype",
									"pk_billtypeid",
									"nvl(dr,0) = 0 and pk_billtypecode = '21-Cxx-YQ—410' and pk_group = '0001A1100000000001QS'");
					hVO.setCtrantypeid(jylxzj);
				} else {
					hVO.setVtrantypecode("21-Cxx-RD-96");
					String jylxzj = (String) hypubBo
							.findColValue(
									"bd_billtype",
									"pk_billtypeid",
									"nvl(dr,0) = 0 and pk_billtypecode = '21-Cxx-RD-96' and pk_group = '0001A1100000000001QS'");
					hVO.setCtrantypeid(jylxzj);
					// hVO.setCtrantypeid("1001A1100000002CKHYC");
				}
				hVO.setCorigcurrencyid("1002Z0100000000001K1"); // 币种
				hVO.setFhtaxtypeflag(1);
				// hVO.setForderstatus(2); // 审批通过
				// hVO.setStatus(2); // 审批通过
				// hVO.setApprover(billmaker);
				// hVO.setTaudittime(approvetime);

				// 表体
				List<Map<String, Object>> bodyInfo = (List) map.get("bill_b");
				List<OrderItemVO> itemVOs = new ArrayList<OrderItemVO>();
				for (int i = 0; i < bodyInfo.size(); i++) {
					Map<String, Object> bodyInfoItem = bodyInfo.get(i);
					OrderItemVO itemVO = new OrderItemVO();
					// 物料信息
					String vvendinventorycode = bodyInfoItem
							.get("vvendinventorycode") + "";
					String pk_material = (String) hypubBo.findColValue(
							"bd_material", "pk_material",
							"nvl(dr,0) = 0 and code='" + vvendinventorycode
									+ "'");
					MaterialVO materialVO = (MaterialVO) hypubBo
							.queryByPrimaryKey(MaterialVO.class, pk_material);
					if (null == materialVO) {
						errorMsg = "物料编码（" + vvendinventorycode
								+ "）在NC中未获取到数据！";
						return createMsg(false, "-1", errorMsg);
					}
					itemVO.setPk_material(pk_material);
					itemVO.setPk_srcmaterial(materialVO.getPk_source());

					// MeasdocVO[] measdocVOs = (MeasdocVO[])
					// hypubBo.queryByCondition(MeasdocVO.class,
					// "nvl(dr,0) = 0 and pk_measdoc='" +
					// materialVO.getPk_measdoc() + "'");
					String scalefactor = "1.00/1.00";
					/*
					 * if(null != measdocVOs && measdocVOs.length > 0) {
					 * MeasdocVO measdocVO = measdocVOs[0]; if(
					 * measdocVO.getScalefactor().getDouble() >0 ) { scalefactor
					 * = measdocVO.getScalefactor() + "/1.00"; } }
					 */
					itemVO.setCastunitid(materialVO.getPk_measdoc());
					itemVO.setCunitid(materialVO.getPk_measdoc());
					itemVO.setCqtunitid(materialVO.getPk_measdoc());
					itemVO.setVchangerate(scalefactor);
					itemVO.setVqtunitrate(scalefactor);
					String batch = "";
					if (null != bodyInfoItem.get("pk_batchcode")) {
						batch = bodyInfoItem.get("pk_batchcode") + "";
					}
					itemVO.setVbatchcode(batch);
					String project_code = bodyInfoItem.get("cprojectid") + "";
					String pk_project = (String) hypubBo.findColValue(
							"bd_project", "pk_project",
							"nvl(dr,0) = 0 and project_code='" + project_code
									+ "'");
					if (isEmpty(pk_project)) {
						pk_project = "~";
					}
					itemVO.setCprojectid(pk_project);
					hVO.setPk_project(pk_project);
					// itemVO.setDplanarrvdate(new
					// UFDate(bodyInfoItem.get("dplanarrvdate")+""));
					itemVO.setDplanarrvdate(hVO.getDbilldate());// 计划到货日期
					String arrvstoorg_code = bodyInfoItem.get("pk_arrvstoorg")
							+ "";
					String pk_arrvstoorg = (String) hypubBo.findColValue(
							"org_stockorg", "pk_stockorg",
							"nvl(dr,0) = 0 and code='" + arrvstoorg_code + "'");
					StockOrgVO arrorgVO = (StockOrgVO) hypubBo
							.queryByPrimaryKey(StockOrgVO.class, pk_arrvstoorg);
					if (null == arrorgVO) {
						errorMsg = "收货库存组织编码：" + arrvstoorg_code + "在NC中不存在！";
						return createMsg(false, "-1", errorMsg);
					}
					itemVO.setPk_arrvstoorg(pk_arrvstoorg);
					itemVO.setPk_arrvstoorg_v(arrorgVO.getPk_vid());
					itemVO.setPk_reqstoorg(pk_arrvstoorg);
					itemVO.setPk_reqstoorg_v(arrorgVO.getPk_vid());
					String recvstordoc = bodyInfoItem.get("pk_recvstordoc")
							+ "";

					String pk_recvstordoc = (String) hypubBo
							.findColValue(
									"bd_stordoc",
									"pk_stordoc",
									"nvl(dr,0) = 0 and code='"
											+ recvstordoc
											+ "' and pk_org in(select pk_stockorg from org_stockorg where code='"
											+ arrvstoorg_code + "' and dr=0)");
					if (isEmpty(pk_recvstordoc)) {
						errorMsg = "收货仓库编码：" + recvstordoc + "在NC中不存在！";
						return createMsg(false, "-1", errorMsg);
					}
					itemVO.setPk_recvstordoc(pk_recvstordoc);

					String psfinanceorg_code = bodyInfoItem
							.get("pk_psfinanceorg_v") + "";
					String pk_psfinanceorg = (String) hypubBo.findColValue(
							"org_financeorg", "pk_financeorg",
							"nvl(dr,0) = 0 and code='" + psfinanceorg_code
									+ "'");
					FinanceOrgVO psforgVO = (FinanceOrgVO) hypubBo
							.queryByPrimaryKey(FinanceOrgVO.class,
									pk_psfinanceorg);
					if (null == psforgVO) {
						errorMsg = "结算财务组织编码：" + psfinanceorg_code + "在NC中不存在！";
						return createMsg(false, "-1", errorMsg);
					}
					itemVO.setPk_psfinanceorg_v(psforgVO.getPk_vid());
					itemVO.setPk_psfinanceorg(pk_psfinanceorg);
					itemVO.setBlargess(new UFBoolean(bodyInfoItem
							.get("blargess") + ""));

					itemVO.setPk_apfinanceorg(pk_psfinanceorg);
					itemVO.setPk_apfinanceorg_v(psforgVO.getPk_vid());

					// 设置默认值
					itemVO.setFbuysellflag(2);
					itemVO.setCsendcountryid("0001Z010000000079UJJ"); // 发货国家/地区
																		// 默认
					itemVO.setCrececountryid("0001Z010000000079UJJ");
					itemVO.setCtaxcountryid("0001Z010000000079UJJ");
					itemVO.setCorigcurrencyid("1002Z0100000000001K1"); // 币种
					itemVO.setCcurrencyid("1002Z0100000000001K1"); // 币种
					itemVO.setFtaxtypeflag(1); // 扣税类别
					itemVO.setNexchangerate(new UFDouble("1"));

					UFDouble nastnum = new UFDouble(bodyInfoItem.get("nastnum")
							+ "");
					UFDouble ntaxmny = new UFDouble(
							bodyInfoItem.get("ntotalorigmny") + "");
					itemVO.setNnum(nastnum);
					itemVO.setNastnum(nastnum);
					itemVO.setNqtunitnum(nastnum);
					itemVO.setNorigtaxmny(ntaxmny);
					itemVO.setNqtorigtaxprice(ntaxmny.div(nastnum));
					itemVO.setCrowno((i + 1) * 10 + "");
					if (itemVO.getNtaxrate() == null
							&& !StringUtils.equals(pk_code, "410")) {
						// 查找材料对应的税码，税率
						String pk_taxcode = (String) hypubBo.findColValue(
								"bd_taxcode",
								"pk_taxcode",
								"nvl(dr,0) = 0 and mattaxes='"
										+ materialVO.getPk_mattaxes() + "'");
						itemVO.setCtaxcodeid(pk_taxcode);
						TaxrateVO[] taxrateVO = (TaxrateVO[]) hypubBo
								.queryByCondition(TaxrateVO.class,
										"nvl(dr,0) = 0 and pk_taxcode='"
												+ pk_taxcode + "'");
						if (null != taxrateVO && taxrateVO.length >= 1) {
							UFDouble taxrate = taxrateVO[0].getTaxrate();
							itemVO.setNtaxrate(taxrate);
						}
					} else {
						UFDouble ntax = new UFDouble(bodyInfoItem.get("ntax")
								+ "");
						if (ntax.compareTo(UFDouble.ZERO_DBL) == 0) {
							String pk_taxcode = (String) hypubBo.findColValue(
									"bd_taxcode", "pk_taxcode",
									"nvl(dr,0) = 0 and code='CN001'");
							itemVO.setCtaxcodeid(pk_taxcode);
							itemVO.setNtaxrate(UFDouble.ZERO_DBL);
							itemVO.setNtax(UFDouble.ZERO_DBL);
						}
					}
					itemVOs.add(itemVO);
				}
				orderVO.setHVO(hVO);
				OrderItemVO[] vos = itemVOs.toArray(new OrderItemVO[0]);
				orderVO.setBVO(vos);
				orderVOs.add(orderVO);
			}

			FillOrderVOInfoFor3rd fillOrderClientVO = new FillOrderVOInfoFor3rd();
			OrderVO[] array = fillOrderClientVO.fillInfo(orderVOs
					.toArray(new OrderVO[0]));
			NCLocator.getInstance().lookup(IOrderMaintain.class)
					.save(array, new OrderContext());

			IplatFormEntry pIplatFormEntry = NCLocator.getInstance().lookup(
					IplatFormEntry.class);
			for (OrderVO clientBill : array) {
				// 提交
				OrderVO[] submitVO = (OrderVO[]) pIplatFormEntry.processAction(
						"SAVE", POBillType.Order.getCode(), null, clientBill,
						null, null);
				if (flag == 1) {
					HashMap hmPfExParams = new HashMap();
					WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
							.getInstance().lookup(IWorkflowMachine.class))
							.checkWorkFlow("APPROVE",
									POBillType.Order.getCode(), submitVO[0],
									hmPfExParams);
					if (worknoteVO != null) {
						worknoteVO.setChecknote("批准");
						worknoteVO.setApproveresult("Y");
					}
					// 审核
					pIplatFormEntry.processAction("APPROVE",
							POBillType.Order.getCode(), worknoteVO,
							submitVO[0], null, null);
				}
			}

			errorMsg = "上传成功！";
			return createMsg(true, "0", errorMsg);
		} catch (Exception e) {
			System.out.println(e);
			errorMsg = "上传失败！系统出现内部错误！错误信息：" + e.getMessage();
			return createMsg(false, "-1", errorMsg);
		}
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
		return JSON.toJSONString(msgMap);
	}

	private static boolean isEmpty(String str) {
		return (str == null || str.length() == 0 || "null".equals(str));
	}

	private boolean isTaxPricePriorToPrice(String pk_org) {
		boolean flag = true;
		if (null == pk_org) {
			return flag;
		}
		PricePriority pricePriority = PUSysParamUtil.getPO28(pk_org);
		if (!PricePriority.TAXPRICE_PRIOR_TO_PRICE.equals(pricePriority)) {
			flag = false;
		}
		return flag;
	}
}