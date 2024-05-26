package nc.ui.uapbd.usertoelec.action;

import nc.ui.pubapp.uif2app.actions.batch.BatchAddLineAction;
import nc.vo.conn.usertoelec.ConnUserElecVO;
/**
  batch addLine or insLine action autogen
*/
public class UsertoelecAddLineAction extends BatchAddLineAction {

	private static final long serialVersionUID = 1L;

	@Override
	protected void setDefaultData(Object obj) {
		super.setDefaultData(obj);
		ConnUserElecVO singleDocVO = (ConnUserElecVO) obj;
//		singleDocVO.setPk_group(this.getModel().getContext().getPk_group());
//		singleDocVO.setPk_org(this.getModel().getContext().getPk_org());
	}

}