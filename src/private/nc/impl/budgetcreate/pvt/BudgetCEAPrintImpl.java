package nc.impl.budgetcreate.pvt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.itf.pbm.budget.pvt.IBudgetCEAPrint;
import nc.itf.pbm.materialplan.pub.IMaterialPlan4CEAPrint;
import nc.itf.pmbd.pub.IEPSQuery4CEAPrint;
import nc.md.data.access.DASFacade;
import nc.md.innerservice.MDQueryService;
import nc.md.model.IBean;
import nc.ui.pbm.budget.CEAPrint.CEAPrintConst;
import nc.vo.bd.cbs.CBSNodeVO;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.pub.BDCacheQueryUtil;
import nc.vo.pbm.budget.BudgetBillVO;
import nc.vo.pbm.budget.BudgetCBSBodyVO;
import nc.vo.pbm.budget.BudgetFactorBodyVO;
import nc.vo.pbm.budget.BudgetHeadVO;
import nc.vo.pbm.materialplan.MaterialPlanBodyVO;
import nc.vo.pim.project.ProjectHeadVO;
import nc.vo.pm.proxy.PMProxy;
import nc.vo.pm.util.ArrayUtil;
import nc.vo.pm.util.UFDoubleUtils;
import nc.vo.pmpub.common.PMCommonKeyConst;
import nc.vo.pmpub.common.utils.ExceptionUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.resa.factor.FactorAsoaVO;

/**
 * @version 6.5
 * @author Luoke
 * @date 2014-5-18
 */
public class BudgetCEAPrintImpl implements IBudgetCEAPrint{

	@Override
	public Map<String, Object> initCEA_APrintData(BudgetBillVO billVO) {
		Map<String,Object> omnipotentMap = new HashMap<String,Object>();
		//构建CBS取值数据
		omnipotentMap.put(CEAPrintConst.CBS, getCbsCodeMny(billVO));
		//构建核算要素取值数据
		omnipotentMap.put(CEAPrintConst.FACTOR, getFactorCodeData(billVO)[0]);
		//EPS类型
		int business = PMProxy.lookup(IEPSQuery4CEAPrint.class).queryEpsType(billVO.getParentVO().getPk_project());
		omnipotentMap.put(CEAPrintConst.EPSTYPE, business);
		omnipotentMap.put("projectDef", getProjectDefData(billVO));
		return omnipotentMap;
	}

	@Override
	public Map<String, Object> initCEA_BPrintData(BudgetBillVO billVO) {
		Map<String,Object> omnipotentMap = new HashMap<String,Object>();
		Object[] objs = getFactorCodeData(billVO);
		//构建核算要素取值数据
		omnipotentMap.put(CEAPrintConst.FACTOR, objs[0]);
		//构建核算要素名称取值数据
		omnipotentMap.put(CEAPrintConst.FACTORNAME, objs[1]);
		//构建物资及服务需求单取值数据
		MaterialPlanBodyVO[] matePlanBodyVO = PMProxy.lookup(IMaterialPlan4CEAPrint.class)
				.queryMaterialPlan4CEA_B(billVO.getParentVO().getPk_project());
		omnipotentMap.put(CEAPrintConst.MATERIALPLAN, matePlanBodyVO);
		omnipotentMap.put("projectDef", getProjectDefData(billVO));
		return omnipotentMap;
	}
	
	@Override
	public Map<String, Object> initCEA_CPrintData(BudgetBillVO billVO) {
		Map<String,Object> omnipotentMap = new HashMap<String,Object>();
		Object[] objs = getFactorCodeData(billVO);
		//构建CBS取值数据
		omnipotentMap.put(CEAPrintConst.CBS, getCbsCodeMny(billVO));
		//构建核算要素取值数据
		omnipotentMap.put(CEAPrintConst.FACTOR, objs[0]);
		//构建核算要素名称取收入预算值数据
		omnipotentMap.put(CEAPrintConst.FACTORIN, objs[2]);
		//EPS类型
		int business = PMProxy.lookup(IEPSQuery4CEAPrint.class).queryEpsType(billVO.getParentVO().getPk_project());
		omnipotentMap.put(CEAPrintConst.EPSTYPE, business);
		omnipotentMap.put("projectDef", getProjectDefData(billVO));
		return omnipotentMap;
	}
	
