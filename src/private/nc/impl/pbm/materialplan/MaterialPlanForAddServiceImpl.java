package nc.impl.pbm.materialplan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nc.bs.pub.pf.PfUtilTools;
import nc.impl.pbm.materialstock.TempTableUtils;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.data.vo.VOQuery;
import nc.impl.pubapp.pattern.data.vo.VOUpdate;
import nc.impl.pubapp.pattern.data.vo.tool.VOConcurrentTool;
import nc.itf.pbm.materialplan.pub.IMaterialPlanForAddService;
import nc.itf.pbm.materialstock.pub.IMaterialStockForPlanService;
import nc.pubitf.pu.m422x.pubquery.IStoreReqPubQuery;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pbm.materialplan.MaterialPlanBillVO;
import nc.vo.pbm.materialplan.MaterialPlanBodyVO;
import nc.vo.pbm.materialplan.MaterialPlanHeadVO;
import nc.vo.pbm.materialplanadd.MaterialPlanAddBillVO;
import nc.vo.pbm.materialplanadd.MaterialPlanAddBodyVO;
import nc.vo.pbm.materialplanadd.MaterialPlanAddHeadVO;
import nc.vo.pbm.materialstock.MaterialVOChangeUtil;
import nc.vo.pm.proxy.PMProxy;
import nc.vo.pm.util.ArrayUtil;
import nc.vo.pm.util.ListUtil;
import nc.vo.pm.util.UFDoubleUtils;
import nc.vo.pm.util.app.ModuleInfoQueryUtil;
import nc.vo.pu.m422x.entity.StoreReqAppItemVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.util.VORowNoUtils;

