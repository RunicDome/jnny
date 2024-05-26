package nc.ui.pbm.budget.CEAPrint;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nc.itf.pbm.budget.pvt.IBudgetCEAPrint;
import nc.md.data.access.DASFacade;
import nc.md.innerservice.MDQueryService;
import nc.md.model.IBean;
import nc.md.model.MetaDataException;
import nc.ui.pub.print.IDataSource;
import nc.vo.pbm.budget.BudgetBillVO;
import nc.vo.pbm.budget.BudgetHeadVO;
import nc.vo.pim.project.ProjectHeadVO;
import nc.vo.pm.constant.PMReportConst;
import nc.vo.pm.proxy.PMProxy;
import nc.vo.pm.util.ExceptionUtils;
import nc.vo.pm.util.StringUtil;
import nc.vo.pm.util.UFDoubleUtils;
import nc.vo.pmpub.common.PMCommonKeyConst;
import nc.vo.pub.lang.MultiLangText;
import nc.vo.pub.lang.UFDouble;

/**
 * 	类描述：
 * 	CEA-C打印数据源
 * 	针对该数据源的特殊处理均依据CEA-C打印模板，修改本类时需依赖打印模板.现有特殊处理如下：
 * 	1.以""+"打头的自定义变量表达式，这些表达式中包含一个或多个核算要素关系表主键，多个核算要素进行加法运算
 * 	2.以hdef打头的项目预算表头自定义项或自定义变量表达式，根据自定义变量表达式值对自定义项值进行加法或乘法运算
 * @date 2018-3-23
 */
public class CEA_CDataSource implements IDataSource{

	/**
	 * 版本号
	 */
	private static final long serialVersionUID = 5813609662218062955L;
	private final BudgetBillVO billVO;
	// 用于存放<打印模板自定义变量和核算要素code,预算金额>键值对
	private final Map<String, UFDouble> factorBudgetMnyMap;
	// 用于存放<打印模板自定义变量和核算要素code,收入预算>键值对
	private final Map<String, UFDouble> factorInBudgetMnyMap;
		
	// 用于存放<预算表头自定义项,自定义项值>键值对
	private final Map<String, UFDouble> budgetHeadDefValueMap =new HashMap<String,UFDouble>();

	private final String projectDefName ;
	/**CBS页签打印数据*/
	private final Map<String, UFDouble> cbsBudgetMnyMap;
	/**EPS类型
	 * 当前项目的EPS属于工商的分类时，即EPS编码为0501、0502、0503时-->2
	 * 当前项目的EPS属于民用和民现的分类时，即EPS编码为 0504、0505时-->1*/
	private final int business;

	@SuppressWarnings("unchecked")
	public CEA_CDataSource(BudgetBillVO billVO) {
		this.billVO = billVO;
		Map<String, Object> omnipotentMap = PMProxy.lookup(IBudgetCEAPrint.class).initCEA_CPrintData(billVO);
		this.factorBudgetMnyMap = (Map<String, UFDouble>)omnipotentMap.get(CEAPrintConst.FACTOR);
		this.factorInBudgetMnyMap = (Map<String, UFDouble>) omnipotentMap.get(CEAPrintConst.FACTORIN);
		this.cbsBudgetMnyMap = (Map<String, UFDouble>)omnipotentMap.get(CEAPrintConst.CBS);
		this.business = (Integer) omnipotentMap.get(CEAPrintConst.EPSTYPE);
		projectDefName = ((Map<String,String>)omnipotentMap.get("projectDef")).get(BudgetHeadVO.PK_PROJECT+PMCommonKeyConst.PATHSEPARATOR+ProjectHeadVO.HDEF64);
		//用户自定义项1是 参照类型 不进行处理
		for(int i = 1;i < 31;i++) {
			Object defData = billVO.getParentVO().getAttributeValue(PMReportConst.HDEF+(i+1));
			if(defData==null) {
				budgetHeadDefValueMap.put(PMReportConst.HDEF+(i+1),UFDouble.ZERO_DBL.setScale(2, UFDouble.ROUND_HALF_UP));
			} else {
				budgetHeadDefValueMap.put(PMReportConst.HDEF+(i+1), UFDoubleUtils.objToUFDouble(defData).setScale(2, UFDouble.ROUND_HALF_UP));
			}
		}
	}
	
