package nc.bs.pm.m4201.query;

import java.util.HashSet;
import java.util.Set;

import nc.bs.scmpub.util.SCMDataAccessUtils;
import nc.impl.pubapp.pattern.data.vo.VOQuery;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pm.feebalance.PmFeebalanceBVO;
import nc.vo.pm.feebalance.PmFeebalanceHVO;
import nc.vo.pubapp.pattern.model.tool.BillComposite;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;

public abstract class Abstract4Z01RefQueryBP {
	protected String headtb;
	protected String itemtb;
	protected QuerySchemeProcessor psor;

	public Abstract4Z01RefQueryBP(IQueryScheme queryScheme) {
		this.psor = new QuerySchemeProcessor(queryScheme);
		this.headtb = this.psor.getMainTableAlias();
		this.itemtb = this.psor
				.getTableAliasOfAttribute("pm_feebalance_b.pm_feebalance_b");
	}

	public String getHeadtb() {
		return this.headtb;
	}

	public String getItemtb() {
		return this.itemtb;
	}

	public QuerySchemeProcessor getPsor() {
		return this.psor;
	}

	public abstract StringBuilder makeGetPKSql();

	public AggPmFeebalance[] queryStoreReqVOs() {
		SCMDataAccessUtils utils = new SCMDataAccessUtils(10000);

		this.psor.appendCurrentGroup();

		StringBuilder sql = makeGetPKSql();

		String[][] pks = utils.query(sql.toString())
				.toTwoDimensionStringArray();
		if ((null == pks) || (pks.length == 0)) {
			return null;
		}
		Set<String> headPks = new HashSet();
		String[] itemPks = new String[pks.length];

		for (int i = 0; i < pks.length; i++) {
			headPks.add(pks[i][0]);
			itemPks[i] = pks[i][1];
		}

		PmFeebalanceHVO[] headers = (PmFeebalanceHVO[]) new VOQuery(
				PmFeebalanceHVO.class).query((String[]) headPks
				.toArray(new String[headPks.size()]));

		PmFeebalanceBVO[] items = (PmFeebalanceBVO[]) new VOQuery(
				PmFeebalanceBVO.class).query(itemPks);

		BillComposite<AggPmFeebalance> bc = new BillComposite(
				AggPmFeebalance.class);

		AggPmFeebalance tempVO = new AggPmFeebalance();
		bc.append(tempVO.getMetaData().getParent(), headers);
		bc.append(tempVO.getMetaData().getVOMeta(PmFeebalanceBVO.class), items);
		AggPmFeebalance[] queryResult = (AggPmFeebalance[]) bc.composite();

		return processQueryResult(queryResult);
	}

	public void setHeadtb(String headtb) {
		this.headtb = headtb;
	}

	public void setItemtb(String itemtb) {
		this.itemtb = itemtb;
	}

	public void setPsor(QuerySchemeProcessor psor) {
		this.psor = psor;
	}

	protected AggPmFeebalance[] processQueryResult(AggPmFeebalance[] queryResult) {
		return queryResult;
	}
}
