package nc.impl.fct.ar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.bs.trade.business.HYPubBO;
import nc.impl.fct.ar.action.ArApproveAction;
import nc.impl.fct.ar.action.ArFreezeAction;
import nc.impl.fct.ar.action.ArModifyAction;
import nc.impl.fct.ar.action.ArSendApproveAction;
import nc.impl.fct.ar.action.ArTerminateAction;
import nc.impl.fct.ar.action.ArUnApproveAction;
import nc.impl.fct.ar.action.ArUnFreezeAction;
import nc.impl.fct.ar.action.ArUnTerminateAction;
import nc.impl.fct.ar.action.ArUnValidateAction;
import nc.impl.fct.ar.action.ArUnsendApprove;
import nc.impl.fct.ar.action.ArValidateAction;
import nc.itf.fct.ar.IArApprove;
import nc.itf.portal.IGetIntoPortal;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.fct.ar.entity.AggCtArVO;
import nc.vo.fct.ar.entity.CtArBVO;
import nc.vo.fct.ar.entity.CtArTermVO;
import nc.vo.fct.ar.entity.CtArVO;
import nc.vo.org.FinanceOrgVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pflow.PfUserObject;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.apache.commons.lang.StringUtils;

// 收款合同
@SuppressWarnings({ "unchecked", "rawtypes", "restriction" })
public class ArApproveImpl implements IArApprove {
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表名

	public ArApproveImpl() {
	}

