package nc.impl.ic.m4k;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.bs.trade.business.HYPubBO;
import nc.impl.ic.m4k.action.ApproveAction;
import nc.impl.ic.m4k.action.CommitAction;
import nc.impl.ic.m4k.action.UnApproveAction;
import nc.impl.ic.m4k.action.UnCommitAction;
import nc.itf.ic.m4k.IWhsTransApprove;
import nc.vo.bd.material.MaterialVO;
import nc.vo.ic.m4k.entity.WhsTransBillBodyVO;
import nc.vo.ic.m4k.entity.WhsTransBillHeaderVO;
import nc.vo.ic.m4k.entity.WhsTransBillVO;
import nc.vo.org.OrgVO;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;

public class WhsTransAppoveImpl implements IWhsTransApprove {
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表明

	public WhsTransBillVO[] approve(WhsTransBillVO[] vos,
			AbstractCompiler2 script) throws BusinessException {
		try {
			return (new ApproveAction()).approve(vos, script);
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
			return new WhsTransBillVO[0];
		}
	}

	public WhsTransBillVO[] commit(WhsTransBillVO[] vos,
			AbstractCompiler2 script) throws BusinessException {
		try {
			WhsTransBillVO[] billVOs = new CommitAction().commit(vos, script);
			senOaData(vos);
			return billVOs;
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
			return new WhsTransBillVO[0];
		}
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

	private void senOaData(WhsTransBillVO[] billVOs) throws BusinessException {
		for (WhsTransBillVO temp : billVOs) {
			WhsTransBillHeaderVO hVO = temp.getHead();
			if ("4".equals(getDef2(hVO.getPk_org()))
					&& hVO.getVtrantypecode().contains("4K")) {
				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hVO.getPk_org(),
						"4K");
				if (oaVo != null && oaVo.getIsdr() == 0) {
					WorkFId = oaVo.getFlowid();
					TableName = oaVo.getTablename();
					// 获取主表数据
					JSONArray headData = getMainMap(hVO);
					// 获取子表数据
					JSONArray bodyData = getDtaileDataMap(temp);
					// 构造workflow信息
					OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
					workFlowVO.setPrimaryKey(temp.getPrimaryKey());
					workFlowVO.setPkGroup(hVO.getPk_group());
					workFlowVO.setPkOrg(hVO.getPk_org());
					workFlowVO.setBillMaker(hVO.getBillmaker());
					workFlowVO.setCreator(hVO.getCreator());
					workFlowVO.setBillCode("4K");
					// 测试临时写死，后期改为接口获取
					workFlowVO.setWorkflowId(OaWorkFlowUtil
							.getOAFlowID(WorkFId));
					workFlowVO.setWorkflowName("转库");
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

	private JSONArray getMainMap(WhsTransBillHeaderVO parentVO)
			throws BusinessException {
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// ---------其他字段begin
		// 库存组织
		String stockName = (String) getHyPubBO().findColValue(
				"org_stockorg",
				"name",
				"nvl(dr,0) = 0 and pk_stockorg = '" + parentVO.getPk_org()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("zzmc", stockName));
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		// 转库类型
		String billTypeName = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and pk_billtypeid  = '"
						+ parentVO.getCtrantypeid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("typename", billTypeName));
		// 出库业务员
		String mname = (String) getHyPubBO().findColValue("bd_psndoc", "name",
				"nvl(dr,0) = 0 and pk_psndoc = '" + parentVO.getCbizid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("ckywy", mname));
		// 出库部门
		String dname = (String) getHyPubBO().findColValue("org_dept_v", "name",
				"nvl(dr,0) = 0 and pk_vid  = '" + parentVO.getCdptvid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("ckbm", dname));
		// 出库仓库
		String ckname = (String) getHyPubBO().findColValue(
				"bd_stordoc",
				"name",
				"nvl(dr,0) = 0 and pk_stordoc  = '"
						+ parentVO.getCwarehouseid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("ckck", ckname));
		// 入库业务员
		String rkname = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc = '" + parentVO.getCotherbizid()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("rkywy", rkname));
		// 入库部门
		String rkbm = (String) getHyPubBO().findColValue(
				"org_dept_v",
				"name",
				"nvl(dr,0) = 0 and pk_vid  = '" + parentVO.getCotherdptvid()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("rkbm", rkbm));
		// 入库仓库
		String rkck = (String) getHyPubBO().findColValue(
				"bd_stordoc",
				"name",
				"nvl(dr,0) = 0 and pk_stordoc  = '" + parentVO.getCotherwhid()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("rkck", rkck));
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
		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	@SuppressWarnings({ "rawtypes" })
	private JSONArray getDtaileDataMap(WhsTransBillVO temp)
			throws BusinessException {
		Map bodyMap = getBody(temp.getBodys());
		JSONArray dtlistString = JSONArray.fromObject(bodyMap);
		return dtlistString;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map getBody(WhsTransBillBodyVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (WhsTransBillBodyVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			MaterialVO materialVO = (MaterialVO) getHyPubBO()
					.queryByPrimaryKey(MaterialVO.class, temp.getCmaterialvid());
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
			// 项目
			if (null != temp.getCprojectid()) {
				ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
						.queryByPrimaryKey(ProjectHeadVO.class,
								temp.getCprojectid());
				// 项目编码
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bproject_code", projectVO.getProject_code()));
				// 项目名称
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bproject_name", projectVO.getProject_name()));
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

	public WhsTransBillVO[] unapprove(WhsTransBillVO[] vos,
			AbstractCompiler2 script) throws BusinessException {
		try {
			return (new UnApproveAction()).unApprove(vos, script);
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
			return new WhsTransBillVO[0];
		}
	}

	public WhsTransBillVO[] uncommit(WhsTransBillVO[] vos,
			AbstractCompiler2 script) throws BusinessException {
		try {
			WhsTransBillVO[] billVOs = new UnCommitAction().unCommit(vos,
					script);
			unOaCommit(billVOs);
			return billVOs;
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
			return new WhsTransBillVO[0];
		}
	}

	private void unOaCommit(WhsTransBillVO[] aggVO) throws BusinessException {
		for (WhsTransBillVO temp : aggVO) {
			// if ((temp.getHVO().getVtrantypecode()).contains("25")) {
			OaWorkFlowUtil.backOaWorkFlow(temp.getPrimaryKey());
			// }
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
}
