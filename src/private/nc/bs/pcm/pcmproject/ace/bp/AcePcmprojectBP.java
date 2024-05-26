package nc.bs.pcm.pcmproject.ace.bp;

import nc.impl.pubapp.pattern.data.vo.SchemeVOQuery;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pcm.pcmproject.PcmProject;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;

public class AcePcmprojectBP {

	public PcmProject[] queryByQueryScheme(IQueryScheme querySheme) {
		QuerySchemeProcessor p = new QuerySchemeProcessor(querySheme);
		p.appendFuncPermissionOrgSql();
		return new SchemeVOQuery<PcmProject>(PcmProject.class).query(
				querySheme, null);
	}
}
