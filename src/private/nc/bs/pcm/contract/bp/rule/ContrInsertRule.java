package nc.bs.pcm.contract.bp.rule;

import nc.impl.pubapp.pattern.rule.IRule;
import nc.itf.pbm.materialplan.pvt.IMaterialPlan;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pbm.materialplan.MaterialPlanBillVO;
import nc.vo.pbm.materialplan.MaterialPlanHeadVO;
import nc.vo.pcm.contract.ContrHeadVO;
import nc.vo.pcm.contract.ContractBillVO;
import nc.vo.pm.proxy.PMProxy;
import nc.vo.pm.util.ArrayUtil;
import nc.vo.pm.util.StringUtil;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

// XBX 20230328 多编码拆单编码，修改清单保存规则
@SuppressWarnings("unused")
public class ContrInsertRule implements IRule<ContractBillVO> {
	public ContrInsertRule() {
	}

	public void process(ContractBillVO[] vos) {
		if (ArrayUtil.isEmpty(vos)) {
			return;
		}
		String srcID = vos[0].getParentVO().getSrc_pk_bill();
		if (StringUtil.isEmpty(srcID)) {
			return;
		}
		// XBX 修改校验
		String srcType = vos[0].getParentVO().getSrc_bill_type();
		if(StringUtil.isEmpty(srcType) || "4Z01".equals(srcType)){
			return;
		}
		
		MaterialPlanBillVO[] billVOs = getMaterialPlanService()
				.queryLastVersionBillVOByPK(srcID);
		if (ArrayUtil.isEmpty(billVOs)) {
			ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl
					.getNCLangRes()
					.getStrByID("subcontract_0", "04820003-0315"));
		}
		int billStatus = billVOs[0].getParentVO().getBill_status().intValue();
		if (14 != billStatus) {
			ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl
					.getNCLangRes()
					.getStrByID("subcontract_0", "04820003-0316"));
		}
	}

	private IMaterialPlan getMaterialPlanService() {
		return (IMaterialPlan) PMProxy.lookup(IMaterialPlan.class);
	}
}
