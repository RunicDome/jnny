package nc.pubitf.pmr.pm;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pub.BusinessException;

public abstract interface IQuery4Z02For4Z01
{
  public abstract AggPmFeebalance[] queryStoreReqApps(IQueryScheme paramIQueryScheme)
    throws BusinessException;
}