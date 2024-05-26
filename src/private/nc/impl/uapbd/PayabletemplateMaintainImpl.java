package nc.impl.uapbd;

import nc.bs.trade.business.HYPubBO;
import nc.impl.pub.ace.AcePayabletemplatePubServiceImpl;
import nc.impl.pubapp.pub.smart.BatchSaveAction;
import nc.itf.uapbd.IPayabletemplateMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.AppContext;
import nc.vo.uap.payabletemplate.PayableTemplate;
import nc.vo.uif2.LoginContext;

public class PayabletemplateMaintainImpl extends AcePayabletemplatePubServiceImpl
		implements IPayabletemplateMaintain {

	@Override
	public PayableTemplate[] queryByDataVisibilitySetting(LoginContext arg0,
			Class<? extends ISuperVO> arg1) throws BusinessException {
		PayableTemplate[] vos = (PayableTemplate[]) super.selectByWhereSql("", PayableTemplate.class);
		return vos;
	}
	
	@Override
	public PayableTemplate[] query(IQueryScheme queryScheme) throws BusinessException {
		return super.pubquerybasedoc(queryScheme);
	}

	@Override
	public BatchOperateVO batchSave(BatchOperateVO batchVO) throws BusinessException {
		BatchSaveAction<PayableTemplate> saveAction = new BatchSaveAction<PayableTemplate>();
		Object[] objects = batchVO.getUpdObjs();
		if(null != objects){
			for(int i=0;i<objects.length;i++){
				PayableTemplate vo = (PayableTemplate) objects[i];
				String pk = vo.getAttributeValue("pk_payabletemplate").toString();
				PayableTemplate oldVO = (PayableTemplate) getHyPubBO().queryByPrimaryKey(
						PayableTemplate.class, pk);
				
				vo.setAttributeValue("ts", oldVO.getTs());
				vo.setAttributeValue("modifier", AppContext.getInstance().getPkUser());
				vo.setAttributeValue("modifiedtime", new UFDateTime());
				objects[i] = vo;
			}
			batchVO.setUpdObjs(objects);
		}
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
