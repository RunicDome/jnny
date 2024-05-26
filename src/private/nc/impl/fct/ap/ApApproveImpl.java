package nc.impl.fct.ap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nc.bs.framework.common.NCLocator;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.bs.trade.business.HYPubBO;
import nc.impl.fct.ap.action.ApApproveAction;
import nc.impl.fct.ap.action.ApFreezeAction;
import nc.impl.fct.ap.action.ApModifyAction;
import nc.impl.fct.ap.action.ApSendApproveAction;
import nc.impl.fct.ap.action.ApTerminateAction;
import nc.impl.fct.ap.action.ApUnApproveAction;
import nc.impl.fct.ap.action.ApUnFreezeAction;
import nc.impl.fct.ap.action.ApUnTerminateAction;
import nc.impl.fct.ap.action.ApUnValidateAction;
import nc.impl.fct.ap.action.ApUnsendApprove;
import nc.impl.fct.ap.action.ApValidateAction;
import nc.itf.fct.ap.IApApprove;
import nc.itf.portal.IGetIntoPortal;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.bd.supplier.SupplierVO;
import nc.vo.fct.ap.entity.AggCtApVO;
import nc.vo.fct.ap.entity.CtApBVO;
import nc.vo.fct.ap.entity.CtApPlanVO;
import nc.vo.fct.ap.entity.CtApTermVO;
import nc.vo.fct.ap.entity.CtApVO;
import nc.vo.org.FinanceOrgVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pflow.PfUserObject;
import nc.vo.sm.UserVO;
import nc.vo.vorg.DeptVersionVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

// 付款合同
@SuppressWarnings({ "unchecked", "unused", "rawtypes", "restriction" })
public class ApApproveImpl implements IApApprove {
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表名

	public ApApproveImpl() {
	}

