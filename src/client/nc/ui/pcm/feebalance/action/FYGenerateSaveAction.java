package nc.ui.pcm.feebalance.action;

import java.awt.event.ActionEvent;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.ui.pcm.feebalance.util.GenSaveUtils;
import nc.ui.pcm.utils.GetDao;
import nc.ui.trade.business.HYPubBO_Client;
import nc.vo.pcm.feebalance.FeeBalanceBillVO;
import nc.vo.pcm.feebalance.FeeBalanceBodyVO;
import nc.vo.pcm.feebalance.FeeBalanceHeadVO;
import nc.vo.pim.project.ProjectHeadVO;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings({ "restriction" })
public class FYGenerateSaveAction extends SaveAction {
	// 通用费用结算单保存验证
	private static final long serialVersionUID = 1L;
	GenSaveUtils genSaveUtils = new GenSaveUtils();

	public void doAction(ActionEvent e) throws Exception {
		/* 获取VO */
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
//		IUAPQueryBS iuapQueryBS = NCLocator.getInstance().lookup(
//				IUAPQueryBS.class);
		FeeBalanceBillVO billVO = (FeeBalanceBillVO) this.editor.getValue();// 费用结算单聚合VO
		FeeBalanceHeadVO headVO = (FeeBalanceHeadVO) billVO.getParentVO();// 表头VO
		FeeBalanceBodyVO[] bodyVO = (FeeBalanceBodyVO[]) billVO.getChildrenVO();// 表体VO
		/* 公共变量 */
		String transType = headVO.getTransi_type();// 交易类型
		String pkOrg = headVO.getPk_org();// 组织
		String errInfo = "";
		/* 招标结果登记单独验证结束 */
		// FeeBalanceBillVO newBillVO = new FeeBalanceBillVO();// 费用结算单聚合VO（修改后）
		String zbtype = "";
		if ("4D83-Cxx-04".equals(headVO.getTransi_type())) {
			zbtype = headVO.getDef1() + "";// 招标结果登记的招标方式
		}
		if ("null".endsWith(zbtype) || "".endsWith(zbtype) || null == zbtype
				|| "~".endsWith(zbtype)) {
			zbtype = "";
		}
		// 屏蔽招标结果登记回填预算功能
					/*
		String zbtp = null;
		if ("0001A110000000000HYQ".equals(pkOrg)) {
			DefdocVO zbtypeVO = (DefdocVO) HYPubBO_Client.queryByPrimaryKey(
					DefdocVO.class, zbtype + "");// 招标方式自定义档案

			ArrayList<String> list = new ArrayList<String>();
			list.add("1001A2100000000MGCIH");
			list.add("1001A2100000000MGCII");
			int flag = 0;
			if ("4D83-Cxx-04".equals(transType)) {
				if (zbtypeVO != null) {
					// 招标结果登记验证
					CircularlyAccessibleValueObject[] childrenVOs = billVO
							.getChildrenVO(); // 子表数据
					for (int j = 0; j < childrenVOs.length; j++) {
						FeeBalanceBodyVO feeBalanceBody = (FeeBalanceBodyVO) childrenVOs[j]; // 子表VO

						// 子表的项目OID
						String objectOID = feeBalanceBody.getPk_project() + "";
						// 取项目名称
						ProjectHeadVO projectHeadVO = (ProjectHeadVO) HYPubBO_Client
								.queryByPrimaryKey(ProjectHeadVO.class,
										objectOID);
						// String objectName = projectHeadVO.getProject_name() +
						// "";
						String objectCode = projectHeadVO.getProject_code()
								+ "";
						// 子表的CBS编号
						String cbsOid = feeBalanceBody.getPk_cbsnode() + ""; // ******暂用def1
																				// 后期注意修改
						// 子表的费用类型 取自CBS档案
						CBSNodeVO defDocVO = (CBSNodeVO) HYPubBO_Client
								.queryByPrimaryKey(CBSNodeVO.class, cbsOid); // ******暂用def2
																				// 后期注意修改
						// 费用类型对应的 字段
						String feeTypeField = defDocVO.getFree9(); // 字段名称放在哪个自定义项，待确定

						String feeTypeName = defDocVO.getName() + ""; // 费用名称
						// 子表的费用金额
						UFDouble money = feeBalanceBody.getMoney(); // 需要设置必填项
						UFDouble jgcmoney = new UFDouble(
								Double.parseDouble(feeBalanceBody.getDef10() == null ? "0"
										: feeBalanceBody.getDef10()));// 甲供材
						System.out.println("甲供材：" + jgcmoney);
						if (jgcmoney.compareTo(new UFDouble(0)) != 0) {
							money = money.add(jgcmoney);
							// 取年度资金计划 费用金额， 根据 项目 CBS 年度
							String RefundTime = new SimpleDateFormat("yyyy")
									.format(new Date());
							IUAPQueryBS bs = NCLocator.getInstance().lookup(
									IUAPQueryBS.class); // 查询语句只能返回一行一列结果
							String nplansql = "select b.def9,b.def10,b.def26,b."
									+ feeTypeField
									+ " from pm_yearplan_b b left join pm_yearplan h on b.pk_yearplan = h.pk_yearplan where h.plan_year = '"
									+ RefundTime
									+ "'  and h.dr = '0' and b.dr = '0' and b.pk_project ='"
									+ objectOID + "' ";
							List<Object[]> planls = getDao.query(nplansql);

							if ((planls != null && planls.size() > 0)
									|| "总承包施工".equals(feeTypeName)) {
								// 取累积登记金额，根据项目，CBS 年度
								String factSql = "select sum(b.money)+sum(case when b.def10 = '~' or b.def10 is null "
										+ "then 0 else TO_NUMBER(b.def10) end) as money  from pm_feebalance_b b  left "
										+ "join pm_feebalance h on b.pk_feebalance = h.pk_feebalance"
										+ " where b.pk_project = '"
										+ objectOID
										+ "' and b.pk_cbsnode = '"
										+ cbsOid
										+ "' and B.DR = '0' and h.billmaketime like '"
										+ RefundTime
										+ "%' and h.pk_feebalance != '"
										+ headVO.getPrimaryKey() + "'";
								String factNum = bs.executeQuery(factSql,
										new ColumnProcessor()) + "";
								if ((factNum == null)
										|| ("null".equals(factNum))
										|| ("".equals(factNum)))
									factNum = "0.0";
								// if ((factNum !=
								// null)&&(!"null".equals(factNum))&&(!"".equals(factNum)))
								// {
								// 当前录入的金
								System.out.println("factSql==" + factSql);
								UFDouble tempNum = new UFDouble(factNum);
								money = money.add(tempNum);
								String tjmon = planls.get(0)[0] == null ? "0"
										: planls.get(0)[0].toString();// def9 土建
								String azmon = planls.get(0)[1] == null ? "0"
										: planls.get(0)[1].toString();// def10
																		// 安装
								String zcmon = planls.get(0)[2] == null ? "0"
										: planls.get(0)[2].toString();// def26
																		// 主材
								String qtmon = planls.get(0)[3] == null ? "0"
										: planls.get(0)[3].toString();// def26
																		// 主材
								double yearVal = 0;
								if ("土建".equals(feeTypeName)) {
									yearVal = Double.parseDouble(tjmon)
											+ Double.parseDouble(zcmon);
								} else if ("安装".equals(feeTypeName)) {
									yearVal = Double.parseDouble(azmon)
											+ Double.parseDouble(zcmon);
								} else if ("总承包施工".equals(feeTypeName)) {
									yearVal = Double.parseDouble(tjmon)
											+ Double.parseDouble(azmon)
											+ Double.parseDouble(zcmon);
								} else {
									yearVal = Double.parseDouble(qtmon);
								}
								System.out.println("年度计划金额合：" + yearVal);
								// 判断
								UFDouble yearNum = new UFDouble(yearVal);
								if (money.toDouble() > yearNum.toDouble()) {
									MessageBox
											.showMessageDialog(
													"提示信息",
													"项目编码["
															+ objectCode
															+ "],费用["
															+ feeTypeName
															+ "] 的累积登记值已经超出年度投资计划值，无法保存");
									flag = 1;
									return;
								}
								// }

							} else {
								// 弹窗提示，没有年度计划，请添加项目年度计划
								MessageBox.showMessageDialog("提示信息", "项目编码["
										+ objectCode + "],费用[" + feeTypeName
										+ "] 没有年度投资计划值，请添加后在进行操作");

								flag = 1;
								return;
							}
						} else {
							// 取年度资金计划 费用金额， 根据 项目 CBS 年度
							String RefundTime = new SimpleDateFormat("yyyy")
									.format(new Date());
							String planSql = "select b."
									+ feeTypeField
									+ " from pm_yearplan_b b left join pm_yearplan h on b.pk_yearplan = h.pk_yearplan where h.plan_year = '"
									+ RefundTime
									+ "'  and h.dr = '0' and b.dr = '0' and b.pk_project ='"
									+ objectOID + "'";// and b.def1 = '" +
														// cbsOid +
														// "'";
														// //*******CBS 暂定def1
														// 注意修改
							System.out.println("planSql==" + planSql);
							IUAPQueryBS bs = NCLocator.getInstance().lookup(
									IUAPQueryBS.class);
							// 查询语句只能返回一行一列结果
							String yearVal = bs.executeQuery(planSql,
									new ColumnProcessor()) + "";

							if (((yearVal != null) && (!"".equals(yearVal)) && (!"null"
									.equals(yearVal)))
									|| "总承包施工".equals(feeTypeName)) {
								// 取累积登记金额，根据项目，CBS 年度
								String factSql = "select sum(b.money) as money  from pm_feebalance_b b  left join pm_feebalance h on b.pk_feebalance = h.pk_feebalance"
										+ " where b.pk_project = '"
										+ objectOID
										+ "' and b.pk_cbsnode = '"
										+ cbsOid
										+ "' and B.DR = '0' and h.billmaketime like '"
										+ RefundTime
										+ "%' and h.pk_feebalance != '"
										+ headVO.getPrimaryKey() + "'";
								String factNum = bs.executeQuery(factSql,
										new ColumnProcessor()) + "";
								System.out.println("无甲供材factSql：" + factSql);
								if ((factNum == null)
										|| ("null".equals(factNum))
										|| ("".equals(factNum)))
									factNum = "0.0";
								// if ((factNum !=
								// null)&&(!"null".equals(factNum))&&(!"".equals(factNum)))
								// {
								// 当前录入的金
								UFDouble tempNum = new UFDouble(factNum);
								money = money.add(tempNum);

								// 判断
								UFDouble yearNum = new UFDouble(yearVal == null
										|| "null".equals(yearVal) ? "0"
										: yearVal);
								if (money.toDouble() > yearNum.toDouble()) {
									MessageBox
											.showMessageDialog(
													"提示信息",
													"项目编码["
															+ objectCode
															+ "],费用["
															+ feeTypeName
															+ "] 的累积登记值已经超出年度投资计划值，无法保存");
									return;
								}
								// }

							} else {
								// 弹窗提示，没有年度计划，请添加项目年度计划
								MessageBox.showMessageDialog("提示信息", "项目编码["
										+ objectCode + "],费用[" + feeTypeName
										+ "] 没有年度投资计划值，请添加后在进行操作");
								return;
							}
						}

					}
				}
				if (!list.contains(headVO.getDef1()) && flag == 0) {
					super.doAction(e);
					return;
				}
				// 招标结果登记 保存后新增逻辑 xuwjl@yonyou.com 单据累计完成 > WBS 走原逻辑 单据累计完成 <=
				// WBS
				// 直接走保存
				Set<String> projectValueSet = VOEntityUtil.getVOsValueSet(
						bodyVO, "pk_project");
				HashMap<String, UFDouble> map = new HashMap<String, UFDouble>();
				for (FeeBalanceBodyVO feeBalanceBodyVO : bodyVO) {
					String pk_project = feeBalanceBodyVO.getPk_project();
					String def7 = feeBalanceBodyVO.getDef7();// 累计完成
					if (StringUtils.isEmpty(def7)) {
						continue;
					}
					for (String projrct : projectValueSet) {
						if (StringUtils.equals(pk_project, projrct)) {
							UFDouble ljwc = new UFDouble(def7);
							UFDouble aljwc = map.get(projrct);
							if (aljwc == null) {
								map.put(projrct, ljwc);
							} else {
								boolean xiaoyu = UFDoubleTool.isXiaoyu(ljwc,
										aljwc);
								if (xiaoyu) {
									map.put(projrct, aljwc);
								} else {
									map.put(projrct, ljwc);
								}
							}
						}
					}
				}
				for (String key : map.keySet()) {
					String sql = " select  actpercent from pm_wbs where dr = 0 and   pk_project = '"
							+ key
							+ "' and wbs_name = '"
							+ zbtypeVO.getName()
							+ "' ";
					ArrayList<BigDecimal> executeQuery = (ArrayList<BigDecimal>) iuapQueryBS
							.executeQuery(sql, new ColumnListProcessor());
					if (!executeQuery.isEmpty() && executeQuery.get(0) != null) {
						BigDecimal actpercent = executeQuery.get(0);// wbs的实际完成
						UFDouble ufDouble = map.get(key);// 单据上的累计完成
						if (UFDoubleTool.isXiaoyu(new UFDouble(actpercent),
								ufDouble)) {
							super.doAction(e);
							return;
						}
					}
				}
			}
			if (zbtypeVO != null) {
				zbtp = zbtypeVO.getName() + "";
			}
		}*/

		/* 1===================第一步验证组织 */
		int orgFlag = genSaveUtils.checkOrg(pkOrg);
		if (orgFlag == 0) {
			// 如果没有则跳过
		} else if (orgFlag == 1) {
			/* 2===================第二步验证前后关系 */
			/*int relaFlag = genSaveUtils.checkRelation(pkOrg, transType, bodyVO,
					zbtp);*/
			// if (relaFlag == 0) {
				/* 3===================第三步验证WBS任务 */
				FeeBalanceBodyVO[] newBodyVO = genSaveUtils.checkWBS(pkOrg,
						transType, bodyVO, zbtype, "");

				if (newBodyVO != null && newBodyVO.length > 0) {
					billVO.setChildrenVO(newBodyVO);
					FeeBalanceBodyVO[] ss = (FeeBalanceBodyVO[]) billVO
							.getChildrenVO();

					System.out.println("输出WBS:" + ss[0].getPk_wbs());
					this.editor.setValue(billVO);
					/* 4===================第四步验证累计完成不能低于之前 */
					for (int i = 0; i < bodyVO.length; i++) {
						FeeBalanceBodyVO itemVO = bodyVO[i];
						String pk_project = itemVO.getPk_project();// 项目PK
						String project_code = "";// 项目编码
						double total = 0.0;
						// 招标结果登记累计完成取自定义项7
						if ("4D83-Cxx-04".equals(transType)) {
							if (StringUtils.isEmpty(itemVO.getDef7())) {
								ExceptionUtils.wrappBusinessException("累计完成不能为空!");
							}
							total = Double.parseDouble(itemVO.getDef7() + "");// 总计
						} else {
							if (itemVO.getMoney() == null) {
								ExceptionUtils.wrappBusinessException("累计完成不能为空!");
							}
							total = Double.parseDouble(itemVO.getMoney() + "");// 总计
						}
						String pk_wbs = itemVO.getPk_wbs();// WBS任务
						String pk_feebalance_b = itemVO.getPk_feebalance_b();
						ProjectHeadVO ProjectVO = (ProjectHeadVO) HYPubBO_Client
								.queryByPrimaryKey(ProjectHeadVO.class, pk_project
										+ ""); // 项目VO
						if (ProjectVO != null) {
							project_code = ProjectVO.getProject_code() + "";
						}
						String pk_projectype = ProjectVO.getPk_projectclass();// 项目类型
						String fg = "";
						String getndjhsql = "SELECT COUNT(ZB.PK_YEARPLAN) AS SL FROM PM_YEARPLAN ZB LEFT JOIN PM_YEARPLAN_B MX ON ZB.PK_YEARPLAN = MX.PK_YEARPLAN WHERE ZB.DR = 0 AND MX.DR = 0 AND ZB.PLAN_YEAR > '2020' AND MX.PK_PROJECT = '"
								+ pk_project + "'";
						System.out.println("查询年度投资计划SQL：" + getndjhsql);
						List<Object[]> tzls = getDao.query(getndjhsql);
						int sl = Integer.parseInt(tzls.get(0)[0] + ""); //
						if (pk_projectype != null
								&& ("1001A21000000005SCHW".equals(pk_projectype)
										|| "1001A21000000005NM9B"
												.equals(pk_projectype)
										|| "1001A21000000005WJ57"
												.equals(pk_projectype) || "1001A21000000005SCSA"
											.equals(pk_projectype)) || sl > 0) {
							fg = "1";
						} else {
							System.out.println("项目类型：" + pk_projectype);
						}
						if ("1".equals(fg)) {
							String returndt = genSaveUtils.checkTotalRate(pkOrg,
									pk_project, project_code, transType, total,
									pk_feebalance_b, pk_wbs);
							if (!"".equals(returndt)) {
								errInfo += returndt;
							}
						}
					}
					if (!"".equals(errInfo)) {
						ExceptionUtils.wrappBusinessException(errInfo);
					}
				}
			// }
		}
		// Object o = this.editor.getValue();
		super.doAction(e);
	}

}