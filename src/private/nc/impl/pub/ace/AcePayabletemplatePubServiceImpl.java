package nc.impl.pub.ace;
import nc.bs.uapbd.payabletemplate.ace.bp.AcePayabletemplateBP;
import nc.impl.pubapp.pub.smart.SmartServiceImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.ISuperVO;
import nc.vo.uap.payabletemplate.PayableTemplate;
import nc.vo.uif2.LoginContext;

public abstract class AcePayabletemplatePubServiceImpl extends SmartServiceImpl {
	public PayableTemplate[] pubquerybasedoc(IQueryScheme querySheme)
			throws nc.vo.pub.BusinessException {
		return new AcePayabletemplateBP().queryByQueryScheme(querySheme);
	}
}