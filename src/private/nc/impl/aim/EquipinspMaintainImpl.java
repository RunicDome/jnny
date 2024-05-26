package nc.impl.aim;

import nc.impl.pub.ace.AceEquipinspPubServiceImpl;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.BillQuery;
import nc.itf.aim.IEquipinspMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.aim.equipinsp.AggEquipinsp;
import nc.vo.pub.BusinessException;

public class EquipinspMaintainImpl extends AceEquipinspPubServiceImpl implements
		IEquipinspMaintain {

	@Override
	public void delete(AggEquipinsp[] clientFullVOs, AggEquipinsp[] originBills)
			throws BusinessException {
		super.pubdeleteBills(clientFullVOs, originBills);
	}

	@Override
	public AggEquipinsp[] insert(AggEquipinsp[] clientFullVOs,
			AggEquipinsp[] originBills) throws BusinessException {
		return super.pubinsertBills(clientFullVOs, originBills);
	}

	@Override
	public AggEquipinsp[] update(AggEquipinsp[] clientFullVOs,
			AggEquipinsp[] originBills) throws BusinessException {
		return super.pubupdateBills(clientFullVOs, originBills);
	}

	@Override
	public AggEquipinsp[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

	@Override
	public AggEquipinsp[] save(AggEquipinsp[] clientFullVOs,
			AggEquipinsp[] originBills) throws BusinessException {
		return super.pubsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggEquipinsp[] unsave(AggEquipinsp[] clientFullVOs,
			AggEquipinsp[] originBills) throws BusinessException {
		return super.pubunsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggEquipinsp[] approve(AggEquipinsp[] clientFullVOs,
			AggEquipinsp[] originBills) throws BusinessException {
		return super.pubapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggEquipinsp[] unapprove(AggEquipinsp[] clientFullVOs,
			AggEquipinsp[] originBills) throws BusinessException {
		return super.pubunapprovebills(clientFullVOs, originBills);
	}

	@Override
	public String[] queryPks(IQueryScheme queryScheme) throws BusinessException {
		AggEquipinsp[] bills = null;
		BillLazyQuery<AggEquipinsp> query = new BillLazyQuery<AggEquipinsp>(
				AggEquipinsp.class);
		bills = query.query(queryScheme, null);
		String[] pks = new String[bills.length];
		for (int i = 0; i < pks.length; i++) {
			pks[i] = bills[i].getPrimaryKey();
		}
		return pks;
	}

	@Override
	public Object[] queryObjectByPks(String[] pks) throws BusinessException {
		AggEquipinsp[] bills = null;
		BillQuery<AggEquipinsp> query = new BillQuery<AggEquipinsp>(
				AggEquipinsp.class);
		bills = query.query(pks);
		return bills;
	}
}
