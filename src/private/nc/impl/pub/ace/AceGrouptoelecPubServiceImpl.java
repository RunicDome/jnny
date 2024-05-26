package nc.impl.pub.ace;
import nc.bs.conn.grouptoelec.ace.bp.AceGrouptoelecBP;
import nc.impl.pubapp.pub.smart.SmartServiceImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.conn.grouptoelec.ConnGroupElecVO;

public abstract class AceGrouptoelecPubServiceImpl extends SmartServiceImpl {
	public ConnGroupElecVO[] pubquerybasedoc(IQueryScheme querySheme)
			throws nc.vo.pub.BusinessException {
		return new AceGrouptoelecBP().queryByQueryScheme(querySheme);
	}
}