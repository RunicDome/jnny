package nc.bs.uapbd.grouptoelec.ace.bp;

import nc.impl.pubapp.pattern.data.vo.SchemeVOQuery;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;
import nc.vo.conn.grouptoelec.ConnGroupElecVO;

public class AceGrouptoelecBP {

	public ConnGroupElecVO[] queryByQueryScheme(IQueryScheme querySheme) {
		QuerySchemeProcessor p = new QuerySchemeProcessor(querySheme);
		p.appendFuncPermissionOrgSql();
		return new SchemeVOQuery<ConnGroupElecVO>(ConnGroupElecVO.class).query(querySheme,
				null);
	}
}
