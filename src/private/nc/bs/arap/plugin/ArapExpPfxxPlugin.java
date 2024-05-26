package nc.bs.arap.plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.arap.bill.ArapBillPubUtil;
import nc.bs.arap.util.ArapVOUtils;
import nc.bs.arap.util.BillMoneyVUtils;
import nc.bs.arap.util.IArapBillTypeCons;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.pf.pub.PfDataCache;
import nc.bs.pfxx.ISwapContext;
import nc.bs.trade.business.HYPubBO;
import nc.itf.arap.initgatheringbill.IArapInitGatheringService;
import nc.itf.arap.initpayablebill.IArapInitPayableService;
import nc.itf.arap.initpaybill.IArapInitPaybillService;
import nc.itf.arap.initreceivable.IArapInitRecService;
import nc.itf.arap.pay.IArapPayBillQueryService;
import nc.itf.uap.pf.IPFConfig;
import nc.itf.uap.pf.IWorkflowMachine;
import nc.itf.uap.pf.IplatFormEntry;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.accperiod.AccountCalendar;
import nc.pubitf.arap.bill.IArapBillPubService;
import nc.util.fi.pub.SqlUtils;
import nc.vo.arap.basebill.BaseAggVO;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.arap.basebill.BaseItemVO;
import nc.vo.arap.gathering.AggGatheringBillVO;
import nc.vo.arap.pay.AggPayBillVO;
import nc.vo.arap.pay.PayBillItemVO;
import nc.vo.arap.pay.PayBillVO;
import nc.vo.arap.payable.AggPayableBillVO;
import nc.vo.arap.pub.BillEnumCollection;
import nc.vo.arap.pub.BillEnumCollection.ObjType;
import nc.vo.arap.receivable.AggReceivableBillVO;
import nc.vo.arap.utils.ArrayUtil;
import nc.vo.arap.utils.StringUtil;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.org.OrgVO;
import nc.vo.pfxx.auxiliary.AggxsysregisterVO;
import nc.vo.pfxx.util.PfxxPluginUtils;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.pubapp.pflow.PfUserObject;
import nc.vo.pubapp.util.NCPfServiceUtils;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * 
 * <p>
 * 在此处添加此类的描述信息
 * </p>
 * 
 * @author zhaoruic
 * @version Your Project V60
 */
