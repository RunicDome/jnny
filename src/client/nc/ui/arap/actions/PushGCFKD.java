package nc.ui.arap.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URLEncoder;

import nc.bs.framework.common.NCLocator;
import nc.itf.arap.forthird.IArapForDGSWService;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.IEditor;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.arap.basebill.BaseAggVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import net.sf.json.JSONObject;

@SuppressWarnings("restriction")
public class PushGCFKD extends NCAction {
	private static final long serialVersionUID = 1L;
	private IEditor editor;
	private AbstractAppModel model;

	public PushGCFKD() {
		// TODO 自动生成的方法存根
		setBtnName("影像上传");
		setCode("pushGCFKD");
	}

	protected boolean isActionEnable() {
		// TODO Auto-generated method stub
		if(this.getModel().getSelectedData() == null)
			return false;
		BaseAggVO aggvo =  (BaseAggVO) this.getModel().getSelectedData();
		int approvestatus = (int) aggvo.getParentVO().getAttributeValue("approvestatus");
		// 自由态可点击
		if(approvestatus == -1){
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		// TODO 自动生成的方法存根
		if (this.model.getSelectedData() == null) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("1054002_0", "01054002-0003"));
		} else {
			BaseAggVO aggvo =  (BaseAggVO) this.getEditor().getValue();// AggVO
			if(aggvo == null){
				aggvo = (BaseAggVO) this.getModel().getSelectedData();
			}
			String billid = aggvo.getParentVO().getPrimaryKey();// 单据主键
			String bill_type = (String) aggvo.getParentVO().getAttributeValue("pk_billtype");// 单据类型
			JSONObject json = new JSONObject();
			try {
				IArapForDGSWService util = (IArapForDGSWService) NCLocator.getInstance().lookup(
						IArapForDGSWService.class);
				json = util.genSWUrl(bill_type,billid);
			} catch (Exception e) {
				json.put("success", "N");
				json.put("errinfo", e.getMessage());
			}
			if(!"Y".equals(json.getString("success"))){
				MessageDialog.showErrorDlg(null, "提示", "调用税务系统出错："+json.getString("errinfo"));
			}else{
				String openurl = json.getString("url") + URLEncoder.encode(json.getString("deurl"), "utf-8");
				openYXUpload(openurl);
			}
		}
	}
	
	//打开影像上传页面
	public void openYXUpload(String url) throws IOException{
		Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);// 使用默认浏览器打开url
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
}
