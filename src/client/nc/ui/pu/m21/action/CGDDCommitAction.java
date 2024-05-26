/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package nc.ui.pu.m21.action;

import java.awt.event.ActionEvent;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.oa.web.getCGDDVO;
import nc.ui.pcm.utils.GetDao;
import nc.vo.pu.m21.entity.OrderHeaderVO;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
//import nc.vo.cmp.netpay.PaymentVO;
//import nc.vo.pu.m21.entity.OrderHeaderVO;

public class CGDDCommitAction extends SendApproveAction {
	public void doAction(ActionEvent e) throws Exception {
		System.out.println("进入采购订单！");
		super.doAction(e);
		AbstractBill billVO = (AbstractBill) this.getModel().getSelectedData();
		OrderHeaderVO headVO = (OrderHeaderVO) billVO.getParentVO();
		String pk_org = headVO.getPk_org();
		String fg = "0";
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		String querySql = "SELECT NAME FROM RL_CBSNAME WHERE TYPE = 'SSZZ'";
		List<Object[]> resultList = getDao.query(querySql);
		if (resultList.size() > 0) {
			for (int i = 0; i < resultList.size(); i++) {
				Object[] item = resultList.get(i);
				if(pk_org.equals(item[0])){
					fg = "1";
				}
			}
		}
		if("1".equals(fg)){
//			getCGDDVO.getCGDDVOMsg(billVO);
		}
		//getCGDDVO.getCGDDVOMsg(billVO);
	}

	private static final long serialVersionUID = 1L;
}
