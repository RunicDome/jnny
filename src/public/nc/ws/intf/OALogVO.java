package nc.ws.intf;

import nc.vo.pub.SuperVO;

// XBX获取OA流程记录日志表
public class OALogVO extends SuperVO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6648444018917458601L;
	public static final String CREATIONTIME = "creationtime";
	public static final String BILL_CODE = "bill_code";
	public static final String PK_BILL = "pk_bill";
	public static final String REQUESTID = "requestid";
	public static final String TRANSI_TYPE = "transi_type";
	public static final String SEND_DATA = "send_data";
	public static final String USERID = "userid";
	public static final String USERIP = "userip";
	public static final String USERNAME = "username";
	public static final String DEF1 = "def1";
	public static final String DEF2 = "def2";
	public static final String DEF3 = "def3";
	public static final String DEF4 = "def4";
	public static final String DEF5 = "def5";
	public static final String DEF6 = "def6";
	public static final String DEF7 = "def7";
	public static final String DEF8 = "def8";
	public static final String DEF9 = "def9";
	public static final String DEF10 = "def10";
	public static final String DR = "dr";

	private String creationtime;
	private String bill_code;
	private String pk_bill;
	private String transi_type;
	private int dr = 0;

	public String getCreationtime() {
		return creationtime;
	}

	public void setCreationtime(String creationtime) {
		this.creationtime = creationtime;
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

	public String getTransi_type() {
		return transi_type;
	}

	public void setTransi_type(String transi_type) {
		this.transi_type = transi_type;
	}

	public String getSend_data() {
		return send_data;
	}

	public void setSend_data(String send_data) {
		this.send_data = send_data;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
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

	private String send_data;
	private String requestid;
	private String userid;
	private String userip;
	private String username;
	private String def1;
	private String def2;
	private String def3;
	private String def4;
	private String def5;
	public String getDef6() {
		return def6;
	}

	public void setDef6(String def6) {
		this.def6 = def6;
	}

	public String getDef7() {
		return def7;
	}

	public void setDef7(String def7) {
		this.def7 = def7;
	}

	public String getDef8() {
		return def8;
	}

	public void setDef8(String def8) {
		this.def8 = def8;
	}

	public String getDef9() {
		return def9;
	}

	public void setDef9(String def9) {
		this.def9 = def9;
	}

	public String getDef10() {
		return def10;
	}

	public void setDef10(String def10) {
		this.def10 = def10;
	}

	private String def6;
	private String def7;
	private String def8;
	private String def9;
	private String def10;

	public String getTableName() {
		return "rl_oalog";
	}

	public String getUserip() {
		return userip;
	}

	public void setUserip(String userip) {
		this.userip = userip;
	}

	public String getRequestid() {
		return requestid;
	}

	public void setRequestid(String requestid) {
		this.requestid = requestid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getDr() {
		return dr;
	}

	public void setDr(int dr) {
		this.dr = dr;
	}

}
