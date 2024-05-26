package nc.ui.aim.equipmaintain.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.itf.aim.IEquipmaintainMaintain;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;

/**
 * 示例单据的操作代理
 * 
 * @author author
 * @version tempProject version
 */
public class AceEquipmaintainMaintainProxy implements IQueryService {
	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		IEquipmaintainMaintain query = NCLocator.getInstance().lookup(
				IEquipmaintainMaintain.class);
		return query.query(queryScheme);
	}

}