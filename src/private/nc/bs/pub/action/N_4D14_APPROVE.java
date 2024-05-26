package nc.bs.pub.action;

import java.util.HashMap;

import nc.bs.framework.common.NCLocator;
import nc.impl.pmpub.servicebase.PMAbstractPfAction;
import nc.itf.arap.forthird.IArapForDGSWService;
import nc.itf.pbm.materialplan.pvt.IMaterialPlanInterface;
import nc.pubitf.para.SysInitQuery;
import nc.vo.pbm.materialplan.MaterialPlanBillVO;
import nc.vo.pbm.materialplan.MaterialPlanHeadVO;
import nc.vo.pm.proxy.PMProxy;
import nc.vo.pm.util.StringUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pubapp.pattern.tool.performance.DeepCloneTool;
import net.sf.json.JSONObject;

// 物资及服务需求单审批
@SuppressWarnings("unused")
public class N_4D14_APPROVE extends PMAbstractPfAction {
	public N_4D14_APPROVE() {
	}

	public Object runComClass(PfParameterVO vo) throws BusinessException {
		DeepCloneTool cloneTool = new DeepCloneTool();

		this.m_tmpVo = vo;
		String pk_org = vo.m_pkOrg;

		String approveAndRelease = SysInitQuery.getParaString(pk_org, "PBM05");

		if (StringUtil.isEqual(approveAndRelease, "Y")) {
			AggregatedValueObject[] vos = getVos();

			((IMaterialPlanInterface) PMProxy
					.lookup(IMaterialPlanInterface.class))
					.approveMaterialPlanVOS((MaterialPlanBillVO[]) vos, vo);

			IMaterialPlanInterface materialPlanService = (IMaterialPlanInterface) PMProxy
					.lookup(IMaterialPlanInterface.class);

			MaterialPlanBillVO[] billVOs = (MaterialPlanBillVO[]) vos;
			MaterialPlanBillVO[] originBillVOs = (MaterialPlanBillVO[]) vo
					.getCustomPropertyBatch().get("originVOs");

			if (StringUtil.isEmpty(originBillVOs[0].getParentVO()
					.getPk_mater_plan())) {
				originBillVOs = (MaterialPlanBillVO[]) cloneTool
						.deepClone(billVOs);
			}

			for (MaterialPlanBillVO materialPlanVO : billVOs) {
				if ((materialPlanVO.getParentVO().getBill_status().intValue() == 3)
						|| (materialPlanVO.getParentVO().getBill_status()
								.intValue() == 1)) {
					materialPlanVO.getParentVO().setBill_status(
							Integer.valueOf(14));

					materialPlanVO.getParentVO().setStatus(1);
					// 调用神思系统
					if ("SS".equals(materialPlanVO.getParentVO().getHdef2())) {
						IArapForDGSWService util = (IArapForDGSWService) NCLocator
								.getInstance()
								.lookup(IArapForDGSWService.class);
						JSONObject hrres = util
								.sendSSBillByNCBill(materialPlanVO
										.getParentVO().getPrimaryKey(), 2);
						if (!"Y".equals(hrres.getString("success"))) {
							throw new BusinessException(
									hrres.getString("errinfo"));
						}
					}
				} else {
					return null;
				}
			}

			return materialPlanService.releaseMaterialPlanVOS(billVOs,
					originBillVOs);
		}

		return ((IMaterialPlanInterface) PMProxy
				.lookup(IMaterialPlanInterface.class)).approveMaterialPlanVOS(
				(MaterialPlanBillVO[]) getVos(), vo);
	}
}
