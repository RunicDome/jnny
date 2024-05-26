package nc.ui.bd.material.baseinfo.action;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.SwingWorker;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.pubitf.para.SysInitQuery;
import nc.ui.bd.uitabextend.ToftPanelAdaptorNCWithExt;
import nc.ui.pcm.utils.GetDao;
import nc.ui.uif2.actions.AddAction;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.bd.config.BDModeSelectedVO;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.uif2.LoginContext;
import nc.vo.util.BDModeManager;

// 物料-集团，新增按钮功能
@SuppressWarnings({ "unchecked", "rawtypes", "unused", "restriction" })
public class MaterialAddAction extends AddAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MaterialAddAction() {
	}

	public void doAction(final ActionEvent e) throws Exception {
		String fg = "0";
		String user = InvocationInfoProxy.getInstance().getUserId();// 当前用户主键
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		String querySql = "select c.cuserid, a.pk_role, a.role_name,c.user_name from sm_role a left join sm_user_role b  on a.pk_role=b.pk_role "
				+ " left join sm_user c on c.cuserid=b.cuserid  where a.role_code = 'WLZZ'";
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
			checkCanAddBySysPara();
			checkManageMode();
			final ToftPanelAdaptorNCWithExt ext = (ToftPanelAdaptorNCWithExt) getModel()
					.getContext().getEntranceUI();

			SwingWorker<Object, Object> sw = new SwingWorker() {
				protected void done() {
					try {
						MaterialAddAction.this.doActionExt(e);
					} catch (Exception e1) {
						Logger.error(e1.getMessage(), e1);
						throw new BusinessRuntimeException(e1.getMessage(), e1);
					}
				}

				protected Object doInBackground() throws Exception {
					while (!ext.isAfterResetLayout()) {
						MaterialAddAction.this.setEnabled(false);
						Thread.sleep(500L);
					}
					return null;
				}
			};
			sw.execute();
		} else {
			ExceptionUtils.wrappBusinessException("不允许自制！");
		}
	}

	private void doActionExt(ActionEvent e) throws Exception {
		super.doAction(e);
	}

	private void checkCanAddBySysPara() throws BusinessException {
		UFBoolean isCanAdd = SysInitQuery.getParaBoolean(getModel()
				.getContext().getPk_group(), "BD311");

		if (!isCanAdd.booleanValue()) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("10140mag", "010140mag0102"));
		}
	}

	private void checkManageMode() throws BusinessException {
		NODE_TYPE nodetype = getModel().getContext().getNodeType();
		BDModeSelectedVO modevo = BDModeManager.getInstance()
				.getBDModeSelectedVOByMDClassID(
						"c7dc0ccd-8872-4eee-8882-160e8f49dfad");

		int managemode = modevo.getManagemode().intValue();
		switch (managemode) {
		case 1:
		case 2:
		case 5:
			if (NODE_TYPE.ORG_NODE.equals(nodetype)) {
				throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("10140mag", "010140mag0214"));
			}
			break;
		}
	}
}
