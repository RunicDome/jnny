package nc.to.servlet;

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
import nc.itf.uap.pf.IPFBusiAction;
import nc.vo.bd.material.MaterialVO;
import nc.vo.bd.taxcode.TaxrateVO;
import nc.vo.logging.Debug;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.scmpub.br.AttributeCoordinate;
import nc.vo.scmpub.br.bill.BillNullCheck;
import nc.vo.to.enumeration.OnWayOwner;
import nc.vo.to.m5x.entity.BillHeaderVO;
import nc.vo.to.m5x.entity.BillItemVO;
import nc.vo.to.m5x.entity.BillVO;
import nc.vo.to.m5x.pub.priceutil.M5XBSNumPriceMnyUtils;
import nc.vo.to.m5x.pub.rule.FillRefForPushUtilsPub;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONObject;

import com.alibaba.fastjson.JSON;

@SuppressWarnings({ "rawtypes", "unchecked", "restriction" })
public class TransOrderServlet extends HttpServlet implements
		IHttpServletAdaptor {

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
		String request = "";
		try {
			request = createVO(body);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		res.setCharacterEncoding("utf-8");
		System.out.println("=================" + request);
		res.setStatus(200);
		res.getWriter().write(request);// 输出body
	}

	private String createVO(String body) throws BusinessException {
		InvocationInfoProxy.getInstance().setGroupId("0001A1100000000001QS");
		String errorMsg = "";
		List<Map> paramList = new ArrayList<Map>();
		JSONObject jsonObj;
		BillVO[] vos = {};
		try {
			jsonObj = new JSONObject(body);
			String arr = jsonObj.getString("data");
			paramList = JSON.parseArray(arr, Map.class);
			List<BillVO> orderVOs = new ArrayList<BillVO>();
			String cuserid = "";
			BillVO billVO;
			for (int k = 0; k < paramList.size(); k++) {
				Map<String, Object> map = paramList.get(k);
				// 获取表头信息
				Map<String, Object> headInfo = (Map) map.get("bill");
				billVO = new BillVO();
				BillHeaderVO hVO = new BillHeaderVO();
				if (headInfo.get("vbillcode") == null) {
					errorMsg = "订单编号为空[vbillcode]！";
					return createMsg(false, "-1", errorMsg);
				}
				String vbillcode = headInfo.get("vbillcode").toString();
				// 查询重复
				String unic_vbillcode = (String) hypubBo.findColValue(
						"to_bill", "vbillcode",
						"nvl(dr,0) = 0 and vbillcode = '" + vbillcode + "'");
				if (!isEmpty(unic_vbillcode)) {
					errorMsg = "订单编号：" + vbillcode + "已经存在，不允许重复提交！";
					return createMsg(false, "-1", errorMsg);
				}
				hVO.setVbillcode(vbillcode);
				if (headInfo.get("pk_org") == null) {
					errorMsg = "库存组织编码[pk_org]为空！";
					return createMsg(false, "-1", errorMsg);
				}
				String org_code = headInfo.get("pk_org").toString();
				String pk_org = (String) hypubBo.findColValue("org_stockorg",
						"pk_stockorg", "nvl(dr,0) = 0 and code = '" + org_code
								+ "'");
				OrgVO orgVO = (OrgVO) hypubBo.queryByPrimaryKey(OrgVO.class,
						pk_org);
				if (null == orgVO) {
					errorMsg = "调出库存组织编码：" + org_code + "在NC中不存在！";
					return createMsg(false, "-1", errorMsg);
				}
				hVO.setPk_org(pk_org);
				hVO.setPk_group(orgVO.getPk_group());
				hVO.setCoutfinanceorgid(pk_org);
				hVO.setCoutfinanceorgvid(orgVO.getPk_vid());
				// 数据库查询组织版本
				hVO.setPk_org_v(orgVO.getPk_vid());
				if (headInfo.get("cinstockorgid") == null) {
					errorMsg = "调入库存组织编码[cinstockorgid]为空！";
					return createMsg(false, "-1", errorMsg);
				}
				String cinstockorgid = headInfo.get("cinstockorgid").toString(); // 调入库存组织编码
				String pk_instockorg = (String) hypubBo.findColValue(
						"org_stockorg", "pk_stockorg",
						"nvl(dr,0) = 0 and code = '" + cinstockorgid + "'");
				OrgVO instockorgVO = (OrgVO) hypubBo.queryByPrimaryKey(
						OrgVO.class, pk_instockorg);
				if (null == instockorgVO) {
					errorMsg = "调入库存组织编码：" + cinstockorgid + "在NC中不存在！";
					return createMsg(false, "-1", errorMsg);
				}
				hVO.setCinstockorgid(pk_instockorg);
				hVO.setCinstockorgvid(instockorgVO.getPk_vid());
				hVO.setCinfinanceorgid(pk_instockorg);
				hVO.setCinfinanceorgvid(instockorgVO.getPk_vid());
				if (headInfo.get("dbilldate") == null) {
					errorMsg = "订单日期[dbilldate]为空！";
					return createMsg(false, "-1", errorMsg);
				}
				SimpleDateFormat timeFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date approvetime = timeFormat.parse(headInfo.get("dbilldate")
						.toString());
				InvocationInfoProxy.getInstance().setBizDateTime(
						approvetime.getTime());
				hVO.setDbilldate(new UFDate(headInfo.get("dbilldate")
						.toString()));
				hVO.setCreationtime(new UFDateTime(timeFormat.parse(headInfo
						.get("dbilldate").toString())));
				hVO.setDmakedate(new UFDate(timeFormat.parse(headInfo.get(
						"dbilldate").toString())));
				if (headInfo.get("coutdeptid") == null) {
					errorMsg = "调出部门编码[coutdeptid]为空！";
					return createMsg(false, "-1", errorMsg);
				}
				String coutdeptid = headInfo.get("coutdeptid").toString();
				String pk_outdeptid = (String) hypubBo.findColValue("ORG_DEPT",
						"pk_dept", "nvl(dr,0) = 0 and code = '" + coutdeptid
								+ "' and pk_org = '" + pk_org + "'");
				if (isEmpty(pk_outdeptid)) {
					errorMsg = "调出部门编码：" + coutdeptid + "在NC中不存在！";
					return createMsg(false, "-1", errorMsg);
				}
				String pk_outdeptid_v = (String) hypubBo.findColValue(
						"ORG_DEPT", "pk_vid", "nvl(dr,0) = 0 and code = '"
								+ coutdeptid + "' and pk_org = '" + pk_org
								+ "'");
				hVO.setCoutdeptid(pk_outdeptid);
				hVO.setCoutdeptvid(pk_outdeptid_v);
				if (headInfo.get("coutpsnid") == null) {
					errorMsg = "调出业务员编码[coutpsnid]为空！";
					return createMsg(false, "-1", errorMsg);
				}
				String coutpsnid = headInfo.get("coutpsnid").toString();
				String pk_coutpsnid = (String) hypubBo.findColValue("v_sw_psn",
						"pk_psndoc", "code = '" + coutpsnid
								+ "' and pk_org = '" + pk_org + "'");
				if (isEmpty(pk_coutpsnid)) {
					errorMsg = "调出业务员编码：" + coutpsnid + "在NC中不存在！";
					return createMsg(false, "-1", errorMsg);
				}
				hVO.setCoutpsnid(pk_coutpsnid);
				String vmemo = "";
				if (null != headInfo.get("vmemo")) {
					vmemo = headInfo.get("vmemo").toString();
				}
				hVO.setVnote(vmemo);
				if (headInfo.get("billmaker") == null) {
					errorMsg = "制单人编码[billmaker]为空！";
					return createMsg(false, "-1", errorMsg);
				}
				String billmaker = headInfo.get("billmaker").toString();
				cuserid = (String) hypubBo.findColValue("sm_user", "cuserid",
						"nvl(dr,0) = 0 and user_code = '" + billmaker + "'");
				if (isEmpty(cuserid)) {
					errorMsg = "制单人编码：" + billmaker + "在NC中不存在！";
					return createMsg(false, "-1", errorMsg);
				}
				hVO.setBillmaker(cuserid);
				hVO.setCreator(cuserid);
				hVO.setFioonwayownerflag(Integer.valueOf(OnWayOwner.OUT
						.toIntValue()));
				hVO.setFotonwayownerflag(Integer.valueOf(OnWayOwner.OUT
						.toIntValue()));
				hVO.setVtrantypecode("5X-Cxx-RD-96");
				String Ctrantypeid = (String) hypubBo.findColValue(
						"bd_billtype", "pk_billtypeid",
						"nvl(dr,0) = 0 and pk_billtypecode = '5X-Cxx-RD-96'");
				hVO.SetCtrantypeid(Ctrantypeid);
				hVO.setCorigcurrencyid("1002Z0100000000001K1"); // 币种
				String biztype = (String) hypubBo.findColValue("bd_busitype",
						"pk_busitype",
						"nvl(dr,0) = 0 and primarybilltype = '5X-Cxx-RD-96'");
				hVO.setCbiztypeid(biztype);
				// hVO.setStatus(2); // 审批通过
				// hVO.setApprover(cuserid);
				// hVO.setTaudittime(new UFDate());
				hVO.setConwayownerorgid(hVO.getCoutfinanceorgvid());
				hVO.setBioreverseflag(new UFBoolean("N"));
				hVO.setBotreverseflag(new UFBoolean("N"));
				billVO.setParentVO(hVO);

				// 表体
				List<Map<String, Object>> bodyInfo = (List) map.get("bill_b");
				List<BillItemVO> itemVOs = new ArrayList<BillItemVO>();
				for (int i = 0; i < bodyInfo.size(); i++) {
					Map<String, Object> bodyInfoItem = bodyInfo.get(i);
					BillItemVO itemVO = new BillItemVO();
					// 物料信息
					if (bodyInfoItem.get("vvendinventorycode") == null) {
						errorMsg = "物料编码[vvendinventorycode]为空！";
						return createMsg(false, "-1", errorMsg);
					}
					String vvendinventorycode = bodyInfoItem.get(
							"vvendinventorycode").toString();
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
					itemVO.setCinventoryid(pk_material);
					itemVO.setCinventoryvid(materialVO.getPk_source());

					/*
					 * MeasdocVO[] measdocVOs = (MeasdocVO[])
					 * hypubBo.queryByCondition(MeasdocVO.class,
					 * "nvl(dr,0) = 0 and pk_measdoc='" +
					 * materialVO.getPk_measdoc() + "'");
					 */
					String scalefactor = "1.00/1.00";
					/*
					 * if (null != measdocVOs && measdocVOs.length > 0) {
					 * MeasdocVO measdocVO = measdocVOs[0]; if
					 * (measdocVO.getScalefactor().getDouble() > 0) { //
					 * scalefactor = measdocVO.getScalefactor() + "/1.00"; } }
					 */
					itemVO.setCastunitid(materialVO.getPk_measdoc());
					itemVO.setCunitid(materialVO.getPk_measdoc());
					itemVO.setCqtunitid(materialVO.getPk_measdoc());
					itemVO.setVchangerate(scalefactor);
					itemVO.setVqtunitrate(scalefactor);
					String batch = "";
					if (null != bodyInfoItem.get("pk_batchcode")) {
						batch = bodyInfoItem.get("pk_batchcode").toString();
						String pk_batchcode = (String) hypubBo.findColValue(
								"scm_batchcode", "pk_batchcode",
								"nvl(dr,0) = 0 and vbatchcode='" + batch + "'");
						itemVO.setPk_batchcode(pk_batchcode);
						itemVO.setVbatchcode(batch);
					}
					UFDate date = new UFDate(bodyInfoItem.get("dplanarrvdate")
							.toString());
					itemVO.setDplanarrivedate(date);
					itemVO.setDrequiredate(date);
					itemVO.setDplanoutdate(date);
					itemVO.setStatus(2);

					String coutstordocid = bodyInfoItem.get("coutstordocid")
							.toString();
					String pk_stordoc = (String) hypubBo
							.findColValue(
									"bd_stordoc",
									"pk_stordoc",
									"nvl(dr,0) = 0 and code='"
											+ coutstordocid
											+ "' and pk_org in (select pk_stockorg from org_stockorg where code='"
											+ org_code + "' and dr=0)");
					if (isEmpty(pk_stordoc)) {
						errorMsg = "调出仓库：" + coutstordocid + "在NC中不存在！";
						return createMsg(false, "-1", errorMsg);
					}
					itemVO.setCoutstordocid(pk_stordoc);
					itemVO.setCtoutstordocid(pk_stordoc);
					String cinstordocid = bodyInfoItem.get("cinstordocid")
							.toString();
					String pk_instordoc = (String) hypubBo
							.findColValue(
									"bd_stordoc",
									"pk_stordoc",
									"nvl(dr,0) = 0 and code='"
											+ cinstordocid
											+ "' and pk_org in (select pk_stockorg from org_stockorg where code='"
											+ cinstockorgid + "' and dr=0)");
					if (isEmpty(pk_instordoc)) {
						errorMsg = "调入仓库：" + cinstordocid + "在NC中不存在！";
						return createMsg(false, "-1", errorMsg);
					}
					itemVO.setCinstordocid(pk_instordoc);
					if (itemVO.getNtaxrate() == null) {
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
					}
					String ciosettleruleid = (String) hypubBo.findColValue(
							"to_settlerule", "csettleruleid",
							"nvl(dr,0) = 0 and ctranstype ='5X-Cxx-RD-96'");
					if (StringUtils.isEmpty(ciosettleruleid)) {
						errorMsg = "获取内部结算主表规则为空！";
						return createMsg(false, "-1", errorMsg);
					}
					String ciosettlerule_bid = (String) hypubBo.findColValue(
							"to_settlerule_b", "csettlerule_bid",
							"nvl(dr,0) = 0 and csettleruleid ='"
									+ ciosettleruleid + "'");
					itemVO.setCiosettleruleid(ciosettleruleid);// 调入调出结算规则
					if (StringUtils.isEmpty(ciosettlerule_bid)) {
						errorMsg = "获取内部结算子表规则为空！";
						return createMsg(false, "-1", errorMsg);
					}
					itemVO.setCiosettlerule_bid(ciosettlerule_bid);// 调入调出结算规则明细

					UFDouble nastnum = new UFDouble(bodyInfoItem.get("nastnum")
							.toString());
					UFDouble ntaxmny = new UFDouble(bodyInfoItem.get(
							"ntotalorigmny").toString());
					itemVO.setNnum(nastnum);
					itemVO.setNastnum(nastnum);
					itemVO.setNqtunitnum(nastnum);
					// itemVO.setNorigtaxmny(ntaxmny);
					// itemVO.setNorigmny(ntaxmny);
					itemVO.setCrowno((i + 1) * 10 + "");
					itemVO.setBiolargessflag(new UFBoolean(false));
					// itemVO.setNoutnum(nastnum);
					// itemVO.setNinnum(nastnum);
					if (itemVO.getNtaxrate() != null) {
						double mny = Double.parseDouble(itemVO.getNtaxrate()
								.toString()) / 100 + 1;
						itemVO.setNorigmny(ntaxmny);
						itemVO.setNorigtaxmny(ntaxmny.multiply(mny).setScale(2,
								UFDouble.ROUND_HALF_UP));
					}
					itemVOs.add(itemVO);
				}
				BillItemVO[] itemvos = itemVOs.toArray(new BillItemVO[bodyInfo
						.size()]);
				billVO.setChildrenVO(itemvos);
				orderVOs.add(billVO);
			}

			vos = orderVOs.toArray(new BillVO[paramList.size()]);

			checkVO(vos);
			FillRefForPushUtilsPub pubUtil = new FillRefForPushUtilsPub();
			pubUtil.fillDefaultData(vos);
			pubUtil.fillDataForExt(vos);
			// BillSaveForOuterUtils util = new BillSaveForOuterUtils();
			// util.fillDefaultValue(vos);
			// M5XVOBusiRuleUtil util = new M5XVOBusiRuleUtil();
			// BillVO ruleVO = vos[0];
			// util.calculateOnWayOnwer(ruleVO);
			// vos[0] = ruleVO;
			calPriceMny(vos);
			// BillVO[] saveArr = (BillVO[])
			// PfServiceScmUtil.processBatch("WRITE",
			// TOBillType.TransOrder.getCode(), vos, null, null);

			// IplatFormEntry pIplatFormEntry =
			// NCLocator.getInstance().lookup(IplatFormEntry.class);
			// for (BillVO clientBill : vos) {
			// String billmaker = clientBill.getParentVO().getBillmaker();
			// InvocationInfoProxy.getInstance().setUserId(clientBill.getParentVO().getBillmaker());
			// clientBill.getParentVO().setApprover( null);
			// clientBill.getParentVO().setTaudittime(null);
			// IPFBusiAction ipFBusiActiona =
			// NCLocator.getInstance().lookup(IPFBusiAction.class);
			// BillVO[] saveVo = (BillVO[])
			// ipFBusiActiona.processAction("WRITE",
			// TOBillType.TransOrder.getCode(), null,
			// clientBill, null, null);
			//
			// // // 提交
			// // BillVO[] submitVO = (BillVO[])
			// pIplatFormEntry.processAction("SAVE",
			// TOBillType.TransOrder.getCode(), null, clientBill,
			// // null, null);
			//
			// // saveVo[0].getParentVO().setApprover(billmaker);
			// // saveVo[0].getParentVO().setTaudittime(new UFDate());
			// // BillItemVO[] itemVOs = saveVo[0].getChildrenVO();
			// // for (int j = 0; j < itemVOs.length; j++) {
			// // //itemVOs[j].setBotsettleendflag(new UFBoolean("Y"));
			// // //itemVOs[j].setBoutendflag(new UFBoolean("Y"));
			// // itemVOs[j].setNoutnum(itemVOs[j].getNnum());
			// // itemVOs[j].setNinnum(itemVOs[j].getNnum());
			// // }
			// // saveVo[0].setChildrenVO(itemVOs);
			//
			// HashMap hmPfExParams = new HashMap();
			// WorkflownoteVO worknoteVO = ((IWorkflowMachine)
			// NCLocator.getInstance().lookup(IWorkflowMachine.class))
			// .checkWorkFlow("APPROVE", "5X", saveVo[0], hmPfExParams);
			// if (worknoteVO != null) {
			// worknoteVO.setChecknote("批准");
			// worknoteVO.setApproveresult("Y");
			// }
			// // 审核
			// HashMap<String, Object> eParam = new HashMap<String, Object>();
			// eParam.put("notechecked", "notechecked");
			// IPFBusiAction ipFBusiAction =
			// NCLocator.getInstance().lookup(IPFBusiAction.class);
			// ipFBusiAction.processAction("APPROVE", "5X", null, saveVo[0],
			// null, eParam);
			// // pIplatFormEntry.processAction("APPROVE", "5X",
			// // null, saveVo[0], null, null);
			//
			// }
			// ITransOrderMaintain itom =
			// NCLocator.getInstance().lookup(ITransOrderMaintain.class);
			// itom.insertTransOrder(vos);
			// 审核
			InvocationInfoProxy.getInstance().setUserId(cuserid);
			HashMap<String, Object> eParam = new HashMap<String, Object>();
			eParam.put("notechecked", "notechecked");
			IPFBusiAction ipFBusiAction = NCLocator.getInstance().lookup(
					IPFBusiAction.class);
			// BillItemVO[] itemVOs = vos[0].getChildrenVO();
			// for (int j = 0; j < itemVOs.length; j++) {
			// //itemVOs[j].setBotsettleendflag(new UFBoolean("Y"));
			// //itemVOs[j].setBoutendflag(new UFBoolean("Y"));
			// itemVOs[j].setNoutnum(itemVOs[j].getNnum());
			// itemVOs[j].setNinnum(itemVOs[j].getNnum());
			// }
			// vos[0].setChildrenVO(itemVOs);
			ipFBusiAction.processAction("WRITE", "5X", null, vos[0], null,
					eParam);
			ipFBusiAction.processAction("APPROVE", "5X", null, vos[0], null,
					eParam);
			errorMsg = "上传成功！";
			return createMsg(true, "0", errorMsg);
		} catch (Exception e) {
			// ITransOrderMaintain itom =
			// NCLocator.getInstance().lookup(ITransOrderMaintain.class);
			// 如果报错，则删除已经保存的单据
			// itom.deleteTransOrder(vos);
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
		Debug.error("上传调拨订单返回结果：" + isSuccess + "; 返回信息：" + msg);
		return JSON.toJSONString(msgMap);
	}

	private static boolean isEmpty(String str) {
		return (str == null || str.length() == 0 || "null".equals(str));
	}

	private void calPriceMny(BillVO[] vos) {
		M5XBSNumPriceMnyUtils m5xmnyUtils = new M5XBSNumPriceMnyUtils();
		for (BillVO bill : vos) {
			BillHeaderVO billHVO = bill.getParentVO();
			BillItemVO[] billBVO = bill.getChildrenVO();
			for (BillItemVO bvo : billBVO) {
				if (null != bvo.getNorigtaxmny()) {
					m5xmnyUtils.calculateNumPriceMny(billHVO, bvo,
							"norigtaxmny");
				} else if (null != bvo.getNqtorigtaxnetprc()) {
					m5xmnyUtils.calculateNumPriceMny(billHVO, bvo,
							"nqtorigtaxnetprc");
				}
			}
		}
	}

	private void checkVO(BillVO[] vos) {
		BillNullCheck<BillVO> check = new BillNullCheck<BillVO>();
		String[] headKeys = { "dbilldate", "cbiztypeid", "ctrantypeid",
				"cinstockorgvid", "pk_org", "corigcurrencyid" };

		String[] itemKeys = { "crowno", "cinventoryvid", "castunitid",
				"nastnum", "dplanoutdate", "dplanarrivedate", "drequiredate" };

		for (String key : headKeys) {
			check.add(new AttributeCoordinate(BillHeaderVO.class, key));
		}
		for (String key : itemKeys) {
			check.add(new AttributeCoordinate(BillItemVO.class, key));
		}
		check.check(vos);

		for (BillVO vo : vos) {
			BillHeaderVO head = vo.getParentVO();
			head.setStatus(2);
			head.setFioonwayownerflag(Integer.valueOf(OnWayOwner.OUT
					.toIntValue()));
			head.setFotonwayownerflag(Integer.valueOf(OnWayOwner.OUT
					.toIntValue()));
			BillItemVO[] items = vo.getChildrenVO();
			for (BillItemVO item : items)
				item.setStatus(2);
		}
	}

}