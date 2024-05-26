package nc.ws.intf;

import nc.vo.pub.SuperVO;

public class WorkFlowBill extends SuperVO {
	
	public static final String PK_WORKFLOW_BILL = "pk_workflow_bill";
	public static final String REQUESTID = "requestid";
	public static final String CREATIONTIME = "creationtime";
	public static final String UPDATETIME = "updatetime";
	public static final String TS = "ts";
	public static final String DR = "dr";
	public static final String CREATOR = "creator";
	public static final String BILL_TYPE = "bill_type";
	public static final String BILL_STATUS = "bill_status";
	public static final String BILL_CODE = "bill_code";
	public static final String PK_BILL = "pk_bill";
	public static final String PK_ORG = "pk_org";
	public static final String PK_GROUP = "pk_group";
	public static final String DEF1 = "def1";
	public static final String DEF2 = "def2";
	public static final String DEF3 = "def3";
	public static final String DEF4 = "def4";
	public static final String DEF5 = "def5";
	public static final String WORKFLOWID = "workflowId";
	public static final String USER_NAME = "user_name";
	public static final String USER_CODE = "user_code";
	public static final String WORKFLOWNAME = "workflowName";

	private static final long serialVersionUID = -6057905148775268085L;

	public WorkFlowBill() {
	}

	private String pk_workflow_bill;
	private Integer requestid;
	private String workflowId;
	private String workflowName;
	private String ts;
	private String creationtime;
	private String updatetime;
	private Integer dr = 0;
	private String creator;
	private String bill_type;
	private String bill_status;
	private String bill_code;
	private String pk_bill;
	private String pk_org;
	private String pk_group;
	private String def1;
	private String def2;
	private String def3;
	private String def4;
	private String def5;
	private String user_name;
	private String user_code;
	private String send_data;
	private String return_data;
	
	public String getPk_workflow_bill() {
		return pk_workflow_bill;
	}
	public void setPk_workflow_bill(String pk_workflow_bill) {
		this.pk_workflow_bill = pk_workflow_bill;
	}
	public Integer getRequestid() {
		return requestid;
	}
	public void setRequestid(Integer requestid) {
		this.requestid = requestid;
	}
	public String getTs() {
		return ts;
	}
	public void setTs(String ts) {
		this.ts = ts;
	}
	public String getCreationtime() {
		return creationtime;
	}
	public void setCreationtime(String creationtime) {
		this.creationtime = creationtime;
	}
	public Integer getDr() {
		return dr;
	}
	public void setDr(Integer dr) {
		this.dr = dr;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getBill_type() {
		return bill_type;
	}
	public void setBill_type(String bill_type) {
		this.bill_type = bill_type;
	}
	public String getBill_status() {
		return bill_status;
	}
	public void setBill_status(String bill_status) {
		this.bill_status = bill_status;
	}
	public String getBill_code() {
		return bill_code;
	}
	public void setBill_code(String bill_code) {
		this.bill_code = bill_code;
	}
	public String getPk_bill() {
		return pk_bill;
	}
	public void setPk_bill(String pk_bill) {
		this.pk_bill = pk_bill;
	}
	public String getPk_org() {
		return pk_org;
	}
	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}
	public String getPk_group() {
		return pk_group;
	}
	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}
	public String getDef1() {
		return def1;
	}
	public void setDef1(String def1) {
		this.def1 = def1;
	}
	public String getDef2() {
		return def2;
	}
	public void setDef2(String def2) {
		this.def2 = def2;
	}
	public String getDef3() {
		return def3;
	}
	public void setDef3(String def3) {
		this.def3 = def3;
	}
	public String getDef4() {
		return def4;
	}
	public void setDef4(String def4) {
		this.def4 = def4;
	}
	public String getDef5() {
		return def5;
	}
	public void setDef5(String def5) {
		this.def5 = def5;
	}
	public String getTableName()
	   {
	     return "pm_workflow_bill";
	   }
	public String getPKFieldName()
	   {
	     return "pk_workflow_bill";
	   }
	public String getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
	public String getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getWorkflowName() {
		return workflowName;
	}
	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}
	public String getUser_code() {
		return user_code;
	}
	public void setUser_code(String user_code) {
		this.user_code = user_code;
	}
	public String getSend_data() {
		return send_data;
	}
	public void setSend_data(String send_data) {
		this.send_data = send_data;
	}
	public String getReturn_data() {
		return return_data;
	}
	public void setReturn_data(String return_data) {
		this.return_data = return_data;
	}
}