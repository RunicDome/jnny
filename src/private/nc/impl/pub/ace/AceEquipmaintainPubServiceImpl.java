package nc.impl.pub.ace;

import nc.bs.aim.equipmaintain.ace.bp.AceEquipmaintainApproveBP;
import nc.bs.aim.equipmaintain.ace.bp.AceEquipmaintainDeleteBP;
import nc.bs.aim.equipmaintain.ace.bp.AceEquipmaintainInsertBP;
import nc.bs.aim.equipmaintain.ace.bp.AceEquipmaintainSendApproveBP;
import nc.bs.aim.equipmaintain.ace.bp.AceEquipmaintainUnApproveBP;
import nc.bs.aim.equipmaintain.ace.bp.AceEquipmaintainUnSendApproveBP;
import nc.bs.aim.equipmaintain.ace.bp.AceEquipmaintainUpdateBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.aim.equipmaintain.AggEquipmaintain;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public abstract class AceEquipmaintainPubServiceImpl {
	// 新增
	public AggEquipmaintain[] pubinsertBills(AggEquipmaintain[] clientFullVOs,
			AggEquipmaintain[] originBills) throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggEquipmaintain> transferTool = new BillTransferTool<AggEquipmaintain>(
					clientFullVOs);
			// 调用BP
			AceEquipmaintainInsertBP action = new AceEquipmaintainInsertBP();
			AggEquipmaintain[] retvos = action.insert(clientFullVOs);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 删除
	public void pubdeleteBills(AggEquipmaintain[] clientFullVOs,
			AggEquipmaintain[] originBills) throws BusinessException {
		try {
			// 调用BP
			new AceEquipmaintainDeleteBP().delete(clientFullVOs);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggEquipmaintain[] pubupdateBills(AggEquipmaintain[] clientFullVOs,
			AggEquipmaintain[] originBills) throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggEquipmaintain> transferTool = new BillTransferTool<AggEquipmaintain>(
					clientFullVOs);
			AceEquipmaintainUpdateBP bp = new AceEquipmaintainUpdateBP();
			AggEquipmaintain[] retvos = bp.update(clientFullVOs, originBills);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggEquipmaintain[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggEquipmaintain[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggEquipmaintain> query = new BillLazyQuery<AggEquipmaintain>(
					AggEquipmaintain.class);
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
	public AggEquipmaintain[] pubsendapprovebills(
			AggEquipmaintain[] clientFullVOs, AggEquipmaintain[] originBills)
			throws BusinessException {
		AceEquipmaintainSendApproveBP bp = new AceEquipmaintainSendApproveBP();
		AggEquipmaintain[] retvos = bp.sendApprove(clientFullVOs, originBills);
		return retvos;
	}

	// 收回
	public AggEquipmaintain[] pubunsendapprovebills(
			AggEquipmaintain[] clientFullVOs, AggEquipmaintain[] originBills)
			throws BusinessException {
		AceEquipmaintainUnSendApproveBP bp = new AceEquipmaintainUnSendApproveBP();
		AggEquipmaintain[] retvos = bp.unSend(clientFullVOs, originBills);
		return retvos;
	};

	// 审批
	public AggEquipmaintain[] pubapprovebills(AggEquipmaintain[] clientFullVOs,
			AggEquipmaintain[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceEquipmaintainApproveBP bp = new AceEquipmaintainApproveBP();
		AggEquipmaintain[] retvos = bp.approve(clientFullVOs, originBills);
		return retvos;
	}

	// 弃审

	public AggEquipmaintain[] pubunapprovebills(
			AggEquipmaintain[] clientFullVOs, AggEquipmaintain[] originBills)
			throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceEquipmaintainUnApproveBP bp = new AceEquipmaintainUnApproveBP();
		AggEquipmaintain[] retvos = bp.unApprove(clientFullVOs, originBills);
		return retvos;
	}

}