package nc.bs.pub.action;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import nc.bs.arap.util.ArapFlowCheckUtil;
import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.itf.arap.forthird.IArapForDGSWService;
import nc.vo.arap.BusiStatus;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.arap.event.IArapBSEventType;
import nc.vo.arap.pay.AggPayBillVO;
import nc.vo.arap.pay.PayBillVO;
import nc.vo.arap.pub.ArapConstant;
import nc.vo.arap.pub.BillEnumCollection;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import net.sf.json.JSONObject;

/**
 * 备注：主付款单的审核 单据动作执行中的动态执行类的动态执行类。
 * 
 * 创建日期：(2006-11-17)
 * 
 * @author 平台脚本生成
 */
public class N_F3_APPROVE extends AbstractCompiler2 {
	// private Hashtable<String, Object> m_methodReturnHas = new
	// Hashtable<String, Object>();
	protected Hashtable<String, Object> m_keyHas = null;

	/*
	 * 备注：平台编写规则类 接口执行类
	 */
	public Object runComClass(PfParameterVO paraVo) throws BusinessException {
		try {

			if (paraVo.m_preValueVos == null)
				return null;
			Object obj = null;// paraVo.m_workFlow.getTaskInfo().getTask().setStatus(1);
			super.m_tmpVo = paraVo;
			// 批量
			procFlowBacth(paraVo);
			// if (ret.keySet().size()!=0){
			// return paraVo.m_preValueVos;
			// }
			List<AggregatedValueObject> list = new ArrayList<AggregatedValueObject>();
			for (AggregatedValueObject aggvo : paraVo.m_preValueVos) {
				Integer bill_status = ((BaseBillVO) aggvo.getParentVO())
						.getBillstatus2();
				// 已审核状态
				if (bill_status != null
						&& bill_status.intValue() == BusiStatus.Audit
								.getBillStatusSubKind()) {
					list.add(aggvo);
				}
			}
			if (list.size() > 0) {
				/*
				 * From: 李瑜 To: liaobx Cc: twei ; lipy Sent: Tuesday, August 03,
				 * 2010 6:51 PM Subject: 收款单监听事件问题
				 * 
				 * 1、监听客户收款单审核前事件，发现传入的收款单的单据状态（billstatus=1）审核态，
				 * 对于审核前的收款单状态不应该是自由态或者审批中状态？请检查监听事件是否正确
				 * 
				 * * BillStatusUtils.refreshApvStatus2ApvingStatus(list);
				 * 已经写到{@link
				 * nc.bs.arap.actions.GatheringbillApproveBatchBSAction
				 * #doBeforeApprove}中
				 */
				// BillStatusUtils.refreshApvStatus2ApvingStatus(list);
				// 批量操作
				setParameter("context",
						list.toArray(new AggregatedValueObject[0]));
				obj = runClass(
						"nc.bs.arap.actions.PaybillApproveBatchBSAction",
						"approveVOs",
						"&context:nc.vo.pub.AggregatedValueObject[]", paraVo,
						m_keyHas);
				// if (obj != null)
				// m_methodReturnHas.put("approveVOs", obj);
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
				// 调用东港税务系统同步单据状态
				IArapForDGSWService util = (IArapForDGSWService) NCLocator
						.getInstance().lookup(IArapForDGSWService.class);
				PayBillVO hvo = (PayBillVO) bxvo.getParentVO();
				// 调用宏景系统
				if ("F3-Cxx-XCFKSPD".equals(hvo.getPk_tradetype())) {
					JSONObject hrres = util.sendHJBillByNCBill(hvo, "01", "");
					if (!"Y".equals(hrres.getString("success"))) {
						throw new BusinessException(hrres.getString("errinfo"));
					}
				}
			}
			return paraVo.m_preValueVos;
		} catch (Exception ex) {
			throw ExceptionHandler.handleException(this.getClass(), ex);

		}
	}

	public String getCodeRemark() {
		return " arap action script not allowed to modify ,all rights reserved!";
	}

	protected void setParameter(String key, Object val) {
		if (m_keyHas == null) {
			m_keyHas = new Hashtable<String, Object>();
		}
		if (val != null) {
			m_keyHas.put(key, val);
		}
	}
}
