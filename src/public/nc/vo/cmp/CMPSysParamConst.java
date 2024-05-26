package nc.vo.cmp;

/**
 * 现金管理系统参数常量类
 * 
 * @author xwq
 * @version 1.0 2011-03-18
 * @since NC6.0
 * 
 */
public class CMPSysParamConst {
 
	/**
	 * 收款结算单传会计凭证环节
	 */
	public static final String CMP36 = "CMP36";

	/**
	 * 付款结算单传会计凭证环节
	 */
	public static final String CMP37 = "CMP37";

	public static final String CONST_SINGTODAP = "签字成功";/*-=notranslate=-*/
	public static final String CONST_SETTLETODAP = "结算成功";/*-=notranslate=-*/
	/**
	 * 协同产生的单据是否允许删除
	 */
	public static final String CMP47 = "CMP47";
	/**
	 * 制单及支付时生成结算号
	 */
	public static final String CMP39 = "CMP39";
	// public static final String CMP39_VALUE_SHI = "Y";
	// public static final String CMP39_VALUE_FOU = "N";

	/**
	 * 单据凭证号显示内容调整
	 */
	public static final String CMP40 = "CMP40";

	/**
	 * 截止到本月单据全部生效
	 */
	public static final String CMP42 = "CMP42";
	/**
	 * 截止到本月单据全部结算
	 */
	public static final String CMP43 = "CMP43";
	/**
	 * 截止到本月单据全部生成会计凭证
	 */
	public static final String CMP44 = "CMP44";
	/**
	 * 截止到本月账户汇兑损益是否计算
	 */
	public static final String CMP45 = "CMP45";
	/**
	 * 单据协同触发动作：保存、签字确认
	 */
	public static final String CMP_TRIGGER_ACTION = "CMP15";
	/**
	 * 协同单据是否可以再协同
	 */
	public static final String CMP_CONFER_AGAIN = "CMP19";
	/**
	 * 协同单据是否控制总金额
	 */
	public static final String CMP_CONTROL_SUM = "CMP18";

	public static final String NC004 = "NC004";

	/**
	 * 收款结算单是否允许对方为散户
	 */
	public static final String CMP48 = "CMP48";
	// public static final String CMP48_VALUE_SHI = "Y";
	// public static final String CMP48_VALUE_FOU = "N";

	/**
	 * 付款结算单是否允许对方为散户
	 */
	public static final String CMP49 = "CMP49";

	// public static final String CMP49_VALUE_SHI = "Y";
	// public static final String CMP49_VALUE_FOU = "N";

	/**
	 * 是否为未达账项管理
	 */
	public static final String CMP50 = "CMP50";

	// public static final String CMP50_VALUE_SHI = "Y";
	// public static final String CMP50_VALUE_FOU = "N";

	/**
	 * 到账通知是否自动生成单据
	 */
	public static final String CMP51 = "CMP51";

	/**
	 * 是否使用欧盟的结算处理方式
	 */
	public static final String CMP52 = "CMP52";

	// public static final String CMP52_VALUE_SHI = "Y";
	// public static final String CMP52_VALUE_FOU = "N";
	/**
	 * 划账结算单是否需要数字签名
	 */
	public static final String CMP53 = "CMP53";

	/**
	 * 付款申请生成付款类单据回写业务环节，组织级参数
	 */
	public static final String CMP62 = "CMP62";
	/**
	 * 合并支付方式是否多张合并支付 参数值是:多张合并支付 否:单张合并支付，默认为单张合并支付
	 */
	public static final String CMP94 = "CMP94";
	// public static final String CMP53_VALUE_Y = "Y";
	// public static final String CMP53_VALUE_N = "N";
}