	@Override
	public String[] getItemValuesByExpress(String itemExpress) {
		if (StringUtil.isNotEmpty(itemExpress)) {
			// 对打印模板上“本公司工程总付出成本”、“回本期[（12）/（19）]”进行特殊处理
			if (CEAPrintConst.PROJECT_TOTAL_COST.equals(itemExpress)
					|| CEAPrintConst.COST_BACK_TIME.equals(itemExpress)) {
				UFDouble value = calcSpecialDefVar(itemExpress);
				return new String[] { value.toString() };
			} else if(CEAPrintConst.TOTAL_BUDGET.equals(itemExpress)) {
				//总收入预算
				UFDouble in_budget = billVO.getParentVO().getIn_budget();
				
				return new String[]{in_budget.setScale(2, UFDouble.ROUND_HALF_UP).toString()};
			} else if(CEAPrintConst.TAX_MNY.equals(itemExpress)) {
				//税金
				//总收入预算
				UFDouble in_budget = billVO.getParentVO().getIn_budget();
				//税金 = in_budget/1.11*0.11
				UFDouble tax_mny =UFDoubleUtils.multiply(UFDoubleUtils.div(in_budget, new UFDouble(1.11)),new UFDouble(0.11));
				return new String[]{ tax_mny.setScale(2, UFDouble.ROUND_HALF_UP).toString()};
			} else if(CEAPrintConst.SALES_MARGINS.equals(itemExpress)) {
				//销售利润率 = 预计利润/不含税收入预算*100% = 100-111*budget_mny/in_budget
				//总收入预算
				UFDouble in_budget = billVO.getParentVO().getIn_budget();
				//预算总金额
				UFDouble budget_mny = billVO.getParentVO().getBudget_mny();
				//销售利润率 
				UFDouble sales_margins = UFDoubleUtils.sub(new UFDouble(100),UFDoubleUtils.div(UFDoubleUtils.multiply(new UFDouble(111), budget_mny),in_budget ));
				return new String[]{sales_margins.setScale(2, UFDouble.ROUND_HALF_UP).toString()};
			}
			if(itemExpress.startsWith(CEAPrintConst.COMP)) {
				//判断EPS
				if(business!=1) return new String[]{""};
				itemExpress = itemExpress.substring(5,itemExpress.length()-1);
				return new String[] { compVariable(itemExpress).toString() };
			}
			//新增字段居民户均计算
			if(itemExpress.startsWith(CEAPrintConst.VALUE)) {
				//判断EPS
				if(business!=1) return new String[]{""};
				itemExpress = itemExpress.substring(5);
				return new String[] { valueTodiv(itemExpress).toString() };
			}
			String[] paths = StringUtils.split(itemExpress, ".");
			if (paths.length > 1) {
				// 获取带有“.”的表达式的值，这些表达式是从参照类型或表体中带出的数据
				String[] returnValues = getValuesHasSeparator(itemExpress);
				return returnValues;
			} else {
				//CBS打印
				if(itemExpress.startsWith(CEAPrintConst.CBS)){
					int start = 5;
					//判断EPS
					if(!isPrint(itemExpress)) return new String[]{" "};
					//当前EPS满足条件时，打印对应
					else if(itemExpress.startsWith(CEAPrintConst.CBS+"GS")||itemExpress.startsWith(CEAPrintConst.CBS+"JT")){
						start = 7;
					}
					itemExpress = itemExpress.substring(start,itemExpress.length()-1);
					return new String[] { sumCbsVariable(itemExpress).toString() };
				}
				//核算要素预算金额打印
				if(itemExpress.startsWith(CEAPrintConst.FACTOR)){
					itemExpress = itemExpress.substring(5,itemExpress.length()-1);
					return new String[] { sumFactorVariable(itemExpress).toString() };
				}
				//核算要素预计收入打印
				if(itemExpress.startsWith(CEAPrintConst.FACTORIN)){
					int start = 7;
					//判断fac
					if(!isfacPrint(itemExpress)) return new String[]{" "};
					//当前EPS满足条件时，打印对应
					else if(itemExpress.startsWith(CEAPrintConst.FACTORIN+"GS")){
						start = 9;
					}
					itemExpress = itemExpress.substring(start,itemExpress.length()-1);
					return new String[] { sumFacinVariable(itemExpress).toString() };
				}
				if (itemExpress.startsWith(PMReportConst.HDEF)) {
					UFDouble defValue = getBudgetHeadDefValue(itemExpress);
					if (null == defValue) {
						return null;
					}
					return new String[] { defValue.toString() };
				} else {
					Object returnValue = billVO.getParentVO()
							.getAttributeValue(itemExpress);

					// 将项目预算单据状态翻译成中文
					if (BudgetHeadVO.BILL_STATUS.equals(itemExpress)) {
						return CEACommonPrintUtil
								.getBillStatusByInteger((Integer) returnValue);
					}

					if (returnValue == null) {
						return new String[] { "" };
					}
					return new String[] { returnValue.toString() };
				}
			}
		}
		return null;
	}

