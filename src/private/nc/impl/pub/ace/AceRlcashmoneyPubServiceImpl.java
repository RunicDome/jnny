package nc.impl.pub.ace;
import nc.bs.fipub.rlcashmoney.ace.bp.AceRlcashmoneyBP;
import nc.impl.pubapp.pub.smart.SmartServiceImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.ISuperVO;
import nc.vo.fipub.rlcashmoney.CashMoneyVO;
import nc.vo.uif2.LoginContext;

public abstract class AceRlcashmoneyPubServiceImpl extends SmartServiceImpl {
	public CashMoneyVO[] pubquerybasedoc(IQueryScheme querySheme)
			throws nc.vo.pub.BusinessException {
		return new AceRlcashmoneyBP().queryByQueryScheme(querySheme);
	}
}