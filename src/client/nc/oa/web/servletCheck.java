package nc.oa.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.adaptor.IHttpServletAdaptor;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.comn.NetStreamContext;
import nc.bs.framework.server.ISecurityTokenCallback;
import nc.bs.logging.Logger;
import nc.cmp.utils.BillcodeGenerater;
import nc.impl.pubapp.pattern.data.bill.BillQuery;
import nc.itf.pbm.materialadd.pvt.IMaterialPlanAdd;
import nc.itf.pbm.materialplan.pvt.IMaterialPlan;
import nc.itf.pcm.contract.pub.IContractService;
import nc.itf.ppm.schedulefill.IShceduleFillInterface;
import nc.itf.pu.m21.IOrderQuery;
import nc.itf.pubapp.pub.smart.IBillQueryService;
import nc.itf.uap.pf.IPFBusiAction;
import nc.itf.uap.pf.IplatFormEntry;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.message.util.MessageCenter;
import nc.message.vo.MessageVO;
import nc.message.vo.NCMessage;
import nc.ui.pcm.feebalance.action.TaskProgressDetailVO;
import nc.ui.pcm.feebalance.action.TaskProgressVO;
import nc.ui.pcm.utils.GetDao;
import nc.ui.pub.pf.PfUtilClient;
import nc.vo.ct.purdaily.entity.AggCtPuVO;
import nc.vo.ct.purdaily.entity.CtPuBVO;
import nc.vo.ct.purdaily.entity.CtPuVO;
import nc.vo.fct.ap.entity.AggCtApVO;
import nc.vo.fct.ap.entity.CtApVO;
import nc.vo.fct.ar.entity.AggCtArVO;
import nc.vo.fct.ar.entity.CtArVO;
import nc.vo.pbm.budgetadd.BudgetAddBillVO;
import nc.vo.pbm.budgetadd.BudgetAddHeadVO;
import nc.vo.pbm.materialplan.MaterialPlanBillVO;
import nc.vo.pbm.materialplanadd.MaterialPlanAddBillVO;
import nc.vo.pcm.contract.ContrHeadVO;
import nc.vo.pcm.contract.ContrWorksVO;
import nc.vo.pcm.contract.ContractBillVO;
import nc.vo.pcm.contractalter.ContrAlterBillVO;
import nc.vo.pcm.contractalter.ContrAlterHeadVO;
import nc.vo.pcm.contractbalance.ContractBalanceBillVO;
import nc.vo.pcm.contractbalance.ContractBalanceHeadVO;
import nc.vo.pcm.feebalance.FeeBalanceBillVO;
import nc.vo.pcm.feebalance.FeeBalanceBodyVO;
import nc.vo.pcm.feebalance.FeeBalanceHeadVO;
import nc.vo.phm.projectproposal.ProjectProposalBillVO;
import nc.vo.phm.projectproposal.ProjectProposalHeadVO;
import nc.vo.phm.projectregister.ProjectRegisterBillVO;
import nc.vo.phm.projectregister.ProjectRegisterHeadVO;
import nc.vo.ppm.projectcheck.ProjectCheckBillVO;
import nc.vo.ppm.projectcheck.ProjectCheckHeadVO;
import nc.vo.ppm.schedulefill.TaskScheduleBillVO;
import nc.vo.ppm.schedulefill.TaskScheduleBodyVO;
import nc.vo.ppm.schedulefill.TaskScheduleHeadVO;
import nc.vo.pu.m21.entity.OrderVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.workflownote.WorkflownoteVO;

//import nc.ui.pub.pf.PfUtilClient;

@SuppressWarnings({ "restriction", "unused" })
public class servletCheck implements IHttpServletAdaptor {
	private BaseDAO dao;

