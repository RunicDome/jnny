package nc.ui.pbm.action;

import java.awt.event.ActionEvent;
import java.util.List;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.ui.pcm.utils.GetDao;
import nc.ui.pm.action.AddAction;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class PmAddAction extends AddAction {
	private static final long serialVersionUID = 1L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		String fg = "0";
		String user = InvocationInfoProxy.getInstance().getUserId();// 当前用户主键
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		String querySql = "select c.cuserid, a.pk_role, a.role_name,c.user_name from sm_role a left join sm_user_role b  on a.pk_role=b.pk_role "
				+ " left join sm_user c on c.cuserid=b.cuserid  where a.role_code = 'WZZZ'";
		List<Object[]> resultList = getDao.query(querySql);
		if (resultList.size() > 0) {
			for (int i = 0; i < resultList.size(); i++) {
				Object[] item = resultList.get(i);
				if (user.equals(item[0])) {
					fg = "1";
				}
			}
		}
		if ("1".equals(fg)) {
			super.doAction(e);
		} else {
			ExceptionUtils.wrappBusinessException("不允许自制！");
		}
	}
}
