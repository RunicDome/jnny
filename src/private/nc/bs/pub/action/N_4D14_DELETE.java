package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.impl.pmpub.servicebase.PMAbstractPfAction;
import nc.itf.arap.forthird.IArapForDGSWService;
import nc.itf.pbm.materialplan.pvt.IMaterialPlanInterface;
import nc.vo.pbm.materialplan.MaterialPlanBillVO;
import nc.vo.pm.proxy.PMProxy;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import net.sf.json.JSONObject;

//物资及服务需求单删除
public class N_4D14_DELETE extends PMAbstractPfAction {
	public N_4D14_DELETE() {
	}

	public Object runComClass(PfParameterVO paraVo) throws BusinessException {
		this.m_tmpVo = paraVo;

		((IMaterialPlanInterface) PMProxy.lookup(IMaterialPlanInterface.class))
				.deleteMaterialPlanVOS((MaterialPlanBillVO[]) getVos());
		// 调用神思系统
		for (MaterialPlanBillVO aggvo : (MaterialPlanBillVO[]) getVos()) {
			if ("SS".equals(aggvo.getParentVO().getHdef2())) {
				IArapForDGSWService util = (IArapForDGSWService) NCLocator
						.getInstance().lookup(IArapForDGSWService.class);
				JSONObject hrres = util.sendSSBillByNCBill(aggvo.getParentVO()
						.getPrimaryKey(), 1);
				if (!"Y".equals(hrres.getString("success"))) {
					throw new BusinessException(hrres.getString("errinfo"));
				}
			}
		}
		return null;
	}
}
