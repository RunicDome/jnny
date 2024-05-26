package nc.impl.wa.payroll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
import nc.hr.frame.persistence.HrBatchService;
import nc.hr.frame.persistence.IValidatorFactory;
import nc.hr.frame.persistence.PersistenceDAO;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.impl.wa.paydata.PaydataDAO;
import nc.itf.hr.pf.HrPfHelper;
import nc.itf.hr.pf.IHrPf;
import nc.itf.hr.wa.IPayrollManageService;
import nc.itf.hr.wa.IPayrollQueryService;
import nc.itf.uap.pf.metadata.IFlowBizItf;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.md.data.access.NCObject;
import nc.pub.billcode.itf.IBillcodeManage;
import nc.pub.billcode.vo.BillCodeContext;
import nc.pub.tools.HiCacheUtils;
import nc.uif.pub.exception.UifException;
import nc.vo.fct.ar.entity.AggCtArVO;
import nc.vo.fct.ar.entity.CtArVO;
import nc.vo.fct.entity.CtAbstractVO;
import nc.vo.hr.pf.PFQueryParams;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.OrgVO;
import nc.vo.pcm.contractalter.ContrAlterBillVO;
import nc.vo.pcm.contractalter.ContrAlterHeadVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.sm.UserVO;
import nc.vo.ta.leave.LeavehVO;
import nc.vo.ta.overtime.AggOvertimeVO;
import nc.vo.uap.pf.PfProcessBatchRetObject;
import nc.vo.uif2.LoginContext;
import nc.vo.util.BDPKLockUtil;
import nc.vo.util.BDVersionValidationUtil;
import nc.vo.wa.func.WherePartUtil;
import nc.vo.wa.payroll.AggPayrollVO;
import nc.vo.wa.payroll.PayrollVO;
import nc.vo.wa.pub.HRWACommonConstants;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
// 薪资发放
public class PayrollManageServiceImpl implements IPayrollManageService, IPayrollQueryService{
	private final String DOC_NAME = "Payroll";
	/*     */   private SimpleDocServiceTemplate serviceTemplate;
	/*     */   private HrBatchService hrbatchservice;
	/*     */   private PayrollDAO payrollDao;
	/*     */   
	/*     */   public PayrollManageServiceImpl() {}
	/*     */   
	/*     */   public void setValidatorFactory(IValidatorFactory docValidatorFactory) {
	/*  69 */     getServiceTemplate().setValidatorFactory(docValidatorFactory);
	/*     */   }
	/*     */   
	/*     */   private PayrollDAO getPayrollDao() {
	/*  73 */     if (this.payrollDao == null) {
	/*  74 */       this.payrollDao = new PayrollDAO();
	/*     */     }
	/*     */     
	/*  77 */     return this.payrollDao;
	/*     */   }
	/*     */   
	/*     */   private SimpleDocServiceTemplate getServiceTemplate() {
	/*  81 */     if (this.serviceTemplate == null) {
	/*  82 */       this.serviceTemplate = new SimpleDocServiceTemplate("Payroll");
	/*  83 */       this.serviceTemplate.setDispatchEvent(false);
	/*  84 */       this.serviceTemplate.setDefaultUniqueValidationEnabled(false);
	/*     */     }
	/*     */     
	/*  87 */     return this.serviceTemplate;
	/*     */   }
	/*     */   
	/*  90 */   private HrBatchService getHRBatchServic() { if (this.hrbatchservice == null) {
	/*  91 */       this.hrbatchservice = new HrBatchService("Payroll");
	/*  92 */       this.hrbatchservice.setDispatchEvent(false);
	/*  93 */       this.hrbatchservice.setDefaultUniqueValidationEnabled(false);
	/*     */     }
	/*     */     
	/*  96 */     return this.hrbatchservice;
	/*     */   }
	/*     */   
	/*     */   public AggPayrollVO[] queryByCondition(LoginContext context, String condition, PFQueryParams queryParams, Boolean isApprove)
	/*     */     throws BusinessException
	/*     */   {
	/* 102 */     if (queryParams == null) {
	/* 103 */       return null;
	/*     */     }
	/* 105 */     String orderby = null;
	/* 106 */     if ((isApprove == null) || (!isApprove.booleanValue())) {
	/* 107 */       orderby = " wa_payroll.billstate asc, wa_payroll.billcode";
	/*     */     }
	/*     */     else {
	/* 110 */       if (!StringUtil.isEmpty(condition))
	/*     */       {
	/*     */ 
	/*     */ 
	/*     */ 
	/* 115 */         if (condition.indexOf("billstate = 1") != -1) {
	/* 116 */           condition = condition.replace("billstate = 1", "billstate in (3,2)");
	/* 117 */         } else if (condition.indexOf("billstate = 0") != -1) {
	/* 118 */           condition = condition.replace("billstate = 0", "billstate in (0,2,1,102)");
	/*     */ 
	/*     */         }
	/* 121 */         else if ((condition.indexOf("billstate in (0,1)") != -1) || (condition.indexOf("billstate in (1,0)") != -1))
	/*     */         {
	/* 123 */           condition = condition.replace("billstate in (0,1)", " billstate in (0,2,1,3,102) ");
	/* 124 */           condition = condition.replace("billstate in (1,0)", " billstate in (0,2,1,3,102) ");
	/*     */         }
	/*     */         else
	/*     */         {
	/* 128 */           condition = condition + " and billstate in (0,2,1,3,102)";
	/*     */         }
	/*     */       } else {
	/* 131 */         condition = " billstate in (2,3)";
	/*     */       }
	/*     */       
	/* 134 */       orderby = " wa_payroll.billstate desc, wa_payroll.billcode";
	/*     */     }
	/* 136 */     if (StringUtil.isEmpty(condition)) {
	/* 137 */       condition = " pk_org = '" + context.getPk_org() + "'";
	/*     */     } else {
	/* 139 */       condition = condition + " and pk_org = '" + context.getPk_org() + "'";
	/*     */     }
	/*     */     
	/*     */ 
	/* 143 */     String strNormalSQL = HrPfHelper.getQueryCondition(AggPayrollVO.class, "wa_payroll", queryParams.isApproveSite(), context.getPk_org(), queryParams.getBillState());
	/*     */     
	/*     */ 
	/* 146 */     if (StringUtils.isBlank(strNormalSQL))
	/*     */     {
	/* 148 */       strNormalSQL = " 1=1";
	/*     */     }
	/*     */     
	/* 151 */     IFlowBizItf itf = HrPfHelper.getFlowBizItf(AggPayrollVO.class);
	/*     */     
	/* 153 */     String strApproveDatePeriod = HrPfHelper.getApproveDatePeriod(itf, "wa_payroll", queryParams.getApproveDateParam(), queryParams.getBillState());
	/*     */     
	/* 155 */     condition = condition + " and " + strNormalSQL;
	/*     */     
	/* 157 */     if (StringUtils.isNotBlank(strApproveDatePeriod))
	/*     */     {
	/* 159 */       condition = condition + " and " + strApproveDatePeriod;
	/*     */     }
	/*     */     
	/* 162 */     AggPayrollVO[] aggvos = (AggPayrollVO[])getServiceTemplate().queryByCondition(context, AggPayrollVO.class, condition, orderby);
	/*     */     
	/*     */ 
	/* 165 */     if (ArrayUtils.isEmpty(aggvos)) {
	/* 166 */       return null;
	/*     */     }
	/*     */     
	/* 169 */     for (AggPayrollVO aggvo : aggvos) {
	/* 170 */       updateYfandSf(aggvo);
	/*     */     }
	/* 172 */     return aggvos;
	/*     */   }
	/*     */   
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */   public AggPayrollVO queryByPk(String pk)
	/*     */     throws BusinessException
	/*     */   {
	/* 183 */     AggPayrollVO aggvo = (AggPayrollVO)getServiceTemplate().queryByPk(AggPayrollVO.class, pk);
	/* 184 */     updateYfandSf(aggvo);
	/* 185 */     return aggvo;
	/*     */   }
	/*     */   
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */   private void batchupdateYfandSf(String tablename, PayrollVO[] vos)
	/*     */     throws DAOException
	/*     */   {
	/* 196 */     if ((vos == null) || (vos.length <= 0)) {
	/* 197 */       return;
	/*     */     }
	/* 199 */     HashMap<String, PayrollVO> map = getPayrollDao().batchsumYf(tablename, vos);
	/* 200 */     for (PayrollVO vo : vos) {
	/* 201 */       String key = vo.getPk_wa_class() + vo.getCyear() + vo.getCperiod();
	/* 202 */       PayrollVO vo2 = (PayrollVO)map.get(key);
	/* 203 */       vo.setYf(vo2.getYf());
	/* 204 */       vo.setSf(vo2.getSf());
	/*     */     }
	/*     */   }
	/*     */   
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */   private void updateYfandSf(AggPayrollVO aggvo)
	/*     */     throws DAOException
	/*     */   {
	/* 215 */     if (aggvo == null)
	/* 216 */       return;
	/* 217 */     PayrollVO mainvo = (PayrollVO)aggvo.getParentVO();
	/* 218 */     PayrollVO yf = getPayrollDao().sumYf(mainvo);
	/* 219 */     mainvo.setYf(yf.getYf());
	/* 220 */     mainvo.setSf(yf.getSf());
	/*     */   }
	/*     */   
	/*     */   public void delete(AggPayrollVO vo) throws BusinessException
	/*     */   {
	/* 225 */     getServiceTemplate().delete(vo);
	/*     */   }
	/*     */   
	/*     */   public AggPayrollVO insert(AggPayrollVO vo) throws BusinessException
	/*     */   {
	/* 230 */     BDPKLockUtil.lockString(new String[] { ((PayrollVO)vo.getParentVO()).getPk_wa_class() });
	/* 231 */     getPayrollDao().checkPayrollCode(vo);
	/* 232 */     getPayrollDao().checkPayrollClass(vo);
	/* 233 */     AggPayrollVO vo2 = (AggPayrollVO)getServiceTemplate().insert(vo);
	/* 234 */     updateYfandSf(vo2);
	/* 235 */     return vo2;
	/*     */   }
	/*     */   
	/*     */   public AggPayrollVO update(AggPayrollVO vo) throws BusinessException
	/*     */   {
	/* 240 */     getPayrollDao().checkPayrollCode(vo);
	/* 241 */     AggPayrollVO aggvo = (AggPayrollVO)getServiceTemplate().update(vo, true);
	/* 242 */     return queryByPk(aggvo.getParentVO().getPrimaryKey());
	/*     */   }
	/*     */   
	/*     */   public AggPayrollVO seal(AggPayrollVO vo) throws BusinessException
	/*     */   {
	/* 247 */     return (AggPayrollVO)getServiceTemplate().sealObject(vo, true);
	/*     */   }
	/*     */   
	/*     */   public AggPayrollVO unSeal(AggPayrollVO vo) throws BusinessException
	/*     */   {
	/* 252 */     return (AggPayrollVO)getServiceTemplate().unSealObject(vo, true);
	/*     */   }
	/*     */   
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */   public AggPayrollVO doCommit(AggPayrollVO vo)
	/*     */     throws BusinessException
	/*     */   {
	/* 264 */     PayrollVO payrollvo = (PayrollVO)vo.getParentVO();
	/* 265 */     payrollvo.setApplydate(payrollvo.getApplydate());
	/* 266 */     payrollvo.setApprovedate(null);
	/* 267 */     payrollvo.setBillstate(Integer.valueOf(3));
	/*     */     
	/* 269 */     AggPayrollVO newVO = (AggPayrollVO)getServiceTemplate().update(vo, false);
					senOaData(newVO);
	/* 270 */     updateYfandSf(newVO);
						
					
	/* 271 */     return newVO;
	/*     */   }
	

	
	private void senOaData(AggPayrollVO temp) throws BusinessException {
			if((((PayrollVO) temp.getParentVO()).getBilltype()).contains("4D44-01")){
				JSONArray headData = null;
				JSONArray bodyData = null;
				PayrollVO ParentVO = (PayrollVO) temp.getParentVO();
				headData = getMainMap(ParentVO);
				bodyData = getDtaileDataMap(temp);
				
				/* 
				 * step-1 调用OA工具类同步数据至OA
				 */
				OaWorkFlowUtil.sendOaData(headData, bodyData, getWorkFlowBill(temp));		
		}
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

	
	private WorkFlowBill getWorkFlowBill(AggPayrollVO temp) throws BusinessException {
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				((PayrollVO) temp.getParentVO()).getOperator());
		String where = " nvl(dr,0) = 0 and pk_bill = '" + temp.getParentVO().getPrimaryKey() + "'";
		WorkFlowBill[] workFlowBills = (WorkFlowBill[]) getHyPubBO()
				.queryByCondition(WorkFlowBill.class, where);
		WorkFlowBill workFlowBill = new WorkFlowBill();
		if(null != workFlowBills && workFlowBills.length > 1){
			throw new BusinessException("查询到多条流程记录");
		}
		if (null != workFlowBills && workFlowBills.length == 1) {
			workFlowBill = workFlowBills[0];
		} else {
			workFlowBill.setPk_group(((CtAbstractVO) temp.getParentVO()).getPk_group());
			workFlowBill.setPk_org(((PayrollVO) temp.getParentVO()).getPk_org());
			workFlowBill.setPk_bill(temp.getParentVO().getPrimaryKey());		
			workFlowBill.setUser_name(userVO.getUser_name());
			workFlowBill.setUser_code(userVO.getUser_code());
			workFlowBill.setBill_status("1");
			workFlowBill.setCreationtime(new UFDate().toStdString());
			workFlowBill.setCreator(((PayrollVO) temp.getParentVO()).getCreator());
			workFlowBill.setWorkflowId("74");
			workFlowBill.setBill_code("4D11-01");
			workFlowBill.setWorkflowName("薪资发放单");
		}
		return workFlowBill;
	}
	private JSONArray getDtaileDataMap(AggPayrollVO temp) {
		// TODO Auto-generated method stub
		return null;
	}
	private JSONArray getMainMap(PayrollVO hvo) throws BusinessException {
		 List list = new ArrayList();
		 
		 OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class, hvo.getPk_org());
			if(null !=orgVO){
				/* 组织 */
				Map szgs =OaWorkFlowUtil.listAdd("szgs",orgVO.getCode() );
				list.add(szgs);
			}
		
