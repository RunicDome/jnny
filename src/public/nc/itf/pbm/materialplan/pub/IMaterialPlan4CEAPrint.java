package nc.itf.pbm.materialplan.pub;

import nc.vo.pbm.materialplan.MaterialPlanBodyVO;

/**
 * 物资为预算CEA打印提供的查询接口
 * @version 6.5
 * @since 6.5
 * @author luoke
 * @time 2014-5-15
 */
public interface IMaterialPlan4CEAPrint {
	/**
	 * 为项目预算CEA_B打印提供的查询
	 * 1.已发布状态的最新版本的物资及服务需求单
	 * 2.表体总价为最大的前10个实物类物料
	 * 
	 * @param pk_project 项目pk
	 * 
	 * @return 
	 */
	public MaterialPlanBodyVO[] queryMaterialPlan4CEA_B(String pk_project);
	
	
	public MaterialPlanBodyVO[] queryMaterialPlan4CEA_D(String pk_project);
	
	public MaterialPlanBodyVO[] queryMaterialPlan4CEA_DSPEC(String pk_project);
}
