package nc.bs.cmp.netpayment;

import static nc.cmp.utils.InterfaceLocator.getInterfaceLocator;
import static nc.cmp.utils.InterfaceLocator.getNetValidate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.cmp.businesslog.BusinessLogType;
import nc.bs.cmp.businesslog.CMPBusinessLogServiceUtil;
import nc.bs.cmp.util.SqlUtils;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.cmp.bill.util.SysInit;
import nc.cmp.settlement.validate.SettleValidate;
import nc.cmp.utils.CMPFactory;
import nc.cmp.utils.CmpInterfaceProxy;
import nc.cmp.utils.CmpUtils;
import nc.cmp.utils.DataUtil;
import nc.cmp.utils.Lists;
import nc.cmp.utils.MD5;
import nc.cmp.utils.NetPayHelper;
import nc.cmp.utils.NetValidate;
import nc.cmp.utils.SettleUtils;
import nc.impl.cmp.settlement.datapower.SettleDataPower;
import nc.itf.cm.prv.CmpConst;
import nc.itf.cmp.ebank.ICMPPaymentService;
import nc.itf.cmp.pub.BusinessLogContextTranslatorImpl;
import nc.itf.cmp.settlement.ISettlementQueryService;
import nc.itf.obm.ebanklog.IEbankLogQueryService;
import nc.pubitf.obm.IOnlinePayment;
import nc.vo.bd.bankaccount.BankAccbasVO;
import nc.vo.bd.bankaccount.IBankAccConstant;
import nc.vo.cmp.CMPSysParamConst;
import nc.vo.cmp.SettleStatus;
import nc.vo.cmp.netpay.CrumbOption;
import nc.vo.cmp.netpay.NetPayHelperVO;
import nc.vo.cmp.netpay.PaymentCrumbVO;
import nc.vo.cmp.settlement.CheckException;
import nc.vo.cmp.settlement.NetpayTransferVO;
import nc.vo.cmp.settlement.PaymentInfoVO4Batch;
import nc.vo.cmp.settlement.SettleEnumCollection.Direction;
import nc.vo.cmp.settlement.SettleKey;
import nc.vo.cmp.settlement.SettlementAggVO;
import nc.vo.cmp.settlement.SettlementBodyVO;
import nc.vo.cmp.settlement.SettlementHeadVO;
import nc.vo.cmp.settlement.international.util.SettleInternationalValidate;
import nc.vo.cmp.settlement.util.SettlePubSecurityUtil;
import nc.vo.cmp.util.CMPBusiUtils;
import nc.vo.cmp.validate.CMPValidate;
import nc.vo.ebank.interfac.FuncTypeConst;
import nc.vo.obm.ebankpaylog.EBankPayLogVO;
import nc.vo.obm.obmvo.PayStateQueryVO;
import nc.vo.obm.pay.LogMgrRequestVO;
import nc.vo.obm.pay.OnlinePaymentVO;
import nc.vo.obm.pay.PaymentRetMsg;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.AppContext;
import nc.vo.tmpub.util.ArrayUtil;
import nc.vo.tmpub.util.StringUtil;

/**
 * CMPPaymentServiceImpl.java
 * 
 * @author wuzhwa
 * @since v6.0
 * @see V57 nc.bs.cmp.netpayment.CMPPaymentServiceImpl
 */
public class CMPPaymentServiceImpl implements ICMPPaymentService {
	nc.itf.cmp.prv.ICMPAccountQuery accQuery = NCLocator.getInstance().lookup(nc.itf.cmp.prv.ICMPAccountQuery.class);
	private boolean isFail;
	private Map<String, PaymentCrumbVO> payVOs = CmpUtils.makeMap(); // 支付信息Map
	private boolean isCombinPayed = false; // 是否合并支付过
	private boolean isCombinPayOperate = false; // 是否合并支付操作
	private boolean isLinkData = false;

	/*
	 * (non-Javadoc) 联查时用到
	 * 
	 * @see
	 * nc.itf.cmp.ebank.ICMPPaymentService#getNetpayTransferVO(nc.vo.cmp.settlement
	 * .SettlementAggVO)
	 */
	@Override
	public NetpayTransferVO getNetpayTransferVO(SettlementAggVO settlement) throws BusinessException {
		SettleValidate.validatePS(settlement);
		NetpayTransferVO transferVO = new NetpayTransferVO();
		Map<String, String> map = new HashMap<String, String>();

		String pk_user = settlement.getPk_user();
		OnlinePaymentVO[] vos = null;
		// 获取表头表体信息
		SettlementHeadVO header = (SettlementHeadVO) settlement.getParentVO();
		SettlementBodyVO[] details = (SettlementBodyVO[]) settlement.getChildrenVO();

		// 验签
		// SettlePubSecurityUtil.verifySign(settlement,
		// SettlePubSecurityUtil.CONST_ACTIONCODE_NETPAY, false);
		// 划账单处理
		if (header.getDirection() == CmpConst.Direction_CHANGE) {
			// 划账结算单校验
			NetValidate.getInstance().validateHJ(settlement);
			String pk_oppaccount = null;
			List<SettlementBodyVO> bodys = Lists.newArrayList(details);
			for (Iterator<SettlementBodyVO> iter = bodys.iterator(); iter.hasNext();) {
				SettlementBodyVO body = iter.next();
				if (body.getDirection() == CmpConst.Direction_Receive) {
					// 将收方向表体放入transfervo中
					transferVO.setRecbody(body);
					pk_oppaccount = body.getPk_account();// 取出对方账号
					// 移除收方向表体
					iter.remove();
				}
			}
			details = CmpUtils.covertListToArrays(bodys, SettlementBodyVO.class);
			settlement.setChildrenVO(details);// 只有付款
			details[0].setPk_oppaccount(pk_oppaccount); // 放入对方账号
		}
		NetValidate.getInstance().validateForUnitOrPerson(settlement);

		// 放入结算信息聚合VO
		transferVO.setAgg(settlement);
		String pk_settlement = null;
		/** 首先查询该结算信息是否已经支付过，如果支付过，则不允许进行网银信息补录 */
		pk_settlement = header.getPk_settlement();
		if (header.getSettlestatus() != SettleStatus.PAYFAIL.getStatus()) {
			// 检查支付状态
			// boolean isPayed =
			// getInterfaceLocator().getPaymentService().isPayed(pk_settlement);

			// if (!isPayed) {
			// throw new
			// BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("3607set_0","03607set-0412")/*@res
			// "单据尚未支付，不能联查网银信息"*/);
			// }
			// CheckException.checkArgument(isPayed,
			// "单据已经支付过或则在支付中，不需要再补录网银信息");
		} else {
			setFail(true);
		}
		isLinkData = true;
		setPayVOs(details);
		if (CmpUtils.isListNull(details)) {
			return null;
		}
		// 一对多关系
		if (header.getArithmetic() == CmpConst.ONE_TO_MANY) {
			Map<SettleKey, List<String>> oneToManyMap = new HashMap<SettleKey, List<String>>();
			SettlementBodyVO[] clones = CmpUtils.CloneObj(details);
			for (SettlementBodyVO body : clones) {
				// 业务信息分组规则
				SettleKey key = new SettleKey();
				CmpUtils.copy(key, body);
				List<String> list = oneToManyMap.get(key);
				if (list == null) {
					list = Lists.newArrayList();
					oneToManyMap.put(key, list);
				}
				list.add(body.getPk_detail());
			}
			transferVO.setClones(clones);
			transferVO.setOneToManyMap(oneToManyMap);
			details = CMPFactory.createAlgorithm(1).sumBodyVOToUI(clones);

			/** 把结算信息转换成网银需要的信息，传递给网银 */
			vos = convertToEbankSigngleWithoutCheck(details, map, transferVO.getAgg(), pk_user);
		} else {
			// 一对一
			/** 把结算信息转换成网银需要的信息，传递给网银 */
			vos = convertToEbankSigngleWithoutCheck(details, map, transferVO.getAgg(), pk_user);
		}
		transferVO.setMap(map);
		transferVO.setOnlinePaymentVO(vos);
		transferVO.setDetails(details);
		return transferVO;
	}

