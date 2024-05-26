package nc.impl.hi.entrymng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pub.pf.CheckStatusCallbackContext;
import nc.bs.pub.pf.ICheckStatusCallback;
import nc.bs.pub.pflock.PfBusinessLock;
import nc.bs.pub.pflock.VOConsistenceCheck;
import nc.bs.pub.pflock.VOLockData;
import nc.bs.pub.pflock.VOsConsistenceCheck;
import nc.bs.pub.pflock.VOsLockData;
import nc.bs.sec.esapi.NCESAPI;
import nc.bs.trade.business.HYPubBO;
import nc.bs.uif2.validation.DefaultValidationService;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.BillCodeHelper;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.hi.IPsndocService;
import nc.itf.hi.entrymng.IEntrymngManageService;
import nc.itf.hi.entrymng.IEntrymngQueryService;
import nc.itf.hr.IHRLicenseChecker;
import nc.itf.hr.frame.IHrBillCode;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.frame.IPersistenceUpdate;
import nc.itf.hr.message.IHRMessageSend;
import nc.itf.hr.notice.INotice;
import nc.itf.hr.pf.IHrPf;
import nc.itf.hrp.psnbudget.IOrgBudgetQueryService;
import nc.itf.om.IAOSQueryService;
import nc.itf.uap.pf.IPFWorkflowQry;
import nc.itf.uap.pf.IplatFormEntry;
import nc.itf.uap.pf.metadata.IFlowBizItf;
import nc.md.data.access.NCObject;
import nc.message.util.MessageCenter;
import nc.message.vo.MessageVO;
import nc.message.vo.NCMessage;
import nc.pub.billcode.itf.IBillcodeManage;
import nc.pub.billcode.vo.BillCodeContext;
import nc.pub.tools.HiCacheUtils;
import nc.pub.tools.HiSQLHelper;
import nc.pub.tools.VOUtils;
import nc.pubitf.para.SysInitQuery;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.psn.PsnClVO;
import nc.vo.hi.entrymng.AggEntryapplyVO;
import nc.vo.hi.entrymng.EntryapplyVO;
import nc.vo.hi.entrymng.EntrymngConst;
import nc.vo.hi.entrymng.HiSendMsgHelper;
import nc.vo.hi.entrymng.ValidBudgetResultVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.pub.BillCodeRepeatBusinessException;
import nc.vo.hi.trnstype.TrnstypeFlowVO;
import nc.vo.hr.message.HRBusiMessageVO;
import nc.vo.hr.notice.NoticeTempletVO;
import nc.vo.hrp.psnorgbudget.ValidateResultVO;
import nc.vo.om.job.JobVO;
import nc.vo.om.post.PostVO;
import nc.vo.org.DeptVO;
import nc.vo.org.OrgVO;
import nc.vo.pf.change.IActionDriveChecker;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.sm.UserVO;
import nc.vo.ta.leave.LeavehVO;
import nc.vo.ta.overtime.AggOvertimeVO;
import nc.vo.trade.pub.HYBillVO;
import nc.vo.uif2.LoginContext;
import nc.vo.wfengine.definition.WorkflowTypeEnum;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
//入职审批
public class EntrymngManageServiceImpl extends SimpleDocServiceTemplate implements IEntrymngManageService, IEntrymngQueryService, IActionDriveChecker, ICheckStatusCallback{

