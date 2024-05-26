package nc.impl.pub.ace;

import nc.bs.aim.worderplan.ace.bp.AceWorderPlanInsertBP;
import nc.bs.aim.worderplan.ace.bp.AceWorderPlanUpdateBP;
import nc.bs.aim.worderplan.ace.bp.AceWorderPlanDeleteBP;
import nc.bs.aim.worderplan.ace.bp.AceWorderPlanSendApproveBP;
import nc.bs.aim.worderplan.ace.bp.AceWorderPlanUnSendApproveBP;
import nc.bs.aim.worderplan.ace.bp.AceWorderPlanApproveBP;
import nc.bs.aim.worderplan.ace.bp.AceWorderPlanUnApproveBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.aim.worderplan.AggWorderplan;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public abstract class AceWorderPlanPubServiceImpl {
	// 新增
	public AggWorderplan[] pubinsertBills(AggWorderplan[] clientFullVOs,
			AggWorderplan[] originBills) throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggWorderplan> transferTool = new BillTransferTool<AggWorderplan>(
					clientFullVOs);
			// 调用BP
			AceWorderPlanInsertBP action = new AceWorderPlanInsertBP();
			AggWorderplan[] retvos = action.insert(clientFullVOs);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 删除
	public void pubdeleteBills(AggWorderplan[] clientFullVOs,
			AggWorderplan[] originBills) throws BusinessException {
		try {
			// 调用BP
			new AceWorderPlanDeleteBP().delete(clientFullVOs);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggWorderplan[] pubupdateBills(AggWorderplan[] clientFullVOs,
			AggWorderplan[] originBills) throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggWorderplan> transferTool = new BillTransferTool<AggWorderplan>(
					clientFullVOs);
			AceWorderPlanUpdateBP bp = new AceWorderPlanUpdateBP();
			AggWorderplan[] retvos = bp.update(clientFullVOs, originBills);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggWorderplan[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggWorderplan[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggWorderplan> query = new BillLazyQuery<AggWorderplan>(
					AggWorderplan.class);
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
	public AggWorderplan[] pubsendapprovebills(
			AggWorderplan[] clientFullVOs, AggWorderplan[] originBills)
			throws BusinessException {
		AceWorderPlanSendApproveBP bp = new AceWorderPlanSendApproveBP();
		AggWorderplan[] retvos = bp.sendApprove(clientFullVOs, originBills);
		return retvos;
	}

	// 收回
	public AggWorderplan[] pubunsendapprovebills(
			AggWorderplan[] clientFullVOs, AggWorderplan[] originBills)
			throws BusinessException {
		AceWorderPlanUnSendApproveBP bp = new AceWorderPlanUnSendApproveBP();
		AggWorderplan[] retvos = bp.unSend(clientFullVOs, originBills);
		return retvos;
	};

	// 审批
	public AggWorderplan[] pubapprovebills(AggWorderplan[] clientFullVOs,
			AggWorderplan[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceWorderPlanApproveBP bp = new AceWorderPlanApproveBP();
		AggWorderplan[] retvos = bp.approve(clientFullVOs, originBills);
		return retvos;
	}

	// 弃审

	public AggWorderplan[] pubunapprovebills(AggWorderplan[] clientFullVOs,
			AggWorderplan[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceWorderPlanUnApproveBP bp = new AceWorderPlanUnApproveBP();
		AggWorderplan[] retvos = bp.unApprove(clientFullVOs, originBills);
		return retvos;
	}

}