	public AggCtApVO[] approve(AggCtApVO[] vos, AbstractCompiler2 script)
			throws BusinessException {
		try {
			return new ApApproveAction().approve(vos, script);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtApVO[] freeze(AggCtApVO[] vos) throws BusinessException {
		try {
			return new ApFreezeAction().freeze(vos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtApVO[] modify(AggCtApVO[] vos, PfUserObject userConfirm)
			throws BusinessException {
		try {
			return new ApModifyAction().modify(vos, userConfirm);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtApVO[] sendapprove(AggCtApVO[] vos) throws BusinessException {
		try {
			AggCtApVO[] aggVO = new ApSendApproveAction().sendapprove(vos);
			for (AggCtApVO temp : aggVO) {
				CtApVO hvo = temp.getParentVO();
				String type = hvo.getCbilltypecode();// 单据类型
				String transi_type = hvo.getVtrantypecode();// 交易类型
				String ifbl = hvo.getVdef15();
				if (ifbl == null || "1001A2100000000B68C3".equals(ifbl)) {
					if ("4".equals(getDef2(hvo.getPk_org()))) {
						// 中台
						OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(
								hvo.getPk_org(), transi_type);
						// ----------------------
						if (oaVo != null && oaVo.getIsdr() == 0) {// 判断流程是否开启
							WorkFId = oaVo.getFlowid();
							TableName = oaVo.getTablename();
							// 获取主表数据
							JSONArray headData = getZTMainMap(hvo);
							// 获取子表数据
							JSONArray bodyData = getZTDtaileDataMap(temp);
							// 构造workflow信息
							OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
							workFlowVO.setPrimaryKey(temp.getPrimaryKey());
							workFlowVO.setPkGroup(hvo.getPk_group());
							workFlowVO.setPkOrg(hvo.getPk_org());
							workFlowVO.setBillMaker(hvo.getBillmaker());
							workFlowVO.setCreator(hvo.getCreator());
							workFlowVO.setBillCode(type);// 单据类型
							workFlowVO.setWorkflowId(OaWorkFlowUtil
									.getOAFlowID(WorkFId));
							workFlowVO.setWorkflowName(oaVo.getBilltypename());// 单据名称
							WorkFlowBill bill = OaWorkFlowUtil
									.getWorkFlowBill(workFlowVO);
							bill.setDef3("ZT");
							bill.setDef4(hvo.getVbillcode());// 单据编号
							// 制单人身份证号
							UserVO userVO = (UserVO) getHyPubBO()
									.queryByPrimaryKey(UserVO.class,
											hvo.getBillmaker());
							String idCard = (String) getHyPubBO().findColValue(
									"bd_psndoc",
									"id",
									"nvl(dr,0) = 0 and pk_psndoc='"
											+ userVO.getPk_psndoc() + "'");
							bill.setDef5(idCard);
							// 调用OA工具类同步数据至OA
							OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
						}
					} else {
						if ("2".equals(getDef2(hvo.getPk_org()))
								|| "1".equals(getDef2(hvo.getPk_org()))) {
							OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(
									hvo.getPk_org(), transi_type);
							// ----------------------
							if (oaVo != null && oaVo.getIsdr() == 0) {// 判断流程是否开启
								WorkFId = oaVo.getFlowid();
								TableName = oaVo.getTablename();
								// 获取主表数据
								JSONArray headData = getZTMainMap(hvo);
								// 获取子表数据
								JSONArray bodyData = getZTDtaileDataMap(temp);
								// 构造workflow信息
								OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
								workFlowVO.setPrimaryKey(temp.getPrimaryKey());
								workFlowVO.setPkGroup(hvo.getPk_group());
								workFlowVO.setPkOrg(hvo.getPk_org());
								workFlowVO.setBillMaker(hvo.getBillmaker());
								workFlowVO.setCreator(hvo.getCreator());
								workFlowVO.setBillCode(type);// 单据类型
								workFlowVO.setWorkflowId(OaWorkFlowUtil
										.getOAFlowID(WorkFId));
								workFlowVO.setWorkflowName(oaVo
										.getBilltypename());// 单据名称
								WorkFlowBill bill = OaWorkFlowUtil
										.getWorkFlowBill(workFlowVO);
								bill.setDef3("ZT");
								bill.setDef4(hvo.getVbillcode());// 单据编号
								// 制单人身份证号
								UserVO userVO = (UserVO) getHyPubBO()
										.queryByPrimaryKey(UserVO.class,
												hvo.getBillmaker());
								String idCard = (String) getHyPubBO()
										.findColValue(
												"bd_psndoc",
												"id",
												"nvl(dr,0) = 0 and pk_psndoc='"
														+ userVO.getPk_psndoc()
														+ "'");
								bill.setDef5(idCard);
								// 调用OA工具类同步数据至OA
								OaWorkFlowUtil.sendOaData(headData, bodyData,
										bill);
							} else {
								senOaData(aggVO);
							}
						}
					}
				}
				// 工程集团及下属单位。付款合同进入共享审批
				convertJKBXVO(temp);
			}

			return aggVO;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 工程集团及下属单位。付款合同进入共享审批
	private void convertJKBXVO(AggCtApVO temp) throws BusinessException {
		// TODO Auto-generated method stub
		CtApVO hvo = temp.getParentVO();
		String pk_org = (String) getHyPubBO().findColValue(
				"v_org_gc",
				"pk_org",
				" pk_org  = '" + hvo.getPk_org()
						+ "' and pk_org <> '0001A110000000062PXO'");
		if (StringUtils.isNotEmpty(pk_org)) {
			IGetIntoPortal itf = NCLocator.getInstance().lookup(
					IGetIntoPortal.class);
			itf.changeIntoPortal(hvo.getCbilltypecode(), "264X-Cxx-gcfkht",
					temp);
			// 清空驳回原因信息 付款合同：表头自定义项12,采购合同：表头自定义项52,销售合同：表头自定义项10
			hvo.setVdef12(null);
			getHyPubBO().update(hvo);
		}
	}

	private JSONArray getZTDtaileDataMap(AggCtApVO temp)
			throws BusinessException {
		// 获取详细信息
		List dtlist = new ArrayList();
		// 明细表1 合同基本(fct_ap_b)
		CtApBVO[] bvos = temp.getCtApBVO();
		if (bvos != null && bvos.length > 0) {
			Map bodyMap = getBody(bvos);
			dtlist.add(bodyMap);
		}
		// 明细表2 合同条款 (fct_ap_term)
		CtApTermVO[] tkvos = temp.getCtApTermVO();
		if (tkvos != null && tkvos.length > 0) {
			Map tkbodyMap = getTKBody(tkvos);
			dtlist.add(tkbodyMap);
		}
		// 明细表3 付款计划 (fct_ap_plan)
		CtApPlanVO[] jhvos = temp.getCtApPlanVO();
		if (jhvos != null && jhvos.length > 0) {
			Map jhbodyMap = getJHBody(jhvos);
			dtlist.add(jhbodyMap);
		}
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(dtlist, jsonConfig);
		return dtlistString;
	}

	private JSONArray getBWJKLDtaileDataMap(AggCtApVO temp)
			throws BusinessException {
		// 获取详细信息
		List dtlist = new ArrayList();
		// 明细表1 合同基本(fct_ap_b)
		CtApBVO[] bvos = temp.getCtApBVO();
		if (bvos != null && bvos.length > 0) {
			Map bodyMap = getFKJBBody(bvos);
			dtlist.add(bodyMap);
		}
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(dtlist, jsonConfig);
		return dtlistString;
	}

	// 中台主表数据
	private JSONArray getZTMainMap(CtApVO parentVO) throws BusinessException {
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
		list.add(OaWorkFlowUtil.listAddObj("username", userVO.getUser_name()));
		// 制单人身份证号
		String idCard = (String) getHyPubBO().findColValue("bd_psndoc", "id",
				"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
		if (null == idCard) {
			throw new BusinessException("制单人身份证号码未维护");
		}
		list.add(OaWorkFlowUtil.listAddObj("userid", idCard));
		// 用印类型
		if (parentVO.getVdef5() != null) {
			String yylx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getVdef5()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("yylx", yylx));
		}
		// 是否使用电子签章
		if (parentVO.getVdef4() != null) {
			String sfsydzqz = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getVdef4()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sfsydzqz", sfsydzqz));
		}
		// 原能投字段START---
		// 所属组织名称
		list.add(OaWorkFlowUtil.listAddObj("sszzmc", orgVO.getName()));
		list.add(OaWorkFlowUtil.listAddObj("szgs", orgVO.getCode()));
		list.add(OaWorkFlowUtil.listAddObj("xmzz", orgVO.getName()));
		// 制单人信息
		list.add(OaWorkFlowUtil.listAddObj("zdrmc", userVO.getUser_name()));
		list.add(OaWorkFlowUtil.listAddObj("zdrzj", userVO.getUser_code()));

		// 身份证号
		list.add(OaWorkFlowUtil.listAddObj("sfzh", idCard));

		// 合同编码
		list.add(OaWorkFlowUtil.listAddObj("htbm", parentVO.getVbillcode()));

		// 合同名称
		list.add(OaWorkFlowUtil.listAddObj("htmc", parentVO.getCtname()));

		// 合同签订时间
		if (parentVO.getSubscribedate() != null) {
			list.add(OaWorkFlowUtil.listAddObj("htqdsj", parentVO
					.getSubscribedate().getYear()
					+ "-"
					+ parentVO.getSubscribedate().getStrMonth()
					+ "-"
					+ parentVO.getSubscribedate().getStrDay()));
		}

		// 单价合同
		if (parentVO.getVdef17() != null) {
			list.add(OaWorkFlowUtil.listAddObj("djht",
					new UFBoolean(parentVO.getVdef17()).booleanValue() ? "是"
							: "否"));
		}

		// 起始日期
		if (parentVO.getValdate() != null) {
			list.add(OaWorkFlowUtil.listAddObj("qsrq", parentVO.getValdate()
					.getYear()
					+ "-"
					+ parentVO.getValdate().getStrMonth()
					+ "-" + parentVO.getValdate().getStrDay()));
		}

		// 终止日期
		if (parentVO.getInvallidate() != null) {
			list.add(OaWorkFlowUtil.listAddObj("zzrq", parentVO
					.getInvallidate().getYear()
					+ "-"
					+ parentVO.getInvallidate().getStrMonth()
					+ "-"
					+ parentVO.getInvallidate().getStrDay()));
		}

		// 供应商
		if (parentVO.getCvendorid() != null) {
			SupplierVO supplierVO = (SupplierVO) getHyPubBO()
					.queryByPrimaryKey(SupplierVO.class,
							parentVO.getCvendorid());
			list.add(OaWorkFlowUtil.listAddObj("gys", supplierVO.getName()));
			list.add(OaWorkFlowUtil.listAddObj("pk_supplier_name",
					supplierVO.getName()));
		}

		// 承办人员
		if (parentVO.getPersonnelid() != null) {
			String cbry = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and pk_psndoc ='"
							+ parentVO.getPersonnelid() + "'");
			list.add(OaWorkFlowUtil.listAddObj("cbry", cbry));
		}

		// 承办部门
		if (parentVO.getDepid() != null) {
			String cbbm = (String) getHyPubBO().findColValue("org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getDepid() + "'");
			list.add(OaWorkFlowUtil.listAddObj("cbbm", cbbm));
		}

		// 合同金额
		list.add(OaWorkFlowUtil.listAddObj("htje", parentVO.getNtotalorigmny()
				.setScale(2, UFDouble.ROUND_HALF_UP).toString()));
		list.add(OaWorkFlowUtil.listAddObj("htje1", parentVO.getNtotalorigmny()
				.setScale(2, UFDouble.ROUND_HALF_UP).toString()));

		// 代控部门
		if (parentVO.getVdef10() != null) {
			DeptVersionVO dkbmVO = (DeptVersionVO) getHyPubBO()
					.queryByPrimaryKey(DeptVersionVO.class,
							parentVO.getVdef10());
			if (dkbmVO != null) {
				/* 代控部门主键 */
				list.add(OaWorkFlowUtil.listAddObj("dkbmzj",
						dkbmVO.getPk_dept()));

				/* 代控部门编码 */
				list.add(OaWorkFlowUtil.listAddObj("dkbmbm", dkbmVO.getCode()));

				/* 代控部门 */
				list.add(OaWorkFlowUtil.listAddObj("dkbm", dkbmVO.getName()));
			}
		}

		// 累计付款金额
		list.add(OaWorkFlowUtil.listAddObj("ljfkje", parentVO
				.getNtotalgpamount().setScale(2, UFDouble.ROUND_HALF_UP)
				.toString()));

		// 累计本币付款金额
		list.add(OaWorkFlowUtil.listAddObj("ljbbfkje", parentVO
				.getNtotalgpamount().setScale(2, UFDouble.ROUND_HALF_UP)
				.toString()));

		// 累计开票金额
		list.add(OaWorkFlowUtil.listAddObj("ljkpje", parentVO
				.getNtotalcopamount().setScale(2, UFDouble.ROUND_HALF_UP)
				.toString()));

		// 对方银行账号
		if (parentVO.getBankaccount() != null) {
			String dfyhzh = (String) getHyPubBO().findColValue(
					"bd_bankaccsub",
					"accnum",
					"nvl(dr,0) = 0 and   pk_bankaccsub ='"
							+ parentVO.getBankaccount() + "'");
			list.add(OaWorkFlowUtil.listAddObj("dfyxzh", dfyhzh));
			list.add(OaWorkFlowUtil.listAddObj("dfyhzh", dfyhzh));
		}
		// 甲方单位
		if (null != parentVO.getCvendorid()) {
			String jfdw = (String) getHyPubBO().findColValue(
					"bd_supplier",
					"name",
					"nvl(dr,0) = 0 and pk_supplier  ='"
							+ parentVO.getCvendorid() + "'");
			list.add(OaWorkFlowUtil.listAddObj("jfdw", jfdw));
		}

		// 招标方式
		if (null != parentVO.getVdef3()) {
			String zbfs = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getVdef3()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("zbfs", zbfs));
		}

		// 合同类别
		if (parentVO.getVdef18() != null) {
			String htlb = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getVdef18()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("htlb", htlb));
		}

		// 金额大写
		// list.add(OaWorkFlowUtil.listAddObj("jedx", parentVO.getVdef16()));

		// 合同变更
		if (null != parentVO.getVdef11()) {
			String htbg = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getVdef11()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("htbgmc", htbg));
		}

		// 备注
		list.add(OaWorkFlowUtil.listAddObj("bz", parentVO.getVdef20()));

		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	// 付款基本
	private Map getBody(CtApBVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		// 接口获取
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (CtApBVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			// 协议方
			if (temp.getVbdef1() != null) {
				SupplierVO supplierVO = (SupplierVO) getHyPubBO()
						.queryByPrimaryKey(SupplierVO.class, temp.getVbdef1());
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("xyf",
						supplierVO.getName()));
			}

			// 项目
			if (temp.getProject() != null) {
				String project_code = (String) getHyPubBO().findColValue(
						"bd_project",
						"project_code",
						"nvl(dr,0) = 0 and pk_project ='" + temp.getProject()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"xmbm", project_code));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bproject_code", project_code));

				String project_name = (String) getHyPubBO().findColValue(
						"bd_project",
						"project_name",
						"nvl(dr,0) = 0 and pk_project ='" + temp.getProject()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"xmmc", project_name));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bproject_name", project_name));
			}

			// 数量
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sl", temp
					.getNnum().setScale(2, UFDouble.ROUND_HALF_UP).toString()));

			// 单价
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("dj", temp
					.getNgtaxprice().setScale(4, UFDouble.ROUND_HALF_UP)
					.toString()));

			// 金额
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("je", temp
					.getNtaxmny().setScale(2, UFDouble.ROUND_HALF_UP)
					.toString()));

			// 税率
			if (null != temp.getVbdef17()) {
				String sl = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc ='" + temp.getVbdef17()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sl1",
						sl));
			}

