package nc.bs.ct.HTApproveListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.bs.businessevent.BdUpdateEvent;
import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.ic.general.businessevent.ICGeneralCommonEvent;
import nc.bs.ppm.schedule.utils.TaskPushUtils;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.oa.web.HTFileUtils;
import nc.ui.pcm.feebalance.action.TaskProgressDetailVO;
import nc.ui.pcm.feebalance.action.TaskProgressVO;
import nc.ui.pcm.utils.GetDao;
import nc.vo.ct.purdaily.entity.AggCtPuVO;
import nc.vo.ct.purdaily.entity.CtPuBVO;
import nc.vo.ct.purdaily.entity.CtPuVO;
import nc.vo.ic.m4a.entity.GeneralInVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

@SuppressWarnings({ "unused" })
public class HTApproveListener implements IBusinessListener {
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);

	public void doAction(IBusinessEvent event) throws BusinessException {
		BdUpdateEvent et = (BdUpdateEvent) event;
		AggCtPuVO[] aggvos = (AggCtPuVO[]) et.getOldObject();

		if (aggvos != null) {
			CtPuVO ctPuVO = (CtPuVO) aggvos[0].getParentVO();
			System.out.println("合同名称：" + ctPuVO.getCtname());
			// 推送电子合同
			/*String dzsql = "SELECT COUNT('contractPk') as sl FROM RL_DZHTLOGC where 'contractPk' = '"
					+ ctPuVO.getPrimaryKey() + "'";
			List<Object[]> dzsqlls = getDao.query(dzsql);
			if (Integer.parseInt(dzsqlls.get(0)[0].toString()) > 0) {
				HTFileUtils fileUtils = new HTFileUtils();
				String rtfu = fileUtils.afterApprove(ctPuVO.getPrimaryKey(),
						"1");
				if ("Y".equals(rtfu)) {
					System.out.println("审批成功后传送电子合同系统成功！");
				} else {
					System.out.println("审批成功后传送电子合同系统成功！");
				}
			}*/
			/*HTFileUtils fileUtils = new HTFileUtils();
			String rtfu = fileUtils.afterApprove(ctPuVO.getPrimaryKey(),
					"1");
			if ("Y".equals(rtfu)) {
				System.out.println("审批成功后传送电子合同系统成功！");
			} else {
				System.out.println("审批成功后传送电子合同系统成功！");
			}
			CtPuBVO[] contrWorksVOVOArr = (CtPuBVO[]) aggvos[0].getChildrenVO();
			// 循环表体添加
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
			for (int i = 0; i < contrWorksVOVOArr.length; i++) {
				CtPuBVO bodyVO = contrWorksVOVOArr[i];
				if (bodyVO.getVbdef10() != null) {
					String updsql = "UPDATE PM_WBS SET PLAN_START_DATE = '"
							+ ctPuVO.getSubscribedate()
							+ "',PLAN_FINISH_DATE = '" + df.format(new Date())
							+ "' WHERE PK_WBS = '" + bodyVO.getVbdef10() + "'";
					System.out.println("更新SQL：" + updsql);
					new BaseDAO().executeUpdate(updsql);
				}
			}*/
			pushTaskProcessByContract(aggvos[0]);
		}
	}

	private AggCtPuVO[] getVOs(IBusinessEvent event) throws BusinessException {

		// ValueObject obj = (ValueObject) event.getUserObject();
		AggCtPuVO[] vos = null;
		/*
		 * boolean s = obj instanceof
		 * ICGeneralCommonEvent.ICGeneralCommonUserObj; if ((obj instanceof
		 * ICGeneralCommonEvent.ICGeneralCommonUserObj)) {
		 * ICGeneralCommonEvent.ICGeneralCommonUserObj objs =
		 * (ICGeneralCommonEvent.ICGeneralCommonUserObj) obj; vos =
		 * (AggCtPuVO[]) objs.getOldObjects(); } else {
		 * //BusinessEvent.BusinessUserObj objs =
		 * (BusinessEvent.BusinessUserObj) obj; //vos = (AggCtPuVO[]) obj.get
		 * //String pk = obj.getPrimaryKey(); } if ((vos == null) || (vos.length
		 * == 0)) { return null; }
		 */
		ICGeneralCommonEvent e = (ICGeneralCommonEvent) event;// 先强制转换event
		Object[] value = e.getOldObjs();// 获取event里面的最原始对象集
		Object data = value[0];
		if (data instanceof GeneralInVO) { // 判断是否可以转成一般的VO

		}
		return vos;
	}

	@SuppressWarnings("unchecked")
	public Boolean pushTaskProcessByContract(AggCtPuVO billVO)
			throws BusinessException {
		try {
			CtPuVO headVO = (CtPuVO) billVO.getParentVO();
			CtPuBVO[] contrWorksVOVOArr = (CtPuBVO[]) billVO.getChildrenVO();
			// 循环表体添加
			for (int i = 0; i < contrWorksVOVOArr.length; i++) {	
				CtPuBVO bodyVO = contrWorksVOVOArr[i];
				if(bodyVO.getVbdef11() != null && !"~".equals(bodyVO.getVbdef11())){
					// 判断总进度
					UFDouble sqjd = new UFDouble(0);// 上期完成
					String getwbsql = "SELECT NVL(MAX(MX.TASKPERCENT),0) AS SL FROM PM_TASKSCHEDULE JD LEFT JOIN "
							+ "PM_TASKSCHEDULE_B MX ON JD.PK_TASKSCHEDULE = MX.PK_TASKSCHEDULE WHERE JD.DR = 0 AND "
							+ "MX.DR = 0 AND MX.PK_WBS = '" + bodyVO.getVbdef11() + "'";
					ArrayList<UFDouble> a = (ArrayList<UFDouble>) new BaseDAO()
							.executeQuery(getwbsql, new ColumnListProcessor());
					UFDouble taskpercevt = new UFDouble(String.valueOf(a.get(0)));
					if (taskpercevt.compareTo(new UFDouble(100)) < 0) {
						sqjd = taskpercevt;
						TaskProgressVO taskProgressVO = new TaskProgressVO();
						taskProgressVO.setBillmaketime(headVO.getDmakedate()); // 制单日期
						taskProgressVO.setCreator(headVO.getCreator());
						taskProgressVO.setCreationtime(headVO.getCreationtime());
						taskProgressVO.setMemo(headVO.getVdef7()); // 备注
						taskProgressVO.setPk_filldept(headVO.getDepid()); // 部门
						taskProgressVO.setPk_filldept_v(headVO.getDepid_v()); // 部门多版本
						taskProgressVO.setPk_fillmaker(headVO.getBillmaker()); // 填报人
						taskProgressVO.setPk_group(headVO.getPk_group()); // 集团
						taskProgressVO.setPk_org(headVO.getPk_org()); // 组织
						taskProgressVO.setPk_org_v(headVO.getPk_org_v()); // 组织
						taskProgressVO.setBillPk(headVO.getPrimaryKey());// 单据主键
						taskProgressVO.setPk_project(bodyVO.getCbprojectid()); // 项目取表体数据
		
						// 项目明细
						TaskProgressDetailVO taskProgressDetailVO = new TaskProgressDetailVO();
						taskProgressDetailVO.setActu_start_date(headVO.getDmakedate()); // 实际开始时间
						taskProgressDetailVO.setActu_finish_date(headVO.getDmakedate()); // 实际结束时间
		
						taskProgressDetailVO.setEvolve_state(""); // 进展说明
						taskProgressDetailVO.setMemo(""); // 备注
						taskProgressDetailVO.setPk_project(bodyVO.getCbprojectid()); // 项目
						taskProgressDetailVO.setPk_wbs(bodyVO.getVbdef11()); // WBS任务
						taskProgressDetailVO.setPrefinishpercent(sqjd); // 上期实际完成
						taskProgressDetailVO.setTtaskpercent(new UFDouble("100")); // 本期时间完成
						taskProgressDetailVO.setPk_wbsstate("4");// pk_wbsstate 任务状态 4=完成
						taskProgressVO.setTaskProgressDetailVO(taskProgressDetailVO);
						// InsertAndUptTaskBIll insertAndUptTaskBIll =
						// NCLocator.getInstance().lookup(InsertAndUptTaskBIll.class);
						// 执行插入
						TaskPushUtils insertUptTaskBill = new TaskPushUtils();
						// TaskUtils insertUptTaskBill =
						// NCLocator.getInstance().lookup(TaskUtils.class);
						// InsertAndUptTaskBIll insertUptTaskBill =
						// NCLocator.getInstance().
						// lookup(InsertAndUptTaskBIll.class);
						insertUptTaskBill.insertTaskBillVO(taskProgressVO, null);
					}
				}
			}
		} catch (BusinessException e) {

			return false;
		}
		return true;
	}
}
