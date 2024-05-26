package nc.impl.phm.projectregister.prv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.pmpub.rule.AppendBusiTypeBeforeRule;
import nc.bs.pmpub.rule.ChangeHeadVOStatusRule;
import nc.bs.pmpub.rule.InitCurrTypeBeforeRule;
import nc.bs.pmpub.rule.UpdateAuditInfoBeforeRule;
import nc.bs.projectRegister.rule.CheckHasProjectOrNotRule;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pmpub.servicebase.BillBaseImpl;
import nc.impl.pmpub.servicebase.action.InsertAction;
import nc.impl.pmpub.servicebase.action.UnApproveAction;
import nc.impl.pmpub.servicebase.action.UpdateAction;
import nc.itf.phm.projectregister.prv.IProjectRegister;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.org.OrgVO;
import nc.vo.pcm.contractalter.ContrAlterBillVO;
import nc.vo.pcm.contractalter.ContrAlterHeadVO;
import nc.vo.phm.projectregister.ProjectRegisterBillVO;
import nc.vo.phm.projectregister.ProjectRegisterHeadVO;
import nc.vo.ppm.startreport.StartReportBillVO;
import nc.vo.ppm.startreport.StartReportHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.sm.UserVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
// 追加项目申请单
public class ProjectRegisterImpl extends BillBaseImpl<ProjectRegisterBillVO> implements IProjectRegister{

	  public ProjectRegisterImpl() {}
	  /*     */   
	  /*     */   protected void initInsertAction(InsertAction<ProjectRegisterBillVO> action)
	  /*     */   {
	  /*  27 */     super.initInsertAction(action);
	  /*     */     
	  /*     */ 
	  /*  30 */     action.addBeforeRule(new AppendBusiTypeBeforeRule());
	  /*     */     
	  /*     */ 
	  /*  33 */     action.addBeforeRule(new InitCurrTypeBeforeRule(new FieldsForInitCurrTypeImpl()));
	  /*     */   }
	  /*     */   
	  /*     */ 
	  /*     */ 
	  /*     */   public ProjectRegisterBillVO[] insertProjectRegister(ProjectRegisterBillVO[] billVOs)
	  /*     */     throws BusinessException
	  /*     */   {
	  /*  41 */     return (ProjectRegisterBillVO[])insert(billVOs);
	  /*     */   }
	  /*     */   
	  /*     */   protected void initUpdateAction(UpdateAction<ProjectRegisterBillVO> action)
	  /*     */   {
	  /*  46 */     super.initUpdateAction(action);
	  /*     */     
	  /*     */ 
	  /*  49 */     action.addBeforeRule(new UpdateAuditInfoBeforeRule());
	  /*     */     
	  /*  51 */     action.addBeforeRule(new ChangeHeadVOStatusRule());
	  /*     */     
	  /*  53 */     action.addBeforeRule(new AppendBusiTypeBeforeRule());
	  /*     */     
	  /*  55 */     action.addBeforeRule(new InitCurrTypeBeforeRule(new FieldsForInitCurrTypeImpl()));
	  /*     */   }
	  /*     */   
	  /*     */ 
	  /*     */ 
	  /*     */ 
	  /*     */   public ProjectRegisterBillVO[] updateProjectRegister(ProjectRegisterBillVO[] billVOs, ProjectRegisterBillVO[] originBillVOs)
	  /*     */     throws BusinessException
	  /*     */   {
	  /*  64 */     return (ProjectRegisterBillVO[])update(billVOs, originBillVOs);
	  /*     */   }
	  /*     */   
	  /*     */ 
	  /*     */ 
	  /*     */ 
	  /*     */   public void deleteProjectRegister(ProjectRegisterBillVO[] billVOs)
	  /*     */     throws BusinessException
	  /*     */   {
	  /*  73 */     delete(billVOs);
	  /*     */   }
	  /*     */   
	  /*     */ 
	  /*     */ 
	  /*     */ 
		//TODO 单据同步OA功能  start create by zwh
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
	  /*     */   public ProjectRegisterBillVO[] commitProjectRegister(ProjectRegisterBillVO[] billVOs)
	  /*     */     throws BusinessException
	  /*     */   {
	  /*  82 */     sendoa(billVOs);
		/*  75 */     return (ProjectRegisterBillVO[])super.commit(billVOs);
	  /*     */   }
	  private void sendoa(ProjectRegisterBillVO[] billVOs) throws BusinessException {
		  for (ProjectRegisterBillVO  temp : billVOs) {
				if ("4D11-01".equals(temp.getParentVO().getTransi_type()) &&
						"1".equals(getDef2(temp.getParentVO().getPk_org()))) {
					JSONArray headData = null;
					headData = getMainMap(temp.getParentVO());
					/*
					 * step-1 调用OA工具类同步数据至OA
					 */
					OaWorkFlowUtil.sendOaData(headData,null, getWorkFlowBill(temp));
				}
			}
	  }

