package nc.impl.pcm.feebalance.pvt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.pcm.contract.bp.rule.CheckPayApplyRule;
import nc.bs.pmpub.rule.AppendBusiTypeBeforeRule;
import nc.bs.pmpub.rule.Approve4SendFipRule;
import nc.bs.pmpub.rule.UnApproveDelFipMesRule;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pcm.billrule.DeletePayableBillRule;
import nc.impl.pcm.feebalance.rule.FeeBalanceCommitRule;
import nc.impl.pcm.feebalance.rule.InitMutiCurrTypeBeforeRule;
import nc.impl.pmpub.servicebase.BillBaseImpl;
import nc.impl.pmpub.servicebase.action.ApproveAction;
import nc.impl.pmpub.servicebase.action.CommitAction;
import nc.impl.pmpub.servicebase.action.InsertAction;
import nc.impl.pmpub.servicebase.action.UnApproveAction;
import nc.impl.pmpub.servicebase.action.UpdateAction;
import nc.itf.pbm.commonrule.BillDelOrUnApprove4BudgetRule;
import nc.itf.pbm.commonrule.BillSaveOrApprove4BudgetRule;
import nc.itf.pcm.feebalance.prv.IFeeBalance;
import nc.pub.tools.VOUtils;
import nc.ui.pcm.utils.GetDao;
import nc.vo.bd.cust.CustSupplierVO;
import nc.vo.bd.material.MaterialVO;
import nc.vo.org.OrgVO;
import nc.vo.pcm.contracttype.ContractTypeHeadVO;
import nc.vo.pcm.feebalance.FeeBalanceBillVO;
import nc.vo.pcm.feebalance.FeeBalanceBodyVO;
import nc.vo.pcm.feebalance.FeeBalanceHeadVO;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.ppm.projectcheck.ProjectCheckHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings({ "unchecked", "rawtypes", "unused", "restriction" })
// 竣工资料，设计需求，图纸会审，热力既有项目预算调整单
public class FeeBalanceImpl extends BillBaseImpl<FeeBalanceBillVO> implements
		IFeeBalance {
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表名
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);

	public FeeBalanceImpl() {
	}

	public FeeBalanceBillVO[] insertFeeBalance(FeeBalanceBillVO[] billVOs)
			throws BusinessException {
		FeeBalanceBodyVO[] bvos = before(billVOs[0]);// 保存前
		if (bvos != null) {
			billVOs[0].setChildrenVO(bvos);
		}
		return (FeeBalanceBillVO[]) insert(billVOs);
	}

	public FeeBalanceBodyVO[] before(FeeBalanceBillVO billVO)
			throws BusinessException {
		// TODO Auto-generated method stub
		FeeBalanceHeadVO hvo = (FeeBalanceHeadVO) billVO.getParentVO();
		String pkOrg = hvo.getPk_org();
		FeeBalanceBodyVO[] bodyvos = (FeeBalanceBodyVO[]) billVO
				.getChildrenVO();
		String transi_type = hvo.getTransi_type();// 交易类型
		/**
		 * 20240402XBX控制价委托申请单“招标控制价金额”超计划大本工程费用剩余金额（投资估算金额）无法保存
		 */
		if ("4D83-Cxx-84".equals(transi_type)) {
			for (FeeBalanceBodyVO bodyvo : bodyvos) {
				if (bodyvo.getDef8() != null) {
					UFDouble def8 = new UFDouble(bodyvo.getDef8());
					UFDouble def9 = new UFDouble(bodyvo.getDef9());
					if (def8.compareTo(bodyvo.getMoney()) > 0) {
						throw new BusinessException("招标控制价金额超出投资估算金额！");
					}
					if (def8.compareTo(def9) > 0) {
						throw new BusinessException("不能超过投资估算金额[" + def9 + "]！");
					}
				}
			}
		}
		/*
		 * ll 2024-01-03 竣工资料 项目的竣工验收单生效态是竣工资料录入的前提条件，不做竣工验收，没法录入竣工资料
		 */
		if ("4D83-Cxx-64".equals(transi_type)) {
			// 查询档案，取校验组织 Object conforg = (Object)
			Object conforg = (Object) getHyPubBO().findColValue(
					"bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
							+ "FROM BD_DEFDOCLIST WHERE CODE = 'HTSAVEORG')"
							+ " and code = '" + hvo.getPk_org() + "'");// 组织
			if (conforg != null) {
				String errinfo = "";
				for (FeeBalanceBodyVO bvo : bodyvos) {
					ProjectCheckHeadVO[] projectCheckHeadVOs = (ProjectCheckHeadVO[]) getHyPubBO()
							.queryByCondition(
									ProjectCheckHeadVO.class,
									" pk_project = '" + bvo.getPk_project()
											+ "' and pk_org = '"
											+ hvo.getPk_org()
											+ "' and bill_status = 1");
					// 项目
					ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
							.queryByPrimaryKey(ProjectHeadVO.class,
									bvo.getPk_project());
					if (projectCheckHeadVOs == null
							|| projectCheckHeadVOs.length <= 0) {
						errinfo += "项目编码[" + projectVO.getProject_code()
								+ "]没有做竣工验收或未审批通过！";
					}
				}
				if (StringUtils.isNotEmpty(errinfo)) {
					throw new BusinessException(errinfo);
				}
			}
		} else if ("4D83-Cxx-38".equals(transi_type)) {
			int num = 0;
			for (FeeBalanceBodyVO bodyvo : bodyvos) {
				int status = bodyvo.getStatus();
				if (status != VOStatus.DELETED) {
					num++;
				}
			}
			if (num > 1) {
				throw new BusinessException("只允许登记一条项目信息填报！");
			}
		}
		// code判断当前组织是否需要推进度
		String code = (String) getHyPubBO()
				.findColValue(
						"bd_defdoc",
						"code",
						"nvl(dr,0) = 0 and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where "
								+ "code = 'PUSHJD' and nvl(dr,0) = 0) and name = '"
								+ pkOrg + "'");
		// name判断当前单据是否需要推进度 name值为任务名称
		String name = (String) getHyPubBO()
				.findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where "
								+ "code = 'RDJDORG' and nvl(dr,0) = 0) and code = '"
								+ transi_type
								+ "' and pk_org = '"
								+ pkOrg
								+ "'");
		if ("4D83-Cxx-62".equals(transi_type)) {
			name = (String) getHyPubBO()
					.findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where "
									+ "code = 'htlx' and nvl(dr,0) = 0) and pk_defdoc = '"
									+ hvo.getDef1() + "' ");
		}
		if ("4D83-Cxx-04".equals(transi_type)) {
			name = (String) getHyPubBO()
					.findColValue(
							"bd_defdoc",
							"name",
							"nvl(dr,0) = 0 and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where "
									+ "code = 'zblb' and nvl(dr,0) = 0) and pk_defdoc = '"
									+ hvo.getDef1() + "' ");
		}
		if ("其他招标".equals(name)) {
			return null;
		}
		if (code != null && name != null) {
			for (int i = 0; i < bodyvos.length; i++) {
				FeeBalanceBodyVO itemvo = bodyvos[i];
				String pk_project = itemvo.getPk_project();
				String pk_wbs = getWbsWork(pkOrg, pk_project, name);
				String project_code = (String) getHyPubBO().findColValue(
						"bd_project", "project_code",
						"nvl(dr,0) = 0 and pk_project  ='" + pk_project + "'");
				double totalFinish = itemvo.getMoney().toDouble();
				String PK_FEEBALANCE_B = itemvo.getPk_feebalance_b();
				String errmsg = checkTotalRate(pkOrg, pk_project, project_code,
						transi_type, totalFinish, PK_FEEBALANCE_B, pk_wbs);
				if (!"".equals(errmsg)) {
					throw new BusinessException(errmsg);
				}
				bodyvos[i].setPk_wbs(pk_wbs);
				String pk_feebalance_b = bodyvos[i].getPrimaryKey();
				String sql = "UPDATE PM_FEEBALANCE_B SET PK_WBS = '" + pk_wbs
						+ "' WHERE PK_FEEBALANCE_B = '" + pk_feebalance_b + "'";
				getDao.executeUpdate(sql);
			}
		} else {
			return null;
		}
		return bodyvos;
	}

	protected void initInsertAction(InsertAction<FeeBalanceBillVO> action) {
		super.initInsertAction(action);
		action.addBeforeRule(new InitMutiCurrTypeBeforeRule());
		action.addBeforeRule(new AppendBusiTypeBeforeRule());
	}

	public FeeBalanceBillVO[] updateFeeBalance(FeeBalanceBillVO[] billVOs,
			FeeBalanceBillVO[] originBillVOs) throws BusinessException {
		FeeBalanceBodyVO[] bvos = before(billVOs[0]);// 保存前
		return (FeeBalanceBillVO[]) update(billVOs, originBillVOs);
	}

	protected void initUpdateAction(UpdateAction<FeeBalanceBillVO> action) {
		super.initUpdateAction(action);
		action.addBeforeRule(new InitMutiCurrTypeBeforeRule());
	}

	public void deleteFeeBalance(FeeBalanceBillVO[] billVOs)
			throws BusinessException {
		delete(billVOs);
	}

	public Object approveFeeBalance(FeeBalanceBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		return approve(billVOs, pfParamVO);
	}

	protected void initApproveAction(ApproveAction<FeeBalanceBillVO> action) {
		super.initApproveAction(action);
		action.addAfterRule(new BillSaveOrApprove4BudgetRule(1, true));
		action.addAfterRule(new Approve4SendFipRule("money"));
	}

	public FeeBalanceBillVO[] unApproveFeeBalance(FeeBalanceBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		return (FeeBalanceBillVO[]) unApprove(billVOs, pfParamVO);
	}

	protected void initUnApproveAction(UnApproveAction<FeeBalanceBillVO> action) {
		super.initUnApproveAction(action);
		action.addBeforeRule(new BillDelOrUnApprove4BudgetRule(1, true));
		action.addBeforeRule(new CheckPayApplyRule());
		action.addBeforeRule(new DeletePayableBillRule());
		action.addBeforeRule(new UnApproveDelFipMesRule("money"));
	}

	protected void initCommitAction(CommitAction<FeeBalanceBillVO> action) {
		super.initCommitAction(action);
		action.addBeforeRule(new FeeBalanceCommitRule());
	}

	private String billType = "4D83";

	// 提交
	public FeeBalanceBillVO[] commitFeeBalance(FeeBalanceBillVO[] billVOs)
			throws BusinessException {
		FeeBalanceBillVO[] nvos = (FeeBalanceBillVO[]) commit(billVOs);
		for (FeeBalanceBillVO temp : billVOs) {
			FeeBalanceHeadVO hvo = (FeeBalanceHeadVO) temp.getParentVO();
			OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
					hvo.getPk_org());
			String transi_type = hvo.getTransi_type();// 交易类型
			JSONArray headData = null;
			JSONArray bodyData = null;
			String ifbl = hvo.getDef4();// 是否补录
			if ("1".equals(getDef2(hvo.getPk_org()))) {
				if (transi_type.equals("4D83-Cxx-83")
						&& null != orgVO.getDef1()) {
					FeeBalanceBodyVO[] bodyVOS = (FeeBalanceBodyVO[]) temp
							.getChildrenVO();
					headData = getMainMap(hvo);
					bodyData = getDtaileDataMap(temp);
					WorkFId = "332";
					OaWorkFlowUtil.sendOaData(headData, bodyData,
							getWorkFlowBill(hvo));
				} else if (transi_type.equals("4D83-Cxx-84")) {
					WorkFId = "47";
					TableName = "formtable_main_50";
					FeeBalanceBodyVO[] bodyVOS = (FeeBalanceBodyVO[]) temp
							.getChildrenVO();
					double bshje = 0;
					for (FeeBalanceBodyVO itemvo : bodyVOS) {
						if (itemvo.getDef7() != null) {
							bshje += Double.parseDouble(itemvo.getDef7() + "");
						}
					}
					// 获取主表数据
					headData = getMainMapNew(hvo, bshje);
					// 获取子表数据
					bodyData = getDtaileDataMapNew(temp, hvo.getPk_org(),
							hvo.getTransi_type());
					OaWorkFlowUtil.sendOaData(headData, bodyData,
							getWorkFlowBill(hvo));
				}
			}
			// -- && !"1001A2100000000B68C1".equals(ifbl)
			String def2 = getDef2(hvo.getPk_org());
			if ("4".equals(def2)) {

				OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hvo.getPk_org(),
						transi_type);
				if (oaVo != null && oaVo.getIsdr() == 0) {
					WorkFId = oaVo.getFlowid();
					TableName = oaVo.getTablename();
					FeeBalanceBodyVO[] bodyVOS = (FeeBalanceBodyVO[]) temp
							.getChildrenVO();
					double bshje = 0;
					for (FeeBalanceBodyVO itemvo : bodyVOS) {
						if (itemvo.getDef7() != null) {
							bshje += Double.parseDouble(itemvo.getDef7() + "");
						}
					}
					// 新增针对港华
					// 获取主表数据
					headData = getMainMapNew(hvo, bshje);
					// 获取子表数据
					bodyData = getDtaileDataMapNew(temp, hvo.getPk_org(),
							hvo.getTransi_type());
					// 构造workflow信息
					OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
					workFlowVO.setPrimaryKey(temp.getPrimaryKey());
					workFlowVO.setPkGroup(hvo.getPk_group());
					workFlowVO.setPkOrg(hvo.getPk_org());
					workFlowVO.setBillMaker(hvo.getBillmaker());
					workFlowVO.setCreator(hvo.getCreator());
					workFlowVO.setBillCode(billType);// 单据类型
					workFlowVO.setWorkflowId(OaWorkFlowUtil
							.getOAFlowID(WorkFId));
					workFlowVO.setWorkflowName(oaVo.getBilltypename());// 名称
					WorkFlowBill bill = OaWorkFlowUtil
							.getWorkFlowBill(workFlowVO);
					bill.setDef3("ZT");
					bill.setDef4(hvo.getBill_code());// 单据编号
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
			}
		}
		return nvos;
	}

	private JSONArray getMainMap(FeeBalanceHeadVO hvo) throws BusinessException {
		List list = new ArrayList();
		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				hvo.getPk_org());
		list.add(OaWorkFlowUtil.listAdd("szgs", orgVO.getCode()));
		list.add(OaWorkFlowUtil.listAdd("xmzz", orgVO.getName()));
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				hvo.getBillmaker());
		if (null != userVO) {
			Map zdrmc = OaWorkFlowUtil.listAdd("zdrmc", userVO.getUser_name());
			list.add(zdrmc);
			Map zdrbm = OaWorkFlowUtil.listAdd("zdrbm", userVO.getUser_code());
			list.add(zdrbm);
			Map zdrzj = OaWorkFlowUtil.listAdd("zdrzj", userVO.getPrimaryKey());
			list.add(zdrzj);
			Map sfzh = new HashMap();
			String id = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"id",
					"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc()
							+ "'");
			sfzh.put("fieldName", "sfzh");
			sfzh.put("fieldValue", id);
			list.add(sfzh);
		}
		list.add(OaWorkFlowUtil.listAdd("djzj", hvo.getPk_feebalance()));
		list.add(OaWorkFlowUtil.listAdd("htbh", hvo.getBill_code()));
		list.add(OaWorkFlowUtil.listAdd("htmc", hvo.getDef3()));
		Map gyszj = OaWorkFlowUtil.listAdd("gyszj", hvo.getPk_supplier());
		list.add(gyszj);
		if (null != hvo.getPk_supplier()) {
			String gysmc = (String) getHyPubBO().findColValue(
					"bd_supplier",
					"name",
					"nvl(dr,0) = 0 and pk_supplier  = '" + hvo.getPk_supplier()
							+ "'");
			list.add(OaWorkFlowUtil.listAdd("gysmc", gysmc));
		}
		list.add(OaWorkFlowUtil.listAdd("qybmzj", hvo.getPk_transact_dept_v()));
		if (null != hvo.getPk_transact_dept_v()) {
			String qybmmc = (String) getHyPubBO().findColValue(
					"org_dept_v",
					"name",
					"nvl(dr,0) = 0 and pk_vid  = '"
							+ hvo.getPk_transact_dept_v() + "'");
			list.add(OaWorkFlowUtil.listAdd("qybmmc", qybmmc));
		}
		list.add(OaWorkFlowUtil.listAdd("dkbmzj", hvo.getDef11()));
		if (null != hvo.getDef11()) {
			String dkmc = (String) getHyPubBO().findColValue("org_dept",
					"name",
					"nvl(dr,0) = 0 and  pk_dept ='" + hvo.getDef11() + "'");
			Map dkbmmc = OaWorkFlowUtil.listAdd("dkbmmc", dkmc);
			list.add(dkbmmc);
		}
		if (null != hvo.getDef11()) {
			String dkbm = (String) getHyPubBO().findColValue("org_dept",
					"code",
					"nvl(dr,0) = 0 and  pk_dept ='" + hvo.getDef11() + "'");
			Map dkbmbm = OaWorkFlowUtil.listAdd("dkbmbm", dkbm);
			list.add(dkbmbm);
		}
		list.add(OaWorkFlowUtil.listAdd("qyrq", hvo.getBalance_date().getYear()
				+ "-" + hvo.getBalance_date().getStrMonth() + "-"
				+ hvo.getBalance_date().getStrDay()));
		// 合同类型
		if (hvo.getDef10() != null) {
			String htlx = (String) getHyPubBO().findColValue(
					"pm_contracttype",
					"type_name",
					" nvl(dr,0) = 0 and pk_contracttype = '" + hvo.getDef10()
							+ "'");
			list.add(OaWorkFlowUtil.listAdd("htlx", htlx));
		}
		// 控制价合同类型
		if (hvo.getDef1() != null) {
			String kzjhtlx = (String) getHyPubBO().findColValue("bd_defdoc",
					"name",
					" nvl(dr,0) = 0 and pk_defdoc = '" + hvo.getDef1() + "'");
			list.add(OaWorkFlowUtil.listAdd("kzjhtlx", kzjhtlx));
		}
		if (hvo.getDef9() != null) {
			String htymc = (String) getHyPubBO().findColValue("bd_supplier",
					"name",
					" nvl(dr,0) = 0 and pk_supplier = '" + hvo.getDef9() + "'");
			list.add(OaWorkFlowUtil.listAdd("htymc", htymc));
		}
		list.add(OaWorkFlowUtil.listAdd("fgldzj", hvo.getDef12()));
		// 合同原名称
		if (null != hvo.getDef12()) {
			String htymc = (String) getHyPubBO().findColValue("bd_defdoc",
					"name",
					" nvl(dr,0) = 0 and pk_defdoc = '" + hvo.getDef12() + "'");
			list.add(OaWorkFlowUtil.listAdd("htymc", htymc));
		}

		// 项目经理
		list.add(OaWorkFlowUtil.listAdd("xmjlzj", hvo.getDef13()));
		if (null != hvo.getDef13()) {
			String xmjlmc = (String) getHyPubBO().findColValue("bd_defdoc",
					"name",
					" nvl(dr,0) = 0 and pk_defdoc = '" + hvo.getDef13() + "'");
			list.add(OaWorkFlowUtil.listAdd("xmjlmc", xmjlmc));
		}
		// 招标方式
		if (hvo.getDef14() != null) {
			String zbfs = (String) getHyPubBO().findColValue("bd_defdoc",
					"name",
					" nvl(dr,0) = 0 and pk_defdoc = '" + hvo.getDef14() + "'");
			list.add(OaWorkFlowUtil.listAdd("zbfs", zbfs));
		}
		// 是否进场备案
		if (hvo.getDef18() != null) {
			String sfjcba = (String) getHyPubBO().findColValue("bd_defdoc",
					"name",
					" nvl(dr,0) = 0 and pk_defdoc = '" + hvo.getDef18() + "'");
			list.add(OaWorkFlowUtil.listAdd("sfjcba", sfjcba));
		}
		// 计价方式
		if (hvo.getDef19() != null) {
			String jjfs = (String) getHyPubBO().findColValue("bd_defdoc",
					"name",
					" nvl(dr,0) = 0 and pk_defdoc = '" + hvo.getDef19() + "'");
			list.add(OaWorkFlowUtil.listAdd("jjfs", jjfs));
		}
		// 跟踪审计需求
		if (hvo.getDef20() != null) {
			String gzsjxq = (String) getHyPubBO().findColValue("bd_defdoc",
					"name",
					" nvl(dr,0) = 0 and pk_defdoc = '" + hvo.getDef20() + "'");
			list.add(OaWorkFlowUtil.listAdd("gzsjxq", gzsjxq));
		}
		list.add(OaWorkFlowUtil.listAdd(
				"sjqhte",
				new UFDouble(hvo.getDef17() == null ? "0.00" : hvo.getDef17())
						.setScale(2, UFDouble.ROUND_HALF_UP) + ""));
		list.add(OaWorkFlowUtil.listAdd(
				"yfkje",
				new UFDouble(hvo.getDef17() == null ? "0.00" : hvo.getDef17())
						.setScale(2, UFDouble.ROUND_HALF_UP) + ""));
		list.add(OaWorkFlowUtil.listAdd(
				"yhtje",
				new UFDouble(hvo.getDef6() == null ? "0.00" : hvo.getDef6())
						.setScale(2, UFDouble.ROUND_HALF_UP) + ""));
		list.add(OaWorkFlowUtil.listAdd(
				"htje",
				new UFDouble(hvo.getMoney())
						.setScale(2, UFDouble.ROUND_HALF_UP) + ""));
		list.add(OaWorkFlowUtil.listAdd("ksrq", hvo.getDef15()));
		list.add(OaWorkFlowUtil.listAdd("jsrq", hvo.getDef16()));
		list.add(OaWorkFlowUtil.listAdd("qyrzj", hvo.getPk_transactor()));
		list.add(OaWorkFlowUtil.listAdd("bz", hvo.getMemo()));
		if (null != hvo.getPk_transactor()) {
			String qyrmc = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and pk_psndoc = '" + hvo.getPk_transactor()
							+ "'");
			list.add(OaWorkFlowUtil.listAdd("qyrmc", qyrmc));
		}
		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	private JSONArray getDtaileDataMap(FeeBalanceBillVO aggVO)
			throws BusinessException {
		Map ctPuBMap = getFeeBalanceBillVO(aggVO);
		JSONArray dtlistString = JSONArray.fromObject(ctPuBMap);
		return dtlistString;
	}

	private Map getFeeBalanceBillVO(FeeBalanceBillVO aggVO)
			throws BusinessException {
		Map dtMap = new HashMap();
		List workflowRequestTableRecords = new ArrayList();
		FeeBalanceHeadVO hvo = (FeeBalanceHeadVO) aggVO.getParentVO();
		FeeBalanceBodyVO[] bodyVOS = (FeeBalanceBodyVO[]) aggVO.getChildrenVO();
		dtMap.put("tableDBName", "formtable_main_431_dt1");
		for (FeeBalanceBodyVO temp : bodyVOS) {
			List workflowRequestTableFields = new ArrayList();
			Map workflowRequestTableFieldsMap = new HashMap();
			String project_code = (String) getHyPubBO().findColValue(
					"bd_project",
					"project_code",
					"nvl(dr,0) = 0 and pk_project ='" + temp.getPk_project()
							+ "'");
			Map xmbm = OaWorkFlowUtil.listAdd("xmbm", project_code);
			workflowRequestTableFields.add(xmbm);
			String project_name = (String) getHyPubBO().findColValue(
					"bd_project",
					"project_name",
					"nvl(dr,0) = 0 and pk_project ='" + temp.getPk_project()
							+ "'");
			Map xmmc = OaWorkFlowUtil.listAdd("xmmc", project_name);
			workflowRequestTableFields.add(xmmc);
			String cbs = (String) getHyPubBO().findColValue(
					"bd_cbsnode",
					"name",
					"nvl(dr,0) = 0 and   pk_cbsnode  ='" + temp.getPk_cbsnode()
							+ "'");
			Map cbsmc = OaWorkFlowUtil.listAdd("cbsmc", cbs);
			workflowRequestTableFields.add(cbsmc);
			String fwjgxss = temp.getService_prc_ratio().setScale(2,
					UFDouble.ROUND_HALF_UP)
					+ "";
			Map fwjgxs = OaWorkFlowUtil.listAdd("fwjgxs", fwjgxss);
			workflowRequestTableFields.add(fwjgxs);
			String material_code = (String) getHyPubBO().findColValue(
					"bd_material",
					"code",
					"nvl(dr,0) = 0 and   pk_material  ='"
							+ temp.getPk_material() + "'");
			Map wlbm = OaWorkFlowUtil.listAdd("wlbm", material_code);
			workflowRequestTableFields.add(wlbm);
			String material_name = (String) getHyPubBO().findColValue(
					"bd_material",
					"name",
					"nvl(dr,0) = 0 and   pk_material  ='"
							+ temp.getPk_material() + "'");
			Map wlmc = OaWorkFlowUtil.listAdd("wlmc", material_name);
			workflowRequestTableFields.add(wlmc);
			Map je = OaWorkFlowUtil.listAdd("je", temp.getMoney() + "");
			workflowRequestTableFields.add(je);
			Map dj = OaWorkFlowUtil.listAdd("dj", temp.getPrice() + "");
			workflowRequestTableFields.add(dj);
			Map sl = OaWorkFlowUtil.listAdd("sl", temp.getMaterial_num() + "");
			workflowRequestTableFields.add(sl);
			String sdw = (String) getHyPubBO().findColValue(
					"bd_measdoc",
					"name",
					"nvl(dr,0) = 0 and   pk_measdoc  ='" + temp.getPk_measdoc()
							+ "'");
			Map dw = OaWorkFlowUtil.listAdd("dw", sdw);
			workflowRequestTableFields.add(dw);
			Map sjqhte = OaWorkFlowUtil.listAdd(
					"sjqhte",
					new UFDouble(temp.getDef4() == null ? "0.00" : temp
							.getDef4()).setScale(2, UFDouble.ROUND_HALF_UP)
							+ "");
			workflowRequestTableFields.add(sjqhte);
			Map yhtje = OaWorkFlowUtil.listAdd(
					"yhtje",
					new UFDouble(temp.getDef2() == null ? "0.00" : temp
							.getDef2()).setScale(2, UFDouble.ROUND_HALF_UP)
							+ "");
			workflowRequestTableFields.add(yhtje);
			Map yfkje = OaWorkFlowUtil.listAdd(
					"yfkje",
					new UFDouble(temp.getDef3() == null ? "0.00" : temp
							.getDef3()).setScale(2, UFDouble.ROUND_HALF_UP)
							+ "");
			workflowRequestTableFields.add(yfkje);
			Map sl1 = OaWorkFlowUtil.listAdd("sl1", temp.getDef5());
			workflowRequestTableFields.add(sl1);
			Map se = OaWorkFlowUtil.listAdd("se", temp.getDef6());
			workflowRequestTableFields.add(se);
			Map bhsje = OaWorkFlowUtil.listAdd("bhsje", temp.getDef7());
			workflowRequestTableFields.add(bhsje);
			if ("1".equals(getDef2(hvo.getPk_org()))) {
				Map fkfs = OaWorkFlowUtil.listAdd("fkfs", temp.getDef12());
				workflowRequestTableFields.add(fkfs);
			}
			Map yysje = OaWorkFlowUtil.listAdd("yysje", temp.getDef1() + "");
			workflowRequestTableFields.add(yysje);
			Map bz = OaWorkFlowUtil.listAdd("bz", temp.getMemo());
			workflowRequestTableFields.add(bz);
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	public FeeBalanceBillVO[] unCommitFeeBalance(FeeBalanceBillVO[] billVOs,
			PfParameterVO pfParamVO) throws BusinessException {
		FeeBalanceBillVO[] aggVO = (FeeBalanceBillVO[]) unCommit(billVOs,
				pfParamVO);
		unOaCommit(aggVO);
		return aggVO;
	}

	private void unOaCommit(FeeBalanceBillVO[] aggVO) throws BusinessException {
		for (FeeBalanceBillVO temp : aggVO) {
			FeeBalanceHeadVO hvo = (FeeBalanceHeadVO) temp.getParentVO();
			OaWorkFlowUtil.backOaWorkFlow(temp.getPrimaryKey());
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

	private WorkFlowBill getWorkFlowBill(FeeBalanceHeadVO hvo)
			throws BusinessException {
		String where = " nvl(dr,0) = 0 and pk_bill = '" + hvo.getPrimaryKey()
				+ "'";
		WorkFlowBill[] workFlowBills = (WorkFlowBill[]) getHyPubBO()
				.queryByCondition(WorkFlowBill.class, where);
		WorkFlowBill workFlowBill = new WorkFlowBill();
		if (null != workFlowBills && workFlowBills.length > 1) {
			throw new BusinessException("查询到多条流程记录");
		}
		String transi_type = hvo.getTransi_type();// 交易类型
		if (null != workFlowBills && workFlowBills.length == 1) {
			workFlowBill = workFlowBills[0];
			if (transi_type.equals("4D83-Cxx-83")) {
				workFlowBill.setWorkflowName("既有项目预算调整单");
			} else if (transi_type.equals("4D83-Cxx-84")) {
				workFlowBill.setWorkflowName("控制价委托申请单");
			}
		} else {
			UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
					UserVO.class, hvo.getBillmaker());
			workFlowBill.setDef5(userVO.getUser_code());
			workFlowBill.setPk_group(hvo.getPk_group());
			workFlowBill.setPk_org(hvo.getPk_org());
			workFlowBill.setPk_bill(hvo.getPrimaryKey());
			workFlowBill.setUser_name(userVO.getUser_name());
			workFlowBill.setUser_code(userVO.getUser_code());
			workFlowBill.setBill_status("1");
			workFlowBill.setCreationtime(new UFDate().toStdString());
			workFlowBill.setCreator(hvo.getCreator());
			workFlowBill.setBill_code(hvo.getBill_type());
			workFlowBill.setWorkflowId(WorkFId);
			if (transi_type.equals("4D83-Cxx-83")) {
				workFlowBill.setWorkflowName("既有项目预算调整单");
			} else if (transi_type.equals("4D83-Cxx-84")) {
				workFlowBill.setWorkflowName("控制价委托申请单");
			}

		}
		return workFlowBill;
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

	public String checkTotalRate(String pkOrg, String pk_project,
			String project_code, String transType, double totalFinish,
			String PK_FEEBALANCE_B, String pk_wbs) throws DAOException {
		String errorInfo = "";
		double maxcom = 0.0;// 最大累计完成
		String sql = "";// 查询最大累计完成
		if ("4D83-Cxx-04".equals(transType)) {
			sql = "SELECT MAX(FYMX.DEF7) AS MAXCOM FROM PM_FEEBALANCE_B FYMX LEFT JOIN PM_FEEBALANCE FY ON FYMX.PK_FEEBALANCE = FY.PK_FEEBALANCE "
					+ "WHERE FY.DR = 0 AND FYMX.DR = 0 AND FY.PK_ORG = '"
					+ pkOrg
					+ "' AND FYMX.PK_PROJECT = '"
					+ pk_project
					+ "' AND FYMX.PK_FEEBALANCE_B <> '"
					+ PK_FEEBALANCE_B
					+ "' " + "AND FYMX.PK_WBS = '" + pk_wbs + "'";
			List<Object[]> maxls = getDao.query(sql);
			if (maxls != null && maxls.size() > 0 && maxls.get(0)[0] != null) {
				maxcom = Double.parseDouble(maxls.get(0)[0] + "");
			}
			System.out.println("<4>[" + project_code + "]项目当前累计完成("
					+ totalFinish + ") || 最大累计完成(" + maxcom + ")");
			if (totalFinish <= maxcom) {
				errorInfo = "[" + project_code + "]项目当前累计完成(" + totalFinish
						+ ") 小于等于最大累计完成(" + maxcom + "),请修改！\r\n";
				// ExceptionUtils.wrappBusinessException(mess);
				// Log.error(mess);
			}
		} else {
			if (transType.length() >= 4
					&& "4D83".equals(transType.substring(0, 4))) {
				// 费用结算单
				sql = "SELECT MAX(FYMX.MONEY) AS MAXCOM FROM PM_FEEBALANCE_B FYMX LEFT JOIN PM_FEEBALANCE FY ON FYMX.PK_FEEBALANCE = FY.PK_FEEBALANCE "
						+ "WHERE FY.DR = 0 AND FYMX.DR = 0 AND FY.PK_ORG = '"
						+ pkOrg
						+ "' AND FYMX.PK_PROJECT = '"
						+ pk_project
						+ "' AND FYMX.PK_FEEBALANCE_B <> '"
						+ PK_FEEBALANCE_B
						+ "' " + "AND FYMX.PK_WBS = '" + pk_wbs + "'";
				List<Object[]> maxls = getDao.query(sql);
				if (maxls != null && maxls.size() > 0
						&& maxls.get(0)[0] != null) {
					maxcom = Double.parseDouble(maxls.get(0)[0] + "");
				}
				System.out.println("<4>[" + project_code + "]项目当前累计完成("
						+ totalFinish + ") || 最大累计完成(" + maxcom + ")");
				if (totalFinish <= maxcom) {
					errorInfo = "[" + project_code + "]项目当前累计完成(" + totalFinish
							+ ") 小于等于最大累计完成(" + maxcom + "),请修改！\r\n";
					// ExceptionUtils.wrappBusinessException(mess);
					// Log.error(mess);
				}
			}
		}
		return errorInfo;
	}

	private JSONArray getMainMapNew(FeeBalanceHeadVO parentVO, double bshje)
			throws BusinessException {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = new ArrayList();
		// 构造数据
		if (!"1".equals(getDef2(parentVO.getPk_org()))
				|| "4D83-Cxx-84".equals(parentVO.getTransi_type())) {
			list = OaWorkFlowUtil.transBean2Map(parentVO);
		}
		// ---------其他字段begin
		String stockName = (String) getHyPubBO()
				.findColValue(
						"org_itemorg",
						"name",
						"nvl(dr,0) = 0 and pk_itemorg = '"
								+ parentVO.getPk_org() + "'");
		list.add(OaWorkFlowUtil.listAddObj("zzmc", stockName));
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		// 控制价合同类型
		if (parentVO.getDef1() != null) {
			String kzjhtlx = (String) getHyPubBO().findColValue("bd_defdoc",
					"name",
					" nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef1() + "'");
			list.add(OaWorkFlowUtil.listAddObj("kzjhtlx", kzjhtlx));
		}
		// 招标方式
		if (parentVO.getDef14() != null) {
			String zbfs = (String) getHyPubBO().findColValue("bd_defdoc",
					"name",
					" nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef14() + "'");
			list.add(OaWorkFlowUtil.listAddObj("zbfs", zbfs));
		}
		// 是否进场备案
		if (parentVO.getDef18() != null) {
			String sfjcba = (String) getHyPubBO().findColValue("bd_defdoc",
					"name",
					" nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef18() + "'");
			list.add(OaWorkFlowUtil.listAddObj("sfjcba", sfjcba));
		}
		// 计价方式
		if (parentVO.getDef19() != null) {
			String jjfs = (String) getHyPubBO().findColValue("bd_defdoc",
					"name",
					" nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef19() + "'");
			list.add(OaWorkFlowUtil.listAddObj("jjfs", jjfs));
		}
		// 跟踪审计需求
		if (parentVO.getDef20() != null) {
			String gzsjxq = (String) getHyPubBO().findColValue("bd_defdoc",
					"name",
					" nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef20() + "'");
			list.add(OaWorkFlowUtil.listAddObj("gzsjxq", gzsjxq));
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
		OrgVO yhorgvo = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				userVO.getPk_org());
		list.add(OaWorkFlowUtil.listAddObj("yhsszzzj", userVO.getPk_org()));
		list.add(OaWorkFlowUtil.listAddObj("yhsszzbm", yhorgvo.getCode()));
		list.add(OaWorkFlowUtil.listAddObj("yhsszzmc", yhorgvo.getName()));
		// 合同类型
		if (null != parentVO.getDef17()) {
			list.add(OaWorkFlowUtil.listAddObj(
					"htlx",
					VOUtils.getDocName(ContractTypeHeadVO.class,
							parentVO.getDef17())));
		}
		// 供应商
		if (parentVO.getPk_supplier() != null) {
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
		// 经办人
		if (null != parentVO.getPk_transactor()) {
			String apppsnhName = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and pk_psndoc  = '"
							+ parentVO.getPk_transactor() + "'");

			list.add(OaWorkFlowUtil.listAddObj("jbr", apppsnhName));
		}
		// 经办部门
		if (null != parentVO.getPk_transact_dept_v()) {
			String sqbmName = (String) getHyPubBO().findColValue(
					"org_dept_v",
					"name",
					"nvl(dr,0) = 0 and pk_vid  = '"
							+ parentVO.getPk_transact_dept_v() + "'");
			list.add(OaWorkFlowUtil.listAddObj("jbbm", sqbmName));
		}
		// 能投代码
		// 单据状态
		String djztstr = parentVO.getBill_status() + "";
		int djztint = Integer.parseInt(djztstr);
		String djzt = "";
		if (djztint == -1) {
			djzt = "自由态";
		} else if (djztint == 0) {
			djzt = "审批未通过";
		} else if (djztint == 1) {
			djzt = "审批通过";
		} else if (djztint == 2) {
			djzt = "审批中";
		} else if (djztint == 3) {
			djzt = "已提交";
		}
		list.add(OaWorkFlowUtil.listAddObj("djzt", djzt));

		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	private JSONArray getDtaileDataMapNew(FeeBalanceBillVO temp, String def2,
			String transi_type) throws BusinessException {
		// 获取详细信息
		FeeBalanceBodyVO[] bvos = (FeeBalanceBodyVO[]) temp.getChildrenVO();
		Map bodyMap = getBody(bvos, getDef2(def2), transi_type);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
		return dtlistString;
	}

	private Map getBody(FeeBalanceBodyVO[] bvo, String def2, String transi_type)
			throws BusinessException {
		Map dtMap = new HashMap();
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (FeeBalanceBodyVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = new ArrayList();
			if (!"1".equals(def2) || "4D83-Cxx-84".equals(transi_type)) {
				workflowRequestTableFields = OaWorkFlowUtil.transBean2Map(temp);
			}
			// 其他字段------begin
			// 物料信息
			if (null != temp.getPk_material()) {
				MaterialVO materialVO = (MaterialVO) getHyPubBO()
						.queryByPrimaryKey(MaterialVO.class,
								temp.getPk_material());
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

			if (null != temp.getPk_project()) {
				// 项目
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
			if (null != temp.getPk_cbsnode()) {
				String cbsName = (String) getHyPubBO().findColValue(
						"bd_cbsnode",
						"name",
						"nvl(dr,0) = 0 and pk_cbsnode  = '"
								+ temp.getPk_cbsnode() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"cbsmc", cbsName));
			}
			// 任务状态
			if (temp.getDef1() != null) {
				String zbdw = (String) getHyPubBO().findColValue(
						"bd_supplier",
						"name",
						"nvl(dr,0) = 0 and pk_supplier = '" + temp.getDef1()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"zbdw", zbdw));
			}
			// 任务状态
			if (temp.getDef6() != null) {
				String rwzt = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef6()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"rwzt", rwzt));
			}
			// 能投
			// 项目编码

			// 项目编码
			/*
			 * String xmbm = (String) getHyPubBO().findColValue( "bd_project",
			 * "project_name ", "nvl(dr,0) = 0 and   pk_project   ='" +
			 * projectVO.getProject_code() + "'");
			 */
			if (null != temp.getPk_project()) {
				ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
						.queryByPrimaryKey(ProjectHeadVO.class,
								temp.getPk_project());
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"xmbm", projectVO.getProject_code()));// 项目编码
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"xmmc", projectVO.getProject_name()));// 项目名称
			}
			// workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("project_code",
			// projectVO.getProject_code()));
			// workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("xmbm",
			// projectVO.getProject_code()));// 项目编码
			// // 项目名称
			/*
			 * String xmmc = (String) getHyPubBO().findColValue( "bd_project",
			 * "project_name ", "nvl(dr,0) = 0 and   pk_project   ='" +
			 * projectVO.getProject_name() + "'");
			 */
			// workflowRequestTableFields.add(xmmc);
			// workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("project_name",
			// projectVO.getProject_name()));
			// workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("xmmc",
			// projectVO.getProject_name()) );//项目名称
			//

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
}
