package nc.impl.pub.ace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.pm.pmrlcontractschedule.ace.bp.AcePmRLContractscheduleApproveBP;
import nc.bs.pm.pmrlcontractschedule.ace.bp.AcePmRLContractscheduleDeleteBP;
import nc.bs.pm.pmrlcontractschedule.ace.bp.AcePmRLContractscheduleInsertBP;
import nc.bs.pm.pmrlcontractschedule.ace.bp.AcePmRLContractscheduleSendApproveBP;
import nc.bs.pm.pmrlcontractschedule.ace.bp.AcePmRLContractscheduleUnApproveBP;
import nc.bs.pm.pmrlcontractschedule.ace.bp.AcePmRLContractscheduleUnSendApproveBP;
import nc.bs.pm.pmrlcontractschedule.ace.bp.AcePmRLContractscheduleUpdateBP;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pm.feebalance.PmFeebalanceBVO;
import nc.vo.pm.rlcontractschedule.AggRLContractschedule;
import nc.vo.pm.rlcontractschedule.RLContractscheduleBVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public abstract class AcePmRLContractschedulePubServiceImpl {
	// 新增
	public AggRLContractschedule[] pubinsertBills(AggRLContractschedule[] clientFullVOs,
			AggRLContractschedule[] originBills) throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggRLContractschedule> transferTool = new BillTransferTool<AggRLContractschedule>(
					clientFullVOs);
			// 调用BP
			AcePmRLContractscheduleInsertBP action = new AcePmRLContractscheduleInsertBP();
			AggRLContractschedule[] retvos = action.insert(clientFullVOs);
			// 构造返回数据
			/*反写多编码合同表体累计拉数量*/
			updateAggPmFeebalance(clientFullVOs);
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	private void updateAggPmFeebalance(AggRLContractschedule[] clientFullVOs) throws BusinessException {
		Map<String, String> sourceId = new HashMap<String, String>();
		for(AggRLContractschedule temp : clientFullVOs){
			RLContractscheduleBVO[] bvos = temp.getChildrenVO();
			for(RLContractscheduleBVO bvo : bvos){
				sourceId.put(bvo.getCsourcebillbid(), bvo.getCsourcebillbid());
			}
		}
		List<PmFeebalanceBVO> updatePmFeebalanceBVO = new ArrayList<PmFeebalanceBVO>();
        for (String key : sourceId.keySet()) {
        	PmFeebalanceBVO bvo = (PmFeebalanceBVO) getHyPubBO().queryByPrimaryKey(PmFeebalanceBVO.class, key);
        	if(null == bvo){
        		throw new BusinessException("单据已删除！");
        	}
        	RLContractscheduleBVO[] bvos = (RLContractscheduleBVO[]) getHyPubBO().queryByCondition(RLContractscheduleBVO.class, "nvl(dr,0) = 0 and csourcebillbid='" + key + "'");
        	UFDouble number = UFDouble.ZERO_DBL;
        	for(RLContractscheduleBVO bvo1 : bvos){
        		/*报送金额*/
        		number = number.add(bvo1.getMoney());
        	}
        	if(bvo.getMoney().compareTo(number) < 0){
        		throw new BusinessException("累计进度金额大于付款金额");
        	}
        	bvo.setStatus(VOStatus.UPDATED);
        	/*累计进度金额赋值*/
        	bvo.setContractalter_mny(number);
        	updatePmFeebalanceBVO.add(bvo);
        }
        if(updatePmFeebalanceBVO.size() > 0){
        	getHyPubBO().updateAry(updatePmFeebalanceBVO.toArray(new PmFeebalanceBVO[0]));
        }
	}

	private HYPubBO hyPubBO;

	public HYPubBO getHyPubBO() {
		if(null == hyPubBO){
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	public void setHyPubBO(HYPubBO hyPubBO) {
		this.hyPubBO = hyPubBO;
	}
	// 删除
	public void pubdeleteBills(AggRLContractschedule[] clientFullVOs,
			AggRLContractschedule[] originBills) throws BusinessException {
		try {
			// 调用BP
			new AcePmRLContractscheduleDeleteBP().delete(clientFullVOs);
			/*反写多编码合同表体累计拉数量*/
			updateAggPmFeebalance(clientFullVOs);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggRLContractschedule[] pubupdateBills(AggRLContractschedule[] clientFullVOs,
			AggRLContractschedule[] originBills) throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggRLContractschedule> transferTool = new BillTransferTool<AggRLContractschedule>(
					clientFullVOs);
			AcePmRLContractscheduleUpdateBP bp = new AcePmRLContractscheduleUpdateBP();
			AggRLContractschedule[] retvos = bp.update(clientFullVOs, originBills);
			// 构造返回数据
			updateAggPmFeebalance(clientFullVOs);
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggRLContractschedule[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggRLContractschedule[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggRLContractschedule> query = new BillLazyQuery<AggRLContractschedule>(
					AggRLContractschedule.class);
			bills = query.query(queryScheme, null);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return bills;
	}

	/**
	 * 由子类实现，查询之前对queryScheme进行加工，加入自己的逻辑
	 * 
	 * @param queryScheme
	 */
	protected void preQuery(IQueryScheme queryScheme) {
		// 查询之前对queryScheme进行加工，加入自己的逻辑
	}

	// 提交
	public AggRLContractschedule[] pubsendapprovebills(
			AggRLContractschedule[] clientFullVOs, AggRLContractschedule[] originBills)
			throws BusinessException {
		AcePmRLContractscheduleSendApproveBP bp = new AcePmRLContractscheduleSendApproveBP();
		AggRLContractschedule[] retvos = bp.sendApprove(clientFullVOs, originBills);
		return retvos;
	}

	// 收回
	public AggRLContractschedule[] pubunsendapprovebills(
			AggRLContractschedule[] clientFullVOs, AggRLContractschedule[] originBills)
			throws BusinessException {
		AcePmRLContractscheduleUnSendApproveBP bp = new AcePmRLContractscheduleUnSendApproveBP();
		AggRLContractschedule[] retvos = bp.unSend(clientFullVOs, originBills);
		return retvos;
	};

	// 审批
	public AggRLContractschedule[] pubapprovebills(AggRLContractschedule[] clientFullVOs,
			AggRLContractschedule[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AcePmRLContractscheduleApproveBP bp = new AcePmRLContractscheduleApproveBP();
		AggRLContractschedule[] retvos = bp.approve(clientFullVOs, originBills);
		return retvos;
	}

	// 弃审

	public AggRLContractschedule[] pubunapprovebills(AggRLContractschedule[] clientFullVOs,
			AggRLContractschedule[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AcePmRLContractscheduleUnApproveBP bp = new AcePmRLContractscheduleUnApproveBP();
		AggRLContractschedule[] retvos = bp.unApprove(clientFullVOs, originBills);
		return retvos;
	}

}