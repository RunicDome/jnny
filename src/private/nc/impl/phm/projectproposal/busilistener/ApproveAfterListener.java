package nc.impl.phm.projectproposal.busilistener;

import java.util.List;

import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.cmp.utils.BillcodeGenerater;
import nc.itf.ppm.schedulefill.IShceduleFillInterface;
import nc.itf.pubapp.pub.smart.IBillQueryService;
import nc.itf.uap.pf.IplatFormEntry;
import nc.itf.uif.pub.IUifService;
import nc.ui.pcm.feebalance.action.TaskProgressDetailVO;
import nc.ui.pcm.feebalance.action.TaskProgressVO;
import nc.ui.pcm.utils.GetDao;
import nc.uif.pub.exception.UifException;
import nc.vo.pfxx.util.ArrayUtils;
import nc.vo.phm.projectproposal.ProjectProposalBillVO;
import nc.vo.phm.projectproposal.ProjectProposalHeadVO;
import nc.vo.pmpub.wbs.WbsVO;
import nc.vo.ppm.schedulefill.TaskScheduleBillVO;
import nc.vo.ppm.schedulefill.TaskScheduleBodyVO;
import nc.vo.ppm.schedulefill.TaskScheduleHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

import org.apache.commons.lang3.StringUtils;

/**
 * 开工容缺受理审批单 审批后 ，更新WMS、生成进度填报单
 * 
 * @author xuwjl@yonyou.com
 * @date 2021年5月18日下午5:14:05
 */
public class ApproveAfterListener implements IBusinessListener {

	private static GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
	private static IUifService iUifService = NCLocator.getInstance().lookup(IUifService.class);
	private static IplatFormEntry ip = (IplatFormEntry) NCLocator.getInstance().lookup(IplatFormEntry.class);

	@Override
	public void doAction(IBusinessEvent arg0) throws BusinessException {
		String sourceID = arg0.getSourceID();
		String eventType = arg0.getEventType();
		Object userObject = arg0.getUserObject();
		ProjectProposalBillVO[] object = (ProjectProposalBillVO[]) ((BusinessEvent.BusinessUserObj) arg0.getUserObject()).getUserObj();
		for (ProjectProposalBillVO aggvo : object) {
			ProjectProposalHeadVO parentVO = aggvo.getParentVO();
			String transi_type = parentVO.getTransi_type();
			String pk_project = parentVO.getPk_project();//项目
			String name = "项目开工";
			WbsVO[] wbsVOs = (WbsVO[]) iUifService.queryByCondition(WbsVO.class, " pk_project = '"+pk_project+"' and wbs_name = '"+name+"'");
			if(ArrayUtils.isEmpty(wbsVOs)){
				continue;
			}
			String primaryKey = wbsVOs[0].getPrimaryKey();
			if (StringUtils.equals("4D15-Cxx-011", transi_type) || StringUtils.equals("4D15-Cxx-008", transi_type)) {
				// this.upDateWBS(parentVO,primaryKey);// 更新WBS
				this.pushTaskProcessByProposal(aggvo,primaryKey,wbsVOs[0]);// 生成进度填报单
			}
		}
	}

