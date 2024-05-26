package nc.bs.pm.pmfeebalancect.ace.bp;

import nc.bs.pm.m4201.query.Abstract4Z01RefQueryBP;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pcm.feebalance.FeeBalanceBillVO;
import nc.vo.pu.pub.enumeration.POEnumBillStatus;
import nc.vo.pubapp.pattern.pub.SqlBuilder;

public class QueryFor4Z01To4Z05BP extends Abstract4Z01RefQueryBP {
	public QueryFor4Z01To4Z05BP(IQueryScheme queryScheme) {
		/* 20 */super(queryScheme);
	}

	public StringBuilder makeGetPKSql() {
		String mainTableAlias = getHeadtb();
		String itemTableAlias = getItemtb();
		SqlBuilder whereSql = new SqlBuilder();
		whereSql.append(" and ");
		whereSql.append(mainTableAlias + ".fbillstatus",
				(Integer) POEnumBillStatus.APPROVE.value());
		whereSql.append(" and ");
		whereSql.append(mainTableAlias + ".dr = 0");
		whereSql.append(" and " + itemTableAlias + ".dr = 0");
		whereSql.append(" and (");
		whereSql.append(" " + itemTableAlias + ".vbdef20='~'");
		whereSql.append(" or (" + itemTableAlias
				+ ".vbdef20 != '~' and to_number(" + itemTableAlias
				+ ".nastnum) > to_number(" + itemTableAlias + ".vbdef20)))");

		StringBuilder wholeSql = new StringBuilder();
		wholeSql.append(" select distinct " + mainTableAlias
				+ ".pm_feebalance,");
		wholeSql.append(itemTableAlias + ".pm_feebalance_b ");
		wholeSql.append(this.psor.getFinalFromWhere());
		return wholeSql;
	}

	protected FeeBalanceBillVO[] processQueryResult(
			FeeBalanceBillVO[] queryResult) {
		return processQueryResult(queryResult);
	}
}