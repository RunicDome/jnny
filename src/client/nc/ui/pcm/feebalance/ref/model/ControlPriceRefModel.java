package nc.ui.pcm.feebalance.ref.model;

import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.IRefDocEdit;
import nc.ui.bd.ref.IRefMaintenanceHandler;
import nc.vo.pcm.feebalance.FeeBalanceHeadVO;

/**
 * 控制价委托申请参照
 */
public class ControlPriceRefModel extends AbstractRefModel {

	public ControlPriceRefModel() {
		reset();
	}

	@Override
	public void reset() {

		setFieldCode(new String[] { FeeBalanceHeadVO.BILL_CODE,
				FeeBalanceHeadVO.BALANCE_DATE, FeeBalanceHeadVO.DEF17,
				FeeBalanceHeadVO.MONEY, FeeBalanceHeadVO.BILLMAKETIME });
		setFieldName(new String[] { "单据号", "计划招标日期", "施工方案介绍及甲供材详细说明",
				"投资估算金额", "制单日期" });
		setHiddenFieldCode(new String[] { FeeBalanceHeadVO.PK_FEEBALANCE });
		setTableName("pm_feebalance");
		setPkFieldCode(FeeBalanceHeadVO.PK_FEEBALANCE);
		setRefCodeField(FeeBalanceHeadVO.BILL_CODE);
		setRefNameField(FeeBalanceHeadVO.BILL_CODE);

		// setFormulas(new String[][] { { StordocVO.STORADDR,"formataddress(" +
		// StordocVO.STORADDR + ");" } });

		// 使用启用条件
		// setAddEnableStateWherePart(true);

		// 数据权限
		// setResourceID(IBDResourceIDConst.STORDOC);

		// 维护
		setRefMaintenanceHandler(new IRefMaintenanceHandler() {

			@Override
			public String[] getFucCodes() {
				return new String[] { "4820020005457" };
			}

			@Override
			public IRefDocEdit getRefDocEdit() {
				return null;
			}
		});

		resetFieldName();
	}

	/**
	 * @return java.lang.String
	 */
	public String getRefTitle() {
		return "控制价委托申请参照";
	}

	/**
	 * 设置不显示参照
	 */
	public String[] getHiddenFieldCode() {
		return new String[] { FeeBalanceHeadVO.PK_FEEBALANCE };
	}

	/**
	 * 设置参照表名
	 */
	public String getTableName() {
		return "pm_feebalance";
	}

	/**
	 * 设置参照查询规则
	 * 
	 */
	public String getWherePart() {
		return " isnull(dr, 0) = 0 and bill_status = 1 and transi_type = '4D83-Cxx-84' and pk_feebalance not "
				+ "in (select distinct hdef55 from pm_contr where dr = 0 and hdef55 != '~' and bill_status <> -1 union all "
				+ "select distinct vdef48 from pm_feebalance_ct where dr = 0 and vdef48 != '~' and fstatusflag <> -1)";
	}

	public boolean isMatchPkWithWherePart() {
		return true;
	}

	/**
	 * 获取主键字段，必要
	 */
	public String getPkFieldCode() {
		return FeeBalanceHeadVO.PK_FEEBALANCE;
	}

}
