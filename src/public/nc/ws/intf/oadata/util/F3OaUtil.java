package nc.ws.intf.oadata.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
import nc.ui.pcm.utils.GetDao;
import nc.vo.arap.pay.PayBillItemVO;
import nc.vo.arap.pay.PayBillVO;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.sm.UserVO;
import nc.ws.intf.OaWorkFlowUtil;
import net.sf.json.JSONArray;

@SuppressWarnings({ "unchecked", "rawtypes", "static-access" })
public class F3OaUtil {
	// 材料付款单
	public static JSONArray getMainMap1(PayBillVO headVO, String supplier)
			throws BusinessException {
		List list = new ArrayList();

		// 材料付款单直接调用共有主表
		getList(headVO, list, supplier);

		/* 付款合同 */
		String str = "0";
		if (null != headVO.getPk_contractno()) {
			str = headVO.getPk_contractno();
		}
		Map fkht = OaWorkFlowUtil.listAdd("fkht", str);
		list.add(fkht);

		/* NC发票挂账金额 */
		Map ncfpgzje = OaWorkFlowUtil.listAdd("ncfpgzje", headVO.getDef42());
		list.add(ncfpgzje);

		/* NC累计付款金额 */
		Map ncljfkje = OaWorkFlowUtil.listAdd("ncljfkje", headVO.getDef43());
		list.add(ncljfkje);

		/* NC欠款金额 */
		Map ncqkje = OaWorkFlowUtil.listAdd("ncqkje", headVO.getDef44());
		list.add(ncqkje);

		/* 付款财务组织 */
		String fname = (String) getHyPubBO().findColValue("org_orgs", "name",
				"nvl(dr,0) = 0 and pk_org  = '" + headVO.getPk_org() + "'");
		Map fkcwzz = OaWorkFlowUtil.listAdd("fkcwzz", fname);
		list.add(fkcwzz);

		// 能投新增 是否原OA单据
		if ("2".equals(getDef2(headVO.getPk_org()))) {
			String sfyoadjstr = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc  = '" + headVO.getDef69()
							+ "'");
			Map sfyoadj = OaWorkFlowUtil.listAdd("sfyoadj", sfyoadjstr);
			list.add(sfyoadj);
		}

		/* 折让金额 */
		// String zr = "0";
		// if (null != headVO.getDef32()) {
		// zr = headVO.getDef32();
		// }
		// Map zrje1 = OaWorkFlowUtil.listAdd("zrje1", zr);
		// list.add(zrje1);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	// 材料/工程付款单共有数据
	public static List getList(PayBillVO headVO, List list, String supplier)
			throws BusinessException {

		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				headVO.getPk_org());
		if (null != orgVO) {
			/* 组织 */
			Map szgs = OaWorkFlowUtil.listAdd("szgs", orgVO.getName());
			list.add(szgs);
			if ("2".equals(getDef2(headVO.getPk_org()))) {
				String khh = getGYSKHH(headVO.getPk_tradetype(),
						headVO.getRecaccount());
				Map gyskhh = OaWorkFlowUtil.listAdd("khh", khh);
				list.add(gyskhh);
			}
		}

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
		// if (null != headVO.getDef4()) {
		// String where = " pk_sipplier = '"
		// + headVO.getDef4()
		// + "' and nvl(dr,0) = 0";
		// String def4 = "0";
		// if (null != (String) getHyPubBO().findColValue("bd_sipplier", "name",
		// where)) {
		// def4 = (String) getHyPubBO().findColValue("bd_sipplier", "name",
		// where);
		// }
		// String def4 = "0";
		// if(null != headVO.getDef4()){
		// String strWhere = "pk_defdoc = '" + headVO.getDef4() +
		// "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'sf' and nvl(dr,0) = 0)";
		// DefdocVO[] defdoc = (DefdocVO[])
		// getHyPubBO().queryByCondition(DefdocVO.class, strWhere);
		// if(null != defdoc && defdoc.length == 1){
		// def4 = defdoc[0].getName();
		// }
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

		/* 部门 */
		String dname = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and pk_dept  = '" + headVO.getPk_deptid() + "'");
		Map bm = OaWorkFlowUtil.listAdd("bm", dname);
		list.add(bm);

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

