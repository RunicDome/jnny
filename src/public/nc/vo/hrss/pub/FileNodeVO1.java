package nc.vo.hrss.pub;

import java.math.BigDecimal;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;

public class FileNodeVO1 extends SuperVO {
	private static final long serialVersionUID = -3346824972936318762L;
	private String pk;
	private String filename;
	private String pk_parent;
	private String filepath;
	private String filelength;
	private BigDecimal filesize;
	private String creator;
	private String pk_doc;
	private Object contentdata;
	private Integer hashidx;
	private String isdoc;
	private String isfolder;
	private String lasttime;
	private String rootpath;
	private Integer dr = Integer.valueOf(0);
	private UFDateTime ts;
	public static final String PK_FILENODE = "pk_filenode";
	public static final String FILEPATH = "filepath";
	public static final String FILESIZE = "filesize";
	public static final String FILELENGTH = "filelength";
	public static final String CREATOR = "creator";
	public static final String PK_DOC = "pk_doc";
	public static final String CONTENTDATA = "contentdata";
	public static final String HASHIDX = "hashidx";
	public static final String ISDOC = "isdoc";
	public static final String ISFOLDER = "isfolder";
	public static final String LASTTIME = "lasttime";
	public static final String ROOTPATH = "rootpath";

	public String getPk() {
		return this.pk;
	}

	public void setPk(String newPk) {
		this.pk = newPk;
	}

	public String getFilepath() {
		return this.filepath;
	}

	public void setFilepath(String newFilepath) {
		this.filepath = newFilepath;
	}
	
	public String getFilelength() {
		return this.filelength;
	}

	public void setFilelength(String newFilelength) {
		this.filelength = newFilelength;
	}

	public BigDecimal getFilesize() {
		return this.filesize;
	}

	public void setFilesize(BigDecimal newFilesize) {
		this.filesize = newFilesize;
	}

	public String getCreator() {
		return this.creator;
	}

	public void setCreator(String newCreator) {
		this.creator = newCreator;
	}

	public String getPk_doc() {
		return this.pk_doc;
	}

	public void setPk_doc(String newPk_doc) {
		this.pk_doc = newPk_doc;
	}

	public Object getContentdata() {
		return this.contentdata;
	}

	public void setContentdata(Object newContentdata) {
		this.contentdata = newContentdata;
	}

	public Integer getHashidx() {
		return this.hashidx;
	}

	public void setHashidx(Integer newHashidx) {
		this.hashidx = newHashidx;
	}

	public String getIsdoc() {
		return this.isdoc;
	}

	public void setIsdoc(String newIsdoc) {
		this.isdoc = newIsdoc;
	}

	public String getIsfolder() {
		return this.isfolder;
	}

	public void setIsfolder(String newIsfolder) {
		this.isfolder = newIsfolder;
	}

	public String getLasttime() {
		return this.lasttime;
	}

	public void setLasttime(String newLasttime) {
		this.lasttime = newLasttime;
	}

	public String getRootpath() {
		return this.rootpath;
	}

	public void setRootpath(String newRootpath) {
		this.rootpath = newRootpath;
	}

	public Integer getDr() {
		return this.dr;
	}

	public void setDr(Integer newDr) {
		this.dr = newDr;
	}

	public UFDateTime getTs() {
		return this.ts;
	}

	public void setTs(UFDateTime newTs) {
		this.ts = newTs;
	}

	public String getParentPKFieldName() {
		return null;
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

	public FileNodeVO1() {
	}

	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getPk_parent() {
		return this.pk_parent;
	}

	public void setPk_parent(String pk_parent) {
		this.pk_parent = pk_parent;
	}
}
