package nc.bs.conn.orgtoelec.ace.bp;

import nc.impl.pubapp.pattern.data.vo.SchemeVOQuery;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;
import nc.vo.conn.orgtoelec.ConnOrgElecVO;

public class AceOrgtoelecBP {

	public ConnOrgElecVO[] queryByQueryScheme(IQueryScheme querySheme) {
		QuerySchemeProcessor p = new QuerySchemeProcessor(querySheme);
		p.appendFuncPermissionOrgSql();
		return new SchemeVOQuery<ConnOrgElecVO>(ConnOrgElecVO.class).query(querySheme,
				null);
	}
}
