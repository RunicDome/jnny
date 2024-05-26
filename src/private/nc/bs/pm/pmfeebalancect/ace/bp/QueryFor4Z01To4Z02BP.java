package nc.bs.pm.pmfeebalancect.ace.bp;

import nc.bs.pm.m4201.query.Abstract4Z01RefQueryBP;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pbm.budget.BillStatusEnum;
import nc.vo.pcm.feebalance.FeeBalanceBillVO;
import nc.vo.pcm.feebalance.FeeBalanceBodyVO;
import nc.vo.pu.m422x.entity.StoreReqAppItemVO;
import nc.vo.pu.m422x.entity.StoreReqAppVO;
import nc.vo.pu.pub.enumeration.POEnumBillStatus;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.pub.SqlBuilder;

public class QueryFor4Z01To4Z02BP extends Abstract4Z01RefQueryBP {
	public QueryFor4Z01To4Z02BP(IQueryScheme queryScheme) {
		super(queryScheme);
	}

	public StringBuilder makeGetPKSql() {
		String mainTableAlias = getHeadtb();
		String itemTableAlias = getItemtb();
		SqlBuilder whereSql = new SqlBuilder();
		whereSql.append(" and ");
		whereSql.append(mainTableAlias + ".fstatusflag",
				(Integer) BillStatusEnum.APPROVED.value());
		whereSql.append(" and ");
		whereSql.append(mainTableAlias + ".dr = 0");
		whereSql.append(" and " + itemTableAlias + ".dr = 0 ");
		whereSql.append(" and " + itemTableAlias
				+ ".contractbalance_mny is null " );
		StringBuilder wholeSql = new StringBuilder();
		wholeSql.append(" select distinct " + mainTableAlias
				+ ".pm_feebalance,");
		wholeSql.append(itemTableAlias + ".pm_feebalance_b ");
		wholeSql.append(this.psor.getFinalFromWhere());
		wholeSql.append(whereSql);
		return wholeSql;
	}

	protected FeeBalanceBillVO[] processQueryResult(
			FeeBalanceBillVO[] queryResult) {
		return processQueryResult(queryResult);
	}
}