package nc.ui.pcm.feebalance.handler;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.Logger;
import nc.itf.pmbd.servprice.pub.IServicePriceService;
import nc.ui.ls.MessageBox;
import nc.ui.pcm.utils.GetDao;
import nc.ui.pm.handler.PMDefaultCardBodyafterHandler;
import nc.ui.pm.util.UICurrencyConvertHead;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
import nc.ui.pubapp.uif2app.view.util.RefMoreSelectedUtils;
import nc.ui.trade.business.HYPubBO_Client;
import nc.vo.bd.cbs.CBSNodeVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pm.proxy.PMProxy;
import nc.vo.pm.util.UFDoubleUtils;
import nc.vo.pmpub.common.utils.BillCardPanelUtil;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

// 费用结算单表体编辑事件
@SuppressWarnings({ "unchecked", "rawtypes", "restriction" })
public class BodyAfterEditHandler extends PMDefaultCardBodyafterHandler
		implements IAppEventHandler<CardBodyAfterEditEvent> {
	public BodyAfterEditHandler() {
	}

	public void handleAppEvent(CardBodyAfterEditEvent e) {
		String key = e.getKey();
		if (("price".equals(key)) || ("material_num".equals(key))
				|| ("service_prc_ratio".equals(key))) {

			setMoney(e);
		} else if ("pk_project".equals(key)) {
			clearWBSValueByProject(e);
			clearCBSValueByProject(e);
			// 立项名称
			setEpsName(e);
		} else if ("money".equals(key)) {
			moneyAfterEdit(e);
		} else if ("pk_material_v".equals(key)) {
			materialAfterEdit(e);
		} else if ("qual_mny_prop".equals(key)) {
			qualMnyPropAfterEdit(e);
		} else if ("pk_cbsnode".equals(key)) {
			cbsAfterEdit(e);
		} else if ("qual_mny".equals(key)) {
			qualAfterEdit(e);
		}
	}

	private void setEpsName(CardBodyAfterEditEvent e) {
		String pk_project = (String) BillCardPanelUtil.getBodyValue(
				e.getBillCardPanel(), "pk_project", e.getRow());// 项目
		String pk_org = (String) BillCardPanelUtil.getHeadValue(
				e.getBillCardPanel(), "pk_org");// 组织
		String transi_type = (String) BillCardPanelUtil.getHeadValue(
				e.getBillCardPanel(), "transi_type");// 交易类型
		// 专项资金登记
		if ("4D83-Cxx-36".equals(transi_type)) {
			try {
				String pk_eps = (String) HYPubBO_Client.findColValue(
						"bd_project", "pk_eps",
						"nvl(dr,0) = 0 and pk_project ='" + pk_project
								+ "' and pk_duty_org = '" + pk_org + "'");
				if (StringUtils.isNotEmpty(pk_eps)) {
					String eps_name = (String) HYPubBO_Client.findColValue(
							"pm_eps", "eps_name", " pk_eps ='" + pk_eps + "'");
					if (StringUtils.isNotEmpty(eps_name)) {
						e.getBillCardPanel().setBodyValueAt(eps_name,
								e.getRow(), "def1", e.getTableCode());
					}
				}
			} catch (BusinessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				Logger.error(e1);
			}
		}
	}

	private void qualAfterEdit(CardBodyAfterEditEvent e) {
		UFDouble qual_mny = (UFDouble) BillCardPanelUtil.getBodyValue(
				e.getBillCardPanel(), "qual_mny", e.getRow());

		UFDouble money = (UFDouble) BillCardPanelUtil.getBodyValue(
				e.getBillCardPanel(), "money", e.getRow());
		if (UFDoubleUtils.isGreaterThan(qual_mny, money)) {
			int row = e.getRow() + 1;
			BillCardPanelUtil.setBodyValue(e.getBillCardPanel(), "qual_mny",
					null, e.getTableCode());
			ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl
					.getNCLangRes().getStrByID("feebalance_0", "04820005-0000",
							null, new String[] { row + "" }));
		}
	}

	private void cbsAfterEdit(CardBodyAfterEditEvent e) {
		String pk_project = (String) BillCardPanelUtil.getBodyValue(
				e.getBillCardPanel(), "pk_project", e.getRow());
		// XBX 修改
		String pk_cbsnode = (String) BillCardPanelUtil.getBodyValue(
				e.getBillCardPanel(), "pk_cbsnode", e.getRow());// CBS
		String pk_org = (String) BillCardPanelUtil.getHeadValue(
				e.getBillCardPanel(), "pk_org");// 组织
		String transi_type = (String) BillCardPanelUtil.getHeadValue(
				e.getBillCardPanel(), "transi_type");// 交易类型
		UFDouble money = (UFDouble) BillCardPanelUtil.getHeadValue(
				e.getBillCardPanel(), "money");// 总金额
		try {
			// 4D83-Cxx-84 控制价委托申请单
			if ("4D83-Cxx-84".equals(transi_type)
					&& StringUtils.isNotEmpty(pk_cbsnode)) {
				ProjectHeadVO projectVO = (ProjectHeadVO) HYPubBO_Client
						.queryByPrimaryKey(ProjectHeadVO.class, pk_project);
				String pk_projectclass = projectVO.getPk_projectclass();
				CBSNodeVO cbsVO = (CBSNodeVO) HYPubBO_Client.queryByPrimaryKey(
						CBSNodeVO.class, pk_cbsnode);
				if (cbsVO == null) {
					MessageBox.showMessageDialog("提示信息", "根据PKCBS["
							+ pk_cbsnode + "]获取CBSVO失败");
					return;
				}
				String free9 = cbsVO.getFree9();
				if (StringUtils.isNotEmpty(free9)) {
					StringBuffer sql = new StringBuffer(
							"select max(to_number(case "
									+ free9
									+ " when '~' then '0' else "
									+ free9
									+ " end)) from pm_yearplan_b where nvl(dr,0) = 0 and pk_project = '"
									+ pk_project + "' and pk_org = '" + pk_org
									+ "'");
					GetDao getDao = NCLocator.getInstance()
							.lookup(GetDao.class);
					List<Object[]> planls = getDao.query(sql.toString());
					// 年度投资计划（最大）费用项金额
					UFDouble fyvalue = UFDouble.ZERO_DBL;
					if (planls != null && planls.size() > 0
							&& planls.get(0) != null
							&& planls.get(0)[0] != null
							&& !"".equals(planls.get(0)[0])
							&& !"null".equals(planls.get(0)[0])) {
						fyvalue = new UFDouble(planls.get(0)[0] + "");
						System.out.println("最大值：" + fyvalue);
					}
					// 按项目+符合条件CBS查询金额
					String wheresql = " FREE9 = '" + free9
							+ "' AND DR = 0 AND (PK_ORG = '" + pk_org
							+ "'  OR PK_ORG = '~') AND (PK_PROJECT "
							+ "<> '~' OR PK_PROJECTTYPE = '" + pk_projectclass
							+ "') ";
					CBSNodeVO[] cbsvos = (CBSNodeVO[]) HYPubBO_Client
							.queryByCondition(CBSNodeVO.class, wheresql);
					String pk_allcbs = "(";
					if (cbsvos != null && cbsvos.length > 0) {
						for (CBSNodeVO cbsNodeVO : cbsvos) {
							pk_allcbs += "'" + cbsNodeVO.getPk_cbsnode() + "',";
						}
						pk_allcbs = pk_allcbs.substring(0,
								pk_allcbs.length() - 1).concat(")");
					} else {
						pk_allcbs = "('')";
					}
					String selExistMoneySql = "SELECT NVL(SUM(MONEY),0) MONEY FROM V_HTJS WHERE PK_ORG = '"
							+ pk_org
							+ "' AND "
							+ "PK_PROJECT = '"
							+ pk_project
							+ "' AND PK_CBSNODE IN " + pk_allcbs;
					List<Object[]> jsjels = getDao.query(selExistMoneySql);
					if (jsjels != null && jsjels.size() > 0
							&& jsjels.get(0) != null
							&& jsjels.get(0)[0] != null) {
						System.out.println("已执行金额："
								+ new UFDouble(jsjels.get(0)[0] + ""));
						fyvalue = fyvalue.sub(new UFDouble(jsjels.get(0)[0]
								+ ""));
					}
					money = money.add(fyvalue);
					BillCardPanelUtil.setHeadValue(e.getBillCardPanel(), "money", money);
					e.getBillCardPanel().setBodyValueAt(fyvalue, e.getRow(),
							"money", e.getTableCode());
					e.getBillCardPanel().setBodyValueAt(fyvalue, e.getRow(),
							"def9", e.getTableCode());
				}
			}
		} catch (BusinessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			MessageBox.showMessageDialog("提示信息", e1.getMessage());
			return;
		}
		// String pk_wbs = (String) BillCardPanelUtil.getBodyValue(
		// e.getBillCardPanel(), "pk_wbs", e.getRow());
		//
		// RefMoreSelectedUtils utils = new RefMoreSelectedUtils(
		// e.getBillCardPanel());
		// this.indexs = utils.refMoreSelected(e.getRow(), e.getKey(), true);
		//
		// if (null != pk_project) {
		// setMutiRefValue(pk_project, e.getBillCardPanel(), "pk_project");
		// }
		// if (null != pk_wbs) {
		// setMutiRefValue(pk_wbs, e.getBillCardPanel(), "pk_wbs");
		// }
	}

	// private void setMutiRefValue(String value, BillCardPanel billCardPanel,
	// String cloName) {
	// for (int i = 0; i < this.indexs.length; i++) {
	// BillCardPanelUtil.setBodyValue(billCardPanel, this.indexs[i],
	// cloName, value);
	// billCardPanel.getBillModel().loadLoadRelationItemValue(
	// this.indexs[i], cloName);
	// billCardPanel.getBillModel().loadEditRelationItemValue(
	// this.indexs[i], cloName);
	// }
	// }

	private void qualMnyPropAfterEdit(CardBodyAfterEditEvent e) {
		BillCardPanel panel = e.getBillCardPanel();

		UFDouble money = (UFDouble) panel.getBodyValueAt(e.getRow(), "money");
		UFDouble qual_mny_prop = (UFDouble) panel.getBodyValueAt(e.getRow(),
				"qual_mny_prop");

		UFDouble qual_mny = UFDoubleUtils.multiply(money, qual_mny_prop).div(
				new UFDouble(100));

		panel.setBodyValueAt(qual_mny, e.getRow(), "qual_mny", e.getTableCode());
	}

	private void setMoney(CardBodyAfterEditEvent e) {
		BillCardPanel panel = e.getBillCardPanel();

		UFDouble price = UFDoubleUtils.objToUFDouble(panel.getBodyValueAt(
				e.getRow(), "price"));
		UFDouble material_num = UFDoubleUtils.objToUFDouble(panel
				.getBodyValueAt(e.getRow(), "material_num"));

		UFDouble servc_prc_ratio = UFDoubleUtils.objToUFDouble(panel
				.getBodyValueAt(e.getRow(), "service_prc_ratio"));

		UFDouble money = price.multiply(material_num).multiply(servc_prc_ratio);
		UFDouble qual_mny_prop = (UFDouble) panel.getBodyValueAt(e.getRow(),
				"qual_mny_prop");

		panel.setBodyValueAt(money, e.getRow(), "money", e.getTableCode());

		panel.setBodyValueAt(
				UFDoubleUtils.multiply(money, qual_mny_prop).div(
						new UFDouble(100)), e.getRow(), "qual_mny",
				e.getTableCode());

		String[] headMnyKeys = { "money" };
		String[] bodyMnyKeys = { "money" };
		UICurrencyConvertHead.sumBodyMny(e.getBillCardPanel(), bodyMnyKeys,
				headMnyKeys);
	}

	private void clearWBSValueByProject(CardBodyAfterEditEvent e) {
		BillCardPanelUtil.setBodyValue(e.getBillCardPanel(), e.getRow(),
				"pk_wbs", null, e.getTableCode());
	}

	private void clearCBSValueByProject(CardBodyAfterEditEvent e) {
		BillCardPanelUtil.setBodyValue(e.getBillCardPanel(), e.getRow(),
				"pk_cbsnode", null, e.getTableCode());
	}

	private void moneyAfterEdit(CardBodyAfterEditEvent e) {
		String[] headMnyKeys = { "money" };
		String[] bodyMnyKeys = { "money" };

		UICurrencyConvertHead.sumBodyMny(e.getBillCardPanel(), bodyMnyKeys,
				headMnyKeys);

		UFDouble qual_mny_prop = (UFDouble) BillCardPanelUtil.getBodyValue(
				e.getBillCardPanel(), "qual_mny_prop", e.getRow());

		if (!UFDoubleUtils.isNullOrZero(qual_mny_prop)) {
			UFDouble money = (UFDouble) BillCardPanelUtil.getBodyValue(
					e.getBillCardPanel(), "money", e.getRow());
			UFDouble qual_mny = UFDoubleUtils.multiply(money, qual_mny_prop)
					.div(new UFDouble(100));
			e.getBillCardPanel().setBodyValueAt(qual_mny, e.getRow(),
					"qual_mny", e.getTableCode());
		}
	}

	private void materialAfterEdit(CardBodyAfterEditEvent e) {
		BillCardPanel panel = e.getBillCardPanel();

		String tablecode = e.getTableCode();
		String pk_project = (String) BillCardPanelUtil.getBodyValue(panel,
				"pk_project", e.getRow());
		String pk_wbs = (String) BillCardPanelUtil.getBodyValue(panel,
				"pk_wbs", e.getRow());
		String pk_cbs = (String) BillCardPanelUtil.getBodyValue(panel,
				"pk_cbsnode", e.getRow());
		String pk_org = (String) BillCardPanelUtil
				.getHeadValue(panel, "pk_org");
		String pk_group = (String) BillCardPanelUtil.getHeadValue(panel,
				"pk_group");
		UFDate balance_date = (UFDate) BillCardPanelUtil.getHeadValue(panel,
				"balance_date");
		String pk_currtype = BillCardPanelUtil.getHeadStringValue(panel,
				"pk_currtype");

		RefMoreSelectedUtils util = new RefMoreSelectedUtils(panel);
		int[] affectRows = util.refMoreSelected(e.getRow(), "pk_material_v",
				Boolean.TRUE.booleanValue());

		if (null != pk_project) {
			setRefPk(affectRows, pk_project, panel, "pk_project", tablecode);
		}
		if (null != pk_wbs) {
			setRefPk(affectRows, pk_wbs, panel, "pk_wbs", tablecode);
		}
		if (null != pk_cbs) {
			setRefPk(affectRows, pk_cbs, panel, "pk_cbsnode", tablecode);
		}

		String old_pk_material_v = (String) e.getOldValue();
		resetValueAfterMaterialChanged(panel, tablecode, old_pk_material_v,
				affectRows);

		setPrice(affectRows, panel, pk_org, pk_group, balance_date, pk_currtype);
	}

	private void resetValueAfterMaterialChanged(BillCardPanel panel,
			String tablecode, String old_pk_material_v, int[] affectRows) {
		String[] pk_material_vs = new String[affectRows.length];
		for (int i = 0; i < affectRows.length; i++) {
			pk_material_vs[i] = ((String) BillCardPanelUtil.getBodyValue(panel,
					tablecode, "pk_material_v", affectRows[i]));
		}

		if (!ArrayUtils.contains(pk_material_vs, old_pk_material_v)) {
			for (int affectRow : affectRows) {
				BillCardPanelUtil.setBodyValue(panel, affectRow,
						"service_prc_ratio", UFDouble.ONE_DBL, tablecode);

				BillCardPanelUtil.setBodyValue(panel, affectRow,
						"material_num", null, tablecode);
				BillCardPanelUtil.setBodyValue(panel, affectRow, "money", null,
						tablecode);
			}
		}
	}

	private void setRefPk(int[] affectRows, String value,
			BillCardPanel billCardPanel, String cloName, String tablecode) {
		for (int i = 0; i < affectRows.length; i++) {
			BillCardPanelUtil.setBodyValue(billCardPanel, affectRows[i],
					cloName, value, tablecode);
			billCardPanel.getBillModel().loadLoadRelationItemValue(
					affectRows[i], cloName);
			billCardPanel.getBillModel().loadEditRelationItemValue(
					affectRows[i], cloName);
		}
	}

	private void setPrice(int[] affectRows, BillCardPanel panel, String pk_org,
			String pk_group, UFDate balance_date, String pk_currtype) {
		String fistAffectedMaterial = (String) BillCardPanelUtil.getBodyValue(
				panel, "pk_material", affectRows[0]);

		if ((1 == affectRows.length) && (null == fistAffectedMaterial)) {
			BillCardPanelUtil.setBodyValue(panel, affectRows[0], "price", null);
			return;
		}

		Set<String> pk_material_set = new HashSet();
		String pk_material = null;
		for (int i = 0; i < affectRows.length; i++) {
			pk_material = (String) BillCardPanelUtil.getBodyValue(panel,
					"pk_material", affectRows[i]);
			if (null != pk_material) {
				pk_material_set.add(pk_material);
			}
		}

		Map<String, UFDouble> Pk_material2Price_Map = ((IServicePriceService) PMProxy
				.lookup(IServicePriceService.class)).getMaterialPrice(
				(String[]) pk_material_set.toArray(new String[pk_material_set
						.size()]), pk_group, pk_org, balance_date, pk_currtype);

		if (Pk_material2Price_Map != null) {
			String material = null;
			for (int i = 0; i < affectRows.length; i++) {
				material = (String) BillCardPanelUtil.getBodyValue(panel,
						"pk_material", affectRows[i]);
				UFDouble price = (UFDouble) Pk_material2Price_Map.get(material);
				BillCardPanelUtil.setBodyValue(panel, affectRows[i], "price",
						price);
			}
		}
	}
}
