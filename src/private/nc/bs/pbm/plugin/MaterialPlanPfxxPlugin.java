package nc.bs.pbm.plugin;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pfxx.ISwapContext;
import nc.bs.pfxx.plugin.AbstractPfxxPlugin;
import nc.bs.trade.business.HYPubBO;
import nc.itf.pbm.materialplan.pvt.IMaterialPlanInterface;
import nc.vo.pbm.materialplan.MaterialPlanBillVO;
import nc.vo.pbm.materialplan.MaterialPlanBodyVO;
import nc.vo.pbm.materialplan.MaterialPlanHeadVO;
import nc.vo.pfxx.auxiliary.AggxsysregisterVO;
import nc.vo.pfxx.util.PfxxPluginUtils;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

public class MaterialPlanPfxxPlugin extends AbstractPfxxPlugin {
	private IMaterialPlanInterface service = null;

	@Override
	protected Object processBill(Object vo, ISwapContext swapContext,
			AggxsysregisterVO aggxsysvo) throws BusinessException {
		// TODO Auto-generated method stub
		if (vo == null) {
			return null;
		}
		String pk = null;
		MaterialPlanBillVO aggVO = (MaterialPlanBillVO) vo;
		MaterialPlanHeadVO hvo = aggVO.getParentVO();
		MaterialPlanBodyVO[] bvos = (MaterialPlanBodyVO[]) aggVO
				.getChildrenVO();
		hvo.setHdef2("SS");
		// 字段校验
		if (bvos == null) {
			throw new BusinessException("明细数据不能为空！");
		}
		if (hvo.getPk_project() == null) {
			throw new BusinessException("项目不能为空！");
		}
		if (hvo.getPk_org() == null || hvo.getPk_org_v() == null) {
			throw new BusinessException("项目组织不能为空！");
		}
		if (hvo.getBillmaker() == null || hvo.getCreator() == null) {
			throw new BusinessException("制单人或创建人不能为空！");
		}
		if (hvo.getBillmaketime() == null || hvo.getCreationtime() == null) {
			throw new BusinessException("制单或创建人不能为空！");
		}
		if (hvo.getReq_make_date() == null) {
			throw new BusinessException("需求编制日期不能为空！");
		}
		if (hvo.getBusi_type() == null || hvo.getTransi_type() == null
				|| hvo.getPk_transitype() == null) {
			throw new BusinessException("单据类型或交易类型不能为空！");
		}
		if (hvo.getPk_currtype() == null) {
			throw new BusinessException("币种不能为空！");
		}
		if (hvo.getContr_version() == null
				|| hvo.getContr_version().toDouble() != 1) {
			throw new BusinessException("表头当前版本必须等于1！");
		}
		for (MaterialPlanBodyVO bvo : bvos) {
			if (bvo.getRowno() == null) {
				throw new BusinessException("行号不能为空！");
			}
			if (bvo.getPk_cbs_node() == null) {
				throw new BusinessException("CBS不能为空！");
			}
			if (bvo.getPk_stockorg() == null) {
				throw new BusinessException("库存组织不能为空！");
			}
			if (bvo.getPk_stockorg_v() == null) {
				throw new BusinessException("库存组织版本不能为空！");
			}
			if (bvo.getNnum() == null) {
				throw new BusinessException("数量不能为空！");
			}
			if (bvo.getPk_material() == null || bvo.getPk_material_v() == null) {
				throw new BusinessException("物料不能为空！");
			}
		}
		InvocationInfoProxy.getInstance().setUserId(hvo.getBillmaker());// 设置默认操作员主键
		// 原项目
		ProjectHeadVO oldvo = (ProjectHeadVO) new HYPubBO().queryByPrimaryKey(
				ProjectHeadVO.class, hvo.getPk_project());
		// 重新翻译项目
		ProjectHeadVO[] newprojectVO = (ProjectHeadVO[]) new HYPubBO()
				.queryByCondition(
						ProjectHeadVO.class,
						" 1= 1 and pk_duty_org = '" + hvo.getPk_org()
								+ "' and project_code = '"
								+ oldvo.getProject_code() + "'");
		hvo.setPk_project(newprojectVO[0].getPrimaryKey());
		for (int i = 0; i < bvos.length; i++) {
			bvos[i].setPk_project(newprojectVO[0].getPrimaryKey());
		}
		try {
			pk = PfxxPluginUtils.queryBillPKBeforeSaveOrUpdate(
					swapContext.getBilltype(), swapContext.getDocID());
			if (StringUtils.isBlank(pk)) {
				aggVO = insertMaterialPlanBillVO(aggVO);
				return aggVO.getPrimaryKey();
			} else {
				throw new BusinessException("暂不支持修改功能！");
			}
		} catch (Exception ex) {
			Logger.error(ex.getMessage(), ex.getCause());
			throw new BusinessException(ex.getMessage(), ex.getCause());
		}
	}

	private MaterialPlanBillVO insertMaterialPlanBillVO(MaterialPlanBillVO aggVO)
			throws BusinessException {
		// TODO Auto-generated method stub
		MaterialPlanBillVO[] aggvos = getService().insertMaterialPlanVOS(
				new MaterialPlanBillVO[] { aggVO });
		return aggvos == null ? null : aggvos[0];
	}

	private IMaterialPlanInterface getService() {
		if (this.service == null) {
			this.service = ((IMaterialPlanInterface) NCLocator.getInstance()
					.lookup(IMaterialPlanInterface.class));
		}
		return this.service;
	}
}
