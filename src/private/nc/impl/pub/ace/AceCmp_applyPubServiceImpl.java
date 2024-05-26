package nc.impl.pub.ace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import nc.bs.cmp.apply.ace.bp.AceCmp_applyApproveBP;
import nc.bs.cmp.apply.ace.bp.AceCmp_applyApproveingBP;
import nc.bs.cmp.apply.ace.bp.AceCmp_applyDeleteBP;
import nc.bs.cmp.apply.ace.bp.AceCmp_applyInsertBP;
import nc.bs.cmp.apply.ace.bp.AceCmp_applyRollBackBP;
import nc.bs.cmp.apply.ace.bp.AceCmp_applySendApproveBP;
import nc.bs.cmp.apply.ace.bp.AceCmp_applySignalBP;
import nc.bs.cmp.apply.ace.bp.AceCmp_applyUnApproveBP;
import nc.bs.cmp.apply.ace.bp.AceCmp_applyUnApproveingBP;
import nc.bs.cmp.apply.ace.bp.AceCmp_applyUnSendApproveBP;
import nc.bs.cmp.apply.ace.bp.AceCmp_applyUpdateBP;
import nc.bs.cmp.apply.ace.bp.AceCmp_applyUpdateMoneyBP;
import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
import nc.cmp.utils.CmpUtils;
import nc.data.param.CommonParam;
import nc.impl.pubapp.pattern.data.bill.BillQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillConcurrentTool;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.itf.arap.forthird.IArapForDGSWService;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.pub.cmp.apply.ApplyOpEnum;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.so.mreturnassign.model.returnassign_base_config;
import nc.vo.bd.bankaccount.BankAccSubVO;
import nc.vo.bd.cust.CustSupplierVO;
import nc.vo.cmp.apply.AggApplyManagerVO;
import nc.vo.cmp.apply.AggApplyVO;
import nc.vo.cmp.apply.ApplyBVO;
import nc.vo.cmp.apply.ApplyVO;
import nc.vo.org.OrgVO;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.BillStatusEnum;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.bill.pagination.util.PaginationUtils;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.query2.sql.process.QueryCondition;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;
import nc.vo.sm.UserVO;
import nc.vo.tmpub.util.SqlUtil;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import nc.ws.intf.oadata.util.ConvertUpMoney;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

// 付款申请
@SuppressWarnings({ "unchecked", "rawtypes", "unused", "restriction" })
public abstract class AceCmp_applyPubServiceImpl {
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表明

	public AceCmp_applyPubServiceImpl() {
	}

