package nc.vo.cmp.bill;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.cmp.pub.cache.FiPubDataCache;
import nc.cmp.pub.exception.ExceptionHandler;
import nc.cmp.utils.CmpUtils;
import nc.cmp.utils.DataUtil;
import nc.cmp.utils.Lists;
import nc.cmp.utils.OrgUnitsUtils;
import nc.cmp.utils.UFDoubleUtils;
import nc.impl.cmp.proxy.Proxy;
import nc.itf.cm.prv.CmpConst;
import nc.itf.cmp.fieldmap.IBillFieldGet;
import nc.md.data.access.NCObject;
import nc.vo.cmp.BusiInfo;
import nc.vo.cmp.BusiStatus;
import nc.vo.cmp.SettleStatus;
import nc.vo.cmp.djlx.DjLXVO;
import nc.vo.cmp.fields.IBillFieldAdapter;
import nc.vo.cmp.settlement.CheckException;
import nc.vo.cmp.settlement.CmpMsg;
import nc.vo.cmp.settlement.NodeType;
import nc.vo.cmp.settlement.SettleEnumCollection;
import nc.vo.cmp.settlement.SettlementAggVO;
import nc.vo.cmp.settlement.SettlementBodyVO;
import nc.vo.cmp.settlement.SettlementHeadVO;
import nc.vo.cmp.settlement.SettleEnumCollection.Direction;
import nc.vo.cmp.util.StringUtils;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.tmpub.util.ArrayUtil;
import nc.vo.tmpub.util.TMCurrencyUtil;

/**
 * 单据的常用工具类
 * 
 * @author liuzz
 * 
 */
@SuppressWarnings("deprecation")
public final class BillUtils {

	private BillUtils() {
	}

	public static final String SYS_CODE = "CMP";

