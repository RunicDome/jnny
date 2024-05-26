package nc.impl.pm;

import nc.impl.pub.ace.AcePmRLContractalterPubServiceImpl;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.BillQuery;
import nc.itf.pm.IPmRLContractalterMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pm.rlcontractalter.AggRLContractalterHVO;
import nc.vo.pub.BusinessException;

public class PmRLContractalterMaintainImpl extends AcePmRLContractalterPubServiceImpl
		implements IPmRLContractalterMaintain {

	@Override
	public void delete(AggRLContractalterHVO[] clientFullVOs,
			AggRLContractalterHVO[] originBills) throws BusinessException {
		super.pubdeleteBills(clientFullVOs, originBills);
	}

	@Override
	public AggRLContractalterHVO[] insert(AggRLContractalterHVO[] clientFullVOs,
			AggRLContractalterHVO[] originBills) throws BusinessException {
		return super.pubinsertBills(clientFullVOs, originBills);
	}

	@Override
	public AggRLContractalterHVO[] update(AggRLContractalterHVO[] clientFullVOs,
			AggRLContractalterHVO[] originBills) throws BusinessException {
		return super.pubupdateBills(clientFullVOs, originBills);
	}

	@Override
	public AggRLContractalterHVO[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

	@Override
	public AggRLContractalterHVO[] save(AggRLContractalterHVO[] clientFullVOs,
			AggRLContractalterHVO[] originBills) throws BusinessException {
		return super.pubsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggRLContractalterHVO[] unsave(AggRLContractalterHVO[] clientFullVOs,
			AggRLContractalterHVO[] originBills) throws BusinessException {
		
		return super.pubunsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggRLContractalterHVO[] approve(AggRLContractalterHVO[] clientFullVOs,
			AggRLContractalterHVO[] originBills) throws BusinessException {
		return super.pubapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggRLContractalterHVO[] unapprove(AggRLContractalterHVO[] clientFullVOs,
			AggRLContractalterHVO[] originBills) throws BusinessException {
		return super.pubunapprovebills(clientFullVOs, originBills);
	}

	@Override
	public String[] queryPks(IQueryScheme queryScheme) throws BusinessException {
		AggRLContractalterHVO[] bills = null;
		BillLazyQuery<AggRLContractalterHVO> query = new BillLazyQuery<AggRLContractalterHVO>(AggRLContractalterHVO.class);
		bills = query.query(queryScheme, null);
		String[] pks = new String[bills.length];
		for (int i = 0; i < pks.length; i++) {
			pks[i] = bills[i].getPrimaryKey();
		}
		return pks;
	}

	@Override
	public Object[] queryObjectByPks(String[] pks) throws BusinessException {
		AggRLContractalterHVO[] bills = null;
		BillQuery<AggRLContractalterHVO> query = new BillQuery<AggRLContractalterHVO>(AggRLContractalterHVO.class);
		bills = query.query(pks);
		return bills;
	}

}
