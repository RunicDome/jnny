package nc.ui.aim.equipinsp.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.itf.aim.IEquipinspMaintain;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.vo.pub.BusinessException;

/**
 * 示例单据的操作代理
 * 
 * @author author
 * @version tempProject version
 */
public class AceEquipinspMaintainProxy implements IQueryService,
		IPaginationQueryService {
	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		IEquipinspMaintain query = NCLocator.getInstance().lookup(
				IEquipinspMaintain.class);
		return query.query(queryScheme);
	}

	@Override
	public Object[] queryObjectByPks(String[] pks) throws BusinessException {
		// TODO Auto-generated method stub
		return NCLocator.getInstance().lookup(IEquipinspMaintain.class)
				.queryObjectByPks(pks);
	}
}