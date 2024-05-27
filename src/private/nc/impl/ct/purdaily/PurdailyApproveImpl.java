package nc.impl.ct.purdaily;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.bs.trade.business.HYPubBO;
import nc.impl.ct.purdaily.action.PurdailyApproveAction;
import nc.impl.ct.purdaily.action.PurdailyFreezeAction;
import nc.impl.ct.purdaily.action.PurdailyModiDeleteAction;
import nc.impl.ct.purdaily.action.PurdailyModifySpAction;
import nc.impl.ct.purdaily.action.PurdailySendApproveAction;
import nc.impl.ct.purdaily.action.PurdailyTerminateAction;
import nc.impl.ct.purdaily.action.PurdailyUnApproveAction;
import nc.impl.ct.purdaily.action.PurdailyUnFreezeAction;
import nc.impl.ct.purdaily.action.PurdailyUnTerminateAction;
import nc.impl.ct.purdaily.action.PurdailyUnValidateAction;
import nc.impl.ct.purdaily.action.PurdailyUnsendApprove;
import nc.impl.ct.purdaily.action.PurdailyValidateAction;
import nc.impl.ct.purdaily.action.PurdailyValidateSpAction;
import nc.itf.ct.purdaily.IPurdailyApprove;
import nc.itf.portal.IGetIntoPortal;
import nc.itf.pu.m422x.IStoreReqAppMaintain;
import nc.itf.uap.pf.IPfExchangeService;
import nc.ui.pcm.utils.GetDao;
import nc.vo.bd.cust.CustSupplierVO;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.material.MaterialVO;
import nc.vo.bd.material.MaterialVersionVO;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.ct.price.entity.CtPriceHeaderVO;
import nc.vo.ct.purdaily.entity.AggCtPuVO;
import nc.vo.ct.purdaily.entity.CtPaymentVO;
import nc.vo.ct.purdaily.entity.CtPuBVO;
import nc.vo.ct.purdaily.entity.CtPuExecVO;
import nc.vo.ct.purdaily.entity.CtPuExpVO;
import nc.vo.ct.purdaily.entity.CtPuTermVO;
import nc.vo.ct.purdaily.entity.CtPuVO;
import nc.vo.ct.purdaily.entity.PayPlanVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.org.OrgVO;
import nc.vo.org.PurchaseOrgVO;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pu.m422x.entity.StoreReqAppItemVO;
import nc.vo.pu.m422x.entity.StoreReqAppVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.pub.MathTool;
import nc.vo.pubapp.pflow.PfUserObject;
import nc.vo.scmpub.util.ArrayUtil;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;
// import nc.ui.pcm.feebalance.util.GenSaveUtils;

