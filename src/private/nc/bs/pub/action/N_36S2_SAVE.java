package nc.bs.pub.action;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import nc.bs.cmp.cash.bpplugin.CashBasePluginPoint;

import nc.bs.framework.common.InvocationInfoProxy;

import nc.bs.framework.common.NCLocator;

import nc.bs.pub.script.CmpScriptChecker;

import nc.bs.pub.tools.CmpPrivateUtil;

import nc.bs.pub.tools.CmpPrivateVOAdapter;

import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.trade.business.HYPubBO;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;

import nc.impl.pubapp.pattern.rule.IRule;

import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.pbm.materialplan.pvt.IMaterialPlanCommonService;

import nc.pubitf.cmp.bankaccbook.IAccQueryService;

import nc.uif.pub.exception.UifException;
import nc.vo.bd.bankaccount.BankAccSubVO;
import nc.vo.bd.material.MaterialVO;
import nc.vo.cmp.cash.AggCashDrawVO;

import nc.vo.cmp.cash.CashDrawVO;

import nc.vo.cmp.exception.ExceptionHandler;

import nc.vo.cmp.pub.constant.CmpBusConstant;

import nc.vo.ml.AbstractNCLangRes;

import nc.vo.ml.NCLangRes4VoTransl;

import nc.vo.org.OrgVO;
import nc.vo.pbm.materialplan.MaterialPlanBillVO;
import nc.vo.pbm.materialplan.MaterialPlanBodyVO;
import nc.vo.pim.project.ProjectHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;

import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

import nc.vo.pubapp.AppContext;

import nc.vo.sm.UserVO;
import nc.vo.tmpub.initdate.CommInitDateCheck;

