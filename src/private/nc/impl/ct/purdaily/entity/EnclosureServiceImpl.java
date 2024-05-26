package nc.impl.ct.purdaily.entity;

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
		String bill_no = (String) aggVO.getParentVO().getAttributeValue("vbillcode");
		String cbilltypecode = (String) aggVO.getParentVO().getAttributeValue("cbilltypecode");
		String ctrantypeid = (String) aggVO.getParentVO().getAttributeValue("ctrantypeid");
		for(RlPmeFile temp : rlPmeFileS){
			/*step-1 组装 bd_fileexattr*/
			BdFileexattr[] bdFileexattrs = (nc.vo.ct.purdaily.entity.BdFileexattr[]) getHyPubBO().queryByCondition(BdFileexattr.class, "nvl(dr,0) = 0 and pk_file = '" + temp.getFile_id() + "'");
			if(bdFileexattrs.length == 1){
				BdFileexattr bdFileexattr = bdFileexattrs[0];
				bdFileexattr.setStatus(VOStatus.NEW);
				bdFileexattr.setPk_billtypecode(cbilltypecode);
				bdFileexattr.setPk_fileexattr(null);
				/*step-2 组装 sm_pub_filesystem */
				SmPubFilesystem smPubFilesystem =  (nc.vo.ct.purdaily.entity.SmPubFilesystem) getHyPubBO().queryByPrimaryKey(SmPubFilesystem.class, temp.getFile_id());
				smPubFilesystem.setPk(null);
				smPubFilesystem.setFilepath(smPubFilesystem.getFilepath().replace(bdFileexattr.getPk_bill(), pk_bill));
				smPubFilesystem.setStatus(VOStatus.NEW);
				/*step-3 组装 pm_doccenter*/
				DocumentCenterVO documentCenterVO  = (DocumentCenterVO) getHyPubBO().queryByPrimaryKey(DocumentCenterVO.class, bdFileexattr.getPk_exattr());
				documentCenterVO.setPk_doccenter(null);
				documentCenterVO.setStatus(VOStatus.NEW);
				documentCenterVO.setBill_code(bill_no);
				documentCenterVO.setPk_bill(pk_bill);
				documentCenterVO.setPk_transitype(ctrantypeid);
				bdFileexattr.setPk_file(getHyPubBO().insert(smPubFilesystem));
				bdFileexattr.setPk_exattr(getHyPubBO().insert(documentCenterVO));
				bdFileexattr.setPk_bill(pk_bill);
				getHyPubBO().insert(bdFileexattr);
			}
		}
	}
	
	private void saveRlPmeFile(RlPmeFile temp, String vtrantypecode,
			String pk_bill)throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	private void saveRlPmeFile(RlPmeFile temp, String vtrantypecode)throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	private void saveBdFileexattr(String pkFileNodeVO,
			String pkDocumentCenterBillVO, String pk_bill, String cbilltypecode)throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	private String saveDocumentCenterBillVO(String bill_no, RlPmeFile temp, String ctrantypeid) throws BusinessException {
		DocumentCenterVO documentCenterVO  = (DocumentCenterVO) getHyPubBO().queryByPrimaryKey(DocumentCenterVO.class, bill_no);
		return null;
	}

	private String saveFileNodeVO(RlPmeFile temp, String pk_bill)throws BusinessException{
		
		return null;
	}
	
	private HYPubBO hyPubBO;

	public HYPubBO getHyPubBO() {
		if(null == hyPubBO){
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	public void setHyPubBO(HYPubBO hyPubBO) {
		this.hyPubBO = hyPubBO;
	}

}
