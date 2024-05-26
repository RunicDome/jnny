package nc.ws.intf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.trade.business.HYPubBO;
import nc.pubitf.pmr.pm.IPmFeebalanceCtMaintain;
import nc.ui.pcm.utils.GetDao;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.cbs.CBSNodeVO;
import nc.vo.ct.purdaily.entity.AggCtPuVO;
import nc.vo.ct.purdaily.entity.CtPuBVO;
import nc.vo.ct.purdaily.entity.CtPuVO;
import nc.vo.pcm.contract.ContrHeadVO;
import nc.vo.pcm.contract.ContrWorksVO;
import nc.vo.pcm.contract.ContractBillVO;
import nc.vo.pcm.contractalter.ContrAlterBillVO;
import nc.vo.pcm.contractalter.ContrAlterBodyVO;
import nc.vo.pcm.contractalter.ContrAlterHeadVO;
import nc.vo.pcm.contractbalance.ContractBalanceBillVO;
import nc.vo.pcm.contractbalance.ContractBalanceBodyVO;
import nc.vo.pcm.contractbalance.ContractBalanceHeadVO;
import nc.vo.pcm.feebalance.FeeBalanceBodyVO;
import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pm.feebalance.PmFeebalanceBVO;
import nc.vo.pm.feebalance.PmFeebalanceHVO;
import nc.vo.pm.rlcontractalter.AggRLContractalterHVO;
import nc.vo.pm.rlcontractalter.RLContractalterBVO;
import nc.vo.pm.rlcontractalter.RLContractalterHVO;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.pub.MathTool;

import org.apache.commons.lang.StringUtils;

// 合同保存校验工具类
public class ContractUtils {
	private HYPubBO hyPubBO;

