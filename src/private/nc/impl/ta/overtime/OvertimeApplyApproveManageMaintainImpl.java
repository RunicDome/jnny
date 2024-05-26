package nc.impl.ta.overtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.trade.business.HYPubBO;
import nc.bs.uif2.validation.DefaultValidationService;
import nc.hr.frame.persistence.HrBatchService;
import nc.hr.utils.PubEnv;
import nc.hr.utils.StringPiecer;
import nc.impl.ta.algorithm.BillValidatorAtServer;
import nc.impl.ta.overtime.validator.OvertimeApplyApproveValidator;
import nc.impl.ta.overtime.validator.OvertimeApplyValidatorFactory;
import nc.impl.ta.timebill.BillMethods;
import nc.itf.ta.IOvertimeApplyApproveManageMaintain;
import nc.itf.ta.PeriodServiceFacade;
import nc.itf.ta.algorithm.BillProcessHelper;
import nc.pub.tools.VOUtils;
import nc.pubitf.para.SysInitQuery;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.om.job.JobVO;
import nc.vo.om.post.PostVO;
import nc.vo.om.pub.AggVOHelper;
import nc.vo.org.GroupVO;
import nc.vo.org.HROrgVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.sm.UserVO;
import nc.vo.ta.overtime.AggOvertimeVO;
import nc.vo.ta.overtime.OvertimeConst;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.ta.overtime.OvertimebVO;
import nc.vo.ta.overtime.OvertimehVO;
import nc.vo.ta.overtime.pf.validator.PFSaveOvertimeValidator;
import nc.vo.ta.timeitem.OverTimeTypeVO;
import nc.vo.ta.wf.pub.TaWorkFlowManager;
import nc.vo.vorg.AdminOrgVersionVO;
import nc.vo.vorg.DeptVersionVO;
import nc.vo.wfengine.definition.WorkflowTypeEnum;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
//import nc.vo.ta.leave.AggLeaveVO;
//import nc.vo.ta.leave.LeavebVO;
//import nc.vo.ta.leave.OvertimehVO;
// 加班申请单

