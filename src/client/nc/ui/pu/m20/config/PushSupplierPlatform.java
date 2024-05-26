package nc.ui.pu.m20.config;

import java.awt.Container;
import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.itf.pu.m20.IPushSupplier;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.IEditor;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.pu.m20.entity.PraybillVO;

//请购单推送供应商平台
public class PushSupplierPlatform extends NCAction {
	private static final long serialVersionUID = 1L;
	private AbstractAppModel model;
	private IEditor editor;

	public PushSupplierPlatform() {
		super.setBtnName("推送供应商平台");
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		PraybillVO qgAggvo = (PraybillVO) getModel().getSelectedData();// AggVO
		IPushSupplier itf = NCLocator.getInstance().lookup(IPushSupplier.class);
		// 推送供应商平台
		itf.pushSupplier(qgAggvo.getPrimaryKey());
		MessageDialog.showHintDlg((Container) editor, "提示", "操作成功！");
	}

	public AbstractAppModel getModel() {
		return this.model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	public IEditor getEditor() {
		return this.editor;
	}

	public void setEditor(IEditor editor) {
		this.editor = editor;
	}

	protected boolean isActionEnable() {
		PraybillVO vo = (PraybillVO) getModel().getSelectedData();// 档案信息
		if (null == vo) {
			return false;
		}
		return true;
	}

}
