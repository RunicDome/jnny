package nc.bs.ct.HTApproveListener;

import java.util.ArrayList;

import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.dao.BaseDAO;
import nc.bs.ppm.schedule.utils.TaskPushUtils;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.ui.pcm.feebalance.action.TaskProgressDetailVO;
import nc.ui.pcm.feebalance.action.TaskProgressVO;
import nc.vo.pcm.contract.ContrHeadVO;
import nc.vo.pcm.contract.ContrWorksVO;
import nc.vo.pcm.contract.ContractBillVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

public class QDHTApproveListener implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		// TODO 自动生成的方法存根
		ContractBillVO[] aggvos = null;
		Object object = ((BusinessEvent) event).getObject();
		if (object.getClass().isArray()) {
			AggregatedValueObject[] aggVO = (AggregatedValueObject[]) object;
			if ((aggVO instanceof ContractBillVO[])) {
				aggvos = (ContractBillVO[]) aggVO;
			}
		}
		if (aggvos != null) {
			/*ContrHeadVO headVO = (ContrHeadVO) aggvos[0].getParentVO();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
			String updsql = "UPDATE PM_WBS SET PLAN_START_DATE = '"
					+ headVO.getPromisetime() + "',PLAN_FINISH_DATE = '"
					+ df.format(new Date()) + "' WHERE PK_WBS = '"
					+ headVO.getPk_wbs() + "'";
			System.out.println("更新SQL：" + updsql);
			new BaseDAO().executeUpdate(updsql);*/
			pushTaskProcessByContract(aggvos[0]);
		}
	}

	// 根据清单发包合同推送
	@SuppressWarnings("unchecked")
	public Boolean pushTaskProcessByContract(ContractBillVO billVO)
			throws BusinessException {
		try {
			ContrHeadVO headVO = (ContrHeadVO) billVO.getParentVO();
			ContrWorksVO[] contrWorksVOVOArr = (ContrWorksVO[]) billVO
					.getChildrenVO();
			System.out.println("长度：" + contrWorksVOVOArr.length);
			// 循环表体添加
			for (int i = 0; i < contrWorksVOVOArr.length; i++) {

				ContrWorksVO bodyVO = contrWorksVOVOArr[i];
				// 判断总进度
				UFDouble sqjd = new UFDouble(0);// 上期完成
				String getwbsql = "SELECT NVL(MAX(MX.TASKPERCENT),0) AS SL FROM PM_TASKSCHEDULE JD LEFT JOIN "
						+ "PM_TASKSCHEDULE_B MX ON JD.PK_TASKSCHEDULE = MX.PK_TASKSCHEDULE WHERE JD.DR = 0 AND "
						+ "MX.DR = 0 AND MX.PK_WBS = '" + headVO.getPk_wbs() + "'";
				ArrayList<UFDouble> a = (ArrayList<UFDouble>) new BaseDAO()
						.executeQuery(getwbsql, new ColumnListProcessor());
				UFDouble taskpercevt = new UFDouble(String.valueOf(a.get(0)));
				if (taskpercevt.compareTo(new UFDouble(100)) < 0) {
					sqjd = taskpercevt;
					TaskProgressVO taskProgressVO = new TaskProgressVO();
					taskProgressVO.setBillmaketime(headVO.getBillmaketime()); // 制单日期
					taskProgressVO.setCreator(headVO.getCreator());
					taskProgressVO.setCreationtime(headVO.getCreationtime());
					// taskProgressVO.setMemo(headVO.getMemo()); // 备注
					taskProgressVO.setPk_filldept(headVO.getPk_promise_dept()); // 经办部门
					taskProgressVO.setPk_filldept_v(headVO.getPk_pro_dept_v()); // 经办部门多版本
					taskProgressVO.setPk_fillmaker(headVO.getBillmaker()); // 填报人
					taskProgressVO.setPk_group(headVO.getPk_group()); // 集团
					taskProgressVO.setPk_org(headVO.getPk_org()); // 组织
					taskProgressVO.setPk_org_v(headVO.getPk_org_v()); // 组织
					taskProgressVO.setBillPk(headVO.getPrimaryKey());// 单据主键
					taskProgressVO.setPk_project(bodyVO.getPk_project()); // 项目取表体数据

					// 项目明细
					TaskProgressDetailVO taskProgressDetailVO = new TaskProgressDetailVO();
					taskProgressDetailVO.setActu_start_date(headVO
							.getBillmaketime()); // 实际开始时间
					taskProgressDetailVO.setActu_finish_date(headVO
							.getBillmaketime()); // 实际结束时间

					taskProgressDetailVO.setEvolve_state(""); // 进展说明
					taskProgressDetailVO.setMemo(""); // 备注
					taskProgressDetailVO.setPk_project(headVO.getPk_project()); // 项目
					taskProgressDetailVO.setPk_wbs(headVO.getPk_wbs()); // WBS任务
					taskProgressDetailVO.setPrefinishpercent(sqjd); // 上期实际完成
					taskProgressDetailVO.setPk_wbsstate("4");// pk_wbsstate 任务状态 4=完成
					taskProgressDetailVO.setTtaskpercent(new UFDouble("100")); // 本期时间完成

					taskProgressVO.setTaskProgressDetailVO(taskProgressDetailVO);
					TaskPushUtils insertAndUptTaskBIll = new TaskPushUtils();
					// TaskUtils insertAndUptTaskBIll =
					// NCLocator.getInstance().lookup(TaskUtils.class);
					// System.out.println(taskProgressVO);
					// 执行插入
					insertAndUptTaskBIll.insertTaskBillVO(taskProgressVO, null);
				}
			}
		} catch (BusinessException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
