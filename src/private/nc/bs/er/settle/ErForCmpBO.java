 package nc.bs.er.settle;
 
 import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import nc.bs.arap.bx.BXBusItemBO;
import nc.bs.arap.bx.BXZbBO;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.dao.BaseDAO;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.cmp.busi.ISettleNotifyPayTypeBusiBillService;
import nc.itf.cmp.settlement.ISettlement;
import nc.pubitf.fip.service.IFipBillQueryService;
import nc.vo.cmp.BusiInfo;
import nc.vo.cmp.BusiStateTrans;
import nc.vo.cmp.BusiStatus;
import nc.vo.cmp.CMPExecStatus;
import nc.vo.cmp.NetPayExecInfo;
import nc.vo.cmp.ReturnBill4BusiVO;
import nc.vo.cmp.fts.MoneyDetail;
import nc.vo.cmp.settlement.CmpMsg;
import nc.vo.cmp.settlement.SettlementAggVO;
import nc.vo.cmp.settlement.SettlementBatchOperateVO;
import nc.vo.cmp.settlement.SettlementBodyVO;
import nc.vo.cmp.settlement.batch.BusiStateChangeVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.VOFactory;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.settle.SettleUtil;
import nc.vo.erm.accruedexpense.AccruedVerifyVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
 
// 借款报销结算
 @SuppressWarnings({ "restriction", "unused", "unchecked", "rawtypes" })
