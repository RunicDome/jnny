package nc.itf.pu.m21.act;

import nc.vo.pub.BusinessException;

public interface UnapproveToERM {
	public void unApprove(String pk_order) throws BusinessException;
}
