package nc.ws.intf.oadata.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.trade.business.HYPubBO;
import nc.uif.pub.exception.UifException;
import nc.vo.arap.payable.PayableBillItemVO;
import nc.vo.arap.payable.PayableBillVO;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.sm.UserVO;
import nc.ws.intf.OaWorkFlowUtil;
import net.sf.json.JSONArray;

@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
public class F1OaUtil {

	// 材料应付单

	public static JSONArray getMainMap3(PayableBillVO parentVO, String supplier)
			throws BusinessException {
		List list = new ArrayList();

		// 工程付款单直接调用共有主表
		getList2(parentVO, list, supplier);

		/* 原OA合同流水号 */
		Map yoahtlsh = OaWorkFlowUtil.listAdd("yoahtlsh", parentVO.getDef10());
		list.add(yoahtlsh);

		/* 发票类型（自定义档案） */
		String def31 = "0";
		if (null != parentVO.getDef31()) {
			String strWhere = "pk_defdoc = '"
					+ parentVO.getDef31()
					+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'fplx' and nvl(dr,0) = 0)";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				def31 = defdoc[0].getName();
			}
		}
		Map fplxzdyda = OaWorkFlowUtil.listAdd("fplxzdyda", def31);
		list.add(fplxzdyda);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	// 材料/工程应付单公有数据
	private static List getList2(PayableBillVO headVO, List list,
			String supplier) throws BusinessException {

		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				headVO.getPk_org());
		if (null != orgVO) {
			/* 组织 */
			if ("2".equals(getDef2(headVO.getPk_org()))) {
				Map szgs = OaWorkFlowUtil.listAdd("szgs", orgVO.getName());
				list.add(szgs);
			}else{
				Map szgs = OaWorkFlowUtil.listAdd("szgs", orgVO.getCode());
				list.add(szgs);
			}
		}

		/* 单据主键 */
		Map djzj = OaWorkFlowUtil.listAdd("djzj", headVO.getPk_payablebill());
		list.add(djzj);

		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				headVO.getBillmaker());
		if (null != userVO) {
			// 身份证号
			Map zdrzj = new HashMap();
			zdrzj.put("fieldName", "zdrzj");
			String id = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"id",
					"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc()
							+ "'");
			if ("2".equals(getDef2(headVO.getPk_org()))) {
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

		/* 组织主键 */
		Map zzzj = OaWorkFlowUtil.listAdd("zzzj", headVO.getPk_org());
		list.add(zzzj);

		/* 单据号 */
		Map djh = OaWorkFlowUtil.listAdd("djh", headVO.getBillno());
		list.add(djh);

		/* 单据日期 */
		Map djrq = OaWorkFlowUtil.listAdd("djrq", headVO.getBilldate()
				.getYear()
				+ "-"
				+ headVO.getBilldate().getStrMonth()
				+ "-"
				+ headVO.getBilldate().getStrDay());
		list.add(djrq);

		/* 往来对象3=业务员，2=部门，1=供应商 */
		Map wldx = OaWorkFlowUtil.listAdd("wldx", headVO.getObjtype() + "");
		list.add(wldx);

		/* 合同现用名 */
		if (null != headVO.getDef4()) {
			// String where = " pk_defdoc = '"+ headVO.getDef4()+
			// "' and pk_supplier in (select pk_supplier from bd_supplier where code = 'htxym' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
			// String def4 = "0";
			// if (null != (String) getHyPubBO().findColValue("bd_defdoc",
			// "name",
			// where)) {
			// def4 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
			// where);
			// }
			String def4 = "0";
			if (null != headVO.getSupplier()) {
				def4 = (String) getHyPubBO().findColValue(
						"bd_supplier",
						"name",
						"nvl(dr,0) = 0 and  pk_supplier  = '"
								+ headVO.getSupplier() + "'");
			}
			Map htxym = OaWorkFlowUtil.listAdd("htxym", def4);
			list.add(htxym);
		}
		/* 供应商 */
		String name = "0";
		if (null != headVO.getSupplier()) {
			name = (String) getHyPubBO().findColValue(
					"bd_supplier",
					"name",
					"nvl(dr,0) = 0 and  pk_supplier  = '"
							+ headVO.getSupplier() + "'");
		}
		if (supplier != null && !"".equals(supplier)) {
			name = (String) getHyPubBO().findColValue("bd_supplier", "name",
					"nvl(dr,0) = 0 and  pk_supplier  = '" + supplier + "'");

		}
		Map gys = OaWorkFlowUtil.listAdd("gys", name);
		list.add(gys);

		/* 部门 */
		String dname = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and pk_dept  = '" + headVO.getPk_deptid() + "'");
		Map bmzj = OaWorkFlowUtil.listAdd("bmzj", dname);
		list.add(bmzj);

		/* 业务员 */
		String str2 = "0";
		if (null != headVO.getPk_psndoc()) {
			str2 = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and pk_psndoc  = '" + headVO.getPk_psndoc()
							+ "'");
		}
		Map ywy = OaWorkFlowUtil.listAdd("ywy", str2);
		list.add(ywy);

		/* 合同号 */
		Map hth = OaWorkFlowUtil.listAdd("hth", headVO.getContractno());
		list.add(hth);

		/* 合同总金额 */
		Map htzje = OaWorkFlowUtil.listAdd(
				"htzje",
				new UFDouble(headVO.getDef70() == null ? "0.00" : headVO
						.getDef70()).setScale(2, UFDouble.ROUND_HALF_UP) + "");
		list.add(htzje);

		/* 已开发票金额 */
		Map ykfpje = OaWorkFlowUtil.listAdd(
				"ykfpje",
				new UFDouble(headVO.getDef72() == null ? "0.00" : headVO
						.getDef72()).setScale(2, UFDouble.ROUND_HALF_UP) + "");
		list.add(ykfpje);

		/* 本次发票金额 */
		Map bckpje = OaWorkFlowUtil.listAdd("bckpje", headVO.getLocal_money()
				+ "");
		list.add(bckpje);

		/* 累计开票金额 */
		Map ljkpje = OaWorkFlowUtil.listAdd(
				"ljkpje",
				new UFDouble(headVO.getDef30() == null ? "0.00" : headVO
						.getDef30()).setScale(2, UFDouble.ROUND_HALF_UP) + "");
		list.add(ljkpje);

		/* 税率 */
		Map sl = OaWorkFlowUtil.listAdd("sl", headVO.getDef32());
		list.add(sl);

		/* 税额 */
		Map se = OaWorkFlowUtil.listAdd(
				"se",
				new UFDouble(headVO.getDef33() == null ? "0.00" : headVO
						.getDef33()).setScale(2, UFDouble.ROUND_HALF_UP) + "");
		list.add(se);

		/* 税额变更后金额 */
		Map sebghje = OaWorkFlowUtil.listAdd(
				"sebghje",
				new UFDouble(headVO.getDef34() == null ? "0.00" : headVO
						.getDef34()).setScale(2, UFDouble.ROUND_HALF_UP) + "");
		list.add(sebghje);

		/* 审计金额 */
		Map sjje = OaWorkFlowUtil.listAdd(
				"sjje",
				new UFDouble(headVO.getDef36() == null ? "0.00" : headVO
						.getDef36()).setScale(2, UFDouble.ROUND_HALF_UP) + "");
		list.add(sjje);

		// 代控部门主键
		Map dkbmzj = OaWorkFlowUtil.listAdd("dkbmzj", headVO.getPu_deptid());
		list.add(dkbmzj);

		/* 代控部门编码 */
		String dkbmbm1 = (String) getHyPubBO().findColValue("org_dept", "code",
				"nvl(dr,0) = 0 and pk_dept  = '" + headVO.getPu_deptid() + "'");
		Map dkbmbm = OaWorkFlowUtil.listAdd("dkbmbm", dkbmbm1);
		list.add(dkbmbm);

		/* 代控部门 */
		String dk = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and pk_dept  = '" + headVO.getPu_deptid() + "'");
		Map dkbm = OaWorkFlowUtil.listAdd("dkbm", dk);
		list.add(dkbm);

		/* 付款类别 */
		String def23 = "0";
		if (null != headVO.getDef23()) {
			String strWhere = "pk_defdoc = '"
					+ headVO.getDef23()
					+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'fklb' and nvl(dr,0) = 0)";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				def23 = defdoc[0].getName();
			}
		}
		Map fklb = OaWorkFlowUtil.listAdd("fklb", def23);
		list.add(fklb);

		/* 核算人员 */
		String str = "0";
		if (null != headVO.getDef13()) {
			str = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and pk_psndoc  = '" + headVO.getDef13()
							+ "'");
		}
		Map hsry = OaWorkFlowUtil.listAdd("hsry", str);
		list.add(hsry);

		/* 附件张数 */
		Map fjzs = OaWorkFlowUtil
				.listAdd("fjzs", headVO.getAccessorynum() + "");
		list.add(fjzs);

		/* 备注 */
		Map bz = OaWorkFlowUtil.listAdd("bz", headVO.getDef12());
		list.add(bz);

		return list;
	}

	// 应付单
	public static JSONArray getYfdDtaileDataMap(AggregatedValueObject temp)
			throws BusinessException {
		Map orderMap = getPayableBillItemVO(
				(PayableBillItemVO[]) temp.getChildrenVO(),
				(PayableBillVO) temp.getParentVO());
		JSONArray dtlistString = JSONArray.fromObject(orderMap);
		return dtlistString;
	}

	/**
	 * 应付单子表数据
	 * 
	 * @param bvos
	 * @param headVO
	 * @return
	 * @throws BusinessException
	 */
	private static Map getPayableBillItemVO(PayableBillItemVO[] bvos,
			PayableBillVO headVO) throws BusinessException {
		Map dtMap = new HashMap();
		if ("D1".equals(headVO.getPk_tradetype())) {
			if ("2".equals(getDef2(headVO.getPk_org()))) {
				dtMap.put("tableDBName", "formtable_main_436_dt1");
			} else {
				dtMap.put("tableDBName", "formtable_main_80_dt1");
			}
		}
		if ("F1-Cxx-01".equals(headVO.getPk_tradetype())) {
			if ("2".equals(getDef2(headVO.getPk_org()))) {
				dtMap.put("tableDBName", "formtable_main_435_dt1");
			} else {
				dtMap.put("tableDBName", "formtable_main_89_dt1");
			}
		}
		List workflowRequestTableRecords = new ArrayList();
		for (PayableBillItemVO temp : bvos) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();

			if ("D1".equals(temp.getPk_tradetype())) {
				/* 票据类型 */
				String def30 = "0";
				if (null != temp.getDef30()) {
					String strWhere = "pk_defdoc = '"
							+ temp.getDef30()
							+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'fplx' and nvl(dr,0) = 0)";
					DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO()
							.queryByCondition(DefdocVO.class, strWhere);
					if (null != defdoc && defdoc.length == 1) {
						def30 = defdoc[0].getName();
					}
				}
				Map pjlx = OaWorkFlowUtil.listAdd("pjlx", def30);
				workflowRequestTableFields.add(pjlx);
			}
			/* 代控部门主键 */
			Map dkbmzj = OaWorkFlowUtil
					.listAdd("dkbmzj", headVO.getPu_deptid());
			workflowRequestTableFields.add(dkbmzj);
			/* 代控部门编码 */
			String dkbmstr = (String) getHyPubBO().findColValue(
					"org_dept",
					"code",
					"nvl(dr,0) = 0 and pk_dept  = '" + headVO.getPu_deptid()
							+ "'");
			Map dkbmbm = OaWorkFlowUtil.listAdd("dkbmbm", dkbmstr);
			workflowRequestTableFields.add(dkbmbm);
			/* 代控部门 */
			String dk = (String) getHyPubBO().findColValue(
					"org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept  = '" + headVO.getPu_deptid()
							+ "'");
			Map dkbm = OaWorkFlowUtil.listAdd("dkbm", dk);
			workflowRequestTableFields.add(dkbm);

			/* 项目 */
			String name = (String) getHyPubBO().findColValue(
					"bd_project",
					"project_name",
					"nvl(dr,0) = 0 and pk_project  = '" + temp.getProject()
							+ "'");
			Map xm = OaWorkFlowUtil.listAdd("xm", name);
			workflowRequestTableFields.add(xm);

			/* 项目编码 */
			String code = (String) getHyPubBO().findColValue(
					"bd_project",
					"project_code",
					"nvl(dr,0) = 0 and pk_project  = '" + temp.getProject()
							+ "'");
			Map xmbm = OaWorkFlowUtil.listAdd("xmbm", code);
			workflowRequestTableFields.add(xmbm);

			/* 合同号 */
			Map hth = OaWorkFlowUtil.listAdd("hth", temp.getContractno());
			workflowRequestTableFields.add(hth);

			/* 发票号 */
			String str = "0";
			if (null != temp.getInvoiceno()) {
				str = temp.getInvoiceno();
			}
			Map fph = OaWorkFlowUtil.listAdd("fph", str);
			workflowRequestTableFields.add(fph);

			/* 摘要 */
			Map zy = OaWorkFlowUtil.listAdd("zy", temp.getScomment());
			workflowRequestTableFields.add(zy);

			/* 物料主键 */
			String s = "0";
			if (null != temp.getMaterial()) {
				s = temp.getMaterial();
			}
			Map wlzj = OaWorkFlowUtil.listAdd("wlzj", s);
			workflowRequestTableFields.add(wlzj);

			/* 往来对象3=业务员，2=部门，1=供应商 */
			Map wldx = OaWorkFlowUtil.listAdd("wldx", temp.getObjtype() + "");
			workflowRequestTableFields.add(wldx);

			/* 发票金额 */
			Map fpje = OaWorkFlowUtil.listAdd("fpje", temp.getLocal_money_cr()
					+ "");
			workflowRequestTableFields.add(fpje);

			/* 单价 */
			Map dj = OaWorkFlowUtil.listAdd("dj", temp.getPrice() + "");
			workflowRequestTableFields.add(dj);

			/* 含税单价 */
			Map hsdj = OaWorkFlowUtil.listAdd("hsdj", temp.getTaxprice() + "");
			workflowRequestTableFields.add(hsdj);

			/* 贷方数量 */
			Map dfsl = OaWorkFlowUtil.listAdd("dfsl", temp.getQuantity_cr()
					+ "");
			workflowRequestTableFields.add(dfsl);

			/* 不含税金额 */
			Map bhsje = OaWorkFlowUtil.listAdd("bhsje",
					temp.getLocal_notax_cr() + "");
			workflowRequestTableFields.add(bhsje);

			/* 税码 */
			Map sm = OaWorkFlowUtil.listAdd("sm", temp.getTaxcodeid() + "");
			workflowRequestTableFields.add(sm);

			/* 税率 */
			Map sl = OaWorkFlowUtil.listAdd("sl", temp.getTaxrate() + "");
			workflowRequestTableFields.add(sl);

			/* 税额 */
			Map se = OaWorkFlowUtil.listAdd("se", temp.getLocal_tax_cr() + "");
			workflowRequestTableFields.add(se);

			/* 票据类型主键 */
			String zj = "0";
			try {
				if (null != temp.getChecktype()) {
					String pjlxstr = (String) getHyPubBO().findColValue(
							"NotetypeVO",
							"name",
							"nvl(dr,0) = 0 and pk_notetype  = '"
									+ temp.getChecktype() + "'");
					Map pjlxzj = OaWorkFlowUtil.listAdd("pjlxzj", pjlxstr);
					workflowRequestTableFields.add(pjlxzj);
				} else {
					Map pjlxzj = OaWorkFlowUtil.listAdd("pjlxzj", zj);
					workflowRequestTableFields.add(pjlxzj);
				}
			} catch (UifException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/* 仓库 */
			Map ck = OaWorkFlowUtil.listAdd("ck", temp.getDef26());
			workflowRequestTableFields.add(ck);

			/* 不可抵扣金额 */
			Map bkdkje = OaWorkFlowUtil.listAdd("bkdkje", temp.getNosubtax()
					+ "");
			workflowRequestTableFields.add(bkdkje);

			/* 物料 */
			Map wl = OaWorkFlowUtil.listAdd("wl", temp.getDef13());
			workflowRequestTableFields.add(wl);

			/* 规格类型 */
			Map gglx = OaWorkFlowUtil.listAdd("gglx", temp.getDef50());
			workflowRequestTableFields.add(gglx);

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);

		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	// 工程应付单
	public static JSONArray getMainMap4(PayableBillVO parentVO, String supplier)
			throws BusinessException {
		List list = new ArrayList();

		// 工程付款单直接调用共有主表
		getList2(parentVO, list, supplier);

		/* 凭证号 */
		String str = "0";
		if (null != parentVO.getDef79()) {
			str = parentVO.getDef79();
		}

		Map pzh = OaWorkFlowUtil.listAdd("pzh", str);
		list.add(pzh);

		/* 发票类型（自定义档案） */
		String def31 = "0";
		if (null != parentVO.getDef31()) {
			String strWhere = "pk_defdoc = '"
					+ parentVO.getDef31()
					+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'fplx' and nvl(dr,0) = 0)";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				def31 = defdoc[0].getName();
			}
		}
		Map fplx = OaWorkFlowUtil.listAdd("fplx", def31);
		list.add(fplx);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	// 费用发票汇总报账单
	public static JSONArray getMainMapFyfp(PayableBillVO headVO, String supplier)
			throws BusinessException {
		List list = new ArrayList();

		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				headVO.getPk_org());
		if (null != orgVO) {
			/* 组织 */
			Map szgs = OaWorkFlowUtil.listAdd("szgs", orgVO.getName());
			list.add(szgs);
		}

		/* 单据主键 */
		Map djzj = OaWorkFlowUtil.listAdd("djzj", headVO.getPk_payablebill());
		list.add(djzj);

		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				headVO.getBillmaker());
		if (null != userVO) {
			// 身份证号
			Map zdrzj = new HashMap();
			zdrzj.put("fieldName", "zdrzj");
			String id = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"id",
					"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc()
							+ "'");
			if ("2".equals(getDef2(headVO.getPk_org()))) {
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

		/* 组织主键 */
		Map zzzj = OaWorkFlowUtil.listAdd("zzzj", headVO.getPk_org());
		list.add(zzzj);

		/* 单据号 */
		Map djh = OaWorkFlowUtil.listAdd("djh", headVO.getBillno());
		list.add(djh);

		/* 单据日期 */
		Map djrq = OaWorkFlowUtil.listAdd("djrq", headVO.getBilldate()
				.getYear()
				+ "-"
				+ headVO.getBilldate().getStrMonth()
				+ "-"
				+ headVO.getBilldate().getStrDay());
		list.add(djrq);

		/* 代控部门主键 */
		Map dkbmzj = OaWorkFlowUtil.listAdd("dkbmzj", headVO.getPk_deptid());
		list.add(dkbmzj);

		/* 代控部门编码 */
		String dkbmbmstr = (String) getHyPubBO().findColValue("org_dept",
				"code",
				"nvl(dr,0) = 0 and pk_dept  = '" + headVO.getPk_deptid() + "'");
		Map dkbmbm = OaWorkFlowUtil.listAdd("dkbmbm", dkbmbmstr);
		list.add(dkbmbm);

		/* 代控部门 */
		String dname = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and pk_dept  = '" + headVO.getPk_deptid() + "'");
		Map dkbm = OaWorkFlowUtil.listAdd("dkbm", dname);
		list.add(dkbm);

		// 费用发票汇总报账
		if ("F1-Cxx-FYFPHZBZD".equals(headVO.getPk_tradetype())) {
			/* 合同名称 */
			Map htmc = OaWorkFlowUtil.listAdd("htmc", headVO.getDef36());
			list.add(htmc);
			/* 本次发票金额 */
			Map bckpje = OaWorkFlowUtil.listAdd("bckpje",
					headVO.getLocal_money() + "");
			list.add(bckpje);
		}

		// 资产发票汇总报账
		if ("F1-Cxx-ZCBZ".equals(headVO.getPk_tradetype())) {
			/* 合同名 */
			Map htmc = OaWorkFlowUtil.listAdd("htmc", headVO.getDef4());
			list.add(htmc);

			/* 发票金额 */
			Map fpje = OaWorkFlowUtil.listAdd("fpje", headVO.getLocal_money()
					+ "");
			list.add(fpje);

			/* 合同总金额 */
			Map htzje = OaWorkFlowUtil.listAdd(
					"htzje",
					new UFDouble(headVO.getDef70() == null ? "0.00" : headVO
							.getDef70()).setScale(2, UFDouble.ROUND_HALF_UP)
							+ "");
			list.add(htzje);
		}

		/* 合同现用名 */
		// if (null != headVO.getDef4()) {
		// String where = " pk_defdoc = '"
		// + headVO.getDef4()
		// +
		// "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'htxym' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
		// String def4 = "0";
		// if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
		// where)) {
		// def4 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
		// where);
		// }
		String def4 = "0";
		if (null != headVO.getDef4()) {
			def4 = (String) getHyPubBO().findColValue(
					"bd_supplier",
					"name",
					"nvl(dr,0) = 0 and pk_supplier  = '" + headVO.getDef4()
							+ "'");
		}
		Map htxym = OaWorkFlowUtil.listAdd("htxym", def4);
		list.add(htxym);
		// }

		/* 供应商 */
		String name = "0";
		if (null != headVO.getSupplier()) {
			name = (String) getHyPubBO().findColValue(
					"bd_supplier",
					"name",
					"nvl(dr,0) = 0 and  pk_supplier  = '"
							+ headVO.getSupplier() + "'");
		}
		if (supplier != null && !"".equals(supplier)) {
			name = (String) getHyPubBO().findColValue("bd_supplier", "name",
					"nvl(dr,0) = 0 and  pk_supplier  = '" + supplier + "'");

		}
		Map gys = OaWorkFlowUtil.listAdd("gys", name);
		list.add(gys);

		/* 挂账类型 */
		if (null != headVO.getDef3()) {
			String where = " pk_defdoc = '"
					+ headVO.getDef3()
					+ "' and nvl(dr,0) = 0";
			String def3 = "0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					where)) {
				def3 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where);
			}
			Map gzlx = OaWorkFlowUtil.listAdd("gzlx", def3);
			list.add(gzlx);
		}

		/* 合同号 */
		Map hth = OaWorkFlowUtil.listAdd("hth", headVO.getContractno());
		list.add(hth);

		/* 合同编码 */
		Map htbm = OaWorkFlowUtil.listAdd("htbm", headVO.getDef35());
		list.add(htbm);

		/* 金额大写 */
		Map jedx = OaWorkFlowUtil.listAdd("jedx", ConvertUpMoney.toChinese(headVO.getLocal_money() +""));
		list.add(jedx);

		String dk = (String) getHyPubBO().findColValue("org_dept", "code",
				"nvl(dr,0) = 0 and pk_dept  = '" + headVO.getPu_deptid() + "'");
		Map fysqbmzj = OaWorkFlowUtil.listAdd("fysqbmzj", dk);
		list.add(fysqbmzj);

		/* 凭证号 */
		String str = "0";
		if (null != headVO.getDef79()) {
			str = headVO.getDef79();
		}

		Map pzh = OaWorkFlowUtil.listAdd("pzh", str);
		list.add(pzh);

		/* 附件张数 */
		Map fjzs = OaWorkFlowUtil
				.listAdd("fjzs", headVO.getAccessorynum() + "");
		list.add(fjzs);

		/* 发票说明 */
		Map fpsm = OaWorkFlowUtil.listAdd("fpsm", headVO.getDef2());
		list.add(fpsm);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	// 水电燃及外购热发票汇总报账
	public static JSONArray getMainMapSDRfp(PayableBillVO headVO,
			String supplier) throws BusinessException {
		List list = new ArrayList();

		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				headVO.getPk_org());
		if (null != orgVO) {
			/* 组织 */
			Map szgs = OaWorkFlowUtil.listAdd("szgs", orgVO.getName());
			list.add(szgs);
		}

		/* 单据主键 */
		Map djzj = OaWorkFlowUtil.listAdd("djzj", headVO.getPk_payablebill());
		list.add(djzj);

		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				headVO.getBillmaker());
		if (null != userVO) {
			// 身份证号
			Map zdrzj = new HashMap();
			zdrzj.put("fieldName", "zdrzj");
			String id = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"id",
					"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc()
							+ "'");
			if ("2".equals(getDef2(headVO.getPk_org()))) {
				if (null == id) {
					throw new BusinessException("制单人身份证号码未维护");
				}
				zdrzj.put("fieldValue", id);
				list.add(zdrzj);

				// 申请人
				Map sqr = OaWorkFlowUtil.listAdd("sqr", userVO.getUser_name());
				list.add(sqr);
			} else {
				zdrzj.put("fieldValue", userVO.getUser_code());
				list.add(zdrzj);
			}
		}

		/* 组织主键 */
		Map zzzj = OaWorkFlowUtil.listAdd("zzzj", headVO.getPk_org());
		list.add(zzzj);

		/* 单据号 */
		Map djh = OaWorkFlowUtil.listAdd("djh", headVO.getBillno());
		list.add(djh);

		/* 单据日期 */
		Map djrq = OaWorkFlowUtil.listAdd("djrq", headVO.getBilldate()
				.getYear()
				+ "-"
				+ headVO.getBilldate().getStrMonth()
				+ "-"
				+ headVO.getBilldate().getStrDay());
		list.add(djrq);

		/* 申请单位 */
		String sqname = (String) getHyPubBO().findColValue(
				"org_financeorg",
				"name",
				"nvl(dr,0) = 0 and pk_financeorg = '" + headVO.getPk_org()
						+ "'");
		Map sqdw = OaWorkFlowUtil.listAdd("sqdw", sqname);
		list.add(sqdw);

		// 费用申请部门
		String dk = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and pk_dept  = '" + headVO.getPu_deptid() + "'");
		Map fysqbmzj = OaWorkFlowUtil.listAdd("fysqbm", dk);
		list.add(fysqbmzj);

		/* 代控部门主键 */
		Map dkbmzj = OaWorkFlowUtil.listAdd("dkbmzj", headVO.getPk_deptid());
		list.add(dkbmzj);

		/* 代控部门编码 */
		String dkbmbmstr = (String) getHyPubBO().findColValue("org_dept",
				"code",
				"nvl(dr,0) = 0 and pk_dept  = '" + headVO.getPk_deptid() + "'");
		Map dkbmbm = OaWorkFlowUtil.listAdd("dkbmbm", dkbmbmstr);
		list.add(dkbmbm);

		/* 代控部门 */
		String dname = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and pk_dept  = '" + headVO.getPk_deptid() + "'");
		Map dkbm = OaWorkFlowUtil.listAdd("dkbm", dname);
		list.add(dkbm);

		/* 供应商 */
		String name = "0";
		if (supplier != null && !"".equals(supplier)) {
			name = (String) getHyPubBO().findColValue("bd_supplier", "name",
					"nvl(dr,0) = 0 and  pk_supplier  = '" + supplier + "'");

		}
		Map gys = OaWorkFlowUtil.listAdd("gys", name);
		list.add(gys);

		/* 挂账类型 */
		if (null != headVO.getDef3()) {
			String where = " pk_defdoc = '"
					+ headVO.getDef3()
					+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'GX-12' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
			String def3 = "0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					where)) {
				def3 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where);
			}
			Map gzlx = OaWorkFlowUtil.listAdd("gzlx", def3);
			list.add(gzlx);
		}

		/* 发票金额 */
		Map fpje = OaWorkFlowUtil.listAdd("fpje", headVO.getLocal_money() + "");
		list.add(fpje);

		/* 金额大写 */
		Map jedx = OaWorkFlowUtil.listAdd("jedx", ConvertUpMoney.toChinese(headVO.getLocal_money()+""));
		list.add(jedx);

		/* 发票说明 */
		Map fpsm = OaWorkFlowUtil.listAdd("fpsm", headVO.getDef2());
		list.add(fpsm);

		/* 附件张数 */
		Map fjzs = OaWorkFlowUtil
				.listAdd("fjzs", headVO.getAccessorynum() + "");
		list.add(fjzs);

		/* 合同现用名 */
		String def4 = "0";
		if (null != headVO.getDef4()) {
			def4 = (String) getHyPubBO().findColValue(
					"bd_supplier",
					"name",
					"nvl(dr,0) = 0 and pk_supplier  = '" + headVO.getDef4()
							+ "'");
		}
		Map htxym = OaWorkFlowUtil.listAdd("htxym", def4);
		list.add(htxym);

		/* 合同编码 */
		Map htbm = OaWorkFlowUtil.listAdd("htbm", headVO.getDef35());
		list.add(htbm);

		/* 合同编码 */
		Map htmc = OaWorkFlowUtil.listAdd("htmc", headVO.getDef36());
		list.add(htmc);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	// 水电燃及外购热发票汇总报账明细
	public static JSONArray getSDRDtaileDataMap(AggregatedValueObject aggVO)
			throws BusinessException {
		PayableBillItemVO[] bvos = (PayableBillItemVO[]) aggVO.getChildrenVO();
		PayableBillVO headVO = (PayableBillVO) aggVO.getParentVO();
		List dtlist = new ArrayList();
		Map dtMap = new HashMap();
		if ("2".equals(getDef2(headVO.getPk_org()))) {
			dtMap.put("tableDBName", "formtable_main_495_dt1");
		}
		List workflowRequestTableRecords = new ArrayList();
		for (PayableBillItemVO temp : bvos) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();

			/* 摘要 */
			Map zy = OaWorkFlowUtil.listAdd("zy", temp.getDef22());
			workflowRequestTableFields.add(zy);

			/* 预算类别 */
			String type1 = (String) getHyPubBO().findColValue(
					"bd_inoutbusiclass",
					"name",
					"nvl(dr,0) = 0 and pk_inoutbusiclass   = '"
							+ temp.getPk_subjcode() + "'");
			Map yslb = OaWorkFlowUtil.listAdd("yslb", type1);
			workflowRequestTableFields.add(yslb);

			/* 费用申请部门 */
			String dk = (String) getHyPubBO().findColValue(
					"org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept  = '" + temp.getPu_deptid()
							+ "'");
			Map fysqbm = OaWorkFlowUtil.listAdd("fysqbm", dk);
			workflowRequestTableFields.add(fysqbm);

			/* 代控部门 */
			String dk2 = (String) getHyPubBO().findColValue(
					"org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept  = '" + temp.getPk_deptid()
							+ "'");
			Map dkbm = OaWorkFlowUtil.listAdd("dkbm", dk2);
			workflowRequestTableFields.add(dkbm);

			/* 发票类型 */
			String def27 = "0";
			if (null != temp.getDef27()) {
				String strWhere = "pk_defdoc = '"
						+ temp.getDef27()
						+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'fplx' and nvl(dr,0) = 0)";
				DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
						DefdocVO.class, strWhere);
				if (null != defdoc && defdoc.length == 1) {
					def27 = defdoc[0].getName();
				}
			}
			Map fplx = OaWorkFlowUtil.listAdd("fplx", def27);
			workflowRequestTableFields.add(fplx);
			
			/* 含税金额 */
			Map hsje = OaWorkFlowUtil.listAdd("hsje", temp.getLocal_money_cr()
					+"");
			workflowRequestTableFields.add(hsje);

			/* 税率 */
			String def17 = "0";
			if (null != temp.getDef17()) {
				String strWhere = "pk_defdoc = '"
						+ temp.getDef17()
						+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code  = 'GX-02' and nvl(dr,0) = 0)";
				DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
						DefdocVO.class, strWhere);
				if (null != defdoc && defdoc.length == 1) {
					def17 = defdoc[0].getName();
				}
			}
			Map sl = OaWorkFlowUtil.listAdd("sl", def17);
			workflowRequestTableFields.add(sl);

			/* 税额 */
			Map se = OaWorkFlowUtil.listAdd("se", temp.getDef19());
			workflowRequestTableFields.add(se);
			
			/* 不含税金额 */
			Map bhsje = OaWorkFlowUtil.listAdd("bhsje", temp.getDef15());
			workflowRequestTableFields.add(bhsje);
			
			/* 换热站 */
			if (null != temp.getDef30()) {
				String where = " pk_defdoc = '"
						+ temp.getDef30()
						+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'hrz' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
				String def30 = "0";
				if (null != (String) getHyPubBO().findColValue("bd_defdoc",
						"name", where)) {
					def30 = (String) getHyPubBO().findColValue("bd_defdoc",
							"name", where);
				}
				Map hrz = OaWorkFlowUtil.listAdd("hrz", def30);
				workflowRequestTableFields.add(hrz);
			}

			/* 费用名称 */
			Map fymc = OaWorkFlowUtil.listAdd("fymc", temp.getDef13());
			workflowRequestTableFields.add(fymc);

			/* 项目 */
			String name = (String) getHyPubBO().findColValue(
					"bd_project",
					"project_name",
					"nvl(dr,0) = 0 and pk_project  = '" + temp.getProject()
							+ "'");
			Map xm = OaWorkFlowUtil.listAdd("xm", name);
			workflowRequestTableFields.add(xm);

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		dtlist.add(dtMap);
		JSONArray dtlistString = JSONArray.fromObject(dtlist);
		return dtlistString;
	}

	// 费用发票汇总报账单子表
	public static JSONArray getDtaileDataMapFyfp(AggregatedValueObject aggVO)
			throws BusinessException {
		PayableBillItemVO[] bvos = (PayableBillItemVO[]) aggVO.getChildrenVO();

		PayableBillVO headVO = (PayableBillVO) aggVO.getParentVO();
		List dtlist = new ArrayList();
		Map dtMap = new HashMap();
		// if(headVO.getPk_tradetype().contains("F3-Cxx-GDZC" )){
		if ("2".equals(getDef2(headVO.getPk_org()))) {
			dtMap.put("tableDBName", "formtable_main_434_dt1");
		} else {
			dtMap.put("tableDBName", "formtable_main_110_dt1");
		}
		// }
		List workflowRequestTableRecords = new ArrayList();
		for (PayableBillItemVO temp : bvos) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();

			/* 摘要 */
			Map zy = OaWorkFlowUtil.listAdd("zy", temp.getDef22());
			workflowRequestTableFields.add(zy);

			/* 预算类别 */
			String type1 = (String) getHyPubBO().findColValue(
					"bd_inoutbusiclass",
					"name",
					"nvl(dr,0) = 0 and pk_inoutbusiclass   = '"
							+ temp.getPk_subjcode() + "'");
			Map yslb = OaWorkFlowUtil.listAdd("yslb", type1);
			workflowRequestTableFields.add(yslb);

			/* 费用申请部门 */
			String dk = (String) getHyPubBO().findColValue(
					"org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept  = '" + temp.getPu_deptid()
							+ "'");
			Map fysqbm = OaWorkFlowUtil.listAdd("fysqbm", dk);
			workflowRequestTableFields.add(fysqbm);

			/* 代控部门主键 */
			Map dkbmzj = OaWorkFlowUtil.listAdd("dkbmzj", temp.getPk_deptid());
			workflowRequestTableFields.add(dkbmzj);

			/* 代控部门编码 */
			String dkbmbmstr = (String) getHyPubBO().findColValue(
					"org_dept",
					"code",
					"nvl(dr,0) = 0 and pk_dept  = '" + temp.getPk_deptid()
							+ "'");
			Map dkbmbm = OaWorkFlowUtil.listAdd("dkbmbm", dkbmbmstr);
			workflowRequestTableFields.add(dkbmbm);

			/* 代控部门 */
			String dk2 = (String) getHyPubBO().findColValue(
					"org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept  = '" + temp.getPk_deptid()
							+ "'");
			Map dkbm = OaWorkFlowUtil.listAdd("dkbm", dk2);
			workflowRequestTableFields.add(dkbm);

			/* 发票类型 */
			// if (null != headVO.getDef27()) {
			// String where = " pk_defdoc = '"
			// + headVO.getDef27()
			// +
			// "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'fplx' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
			// String def27 = "0";
			// if (null != (String) getHyPubBO().findColValue("bd_defdoc",
			// "name", where)) {
			// def27 = (String) getHyPubBO().findColValue("bd_defdoc",
			// "name", where);
			// }
			String def27 = "0";
			if (null != temp.getDef27()) {
				String strWhere = "pk_defdoc = '"
						+ temp.getDef27()
						+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'fplx' and nvl(dr,0) = 0)";
				DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
						DefdocVO.class, strWhere);
				if (null != defdoc && defdoc.length == 1) {
					def27 = defdoc[0].getName();
				}
			}
			Map fplx = OaWorkFlowUtil.listAdd("fplx", def27);
			workflowRequestTableFields.add(fplx);
			// }

			/* 含税金额 */
			Map hsje = OaWorkFlowUtil.listAdd("hsje", temp.getLocal_money_cr()
					+ "");
			workflowRequestTableFields.add(hsje);

			/* 税率 */

			String def17 = "0";
			if (null != temp.getDef17()) {
				String strWhere = "pk_defdoc = '"
						+ temp.getDef17()
						+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code  = 'GX-02' and nvl(dr,0) = 0)";
				DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
						DefdocVO.class, strWhere);
				if (null != defdoc && defdoc.length == 1) {
					def17 = defdoc[0].getName();
				}
			}
			Map sl = OaWorkFlowUtil.listAdd("sl", def17);
			workflowRequestTableFields.add(sl);

			/* 税额 */
			Map se = OaWorkFlowUtil.listAdd("se", temp.getDef19());
			workflowRequestTableFields.add(se);

			/* 不含税金额 */
			Map bhsje = OaWorkFlowUtil.listAdd("bhsje", temp.getDef15());
			workflowRequestTableFields.add(bhsje);

			/* 合同号 */
			Map hth = OaWorkFlowUtil.listAdd("hth", temp.getContractno());
			workflowRequestTableFields.add(hth);

			/* 换热站 */
			if (null != temp.getDef30()) {
				String where = " pk_defdoc = '"
						+ temp.getDef30()
						+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'hrz' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
				String def30 = "0";
				if (null != (String) getHyPubBO().findColValue("bd_defdoc",
						"name", where)) {
					def30 = (String) getHyPubBO().findColValue("bd_defdoc",
							"name", where);
				}
				Map hrz = OaWorkFlowUtil.listAdd("hrz", def30);
				workflowRequestTableFields.add(hrz);
			}

			/* 费用名称 */
			Map fymc = OaWorkFlowUtil.listAdd("fymc", temp.getDef13());
			workflowRequestTableFields.add(fymc);

			/* 项目 */
			String name = (String) getHyPubBO().findColValue(
					"bd_project",
					"project_name",
					"nvl(dr,0) = 0 and pk_project  = '" + temp.getProject()
							+ "'");
			Map xm = OaWorkFlowUtil.listAdd("xm", name);
			workflowRequestTableFields.add(xm);

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		dtlist.add(dtMap);
		JSONArray dtlistString = JSONArray.fromObject(dtlist);
		return dtlistString;
	}

	public static JSONArray getZCdDtaileDataMap(AggregatedValueObject aggVO)
			throws BusinessException {
		PayableBillItemVO[] bvos = (PayableBillItemVO[]) aggVO.getChildrenVO();
		PayableBillVO headVO = (PayableBillVO) aggVO.getParentVO();
		List dtlist = new ArrayList();
		Map dtMap = new HashMap();
		// if(headVO.getPk_tradetype().contains("F3-Cxx-GDZC" )){
		if ("2".equals(getDef2(headVO.getPk_org()))) {
			dtMap.put("tableDBName", "formtable_main_426_dt1");
		} else {
			dtMap.put("tableDBName", "formtable_main_111_dt1");
		}
		// }
		List workflowRequestTableRecords = new ArrayList();
		for (PayableBillItemVO temp : bvos) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();

			/* 资产类别 */
			String zclbname = (String) getHyPubBO().findColValue(
					"tb_budgetsub", "objname",
					"nvl(dr,0) = 0 and  pk_obj = '" + temp.getDef23() + "'");
			Map zclb = OaWorkFlowUtil.listAdd("zclb", zclbname);
			workflowRequestTableFields.add(zclb);

			/* 费用申请部门 */
			String dk = (String) getHyPubBO().findColValue(
					"org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept  = '" + temp.getPu_deptid()
							+ "'");
			Map fysqbm = OaWorkFlowUtil.listAdd("fysqbm", dk);
			workflowRequestTableFields.add(fysqbm);

			/* 发票类型 */
			String def27 = "0";
			if (null != temp.getDef27()) {
				String strWhere = "pk_defdoc = '"
						+ temp.getDef27()
						+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'fplx' and nvl(dr,0) = 0)";
				DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
						DefdocVO.class, strWhere);
				if (null != defdoc && defdoc.length == 1) {
					def27 = defdoc[0].getName();
				}
			}
			Map fplx = OaWorkFlowUtil.listAdd("fplx", def27);
			workflowRequestTableFields.add(fplx);

			/* 含税金额 */
			Map hsje = OaWorkFlowUtil.listAdd("hsje", temp.getLocal_money_cr()
					+ "");
			workflowRequestTableFields.add(hsje);

			/* 税率 */

			String def17 = "0";
			if (null != temp.getDef17()) {
				String strWhere = "pk_defdoc = '"
						+ temp.getDef17()
						+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code  = 'GX-02' and nvl(dr,0) = 0)";
				DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
						DefdocVO.class, strWhere);
				if (null != defdoc && defdoc.length == 1) {
					def17 = defdoc[0].getName();
				}
			}
			Map sl = OaWorkFlowUtil.listAdd("sl", def17);
			workflowRequestTableFields.add(sl);

			/* 税额 */
			Map se = OaWorkFlowUtil.listAdd(
					"se",
					new UFDouble(temp.getDef19() == null ? "0.00" : temp
							.getDef19()).setScale(2, UFDouble.ROUND_HALF_UP)
							+ "");
			workflowRequestTableFields.add(se);

			/* 不含税金额 */
			Map bhsje = OaWorkFlowUtil.listAdd("bhsje", temp.getDef15());
			workflowRequestTableFields.add(bhsje);

			/* 合同号 */
			Map hth = OaWorkFlowUtil.listAdd("hth", temp.getContractno());
			workflowRequestTableFields.add(hth);

			/* 项目 */
			String name = (String) getHyPubBO().findColValue(
					"bd_project",
					"project_name",
					"nvl(dr,0) = 0 and pk_project  = '" + temp.getProject()
							+ "'");
			Map xm = OaWorkFlowUtil.listAdd("xm", name);
			workflowRequestTableFields.add(xm);

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		dtlist.add(dtMap);
		JSONArray dtlistString = JSONArray.fromObject(dtlist);
		return dtlistString;
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

	public static String getDef2(String pk_org) throws BusinessException {
		String def2 = "";
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		if (null != orgVO) {
			def2 = orgVO.getDef2();
		}
		return def2;
	}
}
