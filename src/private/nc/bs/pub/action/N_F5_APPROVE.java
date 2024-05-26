package nc.bs.pub.action;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.cmp.pub.exception.ExceptionHandler;
import nc.data.param.CommonParam;
import nc.itf.arap.forthird.IArapForDGSWService;
import nc.vo.cmp.BusiStatus;
import nc.vo.cmp.bill.BillAggVO;
import nc.vo.cmp.bill.BillVO;
import nc.vo.cmp.bill.CommonContext;
import nc.vo.cmp.fields.IBillFieldAdapter;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.workflownote.WorkflownoteVO;
import net.sf.json.JSONObject;

//付款结算驳回
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
public class N_F5_APPROVE extends AbstractCompiler2 {
	private Hashtable<String, Object> m_methodReturnHas = new Hashtable();
	private Hashtable<String, Object> m_keyHas = null;

	public N_F5_APPROVE() {
	}

	public Object runComClass(PfParameterVO paraVo) throws BusinessException {
		try {
			if (paraVo.m_preValueVos == null)
				return null;
			Object obj = null;
			this.m_tmpVo = paraVo;

			procFlowBacth(paraVo);

			List<AggregatedValueObject> list = new ArrayList();
			for (AggregatedValueObject aggvo : paraVo.m_preValueVos) {
				Integer bill_status = (Integer) ((IBillFieldAdapter) aggvo)
						.getIBillFieldGet().getAtrrValue(aggvo.getParentVO(),
								"h_billstatus");

				if (bill_status.intValue() == BusiStatus.Audit
						.getBillStatusSubKind()) {
					list.add(aggvo);
				}
			}
			if (list.size() > 0) {
				setParameter("context",
						list.toArray(new AggregatedValueObject[0]));

				obj = runClass(
						"nc.bs.cmp.bill.actions.BillApproveBatchBSAction",
						"approveVOs",
						"&context:nc.vo.pub.AggregatedValueObject[]", paraVo,
						this.m_keyHas);

				if (obj != null)
					this.m_methodReturnHas.put("approveVOs", obj);
				return obj;
			}
			setParameter("context", paraVo.m_preValueVos);
			obj = runClass("nc.bs.cmp.bill.actions.BillApproveBatchBSAction",
					"getNewBills",
					"&context:nc.vo.pub.AggregatedValueObject[]", paraVo,
					this.m_keyHas);

			if (obj != null)
				this.m_methodReturnHas.put("approveVOs", obj);
			// XBX
			WorkflownoteVO note = paraVo.m_workFlow;
			String approveresult = note.getApproveresult();
			String senderman = note.getSenderman();
			AggregatedValueObject[] aggvos = paraVo.m_preValueVos;
			for(AggregatedValueObject aggvo :aggvos){
				BillAggVO agvo = (BillAggVO) aggvo;
				BillVO hVO = (BillVO) agvo.getParentVO();
				int status = hVO.getBill_status();
				if("R".equals(approveresult) && senderman.equals(hVO.getBillmaker())){
					// 如果单据状态为-1 保存态，审批日期为空，则为驳回，调用东港税务接口
					// 调用东港税务系统同步单据状态
					IArapForDGSWService util = (IArapForDGSWService) NCLocator.getInstance().lookup(
							IArapForDGSWService.class);
					// 单据号，组织主键，事件类型，单据类型
					JSONObject res = util.sendBillByNCBill(hVO.getPrimaryKey(),
							hVO.getPk_org(), CommonParam.RECALL, hVO.getBill_type());
					if(!"Y".equals(res.getString("success"))){
						throw new BusinessException("提交税务系统报错："+res.getString("errinfo"));
					}
				}
			}
			return obj;
		} catch (Exception ex) {
			throw ExceptionHandler.handleException(getClass(), ex);
		}
	}

	public String getCodeRemark() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
				"3607mng_0", "03607mng-0360");
	}

	protected void setParameter(String key, Object val) {
		if (this.m_keyHas == null) {
			this.m_keyHas = new Hashtable();
		}
		if (val != null) {
			this.m_keyHas.put(key, val);
		}
	}

	protected void setContext(CommonContext context) throws BusinessException {
	}
}