	protected OnlinePaymentVO[] convertToEbankSigngleWithoutCheck(SettlementBodyVO[] bodyvos, Map<String, String> map,
			SettlementAggVO agg, String pk_user) throws BusinessException {

		boolean isperson = CMPBusiUtils.isForPerson(bodyvos);
		List<OnlinePaymentVO> payList = Lists.newArrayList();
		SettlementBodyVO[] details = bodyvos;
		Map<String, Integer> pk_oppaccountTOAccpropMap = CMPBusiUtils.getPk_oppaccountTOAccpropMapByStlBodyVOs(details);
		for (SettlementBodyVO detail : details) {
			if (detail.getDirection() == CmpConst.Direction_Receive) {
				continue;
			}
			OnlinePaymentVO pvo = new OnlinePaymentVO();
			payList.add(pvo);
			/**
			 * 根据账户查找银行信息
			 */
			NetPayHelperVO selfhelperVO = NetPayHelper.instance.getNetPayVO(detail.getPk_account());

			/** 收款信息 */
			if (isperson) {
				BankAccbasVO bankacc = accQuery.findAccountById(detail.getPk_oppaccount());
				pvo.setCrtbank(bankacc.getPk_bankaccbas());
//				pvo.setCrtname(bankacc.getAccname());
			} else {
				NetPayHelperVO opphelperVO = NetPayHelper.instance.getNetPayVO(detail.getPk_oppaccount());

				pvo.setCrtbank(opphelperVO.getPk_bankaccbas());// 收方账户开户行
//				pvo.setCrtname(opphelperVO.getBankdocname());// 收方账户开户行
			}
			pvo.setCrtacc(detail.getOppaccount());// 收款账号
			pvo.setCrtname(detail.getOppaccname());// 收方账户户名
			
			pvo.setCrtaccPk(detail.getPk_oppaccount());

			/** 付款信息 */

			pvo.setDbtacc(detail.getAccountnum()); // 付款账户
			pvo.setDbtaccsubPk(detail.getPk_account()); // 付款账户子户
			pvo.setPk_group(detail.getPk_group());// 付款单位
			pvo.setPk_org(detail.getPk_org());// 付款单位

			pvo.setDbtname(selfhelperVO.getBankdocname());
			pvo.setDbtaccPk(selfhelperVO.getPk_bankaccbas());
			/** 普通信息 */
			pvo.setCurrency(detail.getPk_currtype());// 币种
			pvo.setTrsamt(detail.getPay());// 金额

			pvo.setSrcsystem(detail.getSystemcode());
			pvo.setModulecode(detail.getSystemcode());
			pvo.setBillpk(detail.getPk_bill()); // 单据PK
			pvo.setBilltype(detail.getPk_billtype());
			pvo.setBillcode(detail.getBillcode());

			pvo.setNetitftype(selfhelperVO.getNetbankinftpcode());// 银行类型
			if (pk_oppaccountTOAccpropMap.get(detail.getPk_oppaccount()) == IBankAccConstant.ACCPROPERTY_PERSONAL) {
				pvo.setFunc(FuncTypeConst.DFDK);// 对私指令
				pvo.setDbtname(selfhelperVO.getBankdocname());
//				CheckException.checkArgument(pvo.getCrtacc() != null && pvo.getCrtaccPk() == null,
//						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("3607set_0", "03607set-0413")/*
//																											 * @
//																											 * res
//																											 * "对私支付不能手工录入对方账号"
//																											 */);
			} else if (pk_oppaccountTOAccpropMap.get(detail.getPk_oppaccount()) == IBankAccConstant.ACCPROPERTY_UNIT) {
				pvo.setFunc(FuncTypeConst.ZF);// 指令
			}

			// 网银的必输入项，没有要赋0
			pvo.setBusnar(detail.getMemo());
			pvo.setNusage(detail.getMemo());

			// 如果已经有了，则不需要新的(支付失败的除外)
			PaymentCrumbVO payVO = payVOs.get(detail.getPk_settlement() + detail.getPrimaryKey());
			String yuref = null;
			if (payVO == null) {
				yuref = getYuref(detail);

			} else {

				if (isFail() || payVO.getPaystatus() == 1) {

					yuref = getYuref2(detail);
					yuref = yuref == null ? payVO.getYurref() : yuref;

				} else {
					yuref = payVO.getYurref();
				}
			}
			pvo.setYurref(yuref);// 参考号

			map.put(detail.getPk_detail(), yuref);

		}
		setFail(false);
		return CmpUtils.covertListToArrays(payList, OnlinePaymentVO.class);
	}

	private String getYuref2(SettlementBodyVO detail) throws BusinessException {
		if (isLinkData) {
			// 联查，跳过
		} else if (isCombinPayOperate) {
			// 合并支付，重新取
			return getYuref(detail);
		} else if (isCombinPayed) {
			// 合并支付过，当前非合并支付操作，重新取参考号
			return getYuref(detail);
		}
		return null;
	}

