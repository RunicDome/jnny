package nc.ui.pcm.pcmyearplan.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.itf.pcm.IPcmyearplanMaintain;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;

/**
 * 示例单据的操作代理
 * 
 * @author author
 * @version tempProject version
 */
@SuppressWarnings("restriction")
public class AcePcmyearplanMaintainProxy implements IQueryService {
	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		IPcmyearplanMaintain query = NCLocator.getInstance().lookup(
				IPcmyearplanMaintain.class);
		return query.query(queryScheme);
	}

}