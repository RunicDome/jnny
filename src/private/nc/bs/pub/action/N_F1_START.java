package nc.bs.pub.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
import nc.data.param.CommonParam;
import nc.itf.arap.forthird.IArapForDGSWService;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.arap.payable.PayableBillItemVO;
import nc.vo.arap.payable.PayableBillVO;
import nc.vo.bd.cust.CustSupplierVO;
import nc.vo.bd.material.MaterialVO;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.bd.supplier.SupplierVO;
import nc.vo.org.OrgVO;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import nc.ws.intf.oadata.util.F1OaUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

// 应付单提交
@SuppressWarnings({ "unused", "rawtypes", "unchecked", "restriction" })
public class N_F1_START extends N_F1_SAVE {

	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表名
	public static String WORKFLOWID_JNRL_CL = "76";
	private static String RL_WORKFLOW_ID_CL_TABLE_NAME = "材料应付单";

	public static String WORKFLOWID_JNRL_GC = "86";
	private static String RL_WORKFLOW_ID_GC_TABLE_NAME = "工程应付单";
	private static String RL_WORKFLOW_ID_FYFPHZ_TABLE_NAME = "费用发票汇总报账单";

	private static String RL_WORKFLOW_ID_ZCFPHZ_TABLE_NAME = "资产发票汇总账单";

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

	public N_F1_START() {
	}

