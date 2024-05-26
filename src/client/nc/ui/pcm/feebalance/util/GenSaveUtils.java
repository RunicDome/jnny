package nc.ui.pcm.feebalance.util;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.pub.pushlet.util.Log;
import nc.ui.pcm.utils.GetDao;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.arap.pub.UFDoubleTool;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.pcm.feebalance.FeeBalanceBodyVO;
import nc.vo.pim.project.ProjectHeadVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

@SuppressWarnings({ "restriction", "unused" })
public class GenSaveUtils {
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);

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

	// （2）、验证排序在前一位的上个单据(组织+项目)状态审批完成
	// -----参数：pkOrg==所属组织，transType==交易类型，bodyVO==表体
	public int checkRelation(String pkOrg, String transType,
			FeeBalanceBodyVO[] bodyVO, String zbtype) throws DAOException,
			UifException {
		int i = 0;
		String errorInfo = "";// 记录未完成的项目
		String errorJdInfo = "\r\n";// 记录进度不足100的

		for (FeeBalanceBodyVO itemVO : bodyVO) {
			String pk_project = itemVO.getPk_project();// 项目PK
			String project_code = "";// 项目编码
			String httype = "";// 项目类型PK
			ProjectHeadVO ProjectVO = (ProjectHeadVO) HYPubBO_Client
					.queryByPrimaryKey(ProjectHeadVO.class, pk_project + ""); // 项目VO
			if (ProjectVO != null) {
				project_code = ProjectVO.getProject_code() + "";
				httype = ProjectVO.getPk_projectclass() + "";
			}
			// 一次管网 热源类 项目 年度投资计划 》 2020年的话需要继续往下验证 1001A21000000005SCHW--一次管网项目
			// 1001A21000000005NM9B--热源类建设项目
			String fg = "0";
			// String pk_projectype = projectHeadVO.getPk_projectclass();// 项目类型
			String getndjhsql = "SELECT COUNT(ZB.PK_YEARPLAN) AS SL FROM PM_YEARPLAN ZB LEFT JOIN PM_YEARPLAN_B MX ON ZB.PK_YEARPLAN = MX.PK_YEARPLAN WHERE ZB.DR = 0 AND MX.DR = 0 AND MX.DEF39 = 'Y' AND ZB.PLAN_YEAR > '2020' AND MX.PK_PROJECT = '"
					+ pk_project + "'";
			System.out.println("查询年度投资计划SQL：" + getndjhsql);
			List<Object[]> tzls = getDao.query(getndjhsql);
			int sl = Integer.parseInt(tzls.get(0)[0] + "");
			if (httype != null
					&& ("1001A21000000005SCHW".equals(httype) || "1001A21000000005NM9B"
							.equals(httype)) && sl > 0) {
				// 如果项目编码包含 RD，不进行校验，但保留WBS，进度单据还是要推
				if (project_code.indexOf("RD") == -1) {
					fg = "1";
				}

			} else {
				System.out.println("项目类型：" + httype);
			}
			if ("1".equals(fg)) {
				String cbsql = "SELECT BEFORE FROM RL_RELATION WHERE CODE = 'JDJHBZ' AND AFTER = '"
						+ transType + "' AND XMTYPE = '" + httype + "'";
				if (zbtype != null) {
					cbsql += " AND REMARK = '" + zbtype + "'";
				}
				// System.out.println("cbsql===="+cbsql);
				List<Object[]> orderLs = getDao.query(cbsql);
				if (orderLs != null && orderLs.size() > 0) {
					if (orderLs.get(0)[0] != null && orderLs.get(0)[0] != "") {
						String[] beforebill = orderLs.get(0)[0].toString()
								.split(",");

						// xuwj---------
						if (Arrays.asList(beforebill).contains("4D15-Cxx-008")
								|| Arrays.asList(beforebill).contains(
										"4D15-Cxx-011")) {
							String sql = "SELECT COUNT(PK_PROJECTPROPOSAL) AS SL FROM PM_PROJECTPROPOSAL WHERE BILL_STATUS = 1 AND (TRANSI_TYPE = '4D15-Cxx-008' or TRANSI_TYPE = '4D15-Cxx-011') AND DR = 0 AND PK_ORG = '"
									+ pkOrg
									+ "' "
									+ " AND PK_PROJECT = '"
									+ pk_project + "'";
							List<Object[]> slLs = getDao.query(sql);
							if (slLs != null && slLs.size() > 0
									&& slLs.get(0)[0] != null
									&& !"0".equals((slLs.get(0)[0] + ""))) {
								continue;
							}
						}
						// xuwj---------

						for (String billtrans : beforebill) {
							String type = billtrans;// 上一个单据的交易类型
							System.out.println("<2>项目编码[" + project_code
									+ "] || 排序前一个单据的交易类型[" + type + "]");
							// 根据组织+项目+交易类型判断之前单据是否有并且审批完成
							// 清单发包合同单独判断

							String billName = getBillName(billtrans + "");
							if ("4D83-Cxx-04".equals(type)) {
								billName = "施工招标";
							}
							String billAppFlag = checkBillApp(pkOrg,
									pk_project, type);
							if ("NO".equals(billAppFlag)) {
								errorInfo += "[" + project_code + "]项目 ["
										+ billName + "]单据未填制！\r\n";
							} else {
								double jd = 0.0;
								if ("4D15-Cxx-001".equals(billtrans)
										|| "4D15-Cxx-007".equals(billtrans)) {
									jd = 100.0;
								} else {
									if ("4D42".equals(type)) {
										jd = checkComBillAppContr(pkOrg,
												pk_project,billName);
									} else if ("4D15-Cxx-008".equals(type)) {
										jd = checkComBillAppKGD(pkOrg,
												pk_project);
									} else {
										jd = checkComBillApp(pkOrg, pk_project,
												type);
									}
								}
								if (jd != 100) {
									errorJdInfo += "[" + project_code + "]项目 ["
											+ billName + "]单据已完成[" + jd
											+ "]进度，未完成填报！\r\n";
								}
							}
						}
					}
				}
			}
		}
		if (!"".equals(errorInfo) || !"\r\n".equals(errorJdInfo)) {
			System.out.println("输出：" + errorInfo + errorJdInfo);
			ExceptionUtils.wrappBusinessException(errorInfo + errorJdInfo);
			i = 1;
		}
		return i;
	}

	// （3）、验证WBS任务是否存在
	// -----参数：pkOrg==所属组织，transType==交易类型，bodyVO==表体，zbtype==招标方式（招标结果登记）
	public FeeBalanceBodyVO[] checkWBS(String pkOrg, String transType,
			FeeBalanceBodyVO[] bodyVO, String zbtype, String wbs_name)
			throws DAOException, UifException {
		// FeeBalanceBodyVO[] newBodyVO = new FeeBalanceBodyVO[bodyVO.length];//
		// 手动查询pk_wbs并添加到表体VO，最后一块保存
		String errorInfo = "";// 错误信息
		if (transType == null || transType == "") {
			Log.error("Info：交易类型错误[" + transType + "]");
		} else {
			for (int i = 0; i < bodyVO.length; i++) {
				FeeBalanceBodyVO itemVO = bodyVO[i];
				String pk_project = itemVO.getPk_project();// 项目PK
				String project_code = "";// 项目编码
				String httype = "";// 项目类型PK
				ProjectHeadVO ProjectVO = (ProjectHeadVO) HYPubBO_Client
						.queryByPrimaryKey(ProjectHeadVO.class, pk_project + ""); // 项目VO
				if (ProjectVO != null) {
					project_code = ProjectVO.getProject_code() + "";
					httype = ProjectVO.getPk_projectclass();//
				}
				String trans = transType.substring(0, 2);
				if ("Z2".equals(trans)) {
					// 采购合同类单独处理
					// String wbsName = getWbsName("Z2", null);// 单据对应任务名称
					String wbsName = getWbsName("Z2");// 单据对应任务名称
					if (wbsName != null && !"".equals(wbsName)) {
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
								if (Double.parseDouble(jd) == 100) {
									return null;
								}
							}
						}
						String pk_wbs = getWbsWork(pkOrg, pk_project, wbsName);
						if (pk_wbs != null && !"".equals(pk_wbs)) {
							bodyVO[i].setPk_wbs(pk_wbs);
							// newBodyVO[i] = itemVO;
							// System.out.println("新："+itemVO);
						}
					} else {
						ExceptionUtils
								.wrappBusinessException("采购合同在配置表里没有对应任务，请检查！");
						Log.error("采购合同在配置表里没有对应任务，请检查！");
					}
				} else {
					// 一次管网 热源类 项目 年度投资计划 》 2020年的话需要继续往下验证
					// 1001A21000000005SCHW--一次管网项目
					// 1001A21000000005NM9B--热源类建设项目
					String fg = "1";
					String getndjhsql = "SELECT COUNT(ZB.PK_YEARPLAN) AS SL FROM PM_YEARPLAN ZB LEFT JOIN PM_YEARPLAN_B MX ON ZB.PK_YEARPLAN = MX.PK_YEARPLAN WHERE ZB.DR = 0 AND MX.DR = 0 AND MX.DEF39 = 'Y' AND ZB.PLAN_YEAR > '2020' AND MX.PK_PROJECT = '"
							+ pk_project + "'";
					System.out.println("查询年度投资计划SQL：" + getndjhsql);
					List<Object[]> tzls = getDao.query(getndjhsql);
					int sl = Integer.parseInt(tzls.get(0)[0] + "");
					// String pk_projectype =
					// projectHeadVO.getPk_projectclass();// 项目类型
					/*if (httype != null
							&& ("1001A21000000005SCHW".equals(httype)
									|| "1001A21000000005NM9B".equals(httype)
									|| "1001A21000000005WJ57".equals(httype)
									|| "1001A21000000005SCSA".equals(httype) || project_code
									.indexOf("RD") != -1) && sl > 0) {
						System.out.println("项目类型：" + httype + "SL==" + sl
								+ "Last=" + project_code.indexOf("RD"));
						// System.out.println("满足");
						fg = "1";
					} else {
						System.out.println("项目类型：" + httype);
					}*/
					System.out.println("项目类型：" + httype + "SL==" + sl
							+ "Last=" + project_code.indexOf("RD"));
					// System.out.println("满足");
					// fg = "1";
					if ("1".equals(fg)) {
						// 根据交易类型判断是否需要验证WBS任务有没有
						if (checkIfHaveWbs(transType)) {
							String wbsName = ""; // 单据对应任务名称
							if (!"".endsWith(wbs_name)) {
								wbsName = wbs_name;
							} else {
								if (!"".endsWith(zbtype)) {
									DefdocVO zbtypeVO = (DefdocVO) HYPubBO_Client
											.queryByPrimaryKey(DefdocVO.class,
													zbtype + "");// 招标方式自定义档案
									if (zbtypeVO != null) {
										wbsName = zbtypeVO.getName() + "";
									}
								} else {
									/*String getnamesql = "SELECT DISTINCT NAME FROM BD_DEFDOC WHERE PK_DEFDOC = '"
											+ itemVO.getDef10() + "'";
									List<Object[]> gnls = getDao
											.query(getnamesql);
									String type = null;
									if (gnls != null && gnls.size() > 0
											&& gnls.get(0) != null
											&& gnls.get(0)[0] != null) {
										type = gnls.get(0)[0] + "";
									}
									bsName = getWbsName(transType, type);*/
									wbsName = getWbsName(transType);
								}
							}
							System.out.println("<3>交易类型[" + transType
									+ "] || 单据对应任务名称[" + wbsName + "]");
							if (!"".equals(wbsName)) {
								String rwsql = "SELECT RWMX.TASKPERCENT,WBS.WBS_NAME FROM PM_TASKSCHEDULE_B RWMX LEFT JOIN "
										+ "PM_TASKSCHEDULE RW ON RWMX.PK_TASKSCHEDULE = RW.PK_TASKSCHEDULE LEFT JOIN PM_WBS WBS "
										+ "ON RWMX.PK_WBS = WBS.PK_WBS WHERE RWMX.DR = 0 AND RW.DR = 0 AND WBS.DR = 0 AND "
										+ "WBS.WBS_NAME = '"
										+ wbsName
										+ "' AND RWMX.PK_PROJECT = '"
										+ pk_project
										+ "'";
								List<Object[]> rwLs = getDao.query(rwsql);
								if (rwLs != null && rwLs.size() > 0
										&& rwLs.get(0)[0] != null) {
									String jd = rwLs.get(0)[0] + "";
									if (jd != null && !"".equals(jd)) {
										// 进度不为100 需要推
										if (Double.parseDouble(jd) == 100) {
											return null;
										}
									}
								}
								String pk_wbs = getWbsWork(pkOrg, pk_project,
										wbsName);
								if (!"".equals(pk_wbs)) {
									bodyVO[i].setPk_wbs(pk_wbs);
									// newBodyVO[i] = itemVO;
									// System.out.println("新："+itemVO);
								} else {
									if (!"采购招标结果登记".equals(wbsName)) {
										errorInfo += "[" + project_code
												+ "]项目未找到对应任务！\r\n";
									}
								}
							} else {
								ExceptionUtils.wrappBusinessException("交易类型['"
										+ transType + "']在配置表里没有对应任务，请检查！");
								Log.error("交易类型[" + transType
										+ "]在配置表里没有对应任务，请检查！");
							}
						} /*else {
							String wbsName = ""; // 单据对应任务名称
							if (!"".endsWith(wbs_name)) {
								wbsName = wbs_name;
							} else {
								if (!"".endsWith(zbtype)) {
									DefdocVO zbtypeVO = (DefdocVO) HYPubBO_Client
											.queryByPrimaryKey(DefdocVO.class,
													zbtype + "");// 招标方式自定义档案
									if (zbtypeVO != null) {
										wbsName = zbtypeVO.getName() + "";
									}
								} else {
									String getnamesql = "SELECT DISTINCT NAME FROM BD_DEFDOC WHERE PK_DEFDOC = '"
											+ itemVO.getDef10() + "'";
									List<Object[]> gnls = getDao
											.query(getnamesql);
									String type = null;
									if (gnls != null && gnls.size() > 0
											&& gnls.get(0) != null
											&& gnls.get(0)[0] != null) {
										type = gnls.get(0)[0] + "";
									}
									wbsName = getWbsName(transType, type);
								}
							}
							if (!"".equals(wbsName)) {
								String pk_wbs = getWbsWork(pkOrg, pk_project,
										wbsName);
								if (!"".equals(pk_wbs)) {
									bodyVO[i].setPk_wbs(pk_wbs);
									// newBodyVO[i] = itemVO;
									// System.out.println("新："+itemVO);
								}
							}
						}*/
					}
				}
			}
		}
		if (!"".equals(errorInfo)) {
			ExceptionUtils.wrappBusinessException(errorInfo);
		}
		return bodyVO;
	}

	/*
	 * （4）、验证任务累计完成不能低于之前最大且不能大于100-----参数：pkOrg==所属组织，pk_project==项目，project_code
	 * ==项目编码， transType==交易类型，totalFinish==累计完成，pk_wbs==WBS任务
	 */
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
					&& "4D83".equals(transType.substring(0, 4))
					&& checkType(transType)) {
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

	/* 公共方法 */
	// 根据组织+项目+交易类型判断之前单据是否有并且审批完成-----参数：pkOrg==所属组织，pk_project==项目，transType==交易类型
	public String checkBillApp(String pkOrg, String pk_project, String transType)
			throws DAOException {
		String bill_name = getBillName(transType + "");
		if ("4D83-Cxx-04".equals(transType)) {
			bill_name = "施工招标";
		}
		String flag = "NO";// 没有
		int sl = 0;
		if (transType == null || transType == "") {
			Logger.error("Info：交易类型" + "('" + transType + "')对应数据表失败！");
		} else {
			// XBX0608添加 首先判断进度计划编制里是否已经有并且完成
			String jhbzsql = "select count(pk_wbs) as sl from pm_wbs where pm_wbs.dr = 0 and pk_project "
					+ "= '"
					+ pk_project
					+ "' and ( enablestate = 2 or enablestate = 1 ) and dr = 0 and actpercent = 100 "
					+ "and wbs_name = '" + bill_name + "'";
			Logger.error("查询进度计划编制SQL：" + jhbzsql);
			List<Object[]> ls = getDao.query(jhbzsql);
			if (ls != null) {
				sl = Integer.parseInt(ls.get(0)[0] + "");
			}
			if (sl > 0) {
				flag = "YES";
			} else {
				if (transType.length() >= 4) {
					String trans = transType.substring(0, 4);// 截取四位
					String sql = "";// 查询SQL
					// 招标结果登记
					if ("4D83-Cxx-04".equals(transType)) {
						sql = "SELECT COUNT(PK_FEEBALANCE_B) AS SL FROM PM_FEEBALANCE_B FYMX LEFT JOIN PM_FEEBALANCE FY ON FYMX.PK_FEEBALANCE = "
								+ "FY.PK_FEEBALANCE WHERE FY.DR = 0 AND FY.BILL_STATUS = 1 AND FY.TRANSI_TYPE = '"
								+ transType
								+ "' AND FY.PK_ORG = '"
								+ pkOrg
								+ "' AND FYMX.PK_PROJECT = "
								+ "'"
								+ pk_project
								+ "' AND FY.DEF1 = '1001A2100000000MGCII'";

					} else {
						if ("4D83".equals(trans)) {
							sql = "SELECT COUNT(PK_FEEBALANCE_B) AS SL FROM PM_FEEBALANCE_B FYMX LEFT JOIN PM_FEEBALANCE FY ON FYMX.PK_FEEBALANCE = "
									+ "FY.PK_FEEBALANCE WHERE FY.DR = 0 AND FY.BILL_STATUS = 1 AND FY.TRANSI_TYPE = '"
									+ transType
									+ "' AND FY.PK_ORG = '"
									+ pkOrg
									+ "' AND FYMX.PK_PROJECT = "
									+ "'"
									+ pk_project + "'";
						} else if ("4D15".equals(trans)) {
							// 项目建议书--开工单
							sql = "SELECT COUNT(PK_PROJECTPROPOSAL) AS SL FROM PM_PROJECTPROPOSAL WHERE BILL_STATUS = 1 AND TRANSI_TYPE =  '"
									+ transType
									+ "' AND DR = 0 AND PK_ORG = '"
									+ pkOrg
									+ "' "
									+ " AND PK_PROJECT = '"
									+ pk_project + "'";
						} else if ("4D36".equals(trans)) {
							// 项目验收报告--竣工验收
							sql = "SELECT COUNT(PK_PRO_CHECK) AS SL FROM PM_CHECK_HEAD WHERE BILL_STATUS = 1 AND TRANSI_TYPE = '"
									+ transType
									+ "' AND DR = 0 AND PK_ORG = '"
									+ pkOrg
									+ "' "
									+ " AND PK_PROJECT = '"
									+ pk_project + "'";
						} else if ("4D42".equals(trans)) {
							// 清单发包合同
							/*
							 * sql =
							 * "SELECT COUNT(PK_CONTR) AS SL FROM PM_CONTR WHERE DR = 0 AND BILL_STATUS = 1 AND "
							 * + "PK_ORG = '"+pkOrg+"' AND PK_PROJECT = '" +
							 * pk_project + "'";
							 */
							sql = "SELECT SUM(SL) AS SL FROM (SELECT COUNT(PK_CONTR) AS SL FROM PM_CONTR WHERE DR = 0 AND BILL_STATUS IN('1','9') AND PK_ORG = '"
									+ pkOrg
									+ "' AND PK_PROJECT = '"
									+ pk_project
									+ "' UNION ALL SELECT COUNT(PK_FEEBALANCE) AS SL FROM (SELECT DISTINCT ZB.PK_FEEBALANCE FROM PM_FEEBALANCE ZB LEFT JOIN PM_FEEBALANCE_B MX ON ZB.PK_FEEBALANCE = MX.PK_FEEBALANCE WHERE ZB.TRANSI_TYPE = '4D83-Cxx-02' AND ZB.DR = 0 AND ZB.PK_ORG = '"
									+ pkOrg
									+ "' AND MX.PK_PROJECT = '"
									+ pk_project + "'))";
						} else {
							Log.error("转换失败！");
							return null;
						}
					}
					List<Object[]> slLs = getDao.query(sql);
					if (slLs != null && slLs.size() > 0
							&& slLs.get(0)[0] != null
							&& !"0".equals((slLs.get(0)[0] + ""))) {
						System.out.println("<2>查询之前单据审批完成数量[" + slLs.get(0)[0]
								+ "]");
						flag = "YES";
					}
				}
			}

		}
		return flag;
	}

	// 根据组织+项目+交易类型判断之前单据是否有并且审批完成100-----参数：pkOrg==所属组织，pk_project==项目，transType==交易类型
	public double checkComBillApp(String pkOrg, String pk_project,
			String transType) throws DAOException {
		double maxjd = 0.0;
		String sql = "SELECT MAX(FYMX.MONEY) AS JD FROM PM_FEEBALANCE_B FYMX LEFT JOIN PM_FEEBALANCE FY ON FYMX.PK_FEEBALANCE = "
				+ "FY.PK_FEEBALANCE WHERE FY.DR = 0 AND FY.BILL_STATUS = 1 AND FY.TRANSI_TYPE = '"
				+ transType
				+ "' AND FY.PK_ORG = '"
				+ pkOrg
				+ "' AND FYMX.PK_PROJECT = " + "'" + pk_project + "'";
		List<Object[]> slLs = getDao.query(sql);
		if (slLs != null && slLs.size() > 0 && slLs.get(0)[0] != null) {
			System.out.println("<2>查询最大进度值[" + slLs.get(0)[0]);
			maxjd = Double.parseDouble(slLs.get(0)[0].toString());
		}
		return maxjd;
	}

	// 清单合同查询进度
	public double checkComBillAppContr(String pkOrg, String pk_project,String wbs_name)
			throws DAOException {
		double maxjd = 0.0;
		String sql = "SELECT ACTPERCENT FROM PM_WBS WHERE PK_WBS IN (SELECT PK_WBS FROM PM_CONTR WHERE DR = 0 AND "
				+ "BILL_STATUS IN('1','9') AND PK_ORG = '"
				+ pkOrg
				+ "' AND PK_PROJECT = '"
				+ pk_project
				+ "' UNION ALL SELECT DISTINCT "
				+ "MX.PK_WBS FROM PM_FEEBALANCE ZB LEFT JOIN PM_FEEBALANCE_B MX ON ZB.PK_FEEBALANCE = "
				+ "MX.PK_FEEBALANCE WHERE ZB.TRANSI_TYPE = '4D83-Cxx-02' AND ZB.DR = 0 AND ZB.PK_ORG = '"
				+ pkOrg
				+ "' "
				+ "AND MX.PK_PROJECT = '"
				+ pk_project
				+ "'"
				+ " UNION ALL  select DISTINCT PK_WBS from PM_WBS where wbs_name = '设计合同' and PK_PROJECT = '"
				+ pk_project + "') AND WBS_NAME = '"+wbs_name+"'";
		System.out.println("查询清单完成：" + sql);
		List<Object[]> slLs = getDao.query(sql);
		if (slLs != null && slLs.size() > 0 && slLs.get(0)[0] != null) {
			System.out.println("<2>查询最大进度值[" + slLs.get(0)[0]);
			maxjd = Double.parseDouble(slLs.get(0)[0].toString());
		}
		return maxjd;
	}

	// 开工单
	public double checkComBillAppKGD(String pkOrg, String pk_project)
			throws DAOException {
		double maxjd = 0.0;
		String sql = "SELECT ACTPERCENT FROM PM_WBS WHERE PK_WBS IN (SELECT DISTINCT DEF10 FROM "
				+ "PM_PROJECTPROPOSAL WHERE TRANSI_TYPE = '4D15-Cxx-008' AND DR = 0 AND PK_ORG = '"
				+ pkOrg + "' AND PK_PROJECT = '" + pk_project + "')";
		System.out.println("查询开工单完成：" + sql);
		List<Object[]> slLs = getDao.query(sql);
		if (slLs != null && slLs.size() > 0 && slLs.get(0)[0] != null) {
			System.out.println("<2>查询最大进度值[" + slLs.get(0)[0]);
			maxjd = Double.parseDouble(slLs.get(0)[0].toString());
		}
		if (UFDoubleTool.isXiangdeng(new UFDouble(100), new UFDouble(maxjd))) {
			return maxjd;
		}

		String sql2 = " SELECT ACTPERCENT  FROM PM_WBS WHERE pk_project = '"
				+ pk_project + "' and wbs_name = '项目开工' ";
		List<Object[]> slLs2 = getDao.query(sql2);
		if (slLs2 != null && slLs2.size() > 0 && slLs2.get(0)[0] != null) {
			maxjd = Double.parseDouble(slLs2.get(0)[0].toString());
		}
		return maxjd;
	}

	// 根据交易编码查询对应单据名称-----参数：transType==交易类型
	public String getBillName(String transType) throws DAOException {
		String name = "";
		String sql = "SELECT AFTER FROM RL_RELATION WHERE BEFORE = '"
				+ transType + "' AND CODE = 'BILLTOWORK'";
		System.out.println(sql);
		List<Object[]> nameLs = getDao.query(sql);
		if (nameLs != null && nameLs.size() > 0 && nameLs.get(0)[0] != null) {
			name = nameLs.get(0)[0] + "";
		}
		return name;
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
					ExceptionUtils.wrappBusinessException("项目["+pk_project+"]未找到对应任务！");
				} else {
					wbsName = "施工进度";
				}

			}
		}
		return pk_wbs;
	}

	// 根据组织+项目+任务名称查询是否有该任务-----参数：pkOrg==所属组织，pk_project==项目，wbsName==WBS任务名称（采购合同）
	public String getCTWbsWork(String pkOrg, String pk_project, String wbsName)
			throws DAOException {
		String pk_wbs = "";
		String wbsWorkSql = "SELECT PK_WBS FROM PM_WBS WHERE DR = 0 AND (ENABLESTATE = 1 OR ENABLESTATE = 2) AND PK_DUTY_ORG = '"
				+ pkOrg
				+ "'"
				+ " AND PK_PROJECT = '"
				+ pk_project
				+ "' AND WBS_NAME = '采购合同'";
		List<Object[]> wbsWorkLs = getDao.query(wbsWorkSql);
		if (wbsWorkLs != null && wbsWorkLs.size() > 0
				&& wbsWorkLs.get(0)[0] != null) {
			pk_wbs = wbsWorkLs.get(0)[0] + "";
		} else {
			pk_wbs = null;
		}
		return pk_wbs;
	}

	// 根据交易类型判断是否需要验证WBS任务有没有-----参数：transType==交易类型
	public boolean checkIfHaveWbs(String transType) throws DAOException {
		boolean bool = true;
		String ifWbsSql = "SELECT COUNT(PK_DEFDOC) FROM BD_DEFDOC WHERE PK_DEFDOCLIST = '1001A11000000023EZXV" +
				"' AND DR = 0 AND CODE = '"+transType+"'";
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

	// 根据交易类型查询对应任务名称-----参数：transType==交易类型
	public String getWbsName(String transType) throws DAOException {
		String wbs_name = "";
		/*String wbsNameSql = "";
		if (type == null) {
			wbsNameSql = "SELECT AFTER FROM RL_RELATION WHERE CODE = 'BILLTOWORK' AND BEFORE = '"
					+ transType + "' AND XMTYPE IS NULL";
		} else {
			wbsNameSql = "SELECT AFTER FROM RL_RELATION WHERE CODE = 'BILLTOWORK' AND BEFORE = '"
					+ transType + "' AND XMTYPE = '" + type + "'";
		}
		List<Object[]> wbsNameLs = getDao.query(wbsNameSql);
		if (wbsNameLs != null && wbsNameLs.size() > 0
				&& wbsNameLs.get(0)[0] != null) {
			wbs_name = wbsNameLs.get(0)[0] + "";
		}*/
		String wbsNameSql = "SELECT SHORTNAME FROM BD_DEFDOC WHERE PK_DEFDOCLIST = '1001A11000000023EZXV" +
				"' AND DR = 0 AND CODE = '"+transType+"'";
		List<Object[]> wbsNameLs = getDao.query(wbsNameSql);
		if (wbsNameLs != null && wbsNameLs.size() > 0
				&& wbsNameLs.get(0)[0] != null) {
			wbs_name = wbsNameLs.get(0)[0] + "";
		}
		System.out.println("wbsNameSq==" + wbsNameSql);
		return wbs_name;
	}

	// 根据交易类型查询是否需要校验累计完成-----参数：transType==交易类型
	public boolean checkType(String transType) throws DAOException {
		boolean type = false;
		String typeSql = "SELECT ORDERNUM FROM RL_RELATION WHERE CODE = 'JDJHBZ' AND AFTER = '"
				+ transType + "'";
		List<Object[]> typeLs = getDao.query(typeSql);
		if (typeLs != null && typeLs.size() > 0 && typeLs.get(0)[0] != null) {
			type = true;
		}
		return type;
	}

	// 清单发包合同根据合同类型查询对应任务名称
	public String getQDWbsName(String pk_contracttype) throws DAOException {
		String wbs_name = "";
		String wbsNameSql = "SELECT AFTER FROM RL_RELATION WHERE CODE = 'QDFBHT' AND BEFORE = '"
				+ pk_contracttype + "'";
		List<Object[]> wbsNameLs = getDao.query(wbsNameSql);
		if (wbsNameLs != null && wbsNameLs.size() > 0
				&& wbsNameLs.get(0)[0] != null) {
			wbs_name = wbsNameLs.get(0)[0] + "";
		}
		return wbs_name;
	}
}
