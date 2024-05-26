package nc.impl.ta.leave;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
import nc.bs.uif2.validation.DefaultValidationService;
import nc.hr.frame.persistence.HrBatchService;
import nc.hr.utils.PubEnv;
import nc.hr.utils.StringPiecer;
import nc.impl.ta.algorithm.BillValidatorAtServer;
import nc.impl.ta.leave.validator.LeaveApplyApproveValidator;
import nc.impl.ta.leave.validator.LeaveApplyValidatorFactory;
import nc.impl.ta.timebill.BillMethods;
import nc.itf.ta.ILeaveAppInfoDisplayer;
import nc.itf.ta.ILeaveApplyApproveManageMaintain;
import nc.itf.ta.ILeaveBalanceManageService;
import nc.itf.ta.ILeaveRegisterManageMaintain;
import nc.itf.ta.PeriodServiceFacade;
import nc.itf.ta.algorithm.BillProcessHelper;
import nc.jdbc.framework.generator.SequenceGenerator;
import nc.pub.tools.VOUtils;
import nc.pubitf.para.SysInitQuery;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.om.job.JobVO;
import nc.vo.om.post.PostVO;
import nc.vo.om.pub.AggVOHelper;
import nc.vo.org.GroupVO;
import nc.vo.org.HROrgVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.sm.UserVO;
import nc.vo.ta.leave.AggLeaveVO;
import nc.vo.ta.leave.LeaveConst;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leave.LeavebVO;
import nc.vo.ta.leave.LeavehVO;
import nc.vo.ta.leave.SplitBillResult;
import nc.vo.ta.leave.pf.validator.PFSaveLeaveValidator;
import nc.vo.ta.leavebalance.LeaveBalanceVO;
import nc.vo.ta.pub.CommonMethods;
import nc.vo.ta.timeitem.LeaveTypeVO;
import nc.vo.ta.wf.pub.TaWorkFlowManager;
import nc.vo.uap.pf.PfProcessBatchRetObject;
import nc.vo.vorg.AdminOrgVersionVO;
import nc.vo.vorg.DeptVersionVO;
import nc.vo.wfengine.definition.WorkflowTypeEnum;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