	public AggApplyVO[] pubinsertBills(AggApplyVO[] clientFullVOs,
			AggApplyVO[] originBills) throws BusinessException {
		try {
			BillTransferTool<AggApplyVO> transferTool = new BillTransferTool(
					clientFullVOs);

			AceCmp_applyInsertBP action = new AceCmp_applyInsertBP();
			AggApplyVO[] retvos = action.insert(clientFullVOs);

			return (AggApplyVO[]) transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 删除
	public void pubdeleteBills(AggApplyVO[] clientFullVOs,
			AggApplyVO[] originBills) throws BusinessException {
		try {
			new AceCmp_applyDeleteBP().delete(clientFullVOs);
			for(AggApplyVO aggvo : clientFullVOs){
				ApplyVO parentVO = aggvo.getParentVO();
				// 调用东港税务系统同步单据状态
				IArapForDGSWService util = (IArapForDGSWService) NCLocator.getInstance().lookup(
						IArapForDGSWService.class);
				JSONObject res = util.sendBillByNCBill(parentVO.getPrimaryKey(),
						parentVO.getPk_org(), CommonParam.DELETE, parentVO.getPk_billtypecode());
				if (!"Y".equals(res.getString("success"))) {
					throw new BusinessException(res.getString("errinfo"));
				}
			}
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	public AggApplyVO[] pubupdateBills(AggApplyVO[] clientFullVOs,
			AggApplyVO[] originBills) throws BusinessException {
		try {
			BillTransferTool<AggApplyVO> transferTool = new BillTransferTool(
					clientFullVOs);

			AceCmp_applyUpdateBP bp = new AceCmp_applyUpdateBP();
			for (AggApplyVO clientvo : clientFullVOs) {
				clientvo.getParentVO().setTbbmessage(null);
			}

			AggApplyVO[] retvos = bp.update(clientFullVOs, originBills);
			Map<String, String> tbbmsg = new HashMap();
			for (int i = 0; i < retvos.length; i++) {
				tbbmsg.put(retvos[i].getParentVO().getPrimaryKey(), retvos[i]
						.getParentVO().getTbbmessage());
			}

			AggApplyVO[] billForToClient = (AggApplyVO[]) transferTool
					.getBillForToClient(retvos);

			for (AggApplyVO aggvo : billForToClient) {
				aggvo.getParentVO().setTbbmessage(
						(String) tbbmsg
								.get(aggvo.getParentVO().getPrimaryKey()));
			}

			return billForToClient;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public String[] pubquerypkbills(IQueryScheme queryScheme)
			throws BusinessException {
		StringBuffer sql = new StringBuffer();
		QuerySchemeProcessor processor = new QuerySchemeProcessor(queryScheme);

		processor.appendFuncPermissionOrgSql();
		String mainAlias = processor.getMainTableAlias();
		sql.append(" select  ");
		sql.append(mainAlias);
		sql.append(".");
		sql.append("pk_apply");
		sql.append(processor.getFinalFromWhere());
		sql.append(" and isnull(").append("opsrctype").append(",0)<>1 ");
		sql.append(" order by cmp_apply.pk_org,cmp_apply.applydate desc,cmp_apply.vbillno desc");
		DataAccessUtils dao = new DataAccessUtils();
		IRowSet rowset = dao.query(sql.toString());
		String[] keys = rowset.toOneDimensionStringArray();
		Set<String> keySet = new LinkedHashSet(keys.length);
		for (String string : keys) {
			keySet.add(string);
		}
		keys = (String[]) keySet.toArray(new String[0]);
		QueryCondition condition = processor.getQueryCondition("bisapproving");
		if ((condition != null) && (keys != null) && (keys.length > 0)) {
			Object[] values = condition.getValues();
			if (UFBoolean.valueOf(values[0].toString()).booleanValue()) {
				try {
					Collection<AggregatedValueObject> collection = ((IMDPersistenceQueryService) NCLocator
							.getInstance().lookup(
									IMDPersistenceQueryService.class))
							.queryBillOfVOByCond(ApplyVO.class, SqlUtil
									.buildSqlForIn(
											new ApplyVO().getPKFieldName(),
											keys), true, false);

					AggregatedValueObject[] vos = nc.vo.pubapp.pflow.PfServiceUtil
							.filterForApprove(
									(AggregatedValueObject[]) collection
											.toArray(new AggregatedValueObject[0]),
									"36D1", AppContext.getInstance()
											.getPkUser(), "pk_trantypecode");

					List<String> pks = new ArrayList();
					for (AggregatedValueObject aggVO : vos) {
						pks.add(aggVO.getParentVO().getPrimaryKey());
					}

					keys = (String[]) pks.toArray(new String[0]);
				} catch (Exception e) {
					ExceptionUtils.marsh(e);
				}
			}
		}

		return keys;
	}

	public AggApplyVO[] pubquerybillbypkbills(String[] pks)
			throws BusinessException {
		AggApplyVO[] bills = null;
		BillQuery<AggApplyVO> query = new BillQuery(AggApplyVO.class);

		bills = (AggApplyVO[]) query.query(pks);

		return (AggApplyVO[]) PaginationUtils.filterNotExistBills(bills, pks);
	}

	public AggApplyManagerVO[] pubqueryManagerbillbypkbills(String[] pks)
			throws BusinessException {
		AggApplyManagerVO[] bills = null;
		BillQuery<AggApplyManagerVO> query = new BillQuery(
				AggApplyManagerVO.class);

		bills = (AggApplyManagerVO[]) query.query(pks);
		return (AggApplyManagerVO[]) PaginationUtils.filterNotExistBills(bills,
				pks);
	}

	// 提交
	public AggApplyVO[] pubsendapprovebills(AggApplyVO[] clientFullVOs,
			AggApplyVO[] originBills) throws BusinessException {
		AceCmp_applySendApproveBP bp = new AceCmp_applySendApproveBP();
		AggApplyVO[] retvos = bp.sendApprove(clientFullVOs, originBills);

		senOaData(originBills);
		return retvos;
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

	private String billType = "36D1";

	private void senOaData(AggApplyVO[] billVOs) throws BusinessException {
		for (AggApplyVO temp : billVOs) {
			// 付款申请单
			ApplyVO hVO = temp.getParentVO();
			// 非域外
			if (StringUtils.isEmpty(hVO.getVdef1())) {
				if ("4".equals(getDef2(hVO.getPk_org()))
						&& hVO.getPk_billtypecode().contains(billType)) {
					OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(
							hVO.getPk_org(), "36D1");
					if (oaVo != null && oaVo.getIsdr() == 0) {
						WorkFId = oaVo.getFlowid();
						TableName = oaVo.getTablename();
						// 获取主表数据
						// 结算方式
						String jsName = (String) getHyPubBO()
								.findColValue(
										"bd_balatype",
										"name",
										"nvl(dr,0) = 0 and pk_balatype = '"
												+ ((ApplyBVO[]) temp
														.getChildrenVO())[0]
														.getPk_balatype() + "'");
						JSONArray headData = getMainMap(hVO, jsName);
						// 获取子表数据
						JSONArray bodyData = getDtaileDataMap(temp);
						// 构造workflow信息
						OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
						workFlowVO.setPrimaryKey(temp.getPrimaryKey());
						workFlowVO.setPkGroup(hVO.getPk_group());
						workFlowVO.setPkOrg(hVO.getPk_org());
						workFlowVO.setBillMaker(hVO.getBillmaker());
						workFlowVO.setCreator(hVO.getCreator());
						workFlowVO.setBillCode(billType);
						// 接口获取
						workFlowVO.setWorkflowId(OaWorkFlowUtil
								.getOAFlowID(WorkFId));
						workFlowVO.setWorkflowName("付款申请");
						WorkFlowBill bill = OaWorkFlowUtil
								.getWorkFlowBill(workFlowVO);
						bill.setDef3("ZT");
						// 制单人身份证号
						UserVO userVO = (UserVO) getHyPubBO()
								.queryByPrimaryKey(UserVO.class,
										hVO.getBillmaker());
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
			// 调用东港税务系统同步单据状态
			IArapForDGSWService util = (IArapForDGSWService) NCLocator.getInstance().lookup(
					IArapForDGSWService.class);
			// 单据号，组织主键，事件类型，单据类型
			JSONObject res = util.sendBillByNCBill(hVO.getPrimaryKey(),
					hVO.getPk_org(), CommonParam.COMMIT, hVO.getPk_billtypecode());
			if(!"Y".equals(res.getString("success"))){
				throw new BusinessException("提交税务系统报错："+res.getString("errinfo"));
			}
		}
	}

	private JSONArray getMainMap(ApplyVO parentVO, String jsName)
			throws BusinessException {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// ---------其他字段begin
		// 组织
		String stockName = (String) getHyPubBO().findColValue("org_orgs",
				"name",
				"nvl(dr,0) = 0 and pk_org = '" + parentVO.getPk_org() + "'");
		list.add(OaWorkFlowUtil.listAddObj("zzmc", stockName));
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
		// 收款单位
		String skdwname = (String) getHyPubBO().findColValue(
				"bd_cust_supplier",
				"name",
				"nvl(dr,0) = 0 and pk_cust_sup = '"
						+ parentVO.getPk_receiveunit() + "'");
		list.add(OaWorkFlowUtil.listAddObj("skdw", skdwname));
		if (null != parentVO.getPk_supplier()) {
			// 供应商
			CustSupplierVO custVO = (CustSupplierVO) getHyPubBO()
					.queryByPrimaryKey(CustSupplierVO.class,
							parentVO.getPk_supplier());
			// 供应商编码
			list.add(OaWorkFlowUtil.listAddObj("pk_supplier_code",
					custVO.getCode()));
			// 供应商名称
			list.add(OaWorkFlowUtil.listAddObj("pk_supplier_name",
					custVO.getName()));
		}

		// 收款银行账户
		if (parentVO.getPk_bankacc_r() != null) {
			BankAccSubVO yhvo = (BankAccSubVO) getHyPubBO().queryByPrimaryKey(
					BankAccSubVO.class, parentVO.getPk_bankacc_r());
			// 开户行
			String gryhzh = (String) getHyPubBO().findColValue(
					"bd_bankaccbas",
					"pk_bankdoc",
					"nvl(dr,0) = 0 and pk_bankaccbas = '"
							+ yhvo.getPk_bankaccbas() + "'");
			String yhda = (String) getHyPubBO().findColValue("bd_bankdoc",
					"name", "nvl(dr,0) = 0 and pk_bankdoc = '" + gryhzh + "'");
			list.add(OaWorkFlowUtil.listAddObj("skyhzh", yhvo.getAccnum()));
			list.add(OaWorkFlowUtil.listAddObj("skyhmc", yhda));
			list.add(OaWorkFlowUtil.listAddObj("khh", yhda));
		}
		// 申请付款金额大写
		list.add(OaWorkFlowUtil.listAddObj("applymny", parentVO.getApplysum()
				.toString()));
		// 申请付款金额大写
		list.add(OaWorkFlowUtil.listAddObj("sqfjkjedx",
				ConvertUpMoney.toChinese(parentVO.getOlcapplysum().toString())));
		// 经办人
		String apppsnhName = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc  = '" + parentVO.getPk_resuser()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("jbr", apppsnhName));
		// 付款方式 1=付款财务组织支付，2=委托上级组织支付，3=委托上级组织回拨支付
		int fkfsint = parentVO.getPaytype();
		String fkfs = "";
		if (fkfsint == 1) {
			fkfs = "付款财务组织支付";
		} else if (fkfsint == 2) {
			fkfs = "委托上级组织支付";
		} else if (fkfsint == 3) {
			fkfs = "委托上级组织回拨支付";
		}
		list.add(OaWorkFlowUtil.listAddObj("fkfs", fkfs));
		// 应付付款类型
		if (parentVO.getVdef2() != null) {
			String yffklxbm = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getVdef2()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("yflxbm", yffklxbm));
			String yffklx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getVdef2()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("yflx", yffklx));
			// =======
			list.add(OaWorkFlowUtil.listAddObj("yfklxbm", yffklxbm));
			list.add(OaWorkFlowUtil.listAddObj("yfklx", yffklx));
		}
		// 经办部门
		String sqbmName = (String) getHyPubBO().findColValue(
				"org_dept",
				"name",
				"nvl(dr,0) = 0 and pk_dept  = '" + parentVO.getPk_decidedept()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("jbbm", sqbmName));
		list.add(OaWorkFlowUtil.listAddObj("jsfs", jsName));
		// 交易类型名称
		String typeName = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and pk_billtypeid = '"
						+ parentVO.getPk_trantypeid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("typename", typeName));
		// 来源系统名称
		Map<Integer, String> sourcesystypeMap = sourcesystypeMap();
		if (sourcesystypeMap.containsKey(parentVO.getSourcesystypecode())) {
			list.add(OaWorkFlowUtil.listAddObj("sourcesystypename",
					sourcesystypeMap.get(parentVO.getSourcesystypecode())));
		}

		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	private JSONArray getDtaileDataMap(AggApplyVO temp)
			throws BusinessException {
		// 获取详细信息
		ApplyBVO[] bvos = (ApplyBVO[]) getHyPubBO().queryByCondition(
				ApplyBVO.class,
				"nvl(dr,0) = 0 and pk_apply = '" + temp.getPrimaryKey() + "'");
		Map bodyMap = getBody(bvos);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
		return dtlistString;
	}

	private Map getBody(ApplyBVO[] bvo) throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (ApplyBVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
					.transBean2Map(temp);
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("summary",
					temp.getSummary()));
			// 其他字段------begin
			// 项目
			if (null != temp.getPk_project()) {
				ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
						.queryByPrimaryKey(ProjectHeadVO.class,
								temp.getPk_project());
				// 项目编码
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bproject_code", projectVO.getProject_code()));
				// 项目名称
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bproject_name", projectVO.getProject_name()));
			}
			// 收款单位
			String skdwname = (String) getHyPubBO().findColValue(
					"bd_cust_supplier",
					"name",
					"nvl(dr,0) = 0 and pk_cust_sup = '"
							+ temp.getPk_receiveunit() + "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("skdw",
					skdwname));

			// 结算方式
			String jsName = (String) getHyPubBO().findColValue(
					"bd_balatype",
					"name",
					"nvl(dr,0) = 0 and pk_balatype = '" + temp.getPk_balatype()
							+ "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("jsfs",
					jsName));
			// 资金类别
			if (temp.getVdef20() != null) {
				String zjlb = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + temp.getVdef20()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"zjlb", zjlb));
			}
			// 业务部门
			if (temp.getPk_decidedept() != null) {
				String ywbm = (String) getHyPubBO().findColValue(
						"org_dept",
						"name",
						"nvl(dr,0) = 0 and pk_dept  = '"
								+ temp.getPk_decidedept() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"ywbm", ywbm));
			}
			// 业务员
			if (temp.getPk_resuser() != null) {
				String ywy = (String) getHyPubBO().findColValue(
						"bd_psndoc",
						"name",
						"nvl(dr,0) = 0 and   pk_psndoc  = '"
								+ temp.getPk_resuser() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("ywy",
						ywy));
			}
			// 来源单据类型
			if (temp.getPk_srcbilltypeid() != null) {
				String lydjlx = (String) getHyPubBO().findColValue(
						"bd_billtype",
						"billtypename",
						"nvl(dr,0) = 0 and     pk_billtypeid  = '"
								+ temp.getPk_srcbilltypeid() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"lydjlx", lydjlx));
			}
			// 来源系统
			if (null != temp.getSourcesystypecode()) {
				int lyxtint = temp.getSourcesystypecode();
				String lyxt = "";
				if (lyxtint == 1) {
					lyxt = "应付";
				} else if (lyxtint == 2) {
					lyxt = "其他";
				} else if (lyxtint == 3) {
					lyxt = "手工录入";
				} else if (lyxtint == 4) {
					lyxt = "供应链";
				} else if (lyxtint == 5) {
					lyxt = "项目管理";
				}
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"lyxt", lyxt));
			}
			// 来源单据号
			if (temp.getSrcbillno() != null) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"lydjh", temp.getSrcbillno()));
			}
			// 单据状态
			if (null != temp.getBusistatus()) {
				int djztint = temp.getBusistatus();
				String djzt = "";
				if (djztint == 1) {
					djzt = "待提交";
				} else if (djztint == 2) {
					djzt = "待审批";
				} else if (djztint == 3) {
					djzt = "待生成";
				} else if (djztint == 4) {
					djzt = "部分生成";
				} else if (djztint == 5) {
					djzt = "已生成";
				} else if (djztint == -1) {
					djzt = "自由态";
				}
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"djzt", djzt));
			}

			// 票据类型
			if(temp.getVdef22() != null){
				String pjlx = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + temp.getVdef22()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"pjlx", pjlx));
			}
			// 税码
			if (temp.getVdef28() != null) {
				String smstr = (String) getHyPubBO().findColValue(
						"bd_taxcode",
						"description",
						"nvl(dr,0) = 0 and pk_taxcode ='" + temp.getVdef28()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sm",
						smstr));
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

	// 收回
	public AggApplyVO[] pubunsendapprovebills(AggApplyVO[] clientFullVOs,
			AggApplyVO[] originBills) throws BusinessException {
		AceCmp_applyUnSendApproveBP bp = new AceCmp_applyUnSendApproveBP();
		for (AggApplyVO vo : originBills) {
			ApplyVO hVO = vo.getParentVO();
			OaWorkFlowUtil.backOaWorkFlow(hVO.getPrimaryKey());
			// 调用东港税务系统同步单据状态
			IArapForDGSWService util = (IArapForDGSWService) NCLocator.getInstance().lookup(
					IArapForDGSWService.class);
			// 单据号，组织主键，事件类型，单据类型
			JSONObject res = util.sendBillByNCBill(hVO.getPrimaryKey(),
					hVO.getPk_org(), CommonParam.RECALL, hVO.getPk_billtypecode());
			if(!"Y".equals(res.getString("success"))){
				throw new BusinessException("提交税务系统报错："+res.getString("errinfo"));
			}
		}
		AggApplyVO[] retvos = bp.unSend(clientFullVOs, originBills);
		return retvos;
	}

	public AggApplyVO[] pubapprovebills(AggApplyVO[] clientFullVOs,
			AggApplyVO[] originBills) throws BusinessException {
		for (int i = 0; (clientFullVOs != null) && (i < clientFullVOs.length); i++) {
			if (clientFullVOs[i].getParentVO().getDapprovedate() == null) {
				clientFullVOs[i].getParentVO().setDapprovedate(
						AppContext.getInstance().getBusiDate());
			}
			clientFullVOs[i].getParentVO().setStatus(1);
			for (int j = 0; (clientFullVOs[i] != null)
					&& (j < clientFullVOs[i].getChildren(ApplyBVO.class).length); j++) {
				clientFullVOs[i].getChildren(ApplyBVO.class)[j].setStatus(1);
			}
		}
		AggApplyVO[] retvos = null;
		if ((BillStatusEnum.APPROVED.value().equals(clientFullVOs[0]
				.getParentVO().getVbillstatus()))
				&& (!BillStatusEnum.APPROVED.value().equals(
						originBills[0].getParentVO().getVbillstatus()))) {
			retvos = new AceCmp_applyApproveBP().approve(clientFullVOs,
					originBills);
		} else if ((BillStatusEnum.APPROVED.value().equals(clientFullVOs[0]
				.getParentVO().getVbillstatus()))
				&& (BillStatusEnum.APPROVED.value().equals(originBills[0]
						.getParentVO().getVbillstatus()))) {
			retvos = new AceCmp_applySignalBP().approve(clientFullVOs,
					originBills);
		} else {
			retvos = new AceCmp_applyApproveingBP().approve(clientFullVOs,
					originBills);
		}
		return retvos;
	}

	public AggApplyVO[] pubunapprovebills(AggApplyVO[] clientFullVOs,
			AggApplyVO[] originBills) throws BusinessException {
		for (int i = 0; (clientFullVOs != null) && (i < clientFullVOs.length); i++) {
			clientFullVOs[i].getParentVO().setStatus(1);
			for (int j = 0; (clientFullVOs[i] != null)
					&& (j < clientFullVOs[i].getChildren(ApplyBVO.class).length); j++) {
				clientFullVOs[i].getChildren(ApplyBVO.class)[j].setStatus(1);
			}
		}
		AggApplyVO[] retvos = null;

		if (BillStatusEnum.APPROVED.value().equals(
				clientFullVOs[0].getParentVO().getVbillstatus())) {
			retvos = new AceCmp_applyRollBackBP().unApprove(clientFullVOs,
					originBills);
		} else if (BillStatusEnum.APPROVED.value().equals(
				originBills[0].getParentVO().getVbillstatus())) {
			retvos = new AceCmp_applyUnApproveBP().unApprove(clientFullVOs,
					originBills);
		} else if ((!BillStatusEnum.APPROVED.value().equals(
				clientFullVOs[0].getParentVO().getVbillstatus()))
				&& (!BillStatusEnum.APPROVED.value().equals(
						originBills[0].getParentVO().getVbillstatus()))) {
			retvos = new AceCmp_applyUnApproveingBP().unApprove(clientFullVOs,
					originBills);
		}
		return retvos;
	}

	public AggApplyVO[] pubUpdateMoney(AggApplyVO[] clientFullVOs,
			Map<String, UFDouble> moneyMap, ApplyOpEnum opEnum, boolean isPlan)
			throws BusinessException {
		try {
			BillConcurrentTool tool = new BillConcurrentTool();
			tool.lockBill(clientFullVOs);
			AggApplyVO[] oriApplys = (AggApplyVO[]) CmpUtils
					.cloneArrays(clientFullVOs);
			AceCmp_applyUpdateMoneyBP bp = new AceCmp_applyUpdateMoneyBP(
					moneyMap, opEnum, isPlan);
			return bp.update(clientFullVOs, oriApplys);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
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
		map.put(1, "应付");
		map.put(2, "其他");
		map.put(3, "手工录入");
		map.put(4, "供应链");
		map.put(5, "项目管理");
		return map;
	}
}
