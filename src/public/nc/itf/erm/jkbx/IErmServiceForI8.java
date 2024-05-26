package nc.itf.erm.jkbx;

import nc.vo.pub.BusinessException;

/**
 * 应收ERM模块退回接口
 * 服务于I8系统
 * @author xbx
 *
 */
public abstract interface IErmServiceForI8
{
  public String rollAggBillVO(String pk,String billtype,String reason)
    throws BusinessException;
}