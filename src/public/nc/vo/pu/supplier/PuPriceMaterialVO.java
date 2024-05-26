package nc.vo.pu.supplier;

import nc.vo.pub.SuperVO;

// 供应商平台 待询比物料
public class PuPriceMaterialVO extends SuperVO {
	private static final long serialVersionUID = 1L;
	public static final String ID = "id";
	public static final String PK_PRAYBILL = "pk_praybill";
	public static final String DEPT_NAME = "dept_name";
	public static final String PK_DEPT = "pk_dept";
	public static final String PLAN_DEPT_NAME = "plan_dept_name";
	public static final String PK_PLAN_DEPT = "pk_plan_dept";
	public static final String PURCHASE_NAME = "purchase_name";
	public static final String PURCHASE_CODE = "purchase_code";
	public static final String PK_PURCHASE = "pk_purchase";
	public static final String VBILLCODE = "vbillcode";
	public static final String DBILLDATE = "dbilldate";
	public static final String PSN_NAME = "psn_name";
	public static final String PK_PSN = "pk_psn";
	public static final String STOCK_CODE = "stock_code";
	public static final String STOCK_NAME = "stock_name";
	public static final String PK_STOCK_ORG = "pk_stock_org";
	public static final String PROJECT_NAME = "project_name";
	public static final String PK_PROJECT = "pk_project";
	public static final String PK_MATERIAL = "pk_material";
	public static final String MATERIAL_CODE = "material_code";
	public static final String MATERIAL_NAME = "material_name";
	public static final String MATERIAL_SPEC = "material_spec";
	public static final String MATERIAL_TYPE = "material_type";
	public static final String MAR_BAS_CLASS_NAME = "mar_bas_class_name";
	public static final String PK_MAR_BAS_CLASS = "pk_mar_bas_class";
	public static final String MAIN_MEAS_DOC_NAME = "main_meas_doc_name";
	public static final String MEAS_DOC_NAME = "meas_doc_name";
	public static final String NASTNUM = "nastnum";
	public static final String NNUM = "nnum";
	public static final String D_SUGGEST_DATE = "d_suggest_date";
	public static final String VB_MEMO = "vb_memo";
	public static final String D_REQ_DATE = "d_req_date";
	public static final String PK_EMPLOYEE = "pk_employee";
	public static final String PSN_DOC_NAME = "psn_doc_name";
	public static final String TS = "ts";
	public static final String DR = "dr";
	private String id;
	private String pk_praybill_b;
	private String pk_praybill;
	private String dept_name;
	private String pk_dept;
	private String plan_dept_name;

	public String getPurchase_code() {
		return purchase_code;
	}

	public void setPurchase_code(String purchase_code) {
		this.purchase_code = purchase_code;
	}

	public String getStock_code() {
		return stock_code;
	}

	public void setStock_code(String stock_code) {
		this.stock_code = stock_code;
	}

	private String pk_plan_dept;
	private String purchase_code;
	private String purchase_name;
	private String pk_purchase;
	private String vbillcode;
	private String dbilldate;
	private String psn_name;
	private String pk_psn;
	private String stock_code;
	private String stock_name;
	private String pk_stock_org;
	private String project_name;
	private String pk_project;
	private String pk_material;
	private String material_code;
	private String material_name;
	private String material_spec;
	private String material_type;
	private String mar_bas_class_name;
	private String pk_mar_bas_class;
	private String main_meas_doc_name;
	private String meas_doc_name;
	private double nastnum;
	private double nnum;
	private String d_suggest_date;
	private String vb_memo;
	private String d_req_date;
	private String pk_employee;
	private String psn_doc_name;
	private String ts;
	private int dr = 0;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPk_praybill_b() {
		return pk_praybill_b;
	}

	public void setPk_praybill_b(String pk_praybill_b) {
		this.pk_praybill_b = pk_praybill_b;
	}

	public String getPk_praybill() {
		return pk_praybill;
	}

	public void setPk_praybill(String pk_praybill) {
		this.pk_praybill = pk_praybill;
	}

	public String getDept_name() {
		return dept_name;
	}

	public void setDept_name(String dept_name) {
		this.dept_name = dept_name;
	}

	public String getPk_dept() {
		return pk_dept;
	}

	public void setPk_dept(String pk_dept) {
		this.pk_dept = pk_dept;
	}

	public String getPlan_dept_name() {
		return plan_dept_name;
	}

	public void setPlan_dept_name(String plan_dept_name) {
		this.plan_dept_name = plan_dept_name;
	}

	public String getPk_plan_dept() {
		return pk_plan_dept;
	}

