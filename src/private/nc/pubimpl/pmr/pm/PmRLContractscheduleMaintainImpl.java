package nc.pubimpl.pmr.pm;

import nc.bs.framework.common.NCLocator;
import nc.impl.pub.ace.AcePmRLContractschedulePubServiceImpl;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.BillQuery;
import nc.itf.uap.pf.IPFBusiAction;
import nc.itf.uap.pf.IPfExchangeService;
import nc.pubitf.pmr.pm.IPmRLContractscheduleMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.arap.payable.AggPayableBillVO;
import nc.vo.arap.payable.PayableBillItemVO;
import nc.vo.arap.payable.PayableBillVO;
import nc.vo.pm.rlcontractschedule.AggRLContractschedule;
import nc.vo.pm.rlcontractschedule.RLContractschedule;
import nc.vo.pub.BusinessException;

public class PmRLContractscheduleMaintainImpl extends
		AcePmRLContractschedulePubServiceImpl implements
		IPmRLContractscheduleMaintain {

	@Override
	public void delete(AggRLContractschedule[] clientFullVOs,
			AggRLContractschedule[] originBills) throws BusinessException {
		super.pubdeleteBills(clientFullVOs, originBills);
	}

	@Override
	public AggRLContractschedule[] insert(
			AggRLContractschedule[] clientFullVOs,
			AggRLContractschedule[] originBills) throws BusinessException {
		return super.pubinsertBills(clientFullVOs, originBills);
	}

	@Override
	public AggRLContractschedule[] update(
			AggRLContractschedule[] clientFullVOs,
			AggRLContractschedule[] originBills) throws BusinessException {
		return super.pubupdateBills(clientFullVOs, originBills);
	}

	@Override
	public AggRLContractschedule[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

	@Override
	public AggRLContractschedule[] save(AggRLContractschedule[] clientFullVOs,
			AggRLContractschedule[] originBills) throws BusinessException {
		return super.pubsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggRLContractschedule[] unsave(
			AggRLContractschedule[] clientFullVOs,
			AggRLContractschedule[] originBills) throws BusinessException {
		return super.pubunsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggRLContractschedule[] approve(
			AggRLContractschedule[] clientFullVOs,
			AggRLContractschedule[] originBills) throws BusinessException {
		// 多编码进度款单审批推工程应付单
		AggRLContractschedule clienthvo = clientFullVOs[0];// 审批后Aggvo
		RLContractschedule headvo = (RLContractschedule) clienthvo
				.getParentVO();
		if (headvo.getBill_status() == 1) {// 审批成功
			// 推工程应付单
			IPfExchangeService pf = NCLocator.getInstance().lookup(
					IPfExchangeService.class);
			AggPayableBillVO yfdVO = (AggPayableBillVO) pf.runChangeData(
					"4Z03", "F1-Cxx-01", clienthvo, null);
			/*IArapPayableBillPubService yfser = NCLocator.getInstance().lookup(
					IArapPayableBillPubService.class);
			yfser.save(yfdVO);*/
			getIPFBusiAction().processAction("SAVE", "F1-Cxx-01", null, yfdVO, null,
					null);
		}
		return super.pubapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggRLContractschedule[] unapprove(
			AggRLContractschedule[] clientFullVOs,
			AggRLContractschedule[] originBills) throws BusinessException {
		// 多编码结算单取消审批
		AggRLContractschedule clienthvo = clientFullVOs[0];// 取消审批后Aggvo
		AggPayableBillVO yfaggvo = new AggPayableBillVO();
		RLContractschedule headvo = (RLContractschedule) clienthvo
				.getParentVO();
		if (headvo.getBill_status() == -1) {// 取消审批成功-自由态
			PayableBillItemVO[] mxvos = (PayableBillItemVO[]) getHyPubBO()
					.queryByCondition(
							PayableBillItemVO.class,
							"nvl(dr,0) = 0 and top_billid = '"
									+ headvo.getPrimaryKey() + "'");
			yfaggvo.setChildrenVO(mxvos);
			if (mxvos != null && mxvos.length > 0) {
				PayableBillVO yfhvo = (PayableBillVO) getHyPubBO()
						.queryByPrimaryKey(PayableBillVO.class,
								mxvos[0].getPk_payablebill() + "");
				yfaggvo.setParentVO(yfhvo);
				if (yfhvo != null && yfhvo.getApprovestatus() != -1
						&& yfhvo.getApprovestatus() != 0) {
					throw new BusinessException("下游单据工程应付单单号["
							+ yfhvo.getBillno() + "]单据不为保存态，不能取消审批！");
				}
				getIPFBusiAction().processAction("DELETE", "F1", null, yfaggvo,
						null, null);
			}
		}
		return super.pubunapprovebills(clientFullVOs, originBills);
	}

	public IPFBusiAction getIPFBusiAction() {
		return NCLocator.getInstance().lookup(IPFBusiAction.class);
	}

	@Override
	public String[] queryPks(IQueryScheme queryScheme) throws BusinessException {
		AggRLContractschedule[] bills = null;
		BillLazyQuery<AggRLContractschedule> query = new BillLazyQuery<AggRLContractschedule>(AggRLContractschedule.class);
		bills = query.query(queryScheme, null);
		String[] pks = new String[bills.length];
		for (int i = 0; i < pks.length; i++) {
			pks[i] = bills[i].getPrimaryKey();
		}
		return pks;
	}

	@Override
	public Object[] queryObjectByPks(String[] pks) throws BusinessException {
		AggRLContractschedule[] bills = null;
		BillQuery<AggRLContractschedule> query = new BillQuery<AggRLContractschedule>(AggRLContractschedule.class);
		bills = query.query(pks);
		return bills;
	}
}
