package nc.impl.pu.m20;

import java.text.SimpleDateFormat;
import java.util.Date;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.trade.business.HYPubBO;
import nc.itf.pu.m20.IPushSupplier;
import nc.vo.bd.material.MaterialVO;
import nc.vo.pu.m20.entity.PraybillHeaderVO;
import nc.vo.pu.m20.entity.PraybillItemVO;
import nc.vo.pu.supplier.BdMaterialPriceVO;
import nc.vo.pu.supplier.PuCtMaterialVO;
import nc.vo.pu.supplier.PuPriceMaterialVO;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

public class PushSupplierImpl implements IPushSupplier {
	private HYPubBO hyPubBO;

	public HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	public void setHyPubBO(HYPubBO hyPubBO) {
		this.hyPubBO = hyPubBO;
	}

	// 推送供应商平台
	@Override
	public void pushSupplier(String pk_praybill) throws BusinessException {
		// TODO Auto-generated method stub
		try {
			PraybillHeaderVO hvo = (PraybillHeaderVO) getHyPubBO()
					.queryByPrimaryKey(PraybillHeaderVO.class, pk_praybill);
			beforeRule(hvo);// 校验单据信息
			insertMaterial(hvo);// 推送
		} catch (BusinessException e) {
			// TODO: handle exception
			throw new BusinessException("推送失败：" + e.getMessage());
		}
	}

	// 推送前校验
	public void beforeRule(PraybillHeaderVO hvo) throws BusinessException {
		// bsctype委外
		String bsctype = hvo.getBsctype().toString();
		// fbillstatus单据状态0=自由，1=提交，2=正在审批，3=审批通过，4=审批未通过，5=关闭，
		String fbillstatus = hvo.getFbillstatus().toString();
		if (bsctype.equals("Y")) {
			throw new BusinessException("委外单据不可推送！");
		}
		if (!fbillstatus.equals("3")) {
			throw new BusinessException("单据必须审批完成！");
		}
		if ("Y".equals(hvo.getVdef11())) {
			throw new BusinessException("当前请购单已推送至供应商平台！");
		}
	}

	// 推送至供应商平台
	private void insertMaterial(PraybillHeaderVO hvo) throws BusinessException {
		PraybillItemVO[] itemvos = (PraybillItemVO[]) getHyPubBO()
				.queryByCondition(
						PraybillItemVO.class,
						"nvl(dr,0) = 0 and pk_praybill = '"
								+ hvo.getPrimaryKey() + "'");
		for (PraybillItemVO itemvo : itemvos) {
			// 如果物料有效，生成待签合同物料
			BdMaterialPriceVO mdBdMaterialPriceVO = ifEffect(itemvo
					.getPk_material());
			if (mdBdMaterialPriceVO != null) {
				insertPuCtMaterialVO(hvo, itemvo, mdBdMaterialPriceVO);
			} else {
				// 生成待询比物料
				insertPuPriceMaterial(hvo, itemvo);
			}
		}
		hvo.setVdef11("Y");// vdef11记录推送
		getHyPubBO().update(hvo);
	}

