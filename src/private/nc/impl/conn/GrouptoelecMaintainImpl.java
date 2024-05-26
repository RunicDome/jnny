package nc.impl.conn;

import nc.impl.pub.ace.AceGrouptoelecPubServiceImpl;
import nc.impl.pubapp.pub.smart.BatchSaveAction;
import nc.vo.bd.meta.BatchOperateVO;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.uif2.LoginContext;
import nc.vo.conn.grouptoelec.ConnGroupElecVO;
import nc.itf.conn.IGrouptoelecMaintain;

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
		BatchOperateVO retData = saveAction.batchSave(batchVO);
		return retData;
	}
}