public class ErForCmpBO implements ISettleNotifyPayTypeBusiBillService
 {
   private final BXZbBO bo = new BXZbBO();
   private JKBXVO jkbxVO = null;
   
   public ErForCmpBO() {}
   
   public void billStateChange(BusiInfo busiInfo, BusiStateTrans trans) throws BusinessException { if ((BusiStatus.Audit.equals(trans.getFrom())) && (BusiStatus.Sign.equals(trans.getTo())))
     {
       this.jkbxVO = getBxVO(busiInfo, trans);
       this.bo.signVo(busiInfo.getOperator(), busiInfo.getOperatorDate(), this.jkbxVO);
     } else if ((BusiStatus.Sign.equals(trans.getFrom())) && (BusiStatus.Audit.equals(trans.getTo())))
     {
       this.jkbxVO = getBxVO(busiInfo, trans);
       this.bo.unSignVo(new JKBXVO[] { this.jkbxVO });
     }
   }
   
   public void effectStateChange(BusiInfo busiInfo, BusiStateTrans trans) throws BusinessException
   {
     JKBXVO jkbxVo = getBxVO(busiInfo, trans);
     String flag = null;
     if (BusiStatus.Effet.equals(trans.getTo())) {
       flag = BXZbBO.MESSAGE_SETTLE;
       
 
       DjLXVO djlxvo = ErmDjlxCache.getInstance().getDjlxVO(jkbxVo.getParentVO().getPk_group(), jkbxVo.getParentVO().getDjlxbm());
       if ((djlxvo != null) && (djlxvo.getIsqr() != null) && (djlxvo.getIsqr().booleanValue())) {
         jkbxVo.setHasNtbCheck(true);
       }
       
       this.bo.effectVo(jkbxVo);
       
 
       if ((djlxvo != null) && (djlxvo.getIsqr() != null) && (djlxvo.getIsqr().booleanValue())) {
         busiInfo.setReturnMsg(jkbxVo.getWarningMsg());
         jkbxVo.setWarningMsg(null);
       }
     } else if (BusiStatus.EffectNever.equals(trans.getTo())) {
       flag = BXZbBO.MESSAGE_UNSETTLE;
       this.bo.unEffectVos(new JKBXVO[] { jkbxVo });
     }
     
     if ((BXZbBO.MESSAGE_SETTLE.equals(flag)) && (!SettleUtil.isJsToFip(jkbxVo.getParentVO())) && (
       (jkbxVo.getParentVO().getVouchertag() == null) || (jkbxVo.getParentVO().getVouchertag().intValue() == 1))) {
       jkbxVo.getParentVO().setVouchertag(Integer.valueOf(1));
       this.bo.updateHeaders(new JKBXHeaderVO[] { jkbxVo.getParentVO() }, new String[] { "vouchertag" });
       this.bo.effectToFip(Arrays.asList(new JKBXVO[] { jkbxVo }), flag);
     }
     
 
 
     if (BXZbBO.MESSAGE_UNSETTLE.equals(flag)) {
       if ((jkbxVo.getParentVO().getVouchertag() != null) && (
         (jkbxVo.getParentVO().getVouchertag().intValue() == 0) || (jkbxVo.getParentVO().getVouchertag().intValue() == 1)))
       {
         this.bo.effectToFip(Arrays.asList(new JKBXVO[] { jkbxVo }), flag);
       }
       
       jkbxVo.getParentVO().setVouchertag(null);
       this.bo.updateHeaders(new JKBXHeaderVO[] { jkbxVo.getParentVO() }, new String[] { "vouchertag" });
     }
   }
   
 
public void execStatuesChange(BusiInfo busiInfo, CMPExecStatus status)
     throws BusinessException
   {
     List<JKBXHeaderVO> voList = new ArrayList();
     List<JKBXHeaderVO> vos = this.bo.queryHeadersByPrimaryKeys(new String[] { busiInfo.getPk_bill() }, getDJDL(busiInfo));
     for (JKBXHeaderVO vo : vos) {
       vo.setPayflag(Integer.valueOf(status.getStatus()));
       if (status.getStatus() == 3) {
         vo.setPayman(busiInfo.getOperator());
         vo.setPaydate(busiInfo.getOperatorDate());
       } else {
         vo.setPayman(null);
         vo.setPaydate(null);
       }
       voList.add(vo);
     }
     
 
     if ((((JKBXHeaderVO)voList.get(0)).getPayflag().intValue() == 3) && 
       (SettleUtil.isJsToFip((JKBXHeaderVO)voList.get(0)))) {
       List<JKBXVO> jkbxvoList = ((IBXBillPrivate)NCLocator.getInstance().lookup(IBXBillPrivate.class)).retriveItems(voList);
       forwardToFip(BXZbBO.MESSAGE_SETTLE, jkbxvoList);
       
       voList.clear();
       for (JKBXVO jkbxVo : jkbxvoList) {
         voList.add(jkbxVo.getParentVO());
       }
     }
     
 
 
     this.bo.updateHeaders((JKBXHeaderVO[])voList.toArray(new JKBXHeaderVO[0]), new String[] {
       "payflag", "payman", "paydate", "vouchertag" });
   }
   
 
 
   public void setoffRed(NetPayExecInfo payInfo, Map<String, SettlementBodyVO[]> value)
     throws BusinessException
   {
     if ((payInfo != null) && (value != null)) {
       ((IBXBillPrivate)NCLocator.getInstance().lookup(IBXBillPrivate.class)).settleRedHandleSaveAndSign(payInfo, value);
     }
   }
   
 
 
 
 
 
   private boolean isExistVourcher(JKBXHeaderVO headerVO, String relationId)
     throws BusinessException
   {
     FipRelationInfoVO srcinfovo = new FipRelationInfoVO();
     srcinfovo.setPk_group(headerVO.getPk_group());
     srcinfovo.setPk_org(headerVO.getPk_payorg());
     if ((relationId != null) && (relationId.trim().length() > 0)) {
       srcinfovo.setRelationID(relationId);
     } else {
       srcinfovo.setRelationID(this.jkbxVO.getParentVO().getPk());
     }
     srcinfovo.setPk_billtype(headerVO.getDjlxbm());
     IFipBillQueryService ip = (IFipBillQueryService)NCLocator.getInstance().lookup(IFipBillQueryService.class);
     nc.vo.fip.external.FipExtendAggVO[] datavos = ip.queryDesBillBySrc(new FipRelationInfoVO[] { srcinfovo }, null);
     
     if ((datavos != null) && (datavos.length > 0)) {
       return true;
     }
     return false;
   }
   
   private JKBXHeaderVO getBxHeaderVO(BusiInfo busiInfo)
     throws BusinessException
   {
     JKBXHeaderVO head = null;
     
     if (busiInfo.getRawBill() == null) {
       List<JKBXHeaderVO> vos = this.bo.queryHeadersByPrimaryKeys(new String[] { busiInfo.getPk_bill() }, getDJDL(busiInfo));
       
       if ((vos != null) && (vos.size() > 0))
         head = (JKBXHeaderVO)vos.get(0);
     } else {
       head = ((JKBXVO)busiInfo.getRawBill()).getParentVO();
     }
     return head;
   }
   
 
 
 
 
 
 
   private JKBXVO getBxVO(BusiInfo busiInfo, BusiStateTrans trans)
     throws BusinessException
   {
     JKBXHeaderVO head = null;
     JKBXVO rawbill = null;
     if (busiInfo.getRawBill() == null) {
       List<JKBXHeaderVO> vos = this.bo.queryHeadersByPrimaryKeys(new String[] { busiInfo.getPk_bill() }, getDJDL(busiInfo));
       if ((vos != null) && (vos.size() > 0)) {
         head = (JKBXHeaderVO)vos.get(0);
       }
     } else {
       rawbill = (JKBXVO)busiInfo.getRawBill();
       head = rawbill.getParentVO();
     }
     if (head == null) {
       return null;
     }
     
     nc.vo.ep.bx.BXBusItemVO[] queryBxFinItemVO = new BXBusItemBO().queryByHeaders(new JKBXHeaderVO[] { head });
     JKBXVO bxvo = VOFactory.createVO(head, queryBxFinItemVO);
     
 
     Collection<BxcontrastVO> collection = this.bo.queryContrasts(head);
     bxvo.setContrastVO((BxcontrastVO[])collection.toArray(new BxcontrastVO[0]));
     
 
     Collection<CShareDetailVO> cShares = this.bo.queryCSharesVOS(new JKBXHeaderVO[] { bxvo.getParentVO() });
     bxvo.setcShareDetailVo((CShareDetailVO[])cShares.toArray(new CShareDetailVO[0]));
     
 
     Collection<AccruedVerifyVO> accruedVerifyVOs = this.bo.queryAccruedVerifyVOS(new JKBXHeaderVO[] { bxvo.getParentVO() });
     bxvo.setAccruedVerifyVO((AccruedVerifyVO[])accruedVerifyVOs.toArray(new AccruedVerifyVO[0]));
     
     if (rawbill != null) {
       boolean hasNtbCheck = rawbill.getHasNtbCheck();
       boolean hasZjjhCheck = rawbill.getHasZjjhCheck();
       boolean hasJkCheck = rawbill.getHasJkCheck();
       
       boolean hasZjjhCheck2 = busiInfo.isHasZjjhCheck();
       boolean budgetCheck = busiInfo.isBudgetCheck();
       boolean hasJkCheck2 = busiInfo.isJkCheck();
       
       bxvo.setHasZjjhCheck((hasZjjhCheck) || (hasZjjhCheck2));
       bxvo.setHasNtbCheck((hasNtbCheck) || (budgetCheck));
       bxvo.setHasJkCheck((hasJkCheck) || (hasJkCheck2));
       
       bxvo.authList = rawbill.authList;
     } else {
       boolean hasZjjhCheck2 = busiInfo.isHasZjjhCheck();
       boolean budgetCheck = busiInfo.isBudgetCheck();
       
       bxvo.setHasZjjhCheck(hasZjjhCheck2);
       bxvo.setHasNtbCheck(budgetCheck);
       bxvo.setHasJkCheck(busiInfo.isJkCheck());
     }
     
     if (trans != null) {
       bxvo.setSettlementMap(trans.getDetailMap());
     }
     return bxvo;
   }
   
   public boolean checkCancelEffect(BusiInfo info) throws BusinessException
   {
     return true;
   }
   
   public boolean checkCancelSign(BusiInfo info) throws BusinessException
   {
     return true;
   }
   
   public void coerceDelete(BusiInfo busiInfo) throws BusinessException
   {
     JKBXHeaderVO head = getBxHeaderVO(busiInfo);
     this.bo.delete(new JKBXVO[] { VOFactory.createVO(head) });
   }
   
   public AggregatedValueObject getBillVO(BusiInfo info) throws BusinessException
   {
     return getBxVO(info);
   }
   
   private AggregatedValueObject getBxVO(BusiInfo info) throws BusinessException {
     return getBxVO(info, null);
   }
   
   public boolean isAutoFillEbankInfo(String pk_corp, String pk_billtype) throws BusinessException {
     return false;
   }
   
 
 
 
 
 
 
 
   private Integer getPayStatus(NetPayExecInfo payInfo)
     throws BusinessException
   {
     Integer retStatus = Integer.valueOf(CMPExecStatus.UNPayed.getStatus());
     if (payInfo.getExecStatusMap() == null) {
       throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0", "02011v61013-0027"));
     }
     
 
 
     CMPExecStatus[] status = (CMPExecStatus[])payInfo.getExecStatusMap().values().toArray(new CMPExecStatus[0]);
     boolean isFinished = false;
     boolean isFailed = false;
     for (int i = 0; i < status.length; i++) {
       if (status[i] == CMPExecStatus.SomePayFinish)
         return Integer.valueOf(CMPExecStatus.SomePayFinish.getStatus());
       if (status[i] == CMPExecStatus.PayFinish) {
         isFinished = true;
         if (isFailed) {
           retStatus = Integer.valueOf(CMPExecStatus.SomePayFinish.getStatus());
           break;
         }
       } else if (status[i] == CMPExecStatus.PayFail) {
         isFailed = true;
         if (isFinished) {
           retStatus = Integer.valueOf(CMPExecStatus.SomePayFinish.getStatus());
           break;
         }
       } else if (status[i] == CMPExecStatus.Paying) {
         if (isFailed) {
           retStatus = Integer.valueOf(CMPExecStatus.SomePayFinish.getStatus());
           break;
         }
         retStatus = Integer.valueOf(CMPExecStatus.Paying.getStatus());
       }
     }
     
 
     if (retStatus.intValue() == CMPExecStatus.Paying.getStatus()) {
       return retStatus;
     }
     
     if (((isFinished) && (!isFailed)) || ((isFinished) && (isFailed))) {
       retStatus = Integer.valueOf(CMPExecStatus.PayFinish.getStatus());
     } else if ((isFailed) && (!isFinished)) {
       retStatus = Integer.valueOf(CMPExecStatus.PayFail.getStatus());
     }
     return retStatus;
   }
   
   public void netPayExecChange(NetPayExecInfo payInfo) throws BusinessException
   {
     List<JKBXHeaderVO> vos = this.bo.queryHeadersByPrimaryKeys(new String[] { payInfo.getBillid() }, payInfo.getBilltype());
     JKBXHeaderVO head = (JKBXHeaderVO)vos.get(0);
     
 
     head.setPayflag(getPayStatus(payInfo));
     head.setPaydate(payInfo.getOperateDate());
     head.setPayman(payInfo.getOperator());
     
 
     String settleno = payInfo.getSettleno();
     head.setJsh(settleno);
     String djlxbm = payInfo.getBilltype();
     
     if ((djlxbm == null) || (djlxbm.length() == 0)) {
       throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0", "02011v61013-0028"));
     }
     
 
 
 
     try
     {
       head.setDjlxbm(djlxbm);
       head.setDjdl(getDJDL(djlxbm));
       
       List<JKBXHeaderVO> voList = new ArrayList();
       voList.add(head);
       
 
       if ((head.getPayflag().intValue() == 3) && 
         (SettleUtil.isJsToFip(head))) {
         List<JKBXVO> jkbxvoList = ((IBXBillPrivate)NCLocator.getInstance().lookup(IBXBillPrivate.class)).retriveItems(voList);
         forwardToFip(BXZbBO.MESSAGE_SETTLE, jkbxvoList);
         
         voList.clear();
         for (JKBXVO jkbxVo : jkbxvoList) {
           voList.add(jkbxVo.getParentVO());
         }
       }
       
       new BaseDAO().updateVOArray((SuperVO[])voList.toArray(new JKBXHeaderVO[0]), new String[] { "payflag", "paydate", "payman", "jsh", 
         "vouchertag" });
       EventDispatcher.fireEvent(new ErmBusinessEvent("d9b9f860-4dc7-47fa-a7d5-7a5d91f39290", "ERM1050", vos.toArray(new JKBXHeaderVO[vos.size()])));
     } catch (Exception e) {
       throw ExceptionHandler.handleException(e);
     }
   }
   
 
 
 
 
 
 
   public void invokeCmp(JKBXVO jkbxVo, UFDate date, BusiStatus busiStatus)
     throws BusinessException
   {
     JKBXHeaderVO head = jkbxVo.getParentVO();
     // 工程费用类合同不进结算(XBX) 264X-Cxx-GCFYLHT
     if (head.isAdjustBxd() || "264X-Cxx-GCFYLHT".equals(head.getDjlxbm()))
     {
       return;
     }
     if (SettleUtil.hasSettleInfo(head, head.getDjdl())) {
       ISettlement settle = (ISettlement)NCLocator.getInstance().lookup(ISettlement.class);
       DjLXVO djlxvo = ErmDjlxCache.getInstance().getDjlxVO(jkbxVo.getParentVO().getPk_group(), jkbxVo.getParentVO().getDjlxbm());
       
       SettlementBatchOperateVO batchoperateVOs = new SettlementBatchOperateVO();
       batchoperateVOs.setBusibill(jkbxVo);
       batchoperateVOs.setMsg(SettleUtil.getCmpMsg(jkbxVo, djlxvo, date, busiStatus));
       batchoperateVOs.setSettlementAgg(jkbxVo.getSettlementInfo());
       if (busiStatus == BusiStatus.Save) {
         if (head.getDjzt().intValue() == 1)
         {
 
           if ((jkbxVo.getBxoldvo() != null) && 
             (!jkbxVo.getParentVO().getPk_payorg().equals(jkbxVo.getBxoldvo().getParentVO().getPk_payorg())) && 
             ((jkbxVo.getBxoldvo().getParentVO().getHkybje().compareTo(UFDouble.ZERO_DBL) != 0) || 
             (jkbxVo.getBxoldvo().getParentVO().getZfybje().compareTo(UFDouble.ZERO_DBL) != 0)) && 
             (jkbxVo.getBxoldvo().getParentVO().getDjzt().intValue() == 1)) {
             settle.notifySettlementBatchDelete(new CmpMsg[] { SettleUtil.getCmpMsg(jkbxVo.getBxoldvo(), djlxvo, date, busiStatus) });
           }
           settle.notifySettlementBatchSave(new SettlementBatchOperateVO[] { batchoperateVOs });
         } else {
           settle.notifySettlementBatchReserveAudit(new CmpMsg[] { SettleUtil.getCmpMsg(jkbxVo, djlxvo, date, busiStatus) });
         }
       } else if (busiStatus == BusiStatus.Tempeorary) {
         settle.notifySettlementBatchTempSave(new SettlementBatchOperateVO[] { batchoperateVOs });
       } else if (busiStatus == BusiStatus.Deleted) {
         settle.notifySettlementBatchDelete(new CmpMsg[] { SettleUtil.getCmpMsg(jkbxVo, djlxvo, date, busiStatus) });
       } else {
         settle.notifySettlementBatchAudit(new CmpMsg[] { SettleUtil.getCmpMsg(jkbxVo, djlxvo, date, busiStatus) });
       }
     }
   }
   
   public void notify4HandSettle(List<String> idList, boolean isOpp, UFDate operateDate, String operator) throws BusinessException {
     if ((idList == null) || (idList.size() == 0))
       return;
     List<JKBXHeaderVO> voList = new ArrayList();
     
     String flag = !isOpp ? BXZbBO.MESSAGE_SETTLE : BXZbBO.MESSAGE_UNSETTLE;
     JKBXHeaderVO head = null;
     List<JKBXHeaderVO> name; for (String id : idList) {
       name = new BXZbBO().queryHeadersByPrimaryKeys(new String[] { id }, "bx");
       if ((name == null) || (name.size() == 0)) {
         name = new BXZbBO().queryHeadersByPrimaryKeys(new String[] { id }, "jk");
       }
       
       head = (JKBXHeaderVO)name.get(0);
       head.setPk_jkbx(id);
       head.setPayflag(Integer.valueOf(isOpp ? CMPExecStatus.UNPayed.getStatus() : CMPExecStatus.PayFinish.getStatus()));
       head.setPaydate(isOpp ? null : operateDate);
       head.setPayman(isOpp ? null : operator);
       voList.add(head);
     }
     
 
     if (SettleUtil.isJsToFip(head))
     {
       if (isOpp)
       {
         List<JKBXVO> jkbxvoList = ((IBXBillPrivate)NCLocator.getInstance().lookup(IBXBillPrivate.class)).retriveItems(voList);
         
         backToFip(flag, jkbxvoList);
         
         voList.clear();
         for (JKBXVO jkbxVo : jkbxvoList) {
           voList.add(jkbxVo.getParentVO());
         }
       }
     }
     new BaseDAO().updateVOArray((SuperVO[])voList.toArray(new JKBXHeaderVO[0]), new String[] { "payflag", "paydate", 
       "payman", "vouchertag" });
     
     if (isOpp) {
       EventDispatcher.fireEvent(new ErmBusinessEvent("d9b9f860-4dc7-47fa-a7d5-7a5d91f39290", "ERM1051", voList.toArray(new JKBXHeaderVO[voList.size()])));
     } else {
       EventDispatcher.fireEvent(new ErmBusinessEvent("d9b9f860-4dc7-47fa-a7d5-7a5d91f39290", "ERM1050", voList.toArray(new JKBXHeaderVO[voList.size()])));
     }
   }
   
 
 
 
 
 
   private void forwardToFip(String flag, List<JKBXVO> jkbxvo)
     throws BusinessException
   {
     JKBXHeaderVO jkbxHeadVO = ((JKBXVO)jkbxvo.get(0)).getParentVO();
     if (jkbxHeadVO.getVouchertag() == null) {
       jkbxHeadVO.setVouchertag(Integer.valueOf(2));
     } else if (jkbxHeadVO.getVouchertag().intValue() == 0) {
       jkbxHeadVO.setVouchertag(Integer.valueOf(3));
     }
     
     Integer voucherTag = jkbxHeadVO.getVouchertag();
     
     if ((voucherTag != null) && (voucherTag.intValue() != 1))
     {
       if (!isExistVourcher(jkbxHeadVO, jkbxHeadVO.getPk() + "_" + voucherTag)) {
         this.bo.effectToFip(jkbxvo, flag);
       }
     }
   }
   
 
 
 
 
 
   private void backToFip(String flag, List<JKBXVO> jkbxvo)
     throws BusinessException
   {
     if ((((JKBXVO)jkbxvo.get(0)).getParentVO().getVouchertag() == null) || (
       (((JKBXVO)jkbxvo.get(0)).getParentVO().getVouchertag() != null) && (((JKBXVO)jkbxvo.get(0)).getParentVO().getVouchertag().intValue() != 1))) {
       this.bo.effectToFip(jkbxvo, flag);
     }
     if (((JKBXVO)jkbxvo.get(0)).getParentVO().getVouchertag().intValue() == 2) {
       ((JKBXVO)jkbxvo.get(0)).getParentVO().setVouchertag(null);
     } else if (((JKBXVO)jkbxvo.get(0)).getParentVO().getVouchertag().intValue() == 3) {
       ((JKBXVO)jkbxvo.get(0)).getParentVO().setVouchertag(Integer.valueOf(0));
     }
   }
   
   public List<SettlementBodyVO> autoBX(List<SettlementBodyVO> bodyList) throws BusinessException {
     return bodyList;
   }
   
   public List<SettlementBodyVO> autoUsed(List<SettlementBodyVO> bodyList) throws BusinessException {
     return bodyList;
   }
   
   public List<String> getFromNoticeBill(List<String> idList) throws BusinessException {
     return null;
   }
   
   public boolean isAutoFillEbankInfo(String arg0, String arg1, String arg2) throws BusinessException
   {
     return false;
   }
   
 
 
 
   public void writeBackInnerStatus(boolean isTransfer, SettlementAggVO... aggVOs)
     throws BusinessException
   {}
   
 
 
 
   private static enum ECommitFtsType
   {
     COMMIT,  CANCEL_COMMIT;
   }
   
 
 
   public void notifyPayTypeBillCommitToFts(BusiInfo... busiInfos)
     throws BusinessException
   {
     notifyErmCommitToFts(ECommitFtsType.COMMIT, busiInfos);
   }
   
 
 
   public void notifyPayTypeBillCancelCommitToFts(BusiInfo... busiInfos)
     throws BusinessException
   {
     notifyErmCommitToFts(ECommitFtsType.CANCEL_COMMIT, busiInfos);
   }
   
 
 
 
 
 
   private void notifyErmCommitToFts(ECommitFtsType type, BusiInfo... busiInfos)
     throws BusinessException
   {
     List<String> pk_billList = new ArrayList();
     
     for (BusiInfo busiInfo : busiInfos) {
       pk_billList.add(busiInfo.getPk_bill());
     }
     List<JKBXHeaderVO> vos = this.bo.queryHeadersByPrimaryKeys((String[])pk_billList.toArray(new String[0]), 
       getDJDL(busiInfos[0]));
     for (JKBXHeaderVO vo : vos) {
       vo.setPayflag(
         Integer.valueOf(ECommitFtsType.CANCEL_COMMIT.equals(type) ? CMPExecStatus.UNPayed.getStatus() : CMPExecStatus.Paying.getStatus()));
     }
     
     this.bo.updateHeaders((JKBXHeaderVO[])vos.toArray(new JKBXHeaderVO[0]), new String[] { "payflag" });
   }
   
   public List<nc.vo.cmp.ReturnBillRetDetail> processReturnBill(ReturnBill4BusiVO bill4BusiVO)
     throws BusinessException
   {
     return null;
   }
   
 
   public void notifyPayTypeBillFtsRefuseDeal(BusiInfo... busiInfos)
     throws BusinessException
   {}
   
 
   public void notifyPayTypeBillInnertansferCancelForcePay(BusiInfo... busiInfos)
     throws BusinessException
   {}
   
   public void notifyPayTypeBillInnertansferForcePay(BusiInfo... busiInfos)
     throws BusinessException
   {}
   
   public void notifyPayTypeBillInnertansferRefuseCommisionPay(BusiInfo... busiInfos)
     throws BusinessException
   {}
   
   public void notifyPayTypeBillInnertansferSuccessAndEffect(BusiStateChangeVO... busiStateChangeVOs)
     throws BusinessException
   {}
   
   public void notifyPayTypeBillCancelInnertansferAndCancelEffect(BusiStateChangeVO... busiStateChangeVOs)
     throws BusinessException
   {}
   
   public boolean isAutoSettle(String pk_group, String pk_tradetype, SettlementAggVO... settlementAggVOs)
     throws BusinessException
   {
     boolean isAutoSettle = false;
     DjLXVO typeVO = ErmDjlxCache.getInstance().getDjlxVO(pk_group, pk_tradetype);
     if (typeVO == null) {
       return false;
     }
     isAutoSettle = typeVO.isAutoSettle();
     
     return isAutoSettle;
   }
   
   public void billChange(BusiInfo busiInfo, Map<String, MoneyDetail> value) throws BusinessException
   {}
   
   private String getDJDL(BusiInfo busiInfo)
   {
     String billtype = busiInfo.getBill_type();
     return getDJDL(billtype);
   }
   
   private String getDJDL(String billtype)
   {
     if (billtype.startsWith("263"))
       return "jk";
     if (billtype.startsWith("264")) {
       return "bx";
     }
     return null;
   }
 }