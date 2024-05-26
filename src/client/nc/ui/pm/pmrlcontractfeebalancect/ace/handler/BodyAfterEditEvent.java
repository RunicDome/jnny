package nc.ui.pm.pmrlcontractfeebalancect.ace.handler;

import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
import nc.vo.pub.lang.UFDouble;
//多编码结算单表体编辑后事件类
@SuppressWarnings("restriction")
public class BodyAfterEditEvent implements IAppEventHandler<CardBodyAfterEditEvent> {

	@Override
	public void handleAppEvent(CardBodyAfterEditEvent e) {
		// TODO Auto-generated method stub
		BillCardPanel panel = e.getBillCardPanel();
    	int rowCount = panel.getRowCount(); // 表体行数
    	UFDouble sum = UFDouble.ZERO_DBL;// 初始值0求和用
    	// String key = e.getKey();// 当前
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