	public HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	// 返回是否允许保存,type=0（清单发包合同），type=1（多编码合同）
	public String checkIfSave(AbstractBill abstractBill, int type)
			throws BusinessException {
		String errInfo =  "";
		// return errInfo;
		if (type == 0) {
			// 清单发包合同
			ContractBillVO aggvo = (ContractBillVO) abstractBill;
			// 清单发包合同表头
			ContrHeadVO contrHeadVO = aggvo.getParentVO();
			// 清单发包合同基本
			ContrWorksVO[] bvos = (ContrWorksVO[]) aggvo
					.getChildren(ContrWorksVO.class);
			if (!"4Z01".equals(contrHeadVO.getSrc_bill_type())) {
				// 土建、安装、EPC合同，签订合同金额高于招标控制价不能发起合同
				if (contrHeadVO.getHdef55() != null
						&& ifCheckControl(contrHeadVO.getPk_contracttype())) {
					FeeBalanceBodyVO[] mxvos = (FeeBalanceBodyVO[]) getHyPubBO()
							.queryByCondition(
									FeeBalanceBodyVO.class,
									" nvl(dr,0) = 0 and pk_feebalance = '"
											+ contrHeadVO.getHdef55() + "'");
					// 外层循环合同明细，内层循环控制价明细
					for (ContrWorksVO htbvo : bvos) {
						Map<String, UFDouble> map = new HashMap<>();
						String htProjectAndCbs = htbvo.getPk_project()
								+ htbvo.getPk_cbsnode();// 项目+CBS
						UFDouble htmon = htbvo.getCurr_mny();// 合同金额
						for (FeeBalanceBodyVO kzjmxvo : mxvos) {
							String kzjProjectAndCbs = kzjmxvo.getPk_project()
									+ kzjmxvo.getPk_cbsnode();// 项目+CBS
							if (htProjectAndCbs.equals(kzjProjectAndCbs)) {
								map.put(kzjProjectAndCbs, new UFDouble(kzjmxvo.getDef8()));
							}
						}
						// 项目
						ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
								.queryByPrimaryKey(ProjectHeadVO.class,
										htbvo.getPk_project());
						// CBS
						CBSNodeVO cbsvo = (CBSNodeVO) getHyPubBO()
								.queryByPrimaryKey(CBSNodeVO.class,
										htbvo.getPk_cbsnode());
						if (map.get(htProjectAndCbs) == null) {
							errInfo = "项目[" + projectVO.getProject_code()
									+ "]CBS[" + cbsvo.getCode() + "]不在控制价范围内！";
							return errInfo;
						}
						if (htmon.compareTo(map.get(htProjectAndCbs)) > 0) {
							errInfo = "项目[" + projectVO.getProject_code()
									+ "]CBS[" + cbsvo.getCode()
									+ "]金额不能高于招标控制价金额！";
							return errInfo;
						}
					}
				} else {
					for (ContrWorksVO contrWorksVO : bvos) {
						String pk_project = contrHeadVO.getPk_project();// 项目主键
						if (pk_project != null) {
							String pk_cbs = contrWorksVO.getPk_cbsnode();// CBS
							if (pk_cbs == null) {
								errInfo = "根据[" + pk_cbs + "]获取CBS报错！";
								return errInfo;
							}
							try {
								errInfo += ifSaveByProjectAndCbs(pk_project,
										pk_cbs, contrHeadVO.getPrimaryKey(),
										contrWorksVO.getCurr_mny(),
										contrHeadVO.getSrc_pk_bill(),
										contrHeadVO.getPk_org());
								errInfo += ifSaveByProjectCbsAndSupplier(
										pk_project, pk_cbs,
										contrHeadVO.getPk_supplier(),
										contrHeadVO.getPrimaryKey(),
										contrHeadVO.getSrc_pk_bill(),
										contrHeadVO.getPk_contracttype());
							} catch (BusinessException e) {
								// TODO: handle exception
								e.printStackTrace();
								errInfo = e.getMessage();
							}
						}
					}
				}
			}
		} else if (type == 2) {
			// 清单发包合同补充协议
			ContrAlterBillVO aggvo = (ContrAlterBillVO) abstractBill;
			// 补充协议表头
			ContrAlterHeadVO contrHeadVO = aggvo.getParentVO();
			// 如果为拆分的，则跳出校验，因为多编码合同补充协议已经校验过了。
			if (contrHeadVO.getHdef7() == null) {
				// 补充协议基本
				ContrAlterBodyVO[] bodyVOS = (ContrAlterBodyVO[]) aggvo
						.getChildrenVO();
				for (ContrAlterBodyVO contrAlterBodyVO : bodyVOS) {
					String pk_project = contrHeadVO.getPk_project();// 项目主键
					if (pk_project != null) {
						String pk_cbs = contrAlterBodyVO.getPk_cbsnode();// CBS
						if (pk_cbs == null) {
							errInfo = "根据[" + pk_cbs + "]获取CBS报错！";
							return errInfo;
						}
						try {
							errInfo += ifSaveByProjectAndCbs(pk_project,
									pk_cbs, contrHeadVO.getPrimaryKey(),
									contrAlterBodyVO.getAlt_mny(), null,
									contrHeadVO.getPk_org());
						} catch (BusinessException e) {
							// TODO: handle exception
							e.printStackTrace();
							errInfo = e.getMessage();
						}
					}
				}
			}
		} else if (type == 1) {
			// 多编码合同
			AggPmFeebalance aggPmFeebalance = (AggPmFeebalance) abstractBill;
			// 合同表头
			PmFeebalanceHVO hvo = aggPmFeebalance.getParent();
			// 多编码合同明细
			PmFeebalanceBVO[] childvos = aggPmFeebalance.getChildrenVO();
			// 土建、安装、EPC合同，签订合同金额高于招标控制价不能发起合同
			if (hvo.getDef48() != null && ifCheckControl(hvo.getContracttype())) {
				FeeBalanceBodyVO[] mxvos = (FeeBalanceBodyVO[]) getHyPubBO()
						.queryByCondition(
								FeeBalanceBodyVO.class,
								" nvl(dr,0) = 0 and pk_feebalance = '"
										+ hvo.getDef48() + "'");
				Map<String, UFDouble> htMap = new HashMap<>();
				for (FeeBalanceBodyVO mxvo : mxvos) {
					UFDouble htmon = UFDouble.ZERO_DBL;
					String htProjectAndCbs = mxvo.getPk_project()
							+ mxvo.getPk_cbsnode();
					if (htMap.get(htProjectAndCbs) != null) {
						htmon.add(mxvo.getMoney());
						htMap.put(htProjectAndCbs, htmon);
					} else {
						htMap.put(htProjectAndCbs, mxvo.getMoney());
					}
				}
				// 外层循环合同Map，内层循环控制价明细
				for (String key : htMap.keySet()) {
					// 项目
					ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
							.queryByPrimaryKey(ProjectHeadVO.class,
									key.substring(0, 20));
					// CBS
					CBSNodeVO cbsvo = (CBSNodeVO) getHyPubBO()
							.queryByPrimaryKey(CBSNodeVO.class,
									key.substring(20, 40));
					Map<String, UFDouble> map = new HashMap<>();
					for (FeeBalanceBodyVO kzjmxvo : mxvos) {
						String kzjProjectAndCbs = kzjmxvo.getPk_project()
								+ kzjmxvo.getPk_cbsnode();// 项目+CBS
						if (key.equals(kzjProjectAndCbs)) {
							map.put(kzjProjectAndCbs, new UFDouble(kzjmxvo.getDef8()));
						}
					}
					if (map.get(key) == null) {
						errInfo = "项目[" + projectVO.getProject_code() + "]CBS["
								+ cbsvo.getCode() + "]不在控制价范围内！";
						return errInfo;
					}
					if (htMap.get(key).compareTo(map.get(key)) > 0) {
						errInfo = "项目[" + projectVO.getProject_code() + "]CBS["
								+ cbsvo.getCode() + "]金额不能高于招标控制价金额！";
						return errInfo;
					}
				}
			} else {
				for (PmFeebalanceBVO pmFeebalanceBVO : childvos) {
					if (pmFeebalanceBVO.getStatus() != VOStatus.DELETED) {
						String pk_project = pmFeebalanceBVO.getPk_project();// 项目主键
						if (pk_project != null) {
							String pk_cbs = pmFeebalanceBVO.getPk_cbsnode();// CBS
							if (pk_cbs == null) {
								errInfo = "根据[" + pk_cbs + "]获取CBS报错！";
								return errInfo;
							}
							try {
								errInfo += ifSaveByProjectAndCbs(pk_project,
										pk_cbs,
										aggPmFeebalance.getPrimaryKey(),
										pmFeebalanceBVO.getMoney(), null,
										aggPmFeebalance.getParent().getPk_org());
								errInfo += ifSaveByProjectCbsAndSupplier(
										pk_project, pk_cbs, aggPmFeebalance
												.getParent()
												.getPk_supplier_name(),
										aggPmFeebalance.getPrimaryKey(),
										aggPmFeebalance.getPrimaryKey(),
										aggPmFeebalance.getParent()
												.getContracttype());
							} catch (BusinessException e) {
								// TODO: handle exception
								e.printStackTrace();
								errInfo += e.getMessage();
							}
						}
					}
				}
			}
		} else if (type == 3) {
			// 采购合同
			AggCtPuVO aggVO = (AggCtPuVO) abstractBill;
			CtPuBVO[] bodyVOS = aggVO.getCtPuBVO();
			for (CtPuBVO ctPuBVO : bodyVOS) {
				if (ctPuBVO.getStatus() != VOStatus.DELETED) {
					try {
						errInfo += ifSaveByProject(ctPuBVO.getCbprojectid(),
								aggVO.getPrimaryKey(), ctPuBVO.getNorigmny());
					} catch (BusinessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						errInfo = e.getMessage();
					}
				}
			}
		} else if (type == 4) {
			/*
			 * 结算单 需求时间 2023-06-19 请求人 刘力
			 * 结算单保存时校验结算金额是否超过年度投资计划对应费用项值，取数关系1、项目+CBS 本次
			 * 结算金额+其他合同（已做结算取结算金额）+其他合同（未做结算取合同金额）组织：使用HTSAVEORG
			 */
			ContractBalanceBillVO aggVO = (ContractBalanceBillVO) abstractBill;
			ContractBalanceHeadVO hvo = aggVO.getParentVO();
			ContractBalanceBodyVO[] bvos = (ContractBalanceBodyVO[]) aggVO
					.getChildrenVO();
			for (ContractBalanceBodyVO bvo : bvos) {
				if (bvo.getStatus() != VOStatus.DELETED) {
					try {
						errInfo += balaIfSaveByProjectAndCbs(
								hvo.getPk_project(), bvo.getPk_cbsnode(),
								aggVO.getPrimaryKey(), bvo.getCurr_bala_mny(),
								hvo.getPk_org(), bvo.getPk_contr_works(),
								hvo.getPrimaryKey());
					} catch (BusinessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						errInfo = e.getMessage();
					}
				}
			}
		} else if (type == 5) {
			// 多编码合同补充协议
			AggRLContractalterHVO aggvo = (AggRLContractalterHVO) abstractBill;
			// 补充协议表头
			RLContractalterHVO contrHeadVO = aggvo.getParent();

			// 查询已拆分的清单发包合同是否生成结算单，如果有，则提示后不允许保存
			errInfo += ifSaveByContractAlter(aggvo.getChildrenVO());
			// 补充协议基本
			RLContractalterBVO[] bodyVOS = aggvo.getChildrenVO();
			for (RLContractalterBVO contrAlterBodyVO : bodyVOS) {
				String pk_project = contrAlterBodyVO.getPk_project();// 项目主键
				if (pk_project != null) {
					String pk_cbs = contrAlterBodyVO.getPk_cbsnode();// CBS
					if (pk_cbs == null) {
						errInfo = "根据[" + pk_cbs + "]获取CBS报错！";
						return errInfo;
					}
					try {
						errInfo += ifSaveByProjectAndCbs(pk_project, pk_cbs,
								contrHeadVO.getPrimaryKey(),
								contrAlterBodyVO.getAlt_mny(), null,
								contrHeadVO.getPk_org());
					} catch (BusinessException e) {
						// TODO: handle exception
						e.printStackTrace();
						errInfo += e.getMessage();
					}
				}
			}
		}
		return errInfo;
	}

