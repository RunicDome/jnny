package nc.impl.pcm;

import nc.impl.pub.ace.AcePcmyearplanPubServiceImpl;
import nc.itf.pcm.IPcmyearplanMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pcm.pcmyearplan.AggPcmYearplan;
import nc.vo.pub.BusinessException;

public class PcmyearplanMaintainImpl extends AcePcmyearplanPubServiceImpl
		implements IPcmyearplanMaintain {

	@Override
	public void delete(AggPcmYearplan[] clientFullVOs,
			AggPcmYearplan[] originBills) throws BusinessException {
		super.pubdeleteBills(clientFullVOs, originBills);
	}

	@Override
	public AggPcmYearplan[] insert(AggPcmYearplan[] clientFullVOs,
			AggPcmYearplan[] originBills) throws BusinessException {
		return super.pubinsertBills(clientFullVOs, originBills);
	}

	@Override
	public AggPcmYearplan[] update(AggPcmYearplan[] clientFullVOs,
			AggPcmYearplan[] originBills) throws BusinessException {
		return super.pubupdateBills(clientFullVOs, originBills);
	}

	@Override
	public AggPcmYearplan[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

	@Override
	public AggPcmYearplan[] save(AggPcmYearplan[] clientFullVOs,
			AggPcmYearplan[] originBills) throws BusinessException {
		return super.pubsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggPcmYearplan[] unsave(AggPcmYearplan[] clientFullVOs,
			AggPcmYearplan[] originBills) throws BusinessException {
		return super.pubunsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggPcmYearplan[] approve(AggPcmYearplan[] clientFullVOs,
			AggPcmYearplan[] originBills) throws BusinessException {
		return super.pubapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggPcmYearplan[] unapprove(AggPcmYearplan[] clientFullVOs,
			AggPcmYearplan[] originBills) throws BusinessException {
		return super.pubunapprovebills(clientFullVOs, originBills);
	}

}
