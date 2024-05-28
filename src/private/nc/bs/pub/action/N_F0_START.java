package nc.bs.pub.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.trade.business.HYPubBO;
import nc.data.param.CommonParam;
import nc.itf.arap.forthird.IArapForDGSWService;
import nc.pub.tools.VOUtils;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.arap.receivable.ReceivableBillItemVO;
import nc.vo.arap.receivable.ReceivableBillVO;
import nc.vo.bd.material.MaterialVO;
import nc.vo.bd.material.measdoc.MeasdocVO;
import nc.vo.org.OrgVO;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

// 应收单
@SuppressWarnings("restriction")
public class N_F0_START extends N_F0_SAVE {

	private static String BILLTYPE = "F0"; // 单据类型
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表名

	public N_F0_START() {
	}

	public Object runComClass(PfParameterVO pfparametervo)
			throws BusinessException {
		AggregatedValueObject[] obj = (AggregatedValueObject[]) super
				.runComClass(pfparametervo);
		// NC配置的与OA对接信息表
		for (AggregatedValueObject temp : obj) {
			BaseBillVO hvo = (BaseBillVO) temp.getParentVO();
			ReceivableBillVO hvo1 = (ReceivableBillVO) hvo;
			String customer = ((ReceivableBillItemVO[]) temp.getChildrenVO())[0]
					.getCustomer();
			String transi_type = hvo.getPk_tradetype();// 交易类型
			if ("4".equals(getDef2(hvo1.getPk_org()))) {
				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hvo.getPk_org(),
						BILLTYPE);
				/*
				 * if (oaVo.getIsdr() != 0) { // OA配置信息不可用 throw new
				 * BusinessException("RL_GHOAWORK表查询OA表名和ID出错，信息未启用，请先启用！辅助信息："
				 * + "所属组织-->" + hvo.getPk_org()+ ", 单据类型-->" + BILLTYPE); }
				 */
				if (oaVo != null && oaVo.getIsdr() == 0) {
					WorkFId = oaVo.getFlowid();
					TableName = oaVo.getTablename();
					// 获取主表数据
					JSONArray headData = getNewMainMap(hvo1, customer);
					// 获取子表数据
					JSONArray bodyData = getNewDtaileDataMap(temp);
					// 构造workflow信息
					OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
					workFlowVO.setPrimaryKey(hvo1.getPrimaryKey());
					workFlowVO.setPkGroup(hvo.getPk_group());
					workFlowVO.setPkOrg(hvo.getPk_org());
					workFlowVO.setBillMaker(hvo.getBillmaker());
					workFlowVO.setCreator(hvo.getCreator());
					workFlowVO.setBillCode(BILLTYPE);
					// 接口获取oa流程ID
					workFlowVO.setWorkflowId(OaWorkFlowUtil
							.getOAFlowID(WorkFId));
					workFlowVO.setWorkflowName(oaVo.getBilltypename());
					WorkFlowBill bill = OaWorkFlowUtil
							.getWorkFlowBill(workFlowVO);
					bill.setDef3("ZT");
					bill.setDef4(hvo.getBillno());// 单据编号
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
				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hvo.getPk_org(),
						transi_type);
//				if (!"F0-Cxx-001".equals(transi_type)
//						&& !"F0-Cxx-002".equals(transi_type)) {
				if(oaVo != null && oaVo.getIsdr() == 0){
					// 热力
//					WorkFId = "144";
//					TableName = "formtable_main_197";
					WorkFId = oaVo.getFlowid();
					TableName = oaVo.getTablename();
					// 获取主表数据
					JSONArray headData = getNewMainMap(hvo1, customer);
					// 获取子表数据
					JSONArray bodyData = getNewDtaileDataMap(temp);
					// 构造workflow信息
					OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
					workFlowVO.setPrimaryKey(hvo1.getPrimaryKey());
					workFlowVO.setPkGroup(hvo.getPk_group());
					workFlowVO.setPkOrg(hvo.getPk_org());
					workFlowVO.setBillMaker(hvo.getBillmaker());
					workFlowVO.setCreator(hvo.getCreator());
					workFlowVO.setBillCode(BILLTYPE);
					// 接口获取oa流程ID
					workFlowVO.setWorkflowId(OaWorkFlowUtil
							.getOAFlowID(WorkFId));
					workFlowVO.setWorkflowName(oaVo.getBilltypename());
					WorkFlowBill bill = OaWorkFlowUtil
							.getWorkFlowBill(workFlowVO);
					bill.setDef4(hvo.getBillno());// 单据编号
					// 制单人身份证号
					UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
							UserVO.class, hvo.getBillmaker());
					bill.setDef5(userVO.getUser_code());
					// 调用OA工具类同步数据至OA
					OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
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

	private JSONArray getNewMainMap(ReceivableBillVO parentVO, String customer)
			throws BusinessException {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// ---------其他字段begin
		// 应收财务组织
		String stockName = (String) getHyPubBO().findColValue(
				"org_financeorg",
				"name",
				"nvl(dr,0) = 0 and pk_financeorg = '" + parentVO.getPk_org()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("zzmc", stockName));
		// 申请人
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		// 客户名称
		String custName = (String) getHyPubBO().findColValue("bd_customer",
				"name", "nvl(dr,0) = 0 and pk_customer = '" + customer + "'");
		list.add(OaWorkFlowUtil.listAddObj("custname", custName));
		// 部门
		String sqbmName = (String) getHyPubBO().findColValue(
				"org_dept_v",
				"name",
				"nvl(dr,0) = 0 and pk_vid = '" + parentVO.getPk_deptid_v()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("bm", sqbmName));
		// 业务员
		String ywy = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc = '" + parentVO.getPk_psndoc()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("ywy", ywy));
		// 申请部门
		String sqbm = (String) getHyPubBO()
				.findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept = '"
								+ parentVO.getPk_deptid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("sqbm", sqbm));
		// 责任部门
		String zrbm = (String) getHyPubBO().findColValue(
				"org_dept",
				"name",
				"nvl(dr,0) = 0 and pk_dept = '" + parentVO.getPk_deptid_res()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("zrbm", zrbm));
		// 申请人
		list.add(OaWorkFlowUtil.listAddObj("sqr", userVO.getUser_name()));
		// 制单人用户编码
		list.add(OaWorkFlowUtil.listAddObj("usercode", userVO.getUser_code()));
		// 制单人名称
		list.add(OaWorkFlowUtil.listAddObj("username", userVO.getUser_name()));
		// 制单人身份证号
		String idCard = (String) getHyPubBO().findColValue("bd_psndoc", "id",
				"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
		if (null == idCard) {
			throw new BusinessException("制单人身份证号码未维护");
		}
		list.add(OaWorkFlowUtil.listAddObj("userid", idCard));
		// 代控部门
		if (parentVO.getSo_deptid() != null) {
			/* 代控部门主键 */
			list.add(OaWorkFlowUtil.listAddObj("dkbmzj",
					parentVO.getSo_deptid()));
			/* 代控部门编码 */
			String dkbmbm = (String) getHyPubBO().findColValue(
					"org_dept",
					"code",
					"nvl(dr,0) = 0 and pk_dept = '" + parentVO.getSo_deptid()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbmbm", dkbmbm));

			/* 代控部门 */
			String dkbm = (String) getHyPubBO().findColValue(
					"org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept = '" + parentVO.getSo_deptid()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbm", dkbm));
		}
		// 备注
		if (null != parentVO.getDef12()) {
			list.add(OaWorkFlowUtil.listAddObj("bz", parentVO.getDef12()));
		}
		if (parentVO.getScomment() != null) {
			list.add(OaWorkFlowUtil.listAddObj("scomment",
					parentVO.getScomment()));
		}
		// 合同现用名
		if (null != parentVO.getDef4()) {
			String htxym = (String) getHyPubBO().findColValue(
					"bd_supplier",
					"name",
					"nvl(dr,0) = 0 and pk_supplier = '" + parentVO.getDef4()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("htxym", htxym));
		}
		if (parentVO.getCashitem() != null) {
			// 现金流量项目
			String xjllxm = (String) getHyPubBO().findColValue(
					"bd_cashflow",
					"name",
					"nvl(dr,0) = 0 and pk_cashflow ='" + parentVO.getCashitem()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("xjllxm", xjllxm));
		}
		// 发票类型
		if (parentVO.getDef31() != null) {
			String fplx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getDef31()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("fplx", fplx));
		}
		// 合同类别
		if (parentVO.getDef80() != null) {
			list.add(OaWorkFlowUtil.listAddObj("htlb", parentVO.getDef80()));
		}
		Logger.error("local_money：" + parentVO.getLocal_money());
		// 金额
		if (parentVO.getLocal_money() != null) {
			list.add(OaWorkFlowUtil.listAddObj("local_money",
					parentVO.getLocal_money() + ""));
		}
		// 调整模板后新增字段
		// 本次申请发票类型
		if (parentVO.getDef81() != null) {
			String bcsqfplx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getDef81()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("bcsqfplx", bcsqfplx));
		}
		// 收款业务
		if (parentVO.getDef84() != null) {
			String skyw = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getDef84()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("skyw", skyw));
		}
		// 应收类型
		if (parentVO.getDef88() != null) {
			String yslx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getDef88()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("yslx", yslx));
		}
		// 是否预开
		if (null != parentVO.getDef94()) {
			String sfyk = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getDef94()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sfyk", sfyk));
		}
		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	private JSONArray getNewDtaileDataMap(AggregatedValueObject temp)
			throws BusinessException {
		// 获取详细信息
		ReceivableBillItemVO[] bvos = (ReceivableBillItemVO[]) temp
				.getChildrenVO();
		Map<String, Object> bodyMap = getBody(bvos);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
		return dtlistString;
	}

	private Map<String, Object> getBody(ReceivableBillItemVO[] bvo)
			throws BusinessException {
		Map<String, Object> dtMap = new HashMap<String, Object>();
		dtMap.put("tableDBName", TableName + "_dt1");
		List<Map<String, Object>> workflowRequestTableRecords = new ArrayList<Map<String, Object>>();
		for (ReceivableBillItemVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			if (null != temp.getProject()) {
				// 项目
				ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
						.queryByPrimaryKey(ProjectHeadVO.class,
								temp.getProject());
				// 项目编码
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"project_code", projectVO.getProject_code()));
				// 项目名称
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"project_name", projectVO.getProject_name()));
			}
			if (null != temp.getMaterial()) {
				MaterialVO materialVO = (MaterialVO) getHyPubBO()
						.queryByPrimaryKey(MaterialVO.class, temp.getMaterial());
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
				String str4 = "";
				if (null != materialVO.getMaterialtype()) {
					str4 = materialVO.getMaterialtype();
				}
				// 单位
				if (null != materialVO.getPk_measdoc()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"dw",
							VOUtils.getDocName(MeasdocVO.class,
									materialVO.getPk_measdoc())));
				}
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"materialtype", str4));
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
			// 税率
			if (temp.getDef17() != null) {
				String sl = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef17()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"def17", sl));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sl",
						sl));
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
			// 申请部门
			if (temp.getPk_deptid() != null) {
				String fysqbm = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept = '" + temp.getPk_deptid()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"sqbm", fysqbm));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fsbm", fysqbm));
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
			}
			// 代控部门
			if (temp.getSo_deptid() != null) {
				/* 代控部门主键 */
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dkbmzj", temp.getSo_deptid()));
				/* 代控部门编码 */
				String dkbmbm = (String) getHyPubBO().findColValue(
						"org_dept",
						"code",
						"nvl(dr,0) = 0 and pk_dept = '" + temp.getSo_deptid()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dkbmbm", dkbmbm));
				/* 代控部门 */
				String dkbm = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept = '" + temp.getSo_deptid()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dkbm", dkbm));
			}
			// 发票类型
			if (temp.getDef95() != null) {
				String fplx = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc ='" + temp.getDef95()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fplx", fplx));
			}
			//运输工具种类
			if(temp.getDef110()!= null){
				String ysgjzl = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc ='" + temp.getDef110()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"ysgjzl", ysgjzl));
			}
			//不动产地址(省)
			if(temp.getDef114() != null){
				String bdcdzsheng = (String) getHyPubBO().findColValue(
						"bd_areacl",
						"name",
						"nvl(dr,0) = 0 and pk_areacl ='" + temp.getDef114()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bdcdzsheng", bdcdzsheng));
			}
			//不动产地址(市)
			if(temp.getDef115()!= null){
				String bdcdzshi = (String) getHyPubBO().findColValue(
						"bd_addressdoc",
						"name",
						"nvl(dr,0) = 0 and pk_addressdoc ='" + temp.getDef115()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bdcdzshi", bdcdzshi));
			}
			//面积单位
			if(temp.getDef118()!=null){
				String mjdw = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc ='" + temp.getDef118()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"mjdw", mjdw));
			}
			//农产品收购证件类型
			if(temp.getDef132()!=null){
				String ncpsgzjlx = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc ='" + temp.getDef132()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"ncpsgzjlx", ncpsgzjlx));
			}
			//贸易方式
			if(temp.getDef133()!=null){
				String myfs = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc ='" + temp.getDef133()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"myfs", myfs));
			}
			//币种
			if(temp.getPk_currtype()!=null){
				String bz = (String) getHyPubBO().findColValue(
						"bd_currtype",
						"name",
						"nvl(dr,0) = 0 and pk_currtype ='" + temp.getPk_currtype()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bz", bz));
			}
			//结算方式
			if(temp.getPk_balatype()!=null){
				String jsfs = (String) getHyPubBO().findColValue(
						"bd_balatype",
						"name",
						"nvl(dr,0) = 0 and pk_balatype ='" + temp.getPk_balatype()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"jsfs", jsfs));
			}


			// 其他字段------end
			Map<String, Object> workflowRequestTableFieldsMap = new HashMap<String, Object>();
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
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
}