	private String ifSaveByContractAlter(RLContractalterBVO[] bvos) {
		// TODO Auto-generated method stub
		String error = "";
		for (RLContractalterBVO bvo : bvos) {
			String pm_feebalance = bvo.getCsourcebillhid();// 多编码合同主键
			String pk_project = bvo.getPk_project();// 项目主键
			StringBuffer sql = new StringBuffer(
					" nvl(dr,0) = 0 and pk_project = '" + pk_project
							+ "' and src_pk_bill = '" + pm_feebalance + "'");
			try {
				ContrHeadVO[] hvos = (ContrHeadVO[]) getHyPubBO()
						.queryByCondition(ContrHeadVO.class, sql.toString());
				if (hvos != null && hvos.length > 0) {
					for (ContrHeadVO hvo : hvos) {
						// 循环查询合同是否有结算单
						ContractBalanceHeadVO[] jshvos = (ContractBalanceHeadVO[]) getHyPubBO()
								.queryByCondition(
										ContractBalanceHeadVO.class,
										"nvl(dr,0) = 0 and pk_contr = '"
												+ hvo.getPrimaryKey() + "'");
						if (jshvos != null && jshvos.length > 0) {
							error += jshvos[0].getBill_code() + ",";
						}
					}
				}
			} catch (UifException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				error += e.getMessage();
			}
		}
		if (StringUtils.isNotEmpty(error)) {
			error = error.substring(0, error.length() - 1) + "对应清单合同已完成结算！";
		}
		return error;
	}

