package nc.vo.arap.vochange;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nc.bs.arap.bill.ArapBillCalUtil;
import nc.bs.arap.bill.ArapBillPubUtil;
import nc.bs.arap.util.ArapBillVOUtils;
import nc.bs.arap.util.BillEuroUtils;
import nc.bs.arap.util.BillMoneyVUtils;
import nc.bs.arap.util.BillOrgVUtils;
import nc.bs.arap.util.IArapBillTypeCons;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.bs.logging.Logger;
import nc.bs.trade.business.HYPubBO;
import nc.fipub.framework.base.FIStringUtil;
import nc.itf.arap.fieldmap.IBillFieldGet;
import nc.itf.uap.pf.IPFBillItfDef;
import nc.itf.uap.pf.IPFConfig;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.arap.basebill.BaseItemVO;
import nc.vo.arap.payable.PayableBillVO;
import nc.vo.arap.pub.BillEnumCollection.BillSatus;
import nc.vo.bd.cust.CustomerVO;
import nc.vo.bd.supplier.SupplierVO;
import nc.vo.cmp.apply.ApplyVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pcm.contractbalance.ContractBalanceHeadVO;
import nc.vo.pf.change.BillItfDefVO;
import nc.vo.pf.change.ChangeVOAdjustContext;
import nc.vo.pf.change.IChangeVOAdjust;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.calculator.data.IRelationForItems;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.trade.checkrule.VOChecker;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 应收应付单据VO对照后单据本币原币计算及汇总
 * 
 * @author wuzhwa
 * @since v6.0
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
public class ArapBillVOChange implements IChangeVOAdjust {

	protected Integer syscode;

	protected String strChange;

	protected String local_money;

	protected IRelationForItems relationforitem;

	protected Log LOG = Log.getInstance(this.getClass());

