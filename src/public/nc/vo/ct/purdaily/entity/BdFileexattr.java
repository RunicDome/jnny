package nc.vo.ct.purdaily.entity;

import nc.vo.pub.SuperVO;

public class BdFileexattr extends SuperVO{

	private static final long serialVersionUID = 1L;

	private String location;
	private String pk_bill;
	private String pk_billtypecode;
	private String pk_exattr;
	private String pk_file;
	private String pk_fileexattr;
	private String pk_filetype;
	private String ts;
	private String dr;
	

	public String getPKFieldName() {
		return "pk_fileexattr";
	}

	public String getTableName() {
		return "bd_fileexattr";
	}

	public static String getDefaultTableName() {
		return "bd_fileexattr";
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPk_bill() {
		return pk_bill;
	}

	public void setPk_bill(String pk_bill) {
		this.pk_bill = pk_bill;
	}

	public String getPk_billtypecode() {
		return pk_billtypecode;
	}

	public void setPk_billtypecode(String pk_billtypecode) {
		this.pk_billtypecode = pk_billtypecode;
	}

	public String getPk_exattr() {
		return pk_exattr;
	}

	public void setPk_exattr(String pk_exattr) {
		this.pk_exattr = pk_exattr;
	}

	public String getPk_file() {
		return pk_file;
	}

	public void setPk_file(String pk_file) {
		this.pk_file = pk_file;
	}

	public String getPk_fileexattr() {
		return pk_fileexattr;
	}

	public void setPk_fileexattr(String pk_fileexattr) {
		this.pk_fileexattr = pk_fileexattr;
	}

	public String getPk_filetype() {
		return pk_filetype;
	}

	public void setPk_filetype(String pk_filetype) {
		this.pk_filetype = pk_filetype;
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	public String getDr() {
		return dr;
	}

	public void setDr(String dr) {
		this.dr = dr;
	}

	
}
