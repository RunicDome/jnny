package nc.impl.pmr.pushoautil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.trade.business.HYPubBO;
import nc.itf.pmr.pushoautil.IPushOAService;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.bd.supplier.SupplierVO;
import nc.vo.fct.ap.entity.AggCtApVO;
import nc.vo.fct.ap.entity.CtApBVO;
import nc.vo.fct.ap.entity.CtApPlanVO;
import nc.vo.fct.ap.entity.CtApTermVO;
import nc.vo.fct.ap.entity.CtApVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.sm.UserVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;

//付款合同
@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
public class PushOAServiceimpl implements IPushOAService {
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

	@Override
	public String fkhtpushoa(AggregatedValueObject aggvo)
			throws BusinessException {
		// TODO Auto-generated method stub
		AggCtApVO temp = (AggCtApVO) aggvo;
		CtApVO headvo = temp.getParentVO();
		String ifbl = headvo.getVdef15();
		if ("2".equals(getDef2(temp.getParentVO().getPk_org()))
				|| "1".equals(getDef2(temp.getParentVO().getPk_org()))) {
			if (ifbl == null || "1001A2100000000B68C3".equals(ifbl)) {
				senOaData(temp);
			}
		}
		return "Y";
	}

	private void senOaData(AggCtApVO temp) throws BusinessException {
		// TODO Auto-generated method stub
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
			 * else if ("FCT1-Cxx-04".equals(bill_type)) { // 工程类合同 headData =
			 * getTYMainMap(temp.getParentVO()); }
			 */else if ("FCT1-Cxx-05".equals(bill_type)) {
				// 资产类合同
				headData = getTYMainMap(temp.getParentVO());
			} else if ("FCT1-Cxx-06".equals(bill_type)) {
				// 长期投资类及其他特殊合同（多方）
				headData = getCQTZ(temp.getParentVO());
			}
			getWorkFlowBill(temp, bill_type);
			OaWorkFlowUtil.sendOaData(headData, bodyData,
					getWorkFlowBill(temp, bill_type));
		}
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
			}
			workFlowBill.setWorkflowName(workflowName);
		} else {
			// String bill_type = workFlowBill.getBill_code();
			String workflowId = "";
			String workflowName = "";
			if ("FCT1-Cxx-02".equals(bill_type)) {
				workflowId = "84";
				if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
					workflowId = "489";// 能投
				}
				workflowName = "费用类合同";
			} else if ("FCT1-Cxx-03".equals(bill_type)) {
				workflowId = "94";
				workflowName = "材料类合同";
			} else if ("FCT1-Cxx-04".equals(bill_type)) {
				workflowId = "142";
				workflowName = "工程类合同";
			} else if ("FCT1-Cxx-05".equals(bill_type)) {
				workflowId = "143";
				if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
					workflowId = "488";// 能投
				}
				workflowName = "资产类合同";
			} else if ("FCT1-Cxx-06".equals(bill_type)) {
				workflowId = "223";// OA流程id
				/*
				 * if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
				 * workflowId = "488";// 能投 }
				 */
				workflowName = "长期投资类及其他特殊合同（多方）";
			}
			workFlowBill.setWorkflowId(workflowId);
			UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
					UserVO.class, temp.getParentVO().getBillmaker());
			workFlowBill.setDef5(userVO.getUser_code());
			if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
				PsndocVO psndoc = (PsndocVO) getHyPubBO().queryByPrimaryKey(
						PsndocVO.class, userVO.getPk_psndoc());
				workFlowBill.setDef5(psndoc.getId());
				// workFlowBill.setWorkflowId("0");
			}
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
	// 合同Main通用
	private JSONArray getTYMainMap(CtApVO parentVO) throws BusinessException {
		List list = new ArrayList();

		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				parentVO.getPk_org());
		// 所属组织名称
		Map sszzmc = OaWorkFlowUtil.listAdd("sszzmc", orgVO.getName());
		list.add(sszzmc);
		if (null != orgVO) {

			Map szgs = OaWorkFlowUtil.listAdd("szgs", orgVO.getCode());
			list.add(szgs);
			if (!"2".equals(getDef2(parentVO.getPk_org()))) {
				/* 项目组织 */
				Map xmzz = OaWorkFlowUtil.listAdd("xmzz", orgVO.getName());
				list.add(xmzz);
			}
		}

		/*
		 * Map djzj = OaWorkFlowUtil.listAdd("djzj", parentVO.getPk_fct_ap());
		 * list.add(djzj);
		 */

		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		if (null != userVO) {

			Map zdrmc = OaWorkFlowUtil.listAdd("zdrmc", userVO.getUser_name());
			list.add(zdrmc);

			Map zdrzj = OaWorkFlowUtil.listAdd("zdrzj", userVO.getUser_code());
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
		 * Map zdrq = OaWorkFlowUtil.listAdd("zdrq", parentVO.getDmakedate()
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

		Map htbm = OaWorkFlowUtil.listAdd("htbm", parentVO.getVbillcode());
		list.add(htbm);

		Map htmc = OaWorkFlowUtil.listAdd("htmc", parentVO.getCtname());
		list.add(htmc);

		// 合同签订时间
		Map htqdsj = OaWorkFlowUtil.listAdd("htqdsj", parentVO
				.getSubscribedate().getYear()
				+ "-"
				+ parentVO.getSubscribedate().getStrMonth()
				+ "-"
				+ parentVO.getSubscribedate().getStrDay());
		list.add(htqdsj);

		// 单价合同
		Map djht = OaWorkFlowUtil.listAdd("djht",
				new UFBoolean(parentVO.getVdef17()).booleanValue() ? "是" : "否");
		list.add(djht);

		// 起始日期
		Map qsrq = OaWorkFlowUtil.listAdd("qsrq", parentVO.getValdate()
				.getYear()
				+ "-"
				+ parentVO.getValdate().getStrMonth()
				+ "-"
				+ parentVO.getValdate().getStrDay());
		list.add(qsrq);
		// 终止日期
		Map zzrq = OaWorkFlowUtil.listAdd("zzrq", parentVO.getInvallidate()
				.getYear()
				+ "-"
				+ parentVO.getInvallidate().getStrMonth()
				+ "-" + parentVO.getInvallidate().getStrDay());
		list.add(zzrq);

		// parentVO.getCvendorid()
		/*
		 * Map gyszj = OaWorkFlowUtil.listAdd("gyszj", "1"); list.add(gyszj);
		 */

		SupplierVO supplierVO = (SupplierVO) getHyPubBO().queryByPrimaryKey(
				SupplierVO.class, parentVO.getCvendorid());
		String name = "0";
		if (null != supplierVO.getName()) {
			name = supplierVO.getName();
		}
		Map gysmc = OaWorkFlowUtil.listAdd("gys", name);
		list.add(gysmc);

		/*
		 * Map cbryzj = OaWorkFlowUtil .listAdd("cbryzj",
		 * parentVO.getPersonnelid()); list.add(cbryzj);
		 */

		String mname = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc  ='" + parentVO.getPersonnelid()
						+ "'");
		Map cbrymc = OaWorkFlowUtil.listAdd("cbry", mname);
		list.add(cbrymc);

		String dname = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getDepid() + "'");
		Map cbbmbb = OaWorkFlowUtil.listAdd("cbbm", dname);
		String bill_type = parentVO.getVtrantypecode();
		list.add(cbbmbb);
		Map htje = OaWorkFlowUtil.listAdd("htje", parentVO.getNtotalorigmny()
				.setScale(2, UFDouble.ROUND_HALF_UP).toString());

		if ("FCT1-Cxx-02".equals(bill_type)) {
			// 费用类合同
			// ======================
			/* 代控部门主键 */
			Map dkbmzj = OaWorkFlowUtil.listAdd("dkbmzj", parentVO.getVdef10());
			list.add(dkbmzj);

			/* 代控部门编码 */
			String bmbm = (String) getHyPubBO().findColValue("org_dept_v",
					"code",
					"nvl(dr,0) = 0 and pk_vid ='" + parentVO.getVdef10() + "'");
			Map dkbmbm = OaWorkFlowUtil.listAdd("dkbmbm", bmbm);
			list.add(dkbmbm);

			/* 代控部门 */
			String bm = (String) getHyPubBO().findColValue("org_dept_v",
					"name",
					"nvl(dr,0) = 0 and pk_vid ='" + parentVO.getVdef10() + "'");
			Map dkbm = OaWorkFlowUtil.listAdd("dkbm", bm);
			list.add(dkbm);
			// =======================
			Map ljbbfkje = OaWorkFlowUtil.listAdd("ljfkje", parentVO
					.getNtotalgpamount().setScale(2, UFDouble.ROUND_HALF_UP)
					.toString());
			list.add(ljbbfkje);

			Map ljbbkpje = OaWorkFlowUtil.listAdd("ljkpje", parentVO
					.getNtotalcopamount().setScale(2, UFDouble.ROUND_HALF_UP)
					.toString());
			list.add(ljbbkpje);

			list.add(htje);
		} else if ("FCT1-Cxx-03".equals(bill_type)) {
			// 材料类合同
			Map ljbbfkje = OaWorkFlowUtil.listAdd("ljbbfkje", parentVO
					.getNtotalgpamount().setScale(2, UFDouble.ROUND_HALF_UP)
					.toString());
			list.add(ljbbfkje);

			Map ljbbkpje = OaWorkFlowUtil.listAdd("ljbbkpje", parentVO
					.getNtotalcopamount().setScale(2, UFDouble.ROUND_HALF_UP)
					.toString());
			list.add(ljbbkpje);

			list.add(htje);
			// 测试
			Map qyszzmc = OaWorkFlowUtil.listAdd("qyszzmc", "济南热力集团有限公司");
			list.add(qyszzmc);

			Map htmbmc = OaWorkFlowUtil.listAdd("htmb", "采购_钢材采购合同");
			list.add(htmbmc);

			// 创建人主键
			Map ctid = OaWorkFlowUtil.listAdd("ctid", parentVO.getPrimaryKey());
			list.add(ctid);
			// 甲方签收人
			Map jfqsr = OaWorkFlowUtil.listAdd("jfqsr", "adsadasf");
			list.add(jfqsr);
			// 甲方联系电话
			Map jflxdh = OaWorkFlowUtil.listAdd("jflxdh", "777100");
			list.add(jflxdh);
			// 乙方指定送达地址
			Map yfzdsddd = OaWorkFlowUtil.listAdd("yfzdsddd", "aaa");
			list.add(yfzdsddd);
			// 乙方签收人
			Map yfqsr = OaWorkFlowUtil.listAdd("yfqsr", "七十多分");
			list.add(yfqsr);
			// 乙方联系电话
			Map yflxdh = OaWorkFlowUtil.listAdd("yflxdh", "122021");
			list.add(yflxdh);
			// 契约锁对应流程id
			Map qysdylcid = OaWorkFlowUtil.listAdd("qyshtmb",
					"2906726979865441105");
			list.add(qysdylcid);

			Map qysdylcmc = OaWorkFlowUtil.listAdd("qysdylcmc", "测试模板");
			list.add(qysdylcmc);

			// 合同模板
			Map htmb = OaWorkFlowUtil.listAdd("htmblx", "13");
			list.add(htmb);
			// 用印流程
			Map yylc = OaWorkFlowUtil.listAdd("yylc", "2906826864224661778");
			list.add(yylc);
		} else if ("FCT1-Cxx-04".equals(bill_type)) {
			// 工程类合同
			Map ljbbfkje = OaWorkFlowUtil.listAdd("ljfkje", parentVO
					.getNorigpshamount().setScale(2, UFDouble.ROUND_HALF_UP)
					.toString());
			list.add(ljbbfkje);

			Map ljbbkpje = OaWorkFlowUtil.listAdd("ljkpje", parentVO
					.getNorigcopamount().setScale(2, UFDouble.ROUND_HALF_UP)
					.toString());
			list.add(ljbbkpje);

			list.add(htje);
		} else if ("FCT1-Cxx-05".equals(bill_type)) {
			// 资产类合同
			Map ljbbfkje = OaWorkFlowUtil.listAdd("ljbbfkje", parentVO
					.getNorigpshamount().setScale(2, UFDouble.ROUND_HALF_UP)
					.toString());
			list.add(ljbbfkje);

			// ======================
			/* 代控部门主键 */
			Map dkbmzj = OaWorkFlowUtil.listAdd("dkbmzj", parentVO.getVdef10());
			list.add(dkbmzj);

			/* 代控部门编码 */
			String bmbm = (String) getHyPubBO().findColValue("org_dept_v",
					"code",
					"nvl(dr,0) = 0 and pk_vid ='" + parentVO.getVdef10() + "'");
			Map dkbmbm = OaWorkFlowUtil.listAdd("dkbmbm", bmbm);
			list.add(dkbmbm);

			/* 代控部门 */
			String bm = (String) getHyPubBO().findColValue("org_dept_v",
					"name",
					"nvl(dr,0) = 0 and pk_vid ='" + parentVO.getVdef10() + "'");
			Map dkbm = OaWorkFlowUtil.listAdd("dkbm", bm);
			list.add(dkbm);
			// =======================

			Map ljbbkpje = OaWorkFlowUtil.listAdd("ljbbkpje", parentVO
					.getNtotalcopamount().setScale(2, UFDouble.ROUND_HALF_UP)
					.toString());
			list.add(ljbbkpje);
			if ("2".equals(getDef2(parentVO.getPk_org()))) {
				htje = OaWorkFlowUtil.listAdd("htje1", parentVO
						.getNtotalorigmny().setScale(2, UFDouble.ROUND_HALF_UP)
						.toString());
			}
			list.add(htje);

		}

		String bname = (String) getHyPubBO().findColValue(
				"bd_bankaccsub",
				"accnum",
				"nvl(dr,0) = 0 and   pk_bankaccsub ='"
						+ parentVO.getBankaccount() + "'");
		Map dfyxzh = OaWorkFlowUtil.listAdd("dfyxzh", bname);
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

			Map zbfs = OaWorkFlowUtil.listAdd("zbfs", def3);
			list.add(zbfs);
		}

		String billtypename = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and   pk_billtypeid ='"
						+ parentVO.getCtrantypeid() + "'");
		Map htlbmc = OaWorkFlowUtil.listAdd("htlb", billtypename);
		list.add(htlbmc);

		Map jedx = OaWorkFlowUtil.listAdd("jedx", parentVO.getVdef16());
		list.add(jedx);

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

			Map htbgmc = OaWorkFlowUtil.listAdd("htbg", def11);
			list.add(htbgmc);
		}

		Map bz = OaWorkFlowUtil.listAdd("bz", parentVO.getVdef20());
		list.add(bz);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
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
		// 付款方式
		Map ctApPlanMap = getCtApPlanTerm(aggvo.getCtApPlanVO(), tableDBName3);
		if (null != ctApPlanMap) {
			dtlist.add(ctApPlanMap);
		}
		JSONArray dtlistString = JSONArray.fromObject(dtlist);
		return dtlistString;
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
			// Map hh = OaWorkFlowUtil.listAdd("hh", temp.getCrowno());
			// workflowRequestTableFields.add(hh);
			// }

			Map xh = OaWorkFlowUtil.listAdd("xh", temp.getCrowno());
			workflowRequestTableFields.add(xh);

			// 协议方
			if ("formtable_main_319_dt1".equals(tableDBName)) {
				SupplierVO supplierVO = (SupplierVO) getHyPubBO()
						.queryByPrimaryKey(SupplierVO.class, temp.getVbdef1());
				String name = "0";
				if (null != supplierVO.getName()) {
					name = supplierVO.getName();
				}
				Map xyfmc = OaWorkFlowUtil.listAdd("xyf", name);
				workflowRequestTableFields.add(xyfmc);
			}

			String str = "0";
			if (null != temp.getProject()) {
				str = temp.getProject();
			}
			/*
			 * Map xmzj = OaWorkFlowUtil.listAdd("xmzj", str);
			 * workflowRequestTableFields.add(xmzj);
			 */

			String project_name = (String) getHyPubBO().findColValue(
					"bd_project",
					"project_name ",
					"nvl(dr,0) = 0 and   pk_project   ='" + temp.getProject()
							+ "'");
			Map xmmc = OaWorkFlowUtil.listAdd("xm", project_name);
			workflowRequestTableFields.add(xmmc);

			String s = "0";
			if (null != temp.getVbdef1()) {
				str = temp.getVbdef1();
			}
			/*
			 * Map mc = OaWorkFlowUtil.listAdd("mc", s);
			 * workflowRequestTableFields.add(mc);
			 */
			// 数量
			Map sl = OaWorkFlowUtil.listAdd("sl",
					temp.getNnum().setScale(2, UFDouble.ROUND_HALF_UP)
							.toString());
			workflowRequestTableFields.add(sl);
			// 单价
			Map dj = OaWorkFlowUtil.listAdd("dj", temp.getNgtaxprice()
					.setScale(4, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(dj);
			// 金额
			Map je = OaWorkFlowUtil.listAdd("je",
					temp.getNtaxmny().setScale(2, UFDouble.ROUND_HALF_UP)
							.toString());
			workflowRequestTableFields.add(je);

			// 税率
			if (null != temp.getVbdef17()) {
				String where = " pk_defdoc = '"
						+ temp.getVbdef17()
						+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'GX-02' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
				String def1 = (String) getHyPubBO().findColValue("bd_defdoc",
						"name", where);
				if (null != def1) {
					Map sl1 = OaWorkFlowUtil.listAdd("sl1", def1);
					workflowRequestTableFields.add(sl1);
				}
			}

			if ("formtable_main_97_dt1".equals(tableDBName)) {
				String r = "0";
				if (null != temp.getVbdef3()) {
					r = temp.getVbdef3();
				}
				Map wlmc = OaWorkFlowUtil.listAdd("wlmc", r);
				workflowRequestTableFields.add(wlmc);

				String t = "0";
				if (null != temp.getVbdef4()) {
					t = temp.getVbdef4();
				}
				Map gg = OaWorkFlowUtil.listAdd("wlgg", t);
				workflowRequestTableFields.add(gg);
			} else {
				// 费用名称
				Map fymc = OaWorkFlowUtil.listAdd("fymc", temp.getVbdef5());
				workflowRequestTableFields.add(fymc);
			}

			// nc付款单
			if ("formtable_main_87_dt1".equals(tableDBName)
					|| "formtable_main_195_dt1".equals(tableDBName)
					|| "formtable_main_510_dt1".equals(tableDBName)
					|| "formtable_main_319_dt1".equals(tableDBName)) {
				Map ncfkd = OaWorkFlowUtil.listAdd("ncfkd", temp.getVbdef14());
				workflowRequestTableFields.add(ncfkd);
			}
			/*
			 * if (null != temp.getVbdef17()) { String where = " pk_defdoc = '"
			 * + temp.getVbdef17() +
			 * "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'GX-02' and nvl(dr,0) = 0) and nvl(dr,0) = 0"
			 * ; String def1 = (String) getHyPubBO().findColValue("bd_defdoc",
			 * "name", where); if (null != def1) { Map sl1 =
			 * OaWorkFlowUtil.listAdd("sl1", def1);
			 * workflowRequestTableFields.add(sl1); } }
			 */
			// 不含税金额
			String money = "0";
			if (null != temp.getVbdef15()) {
				money = temp.getVbdef15();
			}
			Map bhsje = OaWorkFlowUtil.listAdd("bhsje", money);
			workflowRequestTableFields.add(bhsje);
			// 税额
			String mone = "0";
			if (null != temp.getVbdef19()) {
				mone = temp.getVbdef19();
			}
			Map se = OaWorkFlowUtil.listAdd("se", mone);
			workflowRequestTableFields.add(se);
			// 累计付款金额
			Map ljfkje = OaWorkFlowUtil.listAdd("ljfkje", temp
					.getNoritotalgpmny().setScale(2, UFDouble.ROUND_HALF_UP)
					.toString());
			workflowRequestTableFields.add(ljfkje);
			/*
			 * // 累计原币付款金额 Map ljybfkje = OaWorkFlowUtil.listAdd("ljybfkje",
			 * temp.getNoritotalgpmny() .setScale(2,
			 * UFDouble.ROUND_HALF_UP).toString());
			 * workflowRequestTableFields.add(ljybfkje);
			 */
			// 累计开票金额
			Map ljkpje = OaWorkFlowUtil.listAdd("ljkpje", temp.getNcopegpmny()
					.setScale(2, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(ljkpje);
			// 备注
			String b = "无";
			if (null != temp.getVmemo()) {
				b = temp.getVmemo();
			}
			Map bz = OaWorkFlowUtil.listAdd("bz", b);
			workflowRequestTableFields.add(bz);
			// 预算类别
			if ("formtable_main_195_dt1".equals(tableDBName)) {
				String yslbstr = (String) getHyPubBO().findColValue(
						"tb_budgetsub", "objname",
						"nvl(dr,0) = 0 and pk_obj ='" + temp.getVbdef2() + "'");
				Map yslb = OaWorkFlowUtil.listAdd("yslb", yslbstr);
				workflowRequestTableFields.add(yslb);
			} else if ("formtable_main_87_dt1".equals(tableDBName)
					|| "formtable_main_510_dt1".equals(tableDBName)
					|| "formtable_main_319_dt1".equals(tableDBName)) {
				String yslbstr = (String) getHyPubBO().findColValue(
						"bd_inoutbusiclass",
						"name",
						"nvl(dr,0) = 0 and pk_inoutbusiclass ='"
								+ temp.getInoutcome() + "'");
				Map yslb = OaWorkFlowUtil.listAdd("yslb", yslbstr);
				workflowRequestTableFields.add(yslb);
			}

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);

			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	// 长期投资类及其他特殊合同（多方）
	private JSONArray getCQTZ(CtApVO parentVO) throws BusinessException {
		List list = new ArrayList();
		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				parentVO.getPk_org());
		if (null != orgVO) {

			Map szgs = OaWorkFlowUtil.listAdd("szgs", orgVO.getCode());
			list.add(szgs);
			if (!"2".equals(getDef2(parentVO.getPk_org()))) {
				/* 项目组织 */
				Map xmzz = OaWorkFlowUtil.listAdd("xmzz", orgVO.getName());
				list.add(xmzz);
			}
		}
		// 制单人主键
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		if (null != userVO) {

			Map zdrmc = OaWorkFlowUtil.listAdd("zdrmc", userVO.getUser_name());
			list.add(zdrmc);

			Map zdrzj = OaWorkFlowUtil.listAdd("zdrzj", userVO.getUser_code());
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
		Map htbm = OaWorkFlowUtil.listAdd("htbm", parentVO.getVbillcode());
		list.add(htbm);

		// 合同名称
		Map htmc = OaWorkFlowUtil.listAdd("htmc", parentVO.getCtname());
		list.add(htmc);

		// 合同类别
		String billtypename = (String) getHyPubBO()
				.findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and   pk_defdoc ='"
								+ parentVO.getVdef18() + "'");
		Map htlbmc = OaWorkFlowUtil.listAdd("htlb", billtypename);
		list.add(htlbmc);

		// 合同签订时间
		Map htqdsj = OaWorkFlowUtil.listAdd("htqdsj", parentVO
				.getSubscribedate().getYear()
				+ "-"
				+ parentVO.getSubscribedate().getStrMonth()
				+ "-"
				+ parentVO.getSubscribedate().getStrDay());
		list.add(htqdsj);

		// 起始日期
		Map qsrq = OaWorkFlowUtil.listAdd("qsrq",
				parentVO.getValdate() == null ? "0" : parentVO.getValdate()
						.getYear()
						+ "-"
						+ parentVO.getValdate().getStrMonth()
						+ "-" + parentVO.getValdate().getStrDay());
		list.add(qsrq);
		// 终止日期
		Map zzrq = OaWorkFlowUtil.listAdd("zzrq",
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
		Map cbrymc = OaWorkFlowUtil.listAdd("cbry", mname);
		list.add(cbrymc);

		// 承办部门
		String dname = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getDepid() + "'");
		Map cbbmbb = OaWorkFlowUtil.listAdd("cbbm", dname);
		list.add(cbbmbb);
		// 合同金额
		Map htje = OaWorkFlowUtil.listAdd("htje", parentVO.getNtotaltaxmny()
				.setScale(2, UFDouble.ROUND_HALF_UP).toString());
		list.add(htje);

		// 供应商档案
		SupplierVO supplierVO = (SupplierVO) getHyPubBO().queryByPrimaryKey(
				SupplierVO.class, parentVO.getCvendorid());
		String name = "0";
		if (null != supplierVO.getName()) {
			name = supplierVO.getName();
		}
		Map gysmc = OaWorkFlowUtil.listAdd("gysda", name);
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
			Map htbgmc = OaWorkFlowUtil.listAdd("htbgmc", def11);
			list.add(htbgmc);
		}
		// 单价合同
		Map djht = OaWorkFlowUtil.listAdd("djht",
				new UFBoolean(parentVO.getVdef17()).booleanValue() ? "是" : "否");
		list.add(djht);
		// 累计付款金额
		Map ljfkje = OaWorkFlowUtil.listAdd("ljfkje", parentVO
				.getNtotalgpamount().setScale(2, UFDouble.ROUND_HALF_UP)
				.toString());
		list.add(ljfkje);
		// 累计开票金额
		Map ljkpje = OaWorkFlowUtil.listAdd("ljkpje", parentVO
				.getNtotalcopamount().setScale(2, UFDouble.ROUND_HALF_UP)
				.toString());
		list.add(ljkpje);

		// 代控部门
		String bill_type = parentVO.getVtrantypecode();
		if ("FCT1-Cxx-06".equals(bill_type)) {
			// 长期投资类及其他特殊合同（多方）
			// 累计付款金额
			/*
			 * Map ljbbfkje = OaWorkFlowUtil.listAdd("ljfkje", parentVO
			 * .getNtotalgpamount().setScale(2, UFDouble.ROUND_HALF_UP)
			 * .toString()); list.add(ljbbfkje);
			 */

			// ======================
			/* 代控部门主键 */
			Map dkbmzj = OaWorkFlowUtil.listAdd("dkbmzj", parentVO.getVdef10());
			list.add(dkbmzj);

			/* 代控部门编码 */
			String bmbm = (String) getHyPubBO().findColValue("org_dept_v",
					"code",
					"nvl(dr,0) = 0 and pk_vid ='" + parentVO.getVdef10() + "'");
			Map dkbmbm = OaWorkFlowUtil.listAdd("dkbmbm", bmbm);
			list.add(dkbmbm);

			/* 代控部门 */
			String bm = (String) getHyPubBO().findColValue("org_dept_v",
					"name",
					"nvl(dr,0) = 0 and pk_vid ='" + parentVO.getVdef10() + "'");
			Map dkbm = OaWorkFlowUtil.listAdd("dkbm", bm);
			list.add(dkbm);
			// =======================
			// 累计开票金额
			/*
			 * Map ljbbkpje = OaWorkFlowUtil.listAdd("ljkpje", parentVO
			 * .getNtotalcopamount().setScale(2, UFDouble.ROUND_HALF_UP)
			 * .toString()); list.add(ljkpje);
			 */
		}

		// 金额大写
		Map jedx = OaWorkFlowUtil.listAdd("jedx", parentVO.getVdef16());
		list.add(jedx);
		// 备注
		Map bz = OaWorkFlowUtil.listAdd("bz", parentVO.getVdef20());
		list.add(bz);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
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
				Map tkbm = OaWorkFlowUtil.listAdd("tkbm", temp.getVtermcode());
				workflowRequestTableFields.add(tkbm);
			}
			// 条款名称
			Map tkmc = OaWorkFlowUtil.listAdd("tkmc", temp.getVtermname());
			workflowRequestTableFields.add(tkmc);
			// 条款类型
			Map tklx = OaWorkFlowUtil.listAdd("tklx", temp.getVtermtypename());
			workflowRequestTableFields.add(tklx);
			// 条款内容
			Map tknr = OaWorkFlowUtil.listAdd("tknr", temp.getVtermcontent());
			workflowRequestTableFields.add(tknr);
			// 其他信息
			Map qtxx = OaWorkFlowUtil.listAdd("qtxx", temp.getVotherinfo());
			workflowRequestTableFields.add(qtxx);
			// 备注
			Map bz = OaWorkFlowUtil.listAdd("bz", temp.getVmemo());
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
			Map jhbl = OaWorkFlowUtil.listAdd("jhbl", temp.getPlanrate()
					.setScale(2, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(jhbl);
			// 计划金额
			Map jhje = OaWorkFlowUtil.listAdd("jhje", temp.getPlanmoney()
					.setScale(2, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(jhje);
			// 结算金额
			Map jsje = OaWorkFlowUtil.listAdd("jsje", temp.getBalancemoney()
					.setScale(2, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(jsje);
			// 预付款
			Map yfk = OaWorkFlowUtil.listAdd("yfk", temp.getPaytype()
					.booleanValue() ? "是" : "否");
			workflowRequestTableFields.add(yfk);
			// 质保金
			Map zbj = OaWorkFlowUtil.listAdd("zbj", temp.getMoneytype()
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
			Map fklx = OaWorkFlowUtil.listAdd("fklx", fklxstr);
			workflowRequestTableFields.add(fklx);

			// 账期号
			Map zqh = OaWorkFlowUtil.listAdd("zqh", temp.getAccountnum()
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

	private String getDef2(String pk_org) throws BusinessException {
		String def2 = "";
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		if (null != orgVO) {
			def2 = orgVO.getDef2();
		}
		return def2;
	}
}
