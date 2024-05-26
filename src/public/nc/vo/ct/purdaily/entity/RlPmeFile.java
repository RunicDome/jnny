package nc.vo.ct.purdaily.entity;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;

public class RlPmeFile extends SuperVO {

	/**
	 * String urlstring = FileStorageClient.getInstance()
							.getDownloadURL(null, filels.get(i)[4] + "");
	 */
	private static final long serialVersionUID = 1L;

	/** 主键 */
	private String pk_rl_pme_file;

	/** eps主键 */
	private String eps_id;

	/** 项目主键 */
	private String project_code;
	/** 项目主键 */
	private String project_name;
	/** 项目主键 */
	private String project_id;
	/** 项目主键 */
	private UFBoolean select;

	/** 一级目录 */
	private String catalog1;

	/** 二级目录 */
	private String catalog2;

	/** 三级目录 */
	private String catalog3;

	/** 四级目录 */
	private String catalog4;

	/** 五级目录 */
	private String catalog5;

	/** 六级目录 */
	private String catalog6;

	/** 文件层级id */
	private String folder_id;

	/** 文件id */
	private String file_id;

	/** 文件路径(预览) */
	private String file_path;
	
	/** 文件下载路径 */
	private String file_down_path;

	/** 日期 */
	private String pk_bill;
	
	/** 日期 */
	private UFDateTime ts;

	/** 自定义项1 */
	private String def1;

	/** 自定义项2 */
	private String def2;

	/** 自定义项3 */
	private String def3;

	/** 自定义项4 */
	private String def4;

	/** 自定义项5 */
	private String def5;
	
	/** 自定义项5 */
	private String bill_type;

	public static final String PKRLPMEFILE = "pk_rl_pme_file";
	public static final String EPS_ID = "eps_id";
	public static final String PROJECT_ID = "project_id";
	public static final String CATALOG1 = "catalog1";
	public static final String CATALOG2 = "catalog2";
	public static final String CATALOG3 = "catalog3";
	public static final String CATALOG4 = "catalog4";
	public static final String CATALOG5 = "catalog5";
	public static final String CATALOG6 = "catalog6";
	public static final String FOLDER_ID = "folder_id";
	public static final String FILE_ID = "file_id";
	public static final String FILE_PATH = "file_path";
	public static final String FILE_DOWN_PATH = "file_down_path";
	public static final String TS = "ts";
	public static final String BILL_TYPE = "bill_type";
	public static final String PK_BILL = "pk_bill";
	/** 自定义项6 */
	private Integer dr = Integer.valueOf(0);

	public void setCatalog1(String catalog1) {
		this.catalog1 = catalog1;
	}

	public String getCatalog1() {
		return catalog1;
	}

	public void setCatalog2(String catalog2) {
		this.catalog2 = catalog2;
	}

	public String getCatalog2() {
		return catalog2;
	}

	public void setCatalog3(String catalog3) {
		this.catalog3 = catalog3;
	}

	public String getCatalog3() {
		return catalog3;
	}

	public void setCatalog4(String catalog4) {
		this.catalog4 = catalog4;
	}

	public String getCatalog4() {
		return catalog4;
	}

	public void setCatalog5(String catalog5) {
		this.catalog5 = catalog5;
	}

	public String getCatalog5() {
		return catalog5;
	}

	public void setCatalog6(String catalog6) {
		this.catalog6 = catalog6;
	}

	public String getCatalog6() {
		return catalog6;
	}

	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	public UFDateTime getTs() {
		return ts;
	}

	public void setDef1(String def1) {
		this.def1 = def1;
	}

	public String getDef1() {
		return def1;
	}

	public void setDef2(String def2) {
		this.def2 = def2;
	}

	public String getDef2() {
		return def2;
	}

	public void setDef3(String def3) {
		this.def3 = def3;
	}

	public String getDef3() {
		return def3;
	}

	public void setDef4(String def4) {
		this.def4 = def4;
	}

	public String getDef4() {
		return def4;
	}

	public void setDef5(String def5) {
		this.def5 = def5;
	}

	public String getDef5() {
		return def5;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public String getPk_rl_pme_file() {
		return pk_rl_pme_file;
	}

	public void setPk_rl_pme_file(String pk_rl_pme_file) {
		this.pk_rl_pme_file = pk_rl_pme_file;
	}

	public String getEps_id() {
		return eps_id;
	}

	public void setEps_id(String eps_id) {
		this.eps_id = eps_id;
	}

	public String getProject_id() {
		return project_id;
	}

	public void setProject_id(String project_id) {
		this.project_id = project_id;
	}

	public String getFolder_id() {
		return folder_id;
	}

	public void setFolder_id(String folder_id) {
		this.folder_id = folder_id;
	}

	public String getFile_id() {
		return file_id;
	}

	public void setFile_id(String file_id) {
		this.file_id = file_id;
	}

	public String getFile_path() {
		return file_path;
	}

	public void setFile_path(String file_path) {
		this.file_path = file_path;
	}

	public String getPKFieldName() {
		return "pk_rl_pme_file";
	}

	public String getTableName() {
		return "rl_pme_file";
	}

	public static String getDefaultTableName() {
		return "rl_pme_file";
	}

	public RlPmeFile() {
	}

	public String getBill_type() {
		return bill_type;
	}

	public void setBill_type(String bill_type) {
		this.bill_type = bill_type;
	}

	public String getFile_down_path() {
		return file_down_path;
	}

	public void setFile_down_path(String file_down_path) {
		this.file_down_path = file_down_path;
	}

	public String getProject_name() {
		return project_name;
	}

	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}

	public String getProject_code() {
		return project_code;
	}

	public void setProject_code(String project_code) {
		this.project_code = project_code;
	}

	public UFBoolean getSelect() {
		return select;
	}

	public void setSelect(UFBoolean select) {
		this.select = select;
	}

	public String getPk_bill() {
		return pk_bill;
	}

	public void setPk_bill(String pk_bill) {
		this.pk_bill = pk_bill;
	}
}
