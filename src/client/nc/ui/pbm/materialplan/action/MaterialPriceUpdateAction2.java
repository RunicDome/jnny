package nc.ui.pbm.materialplan.action;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nc.itf.pmbd.pub.IMaterialPriceService;
import nc.itf.pmbd.servprice.pub.IServicePriceService;
import nc.pubitf.uapbd.IMaterialPubService;
import nc.ui.pm.action.ActionInitializer;
import nc.ui.pm.util.BillCardPanelUtil;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UITable;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.actions.EventFromClosingHandlerJudger;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.material.MaterialVO;
import nc.vo.bd.taxcode.TaxrateVO;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pm.proxy.PMProxy;
import nc.vo.pm.util.ListUtil;
import nc.vo.pm.util.UFDoubleUtils;
import nc.vo.pmbd.manager.CurrencyRateManager;
import nc.vo.pmpub.common.MaterialPriceVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.uif2.LoginContext;

// 物资及服务需求单 单价更新
@SuppressWarnings({ "unused", "restriction", "unchecked", "rawtypes" })
public class MaterialPriceUpdateAction2 extends NCAction {
	private BillManageModel billModel;
	private ShowUpableBillForm billFormEditor;
	private static final long serialVersionUID = 1L;

	public ShowUpableBillForm getBillFormEditor() {
		return this.billFormEditor;
	}

	public void setBillFormEditor(ShowUpableBillForm billFormEditor) {
		this.billFormEditor = billFormEditor;
	}

	public BillManageModel getBillModel() {
		return this.billModel;
	}

	public void setBillModel(BillManageModel billModel) {
		this.billModel = billModel;
	}

	public MaterialPriceUpdateAction2() {
		ActionInitializer.initializeAction(this, "material_price_update");
	}

	public void doAction(ActionEvent e) throws Exception {
		BillCardPanel billCardPanel = getBillFormEditor().getBillCardPanel();

		int[] selectRows = billCardPanel.getBillTable().getSelectedRows();

		if (selectRows.length == 0) {
			ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl
					.getNCLangRes().getStrByID("projectmaterial_0",
							"04815004-0186"));
		}