	public NetpayTransferVO dobeforeFillEBank(SettlementAggVO settlement) throws BusinessException {
		// 检查是否最新的结算信息
		SettleUtils.addDynamicLock(settlement);
		SettleUtils.checkTs(settlement);
		// added by zhufeng 2013-7-13  start
		SettlementHeadVO headVO = NCLocator.getInstance().lookup(ISettlementQueryService.class).findHeadsByPks(
				new String[] { ((SettlementHeadVO) settlement.getParentVO()).getPrimaryKey() })[0];
		settlement.setParentVO(headVO);
		// added by zhufeng 2013-7-13  end
		

		SettleValidate.validatePS(settlement);

		NetpayTransferVO transferVO = new NetpayTransferVO();
		Map<String, String> map = new HashMap<String, String>();

		String pk_user = settlement.getPk_user();
		isLinkData = false;
		// PaymentVO[] vos = null;
		OnlinePaymentVO[] vos = null;
		// 获取表头表体信息
		SettlementHeadVO header = (SettlementHeadVO) settlement.getParentVO();
		SettlementBodyVO[] details = (SettlementBodyVO[]) settlement.getChildrenVO();
		
		Map<String,SettlementBodyVO> bodymap = new HashMap<String,SettlementBodyVO>();
		for(SettlementBodyVO body : details)
		{
			bodymap.put(body.getPk_detail(), body);
		}

		// 验签签名交给后台处理
		SettlementAggVO[] oldaggvos = CmpInterfaceProxy.INSTANCE.getQueryService().querySettlementAggVOsByPks(
				new String[] { header.getPk_settlement() });
		
		oldaggvos[0].setParentVO(header);
		SettlementBodyVO[] olddetails = (SettlementBodyVO[]) oldaggvos[0].getChildrenVO();
		for(int i=0;i<olddetails.length;i++)
		{
			if(bodymap.get(olddetails[i].getPk_detail())!=null)
			{
				olddetails[i] = bodymap.get(olddetails[i].getPk_detail());
			}
		}
		
		SettlePubSecurityUtil.verifySign(oldaggvos[0], SettlePubSecurityUtil.CONST_ACTIONCODE_NETPAY, false);
		// 划账单处理
		if (header.getDirection() == CmpConst.Direction_CHANGE) {
			// 划账结算单校验
			NetValidate.getInstance().validateHJ(settlement);
			String pk_oppaccount = null;
			List<SettlementBodyVO> bodys = Lists.newArrayList(details);
			for (Iterator<SettlementBodyVO> iter = bodys.iterator(); iter.hasNext();) {
				SettlementBodyVO body = iter.next();
				if (body.getDirection() == CmpConst.Direction_Receive) {
					// 将收方向表体放入transfervo中
					transferVO.setRecbody(body);
					pk_oppaccount = body.getPk_account();// 取出对方账号
					// 移除收方向表体
					iter.remove();
				}
			}
			details = CmpUtils.covertListToArrays(bodys, SettlementBodyVO.class);
			settlement.setChildrenVO(details);// 只有付款
			details[0].setPk_oppaccount(pk_oppaccount); // 放入对方账号
		} 
		// 放入结算信息聚合VO

		// String pk_settlement = null;
		/** 首先查询该结算信息是否已经支付过，如果支付过，则不允许进行网银信息补录 */
		// pk_settlement = header.getPk_settlement();
		if (header.getSettlestatus() != SettleStatus.PAYFAIL.getStatus()) {
			// 检查支付状态
			// boolean isPayed =
			// getInterfaceLocator().getPaymentService().isPayed(pk_settlement);
			// CheckException.checkArgument(isPayed,
			// nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("3607set_0","03607set-0414")/*@res
			// "单据已经支付过或则在支付中，不需要再补录网银信息"*/);
		} else {
			setFail(true);
		}
		transferVO.setAgg(settlement);
		isCombinPayOperate = false;
		// 一对多关系
		// if (header.getArithmetic() == CmpConst.ONE_TO_MANY) {
		// Map<SettleKey, List<String>> oneToManyMap = new HashMap<SettleKey,
		// List<String>>();
		// SettlementBodyVO[] clones = CmpUtils.CloneObj(details);
		// for (SettlementBodyVO body : clones) {
		// // 业务信息分组规则
		// SettleKey key = new SettleKey();
		// CmpUtils.copy(key, body);
		// List<String> list = oneToManyMap.get(key);
		// if (list == null) {
		// list = Lists.newArrayList();
		// oneToManyMap.put(key, list);
		// }
		// list.add(body.getPk_detail());
		// }
		// transferVO.setClones(clones);
		// transferVO.setOneToManyMap(oneToManyMap);
		//
		// SettleUtils.convertNullToDefaultValue(clones); //
		// 算法用到了收款金额信息，所以加上此处理。
		// details = CMPFactory.createAlgorithm(1).sumBodyVOToUI(clones);
		// SettleUtils.convertZeroToNull(details); // 算法用到了收款金额信息，移除处理。
		//
		// /** 把结算信息转换成网银需要的信息，传递给网银 */
		// vos = convertToEbankSigngle(details, map, transferVO.getAgg(),
		// pk_user);
		// } else {
		// 一对一
		/** 把结算信息转换成网银需要的信息，传递给网银 */
		vos = convertToEbankSigngle(details, map, transferVO.getAgg(), pk_user);
		// }
		//前台已处理过，不需要再处理了。
//		settlement = NCLocator.getInstance().lookup(ISettlementQueryService.class)
//				.querySettlementAggVOsByPks(new String[] { settlement.getParentVO().getPrimaryKey() })[0];
//		List<SettlementAggVO> aggLst = SettleUtils.filterSettleInfo4NetSettleFlagUnSettle(settlement);
//		if (aggLst.size() == 0) {
//			return null;
//		}
//		settlement = aggLst.get(0);
		transferVO.setAgg(settlement);
		transferVO.setMap(map);
		transferVO.setOnlinePaymentVO(vos);
		transferVO.setDetails(details);
		return transferVO;
	}

	@Override
	public PaymentRetMsg[] doNetTransfer(OnlinePaymentVO[] vos, SettlementBodyVO[] codeBodys, String operaterid,
			SettlementAggVO... aggVOs) throws BusinessException {

		SettlementAggVO[] oldAggVOs = this.queryLastSettlementAggVOs(aggVOs);

		// modified by zhufeng 2013-7-13 进入后,单据必须校验ts start
		SettleUtils.addDynamicLock(aggVOs);
		SettleUtils.addDynamicLock(codeBodys);
		
		SettleUtils.checkTs(aggVOs);
		 // modified by zhufeng 2013-7-13 end
		SettleValidate.validatePS(aggVOs);
		SettleValidate.validatePayBankAccount(aggVOs);

		SettleDataPower.validataUserhasPermission(SettleDataPower.NETPAY, aggVOs);
		setPayVOs(codeBodys);
		for (SettlementAggVO settlementAggVO : aggVOs) {

			CmpUtils.updateObm(settlementAggVO);

		}
		getInterfaceLocator().getPaymentService().checkCode(aggVOs);
		isCombinPayOperate = false;
		// added by zhufeng 2013-9-14  增加校验,校验支付的状态 start
		for (SettlementAggVO settlementAggVO : aggVOs) { 
			checkYurrefStatus( settlementAggVO.getChildrenVO());
		}
		// added by zhufeng 2013-9-14  end
		PaymentRetMsg[] paymentRetMsgs = getInterfaceLocator().getPaymentService().handleTransferFund(
				CmpUtils.covertArraysToList(codeBodys), vos, operaterid);

		CMPBusinessLogServiceUtil.writeLogs(new BusinessLogContextTranslatorImpl(SettlementHeadVO.BEAN_ID)
				.getBusinessLogContexts(BusinessLogType.SettlementBusinessLogType.NETPAY.getValue(),
						this.queryLastSettlementAggVOs(aggVOs), oldAggVOs));

		return paymentRetMsgs;
	}

	private SettlementAggVO[] queryLastSettlementAggVOs(SettlementAggVO[] settlementAggVOs) throws BusinessException {
		List<String> pk_settlements = new ArrayList<String>();
		for (SettlementAggVO settlementAggVO : settlementAggVOs) {
			pk_settlements.add(settlementAggVO.getParentVO().getPrimaryKey());

		}

		ISettlementQueryService settlementQueryService = NCLocator.getInstance().lookup(ISettlementQueryService.class);
		return settlementQueryService.querySettlementAggVOsByPks(pk_settlements.toArray(new String[0]));
	}

	@Override
	public void doNetTransfer4SalaryBill(String operaterid, SettlementAggVO... aggVOs) throws BusinessException {
		SettlementAggVO[] oldAggVOs = this.queryLastSettlementAggVOs(aggVOs);
		SettleValidate.validatePayBankAccount(aggVOs);
		for (SettlementAggVO agg : aggVOs) {
			getNetValidate().validatePay(agg);
		}

		getInterfaceLocator().getPaymentService().handleTransferFund4SalaryBill(operaterid, aggVOs);
		CMPBusinessLogServiceUtil.writeLogs(new BusinessLogContextTranslatorImpl(SettlementHeadVO.BEAN_ID)
				.getBusinessLogContexts(BusinessLogType.SettlementBusinessLogType.NETPAY.getValue(),
						this.queryLastSettlementAggVOs(aggVOs), oldAggVOs));
	}

	// protected PaymentVO[] convertToEbankSigngle(SettlementBodyVO[] bodyvos,
	// Map<String, String> map, SettlementAggVO agg, String pk_user) throws
	// BusinessException {
	protected OnlinePaymentVO[] convertToEbankSigngle(SettlementBodyVO[] bodyvos, Map<String, String> map,
			SettlementAggVO agg, String pk_user) throws BusinessException {
		// List<PaymentVO> payList = Lists.newArrayList();
		// boolean isperson = CMPBusiUtils.isForPerson(bodyvos);
		List<OnlinePaymentVO> payList = Lists.newArrayList();
		getNetValidate().validatePay(agg);
		SettlementBodyVO[] details = bodyvos;
		setPayVOs(details);

		Map<String, Integer> pk_oppaccountTOAccpropMap = CMPBusiUtils.getPk_oppaccountTOAccpropMapByStlBodyVOs(details);
		for (SettlementBodyVO detail : details) {
			if (detail.getDirection() == CmpConst.Direction_Receive) {
				continue;
			}
			// PaymentVO pvo = new PaymentVO();
			OnlinePaymentVO pvo = convertPaymentVO(detail, pk_oppaccountTOAccpropMap);
			payList.add(pvo);

			map.put(detail.getPk_detail(), pvo.getYurref());

		}
		setFail(false);
		return CmpUtils.covertListToArrays(payList, OnlinePaymentVO.class);
	}

