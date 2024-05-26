package nc.ui.conn.orgtoelec.action;

import nc.ui.pubapp.uif2app.actions.batch.BatchAddLineAction;
import nc.vo.conn.orgtoelec.ConnOrgElecVO;
/**
  batch addLine or insLine action autogen
*/
public class OrgtoelecAddLineAction extends BatchAddLineAction {

	private static final long serialVersionUID = 1L;

	@Override
	protected void setDefaultData(Object obj) {
		super.setDefaultData(obj);
		ConnOrgElecVO singleDocVO = (ConnOrgElecVO) obj;
//		singleDocVO.setPk_group(this.getModel().getContext().getPk_group());
//		singleDocVO.setPk_org(this.getModel().getContext().getPk_org());
	}

}