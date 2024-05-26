package nc.impl.uapbd;

import nc.bs.trade.business.HYPubBO;
import nc.impl.pub.ace.AceGrouptoelecPubServiceImpl;
import nc.impl.pubapp.pub.smart.BatchSaveAction;
import nc.vo.bd.meta.BatchOperateVO;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.uif2.LoginContext;
import nc.vo.conn.grouptoelec.ConnGroupElecVO;
import nc.itf.uapbd.IGrouptoelecMaintain;

public class GrouptoelecMaintainImpl extends AceGrouptoelecPubServiceImpl
		implements IGrouptoelecMaintain {

	@Override
	public ConnGroupElecVO[] queryByDataVisibilitySetting(LoginContext arg0,
			Class<? extends ISuperVO> arg1) throws BusinessException {
		ConnGroupElecVO[] vos = (ConnGroupElecVO[]) super.selectByWhereSql("", ConnGroupElecVO.class);
		return vos;
	}


	@Override
	public ConnGroupElecVO[] query(IQueryScheme queryScheme) throws BusinessException {
		return super.pubquerybasedoc(queryScheme);
	}

	@Override
	public BatchOperateVO batchSave(BatchOperateVO batchVO) throws BusinessException {
		BatchSaveAction<ConnGroupElecVO> saveAction = new BatchSaveAction<ConnGroupElecVO>();
		
		Object[] objects = batchVO.getUpdObjs();
		for(int i=0;i<objects.length;i++){
			ConnGroupElecVO vo = (ConnGroupElecVO) objects[i];
			ConnGroupElecVO oldVO = (ConnGroupElecVO) getHyPubBO().queryByPrimaryKey(
					ConnGroupElecVO.class, vo.getAttributeValue("pk_groupelec").toString());
			
			vo.setAttributeValue("ts", oldVO.getTs());
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