	private void checkCombinPayed() {
		if (payVOs.size() == 0) {
			isCombinPayed = false;
			return;
		}
		Set<String> set = new HashSet<String>();
		for (PaymentCrumbVO vo : payVOs.values()) {
			if (set.contains(vo.getYurref())) {
				// 有重复的参考号，表示合并支付过。
				isCombinPayed = true;
				return;
			}
			set.add(vo.getYurref());
		}
		isCombinPayed = false;
	}

	public boolean isFail() {
		return isFail;
	}

	public void setFail(boolean isFail) {
		this.isFail = isFail;
	}

	private PaymentCrumbVO getPayVO(String pk_settlement, String pk_detail) throws BusinessException {
		String condition = " pk_settlement = '" + pk_settlement + "' and pk_detail = '" + pk_detail + "'"; // +
		// "' and paystatus <> 1";
		List<PaymentCrumbVO> payList = getInterfaceLocator().getPaymentService().findPaymentCrumbByCondition(condition);
		if (!CheckException.checkContionsIsNull(payList)) {
			return payList.get(0);
		}
		return null;
	}

	private void setPayVOs(SettlementBodyVO... vos) throws BusinessException {
		Map<String, PaymentCrumbVO> map = CmpUtils.makeMap();
		StringBuilder condition = new StringBuilder(" 1=1 and (");
		for (int i = 0; i < vos.length; i++) {
			SettlementBodyVO settlementBodyVO = vos[i];
			condition.append("  (pk_settlement = '" + settlementBodyVO.getPk_settlement() + "' and pk_detail = '"
					+ settlementBodyVO.getPk_detail() + "')"); // +
			if (i < vos.length - 1) {
				condition.append(" or ");
			}
		}
		condition.append(") order by ts desc");
		List<PaymentCrumbVO> payList = getInterfaceLocator().getPaymentService().findPaymentCrumbByCondition(
				condition.toString());
		if (payList == null) {
			return;
		}
		List<String> yurrefs = CmpUtils.makeList();

		for (PaymentCrumbVO paymentCrumbVO : payList) {
			yurrefs.add(paymentCrumbVO.getYurref());
		}
		// 重新查一次参考号，可能有重复数据
		condition = new StringBuilder(SqlUtils.getInStr("yurref", yurrefs.toArray(new String[] {}), false));

		payList = getInterfaceLocator().getPaymentService().findPaymentCrumbByCondition(condition.toString());
		if (payList == null) {
			return;
		}
		for (PaymentCrumbVO paymentCrumbVO : payList) {
			map.put(paymentCrumbVO.getPk_settlement() + paymentCrumbVO.getPk_detail(), paymentCrumbVO);
		}
		payVOs = map;
		checkCombinPayed();

	}

	private String getYuref(SettlementBodyVO body) throws BusinessException {

		String md5ofStr = new MD5().getMD5ofStr(DataUtil.getUFDate().toString() + body.getPrimaryKey());
		String code = body.getPk_detail() + md5ofStr;
		return code;
	}

	public NetpayTransferVO doCombinPay(SettlementAggVO[] aggs, String info) throws BusinessException {
		SettleUtils.addDynamicLock(aggs);
		SettleUtils.checkTs(aggs);
		// 有TS会导致锁表 FIXME 如何防止并发
		isCombinPayOperate = true;
		List<SettlementAggVO> aggLst = SettleUtils.filterSettleInfo4NetSettleFlagUnSettle(aggs);
		if (aggLst.size() == 0) {

			return null;
		}
		aggs = aggLst.toArray(new SettlementAggVO[]{});

		getNetValidate().combinPay(aggs);
		getNetValidate().validateCombin(aggs, false);
		SettleValidate.validatePS(aggs);
		SettleValidate.validatePayBankAccount(aggs);
		SettleDataPower.validataUserhasPermission(SettleDataPower.COMBINATIONPAY, aggs);
		
		Set<String> pk_settlements = new HashSet<String>();
		for(SettlementAggVO settlementAggVO : aggs){
			pk_settlements.add(settlementAggVO.getParentVO().getPrimaryKey());
			
		}

		// 验签
		SettlementAggVO[] oldaggvos = CmpInterfaceProxy.INSTANCE.getQueryService().querySettlementAggVOsByPks(
						 pk_settlements.toArray(new String[0] ));
		
		SettlePubSecurityUtil.verifySign(SettlePubSecurityUtil.CONST_ACTIONCODE_COMBINPAY, false, oldaggvos);

		String pk_org = ((SettlementHeadVO) aggs[0].getParentVO()).getPk_org();
		String pk_group = ((SettlementHeadVO) aggs[0].getParentVO()).getPk_group();
		List<SettlementBodyVO> bodyList = Lists.newArrayList();
		Set<String> ids = new HashSet<String>();
		getInterfaceLocator().getPaymentService().checkCode(aggs);
		for (SettlementAggVO aggVO : aggs) {

			SettlementHeadVO head = (SettlementHeadVO) aggVO.getParentVO();
			String pk_settlement = head.getPk_settlement();
			if (head.getSettlestatus() != SettleStatus.PAYFAIL.getStatus()) {
				boolean isPayed = getInterfaceLocator().getPaymentService().isPayed(pk_settlement);
				CheckException.checkArgument(isPayed,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("3607set_0", "03607set-0916")/*
																											 * @
																											 * res
																											 * "已经支付过或则在支付中，不需要再补录网银信息"
																											 */);
			}
			ids.add(pk_settlement);
		}

		// 一对一以及一对多的统一
		// List<SettlementHeadVO> heads =
		// getInterfaceLocator().getSettlementQueryService().queryAggVOsByHeadIds(Lists.newArrayList(ids));
		// for (SettlementHeadVO head : heads) {
		// bodyList.addAll(head.getBodys());
		// }
		for (SettlementAggVO settlementAggVO : aggs) {
			bodyList.addAll(CmpUtils.covertArraysToList((SettlementBodyVO[]) settlementAggVO.getChildrenVO()));
		}

		setPayVOs(bodyList.toArray(new SettlementBodyVO[0]));

		Map<String, List<SettlementBodyVO>> bodyMap = new HashMap<String, List<SettlementBodyVO>>();
		Map<String, String> map = new HashMap<String, String>();
		// PaymentVO[] vos = convertToEbank(map, bodyList,bodyMap, aggs, info);
		OnlinePaymentVO[] vos = convertToEbank(map, bodyList, bodyMap, aggs, info);
		String serial = CmpUtils.getSettleCode(pk_org, pk_group);
		NetpayTransferVO transferVO = new NetpayTransferVO();
		// aggs[0] 只用于PK_GROUP。将来可做调整。
		transferVO.setAgg(aggs[0]);
		transferVO.setMap(map);
		transferVO.setOnlinePaymentVO(vos);
		transferVO.setSerial(serial);
		transferVO.setDetails(CmpUtils.covertListToArrays(bodyList, SettlementBodyVO.class));
		transferVO.setBodyMap(bodyMap);
		return transferVO;
	}