	public void setPk_plan_dept(String pk_plan_dept) {
		this.pk_plan_dept = pk_plan_dept;
	}

	public String getPurchase_name() {
		return purchase_name;
	}

	public void setPurchase_name(String purchase_name) {
		this.purchase_name = purchase_name;
	}

	public String getPk_purchase() {
		return pk_purchase;
	}

	public void setPk_purchase(String pk_purchase) {
		this.pk_purchase = pk_purchase;
	}

	public String getVbillcode() {
		return vbillcode;
	}

	public void setVbillcode(String vbillcode) {
		this.vbillcode = vbillcode;
	}

	public String getDbilldate() {
		return dbilldate;
	}

	public void setDbilldate(String dbilldate) {
		this.dbilldate = dbilldate;
	}

	public String getPsn_name() {
		return psn_name;
	}

	public void setPsn_name(String psn_name) {
		this.psn_name = psn_name;
	}

	public String getPk_psn() {
		return pk_psn;
	}

	public void setPk_psn(String pk_psn) {
		this.pk_psn = pk_psn;
	}

	public String getStock_name() {
		return stock_name;
	}

	public void setStock_name(String stock_name) {
		this.stock_name = stock_name;
	}

	public String getPk_stock_org() {
		return pk_stock_org;
	}

	public void setPk_stock_org(String pk_stock_org) {
		this.pk_stock_org = pk_stock_org;
	}

	public String getProject_name() {
		return project_name;
	}

	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}

	public String getPk_project() {
		return pk_project;
	}

	public void setPk_project(String pk_project) {
		this.pk_project = pk_project;
	}

	public String getPk_material() {
		return pk_material;
	}

	public void setPk_material(String pk_material) {
		this.pk_material = pk_material;
	}

	public String getMaterial_code() {
		return material_code;
	}

	public void setMaterial_code(String material_code) {
		this.material_code = material_code;
	}

	public String getMaterial_name() {
		return material_name;
	}

	public void setMaterial_name(String material_name) {
		this.material_name = material_name;
	}

	public String getMaterial_spec() {
		return material_spec;
	}

	public void setMaterial_spec(String material_spec) {
		this.material_spec = material_spec;
	}

	public String getMaterial_type() {
		return material_type;
	}

	public void setMaterial_type(String material_type) {
		this.material_type = material_type;
	}

	public String getMar_bas_class_name() {
		return mar_bas_class_name;
	}

	public void setMar_bas_class_name(String mar_bas_class_name) {
		this.mar_bas_class_name = mar_bas_class_name;
	}

	public String getPk_mar_bas_class() {
		return pk_mar_bas_class;
	}

	public void setPk_mar_bas_class(String pk_mar_bas_class) {
		this.pk_mar_bas_class = pk_mar_bas_class;
	}

	public String getMain_meas_doc_name() {
		return main_meas_doc_name;
	}

	public void setMain_meas_doc_name(String main_meas_doc_name) {
		this.main_meas_doc_name = main_meas_doc_name;
	}

	public String getMeas_doc_name() {
		return meas_doc_name;
	}

	public void setMeas_doc_name(String meas_doc_name) {
		this.meas_doc_name = meas_doc_name;
	}

	public double getNastnum() {
		return nastnum;
	}

	public void setNastnum(double nastnum) {
		this.nastnum = nastnum;
	}

	public double getNnum() {
		return nnum;
	}

	public void setNnum(double nnum) {
		this.nnum = nnum;
	}

	public String getD_suggest_date() {
		return d_suggest_date;
	}

	public void setD_suggest_date(String d_suggest_date) {
		this.d_suggest_date = d_suggest_date;
	}

	public String getVb_memo() {
		return vb_memo;
	}

	public void setVb_memo(String vb_memo) {
		this.vb_memo = vb_memo;
	}

	public String getD_req_date() {
		return d_req_date;
	}

	public void setD_req_date(String d_req_date) {
		this.d_req_date = d_req_date;
	}

	public String getPk_employee() {
		return pk_employee;
	}

	public void setPk_employee(String pk_employee) {
		this.pk_employee = pk_employee;
	}

	public String getPsn_doc_name() {
		return psn_doc_name;
	}

	public void setPsn_doc_name(String psn_doc_name) {
		this.psn_doc_name = psn_doc_name;
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	public int getDr() {
		return dr;
	}

	public void setDr(int dr) {
		this.dr = dr;
	}

	public String getTableName() {
		return "pu_price_material";
	}

	public String getPKFieldName() {
		return "id";
	}
}
