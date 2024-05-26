package nc.bs.pub.action;

import java.util.Hashtable;

import nc.bs.framework.common.NCLocator;
import nc.cmp.pub.exception.ExceptionHandler;
import nc.data.param.CommonParam;
import nc.itf.arap.forthird.IArapForDGSWService;
import nc.vo.cmp.bill.CommonContext;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.compiler.PfParameterVO;
import net.sf.json.JSONObject;

// 付款结算删除
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
public class N_F5_DELETE extends nc.bs.pub.compiler.AbstractCompiler2 {

	private Hashtable<String, Object> m_methodReturnHas = new Hashtable();
	private Hashtable<String, Object> m_keyHas = null;

	public N_F5_DELETE() {
	}

	public Object runComClass(PfParameterVO paraVo) throws BusinessException {
		try {
			if (paraVo.m_preValueVos == null)
				return null;
			Object obj = null;
			this.m_tmpVo = paraVo;

			setParameter("context", paraVo.m_preValueVos);

			obj = runClass("nc.bs.cmp.bill.actions.BillDeleteBatchBSAction",
					"deleteVOs", "&context:nc.vo.pub.AggregatedValueObject[]",
					paraVo, this.m_keyHas);

			if (obj != null)
				this.m_methodReturnHas.put("deleteVOs", obj);
			// 调用东港税务系统同步单据状态
			AggregatedValueObject[] bills = paraVo.m_preValueVos;
			for(AggregatedValueObject aggvo : bills){
				CircularlyAccessibleValueObject parentVO = aggvo.getParentVO();
				IArapForDGSWService util = (IArapForDGSWService) NCLocator.getInstance().lookup(
						IArapForDGSWService.class);
				JSONObject res = util.sendBillByNCBill(parentVO.getPrimaryKey(),
						parentVO.getAttributeValue("pk_org").toString(), CommonParam.DELETE, parentVO.getAttributeValue("bill_type").toString());
				if (!"Y".equals(res.getString("success"))) {
					throw new BusinessException(res.getString("errinfo"));
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