	private OnlinePaymentVO[] convertToEbank(Map<String, String> map, List<SettlementBodyVO> bodyList,
			Map<String, List<SettlementBodyVO>> bodyMap, SettlementAggVO[] aggs, String info) throws BusinessException {
		List<OnlinePaymentVO> payList = Lists.newArrayList();
		for (SettlementAggVO agg : aggs) {
			getNetValidate().validatePay(agg);
		}
		String cmp94 = SysInit.getParaString(AppContext.getInstance().getPkGroup(), CMPSysParamConst.CMP94);
		for (SettlementBodyVO body : bodyList) {
			// 合并规则号对应的是serail
			String combin;
			if ("多张合并支付".equals(cmp94)) {
				combin = body.getPk_oppaccount() + body.getPk_currtype() + body.getPk_account() + body.getOppaccount();
			} else {
				combin = body.getPk_oppaccount() + body.getPk_currtype() + body.getPk_account() + body.getOppaccount()
						+ body.getPk_bill();
			}
			
			List<SettlementBodyVO> bodys = bodyMap.get(combin);
			if (bodys == null) {
				bodys = Lists.newArrayList();
				bodyMap.put(combin, bodys);
			}
			bodys.add(body);
		}
		// 每组一个网银参考号
		for (String code : bodyMap.keySet()) {
			List<SettlementBodyVO> list = bodyMap.get(code);
			OnlinePaymentVO payvo = covertPayVO(list, info);
			// for (OnlinePaymentVO onlinePaymentVO : payVOs) {
			String yurref = payvo.getYurref();
			map.put(code, yurref);
			// }
			payList.add(payvo);
		}
		return CmpUtils.covertListToArrays(payList, OnlinePaymentVO.class);
	}

	/**
	 * 获取在线支付VO
	 * 
	 * @param list
	 * @param operaterid
	 * @return
	 * @throws BusinessException
	 */
	private OnlinePaymentVO covertPayVO(List<SettlementBodyVO> list, String operaterid) throws BusinessException {
		// OnlinePaymentVO pvo = new OnlinePaymentVO();
		SettlementBodyVO body = list.get(0);
		Map<String, Integer> pk_oppaccountTOAccpropMap = CMPBusiUtils.getPk_oppaccountTOAccpropMapByStlBodyVOs(list
				.toArray(new SettlementBodyVO[] {}));
		UFDouble pay = body.getPay();
		for (int i = 1; i < list.size(); i++) {
			pay = pay.add(list.get(i).getPay());
		}
		SettlementBodyVO detail = (SettlementBodyVO) list.get(0).clone();
		detail.setPay(pay);
		OnlinePaymentVO pvo = convertPaymentVO(detail, pk_oppaccountTOAccpropMap);

		return pvo;
	}

	private OnlinePaymentVO convertPaymentVO(SettlementBodyVO detail, Map<String, Integer> pk_oppaccountTOAccpropMap)
			throws BusinessException {
		OnlinePaymentVO pvo = new OnlinePaymentVO();
		/**
		 * 根据账户查找银行信息
		 */

		/** 收款信息 */
		pvo.setCrtbank(detail.getOppbank());// 收方账户银行
		pvo.setCrtaccPk(detail.getPk_oppaccount());
		
		pvo.setCrtacc(detail.getOppaccount());// 收款账号
		pvo.setCrtname(detail.getOppaccname());// 收方账户户名
		
		/** 付款信息 */
		NetPayHelperVO selfhelperVO = NetPayHelper.instance.getNetPayVO(detail.getPk_account());
//		pvo.setDbtacc(selfhelperVO.getAccount()); // 付款账户
		pvo.setDbtacc(detail.getAccountnum()); // 付款账户
		pvo.setDbtname(selfhelperVO.getAccname());
		
		pvo.setDbtaccPk(selfhelperVO.getPk_bankaccbas());
		pvo.setDbtaccsubPk(detail.getPk_account()); // 付款账户子户

		/** 普通信息 */
		pvo.setPk_group(detail.getPk_group());// 付款单位
		pvo.setPk_org(detail.getPk_org());// 付款单位

		pvo.setCurrency(detail.getPk_currtype());// 币种
		pvo.setTrsamt(detail.getPay());// 金额

		pvo.setSrcsystem(detail.getSystemcode());
		pvo.setModulecode(detail.getSystemcode());
		pvo.setBillpk(detail.getPk_bill()); // 单据PK
		pvo.setBilltype(detail.getPk_billtype());
		pvo.setBillcode(detail.getBillcode());

		// 同城，同行，速度
		pvo.setIssamebank(detail.getIssamebank());
		pvo.setIssamecity(detail.getIssamecity());
		pvo.setPay_type(detail.getPay_type());
		pvo.setReconciliationcode(detail.getBankrelated_code());
		pvo.setNetitftype(selfhelperVO.getNetbankinftpcode());// 银行类型
		if (pk_oppaccountTOAccpropMap.get(detail.getPk_oppaccount()) == IBankAccConstant.ACCPROPERTY_PERSONAL) {
			pvo.setFunc(FuncTypeConst.DFDK);// 对私指令
			CheckException.checkArgument(pvo.getCrtacc() != null && pvo.getCrtaccPk() == null,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("3607set_0", "03607set-0413")/*
																										 * @
																										 * res
																										 * "对私支付不能手工录入对方账号"
																										 */);
		} else if (pk_oppaccountTOAccpropMap.get(detail.getPk_oppaccount()) == IBankAccConstant.ACCPROPERTY_UNIT) {
			pvo.setFunc(FuncTypeConst.ZF);// 指令
		}

		// 网银的必输入项，没有要赋0
		pvo.setBusnar(detail.getMemo());
		pvo.setNusage(detail.getMemo());

		// 如果已经有了，则不需要新的(支付失败的除外)
		// PaymentCrumbVO payVO = getPayVO(detail.getPk_settlement(),
		// detail.getPrimaryKey());
		PaymentCrumbVO payVO = payVOs.get(detail.getPk_settlement() + detail.getPrimaryKey());
		
		
		
//		if(payVO != null){
//			String oldYuref = payVO.getYurref();
//			
//			IEbankLogQueryService logQueryService = (IEbankLogQueryService) NCLocator.getInstance().lookup(IEbankLogQueryService.class.getName());
//			EBankPayLogVO[] existslogvos = logQueryService.queryBuLuLogByYurrefs(new String[]{oldYuref}, false);
//			if(existslogvos != null && existslogvos.length> 0){
//				pvo = change(pvo,existslogvos[0]);
//			}
//		}
		
		
		String yuref = null;
		if (payVO == null) {
			yuref = getYuref(detail);

		} else {
			if (isFail() || isCombinPayed || payVO.getPaystatus() == 1) {

				yuref = getYuref2(detail);
				yuref = yuref == null ? payVO.getYurref() : yuref;

			} else {
				yuref = payVO.getYurref();
			}
		}
		pvo.setYurref(yuref);// 参考号
		return pvo;
	}

