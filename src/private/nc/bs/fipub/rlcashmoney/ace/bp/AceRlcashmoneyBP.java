package nc.bs.fipub.rlcashmoney.ace.bp;

import nc.impl.pubapp.pattern.data.vo.SchemeVOQuery;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;
import nc.vo.fipub.rlcashmoney.CashMoneyVO;

public class AceRlcashmoneyBP {

	public CashMoneyVO[] queryByQueryScheme(IQueryScheme querySheme) {
		QuerySchemeProcessor p = new QuerySchemeProcessor(querySheme);
		p.appendFuncPermissionOrgSql();
		return new SchemeVOQuery<CashMoneyVO>(CashMoneyVO.class).query(querySheme,
				null);
	}
}
