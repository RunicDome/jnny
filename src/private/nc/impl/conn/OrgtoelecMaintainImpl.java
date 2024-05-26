package nc.impl.conn;

import nc.impl.pub.ace.AceOrgtoelecPubServiceImpl;
import nc.impl.pubapp.pub.smart.BatchSaveAction;
import nc.itf.conn.IOrgtoelecMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.conn.orgtoelec.ConnOrgElecVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.uif2.LoginContext;

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
		BatchOperateVO retData = saveAction.batchSave(batchVO);
		return retData;
	}
}
