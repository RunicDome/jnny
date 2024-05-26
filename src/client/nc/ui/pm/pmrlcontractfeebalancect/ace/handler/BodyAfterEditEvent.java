package nc.ui.pm.pmrlcontractfeebalancect.ace.handler;

import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
import nc.vo.pub.lang.UFDouble;
//�������㵥����༭���¼���
@SuppressWarnings("restriction")
public class BodyAfterEditEvent implements IAppEventHandler<CardBodyAfterEditEvent> {

	@Override
	public void handleAppEvent(CardBodyAfterEditEvent e) {
		// TODO Auto-generated method stub
		BillCardPanel panel = e.getBillCardPanel();
    	int rowCount = panel.getRowCount(); // ��������
    	UFDouble sum = UFDouble.ZERO_DBL;// ��ʼֵ0�����
    	// String key = e.getKey();// ��ǰ
    	/*if("examinationvalue".equals(key) || "hdef2".equals(key) || "hdef3".equals(key)	){
    		
    	}*/
    	for (int row = 0; row < rowCount; row++) {
			UFDouble financialentrywrit = (UFDouble) (panel.getBodyValueAt(row, "financialentrywrit") == null?new UFDouble(0.0):panel.getBodyValueAt(row, "financialentrywrit"));
			if(null != financialentrywrit){
				sum = sum.add(financialentrywrit, 2);
			}
		}
		panel.setHeadItem("financialentryadd", sum.toString());
	}

}
