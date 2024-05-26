package nc.pim.project.pfxx;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pfxx.ISwapContext;
import nc.bs.pfxx.plugin.AbstractPfxxPlugin;
import nc.itf.pim.project.prv.IProject;
import nc.vo.pfxx.auxiliary.AggxsysregisterVO;
import nc.vo.pfxx.util.PfxxPluginUtils;
import nc.vo.pim.project.ProjectBillVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;

public class PMProjectBillPfxxPlugin extends AbstractPfxxPlugin {
	public PMProjectBillPfxxPlugin() {
	}

	private ProjectBillVO insert(AggregatedValueObject vo) {
		ProjectBillVO[] insertvo = { (ProjectBillVO) vo };

		IProject itf = NCLocator.getInstance().lookup(IProject.class);

		try {
			ProjectBillVO[] retvos = itf.insert(insertvo);
			return retvos[0];
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	private AggregatedValueObject update(AggregatedValueObject arg0) {
		ProjectBillVO[] updatevo = { (ProjectBillVO) arg0 };
		IProject itf = NCLocator.getInstance().lookup(IProject.class);

		try {
			ProjectBillVO[] retvos = itf.update(updatevo);
			return retvos[0];
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected Object processBill(Object vo, ISwapContext swapContext,
			AggxsysregisterVO aggxsysvo) throws BusinessException {
		// TODO Auto-generated method stub
		if (vo == null) {
			return null;
		}
		InvocationInfoProxy.getInstance().setUserId("1001A110000000000HV8");// 设置默认操作员主键
		ProjectBillVO aggvo = null;

		if ((vo instanceof ProjectBillVO)) {
			aggvo = (ProjectBillVO) vo;
		}
		if (aggvo != null) {
			String vopk = PfxxPluginUtils.queryBillPKBeforeSaveOrUpdate(
					swapContext.getBilltype(), swapContext.getDocID());
			if (vopk != null) {
				update(aggvo);
			} else {
				Logger.info("如果此单据没有导入过,保存新单据...");
				aggvo.getParentVO().setBill_type("4D10");
				aggvo.getParentVO().setPk_transitype("0001A21000000000PG04");
				aggvo.getParentVO().setTransi_type("4D10-01");
				ProjectBillVO retvo = insert(aggvo);
				return retvo.getParentVO().getPrimaryKey();
			}
		}
		return null;
	}
}
