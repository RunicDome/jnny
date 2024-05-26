package nc.itf.ssc.task.tools;

import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.ssc.task.task.ISSCTaskService;
import nc.itf.ssc.task.task.SSCTaskCreatorFactory;
import nc.itf.ssc.tools.SqlTool;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.ssc.task.base.SSCBusiOpVO;
import nc.vo.ssc.task.pool.ActiveTaskVO;
import nc.vo.ssc.task.pool.PersonTaskVO;
import nc.vo.ssc.task.pool.PostTaskVO;
import nc.vo.ssc.task.service.SSCTaskPreVO;
import nc.vo.ssc.task.task.SSCTaskAdapter;
import nc.vo.ssc.task.task.SSCTaskLogVO;
import nc.vo.ssc.task.task.SSCTaskVO;

@SuppressWarnings({"unchecked","rawtypes"})
public class SSCTaskObjectFactory {
	public static ActiveTaskVO createActiveTask(SSCTaskVO taskvo,
			String[] pk_busiactivities, String firstact) {
		ActiveTaskVO activeTask = SSCTaskAdapter.getActiveTask(taskvo);
		activeTask.setPk_busiactivity(firstact);
		activeTask.setFirstact(firstact);
		activeTask.setPk_taskstate(null);
		activeTask.setReceivetime(new UFDateTime());
		activeTask.setPk_user(null);
		activeTask.setPk_post(null);
		activeTask.setBusiactivitylist(SqlTool.jointStr(",", pk_busiactivities));

		return activeTask;
	}

	public static SSCTaskVO createTaskVO(String src_system, String relationid,
			SSCTaskPreVO billvo) throws BusinessException {
		SSCTaskVO taskvo = SSCTaskCreatorFactory.getTaskCreator()
				.create(billvo);

		taskvo.setSrc_system(src_system);
		taskvo.setBillrelationid(relationid);
		taskvo.setReceivetime(new UFDateTime());
		//XBX新增供应商
		String billtype = billvo.getPk_billtype();
		Logger.error("billtype===="+billtype);
		Logger.error("billtype===="+(!"0000Z30000000000264X".equals(billtype)));
		if(!"0000Z30000000000264X".equals(billtype)){
			AggregatedValueObject s = (AggregatedValueObject) billvo.getBillvo();
			String gys = s.getParentVO().getAttributeValue("supplier") + "";
			taskvo.setDef3(gys);//供应商
		}
		
		return taskvo;
	}

	public static PostTaskVO createPostTask(String pk_post, ActiveTaskVO avo,
			Map<String, List<String>> activeUserMap) {
		PostTaskVO rsvo = new PostTaskVO();
		rsvo.setPk_activetask(avo.getPk_activetask());
		rsvo.setPk_busiactivity(avo.getPk_busiactivity());
		rsvo.setPk_post(pk_post);
		rsvo.setPk_ssctask(avo.getPk_ssctask());
		rsvo.setPk_group(avo.getPk_group());
		rsvo.setPk_org(avo.getPk_org());
		rsvo.setOrderfield(avo.getExigencelevel() + avo.getReceivetime());

		if ((activeUserMap != null)
				&& (activeUserMap.containsKey(avo.getPk_activetask()))) {
			List<String> pk_users = (List) activeUserMap.get(avo
					.getPk_activetask());
			if (pk_users != null) {
				rsvo.setUserpks(pk_users);
			}
		}

		return rsvo;
	}

	public static PostTaskVO createPostTask(String pk_post, ActiveTaskVO avo) {
		return createPostTask(pk_post, avo, null);
	}

	public static PostTaskVO createPostTask(PersonTaskVO psnvo) {
		PostTaskVO rsvo = new PostTaskVO();
		rsvo.setPk_activetask(psnvo.getPk_activetask());
		rsvo.setPk_busiactivity(psnvo.getPk_busiactivity());
		rsvo.setPk_post(psnvo.getPk_post());
		rsvo.setPk_ssctask(psnvo.getPk_ssctask());
		rsvo.setPk_group(psnvo.getPk_group());
		rsvo.setPk_org(psnvo.getPk_org());

		return rsvo;
	}