	public Object runComClass(PfParameterVO pfparametervo)
			throws BusinessException {
		AggregatedValueObject[] obj = (AggregatedValueObject[]) super
				.runComClass(pfparametervo);
		for (AggregatedValueObject temp : obj) {
			BaseBillVO hvo = (BaseBillVO) temp.getParentVO();
			PayableBillVO hvo1 = (PayableBillVO) hvo;
			PayableBillItemVO[] mxVOs = (PayableBillItemVO[]) temp
					.getChildrenVO();
			String supplier = mxVOs[0].getSupplier();
			String pu_deptid = ((mxVOs == null || mxVOs.length <= 0) ? null
					: mxVOs[0].getPu_deptid());
			String pk_deptid = ((mxVOs == null || mxVOs.length <= 0) ? null
					: mxVOs[0].getPk_deptid());
			// 中台OA
			if ("4".equals(getDef2(hvo1.getPk_org()))) {
				String type = hvo1.getPk_tradetype();
				/*
				 * Map<String, String> billTypeMap = billTypeMap(); if
				 * (billTypeMap.containsKey(type)) { }
				 */
				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hvo.getPk_org(),
						type);
				if (oaVo != null && oaVo.getIsdr() == 0) {
					WorkFId = oaVo.getFlowid();
					TableName = oaVo.getTablename();
					String BillTypename = oaVo.getBilltypename();
					// 获取主表数据
					JSONArray headData = getNewMainMap(hvo1, supplier,
							pu_deptid, pk_deptid);
					// 获取子表数据
					JSONArray bodyData = getNewDtaileDataMap(temp, type);
					// 构造workflow信息
					OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
					workFlowVO.setPrimaryKey(hvo1.getPrimaryKey());
					workFlowVO.setPkGroup(hvo.getPk_group());
					workFlowVO.setPkOrg(hvo.getPk_org());
					workFlowVO.setBillMaker(hvo.getBillmaker());
					workFlowVO.setCreator(hvo.getCreator());
					workFlowVO.setBillCode("F1");
					// 测试临时写死，后期改为接口获取
					// workFlowVO.setWorkflowId(OaWorkFlowUtil.getOAFlowID(WorkFId));
					workFlowVO.setWorkflowId(WorkFId);
					workFlowVO.setWorkflowName(BillTypename);
					WorkFlowBill bill = OaWorkFlowUtil
							.getWorkFlowBill(workFlowVO);
					bill.setDef3("ZT");
					bill.setDef4(hvo.getBillno());
					// 制单人身份证号
					UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
							UserVO.class, hvo.getBillmaker());
					String idCard = (String) getHyPubBO().findColValue(
							"bd_psndoc",
							"id",
							"nvl(dr,0) = 0 and pk_psndoc='"
									+ userVO.getPk_psndoc() + "'");
					bill.setDef5(idCard);
					// 调用OA工具类同步数据至OA
					OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
				}
			} else if ("1".equals(getDef2(hvo1.getPk_org()))) {
				if ("D1".equals(hvo1.getPk_tradetype())) {
					// 材料应付单##@@
					JSONArray headData = F1OaUtil.getMainMap3(
							(PayableBillVO) temp.getParentVO(), supplier);
					JSONArray bodyData = F1OaUtil.getYfdDtaileDataMap(temp);
					/*
					 * step-1 调用OA工具类同步数据至OA
					 */
					OaWorkFlowUtil.sendOaData(headData, bodyData,
							getWorkFlowBill1(temp));
				}

				if ("F1-Cxx-01".equals(hvo1.getPk_tradetype())) {
					// 工程应付单##@@
					JSONArray headData = F1OaUtil.getMainMap4(
							(PayableBillVO) temp.getParentVO(), supplier);
					JSONArray bodyData = F1OaUtil.getYfdDtaileDataMap(temp);
					/*
					 * step-1 调用OA工具类同步数据至OA
					 */
					OaWorkFlowUtil.sendOaData(headData, bodyData,
							getWorkFlowBill1(temp));
				}
				if ("F1-Cxx-FYFPHZBZD".equals(hvo1.getPk_tradetype())) {
					// 费用发票总账汇报单##@@
					JSONArray headData = F1OaUtil.getMainMapFyfp(
							(PayableBillVO) temp.getParentVO(), supplier);
					JSONArray bodyData = F1OaUtil.getDtaileDataMapFyfp(temp);
					/*
					 * step-1 调用OA工具类同步数据至OA
					 */
					OaWorkFlowUtil.sendOaData(headData, bodyData,
							getWorkFlowBill1(temp));
				}
				if ("F1-Cxx-ZCBZ".equals(hvo1.getPk_tradetype())) {
					// 资产应付单##@@
					JSONArray headData = F1OaUtil.getMainMapFyfp(
							(PayableBillVO) temp.getParentVO(), supplier);
					JSONArray bodyData = F1OaUtil.getZCdDtaileDataMap(temp);
					/*
					 * step-1 调用OA工具类同步数据至OA
					 */
					OaWorkFlowUtil.sendOaData(headData, bodyData,
							getWorkFlowBill1(temp));
				}
				if ("F1-Cxx-SDRWGR".equals(hvo1.getPk_tradetype())) {
					// 水电燃及外购热发票汇总报账##
					JSONArray headData = F1OaUtil.getMainMapSDRfp(
							(PayableBillVO) temp.getParentVO(), supplier);
					JSONArray bodyData = F1OaUtil.getSDRDtaileDataMap(temp);
					/*
					 * step-1 调用OA工具类同步数据至OA
					 */
					OaWorkFlowUtil.sendOaData(headData, bodyData,
							getWorkFlowBill1(temp));
				}
			}
			// 调用东港税务系统同步单据状态
			IArapForDGSWService util = (IArapForDGSWService) NCLocator
					.getInstance().lookup(IArapForDGSWService.class);
			// 单据号，组织主键，事件类型，单据类型
			JSONObject res = util.sendBillByNCBill(hvo.getPrimaryKey(),
					hvo.getPk_org(), CommonParam.COMMIT, hvo.getPk_billtype());
			if (!"Y".equals(res.getString("success"))) {
				throw new BusinessException(res.getString("errinfo"));
			}
		}
		return obj;
	}

	/*
	 * private Map<String, String> billTypeMap() { Map<String, String> typeMap =
	 * new HashMap<String, String>();
	 * 
	 * String ZT_CLYF = "D1"; typeMap.put(ZT_CLYF, "材料应付单");
	 * 
	 * String ZT_GCYF = "F1-Cxx-01"; typeMap.put(ZT_GCYF, "工程应付单");
	 * 
	 * String ZT_QTYF = "F1-Cxx-RQ-03"; typeMap.put(ZT_QTYF, "其他应付");
	 * 
	 * return typeMap; }
	 */

	private JSONArray getNewDtaileDataMap(AggregatedValueObject temp,
			String type) throws BusinessException {
		// 获取详细信息
		PayableBillItemVO[] bvos = (PayableBillItemVO[]) temp.getChildrenVO();
		Map bodyMap = getBody(bvos, type);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
		return dtlistString;
	}

	private Map getBody(PayableBillItemVO[] bvo, String type)
			throws BusinessException {
		Map dtMap = new HashMap();
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (PayableBillItemVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			if (null != temp.getProject()) {
				// 项目
				ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
						.queryByPrimaryKey(ProjectHeadVO.class,
								temp.getProject());
				/* 项目 */
				String name = (String) getHyPubBO().findColValue(
						"bd_project",
						"project_name",
						"nvl(dr,0) = 0 and pk_project  = '" + temp.getProject()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("xm",
						name));
				// 项目编码
				String code = (String) getHyPubBO().findColValue(
						"bd_project",
						"project_code",
						"nvl(dr,0) = 0 and pk_project  = '" + temp.getProject()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"xmbm", code));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bproject_code", projectVO.getProject_code()));
				// 项目名称
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bproject_name", projectVO.getProject_name()));
			}
			if (null != temp.getMaterial()) {
				MaterialVO materialVO = (MaterialVO) getHyPubBO()
						.queryByPrimaryKey(MaterialVO.class, temp.getMaterial());
				/* 物料主键 */
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"wlzj", temp.getMaterial()));
				// 物料编码
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"material_code", materialVO.getCode()));
				// 物料名称
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"material_name", materialVO.getName()));
				// 规格
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"materialspec", materialVO.getMaterialspec()));
				// 型号
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"materialtype", materialVO.getMaterialtype()));
			}
			/* 供应商 */
			if (null != temp.getSupplier()) {
				String name = (String) getHyPubBO().findColValue(
						"bd_supplier",
						"name",
						"nvl(dr,0) = 0 and  pk_supplier  = '"
								+ temp.getSupplier() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("gys",
						name));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"pk_supplier_name", name));
			}
			// 税码
			if (temp.getTaxcodeid() != null) {
				String sm = (String) getHyPubBO().findColValue(
						"bd_taxcode",
						"code",
						"nvl(dr,0) = 0 and pk_taxcode = '"
								+ temp.getTaxcodeid() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sm",
						sm));
			}
			// 结算方式
			if (temp.getPk_balatype() != null) {
				String jsfsbm = (String) getHyPubBO().findColValue(
						"bd_balatype",
						"code",
						"nvl(dr,0) = 0 and pk_balatype = '"
								+ temp.getPk_balatype() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"jsfsbm", jsfsbm));
				String jsfs = (String) getHyPubBO().findColValue(
						"bd_balatype",
						"name",
						"nvl(dr,0) = 0 and pk_balatype = '"
								+ temp.getPk_balatype() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"jsfs", jsfs));
			}
			// 费用申请部门
			if (temp.getPu_deptid() != null) {
				String fysqbm = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept = '" + temp.getPu_deptid()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fysqbm", fysqbm));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fyfsbm", fysqbm));
			}
			// 收支项目
			if (temp.getPk_subjcode() != null) {
				String szxm = (String) getHyPubBO().findColValue(
						"bd_inoutbusiclass",
						"name",
						"nvl(dr,0) = 0 and pk_inoutbusiclass  ='"
								+ temp.getPk_subjcode() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"szxm", szxm));
				/* 预算类别 */
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"yslb", szxm));
			}
			// 发票类型
			if (temp.getDef27() != null) {
				String fplx = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc ='" + temp.getDef27()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fplx", fplx));
			}

			/* 票据类型 */
			if (null != temp.getDef30()) {
				String pjlx = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc ='" + temp.getDef30()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"pjlx", pjlx));
			}

			/* 代控部门主键 */
			if ("D1".equals(type) || "F1-Cxx-01".equals(type)) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dkbmzj", temp.getPu_deptid()));
				/* 代控部门编码 */
				String dkbmstr = (String) getHyPubBO().findColValue(
						"org_dept",
						"code",
						"nvl(dr,0) = 0 and pk_dept = '" + temp.getPu_deptid()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dkbmbm", dkbmstr));
				/* 代控部门 */
				String dk = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept = '" + temp.getPu_deptid()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dkbm", dk));
				/* 费用申请部门 */
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fysqbm", dk));
			} else {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dkbmzj", temp.getPk_deptid()));
				/* 代控部门编码 */
				String dkbmstr = (String) getHyPubBO().findColValue(
						"org_dept",
						"code",
						"nvl(dr,0) = 0 and pk_dept = '" + temp.getPk_deptid()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dkbmbm", dkbmstr));
				/* 代控部门 */
				String dk = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept = '" + temp.getPk_deptid()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dkbm", dk));
				/* 生产类型 */
				if (null != temp.getDef39()) {
					String sclx = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc ='" + temp.getDef39()
									+ "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"sclx", sclx));
				}
			}

			/* 合同号 */
			if (null != temp.getContractno()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("hth",
						temp.getContractno()));
			}
			/* 发票号 */
			String str = "0";
			if (null != temp.getInvoiceno()) {
				str = temp.getInvoiceno();
			}
			workflowRequestTableFields.add(OaWorkFlowUtil
					.listAddObj("fph", str));
			/* 摘要 */
			if (null != temp.getScomment()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("zy",
						temp.getScomment()));
			}
			/* 往来对象3=业务员，2=部门，1=供应商 */
			if (null != temp.getObjtype()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"wldx", temp.getObjtype() + ""));
			}
			/* 发票金额 */
			if (null != temp.getLocal_money_cr()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fpje", temp.getLocal_money_cr() + ""));
			}
			/* 单价 */
			if (null != temp.getPrice()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("dj",
						temp.getPrice() + ""));
			}
			/* 含税单价 */
			if (null != temp.getTaxprice()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"hsdj", temp.getTaxprice() + ""));
			}
			/* 贷方数量 */
			if (null != temp.getTaxprice()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dfsl", temp.getQuantity_cr() + ""));
			}
			/* 不含税金额 */
			if (null != temp.getLocal_notax_cr() && temp.getDef15() == null) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bhsje", temp.getLocal_notax_cr() + ""));
			}

			/* 票据类型主键 */
			if (null != temp.getChecktype()) {
				String pjlxstr = (String) getHyPubBO().findColValue(
						"bd_notetype",
						"name",
						"nvl(dr,0) = 0 and pk_notetype  = '"
								+ temp.getChecktype() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"pjlxzj", temp.getChecktype()));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"pjlx", pjlxstr));
			}
			/* 仓库 */
			if (null != temp.getDef26()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("ck",
						temp.getDef26()));
			}
			/* 不可抵扣金额 */
			if (null != temp.getNosubtax()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bkdkje", temp.getNosubtax() + ""));
			}
			/* 物料 */
			if (null != temp.getDef13()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("wl",
						temp.getDef13()));
			}
			/* 规格类型 */
			if (null != temp.getDef50()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"gglx", temp.getDef50()));
			}
			/* 摘要 */
			if (null != temp.getDef22()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("zy",
						temp.getDef22()));
			}
			/* 含税金额 */
			if (null != temp.getLocal_money_cr()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"hsje", temp.getLocal_money_cr() + ""));
			}
			/* 不含税金额 */
			if (null != temp.getDef15()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bhsje", temp.getDef15()));
			}
			/* 换热站 */
			if (null != temp.getDef30()) {
				String hrz = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc ='" + temp.getDef30()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("hrz",
						hrz));
			} else {
				String hrz = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc ='" + temp.getDef44()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("hrz",
						hrz));
			}
			/* 费用名称 */
			if (null != temp.getDef13()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fymc", temp.getDef13()));
			}
			/* 资产类别 */
			if (temp.getDef23() != null) {
				String zclb = (String) getHyPubBO().findColValue(
						"tb_budgetsub", "objname",
						"nvl(dr,0) = 0 and pk_obj = '" + temp.getDef23() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"zclb", zclb));
			}
			// 材料应付单 工程应付单 其他应付 燃器具类应付 工程材料应付 燃气应付
			if ("D1".equals(type) || "F1-Cxx-01".equals(type)
					|| "F1-Cxx-RQ-01".equals(type)
					|| "F1-Cxx-RQ-02".equals(type)
					|| "F1-Cxx-RQ-03".equals(type)
					|| "F1-Cxx-RQ-04".equals(type)) {
				/* 税率 */
				if (temp.getTaxrate() != null) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"sl", temp.getTaxrate() + ""));
				}
				/* 税额 */
				if (temp.getLocal_tax_cr() != null) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"se", temp.getLocal_tax_cr() + ""));
				}
			} else if ("F1-Cxx-ZCBZ".equals(type)
					|| "F1-Cxx-FYFPHZBZD".equals(type)
					|| "F1-Cxx-TSSXSPD".equals(type)
					|| "F1-Cxx-SDRWGR".equals(type)
					|| "F1-Cxx-gcgxfpgx-nc".equals(type)) {
				// 资产发票汇总报账 费用发票汇总报账 特殊事项发票汇总报账 水电燃及外购热发票汇总报账 工程共享-发票挂账单
				// 税率
				if (temp.getDef17() != null) {
					String sl = (String) getHyPubBO().findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc ='" + temp.getDef17()
									+ "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"sl", sl));
				}
				/* 税额 */
				if (temp.getDef19() != null) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"se", temp.getDef19()));
				}
			}
			// 其他字段------end
			Map workflowRequestTableFieldsMap = new HashMap();
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	private JSONArray getNewMainMap(PayableBillVO parentVO, String pk_supplier,
			String pu_deptid, String pk_deptid) throws BusinessException {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// ---------其他字段begin
		String billtype = parentVO.getPk_tradetype();
		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				parentVO.getPk_org());
		// 组织
		String stockName = (String) getHyPubBO().findColValue(
				"org_financeorg",
				"name",
				"nvl(dr,0) = 0 and pk_financeorg = '" + parentVO.getPk_org()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("zzmc", stockName));
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		// 合同类型名称
		String billTypeName = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and pk_billtypeid = '"
						+ parentVO.getPk_tradetypeid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("typename", billTypeName));
		// 应付款类型
		if (parentVO.getDef57() != null) {
			String yfklxbm = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef57()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("yfklxbm", yfklxbm));
			String yfklx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef57()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("yfklx", yfklx));
		}

		// 部门
		if (null != parentVO.getPk_deptid_v()) {
			String sqbmName = (String) getHyPubBO().findColValue(
					"org_dept_v",
					"name",
					"nvl(dr,0) = 0 and pk_vid = '" + parentVO.getPk_deptid_v()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("bm", sqbmName));
		}

		// 费用申请部门
		if (null != pu_deptid) {
			String fysqbm = (String) getHyPubBO().findColValue("org_dept",
					"name", "nvl(dr,0) = 0 and pk_dept = '" + pu_deptid + "'");
			list.add(OaWorkFlowUtil.listAddObj("fysqbm", fysqbm));
		}

		// 申请人
		if (null != userVO.getUser_name()) {
			list.add(OaWorkFlowUtil.listAddObj("sqr", userVO.getUser_name()));
		}

		// 供应商
		if (null != pk_supplier) {
			SupplierVO supplierVO = (SupplierVO) getHyPubBO()
					.queryByPrimaryKey(SupplierVO.class, pk_supplier);
			list.add(OaWorkFlowUtil.listAddObj("pk_supplier_name",
					supplierVO.getName()));
		}

		// 制单人用户编码
		if (null != userVO.getUser_name()) {
			list.add(OaWorkFlowUtil.listAddObj("usercode",
					userVO.getUser_code()));
		}
		// 制单人名称
		if (null != userVO.getUser_name()) {
			list.add(OaWorkFlowUtil.listAddObj("username",
					userVO.getUser_name()));
		}
		// 制单人身份证号
		String idCard = (String) getHyPubBO().findColValue("bd_psndoc", "id",
				"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
		if (null == idCard) {
			throw new BusinessException("制单人身份证号码未维护");
		}
		list.add(OaWorkFlowUtil.listAddObj("userid", idCard));
		list.add(OaWorkFlowUtil.listAddObj("zdrzj", userVO.getUser_code()));
		// 供应商
		if (pk_supplier != null && !"".equals(pk_supplier)) {
			CustSupplierVO custVO = (CustSupplierVO) getHyPubBO()
					.queryByPrimaryKey(CustSupplierVO.class, pk_supplier);
			// 供应商编码
			list.add(OaWorkFlowUtil.listAddObj("pk_supplier_code",
					custVO.getCode()));
			// 供应商名称
			list.add(OaWorkFlowUtil.listAddObj("pk_supplier_name",
					custVO.getName()));
		}
		// 交易类型名称
		if (null != parentVO.getPk_tradetypeid()) {
			String typeName = (String) getHyPubBO().findColValue(
					"bd_billtype",
					"billtypename",
					"nvl(dr,0) = 0 and pk_billtypeid = '"
							+ parentVO.getPk_tradetypeid() + "'");
			list.add(OaWorkFlowUtil.listAddObj("typename", typeName));
		}
		// 来源系统名称
		Map<Integer, String> sourcesystypeMap = sourcesystypeMap();
		if (sourcesystypeMap.containsKey(parentVO.getSrc_syscode())) {
			list.add(OaWorkFlowUtil.listAddObj("sourcesystypename",
					sourcesystypeMap.get(parentVO.getSrc_syscode())));
		}
		// 备注
		if (null != parentVO.getDef12()) {
			list.add(OaWorkFlowUtil.listAddObj("bz", parentVO.getDef12()));
		}
		if (null != parentVO.getDef20()) {
			String fycdbmbm = (String) getHyPubBO().findColValue("org_dept",
					"code",
					"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getDef20() + "'");
			list.add(OaWorkFlowUtil.listAddObj("fycdbmbm", fycdbmbm));
			String fycdbm = (String) getHyPubBO().findColValue("org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getDef20() + "'");
			list.add(OaWorkFlowUtil.listAddObj("fycdbm", fycdbm));
		}
		if (null != parentVO.getScomment()) {
			list.add(OaWorkFlowUtil.listAddObj("scomment",
					parentVO.getScomment()));
		}
		/* 原OA合同流水号 */
		if (parentVO.getDef10() != null) {
			list.add(OaWorkFlowUtil.listAddObj("yoahtlsh", parentVO.getDef10()));
		}
		/* 发票类型（自定义档案） */
		if (null != parentVO.getDef31()) {
			String fplx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef31()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("fplxzdyda", fplx));
		}
		/* 挂账类别（自定义档案） */
		if (null != parentVO.getDef92()) {
			String gzlb = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef92()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("gzlb", gzlb));
		}
		/* 凭证号 */
		if (null != parentVO.getDef79()) {
			list.add(OaWorkFlowUtil.listAddObj("pzh", parentVO.getDef79()));
		}

		if (null != orgVO) {
			/* 组织 */
			list.add(OaWorkFlowUtil.listAddObj("szgs", orgVO.getName()));
		}
		/* 单据主键 */
		list.add(OaWorkFlowUtil.listAddObj("djzj", parentVO.getPk_payablebill()));
		/* 组织主键 */
		list.add(OaWorkFlowUtil.listAddObj("zzzj", parentVO.getPk_org()));
		/* 单据号 */
		list.add(OaWorkFlowUtil.listAddObj("djh", parentVO.getBillno()));
		/* 单据日期 */
		list.add(OaWorkFlowUtil.listAddObj("djrq", parentVO.getBilldate()
				.getYear()
				+ "-"
				+ parentVO.getBilldate().getStrMonth()
				+ "-"
				+ parentVO.getBilldate().getStrDay()));
		list.add(OaWorkFlowUtil.listAddObj("bmzj", parentVO.getPk_deptid()));
		/* 代控部门主键 */
		if ("D1".equals(billtype) || "F1-Cxx-01".equals(billtype)) {
			list.add(OaWorkFlowUtil.listAddObj("dkbmzj", pu_deptid));

			/* 代控部门编码 */
			String dkbmbmstr = (String) getHyPubBO().findColValue("org_dept",
					"code", "nvl(dr,0) = 0 and pk_dept = '" + pu_deptid + "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbmbm", dkbmbmstr));

			/* 代控部门 */
			String dname = (String) getHyPubBO().findColValue("org_dept",
					"name", "nvl(dr,0) = 0 and pk_dept = '" + pu_deptid + "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbm", dname));
			/* 工程类别 */
			if (null != parentVO.getDef75()) {
				String gclb = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef75()
								+ "'");
				list.add(OaWorkFlowUtil.listAddObj("gclb", gclb));
			}
		} else {
			list.add(OaWorkFlowUtil.listAddObj("dkbmzj", pk_deptid));

			/* 代控部门编码 */
			String dkbmbmstr = (String) getHyPubBO().findColValue("org_dept",
					"code", "nvl(dr,0) = 0 and pk_dept = '" + pk_deptid + "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbmbm", dkbmbmstr));

			/* 代控部门 */
			String dname = (String) getHyPubBO().findColValue("org_dept",
					"name", "nvl(dr,0) = 0 and pk_dept = '" + pk_deptid + "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbm", dname));

		}
		/* 合同名称 */
		if (null != parentVO.getDef36()) {
			list.add(OaWorkFlowUtil.listAddObj("htmc", parentVO.getDef36()));
		}
		/* 本次发票金额 */
		if (null != parentVO.getLocal_money()) {
			list.add(OaWorkFlowUtil.listAddObj("bckpje",
					parentVO.getLocal_money() + ""));
		}
		/*
		 * 合同名 if (null != parentVO.getDef4()) {
		 * list.add(OaWorkFlowUtil.listAddObj("htmc", parentVO.getDef4())); }
		 */
		/* 发票金额 */
		if (null != parentVO.getLocal_money()) {
			list.add(OaWorkFlowUtil.listAddObj("fpje",
					parentVO.getLocal_money() + ""));
		}
		/* 合同总金额 */
		if (null != parentVO.getDef70()) {
			list.add(OaWorkFlowUtil.listAddObj(
					"htzje",
					new UFDouble(parentVO.getDef70() == null ? "0.00"
							: parentVO.getDef70()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ ""));
		}
		// 合同现用名
		if (null != parentVO.getDef4()) {
			String htxym = (String) getHyPubBO().findColValue(
					"bd_supplier",
					"name",
					"nvl(dr,0) = 0 and pk_supplier  = '" + parentVO.getDef4()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("htxym", htxym));
		}
		/* 供应商 */
		if (null != parentVO.getSupplier()) {
			String name = (String) getHyPubBO().findColValue(
					"bd_supplier",
					"name",
					"nvl(dr,0) = 0 and  pk_supplier  = '"
							+ parentVO.getSupplier() + "'");
			list.add(OaWorkFlowUtil.listAddObj("gys", name));
			list.add(OaWorkFlowUtil.listAddObj("pk_supplier_name", name));
		}
		/* 挂账类型 */
		if (null != parentVO.getDef3()) {
			String gzlx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef3()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("gzlx", gzlx));
		}
		/* 合同号 */
		if (null != parentVO.getContractno()) {
			list.add(OaWorkFlowUtil.listAddObj("hth", parentVO.getContractno()));
		}
		/* 合同编码 */
		if (null != parentVO.getDef35()) {
			list.add(OaWorkFlowUtil.listAddObj("htbm", parentVO.getDef35()));
		}
		/* 金额大写 */
		/*
		 * if (null != parentVO.getLocal_money()) {
		 * list.add(OaWorkFlowUtil.listAddObj("jedx",
		 * ConvertUpMoney.toChinese(parentVO.getLocal_money() + ""))); }
		 */
		// 费用申请部门编码
		if (null != parentVO.getPu_deptid()) {
			String dk = (String) getHyPubBO().findColValue(
					"org_dept",
					"code",
					"nvl(dr,0) = 0 and pk_dept = '" + parentVO.getPu_deptid()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("fysqbmzj", dk));
		}
		/* 附件张数 */
		if (null != parentVO.getAccessorynum()) {
			list.add(OaWorkFlowUtil.listAddObj("fjzs",
					parentVO.getAccessorynum() + ""));
		}
		/* 发票说明 */
		if (null != parentVO.getDef2()) {
			list.add(OaWorkFlowUtil.listAddObj("fpsm", parentVO.getDef2()));
		}
		/* 申请单位 */
		if (null != parentVO.getPk_org()) {
			String sqdw = (String) getHyPubBO().findColValue(
					"org_financeorg",
					"name",
					"nvl(dr,0) = 0 and pk_financeorg = '"
							+ parentVO.getPk_org() + "'");
			list.add(OaWorkFlowUtil.listAddObj("sqdw", sqdw));
		}
		// 往来对象 3=业务员，2=部门，1=供应商
		if (null != parentVO.getObjtype()) {
			list.add(OaWorkFlowUtil.listAddObj("wldx", parentVO.getObjtype()
					+ ""));
			Integer wlduint = parentVO.getObjtype();
			String wldx = ""; // 往来对象
			if (wlduint == 1) {
				wldx = "供应商";
			} else if (wlduint == 2) {
				wldx = "部门";
			} else if (wlduint == 3) {
				wldx = "业务员";
			}
			list.add(OaWorkFlowUtil.listAddObj("wldxmc", wldx + ""));
		}
		/* 业务员 */
		if (null != parentVO.getPk_psndoc()) {
			String ywy = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and pk_psndoc  = '"
							+ parentVO.getPk_psndoc() + "'");
			list.add(OaWorkFlowUtil.listAddObj("ywy", ywy));
		}
		/* 已开发票金额 */
		if (null != parentVO.getDef72()) {
			list.add(OaWorkFlowUtil.listAddObj(
					"ykfpje",
					new UFDouble(parentVO.getDef72() == null ? "0.00"
							: parentVO.getDef72()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ ""));
		}
		/* 本次发票金额 */
		if (null != parentVO.getLocal_money()) {
			list.add(OaWorkFlowUtil.listAddObj("bckpje",
					parentVO.getLocal_money() + ""));
		}
		/* 累计开票金额 */
		if (null != parentVO.getDef30()) {
			if ("D1".equals(billtype) || "F1-Cxx-01".equals(billtype)) {
				list.add(OaWorkFlowUtil.listAddObj(
						"ljkpje",
						new UFDouble(parentVO.getDef30() == null ? "0.00"
								: parentVO.getDef30()).setScale(2,
								UFDouble.ROUND_HALF_UP)
								+ ""));
			}
		}
		/* 税率 */
		if (null != parentVO.getDef32()) {
			list.add(OaWorkFlowUtil.listAddObj("sl", parentVO.getDef32()));
		}

		/* 税额 */
		if (null != parentVO.getDef33()) {
			list.add(OaWorkFlowUtil.listAddObj(
					"se",
					new UFDouble(parentVO.getDef33() == null ? "0.00"
							: parentVO.getDef33()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ ""));
		}
		/* 税额变更后金额 */
		if (null != parentVO.getDef34()) {
			list.add(OaWorkFlowUtil.listAddObj(
					"sebghje",
					new UFDouble(parentVO.getDef34() == null ? "0.00"
							: parentVO.getDef34()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ ""));
		}

		/* 审计金额 */
		if (null != parentVO.getDef36()) {
			if ("D1".equals(billtype) || "F1-Cxx-01".equals(billtype)) {
				list.add(OaWorkFlowUtil.listAddObj(
						"sjje",
						new UFDouble(parentVO.getDef36() == null ? "0.00"
								: parentVO.getDef36()).setScale(2,
								UFDouble.ROUND_HALF_UP)
								+ ""));
			}
		}
		/* 付款类别 */
		if (null != parentVO.getDef23()) {
			String fklb = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef23()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("fklb", fklb));
		}
		/* 核算人员 */
		if (null != parentVO.getDef13()) {
			String hsry = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and pk_psndoc = '" + parentVO.getDef13()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("hsry", hsry));
		}
		// 事由填写规范
		if (null != parentVO.getDef48()) {
			String sytxgf = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef48()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sytxgf", sytxgf));
		}
		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	/***
	 * 材料应付单//工程应付单// 费用发票汇总报账单
	 * 
	 * @param temp
	 * @return
	 * @throws BusinessException
	 */
	private WorkFlowBill getWorkFlowBill1(AggregatedValueObject temp)
			throws BusinessException {
		String where = " nvl(dr,0) = 0 and pk_bill = '"
				+ ((PayableBillVO) temp.getParentVO()).getPk_payablebill()
				+ "'";
		WorkFlowBill[] workFlowBills = (WorkFlowBill[]) F1OaUtil.getHyPubBO()
				.queryByCondition(WorkFlowBill.class, where);
		WorkFlowBill workFlowBill = new WorkFlowBill();
		if (null != workFlowBills && workFlowBills.length > 1) {
			throw new BusinessException("查询到多条流程记录");
		}
		PayableBillVO hvo1 = (PayableBillVO) temp.getParentVO();
		if (null != workFlowBills && workFlowBills.length == 1) {
			workFlowBill = workFlowBills[0];
			if ("D1".equals(hvo1.getPk_tradetype())) {
				workFlowBill.setWorkflowName(RL_WORKFLOW_ID_CL_TABLE_NAME);
			}
			if ("F1-Cxx-01".equals(hvo1.getPk_tradetype())) {
				workFlowBill.setWorkflowName(RL_WORKFLOW_ID_GC_TABLE_NAME);
			}
			if ("F1-Cxx-FYFPHZBZD".equals(hvo1.getPk_tradetype())) {
				workFlowBill.setWorkflowName(RL_WORKFLOW_ID_FYFPHZ_TABLE_NAME);
			}
			if ("F1-Cxx-ZCBZ".equals(hvo1.getPk_tradetype())) {
				workFlowBill.setWorkflowName("资产发票汇总账单");
			}
			if ("F1-Cxx-SDRWGR".equals(hvo1.getPk_tradetype())) {
				workFlowBill.setWorkflowName("水电燃及外购热发票汇总报账");
			}
		} else {
			UserVO userVO = (UserVO) F1OaUtil.getHyPubBO().queryByPrimaryKey(
					UserVO.class,
					((PayableBillVO) temp.getParentVO()).getBillmaker());
			workFlowBill.setDef5(userVO.getUser_code());
			if ("2".equals(getDef2(((PayableBillVO) temp.getParentVO())
					.getPk_org()))) {
				PsndocVO psndoc = (PsndocVO) F1OaUtil.getHyPubBO()
						.queryByPrimaryKey(PsndocVO.class,
								userVO.getPk_psndoc());
				workFlowBill.setDef5(psndoc.getId());
			}
			workFlowBill.setPk_group(((PayableBillVO) temp.getParentVO())
					.getPk_group());
			workFlowBill.setPk_org(((PayableBillVO) temp.getParentVO())
					.getPk_org());
			workFlowBill.setPk_bill(((PayableBillVO) temp.getParentVO())
					.getPk_payablebill());
			workFlowBill.setUser_name(userVO.getUser_name());
			workFlowBill.setUser_code(userVO.getUser_code());
			workFlowBill.setCreationtime(new UFDate().toStdString());
			workFlowBill.setBill_status("1");
			workFlowBill.setCreator(((PayableBillVO) temp.getParentVO())
					.getBillmaker());
			if ("D1".equals(hvo1.getPk_tradetype())) {
				workFlowBill.setBill_code("F1");
				if ("2".equals(getDef2(hvo1.getPk_org()))) {
					workFlowBill.setWorkflowId("404");
				} else {
					workFlowBill.setWorkflowId(WORKFLOWID_JNRL_CL);
				}
				// workFlowBill.setWorkflowId(WORKFLOWID_JNRL_CL);
				workFlowBill.setWorkflowName(RL_WORKFLOW_ID_CL_TABLE_NAME);
			}
			// 工程应付单
			if ("F1-Cxx-01".equals(hvo1.getPk_tradetype())) {
				workFlowBill.setBill_code("F1");
				if ("2".equals(getDef2(hvo1.getPk_org()))) {
					workFlowBill.setWorkflowId("403");
				} else {
					workFlowBill.setWorkflowId(WORKFLOWID_JNRL_GC);
				}
				workFlowBill.setWorkflowName(RL_WORKFLOW_ID_GC_TABLE_NAME);
			}
			if ("F1-Cxx-FYFPHZBZD".equals(hvo1.getPk_tradetype())) {
				workFlowBill.setWorkflowName(RL_WORKFLOW_ID_FYFPHZ_TABLE_NAME);
				workFlowBill.setBill_code("F1");
				if ("2".equals(getDef2(hvo1.getPk_org()))) {
					workFlowBill.setWorkflowId("402");
				} else {
					workFlowBill.setWorkflowId("106");
				}
			}
			if ("F1-Cxx-ZCBZ".equals(hvo1.getPk_tradetype())) {
				workFlowBill.setWorkflowName("资产发票汇总账单");
				workFlowBill.setBill_code("F1");
				if ("2".equals(getDef2(hvo1.getPk_org()))) {
					workFlowBill.setWorkflowId("392");
				} else {
					workFlowBill.setWorkflowId("107");
				}
			}
			if ("F1-Cxx-SDRWGR".equals(hvo1.getPk_tradetype())) {
				workFlowBill.setWorkflowName("水电燃及外购热发票汇总报账");
				workFlowBill.setBill_code("F1");
				if ("2".equals(getDef2(hvo1.getPk_org()))) {
					workFlowBill.setWorkflowId("471");
				}
			}
		}
		return workFlowBill;
	}

	public String getDef2(String pk_org) throws BusinessException {
		String def2 = "";
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		if (null != orgVO) {
			def2 = orgVO.getDef2();
		}
		return def2;
	}

	private Map<Integer, String> sourcesystypeMap() {
		Map<Integer, String> map = new HashMap<Integer, String>();
		map.put(0, "应收系统");
		map.put(1, "应付系统");
		map.put(2, "现金管理");
		map.put(3, "销售系统");
		map.put(4, "采购系统");
		map.put(5, "资金结算");
		map.put(6, "网上银行");
		map.put(7, "付款排程");
		map.put(8, "票据管理");
		map.put(9, "协同单据");
		map.put(10, "信贷系统");
		map.put(12, "项目管理");
		map.put(13, "全面预算");
		map.put(16, "内部交易");
		map.put(17, "外部交换平台");
		map.put(18, "资金计息");
		map.put(19, "库存管理");
		map.put(20, "合同");
		map.put(24, "运输");
		map.put(104, "资产管理");
		map.put(105, "费用管理");
		map.put(109, "零售系统");
		map.put(113, "进出口系统");
		map.put(114, "CRM");
		map.put(115, "信用证管理");
		return map;
	}
}
