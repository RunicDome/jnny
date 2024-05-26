package nc.impl.invp.result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import nc.bs.invp.plan.InvpLogUtil;
import nc.bs.scmpub.operator.bill.QueryOperator;
import nc.impl.invp.action.maintain.BalanceConfirmAction;
import nc.impl.invp.action.maintain.BuildBalanceTreeAction;
import nc.impl.invp.action.maintain.CancelBalanceRuleAction;
import nc.impl.invp.balance.query.RequirementQueryAdapter;
import nc.itf.invp.result.IResultMaintainApp;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.invp.balance.enumeration.RequestQryFieldCode;
import nc.vo.invp.pub.util.InvpBalanceSysParamUtil;
import nc.vo.invp.result.entity.AggBalanceResultVO;
import nc.vo.invp.result.entity.BalanceResultLightParam;
import nc.vo.invp.result.entity.BalanceResultVO;
import nc.vo.invp.result.entity.BalanceTreeParamVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.pub.MapList;
import nc.vo.pubapp.query2.sql.process.QueryCondition;
import nc.vo.scmpub.res.billtype.EWMBillType;
import nc.vo.scmpub.res.billtype.POBillType;

@SuppressWarnings({ "unchecked", "rawtypes" })
// 需求汇总平衡-确认
public class ResultMaintainAppImpl implements IResultMaintainApp {
	public ResultMaintainAppImpl() {
	}

	public void balanceConfirm(BalanceResultVO[] vos,
			Map<String, List<BalanceResultLightParam>> srcPkMap,
			UFBoolean gatherFlag) throws BusinessException {
		try {
			InvpLogUtil.clear();
			new BalanceConfirmAction()
					.balanceConfirm(vos, srcPkMap, gatherFlag);
			InvpLogUtil.clear();
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	public BalanceTreeParamVO buildBalanceTreeData(Object[] vos)
			throws BusinessException {
		try {
			return new BuildBalanceTreeAction().buildBalanceTree(vos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public MapList<Boolean, AggBalanceResultVO> cancelBalaceProcess(
			AggBalanceResultVO[] delVO, IQueryScheme queryscheme)
			throws BusinessException {
		CancelBalanceRuleAction action = new CancelBalanceRuleAction();

		MapList<Boolean, AggBalanceResultVO> returnmap = action
				.cancelBalaceProcess(delVO);

		AggBalanceResultVO[] requestVOs = queryRequestBills(queryscheme);

		if (null != requestVOs) {
			String pk_org = requestVOs[0].getParentVO().getPk_org();
			UFBoolean bistransflag = InvpBalanceSysParamUtil
					.getINVP011_V(pk_org);
			for (AggBalanceResultVO aggvo : requestVOs) {
				aggvo.getParentVO().setBistransflag(bistransflag);
			}
			returnmap.putAll(Boolean.TRUE, Arrays.asList(requestVOs));
		}

		return returnmap;
	}

	public AggBalanceResultVO[] queryRequestBills(IQueryScheme queryscheme)
			throws BusinessException {
		try {
			RequirementQueryAdapter queryAdapter = new RequirementQueryAdapter(
					queryscheme);

			List<AggBalanceResultVO> requests = new ArrayList();

			Map<String, QueryCondition> logicalConditionMap = (Map) queryscheme
					.get("all_condition");

			QueryCondition queryBillTypes = (QueryCondition) logicalConditionMap
					.get(RequestQryFieldCode.cbilltypeid.code());

			if (queryBillTypes == null) {
				return null;
			}

			Object[] values = queryBillTypes.getValues();

			for (Object value : values) {
				if ("0".equals(value)) {
					requests.addAll(queryAdapter
							.queryStockByBillType(POBillType.MRBill.getCode()));
				}

				if ("1".equals(value)) {
					requests.addAll(queryAdapter
							.queryStockByBillType(EWMBillType.WorkOrder
									.getCode()));
				}
			}

			if (requests.size() > 0) {
				return (AggBalanceResultVO[]) requests
						.toArray(new AggBalanceResultVO[0]);
			}
			return null;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggBalanceResultVO[] queryResultApp(IQueryScheme scheme)
			throws BusinessException {
		QueryOperator<AggBalanceResultVO> query = new QueryOperator(
				AggBalanceResultVO.class);

		AggBalanceResultVO[] bills = null;
		try {
			bills = (AggBalanceResultVO[]) query.query(scheme,
					"pk_balance_result");
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
		}
		return bills;
	}
}
