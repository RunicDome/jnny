package nc.ui.workflow.admin.action;

import java.awt.event.ActionEvent;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.model.AbstractAppModel;

@SuppressWarnings("restriction")
public class DeleteAction extends NCAction {
	private static final long serialVersionUID = 1L;
	protected AbstractAppModel model;

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
	}

	public DeleteAction() {
		this.setCode("scdj");
		this.setBtnName("删除单据");
	}

	public void doAction(ActionEvent e) throws Exception {
		System.out.println("111");
	}
}
