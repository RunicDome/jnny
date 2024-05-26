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
import nc.imag.pub.util.ImageServiceUtil;
import nc.itf.arap.forthird.IArapForDGSWService;
import nc.itf.arap.prv.ICheckStatusCallBack;
import nc.vo.arap.basebill.BaseAggVO;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.arap.event.IArapBSEventType;
import nc.vo.arap.pay.AggPayBillVO;
import nc.vo.arap.pay.PayBillVO;
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
@SuppressWarnings({ "rawtypes", "unused" })
public class N_F3_SIGNAL extends AbstractCompiler2 {
	protected Hashtable<String, Object> m_keyHas = null;

	protected void setParameter(String key, Object val) {
		if (m_keyHas == null) {
			m_keyHas = new Hashtable<String, Object>();
		}
		if (val != null) {
			m_keyHas.put(key, val);
		}
	}

	public N_F3_SIGNAL() {
		super();
	}

	/*
	 * 备注：平台编写规则类 接口执行类
	 */
	@Override
	public Object runComClass(PfParameterVO paraVo) throws BusinessException {
		AggregatedValueObject[] m_preValueVos = paraVo.m_preValueVos;
		if (paraVo.m_workFlow != null
				&& "Y".equals(paraVo.m_workFlow.getApproveresult())) {
			for (AggregatedValueObject svo : m_preValueVos) {
				BaseAggVO v = (BaseAggVO) svo;
				String pk_billtype = v.getHeadVO().getPk_tradetype();
				String pk_org = v.getHeadVO().getPk_org();
				String primaryKey = v.getHeadVO().getPrimaryKey();
				try {
					ImageServiceUtil.commitCheckWithImage(pk_billtype,
							primaryKey, pk_org);
					// if(!nc.web.arap.controller.ImageUtils.isCanSubmitForImage(paraVo,v,
					// pk_billtype, pk_org, primaryKey)){
					// throw new BusinessException("单据必须扫描影像，才能提交单据!");
					// }
				} catch (Exception e) {
					throw new BusinessException(e.getMessage());
				}
			}
		}
		if (paraVo.m_workFlow != null
				&& "R".equals(paraVo.m_workFlow.getApproveresult())) {
			for (AggregatedValueObject svo : m_preValueVos) {
				BaseAggVO v = (BaseAggVO) svo;
				// 跳转到活动
				String jmpAct = paraVo.m_workFlow.getTaskInfo().getTask()
						.getJumpToActivity();
				// 流程定义PK
				String pk_def = paraVo.m_workFlow.getTaskInfo().getTask()
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
						// 调用宏景系统驳回
						IArapForDGSWService util = (IArapForDGSWService) NCLocator
								.getInstance()
								.lookup(IArapForDGSWService.class);
						PayBillVO hvo = (PayBillVO) paraVo.m_preValueVo
								.getParentVO();
						// 流程驳回调用宏景系统
						if ("F3-Cxx-XCFKSPD".equals(hvo.getPk_tradetype())) {
							JSONObject hrres = util.sendHJBillByNCBill(hvo,
									"02", paraVo.m_workFlow.checknote);
							if (!"Y".equals(hrres.getString("success"))) {
								throw new BusinessException(
										hrres.getString("errinfo"));
							}
						}
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
			if (((BaseAggVO) paraVo.m_preValueVos[0]).isUpdateForSignal) {
				setParameter("context", paraVo.m_preValueVos);
				runClass("nc.bs.arap.actions.PaybillEditSaveBatchBSAction",
						"updateVOs",
						"&context:nc.vo.pub.AggregatedValueObject[]", paraVo,
						m_keyHas);
			}
		} catch (Exception ex) {
			throw ExceptionHandler.handleException(this.getClass(), ex);
		}

		try {
			if (paraVo.m_preValueVos == null)
				return null;
			Object obj = null;// paraVo.m_workFlow.getTaskInfo().getTask().setStatus(1);
			super.m_tmpVo = paraVo;
			// 批量
			Hashtable procFlowBacth = procFlowBacth(paraVo);
			List<AggregatedValueObject> list = new ArrayList<AggregatedValueObject>();
			int i = 0;
			for (AggregatedValueObject aggvo : paraVo.m_preValueVos) {
				if (paraVo.m_workFlow == null
						|| !"R".equals(paraVo.m_workFlow.getApproveresult())) {
					NCLocator.getInstance().lookup(ICheckStatusCallBack.class)
							.changeApproveStatus(aggvo);
				}
				if (procFlowBacth.containsKey(Integer.valueOf(i++).toString())) {
					continue;// 审批未通过，或者是会签。
				}
				boolean isWorkFlowFinalNode = isWorkFlowFinalNode(paraVo);
				if (isWorkFlowFinalNode) {
					// start by hubina
					// 工作流最终环节，将审批日期修改为当前服务器时间，解决共享驳回后再次修改制单日期报审批日期不能早于单据日期的问题
					if (paraVo.m_workFlow != null
							&& "Y".equals(paraVo.m_workFlow.getApproveresult())) {
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
						"nc.bs.arap.actions.PaybillApproveBatchBSAction",
						"approveVOs",
						"&context:nc.vo.pub.AggregatedValueObject[]", paraVo,
						m_keyHas);

				if (BillEnumCollection.InureSign.OKINURE.VALUE != ((BaseBillVO) ((AggregatedValueObject[]) obj)[0]
						.getParentVO()).getEffectstatus()) {
					InvocationInfoProxy.getInstance().setProperty(
							"reids_event", "true");
					EventDispatcher.fireEvent(new BusinessEvent(
							ArapConstant.ARAP_MDID_PAYABLEBILL,
							IArapBSEventType.TYPE_APIVADD_AFTER, obj));
					Logger.error("公布业务事件 201615 通知redis更新缓存");
				}
				// luozhw3 校验单据状态和审批状态
				for (AggregatedValueObject aggvo : paraVo.m_preValueVos) {
					AggPayBillVO bxvo = (AggPayBillVO) aggvo;
					ArapFlowCheckUtil.checkArapbillFlowStatus(bxvo
							.getParentVO().getPrimaryKey(), bxvo.getParentVO()
							.getAttributeValue("pk_billtype").toString());
					boolean isWorkFlowFinalNode = isWorkFlowFinalNode(paraVo);
					if (isWorkFlowFinalNode) {
						// 调用东港税务系统同步单据状态
						IArapForDGSWService util = (IArapForDGSWService) NCLocator
								.getInstance().lookup(IArapForDGSWService.class);
						PayBillVO hvo = (PayBillVO) aggvo.getParentVO();
						// 调用宏景系统
						if ("F3-Cxx-XCFKSPD".equals(hvo.getPk_tradetype())) {
							JSONObject hrres = util.sendHJBillByNCBill(hvo, "01", "");
							if (!"Y".equals(hrres.getString("success"))) {
								throw new BusinessException(hrres.getString("errinfo"));
							}
						}
					}
				}
				return obj;
			}

			InvocationInfoProxy.getInstance()
					.setProperty("reids_event", "true");
			EventDispatcher.fireEvent(new BusinessEvent(
					ArapConstant.ARAP_MDID_PAYABLEBILL,
					IArapBSEventType.TYPE_APIVADD_AFTER, paraVo.m_preValueVos));
			Logger.error("公布业务事件 201615 通知redis更新缓存");
			// luozhw3 校验单据状态和审批状态
			for (AggregatedValueObject aggvo : paraVo.m_preValueVos) {
				AggPayBillVO bxvo = (AggPayBillVO) aggvo;
				ArapFlowCheckUtil.checkArapbillFlowStatus(bxvo.getParentVO()
						.getPrimaryKey(),
						bxvo.getParentVO().getAttributeValue("pk_billtype")
								.toString());
			}			
			// XBX新增调用OA接口
			// if (paraVo.m_workFlow != null) {
			// String billtype = paraVo.m_workFlow.getPk_billtype();// 交易类型
			// int status = paraVo.m_workFlow.getStatus();//
			// String checkMan = (String) new HYPubBO().findColValue(
			// "sys_config", "config_value",
			// "config_key='oa-token-approvers'");
			// String def2 = (String) new HYPubBO().findColValue("org_orgs",
			// "def2", "pk_org = '" + paraVo.m_workFlow.getPk_org()
			// + "'");
			// if (StringUtils.equals(def2, "1")) {
			// String mesnote = paraVo.m_workFlow.getMessagenote();
			//
			// // 调用OA接口
			// if (approvestatus == 0
			// && "1001A2100000000QQF9I".equals(senderman)
			// && "1001A2100000000QQF9I".equals(checkman)) {
			// // PfUtilTools.approveSilently(billtype,
			// // paraVo.m_workFlow.getBillid(), "Y", "", checkMan,
			// // null,
			// // IPFActionName.APPROVE);
			// if (null != paraVo.m_workFlow.getCheckman()
			// && checkMan.contains(paraVo.m_workFlow
			// .getCheckman())
			// && "Y".equals(approveresult)
			// && ("D3".equals(billtype)
			// || "F3-Cxx-01".equals(billtype)
			// || "F3-Cxx-SDRFKSPD".equals(billtype)
			// || "F3-Cxx-MTWGRFKSPD".equals(billtype)
			// || "F3-Cxx-RZHK".equals(billtype)
			// || "F3-Cxx-GDZC".equals(billtype) || "F3-Cxx-FYFKSPD"
			// .equals(billtype))) {
			// Logger.error("approveresult==" + approveresult
			// + "senderman==" + senderman
			// + "approvestatus==" + approvestatus
			// + "checkman==" + checkman);
			// appr(paraVo.m_preValueVos);
			// }
			// }
			// }
			// }

			// ==================================================
			return paraVo.m_preValueVos;
		} catch (Exception ex) {
			throw ExceptionHandler.handleException(this.getClass(), ex);
		}
	}

	/*
	 * private void appr(AggregatedValueObject[] aggVO) throws BusinessException
	 * { // TODO Auto-generated method stub String url = (String) new
	 * HYPubBO().findColValue("sys_config", "config_value",
	 * "config_key='oa-url'");
	 * 
	 * String zoomkeyAuthToken = (String) new HYPubBO().findColValue(
	 * "sys_config", "config_value", "config_key='zq-token'");
	 * 
	 * Logger.error("url==" + url); for (AggregatedValueObject aggvo : aggVO) {
	 * AggPayBillVO fkvo = (AggPayBillVO) aggvo; PayBillVO hvo = (PayBillVO)
	 * fkvo.getParent(); if (hvo.getBilldate().compareTo(new
	 * UFDate("2021-10-13")) > 0) { Logger.error("hvo.getPrimaryKey()==" +
	 * hvo.getPrimaryKey()); WorkFlowBill[] vo = (WorkFlowBill[]) new HYPubBO()
	 * .queryByCondition(WorkFlowBill.class, "pk_bill='" + hvo.getPrimaryKey() +
	 * "'"); if (getDef2(hvo.getPk_org()) == null) { return; } if (vo == null ||
	 * vo.length <= 0) { throw new BusinessException("未获取到WorkFlowBillVO！"); }
	 * // PM_WORKFLOW_BILL 自定义项2作为判断是否调用接口 XBX 20211209新增 if (vo[0].getDef2() ==
	 * null || !"NO".equals(vo[0].getDef2())) { String requestid =
	 * vo[0].getRequestid() + ""; Map<String, String> params = new HashMap<>();
	 * params.put("requestid", requestid); Map<String, String> headers = new
	 * HashMap<>(); headers.put("Content-Type",
	 * "application/x-www-form-urlencoded"); String back =
	 * HttpClient.httpPostForm(url, params, headers, "utf-8"); JSONObject res =
	 * JSONObject.fromObject(back); String success =
	 * res.getString("error_code"); if (!"0".equals(success)) { throw new
	 * BusinessException("调用OA获取NC更新数据通知接口出错：" + res.getString("error_msg")); }
	 * WorkFlowBill voa = vo[0]; voa.setDef2("NO");
	 * voa.setStatus(VOStatus.UPDATED); new HYPubBO().update(voa); } } } }
	 */

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
