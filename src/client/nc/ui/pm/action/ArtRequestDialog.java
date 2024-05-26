package nc.ui.pm.action;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.pmpub.prv.IEnclosureService;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.component.ButtonPanel;
import nc.ui.pubapp.bill.BillCardPanel;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.ct.purdaily.entity.RlPmeFile;
import nc.vo.pub.BusinessException;

/**
 * 附件拉取dialog
 * 
 * @author chengkh
 * 
 */
@SuppressWarnings({ "serial", "restriction" })
public class ArtRequestDialog extends UIDialog implements ActionListener,
		BillEditListener2 {

	private BillCardPanel billCardPanel;
	private AbstractUIAppModel model;
	private UIPanel contentPanel = null;
	private ButtonPanel buttonPanel;
	private RlPmeFile[] bvos;

	@SuppressWarnings("deprecation")
	public ArtRequestDialog(AbstractUIAppModel model, RlPmeFile[] rlpmeFile) {
		this.model = model;
		this.setBvos(rlpmeFile);
		initUI();
	}

	private void initUI() {
		
		setTitle("附件拉取");
		setLayout(new BorderLayout());
		this.getContentPane().add(getContentPanel(), BorderLayout.CENTER);
		this.getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
		setSize(1280, 600);
		setLocation(400, 200);
		initListener();
	}

	private UIPanel getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new UIPanel(new BorderLayout());
			contentPanel.add(getBillCardPanel(), BorderLayout.CENTER);
		}
		billCardPanel.getBillModel().loadLoadRelationItemValue();
		return billCardPanel;
	}

	private BillCardPanel getBillCardPanel() {
		if (billCardPanel == null) {
			billCardPanel = new BillCardPanel();
			billCardPanel.loadTemplet("1001ZZ10000000107Y52");
		}
		billCardPanel.getBillModel().setBodyDataVO(getBvos());
		billCardPanel.getBillModel().loadLoadRelationItemValue();
		return billCardPanel;
	}

	/**
	 * 初始化监听
	 */
	private void initListener() {
		getButtonPanel().getBtnOK().addActionListener(this);
		getButtonPanel().getBtnCancel().addActionListener(this);
	}

	@Override
	public void closeOK() {
		RlPmeFile[] artRequestVOs = (RlPmeFile[]) billCardPanel.getBillModel()
				.getBodyValueVOs(RlPmeFile.class.getName());
		System.out.println(artRequestVOs);
		List<RlPmeFile> rlPmeFileS = new ArrayList<RlPmeFile>();
		for(RlPmeFile temp : artRequestVOs){
			if(temp.getSelect().booleanValue()){
				rlPmeFileS.add(temp);
			}
		}
		IEnclosureService service = NCLocator.getInstance().lookup(IEnclosureService.class);
		try {
			service.saveEnclosure(getModel().getSelectedData(), rlPmeFileS);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		MessageDialog.showWarningDlg(billCardPanel, "提示", "保存成功！");
		super.closeOK();
	}

	@Override
	public boolean beforeEdit(BillEditEvent arg0) {
		return false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == getButtonPanel().getBtnOK()) {
			closeOK();
		} else if (e.getSource() == getButtonPanel().getBtnCancel()) {
			closeCancel();
		}

	}

	public AbstractUIAppModel getModel() {
		return model;
	}

	public void setModel(AbstractUIAppModel model) {
		this.model = model;
	}

	public void setBillCardPanel(BillCardPanel billCardPanel) {
		this.billCardPanel = billCardPanel;
	}

	public void setContentPanel(UIPanel contentPanel) {
		this.contentPanel = contentPanel;
	}

	@Override
	public int showModal() {
		return super.showModal();
	}

	public ButtonPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new ButtonPanel();
		}
		return buttonPanel;
	}

	public RlPmeFile[] getBvos() {
		return bvos;
	}

	public void setBvos(RlPmeFile[] bvos) {
		this.bvos = bvos;
	}

}
