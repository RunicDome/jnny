package nc.itf.arap.ysyf;

import nc.vo.pub.BusinessException;

/**
 * 应收ARAP模块退回接口
 * 服务于生产系统
 * @author xbx
 */
public abstract interface IArapServiceForSC {

	public String rollAggBillVO(String billid, String reason) throws BusinessException;

}
