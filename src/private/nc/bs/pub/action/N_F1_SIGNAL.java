package nc.bs.pub.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import nc.bs.arap.util.ArapFlowCheckUtil;
import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pf.pub.PfDataCache;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.bs.pub.pf.CheckStatusCallbackContext;
import nc.bs.trade.business.HYPubBO;
import nc.fipub.framework.base.FIStringUtil;
import nc.itf.arap.prv.ICheckStatusCallBack;
import nc.vo.arap.basebill.BaseAggVO;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.arap.event.IArapBSEventType;
import nc.vo.arap.payable.AggPayableBillVO;
import nc.vo.arap.payable.PayableBillVO;
import nc.vo.arap.pub.ArapConstant;
import nc.vo.arap.pub.BillEnumCollection;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.org.OrgVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pubapp.AppContext;
import nc.vo.wfengine.core.activity.ActivityEx;
import nc.vo.wfengine.core.data.DataField;
import nc.vo.wfengine.core.parser.XPDLParserException;
import nc.vo.wfengine.core.workflow.BasicWorkflowProcess;
import nc.vo.wfengine.definition.ActivityTypeEnum;
import nc.vo.wfengine.pub.WfTaskType;
import nc.ws.intf.HttpClient;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONObject;

/**
 * 工作流流程驱动 2014-11-19
 * 
 * @author shiwla
 * 
 */
@SuppressWarnings("rawtypes")
public class N_F1_SIGNAL extends AbstractCompiler2 {
	protected Hashtable<String, Object> m_keyHas = null;

	protected void setParameter(String key, Object val) {
		if (m_keyHas == null) {
			m_keyHas = new Hashtable<String, Object>();
		}
		if (val != null) {
			m_keyHas.put(key, val);
		}
	}

	public N_F1_SIGNAL() {
		super();
	}

