package nc.vo.pu.supplier;

import java.math.BigDecimal;

import nc.vo.pub.SuperVO;

// 供应商平台 待签合同物料
public class PuCtMaterialVO extends SuperVO {
	private static final long serialVersionUID = 1L;
	public static final String PK_CT_MATERIAL = "pk_ct_material";
	public static final String PK_SUPPLIER_PRICE = "pk_supplier_price";
	public static final String PK_SUPPLIER_PRICE_B = "pk_supplier_price_b";
	public static final String PK_ENQUIRY = "pk_enquiry";
	public static final String PK_ENQUIRY_B = "pk_enquiry_b";
	public static final String PK_ENQUIRY_SS = "pk_enquiry_ss";
	public static final String PK_SUPPLIER = "pk_supplier";
	public static final String PK_MATERIAL = "pk_material";
	public static final String MATERIAL_CODE = "material_code";
	public static final String MATERIAL_NAME = "material_name";
	public static final String MATERIAL_SPEC = "material_spec";
	public static final String MATERIAL_TYPE = "material_type";
	public static final String MAIN_MEAS_DOC_NAME = "main_meas_doc_name";
	public static final String MEAS_DOC_NAME = "meas_doc_name";
	public static final String VB_MEMO = "vb_memo";
	public static final String NASTNUM = "nastnum";
	public static final String NNUM = "nnum";
	public static final String PRICE = "price";
	public static final String AMOUNT = "amount";
	public static final String SUGGESTED_PRICE = "suggested_price";
	public static final String CONTROL_PRICE = "control_price";
	public static final String TS = "ts";
	public static final String STATE = "state";
	public static final String DR = "dr";
	public static final String IS_PURCHASES = "is_purchases";
	public static final String CREATE_BY = "create_by";
	public static final String CREATE_TIME = "create_time";
	public static final String UPDATE_BY = "update_by";
	public static final String UPDATE_TIME = "update_time";
	public static final String SIGN_BY = "sign_by";
	public static final String SIGN_TIME = "sign_time";
	public static final String PK_ORG = "pk_org";
	public static final String USER_ID = "user_id";
	public static final String CUSERID = "cuserid";
	public static final String PK_PRAYBILL = "pk_praybill";
	public static final String PK_PRAYBILL_B = "pk_praybill_b";
	public static final String FROM_TYPE = "from_type";
	public static final String VBILLCODE = "vbillcode";
	public static final String REMARK = "remark";
	public static final String PRAYBILL_NO = "praybill_no";
	public static final String USER_NAME = "user_name";
	public static final String SIGN_NAME = "sign_name";
	public static final String SUPPLIER_NAME = "supplier_name";
	public static final String ENQUIRY_NAME = "enquiry_name";

	private String pk_ct_material;
	private String pk_supplier_price;
	private String pk_supplier_price_b;
	private String pk_enquiry;
	private String pk_enquiry_b;
	private String pk_enquiry_ss;
	private String pk_supplier;
	private String pk_material;
	private String material_code;
	private String material_name;
	private String material_spec;
	private String material_type;
	private String main_meas_doc_name;
	private String meas_doc_name;
	private String vb_memo;
	private BigDecimal nastnum;
	private BigDecimal nnum;
	private BigDecimal price;
	private BigDecimal amount;
	private BigDecimal suggested_price;
	private BigDecimal control_price;
	private String ts;
	private int state;
	private int dr = 0;
	private String is_purchases;
	private String create_by;

	public String getPk_ct_material() {
		return pk_ct_material;
	}

	public void setPk_ct_material(String pk_ct_material) {
		this.pk_ct_material = pk_ct_material;
	}

	public String getPk_supplier_price() {
		return pk_supplier_price;
	}

	public void setPk_supplier_price(String pk_supplier_price) {
		this.pk_supplier_price = pk_supplier_price;
	}

	public String getPk_supplier_price_b() {
		return pk_supplier_price_b;
	}

	public void setPk_supplier_price_b(String pk_supplier_price_b) {
		this.pk_supplier_price_b = pk_supplier_price_b;
	}

	public String getPk_enquiry() {
		return pk_enquiry;
	}

	public void setPk_enquiry(String pk_enquiry) {
		this.pk_enquiry = pk_enquiry;
	}

	public String getPk_enquiry_b() {
		return pk_enquiry_b;
	}

	public void setPk_enquiry_b(String pk_enquiry_b) {
		this.pk_enquiry_b = pk_enquiry_b;
	}

	public String getPk_enquiry_ss() {
		return pk_enquiry_ss;
	}

	public void setPk_enquiry_ss(String pk_enquiry_ss) {
		this.pk_enquiry_ss = pk_enquiry_ss;
	}

	public String getPk_supplier() {
		return pk_supplier;
	}

	public void setPk_supplier(String pk_supplier) {
		this.pk_supplier = pk_supplier;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getSuggested_price() {
		return suggested_price;
	}

	public void setSuggested_price(BigDecimal suggested_price) {
		this.suggested_price = suggested_price;
	}

	public BigDecimal getControl_price() {
		return control_price;
	}

	public void setControl_price(BigDecimal control_price) {
		this.control_price = control_price;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getIs_purchases() {
		return is_purchases;
	}

	public void setIs_purchases(String is_purchases) {
		this.is_purchases = is_purchases;
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

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getSign_by() {
		return sign_by;
	}

	public void setSign_by(String sign_by) {
		this.sign_by = sign_by;
	}

	public String getSign_time() {
		return sign_time;
	}

	public void setSign_time(String sign_time) {
		this.sign_time = sign_time;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getCuserid() {
		return cuserid;
	}

	public void setCuserid(String cuserid) {
		this.cuserid = cuserid;
	}

	public int getFrom_type() {
		return from_type;
	}

	public void setFrom_type(int from_type) {
		this.from_type = from_type;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getPraybill_no() {
		return praybill_no;
	}

	public void setPraybill_no(String praybill_no) {
		this.praybill_no = praybill_no;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getSign_name() {
		return sign_name;
	}

	public void setSign_name(String sign_name) {
		this.sign_name = sign_name;
	}

	public String getSupplier_name() {
		return supplier_name;
	}

	public void setSupplier_name(String supplier_name) {
		this.supplier_name = supplier_name;
	}

	public String getEnquiry_name() {
		return enquiry_name;
	}

	public void setEnquiry_name(String enquiry_name) {
		this.enquiry_name = enquiry_name;
	}

	private String create_time;
	private String update_by;
	private String update_time;
	private String sign_by;
	private String sign_time;
	private String pk_org;
	private String user_id;
	private String cuserid;
	private String pk_praybill;
	private String pk_praybill_b;
	private int from_type;
	private String vbillcode;
	private String remark;
	private String praybill_no;
	private String user_name;
	private String sign_name;
	private String supplier_name;
	private String enquiry_name;

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

	public String getVbillcode() {
		return vbillcode;
	}

	public void setVbillcode(String vbillcode) {
		this.vbillcode = vbillcode;
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

	public BigDecimal getNastnum() {
		return nastnum;
	}

	public void setNastnum(BigDecimal nastnum) {
		this.nastnum = nastnum;
	}

	public BigDecimal getNnum() {
		return nnum;
	}

	public void setNnum(BigDecimal nnum) {
		this.nnum = nnum;
	}

	public String getVb_memo() {
		return vb_memo;
	}

	public void setVb_memo(String vb_memo) {
		this.vb_memo = vb_memo;
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
		return "pu_ct_material";
	}

	public String getPKFieldName() {
		return "pk_ct_material";
	}
}
