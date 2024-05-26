package nc.itf.pbm.budget.pvt;

import java.util.Map;

import nc.vo.pbm.budget.BudgetBillVO;

public interface IBudgetCEAPrint {
	/**
	 * 初始化A表打印数据
	 * @return
	 */
	public Map<String,Object> initCEA_APrintData(BudgetBillVO billVO);
	
	/**
	 * 初始化B表打印数据
	 * @return
	 */
	public Map<String,Object> initCEA_BPrintData(BudgetBillVO billVO);
	
	/**
	 * 初始化C表打印数据
	 * @return
	 */
	public Map<String,Object> initCEA_CPrintData(BudgetBillVO billVO);
	
	/**
	 * 初始化D表打印数据
	 * @return
	 */
	public Map<String,Object> initCEA_DPrintData(BudgetBillVO billVO);
	
	/**
	 * 根据核算要素编码获取名称
	 * @param codeList
	 * @return
	 */
	public Map<String,String> getFacNamesByCode(String[] facCodes);
}