		/* 付款类型 */
		String fkname = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and pk_billtypeid  = '"
						+ headVO.getPk_tradetypeid() + "'");
		Map fklx = OaWorkFlowUtil.listAdd("fklx", fkname);
		list.add(fklx);

		/* 收款银行账户 */
		String yhname = (String) getHyPubBO().findColValue(
				"bd_bankaccsub",
				"accnum",
				"nvl(dr,0) = 0 and pk_bankaccsub  = '" + headVO.getRecaccount()
						+ "'");
		Map skyxzh = OaWorkFlowUtil.listAdd("skyxzh", yhname);
		list.add(skyxzh);

		/* 承兑金额 */
		// String st1 = "0";
		// if (null != headVO.getDef31()) {
		// st1 = headVO.getDef31();
		// }
		Map cdje = OaWorkFlowUtil.listAdd(
				"cdje",
				new UFDouble(headVO.getDef31() == null ? "0.00" : headVO
						.getDef31()).setScale(2, UFDouble.ROUND_HALF_UP) + "");
		list.add(cdje);

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

		/* 单据类型 */
		String s = "0";
		if (null != headVO.getDef22()) {
			s = headVO.getDef22();
		}
		Map djlx = OaWorkFlowUtil.listAdd("djlx", s);
		list.add(djlx);

		/* 分管领导 */
		if (null != headVO.getDef21()) {
			String where = " pk_defdoc = '"
					+ headVO.getDef21()
					+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'fgld' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
			String def21 = "0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					where)) {
				def21 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where);
			}
			Map fgld = OaWorkFlowUtil.listAdd("fgld", def21);
			list.add(fgld);
		}

		/* 代控部门主键 */
		Map dkbmzj = OaWorkFlowUtil.listAdd("dkbmzj", headVO.getDef20());
		list.add(dkbmzj);

		/* 代控部门编码 */
		String dkbmbmstr = (String) getHyPubBO().findColValue("org_dept",
				"code",
				"nvl(dr,0) = 0 and pk_dept  = '" + headVO.getDef20() + "'");
		Map dkbmbm = OaWorkFlowUtil.listAdd("dkbmbm", dkbmbmstr);
		list.add(dkbmbm);

		/* 代控部门 */
		String dk = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and pk_dept  = '" + headVO.getDef20() + "'");
		Map dkbm = OaWorkFlowUtil.listAdd("dkbm", dk);
		list.add(dkbm);

		/* 合同总金额 */
		Map htzje = OaWorkFlowUtil.listAdd("htzje", headVO.getDef70());
		list.add(htzje);

		/* 应付金额 */
		Map yfje = OaWorkFlowUtil.listAdd("yfje", headVO.getLocal_money() + "");
		list.add(yfje);

		/* 应付金额大写 */
		Map yfjedx = OaWorkFlowUtil.listAdd("yfjedx",
				ConvertUpMoney.toChinese(headVO.getLocal_money() + ""));
		list.add(yfjedx);

		/* 实付金额 */
		Map sfje = OaWorkFlowUtil.listAdd("sfje", headVO.getDef26());
		list.add(sfje);

		/* 本次付款比例 */
		String c = "0";
		if (null != headVO.getDef37()) {
			c = headVO.getDef37();
		}
		Map bcfkbl = OaWorkFlowUtil.listAdd("bcfkbl", c);
		list.add(bcfkbl);

		/* 已付款金额 */
		Map yfkje = OaWorkFlowUtil.listAdd("yfkje", headVO.getDef71());
		list.add(yfkje);

		/* 已付款比例 */
		Map yfkbl = OaWorkFlowUtil.listAdd("yfkbl", headVO.getDef38());
		list.add(yfkbl);

		/* 累计付款金额 */
		Map ljfkje = OaWorkFlowUtil.listAdd("ljfkje", headVO.getDef40());
		list.add(ljfkje);

		/* 累计付款比例 */
		Map ljfkbl = OaWorkFlowUtil.listAdd("ljfkbl", headVO.getDef39());
		list.add(ljfkbl);

		/* 已开发票金额 */
		Map ykfpje = OaWorkFlowUtil.listAdd("ykfpje", headVO.getDef72());
		list.add(ykfpje);

		/* 转账银行 */
		if (null != headVO.getDef24()) {
			String where = " pk_defdoc = '"
					+ headVO.getDef24()
					+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'YH' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
			String def24 = "0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					where)) {
				def24 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where);
			}
			Map zzyx = OaWorkFlowUtil.listAdd("zzyx", def24);
			list.add(zzyx);
		}

		/* 承兑银行 */
		String def25 = "0";
		if (null != headVO.getDef25()) {
			String strWhere = "pk_defdoc = '"
					+ headVO.getDef25()
					+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'YH' and nvl(dr,0) = 0)";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				def25 = defdoc[0].getName();
			}
		}
		Map cdyx = OaWorkFlowUtil.listAdd("cdyx", def25);
		list.add(cdyx);

		/* 现金流量项目 */
		String xname = (String) getHyPubBO().findColValue(
				"bd_cashflow",
				"name",
				"nvl(dr,0) = 0 and pk_cashflow  = '" + headVO.getCashitem()
						+ "'");
		Map xjllxm = OaWorkFlowUtil.listAdd("xjllxm", xname);
		list.add(xjllxm);

		/* 折让金额 */
		String t1 = "0";
		if (null != headVO.getDef32()) {
			t1 = headVO.getDef32();
		}
		Map zrje = OaWorkFlowUtil.listAdd("zrje", t1);
		list.add(zrje);

		/* 付款事由 */
		Map fksy = OaWorkFlowUtil.listAdd("fksy", headVO.getDef30());
		list.add(fksy);

		/* 附件张数 */
		Map fjzs = OaWorkFlowUtil
				.listAdd("fjzs", headVO.getAccessorynum() + "");
		list.add(fjzs);

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

		/* 凭证号 */
		String h = "0";
		if (null != headVO.getDef79()) {
			h = headVO.getDef79();
		}
		Map pzh = OaWorkFlowUtil.listAdd("pzh", h);
		list.add(pzh);

		/* 支付日期 */
		String q = "0";
		if (null != headVO.getDef33()) {
			q = headVO.getDef33();
		}
		Map zfrq = OaWorkFlowUtil.listAdd("zfrq", q);
		list.add(zfrq);

		/* CBS状态 */
		String cbs = "0";
		if (null != headVO.getDef33()) {
			cbs = headVO.getDef33();
		}
		Map cbszt = OaWorkFlowUtil.listAdd("cbszt", cbs);
		list.add(cbszt);

		/* 付款单标识 */
		Map fkdbs = OaWorkFlowUtil.listAdd("fkdbs", headVO.getPk_paybill());
		list.add(fkdbs);

		/* 结算方式 */
		String jsfsname = (String) getHyPubBO().findColValue(
				"bd_balatype",
				"name",
				"nvl(dr,0) = 0 and  pk_balatype  = '" + headVO.getPk_balatype()
						+ "'");
		Map jsfs = OaWorkFlowUtil.listAdd("jsfs", jsfsname);
		list.add(jsfs);

		return list;
	}

	/**
	 * 付款单子表数据
	 * 
	 * @param bvos
	 * @param headVO
	 * @return
	 * @throws BusinessException
	 */
	public static Map getPayBillItemVO(PayBillItemVO[] bvos, PayBillVO headVO)
			throws BusinessException {
		Map dtMap = new HashMap();
		if (headVO.getPk_tradetype().contains("D3")) {
			if ("2".equals(getDef2(headVO.getPk_org()))) {
				dtMap.put("tableDBName", "formtable_main_421_dt1");
			} else {
				dtMap.put("tableDBName", "formtable_main_79_dt1");
			}
		}
		if ("F3-Cxx-01".equals(headVO.getPk_tradetype())) {
			if ("2".equals(getDef2(headVO.getPk_org()))) {
				dtMap.put("tableDBName", "formtable_main_422_dt1");
			} else {
				dtMap.put("tableDBName", "formtable_main_82_dt1");
			}
		}
		if ("F3-Cxx-FYFKSPD".equals(headVO.getPk_tradetype())) {
			if ("2".equals(getDef2(headVO.getPk_org()))) {
				dtMap.put("tableDBName", "formtable_main_406_dt1");
			} else {
				dtMap.put("tableDBName", "formtable_main_81_dt1");
			}
		}
		if ("F3-Cxx-SDRFKSPD".equals(headVO.getPk_tradetype())) {
			if ("2".equals(getDef2(headVO.getPk_org()))) {
				dtMap.put("tableDBName", "formtable_main_416_dt1");
			} else {
				dtMap.put("tableDBName", "formtable_main_84_dt1");
			}
		}
		if ("F3-Cxx-GDZC".equals(headVO.getPk_tradetype())) {
			if ("2".equals(getDef2(headVO.getPk_org()))) {
				dtMap.put("tableDBName", "formtable_main_415_dt1");
			} else {
				dtMap.put("tableDBName", "formtable_main_84_dt1");
			}
		}
		// if ("F3-Cxx-MTWGRFKSPD".equals(headVO.getPk_tradetype())) {
		// dtMap.put("tableDBName", "formtable_main_107_dt1");
		// }
		// 能投↓↓↓↓↓↓↓↓↓↓↓↓↓↓
		if ("F3-Cxx-TSSXSPD".equals(headVO.getPk_tradetype())) {
			dtMap.put("tableDBName", "formtable_main_449_dt1");
		}
		List workflowRequestTableRecords = new ArrayList();
		for (PayBillItemVO temp : bvos) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();
			// 材料付款单 || 工程付款单
			if ("D3".equals(temp.getPk_tradetype())
					|| "F3-Cxx-01".equals(temp.getPk_tradetype())) {

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

				/* 发票金额 */
				Map ykfpje = OaWorkFlowUtil.listAdd("ykfpje", temp.getDef9());
				workflowRequestTableFields.add(ykfpje);

				/* 税率 */
				Map sl = OaWorkFlowUtil.listAdd("sl", temp.getDef4());
				workflowRequestTableFields.add(sl);

				/* 税额 */
				Map se = OaWorkFlowUtil.listAdd("se", temp.getDef5());
				workflowRequestTableFields.add(se);
			}
			// 费用付款单
			if ("F3-Cxx-FYFKSPD".equals(temp.getPk_tradetype())) {

				// 预算类别
				String yslbname = (String) getHyPubBO().findColValue(
						"bd_inoutbusiclass",
						"name",
						"nvl(dr,0) = 0 and  pk_inoutbusiclass = '"
								+ temp.getPk_subjcode() + "'");
				Map yslb = OaWorkFlowUtil.listAdd("yslb", yslbname);
				workflowRequestTableFields.add(yslb);

				// 费用发生部门
				String fyfsbmname = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and  pk_dept = '" + temp.getPu_deptid()
								+ "'");
				Map fyfsbm = OaWorkFlowUtil.listAdd("fyfsbm", fyfsbmname);
				workflowRequestTableFields.add(fyfsbm);

				/* 票据类型 */
				String def27 = "0";
				if (null != temp.getDef27()) {
					String strWhere = "pk_defdoc = '"
							+ temp.getDef27()
							+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'fplx' and nvl(dr,0) = 0)";
					DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO()
							.queryByCondition(DefdocVO.class, strWhere);
					if (null != defdoc && defdoc.length == 1) {
						def27 = defdoc[0].getName();
					}
				}
				Map pjlx = OaWorkFlowUtil.listAdd("pjlx", def27);
				workflowRequestTableFields.add(pjlx);

				/* 已开发票金额 */
				Map ykfpje = OaWorkFlowUtil.listAdd("ykfpje", temp.getDef9());
				workflowRequestTableFields.add(ykfpje);

				/* 税率 */
				String def17 = "0";
				if (null != temp.getDef17()) {
					String strWhere = "pk_defdoc = '"
							+ temp.getDef17()
							+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'GX-02' and nvl(dr,0) = 0)";
					DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO()
							.queryByCondition(DefdocVO.class, strWhere);
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

				/* 备注 */
				Map bz = OaWorkFlowUtil.listAdd("bz", temp.getDef13());
				workflowRequestTableFields.add(bz);
			}

			// 水电燃付款单
			if ("F3-Cxx-SDRFKSPD".equals(temp.getPk_tradetype())) {
				// 预算类别
				String yslbname = (String) getHyPubBO().findColValue(
						"bd_inoutbusiclass",
						"name",
						"nvl(dr,0) = 0 and  pk_inoutbusiclass = '"
								+ temp.getPk_subjcode() + "'");
				Map yslb = OaWorkFlowUtil.listAdd("yslb", yslbname);
				workflowRequestTableFields.add(yslb);

				// 费用发生部门
				String fyfsbmname = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and  pk_dept = '" + temp.getPu_deptid()
								+ "'");
				Map fyfsbm = OaWorkFlowUtil.listAdd("fyfsbm", fyfsbmname);
				workflowRequestTableFields.add(fyfsbm);

				/* 票据类型 */
				String def27 = "0";
				if (null != temp.getDef27()) {
					String strWhere = "pk_defdoc = '"
							+ temp.getDef27()
							+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'GX_04' and nvl(dr,0) = 0)";
					DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO()
							.queryByCondition(DefdocVO.class, strWhere);
					if (null != defdoc && defdoc.length == 1) {
						def27 = defdoc[0].getName();
					}
				}
				Map fplx = OaWorkFlowUtil.listAdd("fplx", def27);
				workflowRequestTableFields.add(fplx);

				/* 换热站 */
				String def23 = "0";
				if (null != temp.getDef23()) {
					String strWhere = "pk_defdoc = '"
							+ temp.getDef23()
							+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'hrz' and nvl(dr,0) = 0)";
					DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO()
							.queryByCondition(DefdocVO.class, strWhere);
					if (null != defdoc && defdoc.length == 1) {
						def23 = defdoc[0].getName();
					}
				}
				Map hrz = OaWorkFlowUtil.listAdd("hrz", def23);
				workflowRequestTableFields.add(hrz);

				/* 期间 */
				Map qj = OaWorkFlowUtil.listAdd("qj", temp.getDef14());
				workflowRequestTableFields.add(qj);

				/* 起止码 */
				Map qzm = OaWorkFlowUtil.listAdd("qzm", temp.getDef24());
				workflowRequestTableFields.add(qzm);

				/* 用量 */
				// Map yl = OaWorkFlowUtil.listAdd("yl", temp.getDef25());
				Map yl = OaWorkFlowUtil.listAdd("yl", temp.getDef29());
				workflowRequestTableFields.add(yl);

				/* 单价 */
				Map dj = OaWorkFlowUtil.listAdd("dj", temp.getDef26());
				workflowRequestTableFields.add(dj);

				/* 税率 */
				String def17 = "0";
				if (null != temp.getDef17()) {
					String strWhere = "pk_defdoc = '"
							+ temp.getDef17()
							+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'GX-02' and nvl(dr,0) = 0)";
					DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO()
							.queryByCondition(DefdocVO.class, strWhere);
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
			}

			/* 合同号 */
			String str = "0";
			if (null != temp.getContractno()) {
				str = temp.getContractno();
			}
			Map hth = OaWorkFlowUtil.listAdd("hth", str);
			workflowRequestTableFields.add(hth);

			/* 合同金额 */
			Map htje = OaWorkFlowUtil.listAdd("htje", temp.getDef1());
			workflowRequestTableFields.add(htje);

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

			/* 往来对象3=业务员，2=部门，1=供应商 */
			Map wldx = OaWorkFlowUtil.listAdd("wldx", temp.getObjtype() + "");
			workflowRequestTableFields.add(wldx);

			/* 本次付款金额 */
			Map bcfkje = OaWorkFlowUtil.listAdd("bcfkje",
					temp.getLocal_money_de() + "");
			workflowRequestTableFields.add(bcfkje);

			/* 发票号 */
			String sr = "0";
			if (null != temp.getInvoiceno()) {
				sr = temp.getInvoiceno();
			}
			Map fph = OaWorkFlowUtil.listAdd("fph", sr);
			workflowRequestTableFields.add(fph);

			/* 已付款金额 */
			Map yfkje = OaWorkFlowUtil.listAdd("yfkje", temp.getDef3());
			workflowRequestTableFields.add(yfkje);

			/* 已付款比例 */
			Map yfkbl = OaWorkFlowUtil.listAdd("yfkbl", temp.getDef6());
			workflowRequestTableFields.add(yfkbl);

			/* 本次付款比例 */
			Map bcfkbl = OaWorkFlowUtil.listAdd("bcfkbl", temp.getDef7());
			workflowRequestTableFields.add(bcfkbl);

			/* 总付款比例 */
			Map zfkbl = OaWorkFlowUtil.listAdd("zfkbl", temp.getDef8());
			workflowRequestTableFields.add(zfkbl);

			/* 审计金额 */
			String s = "0";
			if (null != temp.getDef2()) {
				s = temp.getDef2();
			}
			Map sjje = OaWorkFlowUtil.listAdd("sjje", s);
			workflowRequestTableFields.add(sjje);

			/* 现金流量项目 */

			String x = (String) getHyPubBO().findColValue(
					"bd_cashflow",
					"name",
					"nvl(dr,0) = 0 and pk_cashflow  = '" + temp.getCashitem()
							+ "'");
			Map xjllxm = OaWorkFlowUtil.listAdd("xjllxm", x);
			workflowRequestTableFields.add(xjllxm);

			/* 折让金额28 */
			Map zrje = OaWorkFlowUtil.listAdd(
					"zrje",
					new UFDouble(temp.getDef28() == null ? "0.00" : temp
							.getDef28()).setScale(2, UFDouble.ROUND_HALF_UP)
							+ "");
			workflowRequestTableFields.add(zrje);

			/* 审计报告挂账14 */
			String def14 = "0";
			if (null != temp.getDef14()) {
				String strWhere = "pk_defdoc = '"
						+ temp.getDef14()
						+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'sf' and nvl(dr,0) = 0)";
				DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
						DefdocVO.class, strWhere);
				if (null != defdoc && defdoc.length == 1) {
					def14 = defdoc[0].getName();
				}
			}
			Map sjbggz = OaWorkFlowUtil.listAdd("sjbggz", def14);
			workflowRequestTableFields.add(sjbggz);

			/* 本次挂账金额35 */
			Map bcgzje = OaWorkFlowUtil.listAdd("bcgzje", temp.getDef35());
			workflowRequestTableFields.add(bcgzje);

			/* 累计挂账金额36 */
			Map ljgzje = OaWorkFlowUtil.listAdd("ljgzje", temp.getDef36());
			workflowRequestTableFields.add(ljgzje);

			/* 付款单行标识 */
			Map fkdxbs = OaWorkFlowUtil.listAdd("fkdxbs", temp.getPk_payitem());
			workflowRequestTableFields.add(fkdxbs);

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);

		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	// 工程付款单
	public static JSONArray getMainMap2(PayBillVO headVO, String supplier)
			throws BusinessException {

		List list = new ArrayList();

		// 工程付款单直接调用共有主表
		getList(headVO, list, supplier);

		/* 审计金额 */
		Map sjje = OaWorkFlowUtil.listAdd("sjje", headVO.getDef41());
		list.add(sjje);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	// 费用付款单/水电付款单//煤炭付款单
	public static JSONArray getMainMap5(PayBillVO headVO, String supplier)
			throws BusinessException {
		List list = new ArrayList();

		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				headVO.getPk_org());
		if (null != orgVO) {
			/* 组织 */
			Map szgs = OaWorkFlowUtil.listAdd("szgs", orgVO.getName());
			list.add(szgs);
			if ("2".equals(getDef2(headVO.getPk_org()))) {
				String khh = getGYSKHH(headVO.getPk_tradetype(),
						headVO.getRecaccount());
				Map gyskhh = OaWorkFlowUtil.listAdd("khh", khh);
				list.add(gyskhh);
			}
		}
		Map aaa = OaWorkFlowUtil.listAdd("aaa", "aaa");
		list.add(aaa);
		// 费用付款单
		if ("F3-Cxx-FYFKSPD".equals(headVO.getPk_tradetype())) {

			/* 合同现用名 */
			// if (null != headVO.getDef4()) {
			// String where = " pk_defdoc = '"
			// + headVO.getDef4()
			// +
			// "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'htxym' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
			// String def4 = "0";
			// if (null != (String) getHyPubBO().findColValue("bd_defdoc",
			// "name",
			// where)) {
			// def4 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
			// where);
			// }
			// Map htxym = OaWorkFlowUtil.listAdd("htxym", def4);
			// list.add(htxym);
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

			/* 实付金额 */
			Map sfje = OaWorkFlowUtil.listAdd(
					"sfje",
					new UFDouble(headVO.getDef26() == null ? "0.00" : headVO
							.getDef26()).setScale(2, UFDouble.ROUND_HALF_UP)
							+ "");
			list.add(sfje);

			/* 折让金额 */
			// String t1 = "0";
			// if (null != headVO.getDef31()) {
			// t1 = headVO.getDef31();
			// }
			// Map zrje = OaWorkFlowUtil.listAdd("zrje", t1);；
			// list.add(zrje);

			Map zrje = OaWorkFlowUtil.listAdd(
					"zrje",
					new UFDouble(headVO.getDef31() == null ? "0.00" : headVO
							.getDef31()).setScale(2, UFDouble.ROUND_HALF_UP)
							+ "");
			list.add(zrje);

			/* 金额大写 */
			Map jedx = OaWorkFlowUtil.listAdd("jedx",
					ConvertUpMoney.toChinese(headVO.getLocal_money() + ""));
			list.add(jedx);

			// 能投 费用付款审批单 新增收支项目 关联自定义档案
			if ("2".equals(getDef2(headVO.getPk_org()))) {
				String szxmstr = "0";
				if (headVO.getDef80() != null) {
					/* 收支项目 */
					szxmstr = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc = '"
									+ headVO.getDef80() + "'");
				}
				/* 收支项目 */
				Map szxm = OaWorkFlowUtil.listAdd("szxm", szxmstr + "");
				list.add(szxm);
			}
		}

		// 水电燃付款单
		if ("F3-Cxx-SDRFKSPD".equals(headVO.getPk_tradetype())) {

			/* 支付金额 */
			Map zfje = OaWorkFlowUtil.listAdd("zfje", headVO.getLocal_money()
					+ "");
			list.add(zfje);

			/* 金额大写 */
			Map zfjedx = OaWorkFlowUtil.listAdd("zfjedx",
					ConvertUpMoney.toChinese(headVO.getLocal_money() + ""));
			list.add(zfjedx);

			/* 备注 */
			Map bz = OaWorkFlowUtil.listAdd("bz", headVO.getDef2());
			list.add(bz);

			/* 1108 XBX新增 付款类型 */
			if ("2".equals(getDef2(headVO.getPk_org()))) {
				if (null != headVO.getDef3()) {
					String where = " pk_defdoc = '"
							+ headVO.getDef3()
							+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'Defdoc-GX-11' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
					String def3 = "0";
					if (null != (String) getHyPubBO().findColValue("bd_defdoc",
							"name", where)) {
						def3 = (String) getHyPubBO().findColValue("bd_defdoc",
								"name", where);
					}
					Map fklx = OaWorkFlowUtil.listAdd("fklx", def3);
					list.add(fklx);
				}
			}
		}

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
		String dw = (String) getHyPubBO().findColValue(
				"org_financeorg",
				"name",
				"nvl(dr,0) = 0 and pk_financeorg   = '" + headVO.getPk_org()
						+ "'");
		Map sqdw = OaWorkFlowUtil.listAdd("sqdw", dw);
		list.add(sqdw);

		/* 申请部门 */
		String bm = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and pk_dept  = '" + headVO.getDef76() + "'");
		Map sqbm = OaWorkFlowUtil.listAdd("sqbm", bm);
		list.add(sqbm);

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
		String dk = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and pk_dept  = '" + headVO.getPk_deptid() + "'");
		Map dkbm = OaWorkFlowUtil.listAdd("dkbm", dk);
		list.add(dkbm);

		/* 付款方式 */
		if (null != headVO.getDef35()) {
			String where = " pk_defdoc = '"
					+ headVO.getDef35()
					+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'FKFS' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
			String def35 = "0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					where)) {
				def35 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where);
			}
			Map fkfs = OaWorkFlowUtil.listAdd("fkfs", def35);
			list.add(fkfs);
		}

		/* 收款银行账户 */
		String yhname = (String) getHyPubBO().findColValue(
				"bd_bankaccsub",
				"accnum",
				"nvl(dr,0) = 0 and pk_bankaccsub  = '" + headVO.getRecaccount()
						+ "'");
		Map skyxzh = OaWorkFlowUtil.listAdd("skyxzh", yhname);
		list.add(skyxzh);

		/* 承兑金额 */
		// String st1 = "0";
		// if (null != headVO.getDef28()) {
		// st1 = headVO.getDef28();；
		// }
		// Map cdje = OaWorkFlowUtil.listAdd("cdje", st1);
		// list.add(cdje);
		Map cdje = OaWorkFlowUtil.listAdd(
				"cdje",
				new UFDouble(headVO.getDef27() == null ? "0.00" : headVO
						.getDef27()).setScale(2, UFDouble.ROUND_HALF_UP) + "");
		list.add(cdje);

		/* 付款事由 */
		Map fksy = OaWorkFlowUtil.listAdd("fksy", headVO.getDef2());
		list.add(fksy);

		/* 应付金额 */
		Map yfje = OaWorkFlowUtil.listAdd("yfje", headVO.getLocal_money() + "");
		list.add(yfje);

		/* 转账银行 */
		if (null != headVO.getDef24()) {
			String where = " pk_defdoc = '"
					+ headVO.getDef24()
					+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'YH' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
			String def24 = "0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					where)) {
				def24 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where);
			}
			Map zzyx = OaWorkFlowUtil.listAdd("zzyx", def24);
			list.add(zzyx);
		}

		/* 承兑银行 */
		if (null != headVO.getDef25()) {
			String def25 = "0";
			String where = " pk_defdoc = '"
					+ headVO.getDef25()
					+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'YH' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					where)) {
				def25 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where);
			}
			Map cdyx = OaWorkFlowUtil.listAdd("cdyx", def25);
			list.add(cdyx);
		}
		// 水电燃and费用付款单
		if ("F3-Cxx-SDRFKSPD".equals(headVO.getPk_tradetype())
				|| "F3-Cxx-FYFKSPD".equals(headVO.getPk_tradetype())) {
			/* 现金流量项目 */
			String xname = (String) getHyPubBO().findColValue(
					"bd_cashflow",
					"name",
					"nvl(dr,0) = 0 and pk_cashflow  = '" + headVO.getCashitem()
							+ "'");
			Map xjllxm = OaWorkFlowUtil.listAdd("xjllxm", xname);
			list.add(xjllxm);
		}

		// 煤炭外热购
		if ("F3-Cxx-MTWGRFKSPD".equals(headVO.getPk_tradetype())) {
			/* 合同名称 */
			Map htmc = OaWorkFlowUtil.listAdd("htmc", headVO.getDef80());
			list.add(htmc);

			/* 金额大写 */
			Map jedx = OaWorkFlowUtil.listAdd("jedx",
					ConvertUpMoney.toChinese(headVO.getLocal_money() + ""));
			list.add(jedx);

			/* 合同编码 */
			Map htbm = OaWorkFlowUtil.listAdd("htbm", headVO.getDef3());
			list.add(htbm);

			/* 应付总金额 */
			Map yfzje = OaWorkFlowUtil.listAdd(
					"yfzje",
					new UFDouble(headVO.getDef7() == null ? "0.00" : headVO
							.getDef7()).setScale(2, UFDouble.ROUND_HALF_UP)
							+ "");
			list.add(yfzje);

			/* 发票总金额 */
			Map fpzje = OaWorkFlowUtil.listAdd(
					"fpzje",
					new UFDouble(headVO.getDef8() == null ? "0.00" : headVO
							.getDef8()).setScale(2, UFDouble.ROUND_HALF_UP)
							+ "");
			list.add(fpzje);

			/* 已付金额 */
			Map yfje1 = OaWorkFlowUtil.listAdd(
					"yfje1",
					new UFDouble(headVO.getDef12() == null ? "0.00" : headVO
							.getDef12()).setScale(2, UFDouble.ROUND_HALF_UP)
							+ "");
			list.add(yfje1);

			/* 已付款比例 */
			Map yfkbl = OaWorkFlowUtil.listAdd("yfkbl", headVO.getDef14());
			list.add(yfkbl);

			/* 转账金额 */
			Map zzje = OaWorkFlowUtil.listAdd(
					"zzje",
					new UFDouble(headVO.getDef26() == null ? "0.00" : headVO
							.getDef26()).setScale(2, UFDouble.ROUND_HALF_UP)
							+ "");
			list.add(zzje);

			/* 支付金额 */
			Map zfje = OaWorkFlowUtil.listAdd("zfje", headVO.getLocal_money()
					+ "");
			list.add(zfje);

		}

		/* 附件张数 */
		Map fjzs = OaWorkFlowUtil
				.listAdd("fjzs", headVO.getAccessorynum() + "");
		list.add(fjzs);

		/* 凭证号 */
		String str = "0";
		if (null != headVO.getDef79()) {
			str = headVO.getDef79();
		}
		Map pzh = OaWorkFlowUtil.listAdd("pzh", str);
		list.add(pzh);

		/* 支付日期 */
		String q = "0";
		if (null != headVO.getDef33()) {
			q = headVO.getDef33();
		}
		Map zfrq = OaWorkFlowUtil.listAdd("zfrq", q);
		list.add(zfrq);

		/* CBS状态 */
		String cbs = "0";
		if (null != headVO.getDef34()) {
			cbs = headVO.getDef34();
		}
		Map cbszt = OaWorkFlowUtil.listAdd("cbszt", cbs);
		list.add(cbszt);

		/* 付款单标识 */
		Map fkdbs = OaWorkFlowUtil.listAdd("fkdbs", headVO.getPk_paybill());
		list.add(fkdbs);

		/* 付款财务组织 */
		String fname = (String) getHyPubBO().findColValue("org_orgs", "name",
				"nvl(dr,0) = 0 and pk_org  = '" + headVO.getPk_org() + "'");
		Map fkcwzz = OaWorkFlowUtil.listAdd("fkcwzz", fname);
		list.add(fkcwzz);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	// 资产购置付款单
	public static JSONArray getMainMap6(PayBillVO headVO, String supplier,
			String pk_deptid) throws BusinessException {
		List list = new ArrayList();

		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				headVO.getPk_org());
		if (null != orgVO) {
			/* 组织 */
			Map szgs = OaWorkFlowUtil.listAdd("szgs", orgVO.getName());
			list.add(szgs);
			if ("2".equals(getDef2(headVO.getPk_org()))) {
				String khh = getGYSKHH(headVO.getPk_tradetype(),
						headVO.getRecaccount());
				Map gyskhh = OaWorkFlowUtil.listAdd("khh", khh);
				list.add(gyskhh);
			}
		}

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

		/* 申请部门 */
		String bm = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and pk_dept  = '" + headVO.getDef76() + "'");
		Map sqbm = OaWorkFlowUtil.listAdd("sqbm", bm);
		list.add(sqbm);

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

		/* 代控部门主键 */
		Map dkbmzj = OaWorkFlowUtil.listAdd("dkbmzj", pk_deptid);
		list.add(dkbmzj);

		/* 代控部门编码 */
		String dkbmbmstr = (String) getHyPubBO().findColValue("org_dept",
				"code",
				"nvl(dr,0) = 0 and pk_dept = '" + pk_deptid + "'");
		Map dkbmbm = OaWorkFlowUtil.listAdd("dkbmbm", dkbmbmstr);
		list.add(dkbmbm);

		/* 代控部门 */
		String dk = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and pk_dept = '" + pk_deptid + "'");
		Map dkbm = OaWorkFlowUtil.listAdd("dkbm", dk);
		list.add(dkbm);

		/* 付款方式 */
		String def35 = "0";
		if (null != headVO.getDef35()) {
			String strWhere = "pk_defdoc = '"
					+ headVO.getDef35()
					+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'FKFS' and nvl(dr,0) = 0)";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				def35 = defdoc[0].getName();
			}
		}
		Map fkfs = OaWorkFlowUtil.listAdd("fkfs", def35);
		list.add(fkfs);

		/* 收款银行账户 */
		String yhname = (String) getHyPubBO().findColValue(
				"bd_bankaccsub",
				"accnum",
				"nvl(dr,0) = 0 and pk_bankaccsub  = '" + headVO.getRecaccount()
						+ "'");
		Map skyxzh = OaWorkFlowUtil.listAdd("skyxzh", yhname);
		list.add(skyxzh);

		/* 承兑金额 */
		// String st1 = "0";
		// if (null != headVO.getDef28()) {
		// st1 = headVO.getDef28();
		// }
		// Map cdje = OaWorkFlowUtil.listAdd("cdje", st1);
		// list.add(cdje);
		Map cdje = OaWorkFlowUtil.listAdd(
				"cdje",
				new UFDouble(headVO.getDef27() == null ? "0.00" : headVO
						.getDef27()).setScale(2, UFDouble.ROUND_HALF_UP) + "");
		list.add(cdje);

		/* 付款事由 */
		Map fksy = OaWorkFlowUtil.listAdd("fksy", headVO.getDef2());
		list.add(fksy);

		/* 应付金额 */
		Map yfje = OaWorkFlowUtil.listAdd("yfje", headVO.getLocal_money() + "");
		list.add(yfje);

		/* 折让金额 */
		// String t1 = "0";
		// if (null != headVO.getDef31()) {
		// t1 = headVO.getDef31();
		// }
		Map zrje = OaWorkFlowUtil.listAdd(
				"zrje",
				new UFDouble(headVO.getDef31() == null ? "0.00" : headVO
						.getDef31()).setScale(2, UFDouble.ROUND_HALF_UP) + "");
		list.add(zrje);

		/* 实付金额 */
		Map sfje = OaWorkFlowUtil.listAdd(
				"sfje",
				new UFDouble(headVO.getDef26() == null ? "0.00" : headVO
						.getDef26()).setScale(2, UFDouble.ROUND_HALF_UP) + "");
		list.add(sfje);

		/* 转账银行 */
		if (null != headVO.getDef24()) {
			String where = " pk_defdoc = '"
					+ headVO.getDef25()
					+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'YH' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
			String def24 = "0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					where)) {
				def24 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where);
			}
			Map zzyx = OaWorkFlowUtil.listAdd("zzyx", def24);
			list.add(zzyx);
		}

		/* 承兑银行 */
		// String b = "0";
		// if (null != headVO.getDef25()) {
		// b = headVO.getDef25();
		// }
		String def25 = "0";
		if (null != headVO.getDef25()) {
			String strWhere = "pk_defdoc = '"
					+ headVO.getDef25()
					+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'YH' and nvl(dr,0) = 0)";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				def25 = defdoc[0].getName();
			}
		}
		Map cdyx = OaWorkFlowUtil.listAdd("cdyx", def25);
		list.add(cdyx);

		/* 现金流量项目 */
		String xname = (String) getHyPubBO().findColValue(
				"bd_cashflow",
				"name",
				"nvl(dr,0) = 0 and pk_cashflow  = '" + headVO.getCashitem()
						+ "'");
		Map xjllxm = OaWorkFlowUtil.listAdd("xjllxm", xname);
		list.add(xjllxm);

		/* 附件张数 */
		Map fjzs = OaWorkFlowUtil
				.listAdd("fjzs", headVO.getAccessorynum() + "");
		list.add(fjzs);

		/* 凭证号 */
		String str = "0";
		if (null != headVO.getDef79()) {
			str = headVO.getDef79();
		}
		Map pzh = OaWorkFlowUtil.listAdd("pzh", str);
		list.add(pzh);

		/* 支付日期 */
		String q = "0";
		if (null != headVO.getDef33()) {
			q = headVO.getDef33();
		}
		Map zfrq = OaWorkFlowUtil.listAdd("zfrq", q);
		list.add(zfrq);

		/* CBS状态 */
		String cbs = "0";
		if (null != headVO.getDef33()) {
			cbs = headVO.getDef33();
		}
		Map cbszt = OaWorkFlowUtil.listAdd("cbszt", cbs);
		list.add(cbszt);

		/* 付款单标识 */
		Map fkdbs = OaWorkFlowUtil.listAdd("fkdbs", headVO.getPk_paybill());
		list.add(fkdbs);

		/* 付款财务组织 */
		String fname = (String) getHyPubBO().findColValue("org_orgs", "name",
				"nvl(dr,0) = 0 and pk_org  = '" + headVO.getPk_org() + "'");
		Map fkcwzz = OaWorkFlowUtil.listAdd("fkcwzz", fname);
		list.add(fkcwzz);

		/* 实付金额大写 */
		Map sfjedx = OaWorkFlowUtil.listAdd("yfjedx",
				ConvertUpMoney.toChinese(headVO.getLocal_money() + ""));
		list.add(sfjedx);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	// 资产购置付款单子表
	public static JSONArray getDtaileDataMapZC(AggregatedValueObject aggVO)
			throws BusinessException {
		PayBillItemVO[] bvos = (PayBillItemVO[]) aggVO.getChildrenVO();
		PayBillVO headVO = (PayBillVO) aggVO.getParentVO();
		List dtlist = new ArrayList();
		Map dtMap = new HashMap();
		// if(headVO.getPk_tradetype().contains("F3-Cxx-GDZC" )){
		if ("2".equals(getDef2(headVO.getPk_org()))) {
			dtMap.put("tableDBName", "formtable_main_427_dt1");
		} else {
			dtMap.put("tableDBName", "formtable_main_135_dt1");
		}
		// }
		List workflowRequestTableRecords = new ArrayList();
		for (PayBillItemVO temp : bvos) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();

			/* 合同号 */
			String str = "0";
			if (null != temp.getContractno()) {
				str = temp.getContractno();
			}
			Map hth = OaWorkFlowUtil.listAdd("hth", str);
			workflowRequestTableFields.add(hth);

			/* 合同金额 */
			Map htje = OaWorkFlowUtil.listAdd("htje", temp.getDef1());
			workflowRequestTableFields.add(htje);

			/* 项目 */
			String def31 = "0";
			if (null != temp.getDef31()) {
				String strWhere = "pk_defdoc = '"
						+ temp.getDef31()
						+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'xm' and nvl(dr,0) = 0)";
				DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
						DefdocVO.class, strWhere);
				if (null != defdoc && defdoc.length == 1) {
					def31 = defdoc[0].getName();
				}
			}
			Map xm = OaWorkFlowUtil.listAdd("xm", def31);
			workflowRequestTableFields.add(xm);

			/* 费用申请部门 */
			String dk = (String) getHyPubBO().findColValue(
					"org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept  = '" + temp.getPu_deptid()
							+ "'");
			Map fysqbm = OaWorkFlowUtil.listAdd("fysqbm", dk);
			workflowRequestTableFields.add(fysqbm);

			/* 本次付款金额 */
			Map bcfkje = OaWorkFlowUtil.listAdd("bcfkje",
					temp.getLocal_money_de() + "");
			workflowRequestTableFields.add(bcfkje);

			/* 资产类别 */
			String zclbname = (String) getHyPubBO().findColValue(
					"tb_budgetsub", "objname",
					"nvl(dr,0) = 0 and pk_obj  = '" + temp.getDef23() + "'");
			Map zclb = OaWorkFlowUtil.listAdd("zclb", zclbname);
			workflowRequestTableFields.add(zclb);

			/* 发票号 */
			String sr = "0";
			if (null != temp.getInvoiceno()) {
				sr = temp.getInvoiceno();
			}
			Map fph = OaWorkFlowUtil.listAdd("fph", sr);
			workflowRequestTableFields.add(fph);

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

			/* 已付款金额 */
			Map yfkje = OaWorkFlowUtil.listAdd("yfkje", temp.getDef3());
			workflowRequestTableFields.add(yfkje);

			/* 不含税金额 */
			Map bhsje = OaWorkFlowUtil.listAdd("bhsje", temp.getDef15());
			workflowRequestTableFields.add(bhsje);

			/* 备注 */
			Map bz = OaWorkFlowUtil.listAdd("bz", temp.getDef13());
			workflowRequestTableFields.add(bz);

			/* 总付款比例 */
			Map zfkbl = OaWorkFlowUtil.listAdd("zfkbl", temp.getDef8());
			workflowRequestTableFields.add(zfkbl);

			/* 税率 */
			String def17 = "0";
			if (null != temp.getDef17()) {
				String strWhere = "pk_defdoc = '"
						+ temp.getDef17()
						+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'GX-02' and nvl(dr,0) = 0)";
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

			/* 折让金额28 */
			Map zrje = OaWorkFlowUtil.listAdd(
					"zrje",
					new UFDouble(temp.getDef28() == null ? "0.00" : temp
							.getDef28()).setScale(2, UFDouble.ROUND_HALF_UP)
							+ "");
			workflowRequestTableFields.add(zrje);

			/* 付款单行标识 */
			Map fkdxbs = OaWorkFlowUtil.listAdd("fkdxbs", temp.getPk_payitem());
			workflowRequestTableFields.add(fkdxbs);

			/* 项目档案 */
			String m = (String) getHyPubBO()
					.findColValue(
							"bd_project",
							"project_name",
							"nvl(dr,0) = 0 and pk_project  = '"
									+ temp.getDef24() + "'");
			Map xmda = OaWorkFlowUtil.listAdd("xmda", m);
			workflowRequestTableFields.add(xmda);

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

	// 煤炭外热购子表
	public static JSONArray getDtaileDataMapMT(AggregatedValueObject aggVO)
			throws BusinessException {
		PayBillItemVO[] bvos = (PayBillItemVO[]) aggVO.getChildrenVO();
		PayBillVO headVO = (PayBillVO) aggVO.getParentVO();
		List dtlist = new ArrayList();
		Map dtMap = new HashMap();
		// if(headVO.getPk_tradetype().contains("F3-Cxx-GDZC" )){
		if ("2".equals(getDef2(headVO.getPk_org()))) {
			dtMap.put("tableDBName", "formtable_main_414_dt1");
		} else {
			dtMap.put("tableDBName", "formtable_main_107_dt1");
		}
		// }
		List workflowRequestTableRecords = new ArrayList();
		for (PayBillItemVO temp : bvos) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();

			// 预算类别
			String yslbname = (String) getHyPubBO().findColValue(
					"bd_inoutbusiclass",
					"name",
					"nvl(dr,0) = 0 and  pk_inoutbusiclass = '"
							+ temp.getPk_subjcode() + "'");
			Map yslb = OaWorkFlowUtil.listAdd("yslb", yslbname);
			workflowRequestTableFields.add(yslb);

			// 费用发生部门
			String fyfsbmname = (String) getHyPubBO().findColValue(
					"org_dept",
					"name",
					"nvl(dr,0) = 0 and  pk_dept = '" + temp.getPu_deptid()
							+ "'");
			Map fyfsbm = OaWorkFlowUtil.listAdd("fyfsbm", fyfsbmname);
			workflowRequestTableFields.add(fyfsbm);

			/* 税率 */
			String def17 = "0";
			if (null != temp.getDef17()) {
				String strWhere = "pk_defdoc = '"
						+ temp.getDef17()
						+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'GX-02' and nvl(dr,0) = 0)";
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

			/* 票据类型 */
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

			/* 合同号 */
			String str = "0";
			if (null != temp.getContractno()) {
				str = temp.getContractno();
			}
			Map hth = OaWorkFlowUtil.listAdd("hth", str);
			workflowRequestTableFields.add(hth);

			/* 本次付款金额 */
			Map bcfkje = OaWorkFlowUtil.listAdd("bcfkje",
					temp.getLocal_money_de() + "");
			workflowRequestTableFields.add(bcfkje);

			Map fplx = OaWorkFlowUtil.listAdd("fplx", def27);
			workflowRequestTableFields.add(fplx);

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

	// 特殊事项付款审批单
	public static JSONArray getTSMainMap(PayBillVO headVO, String supplier)
			throws BusinessException {
		List list = new ArrayList();

		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				headVO.getPk_org());
		if (null != orgVO) {
			/* 组织 */
			Map szgs = OaWorkFlowUtil.listAdd("szgs", orgVO.getName());
			list.add(szgs);
			if ("2".equals(getDef2(headVO.getPk_org()))) {
				String khh = getGYSKHH(headVO.getPk_tradetype(),
						headVO.getRecaccount());
				Map gyskhh = OaWorkFlowUtil.listAdd("khh", khh);
				list.add(gyskhh);
			}
		}

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

		/* 申请部门 */
		String bm = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and pk_dept  = '" + headVO.getDef76() + "'");
		Map sqbm = OaWorkFlowUtil.listAdd("sqbm", bm);
		list.add(sqbm);

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

		// 合同现用名
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

		// 承兑金额
		Map cdje = OaWorkFlowUtil.listAdd(
				"cdje",
				new UFDouble(headVO.getDef27() == null ? "0.00" : headVO
						.getDef27()).setScale(2, UFDouble.ROUND_HALF_UP) + "");
		list.add(cdje);

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
				Map sqr = new HashMap();
				sqr.put("fieldName", "sqr");
				sqr.put("fieldValue", userVO.getUser_name());
				list.add(sqr);
			} else {
				zdrzj.put("fieldValue", userVO.getUser_code());
				list.add(zdrzj);
			}
		}

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
		String dk = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and pk_dept  = '" + headVO.getPk_deptid() + "'");
		Map dkbm = OaWorkFlowUtil.listAdd("dkbm", dk);
		list.add(dkbm);

		/* 付款方式 */
		String def35 = "0";
		if (null != headVO.getDef35()) {
			String strWhere = "pk_defdoc = '"
					+ headVO.getDef35()
					+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'FKFS' and nvl(dr,0) = 0)";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				def35 = defdoc[0].getName();
			}
		}
		Map fkfs = OaWorkFlowUtil.listAdd("fkfs", def35);
		list.add(fkfs);

		/* 收款银行账户 */
		String yhname = (String) getHyPubBO().findColValue(
				"bd_bankaccsub",
				"accnum",
				"nvl(dr,0) = 0 and pk_bankaccsub  = '" + headVO.getRecaccount()
						+ "'");
		Map skyxzh = OaWorkFlowUtil.listAdd("skyxzh", yhname);
		list.add(skyxzh);

		/* 承兑金额 */
		// String st1 = "0";
		// if (null != headVO.getDef28()) {
		// st1 = headVO.getDef28();
		// }
		// Map cdje = OaWorkFlowUtil.listAdd("cdje", st1);
		// list.add(cdje);

		/* 付款事由 */
		Map fksy = OaWorkFlowUtil.listAdd("fksy", headVO.getDef2());
		list.add(fksy);

		/* 应付金额 */
		Map yfje = OaWorkFlowUtil.listAdd("yfje", headVO.getLocal_money() + "");
		list.add(yfje);

		/* 折让金额 */
		// String t1 = "0";
		// if (null != headVO.getDef31()) {
		// t1 = headVO.getDef31();
		// }
		Map zrje = OaWorkFlowUtil.listAdd(
				"zrje",
				new UFDouble(headVO.getDef31() == null ? "0.00" : headVO
						.getDef31()).setScale(2, UFDouble.ROUND_HALF_UP) + "");
		list.add(zrje);

		/* 实付金额 */
		Map sfje = OaWorkFlowUtil.listAdd(
				"sfje",
				new UFDouble(headVO.getDef26() == null ? "0.00" : headVO
						.getDef26()).setScale(2, UFDouble.ROUND_HALF_UP) + "");
		list.add(sfje);

		/* 转账银行 */
		if (null != headVO.getDef24()) {
			String where = " pk_defdoc = '"
					+ headVO.getDef25()
					+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'YH' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
			String def24 = "0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					where)) {
				def24 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where);
			}
			Map zzyx = OaWorkFlowUtil.listAdd("zzyx", def24);
			list.add(zzyx);
		}

		/* 承兑银行 */
		// String b = "0";
		// if (null != headVO.getDef25()) {
		// b = headVO.getDef25();
		// }
		String def25 = "0";
		if (null != headVO.getDef25()) {
			String strWhere = "pk_defdoc = '"
					+ headVO.getDef25()
					+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'YH' and nvl(dr,0) = 0)";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				def25 = defdoc[0].getName();
			}
		}
		Map cdyx = OaWorkFlowUtil.listAdd("cdyx", def25);
		list.add(cdyx);

		/* 现金流量项目 */
		String xname = (String) getHyPubBO().findColValue(
				"bd_cashflow",
				"name",
				"nvl(dr,0) = 0 and pk_cashflow  = '" + headVO.getCashitem()
						+ "'");
		Map xjllxm = OaWorkFlowUtil.listAdd("xjllxm", xname);
		list.add(xjllxm);

		/* 附件张数 */
		Map fjzs = OaWorkFlowUtil
				.listAdd("fjzs", headVO.getAccessorynum() + "");
		list.add(fjzs);

		/* 凭证号 */
		String str = "0";
		if (null != headVO.getDef79()) {
			str = headVO.getDef79();
		}
		Map pzh = OaWorkFlowUtil.listAdd("pzh", str);
		list.add(pzh);

		/* 支付日期 */
		String q = "0";
		if (null != headVO.getDef33()) {
			q = headVO.getDef33();
		}
		Map zfrq = OaWorkFlowUtil.listAdd("zfrq", q);
		list.add(zfrq);

		/* CBS状态 */
		String cbs = "0";
		if (null != headVO.getDef33()) {
			cbs = headVO.getDef33();
		}
		Map cbszt = OaWorkFlowUtil.listAdd("cbszt", cbs);
		list.add(cbszt);

		/* 付款单标识 */
		Map fkdbs = OaWorkFlowUtil.listAdd("fkdbs", headVO.getPk_paybill());
		list.add(fkdbs);

		/* 付款财务组织 */
		String fname = (String) getHyPubBO().findColValue("org_orgs", "name",
				"nvl(dr,0) = 0 and pk_org  = '" + headVO.getPk_org() + "'");
		Map fkcwzz = OaWorkFlowUtil.listAdd("fkcwzz", fname);
		list.add(fkcwzz);

		/* 实付金额大写 */
		Map sfjedx = OaWorkFlowUtil.listAdd("sfjedx",
				ConvertUpMoney.toChinese(headVO.getLocal_money() + ""));
		list.add(sfjedx);

		/* 付款类型 */
		String def55 = "0";
		if (null != headVO.getDef55()) {
			String strWhere = "pk_defdoc = '"
					+ headVO.getDef55()
					+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'NT02' and nvl(dr,0) = 0)";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				def55 = defdoc[0].getName();
			}
		}
		Map fklx = OaWorkFlowUtil.listAdd("fklx", def55);
		list.add(fklx);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	// 付款单
	public static JSONArray getDtaileDataMap(AggregatedValueObject temp)
			throws BusinessException {
		Map orderMap = new HashMap<>();
		if ("F3-Cxx-TSSXSPD".equals(((PayBillVO) temp.getParentVO())
				.getPk_tradetype())) {
			orderMap = getTSSXPayBillItemVO((PayBillItemVO[]) temp
					.getChildrenVO());
		} else {
			orderMap = getPayBillItemVO((PayBillItemVO[]) temp.getChildrenVO(),
					(PayBillVO) temp.getParentVO());
		}
		JSONArray dtlistString = JSONArray.fromObject(orderMap);
		return dtlistString;
	}

	public static Map getTSSXPayBillItemVO(PayBillItemVO[] childrenVO)
			throws BusinessException {
		// TODO Auto-generated method stub
		Map dtMap = new HashMap();
		dtMap.put("tableDBName", "formtable_main_449_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (PayBillItemVO temp : childrenVO) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();

			// 预算类别
			String yslbname = (String) getHyPubBO().findColValue(
					"bd_inoutbusiclass",
					"name",
					"nvl(dr,0) = 0 and  pk_inoutbusiclass = '"
							+ temp.getPk_subjcode() + "'");
			Map yslb = OaWorkFlowUtil.listAdd("yslb", yslbname);
			workflowRequestTableFields.add(yslb);

			// 费用发生部门
			String fyfsbmname = (String) getHyPubBO().findColValue(
					"org_dept",
					"name",
					"nvl(dr,0) = 0 and  pk_dept = '" + temp.getPu_deptid()
							+ "'");
			Map fyfsbm = OaWorkFlowUtil.listAdd("fyfsbm", fyfsbmname);
			workflowRequestTableFields.add(fyfsbm);

			/* 合同号 */
			String str = "0";
			if (null != temp.getContractno()) {
				str = temp.getContractno();
			}
			Map hth = OaWorkFlowUtil.listAdd("hth", str);
			workflowRequestTableFields.add(hth);

			/* 合同金额 */
			Map htje = OaWorkFlowUtil.listAdd("htje", temp.getDef1());
			workflowRequestTableFields.add(htje);

			/* 本次付款金额 */
			Map bcfkje = OaWorkFlowUtil.listAdd("bcfkje",
					temp.getLocal_money_de() + "");
			workflowRequestTableFields.add(bcfkje);

			/* 票据类型 */
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

			/* 已付款金额 */
			Map yfkje = OaWorkFlowUtil.listAdd("yfkje", temp.getDef3());
			workflowRequestTableFields.add(yfkje);

			/* 总付款比例 */
			Map zfkbl = OaWorkFlowUtil.listAdd("zfkbl", temp.getDef8());
			workflowRequestTableFields.add(zfkbl);

			/* 税率 */
			String def17 = "0";
			if (null != temp.getDef17()) {
				String strWhere = "pk_defdoc = '"
						+ temp.getDef17()
						+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'GX-02' and nvl(dr,0) = 0)";
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

			/* 折让金额 */
			Map zrje = OaWorkFlowUtil.listAdd(
					"zrje",
					new UFDouble(temp.getDef28() == null ? "0.00" : temp
							.getDef28()).setScale(2, UFDouble.ROUND_HALF_UP)
							+ "");
			workflowRequestTableFields.add(zrje);

			/* 项目编码 */
			String code = (String) getHyPubBO().findColValue(
					"bd_project",
					"project_code",
					"nvl(dr,0) = 0 and pk_project  = '" + temp.getProject()
							+ "'");
			Map xmbm = OaWorkFlowUtil.listAdd("xmbm", code);
			workflowRequestTableFields.add(xmbm);

			/* 项目名称 */
			String name = (String) getHyPubBO().findColValue(
					"bd_project",
					"project_name",
					"nvl(dr,0) = 0 and pk_project  = '" + temp.getProject()
							+ "'");
			Map xmmc = OaWorkFlowUtil.listAdd("xmmc", name);
			workflowRequestTableFields.add(xmmc);

			/* 备注 */
			Map bz = OaWorkFlowUtil.listAdd("bz", temp.getDef13());
			workflowRequestTableFields.add(bz);

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

	// 获取供应商开户行
	public static String getGYSKHH(String transi_type, String account)
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
