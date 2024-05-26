package nc.impl.pub.ace;

import nc.bs.pcm.pcmyearplan.ace.bp.AcePcmyearplanInsertBP;
import nc.bs.pcm.pcmyearplan.ace.bp.AcePcmyearplanUpdateBP;
import nc.bs.pcm.pcmyearplan.ace.bp.AcePcmyearplanDeleteBP;
import nc.bs.pcm.pcmyearplan.ace.bp.AcePcmyearplanSendApproveBP;
import nc.bs.pcm.pcmyearplan.ace.bp.AcePcmyearplanUnSendApproveBP;
import nc.bs.pcm.pcmyearplan.ace.bp.AcePcmyearplanApproveBP;
import nc.bs.pcm.pcmyearplan.ace.bp.AcePcmyearplanUnApproveBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pcm.pcmyearplan.AggPcmYearplan;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public abstract class AcePcmyearplanPubServiceImpl {
	// 新增
	public AggPcmYearplan[] pubinsertBills(AggPcmYearplan[] clientFullVOs,
			AggPcmYearplan[] originBills) throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggPcmYearplan> transferTool = new BillTransferTool<AggPcmYearplan>(
					clientFullVOs);
			// 调用BP
			AcePcmyearplanInsertBP action = new AcePcmyearplanInsertBP();
			AggPcmYearplan[] retvos = action.insert(clientFullVOs);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 删除
	public void pubdeleteBills(AggPcmYearplan[] clientFullVOs,
			AggPcmYearplan[] originBills) throws BusinessException {
		try {
			// 调用BP
			new AcePcmyearplanDeleteBP().delete(clientFullVOs);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggPcmYearplan[] pubupdateBills(AggPcmYearplan[] clientFullVOs,
			AggPcmYearplan[] originBills) throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggPcmYearplan> transferTool = new BillTransferTool<AggPcmYearplan>(
					clientFullVOs);
			AcePcmyearplanUpdateBP bp = new AcePcmyearplanUpdateBP();
			AggPcmYearplan[] retvos = bp.update(clientFullVOs, originBills);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggPcmYearplan[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggPcmYearplan[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggPcmYearplan> query = new BillLazyQuery<AggPcmYearplan>(
					AggPcmYearplan.class);
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
	public AggPcmYearplan[] pubsendapprovebills(
			AggPcmYearplan[] clientFullVOs, AggPcmYearplan[] originBills)
			throws BusinessException {
		AcePcmyearplanSendApproveBP bp = new AcePcmyearplanSendApproveBP();
		AggPcmYearplan[] retvos = bp.sendApprove(clientFullVOs, originBills);
		return retvos;
	}

	// 收回
	public AggPcmYearplan[] pubunsendapprovebills(
			AggPcmYearplan[] clientFullVOs, AggPcmYearplan[] originBills)
			throws BusinessException {
		AcePcmyearplanUnSendApproveBP bp = new AcePcmyearplanUnSendApproveBP();
		AggPcmYearplan[] retvos = bp.unSend(clientFullVOs, originBills);
		return retvos;
	};

	// 审批
	public AggPcmYearplan[] pubapprovebills(AggPcmYearplan[] clientFullVOs,
			AggPcmYearplan[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AcePcmyearplanApproveBP bp = new AcePcmyearplanApproveBP();
		AggPcmYearplan[] retvos = bp.approve(clientFullVOs, originBills);
		return retvos;
	}

	// 弃审

	public AggPcmYearplan[] pubunapprovebills(AggPcmYearplan[] clientFullVOs,
			AggPcmYearplan[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AcePcmyearplanUnApproveBP bp = new AcePcmyearplanUnApproveBP();
		AggPcmYearplan[] retvos = bp.unApprove(clientFullVOs, originBills);
		return retvos;
	}

}