package nc.ui.pcm.pcmproject.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.itf.pcm.IPcmprojectMaintain;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;

/**
 * 示例单据的操作代理
 * 
 * @author author
 * @version tempProject version
 */
@SuppressWarnings("restriction")
public class AcePcmprojectMaintainProxy implements IQueryService {
	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		IPcmprojectMaintain query = NCLocator.getInstance().lookup(
				IPcmprojectMaintain.class);
		return query.query(queryScheme);
	}

}