package nc.impl.pub.ace;
import nc.bs.conn.orgtoelec.ace.bp.AceOrgtoelecBP;
import nc.impl.pubapp.pub.smart.SmartServiceImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.ISuperVO;
import nc.vo.conn.orgtoelec.ConnOrgElecVO;
import nc.vo.uif2.LoginContext;

public abstract class AceOrgtoelecPubServiceImpl extends SmartServiceImpl {
	public ConnOrgElecVO[] pubquerybasedoc(IQueryScheme querySheme)
			throws nc.vo.pub.BusinessException {
		return new AceOrgtoelecBP().queryByQueryScheme(querySheme);
	}
}