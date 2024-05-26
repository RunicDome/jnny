package nc.ui.fct.action;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.filesystem.FileManageUI;
import nc.ui.pubapp.uif2app.actions.FileDocManageAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.utils.fipub.FIFileManageConst;
import nc.vo.bd.meta.IBDObject;
import nc.vo.fct.ap.entity.AggCtApVO;
import nc.vo.fct.ar.entity.AggCtArVO;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.uif2.LoginContext;

@SuppressWarnings({ "restriction", "unused" })
public class FctFileDocManageAction extends FileDocManageAction {
	private static final long serialVersionUID = -1L;

	public FctFileDocManageAction() {
	}

	public void doAction(ActionEvent e) throws Exception {
		Object selectedData = getModel().getSelectedData();
		IBDObject target = createBDObject(selectedData);
		if (target != null) {
			String rootPath = (String) target.getId();
			String userCode = WorkbenchEnvironment.getInstance().getLoginUser()
					.getUser_code();

			String funcode = getModel().getContext().getNodeCode();
			String pk_group = getModel().getContext().getPk_group();
			FileManageUI ui = nc.ui.pub.filesystem.FileManageUIFactory
					.getFileManagePnl(rootPath, userCode, funcode, pk_group);
			ui.setTreeRootVisible(false);
			// XBX 控制按钮是否可用
			boolean isEdit = false;
			Integer sfkstatus = 99;
			// 单据状态为审批通过态不能进行删除上传附件
			if (selectedData instanceof AggCtApVO) {
				AggCtApVO vo = (AggCtApVO) selectedData;
				sfkstatus = vo.getParentVO().getFstatusflag();
			} else if (selectedData instanceof AggCtArVO) {
				AggCtArVO vo = (AggCtArVO) selectedData;
				sfkstatus = vo.getParentVO().getFstatusflag();
			}
			// 生效、正在审批、审批通过
			if (sfkstatus == 1 || sfkstatus == 3) {
				ui.setUploadFileEnable(false);
				ui.setDeleteNodeEnable(false);
			}
			
			UIDialog dlg = new UIDialog(
					getModel().getContext().getEntranceUI(),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"pubapp_0", "0pubapp-0129"));

			dlg.getContentPane().setLayout(new BorderLayout());
			dlg.getContentPane().add(ui, "Center");
			dlg.setReset(true);
			dlg.setResizable(true);
			dlg.setSize(FIFileManageConst.FIFILEMANAGEDLGDIMENSION);
			dlg.showModal();
		}
	}

	private void updateButton(FileManageUI manageUI, boolean isEdit) {
		manageUI.setCreateNewFolderEnable(isEdit);
		manageUI.setDeleteNodeEnable(isEdit);
		manageUI.setUploadFileEnable(isEdit);
	}
}
