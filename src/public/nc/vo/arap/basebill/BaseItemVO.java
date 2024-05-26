package nc.vo.arap.basebill;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import nc.bs.logging.Logger;
import nc.vo.arap.pub.BillEnumCollection;
import nc.vo.arap.pub.BillEnumCollection.TaxType;
import nc.vo.arap.termitem.TermVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import org.apache.commons.lang.StringUtils;

@SuppressWarnings({ "unused", "deprecation" })
public abstract class BaseItemVO extends SuperVO implements IArapItemFieldVO,
		IArapMoneyFieldVO {
	private static final long serialVersionUID = 1L;
	private static final String[] moneyFieldsForAddBal = { "quantity_cr",
			"money_cr", "local_money_cr", "groupcrebit", "globalcrebit" };

	private static final String[] isdebitMoneyFieldsForAddBal = {
			"quantity_de", "money_de", "local_money_de", "groupdebit",
			"globaldebit" };

	private static final String[] MoneyFieldsBal = { "quantity_bal",
			"money_bal", "local_money_bal", "groupbalance", "globalbalance" };

	private static final String[] SKMoneyFields = { "quantity_cr",
			"local_money_cr", "notax_cr", "money_cr", "money_bal",
			"local_money_bal", "quantity_bal", "local_tax_cr",
			"local_notax_cr", "grouptax_cre", "groupnotax_cre", "groupcrebit",
			"globalcrebit", "globaltax_cre", "globalnotax_cre" };

	private static final String[] FKMoneyFields = { "money_de",
			"local_money_de", "quantity_de", "money_bal", "local_money_bal",
			"quantity_bal", "local_tax_de", "notax_de", "local_notax_de",
			"grouptax_de", "groupnotax_de", "groupdebit", "globaltax_de",
			"globalnotax_de", "globaldebit" };

	private static final String[] YFandZFMoneyFields = { "quantity_cr",
			"local_money_cr", "notax_cr", "money_cr", "money_bal",
			"local_money_bal", "quantity_bal", "local_tax_cr",
			"local_notax_cr", "grouptax_cre", "groupnotax_cre", "groupcrebit",
			"globalcrebit", "globaltax_cre", "globalnotax_cre", "caltaxmny",
			"nosubtax" };

	private static final String[] YSandZSMoneyFields = { "money_de",
			"local_money_de", "quantity_de", "money_bal", "local_money_bal",
			"quantity_bal", "local_tax_de", "notax_de", "local_notax_de",
			"grouptax_de", "groupnotax_de", "groupdebit", "globaltax_de",
			"globalnotax_de", "globaldebit", "caltaxmny", "nosubtax" };

	private static final String[] RedundancyFields = { "pk_org_v", "sett_org",
			"sett_org_v", "pk_group", "billdate", "pk_tradetype",
			"pk_tradetypeid", "pk_billtype", "billclass", "billno" };

	public String[] getRedundancyFields() {
		return RedundancyFields;
	}

	public static String[] getMoneyFieldsBal(boolean isdebit) {
		return MoneyFieldsBal;
	}

	public static String[] getMoneyFieldsForAddBal(boolean isdebit) {
		if (isdebit) {
			return isdebitMoneyFieldsForAddBal;
		}
		return moneyFieldsForAddBal;
	}

	public static final List<String> MONEYPARTFIELD_LIST = Arrays
			.asList(new String[] { "money_de", "local_money_de", "money_bal",
					"local_money_bal", "local_tax_de", "notax_de",
					"local_notax_de", "groupdebit", "grouptax_de",
					"groupnotax_de", "globaldebit", "globaltax_de",
					"globalnotax_de", "local_money_cr", "money_cr",
					"local_tax_cr", "notax_cr", "local_notax_cr",
					"groupcrebit", "grouptax_cre", "groupnotax_cre",
					"globalcrebit", "globaltax_cre", "globalnotax_cre",
					"occupationmny", "taxprice", "caltaxmny", "nosubtax" });

	public static final List<String> MONEYFIELD_LIST = Arrays
			.asList(new String[] { "money_de", "local_money_de", "quantity_de",
					"money_bal", "local_money_bal", "quantity_bal",
					"local_tax_de", "notax_de", "local_notax_de", "groupdebit",
					"grouptax_de", "groupnotax_de", "globaldebit",
					"globaltax_de", "globalnotax_de", "quantity_cr",
					"local_money_cr", "money_cr", "local_tax_cr", "notax_cr",
					"local_notax_cr", "groupcrebit", "grouptax_cre",
					"groupnotax_cre", "globalcrebit", "globaltax_cre",
					"globalnotax_cre", "occupationmny", "price",
					"groupbalance", "globalbalance", "taxprice", "caltaxmny",
					"nosubtax" });

	public static final List<String> MONEYFIELD_LIST_NORMAL = Arrays
			.asList(new String[] { "money_de", "money_bal", "notax_de",
					"money_cr", "notax_cr", "occupationmny", "caltaxmny",
					"nosubtax" });

	public static final List<String> MONEYFIELD_LIST_LOCAL = Arrays
			.asList(new String[] { "local_money_de", "local_money_bal",
					"local_tax_de", "local_notax_de", "local_money_cr",
					"local_tax_cr", "local_notax_cr" });

	public static final List<String> MONEYFIELD_LIST_GROUP = Arrays
			.asList(new String[] { "groupdebit", "grouptax_de",
					"groupnotax_de", "groupcrebit", "grouptax_cre",
					"groupnotax_cre", "groupbalance" });

	public static final List<String> MONEYFIELD_LIST_GLOBAL = Arrays
			.asList(new String[] { "globaldebit", "globaltax_de",
					"globalnotax_de", "globalcrebit", "globaltax_cre",
					"globalnotax_cre", "globalbalance" });

	public static final List<String> MONEYFIELD_LIST_QUANTITY = Arrays
			.asList(new String[] { "quantity_de", "quantity_bal", "quantity_cr" });

	public static final List<String> MONEYFIELD_LIST_PRICE = Arrays
			.asList(new String[] { "price", "taxprice" });

	public static final List<String> RATEFIELD_LIST = Arrays
			.asList(new String[] { "rate", "grouprate", "globalrate" });
	public static final String FB = "fb.";
	private TermVO[] term;

	public String[] getMoneyFields(String billclass) {
		String[] fields = new String[0];
		if ((billclass.equals("ys")) || (billclass.equals("zs"))) {
			fields = YSandZSMoneyFields;
		} else if ((billclass.equals("yf")) || (billclass.equals("zf"))) {
			fields = YFandZFMoneyFields;
		} else if (billclass.equals("fk")) {
			fields = FKMoneyFields;
		} else if (billclass.equals("sk")) {
			fields = SKMoneyFields;
		}
		return fields;
	}

	public List<String> getMoneyFields() {
		return MONEYFIELD_LIST;
	}

	public List<String> getMoneyPartFields() {
		return MONEYPARTFIELD_LIST;
	}

	public List<String> getRateFields() {
		return RATEFIELD_LIST;
	}

	public String getPrimaryKey() {
		return (String) getAttributeValue(getPKFieldName());
	}

	private SuperVO settleBody = null;
	protected Integer prepay = Integer.valueOf(0);
	private UFDouble money_de = UFDouble.ZERO_DBL;
	private UFDouble money_cr = UFDouble.ZERO_DBL;
	private UFDouble money_bal = UFDouble.ZERO_DBL;
	private UFDouble local_money_bal = UFDouble.ZERO_DBL;
	private UFDouble local_money_de = UFDouble.ZERO_DBL;
	private UFDouble local_money_cr = UFDouble.ZERO_DBL;
	private UFDouble local_notax_de = UFDouble.ZERO_DBL;
	private UFDouble local_notax_cr = UFDouble.ZERO_DBL;
	private UFDouble local_tax_de = UFDouble.ZERO_DBL;
	private UFDouble local_tax_cr = UFDouble.ZERO_DBL;

	private UFDouble price;
	private UFDouble taxprice;
	private UFDouble local_price;
	private UFDouble local_taxprice;
	private UFDouble taxrate = UFDouble.ZERO_DBL;

	private UFDouble notax_de = UFDouble.ZERO_DBL;
	private UFDouble notax_cr = UFDouble.ZERO_DBL;

	private UFDouble quantity_bal;

	private UFDouble quantity_de;
	private UFDouble quantity_cr;
	private UFDouble groupbalance = UFDouble.ZERO_DBL;
	private UFDouble groupdebit = UFDouble.ZERO_DBL;
	private UFDouble groupcrebit = UFDouble.ZERO_DBL;
	private UFDouble grouptax_de = UFDouble.ZERO_DBL;
	private UFDouble grouptax_cre = UFDouble.ZERO_DBL;
	private UFDouble groupnotax_de = UFDouble.ZERO_DBL;
	private UFDouble groupnotax_cre = UFDouble.ZERO_DBL;

	private UFDouble globaldebit = UFDouble.ZERO_DBL;
	private UFDouble globalbalance = UFDouble.ZERO_DBL;
	private UFDouble globaltax_de = UFDouble.ZERO_DBL;
	private UFDouble globalnotax_de = UFDouble.ZERO_DBL;
	private UFDouble globaltax_cre = UFDouble.ZERO_DBL;
	private UFDouble globalnotax_cre = UFDouble.ZERO_DBL;

	private UFDouble occupationmny = UFDouble.ZERO_DBL;

	private UFDouble rate = null;
	private UFDouble grouprate = null;
	private UFDouble globalrate = null;

	private UFDouble agentreceiveprimal = UFDouble.ZERO_DBL;
	private UFDouble agentreceivelocal = UFDouble.ZERO_DBL;
	private UFDouble groupagentreceivelocal = UFDouble.ZERO_DBL;
	private UFDouble globalagentreceivelocal = UFDouble.ZERO_DBL;

	private String commpayer;
	private Integer coordflag;
	private Integer rowtype;
	private String pk_rescenter;
	private UFBoolean ispaytermmodified;
	private UFBoolean isrefused = UFBoolean.FALSE;

	private String refuse_reason;
	private String pk_corp;
	private String pk_fiorg;
	private String pk_pcorg;
	private String pk_pcorg_v;
	private String pk_org_v;
	private String pk_fiorg_v;
	private String so_ordertype;
	private String so_transtype;
	private String so_org;
	private String pu_org;
	private String pu_psndoc;
	private String pu_deptid;
	private String so_psndoc;
	private String so_deptid;
	private String material;
	private String postunit;
	private UFDouble postpricenotax;
	private UFDouble postquantity;
	private UFDouble postprice;
	private String project;
	private String project_task;
	private String dealno;
	private String equipmentcode;
	private String productline;
	private String cashitem;
	private String bankrollprojet;
	private UFBoolean pausetransact = UFBoolean.FALSE;

	private UFDate billdate;
	private String pk_org;
	private String pk_group;
	private String pk_billtype;
	private String billclass;
	private String pk_tradetype;
	private String pk_tradetypeid;
	private UFDate busidate;
	private String pk_deptid_res;
	private String pk_subjcode;
	private String billno;
	private Integer objtype;
	private Integer rowno;
	private Integer direction;
	private String pk_ssitem;
	private String scomment;
	private String subjcode;
	private String pk_currtype;
	private String pk_deptid;
	private String pk_deptid_v;
	private String pu_deptid_v;
	private String so_deptid_v;
	private String pk_psndoc;
	private String taxnum;
	private String top_billid;
	private String top_itemid;
	private String top_billtype;
	private String top_termch;
	private String top_tradetype;
	private String src_tradetype;
	private String src_billtype;
	private String src_billid;
	private String src_itemid;
	private Integer taxtype = BillEnumCollection.TaxType.TAXOUT.VALUE;
	private String pk_payterm;
	private String payaccount;
	private String recaccount;
	private String cashaccount;
	private String ordercubasdoc;
	private String innerorderno;
	private String assetpactno;
	private String contractno;
	private String freecust;
	private String purchaseorder;
	private String checkelement;
	private String invoiceno;
	private String outstoreno;
	private String pk_jobphase;
	private String pk_job;
	private String def30;
	private String def29;
	private String def28;
	private String def27;
	private String def26;
	private String def25;
	private String def24;
	private String def23;
	private String def22;
	private String def21;
	private String def20;
	private String def19;
	private String def18;
	private String def17;
	private String def16;
	private String def15;
	private String def14;
	private String def13;
	private String def12;
	private String def11;
	private String def10;
	private String def9;
	private String def8;
	private String def7;
	private String def6;
	private String def5;
	private String def4;
	private String def3;
	private String def2;
	private String def1;
	private Integer dr;
	private UFDateTime ts;
	private UFDateTime top_changets;
	private String cbs;
	private UFBoolean insurance = UFBoolean.FALSE;
	private String def31;
	private String def32;
	private String def33;
	private String def34;
	private String def35;
	private String def36;
	private String def37;
	private String def38;
	private String def39;
	private String def40;
	private String def41;
	private String def42;
	private String def43;
	private String def44;
	private String def45;
	private String def46;
	private String def47;
	private String def48;
	private String def49;
	private String def50;
	private String def51;
	private String def52;
	private String def53;
	private String def54;
	private String def55;
	private String def56;
	private String def57;
	private String def58;
	private String def59;
	private String def60;
	private String def61;
	private String def62;
	private String def63;
	private String def64;
	private String def65;
	private String def66;
	private String def67;
	private String def68;
	private String def69;
	private String def70;
	private String def71;
	private String def72;
	private String def73;
	private String def74;
	private String def75;
	private String def76;
	private String def77;
	private String def78;
	private String def79;
	private String def80;
	// XBX新增100个自定义项
	private String def81;
	private String def82;
	private String def83;
	private String def84;
	private String def85;
	private String def86;
	private String def87;
	private String def88;
	private String def89;
	private String def90;
	private String def91;
	private String def92;
	private String def93;
	private String def94;
	private String def95;
	private String def96;
	private String def97;
	private String def98;
	private String def99;
	private String def100;
	private String def101;
	private String def102;
	private String def103;
	private String def104;
	private String def105;
	private String def106;
	private String def107;
	private String def108;
	private String def109;
	private String def110;
	private String def111;
	private String def112;
	private String def113;
	private String def114;
	private String def115;
	private String def116;
	private String def117;
	private String def118;
	private String def119;
	private String def120;
	private String def121;
	private String def122;
	private String def123;
	private String def124;
	private String def125;
	private String def126;
	private String def127;
	private String def128;
	private String def129;
	private String def130;
	private String def131;
	private String def132;
	private String def133;
	private String def134;
	private String def135;
	private String def136;
	private String def137;
	private String def138;
	private String def139;
	private String def140;
	private String def141;
	private String def142;
	private String def143;
	private String def144;
	private String def145;
	private String def146;
	private String def147;
	private String def148;
	private String def149;
	private String def150;
	private String def151;
	private String def152;
	private String def153;
	private String def154;
	private String def155;
	private String def156;
	private String def157;
	private String def158;
	private String def159;
	private String def160;
	private String def161;
	private String def162;
	private String def163;
	private String def164;
	private String def165;
	private String def166;
	private String def167;
	private String def168;
	private String def169;
	private String def170;
	private String def171;
	private String def172;
	private String def173;
	private String def174;
	private String def175;
	private String def176;
	private String def177;
	private String def178;
	private String def179;
	private String def180;
	private String so_org_v;
	private String sett_org;

	public UFBoolean getInsurance() {
		return this.insurance;
	}

	public void setInsurance(UFBoolean insurance) {
		this.insurance = insurance;
	}

	public String getCbs() {
		return this.cbs;
	}

	public void setCbs(String cbs) {
		this.cbs = cbs;
	}

	private String pu_org_v;

	private String sett_org_v;
	private String pk_billfiorg;
	private UFDouble globalcrebit = UFDouble.ZERO_DBL;
	private UFDouble forcemoney = UFDouble.ZERO_DBL;

	private Integer forcestatus;

	private UFDate forcestart;

	private UFDate forceend;

	private String bankrelated_code;

	private String checkdirection;

	private String checkno;

	private String checktype;

	private String dstlsubcs;

	private String facard;

	private UFBoolean isverifyfinished;

	private String itemstyle;

	private UFDate paydate;

	private Integer payflag;
	private String payman;
	private String pk_balatype;
	private String pk_bankreceive;
	private String pk_inventory;
	private String produceorder;
	private String projectelement;
	private String seqnum;
	private String supplier;
	private String customer;
	private UFDate transferdate;
	private Integer transferflag;
	private UFDate verifyfinisheddate;
	private Integer commpaytype;
	private Integer commpaystatus;
	private String confernum;
	private String costcenter;
	private String rececountryid;
	private String sendcountryid;
	private Integer buysellflag;
	private UFBoolean triatradeflag;
	private String vatcode;
	private String custvatcode;
	private String taxcodeid;
	private UFDouble caltaxmny = UFDouble.ZERO_DBL;

	private UFDouble nosubtaxrate = UFDouble.ZERO_DBL;

	private UFDouble nosubtax = UFDouble.ZERO_DBL;

	private UFBoolean opptaxflag;

	private String vendorvatcode;

	private String material_src;
	private String matcustcode;
	private UFDouble settlemoney = null;
	private String settlecurr;
	private UFBoolean isdiscount;
	private String batchcode;
	private String pk_batchcode;
	private String pk_recpaytype;
	private String comment;

	public String getPk_recpaytype() {
		return this.pk_recpaytype;
	}

	public void setPk_recpaytype(String pkRecpaytype) {
		this.pk_recpaytype = pkRecpaytype;
	}

	public Object getAttributeValue(String name) {
		if (StringUtils.isEmpty(name)) {
			Logger.error(name + " 空字段", getClass(), "getAttributeValue");
			return null;
		}
		Object value = null;
		if (name.startsWith("fb.")) {
			value = this.settleBody == null ? null : this.settleBody
					.getAttributeValue(name.split("\\.")[1]);
		} else {
			value = BeanHelper.getProperty(this, name);
			if (value == null) {
				value = super.getAttributeValue(name);
			}
		}
		if (value == null) {
			if (getMoneyPartFields().contains(name)) {
				value = UFDouble.ZERO_DBL;
			} else if ((!getRateFields().contains(name))
					&& (name.equals("insurance")))
				return getInsurance();
			if (name.equals("prepay")) {
				return getPrepay();
			}
		}
		return value;
	}

	public void setAttributeValue(String name, Object value) {
		if (value == null) {
			if (getMoneyPartFields().contains(name)) {
				value = UFDouble.ZERO_DBL;
			} else {
				super.setAttributeValue(name, value);
				return;
			}
		}
		Class<? extends Object> clazz = value.getClass();
		if ((clazz.equals(Object.class))
				|| (value.getClass().equals(BigDecimal.class))) {
			super.setAttributeValue(name, value);
		} else
			setAttributeValue2(name, value);
	}

	public void setAttributeValue2(String name, Object value) {
		try {
			if ((value == null) && (getMoneyPartFields().contains(name))) {
				value = UFDouble.ZERO_DBL;
			}

			if (!BeanHelper.setProperty(this, name, value)) {
				super.setAttributeValue(name, value);
			}
		} catch (IllegalArgumentException e) {
			super.setAttributeValue(name, value);
		} catch (ClassCastException e) {
			super.setAttributeValue(name, value);
		}
	}

	public SuperVO getSettleBody() {
		return this.settleBody;
	}

	public void setSettleBody(SuperVO settleBody) {
		this.settleBody = settleBody;
	}

	public BaseItemVO() {
	}

	public Object clone() {
		Object obj = null;
		try {
			obj = super.clone();
		} catch (Exception e) {
			throw new RuntimeException("clone not supported!");
		}
		BaseItemVO children = (BaseItemVO) obj;
		return children;
	}

	public TermVO[] getTerm() {
		return this.term;
	}

	public void setTerm(TermVO[] term) {
		this.term = term;
	}

	public Integer getPrepay() {
		return this.prepay;
	}

	public void setPrepay(Integer newPrepay) {
		if (newPrepay == null) {
			this.prepay = Integer.valueOf(0);
		} else {
			this.prepay = newPrepay;
		}
	}

	public String getCheckdirection() {
		return this.checkdirection;
	}

	public String getCheckno() {
		return this.checkno;
	}

	public String getChecktype() {
		return this.checktype;
	}

	public String getDstlsubcs() {
		return this.dstlsubcs;
	}

	public String getFacard() {
		return this.facard;
	}

	public UFBoolean getIsverifyfinished() {
		return this.isverifyfinished;
	}

	public String getItemstyle() {
		return this.itemstyle;
	}

	public UFDouble getLocal_money_cr() {
		return this.local_money_cr;
	}

	public UFDouble getMoney_cr() {
		return this.money_cr;
	}

	public UFDouble getOccupationmny() {
		return this.occupationmny;
	}

	public UFDate getPaydate() {
		return this.paydate;
	}

	public Integer getPayflag() {
		return this.payflag;
	}

	public String getPayman() {
		return this.payman;
	}

	public String getPk_balatype() {
		return this.pk_balatype;
	}

	public String getPk_bankreceive() {
		return this.pk_bankreceive;
	}

	public String getPk_inventory() {
		return this.pk_inventory;
	}

	public String getProduceorder() {
		return this.produceorder;
	}

	public String getProjectelement() {
		return this.projectelement;
	}

	public UFDouble getQuantity_cr() {
		return this.quantity_cr;
	}

	public String getSeqnum() {
		return this.seqnum;
	}

	public String getSupplier() {
		return this.supplier;
	}

	public UFDate getTransferdate() {
		return this.transferdate;
	}

	public Integer getTransferflag() {
		return this.transferflag;
	}

	public UFDate getVerifyfinisheddate() {
		return this.verifyfinisheddate;
	}

	public void setCheckdirection(String checkdirection) {
		this.checkdirection = checkdirection;
	}

	public void setCheckno(String checkno) {
		this.checkno = checkno;
	}

	public void setChecktype(String checktype) {
		this.checktype = checktype;
	}

	public void setDstlsubcs(String dstlsubcs) {
		this.dstlsubcs = dstlsubcs;
	}

	public void setFacard(String facard) {
		this.facard = facard;
	}

	public void setIsverifyfinished(UFBoolean isverifyfinished) {
		this.isverifyfinished = isverifyfinished;
	}

	public void setItemstyle(String itemstyle) {
		this.itemstyle = itemstyle;
	}

	public void setLocal_money_cr(UFDouble local_money_cr) {
		this.local_money_cr = local_money_cr;
	}

	public void setMoney_cr(UFDouble money_cr) {
		this.money_cr = money_cr;
	}

	public void setOccupationmny(UFDouble occupationmny) {
		this.occupationmny = occupationmny;
	}

	public void setPaydate(UFDate paydate) {
		this.paydate = paydate;
	}

	public void setPayflag(Integer payflag) {
		this.payflag = payflag;
	}

	public void setPayman(String payman) {
		this.payman = payman;
	}

	public void setPk_balatype(String pk_balatype) {
		this.pk_balatype = pk_balatype;
	}

	public void setPk_bankreceive(String pk_bankreceive) {
		this.pk_bankreceive = pk_bankreceive;
	}

	public void setPk_inventory(String pk_inventory) {
		this.pk_inventory = pk_inventory;
	}

	public void setProduceorder(String produceorder) {
		this.produceorder = produceorder;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getProject_task() {
		return this.project_task;
	}

	public void setProject_task(String projectTask) {
		this.project_task = projectTask;
	}

	public void setProjectelement(String projectelement) {
		this.projectelement = projectelement;
	}

	public void setQuantity_cr(UFDouble quantity_cr) {
		this.quantity_cr = quantity_cr;
	}

	public void setSeqnum(String seqnum) {
		this.seqnum = seqnum;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public void setTransferdate(UFDate transferdate) {
		this.transferdate = transferdate;
	}

	public void setTransferflag(Integer transferflag) {
		this.transferflag = transferflag;
	}

	public void setVerifyfinisheddate(UFDate verifyfinisheddate) {
		this.verifyfinisheddate = verifyfinisheddate;
	}

	public UFDouble getLocal_tax_cr() {
		return this.local_tax_cr;
	}

	public void setLocal_tax_cr(UFDouble local_tax_cr) {
		this.local_tax_cr = local_tax_cr;
	}

	public UFDouble getNotax_cr() {
		return this.notax_cr;
	}

	public void setNotax_cr(UFDouble notax_cr) {
		this.notax_cr = notax_cr;
	}

	public UFDouble getLocal_notax_cr() {
		return this.local_notax_cr;
	}

	public void setLocal_notax_cr(UFDouble local_notax_cr) {
		this.local_notax_cr = local_notax_cr;
	}

	public String getCustomer() {
		return this.customer;
	}

	public UFDouble getLocal_money_de() {
		return this.local_money_de;
	}

	public UFDouble getLocal_notax_de() {
		return this.local_notax_de;
	}

	public UFDouble getLocal_tax_de() {
		return this.local_tax_de;
	}

	public UFDouble getMoney_de() {
		return this.money_de;
	}

	public UFDouble getNotax_de() {
		return this.notax_de;
	}

	public UFDouble getQuantity_de() {
		return this.quantity_de;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public void setLocal_money_de(UFDouble local_money_de) {
		this.local_money_de = local_money_de;
	}

	public void setLocal_notax_de(UFDouble local_notax_de) {
		this.local_notax_de = local_notax_de;
	}

	public void setLocal_tax_de(UFDouble local_tax_de) {
		this.local_tax_de = local_tax_de;
	}

	public void setMoney_de(UFDouble money_de) {
		this.money_de = money_de;
	}

	public void setNotax_de(UFDouble notax_de) {
		this.notax_de = notax_de;
	}

	public void setQuantity_de(UFDouble quantity_de) {
		this.quantity_de = quantity_de;
	}

	public void setCoordflag(Integer coordflag) {
		this.coordflag = coordflag;
	}

	public void setRowtype(Integer rowtype) {
		this.rowtype = rowtype;
	}

	public UFBoolean getIspaytermmodified() {
		return this.ispaytermmodified;
	}

	public UFBoolean getIsrefused() {
		return this.isrefused;
	}

	public void setIsrefused(UFBoolean isrefused) {
		this.isrefused = isrefused;
	}

	public String getRefuse_reason() {
		return this.refuse_reason;
	}

	public void setRefuse_reason(String refuseReason) {
		this.refuse_reason = refuseReason;
	}

	public String getPk_rescenter() {
		return this.pk_rescenter;
	}

	public void setIspaytermmodified(UFBoolean ispaytermmodified) {
		this.ispaytermmodified = ispaytermmodified;
	}

	public void setPk_rescenter(String pk_rescenter) {
		this.pk_rescenter = pk_rescenter;
	}

	public String getPk_corp() {
		return this.pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPk_fiorg() {
		return this.pk_fiorg;
	}

	public void setPk_fiorg(String pk_fiorg) {
		this.pk_fiorg = pk_fiorg;
	}

	public String getPk_pcorg() {
		return this.pk_pcorg;
	}

	public void setPk_pcorg(String pk_pcorg) {
		this.pk_pcorg = pk_pcorg;
	}

	public String getPk_pcorg_v() {
		return this.pk_pcorg_v;
	}

	public void setPk_pcorg_v(String pk_pcorg_v) {
		this.pk_pcorg_v = pk_pcorg_v;
	}

	public String getPk_org_v() {
		return this.pk_org_v;
	}

	public void setPk_org_v(String pk_org_v) {
		this.pk_org_v = pk_org_v;
	}

	public String getPk_fiorg_v() {
		return this.pk_fiorg_v;
	}

	public void setPk_fiorg_v(String pk_fiorg_v) {
		this.pk_fiorg_v = pk_fiorg_v;
	}

	public String getSo_ordertype() {
		return this.so_ordertype;
	}

	public void setSo_ordertype(String so_ordertype) {
		this.so_ordertype = so_ordertype;
	}

	public String getSo_transtype() {
		return this.so_transtype;
	}

	public void setSo_transtype(String so_transtype) {
		this.so_transtype = so_transtype;
	}

	public String getSo_org() {
		return this.so_org;
	}

	public void setSo_org(String so_org) {
		this.so_org = so_org;
	}

	public String getPu_org() {
		return this.pu_org;
	}

	public void setPu_org(String pu_org) {
		this.pu_org = pu_org;
	}

	public String getPu_psndoc() {
		return this.pu_psndoc;
	}

	public void setPu_psndoc(String pu_psndoc) {
		this.pu_psndoc = pu_psndoc;
	}

	public String getPu_deptid() {
		return this.pu_deptid;
	}

	public void setPu_deptid(String pu_deptid) {
		this.pu_deptid = pu_deptid;
	}

	public String getSo_psndoc() {
		return this.so_psndoc;
	}

	public void setSo_psndoc(String so_psndoc) {
		this.so_psndoc = so_psndoc;
	}

	public String getSo_deptid() {
		return this.so_deptid;
	}

	public void setSo_deptid(String so_deptid) {
		this.so_deptid = so_deptid;
	}

	public String getMaterial() {
		return this.material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getPostunit() {
		return this.postunit;
	}

	public void setPostunit(String postunit) {
		this.postunit = postunit;
	}

	public UFDouble getPostpricenotax() {
		return this.postpricenotax;
	}

	public void setPostpricenotax(UFDouble postpricenotax) {
		this.postpricenotax = postpricenotax;
	}

	public UFDouble getPostquantity() {
		return this.postquantity;
	}

	public void setPostquantity(UFDouble postquantity) {
		this.postquantity = postquantity;
	}

	public UFDouble getPostprice() {
		return this.postprice;
	}

	public void setPostprice(UFDouble postprice) {
		this.postprice = postprice;
	}

	public String getProject() {
		return this.project;
	}

	public Integer getCoordflag() {
		return this.coordflag;
	}

	public String getDealno() {
		return this.dealno;
	}

	public void setDealno(String dealno) {
		this.dealno = dealno;
	}

	public String getEquipmentcode() {
		return this.equipmentcode;
	}

	public void setEquipmentcode(String equipmentcode) {
		this.equipmentcode = equipmentcode;
	}

	public String getProductline() {
		return this.productline;
	}

	public void setProductline(String productline) {
		this.productline = productline;
	}

	public String getCashitem() {
		return this.cashitem;
	}

	public void setCashitem(String cashitem) {
		this.cashitem = cashitem;
	}

	public String getBankrollprojet() {
		return this.bankrollprojet;
	}

	public void setBankrollprojet(String bankrollprojet) {
		this.bankrollprojet = bankrollprojet;
	}

	public UFBoolean getPausetransact() {
		return this.pausetransact;
	}

	public void setPausetransact(UFBoolean pausetransact) {
		this.pausetransact = pausetransact;
	}

	public UFDate getBilldate() {
		return this.billdate;
	}

	public void setBilldate(UFDate billdate) {
		this.billdate = billdate;
	}

	public String getPk_group() {
		return this.pk_group;
	}

	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	public String getPk_org() {
		return this.pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public String getPk_billtype() {
		return this.pk_billtype;
	}

	public void setPk_billtype(String pk_billtype) {
		this.pk_billtype = pk_billtype;
	}

	public String getBillclass() {
		return this.billclass;
	}

	public void setBillclass(String billclass) {
		this.billclass = billclass;
	}

	public String getPk_tradetype() {
		return this.pk_tradetype;
	}

	public void setPk_tradetype(String pk_tradetype) {
		this.pk_tradetype = pk_tradetype;
	}

	public String getPk_tradetypeid() {
		return this.pk_tradetypeid;
	}

	public void setPk_tradetypeid(String pk_tradetypeid) {
		this.pk_tradetypeid = pk_tradetypeid;
	}

	public UFDate getBusidate() {
		return this.busidate;
	}

	public void setBusidate(UFDate busidate) {
		this.busidate = busidate;
	}

	public String getPk_deptid_res() {
		return this.pk_deptid_res;
	}

	public void setPk_deptid_res(String pk_deptid_res) {
		this.pk_deptid_res = pk_deptid_res;
	}

	public String getPk_subjcode() {
		return this.pk_subjcode;
	}

	public void setPk_subjcode(String pk_subjcode) {
		this.pk_subjcode = pk_subjcode;
	}

	public String getBillno() {
		return this.billno;
	}

	public void setBillno(String billno) {
		this.billno = billno;
	}

	public Integer getObjtype() {
		return this.objtype;
	}

	public void setObjtype(Integer objtye) {
		this.objtype = objtye;
	}

	public Integer getRowno() {
		return this.rowno;
	}

	public void setRowno(Integer rowno) {
		this.rowno = rowno;
	}

	public Integer getRowtype() {
		return this.rowtype;
	}

	public Integer getDirection() {
		return this.direction;
	}

	public void setDirection(Integer direction) {
		this.direction = direction;
	}

	public String getPk_ssitem() {
		return this.pk_ssitem;
	}

	public void setPk_ssitem(String pk_ssitem) {
		this.pk_ssitem = pk_ssitem;
	}

	public String getScomment() {
		return this.scomment;
	}

	public void setScomment(String scomment) {
		this.scomment = scomment;
	}

	public String getSubjcode() {
		return this.subjcode;
	}

	public void setSubjcode(String subjcode) {
		this.subjcode = subjcode;
	}

	public String getPk_currtype() {
		return this.pk_currtype;
	}

	public void setPk_currtype(String pk_currtype) {
		this.pk_currtype = pk_currtype;
	}

	public UFDouble getRate() {
		return this.rate;
	}

	public void setRate(UFDouble rate) {
		this.rate = rate;
	}

	public String getPk_deptid() {
		return this.pk_deptid;
	}

	public void setPk_deptid(String pk_deptid) {
		this.pk_deptid = pk_deptid;
	}

	public String getPk_deptid_v() {
		return this.pk_deptid_v;
	}

	public void setPk_deptid_v(String pkDeptidV) {
		this.pk_deptid_v = pkDeptidV;
	}

	public String getPu_deptid_v() {
		return this.pu_deptid_v;
	}

	public void setPu_deptid_v(String puDeptidV) {
		this.pu_deptid_v = puDeptidV;
	}

	public String getSo_deptid_v() {
		return this.so_deptid_v;
	}

	public void setSo_deptid_v(String soDeptidV) {
		this.so_deptid_v = soDeptidV;
	}

	public String getPk_psndoc() {
		return this.pk_psndoc;
	}

	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	public UFDouble getMoney_bal() {
		return this.money_bal;
	}

	public void setMoney_bal(UFDouble money_bal) {
		if (money_bal != null) {
			this.money_bal = money_bal;
		}
	}

	public UFDouble getLocal_money_bal() {
		return this.local_money_bal;
	}

	public void setLocal_money_bal(UFDouble local_money_bal) {
		this.local_money_bal = local_money_bal;
	}

	public UFDouble getQuantity_bal() {
		return this.quantity_bal;
	}

	public void setQuantity_bal(UFDouble quantity_bal) {
		this.quantity_bal = quantity_bal;
	}

	public UFDouble getPrice() {
		return this.price;
	}

	public void setPrice(UFDouble price) {
		this.price = price;
	}

	public UFDouble getTaxprice() {
		return this.taxprice;
	}

	public void setTaxprice(UFDouble taxprice) {
		this.taxprice = taxprice;
	}

	public UFDouble getTaxrate() {
		return this.taxrate;
	}

	public void setTaxrate(UFDouble taxrate) {
		this.taxrate = taxrate;
	}

	public String getTaxnum() {
		return this.taxnum;
	}

	public void setTaxnum(String taxnum) {
		this.taxnum = taxnum;
	}

	public String getTop_billid() {
		return this.top_billid;
	}

	public void setTop_billid(String top_billid) {
		this.top_billid = top_billid;
	}

	public String getTop_itemid() {
		return this.top_itemid;
	}

	public void setTop_itemid(String top_itemid) {
		this.top_itemid = top_itemid;
	}

	public String getTop_termch() {
		return this.top_termch;
	}

	public void setTop_termch(String top_termch) {
		this.top_termch = top_termch;
	}

	public String getTop_billtype() {
		return this.top_billtype;
	}

	public void setTop_billtype(String top_billtype) {
		this.top_billtype = top_billtype;
	}

	public String getTop_tradetype() {
		return this.top_tradetype;
	}

	public void setTop_tradetype(String top_tradetype) {
		this.top_tradetype = top_tradetype;
	}

	public String getSrc_tradetype() {
		return this.src_tradetype;
	}

	public void setSrc_tradetype(String src_tradetype) {
		this.src_tradetype = src_tradetype;
	}

	public String getSrc_billtype() {
		return this.src_billtype;
	}

	public void setSrc_billtype(String src_billtype) {
		this.src_billtype = src_billtype;
	}

	public String getSrc_billid() {
		return this.src_billid;
	}

	public void setSrc_billid(String src_billid) {
		this.src_billid = src_billid;
	}

	public String getSrc_itemid() {
		return this.src_itemid;
	}

	public void setSrc_itemid(String src_itemid) {
		this.src_itemid = src_itemid;
	}

	public Integer getTaxtype() {
		return this.taxtype;
	}

	public void setTaxtype(Integer taxtype) {
		this.taxtype = taxtype;
	}

	public String getPk_payterm() {
		return this.pk_payterm;
	}

	public void setPk_payterm(String pk_payterm) {
		this.pk_payterm = pk_payterm;
	}

	public String getPayaccount() {
		return this.payaccount;
	}

	public void setPayaccount(String payaccount) {
		this.payaccount = payaccount;
	}

	public String getRecaccount() {
		return this.recaccount;
	}

	public void setRecaccount(String recaccount) {
		this.recaccount = recaccount;
	}

	public String getCashaccount() {
		return this.cashaccount;
	}

	public void setCashaccount(String cashaccount) {
		this.cashaccount = cashaccount;
	}

	public String getOrdercubasdoc() {
		return this.ordercubasdoc;
	}

	public void setOrdercubasdoc(String ordercubasdoc) {
		this.ordercubasdoc = ordercubasdoc;
	}

	public String getInnerorderno() {
		return this.innerorderno;
	}

	public void setInnerorderno(String innerorderno) {
		this.innerorderno = innerorderno;
	}

	public String getAssetpactno() {
		return this.assetpactno;
	}

	public void setAssetpactno(String assetpactno) {
		this.assetpactno = assetpactno;
	}

	public String getContractno() {
		return this.contractno;
	}

	public void setContractno(String contractno) {
		this.contractno = contractno;
	}

	public String getFreecust() {
		return this.freecust;
	}

	public void setFreecust(String freecust) {
		this.freecust = freecust;
	}

	public String getPurchaseorder() {
		return this.purchaseorder;
	}

	public void setPurchaseorder(String purchaseorder) {
		this.purchaseorder = purchaseorder;
	}

	public String getInvoiceno() {
		return this.invoiceno;
	}

	public void setInvoiceno(String invoiceno) {
		this.invoiceno = invoiceno;
	}

	public String getOutstoreno() {
		return this.outstoreno;
	}

	public void setOutstoreno(String outstoreno) {
		this.outstoreno = outstoreno;
	}

	public String getPk_jobphase() {
		return this.pk_jobphase;
	}

	public void setPk_jobphase(String pk_jobphase) {
		this.pk_jobphase = pk_jobphase;
	}

	public String getPk_job() {
		return this.pk_job;
	}

	public void setPk_job(String pk_job) {
		this.pk_job = pk_job;
	}

	public String getDef30() {
		return this.def30;
	}

	public void setDef30(String def30) {
		this.def30 = def30;
	}

	public String getDef29() {
		return this.def29;
	}

	public void setDef29(String def29) {
		this.def29 = def29;
	}

	public String getDef28() {
		return this.def28;
	}

	public void setDef28(String def28) {
		this.def28 = def28;
	}

	public String getDef27() {
		return this.def27;
	}

	public void setDef27(String def27) {
		this.def27 = def27;
	}

	public String getDef26() {
		return this.def26;
	}

	public void setDef26(String def26) {
		this.def26 = def26;
	}

	public String getDef25() {
		return this.def25;
	}

	public void setDef25(String def25) {
		this.def25 = def25;
	}

	public String getDef24() {
		return this.def24;
	}

	public void setDef24(String def24) {
		this.def24 = def24;
	}

	public String getDef23() {
		return this.def23;
	}

	public void setDef23(String def23) {
		this.def23 = def23;
	}

	public String getDef22() {
		return this.def22;
	}

	public void setDef22(String def22) {
		this.def22 = def22;
	}

	public String getDef21() {
		return this.def21;
	}

	public void setDef21(String def21) {
		this.def21 = def21;
	}

	public String getDef20() {
		return this.def20;
	}

	public void setDef20(String def20) {
		this.def20 = def20;
	}

	public String getDef19() {
		return this.def19;
	}

	public void setDef19(String def19) {
		this.def19 = def19;
	}

	public String getDef18() {
		return this.def18;
	}

	public void setDef18(String def18) {
		this.def18 = def18;
	}

	public String getDef17() {
		return this.def17;
	}

	public void setDef17(String def17) {
		this.def17 = def17;
	}

	public String getDef16() {
		return this.def16;
	}

	public void setDef16(String def16) {
		this.def16 = def16;
	}

	public String getDef15() {
		return this.def15;
	}

	public void setDef15(String def15) {
		this.def15 = def15;
	}

	public String getDef14() {
		return this.def14;
	}

	public void setDef14(String def14) {
		this.def14 = def14;
	}

	public String getDef13() {
		return this.def13;
	}

	public void setDef13(String def13) {
		this.def13 = def13;
	}

	public String getDef12() {
		return this.def12;
	}

	public void setDef12(String def12) {
		this.def12 = def12;
	}

	public String getDef11() {
		return this.def11;
	}

	public void setDef11(String def11) {
		this.def11 = def11;
	}

	public String getDef10() {
		return this.def10;
	}

	public void setDef10(String def10) {
		this.def10 = def10;
	}

	public String getDef9() {
		return this.def9;
	}

	public void setDef9(String def9) {
		this.def9 = def9;
	}

	public String getDef8() {
		return this.def8;
	}

	public void setDef8(String def8) {
		this.def8 = def8;
	}

	public String getDef7() {
		return this.def7;
	}

	public void setDef7(String def7) {
		this.def7 = def7;
	}

	public String getDef6() {
		return this.def6;
	}

	public void setDef6(String def6) {
		this.def6 = def6;
	}

	public String getDef5() {
		return this.def5;
	}

	public void setDef5(String def5) {
		this.def5 = def5;
	}

	public String getDef4() {
		return this.def4;
	}

	public void setDef4(String def4) {
		this.def4 = def4;
	}

	public String getDef3() {
		return this.def3;
	}

	public void setDef3(String def3) {
		this.def3 = def3;
	}

	public String getDef2() {
		return this.def2;
	}

	public void setDef2(String def2) {
		this.def2 = def2;
	}

	public String getDef1() {
		return this.def1;
	}

	public void setDef1(String def1) {
		this.def1 = def1;
	}

	public Integer getDr() {
		return this.dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public UFDateTime getTs() {
		return this.ts;
	}

	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	public UFDateTime getTop_changets() {
		return this.top_changets;
	}

	public void setTop_changets(UFDateTime topChangets) {
		this.top_changets = topChangets;
	}

	public String getCheckelement() {
		return this.checkelement;
	}

	public void setCheckelement(String checkelement) {
		this.checkelement = checkelement;
	}

	public UFDouble getGrouprate() {
		return this.grouprate;
	}

	public void setGrouprate(UFDouble grouprate) {
		this.grouprate = grouprate;
	}

	public UFDouble getGlobalrate() {
		return this.globalrate;
	}

	public void setGlobalrate(UFDouble globalrate) {
		this.globalrate = globalrate;
	}

	public UFDouble getGroupdebit() {
		return this.groupdebit;
	}

	public void setGroupdebit(UFDouble groupdebit) {
		this.groupdebit = groupdebit;
	}

	public UFDouble getGlobaldebit() {
		return this.globaldebit;
	}

	public void setGlobaldebit(UFDouble globaldebit) {
		if (globaldebit != null) {
			this.globaldebit = globaldebit;
		}
	}

	public UFDouble getGroupbalance() {
		return this.groupbalance;
	}

	public void setGroupbalance(UFDouble groupbalance) {
		this.groupbalance = groupbalance;
	}

	public UFDouble getGlobalbalance() {
		return this.globalbalance;
	}

	public void setGlobalbalance(UFDouble globalbalance) {
		this.globalbalance = globalbalance;
	}

	public UFDouble getGrouptax_de() {
		return this.grouptax_de;
	}

	public void setGrouptax_de(UFDouble grouptax_de) {
		this.grouptax_de = grouptax_de;
	}

	public UFDouble getGlobaltax_de() {
		return this.globaltax_de;
	}

	public void setGlobaltax_de(UFDouble globaltax_de) {
		this.globaltax_de = globaltax_de;
	}

	public UFDouble getGroupnotax_de() {
		return this.groupnotax_de;
	}

	public void setGroupnotax_de(UFDouble groupnotax_de) {
		this.groupnotax_de = groupnotax_de;
	}

	public UFDouble getGlobalnotax_de() {
		return this.globalnotax_de;
	}

	public void setGlobalnotax_de(UFDouble globalnotax_de) {
		this.globalnotax_de = globalnotax_de;
	}

	public UFDouble getGrouptax_cre() {
		return this.grouptax_cre;
	}

	public void setGrouptax_cre(UFDouble grouptax_cre) {
		this.grouptax_cre = grouptax_cre;
	}

	public UFDouble getGlobaltax_cre() {
		return this.globaltax_cre;
	}

	public void setGlobaltax_cre(UFDouble globaltax_cre) {
		this.globaltax_cre = globaltax_cre;
	}

	public UFDouble getGroupnotax_cre() {
		return this.groupnotax_cre;
	}

	public void setGroupnotax_cre(UFDouble groupnotax_cre) {
		this.groupnotax_cre = groupnotax_cre;
	}

	public UFDouble getGlobalnotax_cre() {
		return this.globalnotax_cre;
	}

	public void setGlobalnotax_cre(UFDouble globalnotax_cre) {
		this.globalnotax_cre = globalnotax_cre;
	}

	public String getSo_org_v() {
		return this.so_org_v;
	}

	public void setSo_org_v(String so_org_v) {
		this.so_org_v = so_org_v;
	}

	public String getPu_org_v() {
		return this.pu_org_v;
	}

	public String getSett_org() {
		return this.sett_org;
	}

	public void setSett_org(String sett_org) {
		this.sett_org = sett_org;
	}

	public String getSett_org_v() {
		return this.sett_org_v;
	}

	public void setSett_org_v(String sett_org_v) {
		this.sett_org_v = sett_org_v;
	}

	public String getPk_billfiorg() {
		return this.pk_billfiorg;
	}

	public void setPk_billfiorg(String pk_billfiorg) {
		this.pk_billfiorg = pk_billfiorg;
	}

	public UFDouble getForcemoney() {
		return this.forcemoney;
	}

	public void setForcemoney(UFDouble forcemoney) {
		this.forcemoney = forcemoney;
	}

	public Integer getForcestatus() {
		return this.forcestatus;
	}

	public void setForcestatus(Integer forcestatus) {
		this.forcestatus = forcestatus;
	}

	public UFDate getForcestart() {
		return this.forcestart;
	}

	public void setForcestart(UFDate forcestart) {
		this.forcestart = forcestart;
	}

	public UFDate getForceend() {
		return this.forceend;
	}

	public void setForceend(UFDate forceend) {
		this.forceend = forceend;
	}

	public void setPu_org_v(String pu_org_v) {
		this.pu_org_v = pu_org_v;
	}

	public UFDouble getGroupcrebit() {
		return this.groupcrebit;
	}

	public void setGroupcrebit(UFDouble groupcrebit) {
		this.groupcrebit = groupcrebit;
	}

	public UFDouble getGlobalcrebit() {
		return this.globalcrebit;
	}

	public void setGlobalcrebit(UFDouble globalcrebit) {
		if (globalcrebit != null) {
			this.globalcrebit = globalcrebit;
		}
	}

	public void setBankrelated_code(String bankrelated_code) {
		this.bankrelated_code = bankrelated_code;
	}

	public String getBankrelated_code() {
		return this.bankrelated_code;
	}

	public String getMaterial_src() {
		return this.material_src;
	}

	public void setMaterial_src(String materialSrc) {
		this.material_src = materialSrc;
	}

	public String getMatcustcode() {
		return this.matcustcode;
	}

	public void setMatcustcode(String matcustcode) {
		this.matcustcode = matcustcode;
	}

	public UFDouble getSettlemoney() {
		return this.settlemoney;
	}

	public void setSettlemoney(UFDouble settlemoney) {
		this.settlemoney = settlemoney;
	}

	public String getSettlecurr() {
		return this.settlecurr;
	}

	public void setSettlecurr(String settlecurr) {
		this.settlecurr = settlecurr;
	}

	public UFBoolean getIsdiscount() {
		return this.isdiscount;
	}

	public void setIsdiscount(UFBoolean isdiscount) {
		this.isdiscount = isdiscount;
	}

	public void resetDefaultValue(String operator, UFDateTime operationtime) {
		setBilldate(operationtime.getDate());
		if (getBusidate() == null) {
			setBusidate(operationtime.getDate());
		}

		setIsverifyfinished(UFBoolean.FALSE);
		setVerifyfinisheddate(new UFDate("3000-01-01"));
		setParentPK(null);
		setPrimaryKey(null);
		setBillno(null);

		String[] defaultNullFields = getDefaultNullFields();
		for (String field : defaultNullFields) {
			setAttributeValue(field, null);
		}
	}

	public void recalculateByShl(UFDouble newQuantity,
			Map<String, UFDouble[]> notaxBalMap) throws BusinessException {
	}

	private String[] getDefaultNullFields() {
		return new String[] { "payman", "paydate", "billno" };
	}

	public Integer getCommpaytype() {
		return this.commpaytype;
	}

	public void setCommpaytype(Integer commpaytype) {
		this.commpaytype = commpaytype;
	}

	public Integer getCommpaystatus() {
		return this.commpaystatus;
	}

	public void setCommpaystatus(Integer commpaystatus) {
		this.commpaystatus = commpaystatus;
	}

	public UFDouble getAgentreceiveprimal() {
		return this.agentreceiveprimal;
	}

	public void setAgentreceiveprimal(UFDouble agentreceiveprimal) {
		this.agentreceiveprimal = agentreceiveprimal;
	}

	public UFDouble getAgentreceivelocal() {
		return this.agentreceivelocal;
	}

	public void setAgentreceivelocal(UFDouble agentreceivelocal) {
		this.agentreceivelocal = agentreceivelocal;
	}

	public UFDouble getGroupagentreceivelocal() {
		return this.groupagentreceivelocal;
	}

	public void setGroupagentreceivelocal(UFDouble groupagentreceivelocal) {
		this.groupagentreceivelocal = groupagentreceivelocal;
	}

	public UFDouble getGlobalagentreceivelocal() {
		return this.globalagentreceivelocal;
	}

	public void setGlobalagentreceivelocal(UFDouble globalagentreceivelocal) {
		this.globalagentreceivelocal = globalagentreceivelocal;
	}

	public String getCommpayer() {
		return this.commpayer;
	}

	public void setCommpayer(String commpayer) {
		this.commpayer = commpayer;
	}

	public UFDouble getLocal_price() {
		return this.local_price;
	}

	public void setLocal_price(UFDouble localPrice) {
		this.local_price = localPrice;
	}

	public UFDouble getLocal_taxprice() {
		return this.local_taxprice;
	}

	public void setLocal_taxprice(UFDouble localTaxprice) {
		this.local_taxprice = localTaxprice;
	}

	public String getConfernum() {
		return this.confernum;
	}

	public void setConfernum(String confernum) {
		this.confernum = confernum;
	}

	public String getCostcenter() {
		return this.costcenter;
	}

	public void setCostcenter(String costcenter) {
		this.costcenter = costcenter;
	}

	public String getRececountryid() {
		return this.rececountryid;
	}

	public void setRececountryid(String rececountryid) {
		this.rececountryid = rececountryid;
	}

	public String getSendcountryid() {
		return this.sendcountryid;
	}

	public void setSendcountryid(String sendcountryid) {
		this.sendcountryid = sendcountryid;
	}

	public Integer getBuysellflag() {
		return this.buysellflag;
	}

	public void setBuysellflag(Integer buysellflag) {
		this.buysellflag = buysellflag;
	}

	public UFBoolean getTriatradeflag() {
		return this.triatradeflag;
	}

	public void setTriatradeflag(UFBoolean triatradeflag) {
		this.triatradeflag = triatradeflag;
	}

	public String getVatcode() {
		return this.vatcode;
	}

	public void setVatcode(String vatcode) {
		this.vatcode = vatcode;
	}

	public String getCustvatcode() {
		return this.custvatcode;
	}

	public void setCustvatcode(String custvatcode) {
		this.custvatcode = custvatcode;
	}

	public String getTaxcodeid() {
		return this.taxcodeid;
	}

	public void setTaxcodeid(String taxcodeid) {
		this.taxcodeid = taxcodeid;
	}

	public UFDouble getCaltaxmny() {
		return this.caltaxmny;
	}

	public void setCaltaxmny(UFDouble caltaxmny) {
		this.caltaxmny = caltaxmny;
	}

	public UFDouble getNosubtaxrate() {
		return this.nosubtaxrate;
	}

	public void setNosubtaxrate(UFDouble nosubtaxrate) {
		this.nosubtaxrate = nosubtaxrate;
	}

	public UFDouble getNosubtax() {
		return this.nosubtax;
	}

	public void setNosubtax(UFDouble nosubtax) {
		this.nosubtax = nosubtax;
	}

	public UFBoolean getOpptaxflag() {
		return this.opptaxflag;
	}

	public void setOpptaxflag(UFBoolean opptaxflag) {
		this.opptaxflag = opptaxflag;
	}

	public String getVendorvatcode() {
		return this.vendorvatcode;
	}

	public void setVendorvatcode(String vendorvatcode) {
		this.vendorvatcode = vendorvatcode;
	}

	public String getBatchcode() {
		return this.batchcode;
	}

	public void setBatchcode(String batchcode) {
		this.batchcode = batchcode;
	}

	public String getPk_batchcode() {
		return this.pk_batchcode;
	}

	public void setPk_batchcode(String pkBatchcode) {
		this.pk_batchcode = pkBatchcode;
	}

	public String getComment() {
		return this.scomment;
	}

	public String getDef31() {
		return this.def31;
	}

	public void setDef31(String def31) {
		this.def31 = def31;
	}

	public String getDef32() {
		return this.def32;
	}

	public void setDef32(String def32) {
		this.def32 = def32;
	}

	public String getDef33() {
		return this.def33;
	}

	public void setDef33(String def33) {
		this.def33 = def33;
	}

	public String getDef34() {
		return this.def34;
	}

	public void setDef34(String def34) {
		this.def34 = def34;
	}

	public String getDef35() {
		return this.def35;
	}

	public void setDef35(String def35) {
		this.def35 = def35;
	}

	public String getDef36() {
		return this.def36;
	}

	public void setDef36(String def36) {
		this.def36 = def36;
	}

	public String getDef37() {
		return this.def37;
	}

	public void setDef37(String def37) {
		this.def37 = def37;
	}

	public String getDef38() {
		return this.def38;
	}

	public void setDef38(String def38) {
		this.def38 = def38;
	}

	public String getDef39() {
		return this.def39;
	}

	public void setDef39(String def39) {
		this.def39 = def39;
	}

	public String getDef40() {
		return this.def40;
	}

	public void setDef40(String def40) {
		this.def40 = def40;
	}

	public String getDef41() {
		return this.def41;
	}

	public void setDef41(String def41) {
		this.def41 = def41;
	}

	public String getDef42() {
		return this.def42;
	}

	public void setDef42(String def42) {
		this.def42 = def42;
	}

	public String getDef43() {
		return this.def43;
	}

	public void setDef43(String def43) {
		this.def43 = def43;
	}

	public String getDef44() {
		return this.def44;
	}

	public void setDef44(String def44) {
		this.def44 = def44;
	}

	public String getDef45() {
		return this.def45;
	}

	public void setDef45(String def45) {
		this.def45 = def45;
	}

	public String getDef46() {
		return this.def46;
	}

	public void setDef46(String def46) {
		this.def46 = def46;
	}

	public String getDef47() {
		return this.def47;
	}

	public void setDef47(String def47) {
		this.def47 = def47;
	}

	public String getDef48() {
		return this.def48;
	}

	public void setDef48(String def48) {
		this.def48 = def48;
	}

	public String getDef49() {
		return this.def49;
	}

	public void setDef49(String def49) {
		this.def49 = def49;
	}

	public String getDef50() {
		return this.def50;
	}

	public void setDef50(String def50) {
		this.def50 = def50;
	}

	public String getDef51() {
		return this.def51;
	}

	public void setDef51(String def51) {
		this.def51 = def51;
	}

	public String getDef52() {
		return this.def52;
	}

	public void setDef52(String def52) {
		this.def52 = def52;
	}

	public String getDef53() {
		return this.def53;
	}

	public void setDef53(String def53) {
		this.def53 = def53;
	}

	public String getDef54() {
		return this.def54;
	}

	public void setDef54(String def54) {
		this.def54 = def54;
	}

	public String getDef55() {
		return this.def55;
	}

	public void setDef55(String def55) {
		this.def55 = def55;
	}

	public String getDef56() {
		return this.def56;
	}

	public void setDef56(String def56) {
		this.def56 = def56;
	}

	public String getDef57() {
		return this.def57;
	}

	public void setDef57(String def57) {
		this.def57 = def57;
	}

	public String getDef58() {
		return this.def58;
	}

	public void setDef58(String def58) {
		this.def58 = def58;
	}

	public String getDef59() {
		return this.def59;
	}

	public void setDef59(String def59) {
		this.def59 = def59;
	}

	public String getDef60() {
		return this.def60;
	}

	public void setDef60(String def60) {
		this.def60 = def60;
	}

	public String getDef61() {
		return this.def61;
	}

	public void setDef61(String def61) {
		this.def61 = def61;
	}

	public String getDef62() {
		return this.def62;
	}

	public void setDef62(String def62) {
		this.def62 = def62;
	}

	public String getDef63() {
		return this.def63;
	}

	public void setDef63(String def63) {
		this.def63 = def63;
	}

	public String getDef64() {
		return this.def64;
	}

	public void setDef64(String def64) {
		this.def64 = def64;
	}

	public String getDef65() {
		return this.def65;
	}

	public void setDef65(String def65) {
		this.def65 = def65;
	}

	public String getDef66() {
		return this.def66;
	}

	public void setDef66(String def66) {
		this.def66 = def66;
	}

	public String getDef67() {
		return this.def67;
	}

	public void setDef67(String def67) {
		this.def67 = def67;
	}

	public String getDef68() {
		return this.def68;
	}

	public void setDef68(String def68) {
		this.def68 = def68;
	}

	public String getDef69() {
		return this.def69;
	}

	public void setDef69(String def69) {
		this.def69 = def69;
	}

	public String getDef70() {
		return this.def70;
	}

	public void setDef70(String def70) {
		this.def70 = def70;
	}

	public String getDef71() {
		return this.def71;
	}

	public void setDef71(String def71) {
		this.def71 = def71;
	}

	public String getDef72() {
		return this.def72;
	}

	public void setDef72(String def72) {
		this.def72 = def72;
	}

	public String getDef73() {
		return this.def73;
	}

	public void setDef73(String def73) {
		this.def73 = def73;
	}

	public String getDef74() {
		return this.def74;
	}

	public void setDef74(String def74) {
		this.def74 = def74;
	}

	public String getDef75() {
		return this.def75;
	}

	public void setDef75(String def75) {
		this.def75 = def75;
	}

	public String getDef76() {
		return this.def76;
	}

	public void setDef76(String def76) {
		this.def76 = def76;
	}

	public String getDef77() {
		return this.def77;
	}

	public void setDef77(String def77) {
		this.def77 = def77;
	}

	public String getDef78() {
		return this.def78;
	}

	public void setDef78(String def78) {
		this.def78 = def78;
	}

	public String getDef79() {
		return this.def79;
	}

	public void setDef79(String def79) {
		this.def79 = def79;
	}

	public String getDef80() {
		return this.def80;
	}

	public void setDef80(String def80) {
		this.def80 = def80;
	}

	// XBX新增自定义项100个
	public String getDef81() {
		return this.def81;
	}

	public void setDef81(String def81) {
		this.def81 = def81;
	}

	public String getDef82() {
		return this.def82;
	}

	public void setDef82(String def82) {
		this.def82 = def82;
	}

	public String getDef83() {
		return this.def83;
	}

	public void setDef83(String def83) {
		this.def83 = def83;
	}

	public String getDef84() {
		return this.def84;
	}

	public void setDef84(String def84) {
		this.def84 = def84;
	}

	public String getDef85() {
		return this.def85;
	}

	public void setDef85(String def85) {
		this.def85 = def85;
	}

	public String getDef86() {
		return this.def86;
	}

	public void setDef86(String def86) {
		this.def86 = def86;
	}

	public String getDef87() {
		return this.def87;
	}

	public void setDef87(String def87) {
		this.def87 = def87;
	}

	public String getDef88() {
		return this.def88;
	}

	public void setDef88(String def88) {
		this.def88 = def88;
	}

	public String getDef89() {
		return this.def89;
	}

	public void setDef89(String def89) {
		this.def89 = def89;
	}

	public String getDef90() {
		return this.def90;
	}

	public void setDef90(String def90) {
		this.def90 = def90;
	}

	public String getDef91() {
		return this.def91;
	}

	public void setDef91(String def91) {
		this.def91 = def91;
	}

	public String getDef92() {
		return this.def92;
	}

	public void setDef92(String def92) {
		this.def92 = def92;
	}

	public String getDef93() {
		return this.def93;
	}

	public void setDef93(String def93) {
		this.def93 = def93;
	}

	public String getDef94() {
		return this.def94;
	}

	public void setDef94(String def94) {
		this.def94 = def94;
	}

	public String getDef95() {
		return this.def95;
	}

	public void setDef95(String def95) {
		this.def95 = def95;
	}

	public String getDef96() {
		return this.def96;
	}

	public void setDef96(String def96) {
		this.def96 = def96;
	}

	public String getDef97() {
		return this.def97;
	}

	public void setDef97(String def97) {
		this.def97 = def97;
	}

	public String getDef98() {
		return this.def98;
	}

	public void setDef98(String def98) {
		this.def98 = def98;
	}

	public String getDef99() {
		return this.def99;
	}

	public void setDef99(String def99) {
		this.def99 = def99;
	}

	public String getDef100() {
		return this.def100;
	}

	public void setDef100(String def100) {
		this.def100 = def100;
	}

	public String getDef101() {
		return this.def101;
	}

	public void setDef101(String def101) {
		this.def101 = def101;
	}

	public String getDef102() {
		return this.def102;
	}

	public void setDef102(String def102) {
		this.def102 = def102;
	}

	public String getDef103() {
		return this.def103;
	}

	public void setDef103(String def103) {
		this.def103 = def103;
	}

	public String getDef104() {
		return this.def104;
	}

	public void setDef104(String def104) {
		this.def104 = def104;
	}

	public String getDef105() {
		return this.def105;
	}

	public void setDef105(String def105) {
		this.def105 = def105;
	}

	public String getDef106() {
		return this.def106;
	}

	public void setDef106(String def106) {
		this.def106 = def106;
	}

	public String getDef107() {
		return this.def107;
	}

	public void setDef107(String def107) {
		this.def107 = def107;
	}

	public String getDef108() {
		return this.def108;
	}

	public void setDef108(String def108) {
		this.def108 = def108;
	}

	public String getDef109() {
		return this.def109;
	}

	public void setDef109(String def109) {
		this.def109 = def109;
	}

	public String getDef110() {
		return this.def110;
	}

	public void setDef110(String def110) {
		this.def110 = def110;
	}

	public String getDef111() {
		return this.def111;
	}

	public void setDef111(String def111) {
		this.def111 = def111;
	}

	public String getDef112() {
		return this.def112;
	}

	public void setDef112(String def112) {
		this.def112 = def112;
	}

	public String getDef113() {
		return this.def113;
	}

	public void setDef113(String def113) {
		this.def113 = def113;
	}

	public String getDef114() {
		return this.def114;
	}

	public void setDef114(String def114) {
		this.def114 = def114;
	}

	public String getDef115() {
		return this.def115;
	}

	public void setDef115(String def115) {
		this.def115 = def115;
	}

	public String getDef116() {
		return this.def116;
	}

	public void setDef116(String def116) {
		this.def116 = def116;
	}

	public String getDef117() {
		return this.def117;
	}

	public void setDef117(String def117) {
		this.def117 = def117;
	}

	public String getDef118() {
		return this.def118;
	}

	public void setDef118(String def118) {
		this.def118 = def118;
	}

	public String getDef119() {
		return this.def119;
	}

	public void setDef119(String def119) {
		this.def119 = def119;
	}

	public String getDef120() {
		return this.def120;
	}

	public void setDef120(String def120) {
		this.def120 = def120;
	}

	public String getDef121() {
		return this.def121;
	}

	public void setDef121(String def121) {
		this.def121 = def121;
	}

	public String getDef122() {
		return this.def122;
	}

	public void setDef122(String def122) {
		this.def122 = def122;
	}

	public String getDef123() {
		return this.def123;
	}

	public void setDef123(String def123) {
		this.def123 = def123;
	}

	public String getDef124() {
		return this.def124;
	}

	public void setDef124(String def124) {
		this.def124 = def124;
	}

	public String getDef125() {
		return this.def125;
	}

	public void setDef125(String def125) {
		this.def125 = def125;
	}

	public String getDef126() {
		return this.def126;
	}

	public void setDef126(String def126) {
		this.def126 = def126;
	}

	public String getDef127() {
		return this.def127;
	}

	public void setDef127(String def127) {
		this.def127 = def127;
	}

	public String getDef128() {
		return this.def128;
	}

	public void setDef128(String def128) {
		this.def128 = def128;
	}

	public String getDef129() {
		return this.def129;
	}

	public void setDef129(String def129) {
		this.def129 = def129;
	}

	public String getDef130() {
		return this.def130;
	}

	public void setDef130(String def130) {
		this.def130 = def130;
	}

	public String getDef131() {
		return this.def131;
	}

	public void setDef131(String def131) {
		this.def131 = def131;
	}

	public String getDef132() {
		return this.def132;
	}

	public void setDef132(String def132) {
		this.def132 = def132;
	}

	public String getDef133() {
		return this.def133;
	}

	public void setDef133(String def133) {
		this.def133 = def133;
	}

	public String getDef134() {
		return this.def134;
	}

	public void setDef134(String def134) {
		this.def134 = def134;
	}

	public String getDef135() {
		return this.def135;
	}

	public void setDef135(String def135) {
		this.def135 = def135;
	}

	public String getDef136() {
		return this.def136;
	}

	public void setDef136(String def136) {
		this.def136 = def136;
	}

	public String getDef137() {
		return this.def137;
	}

	public void setDef137(String def137) {
		this.def137 = def137;
	}

	public String getDef138() {
		return this.def138;
	}

	public void setDef138(String def138) {
		this.def138 = def138;
	}

	public String getDef139() {
		return this.def139;
	}

	public void setDef139(String def139) {
		this.def139 = def139;
	}

	public String getDef140() {
		return this.def140;
	}

	public void setDef140(String def140) {
		this.def140 = def140;
	}

	public String getDef141() {
		return this.def141;
	}

	public void setDef141(String def141) {
		this.def141 = def141;
	}

	public String getDef142() {
		return this.def142;
	}

	public void setDef142(String def142) {
		this.def142 = def142;
	}

	public String getDef143() {
		return this.def143;
	}

	public void setDef143(String def143) {
		this.def143 = def143;
	}

	public String getDef144() {
		return this.def144;
	}

	public void setDef144(String def144) {
		this.def144 = def144;
	}

	public String getDef145() {
		return this.def145;
	}

	public void setDef145(String def145) {
		this.def145 = def145;
	}

	public String getDef146() {
		return this.def146;
	}

	public void setDef146(String def146) {
		this.def146 = def146;
	}

	public String getDef147() {
		return this.def147;
	}

	public void setDef147(String def147) {
		this.def147 = def147;
	}

	public String getDef148() {
		return this.def148;
	}

	public void setDef148(String def148) {
		this.def148 = def148;
	}

	public String getDef149() {
		return this.def149;
	}

	public void setDef149(String def149) {
		this.def149 = def149;
	}

	public String getDef150() {
		return this.def150;
	}

	public void setDef150(String def150) {
		this.def150 = def150;
	}

	public String getDef151() {
		return this.def151;
	}

	public void setDef151(String def151) {
		this.def151 = def151;
	}

	public String getDef152() {
		return this.def152;
	}

	public void setDef152(String def152) {
		this.def152 = def152;
	}

	public String getDef153() {
		return this.def153;
	}

	public void setDef153(String def153) {
		this.def153 = def153;
	}

	public String getDef154() {
		return this.def154;
	}

	public void setDef154(String def154) {
		this.def154 = def154;
	}

	public String getDef155() {
		return this.def155;
	}

	public void setDef155(String def155) {
		this.def155 = def155;
	}

	public String getDef156() {
		return this.def156;
	}

	public void setDef156(String def156) {
		this.def156 = def156;
	}

	public String getDef157() {
		return this.def157;
	}

	public void setDef157(String def157) {
		this.def157 = def157;
	}

	public String getDef158() {
		return this.def158;
	}

	public void setDef158(String def158) {
		this.def158 = def158;
	}

	public String getDef159() {
		return this.def159;
	}

	public void setDef159(String def159) {
		this.def159 = def159;
	}

	public String getDef160() {
		return this.def160;
	}

	public void setDef160(String def160) {
		this.def160 = def160;
	}

	public String getDef161() {
		return this.def161;
	}

	public void setDef161(String def161) {
		this.def161 = def161;
	}

	public String getDef162() {
		return this.def162;
	}

	public void setDef162(String def162) {
		this.def162 = def162;
	}

	public String getDef163() {
		return this.def163;
	}

	public void setDef163(String def163) {
		this.def163 = def163;
	}

	public String getDef164() {
		return this.def164;
	}

	public void setDef164(String def164) {
		this.def164 = def164;
	}

	public String getDef165() {
		return this.def165;
	}

	public void setDef165(String def165) {
		this.def165 = def165;
	}

	public String getDef166() {
		return this.def166;
	}

	public void setDef166(String def166) {
		this.def166 = def166;
	}

	public String getDef167() {
		return this.def167;
	}

	public void setDef167(String def167) {
		this.def167 = def167;
	}

	public String getDef168() {
		return this.def168;
	}

	public void setDef168(String def168) {
		this.def168 = def168;
	}

	public String getDef169() {
		return this.def169;
	}

	public void setDef169(String def169) {
		this.def169 = def169;
	}

	public String getDef170() {
		return this.def170;
	}

	public void setDef170(String def170) {
		this.def170 = def170;
	}

	public String getDef171() {
		return this.def171;
	}

	public void setDef171(String def171) {
		this.def171 = def171;
	}

	public String getDef172() {
		return this.def172;
	}

	public void setDef172(String def172) {
		this.def172 = def172;
	}

	public String getDef173() {
		return this.def173;
	}

	public void setDef173(String def173) {
		this.def173 = def173;
	}

	public String getDef174() {
		return this.def174;
	}

	public void setDef174(String def174) {
		this.def174 = def174;
	}

	public String getDef175() {
		return this.def175;
	}

	public void setDef175(String def175) {
		this.def175 = def175;
	}

	public String getDef176() {
		return this.def176;
	}

	public void setDef176(String def176) {
		this.def176 = def176;
	}

	public String getDef177() {
		return this.def177;
	}

	public void setDef177(String def177) {
		this.def177 = def177;
	}

	public String getDef178() {
		return this.def178;
	}

	public void setDef178(String def178) {
		this.def178 = def178;
	}

	public String getDef179() {
		return this.def179;
	}

	public void setDef179(String def179) {
		this.def179 = def179;
	}

	public String getDef180() {
		return this.def180;
	}

	public void setDef180(String def180) {
		this.def180 = def180;
	}
}