public class OvertimeApplyApproveManageMaintainImpl extends
		TaWorkFlowManager<OvertimehVO, OvertimebVO> implements
		IOvertimeApplyApproveManageMaintain {

	private HrBatchService serviceTemplate;

	/*     */
	/*     */public OvertimeApplyApproveManageMaintainImpl() {
	}

	/*     */
	/*     */private HrBatchService getServiceTemplate()
	/*     */{
		/* 53 */if (this.serviceTemplate == null) {
			/* 54 */this.serviceTemplate = new HrBatchService(
					"a7e298c9-3211-489d-8698-7ae65a922156");
			/*     */}
		/* 56 */return this.serviceTemplate;
		/*     */}

	/*     */
	/*     */public void deleteArrayData(AggOvertimeVO[] vos)
			throws BusinessException
	/*     */{
		/* 61 */getServiceTemplate().delete(vos);
		/* 62 */String pk_org = vos[0].getHeadVO().getPk_org();
		/* 63 */returnBillCodeOnDelete(pk_org, getBillType(),
				getBillCodeFormVOs(vos));
		/*     */}

	/*     */
	/*     */public void deleteData(AggOvertimeVO vo) throws BusinessException
	/*     */{
		/* 68 */getServiceTemplate().delete(new AggOvertimeVO[] { vo });
		/* 69 */OvertimehVO mainVO = (OvertimehVO) vo.getParentVO();
		/* 70 */returnBillCodeOnDelete(mainVO.getPk_org(),
				mainVO.getBill_code());
		/*     */}

	/*     */
	/*     */public AggOvertimeVO[] insertArrayData(AggOvertimeVO[] vos)
			throws BusinessException
	/*     */{
		/* 75 */throw new UnsupportedOperationException();
		/*     */}

	/*     */
	/*     */public AggOvertimeVO insertData(AggOvertimeVO vo)
			throws BusinessException
	/*     */{
		/* 80 */OvertimehVO mainVO = (OvertimehVO) vo.getParentVO();
		/* 81 */BillMethods
				.processBeginEndDatePkJobOrgTimeZone(vo.getBodyVOs());
		/*     */
		/*     */
		/* 84 */DefaultValidationService vService = new DefaultValidationService();
		/* 85 */vService.addValidator(new PFSaveOvertimeValidator());
		/* 86 */vService.validate(vo);
		/* 87 */setValidatorFactory();
		/* 88 */new OvertimeApplyQueryMaintainImpl().check(vo);
		/*     */
		/*     */
		/* 91 */List<OvertimebVO> bVOList = BillProcessHelper
				.toOvertimebVOList(new AggOvertimeVO[] { vo });
		/* 92 */OvertimebVO[] bvos = (OvertimebVO[]) bVOList
				.toArray(new OvertimebVO[0]);
		/* 93 */BillValidatorAtServer.checkCrossBU(vo.getOvertimehVO()
				.getPk_org(), bvos);
		/*     */
		/* 95 */AggOvertimeVO aggVO = ((AggOvertimeVO[]) getServiceTemplate()
				.insert(new AggOvertimeVO[] { vo }))[0];
		/*     */
		/* 97 */commitBillCode(mainVO.getPk_org(), mainVO.getBill_code());
		/* 98 */return aggVO;
		/*     */}

	/*     */
	/*     */public AggOvertimeVO insertDataDirect(AggOvertimeVO vo)
	/*     */throws BusinessException
	/*     */{
		/* 104 */OvertimehVO mainVO = (OvertimehVO) vo.getParentVO();
		/* 105 */AggOvertimeVO aggVO = ((AggOvertimeVO[]) getServiceTemplate()
				.insert(new AggOvertimeVO[] { vo }))[0];
		/*     */
		/* 107 */commitBillCode(mainVO.getPk_org(), mainVO.getBill_code());
		/* 108 */return aggVO;
		/*     */}

	/*     */
	/*     */public AggOvertimeVO[] updateArrayData(AggOvertimeVO[] vos)
			throws BusinessException {
		/* 112 */return updateData(vos, true, true);
		/*     */}

	/*     */
	/*     */protected AggOvertimeVO[] updateData(AggOvertimeVO[] vos,
			boolean isSetAuditInfo, boolean needCheck) throws BusinessException {
		/* 116 */BillMethods
				.processBeginEndDatePkJobOrgTimeZone(BillProcessHelper
						.toOvertimebVOList(vos).toArray(new OvertimebVO[0]));
		/* 117 */String pk_org = vos[0].getHeadVO().getPk_org();
		/* 118 */for (AggOvertimeVO aggVO : vos) {
			/* 119 */OvertimebVO[] bvos = aggVO.getBodyVOs();
			/* 120 */int len = ArrayUtils.getLength(bvos);
			/* 121 */if (len != 0)
			/*     */{
				/* 123 */OvertimehVO hvo = aggVO.getOvertimehVO();
				/* 124 */for (OvertimebVO bvo : bvos) {
					/* 125 */bvo.setPk_overtimetype(hvo.getPk_overtimetype());
					/* 126 */bvo.setPk_overtimetypecopy(hvo
							.getPk_overtimetypecopy());
					/* 127 */bvo.setPk_psndoc(hvo.getPk_psndoc());
					/* 128 */bvo.setPk_psnjob(hvo.getPk_psnjob());
					/* 129 */bvo.setPk_psnorg(hvo.getPk_psnorg());
					/*     */
					/* 131 */if (0 == bvo.getStatus())
						/* 132 */bvo.setStatus(1);
					/*     */}
				/*     */}
		}
		/* 135 */if (needCheck) {
			/* 136 */OvertimeApplyApproveValidator validator = new OvertimeApplyApproveValidator();
			/* 137 */validator.checkOvertimeAggVOs(vos);
			/* 138 */setValidatorFactory();
			/* 139 */new OvertimeApplyQueryMaintainImpl().check(pk_org,
					(OvertimebVO[]) BillProcessHelper.toOvertimebVOList(vos)
							.toArray(new OvertimebVO[0]));
			/* 140 */OvertimebVO[] checkvos = (OvertimebVO[]) BillProcessHelper
					.toOvertimebVOList(vos).toArray(new OvertimebVO[0]);
			/*     */
			/* 142 */BillValidatorAtServer.checkCrossBU(pk_org, checkvos);
			/*     */}
		/* 144 */return (AggOvertimeVO[]) getServiceTemplate().update(
				isSetAuditInfo, vos);
		/*     */}

	/*     */
	/*     */public AggOvertimeVO updateData(AggOvertimeVO vo)
			throws BusinessException
	/*     */{
		/* 149 */return updateArrayData(new AggOvertimeVO[] { vo })[0];
		/*     */}

	/*     */
	/*     */
	/*     */
	/*     */private void setValidatorFactory()
	/*     */{
		/* 156 */getServiceTemplate().setValidatorFactory(
				new OvertimeApplyValidatorFactory());
		/*     */}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */public AggOvertimeVO[] directApprove(int directApproveResult,
			String approveNote, AggOvertimeVO[] vos)
	/*     */throws BusinessException
	/*     */{
		/* 165 */String pk_org = vos[0].getOvertimehVO().getPk_org();
		/*     */
		/*     */
		/* 168 */if ((directApproveResult != -1) && (directApproveResult != 0)) {
			/* 169 */BillValidatorAtServer.checkPeriod(pk_org,
					BillProcessHelper.toOvertimebVOs(vos));
			/*     */}
		/* 171 */WorkflownoteVO[] worknoteVOs = new WorkflownoteVO[vos.length];
		/*     */
		/* 173 */OvertimehVO[] updateMainVOs = new OvertimehVO[vos.length];
		/*     */
		/* 175 */String[] updateFields = { "approver", "approve_time",
				"approve_note", "approve_state" };
		/*     */
		/* 177 */for (int i = 0; i < vos.length; i++) {
			/* 178 */OvertimehVO mainvo = vos[i].getOvertimehVO();
			/*     */
			/* 180 */updateMainVOs[i] = changeBillData(mainvo, updateFields,
					approveNote, Integer.valueOf(directApproveResult));
			/*     */
			/* 182 */worknoteVOs[i] = buildWorkflownoteVO(directApproveResult,
					approveNote, mainvo);
			/*     */}
		/*     */
		/* 185 */getIPersistenceUpdate().insertVOArray(null, worknoteVOs, null);
		/*     */
		/* 187 */getIPersistenceUpdate().updateVOArray(null, updateMainVOs,
				updateFields, null);
		/*     */
		/* 189 */if (directApproveResult == 1)
			/* 190 */exexBills(pk_org, vos);
		/* 191 */return vos;
		/*     */}

	/*     */
	/*     */public AggOvertimeVO directApprove(AggOvertimeVO vo)
	/*     */throws BusinessException
	/*     */{
		/* 197 */return null;
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
			String approveNote, OvertimehVO mainvo)
	/*     */throws BusinessException
	/*     */{
		/* 210 */WorkflownoteVO worknoteVO = new WorkflownoteVO();
		/* 211 */worknoteVO.setBillid(mainvo.getPk_overtimeh());
		/* 212 */worknoteVO.setBillVersionPK(mainvo.getPrimaryKey());
		/* 213 */worknoteVO.setChecknote(approveNote);
		/* 214 */worknoteVO.setDealdate(PubEnv.getServerTime());
		/* 215 */worknoteVO.setSenddate(PubEnv.getServerTime());
		/* 216 */worknoteVO.setPk_org(mainvo.getPk_org());
		/* 217 */worknoteVO.setBillno(mainvo.getBill_code());
		/* 218 */worknoteVO.setSenderman(mainvo.getApprover() == null ? mainvo
				.getBillmaker() : mainvo.getApprover());
		/* 219 */worknoteVO.setApproveresult(1 == directApproveResult ? "Y"
				: -1 == directApproveResult ? "R" : "N");
		/*     */
		/* 221 */worknoteVO.setApprovestatus(Integer.valueOf(1));
		/* 222 */worknoteVO.setIscheck(0 == directApproveResult ? "N"
				: 1 == directApproveResult ? "Y" : "X");
		/*     */
		/* 224 */worknoteVO.setActiontype("APPROVE");
		/* 225 */worknoteVO.setCheckman(mainvo.getApprover());
		/* 226 */worknoteVO.setWorkflow_type(Integer
				.valueOf(WorkflowTypeEnum.Approveflow.getIntValue()));
		/* 227 */worknoteVO.setPk_billtype(mainvo.getPk_billtype());
		/* 228 */return worknoteVO;
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
	/*     */private OvertimehVO changeBillData(OvertimehVO mainvo,
			String[] updateFields, String approveNote,
			Integer directApproveResult)
	/*     */throws BusinessException
	/*     */{
		/* 242 */if (mainvo == null)
			/* 243 */return null;
		/* 244 */mainvo.setAttributeValue(updateFields[0], PubEnv.getPk_user());
		/* 245 */mainvo.setAttributeValue(updateFields[1],
				PubEnv.getServerTime());
		/* 246 */mainvo.setAttributeValue(updateFields[2], approveNote);
		/* 247 */mainvo.setAttributeValue(updateFields[3], directApproveResult);
		/* 248 */return mainvo;
		/*     */}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */public AggOvertimeVO[] doCommit(AggOvertimeVO[] vos)
	/*     */throws BusinessException
	/*     */{
		/* 258 */for (AggOvertimeVO vo : vos) {
			/* 259 */OvertimehVO billvo = vo.getOvertimehVO();
			/*     */
			/* 261 */billvo.setApprove_state(Integer.valueOf(3));
			/* 262 */vo.setParentVO(billvo);
			/*     */}
		/*     */
		/*     */
		/* 266 */PeriodServiceFacade.checkDateScope(vos[0].getHeadVO()
				.getPk_org(), BillProcessHelper.toOvertimebVOs(vos));
		/* 267 */updateData(vos, false, false);
		/* 268 */String pk_org = vos[0].getOvertimehVO().getPk_org();
		/* 269 */Integer approvetype = SysInitQuery.getParaInt(pk_org,
				"HROM0001");
		/* 270 */if ((approvetype != null) && (approvetype.intValue() == 0))
		/*     */{
			/*     */
			/* 273 */sendDirApprMessage(pk_org, "600112", vos);
			/*     */}
		senOaData(vos);
		/* 275 */return vos;
		/*     */}

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

	private WorkFlowBill getWorkFlowBill(AggOvertimeVO temp)
			throws BusinessException {
		String where = " nvl(dr,0) = 0 and pk_bill = '"
				+ temp.getOvertimehVO().getPk_overtimeh() + "'";
		WorkFlowBill[] workFlowBills = (WorkFlowBill[]) getHyPubBO()
				.queryByCondition(WorkFlowBill.class, where);
		WorkFlowBill workFlowBill = new WorkFlowBill();
		if (null != workFlowBills && workFlowBills.length > 1) {
			throw new BusinessException("查询到多条流程记录");
		}
		if (null != workFlowBills && workFlowBills.length == 1) {
			workFlowBill = workFlowBills[0];
			workFlowBill.setWorkflowName("加班申请单");
		} else {
			UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
					UserVO.class, temp.getOvertimehVO().getBillmaker());
			workFlowBill.setPk_group(temp.getOvertimehVO().getPk_group());
			workFlowBill.setPk_org(temp.getOvertimehVO().getPk_org());
			workFlowBill.setPk_bill(temp.getOvertimehVO().getPk_overtimeh());
			workFlowBill.setUser_name(userVO.getUser_name());
			workFlowBill.setUser_code(userVO.getUser_code());
			workFlowBill.setCreationtime(new UFDate().toStdString());
			workFlowBill.setDef5(userVO.getUser_code());
			workFlowBill.setBill_status("1");
			workFlowBill.setCreator(temp.getOvertimehVO().getCreator());
			workFlowBill.setBill_code("6405");
			workFlowBill.setWorkflowId("63");
			workFlowBill.setWorkflowName("加班申请单");
		}
		return workFlowBill;
	}

	private void senOaData(AggOvertimeVO[] billVOs) throws BusinessException {
		for (AggOvertimeVO temp : billVOs) {
			// if((temp.getOvertimehVO().getTranstype()).contains("6405")){
			if ("6405".equals(temp.getOvertimehVO().getPk_billtype())) {
				JSONArray headData = getMainMap(temp.getOvertimehVO());
				JSONArray bodyData = getDtaileDataMap(temp);
				/*
				 * step-1 调用OA工具类同步数据至OA
				 */
				OaWorkFlowUtil.sendOaData(headData,
						bodyData, getWorkFlowBill(temp));
			}
		}
	}

	private JSONArray getDtaileDataMap(AggOvertimeVO aggVO)
			throws BusinessException {
		Map overtimeVO = getContrAlterBodyVO(aggVO);
		JSONArray dtlistString = JSONArray.fromObject(overtimeVO);
		return dtlistString;
	}

	private Map getContrAlterBodyVO(AggOvertimeVO aggVO)
			throws BusinessException {
		Map dtMap = new HashMap();
		dtMap.put("tableDBName", "formtable_main_68_dt1");
		List workflowRequestTableRecords = new ArrayList();
		OvertimebVO[] bodyVOS = (OvertimebVO[]) aggVO.getChildrenVO();
		for (OvertimebVO temp : bodyVOS) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();

			/* 单据编码 */
			Map djbm = OaWorkFlowUtil.listAdd("djbm", temp.getBill_code());
			workflowRequestTableFields.add(djbm);

			/* 加班开始时间 */
			Map jbkssj = OaWorkFlowUtil.listAdd("jbkssj", temp
					.getOvertimebegintime() + "");
			workflowRequestTableFields.add(jbkssj);

			/* 加班结束时间 */
			Map jbjssj = OaWorkFlowUtil.listAdd("jbjssj", temp
					.getOvertimeendtime() + "");
			workflowRequestTableFields.add(jbjssj);

			/* 申请时长 */
			Map sqsc = OaWorkFlowUtil.listAdd("sqsc", temp.getOvertimehour()
					.setScale(2, UFDouble.ROUND_HALF_UP) + "");
			workflowRequestTableFields.add(sqsc);

			/* 扣除时间 */
			Map kcsj = OaWorkFlowUtil.listAdd("kcsj", temp.getDeduct() + "");
			workflowRequestTableFields.add(kcsj);

			/* 所在考勤已加班时数 */
			Map szkqyjbss = OaWorkFlowUtil.listAdd("szkqyjbss", temp
					.getOvertimealready().setScale(2, UFDouble.ROUND_HALF_UP)
					+ "");
			workflowRequestTableFields.add(szkqyjbss);

			/* 所属集团 */
			Map szjt = OaWorkFlowUtil.listAdd("szjt",
					VOUtils.getDocName(GroupVO.class, temp.getPk_group()));
			workflowRequestTableFields.add(szjt);

			/* 加班申请单子表主键 */
			Map jbsqdzbzj = OaWorkFlowUtil.listAdd("jbsqdzbzj",
					temp.getPk_overtimeb());
			workflowRequestTableFields.add(jbsqdzbzj);

			/* 加班申请单主键 */
			Map jbsqdzj = OaWorkFlowUtil.listAdd("jbsqdzj",
					temp.getPk_overtimeh());
			workflowRequestTableFields.add(jbsqdzj);

			/* 所属组织 */
			Map szzz = OaWorkFlowUtil.listAdd("szzz",
					VOUtils.getDocName(HROrgVO.class, temp.getPk_org()));
			workflowRequestTableFields.add(szzz);

			/* 加班说明 */
			Map jbsm = OaWorkFlowUtil.listAdd("jbsm", temp.getOvertimeremark());
			workflowRequestTableFields.add(jbsm);

			/* 是否需要校验 */
			Map sfxyxy = OaWorkFlowUtil.listAdd("sfxyxy", temp.getIsneedcheck()
					.booleanValue() ? "是" : "否");
			workflowRequestTableFields.add(sfxyxy);

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	private JSONArray getMainMap(OvertimehVO parentVO) throws BusinessException {
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
				+ "");
		list.add(djzt);

		/* 申请人 */
		Map sqr = OaWorkFlowUtil.listAdd("sqr",
				VOUtils.getDocName(PsndocVO.class, parentVO.getBillmaker()));
		list.add(sqr);

		/* 申请日期 */
		Map sqrq = OaWorkFlowUtil.listAdd("sqrq", parentVO.getApply_date()
				.getYear()
				+ "-"
				+ parentVO.getApply_date().getStrMonth()
				+ "-"
				+ parentVO.getApply_date().getStrDay());
		list.add(sqrq);

		/* 所属集团 */
		Map szjt = OaWorkFlowUtil.listAdd("szjt",
				VOUtils.getDocName(GroupVO.class, parentVO.getPk_group()));
		list.add(szjt);

		/* 所属组织 */
		Map szzz = OaWorkFlowUtil.listAdd("szzz",
				VOUtils.getDocName(HROrgVO.class, parentVO.getPk_org()));
		list.add(szzz);

		/* 员工号 */
		PsnJobVO jlVO = (PsnJobVO) getHyPubBO().queryByPrimaryKey(
				PsnJobVO.class, parentVO.getPk_psnjob());
		if (null != jlVO) {
			Map ygh = OaWorkFlowUtil.listAdd("ygh", jlVO.getClerkcode());
			list.add(ygh);
		} else {
			Map ygh = OaWorkFlowUtil.listAdd("ygh", "0");
			list.add(ygh);
		}

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
		Map zz = OaWorkFlowUtil.listAdd(
				"zz",
				VOUtils.getDocName(AdminOrgVersionVO.class,
						parentVO.getPk_org_v()));
		list.add(zz);

		/* 部门 */
		Map bm = OaWorkFlowUtil.listAdd("bm", VOUtils.getDocName(
				DeptVersionVO.class, parentVO.getPk_dept_v()));
		list.add(bm);

		/* 岗位 */
		String post = (String) getHyPubBO().findColValue(
				"hi_psnjob",
				"pk_post",
				"nvl(dr,0) = 0 and pk_psnjob  ='" + parentVO.getPk_psnjob()
						+ "'");
		Map gw = OaWorkFlowUtil.listAdd("gw",
				VOUtils.getDocName(PostVO.class, post));
		list.add(gw);

		/* 职位 */
		String job = (String) getHyPubBO().findColValue(
				"hi_psnjob",
				"pk_job",
				"nvl(dr,0) = 0 and pk_psnjob  ='" + parentVO.getPk_psnjob()
						+ "'");
		Map zw = OaWorkFlowUtil.listAdd("zw",
				VOUtils.getDocName(JobVO.class, job));
		list.add(zw);
		list.add(zw);

		/* 加班类别 */
		Map jblb = OaWorkFlowUtil.listAdd(
				"jblb",
				VOUtils.getDocName(OverTimeTypeVO.class,
						parentVO.getPk_overtimetype()));
		list.add(jblb);

		/* 合计加班工时 */
		Map hjjbgs = OaWorkFlowUtil.listAdd("hjjbgs",
				parentVO.getSumhour().setScale(2, UFDouble.ROUND_HALF_UP) + "");
		list.add(hjjbgs);

		/* 创建人 */
		Map cjr = OaWorkFlowUtil.listAdd("cjr",
				VOUtils.getDocName(UserVO.class, parentVO.getCreator()));
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

		/* 加班单主键 */
		Map jbdzj = OaWorkFlowUtil.listAdd("jbdzj", parentVO.getPk_overtimeh());
		list.add(jbdzj);

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

	/*     */
	/*     */public AggOvertimeVO[] doApprove(AggOvertimeVO[] vos)
	/*     */throws BusinessException
	/*     */{
		/* 308 */if (ArrayUtils.isEmpty(vos))
			/* 309 */return null;
		/* 310 */syncHeadInfoToBody(vos);
		/*     */
		/*     */
		/*     */
		/*     */
		/* 315 */OvertimehVO headVO = vos[0].getOvertimehVO();
		/* 316 */int appStatus = headVO.getApprove_state().intValue();
		/* 317 */if ((appStatus != -1) && (appStatus != 0))
			/* 318 */PeriodServiceFacade.checkDateScope(vos[0].getHeadVO()
					.getPk_org(), BillProcessHelper.toOvertimebVOs(vos));
		/* 319 */vos = updateData(vos, false, false);
		/* 320 */if (appStatus == 1)
			/* 321 */exexBills(headVO.getPk_org(), vos);
		/* 322 */return vos;
		/*     */}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */public AggOvertimeVO[] doRecall(AggOvertimeVO[] vos)
	/*     */throws BusinessException
	/*     */{
		/* 332 */if (ArrayUtils.isEmpty(vos))
			/* 333 */return null;
		/* 334 */syncHeadInfoToBody(vos);
		/*     */
		/*     */
		/* 337 */PeriodServiceFacade.checkDateScope(vos[0].getHeadVO()
				.getPk_org(), BillProcessHelper.toOvertimebVOs(vos));
		/* 338 */vos = updateData(vos, false, false);
		/* 339 */return vos;
		/*     */}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */public void exexBills(String pk_org, AggOvertimeVO[] vos)
	/*     */throws BusinessException
	/*     */{
		/* 349 */List<OvertimeRegVO> regvos = new ArrayList();
		/* 350 */for (AggOvertimeVO aggvo : vos) {
			/* 351 */OvertimehVO mainvo = aggvo.getOvertimehVO();
			/* 352 */OvertimebVO[] subvos = aggvo.getOvertimebVOs();
			/* 353 */if (!ArrayUtils.isEmpty(subvos))
			/*     */{
				/* 355 */for (OvertimebVO subvo : subvos)
					/* 356 */regvos.add(buildOvertimeRegVO(mainvo, subvo));
				/*     */}
			/*     */}
		/* 359 */if (CollectionUtils.isNotEmpty(regvos)) {
			/* 360 */new OvertimeRegisterMaintainImpl().insertData(
					(OvertimeRegVO[]) regvos.toArray(new OvertimeRegVO[0]),
					false);
			/*     */}
		/*     */}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */private OvertimeRegVO buildOvertimeRegVO(OvertimehVO mainvo,
			OvertimebVO subvo)
	/*     */{
		/* 371 */OvertimeRegVO regvo = new OvertimeRegVO();
		/* 372 */regvo.setBillsource(Integer.valueOf(0));
		/*     */
		/* 374 */regvo.setPk_billsourceh(mainvo.getPk_overtimeh());
		/* 375 */regvo.setPk_billsourceb(subvo.getPk_overtimeb());
		/* 376 */regvo.setPk_overtimegen(null);
		/* 377 */regvo.setPk_group(mainvo.getPk_group());
		/* 378 */regvo.setPk_org(mainvo.getPk_org());
		/* 379 */regvo.setPk_org_v(mainvo.getPk_org_v());
		/* 380 */regvo.setPk_dept_v(mainvo.getPk_dept_v());
		/* 381 */regvo.setPk_overtimetype(mainvo.getPk_overtimetype());
		/* 382 */regvo.setPk_overtimetypecopy(mainvo.getPk_overtimetypecopy());
		/* 383 */regvo.setPk_psndoc(mainvo.getPk_psndoc());
		/* 384 */regvo.setPk_psnjob(mainvo.getPk_psnjob());
		/* 385 */regvo.setPk_psnorg(mainvo.getPk_psnorg());
		/*     */
		/* 387 */regvo.setActhour(subvo.getActhour());
		/* 388 */regvo.setDeduct(subvo.getDeduct());
		/* 389 */regvo.setIscheck(UFBoolean.FALSE);
		/* 390 */regvo.setIsneedcheck(subvo.getIsneedcheck());
		/* 391 */regvo.setIstorest(UFBoolean.FALSE);
		/* 392 */regvo.setToresthour(UFDouble.ZERO_DBL);
		/* 393 */regvo.setTorestmonth(null);
		/* 394 */regvo.setTorestyear(null);
		/* 395 */regvo.setOvertimealready(subvo.getOvertimealready());
		/* 396 */regvo.setOvertimebegindate(subvo.getOvertimebegindate());
		/* 397 */regvo.setOvertimebegintime(subvo.getOvertimebegintime());
		/* 398 */regvo.setOvertimeenddate(subvo.getOvertimeenddate());
		/* 399 */regvo.setOvertimeendtime(subvo.getOvertimeendtime());
		/* 400 */regvo.setOvertimehour(subvo.getOvertimehour());
		/* 401 */regvo.setOvertimeremark(subvo.getOvertimeremark());
		/*     */
		/* 403 */regvo.setStatus(2);
		/*     */
		/* 405 */return regvo;
		/*     */}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */public AggOvertimeVO[] doUnApprove(AggOvertimeVO[] vos)
	/*     */throws BusinessException
	/*     */{
		/* 415 */if (ArrayUtils.isEmpty(vos)) {
			/* 416 */return null;
			/*     */}
		/* 418 */OvertimehVO[] mainvos = (OvertimehVO[]) AggVOHelper
				.getParentVOArrayFromAggVOs(vos, OvertimehVO.class);
		/* 419 */AggOvertimeVO[] oldvos = (AggOvertimeVO[]) getServiceTemplate()
				.queryByPks(AggOvertimeVO.class,
						StringPiecer.getStrArray(mainvos, "pk_overtimeh"));
		/* 420 */for (AggOvertimeVO oldvo : oldvos) {
			/* 421 */checkPFPassingState(oldvo.getOvertimehVO()
					.getApprove_state().intValue());
			/*     */}
		/* 423 */syncHeadInfoToBody(vos);
		/*     */
		/*     */
		/* 426 */PeriodServiceFacade.checkDateScope(vos[0].getHeadVO()
				.getPk_org(), BillProcessHelper.toOvertimebVOs(vos));
		/* 427 */vos = updateData(vos, false, false);
		/* 428 */return vos;
		/*     */}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */public AggOvertimeVO[] doDelete(AggOvertimeVO[] vos)
	/*     */throws BusinessException
	/*     */{
		/* 438 */for (AggOvertimeVO vo : vos) {
			/* 439 */OvertimehVO headVO = vo.getOvertimehVO();
			/* 440 */String billType = headVO.getPk_billtype();
			/* 441 */deleteOldWorknote(headVO.getPrimaryKey(), billType);
			/*     */}
		/* 443 */deleteArrayData(vos);
		/* 444 */return vos;
		/*     */}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */public AggOvertimeVO[] insertData(OvertimebVO[] vos, boolean mergeBill)
	/*     */throws BusinessException
	/*     */{
		/* 453 */BillMethods.processBeginEndDatePkJobOrgTimeZone(vos);
		/* 454 */if (ArrayUtils.isEmpty(vos))
			/* 455 */return null;
		/* 456 */String pk_org = vos[0].getPk_org();
		/* 457 */setValidatorFactory();
		/*     */
		/* 459 */BillValidatorAtServer.checkCrossBU(pk_org, vos);
		/*     */
		/* 461 */AggOvertimeVO[] mergedAggVOs = mergeSubvo(vos, mergeBill);
		/*     */
		/*     */
		/* 464 */DefaultValidationService vService = new DefaultValidationService();
		/* 465 */vService.addValidator(new PFSaveOvertimeValidator());
		/* 466 */vService.validate(mergedAggVOs);
		/* 467 */setValidatorFactory();
		/* 468 */BillValidatorAtServer.checkOvertime(vos[0].getPk_org(),
				mergedAggVOs);
		/*     */
		/* 470 */String[] billCodes = null;
		/*     */try {
			/* 472 */billCodes = generateBillCodes(pk_org, mergedAggVOs.length);
			/* 473 */for (int i = 0; i < billCodes.length; i++) {
				/* 474 */mergedAggVOs[i].getHeadVO().setBill_code(billCodes[i]);
				/*     */}
			/* 476 */AggOvertimeVO[] aggVOs = (AggOvertimeVO[]) getServiceTemplate()
					.insert(mergedAggVOs);
			/* 477 */commitBillCodes(pk_org, billCodes);
			/* 478 */return aggVOs;
			/*     */} catch (Exception e) {
			/* 480 */rollbackBillCodes(pk_org, billCodes);
			/* 481 */if ((e instanceof BusinessException))
				/* 482 */throw ((BusinessException) e);
			/* 483 */throw new BusinessException(e.getMessage(), e);
			/*     */}
		/*     */}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */private AggOvertimeVO[] mergeSubvo(OvertimebVO[] vos, boolean mergeBill)
	/*     */throws BusinessException
	/*     */{
		/* 495 */if (ArrayUtils.isEmpty(vos)) {
			/* 496 */return null;
			/*     */}
		/*     */
		/* 499 */new OvertimeDAO().setAlreadyHour(vos);
		/* 500 */AggOvertimeVO[] aggvoArray = null;
		/* 501 */if (!mergeBill) {
			/* 502 */aggvoArray = new AggOvertimeVO[vos.length];
			/* 503 */for (int i = 0; i < aggvoArray.length; i++) {
				/* 504 */AggOvertimeVO aggVO = new AggOvertimeVO();
				/* 505 */aggvoArray[i] = aggVO;
				/* 506 */OvertimebVO[] bvos = { vos[i] };
				/* 507 */aggVO.setParentVO(createMainVO(bvos));
				/* 508 */aggVO.setChildrenVO(bvos);
				/*     */}
			/*     */}
		/*     */else {
			/* 512 */aggvoArray = (AggOvertimeVO[]) mergeSubVOs(
					AggOvertimeVO.class, vos);
			/*     */}
		/* 514 */return aggvoArray;
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
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */protected OvertimehVO createMainVO(OvertimebVO[] vos)
	/*     */{
		/* 590 */OvertimebVO vo = vos[0];
		/* 591 */OvertimehVO headVO = new OvertimehVO();
		/* 592 */headVO.setIshrssbill(UFBoolean.FALSE);
		/* 593 */headVO.setPk_group(vo.getPk_group());
		/* 594 */headVO.setPk_org(vo.getPk_org());
		/* 595 */headVO.setPk_psndoc(vo.getPk_psndoc());
		/* 596 */headVO.setPk_psnjob(vo.getPk_psnjob());
		/* 597 */headVO.setPk_psnorg(vo.getPk_psnorg());
		/* 598 */headVO.setPk_org_v(vo.getPk_org_v());
		/* 599 */headVO.setPk_dept_v(vo.getPk_dept_v());
		/* 600 */headVO.setStatus(2);
		/* 601 */headVO.setApprove_state(Integer.valueOf(-1));
		/* 602 */headVO.setBillmaker(PubEnv.getPk_user());
		/* 603 */headVO.setApply_date(PubEnv.getServerLiteralDate());
		/* 604 */headVO.setFun_code("64050otapply");
		/* 605 */headVO.setTranstypeid(vo.getTranstypeid());
		/* 606 */headVO.setTranstype(vo.getTranstype());
		/* 607 */headVO.setPk_billtype("6405");
		/* 608 */headVO.setPk_overtimetype(vo.getPk_overtimetype());
		/* 609 */headVO.setPk_overtimetypecopy(vo.getPk_overtimetypecopy());
		/*     */
		/* 611 */if (vos.length > 1) {
			/* 612 */UFDouble sumHour = new UFDouble();
			/* 613 */for (OvertimebVO bvo : vos) {
				/* 614 */if (bvo.getOvertimehour() != null)
					/* 615 */sumHour = sumHour.add(bvo.getOvertimehour());
				/*     */}
			/* 617 */headVO.setSumhour(sumHour);
			/*     */}
		/*     */else {
			/* 620 */headVO.setSumhour(vo.getOvertimehour());
			/*     */}
		/*     */
		/* 623 */return headVO;
		/*     */}

	/*     */
	/*     */protected String getBillCodeFieldName()
	/*     */{
		/* 628 */return "bill_code";
		/*     */}

	/*     */
	/*     */protected String getBillType()
	/*     */{
		/* 633 */return "6405";
		/*     */}

	/*     */
	/*     */protected Class<?> getHeadVOClass()
	/*     */{
		/* 638 */return OvertimehVO.class;
		/*     */}

	/*     */
	/*     */protected TaWorkFlowManager<OvertimehVO, OvertimebVO>.UserValueConfig getUserValueConfig()
	/*     */{
		/* 643 */TaWorkFlowManager<OvertimehVO, OvertimebVO>.UserValueConfig config = new TaWorkFlowManager.UserValueConfig();
		/* 644 */config.setBillCodeFieldName("bill_code");
		/* 645 */config.setApproveStateFieldName("approve_state");
		/* 646 */config.setFieldCodes(OvertimeConst.FIELDCODE);
		/* 647 */return config;
		/*     */}

	/*     */
	/*     */protected void syncHeadInfoToBody(AggregatedValueObject[] aggVOs)
	/*     */{
		/* 652 */if (ArrayUtils.isEmpty(aggVOs))
			/* 653 */return;
		/* 654 */for (AggregatedValueObject aggVO : aggVOs) {
			/* 655 */OvertimebVO[] bvos = (OvertimebVO[]) aggVO.getChildrenVO();
			/* 656 */if (!ArrayUtils.isEmpty(bvos))
			/*     */{
				/* 658 */OvertimehVO hvo = (OvertimehVO) aggVO.getParentVO();
				/* 659 */String pk_psndoc = hvo.getPk_psndoc();
				/* 660 */String pk_psnjob = hvo.getPk_psnjob();
				/* 661 */String pk_psnorg = hvo.getPk_psnorg();
				/* 662 */String pk_timeitem = hvo.getPk_timeitem();
				/* 663 */String pk_timeitemcopy = hvo.getPk_overtimetypecopy();
				/* 664 */for (OvertimebVO bvo : bvos) {
					/* 665 */bvo.setPk_psndoc(pk_psndoc);
					/* 666 */bvo.setPk_psnjob(pk_psnjob);
					/* 667 */bvo.setPk_psnorg(pk_psnorg);
					/* 668 */bvo.setPk_timeitem(pk_timeitem);
					/* 669 */bvo.setPk_overtimetypecopy(pk_timeitemcopy);
					/*     */}
				/*     */}
			/*     */}
		/*     */}
}
