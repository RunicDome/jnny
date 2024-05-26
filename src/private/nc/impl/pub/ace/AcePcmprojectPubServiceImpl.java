package nc.impl.pub.ace;

import nc.bs.pcm.pcmproject.ace.bp.AcePcmprojectApproveBP;
import nc.bs.pcm.pcmproject.ace.bp.AcePcmprojectDeleteBP;
import nc.bs.pcm.pcmproject.ace.bp.AcePcmprojectInsertBP;
import nc.bs.pcm.pcmproject.ace.bp.AcePcmprojectSendApproveBP;
import nc.bs.pcm.pcmproject.ace.bp.AcePcmprojectUnApproveBP;
import nc.bs.pcm.pcmproject.ace.bp.AcePcmprojectUnSendApproveBP;
import nc.bs.pcm.pcmproject.ace.bp.AcePcmprojectUpdateBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pcm.pcmproject.AggPcmProject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public abstract class AcePcmprojectPubServiceImpl {
	// 新增
	public AggPcmProject[] pubinsertBills(AggPcmProject[] clientFullVOs,
			AggPcmProject[] originBills) throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggPcmProject> transferTool = new BillTransferTool<AggPcmProject>(
					clientFullVOs);
			// 调用BP
			AcePcmprojectInsertBP action = new AcePcmprojectInsertBP();
			AggPcmProject[] retvos = action.insert(clientFullVOs);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 删除
	public void pubdeleteBills(AggPcmProject[] clientFullVOs,
			AggPcmProject[] originBills) throws BusinessException {
		try {
			// 调用BP
			new AcePcmprojectDeleteBP().delete(clientFullVOs);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggPcmProject[] pubupdateBills(AggPcmProject[] clientFullVOs,
			AggPcmProject[] originBills) throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggPcmProject> transferTool = new BillTransferTool<AggPcmProject>(
					clientFullVOs);
			AcePcmprojectUpdateBP bp = new AcePcmprojectUpdateBP();
			AggPcmProject[] retvos = bp.update(clientFullVOs, originBills);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggPcmProject[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggPcmProject[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggPcmProject> query = new BillLazyQuery<AggPcmProject>(
					AggPcmProject.class);
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
	public AggPcmProject[] pubsendapprovebills(AggPcmProject[] clientFullVOs,
			AggPcmProject[] originBills) throws BusinessException {
		AcePcmprojectSendApproveBP bp = new AcePcmprojectSendApproveBP();
		AggPcmProject[] retvos = bp.sendApprove(clientFullVOs, originBills);
		return retvos;
	}

	// 收回
	public AggPcmProject[] pubunsendapprovebills(AggPcmProject[] clientFullVOs,
			AggPcmProject[] originBills) throws BusinessException {
		AcePcmprojectUnSendApproveBP bp = new AcePcmprojectUnSendApproveBP();
		AggPcmProject[] retvos = bp.unSend(clientFullVOs, originBills);
		return retvos;
	};

	// 审批
	public AggPcmProject[] pubapprovebills(AggPcmProject[] clientFullVOs,
			AggPcmProject[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AcePcmprojectApproveBP bp = new AcePcmprojectApproveBP();
		AggPcmProject[] retvos = bp.approve(clientFullVOs, originBills);
		return retvos;
	}

	// 弃审

	public AggPcmProject[] pubunapprovebills(AggPcmProject[] clientFullVOs,
			AggPcmProject[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AcePcmprojectUnApproveBP bp = new AcePcmprojectUnApproveBP();
		AggPcmProject[] retvos = bp.unApprove(clientFullVOs, originBills);
		return retvos;
	}

}