	public AggCtArVO[] approve(AggCtArVO[] vos, AbstractCompiler2 script)
			throws BusinessException {
		try {
			return new ArApproveAction().approve(vos, script);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtArVO[] freeze(AggCtArVO[] vos) throws BusinessException {
		try {
			return new ArFreezeAction().freeze(vos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtArVO[] modify(AggCtArVO[] vos, PfUserObject userConfirm)
			throws BusinessException {
		try {
			return new ArModifyAction().modify(vos, userConfirm);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtArVO[] sendapprove(AggCtArVO[] vos) throws BusinessException {
		try {
			AggCtArVO[] aggVO = new ArSendApproveAction().sendapprove(vos);
			senOaData(aggVO);
			// 工程集团及下属单位。收款款合同进入共享审批
			convertJKBXVO(aggVO);
			return aggVO;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 工程集团及下属单位。付款合同进入共享审批
	private void convertJKBXVO(AggCtArVO[] temps) throws BusinessException {
		for (AggCtArVO temp : temps) {
			// TODO Auto-generated method stub
			CtArVO hvo = temp.getParentVO();
			String pk_org = (String) getHyPubBO().findColValue(
					"v_org_gc",
					"pk_org",
					" pk_org  = '" + hvo.getPk_org()
							+ "' and pk_org <> '0001A110000000062PXO'");
			if (StringUtils.isNotEmpty(pk_org)) {
				IGetIntoPortal itf = NCLocator.getInstance().lookup(
						IGetIntoPortal.class);
				itf.changeIntoPortal(hvo.getCbilltypecode(), "264X-Cxx-gcskht",
						temp);
				// 清空驳回原因信息 付款合同：表头自定义项12,采购合同：表头自定义项52,销售合同：表头自定义项10
				hvo.setVdef12(null);
				getHyPubBO().update(hvo);
			}
		}
	}

	// TODO 单据同步OA功能 start create by zwh
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

	private void senOaData(AggCtArVO[] billVOs) throws BusinessException {
		for (AggCtArVO temp : billVOs) {
			String type = temp.getParentVO().getCbilltypecode();// 单据类型
			CtArVO hvo = (CtArVO) temp.getParent();
			String ifbl = hvo.getVdef15();
			if (ifbl == null || "1001A2100000000B68C3".equals(ifbl)) {
				if ("4".equals(getDef2(hvo.getPk_org()))) {
					// 中台
					OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(
							hvo.getPk_org(), type);
					// ----------------------
					if (oaVo != null && oaVo.getIsdr() == 0) {// 判断流程是否开启
						WorkFId = oaVo.getFlowid();
						TableName = oaVo.getTablename();
						// 获取主表数据
						JSONArray headData = getZTMainMap(hvo);
						// 获取子表数据
						JSONArray bodyData = getZTDtaileDataMap(temp);
						// 构造workflow信息
						OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
						workFlowVO.setPrimaryKey(temp.getPrimaryKey());
						workFlowVO.setPkGroup(hvo.getPk_group());
						workFlowVO.setPkOrg(hvo.getPk_org());
						workFlowVO.setBillMaker(hvo.getBillmaker());
						workFlowVO.setCreator(hvo.getCreator());
						workFlowVO.setBillCode(type);// 单据类型
						workFlowVO.setWorkflowId(OaWorkFlowUtil
								.getOAFlowID(WorkFId));
						workFlowVO.setWorkflowName(oaVo.getBilltypename());// 名称
						WorkFlowBill bill = OaWorkFlowUtil
								.getWorkFlowBill(workFlowVO);
						bill.setDef3("ZT");
						bill.setDef4(hvo.getVbillcode());// 单据编号
						// 制单人身份证号
						UserVO userVO = (UserVO) getHyPubBO()
								.queryByPrimaryKey(UserVO.class,
										hvo.getBillmaker());
						String idCard = (String) getHyPubBO().findColValue(
								"bd_psndoc",
								"id",
								"nvl(dr,0) = 0 and pk_psndoc='"
										+ userVO.getPk_psndoc() + "'");
						bill.setDef5(idCard);
						// 调用OA工具类同步数据至OA
						OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
					}
				} else {
					if ("2".equals(getDef2(temp.getParentVO().getPk_org()))
							|| "1".equals(getDef2(temp.getParentVO()
									.getPk_org()))) {
						OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(
								hvo.getPk_org(), type);
						// ----------------------
						if (oaVo != null && oaVo.getIsdr() == 0) {// 判断流程是否开启
							WorkFId = oaVo.getFlowid();
							TableName = oaVo.getTablename();
							// 获取主表数据
							JSONArray headData = getZTMainMap(hvo);
							// 获取子表数据
							JSONArray bodyData = getZTDtaileDataMap(temp);
							// 构造workflow信息
							OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
							workFlowVO.setPrimaryKey(temp.getPrimaryKey());
							workFlowVO.setPkGroup(hvo.getPk_group());
							workFlowVO.setPkOrg(hvo.getPk_org());
							workFlowVO.setBillMaker(hvo.getBillmaker());
							workFlowVO.setCreator(hvo.getCreator());
							workFlowVO.setBillCode(type);// 单据类型
							workFlowVO.setWorkflowId(OaWorkFlowUtil
									.getOAFlowID(WorkFId));
							workFlowVO.setWorkflowName(oaVo.getBilltypename());// 名称
							WorkFlowBill bill = OaWorkFlowUtil
									.getWorkFlowBill(workFlowVO);
							bill.setDef3("ZT");
							bill.setDef4(hvo.getVbillcode());// 单据编号
							// 制单人身份证号
							UserVO userVO = (UserVO) getHyPubBO()
									.queryByPrimaryKey(UserVO.class,
											hvo.getBillmaker());
							String idCard = (String) getHyPubBO().findColValue(
									"bd_psndoc",
									"id",
									"nvl(dr,0) = 0 and pk_psndoc='"
											+ userVO.getPk_psndoc() + "'");
							bill.setDef5(idCard);
							// 调用OA工具类同步数据至OA
							OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
						} else {
							JSONArray headData = getMainMap(temp.getParentVO());
							JSONArray bodyData = getDtaileDataMap(temp);

							OaWorkFlowUtil.sendOaData(headData, bodyData,
									getWorkFlowBill(temp));
						}
					}
				}
			}

		}
	}

	private JSONArray getZTDtaileDataMap(AggCtArVO temp)
			throws BusinessException {
		// 获取详细信息
		List dtlist = new ArrayList();
		// 明细表1 合同基本(fct_ar_b)
		CtArBVO[] bvos = temp.getCtArBVO();
		if (bvos != null && bvos.length > 0) {
			Map bodyMap = getBody(bvos);
			dtlist.add(bodyMap);
		}
		// 明细表2 合同条款 (fct_ar_term)
		CtArTermVO[] tkvos = temp.getCtArTermVO();
		if (tkvos != null && tkvos.length > 0) {
			Map tkbodyMap = getTKBody(tkvos);
			dtlist.add(tkbodyMap);
		}
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(dtlist, jsonConfig);
		return dtlistString;
	}

	// 中台条款明细
	private Map getTKBody(CtArTermVO[] bvo) throws BusinessException {

		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt2");
		List workflowRequestTableRecords = new ArrayList();
		for (CtArTermVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			// 原能投字段START---
			// 条款编码
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("tkbm",
					temp.getVtermcode()));

			// 条款名称
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("tkmc",
					temp.getVtermname()));

			// 条款类型
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("tklx",
					temp.getVtermtypename()));

			// 条款内容
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("tknr",
					temp.getVtermcontent()));

			// 其他信息
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("qtxx",
					temp.getVotherinfo()));

			// 备注
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bz",
					temp.getVmemo()));

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

	// 中台合同基本
	private Map getBody(CtArBVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (CtArBVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			// 其他字段------begin
			// 原能投字段START---
			// 序号
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("xh",
					temp.getCrowno()));

			// 项目主键
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("xmzj",
					temp.getProject()));

			// 项目名称
			if (temp.getProject() != null) {
				String project_code = (String) getHyPubBO().findColValue(
						"bd_project",
						"project_code",
						"nvl(dr,0) = 0 and pk_project ='" + temp.getProject()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"xmbm", project_code));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bproject_code", project_code));

				String project_name = (String) getHyPubBO().findColValue(
						"bd_project",
						"project_name",
						"nvl(dr,0) = 0 and pk_project ='" + temp.getProject()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"xmmc", project_name));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bproject_name", project_name));
			}

			// 金额
			if (null != temp.getNtaxmny()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"htje",
						temp.getNtaxmny().setScale(2, UFDouble.ROUND_HALF_UP)
								+ ""));
			}
			// 税率
			if (null != temp.getVbdef17()) {
				String sl = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc ='" + temp.getVbdef17()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sl",
						sl));
			}

			// 不含税金额
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bhsje",
					temp.getVbdef15()));

			// 税额
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("se",
					temp.getVbdef19()));

			// 累计收款金额
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("ljskje",
					temp.getNtotalgpmny() + ""));

			// 累计开票金额
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("ljkpje",
					temp.getNcopegpmny() + ""));

			// 备注
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bz",
					temp.getVmemo()));

			// 物料名称
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("wlmc",
					temp.getVbdef3()));

			// 物料规格
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("wlgg",
					temp.getVbdef4()));

			// 数量
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sl1",
					temp.getNnum().setScale(2, UFDouble.ROUND_HALF_UP) + ""));

			// 单价
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("dj", temp
					.getNgtaxprice().setScale(2, UFDouble.ROUND_HALF_UP) + ""));
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

	// 中台主表数据
	private JSONArray getZTMainMap(CtArVO parentVO) throws BusinessException {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// ---------其他字段begin
		FinanceOrgVO orgVO = (FinanceOrgVO) getHyPubBO().queryByPrimaryKey(
				FinanceOrgVO.class, parentVO.getPk_org());
		list.add(OaWorkFlowUtil.listAddObj("zzmc", orgVO.getName()));
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
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
		// 用印类型
		if (parentVO.getVdef5() != null) {
			String yylx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getVdef5()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("yylx", yylx));
		}
		// 是否使用电子签章
		if (parentVO.getVdef4() != null) {
			String sfsydzqz = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getVdef4()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sfsydzqz", sfsydzqz));
		}
		// 原能投字段START---
		if (null != orgVO) {
			list.add(OaWorkFlowUtil.listAddObj("szgs", orgVO.getCode()));
			// 所属组织名称
			list.add(OaWorkFlowUtil.listAddObj("sszzmc", orgVO.getName()));
		}
		// 单据主键
		list.add(OaWorkFlowUtil.listAddObj("djzj", parentVO.getPk_fct_ar()));
		if (null != userVO) {
			list.add(OaWorkFlowUtil.listAddObj("zdrmc", userVO.getUser_name()));// 制单人主键
			list.add(OaWorkFlowUtil.listAddObj("zdrzj", userVO.getUser_code()));// 制单人名称
		}
		Map djzj = OaWorkFlowUtil.listAddObj("djzj", parentVO.getPk_fct_ar());
		list.add(djzj);

		// 制单日期
		list.add(OaWorkFlowUtil.listAddObj("zdrq", parentVO.getDmakedate()
				.getYear()
				+ "-"
				+ parentVO.getDmakedate().getStrMonth()
				+ "-"
				+ parentVO.getDmakedate().getStrDay()));

		// 合同编码
		list.add(OaWorkFlowUtil.listAddObj("htbm", parentVO.getVbillcode()));
		// 合同名称
		list.add(OaWorkFlowUtil.listAddObj("htmc", parentVO.getCtname()));

		// 交易类型主键
		list.add(OaWorkFlowUtil.listAddObj("jylxpk", parentVO.getCtrantypeid()));
		// 交易类型
		String billtypename = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and pk_billtypeid ='"
						+ parentVO.getCtrantypeid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("jylxmc", billtypename));
		// 合同签订时间
		if (parentVO.getSubscribedate() != null) {
			list.add(OaWorkFlowUtil.listAddObj("htqdsj", parentVO
					.getSubscribedate().getYear()
					+ "-"
					+ parentVO.getSubscribedate().getStrMonth()
					+ "-"
					+ parentVO.getSubscribedate().getStrDay()));
		}
		// 生效日期
		if (parentVO.getValdate() != null) {
			list.add(OaWorkFlowUtil.listAddObj("sxrq", parentVO.getValdate()
					.getYear()
					+ "-"
					+ parentVO.getValdate().getStrMonth()
					+ "-" + parentVO.getValdate().getStrDay()));
		}
		// 终止日期
		if (parentVO.getInvallidate() != null) {
			list.add(OaWorkFlowUtil.listAddObj("zzrq", parentVO
					.getInvallidate().getYear()
					+ "-"
					+ parentVO.getInvallidate().getStrMonth()
					+ "-"
					+ parentVO.getInvallidate().getStrDay()));
		}
		// 代控部门
		if (parentVO.getVdef10() != null) {
			/* 代控部门主键 */
			list.add(OaWorkFlowUtil.listAddObj("dkbmzj", parentVO.getVdef10()));

			/* 代控部门编码 */
			Map dkbmbm = OaWorkFlowUtil.listAddObj(
					"dkbmbm",
					(String) getHyPubBO().findColValue(
							"org_dept",
							"code",
							"nvl(dr,0) = 0 and pk_dept ='"
									+ parentVO.getVdef10() + "'"));
			list.add(dkbmbm);

			/* 代控部门 */
			String bm = (String) getHyPubBO()
					.findColValue(
							"org_dept",
							"name",
							"nvl(dr,0) = 0 and pk_dept ='"
									+ parentVO.getVdef10() + "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbm", bm));
			list.add(OaWorkFlowUtil.listAddObj("ncdkbm", bm));
		}
		// 承办人员主键
		list.add(OaWorkFlowUtil.listAddObj("cbryzj", parentVO.getPersonnelid()));

		// 承办人员
		String mname = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and  pk_psndoc ='" + parentVO.getPersonnelid()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("cbrymc", mname));

		// 承办部门主键
		list.add(OaWorkFlowUtil.listAddObj("cbbmbbzj", parentVO.getDepid()));

		// 承办部门
		String dname = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getDepid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("cbbmbb", dname));

		// 金额
		list.add(OaWorkFlowUtil.listAddObj("htje", parentVO.getNtotalorigmny()
				.setScale(2, UFDouble.ROUND_HALF_UP) + ""));// 有值

		//
		list.add(OaWorkFlowUtil.listAddObj("bfyxzhzj",
				parentVO.getBankaccount()));

		if (parentVO.getBankaccount() != null) {
			String bankaccountno = (String) getHyPubBO().findColValue(
					"bd_bankaccsub",
					"accnum",
					"nvl(dr,0) = 0 and pk_bankaccsub ='"
							+ parentVO.getBankaccount() + "'");
			list.add(OaWorkFlowUtil.listAddObj("bfyxzh", bankaccountno + ""));
		}

		// 分管领导主键
		list.add(OaWorkFlowUtil.listAddObj("fgldzj", parentVO.getVdef1()));

		// 分管领导
		if (null != parentVO.getVdef1()) {
			String fgld = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getVdef1()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("fgld", fgld));
		}

		// 招标方式主键
		list.add(OaWorkFlowUtil.listAddObj("zbfszj", parentVO.getVdef3()));

		// 招标方式
		if (null != parentVO.getVdef2()) {
			String zbfs = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getVdef2()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("zbfs", zbfs));
		}

		// 单价合同
		if (parentVO.getVdef17() != null) {
			String djht = "";
			if ("Y".equals(parentVO.getVdef17())) {
				djht = "是";
			} else {
				djht = "否";
			}
			list.add(OaWorkFlowUtil.listAddObj("djht", djht));
		}

		// 金额大写
		list.add(OaWorkFlowUtil.listAddObj("jedx", parentVO.getVdef16()));

		// 合同变更主键
		list.add(OaWorkFlowUtil.listAddObj("htbgpk", parentVO.getVdef11()));

		// 合同变更
		if (null != parentVO.getVdef11()) {
			String htbg = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getVdef11()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("htbgmc", htbg));
		}

		// 客户主键
		list.add(OaWorkFlowUtil.listAddObj("khzj", parentVO.getPk_customer()));

		// 客户
		if (parentVO.getPk_customer() != null) {
			String kh = (String) getHyPubBO().findColValue(
					"bd_customer",
					"name",
					"nvl(dr,0) = 0 and pk_customer ='"
							+ parentVO.getPk_customer() + "'");
			list.add(OaWorkFlowUtil.listAddObj("khmc", kh));
		}

		// 备注
		list.add(OaWorkFlowUtil.listAddObj("bz", parentVO.getVdef20()));
		// 合同类别
		if (parentVO.getVdef18() != null) {
			String htlbbm = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getVdef18()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("htlbbm", htlbbm));
			String htlb = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getVdef18()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("htlb", htlb));
		}
		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	private JSONArray getDtaileDataMap(AggCtArVO temp) throws BusinessException {

		List dtlist = new ArrayList();
		int i = 1;
		if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
			i = 2;
		}
		Map ctArBMap = getCtAr(temp.getCtArBVO(), i);
		if (null != ctArBMap) {
			dtlist.add(ctArBMap);
		}
		Map ctArTermMap = getCtArTerm(temp.getCtArTermVO(), i);
		if (null != ctArTermMap) {
			dtlist.add(ctArTermMap);
		}
		JSONArray dtlistString = JSONArray.fromObject(dtlist);
		return dtlistString;
	}

	private Map getCtArTerm(CtArTermVO[] ctArTermVO, int i)
			throws BusinessException {
		if (null == ctArTermVO || ctArTermVO.length == 0) {
			return null;
		}
		Map dtMap = new HashMap();
		if (i == 1) {
			dtMap.put("tableDBName", "formtable_main_75_dt2");
		} else if (i == 2) {
			dtMap.put("tableDBName", "formtable_main_567_dt2");
		}
		List workflowRequestTableRecords = new ArrayList();
		for (CtArTermVO temp : ctArTermVO) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();

			Map tkbm = OaWorkFlowUtil.listAddObj("tkbm", temp.getVtermcode());
			workflowRequestTableFields.add(tkbm);

			Map tkmc = OaWorkFlowUtil.listAddObj("tkmc", temp.getVtermname());
			workflowRequestTableFields.add(tkmc);

			Map tklx = OaWorkFlowUtil.listAddObj("tklx",
					temp.getVtermtypename());
			workflowRequestTableFields.add(tklx);

			Map tknr = OaWorkFlowUtil
					.listAddObj("tknr", temp.getVtermcontent());
			workflowRequestTableFields.add(tknr);

			Map qtxx = OaWorkFlowUtil.listAddObj("qtxx", temp.getVotherinfo());
			workflowRequestTableFields.add(qtxx);

			Map bz = OaWorkFlowUtil.listAddObj("bz", temp.getVmemo());
			workflowRequestTableFields.add(bz);

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	private Map getCtAr(CtArBVO[] ctArBVO, int i) throws BusinessException {
		if (null == ctArBVO || ctArBVO.length == 0) {
			return null;
		}
		Map dtMap = new HashMap();
		if (i == 1) {
			dtMap.put("tableDBName", "formtable_main_75_dt1");
		} else if (i == 2) {
			dtMap.put("tableDBName", "formtable_main_567_dt1");
		}
		List workflowRequestTableRecords = new ArrayList();
		for (CtArBVO temp : ctArBVO) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();

			Map xh = OaWorkFlowUtil.listAddObj("xh", temp.getCrowno());
			workflowRequestTableFields.add(xh);

			Map xmzj = OaWorkFlowUtil.listAddObj("xmzj", temp.getProject());
			workflowRequestTableFields.add(xmzj);

			String project_name = (String) getHyPubBO().findColValue(
					"bd_project",
					"project_name",
					"nvl(dr,0) = 0 and   pk_project   ='" + temp.getProject()
							+ "'");
			Map xmmc = OaWorkFlowUtil.listAddObj("xmmc", project_name);
			workflowRequestTableFields.add(xmmc);

			String je = temp.getNtaxmny().setScale(2, UFDouble.ROUND_HALF_UP)
					+ "";
			Map htje = OaWorkFlowUtil.listAddObj("htje", je);
			workflowRequestTableFields.add(htje);

			if (null != temp.getVbdef17()) {
				String where = " pk_defdoc = '"
						+ temp.getVbdef17()
						+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'GX-02' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
				String def1 = (String) getHyPubBO().findColValue("bd_defdoc",
						"name", where);
				if (null != def1) {
					Map sl = OaWorkFlowUtil.listAddObj("sl", def1);
					workflowRequestTableFields.add(sl);
				}
			}

			Map bhsje = OaWorkFlowUtil.listAddObj("bhsje", temp.getVbdef15());
			workflowRequestTableFields.add(bhsje);

			Map se = OaWorkFlowUtil.listAddObj("se", temp.getVbdef19());
			workflowRequestTableFields.add(se);

			Map ljskje = OaWorkFlowUtil.listAddObj("ljskje",
					temp.getNtotalgpmny() + "");
			workflowRequestTableFields.add(ljskje);

			Map ljkpje = OaWorkFlowUtil.listAddObj("ljkpje",
					temp.getNcopegpmny() + "");
			workflowRequestTableFields.add(ljkpje);

			Map bz = OaWorkFlowUtil.listAddObj("bz", temp.getVmemo());
			workflowRequestTableFields.add(bz);

			Map wlmc = OaWorkFlowUtil.listAddObj("wlmc", temp.getVbdef3());
			workflowRequestTableFields.add(wlmc);

			Map wlgg = OaWorkFlowUtil.listAddObj("wlgg", temp.getVbdef4());
			workflowRequestTableFields.add(wlgg);

			Map sl1 = OaWorkFlowUtil.listAddObj("sl1",
					temp.getNnum().setScale(2, UFDouble.ROUND_HALF_UP) + "");
			workflowRequestTableFields.add(sl1);

			Map dj = OaWorkFlowUtil.listAddObj("dj", temp.getNgtaxprice()
					.setScale(2, UFDouble.ROUND_HALF_UP) + "");
			workflowRequestTableFields.add(dj);

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	private JSONArray getMainMap(CtArVO parentVO) throws BusinessException {
		List list = new ArrayList();

		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				parentVO.getPk_org());
		if (null != orgVO) {

			Map szgs = OaWorkFlowUtil.listAddObj("szgs", orgVO.getCode());
			list.add(szgs);
		}
		// 所属组织名称
		Map sszzmc = OaWorkFlowUtil.listAddObj("sszzmc", orgVO.getName());
		list.add(sszzmc);

		Map djzj = OaWorkFlowUtil.listAddObj("djzj", parentVO.getPk_fct_ar());
		list.add(djzj);

		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		if (null != userVO) {

			Map zdrmc = OaWorkFlowUtil.listAddObj("zdrmc",
					userVO.getUser_name());
			list.add(zdrmc);

			Map zdrzj = OaWorkFlowUtil.listAddObj("zdrzj",
					userVO.getUser_code());
			list.add(zdrzj);
		}

		Map zdrq = OaWorkFlowUtil.listAddObj("zdrq", parentVO.getDmakedate()
				.getYear()
				+ "-"
				+ parentVO.getDmakedate().getStrMonth()
				+ "-"
				+ parentVO.getDmakedate().getStrDay());
		list.add(zdrq);

		Map htbm = OaWorkFlowUtil.listAddObj("htbm", parentVO.getVbillcode());
		list.add(htbm);

		Map htmc = OaWorkFlowUtil.listAddObj("htmc", parentVO.getCtname());
		list.add(htmc);

		Map jylxpk = OaWorkFlowUtil.listAddObj("jylxpk",
				parentVO.getCtrantypeid());
		list.add(jylxpk);

		String billtypename = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and pk_billtypeid ='"
						+ parentVO.getCtrantypeid() + "'");
		Map jylxmc = OaWorkFlowUtil.listAddObj("jylxmc", billtypename);
		list.add(jylxmc);

		Map htqdsj = OaWorkFlowUtil.listAddObj("htqdsj", parentVO
				.getSubscribedate().getYear()
				+ "-"
				+ parentVO.getSubscribedate().getStrMonth()
				+ "-"
				+ parentVO.getSubscribedate().getStrDay());
		list.add(htqdsj);

		Map sxrq = OaWorkFlowUtil.listAddObj("sxrq", parentVO.getValdate()
				.getYear()
				+ "-"
				+ parentVO.getValdate().getStrMonth()
				+ "-"
				+ parentVO.getValdate().getStrDay());
		list.add(sxrq);

		Map zzrq = OaWorkFlowUtil.listAddObj("zzrq", parentVO.getInvallidate()
				.getYear()
				+ "-"
				+ parentVO.getInvallidate().getStrMonth()
				+ "-" + parentVO.getInvallidate().getStrDay());
		list.add(zzrq);

		Map cbryzj = OaWorkFlowUtil.listAddObj("cbryzj",
				parentVO.getPersonnelid());
		list.add(cbryzj);

		String mname = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and  pk_psndoc ='" + parentVO.getPersonnelid()
						+ "'");
		Map cbrymc = OaWorkFlowUtil.listAddObj("cbrymc", mname);
		list.add(cbrymc);

		Map cbbmbbzj = OaWorkFlowUtil.listAddObj("cbbmbbzj",
				parentVO.getDepid());
		list.add(cbbmbbzj);

		String dname = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and pk_dept ='" + parentVO.getDepid() + "'");
		Map cbbmbb = OaWorkFlowUtil.listAddObj("cbbmbb", dname);
		list.add(cbbmbb);

		String je = parentVO.getNtotalorigmny().setScale(2,
				UFDouble.ROUND_HALF_UP)
				+ "";
		Map htje = OaWorkFlowUtil.listAddObj("htje", je);
		list.add(htje);// 有值

		Map bfyxzhzj = OaWorkFlowUtil.listAddObj("bfyxzhzj",
				parentVO.getBankaccount());
		list.add(bfyxzhzj);

		String bankaccountno = (String) getHyPubBO().findColValue(
				"bd_bankaccsub",
				"accnum",
				"nvl(dr,0) = 0 and  pk_bankaccsub ='"
						+ parentVO.getBankaccount() + "'");
		Map bfyxzh = OaWorkFlowUtil.listAddObj("bfyxzh", bankaccountno + "");
		list.add(bfyxzh);

		Map fgldzj = OaWorkFlowUtil.listAddObj("fgldzj", parentVO.getVdef1());
		list.add(fgldzj);

		if (null != parentVO.getVdef1()) {
			String where = " pk_defdoc = '"
					+ parentVO.getVdef1()
					+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'fgld' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
			String def1 = "0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					where)) {
				def1 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where);
			}

			Map fgld = OaWorkFlowUtil.listAddObj("fgld", def1);
			list.add(fgld);
		}

		Map zbfszj = OaWorkFlowUtil.listAddObj("zbfszj", parentVO.getVdef3());
		list.add(zbfszj);
		// 代控部门
		if (parentVO.getVdef10() != null) {
			/* 代控部门主键 */
			list.add(OaWorkFlowUtil.listAddObj("dkbmzj", parentVO.getVdef10()));

			/* 代控部门编码 */
			Map dkbmbm = OaWorkFlowUtil.listAddObj(
					"dkbmbm",
					(String) getHyPubBO().findColValue(
							"org_dept",
							"code",
							"nvl(dr,0) = 0 and pk_dept ='"
									+ parentVO.getVdef10() + "'"));
			list.add(dkbmbm);

			/* 代控部门 */
			String bm = (String) getHyPubBO()
					.findColValue(
							"org_dept",
							"name",
							"nvl(dr,0) = 0 and pk_dept ='"
									+ parentVO.getVdef10() + "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbm", bm));
			list.add(OaWorkFlowUtil.listAddObj("ncdkbm", bm));
		}
		if (null != parentVO.getVdef2()) {
			String where = " pk_defdoc = '"
					+ parentVO.getVdef2()
					+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'cgfs' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
			String def2 = "0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					where)) {
				def2 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where);
			}

			Map zbfs = OaWorkFlowUtil.listAddObj("zbfs", def2);
			list.add(zbfs);// 0
		}

		/*
		 * String djwhere =" pk_defdoc = '" + parentVO.getVdef17() +
		 * "' and nvl(dr,0) = 0";
		 */
		String def1 = "0";

		if ("N".equals(parentVO.getVdef17())) {
			def1 = "否";
		} else {
			def1 = "是";
		}
		Map djht = OaWorkFlowUtil.listAddObj("djht", def1);
		list.add(djht);

		Map jedx = OaWorkFlowUtil.listAddObj("jedx", parentVO.getVdef16());
		list.add(jedx);

		Map htbgpk = OaWorkFlowUtil.listAddObj("htbgpk", parentVO.getVdef11());
		list.add(htbgpk);

		if (null != parentVO.getVdef11()) {
			String where = " pk_defdoc = '"
					+ parentVO.getVdef11()
					+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'GX-07' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
			String def11 = "0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					where)) {
				def11 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where);
			}

			Map htbgmc = OaWorkFlowUtil.listAddObj("htbgmc", def11);
			list.add(htbgmc);// 0
		}

		Map khzj = OaWorkFlowUtil.listAddObj("khzj", parentVO.getPk_customer());
		list.add(khzj);

		String kname = (String) getHyPubBO().findColValue(
				"bd_customer",
				"name",
				"nvl(dr,0) = 0 and pk_customer ='" + parentVO.getPk_customer()
						+ "'");
		Map khmc = OaWorkFlowUtil.listAddObj("khmc", kname);
		list.add(khmc);

		Map bz = OaWorkFlowUtil.listAddObj("bz", parentVO.getVdef20());
		list.add(bz);
		// 合同类别
		if (parentVO.getVdef18() != null) {
			String htlbbm = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getVdef18()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("htlbbm", htlbbm));
			String htlb = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getVdef18()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("htlb", htlb));
		}
		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	private WorkFlowBill getWorkFlowBill(AggCtArVO temp)
			throws BusinessException {
		String where = " nvl(dr,0) = 0 and pk_bill = '" + temp.getPrimaryKey()
				+ "'";
		WorkFlowBill[] workFlowBills = (WorkFlowBill[]) getHyPubBO()
				.queryByCondition(WorkFlowBill.class, where);
		WorkFlowBill workFlowBill = new WorkFlowBill();
		if (null != workFlowBills && workFlowBills.length > 1) {
			throw new BusinessException("查询到多条流程记录");
		}
		if (null != workFlowBills && workFlowBills.length == 1) {
			workFlowBill = workFlowBills[0];
			workFlowBill.setWorkflowName("收款合同");
		} else {
			workFlowBill.setWorkflowId("73");
			UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
					UserVO.class, temp.getParentVO().getBillmaker());
			workFlowBill.setDef5(userVO.getUser_code());
			if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
				PsndocVO psndoc = (PsndocVO) getHyPubBO().queryByPrimaryKey(
						PsndocVO.class, userVO.getPk_psndoc());
				workFlowBill.setDef5(psndoc.getId());
				workFlowBill.setWorkflowId("538");
			}
			workFlowBill.setPk_group(temp.getParentVO().getPk_group());
			workFlowBill.setPk_org(temp.getParentVO().getPk_org());
			workFlowBill.setPk_bill(temp.getPrimaryKey());
			workFlowBill.setUser_name(userVO.getUser_name());
			workFlowBill.setUser_code(userVO.getUser_code());
			workFlowBill.setCreationtime(new UFDate().toStdString());
			workFlowBill.setBill_status("1");
			workFlowBill.setCreator(temp.getParentVO().getCreator());
			workFlowBill.setBill_code(temp.getParentVO().getCbilltypecode());
			workFlowBill.setWorkflowName("收款合同");
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

	public String getDef1(String pk_org) throws BusinessException {
		String def1 = "";
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		if (null != orgVO) {
			def1 = orgVO.getDef1();
		}
		return def1;
	}

	public AggCtArVO[] terminate(AggCtArVO[] vos) throws BusinessException {
		try {
			return new ArTerminateAction().terminate(vos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtArVO[] unapprove(AggCtArVO[] vos, AbstractCompiler2 script)
			throws BusinessException {
		try {
			return new ArUnApproveAction().unApprove(vos, script);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtArVO[] unfreeze(AggCtArVO[] vos) throws BusinessException {
		try {
			return new ArUnFreezeAction().unfreeze(vos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtArVO[] unsendapprove(AggCtArVO[] vos, AbstractCompiler2 script)
			throws BusinessException {
		try {
			AggCtArVO[] aggVO = new ArUnsendApprove()
					.unsendApprove(vos, script);
			unOaCommit(aggVO);
			return aggVO;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	private void unOaCommit(AggCtArVO[] aggVO) throws BusinessException {
		for (AggCtArVO temp : aggVO) {
			if ((temp.getParentVO().getVtrantypecode()).contains("FCT2")) {
				String pk_org = (String) getHyPubBO().findColValue(
						"v_org_gc",
						"pk_org",
						" pk_org  = '" + temp.getParentVO().getPk_org()
								+ "' and pk_org <> '0001A110000000062PXO'");
				if(StringUtils.isNotEmpty(pk_org)){
					throw new BusinessException("进共享单据不允许收回！");
				}
				OaWorkFlowUtil.backOaWorkFlow(temp.getPrimaryKey());
			}
		}
	}

	public AggCtArVO[] unterminate(AggCtArVO[] vos) throws BusinessException {
		try {
			return new ArUnTerminateAction().unterminate(vos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtArVO[] unvalidate(AggCtArVO[] vos) throws BusinessException {
		try {
			return new ArUnValidateAction().unvalidate(vos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtArVO[] validate(AggCtArVO[] vos) throws BusinessException {
		try {
			return new ArValidateAction().validate(vos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}
}
