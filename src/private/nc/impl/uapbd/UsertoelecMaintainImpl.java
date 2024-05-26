package nc.impl.uapbd;

import nc.bs.trade.business.HYPubBO;
import nc.impl.pub.ace.AceUsertoelecPubServiceImpl;
import nc.impl.pubapp.pub.smart.BatchSaveAction;
import nc.vo.bd.meta.BatchOperateVO;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.uif2.LoginContext;
import nc.vo.conn.usertoelec.ConnUserElecVO;
import nc.itf.uapbd.IUsertoelecMaintain;

public class UsertoelecMaintainImpl extends AceUsertoelecPubServiceImpl
		implements IUsertoelecMaintain {

	@Override
	public ConnUserElecVO[] queryByDataVisibilitySetting(LoginContext arg0,
			Class<? extends ISuperVO> arg1) throws BusinessException {
		ConnUserElecVO[] vos = (ConnUserElecVO[]) super.selectByWhereSql("", ConnUserElecVO.class);
		return vos;
	}
	
	@Override
	public ConnUserElecVO[] query(IQueryScheme queryScheme) throws BusinessException {
		return super.pubquerybasedoc(queryScheme);
	}

	@Override
	public BatchOperateVO batchSave(BatchOperateVO batchVO) throws BusinessException {
		BatchSaveAction<ConnUserElecVO> saveAction = new BatchSaveAction<ConnUserElecVO>();
		
		Object[] objects = batchVO.getUpdObjs();
		for(int i=0;i<objects.length;i++){
			ConnUserElecVO vo = (ConnUserElecVO) objects[i];
			ConnUserElecVO oldVO = (ConnUserElecVO) getHyPubBO().queryByPrimaryKey(
					ConnUserElecVO.class, vo.getAttributeValue("pk_usertoelec").toString());
			
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