	private String balaIfSaveByProjectAndCbs(String pk_project, String pk_cbs,
			String primaryKey, UFDouble curr_bala_mny, String pk_org,
			String pk_contr_works, String pk_contr_bal)
			throws BusinessException {
		// TODO Auto-generated method stub
		String error = "";
		// 项目HVO
		ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
				.queryByPrimaryKey(ProjectHeadVO.class, pk_project);
		String pk_projectclass = projectVO.getPk_projectclass();
		// 项目类型CBS-对应费用项
		CBSNodeVO cbsvo = (CBSNodeVO) getHyPubBO().queryByPrimaryKey(
				CBSNodeVO.class, pk_cbs);
		String fytype = cbsvo.getFree9();
		if (fytype == null) {
			error = "项目CBS未设置对应费用项！";
			return error;
		}
		StringBuffer sql = new StringBuffer(
				"select max(to_number(case "
						+ fytype
						+ " when '~' then '0' else "
						+ fytype
						+ " end)) from pm_yearplan_b where nvl(dr,0) = 0 and pk_project = '"
						+ pk_project + "' and pk_org = '" + pk_org + "'");
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		List<Object[]> planls = getDao.query(sql.toString());
		String feeTypeName = cbsvo.getName() + ""; // 费用名称
		// 年度投资计划（最大）费用项金额
		UFDouble fyvalue = UFDouble.ZERO_DBL;
		if (planls != null && planls.size() > 0 && planls.get(0) != null
				&& planls.get(0)[0] != null && !"".equals(planls.get(0)[0])
				&& !"null".equals(planls.get(0)[0])) {
			fyvalue = new UFDouble(planls.get(0)[0] + "");
		} else {
			error = "项目编码[" + projectVO.getProject_code() + "],费用["
					+ feeTypeName + "] 没有年度投资计划值，请添加后在进行操作";
			return error;
		}

		String wheresql = " FREE9 = '" + fytype
				+ "' AND DR = 0 AND (PK_ORG = '" + pk_org
				+ "'  OR PK_ORG = '~') AND (PK_PROJECT "
				+ "<> '~' OR PK_PROJECTTYPE = '" + pk_projectclass + "') ";
		Logger.error("结算单CBS查询WHERE:" + wheresql);
		System.out.println("结算单CBS查询WHERE:" + wheresql);
		CBSNodeVO[] cbsvos = (CBSNodeVO[]) getHyPubBO().queryByCondition(
				CBSNodeVO.class, wheresql);
		String pk_allcbs = "(";
		if (cbsvos != null && cbsvos.length > 0) {
			for (CBSNodeVO cbsNodeVO : cbsvos) {
				pk_allcbs += "'" + cbsNodeVO.getPk_cbsnode() + "',";
			}
			pk_allcbs = pk_allcbs.substring(0, pk_allcbs.length() - 1).concat(
					")");
		} else {
			pk_allcbs = "('')";
		}
		Logger.error("结算单pk_allcbs:" + pk_allcbs);
		System.out.println("结算单pk_allcbs:" + pk_allcbs);
		// 按项目+符合条件CBS查询金额
		String selExistMoneySql = "SELECT SUM(MONEY) MONEY FROM V_HTJS WHERE PK_ORG = '"
				+ pk_org
				+ "' AND "
				+ "PK_PROJECT = '"
				+ pk_project
				+ "' AND PK_CBSNODE IN "
				+ pk_allcbs
				+ " AND (PK_CONTR_WORKS <> '"
				+ pk_contr_works
				+ "' OR PK_CONTR_WORKS IS NULL) AND "
				+ "PK_CONTR_BAL <> '"
				+ pk_contr_bal + "'";
		Logger.error("结算单视图查询金额SQL:" + selExistMoneySql);
		System.out.println("结算单视图查询金额SQL:" + pk_allcbs);
		List<Object[]> jsjels = getDao.query(selExistMoneySql);
		UFDouble totalMoney = curr_bala_mny;// 合计金额
		if (jsjels != null && jsjels.size() > 0 && jsjels.get(0) != null
				&& jsjels.get(0)[0] != null) {
			totalMoney = new UFDouble(jsjels.get(0)[0] + "").add(totalMoney);
		}
		if (MathTool.compareTo(totalMoney, fyvalue) > 0) {
			error = "总金额[" + totalMoney + "]超出投资计划费用项最大值["
					+ fyvalue.setScale(2, UFDouble.ROUND_HALF_UP) + "]";
			return error;
		}
		return error;
	}

