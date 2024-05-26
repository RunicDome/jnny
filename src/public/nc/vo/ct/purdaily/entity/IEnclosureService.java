package nc.vo.ct.purdaily.entity;

import java.util.List;

import nc.vo.pub.BusinessException;

public abstract interface IEnclosureService
{
  public abstract void saveEnclosure(Object obj, List<RlPmeFile> rlPmeFileS)
    throws BusinessException;
}