// 休假申请单
public class LeaveAplApvManageMaintainImpl extends
		TaWorkFlowManager<LeavehVO, LeavebVO> implements
		ILeaveApplyApproveManageMaintain {

	private HrBatchService serviceTemplate;
	/*     */private ILeaveAppInfoDisplayer autoComputeService;

	/*     */
	/*     */public LeaveAplApvManageMaintainImpl() {
	}

	/*     */
	/*     */public HrBatchService getServiceTemplate()
	/*     */{
		/* 65 */if (this.serviceTemplate == null) {
			/* 66 */this.serviceTemplate = new HrBatchService(
					"4c9f5cd7-d8c5-41c3-aa99-b3f12bdb54a8");
			/* 67 */setValidatorFactory();
			/*     */}
		/* 69 */return this.serviceTemplate;
		/*     */}

	/*     */
	/*     */public AggLeaveVO directApprove(AggLeaveVO vo)
	/*     */throws BusinessException
	/*     */{
		/* 75 */return null;
		/*     */}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */public AggLeaveVO[] directApprove(int directApproveResult,
			String approveNote, AggLeaveVO[] vos)
	/*     */throws BusinessException
	/*     */{
		/* 85 */LeavehVO tempmainvo = (LeavehVO) vos[0].getParentVO();
		/* 86 */String pk_org = tempmainvo.getPk_org();
		/*     */
		/* 88 */if ((directApproveResult != -1) && (directApproveResult != 0)) {
			/* 89 */BillValidatorAtServer.checkPeriod(pk_org,
					BillProcessHelper.toLeavebVOs(vos));
			/*     */}
		/* 91 */WorkflownoteVO[] worknoteVOs = new WorkflownoteVO[vos.length];
		/*     */
		/* 93 */LeavehVO[] updateMainVOs = new LeavehVO[vos.length];
		/*     */
		/* 95 */String[] updateFields = { "approver", "approve_time",
				"approve_note", "approve_state" };
		/*     */
		/* 97 */for (int i = 0; i < vos.length; i++) {
			/* 98 */LeavehVO mainvo = (LeavehVO) vos[i].getParentVO();
			/*     */
			/* 100 */updateMainVOs[i] = changeBillData(mainvo, updateFields,
					approveNote, Integer.valueOf(directApproveResult));
			/*     */
			/* 102 */worknoteVOs[i] = buildWorkflownoteVO(directApproveResult,
					approveNote, mainvo);
			/*     */}
		/*     */
		/* 105 */getIPersistenceUpdate().insertVOArray(null, worknoteVOs, null);
		/*     */
		/* 107 */getIPersistenceUpdate().updateVOArray(null, updateMainVOs,
				updateFields, null);
		/*     */
		/* 109 */if (directApproveResult == 1)
			/* 110 */exexBills(pk_org, vos);
		/* 111 */return vos;
		/*     */}

	/*     */
	/*     */private LeavehVO changeBillData(LeavehVO mainvo,
			String[] updateFields, String approveNote,
			Integer directApproveResult)
	/*     */throws BusinessException
	/*     */{
		/* 117 */if (mainvo == null) {
			/* 118 */return null;
			/*     */}
		/* 120 */mainvo.setAttributeValue(updateFields[0], PubEnv.getPk_user());
		/* 121 */mainvo.setAttributeValue(updateFields[1],
				PubEnv.getServerTime());
		/* 122 */mainvo.setAttributeValue(updateFields[2], approveNote);
		/* 123 */mainvo.setAttributeValue(updateFields[3], directApproveResult);
		/* 124 */return mainvo;
		/*     */}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */public WorkflownoteVO buildWorkflownoteVO(int directApproveResult,
			String approveNote, LeavehVO mainvo)
	/*     */throws BusinessException
	/*     */{
		/* 137 */WorkflownoteVO worknoteVO = new WorkflownoteVO();
		/* 138 */worknoteVO.setBillid(mainvo.getPk_leaveh());
		/* 139 */worknoteVO.setBillVersionPK(mainvo.getPrimaryKey());
		/* 140 */worknoteVO.setChecknote(approveNote);
		/* 141 */worknoteVO.setDealdate(PubEnv.getServerTime());
		/* 142 */worknoteVO.setSenddate(PubEnv.getServerTime());
		/* 143 */worknoteVO.setPk_org(mainvo.getPk_org());
		/* 144 */worknoteVO.setBillno(mainvo.getBill_code());
		/* 145 */worknoteVO.setSenderman(mainvo.getApprover() == null ? mainvo
				.getBillmaker() : mainvo.getApprover());
		/* 146 */worknoteVO.setApproveresult(1 == directApproveResult ? "Y"
				: -1 == directApproveResult ? "R" : "N");
		/*     */
		/* 148 */worknoteVO.setApprovestatus(Integer.valueOf(1));
		/* 149 */worknoteVO.setIscheck(0 == directApproveResult ? "N"
				: 1 == directApproveResult ? "Y" : "X");
		/*     */
		/* 151 */worknoteVO.setActiontype("APPROVE");
		/* 152 */worknoteVO.setCheckman(mainvo.getApprover());
		/* 153 */worknoteVO.setWorkflow_type(Integer
				.valueOf(WorkflowTypeEnum.Approveflow.getIntValue()));
		/* 154 */worknoteVO.setPk_billtype(mainvo.getPk_billtype());
		/* 155 */return worknoteVO;
		/*     */}

	/*     */
	/*     */public void deleteArrayData(AggLeaveVO[] vos)
	/*     */throws BusinessException
	/*     */{
		/* 161 */getServiceTemplate().delete(vos);
		/* 162 */String pk_org = vos[0].getHeadVO().getPk_org();
		/* 163 */returnBillCodeOnDelete(pk_org, getBillType(),
				getBillCodeFormVOs(vos));
		/* 164 */((ILeaveBalanceManageService) NCLocator.getInstance().lookup(
				ILeaveBalanceManageService.class)).queryAndCalLeaveBalanceVO(
				pk_org, (Object[]) vos);
		/*     */}

	/*     */
	/*     */public void deleteData(AggLeaveVO vo) throws BusinessException
	/*     */{
		/* 169 */getServiceTemplate().delete(new AggLeaveVO[] { vo });
		/* 170 */String pk_org = (String) vo.getParentVO().getAttributeValue(
				"pk_org");
		/* 171 */String bill_code = (String) vo.getParentVO()
				.getAttributeValue("bill_code");
		/* 172 */returnBillCodeOnDelete(pk_org, bill_code);
		/* 173 */ILeaveBalanceManageService balanceService = (ILeaveBalanceManageService) NCLocator
				.getInstance().lookup(ILeaveBalanceManageService.class);
		/* 174 */balanceService.queryAndCalLeaveBalanceVO(vo.getHeadVO()
				.getPk_org(), new Object[] { vo.getHeadVO() });
		/*     */}

	/*     */
	/*     */
	/*     */public AggLeaveVO[] insertArrayData(AggLeaveVO[] vos)
	/*     */throws BusinessException
	/*     */{
		/* 181 */if (ArrayUtils.isEmpty(vos))
			/* 182 */return null;
		/* 183 */String pk_org = ((LeavehVO) vos[0].getParentVO()).getPk_org();
		/* 184 */AggLeaveVO[] aggVOs = (AggLeaveVO[]) getServiceTemplate()
				.insert(vos);
		/*     */
		/* 186 */((ILeaveBalanceManageService) NCLocator.getInstance().lookup(
				ILeaveBalanceManageService.class)).calLeaveBalanceVO4NewThread(
				pk_org, (Object[]) vos);
		/* 187 */return aggVOs;
		/*     */}

	/*     */
	/*     */@Deprecated
	/*     */public AggLeaveVO insertData(AggLeaveVO vo) throws BusinessException
	/*     */{
		/* 193 */new LeaveApplyQueryMaintainImpl().check(vo);
		/*     */
		/* 195 */DefaultValidationService vService = new DefaultValidationService();
		/* 196 */vService.addValidator(new PFSaveLeaveValidator());
		/* 197 */vService.validate(vo);
		/*     */
		/* 199 */LeavehVO mainVO = (LeavehVO) vo.getParentVO();
		/* 200 */if (mainVO.getLeaveindex() == null)
			mainVO.setLeaveindex(Integer.valueOf(1));
		/* 201 */AggLeaveVO aggVO = ((AggLeaveVO[]) getServiceTemplate()
				.insert(new AggLeaveVO[] { vo }))[0];
		/* 202 */commitBillCode(mainVO.getPk_org(), mainVO.getBill_code());
		/* 203 */ILeaveBalanceManageService balanceService = (ILeaveBalanceManageService) NCLocator
				.getInstance().lookup(ILeaveBalanceManageService.class);
		/*     */
		/* 205 */balanceService.calLeaveBalanceVO4NewThread(mainVO.getPk_org(),
				new Object[] { aggVO.getHeadVO() });
		/* 206 */return aggVO;
		/*     */}

	/*     */
	/*     */
	/*     */
	// TODO 单据同步OA功能 start create by zwh
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

	private void senOaData(AggLeaveVO[] billVOs) throws BusinessException {
		for (AggLeaveVO temp : billVOs) {

			// if((temp.getLeavehVO().getTranstype()).contains("6404")){
			if ("6404".equals(temp.getLeavehVO().getPk_billtype())) {
				JSONArray headData = getMainMap(temp.getLeavehVO());
				JSONArray bodyData = getDtaileDataMap(temp);
				/*
				 * step-1 调用OA工具类同步数据至OA
				 */
				OaWorkFlowUtil.sendOaData(headData,
						bodyData, getWorkFlowBill(temp));
			}
		}
	}

	private JSONArray getMainMap(LeavehVO parentVO) throws BusinessException {
		List list = new ArrayList();

		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				parentVO.getPk_org());
		if (null != orgVO) {
			/* 组织 */
			Map szgs = OaWorkFlowUtil.listAdd("szgs", orgVO.getCode());
			list.add(szgs);
		}

		/* 单据编码 */
		Map djbm = OaWorkFlowUtil.listAdd("djbm", parentVO.getBill_code());
		list.add(djbm);

		/* 单据状态 */
		Map djzt = OaWorkFlowUtil.listAdd("djzt", parentVO.getApprove_state()
				.toString());
		list.add(djzt);

		/* 申请人 */
		Map sqr = OaWorkFlowUtil.listAdd("sqr", VOUtils.getDocName(PsndocVO.class, parentVO.getBillmaker()));
		list.add(sqr);

		/* 员工号 */
		String ygcode = (String) getHyPubBO().findColValue(
				"hi_psnjob",
				"clerkcode",
				"nvl(dr,0) = 0 and pk_psnjob  ='" + parentVO.getPk_psnjob()
						+ "'");
		Map ygh = OaWorkFlowUtil.listAdd("ygh", ygcode);
		list.add(ygh);
		
		/* 假期联系电话 */
		Map jqlxdh = OaWorkFlowUtil.listAdd("jqlxdh",  parentVO.getRelatetel());
		list.add(jqlxdh);
		
		/* 享有时长 */
		Map xysc = OaWorkFlowUtil.listAdd("xysc", parentVO.getRealdayorhour().setScale(2, UFDouble.ROUND_HALF_UP).toString());
		list.add(xysc);
		
		/* 申请日期 */
		Map sqrq = OaWorkFlowUtil.listAdd("sqrq", parentVO.getApply_date()
				.getYear()
				+ "-"
				+ parentVO.getApply_date().getStrMonth()
				+ "-"
				+ parentVO.getApply_date().getStrDay());
		list.add(sqrq);

		/* 所属集团 */
		Map szjt = OaWorkFlowUtil.listAdd("szjt", VOUtils.getDocName(GroupVO.class, parentVO.getPk_group()));
		list.add(szjt);

		/* 所属组织 */
		Map szzz =OaWorkFlowUtil.listAdd("szzz",VOUtils.getDocName(HROrgVO.class, parentVO.getPk_org()));
		list.add(szzz);

		/* 人员工作记录 */
		Map rygzjl = OaWorkFlowUtil.listAdd("rygzjl", parentVO.getPk_psnjob());
		list.add(rygzjl);

		/* 人员编码 */
		String code = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"code",
				"nvl(dr,0) = 0 and pk_psndoc  ='" + parentVO.getPk_psndoc()
						+ "'");
		Map rybm = OaWorkFlowUtil.listAdd("rybm", code);
		list.add(rybm);

		/* 姓名 */
		String name = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc  ='" + parentVO.getPk_psndoc()
						+ "'");
		Map xm = OaWorkFlowUtil.listAdd("xm", name);
		list.add(xm);

		/* 组织 */
		Map zz =OaWorkFlowUtil.listAdd("zz",VOUtils.getDocName(AdminOrgVersionVO.class, parentVO.getPk_org_v()));
		list.add(zz);

		/* 部门 */
		Map bm =OaWorkFlowUtil.listAdd("bm",VOUtils.getDocName(DeptVersionVO.class, parentVO.getPk_dept_v()));
		list.add(bm);

		/* 岗位 */
		String post = (String) getHyPubBO().findColValue(
				"hi_psnjob",
				"pk_post",
				"nvl(dr,0) = 0 and pk_psnjob  ='" + parentVO.getPk_psnjob()
						+ "'");
		Map gw = OaWorkFlowUtil.listAdd("gw", VOUtils.getDocName(PostVO.class, post));
		list.add(gw);

		/* 职位 */
		String job = (String) getHyPubBO().findColValue(
				"hi_psnjob",
				"pk_job",
				"nvl(dr,0) = 0 and pk_psnjob  ='" + parentVO.getPk_psnjob()
						+ "'");
		Map zw = OaWorkFlowUtil.listAdd("zw", VOUtils.getDocName(JobVO.class, job));
		list.add(zw);

		/* 休假类别 */
		Map xjlb =OaWorkFlowUtil.listAdd("xjlb",VOUtils.getDocName(LeaveTypeVO.class, parentVO.getPk_leavetype()));
		list.add(xjlb);

		/* 假期年度 */
		Map jqnd = OaWorkFlowUtil.listAdd("jqnd", parentVO.getLeaveyear());
		list.add(jqnd);

		/* 假期期间 */
		Map jqqj = OaWorkFlowUtil.listAdd("jqqj", parentVO.getLeavemonth());
		list.add(jqqj);

		/* 休假总时长 */
		Map xjzsc = OaWorkFlowUtil.listAdd("xjzsc", parentVO.getSumhour().setScale(2, UFDouble.ROUND_HALF_UP)
				.toString());
		list.add(xjzsc);

		/* 已休时长 */
		Map yxsc = OaWorkFlowUtil.listAdd("yxsc", parentVO.getResteddayorhour().setScale(2, UFDouble.ROUND_HALF_UP)
				.toString());
		list.add(yxsc);

		/* 结余时长 */
		Map jysc = OaWorkFlowUtil.listAdd("jysc", parentVO.getRestdayorhour().setScale(2, UFDouble.ROUND_HALF_UP)
				.toString());
		list.add(jysc);

		/* 冻结时长 */
		Map djsc = OaWorkFlowUtil.listAdd("djsc", parentVO.getFreezedayorhour().setScale(2, UFDouble.ROUND_HALF_UP)
				.toString());
		list.add(djsc);

		/* 可用时长 */
		Map kysc = OaWorkFlowUtil.listAdd("kysc", parentVO.getUsefuldayorhour().setScale(2, UFDouble.ROUND_HALF_UP)
				.toString());
		list.add(kysc);

		/* 创建人 */
		Map cjr =OaWorkFlowUtil.listAdd("cjr",VOUtils.getDocName(UserVO.class, parentVO.getCreator()));
		list.add(cjr);
		
		/* 创建时间 */
		Map cjsj = OaWorkFlowUtil.listAdd("cjsj", parentVO.getCreationtime()
				.getYear()
				+ "-"
				+ parentVO.getCreationtime().getStrMonth()
				+ "-" + parentVO.getCreationtime().getStrDay());
		list.add(cjsj);

		/* 修改时间 */
		if (parentVO.getModifiedtime() != null) {
			Map xgsj = OaWorkFlowUtil.listAdd("xgsj", parentVO
					.getModifiedtime().getYear()
					+ "-"
					+ parentVO.getModifiedtime().getStrMonth()
					+ "-"
					+ parentVO.getModifiedtime().getStrDay());
			list.add(xgsj);
		} else {
			Map xgsj = OaWorkFlowUtil.listAdd("xgsj", "0");
			list.add(xgsj);
		}

		/* 修改人 */
		Map xgr = OaWorkFlowUtil.listAdd("xgr", parentVO.getModifier());
		list.add(xgr);

		/* 审批人 */
		Map spr = OaWorkFlowUtil.listAdd("spr", parentVO.getApprover());
		list.add(spr);

		/* 审批意见 */
		Map spyj = OaWorkFlowUtil.listAdd("spyj", parentVO.getApprove_note());
		list.add(spyj);

		/* 审批时间 */
		if (parentVO.getApprove_time() != null) {
			Map spsj = OaWorkFlowUtil.listAdd("spsj", parentVO
					.getApprove_time().getYear()
					+ "-"
					+ parentVO.getApprove_time().getStrMonth()
					+ "-"
					+ parentVO.getApprove_time().getStrDay());
			list.add(spsj);
		} else {
			Map spsj = OaWorkFlowUtil.listAdd("spsj", "0");
			list.add(spsj);
		}

		/* 休假单主键 */
		Map xjdzj = OaWorkFlowUtil.listAdd("xjdzj", parentVO.getPk_leaveh());
		list.add(xjdzj);

		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		if (null != userVO) {
			/* 制单人名称 */
			Map zdrmc = OaWorkFlowUtil.listAdd("zdrmc", userVO.getUser_name());
			list.add(zdrmc);

			/* 制单人名称 */
			Map zdrzj = OaWorkFlowUtil.listAdd("zdrzj", userVO.getUser_code());
			list.add(zdrzj);
		}

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	private JSONArray getDtaileDataMap(AggLeaveVO aggVO)
			throws BusinessException {
		Map leaveVO = getContrAlterBodyVO(aggVO);
		JSONArray dtlistString = JSONArray.fromObject(leaveVO);
		return dtlistString;
	}

	private Map getContrAlterBodyVO(AggLeaveVO aggVO) throws BusinessException {
		Map dtMap = new HashMap();
		dtMap.put("tableDBName", "formtable_main_74_dt1");
		List workflowRequestTableRecords = new ArrayList();
		LeavebVO[] bodyVOS = (LeavebVO[]) aggVO.getChildrenVO();
		for (LeavebVO temp : bodyVOS) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();

			/* 单据编码 */
			Map djbm = OaWorkFlowUtil.listAdd("djbm", temp.getBill_code());
			workflowRequestTableFields.add(djbm);

			/* 休假开始时间 */
			Map xjkssj = OaWorkFlowUtil.listAdd("xjkssj", temp
					.getLeavebegintime().getYear()
					+ "-"
					+ temp.getLeavebegintime().getStrMonth()
					+ "-"
					+ temp.getLeavebegintime().getStrDay());
			workflowRequestTableFields.add(xjkssj);

			/* 休假结束时间 */
			Map xjjssj = OaWorkFlowUtil.listAdd("xjjssj", temp
					.getLeaveendtime().getYear()
					+ "-"
					+ temp.getLeaveendtime().getStrMonth()
					+ "-"
					+ temp.getLeaveendtime().getStrDay());
			workflowRequestTableFields.add(xjjssj);

			/* 休假时长 */
			Map xjsc = OaWorkFlowUtil.listAdd("xjsc", temp.getLeavehour().setScale(2, UFDouble.ROUND_HALF_UP)
					.toString());
			workflowRequestTableFields.add(xjsc);

			/* 休假说明 */
			Map xjsm = OaWorkFlowUtil.listAdd("xjsm", temp.getLeaveremark());
			workflowRequestTableFields.add(xjsm);

			/* 所属集团 */
			Map szjt = OaWorkFlowUtil.listAdd("szjt", VOUtils.getDocName(GroupVO.class, temp.getPk_group()));
			workflowRequestTableFields.add(szjt);

			/* 休假单子表主键 */
			Map xjdzbzj = OaWorkFlowUtil
					.listAdd("xjdzbzj", temp.getPk_leaveb());
			workflowRequestTableFields.add(xjdzbzj);

			/* 休假单主键 */
			Map xjdzj = OaWorkFlowUtil.listAdd("xjdzj", temp.getPk_leaveh());
			workflowRequestTableFields.add(xjdzj);

			/* 所属组织 */
			Map szzz = OaWorkFlowUtil.listAdd("szzz", VOUtils.getDocName(HROrgVO.class, temp.getPk_org()));
			workflowRequestTableFields.add(szzz);
			
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	private WorkFlowBill getWorkFlowBill(AggLeaveVO temp)
			throws BusinessException {
		String where = " nvl(dr,0) = 0 and pk_bill = '"
				+ temp.getLeavehVO().getPk_leaveh() + "'";
		WorkFlowBill[] workFlowBills = (WorkFlowBill[]) getHyPubBO()
				.queryByCondition(WorkFlowBill.class, where);
		WorkFlowBill workFlowBill = new WorkFlowBill();
		if (null != workFlowBills && workFlowBills.length > 1) {
			throw new BusinessException("查询到多条流程记录");
		}
		if (null != workFlowBills && workFlowBills.length == 1) {
			workFlowBill = workFlowBills[0];
			workFlowBill.setWorkflowName("休假申请单");
		} else {
			UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
					UserVO.class, temp.getLeavehVO().getBillmaker());
			workFlowBill.setPk_group(temp.getLeavehVO().getPk_group());
			workFlowBill.setPk_org(temp.getLeavehVO().getPk_org());
			workFlowBill.setPk_bill(temp.getLeavehVO().getPk_leaveh());
			workFlowBill.setUser_name(userVO.getUser_name());
			workFlowBill.setUser_code(userVO.getUser_code());
			workFlowBill.setCreationtime(new UFDate().toStdString());
			workFlowBill.setDef5(userVO.getUser_code());
			workFlowBill.setBill_status("1");
			workFlowBill.setCreator(temp.getLeavehVO().getCreator());
			workFlowBill.setBill_code("6404");
			workFlowBill.setWorkflowId("69");
			workFlowBill.setWorkflowName("休假申请单");
		}
		return workFlowBill;
	}

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
	/*     */private void setValidatorFactory()
	/*     */{
		/* 244 */getServiceTemplate().setValidatorFactory(
				new LeaveApplyValidatorFactory());
		/*     */}

	/*     */
	/*     */public AggLeaveVO[] updateArrayData(AggLeaveVO[] vos)
			throws BusinessException
	/*     */{
		/* 249 */return updateData(vos, true, true);
		/*     */}

	/*     */
	/*     */private AggLeaveVO[] updateData(AggLeaveVO[] vos,
			boolean isSetAuditInfo, boolean needCheck) throws BusinessException {
		/* 253 */if (ArrayUtils.isEmpty(vos))
			/* 254 */return null;
		/* 255 */if (needCheck) {
			/* 256 */LeaveApplyApproveValidator validator = new LeaveApplyApproveValidator();
			/* 257 */validator.validateAggVOs(vos);
			/*     */}
		/* 259 */return (AggLeaveVO[]) getServiceTemplate().update(
				isSetAuditInfo, vos);
		/*     */}

	/*     */
	/*     */
	/*     */
	/*     */@Deprecated
	/* 265 */public AggLeaveVO updateData(AggLeaveVO vo)
			throws BusinessException {
		return updateArrayData(new AggLeaveVO[] { vo })[0];
	}

	/*     */
	/*     */@Deprecated
	/*     */protected AggLeaveVO updateData(AggLeaveVO vo, boolean isSetAuditInfo)
			throws BusinessException {
		/* 269 */new LeaveApplyQueryMaintainImpl().check(vo);
		/*     */
		/* 271 */return ((AggLeaveVO[]) getServiceTemplate().update(
				isSetAuditInfo, new AggLeaveVO[] { vo }))[0];
		/*     */}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */public AggLeaveVO[] doRecall(AggLeaveVO[] vos)
	/*     */throws BusinessException
	/*     */{
		/* 281 */if (ArrayUtils.isEmpty(vos)) {
			/* 282 */return null;
			/*     */}
		/* 284 */syncHeadInfoToBody(vos);
		/*     */
		/*     */
		/* 287 */PeriodServiceFacade.checkDateScope(vos[0].getHeadVO()
				.getPk_org(), BillProcessHelper.toLeavebVOs(vos));
		/* 288 */return updateData(vos, false, false);
		/*     */}

	/*     */
	/*     */public PfProcessBatchRetObject doCallBack(boolean blCheckPassIsEnd,
			AggregatedValueObject[] vos)
	/*     */throws BusinessException
	/*     */{
		/* 294 */if (ArrayUtils.isEmpty(vos))
			/* 295 */return null;
		/* 296 */PfProcessBatchRetObject retObj = getIHrPf().callbackBill(
				blCheckPassIsEnd, null, null, "64040leavergst", vos);
		/*     */
		/* 298 */AggLeaveVO[] aggVOs = new AggLeaveVO[vos.length];
		/*     */
		/*     */
		/* 301 */PeriodServiceFacade.checkDateScope(aggVOs[0].getHeadVO()
				.getPk_org(), BillProcessHelper.toLeavebVOs(aggVOs));
		/* 302 */for (int i = 0; i < aggVOs.length; i++) {
			/* 303 */aggVOs[i] = ((AggLeaveVO) vos[i]);
			/*     */}
		/* 305 */String pk_org = aggVOs[0].getHeadVO().getPk_org();
		/* 306 */((ILeaveBalanceManageService) NCLocator.getInstance().lookup(
				ILeaveBalanceManageService.class)).queryAndCalLeaveBalanceVO(
				pk_org, (Object[]) aggVOs);
		/* 307 */return retObj;
		/*     */}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */public AggregatedValueObject[] doCommit(AggLeaveVO[] vos)
	/*     */throws BusinessException
	/*     */{
		/* 318 */for (AggLeaveVO vo : vos)
		/*     */{
			/*     */
			/*     */
			/*     */
			/* 323 */LeavehVO billvo = (LeavehVO) vo.getParentVO();

			/*     */
			/* 325 */billvo.setApprove_state(Integer.valueOf(3));
			/* 326 */vo.setParentVO(billvo);
			/*     */}
		/*     */
		/*     */
		/*     */
		/*     */
		/*     */
		/*     */
		/* 334 */PeriodServiceFacade.checkDateScope(vos[0].getHeadVO()
				.getPk_org(), BillProcessHelper.toLeavebVOs(vos));
		/* 335 */updateData(vos, false, false);
		/* 336 */LeavehVO leavehVO = (LeavehVO) vos[0].getParentVO();
		/* 337 */String pk_org = leavehVO.getPk_org();
		/* 338 */Integer approvetype = SysInitQuery.getParaInt(pk_org,
				"HRLM0001");
		/*     */
		/* 340 */if ((approvetype != null) && (approvetype.intValue() == 0))
		/*     */{
			/*     */
			/* 343 */sendDirApprMessage(pk_org, "600113", vos);
			/*     */}
		/*     */
		/*     */
		/*     */senOaData(vos);
		/* 348 */return vos;
		/*     */}

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
	/*     */public AggLeaveVO[] doApprove(AggLeaveVO[] vos)
	/*     */throws BusinessException
	/*     */{
		/* 384 */if (ArrayUtils.isEmpty(vos))
			/* 385 */return null;
		/* 386 */syncHeadInfoToBody(vos);
		/*     */
		/*     */
		/*     */
		/*     */
		/* 391 */LeavehVO tempvo = (LeavehVO) vos[0].getParentVO();
		/* 392 */int appStatus = tempvo.getApprove_state().intValue();
		/* 393 */if ((appStatus != -1) && (appStatus != 0))
			/* 394 */PeriodServiceFacade.checkDateScope(vos[0].getHeadVO()
					.getPk_org(), BillProcessHelper.toLeavebVOs(vos));
		/* 395 */vos = updateData(vos, false, false);
		/* 396 */if (appStatus == 1) {
			/* 397 */exexBills(tempvo.getPk_org(), vos);
			/* 398 */} else if ((tempvo.getApprove_state().intValue() == -1)
				|| (tempvo.getApprove_state().intValue() == 0))
		/*     */{
			/* 400 */String pk_org = vos[0].getHeadVO().getPk_org();
			/* 401 */((ILeaveBalanceManageService) NCLocator.getInstance()
					.lookup(ILeaveBalanceManageService.class))
					.queryAndCalLeaveBalanceVO(pk_org, (Object[]) vos);
			/*     */}
		/*     */
		/* 404 */return vos;
		/*     */}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */public void exexBills(String pk_org, AggLeaveVO[] vos)
	/*     */throws BusinessException
	/*     */{
		/* 413 */genRegAndCalculate(vos);
		/*     */}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */public AggLeaveVO[] doUnApprove(AggLeaveVO[] vos)
	/*     */throws BusinessException
	/*     */{
		/* 426 */LeavehVO[] mainvos = (LeavehVO[]) AggVOHelper
				.getParentVOArrayFromAggVOs(vos, LeavehVO.class);
		/* 427 */AggLeaveVO[] oldvos = (AggLeaveVO[]) getServiceTemplate()
				.queryByPks(AggLeaveVO.class,
						StringPiecer.getStrArray(mainvos, "pk_leaveh"));
		/* 428 */for (AggLeaveVO oldvo : oldvos) {
			/* 429 */checkPFPassingState(oldvo.getLeavehVO().getApprove_state()
					.intValue());
			/*     */}
		/*     */
		/* 432 */syncHeadInfoToBody(vos);
		/*     */
		/*     */
		/* 435 */PeriodServiceFacade.checkDateScope(vos[0].getHeadVO()
				.getPk_org(), BillProcessHelper.toLeavebVOs(vos));
		/* 436 */vos = updateData(vos, false, false);
		/* 437 */String pk_org = vos[0].getLeavehVO().getPk_org();
		/* 438 */((ILeaveBalanceManageService) NCLocator.getInstance().lookup(
				ILeaveBalanceManageService.class)).queryAndCalLeaveBalanceVO(
				pk_org, (Object[]) vos);
		/* 439 */return vos;
		/*     */}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */public AggLeaveVO[] doDelete(AggLeaveVO[] vos)
	/*     */throws BusinessException
	/*     */{
		/* 450 */for (AggLeaveVO vo : vos)
		/*     */{
			/* 452 */deleteOldWorknote(vo.getParentVO().getPrimaryKey(), "6404");
			/*     */}
		/* 454 */deleteArrayData(vos);
		/* 455 */return vos;
		/*     */}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */protected LeavehVO createMainVO(LeavebVO[] childVOs)
	/*     */{
		/* 468 */LeavehVO mainvo = new LeavehVO();
		/* 469 */LeavebVO childVO = childVOs[0];
		/* 470 */mainvo.setIshrssbill(UFBoolean.FALSE);
		/* 471 */mainvo.setIslactation(UFBoolean.FALSE);
		/* 472 */mainvo.setPk_group(childVO.getPk_group());
		/* 473 */mainvo.setPk_org(childVO.getPk_org());
		/* 474 */mainvo.setPk_psndoc(childVO.getPk_psndoc());
		/* 475 */mainvo.setPk_psnjob(childVO.getPk_psnjob());
		/* 476 */mainvo.setPk_psnorg(childVO.getPk_psnorg());
		/* 477 */mainvo.setPk_org_v(childVO.getPk_org_v());
		/* 478 */mainvo.setPk_dept_v(childVO.getPk_dept_v());
		/* 479 */mainvo.setStatus(2);
		/* 480 */mainvo.setApprove_state(Integer.valueOf(-1));
		/* 481 */mainvo.setBillmaker(PubEnv.getPk_user());
		/* 482 */mainvo.setApply_date(PubEnv.getServerLiteralDate());
		/* 483 */mainvo.setLeaveyear(childVO.getLeaveyear());
		/* 484 */mainvo.setLeavemonth(childVO.getLeavemonth());
		/* 485 */if (childVO.getAttributeValue("transtypeid") != null)
		/*     */{
			/* 487 */mainvo.setTranstypeid((String) childVO
					.getAttributeValue("transtypeid"));
			/*     */}
		/* 489 */if (childVO.getAttributeValue("transtype") != null)
		/*     */{
			/* 491 */mainvo.setTranstype((String) childVO
					.getAttributeValue("transtype"));
			/*     */}
		/*     */
		/* 494 */mainvo
				.setPk_billtype(childVO.getAttributeValue("pk_billtype") == null ? "6404"
						: (String) childVO.getAttributeValue("pk_billtype"));
		/* 495 */mainvo.setPk_leavetype((String) childVO
				.getAttributeValue("pk_leavetype"));
		/* 496 */mainvo.setPk_leavetypecopy((String) childVO
				.getAttributeValue("pk_leavetypecopy"));
		/* 497 */if (childVOs.length == 1) {
			/* 498 */mainvo.setSumhour(childVO.getLeavehour());
			/*     */} else {
			/* 500 */UFDouble sumHour = new UFDouble();
			/* 501 */for (LeavebVO vo : childVOs) {
				/* 502 */if (vo.getLeavehour() != null)
					/* 503 */sumHour = sumHour.add(vo.getLeavehour());
				/*     */}
			/* 505 */mainvo.setSumhour(sumHour);
			/*     */}
		/* 507 */mainvo.setRealdayorhour(childVO.getRealdayorhour());
		/* 508 */mainvo.setRestdayorhour(childVO.getRestdayorhour());
		/* 509 */mainvo.setResteddayorhour(childVO.getResteddayorhour());
		/* 510 */mainvo.setFreezedayorhour(childVO.getFreezedayorhour());
		/* 511 */mainvo.setUsefuldayorhour(childVO.getUsefuldayorhour());
		/*     */
		/* 513 */return mainvo;
		/*     */}

	/*     */
	/*     */private LeavebVO createSubVo(LeavebVO pageVo, String pk_org,
			String pk_group) {
		/* 517 */LeavebVO subvo = new LeavebVO();
		/* 518 */subvo.setAttributeValue("pk_leavetype",
				pageVo.getAttributeValue("pk_leavetype"));
		/* 519 */subvo.setAttributeValue("pk_leavetypecopy",
				pageVo.getAttributeValue("pk_leavetypecopy"));
		/* 520 */subvo.setLeavebegintime(pageVo.getLeavebegintime());
		/* 521 */subvo.setLeaveendtime(pageVo.getLeaveendtime());
		/* 522 */subvo.setLeavebegindate(UFLiteralDate.getDate(pageVo
				.getLeavebegintime().toString().substring(0, 10)));
		/* 523 */subvo.setLeaveenddate(UFLiteralDate.getDate(pageVo
				.getLeaveendtime().toString().substring(0, 10)));
		/* 524 */subvo.setLeaveyear(pageVo.getLeaveyear());
		/* 525 */subvo.setLeavemonth(pageVo.getLeavemonth());
		/* 526 */subvo.setLeavehour(pageVo.getLeavehour());
		/* 527 */subvo.setLeaveremark(pageVo.getLeaveremark());
		/* 528 */subvo.setPk_agentpsn(pageVo.getPk_agentpsn());
		/* 529 */subvo.setTranstypeid(pageVo.getTranstypeid());
		/* 530 */subvo.setTranstype(pageVo.getTranstype());
		/*     */
		/* 532 */subvo.setPk_group(pk_group);
		/* 533 */subvo.setPk_org(pk_org);
		/* 534 */subvo.setStatus(2);
		/* 535 */subvo.setPk_psndoc(pageVo.getPk_psndoc());
		/* 536 */subvo.setPk_psnjob(pageVo.getPk_psnjob());
		/* 537 */subvo.setPk_psnorg(pageVo.getPk_psnorg());
		/* 538 */subvo.setPk_joborg(pageVo.getPk_joborg());
		/* 539 */subvo.setPk_org_v(pageVo.getPk_org_v());
		/* 540 */subvo.setPk_dept_v(pageVo.getPk_dept_v());
		/* 541 */subvo.setRealdayorhour(pageVo.getRealdayorhour());
		/* 542 */subvo.setRestdayorhour(pageVo.getRestdayorhour());
		/* 543 */subvo.setResteddayorhour(pageVo.getResteddayorhour());
		/* 544 */subvo.setFreezedayorhour(pageVo.getFreezedayorhour());
		/* 545 */subvo.setUsefuldayorhour(pageVo.getUsefuldayorhour());
		/* 546 */subvo.setTimezone(pageVo.getTimezone());
		/* 547 */return subvo;
		/*     */}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */public AggLeaveVO[] createAggLeaveVO(LeavebVO[] vos, boolean mergeBill)
	/*     */throws BusinessException
	/*     */{
		/* 558 */if (ArrayUtils.isEmpty(vos))
			/* 559 */return null;
		/* 560 */String pk_org = vos[0].getPk_org();
		/* 561 */String pk_group = vos[0].getPk_group();
		/* 562 */LeavebVO[] subvos = new LeavebVO[vos.length];
		/* 563 */for (int i = 0; i < subvos.length; i++) {
			/* 564 */subvos[i] = createSubVo(vos[i], pk_org, pk_group);
			/*     */}
		/* 566 */AggLeaveVO[] retArray = null;
		/*     */
		/* 568 */if (!mergeBill) {
			/* 569 */List<AggLeaveVO> aggvos = new ArrayList();
			/* 570 */for (LeavebVO subvo : subvos)
			/*     */{
				/* 572 */AggLeaveVO aggVO = new AggLeaveVO();
				/* 573 */aggVO
						.setParentVO(createMainVO(new LeavebVO[] { subvo }));
				/* 574 */aggVO.setChildrenVO(new LeavebVO[] { subvo });
				/* 575 */aggvos.add(aggVO);
				/*     */}
			/* 577 */retArray = (AggLeaveVO[]) aggvos
					.toArray(new AggLeaveVO[0]);
			/*     */}
		/*     */else {
			/* 580 */AggLeaveVO[] tempAggVOs = (AggLeaveVO[]) mergeSubVOs(
					AggLeaveVO.class, subvos);
			/*     */
			/* 582 */List<AggLeaveVO> resultList = new ArrayList();
			/* 583 */for (AggLeaveVO aggVO : tempAggVOs) {
				/* 584 */LeavebVO[] bvos = aggVO.getBodyVOs();
				/* 585 */if (bvos.length == 1) {
					/* 586 */resultList.add(aggVO);
					/*     */}
				/*     */else {
					/* 589 */Map<String, LeavebVO[]> periodGroupMap = nc.hr.utils.CommonUtils
							.group2ArrayByField("yearmonth", bvos);
					/* 590 */if (periodGroupMap.size() == 1) {
						/* 591 */resultList.add(aggVO);
						/*     */}
					/*     */else
						/* 594 */for (LeavebVO[] tempbVOs : periodGroupMap
								.values()) {
							/* 595 */AggLeaveVO a = new AggLeaveVO();
							/* 596 */resultList.add(a);
							/* 597 */a.setParentVO(createMainVO(tempbVOs));
							/* 598 */a.setChildrenVO(tempbVOs);
							/*     */}
					/*     */}
			}
			/* 601 */retArray = (AggLeaveVO[]) resultList
					.toArray(new AggLeaveVO[0]);
			/*     */}
		/* 603 */Map<String, LeaveBalanceVO> leaveBalanceMap = ((ILeaveBalanceManageService) NCLocator
				.getInstance().lookup(ILeaveBalanceManageService.class))
				.queryAndCalLeaveBalanceVO(pk_org, (Object[]) retArray);
		/* 604 */for (int i = 0; i < retArray.length; i++) {
			/* 605 */LeavehVO mainvo = (LeavehVO) retArray[i].getParentVO();
			/* 606 */LeaveBalanceVO leaveBalanceVO = MapUtils
					.isEmpty(leaveBalanceMap) ? null
					: (LeaveBalanceVO) leaveBalanceMap.get(mainvo
							.getPk_psnorg()
							+ mainvo.getPk_leavetype()
							+ mainvo.getYearmonth());
			/* 607 */mainvo.setLeaveindex(Integer
					.valueOf(leaveBalanceVO == null ? 1 : leaveBalanceVO
							.getLeaveindex().intValue()));
			/*     */}
		/* 609 */return retArray;
		/*     */}

	/*     */
	/*     */public ILeaveAppInfoDisplayer getAutoComputeService()
	/*     */{
		/* 614 */if (this.autoComputeService == null) {
			/* 615 */this.autoComputeService = new LeaveAppInfoDisplayer();
			/*     */}
		/* 617 */return this.autoComputeService;
		/*     */}

	/*     */
	/*     */public AggLeaveVO[] insertData(LeavebVO[] vos, boolean mergeBill)
	/*     */throws BusinessException
	/*     */{
		/* 623 */if (ArrayUtils.isEmpty(vos))
			/* 624 */return null;
		/* 625 */BillMethods.processBeginEndDatePkJobOrgTimeZone(vos);
		/* 626 */String pk_org = vos[0].getPk_org();
		/*     */
		/*     */
		/* 629 */AggLeaveVO[] aggVOs = createAggLeaveVO(vos, mergeBill);
		/* 630 */String[] billCodes = null;
		/*     */try
		/*     */{
			/* 633 */billCodes = generateBillCodes(pk_org, aggVOs.length);
			/* 634 */for (int i = 0; i < aggVOs.length; i++) {
				/* 635 */LeavehVO mainvo = (LeavehVO) aggVOs[i].getParentVO();
				/* 636 */mainvo.setBill_code(billCodes[i]);
				/*     */}
			/*     */
			/*     */
			/*     */
			/*     */
			/* 642 */AggLeaveVO[] retVOs = insertArrayData(aggVOs);
			/* 643 */commitBillCodes(pk_org, billCodes);
			/* 644 */return retVOs;
			/*     */}
		/*     */catch (Exception e) {
			/* 647 */if (!ArrayUtils.isEmpty(billCodes)) {
				/* 648 */rollbackBillCodes(pk_org, billCodes);
				/*     */}
			/* 650 */if ((e instanceof BusinessException))
				/* 651 */throw ((BusinessException) e);
			/* 652 */throw new BusinessException(e.getMessage(), e);
			/*     */}
		/*     */}

	/*     */
	/*     */
	/*     */
	/*     */public AggLeaveVO[] insertData(SplitBillResult<AggLeaveVO> splitResult)
	/*     */throws BusinessException
	/*     */{
		/* 661 */AggLeaveVO[] results = (AggLeaveVO[]) splitResult
				.getSplitResult();
		/*     */
		/* 663 */int billCodeCount = 0;
		/* 664 */for (AggLeaveVO aggVO : results)
		/*     */{
			/* 666 */LeavehVO headVO = aggVO.getHeadVO();
			/* 667 */if ((headVO.getIslactation() != null)
					&& (headVO.getIslactation().booleanValue())) {
				/* 668 */headVO.setLeaveyear(null);
				/* 669 */headVO.setLeavemonth(null);
				/*     */}
			/* 671 */if (StringUtils.isEmpty(headVO.getBill_code()))
				/* 672 */billCodeCount++;
			/*     */}
		/* 674 */String pk_org = ((AggLeaveVO) splitResult.getOriginalBill())
				.getHeadVO().getPk_org();
		/* 675 */String[] billCodes = null;
		/*     */try {
			/* 677 */billCodes = generateBillCodes(pk_org, billCodeCount);
			/* 678 */Iterator<String> codeIterator = ArrayUtils
					.isEmpty(billCodes) ? null : Arrays.asList(billCodes)
					.iterator();
			/* 679 */String splitid = new SequenceGenerator().generate();
			/* 680 */for (AggLeaveVO aggVO : results) {
				/* 681 */LeavehVO hVO = aggVO.getHeadVO();
				/* 682 */hVO.setSplitid(splitid);
				/* 683 */if (StringUtils.isEmpty(hVO.getBill_code()))
					/* 684 */hVO.setBill_code((String) codeIterator.next());
				/*     */}
			/* 686 */AggLeaveVO[] retVOs = (AggLeaveVO[]) getServiceTemplate()
					.insert(results);
			/*     */
			/* 688 */((ILeaveBalanceManageService) NCLocator.getInstance()
					.lookup(ILeaveBalanceManageService.class))
					.calLeaveBalanceVO4NewThread(pk_org, (Object[]) retVOs);
			/* 689 */billCodes = new String[results.length];
			/* 690 */for (int i = 0; i < results.length; i++) {
				/* 691 */billCodes[i] = results[i].getHeadVO().getBill_code();
				/*     */}
			/* 693 */commitBillCodes(pk_org, billCodes);
			/* 694 */return retVOs;
			/*     */
			/*     */
			/*     */
			/*     */
			/*     */}
		/*     */catch (Exception e)
		/*     */{
			/*     */
			/*     */
			/*     */
			/* 705 */rollbackBillCodes(pk_org, billCodes);
			/* 706 */if ((e instanceof BusinessException))
				/* 707 */throw ((BusinessException) e);
			/* 708 */throw new BusinessException(e.getMessage(), e);
			/*     */}
		/*     */}

	/*     */
	/*     */public AggLeaveVO[] updateData(SplitBillResult<AggLeaveVO> splitResult)
	/*     */throws BusinessException
	/*     */{
		/* 715 */LeavebVO[] bodyVO = ((AggLeaveVO) splitResult
				.getOriginalBill()).getBodyVOs();
		/*     */
		/* 717 */List<LeavebVO> bodyvoList = new ArrayList();
		/* 718 */for (LeavebVO bodyvo : bodyVO) {
			/* 719 */if (bodyvo.getStatus() == 3) {
				/* 720 */bodyvoList.add(bodyvo);
				/*     */}
			/*     */}
		/* 723 */AggLeaveVO[] results = (AggLeaveVO[]) splitResult
				.getSplitResult();
		/* 724 */BillMethods.processBeginEndDatePkJobOrgTimeZone(results);
		/* 725 */for (int i = 0; i < results.length; i++)
		/*     */{
			/* 727 */LeavebVO[] deleteVOS = (LeavebVO[]) bodyvoList
					.toArray(new LeavebVO[0]);
			/*     */
			/* 729 */LeavebVO[] leaveVOS = results[i].getBodyVOs();
			/* 730 */results[i]
					.setChildrenVO((CircularlyAccessibleValueObject[]) ArrayUtils
							.addAll(deleteVOS, leaveVOS));
			/*     */}
		/*     */
		/* 733 */int billCodeCount = 0;
		/* 734 */for (AggLeaveVO aggVO : results)
		/*     */{
			/* 736 */LeavehVO headVO = aggVO.getHeadVO();
			/* 737 */if ((headVO.getIslactation() != null)
					&& (headVO.getIslactation().booleanValue())) {
				/* 738 */headVO.setLeaveyear(null);
				/* 739 */headVO.setLeavemonth(null);
				/*     */}
			/* 741 */if (StringUtils.isEmpty(headVO.getBill_code()))
				/* 742 */billCodeCount++;
			/*     */}
		/* 744 */LeavehVO orihVO = (LeavehVO) new BaseDAO().retrieveByPK(
				LeavehVO.class, ((AggLeaveVO) splitResult.getOriginalBill())
						.getHeadVO().getPrimaryKey());
		/* 745 */String splitid = orihVO.getSplitid();
		/* 746 */String pk_org = orihVO.getPk_org();
		/* 747 */String[] billCodes = null;
		/*     */try {
			/* 749 */billCodes = generateBillCodes(pk_org, billCodeCount);
			/* 750 */Iterator<String> codeIterator = ArrayUtils
					.isEmpty(billCodes) ? null : Arrays.asList(billCodes)
					.iterator();
			/* 751 */List<AggLeaveVO> resultList = new ArrayList();
			/* 752 */List<AggLeaveVO> insertList = new ArrayList();
			/* 753 */List<AggLeaveVO> updateList = new ArrayList();
			/* 754 */for (AggLeaveVO aggVO : results) {
				/* 755 */LeavehVO hVO = aggVO.getHeadVO();
				/* 756 */hVO.setSplitid(splitid);
				/* 757 */if (StringUtils.isEmpty(hVO.getBill_code()))
					/* 758 */hVO.setBill_code((String) codeIterator.next());
				/* 759 */if (1 == hVO.getStatus()) {
					/* 760 */updateList.add(aggVO);
					/* 761 */} else if (2 == hVO.getStatus()) {
					/* 762 */insertList.add(aggVO);
					/* 763 */} else if (0 == hVO.getStatus()) {
					/* 764 */LeavebVO[] bVOs = aggVO.getBodyVOs();
					/* 765 */for (LeavebVO bVo : bVOs) {
						/* 766 */if (0 == bVo.getStatus()) {
							/* 767 */resultList.add(aggVO);
							/*     */} else {
							/* 769 */updateList.add(aggVO);
							/* 770 */break;
							/*     */}
						/*     */}
					/*     */} else {
					/* 774 */resultList.add(aggVO);
					/*     */}
			}
			/* 776 */if (CollectionUtils.isNotEmpty(updateList))
				/* 777 */CommonMethods.mergeArrayToCol(
						resultList,
						getServiceTemplate().update(true,
								updateList.toArray(new AggLeaveVO[0])));
			/* 778 */if (CollectionUtils.isNotEmpty(insertList))
				/* 779 */CommonMethods.mergeArrayToCol(
						resultList,
						getServiceTemplate().insert(
								insertList.toArray(new AggLeaveVO[0])));
			/* 780 */results = (AggLeaveVO[]) resultList
					.toArray(new AggLeaveVO[0]);
			/*     */
			/* 782 */((ILeaveBalanceManageService) NCLocator.getInstance()
					.lookup(ILeaveBalanceManageService.class))
					.calLeaveBalanceVO4NewThread(pk_org, (Object[]) results);
			/* 783 */commitBillCodes(pk_org, billCodes);
			/* 784 */return results;
			/*     */} catch (Exception e) {
			/* 786 */rollbackBillCodes(pk_org, billCodes);
			/* 787 */if ((e instanceof BusinessException))
				/* 788 */throw ((BusinessException) e);
			/* 789 */throw new BusinessException(e.getMessage(), e);
			/*     */}
		/*     */}

	/*     */
	/*     */private void genRegAndCalculate(AggLeaveVO[] aggVOs)
			throws BusinessException {
		/* 794 */ILeaveRegisterManageMaintain regService = (ILeaveRegisterManageMaintain) NCLocator
				.getInstance().lookup(ILeaveRegisterManageMaintain.class);
		/* 795 */List<LeaveRegVO> insertList = new ArrayList();
		/* 796 */for (AggLeaveVO aggVO : aggVOs) {
			/* 797 */LeavehVO leavehVO = aggVO.getLeavehVO();
			/* 798 */for (LeavebVO leavebVO : aggVO.getBodyVOs())
			/*     */{
				/* 800 */LeaveRegVO regVO = new LeaveRegVO();
				/* 801 */regVO.setPk_group(leavehVO.getPk_group());
				/* 802 */regVO.setPk_org(leavehVO.getPk_org());
				/* 803 */regVO.setBillsource(Integer.valueOf(0));
				/* 804 */regVO.setPk_billsourceh(leavehVO.getPk_leaveh());
				/* 805 */regVO.setBill_code(leavehVO.getBill_code());
				/*     */
				/* 807 */regVO.setIslactation(leavehVO.getIslactation());
				/* 808 */regVO.setLactationholidaytype(leavebVO
						.getLactationholidaytype());
				/* 809 */regVO.setLactationhour(leavehVO.getLactationhour());
				/*     */
				/* 811 */regVO.setIsleaveoff(UFBoolean.FALSE);
				/* 812 */regVO.setPk_leavetype(leavehVO.getPk_leavetype());
				/* 813 */regVO.setPk_leavetypecopy(leavehVO
						.getPk_leavetypecopy());
				/* 814 */regVO.setPk_psnjob(leavehVO.getPk_psnjob());
				/* 815 */regVO.setPk_psnorg(leavehVO.getPk_psnorg());
				/* 816 */regVO.setPk_psndoc(leavehVO.getPk_psndoc());
				/* 817 */regVO.setPk_timeitem(leavehVO.getPk_timeitem());
				/* 818 */regVO.setLeaveyear(leavehVO.getLeaveyear());
				/* 819 */regVO.setLeavemonth(leavehVO.getLeavemonth());
				/* 820 */regVO.setLeaveindex(leavehVO.getLeaveindex());
				/*     */
				/* 822 */regVO.setPk_billsourceb(leavebVO.getPk_leaveb());
				/* 823 */regVO.setLeavebegindate(leavebVO.getLeavebegindate());
				/* 824 */regVO.setLeaveenddate(leavebVO.getLeaveenddate());
				/* 825 */regVO.setLeavebegintime(leavebVO.getLeavebegintime());
				/* 826 */regVO.setLeaveendtime(leavebVO.getLeaveendtime());
				/*     */
				/* 828 */regVO.setLeaveremark(leavebVO.getLeaveremark());
				/* 829 */regVO.setLeavehour(leavebVO.getLeavehour());
				/*     */
				/* 831 */regVO.setRealdayorhour(leavehVO.getRealdayorhour());
				/* 832 */regVO
						.setResteddayorhour(leavehVO.getResteddayorhour());
				/* 833 */regVO.setRestdayorhour(leavehVO.getRestdayorhour());
				/* 834 */regVO
						.setFreezedayorhour(leavehVO.getFreezedayorhour());
				/* 835 */regVO
						.setUsefuldayorhour(leavehVO.getUsefuldayorhour());
				/*     */
				/* 837 */regVO.setPk_org_v(leavehVO.getPk_org_v());
				/* 838 */regVO.setPk_dept_v(leavehVO.getPk_dept_v());
				/*     */
				/* 840 */insertList.add(regVO);
				/*     */}
			/*     */}
		LeaveRegVO[] t = (LeaveRegVO[]) insertList.toArray(new LeaveRegVO[0]);
		/* 843 */regService.insertData(t, false);
		/* 844 */((ILeaveBalanceManageService) NCLocator.getInstance().lookup(
				ILeaveBalanceManageService.class)).queryAndCalLeaveBalanceVO(
				aggVOs[0].getLeavehVO().getPk_org(), (Object[]) aggVOs);
		/*     */}

	/*     */
	/*     */protected String getBillCodeFieldName()
	/*     */{
		/* 849 */return "bill_code";
		/*     */}

	/*     */
	/*     */protected String getBillType()
	/*     */{
		/* 854 */return "6404";
		/*     */}

	/*     */
	/*     */protected Class<?> getHeadVOClass()
	/*     */{
		/* 859 */return LeavehVO.class;
		/*     */}

	/*     */
	/*     */protected TaWorkFlowManager<LeavehVO, LeavebVO>.UserValueConfig getUserValueConfig()
	/*     */{
		/* 864 */TaWorkFlowManager<LeavehVO, LeavebVO>.UserValueConfig config = new TaWorkFlowManager.UserValueConfig();
		/* 865 */config.setBillCodeFieldName("bill_code");
		/* 866 */config.setApproveStateFieldName("approve_state");
		/* 867 */config.setFieldCodes(LeaveConst.FIELDCODE);
		/* 868 */return config;
		/*     */}

	/*     */
	/*     */protected void syncHeadInfoToBody(AggregatedValueObject[] aggVOs)
	/*     */{
		/* 873 */if (ArrayUtils.isEmpty(aggVOs))
			/* 874 */return;
		/* 875 */for (AggregatedValueObject aggVO : aggVOs) {
			/* 876 */LeavebVO[] bvos = (LeavebVO[]) aggVO.getChildrenVO();
			/* 877 */if (!ArrayUtils.isEmpty(bvos))
			/*     */{
				/* 879 */LeavehVO hvo = (LeavehVO) aggVO.getParentVO();
				/* 880 */String pk_psndoc = hvo.getPk_psndoc();
				/* 881 */String pk_psnjob = hvo.getPk_psnjob();
				/* 882 */String pk_psnorg = hvo.getPk_psnorg();
				/* 883 */String pk_timeitem = hvo.getPk_timeitem();
				/* 884 */String pk_timeitemcopy = hvo.getPk_leavetypecopy();
				/* 885 */for (LeavebVO bvo : bvos) {
					/* 886 */bvo.setPk_psndoc(pk_psndoc);
					/* 887 */bvo.setPk_psnjob(pk_psnjob);
					/* 888 */bvo.setPk_psnorg(pk_psnorg);
					/* 889 */bvo.setPk_timeitem(pk_timeitem);
					/* 890 */bvo.setPk_leavetypecopy(pk_timeitemcopy);
					/*     */}
				/*     */}
			/*     */}
		/*     */}
}
