package nc.impl.ct.purdaily;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nc.bs.ct.purdaily.ctpriceqry.CtPriceQueryUtil;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
import nc.impl.ct.purdaily.action.MakePaybillAction;
import nc.impl.ct.purdaily.action.PurdailyDeleteAction;
import nc.impl.ct.purdaily.action.PurdailyInsertAction;
import nc.impl.ct.purdaily.action.PurdailyUpdateAction;
import nc.impl.ct.purdaily.action.QueryDinvoicemnyAction;
import nc.impl.pubapp.pattern.data.bill.BillQuery;
import nc.impl.pubapp.pattern.data.view.ViewQuery;
import nc.impl.pubapp.pattern.data.vo.VOQuery;
import nc.impl.pubapp.pattern.database.DBTool;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.itf.ct.purdaily.IPurdailyMaintain;
import nc.itf.pim.project.prv.IProjectQuery;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.vo.ct.enumeration.MarClassBoundEnum;
import nc.vo.ct.purdaily.entity.AggCtPuVO;
import nc.vo.ct.purdaily.entity.CtPuBVO;
import nc.vo.ct.purdaily.entity.CtPuVO;
import nc.vo.ct.purdaily.entity.CtPubillViewVO;
import nc.vo.ct.uitl.ValueUtil;
import nc.vo.pim.project.ProjectBillVO;
import nc.vo.pim.project.ProjectHeadVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.pub.SqlBuilder;
import nc.vo.pubapp.query2.sql.process.QueryCondition;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;

// 采购合同动作类
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
public class PurdailyMaintainImpl implements IPurdailyMaintain {
	public void deletePurdaily(AggCtPuVO[] bills) throws BusinessException {
		try {
			new PurdailyDeleteAction().delete(bills);
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
		}
	}

	public Map<String, CtPubillViewVO> getMutiPriceViewMap(
			Map<String, CtPubillViewVO> paramMap) throws BusinessException {
		try {
			Map<String, CtPubillViewVO> retMap = new HashMap();

			CtPubillViewVO[] viewVOs = (CtPubillViewVO[]) new ViewQuery(
					CtPubillViewVO.class).query((String[]) paramMap.keySet()
					.toArray(new String[0]));

			for (CtPubillViewVO view : viewVOs) {
				CtPubillViewVO param = (CtPubillViewVO) paramMap.get(view
						.getPk_ct_pu_b());
				view.setActualvalidate(param.getActualvalidate());
				view.setPk_org(param.getPk_org());
			}
			CtPubillViewVO[] retViewVOs = new CtPriceQueryUtil()
					.getMutiPriceViewVOs(viewVOs);

			for (CtPubillViewVO vo : retViewVOs) {
				retMap.put(vo.getPk_ct_pu_b(), vo);
			}
			return retMap;
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
		}
		return null;
	}

