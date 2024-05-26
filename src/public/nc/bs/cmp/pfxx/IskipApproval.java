package nc.bs.cmp.pfxx;

import java.util.HashMap;

import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.workflownote.WorkflownoteVO;

public interface IskipApproval {
	//使用oa审批账号逃过审批
	public void skipApproval(String adopter,String action,String type,AggregatedValueObject aggVo,HashMap hmPfExParams,Object o,HashMap eparam)throws BusinessException;
}
