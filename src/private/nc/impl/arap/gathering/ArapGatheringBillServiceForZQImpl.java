package nc.impl.arap.gathering;

import java.util.HashMap;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
import nc.itf.arap.gathering.IArapGatheringBillServiceForZQ;
import nc.itf.uap.pf.IplatFormEntry;
import nc.uif.pub.exception.UifException;
import nc.vo.arap.gathering.AggGatheringBillVO;
import nc.vo.arap.gathering.GatheringBillItemVO;
import nc.vo.arap.gathering.GatheringBillVO;
import nc.vo.arap.receivable.AggReceivableBillVO;
import nc.vo.arap.receivable.ReceivableBillItemVO;
import nc.vo.arap.receivable.ReceivableBillVO;
import nc.vo.pub.BusinessException;
import nc.ws.intf.HttpClient;
import net.sf.json.JSONObject;

public class ArapGatheringBillServiceForZQImpl implements
		IArapGatheringBillServiceForZQ {

	@Override
	public void rollAggReceivableBillVO(String pk)
			throws BusinessException {
		HYPubBO hyPubBO = new HYPubBO();
		String[] userObj = {AggReceivableBillVO.class.getName(), ReceivableBillVO.class.getName(), ReceivableBillItemVO.class.getName()};
		AggReceivableBillVO aggVO = (AggReceivableBillVO) hyPubBO.queryBillVOByPrimaryKey(userObj, pk);
		AggReceivableBillVO[] aggVOs = {aggVO};
		if(null != aggVO.getHeadVO().getDef8()){
			sendRollBack(aggVO.getHeadVO().getDef8(), null != aggVO.getHeadVO().getDef66() ? aggVO.getHeadVO().getDef66() : "" );
		}else{
			throw new BusinessException("众齐单据主键为空");
		}
		IplatFormEntry ipf = NCLocator.getInstance().lookup(IplatFormEntry.class);
		ipf.processBatch("DELETE", "D0", null, aggVOs, null, null);
	}

	private void sendRollBack(String billId, String message) throws UifException,
			BusinessException {
		String url = (String) new HYPubBO().findColValue("sys_config", "config_value", "config_key='zq-url'");
		String zoomkeyAuthToken = (String) new HYPubBO().findColValue("sys_config", "config_value", "config_key='zq-token'");
		Map<String, String> params = new HashMap<>();
		params.put("billId", billId);
		params.put("commitStatus", "5");
		params.put("refuseReason", message);
		Map<String, String> headers = new HashMap<>();
		headers.put("Zoomkey-Auth-Token", zoomkeyAuthToken);
		String back = HttpClient.httpPostForm(url, params, headers, "utf-8");
		JSONObject res = JSONObject.fromObject(back);
		String success = res.getString("success");
		if(!"true".equals(success)){
			String info = res.getString("info");
			throw new BusinessException("回写众齐单据状态系统出错:" + info);
		}
	}

	@Override
	public void rollBackAggGatheringBillVO(String pk)
			throws BusinessException {
		HYPubBO hyPubBO = new HYPubBO();
		String[] userObj = {AggGatheringBillVO.class.getName(), GatheringBillVO.class.getName(), GatheringBillItemVO.class.getName()};
		AggGatheringBillVO  aggVO = (AggGatheringBillVO) hyPubBO.queryBillVOByPrimaryKey(userObj, pk);
		if(null != aggVO.getHeadVO().getDef8()){
			sendRollBack(aggVO.getHeadVO().getDef8(), null != aggVO.getHeadVO().getDef66() ? aggVO.getHeadVO().getDef66() : "" );
		}else{
			throw new BusinessException("众齐单据主键为空");
		}
		AggGatheringBillVO[] aggVOs = {aggVO};
		IplatFormEntry ipf = NCLocator.getInstance().lookup(IplatFormEntry.class);
		ipf.processBatch("DELETE", "F2", null, aggVOs, null, null);
	}
}
