package nc.ui.cmp.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URLEncoder;

import nc.bs.framework.common.NCLocator;
import nc.itf.arap.forthird.IArapForDGSWService;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.IEditor;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import net.sf.json.JSONObject;

public class SWImageUpload extends NCAction {

	private static final long serialVersionUID = 1L;
	private IEditor editor;
	private AbstractAppModel model;

	public SWImageUpload() {
		// TODO 自动生成的方法存根
		setBtnName("影像上传");
		setCode("SWImageUpload");
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		if (this.model.getSelectedData() == null) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("1054002_0", "01054002-0003"));
		} else {
			AbstractBill AggVO = (AbstractBill) this.getModel()
					.getSelectedData();
			if (AggVO == null) {
				AggVO = (AbstractBill) this.getModel().getSelectedData();
			}
			String billid = AggVO.getParentVO().getPrimaryKey();// 单据主键
			String bill_type = "";// 单据类型
			if (AggVO.getParentVO().getAttributeValue("bill_type") != null) {
				// 付款结算
				bill_type = AggVO.getParentVO().getAttributeValue("bill_type")
						.toString();
			} else {
				// 付款申请
				bill_type = "36D1";
			}
			JSONObject json = new JSONObject();
			try {
				IArapForDGSWService util = (IArapForDGSWService) NCLocator
						.getInstance().lookup(IArapForDGSWService.class);
				json = util.genSWUrl(bill_type, billid);
			} catch (Exception e) {
				json.put("success", "N");
				json.put("errinfo", e.getMessage());
			}
			if (!"Y".equals(json.getString("success"))) {
				MessageDialog.showErrorDlg(null, "提示",
						"调用税务系统出错：" + json.getString("errinfo"));
			} else {
				String openurl = json.getString("url")
						+ URLEncoder.encode(json.getString("deurl"), "utf-8");
				openYXUpload(openurl);
			}
		}
	}

	// 打开影像上传页面
	public void openYXUpload(String url) throws IOException {
		Runtime.getRuntime()
				.exec("rundll32 url.dll,FileProtocolHandler " + url);// 使用默认浏览器打开url
	}

	public IEditor getEditor() {
		return editor;
	}

	public void setEditor(IEditor editor) {
		this.editor = editor;
	}

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	@Override
	protected boolean isActionEnable() {
		// TODO Auto-generated method stub
		if (this.getModel().getSelectedData() == null)
			return false;
		AbstractBill AggVO = (AbstractBill) this.getModel().getSelectedData();
		int bill_status = 0;
		if (AggVO.getParentVO().getAttributeValue("bill_status") != null) {
			bill_status = Integer.parseInt(AggVO.getParentVO()
					.getAttributeValue("bill_status").toString());
			if (bill_status == -99) {
				return true;
			}
		} else if (AggVO.getParentVO().getAttributeValue("vbillstatus") != null) {
			bill_status = Integer.parseInt(AggVO.getParentVO()
					.getAttributeValue("vbillstatus").toString());
		} else if (AggVO.getParentVO().getAttributeValue("busistatus") != null) {
			bill_status = Integer.parseInt(AggVO.getParentVO()
					.getAttributeValue("busistatus").toString());
		}
		// 自由态可点击
		if (bill_status == -1 || bill_status == -10) {
			return true;
		} else {
			return false;
		}
	}
}
