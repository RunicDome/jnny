package nc.bs.cmp.pfxx;

import java.util.HashMap;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
import nc.itf.uap.pf.IWorkflowMachine;
import nc.itf.uap.pf.IplatFormEntry;
import nc.uif.pub.exception.UifException;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.workflownote.WorkflownoteVO;

public class skipApprovalImpl  implements IskipApproval{

	@Override
	public void skipApproval(String adopter,String action, String type,
			AggregatedValueObject aggVo, HashMap hmPfExParams,
			 Object o, HashMap eparam) throws BusinessException {
		//审批单据
		InvocationInfoProxy.getInstance().setUserId(adopter);
			WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
					.getInstance().lookup(IWorkflowMachine.class))
					.checkWorkFlow(action, type, aggVo, hmPfExParams);
			
			if (worknoteVO != null) {
				worknoteVO.setChecknote("批准");
				worknoteVO.setApproveresult("Y");
			}
			getIplatFormEntry().processAction(action, type,
					worknoteVO, aggVo, o, eparam);

		
	}

	 private IplatFormEntry getIplatFormEntry() {
			return NCLocator.getInstance().lookup(IplatFormEntry.class);
		}
}