	public String[] getOIDs(int size) throws BusinessException {
		try {
			return new DBTool().getOIDs(size);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggregatedValueObject[] makePayBill(AggCtPuVO[] ctpuVo)
			throws BusinessException {
		try {
			return new MakePaybillAction().makePaybill(ctpuVo);
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
		}
		return null;
	}

	public AggCtPuVO[] queryCtPuVoByIds(String[] ids) throws BusinessException {
		if (ValueUtil.isEmpty(ids)) {
			return null;
		}
		AggCtPuVO[] bills = null;
		try {
			DataAccessUtils utils = new DataAccessUtils();

			IRowSet rowset = utils.query(createWholeSql(ids));
			String[] cbillids = rowset.toOneDimensionStringArray();
			if (ValueUtil.isEmpty(cbillids)) {
				return null;
			}
			BillQuery<AggCtPuVO> queryVO = new BillQuery(AggCtPuVO.class);
			bills = (AggCtPuVO[]) queryVO.query(cbillids);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}

		return bills;
	}

	public Map<String, UFDouble> queryDinvoicemny(CtPuVO[] headvos)
			throws BusinessException {
		Map<String, UFDouble> map = null;
		try {
			QueryDinvoicemnyAction action = new QueryDinvoicemnyAction();
			map = action.queryDinvoicemny(headvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return map;
	}

	public CtPuBVO[] queryPurdailyBVO(String[] ids) throws BusinessException {
		try {
			VOQuery<CtPuBVO> query = new VOQuery(CtPuBVO.class);
			return (CtPuBVO[]) query.query(ids);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtPuVO[] save(AggCtPuVO[] bills, Object obj,
			AggCtPuVO[] originBills) throws BusinessException {
		try {
			CtPuVO ctPuVO = bills[0].getParentVO();
			/**
			 * XBX添加保存校验 3、采购合同保存时校验该项目累计合同金额是否超过年度投资计划（最大）主材金额，超过不允许保存。
			 * （项目类型为：一次管网项目、数控类项目、技改类项目、二网改造项目校验保存；年度投资计划（最大）
			 * 主材金额：取多个年度投资计划中主材的最大值；累计合同金额：该项目已签订合同金额+本次该项目签订金额； 需要区分组织 热力）
			 */
			beforeCheck(bills[0]);
			if (ctPuVO.getStatus() == 1) {
				return new PurdailyUpdateAction().update(bills, false,
						originBills);
			}
			return new PurdailyInsertAction().insert(bills);
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
		}
		return null;
	}

	private void beforeCheck(AggCtPuVO bills) throws BusinessException {
		CtPuVO hvo = bills.getParentVO();
		CtPuBVO[] bodyVOS = bills.getCtPuBVO();
		String pk_org = hvo.getPk_org();// 组织
		String pk_ct_pu = hvo.getPrimaryKey();// 采购合同主键
		// 组织
		Object conforg = (Object) new HYPubBO().findColValue("bd_defdoc",
				"code",
				"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
						+ "FROM BD_DEFDOCLIST WHERE CODE = 'HTSAVEORG')"
						+ " and code = '" + pk_org + "'");// 组织
		if (conforg != null) {
			List<String> xmls = new ArrayList<String>();
			for (CtPuBVO itemvo : bodyVOS) {
				String cbprojectid = itemvo.getCbprojectid();// 项目PK
				xmls.add(cbprojectid);
			}
			List<String> newxmls = new ArrayList<String>();// 去重后项目主键
			for (String xmpk : xmls) {
				if (!newxmls.contains(xmpk)) {
					newxmls.add(xmpk);
				}
			}
			String errmsg = "";
			// 根据项目取签订合同金额（价税合计）
			for (String xmpk : newxmls) {
				// 查询项目VO
				IProjectQuery xmiq = (IProjectQuery) NCLocator.getInstance()
						.lookup(IProjectQuery.class);
				ProjectBillVO[] ss = xmiq
						.queryProjectHeadVOsBypks(new String[] { xmpk });
				// 项目VO
				ProjectHeadVO xmvo = ss[0].getParentVO();
				// 项目类型编码
				String xmlxbm = (String) new HYPubBO().findColValue(
						"bd_projectclass", "type_code", "pk_projectclass ='"
								+ xmvo.getPk_projectclass() + "'");
				// 项目类型自定义档案，如果当前项目的项目类型在项目类型自定义档案里，则校验，否则不校验
				Object xmlx = (Object) new HYPubBO().findColValue("bd_defdoc",
						"code",
						"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
								+ "FROM BD_DEFDOCLIST WHERE CODE = 'XMLXDA')"
								+ " and code = '" + xmlxbm + "'");
				if (xmlx != null) {
					String sql = "SELECT MX.CBPROJECTID,SUM(MX.NORIGTAXMNY) AS HTJE FROM CT_PU_B MX LEFT "
							+ "JOIN CT_PU ZB ON MX.PK_CT_PU = ZB.PK_CT_PU WHERE MX.DR = 0 AND ZB.BSHOWLATEST = 'Y'"
							+ " AND ZB.DR = 0 AND ZB.PK_ORG = '"
							+ pk_org
							+ "' AND "
							+ "ZB.PK_CT_PU <> '"
							+ pk_ct_pu
							+ "' AND zb.fstatusflag <> 6 AND MX.CBPROJECTID = '"
							+ xmpk
							+ "' GROUP BY MX.CBPROJECTID";
					List<Object[]> xmjels = (List<Object[]>) new BaseDAO()
							.executeQuery(sql, new ArrayListProcessor());
					double money = 0;// 取累计合同金额
					if (xmjels != null && xmjels.size() > 0
							&& xmjels.get(0)[0] != null)
						money = Double.parseDouble(xmjels.get(0)[1] + "");
					double curmoney = 0;// 取本单合同金额
					for (CtPuBVO itemvo : bodyVOS) {
						String cbprojectid = itemvo.getCbprojectid();// 项目PK
						if (cbprojectid.equals(xmpk)) {
							curmoney += Double.parseDouble(itemvo
									.getNorigtaxmny() + "");
						}
					}
					double ljje = money + curmoney;// 合计总金额
					double ndtzjhzd = 0;// 年度投资计划中最大主材
					String ndsql = "SELECT MAX(TO_NUMBER(CASE WHEN DEF26 = '~' THEN '0' ELSE DEF26 END)) AS ZDZC FROM PM_YEARPLAN_B "
							+ "WHERE DR = 0 AND PK_PROJECT = '"
							+ xmpk
							+ "' AND PK_ORG = '" + pk_org + "'";
					List<Object[]> ndjels = (List<Object[]>) new BaseDAO()
							.executeQuery(ndsql, new ArrayListProcessor());
					if (ndjels != null && ndjels.size() > 0
							&& ndjels.get(0)[0] != null)
						ndtzjhzd = Double.parseDouble(ndjels.get(0)[0] + "");

					if (ndjels != null && ndjels.size() > 0
							&& ndjels.get(0) != null
							&& ndjels.get(0)[0] != null
							&& !"".equals(ndjels.get(0)[0])
							&& !"null".equals(ndjels.get(0)[0])) {
						if (ndtzjhzd <= 0) {
							errmsg = "项目编码[" + xmvo.getProject_code()
									+ "],费用项：主材 没有年度投资计划值，请添加后在进行操作";
						} else {
							if (ljje > ndtzjhzd) {
								errmsg = "项目编码："
										+ xmvo.getProject_code()
										+ ";费用项：主材;已录入合同金额："
										+ new UFDouble(money).setScale(2,
												UFDouble.ROUND_HALF_UP)
										+ "+本次合同金额："
										+ new UFDouble(curmoney).setScale(2,
												UFDouble.ROUND_HALF_UP)
										+ ";超出投资计划费用项最大值["
										+ new UFDouble(ndtzjhzd).setScale(2,
												UFDouble.ROUND_HALF_UP) + "]";
							}
						}
					} else {
						errmsg = "项目编码[" + xmvo.getProject_code()
								+ "]无年度投资计划，不允许保存";
					}
				}
			}
			if (StringUtils.isNotEmpty(errmsg)) {
				throw new BusinessException(errmsg);
			}
		}
	}

	private String createWholeSql(String[] ids) {
		SqlBuilder sql = new SqlBuilder();
		sql.append("select distinct ct_pu.pk_ct_pu from ct_pu where ");
		sql.append("ct_pu.pk_ct_pu", ids);
		sql.append(" and ct_pu.dr = 0 ");
		sql.append(" and ct_pu.blatest = 'Y' ");
		return sql.toString();
	}

	private String getBbracketorderSql(String headTableName) {
		SqlBuilder sql = new SqlBuilder();
		sql.append(" and ");
		sql.append(headTableName + "." + "bbracketorder", UFBoolean.FALSE);

		return sql.toString();
	}

	private void putMarbasclassIntoCondition(
			QuerySchemeProcessor qrySchemeProcessor) {
		String bodyTableName = qrySchemeProcessor
				.getTableAliasOfAttribute("pk_ct_pu_b.pk_marbasclass");

		QueryCondition conditionMarbasclass = qrySchemeProcessor
				.getQueryCondition("pk_ct_pu_b.pk_marbasclass");

		QueryCondition conditionMaterialBound = qrySchemeProcessor
				.getQueryCondition("marbasclassbound");

		if ((null != conditionMarbasclass)
				&& (conditionMarbasclass.getValues().length > 0)) {
			Object[] values = conditionMarbasclass.getValues();
			SqlBuilder inConditionNew = new SqlBuilder();

			inConditionNew.append(values[0]);

			Object[] boundValues = conditionMaterialBound.getValues();
			SqlBuilder boundCondition = new SqlBuilder();
			for (Object boundValue : boundValues) {
				if (boundCondition.toString().length() != 0) {
					boundCondition.append(" or ");
				}

				if (MarClassBoundEnum.MATERIAL.getEnumValue().getValue()
						.equals(boundValue)) {
					boundCondition.append(bodyTableName);
					boundCondition.append(".");
					boundCondition.append("pk_material");
					boundCondition.append(" in (select ");
					boundCondition.append("pk_material");
					boundCondition.append(" from bd_material where ");
					boundCondition.append("pk_marbasclass");
					boundCondition.append(" in (");
					boundCondition.append(inConditionNew.toString());
					boundCondition.append("))");

				} else if (MarClassBoundEnum.MARBASCLASS.getEnumValue()
						.getValue().equals(boundValue)) {
					boundCondition.append(bodyTableName);
					boundCondition.append(".");
					boundCondition.append("pk_marbasclass");
					boundCondition.append(" in (");
					boundCondition.append(inConditionNew.toString());
					boundCondition.append(")");
				}
			}
			qrySchemeProcessor.appendWhere(" and (" + boundCondition.toString()
					+ ")");
		}
	}
}