	private void pushTaskProcessByProposal(ProjectProposalBillVO aggvo, String primaryKey, WbsVO wbsVOs) throws ValidationException, BusinessException {

		ProjectProposalHeadVO headVO = (ProjectProposalHeadVO) aggvo.getParentVO();
		// 表体添加
		TaskProgressVO taskProgressVO = new TaskProgressVO();
		taskProgressVO.setBillmaker(headVO.getBillmaker());
		taskProgressVO.setBillmaketime(headVO.getBillmaketime()); // 制单日期
		taskProgressVO.setCreator(headVO.getCreator());
		taskProgressVO.setCreationtime(headVO.getCreationtime());
		taskProgressVO.setMemo(headVO.getMemo()); // 备注
		taskProgressVO.setPk_filldept(headVO.getDef1()); // 经办部门 自定义项1
		taskProgressVO.setPk_filldept_v(headVO.getDef1()); // 经办部门多版本
		taskProgressVO.setPk_fillmaker(headVO.getBillmaker()); // 填报人
		taskProgressVO.setPk_group(headVO.getPk_group()); // 集团
		taskProgressVO.setPk_org(headVO.getPk_org()); // 组织
		taskProgressVO.setPk_org_v(headVO.getPk_org_v()); // 组织
		taskProgressVO.setBillPk(headVO.getPrimaryKey());// 单据主键
		taskProgressVO.setPk_project(headVO.getPk_project()); // 项目取表体数据

		// 项目明细
		TaskProgressDetailVO taskProgressDetailVO = new TaskProgressDetailVO();
		taskProgressDetailVO.setActu_start_date(wbsVOs.getActu_start_date()); // 实际开始时间
		taskProgressDetailVO.setActu_finish_date(wbsVOs.getActu_finish_date()); // 实际结束时间
		
		taskProgressDetailVO.setEvolve_state(""); // 进展说明
		taskProgressDetailVO.setMemo(""); // 备注
		taskProgressDetailVO.setPk_project(headVO.getPk_project()); // 项目
		taskProgressDetailVO.setPk_wbs(primaryKey); // WBS任务 写死 自定项10
		// taskProgressDetailVO.setPrefinishpercent(new UFDouble("545.00"));
		// // 上期实际完成，合同不写
		taskProgressDetailVO.setPk_wbsstate("4");
		taskProgressDetailVO.setTtaskpercent(new UFDouble("100")); // 本期实际完成

		taskProgressVO.setTaskProgressDetailVO(taskProgressDetailVO);

		// 执行插入
		String getwbsql = "SELECT NVL(MAX(MX.TASKPERCENT),0) AS SL FROM PM_TASKSCHEDULE JD LEFT JOIN " + "PM_TASKSCHEDULE_B MX ON JD.PK_TASKSCHEDULE = MX.PK_TASKSCHEDULE WHERE JD.DR = 0 AND "
				+ "MX.DR = 0 AND MX.PK_WBS = '" + primaryKey + "'";
		List<Object[]> ls = getDao.query(getwbsql);
		if (Integer.parseInt(ls.get(0)[0] + "") < 100) {
			insertTaskBillVO(taskProgressVO, null);
		}

	}

