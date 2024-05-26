package nc.ws.intf;

import nc.vo.pub.BusinessException;


public interface IOaWorkFlowService {

	public String oaCallBack(String type, Integer requestid, String pk_org) throws BusinessException;
}