	/*
	 * 备注：平台编写规则类 接口执行类
	 */
	@Override
	public Object runComClass(PfParameterVO vo) throws BusinessException {

		AggregatedValueObject[] m_preValueVos = vo.m_preValueVos;
		if (vo.m_workFlow != null
				&& "Y".equals(vo.m_workFlow.getApproveresult())) {
			// for (AggregatedValueObject svo : m_preValueVos) {
			// BaseAggVO v = (BaseAggVO) svo;
			// String pk_billtype = v.getHeadVO().getPk_tradetype();
			// String pk_org = v.getHeadVO().getPk_org();
			// String primaryKey = v.getHeadVO().getPrimaryKey();
			// try {

			// ImageServiceUtil.commitCheckWithImage(pk_billtype,
			// primaryKey, pk_org);
			//
			// NCLocator.getInstance().lookup(IInvoiceForPubService.class)
			// .commitCheckWithImage(pk_org, primaryKey);
			// if(!nc.web.arap.controller.ImageUtils.isCanSubmitForImage(vo,v,
			// pk_billtype, pk_org, primaryKey)){
			// throw new BusinessException("单据必须扫描影像，才能提交单据!");
			// }
			// } catch (Exception e) {
			// throw new BusinessException(e.getMessage());
			// }
			// }
		}
		

		// ==================================================
		if (vo.m_workFlow != null
				&& "R".equals(vo.m_workFlow.getApproveresult())) {
			for (AggregatedValueObject svo : m_preValueVos) {
				BaseAggVO v = (BaseAggVO) svo;
				// 跳转到活动
				String jmpAct = vo.m_workFlow.getTaskInfo().getTask()
						.getJumpToActivity();
				// 流程定义PK
				String pk_def = vo.m_workFlow.getTaskInfo().getTask()
						.getWfProcessDefPK();
				BasicWorkflowProcess process;
				try {
					process = PfDataCache.getWorkflowProcess(pk_def);
					// 判断是否制单
					if (process.findActivityByID(jmpAct) != null
							&& process.findActivityByID(jmpAct)
									.getActivityType() == ActivityTypeEnum.Makebill
									.getIntValue()) {
						ICheckStatusCallBack checkStatusCallBackImpl = NCLocator
								.getInstance().lookup(
										ICheckStatusCallBack.class);
						CheckStatusCallbackContext cscc = new CheckStatusCallbackContext();
						cscc.setApproveDate(FIStringUtil.coverToString(v
								.getHeadVO().getApprovedate()));
						cscc.setApproveId(FIStringUtil.coverToString(v
								.getHeadVO().getApprover()));
						cscc.setCheckStatus(BillEnumCollection.ApproveStatus.NOSTATE.VALUE);
						cscc.setReject(false);
						checkStatusCallBackImpl
								.checkStatus(v.getHeadVO(), cscc);
					} else {
						NCLocator.getInstance()
								.lookup(ICheckStatusCallBack.class)
								.changeApproveStatus(svo);
					}
				} catch (XPDLParserException e) {
					// ignore exception
				}
			}
		}
		try {
			if (vo.m_preValueVos == null)
				return null;
			if (((BaseAggVO) vo.m_preValueVos[0]).isUpdateForSignal) {
				setParameter("context", vo.m_preValueVos);
				runClass("nc.bs.arap.actions.PayablebillEditBatchBSAction",
						"updateVOs",
						"&context:nc.vo.pub.AggregatedValueObject[]", vo,
						m_keyHas);
			}
		} catch (Exception ex) {
			throw ExceptionHandler.handleException(this.getClass(), ex);

		}
		try {
			if (vo.m_preValueVos == null)
				return null;
			Object obj = null;// paraVo.m_workFlow.getTaskInfo().getTask().setStatus(1);
			super.m_tmpVo = vo;
			// 批量
			Hashtable procFlowBacth = procFlowBacth(vo);
			List<AggregatedValueObject> list = new ArrayList<AggregatedValueObject>();
			int i = 0;
			for (AggregatedValueObject aggvo : vo.m_preValueVos) {
				if (vo.m_workFlow == null
						|| !"R".equals(vo.m_workFlow.getApproveresult())) {
					NCLocator.getInstance().lookup(ICheckStatusCallBack.class)
							.changeApproveStatus(aggvo);
				}
				if (procFlowBacth.containsKey(Integer.valueOf(i++).toString())) {
					continue;// 审批未通过，或者是会签。
				}
				boolean isWorkFlowFinalNode = isWorkFlowFinalNode(vo);
				if (isWorkFlowFinalNode) {
					// start by hubina
					// 工作流最终环节，将审批日期修改为当前服务器时间，解决共享驳回后再次修改制单日期报审批日期不能早于单据日期的问题
					if (vo.m_workFlow != null
							&& "Y".equals(vo.m_workFlow.getApproveresult())) {
						BaseBillVO baseBillVO = (BaseBillVO) aggvo
								.getParentVO();
						baseBillVO.setApprovedate_bak(baseBillVO
								.getApprovedate());
						baseBillVO.setApprovedate(AppContext.getInstance()
								.getServerTime());
					}
					// end by hubina
					list.add(aggvo);
				}
			}
			if (list.size() > 0) {
				// 批量操作
				setParameter("context",
						list.toArray(new AggregatedValueObject[0]));
				obj = runClass(
						"nc.bs.arap.actions.PayablebillApproveBatchBSAction",
						"approveVOs",
						"&context:nc.vo.pub.AggregatedValueObject[]", vo,
						m_keyHas);
				// luozhw3 校验单据状态和审批状态
				for (AggregatedValueObject aggvo : vo.m_preValueVos) {
					AggPayableBillVO bxvo = (AggPayableBillVO) aggvo;
					ArapFlowCheckUtil.checkArapbillFlowStatus(bxvo
							.getParentVO().getPrimaryKey(), bxvo.getParentVO()
							.getAttributeValue("pk_billtype").toString());
				}
				return obj;
			}

			InvocationInfoProxy.getInstance()
					.setProperty("reids_event", "true");
			EventDispatcher.fireEvent(new BusinessEvent(
					ArapConstant.ARAP_MDID_PAYABLEBILL,
					IArapBSEventType.TYPE_APIVADD_AFTER, vo.m_preValueVos));
			Logger.error("公布业务事件 201615 通知redis更新缓存");
			// luozhw3 校验单据状态和审批状态
			for (AggregatedValueObject aggvo : vo.m_preValueVos) {
				AggPayableBillVO bxvo = (AggPayableBillVO) aggvo;
				ArapFlowCheckUtil.checkArapbillFlowStatus(bxvo.getParentVO()
						.getPrimaryKey(),
						bxvo.getParentVO().getAttributeValue("pk_billtype")
								.toString());
			}
			return vo.m_preValueVos;
		} catch (Exception ex) {
			throw ExceptionHandler.handleException(this.getClass(), ex);
		}
	}

