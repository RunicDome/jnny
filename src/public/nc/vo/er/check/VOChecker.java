package nc.vo.er.check;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.erm.matterapp.check.VOStatusChecker;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.bs.erm.util.ErUtil;
import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.pf.pub.PfDataCache;
import nc.itf.arap.pub.IBxUIControl;
import nc.itf.bd.bankacc.subinfo.IBankAccSubInfoQueryService;
import nc.itf.fi.pub.Currency;
import nc.itf.fi.pub.SysInit;
import nc.itf.org.IOrgConst;
import nc.itf.uap.pf.IPFConfig;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pubitf.accperiod.AccountCalendar;
import nc.pubitf.erm.erminit.IErminitQueryService;
import nc.pubitf.org.IOrgUnitPubService;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.uapbd.ISupplierPubService;
import nc.util.erm.costshare.ErmForCShareUtil;
import nc.utils.crosscheckrule.FipubCrossCheckRuleChecker;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.bd.bankaccount.BankAccSubVO;
import nc.vo.bd.period.AccperiodVO;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.bd.supplier.finance.SupFinanceVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKHeaderVO;
import nc.vo.ep.bx.Paytarget;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ContrastBusinessException;
import nc.vo.er.exception.ContrastBusinessException.ContrastBusinessExceptionType;
import nc.vo.er.exception.CrossControlMsgException;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.util.ErmBillCalUtil;
import nc.vo.er.util.UFDoubleTool;
import nc.vo.erm.accruedexpense.AccruedVerifyVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.termendtransact.DataValidateException;
import nc.vo.erm.util.ImageCheckUtil;
import nc.vo.fipub.utils.KeyLock;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.util.BDVersionValidationUtil;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class VOChecker {

	private List<String> notRepeatFields;

	
	public void checkImage(Object obj) throws BusinessException {
		new ImageCheckUtil().checkImage(new Object[] { obj });
	}

	
	public static void prepare(JKBXVO bxvo) throws BusinessException {
		JKBXHeaderVO parentVO = bxvo.getParentVO();
		try {
			// 营改增防止出现空指针
			updatePrepareTax(bxvo);
			prepareForNullJe(bxvo);
			if ("iweb".equals(bxvo.getParentVO().getSrcsystem())) {
				prepareBusItemvoForIweb(bxvo);
				prepareHeaderForIweb(bxvo);
			} else {
				// 添加nc端同步表头币种到表体。（sz）
				prepareBusItemvo(bxvo);
				prepareHeader(parentVO, bxvo.getContrastVO());
			}
		} catch (ValidationException e) {
			if (!parentVO.isInit() && !(parentVO.getDjzt().intValue() == BXStatusConst.DJZT_TempSaved)) {
				throw ExceptionHandler.handleException(e);
			}
		}
	}

	
	private static void updatePrepareTax(JKBXVO bxvo) {
		if (bxvo == null) {
			return;
		}

		JKBXHeaderVO parentVO = bxvo.getParentVO();
		BXBusItemVO[] bodyItems = bxvo.getChildrenVO();

		if (parentVO == null || bodyItems == null || bodyItems.length == 0) {
			return;
		}

		if ((parentVO.getVat_amount() == null || parentVO.getVat_amount().compareTo(UFDouble.ZERO_DBL) == 0)
				&& (parentVO.getTax_amount() == null || parentVO.getTax_amount().compareTo(UFDouble.ZERO_DBL) == 0)
				&& (parentVO.getTni_amount() == null || parentVO.getTni_amount().compareTo(UFDouble.ZERO_DBL) == 0)) {
			parentVO.setVat_amount(parentVO.getYbje());
			parentVO.setOrgvat_amount(parentVO.getBbje());
			parentVO.setGroupvat_amount(parentVO.getGroupbbje());
			parentVO.setGlobalvat_amount(parentVO.getGlobalbbje());

			parentVO.setTni_amount(parentVO.getYbje());
			parentVO.setOrgtni_amount(parentVO.getBbje());
			parentVO.setGrouptni_amount(parentVO.getGroupbbje());
			parentVO.setGlobaltni_amount(parentVO.getGlobalbbje());

			parentVO.setTax_amount(parentVO.getYbje().sub(parentVO.getYbje()));
			parentVO.setOrgtax_amount(parentVO.getBbje().sub(parentVO.getBbje()));
			parentVO.setGrouptax_amount(parentVO.getGroupbbje().sub(parentVO.getGroupbbje()));
			parentVO.setGlobaltax_amount(parentVO.getGlobalbbje().sub(parentVO.getGlobalbbje()));
		}

		for (int i = 0; i < bodyItems.length; i++) {
			BXBusItemVO body = bodyItems[i];
			if ((body.getVat_amount() == null || body.getVat_amount().compareTo(UFDouble.ZERO_DBL) == 0)
					&& (body.getTax_amount() == null || body.getTax_amount().compareTo(UFDouble.ZERO_DBL) == 0)
					&& (body.getTni_amount() == null || body.getTni_amount().compareTo(UFDouble.ZERO_DBL) == 0)) {
				body.setVat_amount(body.getYbje());
				body.setOrgvat_amount(body.getBbje());
				body.setGroupvat_amount(body.getGroupbbje());
				body.setGlobalvat_amount(body.getGlobalbbje());

				body.setTni_amount(body.getYbje());
				body.setOrgtni_amount(body.getBbje());
				body.setGrouptni_amount(body.getGroupbbje());
				body.setGlobaltni_amount(body.getGlobalbbje());

				body.setTax_amount(body.getYbje().sub(body.getYbje()));
				body.setOrgtax_amount(body.getBbje().sub(body.getBbje()));
				body.setGrouptax_amount(body.getGroupbbje().sub(body.getGroupbbje()));
				body.setGlobaltax_amount(body.getGlobalbbje().sub(body.getGlobalbbje()));
			}
		}
	}

	
	private void chkIsMustContrast(JKBXVO bxvo) throws BusinessException {
		JKBXHeaderVO headVo = bxvo.getParentVO();

		if (BXConstans.BX_DJDL.equals(headVo.getDjdl()) && headVo.getDjzt() != null
				&& headVo.getDjzt().intValue() == BXStatusConst.DJZT_Saved) {
			if (headVo.getYbje() != null && headVo.getYbje().doubleValue() >= 0) {
				// added by chendya 单据已有冲借款金额，则不再提示
				if (headVo.getCjkybje() != null && headVo.getCjkybje().compareTo(new UFDouble(0.00)) != 0) {
					return;
				}

				// 报销单是否必须冲借款参数
				boolean paramIsMustContrast = false;
				try {
					paramIsMustContrast = SysInit.getParaBoolean(headVo.getPk_org(),
							BXParamConstant.PARAM_IS_FORCE_CONTRAST).booleanValue();
				} catch (java.lang.Throwable e) {
					ExceptionHandler.consume(e);
				}

				if (paramIsMustContrast) {
					// 本人是否有借款单
//					final boolean hasJKD = NCLocator.getInstance().lookup(IBxUIControl.class)
//							.getJKD(bxvo, headVo.getDjrq(), null).size() > 0;
					final boolean hasJKD = NCLocator.getInstance().lookup(IBxUIControl.class)
							.getPersonalJKD(bxvo, headVo.getDjrq(), null).size() > 0;
					if (hasJKD) {
						throw new ContrastBusinessException(ContrastBusinessExceptionType.FORCE,
								nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UPP2011V57-000002"));
					}
				}
			}
		}
	}

	
	private void prepareAccPeriodBack(JKBXHeaderVO parentVO) throws BusinessException {
		// 设置单据会计年度和会计期间
		if (!parentVO.getQcbz().booleanValue() && !parentVO.isInit()) {
			AccountCalendar calendar = AccountCalendar.getInstanceByPk_org(parentVO.getPk_org());

			if (parentVO.getPk_org() != null && calendar == null) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0",
						"02011v61013-0060"));
			}

			if (null != parentVO.getDjrq()) {
				calendar.setDate(parentVO.getDjrq());
			}
			AccperiodVO accperiod = calendar.getYearVO();
			accperiod.setAccperiodmonth(new AccperiodmonthVO[] { calendar.getMonthVO() });
			parentVO.setKjnd(accperiod.getPeriodyear()); // 单据会计年度
			parentVO.setKjqj(accperiod.getAccperiodmonth()[0].getAccperiodmth());// 单据会计期间
		}
	}

	
	private void prepareBackGround(JKBXVO bxvo) throws BusinessException {

		// 设置会计期间
		prepareAccPeriodBack(bxvo.getParentVO());

		// 添加业务流程
		prepareBusinessType(bxvo);
	}

	private static void prepareForNullJe(JKBXVO bxvo) {
		JKBXHeaderVO parentVO = bxvo.getParentVO();
		String[] jeField = JKBXHeaderVO.getJeField();
		String[] bodyJeField = BXBusItemVO.getBodyJeFieldForDecimal();
		for (String field : jeField) {
			if (parentVO.getAttributeValue(field) == null) {
				parentVO.setAttributeValue(field, UFDouble.ZERO_DBL);
			}
		}

		for (String field : bodyJeField) {
			BXBusItemVO[] bxBusItemVOS = bxvo.getBxBusItemVOS();
			if (bxBusItemVOS != null) {
				for (BXBusItemVO item : bxBusItemVOS) {
					if (item.getAttributeValue(field) == null) {
						item.setAttributeValue(field, UFDouble.ZERO_DBL);
					}
				}
			}
		}
	}

	public static Map<String, List<String>> getCrossItems(JKBXVO bxvo) {
		JKBXHeaderVO parentVO = bxvo.getParentVO();
		BusiTypeVO busTypeVO = BXUtil.getBusTypeVO(parentVO.getDjlxbm(), parentVO.getDjdl());

		String fydwbm = parentVO.getFydwbm();
		String zfdwbm = parentVO.getPk_org();
		String corp = parentVO.getDwbm();

		Map<String, List<String>> corpItems = new HashMap<String, List<String>>();

		List<String> useentity_billitems = busTypeVO.getUseentity_billitems();
		List<String> payentity_billitems = busTypeVO.getPayentity_billitems();
		List<String> costentity_billitems = busTypeVO.getCostentity_billitems();

		changeItemsToMap(useentity_billitems, corp, corpItems);
		changeItemsToMap(payentity_billitems, zfdwbm, corpItems);
		changeItemsToMap(costentity_billitems, fydwbm, corpItems);
		return corpItems;
	}

	private static void changeItemsToMap(List<String> busiitems, String corp, Map<String, List<String>> corpItems) {

		List<String> newItems = new ArrayList<String>();
		newItems.addAll(busiitems);

		if (!corpItems.containsKey(corp)) {
			corpItems.put(corp, newItems);
		} else {
			List<String> items = corpItems.get(corp);
			items.addAll(newItems);
			corpItems.put(corp, items);
		}
	}

	
	public void checkUpdateSave(JKBXVO vo) throws BusinessException {
		JKBXHeaderVO headvo = vo.getParentVO();
		if (!headvo.getDjzt().equals(BXStatusConst.DJZT_TempSaved)) {
			// 修改单据时，状态控制
			String msgs = null;
			if (headvo.getQcbz().booleanValue()) {// 期初单据
				msgs = VOStatusChecker.checkBillStatus(headvo.getDjzt(), ActionUtils.EDIT, new int[] {
						BXStatusConst.DJZT_Saved, BXStatusConst.DJZT_TempSaved, BXStatusConst.DJZT_Sign });
			} else {
				msgs = VOStatusChecker.checkBillStatus(headvo.getDjzt(), ActionUtils.EDIT, new int[] {
						BXStatusConst.DJZT_Saved, BXStatusConst.DJZT_TempSaved });
			}

			if (msgs != null && msgs.trim().length() != 0) {
				throw new DataValidateException(msgs);
			}
		}
		checkkSaveBackground(vo);
	}

	
	public void checkkSaveBackground(JKBXVO vo) throws BusinessException {
		JKBXHeaderVO headVO = vo.getParentVO();

		if (!headVO.isInit()) {

			// 税金校验
			if (headVO.getDjdl().equals(BXConstans.BX_DJDL) && !headVO.getDjlxbm().equals("2647")
					&& !headVO.getDjlxbm().equals("264a") && headVO.getDjzt() != 0) {
				checkTax(vo);
			}
			// 校验交易类型
			checkTradetype(headVO);
			// 业务日期与费用日期校验
			checkMtAppDate(vo);
			// 拉单的单据表体行金额不能<=0
			checkBusitemAmountFromMtapp(vo);
			// 申请人与借款报销人是否必须一致
			checkIsSamePerson(vo);
			// 校验单据是否是要必须申请的单据
			checkFromMtApp(vo);
			// 冲借款加锁
			addContrastLock(vo);
			// 冲借款版本校验
			checkContrastJkTs(vo);

			// 暂存不校验
			if (BXStatusConst.DJZT_TempSaved != headVO.getDjzt()) {

				if (headVO.getQcbz() != null && headVO.getQcbz().booleanValue()) {
					// 校验期初是否关闭
					checkQCClose(headVO.getPk_org());
				}

				// 校验表头合法性
				checkValidHeader(headVO);

				// 校验表体合法性
				checkValidChildrenVO(vo);

				// 校验分摊明细信息
				checkCShareDetail(vo);

				// 财务核报容差校验
				checkFinRange(vo);

				// 分摊页签必填情况的校验
				checkCostSharePageNotNull(vo);

				// 校验表头金额
				checkHeadItemJe(vo);

				// 财务金额校验
				checkValidFinItemVO(vo);

				// 校验待摊信息
				checkExpamortizeinfo(vo);

				// 校验冲借款
				checkBillContrast(vo);

				// 交叉校验
//				doCrossCheck(vo);

				// 校验审批流起点人不能为空
				checkAuditMan(vo);

				// 校验单据日期
				checkBillDate(headVO);

				// 校验汇率不能为0
				checkCurrencyRate(headVO);

				// 报销类型为费用调整的单据，不处理结算、冲借款、核销预提等业务
				if (!headVO.isAdjustBxd()) {
					// 是否必须冲借款校验
					chkIsMustContrast(vo);
				}

				// 检查银行账号对应的币种和单据上的币种是否一致
				chkBankAccountCurrency(vo);

				// 检查个人银行账户和客商银行账户不能同时有值
				// checkBankAccount(vo);636注销

				// 校验供应商冻结标志
				chkCustomerPayFreezeFlag(vo);

				// 收款人和供应商不能同时有值
				// checkToPublicPay(vo); 636注销

				// v6.1新增 检查单位银行帐号和现金帐户不能同时有值
				checkCashaccountAndFkyhzh(headVO);

				// 报销核销预提单时，报销金额必须等于总核销预提金额
				checkAccruedVerify(vo);

				// 校验收款对象相关信息:对与还款单和调整单不做处理
				checkBillPaytargetInfo(vo);
			}

			// 后台赋初始值
			prepareBackGround(vo);
			// 设置业务流程
			getBusitype(headVO);
		}
	}

	
	private void checkTax(JKBXVO vo) throws BusinessException {

		JKBXHeaderVO parentvo = vo.getParentVO();
		BXBusItemVO[] childrenVO = vo.getChildrenVO();

		UFDouble headVat_amount = parentvo.getVat_amount() == null ? UFDouble.ZERO_DBL : parentvo.getVat_amount();
		UFDouble headTax_amount = parentvo.getTax_amount() == null ? UFDouble.ZERO_DBL : parentvo.getTax_amount();
		UFDouble headtni_amount = parentvo.getTni_amount() == null ? UFDouble.ZERO_DBL : parentvo.getTni_amount();
		UFDouble headOrgVat_amount = parentvo.getOrgvat_amount() == null ? UFDouble.ZERO_DBL : parentvo
				.getOrgvat_amount();
		UFDouble headOrgTax_amount = parentvo.getOrgtax_amount() == null ? UFDouble.ZERO_DBL : parentvo
				.getOrgtax_amount();
		UFDouble headOrgTni_amount = parentvo.getOrgtni_amount() == null ? UFDouble.ZERO_DBL : parentvo
				.getOrgtni_amount();
		UFDouble headGroupVat_amount = parentvo.getGroupvat_amount() == null ? UFDouble.ZERO_DBL : parentvo
				.getGroupvat_amount();
		UFDouble headGroupTax_amount = parentvo.getGrouptax_amount() == null ? UFDouble.ZERO_DBL : parentvo
				.getGrouptax_amount();
		UFDouble headGroupTni_amount = parentvo.getGrouptni_amount() == null ? UFDouble.ZERO_DBL : parentvo
				.getGrouptni_amount();
		UFDouble headGlobalVat_amount = parentvo.getGlobalvat_amount() == null ? UFDouble.ZERO_DBL : parentvo
				.getGlobalvat_amount();
		UFDouble headGlobalTax_amount = parentvo.getGlobaltax_amount() == null ? UFDouble.ZERO_DBL : parentvo
				.getGlobaltax_amount();
		UFDouble headGlobalTni_amount = parentvo.getGlobaltni_amount() == null ? UFDouble.ZERO_DBL : parentvo
				.getGlobaltni_amount();

		if (headVat_amount.compareTo(UFDouble.ZERO_DBL) < 0) {
			throw new BusinessException("表头含税金额不能小于0");
		}
		if (headTax_amount.compareTo(UFDouble.ZERO_DBL) < 0) {
			throw new BusinessException("表头税金不能小于0");
		}
		if (headtni_amount.compareTo(UFDouble.ZERO_DBL) < 0) {
			throw new BusinessException("表头不含税金额不能小于0");
		}

		if (headVat_amount.compareTo(headTax_amount.add(headtni_amount)) != 0) {
			throw new BusinessException("表头含税金额不等于税金加不含税金额！");
		}
		if(null!=parentvo && !"iweb".equals(parentvo.getSrcsystem())){//来源于轻量端的单据不做校验
			if (headOrgVat_amount.compareTo(headOrgTax_amount.add(headOrgTni_amount)) != 0) {
				throw new BusinessException("表头含税本币金额不等于税金本币金额加不含税本币金额！");
			}
			if (headGroupVat_amount.compareTo(headGroupTax_amount.add(headGroupTni_amount)) != 0) {
				throw new BusinessException("表头含税集团本币金额不等于税金集团本币金额加不含税本币金额！");
			}
			if (headGlobalVat_amount.compareTo(headGlobalTax_amount.add(headGlobalTni_amount)) != 0) {
				throw new BusinessException("表头含税全局本币金额不等于税金全局本币金额加不含税全局本币金额！");
			}			
		}

		boolean paramIncludeTax = SysInit.getParaBoolean(parentvo.getPk_org(), BXParamConstant.PARAM_IS_INCLUDE_TAX)
				.booleanValue();
		if (childrenVO != null && childrenVO.length > 0) {
			for (BXBusItemVO bxBusItemVO : childrenVO) {

				// 参数校验
				if (paramIncludeTax) {
					if (bxBusItemVO.getAmount().compareTo(bxBusItemVO.getVat_amount()) != 0) {
						throw new BusinessException("费用金额（amount)是否为含税金额参数值和单据的实际数据不匹配，请重新录入数据");
					}
				} else {
					if (bxBusItemVO.getAmount().compareTo(bxBusItemVO.getTni_amount()) != 0) {
						throw new BusinessException("费用金额（amount)是否为含税金额参数值和单据的实际数据不匹配，请重新录入数据");
					}

				}

				UFDouble bodyVat_amount = bxBusItemVO.getVat_amount() == null ? UFDouble.ZERO_DBL : bxBusItemVO
						.getVat_amount();
				UFDouble bodyTax_amount = bxBusItemVO.getTax_amount() == null ? UFDouble.ZERO_DBL : bxBusItemVO
						.getTax_amount();
				UFDouble bodytni_amount = bxBusItemVO.getTni_amount() == null ? UFDouble.ZERO_DBL : bxBusItemVO
						.getTni_amount();
				UFDouble bodyOrgVat_amount = bxBusItemVO.getOrgvat_amount() == null ? UFDouble.ZERO_DBL : bxBusItemVO
						.getOrgvat_amount();
				UFDouble bodyOrgTax_amount = bxBusItemVO.getOrgtax_amount() == null ? UFDouble.ZERO_DBL : bxBusItemVO
						.getOrgtax_amount();
				UFDouble bodyOrgTni_amount = bxBusItemVO.getOrgtni_amount() == null ? UFDouble.ZERO_DBL : bxBusItemVO
						.getOrgtni_amount();
				UFDouble bodyGroupVat_amount = bxBusItemVO.getGroupvat_amount() == null ? UFDouble.ZERO_DBL
						: bxBusItemVO.getGroupvat_amount();
				UFDouble bodyGroupTax_amount = bxBusItemVO.getGrouptax_amount() == null ? UFDouble.ZERO_DBL
						: bxBusItemVO.getGrouptax_amount();
				UFDouble bodyGroupTni_amount = bxBusItemVO.getGrouptni_amount() == null ? UFDouble.ZERO_DBL
						: bxBusItemVO.getGrouptni_amount();
				UFDouble bodyGlobalVat_amount = bxBusItemVO.getGlobalvat_amount() == null ? UFDouble.ZERO_DBL
						: bxBusItemVO.getGlobalvat_amount();
				UFDouble bodyGlobalTax_amount = bxBusItemVO.getGlobaltax_amount() == null ? UFDouble.ZERO_DBL
						: bxBusItemVO.getGlobaltax_amount();
				UFDouble bodyGlobalTni_amount = bxBusItemVO.getGlobaltni_amount() == null ? UFDouble.ZERO_DBL
						: bxBusItemVO.getGlobaltni_amount();
				if (bodyVat_amount.compareTo(UFDouble.ZERO_DBL) < 0) {
					throw new BusinessException("表体含税金额不能小于0");
				}
				if (bodyTax_amount.compareTo(UFDouble.ZERO_DBL) < 0) {
					throw new BusinessException("表体税金不能小于0");
				}
				if (bodytni_amount.compareTo(UFDouble.ZERO_DBL) < 0) {
					throw new BusinessException("表体不含税金额不能小于0");
				}

				if (bodyVat_amount.compareTo(bodyTax_amount.add(bodytni_amount)) != 0) {
					throw new BusinessException("表体含税金额不等于税金加不含税金额！");
				}
				if (bodyOrgVat_amount.compareTo(bodyOrgTax_amount.add(bodyOrgTni_amount)) != 0) {
					throw new BusinessException("表体含税本币金额不等于税金本币金额加不含税本币金额！");
				}
				if (bodyGroupVat_amount.compareTo(bodyGroupTax_amount.add(bodyGroupTni_amount)) != 0) {
					throw new BusinessException("表体含税集团本币金额不等于税金集团本币金额加不含税本币金额！");
				}
				if (bodyGlobalVat_amount.compareTo(bodyGlobalTax_amount.add(bodyGlobalTni_amount)) != 0) {
					throw new BusinessException("表体含税全局本币金额不等于税金全局本币金额加不含税全局本币金额！");
				}
			}
		}

	}

	
	private void checkTradetype(JKBXHeaderVO parentVo) throws BusinessException {
		DjLXVO tradeTypeVo = ErmDjlxCache.getInstance().getDjlxVO(parentVo.getPk_group(), parentVo.getDjlxbm());
		if (tradeTypeVo == null || tradeTypeVo.getFcbz().booleanValue()) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
					"UPP2011-000974")
			);
		}
	}

	private void checkFromMtApp(JKBXVO vo) throws BusinessException {
		if (vo.getParentVO().getPk_item() != null) {
			return;
		}

		// 检查是否必须申请：不是期初和常用单据时才校验
		String djlxbm = vo.getParentVO().getDjlxbm();
		if (!vo.getParentVO().isInit() && !djlxbm.equals(BXConstans.BILLTYPECODE_RETURNBILL)
				&& !vo.getParentVO().getQcbz().booleanValue()) {
			String pk_group = InvocationInfoProxy.getInstance().getGroupId();
			DjLXVO djlxVO = ErmDjlxCache.getInstance().getDjlxVO(pk_group, djlxbm);
			UFBoolean isMactrl = djlxVO.getIs_mactrl();
			if (isMactrl != null && isMactrl.booleanValue()) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000925")
				);
			}
		}
	}

	
	public void checkUnAudit(JKBXVO vo) throws BusinessException {
		// 636预提单支持按预计余额多次红冲，所以下游单据仍可逆操作
		// AccruedVerifyVO[] verifyvos = vo.getAccruedVerifyVO();
		// List<String> accruedBillPks = new ArrayList<String>();
		// if (verifyvos != null && verifyvos.length > 0) {
		// for (AccruedVerifyVO verifyvo : verifyvos) {
		// accruedBillPks.add(verifyvo.getPk_accrued_bill());
		// }
		// }
		// if (accruedBillPks.size() > 0) {
		// AggAccruedBillVO[] aggvos =
		// NCLocator.getInstance().lookup(IErmAccruedBillQuery.class).queryBillByPks(
		// accruedBillPks.toArray(new String[accruedBillPks.size()]), true);
		// if (aggvos != null && aggvos.length > 0) {
		// for (AggAccruedBillVO aggvo : aggvos) {
		// if (aggvo.getParentVO().getRedflag() != null &&
		// aggvo.getParentVO().getRedflag() ==
		// ErmAccruedBillConst.REDFLAG_REDED) {
		// throw new
		// BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
		// "expensepub_0", "02011002-0195")
		// + aggvo.getParentVO().getBillno()
		// + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
		// .getStrByID("expensepub_0", "02011002-0196"));
		// }
		// }
		// }
		// }

	}

	
	public void checkInvalid(JKBXVO vo) throws BusinessException {
		// 作废单据时，状态控制
		String msgs = VOStatusChecker.checkBillStatus(vo.getParentVO().getDjzt(), ActionUtils.INVALID,
				new int[] { BXStatusConst.DJZT_Saved });
		if (msgs != null && msgs.trim().length() != 0) {
			throw new DataValidateException(msgs);
		}
		// 关帐校验
		VOChecker.checkErmIsCloseAcc(vo);
	}

	
	private void checkAccruedVerify(JKBXVO bxvo) throws BusinessException {
		AccruedVerifyVO[] accruedVerifyVOs = bxvo.getAccruedVerifyVO();
		if (accruedVerifyVOs != null && accruedVerifyVOs.length > 0) {
			UFDouble total_amount = UFDouble.ZERO_DBL;
			for (int i = 0; i < accruedVerifyVOs.length; i++) {
				total_amount = UFDoubleTool.sum(total_amount, accruedVerifyVOs[i].getVerify_amount());
			}
			if (total_amount.compareTo(bxvo.getParentVO().getYbje()) != 0) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0194"));
			}
		}

	}

	
	private void checkCostSharePageNotNull(JKBXVO vo) throws BusinessException {
		if (BXConstans.BX_DJDL.equals(vo.getParentVO().getDjdl()) && vo.getParentVO().getIsmashare() != null
				&& vo.getParentVO().getIsmashare().booleanValue()) {
			if (vo.getcShareDetailVo() == null || vo.getcShareDetailVo().length == 0) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0183"));
			}
		}
		JKBXHeaderVO headVo = vo.getParentVO();
		if (headVo.isAdjustBxd()) {
			// 报销类型为费用调整的单据，分摊明细页签不允许为空
			if (vo.getcShareDetailVo() == null || vo.getcShareDetailVo().length == 0) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0191"));
			}
		}
	}

	
	private void checkIsSamePerson(JKBXVO vo) throws BusinessException {
		if (vo.getParentVO().getPk_item() != null) {
			String billmaker = null;
			if (vo.getMaheadvo() != null) {
				billmaker = vo.getMaheadvo().getBillmaker();
			}
			String jkbxr = vo.getParentVO().getJkbxr();
			UFBoolean para = SysInit.getParaBoolean(vo.getParentVO().getPk_org(), BXParamConstant.PARAM_IS_SMAE_PERSON);
			if (para != null && para.booleanValue() && billmaker != null && !billmaker.equals(jkbxr)) {
				if (BXConstans.BX_DJDL.equals(vo.getParentVO().getDjdl())) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0181"));
				} else if (BXConstans.JK_DJDL.equals(vo.getParentVO().getDjdl())) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0180"));
				}
			}
		}
	}

	// private void checkBankAccount(JKBXVO vo) throws BusinessException {
	// String skyhzh = vo.getParentVO().getSkyhzh();
	// String custaccount = vo.getParentVO().getCustaccount();
	// if (skyhzh != null && skyhzh.trim() != null && custaccount != null &&
	// custaccount.trim() != null) {
	// throw new
	// BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
	// "02011002-0179"));
	//
	// }
	// }

	
	private void checkBusitemAmountFromMtapp(JKBXVO vo) throws BusinessException {
		// 拉单的单据表体行金额不能为负数
		if (vo.getParentVO().getPk_item() != null) {
			for (BXBusItemVO child : vo.getBxBusItemVOS()) {
				if (child.getAmount().compareTo(UFDouble.ZERO_DBL) <= 0 && child.getPk_item() != null) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0170"));
				}
			}
		}
	}

	private void checkMtAppDate(JKBXVO vo) throws BusinessException {
		// 有拉单时，校验业务日期和费用申请单日期
		if (vo.getMaheadvo() != null) {
			UFDate busiDate = vo.getParentVO().getDjrq();
			if (vo.getMaheadvo().getBillstatus() != BXStatusConst.DJZT_Sign) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0184"));
			}

			if (vo.getMaheadvo().getApprovetime() != null && vo.getMaheadvo().getApprovetime().afterDate(busiDate)) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0168"));
			}
		}
	}

	private void addContrastLock(JKBXVO vo) throws BusinessException {
		if (vo.getContrastVO() != null && vo.getContrastVO().length > 0) {// 冲借款时,借款单,并发校验
			List<String> pkList = new ArrayList<String>();
			for (BxcontrastVO contrast : vo.getContrastVO()) {
				pkList.add(contrast.getPk_jkd());
			}
			KeyLock.dynamicLockWithException(pkList);
		}
	}

	
	private void checkContrastJkTs(JKBXVO vo) throws BusinessException {
		if (vo.getJkHeadVOs() != null && vo.getJkHeadVOs().length > 0) {// 冲借款时、借款单版本校验
			BDVersionValidationUtil.validateVersion(vo.getJkHeadVOs());
		}
	}

	
	public void checkQCClose(String pk_org) throws BusinessException {
		// 期初关闭校验
		boolean flag = NCLocator.getInstance().lookup(IErminitQueryService.class).queryStatusByOrg(pk_org);
		if (flag == true) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
					"0201107-0002"));
		}
	}

	private void chkCustomerPayFreezeFlag(JKBXVO bxvo) throws ValidationException {
		String hbbm = bxvo.getParentVO().getHbbm();
		if (StringUtils.isEmpty(hbbm)) {
			return;
		}
		ISupplierPubService qryservice = NCLocator.getInstance().lookup(ISupplierPubService.class);
		SupFinanceVO[] supfivos = null;
		try {
			supfivos = qryservice.getSupFinanceVO(new String[] { hbbm }, bxvo.getParentVO().getFydwbm(), new String[] {
					SupFinanceVO.PAYFREEZEFLAG, SupFinanceVO.PK_SUPPLIER });
		} catch (BusinessException e) {
			ExceptionHandler.error(e);
		}
		UFBoolean flag = UFBoolean.FALSE;

		if (!(ArrayUtils.isEmpty(supfivos))) {
			for (SupFinanceVO vo : supfivos) {
				flag = vo.getPayfreezeflag();
				if (flag != null && flag.booleanValue()) {
					if (vo.getPk_supplier().equals(hbbm)) {
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"expensepub_0", "02011002-0160"));
					}
				}
			}
		}
	}

	
	private void prepareBusinessType(JKBXVO bxvo) {
		JKBXHeaderVO headvo = bxvo.getParentVO();
		if (!bxvo.getParentVO().getQcbz().booleanValue()) {
			try {
				IPFConfig ipf = NCLocator.getInstance().lookup(IPFConfig.class);
				String pk_busitype = null;
				if (!StringUtils.isEmpty(headvo.getDjdl()) && !StringUtils.isEmpty(headvo.getDjlxbm())
						&& !StringUtils.isEmpty(headvo.getCreator())) {
					if (headvo.getDjdl().equals(BXConstans.BX_DJDL)) {
						pk_busitype = ipf.retBusitypeCanStart(BXConstans.BX_DJLXBM, headvo.getDjlxbm(),
								headvo.getPk_org(), headvo.getCreator());
					} else if (headvo.getDjdl().equals(BXConstans.JK_DJDL)) {
						pk_busitype = ipf.retBusitypeCanStart(BXConstans.JK_DJLXBM, headvo.getDjlxbm(),
								headvo.getPk_org(), headvo.getCreator());
					}
					headvo.setBusitype(pk_busitype);
				}
			} catch (Exception e) {
				// // 如果出现异常，即使不抛出，EJB事务控制，将回滚事务
				// String msg =
				// nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
				// "UCMD1-000053")
				// + headvo.getDjlxbm()
				// +
				// nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
				// "02011002-0011")
				// + headvo.getDjlxbm()
				// +
				// nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
				// "02011002-0012");
				// modified by chendya 没有查到流程不抛异常
				// throw new BusinessRuntimeException(msg);
				ExceptionHandler.consume(e);
				// --end
			}
		}
	}

	
	private void getBusitype(JKBXHeaderVO parentVO) throws BusinessException, DAOException {

		if (parentVO.getFkyhzh() != null && isInneracc(parentVO.getFkyhzh()).equals("Y")) {

			IPFConfig pFConfig = NCLocator.getInstance().lookup(IPFConfig.class);

			String pk_busiflow = parentVO.getBusitype();
			String trade_type = parentVO.getDjlxbm();
			if (StringUtil.isEmpty(pk_busiflow)) {
				// // 设置业务流程
				String billtype = parentVO.getDjdl().equals(BXConstans.BX_DJDL) ? BXConstans.BX_DJLXBM
						: BXConstans.JK_DJLXBM;
				String userid = InvocationInfoProxy.getInstance().getUserId();
				String pk_busiflowValue = pFConfig.retBusitypeCanStart(billtype, trade_type, parentVO.getPk_org(),
						userid);
				if (parentVO.getDjdl().equals(BXConstans.BX_DJDL) || parentVO.getDjdl().equals(BXConstans.JK_DJDL)) {
					if (pk_busiflowValue == null) {
						throw ExceptionHandler.createException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"2011v61013_0", "02011v61013-0061"));
					}
					parentVO.setBusitype(pk_busiflowValue);

					new BaseDAO().updateVO(parentVO, new String[] { "busitype" });
				}
			}
		}
	}

	public String isInneracc(String pk_account) {
		String sql = "select  isinneracc from bd_bankaccbas  where pk_bankaccbas ='" + pk_account + "'";
		PersistenceManager manager = null;
		try {
			manager = PersistenceManager.getInstance(InvocationInfoProxy.getInstance().getUserDataSource());
			JdbcSession session = manager.getJdbcSession();
			return (String) session.executeQuery(sql, new ResultSetProcessor() {
				private static final long serialVersionUID = 4040766420632132035L;

				public Object handleResultSet(ResultSet rs) throws SQLException {
					String flag = "N";
					if (rs != null && rs.next()) {
						flag = rs.getString("isinneracc").toString();
					}
					return flag;
				}
			});
		} catch (DbException e) {
			nc.bs.logging.Log.getInstance(this.getClass()).error(e);
		} finally {
			if (manager != null)
				manager.release();
		}
		return null;
	}

	
	public void checkSave(JKBXVO bxvo) throws BusinessException {

		prepare(bxvo);

		JKBXHeaderVO parentVO = bxvo.getParentVO();
		// BXBusItemVO[] childrenVO = bxvo.getBxBusItemVOS();
		if (!parentVO.isInit()) {
			// 校验表头合法性
			checkValidHeader(parentVO);
			// 校验表体合法性
			checkValidChildrenVO(bxvo);
			// 校验分摊明细信息
			checkCShareDetail(bxvo);
			// 校验个人银行账户币种与单据币种,和资金账户使用权与单据币种是否相同
			checkCurrency(parentVO);
			// 财务核报容差校验
			checkFinRange(bxvo);
			// 表头表体金额合计校验
			checkHeadItemJe(bxvo);
			// 财务金额校验
			checkValidFinItemVO(bxvo);
			// 校验待摊信息
			checkExpamortizeinfo(bxvo);

		} else {
			checkRepeatCShareDetailRow(bxvo);
		}
	}

	
	private void checkBillPaytargetInfo(JKBXVO bxvo) throws BusinessException {
		// 费用调整单不控制合计金额为0、负数
		JKBXHeaderVO parentVO = bxvo.getParentVO();
		BXBusItemVO[] childrenVO = bxvo.getBxBusItemVOS();
		boolean isAdjust = parentVO.isAdjustBxd();
		if (BXConstans.BX_DJDL.equals(parentVO.getDjdl())
				&& !BXConstans.BILLTYPECODE_RETURNBILL.equals(parentVO.getDjlxbm()) && !isAdjust) {
			if (parentVO.getPaytarget().intValue() == BXStatusConst.PAY_TARGET_RECEIVER) {// 收款对象是员工，收款人不能为空
				if (parentVO.getReceiver() == null) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0185"));
				}
			} else if (parentVO.getPaytarget().intValue() == BXStatusConst.PAY_TARGET_HBBM) {// 收款对象是供应商，供应商不能为空
				if (parentVO.getHbbm() == null) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0186"));
				}
			} else if (parentVO.getPaytarget().intValue() == BXStatusConst.PAY_TARGET_CUSTOMER) {// 收款对象是客户，客户不能为空
				if (parentVO.getCustomer() == null) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0187"));
				}
			}
			for (BXBusItemVO bxBusItemVO : childrenVO) {
				if (bxBusItemVO.getPaytarget().intValue() == BXStatusConst.PAY_TARGET_RECEIVER) {// 收款对象是员工，收款人不能为空
					if (bxBusItemVO.getReceiver() == null) {
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"expensepub_0", "02011002-0188"));
					}
				} else if (bxBusItemVO.getPaytarget().intValue() == BXStatusConst.PAY_TARGET_HBBM) {// 收款对象是供应商，供应商不能为空
					if (bxBusItemVO.getHbbm() == null) {
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"expensepub_0", "02011002-0189"));
					}
				} else if (bxBusItemVO.getPaytarget().intValue() == BXStatusConst.PAY_TARGET_CUSTOMER) {// 收款对象是客户，客户不能为空
					if (bxBusItemVO.getCustomer() == null) {
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"expensepub_0", "02011002-0190"));
					}
				} else if (bxBusItemVO.getPaytarget().intValue() == BXStatusConst.PAY_TARGET_OTHER) {
					if (bxBusItemVO.getDefitem38() == null || bxBusItemVO.getDefitem37() == null
							|| bxBusItemVO.getDefitem36() == null) {
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"expensepub_0", "02011002-0193"));
					}
				}
			}
		}
	}

	
	public void checkRepeatCShareDetailRow(JKBXVO bxvo) throws ValidationException {
		CShareDetailVO[] cShareVos = bxvo.getcShareDetailVo();

		if (!bxvo.isHasCShareDetail())
			return;

		if (bxvo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {

			List<String> controlKeys = new ArrayList<String>();
			StringBuffer controlKey = null;

			String[] attributeNames = cShareVos[0].getAttributeNames();
			for (int i = 0; i < cShareVos.length; i++) {

				controlKey = new StringBuffer();

				for (int j = 0; j < attributeNames.length; j++) {
					if (getNotRepeatFields().contains(attributeNames[j])
							|| attributeNames[j].startsWith(BXConstans.BODY_USERDEF_PREFIX)) {
						controlKey.append(cShareVos[i].getAttributeValue(attributeNames[j]));
					} else {
						continue;
					}
				}

				if (!controlKeys.contains(controlKey.toString())) {
					controlKeys.add(controlKey.toString());
				} else {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
							"0201107-0114"));
				}
			}
		}
	}

	private void checkExpamortizeinfo(JKBXVO bxvo) throws BusinessException {
		if (bxvo.getParentVO().getIsexpamt().equals(UFBoolean.TRUE)) {
			if (nc.vo.er.util.StringUtils.isNullWithTrim(bxvo.getParentVO().getStart_period())) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
						"0201107-0109"));
			} else {
				AccperiodmonthVO accperiodmonthVO = null;
				AccperiodmonthVO startperiodmonthVO = null;
				accperiodmonthVO = ErAccperiodUtil.getAccperiodmonthByUFDate(bxvo.getParentVO().getPk_org(), bxvo
						.getParentVO().getDjrq());
				startperiodmonthVO = ErAccperiodUtil.getAccperiodmonthByPk(bxvo.getParentVO().getStart_period());
				if (startperiodmonthVO.getYearmth().compareTo(accperiodmonthVO.getYearmth()) < 0) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
							"0201107-0110"));
				}
			}
			if (bxvo.getParentVO().getTotal_period() == null || ((int) bxvo.getParentVO().getTotal_period()) <= 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
						"0201107-0111"));
			}
		} else {
			if (!nc.vo.er.util.StringUtils.isNullWithTrim(bxvo.getParentVO().getStart_period())) {
				bxvo.getParentVO().setStart_period(null);

			}
			if (bxvo.getParentVO().getTotal_period() != null) {
				bxvo.getParentVO().setTotal_period(null);
			}
		}
	}

	private void checkCShareDetail(JKBXVO bxvo) throws ValidationException {
		boolean isAdjust = bxvo.getParentVO().isAdjustBxd();
		CShareDetailVO[] cShareVos = bxvo.getcShareDetailVo();

		if (bxvo.getParentVO().getIscostshare().equals(UFBoolean.TRUE)) {
			if (!isAdjust && bxvo.getParentVO().getYbje().compareTo(UFDouble.ZERO_DBL) < 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
						"0201107-0007"));
			}
		}

		if (!bxvo.isHasCShareDetail()) {
			return;
		}

		JKBXHeaderVO parentVO = bxvo.getParentVO();
		if (bxvo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {

			UFDouble total = parentVO.getYbje();
			if (total == null) {
				total = UFDouble.ZERO_DBL;
			}

			UFDouble amount = UFDouble.ZERO_DBL;
			UFDouble ratio = UFDouble.ZERO_DBL;

			// List<String> controlKeys = new ArrayList<String>();
			// StringBuffer controlKey = null;

			// String[] attributeNames = cShareVos[0].getAttributeNames();
			for (int i = 0; i < cShareVos.length; i++) {
				UFDouble shareAmount = ErmForCShareUtil.formatUFDouble(cShareVos[i].getAssume_amount(),
						parentVO.getBzbm());
				UFDouble shareRatio = cShareVos[i].getShare_ratio();

				if (!isAdjust && !ErmForCShareUtil.isUFDoubleGreaterThanZero(shareAmount)) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
							"0201107-0112"));
				}

				if (!isAdjust && !ErmForCShareUtil.isUFDoubleGreaterThanZero(shareRatio)) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
							"0201107-0113"));
				}

				amount = amount.add(shareAmount);
				if (shareRatio != null){
					ratio = ratio.add(shareRatio);
				}
				
				// controlKey = new StringBuffer();

				// for (int j = 0; j < attributeNames.length; j++) {
				// if (getNotRepeatFields().contains(attributeNames[j])
				// ||
				// attributeNames[j].startsWith(BXConstans.BODY_USERDEF_PREFIX)
				// ||
				// (isAdjust&&CShareDetailVO.YSDATE.equals(attributeNames[j])))
				// {
				// controlKey.append(cShareVos[i].getAttributeValue(attributeNames[j]));
				// } else {
				// continue;
				// }
				// }

				// if (!controlKeys.contains(controlKey.toString())) {
				// controlKeys.add(controlKey.toString());
				// } else {
				// throw new
				// ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
				// "0201107-0114"));
				// }
			}

			if (total.toDouble().compareTo(amount.toDouble()) != 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
						"0201107-0115"));
			}
		}
	}

	
	private void checkCashaccountAndFkyhzh(JKBXHeaderVO headerVO) throws BusinessException {
		String fkyhzh = headerVO.getFkyhzh();
		String pkCashaccount = headerVO.getPk_cashaccount();
		if ((!StringUtil.isEmpty(fkyhzh)) && (!StringUtil.isEmpty(pkCashaccount))) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61_1215_0",
					"02011v61215-0000"));
		}
	}

	
	private void chkBankAccountCurrency(JKBXVO vo) throws BusinessException {
		if (vo == null || vo.getParentVO() == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
					"02011002-0135"));
		}
		JKBXHeaderVO headerVO = vo.getParentVO();
		String skyhzh = headerVO.getSkyhzh();// 个人银行账号
		String custaccount = headerVO.getCustaccount();// 客商银行账号
		String pk_currtype = headerVO.getBzbm();// 币种

		IBankAccSubInfoQueryService service = NCLocator.getInstance().lookup(IBankAccSubInfoQueryService.class);
		BankAccSubVO[] vos = service.querySubInfosByPKs(new String[] { skyhzh, custaccount });
		if (vos != null && vos.length > 0) {
			for (BankAccSubVO subvo : vos) {
				if (subvo.getPk_bankaccsub().equals(skyhzh) && !subvo.getPk_currtype().equals(pk_currtype)) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0136"));
				}

				if (subvo.getPk_bankaccsub().equals(custaccount) && !subvo.getPk_currtype().equals(pk_currtype)) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0182"));
				}
			}
		}

	}

	// 交叉校验工具
	private FipubCrossCheckRuleChecker crossChecker;

	private FipubCrossCheckRuleChecker getCrossChecker() {
		if (crossChecker == null) {
			crossChecker = new FipubCrossCheckRuleChecker();
		}
		return crossChecker;
	}

	
	private void doCrossCheck(JKBXVO billVO) throws CrossControlMsgException {
		// 是否不检查
		if (billVO.getHasCrossCheck()) {
			return;
		}
		// 交叉校验
		// 删除行过滤掉(实际上校验时，都不需要考虑删除行的校验)
		JKBXVO jkbxVo = (JKBXVO) billVO.clone();
		if (jkbxVo.getChildrenVO() != null) {
			List<BXBusItemVO> childrenList = new ArrayList<BXBusItemVO>();
			for (BXBusItemVO detail : jkbxVo.getChildrenVO()) {
				if (detail.getStatus() != VOStatus.DELETED) {
					childrenList.add(detail);
				}
			}
			jkbxVo.setChildrenVO(childrenList.toArray(new BXBusItemVO[] {}));
		}

		String retMsg = null;
		try {
			retMsg = getCrossChecker().check2(jkbxVo.getParentVO().getPk_org(), jkbxVo.getParentVO().getDjlxbm(),
					jkbxVo);
		} catch (BusinessException e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}

		if (retMsg != null && retMsg.length() > 0) {
			// 包装后扔出异常
			throw new CrossControlMsgException(retMsg);
		}
	}

	private void checkAuditMan(JKBXVO bxvo) throws BusinessException {
		String auditman = bxvo.getParentVO().getAuditman();
		if (auditman == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000365"));
		}
	}

	
	private void checkBillDate(JKBXHeaderVO parentVO) throws BusinessException {
		if (parentVO.getDjrq() == null) {
			// 数据交换平台可能录入空的单据日期
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0",
					"02011ermpub0316-0012"));
		}

		if (parentVO instanceof JKHeaderVO) {// 最迟还款日期不能早于单据日期
			UFDate zhrq = parentVO.getZhrq();
			if (zhrq != null) {
				if (parentVO.getDjrq().afterDate(zhrq)) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030102",
							"UPP2006030102-001122"));
				}
			}

		}

		String pk_org = parentVO.getPk_org();
		UFDate startDate = null;
		try {
			String yearMonth = NCLocator.getInstance().lookup(IOrgUnitPubService.class)
					.getOrgModulePeriodByOrgIDAndModuleID(pk_org, BXConstans.ERM_MODULEID);
			if (yearMonth != null && yearMonth.length() != 0) {
				String year = yearMonth.substring(0, 4);
				String month = yearMonth.substring(5, 7);
				if (year != null && month != null) {
					// 返回组织的会计日历
					AccountCalendar calendar = AccountCalendar.getInstanceByPk_org(pk_org);
					if (calendar == null) {
						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"2011v61013_0", "02011v61013-0021"));
					}
					calendar.set(year, month);
					if (calendar.getMonthVO() == null) {
						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"2011v61013_0", "02011v61013-0022"));
					}
					startDate = calendar.getMonthVO().getBegindate();
				}
			}
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		if (startDate == null) {
			ExceptionHandler.consume(new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"expensepub_0", "02011002-0001")));
		}
		if (startDate != null) {
			if (parentVO.getQcbz().booleanValue()) {
				if (parentVO.getDjrq() != null && !parentVO.getDjrq().beforeDate(startDate)) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0139"));
				}
			} else {
				if (parentVO.getDjrq() != null && parentVO.getDjrq().beforeDate(startDate)) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0140"));
				}
			}
		} else {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
					"02011002-0141"));
		}
	}

	private void checkCurrency(JKBXHeaderVO parentVO) throws BusinessException {
		// FIXME
		// String PKForAcc = "";//用来校验个人银行账户的pk
		// String PKForCash = "";//用来校验资金账户使用权的pk
		// String AccCurrency = "";//个人银行账户的币种
		// String CashCurrency = "";//资金账户使用权的币种
		// String DjCurrencyType = parentVO.getBzbm();//单据的币种编码
		//
		// PKForAcc = parentVO.getSkyhzh();
		// PKForCash = parentVO.getFkyhzh();
		//
		// //取账户的币种，并与单据的币种进行校验
		// IBankaccQueryService pa =
		// (IBankaccQueryService)NCLocator.getInstance().lookup(IBankaccQueryService.class.getName());
		// if(PKForAcc!=null&&!PKForAcc.equals("")){
		// BankaccbasVO[] accCurrencyType = pa.queryFundAccBasVosByPks(new
		// String[]{PKForAcc});
		// AccCurrency = accCurrencyType[0].getPk_currtype();//取来个人银行账户的币种PK
		// if(!AccCurrency.equals(DjCurrencyType)){
		// throw new
		// ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000393"));
		// }
		// }
		// if(PKForCash!=null&&!PKForCash.equals("")){
		// BankaccbasVO[] cashCurrencyType = pa.queryFundAccBasVosByPks(new
		// String[]{PKForCash});
		// CashCurrency = cashCurrencyType[0].getPk_currtype();//取来资金账户使用权的币种PK
		// if(!CashCurrency.equals(DjCurrencyType)){
		// throw new
		// ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000394"));
		// }
		// }
	}

	private void checkCurrencyRate(JKBXHeaderVO parentVO) throws BusinessException {
		UFDouble hl = parentVO.getBbhl();
		UFDouble globalhl = parentVO.getGlobalbbhl();
		UFDouble grouphl = parentVO.getGroupbbhl();

		// 全局参数判断
		String paramValue = SysInitQuery.getParaString(IOrgConst.GLOBEORG, "NC002");
		// 是否启用全局本币模式
		boolean isGlobalmodel = StringUtils.isNotBlank(paramValue) && !paramValue.equals(BXConstans.GLOBAL_DISABLE);

		// 集团级参数判断
		paramValue = SysInitQuery.getParaString(parentVO.getPk_group(), "NC001");
		// 是否启用集团本币模式
		boolean isGroupmodel = StringUtils.isNotBlank(paramValue) && !paramValue.equals(BXConstans.GROUP_DISABLE);

		if (hl == null || hl.toDouble() == 0) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000395"));
		}
		if (isGlobalmodel) {
			if (globalhl == null || globalhl.toDouble() == 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0142"));
			}
		}
		if (isGroupmodel) {
			if (grouphl == null || grouphl.toDouble() == 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0143"));
			}
		}

	}

	private static void prepareBusItemvo(JKBXVO bxvo) throws BusinessException {
		BXBusItemVO[] busItemVOs = bxvo.getChildrenVO();
		if (busItemVOs != null && busItemVOs.length != 0) {
			JKBXHeaderVO parentVO = bxvo.getParentVO();
			if (busItemVOs[0].getSzxmid() != null) {// 收支项目
				parentVO.setSzxmid(busItemVOs[0].getSzxmid());
			}
			if (busItemVOs[0].getJobid() != null) {// 项目
				parentVO.setJobid(busItemVOs[0].getJobid());
			}

			if (busItemVOs[0].getProjecttask() != null) {
				parentVO.setProjecttask(busItemVOs[0].getProjecttask());
			}

			if (busItemVOs[0].getCashproj() != null) {
				parentVO.setCashproj(busItemVOs[0].getCashproj());
			}

			if (busItemVOs[0].getPk_pcorg() != null) {
				parentVO.setPk_pcorg(busItemVOs[0].getPk_pcorg());
			}

			if (busItemVOs[0].getPk_pcorg_v() != null) {
				parentVO.setPk_pcorg_v(busItemVOs[0].getPk_pcorg_v());
			}

			if (busItemVOs[0].getPk_checkele() != null) {
				parentVO.setPk_checkele(busItemVOs[0].getPk_checkele());
			}

			if (busItemVOs[0].getPk_resacostcenter() != null) {
				parentVO.setPk_resacostcenter(busItemVOs[0].getPk_resacostcenter());
			}
			// 产品线和品牌
			if (busItemVOs[0].getPk_proline() != null) {
				parentVO.setPk_proline(busItemVOs[0].getPk_proline());
			}

			if (busItemVOs[0].getPk_brand() != null) {
				parentVO.setPk_brand(busItemVOs[0].getPk_brand());
			}

			// 将支付对象的信息带到表头
			if (busItemVOs[0].getPaytarget() != null && parentVO.getPaytarget() == null) {
				parentVO.setPaytarget(busItemVOs[0].getPaytarget());
			}

			if (busItemVOs[0].getHbbm() != null && parentVO.getHbbm() == null) {
				parentVO.setHbbm(busItemVOs[0].getHbbm());
			}

			if (busItemVOs[0].getCustomer() != null && parentVO.getCustomer() == null) {
				parentVO.setCustomer(busItemVOs[0].getCustomer());
			}

			if (busItemVOs[0].getReceiver() != null && parentVO.getReceiver() == null) {
				parentVO.setReceiver(busItemVOs[0].getReceiver());
			}

			Integer headPaytarget = parentVO.getPaytarget();
			String headReceiver = parentVO.getReceiver();
			String headHbbm = parentVO.getHbbm();
			String headCustomer = parentVO.getCustomer();

			for (BXBusItemVO item : busItemVOs) {
				item.setDr(Integer.valueOf(0));

				Integer itemPaytarget = item.getPaytarget();
				if (item.getPaytarget() == null && headPaytarget != null) {
					item.setPaytarget(headPaytarget);
				}

				// 将支付对象的信息带到表体
				// 表体银行账户设置默认值
				if (itemPaytarget != null && headPaytarget != null && itemPaytarget.equals(headPaytarget)) {
					if (itemPaytarget == Paytarget.EMPLOYEE) {
						if (item.getReceiver() == null && parentVO.getReceiver() != null) {
							item.setReceiver(parentVO.getReceiver());
						}

						if (item.getReceiver() != null && headReceiver != null
								&& headReceiver.equals(item.getReceiver()) && item.getSkyhzh() == null) {
							item.setSkyhzh(parentVO.getSkyhzh());
						}
					} else if (itemPaytarget == Paytarget.HBBM) {
						if (item.getHbbm() == null && parentVO.getHbbm() != null) {
							item.setHbbm(parentVO.getHbbm());
						}
						if (item.getFreecust() == null && parentVO.getFreecust() != null) {
							item.setFreecust(parentVO.getFreecust());
						}

						if (item.getHbbm() != null && headHbbm != null && headHbbm.equals(item.getHbbm())
								&& item.getCustaccount() == null) {
							item.setCustaccount(parentVO.getCustaccount());
						}
					} else if (itemPaytarget == Paytarget.CUSTOMER) {
						if (item.getCustomer() == null && parentVO.getCustomer() != null) {
							item.setCustomer(parentVO.getCustomer());
						}

						if (item.getFreecust() == null && parentVO.getFreecust() != null) {
							item.setFreecust(parentVO.getFreecust());
						}

						if (item.getCustomer() != null && headCustomer != null
								&& headCustomer.equals(item.getCustomer()) && item.getCustaccount() == null) {
							item.setCustaccount(parentVO.getCustaccount());
						}
					}
				}

				if (item.getBbje() == null || item.getBbje().compareTo(UFDouble.ZERO_DBL) == 0) {
					if (parentVO.getPk_org() != null && parentVO.getDjrq() != null && parentVO.getBzbm() != null) {
						UFDouble orig_amount = item.getYbje() == null ? UFDouble.ZERO_DBL : item.getYbje();
						// 补充本币金额
						UFDouble orgAmount = Currency.getAmountByOpp(parentVO.getPk_org(), parentVO.getBzbm(),
								Currency.getOrgLocalCurrPK(parentVO.getPk_org()), orig_amount, parentVO.getBbhl(),
								parentVO.getDjrq());
						item.setBbje(orgAmount);
						// 集团、全局金额
						UFDouble[] money = Currency.computeGroupGlobalAmount(orig_amount, orgAmount,
								parentVO.getBzbm(), parentVO.getDjrq(), parentVO.getPk_org(), parentVO.getPk_group(),
								parentVO.getGlobalbbhl(), parentVO.getGroupbbhl());
						item.setGroupbbje(money[0]);
						item.setGlobalbbje(money[1]);
					}
				}

				// 借款单的原币余额和预计余额
				item.setYbye(item.getYbje());
				item.setBbye(item.getBbje());
				item.setGroupbbye(item.getGroupbbje());
				item.setGlobalbbye(item.getGlobalbbje());
				item.setYjye(item.getYbje());
				if(item.getPaytarget() != null){
					if(item.getPaytarget().equals(Integer.valueOf(0))){
						if(bxvo.getParentVO().getSkyhzh()!= null && item.getSkyhzh() ==null){
							item.setSkyhzh(bxvo.getParentVO().getSkyhzh());
						}
						
					}else if(item.getPaytarget().equals(Integer.valueOf(1))){
						if(bxvo.getParentVO().getCustaccount()!= null && item.getCustaccount() ==null){
							item.setCustaccount(bxvo.getParentVO().getCustaccount()) ;
						}
					}else if(item.getPaytarget().equals(Integer.valueOf(2))){
						if(bxvo.getParentVO().getCustaccount()!= null && item.getCustaccount() ==null){
							item.setCustaccount(bxvo.getParentVO().getCustaccount()) ;
						}
					}
					
				}
				// 将支付对象的信息带到表体
				if (item.getPaytarget() == null && bxvo.getParentVO().getPaytarget() != null) {
					item.setPaytarget(bxvo.getParentVO().getPaytarget());
				}
				if (item.getHbbm() == null && bxvo.getParentVO().getHbbm() != null) {
					item.setHbbm(bxvo.getParentVO().getHbbm());
				}
				if (item.getCustomer() == null && bxvo.getParentVO().getCustomer() != null) {
					item.setCustomer(bxvo.getParentVO().getCustomer());
				}
				if (item.getReceiver() == null && bxvo.getParentVO().getReceiver() != null) {
					item.setReceiver(bxvo.getParentVO().getReceiver());
				}
				if (item.getCjkybje() == null) {
					item.setCjkybje(UFDouble.ZERO_DBL);
					item.setCjkbbje(UFDouble.ZERO_DBL);
					item.setGroupcjkbbje(UFDouble.ZERO_DBL);
					item.setGlobalcjkbbje(UFDouble.ZERO_DBL);
				}

				// 判断所有的本币金额，如果为空就设置所有的本币金额为0
				String[] bodyJeField = BXBusItemVO.getBodyJeFieldForDecimal();
				for (String field : bodyJeField) {
					if (item.getAttributeValue(field) == null)
						item.setAttributeValue(field, UFDouble.ZERO_DBL);
				}

				if (parentVO.getDjdl().equals(BXConstans.BX_DJDL) && !parentVO.isInit() && parentVO.getDjzt() != 0) {
					if (parentVO.getDjlxbm().equals("2647")) {
						calculateZfHkJeHK(item);
					} else {
						calculateZfHkJeBX(item);
					}
				} else if (parentVO.getDjdl().equals(BXConstans.JK_DJDL)) {
					calculateZfHkJeJK(item);
				}
			}
		}
		// nc端同步表头币种到表体。
		String bzbm = bxvo.getParentVO().getBzbm();
		if (bxvo.getChildrenVO() != null && bxvo.getChildrenVO().length > 0) {
			for (BXBusItemVO bxBusItemVO : bxvo.getChildrenVO()) {
				// if (bxBusItemVO.getBzbm() == null) {
				bxBusItemVO.setBzbm(bzbm);
				bxBusItemVO.setBbhl(bxvo.getParentVO().getBbhl());
				bxBusItemVO.setGlobalbbhl(bxvo.getParentVO().getGlobalbbhl());
				bxBusItemVO.setGroupbbhl(bxvo.getParentVO().getGroupbbhl());
				// }
			}
		}
		if (bxvo.getContrastVO() != null && bxvo.getContrastVO().length > 0) {
			for (BxcontrastVO bxcontrastvo : bxvo.getContrastVO()) {
				// if (bxcontrastvo.getBzbm() == null) {
				bxcontrastvo.setBzbm(bzbm);
				bxcontrastvo.setBbhl(bxvo.getParentVO().getBbhl());
				bxcontrastvo.setGlobalbbhl(bxvo.getParentVO().getGlobalbbhl());
				bxcontrastvo.setGroupbbhl(bxvo.getParentVO().getGroupbbhl());
				// }
			}
		}
		if (bxvo.getcShareDetailVo() != null && bxvo.getcShareDetailVo().length > 0) {
			for (CShareDetailVO cshareDetailVO : bxvo.getcShareDetailVo()) {
				// if (cshareDetailVO.getBzbm() == null) {
				cshareDetailVO.setBzbm(bzbm);
				cshareDetailVO.setBbhl(bxvo.getParentVO().getBbhl());
				cshareDetailVO.setGlobalbbhl(bxvo.getParentVO().getGlobalbbhl());
				cshareDetailVO.setGroupbbhl(bxvo.getParentVO().getGroupbbhl());
				// }
			}
		}
	}

	private static void prepareBusItemvoForIweb(JKBXVO bxvo) throws BusinessException {
		BXBusItemVO[] busItemVOs = bxvo.getChildrenVO();
		if (busItemVOs != null && busItemVOs.length != 0) {
			JKBXHeaderVO parentVO = bxvo.getParentVO();
			Integer headPaytarget = parentVO.getPaytarget();
			String headReceiver = parentVO.getReceiver();
			String headHbbm = parentVO.getHbbm();
			String headCustomer = parentVO.getCustomer();
			for (BXBusItemVO item : busItemVOs) {
				item.setDr(Integer.valueOf(0));
				Integer itemPaytarget = item.getPaytarget();
				if (item.getPaytarget() == null && headPaytarget != null) {
					item.setPaytarget(headPaytarget);
				}
				// 将支付对象的信息带到表体
				// 表体银行账户设置默认值
				if (itemPaytarget != null && headPaytarget != null && itemPaytarget.equals(headPaytarget)) {
					if (itemPaytarget == Paytarget.EMPLOYEE) {
						if (item.getReceiver() == null && parentVO.getReceiver() != null) {
							item.setReceiver(parentVO.getReceiver());
						}
						if (item.getReceiver() != null && headReceiver != null
								&& headReceiver.equals(item.getReceiver()) && item.getSkyhzh() == null) {
							item.setSkyhzh(parentVO.getSkyhzh());
						}
					} else if (itemPaytarget == Paytarget.HBBM) {
						if (item.getHbbm() == null && parentVO.getHbbm() != null) {
							item.setHbbm(parentVO.getHbbm());
						}
						if (item.getFreecust() == null && parentVO.getFreecust() != null) {
							item.setFreecust(parentVO.getFreecust());
						}
						if (item.getHbbm() != null && headHbbm != null && headHbbm.equals(item.getHbbm())
								&& item.getCustaccount() == null) {
							item.setCustaccount(parentVO.getCustaccount());
						}
					} else if (itemPaytarget == Paytarget.CUSTOMER) {
						if (item.getCustomer() == null && parentVO.getCustomer() != null) {
							item.setCustomer(parentVO.getCustomer());
						}
						if (item.getFreecust() == null && parentVO.getFreecust() != null) {
							item.setFreecust(parentVO.getFreecust());
						}
						if (item.getCustomer() != null && headCustomer != null
								&& headCustomer.equals(item.getCustomer()) && item.getCustaccount() == null) {
							item.setCustaccount(parentVO.getCustaccount());
						}
					}
				}
				// 借款单的原币余额和预计余额
				item.setYbye(item.getYbje());
				item.setBbye(item.getBbje());
				item.setGroupbbye(item.getGroupbbje());
				item.setGlobalbbye(item.getGlobalbbje());
				item.setYjye(item.getYbje());
				if(item.getPaytarget() != null){
					if(item.getPaytarget().equals(Integer.valueOf(0))){
						if(bxvo.getParentVO().getSkyhzh()!= null && item.getSkyhzh() ==null){
							item.setSkyhzh(bxvo.getParentVO().getSkyhzh());
						}
						
					}else if(item.getPaytarget().equals(Integer.valueOf(1))){
						if(bxvo.getParentVO().getCustaccount()!= null && item.getCustaccount() ==null){
							item.setCustaccount(bxvo.getParentVO().getCustaccount()) ;
						}
					}else if(item.getPaytarget().equals(Integer.valueOf(2))){
						if(bxvo.getParentVO().getCustaccount()!= null && item.getCustaccount() ==null){
							item.setCustaccount(bxvo.getParentVO().getCustaccount()) ;
						}
					}
					
				}
				
				// 将支付对象的信息带到表体
				if (item.getPaytarget() == null && bxvo.getParentVO().getPaytarget() != null) {
					item.setPaytarget(bxvo.getParentVO().getPaytarget());
				}
				if (item.getHbbm() == null && bxvo.getParentVO().getHbbm() != null) {
					item.setHbbm(bxvo.getParentVO().getHbbm());
				}
				if (item.getCustomer() == null && bxvo.getParentVO().getCustomer() != null) {
					item.setCustomer(bxvo.getParentVO().getCustomer());
				}
				if (item.getReceiver() == null && bxvo.getParentVO().getReceiver() != null&&item.getPaytarget()!=null&&item.getPaytarget().intValue()==0) {
					item.setReceiver(bxvo.getParentVO().getReceiver());
				}
				if (item.getCjkybje() == null) {
					item.setCjkybje(UFDouble.ZERO_DBL);
					item.setCjkbbje(UFDouble.ZERO_DBL);
					item.setGroupcjkbbje(UFDouble.ZERO_DBL);
					item.setGlobalcjkbbje(UFDouble.ZERO_DBL);
				}
				// 判断所有的本币金额，如果为空就设置所有的本币金额为0
				String[] bodyJeField = BXBusItemVO.getBodyJeFieldForDecimal();
				for (String field : bodyJeField) {
					if (item.getAttributeValue(field) == null)
						item.setAttributeValue(field, UFDouble.ZERO_DBL);
				}
				if (parentVO.getDjdl().equals(BXConstans.BX_DJDL) && !parentVO.isInit() && parentVO.getDjzt() != 0) {
					if (parentVO.getDjlxbm().equals("2647")) {
						calculateZfHkJeHK(item);
					} else {
						calculateZfHkJeBX(item);
					}
				} else if (parentVO.getDjdl().equals(BXConstans.JK_DJDL)) {
					calculateZfHkJeJK(item);
				}
			}
		}
	}

	
	private static void calculateZfHkJeJK(BXBusItemVO item) {
		if (UFDoubleTool.isZero(item.getCjkybje())) {
			if (item.getYbje().doubleValue() > 0) {
				item.setZfybje(item.getYjye());
				item.setZfbbje(item.getBbje());
				item.setGroupzfbbje(item.getGroupbbje());
				item.setGlobalzfbbje(item.getGlobalbbje());

				item.setHkybje(UFDouble.ZERO_DBL);
				item.setHkbbje(UFDouble.ZERO_DBL);
				item.setGrouphkbbje(UFDouble.ZERO_DBL);
				item.setGlobalhkbbje(UFDouble.ZERO_DBL);
			} else {
				item.setHkybje(item.getYbje().abs());
				item.setHkbbje(item.getBbje().abs());
				item.setGrouphkbbje(item.getGroupbbje().abs());
				item.setGlobalhkbbje(item.getGlobalbbje().abs());

				item.setZfybje(UFDouble.ZERO_DBL);
				item.setZfbbje(UFDouble.ZERO_DBL);
				item.setGroupzfbbje(UFDouble.ZERO_DBL);
				item.setGlobalzfbbje(UFDouble.ZERO_DBL);
			}

		} else if (UFDoubleTool.isXiaoyu(item.getYbje(), item.getCjkybje())) {
			// 原币金额 < 冲借款金额,有还款,无支付
			item.setZfybje(UFDouble.ZERO_DBL);
			item.setZfbbje(UFDouble.ZERO_DBL);
			item.setGroupzfbbje(UFDouble.ZERO_DBL);
			item.setGlobalzfbbje(UFDouble.ZERO_DBL);

			item.setHkybje(item.getCjkybje().sub(item.getYbje()));
			item.setHkbbje(item.getCjkbbje().sub(item.getBbje()));
			item.setGrouphkbbje(item.getGroupcjkbbje().sub(item.getGroupbbje()));
			item.setGlobalhkbbje(item.getGlobalcjkbbje().sub(item.getGlobalbbje()));
		} else {
			// 原币金额 > 冲借款金额,有支付,无还款
			item.setZfybje(item.getYbje().sub(item.getCjkybje()));
			item.setZfbbje(item.getBbje().sub(item.getCjkbbje()));
			item.setGroupzfbbje(item.getGroupbbje() != null ? item.getGroupbbje().sub(item.getGroupcjkbbje())
					: UFDouble.ZERO_DBL);
			item.setGlobalzfbbje(item.getGlobalbbje() != null ? item.getGlobalbbje().sub(item.getGlobalcjkbbje())
					: UFDouble.ZERO_DBL);

			item.setHkybje(UFDouble.ZERO_DBL);
			item.setHkbbje(UFDouble.ZERO_DBL);
			item.setGrouphkbbje(UFDouble.ZERO_DBL);
			item.setGlobalhkbbje(UFDouble.ZERO_DBL);
		}
	}

	
	private static void calculateZfHkJeHK(BXBusItemVO item) {
		if (UFDoubleTool.isZero(item.getCjkybje())) {
			if (item.getYbje().doubleValue() > 0) {
				item.setZfybje(item.getYbje());
				item.setZfbbje(item.getBbje());
				item.setGroupzfbbje(item.getGroupbbje());
				item.setGlobalzfbbje(item.getGlobalbbje());

				item.setHkybje(UFDouble.ZERO_DBL);
				item.setHkbbje(UFDouble.ZERO_DBL);
				item.setGrouphkbbje(UFDouble.ZERO_DBL);
				item.setGlobalhkbbje(UFDouble.ZERO_DBL);
			} else {
				item.setHkybje(item.getYbje().abs());
				item.setHkbbje(item.getBbje().abs());
				item.setGrouphkbbje(item.getGroupbbje().abs());
				item.setGlobalhkbbje(item.getGlobalbbje().abs());

				item.setZfybje(UFDouble.ZERO_DBL);
				item.setZfbbje(UFDouble.ZERO_DBL);
				item.setGroupzfbbje(UFDouble.ZERO_DBL);
				item.setGlobalzfbbje(UFDouble.ZERO_DBL);
			}

		} else if (UFDoubleTool.isXiaoyu(item.getYbje(), item.getCjkybje())) {
			// 原币金额 < 冲借款金额,有还款,无支付
			item.setZfybje(UFDouble.ZERO_DBL);
			item.setZfbbje(UFDouble.ZERO_DBL);
			item.setGroupzfbbje(UFDouble.ZERO_DBL);
			item.setGlobalzfbbje(UFDouble.ZERO_DBL);

			item.setHkybje(item.getCjkybje().sub(item.getYbje()));
			item.setHkbbje(item.getCjkbbje().sub(item.getBbje()));
			item.setGrouphkbbje(item.getGroupcjkbbje().sub(item.getGroupbbje()));
			item.setGlobalhkbbje(item.getGlobalcjkbbje().sub(item.getGlobalbbje()));
		} else {
			// 原币金额 > 冲借款金额,有支付,无还款
			item.setZfybje(item.getYbje().sub(item.getCjkybje()));
			item.setZfbbje(item.getBbje().sub(item.getCjkbbje()));
			item.setGroupzfbbje(item.getGroupbbje() != null ? item.getGroupbbje().sub(item.getGroupcjkbbje())
					: UFDouble.ZERO_DBL);
			item.setGlobalzfbbje(item.getGlobalbbje() != null ? item.getGlobalbbje().sub(item.getGlobalcjkbbje())
					: UFDouble.ZERO_DBL);

			item.setHkybje(UFDouble.ZERO_DBL);
			item.setHkbbje(UFDouble.ZERO_DBL);
			item.setGrouphkbbje(UFDouble.ZERO_DBL);
			item.setGlobalhkbbje(UFDouble.ZERO_DBL);
		}
	}

	
	private static void calculateZfHkJeBX(BXBusItemVO item) {
		if (UFDoubleTool.isZero(item.getCjkybje())) {
			if (item.getVat_amount().doubleValue() > 0) {
				// xbx 20221013 集团修改
				item.setZfybje(item.getYbje());
				item.setZfbbje(item.getBbje());
				item.setGroupzfbbje(item.getGroupbbje());
				item.setGlobalzfbbje(item.getGlobalbbje());
				
				item.setHkybje(UFDouble.ZERO_DBL);
				item.setHkbbje(UFDouble.ZERO_DBL);
				item.setGrouphkbbje(UFDouble.ZERO_DBL);
				item.setGlobalhkbbje(UFDouble.ZERO_DBL);
			} else {
				item.setHkybje(item.getVat_amount().abs());
				item.setHkbbje(item.getOrgvat_amount().abs());
				item.setGrouphkbbje(item.getGroupvat_amount().abs());
				item.setGlobalhkbbje(item.getGlobalvat_amount().abs());

				item.setZfybje(UFDouble.ZERO_DBL);
				item.setZfbbje(UFDouble.ZERO_DBL);
				item.setGroupzfbbje(UFDouble.ZERO_DBL);
				item.setGlobalzfbbje(UFDouble.ZERO_DBL);
			}

		} else if (UFDoubleTool.isXiaoyu(item.getVat_amount(), item.getCjkybje())) {
			// 原币金额 < 冲借款金额,有还款,无支付
			item.setZfybje(UFDouble.ZERO_DBL);
			item.setZfbbje(UFDouble.ZERO_DBL);
			item.setGroupzfbbje(UFDouble.ZERO_DBL);
			item.setGlobalzfbbje(UFDouble.ZERO_DBL);

			item.setHkybje(item.getCjkybje().sub(item.getVat_amount()));
			item.setHkbbje(item.getCjkbbje().sub(item.getOrgvat_amount()));
			item.setGrouphkbbje(item.getGroupcjkbbje().sub(item.getGroupvat_amount()));
			item.setGlobalhkbbje(item.getGlobalcjkbbje().sub(item.getGlobalvat_amount()));
		} else {
			// 原币金额 > 冲借款金额,有支付,无还款
			item.setZfybje(item.getVat_amount().sub(item.getCjkybje()));
			item.setZfbbje(item.getOrgvat_amount().sub(item.getCjkbbje()));
			item.setGroupzfbbje(item.getGroupvat_amount() != null ? item.getGroupvat_amount().sub(
					item.getGroupcjkbbje()) : UFDouble.ZERO_DBL);
			item.setGlobalzfbbje(item.getGlobalvat_amount() != null ? item.getGlobalvat_amount().sub(
					item.getGlobalcjkbbje()) : UFDouble.ZERO_DBL);

			item.setHkybje(UFDouble.ZERO_DBL);
			item.setHkbbje(UFDouble.ZERO_DBL);
			item.setGrouphkbbje(UFDouble.ZERO_DBL);
			item.setGlobalhkbbje(UFDouble.ZERO_DBL);
		}
	}

	private static void prepareHeader(JKBXHeaderVO parentVO, BxcontrastVO[] bxcontrastVOs) throws BusinessException {
		if (parentVO == null)
			return;

		boolean needRecalBbje = true;// 是否需要重新计算本币金额
		if ("iweb".equals(parentVO.getSrcsystem())) {
			needRecalBbje = false;// 来源于新网报的数据不需要重新计算本币金额
		}
		// 设置其他默认值
		parentVO.setDr(Integer.valueOf(0));
		if (parentVO.getSpzt() == null) {
			parentVO.setSpzt(IBillStatus.FREE);
		}
		parentVO.setQzzt(BXStatusConst.STATUS_NOTVALID);
		if (!parentVO.isAdjustBxd()) {
			parentVO.setPayflag(BXStatusConst.PAYFLAG_None);
		}else{
			// 费用调整单设置支付状态102=调整
			parentVO.setPayflag(BXStatusConst.PAYFLAG_ADJUST);
		}

		if (parentVO.getDjdl() == null || parentVO.getDjzt() == null) {
			return;
		}

		if (parentVO.getDjdl().equals(BXConstans.BX_DJDL)) {
			parentVO.setPk_billtype(BXConstans.BX_DJLXBM);
		} else {
			parentVO.setPk_billtype(BXConstans.JK_DJLXBM);
		}

		// 交易类型设置默认值
		String pk_tradetypeid = PfDataCache.getBillTypeInfo(parentVO.getPk_group(), parentVO.getDjlxbm())
				.getPk_billtypeid();
		parentVO.setPk_tradetypeid(pk_tradetypeid);

		if (parentVO.getPk_group() == null && parentVO.isInit()) {
			parentVO.setPk_group(BXConstans.GROUP_CODE);
			return;
		}

		// 设置冲销完成日期默认值
		parentVO.setContrastenddate(new UFDate(BXConstans.DEFAULT_CONTRASTENDDATE));

		String djdl = parentVO.getDjdl();

		UFDouble bbhl = parentVO.getBbhl();
		UFDouble globalbbhl = parentVO.getGlobalbbhl();
		UFDouble groupbbhl = parentVO.getGroupbbhl();
		String bzbm = parentVO.getBzbm();
		UFDate djrq = parentVO.getDjrq();
		String pk_org = parentVO.getPk_org();

		if (needRecalBbje && pk_org != null && bzbm != null && parentVO.getYbje() != null && djrq != null) {
			// 重新设置表头本币金额
			parentVO.setBbje(Currency.computeYFB(pk_org, Currency.Change_YBJE, bzbm, parentVO.getYbje(), null, null,
					null, bbhl, djrq)[2]);
		}

		// 设置限额型单据控制项
		if (!parentVO.isInit() && !(parentVO.getDjzt().intValue() == BXStatusConst.DJZT_TempSaved)) {
			if (djdl.equals(BXConstans.JK_DJDL)) {
				if (parentVO.getIscheck().booleanValue()) {
					parentVO.setYbje(null);
					parentVO.setBbje(null);
					if (parentVO.getZpxe() == null)
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
								"UPP2011-000311"));
					if (parentVO.getZpxe().doubleValue() <= 0)
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
								"UPP2011-000312"));
				} else {
					if (parentVO.getYbje() == null)
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
								"UPP2011-000313"));
					if (parentVO.getYbje() == null)
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
								"UPP2011-000314"));

					parentVO.setZpxe(null);
				}
			} else {

				// 金额字段是否含税
				boolean paramIncludeTax = SysInit.getParaBoolean(pk_org, BXParamConstant.PARAM_IS_INCLUDE_TAX)
						.booleanValue();
				if (!parentVO.getDjlxbm().equals("2647") && !parentVO.getDjlxbm().equals("264a")) {// 还款单不走这个校验
					if (paramIncludeTax) {
						// 费用调整单可录入负数、0数据，报销单可以为负数
						if (parentVO.getYbje() == null
								|| (!parentVO.isAdjustBxd()
										&& !BXConstans.BILLTYPECODE_RETURNBILL.equals(parentVO.getDjlxbm()) && parentVO
										.getYbje().doubleValue() == 0)) {
							throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
									"UPP2011-000315"));
						}
					} else {// 不含税按照含税金额字段校验
						if (parentVO.getVat_amount() == null
								|| (!parentVO.isAdjustBxd()
										&& !BXConstans.BILLTYPECODE_RETURNBILL.equals(parentVO.getDjlxbm()) && parentVO
										.getVat_amount().doubleValue() == 0)) {
							throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
									"UPP2011-000315"));
						}
					}
				}
			}
			// 补齐未冲借款的报销单支付金额.
			UFDouble cjkybje = UFDouble.ZERO_DBL;
			UFDouble cjkbbje = UFDouble.ZERO_DBL;
			UFDouble groupcjkbbje = UFDouble.ZERO_DBL;
			UFDouble globalcjkbbje = UFDouble.ZERO_DBL;

			if (bxcontrastVOs != null) {
				for (BxcontrastVO vo : bxcontrastVOs) {

					// 校验冲借款VO
					vo.validate();

					// 重新设置冲借款本币金额
					vo.setCjkbbje(Currency.computeYFB(pk_org, Currency.Change_YBJE, bzbm, vo.getCjkybje(), null, null,
							null, bbhl, djrq)[2]);
					vo.setFybbje(Currency.computeYFB(pk_org, Currency.Change_YBJE, bzbm, vo.getFyybje(), null, null,
							null, bbhl, djrq)[2]);

					UFDouble[] ggcjkbbje = Currency.computeGroupGlobalAmount(vo.getCjkybje(), vo.getCjkbbje(), bzbm,
							djrq, parentVO.getPk_org(), parentVO.getPk_group(), globalbbhl, groupbbhl);
					UFDouble[] ggfybbje = Currency.computeGroupGlobalAmount(vo.getFyybje(), vo.getFybbje(), bzbm, djrq,
							parentVO.getPk_org(), parentVO.getPk_group(), globalbbhl, groupbbhl);

					vo.setGroupcjkbbje(ggcjkbbje[0]);
					vo.setGlobalcjkbbje(ggcjkbbje[1]);
					vo.setGroupfybbje(ggfybbje[0]);
					vo.setGlobalfybbje(ggfybbje[1]);
					vo.setYbje(vo.getCjkybje());
					vo.setBbje(vo.getCjkbbje());
					vo.setGroupbbje(vo.getGroupcjkbbje());
					vo.setGlobalbbje(vo.getGlobalcjkbbje());

					cjkybje = cjkybje.add(vo.getCjkybje());
					cjkbbje = cjkbbje.add(vo.getCjkbbje());
					groupcjkbbje = groupcjkbbje.add(vo.getGroupcjkbbje());
					globalcjkbbje = globalcjkbbje.add(vo.getGlobalcjkbbje());
				}
			}

			adjuestCjkje(parentVO, cjkybje, cjkbbje, groupcjkbbje, globalcjkbbje);
		}

		// 补齐余额
		parentVO.setYbye(parentVO.getYbje());
		parentVO.setBbye(parentVO.getBbje());
		parentVO.setGroupbbye(parentVO.getGroupbbje());
		parentVO.setGlobalbbye(parentVO.getGlobalbbje());
		parentVO.setYjye(parentVO.getYbje());

		if (!parentVO.getDjzt().equals(BXStatusConst.DJZT_TempSaved)) {
			if (parentVO.getQcbz().booleanValue()) {
				parentVO.setDjzt(BXStatusConst.DJZT_Sign);
				parentVO.setSxbz(BXStatusConst.SXBZ_VALID);
			} else {
				parentVO.setDjzt(BXStatusConst.DJZT_Saved);
			}
		}

		if (parentVO.getTotal() == null) {
			parentVO.setTotal(parentVO.getYbje());
		}
	}

	private static void prepareHeaderForIweb(JKBXVO jkbxVO) throws BusinessException {
		JKBXHeaderVO parentVO = jkbxVO.getParentVO();
		BxcontrastVO[] bxcontrastVOs = jkbxVO.getContrastVO();
		if (null != bxcontrastVOs) {
			for (BxcontrastVO vo : bxcontrastVOs) {// 因为NC端可能修改单据冲借款，这里给币种汇率赋值
				if (null == vo.getBzbm()) {
					vo.setBzbm(parentVO.getBzbm());
				}
				UFDouble[] hlArr = ErmBillCalUtil.getRate(vo.getBzbm(), parentVO.getPk_org(), parentVO.getPk_group(), parentVO.getDjrq());// 取汇率
				vo.setBbhl(hlArr[0]);
				vo.setGroupbbhl(hlArr[1]);
				vo.setGlobalbbhl(hlArr[2]);
			}		
		}
		boolean isMultiBZ = false;
		Set<String> bzSet = new HashSet<String>();
		bzSet.add(jkbxVO.getParentVO().getBzbm());
		for (BXBusItemVO itemVO : jkbxVO.getBxBusItemVOS()) {
			bzSet.add(itemVO.getBzbm());
		}
		if (bzSet.size() > 1) {
			isMultiBZ = true;
		}
		if (parentVO == null)
			return;
		// 设置其他默认值
		parentVO.setDr(Integer.valueOf(0));
		if (parentVO.getSpzt() == null) {
			parentVO.setSpzt(IBillStatus.FREE);
		}
		parentVO.setQzzt(BXStatusConst.STATUS_NOTVALID);
		if (!parentVO.isAdjustBxd()) {
			parentVO.setPayflag(BXStatusConst.PAYFLAG_None);
		}else{
			// 费用调整单设置支付状态102=调整
			parentVO.setPayflag(BXStatusConst.PAYFLAG_ADJUST);
		}
		if (parentVO.getDjdl() == null || parentVO.getDjzt() == null) {
			return;
		}
		if (parentVO.getDjdl().equals(BXConstans.BX_DJDL)) {
			parentVO.setPk_billtype(BXConstans.BX_DJLXBM);
		} else {
			parentVO.setPk_billtype(BXConstans.JK_DJLXBM);
		}
		// 交易类型设置默认值
		String pk_tradetypeid = PfDataCache.getBillTypeInfo(parentVO.getPk_group(), parentVO.getDjlxbm())
				.getPk_billtypeid();
		parentVO.setPk_tradetypeid(pk_tradetypeid);
		if (parentVO.getPk_group() == null && parentVO.isInit()) {
			parentVO.setPk_group(BXConstans.GROUP_CODE);
			return;
		}
		// 设置冲销完成日期默认值
		parentVO.setContrastenddate(new UFDate(BXConstans.DEFAULT_CONTRASTENDDATE));
		String djdl = parentVO.getDjdl();
		UFDouble globalbbhl = parentVO.getGlobalbbhl();
		UFDouble groupbbhl = parentVO.getGroupbbhl();
		UFDate djrq = parentVO.getDjrq();
		String pk_org = parentVO.getPk_org();
		// 设置限额型单据控制项
		if (!parentVO.isInit() && !(parentVO.getDjzt().intValue() == BXStatusConst.DJZT_TempSaved)) {
			if (djdl.equals(BXConstans.JK_DJDL)) {
				if (parentVO.getIscheck().booleanValue()) {
					parentVO.setYbje(null);
					parentVO.setBbje(null);
					if (parentVO.getZpxe() == null)
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
								"UPP2011-000311"));
					if (parentVO.getZpxe().doubleValue() <= 0)
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
								"UPP2011-000312"));
				} else {
					if (parentVO.getYbje() == null)
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
								"UPP2011-000313"));
					if (parentVO.getYbje() == null)
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
								"UPP2011-000314"));

					parentVO.setZpxe(null);
				}
			} else {
				// 金额字段是否含税
				boolean paramIncludeTax = SysInit.getParaBoolean(pk_org, BXParamConstant.PARAM_IS_INCLUDE_TAX)
						.booleanValue();
				if (!parentVO.getDjlxbm().equals("2647") && !parentVO.getDjlxbm().equals("264a")) {// 还款单不走这个校验
					if (paramIncludeTax) {
						// XBX 单价合同，金额可以为0
						if(!"Y".equals(parentVO.getZyx17())){
							// 费用调整单可录入负数、0数据，报销单可以为负数
							if (parentVO.getYbje() == null
									|| (!parentVO.isAdjustBxd()
											&& !BXConstans.BILLTYPECODE_RETURNBILL.equals(parentVO.getDjlxbm()) && parentVO
											.getYbje().doubleValue() == 0)) {
								throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
										"UPP2011-000315"));
							}
						}
					} else {// 不含税按照含税金额字段校验
						if (parentVO.getVat_amount() == null
								|| (!parentVO.isAdjustBxd()
										&& !BXConstans.BILLTYPECODE_RETURNBILL.equals(parentVO.getDjlxbm()) && parentVO
										.getVat_amount().doubleValue() == 0)) {
							throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
									"UPP2011-000315"));
						}
					}
				}
			}
			// 补齐未冲借款的报销单支付金额.
			UFDouble cjkybje = UFDouble.ZERO_DBL;
			UFDouble cjkbbje = UFDouble.ZERO_DBL;
			UFDouble groupcjkbbje = UFDouble.ZERO_DBL;
			UFDouble globalcjkbbje = UFDouble.ZERO_DBL;
			if (bxcontrastVOs != null) {
				for (BxcontrastVO vo : bxcontrastVOs) {
					// 校验冲借款VO
					vo.validate();
					// 重新设置冲借款本币金额
					vo.setCjkbbje(Currency.computeYFB(pk_org, Currency.Change_YBJE, vo.getBzbm(), vo.getCjkybje(),
							null, null, null, vo.getBbhl(), djrq)[2]);
					vo.setFybbje(Currency.computeYFB(pk_org, Currency.Change_YBJE, vo.getBzbm(), vo.getFyybje(), null,
							null, null, vo.getBbhl(), djrq)[2]);
					UFDouble[] ggcjkbbje = Currency.computeGroupGlobalAmount(vo.getCjkybje(), vo.getCjkbbje(),
							vo.getBzbm(), djrq, parentVO.getPk_org(), parentVO.getPk_group(), globalbbhl, groupbbhl);
					UFDouble[] ggfybbje = Currency.computeGroupGlobalAmount(vo.getFyybje(), vo.getFybbje(),
							vo.getBzbm(), djrq, parentVO.getPk_org(), parentVO.getPk_group(), globalbbhl, groupbbhl);

					vo.setGroupcjkbbje(ggcjkbbje[0]);
					vo.setGlobalcjkbbje(ggcjkbbje[1]);
					vo.setGroupfybbje(ggfybbje[0]);
					vo.setGlobalfybbje(ggfybbje[1]);
					vo.setYbje(vo.getCjkybje());
					vo.setBbje(vo.getCjkbbje());
					vo.setGroupbbje(vo.getGroupcjkbbje());
					vo.setGlobalbbje(vo.getGlobalcjkbbje());

					cjkybje = cjkybje.add(vo.getCjkybje());
					cjkbbje = cjkbbje.add(vo.getCjkbbje());
					groupcjkbbje = groupcjkbbje.add(vo.getGroupcjkbbje());
					globalcjkbbje = globalcjkbbje.add(vo.getGlobalcjkbbje());
				}
			}
			if (!isMultiBZ) {// 多币种时不执行冲借款金额调整
				adjuestCjkje(parentVO, cjkybje, cjkbbje, groupcjkbbje, globalcjkbbje);
			}
			if (!"2647".equals(jkbxVO.getParentVO().getDjlxbm()) && null != jkbxVO.getContrastVO()
					&& jkbxVO.getContrastVO().length > 0) {
				doBodyTotalToHead(jkbxVO);// 存在冲借款时汇总表体金额字段到表头
			}
			if(isMultiBZ){//多币种时不支持分摊
				if(jkbxVO.getParentVO().getIscostshare().booleanValue()){
					throw new ValidationException("不支持多币种单据的分摊！");
				}
				if(jkbxVO.getParentVO().getIsexpamt().booleanValue()){
					throw new ValidationException("不支持多币种单据的待摊！");
				}
			}
		}

		// 补齐余额
		parentVO.setYbye(parentVO.getYbje());
		parentVO.setBbye(parentVO.getBbje());
		parentVO.setGroupbbye(parentVO.getGroupbbje());
		parentVO.setGlobalbbye(parentVO.getGlobalbbje());
		parentVO.setYjye(parentVO.getYbje());
		if (!parentVO.getDjzt().equals(BXStatusConst.DJZT_TempSaved)) {
			if (parentVO.getQcbz().booleanValue()) {
				parentVO.setDjzt(BXStatusConst.DJZT_Sign);
				parentVO.setSxbz(BXStatusConst.SXBZ_VALID);
			} else {
				parentVO.setDjzt(BXStatusConst.DJZT_Saved);
			}
		}
		if (parentVO.getTotal() == null) {
			parentVO.setTotal(parentVO.getYbje());
		}
	}

	
	private static void doBodyTotalToHead(JKBXVO jkbxVO) {
		UFDouble[] totalArr = new UFDouble[JKBXHeaderVO.getJeField().length];
		for (int i = 0; i < totalArr.length; i++) {
			totalArr[i] = UFDouble.ZERO_DBL;
		}
		UFDouble amountTotal = UFDouble.ZERO_DBL;// 表体AMOUNT汇总到表头TOTAL，单独处理
		for (BXBusItemVO item : jkbxVO.getBxBusItemVOS()) {
			for (int j = 0; j < JKBXHeaderVO.getJeField().length; j++) {
				totalArr[j] = totalArr[j]
						.add(null == item.getAttributeValue(JKBXHeaderVO.getJeField()[j]) ? UFDouble.ZERO_DBL
								: (UFDouble) item.getAttributeValue(JKBXHeaderVO.getJeField()[j]));
			}
			amountTotal = amountTotal.add(null == item.getAttributeValue(JKBXHeaderVO.AMOUNT) ? UFDouble.ZERO_DBL
					: (UFDouble) item.getAttributeValue(JKBXHeaderVO.AMOUNT));
		}
		for (int i = 0; i < JKBXHeaderVO.getJeField().length; i++) {
			jkbxVO.getParentVO().setAttributeValue(JKBXHeaderVO.getJeField()[i], totalArr[i]);
		}
		jkbxVO.getParentVO().setTotal(amountTotal);
	}

	public static void adjuestCjkje(JKBXHeaderVO parentVO, UFDouble cjkybje, UFDouble cjkbbje, UFDouble groupcjkbbje,
 UFDouble globalcjkbbje) {

		if (parentVO.isAdjustBxd()) {
			return;
		}
		UFDouble zero = UFDouble.ZERO_DBL;

		if (parentVO.getDjdl().equals("bx")) {
			if (UFDoubleTool.isXiangdeng(parentVO.getYbje(), cjkybje)) {
				parentVO.setZfybje(zero);
				parentVO.setZfbbje(parentVO.getBbje().sub(cjkbbje)
						.compareTo(zero) > 0 ? parentVO.getBbje().sub(cjkbbje)
						: zero);
				parentVO.setGroupzfbbje(parentVO.getGroupbbje()
						.sub(groupcjkbbje).compareTo(zero) > 0 ? parentVO
						.getGroupbbje().sub(groupcjkbbje) : zero);

				parentVO.setGlobalzfbbje(parentVO.getGlobalbbje()
						.sub(globalcjkbbje).compareTo(zero) > 0 ? parentVO
						.getGlobalbbje().sub(globalcjkbbje) : zero);

				parentVO.setHkybje(zero);
				parentVO.setHkbbje(cjkbbje.sub(parentVO.getBbje()).compareTo(
						zero) > 0 ? cjkbbje.sub(parentVO.getBbje()) : zero);
				parentVO.setGrouphkbbje(groupcjkbbje.sub(
						parentVO.getGroupbbje()).compareTo(zero) > 0 ? groupcjkbbje
						.sub(parentVO.getGroupbbje()) : zero);

				parentVO.setGlobalhkbbje(globalcjkbbje.sub(
						parentVO.getGlobalbbje()).compareTo(zero) > 0 ? globalcjkbbje
						.sub(parentVO.getGlobalbbje()) : zero);

			} else if (UFDoubleTool.isZero(cjkybje)) {
				if (parentVO.getYbje().doubleValue() > 0.0D) {
					parentVO.setZfybje(parentVO.getYbje());
					parentVO.setZfbbje(parentVO.getBbje());
					parentVO.setGroupzfbbje(parentVO.getGroupbbje());
					parentVO.setGlobalzfbbje(parentVO.getGlobalbbje());

					parentVO.setHkybje(zero);
					parentVO.setHkbbje(zero);
					parentVO.setGrouphkbbje(zero);
					parentVO.setGlobalhkbbje(zero);
				} else {
					parentVO.setHkybje(parentVO.getYbje().abs());
					parentVO.setHkbbje(parentVO.getBbje().abs());
					parentVO.setGrouphkbbje(parentVO.getGroupbbje().abs());
					parentVO.setGlobalhkbbje(parentVO.getGlobalbbje().abs());

					parentVO.setZfybje(zero);
					parentVO.setZfbbje(zero);
					parentVO.setGroupzfbbje(zero);
					parentVO.setGlobalzfbbje(zero);
				}
			} else if (UFDoubleTool.isXiaoyu(parentVO.getYbje(), cjkybje)) {
				parentVO.setZfybje(zero);
				parentVO.setZfbbje(zero);
				parentVO.setGroupzfbbje(zero);
				parentVO.setGlobalzfbbje(zero);

				parentVO.setHkybje(cjkybje.sub(parentVO.getYbje()));
				parentVO.setHkbbje(cjkbbje.sub(parentVO.getBbje()));
				parentVO.setGrouphkbbje(groupcjkbbje.sub(parentVO
						.getGroupbbje()));
				parentVO.setGlobalhkbbje(globalcjkbbje.sub(parentVO
						.getGlobalbbje()));
			} else if (UFDoubleTool.isXiaoyu(cjkybje, parentVO.getYbje())) {
				parentVO.setZfybje(parentVO.getYbje().sub(cjkybje));
				parentVO.setZfbbje(parentVO.getBbje().sub(cjkbbje));
				parentVO.setGroupzfbbje(parentVO.getGroupbbje().sub(
						groupcjkbbje));
				parentVO.setGlobalzfbbje(parentVO.getGlobalbbje().sub(
						globalcjkbbje));

				parentVO.setHkybje(zero);
				parentVO.setHkbbje(zero);
				parentVO.setGrouphkbbje(zero);
				parentVO.setGlobalhkbbje(zero);
			}
			parentVO.setCjkybje(cjkybje);
			parentVO.setCjkbbje(cjkbbje);
			parentVO.setGroupcjkbbje(groupcjkbbje);
			parentVO.setGlobalcjkbbje(globalcjkbbje);
		} else {
			parentVO.setZfybje(parentVO.getYbje());
			parentVO.setZfbbje(parentVO.getBbje());
			parentVO.setGroupzfbbje(parentVO.getGroupbbje());
			parentVO.setGlobalzfbbje(parentVO.getGlobalbbje());

			parentVO.setHkybje(zero);
			parentVO.setHkbbje(zero);
			parentVO.setGrouphkbbje(zero);
			parentVO.setGlobalhkbbje(zero);
		}

	}

	
	private void checkValidHeader(JKBXHeaderVO parentVO) throws BusinessException {
		parentVO.validate();
		if (!BXConstans.BILLTYPECODE_RETURNBILL.equals(parentVO.getDjlxbm())) {
			// 报销单允许录入负数行，但不可以为0
			if (BXConstans.BX_DJDL.equals(parentVO.getDjdl())) {
				// 费用调整单不控制合计金额为0、负数
				boolean isAdjust = parentVO.isAdjustBxd();
				if (getIsInclude(parentVO.getPk_org())) {
					// XBX 单价合同，金额可以为0
					if(!"Y".equals(parentVO.getZyx17())){
						if (parentVO.getTotal().compareTo(UFDouble.ZERO_DBL) == 0 && !isAdjust) {
							throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
									"expensepub_0", "02011002-0178"));
						}
					}
				} else {// 不含税按照含税校验
					if (!isAdjust && parentVO.getVat_amount().compareTo(UFDouble.ZERO_DBL) == 0) {
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"expensepub_0", "02011002-0178"));
					}
				}

			} else {
				// 借款单金额都要大于0
				if (parentVO.getTotal().compareTo(UFDouble.ZERO_DBL) <= 0) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0177"));
				}
			}
		}
	}

	private void checkHeadFinItemJe(JKBXVO bxvo) throws ValidationException {
		BXBusItemVO[] childrenVO = bxvo.getChildrenVO();

		if (childrenVO == null || childrenVO.length == 0) {
			return;
		}

		JKBXHeaderVO parentVO = bxvo.getParentVO();
		String[] keys = new String[] { "ybje", "bbje", 
		"hkybje", "hkbbje", "zfybje", "zfbbje", "cjkybje", "cjkbbje" };
		String[] name = new String[] {
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000280"),
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000245"),
				// nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
				// "UPP2011-000318"),
				// nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
				// "UPP2011-000246"),
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000319"),
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000320"),
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000321"),
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000322"),
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000323"),
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000324") };
		int length = keys.length;
		for (int j = 0; j < length; j++) {

			UFDouble headJe = parentVO.getAttributeValue(keys[j]) == null ? UFDouble.ZERO_DBL : (UFDouble) parentVO
					.getAttributeValue(keys[j]);
			UFDouble bodyJe = UFDouble.ZERO_DBL;
			for (int i = 0; i < childrenVO.length; i++) {
				UFDouble je = childrenVO[i].getAttributeValue(keys[j]) == null ? UFDouble.ZERO_DBL
						: (UFDouble) childrenVO[i].getAttributeValue(keys[j]);
				if (je != null)
					bodyJe = bodyJe.add(je);

			}

			if (headJe.compareTo(bodyJe) != 0) {
				// 本币金额误差的容错处理
				if (j % 2 == 1 && headJe.sub(bodyJe).abs().compareTo(new UFDouble(1)) < 0) {
					parentVO.setAttributeValue(keys[j], bodyJe);
					continue;
				}
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000325", null, new String[] { name[j] }));
			}

		}
	}

	
	private void checkHeadItemJe(JKBXVO bxvo) throws BusinessException {
		UFDouble total = bxvo.getParentVO().getTotal();
		UFDouble ybje = bxvo.getParentVO().getYbje();
		UFDouble vatAmount = bxvo.getParentVO().getVat_amount();
		// 费用调整单不控制合计金额为0、负数
		boolean isAdjust = bxvo.getParentVO().isAdjustBxd();
		if (!bxvo.getParentVO().getDjlxbm().equals(BXConstans.BILLTYPECODE_RETURNBILL) && !isAdjust) {

			// 营改增新加
			if (bxvo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
				if (getIsInclude(bxvo.getParentVO().getPk_org())) {
					if (total == null || total.compareTo(UFDouble.ZERO_DBL) == 0) {

						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
								"UPP2011-000916"));
					}

					if (ybje == null || ybje.compareTo(UFDouble.ZERO_DBL) == 0) {
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
								"UPP2011-000917"));
					}
				} else {
					if (vatAmount == null || vatAmount.compareTo(UFDouble.ZERO_DBL) == 0) {

						throw new ValidationException("表头含税金额不可以为0！");
					}
				}
			} else {
				if (total == null || total.compareTo(UFDouble.ZERO_DBL) == 0) {

					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
							"UPP2011-000916"));
				}

				if (ybje == null || ybje.compareTo(UFDouble.ZERO_DBL) == 0) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
							"UPP2011-000917"));
				}
			}

		}

		BXBusItemVO[] childrenVO = bxvo.getBxBusItemVOS();
		if (childrenVO == null || childrenVO.length == 0) {
			return;
		}

		if (bxvo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
			UFDouble amount = UFDouble.ZERO_DBL;
			for (int i = 0; i < childrenVO.length; i++) {
				amount = amount.add(childrenVO[i].getAmount());
			}
			if (total.compareTo(amount) != 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000327"));
			}
		} else {
			UFDouble amount = UFDouble.ZERO_DBL;
			for (int i = 0; i < childrenVO.length; i++) {
				amount = amount.add(childrenVO[i].getAmount());
			}
			if (ybje.compareTo(amount) != 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000328"));
			}
		}
	}

	
	private void checkValidChildrenVO(JKBXVO jkbxvo) throws BusinessException {
		BXBusItemVO[] childrenVO = jkbxvo.getChildrenVO();
		childrenVO = removeNullItem(childrenVO);

		if ((childrenVO == null || childrenVO.length == 0)) {
			return;
		}

		for (BXBusItemVO child : childrenVO) {
			child.validate();
			if (child.getTablecode() == null) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0144"));
			}
		}

		if (!BXConstans.BILLTYPECODE_RETURNBILL.equals(jkbxvo.getParentVO().getDjlxbm())) {
			Map<String, List<BXBusItemVO>> bzvoMap = new HashMap<String, List<BXBusItemVO>>();
			for (BXBusItemVO child : childrenVO) {
				// 报销单允许录入负数行，但不可以为0
				if (BXConstans.BX_DJDL.equals(jkbxvo.getParentVO().getDjdl())) {
					if (child.getBzbm() != null) {
						if (bzvoMap.containsKey(child.getBzbm())) {
							bzvoMap.get(child.getBzbm()).add(child);
						} else {
							List<BXBusItemVO> newList = new ArrayList<BXBusItemVO>();
							newList.add(child);
							bzvoMap.put(child.getBzbm(), newList);
						}
					}
					if (getIsInclude(jkbxvo.getParentVO().getPk_org())) {
						if (child.getYbje().compareTo(UFDouble.ZERO_DBL) != 0
								&& child.getBbje().compareTo(UFDouble.ZERO_DBL) == 0) {
							throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
									"expensepub_0", "02011002-0176"));
						}
					} else {
						if (child.getVat_amount().compareTo(UFDouble.ZERO_DBL) != 0
								&& child.getOrgvat_amount().compareTo(UFDouble.ZERO_DBL) == 0) {
							throw new ValidationException("报销单表体含税金额不可以等于0!");
						}
					}
				} else {
					// 借款单金额都要大于0
					if (child.getBbje().compareTo(UFDouble.ZERO_DBL) <= 0) {
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"expensepub_0", "02011002-0175"));
					}
				}
			}
			// 报销单相同币种不允许同时存在还款和支付的行
			boolean isHKZF = false;
			for (Entry<String, List<BXBusItemVO>> entry : bzvoMap.entrySet()) {
				boolean isHK = false;
				boolean isZF = false;
				for (BXBusItemVO eachVO : entry.getValue()) {
					if (eachVO.getHkybje().compareTo(UFDouble.ZERO_DBL) > 0) {
						isHK = true;
					}
					if (eachVO.getZfybje().compareTo(UFDouble.ZERO_DBL) > 0) {
						isZF = true;
					}
				}
				isHKZF = isHK && isZF;
				if (isHKZF) {
					break;
				}
			}
			if (isHKZF) {
				throw new ValidationException("报销金额发生变化，请重新冲借款！");
			}
		}
	}

	private BXBusItemVO[] removeNullItem(BXBusItemVO[] childrenVO) {
		List<BXBusItemVO> bxBusItemVOs = new ArrayList<BXBusItemVO>();
		boolean hasNullItem = false;
		for (BXBusItemVO child : childrenVO) {
			if (!child.isNullItem()) {
				bxBusItemVOs.add(child);
			} else {
				hasNullItem = true;
			}
		}
		if (hasNullItem)
			childrenVO = bxBusItemVOs.toArray(new BXBusItemVO[] {});
		return childrenVO;
	}

	private void checkFinRange(JKBXVO bxvo) throws ValidationException {
		JKBXHeaderVO parentVO = bxvo.getParentVO();
		if (bxvo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
			// 表头财务核报金额与合计金额的容差校验
			Double range = UFDouble.ZERO_DBL.getDouble();
			try {
				// 属于业务单元级别的参数 注意此过程需要新建立集团业务单元复制
				if (SysInit.getParaInt(parentVO.getPk_org(), BXParamConstant.PARAM_ER_FI_RANGE) == null)
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
							"UPP2011-000332"));
				range = SysInit.getParaInt(parentVO.getPk_org(), BXParamConstant.PARAM_ER_FI_RANGE).doubleValue();
			} catch (BusinessException e) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000332"));
			}
			if (range == null)
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000333"));
			Double total = parentVO.getTotal() == null ? 0 : parentVO.getTotal().toDouble();// 合计金额
			Double ybje = parentVO.getYbje() == null ? 0 : parentVO.getYbje().toDouble();// 财务核报金额

			if (range.doubleValue() < 0) { // 只能改小不能改大
				if (ybje > total) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0145"));
				}
			}
			if (Math.abs(total - ybje) > Math.abs(range))
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000334"));
		}
	}

	
	private void checkValidFinItemVO(JKBXVO bxvo) throws ValidationException {
		BXBusItemVO[] childrenVO = bxvo.getChildrenVO();
		boolean ispay = false;
		boolean isreceive = false;

		if (childrenVO != null && childrenVO.length > 0) {
			for (BXBusItemVO child : childrenVO) {
				child.validate();
				// 报销单可以录入负责，但不可以录入0，但借款单但不允许
				if (bxvo.getParentVO().getDjdl().equals(BXConstans.JK_DJDL)) {
					if (child.getYbje().compareTo(UFDouble.ZERO_DBL) <= 0
							&& child.getCjkybje().compareTo(UFDouble.ZERO_DBL) <= 0) {
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"2011v61013_0", "02011v61013-0088"));
					}
				}
				// ehp2版本：业务行金额可以等于0
				// if (bxvo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL))
				// {
				// if (child.getybje().compareTo(UFDouble.ZERO_DBL) == 0
				// && child.getCjkybje().compareTo(UFDouble.ZERO_DBL) == 0) {
				// throw new
				// ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0",
				// "02011v61013-0090"));
				// }
				// }

				if (child.getZfybje().compareTo(UFDouble.ZERO_DBL) > 0) {
					ispay = true;
				}
				if (child.getHkybje().compareTo(UFDouble.ZERO_DBL) > 0) {
					isreceive = true;
				}
			}
			// if (ispay && isreceive) {
			// throw new
			// ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
			// "UPP2011-000397"));
			// }
		}
		checkHeadFinItemJe(bxvo);
	}

	
	public static void checkErmIsCloseAcc(JKBXVO bxvo) throws BusinessException {
		JKBXHeaderVO head = (bxvo.getParentVO());
		String moduleCode = BXConstans.ERM_MODULEID;
		String pk_org = head.getPk_org();
		UFDate date = head.getDjrq();
		// 非期初单据才校验
		if (bxvo.getParentVO().getQcbz() == null || !bxvo.getParentVO().getQcbz().booleanValue()) {
			if (ErUtil.isOrgCloseAcc(moduleCode, pk_org, date)) {
				throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0146"));
			}
		}
	}

	
	private void checkBillContrast(JKBXVO bxvo) throws BusinessException {
		if (BXConstans.BILLTYPECODE_RETURNBILL.equals(bxvo.getParentVO().getDjlxbm())) {
			BxcontrastVO[] contrastVO = bxvo.getContrastVO();
			if (contrastVO == null || (contrastVO != null && contrastVO.length == 0)) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000919")
				);
			}
		}
		if (!bxvo.getParentVO().getDjlxbm().equals(BXConstans.BILLTYPECODE_RETURNBILL)
				&& bxvo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
			BxcontrastVO[] contrastVO = bxvo.getContrastVO();
			if (contrastVO != null && contrastVO.length != 0) {
				BXBusItemVO[] childrenVO = bxvo.getChildrenVO();
				if (childrenVO != null && childrenVO.length != 0) {
					for (BXBusItemVO bxBusItemVO : childrenVO) {
						if (bxBusItemVO.getYbje().compareTo(UFDouble.ZERO_DBL) <= 0) {
							throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
									"UPP2011-000905"));
						}
					}
				}
			}
		}
		BxcontrastVO[] contrastVO = bxvo.getContrastVO();
		if (contrastVO != null && contrastVO.length != 0) {
			UFDouble cjkybjeTotal = UFDouble.ZERO_DBL;
			for (BxcontrastVO bxcontrastVO : contrastVO) {
				if (bxcontrastVO.getCjkybje().compareTo(UFDouble.ZERO_DBL) <= 0) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
							"UPP2011-000918"));
				}
				cjkybjeTotal = cjkybjeTotal.add(bxcontrastVO.getCjkybje());
			}

			UFDouble cjkybjeHead = null == bxvo.getParentVO().getCjkybje() ? UFDouble.ZERO_DBL : bxvo.getParentVO()
					.getCjkybje();
			if (cjkybjeHead.compareTo(cjkybjeTotal) != 0) {
				throw new ValidationException("报销金额发生变化，请重新冲借款");
			}

		}
	}

	public List<String> getNotRepeatFields() {
		if (notRepeatFields == null) {
			notRepeatFields = new ArrayList<String>();
			notRepeatFields.add(CShareDetailVO.ASSUME_ORG);
			notRepeatFields.add(CShareDetailVO.ASSUME_DEPT);
			notRepeatFields.add(CShareDetailVO.PK_IOBSCLASS);
			notRepeatFields.add(CShareDetailVO.PK_PCORG);
			notRepeatFields.add(CShareDetailVO.PK_RESACOSTCENTER);
			notRepeatFields.add(CShareDetailVO.JOBID);
			notRepeatFields.add(CShareDetailVO.PROJECTTASK);
			notRepeatFields.add(CShareDetailVO.PK_CHECKELE);
			notRepeatFields.add(CShareDetailVO.CUSTOMER);
			notRepeatFields.add(CShareDetailVO.HBBM);
			notRepeatFields.add(CShareDetailVO.PK_PROLINE);
			notRepeatFields.add(CShareDetailVO.PK_BRAND);
		}
		return notRepeatFields;
	}

	
	public boolean getIsInclude(String pk_org) throws BusinessException {
		return SysInit.getParaBoolean(pk_org, BXParamConstant.PARAM_IS_INCLUDE_TAX).booleanValue();
	}
}
