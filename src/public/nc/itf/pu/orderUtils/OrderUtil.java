package nc.itf.pu.orderUtils;

import nc.vo.pu.m20.entity.PraybillVO;
import nc.vo.pub.BusinessException;

public interface OrderUtil {
	// 请购单生成物资需求申请单，参数请购单主键
	public String orderToStoreReqApp(PraybillVO praybillVO) throws BusinessException;
}
