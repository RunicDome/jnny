package nc.vo.arap.basebill;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.pubitf.accperiod.AccountCalendar;
import nc.vo.arap.pub.BillEnumCollection;
import nc.vo.arap.pub.BillEnumCollection.ApproveStatus;
import nc.vo.arap.pub.BillEnumCollection.InureSign;
import nc.vo.bd.period.AccperiodVO;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.pub.MapList;

@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
public abstract class BaseBillVO extends SuperVO implements IArapBillFieldVO,
		IArapMoneyFieldVO {
	private static final long serialVersionUID = 1L;
	private MapList<String, String> top_itemid = new MapList();

	private Map<String, String> top_billid = new HashMap();

	private Map<String, UFDouble> top_money = new HashMap();

	public Map<String, String> getTop_billid() {
		return this.top_billid;
	}

	public void setTop_billid(Map<String, String> top_billid) {
		this.top_billid = top_billid;
	}

	public MapList<String, String> getTop_itemid() {
		return this.top_itemid;
	}

	public void setTop_itemid(MapList<String, String> top_itemid) {
		this.top_itemid = top_itemid;
	}

	public Map<String, UFDouble> getTop_money() {
		return this.top_money;
	}

	public void setTop_money(Map<String, UFDouble> top_money) {
		this.top_money = top_money;
	}

	public static final List<String> BUSIORG_FIELD_LIST = Arrays
			.asList(new String[] { "so_deptid_v", "pu_deptid_v", "so_deptid",
					"pu_deptid", "so_psndoc", "pu_psndoc" });

	public static final List<String> DEPTPSN_FIELD_LIST = Arrays
			.asList(new String[] { "pk_deptid", "pk_deptid_v", "pk_psndoc" });

	private static final String[] YFandSKMoneyFields = { "quantity_cr",
			"local_money_cr", "notax_cr", "money_cr", "money_bal",
			"local_money_bal", "quantity_bal", "local_tax_cr",
			"local_notax_cr", "grouptax_cre", "groupnotax_cre", "groupcrebit",
			"globalcrebit", "globaltax_cre", "globalnotax_cre" };

	private static final String[] YSandFKMoneyFields = { "money_de",
			"local_money_de", "quantity_de", "money_bal", "local_money_bal",
			"quantity_bal", "local_tax_de", "notax_de", "local_notax_de",
			"grouptax_de", "groupnotax_de", "groupdebit", "globaltax_de",
			"globalnotax_de", "globaldebit" };

	public static final List<String> RATEFIELD_LIST = Arrays
			.asList(new String[] { "rate", "grouprate", "globalrate" });

	public static final List<String> MONEYFIELD_LIST = Arrays
			.asList(new String[] { "money", "local_money", "grouplocal",
					"globallocal" });

	public static final List<String> OBJTPEFIELD_LIST = Arrays
			.asList(new String[] { "objtype", "supplier", "customer",
					"pk_deptid", "pk_psndoc" });

	public static final List<String> UPDATEIGNORE_LIST = Arrays
			.asList(new String[] { "modifier", "modifiedtime", "creator",
					"creationtime", "billstatus" });

	public static final List<String> ACCEFIELD_LIST = Arrays
			.asList(new String[] { "recaccount", "payaccount", "cashaccount" });

	public static final String ZB = "zb.";

	public static final String H_SUBJCODE = "h_subjcode";

	private boolean isAuthAccount = false;
	private boolean isForceEffect = false;
	private String itemflag;
	private Integer transientFlag;
	private UFBoolean temporarily2Save;
	private UFBoolean saveToTemporarily;
	private transient UFBoolean m_cooperateMoreTimes = UFBoolean.TRUE;

	private UFDouble rate = UFDouble.ONE_DBL;
	private String billmaker;
	private UFBoolean ismandatepay;
	private UFBoolean isnetpayready;
	private UFBoolean isonlinepay;
	private UFDate paydate;
	private Integer prepay;
	private Integer settleflag;
	private String settlenum;
	private Integer settletype;
	private String payman;
	private String pk_currtype;
	private String custdelegate;
	private String customer;
	private String pk_corp;
	private String pk_deptid_res;
	private UFDate signdate;
	private String signperiod;
	private String signuser;
	private String signyear;
	private String parentPK;
	private String pk_org_v;
	private String pk_fiorg_v;
	private String pk_pcorg_v;
	private String pk_fiorg;
	private UFBoolean isreded;
	private String pk_pcorg;
	private String outbusitype;
	private String officialprintuser;
	private UFDate officialprintdate;
	private String approvenote;
	private String pk_org;
	private String pk_group;
	private UFDateTime modifiedtime;
	private UFDateTime creationtime;
	private String creator;
	private String pk_billtype;
	private String custbank;
	private String modifier;
	private String pk_tradetype;
	private String pk_tradetypeid;
	private String billclass;
	private Integer accessorynum;
	private String subjcode;
	private UFBoolean isflowbill;
	private String confirmuser;
	private UFBoolean isinit = UFBoolean.FALSE;
	private UFBoolean isforce;
	private String billno;
	private UFDate billdate;
	private Integer syscode;
	private Integer src_syscode;
	private Integer billstatus;
	private Integer billstatus2;
	private String approver;
	private UFDateTime approvedate;
	private String lastadjustuser;
	private String pk_busitype;
	private UFDouble money = UFDouble.ZERO_DBL;
	private UFDouble local_money = UFDouble.ZERO_DBL;
	private String billyear;
	private String billperiod;
	private String scomment;
	private Integer effectstatus = BillEnumCollection.InureSign.NOINURE.VALUE;
	private String effectuser;
	private UFDate effectdate;
	private String lastapproveid;
	private String sett_org;
	private String def1;
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
	private Integer dr;
	private UFDateTime ts;
	private UFDate tallydate;
	private UFDouble grouplocal = UFDouble.ZERO_DBL;
	private UFDouble globallocal = UFDouble.ZERO_DBL;
	private UFDouble grouprate = UFDouble.ZERO_DBL;
	private UFDouble globalrate = UFDouble.ZERO_DBL;
	private String checkelement;
	private String so_deptid;
	private String so_psndoc;
	private String pu_deptid;
	private String pu_psndoc;
	private String so_org;
	private String pu_org;
	private String pk_rescenter;
	private String cashitem;
	private String bankrollprojet;
	private String supplier;
	private String pk_deptid;
	private String pk_deptid_v;
	private String pu_deptid_v;
	private String so_deptid_v;
	private UFDate busidate;
	private String pk_psndoc;
	private String checkno;
	private String payaccount;
	private String recaccount;
	private String incomeitem;
	private String pk_subjcode;
	private Integer objtype;
	private String so_org_v;
	private String sett_org_v;
	private String pk_input;
	private String pk_balatype;
	private String ordercubasdoc;
	private UFDouble globalnotax = UFDouble.ZERO_DBL;
	private UFDouble grouptax = UFDouble.ZERO_DBL;
	private UFDouble globaltax = UFDouble.ZERO_DBL;
	private UFDouble groupnotax = UFDouble.ZERO_DBL;

	private String pu_org_v;

	private Integer coordflag;

	private Integer estflag;

	private String cashaccount;

	private String costcenter;

	private UFBoolean isfromindependent;
	private String rececountryid;
	private String taxcountryid;
	private String sendcountryid;
	private String creditrefstd;
	private UFDate approvedated;
	private transient UFDateTime approvedate_bak;
	private String start_period;
	private Integer total_period;
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

	private String approvestatus2;

	public UFDateTime getApprovedate_bak() {
		return this.approvedate_bak;
	}

	public void setApprovedate_bak(UFDateTime approvedateBak) {
		this.approvedate_bak = approvedateBak;
	}

	public UFDate getApprovedated() {
		if (this.approvedate == null)
			return null;
		return this.approvedate.getDate();
	}

	public void setApprovedated(UFDate approvedated) {
		this.approvedated = approvedated;
	}

	public String getApprovestatus2() {
		return this.approvestatus2;
	}

	public void setApprovestatus2(String approvestatus2) {
		this.approvestatus2 = approvestatus2;
	}

	public BaseBillVO() {
	}

	public String getPrimaryKey() {
		String primaryKey = super.getPrimaryKey();

		return (String) (primaryKey == null ? getAttributeValue(getPKFieldName())
				: primaryKey);
	}

	public Object clone() {
		Object obj = null;
		try {
			obj = super.clone();
		} catch (Exception e) {
			throw new RuntimeException("clone not supported!");
		}
		BaseBillVO parent = (BaseBillVO) obj;
		return parent;
	}

	public String getBillmaker() {
		return this.billmaker;
	}

	public void setBillmaker(String billmaker) {
		this.billmaker = billmaker;
	}

	public String getItemflag() {
		return this.itemflag;
	}

	public void setItemflag(String itemflag) {
		this.itemflag = itemflag;
	}

	public UFBoolean getTemporarily2Save() {
		return this.temporarily2Save;
	}

	public void setTemporarily2Save(UFBoolean Temporarily2Save) {
		this.temporarily2Save = Temporarily2Save;
	}

	public UFBoolean getSaveToTemporarily() {
		return this.saveToTemporarily;
	}

	public void setSaveTotemporarily(UFBoolean saveTotemporarily) {
		this.saveToTemporarily = saveTotemporarily;
	}

	public UFDate getBusidate() {
		return this.busidate;
	}

	public void setBusidate(UFDate busidate) {
		this.busidate = busidate;
	}

	public UFBoolean getM_cooperateMoreTimes() {
		return this.m_cooperateMoreTimes;
	}

	public void setM_cooperateMoreTimes(UFBoolean moreTimes) {
		this.m_cooperateMoreTimes = moreTimes;
	}

	public Integer getTransientFlag() {
		return this.transientFlag;
	}

	public void setTransientFlag(Integer transientFlag) {
		this.transientFlag = transientFlag;
	}

	private Integer approvestatus = BillEnumCollection.ApproveStatus.NOSTATE.VALUE;

	public Integer getApprovestatus() {
		return this.approvestatus == null ? BillEnumCollection.ApproveStatus.NOSTATE.VALUE
				: this.approvestatus;
	}

	public void setApprovestatus(Integer approvestatus) {
		this.approvestatus = approvestatus;
	}

	public UFBoolean getIsmandatepay() {
		return this.ismandatepay;
	}

	public UFBoolean getIsnetpayready() {
		return this.isnetpayready;
	}

	public UFBoolean getIsonlinepay() {
		return this.isonlinepay;
	}

	public UFDate getPaydate() {
		return this.paydate;
	}

	public String getPayman() {
		return this.payman;
	}

	public Integer getPrepay() {
		return this.prepay;
	}

	public Integer getSettleflag() {
		return this.settleflag;
	}

	public String getSettlenum() {
		return this.settlenum;
	}

	public Integer getSettletype() {
		return this.settletype;
	}

	public void setIsmandatepay(UFBoolean ismandatepay) {
		this.ismandatepay = ismandatepay;
	}

	public void setIsnetpayready(UFBoolean isnetpayready) {
		this.isnetpayready = isnetpayready;
	}

	public void setIsonlinepay(UFBoolean isonlinepay) {
		this.isonlinepay = isonlinepay;
	}

	public void setPaydate(UFDate paydate) {
		this.paydate = paydate;
	}

	public void setPayman(String payman) {
		this.payman = payman;
	}

	public void setPrepay(Integer prepay) {
		this.prepay = prepay;
	}

	public void setSettleflag(Integer settleflag) {
		this.settleflag = settleflag;
	}

	public void setSettlenum(String settlenum) {
		this.settlenum = settlenum;
	}

	public void setSettletype(Integer settletype) {
		this.settletype = settletype;
	}

	public String getPk_currtype() {
		return this.pk_currtype;
	}

	public void setPk_currtype(String pk_currtype) {
		this.pk_currtype = pk_currtype;
	}

	public String getCustdelegate() {
		return this.custdelegate;
	}

	public String getCustomer() {
		return this.customer;
	}

	@Deprecated
	public String getPk_corp() {
		return this.pk_corp;
	}

	public String getPk_deptid_res() {
		return this.pk_deptid_res;
	}

	public UFDate getSigndate() {
		return this.signdate;
	}

	public String getSignperiod() {
		return this.signperiod;
	}

	public String getSignuser() {
		return this.signuser;
	}

	public String getSignyear() {
		return this.signyear;
	}

	public void setCustdelegate(String custdelegate) {
		this.custdelegate = custdelegate;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	@Deprecated
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setPk_deptid_res(String pk_deptid_res) {
		this.pk_deptid_res = pk_deptid_res;
	}

	public void setSigndate(UFDate signdate) {
		this.signdate = signdate;
	}

	public void setSignperiod(String signperiod) {
		this.signperiod = signperiod;
	}

	public void setSignuser(String signuser) {
		this.signuser = signuser;
	}

	public void setSignyear(String signyear) {
		this.signyear = signyear;
	}

	public String getParentPK() {
		return this.parentPK;
	}

	public void setParentPK(String parentPK) {
		this.parentPK = parentPK;
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

	public String getPk_pcorg_v() {
		return this.pk_pcorg_v;
	}

	public void setPk_pcorg_v(String pk_pcorg_v) {
		this.pk_pcorg_v = pk_pcorg_v;
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

	public UFBoolean getInsurance() {
		return null;
	}

	public void setInsurance(UFBoolean newInsurance) {
	}

	public UFBoolean getIsreded() {
		return this.isreded;
	}

	public void setIsreded(UFBoolean isreded) {
		this.isreded = isreded;
	}

	public String getOutbusitype() {
		return this.outbusitype;
	}

	public void setOutbusitype(String outbusitype) {
		this.outbusitype = outbusitype;
	}

	public String getOfficialprintuser() {
		return this.officialprintuser;
	}

	public void setOfficialprintuser(String officialprintuser) {
		this.officialprintuser = officialprintuser;
	}

	public UFDate getOfficialprintdate() {
		return this.officialprintdate;
	}

	public void setOfficialprintdate(UFDate officialprintdate) {
		this.officialprintdate = officialprintdate;
	}

	public String getApprovenote() {
		return this.approvenote;
	}

	public void setApprovenote(String approvenote) {
		this.approvenote = approvenote;
	}

	public String getPk_org() {
		return this.pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public String getPk_group() {
		return this.pk_group;
	}

	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	public UFDateTime getModifiedtime() {
		return this.modifiedtime;
	}

	public void setModifiedtime(UFDateTime modifiedtime) {
		this.modifiedtime = modifiedtime;
	}

	public UFDateTime getCreationtime() {
		return this.creationtime;
	}

	public void setCreationtime(UFDateTime creationtime) {
		this.creationtime = creationtime;
	}

	public String getCreator() {
		return this.creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getPk_billtype() {
		return this.pk_billtype;
	}

	public void setPk_billtype(String pk_billtype) {
		this.pk_billtype = pk_billtype;
	}

	public String getCustbank() {
		return this.custbank;
	}

	public void setCustbank(String custbank) {
		this.custbank = custbank;
	}

	public String getModifier() {
		return this.modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
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

	public String getBillclass() {
		return this.billclass;
	}

	public void setBillclass(String billclass) {
		this.billclass = billclass;
	}

	public Integer getAccessorynum() {
		return this.accessorynum;
	}

	public void setAccessorynum(Integer accessorynum) {
		this.accessorynum = accessorynum;
	}

	public String getSubjcode() {
		return this.subjcode;
	}

	public void setSubjcode(String subjcode) {
		this.subjcode = subjcode;
	}

	public UFBoolean getIsflowbill() {
		return this.isflowbill;
	}

	public void setIsflowbill(UFBoolean isflowbill) {
		this.isflowbill = isflowbill;
	}

	public String getConfirmuser() {
		return this.confirmuser;
	}

	public void setConfirmuser(String confirmuser) {
		this.confirmuser = confirmuser;
	}

	public UFBoolean getIsinit() {
		return this.isinit;
	}

	public void setIsinit(UFBoolean isinit) {
		this.isinit = isinit;
	}

	public String getBillno() {
		return this.billno;
	}

	public void setBillno(String billno) {
		this.billno = billno;
	}

	public UFDate getBilldate() {
		return this.billdate;
	}

	public void setBilldate(UFDate billdate) {
		this.billdate = billdate;
	}

	public Integer getSyscode() {
		return this.syscode;
	}

	public void setSyscode(Integer syscode) {
		this.syscode = syscode;
	}

	public Integer getSrc_syscode() {
		return this.src_syscode;
	}

	public void setSrc_syscode(Integer src_syscode) {
		this.src_syscode = src_syscode;
	}

	public Integer getBillstatus() {
		return this.billstatus;
	}

	public void setBillstatus(Integer billstatus) {
		this.billstatus = billstatus;
	}

	public Integer getBillstatus2() {
		if (this.billstatus2 == null) {
			return this.billstatus;
		}
		return this.billstatus2;
	}

	public void setBillstatus2(Integer billstatus2) {
		this.billstatus2 = billstatus2;
	}

	public String getApprover() {
		return this.approver;
	}

	public void setApprover(String approver) {
		setLastapproveid(approver);
		this.approver = approver;
	}

	public UFDateTime getApprovedate() {
		return this.approvedate;
	}

	public void setApprovedate(UFDateTime approvedate) {
		if ((approvedate == null) && (getApprovedate() != null)) {
			setApprovedate_bak(getApprovedate());
		}
		this.approvedate = approvedate;
	}

	public String getLastadjustuser() {
		return this.lastadjustuser;
	}

	public void setLastadjustuser(String lastadjustuser) {
		this.lastadjustuser = lastadjustuser;
	}

	public String getPk_busitype() {
		return this.pk_busitype;
	}

	public void setPk_busitype(String pk_busitype) {
		this.pk_busitype = pk_busitype;
	}

	public UFDouble getMoney() {
		return this.money;
	}

	public void setMoney(UFDouble money) {
		this.money = money;
	}

	public UFDouble getLocal_money() {
		return this.local_money;
	}

	public void setLocal_money(UFDouble local_money) {
		this.local_money = local_money;
	}

	public String getBillyear() {
		return this.billyear;
	}

	public void setBillyear(String billyear) {
		this.billyear = billyear;
	}

	public String getBillperiod() {
		return this.billperiod;
	}

	public void setBillperiod(String billperiod) {
		this.billperiod = billperiod;
	}

	public String getScomment() {
		return this.scomment;
	}

	public void setScomment(String scomment) {
		this.scomment = scomment;
	}

	public Integer getEffectstatus() {
		return this.effectstatus;
	}

	public void setEffectstatus(Integer effectstatus) {
		this.effectstatus = effectstatus;
	}

	public String getEffectuser() {
		return this.effectuser;
	}

	public void setEffectuser(String effectuser) {
		this.effectuser = effectuser;
	}

	public UFDate getEffectdate() {
		return this.effectdate;
	}

	public void setEffectdate(UFDate effectdate) {
		this.effectdate = effectdate;
	}

	public void setInner_effect_date(UFDate inner_effect_date) {
	}

	public String getLastapproveid() {
		if (this.lastapproveid == null) {
			return this.approver;
		}
		return this.lastapproveid;
	}

	public void setLastapproveid(String lastapproveid) {
		this.lastapproveid = lastapproveid;
	}

	public String getSett_org() {
		return this.sett_org;
	}

	public String getDef1() {
		return this.def1;
	}

	public void setDef1(String def1) {
		this.def1 = def1;
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

	public UFDate getTallydate() {
		return this.tallydate;
	}

	public void setTallydate(UFDate tallydate) {
		this.tallydate = tallydate;
	}

	public UFDouble getRate() {
		return this.rate;
	}

	public void setRate(UFDouble newRate) {
		this.rate = newRate;
	}

	public UFDouble getGrouplocal() {
		return this.grouplocal;
	}

	public void setGrouplocal(UFDouble grouplocal) {
		this.grouplocal = grouplocal;
	}

	public UFDouble getGloballocal() {
		return this.globallocal;
	}

	public void setGloballocal(UFDouble globallocal) {
		this.globallocal = globallocal;
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

	public String getCheckelement() {
		return this.checkelement;
	}

	public void setCheckelement(String checkelement) {
		this.checkelement = checkelement;
	}

	public String getSo_deptid() {
		return this.so_deptid;
	}

	public void setSo_deptid(String so_deptid) {
		this.so_deptid = so_deptid;
	}

	public String getSo_psndoc() {
		return this.so_psndoc;
	}

	public void setSo_psndoc(String so_psndoc) {
		this.so_psndoc = so_psndoc;
	}

	public String getPu_deptid() {
		return this.pu_deptid;
	}

	public void setPu_deptid(String pu_deptid) {
		this.pu_deptid = pu_deptid;
	}

	public String getPu_psndoc() {
		return this.pu_psndoc;
	}

	public void setPu_psndoc(String pu_psndoc) {
		this.pu_psndoc = pu_psndoc;
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

	public String getPk_rescenter() {
		return this.pk_rescenter;
	}

	public void setPk_rescenter(String pk_rescenter) {
		this.pk_rescenter = pk_rescenter;
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

	public String getSupplier() {
		return this.supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getPk_deptid() {
		return this.pk_deptid;
	}

	public void setSett_org(String settOrg) {
		this.sett_org = settOrg;
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

	public void setPk_deptid(String pk_deptid) {
		this.pk_deptid = pk_deptid;
	}

	public String getPk_psndoc() {
		return this.pk_psndoc;
	}

	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	public String getCheckno() {
		return this.checkno;
	}

	public void setCheckno(String checkno) {
		this.checkno = checkno;
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

	public String getIncomeitem() {
		return this.incomeitem;
	}

	public void setIncomeitem(String incomeitem) {
		this.incomeitem = incomeitem;
	}

	public String getPk_subjcode() {
		return this.pk_subjcode;
	}

	public void setPk_subjcode(String pk_subjcode) {
		this.pk_subjcode = pk_subjcode;
	}

	public Integer getObjtype() {
		return this.objtype;
	}

	public void setObjtype(Integer objtype) {
		this.objtype = objtype;
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

	public void setPu_org_v(String pu_org_v) {
		this.pu_org_v = pu_org_v;
	}

	public String getSett_org_v() {
		return this.sett_org_v;
	}

	public void setSett_org_v(String sett_org_v) {
		this.sett_org_v = sett_org_v;
	}

	public String getPk_input() {
		return this.pk_input;
	}

	public void setPk_input(String pk_input) {
		this.pk_input = pk_input;
	}

	public String getPk_balatype() {
		return this.pk_balatype;
	}

	public void setPk_balatype(String pk_balatype) {
		this.pk_balatype = pk_balatype;
	}

	public String getOrdercubasdoc() {
		return this.ordercubasdoc;
	}

	public void setOrdercubasdoc(String ordercubasdoc) {
		this.ordercubasdoc = ordercubasdoc;
	}

	public UFDouble getGlobalnotax() {
		return this.globalnotax;
	}

	public void setGlobalnotax(UFDouble globalnotax) {
		this.globalnotax = globalnotax;
	}

	public UFDouble getGrouptax() {
		return this.grouptax;
	}

	public void setGrouptax(UFDouble grouptax) {
		this.grouptax = grouptax;
	}

	public UFDouble getGlobaltax() {
		return this.globaltax;
	}

	public void setGlobaltax(UFDouble globaltax) {
		this.globaltax = globaltax;
	}

	public UFDouble getGroupnotax() {
		return this.groupnotax;
	}

	public void setGroupnotax(UFDouble groupnotax) {
		this.groupnotax = groupnotax;
	}

	public Integer getCoordflag() {
		return this.coordflag;
	}

	public void setCoordflag(Integer newCoordflag) {
		this.coordflag = newCoordflag;
	}

	public void resetDefaultValue(String billmaker, UFDateTime operationtime) {
		setAccessorynum(Integer.valueOf(0));
		setApprovestatus(Integer
				.valueOf(BillEnumCollection.ApproveStatus.COMMIT.VALUE
						.intValue()));
		setEffectstatus(Integer.valueOf(0));
		setSettleflag(Integer.valueOf(0));

		AccountCalendar acc = AccountCalendar.getInstanceByPk_org(getPk_org());
		try {
			acc.setDate(operationtime.getDate());
		} catch (InvalidAccperiodExcetion e) {
			throw new BusinessRuntimeException(NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2006pub_0", "02006pub-0543"), e);
		}

		setBilldate(operationtime.getDate());
		setBillperiod(acc.getMonthVO().getAccperiodmth());
		setBillyear(acc.getYearVO().getPeriodyear());
		setCreationtime(operationtime);
		setCreator(billmaker);
		setBillmaker(billmaker);
		setPrimaryKey(null);
		setBillno(null);
	}

	public String[] getMoneyFields(String billclass) {
		if ((billclass.equals("ys")) || (billclass.equals("fk")))
			return YSandFKMoneyFields;
		if ((billclass.equals("yf")) || (billclass.equals("sk"))) {
			return YFandSKMoneyFields;
		}
		return new String[0];
	}

	public List<String> getMoneyFields() {
		return MONEYFIELD_LIST;
	}

	public List<String> getRateFields() {
		return RATEFIELD_LIST;
	}

	private SuperVO settleHead = null;

	private UFDate commpayenddate;

	private UFDate commpaybegindate;

	public Object getAttributeValue(String name) {
		if (name == null)
			return "";
		Object value = null;
		if (name.startsWith("zb.")) {
			value = this.settleHead == null ? null : this.settleHead
					.getAttributeValue(name.split("\\.")[1]);
		} else {
			value = super.getAttributeValue(name);
		}
		if (value == null) {
			if (getMoneyFields().contains(name)) {
				value = UFDouble.ZERO_DBL;
			} else if (getRateFields().contains(name)) {
				if ("rate".equals(name)) {
					value = UFDouble.ONE_DBL;
				} else {
					value = UFDouble.ZERO_DBL;
				}
			}
		}

		return value;
	}

	public SuperVO getSettleHead() {
		return this.settleHead;
	}

	public void setSettleHead(SuperVO settleHead) {
		this.settleHead = settleHead;
	}

	public UFBoolean getIsforce() {
		return this.isforce;
	}

	public void setIsforce(UFBoolean isforce) {
		this.isforce = isforce;
	}

	public UFDate getCommpayenddate() {
		return this.commpayenddate;
	}

	public UFDate getCommpaybegindate() {
		return this.commpaybegindate;
	}

	public void setCommpayenddate(UFDate commpayenddate) {
		this.commpayenddate = commpayenddate;
	}

	public void setCommpaybegindate(UFDate commpaybegindate) {
		this.commpaybegindate = commpaybegindate;
	}

	public Integer getEstflag() {
		return this.estflag;
	}

	public void setEstflag(Integer estflag) {
		this.estflag = estflag;
	}

	public boolean isForceEffect() {
		return this.isForceEffect;
	}

	public void setForceEffect(boolean isForceEffect) {
		this.isForceEffect = isForceEffect;
	}

	public String getCostcenter() {
		return this.costcenter;
	}

	public void setCostcenter(String costcenter) {
		this.costcenter = costcenter;
	}

	public UFBoolean getIsfromindependent() {
		return this.isfromindependent;
	}

	public void setIsfromindependent(UFBoolean isfromindependent) {
		this.isfromindependent = isfromindependent;
	}

	public String getRececountryid() {
		return this.rececountryid;
	}

	public void setRececountryid(String rececountryid) {
		this.rececountryid = rececountryid;
	}

	public String getTaxcountryid() {
		return this.taxcountryid;
	}

	public void setTaxcountryid(String taxcountryid) {
		this.taxcountryid = taxcountryid;
	}

	public String getSendcountryid() {
		return this.sendcountryid;
	}

	public void setSendcountryid(String sendcountryid) {
		this.sendcountryid = sendcountryid;
	}

	public String getCreditrefstd() {
		return this.creditrefstd;
	}

	public void setCreditrefstd(String creditrefstd) {
		this.creditrefstd = creditrefstd;
	}

	public boolean isAuthAccount() {
		return this.isAuthAccount;
	}

	public void setAuthAccount(boolean isAuthAccount) {
		this.isAuthAccount = isAuthAccount;
	}

	public void setAttributeValue(String name, Object value) {
		if (value == null) {
			super.setAttributeValue(name, value);
			return;
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
			if (!BeanHelper.setProperty(this, name, value)) {
				super.setAttributeValue(name, value);
			}
		} catch (IllegalArgumentException e) {
			super.setAttributeValue(name, value);
		} catch (ClassCastException e) {
			super.setAttributeValue(name, value);
		}
	}

	public abstract String getPKFieldName();

	public abstract String getTableName();

	public String getStart_period() {
		return this.start_period;
	}

	public void setStart_period(String start_period) {
		this.start_period = start_period;
	}

	public Integer getTotal_period() {
		return this.total_period;
	}

	public void setTotal_period(Integer total_period) {
		this.total_period = total_period;
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
