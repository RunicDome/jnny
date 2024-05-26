package nc.pubitf.pu.m422x.ic.z3;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pu.m422x.entity.StoreReqAppVO;
import nc.vo.pub.BusinessException;

public abstract interface IQuery422xForZ3
{
  public abstract StoreReqAppVO[] queryStoreReqApps(IQueryScheme paramIQueryScheme)
    throws BusinessException;
}