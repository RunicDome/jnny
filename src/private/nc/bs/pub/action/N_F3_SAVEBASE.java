package nc.bs.pub.action;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.arap.actions.N_BASE_ACTION;
import nc.bs.arap.paybp.PayBillBO;
import nc.bs.arap.util.ArapFlowCheckUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.trade.business.HYPubBO;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.pcm.utils.GetDao;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.arap.pay.AggPayBillVO;
import nc.vo.arap.pay.PayBillItemVO;
import nc.vo.bd.inoutbusiclass.InoutBusiClassVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pcm.contractbalance.ContractBalanceHeadVO;
import nc.vo.ppm.projectcheck.ProjectCheckHeadVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.ws.intf.RLTaskSchedule;

import org.apache.commons.lang.StringUtils;

// 付款单保存校验
@SuppressWarnings("restriction")
public class N_F3_SAVEBASE extends N_BASE_ACTION {
	private Hashtable<String, Object> m_keyHas;
	private HYPubBO hyPubBO;

	public N_F3_SAVEBASE() {
	}

	private boolean hasBill(String primaryKey) throws BusinessException {
		boolean hasBill = false;
		if (primaryKey != null) {
			AggPayBillVO[] bvos = null;
			try {
				PayBillBO payableBO = new PayBillBO();
				bvos = payableBO
						.findBillByPrimaryKey(new String[] { primaryKey });
			} catch (Exception e) {
			}
			if (bvos != null && bvos.length != 0 && bvos[0] != null) {
				hasBill = true;
			}
		}
		return hasBill;
	}

	public Object runComClass(PfParameterVO paraVo)
			throws nc.vo.pub.BusinessException {
		try {
			if (paraVo.m_preValueVos == null)
				return null;
			Object obj = null;
			super.m_tmpVo = paraVo;
			// // 走批量
			setParameter("context", paraVo.m_preValueVos);

			beforeCheck();

			// XBX资金类别表头赋值表体
			String zjlb = (String) paraVo.m_preValueVos[0].getParentVO()
					.getAttributeValue("def77");
			if (StringUtils.isNotEmpty(zjlb)) {
				Object conforg = (Object) getHyPubBO().findColValue(
						"bd_defdoc", "code", "nvl(dr,0) = 0 and pk_defdoc = '"
								+ zjlb + "'");
				if (conforg != null) {
					PayBillItemVO[] childrenVOs = (PayBillItemVO[]) paraVo.m_preValueVos[0]
							.getChildrenVO();
					for (int i = 0; i < childrenVOs.length; i++) {
						childrenVOs[i].setDef20(zjlb);
					}
				}
			}
			String primaryKey = paraVo.m_preValueVos[0].getParentVO()
					.getPrimaryKey();

			if (hasBill(primaryKey)) {
				obj = runClass(
						"nc.bs.arap.actions.PaybillEditSaveBatchBSAction",
						"updateVOs",
						"&context:nc.vo.pub.AggregatedValueObject[]", paraVo,
						m_keyHas);
			} else {
				obj = runClass("nc.bs.arap.actions.PaybillSaveBatchBSAction",
						"insertVOs",
						"&context:nc.vo.pub.AggregatedValueObject[]", paraVo,
						m_keyHas);
			}
			// if (obj != null)
			// m_methodReturnHas.put("insertVOs", obj);
			afterCheck();
			// luozhw3 校验单据状态和审批状态
			for (AggregatedValueObject aggvo : paraVo.m_preValueVos) {
				AggPayBillVO bxvo = (AggPayBillVO) aggvo;
				// XBX付款校验
				beforeSave(bxvo);
				ArapFlowCheckUtil.checkArapbillFlowStatus(bxvo.getParentVO()
						.getPrimaryKey(),
						bxvo.getParentVO().getAttributeValue("pk_billtype")
								.toString());
			}

			return obj;
		} catch (Exception exception) {
			throw ExceptionHandler.handleException(this.getClass(), exception);

		}
	}

