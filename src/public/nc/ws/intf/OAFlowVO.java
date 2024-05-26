package nc.ws.intf;

import nc.vo.pub.SuperVO;

// XBX获取OA流程ID和表名专用VO
public class OAFlowVO extends SuperVO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7054846911283731369L;
	public static final String PK_ORG = "pk_org";
	public static final String BILLTYPE = "billtype";
	public static final String FLOWID = "flowid";
	public static final String TABLENAME = "tablename";
	public static final String ISDR = "isdr";
	public static final String BILLTYPENAME = "billtypename";

	private String pk_org;
	private String billtype;
	private String flowid;
	private String tablename;
	private Integer isdr = 0;
	private String billtypename;
	
	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public String getBilltype() {
		return billtype;
	}

	public void setBilltype(String billtype) {
		this.billtype = billtype;
	}

	public String getFlowid() {
		return flowid;
	}

	public void setFlowid(String flowid) {
		this.flowid = flowid;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getTableName() {
		return "rl_ghoawork";
	}

	public Integer getIsdr() {
		return isdr;
	}

	public void setIsdr(Integer isdr) {
		this.isdr = isdr;
	}

	public String getBilltypename() {
		return billtypename;
	}

	public void setBilltypename(String billtypename) {
		this.billtypename = billtypename;
	}
}
