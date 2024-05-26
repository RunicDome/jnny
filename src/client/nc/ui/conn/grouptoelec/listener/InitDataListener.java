package nc.ui.conn.grouptoelec.listener;

import nc.bs.framework.common.NCLocator;
import nc.funcnode.ui.FuncletInitData;
import nc.itf.conn.IGrouptoelecMaintain;
import nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener;
import nc.vo.conn.grouptoelec.ConnGroupElecVO;
import nc.vo.pub.BusinessException;

public class InitDataListener extends DefaultFuncNodeInitDataListener {

	@Override
	public void initData(FuncletInitData arg0) {
		// TODO �Զ����ɵķ������
		IGrouptoelecMaintain maintain = NCLocator.getInstance().lookup(IGrouptoelecMaintain.class);
		
		try {
			ConnGroupElecVO[] vos = maintain.query(null);
			arg0.setInitData(vos);
			this.getModel().initModel(vos);
		} catch (BusinessException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		} catch (Exception e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		//super.initData(arg0);
	}

}