	/**
	 * @param pk_project项目主键
	 * @param pk_cbsCBS
	 * @param pk_primary合同主键
	 * @param curMoney当前明细行合同金额
	 * @return 报错内容
	 * @throws BusinessException
	 */
	private String ifSaveByProjectAndCbs(String pk_project, String pk_cbs,
			String pk_primary, UFDouble curMoney, String pm_feebalance,
			String pk_org) throws BusinessException {
		String error = "";
		// 项目HVO
		ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
				.queryByPrimaryKey(ProjectHeadVO.class, pk_project);
		String pk_projectclass = projectVO.getPk_projectclass();
		// 项目类型CBS-对应费用项
		CBSNodeVO cbsvo = (CBSNodeVO) getHyPubBO().queryByPrimaryKey(
				CBSNodeVO.class, pk_cbs);
		String fytype = cbsvo.getFree9();
		if (fytype == null) {
			error = "项目CBS未设置对应费用项！";
			return error;
		}
		StringBuffer sql = new StringBuffer(
				"select max(to_number(case "
						+ fytype
						+ " when '~' then '0' else "
						+ fytype
						+ " end)) from pm_yearplan_b where nvl(dr,0) = 0 and pk_project = '"
						+ pk_project + "' and pk_org = '" + pk_org + "'");
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		List<Object[]> planls = getDao.query(sql.toString());
		String feeTypeName = cbsvo.getName() + ""; // 费用名称
		// 年度投资计划（最大）费用项金额
		UFDouble fyvalue = UFDouble.ZERO_DBL;
		if (planls != null && planls.size() > 0 && planls.get(0) != null
				&& planls.get(0)[0] != null && !"".equals(planls.get(0)[0])
				&& !"null".equals(planls.get(0)[0])) {
			fyvalue = new UFDouble(planls.get(0)[0] + "");
		} else {
			error = "项目编码[" + projectVO.getProject_code() + "],费用["
					+ feeTypeName + "] 没有年度投资计划值，请添加后在进行操作";
			return error;
		}
		// 按项目+CBS取所有 清单发包合同+多编码合同 累计合同金额
		Map<String, String> map = new HashMap<>();
		map.put("pk_project", pk_project);// 项目
		map.put("pk_projectclass", pk_projectclass);// 项目类型PK
		map.put("fytype", fytype);// CBS
		map.put("pk_primary", pk_primary);// 合同主键
		UFDouble ylrTotal = UFDouble.ZERO_DBL;// 已录入合同金额
		List<Map<String, String>> list = getTotalContractMoneyByProjectAndCbs(
				map, pm_feebalance, pk_org);// 按条件查询所有合同
		if (list.size() > 0) {
			for (Map<String, String> map2 : list) {
				if (StringUtils.isNotEmpty(map2.get("money"))) {
					ylrTotal = ylrTotal.add(new UFDouble(map2.get("money")));
				}
			}
		}
		// 判断累计合同金额是否超过最大费用项控制值
		if ((ylrTotal.add(curMoney)).compareTo(fyvalue) > 0) {
			String errinfo = list.toString().replaceAll("qdhtbm", "清单合同");
			errinfo = errinfo.replaceAll("dbmhtbm", "多编码合同");
			errinfo = errinfo.replaceAll("money", "合同金额");
			error = "项目编码：" + projectVO.getProject_code() + ";费用项："
					+ cbsvo.getName() + ";已录入合同金额："
					+ ylrTotal.setScale(2, UFDouble.ROUND_HALF_UP) + "+本次合同金额："
					+ curMoney.setScale(2, UFDouble.ROUND_HALF_UP)
					+ ";超出投资计划费用项最大值["
					+ fyvalue.setScale(2, UFDouble.ROUND_HALF_UP) + "];"
					+ errinfo;
			return error;
		}
		return error;
	}

