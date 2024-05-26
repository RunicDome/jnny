package nc.impl.pcm.contract.pvt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pcm.contract.bp.rule.AlterCheckRefRule;
import nc.bs.pcm.contract.bp.rule.AlterDateCheckRule;
import nc.bs.pcm.contract.bp.rule.AlterValidateRule;
import nc.bs.pcm.contract.bp.rule.BeginFlagCheckRule;
import nc.bs.pcm.contract.bp.rule.BillCodeQuoteCheckRule;
import nc.bs.pcm.contract.bp.rule.ContrInsertRule;
import nc.bs.pcm.contract.bp.rule.CreateOldVersionRule;
import nc.bs.pcm.contract.bp.rule.DateCheckRule;
import nc.bs.pcm.contract.bp.rule.DeleteHisVersionRule;
import nc.bs.pcm.contract.bp.rule.TerminateBeforeRule;
import nc.bs.pmpub.rule.AppendBusiTypeBeforeRule;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pm.billrule.BillQuoteCheckRule;
import nc.impl.pm.billrule.SupplyProjectInfoBeforeRule;
import nc.impl.pm.billrule.UpdateBillSatusRule;
import nc.impl.pmpub.servicebase.BillBaseImpl;
import nc.impl.pmpub.servicebase.action.ApproveAction;
import nc.impl.pmpub.servicebase.action.DeleteAction;
import nc.impl.pmpub.servicebase.action.InsertAction;
import nc.impl.pmpub.servicebase.action.OtherButtonUpdateAction;
import nc.impl.pmpub.servicebase.action.UnApproveAction;
import nc.impl.pmpub.servicebase.action.UpdateAction;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.itf.bd.domain.cbs.ICBSNodeQueryService;
import nc.itf.pbm.commonrule.BillAlter4BudgetRule;
import nc.itf.pbm.commonrule.BillDelOrUnApprove4BudgetRule;
import nc.itf.pbm.commonrule.BillSaveOrApprove4BudgetRule;
import nc.itf.pbm.commonrule.BillTerminate4BudgetRule;
import nc.itf.pbm.commonrule.BillUpdate4BudgetAfterRule;
import nc.itf.pbm.commonrule.BillUpdate4BudgetBeforeRule;
import nc.itf.pcm.contract.pvt.IContract;
import nc.itf.pim.project.prv.IProjectQuery;
import nc.itf.pmpub.projecttype.pub.IProjectTypeQueryService;
import nc.pub.tools.VOUtils;
import nc.pubitf.pmr.pm.IPmFeebalanceCtMaintain;
import nc.ui.pcm.utils.GetDao;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.cbs.CBSNodeVO;
import nc.vo.bd.material.MaterialVO;
import nc.vo.bd.material.measdoc.MeasdocVO;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.bd.supplier.SupplierVO;
import nc.vo.org.DeptVO;
import nc.vo.org.OrgVO;
import nc.vo.pcm.contract.ContrHeadVO;
import nc.vo.pcm.contract.ContrItemsVO;
import nc.vo.pcm.contract.ContrWorksVO;
import nc.vo.pcm.contract.ContractBillVO;
import nc.vo.pcm.contracttype.ContractTypeHeadVO;
import nc.vo.pim.project.ProjectBillVO;
import nc.vo.pim.project.ProjectHeadVO;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pm.feebalance.PmFeebalanceBVO;
import nc.vo.pm.feebalance.PmFeebalanceHVO;
import nc.vo.pmpub.projecttype.ProjectTypeHeadVO;
import nc.vo.pmpub.wbs.WbsVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.sm.UserVO;
import nc.ws.intf.ContractUtils;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

