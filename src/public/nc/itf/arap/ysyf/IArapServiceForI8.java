package nc.itf.arap.ysyf;

import nc.vo.pub.BusinessException;

/**
 * ARAP模块退回接口
 * 服务于I8系统
 * @author xbx
 *
 */
public abstract interface IArapServiceForI8
{
  public String rollAggBillVO(String pk,String billtype, String reason)
    throws BusinessException;
}