	@Override
	public Map<String, Object> initCEA_DPrintData(BudgetBillVO billVO) {
		Map<String,Object> omnipotentMap = new HashMap<String,Object>();
		Object[] objs = getFactorCodeData(billVO);
		//构建核算要素取值数据
		omnipotentMap.put(CEAPrintConst.FACTOR, objs[0]);
		//构建核算要素名称取值数据
		omnipotentMap.put(CEAPrintConst.FACTORNAME, objs[1]);
		//构建核算要素名称取收入预算值数据
		omnipotentMap.put(CEAPrintConst.FACTORIN, objs[2]);
		//构建物资及服务需求单取值数据
		MaterialPlanBodyVO[] matePlanBodyVO = PMProxy.lookup(IMaterialPlan4CEAPrint.class)
				.queryMaterialPlan4CEA_B(billVO.getParentVO().getPk_project());
		omnipotentMap.put(CEAPrintConst.MATERIALPLAN, matePlanBodyVO);
		//构建打印模板上物资服务需求单的字段值
		MaterialPlanBodyVO[] matePlanBodyVOS = PMProxy.lookup(IMaterialPlan4CEAPrint.class)
				.queryMaterialPlan4CEA_D(billVO.getParentVO().getPk_project());
		omnipotentMap.put(CEAPrintConst.GOODS, matePlanBodyVOS);
		//构建打印模板上物资服务需求单的字段值
		MaterialPlanBodyVO[] matePlanBodyVO4SPEC = PMProxy.lookup(IMaterialPlan4CEAPrint.class)
				.queryMaterialPlan4CEA_DSPEC(billVO.getParentVO().getPk_project());
		omnipotentMap.put(CEAPrintConst.SPEC, matePlanBodyVO4SPEC);
		omnipotentMap.put("projectDef", getProjectDefData(billVO));
		return omnipotentMap;
	}

	/**
	 * 获取CBS页签需要打印的数据
	 * @param billVO
	 * @return
	 */
	private Map<String,UFDouble> getCbsCodeMny(BudgetBillVO billVO){
		BudgetCBSBodyVO[] cbsVOs = (BudgetCBSBodyVO[])billVO.getChildren(BudgetCBSBodyVO.class);
		Map<String,UFDouble> cbsMny = new HashMap<String,UFDouble>();
		List<String> cbsPk = new ArrayList<String>();
		for(BudgetCBSBodyVO cbsVO : cbsVOs){
			cbsPk.add(cbsVO.getPk_cbsnode());
			cbsMny.put(cbsVO.getPk_cbsnode(), cbsVO.getBudget_mny());
		}
		try {
			String[] fileds = new String[]{CBSNodeVO.PK_CBSNODE,CBSNodeVO.CODE};
			CBSNodeVO[] cbsNodeVOs = (CBSNodeVO[]) BDCacheQueryUtil.queryVOsByIDs(CBSNodeVO.class,
					CBSNodeVO.PK_CBSNODE, cbsPk.toArray(new String[0]),fileds);
			for(CBSNodeVO cbsVO : cbsNodeVOs){
				if(cbsMny.get(cbsVO.getPk_cbsnode())!=null){
					UFDouble mny = cbsMny.get(cbsVO.getPk_cbsnode());
					cbsMny.remove(cbsVO.getPk_cbsnode());
					cbsMny.put(cbsVO.getCode(),mny);
				}else{
					cbsMny.put(cbsVO.getCode(), UFDouble.ZERO_DBL);
				}
			}
		} catch (BusinessException e) {
			//抛出异常
			ExceptionUtils.asBusinessExceptionAdpator(e);
		}
		return cbsMny;
	}
	/**
	 * 获取factor页签需要打印的数据
	 * @param billVO
	 * @return
	 */
	private Object[] getFactorCodeData(BudgetBillVO billVO){
		BudgetFactorBodyVO[] factorVOs = (BudgetFactorBodyVO[])billVO.getChildren(BudgetFactorBodyVO.class);
		Object[] omnipotentObj = new Object[3];
		Map<String,UFDouble> factorMny = new HashMap<String,UFDouble>();
		Map<String,String> factorName = new HashMap<String,String>();
		Map<String,UFDouble> factorInbudget = new HashMap<String,UFDouble>();
		List<String> factorPk = new ArrayList<String>();
		for(BudgetFactorBodyVO factorVO : factorVOs){
			if(factorVO.getPk_factor()!=null){
				factorPk.add(factorVO.getPk_factor());
				if(UFDoubleUtils.isNullOrZero(factorMny.get(factorVO.getPk_factor()))){
					factorMny.put(factorVO.getPk_factor(), factorVO.getBudget_mny());
				}else{
					factorMny.put(factorVO.getPk_factor(),UFDoubleUtils.add(factorMny.get(factorVO.getPk_factor()), factorVO.getBudget_mny()));
				}
			}
		}
		// 缓存核算要素对应收入预算map
		for(BudgetFactorBodyVO factorVO : factorVOs){
			if(factorVO.getPk_factor()!=null){
				if(UFDoubleUtils.isNullOrZero(factorInbudget.get(factorVO.getPk_factor()))){
					factorInbudget.put(factorVO.getPk_factor(), factorVO.getIn_budget());
				}else{
					factorInbudget.put(factorVO.getPk_factor(),UFDoubleUtils.add(factorInbudget.get(factorVO.getPk_factor()), factorVO.getIn_budget()));
				}
			}
		}
		FactorAsoaVO[] factorAsoaVOs = null;
		try {
			String[] fileds = new String[]{FactorAsoaVO.PK_FACTORASOA,FactorAsoaVO.FACTORCODE,FactorAsoaVO.FACTORNAME};
			factorAsoaVOs = (FactorAsoaVO[]) BDCacheQueryUtil.queryVOsByIDs(FactorAsoaVO.class,
					FactorAsoaVO.PK_FACTORASOA, factorPk.toArray(new String[0]),fileds);
			if(ArrayUtil.isNotEmpty(factorAsoaVOs)){
				for(FactorAsoaVO factorVO : factorAsoaVOs){
					if(factorName.get(factorVO.getFactorcode())==null){
						factorName.put(factorVO.getFactorcode(), factorVO.getFactorname());
					}
					if(factorMny.get(factorVO.getPk_factorasoa())!=null){
						UFDouble mny = factorMny.get(factorVO.getPk_factorasoa());
						factorMny.put(factorVO.getFactorcode(),mny);
					}else{
						factorMny.put(factorVO.getFactorcode(), UFDouble.ZERO_DBL);
					}
					// 获取核算要素对应的收入预算合计
					if(factorMny.get(factorVO.getPk_factorasoa())!=null){
						UFDouble mny = factorInbudget.get(factorVO.getPk_factorasoa());
						factorInbudget.put(factorVO.getFactorcode(),mny);
					}else{
						factorInbudget.put(factorVO.getFactorcode(), UFDouble.ZERO_DBL);
					}
				}
				//清除数据 key值为PK的数据
				for(FactorAsoaVO factorVO : factorAsoaVOs){
					factorMny.remove(factorVO.getPk_factorasoa());
					factorInbudget.remove(factorVO.getPk_factorasoa());
				}
				// 当核算要素为项目包装收入时，将值替换为为收入预算
//				factorMny.put("0301", factorInbudget.get("0301"));
				omnipotentObj[0] = factorMny;
				omnipotentObj[1] = factorName;
				omnipotentObj[2] = factorInbudget;
			}
		} catch (BusinessException e) {
			//抛出异常
			ExceptionUtils.asBusinessExceptionAdpator(e);
		}
		return omnipotentObj;
	}
	/**
	 * 获取项目档案自定义项显示名称
	 * @param billVO
	 * @return
	 */
	private Object getProjectDefData(BudgetBillVO billVO){
		try {
			// 元数据关联项
			IBean bean = MDQueryService.lookupMDQueryService().getBeanByFullClassName(BudgetHeadVO.class.getName());
			String pk_budget = billVO.getParentVO().getPk_budget();
			Map<String, Object[]> realValueMap = DASFacade.getAttributeValues(bean, new String[] { pk_budget },
					new String[] {BudgetHeadVO.PK_PROJECT+PMCommonKeyConst.PATHSEPARATOR+ProjectHeadVO.HDEF64});
			Object[] values = realValueMap.get("pk_project.hdef64");
			Map<String,String> projectDefName = new HashMap<String,String>();
			if(values[0]==null){
				return projectDefName;
			}
			DefdocVO[] DefdocVO = (DefdocVO[]) BDCacheQueryUtil.queryVOsByIDs(DefdocVO.class,
					"PK_DEFDOC",new String[]{values[0].toString()},new String[]{"name"});
			projectDefName.put(BudgetHeadVO.PK_PROJECT+PMCommonKeyConst.PATHSEPARATOR+ProjectHeadVO.HDEF64, DefdocVO[0].getName());
			return projectDefName;
		} catch (BusinessException e) {
			//抛出异常
			ExceptionUtils.asBusinessExceptionAdpator(e);
		}
		return null;
	}

