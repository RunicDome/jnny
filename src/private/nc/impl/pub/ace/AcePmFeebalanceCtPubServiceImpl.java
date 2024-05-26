package nc.impl.pub.ace;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.framework.common.NCLocator;
import nc.bs.pm.pmfeebalancect.ace.bp.AcePmFeebalanceCtApproveBP;
import nc.bs.pm.pmfeebalancect.ace.bp.AcePmFeebalanceCtDeleteBP;
import nc.bs.pm.pmfeebalancect.ace.bp.AcePmFeebalanceCtInsertBP;
import nc.bs.pm.pmfeebalancect.ace.bp.AcePmFeebalanceCtSendApproveBP;
import nc.bs.pm.pmfeebalancect.ace.bp.AcePmFeebalanceCtUnApproveBP;
import nc.bs.pm.pmfeebalancect.ace.bp.AcePmFeebalanceCtUnSendApproveBP;
import nc.bs.pm.pmfeebalancect.ace.bp.AcePmFeebalanceCtUpdateBP;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.itf.pcm.contract.DbmSplitToContr;
import nc.itf.pcm.contract.pvt.IContract;
import nc.itf.pmbd.pub.IYearPlanForProject;
import nc.ui.pcm.utils.GetDao;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.bd.cbs.CBSNodeVO;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.org.OrgVO;
import nc.vo.pbm.yearplan.YearPlanBillVO;
import nc.vo.pbm.yearplan.YearPlanBodyVO;
import nc.vo.pbm.yearplan.YearPlanHeadVO;
import nc.vo.pcm.contract.ContrHeadVO;
import nc.vo.pcm.contract.ContrWorksVO;
import nc.vo.pcm.contract.ContractBillVO;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pm.feebalance.PmFeebalanceBVO;
import nc.vo.pm.feebalance.PmFeebalanceHVO;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.sm.UserVO;
import nc.ws.intf.ContractUtils;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.apache.commons.lang.StringUtils;

// 多编码合同
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
public abstract class AcePmFeebalanceCtPubServiceImpl {
	// 新增
	public AggPmFeebalance[] pubinsertBills(AggPmFeebalance[] clientFullVOs,
			AggPmFeebalance[] originBills) throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggPmFeebalance> transferTool = new BillTransferTool<AggPmFeebalance>(
					clientFullVOs);
			// XBX多编码合同保存校验 年度投资计划控制 项目类型--技改类项目
			if (checkIFSave(clientFullVOs)) {
				// 调用BP
				AcePmFeebalanceCtInsertBP action = new AcePmFeebalanceCtInsertBP();
				AggPmFeebalance[] retvos = action.insert(clientFullVOs);
				// 构造返回数据
				return transferTool.getBillForToClient(retvos);
			} else {
				return null;
			}
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// XBX多编码合同保存校验 年度投资计划控制 项目类型--技改类项目
	private boolean checkIFSave(AggPmFeebalance[] clientFullVOs)
			throws BusinessException {
		// TODO Auto-generated method stub
		boolean flag = Boolean.TRUE;
		for (AggPmFeebalance aggPmFeebalance : clientFullVOs) {
			// 查询档案，取校验组织
			Object conforg = (Object) getHyPubBO().findColValue(
					"bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
							+ "FROM BD_DEFDOCLIST WHERE CODE = 'HTSAVEORG')"
							+ " and code = '"
							+ aggPmFeebalance.getParent().getPk_org() + "'");// 组织
			if (conforg != null) {
				// 调用工具类校验是否可保存
				ContractUtils utils = new ContractUtils();
				String error = utils.checkIfSave(aggPmFeebalance, 1);
				if (StringUtils.isNotEmpty(error)) {
					throw new BusinessException(error);
				}
			}
		}
		return flag;
	}