@SuppressWarnings({ "unused", "unchecked", "rawtypes", "deprecation" })
public class ArapExpPfxxPlugin<T extends BaseAggVO> extends
		nc.bs.pfxx.plugin.AbstractPfxxPlugin {

	private PfUserObject[] userObjs;

	/**
	 * 将由XML转换过来的VO导入NC系统。业务插件实现此方法即可。<br>
	 * 请注意，业务方法的校验一定要充分
	 * 
	 * @param vo
	 *            转换后的vo数据，在NC系统中可能为ValueObject,SuperVO,AggregatedValueObject,
	 *            IExAggVO等。
	 * @param swapContext
	 *            各种交换参数，组织，接受方，发送方，帐套等等
	 * @param aggxsysvo
	 *            辅助信息vo
	 * @return
	 * @throws BusinessException
	 */
	protected Object processBill(Object vo, ISwapContext swapContext,
			AggxsysregisterVO aggxsysvo) throws BusinessException {

		BaseAggVO newBill = null;

		// 1.得到转换后的VO数据,取决于向导第一步注册的VO信息
		BaseAggVO bill = (BaseAggVO) vo;
		BaseBillVO head = setHeaderDefault(bill.getHeadVO());
		InvocationInfoProxy.getInstance().setUserId(head.getCreator());// 设置默认操作员主键
		if (head.getPk_billtype() == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2006pub_0", "02006pub-0316")/*
																			 * @res
																			 * "单据的单据类型编码字段不能为空，请输入值"
																			 */);
		}
		if (!head.getPk_billtype().equals(IArapBillTypeCons.F0)
				&& !head.getPk_billtype().equals(IArapBillTypeCons.F1)
				&& !head.getPk_billtype().equals(IArapBillTypeCons.F2)
				&& !head.getPk_billtype().equals(IArapBillTypeCons.F3)) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2006pub_0", "02006pub-0650")/*
																			 * @res
																			 * "单据的单据类型编码字段错误"
																			 */);
		}
		if (head.getPk_tradetype() == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2006pub_0", "02006pub-0649")/*
																			 * @res
																			 * "单据的交易类型编码字段不能为空，请输入值"
																			 */);
		}
		if (head.getPk_group() == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2006pub_0", "02006pub-0317")/*
																			 * @res
																			 * "单据的所属集团字段不能为空，请输入值"
																			 */);
		}
		if (head.getPk_org() == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2006pub_0", "02006pub-0318")/*
																			 * @res
																			 * "单据的财务组织字段不能为空，请输入值"
																			 */);
		}

		// 设置业务流程
		try {
			IPFConfig ipf = NCLocator.getInstance().lookup(IPFConfig.class);
			if (!StringUtil.isEmpty(head.getPk_billtype())
					&& !StringUtil.isEmpty(head.getPk_tradetype())) {
				if (head.getCreator() == null) {
					head.setCreator(InvocationInfoProxy.getInstance()
							.getUserId());
				}
				String pk_busitype = ipf.retBusitypeCanStart(
						head.getPk_billtype(), head.getPk_tradetype(),
						head.getPk_org(), head.getCreator());
				if (pk_busitype == null) {
					throw new BusinessException("busitype is null");
				}
				head.setPk_busitype(pk_busitype);
			}
		} catch (Exception e) {
			String msg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"2006pub_0", "02006pub-0127")/* @res "交易类型" */
					+ head.getPk_tradetype()
					+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"2006pub_0", "02006pub-0239")/*
														 * @res
														 * "没有找到相应的流程,请在[业务流定义]配置"
														 */
					+ head.getPk_tradetype()
					+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"2006pub_0", "02006pub-0240")/* @res "自制流程" */;
			throw new BusinessRuntimeException(msg);
		}

		// 如果是期初单据，则需要校验业务单元是否设置了业务期初期间，如果没有不允许导入。如果设置了，将日期设为期初期间前一天
		if (head.getIsinit().booleanValue()) {
			boolean isAr = ArapBillPubUtil.isARSysBilltype(head
					.getPk_billtype());
			UFDate billdate = ArapBillPubUtil.getArapCreateDate(isAr,
					head.getPk_org());
			if (null == billdate) {
				throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes()
						.getStrByID("2006pub_0", "02006pub-0007")/*
																 * @res
																 * "业务单元未设置业务期初期间，不允许维护期初数据"
																 */);
			}
			if (head.getBilldate().after(billdate)) {
				head.setBilldate(billdate);
				for (BaseItemVO item : (BaseItemVO[]) bill.getChildrenVO()) {
					item.setBilldate(billdate);
				}
			}

		}
		// XBX通过pk_paybill判断是否是修改-薪酬付款审批单（宏景），如果有则修改
		if (head.getPk_tradetype().equals("F3-Cxx-XCFKSPD")
				&& head.getPrimaryKey() != null) {
			IplatFormEntry action = (IplatFormEntry) NCLocator.getInstance()
					.lookup(IplatFormEntry.class.getName());
			// 获取原付款单VO数据
			BaseAggVO preVO = getPreVO(head.getPrimaryKey());
			this.translateCuspAccount(bill);
			preVO = getNewVOByUpd((AggPayBillVO) bill, (AggPayBillVO) preVO);
			setBodyDefault(preVO.getHeadVO(),
					(BaseItemVO[]) preVO.getChildrenVO());
			// this.translateCuspAccount(preVO);
			// this.translatePsndocAccount(preVO);
			// end
			BillMoneyVUtils.sumBodyToHead(preVO.getHeadVO(),
					(BaseItemVO[]) preVO.getChildrenVO());
			// 将修改的数据赋值到原VO，修改原VO数据
			Object executePM = action.processAction("SAVEBASE", bill
					.getHeadVO().getPk_billtype(), null, preVO,
					new PfUserObject(), null);
			AggregatedValueObject aggvo = (AggregatedValueObject) ArrayUtil
					.getFirstInArrays((Object[]) executePM);
//			aggvo = (AggPayBillVO) ArrayUtil
//					.getFirstInArrays((Object[]) NCPfServiceUtils.processBatch(
//							"START", bill.getHeadVO().getPk_billtype(),
//							new AggPayBillVO[] { (AggPayBillVO) aggvo },
//							getUserObj(), new WorkflownoteVO()));
			AggPayBillVO newVO = (AggPayBillVO) aggvo;
			Map<String, String> res = new HashMap<>();
			res.put("pk", newVO.getPrimaryKey());
			res.put("billNo", newVO.getParent().getAttributeValue("billno")
					.toString());
			return res;
		}
		setBodyDefault(head, (BaseItemVO[]) bill.getChildrenVO());
		// translateAccount(head,(BaseItemVO[])bill.getChildrenVO());
		// start 特殊处理付款单的收款银行账户 复制导入的代码处理

		this.translatePsndocAccount(bill);
		this.translateCuspAccount(bill);
		// end
		BillMoneyVUtils
				.sumBodyToHead(head, (BaseItemVO[]) bill.getChildrenVO());
		// 2.查询此单据是否已经被导入过
		String oldPk = PfxxPluginUtils.queryBillPKBeforeSaveOrUpdate(
				swapContext.getBilltype(), swapContext.getDocID());
		if (oldPk != null) {

			// 这个判断，好像平台已经过，如果单据已导入，且replace="N"，那么平台就会抛出异常，提示不可重复
			if (swapContext.getReplace().equalsIgnoreCase("N"))
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes()
						.getStrByID("2006pub_0", "02006pub-0319")/*
																 * @res
																 * "不允许重复导入单据，请检查是否是操作错误；如果想更新已导入单据，请把数据文件的replace标志设为‘Y’"
																 */);

			BaseAggVO preVO = null;
			if (head.getPk_billtype().equals(IArapBillTypeCons.F2)) {
				preVO = MDPersistenceService.lookupPersistenceQueryService()
						.queryBillOfVOByPK(AggGatheringBillVO.class, oldPk,
								false);
			} else if (head.getPk_billtype().equals(IArapBillTypeCons.F3)) {
				preVO = MDPersistenceService.lookupPersistenceQueryService()
						.queryBillOfVOByPK(AggPayBillVO.class, oldPk, false);
			} else if (head.getPk_billtype().equals(IArapBillTypeCons.F0)) {
				preVO = MDPersistenceService.lookupPersistenceQueryService()
						.queryBillOfVOByPK(AggReceivableBillVO.class, oldPk,
								false);
			} else if (head.getPk_billtype().equals(IArapBillTypeCons.F1)) {
				preVO = MDPersistenceService
						.lookupPersistenceQueryService()
						.queryBillOfVOByPK(AggPayableBillVO.class, oldPk, false);
			}

			if (preVO != null && preVO.getParentVO() != null) {

				if (((BaseBillVO) preVO.getParentVO()).getBillstatus() == null) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes().getStrByID("2006pub_0",
									"02006pub-0320")/* @res "单据状态不存在！" */);
				}
				if (((BaseBillVO) preVO.getParentVO()).getBillstatus() == BillEnumCollection.BillSatus.Audit.VALUE) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes().getStrByID("2006pub_0",
									"02006pub-0321")/* @res "单据已经审核，不允许重复导入单据。" */);
				}

				if (head.getIsinit().booleanValue()) {
					this.deleteInitBill(preVO);
				} else {
					NCLocator.getInstance().lookup(IArapBillPubService.class)
							.delete(preVO);
				}
			}
		}

		ArapExpPfxxValidater.getInstance().validate(bill);

		ArapVOUtils.validateVoCopyRed(bill);

		head.setBillno(null); // 清空billno，以便于重新生成，避免重复
		// 插入数据
		if (head.getIsinit().booleanValue()) {
			newBill = this.insertInitBill(bill);
		} else {
			newBill = this.insertBill(bill);
		}

		String pk = null;
		if (newBill != null) {
			pk = newBill.getParent().getPrimaryKey();
		}
		if (oldPk != null) {
			PfxxPluginUtils.deleteIDvsPKByDocPK(oldPk);
		}
		PfxxPluginUtils.addDocIDVsPKContrast(swapContext.getBilltype(),
				swapContext.getDocID(), pk);
		Map<String, String> res = new HashMap<>();
		res.put("pk", pk);
		res.put("billNo", null != newBill ? newBill.getParent()
				.getAttributeValue("billno").toString() : "");
		return res;
	}

	// 通过主键获取付款单VO
	private AggPayBillVO getPreVO(String pk_paybill) throws BusinessException {
		// TODO Auto-generated method stub
		IArapPayBillQueryService cs = (IArapPayBillQueryService) NCLocator
				.getInstance().lookup(IArapPayBillQueryService.class);
		AggPayBillVO[] wq = cs.queryBillsByWhereSQL(" PK_PAYBILL = '"
				+ pk_paybill + "'");
		if (wq == null || wq.length <= 0) {
			throw new BusinessException("[" + pk_paybill + "]对应薪酬付款单据不存在！");
		}
		AggPayBillVO aggvo = wq[0];
		PayBillVO hvo = (PayBillVO) aggvo.getParentVO();
		if (!"F3-Cxx-XCFKSPD".equals(hvo.getPk_tradetype())) {
			throw new BusinessException("[" + pk_paybill + "]对应单据不为薪酬付款单！");
		}
		if (hvo.getApprovestatus() != -1) {
			throw new BusinessException("付款单号[" + hvo.getBillno()
					+ "]单据状态不为自由态，不能修改！");
		}
		return wq[0];
	}

	private BaseAggVO getNewVOByUpd(AggPayBillVO newVO, AggPayBillVO preVO) {
		// TODO Auto-generated method stub
		PayBillVO newHVO = (PayBillVO) newVO.getParentVO();
		PayBillVO preHVO = (PayBillVO) preVO.getParentVO();
		// 表头
		preHVO.setLocal_money(newHVO.getLocal_money());
		preHVO.setMoney(newHVO.getLocal_money());
		preHVO.setBilldate(newHVO.getBilldate());
		preHVO.setSupplier(newHVO.getSupplier());
		preHVO.setRecaccount(newHVO.getRecaccount());
		preHVO.setLocal_money(newHVO.getLocal_money());
		preHVO.setCashitem(newHVO.getCashitem());
		preHVO.setAccessorynum(newHVO.getAccessorynum());
		preHVO.setPk_balatype(preHVO.getPk_balatype());
		preHVO.setScomment(newHVO.getScomment());
		preHVO.setDef76(newHVO.getDef76());
		preHVO.setDef65(newHVO.getDef65());
		preHVO.setDef2(newHVO.getDef2());
		preHVO.setStatus(VOStatus.UPDATED);
		// 表体
		PayBillItemVO[] newmxVOs = (PayBillItemVO[]) newVO.getChildrenVO();
		PayBillItemVO[] premxVOs = (PayBillItemVO[]) preVO.getChildrenVO();
		for (int i = 0; i < premxVOs.length; i++) {
			for (int j = 0; j < newmxVOs.length; j++) {
				if (Integer.parseInt(premxVOs[j].getRowno().toString()) == Integer
						.parseInt(newmxVOs[i].getRowno().toString())) {

					preHVO.setPayaccount(newmxVOs[i].getPayaccount());
					premxVOs[j].setPayaccount(newmxVOs[i].getPayaccount());
					premxVOs[j].setSupplier(newmxVOs[i].getSupplier());
					premxVOs[j].setRecaccount(newmxVOs[i].getRecaccount());
					premxVOs[j].setObjtype(newmxVOs[i].getObjtype());
					premxVOs[j].setScomment(newmxVOs[i].getScomment());
					premxVOs[j].setLocal_money_de(newmxVOs[i]
							.getLocal_money_de());
					premxVOs[j].setMoney_de(newmxVOs[i].getMoney_de());
					premxVOs[j].setMoney_bal(newmxVOs[i].getMoney_bal());
					premxVOs[j]
							.setGroupnotax_de(newmxVOs[i].getGroupnotax_de());
					premxVOs[j].setGroupdebit(newmxVOs[i].getGroupdebit());
					premxVOs[j].setProject(newmxVOs[i].getProject());
					premxVOs[j].setPk_subjcode(newmxVOs[i].getPk_subjcode());
					premxVOs[j].setPk_balatype(newmxVOs[i].getPk_balatype());
				}
			}
		}
		return preVO;
	}

	private BaseAggVO insertBill(BaseAggVO bill) throws BusinessException {
		BaseAggVO res = null;
		IplatFormEntry iplatFormEntry = NCLocator.getInstance().lookup(
				IplatFormEntry.class);
		HashMap<String, Object> eParam = new HashMap<String, Object>();
		eParam.put("notechecked", "notechecked");
		if (bill.getHeadVO().getPk_billtype().equals(IArapBillTypeCons.F0)) {
			res = (AggReceivableBillVO) ArrayUtil
					.getFirstInArrays((Object[]) NCPfServiceUtils
							.processBatch(
									"SAVEBASE",
									bill.getHeadVO().getPk_billtype(),
									new AggReceivableBillVO[] { (AggReceivableBillVO) bill },
									getUserObj(), new WorkflownoteVO()));
			res = (AggReceivableBillVO) ArrayUtil
					.getFirstInArrays((Object[]) NCPfServiceUtils
							.processBatch(
									"START",
									bill.getHeadVO().getPk_billtype(),
									new AggReceivableBillVO[] { (AggReceivableBillVO) res },
									getUserObj(), new WorkflownoteVO()));
			if ("1001A1100000002E7H8E".equals(bill.getHeadVO().getBillmaker())) {
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("SIGNAL", bill.getHeadVO()
								.getPk_tradetype(), res, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				// I8DJ提交单据，自动审批
				AggReceivableBillVO[] ysvos = (AggReceivableBillVO[]) iplatFormEntry
						.processAction("SIGNAL", bill.getHeadVO()
								.getPk_billtype(), worknoteVO, res, null,
								eParam);
				res = ysvos[0];
			}
		} else if (bill.getHeadVO().getPk_billtype()
				.equals(IArapBillTypeCons.F1)) {
			res = (AggPayableBillVO) ArrayUtil
					.getFirstInArrays((Object[]) NCPfServiceUtils.processBatch(
							"SAVEBASE", bill.getHeadVO().getPk_billtype(),
							new AggPayableBillVO[] { (AggPayableBillVO) bill },
							getUserObj(), new WorkflownoteVO()));
			res = (AggPayableBillVO) ArrayUtil
					.getFirstInArrays((Object[]) NCPfServiceUtils.processBatch(
							"START", bill.getHeadVO().getPk_billtype(),
							new AggPayableBillVO[] { (AggPayableBillVO) res },
							getUserObj(), new WorkflownoteVO()));
			if ("1001A1100000002E7H8E".equals(bill.getHeadVO().getBillmaker())) {
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("SIGNAL", bill.getHeadVO()
								.getPk_tradetype(), res, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				// I8DJ提交单据，自动审批
				AggPayableBillVO[] yfvos = (AggPayableBillVO[]) iplatFormEntry
						.processAction("SIGNAL", bill.getHeadVO()
								.getPk_billtype(), worknoteVO, res, null,
								eParam);
				res = yfvos[0];
			}
		} else if (bill.getHeadVO().getPk_billtype()
				.equals(IArapBillTypeCons.F2)) {
			// I8推送收款单默认收款银行账户
			OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
					bill.getHeadVO().getPk_org());
			if (orgVO.getDef11() != null) {
				String strwhere = " pk_org = '" + orgVO.getPk_org()
						+ "' and yhbm = '" + orgVO.getDef11() + "'";
				String recaccount = (String) new HYPubBO().findColValue(
						"v_i8_yhzh_zz", "pk_bankaccsub", strwhere);
				if (recaccount != null) {
					bill.getHeadVO().setRecaccount(strwhere);
				} else {
					throw new BusinessException("根据pk_org[" + orgVO.getPk_org()
							+ "],yhbm[" + orgVO.getDef11() + "]获取银行账号失败！");
				}
			}
			res = (AggGatheringBillVO) ArrayUtil
					.getFirstInArrays((Object[]) NCPfServiceUtils
							.processBatch(
									"SAVEBASE",
									bill.getHeadVO().getPk_billtype(),
									new AggGatheringBillVO[] { (AggGatheringBillVO) bill },
									getUserObj(), new WorkflownoteVO()));
			res = (AggGatheringBillVO) ArrayUtil
					.getFirstInArrays((Object[]) NCPfServiceUtils
							.processBatch(
									"START",
									bill.getHeadVO().getPk_billtype(),
									new AggGatheringBillVO[] { (AggGatheringBillVO) res },
									getUserObj(), new WorkflownoteVO()));
			if ("1001A1100000002E7H8E".equals(bill.getHeadVO().getBillmaker())
					|| "F2-Cxx-RD-YWSKD".equals(bill.getHeadVO()
							.getPk_tradetype())) {
				HashMap hmPfExParams = new HashMap();
				WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
						.getInstance().lookup(IWorkflowMachine.class))
						.checkWorkFlow("SIGNAL", bill.getHeadVO()
								.getPk_tradetype(), res, hmPfExParams);
				if (worknoteVO != null) {
					worknoteVO.setChecknote("批准");
					worknoteVO.setApproveresult("Y");
				}
				// 提交单据，自动审批
				Object[] skvos = (Object[]) iplatFormEntry.processAction(
						"SIGNAL", bill.getHeadVO().getPk_billtype(),
						worknoteVO, res, null, eParam);
				res = (BaseAggVO) skvos[0];
			}
		} else if (bill.getHeadVO().getPk_billtype()
				.equals(IArapBillTypeCons.F3)) {
			res = (AggPayBillVO) ArrayUtil
					.getFirstInArrays((Object[]) NCPfServiceUtils.processBatch(
							"SAVEBASE", bill.getHeadVO().getPk_billtype(),
							new AggPayBillVO[] { (AggPayBillVO) bill },
							getUserObj(), new WorkflownoteVO()));
			// XBX 2023-11-10 F3-Cxx-SFFKSPD 税费付款审批单 不提交
			if (!"F3-Cxx-SFFKSPD".equals(bill.getHeadVO().getPk_tradetype())
					&& !"F3-Cxx-XCFKSPD".equals(bill.getHeadVO()
							.getPk_tradetype())) {
				res = (AggPayBillVO) ArrayUtil
						.getFirstInArrays((Object[]) NCPfServiceUtils
								.processBatch(
										"START",
										bill.getHeadVO().getPk_billtype(),
										new AggPayBillVO[] { (AggPayBillVO) res },
										getUserObj(), new WorkflownoteVO()));
				if ("1001A1100000002E7H8E".equals(bill.getHeadVO()
						.getBillmaker())
						|| "F2-Cxx-RD-YWSKD".equals(bill.getHeadVO()
								.getPk_tradetype())) {
					HashMap hmPfExParams = new HashMap();
					WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
							.getInstance().lookup(IWorkflowMachine.class))
							.checkWorkFlow("SIGNAL", bill.getHeadVO()
									.getPk_tradetype(), res, hmPfExParams);
					if (worknoteVO != null) {
						worknoteVO.setChecknote("批准");
						worknoteVO.setApproveresult("Y");
					}
					// I8DJ提交单据，自动审批
					AggPayBillVO[] fkvos = (AggPayBillVO[]) iplatFormEntry
							.processAction("SIGNAL", bill.getHeadVO()
									.getPk_billtype(), worknoteVO, res, null,
									eParam);
					res = fkvos[0];
				}
			}
		}
		return res;
	}

	/**
	 * 银行账户补充翻译 1.往来对象为业务员情况
	 * 
	 * @param childrenVO
	 * @author cyz 2021年5月10日
	 */
	private void translatePsndocAccount(BaseAggVO bill) {
		BaseBillVO head = bill.getHeadVO();
		// 只处理付款单的收款银行账户
		String pk_billtype = head.getPk_billtype();
		if (StringUtil.isEmpty(pk_billtype)
				|| (!IArapBillTypeCons.F3.equals(pk_billtype))) {
			return;
		}
		BaseItemVO[] childrenVO = (BaseItemVO[]) bill.getChildrenVO();
		if (childrenVO == null || childrenVO.length == 0) {
			return;
		}
		Set<String> payAccSet = new HashSet<String>();
		Set<String> recAccSet = new HashSet<String>();

		try {
			// 付款银行账户
			if (IArapBillTypeCons.F0.equals(head.getPk_billtype())
					|| IArapBillTypeCons.F2.equals(head.getPk_billtype())) {
				for (BaseItemVO itemVO : childrenVO) {
					// 往来对象-业务员
					if (!ObjType.PERSON.VALUE.equals(itemVO.getObjtype())) {
						continue;
					}
					String payaccountB = itemVO.getPayaccount();
					if (payaccountB != null && payaccountB.length() > 0) {
						String[] objs = payaccountB.split("\\\\");
						payAccSet.add(objs[0]);
					}
				}
				List<Object[]> result = this.getAccountOfPsnbankacc(payAccSet);
				// 根据key匹配结果
				Map<String, String> nameMap = new HashMap<String, String>();
				Map<String, String> numMap = new HashMap<String, String>();
				if (result != null && result.size() > 0) {
					for (Object[] r : result) {
						numMap.put((String) r[0] + (String) r[2]
								+ (String) r[3], (String) r[4]);
						nameMap.put((String) r[1] + (String) r[2]
								+ (String) r[3], (String) r[4]);
					}
				}
				for (BaseItemVO itemVO : childrenVO) {
					String payaccount = itemVO.getPayaccount();
					String pk_psndoc = itemVO.getPk_psndoc();
					String pk_currtype = itemVO.getPk_currtype();
					if (!StringUtil.isEmpty(payaccount)) {
						String key = payaccount + pk_psndoc + pk_currtype;
						if (nameMap.containsKey(key)) {
							itemVO.setPayaccount(nameMap.get(key));
						} else if (numMap.containsKey(key)) {
							itemVO.setPayaccount(numMap.get(key));
						}
					}
				}
			}

			// 收款银行账户
			if (IArapBillTypeCons.F1.equals(head.getPk_billtype())
					|| IArapBillTypeCons.F3.equals(head.getPk_billtype())) {
				for (BaseItemVO itemVO : childrenVO) {
					// 往来对象-业务员
					if (!ObjType.PERSON.VALUE.equals(itemVO.getObjtype())) {
						continue;
					}
					String recaccountB = itemVO.getRecaccount();
					if (recaccountB != null && recaccountB.length() > 0) {
						String[] objs = recaccountB.split("\\\\");
						recAccSet.add(objs[0]);
					}
				}
				List<Object[]> result = this.getAccountOfPsnbankacc(recAccSet);
				// 根据key匹配结果
				Map<String, String> nameMap = new HashMap<String, String>();
				Map<String, String> numMap = new HashMap<String, String>();
				if (result != null && result.size() > 0) {
					for (Object[] r : result) {
						numMap.put((String) r[0] + (String) r[2]
								+ (String) r[3], (String) r[4]);
						nameMap.put((String) r[1] + (String) r[2]
								+ (String) r[3], (String) r[4]);
					}
				}
				for (BaseItemVO itemVO : childrenVO) {
					String recAccount = itemVO.getRecaccount();
					String pk_psndoc = itemVO.getPk_psndoc();
					String pk_currtype = itemVO.getPk_currtype();
					Integer objtype = itemVO.getObjtype();
					if (!StringUtil.isEmpty(recAccount)) {
						if (objtype.equals(ObjType.PERSON.VALUE)) {
							String key = recAccount + pk_psndoc + pk_currtype;
							if (nameMap.containsKey(key)) {
								itemVO.setRecaccount(nameMap.get(key));
							} else if (numMap.containsKey(key)) {
								itemVO.setRecaccount(numMap.get(key));
							} else {
								throw new BusinessException("传递收款银行账户"
										+ recAccount
										+ "在系统中不存在或不是业务员下的银行账户无法翻译,请检查传递的单据数据");
							}
						}
					}
				}
			}

		} catch (BusinessException e) {
			throw new BusinessRuntimeException(e.getMessage());
		}
	}

	private void translateCuspAccount(BaseAggVO bill) throws BusinessException {
		// 只处理付款单的收款银行账户
		BaseBillVO head = bill.getHeadVO();
		String pk_billtype = head.getPk_billtype();
		if (StringUtil.isEmpty(pk_billtype)
				|| (!IArapBillTypeCons.F3.equals(pk_billtype))) {
			return;
		}
		BaseItemVO[] childrenVO = (BaseItemVO[]) bill.getChildrenVO();
		Set<String> payaccountSet06 = new HashSet<String>();
		Set<String> payaccountSet08 = new HashSet<String>();
		Set<String> recaccountSet06 = new HashSet<String>();
		Set<String> recaccountSet08 = new HashSet<String>();

		for (BaseItemVO itemVO : childrenVO) {
			String payaccountB = itemVO.getPayaccount();
			String recaccountB = itemVO.getRecaccount();
			if ((!StringUtil.isEmpty(payaccountB))
					|| (!StringUtil.isEmpty(recaccountB))) {
				Integer objtype = itemVO.getObjtype();
				if (pk_billtype != null) {
					if (pk_billtype.equals(IArapBillTypeCons.F0)
							|| pk_billtype.equals(IArapBillTypeCons.F2)) {
						if (objtype.equals(ObjType.CUSTOMER.VALUE)) {
							if (payaccountB != null && payaccountB.length() > 0) {
								String[] objs = payaccountB.split("\\\\");
								payaccountSet06.add(objs[0]);
							}
						} else if (objtype.equals(ObjType.SUPPLIER.VALUE)) {
							if (payaccountB != null && payaccountB.length() > 0) {
								String[] objs = payaccountB.split("\\\\");
								payaccountSet08.add(objs[0]);
							}
						}
					} else {
						if (objtype.equals(ObjType.SUPPLIER.VALUE)) {
							if (recaccountB != null && recaccountB.length() > 0) {
								String[] objs = recaccountB.split("\\\\");
								recaccountSet08.add(objs[0]);
							}
						} else if (objtype.equals(ObjType.CUSTOMER.VALUE)) {
							if (recaccountB != null && recaccountB.length() > 0) {
								String[] objs = recaccountB.split("\\\\");
								recaccountSet06.add(objs[0]);
							}
						}
					}
				}

			}
		}
		if (pk_billtype != null
				&& (pk_billtype.equals(IArapBillTypeCons.F0) || pk_billtype
						.equals(IArapBillTypeCons.F2))) {
			List<Object[]> result06 = null;
			List<Object[]> result08 = null;
			if (payaccountSet06.size() > 0) {
				result06 = getAccount("1", payaccountSet06);// 0=个人，1=客户，2=公司，3=供应商，
			}
			if (payaccountSet08.size() > 0) {
				result08 = getAccount("3", payaccountSet08);// 0=个人，1=客户，2=公司，3=供应商，
			}
			Map<String, String> mapname06 = new HashMap<String, String>();
			Map<String, String> mapnum06 = new HashMap<String, String>();
			if (result06 != null && result06.size() > 0) {
				for (Object[] r : result06) {
					mapname06.put(
							(String) r[1] + (String) r[2] + (String) r[3],
							(String) r[4]);
					mapnum06.put((String) r[0] + (String) r[2] + (String) r[3],
							(String) r[4]);
				}
			}
			Map<String, String> mapname08 = new HashMap<String, String>();
			Map<String, String> mapnum08 = new HashMap<String, String>();
			if (result08 != null && result08.size() > 0) {
				for (Object[] r : result08) {
					mapname08.put(
							(String) r[1] + (String) r[2] + (String) r[3],
							(String) r[4]);
					mapnum08.put((String) r[0] + (String) r[2] + (String) r[3],
							(String) r[4]);
				}
			}
			for (BaseItemVO itemVO : childrenVO) {
				String payaccount = itemVO.getPayaccount();
				String customer = itemVO.getCustomer();
				String supplier = itemVO.getSupplier();
				String pk_currtype = itemVO.getPk_currtype();
				Integer objtype = itemVO.getObjtype();
				if (!StringUtil.isEmpty(payaccount)) {
					if (objtype.equals(ObjType.SUPPLIER.VALUE)) {
						String key = payaccount + supplier + pk_currtype;
						if (mapname08.containsKey(key)) {
							itemVO.setPayaccount(mapname08.get(key));
						} else if (mapnum08.containsKey(key)) {
							itemVO.setPayaccount(mapnum08.get(key));
						}
					} else if (objtype.equals(ObjType.CUSTOMER.VALUE)) {
						String key = payaccount + customer + pk_currtype;
						if (mapname06.containsKey(key)) {
							itemVO.setPayaccount(mapname06.get(key));
						} else if (mapnum06.containsKey(key)) {
							itemVO.setPayaccount(mapnum06.get(key));
						}
						// else {
						// throw new
						// BusinessException("银行账户编码或名称"+payaccount+"在系统中不存在或存在重复无法翻译,请检查传递的单据数据");
						// }
					}
				}
			}
		} else if (pk_billtype != null
				&& (pk_billtype.equals(IArapBillTypeCons.F1) || pk_billtype
						.equals(IArapBillTypeCons.F3))) {
			List<Object[]> result06 = null;
			List<Object[]> result08 = null;
			if (recaccountSet06.size() > 0) {
				result06 = getAccount("1", recaccountSet06);
			}
			if (recaccountSet08.size() > 0) {
				result08 = getAccount("3", recaccountSet08);
			}
			Map<String, String> mapname06 = new HashMap<String, String>();
			Map<String, String> mapnum06 = new HashMap<String, String>();
			if (result06 != null && result06.size() > 0) {
				for (Object[] r : result06) {
					mapname06.put(
							(String) r[1] + (String) r[2] + (String) r[3],
							(String) r[4]);
					mapnum06.put((String) r[0] + (String) r[2] + (String) r[3],
							(String) r[4]);
				}
			}
			Map<String, String> mapname08 = new HashMap<String, String>();
			Map<String, String> mapnum08 = new HashMap<String, String>();
			if (result08 != null && result08.size() > 0) {
				for (Object[] r : result08) {
					mapname08.put(
							(String) r[1] + (String) r[2] + (String) r[3],
							(String) r[4]);
					mapnum08.put((String) r[0] + (String) r[2] + (String) r[3],
							(String) r[4]);
				}
			}
			for (BaseItemVO itemVO : childrenVO) {
				String recaccount = itemVO.getRecaccount();
				String customer = itemVO.getCustomer();
				String supplier = itemVO.getSupplier();
				String pk_currtype = itemVO.getPk_currtype();
				Integer objtype = itemVO.getObjtype();
				if (!StringUtil.isEmpty(recaccount)) {
					if (objtype.equals(ObjType.CUSTOMER.VALUE)) {
						String key = recaccount + customer + pk_currtype;
						if (mapname06.containsKey(key)) {
							itemVO.setRecaccount(mapname06.get(key));
						} else if (mapnum06.containsKey(key)) {
							itemVO.setRecaccount(mapnum06.get(key));
						}
					} else if (objtype.equals(ObjType.SUPPLIER.VALUE)) {
						String key = recaccount + supplier + pk_currtype;
						if (mapname08.containsKey(key)) {
							itemVO.setRecaccount(mapname08.get(key));
						} else if (mapnum08.containsKey(key)) {
							itemVO.setRecaccount(mapnum08.get(key));
						} else {
							throw new BusinessException("传递收款银行账户" + recaccount
									+ "在系统中不存在或不是供应商下的银行账户无法翻译,请检查传递的单据数据");
						}
					}
				}
			}
		}
	}

	private BaseAggVO insertInitBill(BaseAggVO bill) throws BusinessException {
		BaseAggVO res = null;
		IplatFormEntry iplatFormEntry = NCLocator.getInstance().lookup(
				IplatFormEntry.class);
		HashMap<String, Object> eParam = new HashMap<String, Object>();
		eParam.put("notechecked", "notechecked");
		if (bill.getHeadVO().getPk_billtype().equals(IArapBillTypeCons.F0)) {
			res = NCLocator.getInstance().lookup(IArapInitRecService.class)
					.save((AggReceivableBillVO) bill);
		} else if (bill.getHeadVO().getPk_billtype()
				.equals(IArapBillTypeCons.F1)) {
			res = NCLocator.getInstance().lookup(IArapInitPayableService.class)
					.save((AggPayableBillVO) bill);
		} else if (bill.getHeadVO().getPk_billtype()
				.equals(IArapBillTypeCons.F2)) {
			res = NCLocator.getInstance()
					.lookup(IArapInitGatheringService.class)
					.save((AggGatheringBillVO) bill);
		} else if (bill.getHeadVO().getPk_billtype()
				.equals(IArapBillTypeCons.F3)) {
			res = NCLocator.getInstance().lookup(IArapInitPaybillService.class)
					.save((AggPayBillVO) bill);
			// XBX 2023-11-10 F3-Cxx-SFFKSPD 税费付款审批单 不提交
			if (!"F3-Cxx-SFFKSPD".equals(bill.getHeadVO().getPk_tradetype())
					&& !"F3-Cxx-XCFKSPD".equals(bill.getHeadVO()
							.getPk_tradetype())) {
				res = (AggPayBillVO) ArrayUtil
						.getFirstInArrays((Object[]) NCPfServiceUtils
								.processBatch(
										"START",
										bill.getHeadVO().getPk_billtype(),
										new AggPayBillVO[] { (AggPayBillVO) res },
										getUserObj(), new WorkflownoteVO()));
				if ("1001A1100000002E7H8E".equals(bill.getHeadVO()
						.getBillmaker())) {
					HashMap hmPfExParams = new HashMap();
					WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
							.getInstance().lookup(IWorkflowMachine.class))
							.checkWorkFlow("SIGNAL", bill.getHeadVO()
									.getPk_tradetype(), res, hmPfExParams);
					if (worknoteVO != null) {
						worknoteVO.setChecknote("批准");
						worknoteVO.setApproveresult("Y");
					}
					// I8DJ提交单据，自动审批
					AggPayBillVO[] fkvos = (AggPayBillVO[]) iplatFormEntry
							.processAction("SIGNAL", bill.getHeadVO()
									.getPk_billtype(), worknoteVO, res, null,
									eParam);
					res = fkvos[0];
				}
			}
		}

		return res;
	}

	public PfUserObject[] getUserObj() {
		if (userObjs == null) {
			userObjs = new PfUserObject[] { new PfUserObject() };
		}
		return userObjs;
	}

	private void deleteInitBill(BaseAggVO bill) throws BusinessException {
		if (bill.getHeadVO().getPk_billtype().equals(IArapBillTypeCons.F0)) {
			NCLocator.getInstance().lookup(IArapInitRecService.class)
					.delete((AggReceivableBillVO) bill);
		} else if (bill.getHeadVO().getPk_billtype()
				.equals(IArapBillTypeCons.F1)) {
			NCLocator.getInstance().lookup(IArapInitPayableService.class)
					.delete((AggPayableBillVO) bill);
		} else if (bill.getHeadVO().getPk_billtype()
				.equals(IArapBillTypeCons.F2)) {
			NCLocator.getInstance().lookup(IArapInitGatheringService.class)
					.delete((AggGatheringBillVO) bill);
		} else if (bill.getHeadVO().getPk_billtype()
				.equals(IArapBillTypeCons.F3)) {
			NCLocator.getInstance().lookup(IArapInitPaybillService.class)
					.delete((AggPayBillVO) bill);
		}
	}

	/**
	 * 设置表头默认信息
	 * 
	 * @param headerVo
	 * @return
	 * @throws BusinessException
	 */
	private BaseBillVO setHeaderDefault(BaseBillVO header)
			throws BusinessException {
		Integer ZERO = Integer.valueOf(0);
		/* 单据状态为未审核 */
		header.setBillstatus(BillEnumCollection.BillSatus.Save.VALUE);
		header.setEffectstatus(BillEnumCollection.InureSign.NOINURE.VALUE);
		header.setDr(ZERO);
		// 来源系统是外部交换平台
		header.setSrc_syscode(BillEnumCollection.FromSystem.WBJHPT.VALUE);
		header.setCreationtime(new UFDateTime());
		header.setCoordflag(null);

		// 单据所属系统 0=应收系统; 1=应付系统;
		if (header.getPk_billtype().equals(IArapBillTypeCons.F0)
				|| header.getPk_billtype().equals(IArapBillTypeCons.F2)) {
			header.setSyscode(BillEnumCollection.FromSystem.AR.VALUE);
		} else if (header.getPk_billtype().equals(IArapBillTypeCons.F1)
				|| header.getPk_billtype().equals(IArapBillTypeCons.F3)) {
			header.setSyscode(BillEnumCollection.FromSystem.AP.VALUE);
		}
		if (header.getPk_tradetype() == null
				|| header.getPk_tradetype().length() == 0) {
			throw new BusinessException("单据的交易类型pk_tradetype字段编码或名称不能为空，请输入值");
		}
		// 设置会计年和会计期间。若根据日期查不到会计期间，则捕获异常，不作处理
		try {
			AccountCalendar ac = AccountCalendar.getInstanceByPk_org(header
					.getPk_org());
			ac.setDate(header.getBilldate());
			header.setBillyear(ac.getYearVO().getPeriodyear());
			header.setBillperiod(ac.getMonthVO().getAccperiodmth());
		} catch (BusinessException ex) {
		}

		// 设置交易类型pk
		header.setPk_tradetypeid(PfDataCache.getBillTypeInfo(
				header.getPk_group(), header.getPk_tradetype())
				.getPk_billtypeid());
		return header;
	}

	void translateAccount(BaseBillVO head, BaseItemVO[] items)
			throws BusinessException {
		Set<String> payaccountSet06 = new HashSet<String>();
		Set<String> payaccountSet08 = new HashSet<String>();
		Set<String> recaccountSet06 = new HashSet<String>();
		Set<String> recaccountSet08 = new HashSet<String>();
		String pk_billtype = head.getPk_billtype();
		for (BaseItemVO itemVO : items) {
			String payaccountB = itemVO.getPayaccount();
			String recaccountB = itemVO.getRecaccount();
			if ((!StringUtil.isEmpty(payaccountB))
					|| (!StringUtil.isEmpty(recaccountB))) {
				Integer objtype = itemVO.getObjtype();
				if (pk_billtype != null) {
					if (pk_billtype.equals(IArapBillTypeCons.F3)) {
						if (!objtype.equals(ObjType.CUSTOMER.VALUE)) {
							if (recaccountB != null && recaccountB.length() > 0) {
								String[] objs = recaccountB.split("\\\\");
								recaccountSet08.add(objs[0]);
							}
						} else {
							if (recaccountB != null && recaccountB.length() > 0) {
								String[] objs = recaccountB.split("\\\\");
								recaccountSet06.add(objs[0]);
							}
						}
					}
				}

			}
		}
		if (pk_billtype.equals(IArapBillTypeCons.F3)) {
			List<Object[]> result06 = null;
			List<Object[]> result08 = null;
			if (recaccountSet06.size() > 0) {
				result06 = getAccount("1", recaccountSet06);
			}
			if (recaccountSet08.size() > 0) {
				result08 = getAccount("3", recaccountSet08);
			}
			Map<String, String> mapname06 = new HashMap<String, String>();
			Map<String, String> mapnum06 = new HashMap<String, String>();
			if (result06 != null && result06.size() > 0) {
				for (Object[] r : result06) {
					mapname06.put(
							(String) r[1] + (String) r[2] + (String) r[3],
							(String) r[4]);
					mapnum06.put((String) r[0] + (String) r[2] + (String) r[3],
							(String) r[4]);
				}
			}
			Map<String, String> mapname08 = new HashMap<String, String>();
			Map<String, String> mapnum08 = new HashMap<String, String>();
			if (result08 != null && result08.size() > 0) {
				for (Object[] r : result08) {
					mapname08.put(
							(String) r[1] + (String) r[2] + (String) r[3],
							(String) r[4]);
					mapnum08.put((String) r[0] + (String) r[2] + (String) r[3],
							(String) r[4]);
				}
			}
			for (BaseItemVO itemVO : items) {
				String recaccount = itemVO.getRecaccount();
				String customer = itemVO.getCustomer();
				String supplier = itemVO.getSupplier();
				String pk_currtype = itemVO.getPk_currtype();
				Integer objtype = itemVO.getObjtype();
				if ((!StringUtil.isEmpty(recaccount))) {
					if (!objtype.equals(ObjType.CUSTOMER.VALUE)) {
						String key = recaccount + supplier + pk_currtype;
						if (mapname08.containsKey(key)) {
							itemVO.setRecaccount(mapname08.get(key));
						} else if (mapnum08.containsKey(key)) {
							itemVO.setRecaccount(mapnum08.get(key));
						} else {
							throw new BusinessRuntimeException(
									"收款银行账户编码或名称存在重复或者不存在" + recaccount
											+ "无法翻译，请检查传递的单据数据");
						}
					} else {
						String key = recaccount + customer + pk_currtype;
						if (mapname06.containsKey(key)) {
							itemVO.setRecaccount(mapname06.get(key));
						} else if (mapnum06.containsKey(key)) {
							itemVO.setRecaccount(mapnum06.get(key));
						}
					}
				}
			}
		}
	}

	private List<Object[]> getAccount(String accclass, Set<String> accounts)
			throws BusinessException {
		StringBuffer sql = new StringBuffer();
		sql.append("select distinct accnum,"
				+ " accname,"
				+ " pk_cust,"
				+ " pk_currtype,"
				+ " pk_bankaccsub"
				+ " from (SELECT bd_bankaccsub.accnum,"
				+ " bd_bankaccsub.accname,"
				+ " pk_cust,"
				+ " enablestate,"
				+ " pk_currtype,"
				+ " bd_custbank.pk_bankaccbas AS pk_bankaccbas,"
				+ " bd_custbank.pk_bankaccsub AS pk_bankaccsub,"
				+ " bd_custbank.accclass"
				+ " FROM bd_bankaccbas, bd_bankaccsub, bd_custbank"
				+ " WHERE bd_bankaccbas.pk_bankaccbas = bd_bankaccsub.pk_bankaccbas"
				+ " AND bd_bankaccsub.pk_bankaccsub = bd_custbank.pk_bankaccsub"
				+ " AND bd_bankaccsub.pk_bankaccbas = bd_custbank.pk_bankaccbas"
				+ " AND bd_custbank.pk_bankaccsub != '~') bd_psnbankacctmp");
		sql.append(" where accclass ='" + accclass + "'");
		sql.append(" and enablestate = 2");
		sql.append(" and ("
				+ SqlUtils.getInStr("accnum", accounts.toArray(new String[0]),
						true)
				+ " or "
				+ SqlUtils.getInStr("accname", accounts.toArray(new String[0]),
						true) + ")");
		List<Object[]> result = (List<Object[]>) new BaseDAO().executeQuery(
				sql.toString(), new ArrayListProcessor());
		return result;
	}

	void setBodyDefault(BaseBillVO head, BaseItemVO[] items)
			throws BusinessException {
		int len = items == null ? 0 : items.length;

		for (int i = 0; i < len; i++) {
			setBodyDefault(head, items[i]);
			if (items[i].getObjtype() == null) {
				throw new BusinessException("单据的表体行的往来对象objtype不能为空，请输入值");
			}
		}
	}

	void setBodyDefault(BaseBillVO head, BaseItemVO item)
			throws BusinessException {
		/* 复制单位 */
		item.setPk_org(head.getPk_org());
		item.setDr(Integer.valueOf(0));
		item.setPk_tradetypeid(PfDataCache.getBillTypeInfo(head.getPk_group(),
				head.getPk_tradetype()).getPk_billtypeid());
		item.setCoordflag(null);
		// 特殊处理业务员
		// String pk_org = head.getPk_org();
		// if(pk_org!=null &&
		// head.getPk_billtype().equals(IArapBillTypeCons.F0)) {
		// if(item.getPk_psndoc()!=null) {
		// item.setPk_psndoc(getPk_psndoc(pk_org,item.getPk_psndoc()));
		// }
		// }

	}

	private static String getPk_psndoc(String pk_org, String value)
			throws BusinessException {
		if (value != null) {
			// 业务员的编码或者名称加财务组织查询为空 pk_org不对无法获取 编码或名称去查询唯一值返回信息
			String wherepartCodeorName = "1=1 and enablestate = 2 and "
					+ "pk_org='" + pk_org + "' and" + " (code = '" + value
					+ "' or name= '" + value + "')";
			PsndocVO[] psndocVOs = null;
			try {
				Collection<PsndocVO> vos = new BaseDAO().retrieveByClause(
						PsndocVO.class, wherepartCodeorName);
				psndocVOs = (PsndocVO[]) vos.toArray(new PsndocVO[0]);
				// 编码或者名称查询唯一的业务员主键返回PK值
				if (psndocVOs != null && !StringUtil.isEmpty(psndocVOs)
						&& psndocVOs.length == 1) {
					return psndocVOs[0].getPk_psndoc();
				}
				psndocVOs = null;
				String wherepartCode = "1=1 and enablestate = 2 and (code = '"
						+ value + "' )";
				vos = new BaseDAO().retrieveByClause(PsndocVO.class,
						wherepartCode);
				psndocVOs = (PsndocVO[]) vos.toArray(new PsndocVO[0]);
				// 编码或者名称查询唯一的业务员主键返回PK值
				if (psndocVOs != null && !StringUtil.isEmpty(psndocVOs)
						&& psndocVOs.length == 1) {
					return psndocVOs[0].getPk_psndoc();
				}
				psndocVOs = null;
				if (StringUtil.isEmpty(psndocVOs)) {
					String wherepartName = "1=1 and enablestate = 2 and (name = '"
							+ value + "')";
					vos = new BaseDAO().retrieveByClause(PsndocVO.class,
							wherepartName);
					psndocVOs = (PsndocVO[]) vos.toArray(new PsndocVO[0]);
					// 编码或者名称查询唯一的业务员主键返回PK值
					if (psndocVOs != null && !StringUtil.isEmpty(psndocVOs)
							&& psndocVOs.length == 1) {
						return psndocVOs[0].getPk_psndoc();
					}
				}
				// 其他返回空
			} catch (Exception e) {
				throw new BusinessException(e);
			}
			StringBuffer sbException = new StringBuffer();
			sbException.append(":[业务员:").append(value)
					.append("]不存在或者编码名称存在重复! 请检查业务员数据。");
			throw new BusinessRuntimeException(sbException.toString());
		}
		return null;
		// return value;
	}

	/**
	 * 获取个人银行账户PK
	 * 
	 * @param accSet
	 * @return
	 * @throws BusinessException
	 * @author cyz 2021年5月10日
	 */
	private List<Object[]> getAccountOfPsnbankacc(Set<String> accSet)
			throws BusinessException {
		StringBuffer sql = new StringBuffer();
		sql.append(" select	                                                                ");
		sql.append(" accnum,                                                                ");
		sql.append(" accname,                                                               ");
		sql.append(" pk_psndoc,                                                             ");
		sql.append(" pk_currtype,                                                           ");
		sql.append(" pk_bankaccsub                                                          ");
		sql.append(" from (                                                                 ");
		sql.append(" select                                                                 ");
		sql.append(" c.pk_psndoc,                                                           ");
		sql.append(" b.accnum, b.accname,  b.pk_currtype, b.pk_bankaccbas, b.pk_bankaccsub, ");
		sql.append(" a.enablestate, a.accclass                                              ");
		sql.append(" from bd_bankaccbas a , bd_bankaccsub b, bd_psnbankacc c                ");
		sql.append(" where a.pk_bankaccbas = b.pk_bankaccbas                                ");
		sql.append(" and b.pk_bankaccsub = c.pk_bankaccsub                                  ");
		sql.append(" and a.pk_bankaccbas = c.pk_bankaccbas                                  ");
		sql.append(" ) temp_bankacc                                                         ");
		sql.append(" where accclass ='0'");
		sql.append(" and enablestate = 2");
		sql.append(" and ("
				+ SqlUtils.getInStr("accnum", accSet.toArray(new String[0]),
						true)
				+ " or "
				+ SqlUtils.getInStr("accname", accSet.toArray(new String[0]),
						true) + ")");
		List<Object[]> result = (List<Object[]>) new BaseDAO().executeQuery(
				sql.toString(), new ArrayListProcessor());
		return result;
	}
}