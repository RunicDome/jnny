package nc.impl.pub.ace;

import nc.bs.aim.equipinsp.ace.bp.AceEquipinspApproveBP;
import nc.bs.aim.equipinsp.ace.bp.AceEquipinspDeleteBP;
import nc.bs.aim.equipinsp.ace.bp.AceEquipinspInsertBP;
import nc.bs.aim.equipinsp.ace.bp.AceEquipinspSendApproveBP;
import nc.bs.aim.equipinsp.ace.bp.AceEquipinspUnApproveBP;
import nc.bs.aim.equipinsp.ace.bp.AceEquipinspUnSendApproveBP;
import nc.bs.aim.equipinsp.ace.bp.AceEquipinspUpdateBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.aim.equipinsp.AggEquipinsp;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public abstract class AceEquipinspPubServiceImpl {
	// 新增
	public AggEquipinsp[] pubinsertBills(AggEquipinsp[] clientFullVOs,
			AggEquipinsp[] originBills) throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggEquipinsp> transferTool = new BillTransferTool<AggEquipinsp>(
					clientFullVOs);
			// 调用BP
			AceEquipinspInsertBP action = new AceEquipinspInsertBP();
			AggEquipinsp[] retvos = action.insert(clientFullVOs);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 删除
	public void pubdeleteBills(AggEquipinsp[] clientFullVOs,
			AggEquipinsp[] originBills) throws BusinessException {
		try {
			// 调用BP
			new AceEquipinspDeleteBP().delete(clientFullVOs);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggEquipinsp[] pubupdateBills(AggEquipinsp[] clientFullVOs,
			AggEquipinsp[] originBills) throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggEquipinsp> transferTool = new BillTransferTool<AggEquipinsp>(
					clientFullVOs);
			AceEquipinspUpdateBP bp = new AceEquipinspUpdateBP();
			AggEquipinsp[] retvos = bp.update(clientFullVOs, originBills);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggEquipinsp[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggEquipinsp[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggEquipinsp> query = new BillLazyQuery<AggEquipinsp>(
					AggEquipinsp.class);
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
	public AggEquipinsp[] pubsendapprovebills(AggEquipinsp[] clientFullVOs,
			AggEquipinsp[] originBills) throws BusinessException {
		AceEquipinspSendApproveBP bp = new AceEquipinspSendApproveBP();
		AggEquipinsp[] retvos = bp.sendApprove(clientFullVOs, originBills);
		return retvos;
	}

	// 收回
	public AggEquipinsp[] pubunsendapprovebills(AggEquipinsp[] clientFullVOs,
			AggEquipinsp[] originBills) throws BusinessException {
		AceEquipinspUnSendApproveBP bp = new AceEquipinspUnSendApproveBP();
		AggEquipinsp[] retvos = bp.unSend(clientFullVOs, originBills);
		return retvos;
	};

	// 审批
	public AggEquipinsp[] pubapprovebills(AggEquipinsp[] clientFullVOs,
			AggEquipinsp[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceEquipinspApproveBP bp = new AceEquipinspApproveBP();
		AggEquipinsp[] retvos = bp.approve(clientFullVOs, originBills);
		return retvos;
	}

	// 弃审

	public AggEquipinsp[] pubunapprovebills(AggEquipinsp[] clientFullVOs,
			AggEquipinsp[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceEquipinspUnApproveBP bp = new AceEquipinspUnApproveBP();
		AggEquipinsp[] retvos = bp.unApprove(clientFullVOs, originBills);
		return retvos;
	}

}