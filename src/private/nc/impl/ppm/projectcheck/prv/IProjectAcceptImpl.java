package nc.impl.ppm.projectcheck.prv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.pmpub.rule.AppendBusiTypeBeforeRule;
import nc.bs.pmpub.rule.ChangeHeadVOStatusRule;
import nc.bs.pmpub.rule.RegularCalculateRule;
import nc.bs.pmpub.rule.SetAddStatusBodyOrgRule;
import nc.bs.pmpub.rule.UpdateAuditInfoBeforeRule;
import nc.bs.ppm.projectcheck.rule.ProjectCheckApproveRule;
import nc.bs.ppm.projectcheck.rule.ProjectCheckCheckPassRule;
import nc.bs.ppm.projectcheck.rule.ProjectCheckCheckProRule;
import nc.bs.ppm.projectcheck.rule.ProjectCheckSaveRule;
import nc.bs.ppm.projectcheck.rule.ProjectCheckSubmitRule;
import nc.bs.ppm.projectcheck.rule.ProjectCheckUnApprovePassRule;
import nc.bs.ppm.projectcheck.rule.ProjectCheckUnapproveRule;
import nc.bs.ppm.projectcheck.rule.WriteBackProjectCheckDate;
import nc.bs.trade.business.HYPubBO;
import nc.cmp.utils.BillcodeGenerater;
import nc.impl.pm.billrule.ProjectStatusCheckBeforeRule;
import nc.impl.pm.billrule.SupplyProjectInfoBeforeRule;
import nc.impl.pm.billrule.SupplyStatusByTransiRuleBeforeRule;
import nc.impl.pm.billrule.WriteBackToProjectBeforeRule;
import nc.impl.pmpub.servicebase.BillBaseImpl;
import nc.impl.pmpub.servicebase.action.ApproveAction;
import nc.impl.pmpub.servicebase.action.CommitAction;
import nc.impl.pmpub.servicebase.action.InsertAction;
import nc.impl.pmpub.servicebase.action.UnApproveAction;
import nc.impl.pmpub.servicebase.action.UnCommitAction;
import nc.impl.pmpub.servicebase.action.UpdateAction;
import nc.impl.ppm.projectcheck.rule.CheckCommitRule;
import nc.impl.ppm.projectcheck.rule.CheckUnApproveRule;
import nc.impl.pubapp.pattern.data.vo.VOQuery;
import nc.itf.ppm.projectcheck.prv.IProjectAccept;
import nc.itf.ppm.schedulefill.IShceduleFillInterface;
import nc.itf.ppm.transtype.ITranstypeQuery;
import nc.itf.pubapp.pub.smart.IBillQueryService;
import nc.itf.uap.pf.IplatFormEntry;
import nc.ui.pcm.feebalance.action.TaskProgressDetailVO;
import nc.ui.pcm.feebalance.action.TaskProgressVO;
import nc.ui.pcm.utils.GetDao;
import nc.vo.bd.material.MaterialVO;
import nc.vo.org.OrgVO;
import nc.vo.pim.project.ProjectHeadVO;
import nc.vo.pim.projectproduct.ProjectProductVO;
import nc.vo.pm.transtype.TransTypeExtendVO;
import nc.vo.pmbd.acceptcheck.AcceptCheckVO;
import nc.vo.pmbd.datalist.CheckDataListVO;
import nc.vo.ppm.projectcheck.CheckItemVO;
import nc.vo.ppm.projectcheck.CheckerVO;
import nc.vo.ppm.projectcheck.DataGoalVO;
import nc.vo.ppm.projectcheck.LeaveIssueVO;
import nc.vo.ppm.projectcheck.ProductTransferVO;
import nc.vo.ppm.projectcheck.ProjectCheckBillVO;
import nc.vo.ppm.projectcheck.ProjectCheckHeadVO;
import nc.vo.ppm.schedulefill.TaskScheduleBillVO;
import nc.vo.ppm.schedulefill.TaskScheduleBodyVO;
import nc.vo.ppm.schedulefill.TaskScheduleHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.apache.commons.lang.StringUtils;

