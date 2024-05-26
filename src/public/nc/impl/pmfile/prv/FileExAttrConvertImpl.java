/*     */ package nc.impl.pmfile.prv;
/*     */ 
/*     */ import java.io.Serializable;
import java.util.List;
import java.util.Map;

import nc.bs.pf.pub.PfDataCache;
import nc.bs.pub.filesystem.IFileExAttrConvert;
import nc.itf.pim.project.pub.IProjectServiceForPu;
import nc.itf.pmfile.billdocsetting.prv.IBillDocSetting;
import nc.itf.uap.pf.metadata.IFlowBizItf;
import nc.md.data.access.NCObject;
import nc.md.model.IBusinessEntity;
import nc.uap.pf.metadata.PfMetadataTools;
import nc.vo.bd.meta.IBDObject;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pm.proxy.PMProxy;
import nc.vo.pm.util.app.ModuleInfoQueryUtil;
import nc.vo.pm.util.cache.CacheDataQueryUtil;
import nc.vo.pmbd.common.utils.ExceptionUtils;
import nc.vo.pmfile.billdocsetting.BillDocSettingVO;
import nc.vo.pmfile.documentcenter.DocumentCenterVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.billtype.BilltypeVO;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FileExAttrConvertImpl
/*     */   implements IFileExAttrConvert, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  43 */   private DocumentCenterVO documentCenterVO = new DocumentCenterVO();
/*     */   
/*     */   private SuperVO srcVO;
/*     */   private String pk_billTypeCode;
/*     */   
/*     */   public FileExAttrConvertImpl(SuperVO _srcVO)
/*     */   {
/*  50 */     this.srcVO = _srcVO;
/*     */   }
/*     */   
/*     */   public String getFileTypeCode()
/*     */   {
/*  55 */     return "PM";
/*     */   }
/*     */   
/*     */   public String getBill()
/*     */   {
/*  60 */     return this.srcVO.getPrimaryKey();
/*     */   }
/*     */   
/*     */   public String getBilltypecode()
/*     */   {
	if(null != this.srcVO.getAttributeValue("bill_type")){
		return (String)this.srcVO.getAttributeValue("bill_type");
	}else{
		return (String)this.srcVO.getAttributeValue("cbilltypecode");
	}
/*  65 */     
/*     */   }
/*     */   
/*     */   public Object getFileAttr()
/*     */   {
/*  70 */     initVO();
/*  71 */     return this.documentCenterVO;
/*     */   }
/*     */   
/*     */   private void initVO()
/*     */   {
/*  76 */     getDataFromIBDObject(this.srcVO);
/*     */     
/*  78 */     getDataFromIFlowBizItf(this.srcVO);
/*     */     
/*  80 */     if ((this.documentCenterVO.getPk_billtype() == null) && (this.documentCenterVO.getPk_transitype() == null))
/*     */     {
/*  82 */       getDataFromIBD(this.srcVO);
/*     */     }
/*     */     
/*     */ 
/*  86 */     getDataFromBillSetting();
/*     */     
/*  88 */     getPk_Project();
/*     */   }
/*     */   
/*     */ 
/*     */   private void getDataFromIBD(SuperVO srcVO)
/*     */   {
/*  94 */     String bill_type = (String)srcVO.getAttributeValue("bill_type");
if(null == bill_type){
	bill_type = (String)srcVO.getAttributeValue("cbilltypecode");
}
/*  95 */     String pk_billType = getBillTypeByCode(bill_type);
/*  96 */     String pk_transitype = (String)srcVO.getAttributeValue("pk_transitype");

if(null == pk_transitype){
	pk_transitype = (String)srcVO.getAttributeValue("ctrantypeid");
}
/*     */     
/*  98 */     this.documentCenterVO.setPk_billtype(pk_billType);
/*     */     
/* 100 */     this.documentCenterVO.setPk_transitype(pk_transitype);
/*     */   }
/*     */   
/*     */ 
/*     */   private void getDataFromIBDObject(SuperVO srcVO)
/*     */   {
/* 106 */     NCObject ncObj = NCObject.newInstance(srcVO);
/* 107 */     Map<String, String> flowBizInfoMap = ((IBusinessEntity)ncObj.getRelatedBean()).getBizInterfaceMapInfo(IBDObject.class.getName());
/*     */     
/*     */ 
/*     */ 
/* 111 */     String pk_bill = (String)srcVO.getAttributeValue((String)flowBizInfoMap.get("id"));
/*     */     
/*     */ 
/* 114 */     String bill_code = (String)srcVO.getAttributeValue((String)flowBizInfoMap.get("code"));
/*     */     
/*     */ 
/* 117 */     String pk_org = (String)srcVO.getAttributeValue((String)flowBizInfoMap.get("pk_org"));
/*     */     
/*     */ 
/*     */ 
/* 121 */     String pk_group = (String)srcVO.getAttributeValue((String)flowBizInfoMap.get("pk_group"));
/*     */     
/*     */ 
/* 124 */     this.documentCenterVO.setPk_bill(pk_bill);
/* 125 */     this.documentCenterVO.setBill_code(bill_code);
/* 126 */     this.documentCenterVO.setPk_org(pk_org);
/* 127 */     this.documentCenterVO.setPk_group(pk_group);
/*     */   }
/*     */   
/*     */ 
/*     */   private void getDataFromIFlowBizItf(SuperVO srcVO)
/*     */   {
/* 133 */     IFlowBizItf fbi = (IFlowBizItf)PfMetadataTools.getBizItfImpl(srcVO, IFlowBizItf.class);
/*     */     
/* 135 */     if (null == fbi) {
/* 136 */       return;
/*     */     }
/*     */     
/*     */ 
/* 140 */     this.pk_billTypeCode = fbi.getBilltype();
/* 141 */     String pk_billType = getBillTypeByCode(this.pk_billTypeCode);
/*     */     
/*     */ 
/* 144 */     String pk_transiType = fbi.getTranstypePk();
/* 145 */     if ((null == pk_transiType) || (pk_transiType.equals(""))) {
/* 146 */       String transiType_code = fbi.getTranstype();
/* 147 */       pk_transiType = lookForPKTransiTypeByCode(transiType_code);
/*     */     }
/*     */     
/* 150 */     if ((null == pk_transiType) || (pk_transiType.length() < 20)) {
/* 151 */       pk_transiType = lookForPKTransiTypeByCode(pk_transiType);
/*     */     }
/*     */     
/* 154 */     this.documentCenterVO.setPk_billtype(pk_billType);
/* 155 */     this.documentCenterVO.setPk_transitype(pk_transiType);
/*     */     
/*     */ 
/* 158 */     if (null == this.documentCenterVO.getPk_bill()) {
/* 159 */       this.documentCenterVO.setPk_bill(fbi.getBillId());
/*     */     }
/*     */     
/* 162 */     if (null == this.documentCenterVO.getBill_code()) {
/* 163 */       this.documentCenterVO.setBill_code(fbi.getBillNo());
/*     */     }
/*     */     
/* 166 */     if (null == this.documentCenterVO.getPk_org()) {
/* 167 */       this.documentCenterVO.setPk_org(fbi.getPkorg());
/*     */     }
/*     */   }
/*     */   
/*     */   private void getPk_Project()
/*     */   {
/* 173 */     String pk_project = (String)this.srcVO.getAttributeValue("pk_project");
				if(null == pk_project){
					pk_project = (String)this.srcVO.getAttributeValue("cprojectid");
				}
/* 174 */     this.documentCenterVO.setPk_project(pk_project);
/* 175 */     if (this.pk_billTypeCode == null) {
/* 176 */       return;
/*     */     }
/*     */     
/*     */ 
/* 180 */     if ((this.pk_billTypeCode.equals("4D11")) || (this.pk_billTypeCode.equals("4D12"))) {
/*     */       try
/*     */       {
/* 183 */         if (ModuleInfoQueryUtil.isPIMEnabled()) {
/* 184 */           pk_project = ((IProjectServiceForPu)PMProxy.lookup(IProjectServiceForPu.class)).queryProHeadVOBySrcPK(this.srcVO.getPrimaryKey());
/*     */           
/* 186 */           if (null != pk_project) {
/* 187 */             this.documentCenterVO.setPk_project(pk_project);
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (BusinessException e) {
/* 192 */         ExceptionUtils.asBusinessRuntimeException(NCLangRes4VoTransl.getNCLangRes().getStrByID("projectdoc_0", "04870010-0008"));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void getDataFromBillSetting()
/*     */   {
/* 206 */     SuperVO[] docSettings = ((IBillDocSetting)PMProxy.lookup(IBillDocSetting.class)).querySuperVOByBillType(this.documentCenterVO.getPk_billtype(), this.documentCenterVO.getPk_transitype(), this.documentCenterVO.getPk_org(), this.documentCenterVO.getPk_group());
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 212 */     if ((null == docSettings) || (docSettings.length < 1)) {
/* 213 */       return;
/*     */     }
/*     */     
/* 216 */     BillDocSettingVO vo = (BillDocSettingVO)docSettings[0];
/* 217 */     this.documentCenterVO.setPk_doctype(vo.getPk_doctype());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private String getBillTypeByCode(String code)
/*     */   {
/* 226 */     BilltypeVO billtypeVO = PfDataCache.getBillTypeInfo(code);
/* 227 */     return billtypeVO.getPk_billtypeid();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected String lookForPKTransiTypeByCode(String transiType_Code)
/*     */   {
/* 234 */     String pk_group = this.documentCenterVO.getPk_group();
/* 235 */     List<BilltypeVO> list = CacheDataQueryUtil.getBillTypeListVO(pk_group);
/* 236 */     list = PfDataCache.getBillTypeAndTranstypesInGrp(pk_group);
/*     */     
/* 238 */     for (BilltypeVO bVo : list) {
/* 239 */       if (bVo.getPk_billtypecode().equals(transiType_Code)) {
/* 240 */         return bVo.getPk_billtypeid();
/*     */       }
/*     */     }
/*     */     
/* 244 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SuperVO getSrcVO()
/*     */   {
/* 262 */     return this.srcVO;
/*     */   }
/*     */   
/*     */   public void setSrcVO(SuperVO srcVO) {
/* 266 */     this.srcVO = srcVO;
/*     */   }
/*     */ }

/* Location:           D:\NC\jnrl\home\modules\pmfile\lib\pubpmfile_projectdoc.jar
 * Qualified Name:     nc.impl.pmfile.prv.FileExAttrConvertImpl
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.0.1
 */