	 private BaseDAO baseDAO = null;
	 /*      */   
	 /*      */   public void callCheckStatus(CheckStatusCallbackContext cscc) throws BusinessException
	 /*      */   {
	 /*  104 */     if (cscc.isTerminate())
	 /*      */     {
	 /*  106 */       NCObject ncObj = NCObject.newInstance(cscc.getBillVo());
	 /*  107 */       IFlowBizItf itf = (IFlowBizItf)ncObj.getBizInterface(IFlowBizItf.class);
	 /*  108 */       String[] fields = new String[4];
	 /*      */       
	 /*  110 */       itf.setApprover(cscc.getApproveId());
	 /*  111 */       fields[0] = itf.getColumnName("approver");
	 /*      */       
	 /*  113 */       itf.setApproveDate(cscc.getApproveDate() == null ? null : new UFDateTime(cscc.getApproveDate()));
	 /*  114 */       fields[1] = itf.getColumnName("approvedate");
	 /*      */       
	 /*  116 */       itf.setApproveStatus(Integer.valueOf(cscc.getCheckStatus()));
	 /*  117 */       fields[2] = itf.getColumnName("approvestatus");
	 /*      */       
	 /*  119 */       itf.setApproveNote(cscc.getCheckNote());
	 /*  120 */       fields[3] = itf.getColumnName("approvenote");
	 /*      */       
	 /*      */ 
	 /*  123 */       SuperVO vo = (SuperVO)((AggregatedValueObject)cscc.getBillVo()).getParentVO();
	 /*  124 */       getBaseDAO().updateVO(vo, fields);
	 /*  125 */       vo = (SuperVO)getBaseDAO().retrieveByPK(vo.getClass(), vo.getPrimaryKey());
	 /*  126 */       ((AggregatedValueObject)cscc.getBillVo()).setParentVO(vo);
	 /*      */     }
	 /*      */   }
	 /*      */   
	 /*      */   public BaseDAO getBaseDAO()
	 /*      */   {
	 /*  132 */     if (this.baseDAO == null)
	 /*      */     {
	 /*  134 */       this.baseDAO = new BaseDAO();
	 /*      */     }
	 /*  136 */     return this.baseDAO;
	 /*      */   }
	 /*      */   
	 /*      */   public EntrymngManageServiceImpl()
	 /*      */   {
	 /*  141 */     super("Entrymng");
	 /*      */   }
	 /*      */   
	 /*      */   public AggEntryapplyVO[] batchSaveBill(AggEntryapplyVO aggvo, ArrayList<String> pkPsnjobs, LoginContext context, String[] billCodes, boolean isShow)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*  147 */     boolean isAutoGenerateBillCode = isAutoGenerateBillCode("6101", context.getPk_group(), context.getPk_org());
	 /*      */     
	 /*  149 */     ArrayList<AggEntryapplyVO> al = new ArrayList();
	 /*      */     
	 /*  151 */     if (!isAutoGenerateBillCode)
	 /*      */     {
	 /*      */ 
	 /*  154 */       BillCodeHelper.lockBillCodeRule("hr_auto_billcode6101", 100L);
	 /*      */     }
	 /*      */     
	 /*      */ 
	 /*      */     try
	 /*      */     {
	 /*  160 */       String prefix = "ZD6101" + PubEnv.getServerDate().toStdString();
	 /*      */       
	 /*  162 */       String flowCode = getFlowCode(prefix);
	 /*  163 */       for (int i = 0; i < pkPsnjobs.size(); i++)
	 /*      */       {
	 /*  165 */         AggEntryapplyVO temp = clone(aggvo);
	 /*  166 */         EntryapplyVO head = (EntryapplyVO)temp.getParentVO();
	 /*  167 */         if ((isAutoGenerateBillCode) && (billCodes != null) && (billCodes.length > 0) && (billCodes[i] != null))
	 /*      */         {
	 /*  169 */           head.setBill_code(billCodes[i]);
	 /*      */ 
	 /*      */         }
	 /*      */         else
	 /*      */         {
	 /*  174 */           head.setBill_code(prefix + "_" + getFlowCode(flowCode, i));
	 /*      */         }
	 /*  176 */         head.setApprove_state(Integer.valueOf(-1));
	 /*  177 */         head.setBillmaker(context.getPk_loginUser());
	 /*      */         
	 /*  179 */         head.setPk_billtype("6101");
	 /*  180 */         head.setPk_org(context.getPk_org());
	 /*  181 */         head.setPk_group(context.getPk_group());
	 /*  182 */         PsnJobVO pj = (PsnJobVO)getIPersistenceRetrieve().retrieveByPk(null, PsnJobVO.class, (String)pkPsnjobs.get(i));
	 /*  183 */         head.setPk_psnjob(pj.getPk_psnjob());
	 /*  184 */         head.setPk_psndoc(pj.getPk_psndoc());
	 /*      */         
	 /*  186 */         Integer approveType = SysInitQuery.getParaInt(context.getPk_org(), (String)IHrPf.hashBillTypePara.get("6101"));
	 /*      */         
	 /*  188 */         if (approveType == null)
	 /*      */         {
	 /*  190 */           approveType = Integer.valueOf(1);
	 /*      */         }
	 /*      */         
	 /*  193 */         TrnstypeFlowVO[] flow = (TrnstypeFlowVO[])((IPersistenceRetrieve)NCLocator.getInstance().lookup(IPersistenceRetrieve.class)).retrieveByClause(null, TrnstypeFlowVO.class, " pk_group = '" + PubEnv.getPk_group() + "' and pk_trnstype = '" + pj.getTrnstype() + "'");
	 /*      */         
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*  199 */         if ((flow != null) && (flow.length > 0))
	 /*      */         {
	 /*  201 */           if ((head.getTranstypeid() == null) && (approveType.intValue() == 1))
	 /*      */           {
	 /*  203 */             head.setTranstypeid(flow[0].getPk_transtype());
	 /*  204 */             if (flow[0].getPk_transtype() != null)
	 /*      */             {
	 /*  206 */               BilltypeVO billtype = (BilltypeVO)((IPersistenceRetrieve)NCLocator.getInstance().lookup(IPersistenceRetrieve.class)).retrieveByPk(null, BilltypeVO.class, flow[0].getPk_transtype());
	 /*      */               
	 /*      */ 
	 /*  209 */               head.setTranstype(billtype.getPk_billtypecode());
	 /*      */             }
	 /*      */           }
	 /*      */           
	 /*  213 */           if ((head.getBusiness_type() == null) && (isShow))
	 /*      */           {
	 /*  215 */             head.setBusiness_type(flow[0].getPk_businesstype());
	 /*      */           }
	 /*      */         }
	 /*  218 */         al.add(temp);
	 /*      */       }
	 /*      */       
	 /*  221 */       checkBillCodeRepeat((AggregatedValueObject[])al.toArray(new AggEntryapplyVO[0]));
	 /*      */       
	 /*  223 */       ArrayList<AggEntryapplyVO> result = new ArrayList();
	 /*  224 */       for (AggEntryapplyVO agg : al)
	 /*      */       {
	 /*  226 */         result.add(insertBill(agg));
	 /*      */       }
	 /*      */       
	 /*  229 */       return (AggEntryapplyVO[])result.toArray(new AggEntryapplyVO[0]);
	 /*      */     }
	 /*      */     catch (Exception e)
	 /*      */     {
	 /*  233 */       String[] codes = null;
	 /*      */       
	 /*  235 */       if ((e instanceof BillCodeRepeatBusinessException))
	 /*      */       {
	 /*  237 */         codes = ((BillCodeRepeatBusinessException)e).getRepeatCodes();
	 /*  238 */         if ((isAutoGenerateBillCode) && (codes != null))
	 /*      */         {
	 /*  240 */           for (int i = 0; i < codes.length; i++)
	 /*      */           {
	 /*      */             try
	 /*      */             {
	 /*  244 */               ((IBillcodeManage)NCLocator.getInstance().lookup(IBillcodeManage.class)).AbandonBillCode_RequiresNew("6101", context.getPk_group(), context.getPk_org(), codes[i]);
	 /*      */ 
	 /*      */ 
	 /*      */             }
	 /*      */             catch (Exception e2)
	 /*      */             {
	 /*      */ 
	 /*      */ 
	 /*  252 */               Logger.error(e2.getMessage(), e2);
	 /*      */             }
	 /*      */           }
	 /*      */         }
	 /*      */       }
	 /*      */       
	 /*      */ 
	 /*  259 */       if ((billCodes != null) && (billCodes.length > 0))
	 /*      */       {
	 /*  261 */         for (String billno : billCodes)
	 /*      */         {
	 /*  263 */           if ((codes == null) || (!ArrayUtils.contains(codes, billno)))
	 /*      */           {
	 /*      */ 
	 /*      */             try
	 /*      */             {
	 /*      */ 
	 /*      */ 
	 /*  270 */               ((IBillcodeManage)NCLocator.getInstance().lookup(IBillcodeManage.class)).rollbackPreBillCode("6101", context.getPk_group(), context.getPk_org(), billno);
	 /*      */ 
	 /*      */             }
	 /*      */             catch (Exception e1)
	 /*      */             {
	 /*  275 */               Logger.error(e1.getMessage(), e1);
	 /*      */             } }
	 /*      */         }
	 /*      */       }
	 /*  279 */       Logger.error(e.getMessage(), e);
	 /*      */       
	 /*  281 */       Logger.error(e.getMessage(), e);
	 /*  282 */       throw new BusinessException(e.getMessage());
	 /*      */     }
	 /*      */     finally
	 /*      */     {
	 /*  286 */       if (!isAutoGenerateBillCode)
	 /*      */       {
	 /*      */ 
	 /*  289 */         BillCodeHelper.unlockBillCodeRule("hr_auto_billcode6101");
	 /*      */       }
	 /*      */     }
	 /*      */   }
	 /*      */   
	 /*      */   public Hashtable<String, String[]> createUserValue(AggregatedValueObject[] aggvos) throws BusinessException
	 /*      */   {
	 /*  296 */     String[] fieldCode = EntrymngConst.FIELDCODE;
	 /*  297 */     Hashtable<String, String[]> hm = new Hashtable();
	 /*  298 */     for (int i = 0; (aggvos != null) && (i < aggvos.length); i++)
	 /*      */     {
	 /*  300 */       EntryapplyVO bill = (EntryapplyVO)((AggEntryapplyVO)aggvos[i]).getParentVO();
	 /*  301 */       PsnJobVO psnJobVO = bill.getPk_psnjob() == null ? null : (PsnJobVO)getIPersistenceRetrieve().retrieveByPk(null, PsnJobVO.class, bill.getPk_psnjob());
	 /*      */       
	 /*      */ 
	 /*  304 */       if (psnJobVO == null)
	 /*      */       {
	 /*  306 */         return hm;
	 /*      */       }
	 /*  308 */       for (int j = 0; j < fieldCode.length; j++)
	 /*      */       {
	 /*  310 */         String value = "";
	 /*      */         
	 /*  312 */         if ("bill_code".equals(fieldCode[j]))
	 /*      */         {
	 /*      */ 
	 /*  315 */           value = bill.getBill_code();
	 /*      */         }
	 /*  317 */         else if ("effect_date".equals(fieldCode[j]))
	 /*      */         {
	 /*      */ 
	 /*  320 */           value = psnJobVO.getBegindate() == null ? "" : psnJobVO.getBegindate().toStdString();
	 /*      */         }
	 /*  322 */         else if ("approve_state".equals(fieldCode[j]))
	 /*      */         {
	 /*      */ 
	 /*  325 */           value = getStatus(Integer.valueOf(bill.getApprove_state() == null ? 102 : bill.getApprove_state().intValue()));
	 /*      */         }
	 /*  327 */         else if ("pk_psnjob".equals(fieldCode[j]))
	 /*      */         {
	 /*      */ 
	 /*  330 */           value = VOUtils.getDocName(PsndocVO.class, psnJobVO.getPk_psndoc());
	 /*      */         }
	 /*  332 */         else if ("pk_org".equals(fieldCode[j]))
	 /*      */         {
	 /*      */ 
	 /*  335 */           value = VOUtils.getDocName(OrgVO.class, psnJobVO.getPk_org());
	 /*      */         }
	 /*  337 */         else if ("pk_dept".equals(fieldCode[j]))
	 /*      */         {
	 /*      */ 
	 /*  340 */           value = VOUtils.getDocName(DeptVO.class, psnJobVO.getPk_dept());
	 /*      */         }
	 /*  342 */         else if ("pk_psncl".equals(fieldCode[j]))
	 /*      */         {
	 /*      */ 
	 /*  345 */           value = VOUtils.getDocName(PsnClVO.class, psnJobVO.getPk_psncl());
	 /*      */         }
	 /*  347 */         else if ("pk_post".equals(fieldCode[j]))
	 /*      */         {
	 /*      */ 
	 /*  350 */           value = VOUtils.getDocName(PostVO.class, psnJobVO.getPk_post());
	 /*      */         }
	 /*  352 */         else if ("pk_job".equals(fieldCode[j]))
	 /*      */         {
	 /*      */ 
	 /*  355 */           value = VOUtils.getDocName(JobVO.class, psnJobVO.getPk_job());
	 /*      */         }
	 /*      */         else
	 /*      */         {
	 /*  359 */           value = "";
	 /*      */         }
	 /*  361 */         hm.put(fieldCode[j] + i, new String[] { value });
	 /*      */       }
	 /*      */     }
	 /*  364 */     return hm;
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */   private AggEntryapplyVO clone(AggEntryapplyVO src)
	 /*      */   {
	 /*  373 */     AggEntryapplyVO trg = new AggEntryapplyVO();
	 /*  374 */     EntryapplyVO head = new EntryapplyVO();
	 /*  375 */     trg.setParentVO(head);
	 /*  376 */     for (String attrName : src.getParentVO().getAttributeNames())
	 /*      */     {
	 /*  378 */       trg.getParentVO().setAttributeValue(attrName, src.getParentVO().getAttributeValue(attrName));
	 /*      */     }
	 /*  380 */     return trg;
	 /*      */   }
	 /*      */   
	 /*      */   public <T extends AggregatedValueObject> void deleteBatchBill(T... billvos)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*  386 */     PfBusinessLock pfLock = null;
	 /*  387 */     IFlowBizItf flowItf = getFlowBizItf(billvos[0].getParentVO());
	 /*      */     
	 /*      */     try
	 /*      */     {
	 /*  391 */       pfLock = new PfBusinessLock();
	 /*  392 */       pfLock.lock(new VOsLockData(billvos, flowItf.getBilltype()), new VOsConsistenceCheck(billvos, flowItf.getBilltype()));
	 /*      */       
	 /*  394 */       DefaultValidationService vService = new DefaultValidationService();
	 /*  395 */       createCustomValidators(vService, "delete");
	 /*  396 */       SuperVO[] headvos = (SuperVO[])getHeadVO(billvos).toArray(new SuperVO[0]);
	 /*  397 */       vService.validate(headvos);
	 /*  398 */       getMDPersistenceService().deleteBillFromDB(billvos);
	 /*      */     }
	 /*      */     finally
	 /*      */     {
	 /*  402 */       if (pfLock != null)
	 /*      */       {
	 /*      */ 
	 /*  405 */         pfLock.unLock();
	 /*      */       }
	 /*      */     }
	 /*      */   }
	 /*      */   
	 /*      */   private void deleteOldWorknote(AggEntryapplyVO vo)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*  413 */     getIHrPf().deleteWorkflowNote(vo);
	 /*      */   }
	 /*      */   
	 /*      */   public <T extends AggregatedValueObject> void deleteBill(T billvo)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*  419 */     PfBusinessLock pfLock = null;
	 /*  420 */     IFlowBizItf flowItf = getFlowBizItf(billvo.getParentVO());
	 /*      */     
	 /*      */     try
	 /*      */     {
	 /*  424 */       pfLock = new PfBusinessLock();
	 /*  425 */       pfLock.lock(new VOLockData(billvo, flowItf.getBilltype()), new VOConsistenceCheck(billvo, flowItf.getBilltype()));
	 /*      */       
	 /*  427 */       DefaultValidationService vService = new DefaultValidationService();
	 /*  428 */       createCustomValidators(vService, "delete");
	 /*  429 */       SuperVO[] headvos = (SuperVO[])getHeadVO(billvo).toArray(new SuperVO[0]);
	 /*  430 */       vService.validate(headvos);
	 /*  431 */       getMDPersistenceService().deleteBillFromDB(billvo);
	 /*      */     }
	 /*      */     finally
	 /*      */     {
	 /*  435 */       if (pfLock != null)
	 /*      */       {
	 /*      */ 
	 /*  438 */         pfLock.unLock();
	 /*      */       }
	 /*      */     }
	 /*      */   }
	 /*      */   
	 /*      */   public AggEntryapplyVO[] doApprove(AggEntryapplyVO[] vos)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*  446 */     AggEntryapplyVO[] vo = ((IEntrymngManageService)NCLocator.getInstance().lookup(IEntrymngManageService.class)).batchUpdateBill_RequiresNew(vos);
	 /*      */     
	 /*  448 */     return execBills(vo);
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */   public AggEntryapplyVO[] execBills(AggEntryapplyVO[] billvos)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*  460 */     if (ArrayUtils.isEmpty(billvos))
	 /*      */     {
	 /*  462 */       return null;
	 /*      */     }
	 /*      */     
	 /*  465 */     ArrayList<AggEntryapplyVO> allvo = new ArrayList();
	 /*  466 */     for (AggEntryapplyVO agg : billvos)
	 /*      */     {
	 /*  468 */       allvo.add(agg);
	 /*      */     }
	 /*      */     
	 /*      */ 
	 /*  472 */     LoginContext tempContext = new LoginContext();
	 /*  473 */     EntryapplyVO parentVO = (EntryapplyVO)billvos[0].getParentVO();
	 /*  474 */     tempContext.setPk_group(parentVO.getPk_group());
	 /*  475 */     tempContext.setPk_org(parentVO.getPk_org());
	 /*      */     
	 /*      */ 
	 /*  478 */     HashMap<String, Object> result = ((IEntrymngManageService)NCLocator.getInstance().lookup(IEntrymngManageService.class)).execBills(billvos, tempContext, false);
	 /*      */     
	 /*  480 */     AggEntryapplyVO[] retObjs = (AggEntryapplyVO[])result.get("RESULT_BILLS");
	 /*  481 */     String msg = (String)result.get("RESULT_MSG");
	 /*      */     
	 /*  483 */     if (!StringUtils.isBlank(msg))
	 /*      */     {
	 /*      */ 
	 /*  486 */       NCMessage ncMessage = new NCMessage();
	 /*  487 */       MessageVO messageVO = new MessageVO();
	 /*  488 */       messageVO.setMsgsourcetype("notice");
	 /*  489 */       messageVO.setReceiver(PubEnv.getPk_user());
	 /*  490 */       messageVO.setIsdelete(UFBoolean.FALSE);
	 /*  491 */       messageVO.setSender("NC_USER0000000000000");
	 /*      */       
	 /*  493 */       messageVO.setSendtime(PubEnv.getServerTime());
	 /*  494 */       messageVO.setDr(Integer.valueOf(0));
	 /*  495 */       messageVO.setSubject(ResHelper.getString("6007entry", "16007entry0015"));
	 /*      */       
	 /*      */ 
	 /*  498 */       messageVO.setContent(msg);
	 /*  499 */       ncMessage.setMessage(messageVO);
	 /*  500 */       NCMessage[] message = new NCMessage[1];
	 /*  501 */       message[0] = ncMessage;
	 /*      */       try
	 /*      */       {
	 /*  504 */         MessageCenter.sendMessage(message);
	 /*      */       }
	 /*      */       catch (Exception e)
	 /*      */       {
	 /*  508 */         Logger.error(e.getMessage(), e);
	 /*      */       }
	 /*      */     }
	 /*      */     
	 /*      */ 
	 /*  513 */     if (!ArrayUtils.isEmpty(retObjs))
	 /*      */     {
	 /*  515 */       for (int i = 0; i < allvo.size(); i++)
	 /*      */       {
	 /*  517 */         AggEntryapplyVO aggivo = (AggEntryapplyVO)allvo.get(i);
	 /*  518 */         for (int j = 0; j < retObjs.length; j++)
	 /*      */         {
	 /*  520 */           if (aggivo.getParentVO().getPrimaryKey().equals(retObjs[j].getParentVO().getPrimaryKey()))
	 /*      */           {
	 /*  522 */             allvo.remove(i);
	 /*      */           }
	 /*      */         }
	 /*      */       }
	 /*  526 */       billvos = (AggEntryapplyVO[])ArrayUtils.addAll(allvo.toArray(new AggEntryapplyVO[0]), retObjs);
	 /*      */     }
	 /*      */     
	 /*  529 */     return billvos;
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */   public AggEntryapplyVO[] batchUpdateBill_RequiresNew(AggEntryapplyVO[] vos)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*  542 */     for (int i = 0; (vos != null) && (i < vos.length); i++)
	 /*      */     {
	 /*  544 */       vos[i] = ((AggEntryapplyVO)updateBill(vos[i], false));
	 /*      */     }
	 /*  546 */     return vos;
	 /*      */   }
	 /*      */   
	 /*      */   public AggEntryapplyVO[] doCommit(AggEntryapplyVO[] vos) throws BusinessException
	 /*      */   {
	 /*  551 */     for (int i = 0; (vos != null) && (i < vos.length); i++)
	 /*      */     {
	 /*  553 */       EntryapplyVO billvo = (EntryapplyVO)vos[i].getParentVO();
	 /*      */       
	 /*  555 */       billvo.setApprove_state(Integer.valueOf(3));
	 /*  556 */       vos[i].setParentVO(billvo);
	 /*  557 */       vos[i] = ((AggEntryapplyVO)updateBill(vos[i], false));
	 /*      */     }
	 /*  559 */     String pk_org = ((EntryapplyVO)vos[0].getParentVO()).getPk_org();
	 /*  560 */     Integer approvetype = Integer.valueOf(1);
	 /*      */     try
	 /*      */     {
	 /*  563 */       approvetype = SysInitQuery.getParaInt(pk_org, (String)IHrPf.hashBillTypePara.get("6101"));
	 /*      */     }
	 /*      */     catch (Exception e)
	 /*      */     {
	 /*  567 */       Logger.error(e.getMessage(), e);
	 /*      */     }
	 /*  569 */     if ((approvetype != null) && (approvetype.intValue() == 0))
	 /*      */     {
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*  577 */       String tempCode = "600711";
	 /*  578 */       HiSendMsgHelper.sendMessage1(tempCode, vos, pk_org);
	 /*      */     }
	 /*      */     senOaData(vos);
	 /*  581 */     return vos;
	 /*      */   }
	 private void senOaData(AggEntryapplyVO[] billVOs) throws BusinessException{
		for (AggEntryapplyVO temp : billVOs) {
			if((( (EntryapplyVO) temp.getParentVO()).getTranstype()).contains("6007")){
				JSONArray headData = getMainMap((EntryapplyVO) temp.getParentVO());
				JSONArray bodyData = null;
				/* 
				 * step-1 调用OA工具类同步数据至OA
				 */
				OaWorkFlowUtil.sendOaData(headData, bodyData, getWorkFlowBill(temp));
			}
		}
	}
	private JSONArray getMainMap(EntryapplyVO parentVO) throws BusinessException {
		List list = new ArrayList();
		
		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class, parentVO.getPk_org());
		if(null !=orgVO){
			/* 组织 */
			Map szgs =OaWorkFlowUtil.listAdd("szgs",orgVO.getCode() );
			list.add(szgs);
		}
		
		/* 申请单编码 */
		 Map sqdbm =OaWorkFlowUtil.listAdd("sqdbm",parentVO.getBill_code() );
		 list.add(sqdbm);
		 
		 /* 流程类型 */
		 Map lclx =OaWorkFlowUtil.listAdd("lclx",parentVO.getTranstypeid() );
		 list.add(lclx);
		 
		 /* 审批状态 */
		 Map spzt =OaWorkFlowUtil.listAdd("spzt",parentVO.getApprove_state().toString() );
		 list.add(spzt);
		 
		 /* 申请人 */
		 Map sqr =OaWorkFlowUtil.listAdd("sqr",parentVO.getBillmaker() );
		 list.add(sqr);
		 
		 /* 申请日期 */
		 Map sqrq =OaWorkFlowUtil.listAdd("sqrq",parentVO.getApply_date().getYear()+ "-"
					+ parentVO.getApply_date().getStrMonth() + "-"
					+ parentVO.getApply_date().getStrDay());
		 list.add(sqrq);
		 
		 /* 审批人 */
		 Map spr =OaWorkFlowUtil.listAdd("spr",parentVO.getApprover());
		 list.add(spr);
		 
		 /* 审批时间 */
		 Map spsj =OaWorkFlowUtil.listAdd("spsj",parentVO.getApprove_time().getYear()+ "-"
					+ parentVO.getApprove_time().getStrMonth() + "-"
					+ parentVO.getApprove_time().getStrDay());
		 list.add(spsj);
		 
		 /*入职人员 */
		 Map rzry =OaWorkFlowUtil.listAdd("rzry",parentVO.getPk_psnjob() );
		 list.add(rzry);
		 
		 PsnJobVO  psnJobVO = (PsnJobVO) getHyPubBO().queryByPrimaryKey(PsnJobVO.class, parentVO.getPk_psnjob());
		if(null !=psnJobVO){
			
			 /* 人员类别*/
			 Map rylb =OaWorkFlowUtil.listAdd("rylb",psnJobVO.getPk_psncl() );
			 list.add(rylb);
			 
			 /* 部门 */
			 Map bm =OaWorkFlowUtil.listAdd("bm",psnJobVO.getPk_dept() );
			 list.add(bm);
			 
			 /* 职位*/
			 Map zw =OaWorkFlowUtil.listAdd("zw",psnJobVO.getPk_job() );
			 list.add(zw);
			 
			 /* 岗位 */
			 Map gw =OaWorkFlowUtil.listAdd("gw",psnJobVO.getPk_post() );
			 list.add(gw);
			 
			 /* 入职类型 */
			 Map rzlx =OaWorkFlowUtil.listAdd("rzlx",psnJobVO.getTrnstype() );
			 list.add(rzlx);
			 
			 /* 生效日期 */
			 Map sxrq =OaWorkFlowUtil.listAdd("sxrq",psnJobVO.getBegindate().getYear()+ "-"
						+ psnJobVO.getBegindate().getStrMonth() + "-"
						+ psnJobVO.getBegindate().getStrDay());
		}
		
		 /* 备注 */
		 Map bz =OaWorkFlowUtil.listAdd("bz",parentVO.getMemo() );
		 list.add(bz);
		 
		 /* 同步履历 */
		 Map tbll =OaWorkFlowUtil.listAdd("tbll",parentVO.getIssyncwork().toString() );
		 list.add(tbll);
		 
			UserVO  userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class, parentVO.getBillmaker());
			if(null !=userVO){
				/* 制单人名称 */
				Map zdrmc =OaWorkFlowUtil.listAdd("zdrmc",userVO.getUser_name() );
				list.add(zdrmc);
				
				/* 制单人名称 */
				Map zdrzj =OaWorkFlowUtil.listAdd("zdrmc",userVO.getUser_code() );
				list.add(zdrzj);
			}
			 

			 
	   	 JSONArray arr = JSONArray.fromObject(list);
		 return arr;
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
		
		private WorkFlowBill getWorkFlowBill(AggEntryapplyVO temp) throws BusinessException {
			 String where = " nvl(dr,0) = 0 and pk_bill = '" + temp.getParentVO().getPrimaryKey()+ "'";
			 WorkFlowBill[] workFlowBills = (WorkFlowBill[]) getHyPubBO()
						.queryByCondition(WorkFlowBill.class, where);
				WorkFlowBill workFlowBill = new WorkFlowBill();
				if(null != workFlowBills && workFlowBills.length > 1){
					throw new BusinessException("查询到多条流程记录");
				}
				if (null != workFlowBills && workFlowBills.length == 1) {
					workFlowBill = workFlowBills[0];
				} else {
			  UserVO  userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class, ((EntryapplyVO) temp.getParentVO()).getBillmaker());
			  workFlowBill.setPk_group(((EntryapplyVO) temp.getParentVO()).getPk_group());
			  workFlowBill.setPk_org(((EntryapplyVO) temp.getParentVO()).getPk_org());
			  workFlowBill.setPk_bill(temp.getParentVO().getPrimaryKey());
			  workFlowBill.setUser_name(userVO.getUser_name());
			  workFlowBill.setUser_code(userVO.getUser_code());
			  workFlowBill.setCreationtime(new UFDate().toStdString());
			  workFlowBill.setBill_status("1");
			  workFlowBill.setCreator(((EntryapplyVO) temp.getParentVO()).getCreator());
			  workFlowBill.setBill_code("6007");
			  workFlowBill.setWorkflowId("44");
			  workFlowBill.setWorkflowName("入职申请单");
				}
			  return workFlowBill;
			}
		
	/*      */   
	 /*      */   public AggEntryapplyVO[] doDelete(AggEntryapplyVO[] vos) throws BusinessException
	 /*      */   {
	 /*  586 */     for (AggEntryapplyVO vo : vos)
	 /*      */     {
	 /*      */ 
	 /*  589 */       String billType = (String)vo.getParentVO().getAttributeValue("pk_billtype");
	 /*  590 */       String pk_group = (String)vo.getParentVO().getAttributeValue("pk_group");
	 /*  591 */       String pk_org = (String)vo.getParentVO().getAttributeValue("pk_org");
	 /*  592 */       String bill_code = (String)vo.getParentVO().getAttributeValue("bill_code");
	 /*  593 */       if (isAutoGenerateBillCode(billType, pk_group, pk_org))
	 /*      */       {
	 /*  595 */         ((IBillcodeManage)NCLocator.getInstance().lookup(IBillcodeManage.class)).returnBillCodeOnDelete(billType, pk_group, pk_org, bill_code, null);
	 /*      */       }
	 /*  597 */       deleteOldWorknote(vo);
	 /*  598 */       deleteBill(vo);
	 /*      */     }
	 /*  600 */     return vos;
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */ 
	 /*      */   public void doPerfromBill_RequiresNew(AggEntryapplyVO aggVO)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*  608 */     if (aggVO == null) { return;
	 /*      */     }
	 /*  610 */     EntryapplyVO vo = (EntryapplyVO)aggVO.getParentVO();
	 /*      */     
	 /*  612 */     PsnOrgVO[] orgVO = (PsnOrgVO[])queryByCondition(PsnOrgVO.class, " pk_psndoc = '" + vo.getPk_psndoc() + "' ");
	 /*  613 */     PsnOrgVO psnorgVO = new PsnOrgVO();
	 /*      */     
	 /*  615 */     for (PsnOrgVO psnvo : orgVO)
	 /*      */     {
	 /*  617 */       if ((psnvo.getEndflag() == UFBoolean.FALSE) && (psnvo.getLastflag() == UFBoolean.TRUE))
	 /*      */       {
	 /*  619 */         psnorgVO = psnvo;
	 /*      */       }
	 /*      */     }
	 /*  622 */     if ((orgVO != null) && (orgVO.length > 0) && (psnorgVO.getIndocflag() != null) && (psnorgVO.getIndocflag().booleanValue()))
	 /*      */     {
	 /*  624 */       PsndocVO psn = (PsndocVO)getIPersistenceRetrieve().retrieveByPk(null, PsndocVO.class, psnorgVO.getPk_psndoc());
	 /*  625 */       String name = MultiLangHelper.getName(psn);
	 /*      */       
	 /*  627 */       throw new BusinessException(ResHelper.getString("6007psn", "06007psn0338", new String[] { psn.getCode(), name }));
	 /*      */     }
	 /*      */     
	 /*      */ 
	 /*  631 */     boolean issyncwork = vo.getIssyncwork() == null ? false : vo.getIssyncwork().booleanValue();
	 /*  632 */     ((IPsndocService)NCLocator.getInstance().lookup(IPsndocService.class)).intoDoc(null, issyncwork, vo.getPk_org(), new String[] { vo.getPk_psnjob() });
	 /*      */     
	 /*      */ 
	 /*  635 */     vo.setAttributeValue("approve_state", Integer.valueOf(102));
	 /*  636 */     getPersistenceUpdate().updateVO(null, vo, new String[] { "approve_state" }, null);
	 /*      */     
	 /*      */ 
	 /*  639 */     PsnJobVO jobVO = (PsnJobVO)queryByPk(PsnJobVO.class, vo.getPk_psnjob());
	 /*  640 */     String[] updateFields = { "oribilltype", "oribillpk" };
	 /*  641 */     jobVO.setAttributeValue(updateFields[0], "6101");
	 /*  642 */     jobVO.setAttributeValue(updateFields[1], vo.getPk_entryapply());
	 /*  643 */     getPersistenceUpdate().updateVO(null, jobVO, updateFields, null);
	 /*      */   }
	 /*      */   
	 /*      */   public void doPushBill_RequiresNew(AggEntryapplyVO bill)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*  649 */     HashMap<String, String> hashPara = new HashMap();
	 /*  650 */     hashPara.put("nosendmessage", "nosendmessage");
	 /*  651 */     ((IplatFormEntry)NCLocator.getInstance().lookup(IplatFormEntry.class)).processAction("PUSH", "6101", null, bill, null, hashPara);
	 /*      */   }
	 /*      */   
	 /*      */   public Object doPush(AggEntryapplyVO vo)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*  657 */     return vo;
	 /*      */   }
	 /*      */   
	 /*      */   public AggregatedValueObject[] doUnApprove(AggregatedValueObject[] vos) throws BusinessException
	 /*      */   {
	 /*  662 */     List<String> entryPKList = new ArrayList();
	 /*  663 */     for (int i = 0; i < vos.length; i++)
	 /*      */     {
	 /*  665 */       entryPKList.add(((EntryapplyVO)vos[i].getParentVO()).getPk_entryapply());
	 /*      */     }
	 /*  667 */     if (!entryPKList.isEmpty())
	 /*      */     {
	 /*  669 */       InSQLCreator isc = new InSQLCreator();
	 /*  670 */       String insql = isc.getInSQL((String[])entryPKList.toArray(new String[0]));
	 /*  671 */       String strCondition = "pk_entryapply in (" + insql + ")";
	 /*  672 */       EntryapplyVO[] entryApplyVOs = (EntryapplyVO[])((IPersistenceRetrieve)NCLocator.getInstance().lookup(IPersistenceRetrieve.class)).retrieveByClause(null, EntryapplyVO.class, strCondition);
	 /*      */       
	 /*      */ 
	 /*  675 */       for (int i = 0; i < entryApplyVOs.length; i++)
	 /*      */       {
	 /*  677 */         int approvestate = entryApplyVOs[i].getApprove_state().intValue();
	 /*      */         
	 /*  679 */         checkPFPassingState(approvestate);
	 /*      */         
	 /*  681 */         if (approvestate == 102)
	 /*      */         {
	 /*  683 */           throw new BusinessException(ResHelper.getString("6009tran", "06009tran0209"));
	 /*      */         }
	 /*      */       }
	 /*      */     }
	 /*  687 */     for (int i = 0; (vos != null) && (i < vos.length); i++)
	 /*      */     {
	 /*  689 */       vos[i] = updateBill(vos[i], false);
	 /*      */     }
	 /*  691 */     return vos;
	 /*      */   }
	 /*      */   
	 /*      */   public void checkPFPassingState(int pfsate)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*  697 */     if (0 == pfsate)
	 /*      */     {
	 /*  699 */       throw new BusinessException(ResHelper.getString("6007entry", "16007entry0014"));
	 /*      */     }
	 /*      */   }
	 /*      */   
	 /*      */   public AggregatedValueObject[] doCallBack(AggregatedValueObject[] vos)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*  706 */     for (int i = 0; (vos != null) && (i < vos.length); i++)
	 /*      */     {
	 /*  708 */       vos[i] = updateBill(vos[i], false);
	 /*      */     }
	 for (int i = 0; (vos != null) && (i < vos.length); i++)
	    {
		 OaWorkFlowUtil.backOaWorkFlow(vos[i].getParentVO().getPrimaryKey());
	  }
	 /*  710 */     return vos;
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */   public HashMap<String, Object> execBills(AggEntryapplyVO[] billVOs, LoginContext context, boolean isRunBackgroundTask)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*  727 */     if (!isRunBackgroundTask)
	 /*      */     {
	 /*      */ 
	 /*  730 */       ArrayList<AggEntryapplyVO> passVOs = new ArrayList();
	 /*  731 */       for (int i = 0; i < billVOs.length; i++)
	 /*      */       {
	 /*  733 */         EntryapplyVO applyVO = (EntryapplyVO)billVOs[i].getParentVO();
	 /*  734 */         Integer apprState = applyVO.getApprove_state();
	 /*  735 */         PsnJobVO psnJobVO = (PsnJobVO)getIPersistenceRetrieve().retrieveByPk(null, PsnJobVO.class, applyVO.getPk_psnjob());
	 /*  736 */         UFLiteralDate effectDate = psnJobVO.getBegindate();
	 /*  737 */         if ((effectDate != null) && (effectDate.compareTo(PubEnv.getServerLiteralDate()) <= 0) && (apprState != null) && (apprState.intValue() == 1))
	 /*      */         {
	 /*      */ 
	 /*  740 */           passVOs.add(billVOs[i]);
	 /*      */         }
	 /*      */       }
	 /*  743 */       billVOs = (AggEntryapplyVO[])passVOs.toArray(new AggEntryapplyVO[0]);
	 /*      */     }
	 /*      */     
	 /*  746 */     if ((billVOs == null) || (billVOs.length == 0))
	 /*      */     {
	 /*      */ 
	 /*      */ 
	 /*  750 */       HashMap<String, Object> res = new HashMap();
	 /*      */       
	 /*  752 */       return res;
	 /*      */     }
	 /*      */     
	 /*  755 */     HashMap<String, Object> result = new HashMap();
	 /*  756 */     StringBuffer sb = new StringBuffer();
	 /*      */     
	 /*  758 */     AggregatedValueObject[] retVOs = null;
	 /*  759 */     if (billVOs.length > 0)
	 /*      */     {
	 /*  761 */       retVOs = getValidBudgetVO(billVOs, context);
	 /*      */     }
	 /*      */     
	 /*      */ 
	 /*  765 */     for (int i = 0; i < billVOs.length; i++)
	 /*      */     {
	 /*  767 */       if (!isExit(retVOs, billVOs[i]))
	 /*      */       {
	 /*      */ 
	 /*      */ 
	 /*  771 */         sb.append(ResHelper.getString("6007entry", "06007entry0038") + billVOs[i].getParentVO().getAttributeValue("bill_code") + ResHelper.getString("6007entry", "06007entry0039"));
	 /*      */       }
	 /*      */     }
	 /*      */     
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*  781 */     if ((retVOs == null) || (retVOs.length == 0))
	 /*      */     {
	 /*      */ 
	 /*  784 */       String msg = sb.length() == 0 ? "" : sb.toString();
	 /*  785 */       result.put("RESULT_MSG", isRunBackgroundTask ? msg : msg.replaceAll("<br>", "\n"));
	 /*      */       
	 /*      */ 
	 /*  788 */       result.put("RESULT_BILLS", null);
	 /*  789 */       return result;
	 /*      */     }
	 /*      */     
	 /*  792 */     boolean bl = ((IHRLicenseChecker)NCLocator.getInstance().lookup(IHRLicenseChecker.class)).checkPsnCountOnSwitchToDoc(retVOs.length);
	 /*      */     
	 /*  794 */     if (bl)
	 /*      */     {
	 /*  796 */       result.put("RESULT_MSG", ResHelper.getString("6007psn", "06007psn0431"));
	 /*  797 */       result.put("RESULT_BILLS", null);
	 /*  798 */       return result;
	 /*      */     }
	 /*      */     
	 /*      */ 
	 /*  802 */     ArrayList<AggEntryapplyVO> passBills = new ArrayList();
	 /*  803 */     for (int i = 0; (retVOs != null) && (i < retVOs.length); i++)
	 /*      */     {
	 /*      */ 
	 /*      */       try
	 /*      */       {
	 /*  808 */         ((IEntrymngManageService)NCLocator.getInstance().lookup(IEntrymngManageService.class)).doPerfromBill_RequiresNew((AggEntryapplyVO)retVOs[i]);
	 /*  809 */         passBills.add((AggEntryapplyVO)retVOs[i]);
	 /*      */ 
	 /*      */       }
	 /*      */       catch (Exception e)
	 /*      */       {
	 /*      */ 
	 /*  815 */         Logger.error(e.getMessage(), e);
	 /*  816 */         String billcode = (String)retVOs[i].getParentVO().getAttributeValue("bill_code");
	 /*  817 */         if (StringUtils.isEmpty(e.getMessage()))
	 /*      */         {
	 /*  819 */           sb.append(i + 1 + ResHelper.getString("6007entry", "06007entry0040") + billcode + ResHelper.getString("6007entry", "06007entry0041") + e.getMessage() + ResHelper.getString("6007entry", "06007entry0042"));
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */         }
	 /*  834 */         else if (e.getMessage().indexOf(billcode) < 0)
	 /*      */         {
	 /*      */ 
	 /*  837 */           sb.append(i + 1 + ResHelper.getString("6007entry", "06007entry0040") + billcode + ResHelper.getString("6007entry", "06007entry0043") + e.getMessage() + ResHelper.getString("6007entry", "06007entry0042"));
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */         }
	 /*      */         else
	 /*      */         {
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*  849 */           sb.append(i + 1 + ":" + e.getMessage());
	 /*      */         }
	 /*      */         
	 /*      */ 
	 /*  853 */         passBills.add((AggEntryapplyVO)retVOs[i]);
	 /*  854 */         continue;
	 /*      */       }
	 /*      */       try
	 /*      */       {
	 /*  858 */         AggEntryapplyVO agg = queryByPk(retVOs[i].getParentVO().getPrimaryKey());
	 /*  859 */         ((IEntrymngManageService)NCLocator.getInstance().lookup(IEntrymngManageService.class)).doPushBill_RequiresNew(agg);
	 /*      */       }
	 /*      */       catch (Exception e)
	 /*      */       {
	 /*  863 */         Logger.error(e.getMessage(), e);
	 /*      */       }
	 /*      */     }
	 /*      */     
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*  878 */     HashMap<String, ArrayList<AggEntryapplyVO>> hmTrans = new HashMap();
	 /*  879 */     for (int i = 0; i < passBills.size(); i++)
	 /*      */     {
	 /*  881 */       String pk_org = (String)((AggEntryapplyVO)passBills.get(i)).getParentVO().getAttributeValue("pk_org");
	 /*  882 */       if (hmTrans.get(pk_org) == null)
	 /*      */       {
	 /*  884 */         hmTrans.put(pk_org, new ArrayList());
	 /*      */       }
	 /*  886 */       ((ArrayList)hmTrans.get(pk_org)).add((AggEntryapplyVO)passBills.get(i));
	 /*      */     }
	 /*      */     
	 /*  889 */     for (String key : hmTrans.keySet())
	 /*      */     {
	 /*  891 */       if ((hmTrans.get(key) != null) && (((ArrayList)hmTrans.get(key)).size() > 0))
	 /*      */       {
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*  896 */         String tempCode = "600701";
	 /*  897 */         HiSendMsgHelper.sendMessage1(tempCode, (HYBillVO[])((ArrayList)hmTrans.get(key)).toArray(new AggEntryapplyVO[0]), key);
	 /*      */       }
	 /*      */     }
	 /*      */     
	 /*  901 */     String msg = sb.length() == 0 ? "" : sb.toString();
	 /*  902 */     result.put("RESULT_MSG", isRunBackgroundTask ? msg : msg.replaceAll("<br>", "\n"));
	 /*  903 */     result.put("RESULT_BILLS", passBills.toArray(new AggEntryapplyVO[0]));
	 /*  904 */     return result;
	 /*      */   }
	 /*      */   
	 /*      */   private boolean isExit(AggregatedValueObject[] retVOs, AggEntryapplyVO billVO)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*  910 */     for (int i = 0; (retVOs != null) && (i < retVOs.length); i++)
	 /*      */     {
	 /*  912 */       if (billVO.getParentVO().getPrimaryKey().equals(retVOs[i].getParentVO().getPrimaryKey()))
	 /*      */       {
	 /*  914 */         return true;
	 /*      */       }
	 /*      */     }
	 /*  917 */     return false;
	 /*      */   }
	 /*      */   
	 /*      */   public String getBillIdSql(int iBillStatus, String billType, String billid)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*  923 */     String strWorkFlowWhere = getIHrPf().getBillIdSql(iBillStatus, billType);
	 /*  924 */     if (!StringUtils.isEmpty(strWorkFlowWhere))
	 /*      */     {
	 /*  926 */       strWorkFlowWhere = billid + " in (" + strWorkFlowWhere + ") ";
	 /*      */     }
	 /*  928 */     return strWorkFlowWhere;
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */   private <T> IFlowBizItf getFlowBizItf(T vo)
	 /*      */   {
	 /*  934 */     NCObject ncObj = NCObject.newInstance(vo);
	 /*  935 */     IFlowBizItf itf = (IFlowBizItf)ncObj.getBizInterface(IFlowBizItf.class);
	 /*  936 */     return itf;
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */   public String getFlowCode(String prefix)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*  952 */     String whereSql = "bill_code like '" + prefix + "%' and len(" + "bill_code" + ") = 22 order by " + "bill_code" + " desc";
	 /*      */     
	 /*      */ 
	 /*  955 */     SuperVO[] vos = ((IPersistenceRetrieve)NCLocator.getInstance().lookup(IPersistenceRetrieve.class)).retrieveByClause(null, EntryapplyVO.class, whereSql);
	 /*  956 */     if ((vos == null) || (vos.length == 0))
	 /*      */     {
	 /*  958 */       return "00001";
	 /*      */     }
	 /*  960 */     for (SuperVO vo : vos)
	 /*      */     {
	 /*  962 */       String code = ((String)vo.getAttributeValue("bill_code")).substring(prefix.length() + 1);
	 /*      */       try
	 /*      */       {
	 /*  965 */         Integer value = Integer.valueOf(code);
	 /*  966 */         if (value != null)
	 /*      */         {
	 /*      */ 
	 /*  969 */           return StringUtils.leftPad(value.intValue() + 1 + "", 5, '0');
	 /*      */         }
	 /*      */       }
	 /*      */       catch (NumberFormatException ex) {}
	 /*      */     }
	 /*      */     
	 /*      */ 
	 /*      */ 
	 /*  977 */     return "00001";
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */   private String getFlowCode(String code, int i)
	 /*      */     throws BusinessException
	 /*      */   {
	 /*  990 */     Integer value = Integer.valueOf(code);
	 /*  991 */     return StringUtils.leftPad(value.intValue() + i + "", 5, '0');
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */   private List<SuperVO> getHeadVO(Object objects)
	 /*      */   {
	 /* 1003 */     List<SuperVO> headls = new ArrayList();
	 /* 1004 */     if (((objects instanceof AggregatedValueObject[])) && (((AggregatedValueObject[])objects).length >= 1))
	 /*      */     {
	 /* 1006 */       AggregatedValueObject[] objs = (AggregatedValueObject[])objects;
	 /* 1007 */       for (int i = 0; i < objs.length; i++)
	 /*      */       {
	 /* 1009 */         headls.add((SuperVO)objs[i].getParentVO());
	 /*      */       }
	 /*      */     }
	 /*      */     else
	 /*      */     {
	 /* 1014 */       AggregatedValueObject obj = (AggregatedValueObject)objects;
	 /* 1015 */       headls.add((SuperVO)obj.getParentVO());
	 /*      */     }
	 /* 1017 */     return headls;
	 /*      */   }
	 /*      */   
	 /*      */   public IHrPf getIHrPf()
	 /*      */   {
	 /* 1022 */     return (IHrPf)NCLocator.getInstance().lookup(IHrPf.class);
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */   private IPersistenceRetrieve getIPersistenceRetrieve()
	 /*      */   {
	 /* 1028 */     return (IPersistenceRetrieve)NCLocator.getInstance().lookup(IPersistenceRetrieve.class);
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */   private IPersistenceUpdate getPersistenceUpdate()
	 /*      */   {
	 /* 1034 */     return (IPersistenceUpdate)NCLocator.getInstance().lookup(IPersistenceUpdate.class);
	 /*      */   }
	 /*      */   
	 /*      */   private String getStatus(Integer state)
	 /*      */   {
	 /* 1039 */     switch (state.intValue())
	 /*      */     {
	 /*      */     case -1: 
	 /* 1042 */       return ResHelper.getString("6007entry", "06007entry0044");
	 /*      */     case 3: 
	 /* 1044 */       return ResHelper.getString("common", "UC001-0000029");
	 /*      */     case 2: 
	 /* 1046 */       return ResHelper.getString("6007entry", "06007entry0045");
	 /*      */     
	 /*      */ 
	 /*      */     case 0: 
	 /* 1050 */       return ResHelper.getString("6007entry", "06007entry0046");
	 /*      */     
	 /*      */ 
	 /*      */     case 1: 
	 /* 1054 */       return ResHelper.getString("6007entry", "06007entry0047");
	 /*      */     
	 /*      */ 
	 /*      */     case 102: 
	 /* 1058 */       return ResHelper.getString("6007entry", "06007entry0048");
	 /*      */     }
	 /*      */     
	 /*      */     
	 /* 1062 */     return ResHelper.getString("6007entry", "06007entry0044");
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */   public boolean isEnableDrive(String srcBilltype, AggregatedValueObject srcBillVO, String srcAction, String destBillType, String beDrivedActionName)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1080 */     return true;
	 /*      */   }
	 /*      */   
	 /*      */   public <T extends AggregatedValueObject> T insertBill(T billvo)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1086 */     PfBusinessLock pfLock = null;
	 /* 1087 */     IFlowBizItf flowItf = getFlowBizItf(billvo.getParentVO());
	 /*      */     
	 /*      */     try
	 /*      */     {
	 /* 1091 */       pfLock = new PfBusinessLock();
	 /* 1092 */       pfLock.lock(new VOLockData(billvo, flowItf.getBilltype()), new VOConsistenceCheck(billvo, flowItf.getBilltype()));
	 /*      */       
	 /* 1094 */       checkBillCodeRepeat(new AggregatedValueObject[] { billvo });
	 /*      */       
	 /*      */ 
	 /* 1097 */       DefaultValidationService vService = new DefaultValidationService();
	 /* 1098 */       createCustomValidators(vService, "insert");
	 /* 1099 */       SuperVO[] headvos = (SuperVO[])getHeadVO(billvo).toArray(new SuperVO[0]);
	 /* 1100 */       vService.validate(headvos);
	 /*      */       
	 /* 1102 */       billvo.getParentVO().setStatus(2);
	 /* 1103 */       setAuditInfoAndTs((SuperVO)billvo.getParentVO(), true);
	 /* 1104 */       String pk = getMDPersistenceService().saveBillWithRealDelete(billvo);
	 /* 1105 */       billvo.getParentVO().setPrimaryKey(pk);
	 /*      */       
	 /*      */ 
	 /* 1108 */       String billCode = flowItf.getBillNo();
	 /* 1109 */       if (isAutoGenerateBillCode("6101", PubEnv.getPk_group(), flowItf.getPkorg()))
	 /*      */       {
	 /* 1111 */         ((IHrBillCode)NCLocator.getInstance().lookup(IHrBillCode.class)).commitPreBillCode("6101", PubEnv.getPk_group(), flowItf.getPkorg(), billCode);
	 /*      */       }
	 /*      */       
	 /*      */ 
	 /*      */     }
	 /*      */     catch (Exception e)
	 /*      */     {
	 /* 1118 */       if ((e instanceof BillCodeRepeatBusinessException))
	 /*      */       {
	 /* 1120 */         String[] codes = ((BillCodeRepeatBusinessException)e).getRepeatCodes();
	 /* 1121 */         if ((isAutoGenerateBillCode("6101", PubEnv.getPk_group(), flowItf.getPkorg())) && (codes != null))
	 /*      */         {
	 /* 1123 */           for (int i = 0; i < codes.length; i++)
	 /*      */           {
	 /*      */             try
	 /*      */             {
	 /* 1127 */               ((IBillcodeManage)NCLocator.getInstance().lookup(IBillcodeManage.class)).AbandonBillCode_RequiresNew("6101", PubEnv.getPk_group(), flowItf.getPkorg(), codes[i]);
	 /*      */ 
	 /*      */ 
	 /*      */             }
	 /*      */             catch (Exception e2)
	 /*      */             {
	 /*      */ 
	 /*      */ 
	 /* 1135 */               Logger.error(e2.getMessage(), e2);
	 /*      */             }
	 /*      */           }
	 /*      */         }
	 /* 1139 */         throw ((BillCodeRepeatBusinessException)e);
	 /*      */       }
	 /*      */       
	 /*      */ 
	 /* 1143 */       if (isAutoGenerateBillCode("6101", PubEnv.getPk_group(), flowItf.getPkorg()))
	 /*      */       {
	 /* 1145 */         ((IHrBillCode)NCLocator.getInstance().lookup(IHrBillCode.class)).rollbackPreBillCode("6101", PubEnv.getPk_group(), flowItf.getPkorg(), flowItf.getBillNo());
	 /*      */       }
	 /*      */       
	 /*      */ 
	 /* 1149 */       throw new BusinessException(e.getMessage());
	 /*      */     }
	 /*      */     finally
	 /*      */     {
	 /* 1153 */       if (pfLock != null)
	 /*      */       {
	 /*      */ 
	 /* 1156 */         pfLock.unLock();
	 /*      */       }
	 /*      */     }
	 /* 1159 */     return billvo;
	 /*      */   }
	 /*      */   
	 /*      */   private boolean isAutoGenerateBillCode(String billType, String pk_group, String pk_org)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1165 */     BillCodeContext billCodeContext = HiCacheUtils.getBillCodeContext(billType, pk_group, pk_org);
	 /* 1166 */     return billCodeContext != null;
	 /*      */   }
	 /*      */   
	 /*      */   public AggEntryapplyVO[] queryByCondition(LoginContext context, String condition)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1172 */     return (AggEntryapplyVO[])queryByCondition(context, AggEntryapplyVO.class, condition);
	 /*      */   }
	 /*      */   
	 /*      */   public AggEntryapplyVO queryByPk(String pk)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1178 */     return (AggEntryapplyVO)queryByPk(AggEntryapplyVO.class, pk);
	 /*      */   }
	 /*      */   
	 /*      */   public <T extends AggregatedValueObject> T[] saveBatchBill(T... billvos)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1184 */     PfBusinessLock pfLock = null;
	 /* 1185 */     IFlowBizItf flowItf = getFlowBizItf(billvos[0].getParentVO());
	 /*      */     
	 /*      */     try
	 /*      */     {
	 /* 1189 */       pfLock = new PfBusinessLock();
	 /* 1190 */       pfLock.lock(new VOsLockData(billvos, flowItf.getBilltype()), new VOsConsistenceCheck(billvos, flowItf.getBilltype()));
	 /*      */       
	 /* 1192 */       DefaultValidationService vService = new DefaultValidationService();
	 /* 1193 */       createCustomValidators(vService, "insert");
	 /* 1194 */       SuperVO[] headvos = (SuperVO[])getHeadVO(billvos).toArray(new SuperVO[0]);
	 /* 1195 */       vService.validate(headvos);
	 /* 1196 */       for (T billvo : billvos)
	 /*      */       {
	 /*      */ 
	 /* 1199 */         billvo.getParentVO().setStatus(2);
	 /* 1200 */         setAuditInfoAndTs((SuperVO)billvo.getParentVO(), true);
	 /* 1201 */         String pk = getMDPersistenceService().saveBillWithRealDelete(billvo);
	 /* 1202 */         billvo.getParentVO().setPrimaryKey(pk);
	 /*      */       }
	 /*      */     }
	 /*      */     finally
	 /*      */     {
	 /* 1207 */       if (pfLock != null)
	 /*      */       {
	 /*      */ 
	 /* 1210 */         pfLock.unLock();
	 /*      */       }
	 /*      */     }
	 /* 1213 */     return billvos;
	 /*      */   }
	 /*      */   
	 /*      */   public <T extends AggregatedValueObject> T updateBill(T billvo, boolean blChangeAuditInfo)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1219 */     PfBusinessLock pfLock = null;
	 /* 1220 */     IFlowBizItf flowItf = getFlowBizItf(billvo.getParentVO());
	 /*      */     
	 /*      */     try
	 /*      */     {
	 /* 1224 */       pfLock = new PfBusinessLock();
	 /* 1225 */       pfLock.lock(new VOLockData(billvo, flowItf.getBilltype()), new VOConsistenceCheck(billvo, flowItf.getBilltype()));
	 /*      */       
	 /* 1227 */       checkBillCodeRepeat(new AggregatedValueObject[] { billvo });
	 /*      */       
	 /* 1229 */       DefaultValidationService vService = new DefaultValidationService();
	 /* 1230 */       createCustomValidators(vService, "update");
	 /* 1231 */       SuperVO[] headvos = (SuperVO[])getHeadVO(billvo).toArray(new SuperVO[0]);
	 /* 1232 */       vService.validate(headvos);
	 /*      */       
	 /* 1234 */       billvo.getParentVO().setStatus(1);
	 /* 1235 */       setAuditInfoAndTs((SuperVO)billvo.getParentVO(), blChangeAuditInfo);
	 /* 1236 */       String pk = getMDPersistenceService().saveBillWithRealDelete(billvo);
	 /* 1237 */       billvo.getParentVO().setPrimaryKey(pk);
	 /*      */ 
	 /*      */     }
	 /*      */     catch (BusinessException e)
	 /*      */     {
	 /* 1242 */       if ((e instanceof BillCodeRepeatBusinessException))
	 /*      */       {
	 /* 1244 */         String[] codes = ((BillCodeRepeatBusinessException)e).getRepeatCodes();
	 /* 1245 */         if ((isAutoGenerateBillCode("6101", PubEnv.getPk_group(), flowItf.getPkorg())) && (codes != null))
	 /*      */         {
	 /* 1247 */           for (int i = 0; i < codes.length; i++)
	 /*      */           {
	 /*      */             try
	 /*      */             {
	 /* 1251 */               ((IBillcodeManage)NCLocator.getInstance().lookup(IBillcodeManage.class)).AbandonBillCode_RequiresNew("6101", PubEnv.getPk_group(), flowItf.getPkorg(), codes[i]);
	 /*      */ 
	 /*      */ 
	 /*      */             }
	 /*      */             catch (Exception e2)
	 /*      */             {
	 /*      */ 
	 /*      */ 
	 /* 1259 */               Logger.error(e2.getMessage(), e2);
	 /*      */             }
	 /*      */           }
	 /*      */         }
	 /* 1263 */         throw ((BillCodeRepeatBusinessException)e);
	 /*      */       }
	 /*      */       
	 /* 1266 */       Logger.error(e.getMessage(), e);
	 /* 1267 */       throw e;
	 /*      */     }
	 /*      */     finally
	 /*      */     {
	 /* 1271 */       if (pfLock != null)
	 /*      */       {
	 /*      */ 
	 /* 1274 */         pfLock.unLock();
	 /*      */       }
	 /*      */     }
	 /* 1277 */     return billvo;
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */   private <T extends AggregatedValueObject> void checkBillCodeRepeat(T... billvos)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1288 */     StringBuffer errMsg = new StringBuffer();
	 /* 1289 */     ArrayList<String> repeatCodes = new ArrayList();
	 /* 1290 */     for (T vo : billvos)
	 /*      */     {
	 /* 1292 */       IFlowBizItf itf = (IFlowBizItf)NCObject.newInstance(vo).getBizInterface(IFlowBizItf.class);
	 /* 1293 */       String billCode = itf.getBillNo();
	 /* 1294 */       String pk_entryapply = itf.getBillId();
	 /* 1295 */       String billType = itf.getBilltype();
	 /* 1296 */       String whereSql = "bill_code = '" + NCESAPI.sqlEncode(billCode) + "' and pk_group = '" + PubEnv.getPk_group() + "'  and " + "pk_billtype" + " = '" + billType + "'";
	 /*      */       
	 /*      */ 
	 /* 1299 */       if (!StringUtils.isEmpty(pk_entryapply))
	 /*      */       {
	 /* 1301 */         whereSql = whereSql + " and pk_entryapply <> '" + pk_entryapply + "'";
	 /*      */       }
	 /* 1303 */       int count = ((IPersistenceRetrieve)NCLocator.getInstance().lookup(IPersistenceRetrieve.class)).getCountByCondition("hi_entryapply", whereSql);
	 /*      */       
	 /* 1305 */       if (count > 0)
	 /*      */       {
	 /* 1307 */         errMsg.append('\n' + ResHelper.getString("6007entry", "06007entry0050") + billCode + ResHelper.getString("6007entry", "06007entry0051"));
	 /*      */         
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /* 1313 */         repeatCodes.add(billCode);
	 /*      */       }
	 /*      */     }
	 /*      */     
	 /* 1317 */     if (errMsg.length() > 0)
	 /*      */     {
	 /* 1319 */       BillCodeRepeatBusinessException ex = new BillCodeRepeatBusinessException(ResHelper.getString("6007entry", "06007entry0052") + errMsg.toString());
	 /*      */       
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /* 1325 */       ex.setRepeatCodes((String[])repeatCodes.toArray(new String[0]));
	 /* 1326 */       throw ex;
	 /*      */     }
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */   public boolean isValidBudgetVO(String[] pkPsnjobs, LoginContext context)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1342 */     ValidateResultVO[] resultVOs = ((IOrgBudgetQueryService)NCLocator.getInstance().lookup(IOrgBudgetQueryService.class)).validateBudgetValue(context, pkPsnjobs);
	 /*      */     
	 /*      */ 
	 /* 1345 */     if ((resultVOs == null) || (resultVOs.length <= 0)) { return true;
	 /*      */     }
	 /* 1347 */     for (ValidateResultVO resultVO : resultVOs)
	 /*      */     {
	 /*      */ 
	 /* 1350 */       if (!resultVO.isValid())
	 /*      */       {
	 /* 1352 */         return false;
	 /*      */       }
	 /*      */     }
	 /* 1355 */     return true;
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */   public AggregatedValueObject[] getValidBudgetVO(AggregatedValueObject[] vos, LoginContext context)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1369 */     Vector<AggregatedValueObject> passVO = new Vector();
	 /*      */     
	 /* 1371 */     String[] pkPsnjobs = new String[vos.length];
	 /* 1372 */     for (int i = 0; i < vos.length; i++)
	 /*      */     {
	 /* 1374 */       pkPsnjobs[i] = ((EntryapplyVO)vos[i].getParentVO()).getPk_psnjob();
	 /* 1375 */       passVO.add(vos[i]);
	 /*      */     }
	 /*      */     
	 /* 1378 */     ValidateResultVO[] resultVOs = ((IOrgBudgetQueryService)NCLocator.getInstance().lookup(IOrgBudgetQueryService.class)).validateBudgetValue(context, pkPsnjobs);
	 /*      */     
	 /*      */ 
	 /* 1381 */     if ((resultVOs == null) || (resultVOs.length < 0)) { return vos;
	 /*      */     }
	 /* 1383 */     for (ValidateResultVO resultVO : resultVOs)
	 /*      */     {
	 /*      */ 
	 /* 1386 */       if (!resultVO.isValid())
	 /*      */       {
	 /* 1388 */         String pk_org = resultVO.getPk_org();
	 /*      */         
	 /* 1390 */         for (AggregatedValueObject vo : vos)
	 /*      */         {
	 /* 1392 */           PsnJobVO psnJobVO = (PsnJobVO)getIPersistenceRetrieve().retrieveByPk(null, PsnJobVO.class, ((EntryapplyVO)vo.getParentVO()).getPk_psnjob());
	 /*      */           
	 /*      */ 
	 /* 1395 */           if ((psnJobVO == null) || (pk_org.equals(psnJobVO.getPk_org()))) { passVO.removeElement(vo);
	 /*      */           }
	 /*      */         }
	 /*      */       }
	 /*      */     }
	 /*      */     
	 /*      */ 
	 /*      */ 
	 /* 1403 */     sendMessage(resultVOs);
	 /*      */     
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /* 1450 */     return (AggregatedValueObject[])passVO.toArray(new AggregatedValueObject[0]);
	 /*      */   }
	 /*      */   
	 /*      */   private void sendMessage(ValidateResultVO[] resultVOs)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1456 */     for (ValidateResultVO vo : resultVOs)
	 /*      */     {
	 /* 1458 */       if (!StringUtils.isEmpty(vo.getHintMsg()))
	 /*      */       {
	 /*      */ 
	 /*      */ 
	 /* 1462 */         OrgVO org = ((IAOSQueryService)NCLocator.getInstance().lookup(IAOSQueryService.class)).queryHROrgByOrgPK(vo.getPk_org());
	 /* 1463 */         String hrorg = org == null ? vo.getPk_org() : org.getPk_org();
	 /* 1464 */         IHRMessageSend messageSendService = (IHRMessageSend)NCLocator.getInstance().lookup(IHRMessageSend.class);
	 /*      */         
	 /* 1466 */         HRBusiMessageVO messageInfoVO = new HRBusiMessageVO();
	 /*      */         
	 /*      */ 
	 /* 1469 */         Hashtable<String, Object> value = new Hashtable();
	 /* 1470 */         value.put("reason", vo.getHintMsg());
	 /* 1471 */         messageInfoVO.setBusiVarValues(value);
	 /*      */         
	 /* 1473 */         messageInfoVO.setBillVO(vo);
	 /* 1474 */         String tempCode = "600301";
	 /* 1475 */         messageInfoVO.setMsgrescode(tempCode);
	 /* 1476 */         messageInfoVO.setPkorgs(new String[] { hrorg });
	 /*      */         
	 /* 1478 */         messageSendService.sendBuziMessage_RequiresNew(new HRBusiMessageVO[] { messageInfoVO });
	 /*      */       }
	 /*      */     }
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */   public ValidBudgetResultVO validateBudget(AggregatedValueObject[] vos, LoginContext context)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1520 */     ValidBudgetResultVO validVO = new ValidBudgetResultVO();
	 /*      */     
	 /* 1522 */     Vector<AggregatedValueObject> passVO = new Vector();
	 /*      */     
	 /* 1524 */     String[] pkPsnjobs = new String[vos.length];
	 /* 1525 */     for (int i = 0; i < vos.length; i++)
	 /*      */     {
	 /* 1527 */       pkPsnjobs[i] = ((EntryapplyVO)vos[i].getParentVO()).getPk_psnjob();
	 /* 1528 */       passVO.add(vos[i]);
	 /*      */     }
	 /*      */     
	 /* 1531 */     ValidateResultVO[] resultVOs = ((IOrgBudgetQueryService)NCLocator.getInstance().lookup(IOrgBudgetQueryService.class)).validateBudgetValue(context, pkPsnjobs);
	 /*      */     
	 /* 1533 */     String errorMsg = "";
	 /* 1534 */     String hintMsg = "";
	 /*      */     
	 /* 1536 */     if ((resultVOs != null) && (resultVOs.length > 0))
	 /*      */     {
	 /* 1538 */       for (ValidateResultVO resultVO : resultVOs)
	 /*      */       {
	 /*      */ 
	 /* 1541 */         if (!resultVO.isValid())
	 /*      */         {
	 /* 1543 */           errorMsg = errorMsg + "\n" + resultVO.getHintMsg();
	 /* 1544 */           String pk_org = resultVO.getPk_org();
	 /*      */           
	 /* 1546 */           for (AggregatedValueObject vo : vos)
	 /*      */           {
	 /* 1548 */             PsnJobVO psnJobVO = (PsnJobVO)getIPersistenceRetrieve().retrieveByPk(null, PsnJobVO.class, ((EntryapplyVO)vo.getParentVO()).getPk_psnjob());
	 /*      */             
	 /*      */ 
	 /* 1551 */             if ((psnJobVO == null) || (pk_org.equals(psnJobVO.getPk_org()))) passVO.removeElement(vo);
	 /*      */           }
	 /*      */         }
	 /* 1554 */         else if (resultVO.getHintMsg() != null)
	 /*      */         {
	 /* 1556 */           hintMsg = hintMsg + "\n" + resultVO.getHintMsg();
	 /*      */         }
	 /*      */       }
	 /*      */     }
	 /*      */     
	 /* 1561 */     String pk_sort = "1001Z7BUDGET00000002";
	 /* 1562 */     for (ValidateResultVO vo : resultVOs)
	 /*      */     {
	 /* 1564 */       if (!StringUtils.isEmpty(vo.getHintMsg()))
	 /*      */       {
	 /*      */ 
	 /*      */ 
	 /* 1568 */         OrgVO org = ((IAOSQueryService)NCLocator.getInstance().lookup(IAOSQueryService.class)).queryHROrgByOrgPK(vo.getPk_org());
	 /* 1569 */         String hrorg = org == null ? vo.getPk_org() : org.getPk_org();
	 /* 1570 */         INotice setvice = (INotice)NCLocator.getInstance().lookup(INotice.class);
	 /* 1571 */         NoticeTempletVO[] nt = setvice.queryDistributedTemplates(pk_sort, PubEnv.getPk_group(), hrorg, Boolean.valueOf(true));
	 /* 1572 */         if ((nt != null) && (nt.length > 0))
	 /*      */         {
	 /* 1574 */           String content = nt[0].getContent();
	 /* 1575 */           if ((content != null) && (content.indexOf("<#reason#>") >= 0))
	 /*      */           {
	 /* 1577 */             nt[0].setContent(content.replace("<#reason#>", vo.getHintMsg()));
	 /*      */           }
	 /*      */           else
	 /*      */           {
	 /* 1581 */             nt[0].setContent(content + '\n' + vo.getHintMsg());
	 /*      */           }
	 /* 1583 */           if ((StringUtils.isEmpty(nt[0].getCurrentUserPk())) || (nt[0].getCurrentUserPk().length() != 20))
	 /*      */           {
	 /*      */ 
	 /* 1586 */             nt[0].setCurrentUserPk((PubEnv.getPk_user() != null) && (PubEnv.getPk_user().length() == 20) ? PubEnv.getPk_user() : "NC_USER0000000000000");
	 /*      */           }
	 /*      */           
	 /* 1589 */           setvice.sendNotice_RequiresNew(nt[0], hrorg, false);
	 /*      */         }
	 /*      */       } }
	 /* 1592 */     validVO.setVos((AggregatedValueObject[])passVO.toArray(new AggregatedValueObject[0]));
	 /* 1593 */     validVO.setErrorMsg(errorMsg);
	 /* 1594 */     validVO.setHintMsg(hintMsg);
	 /* 1595 */     return validVO;
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */   public Object[] queryWaitforBills(Class<? extends AggregatedValueObject> aggVOClass, String billType, boolean isApproveSite, LoginContext context)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1602 */     String condition = HiSQLHelper.getQueryCondition(aggVOClass, billType, context, isApproveSite);
	 /* 1603 */     return queryByCondition(aggVOClass, condition);
	 /*      */   }
	 /*      */   
	 /*      */   public String[] getPsndocPks(String[] pks)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1609 */     if ((pks == null) || (pks.length == 0))
	 /*      */     {
	 /* 1611 */       return null;
	 /*      */     }
	 /*      */     
	 /* 1614 */     InSQLCreator isc = new InSQLCreator();
	 /* 1615 */     String insql = isc.getInSQL(pks);
	 /* 1616 */     String strCondition = "pk_psnjob in (" + insql + ")";
	 /* 1617 */     PsnJobVO[] PsnJobVOs = (PsnJobVO[])((IPersistenceRetrieve)NCLocator.getInstance().lookup(IPersistenceRetrieve.class)).retrieveByClause(null, PsnJobVO.class, strCondition);
	 /*      */     
	 /* 1619 */     if (ArrayUtils.isEmpty(PsnJobVOs))
	 /*      */     {
	 /* 1621 */       return null;
	 /*      */     }
	 /*      */     
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /* 1628 */     List<String> listdocpks = new ArrayList();
	 /* 1629 */     for (int i = 0; i < pks.length; i++)
	 /*      */     {
	 /* 1631 */       for (int j = 0; j < PsnJobVOs.length; j++)
	 /*      */       {
	 /* 1633 */         if (pks[i].equals(PsnJobVOs[j].getPk_psnjob()))
	 /*      */         {
	 /* 1635 */           listdocpks.add(PsnJobVOs[j].getPk_psndoc());
	 /*      */         }
	 /*      */       }
	 /*      */     }
	 /*      */     
	 /*      */ 
	 /* 1641 */     return (String[])listdocpks.toArray(new String[0]);
	 /*      */   }
	 /*      */   
	 /*      */   public int getBillCount(String billtype, String whereOrg)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1647 */     SuperVO[] billvos = ((IPersistenceRetrieve)NCLocator.getInstance().lookup(IPersistenceRetrieve.class)).retrieveByClause(null, getBillClassName(billtype), " approve_state = -1 and pk_billtype = '" + billtype + "' " + whereOrg + " ");
	 /*      */     
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /* 1654 */     if ((billvos == null) || (billvos.length == 0))
	 /*      */     {
	 /* 1656 */       return 0;
	 /*      */     }
	 /* 1658 */     for (int i = 0; i < billvos.length; i++)
	 /*      */     {
	 /* 1660 */       IFlowBizItf itf = (IFlowBizItf)NCObject.newInstance(billvos[i]).getBizInterface(IFlowBizItf.class);
	 /* 1661 */       String type = StringUtils.isBlank(itf.getTranstype()) ? itf.getBilltype() : itf.getTranstype();
	 /* 1662 */       WorkflownoteVO[] items = ((IPFWorkflowQry)NCLocator.getInstance().lookup(IPFWorkflowQry.class)).queryWorkitems(itf.getBillId(), type, WorkflowTypeEnum.Approveflow.getIntValue(), 0);
	 /*      */       
	 /*      */ 
	 /* 1665 */       if ((items != null) && (items.length > 0))
	 /*      */       {
	 /* 1667 */         return 1;
	 /*      */       }
	 /*      */     }
	 /* 1670 */     return 0;
	 /*      */   }
	 /*      */   
	 /*      */   private Class getBillClassName(String billtype) throws BusinessException
	 /*      */   {
	 /*      */     try
	 /*      */     {
	 /* 1677 */       if ("6101".equals(billtype))
	 /*      */       {
	 /* 1679 */         return EntryapplyVO.class;
	 /*      */       }
	 /* 1681 */       if ("6111".equals(billtype))
	 /*      */       {
	 /* 1683 */         return Class.forName("nc.vo.trn.regmng.RegapplyVO");
	 /*      */       }
	 /*      */       
	 /*      */ 
	 /* 1687 */       return Class.forName("nc.vo.trn.transmng.StapplyVO");
	 /*      */ 
	 /*      */     }
	 /*      */     catch (Exception e)
	 /*      */     {
	 /* 1692 */       Logger.error(e.getMessage(), e);
	 /* 1693 */       throw new BusinessException(e.getMessage());
	 /*      */     }
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */   public HashMap<String, Object> manualExecBills(AggEntryapplyVO[] bills, LoginContext context, UFLiteralDate effectDate)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1701 */     HashMap<String, Object> result = execBills(bills, context, true);
	 /*      */     
	 /* 1703 */     return result;
	 /*      */   }
	 /*      */   
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */ 
	 /*      */   public String validateValidBudget(LoginContext context, AggEntryapplyVO[] billvos)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1720 */     String strWarningMsg = "";
	 /* 1721 */     if (ArrayUtils.isEmpty(billvos))
	 /*      */     {
	 /* 1723 */       return strWarningMsg;
	 /*      */     }
	 /*      */     
	 /* 1726 */     ValidateResultVO[] resultVOs = null;
	 /*      */     
	 /* 1728 */     String[] strPk_psnjobs = new String[billvos.length];
	 /* 1729 */     for (int i = 0; i < strPk_psnjobs.length; i++)
	 /*      */     {
	 /* 1731 */       strPk_psnjobs[i] = ((EntryapplyVO)billvos[i].getParentVO()).getPk_psnjob();
	 /*      */     }
	 /*      */     
	 /* 1734 */     if ((strPk_psnjobs != null) && (strPk_psnjobs.length > 0))
	 /*      */     {
	 /* 1736 */       resultVOs = ((IOrgBudgetQueryService)NCLocator.getInstance().lookup(IOrgBudgetQueryService.class)).validateBudgetValue(context, strPk_psnjobs);
	 /*      */     }
	 /*      */     
	 /* 1739 */     if (resultVOs != null)
	 /*      */     {
	 /* 1741 */       for (ValidateResultVO resultVO : resultVOs)
	 /*      */       {
	 /*      */ 
	 /* 1744 */         if ((resultVO.getHintMsg() != null) && (resultVO.isValid()))
	 /*      */         {
	 /* 1746 */           strWarningMsg = strWarningMsg + "\n" + resultVO.getHintMsg();
	 /*      */         }
	 /* 1748 */         else if (resultVO.getHintMsg() != null)
	 /*      */         {
	 /* 1750 */           throw new BusinessException(resultVO.getHintMsg());
	 /*      */         }
	 /*      */       }
	 /*      */     }
	 /*      */     
	 /* 1755 */     return strWarningMsg;
	 /*      */   }
	 /*      */   
	 /*      */   public AggEntryapplyVO[] queryByCondition(LoginContext context, String[] psndocPKS)
	 /*      */     throws BusinessException
	 /*      */   {
	 /* 1761 */     InSQLCreator isc = new InSQLCreator();
	 /* 1762 */     String insql = isc.getInSQL(psndocPKS);
	 /* 1763 */     String condition = " pk_psndoc in (" + insql + " ) and approve_state <> 0 and approve_state <> 102 ";
	 /* 1764 */     AggEntryapplyVO[] entryVOS = queryByCondition(context, condition);
	 /* 1765 */     return entryVOS;
	 /*      */   }

}
