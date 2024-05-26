package nc.itf.pu.m20;

import nc.vo.pub.BusinessException;

// 请购单 推送供应商平台 
public abstract interface IPushSupplier {
	public abstract void pushSupplier(String pk_praybill)
			throws BusinessException;
}