	private OnlinePaymentVO change(OnlinePaymentVO onlinePaymentVO, EBankPayLogVO eBankPayLogVO) {
		
		OnlinePaymentVO oldOnlinePaymentVO = new OnlinePaymentVO();
		

		String[] atrris = oldOnlinePaymentVO.getAttributeNames();
		for (String attr : atrris) {
			oldOnlinePaymentVO.setAttributeValue(attr, eBankPayLogVO.getAttributeValue(attr));
		}
		
		
		oldOnlinePaymentVO.setDbtaccPk(eBankPayLogVO.getPk_bankaccbas());
		oldOnlinePaymentVO.setDbtaccsubPk(eBankPayLogVO.getPk_bankaccsub());
		oldOnlinePaymentVO.setDbtname(eBankPayLogVO.getDbtaccname());
		oldOnlinePaymentVO.setDbtacc(eBankPayLogVO.getDbtacc());
		
		oldOnlinePaymentVO.setCrtacc(eBankPayLogVO.getCrtacc());
		
		oldOnlinePaymentVO.setCrtname(eBankPayLogVO.getCrtaccname());
		oldOnlinePaymentVO.setCrtbank(eBankPayLogVO.getCrtbranchname());
		
		oldOnlinePaymentVO.setGroupacc(eBankPayLogVO.getJtaccount());
		oldOnlinePaymentVO.setAgentacc(eBankPayLogVO.getAgentacc());
		oldOnlinePaymentVO.setAgentname(eBankPayLogVO.getAgentname());
		
		oldOnlinePaymentVO.setCurrency(eBankPayLogVO.getC_ccynbr());
		oldOnlinePaymentVO.setTrsamt(eBankPayLogVO.getTrsamt());
		oldOnlinePaymentVO.setNetitftype(eBankPayLogVO.getBanktypecode());
		
		oldOnlinePaymentVO.setIssamebank(eBankPayLogVO.getIssamebank());
		oldOnlinePaymentVO.setSrcsystem(eBankPayLogVO.getSrcsystem());
		
		oldOnlinePaymentVO.setBillpk(eBankPayLogVO.getSrcpkid());
		
		oldOnlinePaymentVO.setYurref(eBankPayLogVO.getYurref());
		oldOnlinePaymentVO.setFunc(eBankPayLogVO.getFunc());

		
		for(String attr : atrris){
			
			if(onlinePaymentVO.getAttributeValue(attr) ==null){
				onlinePaymentVO.setAttributeValue(attr, oldOnlinePaymentVO.getAttributeValue(attr));
			}
			
		}
		
		return onlinePaymentVO;
	}

	public List<PaymentCrumbVO> dealOneToMany(NetpayTransferVO transferVO, int status, String operaterid,
			UFDate billdate) throws BusinessException {
		Map<String, String> idmap = new HashMap<String, String>();
		List<PaymentCrumbVO> crumbs = Lists.newArrayList();
		Map<String, PaymentCrumbVO> yurrefMap = new HashMap<String, PaymentCrumbVO>();
		Map<String, PaymentCrumbVO> allMap = new HashMap<String, PaymentCrumbVO>();
		SettlementHeadVO head = (SettlementHeadVO) transferVO.getAgg().getParentVO();
		for (SettlementBodyVO body : transferVO.getClones()) {
			PaymentCrumbVO crumb = null;
			if (body.getDirection() == CmpConst.Direction_Receive) {
				continue;
			}
			// crumb = getPayVO(body.getPk_settlement(), body.getPk_detail());
			crumb = payVOs.get(body.getPk_settlement() + body.getPrimaryKey());
			if (crumb == null) {
				crumb = new PaymentCrumbVO();

				crumb.setPk_detail(body.getPk_detail());
				crumb.setPk_bill(body.getPk_bill());

				crumb.setPk_billtype(body.getPk_billtype());
				crumb.setPk_group(head.getPk_group());
				crumb.setPk_org(head.getPk_org());
				crumb.setPk_settlement(body.getPk_settlement());
			} else {
				allMap.put(body.getPk_detail(), crumb);
			}
			crumb.setPk_billdetail(body.getPk_billdetail());
			String yurref = transferVO.getMap().get(body.getPrimaryKey());

			if (yurref == null) {
				idmap.put(body.getPk_detail(), body.getPk_billdetail());
				// noList.add(body.getPk_detail());
				continue;
			}
			crumb.setYurref(yurref);
			crumb.setSerial(CmpUtils.getSettleCode(head.getPk_org(), head.getPk_group()));
			crumb.setUpdate_operator(operaterid);
			crumb.setUpdatedate(billdate);
			crumb.setPaystatus(status);
			crumbs.add(crumb);
			// 有参考号的
			yurrefMap.put(body.getPk_detail(), crumb);
		}

		/** 保存支付信息 */
		CrumbOption option = new CrumbOption();
		option.setBulu(true);
		option.setCombin(false);
		if (!CheckException.checkMapIsNull(idmap)) {
			for (String pk_detail : idmap.keySet()) {
				for (SettleKey key : transferVO.getOneToManyMap().keySet()) {
					List<String> list = transferVO.getOneToManyMap().get(key);
					if (list.contains(pk_detail)) {
						for (String a : list) {
							if (yurrefMap.containsKey(a)) {
								PaymentCrumbVO crumbVO = yurrefMap.get(a);
								PaymentCrumbVO pay = null;
								if (allMap.containsKey(pk_detail)) {
									pay = allMap.get(pk_detail);
									pay.setYurref(crumbVO.getYurref());
								} else {
									pay = new PaymentCrumbVO();
									CmpUtils.copy(pay, crumbVO);
									pay.setPrimaryKey(null);
								}
								pay.setPk_detail(pk_detail);
								pay.setPk_billdetail(idmap.get(pk_detail));
								crumbs.add(pay);
							}
						}
					}
				}
			}
		}

		savePayment(crumbs);
		return crumbs;
	}

	public List<PaymentCrumbVO> dealOneToOne(NetpayTransferVO transferVO, int status, String operaterid, UFDate billdate)
			throws BusinessException {
		List<PaymentCrumbVO> crumbs = Lists.newArrayList();
		SettlementHeadVO head = (SettlementHeadVO) transferVO.getAgg().getParentVO();
		// added by zhufeng 2013-9-16 获取paymentvo之前,增加支付指令的校验 start
		checkYurrefStatus(transferVO.getDetails());
		// added by zhufeng 2013-9-16  end
		for (int i = 0; i < transferVO.getDetails().length; i++) {
			SettlementBodyVO body = transferVO.getDetails()[i];
			PaymentCrumbVO crumb = null;

			if (!Direction.PAY.VALUE.equals(body.getDirection())) {
				continue;
			}

			crumb = getPayVO(body.getPk_settlement(), body.getPk_detail());
			if (crumb == null) {
				crumb = new PaymentCrumbVO();

				crumb.setPk_detail(body.getPk_detail());
				crumb.setPk_bill(body.getPk_bill());
				crumb.setPk_billdetail(body.getPk_billdetail());
				crumb.setPk_billtype(body.getPk_billtype());
				crumb.setPk_org(head.getPk_org());
				crumb.setPk_group(head.getPk_group());
				crumb.setPk_settlement(body.getPk_settlement());
			}
			String yurref = transferVO.getMap().get(body.getPrimaryKey());
			crumb.setYurref(yurref);

			crumb.setSerial(CmpUtils.getSettleCode(body.getPk_org(), body.getPk_group()));
			crumb.setUpdate_operator(operaterid);
			crumb.setUpdatedate(billdate);
			crumb.setPaystatus(status);
			crumbs.add(crumb);
		}

		/** 保存支付信息 */
		CrumbOption option = new CrumbOption();
		option.setBulu(true);
		option.setCombin(false);
		// if(isBulu) {
		// getPaymentService().savePaymentCrumb(crumbs, option);
		// }
		savePayment(crumbs);
		return crumbs;
	}

	private void savePayment(List<PaymentCrumbVO> list) throws BusinessException {
		List<PaymentCrumbVO> addList = Lists.newArrayList();
		List<PaymentCrumbVO> updateList = Lists.newArrayList();
		for (PaymentCrumbVO pay : list) {
			if (pay.getPrimaryKey() == null) {
				addList.add(pay);
			} else {
				updateList.add(pay);
			}
		}
		if (!CheckException.checkContionsIsNull(addList)) {
			new BaseDAO().insertVOList(addList);
		}
		if (!CheckException.checkContionsIsNull(updateList)) {
			new BaseDAO().updateVOList(updateList);
		}
	}