// 采购合同
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PurdailyApproveImpl implements IPurdailyApprove {
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表名
	public static String WORKFLOWID_JNRL = "33";
	public static String WORKFLOWID_JNNY = "253";
	public static String WORKFLOWID_JNNYQYS = "571";

	public static String BODY_TABLE_NAME_JNRL = "formtable_main_130";
	public static String BODY_TABLE_NAME_JNNY = "formtable_main_299";
	public static String BODY_TABLE_NAME_JNNYQYS = "formtable_main_597";
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);

	// GenSaveUtils genSaveUtils = new GenSaveUtils();

	public String getDef2(String pk_org) throws BusinessException {
		String def2 = "";
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		if (null != orgVO) {
			def2 = orgVO.getDef2();
		}
		return def2;
	}

	public PurdailyApproveImpl() {
	}

	public AggCtPuVO[] approve(AggCtPuVO[] vos, AbstractCompiler2 script)
			throws BusinessException {
		try {
			AggCtPuVO[] aggvo = new PurdailyApproveAction()
					.approve(vos, script);
			// 调用采购合同转物资申请需求单
			HfWzxqdTs(vos);
			return aggvo;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	private void HfWzxqdTs(AggCtPuVO[] billVOs) throws BusinessException {
		for (int j = 0; j < billVOs.length; j++) {
			try {
				String pk_org = billVOs[j].getParentVO().getPk_org();
				OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(
						OrgVO.class, pk_org);
				String pk_fatherorg = orgVO.getPk_fatherorg();
				OrgVO orggVO = (OrgVO) new HYPubBO().queryByPrimaryKey(
						OrgVO.class, pk_fatherorg);
				String pk_grandpa = orggVO.getPk_fatherorg();

				// 供应商主键
				String cvendorid = billVOs[j].getParentVO().getCvendorid();
				// 判断是否属于能投和热力及子公司且 供应商是和丰才会自动推送和丰物资需求申请单
				if (("0001A21000000003U6L6".equals(pk_org)
						|| "0001A21000000003U6L6".equals(pk_fatherorg)
						|| "0001A21000000003U6L6".equals(pk_grandpa)
						|| "0001A110000000000HYQ".equals(pk_org)
						|| "0001A110000000000HYQ".equals(pk_fatherorg) || "0001A110000000000HYQ"
							.equals(pk_grandpa))
						&& "1001A110000000101E7V".equals(cvendorid)) {
					// 将采购合同转换为物资需求单
					IPfExchangeService pf = NCLocator.getInstance().lookup(
							IPfExchangeService.class);
					// Z2-01 原交易类型，422X-01 转换后交易类型
					StoreReqAppVO[] aggvo = (StoreReqAppVO[]) pf
							.runChangeDataAry("Z2", "422X", billVOs, null);

					for (int i = 0; i < aggvo.length; i++) {
						// 主表

						aggvo[i].getParentVO().setAttributeValue("pk_org",
								"0001A110000000055RP0");
						aggvo[i].getParentVO().setAttributeValue("pk_org_v",
								"0001A110000000055RP0");
						// 子表
						StoreReqAppItemVO[] childerVo = (StoreReqAppItemVO[]) aggvo[i]
								.getChildrenVO();
						childerVo[i].setPk_org("0001A110000000055RP0");
						childerVo[i].setPk_org_v("0001A110000000055RP0");
						aggvo[i].getHVO().setStatus(VOStatus.NEW);

					}
					// save保存
					(NCLocator.getInstance().lookup(IStoreReqAppMaintain.class))
							.save(aggvo);
				}
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				ExceptionUtils.marsh(e);
			}
		}

	}

	public AggCtPuVO[] freeze(AggCtPuVO[] vos, AggCtPuVO[] originBills)
			throws BusinessException {
		try {
			return new PurdailyFreezeAction().freeze(vos, originBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtPuVO[] modiDelete(AggCtPuVO[] vos) throws BusinessException {
		try {
			return new PurdailyModiDeleteAction().modiDelete(vos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtPuVO[] modify(AggCtPuVO[] vos, PfUserObject userConfirm,
			AggCtPuVO[] originBills) throws BusinessException {
		try {
			return new PurdailyModifySpAction().modify(vos, userConfirm,
					originBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtPuVO[] sendapprove(AggCtPuVO[] vos, AbstractCompiler2 script)
			throws BusinessException {
		try {

			AggCtPuVO[] aggVO = new PurdailySendApproveAction().sendapprove(
					vos, script);
			senOaData(vos);
			/*提交进共享屏蔽，上线再放开屏蔽*/
			// intoPortal(vos);
			return aggVO;

			// AggCtPuVO[] aggVO=new
			// PurdailySendApproveAction().sendapprove(vos, script);
			//
			// senOaData(aggVO);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 和丰采购合同提交后进共享（报销单），Z2->264X-Cxx-HFCGHT
	private void intoPortal(AggCtPuVO[] vos) throws BusinessException {
		// TODO Auto-generated method stub
		for (AggCtPuVO vo : vos) {
			CtPuVO hvo = vo.getParentVO();
			if ("0001A110000000055RP0".equals(hvo.getPk_org())) {
				IGetIntoPortal itf = NCLocator.getInstance().lookup(
						IGetIntoPortal.class);
				itf.changeIntoPortal(hvo.getCbilltypecode(), "264X-Cxx-HFCGHT",
						vo);
				// 清空驳回原因信息
				hvo.setVdef52(null);
				getHyPubBO().update(hvo);
			}
		}
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

	private void senOaData(AggCtPuVO[] billVOs) throws Exception {
		for (AggCtPuVO temp : billVOs) {
			// 推进度
			CtPuVO headvo = temp.getParentVO();
			CtPuBVO[] bodyS = temp.getCtPuBVO();
			for (CtPuBVO vo : bodyS) {
				String pk_org = headvo.getPk_org();// 组织
				// String transType = headvo.getTransi_type(); // 交易类型
				// String htlx = headvo.getCtrantypeid(); // 合同类型
				String pk_project = vo.getCbprojectid(); // 项目主键
				String pk_ct_pu_b = vo.getPk_ct_pu_b();// 采购合同明细主键
				// 推进度
				int orgFlag = checkOrg(pk_org);
				if (orgFlag == 1) {// 该组织需要推进度
					if (checkIfHaveWbs("Z2")) {
						// String wbs_name = htlxcheck(htlx);
						String wbs_name = "采购合同";
						if (wbs_name != null && !"".equals(wbs_name)) {
							// 判断当前任务是否完成进度，是的话不需要推，否则需要推
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
										if (!"".equals(pk_wbs)) {
											String updsql = "update ct_pu_b set vbdef11 = '"
													+ pk_wbs
													+ "' where pk_ct_pu_b "
													+ "= '" + pk_ct_pu_b + "'";
											getDao.executeUpdate(updsql);
										}
									}
								}
							} else {
								String pk_wbs = getWbsWork(pk_org, pk_project,
										wbs_name);
								String updsql = "update ct_pu_b set vbdef11 = '"
										+ pk_wbs
										+ "' where pk_ct_pu_b "
										+ "= '" + pk_ct_pu_b + "'";
								getDao.executeUpdate(updsql);
							}
						}
					}
				}
			}
			// 推进度结束
			CtPuVO hvo = temp.getParentVO();
			OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
					hvo.getPk_org());
			String ifbl = temp.getParentVO().getVdef16();// 是否补录
			if ("Z2".equals(hvo.getCbilltypecode())) {
				if (ifbl == null || "1001A2100000000B68C3".equals(ifbl)) {
					if ("1".equals(orgVO.getDef2())
							|| "HR".equals(orgVO.getDef2())) {
						OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(
								hvo.getPk_org(), "Z2");
						if (oaVo != null && oaVo.getIsdr() == 0) {
							WorkFId = oaVo.getFlowid();
							TableName = oaVo.getTablename();
							// 原非中台数据
							CtPuBVO[] bodyVOS = temp.getCtPuBVO();
							double se = 0.0;// 税额
							double wsje = 0.0;// 无税金额
							String xmbm = "";// 项目编码
							String xmmc = "";// 项目名称
							if (null != bodyVOS) {
								for (CtPuBVO tempb : bodyVOS) {
									if (tempb.getNtax() != null) {
										se += tempb.getNtax().toDouble();
									}
									if (tempb.getNorigmny() != null) {
										wsje += tempb.getNorigmny().toDouble();
									}
									if (tempb.getCbprojectid() != null) {
										if ("".equals(xmbm)) {
											xmbm = (String) getHyPubBO()
													.findColValue(
															"bd_project",
															"project_code",
															"nvl(dr,0) = 0 and pk_project='"
																	+ tempb.getCbprojectid()
																	+ "'");
											xmmc = (String) getHyPubBO()
													.findColValue(
															"bd_project",
															"project_name",
															"nvl(dr,0) = 0 and pk_project='"
																	+ tempb.getCbprojectid()
																	+ "'");
										}
									}
								}
							}
							BigDecimal se1 = new BigDecimal(se);
							BigDecimal wsje1 = new BigDecimal(wsje);
							se = se1.setScale(2, BigDecimal.ROUND_HALF_UP)
									.doubleValue();
							wsje = wsje1.setScale(2, BigDecimal.ROUND_HALF_UP)
									.doubleValue();
							// 获取主表数据
							JSONArray headData = getNewMainMap(hvo, se, wsje,
									xmbm, xmmc);
							// 获取子表数据
							JSONArray bodyData = getNewDtaileDataMap(temp);
							// 构造workflow信息
							OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
							workFlowVO.setPrimaryKey(temp.getPrimaryKey());
							workFlowVO.setPkGroup(hvo.getPk_group());
							workFlowVO.setPkOrg(hvo.getPk_org());
							workFlowVO.setBillMaker(hvo.getBillmaker());
							workFlowVO.setCreator(hvo.getCreator());
							workFlowVO.setBillCode("Z2");
							// 测试临时写死，后期改为接口获取
							workFlowVO.setWorkflowId(OaWorkFlowUtil
									.getOAFlowID(WorkFId));
							workFlowVO.setWorkflowName("采购合同");
							WorkFlowBill bill = OaWorkFlowUtil
									.getWorkFlowBill(workFlowVO);
							bill.setDef3("ZT");
							bill.setDef4(hvo.getVbillcode());
							// 制单人身份证号
							UserVO userVO = (UserVO) getHyPubBO()
									.queryByPrimaryKey(UserVO.class,
											hvo.getBillmaker());
							String idCard = (String) getHyPubBO().findColValue(
									"bd_psndoc",
									"id",
									"nvl(dr,0) = 0 and pk_psndoc='"
											+ userVO.getPk_psndoc() + "'");
							bill.setDef5(idCard);
							// 调用OA工具类同步数据至OA
							OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
						} else {
							CtPuBVO[] bodyVOS = temp.getCtPuBVO();
							double se = 0.0;// 税额
							double wsje = 0.0;// 无税金额
							String xmbm = "";// 项目编码
							String xmmc = "";// 项目名称
							if (null != bodyVOS) {
								for (CtPuBVO tempb : bodyVOS) {
									if (tempb.getNtax() != null) {
										se += tempb.getNtax().toDouble();
									}
									if (tempb.getNorigmny() != null) {
										wsje += tempb.getNorigmny().toDouble();
									}
									if (tempb.getCbprojectid() != null) {
										if ("".equals(xmbm)) {
											xmbm = (String) getHyPubBO()
													.findColValue(
															"bd_project",
															"project_code",
															"nvl(dr,0) = 0 and pk_project='"
																	+ tempb.getCbprojectid()
																	+ "'");
											xmmc = (String) getHyPubBO()
													.findColValue(
															"bd_project",
															"project_name",
															"nvl(dr,0) = 0 and pk_project='"
																	+ tempb.getCbprojectid()
																	+ "'");
										}
									}
								}
							}
							BigDecimal se1 = new BigDecimal(se);
							BigDecimal wsje1 = new BigDecimal(wsje);
							se = se1.setScale(2, BigDecimal.ROUND_HALF_UP)
									.doubleValue();
							wsje = wsje1.setScale(2, BigDecimal.ROUND_HALF_UP)
									.doubleValue();
							JSONArray headData = getMainMap(hvo, se, wsje);
							JSONArray bodyData = getDtaileDataMap(temp);
							// 能投契约锁测试 vdef34用印类型
							if (hvo.getVdef34() != null) {
								headData = getQYSMainMap(hvo, se, wsje, xmbm,
										xmmc);
								bodyData = getNTQYSDtaileDataMap(temp);
								OaWorkFlowUtil.sendOaData(headData, bodyData,
										getNTQYSWorkFlowBill(temp));
							} else {
								OaWorkFlowUtil.sendOaData(headData, bodyData,
										getWorkFlowBill(temp));
							}
						}
					} else if ("4".equals(getDef2(hvo.getPk_org()))) {
						OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(
								hvo.getPk_org(), "Z2");
						if (oaVo != null && oaVo.getIsdr() == 0) {
							WorkFId = oaVo.getFlowid();
							TableName = oaVo.getTablename();
							// 原非中台数据
							CtPuBVO[] bodyVOS = temp.getCtPuBVO();
							double se = 0.0;// 税额
							double wsje = 0.0;// 无税金额
							String xmbm = "";// 项目编码
							String xmmc = "";// 项目名称
							if (null != bodyVOS) {
								for (CtPuBVO tempb : bodyVOS) {
									if (tempb.getNtax() != null) {
										se += tempb.getNtax().toDouble();
									}
									if (tempb.getNorigmny() != null) {
										wsje += tempb.getNorigmny().toDouble();
									}
									if (tempb.getCbprojectid() != null) {
										if ("".equals(xmbm)) {
											xmbm = (String) getHyPubBO()
													.findColValue(
															"bd_project",
															"project_code",
															"nvl(dr,0) = 0 and pk_project='"
																	+ tempb.getCbprojectid()
																	+ "'");
											xmmc = (String) getHyPubBO()
													.findColValue(
															"bd_project",
															"project_name",
															"nvl(dr,0) = 0 and pk_project='"
																	+ tempb.getCbprojectid()
																	+ "'");
										}
									}
								}
							}
							BigDecimal se1 = new BigDecimal(se);
							BigDecimal wsje1 = new BigDecimal(wsje);
							se = se1.setScale(2, BigDecimal.ROUND_HALF_UP)
									.doubleValue();
							wsje = wsje1.setScale(2, BigDecimal.ROUND_HALF_UP)
									.doubleValue();
							// 获取主表数据
							JSONArray headData = getNewMainMap(hvo, se, wsje,
									xmbm, xmmc);
							// 获取子表数据
							JSONArray bodyData = getNewDtaileDataMap(temp);
							// 构造workflow信息
							OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
							workFlowVO.setPrimaryKey(temp.getPrimaryKey());
							workFlowVO.setPkGroup(hvo.getPk_group());
							workFlowVO.setPkOrg(hvo.getPk_org());
							workFlowVO.setBillMaker(hvo.getBillmaker());
							workFlowVO.setCreator(hvo.getCreator());
							workFlowVO.setBillCode("Z2");
							// 测试临时写死，后期改为接口获取
							workFlowVO.setWorkflowId(OaWorkFlowUtil
									.getOAFlowID(WorkFId));
							workFlowVO.setWorkflowName("采购合同");
							WorkFlowBill bill = OaWorkFlowUtil
									.getWorkFlowBill(workFlowVO);
							bill.setDef3("ZT");
							bill.setDef4(hvo.getVbillcode());
							// 制单人身份证号
							UserVO userVO = (UserVO) getHyPubBO()
									.queryByPrimaryKey(UserVO.class,
											hvo.getBillmaker());
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
			}
		}
	}

	// 根据合同类型查询对应任务名称-----参数：htlx==合同类型
	// private String htlxcheck(String htlx) throws DAOException {
	// String wbs_name = "";
	// GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
	// String wbsNameSql =
	// "SELECT SHORTNAME FROM BD_DEFDOC WHERE PK_DEFDOCLIST = "
	// + "'1001A11000000026ES9C' AND DR = 0 AND CODE = '" + htlx + "'";
	// List<Object[]> wbsNameLs = getDao.query(wbsNameSql);
	// if (wbsNameLs != null && wbsNameLs.size() > 0
	// && wbsNameLs.get(0)[0] != null) {
	// wbs_name = wbsNameLs.get(0)[0] + "";
	// }
	// System.out.println("wbsNameSq==" + wbsNameSql);
	// return wbs_name;
	// }

	private WorkFlowBill getWorkFlowBill(AggCtPuVO temp)
			throws BusinessException {
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
			workFlowBill.setWorkflowName("采购合同");
		} else {
			UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
					UserVO.class, temp.getParentVO().getBillmaker());
			workFlowBill.setWorkflowId(WORKFLOWID_JNRL);
			workFlowBill.setDef5(userVO.getUser_code());
			if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
				PsndocVO psndoc = (PsndocVO) getHyPubBO().queryByPrimaryKey(
						PsndocVO.class, userVO.getPk_psndoc());
				workFlowBill.setDef5(psndoc.getId());
				workFlowBill.setWorkflowId(WORKFLOWID_JNNY);
			}
			workFlowBill.setPk_group(temp.getParentVO().getPk_group());
			workFlowBill.setPk_org(temp.getParentVO().getPk_org());
			workFlowBill.setPk_bill(temp.getPrimaryKey());
			workFlowBill.setBill_code("Z2");
			workFlowBill.setUser_name(userVO.getUser_name());
			workFlowBill.setUser_code(userVO.getUser_code());
			workFlowBill.setBill_status("1");
			workFlowBill.setDef4(temp.getParentVO().getVbillcode());
			workFlowBill.setCreationtime(new UFDate().toStdString());
			workFlowBill.setCreator(temp.getParentVO().getCreator());
			workFlowBill.setWorkflowName("采购合同");
		}
		return workFlowBill;
	}

	private WorkFlowBill getNTQYSWorkFlowBill(AggCtPuVO temp)
			throws BusinessException {
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
			workFlowBill.setWorkflowName("采购合同");
		} else {
			UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
					UserVO.class, temp.getParentVO().getBillmaker());
			workFlowBill.setWorkflowId(WORKFLOWID_JNRL);
			workFlowBill.setDef5(userVO.getUser_code());
			if ("2".equals(getDef2(temp.getParentVO().getPk_org()))) {
				PsndocVO psndoc = (PsndocVO) getHyPubBO().queryByPrimaryKey(
						PsndocVO.class, userVO.getPk_psndoc());
				workFlowBill.setDef5(psndoc.getId());
				workFlowBill.setWorkflowId(WORKFLOWID_JNNY);
			}
			workFlowBill.setPk_group(temp.getParentVO().getPk_group());
			workFlowBill.setPk_org(temp.getParentVO().getPk_org());
			workFlowBill.setPk_bill(temp.getPrimaryKey());
			workFlowBill.setBill_code("Z2");
			workFlowBill.setUser_name(userVO.getUser_name());
			workFlowBill.setUser_code(userVO.getUser_code());
			workFlowBill.setBill_status("1");
			workFlowBill.setDef4(temp.getParentVO().getVbillcode());
			workFlowBill.setCreationtime(new UFDate().toStdString());
			workFlowBill.setCreator(temp.getParentVO().getCreator());
			workFlowBill.setWorkflowName("采购合同");
		}
		return workFlowBill;
	}

	private JSONArray getDtaileDataMap(AggCtPuVO aggvo) throws Exception {

		List dtlist = new ArrayList();
		Map ctPuBMap = getCtPuB(aggvo.getCtPuBVO(), 0);
		if (null != ctPuBMap) {
			dtlist.add(ctPuBMap);
		}

		Map ctPuTermMap = getCtPuTerm(aggvo.getCtPuTermVO(), 0);
		if (null != ctPuTermMap) {
			dtlist.add(ctPuTermMap);
		}

		Map ctPayPlanMap = getCtPayPlan(aggvo.getCtPayPlanVO(),
				aggvo.getParentVO(), 0);
		if (null != ctPayPlanMap) {
			dtlist.add(ctPayPlanMap);
		}

		Map ctPuExpMap = getCtPuExp(aggvo.getCtPuExpVO(), 0);
		if (null != ctPuExpMap) {
			dtlist.add(ctPuExpMap);
		}

		Map ctPaymentMap = getCtPayment(aggvo.getCtPaymentVO(), 0);
		if (null != ctPaymentMap) {
			dtlist.add(ctPaymentMap);
		}
		JSONArray dtlistString = JSONArray.fromObject(dtlist);
		return dtlistString;
	}

	private JSONArray getNTQYSDtaileDataMap(AggCtPuVO aggvo) throws Exception {

		List dtlist = new ArrayList();
		Map ctPuBMap = getCtPuB(aggvo.getCtPuBVO(), 1);
		if (null != ctPuBMap) {
			dtlist.add(ctPuBMap);
		}

		Map ctPuTermMap = getCtPuTerm(aggvo.getCtPuTermVO(), 1);
		if (null != ctPuTermMap) {
			dtlist.add(ctPuTermMap);
		}

		Map ctPayPlanMap = getCtPayPlan(aggvo.getCtPayPlanVO(),
				aggvo.getParentVO(), 1);
		if (null != ctPayPlanMap) {
			dtlist.add(ctPayPlanMap);
		}

		Map ctPuExpMap = getCtPuExp(aggvo.getCtPuExpVO(), 1);
		if (null != ctPuExpMap) {
			dtlist.add(ctPuExpMap);
		}

		Map ctPaymentMap = getCtPayment(aggvo.getCtPaymentVO(), 1);
		if (null != ctPaymentMap) {
			dtlist.add(ctPaymentMap);
		}
		JSONArray dtlistString = JSONArray.fromObject(dtlist);
		return dtlistString;
	}

	private Map getCtPuB(CtPuBVO[] bodyVOS, int ifnt) throws Exception {
		Map dtMap = new HashMap();
		dtMap.put("tableDBName", BODY_TABLE_NAME_JNRL + "_dt1");
		if ("2".equals(getDef2(bodyVOS[0].getPk_org()))) {
			if (ifnt == 0) {
				dtMap.put("tableDBName", BODY_TABLE_NAME_JNNY + "_dt1");
			} else if (ifnt == 1) {
				dtMap.put("tableDBName", BODY_TABLE_NAME_JNNY + "_dt1");
			}
		}
		List workflowRequestTableRecords = new ArrayList();
		for (CtPuBVO temp : bodyVOS) {
			Map workflowRequestTableFieldsMap = new HashMap();
			List workflowRequestTableFields = new ArrayList();

			if (temp.getPk_material() != null) {
				MaterialVersionVO materialVersionVO = (MaterialVersionVO) getHyPubBO()
						.queryByPrimaryKey(MaterialVersionVO.class,
								temp.getPk_material());

				if (null != materialVersionVO) {
					Map wlbm = new HashMap();
					wlbm.put("fieldName", "wlbm");
					wlbm.put("fieldValue", materialVersionVO.getCode());
					workflowRequestTableFields.add(wlbm);

					Map wlmc = new HashMap();
					wlmc.put("fieldName", "wlmc");
					wlmc.put("fieldValue", materialVersionVO.getName());
					workflowRequestTableFields.add(wlmc);

					Map gg = new HashMap();
					gg.put("fieldName", "gg");
					gg.put("fieldValue", materialVersionVO.getMaterialspec());
					workflowRequestTableFields.add(gg);

					String str = "0";
					if (null != materialVersionVO.getMaterialtype()) {
						str = materialVersionVO.getMaterialtype();
					}
					Map xh = new HashMap();
					xh.put("fieldName", "xh");
					xh.put("fieldValue", str);
					workflowRequestTableFields.add(xh);

				}
			}

			if (temp.getCbprojectid() != null) {
				String project_name = (String) getHyPubBO().findColValue(
						"bd_project",
						"project_name",
						"nvl(dr,0) = 0 and pk_project='"
								+ temp.getCbprojectid() + "'");
				Map xm = OaWorkFlowUtil.listAdd("xm", project_name);
				workflowRequestTableFields.add(xm);
				String project_code = (String) getHyPubBO().findColValue(
						"bd_project",
						"project_code",
						"nvl(dr,0) = 0 and pk_project='"
								+ temp.getCbprojectid() + "'");
				Map xmbm = OaWorkFlowUtil.listAdd("xmbm", project_code);
				workflowRequestTableFields.add(xmbm);
			}

			String dwname = (String) getHyPubBO().findColValue(
					"bd_measdoc",
					"name",
					"nvl(dr,0) = 0 and pk_measdoc='" + temp.getCastunitid()
							+ "'");
			Map dw = OaWorkFlowUtil.listAdd("dw", dwname);
			workflowRequestTableFields.add(dw);

			Map sl1 = OaWorkFlowUtil.listAdd("sl1", temp.getNastnum() + "");
			workflowRequestTableFields.add(sl1);

			Map bz = OaWorkFlowUtil.listAdd("bz", temp.getVmemo());
			workflowRequestTableFields.add(bz);

			String pk_measdoc = (String) getHyPubBO().findColValue(
					"bd_measdoc",
					"name",
					"nvl(dr,0) = 0 and pk_measdoc='" + temp.getCastunitid()
							+ "'");

			Map zdw = new HashMap();
			zdw.put("fieldName", "zdw");
			zdw.put("fieldValue", pk_measdoc);
			workflowRequestTableFields.add(zdw);

			Map zslsl = new HashMap();
			zslsl.put("fieldName", "zslsl");
			zslsl.put("fieldValue", temp.getNastnum().toString());
			workflowRequestTableFields.add(zslsl);

			Map hsl = new HashMap();
			hsl.put("fieldName", "hsl");
			hsl.put("fieldValue", temp.getVchangerate());
			workflowRequestTableFields.add(hsl);

			Map wsdj = new HashMap();
			wsdj.put("fieldName", "wsdj");
			wsdj.put("fieldValue", temp.getNqtorigprice().toString());
			workflowRequestTableFields.add(wsdj);

			String dj = ((UFDouble) (temp.getNqtorigtaxprice() == null ? new UFDouble(
					"0.00") : temp.getNqtorigtaxprice())).setScale(2,
					UFDouble.ROUND_HALF_UP).toString();
			Map hsdj = OaWorkFlowUtil.listAdd("hsdj", dj);
			workflowRequestTableFields.add(hsdj);

			Map zbbwsdj = new HashMap();
			zbbwsdj.put("fieldName", "zbbwsdj");
			zbbwsdj.put("fieldValue", temp.getNgprice().toString());
			workflowRequestTableFields.add(zbbwsdj);

			Map zbbhsdj = new HashMap();
			zbbhsdj.put("fieldName", "zbbhsdj");
			zbbhsdj.put("fieldValue", temp.getNgtaxprice().toString());
			workflowRequestTableFields.add(zbbhsdj);

			String ws = ((UFDouble) (temp.getNorigmny() == null ? new UFDouble(
					0.00) : temp.getNorigmny())).setScale(2,
					UFDouble.ROUND_HALF_UP).toString();
			Map wsje = OaWorkFlowUtil.listAdd("wsje", ws);
			workflowRequestTableFields.add(wsje);

			Map sl = new HashMap();
			sl.put("fieldName", "sl");
			sl.put("fieldValue", temp.getNtaxrate().toString());
			workflowRequestTableFields.add(sl);

			Map kslb = new HashMap();
			kslb.put("fieldName", "kslb");
			kslb.put("fieldValue", temp.getFtaxtypeflag().toString());
			workflowRequestTableFields.add(kslb);

			String vschemename = "0";
			if (null != temp.getCqpbaseschemeid()) {
				vschemename = (String) getHyPubBO().findColValue(
						"purp_qpscheme",
						"vschemename",
						"nvl(dr,0) = 0 and pk_qpscheme='"
								+ temp.getCqpbaseschemeid() + "'");
			}
			Map yzyjfa = new HashMap();
			yzyjfa.put("fieldName", "yzyjfa");
			yzyjfa.put("fieldValue", vschemename);
			workflowRequestTableFields.add(yzyjfa);

			String js = ((UFDouble) (temp.getNorigtaxmny() == null ? new UFDouble(
					"0.00") : temp.getNorigtaxmny())).setScale(2,
					UFDouble.ROUND_HALF_UP).toString();
			Map jshj = OaWorkFlowUtil.listAdd("jshj", js);
			workflowRequestTableFields.add(jshj);

			String str3 = "0";
			if (null != temp.getNmny()) {
				str3 = temp.getNmny().toString();
			}
			Map bbwsje = new HashMap();
			bbwsje.put("fieldName", "bbwsje");
			bbwsje.put("fieldValue", str3);
			workflowRequestTableFields.add(bbwsje);

			if (temp.getCtaxcodeid() != null) {
				String description = (String) getHyPubBO().findColValue(
						"bd_taxcode",
						"description",
						"nvl(dr,0) = 0 and pk_taxcode ='"
								+ temp.getCtaxcodeid() + "'");
				Map sm = new HashMap();
				sm.put("fieldName", "sm");
				sm.put("fieldValue", description);
				workflowRequestTableFields.add(sm);
			}

			String str = "0";
			if (null != temp.getNnosubtaxrate()) {
				str = temp.getNnosubtaxrate().toString();
			}
			Map bkdssl = new HashMap();
			bkdssl.put("fieldName", "bkdssl");
			bkdssl.put("fieldValue", str.toString());
			workflowRequestTableFields.add(bkdssl);

			String str1 = "0";
			if (null != temp.getNnosubtax()) {
				str1 = temp.getNnosubtax().toString();
			}
			Map bkdsje = new HashMap();
			bkdsje.put("fieldName", "bkdsje");
			bkdsje.put("fieldValue", str1.toString());
			workflowRequestTableFields.add(bkdsje);

			String str4 = "0";
			if (null != temp.getNcalcostmny()) {
				str4 = temp.getNcalcostmny().toString();
			}
			Map jcbje = new HashMap();
			jcbje.put("fieldName", "jcbje");
			jcbje.put("fieldValue", str4);
			workflowRequestTableFields.add(jcbje);

			Map ljddzsl = new HashMap();
			ljddzsl.put("fieldName", "ljddzsl");
			ljddzsl.put("fieldValue", temp.getNordnum().toString());
			workflowRequestTableFields.add(ljddzsl);

			Map ljddjshj = new HashMap();
			ljddjshj.put("fieldName", "ljddjshj");
			ljddjshj.put("fieldValue", temp.getNordsum().toString());
			workflowRequestTableFields.add(ljddjshj);

			String shortname = (String) getHyPubBO().findColValue(
					"org_financeorg",
					"name",
					"nvl(dr,0) = 0 and pk_financeorg ='"
							+ temp.getPk_financeorg() + "'");
			if (null != shortname) {
				Map cwzz = new HashMap();
				cwzz.put("fieldName", "cwzz");
				cwzz.put("fieldValue", shortname);
				workflowRequestTableFields.add(cwzz);
			}

			//
			// String str2="0";
			// if(null!=temp.getVmemo()){
			// str2=temp.getVmemo();
			// }
			// Map bz = new HashMap();
			// bz.put("fieldName", "bz");
			// bz.put("fieldValue", str2);
			// workflowRequestTableFields.add(bz);

			if (null != temp.getPk_ct_price()) {
				CtPriceHeaderVO ctPriceHeaderVO = (CtPriceHeaderVO) getHyPubBO()
						.queryByPrimaryKey(CtPriceHeaderVO.class,
								temp.getPk_ct_price());
				if (null != ctPriceHeaderVO) {

					Map htjg = new HashMap();
					htjg.put("fieldName", "htjg");
					htjg.put("fieldValue", ctPriceHeaderVO.getPk_ct_price());
					workflowRequestTableFields.add(htjg);

					Map htjgxxmc = new HashMap();
					htjgxxmc.put("fieldName", "htjgxxmc");
					htjgxxmc.put("fieldValue", ctPriceHeaderVO.getVname());
					workflowRequestTableFields.add(htjgxxmc);
				}
			}

			String r = "0";
			if (null != temp.getNschedulernum()) {
				r = temp.getNschedulernum() + "";
			}
			Map ljpcsl = new HashMap();
			ljpcsl.put("fieldName", "ljpcsl");
			ljpcsl.put("fieldValue", r);
			workflowRequestTableFields.add(ljpcsl);

			Map shkczz = new HashMap();
			shkczz.put("fieldName", "shkczz");
			shkczz.put("fieldValue", temp.getPk_arrvstock());
			workflowRequestTableFields.add(shkczz);

			Map htjg = OaWorkFlowUtil.listAdd("htjg", temp.getPk_ct_price());
			workflowRequestTableFields.add(htjg);

			String vname = (String) getHyPubBO().findColValue(
					"ct_price",
					"vname",
					"nvl(dr,0) = 0 and pk_ct_price   ='"
							+ temp.getPk_ct_price() + "'");
			Map htjgxxmc = OaWorkFlowUtil.listAdd("htjgxxmc", vname);
			workflowRequestTableFields.add(htjgxxmc);

			Map qgdh = new HashMap();
			qgdh.put("fieldName", "qgdh");
			qgdh.put("fieldValue",
					null != temp.getVpraybillcode() ? temp.getVpraybillcode()
							: "0");
			workflowRequestTableFields.add(qgdh);

			if ("1".equals(getDef2(bodyVOS[0].getPk_org()))) {
				Map se = OaWorkFlowUtil.listAdd("se", temp.getNtax() + "");
				workflowRequestTableFields.add(se);
			}

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}

		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	private Map getCtPuTerm(CtPuTermVO[] bodyVOS, int ifnt)
			throws BusinessException {
		Map dtMap = new HashMap();
		if (bodyVOS.length > 0) {
			dtMap.put("tableDBName", BODY_TABLE_NAME_JNRL + "_dt2");
			if ("2".equals(getDef2(bodyVOS[0].getPk_org()))) {
				if (ifnt == 0) {
					dtMap.put("tableDBName", BODY_TABLE_NAME_JNNY + "_dt2");
				} else if (ifnt == 1) {
					dtMap.put("tableDBName", BODY_TABLE_NAME_JNNY + "_dt2");
				}
			}
			List workflowRequestTableRecords = new ArrayList();
			if (null == bodyVOS || bodyVOS.length == 0) {
				return null;
			}
			for (CtPuTermVO temp : bodyVOS) {
				Map workflowRequestTableFieldsMap = new HashMap();
				List workflowRequestTableFields = new ArrayList();

				Map tkbm = new HashMap();
				String tkbmstr = (String) getHyPubBO().findColValue(
						"ct_termset",
						"vtermcode",
						"nvl(dr,0) = 0 and pk_ct_termset ='"
								+ temp.getVtermcode() + "'");
				// Map qxrq = new HashMap();
				tkbm.put("fieldName", "tkbm");
				tkbm.put("fieldValue", tkbmstr);
				workflowRequestTableFields.add(tkbm);

				Map tkmc = new HashMap();
				tkmc.put("fieldName", "tkmc");
				tkmc.put("fieldValue", temp.getVtermname());
				workflowRequestTableFields.add(tkmc);

				Map tklx = new HashMap();
				tklx.put("fieldName", "tklx");
				tklx.put("fieldValue", temp.getVtermtypename());
				workflowRequestTableFields.add(tklx);

				Map tknr = new HashMap();
				tknr.put("fieldName", "tknr");
				tknr.put("fieldValue", temp.getVtermcontent());
				workflowRequestTableFields.add(tknr);

				Map qtxx = new HashMap();
				qtxx.put("fieldName", "qtxx");
				qtxx.put("fieldValue", temp.getVotherinfo());
				workflowRequestTableFields.add(qtxx);

				Map bz = new HashMap();
				bz.put("fieldName", "bz");
				bz.put("fieldValue", temp.getVmemo());
				workflowRequestTableFields.add(bz);

				workflowRequestTableFieldsMap.put("recordOrder", "0");
				workflowRequestTableFieldsMap.put("workflowRequestTableFields",
						workflowRequestTableFields);
				workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
			}

			dtMap.put("workflowRequestTableRecords",
					workflowRequestTableRecords);
		} else {
			return null;
		}
		return dtMap;
	}

	private Map getCtPayPlan(PayPlanVO[] bodyVOS, CtPuVO hvo, int ifnt)
			throws Exception {
		if (null == bodyVOS || bodyVOS.length == 0) {
			return null;
		}
		Map dtMap = new HashMap();
		dtMap.put("tableDBName", BODY_TABLE_NAME_JNNY + "_dt3");
		if ("2".equals(getDef2(hvo.getPk_org()))) {
			if (ifnt == 0) {
				dtMap.put("tableDBName", BODY_TABLE_NAME_JNNY + "_dt3");
			} else if (ifnt == 1) {
				dtMap.put("tableDBName", BODY_TABLE_NAME_JNNY + "_dt3");
			}
		}
		List workflowRequestTableRecords = new ArrayList();
		for (PayPlanVO temp : bodyVOS) {
			Map workflowRequestTableFieldsMap = new HashMap();
			List workflowRequestTableFields = new ArrayList();

			String shortname = (String) getHyPubBO().findColValue(
					"org_financeorg",
					"shortname",
					"nvl(dr,0) = 0 and pk_taxcode ='" + temp.getPk_financeorg()
							+ "'");
			Map yfcwzz = new HashMap();
			yfcwzz.put("fieldName", "yfcwzz");
			yfcwzz.put("fieldValue", shortname);
			workflowRequestTableFields.add(yfcwzz);

			Map qsrq = new HashMap();
			qsrq.put("fieldName", "qsrq");
			qsrq.put("fieldValue", temp.getDbegindate().getYear() + "-"
					+ temp.getDbegindate().getStrMonth() + "-"
					+ temp.getDbegindate().getStrDay());
			workflowRequestTableFields.add(qsrq);

			Map zqdqr = new HashMap();
			zqdqr.put("fieldName", "zqdqr");
			zqdqr.put("fieldValue", temp.getDenddate().getYear() + "-"
					+ temp.getDenddate().getStrMonth() + "-"
					+ temp.getDenddate().getStrDay());
			workflowRequestTableFields.add(zqdqr);

			Map zqh = new HashMap();
			zqh.put("fieldName", "zqh");
			zqh.put("fieldValue", temp.getIaccounttermno());
			workflowRequestTableFields.add(zqh);

			Map zqts = new HashMap();
			zqts.put("fieldName", "zqts");
			zqts.put("fieldValue", temp.getIitermdays());
			workflowRequestTableFields.add(zqts);

			Map je = new HashMap();
			je.put("fieldName", "je");
			je.put("fieldValue", temp.getNorigmny());
			workflowRequestTableFields.add(je);

			Map bl = new HashMap();
			bl.put("fieldName", "nrate");
			bl.put("fieldValue", temp.getNrate());
			workflowRequestTableFields.add(bl);

			Map ljfkje = new HashMap();
			ljfkje.put("fieldName", "ljfkje");
			ljfkje.put("fieldValue", temp.getNaccumpayorgmny());
			workflowRequestTableFields.add(ljfkje);

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}

		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	private Map getCtPuExp(CtPuExpVO[] bodyVOS, int ifnt)
			throws BusinessException {
		if (null == bodyVOS || bodyVOS.length == 0) {
			return null;
		}
		Map dtMap = new HashMap();
		dtMap.put("tableDBName", BODY_TABLE_NAME_JNRL + "_dt4");
		if ("2".equals(getDef2(bodyVOS[0].getPk_org()))) {
			if (ifnt == 0) {
				dtMap.put("tableDBName", BODY_TABLE_NAME_JNNY + "_dt4");
			} else if (ifnt == 1) {
				dtMap.put("tableDBName", BODY_TABLE_NAME_JNNY + "_dt4");
			}
		}
		List workflowRequestTableRecords = new ArrayList();
		for (CtPuExpVO temp : bodyVOS) {
			Map workflowRequestTableFieldsMap = new HashMap();
			List workflowRequestTableFields = new ArrayList();

			Map fybm = new HashMap();
			fybm.put("fieldName", "fybm");
			fybm.put("fieldValue", temp.getVexpcode());
			workflowRequestTableFields.add(fybm);

			Map fymc = new HashMap();
			fymc.put("fieldName", "fymc");
			fymc.put("fieldValue", temp.getVexpname());
			workflowRequestTableFields.add(fymc);

			Map fyje = new HashMap();
			fyje.put("fieldName", "fyje");
			fyje.put("fieldValue", temp.getVexpsum());
			workflowRequestTableFields.add(fyje);

			Map bz = new HashMap();
			bz.put("fieldName", "bz");
			bz.put("fieldValue", temp.getVmemo());
			workflowRequestTableFields.add(bz);

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}

		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	private Map getCtPayment(CtPaymentVO[] bodyVOS, int ifnt)
			throws BusinessException {
		if (null == bodyVOS || bodyVOS.length == 0) {
			return null;
		}
		Map dtMap = new HashMap();
		dtMap.put("tableDBName", BODY_TABLE_NAME_JNRL + "_dt5");

		if ("2".equals(getDef2(bodyVOS[0].getPk_org()))) {
			if (ifnt == 0) {
				dtMap.put("tableDBName", BODY_TABLE_NAME_JNNY + "_dt5");
			} else if (ifnt == 1) {
				dtMap.put("tableDBName", BODY_TABLE_NAME_JNNY + "_dt5");
			}
		}
		List workflowRequestTableRecords = new ArrayList();
		for (CtPaymentVO temp : bodyVOS) {
			List workflowRequestTableFields = new ArrayList();

			Map workflowRequestTableFieldsMap = new HashMap();

			Map fkq = new HashMap();
			fkq.put("fieldName", "fkq");
			fkq.put("fieldValue", temp.getShoworder());
			workflowRequestTableFields.add(fkq);

			Map fkbl = new HashMap();
			fkbl.put("fieldName", "fkbl");
			fkbl.put("fieldValue", temp.getAccrate());
			workflowRequestTableFields.add(fkbl);

			Map yfk = new HashMap();
			yfk.put("fieldName", "yfk");
			yfk.put("fieldValue", temp.getPrepayment().booleanValue() ? "是"
					: "否");
			workflowRequestTableFields.add(yfk);

			String name = (String) getHyPubBO().findColValue(
					"bd_payperiod",
					"name",
					"nvl(dr,0) = 0 and pk_payperiod ='"
							+ temp.getPk_payperiod() + "'");
			Map qxrq = new HashMap();
			qxrq.put("fieldName", "qxrq");
			qxrq.put("fieldValue", name);
			workflowRequestTableFields.add(qxrq);

			Map qxrqyqts = new HashMap();
			qxrqyqts.put("fieldName", "qxrqyqts");
			qxrqyqts.put("fieldValue", temp.getEffectdateadddate());
			workflowRequestTableFields.add(qxrqyqts);

			Map czr = new HashMap();
			czr.put("fieldName", "czr");
			czr.put("fieldValue", temp.getOutaccountdate());
			workflowRequestTableFields.add(czr);

			Map zqts = new HashMap();
			zqts.put("fieldName", "zqts");
			zqts.put("fieldValue", temp.getPaymentday() + "");
			workflowRequestTableFields.add(zqts);

			Map gdjzr = new HashMap();
			gdjzr.put("fieldName", "gdjzr");
			gdjzr.put("fieldValue", temp.getCheckdata());
			workflowRequestTableFields.add(gdjzr);

			Map sxy = new HashMap();
			sxy.put("fieldName", "sxy");
			sxy.put("fieldValue", temp.getEffectmonth());
			workflowRequestTableFields.add(sxy);

			Map fjy = new HashMap();
			fjy.put("fieldName", "fjy");
			fjy.put("fieldValue", temp.getEffectaddmonth());
			workflowRequestTableFields.add(fjy);

			String jname = (String) getHyPubBO().findColValue(
					"bd_balatype",
					"name",
					"nvl(dr,0) = 0 and pk_balatype ='" + temp.getPk_balatype()
							+ "'");
			Map jsfs = new HashMap();
			jsfs.put("fieldName", "jsfs");
			jsfs.put("fieldValue", jname);
			workflowRequestTableFields.add(jsfs);

			Map bzj = new HashMap();
			bzj.put("fieldName", "bzj");
			bzj.put("fieldValue", temp.getIsdeposit().booleanValue() ? "是"
					: "否");
			workflowRequestTableFields.add(bzj);

			String xname = (String) getHyPubBO().findColValue(
					"bd_rateschema",
					"name",
					"nvl(dr,0) = 0 and  pk_rateschema  ='" + temp.getPk_rate()
							+ "'");
			Map xjzk = new HashMap();
			xjzk.put("fieldName", "xjzk");
			xjzk.put("fieldValue", xname);
			workflowRequestTableFields.add(xjzk);

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}

		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	private JSONArray getMainMap(CtPuVO hvo, double se, double wsje)
			throws BusinessException {
		List list = new ArrayList();

		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				hvo.getPk_org());
		// 所属组织名称
		Map sszzmc = OaWorkFlowUtil.listAdd("sszzmc", orgVO.getName());
		list.add(sszzmc);
		if (null != orgVO && "HR".equals(orgVO.getCode())) {

			Map szgs = OaWorkFlowUtil.listAdd("szgs", orgVO.getName());
			list.add(szgs);
		}
		if (null != orgVO && !"2".equals(getDef2(hvo.getPk_org()))) {

			Map xmzz = new HashMap();
			xmzz.put("fieldName", "xmzz");
			xmzz.put("fieldValue", orgVO.getName());
			list.add(xmzz);

			// XBX 20220414 新增税额，无税金额合计
			Map sen = OaWorkFlowUtil.listAdd("se", se + "");
			list.add(sen);
			Map wsjen = OaWorkFlowUtil.listAdd("wsje", wsje + "");
			list.add(wsjen);
		}
		if ("1".equals(getDef2(hvo.getPk_org()))
				|| "HR".equals(getDef2(hvo.getPk_org()))) {
			// 甲方签收人
			Map jfqsr = OaWorkFlowUtil.listAdd("jfqsr", hvo.getVdef21());
			list.add(jfqsr);
			// 甲方签收人联系电话
			Map jfqsrlxdh = OaWorkFlowUtil
					.listAdd("jfqsrlxdh", hvo.getVdef22());
			list.add(jfqsrlxdh);
			// 乙方指定的送达地点
			Map yfzddsddd = OaWorkFlowUtil
					.listAdd("yfzddsddd", hvo.getVdef23());
			list.add(yfzddsddd);
			// 乙方签收人
			Map yfqsr = OaWorkFlowUtil.listAdd("yfqsr", hvo.getVdef24());
			list.add(yfqsr);
			// 乙方签收人联系电话
			Map yfqsrlxdh = OaWorkFlowUtil
					.listAdd("yfqsrlxdh", hvo.getVdef25());
			list.add(yfqsrlxdh);
			// 乙方单位地址（乙方指定的送达地点）
			Map yfdwdz = OaWorkFlowUtil.listAdd("yfdwdz", hvo.getVdef23());
			list.add(yfdwdz);
			// 乙方电话（乙方签收人联系电话）
			Map yfdh = OaWorkFlowUtil.listAdd("yfdh", hvo.getVdef25());
			list.add(yfdh);
			// 乙方开户行
			String khyh = (String) getHyPubBO().findColValue(
					"bd_asslinenum",
					"name",
					"nvl(dr,0) = 0 and pk_asslinenum = '" + hvo.getVdef26()
							+ "'");
			Map khyhmap = OaWorkFlowUtil.listAdd("yfkhx", khyh);
			list.add(khyhmap);
			// 乙方银行账号
			Map yfyxzh = OaWorkFlowUtil.listAdd("yfyxzh", hvo.getVdef27());
			list.add(yfyxzh);
			// 乙方税号
			Map yfsh = OaWorkFlowUtil.listAdd("yfsh", hvo.getVdef28());
			list.add(yfsh);
			// 交货安装期限
			Map jhazqx = OaWorkFlowUtil.listAdd("jhazqx", hvo.getVdef20());
			list.add(jhazqx);
			// 契约锁合同模板
			String qyshtmb_id = (String) getHyPubBO().findColValue("bd_defdoc",
					"mnecode",
					"nvl(dr,0) = 0 and pk_defdoc = '" + hvo.getVdef12() + "'");
			if (qyshtmb_id == null || "".equals(qyshtmb_id) || qyshtmb_id == "")
				qyshtmb_id = "2936164539873645088";
			Map qyshtmbmc = OaWorkFlowUtil.listAdd("qyshtmb", qyshtmb_id);
			list.add(qyshtmbmc);
		}
		// 能投新增
		if ("2".equals(getDef2(hvo.getPk_org()))) {
			// 31 是否使用电子签章
			String sfsydzqz = (String) getHyPubBO().findColValue("bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoc = '" + hvo.getVdef31() + "'");
			if (sfsydzqz != null && "01".equals(sfsydzqz)) {
				sfsydzqz = "0";
			} else if (sfsydzqz != null && "02".equals(sfsydzqz)) {
				sfsydzqz = "1";
			}
			list.add(OaWorkFlowUtil.listAdd("sfsydzqz", sfsydzqz));
			// 32 税额
			list.add(OaWorkFlowUtil.listAdd("se", hvo.getVdef32()));
			// 33 税前金额
			list.add(OaWorkFlowUtil.listAdd("sqje", hvo.getVdef33()));
			// 34 用印类型
			String yylx = (String) getHyPubBO().findColValue("bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + hvo.getVdef34() + "'");
			list.add(OaWorkFlowUtil.listAdd("yylx", yylx));
		}
		Map htbh = new HashMap();
		htbh.put("fieldName", "htbh");
		htbh.put("fieldValue", hvo.getVbillcode());
		list.add(htbh);

		Map htmc = new HashMap();
		htmc.put("fieldName", "htmc");
		htmc.put("fieldValue", hvo.getCtname());
		list.add(htmc);

		String billtypename = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and pk_billtypeid  = '" + hvo.getCtrantypeid()
						+ "'");
		Map helx = new HashMap();
		helx.put("fieldName", "helx");
		helx.put("fieldValue", billtypename);
		list.add(helx);

		String zzmc = (String) getHyPubBO().findColValue("org_purchaseorg_v",
				"name",
				"nvl(dr,0) = 0 and pk_vid = '" + hvo.getPk_org_v() + "'");
		Map zdrzzmc = new HashMap();
		zdrzzmc.put("fieldName", "zdrzzmc");
		zdrzzmc.put("fieldValue", zzmc);
		list.add(zdrzzmc);

		Map bbh = new HashMap();
		bbh.put("fieldName", "bbh");
		bbh.put("fieldValue", hvo.getVersion().toString());
		list.add(bbh);

		Map htqdrq = new HashMap();
		htqdrq.put("fieldName", "htqdrq");
		htqdrq.put("fieldValue", hvo.getSubscribedate().getYear() + "-"
				+ hvo.getSubscribedate().getStrMonth() + "-"
				+ hvo.getSubscribedate().getStrDay());
		list.add(htqdrq);

		Map jhsxrq = new HashMap();
		jhsxrq.put("fieldName", "jhsxrq");
		jhsxrq.put("fieldValue", hvo.getValdate().getYear() + "-"
				+ hvo.getValdate().getStrMonth() + "-"
				+ hvo.getValdate().getStrDay());
		list.add(jhsxrq);

		Map jhzzrq = new HashMap();
		jhzzrq.put("fieldName", "jhzzrq");
		jhzzrq.put("fieldValue", hvo.getInvallidate().getYear() + "-"
				+ hvo.getInvallidate().getStrMonth() + "-"
				+ hvo.getInvallidate().getStrDay());
		list.add(jhzzrq);

		String name = (String) getHyPubBO()
				.findColValue(
						"bd_supplier",
						"name",
						"nvl(dr,0) = 0 and pk_supplier  = '"
								+ hvo.getCvendorid() + "'");
		Map gys = new HashMap();
		gys.put("fieldName", "gys");
		gys.put("fieldValue", name);
		list.add(gys);

		String str = "0";
		if (null != hvo.getCustunit()) {
			str = hvo.getCustunit();
		}
		Map dfdwsm = new HashMap();
		dfdwsm.put("fieldName", "dfdwsm");
		dfdwsm.put("fieldValue", str);
		list.add(dfdwsm);

		String manName = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc   = '" + hvo.getPersonnelid()
						+ "'");
		Map ry = new HashMap();
		ry.put("fieldName", "ry");
		ry.put("fieldValue", manName);
		list.add(ry);

		String dName = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and  pk_dept    = '" + hvo.getDepid() + "'");
		Map bm = new HashMap();
		bm.put("fieldName", "bm");
		bm.put("fieldValue", dName);
		list.add(bm);

		String jName = "0";
		if (null != hvo.getDeliaddr()) {
			jName = (String) getHyPubBO().findColValue(
					"bd_addressdoc",
					"name ",
					"nvl(dr,0) = 0 and pk_addressdoc   = '" + hvo.getDeliaddr()
							+ "'");
		}
		Map jhdd = new HashMap();
		jhdd.put("fieldName", "jhdd");
		jhdd.put("fieldValue", jName);
		list.add(jhdd);

		String bName = (String) getHyPubBO().findColValue(
				"bd_currtype",
				"name",
				"nvl(dr,0) = 0 and   pk_currtype     = '"
						+ hvo.getCorigcurrencyid() + "'");
		Map bz = new HashMap();
		bz.put("fieldName", "bz");
		bz.put("fieldValue", bName);
		list.add(bz);

		Map zbhl = new HashMap();
		zbhl.put("fieldName", "zbhl");
		zbhl.put("fieldValue", hvo.getNexchangerate().toString());
		list.add(zbhl);

		String fName = "0";
		if (null != hvo.getPk_payterm()) {
			fName = (String) getHyPubBO().findColValue(
					"bd_payment",
					"name ",
					"nvl(dr,0) = 0 and pk_payment    = '" + hvo.getPk_payterm()
							+ "'");
		}

		Map fkxy = new HashMap();
		fkxy.put("fieldName", "fkxy");
		fkxy.put("fieldValue", fName);
		list.add(fkxy);

		String str1 = "0";
		if (null != hvo.getNoriprepaylimitmny()) {
			str1 = hvo.getNoriprepaylimitmny().toString();
		}
		Map yfkxe = new HashMap();
		yfkxe.put("fieldName", "yfkxe");
		yfkxe.put("fieldValue", str1);
		list.add(yfkxe);

		Map htzt = new HashMap();
		htzt.put("fieldName", "htzt");
		htzt.put("fieldValue", hvo.getFstatusflag().toString());
		list.add(htzt);

		Map ww = new HashMap();
		ww.put("fieldName", "ww");
		ww.put("fieldValue", hvo.getBsc().booleanValue() ? "是" : "否");
		list.add(ww);

		Map yscddlzwhtzx = new HashMap();
		yscddlzwhtzx.put("fieldName", "yscddlzwhtzx");
		yscddlzwhtzx.put("fieldValue",
				hvo.getBordernumexec().booleanValue() ? "是" : "否");
		list.add(yscddlzwhtzx);

		Map ljfkze = new HashMap();
		ljfkze.put("fieldName", "ljfkze");
		ljfkze.put("fieldValue", hvo.getNtotalgpamount().toString());
		list.add(ljfkze);

		Map zsl = new HashMap();
		zsl.put("fieldName", "zsl");
		zsl.put("fieldValue", hvo.getNtotalastnum().toString());
		list.add(zsl);

		Map jshj = new HashMap();
		jshj.put("fieldName", "jshj");
		jshj.put("fieldValue", hvo.getNtotalorigmny().toString());
		list.add(jshj);

		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				hvo.getBillmaker());
		if (null != userVO) {

			Map zdrzj = new HashMap();
			zdrzj.put("fieldName", "zdrzj");
			zdrzj.put("fieldValue", userVO.getUser_code());
			list.add(zdrzj);

			Map zdrmc = new HashMap();
			zdrmc.put("fieldName", "zdrmc");
			zdrmc.put("fieldValue", userVO.getUser_name());
			list.add(zdrmc);
		}

		Map sfzh = new HashMap();
		String id = (String) getHyPubBO().findColValue("bd_psndoc", "id",
				"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
		if (null == id) {
			throw new BusinessException("制单人身份证号码未维护");
		}
		sfzh.put("fieldName", "sfzh");
		sfzh.put("fieldValue", id);
		list.add(sfzh);

		Map zdrq = new HashMap();
		zdrq.put("fieldName", "zdrq");
		zdrq.put("fieldValue", hvo.getDmakedate().getYear() + "-"
				+ hvo.getDmakedate().getStrMonth() + "-"
				+ hvo.getDmakedate().getStrDay());
		list.add(zdrq);

		Map zbqn = OaWorkFlowUtil.listAdd("zbqn", hvo.getVdef1());
		list.add(zbqn);

		String vdef6 = "0";
		if (null != hvo.getVdef6()) {
			String strWhere = "pk_defdoc = '" + hvo.getVdef6()
					+ "' and nvl(dr,0) = 0 ";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				vdef6 = defdoc[0].getName();
			}
		}
		Map xmjl = OaWorkFlowUtil.listAdd("xmjl", vdef6);
		list.add(xmjl);

		String vdef10 = "0";
		if (null != hvo.getVdef10()) {
			String strWhere = "pk_defdoc = '" + hvo.getVdef10()
					+ "' and nvl(dr,0) = 0 ";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				vdef10 = defdoc[0].getName();
			}
		}
		Map sftsht = OaWorkFlowUtil.listAdd("sftsht", vdef10);
		list.add(sftsht);

		String vdef11 = "0";
		if (null != hvo.getVdef11()) {
			String strWhere = "pk_defdoc = '" + hvo.getVdef11()
					+ "' and nvl(dr,0) = 0 ";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				vdef11 = defdoc[0].getName();
			}
		}
		Map sfmbht = OaWorkFlowUtil.listAdd("sfmbht", vdef11);
		list.add(sfmbht);

		String vdef12 = "0";
		if (null != hvo.getVdef12()) {
			String strWhere = "";
			if ("2".equals(getDef2(hvo.getPk_org()))) {
				strWhere = "pk_defdoc = '" + hvo.getVdef38()
						+ "' and nvl(dr,0) = 0 ";
			} else {
				strWhere = "pk_defdoc = '" + hvo.getVdef12()
						+ "' and nvl(dr,0) = 0 ";
			}
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				vdef12 = defdoc[0].getCode();
			}
		}
		Map htmb = OaWorkFlowUtil.listAdd("htmb", vdef12);
		list.add(htmb);

		String vdef15 = "0";
		if (null != hvo.getVdef15()) {
			String strWhere = "pk_defdoc = '" + hvo.getVdef15()
					+ "' and nvl(dr,0) = 0";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				vdef15 = defdoc[0].getName();
			}
		}
		Map zbfs = OaWorkFlowUtil.listAdd("zbfs", vdef15);
		list.add(zbfs);

		Map yhtje = OaWorkFlowUtil.listAdd("yhtje", hvo.getVdef17());
		list.add(yhtje);

		Map ygzje = OaWorkFlowUtil.listAdd("ygzje", hvo.getVdef13());
		list.add(ygzje);

		Map htzkje = OaWorkFlowUtil.listAdd("htzkje", hvo.getVdef19());
		list.add(htzkje);

		// Map yyfkje =OaWorkFlowUtil.listAdd("yyfkje",hvo.getVdef18());
		// list.add(yyfkje);

		Map bz1 = OaWorkFlowUtil.listAdd("bz1", hvo.getVdef7());
		list.add(bz1);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	// 能投契约锁
	private JSONArray getQYSMainMap(CtPuVO hvo, double se, double wsje,
			String xmbm, String xmmc) throws BusinessException {
		List list = new ArrayList();

		OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
				hvo.getPk_org());
		// 所属组织名称
		Map sszzmc = OaWorkFlowUtil.listAdd("sszzmc", orgVO.getName());
		list.add(sszzmc);
		if (null != orgVO && "02".equals(orgVO.getCode())) {

			Map szgs = OaWorkFlowUtil.listAdd("szgs", orgVO.getName());
			list.add(szgs);
		}
		if (null != orgVO && !"2".equals(getDef2(hvo.getPk_org()))) {

			Map xmzz = new HashMap();
			xmzz.put("fieldName", "xmzz");
			xmzz.put("fieldValue", orgVO.getName());
			list.add(xmzz);

			// XBX 20220414 新增税额，无税金额合计
			Map sen = OaWorkFlowUtil.listAdd("se", se + "");
			list.add(sen);
			Map wsjen = OaWorkFlowUtil.listAdd("wsje", wsje + "");
			list.add(wsjen);
		}
		if ("1".equals(getDef2(hvo.getPk_org()))) {
			// 甲方签收人
			Map jfqsr = OaWorkFlowUtil.listAdd("jfqsr", hvo.getVdef21());
			list.add(jfqsr);
			// 甲方签收人联系电话
			Map jfqsrlxdh = OaWorkFlowUtil
					.listAdd("jfqsrlxdh", hvo.getVdef22());
			list.add(jfqsrlxdh);
			// 乙方指定的送达地点
			Map yfzddsddd = OaWorkFlowUtil
					.listAdd("yfzddsddd", hvo.getVdef23());
			list.add(yfzddsddd);
			// 乙方签收人
			Map yfqsr = OaWorkFlowUtil.listAdd("yfqsr", hvo.getVdef24());
			list.add(yfqsr);
			// 乙方签收人联系电话
			Map yfqsrlxdh = OaWorkFlowUtil
					.listAdd("yfqsrlxdh", hvo.getVdef25());
			list.add(yfqsrlxdh);
			// 乙方单位地址（乙方指定的送达地点）
			Map yfdwdz = OaWorkFlowUtil.listAdd("yfdwdz", hvo.getVdef23());
			list.add(yfdwdz);
			// 乙方电话（乙方签收人联系电话）
			Map yfdh = OaWorkFlowUtil.listAdd("yfdh", hvo.getVdef25());
			list.add(yfdh);
			// 乙方开户行
			String khyh = (String) getHyPubBO().findColValue(
					"bd_asslinenum",
					"name",
					"nvl(dr,0) = 0 and pk_asslinenum = '" + hvo.getVdef26()
							+ "'");
			Map khyhmap = OaWorkFlowUtil.listAdd("yfkhx", khyh);
			list.add(khyhmap);
			// 乙方银行账号
			Map yfyxzh = OaWorkFlowUtil.listAdd("yfyxzh", hvo.getVdef27());
			list.add(yfyxzh);
			// 乙方税号
			Map yfsh = OaWorkFlowUtil.listAdd("yfsh", hvo.getVdef28());
			list.add(yfsh);
			// 交货安装期限
			Map jhazqx = OaWorkFlowUtil.listAdd("jhazqx", hvo.getVdef20());
			list.add(jhazqx);
			// 契约锁合同模板
			String qyshtmb_id = (String) getHyPubBO().findColValue("bd_defdoc",
					"mnecode",
					"nvl(dr,0) = 0 and pk_defdoc = '" + hvo.getVdef12() + "'");
			if (qyshtmb_id == null || "".equals(qyshtmb_id) || qyshtmb_id == "")
				qyshtmb_id = "2936164539873645088";
			Map qyshtmbmc = OaWorkFlowUtil.listAdd("qyshtmb", qyshtmb_id);
			list.add(qyshtmbmc);
		}
		// 能投新增
		if ("2".equals(getDef2(hvo.getPk_org()))) {
			if (!"".equals(xmbm)) {
				list.add(OaWorkFlowUtil.listAdd("xmbm", xmbm));
				list.add(OaWorkFlowUtil.listAdd("xm", xmmc));
			}
			// 31 是否使用电子签章
			String sfsydzqz = (String) getHyPubBO().findColValue("bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + hvo.getVdef31() + "'");
			// 01 是，02 否
			if (sfsydzqz != null && "01".equals(sfsydzqz)) {
				sfsydzqz = "0";
			} else if (sfsydzqz != null && "02".equals(sfsydzqz)) {
				sfsydzqz = "1";
			}
			list.add(OaWorkFlowUtil.listAdd("sfsydzqz", sfsydzqz));
			// 32 税额
			list.add(OaWorkFlowUtil.listAdd("se", se + ""));
			// 33 税前金额
			list.add(OaWorkFlowUtil.listAdd("sqje", wsje + ""));
			// 34 用印类型
			String yylx = (String) getHyPubBO().findColValue("bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + hvo.getVdef34() + "'");
			list.add(OaWorkFlowUtil.listAdd("yylx", yylx));
			String yylxbm = (String) getHyPubBO().findColValue("bd_defdoc",
					"shortname",
					"nvl(dr,0) = 0 and pk_defdoc = '" + hvo.getVdef34() + "'");
			list.add(OaWorkFlowUtil.listAdd("yzlxbm", yylxbm));
			// 甲方签收人
			Map jfqsr = OaWorkFlowUtil.listAdd("jfqsr", hvo.getVdef21());
			list.add(jfqsr);
			// 甲方指定送达地址
			Map jfzdsddz = OaWorkFlowUtil.listAdd("jfzdsddz", hvo.getVdef35());
			list.add(jfzdsddz);
			// 甲方签收人联系电话
			Map jfqsrlxdh = OaWorkFlowUtil.listAdd("jflxdh", hvo.getVdef22());
			list.add(jfqsrlxdh);
			// 乙方
			String name = (String) getHyPubBO().findColValue(
					"bd_supplier",
					"name",
					"nvl(dr,0) = 0 and pk_supplier  = '" + hvo.getCvendorid()
							+ "'");
			Map yf = OaWorkFlowUtil.listAdd("yf", name);
			list.add(yf);
			// 乙方签收人
			Map yfqsr = OaWorkFlowUtil.listAdd("yfqsr", hvo.getVdef24());
			list.add(yfqsr);
			// 乙方签收人联系电话
			Map yfqsrlxdh = OaWorkFlowUtil.listAdd("yflxdh", hvo.getVdef25());
			list.add(yfqsrlxdh);
			// 乙方指定送达地点
			Map yfdwdz = OaWorkFlowUtil.listAdd("yfzdsddz", hvo.getVdef23());
			list.add(yfdwdz);
			// 乙方电话（乙方签收人联系电话）
			Map yfdh = OaWorkFlowUtil.listAdd("yflxdh", hvo.getVdef25());
			list.add(yfdh);
			// 甲方授权人
			if (hvo.getVdef37() != null) {
				String jfsrqstr = (String) getHyPubBO().findColValue(
						"bd_psndoc",
						"name",
						"nvl(dr,0) = 0 and pk_psndoc = '" + hvo.getVdef37()
								+ "'");
				Map jfsqr = OaWorkFlowUtil.listAdd("jfsqr", jfsrqstr);
				list.add(jfsqr);
				String jfsrqid = (String) getHyPubBO().findColValue(
						"bd_psndoc",
						"id",
						"nvl(dr,0) = 0 and pk_psndoc = '" + hvo.getVdef37()
								+ "'");
				Map jfsqrsfzh = OaWorkFlowUtil.listAdd("jfsqrsfzh", jfsrqid);
				list.add(jfsqrsfzh);
			}
			/*
			 * // 乙方经办人签字位置 Map yfjbrqzwz = OaWorkFlowUtil.listAdd("yfjbrqzwz",
			 * hvo.getVdef37()); list.add(yfjbrqzwz); // 乙方盖章位置 Map yfgzwz =
			 * OaWorkFlowUtil.listAdd("yfgzwz", hvo.getVdef38());
			 * list.add(yfgzwz); // 甲方经办人签字位置 Map jfjbrqzwz =
			 * OaWorkFlowUtil.listAdd("jfjbrqzwz", hvo.getVdef39());
			 * list.add(jfjbrqzwz); // 甲方盖章位置 Map jfgzwz =
			 * OaWorkFlowUtil.listAdd("jfgzwz", hvo.getVdef40());
			 * list.add(jfgzwz);
			 */
		}
		Map htbh = new HashMap();
		htbh.put("fieldName", "htbh");
		htbh.put("fieldValue", hvo.getVbillcode());
		list.add(htbh);

		Map htmc = new HashMap();
		htmc.put("fieldName", "htmc");
		htmc.put("fieldValue", hvo.getCtname());
		list.add(htmc);

		String billtypename = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and pk_billtypeid  = '" + hvo.getCtrantypeid()
						+ "'");
		Map helx = new HashMap();
		helx.put("fieldName", "helx");
		helx.put("fieldValue", billtypename);
		list.add(helx);

		String zzmc = (String) getHyPubBO().findColValue("org_purchaseorg_v",
				"name",
				"nvl(dr,0) = 0 and pk_vid = '" + hvo.getPk_org_v() + "'");
		Map zdrzzmc = new HashMap();
		zdrzzmc.put("fieldName", "zdrzzmc");
		zdrzzmc.put("fieldValue", zzmc);
		list.add(zdrzzmc);

		Map bbh = new HashMap();
		bbh.put("fieldName", "bbh");
		bbh.put("fieldValue", hvo.getVersion().toString());
		list.add(bbh);

		Map htqdrq = new HashMap();
		htqdrq.put("fieldName", "htqdrq");
		htqdrq.put("fieldValue", hvo.getSubscribedate().getYear() + "-"
				+ hvo.getSubscribedate().getStrMonth() + "-"
				+ hvo.getSubscribedate().getStrDay());
		list.add(htqdrq);

		Map jhsxrq = new HashMap();
		jhsxrq.put("fieldName", "jhsxrq");
		jhsxrq.put("fieldValue", hvo.getValdate().getYear() + "-"
				+ hvo.getValdate().getStrMonth() + "-"
				+ hvo.getValdate().getStrDay());
		list.add(jhsxrq);

		Map jhzzrq = new HashMap();
		jhzzrq.put("fieldName", "jhzzrq");
		jhzzrq.put("fieldValue", hvo.getInvallidate().getYear() + "-"
				+ hvo.getInvallidate().getStrMonth() + "-"
				+ hvo.getInvallidate().getStrDay());
		list.add(jhzzrq);

		String name = (String) getHyPubBO()
				.findColValue(
						"bd_supplier",
						"name",
						"nvl(dr,0) = 0 and pk_supplier  = '"
								+ hvo.getCvendorid() + "'");
		Map gys = new HashMap();
		gys.put("fieldName", "gys");
		gys.put("fieldValue", name);
		list.add(gys);

		String str = "0";
		if (null != hvo.getCustunit()) {
			str = hvo.getCustunit();
		}
		Map dfdwsm = new HashMap();
		dfdwsm.put("fieldName", "dfdwsm");
		dfdwsm.put("fieldValue", str);
		list.add(dfdwsm);

		String manName = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc   = '" + hvo.getPersonnelid()
						+ "'");
		Map ry = new HashMap();
		ry.put("fieldName", "ry");
		ry.put("fieldValue", manName);
		list.add(ry);

		String dName = (String) getHyPubBO().findColValue("org_dept", "name",
				"nvl(dr,0) = 0 and  pk_dept    = '" + hvo.getDepid() + "'");
		Map bm = new HashMap();
		bm.put("fieldName", "bm");
		bm.put("fieldValue", dName);
		list.add(bm);

		String jName = "0";
		if (null != hvo.getDeliaddr()) {
			jName = (String) getHyPubBO().findColValue(
					"bd_addressdoc",
					"name ",
					"nvl(dr,0) = 0 and pk_addressdoc   = '" + hvo.getDeliaddr()
							+ "'");
		}
		Map jhdd = new HashMap();
		jhdd.put("fieldName", "jhdd");
		jhdd.put("fieldValue", jName);
		list.add(jhdd);

		String bName = (String) getHyPubBO().findColValue(
				"bd_currtype",
				"name",
				"nvl(dr,0) = 0 and   pk_currtype     = '"
						+ hvo.getCorigcurrencyid() + "'");
		Map bz = new HashMap();
		bz.put("fieldName", "bz");
		bz.put("fieldValue", bName);
		list.add(bz);

		Map zbhl = new HashMap();
		zbhl.put("fieldName", "zbhl");
		zbhl.put("fieldValue", hvo.getNexchangerate().toString());
		list.add(zbhl);

		String fName = "0";
		if (null != hvo.getPk_payterm()) {
			fName = (String) getHyPubBO().findColValue(
					"bd_payment",
					"name ",
					"nvl(dr,0) = 0 and pk_payment    = '" + hvo.getPk_payterm()
							+ "'");
		}

		Map fkxy = new HashMap();
		fkxy.put("fieldName", "fkxy");
		fkxy.put("fieldValue", fName);
		list.add(fkxy);

		String str1 = "0";
		if (null != hvo.getNoriprepaylimitmny()) {
			str1 = hvo.getNoriprepaylimitmny().toString();
		}
		Map yfkxe = new HashMap();
		yfkxe.put("fieldName", "yfkxe");
		yfkxe.put("fieldValue", str1);
		list.add(yfkxe);

		Map htzt = new HashMap();
		htzt.put("fieldName", "htzt");
		htzt.put("fieldValue", hvo.getFstatusflag().toString());
		list.add(htzt);

		Map ww = new HashMap();
		ww.put("fieldName", "ww");
		ww.put("fieldValue", hvo.getBsc().booleanValue() ? "是" : "否");
		list.add(ww);

		Map yscddlzwhtzx = new HashMap();
		yscddlzwhtzx.put("fieldName", "yscddlzwhtzx");
		yscddlzwhtzx.put("fieldValue",
				hvo.getBordernumexec().booleanValue() ? "是" : "否");
		list.add(yscddlzwhtzx);

		Map ljfkze = new HashMap();
		ljfkze.put("fieldName", "ljfkze");
		ljfkze.put("fieldValue", hvo.getNtotalgpamount().toString());
		list.add(ljfkze);

		Map zsl = new HashMap();
		zsl.put("fieldName", "zsl");
		zsl.put("fieldValue", hvo.getNtotalastnum().toString());
		list.add(zsl);

		Map jshj = new HashMap();
		jshj.put("fieldName", "jshj");
		jshj.put("fieldValue", hvo.getNtotalorigmny().toString());
		list.add(jshj);

		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				hvo.getBillmaker());
		if (null != userVO) {

			Map zdrzj = new HashMap();
			zdrzj.put("fieldName", "zdrzj");
			zdrzj.put("fieldValue", userVO.getUser_code());
			list.add(zdrzj);

			Map zdrmc = new HashMap();
			zdrmc.put("fieldName", "zdrmc");
			zdrmc.put("fieldValue", userVO.getUser_name());
			list.add(zdrmc);
		}

		Map sfzh = new HashMap();
		String id = (String) getHyPubBO().findColValue("bd_psndoc", "id",
				"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
		if (null == id) {
			throw new BusinessException("制单人身份证号码未维护");
		}
		sfzh.put("fieldName", "sfzh");
		sfzh.put("fieldValue", id);
		list.add(sfzh);

		Map zdrq = new HashMap();
		zdrq.put("fieldName", "zdrq");
		zdrq.put("fieldValue", hvo.getDmakedate().getYear() + "-"
				+ hvo.getDmakedate().getStrMonth() + "-"
				+ hvo.getDmakedate().getStrDay());
		list.add(zdrq);

		Map zbqn = OaWorkFlowUtil.listAdd("zbqn", hvo.getVdef1());
		list.add(zbqn);

		String vdef6 = "0";
		if (null != hvo.getVdef6()) {
			String strWhere = "pk_defdoc = '" + hvo.getVdef6()
					+ "' and nvl(dr,0) = 0 ";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				vdef6 = defdoc[0].getName();
			}
		}
		Map xmjl = OaWorkFlowUtil.listAdd("xmjl", vdef6);
		list.add(xmjl);

		String vdef10 = "0";
		if (null != hvo.getVdef10()) {
			String strWhere = "pk_defdoc = '" + hvo.getVdef10()
					+ "' and nvl(dr,0) = 0";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				vdef10 = defdoc[0].getName();
			}
		}
		Map sftsht = OaWorkFlowUtil.listAdd("sftsht", vdef10);
		list.add(sftsht);

		String vdef11 = "0";
		if (null != hvo.getVdef11()) {
			String strWhere = "pk_defdoc = '" + hvo.getVdef11()
					+ "' and nvl(dr,0) = 0 ";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				vdef11 = defdoc[0].getName();
			}
		}
		Map sfmbht = OaWorkFlowUtil.listAdd("sfmbht", vdef11);
		list.add(sfmbht);

		String vdef12 = "0";
		if (null != hvo.getVdef12()) {
			String strWhere = "pk_defdoc = '" + hvo.getVdef12()
					+ "' and nvl(dr,0) = 0 ";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				vdef12 = defdoc[0].getCode();
				Map htmbmc = OaWorkFlowUtil.listAdd("htmbmc",
						defdoc[0].getName());
				list.add(htmbmc);
			}
		}
		Map htmb = OaWorkFlowUtil.listAdd("htmb", vdef12);
		list.add(htmb);

		String vdef15 = "0";
		if (null != hvo.getVdef15()) {
			String strWhere = "pk_defdoc = '" + hvo.getVdef15()
					+ "' and nvl(dr,0) = 0 ";
			DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
					DefdocVO.class, strWhere);
			if (null != defdoc && defdoc.length == 1) {
				vdef15 = defdoc[0].getName();
			}
		}
		Map zbfs = OaWorkFlowUtil.listAdd("zbfs", vdef15);
		list.add(zbfs);

		Map yhtje = OaWorkFlowUtil.listAdd("yhtje", hvo.getVdef17());
		list.add(yhtje);

		Map ygzje = OaWorkFlowUtil.listAdd("ygzje", hvo.getVdef13());
		list.add(ygzje);

		Map htzkje = OaWorkFlowUtil.listAdd("htzkje", hvo.getVdef19());
		list.add(htzkje);

		// Map yyfkje =OaWorkFlowUtil.listAdd("yyfkje",hvo.getVdef18());
		// list.add(yyfkje);

		Map bz1 = OaWorkFlowUtil.listAdd("bz1", hvo.getVdef7());
		list.add(bz1);

		JSONArray arr = JSONArray.fromObject(list);
		return arr;
	}

	public AggCtPuVO[] terminate(AggCtPuVO[] vos, AggCtPuVO[] originBills)
			throws BusinessException {
		try {
			return new PurdailyTerminateAction().terminate(vos, originBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtPuVO[] unapprove(AggCtPuVO[] vos, AbstractCompiler2 script)
			throws BusinessException {
		try {
			return new PurdailyUnApproveAction().unApprove(vos, script);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtPuVO[] unfreeze(AggCtPuVO[] vos, AggCtPuVO[] originBills)
			throws BusinessException {
		try {
			return new PurdailyUnFreezeAction().unfreeze(vos, originBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtPuVO[] unsendapprove(AggCtPuVO[] vos, AbstractCompiler2 script)
			throws BusinessException {
		try {
			AggCtPuVO[] aggVO = new PurdailyUnsendApprove().unsendApprove(vos,
					script);
			unOaCommit(aggVO);
			return aggVO;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	private void unOaCommit(AggCtPuVO[] aggVO) throws BusinessException {
		for (AggCtPuVO temp : aggVO) {
			OaWorkFlowUtil.backOaWorkFlow(temp.getPrimaryKey());
			/*
			 * String strWhere = " nvl(dr,0) = 0  and pk_bill ='" +
			 * temp.getPrimaryKey() + "'"; WorkFlowBill[] workFlowBills =
			 * (WorkFlowBill[]) new HYPubBO()
			 * .queryByCondition(WorkFlowBill.class, strWhere); if (null !=
			 * workFlowBills && workFlowBills.length == 1) { throw new
			 * BusinessException("已提交OA系统单据不允许收回！"); }
			 */
		}
	}

	public AggCtPuVO[] unterminate(AggCtPuVO[] vos, AggCtPuVO[] originBills)
			throws BusinessException {
		try {
			return new PurdailyUnTerminateAction()
					.unterminate(vos, originBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtPuVO[] unvalidate(AggCtPuVO[] vos, AggCtPuVO[] originBills)
			throws BusinessException {
		try {
			for (AggCtPuVO aggvo : vos) {
				if (!UFDouble.ONE_DBL.equals(aggvo.getParentVO().getVersion())) {
					ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl
							.getNCLangRes().getStrByID("4020003_0",
									"04020003-0406"));
				}
			}

			return new PurdailyUnValidateAction().unvalidate(vos, originBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggCtPuVO[] validate(AggCtPuVO[] vos, AggCtPuVO[] originBills)
			throws BusinessException {
		try {
			List<AggCtPuVO> firstAndCancelVersion = new ArrayList();
			List<AggCtPuVO> firstAndCancelVersionOrig = new ArrayList();
			List<AggCtPuVO> otherVersion = new ArrayList();
			List<AggCtPuVO> otherVersionOrig = new ArrayList();
			AggCtPuVO[] firstAndCancelReturnVOs = null;
			AggCtPuVO[] otherReturnVOs = null;
			int cursor = 0;
			for (AggCtPuVO vo : vos) {
				CtPuExecVO[] execs = vo.getCtPuExecVO();
				if (!ArrayUtil.isEmpty(execs)) {
					CtPuExecVO newest = execs[0];
					for (int i = 1; i < execs.length; i++) {
						if ((execs[i].getStatus() != 2)
								&& (execs[i].getVexecdate().compareTo(
										newest.getVexecdate()) > 0)) {
							newest = execs[i];
						}
					}

					if ("取消生效".equals(newest.getVexecflow())) {
						firstAndCancelVersion.add(vo);
						firstAndCancelVersionOrig.add(originBills[cursor]);
						cursor++;
						continue;
					}
				}
				if (MathTool.equals(UFDouble.ONE_DBL, vo.getParentVO()
						.getVersion())) {
					firstAndCancelVersion.add(vo);
					firstAndCancelVersionOrig.add(originBills[cursor]);
				} else {
					otherVersion.add(vo);
					otherVersionOrig.add(originBills[cursor]);
				}
				cursor++;
			}
			if (firstAndCancelVersion.size() > 0) {
				firstAndCancelReturnVOs = new PurdailyValidateAction()
						.validate(
								(AggCtPuVO[]) firstAndCancelVersion
										.toArray(new AggCtPuVO[firstAndCancelVersion
												.size()]),
								(AggCtPuVO[]) firstAndCancelVersionOrig
										.toArray(new AggCtPuVO[firstAndCancelVersionOrig
												.size()]));
			}

			if (otherVersion.size() > 0) {
				otherReturnVOs = new PurdailyValidateSpAction()
						.validate((AggCtPuVO[]) otherVersion
								.toArray(new AggCtPuVO[otherVersion.size()]),
								(AggCtPuVO[]) otherVersionOrig
										.toArray(new AggCtPuVO[otherVersionOrig
												.size()]));
			}

			return (AggCtPuVO[]) ArrayUtil.combinArrays(new AggCtPuVO[][] {
					firstAndCancelReturnVOs, otherReturnVOs });
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	private JSONArray getNewMainMap(CtPuVO parentVO, double se, double wsje,
			String xmbm, String xmmc) throws BusinessException {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		List<Map<String, Object>> list = new ArrayList();
		// 构造数据
		if (!"1".equals(getDef2(parentVO.getPk_org()))) {
			list = OaWorkFlowUtil.transBean2Map(parentVO);
		}
		// ---------其他字段begin
		PurchaseOrgVO orgVO = (PurchaseOrgVO) getHyPubBO().queryByPrimaryKey(
				PurchaseOrgVO.class, parentVO.getPk_org());
		// 组织
		list.add(OaWorkFlowUtil.listAddObj("zzmc", orgVO.getName()));
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		// 合同类型名称
		String billTypeName = (String) getHyPubBO().findColValue(
				"bd_billtype",
				"billtypename",
				"nvl(dr,0) = 0 and pk_billtypeid = '"
						+ parentVO.getCtrantypeid() + "'");
		list.add(OaWorkFlowUtil.listAddObj("typename", billTypeName));
		// 人员
		String apppsnhName = (String) getHyPubBO().findColValue(
				"bd_psndoc",
				"name",
				"nvl(dr,0) = 0 and pk_psndoc  = '" + parentVO.getPersonnelid()
						+ "'");
		list.add(OaWorkFlowUtil.listAddObj("ry", apppsnhName));
		// 部门
		String sqbmName = (String) getHyPubBO().findColValue("org_dept_v",
				"name",
				"nvl(dr,0) = 0 and pk_vid = '" + parentVO.getDepid_v() + "'");
		list.add(OaWorkFlowUtil.listAddObj("bm", sqbmName));
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
		// 供应商
		if (null != parentVO.getCvendorid()) {
			CustSupplierVO custVO = (CustSupplierVO) getHyPubBO()
					.queryByPrimaryKey(CustSupplierVO.class,
							parentVO.getCvendorid());
			// 供应商编码
			list.add(OaWorkFlowUtil.listAddObj("pk_supplier_code",
					custVO.getCode()));
			// 供应商名称
			list.add(OaWorkFlowUtil.listAddObj("pk_supplier_name",
					custVO.getName()));
			// 供应商名称
			list.add(OaWorkFlowUtil.listAddObj("gys", custVO.getName()));
			// 乙方
			list.add(OaWorkFlowUtil.listAddObj("yf", custVO.getName()));
		}
		// 采购类型
		if (parentVO.getVdef9() != null) {
			String cglx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getVdef9()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("cglxbm", cglx));
			String cglxmc = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getVdef9()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("cglx", cglxmc));
		}
		// 项目经理
		if (parentVO.getVdef6() != null) {
			String xmlj = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getVdef6()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("xmjl", xmlj));
		}
		// 能投中台
		// 所属组织名称
		list.add(OaWorkFlowUtil.listAddObj("sszzmc", orgVO.getName()));
		list.add(OaWorkFlowUtil.listAddObj("szgs", orgVO.getName()));
		list.add(OaWorkFlowUtil.listAddObj("xmzz", orgVO.getName()));
		list.add(OaWorkFlowUtil.listAddObj("zdrzzmc", orgVO.getName()));
		// 税额合计
		list.add(OaWorkFlowUtil.listAddObj("se", se + ""));
		// 无税金额合计
		list.add(OaWorkFlowUtil.listAddObj("wsje", wsje + ""));
		// 合同编码
		list.add(OaWorkFlowUtil.listAddObj("htbh", parentVO.getVbillcode()));
		// 合同名称
		list.add(OaWorkFlowUtil.listAddObj("htmc", parentVO.getCtname()));
		// 合同类型
		list.add(OaWorkFlowUtil.listAddObj("helx", billTypeName));
		// 版本号
		list.add(OaWorkFlowUtil.listAddObj("bbh", parentVO.getVersion() + ""));
		// 合同签订日期
		if (parentVO.getSubscribedate() != null) {
			list.add(OaWorkFlowUtil.listAddObj("htqdrq", parentVO
					.getSubscribedate().getYear()
					+ "-"
					+ parentVO.getSubscribedate().getStrMonth()
					+ "-"
					+ parentVO.getSubscribedate().getStrDay()));
		}
		// 计划生效日期
		if (parentVO.getValdate() != null) {
			list.add(OaWorkFlowUtil.listAddObj("jhsxrq", parentVO.getValdate()
					.getYear()
					+ "-"
					+ parentVO.getValdate().getStrMonth()
					+ "-" + parentVO.getValdate().getStrDay()));
		}
		// 计划终止日期
		if (parentVO.getInvallidate() != null) {
			list.add(OaWorkFlowUtil.listAddObj("jhzzrq", parentVO
					.getInvallidate().getYear()
					+ "-"
					+ parentVO.getInvallidate().getStrMonth()
					+ "-"
					+ parentVO.getInvallidate().getStrDay()));
		}
		// 计划终止日期
		if (parentVO.getInvallidate() != null) {
			list.add(OaWorkFlowUtil.listAddObj("jhzzrq", parentVO
					.getInvallidate().getYear()
					+ "-"
					+ parentVO.getInvallidate().getStrMonth()
					+ "-"
					+ parentVO.getInvallidate().getStrDay()));
		}
		// 对方单位说明
		if (null != parentVO.getCustunit()) {
			list.add(OaWorkFlowUtil.listAddObj("dfdwsm", parentVO.getCustunit()
					+ ""));
		}
		// 交货地点
		if (null != parentVO.getDeliaddr()) {
			String jhdd = (String) getHyPubBO().findColValue(
					"bd_addressdoc",
					"name ",
					"nvl(dr,0) = 0 and pk_addressdoc   = '"
							+ parentVO.getDeliaddr() + "'");
			list.add(OaWorkFlowUtil.listAddObj("jhdd", jhdd));
		}
		// 币种
		if (null != parentVO.getCorigcurrencyid()) {
			String bz = (String) getHyPubBO().findColValue(
					"bd_currtype",
					"name",
					"nvl(dr,0) = 0 and pk_currtype = '"
							+ parentVO.getCorigcurrencyid() + "'");
			list.add(OaWorkFlowUtil.listAddObj("bz", bz));
		}
		// 折本汇率
		if (null != parentVO.getNexchangerate()) {
			list.add(OaWorkFlowUtil.listAddObj("zbhl",
					parentVO.getNexchangerate() + ""));
		}
		// 付款协议
		if (null != parentVO.getPk_payterm()) {
			String fkxy = (String) getHyPubBO().findColValue(
					"bd_payment",
					"name ",
					"nvl(dr,0) = 0 and pk_payment = '"
							+ parentVO.getPk_payterm() + "'");
			list.add(OaWorkFlowUtil.listAddObj("fkxy", fkxy));
		}
		// 预付款限额
		if (null != parentVO.getNoriprepaylimitmny()) {
			list.add(OaWorkFlowUtil.listAddObj("yfkxe",
					parentVO.getNoriprepaylimitmny() + ""));
		}
		// 预付款限额
		if (null != parentVO.getNoriprepaylimitmny()) {
			list.add(OaWorkFlowUtil.listAddObj("yfkxe",
					parentVO.getNoriprepaylimitmny() + ""));
		}
		// 委外
		if (null != parentVO.getBsc()) {
			list.add(OaWorkFlowUtil.listAddObj("ww", parentVO.getBsc()
					.booleanValue() ? "是" : "否"));
		}
		// 已生成订单量作为合同执行
		if (null != parentVO.getBordernumexec()) {
			list.add(OaWorkFlowUtil.listAddObj("yscddlzwhtzx", parentVO
					.getBordernumexec().booleanValue() ? "是" : "否"));
		}
		// 累计本币付款核销总额
		if (null != parentVO.getNtotalgpamount()) {
			list.add(OaWorkFlowUtil.listAddObj("ljfkze",
					parentVO.getNtotalgpamount() + ""));
		}
		// 总数量
		if (null != parentVO.getNtotalastnum()) {
			list.add(OaWorkFlowUtil.listAddObj("zsl",
					parentVO.getNtotalastnum() + ""));
		}
		// 价税合计
		if (null != parentVO.getNtotalorigmny()) {
			list.add(OaWorkFlowUtil.listAddObj("jshj",
					parentVO.getNtotalorigmny() + ""));
		}
		// 预付款限额
		if (null != parentVO.getNoriprepaylimitmny()) {
			list.add(OaWorkFlowUtil.listAddObj("yfkxe",
					parentVO.getNoriprepaylimitmny() + ""));
		}
		// 制单人用户编码
		list.add(OaWorkFlowUtil.listAddObj("zdrzj", userVO.getUser_code()));
		// 制单人名称
		list.add(OaWorkFlowUtil.listAddObj("zdrmc", userVO.getUser_name()));
		// 制单人身份证号
		list.add(OaWorkFlowUtil.listAddObj("sfzh", idCard));
		// 制单日期
		if (parentVO.getDmakedate() != null) {
			list.add(OaWorkFlowUtil.listAddObj("zdrq", parentVO.getDmakedate()
					.getYear()
					+ "-"
					+ parentVO.getDmakedate().getStrMonth()
					+ "-" + parentVO.getDmakedate().getStrDay()));
		}
		// 质保期（年）
		if (null != parentVO.getVdef1()) {
			list.add(OaWorkFlowUtil.listAddObj("zbqn", parentVO.getVdef1()));
		}
		// 是否特殊合同
		if (parentVO.getVdef10() != null) {
			String sftsht = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getVdef10()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sftsht", sftsht));
		}
		// 是否模板合同
		if (parentVO.getVdef11() != null) {
			String sfmbht = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getVdef11()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("sfmbht", sfmbht));
		}
		// 合同模板
		if (parentVO.getVdef12() != null) {
			String sfmbht = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getVdef12()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("htmb", sfmbht));
		}
		// 招标方式
		if (parentVO.getVdef15() != null) {
			String zbfs = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getVdef15()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("zbfs", zbfs));
		}
		// 原合同金额
		if (null != parentVO.getVdef17()) {
			list.add(OaWorkFlowUtil.listAddObj("yhtje", parentVO.getVdef17()));
		}
		// 原合同金额
		if (null != parentVO.getVdef17()) {
			list.add(OaWorkFlowUtil.listAddObj("yhtje", parentVO.getVdef17()));
		}
		// 原挂账金额
		if (null != parentVO.getVdef13()) {
			list.add(OaWorkFlowUtil.listAddObj("ygzje", parentVO.getVdef13()));
		}
		// 合同折扣金额
		if (null != parentVO.getVdef19()) {
			list.add(OaWorkFlowUtil.listAddObj("htzkje", parentVO.getVdef19()));
		}
		// 备注
		if (null != parentVO.getVdef7()) {
			list.add(OaWorkFlowUtil.listAddObj("bz1", parentVO.getVdef7()));
		}
		// 项目
		if (!"".equals(xmbm)) {
			list.add(OaWorkFlowUtil.listAddObj("xmbm", xmbm));
			list.add(OaWorkFlowUtil.listAddObj("xm", xmmc));
		}
		// 31 是否使用电子签章
		if (parentVO.getVdef31() != null) {
			String sfsydzqz = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getVdef31()
							+ "'");
			// 01 是，02 否
			if (sfsydzqz != null && "01".equals(sfsydzqz)) {
				sfsydzqz = "0";
			} else if (sfsydzqz != null && "02".equals(sfsydzqz)) {
				sfsydzqz = "1";
			}
			list.add(OaWorkFlowUtil.listAddObj("sfsydzqz", sfsydzqz));
		}
		// 32 税额
		list.add(OaWorkFlowUtil.listAddObj("se", se + ""));
		// 33 税前金额
		list.add(OaWorkFlowUtil.listAddObj("sqje", wsje + ""));
		// 34 用印类型
		if (parentVO.getVdef34() != null) {
			String yylx = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getVdef34()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("yylx", yylx));
			// 34 用印类型 简称
			String yylxbm = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"shortname",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getVdef34()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("yzlxbm", yylxbm));
		}
		// 甲方签收人
		if (null != parentVO.getVdef21()) {
			list.add(OaWorkFlowUtil.listAddObj("jfqsr", parentVO.getVdef21()));
		}
		// 甲方指定送达地址;
		if (null != parentVO.getVdef35()) {
			list.add(OaWorkFlowUtil.listAddObj("jfzdsddz", parentVO.getVdef35()));
		}
		// 甲方签收人联系电话
		if (null != parentVO.getVdef22()) {
			list.add(OaWorkFlowUtil.listAddObj("jflxdh", parentVO.getVdef22()));
		}
		// 乙方签收人
		if (null != parentVO.getVdef24()) {
			list.add(OaWorkFlowUtil.listAddObj("yfqsr", parentVO.getVdef24()));
		}
		// 乙方签收人联系电话
		if (null != parentVO.getVdef25()) {
			list.add(OaWorkFlowUtil.listAddObj("yflxdh", parentVO.getVdef25()));
		}
		// 乙方指定送达地点吗
		if (null != parentVO.getVdef23()) {
			list.add(OaWorkFlowUtil.listAddObj("yfzdsddz", parentVO.getVdef23()));
		}
		// 乙方电话（乙方签收人联系电话）
		if (null != parentVO.getVdef25()) {
			list.add(OaWorkFlowUtil.listAddObj("yflxdh", parentVO.getVdef25()));
		}
		// 甲方授权人
		if (parentVO.getVdef37() != null) {
			String jfsrqstr = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"name",
					"nvl(dr,0) = 0 and pk_psndoc = '" + parentVO.getVdef37()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("jfsqr", jfsrqstr));
			String jfsrqid = (String) getHyPubBO().findColValue(
					"bd_psndoc",
					"id",
					"nvl(dr,0) = 0 and pk_psndoc = '" + parentVO.getVdef37()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("jfsqrsfzh", jfsrqid));
		}
		// 电子合同模板
		if (parentVO.getVdef38() != null) {
			String htmb = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"code",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getVdef38()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("htmb", htmb));
			String htmbmc = (String) getHyPubBO().findColValue(
					"bd_defdoc",
					"name",
					"nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getVdef38()
							+ "'");
			list.add(OaWorkFlowUtil.listAddObj("htmbmc", htmbmc));
		}
		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	private JSONArray getNewDtaileDataMap(AggCtPuVO temp)
			throws BusinessException {
		// 获取详细信息
		List dtlist = new ArrayList();
		CtPuBVO[] bvos = (CtPuBVO[]) getHyPubBO().queryByCondition(
				CtPuBVO.class,
				"nvl(dr,0) = 0 and pk_ct_pu = '" + temp.getPrimaryKey() + "'");
		Map bodyMap = getBody(bvos, getDef2(temp.getParentVO().getPk_org()));
		dtlist.add(bodyMap);

		CtPuTermVO[] bvos2 = (CtPuTermVO[]) getHyPubBO().queryByCondition(
				CtPuTermVO.class,
				"nvl(dr,0) = 0 and pk_ct_pu = '" + temp.getPrimaryKey() + "'");
		if (bvos2 != null && bvos2.length > 0) {
			Map bodyMap2 = getBody2(bvos2, getDef2(temp.getParentVO()
					.getPk_org()));
			dtlist.add(bodyMap2);
		}

		PayPlanVO[] bvos3 = (PayPlanVO[]) getHyPubBO().queryByCondition(
				PayPlanVO.class,
				"nvl(dr,0) = 0 and pk_ct_pu = '" + temp.getPrimaryKey() + "'");
		if (bvos3 != null && bvos3.length > 0) {
			Map bodyMap3 = getBody3(bvos3, getDef2(temp.getParentVO()
					.getPk_org()));
			dtlist.add(bodyMap3);
		}

		CtPuExpVO[] bvos4 = (CtPuExpVO[]) getHyPubBO().queryByCondition(
				CtPuExpVO.class,
				"nvl(dr,0) = 0 and pk_ct_pu = '" + temp.getPrimaryKey() + "'");
		if (bvos4 != null && bvos4.length > 0) {
			Map bodyMap4 = getBody4(bvos4, getDef2(temp.getParentVO()
					.getPk_org()));
			dtlist.add(bodyMap4);
		}

		CtPaymentVO[] bvos5 = (CtPaymentVO[]) getHyPubBO().queryByCondition(
				CtPaymentVO.class,
				"nvl(dr,0) = 0 and pk_ct_pu = '" + temp.getPrimaryKey() + "'");
		if (bvos5 != null && bvos5.length > 0) {
			Map bodyMap5 = getBody5(bvos5, getDef2(temp.getParentVO()
					.getPk_org()));
			dtlist.add(bodyMap5);
		}
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray dtlistString = JSONArray.fromObject(dtlist, jsonConfig);
		return dtlistString;
	}

	private Map getBody5(CtPaymentVO[] bvos5, String def2)
			throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt5");
		List workflowRequestTableRecords = new ArrayList();
		for (CtPaymentVO temp : bvos5) {
			List<Map<String, Object>> workflowRequestTableFields = new ArrayList();
			if (!"1".equals(def2)) {
				workflowRequestTableFields = OaWorkFlowUtil.transBean2Map(temp);
			}
			Map workflowRequestTableFieldsMap = new HashMap();
			if (null != temp.getShoworder()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("fkq",
						temp.getShoworder().toString()));
			}
			if (null != temp.getAccrate()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fkbl", temp.getAccrate().toString()));
			}
			if (null != temp.getPrepayment()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("yfk",
						temp.getPrepayment().booleanValue() ? "是" : "否"));
			}
			if (null != temp.getPk_payperiod()) {
				String name = (String) getHyPubBO().findColValue(
						"bd_payperiod",
						"name",
						"nvl(dr,0) = 0 and pk_payperiod ='"
								+ temp.getPk_payperiod() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"qxrq", name));
			}

			Map qxrqyqts = new HashMap();
			qxrqyqts.put("fieldName", "qxrqyqts");
			qxrqyqts.put("fieldValue", temp.getEffectdateadddate());
			workflowRequestTableFields.add(qxrqyqts);

			Map czr = new HashMap();
			czr.put("fieldName", "czr");
			czr.put("fieldValue", temp.getOutaccountdate());
			workflowRequestTableFields.add(czr);

			Map zqts = new HashMap();
			zqts.put("fieldName", "zqts");
			zqts.put("fieldValue", temp.getPaymentday() + "");
			workflowRequestTableFields.add(zqts);

			Map gdjzr = new HashMap();
			gdjzr.put("fieldName", "gdjzr");
			gdjzr.put("fieldValue", temp.getCheckdata());
			workflowRequestTableFields.add(gdjzr);

			Map sxy = new HashMap();
			sxy.put("fieldName", "sxy");
			sxy.put("fieldValue", temp.getEffectmonth());
			workflowRequestTableFields.add(sxy);

			Map fjy = new HashMap();
			fjy.put("fieldName", "fjy");
			fjy.put("fieldValue", temp.getEffectaddmonth());
			workflowRequestTableFields.add(fjy);

			String jname = (String) getHyPubBO().findColValue(
					"bd_balatype",
					"name",
					"nvl(dr,0) = 0 and pk_balatype ='" + temp.getPk_balatype()
							+ "'");
			Map jsfs = new HashMap();
			jsfs.put("fieldName", "jsfs");
			jsfs.put("fieldValue", jname);
			workflowRequestTableFields.add(jsfs);

			Map bzj = new HashMap();
			bzj.put("fieldName", "bzj");
			bzj.put("fieldValue", temp.getIsdeposit().booleanValue() ? "是"
					: "否");
			workflowRequestTableFields.add(bzj);

			String xname = (String) getHyPubBO().findColValue(
					"bd_rateschema",
					"name",
					"nvl(dr,0) = 0 and  pk_rateschema  ='" + temp.getPk_rate()
							+ "'");
			Map xjzk = new HashMap();
			xjzk.put("fieldName", "xjzk");
			xjzk.put("fieldValue", xname);
			workflowRequestTableFields.add(xjzk);

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	private Map getBody4(CtPuExpVO[] bvos4, String def2)
			throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt4");
		List workflowRequestTableRecords = new ArrayList();
		for (CtPuExpVO temp : bvos4) {
			List<Map<String, Object>> workflowRequestTableFields = new ArrayList();
			if (!"1".equals(def2)) {
				workflowRequestTableFields = OaWorkFlowUtil.transBean2Map(temp);
			}
			Map workflowRequestTableFieldsMap = new HashMap();
			if (null != temp.getVexpcode()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fybm", temp.getVexpcode()));
			}
			if (null != temp.getVexpname()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fymc", temp.getVexpname()));
			}
			if (null != temp.getVexpsum()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"fyje", temp.getVexpsum().toString()));
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bz",
					temp.getVmemo()));
			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}
		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	private Map getBody3(PayPlanVO[] bvos3, String def2)
			throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt3");
		List workflowRequestTableRecords = new ArrayList();
		for (PayPlanVO temp : bvos3) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = new ArrayList();
			if (!"1".equals(def2)) {
				workflowRequestTableFields = OaWorkFlowUtil.transBean2Map(temp);
			}
			Map workflowRequestTableFieldsMap = new HashMap();
			if (null != temp.getPk_financeorg()) {
				String shortname = (String) getHyPubBO().findColValue(
						"org_financeorg",
						"shortname",
						"nvl(dr,0) = 0 and pk_taxcode ='"
								+ temp.getPk_financeorg() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"yfcwzz", shortname));
			}
			if (null != temp.getDbegindate()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"qsrq", temp.getDbegindate().getYear() + "-"
								+ temp.getDbegindate().getStrMonth() + "-"
								+ temp.getDbegindate().getStrDay()));
			}
			if (null != temp.getDenddate()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"zqdqr", temp.getDenddate().getYear() + "-"
								+ temp.getDenddate().getStrMonth() + "-"
								+ temp.getDenddate().getStrDay()));
			}
			if (null != temp.getIaccounttermno()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("zqh",
						temp.getIaccounttermno().toString()));
			}
			if (null != temp.getIitermdays()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"zqts", temp.getIitermdays().toString()));
			}
			if (null != temp.getNorigmny()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("je",
						temp.getNorigmny().toString()));
			}
			if (null != temp.getNrate()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"nrate", temp.getNrate().toString()));
			}
			if (null != temp.getNaccumpayorgmny()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"ljfkje", temp.getNaccumpayorgmny().toString()));
			}

			workflowRequestTableFieldsMap.put("recordOrder", "0");
			workflowRequestTableFieldsMap.put("workflowRequestTableFields",
					workflowRequestTableFields);
			workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
		}

		dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
		return dtMap;
	}

	private Map getBody2(CtPuTermVO[] bvos2, String def2)
			throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt2");
		List workflowRequestTableRecords = new ArrayList();
		for (CtPuTermVO temp : bvos2) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = new ArrayList();
			if (!"1".equals(def2)) {
				workflowRequestTableFields = OaWorkFlowUtil.transBean2Map(temp);
			}
			// 其他字段------begin
			// Map tkbm = new HashMap();
			if (null != temp.getVtermcode()) {
				String tkbmstr = (String) getHyPubBO().findColValue(
						"ct_termset",
						"vtermcode",
						"nvl(dr,0) = 0 and pk_ct_termset ='"
								+ temp.getVtermcode() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"tkbm", tkbmstr));
			}
			if (null != temp.getVtermname()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"tkmc", temp.getVtermname()));
			}
			if (null != temp.getVtermtypename()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"tklx", temp.getVtermtypename()));
			}
			if (null != temp.getVtermcontent()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"tknr", temp.getVtermcontent()));
			}
			if (null != temp.getVotherinfo()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"qtxx", temp.getVotherinfo()));
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bz",
					temp.getVmemo()));

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

	private Map getBody(CtPuBVO[] bvo, String def2) throws BusinessException {
		Map dtMap = new HashMap();
		// 测试临时写死，后期改为接口获取
		dtMap.put("tableDBName", TableName + "_dt1");
		List workflowRequestTableRecords = new ArrayList();
		for (CtPuBVO temp : bvo) {
			// 构造数据
			List<Map<String, Object>> workflowRequestTableFields = new ArrayList();
			if (!"1".equals(def2)) {
				workflowRequestTableFields = OaWorkFlowUtil.transBean2Map(temp);
			}
			// 其他字段------begin
			if (null != temp.getCbprojectid()) {
				// 项目
				ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
						.queryByPrimaryKey(ProjectHeadVO.class,
								temp.getCbprojectid());
				// 项目编码
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bproject_code", projectVO.getProject_code()));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"xmbm", projectVO.getProject_code()));
				// 项目名称
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bproject_name", projectVO.getProject_name()));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("xm",
						projectVO.getProject_name()));
				// EPS名称
				if (projectVO.getPk_eps() != null) {
					String eps_name = (String) getHyPubBO().findColValue(
							"pm_eps",
							"eps_name",
							"nvl(dr,0) = 0 and pk_eps = '"
									+ projectVO.getPk_eps() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"eps_name", eps_name));
					String eps_code = (String) getHyPubBO().findColValue(
							"pm_eps",
							"eps_code",
							"nvl(dr,0) = 0 and pk_eps = '"
									+ projectVO.getPk_eps() + "'");
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"eps_code", eps_code));
				}

				// 工程详细位置
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"gcxxwz", projectVO.getHdef53()));
			}
			// 采购类型
			if (temp.getVbdef9() != null) {
				String cglx = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"code",
						"nvl(dr,0) = 0 and pk_defdoc = '" + temp.getVbdef9()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"cglxbm", cglx));
				String cglxmc = (String) getHyPubBO().findColValue(
						"bd_defdoc",
						"name",
						"nvl(dr,0) = 0 and pk_defdoc = '" + temp.getVbdef9()
								+ "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"cglx", cglxmc));
			}
			if (null != temp.getPk_material()) {
				MaterialVO materialVO = (MaterialVO) getHyPubBO()
						.queryByPrimaryKey(MaterialVO.class,
								temp.getPk_material());
				// 物料编码
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"material_code", materialVO.getCode()));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"wlbm", materialVO.getCode()));
				// 物料名称
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"material_name", materialVO.getName()));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"wlmc", materialVO.getName()));
				// 规格
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"materialspec", materialVO.getMaterialspec()));
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("gg",
						materialVO.getMaterialspec()));
				// 型号
				if (null != materialVO.getMaterialtype()) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"materialtype", materialVO.getMaterialtype()));
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"xh", materialVO.getMaterialtype()));
				}

			}
			// 主单位
			String zdwstr = "0";
			if (null != temp.getCunitid()) {
				zdwstr = (String) getHyPubBO().findColValue(
						"bd_measdoc",
						"name",
						"nvl(dr,0) = 0 and pk_measdoc  = '" + temp.getCunitid()
								+ "'");
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("zdw",
					zdwstr));
			// 单位
			String dwstr = "";
			if (null != temp.getCunitid()) {
				dwstr = (String) getHyPubBO().findColValue(
						"bd_measdoc",
						"name",
						"nvl(dr,0) = 0 and pk_measdoc  = '"
								+ temp.getCastunitid() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("dw",
						dwstr));
			}
			// 收货库存组织
			String shkczz = "0";
			if (temp.getPk_arrvstock_v() != null) {
				shkczz = (String) getHyPubBO().findColValue(
						"org_stockorg_v",
						"name",
						"nvl(dr,0) = 0 and pk_vid = '"
								+ temp.getPk_arrvstock_v() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"shkczz", shkczz));
			}
			// 财务组织
			String cwzz = "0";
			if (temp.getPk_financeorg_v() != null) {
				cwzz = (String) getHyPubBO().findColValue(
						"org_financeorg_v",
						"name",
						"nvl(dr,0) = 0 and pk_vid = '"
								+ temp.getPk_financeorg_v() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"cwzz", cwzz));
			}
			// 其他字段------end

			// 能投
			// 折扣类别
			String zklbstr = temp.getFtaxtypeflag() + "";
			int zklbint = Integer.parseInt(zklbstr);
			String zklb = "";
			if (zklbint == 0) {
				zklb = "应税内含";
			} else if (zklbint == 1) {
				zklb = "应税外加";
			}
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("zklb",
					zklb));
			// 税码
			String sm = "0";
			if (temp.getCtaxcodeid() != null) {
				sm = (String) getHyPubBO().findColValue(
						"bd_taxcode",
						"code",
						"nvl(dr,0) = 0 and   pk_taxcode  = '"
								+ temp.getCtaxcodeid() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sm",
						sm));
			}
			// 数量
			if (null != temp.getNastnum()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sl1",
						temp.getNastnum() + ""));
				// 主数量
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"zslsl", temp.getNastnum() + ""));
			}
			// 含税率
			if (null != temp.getVchangerate()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("hsl",
						temp.getVchangerate() + ""));
			}
			// 无税单价
			if (null != temp.getNqtorigprice()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"wsdj", temp.getNqtorigprice() + ""));
			}
			// 含税单价
			if (null != temp.getNqtorigtaxprice()) {
				workflowRequestTableFields
						.add(OaWorkFlowUtil.listAddObj(
								"hsdj",
								((UFDouble) (temp.getNqtorigtaxprice() == null ? new UFDouble(
										"0.00") : temp.getNqtorigtaxprice()))
										.setScale(2, UFDouble.ROUND_HALF_UP)
										.toString()));
			}
			// 本币无税单价
			if (null != temp.getNgprice()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"zbbwsdj", temp.getNgprice() + ""));
			}
			// 本币含税单价
			if (null != temp.getNgtaxprice()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"zbbhsdj", temp.getNgtaxprice() + ""));
			}
			// 无税金额
			if (null != temp.getNorigmny()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"wsje",
						((UFDouble) (temp.getNorigmny() == null ? new UFDouble(
								0.00) : temp.getNorigmny())).setScale(2,
								UFDouble.ROUND_HALF_UP).toString()));
			}
			// 税率
			if (null != temp.getNtaxrate()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sl",
						temp.getNtaxrate() + ""));
			}
			//
			if (null != temp.getFtaxtypeflag()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"kslb", temp.getFtaxtypeflag() + ""));
			}
			// 优质优价方案
			if (null != temp.getCqpbaseschemeid()) {
				String yzyjfa = (String) getHyPubBO().findColValue(
						"purp_qpscheme",
						"vschemename",
						"nvl(dr,0) = 0 and pk_qpscheme='"
								+ temp.getCqpbaseschemeid() + "'");
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"yzyjfa", yzyjfa));
			}
			// 价税合计
			if (null != temp.getNorigtaxmny()) {
				workflowRequestTableFields
						.add(OaWorkFlowUtil.listAddObj(
								"jshj",
								((UFDouble) (temp.getNorigtaxmny() == null ? new UFDouble(
										"0.00") : temp.getNorigtaxmny()))
										.setScale(2, UFDouble.ROUND_HALF_UP)
										.toString()));
			}
			// 本币无税金额
			if (null != temp.getNmny()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bbwsje", temp.getNmny() + ""));
			}
			//
			if (null != temp.getNnosubtaxrate()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bkdssl", temp.getNnosubtaxrate() + ""));
			}
			//
			if (null != temp.getNnosubtax()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"bkdsje", temp.getNnosubtax() + ""));
			}
			//
			if (null != temp.getNcalcostmny()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"jcbje", temp.getNcalcostmny() + ""));
			}
			//
			if (null != temp.getNordnum()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"ljddzsl", temp.getNordnum() + ""));
			}
			//
			if (null != temp.getNordsum()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"ljddjshj", temp.getNordsum() + ""));
			}
			//
			if (null != temp.getPk_ct_price()) {
				CtPriceHeaderVO ctPriceHeaderVO = (CtPriceHeaderVO) getHyPubBO()
						.queryByPrimaryKey(CtPriceHeaderVO.class,
								temp.getPk_ct_price());
				if (null != ctPriceHeaderVO) {
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"ljddzsl", ctPriceHeaderVO.getPk_ct_price() + ""));
					workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
							"htjgxxmc", ctPriceHeaderVO.getVname() + ""));
				}
			}
			//
			if (null != temp.getNschedulernum()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"ljpcsl", temp.getNschedulernum() + ""));
			}
			// 请购单号
			if (null != temp.getVpraybillcode()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
						"qgdh", temp.getVpraybillcode() + ""));
			}
			// 税额
			if (null != temp.getNtax()) {
				workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("se",
						temp.getNtax() + ""));
			}
			// 备注
			workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bz",
					temp.getVmemo() + ""));

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
					// ExceptionUtils.wrappBusinessException("项目[" + pk_project+
					// "]未找到对应任务！");
					wbsName = "";
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