	// GenPushJDKUtil utilAct = new GenPushJDKUtil();
	@SuppressWarnings({ "deprecation", "unchecked", "rawtypes" })
	public void doAction(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// initSystemInfo();//初始化NC
		String ds = "RLJT";
		InvocationInfoProxy.getInstance().setUserDataSource(ds);
		InvocationInfoProxy.getInstance().setGroupId("0001A1100000000001QS");// 设置默认的集团主键
		InvocationInfoProxy.getInstance().setUserCode("liuli");// 设置接口默认操作员编码
		InvocationInfoProxy.getInstance().setUserId("1001A110000000000HV8");// 设置接口默认操作员主键
																			// 1001A110000000000HV8
		ISecurityTokenCallback tc = NCLocator.getInstance().lookup(
				ISecurityTokenCallback.class);
		byte[] token = tc.token("NCSystem".getBytes(), "pfxx".getBytes());

		NetStreamContext.setToken(token);

		// 输出到前台
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter printWriter = resp.getWriter();
		String msg = "";
		String htmsg = "";
		String billID = req.getParameter("BillID");// 单据主键
		String billType = req.getParameter("BillType"); // 单据类型
		String resultFromOA = req.getParameter("ResultFromOA"); // 审批结果
		String checType = req.getParameter("checkType"); // 审批类型 1 正常审批 2 取消审批
		// String billCode = req.getParameter("billCode"); // OA单据号
		Logger.error("单据主键：" + billID + "=单据类型：" + billType + "=审批结果："
				+ resultFromOA);
		if (billID == null) {
			Logger.error("单据主键：" + billID + "=单据类型：" + billType + "=审批结果："
					+ resultFromOA);
			msg = "单据主键：" + billID + "=单据类型：" + billType + "=审批结果："
					+ resultFromOA;
			return;
		}

		if ("1".equals(billType)) {

			IContractService cs = (IContractService) NCLocator.getInstance()
					.lookup(IContractService.class);// 查询单据VO
			IplatFormEntry ipf = (IplatFormEntry) NCLocator.getInstance()
					.lookup(IplatFormEntry.class);// 动作执行类
			ContractBillVO[] wq = cs.queryBillVOByPk(new String[] { billID });// 查询单据信息
			if (wq.length > 0) {
				String status = wq[0].getParentVO().getBill_status() + "";// 合同状态
				String contractname = wq[0].getParentVO().getBill_name();// 合同名称
				String billMakerID = wq[0].getParentVO().getBillmaker();

				String htstatus = wq[0].getParentVO().getBill_status() + "";
				if ("-1".equals(status)) {
					htstatus = "自由态";
				} else if ("1".equals(status)) {
					htstatus = "审批通过";
				} else if ("2".equals(status)) {
					htstatus = "审批中";
				} else if ("9".equals(status)) {
					htstatus = "生效";
				} else if ("10".equals(status)) {
					htstatus = "已冻结";
				} else if ("11".equals(status)) {
					htstatus = "终止";
				}
				if ("2".equals(checType)) { // 如果是取消审批
					try {
						if ("0".equals(resultFromOA)) {
							// 取消审批
							ipf.processAction("UNAPPROVE", "4D42", null, wq[0],
									null, null);
							msg = "[清单发包合同]名称为" + contractname + "的单据取消审批成功！";
							// 取消审批
							// InsertAndUptTaskBIll insertUptTaskBill =
							// NCLocator.getInstance().lookup(InsertAndUptTaskBIll.class);
							// deleteTaskBillVO(billID);
							// XBX20191119调用合同台账接口
							String result = BillCancle.Cancle(billID, "0");
							if ("0".equals(result)) {
								htmsg = "[清单发包合同]名称为" + contractname
										+ "的单据取消审批调用合同台账成功！";
							} else {
								htmsg = "[清单发包合同]名称为" + contractname
										+ "的单据取消审批调用合同台账失败！";
							}
						} else {
							msg = "[清单发包合同]名称为" + contractname + "的单据取消审批被驳回！";
						}
					} catch (BusinessException e) {
						// TODO 自动生成的 catch 块
						msg = "清单发包合同取消审批错误" + e.toString();
						e.printStackTrace();
					}

				} else { // 正常审批
					if (!"3".equals(status)) {
						Logger.error("清单发包名称为" + contractname + "的状态是："
								+ htstatus + ",不能审批！");
						msg = "清单发包名称为" + contractname + "的状态是：" + htstatus
								+ ",不能审批！";
					} else {
						try {
							if ("0".equals(resultFromOA)) {

								/*ipf.processAction("APPROVE", "4D42", null,
										wq[0], null, null);*/
								getIplatFormEntry().processAction("APPROVE", "4D42", null,
										wq[0], null, getEparam());
								msg = "[清单发包合同]名称为" + contractname + "的单据审批成功！";
								// XBX1106新增回填电子合同状态
								HTFileUtils fileUtils = new HTFileUtils();
								String rtfu = fileUtils.afterApprove(billID,
										"1");
								if ("Y".equals(rtfu)) {
									Logger.error("审批成功后传送电子合同系统成功！");
								} else {
									Logger.error("审批成功后传送电子合同系统成功！");
								}
								// suchb20200427 添加，根据清单发包合同推送“进度填报单”
								BillQuery<ContractBillVO> bq = new BillQuery<>(
										ContractBillVO.class);
								String[] pkArr = new String[] { billID };

								ContractBillVO[] contractBillVOArr = bq
										.query(pkArr);
								// pushTaskProcessByContract(contractBillVOArr[0]);

								// XBX20191119调用合同台账接口
								RLContractTZService service = new RLContractTZService();
								String result = OAGetQDFBJson.getVO(billID,
										service);
								if ("0".equals(result)) {
									htmsg = "[清单发包合同]名称为" + contractname
											+ "的单据审批调用合同台账成功！";
								} else {
									htmsg = "[清单发包合同]名称为" + contractname
											+ "的单据审批调用合同台账失败！";
								}
								// 添加清单发包合同档案，自定义档案
								/*
								 * try {
								 * AddDefdoc.addDefdoc(wq[0].getParentVO()); }
								 * catch (BusinessException e1) { // TODO 自动生成的
								 * catch 块 msg = "清单发包审批错误" + e1.toString();
								 * e1.printStackTrace(); }
								 */
							} else if ("1".equals(resultFromOA)) {
								UpdStatus("PM_CONTR", "BILL_STATUS", "0",
										"PK_CONTR", billID);
								// XBX1106新增回填电子合同状态
								HTFileUtils fileUtils = new HTFileUtils();
								String rtfu = fileUtils.afterApprove(billID,
										"0");
								if ("Y".equals(rtfu)) {
									Logger.error("审批成功后传送电子合同系统成功！");
								} else {
									Logger.error("审批成功后传送电子合同系统成功！");
								}
								msg = "[清单发包合同]名称为" + contractname
										+ "的单据审批未通过！";
							}
							Logger.error(msg);
							String users[] = getRoleUsers("1001A21000000008AOHK");// 测试服务
							sendMsgToUser(users, msg);
							// 制单人发送消息
							sendMsgToBillMaker(billMakerID, msg);
							Logger.error("消息发送成功！");
						} catch (BusinessException e) {
							// TODO 自动生成的 catch 块
							msg = "清单发包审批错误" + e.toString();
							e.printStackTrace();
						} catch (Exception e) {
							// TODO 自动生成的 catch 块
							msg = "清单发包审批错误" + e.toString();
							e.printStackTrace();
						}
					}
				}

			} else {
				Logger.error("ID:" + billID + "的单据，查询失败");
				msg = "ID:" + billID + "的单据，查询失败";
			}
		} else if ("2".equals(billType)) {// 采购合同
			try {
				/*
				 * IPurdailyMaintain purdailyQus =
				 * NCLocator.getInstance().lookup( IPurdailyMaintain.class);
				 * AggCtPuVO[] ctPuVO = purdailyQus .queryCtPuVoByIds(new
				 * String[] { billID });
				 */
				IBillQueryService billquery = NCLocator.getInstance().lookup(
						IBillQueryService.class);
				/*IplatFormEntry ipf = (IplatFormEntry) NCLocator.getInstance()
						.lookup(IplatFormEntry.class);// 动作执行类*/
				IPFBusiAction ipf = (IPFBusiAction) NCLocator.getInstance()
						.lookup(IPFBusiAction.class);//
				AggCtPuVO aggCtPuVO = billquery.querySingleBillByPk(
						AggCtPuVO.class, billID + "");
				if (aggCtPuVO != null) {
					String status = aggCtPuVO.getParentVO().getFstatusflag()
							+ "";// 采购合同状态
					String contractname = aggCtPuVO.getParentVO().getCtname();// 合同名称
					String billMakerID = aggCtPuVO.getParentVO().getBillmaker(); // 制单人ID
					String htstatus = aggCtPuVO.getParentVO().getFstatusflag()
							+ "";
					if ("0".equals(status)) {
						htstatus = "自由态";
					} else if ("1".equals(status)) {
						htstatus = "生效";
					} else if ("2".equals(status)) {
						htstatus = "审批中";
					} else if ("4".equals(status)) {
						htstatus = "审批未通过";
					} else if ("5".equals(status)) {
						htstatus = "已冻结";
					} else if ("6".equals(status)) {
						htstatus = "终止";
					}
					if ("2".equals(checType)) { // 如果是取消审批
						if ("0".equals(resultFromOA)) {
							String sql = "UPDATE ct_pu SET fstatusflag = 0,APPROVER = '~',taudittime = NULL WHERE pk_ct_pu = '"
									+ billID + "'";
							Logger.error(sql);
							getDao();
							dao.executeUpdate(sql);
							// ipf.processAction("UNAPPROVE", "Z2",
							// null,ctPuVO[0], null, null);
							msg = "[采购合同]名称为 " + contractname + " 的单据取消审批成功！";
							// suchb2020-04-27 删除进度填报单
							// InsertAndUptTaskBIll insertUptTaskBill =
							// NCLocator.getInstance().lookup(InsertAndUptTaskBIll.class);
							// deleteTaskBillVO(billID);

							// XBX20191119调用合同台账接口
							String result = BillCancle.Cancle(billID, "0");
							if ("0".equals(result)) {
								htmsg = "[采购合同]名称为 " + contractname
										+ " 的单据取消审批调用合同台账成功！";
							} else {
								htmsg = "[采购合同]名称为 " + contractname
										+ " 的单据取消审批调用合同台账失败！";
							}
							HTFileUtils fileUtils = new HTFileUtils();
								String rtfu = fileUtils.afterApprove(billID,
										"0");
						} else {
							msg = "[采购合同]名称为 " + contractname + "的单据取消审批被驳回！";
						}
					} else { // 正常审批
						if (!"2".equals(status)) {
							Logger.error("合同名称为" + contractname + "的状态是："
									+ htstatus + ",不能审批！");
							msg = "合同名称为" + contractname + "的状态是：" + htstatus
									+ ",不能审批！";
						} else {

							if ("0".equals(resultFromOA)) {
								/*ipf.processAction("APPROVE", "Z2", null,
										aggCtPuVO, null, null);*/
								String wfsql = "SELECT PK_CHECKFLOW FROM PUB_WORKFLOWNOTE WHERE BILLID = '"
										+ aggCtPuVO.getParentVO()
												.getPrimaryKey()
										+ "' AND ISCHECK = 'N'";
								List<Object[]> wfls = getDao.query(wfsql);
								// Logger.error("输出："+wfls.get(0)[0]);
								if (wfls == null || wfls.size() == 0) {
									return;
								} else {
									HashMap map = new HashMap();
									BaseDAO daoe = new BaseDAO();
									WorkflownoteVO worknoteVO = (WorkflownoteVO) daoe
											.retrieveByPK(WorkflownoteVO.class,
													wfls.get(0)[0] + "");
									// Logger.error(worknoteVO);
									// System.out.println(worknoteVO);
									// Logger.error("输出Work："+worknoteVO);
									if (worknoteVO == null) {
										Logger.error("未找到流程实例，请检查！");
									} else {
										worknoteVO.setChecknote("Y");
										worknoteVO.setApproveresult("Y");

										map.put("worknote", worknoteVO);
										String pk_approver = "1001A110000000000HV8";// "SIGNAL"
																					// +
																					// pk_approver,
										/*PfUtilClient.runAction(null, "APPROVE",
												"Z2", aggCtPuVO, null, null, null, map);*/
										/*ifbaction.processAction("SIGNAL" + pk_approver, "Z2", null, aggCtPuVO, null, map);*/
										// IPFBusiAction ifbaction =
										// NCLocator.getInstance().lookup(IPFBusiAction.class);
										// ifbaction.processAction("APPROVE",
										// "Z2", null, aggCtPuVO, null, null);
										/*IplatFormEntry ipfo = NCLocator
										.getInstance().lookup(
												IplatFormEntry.class);
										ipfo.processAction("APPROVE", "Z2",
												null, aggCtPuVO, null, map);*/
										getIplatFormEntry().processAction("APPROVE", "Z2", null,
												aggCtPuVO, null, getEparam());
										Logger.error("审批成功，审批用户PK："
												+ pk_approver);
									}
								}
								msg = "[采购合同]名称为" + contractname + "的单据审批成功！";
								// XBX1106新增回填电子合同状态
								String dzsql = "SELECT COUNT('contractPk') as sl FROM RL_DZHTLOGC where 'contractPk' = '"
										+ billID + "'";
								List<Object[]> dzsqlls = getDao.query(dzsql);
								if (Integer.parseInt(dzsqlls.get(0)[0]
										.toString()) > 0) {
									HTFileUtils fileUtils = new HTFileUtils();
									String rtfu = fileUtils.afterApprove(
											billID, "1");
									if ("Y".equals(rtfu)) {
										Logger.error("审批成功后传送电子合同系统成功！");
									} else {
										Logger.error("审批成功后传送电子合同系统成功！");
									}
								}

								// suchb 2020-04-27 根据采购合同推送任务填报单
								BillQuery<AggCtPuVO> bq = new BillQuery<>(
										AggCtPuVO.class);
								String[] pkArr = new String[] { billID };
								AggCtPuVO[] aggCtPuVOArr = bq.query(pkArr);
								pushTaskProcessbyCtpuVO(aggCtPuVOArr[0]);

								// XBX20191119调用合同台账接口
								RLContractTZService service = new RLContractTZService();
								String result = OAGetCGHTJson.getVO(billID,
										service);
								if ("0".equals(result)) {
									htmsg = "[采购合同]名称为" + contractname
											+ "的单据审批调用合同台账成功！";
								} else {
									htmsg = "[采购合同]名称为" + contractname
											+ "的单据审批调用合同台账失败！";
								}

								// 添加采购合同档案
								/*
								 * try {
								 * AddDefdoc.addDefdoc(ctPuVO[0].getParentVO());
								 * } catch (BusinessException e1) { // TODO
								 * 自动生成的 catch 块 msg = "添加采购合同档案错误" +
								 * e1.toString(); e1.printStackTrace(); }
								 */

							} else if ("1".equals(resultFromOA)) {
								UpdStatus("CT_PU", "FSTATUSFLAG", "4",
										"PK_CT_PU", billID);
								// XBX1106新增回填电子合同状态
								HTFileUtils fileUtils = new HTFileUtils();
								String rtfu = fileUtils.afterApprove(billID,
										"0");
								if ("Y".equals(rtfu)) {
									Logger.error("审批成功后传送电子合同系统成功！");
								} else {
									Logger.error("审批成功后传送电子合同系统成功！");
								}
								msg = "[采购合同]名称为" + contractname + "的单据审批未通过！";
							}
							Logger.error(msg);
							String users[] = getRoleUsers("1001ZZ1000000006L64S");// 测试服务
							sendMsgToUser(users, msg);
							// 制单人发送消息
							sendMsgToBillMaker(billMakerID, msg);
							Logger.error("消息发送成功！");
						}
					}
				} else {
					Logger.error("ID:" + billID + "的单据，查询失败");
					msg = "ID:" + billID + "的单据，查询失败";
				}
			} catch (BusinessException e) {
				// TODO 自动生成的 catch 块
				msg = "[采购合同]" + e.toString();
				e.printStackTrace();
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				msg = "[采购合同]" + e.toString();
				e.printStackTrace();
			}

		} else if ("3".equals(billType)) { // 采购订单审批

			IOrderQuery OrderQus = NCLocator.getInstance().lookup(
					IOrderQuery.class);
			IplatFormEntry ipf = (IplatFormEntry) NCLocator.getInstance()
					.lookup(IplatFormEntry.class);// 动作执行类
			try {

				UFBoolean UFBOOLEAN_TRUE = new UFBoolean(false);
				OrderVO[] orderVO = OrderQus.queryOrderVOsByIds(
						new String[] { billID }, UFBOOLEAN_TRUE);
				if (orderVO.length > 0) {
					String status = orderVO[0].getHVO().getForderstatus() + "";// 采购合同状态
					String contractname = orderVO[0].getHVO().getVbillcode();// 订单编号
					String billMakerID = orderVO[0].getHVO().getBillmaker(); // 制单人ID
					String htstatus = orderVO[0].getHVO().getForderstatus()
							+ "";
					if ("0".equals(status)) {
						htstatus = "自由态";
					} else if ("1".equals(status)) {
						htstatus = "提交";
					} else if ("2".equals(status)) {
						htstatus = "正在审批";
					} else if ("3".equals(status)) {
						htstatus = "审批";
					} else if ("4".equals(status)) {
						htstatus = "审批不通过";
					} else if ("5".equals(status)) {
						htstatus = "已冻结";
					} else if ("6".equals(status)) {
						htstatus = "终止";
					}
					if ("2".equals(checType)) { // 如果是取消审批
						if ("0".equals(resultFromOA)) {
							String sql = "UPDATE PO_ORDER SET FORDERSTATUS = 0 WHERE PK_ORDER = '"
									+ billID + "'";
							Logger.error(sql);
							getDao();
							dao.executeUpdate(sql);
							msg = "[采购订单]订单号为 " + contractname + " 的单据取消审批成功！";
						} else {
							msg = "[采购订单]订单号为 " + contractname + " 的单据取消审批被驳回！";
						}

					} else { // 正常审批
						if (!"2".equals(status)) {
							Logger.error("[采购订单]名称为" + contractname + "的状态是："
									+ htstatus + ",不能审批！");
							msg = "[采购订单]名称为" + contractname + "的状态是："
									+ htstatus + ",不能审批！";
						} else {

							if ("0".equals(resultFromOA)) {
								ipf.processAction("APPROVE", "21", null,
										orderVO[0], null, null);
								msg = "[采购订单]订单号为 " + contractname
										+ " 的单据审批成功！";
							} else if ("1".equals(resultFromOA)) {
								// UpdStatus("EC_PURORDER_H","BILLSTATUS","4","PK_PURORDER_H",billID);
								UpdStatus("po_order", "forderstatus", "4",
										"pk_order ", billID);
								// MessageBox.showMessageDialog("提示信息", "合同名称为"
								// +
								// contractname+ "的单据审批未通过！");
								msg = "合同名称为 " + contractname + " 的单据审批未通过！";
							}
							Logger.error(msg);
							String users[] = getRoleUsers("1001A21000000008AOI1");// 测试服务
							sendMsgToUser(users, msg);
							// 制单人发送消息
							sendMsgToBillMaker(billMakerID, msg);
							Logger.error("消息发送成功！");
						}
					}
				} else {
					Logger.error("ID:" + billID + "的单据，查询失败");
					msg = "ID:" + billID + "的单据，查询失败";
				}

			} catch (BusinessException e) {
				msg = "采购订单错误" + e.toString();
				e.printStackTrace();
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				msg = "采购订单错误" + e.toString();
				e.printStackTrace();
			}

		} else if ("4".equals(billType)) { // 物质及服务需求
			IMaterialPlan materialQus = NCLocator.getInstance().lookup(
					IMaterialPlan.class);
			IplatFormEntry ipf = (IplatFormEntry) NCLocator.getInstance()
					.lookup(IplatFormEntry.class);// 动作执行类
			try {
				MaterialPlanBillVO[] materialPlanVO = materialQus
						.queryLastVersionBillVOByPK(billID);
				if (materialPlanVO != null) {
					String status = materialPlanVO[0].getParentVO()
							.getBill_status() + "";// 单据状态
					String paybillno = materialPlanVO[0].getParentVO()
							.getBill_code();// 单据号
					String billMakerID = materialPlanVO[0].getParentVO()
							.getBillmaker(); // 制单人ID
					String htstatus = materialPlanVO[0].getParentVO()
							.getBill_status() + "";
					if ("1".equals(status)) {
						htstatus = "审批通过";
					} else if ("2".equals(status)) {
						htstatus = "审批中";
					} else if ("-99".equals(status)) {
						htstatus = "暂存";
					} else if ("8".equals(status)) {
						htstatus = "签字";
					} else if ("9".equals(status)) {
						htstatus = "未确认";
					} else if ("0".equals(status)) {
						htstatus = "审批未通过";
					}
					if ("2".equals(checType)) { // 如果是取消审批
						if ("0".equals(resultFromOA)) {
							ipf.processAction("UNAPPROVE", "4D14", null,
									materialPlanVO[0], null, null);
							msg = "[物资及服务需求]单据号为 " + paybillno + " 的单据取消审批成功！";
						} else {
							msg = "[物资及服务需求]单据号为 " + paybillno + " 的单据取消审批被驳回！";
						}

					} else { // 正常审批
						if (!"3".equals(status)) {
							// MessageBox.showMessageDialog("提示信息",
							// "单据号为"+paybillno+"的状态是："+htstatus+",不能审批！");
							Logger.error("单据号为" + paybillno + "的状态是："
									+ htstatus + ",不能审批！");
							msg = "单据号为" + paybillno + "的状态是：" + htstatus
									+ ",不能审批！";
						} else {

							if ("0".equals(resultFromOA)) {
								ipf.processAction("APPROVE", "4D14", null,
										materialPlanVO[0], null, null);
								msg = "[物资及服务需求]单据号为" + paybillno + "的单据审批成功！";
								// MessageBox.showMessageDialog("提示信息",msg);
							} else if ("1".equals(resultFromOA)) {
								UpdStatus("PM_MATER_PLAN", "BILL_STATUS", "0",
										"Pk_mater_plan", billID);
								msg = "单据号为" + paybillno + "的单据审批未通过！";
								// MessageBox.showMessageDialog("提示信息",msg);
							}
							// String users[] =
							// getRoleUsers("1001ZZ10000000060PP9");

							Logger.error(msg);
							String users[] = getRoleUsers("1001A210000000072R9V");// 测试服务
							sendMsgToUser(users, msg);
							// 制单人发送消息
							sendMsgToBillMaker(billMakerID, msg);
							Logger.error("消息发送成功！");
							printWriter.write(msg);
						}
					}
				} else {
					Logger.error("ID:" + billID + "的单据，查询失败");
					msg = "ID:" + billID + "的单据，查询失败";
				}
			} catch (BusinessException e) {
				// TODO 自动生成的 catch 块
				msg = "物资及服务需求错误" + e.toString();
				e.printStackTrace();
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				msg = "物资及服务需求错误" + e.toString();
				e.printStackTrace();
			}
		} else if ("5".equals(billType)) {
			// IMaterialPlanAdd 物资及服务调整单 需求变更
			IMaterialPlanAdd materialAddQus = NCLocator.getInstance().lookup(
					IMaterialPlanAdd.class);
			IplatFormEntry ipf = (IplatFormEntry) NCLocator.getInstance()
					.lookup(IplatFormEntry.class);// 动作执行类
			try {
				MaterialPlanAddBillVO[] materialPlanAddVO = materialAddQus
						.queryMaterialAddBySql(" pk_matplanadd = '" + billID
								+ "'");
				if (materialPlanAddVO.length > 0) {
					String status = materialPlanAddVO[0].getParentVO()
							.getBill_status() + "";// 单据状态
					String paybillno = materialPlanAddVO[0].getParentVO()
							.getBill_code();// 单据号
					String billMakerID = materialPlanAddVO[0].getParentVO()
							.getBillmaker(); // 制单人ID
					String htstatus = materialPlanAddVO[0].getParentVO()
							.getBill_status() + "";
					if ("1".equals(status)) {
						htstatus = "审批通过";
					} else if ("2".equals(status)) {
						htstatus = "审批中";
					} else if ("-99".equals(status)) {
						htstatus = "暂存";
					} else if ("8".equals(status)) {
						htstatus = "签字";
					} else if ("9".equals(status)) {
						htstatus = "未确认";
					}
					if ("2".equals(checType)) { // 如果是取消审批
						if ("0".equals(resultFromOA)) {
							ipf.processAction("UNAPPROVE", "4D16", null,
									materialPlanAddVO[0], null, null);
							msg = "[物资及服务调整单]单据号为 " + paybillno + " 的单据取消审批成功！";
						} else {
							msg = "[物资及服务调整单]单据号为 " + paybillno
									+ " 的单据取消审批被驳回！";
						}

					} else { // 正常审批
						if (!"3".equals(status)) {
							// MessageBox.showMessageDialog("提示信息",
							// "单据号为"+paybillno+"的状态是："+htstatus+",不能审批！");
							Logger.error("单据号为" + paybillno + "的状态是："
									+ htstatus + ",不能审批！");
							msg = "单据号为" + paybillno + "的状态是：" + htstatus
									+ ",不能审批！";
						} else {

							if ("0".equals(resultFromOA)) {
								ipf.processAction("APPROVE", "4D16", null,
										materialPlanAddVO[0], null, null);
								// MessageBox.showMessageDialog("提示信息","单据号为"+paybillno+"的单据审批成功！");
								msg = "单据号为" + paybillno + "的单据审批成功！";
							} else if ("1".equals(resultFromOA)) {
								UpdStatus("PM_MATEPLANADD", "BILL_STATUS", "0",
										"PK_MATPLANADD", billID);
								// MessageBox.showMessageDialog("提示信息","单据号为"+paybillno+"的单据审批未通过！");
								msg = "[物资及服务需求调整]单据号为" + paybillno
										+ "的单据审批未通过！";
							}
							Logger.error(msg);
							String users[] = getRoleUsers("1001A21000000008AOIQ");// 测试服务
							sendMsgToUser(users, msg);
							// 制单人发送消息
							sendMsgToBillMaker(billMakerID, msg);
							Logger.error("消息发送成功！");
						}
					}
				} else {
					Logger.error("ID:" + billID + "的单据，查询失败");
					msg = "ID:" + billID + "的单据，查询失败";
				}
			} catch (BusinessException e) {
				msg = "物资及服务调整单" + e.toString();
				e.printStackTrace();
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				msg = "物资及服务调整单" + e.toString();
				e.printStackTrace();
			}

		} /*else if ("6".equals(billType) || ("7".equals(billType))) {
			IArapPayBillQueryService cs = (IArapPayBillQueryService) NCLocator
					.getInstance().lookup(IArapPayBillQueryService.class);
			IplatFormEntry ipf = (IplatFormEntry) NCLocator.getInstance()
					.lookup(IplatFormEntry.class);// 动作执行类
			try {
				AggPayBillVO[] wq = cs.queryBillsByWhereSQL(" PK_PAYBILL = '"
						+ billID + "'");
				if (wq.length > 0) {
					String status = wq[0].getHeadVO().getBillstatus() + "";// 单据状态
					String paybillno = wq[0].getHeadVO().getBillno();// 单据号
					String billMakerID = wq[0].getHeadVO().getBillmaker(); // 制单人ID
					String htstatus = "";
					if ("1".equals(status)) {
						htstatus = "审批通过";
					} else if ("2".equals(status)) {
						htstatus = "审批中";
					} else if ("-99".equals(status)) {
						htstatus = "暂存";
					} else if ("8".equals(status)) {
						htstatus = "签字";
					} else if ("9".equals(status)) {
						htstatus = "未确认";
					}
					if ("2".equals(checType)) { // 如果是取消审批
						if ("0".equals(resultFromOA)) {
							ipf.processAction("UNAPPROVE", "F3", null, wq[0],
									null, null);
							// MessageBox.showMessageDialog("提示信息","单据号为"+paybillno+"的单据审批成功！");
							msg = "[付款单]单据号为" + paybillno + "的单据取消审批成功！";
							String result = BillCancle.Cancle(billID, "1");
							if ("0".equals(result)) {
								htmsg = "[付款单]单据号为" + paybillno
										+ "的单据取消审批调用合同台账成功！";
							} else {
								htmsg = "[付款单]单据号为" + paybillno
										+ "的单据取消审批调用合同台账失败！";
							}
						} else {
							msg = "[付款单]单据号为" + paybillno + "的单据取消审批被驳回！";
						}

					} else { // 正常审批
						if (!"-1".equals(status)) {
							Logger.error("单据号为" + paybillno + "的状态是："
									+ htstatus + ",不能审批！");
							msg = "单据号为" + paybillno + "的状态是：" + htstatus
									+ ",不能审批！";
						} else {
							String users[];
							// 备注：单据状态没有未通过，审批状态有，暂时修改审批状态
							if ("0".equals(resultFromOA)) {
								
								 * ipf.processAction("APPROVE", "F3", null,
								 * wq[0], null, null); msg = "[付款单]单据号为" +
								 * paybillno + "的单据审批成功！"; String result =
								 * GetFuKDJson.getVO(billID);
								 * if("0".equals(result)){ htmsg = "[付款单]单据号为" +
								 * paybillno + "的单据审批调用合同台账成功！"; }else{ htmsg =
								 * "[付款单]单据号为" + paybillno + "的单据审批调用合同台账失败！"; }
								 * // 将OA单据号回写到单据的自定义项
								 * AddDefdoc.updateArapOABill(billID, billCode);
								 
								UpdStatus("AP_PAYBILL", "APPROVESTATUS", "-1",
										"PK_PAYBILL", billID);
								msg = "单据号为" + paybillno + "的单据审批通过,状态改为自由！";
								
								 * ipf.processAction("APPROVE", "F3", null,
								 * wq[0], null, null); msg = "[付款单]单据号为" +
								 * paybillno + "的单据审批成功！"; // 将OA单据号回写到单据的自定义项
								 * AddDefdoc.updateArapOABill(billID, billCode);
								 
							} else if ("1".equals(resultFromOA)) {

								UpdStatus("AP_PAYBILL", "APPROVESTATUS", "-1",
										"PK_PAYBILL", billID);
								msg = "单据号为" + paybillno + "的单据审批未通过,状态改为自由！";
								Logger.error(msg);
								if ("6".endsWith(billType)) // 付款单
								{
									users = getRoleUsers("1001A21000000008AOII");

								} else // 工程付款单
								{
									users = getRoleUsers("1001A21000000008AOIA");
								}

								sendMsgToUser(users, msg);
								Logger.error("消息发送成功！");
								// 制单人发送消息
								sendMsgToBillMaker(billMakerID, msg);
							}
						}
					}
				} else {
					Logger.error("ID:" + billID + "的单据，查询失败");
					msg = "ID:" + billID + "的单据，查询失败";
				}
			} catch (BusinessException e) {
				// TODO 自动生成的 catch 块
				msg = "付款单]单据号" + e.toString();
				e.printStackTrace();
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				msg = "付款单]单据号" + e.toString();
				e.printStackTrace();
			}

			}*/else if ("8".equals(billType)) {
			IBillQueryService billQuery = NCLocator.getInstance().lookup(
					IBillQueryService.class);
			FeeBalanceBillVO feevo = billQuery.querySingleBillByPk(
					FeeBalanceBillVO.class, billID + "");
			IplatFormEntry ipf = (IplatFormEntry) NCLocator.getInstance()
					.lookup(IplatFormEntry.class);// 动作执行类
			FeeBalanceHeadVO headvo = (FeeBalanceHeadVO) feevo.getParentVO();
			String paybillno = headvo.getBill_code();// 单据号
			String billMakerID = headvo.getBillmaker(); // 制单人ID
			try {
				if ("2".equals(checType)) { // 如果是取消审批
					if ("0".equals(resultFromOA)) {
						ipf.processAction("UNAPPROVE", "4D83", null, feevo,
								null, null);
						// MessageBox.showMessageDialog("提示信息","单据号为"+paybillno+"的单据审批成功！");
						msg = "[多编码合同]单据号为" + paybillno + "的单据取消审批成功！";
						// suchb2020-04-27 删除进度填报单
						// InsertAndUptTaskBIll insertUptTaskBill =
						// NCLocator.getInstance().lookup(InsertAndUptTaskBIll.class);
						deleteTaskBillVO(billID);

						String result = BillCancle.Cancle(billID, "0");
						if ("0".equals(result)) {
							htmsg = "[多编码合同]单据号为" + paybillno
									+ "的单据取消审批调用合同台账成功！";
						} else {
							htmsg = "[多编码合同]单据号为" + paybillno
									+ "的单据取消审批调用合同台账失败！";
						}
					} else {
						msg = "[多编码合同]单据号为" + paybillno + "的单据取消审批被驳回！";
					}
				} else { // 正常审批
					if ("0".equals(resultFromOA)) {
						ipf.processAction("APPROVE", "4D83", null, feevo, null,
								null);
						msg = "[多编码合同]单据号为" + paybillno + "的单据审批成功！";
						// suchb20200427 添加，根据多编码合同推送“进度填报单”
						BillQuery<FeeBalanceBillVO> bq = new BillQuery<>(
								FeeBalanceBillVO.class);
						String[] pkArr = new String[] { billID };
						FeeBalanceBillVO[] feeBalanceBillVOArr = bq
								.query(pkArr);
						pushTaskProcessByFee(feeBalanceBillVOArr[0]);

						RLContractTZService service = new RLContractTZService();
						String result = OAGetDBMJson.getVO(billID, service);
						if ("0".equals(result)) {
							System.out.println("[多编码合同]单据号为"
									+ headvo.getBill_code() + "的单据审批调用合同台账成功！");
						} else {
							System.out.println("[多编码合同]单据号为"
									+ headvo.getBill_code() + "的单据审批调用合同台账失败！");
						}
					} else if ("1".equals(resultFromOA)) {
						UpdStatus("PM_FEEBALANCE", "BILL_STATUS", "-1",
								"PK_FEEBALANCE", billID);
						msg = "[多编码合同]单据号为" + paybillno + "的单据审批未通过！";
					}
					Logger.error(msg);
					String users[] = getRoleUsers("1001A21000000008AOIQ");// 测试服务
					sendMsgToUser(users, msg);
					// 制单人发送消息
					sendMsgToBillMaker(billMakerID, msg);
					Logger.error("消息发送成功！");
				}
			} catch (BusinessException e) {
				// TODO 自动生成的 catch 块
				msg = "[多编码合同]单据号" + e.toString();
				e.printStackTrace();
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				msg = "[多编码合同]单据号" + e.toString();
				e.printStackTrace();
			}
		} else if ("9".equals(billType)) {
			// 预算调整
			IBillQueryService billQuery = NCLocator.getInstance().lookup(
					IBillQueryService.class);
			BudgetAddBillVO ystzvo = billQuery.querySingleBillByPk(
					BudgetAddBillVO.class, billID + "");
			IplatFormEntry ipf = (IplatFormEntry) NCLocator.getInstance()
					.lookup(IplatFormEntry.class);// 动作执行类
			BudgetAddHeadVO headvo = (BudgetAddHeadVO) ystzvo.getParentVO();
			String paybillno = headvo.getBill_code();// 单据号
			String billMakerID = headvo.getBillmaker(); // 制单人ID
			try {
				if ("2".equals(checType)) { // 如果是取消审批
					if ("0".equals(resultFromOA)) {
						ipf.processAction("UNAPPROVE", "4D20", null, ystzvo,
								null, null);
						// MessageBox.showMessageDialog("提示信息","单据号为"+paybillno+"的单据审批成功！");
						msg = "[预算调整]单据号为" + paybillno + "的单据取消审批成功！";
					} else {
						msg = "[预算调整]单据号为" + paybillno + "的单据取消审批被驳回！";
					}
				} else { // 正常审批
					if ("0".equals(resultFromOA)) {
						ipf.processAction("APPROVE", "4D20", null, ystzvo,
								null, null);
						msg = "[预算调整]单据号为" + paybillno + "的单据审批成功！";
					} else if ("1".equals(resultFromOA)) {
						UpdStatus("PM_BUDGETADD", "BILL_STATUS", "-1",
								"PK_BUDGETADD", billID);
						msg = "[预算调整]单据号为" + paybillno + "的单据审批未通过！";
					}
					Logger.error(msg);
					String users[] = getRoleUsers("1001A21000000008AOIQ");// 测试服务
					sendMsgToUser(users, msg);
					// 制单人发送消息
					sendMsgToBillMaker(billMakerID, msg);
					Logger.error("消息发送成功！");
				}
			} catch (BusinessException e) {
				// TODO 自动生成的 catch 块
				msg = "[预算调整]单据号" + e.toString();
				e.printStackTrace();
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				msg = "[预算调整]单据号" + e.toString();
				e.printStackTrace();
			}
		} else if ("10".equals(billType)) {
			// 开工单
			IBillQueryService billQuery = NCLocator.getInstance().lookup(
					IBillQueryService.class);
			ProjectProposalBillVO kgdvo = billQuery.querySingleBillByPk(
					ProjectProposalBillVO.class, billID + "");
			IplatFormEntry ipf = (IplatFormEntry) NCLocator.getInstance()
					.lookup(IplatFormEntry.class);// 动作执行类
			ProjectProposalHeadVO headvo = (ProjectProposalHeadVO) kgdvo
					.getParentVO();
			String paybillno = headvo.getBill_code();// 单据号
			String billMakerID = headvo.getBillmaker(); // 制单人ID
			try {
				if ("2".equals(checType)) { // 如果是取消审批
					if ("0".equals(resultFromOA)) {
						ipf.processAction("UNAPPROVE", "4D15", null, kgdvo,
								null, null);
						// suchb20200427 添加，根据多编码合同删除“进度填报单”
						// InsertAndUptTaskBIll insertUptTaskBill =
						// NCLocator.getInstance().lookup(InsertAndUptTaskBIll.class);
						//deleteTaskBillVO(billID);
						msg = "[开工单]单据号为" + paybillno + "的单据取消审批成功！";
					} else {
						msg = "[开工单]单据号为" + paybillno + "的单据取消审批被驳回！";
					}
				} else { // 正常审批
					if ("0".equals(resultFromOA)) {
						ipf.processAction("APPROVE", "4D15", null, kgdvo, null,
								null);
						msg = "[开工单]单据号为" + paybillno + "的单据审批成功！";
						// suchb20200427 添加，根据多编码合同推送“进度填报单”
						BillQuery<ProjectProposalBillVO> bq = new BillQuery<>(
								ProjectProposalBillVO.class);
						String[] pkArr = new String[] { billID };
						ProjectProposalBillVO[] projectProposalBillVOArr = bq
								.query(pkArr);
						//pushTaskProcessByProposal(projectProposalBillVOArr[0]);

					} else if ("1".equals(resultFromOA)) {
						UpdStatus("PM_PROJECTPROPOSAL", "BILL_STATUS", "-1",
								"PK_PROJECTPROPOSAL", billID);
						msg = "[开工单]单据号为" + paybillno + "的单据审批未通过！";
					}
					Logger.error(msg);
					String users[] = getRoleUsers("1001A21000000008AOIQ");// 测试服务
					sendMsgToUser(users, msg);
					// 制单人发送消息
					sendMsgToBillMaker(billMakerID, msg);
					Logger.error("消息发送成功！");
				}
			} catch (BusinessException e) {
				// TODO 自动生成的 catch 块
				msg = "[开工单]单据号" + e.toString();
				e.printStackTrace();
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				msg = "[开工单]单据号" + e.toString();
				e.printStackTrace();
			}
		} else if ("11".equals(billType)) {
			// 追加项目申请单
			IBillQueryService billQuery = NCLocator.getInstance().lookup(
					IBillQueryService.class);
			ProjectRegisterBillVO kgdvo = billQuery.querySingleBillByPk(
					ProjectRegisterBillVO.class, billID + "");
			IplatFormEntry ipf = (IplatFormEntry) NCLocator.getInstance()
					.lookup(IplatFormEntry.class);// 动作执行类
			ProjectRegisterHeadVO headvo = (ProjectRegisterHeadVO) kgdvo
					.getParentVO();
			String paybillno = headvo.getBill_code();// 单据号
			String billMakerID = headvo.getBillmaker(); // 制单人ID
			try {
				if ("2".equals(checType)) { // 如果是取消审批
					if ("0".equals(resultFromOA)) {
						ipf.processAction("UNAPPROVE", "4D11", null, kgdvo,
								null, null);
						msg = "[追加项目申请单]单据号为" + paybillno + "的单据取消审批成功！";
					} else {
						msg = "[追加项目申请单]单据号为" + paybillno + "的单据取消审批被驳回！";
					}
				} else { // 正常审批
					if ("0".equals(resultFromOA)) {
						ipf.processAction("APPROVE", "4D11", null, kgdvo, null,
								null);
						msg = "[追加项目申请单]单据号为" + paybillno + "的单据审批成功！";
					} else if ("1".equals(resultFromOA)) {
						UpdStatus("pm_projectregister", "BILL_STATUS", "-1",
								"PK_projectregister", billID);
						msg = "[追加项目申请单]单据号为" + paybillno + "的单据审批未通过！";
					}
					Logger.error(msg);
					String users[] = getRoleUsers("1001A21000000008AOIQ");// 测试服务
					sendMsgToUser(users, msg);
					// 制单人发送消息
					sendMsgToBillMaker(billMakerID, msg);
					Logger.error("消息发送成功！");
				}
			} catch (BusinessException e) {
				// TODO 自动生成的 catch 块
				msg = "[追加项目申请单]单据号" + e.toString();
				e.printStackTrace();
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				msg = "[追加项目申请单]单据号" + e.toString();
				e.printStackTrace();
			}
		} else if ("12".equals(billType)) {
			// 补充协议
			IBillQueryService billQuery = NCLocator.getInstance().lookup(
					IBillQueryService.class);
			ContrAlterBillVO kgdvo = billQuery.querySingleBillByPk(
					ContrAlterBillVO.class, billID + "");
			IplatFormEntry ipf = (IplatFormEntry) NCLocator.getInstance()
					.lookup(IplatFormEntry.class);// 动作执行类
			ContrAlterHeadVO headvo = (ContrAlterHeadVO) kgdvo.getParentVO();
			String paybillno = headvo.getBill_code();// 单据号
			String billMakerID = headvo.getBillmaker(); // 制单人ID
			try {
				if ("2".equals(checType)) { // 如果是取消审批
					if ("0".equals(resultFromOA)) {
						ipf.processAction("UNAPPROVE", "4D44", null, kgdvo,
								null, null);
						msg = "[补充协议]单据号为" + paybillno + "的单据取消审批成功！";
					} else {
						msg = "[补充协议]单据号为" + paybillno + "的单据取消审批被驳回！";
					}
				} else { // 正常审批
					if ("0".equals(resultFromOA)) {
						ipf.processAction("APPROVE", "4D44", null, kgdvo, null,
								null);
						msg = "[补充协议]单据号为" + paybillno + "的单据审批成功！";
					} else if ("1".equals(resultFromOA)) {
						UpdStatus("pm_contr_alter", "BILL_STATUS", "-1",
								"PK_contr_alter", billID);
						msg = "[补充协议]单据号为" + paybillno + "的单据审批未通过！";
					}
					Logger.error(msg);
					String users[] = getRoleUsers("1001A21000000008AOIQ");// 测试服务
					sendMsgToUser(users, msg);
					// 制单人发送消息
					sendMsgToBillMaker(billMakerID, msg);
					Logger.error("消息发送成功！");
				}
			} catch (BusinessException e) {
				// TODO 自动生成的 catch 块
				msg = "[追加项目申请单]单据号" + e.toString();
				e.printStackTrace();
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				msg = "[追加项目申请单]单据号" + e.toString();
				e.printStackTrace();
			}
		} else if ("13".equals(billType)) {
			// 勘察申请单
			IBillQueryService billQuery = NCLocator.getInstance().lookup(
					IBillQueryService.class);
			ProjectProposalBillVO kgdvo = billQuery.querySingleBillByPk(
					ProjectProposalBillVO.class, billID + "");
			IplatFormEntry ipf = (IplatFormEntry) NCLocator.getInstance()
					.lookup(IplatFormEntry.class);// 动作执行类
			ProjectProposalHeadVO headvo = (ProjectProposalHeadVO) kgdvo
					.getParentVO();
			String paybillno = headvo.getBill_code();// 单据号
			String billMakerID = headvo.getBillmaker(); // 制单人ID
			try {
				if ("2".equals(checType)) { // 如果是取消审批
					if ("0".equals(resultFromOA)) {
						ipf.processAction("UNAPPROVE", "4D15", null, kgdvo,
								null, null);
						msg = "[勘察申请单]单据号为" + paybillno + "的单据取消审批成功！";
					} else {
						msg = "[勘察申请单]单据号为" + paybillno + "的单据取消审批被驳回！";
					}
				} else { // 正常审批
					if ("0".equals(resultFromOA)) {
						ipf.processAction("APPROVE", "4D15", null, kgdvo, null,
								null);
						msg = "[勘察申请单]单据号为" + paybillno + "的单据审批成功！";
					} else if ("1".equals(resultFromOA)) {
						UpdStatus("PM_PROJECTPROPOSAL", "BILL_STATUS", "-1",
								"PK_PROJECTPROPOSAL", billID);
						msg = "[勘察申请单]单据号为" + paybillno + "的单据审批未通过！";
					}
					Logger.error(msg);
					String users[] = getRoleUsers("1001A21000000008AOIQ");// 测试服务
					sendMsgToUser(users, msg);
					// 制单人发送消息
					sendMsgToBillMaker(billMakerID, msg);
					Logger.error("消息发送成功！");
				}
			} catch (BusinessException e) {
				// TODO 自动生成的 catch 块
				msg = "[勘察申请单]单据号" + e.toString();
				e.printStackTrace();
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				msg = "[勘察申请单]单据号" + e.toString();
				e.printStackTrace();
			}
		} else if ("14".equals(billType)) {
			// 设计申请单
			IBillQueryService billQuery = NCLocator.getInstance().lookup(
					IBillQueryService.class);
			ProjectProposalBillVO kgdvo = billQuery.querySingleBillByPk(
					ProjectProposalBillVO.class, billID + "");
			IplatFormEntry ipf = (IplatFormEntry) NCLocator.getInstance()
					.lookup(IplatFormEntry.class);// 动作执行类
			ProjectProposalHeadVO headvo = (ProjectProposalHeadVO) kgdvo
					.getParentVO();
			String paybillno = headvo.getBill_code();// 单据号
			String billMakerID = headvo.getBillmaker(); // 制单人ID
			try {
				if ("2".equals(checType)) { // 如果是取消审批
					if ("0".equals(resultFromOA)) {
						ipf.processAction("UNAPPROVE", "4D15", null, kgdvo,
								null, null);
						msg = "[设计申请单]单据号为" + paybillno + "的单据取消审批成功！";
					} else {
						msg = "[设计申请单]单据号为" + paybillno + "的单据取消审批被驳回！";
					}
				} else { // 正常审批
					if ("0".equals(resultFromOA)) {
						ipf.processAction("APPROVE", "4D15", null, kgdvo, null,
								null);
						msg = "[设计申请单]单据号为" + paybillno + "的单据审批成功！";
					} else if ("1".equals(resultFromOA)) {
						UpdStatus("PM_PROJECTPROPOSAL", "BILL_STATUS", "-1",
								"PK_PROJECTPROPOSAL", billID);
						msg = "[设计申请单]单据号为" + paybillno + "的单据审批未通过！";
					}
					Logger.error(msg);
					String users[] = getRoleUsers("1001A21000000008AOIQ");// 测试服务
					sendMsgToUser(users, msg);
					// 制单人发送消息
					sendMsgToBillMaker(billMakerID, msg);
					Logger.error("消息发送成功！");
				}
			} catch (BusinessException e) {
				// TODO 自动生成的 catch 块
				msg = "[设计申请单]单据号" + e.toString();
				e.printStackTrace();
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				msg = "[设计申请单]单据号" + e.toString();
				e.printStackTrace();
			}
		} else if ("15".equals(billType)) {
			// 刨掘申请单
			IBillQueryService billQuery = NCLocator.getInstance().lookup(
					IBillQueryService.class);
			ProjectProposalBillVO kgdvo = billQuery.querySingleBillByPk(
					ProjectProposalBillVO.class, billID + "");
			IplatFormEntry ipf = (IplatFormEntry) NCLocator.getInstance()
					.lookup(IplatFormEntry.class);// 动作执行类
			ProjectProposalHeadVO headvo = (ProjectProposalHeadVO) kgdvo
					.getParentVO();
			String paybillno = headvo.getBill_code();// 单据号
			String billMakerID = headvo.getBillmaker(); // 制单人ID
			try {
				if ("2".equals(checType)) { // 如果是取消审批
					if ("0".equals(resultFromOA)) {
						ipf.processAction("UNAPPROVE", "4D15", null, kgdvo,
								null, null);
						msg = "[刨掘申请单]单据号为" + paybillno + "的单据取消审批成功！";
					} else {
						msg = "[刨掘申请单]单据号为" + paybillno + "的单据取消审批被驳回！";
					}
				} else { // 正常审批
					if ("0".equals(resultFromOA)) {
						ipf.processAction("APPROVE", "4D15", null, kgdvo, null,
								null);
						msg = "[刨掘申请单]单据号为" + paybillno + "的单据审批成功！";
					} else if ("1".equals(resultFromOA)) {
						UpdStatus("PM_PROJECTPROPOSAL", "BILL_STATUS", "-1",
								"PK_PROJECTPROPOSAL", billID);
						msg = "[刨掘申请单]单据号为" + paybillno + "的单据审批未通过！";
					}
					Logger.error(msg);
					String users[] = getRoleUsers("1001A21000000008AOIQ");// 测试服务
					sendMsgToUser(users, msg);
					// 制单人发送消息
					sendMsgToBillMaker(billMakerID, msg);
					Logger.error("消息发送成功！");
				}
			} catch (BusinessException e) {
				// TODO 自动生成的 catch 块
				msg = "[刨掘申请单]单据号" + e.toString();
				e.printStackTrace();
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				msg = "[刨掘申请单]单据号" + e.toString();
				e.printStackTrace();
			}
		} else if ("16".equals(billType)) {
			// 控制价委托申请单
			IBillQueryService billQuery = NCLocator.getInstance().lookup(
					IBillQueryService.class);
			ProjectProposalBillVO kgdvo = billQuery.querySingleBillByPk(
					ProjectProposalBillVO.class, billID + "");
			IplatFormEntry ipf = (IplatFormEntry) NCLocator.getInstance()
					.lookup(IplatFormEntry.class);// 动作执行类
			ProjectProposalHeadVO headvo = (ProjectProposalHeadVO) kgdvo
					.getParentVO();
			String paybillno = headvo.getBill_code();// 单据号
			String billMakerID = headvo.getBillmaker(); // 制单人ID
			try {
				if ("2".equals(checType)) { // 如果是取消审批
					if ("0".equals(resultFromOA)) {
						ipf.processAction("UNAPPROVE", "4D15", null, kgdvo,
								null, null);
						msg = "[控制价委托申请单]单据号为" + paybillno + "的单据取消审批成功！";
					} else {
						msg = "[控制价委托申请单]单据号为" + paybillno + "的单据取消审批被驳回！";
					}
				} else { // 正常审批
					if ("0".equals(resultFromOA)) {
						ipf.processAction("APPROVE", "4D15", null, kgdvo, null,
								null);
						msg = "[控制价委托申请单]单据号为" + paybillno + "的单据审批成功！";
					} else if ("1".equals(resultFromOA)) {
						UpdStatus("PM_PROJECTPROPOSAL", "BILL_STATUS", "-1",
								"PK_PROJECTPROPOSAL", billID);
						msg = "[控制价委托申请单]单据号为" + paybillno + "的单据审批未通过！";
					}
					Logger.error(msg);
					String users[] = getRoleUsers("1001A21000000008AOIQ");// 测试服务
					sendMsgToUser(users, msg);
					// 制单人发送消息
					sendMsgToBillMaker(billMakerID, msg);
					Logger.error("消息发送成功！");
				}
			} catch (BusinessException e) {
				// TODO 自动生成的 catch 块
				msg = "[控制价委托申请单]单据号" + e.toString();
				e.printStackTrace();
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				msg = "[控制价委托申请单]单据号" + e.toString();
				e.printStackTrace();
			}
		} else if ("17".equals(billType)) {
			// 竣工验收
			IBillQueryService billQuery = NCLocator.getInstance().lookup(
					IBillQueryService.class);
			ProjectCheckBillVO kgdvo = billQuery.querySingleBillByPk(
					ProjectCheckBillVO.class, billID + "");
			IplatFormEntry ipf = (IplatFormEntry) NCLocator.getInstance()
					.lookup(IplatFormEntry.class);// 动作执行类
			ProjectCheckHeadVO headvo = (ProjectCheckHeadVO) kgdvo
					.getParentVO();
			String paybillno = headvo.getBill_code();// 单据号
			String billMakerID = headvo.getBillmaker(); // 制单人ID
			try {
				if ("2".equals(checType)) { // 如果是取消审批
					if ("0".equals(resultFromOA)) {
						ipf.processAction("UNAPPROVE", "4D36", null, kgdvo,
								null, null);
						// suchb20200427 添加，根据多编码合同删除“进度填报单”
						// InsertAndUptTaskBIll insertUptTaskBill =
						// NCLocator.getInstance().lookup(InsertAndUptTaskBIll.class);
						deleteTaskBillVO(billID);

						msg = "[竣工验收]单据号为" + paybillno + "的单据取消审批成功！";
					} else {
						msg = "[竣工验收]单据号为" + paybillno + "的单据取消审批被驳回！";
					}
				} else { // 正常审批
					if ("0".equals(resultFromOA)) {
						ipf.processAction("APPROVE", "4D36", null, kgdvo, null,
								null);
						// suchb20200427 添加，根据多编码合同推送“进度填报单”
						BillQuery<ProjectCheckBillVO> bq = new BillQuery<>(
								ProjectCheckBillVO.class);
						String[] pkArr = new String[] { billID };
						ProjectCheckBillVO[] projectCheckBillVOArr = bq
								.query(pkArr);

						pushTaskProcessBycheck(projectCheckBillVOArr[0]);
						msg = "[竣工验收]单据号为" + paybillno + "的单据审批成功！";
					} else if ("1".equals(resultFromOA)) {
						UpdStatus("pm_check_head", "BILL_STATUS", "-1",
								"pk_pro_check", billID);
						msg = "[竣工验收]单据号为" + paybillno + "的单据审批未通过！";
					}
					Logger.error(msg);
					String users[] = getRoleUsers("1001A21000000008AOIQ");// 测试服务
					sendMsgToUser(users, msg);
					// 制单人发送消息
					sendMsgToBillMaker(billMakerID, msg);
					Logger.error("消息发送成功！");
				}
			} catch (BusinessException e) {
				// TODO 自动生成的 catch 块
				msg = "[竣工验收]单据号" + e.toString();
				e.printStackTrace();
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				msg = "[竣工验收]单据号" + e.toString();
				e.printStackTrace();
			}
		} else if ("21".equals(billType)) {
			// 结算单
			IBillQueryService billQuery = NCLocator.getInstance().lookup(
					IBillQueryService.class);
			ContractBalanceBillVO kgdvo = billQuery.querySingleBillByPk(
					ContractBalanceBillVO.class, billID + "");
			IplatFormEntry ipf = (IplatFormEntry) NCLocator.getInstance()
					.lookup(IplatFormEntry.class);// 动作执行类
			ContractBalanceHeadVO headvo = (ContractBalanceHeadVO) kgdvo
					.getParentVO();
			String paybillno = headvo.getBill_code();// 单据号
			String billMakerID = headvo.getBillmaker(); // 制单人ID
			try {
				if ("2".equals(checType)) { // 如果是取消审批
					if ("0".equals(resultFromOA)) {
						ipf.processAction("UNAPPROVE", "4D50", null, kgdvo,
								null, null);
						msg = "[结算单]单据号为" + paybillno + "的单据取消审批成功！";
					} else {
						msg = "[结算单]单据号为" + paybillno + "的单据取消审批被驳回！";
					}
				} else { // 正常审批
					if ("0".equals(resultFromOA)) {
						ipf.processAction("APPROVE", "4D50", null, kgdvo, null,
								null);
						msg = "[结算单]单据号为" + paybillno + "的单据审批成功！";
					} else if ("1".equals(resultFromOA)) {
						UpdStatus("PM_CONTR_BAL", "BILL_STATUS", "-1",
								"PK_CONTR_BAL", billID);
						msg = "[结算单]单据号为" + paybillno + "的单据审批未通过！";
					}
					Logger.error(msg);
					String users[] = getRoleUsers("1001A21000000008AOIQ");// 测试服务
					sendMsgToUser(users, msg);
					// 制单人发送消息
					sendMsgToBillMaker(billMakerID, msg);
					Logger.error("消息发送成功！");
				}
			} catch (BusinessException e) {
				// TODO 自动生成的 catch 块
				msg = "[结算单]单据号" + e.toString();
				e.printStackTrace();
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				msg = "[结算单]单据号" + e.toString();
				e.printStackTrace();
			}
		} else if ("30".equals(billType)) {
			// 付款合同
			IBillQueryService billQuery = NCLocator.getInstance().lookup(
					IBillQueryService.class);
			AggCtApVO ctvo = billQuery.querySingleBillByPk(AggCtApVO.class,
					billID + "");
			IplatFormEntry ipf = (IplatFormEntry) NCLocator.getInstance()
					.lookup(IplatFormEntry.class);// 动作执行类
			CtApVO headvo = ctvo.getParentVO();
			String paybillno = headvo.getVbillcode();// 单据号
			String billMakerID = headvo.getBillmaker(); // 制单人ID
			try {
				if ("2".equals(checType)) { // 如果是取消审批
					if ("0".equals(resultFromOA)) {
						ipf.processAction("UNAPPROVE", "FCT1", null, ctvo,
								null, null);
						msg = "[付款合同]单据号为" + paybillno + "的单据取消审批成功！";
					} else {
						msg = "[付款合同]单据号为" + paybillno + "的单据取消审批被驳回！";
					}
				} else {
					if ("0".equals(resultFromOA)) {
						ipf.processAction("APPROVE", "FCT1", null, ctvo, null,
								null);
						/*String wfsql = "SELECT PK_CHECKFLOW FROM PUB_WORKFLOWNOTE WHERE BILLNO = '"+ctvo.getParentVO().getPrimaryKey()+"' AND ISCHECK = 'N'";
						List<Object[]> wfls = getDao.query(wfsql);
						if(wfls == null || wfls.size() == 0){
							return ;
						}else{
							HashMap map = new HashMap();
							WorkflownoteVO worknoteVO = (WorkflownoteVO) dao.retrieveByPK(
									WorkflownoteVO.class, wfls.get(0)[0].toString());
							//Logger.error(worknoteVO);
							// System.out.println(worknoteVO);
							if (worknoteVO == null) {
								Logger.error("未找到流程实例，请检查！");
							} else {
								worknoteVO.setChecknote("y");
								worknoteVO.setApproveresult("Y");
								map.put("worknote", worknoteVO);
								String pk_approver = "1001A110000000000HV8";
								PfUtilClient.runAction(null, "SIGNAL" + pk_approver,
										"FCT1", ctvo, null, null, null, map);
										IPFBusiAction ifbaction = NCLocator.getInstance().lookup(IPFBusiAction.class);
								ifbaction.processAction("SIGNAL" + pk_approver, transi_type, null, clfVO, null, map);
								Logger.error("审批成功，审批用户PK：" + pk_approver);
							}
						}*/
						msg = "[付款合同]单据号为" + paybillno + "的单据审批成功！";
					} else if ("1".equals(resultFromOA)) {
						UpdStatus("fct_ap", "fstatusflag", "4", "pk_fct_ap",
								billID);
						msg = "[付款合同]单据号为" + paybillno + "的单据审批未通过！";
					}
					Logger.error(msg);
					String users[] = getRoleUsers("1001A21000000008AOIQ");// 测试服务
					sendMsgToUser(users, msg);
					// 制单人发送消息
					sendMsgToBillMaker(billMakerID, msg);
					Logger.error("消息发送成功！");
				}
			} catch (BusinessException e) {
				msg = "[付款合同]单据号" + e.toString();
				e.printStackTrace();
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				msg = "[付款合同]单据号" + e.toString();
				e.printStackTrace();
			}

		} else if ("31".equals(billType)) {
			// 收款合同
			IBillQueryService billQuery = NCLocator.getInstance().lookup(
					IBillQueryService.class);
			AggCtArVO ctvo = billQuery.querySingleBillByPk(AggCtArVO.class,
					billID + "");
			IplatFormEntry ipf = (IplatFormEntry) NCLocator.getInstance()
					.lookup(IplatFormEntry.class);// 动作执行类
			CtArVO headvo = ctvo.getParentVO();
			String paybillno = headvo.getVbillcode();// 单据号
			String billMakerID = headvo.getBillmaker(); // 制单人ID
			try {
				if ("2".equals(checType)) { // 如果是取消审批
					if ("0".equals(resultFromOA)) {
						ipf.processAction("UNAPPROVE", "FCT2", null, ctvo,
								null, null);
						msg = "[收款合同]单据号为" + paybillno + "的单据取消审批成功！";
					} else {
						msg = "[收款合同]单据号为" + paybillno + "的单据取消审批被驳回！";
					}
				} else {
					if ("0".equals(resultFromOA)) {
						ipf.processAction("APPROVE", "FCT2", null, ctvo, null,
								null);
						/*String wfsql = "SELECT PK_CHECKFLOW FROM PUB_WORKFLOWNOTE WHERE BILLNO = '"+ctvo.getParentVO().getPrimaryKey()+"' AND ISCHECK = 'N'";
						List<Object[]> wfls = getDao.query(wfsql);
						if(wfls == null || wfls.size() == 0){
							return ;
						}else{
							HashMap map = new HashMap();
							WorkflownoteVO worknoteVO = (WorkflownoteVO) dao.retrieveByPK(
									WorkflownoteVO.class, wfls.get(0)[0].toString());
							//Logger.error(worknoteVO);
							// System.out.println(worknoteVO);
							if (worknoteVO == null) {
								Logger.error("未找到流程实例，请检查！");
							} else {
								worknoteVO.setChecknote("y");
								worknoteVO.setApproveresult("Y");
								map.put("worknote", worknoteVO);
								String pk_approver = "1001A110000000000HV8";
								PfUtilClient.runAction(null, "SIGNAL" + pk_approver,
										"FCT2", ctvo, null, null, null, map);
										IPFBusiAction ifbaction = NCLocator.getInstance().lookup(IPFBusiAction.class);
								ifbaction.processAction("SIGNAL" + pk_approver, transi_type, null, clfVO, null, map);
								Logger.error("审批成功，审批用户PK：" + pk_approver);
							}
						}*/
						msg = "[收款合同]单据号为" + paybillno + "的单据审批成功！";
					} else if ("1".equals(resultFromOA)) {
						UpdStatus("fct_ar", "fstatusflag", "4", "pk_fct_ar",
								billID);
						msg = "[收款合同]单据号为" + paybillno + "的单据审批未通过！";
					}
					Logger.error(msg);
					String users[] = getRoleUsers("1001A21000000008AOIQ");// 测试服务
					sendMsgToUser(users, msg);
					// 制单人发送消息
					sendMsgToBillMaker(billMakerID, msg);
					Logger.error("消息发送成功！");
				}
			} catch (BusinessException e) {
				msg = "[付款合同]单据号" + e.toString();
				e.printStackTrace();
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				msg = "[付款合同]单据号" + e.toString();
				e.printStackTrace();
			}

		}
		printWriter.print(msg);
		printWriter.print(htmsg);
		printWriter.close();
	}