	private List<PaymentCrumbVO> deal(NetpayTransferVO transferVO, Map<String, List<SettlementBodyVO>> bodyMap,
			String serial, Map<String, String> map, int status, String operaterid, UFDate busidate)
			throws BusinessException {
		List<PaymentCrumbVO> crumbs = Lists.newLinkedList();
		// String yurref = bodyMap.values()
		for (String code : bodyMap.keySet()) {
			List<SettlementBodyVO> list = bodyMap.get(code);
			// 一个组一个serial1
			// String serial1 =
			SettlementHeadVO head = (SettlementHeadVO) transferVO.getAgg().getParentVO();
			CmpUtils.getSettleCode(head.getPk_org(), head.getPk_group());
			for (SettlementBodyVO body : list) {
				PaymentCrumbVO crumb = null;
				if (body.getDirection() == CmpConst.Direction_Receive) {
					continue;
				}
				// crumb = getPayVO(body.getPk_settlement(),
				// body.getPk_detail());
				crumb = payVOs.get(body.getPk_settlement() + body.getPrimaryKey());
				if (crumb == null) {
					crumb = new PaymentCrumbVO();

					crumb.setPk_detail(body.getPk_detail());
					crumb.setPk_bill(body.getPk_bill());
					crumb.setPk_billdetail(body.getPk_billdetail());
					crumb.setPk_billtype(body.getPk_billtype());
					crumb.setPk_group(body.getPk_group());
					crumb.setPk_org(body.getPk_org());
					crumb.setPk_settlement(body.getPk_settlement());
				}
				// 每一个组一个网银序列号，每组一条指令
				crumb.setYurref(map.get(code));
				// crumb.setSerial(serial);
				crumb.setUpdate_operator(operaterid);
				crumb.setUpdatedate(busidate);
				crumb.setPaystatus(status);
				crumbs.add(crumb);
			}
		}
		// 全部一起支付的为一个serial
		for (PaymentCrumbVO crumb : crumbs) {
			crumb.setSerial(serial);
		}
		CrumbOption option = new CrumbOption();
		option.setBulu(true);
		option.setCombin(false);
		savePayment(crumbs);
		return crumbs;
	}

	public void dealCombim(SettlementAggVO agg, boolean isPerson, String operaterid, UFDate busidate)
			throws BusinessException {
		if (agg.getParentVO() != null) {
			// 没有传递表头是因为是数组，以后设计一下，暂时这么处理
			CmpUtils.updateObm(agg);
			// 表头结算号处理
			getInterfaceLocator().getPaymentService().checkCode(agg);
		}
		getInterfaceLocator().getPaymentService().handleTransferFund(
				CmpUtils.covertArraysToList((SettlementBodyVO[]) agg.getChildrenVO()),
				agg.getTransferVO().getOnlinePaymentVOs(), operaterid);
		// CmpUtils.updateObm(aggVO);
		//
		// getInterfaceLocator().getPaymentService().checkCode(aggVO);
		//
		// return getInterfaceLocator().getPaymentService().handleTransferFund(
		// CmpUtils.covertArraysToList((SettlementBodyVO[])
		// aggVO.getChildrenVO()), vos, operaterid);

	}

	public void savePayment(NetpayTransferVO transferVO, int status, String operaterid, UFDate busidate)
			throws BusinessException {
//		deal(transferVO, transferVO.getBodyMap(), transferVO.getSerial(), transferVO.getMap(), status, operaterid,
//				busidate);
		throw new BusinessException("unsupport method!");

	}
	
	// added by zhufeng 2013-9-14  start
	@Override
	public void savePayment_RequiresNew(NetpayTransferVO transferVO, int status, String operaterid, UFDate busidate)
		throws BusinessException {
		deal(transferVO, transferVO.getBodyMap(), transferVO.getSerial(), transferVO.getMap(), status, operaterid,
				busidate);
	}
	// added by zhufeng 2013-9-14  end

	public void savePaymentAndDealCombim(NetpayTransferVO transferVO, String operaterid, UFDate busidate)
			throws BusinessException {

		Set<String> pk_setttles = new HashSet<String>();
		SettlementBodyVO[] settlementBodyVOs = transferVO.getDetails();
		for (SettlementBodyVO settlementBodyVO : settlementBodyVOs) {
			pk_setttles.add(settlementBodyVO.getPk_settlement());
		}

		SettlementAggVO[] oldAggVOs = NCLocator.getInstance().lookup(ISettlementQueryService.class)
				.querySettlementAggVOsByPks(pk_setttles.toArray(new String[0]));

		// modified by zhufeng 2015-6-26 0、支付成功  1、支付失败   2、不明  3 支付中  4 已经补录过 start
		// 对公对私都设置为支付中
		int status = 3;
		boolean isPerson = false;
		if (CMPBusiUtils.isForPerson(transferVO.getDetails())) {
			isPerson = true;
		}
		// modified by zhufeng 2015-6-26 end
		SettleUtils.addDynamicLock(transferVO.getDetails());

		SettleUtils.checkTs(transferVO.getDetails());
		// modified by zhufeng 2013-9-14 start
		// 增加校验,校验支付的状态
		checkYurrefStatus(transferVO.getDetails());
		// 独立事务保存payment
		NCLocator.getInstance().lookup(ICMPPaymentService.class).savePayment_RequiresNew(transferVO, status, operaterid, busidate);
		// modified by zhufeng 2013-9-14 end
		
		SettlementAggVO newAgg = new SettlementAggVO();
		newAgg.setTransferVO(transferVO);
		newAgg.setChildrenVO(transferVO.getDetails());
		dealCombim(newAgg, isPerson, operaterid, busidate);

		Set<String> pk_settlements = new HashSet<String>();
		for (SettlementBodyVO settlementBodyVO : (SettlementBodyVO[]) newAgg.getChildrenVO()) {

			pk_settlements.add(settlementBodyVO.getPk_settlement());

		}

		SettlementAggVO[] settlementAggVOs = NCLocator.getInstance().lookup(ISettlementQueryService.class)
				.querySettlementAggVOsByPks(pk_settlements.toArray(new String[0]));

		CMPBusinessLogServiceUtil.writeLogs(new BusinessLogContextTranslatorImpl(SettlementHeadVO.BEAN_ID)
				.getBusinessLogContexts(BusinessLogType.SettlementBusinessLogType.COMBINATIONPAY.getValue(),
						settlementAggVOs, oldAggVOs));

	}

