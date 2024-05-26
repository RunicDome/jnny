package nc.impl.erm.accruedexpense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nc.bs.erm.accruedexpense.ErmAccruedBillBO;
import nc.bs.trade.business.HYPubBO;
import nc.vo.bd.material.MaterialVO;
import nc.vo.bd.supplier.SupplierVO;
import nc.vo.erm.accruedexpense.AccruedDetailVO;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.ic.m4455.entity.SapplyBillBodyVO;
import nc.vo.ic.m4455.entity.SapplyBillHeadVO;
import nc.vo.ic.m4455.entity.SapplyBillVO;
import nc.vo.org.OrgVO;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;

// 费用预提
@SuppressWarnings("unused")
public class ErmAccruedBillCommitImpl implements
		nc.pubitf.erm.accruedexpense.IErmAccruedBillCommit {

	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表明

	public ErmAccruedBillCommitImpl() {
	}

	public AggAccruedBillVO[] commitVOs(AggAccruedBillVO[] vos)
			throws nc.vo.pub.BusinessException {
		vos = (new ErmAccruedBillBO()).commitVOs(vos);
		senOaData(vos);
		return vos;
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

	private void senOaData(AggAccruedBillVO[] billVOs) throws BusinessException {
		for (AggAccruedBillVO temp : billVOs) {
			AccruedVO hVO = (AccruedVO) temp.getParentVO();
			if ("4".equals(getDef2(hVO.getPk_org()))
					&& hVO.getPk_tradetype().contains("262")) {
				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hVO.getPk_org(),
						hVO.getPk_tradetype());
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
					workFlowVO.setBillMaker(hVO.getCreator());
					workFlowVO.setCreator(hVO.getCreator());
					workFlowVO.setBillCode("262X");
					// 测试临时写死，后期改为接口获取
					workFlowVO.setWorkflowId(OaWorkFlowUtil
							.getOAFlowID(WorkFId));
					workFlowVO.setWorkflowName("费用预提单");
					WorkFlowBill bill = OaWorkFlowUtil
							.getWorkFlowBill(workFlowVO);
					// WorkFlowBill的自定义项3 判断是否港华单据
					bill.setDef3("ZT");
					// 制单人身份证号
					UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
							UserVO.class, temp.getParentVO().getCreator());
					if (null != userVO) {
						String idCard = (String) getHyPubBO().findColValue(
								"bd_psndoc",
								"id",
								"nvl(dr,0) = 0 and pk_psndoc='"
										+ userVO.getPk_psndoc() + "'");
						bill.setDef5(idCard);
					}
					// 调用OA工具类同步数据至OA
					OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
				}
			}
		}
	}

	private JSONArray getMainMap(AccruedVO parentVO) throws BusinessException {
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// ---------其他字段begin
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getCreator());
		// 订单类型名称
		String billTypeName = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and pk_billtypeid  = '"
						+ parentVO.getPk_tradetypeid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("typename", billTypeName));
		// 经办人单位
		String jbrdw = (String) getHyPubBO().findColValue(
				"org_adminorg",
				"name",
				"nvl(dr,0) = 0 and pk_adminorg = '"
						+ parentVO.getOperator_org() + "'");
		list.add(OaWorkFlowUtil.listAddObj("jbrdw", jbrdw));
		list.add(OaWorkFlowUtil.listAddObj("sqrdw", jbrdw));
		// 经办人部门
		String jbrbm = (String) getHyPubBO().findColValue(
				"org_dept",
				"name",
				"nvl(dr,0) = 0 and pk_dept = '"
						+ parentVO.getOperator_dept() + "'");
		list.add(OaWorkFlowUtil.listAddObj("jbrbm", jbrbm));
		list.add(OaWorkFlowUtil.listAddObj("sqrbm", jbrdw));
		// 经办人
		String jbr = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc = '"
						+ parentVO.getOperator() + "'");
		list.add(OaWorkFlowUtil.listAddObj("jbr", jbr));
		list.add(OaWorkFlowUtil.listAddObj("sqr", jbr));
		// 紧急
		if(parentVO.getIsexpedited() != null){
			String jj =  "否";
			if("Y".equals(parentVO.getIsexpedited().toString())){
				jj = "是";
			}
			list.add(OaWorkFlowUtil.listAddObj("jj", jj));
		}
		if (null != userVO) {
			// 制单人用户编码
			list.add(OaWorkFlowUtil.listAddObj("usercode",
					userVO.getUser_code()));
			// 制单人名称
			list.add(OaWorkFlowUtil.listAddObj("username",
					userVO.getUser_name()));
			// 制单人身份证号
			String idCard = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"id",
					"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc()
							+ "'");
			if (null == idCard) {
				throw new BusinessException("制单人身份证号码未维护");
			}
			list.add(OaWorkFlowUtil.listAddObj("userid", idCard));
		}
		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	@SuppressWarnings({ "rawtypes" })
	private JSONArray getDtaileDataMap(AggAccruedBillVO temp)
			throws BusinessException {
		Map bodyMap = getBody(temp.getChildrenVO());
		JSONArray dtlistString = JSONArray.fromObject(bodyMap);
		return dtlistString;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map getBody(AccruedDetailVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (AccruedDetailVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			// 费用承担单位
			String fycddw = (String) getHyPubBO().findColValue("org_orgs", "name",
					"nvl(dr,0) = 0 and pk_org ='" + temp.getAssume_org() + "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("fycddw", fycddw));
			// 费用承担部门
			String fycdbm = (String) getHyPubBO().findColValue("org_dept", "name",
					"nvl(dr,0) = 0 and pk_dept ='" + temp.getAssume_dept() + "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("fycdbm", fycdbm));
			// 费用项目
			String fyxm = (String) getHyPubBO().findColValue("bd_inoutbusiclass", "name",
					"nvl(dr,0) = 0 and pk_inoutbusiclass ='" + temp.getPk_iobsclass() + "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
					"fyxm", fyxm));
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
					"szxm", fyxm));
			// 供应商
			if(temp.getPk_supplier() != null){
				SupplierVO supplierVO = (SupplierVO) getHyPubBO()
						.queryByPrimaryKey(SupplierVO.class, temp.getPk_supplier());
				String pk_supplier_name = supplierVO.getName();
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"pk_supplier_name", fyxm));
			}
			// 供应商
			if(temp.getPk_supplier() != null){
				SupplierVO supplierVO = (SupplierVO) getHyPubBO()
						.queryByPrimaryKey(SupplierVO.class, temp.getPk_supplier());
				String pk_supplier_name = supplierVO.getName();
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"pk_supplier_name", fyxm));
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

	public String getDef2(String pk_org) throws BusinessException {
		String def2 = "";
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		if (null != orgVO) {
			def2 = orgVO.getDef2();
		}
		return def2;
	}

	public AggAccruedBillVO[] recallVOs(AggAccruedBillVO[] vos)
			throws nc.vo.pub.BusinessException {
		for(AggAccruedBillVO temp : vos){
			OaWorkFlowUtil.backOaWorkFlow(temp.getPrimaryKey());
		}
		return new ErmAccruedBillBO().recallVOs(vos);
	}
}
