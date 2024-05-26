package nc.itf.arap.gathering;

import nc.vo.pub.BusinessException;

/**
 * 收款单退回接口
 * 服务于众齐系统
 * @author jor
 *
 */
public abstract interface IArapGatheringBillServiceForZQ
{
  public void rollAggReceivableBillVO(String pk)
    throws BusinessException;
  
  public void rollBackAggGatheringBillVO(String pk)
		  throws BusinessException;
}