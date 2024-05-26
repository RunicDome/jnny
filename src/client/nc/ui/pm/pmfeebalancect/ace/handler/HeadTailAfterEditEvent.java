package nc.ui.pm.pmfeebalancect.ace.handler;

import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler; 
import nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent; 
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.hi.psndoc.PsnJobVO;
//��ͷ�༭���¼���  �༭ǩԼ�˴���ǩԼ�����ֶ�(δ���)
@SuppressWarnings("restriction")
public class HeadTailAfterEditEvent implements IAppEventHandler<CardHeadTailAfterEditEvent> {       
    
	@Override       
    public void handleAppEvent(CardHeadTailAfterEditEvent e) {    
		String key = e.getKey();
     	BillCardPanel panel = e.getBillCardPanel();
		//�༭ǩԼ�˴���ǩԼ�����ֶ�
		if("pk_signer_name".equals(key)){
			//��ȡ����ǩԼ�˲���pkֵ
			String pk_signer_name = (String) e.getValue();
			//��װ��ѯ
			String sql = "principal in (SELECT  pk_psndoc FROM bd_psndoc p where p.pk_psndoc='"+pk_signer_name+"')";
			//PsnJobVO
			HYPubBO_Client service = new HYPubBO_Client();
			try {
				//��ѯ��������¼����Ϣ
				//�������
				PsnJobVO[] psnjobvos =  (PsnJobVO[]) service.queryByCondition(PsnJobVO.class, sql);
				//������¼��ȡ�ò���pk
				String pk_qydept = psnjobvos[0].getPk_dept();

				panel.setHeadItem(pk_qydept, "pk_qydept");
			} catch (UifException e1) {
				e1.printStackTrace();
			}

		}
    } 
}