	/**
	 * 根据roleID查找人员
	 * 
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	private String[] getRoleUsers(String roleID) throws DAOException {
		BaseDAO dao = new BaseDAO();// 只能在private端使用
		String querySql = "select c.cuserid, a.pk_role, a.role_name,c.user_name from sm_role a left join sm_user_role b  on a.pk_role=b.pk_role "
				+ " left join sm_user c on c.cuserid=b.cuserid  where a.pk_role= '"
				+ roleID + "'";
		// Object result1 =
		// dao.executeQuery("select vbillcode from demo_quotation where pk_salequotation='某个主键'",
		// new ColumnProcessor());//查询语句只能返回一行一列结果
		List<Object[]> resultList = (List<Object[]>) dao.executeQuery(querySql,
				new ArrayListProcessor());// 查询语句可以返回多行多列
		String[] users;
		if (resultList.size() > 0) {
			users = new String[resultList.size()];
			for (int i = 0; i < resultList.size(); i++) {
				Object[] item = resultList.get(i);
				users[i] = item[0].toString();
			}
		} else {
			users = null;
		}

		return users;

	}

	/*****
	 * 向制单人发送消息
	 * 
	 * @throws Exception
	 * ****/
	private void sendMsgToBillMaker(String billMakerID, String Msg)
			throws Exception {
		String nc_sender = InvocationInfoProxy.getInstance().getUserId();
		if (!"".equals(billMakerID)) {
			NCMessage ncMsg = new NCMessage();
			MessageVO messageVO = new MessageVO();
			messageVO.setMsgtype("nc");// 设置消息类型
			messageVO.setSender(nc_sender);// 发送方
			messageVO.setContenttype("text/plain");// 设置消息内容类型!
			messageVO.setDestination("outbox");
			messageVO.setDetail(null);
			messageVO.setIsdelete(UFBoolean.FALSE);
			messageVO.setIsread(UFBoolean.FALSE);
			messageVO.setExpiration(null);
			messageVO.setSendtime(new UFDateTime());
			messageVO.setSendstate(UFBoolean.TRUE);
			messageVO.setPriority(10);
			messageVO.setSubject("单据状态更新");// 主题
			messageVO.setContent(Msg);// 内容
			// System.out.println("接收人PK："+users[i]);
			messageVO.setReceiver(billMakerID);// 接收方主键
			messageVO.setMsgsourcetype("notice");// 通知(notice)、预警、工作任务
			ncMsg.setMessage(messageVO);
			MessageCenter.sendMessage(new NCMessage[] { ncMsg });

		}
	}

