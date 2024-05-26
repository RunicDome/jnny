package nc.impl.pu.m21;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.pub.compiler.AbstractCompiler2;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pu.m21.action.OrderApproveAction;
import nc.impl.pu.m21.action.OrderSendApproveAction;
import nc.impl.pu.m21.action.OrderUnArppoveAction;
import nc.impl.pu.m21.action.OrderUnSendApproveAction;
import nc.itf.pu.m21.IOrderApprove;
import nc.vo.bd.material.MaterialVO;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.bd.supplier.SupplierVO;
import nc.vo.org.OrgVO;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pu.m21.entity.OrderHeaderVO;
import nc.vo.pu.m21.entity.OrderItemVO;
import nc.vo.pu.m21.entity.OrderVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
// 采购订单
public class OrderAppoveImpl implements IOrderApprove {
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表明

	public OrderAppoveImpl() {
	}

	public OrderVO[] approve(OrderVO[] vos, AbstractCompiler2 script)
			throws BusinessException {
		try {
			return new OrderApproveAction().approve(vos, script);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public OrderVO[] sendapprove(OrderVO[] vos, AbstractCompiler2 script)
			throws BusinessException {
		try {
			OrderVO[] orderVO = new OrderSendApproveAction().sendApprove(vos,
					script);
			senOaData(orderVO);
			return orderVO;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// TODO 单据同步OA功能 start create by zwh
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

	private void senOaData(OrderVO[] billVOs) throws BusinessException {
		for (OrderVO temp : billVOs) {
			// 采购订单
			OrderHeaderVO hVO = temp.getHVO();
			if ((temp.getHVO().getVtrantypecode()).contains("21")) {
				if ("4".equals(getDef2(temp.getHVO().getPk_org()))) {
					OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(
							OrgVO.class, hVO.getPk_org());
					if ("0001A21000000001ZBFB".equals(orgVO.getPk_org())
							|| "0001A21000000001ZBFB".equals(orgVO
									.getPk_fatherorg())) {
						if (hVO.getBreturn().booleanValue()) {
							OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(
									hVO.getPk_org(), "21");
							if (oaVo != null && oaVo.getIsdr() == 0) {
								WorkFId = oaVo.getFlowid();
								TableName = oaVo.getTablename();
								// 获取主表数据
								JSONArray headData = getNewMainMap(temp
										.getHVO());
								// 获取子表数据
								JSONArray bodyData = getNewDtaileDataMap(temp);
								// 构造workflow信息
								OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
								workFlowVO.setPrimaryKey(temp.getPrimaryKey());
								workFlowVO.setPkGroup(hVO.getPk_group());
								workFlowVO.setPkOrg(hVO.getPk_org());
								workFlowVO.setBillMaker(hVO.getBillmaker());
								workFlowVO.setCreator(hVO.getCreator());
								workFlowVO.setBillCode("21");
								// 测试临时写死，后期改为接口获取
								workFlowVO.setWorkflowId(OaWorkFlowUtil
										.getOAFlowID(WorkFId));
								workFlowVO.setWorkflowName("采购订单");
								WorkFlowBill bill = OaWorkFlowUtil
										.getWorkFlowBill(workFlowVO);
								bill.setDef3("ZT");
								// 调用OA工具类同步数据至OA
								// 制单人身份证号
								UserVO userVO = (UserVO) getHyPubBO()
										.queryByPrimaryKey(UserVO.class,
												temp.getHVO().getBillmaker());
								String idCard = (String) getHyPubBO()
										.findColValue(
												"bd_psndoc",
												"id",
												"nvl(dr,0) = 0 and pk_psndoc='"
														+ userVO.getPk_psndoc()
														+ "'");
								bill.setDef5(idCard);
								OaWorkFlowUtil.sendOaData(headData, bodyData,
										bill);
							}
						}
					} else {
						if ("21-Cxx-RD-96".equals(hVO.getVtrantypecode())
								&& ("0001A21000000001ZBF5".equals(orgVO
										.getPk_org()) || "0001A21000000001ZBF5"
										.equals(orgVO.getPk_fatherorg()))) {
							return;
						} else {
							OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(
									hVO.getPk_org(), "21");
							if (oaVo.getIsdr() == 0) {
								WorkFId = oaVo.getFlowid();
								TableName = oaVo.getTablename();
								// 获取主表数据
								JSONArray headData = getNewMainMap(temp
										.getHVO());
								// 获取子表数据
								JSONArray bodyData = getNewDtaileDataMap(temp);
								// 构造workflow信息
								OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
								workFlowVO.setPrimaryKey(temp.getPrimaryKey());
								workFlowVO.setPkGroup(hVO.getPk_group());
								workFlowVO.setPkOrg(hVO.getPk_org());
								workFlowVO.setBillMaker(hVO.getBillmaker());
								workFlowVO.setCreator(hVO.getCreator());
								workFlowVO.setBillCode("21");
								// 测试临时写死，后期改为接口获取
								workFlowVO.setWorkflowId(OaWorkFlowUtil
										.getOAFlowID(WorkFId));
								workFlowVO.setWorkflowName("采购订单");
								WorkFlowBill bill = OaWorkFlowUtil
										.getWorkFlowBill(workFlowVO);
								bill.setDef3("ZT");
								// 调用OA工具类同步数据至OA
								// 制单人身份证号
								UserVO userVO = (UserVO) getHyPubBO()
										.queryByPrimaryKey(UserVO.class,
												temp.getHVO().getBillmaker());
								String idCard = (String) getHyPubBO()
										.findColValue(
												"bd_psndoc",
												"id",
												"nvl(dr,0) = 0 and pk_psndoc='"
														+ userVO.getPk_psndoc()
														+ "'");
								bill.setDef5(idCard);
								OaWorkFlowUtil.sendOaData(headData, bodyData,
										bill);
							}
						}
					}
				} /*
				 * else { JSONArray headData = getMainMap(temp.getHVO());
				 * JSONArray bodyData = getDtaileDataMap(temp);
				 * OaWorkFlowUtil.sendOaData(headData, bodyData,
				 * getWorkFlowBill(temp)); }
				 */
			}
		}
	}

	private JSONArray getMainMap(OrderHeaderVO parentVO)
			throws BusinessException {
		List list = new ArrayList();
		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				parentVO.getPk_org());
		if (null != orgVO) {
			// Map szgs =OaWorkFlowUtil.listAdd("szgs",orgVO.getCode() );
			// list.add(szgs);
		}
		String shortname = (String) getHyPubBO().findColValue(
				"org_purchaseorg",
				"shortname ",
				"nvl(dr,0) = 0 and   pk_purchaseorg ='" + parentVO.getPk_org()
						+ "'");
		Map cgzz = OaWorkFlowUtil.listAdd("cgzz", shortname);
		list.add(cgzz);
		String billtypename = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and pk_billtypeid  = '"
						+ parentVO.getCtrantypeid() + "'");
		Map ddlx = OaWorkFlowUtil.listAdd("ddlx", billtypename);
		list.add(ddlx);
		String where = " pk_defdoc = '"
				+ parentVO.getVdef15()
				+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'cgfs' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
		String def5 = "0";
		if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
				where)) {
			def5 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
					where);
		}
		Map cgfs = OaWorkFlowUtil.listAdd("cgfs", def5);
		list.add(cgfs);
		Map ddbh = OaWorkFlowUtil.listAdd("ddbh", parentVO.getVbillcode());
		list.add(ddbh);
		String str6 = "0";
		if (null != parentVO.getVcoopordercode()) {
			str6 = parentVO.getVcoopordercode();
		}
		Map dfdjh = OaWorkFlowUtil.listAdd("dfdjh", str6);
		list.add(dfdjh);
		Map ddrq = OaWorkFlowUtil.listAdd("ddrq", parentVO.getDbilldate()
				.getYear()
				+ "-"
				+ parentVO.getDbilldate().getStrMonth()
				+ "-"
				+ parentVO.getDbilldate().getStrDay());
		list.add(ddrq);
		SupplierVO supplierVO = (SupplierVO) getHyPubBO().queryByPrimaryKey(
				SupplierVO.class, parentVO.getPk_supplier());
		String name = "0";
		if (null != supplierVO.getPk_supplier()) {
			name = supplierVO.getPk_supplier();
		}
		Map gys = OaWorkFlowUtil.listAdd("gys", name);
		list.add(gys);
		Map kpgys = OaWorkFlowUtil.listAdd("kpgys",
				parentVO.getPk_invcsupllier());
		list.add(kpgys);
		String mname = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc  = '" + parentVO.getCemployeeid()
						+ "'");
		Map cgy = OaWorkFlowUtil.listAdd("cgy", mname);
		list.add(cgy);
		String dname = (String) getHyPubBO()
				.findColValue(
						"org_dept_v",
						"name",
						"nvl(dr,0) = 0 and pk_vid  = '"
								+ parentVO.getPk_dept_v() + "'");
		Map cgbm = OaWorkFlowUtil.listAdd("cgbm", dname);
		list.add(cgbm);
		Map zsl = OaWorkFlowUtil.listAdd("zsl", parentVO.getNtotalastnum()
				.toString());
		list.add(zsl);
		Map jshj = OaWorkFlowUtil.listAdd("jshj", parentVO.getNtotalorigmny()
				.toString());
		list.add(jshj);
		Map djzt = OaWorkFlowUtil.listAdd("djzt", parentVO.getForderstatus()
				.toString());
		list.add(djzt);
		Map bz = OaWorkFlowUtil.listAdd("bz", parentVO.getVmemo());
		list.add(bz);
		Map yxzh = OaWorkFlowUtil.listAdd("yxzh", parentVO.getPk_bankdoc());
		list.add(yxzh);
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		Map zdrzj = new HashMap();
		zdrzj.put("fieldName", "zdrzj");
		zdrzj.put("fieldValue", userVO.getUser_code());
		list.add(zdrzj);
		Map sfzh = new HashMap();
		String id = (String) getHyPubBO().findColValue("bd_psndoc", "id",
				"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
		if (null == id) {
			throw new BusinessException("制单人身份证号码未维护");
		}
		sfzh.put("fieldName", "sfzh");
		sfzh.put("fieldValue", id);
		list.add(sfzh);
		Map zdrmc = new HashMap();
		zdrmc.put("fieldName", "zdrmc");
		zdrmc.put("fieldValue", userVO.getUser_name());
		list.add(zdrmc);
		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	private WorkFlowBill getWorkFlowBill(OrderVO temp) throws BusinessException {
		String where = " nvl(dr,0) = 0 and pk_bill = '" + temp.getPrimaryKey()
				+ "'";
		WorkFlowBill[] workFlowBills = (WorkFlowBill[]) getHyPubBO()
				.queryByCondition(WorkFlowBill.class, where);
		WorkFlowBill workFlowBill = new WorkFlowBill();
		if (null != workFlowBills && workFlowBills.length > 1) {
			throw new BusinessException("查询到多条流程记录");
		}
		if (null != workFlowBills && workFlowBills.length == 1) {
			workFlowBill = workFlowBills[0];
			workFlowBill.setWorkflowName("采购订单");
		} else {
			workFlowBill.setWorkflowId("42");
			UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
					UserVO.class, temp.getHVO().getBillmaker());
			workFlowBill.setDef5(userVO.getUser_code());
			if ("2".equals(getDef2(temp.getHVO().getPk_org()))) {
				PsndocVO psndoc = (PsndocVO) getHyPubBO().queryByPrimaryKey(
						PsndocVO.class, userVO.getPk_psndoc());
				workFlowBill.setDef5(psndoc.getId());
				workFlowBill.setWorkflowId("0");
			}
			workFlowBill.setPk_group(temp.getHVO().getPk_group());
			workFlowBill.setPk_org(temp.getHVO().getPk_org());
			workFlowBill.setPk_bill(temp.getPrimaryKey());
			workFlowBill.setUser_name(userVO.getUser_name());
			workFlowBill.setUser_code(userVO.getUser_code());
			workFlowBill.setCreationtime(new UFDate().toStdString());
			workFlowBill.setCreator(temp.getHVO().getCreator());
			workFlowBill.setBill_code("21");
			workFlowBill.setBill_status("1");
			workFlowBill.setWorkflowName("采购订单");
		}
		return workFlowBill;
	}

	public String getDef2(String pk_org) throws BusinessException {
		String def2 = "";
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		if (null != orgVO) {
			def2 = orgVO.getDef2();
		}
		return def2;
	}

	private JSONArray getDtaileDataMap(OrderVO temp) throws BusinessException {
		Map orderMap = getOrderBody(temp.getBVO());
		JSONArray dtlistString = JSONArray.fromObject(orderMap);
		return dtlistString;
	}

	private Map getOrderBody(OrderItemVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		dtMap.put("tableDBName", "formtable_main_45_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (OrderItemVO temp : bvo) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();
			Map xh = OaWorkFlowUtil.listAdd("xh", temp.getCrowno());
			workflowRequestTableFields.add(xh);
			Map wlbm = OaWorkFlowUtil.listAdd("wlbm", temp.getPk_material());
			workflowRequestTableFields.add(wlbm);
			MaterialVO materialVO = (MaterialVO) getHyPubBO()
					.queryByPrimaryKey(MaterialVO.class, temp.getPk_material());
			if (null != materialVO) {
				Map wlmc = OaWorkFlowUtil.listAdd("wlmc", materialVO.getName());
				workflowRequestTableFields.add(wlmc);
				Map gg = OaWorkFlowUtil.listAdd("gg",
						materialVO.getMaterialspec());
				workflowRequestTableFields.add(gg);
				String str4 = "0";
				if (null != materialVO.getMaterialtype()) {
					str4 = materialVO.getMaterialtype();
				}
				Map xh1 = OaWorkFlowUtil.listAdd("xh1", str4);
				workflowRequestTableFields.add(xh1);
			}
			Map dw = OaWorkFlowUtil.listAdd("dw", temp.getCastunitid());
			workflowRequestTableFields.add(dw);
			Map xm = OaWorkFlowUtil.listAdd("xm", temp.getCprojectid());
			workflowRequestTableFields.add(xm);
			String project_name = (String) getHyPubBO().findColValue(
					"bd_project",
					"project_name",
					"nvl(dr,0) = 0 and   pk_project   = '"
							+ temp.getCprojectid() + "'");
			Map xmmc = OaWorkFlowUtil.listAdd("xmmc", project_name);
			workflowRequestTableFields.add(xmmc);
			Map sl = OaWorkFlowUtil.listAdd("sl", temp.getNastnum().toString());
			workflowRequestTableFields.add(sl);
			Map wsdj = OaWorkFlowUtil.listAdd("wsdj", temp.getNqtorigprice()
					.toString());
			workflowRequestTableFields.add(wsdj);
			Map hsdj = OaWorkFlowUtil.listAdd("hsdj", temp.getNqtorigtaxprice()
					.toString());
			workflowRequestTableFields.add(hsdj);
			Map wsje = OaWorkFlowUtil.listAdd("wsje", temp.getNorigmny()
					.toString());
			workflowRequestTableFields.add(wsje);
			Map jshj = OaWorkFlowUtil.listAdd("jshj", temp.getNorigtaxmny()
					.toString());
			workflowRequestTableFields.add(jshj);
			Map jcbje = OaWorkFlowUtil.listAdd("jcbje", temp.getNcalcostmny()
					.toString());
			workflowRequestTableFields.add(jcbje);
			Map sm = OaWorkFlowUtil.listAdd("sm", temp.getCtaxcodeid());
			workflowRequestTableFields.add(sm);
			Map sl1 = OaWorkFlowUtil.listAdd("sl1", temp.getNtaxrate()
					.toString());
			workflowRequestTableFields.add(sl1);
			Map se = OaWorkFlowUtil.listAdd("se", temp.getNtax().toString());
			workflowRequestTableFields.add(se);
			Map yfzz = OaWorkFlowUtil
					.listAdd("yfzz", temp.getPk_apfinanceorg());
			workflowRequestTableFields.add(yfzz);
			String str = "0";
			if (null != temp.getCcontractrowid()) {
				str = temp.getCcontractrowid();
			}
			Map htmx = OaWorkFlowUtil.listAdd("htmx", str);
			workflowRequestTableFields.add(htmx);
			String str1 = "0";
			if (null != temp.getCcontractid()) {
				str1 = temp.getCcontractid();
			}
			Map htxx = OaWorkFlowUtil.listAdd("htxx", str1);
			workflowRequestTableFields.add(htxx);
			String str2 = "0";
			if (null != temp.getVcontractcode()) {
				str2 = temp.getVcontractcode();
			}
			Map hth = OaWorkFlowUtil.listAdd("hth", str2);
			workflowRequestTableFields.add(hth);
			String str3 = "0";
			if (null != temp.getVbmemo()) {
				str3 = temp.getVbmemo();
			}
			Map bz = OaWorkFlowUtil.listAdd("bz", str3);
			workflowRequestTableFields.add(bz);
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	// 封装主表数据
	private JSONArray getNewMainMap(OrderHeaderVO parentVO)
			throws BusinessException {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// ---------其他字段begin
		// 组织名称
		String shortname = (String) getHyPubBO().findColValue(
				"org_purchaseorg",
				"name",
				"nvl(dr,0) = 0 and pk_purchaseorg ='" + parentVO.getPk_org()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("zzmc", shortname));
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		// 订单类型名称
		String billTypeName = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and pk_billtypeid  = '"
						+ parentVO.getCtrantypeid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("typename", billTypeName));
		// 采购员
		String mname = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc  = '" + parentVO.getCemployeeid()
						+ "'");
		Map cgy = OaWorkFlowUtil.listAdd("cgy", mname);
		list.add(cgy);
		// 采购部门
		String dname = (String) getHyPubBO()
				.findColValue(
						"org_dept_v",
						"name",
						"nvl(dr,0) = 0 and pk_vid  = '"
								+ parentVO.getPk_dept_v() + "'");
		Map cgbm = OaWorkFlowUtil.listAdd("cgbm", dname);
		// 采购类型
		if (parentVO.getVdef9() != null) {
			String cglxbmstr = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getVdef9()
							+ "'");
			Map cglxbm = OaWorkFlowUtil.listAdd("cglxbm", cglxbmstr);
			list.add(cglxbm);
			String cglxstr = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getVdef9()
							+ "'");
			Map cglx = OaWorkFlowUtil.listAdd("cglx", cglxstr);
			list.add(cglx);
		}

		// 供应商名称
		SupplierVO supplierVO = (SupplierVO) getHyPubBO().queryByPrimaryKey(
				SupplierVO.class, parentVO.getPk_supplier());
		String name = "";
		if (null != supplierVO.getPk_supplier()) {
			name = supplierVO.getName();
		}
		Map gys = OaWorkFlowUtil.listAdd("pk_supplier_name", name);
		list.add(gys);
		// ---固定字段
		// 制单人用户编码
		list.add(OaWorkFlowUtil.listAddObj("usercode", userVO.getUser_code()));
		// 制单人名称
		list.add(OaWorkFlowUtil.listAddObj("username", userVO.getUser_name()));
		// 制单人身份证号
		String idCard = (String) getHyPubBO().findColValue("bd_psndoc", "id",
				"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
		if (null == idCard) {
			throw new BusinessException("制单人身份证号码未维护");
		}
		list.add(OaWorkFlowUtil.listAddObj("userid", idCard));
		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	// 封装子表数据
	private JSONArray getNewDtaileDataMap(OrderVO temp)
			throws BusinessException {
		Map bodyMap = getBody(temp.getBVO());
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
		return dtlistString;
	}

	private Map getBody(OrderItemVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (OrderItemVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			MaterialVO materialVO = (MaterialVO) getHyPubBO()
					.queryByPrimaryKey(MaterialVO.class, temp.getPk_material());
			if (null != materialVO) {
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
			// 主单位
			String zdwstr = "0";
			if (null != temp.getCunitid()) {
				zdwstr = (String) getHyPubBO().findColValue(
						"bd_measdoc",
						"name",
						"nvl(dr,0) = 0 and pk_measdoc  = '" + temp.getCunitid()
								+ "'");
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("zdw",
					zdwstr));
			// 单位
			String dwstr = "";
			if (null != temp.getCastunitid()) {
				dwstr = (String) getHyPubBO().findColValue(
						"bd_measdoc",
						"name",
						"nvl(dr,0) = 0 and pk_measdoc  = '"
								+ temp.getCastunitid() + "'");
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("dw",
					dwstr));

			if (null != temp.getCprojectid()) {
				// 项目
				ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
						.queryByPrimaryKey(ProjectHeadVO.class,
								temp.getCprojectid());
				// 项目编码
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bproject_code", projectVO.getProject_code()));
				// 项目名称
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bproject_name", projectVO.getProject_name()));
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

	public OrderVO[] unapprove(OrderVO[] vos, AbstractCompiler2 script)
			throws BusinessException {
		try {
			return new OrderUnArppoveAction().unapprove(vos, script);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public OrderVO[] unSendapprove(OrderVO[] vos, AbstractCompiler2 script)
			throws BusinessException {
		try {
			OrderVO[] orderVO = new OrderUnSendApproveAction().unSendApprove(
					vos, script);
			unOaCommit(orderVO);
			return orderVO;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	private void unOaCommit(OrderVO[] aggVO) throws BusinessException {
		for (OrderVO temp : aggVO) {
			if ((temp.getHVO().getVtrantypecode()).contains("21")) {
				OaWorkFlowUtil.backOaWorkFlow(temp.getPrimaryKey());
			}
		}
	}
}