	// XBX付款校验
	private void beforeSave(AggPayBillVO fkvo) throws BusinessException,
			ParseException {
		IUAPQueryBS bs = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		BaseBillVO headVO = fkvo.getHeadVO();// 付款表头VO
		PayBillItemVO[] childrenVOs = (PayBillItemVO[]) fkvo.getChildrenVO();// 付款明细VO
		// 所有不重复项目
		Set<String> xmpk = new HashSet<>();
		// 验证合同
		String jylx = headVO.getPk_tradetype();// 交易类型
		String zz = headVO.getPk_org();// 组织主键
		Object conforg = (Object) getHyPubBO().findColValue(
				"bd_defdoc",
				"code",
				"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
						+ "FROM BD_DEFDOCLIST WHERE CODE = 'HTSAVEORG')"
						+ " and code = '" + zz + "'");
		String htsave_jylx = (String) new HYPubBO().findColValue("sys_config",
				"config_value", "config_key='FKHTSave_JYLX'");
		if (conforg != null) {
			if (htsave_jylx.contains(jylx)) {
				if (headVO.getCreationtime().afterDate(
						new UFDate("2023-05-22 00:00:00"))) {
					for (PayBillItemVO conWorkVO : childrenVOs) {
						if (StringUtils.isEmpty(conWorkVO.getTop_billid())) {
							throw new BusinessException("行号："
									+ conWorkVO.getRowno() + "不允许自制！");
						}
					}
				}
			} else if ("F3-Cxx-FYFKSPD".equals(jylx)) {
				/*
				 * 费用付款审批单根据收支项目判断是否需要参照合同发起费用付款审批单。 哪些收支项目需要通过收支项目自定义项1来判断。
				 */
				for (PayBillItemVO conWorkVO : childrenVOs) {
					if (conWorkVO.getPk_subjcode() != null) {
						InoutBusiClassVO sxzmVO = (InoutBusiClassVO) getHyPubBO()
								.queryByPrimaryKey(InoutBusiClassVO.class,
										conWorkVO.getPk_subjcode());
						if ("1001A2100000000B68C1".equals(sxzmVO.getDef1())
								&& StringUtils.isEmpty(conWorkVO
										.getTop_billid())) {
							throw new BusinessException("行号："
									+ conWorkVO.getRowno() + " 收支项目["
									+ sxzmVO.getName() + "]不允许自制！");
						}
					}
				}

			}
		}

		List<Map<String, String>> htxmje = new ArrayList<Map<String, String>>();
		Object fkOrg = (Object) getHyPubBO().findColValue(
				"bd_defdoc",
				"code",
				"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
						+ "FROM BD_DEFDOCLIST WHERE CODE = 'FKSAVEORG')"
						+ " and code = '" + zz + "'");
		if (fkOrg != null && htsave_jylx.contains(jylx)) {
			for (PayBillItemVO conWorkVO : childrenVOs) {
				xmpk.add(conWorkVO.getProject());
				String htno = conWorkVO.getContractno() + "";// 合同号
				String gyspk = conWorkVO.getSupplier() + "";// 供应商
				String pk_project = conWorkVO.getProject();// 项目
				if (pk_project != null) {
					String project_code = (String) getHyPubBO().findColValue(
							"bd_project",
							"project_code ",
							"nvl(dr,0) = 0 and pk_project ='" + pk_project
									+ "'");
					double ncyfkje = Double.parseDouble(conWorkVO.getDef3()
							.toString());// 已付款金额
					double fkje = conWorkVO.getLocal_money_de().toDouble();// 付款金额
					if (htxmje.size() == 0) {
						Map<String, String> xmmap = new HashMap<String, String>();
						xmmap.put("htbh", htno);// 合同号
						xmmap.put("xmbh", project_code);// 项目编号
						xmmap.put("pk_project", pk_project);// 项目主键
						xmmap.put("gys", gyspk);// 供应商主键
						xmmap.put("fkje", fkje + "");// 付款金额
						xmmap.put("yfkje", ncyfkje + "");// 已付款金额
						htxmje.add(xmmap);
					} else {
						int i = 0;
						for (int xmls = 0; xmls < htxmje.size(); xmls++) {
							Map<String, String> map = htxmje.get(xmls);
							if (project_code.equals(map.get("xmbh"))) {
								double ncyfkjemx = Double.parseDouble(map.get(
										"yfkje").toString());
								map.remove("yfkje");
								map.put("yfkje", (ncyfkjemx + ncyfkje) + "");// 已付款金额

								double ncfkje = Double.parseDouble(map.get(
										"fkje").toString());
								map.remove("fkje");
								map.put("fkje", (ncfkje + fkje) + "");// 已付款金额
								i = 1;
							}
						}
						if (i == 0) {
							Map<String, String> xmmap = new HashMap<String, String>();
							xmmap.put("htbh", htno);// 合同号
							xmmap.put("xmbh", project_code);// 项目编号
							xmmap.put("pk_project", pk_project);// 项目主键
							xmmap.put("gys", gyspk);// 供应商主键
							xmmap.put("fkje", fkje + "");// 付款金额
							xmmap.put("yfkje", ncyfkje + "");// 已付款金额
							htxmje.add(xmmap);
						}
					}
				}
			}
			for (Map<String, String> xmmap : htxmje) {
				String htno = xmmap.get("htbh") + "";// 合同号
				String gyspk = xmmap.get("gys") + "";// 供应商
				String xmbh = xmmap.get("xmbh");// 项目编码
				// String pk_project = xmmap.get("pk_project");// 项目
				double fkje = Double.parseDouble(xmmap.get("fkje") + "");// 付款金额
				double yfkje = Double.parseDouble(xmmap.get("yfkje") + "");// 已付款金额
				DecimalFormat df = new DecimalFormat("#.00");
				fkje = Double.parseDouble(df.format(fkje));
				yfkje = Double.parseDouble(df.format(yfkje));
				System.out.println("fkje==" + fkje + "yfkje==" + yfkje);
				String sql = "SELECT PK_CONTR AS HTPK FROM PM_CONTR WHERE BILL_CODE = '"
						+ htno
						+ "' AND PK_SUPPLIER = '"
						+ gyspk
						+ "' AND DR = 0  AND (BILL_STATUS = 1 OR BILL_STATUS = 9)"
						+ "UNION ALL SELECT PK_CT_PU AS HTPK FROM CT_PU WHERE VBILLCODE = '"
						+ htno
						+ "' AND CVENDORID = '"
						+ gyspk
						+ "' AND DR = 0 AND (FSTATUSFLAG = 1 OR FSTATUSFLAG = 3)"
						+ "UNION ALL SELECT PM_FEEBALANCE AS HTPK FROM PM_FEEBALANCE_CT WHERE BILL_CODE = '"
						+ htno
						+ "' AND PK_SUPPLIER_NAME = '"
						+ gyspk
						+ "' AND DR = 0 AND FSTATUSFLAG = 1 AND VDEF19 <> 'Y' UNION ALL "
						+ "SELECT PK_FCT_AP FROM FCT_AP WHERE VBILLCODE = '"
						+ htno
						+ "' AND CVENDORID = '"
						+ gyspk
						+ "' AND DR = 0 ";
				Object htpk = bs.executeQuery(sql, new ColumnProcessor());
				if ((htpk != null && !"".equals(htpk + "") && !"~".equals(htpk
						+ ""))) {
					String jesql = "SELECT QKJE,LJFKJE FROM XM_HT WHERE HTBM = '"
							+ htno
							+ "' AND GYSPK = '"
							+ gyspk
							+ "' AND XMBM = '" + xmbh + "'";
					List<Object[]> jels = getDao.query(jesql);
					if (jels != null && jels.size() > 0 && jels.get(0) != null) {
						String qkje = jels.get(0)[0].toString();
						// String mxpk = conWorkVO.getPk_payitem();
						double dbqkje = Double.parseDouble(qkje);// 欠款金额
						double ljfkje = 0.0;
						if (jels.get(0)[1] != null
								&& !"~".equals(jels.get(0)[1])
								&& !"".equals(jels.get(0)[1])) {
							ljfkje = Double.parseDouble(jels.get(0)[1]
									.toString());// 累计付款金额
						}
						if (yfkje != ljfkje) {
							throw new BusinessException("项目[" + xmbh
									+ "]已付款金额[" + yfkje + "]不等于累计付款[" + ljfkje
									+ "]！");
						}
						if (dbqkje < fkje) {
							throw new BusinessException("项目[" + xmbh + "]付款金额["
									+ fkje + "]超过欠款金额[" + dbqkje + "]！");
						}

					}
				} else {
					throw new BusinessException("暂无该合同，请重新录入！");
				}
			}
		}

		/**
		 * 验证 付款单号[]的单据未审批通过 2021-11-01 liuli 修改组织、交易类型、 修改人 ：XBX
		 */
		String cfsave_org = (String) new HYPubBO().findColValue("sys_config",
				"config_value", "config_key='FKHTSaveCF_ORG'");
		String cfsave_jylx = (String) new HYPubBO().findColValue("sys_config",
				"config_value", "config_key='FKHTSaveCF_JYLX'");
		PayBillItemVO itemVO = (PayBillItemVO) childrenVOs[0];
		String mhtno = itemVO.getContractno() + "";// 合同号
		String mgyspk = itemVO.getSupplier() + "";// 供应商
		if (cfsave_org.contains(zz) && cfsave_jylx.contains(jylx)) {
			String fksql = "SELECT DISTINCT ZB.BILLNO,MX.SUPPLIER,ZB.APPROVESTATUS FROM AP_PAYBILL ZB "
					+ "LEFT JOIN AP_PAYITEM MX ON ZB.PK_PAYBILL = MX.PK_PAYBILL WHERE ZB.DR = 0 AND MX.DR = 0 "
					+ "AND MX.CONTRACTNO = '"
					+ mhtno
					+ "' AND MX.SUPPLIER = '"
					+ mgyspk + "' AND ZB.PK_TRADETYPE IN ('D3','F3-Cxx-01') ";
			if (headVO.getPrimaryKey() != null
					&& !"".equals(headVO.getPrimaryKey())) {
				fksql += "AND ZB.PK_PAYBILL <> '" + headVO.getPrimaryKey()
						+ "'";
			}
			System.out.println("查询付款SQL：" + fksql);
			List<Object[]> fkls = getDao.query(fksql);
			if (fkls != null && fkls.size() > 0 && fkls.get(0) != null) {
				for (int fki = 0; fki < fkls.size(); fki++) {
					String spzt = fkls.get(fki)[2] + "";// 单据状态
					if (!"1".equals(spzt)) {
						throw new BusinessException("付款单号[" + fkls.get(fki)[0]
								+ "" + "]的单据未审批通过！");
					}
				}
			}
			// 查询应付单
			String yfsql = "SELECT DISTINCT ZB.BILLNO,MX.SUPPLIER,ZB.APPROVESTATUS FROM AP_PAYABLEBILL ZB "
					+ "LEFT JOIN AP_PAYABLEITEM MX ON ZB.PK_PAYABLEBILL = MX.PK_PAYABLEBILL WHERE ZB.DR = 0 AND MX.DR = 0 "
					+ "AND MX.CONTRACTNO = '"
					+ mhtno
					+ "' AND MX.SUPPLIER = '"
					+ mgyspk
					+ "' AND ZB.PK_TRADETYPE IN ('D1','F1-Cxx-01') "
					+ " AND ZB.approvestatus IN (2,3)";
			System.out.println("查询应付SQL：" + yfsql);
			List<Object[]> yfls = getDao.query(yfsql);
			if (yfls != null && yfls.size() > 0 && yfls.get(0) != null) {
				for (int yfi = 0; yfi < yfls.size(); yfi++) {
					String spzt = yfls.get(yfi)[2] + "";// 单据状态
					if (!"1".equals(spzt)) {
						throw new BusinessException("应付单号[" + yfls.get(yfi)[0]
								+ "" + "]的单据未审批通过！");
					}
				}
			}
		}
		/**
		 * 验证 NC发票挂账金额小于NC累计付款金额，不允许保存！ 2021-11-01 liuli 修改组织、交易类型、 修改人 ：XBX
		 */
		String fklb = null;
		/* 付款类别 */
		if (null != headVO.getDef23()) {
			fklb = (String) getHyPubBO()
					.findColValue(
							"bd_defdoc",
							"code",
							"nvl(dr,0) = 0 and pk_defdoc = '"
									+ headVO.getDef23() + "'");
		}
		// 能投工程类别为“光伏项目材料（预付）”，不校验累计开票
		String gclb = "";
		/* 付款类别 */
		if (null != headVO.getDef75()) {
			gclb = (String) getHyPubBO()
					.findColValue(
							"bd_defdoc",
							"code",
							"nvl(dr,0) = 0 and pk_defdoc = '"
									+ headVO.getDef75() + "'");
		}
		if (!"1004".equals(gclb)) {
			Object fklbda = (Object) getHyPubBO().findColValue(
					"bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
							+ "FROM BD_DEFDOCLIST WHERE CODE = 'FK_FKLB')"
							+ " and code = '" + fklb + "'");
			if (fklbda == null) {
				String jesave_org = (String) new HYPubBO().findColValue(
						"sys_config", "config_value",
						"config_key='FKHTSaveJE_ORG'");
				String jesave_jylx = (String) new HYPubBO().findColValue(
						"sys_config", "config_value",
						"config_key='FKHTSaveJE_JYLX'");
				if (jesave_org.contains(zz) && jesave_jylx.contains(jylx)) {
					// 工程付款单 验证金额
					double yfmon = 0.00;// NC发票挂账金额
					double fkmon = headVO.getLocal_money()
							.setScale(2, UFDouble.ROUND_HALF_UP).toDouble();// NC累计付款金额+本次付款金额合计值
					Logger.error("当前付款金额：" + fkmon);
					double qkmon = 0.00;// 欠款
					String sql = "";
					if ("F3-Cxx-01".equals(jylx)) {
						sql = "SELECT HTBM,PK_SUPPLIER,FPJE,LJFKJE FROM VIEW_GCHTBYHT WHERE PK_SUPPLIER = '"
								+ mgyspk + "' AND " + "HTBM = '" + mhtno + "'";
					} else if ("D3".equals(jylx)) {
						// 材料付款单 验证金额
						sql = "SELECT HTBM,PK_SUPPLIER,FPJE,LJFKJE FROM VIEW_CGHTBYHT WHERE PK_SUPPLIER = '"
								+ mgyspk + "' AND " + "HTBM = '" + mhtno + "'";
					}
					Logger.error("查询NC发票挂账金额：" + sql);
					List<Object[]> yfls = getDao.query(sql);
					DecimalFormat decimalFormat = new DecimalFormat("0.00");
					if (yfls != null && yfls.size() > 0 && yfls.get(0) != null) {
						yfmon = Double.parseDouble(yfls.get(0)[2] + "");
						fkmon += Double.parseDouble(yfls.get(0)[3] + "");
						Logger.error("累计付款金额"
								+ Double.parseDouble(yfls.get(0)[3] + ""));
					}
					fkmon = Double.parseDouble(decimalFormat.format(fkmon));
					Logger.error("fkmon：" + fkmon);
					qkmon = yfmon - fkmon;// 欠款
					System.out.println("发票：" + yfmon + "付款：" + fkmon + "欠款："
							+ qkmon);
					Logger.error("发票：" + yfmon + "付款：" + fkmon + "欠款：" + qkmon);
					if (qkmon < 0) {
						throw new BusinessException("NC发票挂账金额小于NC累计付款金额，不允许保存！");
					}
				}
			}
		}
		/**
		 * 工程付款单和工程完工90天后，无竣工验收单不能发起付款；和工程完工180天后，该合同未完成结算单不允许发起付款。
		 */
		// conforg = null;// 暂时屏蔽，如需放开请删除当前行
		// 20231031 LL 角色下跳过校验
		String pk_user = InvocationInfoProxy.getInstance().getUserId();
		String querySql = "SELECT COUNT(YHJS.CUSERID) AS SL FROM SM_USER_ROLE YHJS LEFT JOIN SM_ROLE JS ON YHJS.PK_ROLE = JS.PK_ROLE WHERE JS.ROLE_CODE = 'FKDJY' AND YHJS.CUSERID = '"
				+ pk_user + "'";
		List<Object[]> bxls = getDao.query(querySql);
		if (bxls != null && Integer.parseInt(bxls.get(0)[0] + "") > 0) {
			return;
		} else {
			if (conforg != null && "F3-Cxx-01".equals(jylx)) {
				if (headVO.getCreationtime().afterDate(
						new UFDate("2023-05-22 00:00:00"))) {
					for (PayBillItemVO conWorkVO : childrenVOs) {

						String pk_project = conWorkVO.getProject();// 项目主键
						// 项目
						// ProjectHeadVO projectVO = (ProjectHeadVO)
						// getHyPubBO()
						// .queryByPrimaryKey(ProjectHeadVO.class,
						// pk_project);
						String strWhere = " nvl(dr,0) = 0  and pk_project ='"
								+ pk_project + "'";
						// 项目进度表 RL_TASKSCHEDULE
						RLTaskSchedule[] rlTaskSchedules = (RLTaskSchedule[]) getHyPubBO()
								.queryByCondition(RLTaskSchedule.class,
										strWhere);
						if (rlTaskSchedules != null
								&& rlTaskSchedules.length > 0) {
							// 项目进度
							RLTaskSchedule rlTaskSchedule = rlTaskSchedules[0];
							if (rlTaskSchedule.getTask_date() != null) {
								SimpleDateFormat formatter = new SimpleDateFormat(
										"yyyy-MM-dd hh:mm:ss");
								Date task_date = formatter.parse(rlTaskSchedule
										.getTask_date());
								Date curDate = new Date();

								// 和工程完工90天后，无竣工验收单不能发起付款
								if (differentDaysByDate(task_date, curDate) > 90) {
									String jgysql = " nvl(dr,0) = 0 and pk_project = '"
											+ pk_project + "'";
									ProjectCheckHeadVO[] headVOs = (ProjectCheckHeadVO[]) getHyPubBO()
											.queryByCondition(
													ProjectCheckHeadVO.class,
													jgysql);
									if (headVOs == null || headVOs.length <= 0) {
										throw new BusinessException(
												"请先新增竣工验收单！");
									} else {
										for (int i = 0; i < headVOs.length; i++) {
											ProjectCheckHeadVO projectCheckHeadVO = headVOs[i];
											if (projectCheckHeadVO
													.getBill_status() != 1) {
												throw new BusinessException(
														"竣工验收单["
																+ projectCheckHeadVO
																		.getBill_code()
																+ "]未审批通过！");
											}
										}
									}
								}
								// 和工程完工180天后，该合同未完成结算单不允许发起付款
								if (differentDaysByDate(task_date, curDate) > 180) {
									// 查询清单合同主键
									String htno = conWorkVO.getContractno()
											+ "";// 合同号
									String gyspk = conWorkVO.getSupplier() + "";// 供应商
									String sql = "SELECT PK_CONTR AS HTPK FROM PM_CONTR WHERE BILL_CODE = '"
											+ htno
											+ "' AND PK_SUPPLIER = '"
											+ gyspk
											+ "' AND DR = 0  AND (BILL_STATUS = 1 OR BILL_STATUS = 9)";
									String htjsql = " pk_contr in ( " + sql
											+ ") and dr = 0 ";
									ContractBalanceHeadVO[] hvos = (ContractBalanceHeadVO[]) getHyPubBO()
											.queryByCondition(
													ContractBalanceHeadVO.class,
													htjsql);
									if (hvos == null || hvos.length <= 0) {
										throw new BusinessException(
												"请先新增合同对应结算单！");
									}
									for (int i = 0; i < hvos.length; i++) {
										ContractBalanceHeadVO contractBalanceHeadVO = hvos[i];
										if (contractBalanceHeadVO
												.getBill_status() != 1) {
											throw new BusinessException(
													"结算单["
															+ contractBalanceHeadVO
																	.getBill_code()
															+ "]未审批通过！");
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public static int differentDaysByDate(Date date1, Date date2) {
		int days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
		return days;
	}

	protected void setParameter(String key, Object val) {
		if (m_keyHas == null) {
			m_keyHas = new Hashtable<String, Object>();
		}
		if (val != null) {
			m_keyHas.put(key, val);
		}
	}

	public String getCodeRemark() {
		return " arap action script not allowed to modify ,all rights reserved!";
	}

	public HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	public void setHyPubBO(HYPubBO hyPubBO) {
		this.hyPubBO = hyPubBO;
	}
}