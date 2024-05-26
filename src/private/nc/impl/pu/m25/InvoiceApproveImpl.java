package nc.impl.pu.m25;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.pub.compiler.AbstractCompiler2;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pu.m25.action.InvoiceApproveAction;
import nc.impl.pu.m25.action.InvoiceSendApproveAction;
import nc.impl.pu.m25.action.InvoiceUnApproveAction;
import nc.impl.pu.m25.action.InvoiceUnSendApproveAction;
import nc.itf.pu.m25.IInvoiceApprove;
import nc.itf.pu.reference.it.ITServices;
import nc.vo.bd.material.MaterialVO;
import nc.vo.bd.supplier.SupplierVO;
import nc.vo.org.OrgVO;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pu.m25.entity.InvoiceHeaderVO;
import nc.vo.pu.m25.entity.InvoiceItemVO;
import nc.vo.pu.m25.entity.InvoiceVO;
import nc.vo.pu.m25.env.InvoiceUIToBSEnv;
import nc.vo.pu.m25.pub.InvoiceVOUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

//采购发票
public class InvoiceApproveImpl implements IInvoiceApprove {
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表明

	public InvoiceApproveImpl() {
	}

	public InvoiceVO[] approve(InvoiceVO[] vos, AbstractCompiler2 script,
			InvoiceUIToBSEnv[] envs) throws BusinessException {
		try {
			if (InvoiceVOUtil.isPuInvoice(vos)) {
				return new InvoiceApproveAction().approve(vos, script, envs);
			}
			return ITServices.approve(vos, script, envs);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public InvoiceVO[] sendapprove(InvoiceVO[] vos, AbstractCompiler2 script)
			throws BusinessException {
		try {
			InvoiceVO[] voiceVOs = new InvoiceSendApproveAction()
					.sendapprove(vos);
			senOaData(vos);
			return voiceVOs;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
			return null;
		}
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

	// TODO 单据同步OA功能
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

	private void senOaData(InvoiceVO[] billVOs) throws BusinessException {
		for (InvoiceVO temp : billVOs) {
			// 采购发票维护
			InvoiceHeaderVO hVO = temp.getParentVO();
			// OrgVO orgVO = (OrgVO) new
			// HYPubBO().queryByPrimaryKey(OrgVO.class,
			// hVO.getPk_org());
			if ("4".equals(getDef2(hVO.getPk_org()))) {
				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hVO.getPk_org(),
						"25");
				if (oaVo != null && oaVo.getIsdr() == 0) {
					WorkFId = oaVo.getFlowid();
					TableName = oaVo.getTablename();
					// 获取主表数据
					JSONArray headData = getMainMap(temp.getParentVO());
					// 获取子表数据
					JSONArray bodyData = getDtaileDataMap(temp);
					// 构造workflow信息
					OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
					workFlowVO.setPrimaryKey(temp.getPrimaryKey());
					workFlowVO.setPkGroup(hVO.getPk_group());
					workFlowVO.setPkOrg(hVO.getPk_org());
					workFlowVO.setBillMaker(hVO.getBillmaker());
					workFlowVO.setCreator(hVO.getCreator());
					workFlowVO.setBillCode("25");
					// 测试临时写死，后期改为接口获取
					workFlowVO.setWorkflowId(OaWorkFlowUtil
							.getOAFlowID(WorkFId));
					workFlowVO.setWorkflowName("采购发票维护");
					WorkFlowBill bill = OaWorkFlowUtil
							.getWorkFlowBill(workFlowVO);
					// WorkFlowBill的自定义项3 判断是否港华单据
					bill.setDef3("ZT");
					// 制单人身份证号
					UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
							UserVO.class, temp.getParentVO().getBillmaker());
					String idCard = (String) getHyPubBO().findColValue(
							"bd_psndoc",
							"id",
							"nvl(dr,0) = 0 and pk_psndoc='"
									+ userVO.getPk_psndoc() + "'");
					bill.setDef5(idCard);
					// 调用OA工具类同步数据至OA
					OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
				}
			}
		}
	}

	private JSONArray getMainMap(InvoiceHeaderVO parentVO)
			throws BusinessException {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// ---------其他字段begin
		// 财务组织
		String orgName = (String) getHyPubBO().findColValue(
				"org_financeorg",
				"name",
				"nvl(dr,0) = 0 and pk_financeorg  = '" + parentVO.getPk_org()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("zzmc", orgName));
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		// 发票类型名称
		String billTypeName = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and pk_billtypeid  = '"
						+ parentVO.getCtrantypeid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("typename", billTypeName));
		// 采购员
		String mname = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc  = '" + parentVO.getPk_bizpsn()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("cgy", mname));
		// 采购部门
		String dname = (String) getHyPubBO()
				.findColValue(
						"org_dept_v",
						"name",
						"nvl(dr,0) = 0 and pk_vid  = '"
								+ parentVO.getPk_dept_v() + "'");
		list.add(OaWorkFlowUtil.listAddObj("cgbm", dname));
		// 供应商名称
		SupplierVO supplierVO = (SupplierVO) getHyPubBO().queryByPrimaryKey(
				SupplierVO.class, parentVO.getPk_supplier());
		String name = "";
		if (null != supplierVO.getPk_supplier()) {
			name = supplierVO.getName();
		}
		list.add(OaWorkFlowUtil.listAddObj("pk_supplier_name", name));
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

		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	@SuppressWarnings({ "rawtypes" })
	private JSONArray getDtaileDataMap(InvoiceVO temp) throws BusinessException {
		Map bodyMap = getBody(temp.getChildrenVO());
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
		return dtlistString;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map getBody(InvoiceItemVO[] invoiceItemVOs)
			throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (InvoiceItemVO temp : invoiceItemVOs) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			// 项目
			if (temp.getCprojectid() != null) {
				ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
						.queryByPrimaryKey(ProjectHeadVO.class,
								temp.getCprojectid());
				if (null != projectVO) {
					// 项目编码
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"bproject_code", projectVO.getProject_code()));
					// 项目名称
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"bproject_name", projectVO.getProject_name()));
				}
			}
			if (temp.getPk_material() != null) {
				MaterialVO materialVO = (MaterialVO) getHyPubBO()
						.queryByPrimaryKey(MaterialVO.class,
								temp.getPk_material());
				if (null != materialVO) {
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
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"materialtype", str4));
				}
			}
			// 主单位
			String zdwstr = "";
			if (null != temp.getCunitid()) {
				zdwstr = (String) getHyPubBO().findColValue(
						"bd_measdoc",
						"name",
						"nvl(dr,0) = 0 and pk_measdoc  = '" + temp.getCunitid()
								+ "'");
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("zdw",
					zdwstr));
			// 单位
			String dwstr = "";
			if (null != temp.getCastunitid()) {
				dwstr = (String) getHyPubBO().findColValue(
						"bd_measdoc",
						"name",
						"nvl(dr,0) = 0 and pk_measdoc  = '"
								+ temp.getCastunitid() + "'");
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("dw",
					dwstr));
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

	public InvoiceVO[] unapprove(InvoiceVO[] vos, AbstractCompiler2 script)
			throws BusinessException {
		try {
			if (InvoiceVOUtil.isPuInvoice(vos)) {
				return new InvoiceUnApproveAction().unapprove(vos, script);
			}
			return ITServices.unapprove(vos, script);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public InvoiceVO[] unSendapprove(InvoiceVO[] vos, AbstractCompiler2 script)
			throws BusinessException {
		try {
			InvoiceVO[] voiceVOs = new InvoiceUnSendApproveAction()
					.unSendApprove(vos, script);
			unOaCommit(vos);
			return voiceVOs;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	private void unOaCommit(InvoiceVO[] aggVO) throws BusinessException {
		for (InvoiceVO temp : aggVO) {
			if ((temp.getParentVO().getVtrantypecode()).contains("25")) {
				OaWorkFlowUtil.backOaWorkFlow(temp.getPrimaryKey());
			}
		}
	}
}
