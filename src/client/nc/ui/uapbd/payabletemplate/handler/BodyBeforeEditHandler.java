package nc.ui.uapbd.payabletemplate.handler;

import nc.ui.bd.ref.model.DefdocGridRefModel;
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
//	if("pk_contr".equals(key)){
//		((DefdocGridRefModel)((UIRefPane)panel.getBodyItem("pk_contr").getComponent()).getRefModel()).setPk_org(pk_org);
//	}
	
	e.setReturnValue(Boolean.TRUE);
  }

}