	// added by zhufeng 2013-9-14  start
	private void checkYurrefStatus(CircularlyAccessibleValueObject[] settlementBodyVOs) throws BusinessException {
		if (ArrayUtil.isNull(settlementBodyVOs)){
			throw new IllegalArgumentException("===> nc.bs.cmp.netpayment.CMPPaymentServiceImpl.checkYurrefStatus(CircularlyAccessibleValueObject[]) bodys is null!");
		}
		String where = SqlUtils.getInStr(SettlementBodyVO.PK_DETAIL, settlementBodyVOs,
				SettlementBodyVO.PK_DETAIL);
		List<PaymentCrumbVO> list = getInterfaceLocator().getPaymentService().findPaymentCrumbByCondition(
				where);
		if (ArrayUtil.isNull(list)) {
			return;
		}
		Set<String> yurrefSet = new HashSet<String>();
		for (PaymentCrumbVO paymentCrumbVO : list) {
			String yurref = paymentCrumbVO.getYurref();
			if (!StringUtil.isNull(yurref) ) {
				yurrefSet.add(yurref);
			}
		}
		if (!yurrefSet.isEmpty()) {
			// 如果有有效指令,obm直接扔exception
			NCLocator.getInstance().lookup(IEbankLogQueryService.class).checkIsAllowBillBackOperate(yurrefSet.toArray(new String[0]));
		}
	}
	// added by zhufeng 2013-9-14  end

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nc.itf.cmp.ebank.ICMPPaymentService#updatePayStatus(nc.vo.cmp.settlement
	 * .SettlementAggVO)
	 */
	@Override
	public void updatePayStatus(SettlementAggVO aggVo) throws BusinessException {
		IOnlinePayment service = NCLocator.getInstance().lookup(IOnlinePayment.class);

		PayStateQueryVO qvo = getPayStateQueryVO(aggVo);

		service.queryPayState(qvo);
	}

	/**
	 * 构建支付指令状态查询VO
	 * 
	 * @param aggVO
	 * @return
	 * @throws BusinessException
	 * @author jiaweib
	 * @since NC6.0
	 */
	@SuppressWarnings("unchecked")
	private PayStateQueryVO getPayStateQueryVO(SettlementAggVO aggVO) throws BusinessException {
		// 支付参考号
		List<PaymentCrumbVO> crumbs = this.queryCrumb(aggVO.getParentVO().getPrimaryKey());

		if (crumbs == null || crumbs.size() == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("3607set_0",
					"03607set-0416")/* @res "单据没有通过网上支付" */);
		}

		Map<String, String> pk_detailToYurefMap = CmpUtils.getPk_detailToYurefMapByPaymentCrumbVOs(crumbs);
		Collection<String> values = pk_detailToYurefMap.values();
		Set<String> yufefSet = new HashSet<String>();
		for (Iterator iterator = values.iterator(); iterator.hasNext();) {
			String yuref = (String) iterator.next();
			yufefSet.add(yuref);
		}

		// 支付类别
		SettlementBodyVO[] bodys = (SettlementBodyVO[]) aggVO.getChildrenVO();
		String payFunc = null;
		boolean isPerson = CMPBusiUtils.isForPerson(bodys);
		if (isPerson) {
			payFunc = PayStateQueryVO.PAYFUNC_DFDK;
		} else {
			payFunc = PayStateQueryVO.PAYFUNC_ZF;
		}

		PayStateQueryVO qvo = new PayStateQueryVO();
		qvo.setYurrefs(yufefSet.toArray(new String[] {}));
		qvo.setPayFunc(payFunc);
		qvo.setUseid((String) aggVO.getParentVO().getAttributeValue("pk_operator"));
		// qvo.setDbtacc("pk_account");// 付款银行账号字段
		qvo.setPk_corp(bodys[0].getPk_org());
		return qvo;
	}

	/**
	 * 查询支付信息
	 */
	@SuppressWarnings("unchecked")
	public List<PaymentCrumbVO> queryCrumb(String pk_settlement) throws BusinessException {
		String condition = " pk_settlement = '" + pk_settlement + "'";
		Collection<PaymentCrumbVO> collection = new BaseDAO().retrieveByClause(PaymentCrumbVO.class, condition);
		if (!CheckException.checkContionsIsNull(collection)) {
			return Lists.newArrayList(collection);
		}
		return null;
	}

	@Override
	public PaymentRetMsg[] doNetTransfer_RequiresNew(OnlinePaymentVO[] vos, SettlementBodyVO[] codeBodys,
			String operaterid, SettlementAggVO... aggVOs) throws BusinessException {
		// TODO Auto-generated method stub
		return doNetTransfer(vos, codeBodys, operaterid, aggVOs);
	}

	@Override
	public PaymentInfoVO4Batch[] doBatchNetTransfer(NetpayTransferVO[] netPayTransferVOs) throws BusinessException {
		PaymentInfoVO4Batch[] batchInfos = new PaymentInfoVO4Batch[netPayTransferVOs.length];

		for (int i = 0; i < netPayTransferVOs.length; i++) {
			SettlementBodyVO[] clones = CmpUtils.CloneObj((SettlementBodyVO[]) netPayTransferVOs[i].getAgg()
					.getChildrenVO());
			batchInfos[i] = new PaymentInfoVO4Batch();
			try {
				batchInfos[i].setAggvo(netPayTransferVOs[i].getAgg());
				batchInfos[i].setRetMsgs(NCLocator
						.getInstance()
						.lookup(ICMPPaymentService.class)
						.doNetTransfer_RequiresNew(
								netPayTransferVOs[i].getOnlinePaymentVOs(),
								clones, DataUtil.getCurrentUser(),
								netPayTransferVOs[i].getAgg()));
			} catch (Exception e) {
				batchInfos[i].setEx(e);
				Logger.error(e.getMessage(), e);
			}
		}
		return batchInfos;
	}
	@Override
	public void validateCombinPay(SettlementAggVO[] aggvos) throws BusinessException {
		StringBuffer sbmsg = new StringBuffer();
		for (SettlementAggVO settlementAggVO : aggvos) {
			SettlementHeadVO headvo = (SettlementHeadVO) settlementAggVO.getParentVO();

			// 工资发放单据,不能手工结算
			String pk_tradetype = headvo.getPk_tradetype();

			if ("DS".equals(pk_tradetype)) {
				sbmsg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("3607set1_0", "03607set1-0221",
						null, new String[] { headvo.getBillcode() })/* "业务单据号为{0}为工资发放单据,不能进行合并支付！\n */);
			}
		}

		if (sbmsg.length() > 0) {
			throw new BusinessException(sbmsg.toString());
		}

		// 校验支付业务数据
		SettleValidate.validatePayData(aggvos);
		// 是否启用
		SettleValidate.validateOBM(aggvos);
		// 网上支付的单据单据行校验
		SettleValidate.validateNetPayNegativeRow(aggvos);
		// 校验单据状态
		SettleValidate.validateNetPayState(aggvos);
		// 校验供应商是否已经付款冻结
		SettleValidate.validateSupplier(aggvos);
		SettleInternationalValidate.checkInternationalNotSupportedOperate(aggvos);

		CMPValidate.validate(aggvos);
	}

	
	@Override
	public void validatePay(SettlementAggVO[] aggvos) throws BusinessException {

		// 校验OBM是否启用
		SettleValidate.validateOBM(aggvos);
		// 校验支付状态和单据状态
		SettleValidate.validateNetPayState(aggvos);
		// 校验网上支付标题行不能为空，不能为负数
		SettleValidate.validateNetPayNegativeRow(aggvos);
		// 校验结算号规则
		SettleValidate.validateCombin(aggvos, true);
		// 校验供应商是否已经付款冻结
		SettleValidate.validateSupplier(aggvos);

		SettleValidate.dataIntegralityValidate(aggvos);
		SettleInternationalValidate.checkInternationalNotSupportedOperate(aggvos);
		CMPValidate.validate(aggvos);
	}
	
	
	@Override
	public NetpayTransferVO doCombinPayDelOBMLog(SettlementAggVO[] aggs, String operaterid) throws BusinessException {

		NetpayTransferVO transferVO = doCombinPay(aggs, operaterid);

		// 先删除之前的补录信息
		OnlinePaymentVO[] onlinePaymentVo = transferVO.getOnlinePaymentVOs();

		if (onlinePaymentVo.length != 0) {
			String[] yurrefs = new String[onlinePaymentVo.length];

			for (int i = 0; i < yurrefs.length; i++) {
				yurrefs[i] = onlinePaymentVo[i].getYurref();
			}

			NCLocator.getInstance().lookup(nc.pubitf.obm.IObmLogManageService.class)
					.deleteObmLog(LogMgrRequestVO.createDelRequest(yurrefs));
		}
		return transferVO;
	}

	@Override
	public List<NetpayTransferVO> dobeforeFillEBank(SettlementAggVO[] aggvos) throws BusinessException {
		List<NetpayTransferVO>  list = new ArrayList<NetpayTransferVO>();
		 for(SettlementAggVO settlementAggVO : aggvos) {
			 list.add(dobeforeFillEBank(settlementAggVO));
		 }
		
		return list;
	}

	@Override
	public List<PaymentCrumbVO> processPayment(List<NetpayTransferVO> netpayTransferVOList, int[] arithmetics,
			int status, String operaterid, UFDate busidate) throws BusinessException {
		for (int i = 0; i < arithmetics.length; i++) {
			if (arithmetics[i] == CmpConst.ONE_TO_MANY) {

				dealOneToMany(netpayTransferVOList.get(i), status, operaterid, busidate);
			} else {
				dealOneToOne(netpayTransferVOList.get(i), status, operaterid, busidate);
			}
		}
		return null;
	}
}