	private static List<SuperVO> splitDjzbitems(List<SuperVO> djzbitemsList, SuperVO head, AssembleBillVO itemvo,
			List<SettlementBodyVO> list) throws BusinessException {
		AssembleBillVO tempdjzbitem = itemvo;
		for (SettlementBodyVO settlementBodyVO : list) {
			// if ( map.get(pk)==null) {
			// throw
			// ExceptionHandler.createException("传入的业务表体行和结算表体行的对应关系有误！");
			// }
			tempdjzbitem = (AssembleBillVO) itemvo.clone();
			if (tempdjzbitem.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(IBillFieldGet.DIRECTION)) == BillEnumCollection.Direction.JF) {// 借方
				if ("DR".equals(head.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
						IBillFieldGet.H_PK_TRADETYPE)))) {
					// tempdjzbitem.setJfbbje(settlementBodyVO.getPaylocal());
					tempdjzbitem.setAttributeValue(
							CmpBillFieldGet.getInstance().getFieldName(IBillFieldGet.LOCAL_MONEY_DE),
							settlementBodyVO.getPaylocal());
				} else {
					tempdjzbitem.setAttributeValue(CmpBillFieldGet.getInstance().getFieldName(IBillFieldGet.MONEY_DE),
							settlementBodyVO.getPay());
					tempdjzbitem.setAttributeValue(
							CmpBillFieldGet.getInstance().getFieldName(IBillFieldGet.LOCAL_MONEY_DE),
							settlementBodyVO.getPaylocal());
					// try {
					// tempdjzbitem =
					// ArapDjCalculator.getInstance().calculateVO(tempdjzbitem,
					// "jfybje", head.getDjrq().toString(), head.getDjdl(),
					// ArapDjCalculator.getInstance().getProior(head));
					// } catch (Exception e) {
					// throw ExceptionHandler.handleException(e);
					// } 
				}
			} else if (tempdjzbitem.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
					IBillFieldGet.DIRECTION)) == BillEnumCollection.Direction.DF) {// 贷方
				if ("DR".equals(head.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
						IBillFieldGet.H_PK_TRADETYPE)))) {
					// tempdjzbitem.setDfybje(settlementBodyVO.getReceivelocal());
					tempdjzbitem.setAttributeValue(
							CmpBillFieldGet.getInstance().getFieldName(IBillFieldGet.LOCAL_MONEY_CR),
							settlementBodyVO.getReceive());
				} else {
					tempdjzbitem.setAttributeValue(CmpBillFieldGet.getInstance().getFieldName(IBillFieldGet.MONEY_CR),
							settlementBodyVO.getReceive());
					// tempdjzbitem.setDfybje(settlementBodyVO.getReceive());
					// try {
					// tempdjzbitem =
					// ArapDjCalculator.getInstance().calculateVO(tempdjzbitem,
					// "dfybje", head.getDjrq().toString(), head.getDjdl(),
					// ArapDjCalculator.getInstance().getProior(head));
					// } catch (Exception e) {
					//
					// throw ExceptionHandler.handleException(e);
					// }
				}
			}
			djzbitemsList.add(tempdjzbitem);
			tempdjzbitem.setSettleBodyVO(settlementBodyVO);
		}
		return djzbitemsList;
	}

	public static AggregatedValueObject getDJZBVOBySettlementVO(AggregatedValueObject djzbvo,
			AggregatedValueObject cloneDJZBVO, Map<String, List<SettlementBodyVO>> detailMap) throws BusinessException {
		// Map<String, DJZBItemVO> map = getDJZBItemVOPKMap(djzbvo);
		// AggregatedValueObject
		// cloneDJZBVO=(AggregatedValueObject)djzbvo.clone();//应郭殿伟要求添加
		List<SuperVO> djzbitemsList = new ArrayList<SuperVO>();
		nc.vo.cmp.bill.AssembleBillVO tempdjzbitem = null;
		// List<SettlementBodyVO> list = null;
		SuperVO clonehead = (SuperVO) djzbvo.getParentVO().clone();
		SuperVO[] items = (SuperVO[]) djzbvo.getChildrenVO();
		// AssembleBillVO[] cloneitems = new
		// AssembleBillVO[djzbvo.getChildrenVO().length];
		// for (int i = 0; i < items.length; i++) {
		// cloneitems[i] = (AssembleBillVO) items[i].clone();
		// }
		for (CircularlyAccessibleValueObject item : items) {
			tempdjzbitem = (AssembleBillVO) item.clone();

			djzbitemsList.add(tempdjzbitem);

		}
		// AggregatedValueObject cloneDJZBVO=new AggregatedValueObject();
		cloneDJZBVO.setParentVO(clonehead);
		cloneDJZBVO.setChildrenVO(djzbitemsList.toArray(new SuperVO[0]));
		return cloneDJZBVO;
	}

	/**
	 * 将表头信息转化为CmpMsg
	 * 
	 * @param bean
	 * @return
	 */

	public static CmpMsg convertBeanToCmpMsg(BaseBillVO basebill) {
		// HYBillVO aggvo = (HYBillVO) basebill.getBeanList()[0];
		// huzw
		AggregatedValueObject aggvo = basebill.getBeanList()[0];
		IBillFieldAdapter bfa = (IBillFieldAdapter) aggvo;
		SuperVO bean = (SuperVO) aggvo.getParentVO();
		CmpMsg msg = new CmpMsg();
		msg.setAuthList(null);

		msg.setAutoSign(false);
		msg.setBillclass(BaseFile.BILL_TYPE);
		msg.setBillcode((String) bean.getAttributeValue(BaseFile.BILL_NO));
		msg.setBillDate((UFDate) bean.getAttributeValue(BaseFile.BILL_DATE));
		msg.setBillkey(bean.getPrimaryKey());
		msg.setBillOperator((String) bean.getAttributeValue(BaseFile.PK_RECODER));

		msg.setBilltype((String) bfa.getIBillFieldGet().getAtrrValue(bean, IBillFieldGet.H_PK_TRADETYPE));
		msg.setBusiFlow((String) bean.getAttributeValue(BaseFile.PK_BUSIFLOW));

		Integer billstatus = (Integer) (bfa.getIBillFieldGet().getAtrrValue(bean, IBillFieldGet.H_BILLSTATUS));
		if (billstatus != null) {
			msg.setBusistatus(BusiStatus.convertInt2Enum(billstatus));
		}

		msg.setOperateStatus(getOperteStatus(basebill));
		msg.setCopyMap(getCopyMap(aggvo));
		msg.setDirection(getDirection(aggvo));
		msg.setEbankRed(false);
		msg.setFtsExec(bean.getAttributeValue(BaseFile.ISJSZXZF) == null ? false : ((UFBoolean) bean
				.getAttributeValue(BaseFile.ISJSZXZF)).booleanValue());
		msg.setIdmap(basebill.getIdMap());
		msg.setIspay(getDirection(aggvo) == CmpConst.Direction_Pay ? true : false);
		msg.setLastOperator((String) bean.getAttributeValue(BaseFile.MODIFIER));
		UFDateTime datetime = (UFDateTime) bean.getAttributeValue(BaseFile.MODIFIEDTIME);
		msg.setLastOperatorDate(datetime == null ? null : datetime.getDate());
		msg.setLocal((UFDouble) bean.getAttributeValue(BaseFile.LOCAL));
		msg.setPrimal((UFDouble) bean.getAttributeValue(BaseFile.PRIMAL));
		msg.setGrouplocal((UFDouble) bean.getAttributeValue("group_local"));
		msg.setGloballocal((UFDouble) bean.getAttributeValue("global_local"));
		// msg.setOperateStatus(getOperteStatus(basebill));
		msg.setPk_group((String) bean.getAttributeValue(BaseFile.PK_GROUP));
		msg.setPk_org((String) bean.getAttributeValue(BaseFile.PK_ORG));
		msg.setPk_org_v((String) bean.getAttributeValue("pk_org_v"));
		msg.setPk_pcorg((String) bean.getAttributeValue("pk_pcorg"));
		msg.setCostcenter((String) bean.getAttributeValue("costcenter"));
		msg.setPk_pcorg_v((String) bean.getAttributeValue("pk_pcorg_v"));
		msg.setSystem((String) bean.getAttributeValue("source_flag"));
		msg.setIscommpay((UFBoolean) bean.getAttributeValue("is_cf"));
		msg.setCommpayBegindate((UFDate) bean.getAttributeValue("cf_begindate"));
		msg.setCommpayEnddate((UFDate) bean.getAttributeValue("cf_enddate"));
		msg.setPk_upbill((String) bean.getAttributeValue("pk_upbill"));
		msg.setBillOperator((String) bean.getAttributeValue(BaseFile.CREATOR));
		msg.setLastOperator((String) bean.getAttributeValue(BaseFile.MODIFIER));
		if (bean.getAttributeValue(BaseFile.MODIFIEDTIME) != null) {
			msg.setLastOperatorDate(new UFDate((bean.getAttributeValue(BaseFile.MODIFIEDTIME)).toString()));
		}
		if (bean.getAttributeValue(BaseFile.AUDIT_DATE) != null) {
			msg.setLastauditedate(new UFDate((bean.getAttributeValue(BaseFile.AUDIT_DATE).toString())));
		}

		msg.setLastauditer((String) bean.getAttributeValue(BaseFile.PK_AUDITOR));

		msg.setCostcenter((String) bean.getAttributeValue("costcenter"));

		// 加承付人

		// 2011-09-14 add by jiawei
		// 审批即签字余额控制验证字段
		// msg.setIsauthpass(UFBoolean.FALSE);
		// for (CircularlyAccessibleValueObject circularlyAccessibleValueObject
		// : aggvo.getChildrenVO()) {
		// if
		// (UFBoolean.FALSE.equals(circularlyAccessibleValueObject.getAttributeValue("isauthpass"))
		// && (String)
		// circularlyAccessibleValueObject.getAttributeValue("pk_oppaccount") !=
		// null) {
		// msg.setIsauthpass(UFBoolean.FALSE);
		// break;
		// }
		// }

		// jiawei 2011-11-17 NCdp203529419
		// 审批即签字余额控制
		// 只控制本方账户和现金账户
		msg.setIsauthpass(UFBoolean.TRUE);
		for (CircularlyAccessibleValueObject vo : aggvo.getChildrenVO()) {
			if (vo instanceof BillDetailVO) {// 付款
				if ((String) vo.getAttributeValue("pk_oppaccount") != null
						|| (String) vo.getAttributeValue("mon_account") != null) {
					msg.setIsauthpass((UFBoolean) vo.getAttributeValue("isauthpass"));
					break;
				}
			} else if (vo instanceof RecBillDetailVO) {// 收款
				if ((String) vo.getAttributeValue("pk_account") != null
						|| (String) vo.getAttributeValue("mon_account") != null) {
					msg.setIsauthpass((UFBoolean) vo.getAttributeValue("isauthpass"));
					break;
				}
			} else if (vo instanceof ChangeBillDetailVO) {// 划账
				if ((String) vo.getAttributeValue("pk_oppaccount") != null
						|| (String) vo.getAttributeValue("pk_account") != null
						|| (String) vo.getAttributeValue("mon_account") != null) {
					msg.setIsauthpass((UFBoolean) vo.getAttributeValue("isauthpass"));
					break;
				}
			}
		}

		if (msg.getPk_upbill() != null) {
			msg.setApplyCombine(CmpConst.ONE_TO_ONE);
		} else {
			DjLXVO billType = FiPubDataCache.getBillType(msg.getBilltype(), msg.getPk_group());
			if (billType == null || billType.getIsautocombinsettle() == null) {
				msg.setApplyCombine(CmpConst.ONE_TO_ONE);
			} else {
				msg.setApplyCombine(billType.getIsautocombinsettle() == null
						|| billType.getIsautocombinsettle().equals(UFBoolean.FALSE) ? CmpConst.ONE_TO_ONE
						: CmpConst.ONE_TO_MANY);
			}
			// msg.setApplyCombine((billType == null ||
			// billType.getIsautocombinsettle() == null) ? CmpConst.ONE_TO_ONE :
			// CmpConst.ONE_TO_MANY);
		}

		// msg.setIsauthpass((UFBoolean) bean.getAttributeValue("isauthpass"));

		// 结算日期
		msg.setSettledate((UFDate) bfa.getIBillFieldGet().getAtrrValue(bean, IBillFieldGet.PAYDATE));
		return msg;
	}

	// private static BusiStatus getBusiStatus(SuperVO bean) {
	// Integer bill_status = (Integer)
	// bean.getAttributeValue(BaseFile.BILL_STATUS);
	// if (bill_status != null) {
	// return CMPBillStatus.MAPPING_MAP.get(bill_status).getBusiStatus();
	// }
	// return BusiStatus.Save;
	// }

	/**
	 * 将表体信息转为Meta
	 * 
	 * @param details
	 * @return
	 */

	public static SettlementBodyVO[] convertDetailsToMeta(BaseBillVO basebill) throws BusinessException {
		AggregatedValueObject aggregatedValueObject = basebill.getBeanList()[0];
		// SuperVO bean = (SuperVO) basebill.getBeanList()[0].getParentVO();
		// bean.setItems((BillDetailVO[]) aggbean.getChildrenVO());
		SuperVO[] details = (SuperVO[]) basebill.getBeanList()[0].getChildrenVO();
		SuperVO head = (SuperVO) aggregatedValueObject.getParentVO();
		SettlementBodyVO[] metas = new SettlementBodyVO[details.length];

		String tradeType = null;
		if (details.length > 0) {
			tradeType = (String) details[0].getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
					IBillFieldGet.PK_TRADETYPE));
		}
		int index = 0;
		for (SuperVO detail : details) {
			SettlementBodyVO meta = new SettlementBodyVO();
			meta.setPk_bill(head.getPrimaryKey());
			meta.setPk_billdetail(detail.getPrimaryKey());
			meta.setBillcode((String) detail.getAttributeValue("bill_no"));
			meta.setBilldate((UFDate) detail.getAttributeValue("bill_date"));
			meta.setDirection(getDirection2(detail));
			meta.setFundsflag(getFundsflag(aggregatedValueObject));
			meta.setMemo((String) detail.getAttributeValue("memo"));
			meta.setNotenumber((String) detail.getAttributeValue(IBillFieldGet.CHECKNO));
			// meta.setPk_billtype((String)
			// detail.getAttributeValue("bill_type"));
			meta.setPk_billtypeid((String) head.getAttributeValue("pk_tradetypeid"));
			meta.setPk_billtype((String) detail.getAttributeValue("trade_type"));
			//
			if (IBillFieldGet.FJ.equals(detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
					IBillFieldGet.BILLCLASS)))) {
				// 付款结算单
				// 银行账户
				detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(IBillFieldGet.OPPACCOUNT));

				meta.setPk_account((String) detail.getAttributeValue("pk_oppaccount"));

				meta.setPk_oppaccount((String) detail.getAttributeValue("pk_account"));
				meta.setOppaccount((String) detail.getAttributeValue("accountcode"));
				meta.setOppaccname((String) detail.getAttributeValue("accountname"));
				meta.setOppbank((String) detail.getAttributeValue("accountopenbank"));
				if (detail.getAttributeValue("accounttype") != null) {
					meta.setAccounttype(Integer.valueOf((String) detail.getAttributeValue("accounttype")));
				}
				
				
				meta.setPk_busiorg((String)detail.getAttributeValue("pk_busiorg"));
				meta.setPk_busiorg_v((String)detail.getAttributeValue("pk_busiorg_v"));
				
				// 银行档案
				// meta.setPk_bank(CmpBillFieldGet.getInstance().getFieldName(IBillFieldGet.pk_b))
				//add by lixj 2022-6-29  付款结算单结算方式为委托付款，并且自定义项30为是时，结算单的是否网银支付置为是   start
				if("1001H71000000002BOXS".equals((String)detail.getAttributeValue(IBillFieldGet.PK_BALATYPE))&&"1".equals((String)detail.getAttributeValue("zyx30"))) {
					meta.setIsnetbankpay(UFBoolean.TRUE);
				//}else if("1001H71000000002BOXS".equals((String)detail.getAttributeValue(IBillFieldGet.PK_BALATYPE))&&"0".equals((String)detail.getAttributeValue("zyx30"))){
				}else if("1001H71000000002BOXS".equals((String)detail.getAttributeValue(IBillFieldGet.PK_BALATYPE))){
					meta.setIsnetbankpay(UFBoolean.FALSE);
				}
				//add by lixj 2022-6-29  付款结算单结算方式为委托付款，并且自定义项30为是时，结算单的是否网银支付置为是   end
			} else if (IBillFieldGet.SJ.equals(detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
					IBillFieldGet.BILLCLASS)))) {
				// 收款结算单

				meta.setOppaccount((String) detail.getAttributeValue("oppaccountcode"));
				meta.setOppaccname((String) detail.getAttributeValue("oppaccountname"));
				meta.setOppbank((String) detail.getAttributeValue("oppaccountopenbank"));

				meta.setPk_account((String) detail.getAttributeValue("pk_account"));
				meta.setPk_oppaccount((String) detail.getAttributeValue("pk_oppaccount"));
			} else if (IBillFieldGet.HJ.equals(detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
					IBillFieldGet.BILLCLASS)))) {
				// 划账结算单
				if (meta.getDirection().equals(Direction.PAY.VALUE)) {
					meta.setPk_account((String) detail.getAttributeValue("pk_oppaccount"));
					meta.setPk_oppaccount((String) detail.getAttributeValue("pk_account"));

				} else {
					meta.setPk_account((String) detail.getAttributeValue("pk_account"));
					meta.setPk_oppaccount((String) detail.getAttributeValue("pk_oppaccount"));

				}
			} else {
				return null;
			}

			if (meta.getPk_oppaccount() != null && meta.getOppaccount() != null
					&& meta.getPk_oppaccount().equals(meta.getOppaccount())) {
				// PK和账户值相同，说明传递的不是PK值，清空Pk_oppaccount的信息。
				// 收付结算单存在pk_里面不存PK值而存账户的情况。PK肯定有值，oppaccount可能有值，oppaccount有值的情况下，pk肯定和oppaccount一致
				meta.setPk_oppaccount(null);
			}
			meta.setPk_balatype((String) detail.getAttributeValue(IBillFieldGet.PK_BALATYPE));
			meta.setPk_currtype((String) detail.getAttributeValue(IBillFieldGet.PK_CURRTYPE));
			meta.setPk_group((String) detail.getAttributeValue(IBillFieldGet.PK_GROUP));
			meta.setPk_org((String) detail.getAttributeValue(IBillFieldGet.PK_ORG));
			meta.setPk_org_v((String) detail.getAttributeValue("pk_org_v"));
			// 利润中心
			meta.setPk_pcorg((String) detail.getAttributeValue("pk_pcorg"));
			meta.setPk_pcorg_v((String) detail.getAttributeValue("pk_pcorg_v"));
			// meta.setPk_pcorg((String)detail.getAttributeValue(IBillFieldGet.PK_PCORG));
			meta.setSystemcode(SYS_CODE);
			meta.setTranstype(getTranstype(detail));

			// 承付
			meta.setCommpaystatus((Integer) detail.getAttributeValue("cf_status"));
			meta.setIsrefused((UFBoolean) detail.getAttributeValue("is_refuse"));
			meta.setCommpaytype((Integer) detail.getAttributeValue("cf_type"));
			meta.setRefusereason((String) detail.getAttributeValue("refusenote"));
			meta.setCommpayer((String) detail.getAttributeValue("cf_man"));
			meta.setRefusereason((String) detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
					IBillFieldGet.REFUSENOTE)));

			if (meta.getCommpaytype() != null) {
				// 需求文档没有此字段，需求说用单据日期。
				meta.setCommpaytime(new UFDateTime(meta.getBilldate().toDate()));
			} else {
				meta.setCommpaytime(null);
			}
			// 托收/托付
			if (meta.getDirection().equals(Direction.PAY.VALUE)) {

				meta.setAgentreceiveprimal((UFDouble) detail.getAttributeValue(CmpBillFieldGet.getInstance()
						.getFieldName(IBillFieldGet.TS_PRIMAL)));
				meta.setAgentreceivelocal((UFDouble) detail.getAttributeValue(CmpBillFieldGet.getInstance()
						.getFieldName(IBillFieldGet.TS_LOCAL)));
				meta.setGroupagentreceivelocal((UFDouble) detail.getAttributeValue(CmpBillFieldGet.getInstance()
						.getFieldName(IBillFieldGet.GROUP_LOCAL_TS)));
				meta.setGlobalagentreceivelocal((UFDouble) detail.getAttributeValue(CmpBillFieldGet.getInstance()
						.getFieldName(IBillFieldGet.GlOBAL_LOCAL_TS)));
			} else if (meta.getDirection().equals(Direction.REC.VALUE)) {
				meta.setAgentreceiveprimal((UFDouble) detail.getAttributeValue(CmpBillFieldGet.getInstance()
						.getFieldName(IBillFieldGet.TS_PRIMAL)));
				meta.setAgentreceivelocal((UFDouble) detail.getAttributeValue(CmpBillFieldGet.getInstance()
						.getFieldName(IBillFieldGet.TS_LOCAL)));
				meta.setGroupagentreceivelocal((UFDouble) detail.getAttributeValue(CmpBillFieldGet.getInstance()
						.getFieldName(IBillFieldGet.GROUP_LOCAL_TS)));
				meta.setGlobalagentreceivelocal((UFDouble) detail.getAttributeValue(CmpBillFieldGet.getInstance()
						.getFieldName(IBillFieldGet.GlOBAL_LOCAL_TS)));
			}

			// 汇率、金额
			meta.setLocalrate((UFDouble) detail.getAttributeValue(IBillFieldGet.LOCAL_RATE));
			meta.setPay((UFDouble) detail.getAttributeValue(IBillFieldGet.PAY_PRIMAL));
			meta.setPaylocal((UFDouble) detail.getAttributeValue(IBillFieldGet.PAY_LOCAL));
			meta.setReceive((UFDouble) detail.getAttributeValue(IBillFieldGet.REC_PRIMAL));
			meta.setReceivelocal((UFDouble) detail.getAttributeValue(IBillFieldGet.REC_LOCAL));
			meta.setAgentreceiveprimal((UFDouble) detail.getAttributeValue(IBillFieldGet.TS_PRIMAL));
			meta.setAgentreceivelocal((UFDouble) detail.getAttributeValue(IBillFieldGet.TS_LOCAL));
			meta.setGrouppaylocal((UFDouble) detail.getAttributeValue(IBillFieldGet.GROUP_LOCAL_PAY));
			meta.setGlobalpaylocal((UFDouble) detail.getAttributeValue(IBillFieldGet.GlOBAL_LOCAL_PAY));
			meta.setGroupreceivelocal((UFDouble) detail.getAttributeValue(IBillFieldGet.GROUP_LOCAL_REC));
			meta.setGlobalreceivelocal((UFDouble) detail.getAttributeValue(IBillFieldGet.GlOBAL_LOCAL_REC));
			meta.setGroupagentreceivelocal((UFDouble) detail.getAttributeValue(IBillFieldGet.GROUP_LOCAL_TS));
			meta.setGlobalagentreceivelocal((UFDouble) detail.getAttributeValue(IBillFieldGet.GROUP_LOCAL_TS));

			meta.setGrouprate((UFDouble) detail.getAttributeValue(IBillFieldGet.GROUP_RATE));
			meta.setGlobalrate((UFDouble) detail.getAttributeValue(IBillFieldGet.GlOBAL_RATE));

			// @author wuzhwa 添加现金账户的处理
			meta.setPk_cashaccount((String) detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
					IBillFieldGet.CASHACCOUNT)));
			meta.setBankrelated_code((String) detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
					IBillFieldGet.BANKRELATED_CODE)));
			meta.setBusilineno((Integer) detail.getAttributeValue("billdetail_no"));

			// zhouweif 2011-02-25 增加未传递字段

			meta.setNotenumber((String) detail.getAttributeValue("note_no"));
			meta.setPk_notetype((String) detail.getAttributeValue("note_type"));
			meta.setPk_plansubj((String) detail.getAttributeValue("bankroll_projet"));
			meta.setPk_deptdoc((String) detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
					IBillFieldGet.PK_DEPTID)));
			meta.setPk_psndoc((String) detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
					IBillFieldGet.PK_PSNDOC)));

			// @wuzhwa 添加vo处理
			// 显示交易对象类型
			if (detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(IBillFieldGet.OBJTYPE)) != null) {
				meta.setTradertype((Integer) detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
						IBillFieldGet.OBJTYPE)));
			}
			if (meta.getTradertype() == null) {
				// 无
				meta.setTradertype(CmpConst.TradeObjType_Never);
			} else if (meta.getTradertype() == CmpConst.TradeObjType_CUSTOMER) {
				// 客户
				meta.setPk_trader((String) detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
						IBillFieldGet.CUSTOMER)));
			} else if (meta.getTradertype() == CmpConst.TradeObjType_SUPPLIER) {
				// 供应商
				meta.setPk_trader((String) detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
						IBillFieldGet.SUPPLIER)));
			} else if (meta.getTradertype() == CmpConst.TradeObjType_Department) {
				// 部门
				meta.setPk_trader((String) detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
						IBillFieldGet.PK_DEPTID)));
			} else if (meta.getTradertype() == CmpConst.TradeObjType_Person) {
				// 个人
				meta.setPk_trader((String) detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
						IBillFieldGet.PK_PSNDOC)));

			} else if (meta.getTradertype() == CmpConst.TradeObjType_SanHu) {

				// 散户

				// meta.setPk_trader((String)
				// detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
				// IBillFieldGet.FREECUST)));
				// 散户
				if (detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
				// 客户
						IBillFieldGet.CUSTOMER)) != null) {
					meta.setPk_trader((String) detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
							IBillFieldGet.CUSTOMER)));
				} else if (detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(IBillFieldGet.SUPPLIER)) != null) {
					// 供应商
					meta.setPk_trader((String) detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
							IBillFieldGet.SUPPLIER)));
				}
				// 散户是手输账户信息，不含PK信息
				meta.setPk_oppaccount(null);
				// @ TODO 散户未加处理
			} else if (meta.getTradertype() == CmpConst.TradeObjType_KeShang) {
				if (CmpBillFieldGet.getInstance().getFieldName(IBillFieldGet.CUSTOMER) != null) {
					meta.setPk_trader((String) detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
							IBillFieldGet.CUSTOMER)));
				} else if (CmpBillFieldGet.getInstance().getFieldName(IBillFieldGet.SUPPLIER) != null) {
					meta.setPk_trader((String) detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
							IBillFieldGet.SUPPLIER)));
				}
			}
			// 收支项目
			meta.setPk_costsubj((String) detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
					IBillFieldGet.PK_RECPROJECT)));
			// 项目
			meta.setPk_job((String) detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
					IBillFieldGet.PK_JOB)));
			// 现金流量项目
			meta.setPk_cashflow((String) detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
					IBillFieldGet.CASHITEM)));
			// 物料基本档案
			meta.setPk_invbasdoc((String) detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
					IBillFieldGet.MATERIAL)));
			// 物料分类单据没有??
			// meta.setPk_invcl(newPk_invcl);
			// 部门
			meta.setPk_deptdoc((String) detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
					IBillFieldGet.PK_DEPTID)));
			// 业务员
			meta.setPk_psndoc((String) detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
					IBillFieldGet.PK_PSNDOC)));

			// 项目阶段/项目任务信息
			meta.setPk_jobphase((String) detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
					IBillFieldGet.PK_JOBOBJPHA)));
			// 资金形态
			meta.setFundtype((String) detail.getAttributeValue("pk_fundtype"));

			meta.setPayreason((String) detail.getAttributeValue("payreason"));

			meta.setFailurereason((String) detail.getAttributeValue("failurereason"));
			// 结算日期
			meta.setTallydate((UFDate) detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
					IBillFieldGet.PAYDATE)));
			//业务组织
			meta.setPk_busiorg((String) detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
					IBillFieldGet.PK_BUSIORG)));
			//业务组织版本
			meta.setPk_busiorg_v((String) detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
					IBillFieldGet.PK_BUSIORG_V)));
			metas[index++] = meta;
		}
		// return getIncorporateSettlementBodyVO(tradeType, metas);
		return metas;
	}

	@SuppressWarnings("unchecked")
	private static SettlementBodyVO[] getIncorporateSettlementBodyVO(String tradeType,
			SettlementBodyVO[] settlementBodyVOs) {
		// 如果结算信息表体为空 或者小于1个 直接返回 不合并
		if (settlementBodyVOs == null || settlementBodyVOs.length < 2) {
			return settlementBodyVOs;
		}

		String pk_group = settlementBodyVOs[0].getPk_group();

		if (StringUtils.isNullWithTrim(tradeType) || StringUtils.isNullWithTrim(pk_group)) {
			return settlementBodyVOs;
		}

		DjLXVO djlx = FiPubDataCache.getBillType(tradeType, pk_group);

		UFBoolean isautocombinsettle = djlx.getIsautocombinsettle();
		if (!UFBoolean.TRUE.equals(isautocombinsettle)) {
			return settlementBodyVOs;
		}
		Map<String, List<SettlementBodyVO>> map = new HashMap<String, List<SettlementBodyVO>>();

		for (SettlementBodyVO settlementBodyVO : settlementBodyVOs) {
			StringBuilder keyBuilder = new StringBuilder();
			// 合并条件 交易对象类型 + 交易对象 + 付款银行账户 + 收款银行账户 + 币种 + 方向
			keyBuilder.append(settlementBodyVO.getTradertype()).append("-");

			keyBuilder.append(settlementBodyVO.getPk_trader()).append("-");

			keyBuilder.append(settlementBodyVO.getPk_account()).append("-");

			keyBuilder.append(settlementBodyVO.getPk_oppaccount()).append("-");

			keyBuilder.append(settlementBodyVO.getPk_currtype()).append("-");

			keyBuilder.append(settlementBodyVO.getDirection()).append("-");
			String key = keyBuilder.toString();
			if (map.containsKey(key)) {
				map.get(key).add(settlementBodyVO);
			} else {
				List<SettlementBodyVO> list = new ArrayList<SettlementBodyVO>();
				list.add(settlementBodyVO);
				map.put(key, list);
			}

		}

		List<SettlementBodyVO> rsnList = new ArrayList<SettlementBodyVO>();

		Iterator it = map.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			List<SettlementBodyVO> list = (List<SettlementBodyVO>) entry.getValue();
			SettlementBodyVO settlementBodyVO = null;

			for (int i = 0; i < list.size(); i++) {
				if (i == 0) {
					settlementBodyVO = list.get(0);
					rsnList.add(settlementBodyVO);
				} else {
					SettlementBodyVO body = list.get(i);

					settlementBodyVO.setPay(UFDoubleUtils.add(settlementBodyVO.getPay(), body.getPay()));
					settlementBodyVO.setPaylocal(UFDoubleUtils.add(settlementBodyVO.getPaylocal(), body.getPaylocal()));
					settlementBodyVO.setReceive(UFDoubleUtils.add(settlementBodyVO.getReceive(), body.getReceive()));
					settlementBodyVO.setReceivelocal(UFDoubleUtils.add(settlementBodyVO.getReceivelocal(),
							body.getReceivelocal()));

				}
			}

		}

		return rsnList.toArray(new SettlementBodyVO[] {});
	}

	/**
	 * 将结算信息转为收款结算单信息
	 * 
	 * @param settlevos
	 * @return
	 */
	public static RecBillAggVO[] convertF4BusiAggToMeta(SettlementAggVO[] settlevos) throws BusinessException {
		if (settlevos == null || settlevos.length < 1) {
			throw new IllegalArgumentException("settlevos are null or length of  settlevos is 0");
		}
		RecBillAggVO[] billAggVOs = new RecBillAggVO[settlevos.length];
		for (int index = 0; index < settlevos.length; index++) {
			SettlementAggVO settAggVO = settlevos[index];
			SettlementHeadVO settHeadVO = (SettlementHeadVO) settAggVO.getParentVO();
			billAggVOs[index] = new RecBillAggVO();
			RecBillVO billVO = new RecBillVO();

			billVO.setBill_type(settHeadVO.getPk_billtype());
			billVO.setTrade_type(settHeadVO.getTradertypecode());
			billVO.setBill_no(settHeadVO.getBillcode());
			billVO.setPk_recbill(settHeadVO.getPk_settlement());
			billVO.setModifier(settHeadVO.getModifier());
			billVO.setBillmaker(settHeadVO.getCreator());
			billVO.setBillmaker_date(DataUtil.getUFDate());
			billVO.setBill_date(DataUtil.getUFDate());
			billVO.setApprover(settHeadVO.getPk_auditor() == null ? null : settHeadVO.getPk_auditor());
			billVO.setPrimal_money(settHeadVO.getPrimal());
			billVO.setLocal_money(settHeadVO.getOrglocal());
			billVO.setPk_org(settHeadVO.getPk_org());
			billVO.setPk_org_v(settHeadVO.getPk_org_v());
			billVO.setPk_group(settHeadVO.getPk_group());
			billVO.setModifier(settHeadVO.getModifier());
			billVO.setBill_status(BusiStatus.Tempeorary.getBillStatusKind());
			billVO.setModifiedtime(settHeadVO.getModifiedtime());
			billVO.setCreator(settHeadVO.getCreator());
			billVO.setCreationtime(settHeadVO.getCreationtime());
			if (settHeadVO.getSettlestatus() != null
					&& settHeadVO.getSettlestatus().equals(SettleStatus.SUCCESSSETTLE.getStatus())) {
				// 已结算的结算信息
				billVO.setPaystatus(BillEnumCollection.PayStatus.SUCESS.VALUE);
				billVO.setPaydate(DataUtil.getUFDate());
				billVO.setPayman(DataUtil.getCurrentUser());
			}

			billVO.setTrade_type(IBillFieldGet.D4);
			billVO.setBillclass(IBillFieldGet.SJ);
			billVO.setBill_type(IBillFieldGet.F4);
			billVO.setStatus(nc.vo.pub.VOStatus.NEW);
			billVO.setIs_cf(settHeadVO.getIscommpay());
			billVO.setSource_flag("5");
			billVO.setAttributeValue("settleflag", settHeadVO.getSettlestatus());

			billAggVOs[index].setParent(billVO);
			// 上游单据
			billVO.setPk_upbill(settHeadVO.getPk_ftsbill());
			billVO.setUp_billtype(settHeadVO.getFts_billtype());
			billVO.setUp_tradetype(settHeadVO.getFts_billtype());

			SettlementBodyVO[] settBodyVOs = (SettlementBodyVO[]) settAggVO.getChildrenVO();
			RecBillDetailVO[] billDtlVOs = new RecBillDetailVO[settBodyVOs.length];
			for (int j = 0; j < settBodyVOs.length; j++) {
				billDtlVOs[j] = new RecBillDetailVO();
				SettlementBodyVO detail = settBodyVOs[j];
				RecBillDetailVO billDtlVO = billDtlVOs[j];
				// 上游单据
				billDtlVO.setPk_upperbill(detail.getPk_ftsbill());
				billDtlVO.setPk_upperbill_detail(detail.getPk_ftsbilldetail());
				billDtlVO.setUpper_billtype(settHeadVO.getFts_billtype());
				
				if(!Integer.valueOf(99).equals(settBodyVOs[j].getTradertype())){
					// 对象
					billDtlVO.setObjecttype(settBodyVOs[j].getTradertype());
				}
				setTradeInfo(settBodyVOs[j].getTradertype(), billDtlVO, settBodyVOs[j].getPk_trader(),
						settBodyVOs[j].getTradername());

				billDtlVO.setBill_no(detail.getBillcode());
				billDtlVO.setBill_date(DataUtil.getUFDate());
				billDtlVO.setDirection(getDirection3(detail));
				billDtlVO.setLocal_rate(detail.getLocalrate());
				billDtlVO.setMemo(detail.getMemo());
				billDtlVO.setNote_no(detail.getNotenumber());
				billDtlVO.setRec_primal(detail.getReceive());
				billDtlVO.setRec_local(detail.getReceivelocal());
				billDtlVO.setPk_account(detail.getPk_account());
				billDtlVO.setPk_oppaccount(detail.getPk_oppaccount());
				billDtlVO.setPk_recbill(settHeadVO.getPrimaryKey());
				billDtlVO.setPk_recbill_detail(detail.getPrimaryKey());
				billDtlVO.setPk_balatype(detail.getPk_balatype());
				billDtlVO.setPk_currtype(detail.getPk_currtype());
				billDtlVO.setPk_group(detail.getPk_group());
				billDtlVO.setPk_org(detail.getPk_org());
				billDtlVO.setPk_org_v(detail.getPk_org_v());
				billDtlVO.setStatus(nc.vo.pub.VOStatus.NEW);
				// billDtlVO.setCf_man(detail);
				billDtlVO.setCf_status(detail.getCommpaystatus());
				billDtlVO.setCf_type(detail.getCommpaytype());
				billDtlVO.setIs_refuse(detail.getIsrefused());
				billDtlVO.setRefusenote(detail.getRefusereason());
				billDtlVO.setBill_type(IBillFieldGet.F4);
				billDtlVO.setTeade_type(IBillFieldGet.D4);
				billDtlVO.setTrade_type(IBillFieldGet.D4);
				billDtlVO.setBillclass(IBillFieldGet.SJ);
				billDtlVO.setCreationtime(detail.getCreationtime());
				billDtlVO.setCreator(detail.getCreator());
				billDtlVO.setTs_local(detail.getAgentreceivelocal());
				billDtlVO.setTs_primal(detail.getAgentreceiveprimal());
				billDtlVO.setGlobal_local_ts(detail.getGlobalagentreceivelocal());
				billDtlVO.setGroup_local_ts(detail.getGroupagentreceivelocal());
				billVO.setSource_flag("5");
				if (detail.getBusilineno() == null) {
					detail.setBusilineno(j);
				}
				billDtlVO.setBilldetail_no(detail.getBusilineno());
				// 币种处理
				UFDouble groupCurrRate = TMCurrencyUtil.getGroupCurrRate(billDtlVO.getPk_group(),
						billDtlVO.getPk_org(), billDtlVO.getPk_currtype(), billDtlVO.getBill_date());
				UFDouble globalCurrRate = TMCurrencyUtil.getGlobalCurrRate(billDtlVO.getPk_org(),
						billDtlVO.getPk_currtype(), billDtlVO.getBill_date());
				UFDouble orgCurrRate = TMCurrencyUtil.getOrgCurrRate(billDtlVO.getPk_org(), billDtlVO.getPk_currtype(),
						billDtlVO.getBill_date());
				billDtlVO.setRec_local(TMCurrencyUtil.getOrgLocalMoney(billDtlVO.getPk_org(),
						billDtlVO.getPk_currtype(), billDtlVO.getRec_primal(), orgCurrRate, billDtlVO.getBill_date()));
				billDtlVO.setLocal_rate(orgCurrRate);
				billDtlVO.setGroup_rate(groupCurrRate);
				billDtlVO.setGlobal_rate(globalCurrRate);
				billDtlVO.setGroup_local_rec(TMCurrencyUtil.getGroupLocalMoney(billDtlVO.getPk_group(),
						billDtlVO.getPk_org(), billDtlVO.getPk_currtype(), billDtlVO.getRec_primal(), groupCurrRate,
						orgCurrRate, billDtlVO.getBill_date()));
				billDtlVO.setGlobal_local_rec(TMCurrencyUtil.getGlobalLocalMoney(billDtlVO.getPk_org(),
						billDtlVO.getPk_currtype(), billDtlVO.getRec_primal(), globalCurrRate, orgCurrRate,
						billDtlVO.getBill_date()));
				// billDtlVO.setBankroll_projet(detail.getPk_plansubj());
				// billDtlVO.setMon_account(detail.getPk_cashaccount());
				// billDtlVO.setTs_primal(detail.getAgentreceiveprimal());
				// billDtlVO.setTs_local(detail.getAgentreceivelocal());
				// billDtlVO.setGroup_local_ts(detail.getGroupcommreceivelocal());
				// billDtlVO.setGlobal_local_ts(detail.getGlobalcommreceivelocal());
			}
			billAggVOs[index].setChildrenVO(billDtlVOs);
			billAggVOs[index].setSettlementInfo(settlevos[index]);
		}
		return billAggVOs;
	}

	/**
	 * 将结算信息转为收款结算单信息
	 * 
	 * @param settlevos
	 * @return
	 * @throws BusinessException
	 */
	public static RecBillAggVO[] convertF4BusiAggToMeta(SettlementAggVO[] settlevos, String trade_type)
			throws BusinessException {
		if (settlevos == null || settlevos.length < 1) {
			throw new IllegalArgumentException("settlevos are null or length of  settlevos is 0");
		}
		RecBillAggVO[] billAggVOs = new RecBillAggVO[settlevos.length];
		for (int index = 0; index < settlevos.length; index++) {
			SettlementAggVO settAggVO = settlevos[index];
			SettlementHeadVO settHeadVO = (SettlementHeadVO) settAggVO.getParentVO();
			billAggVOs[index] = new RecBillAggVO();
			RecBillVO billVO = new RecBillVO();

			billVO.setBill_type(settHeadVO.getPk_billtype());
			billVO.setTrade_type(settHeadVO.getTradertypecode());
			billVO.setBill_no(settHeadVO.getBillcode());
			billVO.setModifier(settHeadVO.getModifier());
			billVO.setBillmaker(settHeadVO.getCreator());
			billVO.setBillmaker_date(DataUtil.getUFDate());
			billVO.setBill_date(DataUtil.getUFDate());
			billVO.setApprover(settHeadVO.getPk_auditor() == null ? null : settHeadVO.getPk_auditor());
			billVO.setPrimal_money(settHeadVO.getPrimal());
			billVO.setLocal_money(settHeadVO.getOrglocal());
			billVO.setPk_org(settHeadVO.getPk_org());
			billVO.setPk_org_v(OrgUnitsUtils.getOrgUnit_V(settHeadVO.getPk_org()));
			billVO.setPk_group(settHeadVO.getPk_group());
			billVO.setModifier(settHeadVO.getModifier());
			billVO.setBill_status(BusiStatus.Tempeorary.getBillStatusKind());
			billVO.setModifiedtime(settHeadVO.getModifiedtime());
			billVO.setCreator(settHeadVO.getCreator());
			billVO.setCreationtime(settHeadVO.getCreationtime());

			if (settHeadVO.getSettlestatus() != null
					&& settHeadVO.getSettlestatus().equals(SettleStatus.SUCCESSSETTLE.getStatus())) {
				// 已结算的结算信息
				billVO.setPaystatus(BillEnumCollection.PayStatus.SUCESS.VALUE);
				billVO.setPaydate(DataUtil.getUFDate());
				billVO.setPayman(DataUtil.getCurrentUser());
			}

			billVO.setTrade_type(trade_type);
			billVO.setBillclass(IBillFieldGet.SJ);
			billVO.setBill_type(IBillFieldGet.F4);
			billVO.setStatus(nc.vo.pub.VOStatus.NEW);
			billVO.setIs_cf(settHeadVO.getIscommpay());
			billVO.setSource_flag("5");
			billVO.setAttributeValue("settleflag", settHeadVO.getSettlestatus());
			billAggVOs[index].setParent(billVO);

			// 上游单据
			billVO.setPk_upbill(settHeadVO.getPk_ftsbill());
			billVO.setUp_billtype(settHeadVO.getFts_billtype());
			billVO.setUp_tradetype(settHeadVO.getFts_billtype());

			SettlementBodyVO[] settBodyVOs = (SettlementBodyVO[]) settAggVO.getChildrenVO();
			RecBillDetailVO[] billDtlVOs = new RecBillDetailVO[settBodyVOs.length];
			for (int j = 0; j < settBodyVOs.length; j++) {
				billDtlVOs[j] = new RecBillDetailVO();
				SettlementBodyVO detail = settBodyVOs[j];
				RecBillDetailVO billDtlVO = billDtlVOs[j];

				// 上游单据
				billDtlVO.setPk_upperbill(detail.getPk_ftsbill());
				billDtlVO.setPk_upperbill_detail(detail.getPk_ftsbilldetail());
				billDtlVO.setUpper_billtype(settHeadVO.getFts_billtype());

				if(!Integer.valueOf(99).equals(settBodyVOs[j].getTradertype())){
					// 对象
					billDtlVO.setObjecttype(settBodyVOs[j].getTradertype());
				}
				setTradeInfo(settBodyVOs[j].getTradertype(), billDtlVO, settBodyVOs[j].getPk_trader(),
						settBodyVOs[j].getTradername());
				billDtlVO.setBill_no(detail.getBillcode());
				billDtlVO.setBill_date(DataUtil.getUFDate());
				billDtlVO.setDirection(getDirection3(detail));
				billDtlVO.setLocal_rate(detail.getLocalrate());
				billDtlVO.setMemo(detail.getMemo());
				billDtlVO.setNote_no(detail.getNotenumber());
				billDtlVO.setRec_primal(detail.getReceive());
				billDtlVO.setRec_local(detail.getReceivelocal());
				billDtlVO.setPk_account(detail.getPk_account());
				billDtlVO.setPk_oppaccount(detail.getPk_oppaccount());
				billDtlVO.setPk_balatype(detail.getPk_balatype());
				billDtlVO.setPk_currtype(detail.getPk_currtype());
				billDtlVO.setPk_group(detail.getPk_group());
				billDtlVO.setPk_org(detail.getPk_org());
				billDtlVO.setPk_org_v(billVO.getPk_org_v());
				billDtlVO.setStatus(nc.vo.pub.VOStatus.NEW);

				billDtlVO.setGroup_rate(detail.getGrouprate());
				billDtlVO.setGlobal_rate(detail.getGlobalrate());

				billDtlVO.setCf_status(detail.getCommpaystatus());
				billDtlVO.setCf_type(detail.getCommpaytype());
				billDtlVO.setIs_refuse(detail.getIsrefused());
				billDtlVO.setRefusenote(detail.getRefusereason());
				billDtlVO.setBill_type(IBillFieldGet.F4);
				billDtlVO.setTeade_type(trade_type);
				billDtlVO.setTrade_type(IBillFieldGet.D4);
				billDtlVO.setBillclass(IBillFieldGet.SJ);
				billDtlVO.setCreationtime(detail.getCreationtime());
				billDtlVO.setCreator(detail.getCreator());

				// 按照 NCdp203231550 修改
				/** 　需求确定，承付的时候要有托收信息 */
				if (settHeadVO.getIscommpay() != null && settHeadVO.getIscommpay().booleanValue()) {
					billDtlVO.setTs_local(detail.getAgentreceivelocal());
					billDtlVO.setTs_primal(detail.getAgentreceiveprimal());
					billDtlVO.setGlobal_local_ts(detail.getGlobalagentreceivelocal());
					billDtlVO.setGroup_local_ts(detail.getGroupagentreceivelocal());
				}

				if (detail.getBusilineno() == null) {
					detail.setBusilineno(j);
				}
				billDtlVO.setBilldetail_no(detail.getBusilineno());

				billDtlVO.setBankrelated_code(detail.getBankrelated_code());
				billDtlVO.setBankroll_projet(detail.getPk_plansubj());

				// 币种处理
				UFDouble groupCurrRate = TMCurrencyUtil.getGroupCurrRate(billDtlVO.getPk_group(),
						billDtlVO.getPk_org(), billDtlVO.getPk_currtype(), billDtlVO.getBill_date());
				UFDouble globalCurrRate = TMCurrencyUtil.getGlobalCurrRate(billDtlVO.getPk_org(),
						billDtlVO.getPk_currtype(), billDtlVO.getBill_date());
				UFDouble orgCurrRate = TMCurrencyUtil.getOrgCurrRate(billDtlVO.getPk_org(), billDtlVO.getPk_currtype(),
						billDtlVO.getBill_date());
				billDtlVO.setRec_local(TMCurrencyUtil.getOrgLocalMoney(billDtlVO.getPk_org(),
						billDtlVO.getPk_currtype(), billDtlVO.getRec_primal(), orgCurrRate, billDtlVO.getBill_date()));
				billDtlVO.setLocal_rate(orgCurrRate);
				billDtlVO.setGroup_rate(groupCurrRate);
				billDtlVO.setGlobal_rate(globalCurrRate);
				billDtlVO.setGroup_local_rec(TMCurrencyUtil.getGroupLocalMoney(billDtlVO.getPk_group(),
						billDtlVO.getPk_org(), billDtlVO.getPk_currtype(), billDtlVO.getRec_primal(), groupCurrRate,
						orgCurrRate, billDtlVO.getBill_date()));
				billDtlVO.setGlobal_local_rec(TMCurrencyUtil.getGlobalLocalMoney(billDtlVO.getPk_org(),
						billDtlVO.getPk_currtype(), billDtlVO.getRec_primal(), globalCurrRate, orgCurrRate,
						billDtlVO.getBill_date()));

				billDtlVO.setOppaccountcode(detail.getOppaccount());
				billDtlVO.setOppaccountname(detail.getOppaccname());
				billDtlVO.setOppaccountopenbank(detail.getOppbank());

			}
			billAggVOs[index].setChildrenVO(billDtlVOs);
			billAggVOs[index].setSettlementInfo(settlevos[index]);
		}
		return billAggVOs;
	}

	/**
	 * 将结算信息转为付款结算单信息
	 * 
	 * @param settlevos
	 * @return
	 */
	public static BillAggVO[] convertF5BusiAggToMeta(SettlementAggVO[] settlevos) throws BusinessException {
		if (settlevos == null || settlevos.length < 1) {
			throw new IllegalArgumentException("settlevos are null or length of  settlevos is 0");
		}
		BillAggVO[] billAggVOs = new BillAggVO[settlevos.length];
		for (int index = 0; index < settlevos.length; index++) {
			SettlementAggVO settAggVO = settlevos[index];
			SettlementHeadVO settHeadVO = (SettlementHeadVO) settAggVO.getParentVO();
			billAggVOs[index] = new BillAggVO();
			BillVO billVO = new BillVO();

			billVO.setBill_type(settHeadVO.getPk_billtype());
			billVO.setTrade_type(settHeadVO.getTradertypecode());
			billVO.setBill_no(settHeadVO.getBillcode());
			billVO.setModifier(settHeadVO.getModifier());
			billVO.setBillmaker(settHeadVO.getCreator());
			billVO.setBillmaker_date(DataUtil.getUFDate());
			billVO.setBill_date(DataUtil.getUFDate());
			billVO.setApprover(settHeadVO.getPk_auditor() == null ? null : settHeadVO.getPk_auditor());
			billVO.setPrimal_money(settHeadVO.getPrimal());
			billVO.setLocal_money(settHeadVO.getOrglocal());
			billVO.setPk_org(settHeadVO.getPk_org());
			billVO.setPk_org_v(settHeadVO.getPk_org_v());
			billVO.setPk_group(settHeadVO.getPk_group());
			billVO.setModifier(settHeadVO.getModifier());
			billVO.setBill_status(BusiStatus.Tempeorary.getBillStatusKind());
			billVO.setModifiedtime(settHeadVO.getModifiedtime());
			billVO.setCreator(settHeadVO.getCreator());
			billVO.setCreationtime(settHeadVO.getCreationtime());
			billVO.setTrade_type(IBillFieldGet.D5);
			billVO.setAttributeValue("settleflag", settHeadVO.getSettlestatus());

			if (settHeadVO.getSettlestatus() != null
					&& settHeadVO.getSettlestatus().equals(SettleStatus.SUCCESSSETTLE.getStatus())) {
				// 已结算的结算信息
				billVO.setPaystatus(BillEnumCollection.PayStatus.SUCESS.VALUE);
				billVO.setPaydate(DataUtil.getUFDate());
				billVO.setPayman(DataUtil.getCurrentUser());
			}

			billVO.setBill_type(IBillFieldGet.F5);
			billVO.setBillclass(IBillFieldGet.FJ);
			billVO.setStatus(nc.vo.pub.VOStatus.NEW);
			billVO.setIs_cf(settHeadVO.getIscommpay());

			billVO.setCf_begindate(settHeadVO.getCommpaybegindate());
			billVO.setCf_enddate(settHeadVO.getCommpayenddate());
			billVO.setSource_flag("5");
			// “开始承付日期”（付款类结算信息使用）、“截止承付日期”（付款类结算信息使用）

			billAggVOs[index].setParent(billVO);

			// 上游单据
			billVO.setPk_upbill(settHeadVO.getPk_ftsbill());
			billVO.setUp_billtype(settHeadVO.getFts_billtype());
			billVO.setUp_tradetype(settHeadVO.getFts_billtype());

			SettlementBodyVO[] settBodyVOs = (SettlementBodyVO[]) settAggVO.getChildrenVO();
			BillDetailVO[] billDtlVOs = new BillDetailVO[settBodyVOs.length];
			for (int j = 0; j < settBodyVOs.length; j++) {
				billDtlVOs[j] = new BillDetailVO();
				SettlementBodyVO detail = settBodyVOs[j];
				BillDetailVO billDtlVO = billDtlVOs[j];

				// 上游单据
				billDtlVO.setPk_upperbill(detail.getPk_ftsbill());
				billDtlVO.setPk_upperbill_detail(detail.getPk_ftsbilldetail());
				billDtlVO.setUpper_billtype(settHeadVO.getFts_billtype());
				if(!Integer.valueOf(99).equals(settBodyVOs[j].getTradertype())){
					// 对象
					billDtlVO.setObjecttype(settBodyVOs[j].getTradertype());
				}
				setTradeInfo(settBodyVOs[j].getTradertype(), billDtlVO, settBodyVOs[j].getPk_trader(),
						settBodyVOs[j].getTradername());

				billDtlVO.setBill_no(detail.getBillcode());
				billDtlVO.setBill_date(DataUtil.getUFDate());
				billDtlVO.setDirection(getDirection3(detail));
				billDtlVO.setLocal_rate(detail.getLocalrate());
				billDtlVO.setMemo(detail.getMemo());
				billDtlVO.setNote_no(detail.getNotenumber());
				billDtlVO.setPay_primal(detail.getPay());
				billDtlVO.setPay_local(detail.getPaylocal());
				billDtlVO.setPk_account(detail.getPk_account());
				billDtlVO.setPk_oppaccount(detail.getPk_oppaccount());
				billDtlVO.setPk_balatype(detail.getPk_balatype());
				billDtlVO.setPk_currtype(detail.getPk_currtype());
				billDtlVO.setPk_group(detail.getPk_group());
				billDtlVO.setPk_org(detail.getPk_org());
				billDtlVO.setPk_org_v(billVO.getPk_org_v());
				billDtlVO.setStatus(nc.vo.pub.VOStatus.NEW);
				billDtlVO.setCf_status(detail.getCommpaystatus());
				billDtlVO.setCf_type(detail.getCommpaytype());
				billDtlVO.setIs_refuse(detail.getIsrefused());
				billDtlVO.setRefusenote(detail.getRefusereason());
				billDtlVO.setTs_local(detail.getAgentreceivelocal());
				billDtlVO.setTs_primal(detail.getAgentreceiveprimal());
				billDtlVO.setGlobal_local_ts(detail.getGlobalagentreceivelocal());
				billDtlVO.setGroup_local_ts(detail.getGroupagentreceivelocal());

				billDtlVO.setBill_type(IBillFieldGet.F5);
				billDtlVO.setBillclass(IBillFieldGet.FJ);
				billDtlVO.setTrade_type(IBillFieldGet.D5);
				billDtlVO.setTeade_type(IBillFieldGet.D5);
				billDtlVO.setCreationtime(detail.getCreationtime());
				billDtlVO.setCreator(detail.getCreator());
				billDtlVO.setBankrelated_code(detail.getBankrelated_code());
				if (detail.getBusilineno() == null) {
					detail.setBusilineno(j);
				}
				billDtlVO.setBilldetail_no(detail.getBusilineno());

				billVO.setAttributeValue("settleflag", settHeadVO.getSettlestatus());
				// 币种处理
				UFDouble groupCurrRate = TMCurrencyUtil.getGroupCurrRate(billDtlVO.getPk_group(),
						billDtlVO.getPk_org(), billDtlVO.getPk_currtype(), billDtlVO.getBill_date());
				UFDouble globalCurrRate = TMCurrencyUtil.getGlobalCurrRate(billDtlVO.getPk_org(),
						billDtlVO.getPk_currtype(), billDtlVO.getBill_date());
				UFDouble orgCurrRate = TMCurrencyUtil.getOrgCurrRate(billDtlVO.getPk_org(), billDtlVO.getPk_currtype(),
						billDtlVO.getBill_date());
				billDtlVO.setPay_local(TMCurrencyUtil.getOrgLocalMoney(billDtlVO.getPk_org(),
						billDtlVO.getPk_currtype(), billDtlVO.getPay_primal(), orgCurrRate, billDtlVO.getBill_date()));
				billDtlVO.setLocal_rate(orgCurrRate);
				billDtlVO.setGroup_rate(groupCurrRate);
				billDtlVO.setGlobal_rate(globalCurrRate);
				billDtlVO.setGroup_local_pay(TMCurrencyUtil.getGroupLocalMoney(billDtlVO.getPk_group(),
						billDtlVO.getPk_org(), billDtlVO.getPk_currtype(), billDtlVO.getPay_primal(), groupCurrRate,
						orgCurrRate, billDtlVO.getBill_date()));
				billDtlVO.setGlobal_local_pay(TMCurrencyUtil.getGlobalLocalMoney(billDtlVO.getPk_org(),
						billDtlVO.getPk_currtype(), billDtlVO.getPay_primal(), globalCurrRate, orgCurrRate,
						billDtlVO.getBill_date()));
			}
			billAggVOs[index].setChildrenVO(billDtlVOs);
			billAggVOs[index].setSettlementInfo(settlevos[index]);
		}
		return billAggVOs;
	}

	/**
	 * 付款结算 处理本币金额
	 * 
	 * @param billAggVOs
	 * @throws BusinessException
	 */
	public static void handleMoney(BillAggVO... billAggVOs) throws BusinessException {
		for (BillAggVO billAggVO : billAggVOs) {

			for (SuperVO superVO : (SuperVO[]) billAggVO.getChildrenVO()) {
				BillDetailVO billDtlVO = (BillDetailVO) superVO;

				// 币种处理
				UFDouble groupCurrRate = TMCurrencyUtil.getGroupCurrRate(billDtlVO.getPk_group(),
						billDtlVO.getPk_org(), billDtlVO.getPk_currtype(), billDtlVO.getBill_date());
				UFDouble globalCurrRate = TMCurrencyUtil.getGlobalCurrRate(billDtlVO.getPk_org(),
						billDtlVO.getPk_currtype(), billDtlVO.getBill_date());
				UFDouble orgCurrRate = TMCurrencyUtil.getOrgCurrRate(billDtlVO.getPk_org(), billDtlVO.getPk_currtype(),
						billDtlVO.getBill_date());
				billDtlVO.setPay_local(TMCurrencyUtil.getOrgLocalMoney(billDtlVO.getPk_org(),
						billDtlVO.getPk_currtype(), billDtlVO.getPay_primal(), orgCurrRate, billDtlVO.getBill_date()));
				billDtlVO.setLocal_rate(orgCurrRate);
				billDtlVO.setGroup_rate(groupCurrRate);
				billDtlVO.setGlobal_rate(globalCurrRate);
				billDtlVO.setGroup_local_pay(TMCurrencyUtil.getGroupLocalMoney(billDtlVO.getPk_group(),
						billDtlVO.getPk_org(), billDtlVO.getPk_currtype(), billDtlVO.getPay_primal(), groupCurrRate,
						orgCurrRate, billDtlVO.getBill_date()));
				billDtlVO.setGlobal_local_pay(TMCurrencyUtil.getGlobalLocalMoney(billDtlVO.getPk_org(),
						billDtlVO.getPk_currtype(), billDtlVO.getPay_primal(), globalCurrRate, orgCurrRate,
						billDtlVO.getBill_date()));
			}
		}

	}

	/**
	 * 将结算信息转为付款结算单信息
	 * 
	 * @param settlevos
	 * @return
	 * @throws BusinessException
	 */
	public static BillAggVO[] convertF5BusiAggToMeta(SettlementAggVO[] settlevos, String trade_type)
			throws BusinessException {
		if (settlevos == null || settlevos.length < 1) {
			throw new IllegalArgumentException("settlevos are null or length of  settlevos is 0");
		}
		BillAggVO[] billAggVOs = new BillAggVO[settlevos.length];
		for (int index = 0; index < settlevos.length; index++) {
			SettlementAggVO settAggVO = settlevos[index];
			SettlementHeadVO settHeadVO = (SettlementHeadVO) settAggVO.getParentVO();
			billAggVOs[index] = new BillAggVO();
			BillVO billVO = new BillVO();

			billVO.setBill_type(settHeadVO.getPk_billtype());
			billVO.setTrade_type(settHeadVO.getTradertypecode());
			billVO.setBill_no(settHeadVO.getBillcode());
			billVO.setModifier(settHeadVO.getModifier());
			billVO.setBillmaker(settHeadVO.getCreator());
			billVO.setBillmaker_date(DataUtil.getUFDate());
			billVO.setBill_date(DataUtil.getUFDate());
			billVO.setApprover(settHeadVO.getPk_auditor() == null ? null : settHeadVO.getPk_auditor());
			billVO.setPrimal_money(settHeadVO.getPrimal());
			billVO.setLocal_money(settHeadVO.getOrglocal());
			billVO.setPk_org(settHeadVO.getPk_org());
			billVO.setPk_org_v(OrgUnitsUtils.getOrgUnit_V(settHeadVO.getPk_org()));
			billVO.setPk_group(settHeadVO.getPk_group());
			billVO.setModifier(settHeadVO.getModifier());
			billVO.setBill_status(BusiStatus.Tempeorary.getBillStatusKind());
			billVO.setModifiedtime(settHeadVO.getModifiedtime());
			billVO.setCreator(settHeadVO.getCreator());
			billVO.setCreationtime(settHeadVO.getCreationtime());
			billVO.setTrade_type(trade_type);
			billVO.setAttributeValue("settleflag", settHeadVO.getSettlestatus());

			if (settHeadVO.getSettlestatus() != null
					&& settHeadVO.getSettlestatus().equals(SettleStatus.SUCCESSSETTLE.getStatus())) {
				// 已结算的结算信息
				billVO.setPaystatus(BillEnumCollection.PayStatus.SUCESS.VALUE);
				billVO.setPaydate(DataUtil.getUFDate());
				billVO.setPayman(DataUtil.getCurrentUser());
			}

			billVO.setBill_type(IBillFieldGet.F5);
			billVO.setBillclass(IBillFieldGet.FJ);
			billVO.setStatus(nc.vo.pub.VOStatus.NEW);
			billVO.setIs_cf(settHeadVO.getIscommpay());
			billVO.setCf_begindate(settHeadVO.getCommpaybegindate());
			billVO.setCf_enddate(settHeadVO.getCommpayenddate());

			billVO.setSource_flag("5");
			// “开始承付日期”（付款类结算信息使用）、“截止承付日期”（付款类结算信息使用）

			// 上游单据
			billVO.setPk_upbill(settHeadVO.getPk_ftsbill());
			billVO.setUp_billtype(settHeadVO.getFts_billtype());
			billVO.setUp_tradetype(settHeadVO.getFts_billtype());

			billAggVOs[index].setParent(billVO);

			SettlementBodyVO[] settBodyVOs = (SettlementBodyVO[]) settAggVO.getChildrenVO();
			BillDetailVO[] billDtlVOs = new BillDetailVO[settBodyVOs.length];
			for (int j = 0; j < settBodyVOs.length; j++) {
				billDtlVOs[j] = new BillDetailVO();
				SettlementBodyVO detail = settBodyVOs[j];
				BillDetailVO billDtlVO = billDtlVOs[j];

				// 上游单据
				billDtlVO.setPk_upperbill(detail.getPk_ftsbill());
				billDtlVO.setPk_upperbill_detail(detail.getPk_ftsbilldetail());
				billDtlVO.setUpper_billtype(settHeadVO.getFts_billtype());

				if(!Integer.valueOf(99).equals(settBodyVOs[j].getTradertype())){
					// 对象
					billDtlVO.setObjecttype(settBodyVOs[j].getTradertype());
				}
				if (settBodyVOs[j].getTradertype() != null) {
					setTradeInfo(settBodyVOs[j].getTradertype(), billDtlVO, settBodyVOs[j].getPk_trader(),
							settBodyVOs[j].getTradername());
				}
				billDtlVO.setBill_no(detail.getBillcode());
				billDtlVO.setBill_date(DataUtil.getUFDate());
				billDtlVO.setDirection(getDirection3(detail));
				billDtlVO.setLocal_rate(detail.getLocalrate());
				billDtlVO.setMemo(detail.getMemo());
				billDtlVO.setNote_no(detail.getNotenumber());
				billDtlVO.setPay_primal(detail.getPay());
				billDtlVO.setPay_local(detail.getPaylocal());

				billDtlVO.setGroup_rate(detail.getGrouprate());
				billDtlVO.setGlobal_rate(detail.getGlobalrate());

				billDtlVO.setPk_account(detail.getPk_oppaccount());
				billDtlVO.setPk_oppaccount(detail.getPk_account());
				billDtlVO.setPk_balatype(detail.getPk_balatype());
				billDtlVO.setPk_currtype(detail.getPk_currtype());
				billDtlVO.setPk_group(detail.getPk_group());
				billDtlVO.setPk_org(detail.getPk_org());
				billDtlVO.setPk_org_v(billVO.getPk_org_v());
				billDtlVO.setStatus(nc.vo.pub.VOStatus.NEW);
				billDtlVO.setCf_status(detail.getCommpaystatus());
				billDtlVO.setCf_type(detail.getCommpaytype());
				billDtlVO.setIs_refuse(detail.getIsrefused());
				billDtlVO.setRefusenote(detail.getRefusereason());

				// 承付的单据需要信息
				if (settHeadVO.getIscommpay() != null && settHeadVO.getIscommpay().booleanValue()) {
					billDtlVO.setTs_local(detail.getAgentreceivelocal());
					billDtlVO.setTs_primal(detail.getAgentreceiveprimal());
					billDtlVO.setGlobal_local_ts(detail.getGlobalagentreceivelocal());
					billDtlVO.setGroup_local_ts(detail.getGroupagentreceivelocal());
				}

				billDtlVO.setBankroll_projet(detail.getPk_plansubj());
				billDtlVO.setBill_type(IBillFieldGet.F5);
				billDtlVO.setBillclass(IBillFieldGet.FJ);
				billDtlVO.setTrade_type(trade_type);
				billDtlVO.setTeade_type(trade_type);
				billDtlVO.setCreationtime(detail.getCreationtime());
				billDtlVO.setCreator(detail.getCreator());
				billDtlVO.setBankrelated_code(detail.getBankrelated_code());
				if (detail.getBusilineno() == null) {
					detail.setBusilineno(j);
				}
				billDtlVO.setBilldetail_no(detail.getBusilineno());

				// 币种处理
				UFDouble groupCurrRate = TMCurrencyUtil.getGroupCurrRate(billDtlVO.getPk_group(),
						billDtlVO.getPk_org(), billDtlVO.getPk_currtype(), billDtlVO.getBill_date());
				UFDouble globalCurrRate = TMCurrencyUtil.getGlobalCurrRate(billDtlVO.getPk_org(),
						billDtlVO.getPk_currtype(), billDtlVO.getBill_date());
				UFDouble orgCurrRate = TMCurrencyUtil.getOrgCurrRate(billDtlVO.getPk_org(), billDtlVO.getPk_currtype(),
						billDtlVO.getBill_date());
				billDtlVO.setPay_local(TMCurrencyUtil.getOrgLocalMoney(billDtlVO.getPk_org(),
						billDtlVO.getPk_currtype(), billDtlVO.getPay_primal(), orgCurrRate, billDtlVO.getBill_date()));
				billDtlVO.setLocal_rate(orgCurrRate);
				billDtlVO.setGroup_rate(groupCurrRate);
				billDtlVO.setGlobal_rate(globalCurrRate);
				billDtlVO.setGroup_local_pay(TMCurrencyUtil.getGroupLocalMoney(billDtlVO.getPk_group(),
						billDtlVO.getPk_org(), billDtlVO.getPk_currtype(), billDtlVO.getPay_primal(), groupCurrRate,
						orgCurrRate, billDtlVO.getBill_date()));
				billDtlVO.setGlobal_local_pay(TMCurrencyUtil.getGlobalLocalMoney(billDtlVO.getPk_org(),
						billDtlVO.getPk_currtype(), billDtlVO.getPay_primal(), globalCurrRate, orgCurrRate,
						billDtlVO.getBill_date()));
				// 托收信息
				billDtlVO.setTs_local(TMCurrencyUtil.getOrgLocalMoney(billDtlVO.getPk_org(),
						billDtlVO.getPk_currtype(), billDtlVO.getTs_primal(), orgCurrRate, billDtlVO.getBill_date()));
				billDtlVO.setGroup_local_ts(TMCurrencyUtil.getGroupLocalMoney(billDtlVO.getPk_group(),
						billDtlVO.getPk_org(), billDtlVO.getPk_currtype(), billDtlVO.getTs_primal(), groupCurrRate,
						orgCurrRate, billDtlVO.getBill_date()));
				billDtlVO.setGlobal_local_ts(TMCurrencyUtil.getGlobalLocalMoney(billDtlVO.getPk_org(),
						billDtlVO.getPk_currtype(), billDtlVO.getTs_primal(), globalCurrRate, orgCurrRate,
						billDtlVO.getBill_date()));

				billDtlVO.setAccountCode(detail.getOppaccount());
				billDtlVO.setAccountName(detail.getOppaccname());
				billDtlVO.setAccountopenbank(detail.getOppbank());
				if (detail.getAccounttype() != null) {
					billDtlVO.setAccountType(detail.getAccounttype().toString());
				}

			}
			billAggVOs[index].setChildrenVO(billDtlVOs);
			billAggVOs[index].setSettlementInfo(settlevos[index]);
		}
		return billAggVOs;
	}

	public static AbstractBill[] convertF6BusiAggToMeta(SettlementAggVO[] settlementAggVOs, String trade_type)
			throws BusinessException {
		if (settlementAggVOs == null || settlementAggVOs.length < 1) {
			throw new IllegalArgumentException("settlevos are null or length of  settlevos is 0");
		}
		ChangeBillAggVO[] changeBillAggVOs = new ChangeBillAggVO[settlementAggVOs.length];
		for (int index = 0; index < settlementAggVOs.length; index++) {
			SettlementAggVO settAggVO = settlementAggVOs[index];

			SettlementHeadVO settHeadVO = (SettlementHeadVO) settAggVO.getParentVO();
			changeBillAggVOs[index] = new ChangeBillAggVO();
			ChangeBillVO billVO = new ChangeBillVO();

			billVO.setBill_type(settHeadVO.getPk_billtype());
			billVO.setTrade_type(settHeadVO.getTradertypecode());
			billVO.setBill_no(settHeadVO.getBillcode());
			billVO.setModifier(settHeadVO.getModifier());
			billVO.setBillmaker(settHeadVO.getCreator());
			billVO.setBillmaker_date(DataUtil.getUFDate());
			billVO.setBill_date(DataUtil.getUFDate());
			billVO.setApprover(settHeadVO.getPk_auditor() == null ? null : settHeadVO.getPk_auditor());
			billVO.setPrimal_money(settHeadVO.getPrimal());
			billVO.setLocal_money(settHeadVO.getOrglocal());
			billVO.setPk_org(settHeadVO.getPk_org());
			billVO.setPk_org_v(OrgUnitsUtils.getOrgUnit_V(settHeadVO.getPk_org()));
			billVO.setPk_group(settHeadVO.getPk_group());
			billVO.setModifier(settHeadVO.getModifier());
			billVO.setBill_status(BusiStatus.Tempeorary.getBillStatusKind());
			billVO.setModifiedtime(settHeadVO.getModifiedtime());
			billVO.setCreator(settHeadVO.getCreator());
			billVO.setCreationtime(settHeadVO.getCreationtime());
			billVO.setTrade_type(trade_type);
			billVO.setAttributeValue("settleflag", settHeadVO.getSettlestatus());
			if (settHeadVO.getSettlestatus() != null
					&& settHeadVO.getSettlestatus().equals(SettleStatus.SUCCESSSETTLE.getStatus())) {
				// 已结算的结算信息
				billVO.setPaystatus(BillEnumCollection.PayStatus.SUCESS.VALUE);
				billVO.setPaydate(DataUtil.getUFDate());
				billVO.setPayman(DataUtil.getCurrentUser());
			}

			billVO.setBill_type(IBillFieldGet.F6);
			billVO.setBillclass(IBillFieldGet.HJ);
			billVO.setStatus(nc.vo.pub.VOStatus.NEW);

			billVO.setSource_flag("5");
			billVO.setAttributeValue("settleflag", settHeadVO.getSettlestatus());
			// “开始承付日期”（付款类结算信息使用）、“截止承付日期”（付款类结算信息使用）

			changeBillAggVOs[index].setParent(billVO);
			// 上游单据
			billVO.setPk_upbill(settHeadVO.getPk_ftsbill());
			billVO.setUp_billtype(settHeadVO.getFts_billtype());
			billVO.setUp_tradetype(settHeadVO.getFts_billtype());

			SettlementBodyVO[] settBodyVOs = (SettlementBodyVO[]) settAggVO.getChildrenVO();
			ChangeBillDetailVO[] billDtlVOs = new ChangeBillDetailVO[settBodyVOs.length];
			for (int j = 0; j < settBodyVOs.length; j++) {
				billDtlVOs[j] = new ChangeBillDetailVO();
				SettlementBodyVO detail = settBodyVOs[j];
				ChangeBillDetailVO changeBillDtlVO = billDtlVOs[j];
				// 上游单据
				changeBillDtlVO.setPk_upperbill(detail.getPk_ftsbill());
				changeBillDtlVO.setPk_upperbill_detail(detail.getPk_ftsbilldetail());
				changeBillDtlVO.setUpper_billtype(settHeadVO.getFts_billtype());

				if(!Integer.valueOf(99).equals(settBodyVOs[j].getTradertype())){
					// 对象
					changeBillDtlVO.setObjecttype(settBodyVOs[j].getTradertype());
				}
				setTradeInfo(settBodyVOs[j].getTradertype(), changeBillDtlVO, settBodyVOs[j].getPk_trader(),
						settBodyVOs[j].getTradername());

				changeBillDtlVO.setBill_no(detail.getBillcode());
				changeBillDtlVO.setBill_date(DataUtil.getUFDate());
				changeBillDtlVO.setDirection(getDirection3(detail));
				changeBillDtlVO.setLocal_rate(detail.getLocalrate());

				changeBillDtlVO.setGroup_rate(detail.getGrouprate());
				changeBillDtlVO.setGlobal_rate(detail.getGlobalrate());

				changeBillDtlVO.setBankroll_projet(detail.getPk_plansubj());
				changeBillDtlVO.setPk_currtype(detail.getPk_currtype());
				changeBillDtlVO.setPk_group(detail.getPk_group());
				changeBillDtlVO.setPk_org(detail.getPk_org());
				changeBillDtlVO.setPk_org_v(billVO.getPk_org_v());
				changeBillDtlVO.setMemo(detail.getMemo());
				changeBillDtlVO.setNote_no(detail.getNotenumber());
				if (changeBillDtlVO.getDirection().equals(BillEnumCollection.Direction.JF.VALUE)) {
					changeBillDtlVO.setRec_primal(detail.getReceive());
					changeBillDtlVO.setRec_local(detail.getReceivelocal());
					changeBillDtlVO.setPay_primal(UFDouble.ZERO_DBL);
					changeBillDtlVO.setPay_local(UFDouble.ZERO_DBL);
					changeBillDtlVO.setPk_account(detail.getPk_account());
					changeBillDtlVO.setPk_oppaccount(null);

					// 币种处理
					UFDouble groupCurrRate = TMCurrencyUtil.getGroupCurrRate(changeBillDtlVO.getPk_group(),
							changeBillDtlVO.getPk_org(), changeBillDtlVO.getPk_currtype(),
							changeBillDtlVO.getBill_date());
					UFDouble globalCurrRate = TMCurrencyUtil.getGlobalCurrRate(changeBillDtlVO.getPk_org(),
							changeBillDtlVO.getPk_currtype(), changeBillDtlVO.getBill_date());
					UFDouble orgCurrRate = TMCurrencyUtil.getOrgCurrRate(changeBillDtlVO.getPk_org(),
							changeBillDtlVO.getPk_currtype(), changeBillDtlVO.getBill_date());
					changeBillDtlVO.setRec_local(TMCurrencyUtil.getOrgLocalMoney(changeBillDtlVO.getPk_org(),
							changeBillDtlVO.getPk_currtype(), changeBillDtlVO.getRec_primal(), orgCurrRate,
							changeBillDtlVO.getBill_date()));
					changeBillDtlVO.setLocal_rate(orgCurrRate);
					changeBillDtlVO.setGroup_rate(groupCurrRate);
					changeBillDtlVO.setGlobal_rate(globalCurrRate);
					changeBillDtlVO
							.setGroup_local_rec(TMCurrencyUtil.getGroupLocalMoney(changeBillDtlVO.getPk_group(),
									changeBillDtlVO.getPk_org(), changeBillDtlVO.getPk_currtype(),
									changeBillDtlVO.getRec_primal(), groupCurrRate, orgCurrRate,
									changeBillDtlVO.getBill_date()));
					changeBillDtlVO.setGlobal_local_rec(TMCurrencyUtil.getGlobalLocalMoney(changeBillDtlVO.getPk_org(),
							changeBillDtlVO.getPk_currtype(), changeBillDtlVO.getRec_primal(), globalCurrRate,
							orgCurrRate, changeBillDtlVO.getBill_date()));
				} else {
					changeBillDtlVO.setRec_primal(UFDouble.ZERO_DBL);
					changeBillDtlVO.setRec_local(UFDouble.ZERO_DBL);
					changeBillDtlVO.setPay_primal(detail.getPay());
					changeBillDtlVO.setPay_local(detail.getPaylocal());
					changeBillDtlVO.setPk_account(null);
					changeBillDtlVO.setPk_oppaccount(detail.getPk_account());

					// 币种处理
					UFDouble groupCurrRate = TMCurrencyUtil.getGroupCurrRate(changeBillDtlVO.getPk_group(),
							changeBillDtlVO.getPk_org(), changeBillDtlVO.getPk_currtype(),
							changeBillDtlVO.getBill_date());
					UFDouble globalCurrRate = TMCurrencyUtil.getGlobalCurrRate(changeBillDtlVO.getPk_org(),
							changeBillDtlVO.getPk_currtype(), changeBillDtlVO.getBill_date());
					UFDouble orgCurrRate = TMCurrencyUtil.getOrgCurrRate(changeBillDtlVO.getPk_org(),
							changeBillDtlVO.getPk_currtype(), changeBillDtlVO.getBill_date());
					changeBillDtlVO.setPay_local(TMCurrencyUtil.getOrgLocalMoney(changeBillDtlVO.getPk_org(),
							changeBillDtlVO.getPk_currtype(), changeBillDtlVO.getPay_primal(), orgCurrRate,
							changeBillDtlVO.getBill_date()));
					changeBillDtlVO.setLocal_rate(orgCurrRate);
					changeBillDtlVO.setGroup_rate(groupCurrRate);
					changeBillDtlVO.setGlobal_rate(globalCurrRate);
					changeBillDtlVO
							.setGroup_local_pay(TMCurrencyUtil.getGroupLocalMoney(changeBillDtlVO.getPk_group(),
									changeBillDtlVO.getPk_org(), changeBillDtlVO.getPk_currtype(),
									changeBillDtlVO.getPay_primal(), groupCurrRate, orgCurrRate,
									changeBillDtlVO.getBill_date()));
					changeBillDtlVO.setGlobal_local_pay(TMCurrencyUtil.getGlobalLocalMoney(changeBillDtlVO.getPk_org(),
							changeBillDtlVO.getPk_currtype(), changeBillDtlVO.getPay_primal(), globalCurrRate,
							orgCurrRate, changeBillDtlVO.getBill_date()));
				}

				// changeBillDtlVO.setPk_account(detail.getPk_oppaccount());
				// changeBillDtlVO.setPk_oppaccount(detail.getPk_account());
				// billDtlVO.setPk_paybill(settHeadVO.getPrimaryKey());
				// billDtlVO.setPk_paybill_detail(detail.getPrimaryKey());
				changeBillDtlVO.setPk_balatype(detail.getPk_balatype());

				// billDtlVO.setPk_paybill_detail(detail.getPk_detail());
				changeBillDtlVO.setStatus(nc.vo.pub.VOStatus.NEW);
				// billDtlVO.setCf_man(detail);
				// billDtlVO.setCf_status(detail.getCommpaystatus());
				// billDtlVO.setCf_type(detail.getCommpaytype());
				// billDtlVO.setIs_refuse(detail.getIsrefused());
				// billDtlVO.setRefusenote(detail.getRefusereason());
				// billDtlVO.setTf_local(detail.getAgentreceivelocal());
				// billDtlVO.setTf_primal(detail.getAgentreceiveprimal());
				// billDtlVO.setGlobal_local_ts(detail.getGlobalcommreceivelocal());
				// billDtlVO.setGroup_local_ts(detail.getGroupcommreceivelocal());

				changeBillDtlVO.setBill_type(IBillFieldGet.F6);
				changeBillDtlVO.setBillclass(IBillFieldGet.HJ);
				changeBillDtlVO.setTrade_type(trade_type);
				changeBillDtlVO.setCreationtime(detail.getCreationtime());
				changeBillDtlVO.setCreator(detail.getCreator());
				changeBillDtlVO.setBankrelated_code(detail.getBankrelated_code());
				if (detail.getBusilineno() == null) {
					detail.setBusilineno(j);
				}
				changeBillDtlVO.setBilldetail_no(detail.getBusilineno());
			}
			changeBillAggVOs[index].setChildrenVO(billDtlVOs);
			changeBillAggVOs[index].setSettlementInfo(settlementAggVOs[index]);
		}
		return changeBillAggVOs;
	}

	private static Integer getTranstype(SuperVO detail) {

		return null;
	}

	private static Integer getFundsflag(AggregatedValueObject vo) {
		int direction = getDirection(vo);
		if (direction == SettleEnumCollection.Direction.PAY.VALUE) {
			return SettleEnumCollection.FundsFlag.OUT.VALUE;
		} else if (direction == SettleEnumCollection.Direction.REC.VALUE) {
			return SettleEnumCollection.FundsFlag.IN.VALUE;
		} else {
			return SettleEnumCollection.FundsFlag.CHANGE.VALUE;
		}
	}

	/**
	 * 将节点号转为NodeType
	 * 
	 * @param nodeCode
	 * @return
	 */
	public static NodeType convertNodeCodeToNodeType(String nodeCode) {
		if (BillFunCode.RECORD_CODE.contains(nodeCode)) {
			return NodeType.RECORD;
		} else if (BillFunCode.MNG_CODE.contains(nodeCode)) {
			return NodeType.MANAGER;
		} else if (nodeCode.equals(BillFunCode.QUERY.VALUE)) {
			return NodeType.RECORD;
		} else {
			return NodeType.COMFIRM;
		}

	}

	// huzw_TODO 借贷方向待确定
	private static int getDirection3(SettlementBodyVO detail) {
		Integer i = detail.getDirection();
		if (i.equals(SettleEnumCollection.Direction.REC.VALUE)) {
			return BillEnumCollection.Direction.JF.VALUE;
		} else {// if(i.equals(SettleEnumCollection.Direction.PAY.VALUE)){
			return BillEnumCollection.Direction.DF.VALUE;
		}
	}

	// maji_TODO 借贷方向待确定
	private static int getDirection2(SuperVO detail) throws BusinessException {
		if (detail.getAttributeValue("direction") == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("3607mng_0",
					"03607mng-0383")/* @res "业务单据数据异常，传递借贷方向为Null" */);
		}
		if (IBillFieldGet.HJ.equalsIgnoreCase((String) detail.getAttributeValue(CmpBillFieldGet.getInstance()
				.getFieldName(IBillFieldGet.H_BILLCLASS)))) {
			if (Integer.valueOf(1).equals(detail.getAttributeValue("direction"))) {
				// if
				// (detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(IBillFieldGet.REC_PRIMAL))
				// != null
				// && (((UFDouble)
				// detail.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
				// IBillFieldGet.REC_PRIMAL))).equals(UFDouble.ZERO_DBL))) {
				return SettleEnumCollection.Direction.REC.VALUE;
			} else {
				return SettleEnumCollection.Direction.PAY.VALUE;
			}
		} else if (IBillFieldGet.FJ.equalsIgnoreCase((String) detail.getAttributeValue(CmpBillFieldGet.getInstance()
				.getFieldName(IBillFieldGet.H_BILLCLASS)))) {
			return SettleEnumCollection.Direction.PAY.VALUE;
			// msg.setDirection(((DJZBItemVO)(djzbvo.getChildrenVO()[0])).getFx());
		} else {
			return SettleEnumCollection.Direction.REC.VALUE;
		}
		// Integer i =
		// (Integer)detail.getAttributeValue(BaseFileBody.DIRECTION);
		// if(i.equals(BillEnumCollection.Direction.JF.VALUE)) {
		// return SettleEnumCollection.Direction.PAY.VALUE;
		// }
		// else{
		// return SettleEnumCollection.Direction.REC.VALUE;
		// }
	}

	private static int getDirection(AggregatedValueObject vo) {

		if (vo instanceof RecBillAggVO) {
			return SettleEnumCollection.Direction.REC.VALUE;
		} else if (vo instanceof BillAggVO) {
			return SettleEnumCollection.Direction.PAY.VALUE;
		} else {
			return SettleEnumCollection.Direction.CHANGE.VALUE;
		}

		// SuperVO[] details = (SuperVO[]) vo.getChildrenVO();
		// Set<Integer> direction = new HashSet<Integer>();
		// for (SuperVO detail : details) {
		// if (detail.getAttributeValue(BaseFileBody.DIRECTION) != null) {
		// direction.add((Integer)
		// detail.getAttributeValue(BaseFileBody.DIRECTION));
		// }
		// }
		// // maji_TODO 借贷方向待确定
		// if (direction.size() == 1) {
		// Integer i = direction.iterator().next();
		// if (i.equals(BillEnumCollection.Direction.JF.VALUE)) {
		// return SettleEnumCollection.Direction.PAY.VALUE;
		// } else {
		// return SettleEnumCollection.Direction.REC.VALUE;
		// }
		// } else {
		// return SettleEnumCollection.Direction.CHANGE.VALUE;
		// }
	}

	private static int getOperteStatus(BaseBillVO basebill) {
		if (basebill.getOperateType() == null) {
			return 0;
		} else if (basebill.getOperateType() == SettleEnumCollection.OperateType.COPY) {
			return 2;
		}
		return basebill.getOperateType().VALUE;
	}

	private static Map<String, List<String>> getCopyMap(AggregatedValueObject vo) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> detailIds = Lists.newArrayList();

		SuperVO[] details = (SuperVO[]) vo.getChildrenVO();
		for (SuperVO supervo : details) {
			detailIds.add(supervo.getPrimaryKey());
		}
		map.put(((SuperVO) vo.getParentVO()).getPrimaryKey(), detailIds);
		return map;
	}

	// public static void writeLog(Object bill, AuditLog log) throws
	// BusinessException {
	// ExecutorService newSingleThreadExecutor =
	// Executors.newSingleThreadExecutor();
	// WriteAuditLogTask task = new WriteAuditLogTask();
	// task.setBill(bill);
	// task.setLog(log);
	// newSingleThreadExecutor.execute(task);
	// }

	// public static AuditLog convertBillToLog(Object bill) {
	// AuditLog log = new AuditLog();
	// return log;
	// }

	// public static CommonContext getDefaultContext(BillAggVO bean) {
	// CommonContext context = new CommonContext();
	// context.setBills(new Object[]{bean});
	// context.setCompareTs(true);
	// context.setUpdate(true);
	// return context;
	// }

	public static void setBodyStatusUnChange(SuperVO[] beans) {
		for (SuperVO bean : beans) {
			bean.setStatus(VOStatus.UNCHANGED);
		}
	}

	public static void setBeanBodyStatusUnChange(AggregatedValueObject aggBean) {
		SuperVO head = (SuperVO) aggBean.getParentVO();
		SuperVO[] bodys = (SuperVO[]) aggBean.getChildrenVO();
		head.setStatus(VOStatus.UPDATED);
		if (!CheckException.checkArraysIsNull(bodys)) {
			for (SuperVO bean : bodys) {
				bean.setStatus(VOStatus.UNCHANGED);
			}
		}
	}

	public static void setVOStatusUpdate(NCObject o) {
		o.setVOStatus(VOStatus.UPDATED);
		for (NCObject body : (NCObject[]) o.getAttributeValue(BaseFile.ITEMS)) {
			body.setVOStatus(VOStatus.UPDATED);
		}
	}

	/**
	 * 聚合VO的设置修改状态 (修改人/修改时间)
	 * 
	 * @param aggVO
	 */
	public static void setVOStatusUpdate(AggregatedValueObject aggVO) {
		((SuperVO) aggVO.getParentVO()).setStatus(VOStatus.UPDATED);
		for (SuperVO child : (SuperVO[]) aggVO.getChildrenVO()) {
			child.setStatus(VOStatus.UPDATED);
		}
	}

	public static void setVOStatusUpdate(SuperVO vo) {
		vo.setStatus(VOStatus.UPDATED);
		for (SuperVO child : (SuperVO[]) vo.getAttributeValue(BaseFile.ITEMS)) {
			child.setStatus(VOStatus.UPDATED);
		}
	}

	public static void setModifySql(Appendable app) throws BusinessException {

		try {
			app.append(" modifier = '").append(DataUtil.getCurrentUser()).append("', modifiedtime = '")
					.append(DataUtil.getCurrentTime().toString()).append("'");
		} catch (IOException e) {
			throw new BusinessException(e);
		}
	}

	// public static void setIdWhere(Appendable app, SuperVO bean) throws
	// BusinessException {
	// try {
	// app.append(bean.getPKFieldName()).append(" = '").append(bean.getPrimaryKey()).append("'");
	// } catch (IOException e) {
	// throw new BusinessException(e);
	// }
	// }

	public static CommonContext getDefaultContext(BaseBillVO basebill) {
		// 需要走流程

		CommonContext context = new CommonContext();
		// context.setBills(basebill.getBeanList());
		context.setCompareTs(true);
		context.setUpdate(true);
		context.setCommand(basebill.getCommand());
		context.setBasebill(basebill);
		return context;
	}

	public static AggregatedValueObject getAggVOFromNcObject(NCObject o) {
		return (AggregatedValueObject) o.getContainmentObject();
	}

	/**
	 * modified by liaobx 2010-7-26 13:34:00 转换objects到aggvos
	 * 
	 * @param values
	 *            ojbect数组
	 * @return 转换objects到aggvos
	 */
	public static AggregatedValueObject[] convertObjetcsToAggs(Object... values) {
		AggregatedValueObject[] aggs = new AggregatedValueObject[values.length];
		// int index = 0;
		// for (Object o : values) {
		// aggs[index++] = (AggregatedValueObject) o;
		// }
		//
		System.arraycopy(values, 0, aggs, 0, aggs.length);
		return aggs;
	}

	/**
	 * 判断是否是拥有待做事务的人
	 */
	public static boolean checkIsMan(AggregatedValueObject[] aggvos) {
		if (ArrayUtil.isNull(aggvos))
			return false;
		AggregatedValueObject aggvo = aggvos[0];
		if (aggvo != null) {
			CircularlyAccessibleValueObject parentVO = aggvo.getParentVO();
			IBillFieldGet iBillFieldGet = ((IBillFieldAdapter) aggvo).getIBillFieldGet();
			try {
				String pk_bill = parentVO.getPrimaryKey();
				String pk_tradetype = (String) iBillFieldGet.getAtrrValue(parentVO, IBillFieldGet.PK_TRADETYPE);
				String userId = InvocationInfoProxy.getInstance().getUserId();
				if (Proxy.getIPFWorkflowQry().isCheckman(pk_bill, pk_tradetype, userId)) {
					return true;
				}
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		}
		return false;
	}

	/**
	 * 获取泛型类的类型
	 * 
	 * @param clz
	 * @return
	 * @throws BusinessException
	 */
	public static Class<?> getClassFormSuperGen(Class<?> clz) {
		Type genericSuperclass = clz.getGenericSuperclass();
		if (!(genericSuperclass instanceof ParameterizedType)) {
			return Object.class;
		}
		ParameterizedType p = (ParameterizedType) genericSuperclass;
		Type[] actualTypeArguments = p.getActualTypeArguments();
		if (CheckException.checkArraysIsNull(actualTypeArguments)) {
			return Object.class;
		}
		return (Class<?>) actualTypeArguments[0];
	}

	/**
	 * 得到资金单据表体pk和结算表体的对应关系 这里假定对的资金单核结算信息的一对一的情况，不涉及拆行，仅在VO交换插件类中调用
	 * 
	 * @param SettlementAggVO
	 * @return Map<String, SettlementBodyVO[]> key--SettleheadPk
	 *         ,value--SettlementBodyVO[]
	 */
	public static Map<String, AssembleBillVO> convertBusiAggVOToFtsBodyPkToBusiBodyMap(AggregatedValueObject agg)
			throws BusinessException {
		Map<String, AssembleBillVO> ftsbodypkTosettleBodyMap = CmpUtils.makeMap();
		AssembleBillVO[] bodys = (AssembleBillVO[]) agg.getChildrenVO();
		for (AssembleBillVO body : bodys) {
			ftsbodypkTosettleBodyMap.put((String) body.getAttributeValue(CmpBillFieldGet.getInstance().getFieldName(
					IBillFieldGet.TOP_ITEMID)), body);
		}
		return ftsbodypkTosettleBodyMap;
	}

	public static Map<String, BusiInfo> convertBusiInfosToPk_busibillToBusiInfoMap(BusiInfo... busiInfos) {
		Map<String, BusiInfo> Pk_busibillToBusiInfoMap = CmpUtils.makeMap();
		for (BusiInfo busiInfo : busiInfos) {
			Pk_busibillToBusiInfoMap.put(busiInfo.getPk_bill(), busiInfo);
		}
		return Pk_busibillToBusiInfoMap;
	}

	/**
	 * 设置表体交易对象信息
	 * 
	 * @param tradetype
	 * @param bodyvo
	 * @throws BusinessException
	 */
	public static void setTradeInfo(Integer tradetype, SuperVO bodyvo, String pk_trader, String tradername) {
		if (tradetype == null)
			return;
		String pk_field = null;
		String fieldName = null;
		if (tradetype == CmpConst.TradeObjType_CUSTOMER) {
			pk_field = CmpBillFieldGet.getInstance().getFieldName(IBillFieldGet.CUSTOMER);
			fieldName = CmpBillFieldGet.getInstance().getFieldName(IBillFieldGet.CUSTOMERNAME);
		} else if (tradetype == CmpConst.TradeObjType_SUPPLIER) {
			pk_field = CmpBillFieldGet.getInstance().getFieldName(IBillFieldGet.SUPPLIER);
			fieldName = CmpBillFieldGet.getInstance().getFieldName(IBillFieldGet.SUPPLIERNAME);
		} else if (tradetype == CmpConst.TradeObjType_Department) {
			pk_field = CmpBillFieldGet.getInstance().getFieldName(IBillFieldGet.PK_DEPTID);

		} else if (tradetype == CmpConst.TradeObjType_Person) {
			pk_field = CmpBillFieldGet.getInstance().getFieldName(IBillFieldGet.PK_PSNDOC);

		} else if (tradetype == CmpConst.TradeObjType_SanHu) {
			pk_field = CmpBillFieldGet.getInstance().getFieldName(IBillFieldGet.FREECUST);
		} else if (tradetype == CmpConst.TradeObjType_Never) {

		}
		if (pk_field != null)
			bodyvo.setAttributeValue(pk_field, pk_trader);
		if (fieldName != null)
			bodyvo.setAttributeValue(fieldName, tradername);
	}

}