package nc.impl.erm.matterapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.erm.matterapp.ErmMatterAppBO;
import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
import nc.data.param.CommonParam;
import nc.itf.arap.forthird.IArapForDGSWService;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

// 申请单
@SuppressWarnings({ "unchecked", "rawtypes", "unused", "restriction" })
public class ErmMatterAppBillApproveImpl implements
		nc.pubitf.erm.matterapp.IErmMatterAppBillApprove {
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表名
	private static String RL_JBCF_001 = "261X-Cxx-02";
	// private static String RL_WORKFLOW_ID_001 = "102";

	private static String RL_CLF_002 = "261X-Cxx-01";

	// private static String RL_WORKFLOW_ID_002 = "101";

	public ErmMatterAppBillApproveImpl() {
	}

	public AggMatterAppVO[] commitVOs(AggMatterAppVO[] vos)
			throws BusinessException {
		AggMatterAppVO[] aggvos = new ErmMatterAppBO().commitVOs(vos);
		senOa(aggvos);
		return aggvos;
	}

	private void senOa(AggMatterAppVO[] vos) throws BusinessException {
		for (AggMatterAppVO temp : vos) {
			String type = temp.getParentVO().getPk_tradetype();// 交易类型
			String type1 = temp.getParentVO().getPk_billtype();// 单据类型
			MatterAppVO hvo = (MatterAppVO) temp.getParent();
			if ("4".equals(getDef2(hvo.getPk_org()))) {
				// Map<String, String> billTypeMap = billTypeMap();
				/*
				 * if (billTypeMap.containsKey(type)) { }
				 */
				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hvo.getPk_org(),
						type);
				// ----------------------
				if (oaVo != null && oaVo.getIsdr() == 0) {
					WorkFId = oaVo.getFlowid();
					TableName = oaVo.getTablename();
					// 获取主表数据
					JSONArray headData = getNewMainMap(hvo);
					// 获取子表数据
					JSONArray bodyData = null;
					/*if (RL_JBCF_001.equals(type)) {
						bodyData = getDtaileDataMap(temp);
					}else{
						bodyData = getNewDtaileDataMap(temp);
					}*/
					bodyData = getNewDtaileDataMap(temp);
					// 构造workflow信息
					WorkFlowBill bill = getFlowBill(temp, hvo, type1,
							WorkFId, oaVo.getBilltypename());
					// 调用OA工具类同步数据至OA
					OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
				}
			} else if ("1".equals(getDef2(hvo.getPk_org()))) {
				if (RL_JBCF_001.equals(type)) {
					// 加班餐费申请单
					JSONArray headData = getMainMap(temp.getParentVO());
					JSONArray bodyData = getDtaileDataMap(temp);

					OaWorkFlowUtil.sendOaData(headData, bodyData,
							getWorkFlowBill(temp));
				} else if (RL_CLF_002.equals(type)) {
					// 差旅费申请单
					JSONArray headData = getMainMap(temp.getParentVO());
					JSONArray bodyData = getDtaileDataMap1(temp);

					OaWorkFlowUtil.sendOaData(headData, bodyData,
							getWorkFlowBill(temp));
				}
			}
			// 调用东港税务系统同步单据状态
			IArapForDGSWService util = (IArapForDGSWService) NCLocator.getInstance().lookup(
					IArapForDGSWService.class);
			// 单据号，组织主键，事件类型，单据类型
			JSONObject res = util.sendBillByNCBill(hvo.getPrimaryKey(),
					hvo.getPk_org(), CommonParam.COMMIT, hvo.getPk_billtype());
			if(!"Y".equals(res.getString("success"))){
				throw new BusinessException(res.getString("errinfo"));
			}
		}
	}

	private WorkFlowBill getFlowBill(AggMatterAppVO temp, MatterAppVO hvo,
			String billCode, String workFId, String flowName)
			throws BusinessException {
		OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
		workFlowVO.setPrimaryKey(temp.getPrimaryKey());
		workFlowVO.setPkGroup(hvo.getPk_group());
		workFlowVO.setPkOrg(hvo.getPk_org());
		workFlowVO.setBillMaker(hvo.getCreator());
		workFlowVO.setCreator(hvo.getCreator());
		workFlowVO.setBillCode(billCode);// 单据类型
		// 接口获取
		workFlowVO.setWorkflowId(OaWorkFlowUtil.getOAFlowID(workFId));
		workFlowVO.setWorkflowName(flowName);
		WorkFlowBill bill = OaWorkFlowUtil.getWorkFlowBill(workFlowVO);
		bill.setDef3("ZT");
		bill.setDef4(hvo.getBillno());// 单据编号
		// 制单人身份证号
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				hvo.getCreator());
		String idCard = (String) getHyPubBO().findColValue("bd_psndoc", "id",
				"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
		bill.setDef5(idCard);
		return bill;
	}

	private JSONArray getNewMainMap(MatterAppVO parentVO)
			throws BusinessException {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// ---------其他字段begin
		// vat_amount 金额
		list.add(OaWorkFlowUtil.listAddObj("vat_amount",
				parentVO.getOrg_amount() + ""));
		// 组织名称
		String shortname = (String) getHyPubBO().findColValue(
				"org_purchaseorg",
				"name",
				"nvl(dr,0) = 0 and pk_purchaseorg ='" + parentVO.getPk_org()
						+ "'");
		if (shortname == null) {
			shortname = (String) getHyPubBO().findColValue(
					"org_financeorg",
					"name",
					"nvl(dr,0) = 0 and pk_financeorg ='" + parentVO.getPk_org()
							+ "'");
		}
		if (shortname == null) {
			shortname = (String) getHyPubBO().findColValue("org_orgs", "name",
					"nvl(dr,0) = 0 and pk_org ='" + parentVO.getPk_org() + "'");
		}
		list.add(OaWorkFlowUtil.listAddObj("zzmc", shortname));
		list.add(OaWorkFlowUtil.listAddObj("bxdw", shortname));
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getCreator());
		// 交易类型
		String billTypeName = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and pk_billtypeid  = '"
						+ parentVO.getPk_tradetypeid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("typename", billTypeName));
		// 申请人
		String sqr = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc ='" + parentVO.getBillmaker()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("sqr", sqr));
		// 费用承担单位
		String fycddw = (String) getHyPubBO().findColValue("org_orgs", "name",
				"nvl(dr,0) = 0 and pk_org ='" + parentVO.getDefitem4() + "'");
		list.add(OaWorkFlowUtil.listAddObj("fycddw", fycddw));
		// 申请单位
		if (parentVO.getApply_org() != null) {
			String sqdw = (String) getHyPubBO().findColValue(
					"org_adminorg",
					"name",
					"nvl(dr,0) = 0 and pk_adminorg ='"
							+ parentVO.getApply_org() + "'");
			list.add(OaWorkFlowUtil.listAddObj("sqdw", sqdw));
		}
		// 申请部门
		if (parentVO.getApply_dept() != null) {
			String sqbm = (String) getHyPubBO().findColValue(
					"org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getApply_dept()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sqbm", sqbm));
		}
		// 预算项目
		if (parentVO.getDefitem14() != null) {
			String ysxm = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getDefitem14()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("ysxm", ysxm));
		}
		// 是否属于安全经费
		if (parentVO.getDefitem12() != null) {
			String sfsyaqjf = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and  pk_defdoc ='" + parentVO.getDefitem12()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sfsyaqjf", sfsyaqjf));
		}
		// 带队人员姓名
		if (parentVO.getDefitem1() != null) {
			String ddryxm = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and  pk_psndoc ='" + parentVO.getDefitem1()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("ddryxm", ddryxm));
		}
		// 带队人员职务
		if (parentVO.getDefitem2() != null) {
			String ddryzw = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getDefitem2()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("ddryzw", ddryzw));
		}
		// 是否需要董事长审批
		if (parentVO.getDefitem1() != null) {
			String sfxydszsp = (String) getHyPubBO().findColValue(
					"org_dept",
					"name",
					"nvl(dr,0) = 0 and  pk_dept ='" + parentVO.getDefitem1()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sfxydszsp", sfxydszsp));
		}
		// 固定资产项目
		if (parentVO.getDefitem15() != null) {
			String gdzcxm = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getDefitem15()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("gdzcxm", gdzcxm));
		}
		// 项目明细
		if (parentVO.getDefitem11() != null) {
			String xmmx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getDefitem11()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("xmmx", xmmx));
		}
		// 申请类型
		if (parentVO.getDefitem11() != null) {
			String sqlx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and  pk_defdoc ='" + parentVO.getDefitem11()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sqlx", sqlx));
		}
		// 代控部门编码
		if (parentVO.getAssume_dept() != null) {
			String dkbmbm = (String) getHyPubBO().findColValue(
					"org_dept_v",
					"code",
					"nvl(dr,0) = 0 and  pk_dept ='" + parentVO.getAssume_dept()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbmbm", dkbmbm));
		}
		// 代控部门名称
		if (parentVO.getAssume_dept() != null) {
			String dkbm = (String) getHyPubBO().findColValue(
					"org_dept_v",
					"name",
					"nvl(dr,0) = 0 and  pk_dept ='" + parentVO.getAssume_dept()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbm", dkbm));
		}
		// 代控部门主键
		if (parentVO.getAssume_dept() != null) {
			Map dkbmzj = OaWorkFlowUtil.listAdd("dkbmzj",
					parentVO.getAssume_dept());
			list.add(dkbmzj);
		}
		// 附件张数
		if (parentVO.getAttach_amount() != null) {
			list.add(OaWorkFlowUtil.listAddObj("fjzs",
					parentVO.getAttach_amount() + ""));
		}

		// 项目编号
		String project_code = (String) getHyPubBO().findColValue(
				"bd_project",
				"project_code",
				"nvl(dr,0) = 0 and pk_project ='" + parentVO.getDefitem2()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("project_code", project_code));
		// 项目名称
		String project_name = (String) getHyPubBO().findColValue(
				"bd_project",
				"project_name",
				"nvl(dr,0) = 0 and pk_project ='" + parentVO.getDefitem2()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("project_name", project_name));
		// 费用承担部门
		if (null != parentVO.getAssume_dept()) {
			String fycdbm = (String) getHyPubBO().findColValue(
					"org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getAssume_dept()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("fycdbm", fycdbm));
		}
		// 收支项目
		if (null != parentVO.getPk_iobsclass()) {
			String szxm = (String) getHyPubBO().findColValue(
					"bd_inoutbusiclass",
					"name",
					"nvl(dr,0) = 0 and pk_inoutbusiclass ='"
							+ parentVO.getPk_iobsclass() + "'");
			list.add(OaWorkFlowUtil.listAddObj("szxm", szxm));
		}
		// 预算内
		if (parentVO.getDefitem9() != null) {
			String ysn = "否";
			if ("Y".equals(UFBoolean.valueOf(parentVO.getDefitem9()).toString())) {
				ysn = "是";
			}
			list.add(OaWorkFlowUtil.listAddObj("ysn", ysn));
		}
		// 紧急
		if (parentVO.getIsexpedited() != null) {
			String jj = "否";
			if ("Y".equals(parentVO.getIsexpedited().toString())) {
				jj = "是";
			}
			list.add(OaWorkFlowUtil.listAddObj("jj", jj));
		}
		// ---固定字段
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
		// 能投
		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				parentVO.getPk_org());
		if (null != orgVO) {
			list.add(OaWorkFlowUtil.listAddObj("szgs", orgVO.getCode()));
		}
		if (null != userVO) {// 制单人主键
			// 身份证号
			Map zdrzj = new HashMap();
			zdrzj.put("fieldName", "zdrzj");
			String id = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"id",
					"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc()
							+ "'");
			if ("2".equals(getDef2(parentVO.getPk_org()))) {
				if (null == id) {
					throw new BusinessException("制单人身份证号码未维护");
				}
				zdrzj.put("fieldValue", id);
				list.add(zdrzj);
			} else {
				zdrzj.put("fieldValue", userVO.getUser_code());
				list.add(zdrzj);
			}
		}
		list.add(OaWorkFlowUtil.listAddObj("djh", parentVO.getBillno()));// 制单人
		if (null != parentVO.getBilldate()) {
			list.add(OaWorkFlowUtil.listAddObj("djrq", parentVO.getBilldate()
					.getYear()
					+ "-"
					+ parentVO.getBilldate().getStrMonth()
					+ "-" + parentVO.getBilldate().getStrDay()));// 单据日期
		}
		// list.add(OaWorkFlowUtil.listAddObj("sqdw",
		// parentVO.getApply_org()));//申请单位
		String vname = (String) getHyPubBO().findColValue(
				"org_financeorg",
				"name",
				"nvl(dr,0) = 0 and pk_financeorg  ='" + parentVO.getPk_org()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("vname", vname));
		// list.add(OaWorkFlowUtil.listAddObj("sqbm",
		// parentVO.getApply_dept()));//申请部门
		// list.add(OaWorkFlowUtil.listAddObj("sqr",
		// parentVO.getBillmaker()));//申请人
		if (null != parentVO.getDefitem1()) {
			String ry = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and   pk_psndoc   ='"
							+ parentVO.getDefitem1() + "'");
			list.add(OaWorkFlowUtil.listAddObj("ry", ry));// 人员
		}
		String defitem2 = "0";
		if (null != parentVO.getDefitem2()) {
			defitem2 = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and   pk_defdoc   ='"
							+ parentVO.getDefitem2() + "'");
		}
		list.add(OaWorkFlowUtil.listAddObj("defitem2", defitem2));
		if (null != parentVO.getDefitem3()) {
			list.add(OaWorkFlowUtil.listAddObj("ccry", parentVO.getDefitem3()));
		}
		if (null != parentVO.getDefitem5()) {
			list.add(OaWorkFlowUtil.listAddObj("ccsy", parentVO.getDefitem5()));
		}
		if (null != parentVO.getOrig_amount()) {
			list.add(OaWorkFlowUtil.listAddObj("hjje", parentVO
					.getOrig_amount().setScale(2, UFDouble.ROUND_HALF_UP)
					.toString()));// 合同金额
		}
		if (null != parentVO.getDefitem4()) {
			list.add(OaWorkFlowUtil.listAddObj("jedx", parentVO.getDefitem4()));// 金额大写
		}
		if (null != parentVO.getRest_amount()) {
			list.add(OaWorkFlowUtil.listAddObj("ye", parentVO.getRest_amount()
					.setScale(2, UFDouble.ROUND_HALF_UP).toString()));// 余额
		}
		if (null != parentVO.getPre_amount()) {
			list.add(OaWorkFlowUtil.listAddObj("yzs", parentVO.getPre_amount()
					.setScale(2, UFDouble.ROUND_HALF_UP).toString()));
		}

		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	private JSONArray getNewDtaileDataMap(AggMatterAppVO temp)
			throws BusinessException {
		// 获取详细信息
		Map bodyMap = getBody((MtAppDetailVO[]) temp.getChildrenVO());
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
		return dtlistString;
	}

	private Map getBody(MtAppDetailVO[] bvo)
			throws BusinessException {
		Map dtMap = new HashMap();
		// 接口获取
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (MtAppDetailVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			// 资产类别
			String zclb = (String) getHyPubBO()
					.findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoc ='"
									+ temp.getDefitem6() + "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("zclb",
					zclb));
			// 收支项目
			String szxm = (String) getHyPubBO().findColValue(
					"bd_inoutbusiclass",
					"name",
					"nvl(dr,0) = 0 and pk_inoutbusiclass ='"
							+ temp.getPk_iobsclass() + "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("szxm",
					szxm));
			// 费用发生部门
			if (temp.getAssume_dept() != null) {
				String fyfsbm = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept   ='"
								+ temp.getAssume_dept() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fyfsbm", fyfsbm));
			}
			// 费用承担单位
			if (temp.getAssume_org() != null) {
				String fycddw = (String) getHyPubBO().findColValue(
						"org_orgs",
						"name",
						"nvl(dr,0) = 0 and  pk_org     ='"
								+ temp.getAssume_org() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fycddw", fycddw));
			}
			// 费用承担部门
			if (temp.getAssume_dept() != null) {
				String fycdbm = (String) getHyPubBO().findColValue(
						"org_orgs",
						"name",
						"nvl(dr,0) = 0 and  pk_org     ='"
								+ temp.getAssume_org() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fycdbm", fycdbm));
			}
			// 代控部门编码
			if (temp.getAssume_dept() != null) {
				String dkbmbm = (String) getHyPubBO().findColValue(
						"org_dept_v",
						"code",
						"nvl(dr,0) = 0 and  pk_dept ='" + temp.getAssume_dept()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dkbmbm", dkbmbm));
			}
			// 代控部门名称
			if (temp.getAssume_dept() != null) {
				String dkbm = (String) getHyPubBO().findColValue(
						"org_dept_v",
						"name",
						"nvl(dr,0) = 0 and  pk_dept ='" + temp.getAssume_dept()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dkbm", dkbm));
			}
			// 代控部门主键
			if (temp.getAssume_dept() != null) {
				Map dkbmzj = OaWorkFlowUtil.listAdd("dkbmzj",
						temp.getAssume_dept());
				workflowRequestTableFields.add(dkbmzj);
			}

			// 出发地点
			if (temp.getDefitem7() != null) {
				String cfdd = (String) getHyPubBO().findColValue(
						"bd_addressdoc",
						"name",
						"nvl(dr,0) = 0 and pk_addressdoc    ='"
								+ temp.getDefitem7() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"cfdd", cfdd));
			}
			// 目的地点
			if (temp.getDefitem8() != null) {
				String mddd = (String) getHyPubBO().findColValue(
						"bd_addressdoc",
						"name",
						"nvl(dr,0) = 0 and pk_addressdoc    ='"
								+ temp.getDefitem8() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"mddd", mddd));
			}
			// 交通工具
			if (temp.getDefitem9() != null) {
				String jtgj = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc    ='"
								+ temp.getDefitem9() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"jtgj", jtgj));
			}
			// 申请部门
			if (temp.getApply_dept() != null) {
				String sqbm = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and  pk_dept ='" + temp.getApply_dept()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"sqbm", sqbm));
			}

			// 单位
			if (null != temp.getDefitem12()) {
				String dw = (String) getHyPubBO().findColValue(
						"bd_measdoc",
						"name",
						"nvl(dr,0) = 0 and pk_measdoc  = '"
								+ temp.getDefitem12() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("dw",
						dw));
			}
			// 能投
			if (null != temp.getAssume_org()) {
				String vname = (String) getHyPubBO().findColValue(
						"org_orgs",
						"name",
						"nvl(dr,0) = 0 and   pk_org  ='" + temp.getAssume_org()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"vname", vname));
			}
			if (null != temp.getAssume_dept()) {
				String dk = (String) getHyPubBO().findColValue(
						"bd_inoutbusiclass",
						"name",
						"nvl(dr,0) = 0 and   pk_inoutbusiclass  ='"
								+ temp.getPk_iobsclass() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("dk",
						dk));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dkbmzj", temp.getAssume_dept()));// 带控部门主键
				String bmbm = (String) getHyPubBO().findColValue(
						"org_dept",
						"code",
						"nvl(dr,0) = 0 and   pk_dept   ='"
								+ temp.getAssume_dept() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bmbm", bmbm));// 部门编码
				String bm = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and   pk_dept   ='"
								+ temp.getAssume_dept() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dkbm", bm));// 带控部门
			}
			if (null != temp.getDefitem20()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"jbsy", temp.getDefitem20()));
			}
			if (null != temp.getDefitem23()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"jbrymd", temp.getDefitem23()));
			}
			if (null != temp.getDefitem29()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"jbrs", temp.getDefitem29()));
			}
			if (null != temp.getDefitem21()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"jbkssj", temp.getDefitem21()));
			}
			if (null != temp.getDefitem22()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"jbjssj", temp.getDefitem22()));
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bz",
					temp.getDefitem30()));// 备注
			if (null != temp.getOrig_amount()) {
				workflowRequestTableFields.add(OaWorkFlowUtil
						.listAddObj(
								"je",
								temp.getOrig_amount()
										.setScale(2, UFDouble.ROUND_HALF_UP)
										.toString()));// 金额
			}
			if (null != temp.getDefitem3()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"ccrs", temp.getDefitem3()));
			}
			if (null != temp.getDefitem7()) {
				String defitem7 = (String) getHyPubBO().findColValue(
						"bd_addressdoc",
						"name",
						"nvl(dr,0) = 0 and    pk_addressdoc   ='"
								+ temp.getDefitem7() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"cfdd", defitem7));
			}
			if (null != temp.getDefitem8()) {
				String defitem8 = (String) getHyPubBO().findColValue(
						"bd_addressdoc",
						"name",
						"nvl(dr,0) = 0 and    pk_addressdoc   ='"
								+ temp.getDefitem8() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"mddd", defitem8));
			}
			if (null != temp.getDefitem4()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"cfrq", temp.getDefitem4()));
			}
			if (null != temp.getDefitem5()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fcrq", temp.getDefitem5()));
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("ts",
					temp.getDefitem6()));
			if (null != temp.getOrig_amount()) {
				workflowRequestTableFields.add(OaWorkFlowUtil
						.listAddObj(
								"hjje",
								temp.getOrig_amount()
										.setScale(2, UFDouble.ROUND_HALF_UP)
										.toString()));// 合计金额
			}
			if (null != temp.getPk_iobsclass()) {
				String yslb = (String) getHyPubBO().findColValue(
						"bd_inoutbusiclass",
						"name",
						"nvl(dr,0) = 0 and   pk_inoutbusiclass  ='"
								+ temp.getPk_iobsclass() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"yslb", yslb));
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

	private WorkFlowBill getWorkFlowBill(AggMatterAppVO temp)
			throws BusinessException {
		String where = " nvl(dr,0) = 0 and pk_bill = '" + temp.getPrimaryKey()
				+ "'";
		WorkFlowBill[] workFlowBills = (WorkFlowBill[]) getHyPubBO()
				.queryByCondition(WorkFlowBill.class, where);
		WorkFlowBill workFlowBill = new WorkFlowBill();
		String type = temp.getParentVO().getPk_tradetype();
		if (null != workFlowBills && workFlowBills.length > 1) {
			throw new BusinessException("查询到多条流程记录");
		}
		if (null != workFlowBills && workFlowBills.length == 1) {
			workFlowBill = workFlowBills[0];
			if (RL_JBCF_001.equals(type)) {
				workFlowBill = workFlowBills[0];
				workFlowBill.setWorkflowName("加班餐费申请单");
			} else if (RL_CLF_002.equals(type)) {
				workFlowBill = workFlowBills[0];
				workFlowBill.setWorkflowName("差旅费申请单");
			}
		} else {
			// PsndocVO userVO = (PsndocVO)
			// getHyPubBO().queryByPrimaryKey(PsndocVO.class,
			// temp.getParentVO().getBillmaker());
			UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
					UserVO.class, temp.getParentVO().getCreator());

			// workFlowBill.setWorkflowId("102");
			workFlowBill.setDef5(userVO.getUser_code());
			if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
				PsndocVO psndoc = (PsndocVO) getHyPubBO().queryByPrimaryKey(
						PsndocVO.class, userVO.getPk_psndoc());
				workFlowBill.setDef5(psndoc.getId());
				// workFlowBill.setWorkflowId("102");
			}
			workFlowBill.setPk_group(temp.getParentVO().getPk_group());
			workFlowBill.setPk_org(temp.getParentVO().getPk_org());
			workFlowBill.setPk_bill(temp.getPrimaryKey());
			workFlowBill.setUser_name(userVO.getUser_name());
			workFlowBill.setUser_code(userVO.getUser_code());
			workFlowBill.setBill_status("1");
			workFlowBill.setCreationtime(new UFDate().toStdString());
			workFlowBill.setCreator(temp.getParentVO().getCreator());
			workFlowBill.setBill_code("261X");
			if (RL_JBCF_001.equals(type)) {
				workFlowBill.setWorkflowName("加班餐费申请单");
				workFlowBill.setWorkflowId("102");
				if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
					workFlowBill.setWorkflowId("443");
				}
			} else if (RL_CLF_002.equals(type)) {
				workFlowBill.setWorkflowName("差旅费申请单");
				workFlowBill.setWorkflowId("101");
				if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
					workFlowBill.setWorkflowId("418");
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

	private JSONArray getMainMap(MatterAppVO parentVO) throws BusinessException {
		List list = new ArrayList();

		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				parentVO.getPk_org());

		if (null != orgVO) {

			Map szgs = OaWorkFlowUtil.listAdd("szgs", orgVO.getCode());
			list.add(szgs);
		}

		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getCreator());
		// PsndocVO userVO = (PsndocVO)
		// getHyPubBO().queryByPrimaryKey(PsndocVO.class,
		// parentVO.getBillmaker());

		if (null != userVO) {
			// 身份证号
			Map zdrzj = new HashMap();
			zdrzj.put("fieldName", "zdrzj");
			String id = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"id",
					"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc()
							+ "'");
			if ("2".equals(getDef2(parentVO.getPk_org()))) {
				if (null == id) {
					throw new BusinessException("制单人身份证号码未维护");
				}
				zdrzj.put("fieldValue", id);
				list.add(zdrzj);
			} else {
				zdrzj.put("fieldValue", userVO.getUser_code());
				list.add(zdrzj);
			}

			// Map zdrq =OaWorkFlowUtil.listAdd("zdrq",
			// parentVO.getBilldate().getYear() + "-"
			// + parentVO.getBilldate().getStrMonth() + "-"
			// + parentVO.getBilldate().getStrDay() );
			// list.add(zdrq);
		}

		Map djh = OaWorkFlowUtil.listAdd("djh", parentVO.getBillno());
		list.add(djh);

		Map djrq = OaWorkFlowUtil.listAdd("djrq", parentVO.getBilldate()
				.getYear()
				+ "-"
				+ parentVO.getBilldate().getStrMonth()
				+ "-"
				+ parentVO.getBilldate().getStrDay());
		list.add(djrq);

		Map sqdw = OaWorkFlowUtil.listAdd("sqdw", parentVO.getApply_org());
		list.add(sqdw);

		String vname = (String) getHyPubBO().findColValue(
				"org_financeorg",
				"name",
				"nvl(dr,0) = 0 and pk_financeorg  ='" + parentVO.getPk_org()
						+ "'");
		Map bxdw = OaWorkFlowUtil.listAdd("bxdw", vname);
		list.add(bxdw);

		Map sqbm = OaWorkFlowUtil.listAdd("sqbm", parentVO.getApply_dept());
		list.add(sqbm);

		Map sqr = OaWorkFlowUtil.listAdd("sqr", parentVO.getBillmaker());
		list.add(sqr);

		String ry = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and   pk_psndoc   ='" + parentVO.getDefitem1()
						+ "'");
		Map ddryxm = OaWorkFlowUtil.listAdd("ddryxm", ry);
		list.add(ddryxm);

		String defitem2 = "0";
		if (null != parentVO.getDefitem2()) {
			defitem2 = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and   pk_defdoc   ='"
							+ parentVO.getDefitem2() + "'");
		}
		Map ddryzw = OaWorkFlowUtil.listAdd("ddryzw", defitem2);
		list.add(ddryzw);

		Map ccry = OaWorkFlowUtil.listAdd("ccry", parentVO.getDefitem3());
		list.add(ccry);

		Map ccsy = OaWorkFlowUtil.listAdd("ccsy", parentVO.getDefitem5());
		list.add(ccsy);

		Map hjje = OaWorkFlowUtil.listAdd("hjje", parentVO.getOrig_amount()
				.setScale(2, UFDouble.ROUND_HALF_UP).toString());
		list.add(hjje);

		Map jedx = OaWorkFlowUtil.listAdd("jedx", parentVO.getDefitem4());
		list.add(jedx);

		Map ye = OaWorkFlowUtil.listAdd("ye", parentVO.getRest_amount()
				.setScale(2, UFDouble.ROUND_HALF_UP).toString());
		list.add(ye);

		Map yzs = OaWorkFlowUtil.listAdd("yzs", parentVO.getPre_amount()
				.setScale(2, UFDouble.ROUND_HALF_UP).toString());
		list.add(yzs);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	private JSONArray getDtaileDataMap(AggMatterAppVO temp)
			throws BusinessException {
		Map orderMap = getMtAppDetailVO((MtAppDetailVO[]) temp.getChildrenVO(),
				temp.getParentVO().getPk_org());
		JSONArray dtlistString = JSONArray.fromObject(orderMap);
		return dtlistString;
	}

	private Map getMtAppDetailVO(MtAppDetailVO[] aggVO, String zz)
			throws BusinessException {
		Map dtMap = new HashMap();
		if ("1".equals(getDef2(zz))) {
			dtMap.put("tableDBName", "formtable_main_106_dt2");
		}else{
			dtMap.put("tableDBName", TableName + "_dt2");
		}
		List workflowRequestTableRecords = new ArrayList();
		for (MtAppDetailVO temp : aggVO) {
			List workflowRequestTableFields = new ArrayList();
			if (!"1".equals(getDef2(zz))) {
				workflowRequestTableFields = OaWorkFlowUtil
						.transBean2Map(temp);
			}
			Map workflowRequestTableFieldsMap = new HashMap();

			String vname = (String) getHyPubBO().findColValue(
					"org_orgs",
					"name",
					"nvl(dr,0) = 0 and   pk_org  ='" + temp.getAssume_org()
							+ "'");
			Map fycddw = OaWorkFlowUtil.listAdd("fycddw", vname);
			workflowRequestTableFields.add(fycddw);

			String dk = (String) getHyPubBO().findColValue(
					"bd_inoutbusiclass",
					"name",
					"nvl(dr,0) = 0 and   pk_inoutbusiclass  ='"
							+ temp.getPk_iobsclass() + "'");
			Map yslb = OaWorkFlowUtil.listAdd("yslb", dk);
			workflowRequestTableFields.add(yslb);

			Map dkbmzj = OaWorkFlowUtil
					.listAdd("dkbmzj", temp.getAssume_dept());
			workflowRequestTableFields.add(dkbmzj);

			String bmbm = (String) getHyPubBO().findColValue(
					"org_dept",
					"code",
					"nvl(dr,0) = 0 and   pk_dept   ='" + temp.getAssume_dept()
							+ "'");
			Map dkbmbm = OaWorkFlowUtil.listAdd("dkbmbm", bmbm);
			workflowRequestTableFields.add(dkbmbm);

			String bm = (String) getHyPubBO().findColValue(
					"org_dept",
					"name",
					"nvl(dr,0) = 0 and   pk_dept   ='" + temp.getAssume_dept()
							+ "'");
			Map dkbm = OaWorkFlowUtil.listAdd("dkbm", bm);
			workflowRequestTableFields.add(dkbm);

			Map jbsy = OaWorkFlowUtil.listAdd("jbsy", temp.getDefitem20());
			workflowRequestTableFields.add(jbsy);

			Map jbrymd = OaWorkFlowUtil.listAdd("jbrymd", temp.getDefitem23());
			workflowRequestTableFields.add(jbrymd);

			Map jbrs = OaWorkFlowUtil.listAdd("jbrs", temp.getDefitem29());
			workflowRequestTableFields.add(jbrs);

			Map jbkssj = OaWorkFlowUtil.listAdd("jbkssj", temp.getDefitem21());
			workflowRequestTableFields.add(jbkssj);

			Map jbjssj = OaWorkFlowUtil.listAdd("jbjssj", temp.getDefitem22());
			workflowRequestTableFields.add(jbjssj);

			Map bz = OaWorkFlowUtil.listAdd("bz", temp.getDefitem30());
			workflowRequestTableFields.add(bz);

			Map je = OaWorkFlowUtil.listAdd("je", temp.getOrig_amount()
					.setScale(2, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(je);

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);

		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	private JSONArray getDtaileDataMap1(AggMatterAppVO temp)
			throws BusinessException {
		Map orderMap = getMtAppDetailVO1(
				(MtAppDetailVO[]) temp.getChildrenVO(), temp.getParentVO()
						.getPk_org());
		JSONArray dtlistString = JSONArray.fromObject(orderMap);
		return dtlistString;
	}

	private Map getMtAppDetailVO1(MtAppDetailVO[] childrenVO, String zz)
			throws BusinessException {
		Map dtMap = new HashMap();
		dtMap.put("tableDBName", "formtable_main_105_dt1");
		if ("2".equals(getDef2(zz))) {
			dtMap.put("tableDBName", "formtable_main_447_dt1");
		}
		List workflowRequestTableRecords = new ArrayList();
		for (MtAppDetailVO temp : childrenVO) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();

			String vname = (String) getHyPubBO().findColValue(
					"org_orgs",
					"name",
					"nvl(dr,0) = 0 and   pk_org  ='" + temp.getAssume_org()
							+ "'");
			Map fycddw = OaWorkFlowUtil.listAdd("fycddw", vname);
			workflowRequestTableFields.add(fycddw);

			String dk = (String) getHyPubBO().findColValue(
					"bd_inoutbusiclass",
					"name",
					"nvl(dr,0) = 0 and   pk_inoutbusiclass  ='"
							+ temp.getPk_iobsclass() + "'");
			Map yslb = OaWorkFlowUtil.listAdd("yslb", dk);
			workflowRequestTableFields.add(yslb);

			Map dkbmzj = OaWorkFlowUtil
					.listAdd("dkbmzj", temp.getAssume_dept());
			workflowRequestTableFields.add(dkbmzj);

			String bmbm = (String) getHyPubBO().findColValue(
					"org_dept",
					"code",
					"nvl(dr,0) = 0 and   pk_dept   ='" + temp.getAssume_dept()
							+ "'");
			Map dkbmbm = OaWorkFlowUtil.listAdd("dkbmbm", bmbm);
			workflowRequestTableFields.add(dkbmbm);

			String bm = (String) getHyPubBO().findColValue(
					"org_dept",
					"name",
					"nvl(dr,0) = 0 and   pk_dept   ='" + temp.getAssume_dept()
							+ "'");
			Map dkbm = OaWorkFlowUtil.listAdd("dkbm", bm);
			workflowRequestTableFields.add(dkbm);

			Map ccrs = OaWorkFlowUtil.listAdd("ccrs", temp.getDefitem3());
			workflowRequestTableFields.add(ccrs);

			String defitem7 = (String) getHyPubBO().findColValue(
					"bd_addressdoc",
					"name",
					"nvl(dr,0) = 0 and    pk_addressdoc   ='"
							+ temp.getDefitem7() + "'");
			Map cfdd = OaWorkFlowUtil.listAdd("cfdd", defitem7);
			workflowRequestTableFields.add(cfdd);

			String defitem8 = (String) getHyPubBO().findColValue(
					"bd_addressdoc",
					"name",
					"nvl(dr,0) = 0 and    pk_addressdoc   ='"
							+ temp.getDefitem8() + "'");
			Map mddd = OaWorkFlowUtil.listAdd("mddd", defitem8);
			workflowRequestTableFields.add(mddd);

			Map cfrq = OaWorkFlowUtil.listAdd("cfrq", temp.getDefitem4());
			workflowRequestTableFields.add(cfrq);

			Map fcrq = OaWorkFlowUtil.listAdd("fcrq", temp.getDefitem5());
			workflowRequestTableFields.add(fcrq);

			String defitem9 = "0";
			if (null != temp.getDefitem9()) {
				String strWhere = "pk_defdoc = '"
						+ temp.getDefitem9()
						+ "' and nvl(dr,0) = 0 and pk_defdoclist in (SELECT pk_defdoclist FROM bd_defdoclist WHERE code = 'JTGJ' and nvl(dr,0) = 0)";
				DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
						DefdocVO.class, strWhere);
				if (null != defdoc && defdoc.length == 1) {
					defitem9 = defdoc[0].getName();
				}
			}
			Map jtgj = OaWorkFlowUtil.listAdd("jtgj", defitem9);
			workflowRequestTableFields.add(jtgj);

			Map ts = OaWorkFlowUtil.listAdd("ts", temp.getDefitem6());
			workflowRequestTableFields.add(ts);

			Map hjje = OaWorkFlowUtil.listAdd("hjje", temp.getOrig_amount()
					.setScale(2, UFDouble.ROUND_HALF_UP).toString());
			workflowRequestTableFields.add(hjje);
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);

		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	public AggMatterAppVO[] recallVOs(AggMatterAppVO[] vos)
			throws BusinessException {
		AggMatterAppVO[] aggvo = new ErmMatterAppBO().recallVOs(vos);
		for (AggregatedValueObject temp : aggvo) {
			OaWorkFlowUtil.backOaWorkFlow(temp.getParentVO().getPrimaryKey());
			// 调用东港税务系统同步单据状态
			IArapForDGSWService util = (IArapForDGSWService) NCLocator.getInstance().lookup(
					IArapForDGSWService.class);
			MatterAppVO hvo = (MatterAppVO) temp.getParentVO();
			// 单据号，组织主键，事件类型，单据类型
			JSONObject res = util.sendBillByNCBill(hvo.getPrimaryKey(),
					hvo.getPk_org(), CommonParam.RECALL, hvo.getPk_billtype());
			if(!"Y".equals(res.getString("success"))){
				throw new BusinessException(res.getString("errinfo"));
			}
		}
		return aggvo;
	}

	public MessageVO[] approveVOs(AggMatterAppVO[] vos)
			throws BusinessException {
		return new ErmMatterAppBO().approveVOs(vos);
	}

	public MessageVO[] unApproveVOs(AggMatterAppVO[] vos)
			throws BusinessException {
		return new ErmMatterAppBO().unapproveVOs(vos);
	}

	public AggMatterAppVO updateVOBillStatus(AggMatterAppVO vo)
			throws BusinessException {
		return new ErmMatterAppBO().updateVOBillStatus(vo);
	}

	private Map<String, String> billTypeMap() {
		Map<String, String> typeMap = new HashMap<String, String>();

		String JH_EASQ = "261X-Cxx-RQEASQ";
		typeMap.put(JH_EASQ, "EA申请单");

		String JH_CEASQ = "261X-Cxx-RQCEASQ";
		typeMap.put(JH_CEASQ, "CEA申请单");

		String JH_QT = "261X-Cxx-RQQT";
		typeMap.put(JH_QT, "其它申请单");

		String JH_GHDF = "261X-Cxx-GHDF";
		typeMap.put(JH_GHDF, "党组织工作经费事前申请");

		String JH_GHXC = "261X-Cxx-GHXC";
		typeMap.put(JH_GHXC, "宣传制作费事前申请");

		String JH_GHPX = "261X-Cxx-GHPX";
		typeMap.put(JH_GHPX, "培训事前申请");

		String JH_GHCEA_C = "261X-Cxx-GHCEA-C";
		typeMap.put(JH_GHCEA_C, "CEA-C申请单");

		String JH_GHQTFY = "261X-Cxx-GHQTFY";
		typeMap.put(JH_GHQTFY, "其他费用事前申请");

		String JH_GHSCSBWX = "261X-Cxx-GHSCSBWX";
		typeMap.put(JH_GHSCSBWX, "生产设备维修费事前申请");

		String JH_GHDLJH = "261X-Cxx-GHDLJH";
		typeMap.put(JH_GHDLJH, "道路监护费事前申请");

		String JH_GHKTHD = "261X-Cxx-GHKTHD";
		typeMap.put(JH_GHKTHD, "康体活动费事前申请 ");

		String JH_GHAQJF = "261X-Cxx-GHAQJF";
		typeMap.put(JH_GHAQJF, "安全经费事前申请");

		String JH_01 = "261X-Cxx-01";
		typeMap.put(JH_01, "差旅费申请单");

		String JH_02 = "261X-Cxx-02";
		typeMap.put(JH_02, "加班餐费申请单");
		return typeMap;
	}

}
