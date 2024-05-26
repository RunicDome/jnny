/*     */ package nc.bs.bd.pfxx.plugin;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import nc.bs.framework.common.NCLocator;
/*     */ import nc.bs.pfxx.ISwapContext;
/*     */ import nc.bs.pfxx.plugin.AbstractPfxxPlugin;
import nc.bs.trade.business.HYPubBO;
/*     */ import nc.itf.bd.supplier.baseinfo.ISupplierBaseInfoService;
/*     */ import nc.md.persist.framework.IMDPersistenceQueryService;
/*     */ import nc.md.persist.framework.IMDPersistenceService;
/*     */ import nc.md.persist.framework.MDPersistenceService;
/*     */ import nc.vo.bd.supplier.SupLinkmanVO;
/*     */ import nc.vo.bd.supplier.SupplierVO;
/*     */ import nc.vo.ml.AbstractNCLangRes;
/*     */ import nc.vo.ml.NCLangRes4VoTransl;
/*     */ import nc.vo.pfxx.auxiliary.AggxsysregisterVO;
/*     */ import nc.vo.pfxx.util.ArrayUtils;
/*     */ import nc.vo.pfxx.util.PfxxPluginUtils;
/*     */ import nc.vo.pub.BusinessException;
/*     */ import nc.vo.pub.SuperVO;
/*     */ import nc.vo.pub.lang.UFBoolean;
/*     */ 
/*     */ public class SupplierPfxxPlugin
/*     */   extends AbstractPfxxPlugin
/*     */ {
/*     */   private IMDPersistenceQueryService mdQryService;
/*     */   private IMDPersistenceService mdService;
/*     */   private ISupplierBaseInfoService basesService;
/*     */   
/*     */   public SupplierPfxxPlugin() {}
/*     */   
/*     */   protected Object processBill(Object vo, ISwapContext swapContext, AggxsysregisterVO aggvo) throws BusinessException
/*     */   {
/*  35 */     SupplierVO supplierVO = (SupplierVO)vo;
/*  36 */     if (!hasUniqueDefaultLinkMan(supplierVO)) {
/*  37 */       throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("bdpub", "0bdpub0060"));
/*     */     }
/*  39 */     String voPk = PfxxPluginUtils.queryBillPKBeforeSaveOrUpdate(swapContext.getBilltype(), swapContext.getDocID());
/*     */     
/*     */ 
String code = (String) new HYPubBO().findColValue("bd_supplier", "code", "nvl(dr,0) = 0 and name='" + supplierVO.getName() +"'");
if(null != code){
	return code;
}
code = (String) new HYPubBO().findColValue("bd_customer", "code", "nvl(dr,0) = 0 and name='" + supplierVO.getName() +"'");
if(null != code){
	return code;
}
/*  42 */     setVOStatus(supplierVO.getSuplinkman(), 2);
/*  43 */     if (voPk == null) {
/*  44 */       supplierVO.setStatus(2);
/*  45 */       supplierVO = getBasesService().pfxxInsertSupplierVO(supplierVO, false);
/*  46 */       voPk = supplierVO.getCode();
/*  47 */       PfxxPluginUtils.addDocIDVsPKContrast(swapContext.getBilltype(), swapContext.getDocID(), voPk);
/*     */     }
/*     */     else {
/*  50 */       supplierVO.setStatus(1);
/*  51 */       setUpdateValues(supplierVO, voPk);
/*  52 */       getBasesService().pfxxUpdateSupplierVO(supplierVO, false);
/*     */     }
/*  54 */     return voPk;
/*     */   }
/*     */   
/*     */   private boolean hasUniqueDefaultLinkMan(SupplierVO supplier) {
/*  58 */     if ((supplier.getSuplinkman() == null) || (supplier.getSuplinkman().length == 0))
/*  59 */       return true;
/*  60 */     int i = 0;
/*  61 */     for (SupLinkmanVO linkMan : supplier.getSuplinkman()) {
/*  62 */       if ((linkMan.getIsdefault() != null) && (linkMan.getIsdefault().booleanValue() == true)) {
/*  63 */         i++;
/*     */       }
/*     */     }
/*  66 */     return i <= 1;
/*     */   }
/*     */   
/*     */   private void setVOStatus(SuperVO[] vos, int status) {
/*  70 */     if (ArrayUtils.isEmpty(vos))
/*  71 */       return;
/*  72 */     for (SuperVO vo : vos)
/*  73 */       vo.setStatus(status);
/*     */   }
/*     */   
/*     */   private void setUpdateValues(SupplierVO updateDocVO, String pk) throws BusinessException {
/*  77 */     Object[] objs = getMdQryService().queryBillOfVOByPKsWithOrder(SupplierVO.class, new String[] { pk }, new String[] { "suplinkman" });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  82 */     if (ArrayUtils.isEmpty(objs)) {
/*  83 */       throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("bdpub", "0bdpub0057"));
/*     */     }
/*  85 */     SupplierVO oldDocVO = (SupplierVO)objs[0];
/*  86 */     updateDocVO.setCreator(oldDocVO.getCreator());
/*  87 */     updateDocVO.setCreationtime(oldDocVO.getCreationtime());
/*  88 */     updateDocVO.setModifier(oldDocVO.getModifier());
/*  89 */     updateDocVO.setModifiedtime(oldDocVO.getModifiedtime());
/*  90 */     updateDocVO.setDataoriginflag(oldDocVO.getDataoriginflag());
/*  91 */     updateDocVO.setPrimaryKey(pk);
/*  92 */     List<SupLinkmanVO> linkManVOList = new ArrayList();
/*  93 */     if (oldDocVO.getSuplinkman() != null) {
/*  94 */       for (SupLinkmanVO linkManVO : oldDocVO.getSuplinkman()) {
/*  95 */         linkManVO.setStatus(3);
/*  96 */         linkManVOList.add(linkManVO);
/*     */       }
/*     */     }
/*  99 */     if (updateDocVO.getSuplinkman() != null) {
/* 100 */       linkManVOList.addAll(Arrays.asList(updateDocVO.getSuplinkman()));
/*     */     }
/* 102 */     updateDocVO.setSuplinkman((SupLinkmanVO[])linkManVOList.toArray(new SupLinkmanVO[0]));
/*     */   }
/*     */   
/*     */   private IMDPersistenceQueryService getMdQryService() {
/* 106 */     if (this.mdQryService == null)
/* 107 */       this.mdQryService = MDPersistenceService.lookupPersistenceQueryService();
/* 108 */     return this.mdQryService;
/*     */   }
/*     */   
/*     */   private ISupplierBaseInfoService getBasesService() {
/* 112 */     if (this.basesService == null)
/* 113 */       this.basesService = ((ISupplierBaseInfoService)NCLocator.getInstance().lookup(ISupplierBaseInfoService.class));
/* 114 */     return this.basesService;
/*     */   }
/*     */   
/*     */   private IMDPersistenceService getMdService() {
/* 118 */     if (this.mdService == null)
/* 119 */       this.mdService = MDPersistenceService.lookupPersistenceService();
/* 120 */     return this.mdService;
/*     */   }
/*     */ }
