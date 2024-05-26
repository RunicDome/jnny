package nc.pubimpl.pmr.pm;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pub.ace.AcePmFeebalanceCtPubServiceImpl;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.BillQuery;
import nc.itf.pcm.contract.DbmSplitToContr;
import nc.itf.pcm.contract.pvt.IContract;
import nc.pubitf.pmr.pm.IPmFeebalanceCtMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pcm.contract.ContrHeadVO;
import nc.vo.pcm.contract.ContrWorksVO;
import nc.vo.pcm.contract.ContractBillVO;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;

public class PmFeebalanceCtMaintainImpl extends AcePmFeebalanceCtPubServiceImpl
		implements IPmFeebalanceCtMaintain {

	@Override
	public void delete(AggPmFeebalance[] clientFullVOs,
			AggPmFeebalance[] originBills) throws BusinessException {
		super.pubdeleteBills(clientFullVOs, originBills);
	}

	@Override
	public AggPmFeebalance[] insert(AggPmFeebalance[] clientFullVOs,
			AggPmFeebalance[] originBills) throws BusinessException {
		return super.pubinsertBills(clientFullVOs, originBills);
	}

	@Override
	public AggPmFeebalance[] update(AggPmFeebalance[] clientFullVOs,
			AggPmFeebalance[] originBills) throws BusinessException {
		return super.pubupdateBills(clientFullVOs, originBills);
	}

	@Override
	public AggPmFeebalance[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

	@Override
	public AggPmFeebalance[] save(AggPmFeebalance[] clientFullVOs,
			AggPmFeebalance[] originBills) throws BusinessException {
		return super.pubsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggPmFeebalance[] unsave(AggPmFeebalance[] clientFullVOs,
			AggPmFeebalance[] originBills) throws BusinessException {
		return super.pubunsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggPmFeebalance[] approve(AggPmFeebalance[] clientFullVOs,
			AggPmFeebalance[] originBills) throws BusinessException {
		AggPmFeebalance[] resVOs = super.pubapprovebills(clientFullVOs, originBills);
		return resVOs;
	}

	@Override
	public AggPmFeebalance[] unapprove(AggPmFeebalance[] clientFullVOs,
			AggPmFeebalance[] originBills) throws BusinessException {
		return super.pubunapprovebills(clientFullVOs, originBills);
	}

	@Override
	public String[] queryPks(IQueryScheme queryScheme) throws BusinessException {
		AggPmFeebalance[] bills = null;
		BillLazyQuery<AggPmFeebalance> query = new BillLazyQuery<AggPmFeebalance>(AggPmFeebalance.class);
		bills = query.query(queryScheme, null);
		String[] pks = new String[bills.length];
		for (int i = 0; i < pks.length; i++) {
			pks[i] = bills[i].getPrimaryKey();
		}
		return pks;
	}

	@Override
	public Object[] queryObjectByPks(String[] pks) throws BusinessException {
		AggPmFeebalance[] bills = null;
		BillQuery<AggPmFeebalance> query = new BillQuery<AggPmFeebalance>(AggPmFeebalance.class);
		bills = query.query(pks);
		return bills;
	}

}