			// 物料名称
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("wlmc",
					temp.getVbdef3()));

			// 物料规格
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("wlgg",
					temp.getVbdef4()));

			// 费用名称
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("fymc",
					temp.getVbdef5()));

			// nc付款单
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("ncfkd",
					temp.getVbdef14()));

			// 不含税金额
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bhsje",
					temp.getVbdef15()));

			// 税额
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("se",
					temp.getVbdef19()));

			// 累计付款金额
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("ljfkje",
					temp.getNoritotalgpmny()
							.setScale(2, UFDouble.ROUND_HALF_UP).toString()));

			// 累计开票金额
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("ljkpje",
					temp.getNcopegpmny().setScale(2, UFDouble.ROUND_HALF_UP)
							.toString()));

			// 备注
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bz",
					temp.getVmemo()));

			// 预算类别
			String yslbstr = "";
			yslbstr = (String) getHyPubBO().findColValue("tb_budgetsub",
					"objname",
					"nvl(dr,0) = 0 and pk_obj ='" + temp.getVbdef2() + "'");
			if (yslbstr == null) {
				yslbstr = (String) getHyPubBO().findColValue(
						"bd_inoutbusiclass",
						"name",
						"nvl(dr,0) = 0 and pk_inoutbusiclass ='"
								+ temp.getInoutcome() + "'");
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("yslb",
					yslbstr));
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

	// 付款基本
	private Map getFKJBBody(CtApBVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		// 接口获取
		dtMap.put("tableDBName", "formtable_main_407_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (CtApBVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			// 协议方
			if (temp.getVbdef1() != null) {
				SupplierVO supplierVO = (SupplierVO) getHyPubBO()
						.queryByPrimaryKey(SupplierVO.class, temp.getVbdef1());
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("xyf",
						supplierVO.getName()));
			}

			// 项目
			if (temp.getProject() != null) {
				String project_code = (String) getHyPubBO().findColValue(
						"bd_project",
						"project_code",
						"nvl(dr,0) = 0 and pk_project ='" + temp.getProject()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"xmbm", project_code));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bproject_code", project_code));

				String project_name = (String) getHyPubBO().findColValue(
						"bd_project",
						"project_name",
						"nvl(dr,0) = 0 and pk_project ='" + temp.getProject()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"xmmc", project_name));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bproject_name", project_name));
			}

			// 数量
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sl", temp
					.getNnum().setScale(2, UFDouble.ROUND_HALF_UP).toString()));

			// 单价
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("dj", temp
					.getNgtaxprice().setScale(4, UFDouble.ROUND_HALF_UP)
					.toString()));

			// 金额
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("je", temp
					.getNtaxmny().setScale(2, UFDouble.ROUND_HALF_UP)
					.toString()));

			// 税率
			if (null != temp.getVbdef17()) {
				String sl = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc ='" + temp.getVbdef17()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sl1",
						sl));
			}

			// 物料名称
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("wlmc",
					temp.getVbdef3()));

			// 物料规格
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("wlgg",
					temp.getVbdef4()));

			// 费用名称
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("fymc",
					temp.getVbdef5()));

			// nc付款单
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("ncfkd",
					temp.getVbdef14()));

			// 不含税金额
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bhsje",
					temp.getVbdef15()));

			// 税额
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("se",
					temp.getVbdef19()));

			// 累计付款金额
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("ljfkje",
					temp.getNoritotalgpmny()
							.setScale(2, UFDouble.ROUND_HALF_UP).toString()));

			// 累计开票金额
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("ljkpje",
					temp.getNcopegpmny().setScale(2, UFDouble.ROUND_HALF_UP)
							.toString()));

			// 备注
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bz",
					temp.getVmemo()));

			// 预算类别
			String yslbstr = "";
			yslbstr = (String) getHyPubBO().findColValue("tb_budgetsub",
					"objname",
					"nvl(dr,0) = 0 and pk_obj ='" + temp.getVbdef2() + "'");
			if (yslbstr == null) {
				yslbstr = (String) getHyPubBO().findColValue(
						"bd_inoutbusiclass",
						"name",
						"nvl(dr,0) = 0 and pk_inoutbusiclass ='"
								+ temp.getInoutcome() + "'");
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("yslb",
					yslbstr));
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

	// 付款条款
	private Map getTKBody(CtApTermVO[] bvo) throws BusinessException {

		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt2");
		List workflowRequestTableRecords = new ArrayList();
		for (CtApTermVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			// 条款编码
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("tkbm",
					temp.getVtermcode()));

			// 条款名称
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("tkmc",
					temp.getVtermname()));

			// 条款类型
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("tklx",
					temp.getVtermtypename()));

			// 条款内容
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("tknr",
					temp.getVtermcontent()));

			// 其他信息
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("qtxx",
					temp.getVotherinfo()));

			// 备注
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bz",
					temp.getVmemo()));
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

	// 付款计划
	private Map getJHBody(CtApPlanVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt3");
		List workflowRequestTableRecords = new ArrayList();
		for (CtApPlanVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			// 计划比例
			Map jhbl = OaWorkFlowUtil.listAddObj("jhbl", temp.getPlanrate()
					.setScale(2, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(jhbl);
			// 计划金额
			Map jhje = OaWorkFlowUtil.listAddObj("jhje", temp.getPlanmoney()
					.setScale(2, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(jhje);
			// 结算金额
			Map jsje = OaWorkFlowUtil.listAddObj("jsje", temp.getBalancemoney()
					.setScale(2, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(jsje);
			// 预付款
			Map yfk = OaWorkFlowUtil.listAddObj("yfk", temp.getPaytype()
					.booleanValue() ? "是" : "否");
			workflowRequestTableFields.add(yfk);
			// 质保金
			Map zbj = OaWorkFlowUtil.listAddObj("zbj", temp.getMoneytype()
					.booleanValue() ? "是" : "否");
			workflowRequestTableFields.add(zbj);
			// 付款类型
			String fklxstr = "";
			if (temp.getPaymontype() != null) {
				fklxstr = (String) getHyPubBO().findColValue(
						"fi_recpaytype",
						"name ",
						"nvl(dr,0) = 0 and pk_recpaytype   = '"
								+ temp.getPaymontype() + "'");
			}
			Map fklx = OaWorkFlowUtil.listAddObj("fklx", fklxstr);
			workflowRequestTableFields.add(fklx);

			// 账期号
			Map zqh = OaWorkFlowUtil.listAddObj("zqh", temp.getAccountnum()
					.toString());
			workflowRequestTableFields.add(zqh);
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

	private WorkFlowBill getWorkFlowBill(AggCtApVO temp, String bill_type)
			throws BusinessException {
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
			// String bill_type = workFlowBill.getBill_code();
			String workflowName = "";
			if ("FCT1-Cxx-02".equals(bill_type)) {
				workflowName = "费用类合同";
			} else if ("FCT1-Cxx-03".equals(bill_type)) {
				workflowName = "材料类合同";
			} else if ("FCT1-Cxx-04".equals(bill_type)) {
				workflowName = "工程类合同";
			} else if ("FCT1-Cxx-05".equals(bill_type)) {
				workflowName = "资产类合同";
			} else if ("FCT1-Cxx-06".equals(bill_type)) {
				workflowName = "长期投资类及其他特殊合同（多方）";
			} else if ("FCT1-Cxx-BWJK".equals(bill_type)) {
				workflowName = "保温接口类合同";
			}
			workFlowBill.setWorkflowName(workflowName);
		} else {
			// String bill_type = workFlowBill.getBill_code();
			String workflowId = "";
			String workflowName = "";
			if ("FCT1-Cxx-02".equals(bill_type)) {
				workflowId = "84";
				workflowName = "费用类合同";
			} else if ("FCT1-Cxx-03".equals(bill_type)) {
				workflowId = "94";
				workflowName = "材料类合同";
			} else if ("FCT1-Cxx-04".equals(bill_type)) {
				workflowId = "142";
				workflowName = "工程类合同";
			} else if ("FCT1-Cxx-05".equals(bill_type)) {
				workflowId = "143";
				workflowName = "资产类合同";
			} else if ("FCT1-Cxx-06".equals(bill_type)) {
				workflowId = "223";// OA流程id
				workflowName = "长期投资类及其他特殊合同（多方）";
			} else if ("FCT1-Cxx-BWJK".equals(bill_type)) {
				workflowId = "307";// OA流程id
				workflowName = "保温接口类合同";
			}
			workFlowBill.setWorkflowId(workflowId);
			UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
					UserVO.class, temp.getParentVO().getBillmaker());
			workFlowBill.setDef5(userVO.getUser_code());
			workFlowBill.setPk_group(temp.getParentVO().getPk_group());
			workFlowBill.setPk_org(temp.getParentVO().getPk_org());
			workFlowBill.setPk_bill(temp.getPrimaryKey());
			workFlowBill.setUser_name(userVO.getUser_name());
			workFlowBill.setUser_code(userVO.getUser_code());
			workFlowBill.setCreationtime(new UFDate().toStdString());
			workFlowBill.setBill_status("1");
			workFlowBill.setCreator(temp.getParentVO().getBillmaker());
			workFlowBill.setBill_code(temp.getParentVO().getCbilltypecode());
			workFlowBill.setWorkflowName(workflowName);
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

	private void senOaData(AggCtApVO[] billVOs) throws BusinessException {
		for (AggCtApVO temp : billVOs) {
			if ((temp.getParentVO().getVtrantypecode()).contains("FCT1")
					&& !"FCT1-01".equals(temp.getParentVO().getVtrantypecode())) {
				String bill_type = temp.getParentVO().getVtrantypecode();
				JSONArray headData = null;
				JSONArray bodyData = getDtaileDataMap(temp);
				if ("FCT1-Cxx-02".equals(bill_type)) {
					// 费用类合同
					headData = getTYMainMap(temp.getParentVO());
				} else if ("FCT1-Cxx-03".equals(bill_type)) {
					// 材料类合同
					headData = getTYMainMap(temp.getParentVO());
				} /*
				 * else if ("FCT1-Cxx-04".equals(bill_type)) { // 工程类合同 headData
				 * = getTYMainMap(temp.getParentVO()); }
				 */else if ("FCT1-Cxx-05".equals(bill_type)) {
					// 资产类合同
					headData = getTYMainMap(temp.getParentVO());
				} else if ("FCT1-Cxx-06".equals(bill_type)) {
					// 长期投资类及其他特殊合同（多方）
					headData = getCQTZ(temp.getParentVO());
				} else if ("FCT1-Cxx-BWJK".equals(bill_type)) {
					// 保温接口类合同 0323新增
					headData = getZTMainMap(temp.getParentVO());
					bodyData = getBWJKLDtaileDataMap(temp);
				}
				getWorkFlowBill(temp, bill_type);
				OaWorkFlowUtil.sendOaData(headData, bodyData,
						getWorkFlowBill(temp, bill_type));
			}

		}
	}

	private JSONArray getDtaileDataMap(AggCtApVO aggvo)
			throws BusinessException {

		List dtlist = new ArrayList();
		String bill_type = aggvo.getParentVO().getVtrantypecode();
		String tableDBName1 = "";
		String tableDBName2 = "";
		String tableDBName3 = "";
		if ("FCT1-Cxx-02".equals(bill_type)) {
			// 费用类合同
			tableDBName1 = "formtable_main_87_dt1";
			tableDBName2 = "formtable_main_87_dt2";
			tableDBName3 = "formtable_main_87_dt3";
			if ("2".equals(getDef2(aggvo.getParentVO().getPk_org()))) {
				tableDBName1 = "formtable_main_510_dt1";
				tableDBName2 = "formtable_main_510_dt2";
				tableDBName3 = "formtable_main_510_dt3";
			}
		} else if ("FCT1-Cxx-03".equals(bill_type)) {
			// 材料类合同
			tableDBName1 = "formtable_main_97_dt1";
			tableDBName2 = "formtable_main_97_dt2";
			tableDBName3 = "formtable_main_97_dt3";
		} else if ("FCT1-Cxx-04".equals(bill_type)) {
			// 工程类合同
			tableDBName1 = "formtable_main_194_dt1";
			tableDBName2 = "formtable_main_194_dt2";
			tableDBName3 = "formtable_main_194_dt3";
		} else if ("FCT1-Cxx-05".equals(bill_type)) {
			// 资产类合同
			tableDBName1 = "formtable_main_195_dt1";
			tableDBName2 = "formtable_main_195_dt2";
			tableDBName3 = "formtable_main_195_dt3";
			if ("2".equals(getDef2(aggvo.getParentVO().getPk_org()))) {
				tableDBName1 = "formtable_main_509_dt1";
				tableDBName2 = "formtable_main_509_dt2";
				tableDBName3 = "formtable_main_509_dt3";
			}
		} else if ("FCT1-Cxx-06".equals(bill_type)) {
			// 长期投资类及其他特殊合同（多方）
			tableDBName1 = "formtable_main_319_dt1";
			tableDBName2 = "formtable_main_319_dt2";
			tableDBName3 = "formtable_main_319_dt3";
		}
		// 合同基本
		Map ctApBMap = getTYCtAp(aggvo.getCtApBVO(), tableDBName1);
		if (null != ctApBMap) {
			dtlist.add(ctApBMap);
		}
		// 合同条款
		Map ctApTermMap = getCtApTerm(aggvo.getCtApTermVO(), tableDBName2);
		if (null != ctApTermMap) {
			dtlist.add(ctApTermMap);
		}
		// 付款计划 (fct_ap_plan)
		Map ctApPlanMap = getCtApPlanTerm(aggvo.getCtApPlanVO(), tableDBName3);
		if (null != ctApPlanMap) {
			dtlist.add(ctApPlanMap);
		}
		JSONArray dtlistString = JSONArray.fromObject(dtlist);
		return dtlistString;
	}

	// 合同条款
	private Map getCtApTerm(CtApTermVO[] ctApTermVO, String tableDBName)
			throws BusinessException {
		if (null == ctApTermVO || ctApTermVO.length == 0) {
			return null;
		}
		Map dtMap = new HashMap();
		dtMap.put("tableDBName", tableDBName);
		List workflowRequestTableRecords = new ArrayList();
		for (CtApTermVO temp : ctApTermVO) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();
			// 条款编码
			if (!"formtable_main_319_dt2".equals(tableDBName)) {
				Map tkbm = OaWorkFlowUtil.listAddObj("tkbm",
						temp.getVtermcode());
				workflowRequestTableFields.add(tkbm);
			}
			// 条款名称
			Map tkmc = OaWorkFlowUtil.listAddObj("tkmc", temp.getVtermname());
			workflowRequestTableFields.add(tkmc);
			// 条款类型
			Map tklx = OaWorkFlowUtil.listAddObj("tklx",
					temp.getVtermtypename());
			workflowRequestTableFields.add(tklx);
			// 条款内容
			Map tknr = OaWorkFlowUtil
					.listAddObj("tknr", temp.getVtermcontent());
			workflowRequestTableFields.add(tknr);
			// 其他信息
			Map qtxx = OaWorkFlowUtil.listAddObj("qtxx", temp.getVotherinfo());
			workflowRequestTableFields.add(qtxx);
			// 备注
			Map bz = OaWorkFlowUtil.listAddObj("bz", temp.getVmemo());
			workflowRequestTableFields.add(bz);

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	// 付款计划子表
	private Map getCtApPlanTerm(CtApPlanVO[] ctApPlanVO, String tableDBName)
			throws BusinessException {
		if (null == ctApPlanVO || ctApPlanVO.length == 0) {
			return null;
		}
		Map dtMap = new HashMap();
		dtMap.put("tableDBName", tableDBName);
		List workflowRequestTableRecords = new ArrayList();
		for (CtApPlanVO temp : ctApPlanVO) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();
			// 计划比例
			Map jhbl = OaWorkFlowUtil.listAddObj("jhbl", temp.getPlanrate()
					.setScale(2, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(jhbl);
			// 计划金额
			Map jhje = OaWorkFlowUtil.listAddObj("jhje", temp.getPlanmoney()
					.setScale(2, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(jhje);
			// 结算金额
			Map jsje = OaWorkFlowUtil.listAddObj("jsje", temp.getBalancemoney()
					.setScale(2, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(jsje);
			// 预付款
			Map yfk = OaWorkFlowUtil.listAddObj("yfk", temp.getPaytype()
					.booleanValue() ? "是" : "否");
			workflowRequestTableFields.add(yfk);
			// 质保金
			Map zbj = OaWorkFlowUtil.listAddObj("zbj", temp.getMoneytype()
					.booleanValue() ? "是" : "否");
			workflowRequestTableFields.add(zbj);
			// 付款类型
			String fklxstr = "";
			if (temp.getPaymontype() != null) {
				fklxstr = (String) getHyPubBO().findColValue(
						"fi_recpaytype",
						"name ",
						"nvl(dr,0) = 0 and pk_recpaytype   = '"
								+ temp.getPaymontype() + "'");
			}
			Map fklx = OaWorkFlowUtil.listAddObj("fklx", fklxstr);
			workflowRequestTableFields.add(fklx);

			// 账期号
			Map zqh = OaWorkFlowUtil.listAddObj("zqh", temp.getAccountnum()
					.toString());
			workflowRequestTableFields.add(zqh);

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	public AggCtApVO[] terminate(AggCtApVO[] vos) throws BusinessException {
		try {
			return new ApTerminateAction().terminate(vos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtApVO[] unapprove(AggCtApVO[] vos, AbstractCompiler2 script)
			throws BusinessException {
		try {
			return new ApUnApproveAction().unApprove(vos, script);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtApVO[] unfreeze(AggCtApVO[] vos) throws BusinessException {
		try {
			return new ApUnFreezeAction().unfreeze(vos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtApVO[] unsendapprove(AggCtApVO[] vos, AbstractCompiler2 script)
			throws BusinessException {
		try {
			unOaCommit(vos);
			return new ApUnsendApprove().unsendApprove(vos, script);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtApVO[] unterminate(AggCtApVO[] vos) throws BusinessException {
		try {
			return new ApUnTerminateAction().unterminate(vos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtApVO[] unvalidate(AggCtApVO[] vos) throws BusinessException {
		try {
			// unOaCommit(aggVO);
			return new ApUnValidateAction().unvalidate(vos);
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

	private void unOaCommit(AggCtApVO[] aggVO) throws BusinessException {
		for (AggCtApVO temp : aggVO) {
			if ((temp.getParentVO().getVtrantypecode()).contains("FCT1")) {
				String pk_org = (String) getHyPubBO().findColValue(
						"v_org_gc",
						"pk_org",
						" pk_org  = '" + temp.getParentVO().getPk_org()
								+ "' and pk_org <> '0001A110000000062PXO'");
				if (StringUtils.isNotEmpty(pk_org)) {
					throw new BusinessException("进共享单据不允许收回！");
				}
				OaWorkFlowUtil.backOaWorkFlow(temp.getPrimaryKey());
			}
		}
	}

	public AggCtApVO[] validate(AggCtApVO[] vos) throws BusinessException {
		try {
			return new ApValidateAction().validate(vos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 合同Main通用
	private JSONArray getTYMainMap(CtApVO parentVO) throws BusinessException {
		List list = new ArrayList();

		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				parentVO.getPk_org());
		// 所属组织名称
		Map sszzmc = OaWorkFlowUtil.listAddObj("sszzmc", orgVO.getName());
		list.add(sszzmc);
		if (null != orgVO) {

			Map szgs = OaWorkFlowUtil.listAddObj("szgs", orgVO.getCode());
			list.add(szgs);
			if (!"2".equals(getDef2(parentVO.getPk_org()))) {
				/* 项目组织 */
				Map xmzz = OaWorkFlowUtil.listAddObj("xmzz", orgVO.getName());
				list.add(xmzz);
			}
		}

		/*
		 * Map djzj = OaWorkFlowUtil.listAddObj("djzj",
		 * parentVO.getPk_fct_ap()); list.add(djzj);
		 */

		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		if (null != userVO) {

			Map zdrmc = OaWorkFlowUtil.listAddObj("zdrmc",
					userVO.getUser_name());
			list.add(zdrmc);

			Map zdrzj = OaWorkFlowUtil.listAddObj("zdrzj",
					userVO.getUser_code());
			String id = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"id",
					"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc()
							+ "'");
			if ("2".equals(getDef2(parentVO.getPk_org()))) {
				list.add(zdrzj);
			} else {
				list.add(zdrzj);
			}
		}

		/*
		 * Map zdrq = OaWorkFlowUtil.listAddObj("zdrq", parentVO.getDmakedate()
		 * .getYear() + "-" + parentVO.getDmakedate().getStrMonth() + "-" +
		 * parentVO.getDmakedate().getStrDay()); list.add(zdrq);
		 */

		Map sfzh = new HashMap();
		String id = (String) getHyPubBO().findColValue("bd_psndoc", "id",
				"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
		if (null == id) {
			// throw new BusinessException("制单人身份证号码未维护");
		}
		sfzh.put("fieldName", "sfzh");
		sfzh.put("fieldValue", id);
		list.add(sfzh);

		Map htbm = OaWorkFlowUtil.listAddObj("htbm", parentVO.getVbillcode());
		list.add(htbm);

		Map htmc = OaWorkFlowUtil.listAddObj("htmc", parentVO.getCtname());
		list.add(htmc);

		// 合同签订时间
		Map htqdsj = OaWorkFlowUtil.listAddObj("htqdsj", parentVO
				.getSubscribedate().getYear()
				+ "-"
				+ parentVO.getSubscribedate().getStrMonth()
				+ "-"
				+ parentVO.getSubscribedate().getStrDay());
		list.add(htqdsj);

		// 单价合同
		Map djht = OaWorkFlowUtil.listAddObj("djht",
				new UFBoolean(parentVO.getVdef17()).booleanValue() ? "是" : "否");
		list.add(djht);

		// 起始日期
		if (parentVO.getValdate() != null) {
			Map qsrq = OaWorkFlowUtil.listAddObj("qsrq", parentVO.getValdate()
					.getYear()
					+ "-"
					+ parentVO.getValdate().getStrMonth()
					+ "-" + parentVO.getValdate().getStrDay());
			list.add(qsrq);
		}
		// 终止日期
		if (parentVO.getInvallidate() != null) {
			Map zzrq = OaWorkFlowUtil.listAddObj("zzrq", parentVO
					.getInvallidate().getYear()
					+ "-"
					+ parentVO.getInvallidate().getStrMonth()
					+ "-"
					+ parentVO.getInvallidate().getStrDay());
			list.add(zzrq);
		}
		// parentVO.getCvendorid()
		/*
		 * Map gyszj = OaWorkFlowUtil.listAddObj("gyszj", "1"); list.add(gyszj);
		 */

		SupplierVO supplierVO = (SupplierVO) getHyPubBO().queryByPrimaryKey(
				SupplierVO.class, parentVO.getCvendorid());
		String name = "0";
		if (null != supplierVO.getName()) {
			name = supplierVO.getName();
		}
		Map gysmc = OaWorkFlowUtil.listAddObj("gys", name);
		list.add(gysmc);

		/*
		 * Map cbryzj = OaWorkFlowUtil .listAddObj("cbryzj",
		 * parentVO.getPersonnelid()); list.add(cbryzj);
		 */

		String mname = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc  ='" + parentVO.getPersonnelid()
						+ "'");
		Map cbrymc = OaWorkFlowUtil.listAddObj("cbry", mname);
		list.add(cbrymc);

		String dname = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getDepid() + "'");
		Map cbbmbb = OaWorkFlowUtil.listAddObj("cbbm", dname);
		String bill_type = parentVO.getVtrantypecode();
		list.add(cbbmbb);
		Map htje = OaWorkFlowUtil.listAddObj("htje", parentVO
				.getNtotalorigmny().setScale(2, UFDouble.ROUND_HALF_UP)
				.toString());
		if ("FCT1-Cxx-05".equals(bill_type)) {
			// 资产类合同
			// 合同模板
			String templateName = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"mnecode",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getVdef13()
							+ "'");
			Map htmb = OaWorkFlowUtil.listAddObj("qyshtmb", templateName);
			list.add(htmb);
			Map ljbbfkje = OaWorkFlowUtil.listAddObj("ljbbfkje", parentVO
					.getNorigpshamount().setScale(2, UFDouble.ROUND_HALF_UP)
					.toString());
			list.add(ljbbfkje);

			// ======================
			/* 代控部门主键 */
			Map dkbmzj = OaWorkFlowUtil.listAddObj("dkbmzj",
					parentVO.getVdef10());
			list.add(dkbmzj);

			/* 代控部门编码 */
			String bmbm = (String) getHyPubBO().findColValue("org_dept_v",
					"code",
					"nvl(dr,0) = 0 and pk_vid ='" + parentVO.getVdef10() + "'");
			Map dkbmbm = OaWorkFlowUtil.listAddObj("dkbmbm", bmbm);
			list.add(dkbmbm);

			/* 代控部门 */
			String bm = (String) getHyPubBO().findColValue("org_dept_v",
					"name",
					"nvl(dr,0) = 0 and pk_vid ='" + parentVO.getVdef10() + "'");
			Map dkbm = OaWorkFlowUtil.listAddObj("dkbm", bm);
			list.add(dkbm);
			// =======================

			Map ljbbkpje = OaWorkFlowUtil.listAddObj("ljbbkpje", parentVO
					.getNtotalcopamount().setScale(2, UFDouble.ROUND_HALF_UP)
					.toString());
			list.add(ljbbkpje);
			if ("2".equals(getDef2(parentVO.getPk_org()))) {
				htje = OaWorkFlowUtil.listAddObj("htje1", parentVO
						.getNtotalorigmny().setScale(2, UFDouble.ROUND_HALF_UP)
						.toString());
			}
			list.add(htje);
		} else if ("FCT1-Cxx-02".equals(bill_type)) {
			// 费用类合同
			// ======================
			/* 代控部门主键 */
			Map dkbmzj = OaWorkFlowUtil.listAddObj("dkbmzj",
					parentVO.getVdef10());
			list.add(dkbmzj);

			/* 代控部门编码 */
			String bmbm = (String) getHyPubBO().findColValue("org_dept_v",
					"code",
					"nvl(dr,0) = 0 and pk_vid ='" + parentVO.getVdef10() + "'");
			Map dkbmbm = OaWorkFlowUtil.listAddObj("dkbmbm", bmbm);
			list.add(dkbmbm);

			/* 代控部门 */
			String bm = (String) getHyPubBO().findColValue("org_dept_v",
					"name",
					"nvl(dr,0) = 0 and pk_vid ='" + parentVO.getVdef10() + "'");
			Map dkbm = OaWorkFlowUtil.listAddObj("dkbm", bm);
			list.add(dkbm);
			// =======================
			Map ljbbfkje = OaWorkFlowUtil.listAddObj("ljfkje", parentVO
					.getNtotalgpamount().setScale(2, UFDouble.ROUND_HALF_UP)
					.toString());
			list.add(ljbbfkje);

			Map ljbbkpje = OaWorkFlowUtil.listAddObj("ljkpje", parentVO
					.getNtotalcopamount().setScale(2, UFDouble.ROUND_HALF_UP)
					.toString());
			list.add(ljbbkpje);

			list.add(htje);
			// 合同模板
			String templateName = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"mnecode",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getVdef13()
							+ "'");
			Map htmb = OaWorkFlowUtil.listAddObj("htmb", templateName);
			list.add(htmb);
		} else if ("FCT1-Cxx-03".equals(bill_type)) {
			// 材料类合同
			Map ljbbfkje = OaWorkFlowUtil.listAddObj("ljbbfkje", parentVO
					.getNtotalgpamount().setScale(2, UFDouble.ROUND_HALF_UP)
					.toString());
			list.add(ljbbfkje);

			Map ljbbkpje = OaWorkFlowUtil.listAddObj("ljbbkpje", parentVO
					.getNtotalcopamount().setScale(2, UFDouble.ROUND_HALF_UP)
					.toString());
			list.add(ljbbkpje);

			list.add(htje);
			// 测试
			Map qyszzmc = OaWorkFlowUtil.listAddObj("qyszzmc", "济南热力集团有限公司");
			list.add(qyszzmc);

			Map htmbmc = OaWorkFlowUtil.listAddObj("htmb", "采购_钢材采购合同");
			list.add(htmbmc);

			// 创建人主键
			Map ctid = OaWorkFlowUtil.listAddObj("ctid",
					parentVO.getPrimaryKey());
			list.add(ctid);
			// 甲方签收人
			Map jfqsr = OaWorkFlowUtil.listAddObj("jfqsr", "adsadasf");
			list.add(jfqsr);
			// 甲方联系电话
			Map jflxdh = OaWorkFlowUtil.listAddObj("jflxdh", "777100");
			list.add(jflxdh);
			// 乙方指定送达地址
			Map yfzdsddd = OaWorkFlowUtil.listAddObj("yfzdsddd", "aaa");
			list.add(yfzdsddd);
			// 乙方签收人
			Map yfqsr = OaWorkFlowUtil.listAddObj("yfqsr", "七十多分");
			list.add(yfqsr);
			// 乙方联系电话
			Map yflxdh = OaWorkFlowUtil.listAddObj("yflxdh", "122021");
			list.add(yflxdh);
			// 契约锁对应流程id
			Map qysdylcid = OaWorkFlowUtil.listAddObj("qyshtmb",
					"2906726979865441105");
			list.add(qysdylcid);

			Map qysdylcmc = OaWorkFlowUtil.listAddObj("qysdylcmc", "测试模板");
			list.add(qysdylcmc);

			// 合同模板
			Map htmb = OaWorkFlowUtil.listAddObj("htmblx", "13");
			list.add(htmb);
			// 用印流程
			Map yylc = OaWorkFlowUtil.listAddObj("yylc", "2906826864224661778");
			list.add(yylc);
		} else if ("FCT1-Cxx-04".equals(bill_type)) {
			// 工程类合同
			Map ljbbfkje = OaWorkFlowUtil.listAddObj("ljfkje", parentVO
					.getNorigpshamount().setScale(2, UFDouble.ROUND_HALF_UP)
					.toString());
			list.add(ljbbfkje);

			Map ljbbkpje = OaWorkFlowUtil.listAddObj("ljkpje", parentVO
					.getNorigcopamount().setScale(2, UFDouble.ROUND_HALF_UP)
					.toString());
			list.add(ljbbkpje);

			list.add(htje);
		}

		String bname = (String) getHyPubBO().findColValue(
				"bd_bankaccsub",
				"accnum",
				"nvl(dr,0) = 0 and   pk_bankaccsub ='"
						+ parentVO.getBankaccount() + "'");
		Map dfyxzh = OaWorkFlowUtil.listAddObj("dfyxzh", bname);
		list.add(dfyxzh);

		if (null != parentVO.getVdef3()) {
			String where = " pk_defdoc = '"
					+ parentVO.getVdef3()
					+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'cgfs' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
			String def3 = "0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					where)) {
				def3 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where);
			}

			Map zbfs = OaWorkFlowUtil.listAddObj("zbfs", def3);
			list.add(zbfs);
		}

		// 合同类别
		String billtypename = (String) getHyPubBO()
				.findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and   pk_defdoc ='"
								+ parentVO.getVdef18() + "'");
		Map htlbmc = OaWorkFlowUtil.listAddObj("htlb", billtypename);
		list.add(htlbmc);

		// Map jedx = OaWorkFlowUtil.listAddObj("jedx", parentVO.getVdef16());
		// list.add(jedx);

		if (null != parentVO.getVdef11()) {
			String where = " pk_defdoc = '"
					+ parentVO.getVdef11()
					+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'GX-07' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
			String def11 = "0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					where)) {
				def11 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where);
			}

			Map htbgmc = OaWorkFlowUtil.listAddObj("htbg", def11);
			list.add(htbgmc);
		}

		Map bz = OaWorkFlowUtil.listAddObj("bz", parentVO.getVdef20());
		list.add(bz);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	// 长期投资类及其他特殊合同（多方）
	private JSONArray getCQTZ(CtApVO parentVO) throws BusinessException {
		List list = new ArrayList();
		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				parentVO.getPk_org());
		if (null != orgVO) {

			Map szgs = OaWorkFlowUtil.listAddObj("szgs", orgVO.getCode());
			list.add(szgs);
			if (!"2".equals(getDef2(parentVO.getPk_org()))) {
				/* 项目组织 */
				Map xmzz = OaWorkFlowUtil.listAddObj("xmzz", orgVO.getName());
				list.add(xmzz);
			}
		}
		// 制单人主键
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		if (null != userVO) {

			Map zdrmc = OaWorkFlowUtil.listAddObj("zdrmc",
					userVO.getUser_name());
			list.add(zdrmc);

			Map zdrzj = OaWorkFlowUtil.listAddObj("zdrzj",
					userVO.getUser_code());
			String id = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"id",
					"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc()
							+ "'");
			if ("2".equals(getDef2(parentVO.getPk_org()))) {
				list.add(zdrzj);
			} else {
				list.add(zdrzj);
			}
		}
		// 合同编码
		Map htbm = OaWorkFlowUtil.listAddObj("htbm", parentVO.getVbillcode());
		list.add(htbm);

		// 合同名称
		Map htmc = OaWorkFlowUtil.listAddObj("htmc", parentVO.getCtname());
		list.add(htmc);

		// 合同类别
		String billtypename = (String) getHyPubBO()
				.findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and   pk_defdoc ='"
								+ parentVO.getVdef18() + "'");
		Map htlbmc = OaWorkFlowUtil.listAddObj("htlb", billtypename);
		list.add(htlbmc);

		// 合同签订时间
		Map htqdsj = OaWorkFlowUtil.listAddObj("htqdsj", parentVO
				.getSubscribedate().getYear()
				+ "-"
				+ parentVO.getSubscribedate().getStrMonth()
				+ "-"
				+ parentVO.getSubscribedate().getStrDay());
		list.add(htqdsj);

		// 起始日期
		Map qsrq = OaWorkFlowUtil.listAddObj("qsrq",
				parentVO.getValdate() == null ? "0" : parentVO.getValdate()
						.getYear()
						+ "-"
						+ parentVO.getValdate().getStrMonth()
						+ "-" + parentVO.getValdate().getStrDay());
		list.add(qsrq);
		// 终止日期
		Map zzrq = OaWorkFlowUtil.listAddObj("zzrq",
				parentVO.getInvallidate() == null ? "0" : parentVO
						.getInvallidate().getYear()
						+ "-"
						+ parentVO.getInvallidate().getStrMonth()
						+ "-"
						+ parentVO.getInvallidate().getStrDay());
		list.add(zzrq);

		// 承办人员
		String mname = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc  ='" + parentVO.getPersonnelid()
						+ "'");
		Map cbrymc = OaWorkFlowUtil.listAddObj("cbry", mname);
		list.add(cbrymc);

		// 承办部门
		String dname = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getDepid() + "'");
		Map cbbmbb = OaWorkFlowUtil.listAddObj("cbbm", dname);
		list.add(cbbmbb);
		// 合同金额
		Map htje = OaWorkFlowUtil.listAddObj("htje", parentVO.getNtotaltaxmny()
				.setScale(2, UFDouble.ROUND_HALF_UP).toString());
		list.add(htje);

		// 供应商档案
		SupplierVO supplierVO = (SupplierVO) getHyPubBO().queryByPrimaryKey(
				SupplierVO.class, parentVO.getCvendorid());
		String name = "0";
		if (null != supplierVO.getName()) {
			name = supplierVO.getName();
		}
		Map gysmc = OaWorkFlowUtil.listAddObj("gysda", name);
		list.add(gysmc);

		// 合同变更名称
		if (null != parentVO.getVdef11()) {
			String where = " pk_defdoc = '"
					+ parentVO.getVdef11()
					+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'GX-07' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
			String def11 = "0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					where)) {
				def11 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where);
			}
			Map htbgmc = OaWorkFlowUtil.listAddObj("htbgmc", def11);
			list.add(htbgmc);
		}
		// 单价合同
		Map djht = OaWorkFlowUtil.listAddObj("djht",
				new UFBoolean(parentVO.getVdef17()).booleanValue() ? "是" : "否");
		list.add(djht);
		// 累计付款金额
		Map ljfkje = OaWorkFlowUtil.listAddObj("ljfkje", parentVO
				.getNtotalgpamount().setScale(2, UFDouble.ROUND_HALF_UP)
				.toString());
		list.add(ljfkje);
		// 累计开票金额
		Map ljkpje = OaWorkFlowUtil.listAddObj("ljkpje", parentVO
				.getNtotalcopamount().setScale(2, UFDouble.ROUND_HALF_UP)
				.toString());
		list.add(ljkpje);

		// 代控部门
		String bill_type = parentVO.getVtrantypecode();
		if ("FCT1-Cxx-06".equals(bill_type)) {
			// 长期投资类及其他特殊合同（多方）
			// 累计付款金额
			/*
			 * Map ljbbfkje = OaWorkFlowUtil.listAddObj("ljfkje", parentVO
			 * .getNtotalgpamount().setScale(2, UFDouble.ROUND_HALF_UP)
			 * .toString()); list.add(ljbbfkje);
			 */

			// ======================
			/* 代控部门主键 */
			Map dkbmzj = OaWorkFlowUtil.listAddObj("dkbmzj",
					parentVO.getVdef10());
			list.add(dkbmzj);

			/* 代控部门编码 */
			String bmbm = (String) getHyPubBO().findColValue("org_dept_v",
					"code",
					"nvl(dr,0) = 0 and pk_vid ='" + parentVO.getVdef10() + "'");
			Map dkbmbm = OaWorkFlowUtil.listAddObj("dkbmbm", bmbm);
			list.add(dkbmbm);

			/* 代控部门 */
			String bm = (String) getHyPubBO().findColValue("org_dept_v",
					"name",
					"nvl(dr,0) = 0 and pk_vid ='" + parentVO.getVdef10() + "'");
			Map dkbm = OaWorkFlowUtil.listAddObj("dkbm", bm);
			list.add(dkbm);
			// =======================
			// 累计开票金额
			/*
			 * Map ljbbkpje = OaWorkFlowUtil.listAddObj("ljkpje", parentVO
			 * .getNtotalcopamount().setScale(2, UFDouble.ROUND_HALF_UP)
			 * .toString()); list.add(ljkpje);
			 */
		}

		// 金额大写
		// Map jedx = OaWorkFlowUtil.listAddObj("jedx", parentVO.getVdef16());
		// list.add(jedx);
		// 备注
		Map bz = OaWorkFlowUtil.listAddObj("bz", parentVO.getVdef20());
		list.add(bz);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	// 合同基本
	private Map getTYCtAp(CtApBVO[] ctApBVO, String tableDBName)
			throws BusinessException {
		if (null == ctApBVO || ctApBVO.length == 0) {
			return null;
		}
		Map dtMap = new HashMap();
		dtMap.put("tableDBName", tableDBName);
		List workflowRequestTableRecords = new ArrayList();
		for (CtApBVO temp : ctApBVO) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();
			// 行号
			// if("formtable_main_319_dt1".equals(tableDBName)){
			// Map hh = OaWorkFlowUtil.listAddObj("hh", temp.getCrowno());
			// workflowRequestTableFields.add(hh);
			// }

			Map xh = OaWorkFlowUtil.listAddObj("xh", temp.getCrowno());
			workflowRequestTableFields.add(xh);

			// 协议方
			if ("formtable_main_319_dt1".equals(tableDBName)) {
				SupplierVO supplierVO = (SupplierVO) getHyPubBO()
						.queryByPrimaryKey(SupplierVO.class, temp.getVbdef1());
				String name = "0";
				if (null != supplierVO.getName()) {
					name = supplierVO.getName();
				}
				Map xyfmc = OaWorkFlowUtil.listAddObj("xyf", name);
				workflowRequestTableFields.add(xyfmc);
			}

			String str = "0";
			if (null != temp.getProject()) {
				str = temp.getProject();
			}
			/*
			 * Map xmzj = OaWorkFlowUtil.listAddObj("xmzj", str);
			 * workflowRequestTableFields.add(xmzj);
			 */

			String project_name = (String) getHyPubBO().findColValue(
					"bd_project",
					"project_name ",
					"nvl(dr,0) = 0 and   pk_project   ='" + temp.getProject()
							+ "'");
			Map xmmc = OaWorkFlowUtil.listAddObj("xm", project_name);
			workflowRequestTableFields.add(xmmc);

			String s = "0";
			if (null != temp.getVbdef1()) {
				str = temp.getVbdef1();
			}
			/*
			 * Map mc = OaWorkFlowUtil.listAddObj("mc", s);
			 * workflowRequestTableFields.add(mc);
			 */
			// 数量
			Map sl = OaWorkFlowUtil.listAddObj("sl",
					temp.getNnum().setScale(2, UFDouble.ROUND_HALF_UP)
							.toString());
			workflowRequestTableFields.add(sl);
			// 单价
			Map dj = OaWorkFlowUtil.listAddObj("dj", temp.getNgtaxprice()
					.setScale(4, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(dj);
			// 金额
			Map je = OaWorkFlowUtil.listAddObj("je", temp.getNtaxmny()
					.setScale(2, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(je);

			// 税率
			if (null != temp.getVbdef17()) {
				String where = " pk_defdoc = '"
						+ temp.getVbdef17()
						+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'GX-02' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
				String def1 = (String) getHyPubBO().findColValue("bd_defdoc",
						"name", where);
				if (null != def1) {
					Map sl1 = OaWorkFlowUtil.listAddObj("sl1", def1);
					workflowRequestTableFields.add(sl1);
				}
			}

			if ("formtable_main_97_dt1".equals(tableDBName)) {
				String r = "0";
				if (null != temp.getVbdef3()) {
					r = temp.getVbdef3();
				}
				Map wlmc = OaWorkFlowUtil.listAddObj("wlmc", r);
				workflowRequestTableFields.add(wlmc);

				String t = "0";
				if (null != temp.getVbdef4()) {
					t = temp.getVbdef4();
				}
				Map gg = OaWorkFlowUtil.listAddObj("wlgg", t);
				workflowRequestTableFields.add(gg);
			} else {
				// 费用名称
				Map fymc = OaWorkFlowUtil.listAddObj("fymc", temp.getVbdef5());
				workflowRequestTableFields.add(fymc);
			}

			// nc付款单
			if ("formtable_main_87_dt1".equals(tableDBName)
					|| "formtable_main_195_dt1".equals(tableDBName)
					|| "formtable_main_510_dt1".equals(tableDBName)
					|| "formtable_main_319_dt1".equals(tableDBName)) {
				Map ncfkd = OaWorkFlowUtil.listAddObj("ncfkd",
						temp.getVbdef14());
				workflowRequestTableFields.add(ncfkd);
			}
			/*
			 * if (null != temp.getVbdef17()) { String where = " pk_defdoc = '"
			 * + temp.getVbdef17() +
			 * "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'GX-02' and nvl(dr,0) = 0) and nvl(dr,0) = 0"
			 * ; String def1 = (String) getHyPubBO().findColValue("bd_defdoc",
			 * "name", where); if (null != def1) { Map sl1 =
			 * OaWorkFlowUtil.listAddObj("sl1", def1);
			 * workflowRequestTableFields.add(sl1); } }
			 */
			// 不含税金额
			String money = "0";
			if (null != temp.getVbdef15()) {
				money = temp.getVbdef15();
			}
			Map bhsje = OaWorkFlowUtil.listAddObj("bhsje", money);
			workflowRequestTableFields.add(bhsje);
			// 税额
			String mone = "0";
			if (null != temp.getVbdef19()) {
				mone = temp.getVbdef19();
			}
			Map se = OaWorkFlowUtil.listAddObj("se", mone);
			workflowRequestTableFields.add(se);
			// 累计付款金额
			UFDouble lfk = temp.getNoritotalgpmny() == null ? new UFDouble(0.0)
					: temp.getNoritotalgpmny();
			Map ljfkje = OaWorkFlowUtil.listAddObj("ljfkje",
					lfk.setScale(2, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(ljfkje);
			/*
			 * // 累计原币付款金额 Map ljybfkje = OaWorkFlowUtil.listAddObj("ljybfkje",
			 * temp.getNoritotalgpmny() .setScale(2,
			 * UFDouble.ROUND_HALF_UP).toString());
			 * workflowRequestTableFields.add(ljybfkje);
			 */
			// 累计开票金额
			UFDouble lkp = temp.getNcopegpmny() == null ? new UFDouble(0.0)
					: temp.getNcopegpmny();
			Map ljkpje = OaWorkFlowUtil.listAddObj("ljkpje",
					lkp.setScale(2, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(ljkpje);
			// 备注
			String b = "无";
			if (null != temp.getVmemo()) {
				b = temp.getVmemo();
			}
			Map bz = OaWorkFlowUtil.listAddObj("bz", b);
			workflowRequestTableFields.add(bz);
			// 预算类别
			if ("formtable_main_195_dt1".equals(tableDBName)) {
				String yslbstr = (String) getHyPubBO().findColValue(
						"tb_budgetsub", "objname",
						"nvl(dr,0) = 0 and pk_obj ='" + temp.getVbdef2() + "'");
				Map yslb = OaWorkFlowUtil.listAddObj("yslb", yslbstr);
				workflowRequestTableFields.add(yslb);
			} else if ("formtable_main_87_dt1".equals(tableDBName)
					|| "formtable_main_510_dt1".equals(tableDBName)
					|| "formtable_main_319_dt1".equals(tableDBName)) {
				String yslbstr = (String) getHyPubBO().findColValue(
						"bd_inoutbusiclass",
						"name",
						"nvl(dr,0) = 0 and pk_inoutbusiclass ='"
								+ temp.getInoutcome() + "'");
				Map yslb = OaWorkFlowUtil.listAddObj("yslb", yslbstr);
				workflowRequestTableFields.add(yslb);
			}
			// 无税单价
			if ("formtable_main_87_dt1".equals(tableDBName)
					|| "formtable_main_195_dt1".equals(tableDBName)) {
				UFDouble wsdjold = temp.getNorigprice() == null ? new UFDouble(
						0.0) : temp.getNorigprice();
				Map wsdj = OaWorkFlowUtil.listAddObj("wsdj",
						wsdjold.setScale(4, UFDouble.ROUND_HALF_UP).toString());
				workflowRequestTableFields.add(wsdj);
			}
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);

			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	public String getDef1(String pk_org) throws BusinessException {
		String def1 = "";
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		if (null != orgVO) {
			def1 = orgVO.getDef1();
		}
		return def1;
	}
}
