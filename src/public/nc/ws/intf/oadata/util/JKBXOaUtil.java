package nc.ws.intf.oadata.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.trade.business.HYPubBO;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKBusItemVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.sm.UserVO;
import nc.ws.intf.OaWorkFlowUtil;
import net.sf.json.JSONArray;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class JKBXOaUtil {
	/* 员工借款单 差旅费借款单主表 */

	public static JSONArray getMainMap(JKBXHeaderVO parentVO)
			throws BusinessException {
		List list = new ArrayList();

		/* 单据主键 */
		// Map djzj =OaWorkFlowUtil.listAdd("djzj",parentVO.getPk_jkbx() );
		// list.add(djzj);
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getCreator());
		if (null != userVO) {
			/* 制单人名称 */
			Map zdrmc = OaWorkFlowUtil.listAdd("zdrmc", userVO.getUser_name());
			list.add(zdrmc);
			// 身份证号
			Map zdrzj = new HashMap();
			zdrzj.put("fieldName", "zdrzj");
			String id = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"id",
					"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc()
							+ "'");
			if ("2".equals(getDef2(parentVO.getPk_org()))) {
				if (null == id) {
					throw new BusinessException("制单人身份证号码未维护");
				}
				zdrzj.put("fieldValue", id);
				list.add(zdrzj);
			} else {
				zdrzj.put("fieldValue", userVO.getUser_code());
				list.add(zdrzj);
			}
		}

		/* 单据号 */
		Map djh = OaWorkFlowUtil.listAdd("djh", parentVO.getDjbh());
		list.add(djh);

		/* 单据日期 */
		Map djrq = OaWorkFlowUtil.listAdd("djrq", parentVO.getDjrq().getYear()
				+ "-" + parentVO.getDjrq().getStrMonth() + "-"
				+ parentVO.getDjrq().getStrDay());
		list.add(djrq);

		/* 申请人单位 */
		String dw = (String) getHyPubBO().findColValue(
				"org_adminorg",
				"name",
				"nvl(dr,0) = 0 and   pk_adminorg  ='" + parentVO.getDwbm()
						+ "'");
		Map sqdw = OaWorkFlowUtil.listAdd("sqdw", dw);
		list.add(sqdw);

		/* 申请部门 */
		String bm = (String) getHyPubBO()
				.findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and   pk_dept   ='"
								+ parentVO.getDeptid() + "'");
		Map sqrbm = OaWorkFlowUtil.listAdd("sqrbm", bm);
		list.add(sqrbm);

		/* 申请人 */
		Map sqr = OaWorkFlowUtil.listAdd("sqr", parentVO.getJkbxr());
		list.add(sqr);

		/* 借款人 */
		String mname = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and   pk_psndoc  ='" + parentVO.getReceiver()
						+ "'");
		Map jkr = OaWorkFlowUtil.listAdd("jkr", mname);
		list.add(jkr);

		/* 借款单位 */
		String jdw = (String) getHyPubBO().findColValue(
				"org_financeorg",
				"name",
				"nvl(dr,0) = 0 and    pk_financeorg   ='"
						+ parentVO.getPk_org() + "'");
		Map jkdw = OaWorkFlowUtil.listAdd("jkdw", jdw);
		list.add(jkdw);

		/* 借款人银行账户 */
		String yhzh = (String) getHyPubBO().findColValue(
				"bd_bankaccsub",
				"accnum",
				"nvl(dr,0) = 0 and   pk_bankaccsub  ='" + parentVO.getSkyhzh()
						+ "'");
		Map jkryxzh = OaWorkFlowUtil.listAdd("jkryxzh", yhzh);
		list.add(jkryxzh);

		/* 借款金额 */
		Map jkje = OaWorkFlowUtil.listAdd("jkje", parentVO.getTotal() + "");
		list.add(jkje);

		/* 借款金额大写 */
		Map jkjedx = OaWorkFlowUtil.listAdd("jkjedx",
				ConvertUpMoney.toChinese(parentVO.getTotal() + ""));
		list.add(jkjedx);

		/* 转账银行 */
		String zyx24 = "0";
		if (null != parentVO.getZyx24()) {
			String strWhere = "pk_defdoc = '"
					+ parentVO.getZyx24()
					+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'YH' and nvl(dr,0) = 0)";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				zyx24 = defdoc[0].getName();
			}
		}
		Map zzyx = OaWorkFlowUtil.listAdd("zzyx", zyx24);
		list.add(zzyx);

		/* 凭证号 */
		Map pzh = OaWorkFlowUtil.listAdd("pzh", parentVO.getZyx29());
		list.add(pzh);

		/* 支付日期 */
		Map zfrq = OaWorkFlowUtil.listAdd("zfrq", parentVO.getZyx21());
		list.add(zfrq);

		/* CBS状态 */
		Map cbszt = OaWorkFlowUtil.listAdd("cbszt", parentVO.getZyx22());
		list.add(cbszt);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	// 还款单
	public static Map getBXBusItemVO6(BxcontrastVO[] bvo, String pk_org)
			throws BusinessException {
		Map dtMap = new HashMap();

		List workflowRequestTableRecords = new ArrayList();
		for (BxcontrastVO temp : bvo) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();
			if ("2".equals(pk_org)) {
				dtMap.put("tableDBName", "formtable_main_566_dt1");
			} else {
				dtMap.put("tableDBName", "formtable_main_92_dt1");
			}
			/* 借款单号 */
			Map jkdh = OaWorkFlowUtil.listAdd("jkdh", temp.getJkdjbh());
			workflowRequestTableFields.add(jkdh);

			/* 还款金额 */
			Map hkje = OaWorkFlowUtil.listAdd("hkje", temp.getCjkybje()
					.setScale(2, UFDouble.ROUND_HALF_UP) + "");
			workflowRequestTableFields.add(hkje);

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);

		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	/* 员工借款单 */
	public static JSONArray getDtaileDataMap1(JKVO temp)
			throws BusinessException {
		Map orderMap = getJKBusItemVO1(temp.getChildrenVO(), getDef2(temp
				.getParentVO().getPk_org()));
		JSONArray dtlistString = JSONArray.fromObject(orderMap);
		return dtlistString;
	}

	/* 员工借款单子表 */
	private static Map getJKBusItemVO1(BXBusItemVO[] childrenVOS, String pk_org)
			throws BusinessException {
		Map dtMap = new HashMap();
		// dtMap.put("tableDBName", "formtable_main_113_dt1");
		// JKBusItemVO[] childrenVO = (JKBusItemVO[])childrenVOS;
		List workflowRequestTableRecords = new ArrayList();
		for (BXBusItemVO temp1 : childrenVOS) {
			JKBusItemVO temp = (JKBusItemVO) temp1;
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();

			if ("2".equals(pk_org)) {
				dtMap.put("tableDBName", "formtable_main_424_dt1");
			} else {
				dtMap.put("tableDBName", "formtable_main_113_dt1");
			}
			/* 借款事由 */
			Map jksy = OaWorkFlowUtil.listAdd("jksy", temp.getDefitem30());
			workflowRequestTableFields.add(jksy);

			/* 金额 */
			Map je = OaWorkFlowUtil.listAdd("je", temp.getAmount() + "");
			workflowRequestTableFields.add(je);

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);

		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	public static JSONArray getDtaileDataMap6(BXVO bxVO)
			throws BusinessException {
		Map orderMap = getBXBusItemVO6(bxVO.getContrastVO(), getDef2(bxVO
				.getParentVO().getPk_org()));
		JSONArray dtlistString = JSONArray.fromObject(orderMap);
		return dtlistString;
	}

	public static JSONArray getDtaileDataMap2(JKVO temp)
			throws BusinessException {
		Map orderMap = getJKBusItemVO2(temp.getChildrenVO(), getDef2(temp
				.getParentVO().getPk_org()));
		JSONArray dtlistString = JSONArray.fromObject(orderMap);
		return dtlistString;
	}

	// 差旅费借款单子表
	private static Map getJKBusItemVO2(BXBusItemVO[] bxBusItemVOs, String pk_org) {
		Map dtMap = new HashMap();
		// dtMap.put("tableDBName", "formtable_main_112_dt1");
		// JKBusItemVO[] childrenVO = (JKBusItemVO[])childrenVOS;
		List workflowRequestTableRecords = new ArrayList();
		for (BXBusItemVO temp1 : bxBusItemVOs) {
			JKBusItemVO temp = (JKBusItemVO) temp1;
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();
			if ("2".equals(pk_org)) {
				dtMap.put("tableDBName", "formtable_main_425_dt1");
			} else {
				dtMap.put("tableDBName", "formtable_main_112_dt1");
			}
			/* 借款事由 */
			Map jksy = OaWorkFlowUtil.listAdd("jksy", temp.getDefitem30());
			workflowRequestTableFields.add(jksy);

			/* 金额 */
			Map je = OaWorkFlowUtil.listAdd("je", temp.getAmount() + "");
			workflowRequestTableFields.add(je);

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);

		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	/* 共有主表数据 */
	/* 加班餐费报销单/采暖费退费审批单/员工费用报销单/配套及热计量退费审/差旅费报销单 */
	public static List getList(JKBXHeaderVO parentVO, List list)
			throws BusinessException {

		// /* 单据主键*/
		// Map djzj =OaWorkFlowUtil.listAdd("djzj",parentVO.getPk_jkbx() );
		// list.add(djzj);
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getCreator());
		if (null != userVO) {
			/* 制单人名称 */
			Map zdrmc = OaWorkFlowUtil.listAdd("zdrmc", userVO.getUser_name());
			list.add(zdrmc);
			// 身份证号
			Map zdrzj = new HashMap();
			zdrzj.put("fieldName", "zdrzj");
			String id = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"id",
					"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc()
							+ "'");
			if ("2".equals(getDef2(parentVO.getPk_org()))) {
				/* 开户行 */
				Map khh = OaWorkFlowUtil.listAdd("khh", parentVO.getZyx28());
				list.add(khh);
				if (null == id) {
					throw new BusinessException("制单人身份证号码未维护");
				}
				zdrzj.put("fieldValue", id);
				list.add(zdrzj);
			} else {
				zdrzj.put("fieldValue", userVO.getUser_code());
				list.add(zdrzj);
			}
		}
		/* 单据号 */
		Map djh = OaWorkFlowUtil.listAdd("djh", parentVO.getDjbh());
		list.add(djh);

		/* 单据日期 */
		Map djrq = OaWorkFlowUtil.listAdd("djrq", parentVO.getDjrq().getYear()
				+ "-" + parentVO.getDjrq().getStrMonth() + "-"
				+ parentVO.getDjrq().getStrDay());
		list.add(djrq);

		/* 申请单位 */
		String dw = (String) getHyPubBO().findColValue(
				"org_adminorg",
				"name",
				"nvl(dr,0) = 0 and   pk_adminorg  ='" + parentVO.getDwbm()
						+ "'");
		Map sqdw = OaWorkFlowUtil.listAdd("sqdw", dw);
		list.add(sqdw);

		/* 报销单位 */
		String vname = (String) getHyPubBO().findColValue(
				"org_financeorg",
				"name",
				"nvl(dr,0) = 0 and   pk_financeorg  ='" + parentVO.getPk_org()
						+ "'");
		Map bxdw = OaWorkFlowUtil.listAdd("bxdw", vname);
		list.add(bxdw);

		/* 申请部门 */
		String bm = (String) getHyPubBO()
				.findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and   pk_dept   ='"
								+ parentVO.getDeptid() + "'");
		Map sqbm = OaWorkFlowUtil.listAdd("sqbm", bm);
		list.add(sqbm);

		/* 申请人 */
		Map sqr = OaWorkFlowUtil.listAdd("sqr", parentVO.getJkbxr());
		list.add(sqr);

		/* 附件张数 */
		Map fjzs = OaWorkFlowUtil.listAdd("fjzs", parentVO.getFjzs() + "");
		list.add(fjzs);

		/* 转账银行 */
		String zyx24 = "0";
		if (null != parentVO.getZyx24()) {
			String strWhere = "pk_defdoc = '"
					+ parentVO.getZyx24()
					+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'YH' and nvl(dr,0) = 0)";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				zyx24 = defdoc[0].getName();
			}
		}
		Map zzyx = OaWorkFlowUtil.listAdd("zzyx", zyx24 + "");
		list.add(zzyx);

		/* 凭证号 */
		Map pzh = OaWorkFlowUtil.listAdd("pzh", parentVO.getZyx29());
		list.add(pzh);

		/* 支付日期 */
		Map zfrq = OaWorkFlowUtil.listAdd("zfrq", parentVO.getZyx21());
		list.add(zfrq);

		/* CBS状态 */
		Map cbszt = OaWorkFlowUtil.listAdd("cbszt", parentVO.getZyx22());
		list.add(cbszt);

		return list;
	}

	// 员工费用报销单主表
	public static JSONArray getMainMap3(JKBXHeaderVO parentVO)
			throws BusinessException {
		List list = new ArrayList();
		getList(parentVO, list);

		/* 代控部门主键 */
		Map dkbmzj = OaWorkFlowUtil.listAdd("dkbmzj", parentVO.getFydeptid());
		list.add(dkbmzj);

		/* 代控部门编码 */
		String dkbmbmstr = (String) getHyPubBO()
				.findColValue(
						"org_dept",
						"code",
						"nvl(dr,0) = 0 and pk_dept  = '"
								+ parentVO.getFydeptid() + "'");
		Map dkbmbm = OaWorkFlowUtil.listAdd("dkbmbm", dkbmbmstr);
		list.add(dkbmbm);

		/* 代控部门 */
		String dkbmmcstr = (String) getHyPubBO()
				.findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept  = '"
								+ parentVO.getFydeptid() + "'");
		Map dkbm = OaWorkFlowUtil.listAdd("dkbm", dkbmmcstr);
		list.add(dkbm);

		/* 费用承担单位 */
		String vname = (String) getHyPubBO().findColValue("org_orgs", "name",
				"nvl(dr,0) = 0 and   pk_org  ='" + parentVO.getFydwbm() + "'");
		Map fycddw = OaWorkFlowUtil.listAdd("fycddw", vname);
		list.add(fycddw);

		/* 付款事由 */
		Map fksy = OaWorkFlowUtil.listAdd("fksy", parentVO.getZyx30());
		list.add(fksy);

		/* 支付金额大写 */
		Map zfjedx = OaWorkFlowUtil.listAdd("zfjedx",
				ConvertUpMoney.toChinese(parentVO.getZfbbje() + ""));
		list.add(zfjedx);

		/* 现金流量项目 */
		Map xjllxm = OaWorkFlowUtil.listAdd("xjllxm", parentVO.getCashitem());
		list.add(xjllxm);

		/* 收款人 */
		String mname = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and   pk_psndoc  ='" + parentVO.getReceiver()
						+ "'");
		Map skr = OaWorkFlowUtil.listAdd("skr", mname);
		list.add(skr);

		/* 个人银行账户 */
		String yhzh = (String) getHyPubBO().findColValue(
				"bd_bankaccsub",
				"accnum",
				"nvl(dr,0) = 0 and   pk_bankaccsub  ='" + parentVO.getSkyhzh()
						+ "'");
		Map gryxzh = OaWorkFlowUtil.listAdd("gryxzh", yhzh);
		list.add(gryxzh);

		/* 报销金额 */
		Map bxje = OaWorkFlowUtil
				.listAdd("bxje", parentVO.getVat_amount() + "");
		list.add(bxje);

		/* 冲借款金额 */
		String je = ((UFDouble) (parentVO.getCjkbbje() == null ? "0.00"
				: parentVO.getCjkbbje())).setScale(2, UFDouble.ROUND_HALF_UP)
				+ "";
		Map cjkje = OaWorkFlowUtil.listAdd("cjkje", je);
		list.add(cjkje);

		/* 支付金额 */
		Map zfje = OaWorkFlowUtil.listAdd("zfje", parentVO.getZfbbje() + "");
		list.add(zfje);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	// 员工费用报销单
	public static JSONArray getDtaileDataMap3(BXVO bxVO)
			throws BusinessException {
		Map orderMap = getBXBusItemVO3((BXBusItemVO[]) bxVO.getChildrenVO(),
				getDef2(bxVO.getParentVO().getPk_org()));
		JSONArray dtlistString = JSONArray.fromObject(orderMap);
		return dtlistString;
	}

	public static Map getBXBusItemVO3(BXBusItemVO[] childrenVO, String pk_org)
			throws BusinessException {
		Map dtMap = new HashMap();
		// dtMap.put("tableDBName", "formtable_main_11_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (BXBusItemVO temp : childrenVO) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();

			if ("2".equals(pk_org)) {
				dtMap.put("tableDBName", "formtable_main_417_dt1");
			} else {
				dtMap.put("tableDBName", "formtable_main_11_dt1");
			}
			/* 预算类别 */
			String lb = (String) getHyPubBO().findColValue(
					"bd_inoutbusiclass",
					"name",
					"nvl(dr,0) = 0 and   pk_inoutbusiclass  ='"
							+ temp.getSzxmid() + "'");
			Map yslb = OaWorkFlowUtil.listAdd("yslb", lb);
			workflowRequestTableFields.add(yslb);

			/* 费用发生部门 */
			String bm = (String) getHyPubBO()
					.findColValue(
							"org_dept",
							"name",
							"nvl(dr,0) = 0 and   pk_dept   ='"
									+ temp.getDeptid() + "'");
			Map fyfsbm = OaWorkFlowUtil.listAdd("fyfsbm", bm);
			workflowRequestTableFields.add(fyfsbm);

			/* 发票类型 */
			String defitem6 = "0";
			if (null != temp.getDefitem6()) {
				String strWhere = "pk_defdoc = '"
						+ temp.getDefitem6()
						+ "' and nvl(dr,0) = 0 ";
				DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
						DefdocVO.class, strWhere);
				if (null != defdoc && defdoc.length == 1) {
					defitem6 = defdoc[0].getName();
				}
			}
			if(defitem6 == null){
				defitem6 = temp.getDefitem6();
			}
			Map fplx = OaWorkFlowUtil.listAdd("fplx", defitem6);
			workflowRequestTableFields.add(fplx);

			/* 金额合计 */
			Map jehj = OaWorkFlowUtil
					.listAdd("jehj", temp.getVat_amount() + "");
			workflowRequestTableFields.add(jehj);

			/* 税率 */
			String def7 = "0";
			if (null != temp.getDefitem17()) {
				String strWhere = "pk_defdoc = '"
						+ temp.getDefitem17()
						+ "' and nvl(dr,0) = 0 ";
				DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
						DefdocVO.class, strWhere);
				if (null != defdoc && defdoc.length == 1) {
					def7 = defdoc[0].getName();
				}
			}
			if(def7 == null){
				def7 = temp.getDefitem17();
			}
			Map sl = OaWorkFlowUtil.listAdd("sl", def7);
			workflowRequestTableFields.add(sl);

			/* 税额 */
			Map se = OaWorkFlowUtil.listAdd("se", temp.getDefitem19());
			workflowRequestTableFields.add(se);

			/* 不含税金额 */
			Map bhsje = OaWorkFlowUtil.listAdd("bhsje", temp.getDefitem15());
			workflowRequestTableFields.add(bhsje);

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);

		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	// 配套及热计量退费审批单主表
	public static JSONArray getMainMap4(JKBXHeaderVO parentVO)
			throws BusinessException {
		List list = new ArrayList();
		getList(parentVO, list);

		/* 费用承担单位 */
		String vname = (String) getHyPubBO().findColValue("org_orgs", "name",
				"nvl(dr,0) = 0 and   pk_org  ='" + parentVO.getFydwbm() + "'");
		Map fycddw = OaWorkFlowUtil.listAdd("fycddw", vname);
		list.add(fycddw);

		/* 退费金额大写 */
		Map tfjedx = OaWorkFlowUtil.listAdd("tfjedx", ConvertUpMoney.toChinese(parentVO.getVat_amount() + ""));
		list.add(tfjedx);

		/* 退费说明 */
		Map tfsm = OaWorkFlowUtil.listAdd("tfsm", parentVO.getZyx30());
		list.add(tfsm);

		/* 退费所属部门 */
		String bm = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and   pk_dept   ='" + parentVO.getZyx2() + "'");
		Map tfszbm = OaWorkFlowUtil.listAdd("tfszbm", bm);
		list.add(tfszbm);

		/* 收款方 */
		Map skf = OaWorkFlowUtil.listAdd("skf", parentVO.getZyx11());
		list.add(skf);

		/* 退费金额 */
		Map tfje = OaWorkFlowUtil.listAdd("tfje", parentVO.getVat_amount()
				.setScale(2, UFDouble.ROUND_HALF_UP) + "");
		list.add(tfje);

		/* 收款方开户行 */
		String khh = (String) getHyPubBO().findColValue(
				"bd_asslinenum",
				"name",
				"nvl(dr,0) = 0 and    pk_asslinenum  ='" + parentVO.getZyx9()
						+ "'");
		Map skfkhx = OaWorkFlowUtil.listAdd("skfkhx", khh);
		list.add(skfkhx);

		/* 收款方银行账号 */
		Map skfyxzh = OaWorkFlowUtil.listAdd("skfyxzh", parentVO.getZyx14());
		list.add(skfyxzh);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	// 配套及热计量退费审批单
	public static JSONArray getDtaileDataMap4(BXVO bxVO)
			throws BusinessException {
		Map orderMap = getBXBusItemVO4((BXBusItemVO[]) bxVO.getChildrenVO(),
				getDef2(bxVO.getParentVO().getPk_org()));
		JSONArray dtlistString = JSONArray.fromObject(orderMap);
		return dtlistString;
	}

	// 配套及热计量退费审批单子表
	public static Map getBXBusItemVO4(BXBusItemVO[] childrenVO, String pk_org)
			throws BusinessException {
		Map dtMap = new HashMap();
		// dtMap.put("tableDBName", "formtable_main_162_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (BXBusItemVO temp : childrenVO) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();

			if ("2".equals(pk_org)) {
				dtMap.put("tableDBName", "formtable_main_413_dt1");
			} else {
				dtMap.put("tableDBName", "formtable_main_162_dt1");
			}
			/* 退费类型 */
			String z40 = "0";
			if (null != temp.getDefitem40()) {
				String strWhere = "pk_defdoc = '"
						+ temp.getDefitem40()
						+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'tflx' and nvl(dr,0) = 0)";
				DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
						DefdocVO.class, strWhere);
				if (null != defdoc && defdoc.length == 1) {
					z40 = defdoc[0].getName();
				}
			}
			Map tflx = OaWorkFlowUtil.listAdd("tflx", z40);
			workflowRequestTableFields.add(tflx);

			/* 退费金额 */
			Map tfje = OaWorkFlowUtil.listAdd("tfje", temp.getVat_amount()
					.setScale(2, UFDouble.ROUND_HALF_UP) + "");
			workflowRequestTableFields.add(tfje);

			/* 是否热电用户 */
			String zyx50 = "0";
			if (null != temp.getDefitem50()) {
				String strWhere = "pk_defdoc = '"
						+ temp.getDefitem50()
						+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'sf' and nvl(dr,0) = 0)";
				DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
						DefdocVO.class, strWhere);
				if (null != defdoc && defdoc.length == 1) {
					zyx50 = defdoc[0].getName();
				}
			}
			Map sfrdyh = OaWorkFlowUtil.listAdd("sfrdyh", zyx50 + "");
			workflowRequestTableFields.add(sfrdyh);

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);

		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	// 差旅费报销单主表
	public static JSONArray getMainMap5(JKBXHeaderVO parentVO)
			throws BusinessException {
		List list = new ArrayList();
		getList(parentVO, list);

		/* 费用承担单位 */
		String vname = (String) getHyPubBO().findColValue("org_orgs", "name",
				"nvl(dr,0) = 0 and   pk_org  ='" + parentVO.getFydwbm() + "'");
		Map fycddw = OaWorkFlowUtil.listAdd("fycddw", vname);
		list.add(fycddw);

		/* 支付金额大写 */
		Map zfjedx = OaWorkFlowUtil.listAdd("zfjedx", ConvertUpMoney.toChinese(parentVO.getZfbbje() + ""));
		list.add(zfjedx);

		/* 出差事由 */
		Map ccsy = OaWorkFlowUtil.listAdd("ccsy", parentVO.getZyx30());
		list.add(ccsy);

		/* 现金流量项目 */
		Map xjllxm = OaWorkFlowUtil.listAdd("xjllxm", parentVO.getCashitem());
		list.add(xjllxm);

		/* 代控部门主键 */
		Map dkbmzj = OaWorkFlowUtil.listAdd("dkbmzj", parentVO.getFydeptid());
		list.add(dkbmzj);

		/* 代控部门编码 */
		String dkbmbmstr = (String) getHyPubBO()
				.findColValue(
						"org_dept",
						"code",
						"nvl(dr,0) = 0 and pk_dept  = '"
								+ parentVO.getFydeptid() + "'");
		Map dkbmbm = OaWorkFlowUtil.listAdd("dkbmbm", dkbmbmstr);
		list.add(dkbmbm);

		/* 代控部门 */
		String dkbmmcstr = (String) getHyPubBO()
				.findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept  = '"
								+ parentVO.getFydeptid() + "'");
		Map dkbm = OaWorkFlowUtil.listAdd("dkbm", dkbmmcstr);
		list.add(dkbm);

		/* 收款人 */
		String mname = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and   pk_psndoc  ='" + parentVO.getReceiver()
						+ "'");
		Map skr = OaWorkFlowUtil.listAdd("skr", mname);
		list.add(skr);

		/* 个人银行账户 */
		String yhzh = (String) getHyPubBO().findColValue(
				"bd_bankaccsub",
				"accnum",
				"nvl(dr,0) = 0 and   pk_bankaccsub  ='" + parentVO.getSkyhzh()
						+ "'");
		Map gryxzh = OaWorkFlowUtil.listAdd("gryxzh", yhzh);
		list.add(gryxzh);

		/* 报销金额 */
		Map bxje = OaWorkFlowUtil
				.listAdd("bxje", parentVO.getVat_amount() + "");
		list.add(bxje);

		/* 冲借款金额 */
		String je = ((UFDouble) (parentVO.getCjkbbje() == null ? "0.00"
				: parentVO.getCjkbbje())).setScale(2, UFDouble.ROUND_HALF_UP)
				+ "";
		Map cjkje = OaWorkFlowUtil.listAdd("cjkje", je);
		list.add(cjkje);

		/* 支付金额 */
		Map zfje = OaWorkFlowUtil.listAdd("zfje", parentVO.getZfbbje() + "");
		list.add(zfje);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	// 差旅费报销单
	public static JSONArray getDtaileDataMap5(BXVO bxVO)
			throws BusinessException {
		Map orderMap = getBXBusItemVO5((BXBusItemVO[]) bxVO.getChildrenVO(),
				getDef2(bxVO.getParentVO().getPk_org()));
		JSONArray dtlistString = JSONArray.fromObject(orderMap);
		return dtlistString;
	}

	// 差旅费报销单子表
	private static Map getBXBusItemVO5(BXBusItemVO[] childrenVO, String pk_org)
			throws BusinessException {
		Map dtMap = new HashMap();
		// dtMap.put("tableDBName", "formtable_main_85_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (BXBusItemVO temp : childrenVO) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();

			if ("2".equals(pk_org)) {
				dtMap.put("tableDBName", "formtable_main_423_dt1");
			} else {
				dtMap.put("tableDBName", "formtable_main_85_dt1");
			}
			/* 费用发生部门 */
			String bm = (String) getHyPubBO()
					.findColValue(
							"org_dept",
							"name",
							"nvl(dr,0) = 0 and   pk_dept   ='"
									+ temp.getDeptid() + "'");
			Map fyfsbm = OaWorkFlowUtil.listAdd("fyfsbm", bm);
			workflowRequestTableFields.add(fyfsbm);

			/* 报销类型 */
			String bx = (String) getHyPubBO().findColValue(
					"er_reimtype",
					"name",
					"nvl(dr,0) = 0 and   pk_reimtype    ='"
							+ temp.getPk_reimtype() + "'");
			Map bxlx = OaWorkFlowUtil.listAdd("bxlx", bx);
			workflowRequestTableFields.add(bxlx);

			/* 预算类别 */
			String lb = (String) getHyPubBO().findColValue(
					"bd_inoutbusiclass",
					"name",
					"nvl(dr,0) = 0 and   pk_inoutbusiclass  ='"
							+ temp.getSzxmid() + "'");
			Map yslb = OaWorkFlowUtil.listAdd("yslb", lb);
			workflowRequestTableFields.add(yslb);

			/* 出差人数 */
			Map ccrs = OaWorkFlowUtil.listAdd("ccrs", temp.getDefitem45());
			workflowRequestTableFields.add(ccrs);

			/* 出发日期 */
			Map cfrq = OaWorkFlowUtil.listAdd("cfrq", temp.getDefitem1());
			workflowRequestTableFields.add(cfrq);

			/* 返程日期 */
			Map fcrq = OaWorkFlowUtil.listAdd("fcrq", temp.getDefitem2());
			workflowRequestTableFields.add(fcrq);

			/* 出差天数 */
			Map ccts = OaWorkFlowUtil.listAdd("ccts", temp.getDefitem9());
			workflowRequestTableFields.add(ccts);

			/* 出发地点 */
			String defitem3 = (String) getHyPubBO().findColValue(
					"bd_addressdoc",
					"name",
					"nvl(dr,0) = 0 and    pk_addressdoc   ='"
							+ temp.getDefitem3() + "'");
			Map cfdd = OaWorkFlowUtil.listAdd("cfdd", defitem3);
			workflowRequestTableFields.add(cfdd);

			/* 目的地点 */
			String defitem4 = (String) getHyPubBO().findColValue(
					"bd_addressdoc",
					"name",
					"nvl(dr,0) = 0 and    pk_addressdoc   ='"
							+ temp.getDefitem4() + "'");
			Map mddd = OaWorkFlowUtil.listAdd("mddd", defitem4);
			workflowRequestTableFields.add(mddd);

			/* 交通工具 */
			String defitem5 = "0";
			if (null != temp.getDefitem5()) {
				String strWhere = "pk_defdoc = '"
						+ temp.getDefitem5()
						+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'JTGJ' and nvl(dr,0) = 0)";
				DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
						DefdocVO.class, strWhere);
				if (null != defdoc && defdoc.length == 1) {
					defitem5 = defdoc[0].getName();
				}
			}
			Map jtgj = OaWorkFlowUtil.listAdd("jtgj", defitem5);
			workflowRequestTableFields.add(jtgj);

			/* 标准 */
			Map bz = OaWorkFlowUtil.listAdd("bz", temp.getAmount() + "");
			workflowRequestTableFields.add(bz);

			/* 发票类型 */
			String defitem6 = "0";
			if (null != temp.getDefitem6()) {
				String strWhere = "pk_defdoc = '"
						+ temp.getDefitem6()
						+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'fplx' and nvl(dr,0) = 0)";
				DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
						DefdocVO.class, strWhere);
				if (null != defdoc && defdoc.length == 1) {
					defitem6 = defdoc[0].getName();
				}
			}
			Map fplx = OaWorkFlowUtil.listAdd("fplx", defitem6);
			workflowRequestTableFields.add(fplx);

			/* 金额合计 */
			Map jehj = OaWorkFlowUtil
					.listAdd("jehj", temp.getVat_amount() + "");
			workflowRequestTableFields.add(jehj);

			/* 税率 */
			String defitem17 = "0";
			if (null != temp.getDefitem17()) {
				String strWhere = "pk_defdoc = '"
						+ temp.getDefitem17()
						+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'GX-02' and nvl(dr,0) = 0)";
				DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
						DefdocVO.class, strWhere);
				if (null != defdoc && defdoc.length == 1) {
					defitem17 = defdoc[0].getName();
				}
			}
			Map sl = OaWorkFlowUtil.listAdd("sl", defitem17);
			workflowRequestTableFields.add(sl);

			/* 税额 */
			Map se = OaWorkFlowUtil.listAdd("se", temp.getDefitem19());
			workflowRequestTableFields.add(se);

			/* 备注 */
			Map bz1 = OaWorkFlowUtil.listAdd("bz1", temp.getDefitem31());
			workflowRequestTableFields.add(bz1);

			/* 不含税金额 */
			Map bhsje = OaWorkFlowUtil.listAdd("bhsje", temp.getDefitem15());
			workflowRequestTableFields.add(bhsje);

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);

		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	// 还款单主表
	public static JSONArray getMainMap6(JKBXHeaderVO parentVO)
			throws BusinessException {
		List list = new ArrayList();

		// /* 单据主键*/
		// Map djzj =OaWorkFlowUtil.listAdd("djzj",parentVO.getPk_jkbx() );
		// list.add(djzj);
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getCreator());
		if (null != userVO) {
			/* 制单人名称 */
			Map zdrmc = OaWorkFlowUtil.listAdd("zdrmc", userVO.getUser_name());
			list.add(zdrmc);
			// 身份证号
			Map zdrzj = new HashMap();
			zdrzj.put("fieldName", "zdrzj");
			String id = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"id",
					"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc()
							+ "'");
			if ("2".equals(getDef2(parentVO.getPk_org()))) {
				if (null == id) {
					throw new BusinessException("制单人身份证号码未维护");
				}
				zdrzj.put("fieldValue", id);
				list.add(zdrzj);
			} else {
				zdrzj.put("fieldValue", userVO.getUser_code());
				list.add(zdrzj);
			}
		}
		/* 单据号 */
		Map djh = OaWorkFlowUtil.listAdd("djh", parentVO.getDjbh());
		list.add(djh);

		/* 单据日期 */
		Map djrq = OaWorkFlowUtil.listAdd("djrq", parentVO.getDjrq().getYear()
				+ "-" + parentVO.getDjrq().getStrMonth() + "-"
				+ parentVO.getDjrq().getStrDay());
		list.add(djrq);

		/* 申请单位 */
		String dw = (String) getHyPubBO().findColValue(
				"org_adminorg",
				"name",
				"nvl(dr,0) = 0 and   pk_adminorg  ='" + parentVO.getDwbm()
						+ "'");
		Map sqdw = OaWorkFlowUtil.listAdd("sqdw", dw);
		list.add(sqdw);

		/* 报销单位 */
		String vname = (String) getHyPubBO().findColValue(
				"org_financeorg",
				"name",
				"nvl(dr,0) = 0 and   pk_financeorg  ='" + parentVO.getPk_org()
						+ "'");
		Map bxdw = OaWorkFlowUtil.listAdd("bxdw", vname);
		list.add(bxdw);

		/* 申请部门 */
		String bm = (String) getHyPubBO()
				.findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and   pk_dept   ='"
								+ parentVO.getDeptid() + "'");
		Map sqbm = OaWorkFlowUtil.listAdd("sqbm", bm);
		list.add(sqbm);

		/* 还款人 */
		String mname = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and   pk_psndoc  ='" + parentVO.getReceiver()
						+ "'");
		Map hkr = OaWorkFlowUtil.listAdd("hkr", mname);
		list.add(hkr);

		/* 还款事由 */
		Map hksy = OaWorkFlowUtil.listAdd("hksy", parentVO.getZyx30());
		list.add(hksy);

		/* 还款金额大写 */
		Map hkjedx = OaWorkFlowUtil.listAdd("hkjedx", ConvertUpMoney.toChinese(parentVO.getHkybje() + ""));
		list.add(hkjedx);

		/* 还款金额 */
		Map hkje = OaWorkFlowUtil.listAdd("hkje", parentVO.getHkybje() + "");
		list.add(hkje);

		/* 还款银行 */
		String zyx24 = "0";
		if (null != parentVO.getZyx24()) {
			String strWhere = "pk_defdoc = '"
					+ parentVO.getZyx24()
					+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'YH' and nvl(dr,0) = 0)";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				zyx24 = defdoc[0].getName();
			}
		}
		Map hkyx = OaWorkFlowUtil.listAdd("hkyx", zyx24);
		list.add(hkyx);

		/* 附件张数 */
		Map fjzs = OaWorkFlowUtil.listAdd("fjzs", parentVO.getFjzs() + "");
		list.add(fjzs);

		/* 凭证号 */
		Map pzh = OaWorkFlowUtil.listAdd("pzh", parentVO.getZyx18());
		list.add(pzh);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	// 加班餐费报销单主表
	public static JSONArray getMainMap7(JKBXHeaderVO parentVO)
			throws BusinessException {
		List list = new ArrayList();
		getList(parentVO, list);

		/* 费用承担单位 */
		String vname = (String) getHyPubBO().findColValue("org_orgs", "name",
				"nvl(dr,0) = 0 and   pk_org  ='" + parentVO.getFydwbm() + "'");
		Map fycddw = OaWorkFlowUtil.listAdd("fycddw", vname);
		list.add(fycddw);

		/* 转账银行 */
		String zyx24 = "0";
		if (null != parentVO.getZyx24()) {
			String strWhere = "pk_defdoc = '"
					+ parentVO.getZyx24()
					+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'YH' and nvl(dr,0) = 0)";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				zyx24 = defdoc[0].getName();
			}
		}
		Map zzyx = OaWorkFlowUtil.listAdd("zzyx", zyx24);
		list.add(zzyx);

		/* 代控部门主键 */
		Map dkbmzj = OaWorkFlowUtil.listAdd("dkbmzj", parentVO.getFydeptid());
		list.add(dkbmzj);

		/* 代控部门编码 */
		String dkbmbmstr = (String) getHyPubBO()
				.findColValue(
						"org_dept",
						"code",
						"nvl(dr,0) = 0 and pk_dept  = '"
								+ parentVO.getFydeptid() + "'");
		Map dkbmbm = OaWorkFlowUtil.listAdd("dkbmbm", dkbmbmstr);
		list.add(dkbmbm);

		/* 代控部门 */
		String dkbmmcstr = (String) getHyPubBO()
				.findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept  = '"
								+ parentVO.getFydeptid() + "'");
		Map dkbm = OaWorkFlowUtil.listAdd("dkbm", dkbmmcstr);
		list.add(dkbm);

		/* 付款事由 */
		Map fksy = OaWorkFlowUtil.listAdd("fksy", parentVO.getZyx30());
		list.add(fksy);

		/* 支付金额大写 */
		Map zfjedx = OaWorkFlowUtil.listAdd("zfjedx", ConvertUpMoney.toChinese(parentVO.getZfbbje() + ""));
		list.add(zfjedx);

		/* 现金流量项目 */
		Map xjllxm = OaWorkFlowUtil.listAdd("xjllxm", parentVO.getCashitem());
		list.add(xjllxm);

		/* 收款人 */
		String mname = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and   pk_psndoc  ='" + parentVO.getReceiver()
						+ "'");
		Map skr = OaWorkFlowUtil.listAdd("skr", mname);
		list.add(skr);

		/* 个人银行账户 */
		String yhzh = (String) getHyPubBO().findColValue(
				"bd_bankaccsub",
				"accnum",
				"nvl(dr,0) = 0 and   pk_bankaccsub  ='" + parentVO.getSkyhzh()
						+ "'");
		Map gryxzh = OaWorkFlowUtil.listAdd("gryxzh", yhzh);
		list.add(gryxzh);

		/* 报销金额 */
		Map bxje = OaWorkFlowUtil
				.listAdd("bxje", parentVO.getVat_amount() + "");
		list.add(bxje);

		/* 支付金额 */
		Map zfje = OaWorkFlowUtil.listAdd("zfje", parentVO.getZfbbje() + "");
		list.add(zfje);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	// 加班餐费报销单
	public static JSONArray getDtaileDataMap7(BXVO bxVO)
			throws BusinessException {
		Map orderMap = getBXBusItemVO7((BXBusItemVO[]) bxVO.getChildrenVO(),
				getDef2(bxVO.getParentVO().getPk_org()));
		JSONArray dtlistString = JSONArray.fromObject(orderMap);
		return dtlistString;
	}

	// 加班餐费报销单子表
	private static Map getBXBusItemVO7(BXBusItemVO[] childrenVO, String pk_org)
			throws BusinessException {
		Map dtMap = new HashMap();
		// dtMap.put("tableDBName", "formtable_main_93_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (BXBusItemVO temp : childrenVO) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();

			if ("2".equals(pk_org)) {
				dtMap.put("tableDBName", "formtable_main_418_dt1");
			} else {
				dtMap.put("tableDBName", "formtable_main_93_dt1");
			}
			/* 加班事由 */
			Map jbsy = OaWorkFlowUtil.listAdd("jbsy", temp.getDefitem20());
			workflowRequestTableFields.add(jbsy);

			/* 预算类别 */
			String lb = (String) getHyPubBO().findColValue(
					"bd_inoutbusiclass",
					"name",
					"nvl(dr,0) = 0 and   pk_inoutbusiclass  ='"
							+ temp.getSzxmid() + "'");
			Map yslb = OaWorkFlowUtil.listAdd("yslb", lb);
			workflowRequestTableFields.add(yslb);

			/* 费用发生部门 */
			String bm = (String) getHyPubBO()
					.findColValue(
							"org_dept",
							"name",
							"nvl(dr,0) = 0 and   pk_dept   ='"
									+ temp.getDeptid() + "'");
			Map fyfsbm = OaWorkFlowUtil.listAdd("fyfsbm", bm);
			workflowRequestTableFields.add(fyfsbm);

			/* 加班人员名单 */
			Map jbrymd = OaWorkFlowUtil.listAdd("jbrymd", temp.getDefitem23());
			workflowRequestTableFields.add(jbrymd);

			/* 加班人数 */
			Map jbrs = OaWorkFlowUtil.listAdd("jbrs", temp.getDefitem29());
			workflowRequestTableFields.add(jbrs);

			/* 加班开始时间 */
			Map jbkssj = OaWorkFlowUtil.listAdd("jbkssj", temp.getDefitem21());
			workflowRequestTableFields.add(jbkssj);

			/* 加班结束时间 */
			Map jbjssj = OaWorkFlowUtil.listAdd("jbjssj", temp.getDefitem22());
			workflowRequestTableFields.add(jbjssj);

			/* 标准 */
			Map bz = OaWorkFlowUtil.listAdd("bz", temp.getDefitem30());
			workflowRequestTableFields.add(bz);

			/* 金额合计 */
			Map jehj = OaWorkFlowUtil
					.listAdd("jehj", temp.getVat_amount() + "");
			workflowRequestTableFields.add(jehj);

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);

		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	// 采暖费退费审批单主表
	public static JSONArray getMainMap8(JKBXHeaderVO parentVO)
			throws BusinessException {
		List list = new ArrayList();
		getList(parentVO, list);

		/* 费用承担单位 */
		String vname = (String) getHyPubBO().findColValue("org_orgs", "name",
				"nvl(dr,0) = 0 and   pk_org  ='" + parentVO.getFydwbm() + "'");
		Map fycddw = OaWorkFlowUtil.listAdd("fycddw", vname);
		list.add(fycddw);

		/* 代控部门主键 */
		Map dkbmzj = OaWorkFlowUtil.listAdd("dkbmzj", parentVO.getFydeptid());
		list.add(dkbmzj);

		/* 代控部门编码 */
		String dkbmbmstr = (String) getHyPubBO()
				.findColValue(
						"org_dept",
						"code",
						"nvl(dr,0) = 0 and pk_dept  = '"
								+ parentVO.getFydeptid() + "'");
		Map dkbmbm = OaWorkFlowUtil.listAdd("dkbmbm", dkbmbmstr);
		list.add(dkbmbm);

		/* 代控部门 */
		String dkbmmcstr = (String) getHyPubBO()
				.findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept  = '"
								+ parentVO.getFydeptid() + "'");
		Map dkbm = OaWorkFlowUtil.listAdd("dkbm", dkbmmcstr);
		list.add(dkbm);

		/* 退费说明 */
		Map tfsm = OaWorkFlowUtil.listAdd("tfsm", parentVO.getZyx30());
		list.add(tfsm);

		/*
		 * 转账银行 String zyx24 = "0"; if (null != parentVO.getZyx24()) { String
		 * strWhere = "pk_defdoc = '" + parentVO.getZyx24() +
		 * "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'YH' and nvl(dr,0) = 0)"
		 * ; DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
		 * DefdocVO.class, strWhere); if (null != defdoc && defdoc.length == 1)
		 * { zyx24 = defdoc[0].getName(); } } Map zzyx =
		 * OaWorkFlowUtil.listAdd("zzyx", zyx24); list.add(zzyx);
		 */

		/* 付款事由 */
		Map fksy = OaWorkFlowUtil.listAdd("fksy", parentVO.getZyx30());
		list.add(fksy);

		/* 退费金额大写 */
		Map tfjedx = OaWorkFlowUtil.listAdd("tfjedx", ConvertUpMoney.toChinese(parentVO.getVat_amount() + ""));
		list.add(tfjedx);

		/* 退费所属部门 */
		String bm = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and   pk_dept   ='" + parentVO.getZyx15() + "'");
		Map tfszbm = OaWorkFlowUtil.listAdd("tfszbm", bm);
		list.add(tfszbm);

		/* 收款方 */
		Map skf = OaWorkFlowUtil.listAdd("skf", parentVO.getZyx11());
		list.add(skf);

		/* 退费金额 */
		Map tfje = OaWorkFlowUtil
				.listAdd("tfje", parentVO.getVat_amount() + "");
		list.add(tfje);

		/* 收款方开户行 */
		String khh = (String) getHyPubBO().findColValue(
				"bd_asslinenum",
				"name",
				"nvl(dr,0) = 0 and    pk_asslinenum  ='" + parentVO.getZyx9()
						+ "'");
		Map skfkhx = OaWorkFlowUtil.listAdd("skfkhx", khh);
		list.add(skfkhx);

		/* 收款方银行账号 */
		Map skfyxzh = OaWorkFlowUtil.listAdd("skfyxzh", parentVO.getZyx14());
		list.add(skfyxzh);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	// 采暖费退费审批单
	public static JSONArray getDtaileDataMap8(JKBXVO temp)
			throws BusinessException {
		Map orderMap = getBXBusItemVO8((BXBusItemVO[]) temp.getChildrenVO(),
				getDef2(temp.getParentVO().getPk_org()));
		JSONArray dtlistString = JSONArray.fromObject(orderMap);
		return dtlistString;
	}

	// 采暖费退费审批单子表
	private static Map getBXBusItemVO8(BXBusItemVO[] childrenVO, String pk_org)
			throws BusinessException {
		Map dtMap = new HashMap();
		// dtMap.put("tableDBName", "formtable_main_121_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (BXBusItemVO temp : childrenVO) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();

			if ("2".equals(pk_org)) {
				dtMap.put("tableDBName", "formtable_main_411_dt1");
			} else {
				dtMap.put("tableDBName", "formtable_main_121_dt1");
			}
			/* 预算类别 */
			String lb = (String) getHyPubBO().findColValue(
					"bd_inoutbusiclass",
					"name",
					"nvl(dr,0) = 0 and   pk_inoutbusiclass  ='"
							+ temp.getSzxmid() + "'");
			Map yslb = OaWorkFlowUtil.listAdd("yslb", lb);
			workflowRequestTableFields.add(yslb);

			/* 退费类型 */
			String z40 = "0";
			if (null != temp.getDefitem40()) {
				String strWhere = "pk_defdoc = '"
						+ temp.getDefitem40()
						+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'tflx' and nvl(dr,0) = 0)";
				DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
						DefdocVO.class, strWhere);
				if (null != defdoc && defdoc.length == 1) {
					z40 = defdoc[0].getName();
				}
			}
			Map tflx = OaWorkFlowUtil.listAdd("tflx", z40);
			workflowRequestTableFields.add(tflx);

			/* 采暖季 */
			String z42 = "0";
			if (null != temp.getDefitem42()) {
				String strWhere = "pk_defdoc = '"
						+ temp.getDefitem42()
						+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'CNJ' and nvl(dr,0) = 0)";
				DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
						DefdocVO.class, strWhere);
				if (null != defdoc && defdoc.length == 1) {
					z42 = defdoc[0].getName();
				}
			}
			Map cnj = OaWorkFlowUtil.listAdd("cnj", z42);
			workflowRequestTableFields.add(cnj);

			/* 退费金额 */
			Map tfje = OaWorkFlowUtil.listAdd("tfje", temp.getVat_amount()
					.setScale(2, UFDouble.ROUND_HALF_UP) + "");
			workflowRequestTableFields.add(tfje);

			/* 应交金额 */
			Map yjje = OaWorkFlowUtil.listAdd(
					"yjje",
					new UFDouble(temp.getDefitem43() == null ? "0.00" : temp
							.getDefitem43())
							.setScale(2, UFDouble.ROUND_HALF_UP)
							+ "");
			workflowRequestTableFields.add(yjje);

			/* 已交金额 */
			Map yjje1 = OaWorkFlowUtil.listAdd(
					"yjje1",
					new UFDouble(temp.getDefitem44() == null ? "0.00" : temp
							.getDefitem44())
							.setScale(2, UFDouble.ROUND_HALF_UP)
							+ "");
			workflowRequestTableFields.add(yjje1);

			/* 是否热电用户 */
			String zyx50 = "0";
			if (null != temp.getDefitem50()) {
				String strWhere = "pk_defdoc = '"
						+ temp.getDefitem50()
						+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'sf' and nvl(dr,0) = 0)";
				DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
						DefdocVO.class, strWhere);
				if (null != defdoc && defdoc.length == 1) {
					zyx50 = defdoc[0].getName();
				}
			}
			Map sfrdyh = OaWorkFlowUtil.listAdd("sfrdyh", zyx50 + "");
			workflowRequestTableFields.add(sfrdyh);

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);

		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	public static String getDef2(String pk_org) throws BusinessException {
		String def2 = "";
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		if (null != orgVO) {
			def2 = orgVO.getDef2();
		}
		return def2;
	}

	private static HYPubBO hyPubBO;

	public static HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	public void setHyPubBO(HYPubBO hyPubBO) {
		this.hyPubBO = hyPubBO;
	}
}
