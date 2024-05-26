package nc.bs.arap.util;

import nc.vo.pub.BusinessException;

public class ArapFlowUtil {
	public static final String FLOW_TYPE_WORKFLOW = "2";
	public static final String FLOW_TYPE_APPROVEFLOW = "1";

	public ArapFlowUtil() {
	}

	public static String getParamCode(String billtype) {
		return "ARAP" + billtype;
	}

	public static String getTransTypeFlowType(String pk_org, String billtypecode) {
		try {
			String billtype = getBillType(billtypecode);
			nc.vo.pub.para.SysInitVO sysInitVO = ((nc.pubitf.para.ISysInitDAO) nc.bs.framework.common.NCLocator
					.getInstance().lookup(nc.pubitf.para.ISysInitDAO.class))
					.getSysInitVO(getParamCode(billtype), pk_org);
			if (sysInitVO != null) {
				return sysInitVO.getValue();
			}
		} catch (BusinessException e) {
			nc.vo.fipub.exception.ExceptionHandler.consume(e);
		}
		return null;
	}

	public static String getBillType(String billtypecode)
			throws BusinessException {
		if ((billtypecode.equals("F0")) || (billtypecode.equals("D0"))
				|| (billtypecode.startsWith("F0-")))
			return "F0";
		if ((billtypecode.equals("F1")) || (billtypecode.equals("D1"))
				|| (billtypecode.startsWith("F1-")))
			return "F1";
		if ((billtypecode.equals("F2")) || (billtypecode.equals("D2"))
				|| (billtypecode.startsWith("F2-")))
			return "F2";
		if ((billtypecode.equals("F3")) || (billtypecode.equals("D3"))
				|| (billtypecode.startsWith("F3-"))) {
			return "F3";
		}
		throw new BusinessException("不支持的交易类型,请检查");
	}

	public static String getApproveActionCode(String pk_org, String billtypecode) {
		String actionCode = "APPROVE";

		String paraString = getTransTypeFlowType(pk_org, billtypecode);
		if ((paraString != null) && ("2".equals(paraString))) {
			actionCode = "SIGNAL";
		}

		return actionCode;
	}

	public static String getUnApproveActionCode(String pk_org,
			String billtypecode) {
		String actionCode = "UNAPPROVE";

		String paraString = getTransTypeFlowType(pk_org, billtypecode);
		if ("2".equals(paraString)) {
			actionCode = "ROLLBACK";
		}

		return actionCode;
	}

	public static String getCommitActionCodeBySSC(String pk_org,
			String billtypecode) throws BusinessException {
		String actionCode = "COMMIT";
		if (isUseWorkFlow(pk_org, billtypecode))
			actionCode = "START";
		return actionCode;
	}

	public static String getCommitActionCode(String pk_org, String billtypecode) {
		String actionCode = "SAVE";
		String paraString = getTransTypeFlowType(pk_org, billtypecode);
		if ("2".equals(paraString)) {
			actionCode = "START";
		}
		return actionCode;
	}

	public static String getUnCommitActionCode(String pk_org,
			String billtypecode) {
		String actionCode = "UNSAVE";

		String paraString = getTransTypeFlowType(pk_org, billtypecode);
		if ("2".equals(paraString)) {
			actionCode = "RECALL";
		}

		return actionCode;
	}

	public static boolean isUseWorkFlow(String pk_org, String billtypecode)
			throws BusinessException {
		String paraString = getTransTypeFlowType(pk_org, billtypecode);
		if ((paraString == null) || ("1".equals(paraString))) {
			return false;
		}
		return true;
	}

	public static boolean isUseApproveFlow(String pk_org, String billtypecode)
			throws BusinessException {
		String paraString = getTransTypeFlowType(pk_org, billtypecode);
		if ((paraString == null) || ("1".equals(paraString))) {
			return true;
		}
		return false;
	}

	public static int getWorkFlowType(String pk_org, String billtypecode) {
		int flowtype = nc.vo.wfengine.definition.WorkflowTypeEnum.Approveflow
				.getIntValue();

		String paraString = getTransTypeFlowType(pk_org, billtypecode);
		if ("2".equals(paraString)) {
			flowtype = nc.vo.wfengine.definition.WorkflowTypeEnum.Workflow
					.getIntValue();
		}

		return flowtype;
	}
}
