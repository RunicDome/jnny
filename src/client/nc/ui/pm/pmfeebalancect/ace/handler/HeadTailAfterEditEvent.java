package nc.ui.pm.pmfeebalancect.ace.handler;

import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler; 
import nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent; 
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.hi.psndoc.PsnJobVO;
//表头编辑后事件类  编辑签约人带出签约部门字段(未完成)
@SuppressWarnings("restriction")
public class HeadTailAfterEditEvent implements IAppEventHandler<CardHeadTailAfterEditEvent> {       
    
	@Override       
    public void handleAppEvent(CardHeadTailAfterEditEvent e) {    
		String key = e.getKey();
     	BillCardPanel panel = e.getBillCardPanel();
		//编辑签约人带出签约部门字段
		if("pk_signer_name".equals(key)){
			//获取输入签约人参照pk值
			String pk_signer_name = (String) e.getValue();
			//组装查询
			String sql = "principal in (SELECT  pk_psndoc FROM bd_psndoc p where p.pk_psndoc='"+pk_signer_name+"')";
			//PsnJobVO
			HYPubBO_Client service = new HYPubBO_Client();
			try {
				//查询出工作记录表信息
				//这里出错
				PsnJobVO[] psnjobvos =  (PsnJobVO[]) service.queryByCondition(PsnJobVO.class, sql);
				//工作记录表取得部门pk
				String pk_qydept = psnjobvos[0].getPk_dept();

				panel.setHeadItem(pk_qydept, "pk_qydept");
			} catch (UifException e1) {
				e1.printStackTrace();
			}

		}
    } 
}
