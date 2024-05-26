package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.impl.pmpub.servicebase.PMAbstractPfAction;
import nc.itf.arap.forthird.IArapForDGSWService;
import nc.itf.pbm.materialadd.pvt.IMaterialPlanAddInterface;
import nc.vo.pbm.materialplanadd.MaterialPlanAddBillVO;
import nc.vo.pm.proxy.PMProxy;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import net.sf.json.JSONObject;

// 物资及服务需求调整单审批
public class N_4D16_APPROVE extends PMAbstractPfAction {
	public N_4D16_APPROVE() {
	}

	public Object runComClass(PfParameterVO paraVo) throws BusinessException {
		this.m_tmpVo = paraVo;
		IMaterialPlanAddInterface materialPlanAddService = (IMaterialPlanAddInterface) PMProxy
				.lookup(IMaterialPlanAddInterface.class);

		MaterialPlanAddBillVO[] billVOs = (MaterialPlanAddBillVO[]) getVos();
		Object obj = null;

		obj = materialPlanAddService.approveMaterialPlanAddVOS(billVOs, paraVo);
		for (MaterialPlanAddBillVO aggvo : billVOs) {
			// 调用神思系统
			if ("SS".equals(aggvo.getParentVO().getHdef2())) {
				IArapForDGSWService util = (IArapForDGSWService) NCLocator
						.getInstance().lookup(IArapForDGSWService.class);
				JSONObject hrres = util.sendSSBillByNCBill(aggvo.getParentVO()
						.getPrimaryKey(), 2);
				if (!"Y".equals(hrres.getString("success"))) {
					throw new BusinessException(hrres.getString("errinfo"));
				}
			}
		}
		return obj;
	}
}
