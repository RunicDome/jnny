package nc.ui.pubapp.uif2app.actions.pflow;

import java.awt.event.ActionEvent;
import java.util.List;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.ppm.schedulefill.IShceduleFillInterface;
import nc.itf.ppm.schedulefill.TaskUtils;
import nc.itf.uap.pf.IplatFormEntry;
import nc.oa.web.NCService;
import nc.oa.web.NCServiceSoap;
import nc.ui.pcm.utils.GetDao;
import nc.ui.pub.beans.MessageDialog;
import nc.vo.pcm.contract.ContrHeadVO;
import nc.vo.pcm.contract.ContractBillVO;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;

@SuppressWarnings({ "restriction", "unused" })
public class QDFBHTUNApproveScriptAction extends UNApproveScriptAction {

	private static final long serialVersionUID = 1L;
	IplatFormEntry ip = (IplatFormEntry) NCLocator.getInstance().lookup(
			IplatFormEntry.class);// 鍔ㄤ綔鎵ц绫�
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);

	public void doAction(ActionEvent arg0) throws Exception {
		super.doAction(arg0);
	}

	/*public List<Object[]> Schepk(String billPK) throws DAOException {
		String sql = "SELECT PK_TASKSCHEDULE FROM PM_TASKSCHEDULE WHERE HDEF10 = '"
				+ billPK + "' AND DR = 0";
		List<Object[]> ls = getDao.query(sql);
		return ls;
	}*/
}
