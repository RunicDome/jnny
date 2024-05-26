package nc.ui.ic.m45.action;

import java.awt.event.ActionEvent;
import java.util.List;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.ui.ic.general.action.GeneralAddFromSourceAction;
import nc.ui.ic.general.util.CardNumValueController;
import nc.ui.ic.pub.util.UIBusiCalculator;
import nc.ui.pcm.utils.GetDao;
import nc.ui.pub.bill.BillCardPanel;
import nc.vo.ic.pub.util.StringUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.scmpub.res.billtype.ITBillType;
import nc.vo.scmpub.res.billtype.POBillType;

import org.apache.commons.lang.ArrayUtils;

@SuppressWarnings({ "restriction" })
public class PurchaseInAddFromSourceAction extends GeneralAddFromSourceAction {
	private static final long serialVersionUID = 7236153870772736486L;

	// XBX 采购入库 ->新增  -> 采购订单加角色 CGRK002控制
	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		if ("PurchaseIn".equals(getEditorModel().getICBillType() + "")) {
			String fg = "0";
			String user = InvocationInfoProxy.getInstance().getUserId();// 当前用户主键
			GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
			String querySql = "select c.cuserid, a.pk_role, a.role_name,c.user_name from sm_role a left join sm_user_role b  on a.pk_role=b.pk_role "
					+ " left join sm_user c on c.cuserid=b.cuserid  where a.role_code = 'CGRK002'";
			List<Object[]> resultList = getDao.query(querySql);
			System.out.println("sql===" + querySql);
			if (resultList.size() > 0) {
				for (int i = 0; i < resultList.size(); i++) {
					Object[] item = resultList.get(i);
					if (user.equals(item[0])) {
						fg = "1";
					}
				}
			}
			System.out.println("当前登录用户主键：" + user);
			if ("1".equals(fg)) {
				super.doAction(arg0);
			} else {
				//ExceptionUtils.wrappBusinessException("不允许自制！");
				ExceptionUtils.wrappBusinessException("不允许自制！");
			}

		}
	}

	private void calculate(int[] rows) {
		UIBusiCalculator cal = new UIBusiCalculator();
		BillCardPanel panel = getEditorModel().getICBizView()
				.getBillCardPanel();

		cal.calc(panel, "nqtorigprice", rows);
	}

	private int[] getRows() {
		BillCardPanel panel = getEditorModel().getICBizView()
				.getBillCardPanel();

		int rowcount = panel.getRowCount();
		if (rowcount == 0) {
			return null;
		}
		int[] rows = new int[rowcount];
		for (int i = 0; i < rowcount; i++) {
			rows[i] = i;
		}
		return rows;
	}

	private void processForJCK() {
		if (StringUtil.isStringEqual(ITBillType.Detail.getCode(),
				getSourceBillType())) {
			int[] rows = getRows();
			if (ArrayUtils.isEmpty(rows)) {
				return;
			}
			calculate(rows);
		}
	}

	protected void afterProcessor() {
		super.afterProcessor();
		processForJCK();
	}

	protected AggregatedValueObject[] processAfterVoChange(
			AggregatedValueObject[] bills) {
		new CardNumValueController().setLGValueRange(getEditorModel(),
				new UFDouble(Double.MAX_VALUE));

		if ((StringUtil.isStringEqual(POBillType.Order.getCode(),
				getSourceBillType()))
				|| (StringUtil.isStringEqual(ITBillType.Detail.getCode(),
						getSourceBillType()))) {

			getEditorModel().getICBizView().getBillCardPanel().getBodyPanel()
					.setAutoAddLine(false);
		} else {
			getEditorModel().getICBizView().getBillCardPanel().getBodyPanel()
					.setAutoAddLine(true);
		}

		return bills;
	}
}