	/**
	 * 采购合同保存校验
	 * 
	 * @param pk_project
	 *            项目主键
	 * @param pk_primary
	 *            合同主键
	 * @param curMoney
	 *            当前合同金额
	 * @return
	 * @throws BusinessException
	 */
	private String ifSaveByProject(String pk_project, String pk_primary,
			UFDouble curMoney) throws BusinessException {
		String error = "";
		// 项目HVO
		ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
				.queryByPrimaryKey(ProjectHeadVO.class, pk_project);
		StringBuffer sql = new StringBuffer(
				"select max(to_number(case def26 when '~' then '0' else def26 end)) from pm_yearplan_b where nvl(dr,0) = 0 and pk_project = '"
						+ pk_project + "'");
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		List<Object[]> planls = getDao.query(sql.toString());
		String feeTypeName = "主材费用"; // 费用名称
		// 年度投资计划（最大）费用项金额
		UFDouble fyvalue = UFDouble.ZERO_DBL;
		if (planls != null && planls.size() > 0 && planls.get(0) != null
				&& planls.get(0)[0] != null && !"".equals(planls.get(0)[0])
				&& !"null".equals(planls.get(0)[0])) {
			fyvalue = new UFDouble(planls.get(0)[0] + "");
		} else {
			error = "项目编码[" + projectVO.getProject_code() + "],费用["
					+ feeTypeName + "] 没有年度投资计划值，请添加后在进行操作";
			return error;
		}
		// 按项目查询采购HVO
		StringBuffer ctsql = new StringBuffer(
				"nvl(dr,0) = 0 and cbprojectid = '" + pk_project + "'");
		if (StringUtils.isNotEmpty(pk_primary)) {
			ctsql.append(" and pk_ct_pu != '" + pk_primary + "'");
		}
		List<Map<String, String>> htls = new ArrayList<Map<String, String>>();
		// 查询采购合同表头HeadVO
		CtPuBVO[] ctPuBVOs = (CtPuBVO[]) getHyPubBO().queryByCondition(
				CtPuBVO.class, ctsql.toString());
		if (ctPuBVOs != null && ctPuBVOs.length > 0) {
			for (CtPuBVO ctPuBVO : ctPuBVOs) {
				CtPuVO hvo = (CtPuVO) getHyPubBO().queryByPrimaryKey(
						CtPuVO.class, ctPuBVO.getPk_ct_pu());
				// 查询最新版本的
				if (hvo.getBlatest().booleanValue()) {
					Map<String, String> curmap = new HashMap<>();// 当前合同信息
					UFDouble curTotal = UFDouble.ZERO_DBL;// 当前合同金额（按项目）
					// 明细表 ---合同基本
					CtPuBVO[] bvos = (CtPuBVO[]) getHyPubBO().queryByCondition(
							CtPuBVO.class,
							"nvl(dr,0) = 0 and pk_ct_pu = '"
									+ hvo.getPrimaryKey() + "'");
					if (bvos != null && bvos.length > 0) {
						for (CtPuBVO htPuBVO : bvos) {
							curTotal = curTotal.add(htPuBVO.getNorigtaxmny())
									.setScale(2, UFDouble.ROUND_HALF_UP);// 价税合计
						}
					}
					if (curTotal.compareTo(UFDouble.ZERO_DBL) != 0) {
						curmap.put("cthtbm", hvo.getVbillcode());
						curmap.put("money", curTotal.toString());
						htls.add(curmap);
					}
				}
			}
		}
		UFDouble ylrTotal = UFDouble.ZERO_DBL;// 已录入合同金额
		if (htls.size() > 0) {
			for (Map<String, String> map2 : htls) {
				ylrTotal = ylrTotal.add(new UFDouble(map2.get("money")));
			}
		}
		// 判断累计合同金额是否超过最大费用项控制值
		if ((ylrTotal.add(curMoney)).compareTo(fyvalue) > 0) {
			String errinfo = htls.toString().replaceAll("cthtbm", "采购合同");
			errinfo = errinfo.replaceAll("money", "合同金额");
			error = "项目编码：" + projectVO.getProject_code() + ";费用项："
					+ feeTypeName + ";已录入合同金额："
					+ ylrTotal.setScale(2, UFDouble.ROUND_HALF_UP) + "+本次合同金额："
					+ curMoney.setScale(2, UFDouble.ROUND_HALF_UP)
					+ ";超出投资计划费用项最大值["
					+ fyvalue.setScale(2, UFDouble.ROUND_HALF_UP) + "];"
					+ errinfo;
			return error;
		}
		return error;
	}