	@Override
	public Map<String, String> getFacNamesByCode(String[] facCodes) {
		Map<String,String> factorName = new HashMap<String,String>();
		String[] fileds = new String[]{FactorAsoaVO.PK_FACTORASOA,FactorAsoaVO.FACTORCODE,FactorAsoaVO.FACTORNAME};
		try {
			//需要的参数数据结构 二维数组
			String[][] paramValue = new String[facCodes.length][];
			//构建参数
			for(int i=0;i<facCodes.length;i++){
				paramValue[i] = new String[]{facCodes[i]};
			}
			//查询数据
			FactorAsoaVO[] factorAsoaVOs = (FactorAsoaVO[]) BDCacheQueryUtil.queryVOs(FactorAsoaVO.class,fileds,
					new String[]{FactorAsoaVO.FACTORCODE}, paramValue);
			//构造返回数据
			if(ArrayUtil.isNotEmpty(factorAsoaVOs)){
				for(FactorAsoaVO factorVO : factorAsoaVOs){
					if(factorVO==null){
						continue;
					}
					if(factorName.get(factorVO.getFactorcode())==null){
						factorName.put(factorVO.getFactorcode(), factorVO.getFactorname());
					}
				}
			}
		} catch (BusinessException e) {
			//抛出异常
			ExceptionUtils.asBusinessExceptionAdpator(e);
		}
		return factorName;
	}

}
