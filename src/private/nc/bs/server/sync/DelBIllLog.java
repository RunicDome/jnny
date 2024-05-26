package nc.bs.server.sync;

import nc.vo.pub.SuperVO;

public class DelBIllLog extends SuperVO {
	
	public static final String PK_DELBILLLOG = "pk_delbilllog";
	public static final String CREATIONTIME = "creationtime";
	public static final String UPDATETIME = "updatetime";
	public static final String TS = "ts";
	public static final String DR = "dr";
	public static final String CREATOR = "creator";
	public static final String BILL_TYPE = "bill_type";
	public static final String BILL_CODE = "bill_code";
	public static final String PK_BILL = "pk_bill";
	public static final String PK_ORG = "pk_org";
	public static final String PK_GROUP = "pk_group";
	public static final String DEF1 = "def1";
	public static final String DEF2 = "def2";
	public static final String DEF3 = "def3";
	public static final String DEF4 = "def4";
	public static final String DEF5 = "def5";

	private static final long serialVersionUID = -6057905148775268085L;

	public DelBIllLog() {
	}

	private String pk_delbilllog;
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
	     return "rl_delbilllog";
	   }
	public String getPKFieldName()
	   {
	     return "pk_delbilllog";
	   }
	
	public String getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}
	public String getPk_delbilllog() {
		return pk_delbilllog;
	}
	public void setPk_delbilllog(String pk_delbilllog) {
		this.pk_delbilllog = pk_delbilllog;
	}
	
}