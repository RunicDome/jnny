package nc.impl.fipub;

import nc.bs.trade.business.HYPubBO;
import nc.impl.pub.ace.AceRlcashmoneyPubServiceImpl;
import nc.impl.pubapp.pub.smart.BatchSaveAction;
import nc.itf.fipub.IRlcashmoneyMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.conn.grouptoelec.ConnGroupElecVO;
import nc.vo.fipub.rlcashmoney.CashMoneyVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.uif2.LoginContext;

public class RlcashmoneyMaintainImpl extends AceRlcashmoneyPubServiceImpl
		implements IRlcashmoneyMaintain {

	@Override
	public CashMoneyVO[] queryByDataVisibilitySetting(LoginContext arg0,
			Class<? extends ISuperVO> arg1) throws BusinessException {
		CashMoneyVO[] vos = (CashMoneyVO[]) super.selectByWhereSql("", CashMoneyVO.class);
		return vos;
	}
	
	@Override
	public CashMoneyVO[] query(IQueryScheme queryScheme) throws BusinessException {
		return super.pubquerybasedoc(queryScheme);
	}

	@Override
	public BatchOperateVO batchSave(BatchOperateVO batchVO) throws BusinessException {
		BatchSaveAction<CashMoneyVO> saveAction = new BatchSaveAction<CashMoneyVO>();
		
		Object[] objects = batchVO.getUpdObjs();
		for(int i=0;i<objects.length;i++){
			CashMoneyVO vo = (CashMoneyVO) objects[i];
			CashMoneyVO oldVO = (CashMoneyVO) getHyPubBO().queryByPrimaryKey(
					CashMoneyVO.class, vo.getAttributeValue("pk_cashmoney").toString());
			
			vo.setAttributeValue("ts", oldVO.getAttributeValue("ts"));
			objects[i] = vo;
		}
		batchVO.setUpdObjs(objects);
		
		BatchOperateVO retData = saveAction.batchSave(batchVO);
		return retData;
	}
	
	private HYPubBO hyPubBO;
	
	public HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	public void setHyPubBO(HYPubBO hyPubBO) {
		this.hyPubBO = hyPubBO;
	}
}
