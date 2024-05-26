package nc.impl.pbm.projectfinal.pvt;

import java.util.Map;
import nc.bs.framework.common.NCLocator;
import nc.bs.pbm.projectfinal.rule.CalcuCurrTypeBeforeRule;
import nc.bs.pbm.projectfinal.rule.VOStatusBeforeRule;
import nc.impl.pbm.projectfinal.util.ObtainCostUtil;
import nc.impl.pubapp.pattern.data.bill.BillQuery;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.pbm.projectfinal.prv.IProFinalObtainTotalCost;
import nc.itf.pmbd.pub.IFinalFlagService;
import nc.itf.pmbd.pub.IMarketContrService4Final;
import nc.itf.uap.IUAPQueryBS;
import nc.vo.fip.pub.FipBaseDataProxy;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.org.AccountingBookVO;
import nc.vo.org.util.OrgPubUtil;
import nc.vo.pbm.projectfinal.ProjectFinalBillVO;
import nc.vo.pbm.projectfinal.ProjectFinalHeadVO;
import nc.vo.pbm.projectfinal.uitl.ValueCalcUtil;
import nc.vo.pm.proxy.PMProxy;
import nc.vo.pm.util.StringUtil;
import nc.vo.pm.util.app.ModuleInfoQueryUtil;
import nc.vo.pm.util.cache.OrgUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

// 项目决算单 开始决算 校验
@SuppressWarnings({ "restriction", "unused", "rawtypes", "unchecked" })
public class ProFinalObtainTotalCostImpl implements IProFinalObtainTotalCost {
	public ProFinalObtainTotalCostImpl() {
	}

	public ProjectFinalBillVO obtainTotalCostAndIncome(
			ProjectFinalBillVO billVO, boolean isNeedSave)
			throws BusinessException {
		String pk_project = billVO.getParentVO().getPk_project();

		if (StringUtil.isEmpty(pk_project)) {
			ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl
					.getNCLangRes().getStrByID("projectfinal_0",
							"04815007-0019"));
		}

		// XBX移除   项目未处于“开始决算”，不能获取成本和收入 校验
		// if (!isStartFinal(pk_project)) {
		// ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl
		// .getNCLangRes().getStrByID("projectfinal_0",
		// "04815007-0020"));
		// }

		UFDouble costMny = obtainTotalCost(pk_project, billVO);

		UFDouble IncomeMny = obtainTotalIncome(pk_project);

		billVO.getParentVO().setCost_mny(costMny);
		billVO.getParentVO().setIncome_mny(IncomeMny);

		billVO = ValueCalcUtil.valueCalc(billVO);

		if (isNeedSave) {
			billVO = updateBillAfterObtainTotalCost(billVO);
		}
		return billVO;
	}

	private boolean isStartFinal(String pk_project) throws BusinessException {
		Map<String, Integer> pk_project2FlagValue = ((IFinalFlagService) PMProxy
				.lookup(IFinalFlagService.class))
				.queryFlagValue(new String[] { pk_project });

		if (pk_project2FlagValue != null) {
			Integer flagValue = (Integer) pk_project2FlagValue.get(pk_project);
			if ((flagValue != null) && (2 == flagValue.intValue())) {
				return true;
			}
		}
		return false;
	}

	private UFDouble obtainTotalCost(String pk_project,
			ProjectFinalBillVO billVO) throws BusinessException {
		UFDouble costMny = UFDouble.ZERO_DBL;

		String pk_org = billVO.getParentVO().getPk_org();
		String pk_financeOrg = OrgUtil.getDefaultFinanceOrgByProjectOrg(pk_org);

		String defaultMainAccountingBookID = FipBaseDataProxy
				.getMainAccountingBookIDByPk_org(pk_financeOrg);
		AccountingBookVO bookVO = (AccountingBookVO) getService().retrieveByPK(
				AccountingBookVO.class, defaultMainAccountingBookID,
				new String[] { "itemcostenablestate" });

		boolean isEnableProjectCost = OrgPubUtil.isVOEnabled(bookVO,
				"itemcostenablestate");

		if ((isEnableProjectCost)
				&& (ModuleInfoQueryUtil.isModuleEnabled("3840"))) {
			costMny = ((nc.itf.pca.domain.finalaccount.IFinalAccountQueryService) PMProxy
					.lookup(nc.itf.pca.domain.finalaccount.IFinalAccountQueryService.class))
					.queryProCostByPropk(pk_project)[0];
		} else {
			costMny = ObtainCostUtil.obtainCostFromOtherModule(billVO);
		}
		return costMny;
	}

	private UFDouble obtainTotalIncome(String pk_project) {
		UFDouble incomeMny = new UFDouble(0.0D);
		try {
			if (ModuleInfoQueryUtil.isPCMEnabled()) {
				incomeMny = ((IMarketContrService4Final) PMProxy
						.lookup(IMarketContrService4Final.class))
						.getShouMnyByProject(pk_project);
			}
		} catch (BusinessException e) {
			ExceptionUtils.wrappException(e);
		}
		return incomeMny;
	}

	private ProjectFinalBillVO updateBillAfterObtainTotalCost(
			ProjectFinalBillVO billVO) {
		BillQuery<ProjectFinalBillVO> query = new BillQuery(
				ProjectFinalBillVO.class);

		ProjectFinalBillVO[] originBills = (ProjectFinalBillVO[]) query
				.query(new String[] { billVO.getParentVO().getPk_projectfinal() });

		UpdateBPTemplate<ProjectFinalBillVO> update = new UpdateBPTemplate(null);

		CompareAroundProcesser<ProjectFinalBillVO> processor = update
				.getAroundProcesser();

		processor.addBeforeRule(new CalcuCurrTypeBeforeRule());

		processor.addBeforeRule(new VOStatusBeforeRule());
		ProjectFinalBillVO[] resultVOs = (ProjectFinalBillVO[]) update.update(
				new ProjectFinalBillVO[] { billVO }, originBills);

		return resultVOs[0];
	}

	private IUAPQueryBS getService() {
		return (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class);
	}
}