	// 按项目+CBS+pk_org取所有 清单发包合同+多编码合同 累计合同金额
	private List<Map<String, String>> getTotalContractMoneyByProjectAndCbs(
			Map<String, String> map, String pm_feebalance, String pk_org)
			throws BusinessException {
		List<Map<String, String>> htls = new ArrayList<Map<String, String>>();
		String pk_project = map.get("pk_project");// 项目主键
		String fytype = map.get("fytype");// 费用项
		String pk_projectclass = map.get("pk_projectclass");// 项目类型PK
		String pk_primary = map.get("pk_primary");// 过滤当前合同
		// 查询符合条件的CBS数据 20230829
		String wheresql = " FREE9 = '" + fytype
				+ "' AND DR = 0 AND( PK_ORG = '" + pk_org
				+ "' OR PK_ORG = '~') AND (PK_PROJECT "
				+ "<> '~' OR PK_PROJECTTYPE = '" + pk_projectclass + "') ";
		Logger.error("CBS查询WHERE:" + wheresql);
		System.out.println("CBS查询WHERE:" + wheresql);
		CBSNodeVO[] cbsvos = (CBSNodeVO[]) getHyPubBO().queryByCondition(
				CBSNodeVO.class, wheresql);
		String pk_allcbs = "(";
		if (cbsvos != null && cbsvos.length > 0) {
			for (CBSNodeVO cbsvo : cbsvos) {
				pk_allcbs += "'" + cbsvo.getPk_cbsnode() + "',";
			}
		}
		pk_allcbs = pk_allcbs.substring(0, pk_allcbs.length() - 1).concat(")");
		Logger.error("pk_allcbs:" + pk_allcbs);
		System.out.println("pk_allcbs:" + pk_allcbs);
		/** 查询清单发包合同累计合同金额 */
		// 按项目查询清单HVO
		StringBuffer sql = new StringBuffer(
				" (src_bill_type <> '4Z01' or src_bill_type is null) and pk_org = '"
						+ pk_org + "' and last_v_flag = 'Y' and pk_project = '"
						+ pk_project + "'");
		// sql.append(" and bill_status in (1, 9) ");
		if (StringUtils.isNotEmpty(pk_primary)) {
			sql.append(" and pk_contr != '" + pk_primary + "' ");
		}
		ContrHeadVO[] hvos = (ContrHeadVO[]) getHyPubBO().queryByCondition(
				ContrHeadVO.class, sql.toString());
		if (hvos != null && hvos.length > 0) {
			for (ContrHeadVO contrHeadVO : hvos) {
				String[] userObj = { ContractBillVO.class.getName(),
						ContrHeadVO.class.getName(),
						ContrWorksVO.class.getName() };
				ContractBillVO aggvo = (ContractBillVO) getHyPubBO()
						.queryBillVOByPrimaryKey(userObj,
								contrHeadVO.getPrimaryKey());
				Map<String, String> curmap = new HashMap<>();// 当前合同信息
				UFDouble curTotal = UFDouble.ZERO_DBL;// 当前合同金额（按项目+明细CBS）
				// 明细表 ---合同基本
				ContrWorksVO[] bvos = (ContrWorksVO[]) aggvo
						.getChildren(ContrWorksVO.class);
				if (bvos != null && bvos.length > 0) {
					for (ContrWorksVO contrWorksVO : bvos) {
						if (pk_allcbs.indexOf(contrWorksVO.getPk_cbsnode()) != -1) {
							curTotal = curTotal.add(contrWorksVO.getCurr_mny())
									.setScale(2, UFDouble.ROUND_HALF_UP);// 含税金额
						}
					}
				}
				if (curTotal.compareTo(UFDouble.ZERO_DBL) != 0) {
					curmap.put("qdhtbm", contrHeadVO.getBill_code());
					curmap.put("money", curTotal.toString());
					htls.add(curmap);
				}
			}
		}
		/** 查询多编码合同累计合同金额 */
		// 按项目查询多编码HVO
		StringBuffer dbmmxsql = new StringBuffer(
				"nvl(dr,0) = 0 and pk_project = '" + pk_project + "'");
		dbmmxsql.append(" and pk_cbsnode in " + pk_allcbs);
		if (StringUtils.isNotEmpty(pk_primary)) {
			dbmmxsql.append(" and pm_feebalance != '" + pk_primary + "' ");
		}
		if (StringUtils.isNotEmpty(pm_feebalance)) {
			dbmmxsql.append(" and pm_feebalance != '" + pm_feebalance + "' ");
		}
		PmFeebalanceBVO[] childvos = (PmFeebalanceBVO[]) getHyPubBO()
				.queryByCondition(PmFeebalanceBVO.class, dbmmxsql.toString());
		if (childvos != null && childvos.length > 0) {
			for (PmFeebalanceBVO pmFeebalanceBVO : childvos) {
				AggPmFeebalance[] aggvoS = (AggPmFeebalance[]) NCLocator
						.getInstance()
						.lookup(IPmFeebalanceCtMaintain.class)
						.queryObjectByPks(
								new String[] { pmFeebalanceBVO
										.getPm_feebalance() });
				AggPmFeebalance aggPmFeebalance = aggvoS.length <= 0 ? null
						: aggvoS[0];
				PmFeebalanceHVO pmFeebalanceHVO = aggPmFeebalance.getParent();
				if (pk_org.equals(pmFeebalanceHVO.getPk_org())) {
					Map<String, String> curmap = new HashMap<>();// 当前合同信息
					UFDouble curTotal = UFDouble.ZERO_DBL;// 当前合同金额（按项目+明细CBS）
					// if(pmFeebalanceHVO.getFstatusflag() == 1){
					curTotal = curTotal.add(pmFeebalanceBVO.getMoney())
							.setScale(2, UFDouble.ROUND_HALF_UP);
					// }
					if (curTotal.compareTo(UFDouble.ZERO_DBL) != 0) {
						curmap.put("dbmhtbm", pmFeebalanceHVO.getBill_code());
						curmap.put("money", curTotal.toString());
						htls.add(curmap);
					}
				}
			}
		}
		return htls;
	}