		 /*所属组织 */
		 Map szzz =OaWorkFlowUtil.listAdd("szzz",hvo.getPk_org() );
		 list.add(szzz);
		 
		 /*申请单编号 */
		 Map sqdbh =OaWorkFlowUtil.listAdd("sqdbh",hvo.getBillcode());
		 list.add(sqdbh);
		 
		 /*申请单名称*/
		 Map sqdmc =OaWorkFlowUtil.listAdd("sqdmc",hvo.getBillname());
		 list.add(sqdmc);
		 
		 /*薪资方案*/
		 Map xzfa =OaWorkFlowUtil.listAdd("xzfa",hvo.getPk_wa_class());
		 list.add(xzfa);
		 
		 /*薪资期间*/
		 Map xzqj =OaWorkFlowUtil.listAdd("xzqj",hvo.getClassperiod());
		 list.add(xzqj);
		 
		 /*应发合计*/
		 Map yfhj =OaWorkFlowUtil.listAdd("yfhj",hvo.getYf().toString());
		 list.add(yfhj);
		 
		 /*实发合计*/
		 Map sfhj =OaWorkFlowUtil.listAdd("sfhj",hvo.getSf().toString());
		 list.add(sfhj);
		 
		 /*审批状态*/
		 Map spzt =OaWorkFlowUtil.listAdd("spzt",hvo.getBillstate().toString());
		 list.add(spzt);
		 