	public static boolean isWorkFlowFinalNode(PfParameterVO vo)
			throws BusinessException {
		if (vo.m_workFlow == null) {
			return false;
		}
		List argsList = vo.m_workFlow.getApplicationArgs();
		int i = vo.m_workFlow.getTaskInfo().getTask().getTaskType();
		Iterator iterator;
		if ((argsList != null) && (argsList.size() > 0)) {
			for (iterator = argsList.iterator(); iterator.hasNext();) {
				DataField df = (DataField) iterator.next();
				Object value = df.getInitialValue();
				if (value != null) {
					if (("isWorkFlowFinalNode".equals(df.getName()))
							&& (UFBoolean.valueOf(value.toString())
									.booleanValue())
							&& i != WfTaskType.Backward.getIntValue())
						return true;
				}
			}
		}
		// 工作流最终环节设置本人自动审批，判断是否最终环节参数新方式 chenshfc bgy
		String activityID = vo.m_workFlow.getTaskInfo().getTask()
				.getActivityID();
		String wfDef = vo.m_workFlow.getTaskInfo().getTask()
				.getWfProcessDefPK();
		BasicWorkflowProcess wfProcess = null;
		try {
			wfProcess = PfDataCache.getWorkflowProcess(wfDef);
		} catch (XPDLParserException e) {
			new BusinessException(e);
		}
		if (wfProcess == null) {
			return false;
		}
		ActivityEx activity = (ActivityEx) wfProcess
				.findActivityByID(activityID);
		DataField datafield = getActivityArgs(activity, "isWorkFlowFinalNode");
		if (datafield != null) {
			Object value = datafield.getInitialValue();
			if (value != null) {
				if ("isWorkFlowFinalNode".equals(datafield.getName())
						&& (UFBoolean.valueOf(value.toString()).booleanValue())
						&& i != WfTaskType.Backward.getIntValue()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 获取流程参数（例如：是否最终环节）
	 * 
	 * @param act
	 * @param argName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static DataField getActivityArgs(ActivityEx act, String argName) {
		if (act == null) {
			return null;
		}
		List<DataField> bizArgs = act.getApplicationArgs();
		if ((bizArgs != null) && (bizArgs.size() > 0)) {
			for (DataField d : bizArgs) {
				if (d.getName().equals(argName)) {
					return d;
				}
			}
		}
		return null;
	}

	private void appr(AggregatedValueObject[] aggVO) throws BusinessException {
		// TODO Auto-generated method stub
		String url = (String) new HYPubBO().findColValue("sys_config",
				"config_value", "config_key='oa-url'");
		for (AggregatedValueObject aggvo : aggVO) {
			AggPayableBillVO ykvo = (AggPayableBillVO) aggvo;
			PayableBillVO hvo = (PayableBillVO) ykvo.getParent();
			if(hvo.getBilldate().compareTo(new UFDate("2021-10-13")) > 0){
				WorkFlowBill[] vo = (WorkFlowBill[]) new HYPubBO().queryByCondition(WorkFlowBill.class, "pk_bill='"+hvo.getPrimaryKey()+"'");
				if(getDef2(hvo.getPk_org()) == null){
					return ;
				}
				if(vo == null || vo.length <= 0){
					throw new BusinessException("未获取到WorkFlowBillVO！");
				}
				// PM_WORKFLOW_BILL 自定义项2作为判断是否调用接口   XBX  20211209新增
				if(vo[0].getDef2() == null || !"NO".equals(vo[0].getDef2())){
					String requestid = vo[0].getRequestid() + "";
					Map<String, String> params = new HashMap<>();
					params.put("requestid", requestid);
					Map<String, String> headers = new HashMap<>();
					headers.put("Content-Type", "application/x-www-form-urlencoded");
					String back = HttpClient
							.httpPostForm(url, params, headers, "utf-8");
					JSONObject res = JSONObject.fromObject(back);
					String success = res.getString("error_code");
					if (!"0".equals(success)) {
						throw new BusinessException("调用OA获取NC更新数据通知接口出错："
								+ res.getString("error_msg"));
					}
					WorkFlowBill voa = vo[0];
					voa.setDef2("NO");
					voa.setStatus(VOStatus.UPDATED);
					new HYPubBO().update(voa);
				}
			}
		}
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
}
