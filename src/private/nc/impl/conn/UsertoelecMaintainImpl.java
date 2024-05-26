package nc.impl.conn;

import nc.impl.pub.ace.AceUsertoelecPubServiceImpl;
import nc.impl.pubapp.pub.smart.BatchSaveAction;
import nc.itf.conn.IUsertoelecMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.conn.usertoelec.ConnUserElecVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.uif2.LoginContext;

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
		BatchOperateVO retData = saveAction.batchSave(batchVO);
		return retData;
	}
}