// 清单发包合同
@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
public class ContractImpl extends BillBaseImpl<ContractBillVO> implements
		IContract {
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表名
	private static String RL_001 = "4D42-01";
	public static String WORKFLOWID_JNRL_QD = "64";
	public static String WORKFLOWID_JNNY_QD = "305";
	private static String RL_WORKFLOW_ID_001_TABLE_NAME = "清单发包合同";

	public static String BODY_TABLE_NAME_JNRL_QD_dt1 = "formtable_main_69_dt1";
	public static String BODY_TABLE_NAME_JNNY_QD_dt1 = "formtable_main_346_dt1";
	public static String BODY_TABLE_NAME_JNRL_QD_dt2 = "formtable_main_69_dt2";
	public static String BODY_TABLE_NAME_JNNY_QD_dt2 = "formtable_main_346_dt2";

	private static String RL_005 = "4D42-Cxx-05";
	public static String WORKFLOWID_JNRL_SG = "49";
	public static String WORKFLOWID_JNNY_SG = "307";
	private static String RL_WORKFLOW_ID_005_TABLE_NAME = "施工合同";

	public static String BODY_TABLE_NAME_JNRL_SG_dt1 = "formtable_main_52_dt1";
	public static String BODY_TABLE_NAME_JNNY_SG_dt1 = "formtable_main_348_dt1";
	public static String BODY_TABLE_NAME_JNRL_SG_dt2 = "formtable_main_52_dt2";
	public static String BODY_TABLE_NAME_JNNY_SG_dt2 = "formtable_main_348_dt2";

	private static String RL_004 = "4D42-Cxx-04";
	public static String WORKFLOWID_JNRL_SJ = "65";
	public static String WORKFLOWID_JNNY_SJ = "306";
	private static String RL_WORKFLOW_ID_004_TABLE_NAME = "设计合同";

	public static String BODY_TABLE_NAME_JNRL_SJ_dt1 = "formtable_main_133_dt1";
	public static String BODY_TABLE_NAME_JNNY_SJ_dt1 = "formtable_main_347_dt1";
	public static String BODY_TABLE_NAME_JNRL_SJ_dt2 = "formtable_main_133_dt2";
	public static String BODY_TABLE_NAME_JNNY_SJ_dt2 = "formtable_main_347_dt2";

	private static String RL_003 = "4D42-Cxx-03";
	public static String WORKFLOWID_JNRL_JL = "116";
	public static String WORKFLOWID_JNNY_JL = "303";
	private static String RL_WORKFLOW_ID_003_TABLE_NAME = "监理合同";

	public static String BODY_TABLE_NAME_JNRL_JL_dt1 = "formtable_main_132_dt1";
	public static String BODY_TABLE_NAME_JNNY_JL_dt1 = "formtable_main_344_dt1";
	public static String BODY_TABLE_NAME_JNRL_JL_dt2 = "formtable_main_132_dt2";
	public static String BODY_TABLE_NAME_JNNY_JL_dt2 = "formtable_main_344_dt2";

	// GenSaveUtils genSaveUtils = new GenSaveUtils();
	public ContractImpl() {
	}

	public ContractBillVO[] alterContr(ContractBillVO[] billVOs,
			ContractBillVO[] originBillVOs) throws BusinessException {
		OtherButtonUpdateAction<ContractBillVO> action = createUpdateWithoutEventAction(
				"1231", "1232");
		// 新增保存前校验
		ContrHeadVO hvos = beforecheck(billVOs[0]);
		if (hvos != null) {
			billVOs[0].setParentVO(hvos);
		}
		initAlterAction(action, originBillVOs);
		return (ContractBillVO[]) action.processAction(billVOs, originBillVOs);
	}

	public Object approveContr(ContractBillVO[] billVOs, PfParameterVO pfParamVO)
			throws BusinessException {
		return approve(billVOs, pfParamVO);
	}

	// XBX新增保存前校验
	public ContrHeadVO beforecheck(ContractBillVO billVO)
			throws BusinessException {
		ContrHeadVO headvo = null;
		if (billVO != null) {
			headvo = billVO.getParentVO();
			ContrWorksVO[] bvos = (ContrWorksVO[]) billVO
					.getChildren(ContrWorksVO.class);
			if (bvos == null || bvos.length <= 0) {
				throw new BusinessException("合同基本数据不能为空！");
			}
			if (headvo.getHdef53() != null
					&& bvos[0].getSrc_pk_bill_b() != null) {
				return headvo;
			}
			String pk_org = headvo.getPk_org();// 组织
			String transType = headvo.getTransi_type(); // 交易类型
			String htlx = headvo.getPk_contracttype(); // 合同类型
			String pk_project = headvo.getPk_project(); // 项目主键

			OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
					pk_org);

			// 查询档案，取校验组织 Object conforg = (Object)
			Object conforg = (Object) getHyPubBO().findColValue(
					"bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
							+ "FROM BD_DEFDOCLIST WHERE CODE = 'HTSAVEORG')"
							+ " and code = '" + pk_org + "'");// 组织
			if (conforg != null) {
				// 调用工具类校验是否可保存
				ContractUtils utils = new ContractUtils();
				String error = utils.checkIfSave(billVO, 0);
				if (StringUtils.isNotEmpty(error)
						&& !StringUtils.equals("null", error)) {
					throw new BusinessException(error);
				}
			}

			String pk_fatherorg = orgVO.getPk_fatherorg();
			// 热力
			if ("0001A110000000000HYQ".equals(pk_org)
					|| "0001A21000000003U6L6".equals(pk_org)
					|| "0001A21000000003U6L6".equals(pk_fatherorg)) {
				checkRLOrg(billVO);
			} else {
				// 山东济华及济南济华项目+施工单位只允许保存一个清单发包合同
				checkRQOrg(billVO);
			}
			// 能投 施工合同保存时判断是否有设计合同，按项目类型 热源类建设项目，一次管网项目、基建类项目、小配套
			checkSGCont(billVO);
			// 推进度
			int orgFlag = checkOrg(pk_org);
			if (orgFlag == 1) {// 该组织需要推进度
				if (checkIfHaveWbs(transType)) {
					String wbs_name = htlxcheck(htlx);
					if (wbs_name != null && !"".equals(wbs_name)) {
						// 判断当前任务是否完成进度，是的话不需要推，否则需要推
						GetDao getDao = NCLocator.getInstance().lookup(
								GetDao.class);
						String rwsql = "SELECT RWMX.TASKPERCENT,WBS.WBS_NAME FROM PM_TASKSCHEDULE_B RWMX LEFT JOIN "
								+ "PM_TASKSCHEDULE RW ON RWMX.PK_TASKSCHEDULE = RW.PK_TASKSCHEDULE LEFT JOIN PM_WBS WBS "
								+ "ON RWMX.PK_WBS = WBS.PK_WBS WHERE RWMX.DR = 0 AND RW.DR = 0 AND WBS.DR = 0 AND "
								+ "WBS.WBS_NAME = '"
								+ wbs_name
								+ "' AND RWMX.PK_PROJECT = '"
								+ pk_project
								+ "'";
						List<Object[]> rwLs = getDao.query(rwsql);
						if (rwLs != null && rwLs.size() > 0
								&& rwLs.get(0)[0] != null) {
							String jd = rwLs.get(0)[0] + "";
							if (jd != null && !"".equals(jd)) {
								// 进度不为100 需要推
								if (Double.parseDouble(jd) != 100) {
									String pk_wbs = getWbsWork(pk_org,
											pk_project, wbs_name);
									headvo.setPk_wbs(pk_wbs);
								}
							}
						} else {
							String pk_wbs = getWbsWork(pk_org, pk_project,
									wbs_name);
							headvo.setPk_wbs(pk_wbs);
						}
					}
				}
			}
		} else {
			throw new BusinessException("未获取到相应VO！");
		}
		return headvo;
	}

	// 根据合同类型查询对应任务名称-----参数：htlx==合同类型
	private String htlxcheck(String htlx) throws DAOException {
		String wbs_name = "";
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		String wbsNameSql = "SELECT SHORTNAME FROM BD_DEFDOC WHERE PK_DEFDOCLIST = "
				+ "'1001A11000000026ES9C' AND DR = 0 AND CODE = '" + htlx + "'";
		List<Object[]> wbsNameLs = getDao.query(wbsNameSql);
		if (wbsNameLs != null && wbsNameLs.size() > 0
				&& wbsNameLs.get(0)[0] != null) {
			wbs_name = wbsNameLs.get(0)[0] + "";
		}
		System.out.println("wbsNameSq==" + wbsNameSql);
		return wbs_name;
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

	private WorkFlowBill getWorkFlowBill(ContractBillVO temp)
			throws BusinessException {
		String where = " nvl(dr,0) = 0 and pk_bill = '" + temp.getPrimaryKey()
				+ "'";
		WorkFlowBill[] workFlowBills = (WorkFlowBill[]) getHyPubBO()
				.queryByCondition(WorkFlowBill.class, where);
		WorkFlowBill workFlowBill = new WorkFlowBill();
		if (null != workFlowBills && workFlowBills.length > 1) {
			throw new BusinessException("查询到多条流程记录");
		}
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				temp.getParentVO().getBillmaker());
		String flowname = VOUtils.getDocName(ContractTypeHeadVO.class, temp
				.getParentVO().getPk_contracttype());// 合同类型
		/*
		 * String zddrq = temp.getParentVO().getBillmaketime() .getYear() + "-"
		 * + temp.getParentVO().getBillmaketime().getStrMonth() + "-" +
		 * temp.getParentVO().getBillmaketime().getStrDay();
		 */
		if (null != workFlowBills && workFlowBills.length == 1) {
			workFlowBill = workFlowBills[0];
			if (RL_001.equals(temp.getParentVO().getTransi_type())) {
				workFlowBill.setWorkflowName(RL_WORKFLOW_ID_001_TABLE_NAME);
			}
			if (RL_005.equals(temp.getParentVO().getTransi_type())) {
				workFlowBill.setWorkflowName(RL_WORKFLOW_ID_005_TABLE_NAME);
			}
			if (RL_004.equals(temp.getParentVO().getTransi_type())) {
				if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
					workFlowBill.setWorkflowName(RL_WORKFLOW_ID_004_TABLE_NAME);
				} else {
					workFlowBill.setWorkflowName(flowname);
				}
			}
			if (RL_003.equals(temp.getParentVO().getTransi_type())) {
				workFlowBill.setWorkflowName(RL_WORKFLOW_ID_003_TABLE_NAME);
			}
		} else {

			workFlowBill.setPk_group(temp.getParentVO().getPk_group());
			workFlowBill.setPk_org(temp.getParentVO().getPk_org());
			workFlowBill.setPk_bill(temp.getPrimaryKey());
			workFlowBill.setUser_name(userVO.getUser_name());
			workFlowBill.setUser_code(userVO.getUser_code());
			workFlowBill.setBill_status("1");
			workFlowBill.setCreationtime(new UFDate().toStdString());
			workFlowBill.setCreator(temp.getParentVO().getCreator());
			workFlowBill.setBill_code("4D42");
			UserVO user = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
					temp.getParentVO().getBillmaker());
			workFlowBill.setDef5(user.getUser_code());
			if (RL_001.equals(temp.getParentVO().getTransi_type())) {
				workFlowBill.setWorkflowName(RL_WORKFLOW_ID_001_TABLE_NAME);
				workFlowBill.setWorkflowId(WORKFLOWID_JNRL_QD);
				if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
					PsndocVO psndoc = (PsndocVO) getHyPubBO()
							.queryByPrimaryKey(PsndocVO.class,
									user.getPk_psndoc());
					workFlowBill.setDef5(psndoc.getId());
					workFlowBill.setWorkflowId(WORKFLOWID_JNNY_QD);
				}
			}
			if (RL_005.equals(temp.getParentVO().getTransi_type())) {
				workFlowBill.setWorkflowName(RL_WORKFLOW_ID_005_TABLE_NAME);
				workFlowBill.setWorkflowId(WORKFLOWID_JNRL_SG);
				if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
					PsndocVO psndoc = (PsndocVO) getHyPubBO()
							.queryByPrimaryKey(PsndocVO.class,
									user.getPk_psndoc());
					workFlowBill.setDef5(psndoc.getId());
					workFlowBill.setWorkflowId(WORKFLOWID_JNNY_SG);
				}
			}
			if (RL_004.equals(temp.getParentVO().getTransi_type())) {
				if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
					workFlowBill.setWorkflowName(RL_WORKFLOW_ID_004_TABLE_NAME);
				} else {
					workFlowBill.setWorkflowName(flowname);
				}
				workFlowBill.setWorkflowId(WORKFLOWID_JNRL_SJ);
				if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
					PsndocVO psndoc = (PsndocVO) getHyPubBO()
							.queryByPrimaryKey(PsndocVO.class,
									user.getPk_psndoc());
					workFlowBill.setDef5(psndoc.getId());
					workFlowBill.setWorkflowId(WORKFLOWID_JNNY_SJ);
				}
			}
			if (RL_003.equals(temp.getParentVO().getTransi_type())) {
				workFlowBill.setWorkflowName(RL_WORKFLOW_ID_003_TABLE_NAME);
				// workFlowBill.setWorkflowId("66");
				workFlowBill.setWorkflowId(WORKFLOWID_JNRL_JL);
				if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
					PsndocVO psndoc = (PsndocVO) getHyPubBO()
							.queryByPrimaryKey(PsndocVO.class,
									user.getPk_psndoc());
					workFlowBill.setDef5(psndoc.getId());
					workFlowBill.setWorkflowId(WORKFLOWID_JNNY_JL);
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

	public ContractBillVO[] commitContr(ContractBillVO[] billVOs)
			throws BusinessException {
		ContractBillVO[] aggVOs = commit(billVOs);
		for (ContractBillVO temp : aggVOs) {
			OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
					temp.getParentVO().getPk_org());
			ContrHeadVO hVO = temp.getParentVO();
			String ifbl = temp.getParentVO().getHdef25();// 是否补录
			String dbmhtbm = temp.getParentVO().getHdef53();// 多编码合同编码
			if (ifbl == null || "1001A2100000000B68C3".equals(ifbl)) {
				if (null != orgVO.getDef2()
						&& (dbmhtbm == null || dbmhtbm.equals("~"))) {
					if ((temp.getParentVO().getBill_type()).contains("4D42")) {
						String transi_type = hVO.getTransi_type();
						if ("4".equals(orgVO.getDef2())) {
							// 能投
							OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(
									hVO.getPk_org(), transi_type);
							if (oaVo != null && oaVo.getIsdr() == 0) {
								WorkFId = oaVo.getFlowid();
								TableName = oaVo.getTablename();
								// 获取主表数据
								String bhsjemap = null;
								ContrWorksVO[] tm = (ContrWorksVO[]) temp
										.getChildren(ContrWorksVO.class);
								if (null != tm[0].getBdef1()) {
									bhsjemap = tm[0].getBdef1() + "";
								}
								JSONArray headData = getGHMainMap(hVO, bhsjemap);
								// 获取子表数据
								JSONArray bodyData = getDtaileDataMapNew(temp);
								// 构造workflow信息
								OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
								workFlowVO.setPrimaryKey(temp.getPrimaryKey());
								workFlowVO.setPkGroup(hVO.getPk_group());
								workFlowVO.setPkOrg(hVO.getPk_org());
								workFlowVO.setBillMaker(hVO.getBillmaker());
								workFlowVO.setCreator(hVO.getCreator());
								workFlowVO.setBillCode("4D42");// 交易类型
								// 接口获取

								workFlowVO.setWorkflowId(OaWorkFlowUtil
										.getOAFlowID(WorkFId));
								workFlowVO.setWorkflowName(oaVo
										.getBilltypename());// 单据名称
								WorkFlowBill bill = OaWorkFlowUtil
										.getWorkFlowBill(workFlowVO);
								bill.setDef3("ZT");
								bill.setDef4(hVO.getBill_code());
								// 制单人身份证号
								UserVO userVO = (UserVO) getHyPubBO()
										.queryByPrimaryKey(UserVO.class,
												hVO.getBillmaker());
								String idCard = (String) getHyPubBO()
										.findColValue(
												"bd_psndoc",
												"id",
												"nvl(dr,0) = 0 and pk_psndoc='"
														+ userVO.getPk_psndoc()
														+ "'");
								bill.setDef5(idCard);
								// 调用OA工具类同步数据至OA
								OaWorkFlowUtil.sendOaData(headData, bodyData,
										bill);
							}
						} else {
							// 能投
							OAFlowVO Vo = OaWorkFlowUtil.getOAFlowInfo(
									hVO.getPk_org(), transi_type);
							if (Vo != null && Vo.getIsdr() == 0) {
								WorkFId = Vo.getFlowid();
								TableName = Vo.getTablename();
								// 获取主表数据
								String bhsjemap = null;
								ContrWorksVO[] tm = (ContrWorksVO[]) temp
										.getChildren(ContrWorksVO.class);
								if (null != tm[0].getBdef1()) {
									bhsjemap = tm[0].getBdef1() + "";
								}
								JSONArray headData = getGHMainMap(hVO, bhsjemap);
								// 获取子表数据
								JSONArray bodyData = getDtaileDataMapNew(temp);
								// 构造workflow信息
								OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
								workFlowVO.setPrimaryKey(temp.getPrimaryKey());
								workFlowVO.setPkGroup(hVO.getPk_group());
								workFlowVO.setPkOrg(hVO.getPk_org());
								workFlowVO.setBillMaker(hVO.getBillmaker());
								workFlowVO.setCreator(hVO.getCreator());
								workFlowVO.setBillCode("4D42");// 交易类型
								// 接口获取

								workFlowVO.setWorkflowId(OaWorkFlowUtil
										.getOAFlowID(WorkFId));
								workFlowVO
										.setWorkflowName(Vo.getBilltypename());// 单据名称
								WorkFlowBill bill = OaWorkFlowUtil
										.getWorkFlowBill(workFlowVO);
								bill.setDef3("ZT");
								bill.setDef4(hVO.getBill_code());
								// 制单人身份证号
								UserVO userVO = (UserVO) getHyPubBO()
										.queryByPrimaryKey(UserVO.class,
												hVO.getBillmaker());
								String idCard = (String) getHyPubBO()
										.findColValue(
												"bd_psndoc",
												"id",
												"nvl(dr,0) = 0 and pk_psndoc='"
														+ userVO.getPk_psndoc()
														+ "'");
								bill.setDef5(idCard);
								// 调用OA工具类同步数据至OA
								OaWorkFlowUtil.sendOaData(headData, bodyData,
										bill);
							} else if ("1".equals(orgVO.getDef2())) {
								if (RL_001.equals(transi_type)) {
									// 清单发包合同
									// XBX 如果组织为港华，调用全量字段 期初不传OA Begin_flag
									if ("4".equals(orgVO.getDef2())
											&& !hVO.getBegin_flag()
													.booleanValue()) {
										OAFlowVO oaVo = OaWorkFlowUtil
												.getOAFlowInfo(hVO.getPk_org(),
														"4D42");
										if (oaVo.getIsdr() == 0) {
											WorkFId = oaVo.getFlowid();
											TableName = oaVo.getTablename();
											// 获取主表数据
											String bhsjemap = null;
											ContrWorksVO[] tm = (ContrWorksVO[]) temp
													.getChildren(ContrWorksVO.class);
											if (null != tm[0].getBdef1()) {
												bhsjemap = tm[0].getBdef1()
														+ "";
											}
											JSONArray headData = getGHMainMap(
													hVO, bhsjemap);
											// 获取子表数据
											JSONArray bodyData = getDtaileDataMapNew(temp);
											// 构造workflow信息
											OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
											workFlowVO.setPrimaryKey(temp
													.getPrimaryKey());
											workFlowVO.setPkGroup(hVO
													.getPk_group());
											workFlowVO
													.setPkOrg(hVO.getPk_org());
											workFlowVO.setBillMaker(hVO
													.getBillmaker());
											workFlowVO.setCreator(hVO
													.getCreator());
											workFlowVO.setBillCode("4D42");
											// 测试临时写死，后期改为接口获取
											/*
											 * workFlowVO.setWorkflowId(
											 * OaWorkFlowUtil
											 * .getOAFlowID(WorkFId));
											 */
											workFlowVO.setWorkflowId(WorkFId);
											workFlowVO
													.setWorkflowName("清单发包合同");
											WorkFlowBill bill = OaWorkFlowUtil
													.getWorkFlowBill(workFlowVO);
											bill.setDef3("ZT");
											// 制单人身份证号
											UserVO userVO = (UserVO) getHyPubBO()
													.queryByPrimaryKey(
															UserVO.class,
															hVO.getBillmaker());
											String idCard = (String) getHyPubBO()
													.findColValue(
															"bd_psndoc",
															"id",
															"nvl(dr,0) = 0 and pk_psndoc='"
																	+ userVO.getPk_psndoc()
																	+ "'");
											bill.setDef5(idCard);
											// 调用OA工具类同步数据至OA
											OaWorkFlowUtil.sendOaData(headData,
													bodyData, bill);
										}
									} else {
										if (getDef2(hVO.getPk_org()) != null) {
											String bhsjemap = null;
											ContrWorksVO[] tm = (ContrWorksVO[]) temp
													.getChildren(ContrWorksVO.class);
											if (null != tm[0].getBdef1()) {
												bhsjemap = tm[0].getBdef1()
														+ "";
											}
											JSONArray headData = getMainMap(
													temp.getParentVO(),
													bhsjemap);
											JSONArray bodyData = getDtaileDataMap(temp);
											OaWorkFlowUtil.sendOaData(headData,
													bodyData,
													getWorkFlowBill(temp));
										}
									}
								}
								if (RL_005.equals(transi_type)) {
									// 施工合同
									JSONArray headData = getMainMap(
											temp.getParentVO(), null);
									JSONArray bodyData = getDtaileDataMap(temp);
									OaWorkFlowUtil.sendOaData(headData,
											bodyData, getWorkFlowBill(temp));
								}
								if (RL_004.equals(transi_type)) {
									// 设计合同
									JSONArray headData = getMainMap(
											temp.getParentVO(), null);
									JSONArray bodyData = getDtaileDataMap(temp);
									OaWorkFlowUtil.sendOaData(headData,
											bodyData, getWorkFlowBill(temp));

								}
								if (RL_003.equals(transi_type)) {
									// 监理合同
									JSONArray headData = getMainMap(
											temp.getParentVO(), null);
									JSONArray bodyData = getDtaileDataMap(temp);
									OaWorkFlowUtil.sendOaData(headData,
											bodyData, getWorkFlowBill(temp));
								}
							}
						}
					}
				}
			}
		}
		return aggVOs;
	}

	// 拼接港华Main字段
	private JSONArray getGHMainMap(ContrHeadVO parentVO, String bhsje)
			throws BusinessException {
		// TODO Auto-generated method stub
		List<Map<String, Object>> list = new ArrayList();
		// 构造数据
		if (!"1".equals(getDef2(parentVO.getPk_org()))) {
			list = OaWorkFlowUtil.transBean2Map(parentVO);
		}
		// ---------其他字段begin
		// 组织
		String stockName = (String) getHyPubBO()
				.findColValue(
						"org_itemorg",
						"name",
						"nvl(dr,0) = 0 and pk_itemorg = '"
								+ parentVO.getPk_org() + "'");
		list.add(OaWorkFlowUtil.listAddObj("zzmc", stockName));
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		// 合同类型
		String billTypeName = (String) getHyPubBO().findColValue(
				"pm_contracttype",
				"type_name",
				"nvl(dr,0) = 0 and pk_contracttype  = '"
						+ parentVO.getPk_contracttype() + "'");
		list.add(OaWorkFlowUtil.listAddObj("typename", billTypeName));
		list.add(OaWorkFlowUtil.listAddObj("billTypeName", billTypeName));

		if (null != parentVO.getPk_project()) {
			// 项目
			ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
					.queryByPrimaryKey(ProjectHeadVO.class,
							parentVO.getPk_project());
			// 项目编码
			list.add(OaWorkFlowUtil.listAddObj("project_code",
					projectVO.getProject_code()));
			list.add(OaWorkFlowUtil.listAddObj("xmbm",
					projectVO.getProject_code()));
			// 项目名称
			list.add(OaWorkFlowUtil.listAddObj("project_name",
					projectVO.getProject_name()));
			list.add(OaWorkFlowUtil.listAddObj("xmmc",
					projectVO.getProject_name()));
			// 项目类型名称
			String xmlx = (String) getHyPubBO().findColValue(
					"bd_projectclass",
					"type_name",
					"nvl(dr,0) = 0 and pk_projectclass  = '"
							+ projectVO.getPk_projectclass() + "'");
			list.add(OaWorkFlowUtil.listAddObj("xmlx", xmlx));
		}
		// 签约人
		String apppsnhName = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc  = '"
						+ parentVO.getPk_promisepsn() + "'");
		list.add(OaWorkFlowUtil.listAddObj("qyr", apppsnhName));
		// 签约部门
		String sqbmName = (String) getHyPubBO().findColValue(
				"org_dept_v",
				"name",
				"nvl(dr,0) = 0 and pk_vid = '" + parentVO.getPk_pro_dept_v()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("qybm", sqbmName));
		// 监理单位
		SupplierVO supplierVO = (SupplierVO) getHyPubBO().queryByPrimaryKey(
				SupplierVO.class, parentVO.getPk_supplier());
		String name = "0";
		if (null != supplierVO.getPk_supplier()) {
			name = supplierVO.getName();
		}
		list.add(OaWorkFlowUtil.listAddObj("pk_supplier_name", name));
		list.add(OaWorkFlowUtil.listAddObj("gys", name));
		if (parentVO.getHdef2() != null) {
			SupplierVO jldwVO = (SupplierVO) getHyPubBO().queryByPrimaryKey(
					SupplierVO.class, parentVO.getHdef2());
			String jldw = "0";
			if (null != jldwVO.getPk_supplier()) {
				jldw = jldwVO.getName();
			}
			list.add(OaWorkFlowUtil.listAddObj("jldw", jldw));
		}
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
		if (parentVO.getHdef44() != null) {
			String yylx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getHdef44()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("yylx", yylx));
		}
		// 是否使用电子签章
		if (parentVO.getHdef45() != null) {
			String sfsydzqz = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getHdef45()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sfsydzqz", sfsydzqz));
		}
		// 项目审批类型
		if (parentVO.getHdef51() != null) {
			String xmsplxbm = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getHdef51()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("xmsplxbm", xmsplxbm));
			String xmsplx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getHdef51()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("xmsplx", xmsplx));
		}
		// 能投原字段
		list.add(OaWorkFlowUtil.listAddObj("sszzmc", stockName));// 库存组织名称
		list.add(OaWorkFlowUtil.listAddObj("bhsje", bhsje));// 不含税金额
		if (null != parentVO.getHdef34()) {
			String htmb = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getHdef34()
							+ "'");
			String htmbbm = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getHdef34()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("htmb", htmb));// 合同模板
			list.add(OaWorkFlowUtil.listAddObj("htmblx", htmbbm));// 合同模板编码
		}
		list.add(OaWorkFlowUtil.listAddObj("qyszzmc", "济南热力集团有限公司"));// 契约锁组织名称
		list.add(OaWorkFlowUtil.listAddObj("ctid", parentVO.getPrimaryKey()));// 单据主键
		// 契约锁
		if (null != parentVO.getHdef36()) {
			list.add(OaWorkFlowUtil.listAddObj("jfqsr", parentVO.getHdef36()));// 甲方签收人
		}
		if (null != parentVO.getHdef37()) {
			list.add(OaWorkFlowUtil.listAddObj("jflxdh", parentVO.getHdef37()));// 甲方联系电话
			list.add(OaWorkFlowUtil.listAddObj("jfqsrlxdh",
					parentVO.getHdef37()));// 甲方签收人联系电话==/甲方联系电话
		}
		if (null != parentVO.getHdef38()) {
			list.add(OaWorkFlowUtil.listAddObj("yfzdsddd", parentVO.getHdef38()));// 乙方指定送达地址
			list.add(OaWorkFlowUtil.listAddObj("zs", parentVO.getHdef38()));// 住所
		}
		if (null != parentVO.getHdef39()) {
			list.add(OaWorkFlowUtil.listAddObj("yfqsr", parentVO.getHdef39()));// 乙方签收人
			list.add(OaWorkFlowUtil.listAddObj("jlrqsr", parentVO.getHdef39()));// 监理人签收人
																				// ==
																				// 乙方签收人
		}
		if (null != parentVO.getHdef40()) {
			list.add(OaWorkFlowUtil.listAddObj("yflxdh", parentVO.getHdef40()));// 乙方联系电话
			list.add(OaWorkFlowUtil.listAddObj("jlrlxdh", parentVO.getHdef40()));// 监理人联系电话
																					// ==乙方联系电话
		}
		// 契约锁对应流程id
		String qyshtmb = (String) getHyPubBO().findColValue("bd_defdoc",
				"mnecode",
				"nvl(dr,0)= 0 and pk_defdoc = '" + parentVO.getHdef34() + "'");
		if (qyshtmb == null || "".equals(qyshtmb) || qyshtmb == "")
			qyshtmb = "2936164539873645088";
		list.add(OaWorkFlowUtil.listAddObj("qyshtmb", qyshtmb));// 契约锁合同模板
		if (null != parentVO.getPromiseplace()) {
			list.add(OaWorkFlowUtil.listAddObj("gcdd",
					parentVO.getPromiseplace()));// 工程地点
			list.add(OaWorkFlowUtil.listAddObj("gcdz",
					parentVO.getPromiseplace()));// 工程地点
		}
		if (null != parentVO.getHdef5()) {
			list.add(OaWorkFlowUtil.listAddObj(
					"jgc",
					new UFDouble(parentVO.getHdef5() == null ? "0.00"
							: parentVO.getHdef5()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ ""));// 甲供材
		}
		if (null != parentVO.getHdef6()) {
			list.add(OaWorkFlowUtil.listAddObj(
					"azf",
					new UFDouble(parentVO.getHdef6() == null ? "0.00"
							: parentVO.getHdef6()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ ""));// 安装费
		}
		if (null != parentVO.getHdef8()) {
			list.add(OaWorkFlowUtil.listAddObj("cbsgfs", parentVO.getHdef8()));// 承包施工方式
		}
		if (null != parentVO.getHdef16()) {
			list.add(OaWorkFlowUtil.listAddObj("cbfwhnr", parentVO.getHdef16()));// 承包范围和内容
		}
		if (null != parentVO.getHdef17()) {
			list.add(OaWorkFlowUtil.listAddObj("gqrlts", parentVO.getHdef17()));// 工期日历天数
		}
		if (null != parentVO.getHdef18()) {
			list.add(OaWorkFlowUtil.listAddObj("jfdb", parentVO.getHdef18()));// 甲方代表
		}
		if (null != parentVO.getHdef19()) {
			list.add(OaWorkFlowUtil.listAddObj("yfdb", parentVO.getHdef19()));// 乙方代表
		}
		if (null != parentVO.getHdef20()) {
			list.add(OaWorkFlowUtil.listAddObj("sjdw", parentVO.getHdef20()));// 审计单位
		}
		if (null != parentVO.getHdef4()) {
			list.add(OaWorkFlowUtil.listAddObj(
					"se",
					new UFDouble(parentVO.getHdef4() == null ? "0.00"
							: parentVO.getHdef4()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ ""));// 税额
		}
		if (null != parentVO.getHdef7()) {
			list.add(OaWorkFlowUtil.listAddObj(
					"sl",
					new UFDouble(parentVO.getHdef7() == null ? "0.00"
							: parentVO.getHdef7()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ ""));// 税率
		}
		if (null != parentVO.getHdef3()) {
			list.add(OaWorkFlowUtil.listAddObj(
					"bhsj",
					new UFDouble(parentVO.getHdef3() == null ? "0.00"
							: parentVO.getHdef3()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ ""));// 不含税价
		}
		// 开户银行
		if (null != parentVO.getHdef33()) {
			String khyh = (String) getHyPubBO().findColValue(
					"bd_asslinenum",
					"name",
					"nvl(dr,0) = 0 and pk_asslinenum = '"
							+ parentVO.getHdef33() + "'");
			list.add(OaWorkFlowUtil.listAddObj("khyh", khyh));
		}
		if (null != parentVO.getHdef35()) {
			list.add(OaWorkFlowUtil.listAddObj("zh", parentVO.getHdef35()));// 账号
		}
		if (null != parentVO.getHdef41()) {
			list.add(OaWorkFlowUtil.listAddObj("jzmj", parentVO.getHdef41()));// 建筑面积
		}
		if (null != parentVO.getHdef42()) {
			list.add(OaWorkFlowUtil.listAddObj("htqx", parentVO.getHdef42()));// 合同期限
		}

		if (null != parentVO.getHdef43()) {
			list.add(OaWorkFlowUtil.listAddObj("nd", parentVO.getHdef43()));// 年度
		}
		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				parentVO.getPk_org());
		list.add(OaWorkFlowUtil.listAddObj("szgs", orgVO.getName()));// 所属组织名称
		list.add(OaWorkFlowUtil.listAddObj("szgsbm", orgVO.getCode()));// 所属组织编码
		list.add(OaWorkFlowUtil.listAddObj("xmzz", orgVO.getName()));// 所属组织名称
		list.add(OaWorkFlowUtil.listAddObj("htbh", parentVO.getBill_code()));// 合同编码
		list.add(OaWorkFlowUtil.listAddObj("htmc", parentVO.getBill_name()));// 合同名称
		list.add(OaWorkFlowUtil.listAddObj("bbh", parentVO.getContr_version()
				+ ""));// 版本号
		if (null != parentVO.getPk_contracttype()) {
			list.add(OaWorkFlowUtil.listAddObj(
					"htlx",
					VOUtils.getDocName(ContractTypeHeadVO.class,
							parentVO.getPk_contracttype())));// 合同类型
		}
		if (parentVO.getHdef31() != null) {
			list.add(OaWorkFlowUtil.listAddObj("dkbmzj", parentVO.getHdef31()));// 代控部门主键
			String dkbmstr = (String) getHyPubBO().findColValue(
					"org_dept",
					"code",
					"nvl(dr,0) = 0 and pk_dept = '" + parentVO.getHdef31()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbmbm", dkbmstr));// 代控部门编码
			String dkbmname = (String) getHyPubBO().findColValue(
					"org_dept",
					"name",
					"nvl(dr,0) = 0 and pk_dept = '" + parentVO.getHdef31()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbm", dkbmname));// 代控部门名称
		}
		if (parentVO.getPk_wbs() != null) {
			list.add(OaWorkFlowUtil.listAddObj("xmrw",
					VOUtils.getDocName(WbsVO.class, parentVO.getPk_wbs())));// 项目任务
		}
		list.add(OaWorkFlowUtil.listAddObj("qyrq", parentVO.getPromisetime()
				.getYear()
				+ "-"
				+ parentVO.getPromisetime().getStrMonth()
				+ "-" + parentVO.getPromisetime().getStrDay()));// 签约日期
		if (null != parentVO.getPromiseplace()) {
			list.add(OaWorkFlowUtil.listAddObj("qydd",
					parentVO.getPromiseplace()));// 签约地点
			list.add(OaWorkFlowUtil.listAddObj("qydd",
					parentVO.getPromiseplace()));// 签约地点
		}
		if (parentVO.getHdef2() != null) {
			list.add(OaWorkFlowUtil.listAddObj("zrr", parentVO.getHdef2()));
		}
		if (parentVO.getPlan_validate_time() != null) {
			list.add(OaWorkFlowUtil.listAddObj("jhsxrq", parentVO
					.getPlan_validate_time().getYear()
					+ "-"
					+ parentVO.getPlan_validate_time().getStrMonth()
					+ "-"
					+ parentVO.getPlan_validate_time().getStrDay()));// 计划生效日期
		}
		if (parentVO.getPlan_teminate_time() != null) {
			list.add(OaWorkFlowUtil.listAddObj("jhzzrq", parentVO
					.getPlan_teminate_time().getYear()
					+ "-"
					+ parentVO.getPlan_teminate_time().getStrMonth()
					+ "-"
					+ parentVO.getPlan_teminate_time().getStrDay()));// 计划终止日期
		}
		if (parentVO.getActu_validate_time() != null) {
			list.add(OaWorkFlowUtil.listAddObj("sjsxrq", parentVO
					.getActu_validate_time().getYear()
					+ "-"
					+ parentVO.getActu_validate_time().getStrMonth()
					+ "-"
					+ parentVO.getActu_validate_time().getStrDay()));// 实际生效日期
		}
		if (null != parentVO.getServicebalrule()) {
			list.add(OaWorkFlowUtil.listAddObj("fwjsqj",
					parentVO.getServicebalrule() + ""));// 服务结算取价
		}
		if (null != parentVO.getServicebalrule()) {
			list.add(OaWorkFlowUtil
					.listAddObj(
							"htje",
							parentVO.getNcurrent_mny().setScale(2,
									UFDouble.ROUND_HALF_UP)
									+ ""));// 合同金额
		}
		// 原能投复制字段
		if (null != parentVO.getPrepay_prop()) {
			list.add(OaWorkFlowUtil.listAddObj("yfkbl", parentVO
					.getPrepay_prop().setScale(2, UFDouble.ROUND_HALF_UP) + ""));
		}

		if (null != parentVO.getQual_mny_prop()) {
			list.add(OaWorkFlowUtil.listAddObj("bzjbl",
					parentVO.getQual_mny_prop() + ""));
		}

		if (null != parentVO.getQual_mny_deadline()) {
			list.add(OaWorkFlowUtil.listAddObj("zbjdqrq",
					parentVO.getQual_mny_deadline() + ""));
		}

		UFDouble ljyfmon = parentVO.getNtot_sld_mny() == null ? new UFDouble(0)
				: parentVO.getNtot_sld_mny();
		list.add(OaWorkFlowUtil.listAddObj("ljyf",
				ljyfmon.setScale(2, UFDouble.ROUND_HALF_UP) + ""));

		UFDouble hxjemon = parentVO.getVerify_mny() == null ? new UFDouble(0)
				: parentVO.getVerify_mny();
		list.add(OaWorkFlowUtil.listAddObj("hxje",
				hxjemon.setScale(2, UFDouble.ROUND_HALF_UP) + ""));

		UFDouble ljsfmon = parentVO.getPay_mny() == null ? new UFDouble(0)
				: parentVO.getPay_mny();
		list.add(OaWorkFlowUtil.listAddObj("ljsf",
				ljsfmon.setScale(2, UFDouble.ROUND_HALF_UP) + ""));

		String jsztstr = "";
		if (parentVO.getBalaflag() == 1) {
			jsztstr = "未结算";
		} else if (parentVO.getBalaflag() == 2) {
			jsztstr = "结算中";
		} else if (parentVO.getBalaflag() == 3) {
			jsztstr = "已结算";
		}
		list.add(OaWorkFlowUtil.listAddObj("jszt", jsztstr + ""));
		list.add(OaWorkFlowUtil.listAddObj("qc", parentVO.getBegin_flag()
				.booleanValue() ? "是" : "否"));

		if (null != parentVO.getMemo()) {
			list.add(OaWorkFlowUtil.listAddObj("bz", parentVO.getMemo()));
		}

		// Map zdrzj =OaWorkFlowUtil.listAdd("zdrzj",parentVO.getBillmaker() );
		// list.add(zdrzj);

		if (null != userVO) {
			list.add(OaWorkFlowUtil.listAddObj("zdrzj", idCard));
			list.add(OaWorkFlowUtil.listAddObj("zdrmc", userVO.getUser_name()));
		}
		if (null != parentVO.getBillmaketime()) {
			list.add(OaWorkFlowUtil.listAddObj("zdrq", parentVO
					.getBillmaketime().getYear()
					+ "-"
					+ parentVO.getBillmaketime().getStrMonth()
					+ "-"
					+ parentVO.getBillmaketime().getStrDay()));
		}

		if (null != parentVO.getPk_transitype()) {
			list.add(OaWorkFlowUtil.listAddObj("jylxpk",
					parentVO.getPk_transitype()));
		}
		if (null != parentVO.getTransi_type()) {
			list.add(OaWorkFlowUtil.listAddObj("jylxbm",
					parentVO.getTransi_type()));
		}
		if (null != parentVO.getPk_transitype()) {
			String billtypename = (String) getHyPubBO().findColValue(
					"bd_billtype",
					"billtypename",
					"nvl(dr,0) = 0 and pk_billtypeid ='"
							+ parentVO.getPk_transitype() + "'");
			list.add(OaWorkFlowUtil.listAddObj("jylxmc", billtypename));
		}
		if (null != parentVO.getHdef1()) {
			list.add(OaWorkFlowUtil.listAddObj("zgldpk", parentVO.getHdef1()));
		}

		if (null != parentVO.getHdef21()) {
			String where = " pk_defdoc = '" + parentVO.getHdef21()
					+ "' and nvl(dr,0) = 0";
			String def21 = "0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					where)) {
				def21 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where);
			}
			list.add(OaWorkFlowUtil.listAddObj("sftsht", def21));
		}

		if (null != parentVO.getHdef22()) {
			String sfmbht = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getHdef22()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sfmbht", sfmbht));
		}

		if (null != parentVO.getHdef23()) {
			String zbfs = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getHdef23()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("zbfs", zbfs));
		}

		if (null != parentVO.getHdef24()) {
			String xmjl = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getHdef24()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("xmjl", xmjl));
		}

		// 合同原名称
		if (null != parentVO.getHdef32()) {
			String htymc = (String) getHyPubBO().findColValue(
					"bd_supplier",
					"name",
					"nvl(dr,0) = 0 and pk_supplier = '" + parentVO.getHdef32()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("htymc", htymc));
		}
		/* 审定值 */
		if (null != parentVO.getHdef28()) {
			list.add(OaWorkFlowUtil.listAddObj(
					"sdz",
					new UFDouble(parentVO.getHdef28() == null ? "0.00"
							: parentVO.getHdef28()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ ""));
		}
		// 合同金额/欠款
		if (null != parentVO.getNcurrent_mny()) {
			list.add(OaWorkFlowUtil
					.listAddObj(
							"htjeqk",
							parentVO.getNcurrent_mny().setScale(2,
									UFDouble.ROUND_HALF_UP)
									+ ""));
		}

		// 代控部门分管领导
		if (null != parentVO.getHdef1()) {
			String dkbmfgld = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getHdef1()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbmfgld", dkbmfgld));
			list.add(OaWorkFlowUtil.listAddObj("zgldmc", dkbmfgld));
		}

		// 发起方分管领导
		if (null != parentVO.getHdef33()) {
			String fqffgld = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getHdef33()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("fqffgld", fqffgld));
		}

		/* 原合同金额 */
		if (null != parentVO.getHdef26()) {
			list.add(OaWorkFlowUtil.listAddObj(
					"yhtje",
					new UFDouble(parentVO.getHdef26() == null ? "0.00"
							: parentVO.getHdef26()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ ""));
		}

		/*
		 * 合同折扣金额 if (null != parentVO.getHdef30()) {
		 * list.add(OaWorkFlowUtil.listAddObj( "htzkje", new
		 * UFDouble(parentVO.getHdef30() == null ? "0.00" :
		 * parentVO.getHdef30()).setScale(2, UFDouble.ROUND_HALF_UP) + "")); }
		 */

		/* 已付款金额 */
		if (null != parentVO.getHdef27()) {
			list.add(OaWorkFlowUtil.listAddObj(
					"yfkje",
					new UFDouble(parentVO.getHdef27() == null ? "0.00"
							: parentVO.getHdef27()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ ""));
		}

		/* 监理代表 */
		if (null != parentVO.getHdef29()) {
			list.add(OaWorkFlowUtil.listAddObj("jldb", parentVO.getHdef29()
					+ ""));
		}
		// 实施地点
		if (null != parentVO.getPromiseplace()) {
			list.add(OaWorkFlowUtil.listAddObj("ssdd",
					parentVO.getPromiseplace()));
		}
		// 工程规模
		if (null != parentVO.getHdef9()) {
			list.add(OaWorkFlowUtil.listAddObj("gcgm", parentVO.getHdef9()));
		}
		// 委托人代表
		if (null != parentVO.getHdef15()) {
			list.add(OaWorkFlowUtil.listAddObj("wtrdb", parentVO.getHdef15()));
		}
		// 工程概算投资额或建筑安装工程费
		if (null != parentVO.getHdef10()) {
			list.add(OaWorkFlowUtil.listAddObj("gcgstzehjzazgcf",
					parentVO.getHdef10()));
		}
		// 总监理工程师姓名
		if (null != parentVO.getHdef11()) {
			list.add(OaWorkFlowUtil.listAddObj("zjlgcsxm", parentVO.getHdef11()));
		}
		// 总监理工程师身份证号
		if (null != parentVO.getHdef12()) {
			list.add(OaWorkFlowUtil.listAddObj("zjlgcssfzh",
					parentVO.getHdef12()));
		}
		// 总监理工程师注册号
		if (null != parentVO.getHdef13()) {
			list.add(OaWorkFlowUtil.listAddObj("zjlgcszch",
					parentVO.getHdef13()));
		}
		// 监理费率
		if (null != parentVO.getHdef14()) {
			list.add(OaWorkFlowUtil.listAddObj("jlfl", parentVO.getHdef14()));
		}
		// ---------其他字段end

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	private JSONArray getDtaileDataMap(ContractBillVO temp)
			throws BusinessException {

		List dtlist = new ArrayList();
		Map contrWorksVOMap = getContrWorks(
				(ContrWorksVO[]) temp.getChildren(ContrWorksVO.class), temp
						.getParentVO().getTransi_type());
		if (null != contrWorksVOMap) {
			dtlist.add(contrWorksVOMap);
		}

		Map contrItemsMap = getContrItems(
				(ContrItemsVO[]) temp.getChildren(ContrItemsVO.class), temp
						.getParentVO().getTransi_type());
		if (null != contrItemsMap) {
			dtlist.add(contrItemsMap);
		}
		JSONArray dtlistString = JSONArray.fromObject(dtlist);
		return dtlistString;
	}

	private Map getContrItems(ContrItemsVO[] children, String type)
			throws BusinessException {
		if (null == children || children.length == 0) {
			return null;
		}
		Map dtMap = new HashMap();
		if (RL_001.equals(type)) {
			// dtMap.put("tableDBName", "formtable_main_69_dt2");
			dtMap.put("tableDBName", BODY_TABLE_NAME_JNRL_QD_dt2);
			if ("2".equals(getDef2(children[0].getPk_org()))) {
				dtMap.put("tableDBName", BODY_TABLE_NAME_JNNY_QD_dt2);
			}

		}
		if (RL_005.equals(type)) {
			// dtMap.put("tableDBName", "formtable_main_52_dt2");
			dtMap.put("tableDBName", BODY_TABLE_NAME_JNRL_SG_dt2);
			if ("2".equals(getDef2(children[0].getPk_org()))) {
				dtMap.put("tableDBName", BODY_TABLE_NAME_JNNY_SG_dt2);
			}
		}
		if (RL_004.equals(type)) {
			// dtMap.put("tableDBName", "formtable_main_70_dt2");
			dtMap.put("tableDBName", BODY_TABLE_NAME_JNRL_SJ_dt2);
			if ("2".equals(getDef2(children[0].getPk_org()))) {
				dtMap.put("tableDBName", BODY_TABLE_NAME_JNNY_SJ_dt2);
			}
		}
		if (RL_003.equals(type)) {
			// dtMap.put("tableDBName", "formtable_main_71_dt2");
			dtMap.put("tableDBName", BODY_TABLE_NAME_JNRL_JL_dt2);
			if ("2".equals(getDef2(children[0].getPk_org()))) {
				dtMap.put("tableDBName", BODY_TABLE_NAME_JNNY_JL_dt2);
			}
		}
		List workflowRequestTableRecords = new ArrayList();
		// Map workflowRequestTableFieldsMap = new HashMap();
		for (ContrItemsVO temp : children) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();
			String tkbmstr = (String) getHyPubBO().findColValue(
					"ct_termset",
					"vtermcode",
					"nvl(dr,0) = 0 and pk_ct_termset ='"
							+ temp.getPk_ct_termset() + "'");
			Map tkbm = OaWorkFlowUtil.listAdd("tkbm", tkbmstr);
			workflowRequestTableFields.add(tkbm);

			Map tkmc = OaWorkFlowUtil.listAdd("tkmc", temp.getVtermcontent());
			workflowRequestTableFields.add(tkmc);

			String tklxzj = (String) getHyPubBO().findColValue(
					"ct_termset",
					"pk_ct_termtype",
					"nvl(dr,0) = 0 and pk_ct_termset ='"
							+ temp.getPk_ct_termset() + "'");
			String tklxstr = (String) getHyPubBO().findColValue("ct_termtype",
					"termtypename",
					"nvl(dr,0) = 0 and pk_ct_termtype ='" + tklxzj + "'");
			Map tklx = OaWorkFlowUtil.listAdd("tklx", tklxstr);
			workflowRequestTableFields.add(tklx);

			Map tknr = OaWorkFlowUtil.listAdd("tknr", temp.getVtermcontent());
			workflowRequestTableFields.add(tknr);

			Map qtxx = OaWorkFlowUtil.listAdd("qtxx", temp.getOther_info());
			workflowRequestTableFields.add(qtxx);

			Map bz = OaWorkFlowUtil.listAdd("bz", temp.getMemo());
			workflowRequestTableFields.add(bz);
			if ("4D42-01".equals(type)) {
				// 乙方上报资料限期
				Map yfsbzlxq = OaWorkFlowUtil.listAdd("yfsbzlxq",
						temp.getBdef1());
				workflowRequestTableFields.add(yfsbzlxq);

				// 生产管理部验收限期
				Map scglbysxq = OaWorkFlowUtil.listAdd("scglbysxq",
						temp.getBdef2());
				workflowRequestTableFields.add(scglbysxq);
			}

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		// workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	private Map getContrWorks(ContrWorksVO[] children, String type)
			throws BusinessException {
		if (null == children || children.length == 0) {
			return null;
		}
		Map dtMap = new HashMap();
		if (RL_001.equals(type)) {
			// dtMap.put("tableDBName", "formtable_main_69_dt1");
			dtMap.put("tableDBName", BODY_TABLE_NAME_JNRL_QD_dt1);
			if ("2".equals(getDef2(children[0].getPk_org()))) {
				dtMap.put("tableDBName", BODY_TABLE_NAME_JNNY_QD_dt1);
			}
		}
		if (RL_005.equals(type)) {
			// dtMap.put("tableDBName", "formtable_main_52_dt1");
			dtMap.put("tableDBName", BODY_TABLE_NAME_JNRL_SG_dt1);
			if ("2".equals(getDef2(children[0].getPk_org()))) {
				dtMap.put("tableDBName", BODY_TABLE_NAME_JNNY_SG_dt1);
			}

		}
		if (RL_004.equals(type)) {
			// dtMap.put("tableDBName", "formtable_main_70_dt1");
			dtMap.put("tableDBName", BODY_TABLE_NAME_JNRL_SJ_dt1);
			if ("2".equals(getDef2(children[0].getPk_org()))) {
				dtMap.put("tableDBName", BODY_TABLE_NAME_JNNY_SJ_dt1);
			}
		}
		if (RL_003.equals(type)) {
			// dtMap.put("tableDBName", "formtable_main_71_dt1");
			dtMap.put("tableDBName", BODY_TABLE_NAME_JNRL_JL_dt1);
			if ("2".equals(getDef2(children[0].getPk_org()))) {
				dtMap.put("tableDBName", BODY_TABLE_NAME_JNNY_JL_dt1);
			}
		}
		List workflowRequestTableRecords = new ArrayList();
		// Map workflowRequestTableFieldsMap = new HashMap();
		for (ContrWorksVO temp : children) {
			List workflowRequestTableFields = new ArrayList();

			Map workflowRequestTableFieldsMap = new HashMap();

			// XBX20221223 新增税码税率等
			// 税码
			if (temp.getBdef6() != null) {
				String smstr = (String) getHyPubBO().findColValue(
						"bd_taxcode",
						"description",
						"nvl(dr,0) = 0 and pk_taxcode ='" + temp.getBdef6()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAdd("sm",
						smstr));
			}
			// 税率
			if (temp.getBdef7() != null) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAdd("bdef7",
						temp.getBdef7()));
			}
			// 无税金额
			if (temp.getBdef8() != null) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAdd("bdef8",
						temp.getBdef8()));
			}
			// 税额
			if (temp.getBdef9() != null) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAdd("bdef9",
						temp.getBdef9()));
			}
			// 无税单价
			if (temp.getBdef10() != null) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAdd("bdef10",
						temp.getBdef10()));
			}
			MaterialVO materialVO = (MaterialVO) getHyPubBO()
					.queryByPrimaryKey(MaterialVO.class,
							temp.getPk_material_v());
			if (null != materialVO) {
				Map mc = OaWorkFlowUtil.listAdd("mc", materialVO.getName());
				workflowRequestTableFields.add(mc);
				Map bm = OaWorkFlowUtil.listAdd("bm", materialVO.getCode());
				workflowRequestTableFields.add(bm);
			}

			Map dw = OaWorkFlowUtil.listAdd("dw",
					VOUtils.getDocName(MeasdocVO.class, temp.getPk_measdoc()));
			workflowRequestTableFields.add(dw);

			String s = "0";
			if (null != temp.getPk_wbs()) {
				s = VOUtils.getDocName(WbsVO.class, temp.getPk_wbs());
			}
			Map xmrw = OaWorkFlowUtil.listAdd("xmrw", s);
			workflowRequestTableFields.add(xmrw);

			Map cbs = OaWorkFlowUtil.listAdd("cbs",
					VOUtils.getDocName(CBSNodeVO.class, temp.getPk_cbsnode()));
			workflowRequestTableFields.add(cbs);

			String strdj = "0";
			if (null != temp.getNprom_price()) {
				strdj = temp.getNprom_price().setScale(2,
						UFDouble.ROUND_HALF_UP)
						+ "";
			}
			Map dj = OaWorkFlowUtil.listAdd("dj", strdj);
			workflowRequestTableFields.add(dj);

			Map fwjgxs = OaWorkFlowUtil.listAdd("fwjgxs",
					temp.getService_prc_ratio() + "");
			workflowRequestTableFields.add(fwjgxs);

			String strsl = "0";
			if (null != temp.getCurr_num()) {
				strsl = temp.getCurr_num().setScale(2, UFDouble.ROUND_HALF_UP)
						+ "";
			}
			Map sl = OaWorkFlowUtil.listAdd("sl", strsl);
			workflowRequestTableFields.add(sl);

			Map je = OaWorkFlowUtil
					.listAdd(
							"je",
							temp.getCurr_mny().setScale(2,
									UFDouble.ROUND_HALF_UP)
									+ "");
			workflowRequestTableFields.add(je);
			// 清单合同加不含税金额
			if (RL_001.equals(type)) {
				if ("1".equals(getDef2(children[0].getPk_org()))) {
					String bhsjemap = "0";
					if (null != temp.getBdef1()) {
						bhsjemap = temp.getBdef1() + "";
						Map bhsje = OaWorkFlowUtil.listAdd("bhsje", bhsjemap);
						workflowRequestTableFields.add(bhsje);
					}

				}
			}
			String str = "0";
			if (null != temp.getTot_comp_num()) {
				str = temp.getTot_comp_num() + "";
			}
			Map ljwcl = OaWorkFlowUtil.listAdd("ljwcl", str);
			workflowRequestTableFields.add(ljwcl);
			// 施工合同 热力新增
			if (RL_005.equals(type)) {
				if ("1".equals(getDef2(children[0].getPk_org()))) {
					// 战网名称
					Map zwmc = OaWorkFlowUtil.listAdd("zwmc", temp.getBdef11()
							+ "");
					workflowRequestTableFields.add(zwmc);
					// 维保内容
					Map wbnr = OaWorkFlowUtil.listAdd("wbnr", temp.getBdef12()
							+ "");
					workflowRequestTableFields.add(wbnr);
					// 备注
					Map bz = OaWorkFlowUtil
							.listAdd("bz", temp.getBdef13() + "");
					workflowRequestTableFields.add(bz);
					// 用热地址
					Map yrdz = OaWorkFlowUtil.listAdd("yrdz", temp.getBdef14()
							+ "");
					workflowRequestTableFields.add(yrdz);
					// 设备名称/机组型号
					Map sbmcjzxh = OaWorkFlowUtil.listAdd("sbmcjzxh",
							temp.getBdef15() + "");
					workflowRequestTableFields.add(sbmcjzxh);
				}
			}

			/*
			 * Map ljyf = OaWorkFlowUtil.listAdd("ljyf",
			 * temp.getNtot_sld_mny().setScale(2, UFDouble.ROUND_HALF_UP) +"");
			 * workflowRequestTableFields.add(ljyf);
			 */

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		// workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	private JSONArray getMainMap(ContrHeadVO parentVO, String bhsje)
			throws BusinessException {
		List list = new ArrayList();
		String transi_type = parentVO.getTransi_type();// 交易类型
		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				parentVO.getPk_org());
		// 所属组织名称
		Map sszzmc = OaWorkFlowUtil.listAdd("sszzmc", orgVO.getName());
		list.add(sszzmc);
		// 是否审计
		String sfsj = "否";
		if (parentVO.getHdef30() != null && "Y".equals(parentVO.getHdef30())) {
			sfsj = "是";
		}
		list.add(OaWorkFlowUtil.listAdd("sfsj", sfsj));
		// 清单合同
		if ("4D42-01".equals(parentVO.getTransi_type())) {
			if (bhsje != null) {
				Map bhsjemap = OaWorkFlowUtil.listAdd("bhsje", bhsje);
				list.add(bhsjemap);
			}
		}
		// OA合同模板 -- 施工合同
		if ("4D42-Cxx-05".equals(parentVO.getTransi_type())) {
			if (null != parentVO.getHdef52()) {
				list.add(OaWorkFlowUtil.listAddObj("gcdj", parentVO.getHdef52()));// 工程单价
			}
			if (null != parentVO.getHdef34()) {
				String where = " pk_defdoc = '" + parentVO.getHdef34()
						+ "' and nvl(dr,0) = 0";
				String htmbbm = "0";
				if (null != (String) getHyPubBO().findColValue("bd_defdoc",
						"code", where)) {
					htmbbm = (String) getHyPubBO().findColValue("bd_defdoc",
							"code", where);
					Map htmb = OaWorkFlowUtil.listAdd("htmb", htmbbm);
					list.add(htmb);
				}
			}
			Map qyszzmc = OaWorkFlowUtil.listAdd("qyszzmc", "济南热力集团有限公司");
			list.add(qyszzmc);

			String mbmc = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0)= 0 and pk_defdoc = '" + parentVO.getHdef34()
							+ "'");
			Map htmbmc = OaWorkFlowUtil.listAdd("htmb", mbmc);
			list.add(htmbmc);
			// 户表安装数量
			if (parentVO.getHdef50() != null) {
				list.add(OaWorkFlowUtil.listAdd("hbazsl", parentVO.getHdef50()));
			}
			// 创建人主键
			Map ctid = OaWorkFlowUtil.listAdd("ctid", parentVO.getPrimaryKey());
			list.add(ctid);

			if ("1".equals(getDef2(parentVO.getPk_org()))) {
				// 甲方签收人
				Map jfqsr = OaWorkFlowUtil.listAdd("jfqsr",
						parentVO.getHdef36());
				list.add(jfqsr);
				// 甲方联系电话
				Map jflxdh = OaWorkFlowUtil.listAdd("jflxdh",
						parentVO.getHdef37());
				list.add(jflxdh);
				// 乙方指定送达地址
				Map yfzdsddd = OaWorkFlowUtil.listAdd("yfzdsddd",
						parentVO.getHdef38());
				list.add(yfzdsddd);
				// 乙方签收人
				Map yfqsr = OaWorkFlowUtil.listAdd("yfqsr",
						parentVO.getHdef39());
				list.add(yfqsr);
				// 乙方联系电话
				Map yflxdh = OaWorkFlowUtil.listAdd("yflxdh",
						parentVO.getHdef40());
				list.add(yflxdh);
				// 契约锁对应流程id
				String qyshtmb = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"mnecode",
						"nvl(dr,0)= 0 and pk_defdoc = '" + parentVO.getHdef34()
								+ "'");
				if (qyshtmb == null || "".equals(qyshtmb) || qyshtmb == "")
					qyshtmb = "2936164539873645088";
				Map qysdylcid = OaWorkFlowUtil.listAdd("qyshtmb", qyshtmb);
				list.add(qysdylcid);
			}

			// 合同模板
			if (null != parentVO.getHdef34()) {
				String where = " pk_defdoc = '" + parentVO.getHdef34()
						+ "' and nvl(dr,0) = 0";
				String htmbbm = "0";
				if (null != (String) getHyPubBO().findColValue("bd_defdoc",
						"code", where)) {
					htmbbm = (String) getHyPubBO().findColValue("bd_defdoc",
							"code", where);
					Map htmb = OaWorkFlowUtil.listAdd("htmblx", htmbbm);
					list.add(htmb);
				}
			}

			// 工程地点
			Map gcdd = OaWorkFlowUtil.listAdd("gcdd",
					parentVO.getPromiseplace());
			list.add(gcdd);

			/* 甲供材 */
			Map jgc = OaWorkFlowUtil.listAdd(
					"jgc",
					new UFDouble(parentVO.getHdef5() == null ? "0.00"
							: parentVO.getHdef5()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ "");
			list.add(jgc);
			/* 安装费 */
			Map azf = OaWorkFlowUtil.listAdd(
					"azf",
					new UFDouble(parentVO.getHdef6() == null ? "0.00"
							: parentVO.getHdef6()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ "");
			list.add(azf);
			/* 承包施工方式 */
			Map cbsgfs = OaWorkFlowUtil.listAdd("cbsgfs", parentVO.getHdef8());
			list.add(cbsgfs);
			/* 承包范围和内容 */
			Map cbfwhnr = OaWorkFlowUtil.listAdd("cbfwhnr",
					parentVO.getHdef16());
			list.add(cbfwhnr);
			/* 工期日历天数 */
			Map gqrlts = OaWorkFlowUtil.listAdd("gqrlts", parentVO.getHdef17());
			list.add(gqrlts);

			/* 甲方代表 */
			Map jfdb = OaWorkFlowUtil.listAdd("jfdb", parentVO.getHdef18());
			list.add(jfdb);

			/* 乙方代表 */
			Map yfdb = OaWorkFlowUtil.listAdd("yfdb", parentVO.getHdef19());
			list.add(yfdb);

			/* 审计单位 */
			Map sjdw = OaWorkFlowUtil.listAdd("sjdw", parentVO.getHdef20());
			list.add(sjdw);

			/* 税额 */
			Map se = OaWorkFlowUtil.listAdd(
					"se",
					new UFDouble(parentVO.getHdef4() == null ? "0.00"
							: parentVO.getHdef4()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ "");
			list.add(se);
			/* 税率 */
			Map sl = OaWorkFlowUtil.listAdd(
					"sl",
					new UFDouble(parentVO.getHdef7() == null ? "0.00"
							: parentVO.getHdef7()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ "");
			list.add(sl);
			/* 不含税价 */
			Map bhsj = OaWorkFlowUtil.listAdd(
					"bhsj",
					new UFDouble(parentVO.getHdef3() == null ? "0.00"
							: parentVO.getHdef3()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ "");
			list.add(bhsj);

		}
		// 监理合同
		if ("4D42-Cxx-03".equals(parentVO.getTransi_type())) {
			if ("1".equals(getDef2(parentVO.getPk_org()))) {
				// 委托人签收人 =甲方签收人
				Map wtrqsr = OaWorkFlowUtil.listAdd("jfqsr",
						parentVO.getHdef36());
				list.add(wtrqsr);
				// 委托人电话=甲方签收人联系电话
				Map wtrdh = OaWorkFlowUtil.listAdd("jfqsrlxdh",
						parentVO.getHdef37());
				list.add(wtrdh);
				// 监理人指定送达地址=住所
				Map jlrzdsddd = OaWorkFlowUtil.listAdd("zs",
						parentVO.getHdef38());
				list.add(jlrzdsddd);
				// 监理人签收人
				Map jlrqsr = OaWorkFlowUtil.listAdd("jlrqsr",
						parentVO.getHdef39());
				list.add(jlrqsr);
				// 监理人联系电话
				Map jlrlxdh = OaWorkFlowUtil.listAdd("jlrlxdh",
						parentVO.getHdef40());
				list.add(jlrlxdh);

				// 工程地点
				Map gcdz = OaWorkFlowUtil.listAdd("gcdz",
						parentVO.getPromiseplace());
				list.add(gcdz);

				// 开户银行
				String khyh = (String) getHyPubBO().findColValue(
						"bd_asslinenum",
						"name",
						"nvl(dr,0) = 0 and pk_asslinenum = '"
								+ parentVO.getHdef33() + "'");
				Map khyhmap = OaWorkFlowUtil.listAdd("gcdz", khyh);
				list.add(khyhmap);

				// 账号
				Map zh = OaWorkFlowUtil.listAdd("zh", parentVO.getHdef35());
				list.add(zh);
				// 契约锁对应流程id
				String qyshtmb = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"mnecode",
						"nvl(dr,0)= 0 and pk_defdoc = '" + parentVO.getHdef34()
								+ "'");
				if (qyshtmb == null || "".equals(qyshtmb) || qyshtmb == "")
					qyshtmb = "2936164539873645088";
				Map qysdylcid = OaWorkFlowUtil.listAdd("qyshtmb", qyshtmb);
				list.add(qysdylcid);
			}
			// 合同模板
			if (null != parentVO.getHdef34()) {
				String where = " pk_defdoc = '" + parentVO.getHdef34()
						+ "' and nvl(dr,0) = 0";
				String htmbbm = "0";
				if (null != (String) getHyPubBO().findColValue("bd_defdoc",
						"code", where)) {
					htmbbm = (String) getHyPubBO().findColValue("bd_defdoc",
							"code", where);
					Map htmb = OaWorkFlowUtil.listAdd("htmblx", htmbbm);
					list.add(htmb);
				}
			}

		}
		// 设计、探伤类合同
		if ("4D42-Cxx-04".equals(parentVO.getTransi_type())) {
			// 其他事项
			// Map qtsx = OaWorkFlowUtil.listAdd("qtsx", parentVO.getHdef35());
			// list.add(qtsx);
			if ("1".equals(getDef2(parentVO.getPk_org()))) {
				// 甲方签收人
				Map jfqsr = OaWorkFlowUtil.listAdd("jfqsr",
						parentVO.getHdef36());
				list.add(jfqsr);
				// 甲方联系电话
				Map jflxdh = OaWorkFlowUtil.listAdd("jflxdh",
						parentVO.getHdef37());
				list.add(jflxdh);
				// 乙方指定送达地址
				Map yfzdsddd = OaWorkFlowUtil.listAdd("yfzdsddz",
						parentVO.getHdef38());
				list.add(yfzdsddd);
				// 乙方签收人
				Map yfqsr = OaWorkFlowUtil.listAdd("yfqsr",
						parentVO.getHdef39());
				list.add(yfqsr);
				// 乙方联系电话
				Map yflxdh = OaWorkFlowUtil.listAdd("yflxdh",
						parentVO.getHdef40());
				list.add(yflxdh);

				/*
				 * // 业务分类ID Map ywflid = OaWorkFlowUtil.listAdd("ywflid",
				 * "设计探伤合同"); list.add(ywflid);
				 * 
				 * // 契约锁合同模板 Map qyshtmb = OaWorkFlowUtil.listAdd("qyshtmb",
				 * "2902129048713720715"); list.add(qyshtmb);
				 */
				// 契约锁对应流程id
				String qyshtmb = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"mnecode",
						"nvl(dr,0)= 0 and pk_defdoc = '" + parentVO.getHdef34()
								+ "'");
				if (qyshtmb == null || "".equals(qyshtmb) || qyshtmb == "")
					qyshtmb = "2936164539873645088";
				Map qysdylcid = OaWorkFlowUtil.listAdd("qyshtmb", qyshtmb);
				list.add(qysdylcid);
			}
			// 合同模板
			if (null != parentVO.getHdef34()) {
				String where = " pk_defdoc = '" + parentVO.getHdef34()
						+ "' and nvl(dr,0) = 0";
				String htmbbm = "0";
				if (null != (String) getHyPubBO().findColValue("bd_defdoc",
						"code", where)) {
					htmbbm = (String) getHyPubBO().findColValue("bd_defdoc",
							"code", where);
					Map htmb = OaWorkFlowUtil.listAdd("htmblx", htmbbm);
					list.add(htmb);
				}
			}

		}
		// 建筑面积
		Map jzmj = OaWorkFlowUtil.listAdd("jzmj", parentVO.getHdef41());
		list.add(jzmj);
		// 合同期限
		Map htqx = OaWorkFlowUtil.listAdd("htqx", parentVO.getHdef42());
		list.add(htqx);
		// 年度
		Map nd = OaWorkFlowUtil.listAdd("nd", parentVO.getHdef43());
		list.add(nd);

		if (null != orgVO) {
			/* 组织 */
			Map szgs = OaWorkFlowUtil.listAdd("szgs", orgVO.getName());
			list.add(szgs);
			/* 组织编码 */
			Map szgsbm = OaWorkFlowUtil.listAdd("szgsbm", orgVO.getCode());
			list.add(szgsbm);
		}
		if (!"2".equals(getDef2(parentVO.getPk_org()))) {
			/* 项目组织 */
			Map xmzz = OaWorkFlowUtil.listAdd("xmzz", orgVO.getName());
			list.add(xmzz);
		}
		Map htbh = OaWorkFlowUtil.listAdd("htbh", parentVO.getBill_code());
		list.add(htbh);

		Map htmc = OaWorkFlowUtil.listAdd("htmc", parentVO.getBill_name());
		list.add(htmc);

		Map htzt = OaWorkFlowUtil.listAdd("htzt", parentVO.getBill_status()
				+ "");
		list.add(htzt);

		Map bbh = OaWorkFlowUtil.listAdd("bbh", parentVO.getContr_version()
				+ "");
		list.add(bbh);

		Map htlx = OaWorkFlowUtil.listAdd(
				"htlx",
				VOUtils.getDocName(ContractTypeHeadVO.class,
						parentVO.getPk_contracttype()));
		list.add(htlx);

		Map gys = OaWorkFlowUtil
				.listAdd(
						"gys",
						VOUtils.getDocName(SupplierVO.class,
								parentVO.getPk_supplier()));
		list.add(gys);

		String project_code = (String) getHyPubBO().findColValue(
				"bd_project",
				"project_code",
				"nvl(dr,0) = 0 and pk_project     = '"
						+ parentVO.getPk_project() + "'");
		Map xmbm = OaWorkFlowUtil.listAdd("xmbm", project_code);
		list.add(xmbm);

		String project_name = (String) getHyPubBO().findColValue(
				"bd_project",
				"project_name",
				"nvl(dr,0) = 0 and pk_project     = '"
						+ parentVO.getPk_project() + "'");
		Map xmmc = OaWorkFlowUtil.listAdd("xmmc", project_name);
		list.add(xmmc);

		// 代控部门编码
		String dkbmstr = (String) getHyPubBO().findColValue("org_dept", "code",
				"nvl(dr,0) = 0 and pk_dept = '" + parentVO.getHdef31() + "'");
		Map dkbmbm = OaWorkFlowUtil.listAdd("dkbmbm", dkbmstr);
		list.add(dkbmbm);

		// 代控部门
		String dkbmname = (String) getHyPubBO().findColValue("org_dept",
				"name",
				"nvl(dr,0) = 0 and pk_dept = '" + parentVO.getHdef31() + "'");
		Map dkbm = OaWorkFlowUtil.listAdd("dkbm", dkbmname);
		list.add(dkbm);

		// 代控部门主键
		Map dkbmzj = OaWorkFlowUtil.listAdd("dkbmzj", parentVO.getHdef31());
		list.add(dkbmzj);

		String s = "0";
		if (null != parentVO.getPk_wbs()) {
			// s = parentVO.getPk_wbs();
			s = VOUtils.getDocName(WbsVO.class, parentVO.getPk_wbs());
		}
		Map xmrw = OaWorkFlowUtil.listAdd("xmrw", s);
		list.add(xmrw);

		Map qyrq = OaWorkFlowUtil.listAdd("qyrq", parentVO.getPromisetime()
				.getYear()
				+ "-"
				+ parentVO.getPromisetime().getStrMonth()
				+ "-" + parentVO.getPromisetime().getStrDay());
		list.add(qyrq);

		String st = "0";
		if (null != parentVO.getPromiseplace()) {
			st = parentVO.getPromiseplace();
		}
		Map qydd = OaWorkFlowUtil.listAdd("qydd", st);
		list.add(qydd);

		Map qybm = OaWorkFlowUtil
				.listAdd(
						"qybm",
						VOUtils.getDocName(DeptVO.class,
								parentVO.getPk_promise_dept()));
		list.add(qybm);

		String str = "0";
		if (null != parentVO.getPk_promisepsn()) {
			str = VOUtils.getDocName(PsndocVO.class,
					parentVO.getPk_promisepsn());
		}
		Map qyr = OaWorkFlowUtil.listAdd("qyr", str);
		list.add(qyr);

		String string = "0";
		if (null != parentVO.getHdef2()) {
			string = parentVO.getHdef2();
		}
		Map zrr = OaWorkFlowUtil.listAdd("zrr", string);
		list.add(zrr);

		if (null != parentVO.getPlan_validate_time()) {
			String str12 = parentVO.getPlan_validate_time().getYear() + "-"
					+ parentVO.getPlan_validate_time().getStrMonth() + "-"
					+ parentVO.getPlan_validate_time().getStrDay();
			list.add(OaWorkFlowUtil.listAdd("jhsxrq", str12));
		}

		if (null != parentVO.getPlan_teminate_time()) {
			String strs = parentVO.getPlan_teminate_time().getYear() + "-"
					+ parentVO.getPlan_teminate_time().getStrMonth() + "-"
					+ parentVO.getPlan_teminate_time().getStrDay();
			list.add(OaWorkFlowUtil.listAdd("jhzzrq", strs));
		}

		if (null != parentVO.getActu_validate_time()) {
			String str1 = parentVO.getActu_validate_time().getYear() + "-"
					+ parentVO.getActu_validate_time().getStrMonth() + "-"
					+ parentVO.getActu_validate_time().getStrDay();
			list.add(OaWorkFlowUtil.listAdd("sjsxrq", str1));
		}

		Map fwjsqj = OaWorkFlowUtil.listAdd("fwjsqj",
				parentVO.getServicebalrule() + "");
		list.add(fwjsqj);

		Map htje = OaWorkFlowUtil.listAdd("htje", parentVO.getNcurrent_mny()
				.setScale(2, UFDouble.ROUND_HALF_UP) + "");
		list.add(htje);

		String str2 = "0";
		if (null != parentVO.getPrepay_prop()) {
			str2 = parentVO.getPrepay_prop()
					.setScale(2, UFDouble.ROUND_HALF_UP) + "";
		}
		Map yfkbl = OaWorkFlowUtil.listAdd("yfkbl", str2);
		list.add(yfkbl);

		/*
		 * Map jdkbl = OaWorkFlowUtil.listAdd("jdkbl",
		 * parentVO.getSche_mny_prop().setScale(2, UFDouble.ROUND_HALF_UP) +"");
		 * list.add(jdkbl);
		 */

		String str3 = "0";
		if (null != parentVO.getQual_mny_prop()) {
			str3 = parentVO.getQual_mny_prop() + "";
		}
		Map bzjbl = OaWorkFlowUtil.listAdd("bzjbl", str3);
		list.add(bzjbl);

		/*
		 * Map zbq = OaWorkFlowUtil.listAdd("zbq", parentVO.getQual_time() +"");
		 * list.add(zbq);
		 */

		String str4 = "0";
		if (null != parentVO.getQual_mny_deadline()) {
			str4 = parentVO.getQual_mny_deadline() + "";
		}
		Map zbjdqrq = OaWorkFlowUtil.listAdd("zbjdqrq", str4);
		list.add(zbjdqrq);

		/*
		 * Map yfk = OaWorkFlowUtil.listAdd("yfk",
		 * parentVO.getNprepay_mny().setScale(2, UFDouble.ROUND_HALF_UP) +"");
		 * list.add(yfk);
		 * 
		 * Map yfkljcx = OaWorkFlowUtil.listAdd("yfkljcx", parentVO
		 * .getNtot_expe_mny().setScale(2, UFDouble.ROUND_HALF_UP)+"");
		 * list.add(yfkljcx);
		 * 
		 * Map syyfk = OaWorkFlowUtil.listAdd("syyfk",
		 * parentVO.getNspare_mny().setScale(2, UFDouble.ROUND_HALF_UP) +"");
		 * list.add(syyfk);
		 */

		UFDouble ljyfmon = parentVO.getNtot_sld_mny() == null ? new UFDouble(0)
				: parentVO.getNtot_sld_mny();
		Map ljyf = OaWorkFlowUtil.listAdd("ljyf",
				ljyfmon.setScale(2, UFDouble.ROUND_HALF_UP) + "");
		list.add(ljyf);

		UFDouble hxjemon = parentVO.getVerify_mny() == null ? new UFDouble(0)
				: parentVO.getVerify_mny();
		Map hxje = OaWorkFlowUtil.listAdd("hxje",
				hxjemon.setScale(2, UFDouble.ROUND_HALF_UP) + "");
		list.add(hxje);

		UFDouble ljsfmon = parentVO.getPay_mny() == null ? new UFDouble(0)
				: parentVO.getPay_mny();
		Map ljsf = OaWorkFlowUtil.listAdd("ljsf",
				ljsfmon.setScale(2, UFDouble.ROUND_HALF_UP) + "");
		list.add(ljsf);

		String jsztstr = "";
		if (parentVO.getBalaflag() == 1) {
			jsztstr = "未结算";
		} else if (parentVO.getBalaflag() == 2) {
			jsztstr = "结算中";
		} else if (parentVO.getBalaflag() == 3) {
			jsztstr = "已结算";
		}
		/*
		 * Map jszt = OaWorkFlowUtil.listAdd("jszt", parentVO.getBalaflag()
		 * +"");
		 */
		Map jszt = OaWorkFlowUtil.listAdd("jszt", jsztstr + "");
		list.add(jszt);

		Map qc = OaWorkFlowUtil.listAdd("qc", parentVO.getBegin_flag()
				.booleanValue() ? "是" : "否");
		list.add(qc);

		String code = "0";
		if (null != parentVO.getMemo()) {
			code = parentVO.getMemo();
		}
		Map bz = OaWorkFlowUtil.listAdd("bz", code);
		list.add(bz);

		// Map zdrzj =OaWorkFlowUtil.listAdd("zdrzj",parentVO.getBillmaker() );
		// list.add(zdrzj);

		String user_code = (String) getHyPubBO().findColValue(
				"sm_user",
				"user_code",
				"nvl(dr,0) = 0 and   cuserid  ='" + parentVO.getBillmaker()
						+ "'");
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		if (null != userVO) {
			Map zdrmc = OaWorkFlowUtil.listAdd("zdrmc", userVO.getUser_name());
			list.add(zdrmc);
			Map zdrzj = new HashMap();
			zdrzj.put("fieldName", "zdrzj");
			String id = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"id",
					"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc()
							+ "'");
			if (null == id) {
				throw new BusinessException("制单人身份证号码未维护");
			}
			if ("2".equals(getDef2(parentVO.getPk_org()))) {

				zdrzj.put("fieldValue", id);
				list.add(zdrzj);
			} else {
				zdrzj.put("fieldValue", user_code);
				list.add(zdrzj);
			}

		}

		Map zdrq = OaWorkFlowUtil.listAdd("zdrq", parentVO.getBillmaketime()
				.getYear()
				+ "-"
				+ parentVO.getBillmaketime().getStrMonth()
				+ "-" + parentVO.getBillmaketime().getStrDay());
		list.add(zdrq);

		Map jylxpk = OaWorkFlowUtil.listAdd("jylxpk",
				parentVO.getPk_transitype());
		list.add(jylxpk);

		Map jylxbm = OaWorkFlowUtil
				.listAdd("jylxbm", parentVO.getTransi_type());
		list.add(jylxbm);

		String billtypename = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and   pk_billtypeid ='"
						+ parentVO.getPk_transitype() + "'");
		Map jylxmc = OaWorkFlowUtil.listAdd("jylxmc", billtypename);
		list.add(jylxmc);

		Map zgldpk = OaWorkFlowUtil.listAdd("zgldpk", parentVO.getHdef1());
		list.add(zgldpk);

		if (null != parentVO.getHdef1()) {
			String where = " pk_defdoc = '" + parentVO.getHdef1()
					+ "' and nvl(dr,0) = 0";
			String name = "0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					where)) {
				name = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where);
			}

			Map zgldmc = OaWorkFlowUtil.listAdd("zgldmc", name);
			list.add(zgldmc);
		}

		if (null != parentVO.getHdef21()) {
			String where = " pk_defdoc = '" + parentVO.getHdef21()
					+ "' and nvl(dr,0) = 0";
			String def21 = "0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					where)) {
				def21 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where);
			}

			Map sftsht = OaWorkFlowUtil.listAdd("sftsht", def21);
			list.add(sftsht);
		}

		if (null != parentVO.getHdef22()) {
			String where = " pk_defdoc = '" + parentVO.getHdef22()
					+ "'  and nvl(dr,0) = 0";
			String def22 = "0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					where)) {
				def22 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where);
			}

			Map sfmbht = OaWorkFlowUtil.listAdd("sfmbht", def22);
			list.add(sfmbht);
		}

		if (null != parentVO.getHdef23()) {
			String where = " pk_defdoc = '" + parentVO.getHdef23()
					+ "' and nvl(dr,0) = 0";
			String def23 = "0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					where)) {
				def23 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where);
			}

			Map zbfs = OaWorkFlowUtil.listAdd("zbfs", def23);
			list.add(zbfs);
		}

		if (null != parentVO.getHdef24()) {
			String where = " pk_defdoc = '" + parentVO.getHdef24()
					+ "' and nvl(dr,0) = 0";
			String def24 = "0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					where)) {
				def24 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						where);
			}

			Map xmjl = OaWorkFlowUtil.listAdd("xmjl", def24);
			list.add(xmjl);
		}

		// 合同原名称
		if (null != parentVO.getHdef32()) {
			String ymcwhere = " pk_supplier = '" + parentVO.getHdef32()
					+ "' and nvl(dr,0) = 0";
			String def32 = "0";
			if (null != (String) getHyPubBO().findColValue("bd_supplier",
					"name", ymcwhere)) {
				def32 = (String) getHyPubBO().findColValue("bd_supplier",
						"name", ymcwhere);
			}

			Map htymc = OaWorkFlowUtil.listAdd("htymc", def32);
			list.add(htymc);
		}
		/* 审定值 */
		Map sdz = OaWorkFlowUtil.listAdd(
				"sdz",
				new UFDouble(parentVO.getHdef28() == null ? "0.00" : parentVO
						.getHdef28()).setScale(2, UFDouble.ROUND_HALF_UP) + "");
		list.add(sdz);
		// 清单发包合同
		if ("4D42-01".equals(transi_type)) {
			// 合同金额/欠款
			Map htjeqk = OaWorkFlowUtil
					.listAdd(
							"htjeqk",
							parentVO.getNcurrent_mny().setScale(2,
									UFDouble.ROUND_HALF_UP)
									+ "");
			list.add(htjeqk);

			// 代控部门分管领导
			if (null != parentVO.getHdef1()) {
				String dkfgwhere = " pk_defdoc = '" + parentVO.getHdef1()
						+ "' and nvl(dr,0) = 0";
				String hdef1 = "0";
				if (null != (String) getHyPubBO().findColValue("bd_defdoc",
						"name", dkfgwhere)) {
					hdef1 = (String) getHyPubBO().findColValue("bd_defdoc",
							"name", dkfgwhere);
				}

				Map dkbmfgld = OaWorkFlowUtil.listAdd("dkbmfgld", hdef1);
				list.add(dkbmfgld);
			}

			// 发起方分管领导
			if (null != parentVO.getHdef1()) {
				String fqffgwhere = " pk_defdoc = '" + parentVO.getHdef1()
						+ "' and nvl(dr,0) = 0";
				String hdef33 = "0";
				if (null != (String) getHyPubBO().findColValue("bd_defdoc",
						"name", fqffgwhere)) {
					hdef33 = (String) getHyPubBO().findColValue("bd_defdoc",
							"name", fqffgwhere);
				}

				Map fqffgld = OaWorkFlowUtil.listAdd("fqffgld", hdef33);
				list.add(fqffgld);
			}

			/* 原合同金额 */
			Map yhtje = OaWorkFlowUtil.listAdd(
					"yhtje",
					new UFDouble(parentVO.getHdef26() == null ? "0.00"
							: parentVO.getHdef26()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ "");
			list.add(yhtje);

			/*
			 * 合同折扣金额 Map htzkje = OaWorkFlowUtil.listAdd( "htzkje", new
			 * UFDouble(parentVO.getHdef30() == null ? "0.00" :
			 * parentVO.getHdef30()).setScale(2, UFDouble.ROUND_HALF_UP) + "");
			 * list.add(htzkje);
			 */

			/* 已付款金额 */
			Map yfkje = OaWorkFlowUtil.listAdd(
					"yfkje",
					new UFDouble(parentVO.getHdef27() == null ? "0.00"
							: parentVO.getHdef27()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ "");
			list.add(yfkje);

			/* 监理代表 */
			Map zlj = OaWorkFlowUtil.listAdd("jldb", parentVO.getHdef29() + "");
			list.add(zlj);
		} else if ("4D42-Cxx-05".equals(transi_type)) { // 施工合同
			// 工程地点
			Map gcdd = OaWorkFlowUtil.listAdd("gcdd",
					parentVO.getPromiseplace());
			list.add(gcdd);

			/* 甲供材 */
			Map jgc = OaWorkFlowUtil.listAdd(
					"jgc",
					new UFDouble(parentVO.getHdef5() == null ? "0.00"
							: parentVO.getHdef5()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ "");
			list.add(jgc);
			/* 安装费 */
			Map azf = OaWorkFlowUtil.listAdd(
					"azf",
					new UFDouble(parentVO.getHdef6() == null ? "0.00"
							: parentVO.getHdef6()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ "");
			list.add(azf);
			/* 承包施工方式 */
			Map cbsgfs = OaWorkFlowUtil.listAdd("cbsgfs", parentVO.getHdef8());
			list.add(cbsgfs);
			/* 承包范围和内容 */
			Map cbfwhnr = OaWorkFlowUtil.listAdd("cbfwhnr",
					parentVO.getHdef16());
			list.add(cbfwhnr);
			/* 工期日历天数 */
			Map gqrlts = OaWorkFlowUtil.listAdd("gqrlts", parentVO.getHdef17());
			list.add(gqrlts);

			/* 甲方代表 */
			Map jfdb = OaWorkFlowUtil.listAdd("jfdb", parentVO.getHdef18());
			list.add(jfdb);

			/* 乙方代表 */
			Map yfdb = OaWorkFlowUtil.listAdd("yfdb", parentVO.getHdef19());
			list.add(yfdb);

			/* 审计单位 */
			Map sjdw = OaWorkFlowUtil.listAdd("sjdw", parentVO.getHdef20());
			list.add(sjdw);

			/* 税额 */
			Map se = OaWorkFlowUtil.listAdd(
					"se",
					new UFDouble(parentVO.getHdef4() == null ? "0.00"
							: parentVO.getHdef4()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ "");
			list.add(se);
			/* 税率 */
			Map sl = OaWorkFlowUtil.listAdd(
					"sl",
					new UFDouble(parentVO.getHdef7() == null ? "0.00"
							: parentVO.getHdef7()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ "");
			list.add(sl);
			/* 不含税价 */
			Map bhsj = OaWorkFlowUtil.listAdd(
					"bhsj",
					new UFDouble(parentVO.getHdef3() == null ? "0.00"
							: parentVO.getHdef3()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ "");
			list.add(bhsj);
		} else if ("4D42-Cxx-04".equals(transi_type)) {// 设计、探伤类合同
			/* 不含税价 */
			Map bhsj = OaWorkFlowUtil.listAdd(
					"bhsj",
					new UFDouble(parentVO.getHdef3() == null ? "0.00"
							: parentVO.getHdef3()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ "");
			list.add(bhsj);
			// 实施地点
			Map ssdd = OaWorkFlowUtil.listAdd("ssdd",
					parentVO.getPromiseplace());
			list.add(ssdd);
			/* 税额 */
			Map se = OaWorkFlowUtil.listAdd(
					"se",
					new UFDouble(parentVO.getHdef4() == null ? "0.00"
							: parentVO.getHdef4()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ "");
			list.add(se);
			/* 税率 */
			Map sl = OaWorkFlowUtil.listAdd(
					"sl",
					new UFDouble(parentVO.getHdef7() == null ? "0.00"
							: parentVO.getHdef7()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ "");
			list.add(sl);
		} else if ("4D42-Cxx-03".equals(transi_type)) { // 监理合同
			// 工程地点
			Map gcdd = OaWorkFlowUtil.listAdd("gcdd",
					parentVO.getPromiseplace());
			list.add(gcdd);
			// 工程规模
			Map gcgm = OaWorkFlowUtil.listAdd("gcgm", parentVO.getHdef9());
			list.add(gcgm);
			// 委托人代表
			Map wtrdb = OaWorkFlowUtil.listAdd("wtrdb", parentVO.getHdef15());
			list.add(wtrdb);
			// 工程概算投资额或建筑安装工程费
			Map gcgstzehjzazgcf = OaWorkFlowUtil.listAdd("gcgstzehjzazgcf",
					parentVO.getHdef10());
			list.add(gcgstzehjzazgcf);
			// 总监理工程师姓名
			Map zjlgcsxm = OaWorkFlowUtil.listAdd("zjlgcsxm",
					parentVO.getHdef11());
			list.add(zjlgcsxm);
			// 总监理工程师身份证号
			Map zjlgcssfzh = OaWorkFlowUtil.listAdd("zjlgcssfzh",
					parentVO.getHdef12());
			list.add(zjlgcssfzh);
			// 总监理工程师注册号
			Map zjlgcszch = OaWorkFlowUtil.listAdd("zjlgcszch",
					parentVO.getHdef13());
			list.add(zjlgcszch);
			// 监理费率
			Map jlfl = OaWorkFlowUtil.listAdd("jlfl", parentVO.getHdef14());
			list.add(jlfl);
			/* 税额 */
			Map se = OaWorkFlowUtil.listAdd(
					"se",
					new UFDouble(parentVO.getHdef4() == null ? "0.00"
							: parentVO.getHdef4()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ "");
			list.add(se);
			/* 税率 */
			Map sl = OaWorkFlowUtil.listAdd(
					"sl",
					new UFDouble(parentVO.getHdef7() == null ? "0.00"
							: parentVO.getHdef7()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ "");
			list.add(sl);
			/* 不含税价 */
			Map bhsj = OaWorkFlowUtil.listAdd(
					"bhsj",
					new UFDouble(parentVO.getHdef3() == null ? "0.00"
							: parentVO.getHdef3()).setScale(2,
							UFDouble.ROUND_HALF_UP)
							+ "");
			list.add(bhsj);
		}

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	public ContractBillVO[] deleteContr(ContractBillVO[] billVOs)
			throws BusinessException {
		return (ContractBillVO[]) delete(billVOs);
	}

	public ContractBillVO[] freeContr(ContractBillVO[] billVOs,
			ContractBillVO[] originBillVOs) throws BusinessException {
		OtherButtonUpdateAction<ContractBillVO> action = createUpdateWithoutEventAction(
				"1225", "1226");

		initFreeAction(action);
		return (ContractBillVO[]) action.processAction(billVOs, originBillVOs);
	}

	public void initAlterAction(UpdateAction<ContractBillVO> action,
			ContractBillVO[] originBillVOs) {
		action.addBeforeRule(new AlterCheckRefRule());

		action.addBeforeRule(new AlterDateCheckRule());

		action.addBeforeRule(new AlterValidateRule());

		action.addBeforeRule(new CreateOldVersionRule(originBillVOs));

		action.addAfterRule(new BillAlter4BudgetRule(0));
	}

	public void initFreeAction(UpdateAction<ContractBillVO> action) {
		action.addBeforeRule(new UpdateBillSatusRule(10));
	}

	public void initTerminateAction(UpdateAction<ContractBillVO> action) {
		action.addBeforeRule(new DateCheckRule());

		action.addBeforeRule(new TerminateBeforeRule());

		action.addBeforeRule(new BillTerminate4BudgetRule(0));

		action.addBeforeRule(new UpdateBillSatusRule(11));
	}

	public void initUnFrozenAction(UpdateAction<ContractBillVO> action) {
		action.addBeforeRule(new UpdateBillSatusRule(9));
	}

	public void initUnValidateAction(UpdateAction<ContractBillVO> action) {
		action.addBeforeRule(new UpdateBillSatusRule(1));

		action.addBeforeRule(new BillQuoteCheckRule());
		action.addBeforeRule(new BillCodeQuoteCheckRule());
	}

	public void initValidateAction(UpdateAction<ContractBillVO> action) {
		action.addBeforeRule(new UpdateBillSatusRule(9));
	}

	public ContractBillVO[] insertContr(ContractBillVO[] billVOs)
			throws BusinessException {
		// 测试临时屏蔽校验，正式需要放开

		for (ContractBillVO billVO : billVOs) {
			try {
				ContrHeadVO hvos = beforecheck(billVO);// 新增保存前校验
				if (hvos != null) {
					billVO.setParentVO(hvos);
				}
			} catch (BusinessException e) {
				// TODO: handle exception
				throw new BusinessException(e.getMessage());
			}

		}
		return (ContractBillVO[]) insert(billVOs);
		// return (ContractBillVO[]) insert(vos);
	}

	public ContractBillVO[] terminateContr(ContractBillVO[] billVOs,
			ContractBillVO[] originBillVOs) throws BusinessException {
		OtherButtonUpdateAction<ContractBillVO> action = createUpdateWithoutEventAction(
				"1229", "1230");

		initTerminateAction(action);
		return (ContractBillVO[]) action.processAction(billVOs, originBillVOs);
	}

	public ContractBillVO[] unapproveContr(ContractBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		return (ContractBillVO[]) unApprove(billVOs, pfParamVO);
	}

	public ContractBillVO[] unCommitContr(ContractBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		ContractBillVO[] aggVO = (ContractBillVO[]) unCommit(billVOs, pfParamVO);
		unOaCommit(aggVO);
		return aggVO;
	}

	private void unOaCommit(ContractBillVO[] aggVO) throws BusinessException {
		for (ContractBillVO temp : aggVO) {
			OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
					temp.getParentVO().getPk_org());
			if (null != orgVO.getDef2()) {
				if ((temp.getParentVO().getTransi_type()).contains("4D42")) {
					OaWorkFlowUtil.backOaWorkFlow(temp.getPrimaryKey());
				}
			}
		}
	}

	public ContractBillVO[] unForzenContr(ContractBillVO[] billVOs,
			ContractBillVO[] originBillVOs) throws BusinessException {
		OtherButtonUpdateAction<ContractBillVO> action = createUpdateWithoutEventAction(
				"1227", "1228");

		initUnFrozenAction(action);
		return (ContractBillVO[]) action.processAction(billVOs, originBillVOs);
	}

	public ContractBillVO[] unValidateContr(ContractBillVO[] billVOs,
			ContractBillVO[] originBillVOs) throws BusinessException {
		OtherButtonUpdateAction<ContractBillVO> action = createUpdateWithoutEventAction(
				"1223", "1224");

		initUnValidateAction(action);
		return (ContractBillVO[]) action.processAction(billVOs, originBillVOs);
	}

	public ContractBillVO[] updateContr(ContractBillVO[] billVOs,
			ContractBillVO[] originBillVOs) throws BusinessException {
		ContrHeadVO hvos = beforecheck(billVOs[0]);// 新增保存前校验
		if (hvos != null) {
			billVOs[0].setParentVO(hvos);
			billVOs[0].getParentVO().setStatus(VOStatus.UPDATED);
		}
		return (ContractBillVO[]) update(billVOs, originBillVOs);
	}

	public ContractBillVO[] validateContr(ContractBillVO[] billVOs,
			ContractBillVO[] originBillVOs) throws BusinessException {
		OtherButtonUpdateAction<ContractBillVO> action = createUpdateWithoutEventAction(
				"1221", "1222");

		initValidateAction(action);
		return (ContractBillVO[]) action.processAction(billVOs, originBillVOs);
	}

	protected void initApproveAction(ApproveAction<ContractBillVO> action) {
		super.initApproveAction(action);

		IRule<ContractBillVO> rule1 = new SupplyProjectInfoBeforeRule();
		action.addAfterRule(rule1);

		action.addAfterRule(new BillSaveOrApprove4BudgetRule(1));
	}

	protected void initDeleteAction(DeleteAction<ContractBillVO> action) {
		super.initDeleteAction(action);

		action.addBeforeRule(new BillDelOrUnApprove4BudgetRule(0));

		action.addAfterRule(new DeleteHisVersionRule());
	}

	protected void initInsertAction(InsertAction<ContractBillVO> action) {
		super.initInsertAction(action);

		action.addBeforeRule(new AppendBusiTypeBeforeRule());
		action.addBeforeRule(new BeginFlagCheckRule());

		action.addBeforeRule(new ContrInsertRule());

		action.addAfterRule(new BillSaveOrApprove4BudgetRule(0));
	}

	protected void initUnApproveAction(UnApproveAction<ContractBillVO> action) {
		super.initUnApproveAction(action);

		action.addBeforeRule(new BillDelOrUnApprove4BudgetRule(1));
	}

	protected void initUpdateAction(UpdateAction<ContractBillVO> action) {
		super.initUpdateAction(action);
		action.addBeforeRule(new BeginFlagCheckRule());

		action.addBeforeRule(new ContrInsertRule());
		action.addBeforeRule(new BeginFlagCheckRule());

		action.addBeforeRule(new BillUpdate4BudgetBeforeRule(0));

		action.addAfterRule(new BillUpdate4BudgetAfterRule(0));
	}

	// 能投 施工合同保存时判断是否有设计合同，按项目类型 热源类建设项目，一次管网项目、基建类项目、小配套
	private void checkSGCont(ContractBillVO billVO) throws BusinessException {
		// TODO Auto-generated method stub
		// 施工合同校验 合同类型控制，在（）档案下的合同类型需要加判断
		// 校验是否能投或下属子公司
		int flag = 0;
		String pk_org = billVO.getParentVO().getPk_org();
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		if ("0001A21000000003U6L6".equals(pk_org)) {
			flag = 1;
		}
		// if (flag == 0 && StringUtils.isNotEmpty(orgVO.getPk_fatherorg())) {
		// OrgVO fatherorgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(
		// OrgVO.class, pk_org);// 上级组织
		// if (StringUtils.isNotEmpty(fatherorgVO.getPk_fatherorg())
		// && "0001A21000000003U6L6".equals(fatherorgVO
		// .getPk_fatherorg())) {
		// flag = 1;
		// }
		// }
		// 判断该项目类型是否需要校验
		if (flag == 1) {
			ContrHeadVO headVO = (ContrHeadVO) billVO.getParentVO();
			String ifbl = headVO.getHdef25();// 是否补录
			String pk_project = headVO.getPk_project();// 项目主键
			IProjectQuery xmiq = (IProjectQuery) NCLocator.getInstance()
					.lookup(IProjectQuery.class);
			ProjectBillVO[] ss = xmiq
					.queryProjectHeadVOsBypks(new String[] { pk_project });
			ProjectHeadVO projectHeadVO = ss[0].getParentVO();
			if (projectHeadVO != null) {
				String project_class = projectHeadVO.getPk_projectclass();
				String xmlx = (String) getHyPubBO().findColValue("sys_config",
						"config_value", "nvl(dr,0) = 0 and config_id ='68'");
				if (xmlx != null && xmlx.indexOf(project_class) != -1) {
					flag = 1;
				} else {
					flag = 0;
				}
			}
			if (flag == 1) {
				// 合同类型
				String billTypeCode = (String) getHyPubBO().findColValue(
						"pm_contracttype",
						"type_code",
						"nvl(dr,0) = 0 and pk_contracttype  = '"
								+ billVO.getParentVO().getPk_contracttype()
								+ "'");
				// 若同一个项目无02001合同类型则涉及到到0301，0302，0309，0310，0312,0313，0314,0315此合同类型的单据组织单据保存，并弹出窗口
				Object conforg = (Object) getHyPubBO().findColValue(
						"bd_defdoc",
						"code",
						"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
								+ "FROM BD_DEFDOCLIST WHERE CODE = 'NTHTLX')"
								+ " and code = '" + billTypeCode + "'");
				if (conforg != null) {
					// 校验是否有设计合同
					String pk_contracttype = (String) getHyPubBO()
							.findColValue("pm_contracttype", "pk_contracttype",
									"nvl(dr,0) = 0 and type_code = '02001'");
					// 查询清单发包合同
					String strWhere = " nvl(dr,0) = 0 and bill_status = 9 and last_v_flag = 'Y' and pk_contracttype = '"
							+ pk_contracttype
							+ "' and pk_project = '"
							+ pk_project + "'";
					ContrHeadVO[] hvos = (ContrHeadVO[]) getHyPubBO()
							.queryByCondition(ContrHeadVO.class, strWhere);
					// 查询多编码合同
					int dbmflag = 1;
					StringBuffer dbmmxsql = new StringBuffer(
							"nvl(dr,0) = 0 and pk_project = '" + pk_project
									+ "'");
					PmFeebalanceBVO[] childvos = (PmFeebalanceBVO[]) getHyPubBO()
							.queryByCondition(PmFeebalanceBVO.class,
									dbmmxsql.toString());
					if (childvos != null && childvos.length > 0) {
						for (PmFeebalanceBVO pmFeebalanceBVO : childvos) {
							AggPmFeebalance[] aggvoS = (AggPmFeebalance[]) NCLocator
									.getInstance()
									.lookup(IPmFeebalanceCtMaintain.class)
									.queryObjectByPks(
											new String[] { pmFeebalanceBVO
													.getPm_feebalance() });
							PmFeebalanceHVO pmFeebalanceHVO = aggvoS[0]
									.getParent();
							if (pk_contracttype.equals(pmFeebalanceHVO
									.getContracttype())
									&& pmFeebalanceHVO.getFstatusflag() == 1) {
								flag = 0;
							}
						}
					}
					if ((hvos == null || hvos.length <= 0) && (flag != 0)) {
						throw new BusinessException("请先新增设计合同！");
					}
					/*
					 * String selcfsql =
					 * "SELECT COUNT(PK_CONTR) AS SL FROM PM_CONTR WHERE PK_PROJECT = '"
					 * + pk_project +
					 * "' AND last_v_flag = 'Y' AND DR = 0 AND pk_contracttype = '"
					 * +pk_contracttype+"'"; List<Object[]> xmxzls =
					 * getDao.query(selcfsql); int yysl =
					 * Integer.parseInt(xmxzls.get(0)[0] + "");// 数量
					 * System.out.println("项目验证探伤合同sql：" + selcfsql);
					 * Logger.error("项目验证探伤合同sql：" + selcfsql); if (yysl <= 0) {
					 * throw new BusinessException("请先新增设计合同！"); }
					 */
				}
			}
		}
	}

	// 校验热力组织
	public void checkRLOrg(ContractBillVO billVO) throws BusinessException {
		String pk_org = billVO.getParentVO().getPk_org();
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		String pk_fatherorg = orgVO.getPk_fatherorg();
		String ifbl = billVO.getParentVO().getHdef25();// 是否补录
		ICBSNodeQueryService cbsiq = (ICBSNodeQueryService) NCLocator
				.getInstance().lookup(ICBSNodeQueryService.class);
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		ContrHeadVO headVO = (ContrHeadVO) billVO.getParentVO();
		ContrWorksVO[] conWorkVO = (ContrWorksVO[]) billVO.getChildrenVO();
		String pk_contr = headVO.getPrimaryKey();// 单据主键
		String pk_project = headVO.getPk_project();// 项目主键
		String pk_supplier = headVO.getPk_supplier();// 供应商主键
		String pk_contracttype = headVO.getPk_contracttype();// 合同类型 ---新增
		Logger.error("单据PK：" + headVO.getPrimaryKey());
		String type_name = "";// 项目类型名称
		IProjectQuery xmiq = (IProjectQuery) NCLocator.getInstance().lookup(
				IProjectQuery.class);
		ProjectBillVO[] ss = xmiq
				.queryProjectHeadVOsBypks(new String[] { pk_project });
		ProjectHeadVO projectHeadVO = ss[0].getParentVO();
		if (projectHeadVO != null) {
			IProjectTypeQueryService xmtpiq = (IProjectTypeQueryService) NCLocator
					.getInstance().lookup(IProjectTypeQueryService.class);
			ProjectTypeHeadVO lxVO = xmtpiq
					.queryProjectTypeHeadVOsByHeadPK(projectHeadVO
							.getPk_projectclass());// 项目类型VO
			if (lxVO != null) {
				type_name = lxVO.getType_name() + "";// 项目类型名称
			}
		}
		String selcfsql = "SELECT COUNT(PK_CONTR) AS SL FROM PM_CONTR WHERE PK_PROJECT = '"
				+ pk_project
				+ "' AND PK_CONTRACTTYPE = "
				+ "'"
				+ pk_contracttype
				+ "' AND PK_SUPPLIER = '"
				+ pk_supplier
				+ "' AND DR = 0 AND PK_CONTR <> '"
				+ headVO.getPrimaryKey()
				+ "'";
		List<Object[]> xmxzls = getDao.query(selcfsql);
		int yysl = Integer.parseInt(xmxzls.get(0)[0] + "");// 数量
		Logger.error("项目+类型+供应商验证sql：" + selcfsql);

		/*
		 * if
		 * (!"1001A2100000000D1ZL8".equals(projectHeadVO.getPk_projectclass())
		 * && yysl > 0 && !"1001A21000000005NM9B".equals(projectHeadVO
		 * .getPk_projectclass())) { throw new
		 * BusinessException("同类合同系统已存在，不允许重复签订！"); }
		 */

		// System.out.println("项目类型："+type_name);
		String sqltp = "SELECT NAME FROM RL_CBSNAME WHERE NAME LIKE '%"
				+ type_name + "%' AND TYPE = 'QDFBHT'";
		System.out.println("sqltp===" + sqltp);
		List<Object[]> ls = getDao.query(sqltp);

		for (int i = 0; i < conWorkVO.length; i++) {
			ContrWorksVO itemvo = conWorkVO[i];
			String pk_cbsnode = itemvo.getPk_cbsnode();// CBS主键
			String pk_cbsnode_name = "";// CBS名称

			if (itemvo.getPk_cbsnode() != null) {
				CBSNodeVO cbsVO = cbsiq.qryCbsNodeByPK(itemvo.getPk_cbsnode());
				pk_cbsnode_name = cbsVO.getName();
			}
			if (ls != null && ls.size() > 0) {
				// ----------------------1是否完成招标结果登记----------------------
				// 查询招标结果登记
				String sql = "SELECT DISTINCT A.PK_FEEBALANCE_B,B.PK_FEEBALANCE FROM PM_FEEBALANCE_B A LEFT JOIN PM_FEEBALANCE B ON A.PK_FEEBALANCE = "
						+ "B.PK_FEEBALANCE WHERE B.DR = 0 AND A.DR = 0 AND B.BILL_STATUS = 1 AND "
						+ "B.TRANSI_TYPE = '4D83-Cxx-04' AND B.PK_GROUP = '0001A1100000000001QS' AND A.DEF1 = '"
						+ pk_supplier
						+ "' AND "
						+ "A.PK_PROJECT = '"
						+ pk_project
						+ "' AND A.PK_CBSNODE = '"
						+ pk_cbsnode
						+ "'";
				List<Object[]> zbjgdjls = getDao.query(sql);
				if (zbjgdjls.size() == 0) {
					if (("0001A21000000003U6L6".equals(pk_org) || "0001A21000000003U6L6"
							.equals(pk_fatherorg))
							&& (ifbl == null || "1001A2100000000B68C3"
									.equals(ifbl))) {
						throw new BusinessException("第" + (i + 1)
								+ "行未完成招标结果登记！");
					}
				}
				// 查询清单发包合同已有数量
				String selqdsql = "SELECT DISTINCT A.PK_CONTR_WORKS,B.PK_CONTR FROM PM_CONTR_WORKS A LEFT JOIN PM_CONTR B ON A.PK_CONTR = B.PK_CONTR "
						+ "WHERE B.DR = 0 AND B.PK_PROJECT = '"
						+ pk_project
						+ "' AND B.PK_SUPPLIER = '"
						+ pk_supplier
						+ "' AND A.PK_CBSNODE = '"
						+ pk_cbsnode
						+ "' AND"
						+ " B.PK_CONTR <> '" + pk_contr + "'";
				// 查询多编码合同已有数量
				String seldbmsql = "SELECT DISTINCT A.PK_FEEBALANCE_B,B.PK_FEEBALANCE FROM PM_FEEBALANCE_B A LEFT JOIN PM_FEEBALANCE B ON A.PK_FEEBALANCE = B.PK_FEEBALANCE WHERE B.DR = 0 AND 	A.PK_PROJECT = '"
						+ pk_project
						+ "'"
						+ " AND B.PK_SUPPLIER = '"
						+ pk_supplier
						+ "' AND A.PK_CBSNODE = '"
						+ pk_cbsnode
						+ "'";
				int zbnum = zbjgdjls.size();
				List<Object[]> qdls = getDao.query(selqdsql);
				List<Object[]> dbmls = getDao.query(seldbmsql);
				if (qdls.size() + dbmls.size() >= zbnum) {
					if (("0001A21000000003U6L6".equals(pk_org) || "0001A21000000003U6L6"
							.equals(pk_fatherorg))
							&& (ifbl == null || "1001A2100000000B68C3"
									.equals(ifbl))) {
						throw new BusinessException("第" + (i + 1)
								+ "行超出招标结果登记数量限制，总数为" + zbnum + "!");
					}
				}
				// ----------------------1是否完成招标结果登记判断结束----------------------
				/*
				 * if ("0001A110000000000HYQ".equals(billVO.getParentVO()
				 * .getPk_org()) ||
				 * "0001A21000000000IYIU".equals(billVO.getParentVO()
				 * .getPk_org()) ||
				 * "0001A21000000000YHPX".equals(billVO.getParentVO()
				 * .getPk_org())) { //
				 * ----------------------2是否完成预算录入---------------------- String
				 * ysql =
				 * "SELECT DISTINCT A.PK_BUDGETCBS,B.PK_BUDGET FROM PM_BUDGETCBS A LEFT JOIN  PM_BUDGET B ON A.PK_BUDGET = B.PK_BUDGET WHERE "
				 * +
				 * "B.DR = 0 AND B.PK_GROUP = '0001A1100000000001QS' AND B.LAST_V_FLAG = 'Y' AND B.BILL_STATUS = '9' AND B.TRANSI_TYPE "
				 * + "= '4D18-01' AND B.PK_PROJECT = '" + pk_project +
				 * "' AND A.PK_CBSNODE = '" + pk_cbsnode + "'"; List<Object[]>
				 * ysls = getDao.query(ysql); if (ysls.size() <= 0) { if ((ifbl
				 * == null || "1001A2100000000B68C3" .equals(ifbl))) { throw new
				 * BusinessException("第" + (i + 1) + "行未完成预算录入!"); } } //
				 * ----------------------2是否完成预算录入判断结束---------------------- }
				 */

			}
			// ----------------------3是否有开工单----------------------
			String cbsql = "SELECT NAME FROM RL_CBSNAME WHERE NAME = '"
					+ pk_cbsnode_name + "' AND TYPE = 'CBS'";
			List<Object[]> cbsls = getDao.query(cbsql);
			if (cbsls != null && cbsls.size() > 0) {
				String rqsql = "SELECT COUNT(PK_PROJECTPROPOSAL) AS SL FROM PM_PROJECTPROPOSAL WHERE BILL_STATUS = '1' AND PK_GROUP = '0001A1100000000001QS' "
						+ "AND TRANSI_TYPE = '4D15-Cxx-011' AND DR = 0 AND PK_PROJECT = '"
						+ pk_project + "'";
				List<Object[]> rqls = getDao.query(rqsql);
				Logger.error("查询开工容缺受理单sql：" + rqsql);
				if (Integer.parseInt(rqls.get(0)[0] + "") <= 0) {
					String pgdsql = "SELECT PK_PROJECTPROPOSAL FROM PM_PROJECTPROPOSAL WHERE BILL_STATUS = '1' AND PK_GROUP = '0001A1100000000001QS' "
							+ "AND TRANSI_TYPE = '4D15-Cxx-008' AND DR = 0 AND PK_PROJECT = '"
							+ pk_project
							+ "' AND AUDITTIME >= '2021-03-26 00:00:00'";
					Logger.error("查询开工单sql：" + pgdsql);
					List<Object[]> pgdls = getDao.query(pgdsql);
					System.out.println("pgdsql==" + pgdsql);
					if (pgdls != null && pgdls.size() > 0
							&& pgdls.get(0) != null && pgdls.get(0)[0] != null) {
						if (("0001A21000000003U6L6".equals(pk_org) || "0001A21000000003U6L6"
								.equals(pk_fatherorg))
								&& (ifbl == null || "1001A2100000000B68C3"
										.equals(ifbl))) {
							throw new BusinessException("该项目已完成开工单!");
						}
					}
				}
			}
			// ----------------------3是否有派工单判断结束----------------------
		}
	}

	// 校验燃气组织
	public void checkRQOrg(ContractBillVO billVO) throws BusinessException {
		String pk_org = billVO.getParentVO().getPk_org();
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		String checkpk_org = (String) getHyPubBO().findColValue("sys_config",
				"config_value", "nvl(dr,0) = 0 and config_key ='66'");
		if (checkpk_org != null && checkpk_org.indexOf(pk_org) != -1) {
			GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
			ContrHeadVO headVO = (ContrHeadVO) billVO.getParentVO();
			String pk_contr = headVO.getPrimaryKey();// 单据主键
			String pk_project = headVO.getPk_project();// 项目主键
			String pk_supplier = headVO.getPk_supplier();// 供应商主键 --施工单位
			Logger.error("单据PK：" + headVO.getPrimaryKey());
			String selcfsql = "SELECT COUNT(PK_CONTR) AS SL FROM PM_CONTR WHERE PK_PROJECT = '"
					+ pk_project
					+ "' AND PK_SUPPLIER = '"
					+ pk_supplier
					+ "' AND DR = 0 AND BILL_STATUS <> 11 AND PK_CONTR <> '"
					+ headVO.getPrimaryKey() + "'";
			List<Object[]> xmxzls = getDao.query(selcfsql);
			int yysl = Integer.parseInt(xmxzls.get(0)[0] + "");// 数量
			Logger.error("项目+供应商验证sql：" + selcfsql);
			if (yysl > 0) {
				throw new BusinessException("同类合同系统已存在，不允许重复签订！");
			}
		}
	}

	private JSONArray getDtaileDataMapNew(ContractBillVO temp)
			throws BusinessException {
		// 获取详细信息
		List dtlist = new ArrayList();
		ContrWorksVO[] bvos = (ContrWorksVO[]) temp
				.getChildren(ContrWorksVO.class);
		Map bodyMap = getBody(bvos, getDef2(temp.getParentVO().getPk_org()));
		dtlist.add(bodyMap);

		if (temp.getChildren(ContrItemsVO.class) != null
				&& temp.getChildren(ContrItemsVO.class).length > 0) {
			Map contrItemsMap = getNewContrItems(
					(ContrItemsVO[]) temp.getChildren(ContrItemsVO.class),
					getDef2(temp.getParentVO().getPk_org()));
			if (null != contrItemsMap) {
				dtlist.add(contrItemsMap);
			}
		}

		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(dtlist, jsonConfig);
		return dtlistString;
	}

	// 合同条款
	private Map getNewContrItems(ContrItemsVO[] children, String def2)
			throws BusinessException {
		Map dtMap = new HashMap();
		// 接口获取
		dtMap.put("tableDBName", TableName + "_dt2");
		List workflowRequestTableRecords = new ArrayList();
		for (ContrItemsVO temp : children) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = new ArrayList();
			if (!"1".equals(def2)) {
				workflowRequestTableFields = OaWorkFlowUtil.transBean2Map(temp);
			}
			Map workflowRequestTableFieldsMap = new HashMap();
			String tkbmstr = (String) getHyPubBO().findColValue(
					"ct_termset",
					"vtermcode",
					"nvl(dr,0) = 0 and pk_ct_termset ='"
							+ temp.getPk_ct_termset() + "'");
			Map tkbm = OaWorkFlowUtil.listAdd("tkbm", tkbmstr);
			workflowRequestTableFields.add(tkbm);

			Map tkmc = OaWorkFlowUtil.listAdd("tkmc", temp.getVtermcontent());
			workflowRequestTableFields.add(tkmc);

			String tklxzj = (String) getHyPubBO().findColValue(
					"ct_termset",
					"pk_ct_termtype",
					"nvl(dr,0) = 0 and pk_ct_termset ='"
							+ temp.getPk_ct_termset() + "'");
			String tklxstr = (String) getHyPubBO().findColValue("ct_termtype",
					"termtypename",
					"nvl(dr,0) = 0 and pk_ct_termtype ='" + tklxzj + "'");
			Map tklx = OaWorkFlowUtil.listAdd("tklx", tklxstr);
			workflowRequestTableFields.add(tklx);

			Map tknr = OaWorkFlowUtil.listAdd("tknr", temp.getVtermcontent());
			workflowRequestTableFields.add(tknr);

			Map qtxx = OaWorkFlowUtil.listAdd("qtxx", temp.getOther_info());
			workflowRequestTableFields.add(qtxx);

			Map bz = OaWorkFlowUtil.listAdd("bz", temp.getMemo());
			workflowRequestTableFields.add(bz);
			// 乙方上报资料限期
			Map yfsbzlxq = OaWorkFlowUtil.listAdd("yfsbzlxq", temp.getBdef1());
			workflowRequestTableFields.add(yfsbzlxq);

			// 生产管理部验收限期
			Map scglbysxq = OaWorkFlowUtil
					.listAdd("scglbysxq", temp.getBdef2());
			workflowRequestTableFields.add(scglbysxq);
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	private Map getBody(ContrWorksVO[] bvo, String def2)
			throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (ContrWorksVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = new ArrayList();
			if (!"1".equals(def2)) {
				workflowRequestTableFields = OaWorkFlowUtil.transBean2Map(temp);
			}
			// 其他字段------begin
			// 物料信息
			MaterialVO materialVO = (MaterialVO) getHyPubBO()
					.queryByPrimaryKey(MaterialVO.class, temp.getPk_material());
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
			// 单位
			String dwstr = "";
			if (null != temp.getPk_measdoc()) {
				dwstr = (String) getHyPubBO().findColValue(
						"bd_measdoc",
						"name",
						"nvl(dr,0) = 0 and pk_measdoc  = '"
								+ temp.getPk_measdoc() + "'");
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("dw",
					dwstr));
			// CBS名称
			String cbsName = (String) getHyPubBO().findColValue(
					"bd_cbsnode",
					"name",
					"nvl(dr,0) = 0 and pk_cbsnode  = '" + temp.getPk_cbsnode()
							+ "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("cbsmc",
					cbsName));
			// 能投原字段
			if (null != materialVO) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("mc",
						materialVO.getName()));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bm",
						materialVO.getCode()));
			}
			if (null != temp.getPk_measdoc()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"dw",
						VOUtils.getDocName(MeasdocVO.class,
								temp.getPk_measdoc())));
			}
			String s = "0";
			if (null != temp.getPk_wbs()) {
				s = VOUtils.getDocName(WbsVO.class, temp.getPk_wbs());
			}
			workflowRequestTableFields
					.add(OaWorkFlowUtil.listAddObj("xmrw", s));
			if (null != temp.getPk_cbsnode()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"cbs",
						VOUtils.getDocName(CBSNodeVO.class,
								temp.getPk_cbsnode())));
			}
			String strdj = "0";
			if (null != temp.getNprom_price()) {
				strdj = temp.getNprom_price().setScale(2,
						UFDouble.ROUND_HALF_UP)
						+ "";
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("dj",
					strdj));
			if (null != temp.getService_prc_ratio()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fwjgxs", temp.getService_prc_ratio() + ""));
			}
			String strsl = "0";
			if (null != temp.getCurr_num()) {
				strsl = temp.getCurr_num().setScale(2, UFDouble.ROUND_HALF_UP)
						+ "";
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sl",
					strsl));
			if (null != temp.getCurr_mny()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("je",
						temp.getCurr_mny().setScale(2, UFDouble.ROUND_HALF_UP)
								+ ""));
			}

			if (null != temp.getBdef1()) {
				String bhsjemap = temp.getBdef1() + "";
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bhsje", bhsjemap));
			}

			if (null != temp.getTot_comp_num()) {
				String str = temp.getTot_comp_num() + "";
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"ljwcl", str));
			}

			// 战网名称
			if (null != temp.getBdef11()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"zwmc", temp.getBdef11() + ""));
			}
			// 维保内容
			if (null != temp.getBdef12()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"wbnr", temp.getBdef12() + ""));
			}
			// 备注
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bz",
					temp.getBdef13() + ""));
			// 用热地址
			if (null != temp.getBdef14()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"yrdz", temp.getBdef14() + ""));
			}
			// 设备名称/机组型号
			if (null != temp.getBdef15()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"sbmcjzxh", temp.getBdef15() + ""));
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

	// 根据组织+项目+任务名称查询是否有该任务-----参数：pkOrg==所属组织，pk_project==项目，wbsName==WBS任务名称
	public String getWbsWork(String pkOrg, String pk_project, String wbsName)
			throws DAOException {
		String pk_wbs = "";
		String wbsWorkSql = "SELECT PK_WBS FROM PM_WBS WHERE DR = 0 AND (ENABLESTATE = 1 OR ENABLESTATE = 2) AND PK_DUTY_ORG = '"
				+ pkOrg
				+ "'"
				+ " AND PK_PROJECT = '"
				+ pk_project
				+ "' AND WBS_NAME = '" + wbsName + "'";
		List<Object[]> wbsWorkLs = getDao.query(wbsWorkSql);
		if (wbsWorkLs != null && wbsWorkLs.size() > 0
				&& wbsWorkLs.get(0)[0] != null) {
			// 如果查出多条只取第一条
			pk_wbs = wbsWorkLs.get(0)[0] + "";
		} else {
			// 根据组织+项目+任务去
			String selhaschildsql = "SELECT PK_WBS FROM PM_WBS WHERE DR = 0 AND ( ENABLESTATE = 1 OR ENABLESTATE = 2 ) AND PK_DUTY_ORG = '"
					+ pkOrg
					+ "' AND PK_PROJECT = '"
					+ pk_project
					+ "' AND WBS_NAME = '施工进度'";
			System.out.println("selhaschildsql==" + selhaschildsql);
			List<Object[]> chLs = getDao.query(selhaschildsql);
			if (chLs != null && chLs.size() > 0 && chLs.get(0)[0] != null) {
				String selchildsql = "SELECT COUNT(PK_WBS) AS SL FROM PM_WBS WHERE DR = 0 AND ( ENABLESTATE = 1 OR ENABLESTATE = 2 ) AND PK_DUTY_ORG = '"
						+ pkOrg + "' AND PK_PARENT  = '" + chLs.get(0)[0] + "'";
				System.out.println("selchildsql==" + selchildsql);
				List<Object[]> slLs = getDao.query(selchildsql);
				if (slLs != null && Integer.parseInt(slLs.get(0)[0] + "") > 0) {
					ExceptionUtils.wrappBusinessException("项目[" + pk_project
							+ "]未找到对应任务！");
				} else {
					wbsName = "施工进度";
				}

			}
		}
		return pk_wbs;
	}

	// （1）、验证组织，如果有则继续之后的校验，没有则doAction -----参数：pkOrg==所属组织
	public int checkOrg(String pkOrg) throws DAOException {
		int flag = 0;// 返回 0则doAction====1则继续
		String fg = "0";// 是否有当前组织
		String orgSql = "SELECT NAME FROM BD_DEFDOC WHERE PK_DEFDOCLIST = '1001A11000000017B4LB'";
		List<Object[]> resultList = getDao.query(orgSql);
		if (resultList.size() > 0 && resultList.get(0)[0] != null) {
			for (int i = 0; i < resultList.size(); i++) {
				Object[] item = resultList.get(i);
				if (pkOrg.equals(item[0])) {
					fg = "1";
				}
			}
		}
		if ("1".equals(fg)) {
			flag = 1;
		}

		System.out.println("<1>当前组织[" + pkOrg + "]在验证范围之内！");
		return flag;
	}

	// 根据交易类型判断是否需要验证WBS任务有没有-----参数：transType==交易类型
	public boolean checkIfHaveWbs(String transType) throws DAOException {
		boolean bool = true;
		String ifWbsSql = "SELECT COUNT(PK_DEFDOC) FROM BD_DEFDOC WHERE PK_DEFDOCLIST = '1001A11000000023EZXV"
				+ "' AND DR = 0 AND CODE = '" + transType + "'";
		List<Object[]> ifWbsLs = getDao.query(ifWbsSql);
		// System.out.println("ifWbsLs.get(0)[0]==" + ifWbsLs.get(0)[0]);
		if (!"0".equals(ifWbsLs.get(0)[0] + "")) {
			bool = true;
		} else {
			bool = false;
		}
		System.out.println("是否需要验证：" + bool);
		return bool;
	}
}
