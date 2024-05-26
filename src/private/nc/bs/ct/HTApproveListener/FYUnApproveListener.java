package nc.bs.ct.HTApproveListener;

import java.util.List;

import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.ppm.schedule.utils.TaskPushUtils;
import nc.ui.pcm.utils.GetDao;
import nc.vo.pcm.feebalance.FeeBalanceBillVO;
import nc.vo.pcm.feebalance.FeeBalanceHeadVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
//import nc.ui.pcm.feebalance.util.GenPushJDKUtil;

public class FYUnApproveListener implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		// TODO 自动生成的方法存根
		FeeBalanceBillVO[] aggvos = null;
		Object object = ((BusinessEvent) event).getObject();
		if (object.getClass().isArray()) {
			AggregatedValueObject[] aggVO = (AggregatedValueObject[]) object;
			if ((aggVO instanceof FeeBalanceBillVO[])) {
				aggvos = (FeeBalanceBillVO[]) aggVO;
			}
		}
		if (aggvos != null) {
			FeeBalanceHeadVO headvo = (FeeBalanceHeadVO) aggvos[0]
					.getParentVO();
			String billtype = headvo.getBill_type() + "";// 单据类型
			if ("4D83".equals(billtype)
					&& !"4D83-01".equals(headvo.getTransi_type())) {
				/*
				 * String fg = "0"; String user =
				 * InvocationInfoProxy.getInstance().getUserId();// 当前用户主键
				 * GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
				 * String querySql =
				 * "select c.cuserid, a.pk_role, a.role_name,c.user_name from sm_role a left join sm_user_role b  on a.pk_role=b.pk_role "
				 * +
				 * " left join sm_user c on c.cuserid=b.cuserid  where a.role_code = 'XMQXSP01'"
				 * ; List<Object[]> resultList = getDao.query(querySql);
				 * System.out.println("sql===" + querySql); if
				 * (resultList.size() > 0) { for (int i = 0; i <
				 * resultList.size(); i++) { Object[] item = resultList.get(i);
				 * if (user.equals(item[0])) { fg = "1"; } } }
				 * System.out.println("当前登录用户主键：" + user); if (!"1".equals(fg))
				 * { throw new BusinessException("不允许取消审批！"); //
				 * ExceptionUtils.wrappBusinessException("不允许自制！"); // return; }
				 */
				TaskPushUtils util = new TaskPushUtils();// 推进度填报
				try {
					util.deleteTaskBillVO(headvo.getPrimaryKey());
				} catch (BusinessException e1) { // TODO 自动生成的 catch 块
					e1.printStackTrace();
				}

				// XBX20210115新增取消，
				/*if ("4D83-Cxx-04".equals(headvo.getTransi_type())) {
					// 项目组织
					GetDao getDao = NCLocator.getInstance()
							.lookup(GetDao.class);
					String pkOrg = headvo.getPk_org();
					CircularlyAccessibleValueObject[] childrenVOs = aggvos[0]
							.getChildrenVO(); // 子表数据
					for (int j = 0; j < childrenVOs.length; j++) {
						FeeBalanceBodyVO feeBalanceBody = (FeeBalanceBodyVO) childrenVOs[j]; // 子表VO
						String pk_feebalance_b = feeBalanceBody.getPrimaryKey();// 子表主键
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
							String pk_project = feeBalanceBody.getPk_project()
									+ "";
							String budFactorSql = "select a.pk_budgetfactor, a.budget_mny, a.bala_mny from pm_budgetfactor a left join pm_budget b on a.pk_budget = b.pk_budget where b.pk_project = '"
									+ pk_project
									+ "' and a.pk_cbsnode = '"
									+ cbsOID
									+ "'and a.dr = 0 and b.last_v_flag = 'Y' and b.dr = 0"; // 如果有返回主键，为修改做准备
							budFactorArrList = (List<Object[]>) bs
									.executeQuery(budFactorSql,
											new ArrayListProcessor());
							if (budFactorArrList.size() > 0) {
								System.out.println("budFactorSql==="
										+ budFactorSql);
								System.out.println("budFactorArrList长度："
										+ budFactorArrList.size());
								System.out.println("v.get(0)=="
										+ budFactorArrList.get(0));
								Object[] obj = budFactorArrList.get(0);
								String budgetfactorOID = (String) obj[0]; // 预算明细CBS的OID
								UFDouble budgetfactormny = new UFDouble(
										Double.parseDouble(obj[1] + "")); // 预算明细
																			// 金额
								UFDouble balafactormny = new UFDouble(
										Double.parseDouble(obj[2] + "")); // 预算余额
								budgetfactormny = budgetfactormny.sub(feemoney);

								if (feemoney.toDouble() <= balafactormny
										.toDouble()) {
									balafactormny = balafactormny.sub(feemoney);
									System.out.println("budgetfactormny=="
											+ budgetfactormny);
									// 修改核素要素金额
									updateFactorCbs(budgetfactorOID, pk_budget,
											budgetfactormny, balafactormny,
											cbsOID, pkOrg);

									// 查询预算明细里面有无 CBS 预算明细
									String cbsSql = "select pk_budgetcbs, budget_mny, bala_mny from  pm_budgetcbs where pk_budget = '"
											+ pk_budget
											+ "' and pk_cbsnode = '"
											+ cbsOID
											+ "'  and dr = 0"; // 如果有返回主键，为修改做准备
									budCbsArrList = (List<Object[]>) bs
											.executeQuery(cbsSql,
													new ArrayListProcessor());
									// 如果有 取 预算值 和 当前值相加 并更新
									Object[] objs = budCbsArrList.get(0);
									String budgetcbs = (String) objs[0]; // 预算明细CBS的OID
									String aaa = objs[1] + "";
									Double db = Double.parseDouble(aaa);
									if (db > 0) {
										UFDouble budgetmny = new UFDouble(db); // 预算明细
																				// 金额
										budgetmny = budgetmny.sub(feemoney);
										String bbb = objs[2] + "";
										Double db2 = Double.parseDouble(bbb);
										UFDouble balamny = new UFDouble(db2); // 预算余额
										balamny = balamny.sub(feemoney);
										// 修改 主键，主表主键，预算金额，预算余额
										updateBudgetCbs(budgetcbs, pk_budget,
												budgetmny, balamny, feemoney,
												cbsOID);
										String updsql = "UPDATE PM_FEEBALANCE_B SET DEF19 = '~' WHERE PK_FEEBALANCE_B = '"
												+ pk_feebalance_b + "'";
										getDao.executeUpdate(updsql);
									}
								}
							}
						}
					}
				}*/
			}
		}
	}

	// 修改核算要素值
	/*private void updateFactorCbs(String cbsOID, String budgetOID,
			UFDouble money, UFDouble balamny, String FactorCbs, String pkorg)
			throws DAOException, UifException {
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class); // 获取DAO执行插入
		IUifService iUifService = (IUifService) NCLocator.getInstance().lookup(
				IUifService.class.getName());
		BudgetFactorBodyVO factorBodyVO = (BudgetFactorBodyVO) iUifService
				.queryByPrimaryKey(BudgetFactorBodyVO.class, cbsOID);

		factorBodyVO.setPk_budgetfactor(cbsOID); // 要素预算明细主键
		factorBodyVO.setPk_budget(budgetOID);

		// 核算要素 这个怎么来的？***************************
		CBSNodeVO defDocVO = (CBSNodeVO) iUifService.queryByPrimaryKey(
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
	}

	// 修改CBS明细 金额
	private void updateBudgetCbs(String budgetcbsOID, String budgetOID,
			UFDouble money, UFDouble balamny, UFDouble feemoney, String cbsOID)
			throws DAOException, UifException {
		IUifService iUifService = (IUifService) NCLocator.getInstance().lookup(
				IUifService.class.getName());
		BudgetCBSBodyVO cbsBodyVO = (BudgetCBSBodyVO) iUifService
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
		System.out.println("sss====" + s);

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
		CBSNodeVO defDocVO = (CBSNodeVO) iUifService.queryByPrimaryKey(
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
			BudgetCBSBodyVO cbsBodyTotalVO = (BudgetCBSBodyVO) iUifService
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
	}*/

	@SuppressWarnings("unused")
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
}