	private WorkFlowBill getWorkFlowBill(ProjectRegisterBillVO temp) throws BusinessException {
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				temp.getParentVO().getBillmaker());
		String where = " nvl(dr,0) = 0 and pk_bill = '" + temp.getPrimaryKey() + "'";
		WorkFlowBill[] workFlowBills = (WorkFlowBill[]) getHyPubBO()
				.queryByCondition(WorkFlowBill.class, where);
		WorkFlowBill workFlowBill = new WorkFlowBill();
		if(null != workFlowBills && workFlowBills.length > 1){
			throw new BusinessException("查询到多条流程记录");
		}
		if (null != workFlowBills && workFlowBills.length == 1) {
			workFlowBill = workFlowBills[0];
			workFlowBill.setWorkflowName("追加项目申请单");
		} else {
			workFlowBill.setWorkflowId("52");
			workFlowBill.setDef5(userVO.getUser_code());
			if("2".equals(getDef2(temp.getParentVO().getPk_org()))){
				PsndocVO psndoc = (PsndocVO) getHyPubBO().queryByPrimaryKey(PsndocVO.class, userVO.getPk_psndoc());
				workFlowBill.setDef5(psndoc.getId());
				workFlowBill.setWorkflowId("0");
			}	
			workFlowBill.setPk_group(temp.getParentVO().getPk_group());
			workFlowBill.setPk_org(temp.getParentVO().getPk_org());
			workFlowBill.setPk_bill(temp.getPrimaryKey());		
			workFlowBill.setUser_name(userVO.getUser_name());
			workFlowBill.setUser_code(userVO.getUser_code());
			workFlowBill.setBill_status("1");
			workFlowBill.setCreationtime(new UFDate().toStdString());
			workFlowBill.setCreator(temp.getParentVO().getCreator());
			workFlowBill.setBill_code("4D11");
			workFlowBill.setWorkflowName("追加项目申请单");
		}
		return workFlowBill;
	}
	public String getDef2(String pk_org) throws BusinessException {
		String def2 = "";
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		if(null != orgVO){
			def2 = orgVO.getDef2();
		}
		return def2;
	}
	//追加项目申请单主表数据
	private JSONArray getMainMap(ProjectRegisterHeadVO parentVO) throws BusinessException {
		List list = new ArrayList();
		
		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class, parentVO.getPk_org());
		if(null !=orgVO){
			/* 组织 */
			Map szgs =OaWorkFlowUtil.listAdd("szgs",orgVO.getCode() );
			list.add(szgs);
		}
		
		 /* 单据主键*/
		 Map djzj =OaWorkFlowUtil.listAdd("djzj",parentVO.getPk_projectregister() );
		 list.add(djzj);
		 
		 
		 
		 UserVO  userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class, parentVO.getBillmaker());
			if(null !=userVO){
				/* 制单人名称 */
				Map zdrmc =OaWorkFlowUtil.listAdd("zdrmc",userVO.getUser_name() );
				list.add(zdrmc);
				/* 制单人主键 */
				 Map zdrzj =OaWorkFlowUtil.listAdd("zdrzj",userVO.getUser_code() );
				 list.add(zdrzj);
			}
			
			/* 制单日期 */
		   	 Map zdrq =OaWorkFlowUtil.listAdd("zdrq", parentVO.getBillmaketime().getYear() + "-"
					+ parentVO.getBillmaketime().getStrMonth() + "-"
					+ parentVO.getBillmaketime().getStrDay() );
			 list.add(zdrq);
			 
			 /* 单据号*/
			 Map djh =OaWorkFlowUtil.listAdd("djh",parentVO.getBill_code() );
			 list.add(djh);
			 
			 /* 申请单位*/
			 Map sqdw =OaWorkFlowUtil.listAdd("sqdw",parentVO.getDescription() );
			 list.add(sqdw);
			 
			 /* 项目名称*/
			 Map xmmc =OaWorkFlowUtil.listAdd("xmmc",parentVO.getProject_name() );
			 list.add(xmmc);
			 
			 /* 项目编码*/
			 //String project_code = (String) getHyPubBO().findColValue("bd_project", "project_code", "nvl(dr,0) = 0 and pk_project ='" + parentVO.getProject_name() + "'");
			 Map xmbm =OaWorkFlowUtil.listAdd("xmbm",parentVO.getProject_sh_name() );
			 list.add(xmbm);
			 
			 /* 项目起止范围*/
			 Map xmqzfw =OaWorkFlowUtil.listAdd("xmqzfw",parentVO.getDef3() );
			 list.add(xmqzfw);
			 
			 /* 项目类型主键*/
			 Map xmlxzj =OaWorkFlowUtil.listAdd("xmlxzj",parentVO.getPk_projectclass() );
			 list.add(xmlxzj);
			 
			 /* 项目类型名称*/
			 String pk_projectclass_name  = (String) getHyPubBO().findColValue("bd_projectclass", "type_name", "nvl(dr,0) = 0 and   pk_projectclass  ='" + parentVO.getPk_projectclass()  + "'");
			 Map xmlxmc =OaWorkFlowUtil.listAdd("xmlxmc",pk_projectclass_name );
			 list.add(xmlxmc);
			 
			 /* EPS主键*/
			 Map epszj =OaWorkFlowUtil.listAdd("epszj",parentVO.getPk_eps() );
			 list.add(epszj);
			 
			 /*EPS名称*/
			 String pk_eps_name  = (String) getHyPubBO().findColValue("pm_eps", "eps_name", "nvl(dr,0) = 0 and  pk_eps ='" + parentVO.getPk_eps() + "'");
			 Map epsmc =OaWorkFlowUtil.listAdd("epsmc",pk_eps_name );
			 list.add(epsmc);
			 
			 /*管径*/
			 Map gj =OaWorkFlowUtil.listAdd("gj",parentVO.getDef1() );
			 list.add(gj);
			 
			 /*长度（米）*/
			 Map cdm =OaWorkFlowUtil.listAdd("cdm",new UFDouble(parentVO.getDef2()==null?"0.00":parentVO.getDef2()).setScale(2, UFDouble.ROUND_HALF_UP).toString() );
			 list.add(cdm);
			 
			 /*勘探定界费（万元）*/
			 Map ktdjfwy =OaWorkFlowUtil.listAdd("ktdjfwy",new UFDouble(parentVO.getDef6()==null?"0.00":parentVO.getDef6()).setScale(2, UFDouble.ROUND_HALF_UP).toString());
			 list.add(ktdjfwy);
			 
			 /*环评报告编制费（万元）*/
			 Map hpbgbzfwy =OaWorkFlowUtil.listAdd("hpbgbzfwy",new UFDouble(parentVO.getDef7()==null?"0.00":parentVO.getDef7()).setScale(2, UFDouble.ROUND_HALF_UP).toString());
			 list.add(hpbgbzfwy);
			 
			 /*能评报告编制费（万元）*/
			 Map npbgbzfwy =OaWorkFlowUtil.listAdd("npbgbzfwy",new UFDouble(parentVO.getDef20()==null?"0.00":parentVO.getDef20()).setScale(2, UFDouble.ROUND_HALF_UP).toString());
			 list.add(npbgbzfwy);
			 
			 /*其他费用*/
			 Map qtfy =OaWorkFlowUtil.listAdd("qtfy",new UFDouble(parentVO.getBuilfreason()==null?"0.00":parentVO.getBuilfreason()).setScale(2, UFDouble.ROUND_HALF_UP).toString());
			 list.add(qtfy);
			 
			 /*设计费（万元）*/
			 Map sjfwy =OaWorkFlowUtil.listAdd("sjfwy",new UFDouble(parentVO.getDef8()==null?"0.00":parentVO.getDef8()).setScale(2, UFDouble.ROUND_HALF_UP).toString());
			 list.add(sjfwy);
			 
			 /*材料设备投资费（万元）*/
			 Map clsbtzfwy =OaWorkFlowUtil.listAdd("clsbtzfwy",new UFDouble(parentVO.getDef9()==null?"0.00":parentVO.getDef9()).setScale(2, UFDouble.ROUND_HALF_UP).toString() );
			 list.add(clsbtzfwy);
			 
			 /*安装费（万元）*/
			 Map azfwy =OaWorkFlowUtil.listAdd("azfwy",new UFDouble(parentVO.getDef10()==null?"0.00":parentVO.getDef10()).setScale(2, UFDouble.ROUND_HALF_UP).toString() );
			 list.add(azfwy);
			 
			 /*土建费（万元）*/
			 Map tjfwy =OaWorkFlowUtil.listAdd("tjfwy",new UFDouble(parentVO.getDef11()==null?"0.00":parentVO.getDef11()).setScale(2, UFDouble.ROUND_HALF_UP).toString());
			 list.add(tjfwy);
			 
			 /*监理费（万元）*/
			 Map jlfwy =OaWorkFlowUtil.listAdd("jlfwy",new UFDouble(parentVO.getDef12()==null?"0.00":parentVO.getDef12()).setScale(2, UFDouble.ROUND_HALF_UP).toString());
			 list.add(jlfwy);
			 
			 /*无损检测费（万元）*/
			 Map wsjcfwy =OaWorkFlowUtil.listAdd("wsjcfwy",new UFDouble(parentVO.getDef13()==null?"0.00":parentVO.getDef13()).setScale(2, UFDouble.ROUND_HALF_UP).toString());
			 list.add(wsjcfwy);
			 
			 /*刨倔费（万元）*/
			 Map pjfwy =OaWorkFlowUtil.listAdd("pjfwy",new UFDouble(parentVO.getDef14()==null?"0.00":parentVO.getDef14()).setScale(2, UFDouble.ROUND_HALF_UP).toString());
			 list.add(pjfwy);
			 
			 /*园林占用及苗木移植费（万元）*/
			 Map ylzyjmmyzfwy =OaWorkFlowUtil.listAdd("ylzyjmmyzfwy",new UFDouble(parentVO.getDef15()==null?"0.00":parentVO.getDef15()).setScale(2, UFDouble.ROUND_HALF_UP).toString());
			 list.add(ylzyjmmyzfwy);
			 
			 /*分管领导主键*/
			 Map fgldzj =OaWorkFlowUtil.listAdd("fgldzj",parentVO.getDef16() );
			 list.add(fgldzj);
			 
			 /* 分管领导 */
			if(null != parentVO.getDef16()){
				String where = " pk_defdoc = '" + parentVO.getDef16() + "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'fgldmc' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
				String def16 = "0";
				if(null !=(String) getHyPubBO().findColValue("bd_defdoc", "name", where)){
					def16 = (String) getHyPubBO().findColValue("bd_defdoc", "name", where);
				}
				Map fgldmc =OaWorkFlowUtil.listAdd("fgldmc",def16 );
				list.add(fgldmc);
			}
			
			 /*交通设施费（万元）*/
			 Map jtssfwy =OaWorkFlowUtil.listAdd("jtssfwy",new UFDouble(parentVO.getDef17()==null?"0.00":parentVO.getDef17()).setScale(2, UFDouble.ROUND_HALF_UP).toString());
			 list.add(jtssfwy);
			 
			 /*围栏宣传费（万元）*/
			 Map wlxcfwy =OaWorkFlowUtil.listAdd("wlxcfwy",new UFDouble(parentVO.getDef18()==null?"0.00":parentVO.getDef18()).setScale(2, UFDouble.ROUND_HALF_UP).toString());
			 list.add(wlxcfwy);
			 
			 /*项目总投资（万元）*/
			 Map xmztzwy =OaWorkFlowUtil.listAdd("xmztzwy",new UFDouble(parentVO.getDef19()==null?"0.00":parentVO.getDef19()).setScale(2, UFDouble.ROUND_HALF_UP).toString());
			 list.add(xmztzwy);
			 
			 /* 项目实施时间*/
			 String str10 = "0";
			 if(null != parentVO.getReq_start_date()){
				 str10 = parentVO.getReq_start_date().getYear() + "-"
							+ parentVO.getReq_start_date().getStrMonth() + "-"
							+ parentVO.getReq_start_date().getStrDay() ;
			 }
		   	 Map xmsssj =OaWorkFlowUtil.listAdd("xmsssj", str10);
			 list.add(xmsssj);
			 
			 /*是否随道路施工*/
				String def4 = "0";
				if(null != parentVO.getDef4()){
					String strWhere = "pk_defdoc = '" + parentVO.getDef4() + "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'sf' and nvl(dr,0) = 0)";
					DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(DefdocVO.class, strWhere);
					if(null != defdoc && defdoc.length == 1){
						def4 = defdoc[0].getName();
					}
				}
			 Map sfsdlsg =OaWorkFlowUtil.listAdd("sfsdlsg",def4);
			 list.add(sfsdlsg);
			 
			 /*是否为续建项目*/
				String def5 = "0";
				if(null != parentVO.getDef5()){
					String strWhere = "pk_defdoc = '" + parentVO.getDef5() + "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'sf' and nvl(dr,0) = 0)";
					DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(DefdocVO.class, strWhere);
					if(null != defdoc && defdoc.length == 1){
						def5 = defdoc[0].getName();
					}
				}
			 Map sfwxjxm =OaWorkFlowUtil.listAdd("sfwxjxm",def5);
			 list.add(sfwxjxm);
			 
			 /*实施原因及内容*/
			 Map ssyyjnr =OaWorkFlowUtil.listAdd("ssyyjnr",parentVO.getMemo() );
			 list.add(ssyyjnr);
		 
				
				
			 
		
		
		JSONArray arr = JSONArray.fromObject(list);
		return arr;
		
	}
	/*     */   
	  /*     */ 
	  /*     */ 
	  /*     */ 
	  /*     */ 
	  /*     */   public ProjectRegisterBillVO[] unCommitProjectRegister(ProjectRegisterBillVO[] billVOs, PfParameterVO pfParamVO)
	  /*     */     throws BusinessException
	  /*     */   {
	  /*  92 */     ProjectRegisterBillVO[] newAggVO = (ProjectRegisterBillVO[])unCommit(billVOs, pfParamVO);
					unOaCommit(newAggVO);
/*  96 */     		return newAggVO;
	  /*     */   }
				  private void unOaCommit(ProjectRegisterBillVO[] aggVO) throws BusinessException {
					  for (ProjectRegisterBillVO temp : aggVO) {
			  				if ("4D11-01".equals(temp.getParentVO().getBill_type())) {
			  					OaWorkFlowUtil.backOaWorkFlow(temp.getPrimaryKey());
			  				}
		  				}
					
				}
	/*     */   
	  /*     */ 
	  /*     */ 
	  /*     */ 
	  /*     */   public Object approveProjectRegister(ProjectRegisterBillVO[] billVOs, PfParameterVO pfParamVO)
	  /*     */     throws BusinessException
	  /*     */   {
	  /* 101 */     return approve(billVOs, pfParamVO);
	  /*     */   }
	  /*     */   
	  /*     */ 
	  /*     */   protected void initUnApproveAction(UnApproveAction<ProjectRegisterBillVO> action)
	  /*     */   {
	  /* 107 */     super.initUnApproveAction(action);
	  /*     */     
	  /* 109 */     action.addBeforeRule(new CheckHasProjectOrNotRule());
	  /*     */   }
	  /*     */   
	  /*     */ 
	  /*     */ 
	  /*     */ 
	  /*     */   public ProjectRegisterBillVO[] unapproveProjectRegister(ProjectRegisterBillVO[] billVOs, PfParameterVO pfParamVO)
	  /*     */     throws BusinessException
	  /*     */   {
	  /* 118 */     return (ProjectRegisterBillVO[])unApprove(billVOs, pfParamVO);
	  /*     */   }
}
