package nc.bs.pub.action;

import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.data.param.CommonParam;
import nc.itf.arap.forthird.IArapForDGSWService;
import nc.vo.arap.pub.ArapConstant;
import nc.vo.arap.receivable.ReceivableBillVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.ws.intf.OaWorkFlowUtil;
import net.sf.json.JSONObject;

// 应收单收回
public class N_F0_RECALL extends N_F0_EDIT {
	public N_F0_RECALL() {
	}

	public Object runComClass(PfParameterVO pfparametervo)
			throws BusinessException {
		procRecallFlow(pfparametervo);
		Object o = super.runComClass(pfparametervo);
		InvocationInfoProxy.getInstance().setProperty("reids_event", "true");
		EventDispatcher.fireEvent(new BusinessEvent(
				ArapConstant.ARAP_MDID_PAYABLEBILL, "200615", o));
		Logger.error("公布业务事件 201615 通知redis更新缓存");
		// 判断是否允许收回
		for (AggregatedValueObject temp : pfparametervo.m_preValueVos) {
			OaWorkFlowUtil.backOaWorkFlow(temp.getParentVO().getPrimaryKey());
			// 调用东港税务系统同步单据状态
			IArapForDGSWService util = (IArapForDGSWService) NCLocator.getInstance().lookup(
					IArapForDGSWService.class);
			ReceivableBillVO hvo = (ReceivableBillVO) temp.getParentVO();
			// 单据号，组织主键，事件类型，单据类型
			JSONObject res = util.sendBillByNCBill(hvo.getPrimaryKey(),
					hvo.getPk_org(), CommonParam.RECALL, hvo.getPk_billtype());
			if(!"Y".equals(res.getString("success"))){
				throw new BusinessException(res.getString("errinfo"));
			}
		}
		return o;
	}
}
