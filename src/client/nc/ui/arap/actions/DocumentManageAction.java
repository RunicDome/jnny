package nc.ui.arap.actions;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import nc.bs.framework.common.NCLocator;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.jdbc.framework.generator.IdGenerator;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.filesystem.FileManageUI;
import nc.ui.pub.filesystem.FileManageUIFactory;
import nc.ui.pubapp.uif2app.actions.FileDocManageAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.editor.IEditor;
import nc.ui.uif2.model.AbstractAppModel;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.utils.fipub.FIFileManageConst;
import nc.vo.arap.basebill.BaseAggVO;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.bd.meta.IBDObject;
import nc.vo.cmp.util.StringUtils;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.sm.UserVO;
import nc.vo.uif2.LoginContext;

// XBX运行审批中或完成的附件进行删除
@SuppressWarnings({ "restriction", "unused" })
public class DocumentManageAction extends FileDocManageAction {
	private static final long serialVersionUID = 1L;
	private IEditor editor;

	public DocumentManageAction() {
		setBtnName(NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0",
				"02006pub-0072"));
	}

	public void doAction(ActionEvent e) throws Exception {
		Object selectedData = null;
		if (((BillForm) getEditor()).isShowing()) {
			selectedData = ((BillForm) this.editor).getValue() == null ? ((BillForm) this.editor)
					.getModel().getSelectedData() : ((BillForm) this.editor)
					.getValue();
		} else {
			selectedData = getModel().getSelectedData();
		}

		setPkField(selectedData);

		IBDObject target = createBDObject(selectedData);
		if (target != null) {
			String rootPath = (String) target.getId();
			String userCode = WorkbenchEnvironment.getInstance().getLoginUser()
					.getUser_code();

			String funcode = getModel().getContext().getNodeCode();
			String pk_group = getModel().getContext().getPk_group();
			FileManageUI ui = FileManageUIFactory.getFileManagePnl(rootPath,
					userCode, funcode, pk_group);

			ui.setTreeRootVisible(false);
			// 默认为false ，修改为true
			// boolean isEdit = true;
			boolean isEdit = false;
			if ((getModel().getUiState() == UIState.EDIT)
					|| (getModel().getUiState() == UIState.ADD)) {
				isEdit = true;
			}
			updateButton(ui, isEdit);
			UIDialog dlg = new UIDialog(
					getModel().getContext().getEntranceUI(), NCLangRes4VoTransl
							.getNCLangRes().getStrByID("pubapp_0",
									"0pubapp-0129"));
			dlg.getContentPane().setLayout(new BorderLayout());
			dlg.getContentPane().add(ui, "Center");
			dlg.setResizable(true);
			dlg.setReset(true);
			dlg.setSize(FIFileManageConst.FIFILEMANAGEDLGDIMENSION);
			dlg.showModal();
		}
		ShowStatusBarMsgUtil.showStatusBarMsg(
				NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0",
						"02006pub-0011", null, new String[] { getBtnName() }),
				getModel().getContext());
	}

	private void setPkField(Object selectedData) {
		BaseBillVO parentVO = (BaseBillVO) ((BaseAggVO) selectedData)
				.getParentVO();
		String primaryKey = parentVO.getPrimaryKey();
		String pkFieldName = parentVO.getPKFieldName();
		if (StringUtils.isEmpty(primaryKey)) {
			String oid = getOID();
			parentVO.setPrimaryKey(oid);
			BillItem pkItem = ((BillForm) getEditor()).getBillCardPanel()
					.getHeadItem(pkFieldName);
			pkItem.setValue(oid);
		}
	}

	protected boolean isActionEnable() {
		boolean ret = (getModel().getSelectedData() != null)
				|| (getModel().getUiState() == UIState.EDIT)
				|| (getModel().getUiState() == UIState.ADD);
		return ret;
		// return Boolean.FALSE;
	}

	private void updateButton(FileManageUI manageUI, boolean isEdit) {
		manageUI.setCreateNewFolderEnable(isEdit);
		manageUI.setDeleteNodeEnable(isEdit);
		manageUI.setUploadFileEnable(isEdit);
	}

	private String getOID() {
		IdGenerator idGenerator = (IdGenerator) NCLocator.getInstance().lookup(
				IdGenerator.class);
		return idGenerator.generate();
	}

	public IEditor getEditor() {
		return this.editor;
	}

	public void setEditor(IEditor editor) {
		this.editor = editor;
	}
}