	/**
	 * 向用户发送消息
	 */
	private void sendMsgToUser(String[] users, String msg) {
		String nc_sender = InvocationInfoProxy.getInstance().getUserId();
		if (users != null) {
			try {
				for (int i = 0; i < users.length; i++) {
					NCMessage ncMsg = new NCMessage();
					MessageVO messageVO = new MessageVO();
					messageVO.setMsgtype("nc");// 设置消息类型
					messageVO.setSender(nc_sender);// 发送方
					messageVO.setContenttype("text/plain");// 设置消息内容类型
					messageVO.setDestination("outbox");
					messageVO.setDetail(null);
					messageVO.setIsdelete(UFBoolean.FALSE);
					messageVO.setIsread(UFBoolean.FALSE);
					messageVO.setExpiration(null);
					messageVO.setSendtime(new UFDateTime());
					messageVO.setSendstate(UFBoolean.TRUE);
					messageVO.setPriority(10);
					messageVO.setSubject("单据状态更新");// 主题
					messageVO.setContent(msg);// 内容
					System.out.println("接收人PK：" + users[i]);
					messageVO.setReceiver(users[i]);// 接收方主键
					messageVO.setMsgsourcetype("notice");// 通知(notice)、预警、工作任务
					ncMsg.setMessage(messageVO);
					MessageCenter.sendMessage(new NCMessage[] { ncMsg });
				}

			} catch (Exception e) {
				// TODO: handle exception
				Logger.error("向消息中心发送消息时发生错误" + e.getMessage(), e);
			}
		}
	}

