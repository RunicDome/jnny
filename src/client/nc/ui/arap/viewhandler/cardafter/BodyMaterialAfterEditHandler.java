package nc.ui.arap.viewhandler.cardafter;

import java.util.Collection;
import java.util.Map;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.pubitf.uapbd.IMaterialPubService;
import nc.ui.arap.viewhandler.AbstractBillHandler;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
import nc.ui.trade.business.HYPubBO_Client;
import nc.vo.arap.basebill.BaseItemVO;
import nc.vo.bd.material.MaterialVO;
import nc.vo.pub.BusinessException;

// 应收应付表体物料编辑事件
@SuppressWarnings("restriction")
public class BodyMaterialAfterEditHandler extends
		AbstractBillHandler<CardBodyAfterEditEvent> {
	public BodyMaterialAfterEditHandler() {
	}

	public void handle() {
		BillCardPanel panel = getBillCardPanel();
		if ("customer".equals(getKey())) {
			if (null != panel.getBodyValueAt(getRow(), "matcustcode")) {
				panel.setBodyValueAt(null, getRow(), "matcustcode");
			}
		}
		if (getKey().equals("material")) {
			String pk_material = (String) panel.getBodyValueAt(getRow(),
					"material");
			if (null != panel.getBodyItem("material")) {
				try {
					Map<String, MaterialVO> vo = ((IMaterialPubService) NCLocator
							.getInstance().lookup(IMaterialPubService.class))
							.queryMaterialBaseInfoByPks(
									new String[] { pk_material }, new String[] {
											"pk_prodline", "pk_source" });

					String pk_prodline = vo.get(pk_material) == null ? null
							: ((MaterialVO) vo.get(pk_material))
									.getPk_prodline();

					MaterialVO materialVO = (MaterialVO) HYPubBO_Client
							.queryByPrimaryKey(MaterialVO.class, pk_material);
					String materialspec = materialVO == null ? null
							: materialVO.getMaterialspec();// 物料规格
					String materialtype = materialVO == null ? null
							: materialVO.getMaterialtype();// 物料型号
					
					String material_src = vo.get(pk_material) == null ? null
							: ((MaterialVO) vo.get(pk_material)).getPk_source();
					panel.setBodyValueAt(material_src, getRow(), "material_src");
					panel.setBodyValueAt(pk_prodline, getRow(), "productline");
					Logger.error("规格："+materialspec+"型号："+materialtype+"material_src=="+material_src);
					// panel.setBodyValueAt(materialspec, getRow(), "def84");// 规格型号
				} catch (BusinessException e) {
					nc.vo.pubapp.pattern.exception.ExceptionUtils
							.wrappException(e);
				}
			}
			for (String s : BaseItemVO.MONEYFIELD_LIST) {
				if (null != panel.getBodyItem(s)) {
					panel.setBodyValueAt(null, getRow(), s);
				}
			}
			if (null != getBodyItem("money_de")) {
				setHeadValue(getBodyAmountValue("money_de"), "money");
				setHeadValue(getBodyAmountValue("local_money_de"),
						"local_money");
				setHeadValue(getBodyAmountValue("groupdebit"), "grouplocal");
				setHeadValue(getBodyAmountValue("globaldebit"), "globallocal");
			} else {
				setHeadValue(getBodyAmountValue("money_cr"), "money");
				setHeadValue(getBodyAmountValue("local_money_cr"),
						"local_money");
				setHeadValue(getBodyAmountValue("groupcrebit"), "grouplocal");
				setHeadValue(getBodyAmountValue("globalcrebit"), "globallocal");
			}
		}
	}

	protected Collection<String> getFilterKey() {
		return java.util.Arrays.asList(new String[] { "material", "customer" });
	}
}