	private void insertTaskBillVO(TaskProgressVO taskProgressVO, List<TaskProgressDetailVO> taskProgressDetailVOArr) throws ValidationException, BusinessException {
		IShceduleFillInterface ipf = (IShceduleFillInterface) NCLocator.getInstance().lookup(IShceduleFillInterface.class);
		TaskProgressDetailVO taskProgressDetailVO = taskProgressVO.getTaskProgressDetailVO();
		if (taskProgressDetailVO.getPk_wbs() == null || "".equals(taskProgressDetailVO.getPk_wbs()) || "null".equals(taskProgressDetailVO.getPk_wbs())) {
			return;
		} else {
			// 如果表体为空，所有数据从表头添加
			if (taskProgressDetailVOArr == null) {

				TaskScheduleBillVO taskScheduleBillVO = new TaskScheduleBillVO();
				// ----------------------单据表头VO----------------------
				TaskScheduleHeadVO taskScheduleHeadVO = new TaskScheduleHeadVO();
				// 获取单据编号
				BillcodeGenerater gene = new BillcodeGenerater();
				String billno = gene.getBillCode("4D24", taskProgressVO.getPk_group(), taskProgressVO.getPk_org(), null, null);
				System.out.println("输出生成的单据编号：" + billno);
				taskScheduleHeadVO.setBill_code(billno);// 单据编号
				// taskScheduleHeadVO.setAuditor(""); // 审批人
				// taskScheduleHeadVO.setAudittime(); // 审批日期
				// taskScheduleHeadVO.setBill_status(-1); // 单据状态 默认提交态
				taskScheduleHeadVO.setBill_type("4D24"); // bill_type
				taskScheduleHeadVO.setBillmaker(taskProgressVO.getBillmaker()); // 制单人
				taskScheduleHeadVO.setBillmaketime(taskProgressVO.getBillmaketime()); // 制单日期
				taskScheduleHeadVO.setCreator(taskProgressVO.getCreator() + ""); // 创建人
				taskScheduleHeadVO.setCreationtime(taskProgressVO.getCreationtime()); // 创建日期
				taskScheduleHeadVO.setFilltime(taskProgressVO.getBillmaketime()); // 填报日期，默认制单日期
				taskScheduleHeadVO.setMemo(taskProgressVO.getMemo()); // 备注
				System.out.println("部门：" + taskProgressVO.getPk_filldept() + "人员：" + taskProgressVO.getPk_fillmaker());
				taskScheduleHeadVO.setPk_filldept(taskProgressVO.getPk_filldept()); // 经办部门
				taskScheduleHeadVO.setPk_filldept_v(taskProgressVO.getPk_filldept_v()); // 经办部门多版本
				taskScheduleHeadVO.setPk_fillmaker(taskProgressVO.getPk_fillmaker()); // 填报人
				taskScheduleHeadVO.setPk_group(taskProgressVO.getPk_group()); // 集团
				taskScheduleHeadVO.setPk_org(taskProgressVO.getPk_org()); // 组织
				taskScheduleHeadVO.setPk_org_v(taskProgressVO.getPk_org_v()); // 组织
				taskScheduleHeadVO.setPk_project(taskProgressDetailVO.getPk_project()); // 项目pkOID取表体里面的object
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
				taskScheduleBodyVO.setActu_start_date(taskProgressDetailVO.getActu_start_date()); // 实际开始时间
				System.out.println("taskProgressDetailVO.getTtaskpercent()=" + taskProgressDetailVO.getTtaskpercent());
				if (Double.parseDouble(taskProgressDetailVO.getTtaskpercent().toString()) == 100) {
					taskScheduleBodyVO.setActu_finish_date(taskProgressDetailVO.getActu_finish_date()); // 时间结束时间
					taskScheduleBodyVO.setPk_wbsstate(4); // WBS任务状态 完成
				} else {
					taskScheduleBodyVO.setPk_wbsstate(1); // WBS任务状态 进行中
				}

				taskScheduleBodyVO.setDr(0);
				taskScheduleBodyVO.setEvolve_state(taskProgressDetailVO.getEvolve_state()); // 进展说明
				taskScheduleBodyVO.setMemo(taskProgressDetailVO.getMemo()); // 备注
				taskScheduleBodyVO.setPk_project(taskProgressDetailVO.getPk_project()); // 项目
				taskScheduleBodyVO.setPk_wbs(taskProgressDetailVO.getPk_wbs()); // WBS任务
				String totalMax = getMaxFinish(taskProgressVO.getPk_org(), taskProgressDetailVO.getPk_project(), taskProgressDetailVO.getPk_wbs(), taskScheduleBodyVO.getPk_taskschedule_b());// 最大完成
				if (!"".equals(totalMax)) {
					taskScheduleBodyVO.setPrefinishpercent(new UFDouble(totalMax)); // 上期实际完成
					if (Double.parseDouble(totalMax) == 100) {
						taskScheduleBodyVO.setPre_wbsstate(4);
					} else {
						taskScheduleBodyVO.setPre_wbsstate(1);
					}
				}

				taskScheduleBodyVO.setTaskpercent(taskProgressDetailVO.getTtaskpercent()); // 本期实际完成

				// ----------------------调用接口写入----------------------
				CircularlyAccessibleValueObject[] vos = new TaskScheduleBodyVO[] { taskScheduleBodyVO };
				taskScheduleBillVO.setChildrenVO(vos);
				// pk = taskScheduleHeadVO.getPrimaryKey();
				TaskScheduleBillVO[] taskScheduleBillVOArr = new TaskScheduleBillVO[] { taskScheduleBillVO };
				ipf.insertTaskScheduleBillVOS(taskScheduleBillVOArr);
				String pks = Schepk(taskProgressVO.getBillPk(), taskProgressVO.getPk_project());
				IBillQueryService billQuery = NCLocator.getInstance().lookup(IBillQueryService.class);
				TaskScheduleBillVO ystzvo = billQuery.querySingleBillByPk(TaskScheduleBillVO.class, pks);
				if (ystzvo != null) {
					ip.processAction("APPROVE", "4D24", null, ystzvo, null, null);
				}

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
					String billno = gene.getBillCode("4D24", taskProgressVO.getPk_group(), taskProgressVO.getPk_org(), null, null);
					taskScheduleHeadVO.setBill_code(billno);// 单据编号

					// taskScheduleHeadVO.setAuditor(""); // 审批人
					// taskScheduleHeadVO.setAudittime(); // 审批日期
					// taskScheduleHeadVO.setBill_status(-1); // 单据状态 默认提交态
					taskScheduleHeadVO.setBill_type("4D24"); // bill_type
					taskScheduleHeadVO.setBillmaker(taskProgressVO.getBillmaker()); // 制单人
					taskScheduleHeadVO.setBillmaketime(taskProgressVO.getBillmaketime()); // 制单日期
					taskScheduleHeadVO.setCreator(taskProgressVO.getCreator() + ""); // 创建人
					taskScheduleHeadVO.setCreationtime(taskProgressVO.getCreationtime()); // 创建日期
					taskScheduleHeadVO.setFilltime(taskProgressVO.getBillmaketime()); // 填报日期，默认制单日期
					taskScheduleHeadVO.setMemo(taskProgressVO.getMemo()); // 备注
					taskScheduleHeadVO.setPk_filldept(taskProgressVO.getPk_filldept()); // 经办部门
					taskScheduleHeadVO.setPk_filldept_v(taskProgressVO.getPk_filldept_v()); // 经办部门多版本
					taskScheduleHeadVO.setPk_fillmaker(taskProgressVO.getPk_fillmaker()); // 填报人
					taskScheduleHeadVO.setPk_group(taskProgressVO.getPk_group()); // 集团
					taskScheduleHeadVO.setPk_org(taskProgressVO.getPk_org()); // 组织
					taskScheduleHeadVO.setPk_org_v(taskProgressVO.getPk_org_v()); // 组织
					taskScheduleHeadVO.setPk_project(taskProgressDetailVO.getPk_project()); // 项目pkOID取表体里面的object
					taskScheduleHeadVO.setPk_transitype("0001A21000000000PG0E"); // 交易类型
					taskScheduleHeadVO.setTransi_type("4D24-01"); // 交易类型编码
																	// transi_type
					taskScheduleHeadVO.setHdef10(taskProgressVO.getBillPk());// 自定义项10存放单据pk
					taskScheduleHeadVO.setDr(0);
					taskScheduleHeadVO.setAuditor(taskProgressVO.getBillmaker()); // 审批人
					taskScheduleHeadVO.setAudittime(taskProgressVO.getBillmaketime()); // 审批日期
					taskScheduleHeadVO.setBill_status(1); // 单据状态 默认审批态
					// 写入表头
					taskScheduleBillVO.setParent(taskScheduleHeadVO);

					// ----------------------单据表体VO----------------------
					TaskScheduleBodyVO taskScheduleBodyVO = new TaskScheduleBodyVO();
					// 添加子表字段
					taskScheduleBodyVO.setActu_start_date(taskProgressDetailVO.getActu_start_date()); // 实际开始时间
					if ((new UFDouble("100")) == taskProgressDetailVO.getTtaskpercent()) {
						taskScheduleBodyVO.setActu_finish_date(taskProgressDetailVO.getActu_finish_date()); // 时间结束时间
						taskScheduleBodyVO.setPk_wbsstate(4); // WBS任务状态 进行中
					}
					// taskScheduleBodyVO.setActu_finish_date(taskProgressDetailVO.getActu_finish_date());
					// // 时间结束时间
					taskScheduleBodyVO.setDr(0);
					taskScheduleBodyVO.setEvolve_state(""); // 进展说明
					taskScheduleBodyVO.setMemo(taskProgressDetailVO.getMemo()); // 备注
					taskScheduleBodyVO.setPk_project(taskProgressDetailVO.getPk_project()); // 项目
					taskScheduleBodyVO.setPk_wbs(taskProgressDetailVO.getPk_wbs()); // WBS任务
					taskScheduleBodyVO.setPk_wbsstate(0); // WBS任务状态
					taskScheduleBodyVO.setPrefinishpercent(taskProgressDetailVO.getPrefinishpercent()); // 上期实际完成
					taskScheduleBodyVO.setTaskpercent(taskProgressDetailVO.getTtaskpercent()); // 本期时间完成
					// ----------------------调用接口写入----------------------

					CircularlyAccessibleValueObject[] vos = new TaskScheduleBodyVO[] { taskScheduleBodyVO };
					taskScheduleBillVO.setChildrenVO(vos);

					TaskScheduleBillVO[] taskScheduleBillVOArr = new TaskScheduleBillVO[] { taskScheduleBillVO };
					ipf.insertTaskScheduleBillVOS(taskScheduleBillVOArr);

				}
			}

		}

	}

	private void upDateWBS(ProjectProposalHeadVO parentVO, String primaryKey) throws UifException {
			WbsVO wbsVO = (WbsVO) iUifService.queryByPrimaryKey(WbsVO.class, primaryKey);
			wbsVO.setPlan_start_date(parentVO.getReq_start_date());
			wbsVO.setPlan_finish_date(new UFDate());
			wbsVO.setStatus(VOStatus.UPDATED);
			wbsVO.setDr(0);
			wbsVO.setStatus(VOStatus.UPDATED);
			iUifService.update(wbsVO);
	}
	
	// 去除重复项目
		public List<TaskProgressDetailVO> removeDuplicate(List<TaskProgressDetailVO> oldList) {
			for (int i = 0; i < oldList.size() - 1; i++) {
				TaskProgressDetailVO taskProgressDetailVOFirst = oldList.get(i);
				String fristProject = taskProgressDetailVOFirst.getPk_project();
				for (int j = oldList.size() - 1; j > i; j--) {
					TaskProgressDetailVO taskProgressDetailVOSecond = oldList.get(j);
					String secondProject = taskProgressDetailVOSecond.getPk_project();
					// if (oldList.get(j).equals(oldList.get(i))) {
					if (fristProject.equals(secondProject)) {
						oldList.remove(j);
					}
				}
			}
			return oldList;
		}

		public Boolean getTaskScheduleByBillType(String billType) throws DAOException {
			String sql = "select * from pm_taskschedule where hdef10='" + billType + "' and dr = 0";
			List<Object[]> orderLs = getDao.query(sql);
			if (orderLs.size() > 0 && orderLs.get(0) != null) {
				return true;
			} else {
				return false;
			}
		}

		public String getMaxFinish(String pk_org, String pk_project, String pk_wbs, String PK_TASKSCHEDULE_B) {
			String maxTotal = "";
			String sql = "SELECT MAX(JDMX.TASKPERCENT) AS MAXTOTAL FROM PM_TASKSCHEDULE_B JDMX LEFT JOIN PM_TASKSCHEDULE JD ON "
					+ "JDMX.PK_TASKSCHEDULE = JD.PK_TASKSCHEDULE WHERE JD.DR = 0 AND JDMX.DR = 0 AND JD.PK_ORG = '" + pk_org + "' AND JDMX.PK_PROJECT = '" + pk_project + "' AND " + "JDMX.PK_WBS = '"
					+ pk_wbs + "' AND PK_TASKSCHEDULE_B <> '" + PK_TASKSCHEDULE_B + "'";
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

		public String Schepk(String billPK, String pk_project) throws DAOException {
			String pk = "";
			String sql = "SELECT PK_TASKSCHEDULE FROM PM_TASKSCHEDULE WHERE HDEF10 = '" + billPK + "' AND DR = 0";
			if (!"".equals(pk_project)) {
				sql += " AND PK_PROJECT = '" + pk_project + "'";
			}
			List<Object[]> ls = getDao.query(sql);
			if (ls != null && ls.size() > 0 && ls.get(0)[0] != null) {
				pk = ls.get(0)[0] + "";
			}
			return pk;
		}

}
