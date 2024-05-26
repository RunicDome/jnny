package nc.ui.uapbd.usertoelec.handler;

import nc.ui.bd.ref.model.PsndocDefaultRefModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent;

public class BodyBeforeEditHandler implements IAppEventHandler<CardBodyBeforeEditEvent> {

  @Override
  public void handleAppEvent(CardBodyBeforeEditEvent e) {
    // TODO Auto-generated method stub
    String key = e.getKey();
 	BillCardPanel panel = e.getBillCardPanel();
 	
 	String pk_org = (String) panel.getBodyItem("pk_org").getValueObject();
 	
 	if(null == pk_org || pk_org.equals("")){
 		e.setReturnValue(Boolean.TRUE);
 	}
	if("pk_user".equals(key)){
		//((PsndocDefaultRefModel)(PsndocDefaultRefModel)((UIRefPane)panel.getBodyItem("pk_user").getComponent()).getRefModel()).setMutiGroup(true);
//		panel.getBodyItem("pk_user").setPk_org("0001A21000000001ZBF5");
		((PsndocDefaultRefModel)(PsndocDefaultRefModel)((UIRefPane)panel.getBodyItem("pk_user").getComponent()).getRefModel()).setPk_org(pk_org);
	}
	
	e.setReturnValue(Boolean.TRUE);
  }

}
