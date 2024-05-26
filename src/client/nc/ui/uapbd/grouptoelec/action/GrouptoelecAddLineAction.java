package nc.ui.uapbd.grouptoelec.action;

import nc.ui.pubapp.uif2app.actions.batch.BatchAddLineAction;
import nc.vo.conn.grouptoelec.ConnGroupElecVO;
/**
  batch addLine or insLine action autogen
*/
public class GrouptoelecAddLineAction extends BatchAddLineAction {

	private static final long serialVersionUID = 1L;

	@Override
	protected void setDefaultData(Object obj) {
		super.setDefaultData(obj);
		ConnGroupElecVO singleDocVO = (ConnGroupElecVO) obj;
//		singleDocVO.setPk_group(this.getModel().getContext().getPk_group());
//		singleDocVO.setPk_org(this.getModel().getContext().getPk_org());
	}

}