	// 项目+CBS+供应商 校验
	private String ifSaveByProjectCbsAndSupplier(String pk_project,
			String pk_cbs, String pk_supplier, String pk_primarykey,
			String pm_feebalance, String pk_contracttype)
			throws BusinessException {
		String errinfo = "";
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		// 查询清单合同是否有同类合同
		String qdsql = "SELECT ZB.PK_CONTR ZJ,ZB.BILL_CODE HTBM FROM PM_CONTR ZB LEFT JOIN PM_CONTR_WORKS MX ON "
				+ "ZB.PK_CONTR = MX.PK_CONTR WHERE ZB.LAST_V_FLAG = 'Y' AND "
				+ " ZB.PK_CONTRACTTYPE = '"
				+ pk_contracttype
				+ "' "
				+ "AND ZB.PK_PROJECT = '"
				+ pk_project
				+ "' "
				+ "AND ZB.PK_SUPPLIER = '"
				+ pk_supplier
				+ "' "
				+ "AND ZB.DR = 0 AND MX.DR = 0 "
				+ "AND ZB.PK_CONTR <> '"
				+ pk_primarykey + "' " + "AND MX.PK_CBSNODE = '" + pk_cbs + "'";
		Logger.error("清单项目+供应商 +CBSsql：" + qdsql);
		System.out.println("清单项目+供应商 +CBSsql：" + qdsql);
		List<Object[]> qdls = getDao.query(qdsql);
		if (qdls != null && qdls.size() > 0) {
			errinfo = "清单合同";
			for (int i = 0; i < qdls.size(); i++) {
				errinfo += "[" + qdls.get(i)[1] + "]";
			}
			errinfo += "同类合同系统已存在，不允许重复签订！";
		}
		// 查询多编码合同是否有同类合同
		String dbmsql = "SELECT HT.PM_FEEBALANCE ZJ,HT.BILL_CODE HTBM FROM PM_FEEBALANCE_CT HT "
				+ "LEFT JOIN PM_FEEBALANCE_CT_B MX ON HT.PM_FEEBALANCE = MX.PM_FEEBALANCE "
				+ "WHERE HT.DR = 0 AND MX.DR = 0 AND HT.PM_FEEBALANCE != '"
				+ pm_feebalance
				+ "' AND "
				+ "HT.PK_SUPPLIER_NAME = '"
				+ pk_supplier
				+ "' AND CONTRACTTYPE = '"
				+ pk_contracttype
				+ "'"
				+ " AND MX.PK_PROJECT = '"
				+ pk_project
				+ "' AND "
				+ "MX.PK_CBSNODE = '" + pk_cbs + "'";
		Logger.error("多编码项目+供应商 +CBSsql：" + dbmsql);
		System.out.println("多编码项目+供应商 +CBSsql：" + dbmsql);
		List<Object[]> dbmls = getDao.query(dbmsql);
		if (dbmls != null && dbmls.size() > 0) {
			errinfo += "多编码合同";
			for (int n = 0; n < dbmls.size(); n++) {
				errinfo += "[" + dbmls.get(n)[1] + "]";
			}
			errinfo += "同类合同系统已存在，不允许重复签订！";
		}
		return errinfo;
	}

	// 查询档案，判断合同类型是否需要校验控制价
	private boolean ifCheckControl(String pk_contracttype)
			throws BusinessException {
		Object conforg = (Object) getHyPubBO().findColValue(
				"bd_defdoc",
				"code",
				"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
						+ "FROM BD_DEFDOCLIST WHERE CODE = 'HTLXSAVE')"
						+ " and code = '" + pk_contracttype + "'");// 合同类型
		if (conforg != null) {
			return true;
		}
		return false;
	}
}
