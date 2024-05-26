package nc.bs.conn.usertoelec.ace.bp;

import nc.impl.pubapp.pattern.data.vo.SchemeVOQuery;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;
import nc.vo.conn.usertoelec.ConnUserElecVO;

public class AceUsertoelecBP {

	public ConnUserElecVO[] queryByQueryScheme(IQueryScheme querySheme) {
		QuerySchemeProcessor p = new QuerySchemeProcessor(querySheme);
		p.appendFuncPermissionOrgSql();
		return new SchemeVOQuery<ConnUserElecVO>(ConnUserElecVO.class).query(querySheme,
				null);
	}
}