import nc.vo.tmpub.util.TMBillOrgVUtils;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class N_36S2_SAVE extends AbstractPfAction<AggCashDrawVO> {
	private static String WorkFId;// OA WorkflowId 流程ID
	private static String TableName;// OA TableName 表名
	// TODO 单据同步OA功能
	private HYPubBO hyPubBO;

	public HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	public N_36S2_SAVE() {
	}

	private void senOaData(AggCashDrawVO temp) throws BusinessException {
		// TODO Auto-generated method stub
		CashDrawVO hVO = temp.getParentVO();
		String billType = "36S2";
		if ("4".equals(getDef2(hVO.getPk_org()))
				&& hVO.getBilltypecode().contains(billType)) {
			OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hVO.getPk_org(),
					billType);
			if (oaVo.getIsdr() == 0) {
				WorkFId = oaVo.getFlowid();
				TableName = oaVo.getTablename();
				// 获取主表数据
				JSONArray headData = getMainMap(temp.getParentVO());
				// 获取子表数据
				JSONArray bodyData = null;
				// 构造workflow信息
				OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
				workFlowVO.setPrimaryKey(temp.getPrimaryKey());
				workFlowVO.setPkGroup(hVO.getPk_group());
				workFlowVO.setPkOrg(hVO.getPk_org());
				workFlowVO.setBillMaker(hVO.getBillmaker());
				workFlowVO.setCreator(hVO.getCreator());
				workFlowVO.setBillCode(billType);
				// 临时写死，后期改为接口获取
				workFlowVO.setWorkflowId(OaWorkFlowUtil.getOAFlowID(WorkFId));
				workFlowVO.setWorkflowName("现金支取");
				WorkFlowBill bill = OaWorkFlowUtil.getWorkFlowBill(workFlowVO);
				// WorkFlowBill的自定义项3 判断是否港华单据
				bill.setDef3("ZT");
				// 制单人身份证号
				UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
						UserVO.class, temp.getParentVO().getBillmaker());
				String idCard = (String) getHyPubBO().findColValue(
						"bd_psndoc",
						"id",
						"nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc()
								+ "'");
				bill.setDef5(idCard);
				// 调用OA工具类同步数据至OA
				OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
			}
		}
	}

	private JSONArray getMainMap(CashDrawVO parentVO) throws BusinessException {
		// TODO Auto-generated method stub
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 构造数据
		List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
		// ---------其他字段begin
		// 库存组织
		String stockName = (String) getHyPubBO()
				.findColValue(
						"org_itemorg",
						"name",
						"nvl(dr,0) = 0 and pk_itemorg = '"
								+ parentVO.getPk_org() + "'");
		list.add(OaWorkFlowUtil.listAddObj("zzmc", stockName));
		UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
				parentVO.getBillmaker());
		//币种
		String bz = (String) getHyPubBO()
				.findColValue(
						"bd_currtype",
						"name",
						"nvl(dr,0) = 0 and pk_currtype = '"
								+ parentVO.getPk_currency() + "'");
		list.add(OaWorkFlowUtil.listAddObj("bz", bz));
		//结算方式
		String jsfs = (String) getHyPubBO()
				.findColValue(
						"bd_balatype",
						"name",
						"nvl(dr,0) = 0 and pk_balatype = '"
								+ parentVO.getPk_balatype() + "'");
		list.add(OaWorkFlowUtil.listAddObj("jsfs", jsfs));
		//现金账户
		String xjzh = (String) getHyPubBO()
				.findColValue(
						"bd_cashaccount",
						"code",
						"nvl(dr,0) = 0 and pk_cashaccount = '"
								+ parentVO.getPk_cashaccount() + "'");
		list.add(OaWorkFlowUtil.listAddObj("xjzh", xjzh));
		// 银行账户
		if(parentVO.getPk_bankaccount() != null){
			BankAccSubVO yhvo = (BankAccSubVO) getHyPubBO()
					.queryByPrimaryKey(BankAccSubVO.class,
							parentVO.getPk_bankaccount());
			list.add(OaWorkFlowUtil.listAddObj("yhzh", yhvo.getAccnum()));
		}
		
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
		// ---------其他字段end
		JSONArray arr = JSONArray.fromObject(list, jsonConfig);
		return arr;
	}

	protected CompareAroundProcesser<AggCashDrawVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggCashDrawVO> processor = new CompareAroundProcesser(
				CashBasePluginPoint.SEND_APPROVE);
		processor.addBeforeRule(new IRule() {
			public void process(AggCashDrawVO[] vos) {
				try {
					for (AggCashDrawVO vo : vos) {
						CashDrawVO parentvo = vo.getParentVO();
						if (parentvo
								.getBillstatus()
								.compareTo(
										Integer.valueOf(nc.vo.cmp.cash.BillStatusEnum.SAVE
												.toIntValue())) != 0)
							throw new BusinessException(NCLangRes4VoTransl
									.getNCLangRes().getStrByID("3607cash_0",
											"03607cash-0053"));
						if (parentvo.getVbillstatus().compareTo(
								Integer.valueOf(-1)) != 0) {
							throw new BusinessException(NCLangRes4VoTransl
									.getNCLangRes().getStrByID("3607cash_0",
											"03607cash-0054"));
						}
					}
				} catch (BusinessException e) {
					ExceptionHandler.handleRuntimeException(e);
				}
			}

			@Override
			public void process(Object[] paramArrayOfE) {
				// TODO Auto-generated method stub

			}
		});
		return processor;
	}

	protected AggCashDrawVO[] processBP(Object userObj,
			AggCashDrawVO[] clientFullVOs, AggCashDrawVO[] originBills) {
		for (AggCashDrawVO aggCashDrawVO : clientFullVOs) {
			CashDrawVO cashDrawVo = aggCashDrawVO.getParentVO();

			try {
				String result = CommInitDateCheck.checkInitDate(AppContext
						.getInstance().getBusiDate(), cashDrawVo.getPk_org(),
						CmpBusConstant.PRODUCT_CMP_FUNCODE);

				if (result != null) {
					ExceptionHandler
							.handleRuntimeException(new BusinessException(
									result));
				}

				IAccQueryService accQueryService = (IAccQueryService) NCLocator
						.getInstance().lookup(IAccQueryService.class);

				Integer billstatus = (Integer) aggCashDrawVO.getParentVO()
						.getAttributeValue("billstatus");

				if ((billstatus != null) && (billstatus.intValue() != 0)) {
					accQueryService.checkSettleAccount((String) aggCashDrawVO
							.getParentVO().getAttributeValue("pk_org"),
							AppContext.getInstance().getBusiDate());
				}
			} catch (BusinessException e) {
				ExceptionHandler.handleRuntimeException(e);
			}

			CmpPrivateUtil
					.checkBankAccountFrozen(new AggCashDrawVO[] { aggCashDrawVO });

			if ((null != cashDrawVo.getPk_notetype())
					&& (null == cashDrawVo.getNoteno())) {
				ExceptionHandler.handleRuntimeException(new BusinessException(
						NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"3607cash_0", "03607cash-0106")));
			}

			cashDrawVo.setAttributeValue("billstatus",
					nc.vo.cmp.cash.BillStatusEnum.PREAPPROVE.value());

			cashDrawVo.setVbillstatus(Integer
					.valueOf(nc.vo.pub.pf.BillStatusEnum.COMMIT.toIntValue()));

			cashDrawVo.setAttributeValue("commiter", InvocationInfoProxy
					.getInstance().getUserId());

			cashDrawVo.setAttributeValue("commitdate", AppContext.getInstance()
					.getBusiDate());
			if (cashDrawVo.getAttributeValue("modifier") == null) {
				cashDrawVo.setAttributeValue("modifiedtime", null);
			}
			try {
				CmpScriptChecker.getInstance().check4Save(aggCashDrawVO);
				cashDrawVo.setStatus(1);

				UFDate buzDate = CmpPrivateUtil.getBuzDate(cashDrawVo);
				TMBillOrgVUtils.setOrgV(aggCashDrawVO, buzDate);

				CmpPrivateVOAdapter.prepareVOForCashDepositInfo(aggCashDrawVO);
			} catch (BusinessException e) {
				ExceptionHandler.handleRuntimeException(e);
			}
		}

		AggCashDrawVO[] returnVos = (AggCashDrawVO[]) new BillUpdate().update(
				clientFullVOs, originBills);
		// 推OA
		for (AggCashDrawVO billVO : returnVos) {
			CashDrawVO headvo = billVO.getParentVO();
			if (headvo.getBillstatus() == 2) {
				try {
					senOaData(billVO);
				} catch (BusinessException e) {
					// TODO Auto-generated catch block
					ExceptionHandler.handleRuntimeException(e);
				}
			}
		}
		return returnVos;
	}

	public String getDef2(String pk_org) throws BusinessException {
		String def2 = "";
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		if (null != orgVO) {
			def2 = orgVO.getDef2();
		}
		return def2;
	}
}