	// 生成待询比物料
	public void insertPuPriceMaterial(PraybillHeaderVO hvo,
			PraybillItemVO itemvo) throws BusinessException {
		PuPriceMaterialVO vo = new PuPriceMaterialVO();
		vo.setPk_praybill_b(itemvo.getPrimaryKey());// 请购明细主键
		vo.setPk_praybill(itemvo.getPk_praybill());// 请购单主键
		// 需求部门
		vo.setPk_dept(itemvo.getPk_reqdept());
		if (itemvo.getPk_reqdept() != null) {
			String dept_name = (String) getHyPubBO().findColValue(
					"org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept = '" + itemvo.getPk_reqdept()
							+ "'");
			vo.setDept_name(dept_name);
		}
		// 计划部门
		vo.setPk_plan_dept(hvo.getPk_plandept());
		if (hvo.getPk_plandept() != null) {
			String plan_dept_name = (String) getHyPubBO().findColValue(
					"org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept = '" + hvo.getPk_plandept()
							+ "'");
			vo.setPlan_dept_name(plan_dept_name);
		}
		// 采购组织
		vo.setPk_purchase(itemvo.getPk_purchaseorg());
		if (itemvo.getPk_purchaseorg() != null) {
			String purchase_code = (String) getHyPubBO().findColValue(
					"org_purchaseorg",
					"code",
					"nvl(dr,0) = 0 and pk_purchaseorg = '"
							+ itemvo.getPk_purchaseorg() + "'");
			vo.setPurchase_code(purchase_code);
			String purchase_name = (String) getHyPubBO().findColValue(
					"org_purchaseorg",
					"name",
					"nvl(dr,0) = 0 and pk_purchaseorg = '"
							+ itemvo.getPk_purchaseorg() + "'");
			vo.setPurchase_name(purchase_name);
		}
		vo.setVbillcode(hvo.getVbillcode());// 单据号
		vo.setDbilldate(hvo.getDbilldate().toStdString());// 单据日期
		// 计划员
		vo.setPk_psn(hvo.getPk_planpsn());
		if (hvo.getPk_planpsn() != null) {
			String psn_name = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and pk_psndoc = '" + hvo.getPk_planpsn()
							+ "'");
			vo.setPsn_name(psn_name);
		}
		// 库存组织
		vo.setPk_stock_org(hvo.getPk_org());
		if (hvo.getPk_org() != null) {
			String stock_code = (String) getHyPubBO()
					.findColValue(
							"org_stockorg",
							"code",
							"nvl(dr,0) = 0 and pk_stockorg = '"
									+ hvo.getPk_org() + "'");
			vo.setStock_code(stock_code);
			String stock_name = (String) getHyPubBO()
					.findColValue(
							"org_stockorg",
							"name",
							"nvl(dr,0) = 0 and pk_stockorg = '"
									+ hvo.getPk_org() + "'");
			vo.setStock_name(stock_name);
		}
		// 项目信息
		vo.setPk_project(itemvo.getCprojectid());
		if (itemvo.getCprojectid() != null) {
			String project_name = (String) getHyPubBO().findColValue(
					"bd_project",
					"project_name",
					"nvl(dr,0) = 0 and pk_project = '" + itemvo.getCprojectid()
							+ "'");
			vo.setProject_name(project_name);
		}
		// 物料信息
		vo.setPk_material(itemvo.getPk_material());
		if (itemvo.getPk_material() != null) {
			MaterialVO materialVO = (MaterialVO) getHyPubBO()
					.queryByPrimaryKey(MaterialVO.class,
							itemvo.getPk_material());
			vo.setMaterial_code(materialVO.getCode());// 物料编码
			vo.setMaterial_name(materialVO.getName());// 物料名称
			vo.setMaterial_spec(materialVO.getMaterialspec());// 规格
			vo.setMaterial_type(materialVO.getMaterialtype()); // 型号
			vo.setPk_mar_bas_class(materialVO.getPk_marbasclass());// 物料分类主键
			// 物料分类名称
			String mar_bas_class_name = (String) getHyPubBO().findColValue(
					"bd_marbasclass",
					"code",
					"nvl(dr,0) = 0 and pk_marbasclass = '"
							+ materialVO.getPk_marbasclass() + "'");
			vo.setMar_bas_class_name(mar_bas_class_name);
		}
		// 主单位
		if (itemvo.getCunitid() != null) {
			String main_meas_doc_name = (String) getHyPubBO().findColValue(
					"bd_measdoc",
					"name",
					"nvl(dr,0) = 0 and pk_measdoc  = '" + itemvo.getCunitid()
							+ "'");
			vo.setMain_meas_doc_name(main_meas_doc_name);
		}
		// 单位
		if (itemvo.getCastunitid() != null) {
			String meas_doc_name = (String) getHyPubBO().findColValue(
					"bd_measdoc",
					"name",
					"nvl(dr,0) = 0 and pk_measdoc  = '"
							+ itemvo.getCastunitid() + "'");
			vo.setMeas_doc_name(meas_doc_name);
		}
		vo.setNastnum(itemvo.getNastnum().toDouble());// 需求数量
		vo.setNnum(itemvo.getNnum().toDouble());// 需求主数量
		vo.setD_suggest_date(itemvo.getDsuggestdate().toStdString());// 建议订货日期
		vo.setVb_memo(itemvo.getVbmemo());// 备注
		vo.setD_req_date(itemvo.getDreqdate().toStdString());// 需求日期
		// 采购员
		vo.setPk_employee(itemvo.getPk_employee());
		if (itemvo.getPk_employee() != null) {
			String psn_doc_name = (String) getHyPubBO().findColValue(
					"bd_psndoc", "name",
					"nvl(dr,0) = 0 and pk_psndoc = '" + itemvo.getPk_employee()

					+ "'");
			vo.setPsn_doc_name(psn_doc_name);
		}
		getHyPubBO().insert(vo);
	}

	// 生成待签合同物料
	public void insertPuCtMaterialVO(PraybillHeaderVO hvo,
			PraybillItemVO itemvo, BdMaterialPriceVO mdBdMaterialPriceVO)
			throws BusinessException {
		PuCtMaterialVO vo = new PuCtMaterialVO();
		vo.setPk_supplier(itemvo.getPk_suggestsupplier());// 建议供应商
		// 供应商名称
		if (itemvo.getPk_suggestsupplier() != null) {
			String supplier_name = (String) getHyPubBO().findColValue(
					"bd_supplier",
					"name",
					"nvl(dr,0) = 0 and pk_supplier = '"
							+ itemvo.getPk_suggestsupplier() + "'");
			vo.setSupplier_name(supplier_name);
		}
		// 物料信息
		vo.setPk_material(itemvo.getPk_material());
		if (itemvo.getPk_material() != null) {
			MaterialVO materialVO = (MaterialVO) getHyPubBO()
					.queryByPrimaryKey(MaterialVO.class,
							itemvo.getPk_material());
			vo.setMaterial_code(materialVO.getCode());// 物料编码
			vo.setMaterial_name(materialVO.getName());// 物料名称
			vo.setMaterial_spec(materialVO.getMaterialspec());// 规格
			vo.setMaterial_type(materialVO.getMaterialtype()); // 型号
		}
		// 主单位
		if (itemvo.getCunitid() != null) {
			String main_meas_doc_name = (String) getHyPubBO().findColValue(
					"bd_measdoc",
					"name",
					"nvl(dr,0) = 0 and pk_measdoc  = '" + itemvo.getCunitid()
							+ "'");
			vo.setMain_meas_doc_name(main_meas_doc_name);
		}
		// 单位
		if (itemvo.getCastunitid() != null) {
			String meas_doc_name = (String) getHyPubBO().findColValue(
					"bd_measdoc",
					"name",
					"nvl(dr,0) = 0 and pk_measdoc  = '"
							+ itemvo.getCastunitid() + "'");
			vo.setMeas_doc_name(meas_doc_name);
		}
		vo.setVb_memo(itemvo.getVbmemo());
		vo.setNastnum(itemvo.getNastnum().toBigDecimal());// 主数量
		vo.setNnum(itemvo.getNnum().toBigDecimal());// 数量
		vo.setPrice(mdBdMaterialPriceVO.getTax_price());// 价格--物料价格档案含税单价
		vo.setAmount(vo.getPrice().multiply(vo.getNnum()));// 金额--物料价格档案含税单价*数量
		vo.setState(0);// 状态--生效
		vo.setPk_org(itemvo.getPk_purchaseorg());
		// 业务员-当前登录人
		String userID = InvocationInfoProxy.getInstance().getUserId();// 获取当前系统登录用户
		vo.setCuserid(userID);
		if (userID != null && StringUtils.isNotEmpty(userID)) {
			String user_name = (String) new HYPubBO().findColValue("sm_user",
					"user_name", " cuserid = '" + userID + "'");
			vo.setUser_id(user_name);
		}

		vo.setPk_praybill(hvo.getPrimaryKey());
		vo.setPk_praybill_b(itemvo.getPrimaryKey());
		getHyPubBO().insert(vo);
	}

	// 查询该物料是否有效
	public BdMaterialPriceVO ifEffect(String pk_material)
			throws BusinessException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
		// 当前时间
		String curdate = df.format(new Date());
		String whereSql = "dr = 0 and status = 0 and effective_start <= '"
				+ curdate + "' and effective_end >= '" + curdate
				+ "' and pk_material = '" + pk_material + "'";
		BdMaterialPriceVO[] bdMaterialPriceVOs = (BdMaterialPriceVO[]) getHyPubBO()
				.queryByCondition(BdMaterialPriceVO.class, whereSql);
		if (bdMaterialPriceVOs != null && bdMaterialPriceVOs.length > 0) {
			return bdMaterialPriceVOs[0];
		}
		return null;
	}
}