	/*
	 * VO对照后,修改DestVO
	 * 
	 * @seenc.vo.pf.change.IChangeVOAdjust#adjustAfterChange(nc.vo.pub.
	 * AggregatedValueObject, nc.vo.pub.AggregatedValueObject,
	 * nc.vo.pf.change.ChangeVOAdjustContext)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see nc.vo.pf.change.IChangeVOAdjust#adjustAfterChange(nc.vo.pub.
	 * AggregatedValueObject, nc.vo.pub.AggregatedValueObject,
	 * nc.vo.pf.change.ChangeVOAdjustContext)
	 */
	@Override
	public AggregatedValueObject adjustAfterChange(AggregatedValueObject srcVO,
			AggregatedValueObject destVO, ChangeVOAdjustContext adjustContext)
			throws BusinessException {

		if (destVO.getParentVO().getAttributeValue(IBillFieldGet.PK_GROUP) == null) {
			destVO.getParentVO().setAttributeValue(IBillFieldGet.PK_GROUP,
					InvocationInfoProxy.getInstance().getGroupId());
		}

		ArapBillPubUtil.fillTradeTypeInfo(destVO);
		this.checkTranstype(destVO);
		/*
		 * 如果VO对照前本币数据已经存在，那么不需要做此计算，以免在计算前后金额精度存在差异。 确认当前操作员默认币种
		 * 先判断传入的币种和组织默认币种是否一致 如果不一致，需要计算
		 * 如果一致，看本币有没有传入值，如果有值，那么本币不做计算，如果没有值那么需要计算 如果一致，且表头没有值的话，需要单独做汇总
		 */
		try {
			ArapBillVOUtils
					.prepareRedundancyFields(new AggregatedValueObject[] { destVO });

			ArapBillPubUtil.fillNeedFlds(destVO);
			// 资产的单据，当时已经计算了全局本币和集团本币，不需要再计算全局本币和集团本币。且必须不能计算，因为资产单据生成时候的汇率可能不一样

			if (this.isFillRate()) {
				ArapBillPubUtil.processMoneyWithoutCal(destVO);
			}

			if (destVO.getChildrenVO() != null && destVO.getParentVO() != null) {
				// XBX 结算单->应付单 如果自定义7有值，将档案7的助记码（翻译成用户主键后）放入应付单制单人，创建人
				String yfbill_type = FIStringUtil.coverToString(destVO
						.getParentVO().getAttributeValue(
								PayableBillVO.PK_BILLTYPE));// 应付单单据类型
				String jsbill_type = FIStringUtil.coverToString(srcVO
						.getParentVO().getAttributeValue(
								ContractBalanceHeadVO.BILL_TYPE));// 结算单单据类型
				// 结算->应付
				if ("F1".equals(yfbill_type) && "4D50".equals(jsbill_type)) {
					String hdef7 = FIStringUtil.coverToString(srcVO
							.getParentVO().getAttributeValue(
									ContractBalanceHeadVO.HDEF7));// 结算单
					if (StringUtils.isNotEmpty(hdef7)) {
						String user_code = (String) new HYPubBO().findColValue(
								"bd_defdoc", "mnecode",
								"nvl(dr,0) = 0 and pk_defdoc  = '" + hdef7
										+ "'");
						String cuserid = (String) new HYPubBO().findColValue(
								"sm_user", "cuserid",
								"nvl(dr,0) = 0 and user_code  = '" + user_code
										+ "'");
						if (StringUtils.isNotEmpty(cuserid)) {
							// 制单人
							destVO.getParentVO().setAttributeValue(
									PayableBillVO.BILLMAKER, cuserid);
							// 创建人
							destVO.getParentVO().setAttributeValue(
									PayableBillVO.CREATOR, cuserid);
						}
					}
				}
				// 设置单据日期
				UFDate busidate = new UFDate(InvocationInfoProxy.getInstance()
						.getBizDateTime());
				for (CircularlyAccessibleValueObject item : destVO
						.getChildrenVO()) {
					if (item.getAttributeValue(IBillFieldGet.BILLDATE) == null) {
						item.setAttributeValue(IBillFieldGet.BILLDATE, busidate);
					}
				}
				if (destVO.getParentVO().getAttributeValue(
						IBillFieldGet.BILLDATE) == null) {
					destVO.getParentVO().setAttributeValue(
							IBillFieldGet.BILLDATE, busidate);
				}
				// 设置币种表体第一行币种到表头
				if (destVO.getParentVO().getAttributeValue(
						IBillFieldGet.PK_CURRTYPE) == null
						&& !ArrayUtils.isEmpty(destVO.getChildrenVO())
						&& destVO.getChildrenVO()[0]
								.getAttributeValue(IBillFieldGet.PK_CURRTYPE) != null) {

					destVO.getParentVO()
							.setAttributeValue(
									IBillFieldGet.PK_CURRTYPE,
									destVO.getChildrenVO()[0]
											.getAttributeValue(IBillFieldGet.PK_CURRTYPE));
				}
			}

			// 补充欧盟税务信息

			ArapBillVOUtils
					.prepareSKFKTaxFields(new AggregatedValueObject[] { destVO });

			// add by zmy 进度单传应付时 不重算
			String top_billtypeb = (String) destVO.getChildrenVO()[0]
					.getAttributeValue(IBillFieldGet.TOP_BILLTYPE);
			if (this.isProcessMny()) {
				if (!"4D48".equals(top_billtypeb)) {
					ArapBillPubUtil.processMoney(destVO);
				}

			}

			// 防止收付款单的税的金额被修改了
			ArapBillVOUtils
					.prepareSKFKTaxFields(new AggregatedValueObject[] { destVO });

			// 处理精度
			ArapBillCalUtil.processBillScale(destVO);
			// 设置散户银行账户
			// resetFreecustAccount(srcVO,destVO);

		} catch (Exception e) {
			Logger.error(e);
			throw new BusinessException(e);
		}

		// 处理起算日期
		this.processBusidate(destVO);
		// 设置预占用余额
		this.setOccuPationmny(destVO);
		// 补充其他默认值
		this.setDefaultValues(destVO);
		// 调整单据状态
		this.adjuestBillStatus(destVO);
		// 调整摘要信息
		this.adjuestScomments(destVO);
		// 过滤金额为0的行
		// filterZeroLines(destVO);

		return destVO;

	}