	/**
	 * 对打印模板上“本公司工程总付出成本”、“回本期[（12）/（19）]”进行特殊处理
	 * 
	 * @param itemExpress
	 * @return
	 */
	private UFDouble calcSpecialDefVar(String itemExpress) {
		// 取Map“总工程金额”
		UFDouble project_total_money = budgetHeadDefValueMap.get(CEAPrintConst.PROJECT_TOTAL);
		//总收入预算
		UFDouble in_budget = billVO.getParentVO().getIn_budget();
		// “本公司工程总付出成本” = “总工程金额” - “总收入”
		//改为 = 预算总成本 - 不含税收入预算 = 预算总成本 - 总收入预算/1.11 modified by jiyjb 2017年4月17日
		if (CEAPrintConst.PROJECT_TOTAL_COST.equals(itemExpress)) {
			//不含税收入预算
			UFDouble notax_total_income = UFDoubleUtils.div(in_budget, new UFDouble(1.11));
			UFDouble project_total_cost = UFDoubleUtils.sub(
					project_total_money, notax_total_income);
			return project_total_cost.setScale(2, UFDouble.ROUND_HALF_UP);
		} else {// “回本期[（12）/（19）]” = “总工程金额” / “总收入”
			UFDouble cost_back_time = UFDoubleUtils.div(project_total_money,
					in_budget);
			return cost_back_time.setScale(2, UFDouble.ROUND_HALF_UP);
		}
	}