	// 2019-06-04修改单据状态
	public void UpdStatus(String TableName, String FieldName,
			String FieldValue, String PKNAME, String PKValue)
			throws DAOException {
		String sql = "UPDATE " + TableName + " SET " + FieldName + " = '"
				+ FieldValue + "' WHERE " + PKNAME + " = '" + PKValue + "'";
		getDao().executeUpdate(sql);
	}

	// 建立SQL链接
	private BaseDAO getDao() {
		if (dao == null) {
			dao = new BaseDAO();
		}
		return dao;
	}

	// 任务进度填报推送
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);

	// 根据采购合同VO推送进度填报单
	public Boolean pushTaskProcessbyCtpuVO(AggCtPuVO aggCtPuVO) {
		try {
			CtPuVO headVO = (CtPuVO) aggCtPuVO.getParentVO();
			CtPuBVO[] bodyVOArr = (CtPuBVO[]) aggCtPuVO.getChildrenVO();
			// 循环表体添加
			for (int i = 0; i < bodyVOArr.length; i++) {

				CtPuBVO bodyVO = bodyVOArr[i];
				TaskProgressVO taskProgressVO = new TaskProgressVO();
				taskProgressVO.setBillmaker(headVO.getBillmaker());
				taskProgressVO.setBillmaketime(headVO.getDbilldate()); // 制单日期
				taskProgressVO.setCreator(headVO.getCreator());
				taskProgressVO.setCreationtime(headVO.getCreationtime());
				// taskProgressVO.setMemo(headVO.getMemo()); // 备注
				taskProgressVO.setPk_filldept(headVO.getDepid()); // 经办部门
				taskProgressVO.setPk_filldept_v(headVO.getDepid_v()); // 经办部门多版本
				taskProgressVO.setPk_fillmaker(headVO.getBillmaker()); // 填报人
				taskProgressVO.setPk_group(headVO.getPk_group()); // 集团
				taskProgressVO.setPk_org(headVO.getPk_org()); // 组织
				taskProgressVO.setPk_org_v(headVO.getPk_org_v()); // 组织
				taskProgressVO.setPk_project(headVO.getCprojectid()); // 项目信息
				taskProgressVO.setBillPk(headVO.getPrimaryKey());// 单据主键
				// 项目明细
				TaskProgressDetailVO taskProgressDetailVO = new TaskProgressDetailVO();
				taskProgressDetailVO.setActu_start_date(headVO.getDbilldate()); // 实际开始时间
				taskProgressDetailVO.setActu_finish_date(headVO.getDbilldate()); // 实际结束时间

				taskProgressDetailVO.setEvolve_state(bodyVO.getVmemo()); // 进展说明
				taskProgressDetailVO.setMemo(""); // 备注
				taskProgressDetailVO.setPk_project(bodyVO.getCbprojectid()); // 项目
				taskProgressDetailVO.setPk_wbs(bodyVO.getVmemo()); // WBS任务

				// 自定义项10
				// 项目取表体数据
				// taskProgressDetailVO.setPrefinishpercent(new
				// UFDouble("545.00")); // 上期实际完成
				taskProgressDetailVO.setTtaskpercent(new UFDouble("100")); // 本期时间完成
				taskProgressVO.setTaskProgressDetailVO(taskProgressDetailVO);
				// InsertAndUptTaskBIll insertAndUptTaskBIll =
				// NCLocator.getInstance().lookup(InsertAndUptTaskBIll.class);

				// 执行插入
				insertTaskBillVO(taskProgressVO, null);

			}
		} catch (BusinessException e) {

			return false;
		}
		return true;
	}

	// 根据项目（竣工）验收报告 推送进度填报单
	public Boolean pushTaskProcessBycheck(ProjectCheckBillVO billVO) {
		try {
			ProjectCheckHeadVO headVO = (ProjectCheckHeadVO) billVO
					.getParentVO();
			// FeeBalanceBodyVO[] bodyVOArr = (FeeBalanceBodyVO[])
			// feeBalanceBillVOArr[0].getChildrenVO();
			// 表体添加
			// FeeBalanceBodyVO bodyVO = bodyVOArr[i];
			TaskProgressVO taskProgressVO = new TaskProgressVO();
			taskProgressVO.setBillmaker(headVO.getBillmaker());
			taskProgressVO.setBillmaketime(headVO.getBillmaketime()); // 制单日期
			taskProgressVO.setCreator(headVO.getCreator());
			taskProgressVO.setCreationtime(headVO.getCreationtime());
			taskProgressVO.setMemo(headVO.getMemo()); // 备注
			taskProgressVO.setPk_filldept(headVO.getPk_apply_dept()); // 经办部门
			taskProgressVO.setPk_filldept_v(headVO.getPk_apply_dept_v()); //
			taskProgressVO.setPk_fillmaker(headVO.getBillmaker()); // 填报人
			taskProgressVO.setPk_group(headVO.getPk_group()); // 集团
			taskProgressVO.setPk_org(headVO.getPk_org()); // 组织
			taskProgressVO.setPk_org_v(headVO.getPk_org_v()); // 组织
			taskProgressVO.setBillPk(headVO.getPrimaryKey());// 单据主键
			taskProgressVO.setPk_project(headVO.getPk_project()); // 项目取表体数据

			// 项目明细
			TaskProgressDetailVO taskProgressDetailVO = new TaskProgressDetailVO();
			taskProgressDetailVO.setActu_start_date(headVO.getBillmaketime()); // 实际开始时间
																				// --暂时以制单日期为准
			taskProgressDetailVO.setActu_finish_date(headVO.getBillmaketime()); // 实际结束时间
																				// --暂时以制单日期为准

			taskProgressDetailVO.setEvolve_state(""); // 进展说明
			taskProgressDetailVO.setMemo(""); // 备注
			taskProgressDetailVO.setPk_project(headVO.getPk_project()); // 项目
			taskProgressDetailVO.setPk_wbs(headVO.getPk_wbs()); // WBS任务 写死
																// 自定项10

			// taskProgressDetailVO.setPrefinishpercent(new UFDouble("545.00"));
			// // 上期实际完成，合同不写
			taskProgressDetailVO.setTtaskpercent(new UFDouble("100")); // 本期实际完成

			taskProgressVO.setTaskProgressDetailVO(taskProgressDetailVO);

			// InsertAndUptTaskBIll insertAndUptTaskBIll =
			// NCLocator.getInstance().lookup(InsertAndUptTaskBIll.class);

			// 执行插入
			insertTaskBillVO(taskProgressVO, null);

		} catch (BusinessException e) {

			return false;
		}
		return true;
	}

	// 根据开工单（项目建议书pm_projectproposal推送进度填报单
	public Boolean pushTaskProcessByProposal(ProjectProposalBillVO billVO) {
		try {
			ProjectProposalHeadVO headVO = (ProjectProposalHeadVO) billVO
					.getParentVO();
			// FeeBalanceBodyVO[] bodyVOArr = (FeeBalanceBodyVO[])
			// feeBalanceBillVOArr[0].getChildrenVO();
			// 表体添加
			// FeeBalanceBodyVO bodyVO = bodyVOArr[i];
			TaskProgressVO taskProgressVO = new TaskProgressVO();
			taskProgressVO.setBillmaker(headVO.getBillmaker());
			taskProgressVO.setBillmaketime(headVO.getBillmaketime()); // 制单日期
			taskProgressVO.setCreator(headVO.getCreator());
			taskProgressVO.setCreationtime(headVO.getCreationtime());
			taskProgressVO.setMemo(headVO.getMemo()); // 备注
			taskProgressVO.setPk_filldept(headVO.getDef1()); // 经办部门 自定义项1
			taskProgressVO.setPk_filldept_v(headVO.getDef1()); // 经办部门多版本
			taskProgressVO.setPk_fillmaker(headVO.getBillmaker()); // 填报人
			taskProgressVO.setPk_group(headVO.getPk_group()); // 集团
			taskProgressVO.setPk_org(headVO.getPk_org()); // 组织
			taskProgressVO.setPk_org_v(headVO.getPk_org_v()); // 组织
			taskProgressVO.setBillPk(headVO.getPrimaryKey());// 单据主键
			taskProgressVO.setPk_project(headVO.getPk_project()); // 项目取表体数据

			// 项目明细
			TaskProgressDetailVO taskProgressDetailVO = new TaskProgressDetailVO();
			taskProgressDetailVO.setActu_start_date(headVO.getBillmaketime()); // 实际开始时间
																				// --暂时以制单日期为准
			taskProgressDetailVO.setActu_finish_date(headVO.getBillmaketime()); // 实际结束时间
																				// --暂时以制单日期为准

			taskProgressDetailVO.setEvolve_state(""); // 进展说明
			taskProgressDetailVO.setMemo(""); // 备注
			taskProgressDetailVO.setPk_project(headVO.getPk_project()); // 项目
			taskProgressDetailVO.setPk_wbs(headVO.getDef10()); // WBS任务 写死 自定项10
			// taskProgressDetailVO.setPrefinishpercent(new UFDouble("545.00"));
			// // 上期实际完成，合同不写
			taskProgressDetailVO.setTtaskpercent(new UFDouble("100")); // 本期实际完成

			taskProgressVO.setTaskProgressDetailVO(taskProgressDetailVO);

			// InsertAndUptTaskBIll insertAndUptTaskBIll =
			// NCLocator.getInstance().lookup(InsertAndUptTaskBIll.class);

			// 执行插入
			insertTaskBillVO(taskProgressVO, null);

		} catch (BusinessException e) {

			return false;
		}
		return true;
	}

	/*// 根据清单发包合同推送
	public Boolean pushTaskProcessByContract(ContractBillVO billVO)
			throws BusinessException {
		try {
			ContrHeadVO headVO = (ContrHeadVO) billVO.getParentVO();
			ContrWorksVO[] contrWorksVOVOArr = (ContrWorksVO[]) billVO
					.getChildrenVO();
			// 循环表体添加
			for (int i = 0; i < contrWorksVOVOArr.length; i++) {

				ContrWorksVO bodyVO = contrWorksVOVOArr[i];

				TaskProgressVO taskProgressVO = new TaskProgressVO();
				taskProgressVO.setBillmaketime(headVO.getBillmaketime()); // 制单日期
				taskProgressVO.setCreator(headVO.getCreator());
				taskProgressVO.setCreationtime(headVO.getCreationtime());
				taskProgressVO.setMemo(headVO.getMemo()); // 备注
				taskProgressVO.setPk_filldept(headVO.getPk_promise_dept()); // 经办部门
				taskProgressVO.setPk_filldept_v(headVO.getPk_pro_dept_v()); // 经办部门多版本
				taskProgressVO.setPk_fillmaker(headVO.getBillmaker()); // 填报人
				taskProgressVO.setPk_group(headVO.getPk_group()); // 集团
				taskProgressVO.setPk_org(headVO.getPk_org()); // 组织
				taskProgressVO.setPk_org_v(headVO.getPk_org_v()); // 组织
				taskProgressVO.setBillPk(headVO.getPrimaryKey());// 单据主键
				taskProgressVO.setPk_project(bodyVO.getPk_project()); // 项目取表体数据

				// 项目明细
				TaskProgressDetailVO taskProgressDetailVO = new TaskProgressDetailVO();
				taskProgressDetailVO.setActu_start_date(headVO
						.getBillmaketime()); // 实际开始时间
				taskProgressDetailVO.setActu_finish_date(headVO
						.getBillmaketime()); // 实际结束时间

				taskProgressDetailVO.setEvolve_state(""); // 进展说明
				taskProgressDetailVO.setMemo(""); // 备注
				taskProgressDetailVO.setPk_project(headVO.getPk_project()); // 项目
				taskProgressDetailVO.setPk_wbs(bodyVO.getPk_wbs()); // WBS任务
				// taskProgressDetailVO.setPrefinishpercent(new
				// UFDouble("545.00")); // 上期实际完成
				taskProgressDetailVO.setTtaskpercent(new UFDouble("100")); // 本期时间完成

				taskProgressVO.setTaskProgressDetailVO(taskProgressDetailVO);
				//InsertAndUptTaskBIll insertAndUptTaskBIll = NCLocator.getInstance().lookup(InsertAndUptTaskBIll.class);

				// 执行插入
				insertTaskBillVO(taskProgressVO, null);
			}
		} catch (BusinessException e) {

			return false;
		}
		return true;
	}*/
	// InsertAndUptTaskBIll里的方法迁移到PIM
	IplatFormEntry ip = (IplatFormEntry) NCLocator.getInstance().lookup(
			IplatFormEntry.class);// 动作执行类

	public void insertTaskBillVO(TaskProgressVO taskProgressVO,
			List<TaskProgressDetailVO> taskProgressDetailVOArr)
			throws BusinessException {
		IShceduleFillInterface ipf = (IShceduleFillInterface) NCLocator
				.getInstance().lookup(IShceduleFillInterface.class);
		if (getTaskScheduleByBillType(taskProgressVO.getBillPk())) {
			return;
		} else {
			// 如果表体为空，所有数据从表头添加
			if (taskProgressDetailVOArr == null) {

				TaskProgressDetailVO taskProgressDetailVO = taskProgressVO
						.getTaskProgressDetailVO();

				TaskScheduleBillVO taskScheduleBillVO = new TaskScheduleBillVO();
				// ----------------------单据表头VO----------------------
				TaskScheduleHeadVO taskScheduleHeadVO = new TaskScheduleHeadVO();
				// 获取单据编号
				BillcodeGenerater gene = new BillcodeGenerater();
				String billno = gene.getBillCode("4D24",
						taskProgressVO.getPk_group(),
						taskProgressVO.getPk_org(), null, null);

				taskScheduleHeadVO.setBill_code(billno);// 单据编号
				// taskScheduleHeadVO.setAuditor(""); // 审批人
				// taskScheduleHeadVO.setAudittime(); // 审批日期
				// taskScheduleHeadVO.setBill_status(-1); // 单据状态 默认提交态
				taskScheduleHeadVO.setBill_type("4D24"); // bill_type
				taskScheduleHeadVO.setBillmaker(taskProgressVO.getBillmaker()); // 制单人
				taskScheduleHeadVO.setBillmaketime(taskProgressVO
						.getBillmaketime()); // 制单日期
				taskScheduleHeadVO.setCreator(taskProgressVO.getCreator() + ""); // 创建人
				taskScheduleHeadVO.setCreationtime(taskProgressVO
						.getCreationtime()); // 创建日期
				taskScheduleHeadVO
						.setFilltime(taskProgressVO.getBillmaketime()); // 填报日期，默认制单日期
				taskScheduleHeadVO.setMemo(taskProgressVO.getMemo()); // 备注
				taskScheduleHeadVO.setPk_filldept(taskProgressVO
						.getPk_filldept()); // 经办部门
				taskScheduleHeadVO.setPk_filldept_v(taskProgressVO
						.getPk_filldept_v()); // 经办部门多版本
				taskScheduleHeadVO.setPk_fillmaker(taskProgressVO
						.getPk_fillmaker()); // 填报人
				taskScheduleHeadVO.setPk_group(taskProgressVO.getPk_group()); // 集团
				taskScheduleHeadVO.setPk_org(taskProgressVO.getPk_org()); // 组织
				taskScheduleHeadVO.setPk_org_v(taskProgressVO.getPk_org_v()); // 组织
				taskScheduleHeadVO.setPk_project(taskProgressDetailVO
						.getPk_project()); // 项目pkOID取表体里面的object
				taskScheduleHeadVO.setPk_transitype("0001A21000000000PG0E"); // 交易类型
				taskScheduleHeadVO.setTransi_type("4D24-01"); // 交易类型编码
																// transi_type
				taskScheduleHeadVO.setHdef10(taskProgressVO.getBillPk());// 自定义项10存放单据pk
				taskScheduleHeadVO.setDr(0);
				taskScheduleHeadVO.setReport_fintsk_flag(new UFBoolean("N"));
				taskScheduleHeadVO.setReport_kms_flag(new UFBoolean("N"));
				// taskScheduleHeadVO.setAuditor(taskProgressVO.getBillmaker());
				// // 审批人
				// taskScheduleHeadVO.setAudittime(taskProgressVO.getBillmaketime());
				// // 审批日期
				taskScheduleHeadVO.setBill_status(3); // 单据状态 默认审批态
				// 写入表头
				taskScheduleBillVO.setParent(taskScheduleHeadVO);

				// ----------------------单据表体VO----------------------

				TaskScheduleBodyVO taskScheduleBodyVO = new TaskScheduleBodyVO();
				taskScheduleBodyVO.setPk_wbsstate(1); // WBS任务状态 进行中
				// 添加子表字段
				taskScheduleBodyVO.setActu_start_date(taskProgressDetailVO
						.getActu_start_date()); // 实际开始时间

				if ((new UFDouble("100")) == taskProgressDetailVO
						.getTtaskpercent()) {
					taskScheduleBodyVO.setActu_finish_date(taskProgressDetailVO
							.getActu_finish_date()); // 时间结束时间
					taskScheduleBodyVO.setPk_wbsstate(4); // WBS任务状态 进行中
				}

				taskScheduleBodyVO.setDr(0);
				// taskScheduleBodyVO.setEvolve_state(""); // 进展说明
				taskScheduleBodyVO.setMemo(taskProgressDetailVO.getMemo()); // 备注
				taskScheduleBodyVO.setPk_project(taskProgressDetailVO
						.getPk_project()); // 项目
				taskScheduleBodyVO.setPk_wbs(taskProgressDetailVO.getPk_wbs()); // WBS任务
				String totalMax = getMaxFinish(taskProgressVO.getPk_org(),
						taskProgressDetailVO.getPk_project(),
						taskProgressDetailVO.getPk_wbs(),
						taskScheduleBodyVO.getPk_taskschedule_b());// 最大完成
				System.out.println("totalMax==" + totalMax);
				taskScheduleBodyVO.setPre_wbsstate(1);
				if (!"".equals(totalMax)) {
					taskScheduleBodyVO.setPrefinishpercent(new UFDouble(
							totalMax)); // 上期实际完成
					if (new UFDouble(totalMax) == new UFDouble(100)) {
						taskScheduleBodyVO.setPre_wbsstate(4);
					}
				}

				taskScheduleBodyVO.setTaskpercent(taskProgressDetailVO
						.getTtaskpercent()); // 本期实际完成

				// ----------------------调用接口写入----------------------
				CircularlyAccessibleValueObject[] vos = new TaskScheduleBodyVO[] { taskScheduleBodyVO };
				taskScheduleBillVO.setChildrenVO(vos);
				// pk = taskScheduleHeadVO.getPrimaryKey();
				TaskScheduleBillVO[] taskScheduleBillVOArr = new TaskScheduleBillVO[] { taskScheduleBillVO };
				ipf.insertTaskScheduleBillVOS(taskScheduleBillVOArr);
				String pks = Schepk(taskProgressVO.getBillPk());
				IBillQueryService billQuery = NCLocator.getInstance().lookup(
						IBillQueryService.class);
				TaskScheduleBillVO ystzvo = billQuery.querySingleBillByPk(
						TaskScheduleBillVO.class, pks);
				System.out.println("ystzvo==" + ystzvo);
				if (ystzvo != null) {
					ip.processAction("APPROVE", "4D24", null, ystzvo, null,
							null);
				}

				/**
				 * IQueryScheme paramIQueryScheme = null;
				 * paramIQueryScheme.put("pk_taskschedule", pk);
				 * sd.queryByQueryScheme(paramIQueryScheme);
				 */

			} else {
				for (int i = 0; i < taskProgressDetailVOArr.size(); i++) {
					// 去掉重复的项目 判断条件 组织+项目OID 累积完成 修改为100%
					removeDuplicate(taskProgressDetailVOArr);

					// ----------------------循环添加进度填报单表头、表体信息----------------------
					TaskProgressDetailVO taskProgressDetailVO = taskProgressDetailVOArr
							.get(i);
					// ----------------------单据VO----------------------
					TaskScheduleBillVO taskScheduleBillVO = new TaskScheduleBillVO();
					// ----------------------单据表头VO----------------------
					TaskScheduleHeadVO taskScheduleHeadVO = new TaskScheduleHeadVO();

					// 获取单据编号
					BillcodeGenerater gene = new BillcodeGenerater();
					String billno = gene.getBillCode("4D24",
							taskProgressVO.getPk_group(),
							taskProgressVO.getPk_org(), null, null);
					taskScheduleHeadVO.setBill_code(billno);// 单据编号

					// taskScheduleHeadVO.setAuditor(""); // 审批人
					// taskScheduleHeadVO.setAudittime(); // 审批日期
					// taskScheduleHeadVO.setBill_status(-1); // 单据状态 默认提交态
					taskScheduleHeadVO.setBill_type("4D24"); // bill_type
					taskScheduleHeadVO.setBillmaker(taskProgressVO
							.getBillmaker()); // 制单人
					taskScheduleHeadVO.setBillmaketime(taskProgressVO
							.getBillmaketime()); // 制单日期
					taskScheduleHeadVO.setCreator(taskProgressVO.getCreator()
							+ ""); // 创建人
					taskScheduleHeadVO.setCreationtime(taskProgressVO
							.getCreationtime()); // 创建日期
					taskScheduleHeadVO.setFilltime(taskProgressVO
							.getBillmaketime()); // 填报日期，默认制单日期
					taskScheduleHeadVO.setMemo(taskProgressVO.getMemo()); // 备注
					taskScheduleHeadVO.setPk_filldept(taskProgressVO
							.getPk_filldept()); // 经办部门
					taskScheduleHeadVO.setPk_filldept_v(taskProgressVO
							.getPk_filldept_v()); // 经办部门多版本
					taskScheduleHeadVO.setPk_fillmaker(taskProgressVO
							.getPk_fillmaker()); // 填报人
					taskScheduleHeadVO
							.setPk_group(taskProgressVO.getPk_group()); // 集团
					taskScheduleHeadVO.setPk_org(taskProgressVO.getPk_org()); // 组织
					taskScheduleHeadVO
							.setPk_org_v(taskProgressVO.getPk_org_v()); // 组织
					taskScheduleHeadVO.setPk_project(taskProgressDetailVO
							.getPk_project()); // 项目pkOID取表体里面的object
					taskScheduleHeadVO.setPk_transitype("0001A21000000000PG0E"); // 交易类型
					taskScheduleHeadVO.setTransi_type("4D24-01"); // 交易类型编码
																	// transi_type
					taskScheduleHeadVO.setHdef10(taskProgressVO.getBillPk());// 自定义项10存放单据pk
					taskScheduleHeadVO.setDr(0);
					taskScheduleHeadVO
							.setAuditor(taskProgressVO.getBillmaker()); // 审批人
					taskScheduleHeadVO.setAudittime(taskProgressVO
							.getBillmaketime()); // 审批日期
					taskScheduleHeadVO.setBill_status(1); // 单据状态 默认审批态
					// 写入表头
					taskScheduleBillVO.setParent(taskScheduleHeadVO);

					// ----------------------单据表体VO----------------------
					TaskScheduleBodyVO taskScheduleBodyVO = new TaskScheduleBodyVO();
					// 添加子表字段
					taskScheduleBodyVO.setActu_start_date(taskProgressDetailVO
							.getActu_start_date()); // 实际开始时间
					if ((new UFDouble("100")) == taskProgressDetailVO
							.getTtaskpercent()) {
						taskScheduleBodyVO
								.setActu_finish_date(taskProgressDetailVO
										.getActu_finish_date()); // 时间结束时间
						taskScheduleBodyVO.setPk_wbsstate(4); // WBS任务状态 进行中
					}
					// taskScheduleBodyVO.setActu_finish_date(taskProgressDetailVO.getActu_finish_date());
					// // 时间结束时间
					taskScheduleBodyVO.setDr(0);
					taskScheduleBodyVO.setEvolve_state(""); // 进展说明
					taskScheduleBodyVO.setMemo(taskProgressDetailVO.getMemo()); // 备注
					taskScheduleBodyVO.setPk_project(taskProgressDetailVO
							.getPk_project()); // 项目
					taskScheduleBodyVO.setPk_wbs(taskProgressDetailVO
							.getPk_wbs()); // WBS任务
					taskScheduleBodyVO.setPk_wbsstate(0); // WBS任务状态
					taskScheduleBodyVO.setPrefinishpercent(taskProgressDetailVO
							.getPrefinishpercent()); // 上期实际完成
					taskScheduleBodyVO.setTaskpercent(taskProgressDetailVO
							.getTtaskpercent()); // 本期时间完成
					// ----------------------调用接口写入----------------------
					// CircularlyAccessibleValueObject[]
					// circularlyAccessibleValueObject = null;
					// circularlyAccessibleValueObject[0] = taskScheduleBodyVO;
					// taskScheduleBillVO.setChildrenVO(circularlyAccessibleValueObject);
					// TaskScheduleBillVO[] taskScheduleBillVOArr = new
					// TaskScheduleBillVO[] { taskScheduleBillVO };
					// ipf.insertTaskScheduleBillVOS(taskScheduleBillVOArr);

					CircularlyAccessibleValueObject[] vos = new TaskScheduleBodyVO[] { taskScheduleBodyVO };
					taskScheduleBillVO.setChildrenVO(vos);

					TaskScheduleBillVO[] taskScheduleBillVOArr = new TaskScheduleBillVO[] { taskScheduleBillVO };
					ipf.insertTaskScheduleBillVOS(taskScheduleBillVOArr);

				}
			}

		}

	}

	// 去除重复项目
	public List<TaskProgressDetailVO> removeDuplicate(
			List<TaskProgressDetailVO> oldList) {
		for (int i = 0; i < oldList.size() - 1; i++) {
			TaskProgressDetailVO taskProgressDetailVOFirst = oldList.get(i);
			String fristProject = taskProgressDetailVOFirst.getPk_project();
			for (int j = oldList.size() - 1; j > i; j--) {
				TaskProgressDetailVO taskProgressDetailVOSecond = oldList
						.get(j);
				String secondProject = taskProgressDetailVOSecond
						.getPk_project();
				// if (oldList.get(j).equals(oldList.get(i))) {
				if (fristProject.equals(secondProject)) {
					oldList.remove(j);
				}
			}
		}
		return oldList;
	}

	// 删除 进度单据
	public void deleteTaskBillVO(String pkTaskScheduleBill)
			throws BusinessException {
		// getDao.deleteByClause(TaskScheduleBillVO.class, "hdef10='" +
		// pkTaskScheduleBill + "' and dr = 0");
		IShceduleFillInterface ipf = (IShceduleFillInterface) NCLocator
				.getInstance().lookup(IShceduleFillInterface.class);

		String pks = Schepk(pkTaskScheduleBill);
		IBillQueryService billQuery = NCLocator.getInstance().lookup(
				IBillQueryService.class);
		TaskScheduleBillVO ystzvo = billQuery.querySingleBillByPk(
				TaskScheduleBillVO.class, pks);
		if (ystzvo != null) {
			ip.processAction("UNAPPROVE", "4D24", null, ystzvo, null, null);
			TaskScheduleBillVO ystznvo = billQuery.querySingleBillByPk(
					TaskScheduleBillVO.class, pks);
			if (ystznvo != null) {
				ipf.deleteTaskScheduleBillVOS(new TaskScheduleBillVO[] { ystznvo });
			}
			String sql = "select pk_taskschedule from pm_taskschedule where hdef10='"
					+ pkTaskScheduleBill + "' and dr = 0";
			List<Object[]> orderLs = getDao.query(sql);
			String parentsql = "update pm_taskschedule set dr = 1 where hdef10 = '"
					+ pkTaskScheduleBill + "'";
			getDao.executeUpdate(parentsql);
			if (orderLs != null && orderLs.size() > 0 && orderLs.get(0) != null
					&& orderLs.get(0)[0] != null) {
				for (int i = 0; i < orderLs.size(); i++) {
					String childsql = "update pm_taskschedule_b set dr = 1 where pk_taskschedule = '"
							+ orderLs.get(i)[0] + "'";
					getDao.executeUpdate(childsql);
				}
			}
		}

	}

	public Boolean getTaskScheduleByBillType(String billType)
			throws DAOException {
		System.out.println("hdef10==" + billType);
		String sql = "select * from pm_taskschedule where hdef10='" + billType
				+ "' and dr = 0";
		List<Object[]> orderLs = getDao.query(sql);
		if (orderLs.size() > 0 && orderLs.get(0) != null) {
			return true;
		} else {
			return false;
		}
	}

	public String getMaxFinish(String pk_org, String pk_project, String pk_wbs,
			String PK_TASKSCHEDULE_B) {
		String maxTotal = "";
		String sql = "SELECT MAX(JDMX.TASKPERCENT) AS MAXTOTAL FROM PM_TASKSCHEDULE_B JDMX LEFT JOIN PM_TASKSCHEDULE JD ON "
				+ "JDMX.PK_TASKSCHEDULE = JD.PK_TASKSCHEDULE WHERE JD.DR = 0 AND JDMX.DR = 0 AND JD.PK_ORG = '"
				+ pk_org
				+ "' AND JDMX.PK_PROJECT = '"
				+ pk_project
				+ "' AND "
				+ "JDMX.PK_WBS = '"
				+ pk_wbs
				+ "' AND PK_TASKSCHEDULE_B <> '"
				+ PK_TASKSCHEDULE_B + "'";
		System.out.println("sql==" + sql);
		try {
			List<Object[]> maxls = getDao.query(sql);

			if (maxls != null && maxls.size() > 0 && maxls.get(0)[0] != null) {
				maxTotal = maxls.get(0)[0] + "";
				System.out.println("maxTotal--" + maxTotal);
			}
		} catch (DAOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return maxTotal;
	}

	public String Schepk(String billPK) throws DAOException {
		String pk = "";
		String sql = "SELECT PK_TASKSCHEDULE FROM PM_TASKSCHEDULE WHERE HDEF10 = '"
				+ billPK + "' AND DR = 0";
		List<Object[]> ls = getDao.query(sql);
		if (ls != null && ls.size() > 0 && ls.get(0)[0] != null) {
			pk = ls.get(0)[0] + "";
		}
		return pk;

	}

	public void pushTaskProcessByFee(FeeBalanceBillVO billVO)
			throws BusinessException {

		FeeBalanceHeadVO headVO = (FeeBalanceHeadVO) billVO.getParentVO();
		FeeBalanceBodyVO[] bodyVOArr = (FeeBalanceBodyVO[]) billVO
				.getChildrenVO();
		// 循环表体添加
		for (int i = 0; i < bodyVOArr.length; i++) {
			FeeBalanceBodyVO bodyVO = bodyVOArr[i];
			// System.out.println("bodyVO==" + bodyVO);
			TaskProgressVO taskProgressVO = new TaskProgressVO();
			taskProgressVO.setBillmaker(headVO.getBillmaker());
			taskProgressVO.setBillmaketime(headVO.getBillmaketime()); // 制单日期
			taskProgressVO.setCreator(headVO.getCreator());
			taskProgressVO.setCreationtime(headVO.getCreationtime());
			taskProgressVO.setMemo(headVO.getMemo()); // 备注
			taskProgressVO.setPk_filldept(headVO.getPk_transact_dept()); // 经办部门
			taskProgressVO.setPk_filldept_v(headVO.getPk_transact_dept_v()); // 经办部门多版本
			taskProgressVO.setPk_fillmaker(headVO.getBillmaker()); // 填报人
			taskProgressVO.setPk_group(headVO.getPk_group()); // 集团
			taskProgressVO.setPk_org(headVO.getPk_org()); // 组织
			taskProgressVO.setPk_org_v(headVO.getPk_org_v()); // 组织
			taskProgressVO.setBillPk(headVO.getPrimaryKey());// 单据主键

			taskProgressVO.setPk_project(bodyVO.getPk_project()); // 项目取表体数据

			// 项目明细
			TaskProgressDetailVO taskProgressDetailVO = new TaskProgressDetailVO();
			taskProgressDetailVO
					.setActu_start_date(new UFDate(bodyVO.getDef4())); // 实际开始时间

			taskProgressDetailVO.setActu_finish_date(headVO.getBillmaketime()); // 实际结束时间
																				// --暂时以制单日期为准
			taskProgressDetailVO.setEvolve_state(bodyVO.getMemo()); // 进展说明
			taskProgressDetailVO.setMemo(""); // 备注
			taskProgressDetailVO.setPk_project(bodyVO.getPk_project()); // 项目
			taskProgressDetailVO.setPk_wbs(bodyVO.getPk_wbs()); // WBS任务 写死
			// taskProgressDetailVO.setPrefinishpercent(new UFDouble("545.00"));
			if ("4D83-Cxx-04".equals(headVO.getTransi_type())
					|| "4D83-Cxx-22".equals(headVO.getTransi_type())) {
				taskProgressDetailVO.setTtaskpercent(new UFDouble(bodyVO
						.getDef7() + ""));
			} else {
				// // 上期实际完成，合同不写
				if ("".equals(bodyVO.getMoney())
						|| "null".equals(bodyVO.getMoney())
						|| "~".equals(bodyVO.getMoney())
						|| bodyVO.getMoney() == null) {
					taskProgressDetailVO.setTtaskpercent(new UFDouble("100")); // 本期实际完成
				} else {
					taskProgressDetailVO.setTtaskpercent(new UFDouble(bodyVO
							.getMoney() + "")); // 本期实际完成
				}
			}
			taskProgressVO.setTaskProgressDetailVO(taskProgressDetailVO);

			// InsertAndUptTaskBIll insertAndUptTaskBIll = new
			// InsertAndUptTaskBIll();

			// 执行插入
			insertTaskBillVO(taskProgressVO, null);
		}
	}
	public HashMap<String, Object> getEparam() {
		HashMap<String, Object> eParam = new HashMap<String, Object>();
		eParam.put("notechecked", "notechecked");
		return eParam;
	}

	public IplatFormEntry getIplatFormEntry() {
		return NCLocator.getInstance().lookup(IplatFormEntry.class);
	}
}
