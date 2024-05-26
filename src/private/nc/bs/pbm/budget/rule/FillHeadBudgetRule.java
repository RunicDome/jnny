package nc.bs.pbm.budget.rule;

import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.pbm.budget.BudgetBillVO;
import nc.vo.pbm.budget.BudgetCBSBodyVO;
import nc.vo.pbm.budget.BudgetHeadVO;
import nc.vo.pbm.budget.util.CBSTreeUtil;
import nc.vo.pm.util.UFDoubleUtils;
import nc.vo.pub.lang.UFDouble;

// 项目预算表体计算表头
@SuppressWarnings("unused")
public class FillHeadBudgetRule implements IRule<BudgetBillVO> {
	public FillHeadBudgetRule() {
	}

	public void process(BudgetBillVO[] vos) {
		BudgetCBSBodyVO cbsBodyVO4Root = CBSTreeUtil.getCBSBodyVOForRoot(vos[0]
				.getBudgetCBSBodyVO());

		vos[0].getParentVO().setBudget_mny(cbsBodyVO4Root.getBudget_mny());

		vos[0].getParentVO().setIn_budget(cbsBodyVO4Root.getIn_budget());

		UFDouble pre_profit = UFDoubleUtils.sub(vos[0].getParentVO()
				.getIn_budget(), vos[0].getParentVO().getBudget_mny());
		vos[0].getParentVO().setPre_profit(pre_profit);
		// 计算表头 报装毛利 
		
		// 计算表头 报装毛利率
	}
}