// 竣工验收
@SuppressWarnings({ "unchecked", "rawtypes", "restriction" })
public class IProjectAcceptImpl extends BillBaseImpl<ProjectCheckBillVO>
		implements IProjectAccept {
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表名
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);

	public IProjectAcceptImpl() {
	}

	public ProjectCheckBillVO[] insertProAccept(ProjectCheckBillVO[] billVOs)
			throws BusinessException {
		InsertAction<ProjectCheckBillVO> action = createInsertAction();
		initInsertAction(action, billVOs[0].getParentVO());

		return (ProjectCheckBillVO[]) action.processAction(billVOs);
	}

	protected void initInsertAction(InsertAction<ProjectCheckBillVO> action,
			ProjectCheckHeadVO headVO) {
		super.initInsertAction(action);

		TransTypeExtendVO extendVO = null;
		try {
			extendVO = getITranstypeQuery().queryExtVO(headVO.getTransi_type(),
					headVO.getPk_group());
		} catch (BusinessException e) {
			ExceptionUtils.wrappException(e);
		}
		if (extendVO == null) {
			return;
		}

		if (2 == extendVO.getCheck_type().intValue()) {
			action.addBeforeRule(new ProjectCheckCheckPassRule());
		}

		action.addBeforeRule(new ProjectCheckSaveRule());

		action.addBeforeRule(new ProjectCheckCheckProRule());

		action.addBeforeRule(new ProjectStatusCheckBeforeRule());

		action.addBeforeRule(new AppendBusiTypeBeforeRule());

		action.addBeforeRule(new RegularCalculateRule());
	}

	public ProjectCheckBillVO[] updateProAccept(ProjectCheckBillVO[] billVOs,
			ProjectCheckBillVO[] originBillVOs) throws BusinessException {
		return (ProjectCheckBillVO[]) update(billVOs, originBillVOs);
	}

	protected void initUpdateAction(UpdateAction<ProjectCheckBillVO> action) {
		action.addBeforeRule(new ProjectCheckCheckProRule());

		action.addBeforeRule(new UpdateAuditInfoBeforeRule());

		action.addBeforeRule(new SetAddStatusBodyOrgRule());

		action.addBeforeRule(new ChangeHeadVOStatusRule());

		action.addBeforeRule(new AppendBusiTypeBeforeRule());

		action.addBeforeRule(new RegularCalculateRule());
	}

	public ProjectCheckBillVO[] deleteProAccept(ProjectCheckBillVO[] billVOs)
			throws BusinessException {
		return (ProjectCheckBillVO[]) delete(billVOs);
	}

	public Object approveProAccept(ProjectCheckBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		ApproveAction<ProjectCheckBillVO> action = createApproveAction();
		initApproveAction(action, billVOs[0].getParentVO());
		Object app = action.processAction(billVOs, pfParamVO);

		for (ProjectCheckBillVO billVO : billVOs) {
			ProjectCheckHeadVO hvo = billVO.getParentVO();// 表头VO
			String pkOrg = hvo.getPk_org();
			String code = (String) getHyPubBO()
					.findColValue(
							"bd_defdoc",
							"code",
							"nvl(dr,0) = 0 and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where "
									+ "code = 'PUSHJD' and nvl(dr,0) = 0) and name = '"
									+ pkOrg + "'");
			if (code != null) {
				String pk_project = hvo.getPk_project();
				String pk_wbs = getWbsWork(pkOrg, pk_project, "竣工验收");
				if (StringUtils.isNotEmpty(pk_wbs) && !"Y".equals(pk_wbs)) {
					String sql = "UPDATE Pm_Check_Head SET HDEF5 = '" + pk_wbs
							+ "' WHERE pk_pro_check = '" + hvo.getPrimaryKey()
							+ "'";
					getDao.executeUpdate(sql);
					String mes = pushTaskProcessByContract(hvo, pk_wbs);
					if (StringUtils.isNotEmpty(mes)) {
						throw new BusinessException("竣工验收推进度失败：" + mes);
					}
				}
			}
		}
		return app;
	}

	// 根据组织+项目+任务名称查询是否有该任务-----参数：pkOrg==所属组织，pk_project==项目，wbsName==WBS任务名称
	private String getWbsWork(String pkOrg, String pk_project, String wbsName)
			throws BusinessException {
		String pk_wbs = "";
		String wbsWorkSql = "SELECT PK_WBS,ACTPERCENT FROM PM_WBS WHERE DR = 0 AND (ENABLESTATE = 1 OR ENABLESTATE = 2) AND PK_DUTY_ORG = '"
				+ pkOrg
				+ "'"
				+ " AND PK_PROJECT = '"
				+ pk_project
				+ "' AND WBS_NAME = '" + wbsName + "'";
		List<Object[]> wbsWorkLs = getDao.query(wbsWorkSql);
		if (wbsWorkLs != null && wbsWorkLs.size() > 0
				&& wbsWorkLs.get(0)[0] != null) {
			// 如果查出多条只取第一条
			if (Double.parseDouble(wbsWorkLs.get(0)[1] + "") == 100) {
				return "Y";
			}
			pk_wbs = wbsWorkLs.get(0)[0] + "";
		}/* else {
			throw new BusinessException("未获取到项目任务！");
		}*/
		return pk_wbs;
	}

	public String pushTaskProcessByContract(ProjectCheckHeadVO headVO,
			String pk_wbs) throws BusinessException {
		try {
			TaskProgressVO taskProgressVO = new TaskProgressVO();
			taskProgressVO.setBillmaker(headVO.getBillmaker());
			taskProgressVO.setBillmaketime(headVO.getBillmaketime()); // 制单日期
			taskProgressVO.setCreator(headVO.getCreator());
			taskProgressVO.setCreationtime(headVO.getCreationtime());
			taskProgressVO.setMemo(headVO.getMemo()); // 备注
			taskProgressVO.setPk_filldept(headVO.getPk_apply_dept()); // 部门
			taskProgressVO.setPk_filldept_v(headVO.getPk_apply_dept_v()); // 部门多版本
			taskProgressVO.setPk_fillmaker(headVO.getBillmaker()); // 填报人
			taskProgressVO.setPk_group(headVO.getPk_group()); // 集团
			taskProgressVO.setPk_org(headVO.getPk_org()); // 组织
			taskProgressVO.setPk_org_v(headVO.getPk_org_v()); // 组织
			taskProgressVO.setBillPk(headVO.getPrimaryKey());// 单据主键
			taskProgressVO.setPk_project(headVO.getPk_project()); // 项目

			// 项目明细
			TaskProgressDetailVO taskProgressDetailVO = new TaskProgressDetailVO();
			taskProgressDetailVO.setActu_start_date(headVO.getApply_date()); // 实际开始时间
			taskProgressDetailVO.setActu_finish_date(headVO.getCheck_date()); // 实际结束时间

			taskProgressDetailVO.setEvolve_state(""); // 进展说明
			taskProgressDetailVO.setMemo(""); // 备注
			taskProgressDetailVO.setPk_project(headVO.getPk_project()); // 项目
			taskProgressDetailVO.setPk_wbs(pk_wbs); // WBS任务
			// taskProgressDetailVO.setPrefinishpercent(new
			// UFDouble("545.00")); // 上期实际完成
			taskProgressDetailVO.setTtaskpercent(new UFDouble("100")); // 本期时间完成
			taskProgressVO.setTaskProgressDetailVO(taskProgressDetailVO);
			// 执行插入
			insertTaskBillVO(taskProgressVO, null);
		} catch (BusinessException e) {
			return e.getMessage();
		}
		return null;
	}
	public void insertTaskBillVO(TaskProgressVO taskProgressVO,
			List<TaskProgressDetailVO> taskProgressDetailVOArr)
			throws BusinessException {
		IShceduleFillInterface ipf = (IShceduleFillInterface) NCLocator
				.getInstance().lookup(IShceduleFillInterface.class);
		TaskProgressDetailVO taskProgressDetailVO = taskProgressVO
				.getTaskProgressDetailVO();
		if (taskProgressDetailVO.getPk_wbs() == null
				|| "".equals(taskProgressDetailVO.getPk_wbs())
				|| "null".equals(taskProgressDetailVO.getPk_wbs())) {
			return;
		} else {
			// 如果表体为空，所有数据从表头添加
			if (taskProgressDetailVOArr == null) {

				TaskScheduleBillVO taskScheduleBillVO = new TaskScheduleBillVO();
				// ----------------------单据表头VO----------------------
				TaskScheduleHeadVO taskScheduleHeadVO = new TaskScheduleHeadVO();
				// 获取单据编号
				BillcodeGenerater gene = new BillcodeGenerater();
				String billno = gene.getBillCode("4D24",
						taskProgressVO.getPk_group(),
						taskProgressVO.getPk_org(), null, null);
				System.out.println("输出生成的单据编号：" + billno);
				taskScheduleHeadVO.setBill_code(billno);// 单据编号
				// taskScheduleHeadVO.setAuditor(""); // 审批人
				// taskScheduleHeadVO.setAudittime(); // 审批日期
				// taskScheduleHeadVO.setBill_status(-1); // 单据状态 默认提交态
				taskScheduleHeadVO.setBill_type("4D24"); // bill_type
				taskScheduleHeadVO.setBillmaker(taskProgressVO.getBillmaker()); // 制单人
				taskScheduleHeadVO.setBillmaketime(taskProgressVO
						.getBillmaketime()); // 制单日期
				taskScheduleHeadVO.setCreator(taskProgressVO.getCreator() + ""); // 创建人
				taskScheduleHeadVO.setCreationtime(taskProgressVO
						.getCreationtime()); // 创建日期
				taskScheduleHeadVO
						.setFilltime(taskProgressVO.getBillmaketime()); // 填报日期，默认制单日期
				taskScheduleHeadVO.setMemo(taskProgressVO.getMemo()); // 备注
				taskScheduleHeadVO.setPk_filldept(taskProgressVO
						.getPk_filldept()); // 经办部门
				taskScheduleHeadVO.setPk_filldept_v(taskProgressVO
						.getPk_filldept_v()); // 经办部门多版本
				taskScheduleHeadVO.setPk_fillmaker(taskProgressVO
						.getPk_fillmaker()); // 填报人
				taskScheduleHeadVO.setPk_group(taskProgressVO.getPk_group()); // 集团
				taskScheduleHeadVO.setPk_org(taskProgressVO.getPk_org()); // 组织
				taskScheduleHeadVO.setPk_org_v(taskProgressVO.getPk_org_v()); // 组织
				taskScheduleHeadVO.setPk_project(taskProgressDetailVO
						.getPk_project()); // 项目pkOID取表体里面的object
				taskScheduleHeadVO.setPk_transitype("0001A21000000000PG0E"); // 交易类型
				taskScheduleHeadVO.setTransi_type("4D24-01"); // 交易类型编码
																// transi_type
				taskScheduleHeadVO.setHdef10(taskProgressVO.getBillPk());// 自定义项10存放单据pk
				taskScheduleHeadVO.setDr(0);
				taskScheduleHeadVO.setReport_fintsk_flag(new UFBoolean("N"));
				taskScheduleHeadVO.setReport_kms_flag(new UFBoolean("N"));
				// taskScheduleHeadVO.setAuditor(taskProgressVO.getBillmaker());
				// // 审批人
				// taskScheduleHeadVO.setAudittime(taskProgressVO.getBillmaketime());
				// // 审批日期
				taskScheduleHeadVO.setBill_status(3); // 单据状态 默认审批态
				// 写入表头
				taskScheduleBillVO.setParent(taskScheduleHeadVO);

				// ----------------------单据表体VO----------------------

				TaskScheduleBodyVO taskScheduleBodyVO = new TaskScheduleBodyVO();
				taskScheduleBodyVO.setPk_wbsstate(1); // WBS任务状态 进行中
				// 添加子表字段
				taskScheduleBodyVO.setActu_start_date(taskProgressDetailVO
						.getActu_start_date()); // 实际开始时间
				System.out.println("taskProgressDetailVO.getTtaskpercent()="
						+ taskProgressDetailVO.getTtaskpercent());
				if (Double.parseDouble(taskProgressDetailVO.getTtaskpercent()
						.toString()) == 100) {
					taskScheduleBodyVO.setActu_finish_date(taskProgressDetailVO
							.getActu_finish_date()); // 时间结束时间
					taskScheduleBodyVO.setPk_wbsstate(4); // WBS任务状态 完成
				} else {
					taskScheduleBodyVO.setPk_wbsstate(1); // WBS任务状态 进行中
				}

				taskScheduleBodyVO.setDr(0);
				taskScheduleBodyVO.setEvolve_state(taskProgressDetailVO
						.getEvolve_state()); // 进展说明
				taskScheduleBodyVO.setMemo(taskProgressDetailVO.getMemo()); // 备注
				taskScheduleBodyVO.setPk_project(taskProgressDetailVO
						.getPk_project()); // 项目
				taskScheduleBodyVO.setPk_wbs(taskProgressDetailVO.getPk_wbs()); // WBS任务
				String totalMax = getMaxFinish(taskProgressVO.getPk_org(),
						taskProgressDetailVO.getPk_project(),
						taskProgressDetailVO.getPk_wbs(),
						taskScheduleBodyVO.getPk_taskschedule_b());// 最大完成
				if (!"".equals(totalMax)) {
					taskScheduleBodyVO.setPrefinishpercent(new UFDouble(
							totalMax)); // 上期实际完成
					if (Double.parseDouble(totalMax) == 100) {
						taskScheduleBodyVO.setPre_wbsstate(4);
					} else {
						taskScheduleBodyVO.setPre_wbsstate(1);
					}
				}

				taskScheduleBodyVO.setTaskpercent(taskProgressDetailVO
						.getTtaskpercent()); // 本期实际完成

				// ----------------------调用接口写入----------------------
				CircularlyAccessibleValueObject[] vos = new TaskScheduleBodyVO[] { taskScheduleBodyVO };
				taskScheduleBillVO.setChildrenVO(vos);
				// pk = taskScheduleHeadVO.getPrimaryKey();
				TaskScheduleBillVO[] taskScheduleBillVOArr = new TaskScheduleBillVO[] { taskScheduleBillVO };
				ipf.insertTaskScheduleBillVOS(taskScheduleBillVOArr);
				/*String pks = Schepk(taskProgressVO.getBillPk(),
						taskProgressVO.getPk_project(),
						taskProgressDetailVO.getPk_wbs());*/
				String pks = taskScheduleBillVOArr[0].getPrimaryKey();
				IBillQueryService billQuery = NCLocator.getInstance().lookup(
						IBillQueryService.class);
				TaskScheduleBillVO ystzvo = billQuery.querySingleBillByPk(
						TaskScheduleBillVO.class, pks);
				if (ystzvo != null) {
					IplatFormEntry ip = (IplatFormEntry) NCLocator.getInstance().lookup(
							IplatFormEntry.class);// 动作执行类
					ip.processAction("APPROVE", "4D24", null, ystzvo, null,
							null);
				}
				/*
				 * IQueryScheme paramIQueryScheme = null;
				 * paramIQueryScheme.put("pk_taskschedule", pk);
				 * sd.queryByQueryScheme(paramIQueryScheme);
				 */

			} else {
				for (int i = 0; i < taskProgressDetailVOArr.size(); i++) {
					// 去掉重复的项目 判断条件 组织+项目OID 累积完成 修改为100%
					removeDuplicate(taskProgressDetailVOArr);

					// ----------------------循环添加进度填报单表头、表体信息----------------------
					// TaskProgressDetailVO taskProgressDetailVO =
					// taskProgressDetailVOArr.get(i);
					// ----------------------单据VO----------------------
					TaskScheduleBillVO taskScheduleBillVO = new TaskScheduleBillVO();
					// ----------------------单据表头VO----------------------
					TaskScheduleHeadVO taskScheduleHeadVO = new TaskScheduleHeadVO();

					// 获取单据编号
					BillcodeGenerater gene = new BillcodeGenerater();
					String billno = gene.getBillCode("4D24",
							taskProgressVO.getPk_group(),
							taskProgressVO.getPk_org(), null, null);
					taskScheduleHeadVO.setBill_code(billno);// 单据编号

					// taskScheduleHeadVO.setAuditor(""); // 审批人
					// taskScheduleHeadVO.setAudittime(); // 审批日期
					// taskScheduleHeadVO.setBill_status(-1); // 单据状态 默认提交态
					taskScheduleHeadVO.setBill_type("4D24"); // bill_type
					taskScheduleHeadVO.setBillmaker(taskProgressVO
							.getBillmaker()); // 制单人
					taskScheduleHeadVO.setBillmaketime(taskProgressVO
							.getBillmaketime()); // 制单日期
					taskScheduleHeadVO.setCreator(taskProgressVO.getCreator()
							+ ""); // 创建人
					taskScheduleHeadVO.setCreationtime(taskProgressVO
							.getCreationtime()); // 创建日期
					taskScheduleHeadVO.setFilltime(taskProgressVO
							.getBillmaketime()); // 填报日期，默认制单日期
					taskScheduleHeadVO.setMemo(taskProgressVO.getMemo()); // 备注
					taskScheduleHeadVO.setPk_filldept(taskProgressVO
							.getPk_filldept()); // 经办部门
					taskScheduleHeadVO.setPk_filldept_v(taskProgressVO
							.getPk_filldept_v()); // 经办部门多版本
					taskScheduleHeadVO.setPk_fillmaker(taskProgressVO
							.getPk_fillmaker()); // 填报人
					taskScheduleHeadVO
							.setPk_group(taskProgressVO.getPk_group()); // 集团
					taskScheduleHeadVO.setPk_org(taskProgressVO.getPk_org()); // 组织
					taskScheduleHeadVO
							.setPk_org_v(taskProgressVO.getPk_org_v()); // 组织
					taskScheduleHeadVO.setPk_project(taskProgressDetailVO
							.getPk_project()); // 项目pkOID取表体里面的object
					taskScheduleHeadVO.setPk_transitype("0001A21000000000PG0E"); // 交易类型
					taskScheduleHeadVO.setTransi_type("4D24-01"); // 交易类型编码
																	// transi_type
					taskScheduleHeadVO.setHdef10(taskProgressVO.getBillPk());// 自定义项10存放单据pk
					taskScheduleHeadVO.setDr(0);
					taskScheduleHeadVO
							.setAuditor(taskProgressVO.getBillmaker()); // 审批人
					taskScheduleHeadVO.setAudittime(taskProgressVO
							.getBillmaketime()); // 审批日期
					taskScheduleHeadVO.setBill_status(1); // 单据状态 默认审批态
					// 写入表头
					taskScheduleBillVO.setParent(taskScheduleHeadVO);

					// ----------------------单据表体VO----------------------
					TaskScheduleBodyVO taskScheduleBodyVO = new TaskScheduleBodyVO();
					// 添加子表字段
					taskScheduleBodyVO.setActu_start_date(taskProgressDetailVO
							.getActu_start_date()); // 实际开始时间
					if ((new UFDouble("100")) == taskProgressDetailVO
							.getTtaskpercent()) {
						taskScheduleBodyVO
								.setActu_finish_date(taskProgressDetailVO
										.getActu_finish_date()); // 时间结束时间
						taskScheduleBodyVO.setPk_wbsstate(4); // WBS任务状态 进行中
					}
					// taskScheduleBodyVO.setActu_finish_date(taskProgressDetailVO.getActu_finish_date());
					// // 时间结束时间
					taskScheduleBodyVO.setDr(0);
					taskScheduleBodyVO.setEvolve_state(""); // 进展说明
					taskScheduleBodyVO.setMemo(taskProgressDetailVO.getMemo()); // 备注
					taskScheduleBodyVO.setPk_project(taskProgressDetailVO
							.getPk_project()); // 项目
					taskScheduleBodyVO.setPk_wbs(taskProgressDetailVO
							.getPk_wbs()); // WBS任务
					taskScheduleBodyVO.setPk_wbsstate(0); // WBS任务状态
					taskScheduleBodyVO.setPrefinishpercent(taskProgressDetailVO
							.getPrefinishpercent()); // 上期实际完成
					taskScheduleBodyVO.setTaskpercent(taskProgressDetailVO
							.getTtaskpercent()); // 本期时间完成
					// ----------------------调用接口写入----------------------

					CircularlyAccessibleValueObject[] vos = new TaskScheduleBodyVO[] { taskScheduleBodyVO };
					taskScheduleBillVO.setChildrenVO(vos);

					TaskScheduleBillVO[] taskScheduleBillVOArr = new TaskScheduleBillVO[] { taskScheduleBillVO };
					ipf.insertTaskScheduleBillVOS(taskScheduleBillVOArr);

				}
			}
		}
	}
	private String getMaxFinish(String pk_org, String pk_project, String pk_wbs,
			String PK_TASKSCHEDULE_B) {
		String maxTotal = "";
		String sql = "SELECT MAX(JDMX.TASKPERCENT) AS MAXTOTAL FROM PM_TASKSCHEDULE_B JDMX LEFT JOIN PM_TASKSCHEDULE JD ON "
				+ "JDMX.PK_TASKSCHEDULE = JD.PK_TASKSCHEDULE WHERE JD.DR = 0 AND JDMX.DR = 0 AND JD.PK_ORG = '"
				+ pk_org
				+ "' AND JDMX.PK_PROJECT = '"
				+ pk_project
				+ "' AND "
				+ "JDMX.PK_WBS = '"
				+ pk_wbs
				+ "' AND PK_TASKSCHEDULE_B <> '"
				+ PK_TASKSCHEDULE_B + "'";
		System.out.println("sql==" + sql);
		try {
			List<Object[]> maxls = getDao.query(sql);

			if (maxls != null && maxls.size() > 0 && maxls.get(0)[0] != null) {
				maxTotal = maxls.get(0)[0] + "";
				System.out.println("maxTotal--" + maxTotal);
			}
		} catch (DAOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return maxTotal;
	}
	// 去除重复项目
	private List<TaskProgressDetailVO> removeDuplicate(
			List<TaskProgressDetailVO> oldList) {
		for (int i = 0; i < oldList.size() - 1; i++) {
			TaskProgressDetailVO taskProgressDetailVOFirst = oldList.get(i);
			String fristProject = taskProgressDetailVOFirst.getPk_project();
			for (int j = oldList.size() - 1; j > i; j--) {
				TaskProgressDetailVO taskProgressDetailVOSecond = oldList
						.get(j);
				String secondProject = taskProgressDetailVOSecond
						.getPk_project();
				// if (oldList.get(j).equals(oldList.get(i))) {
				if (fristProject.equals(secondProject)) {
					oldList.remove(j);
				}
			}
		}
		return oldList;
	}

	private ITranstypeQuery getITranstypeQuery() {
		return (ITranstypeQuery) NCLocator.getInstance().lookup(
				ITranstypeQuery.class);
	}

	protected void initApproveAction(ApproveAction<ProjectCheckBillVO> action,
			ProjectCheckHeadVO headVO) {
		super.initApproveAction(action);

		TransTypeExtendVO extendVO = null;
		try {
			extendVO = getITranstypeQuery().queryExtVO(headVO.getTransi_type(),
					headVO.getPk_group());
		} catch (BusinessException e) {
			ExceptionUtils.wrappException(e);
		}
		if (extendVO == null) {
			return;
		}

		action.addAfterRule(new ProjectCheckApproveRule());

		if (((2 == extendVO.getCheck_type().intValue()) && (headVO
				.getCheckpass().booleanValue()))
				|| (2 != extendVO.getCheck_type().intValue())) {
			action.addAfterRule(new SupplyProjectInfoBeforeRule());
			action.addAfterRule(new SupplyStatusByTransiRuleBeforeRule());
			action.addAfterRule(new WriteBackToProjectBeforeRule(true));

			action.addAfterRule(new WriteBackProjectCheckDate(true));
		}
	}

	public ProjectCheckBillVO[] unapproveProAccept(
			ProjectCheckBillVO[] billVOs, PfParameterVO pfParamVO)
			throws BusinessException {
		UnApproveAction<ProjectCheckBillVO> action = createUnApproveAction();
		initUnApproveAction(action, billVOs[0].getParentVO());
		return (ProjectCheckBillVO[]) action.processAction(billVOs, pfParamVO);
	}

	protected void initUnApproveAction(
			UnApproveAction<ProjectCheckBillVO> action,
			ProjectCheckHeadVO headVO) {
		super.initUnApproveAction(action);
		action.addBeforeRule(new ProjectCheckUnApprovePassRule());
		action.addBeforeRule(new CheckUnApproveRule());
		action.addAfterRule(new ProjectCheckUnapproveRule());

		action.addAfterRule(new WriteBackToProjectBeforeRule(false));

		action.addAfterRule(new WriteBackProjectCheckDate(false));
	}

	public ProjectCheckBillVO[] commitProAccept(ProjectCheckBillVO[] billVOs)
			throws BusinessException {
		for (ProjectCheckBillVO temp : billVOs) {
			if ((temp.getParentVO().getTransi_type()).contains("4D36")) {
				ProjectCheckHeadVO hVO = temp.getParentVO();
				if ("4".equals(getDef2(hVO.getPk_org()))) {
					OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(
							hVO.getPk_org(), "4D36");
					if (oaVo != null && oaVo.getIsdr() == 0) {
						WorkFId = oaVo.getFlowid();
						TableName = oaVo.getTablename();
						// 获取主表数据
						JSONArray headData = getNewMainMap(hVO);
						// 获取子表数据
						JSONArray bodyData = getNewDtaileDataMap(temp);
						// 构造workflow信息
						OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
						workFlowVO.setPrimaryKey(temp.getPrimaryKey());
						workFlowVO.setPkGroup(hVO.getPk_group());
						workFlowVO.setPkOrg(hVO.getPk_org());
						workFlowVO.setBillMaker(hVO.getBillmaker());
						workFlowVO.setCreator(hVO.getCreator());
						workFlowVO.setBillCode("4D36");// 单据类型
						// 接口获取
						workFlowVO.setWorkflowId(OaWorkFlowUtil
								.getOAFlowID(WorkFId));
						workFlowVO.setWorkflowName("竣工验收");// 单据名称
						WorkFlowBill bill = OaWorkFlowUtil
								.getWorkFlowBill(workFlowVO);
						bill.setDef3("ZT");
						bill.setDef4(hVO.getBill_code());
						// 制单人身份证号
						UserVO userVO = (UserVO) getHyPubBO()
								.queryByPrimaryKey(UserVO.class,
										hVO.getBillmaker());
						String idCard = (String) getHyPubBO().findColValue(
								"bd_psndoc",
								"id",
								"nvl(dr,0) = 0 and pk_psndoc='"
										+ userVO.getPk_psndoc() + "'");
						bill.setDef5(idCard);
						// 调用OA工具类同步数据至OA
						OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
					}
				} else if ("1".equals(getDef2(hVO.getPk_org()))
						|| "2".equals(getDef2(hVO.getPk_org()))) {
					// String realUrl = "http://172.18.128.41:8082";
					OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(
							hVO.getPk_org(), "4D36");
					if (oaVo != null && oaVo.getIsdr() == 0) {
						WorkFId = oaVo.getFlowid();
						TableName = oaVo.getTablename();
						// 获取主表数据
						JSONArray headData = getNewMainMap(hVO);
						// 获取子表数据
						JSONArray bodyData = getNewDtaileDataMap(temp);
						// 构造workflow信息
						OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
						workFlowVO.setPrimaryKey(temp.getPrimaryKey());
						workFlowVO.setPkGroup(hVO.getPk_group());
						workFlowVO.setPkOrg(hVO.getPk_org());
						workFlowVO.setBillMaker(hVO.getBillmaker());
						workFlowVO.setCreator(hVO.getCreator());
						workFlowVO.setBillCode("4D36");// 单据类型
						// 接口获取
						workFlowVO.setWorkflowId(OaWorkFlowUtil
								.getOAFlowID(WorkFId));
						workFlowVO.setWorkflowName("竣工验收");// 单据名称
						WorkFlowBill bill = OaWorkFlowUtil
								.getWorkFlowBill(workFlowVO);
						bill.setDef3("ZT");
						bill.setDef4(hVO.getBill_code());
						// 制单人身份证号
						UserVO userVO = (UserVO) getHyPubBO()
								.queryByPrimaryKey(UserVO.class,
										hVO.getBillmaker());
						String idCard = (String) getHyPubBO().findColValue(
								"bd_psndoc",
								"id",
								"nvl(dr,0) = 0 and pk_psndoc='"
										+ userVO.getPk_psndoc() + "'");
						bill.setDef5(idCard);
						// 调用OA工具类同步数据至OA
						OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
					} else {
						JSONArray headData = null;
						JSONArray bodyData = null;
						headData = getMainMap(temp.getParentVO());
						bodyData = getDtaileDataMap(temp);

						OaWorkFlowUtil.sendOaData(headData, bodyData,
								getWorkFlowBill(temp));
					}
				}
			}
		}
		CommitAction<ProjectCheckBillVO> action = createCommitAction();
		initCommitAction(action, billVOs[0].getParentVO());

		return (ProjectCheckBillVO[]) action.processAction(billVOs);
	}

	private JSONArray getNewDtaileDataMap(ProjectCheckBillVO temp)
			throws BusinessException {
		// 获取详细信息
		List dtlist = new ArrayList();
		ProductTransferVO[] bvos1 = (ProductTransferVO[]) temp
				.getChildren(ProductTransferVO.class);
		Map bodyMap1 = getBody1(bvos1);
		dtlist.add(bodyMap1);

		CheckItemVO[] bvos2 = (CheckItemVO[]) temp
				.getChildren(CheckItemVO.class);
		Map bodyMap2 = getBody2(bvos2);
		dtlist.add(bodyMap2);

		CheckerVO[] bvos3 = (CheckerVO[]) temp.getChildren(CheckerVO.class);
		Map bodyMap3 = getBody3(bvos3);
		dtlist.add(bodyMap3);

		LeaveIssueVO[] bvos4 = (LeaveIssueVO[]) temp
				.getChildren(LeaveIssueVO.class);
		Map bodyMap4 = getBody4(bvos4);
		dtlist.add(bodyMap4);

		DataGoalVO[] bvos5 = (DataGoalVO[]) temp.getChildren(DataGoalVO.class);
		Map bodyMap5 = getBody5(bvos5);
		dtlist.add(bodyMap5);

		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(dtlist, jsonConfig);
		return dtlistString;
	}

	// 项目产出物
	private Map getBody1(ProductTransferVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (ProductTransferVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			// 产出物
			if (temp.getPk_transfer() != null) {
				ProjectProductVO ccwVO = (ProjectProductVO) getHyPubBO()
						.queryByPrimaryKey(ProjectProductVO.class,
								temp.getPk_transfer());
				// 产出物编码
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"ccwbm", ccwVO.getBill_code()));
				// 产出物名称
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"ccwmc", ccwVO.getProduct_name()));
				// 产出物类型
				String ccwlx = (String) getHyPubBO().findColValue(
						"pm_outcometype",
						"type_name",
						"nvl(dr,0) = 0 and pk_outcometype = '"
								+ ccwVO.getPk_producttype() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"ccwlx", ccwlx));
				// 产出物描述
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"ccwms", ccwVO.getProduct_desc()));
				// 物料
				if (ccwVO.getPk_materiel() != null) {
					MaterialVO materialVO = (MaterialVO) getHyPubBO()
							.queryByPrimaryKey(MaterialVO.class,
									ccwVO.getPk_materiel());
					// 物料编码
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"material_code", materialVO.getCode()));
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"wlbm", materialVO.getCode()));
					// 物料名称
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"material_name", materialVO.getName()));
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"wlmc", materialVO.getName()));
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"wl", materialVO.getName()));
					// 规格
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"materialspec", materialVO.getMaterialspec()));
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"gg", materialVO.getMaterialspec()));
					// 型号
					if (null != materialVO.getMaterialtype()) {
						String xh = materialVO.getMaterialtype();
						workflowRequestTableFields.add(OaWorkFlowUtil
								.listAddObj("materialtype", xh));
						workflowRequestTableFields.add(OaWorkFlowUtil
								.listAddObj("xh", xh));
					}
				}
				// 计量单位
				if (null != ccwVO.getPk_measure()) {
					String dwstr = (String) getHyPubBO().findColValue(
							"bd_measdoc",
							"name",
							"nvl(dr,0) = 0 and pk_measdoc  = '"
									+ ccwVO.getPk_measure() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"dw", dwstr));
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"jldw", dwstr));
				}
				// 数量
				if (null != ccwVO.getNnum()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"sl", ccwVO.getNnum() + ""));
				}
				// 供应商
				if (ccwVO.getPk_supplier() != null) {
					String pk_supplier_name = (String) getHyPubBO()
							.findColValue(
									"bd_supplier",
									"name",
									"nvl(dr,0) = 0 and pk_supplier = '"
											+ ccwVO.getPk_supplier() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"pk_supplier_name", pk_supplier_name));
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"pk_sullier_name", pk_supplier_name));
				}
			}
			// 其他字段------end
			Map workflowRequestTableFieldsMap = new HashMap();
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	// 验收检查项
	private Map getBody2(CheckItemVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt2");
		List workflowRequestTableRecords = new ArrayList();
		for (CheckItemVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			// 检查项
			if (temp.getPk_acceptcheck() != null) {
				AcceptCheckVO jcxVO = (AcceptCheckVO) getHyPubBO()
						.queryByPrimaryKey(AcceptCheckVO.class,
								temp.getPk_acceptcheck());
				// 检查项编码
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"jcxbm", jcxVO.getAcce_check_code()));
				// 检查项名称
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"jcxmc", jcxVO.getAcce_check_name()));
				// 检查项描述
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"jcxms", jcxVO.getCheckdescription()));
			}
			// 检查要求
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("jcyq",
					temp.getCheck_requ()));
			// 检查方式
			if (temp.getCheck_mode() != null) {
				// 1=现场检查，2=检验，3=实验，
				String jcfsstr = temp.getCheck_mode();
				String jcfs = "";
				if ("1".equals(jcfsstr)) {
					jcfs = "现场检查";
				} else if ("2".equals(jcfsstr)) {
					jcfs = "检验";
				} else if ("3".equals(jcfsstr)) {
					jcfs = "实验验";
				}
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"jcfs", jcfs));
			}
			// 重要性
			if (temp.getCheck_impt() != null) {
				// 1=重要，2=一般，
				int zyxint = temp.getCheck_impt();
				String zyx = "";
				if (zyxint == 1) {
					zyx = "重要";
				}
				if (zyxint == 2) {
					zyx = "一般";
				}
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("zyx",
						zyx));
			}
			// 检查
			if (temp.getCheck_flag().booleanValue()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("jc",
						"是"));
			} else {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("jc",
						"否"));
			}
			// 检查结果
			if (temp.getCheck_result() != null) {
				// 1=合格，2=不合格，
				int jcjgint = temp.getCheck_result();
				String jcjg = "";
				if (jcjgint == 1) {
					jcjg = "合格";
				}
				if (jcjgint == 2) {
					jcjg = "不合格";
				}
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"check_result", jcjg));
			}
			// 其他字段------end
			Map workflowRequestTableFieldsMap = new HashMap();
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	// 验收人
	private Map getBody3(CheckerVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt3");
		List workflowRequestTableRecords = new ArrayList();
		for (CheckerVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			// 验收人
			if (temp.getPk_checker_code() != null) {
				String ysrbm = (String) getHyPubBO().findColValue(
						"bd_psndoc",
						"code",
						"nvl(dr,0) = 0 and pk_psndoc='"
								+ temp.getPk_checker_code() + "'");
				// 验收人编码
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"ysrbm", ysrbm));
			}
			// 其他字段------end
			Map workflowRequestTableFieldsMap = new HashMap();
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	// 问题整改情况
	private Map getBody4(LeaveIssueVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt4");
		List workflowRequestTableRecords = new ArrayList();
		for (LeaveIssueVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			// 责任人
			if (temp.getPk_prover() != null) {
				String zrr = (String) getHyPubBO().findColValue(
						"bd_psndoc",
						"name",
						"nvl(dr,0) = 0 and pk_psndoc='" + temp.getPk_prover()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("zrr",
						zrr));
			}
			// 供应商
			if (temp.getPk_supplier() != null) {
				String pk_supplier_name = (String) getHyPubBO().findColValue(
						"bd_supplier",
						"name",
						"nvl(dr,0) = 0 and pk_supplier  ='"
								+ temp.getPk_supplier() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"pk_supplier_name", pk_supplier_name));
			}
			// 问题状态
			if (temp.getProblem_status() != null) {
				// 1=未解决，2=已解决，
				String wtztint = temp.getProblem_status();
				String wtzt = "";
				if ("1".equals(wtztint)) {
					wtzt = "未解决";
				}
				if ("2".equals(wtztint)) {
					wtzt = "已解决";
				}
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"problem_status", wtzt));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"wtzt", wtzt));
			}
			// 其他字段------end
			Map workflowRequestTableFieldsMap = new HashMap();
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	// 验收资料清单
	private Map getBody5(DataGoalVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt5");
		List workflowRequestTableRecords = new ArrayList();
		for (DataGoalVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			// 验收资料
			if (temp.getPk_checkdatalist() != null) {
				CheckDataListVO yszlVO = (CheckDataListVO) getHyPubBO()
						.queryByPrimaryKey(CheckDataListVO.class,
								temp.getPk_checkdatalist());
				// 验收资料编码
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"yszlbm", yszlVO.getData_list_code()));
				// 资料名称
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"zlmc", yszlVO.getData_list_name()));
			}
			// 资料保存形式
			if (temp.getSave_medium() != null) {
				// 0=电子，1=纸质，2=电子纸质，
				int zlbcxsint = temp.getSave_medium();
				String zlbcxs = "";
				if (zlbcxsint == 0) {
					zlbcxs = "电子";
				} else if (zlbcxsint == 1) {
					zlbcxs = "纸质";
				} else if (zlbcxsint == 2) {
					zlbcxs = "电子纸质";
				}
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"save_medium", zlbcxs));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"zlbcxss", zlbcxs));
			}
			// 保管人
			if (temp.getPk_keeper() != null) {
				String bgr = (String) getHyPubBO().findColValue(
						"bd_psndoc",
						"name",
						"nvl(dr,0) = 0 and pk_psndoc='" + temp.getPk_keeper()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bgr",
						bgr));
			}
			// 其他字段------end
			Map workflowRequestTableFieldsMap = new HashMap();
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	// 获取中台主表数据
	private JSONArray getNewMainMap(ProjectCheckHeadVO parentVO)
			throws BusinessException {
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// 组织
		String stockName = (String) getHyPubBO().findColValue("org_orgs",
				"name",
				"nvl(dr,0) = 0 and pk_org = '" + parentVO.getPk_org() + "'");
		list.add(OaWorkFlowUtil.listAddObj("zzmc", stockName));
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		// 制单人用户编码
		list.add(OaWorkFlowUtil.listAddObj("usercode", userVO.getUser_code()));
		// 制单人名称
		list.add(OaWorkFlowUtil.listAddObj("username", userVO.getUser_name()));
		// 制单人身份证号
		String idCard = (String) getHyPubBO().findColValue("bd_psndoc", "id",
				"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
		if (null == idCard) {
			throw new BusinessException("制单人身份证号码未维护");
		}
		list.add(OaWorkFlowUtil.listAddObj("userid", idCard));
		OrgVO yhorgvo = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				userVO.getPk_org());
		list.add(OaWorkFlowUtil.listAddObj("yhsszzzj", userVO.getPk_org()));
		list.add(OaWorkFlowUtil.listAddObj("yhsszzbm", yhorgvo.getCode()));
		list.add(OaWorkFlowUtil.listAddObj("yhsszzmc", yhorgvo.getName()));
		// 单据状态
		String djztstr = parentVO.getBill_status() + "";
		int djztint = Integer.parseInt(djztstr);
		String djzt = "";
		if (djztint == -1) {
			djzt = "自由态";
		} else if (djztint == 0) {
			djzt = "审批未通过";
		} else if (djztint == 1) {
			djzt = "审批通过";
		} else if (djztint == 2) {
			djzt = "审批中";
		} else if (djztint == 3) {
			djzt = "已提交";
		}
		list.add(OaWorkFlowUtil.listAddObj("djzt", djzt));
		if (null != parentVO.getPk_project()) {
			// 项目
			ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
					.queryByPrimaryKey(ProjectHeadVO.class,
							parentVO.getPk_project());
			// 项目编码
			list.add(OaWorkFlowUtil.listAddObj("project_code",
					projectVO.getProject_code()));
			list.add(OaWorkFlowUtil.listAddObj("xmbm",
					projectVO.getProject_code()));
			// 项目名称
			list.add(OaWorkFlowUtil.listAddObj("project_name",
					projectVO.getProject_name()));
			list.add(OaWorkFlowUtil.listAddObj("xmmc",
					projectVO.getProject_name()));
			// 项目类型名称
			String xmlx = (String) getHyPubBO().findColValue(
					"bd_projectclass",
					"type_name",
					"nvl(dr,0) = 0 and pk_projectclass  = '"
							+ projectVO.getPk_projectclass() + "'");
			list.add(OaWorkFlowUtil.listAddObj("xmlx", xmlx));
		}
		// 验收标准
		if (parentVO.getPk_checkrequest() != null) {
			String ysbz = (String) getHyPubBO().findColValue(
					"pm_checkrequest",
					"check_stand_name",
					"nvl(dr,0) = 0 and pk_checkrequest = '"
							+ parentVO.getPk_checkrequest() + "'");
			list.add(OaWorkFlowUtil.listAddObj("ysbz", ysbz));
		}
		// 施工单位
		if (parentVO.getHdef1() != null) {
			String sgdw = (String) getHyPubBO().findColValue(
					"bd_supplier",
					"name",
					"nvl(dr,0) = 0 and pk_supplier = '" + parentVO.getHdef1()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sgdw", sgdw));
		}
		// 监理单位
		if (parentVO.getHdef2() != null) {
			String jldw = (String) getHyPubBO().findColValue(
					"bd_supplier",
					"name",
					"nvl(dr,0) = 0 and pk_supplier = '" + parentVO.getHdef2()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("jldw", jldw));
		}
		// 申请部门
		String sqbm = (String) getHyPubBO().findColValue(
				"org_dept_v",
				"name",
				"nvl(dr,0) = 0 and pk_vid = '" + parentVO.getPk_apply_dept_v()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("sqbm", sqbm));
		// 申请人
		String sqr = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc = '"
						+ parentVO.getPk_apply_person() + "'");
		list.add(OaWorkFlowUtil.listAddObj("sqr", sqr));
		// 管线测绘
		if (parentVO.getHdef4() != null) {
			String gxhc = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and   pk_defdoc  ='" + parentVO.getHdef4()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("gxhc", gxhc));
		}
		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	private JSONArray getDtaileDataMap(ProjectCheckBillVO billVO)
			throws BusinessException {
		Map map = getProjCheckBillData((ProductTransferVO[]) billVO
				.getChildren(ProductTransferVO.class));
		JSONArray dtlistString = JSONArray.fromObject(map);
		return dtlistString;
	}

	private Map getProjCheckBillData(ProductTransferVO[] billVO)
			throws BusinessException {
		if (null == billVO || billVO.length == 0) {
			return null;
		}
		Map dtMap = new HashMap();
		dtMap.put("tableDBName", "formtable_main_48_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (ProductTransferVO temp : billVO) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();

			ProjectProductVO projectProductVO = (ProjectProductVO) getHyPubBO()
					.queryByPrimaryKey(ProjectProductVO.class,
							temp.getPk_transfer());

			if (null != projectProductVO) {
				Map ccwzj = OaWorkFlowUtil.listAdd("ccwzj",
						temp.getPk_transfer());
				workflowRequestTableFields.add(ccwzj);

				String dcode = (String) getHyPubBO().findColValue(
						"pm_projectproduct",
						"bill_code",
						"nvl(dr,0) = 0 and   pk_projectproduct  ='"
								+ temp.getPk_transfer() + "'");
				Map ccwbm = OaWorkFlowUtil.listAdd("ccwbm", dcode);
				workflowRequestTableFields.add(ccwbm);

				String dname = (String) getHyPubBO().findColValue(
						"pm_projectproduct",
						"product_name",
						"nvl(dr,0) = 0 and   pk_projectproduct  ='"
								+ temp.getPk_transfer() + "'");
				Map ccwmc = OaWorkFlowUtil.listAdd("ccwmc", dname);
				workflowRequestTableFields.add(ccwmc);

				String type_name = (String) getHyPubBO().findColValue(
						"pm_outcometype",
						"type_name",
						"nvl(dr,0) = 0 and   pk_outcometype  ='"
								+ projectProductVO.getPk_producttype() + "'");
				Map ccwlxmc = OaWorkFlowUtil.listAdd("ccwlxmc", type_name);
				workflowRequestTableFields.add(ccwlxmc);

				String str6 = "0";
				if (null != projectProductVO.getProduct_desc()) {
					str6 = projectProductVO.getProduct_desc();
				}
				Map ccwms = OaWorkFlowUtil.listAdd("ccwms", str6);
				workflowRequestTableFields.add(ccwms);

				String str = "0";
				if (null != projectProductVO.getPk_materiel()) {
					str = projectProductVO.getPk_materiel();
				}
				Map wlzj = OaWorkFlowUtil.listAdd("wlzj", str);
				workflowRequestTableFields.add(wlzj);

				if (null != projectProductVO.getPk_materiel()) {
					MaterialVO materialVO = (MaterialVO) getHyPubBO()
							.queryByPrimaryKey(MaterialVO.class,
									projectProductVO.getPk_materiel());
					if (null != materialVO) {

						Map wlbm = OaWorkFlowUtil.listAdd("wlbm",
								materialVO.getCode());
						workflowRequestTableFields.add(wlbm);

						Map wlmc = OaWorkFlowUtil.listAdd("wlmc",
								materialVO.getName());
						workflowRequestTableFields.add(wlmc);
					}

					Map gg = OaWorkFlowUtil.listAdd("gg",
							projectProductVO.getProspec());
					workflowRequestTableFields.add(gg);

					Map xh = OaWorkFlowUtil.listAdd("xh",
							projectProductVO.getProtype());
					workflowRequestTableFields.add(xh);

					Map jldw = OaWorkFlowUtil.listAdd("jldw",
							projectProductVO.getPk_measure());
					workflowRequestTableFields.add(jldw);

					Map sl = OaWorkFlowUtil.listAdd("sl", projectProductVO
							.getNnum().toString());
					workflowRequestTableFields.add(sl);

					Map gys = OaWorkFlowUtil.listAdd("gys",
							projectProductVO.getPk_supplier());
					workflowRequestTableFields.add(gys);

					String name = (String) getHyPubBO().findColValue(
							"bd_supplier",
							"name",
							"nvl(dr,0) = 0 and   pk_supplier  ='"
									+ projectProductVO.getPk_supplier() + "'");
					Map gysmc = OaWorkFlowUtil.listAdd("gysmc", name);
					workflowRequestTableFields.add(gysmc);
				}
			}

			Map hgbelx = OaWorkFlowUtil.listAdd("hgbelx", temp.getWell()
					.booleanValue() ? "是" : "否");
			workflowRequestTableFields.add(hgbelx);

			String str5 = "0";
			if (null != temp.getMemo()) {
				str5 = temp.getMemo();
			}
			Map bz = OaWorkFlowUtil.listAdd("bz", str5);
			workflowRequestTableFields.add(bz);

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	private JSONArray getMainMap(ProjectCheckHeadVO parentVO)
			throws BusinessException {
		List list = new ArrayList();

		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				parentVO.getPk_org());
		if (null != orgVO) {

			Map szgs = OaWorkFlowUtil.listAdd("szgs", orgVO.getCode());
			list.add(szgs);
		}

		Map djzj = OaWorkFlowUtil.listAdd("djzj", parentVO.getPk_pro_check());
		list.add(djzj);

		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		if (null != userVO) {

			Map zdrmc = OaWorkFlowUtil.listAdd("zdrmc", userVO.getUser_name());
			list.add(zdrmc);

			Map zdrq = OaWorkFlowUtil.listAdd("zdrq", userVO.getAbledate()
					.getYear()
					+ "-"
					+ userVO.getAbledate().getStrMonth()
					+ "-"
					+ userVO.getAbledate().getStrDay());
			list.add(zdrq);
			Map zdrzj = OaWorkFlowUtil.listAdd("zdrzj", userVO.getUser_code());
			list.add(zdrzj);
		}

		Map djh = OaWorkFlowUtil.listAdd("djh", parentVO.getBill_code());
		list.add(djh);

		String djmcNC = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				" nvl(dr,0) = 0 and pk_billtypecode = '"
						+ parentVO.getTransi_type() + "'");
		Map djmc = OaWorkFlowUtil.listAdd("djmc", djmcNC);
		list.add(djmc);

		Map sqbmbh = OaWorkFlowUtil.listAdd("sqbmbh",
				parentVO.getPk_apply_dept());
		list.add(sqbmbh);

		String dname = (String) getHyPubBO().findColValue(
				"org_dept",
				"name",
				"nvl(dr,0) = 0 and   pk_dept  ='" + parentVO.getPk_apply_dept()
						+ "'");
		Map sqbmmc = OaWorkFlowUtil.listAdd("sqbmmc", dname);
		list.add(sqbmmc);

		String project_code = (String) getHyPubBO().findColValue(
				"bd_project",
				"project_code ",
				"nvl(dr,0) = 0 and   pk_project   ='"
						+ parentVO.getPk_project() + "'");
		Map xmbm = OaWorkFlowUtil.listAdd("xmbm", project_code);
		list.add(xmbm);

		String xname = (String) getHyPubBO().findColValue(
				"bd_project",
				"project_name ",
				"nvl(dr,0) = 0 and   pk_project   ='"
						+ parentVO.getPk_project() + "'");
		Map xmmc = OaWorkFlowUtil.listAdd("xmmc", xname);
		list.add(xmmc);

		String pk_projectclass = (String) getHyPubBO().findColValue(
				"bd_project",
				"pk_projectclass ",
				"nvl(dr,0) = 0 and   pk_project   ='"
						+ parentVO.getPk_project() + "'");
		String type_name = (String) getHyPubBO().findColValue(
				"bd_projectclass",
				"type_name",
				"nvl(dr,0) = 0 and   pk_projectclass   ='" + pk_projectclass
						+ "'");
		Map xmlx = OaWorkFlowUtil.listAdd("xmlx", type_name);
		list.add(xmlx);

		Map yzbzzj = OaWorkFlowUtil.listAdd("yzbzzj",
				parentVO.getPk_checkrequest());
		list.add(yzbzzj);

		String check_stand_name = (String) getHyPubBO().findColValue(
				"pm_checkrequest",
				"check_stand_name  ",
				"nvl(dr,0) = 0 and   pk_checkrequest   ='"
						+ parentVO.getPk_checkrequest() + "'");
		Map yzbzmc = OaWorkFlowUtil.listAdd("yzbzmc", check_stand_name);
		list.add(yzbzmc);

		Map ysrq = OaWorkFlowUtil.listAdd("ysrq", parentVO.getCheck_date()
				.getYear()
				+ "-"
				+ parentVO.getCheck_date().getStrMonth()
				+ "-"
				+ parentVO.getCheck_date().getStrDay());
		list.add(ysrq);

		Map ysqk = OaWorkFlowUtil.listAdd("ysqk", parentVO.getCheck_circs());
		list.add(ysqk);

		Map ystgbelx = OaWorkFlowUtil.listAdd("ystgbelx", parentVO
				.getCheckpass().booleanValue() ? "是" : "否");
		list.add(ystgbelx);

		String str1 = "0";
		if (null != parentVO.getImpt_regular()) {
			str1 = parentVO.getImpt_regular().toString();
		}
		Map zyxhgl = OaWorkFlowUtil.listAdd("zyxhgl", str1);
		list.add(zyxhgl);

		String str2 = "0";
		if (null != parentVO.getCommon_regular()) {
			str2 = parentVO.getCommon_regular().toString();
		}
		Map ybxhgl = OaWorkFlowUtil.listAdd("ybxhgl", str2);
		list.add(ybxhgl);

		Map sqrzj = OaWorkFlowUtil.listAdd("sqrzj",
				parentVO.getPk_apply_person());
		list.add(sqrzj);

		String name = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name  ",
				"nvl(dr,0) = 0 and   pk_psndoc   ='"
						+ parentVO.getPk_apply_person() + "'");
		Map sqrmc = OaWorkFlowUtil.listAdd("sqrmc", name);
		list.add(sqrmc);

		Map sqrq = OaWorkFlowUtil.listAdd("sqrq", parentVO.getApply_date()
				.getYear()
				+ "-"
				+ parentVO.getApply_date().getStrMonth()
				+ "-"
				+ parentVO.getApply_date().getStrDay());
		list.add(sqrq);

		Map bz = OaWorkFlowUtil.listAdd("bz", parentVO.getMemo());
		list.add(bz);

		// Map sgdwzj =OaWorkFlowUtil.listAdd("sgdwzj",parentVO.getHdef1() );
		// list.add(sgdwzj);

		// if(null != parentVO.getHdef1()){
		// String where = " pk_defdoc = '" + parentVO.getHdef1() +
		// "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'sgdwmc' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
		// String hef1 = "0";
		// if(null !=(String) getHyPubBO().findColValue("bd_defdoc", "name",
		// where)){
		// hef1 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
		// where);
		// }
		String sgname = "0";
		if (null != parentVO.getHdef1()) {
			sgname = (String) getHyPubBO().findColValue(
					"bd_supplier",
					"name",
					"nvl(dr,0) = 0 and  pk_supplier  = '" + parentVO.getHdef1()
							+ "'");
		}
		Map sgdwmc = OaWorkFlowUtil.listAdd("sgdwmc", sgname);
		list.add(sgdwmc);
		// }

		// Map jldwzj =OaWorkFlowUtil.listAdd("jldwzj",parentVO.getHdef2() );
		// list.add(jldwzj);

		// if(null != parentVO.getHdef2()){
		// String where = " pk_defdoc = '" + parentVO.getHdef2() +
		// "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'jldwmc' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
		// String def2 = "0";
		// if(null !=(String) getHyPubBO().findColValue("bd_defdoc", "name",
		// where)){
		// def2 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
		// where);
		// }
		String jlname = "0";
		if (null != parentVO.getHdef2()) {
			jlname = (String) getHyPubBO().findColValue(
					"bd_supplier",
					"name",
					"nvl(dr,0) = 0 and  pk_supplier  = '" + parentVO.getHdef2()
							+ "'");
		}
		Map jldwmc = OaWorkFlowUtil.listAdd("jldwmc", jlname);
		list.add(jldwmc);
		// }

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

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

	private WorkFlowBill getWorkFlowBill(ProjectCheckBillVO temp)
			throws BusinessException {
		String where = " nvl(dr,0) = 0 and pk_bill = '" + temp.getPrimaryKey()
				+ "'";
		WorkFlowBill[] workFlowBills = (WorkFlowBill[]) getHyPubBO()
				.queryByCondition(WorkFlowBill.class, where);
		WorkFlowBill workFlowBill = new WorkFlowBill();
		if (null != workFlowBills && workFlowBills.length > 1) {
			throw new BusinessException("查询到多条流程记录");
		}
		if (null != workFlowBills && workFlowBills.length == 1) {
			workFlowBill = workFlowBills[0];
			workFlowBill.setWorkflowName("竣工验收");
		} else {
			UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
					UserVO.class, temp.getParentVO().getBillmaker());
			workFlowBill.setWorkflowId("45");
			workFlowBill.setDef5(userVO.getUser_code());

			workFlowBill.setPk_group(temp.getParentVO().getPk_group());
			workFlowBill.setPk_org(temp.getParentVO().getPk_org());
			workFlowBill.setPk_bill(temp.getPrimaryKey());
			workFlowBill.setUser_name(userVO.getUser_name());
			workFlowBill.setUser_code(userVO.getUser_code());
			workFlowBill.setBill_status("1");
			workFlowBill.setCreationtime(new UFDate().toStdString());
			workFlowBill.setCreator(temp.getParentVO().getCreator());
			workFlowBill.setBill_code("4D36");
			workFlowBill.setWorkflowName("竣工验收");
		}
		return workFlowBill;
	}

	public String getDef2(String pk_org) throws BusinessException {
		String def2 = "";
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		if (null != orgVO) {
			def2 = orgVO.getDef2();
		}
		return def2;
	}

	protected void initCommitAction(CommitAction<ProjectCheckBillVO> action,
			ProjectCheckHeadVO headVO) {
		super.initCommitAction(action);

		TransTypeExtendVO extendVO = null;
		try {
			extendVO = getITranstypeQuery().queryExtVO(headVO.getTransi_type(),
					headVO.getPk_group());
		} catch (BusinessException e) {
			ExceptionUtils.wrappException(e);
		}
		if (extendVO == null) {
			return;
		}

		if (2 == extendVO.getCheck_type().intValue()) {
			action.addBeforeRule(new ProjectCheckCheckPassRule());
		}

		action.addBeforeRule(new ProjectCheckSubmitRule());

		action.addBeforeRule(new CheckCommitRule());
	}

	public ProjectCheckBillVO[] uncommitProAccept(ProjectCheckBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		ProjectCheckBillVO[] aggVO = (ProjectCheckBillVO[]) unCommit(billVOs,
				pfParamVO);
		unOaCommit(aggVO);
		return aggVO;
	}

	private void unOaCommit(ProjectCheckBillVO[] aggVO)
			throws BusinessException {
		for (ProjectCheckBillVO temp : aggVO) {
			if ((temp.getParentVO().getTransi_type()).contains("4D36")) {
				OaWorkFlowUtil.backOaWorkFlow(temp.getPrimaryKey());
			}
		}
	}

	protected void initUnCommitAction(UnCommitAction<ProjectCheckBillVO> action) {
		super.initUnCommitAction(action);
	}

	public SuperVO[] queryProjectCheckBySqlWhere(String sqlWhere)
			throws BusinessException {
		VOQuery<ProjectCheckHeadVO> query = new VOQuery(
				ProjectCheckHeadVO.class);
		return (SuperVO[]) query.query(sqlWhere, null);
	}
}
