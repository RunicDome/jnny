package nc.impl.pcm;

import nc.impl.pub.ace.AcePcmprojectPubServiceImpl;
import nc.itf.pcm.IPcmprojectMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pcm.pcmproject.AggPcmProject;
import nc.vo.pub.BusinessException;

public class PcmprojectMaintainImpl extends AcePcmprojectPubServiceImpl
		implements IPcmprojectMaintain {

	@Override
	public void delete(AggPcmProject[] clientFullVOs,
			AggPcmProject[] originBills) throws BusinessException {
		super.pubdeleteBills(clientFullVOs, originBills);
	}

	@Override
	public AggPcmProject[] insert(AggPcmProject[] clientFullVOs,
			AggPcmProject[] originBills) throws BusinessException {
		return super.pubinsertBills(clientFullVOs, originBills);
	}

	@Override
	public AggPcmProject[] update(AggPcmProject[] clientFullVOs,
			AggPcmProject[] originBills) throws BusinessException {
		return super.pubupdateBills(clientFullVOs, originBills);
	}

	@Override
	public AggPcmProject[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

	@Override
	public AggPcmProject[] save(AggPcmProject[] clientFullVOs,
			AggPcmProject[] originBills) throws BusinessException {
		return super.pubsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggPcmProject[] unsave(AggPcmProject[] clientFullVOs,
			AggPcmProject[] originBills) throws BusinessException {
		return super.pubunsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggPcmProject[] approve(AggPcmProject[] clientFullVOs,
			AggPcmProject[] originBills) throws BusinessException {
		return super.pubapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggPcmProject[] unapprove(AggPcmProject[] clientFullVOs,
			AggPcmProject[] originBills) throws BusinessException {
		return super.pubunapprovebills(clientFullVOs, originBills);
	}

}
