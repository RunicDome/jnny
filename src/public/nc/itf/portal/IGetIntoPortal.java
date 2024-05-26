package nc.itf.portal;

import nc.vo.ep.bx.BXVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;

// 单据转换后进共享相关
public interface IGetIntoPortal {
	// 单据进共享
	public void changeIntoPortal(String srcBillOrTranstype,
			String destBillOrTranstype, AggregatedValueObject srcBillVO)
			throws BusinessException;

	// 共享单据审核完成后，同步审核转换前单据
	public void approveSrcBill(String bxTransiType,BXVO bxvo)
			throws BusinessException;
}