		 /*申请人*/
		 Map sqr =OaWorkFlowUtil.listAdd("sqr",hvo.getOperator());
		 list.add(sqr);
		 
		 /*申请日期*/
		Map sqrq =OaWorkFlowUtil.listAdd("sqrq",hvo.getApplydate().getYear()+ "-"
				+ hvo.getApplydate().getStrMonth() + "-"
				+ hvo.getApplydate().getStrDay());
		list.add(sqrq);
		
		/*所属集团*/
		 Map szjt =OaWorkFlowUtil.listAdd("szjt",hvo.getPk_group());
		 list.add(szjt);
		 
		 /*薪资发放申请单主键*/
		 Map xzffsqdzj =OaWorkFlowUtil.listAdd("xzffsqdzj",hvo.getPk_payroll());
		 list.add(xzffsqdzj);
		 
		 
		 JSONArray arr = JSONArray.fromObject(list);
		 return arr;
	}
	/*     */   
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */   public AggPayrollVO doApprove(AggPayrollVO vo)
	/*     */     throws BusinessException
	/*     */   {
	/* 283 */     PayrollVO mainvo = (PayrollVO)vo.getParentVO();
	/*     */     
	/* 285 */     UFBoolean isappoved = UFBoolean.FALSE;
	/* 286 */     if (mainvo.getBillstate().intValue() == 1) {
	/* 287 */       isappoved = UFBoolean.TRUE;
	/*     */     }
	/* 289 */     PayrollVO payrollvo = (PayrollVO)vo.getParentVO();
	/* 290 */     payrollvo.setIsapproved(isappoved.toString());
	/* 291 */     new PaydataDAO().updateTableByColKey("wa_periodstate", "isapproved", isappoved, WherePartUtil.getPeriodStateWhere(mainvo.getPk_wa_class(), mainvo.getCyear(), mainvo.getCperiod()));
	/*     */     
	/* 293 */     AggPayrollVO newVO = (AggPayrollVO)getServiceTemplate().update(vo, true);
	/* 294 */     updateYfandSf(newVO);
	/* 295 */     return newVO;
	/*     */   }
	/*     */   
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */   public AggPayrollVO doUnapprove(AggPayrollVO vo)
	/*     */     throws BusinessException
	/*     */   {
	/* 308 */     PayrollVO mainvo = (PayrollVO)vo.getParentVO();
	/* 309 */     AggPayrollVO oldaggvos = (AggPayrollVO)getServiceTemplate().queryByPk(AggPayrollVO.class, mainvo.getPk_payroll());
	/*     */     
	/* 311 */     checkPFPassingState(((PayrollVO)oldaggvos.getParentVO()).getBillstate().intValue());
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
	/* 331 */     getPayrollDao().updatePayroll(mainvo);
	/* 332 */     new PaydataDAO().updateTableByColKey("wa_periodstate", "isapproved", UFBoolean.FALSE, WherePartUtil.getPeriodStateWhere(mainvo.getPk_wa_class(), mainvo.getCyear(), mainvo.getCperiod()));
	/*     */     
	/* 334 */     return queryByPk(mainvo.getPk_payroll());
	/*     */   }
	/*     */   
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */   public void checkPFPassingState(int pfsate)
	/*     */     throws BusinessException
	/*     */   {
	/* 345 */     if ((0 == pfsate) || (102 == pfsate)) {
	/* 346 */       throw new BusinessException(ResHelper.getString("60130payslipaly", "060130payslipaly0557"));
	/*     */     }
	/*     */   }
	/*     */   
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */   public AggPayrollVO doDelete(AggPayrollVO vo)
	/*     */     throws BusinessException
	/*     */   {
	/* 358 */     String billType = (String)vo.getParentVO().getAttributeValue("billtype");
	/* 359 */     String pk_group = (String)vo.getParentVO().getAttributeValue("pk_group");
	/* 360 */     String pk_org = (String)vo.getParentVO().getAttributeValue("pk_org");
	/* 361 */     String bill_code = (String)vo.getParentVO().getAttributeValue("billcode");
	/* 362 */     if (isAutoGenerateBillCode(billType, pk_group, pk_org))
	/*     */     {
	/* 364 */       getIBillcodeManage().returnBillCodeOnDelete(billType, pk_group, pk_org, bill_code, null);
	/*     */     }
	/*     */     
	/* 367 */     PayrollVO payrollvo = (PayrollVO)vo.getParentVO();
	/* 368 */     getPayrollDao().getBaseDao().deleteVO(payrollvo);
	/* 369 */     return vo;
	/*     */   }
	/*     */   
	/*     */ 
	/*     */   private IBillcodeManage getIBillcodeManage()
	/*     */   {
	/* 375 */     return (IBillcodeManage)NCLocator.getInstance().lookup(IBillcodeManage.class);
	/*     */   }
	/*     */   
	/*     */   private boolean isAutoGenerateBillCode(String billType, String pk_group, String pk_org)
	/*     */     throws BusinessException
	/*     */   {
	/* 381 */     BillCodeContext billCodeContext = HiCacheUtils.getBillCodeContext(billType, pk_group, pk_org);
	/* 382 */     return billCodeContext != null;
	/*     */   }
	/*     */   
	/*     */   public AggPayrollVO directApprove(AggPayrollVO aggVO) throws BusinessException
	/*     */   {
	/* 387 */     return doApprove(aggVO);
	/*     */   }
	/*     */   
	/*     */   public void callbackWithDelete(AggPayrollVO vo, String billtype) throws BusinessException
	/*     */   {
	/* 392 */     NCObject ncObj = NCObject.newInstance(vo.getParentVO());
	/*     */     
	/* 394 */     IFlowBizItf itf = (IFlowBizItf)ncObj.getBizInterface(IFlowBizItf.class);
	/*     */     
	/* 396 */     itf.setApproveStatus(Integer.valueOf(-1));
	/*     */     
	/* 398 */     IHrPf hrpf = (IHrPf)NCLocator.getInstance().lookup(IHrPf.class);
	/* 399 */     AggPayrollVO backVO = (AggPayrollVO)hrpf.callbackBill(vo);
	/* 400 */     delete(backVO);
	/*     */   }
	/*     */   
	/*     */   public AggPayrollVO getPayroll(String pk_wa_class, String cyear, String cperiod) throws BusinessException
	/*     */   {
	/* 405 */     PayrollVO mainvo = getPayrollDao().getPayroll(pk_wa_class, cyear, cperiod);
	/* 406 */     if (mainvo == null) {
	/* 407 */       return null;
	/*     */     }
	/* 409 */     AggPayrollVO aggvo = new AggPayrollVO();
	/* 410 */     aggvo.setParentVO(mainvo);
	/* 411 */     return aggvo;
	/*     */   }
	/*     */   
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */   public AggPayrollVO updateApproveDate(AggPayrollVO aggVO)
	/*     */     throws BusinessException
	/*     */   {
	/* 422 */     PayrollVO mainvo = (PayrollVO)aggVO.getParentVO();
	/* 423 */     getPayrollDao().updatePayroll(mainvo);
	/* 424 */     return queryByPk(mainvo.getPk_payroll());
	/*     */   }
	/*     */   
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */   public boolean isPayed(PayrollVO mainvo)
	/*     */     throws BusinessException
	/*     */   {
	/* 435 */     return getPayrollDao().isPayed(mainvo);
	/*     */   }
	/*     */   
	/*     */   public AggPayrollVO doRecall(AggPayrollVO vo) throws BusinessException
	/*     */   {
	/* 440 */     PayrollVO payrollvo = (PayrollVO)vo.getParentVO();
	/*     */     
	/* 442 */     payrollvo.setApprovedate(null);
	/* 443 */     payrollvo.setBillstate(Integer.valueOf(-1));
	/* 444 */     AggPayrollVO newVO = (AggPayrollVO)getServiceTemplate().update(vo, true);
	/* 445 */     updateYfandSf(newVO);
	/* 446 */     return newVO;
	/*     */   }
	/*     */   
	/*     */   private String createTempTable(PayrollVO[] prvos) throws BusinessException {
	/* 450 */     InSQLCreator isc = new InSQLCreator();
	/*     */     
	/* 452 */     String[] strAttrs = { "pk_payroll", "billcode", "pk_wa_class", "cyear", "cperiod" };
	/* 453 */     String tablename = isc.insertValues("wa_temp_datab", HRWACommonConstants.DATAB_COLUMN, strAttrs, prvos);
	/*     */     
	/* 455 */     return tablename;
	/*     */   }
	/*     */   
	/*     */   private String getPeriodStateWhereBatch(String tablename) {
	/* 459 */     StringBuffer whereBf = new StringBuffer();
	/* 460 */     whereBf.append(" wa_periodstate.pk_wa_class = ");
	/* 461 */     whereBf.append(tablename);
	/* 462 */     whereBf.append(".pk_wa_class and wa_periodstate.pk_wa_period in");
	/* 463 */     whereBf.append("(select pk_wa_period from wa_period,wa_waclass ," + tablename);
	/* 464 */     whereBf.append("where wa_period.pk_periodscheme = wa_waclass.pk_periodscheme ");
	/* 465 */     whereBf.append("and wa_waclass.pk_wa_class = ");
	/* 466 */     whereBf.append(tablename);
	/* 467 */     whereBf.append(".pk_wa_class and wa_period.cyear = ");
	/* 468 */     whereBf.append(tablename);
	/* 469 */     whereBf.append(".cyear and wa_period.cperiod = ");
	/* 470 */     whereBf.append(tablename);
	/* 471 */     whereBf.append(".cperiod)");
	/* 472 */     return whereBf.toString();
	/*     */   }
	/*     */   
	/*     */   public PfProcessBatchRetObject doDirectUnapprove(AggregatedValueObject[] bills) throws Exception
	/*     */   {
	/* 477 */     BDVersionValidationUtil.validateVersion(bills);
	/* 478 */     PfProcessBatchRetObject retObj = ((IHrPf)NCLocator.getInstance().lookup(IHrPf.class)).directUnApprove(bills);
	/* 479 */     PaydataDAO dao = new PaydataDAO();
	/* 480 */     AggPayrollVO aggVO = null;
	/* 481 */     PayrollVO mainvo = null;
	/* 482 */     PayrollVO[] prvos = null;
	/* 483 */     String[] stateSql = null;
	/* 484 */     for (int i = 0; i < retObj.getRetObj().length; i++) {
	/* 485 */       aggVO = (AggPayrollVO)retObj.getRetObj()[i];
	/* 486 */       mainvo = (PayrollVO)aggVO.getParentVO();
	/* 487 */       mainvo.setIsapproved(UFBoolean.FALSE.toString());
	/*     */       
	/* 489 */       prvos = (PayrollVO[])ArrayUtils.add(prvos, mainvo);
	/* 490 */       StringBuffer bsql = new StringBuffer();
	/* 491 */       bsql.append("update wa_periodstate set isapproved = ");
	/* 492 */       bsql.append("'N'");
	/* 493 */       bsql.append(" where ");
	/* 494 */       bsql.append(WherePartUtil.getPeriodStateWhere(mainvo.getPk_wa_class(), mainvo.getCyear(), mainvo.getCperiod()));
	/* 495 */       stateSql = (String[])ArrayUtils.add(stateSql, bsql.toString());
	/*     */     }
	/*     */     
	/*     */ 
	/*     */ 
	/*     */ 
	/*     */ 
	/* 502 */     PersistenceDAO pd = new PersistenceDAO();
	/* 503 */     pd.executeSQLs(stateSql);
	/* 504 */     String tablename = createTempTable(prvos);
	/*     */     
	/* 506 */     getHRBatchServic().update(true, prvos);
	/* 507 */     batchupdateYfandSf(tablename, prvos);
	/* 508 */     return retObj;
	/*     */   }
	/*     */   
	/*     */ 
	/*     */ 
	/*     */   public PfProcessBatchRetObject doDirectApprove(AggregatedValueObject[] billvos, String pk_user, UFDateTime approveTime, String approveNote, int directApproveResult)
	/*     */     throws BusinessException
	/*     */   {
	/* 516 */     PfProcessBatchRetObject retObj = ((IHrPf)NCLocator.getInstance().lookup(IHrPf.class)).directApprove(billvos, PubEnv.getPk_user(), PubEnv.getServerTime(), approveNote, directApproveResult);
	/*     */     
	/*     */ 
	/* 519 */     PaydataDAO dao = new PaydataDAO();
	/* 520 */     AggPayrollVO aggVO = null;
	/* 521 */     PayrollVO mainvo = null;
	/* 522 */     UFBoolean isappoved = UFBoolean.FALSE;
	/* 523 */     PayrollVO[] prvos = null;
	/* 524 */     String[] stateSql = null;
	/* 525 */     for (int i = 0; i < retObj.getRetObj().length; i++) {
	/* 526 */       isappoved = UFBoolean.FALSE;
	/* 527 */       aggVO = (AggPayrollVO)retObj.getRetObj()[i];
	/* 528 */       mainvo = (PayrollVO)aggVO.getParentVO();
	/*     */       
	/* 530 */       mainvo.setIsapproved(UFBoolean.TRUE.toString());
	/*     */       
	/* 532 */       if (mainvo.getBillstate().intValue() == 1) {
	/* 533 */         isappoved = UFBoolean.TRUE;
	/*     */       }
	/* 535 */       prvos = (PayrollVO[])ArrayUtils.add(prvos, mainvo);
	/* 536 */       StringBuffer bsql = new StringBuffer();
	/* 537 */       bsql.append("update wa_periodstate set isapproved = ");
	/* 538 */       bsql.append("'" + isappoved.toString() + "'");
	/* 539 */       bsql.append(" where ");
	/* 540 */       bsql.append(WherePartUtil.getPeriodStateWhere(mainvo.getPk_wa_class(), mainvo.getCyear(), mainvo.getCperiod()));
	/* 541 */       stateSql = (String[])ArrayUtils.add(stateSql, bsql.toString());
	/*     */     }
	/*     */     
	/*     */ 
	/*     */ 
	/*     */ 
	/* 547 */     PersistenceDAO pd = new PersistenceDAO();
	/* 548 */     pd.executeSQLs(stateSql);
	/* 549 */     String tablename = createTempTable(prvos);
	/*     */     
	/* 551 */     getHRBatchServic().update(true, prvos);
	/* 552 */     batchupdateYfandSf(tablename, prvos);
	/* 553 */     return retObj;
	/*     */   }
	/*     */   
	/*     */   public PayrollVO sumYf(PayrollVO mainvo) throws BusinessException
	/*     */   {
	/* 558 */     return getPayrollDao().sumYf(mainvo);
	/*     */   }
	/*     */   
	/*     */ 
	/*     */   public String[] queryPksByCondition(LoginContext context, String condition, PFQueryParams queryParams, Boolean isApprove)
	/*     */     throws BusinessException
	/*     */   {
	/* 565 */     if (queryParams == null) {
	/* 566 */       return null;
	/*     */     }
	/* 568 */     String orderby = null;
	/* 569 */     if ((isApprove == null) || (!isApprove.booleanValue())) {
	/* 570 */       orderby = " wa_payroll.billstate asc, wa_payroll.billcode";
	/*     */ 
	/*     */     }
	/* 573 */     else if (!StringUtil.isEmpty(condition))
	/*     */     {
	/*     */ 
	/*     */ 
	/*     */ 
	/* 578 */       if (condition.indexOf("billstate = 1") != -1) {
	/* 579 */         condition = condition.replace("billstate = 1", "billstate in (3,2)");
	/* 580 */       } else if (condition.indexOf("billstate = 0") != -1) {
	/* 581 */         condition = condition.replace("billstate = 0", "billstate in (0,2,1,102)");
	/*     */ 
	/*     */       }
	/* 584 */       else if ((condition.indexOf("billstate in (0,1)") != -1) || (condition.indexOf("billstate in (1,0)") != -1))
	/*     */       {
	/* 586 */         condition = condition.replace("billstate in (0,1)", " billstate in (0,2,1,3,102) ");
	/* 587 */         condition = condition.replace("billstate in (1,0)", " billstate in (0,2,1,3,102) ");
	/*     */       }
	/*     */       else
	/*     */       {
	/* 591 */         condition = condition + " and billstate in (0,2,1,3,102)";
	/*     */       }
	/*     */     } else {
	/* 594 */       condition = " billstate in (2,3)";
	/*     */     }
	/*     */     
	/*     */ 
	/* 598 */     if (StringUtil.isEmpty(condition)) {
	/* 599 */       condition = " pk_org = '" + context.getPk_org() + "'";
	/*     */     } else {
	/* 601 */       condition = condition + " and pk_org = '" + context.getPk_org() + "'";
	/*     */     }
	/*     */     
	/*     */ 
	/* 605 */     String strNormalSQL = HrPfHelper.getQueryCondition(AggPayrollVO.class, "wa_payroll", queryParams.isApproveSite(), context.getPk_org(), queryParams.getBillState());
	/*     */     
	/*     */ 
	/* 608 */     if (StringUtils.isBlank(strNormalSQL))
	/*     */     {
	/* 610 */       strNormalSQL = " 1=1";
	/*     */     }
	/*     */     
	/* 613 */     IFlowBizItf itf = HrPfHelper.getFlowBizItf(AggPayrollVO.class);
	/*     */     
	/* 615 */     String strApproveDatePeriod = HrPfHelper.getApproveDatePeriod(itf, "wa_payroll", queryParams.getApproveDateParam(), queryParams.getBillState());
	/*     */     
	/* 617 */     condition = condition + " and " + strNormalSQL;
	/*     */     
	/* 619 */     if (StringUtils.isNotBlank(strApproveDatePeriod))
	/*     */     {
	/* 621 */       condition = condition + " and " + strApproveDatePeriod;
	/*     */     }
	/* 623 */     String sql = "select wa_payroll.pk_payroll from wa_payroll where " + condition;
	/* 624 */     List<String> pk_payrollsList = (List)new BaseDAO().executeQuery(sql.toString(), new ColumnListProcessor());
	/* 625 */     if (pk_payrollsList == null) {
	/* 626 */       return new String[0];
	/*     */     }
	/* 628 */     return (String[])pk_payrollsList.toArray(new String[0]);
	/*     */   }

	/*     */ 
	/*     */   public AggPayrollVO[] queryObjectByPks(String[] pks)
	/*     */     throws BusinessException
	/*     */   {
	/* 659 */     AggPayrollVO[] aggvos = (AggPayrollVO[])getServiceTemplate().queryByPks(AggPayrollVO.class, pks);
	/* 660 */     if (ArrayUtils.isEmpty(aggvos)) {
	/* 661 */       return null;
	/*     */     }
	/*     */     
	/* 664 */     for (AggPayrollVO aggvo : aggvos) {
	/* 665 */       updateYfandSf(aggvo);
	/*     */     }
	/* 667 */     return aggvos;
	/*     */   }

}
