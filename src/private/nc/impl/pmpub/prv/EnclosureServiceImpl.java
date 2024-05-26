package nc.impl.pmpub.prv;

import java.util.List;

import nc.bs.trade.business.HYPubBO;
import nc.itf.pmpub.prv.IEnclosureService;
import nc.vo.ct.purdaily.entity.BdFileexattr;
import nc.vo.ct.purdaily.entity.RlPmeFile;
import nc.vo.ct.purdaily.entity.SmPubFilesystem;
import nc.vo.pmfile.documentcenter.DocumentCenterVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;

public class EnclosureServiceImpl implements IEnclosureService {

	@Override
	public void saveEnclosure(Object obj, List<RlPmeFile> rlPmeFileS)
			throws BusinessException {
		AggregatedValueObject aggVO = (AggregatedValueObject) obj;
		String pk_bill = aggVO.getParentVO().getPrimaryKey();
		String bill_no = (String) aggVO.getParentVO().getAttributeValue(
				"vbillcode");
		if (null == bill_no) {
			bill_no = (String) aggVO.getParentVO().getAttributeValue(
					"bill_code");
		}
		String cbilltypecode = (String) aggVO.getParentVO().getAttributeValue(
				"cbilltypecode");
		if (null == cbilltypecode) {
			cbilltypecode = (String) aggVO.getParentVO().getAttributeValue(
					"bill_type");
		}
		String ctrantypeid = (String) aggVO.getParentVO().getAttributeValue(
				"ctrantypeid");
		if (null == ctrantypeid) {
			ctrantypeid = (String) aggVO.getParentVO().getAttributeValue(
					"pk_transitype");
		}
		for (RlPmeFile temp : rlPmeFileS) {
			/* step-1 组装 bd_fileexattr */
			BdFileexattr[] bdFileexattrs = (nc.vo.ct.purdaily.entity.BdFileexattr[]) getHyPubBO()
					.queryByCondition(
							BdFileexattr.class,
							"nvl(dr,0) = 0 and pk_file = '" + temp.getFile_id()
									+ "'");
			if (bdFileexattrs.length == 1) {
				BdFileexattr bdFileexattr = bdFileexattrs[0];
				bdFileexattr.setStatus(VOStatus.NEW);
				bdFileexattr.setPk_billtypecode(cbilltypecode);
				bdFileexattr.setPk_fileexattr(null);
				/* step-2 组装 sm_pub_filesystem */
				SmPubFilesystem smPubFilesystem = (nc.vo.ct.purdaily.entity.SmPubFilesystem) getHyPubBO()
						.queryByPrimaryKey(SmPubFilesystem.class,
								temp.getFile_id());
				smPubFilesystem.setPk(null);
				smPubFilesystem.setFilepath(smPubFilesystem.getFilepath()
						.replace(bdFileexattr.getPk_bill(), pk_bill));
				smPubFilesystem.setStatus(VOStatus.NEW);
				/* step-3 组装 pm_doccenter */
				DocumentCenterVO documentCenterVO = (DocumentCenterVO) getHyPubBO()
						.queryByPrimaryKey(DocumentCenterVO.class,
								bdFileexattr.getPk_exattr());
				documentCenterVO.setPk_doccenter(null);
				documentCenterVO.setStatus(VOStatus.NEW);
				documentCenterVO.setBill_code(bill_no);
				documentCenterVO.setPk_bill(pk_bill);
				documentCenterVO.setPk_transitype(ctrantypeid);
				if (null == documentCenterVO.getPk_project()) {
					documentCenterVO.setPk_project(temp.getProject_id());
				}
				bdFileexattr.setPk_file(getHyPubBO().insert(smPubFilesystem));
				bdFileexattr
						.setPk_exattr(getHyPubBO().insert(documentCenterVO));
				bdFileexattr.setPk_bill(pk_bill);
				getHyPubBO().insert(bdFileexattr);
			}
		}
	}

	private HYPubBO hyPubBO;

	public HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	public void setHyPubBO(HYPubBO hyPubBO) {
		this.hyPubBO = hyPubBO;
	}
}
