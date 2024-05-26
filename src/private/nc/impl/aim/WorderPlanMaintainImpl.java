package nc.impl.aim;

import nc.impl.pub.ace.AceWorderPlanPubServiceImpl;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.BillQuery;
import nc.itf.aim.IWorderPlanMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.aim.worderplan.AggWorderplan;
import nc.vo.pub.BusinessException;

public class WorderPlanMaintainImpl extends AceWorderPlanPubServiceImpl
		implements IWorderPlanMaintain {

	@Override
	public void delete(AggWorderplan[] clientFullVOs,
			AggWorderplan[] originBills) throws BusinessException {
		super.pubdeleteBills(clientFullVOs, originBills);
	}

	@Override
	public AggWorderplan[] insert(AggWorderplan[] clientFullVOs,
			AggWorderplan[] originBills) throws BusinessException {
		return super.pubinsertBills(clientFullVOs, originBills);
	}

	@Override
	public AggWorderplan[] update(AggWorderplan[] clientFullVOs,
			AggWorderplan[] originBills) throws BusinessException {
		return super.pubupdateBills(clientFullVOs, originBills);
	}

	@Override
	public AggWorderplan[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

	@Override
	public AggWorderplan[] save(AggWorderplan[] clientFullVOs,
			AggWorderplan[] originBills) throws BusinessException {
		return super.pubsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggWorderplan[] unsave(AggWorderplan[] clientFullVOs,
			AggWorderplan[] originBills) throws BusinessException {
		return super.pubunsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggWorderplan[] approve(AggWorderplan[] clientFullVOs,
			AggWorderplan[] originBills) throws BusinessException {
		return super.pubapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggWorderplan[] unapprove(AggWorderplan[] clientFullVOs,
			AggWorderplan[] originBills) throws BusinessException {
		return super.pubunapprovebills(clientFullVOs, originBills);
	}

	@Override
	public String[] queryPks(IQueryScheme queryScheme) throws BusinessException {
		AggWorderplan[] bills = null;
		BillLazyQuery<AggWorderplan> query = new BillLazyQuery<AggWorderplan>(
				AggWorderplan.class);
		bills = query.query(queryScheme, null);
		String[] pks = new String[bills.length];
		for (int i = 0; i < pks.length; i++) {
			pks[i] = bills[i].getPrimaryKey();
		}
		return pks;
	}

	@Override
	public Object[] queryObjectByPks(String[] pks) throws BusinessException {
		AggWorderplan[] bills = null;
		BillQuery<AggWorderplan> query = new BillQuery<AggWorderplan>(
				AggWorderplan.class);
		bills = query.query(pks);
		return bills;
	}

}
