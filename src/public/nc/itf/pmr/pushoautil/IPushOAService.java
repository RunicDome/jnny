package nc.itf.pmr.pushoautil;

import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;

public abstract interface IPushOAService
{
  public abstract String fkhtpushoa(AggregatedValueObject paramAggregatedValueObject)
    throws BusinessException;
}