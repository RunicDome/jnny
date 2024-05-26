package nc.vo.ct.purdaily.entity;

import java.math.BigDecimal;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;

public class SmPubFilesystem

extends SuperVO

{

	private static final long serialVersionUID = 7708170088123507006L;
	private String contentdata;
	private String creator;
	private Integer dr = Integer.valueOf(0);
	private String filedesc;
	private BigDecimal filelength;
	private String filepath;
	private String filetype;
	private Object hashidx;
	private Integer isdoc;
	private String isfolder;
	private String lasttime;
	private UFDateTime modifytime;
	private String pk;
	private String pk_doc;
	private String rootpath;
	private String scantimes;
	private String smallimagedata;

	private UFDateTime ts;

	public static final String CONTENTDATA = "contentdata";
	public static final String CREATOR = "creator";
	public static final String DR = "dr";
	public static final String FILEDESC = "filedesc";
	public static final String FILELENGTH = "filelength";
	public static final String FILEPATH = "filepath";
	public static final String FILETYPE = "filetype";
	public static final String ISDOC = "isdoc";
	public static final String ISFOLDER = "isfolder";
	public static final String LASTTIME = "lasttime";
	public static final String MODIFYTIME = "modifytime";
	public static final String PK_DOC = "pk_doc";
	public static final String PK = "pk";
	public static final String ROOTPATH = "rootpath";

	public static final String SCANTIMES = "scantimes";

	public static final String SMALLIMAGEDATA = "smallimagedata";

	public static final String TS = "ts";

	public String getContentdata() {
		return contentdata;
	}

	public void setContentdata(String contentdata) {
		this.contentdata = contentdata;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public String getFiledesc() {
		return filedesc;
	}

	public void setFiledesc(String filedesc) {
		this.filedesc = filedesc;
	}

	public BigDecimal getFilelength() {
		return filelength;
	}

	public void setFilelength(BigDecimal filelength) {
		this.filelength = filelength;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getFiletype() {
		return filetype;
	}

	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}

	public Object getHashidx() {
		return hashidx;
	}

	public void setHashidx(Object hashidx) {
		this.hashidx = hashidx;
	}

	public Integer getIsdoc() {
		return isdoc;
	}

	public void setIsdoc(Integer isdoc) {
		this.isdoc = isdoc;
	}

	public String getIsfolder() {
		return isfolder;
	}

	public void setIsfolder(String isfolder) {
		this.isfolder = isfolder;
	}

	public String getLasttime() {
		return lasttime;
	}

	public void setLasttime(String lasttime) {
		this.lasttime = lasttime;
	}

	public UFDateTime getModifytime() {
		return modifytime;
	}

	public void setModifytime(UFDateTime modifytime) {
		this.modifytime = modifytime;
	}

	public String getPk() {
		return pk;
	}

	public void setPk(String pk) {
		this.pk = pk;
	}

	public String getPk_doc() {
		return pk_doc;
	}

	public void setPk_doc(String pk_doc) {
		this.pk_doc = pk_doc;
	}

	public String getRootpath() {
		return rootpath;
	}

	public void setRootpath(String rootpath) {
		this.rootpath = rootpath;
	}

	public String getScantimes() {
		return scantimes;
	}

	public void setScantimes(String scantimes) {
		this.scantimes = scantimes;
	}

	public String getSmallimagedata() {
		return smallimagedata;
	}

	public void setSmallimagedata(String smallimagedata) {
		this.smallimagedata = smallimagedata;
	}

	public UFDateTime getTs() {
		return ts;
	}

	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	public String getPKFieldName() {
		return "pk";
	}

	public String getTableName() {
		return "sm_pub_filesystem";
	}

	public static String getDefaultTableName() {
		return "sm_pub_filesystem";
	}

}