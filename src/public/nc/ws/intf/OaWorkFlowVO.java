package nc.ws.intf;

public class OaWorkFlowVO {

	private String primaryKey;
	private String billMaker;
	private String pkOrg;
	private String pkGroup;
	private String creator;
	private String billCode;
	private String workflowName;
	private String workflowId;
	
	public String getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}
	public String getBillMaker() {
		return billMaker;
	}
	public void setBillMaker(String billMaker) {
		this.billMaker = billMaker;
	}
	public String getPkOrg() {
		return pkOrg;
	}
	public void setPkOrg(String pkOrg) {
		this.pkOrg = pkOrg;
	}
	public String getPkGroup() {
		return pkGroup;
	}
	public void setPkGroup(String pkGroup) {
		this.pkGroup = pkGroup;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getBillCode() {
		return billCode;
	}
	public void setBillCode(String billCode) {
		this.billCode = billCode;
	}
	public String getWorkflowName() {
		return workflowName;
	}
	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}
	public String getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
	
}