// 物资及服务需求调整单审批完成后回写物资及服务需求单
@SuppressWarnings({ "restriction", "unused", "unchecked", "rawtypes" })
public class MaterialPlanForAddServiceImpl implements
		IMaterialPlanForAddService {
	public MaterialPlanForAddServiceImpl() {
	}

	private IMaterialStockForPlanService getStockService() {
		return (IMaterialStockForPlanService) PMProxy
				.lookup(IMaterialStockForPlanService.class);
	}

	public void approveUpdateMaterialPlan(MaterialPlanAddBillVO addAggVO)
			throws BusinessException {
		MaterialPlanAddHeadVO addHeadVO = addAggVO.getParentVO();
		String pk_project = addHeadVO.getPk_project();

		MaterialPlanBillVO billVO = queryMaterPlanBillVO(pk_project);

		MaterialPlanHeadVO headVO = billVO.getParentVO();

		VOConcurrentTool tool = new VOConcurrentTool();

		tool.checkTSWithDB(new MaterialPlanHeadVO[] { headVO });

		approveUpdateBody(addAggVO, billVO);
	}

	private MaterialPlanBillVO queryMaterPlanBillVO(String pk_project) {
		MaterialPlanHeadVO headVO = queryReleasedMaterPlanHead(pk_project);
		if (null == headVO) {
			ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl
					.getNCLangRes().getStrByID("projectmaterial_0",
							"04815004-0089"));
		}

		MaterialPlanBillVO billVO = new MaterialPlanBillVO();
		billVO.setParentVO(headVO);

		MaterialPlanBodyVO[] bodyVOs = getAllMaterPlanBodys(headVO
				.getPk_mater_plan());

		if (ArrayUtil.isNotEmpty(bodyVOs)) {
			billVO.setChildrenVO(bodyVOs);
		}

		return billVO;
	}

	private void approveUpdateBody(MaterialPlanAddBillVO addAggVO,
			MaterialPlanBillVO billVO) throws BusinessException {
		List<MaterialPlanAddBodyVO> addBodyList = new ArrayList();

		List<MaterialPlanAddBodyVO> updateBodyList = new ArrayList();
		MaterialPlanAddBodyVO[] addBodyVOs = addAggVO.getChildrenVO();
		if (ArrayUtil.isNotEmpty(addBodyVOs)) {
			for (int i = 0; i < addBodyVOs.length; i++) {
				if (addBodyVOs[i].getPk_mater_plan_b() != null) {
					updateBodyList.add(addBodyVOs[i]);
				} else {
					addBodyList.add(addBodyVOs[i]);
				}
			}

			MaterialPlanAddBodyVO[] addBodys = new MaterialPlanAddBodyVO[addBodyList
					.size()];

			MaterialPlanAddBillVO insertAggVO = null;
			MaterialPlanAddBillVO updateAggVO = null;
			if (!ListUtil.isEmpty(addBodyList)) {
				addBodys = (MaterialPlanAddBodyVO[]) addBodyList
						.toArray(addBodys);

				insertAggVO = new MaterialPlanAddBillVO();
				insertAggVO.setParentVO(addAggVO.getParentVO());
				insertAggVO.setChildrenVO(addBodys);

				insertBodyVO(billVO, insertAggVO);
			}

			if (!ListUtil.isEmpty(updateBodyList)) {
				updateAggVO = new MaterialPlanAddBillVO();
				updateAggVO.setParentVO(addAggVO.getParentVO());
				updateAggVO
						.setChildrenVO((CircularlyAccessibleValueObject[]) ListUtil
								.toArray(updateBodyList));
				approveUpdateBody(billVO, updateBodyList);
			}
			try {
				getStockService().StockForMaterAddApprove(
						MaterialVOChangeUtil.materialPlanAddToMaterStock(
								insertAggVO, Boolean.TRUE),
						MaterialVOChangeUtil.materialPlanAddToMaterStock(
								updateAggVO, Boolean.TRUE), null);

			} catch (BusinessException e) {

				ExceptionUtils.wrappException(e);
			}
		}
	}

	public MaterialPlanHeadVO queryMaterPlanHead(String pk_project) {
		VOQuery<MaterialPlanHeadVO> query = new VOQuery(
				MaterialPlanHeadVO.class);

		MaterialPlanHeadVO[] headVOs = (MaterialPlanHeadVO[]) query.query(
				" and pk_project='" + pk_project + "'"
						+ " and last_v_flag = 'Y' ", null);

		if (!ArrayUtil.isEmpty(headVOs)) {
			return headVOs[0];
		}
		return null;
	}

	public MaterialPlanHeadVO queryReleasedMaterPlanHead(String pk_project) {
		VOQuery<MaterialPlanHeadVO> query = new VOQuery(
				MaterialPlanHeadVO.class);

		MaterialPlanHeadVO[] headVOs = (MaterialPlanHeadVO[]) query.query(
				" and pk_project='" + pk_project + "'"
						+ " and last_v_flag = 'Y'  and " + "bill_status"
						+ " = " + 14, null);

		if (!ArrayUtil.isEmpty(headVOs)) {
			return headVOs[0];
		}
		return null;
	}

	private MaterialPlanBodyVO[] getAllMaterPlanBodys(String pk_mater_plan) {
		VOQuery<MaterialPlanBodyVO> query = new VOQuery(
				MaterialPlanBodyVO.class);

		MaterialPlanBodyVO[] bodyVOs = (MaterialPlanBodyVO[]) query.query(
				" and pk_mater_plan='" + pk_mater_plan + "'", null);

		return bodyVOs;
	}

	// 修改数据的回写
	private MaterialPlanBodyVO[] approveUpdateBody(MaterialPlanBillVO billVO,
			List<MaterialPlanAddBodyVO> updateBodyList)
			throws BusinessException {
		Map<String, MaterialPlanAddBodyVO> updateMap = getMaterAddMap(updateBodyList);

		MaterialPlanBodyVO[] bodyVOs = billVO.getChildrenVO();

		for (int i = 0; i < bodyVOs.length; i++) {
			if (updateMap.containsKey(bodyVOs[i].getPk_mater_plan_b())) {
				UFDouble add_num = UFDoubleUtils
						.objToUFDouble(((MaterialPlanAddBodyVO) updateMap
								.get(bodyVOs[i].getPk_mater_plan_b()))
								.getNowadd_num());

				UFDouble total_add_num = UFDoubleUtils.objToUFDouble(bodyVOs[i]
						.getTotal_add_num());

				bodyVOs[i].setTotal_add_num(UFDoubleUtils.add(total_add_num,
						add_num));

				UFDouble norig_num = UFDoubleUtils.objToUFDouble(bodyVOs[i]
						.getNnum());

				UFDouble num = UFDoubleUtils.add(add_num, norig_num);
				bodyVOs[i].setNnum(num);

				// 含税单价
				UFDouble hsdj = new UFDouble(bodyVOs[i].getBdef10());
				// 含税总价
				UFDouble hszj = hsdj.multiply(num);
				bodyVOs[i].setBdef8(hszj.toString());
				
				bodyVOs[i].setMater_price(UFDoubleUtils.add(bodyVOs[i]
						.getMater_price(), ((MaterialPlanAddBodyVO) updateMap
						.get(bodyVOs[i].getPk_mater_plan_b()))
						.getMater_margin()));

				bodyVOs[i].setMater_pr_group(UFDoubleUtils.add(bodyVOs[i]
						.getMater_pr_group(),
						((MaterialPlanAddBodyVO) updateMap.get(bodyVOs[i]
								.getPk_mater_plan_b())).getMater_mar_group()));

				bodyVOs[i].setMater_pr_global(UFDoubleUtils.add(bodyVOs[i]
						.getMater_pr_global(),
						((MaterialPlanAddBodyVO) updateMap.get(bodyVOs[i]
								.getPk_mater_plan_b())).getMater_mar_global()));

				bodyVOs[i]
						.setFix_price(UFDoubleUtils.add(bodyVOs[i]
								.getFix_price(),
								((MaterialPlanAddBodyVO) updateMap
										.get(bodyVOs[i].getPk_mater_plan_b()))
										.getFix_margin()));

				bodyVOs[i].setFix_pr_group(UFDoubleUtils.add(bodyVOs[i]
						.getFix_pr_group(), ((MaterialPlanAddBodyVO) updateMap
						.get(bodyVOs[i].getPk_mater_plan_b()))
						.getFix_mar_group()));

				bodyVOs[i].setFix_pr_global(UFDoubleUtils.add(bodyVOs[i]
						.getFix_pr_global(), ((MaterialPlanAddBodyVO) updateMap
						.get(bodyVOs[i].getPk_mater_plan_b()))
						.getFix_mar_global()));

				bodyVOs[i].setOther_price(UFDoubleUtils.add(bodyVOs[i]
						.getOther_price(), ((MaterialPlanAddBodyVO) updateMap
						.get(bodyVOs[i].getPk_mater_plan_b()))
						.getOther_margin()));

				bodyVOs[i].setOther_pr_group(UFDoubleUtils.add(bodyVOs[i]
						.getOther_pr_group(),
						((MaterialPlanAddBodyVO) updateMap.get(bodyVOs[i]
								.getPk_mater_plan_b())).getOther_mar_group()));

				bodyVOs[i].setOther_pr_global(UFDoubleUtils.add(bodyVOs[i]
						.getOther_pr_global(),
						((MaterialPlanAddBodyVO) updateMap.get(bodyVOs[i]
								.getPk_mater_plan_b())).getOther_mar_global()));

				bodyVOs[i]
						.setPk_supplier(((MaterialPlanAddBodyVO) updateMap
								.get(bodyVOs[i].getPk_mater_plan_b()))
								.getPk_supplier());

				bodyVOs[i].setRequire_date(((MaterialPlanAddBodyVO) updateMap
						.get(bodyVOs[i].getPk_mater_plan_b()))
						.getRequire_date());

				bodyVOs[i].setStatus(1);
			}
		}
		VOUpdate<MaterialPlanBodyVO> update = new VOUpdate();
		update.update(bodyVOs);

		return bodyVOs;
	}

	private void unApproveUpdateAddNum(MaterialPlanBillVO billVO,
			List<MaterialPlanAddBodyVO> updateBodyList) {
		Map<String, MaterialPlanAddBodyVO> updateMap = getMaterAddMap(updateBodyList);

		MaterialPlanBodyVO[] bodyVOs = billVO.getChildrenVO();
		for (int i = 0; i < bodyVOs.length; i++) {
			if (bodyVOs[i].getStatus() != 3) {

				if (updateMap.containsKey(bodyVOs[i].getPk_mater_plan_b())) {
					UFDouble add_num = UFDoubleUtils
							.objToUFDouble(((MaterialPlanAddBodyVO) updateMap
									.get(bodyVOs[i].getPk_mater_plan_b()))
									.getNowadd_num());

					UFDouble total_add_num = UFDoubleUtils
							.objToUFDouble(bodyVOs[i].getTotal_add_num());

					bodyVOs[i].setTotal_add_num(UFDoubleUtils.sub(
							total_add_num, add_num));

					UFDouble num = UFDoubleUtils.sub(bodyVOs[i].getNnum(),
							add_num);
					bodyVOs[i].setNnum(num);

					bodyVOs[i].setMater_price(UFDoubleUtils.sub(bodyVOs[i]
							.getMater_price(),
							((MaterialPlanAddBodyVO) updateMap.get(bodyVOs[i]
									.getPk_mater_plan_b())).getMater_margin()));

					bodyVOs[i].setMater_pr_group(UFDoubleUtils.sub(bodyVOs[i]
							.getMater_pr_group(),
							((MaterialPlanAddBodyVO) updateMap.get(bodyVOs[i]
									.getPk_mater_plan_b()))
									.getMater_mar_group()));

					bodyVOs[i].setMater_pr_global(UFDoubleUtils.sub(bodyVOs[i]
							.getMater_pr_global(),
							((MaterialPlanAddBodyVO) updateMap.get(bodyVOs[i]
									.getPk_mater_plan_b()))
									.getMater_mar_global()));

					bodyVOs[i].setFix_price(UFDoubleUtils.sub(bodyVOs[i]
							.getFix_price(), ((MaterialPlanAddBodyVO) updateMap
							.get(bodyVOs[i].getPk_mater_plan_b()))
							.getFix_margin()));

					bodyVOs[i]
							.setFix_pr_group(UFDoubleUtils.sub(bodyVOs[i]
									.getFix_pr_group(),
									((MaterialPlanAddBodyVO) updateMap
											.get(bodyVOs[i]
													.getPk_mater_plan_b()))
											.getFix_mar_group()));

					bodyVOs[i]
							.setFix_pr_global(UFDoubleUtils.sub(bodyVOs[i]
									.getFix_pr_global(),
									((MaterialPlanAddBodyVO) updateMap
											.get(bodyVOs[i]
													.getPk_mater_plan_b()))
											.getFix_mar_global()));

					bodyVOs[i].setOther_price(UFDoubleUtils.sub(bodyVOs[i]
							.getOther_price(),
							((MaterialPlanAddBodyVO) updateMap.get(bodyVOs[i]
									.getPk_mater_plan_b())).getOther_margin()));

					bodyVOs[i].setOther_pr_group(UFDoubleUtils.sub(bodyVOs[i]
							.getOther_pr_group(),
							((MaterialPlanAddBodyVO) updateMap.get(bodyVOs[i]
									.getPk_mater_plan_b()))
									.getOther_mar_group()));

					bodyVOs[i].setOther_pr_global(UFDoubleUtils.sub(bodyVOs[i]
							.getOther_pr_global(),
							((MaterialPlanAddBodyVO) updateMap.get(bodyVOs[i]
									.getPk_mater_plan_b()))
									.getOther_mar_global()));

					((MaterialPlanAddBodyVO) updateMap.get(bodyVOs[i]
							.getPk_mater_plan_b())).setPk_supplier(bodyVOs[i]
							.getPk_supplier());

					((MaterialPlanAddBodyVO) updateMap.get(bodyVOs[i]
							.getPk_mater_plan_b())).setRequire_date(bodyVOs[i]
							.getRequire_date());

					bodyVOs[i].setStatus(1);
				}
			}
		}
		VOUpdate<MaterialPlanBodyVO> update = new VOUpdate();
		update.update(bodyVOs);
	}

	private MaterialPlanBodyVO[] insertBodyVO(MaterialPlanBillVO billVO,
			MaterialPlanAddBillVO addBillVO) {
		MaterialPlanBillVO aggVO = null;
		try {
			aggVO = (MaterialPlanBillVO) PfUtilTools.runChangeData("4D16",
					"4D14", addBillVO);
		} catch (BusinessException e) {
			ExceptionUtils.wrappException(e);
		}

		if (null == aggVO) {
			return null;
		}

		MaterialPlanHeadVO headVO = billVO.getParentVO();
		MaterialPlanAddBodyVO[] addBodyVOs = addBillVO.getChildrenVO();

		MaterialPlanBodyVO[] newBodyVOs = aggVO.getChildrenVO();
		for (int i = 0; i < newBodyVOs.length; i++) {

			newBodyVOs[i].setPk_mater_plan(headVO.getPk_mater_plan());
			newBodyVOs[i].setAttributeValue("pk_mater_plan",
					headVO.getPk_mater_plan());
			newBodyVOs[i].setStatus(2);
			for (int j = 0; j < addBodyVOs.length; j++) {
				if (newBodyVOs[i].getPk_material().equals(
						addBodyVOs[j].getPk_material())) {
					newBodyVOs[i].setOppo_material(addBodyVOs[j]
							.getOppo_material());
				}
			}
		}

		MaterialPlanBodyVO[] bodys = billVO.getChildrenVO();

		MaterialPlanBodyVO[] newBodys = new MaterialPlanBodyVO[bodys.length
				+ newBodyVOs.length];

		for (int i = 0; i < bodys.length; i++) {
			newBodys[i] = bodys[i];
		}
		for (int i = 0; i < newBodyVOs.length; i++) {
			newBodys[(i + bodys.length)] = newBodyVOs[i];
		}
		VORowNoUtils.setVOsRowNoByRule(newBodys, "rowno");

		aggVO.setParentVO(headVO);
		aggVO.setChildrenVO(newBodyVOs);

		MaterialPlanBillVO origAggVO = new MaterialPlanBillVO();
		origAggVO.setParentVO(headVO);
		origAggVO.setChildrenVO(bodys);

		UpdateBPTemplate<MaterialPlanBillVO> bp = new UpdateBPTemplate(null);

		bp.update(new MaterialPlanBillVO[] { aggVO },
				new MaterialPlanBillVO[] { origAggVO });

		return aggVO.getChildrenVO();
	}

	private Map<String, MaterialPlanAddBodyVO> getMaterAddMap(
			List<MaterialPlanAddBodyVO> updateList) {
		Map<String, MaterialPlanAddBodyVO> updateMap = new HashMap();
		for (int i = 0; i < updateList.size(); i++) {
			String pk_mater_plan_b = ((MaterialPlanAddBodyVO) updateList.get(i))
					.getPk_mater_plan_b();
			updateMap.put(pk_mater_plan_b, updateList.get(i));
		}
		return updateMap;
	}

	private void deleteBodyVO(MaterialPlanBillVO billVO,
			MaterialPlanAddBillVO addBillVO) {
		List<MaterialPlanBodyVO> newList = new ArrayList();

		MaterialPlanBodyVO[] bodys = billVO.getChildrenVO();

		List<String> pkAndRow = new ArrayList();

		for (int i = 0; i < bodys.length; i++) {
			for (int j = 0; j < addBillVO.getChildrenVO().length; j++) {
				if (addBillVO.getChildrenVO()[j].getPk_matplanadd_b().equals(
						bodys[i].getPk_matplanadd_b())) {
					bodys[i].setStatus(3);
					pkAndRow.add(bodys[i].getPk_mater_plan_b());
					newList.add(bodys[i]);
				}
			}
		}

		try {
			if (ModuleInfoQueryUtil.isPUEnabled()) {
				String[] fields = { "csourcebid", "pk_storereq_b" };
				StoreReqAppItemVO[] itemVOs = getIStoreReqPubQuery()
						.queryItemVOByCSourcebids(
								(String[]) pkAndRow.toArray(new String[0]),
								fields);
				if (ArrayUtil.isNotEmpty(itemVOs)) {
					ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl
							.getNCLangRes().getStrByID("projectmaterial_0",
									"04815004-0173"));
				}
			}
		} catch (BusinessException e) {
			ExceptionUtils.wrappException(e);
		}
		MaterialPlanHeadVO headVO = billVO.getParentVO();

		MaterialPlanBodyVO[] newBodys = new MaterialPlanBodyVO[newList.size()];
		newBodys = (MaterialPlanBodyVO[]) newList.toArray(newBodys);
		MaterialPlanBillVO aggVO = new MaterialPlanBillVO();
		aggVO.setParentVO(headVO);
		aggVO.setChildrenVO(newBodys);

		MaterialPlanBillVO origAggVO = new MaterialPlanBillVO();
		origAggVO.setParentVO(headVO);
		origAggVO.setChildrenVO(bodys);

		UpdateBPTemplate<MaterialPlanBillVO> bp = new UpdateBPTemplate(null);

		bp.update(new MaterialPlanBillVO[] { aggVO },
				new MaterialPlanBillVO[] { origAggVO });
	}

	public void unApproveUpdateMaterialPlan(MaterialPlanAddBillVO addAggVO) {
		MaterialPlanAddHeadVO addHeadVO = addAggVO.getParentVO();
		String pk_project = addHeadVO.getPk_project();

		MaterialPlanBillVO billVO = queryMaterPlanBillVO(pk_project);
		MaterialPlanHeadVO headVO = billVO.getParentVO();

		VOConcurrentTool tool = new VOConcurrentTool();

		tool.checkTSWithDB(new MaterialPlanHeadVO[] { headVO });

		unApproveUpdateBody(addAggVO, billVO);
	}

	private void unApproveUpdateBody(MaterialPlanAddBillVO addAggVO,
			MaterialPlanBillVO billVO) {
		List<MaterialPlanAddBodyVO> updateBodyList = new ArrayList();

		List<MaterialPlanAddBodyVO> deleteBodyList = new ArrayList();
		MaterialPlanAddBodyVO[] addBodyVOs = addAggVO.getChildrenVO();
		MaterialPlanAddBillVO updateBillVO = new MaterialPlanAddBillVO();
		updateBillVO.setParentVO(addAggVO.getParentVO());
		MaterialPlanAddBillVO deleteBillVO = new MaterialPlanAddBillVO();
		deleteBillVO.setParentVO(addAggVO.getParentVO());
		if (ArrayUtil.isNotEmpty(addBodyVOs)) {
			for (int i = 0; i < addBodyVOs.length; i++) {
				if (addBodyVOs[i].getPk_mater_plan_b() != null) {
					updateBodyList.add(addBodyVOs[i]);
				} else {
					deleteBodyList.add(addBodyVOs[i]);
				}
			}
			updateBillVO
					.setChildrenVO((CircularlyAccessibleValueObject[]) ListUtil
							.toArray(updateBodyList));
			deleteBillVO
					.setChildrenVO((CircularlyAccessibleValueObject[]) ListUtil
							.toArray(deleteBodyList));

			deleteBodyVO(billVO, addAggVO);
			if (!ListUtil.isEmpty(updateBodyList)) {
				unApproveUpdateAddNum(billVO, updateBodyList);
			}
			try {
				getStockService().StockForMaterAddUnAppr(
						null,
						MaterialVOChangeUtil.materialPlanAddToMaterStock(
								updateBillVO, Boolean.FALSE),
						MaterialVOChangeUtil.materialPlanAddToMaterStock(
								deleteBillVO, Boolean.FALSE));

			} catch (BusinessException e) {

				ExceptionUtils.wrappException(e);
			}
		}
	}

	public MaterialPlanBodyVO[] getMaterialPlanBodys(String[] projectArray,
			String[] materialsArray, String[] cbsArray)
			throws BusinessException {
		String tempTableName = TempTableUtils.createTempTableFor3Cols(
				projectArray, materialsArray, cbsArray);

		String querySql = createqueryByBzPKSql(tempTableName);

		String[][] result = TempTableUtils.useTempTableQuery(querySql);

		return parseByQueryResult(result);
	}

	private String createqueryByBzPKSql(String tempTableName) {
		StringBuilder sql = new StringBuilder();

		sql.append(" select plan_body.pk_mater_plan_b, ")
				.append("        plan_body.pk_material, ")
				.append("        plan_body.pk_cbs_node, ")
				.append("        plan_body.rowno, ")
				.append("        plan_body.nnum, ")
				.append("        plan_body.require_date, ")
				.append("        plan_body.pk_stockorg, ")
				.append("        plan_body.pray_param, ")
				.append("        plan_body.pk_stockorg_v, ")
				.append("        plan_body.mater_unit_price ")
				.append(" from   pm_mater_plan_b plan_body ")
				.append(" inner join pm_mater_plan ")
				.append(" on  pm_mater_plan.pk_mater_plan=plan_body.pk_mater_plan ")
				.append(" and pm_mater_plan.bill_status=").append(14)
				.append(" and plan_body.dr= 0 ")
				.append(" and pm_mater_plan.dr= 0 ")
				.append(" and pm_mater_plan.last_v_flag = 'Y' ")
				.append(" inner join " + tempTableName + " temp ")
				.append(" on  temp.col1=plan_body.pk_project ")
				.append(" and temp.col2=plan_body.pk_material ")
				.append(" and plan_body.dr= 0 order by ").append(" plan_body.")
				.append("rowno").append(",").append(" plan_body.")
				.append("nnum");

		return sql.toString();
	}

	private MaterialPlanBodyVO[] parseByQueryResult(String[][] result) {
		MaterialPlanBodyVO[] returnMaterialPlanBodyVO = new MaterialPlanBodyVO[result.length];
		for (int i = 0; i < result.length; i++) {
			String[] rowData = result[i];
			MaterialPlanBodyVO tempVo = new MaterialPlanBodyVO();
			tempVo.setPk_mater_plan_b(rowData[0]);
			tempVo.setPk_material(rowData[1]);
			tempVo.setPk_cbs_node(rowData[2]);
			tempVo.setRowno(rowData[3]);
			tempVo.setNnum(new UFDouble(rowData[4]));
			if (rowData[5] != null) {
				tempVo.setRequire_date(new UFDate(rowData[5]));
			}
			tempVo.setPk_stockorg(rowData[6]);
			if (rowData[7] != null) {
				tempVo.setPray_param(Integer.valueOf(Integer
						.parseInt(rowData[7])));
			}
			tempVo.setPk_stockorg_v(rowData[8]);
			if (rowData[9] != null) {
				tempVo.setMater_unit_price(new UFDouble(rowData[9]));
			}
			returnMaterialPlanBodyVO[i] = tempVo;
		}
		return returnMaterialPlanBodyVO;
	}

	public String[] getRelasedProject() throws BusinessException {
		VOQuery<MaterialPlanHeadVO> query = new VOQuery(
				MaterialPlanHeadVO.class);

		MaterialPlanHeadVO[] headVOs = (MaterialPlanHeadVO[]) query
				.query(" and bill_status = 14 and last_v_flag = 'Y' and dr = 0 ",
						null);

		String[] pk_project = null;
		if (!ArrayUtil.isEmpty(headVOs)) {
			pk_project = new String[headVOs.length];
			for (int i = 0; i < headVOs.length; i++) {
				pk_project[i] = headVOs[i].getPk_project();
			}
		}
		return pk_project;
	}

	public MaterialPlanBodyVO[] queryMaterialPlanBody(String pk_project) {
		MaterialPlanBillVO billVO = new MaterialPlanImpl()
				.getReleasedMaterialPlanBillVOs(pk_project);

		if (billVO == null) {
			return null;
		}
		return billVO.getChildrenVO();
	}

	private IStoreReqPubQuery getIStoreReqPubQuery() {
		return (IStoreReqPubQuery) PMProxy.lookup(IStoreReqPubQuery.class);
	}
}
