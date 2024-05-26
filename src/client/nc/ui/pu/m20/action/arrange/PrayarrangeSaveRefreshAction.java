package nc.ui.pu.m20.action.arrange;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import nc.bs.framework.common.NCLocator;
import nc.itf.pu.orderUtils.OrderUtil;
import nc.ui.ml.NCLangRes;
import nc.ui.pubapp.uif2app.actions.batch.BatchSaveAction;
import nc.ui.pubapp.uif2app.query2.model.IModelDataManager;
import nc.ui.trade.business.HYPubBO_Client;
import nc.vo.pu.m20.entity.PrayarrangeViewVO;
import nc.vo.pu.m20.entity.PraybillHeaderVO;
import nc.vo.pu.m20.entity.PraybillItemVO;
import nc.vo.pu.m20.entity.PraybillVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

// 请购安排 2023-05-22 XBX
@SuppressWarnings({ "restriction", "unused", "rawtypes", "unchecked" })
public class PrayarrangeSaveRefreshAction extends BatchSaveAction {
	private static final long serialVersionUID = -2395913125669020523L;
	private IModelDataManager dataManager;

	public PrayarrangeSaveRefreshAction() {
	}

	public void doAction(ActionEvent e) throws Exception {
		check();
		super.doAction(e);
		// 安排的供应商为和丰的传入和丰公司的物资需求申请单
		orderToStoreReqApp();
		getDataManager().refresh();
	}

	// 生成和丰物资需求申请单
	private void orderToStoreReqApp() throws BusinessException {
		// TODO Auto-generated method stub
		List<Object> views = getModel().getRows();
		List<Integer> rows = new ArrayList();
		if (views.size() > 0) {
			// 请购单接口--功能：生成物资需求申请单
			OrderUtil orderUtil = NCLocator.getInstance().lookup(
					OrderUtil.class);
			Set<String> pk_praybillset = new HashSet<>();// 请购单主键
			PraybillVO aggvo = new PraybillVO();// AggVO
			List<PraybillItemVO> praybillItemVOs = new ArrayList<PraybillItemVO>();// 明细VO
			for (int i = 0; i < views.size(); i++) {
				PrayarrangeViewVO view = (PrayarrangeViewVO) views.get(i);
				String pk_suggestsupplier = view
						.getAttributeValue("pk_suggestsupplier") + "";// 建议供应商
				String pk_supplier = (String) HYPubBO_Client.findColValue(
						"bd_supplier", "pk_supplier",
						"nvl(dr,0) = 0 and code = '512'");
				if (pk_suggestsupplier.equals(pk_supplier)) {
					String pk_employee = view.getAttributeValue("pk_employee")
							+ "";// 采购员
					String pk_praybill = view.getAttributeValue("pk_praybill")
							+ "";// 请购单主键
					String pk_praybill_b = view.getPk_praybill_b();// 请购单子表主键
					
					// 请购单明细
					PraybillItemVO praybillItemVO = (PraybillItemVO) ((PraybillItemVO) HYPubBO_Client
							.queryByPrimaryKey(PraybillItemVO.class,
									pk_praybill_b)).clone();
					// 如果建议供应商和原建议供应商不一致或者有采购员，则需要安排。
					if (!pk_suggestsupplier.equals(praybillItemVO
							.getPk_suggestsupplier())
							|| StringUtils.isNotEmpty(pk_praybill_b)) {
						pk_praybillset.add(pk_praybill);
						if (pk_praybillset.size() > 1) {
							ExceptionUtils
									.wrappBusinessException("建议供应商为和丰贸易的请购安排，对应的请购单据必须唯一！");
						}
						// 请购单主表
						PraybillHeaderVO hvo = (PraybillHeaderVO) ((PraybillHeaderVO) HYPubBO_Client
								.queryByPrimaryKey(PraybillHeaderVO.class,
										pk_praybill)).clone();
						hvo.setModifiedtime(null);
						hvo.setModifier(null);
						hvo.setStatus(VOStatus.NEW);
						aggvo.setParentVO(hvo);
						// 修改明细数据
						praybillItemVO.setPk_employee(pk_employee);
						praybillItemVO.setPk_suggestsupplier(pk_suggestsupplier);
						praybillItemVO.setStatus(VOStatus.NEW);
						praybillItemVOs.add(praybillItemVO);
					}
				}
			}
			// 需要生成物资需求申请单的数据
			if(praybillItemVOs.size() > 0){
				PraybillItemVO[] mxvos = new PraybillItemVO[praybillItemVOs.size()];
				for (int i = 0; i < praybillItemVOs.size(); i++) {
					mxvos[i] = praybillItemVOs.get(i);
				}
				aggvo.setChildrenVO(mxvos);
				// 调用接口，将请购单转为物资需求申请单并保存
				orderUtil.orderToStoreReqApp(aggvo);
			}
		}
	}

	public IModelDataManager getDataManager() {
		return this.dataManager;
	}

	public void setDataManager(IModelDataManager dataManager) {
		this.dataManager = dataManager;
	}

	private void check() {
		List<Object> views = getModel().getRows();
		List<Integer> rows = new ArrayList();
		for (int i = 0; i < views.size(); i++) {
			PrayarrangeViewVO view = (PrayarrangeViewVO) views.get(i);
			if (view.getPk_purchaseorg() == null) {
				rows.add(Integer.valueOf(i + 1));
			}
		}
		if (rows.size() == 0) {
			return;
		}
		String errMsg = NCLangRes.getInstance().getStrByID("4004020_0",
				"04004020-0112", null, new String[] { rows.toString() });

		ExceptionUtils.wrappBusinessException(errMsg);
	}
}
