package nc.ui.pm.PmFeeBalance.billref.m4Z01To4Z05;

import java.awt.Container;

import nc.bs.framework.common.NCLocator;
import nc.itf.scmpub.reference.uap.setting.defaultdata.DefaultDataSettingAccessor;
import nc.ui.pubapp.billref.src.DefaultBillReferQuery;
import nc.ui.pubapp.uif2app.query2.QueryConditionDLGDelegator;
import nc.ui.pubapp.uif2app.query2.totalvo.MarAssistantDealer;
import nc.ui.scmpub.query.refregion.QDeptFilter;
import nc.ui.scmpub.query.refregion.QPsndocFilter;
import nc.ui.scmpub.query.refregion.QTransTypeFilter;
import nc.ui.scmpub.query.refregion.RefCommonFilterListener;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.scmpub.res.billtype.SOBillType;

public class BillReferQueryFor4Z05 extends DefaultBillReferQuery {
	public BillReferQueryFor4Z05(Container c, TemplateInfo info) {
		super(c, info);
	}

	protected void initQueryConditionDLG(QueryConditionDLGDelegator dlgDelegator) {
		setDefaultPk_org(dlgDelegator);

		initFilterRef(dlgDelegator);

		processBodyItem(dlgDelegator);

		dlgDelegator
				.registerNeedPermissionOrgFieldCodes(new String[] { "pk_org" });

		dlgDelegator.addQueryCondVODealer(new MarAssistantDealer());
	}

	private void initFilterRef(QueryConditionDLGDelegator condDLGDelegator) {
		QTransTypeFilter trantype = new QTransTypeFilter(condDLGDelegator,
				"4Z01");

		trantype.filter();

		RefCommonFilterListener filterUtil = new RefCommonFilterListener(
				condDLGDelegator, "pk_org");

		filterUtil.addFilterMapsListeners();

		QDeptFilter deptFilter = QDeptFilter.createDeptFilterOfSO(
				condDLGDelegator, "pk_dept");

		deptFilter.setPk_orgCode("pk_org");
		deptFilter.addEditorListener();

		QPsndocFilter psnFilter = QPsndocFilter.createQPsndocFilterOfSO(
				condDLGDelegator, "cemployeeid");

		psnFilter.setPk_orgCode("pk_org");
		psnFilter.addEditorListener();
	}

	private void processBodyItem(QueryConditionDLGDelegator condDLGDelegator) {
		// condDLGDelegator.addRedundancyInfo("pk_org",
		// "salequotationdetail.pk_org");
	}

	private void setDefaultPk_org(QueryConditionDLGDelegator condDLGDelegator) {
		String defaultOrg = null;
		try {
			defaultOrg = DefaultDataSettingAccessor.getDefaultOrgUnit();
		} catch (Exception ex) {
			ExceptionUtils.wrappException(ex);
		}
		if ((defaultOrg != null) && (defaultOrg.trim().length() > 0)) {
			condDLGDelegator.setDefaultValue("po_storereq.pk_org", defaultOrg);
		}
	}
}