	/**
	 * 获取带有“.”的表达式的值，这些表达式是从参照类型或表体中带出的数据
	 * 
	 * @param itemExpress
	 * @return
	 */
	private String[] getValuesHasSeparator(String itemExpress) {
		try {
			// 元数据关联项
			IBean bean = MDQueryService.lookupMDQueryService()
					.getBeanByFullClassName(BudgetHeadVO.class.getName());
			String pk_budget = billVO.getParentVO().getPk_budget();
			Map<String, Object[]> realValueMap = DASFacade.getAttributeValues(
					bean, new String[] { pk_budget },
					new String[] { itemExpress });
			Object[] values = realValueMap.get(itemExpress);

			// 获取气体种类自定义项主键对应气体种类名称
			if (itemExpress.equals(BudgetHeadVO.PK_PROJECT+PMCommonKeyConst.PATHSEPARATOR+ProjectHeadVO.HDEF64)) {
				return new String[]{projectDefName};
			}
			String[] returnValues = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				if (values[i] == null) {
					returnValues[i] = null;
					continue;
				}
				if (values[i] instanceof MultiLangText) {
					returnValues[i] = ((MultiLangText) values[i]).toString();
				} else {
					returnValues[i] = values[i].toString();
				}
			}
			return returnValues;
		} catch (MetaDataException e) {
			ExceptionUtils.asBusinessRuntimeException(e);
		}
		return null;
	}
	/**
	 * 获取打印模板右半边的前缀为“hdef”的自定义项及自定义变量的值
	 * 
	 * @param itemExpress
	 * @return
	 */
	private UFDouble getBudgetHeadDefValue(String itemExpress) {
		// 字符串长度小于等于6，表示是一个自定义项
		if (itemExpress.length() <= 6) {
			if(business!=1){
				if(itemExpress.equals(PMReportConst.HDEF+2)||
						itemExpress.equals(PMReportConst.HDEF+3)||
						itemExpress.equals(PMReportConst.HDEF+4)||
						itemExpress.equals(PMReportConst.HDEF+5)||
						itemExpress.equals(PMReportConst.HDEF+6)){
					return null;
				}
			}
			UFDouble defValue = budgetHeadDefValueMap.get(itemExpress);
			return defValue;
		}

		String[] multiItems = itemExpress.split("\\*");
		// 根据“*”运算符分割，将各自定义项相乘，最后需要乘以12
		if (multiItems.length > 1) {
			UFDouble multiSum = new UFDouble(12);
			for (String multiItem : multiItems) {
				UFDouble value = budgetHeadDefValueMap.get(multiItem);
				multiSum = UFDoubleUtils.multiply(multiSum, value);
			}
			return multiSum.setScale(2, UFDouble.ROUND_HALF_UP);
		} else {// 根据“+”运算符分割，将各自定义项相加
			String[] plusItems = itemExpress.split("\\+");
			UFDouble plusSum = null;
			for (String plusItem : plusItems) {
				UFDouble value = budgetHeadDefValueMap.get(plusItem);

				plusSum = UFDoubleUtils.add(plusSum, value);
			}
			UFDouble proceedMny = budgetHeadDefValueMap.get(CEAPrintConst.PROJECT_PROCEED);
			if(UFDoubleUtils.isGreaterThan(plusSum, proceedMny)){
				budgetHeadDefValueMap.put(CEAPrintConst.PROJECT_PROCEED, plusSum);
			}
			return plusSum.setScale(2, UFDouble.ROUND_HALF_UP);
		}
	}

	/**
	 * CBS表达式解析
	 * @param itemExpress
	 * @return
	 */
	private UFDouble sumCbsVariable(String itemExpress){
		// 以“,”将自定义变量字符串分割为核算要素数组，用于求和值
		String[] cbsCodes = itemExpress.split(",");
		UFDouble varSum = UFDouble.ZERO_DBL;
		for(String cbsCode:cbsCodes){
			varSum = UFDoubleUtils.add(varSum, cbsBudgetMnyMap.get(cbsCode));
		}
		varSum.setScale(2, UFDouble.ROUND_HALF_UP);
		if(cbsCodes.length>1){
			UFDouble totMny = budgetHeadDefValueMap.get(CEAPrintConst.PROJECT_TOTAL);
			if(UFDoubleUtils.isGreaterThan(varSum, totMny)){
				budgetHeadDefValueMap.put(CEAPrintConst.PROJECT_TOTAL, varSum);
			}
		}
		return varSum.setScale(2, UFDouble.ROUND_HALF_UP);
	}
	/**
	 * factor表达式解析
	 * @param itemExpress
	 * @return
	 */
	private UFDouble sumFactorVariable(String itemExpress){
		String[] factorCodes = itemExpress.split(",");
		UFDouble varSum = UFDouble.ZERO_DBL;
		for(String factorCode:factorCodes){
			if(factorBudgetMnyMap.get(factorCode)==null){
				//汇总下级核算要素
				for(String factorKey:factorBudgetMnyMap.keySet()){
					if(factorKey.startsWith(factorCode)){
						varSum = UFDoubleUtils.add(varSum, factorBudgetMnyMap.get(factorKey));
					}
				}
			}else{
				varSum = UFDoubleUtils.add(varSum, factorBudgetMnyMap.get(factorCode));
			}
		}
		return varSum.setScale(2, UFDouble.ROUND_HALF_UP);
		
	}
	
	/**
	 * facin表达式解析
	 * @param itemExpress
	 * @return
	 */
	private UFDouble sumFacinVariable(String itemExpress){
		String[] factorCodes = itemExpress.split(",");
		UFDouble varSum = UFDouble.ZERO_DBL;
		for(String factorCode:factorCodes){
			if(factorInBudgetMnyMap.get(factorCode)==null){
				//汇总下级核算要素
				for(String factorKey:factorInBudgetMnyMap.keySet()){
					if(factorKey.startsWith(factorCode)){
						varSum = UFDoubleUtils.add(varSum, factorInBudgetMnyMap.get(factorKey));
					}
				}
			}else{
				varSum = UFDoubleUtils.add(varSum, factorInBudgetMnyMap.get(factorCode));
			}
		}
		return varSum.setScale(2, UFDouble.ROUND_HALF_UP);
		
	}
	
	private UFDouble compVariable(String itemExpress){
		String[] dataCode = itemExpress.split("\\/");
		UFDouble cbsMny = cbsBudgetMnyMap.get(dataCode[1]);
		UFDouble defMny = budgetHeadDefValueMap.get(dataCode[0]);
		return UFDoubleUtils.div(cbsMny, defMny, 2);
	}
	private UFDouble valueTodiv(String itemExpress){
		String[] dataCode = itemExpress.split("\\/");
		UFDouble Mny = UFDouble.ZERO_DBL;
		if(dataCode[0].endsWith("budget_mny")) {
			Mny = billVO.getParentVO().getBudget_mny();
		} else {
			Mny = billVO.getParentVO().getIn_budget();
		}
		UFDouble sum = budgetHeadDefValueMap.get(dataCode[1]);
		if(sum == null) {
			return null;
		}
		return UFDoubleUtils.div(Mny, sum, 2);
		
	}
	private boolean isPrint(String itemExpress){
		if(business==0&&(itemExpress.startsWith(CEAPrintConst.CBS+"GS")||itemExpress.startsWith(CEAPrintConst.CBS+"JT")))return false;
		if(business==1&&itemExpress.startsWith(CEAPrintConst.CBS+"GS")) return false;
		if(business==2&&itemExpress.startsWith(CEAPrintConst.CBS+"JT")) return false;
		return true;
	}
	
	private boolean isfacPrint(String itemExpress){
		if(business==1&&itemExpress.startsWith(CEAPrintConst.FACTORIN+"GS")) return false;
		if(business==0&&itemExpress.startsWith(CEAPrintConst.FACTORIN+"GS")) return false;
		return true;
	}
	@Override
	public String[] getAllDataItemExpress() {
		return null;
	}

	@Override
	public String[] getAllDataItemNames() {
		return null;
	}

	@Override
	public String[] getDependentItemExpressByExpress(String arg0) {
		return null;
	}

	@Override
	public String getModuleName() {
		return null;
	}

	@Override
	public boolean isNumber(String arg0) {
		return false;
	}

}
