package nc.vo.pu.supplier;

import java.math.BigDecimal;

import nc.vo.pub.SuperVO;

// 供应商平台 物料价格档案
public class BdMaterialPriceVO extends SuperVO {
	private static final long serialVersionUID = 1L;
	public static final String ID = "id";
	public static final String TS = "ts";
	public static final String DR = "dr";
	public static final String CREATE_BY = "create_by";
	public static final String CREATE_TIME = "create_time";
	public static final String UPDATE_BY = "update_by";
	public static final String UPDATE_TIME = "update_time";
	public static final String REMARK = "remark";
	public static final String PK_MATERIAL = "pk_material";
	public static final String MATERIAL_CODE = "material_code";
	public static final String MATERIAL_NAME = "material_name";
	public static final String PK_SUPPLIER = "pk_supplier";
	public static final String SUPPLIER_NAME = "supplier_name";
	public static final String SUPPLIER_CODE = "supplier_code";
	public static final String PK_ORG = "pk_org";
	public static final String ORG_CODE = "org_code";
	public static final String ORG_NAME = "org_name";
	public static final String NUM = "num";
	public static final String TAX_PRICE = "tax_price";
	public static final String NO_TAX_PRICE = "no_tax_price";
	public static final String TAX_RATE = "tax_rate";
	public static final String PK_TAX_RATE = "pk_tax_rate";
	public static final String STATUS = "status";
	public static final String EFFECTIVE_START = "effective_start";
	public static final String EFFECTIVE_END = "effective_end";
	public static final String PROJECT_CODE = "project_code";
	public static final String PROJECT_NAME = "project_name";
	public static final String PROJECT_ID = "project_id";
	public static final String PURCHASER_ID = "purchaser_id";
	public static final String PURCHASER_NAME = "purchaser_name";
	public static final String FROM_ID = "from_id";
	public static final String FROM_BODY_ID = "from_body_id";
	public static final String FROM_BILL_TYPE = "from_bill_type";

	private String id;
	private String ts;
	private int dr;
	private String create_by;
	private String create_time;
	private String update_by;
	private String remark;
	private String pk_material;
	private String material_code;
	private String material_name;
	private String pk_supplier;
	private String supplier_name;
	private String supplier_code;
	private String pk_org;
	private String org_code;
	private String org_name;
	private BigDecimal nnum;
	private BigDecimal tax_price;
	private BigDecimal no_tax_price;
	private int tax_rate;
	private String pk_tax_rate;
	private int status;
	private String effective_start;
	private String effective_end;
	private String project_code;
	private String project_name;
	private String project_id;
	private String purchaser_id;
	private String purchaser_name;
	private String from_id;
	private String from_body_id;
	private String from_bill_type;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTs() {
		return ts;
	}

	public String getCreate_by() {
		return create_by;
	}

	public void setCreate_by(String create_by) {
		this.create_by = create_by;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getUpdate_by() {
		return update_by;
	}

	public void setUpdate_by(String update_by) {
		this.update_by = update_by;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public String getPk_supplier() {
		return pk_supplier;
	}

	public void setPk_supplier(String pk_supplier) {
		this.pk_supplier = pk_supplier;
	}

	public String getSupplier_name() {
		return supplier_name;
	}

	public void setSupplier_name(String supplier_name) {
		this.supplier_name = supplier_name;
	}

	public String getSupplier_code() {
		return supplier_code;
	}

	public void setSupplier_code(String supplier_code) {
		this.supplier_code = supplier_code;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public String getOrg_code() {
		return org_code;
	}

	public void setOrg_code(String org_code) {
		this.org_code = org_code;
	}

	public String getOrg_name() {
		return org_name;
	}

	public void setOrg_name(String org_name) {
		this.org_name = org_name;
	}

	public BigDecimal getNnum() {
		return nnum;
	}

	public void setNnum(BigDecimal nnum) {
		this.nnum = nnum;
	}

	public BigDecimal getTax_price() {
		return tax_price;
	}

	public void setTax_price(BigDecimal tax_price) {
		this.tax_price = tax_price;
	}

	public BigDecimal getNo_tax_price() {
		return no_tax_price;
	}

	public void setNo_tax_price(BigDecimal no_tax_price) {
		this.no_tax_price = no_tax_price;
	}

	public int getTax_rate() {
		return tax_rate;
	}

	public void setTax_rate(int tax_rate) {
		this.tax_rate = tax_rate;
	}

	public String getPk_tax_rate() {
		return pk_tax_rate;
	}

	public void setPk_tax_rate(String pk_tax_rate) {
		this.pk_tax_rate = pk_tax_rate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getEffective_start() {
		return effective_start;
	}

	public void setEffective_start(String effective_start) {
		this.effective_start = effective_start;
	}

	public String getEffective_end() {
		return effective_end;
	}

	public void setEffective_end(String effective_end) {
		this.effective_end = effective_end;
	}

	public String getProject_code() {
		return project_code;
	}

	public void setProject_code(String project_code) {
		this.project_code = project_code;
	}

	public String getProject_name() {
		return project_name;
	}

	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}

	public String getProject_id() {
		return project_id;
	}

	public void setProject_id(String project_id) {
		this.project_id = project_id;
	}

	public String getPurchaser_id() {
		return purchaser_id;
	}

	public void setPurchaser_id(String purchaser_id) {
		this.purchaser_id = purchaser_id;
	}

	public String getPurchaser_name() {
		return purchaser_name;
	}

	public void setPurchaser_name(String purchaser_name) {
		this.purchaser_name = purchaser_name;
	}

	public String getFrom_id() {
		return from_id;
	}

	public void setFrom_id(String from_id) {
		this.from_id = from_id;
	}

	public String getFrom_body_id() {
		return from_body_id;
	}

	public void setFrom_body_id(String from_body_id) {
		this.from_body_id = from_body_id;
	}

	public String getFrom_bill_type() {
		return from_bill_type;
	}

	public void setFrom_bill_type(String from_bill_type) {
		this.from_bill_type = from_bill_type;
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
		return "bd_material_price";
	}

	public String getPKFieldName() {
		return "id";
	}
}
