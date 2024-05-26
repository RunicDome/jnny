package nc.impl.pub.ace;
import nc.bs.conn.usertoelec.ace.bp.AceUsertoelecBP;
import nc.impl.pubapp.pub.smart.SmartServiceImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.ISuperVO;
import nc.vo.conn.usertoelec.ConnUserElecVO;
import nc.vo.uif2.LoginContext;

public abstract class AceUsertoelecPubServiceImpl extends SmartServiceImpl {
	public ConnUserElecVO[] pubquerybasedoc(IQueryScheme querySheme)
			throws nc.vo.pub.BusinessException {
		return new AceUsertoelecBP().queryByQueryScheme(querySheme);
	}
}