package nc.ws.intf;

import nc.vo.pub.SuperVO;

// XBX对接电采日志VO
public class DCLogVO extends SuperVO {

	private static final long serialVersionUID = -8933673295213016532L;
	public static final String PK_ORG = "pk_org";
	public static final String ZZMC = "zzmc";
	public static final String USERID = "userid";
	public static final String USERNAME = "username";
	public static final String RETURN_DATA = "return_data";
	public static final String URL = "url";
	public static final String SUCCESS = "success";
	public static final String PK_BILL = "pk_bill";
	public static final String BILLTYPE = "billtype";
	public static final String TS = "ts";

	private String pk_org;
	private String zzmc;
	private String userid;
	private String username;
	private String return_data;
	private String url;
	private String success;
	private String billtype;
	private String pk_bill;
	private String ts;
	
	public String getPk_org() {
		return pk_org;
	}
	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}
	public String getZzmc() {
		return zzmc;
	}
	public void setZzmc(String zzmc) {
		this.zzmc = zzmc;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getReturn_data() {
		return return_data;
	}
	public void setReturn_data(String return_data) {
		this.return_data = return_data;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getBilltype() {
		return billtype;
	}
	public void setBilltype(String billtype) {
		this.billtype = billtype;
	}
	public String getPk_bill() {
		return pk_bill;
	}
	public void setPk_bill(String pk_bill) {
		this.pk_bill = pk_bill;
	}
	public String getTs() {
		return ts;
	}
	public void setTs(String ts) {
		this.ts = ts;
	}
	public String getTableName() {
		return "RL_PUSHDCLOG";
	}
}
