package nc.ui.pcm.feebalance.action;

import java.awt.event.ActionEvent;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.cmp.utils.BillcodeGenerater;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.oa.web.GetDBMJson;
import nc.oa.web.RLContractTZService;
import nc.ui.pcm.feebalance.util.GenPushJDKUtil;
import nc.ui.pcm.utils.GetDao;
import nc.ui.pm.util.BudgetAlterInfoUtil;
import nc.ui.pmpub.action.PMApproveAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.cbs.CBSNodeVO;
import nc.vo.pbm.budget.BudgetCBSBodyVO;
import nc.vo.pbm.budget.BudgetFactorBodyVO;
import nc.vo.pbm.budget.BudgetHeadVO;
import nc.vo.pcm.feebalance.FeeBalanceBillVO;
import nc.vo.pcm.feebalance.FeeBalanceBodyVO;
import nc.vo.pcm.feebalance.FeeBalanceHeadVO;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
//import nc.ui.pcm.feebalance.util.GenPushJDKUtil;
//import nc.uap.wfm.prodef.robot.SequenceGenerator;
//import nc.jdbc.framework.generator.SequenceGenerator;

@SuppressWarnings({ "restriction" })
public class ApproveAction extends PMApproveAction {
	// 多编码合同审批
	private static final long serialVersionUID = 1L;
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);

	protected AbstractBill[] produceLightVO(AbstractBill[] newVO) {
		fillInfoAfterLight(newVO);
		return newVO;
	}

	protected void processReturnObj(Object[] pretObj) throws Exception {
		super.processReturnObj(pretObj);

		BudgetAlterInfoUtil.processCheckResulrObj(pretObj, getModel());
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		// TODO 自动生成的方法存根

		FeeBalanceBillVO fee = null;
		fee = (FeeBalanceBillVO) editor.getValue();
		if (fee.getPrimaryKey() == null) {
			fee = (FeeBalanceBillVO) getModel().getSelectedData();
		}
		FeeBalanceHeadVO headvo = (FeeBalanceHeadVO) fee.getParentVO();
		// 0428 XBX新增 --0610暂时屏蔽
		CircularlyAccessibleValueObject[] childVOs = fee.getChildrenVO(); // 子表数据
		if (childVOs.length > 0) {
			FeeBalanceBodyVO bodyvo = (FeeBalanceBodyVO) childVOs[0]; // 子表VO
			if (bodyvo.getPk_wbs() != null && !"".equals(bodyvo)) {
				for (int j = 0; j < childVOs.length; j++) {
					FeeBalanceBodyVO feeBalanceBody = (FeeBalanceBodyVO) childVOs[j]; // 子表VO
					// feeBalanceBody.getDef1();// 任务状态
					// feeBalanceBody.getPk_wbs();// PK_WBS
					String sql = "";
					//多编码合同
					if(!"4D83-01".equals(headvo.getTransi_type())){
					if("4D83-Cxx-02".equals(headvo.getTransi_type())){
						sql = "SELECT MIN(KSRQ) FROM (SELECT DISTINCT ZB.DEF15 AS KSRQ,MX.PK_FEEBALANCE_B,MIN(MX.MONEY) AS LJWC FROM "
								+ "PM_FEEBALANCE ZB LEFT JOIN PM_FEEBALANCE_B MX ON ZB.PK_FEEBALANCE = MX.PK_FEEBALANCE "
								+ "WHERE ZB.DR = 0 AND MX.DR = 0 AND ZB.TRANSI_TYPE = '"
								+ headvo.getTransi_type()
								+ "' "
								+ "AND MX.PK_WBS = '"
								+ feeBalanceBody.getPk_wbs()
								+ "' GROUP BY ZB.PK_FEEBALANCE,"
								+ "MX.PK_FEEBALANCE_B,ZB.DEF15)";
					}else{
						sql = "SELECT MIN(KSRQ) FROM (SELECT DISTINCT MX.DEF4 AS KSRQ,MX.PK_FEEBALANCE_B,MIN(MX.MONEY) AS LJWC FROM "
							+ "PM_FEEBALANCE ZB LEFT JOIN PM_FEEBALANCE_B MX ON ZB.PK_FEEBALANCE = MX.PK_FEEBALANCE "
							+ "WHERE ZB.DR = 0 AND MX.DR = 0 AND ZB.TRANSI_TYPE = '"
							+ headvo.getTransi_type()
							+ "' "
							+ "AND MX.PK_WBS = '"
							+ feeBalanceBody.getPk_wbs()
							+ "' GROUP BY ZB.PK_FEEBALANCE,"
							+ "MX.PK_FEEBALANCE_B,MX.DEF4)";
					}
					List<Object[]> wbsls = getDao.query(sql);

					if (wbsls != null && wbsls.size() > 0
							&& wbsls.get(0) != null) {
						String ksri = wbsls.get(0)[0] + "";
						String wbsstartdate = "";// 进度计划编制计划开始日期
						String wbsfinishdate = "";// 进度计划编制计划完成日期
						String getwbsql = "SELECT PLAN_START_DATE,PLAN_FINISH_DATE FROM PM_WBS WHERE PK_WBS = '"
								+ feeBalanceBody.getPk_wbs() + "'";
						List<Object[]> getls = getDao.query(getwbsql);
						if (getls != null && getls.size() > 0) {
							wbsstartdate = getls.get(0)[0] + "";
							wbsfinishdate = getls.get(0)[1] + "";
						}
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");

						System.out.println("wbsstartdate==" + wbsstartdate
								+ "wbsfinishdate==" + wbsfinishdate);
						double jd = 0.0;
						// 采购招标结果登记(-22)和招标结果登记 (-04) 自定义项7
						if (headvo.getTransi_type().equals("4D83-Cxx-22")
								|| headvo.getTransi_type()
										.equals("4D83-Cxx-04")) {
							jd = Double.parseDouble(feeBalanceBody.getDef7()
									+ "");
						} else {
							jd = feeBalanceBody.getMoney().toDouble();
						}
						if (jd == 100) {
							String updsql = "UPDATE PM_WBS SET PLAN_START_DATE = '"
									+ ksri
									+ "' WHERE PK_WBS = '"
									+ feeBalanceBody.getPk_wbs() + "'";
							System.out.println("更新SQL：" + updsql);
							String upsql = "";
							if("4D83-Cxx-02".equals(headvo.getTransi_type())){
								 upsql = "UPDATE PM_WBS SET PLAN_FINISH_DATE "
											+ "= '"
											+ headvo.getDef16()
											+ "' WHERE PK_WBS = '"
											+ feeBalanceBody.getPk_wbs() + "'";
							}else{
								 upsql = "UPDATE PM_WBS SET PLAN_FINISH_DATE "
											+ "= '"
											+ feeBalanceBody.getDef5()
											+ "' WHERE PK_WBS = '"
											+ feeBalanceBody.getPk_wbs() + "'";
							}
							System.out.println("更新SQL2：" + upsql);
							getDao.executeUpdate(updsql);
							getDao.executeUpdate(upsql);
						} else {
							if (sdf.parse(ksri).compareTo(
									sdf.parse(wbsfinishdate)) == 1) {
								/*
								 * ExceptionUtils .wrappBusinessException(
								 * "实际开始日期大于计划完成日期，请修改计划完成日期！");
								 */
								MessageDialog.showErrorDlg(null, "提示",
										"实际开始日期大于计划完成日期，请修改计划完成日期！");
								return;
							}
							String updsql = "UPDATE PM_WBS SET PLAN_START_DATE = '"
									+ ksri
									+ "' WHERE PK_WBS = '"
									+ feeBalanceBody.getPk_wbs() + "'";
							System.out.println("更新SQL：" + updsql);

							if (feeBalanceBody.getDef5() != null
									&& sdf.parse(feeBalanceBody.getDef5())
											.compareTo(sdf.parse(wbsstartdate)) == -1) {
								// if(Date.valueOf(wbsstartdate).after(Date.valueOf(feeBalanceBody.getDef5()))){
								/*
								 * ExceptionUtils .wrappBusinessException(
								 * "实际完成日期小于计划开始日期，请修改计划开始日期！");
								 */
								MessageDialog.showErrorDlg(null, "提示",
										"实际完成日期小于计划开始日期，请修改计划开始日期！");
								return;
							}
							String upsql = "UPDATE PM_WBS SET PLAN_FINISH_DATE "
									+ "= '"
									+ feeBalanceBody.getDef5()
									+ "' WHERE PK_WBS = '"
									+ feeBalanceBody.getPk_wbs() + "'";
							System.out.println("更新SQL2：" + upsql);
							getDao.executeUpdate(updsql);
							if (feeBalanceBody.getDef5() != null) {
								getDao.executeUpdate(upsql);
							}
						}

					}
				}
				/*GenPushJDKUtil util = new GenPushJDKUtil();// 推进度填报
				util.pushTaskProcessByFee(fee);*/
				}
			}
		}

		super.doAction(e);
		// 20230513 招标结果登记审批后推送预算   屏蔽XBX
		/*if ("4D83-Cxx-04".equals(headvo.getTransi_type())) {
			// 项目组织
			String pkOrg = headvo.getPk_org();
			CircularlyAccessibleValueObject[] childrenVOs = fee.getChildrenVO(); // 子表数据
			for (int j = 0; j < childrenVOs.length; j++) {
				FeeBalanceBodyVO feeBalanceBody = (FeeBalanceBodyVO) childrenVOs[j]; // 子表VO
				// 取中标登记表里面的CBSOID
				String cbsOID = feeBalanceBody.getPk_cbsnode() + "";
				// 取中标登记里面的金额
				UFDouble feemoney = feeBalanceBody.getMoney();
				// 查询 成本预算表中有该是否有该项目信息
				String budgetSQL = "select pk_budget from pm_budget where pk_project = '"
						+ feeBalanceBody.getPk_project()
						+ "' and dr = 0 and last_v_flag = 'Y'";
				System.out.println("budgetSQL===" + budgetSQL);
				IUAPQueryBS bs = NCLocator.getInstance().lookup(
						IUAPQueryBS.class);
				String pk_budget = bs.executeQuery(budgetSQL,
						new ColumnProcessor()) + "";
				List<Object[]> budCbsArrList;
				List<Object[]> budFactorArrList;
				if ((pk_budget != null) && (!"".equals(pk_budget))
						&& (!"null".equals(pk_budget))) { // 如果有项目预算

					// 判断核算要素里面有没有？
					String pk_project = feeBalanceBody.getPk_project() + "";
					String budFactorSql = "select a.pk_budgetfactor, a.budget_mny, a.bala_mny from pm_budgetfactor a left join pm_budget b on a.pk_budget = b.pk_budget where b.pk_project = '"
							+ pk_project
							+ "' and a.pk_cbsnode = '"
							+ cbsOID
							+ "'and a.dr = 0 and b.last_v_flag = 'Y' and b.dr = 0"; // 如果有返回主键，为修改做准备
					budFactorArrList = (List<Object[]>) bs.executeQuery(
							budFactorSql, new ArrayListProcessor());
					System.out.println("budFactorSql===" + budFactorSql);
					System.out.println("budFactorArrList长度："
							+ budFactorArrList.size());
					if (budFactorArrList.size() <= 0) {
						// if ((budFactorOID !=
						// null)&&(!"".equals(budFactorOID))){ //如果没有CBS
						// 如果没有 新增一条明细 CBS明细
						insertFactorCbs(pk_budget, feeBalanceBody);

					} else {
						// 如果有 取 预算值 和 当前值相加 并更新
						
						 * System.out.println("v.get(0)==" +
						 * budFactorArrList.get(0));
						 
						Object[] obj = budFactorArrList.get(0);
						String budgetfactorOID = (String) obj[0]; // 预算明细CBS的OID
						UFDouble budgetfactormny = new UFDouble(
								Double.parseDouble(obj[1] + "")); // 预算明细 金额
						UFDouble balafactormny = new UFDouble(
								Double.parseDouble(obj[2] + "")); // 预算余额
						budgetfactormny = budgetfactormny.add(feemoney);

						balafactormny = balafactormny.add(feemoney);
						System.out.println("budgetfactormny=="
								+ budgetfactormny);
						// 修改核素要素金额
						updateFactorCbs(budgetfactorOID, pk_budget,
								budgetfactormny, balafactormny, cbsOID, pkOrg);
					}

					// 查询预算明细里面有无 CBS 预算明细
					String cbsSql = "select pk_budgetcbs, budget_mny, bala_mny from  pm_budgetcbs where pk_budget = '"
							+ pk_budget
							+ "' and pk_cbsnode = '"
							+ cbsOID
							+ "'  and dr = 0"; // 如果有返回主键，为修改做准备
					budCbsArrList = (List<Object[]>) bs.executeQuery(cbsSql,
							new ArrayListProcessor());
					if (budCbsArrList.size() <= 0) {
						// if ((budCbsOID != null)&&(!"".equals(budCbsOID))){
						// //如果没有CBS
						// 如果没有 新增一条明细 CBS明细
						insertBudgetCbs(pk_budget, feeBalanceBody);

					} else {
						// 如果有 取 预算值 和 当前值相加 并更新
						Object[] obj = budCbsArrList.get(0);
						String budgetcbs = (String) obj[0]; // 预算明细CBS的OID
						String aaa = obj[1] + "";
						Double db = Double.parseDouble(aaa);
						UFDouble budgetmny = new UFDouble(db); // 预算明细 金额
						budgetmny = budgetmny.add(feemoney);
						String bbb = obj[2] + "";
						Double db2 = Double.parseDouble(bbb);
						UFDouble balamny = new UFDouble(db2); // 预算余额
						balamny = balamny.add(feemoney);
						// 修改 主键，主表主键，预算金额，预算余额
						updateBudgetCbs(budgetcbs, pk_budget, budgetmny,
								balamny, feemoney, cbsOID);
					}
				} else {
					// 如果没有项目预算
					AddBudgetBill(headvo, feeBalanceBody);
				}

				// 修改预算主表 预算总金额 计算方式
				updateBudgetMny(feeBalanceBody.getPk_project());
			}
		}

	}

	// 修改预算主表 总金额 计算方式：取核素要素表中 所有子表中的数据？是否正确需进一步确认
	private void updateBudgetMny(String projectOID) throws DAOException {
		String getSql = "select sum(budget_mny) as totalmny from pm_budgetfactor where pk_budget in (select pk_budget from pm_budget where pk_project='"
				+ projectOID + "' and dr=0)";
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		List<Object[]> lstObj = getDao.query(getSql);
		String budgetmny = lstObj.get(0)[0] + "";
		if (!"".equals(budgetmny) && !"null".equals(budgetmny)
				&& !"0".equals(budgetmny)) {
			String uptSql = "update pm_budget set  budget_mny = '"
					+ budgetmny
					+ "' where pk_budget in (select pk_budget from pm_budget where pk_project='"
					+ projectOID + "' and dr=0)";
			getDao.updatevo(uptSql);
		}*/

	}

	/*// 添加预算主表，子表信息
	private void AddBudgetBill(FeeBalanceHeadVO headVO,
			FeeBalanceBodyVO feeBalanceBody) throws Exception {

		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class); // 获取DAO执行插入
		// 预算单主表
		BudgetHeadVO budgetHeadVO = new BudgetHeadVO();
		// 登记单 审批通过时间
		UFDate audittime = headVO.getAudittime();
		UFDate billtime = headVO.getBillmaketime();// 预算编制日期取制单日期
		String auditor = headVO.getAuditor();// 制单人
		String pk_org = headVO.getPk_org() + ""; // 取项目组织
		String pk_org_v = headVO.getPk_org_v() + ""; // 取项目组织

		// String billOID = getBillPK(); //生成OID
		// budgetHeadVO.setPk_budget(billOID); //预算单主键
		// 生成单据号
		BillcodeGenerater gene = new BillcodeGenerater();
		String billno = gene.getBillCode("4D18", "0001A1100000000001QS",
				pk_org, null, null);
		budgetHeadVO.setPk_group("0001A1100000000001QS"); // 集团
		budgetHeadVO.setMake_date(billtime);// 预算编制日期
		budgetHeadVO.setAudittime(audittime);// 审批日期
		budgetHeadVO.setAuditor(auditor);// 审批人
		budgetHeadVO.setPk_org(pk_org);// 组织
		budgetHeadVO.setPk_org_v(pk_org_v);// 项目组织
		budgetHeadVO.setBill_code(billno); // 单据号
		budgetHeadVO.setBill_status(9); // 单据状态
		budgetHeadVO.setIn_budget(new UFDouble(0)); // 总收入预算
		budgetHeadVO.setPk_currtype("1002Z0100000000001K1"); // 币种

		String projectOID = feeBalanceBody.getPk_project() + "";
		budgetHeadVO.setPk_project(projectOID);// 项目OID

		budgetHeadVO.setBudget_mny(new UFDouble(10000)); // 预算总金额
		budgetHeadVO.setVersion(1); // 版本号 1
		budgetHeadVO.setDr(0);
		String cbsOID = feeBalanceBody.getPk_cbsnode() + "";
		budgetHeadVO.setPk_default_cbsnode(cbsOID);// 默认CBS
		// 编制模式 1
		budgetHeadVO.setMake_mode(1);
		budgetHeadVO.setPk_costbgtctrl("0001A21000000000PJKM");// 预算控制
		budgetHeadVO.setCtrl_model(0); // 控制方式
		budgetHeadVO.setBill_type("4D18"); // 单据类型
		budgetHeadVO.setLast_v_flag(new UFBoolean("Y")); // 最新版本标志 必填要不无法查询
		budgetHeadVO.setPk_transitype("0001A21000000000PG0C");
		budgetHeadVO.setTransi_type("4D18-01"); //
		String userOID = InvocationInfoProxy.getInstance().getUserId();
		budgetHeadVO.setBillmaker(userOID); // 预算编制人
		budgetHeadVO.setCreator(userOID); // 创建人
		budgetHeadVO.setPk_releasepsn(userOID); // 预算发布人
		budgetHeadVO.setBillmaketime(new UFDate());// 预算编制日期
		budgetHeadVO.setCreationtime(new UFDateTime()); // 创建日期
		budgetHeadVO.setRelease_date(new UFDate()); // // 预算发布日期

		budgetHeadVO.setStatus_date_after(audittime); // 项目变化后日期
		budgetHeadVO.setStatus_date_before(audittime); // 项目变化前日期

		budgetHeadVO.setFloat_per(new UFDouble(0.0)); // 浮动百分百
		// 预算编制部门 // 变更人 // 变更日期 // 备注
		budgetHeadVO.setFloat_mny(new UFDouble(0.0)); // 浮动金额
		SuperVO superVO = (SuperVO) budgetHeadVO;

		getDao.InsertVOWithPK(superVO);

		String budgetSQL = "select pk_budget from pm_budget where pk_project = '"
				+ feeBalanceBody.getPk_project() + "' and dr = 0";
		System.out.println("budgetSQL===" + budgetSQL);
		IUAPQueryBS bs = NCLocator.getInstance().lookup(IUAPQueryBS.class);

		String pk_budget = bs.executeQuery(budgetSQL, new ColumnProcessor())
				+ "";

		// / 核算要素明细
		insertFactorCbs(pk_budget, feeBalanceBody);
		// /////// 添加CBS预算明细
		insertBudgetCbs(pk_budget, feeBalanceBody);

		// }

	}

	// 添加核算要素明细
	private void insertFactorCbs(String billOID, FeeBalanceBodyVO feeBodyVO)
			throws DAOException, UifException {

		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class); // 获取DAO执行插入
		String cbsCode = feeBodyVO.getPk_cbsnode() + ""; // 招标登记 CBS编码
		String materialCode = feeBodyVO.getPk_material() + ""; // 招标登记 物料编码
		UFDouble money = feeBodyVO.getMoney(); // 招标登记金额

		// 取登记表的 组织 和组织最新版
		String pkOrg = feeBodyVO.getPk_org() + "";
		String pkOrgv = feeBodyVO.getPk_org_v() + "";

		BudgetFactorBodyVO factorBodyVO = new BudgetFactorBodyVO();

		// String factorBillOID = getBillPK(); //生成OID
		// factorBodyVO.setPk_budgetfactor(factorBillOID); // 要素预算明细主键
		factorBodyVO.setPk_budget(billOID); // 预算单主表 外键
		factorBodyVO.setPk_cbsnode(cbsCode); // CBS编码

		// 核算要素 这个怎么来的？***************************
		CBSNodeVO defDocVO = (CBSNodeVO) HYPubBO_Client.queryByPrimaryKey(
				CBSNodeVO.class, cbsCode); // 暂用def2 后期注意修改
		// 费用类型对应的 字段
		String factorName = defDocVO.getFree10(); // 核算要素中文名称
		String fchartSql = "select pk_factorchart from resa_factorchart  where pk_org = '"
				+ pkOrg + "'";
		List<Object[]> lstfchartObj = getDao.query(fchartSql);
		String pk_factorchart = lstfchartObj.get(0)[0] + "";
		String factorasoa = "select  pk_factorasoa  from resa_factorasoa where factorname='"
				+ factorName
				+ "'  and  PK_FACTORCHART = '"
				+ pk_factorchart
				+ "'";
		List<Object[]> lstObj = getDao.query(factorasoa);
		String PKFactor = lstObj.get(0)[0] + "";

		factorBodyVO.setBudget_mny(money); // 预算金额 budget_mny

		factorBodyVO.setBala_mny(money); // 预算余额 bala_mny
		factorBodyVO.setPk_factor(PKFactor);

		factorBodyVO.setTot_add_mny(new UFDouble(0.0)); // 累积调整
		factorBodyVO.setExe_mny(new UFDouble(0.0)); // 执行金额
		factorBodyVO.setIn_budget(new UFDouble(0.0)); // 收入预算
		factorBodyVO.setIn_tot_add(new UFDouble(0.0)); // 累计收入调整
		factorBodyVO.setIn_exe_mny(new UFDouble(0.0)); // 收入执行金额
		factorBodyVO.setIn_bala_mny(new UFDouble(0.0)); // 收入差额
		factorBodyVO.setFloat_per(new UFDouble(0.0)); // 浮动百分比
		factorBodyVO.setFloat_mny(new UFDouble(0.0)); // 浮动金额
		factorBodyVO.setPre_exe_mny(new UFDouble(0.0)); // 预占金额
		factorBodyVO.setRes_mny(new UFDouble(0.0)); // 资源预算

		factorBodyVO.setPk_group("0001A1100000000001QS"); // 集团
		factorBodyVO.setPk_org(pkOrg); // 项目组织最新版本
		factorBodyVO.setPk_org_v(pkOrgv); // 项目组织

		String rowno = getMaxRowno("pm_budgetfactor", billOID);
		factorBodyVO.setRowno(rowno);

		factorBodyVO.setDr(0);
		SuperVO s = factorBodyVO;
		s.setAttributeValue("pk_budget", billOID);

		getDao.InsertVOWithPK(s);
	}

	// 添加CBS明细
	private void insertBudgetCbs(String billOID, FeeBalanceBodyVO feeBodyVO)
			throws DAOException, UifException {
		String cbsCode = feeBodyVO.getPk_cbsnode() + ""; // 招标登记 CBS编码
		String materialCode = feeBodyVO.getPk_material() + ""; // 招标登记 物料编码
		UFDouble money = feeBodyVO.getMoney(); // 招标登记金额
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		// 取登记表的 组织 和组织最新版
		String pkOrg = feeBodyVO.getPk_org() + "";
		String pkOrgv = feeBodyVO.getPk_org_v() + "";

		*//**************** 处理汇总行 *********************//*
		// 取factor里面汇总的值
		String totalSQL = "select sum(budget_mny), sum(bala_mny) from pm_budgetfactor where pk_budget = '"
				+ billOID + "' and dr = 0";
		List<Object[]> lsttotalmnyObj = getDao.query(totalSQL);
		UFDouble Budget_mny;
		if (!"null".equals(lsttotalmnyObj.get(0)[0])) {
			Budget_mny = new UFDouble(lsttotalmnyObj.get(0)[0].toString());
		} else {
			Budget_mny = money;
		}
		UFDouble Bala_mny;
		if ("null".equals(lsttotalmnyObj.get(0)[1])) {
			Bala_mny = new UFDouble(lsttotalmnyObj.get(0)[1].toString());
		} else {
			Bala_mny = money;
		}
		// 判断是否有汇总行 有修改值，没有添加 返回 主键OID

		CBSNodeVO defDocVO = (CBSNodeVO) HYPubBO_Client.queryByPrimaryKey(
				CBSNodeVO.class, cbsCode);

		// 查询CBS父节点 OID，和名称
		String cbsFathe = defDocVO.getPk_father() + "";
		String getfatherSQL = "select pk_budgetcbs from pm_budgetcbs where pk_budget= '"
				+ billOID + "' and pk_cbsnode = '" + cbsFathe + "'";
		List<Object[]> lstObj = getDao.query(getfatherSQL);
		String pk_budgetcbs;
		if (lstObj.size() >= 1) {
			pk_budgetcbs = lstObj.get(0)[0] + "";

			// 修改汇总行信息
			BudgetCBSBodyVO cbsBodyTotalVO = (BudgetCBSBodyVO) HYPubBO_Client
					.queryByPrimaryKey(BudgetCBSBodyVO.class, pk_budgetcbs);

			cbsBodyTotalVO.setPk_budgetcbs(pk_budgetcbs); // pk_budgetcbs
			cbsBodyTotalVO.setBudget_mny(Budget_mny);
			cbsBodyTotalVO.setBala_mny(Bala_mny);
			cbsBodyTotalVO.setPk_budget(billOID);

			// 余额是否需要修改？?? 目前，余额算法 预算金额-执行金额=预算余额
			SuperVO tempVO = cbsBodyTotalVO;
			tempVO.setAttributeValue("pk_budget", billOID);
			tempVO.setAttributeValue("pk_budgetcbs", pk_budgetcbs);

			getDao.updateVO(tempVO);

		} else {
			// 新增汇总行
			// pk_budgetcbs = getBillPK(); //生成OID

			BudgetCBSBodyVO cbsBodyTotalVO = new BudgetCBSBodyVO();

			cbsBodyTotalVO.setPk_budget(billOID); // 预算单主表 外键
			cbsBodyTotalVO.setDr(0);
			// CBSOID 赋值为 父节点OID
			cbsBodyTotalVO.setPk_cbsnode(cbsFathe);
			// cbsBodyTotalVO.setPk_budget(); //
			cbsBodyTotalVO.setBudget_mny(Budget_mny); // CBS预算金额 budget_mny
			cbsBodyTotalVO.setBala_mny(Bala_mny); // CBS预算余额 bala_mny

			String rowno = getMaxRowno("pm_budgetcbs", billOID);
			cbsBodyTotalVO.setRowno(rowno);

			cbsBodyTotalVO.setTot_add_mny(new UFDouble(0.0)); // 累积调整
			cbsBodyTotalVO.setExe_mny(new UFDouble(0.0)); // 执行金额
			cbsBodyTotalVO.setIn_budget(new UFDouble(0.0)); // 收入预算
			cbsBodyTotalVO.setIn_tot_add(new UFDouble(0.0)); // 累计收入调整
			cbsBodyTotalVO.setIn_exe_mny(new UFDouble(0.0)); // 收入执行金额
			cbsBodyTotalVO.setIn_bala_mny(new UFDouble(0.0)); // 收入差额
			cbsBodyTotalVO.setFloat_per(new UFDouble(0.0)); // 浮动百分比
			cbsBodyTotalVO.setFloat_mny(new UFDouble(0.0)); // 浮动金额
			cbsBodyTotalVO.setPre_exe_mny(new UFDouble(0.0)); // 预占金额
			cbsBodyTotalVO.setRes_mny(new UFDouble(0.0)); // 资源预算
			cbsBodyTotalVO.setPk_group("0001A1100000000001QS"); // 集团
			cbsBodyTotalVO.setPk_org(pkOrg); // 项目组织最新版本
			cbsBodyTotalVO.setPk_org_v(pkOrgv); // 项目组织

			SuperVO s = cbsBodyTotalVO;
			s.setAttributeValue("pk_budget", billOID);
			s.setAttributeValue("pk_cbsnode", cbsFathe);
			getDao.InsertVOWithPK(s);

		}

		*//*********************************** 汇总行处理完毕 *******************//*

		// /////// 添加CBS预算明细
		BudgetCBSBodyVO cbsBodyVO = new BudgetCBSBodyVO();
		// String cbsBillOID = getBillPK(); //生成OID
		// cbsBodyVO.setPk_budgetcbs(cbsBillOID); // 预算CBS明细主键主键
		cbsBodyVO.setPk_budget(billOID); // 预算单主表 外键

		// cbsBodyVO.setPk_budgetcbs(cbsCode);// CBS编码 pk_cbsnode
		cbsBodyVO.setDr(0);
		cbsBodyVO.setPk_cbsnode(cbsCode);
		cbsBodyVO.setBudget_mny(money); // CBS预算金额 budget_mny
		cbsBodyVO.setBala_mny(money); // CBS预算余额 bala_mny

		cbsBodyVO.setTot_add_mny(new UFDouble(0.0)); // 累积调整
		cbsBodyVO.setExe_mny(new UFDouble(0.0)); // 执行金额
		cbsBodyVO.setIn_budget(new UFDouble(0.0)); // 收入预算
		cbsBodyVO.setIn_tot_add(new UFDouble(0.0)); // 累计收入调整
		cbsBodyVO.setIn_exe_mny(new UFDouble(0.0)); // 收入执行金额
		cbsBodyVO.setIn_bala_mny(new UFDouble(0.0)); // 收入差额
		cbsBodyVO.setFloat_per(new UFDouble(0.0)); // 浮动百分比
		cbsBodyVO.setFloat_mny(new UFDouble(0.0)); // 浮动金额
		cbsBodyVO.setPre_exe_mny(new UFDouble(0.0)); // 预占金额
		cbsBodyVO.setRes_mny(new UFDouble(0.0)); // 资源预算
		cbsBodyVO.setPk_group("0001A1100000000001QS"); // 集团
		cbsBodyVO.setPk_org(pkOrg); // 项目组织最新版本
		cbsBodyVO.setPk_org_v(pkOrgv); // 项目组织
		String rowno = getMaxRowno("pm_budgetcbs", billOID);
		cbsBodyVO.setRowno(rowno);

		// GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		// //获取DAO执行插入
		SuperVO s = cbsBodyVO;
		s.setAttributeValue("pk_budget", billOID);
		s.setAttributeValue("pk_cbsnode", cbsCode);
		System.out.println("123====" + s);
		getDao.InsertVOWithPK(s);

	}

	// 修改CBS明细 金额
	private void updateBudgetCbs(String budgetcbsOID, String budgetOID,
			UFDouble money, UFDouble balamny, UFDouble feemoney, String cbsOID)
			throws DAOException, UifException {

		BudgetCBSBodyVO cbsBodyVO = (BudgetCBSBodyVO) HYPubBO_Client
				.queryByPrimaryKey(BudgetCBSBodyVO.class, budgetcbsOID);
		cbsBodyVO.setPk_budget(budgetOID);
		cbsBodyVO.setPk_budgetcbs(budgetcbsOID); // pk_budgetcbs
		cbsBodyVO.setBudget_mny(money); // 预算金额

		cbsBodyVO.setBala_mny(balamny);
		System.out.println("===" + cbsBodyVO.getPk_budget());
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		SuperVO s = cbsBodyVO;
		s.setAttributeValue("pk_budget", budgetOID);
		s.setAttributeValue("pk_budgetcbs", budgetcbsOID);
		// System.out.println("sss====" + s);

		getDao.updateVO(s);

		*//********************** 修改汇总行 ******************************//*
		// 取factor里面汇总的值
		String totalSQL = "select sum(budget_mny), sum(bala_mny) from pm_budgetfactor where pk_budget = '"
				+ budgetOID + "' and dr = 0";
		List<Object[]> lsttotalmnyObj = getDao.query(totalSQL);
		UFDouble Budget_mny;
		if (!"null".equals(lsttotalmnyObj.get(0)[0])) {
			Budget_mny = new UFDouble(lsttotalmnyObj.get(0)[0].toString());
		} else {
			Budget_mny = money;
		}
		UFDouble Bala_mny;
		if ("null".equals(lsttotalmnyObj.get(0)[1])) {
			Bala_mny = new UFDouble(lsttotalmnyObj.get(0)[1].toString());
		} else {
			Bala_mny = money;
		}

		// 修改汇总行信息
		CBSNodeVO defDocVO = (CBSNodeVO) HYPubBO_Client.queryByPrimaryKey(
				CBSNodeVO.class, cbsOID);

		// 查询CBS父节点 OID，和名称 // 父cbsOID
		String cbsFathe = defDocVO.getPk_father() + "";
		String getfatherSQL = "select pk_budgetcbs from pm_budgetcbs where pk_budget= '"
				+ budgetOID + "' and pk_cbsnode = '" + cbsFathe + "'";
		List<Object[]> lstObj = getDao.query(getfatherSQL);

		String pk_budgetcbs = "";
		if (lstObj.size() >= 1) {
			// if(!"".equals(pk_budgetcbs) && !"null".equals(pk_budgetcbs) &&
			// !"0".equals(pk_budgetcbs)){ //如果有 汇总行

			pk_budgetcbs = lstObj.get(0)[0] + "";
			BudgetCBSBodyVO cbsBodyTotalVO = (BudgetCBSBodyVO) HYPubBO_Client
					.queryByPrimaryKey(BudgetCBSBodyVO.class, pk_budgetcbs);

			cbsBodyTotalVO.setPk_budgetcbs(pk_budgetcbs); // pk_budgetcbs
			cbsBodyTotalVO.setBudget_mny(Budget_mny);
			cbsBodyTotalVO.setBala_mny(Bala_mny);
			cbsBodyTotalVO.setPk_budget(budgetOID);

			// PK_BUDGET
			// 余额是否需要修改？?? 目前，余额算法 预算金额-执行金额=预算余额
			SuperVO tempVO = cbsBodyTotalVO;
			tempVO.setAttributeValue("pk_budget", budgetOID);
			tempVO.setAttributeValue("pk_budgetcbs", pk_budgetcbs);
			// System.out.println("sss===="+s);
			getDao.updateVO(tempVO);
		} else {
			// 添加汇总
			// pk_budgetcbs = getBillPK(); //生成OID

			BudgetCBSBodyVO cbsBodyTotalVO = new BudgetCBSBodyVO();
			cbsBodyTotalVO.setPk_budget(budgetOID); // 预算单主表 外键
			// cbsBodyTotalVO.setPk_budgetcbs(pk_budgetcbs);
			cbsBodyTotalVO.setDr(0);
			// CBSOID 赋值为 父节点OID
			cbsBodyTotalVO.setPk_cbsnode(cbsFathe);
			cbsBodyTotalVO.setBudget_mny(Budget_mny); // CBS预算金额 budget_mny
			cbsBodyTotalVO.setBala_mny(Bala_mny); // CBS预算余额 bala_mny
			String rowno = getMaxRowno("pm_budgetcbs", budgetOID);
			cbsBodyTotalVO.setRowno(rowno);
			SuperVO tempVO = cbsBodyTotalVO;
			tempVO.setAttributeValue("pk_budget", budgetOID);
			tempVO.setAttributeValue("pk_cbsnode", cbsFathe);
			tempVO.setAttributeValue("pk_budgetcbs", pk_budgetcbs);

			getDao.InsertVOWithPK(tempVO);
		}
		*//********************* 汇总行处理完成 ************************//*
	}

	// 修改核算要素值
	private void updateFactorCbs(String cbsOID, String budgetOID,
			UFDouble money, UFDouble balamny, String FactorCbs, String pkorg)
			throws DAOException, UifException {
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class); // 获取DAO执行插入
		BudgetFactorBodyVO factorBodyVO = (BudgetFactorBodyVO) HYPubBO_Client
				.queryByPrimaryKey(BudgetFactorBodyVO.class, cbsOID);

		factorBodyVO.setPk_budgetfactor(cbsOID); // 要素预算明细主键
		factorBodyVO.setPk_budget(budgetOID);

		// 核算要素 这个怎么来的？***************************
		CBSNodeVO defDocVO = (CBSNodeVO) HYPubBO_Client.queryByPrimaryKey(
				CBSNodeVO.class, FactorCbs); // 暂用def2 后期注意修改
		// 费用类型对应的 字段
		// String feeTypeField = defDocVO.getFree10(); // 核算要素
		String factorName = defDocVO.getFree10(); // 核算要素中文名称
		// suchb20200610修改

		// String factorSql =
		// "select pk_factorasoa from resa_factorasoa where factorname ='" +
		// factorName + "'";
		String factorSql = "select A.pk_factorasoa from resa_factorasoa A	left join resa_factorchart B on A.pk_factorchart = B.pk_factorchart	where A.factorname ='"
				+ factorName + "' and B.PK_ORG = '" + pkorg + "'";
		System.out.println("factorSql====" + factorSql);
		List<Object[]> lstObj = getDao.query(factorSql);
		String PKFactor = lstObj.get(0)[0] + "";

		factorBodyVO.setPk_factor(PKFactor); // 核算要素
		factorBodyVO.setBudget_mny(money); // 预算金额 budget_mny
		
		 * UFDouble exeMny = factorBodyVO.getExe_mny(); UFDouble balaMny; if
		 * (exeMny != null) { balaMny = money.sub(exeMny); //预算余额 }else{ balaMny
		 * = money; }
		 
		factorBodyVO.setBala_mny(balamny); // 预算余额 bala_mny

		SuperVO ss = factorBodyVO;
		ss.setAttributeValue("pk_budget", budgetOID);
		ss.setAttributeValue("pk_budgetfactor", cbsOID);
		// System.out.println("ssssss===="+ss);

		getDao.updateVO(ss);
	}*/

	private String getMaxRowno(String tableName, String mainOID)
			throws DAOException {
		String getSql = "select rowno from (select * from " + tableName
				+ " where dr = 0 and pk_budget = '" + mainOID
				+ "' order by rowno desc) where rownum=1";

		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		String rtnMaxRowno = "10";

		List<Object[]> lstObj = getDao.query(getSql);
		if ((lstObj != null) && (lstObj.size() != 0)) {
			String maxRowno = lstObj.get(0)[0] + "";
			if (!"~".equals(maxRowno) && (!"null".equals(maxRowno))) {
				Integer maxRow = Integer.valueOf(maxRowno) + 10;
				rtnMaxRowno = maxRow.toString();
			}
		}

		return rtnMaxRowno;
	}

	private double js() throws ScriptException {
		String expr = "(1+2/5)*3";
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("js");
		Object result = engine.eval(expr);
		return Double.parseDouble(result + "");
	}

	/*
	 * private String getBillPK() { IdGenerator idGenerator = new
	 * SequenceGenerator(); return idGenerator.generate(); //生成OID }
	 */
}
