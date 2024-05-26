package nc.ui.pmpub.action;

import java.awt.event.ActionEvent;

import nc.bs.logging.Logger;
import nc.bs.trade.business.HYPubBO;
import nc.oa.web.NCService;
import nc.oa.web.NCServiceSoap;
import nc.ui.pub.beans.MessageDialog;
import nc.vo.org.OrgVO;
import nc.vo.pcm.contractalter.ContrAlterBillVO;
import nc.vo.pcm.contractalter.ContrAlterHeadVO;

@SuppressWarnings("restriction")
public class BCXYPMUnApproveAction extends PMUnApproveAction {
	private static final long serialVersionUID = 1L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		// TODO 自动生成的方法存根
		/*ContrAlterBillVO billvo = (ContrAlterBillVO) this.getModel()
				.getSelectedData();
		ContrAlterBillVO contractBillVO = (ContrAlterBillVO) this.editor
				.getValue();
		ContrAlterHeadVO headVO = null;
		if (billvo == null) {
			headVO = (ContrAlterHeadVO) contractBillVO.getParentVO();
		} else {
			headVO = (ContrAlterHeadVO) billvo.getParentVO();
		}
		int diaResult = MessageDialog.showOkCancelDlg(null, "确认取消审批",
				"你确认要取消审批吗?");
		if (diaResult == 1) {
			OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
					headVO.getPk_org());
			if (orgVO.getDef2() == null) {
				// 调用webservice 启动取消审批的流程
				String billID = headVO.getPrimaryKey() + "";
				NCService service = new NCService();
				NCServiceSoap serviceSoap = service.getNCServiceSoap();
				String type = "12";
				Logger.error("-------开始调用webservic-------" + type);
				String webRtn = serviceSoap.cancelflow(type, billID);
				System.out.println("输出取消审批返回结果：" + webRtn);
				Logger.error("-------webservic调用结束-------");
				MessageDialog.showErrorDlg(null, "提示信息", "已提交OA审批，请稍等......");
			}
		} else {
			super.doAction(e);
		}*/
		super.doAction(e);
	}
}