	public static PersonTaskVO createPersonTask(String pk_user,
			ActiveTaskVO avo, PostTaskVO pvo) {
		PersonTaskVO rs = new PersonTaskVO();
		rs.setPk_group(pvo.getPk_group());
		rs.setPk_activetask(avo.getPk_activetask());
		rs.setPk_busiactivity(pvo.getPk_busiactivity());
		rs.setPk_org(pvo.getPk_org());
		rs.setPk_post(pvo.getPk_post());
		rs.setPk_ssctask(avo.getPk_ssctask());
		rs.setPk_user(pk_user);
		rs.setAppointtime(new UFDateTime());
		rs.setIsappointed(UFBoolean.FALSE);
		rs.setIstimewarning(UFBoolean.FALSE);
		rs.setOrderfield(avo.getExigencelevel() + avo.getReceivetime());
		return rs;
	}

	public static SSCTaskLogVO createOperationLog(SSCBusiOpVO opvo,
			PersonTaskVO pvo, ActiveTaskVO avo, String message) {
		SSCTaskLogVO rs = new SSCTaskLogVO();
		if (pvo == null) {
			rs.setPk_busiact(avo.getPk_busiactivity());
			rs.setPk_operator(avo.getPk_user());
			rs.setPk_post(avo.getPk_post());
			rs.setAppointtime(avo.getAppointtime());
		} else {
			rs.setPk_busiact(pvo.getPk_busiactivity());
			rs.setPk_operator(pvo.getPk_user());
			rs.setPk_post(pvo.getPk_post());
			rs.setAppointtime(pvo.getAppointtime());
		}
		rs.setPk_busiop(opvo.getPk_busioperation());
		rs.setMessage(message);
		if (opvo.getEndstate() == null) {
			rs.setEndstate(avo.getPk_taskstate());
		} else if ("0001Z0TASKSTATE00099".equals(opvo.getEndstate())) {
			try {
				SSCTaskVO[] tvos = ((ISSCTaskService) NCLocator.getInstance()
						.lookup(ISSCTaskService.class)).queryByCondition(
						"pk_ssctask='" + avo.getPk_ssctask() + "'", null, true);

				if ((tvos != null) && (tvos.length > 0)) {
					rs.setEndstate(SSCTaskAdapter.getLastEndOperationLog(
							tvos[0], opvo.getBeginstate()).getBeginstate());
				}
			} catch (BusinessException e) {
				Logger.error("", e);
			}
		} else {
			rs.setEndstate(opvo.getEndstate());
		}
		rs.setEndtime(new UFDateTime());
		if (opvo.getBeginstate() == null) {
			rs.setBeginstate(avo.getPk_taskstate());
		} else
			rs.setBeginstate(opvo.getBeginstate());
		rs.setBegintime(avo.getLastoptime() == null ? avo.getReceivetime()
				: avo.getLastoptime());

		rs.setPk_ssctask(avo.getPk_ssctask());
		rs.setLasttime(Integer.valueOf(UFDateTime.getSecondsBetween(
				rs.getBegintime(), rs.getEndtime())));

		return rs;
	}

	public static SSCTaskLogVO createOperationLog(SSCBusiOpVO opvo,
			SSCTaskVO avo, String message) {
		SSCTaskLogVO lastOperationLog = SSCTaskAdapter.getLastOperationLog(avo);
		SSCTaskLogVO rs = new SSCTaskLogVO();
		if (opvo.getBeginstate() == null) {
			if (lastOperationLog != null) {
				rs.setBeginstate(lastOperationLog.getEndstate());
			}
		} else {
			rs.setBeginstate(opvo.getBeginstate());
		}
		if (lastOperationLog != null) {
			rs.setBegintime(lastOperationLog.getEndtime());
		} else {
			rs.setBegintime(avo.getReceivetime());
		}

		rs.setPk_busiop(opvo.getPk_busioperation());
		rs.setMessage(message);
		rs.setEndstate(opvo.getEndstate());
		rs.setEndtime(new UFDateTime());

		rs.setPk_ssctask(avo.getPk_ssctask());
		if (rs.getBegintime() == null)
			rs.setBegintime(rs.getEndtime());
		rs.setLasttime(Integer.valueOf(UFDateTime.getSecondsBetween(
				rs.getBegintime(), rs.getEndtime())));

		return rs;
	}
}