	private void filterZeroLines(AggregatedValueObject destVO)
			throws BusinessException {
		CircularlyAccessibleValueObject[] childrenVO = destVO.getChildrenVO();
		List<CircularlyAccessibleValueObject> childWithoutZero = new ArrayList<CircularlyAccessibleValueObject>();
		for (CircularlyAccessibleValueObject child : childrenVO) {
			UFDouble money = (UFDouble) child
					.getAttributeValue(IBillFieldGet.MONEY_DE);
			if (money == null || money.equals(UFDouble.ZERO_DBL)) {
				money = (UFDouble) child
						.getAttributeValue(IBillFieldGet.MONEY_CR);
			}
			UFDouble taxmoney = (UFDouble) child
					.getAttributeValue(IBillFieldGet.LOCAL_TAX_DE);
			if (taxmoney == null || taxmoney.equals(UFDouble.ZERO_DBL)) {
				taxmoney = (UFDouble) child
						.getAttributeValue(IBillFieldGet.LOCAL_TAX_CR);
			}

			if (money != null && !money.equals(UFDouble.ZERO_DBL)) { // 金额不为0
				childWithoutZero.add(child);
			} else if (taxmoney != null && !taxmoney.equals(UFDouble.ZERO_DBL)) { // 金额为0，税额不为0
				childWithoutZero.add(child);
			}

		}
		if (childWithoutZero.size() == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2006v61020_0",
							"02006v61020-0127"));/* res *因为核销关系，导致表体行为空，保存失败 */
		}
		destVO.setChildrenVO(childWithoutZero.toArray(new BaseItemVO[0]));
		BillMoneyVUtils.sumBodyToHead((BaseBillVO) destVO.getParentVO(),
				(BaseItemVO[]) destVO.getChildrenVO());
	}

	private boolean isFreecust(String pk_billtype, BaseItemVO itemVO) {

		try {
			if (pk_billtype.equals(IBillFieldGet.F3)
					|| pk_billtype.equals(IBillFieldGet.F1)) {
				String supplier = itemVO.getSupplier();
				SupplierVO supplierVO = MDPersistenceService
						.lookupPersistenceQueryService().queryBillOfVOByPK(
								SupplierVO.class, supplier, false);
				if (supplierVO != null) {
					return supplierVO.getIsfreecust() != null
							&& supplierVO.getIsfreecust().booleanValue();
				}
			} else if (pk_billtype.equals(IBillFieldGet.F0)
					|| pk_billtype.equals(IBillFieldGet.F2)) {
				String customer = itemVO.getCustomer();
				CustomerVO customerVO = MDPersistenceService
						.lookupPersistenceQueryService().queryBillOfVOByPK(
								CustomerVO.class, customer, false);
				if (customerVO != null) {
					return customerVO.getIsfreecust() != null
							&& customerVO.getIsfreecust().booleanValue();
				}
			}
		} catch (MetaDataException e) {
			ExceptionHandler.consume(e);
		}
		return false;
	}

	private void adjuestOrgV(AggregatedValueObject[] destVOs) {
		String[] v_orgs = new String[] { IBillFieldGet.PK_ORG_V,
				IBillFieldGet.SETT_ORG_V, IBillFieldGet.SO_ORG_V,
				IBillFieldGet.PU_ORG_V, IBillFieldGet.PK_PCORG_V,
				IBillFieldGet.SO_DEPTID_V, IBillFieldGet.PK_DEPTID_V,
				IBillFieldGet.PU_DEPTID_V };
		for (AggregatedValueObject destVO : destVOs) {
			for (String name : v_orgs) {
				if (destVO.getParentVO() != null) {
					destVO.getParentVO().setAttributeValue(name, null);
				}
				if (destVO.getChildrenVO() != null) {
					for (BaseItemVO item : (BaseItemVO[]) destVO
							.getChildrenVO()) {
						item.setAttributeValue2(name, null);
					}
				}
			}
		}
		try {
			BillOrgVUtils.setOrgV(destVOs);
		} catch (BusinessException e) {
			ExceptionHandler.handleRuntimeException(e);
		}
	}

	protected void adjuestScomments(AggregatedValueObject destVO) {
		String prefix = "ML:";
		if (destVO.getChildrenVO() != null) {
			for (CircularlyAccessibleValueObject item : destVO.getChildrenVO()) {
				String value = (String) item
						.getAttributeValue(IBillFieldGet.SCOMMENT);
				if (StringUtils.isNotEmpty(value) && value.startsWith(prefix)) {
					item.setAttributeValue(
							IBillFieldGet.SCOMMENT,
							nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("common",
											value.substring(prefix.length())));
				}
			}
		}

		String[] fields = new String[] { IBillFieldGet.SCOMMENT,
				IBillFieldGet.INVOICENO };
		for (String field : fields) {
			String value = (String) destVO.getParentVO().getAttributeValue(
					field);
			if (destVO.getChildrenVO() != null
					&& (StringUtils.isEmpty(value) || "~".equals(value))) {
				Set<String> childScomments = new HashSet<String>();
				for (CircularlyAccessibleValueObject item : destVO
						.getChildrenVO()) {
					value = (String) item.getAttributeValue(field);
					if (StringUtils.isEmpty(value) || "~".equals(value)) {
						continue;
					}
					childScomments.add(value);
				}

				Iterator<String> iterator = childScomments.iterator();
				if (iterator.hasNext()) {
					destVO.getParentVO().setAttributeValue(field,
							iterator.next());
				}
			}
		}
	}

	private void processBusidate(AggregatedValueObject destVO) {
		// 设置业务日期
		if (destVO.getChildrenVO() != null) {
			for (BaseItemVO item : (BaseItemVO[]) destVO.getChildrenVO()) {
				if (item.getAttributeValue(IBillFieldGet.BUSIDATE) == null) {
					item.setAttributeValue2(IBillFieldGet.BUSIDATE,
							item.getAttributeValue(IBillFieldGet.BILLDATE));
				}
				if (item.getAttributeValue(IBillFieldGet.ROWTYPE) == null) {
					item.setAttributeValue2(IBillFieldGet.ROWTYPE,
							Integer.valueOf(0));
				}
			}
		}
	}

	private void adjuestBillStatus(AggregatedValueObject destVO) {
		// 调整单据状态， 不能高于审批状态
		Integer billstatus = (Integer) destVO.getParentVO().getAttributeValue(
				IBillFieldGet.BILLSTATUS);
		if (billstatus != null
				&& (billstatus.intValue() == BillSatus.Audit.VALUE.intValue() || billstatus
						.intValue() == BillSatus.Sign.VALUE.intValue())) {
			destVO.getParentVO().setAttributeValue(IBillFieldGet.BILLSTATUS,
					BillSatus.Save.VALUE);
		}
	}

	private void checkTranstype(AggregatedValueObject destVO)
			throws BusinessException {
		BaseBillVO parentVO = (BaseBillVO) destVO.getParentVO();
		if (StringUtils.isEmpty(parentVO.getPk_tradetype())) {
			this.LOG.info(NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"2006arappub0316_0",
					"02006arappub0316-0033",
					null,
					new String[] { parentVO.getPk_tradetypeid(),
							parentVO.getPk_busitype() }));
		}

	}

	@Override
	public AggregatedValueObject adjustBeforeChange(
			AggregatedValueObject srcVO, ChangeVOAdjustContext adjustContext)
			throws BusinessException {
		return srcVO;
	}

	/*
	 * 批量VO对照后
	 * 
	 * @seenc.vo.pf.change.IChangeVOAdjust#batchAdjustAfterChange(nc.vo.pub.
	 * AggregatedValueObject[], nc.vo.pub.AggregatedValueObject[],
	 * nc.vo.pf.change.ChangeVOAdjustContext) 计算必须字段有币种，单据大类，原币金额，
	 */
	@Override
	public AggregatedValueObject[] batchAdjustAfterChange(
			AggregatedValueObject[] srcVOs, AggregatedValueObject[] destVOs,
			ChangeVOAdjustContext adjustContext) throws BusinessException {
		if (destVOs == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2006pub_0", "02006pub-0563")/*
																			 * @res
																			 * "VO数据交换后目的单据VO为Null"
																			 */);
		}

		// 补充欧盟信息
		BillEuroUtils.setEuroInfo(destVOs);
		List<AggregatedValueObject> rtnVOs = new ArrayList();
		for (int i = 0; i < destVOs.length; i++) {
			rtnVOs.add(this.adjustAfterChange(srcVOs[i], destVOs[i],
					adjustContext));
		}

		// 调整多版本取最新的版本
		// 多版本，拉单新生成的单据应保存为最新版本，现在保存的是旧版本
		this.adjuestOrgV(destVOs);

		// 设置业务类型
		this.setBiztype(destVOs, adjustContext.getSrcBilltype());

		// 单据档案过滤
		ArapBillPubUtil.resetDestVODoc(destVOs);

		// 设置默认收付款业务类型
		ArapBillVOUtils.setDefaultRecpaytype(destVOs);

		return rtnVOs.toArray(new AggregatedValueObject[] {});
	}

	/*
	 * 批量VO对照前
	 * 
	 * @seenc.vo.pf.change.IChangeVOAdjust#batchAdjustBeforeChange(nc.vo.pub.
	 * AggregatedValueObject[], nc.vo.pf.change.ChangeVOAdjustContext)
	 */
	@Override
	public AggregatedValueObject[] batchAdjustBeforeChange(
			AggregatedValueObject[] srcVOs, ChangeVOAdjustContext adjustContext)
			throws BusinessException {

		List<AggregatedValueObject> rtnVOs = new ArrayList<AggregatedValueObject>();
		for (int i = 0; i < srcVOs.length; i++) {
			// for (AggregatedValueObject destVO : destVOs) {
			rtnVOs.add(this.adjustBeforeChange(srcVOs[i], adjustContext));
		}
		return rtnVOs.toArray(new AggregatedValueObject[] {});
	}

	protected void setOccuPationmny(AggregatedValueObject vo) {
		Object pk_billtype = vo.getParentVO().getAttributeValue(
				IBillFieldGet.PK_BILLTYPE);
		BaseItemVO[] children = (BaseItemVO[]) vo.getChildrenVO();
		if (IArapBillTypeCons.F0.equals(pk_billtype)
				|| IArapBillTypeCons.F3.equals(pk_billtype)) {
			for (BaseItemVO item : children) {
				item.setOccupationmny(item.getMoney_de());
				item.setMoney_bal(item.getMoney_de());
			}

		} else if (IArapBillTypeCons.F1.equals(pk_billtype)
				|| IArapBillTypeCons.F2.equals(pk_billtype)) {
			for (BaseItemVO item : children) {
				item.setOccupationmny(item.getMoney_cr());
				item.setMoney_bal(item.getMoney_cr());
			}
		}
	}

	protected void setBiztype(AggregatedValueObject[] destVOs,
			String src_billtype) {
		if (!this.isSetBiztype() || VOChecker.isEmpty(destVOs)) {
			return;
		}

		IPFBillItfDef itfDefService = NCLocator.getInstance().lookup(
				IPFBillItfDef.class);
		BillItfDefVO defVO = new BillItfDefVO();
		defVO.setDest_billtype((String) destVOs[0].getParentVO()
				.getAttributeValue(IBillFieldGet.PK_BILLTYPE));
		defVO.setPk_group((String) destVOs[0].getParentVO().getAttributeValue(
				IBillFieldGet.PK_GROUP));
		defVO.setSrc_billtype(src_billtype);
		try {
			BillItfDefVO[] billItfDefVOs = itfDefService.getBillItfDef(defVO);
			if (!ArrayUtils.isEmpty(billItfDefVOs)) {
				for (AggregatedValueObject vo : destVOs) {
					vo.getParentVO().setAttributeValue(
							IBillFieldGet.PK_BUSITYPE, null);
				}
			}
		} catch (BusinessException e) {
			ExceptionUtils.wrappException(e);
		}

		// 匹配业务流程
		String billtype = (String) destVOs[0].getParentVO().getAttributeValue(
				IBillFieldGet.PK_BILLTYPE);
		String vtrantype = (String) destVOs[0].getParentVO().getAttributeValue(
				IBillFieldGet.PK_TRADETYPE);
		String pk_org = (String) destVOs[0].getParentVO().getAttributeValue(
				IBillFieldGet.PK_ORG);
		String userid = InvocationInfoProxy.getInstance().getUserId();
		if (billtype != null && billtype.trim().length() > 0 && pk_org != null
				&& pk_org.trim().length() > 0) {
			try {
				String cbiztypeid = NCLocator
						.getInstance()
						.lookup(IPFConfig.class)
						.retBusitypeCanStart(billtype, vtrantype, pk_org,
								userid);
				if (null == cbiztypeid) {
					if (!billtype.equals(IBillFieldGet.E0)
							&& !billtype.equals(IBillFieldGet.E1)) {
						String msg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
								.getStrByID("2006pub_0", "02006pub-0652"/** 单据类型 */
								)
								+ billtype
								+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
										.getStrByID("2006pub_0",
												"02006pub-0653"/** 交易类类型 */
										)
								+ (vtrantype == null ? "" : vtrantype);
						ExceptionUtils
								.wrappBusinessException(nc.vo.ml.NCLangRes4VoTransl
										.getNCLangRes().getStrByID("2006pub_0",
												"02006pub-0564")/*
																 * @res
																 * "当前单据没有业务流程且没有匹配到默认自制流程，请检查流程平台配置！"
																 */
										+ "," + msg);
					}
				} else {
					for (AggregatedValueObject vo : destVOs) {
						if (null == cbiztypeid
								|| cbiztypeid.equals("~")
								|| vo.getParentVO().getAttributeValue(
										IBillFieldGet.PK_BUSITYPE) == null) {
							vo.getParentVO().setAttributeValue(
									IBillFieldGet.PK_BUSITYPE, cbiztypeid);
						}
					}
				}
			} catch (BusinessException e) {
				ExceptionUtils.wrappException(e);
			}
		}
	}

	protected void setDefaultValues(AggregatedValueObject vo) {
		return;
	}

	protected boolean isSetBiztype() {
		return true;
	}

	protected boolean isProcessMny() {
		return true;
	}

	protected boolean isFillRate() {
		return false;
	}

}
