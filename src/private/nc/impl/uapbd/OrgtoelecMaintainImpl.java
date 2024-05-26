package nc.impl.uapbd;

import nc.bs.trade.business.HYPubBO;
import nc.impl.pub.ace.AceOrgtoelecPubServiceImpl;
import nc.impl.pubapp.pub.smart.BatchSaveAction;
import nc.vo.bd.meta.BatchOperateVO;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.uif2.LoginContext;
import nc.vo.conn.orgtoelec.ConnOrgElecVO;
import nc.itf.uapbd.IOrgtoelecMaintain;

public class OrgtoelecMaintainImpl extends AceOrgtoelecPubServiceImpl
		implements IOrgtoelecMaintain {

	@Override
	public ConnOrgElecVO[] queryByDataVisibilitySetting(LoginContext arg0,
			Class<? extends ISuperVO> arg1) throws BusinessException {
		ConnOrgElecVO[] vos = (ConnOrgElecVO[]) super.selectByWhereSql("", ConnOrgElecVO.class);
		return vos;
	}
	
	@Override
	public ConnOrgElecVO[] query(IQueryScheme queryScheme) throws BusinessException {
		return super.pubquerybasedoc(queryScheme);
	}

	@Override
	public BatchOperateVO batchSave(BatchOperateVO batchVO) throws BusinessException {
		BatchSaveAction<ConnOrgElecVO> saveAction = new BatchSaveAction<ConnOrgElecVO>();
		
		Object[] objects = batchVO.getUpdObjs();
		for(int i=0;i<objects.length;i++){
			ConnOrgElecVO vo = (ConnOrgElecVO) objects[i];
			String pk = vo.getAttributeValue("pk_orgtoelec").toString();
			ConnOrgElecVO oldVO = (ConnOrgElecVO) getHyPubBO().queryByPrimaryKey(
					ConnOrgElecVO.class, pk);
			
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
