package nc.ui.uapbd.payabletemplate.action;

import nc.ui.pubapp.uif2app.actions.batch.BatchAddLineAction;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.uap.payabletemplate.PayableTemplate;
/**
  batch addLine or insLine action autogen
*/
public class PayabletemplateAddLineActiona extends BatchAddLineAction {

	private static final long serialVersionUID = 1L;

	@Override
	protected void setDefaultData(Object obj) {
		super.setDefaultData(obj);
		PayableTemplate singleDocVO = (PayableTemplate) obj;
		singleDocVO.setAttributeValue("creator", this.getModel().getContext().getPk_loginUser());
		singleDocVO.setAttributeValue("creationtime", new UFDateTime());
	}

}