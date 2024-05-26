package nc.pubimpl.pmpub.project.pfxx;

import java.util.ArrayList;
import java.util.List;

import nc.bs.pmpub.uap.project.bp.ProjectInsertBP;
import nc.bs.pmpub.uap.project.bp.ProjectUpdateBP;
import nc.impl.pubapp.pattern.data.bill.BillQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillConcurrentTool;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.pubimpl.so.pfxx.AbstractSOPfxxPlugin;
import nc.vo.pmpub.project.ProjectBillVO;
import nc.vo.pub.AggregatedValueObject;

public class ProjectBillPfxxPlugin extends AbstractSOPfxxPlugin {
	public ProjectBillPfxxPlugin() {
	}

	public List<IRule<AggregatedValueObject>> getCheckers() {
		List<IRule<AggregatedValueObject>> rules = new ArrayList();

		return rules;
	}

	protected AggregatedValueObject insert(AggregatedValueObject vo) {
		ProjectBillVO[] insertvo = { (ProjectBillVO) vo };

		ProjectInsertBP insertact = new ProjectInsertBP();
		ProjectBillVO[] retvos = insertact.insert(insertvo);
		if ((null == retvos) || (retvos.length == 0)) {
			return null;
		}
		return retvos[0];
	}

	protected AggregatedValueObject update(AggregatedValueObject vo, String vopk) {
		ProjectBillVO[] updatevo = { (ProjectBillVO) vo };

		BillQuery<ProjectBillVO> billquery = new BillQuery(ProjectBillVO.class);

		ProjectBillVO[] origvos = (ProjectBillVO[]) billquery
				.query(new String[] { vopk });

		BillConcurrentTool tool = new BillConcurrentTool();
		tool.lockBill(origvos);
		ProjectUpdateBP insertact = new ProjectUpdateBP();
		ProjectBillVO[] retvos = insertact.update(updatevo, origvos);
		if ((null == retvos) || (retvos.length == 0)) {
			return null;
		}
		return retvos[0];
	}
}