		int rowCount = billCardPanel.getRowCount();
		if (rowCount <= 0) {
			return;
		}
		if (confirmDialog(e).booleanValue()) {
			getPrice();
		}
	}

	private void getPrice() throws BusinessException {
		BillCardPanel billCardPanel = getBillFormEditor().getBillCardPanel();

		int[] selectRows = billCardPanel.getBillTable().getSelectedRows();

		String[] pk_materials = new String[selectRows.length];

		for (int i = 0; i < selectRows.length; i++) {
			String pk_material = (String) BillCardPanelUtil.getBodyValue(
					billCardPanel, "pk_material", selectRows[i]);

			pk_materials[i] = pk_material;
		}
		Map<String, MaterialVO> materialMap = new HashMap();
		materialMap = getIMaterialPubService().queryMaterialBaseInfoByPks(
				pk_materials, new String[] { "fee" });

		List<Integer> feeRows = new ArrayList();

		List<Integer> materialRows = new ArrayList();
		for (int i = 0; i < selectRows.length; i++) {
			String pk_material = (String) BillCardPanelUtil.getBodyValue(
					billCardPanel, "pk_material", selectRows[i]);

			if (materialMap.containsKey(pk_material)) {
				if (((MaterialVO) materialMap.get(pk_material)).getFee() == UFBoolean.TRUE) {
					feeRows.add(Integer.valueOf(selectRows[i]));
				} else {
					materialRows.add(Integer.valueOf(selectRows[i]));
				}
			}
		}

		if (!ListUtil.isEmpty(feeRows)) {
			setPriceToMaterialPrice(getFixMaterialPrice(feeRows), feeRows);
		}

		if (!ListUtil.isEmpty(materialRows)) {
			getMaterialPrice(materialRows);
			setPriceToFixPrice(getFixMaterialPrice(materialRows), materialRows);
		}
	}

	private Map<String, UFDouble> getFixMaterialPrice(List<Integer> rows) {
		BillCardPanel billCardPanel = getBillFormEditor().getBillCardPanel();

		UFDate billMakeTime = (UFDate) BillCardPanelUtil.getHeadItemValue(
				billCardPanel, "req_make_date");

		String pk_group = (String) BillCardPanelUtil.getHeadItemValue(
				billCardPanel, "pk_group");

		String pk_org = (String) BillCardPanelUtil.getHeadItemValue(
				billCardPanel, "pk_org");

		int size = rows.size();
		String[] pk_materials = new String[size];
		for (int i = 0; i < size; i++) {
			String pk_material = (String) BillCardPanelUtil.getBodyValue(
					billCardPanel, "pk_material",
					((Integer) rows.get(i)).intValue());

			pk_materials[i] = pk_material;
		}
		Map<String, UFDouble> priceMap = getIServicePriceService()
				.getMaterialPrice(pk_materials, pk_group, pk_org, billMakeTime,
						null);

		return priceMap;
	}

	private void setPriceToMaterialPrice(Map<String, UFDouble> priceMap,
			List<Integer> rows) {
		BillCardPanel billCardPanel = getBillFormEditor().getBillCardPanel();

		if (priceMap == null) {
			return;
		}

		int size = rows.size();
		for (int i = 0; i < size; i++) {
			int currentRow = ((Integer) rows.get(i)).intValue();
			String pk_material = (String) BillCardPanelUtil.getBodyValue(
					billCardPanel, "pk_material", currentRow);

			if (priceMap.get(pk_material) != null) {
				UFDouble price = (UFDouble) priceMap.get(pk_material);
				BillCardPanelUtil.setBodyValue(billCardPanel, currentRow,
						"mater_unit_price", price);

				UFDouble num = (UFDouble) BillCardPanelUtil.getBodyValue(
						billCardPanel, "nnum", currentRow);

				UFDouble service_prc_ratio = (UFDouble) BillCardPanelUtil
						.getBodyValue(billCardPanel, "service_prc_ratio",
								currentRow);

				price = (UFDouble) BillCardPanelUtil.getBodyValue(
						billCardPanel, "mater_unit_price", currentRow);

				BillCardPanelUtil.setBodyValue(
						billCardPanel,
						currentRow,
						"mater_price",
						UFDoubleUtils.multiply(new UFDouble[] { price, num,
								service_prc_ratio }));
				/*// 无税单价
				UFDouble mater_unit_price = price;
				// 税率
				try {
					String pk_taxcode = (String) BillCardPanelUtil
							.getBodyValue(billCardPanel, "bdef6",
									currentRow);// 税码
					TaxrateVO[] taxrateVO = (TaxrateVO[]) HYPubBO_Client.queryByCondition(TaxrateVO.class, "nvl(dr,0) = 0 and pk_taxcode='"+pk_taxcode+"'");
					if(null != taxrateVO && taxrateVO.length >= 1) {
						UFDouble taxrate = taxrateVO[0].getTaxrate();
						BillCardPanelUtil.setBodyValue(taxrate, currentRow, "bdef7");
					}
				} catch (UifException e1) {
					e1.printStackTrace();
				}
				UFDouble bdef7 = BillCardPanelUtil.getBodyValueAt(currentRow, "bdef7") == null ? new UFDouble(0)
				: new UFDouble(BillCardPanelUtil.getBodyValueAt(currentRow, "bdef7").toString());
				// 无税总价
				UFDouble mater_price = service_prc_ratio.multiply(mater_unit_price.multiply(num));
				BillCardPanelUtil.setBodyValue( billCardPanel,
						currentRow,"mater_price",mater_price);
				// 含税单价
				UFDouble bdef10 = mater_unit_price.multiply(bdef7.div(100).add(1));
				BillCardPanelUtil.setBodyValue(bdef10, currentRow, "bdef10");
				// 含税总价
				UFDouble bdef8 = service_prc_ratio.multiply(bdef10.multiply(num));
				BillCardPanelUtil.setBodyValue(bdef8, currentRow, "bdef8");
				// 税额
				UFDouble bdef9 = bdef8.sub(mater_price);
				BillCardPanelUtil.setBodyValue(bdef9, currentRow, "bdef9");*/
			}
		}
	}

	private void setPriceToFixPrice(Map<String, UFDouble> priceMap,
			List<Integer> rows) {
		BillCardPanel billCardPanel = getBillFormEditor().getBillCardPanel();

		if (priceMap == null) {
			return;
		}

		int size = rows.size();
		for (int i = 0; i < size; i++) {
			int currentRow = ((Integer) rows.get(i)).intValue();
			String pk_material = (String) BillCardPanelUtil.getBodyValue(
					billCardPanel, "pk_material", currentRow);

			if (priceMap.get(pk_material) != null) {
				UFDouble price = (UFDouble) priceMap.get(pk_material);
				BillCardPanelUtil.setBodyValue(billCardPanel, currentRow,
						"fix_unit_price", price);

				UFDouble num = (UFDouble) BillCardPanelUtil.getBodyValue(
						billCardPanel, "nnum", currentRow);

				price = (UFDouble) BillCardPanelUtil.getBodyValue(
						billCardPanel, "fix_unit_price", currentRow);

				BillCardPanelUtil.setBodyValue(billCardPanel, currentRow,
						"fix_price", UFDoubleUtils.multiply(price, num));
			}
		}
	}

	private IMaterialPriceService getIMaterialPriceService() {
		return (IMaterialPriceService) PMProxy
				.lookup(IMaterialPriceService.class);
	}

	private IServicePriceService getIServicePriceService() {
		return (IServicePriceService) PMProxy
				.lookup(IServicePriceService.class);
	}

	private IMaterialPubService getIMaterialPubService() {
		return (IMaterialPubService) PMProxy.lookup(IMaterialPubService.class);
	}

	private void getMaterialPrice(List<Integer> rows) throws BusinessException {
		BillCardPanel billCardPanel = getBillFormEditor().getBillCardPanel();

		String pk_org = (String) BillCardPanelUtil.getHeadItemValue(
				billCardPanel, "pk_org");

		Boolean tax_flag = (Boolean) BillCardPanelUtil.getHeadValue(
				billCardPanel, "pk_project.tax_flag");

		List<MaterialPriceVO> getPriceVOList = new ArrayList();

		int size = rows.size();
		Map<Integer, String> rowNumMap = new HashMap();
		for (int i = 0; i < size; i++) {
			int currentRow = ((Integer) rows.get(i)).intValue();
			String pk_material = (String) BillCardPanelUtil.getBodyValue(
					billCardPanel, "pk_material", currentRow);

			String pk_stockorg = (String) BillCardPanelUtil.getBodyValue(
					billCardPanel, "pk_stockorg", currentRow);

			String supplier = (String) BillCardPanelUtil.getBodyValue(
					billCardPanel, "pk_supplier", currentRow);

			UFDate require_date = (UFDate) BillCardPanelUtil.getHeadItemValue(
					billCardPanel, "req_make_date");

			String pk_project = (String) BillCardPanelUtil.getHeadItemValue(
					billCardPanel, "pk_project");

			String keyString = pk_material + pk_stockorg + supplier
					+ require_date;

			rowNumMap.put(Integer.valueOf(currentRow), keyString);
			MaterialPriceVO priceVO = new MaterialPriceVO();
			priceVO.setPk_org(pk_org);
			priceVO.setPk_project(pk_project);
			priceVO.setMaterialpk(pk_material);
			priceVO.setStockorgid(pk_stockorg);
			priceVO.setPk_supplier(supplier);
			priceVO.setBusiDate(require_date);
			priceVO.setIsTaxes(tax_flag);
			getPriceVOList.add(priceVO);
		}

		getPriceVOList = getIMaterialPriceService().getPriceForMaterialstock(
				getPriceVOList);

		Map<String, UFDouble> priceMap = new HashMap();
		if (ListUtil.isEmpty(getPriceVOList)) {
			return;
		}
		UFDouble rate = UFDouble.ONE_DBL;

		for (MaterialPriceVO tempVO : getPriceVOList) {
			UFDouble price = tempVO.getPrice();
			if (!tempVO.getSrc_currency_pk().equals(tempVO.getPk_current())) {
				rate = CurrencyRateManager.getRate(tempVO.getSrc_currency_pk(),
						tempVO.getPk_current(), tempVO.getBusiDate());

				if (rate == null) {
					throw new BusinessException(NCLangRes4VoTransl
							.getNCLangRes().getStrByID("subcontract_0",
									"04820003-0238"));
				}
				price = CurrencyRateManager.getNmnyByNorigmny(
						tempVO.getSrc_currency_pk(), tempVO.getPk_current(),
						tempVO.getPrice(), rate, tempVO.getBusiDate());
			}

			String keyString = tempVO.getMaterialpk() + tempVO.getStockorgid()
					+ tempVO.getPk_supplier() + tempVO.getBusiDate();

			priceMap.put(keyString, price);
		}
		for (int i = 0; i < size; i++) {
			int currentRow = ((Integer) rows.get(i)).intValue();

			UFDouble price = (UFDouble) priceMap.get(rowNumMap.get(Integer
					.valueOf(currentRow)));
			if (null != price) {
				BillCardPanelUtil.setBodyValue(billCardPanel, currentRow,
						"mater_unit_price", price);

				UFDouble num = (UFDouble) BillCardPanelUtil.getBodyValue(
						billCardPanel, "nnum", currentRow);

				price = (UFDouble) BillCardPanelUtil.getBodyValue(
						billCardPanel, "mater_unit_price", currentRow);

				BillCardPanelUtil.setBodyValue(billCardPanel, currentRow,
						"mater_price", UFDoubleUtils.multiply(price, num));
			}
		}
	}

	private Boolean confirmDialog(ActionEvent e) {
		BillCardPanel billCardPanel = getBillFormEditor().getBillCardPanel();

		int[] selectRows = billCardPanel.getBillTable().getSelectedRows();

		Boolean hasPrice = Boolean.FALSE;
		Boolean needQueryPrice = Boolean.TRUE;
		for (int i = 0; i < selectRows.length; i++) {
			UFDouble mater_unit_price = (UFDouble) BillCardPanelUtil
					.getBodyValue(billCardPanel, "mater_unit_price",
							selectRows[i]);

			UFDouble fix_unit_price = (UFDouble) BillCardPanelUtil
					.getBodyValue(billCardPanel, "fix_unit_price",
							selectRows[i]);

			if ((mater_unit_price != null) || (fix_unit_price != null)) {
				hasPrice = Boolean.TRUE;
				break;
			}
		}
		if ((hasPrice.booleanValue())
				&& (!EventFromClosingHandlerJudger.isFromClosingHandler(e))
				&& (4 != showConfirmCancelDialog(getBillModel().getContext()
						.getEntranceUI()))) {

			needQueryPrice = Boolean.FALSE;
		}

		return needQueryPrice;
	}

	private int showConfirmCancelDialog(Container parent) {
		String TITLE = NCLangRes4VoTransl.getNCLangRes().getStrByID(
				"projectmaterial_0", "04815004-0013");

		String QUESTION = NCLangRes4VoTransl.getNCLangRes().getStrByID(
				"projectmaterial_0", "04815004-0014");

		return MessageDialog.showYesNoDlg(parent, TITLE, QUESTION, 8);
	}
}