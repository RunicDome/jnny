package nc.ui.pu.m21.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.itf.pu.m21.act.UnapproveToERM;
import nc.ui.pub.pf.PfUtilClient;
import nc.ui.pubapp.uif2app.AppUiState;
import nc.ui.pubapp.uif2app.actions.pflow.UNApproveScriptAction;
import nc.vo.pu.m21.entity.OrderVO;
import nc.vo.pu.m21.entity.PayPlanVO;
import nc.vo.pu.pub.util.ApproveFlowUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.vo.pubapp.pattern.model.transfer.bill.ClientBillCombinServer;
import nc.vo.pubapp.pflow.PFReturnObject;

/**
 * 采购订单取消审批校验二维码是否可取消审批
 * 
 * @author Xbx
 */
@SuppressWarnings({ "restriction", "unchecked", "rawtypes" })
public class OrderUnApproveAction extends UNApproveScriptAction {
	private static final long serialVersionUID = 6758518838761391849L;
	UnapproveToERM unapp = NCLocator.getInstance().lookup(UnapproveToERM.class);

	public OrderUnApproveAction() {
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		// TODO Auto-generated method stub
		Object[] objs = this.model.getSelectedOperaDatas();
		if (objs != null && objs.length > 0) {
			for (Object obj : objs) {
				OrderVO aggvo = (OrderVO) obj;
				unapp.unApprove(aggvo.getParentVO().getPrimaryKey());
				super.doAction(e);
			}
		}

	}

	protected boolean isActionEnable() {
		Object[] objs = this.model.getSelectedOperaDatas();
		if ((objs != null) && (objs.length > 1)) {
			return true;
		}

		boolean isEnabled = false;
		if (this.model.getSelectedData() != null) {
			isEnabled = this.model.getAppUiState() == AppUiState.NOT_EDIT;
		}

		return (isEnabled)
				&& (ApproveFlowUtil.isCanUnApprove((AbstractBill) this.model
						.getSelectedData()));
	}

	protected void processReturnObj(Object[] pretObj) throws Exception {
		Object[] retObj = pretObj;
		if ((retObj == null) || (retObj.length == 0)) {
			if (PfUtilClient.isSuccess()) {
				this.model.setAppUiState(AppUiState.NOT_EDIT);
			}
			return;
		}
		if ((pretObj instanceof PFReturnObject[])) {
			retObj = ((PFReturnObject) pretObj[0]).getBills();
		}
		if ((PfUtilClient.isSuccess())
				&& ((retObj[0] instanceof AggregatedValueObject))) {
			OrderVO[] retVOs = (OrderVO[]) retObj;
			for (OrderVO vo : retVOs) {
				vo.setChildren(PayPlanVO.class, null);
			}
			new ClientBillCombinServer().combine((IBill[]) getFullOldVOs(),
					(IBill[]) retObj);

			OrderVO[] vos = (OrderVO[]) getFullOldVOs();
			for (OrderVO vo : vos) {
				vo.setChildren(PayPlanVO.class, null);
			}
			this.model.directlyUpdate(vos);
			this.model.setAppUiState(AppUiState.NOT_EDIT);
		}

		if (getMultibillScriptRunner().isTaskSuccessful()) {
			showSuccessInfo();
		}
		if ((!getMultibillScriptRunner().isTaskSuccessful())
				&& (retObj.length > 1)) {
			showFailedInfo();
		}
	}

}