	// 获取系统当前年份
	private static String getCurrentYear() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		Date date = new Date();
		return sdf.format(date);
	}

	// 删除
	public void pubdeleteBills(AggPmFeebalance[] clientFullVOs,
			AggPmFeebalance[] originBills) throws BusinessException {
		try {
			// 调用BP
			new AcePmFeebalanceCtDeleteBP().delete(clientFullVOs);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggPmFeebalance[] pubupdateBills(AggPmFeebalance[] clientFullVOs,
			AggPmFeebalance[] originBills) throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggPmFeebalance> transferTool = new BillTransferTool<AggPmFeebalance>(
					clientFullVOs);
			// XBX多编码合同保存校验 年度投资计划控制 项目类型--技改类项目
			if (checkIFSave(clientFullVOs)) {
				AcePmFeebalanceCtUpdateBP bp = new AcePmFeebalanceCtUpdateBP();
				AggPmFeebalance[] retvos = bp
						.update(clientFullVOs, originBills);
				// 构造返回数据
				return transferTool.getBillForToClient(retvos);
			}
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggPmFeebalance[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggPmFeebalance[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggPmFeebalance> query = new BillLazyQuery<AggPmFeebalance>(
					AggPmFeebalance.class);
			bills = query.query(queryScheme, null);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return bills;
	}

	/**
	 * 由子类实现，查询之前对queryScheme进行加工，加入自己的逻辑
	 * 
	 * @param queryScheme
	 */
	protected void preQuery(IQueryScheme queryScheme) {
		// 查询之前对queryScheme进行加工，加入自己的逻辑
	}

	// 提交
	public AggPmFeebalance[] pubsendapprovebills(
			AggPmFeebalance[] clientFullVOs, AggPmFeebalance[] originBills)
			throws BusinessException {
		AcePmFeebalanceCtSendApproveBP bp = new AcePmFeebalanceCtSendApproveBP();
		AggPmFeebalance[] retvos = bp.sendApprove(clientFullVOs, originBills);

		for (AggPmFeebalance temp : retvos) {
			PmFeebalanceHVO hvo = temp.getParent();
			String ifbl = temp.getParent().getDef4();// 是否补录
			JSONArray headData = null;
			JSONArray bodyData = null;
			String def2 = getDef2(hvo.getPk_org());
			// 能投增加中台4
			if ("1".equals(def2) || "4".equals(def2)) {
				if ((ifbl == null || "1001A2100000000B68C3".equals(ifbl))) {
					headData = getMainMap(temp, def2);
					String WorkFId = "";// 中台FLOWID
					String TableName = "";// 中台 TABLNAME
					OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(
							hvo.getPk_org(), "4Z01");
					if (oaVo != null && oaVo.getIsdr() == 0) {
						WorkFId = oaVo.getFlowid();
						TableName = oaVo.getTablename();
					}
					bodyData = getDtaileDataMap(temp, def2, TableName);
					WorkFlowBill bill = getWorkFlowBill(temp, def2, WorkFId);
					if (oaVo != null && oaVo.getIsdr() == 0) {
						bill.setDef3("ZT");
					}
					bill.setDef4(hvo.getBill_code());// 合同编码
					// 如果是中台，则调用中台oa接口
					OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
				}
			}
		}

		return retvos;

	}

	private WorkFlowBill getWorkFlowBill(AggPmFeebalance temp, String def2,
			String workFId) throws BusinessException {
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
			workFlowBill.setWorkflowName("多编码合同");
		} else {
			PmFeebalanceHVO hvo = (PmFeebalanceHVO) temp.getParentVO();
			UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
					UserVO.class, hvo.getBillmaker());
			PsndocVO psndoc = (PsndocVO) getHyPubBO().queryByPrimaryKey(
					PsndocVO.class, userVO.getPk_psndoc());
			workFlowBill.setDef4(hvo.getBill_code());
			if ("1".equals(getDef2(hvo.getPk_org()))) {
				workFlowBill.setDef5(userVO.getUser_code());
			} else {
				workFlowBill.setWorkflowId(workFId);
				workFlowBill.setDef5(psndoc.getId());
			}
			workFlowBill.setPk_group(hvo.getPk_group());
			workFlowBill.setPk_org(hvo.getPk_org());
			workFlowBill.setPk_bill(temp.getPrimaryKey());
			workFlowBill.setUser_name(userVO.getUser_name());
			workFlowBill.setUser_code(userVO.getUser_code());
			workFlowBill.setBill_status("1");
			workFlowBill.setCreationtime(new UFDate().toStdString());
			workFlowBill.setCreator(hvo.getCreator());
			/*
			 * if ("2".equals(getDef2(hvo.getPk_org()))) {
			 * workFlowBill.setWorkflowId("492"); } else
			 */if ("1".equals(getDef2(hvo.getPk_org()))) {
				workFlowBill.setWorkflowId("43");
			}
			workFlowBill.setBill_code(hvo.getBill_type());
			workFlowBill.setWorkflowName("多编码合同");
		}
		return workFlowBill;
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

	private JSONArray getMainMap(AggPmFeebalance temp, String def2)
			throws BusinessException {
		PmFeebalanceHVO hvo = temp.getParent();
		List list = new ArrayList();
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 全量字段
		/*
		 * if ("4".equals(def2) || "1".equals(def2)) { // 构造数据 list =
		 * OaWorkFlowUtil.transBean2Map(hvo); }
		 */
		// 构造数据
		if (!"1".equals(def2)) {
			list = OaWorkFlowUtil.transBean2Map(hvo);
			// 项目审批类型
			if (hvo.getDef11() != null) {
				String xmsplxbm = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"code",
						"nvl(dr,0) = 0 and pk_defdoc = '" + hvo.getDef11()
								+ "'");
				list.add(OaWorkFlowUtil.listAddObj("xmsplxbm", xmsplxbm));
				String xmsplx = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + hvo.getDef11()
								+ "'");
				list.add(OaWorkFlowUtil.listAddObj("xmsplx", xmsplx));
			}
		}
		// 用印类型
		if (hvo.getDef8() != null) {
			String yylx = (String) getHyPubBO().findColValue("bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + hvo.getDef8() + "'");
			list.add(OaWorkFlowUtil.listAddObj("yylx", yylx));
		}
		// 是否使用电子签章
		if (hvo.getDef7() != null) {
			String sfsydzqz = (String) getHyPubBO().findColValue("bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + hvo.getDef7() + "'");
			list.add(OaWorkFlowUtil.listAddObj("sfsydzqz", sfsydzqz));
		}
		// 是否审计
		String sfsj = "否";
		if (hvo.getDef10() != null && "Y".equals(hvo.getDef10())) {
			sfsj = "是";
		}
		list.add(OaWorkFlowUtil.listAdd("sfsj", sfsj));
		// ---------其他字段begin

		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				hvo.getPk_org());
		double bshje = 0;
		for (PmFeebalanceBVO itemvo : temp.getChildrenVO()) {
			if (itemvo.getDef3() != null) {
				bshje += Double.parseDouble(itemvo.getDef3().toString());
			}
		}
		if (null != orgVO) {
			/* 组织 */
			list.add(OaWorkFlowUtil.listAddObj("szgs", orgVO.getCode()));
			// 所属组织名称
			list.add(OaWorkFlowUtil.listAddObj("sszzmc", orgVO.getName()));
			if (!"2".equals(getDef2(hvo.getPk_org()))) {
				/* 项目组织 */
				list.add(OaWorkFlowUtil.listAddObj("xmzz", orgVO.getName()));
				// XBX2021-12-31 新增不含税金额 取表体合计
				list.add(OaWorkFlowUtil.listAddObj("bhsje", bshje + ""));
			}
		}
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				hvo.getBillmaker());
		if (null != userVO) {
			/* 制单人名称 */
			list.add(OaWorkFlowUtil.listAddObj("zdrmc", userVO.getUser_name()));

			/* 制单人编码 */
			list.add(OaWorkFlowUtil.listAddObj("zdrzj", userVO.getUser_code()));

			/* 制单日期 */
			list.add(OaWorkFlowUtil.listAddObj("zdrq", null != hvo
					.getBillmaketime() ? hvo.getDbilldate() + "" : ""));

			/* 制单人身份证 */
			Map sfzh = new HashMap();
			String id = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"id",
					"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc()
							+ "'");
			if (null == id) {
				throw new BusinessException("制单人身份证号码未维护");
			}
			sfzh.put("fieldName", "sfzh");
			sfzh.put("fieldValue", id);
			list.add(sfzh);
		}

		/* 单据主键 */
		list.add(OaWorkFlowUtil.listAddObj("djzj", hvo.getPm_feebalance()));

		/* 合同编号 */
		list.add(OaWorkFlowUtil.listAddObj("htbh", hvo.getBill_code()));

		/* 合同名称 */
		list.add(OaWorkFlowUtil.listAddObj("htmc", hvo.getBill_name()));

		/* 供应商主键 */
		list.add(OaWorkFlowUtil.listAddObj("gyszj", hvo.getPk_supplier_name()));

		/* 供应商名称 */
		if (null != hvo.getPk_supplier_name()) {
			String gname = (String) getHyPubBO().findColValue(
					"bd_supplier",
					"name",
					"nvl(dr,0) = 0 and pk_supplier  = '"
							+ hvo.getPk_supplier_name() + "'");
			list.add(OaWorkFlowUtil.listAddObj("gysmc", gname));
		}

		/* 签约部门主键 */
		list.add(OaWorkFlowUtil.listAddObj("qybmzj", hvo.getPk_qydept()));

		/* 签约部门名称 */
		String qname = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and pk_dept = '" + hvo.getPk_qydept() + "'");
		list.add(OaWorkFlowUtil.listAddObj("qybmmc", qname));

		/* 代控部门主键 */
		if (null != hvo.getPk_dkdept()) {
			list.add(OaWorkFlowUtil.listAddObj("dkbmzj", hvo.getPk_dkdept()));
		}

		/* 代控部门名称 */
		if (null != hvo.getPk_dkdept()) {
			String pk_dkdept = "0";
			String dkmc = (String) getHyPubBO().findColValue("org_dept",
					"name",
					"nvl(dr,0) = 0 and  pk_dept ='" + hvo.getPk_dkdept() + "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbmmc", dkmc));
		}

		/* 代控部门编码 */
		if (null != hvo.getPk_dkdept()) {
			String dkbm = (String) getHyPubBO().findColValue("org_dept",
					"code",
					"nvl(dr,0) = 0 and  pk_dept ='" + hvo.getPk_dkdept() + "'");
			list.add(OaWorkFlowUtil.listAddObj("dkbmbm", dkbm));
		}

		/* 合同金额 */
		if (null != hvo.getContractmoney()) {
			list.add(OaWorkFlowUtil.listAddObj("htje", hvo.getContractmoney()
					+ ""));
		}
		/* 签约日期 */
		if (null != hvo.getSigndate()) {
			list.add(OaWorkFlowUtil.listAddObj("qyrq", hvo.getSigndate()
					.getYear()
					+ "-"
					+ hvo.getSigndate().getStrMonth()
					+ "-"
					+ hvo.getSigndate().getStrDay()));
		}
		/* 合同类型 */
		String htlxwhere = " pk_contracttype = '" + hvo.getContracttype()
				+ "' and nvl(dr,0) = 0";
		String def10 = "0";
		if (null != (String) getHyPubBO().findColValue("pm_contracttype",
				"type_name", htlxwhere)) {
			def10 = (String) getHyPubBO().findColValue("pm_contracttype",
					"type_name", htlxwhere);
		}
		list.add(OaWorkFlowUtil.listAddObj("htlx", def10));

		/* 分管领导名称 */
		if (null != hvo.getPk_lead_name()) {
			list.add(OaWorkFlowUtil.listAddObj("fgldmc", hvo.getPk_lead_name()));
		}
		/* 项目经理名称 */
		if (null != hvo.getPk_manager_name()) {
			list.add(OaWorkFlowUtil.listAddObj("xmjlmc",
					hvo.getPk_manager_name()));
		}
		/* 招标方式 */
		if (null != hvo.getZbmethod()) {
			String zbgswhere = " pk_defdoc = '"
					+ hvo.getZbmethod()
					+ "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'cgfs' and nvl(dr,0) = 0) and nvl(dr,0) = 0";
			String def14 = "0";
			if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
					zbgswhere)) {
				def14 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
						zbgswhere);
			}
			list.add(OaWorkFlowUtil.listAddObj("zbfs", def14));
		}

		/* 开始日期 */
		if (null != hvo.getStartdate()) {
			list.add(OaWorkFlowUtil.listAddObj("ksrq", hvo.getStartdate().toString().substring(0, 10)));
		}

		/* 结束日期 */
		if (null != hvo.getEnddate()) {
			list.add(OaWorkFlowUtil.listAddObj("jsrq", hvo.getEnddate().toString().substring(0, 10)));
		}

		/* 签约人主键 */
		if (null != hvo.getPk_signer()) {
			list.add(OaWorkFlowUtil.listAddObj("qyrzj", hvo.getPk_signer()));
		}
		/* 签约人名称 */
		if (null != hvo.getPk_signer_name()) {
			String qyrname = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and pk_psndoc = '" + hvo.getPk_signer_name()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("qyrmc", qyrname == null ? "0"
					: qyrname));
		}

		/* 单据状态 */
		list.add(OaWorkFlowUtil.listAddObj("djzt", hvo.getFstatusflag() + ""));

		/* 原合同金额 */
		if (null != hvo.getProtocontractmoney()) {
			list.add(OaWorkFlowUtil.listAddObj(
					"yhtje",
					hvo.getProtocontractmoney() == null ? "0.00" : hvo
							.getProtocontractmoney() + ""));
		}

		/* 已付款金额 */
		if (null != hvo.getPaymoney()) {
			list.add(OaWorkFlowUtil.listAddObj("yfkje",
					hvo.getPaymoney() == null ? "0.00" : hvo.getPaymoney() + ""));
		}

		/* 累计付款金额 */
		if (null != hvo.getAddpaymoney()) {
			list.add(OaWorkFlowUtil.listAddObj(
					"ljfkje",
					hvo.getAddpaymoney() == null ? "0.00" : hvo
							.getAddpaymoney() + ""));
		}

		/* 集团 */
		// Map jt =OaWorkFlowUtil.listAdd("jt",hvo.getPk_group());
		// list.add(jt);

		/* 项目组织 */
		/*
		 * Map xmzz = OaWorkFlowUtil.listAdd("xmzz", hvo.getPk_org());
		 * list.add(xmzz);
		 */

		/* 项目组织最新版本 */
		list.add(OaWorkFlowUtil.listAddObj("xmzzzxbb", hvo.getPk_org_v()));

		/* 单据类型 */
		list.add(OaWorkFlowUtil.listAddObj("djlx", hvo.getBill_type()));

		/* 单据日期 */
		if (null != hvo.getDbilldate()) {
			list.add(OaWorkFlowUtil.listAddObj("djrq", hvo.getDbilldate() + ""));
		}

		/* 审批人 */
		if (null != hvo.getAuditor()) {
			list.add(OaWorkFlowUtil.listAddObj("spr", hvo.getAuditor()));
		}

		/* 审批日期 */
		if (null != hvo.getAudittime()) {
			list.add(OaWorkFlowUtil.listAddObj("sprq", hvo.getAudittime() + ""));
		}

		/* 审批意见 */
		if (null != hvo.getCheck_opinion()) {
			list.add(OaWorkFlowUtil.listAddObj("spyj", hvo.getCheck_opinion()));
		}

		/* 创建人 */
		if (null != hvo.getCreator()) {
			list.add(OaWorkFlowUtil.listAddObj("cjr", hvo.getCreator()));
		}

		/* 创建时间 */
		list.add(OaWorkFlowUtil.listAddObj("cjsj", hvo.getCreationtime() + ""));

		/* 最后修改人 */
		if (null != hvo.getModifier()) {
			list.add(OaWorkFlowUtil.listAddObj("zhxgr", hvo.getModifier()));
		}

		/* 最后修改时间 */
		if (null != hvo.getModifiedtime()) {
			list.add(OaWorkFlowUtil.listAddObj("zhxgsj", hvo.getModifiedtime()
					+ ""));
		}

		/* 交易类型 */
		if (null != hvo.getPk_transitype()) {
			list.add(OaWorkFlowUtil.listAddObj("jylx", hvo.getPk_transitype()));
		}

		/* 交易类型编码 */
		if (null != hvo.getTransitypecode()) {
			list.add(OaWorkFlowUtil.listAddObj("jylxbm",
					hvo.getTransitypecode()));
		}

		/* 单据类型主键 */
		list.add(OaWorkFlowUtil.listAddObj("djlxzj", hvo.getPk_bill_type()));

		/* 备注 */
		list.add(OaWorkFlowUtil.listAddObj("bz", hvo.getVmemo()));

		String Def12 = "0";
		if (null != hvo.getDef6()) {
			String strWhere = "pk_defdoc = '" + hvo.getDef6()
					+ "' and nvl(dr,0) = 0 ";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				Def12 = defdoc[0].getCode();
				list.add(OaWorkFlowUtil.listAddObj("htmbmc",
						defdoc[0].getName()));
			}
			// 契约锁合同模板
			String qyshtmb_id = (String) getHyPubBO().findColValue("bd_defdoc",
					"mnecode",
					"nvl(dr,0) = 0 and pk_defdoc = '" + hvo.getDef6() + "'");
			if (qyshtmb_id == null || "".equals(qyshtmb_id) || qyshtmb_id == "")
				qyshtmb_id = "2936164539873645088";
			list.add(OaWorkFlowUtil.listAddObj("qyshtmb", qyshtmb_id));
		}
		list.add(OaWorkFlowUtil.listAddObj("htmb", Def12));
		// 热力新增字段 2023-05-26
		// 工程地址
		list.add(OaWorkFlowUtil.listAddObj("gcdz", hvo.getDef21()));
		// 税率
		list.add(OaWorkFlowUtil.listAddObj("sl", hvo.getDef22()));
		// 不含税金额
		list.add(OaWorkFlowUtil.listAddObj("bhsje", hvo.getDef23()));
		// 税额
		list.add(OaWorkFlowUtil.listAddObj("se", hvo.getDef24()));
		// 乙方开户银行名称
		list.add(OaWorkFlowUtil.listAddObj("yfkhyhmc", hvo.getDef25()));
		// 乙方开户银行账号
		list.add(OaWorkFlowUtil.listAddObj("yfkhyhzh", hvo.getDef26()));
		// 甲方签收人
		list.add(OaWorkFlowUtil.listAddObj("jfqsr", hvo.getDef27()));
		// 甲方联系电话
		list.add(OaWorkFlowUtil.listAddObj("jfllxdh", hvo.getDef28()));
		// 乙方签收人
		list.add(OaWorkFlowUtil.listAddObj("yfqsr", hvo.getDef29()));
		// 乙方联系电话
		list.add(OaWorkFlowUtil.listAddObj("yflxdh", hvo.getDef30()));
		// 乙方指定送达地址
		list.add(OaWorkFlowUtil.listAddObj("yfzdsddd", hvo.getDef31()));
		// 工程规模（监理）
		list.add(OaWorkFlowUtil.listAddObj("gcgm", hvo.getDef32()));
		// 程概算投资额或建筑安装工程费（监理）
		list.add(OaWorkFlowUtil.listAddObj("cgstze", hvo.getDef33()));
		// 总监理工程师姓名（监理）
		list.add(OaWorkFlowUtil.listAddObj("zjlgcsmx", hvo.getDef34()));
		// 总监理工程师身份证号码（监理）
		list.add(OaWorkFlowUtil.listAddObj("zjlgcssfzh", hvo.getDef35()));
		// 总监理工程师注册号（监理）
		list.add(OaWorkFlowUtil.listAddObj("zjlgcszch", hvo.getDef36()));
		// 监理费率（监理）
		list.add(OaWorkFlowUtil.listAddObj("jlfl", hvo.getDef37()));
		// 委托人代表（监理）
		list.add(OaWorkFlowUtil.listAddObj("wtrdb", hvo.getDef38()));
		// 设计规模及内容（设计）
		list.add(OaWorkFlowUtil.listAddObj("sjgmjnr", hvo.getDef39()));
		// 承包范围和内容（施工）
		list.add(OaWorkFlowUtil.listAddObj("cbfwhnr", hvo.getDef40()));
		// 甲供材费用（施工）
		list.add(OaWorkFlowUtil.listAddObj("jgcfy", hvo.getDef41()));
		// 安装费（施工）
		list.add(OaWorkFlowUtil.listAddObj("azf", hvo.getDef42()));
		// 承包施工方式（施工）
		list.add(OaWorkFlowUtil.listAddObj("cbsgfs", hvo.getDef43()));
		// 工期日历天数（施工）
		list.add(OaWorkFlowUtil.listAddObj("gqrlts", hvo.getDef44()));
		// 审计单位（施工）
		list.add(OaWorkFlowUtil.listAddObj("sjdw", hvo.getDef45()));
		// 技改年度（技改维修）
		list.add(OaWorkFlowUtil.listAddObj("jgnd", hvo.getDef46()));

		// 阶段（设计）
		if (null != hvo.getDef47()) {
			String strWhere = "pk_defdoc = '" + hvo.getDef47()
					+ "' and nvl(dr,0) = 0 ";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				list.add(OaWorkFlowUtil.listAddObj("jdbm",
						defdoc[0].getCode()));
				list.add(OaWorkFlowUtil.listAddObj("jdmc",
						defdoc[0].getName()));
			}
		}
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	private JSONArray getDtaileDataMap(AggPmFeebalance temp, String def2,
			String tableName) throws BusinessException {
		Map ctPuBMap = getPmFeebalanceBVO(temp, def2, tableName);
		// JSONArray dtlistString = JSONArray.fromObject(ctPuBMap);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(ctPuBMap, jsonConfig);
		return dtlistString;
	}

	private Map getPmFeebalanceBVO(AggPmFeebalance aggVO, String def2,
			String tableName) throws BusinessException {
		Map dtMap = new HashMap();
		PmFeebalanceHVO hvo = (PmFeebalanceHVO) aggVO.getParentVO();
		/*
		 * if ("2".equals(getDef2(hvo.getPk_org()))) { dtMap.put("tableDBName",
		 * "formtable_main_513_dt1"); } else
		 */if ("1".equals(getDef2(hvo.getPk_org()))) {
			dtMap.put("tableDBName", "formtable_main_46_dt1");
		} else {
			dtMap.put("tableDBName", tableName + "_dt1");
		}
		List workflowRequestTableRecords = new ArrayList();
		PmFeebalanceBVO[] bodyVOS = (PmFeebalanceBVO[]) aggVO.getChildrenVO();
		for (PmFeebalanceBVO temp : bodyVOS) {
			List workflowRequestTableFields = new ArrayList();
			// 构造数据
			if (!"1".equals(def2)) {
				workflowRequestTableFields = OaWorkFlowUtil.transBean2Map(temp);
			}
			Map workflowRequestTableFieldsMap = new HashMap();

			/* 子表主键 */
			/*
			 * Map zbzj
			 * =OaWorkFlowUtil.listAdd("zbzj",temp.getPm_feebalance_b());
			 * workflowRequestTableFields.add(zbzj);
			 */

			/* 项目编码 */
			String project_code = (String) getHyPubBO().findColValue(
					"bd_project",
					"project_code",
					"nvl(dr,0) = 0 and   pk_project  ='" + temp.getPk_project()
							+ "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("xmbm",
					project_code));

			/* 项目名称 */
			String project_name = (String) getHyPubBO().findColValue(
					"bd_project",
					"project_name",
					"nvl(dr,0) = 0 and   pk_project  ='" + temp.getPk_project()
							+ "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("xmmc",
					project_name));

			/* CBS名称 */
			String cbs = (String) getHyPubBO().findColValue(
					"bd_cbsnode",
					"name",
					"nvl(dr,0) = 0 and   pk_cbsnode  ='" + temp.getPk_cbsnode()
							+ "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("cbsmc",
					cbs));

			/* 物料编码 */
			String material_code = (String) getHyPubBO().findColValue(
					"bd_material_v",
					"code",
					"nvl(dr,0) = 0 and pk_source ='" + temp.getMaterial_v()
							+ "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("wlbm",
					material_code));

			/* 物料名称 */
			String material_name = (String) getHyPubBO().findColValue(
					"bd_material_v",
					"name",
					"nvl(dr,0) = 0 and pk_material  ='" + temp.getMaterial_v()
							+ "'");
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("wlmc",
					material_name));

			/* 金额 */
			if (null != temp.getMoney()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("je",
						temp.getMoney() + ""));
			}

			/* 单价 */
			if (null != temp.getUnitmoney()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("dj",
						temp.getUnitmoney() + ""));
			}

			/* 数量 */
			if (null != temp.getNum()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sl",
						temp.getNum() + ""));
			}
			/* 单位 */
			if (null != temp.getPk_measdoc()) {
				String sdw = (String) getHyPubBO().findColValue(
						"bd_measdoc",
						"name",
						"nvl(dr,0) = 0 and   pk_measdoc  ='"
								+ temp.getPk_measdoc() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("dw",
						sdw));
			}
			/* 审计前合同额 */
			if (null != temp.getDef4()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"sjqhte",
						new UFDouble(temp.getDef4() == null ? "0.00" : temp
								.getDef4()).setScale(2, UFDouble.ROUND_HALF_UP)
								+ ""));
			}

			/* 原合同金额 */
			if (null != temp.getProtocontractmoney()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"yhtje", temp.getProtocontractmoney() + ""));
			}
			/* 已付款金额 */
			if (null != temp.getPaymoney()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"yfkje", temp.getPaymoney() + ""));
			}
			/* 税率 */
			if (null != temp.getDef5()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sl1",
						temp.getDef5()));
			}
			/* 税额 */
			if (null != temp.getDef4()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("se",
						temp.getDef4()));
			}
			/* 不含税金额 */
			if (null != temp.getDef3()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bhsje", temp.getDef3()));
			}

			/* 备注 */
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bz",
					temp.getDef6()));

			/* 管径（mm）（设计） */
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("gj",
					temp.getDef7()));
			/* 起止点（设计） */
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("qzd",
					temp.getDef8()));
			/* 沟槽长度（km）（设计） */
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("gccd",
					temp.getDef9()));
			/* 初设费（万元）（设计） */
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("csf",
					temp.getDef10()));
			/* 站网名称（技改维修） */
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("zwmc",
					temp.getDef11()));
			/* 维保内容（技改维修） */
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("wbnr",
					temp.getDef12()));
			/* 用热地址（技改维修） */
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("yrdz",
					temp.getDef13()));

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	// 收回
	public AggPmFeebalance[] pubunsendapprovebills(
			AggPmFeebalance[] clientFullVOs, AggPmFeebalance[] originBills)
			throws BusinessException {
		AcePmFeebalanceCtUnSendApproveBP bp = new AcePmFeebalanceCtUnSendApproveBP();
		AggPmFeebalance[] retvos = bp.unSend(clientFullVOs, originBills);
		unOaCommit(retvos);
		return retvos;
	};

	private void unOaCommit(AggPmFeebalance[] aggVO) throws BusinessException {
		for (AggPmFeebalance temp : aggVO) {
			PmFeebalanceHVO hvo = (PmFeebalanceHVO) temp.getParentVO();
			OaWorkFlowUtil.backOaWorkFlow(temp.getPrimaryKey());
		}
	}

	// 审批后自动拆分
	public AggPmFeebalance[] pubapprovebills(AggPmFeebalance[] clientFullVOs,
			AggPmFeebalance[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AcePmFeebalanceCtApproveBP bp = new AcePmFeebalanceCtApproveBP();
		AggPmFeebalance[] retvos = bp.approve(clientFullVOs, originBills);
		// 审批完成后自动拆分为清单发包合同
		Set bill_codes = new HashSet<>();// 记录已生成合同
		Set err_bill_codes = new HashSet<>();// 记录报错合同
		for (AggPmFeebalance aggPmFeebalance : retvos) {
			if (!(aggPmFeebalance.getParent().getDef19() != null && "Y"
					.equals(aggPmFeebalance.getParent().getDef19()))) {
				// 调用多编码合同拆分接口，传入单个多编码合同
				DbmSplitToContr split = NCLocator.getInstance().lookup(
						DbmSplitToContr.class);
				try {
					String res = split.SplitToContr(aggPmFeebalance);
					if (StringUtils.isEmpty(res)) {
						bill_codes.add(aggPmFeebalance.getParent()
								.getBill_code() + "拆分成功！");
					} else {
						err_bill_codes.add(aggPmFeebalance.getParent()
								.getBill_code() + res);
						String[] userObj = { ContractBillVO.class.getName(),
								ContrHeadVO.class.getName(),
								ContrWorksVO.class.getName() };
						String strWhere = " NVL(DR,0) = 0 AND HDEF53 = '"
								+ aggPmFeebalance.getParent().getBill_code()
								+ "'";
						ContractBillVO[] aggvos = (ContractBillVO[]) new HYPubBO()
								.queryBillVOByCondition(userObj, strWhere);
						if (aggvos != null && aggvos.length > 0) {
							IContract qditf = NCLocator.getInstance().lookup(
									IContract.class);
							qditf.deleteContr(aggvos);
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (err_bill_codes.size() > 0) {
			throw new BusinessException(err_bill_codes.toString() + "拆分失败！");
		}
		return retvos;
	}

	// 弃审

	public AggPmFeebalance[] pubunapprovebills(AggPmFeebalance[] clientFullVOs,
			AggPmFeebalance[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AcePmFeebalanceCtUnApproveBP bp = new AcePmFeebalanceCtUnApproveBP();
		AggPmFeebalance[] retvos = bp.unApprove(clientFullVOs, originBills);
		return retvos;
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