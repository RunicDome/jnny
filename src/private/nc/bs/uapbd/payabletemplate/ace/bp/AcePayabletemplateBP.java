package nc.bs.uapbd.payabletemplate.ace.bp;

import nc.impl.pubapp.pattern.data.vo.SchemeVOQuery;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;
import nc.vo.uap.payabletemplate.PayableTemplate;

public class AcePayabletemplateBP {

	public PayableTemplate[] queryByQueryScheme(IQueryScheme querySheme) {
		QuerySchemeProcessor p = new QuerySchemeProcessor(querySheme);
		p.appendFuncPermissionOrgSql();
		return new SchemeVOQuery<PayableTemplate>(PayableTemplate.class).query(querySheme,
				null);
	}
}
