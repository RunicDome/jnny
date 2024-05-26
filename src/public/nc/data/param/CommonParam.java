package nc.data.param;

// 公共参数
public abstract interface CommonParam {
	// 单据状态（0 自由态（驳回） 1 提交成功 9 删除）
	public static String RECALL = "0";
	public static String COMMIT = "1";
	public static String DELETE = "9";
	// 单据类型
	public static String BILLTYPE_SQ = "1";// 申请单
	public static String BILLTYPE_JK = "2";// 借款单
	public static String BILLTYPE_BX = "3";// 报销单
	public static String BILLTYPE_YS = "4";// 应收单
	public static String BILLTYPE_YF = "5";// 应付单
	public static String BILLTYPE_SK = "6";// 收款单
	public static String BILLTYPE_FK = "7";// 付款单
	public static String BILLTYPE_FKSQ = "8";// 付款申请单
	public static String BILLTYPE_FKJS = "9";// 付款结算单
}