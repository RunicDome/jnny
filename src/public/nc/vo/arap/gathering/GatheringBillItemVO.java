package nc.vo.arap.gathering;

import nc.vo.arap.basebill.BaseItemVO;
import nc.vo.arap.pub.VOMetaFactory;
import nc.vo.pub.IVOMeta;

public class GatheringBillItemVO extends BaseItemVO {
	private static final long serialVersionUID = 1168473679211807409L;
	private String pk_gatherbill;
	private String pk_gatheritem;
	private String payreason;
	public static final String PK_GATHERBILL = "pk_gatherbill";
	public static final String PK_ORG = "pk_org";
	public static final String PK_FIORG = "pk_fiorg";
	public static final String PK_PCORG = "pk_pcorg";
	public static final String SETT_ORG = "sett_org";
	public static final String SETT_ORG_V = "sett_org_v";
	public static final String PK_PCORG_V = "pk_pcorg_v";
	public static final String PK_ORG_V = "pk_org_v";
	public static final String PK_FIORG_V = "pk_fiorg_v";
	public static final String SO_ORG = "so_org";
	public static final String SO_ORG_V = "so_org_v";
	public static final String SO_TRANSTYPE = "so_transtype";
	public static final String SO_ORDERTYPE = "so_ordertype";
	public static final String SO_PSNDOC = "so_psndoc";
	public static final String SO_DEPTID = "so_deptid";
	public static final String PREPAY = "prepay";
	public static final String CUSTOMER = "customer";
	public static final String POSTUNIT = "postunit";
	public static final String POSTPRICENOTAX = "postpricenotax";
	public static final String POSTQUANTITY = "postquantity";
	public static final String POSTPRICE = "postprice";
	public static final String CHECKDIRECTION = "checkdirection";
	public static final String COORDFLAG = "coordflag";
	public static final String EQUIPMENTCODE = "equipmentcode";
	public static final String PRODUCTLINE = "productline";
	public static final String CASHITEM = "cashitem";
	public static final String PAYFLAG = "payflag";
	public static final String BANKROLLPROJET = "bankrollprojet";
	public static final String PAUSETRANSACT = "pausetransact";
	public static final String BILLDATE = "billdate";
	public static final String PAYMAN = "payman";
	public static final String PAYDATE = "paydate";
	public static final String PK_GROUP = "pk_group";
	public static final String PK_BILLTYPE = "pk_billtype";
	public static final String BILLCLASS = "billclass";
	public static final String PK_TRADETYPE = "pk_tradetype";
	public static final String PK_TRADETYPEID = "pk_tradetypeid";
	public static final String PK_GATHERITEM = "pk_gatheritem";
	public static final String BUSIDATE = "busidate";
	public static final String PK_SUBJCODE = "pk_subjcode";
	public static final String BILLNO = "billno";
	public static final String OBJTYPE = "objtype";
	public static final String ROWNO = "rowno";
	public static final String ROWTYPE = "rowtype";
	public static final String DIRECTION = "direction";
	public static final String CHECKTYPE = "checktype";
	public static final String PK_SSITEM = "pk_ssitem";
	public static final String SCOMMENT = "scomment";
	public static final String SUBJCODE = "subjcode";
	public static final String PK_CURRTYPE = "pk_currtype";
	public static final String RATE = "rate";
	public static final String PK_DEPTID = "pk_deptid";
	public static final String PK_PSNDOC = "pk_psndoc";
	public static final String QUANTITY_CR = "quantity_cr";
	public static final String LOCAL_MONEY_CR = "local_money_cr";
	public static final String MONEY_CR = "money_cr";
	public static final String MONEY_BAL = "money_bal";
	public static final String LOCAL_MONEY_BAL = "local_money_bal";
	public static final String QUANTITY_BAL = "quantity_bal";
	public static final String LOCAL_TAX_CR = "local_tax_cr";
	public static final String NOTAX_CR = "notax_cr";
	public static final String LOCAL_NOTAX_CR = "local_notax_cr";
	public static final String PK_INVENTORY = "pk_inventory";
	public static final String PRICE = "price";
	public static final String TAXPRICE = "taxprice";
	public static final String TAXRATE = "taxrate";
	public static final String TAXNUM = "taxnum";
	public static final String PK_BALATYPE = "pk_balatype";
	public static final String TOP_BILLID = "top_billid";
	public static final String TOP_ITEMID = "top_itemid";
	public static final String TOP_BILLTYPE = "top_billtype";
	public static final String TOP_TRADETYPE = "top_tradetype";
	public static final String SRC_TRADETYPE = "src_tradetype";
	public static final String SRC_BILLTYPE = "src_billtype";
	public static final String SRC_BILLID = "src_billid";
	public static final String SRC_ITEMID = "src_itemid";
	public static final String TAXTYPE = "taxtype";
	public static final String PK_PAYTERM = "pk_payterm";
	public static final String CHECKNO = "checkno";
	public static final String PAYACCOUNT = "payaccount";
	public static final String RECACCOUNT = "recaccount";
	public static final String CASHACCOUNT = "cashaccount";
	public static final String ORDERCUBASDOC = "ordercubasdoc";
	public static final String INNERORDERNO = "innerorderno";
	public static final String ASSETPACTNO = "assetpactno";
	public static final String CONTRACTNO = "contractno";
	public static final String FREECUST = "freecust";
	public static final String FACARD = "facard";
	public static final String PURCHASEORDER = "purchaseorder";
	public static final String INVOICENO = "invoiceno";
	public static final String OUTSTORENO = "outstoreno";
	public static final String PK_JOBPHASE = "pk_jobphase";
	public static final String PK_JOB = "pk_job";
	public static final String DEF30 = "def30";
	public static final String DEF29 = "def29";
	public static final String DEF28 = "def28";
	public static final String DEF27 = "def27";
	public static final String DEF26 = "def26";
	public static final String DEF25 = "def25";
	public static final String DEF24 = "def24";
	public static final String DEF23 = "def23";
	public static final String DEF22 = "def22";
	public static final String DEF21 = "def21";
	public static final String DEF20 = "def20";
	public static final String DEF19 = "def19";
	public static final String DEF18 = "def18";
	public static final String DEF17 = "def17";
	public static final String DEF16 = "def16";
	public static final String DEF15 = "def15";
	public static final String DEF14 = "def14";
	public static final String DEF13 = "def13";
	public static final String DEF12 = "def12";
	public static final String DEF11 = "def11";
	public static final String DEF10 = "def10";
	public static final String DEF9 = "def9";
	public static final String DEF8 = "def8";
	public static final String DEF7 = "def7";
	public static final String DEF6 = "def6";
	public static final String DEF5 = "def5";
	public static final String DEF4 = "def4";
	public static final String DEF3 = "def3";
	public static final String DEF2 = "def2";
	public static final String DEF1 = "def1";
	public static final String CHECKELEMENT = "checkelement";
	public static final String GROUPRATE = "grouprate";
	public static final String GLOBALRATE = "globalrate";
	public static final String GROUPCREBIT = "groupcrebit";
	public static final String GLOBALCREBIT = "globalcrebit";
	public static final String GROUPBALANCE = "groupbalance";
	public static final String GLOBALBALANCE = "globalbalance";
	public static final String GROUPTAX_CRE = "grouptax_cre";
	public static final String GLOBALTAX_CRE = "globaltax_cre";
	public static final String GROUPNOTAX_CRE = "groupnotax_cre";
	public static final String GLOBALNOTAX_CRE = "globalnotax_cre";
	public static final String FORCESTART = "forcestart";
	public static final String FORCEEND = "forceend";
	public static final String OCCUPATIONMNY = "occupationmny";
	public static final String BANKRELATED_CODE = "bankrelated_code";
	public static final String PROJECT = "project";
	public static final String PROJECT_TASK = "project_task";
	public static final String COMMPAYTYPE = "commpaytype";
	public static final String COMMPAYSTATUS = "commpaystatus";
	public static final String REFUSE_REASON = "refuse_reason";
	public static final String AGENTRECEIVEPRIMAL = "agentreceiveprimal";
	public static final String AGENTRECEIVELOCAL = "agentreceivelocal";
	public static final String GROUPAGENTRECEIVELOCAL = "groupagentreceivelocal";
	public static final String GLOBALAGENTRECEIVELOCAL = "globalagentreceivelocal";
	public static final String COMMPAYER = "commpayer";
	public static final String ISREFUSED = "isrefused";
	public static final String TOP_CHANGETS = "top_changets";
	public static final String PAYREASON = "payreason";
	public static final String SETTLEMONEY = "settlemoney";
	public static final String SETTLECURR = "settlecurr";
	public static final String DEF31 = "def31";
	public static final String DEF32 = "def32";
	public static final String DEF33 = "def33";
	public static final String DEF34 = "def34";
	public static final String DEF35 = "def35";
	public static final String DEF36 = "def36";
	public static final String DEF37 = "def37";
	public static final String DEF38 = "def38";
	public static final String DEF39 = "def39";
	public static final String DEF40 = "def40";
	public static final String DEF41 = "def41";
	public static final String DEF42 = "def42";
	public static final String DEF43 = "def43";
	public static final String DEF44 = "def44";
	public static final String DEF45 = "def45";
	public static final String DEF46 = "def46";
	public static final String DEF47 = "def47";
	public static final String DEF48 = "def48";
	public static final String DEF49 = "def49";
	public static final String DEF50 = "def50";
	public static final String DEF51 = "def51";
	public static final String DEF52 = "def52";
	public static final String DEF53 = "def53";
	public static final String DEF54 = "def54";
	public static final String DEF55 = "def55";
	public static final String DEF56 = "def56";
	public static final String DEF57 = "def57";
	public static final String DEF58 = "def58";
	public static final String DEF59 = "def59";
	public static final String DEF60 = "def60";
	public static final String DEF61 = "def61";
	public static final String DEF62 = "def62";
	public static final String DEF63 = "def63";
	public static final String DEF64 = "def64";
	public static final String DEF65 = "def65";
	public static final String DEF66 = "def66";
	public static final String DEF67 = "def67";
	public static final String DEF68 = "def68";
	public static final String DEF69 = "def69";
	public static final String DEF70 = "def70";
	public static final String DEF71 = "def71";
	public static final String DEF72 = "def72";
	public static final String DEF73 = "def73";
	public static final String DEF74 = "def74";
	public static final String DEF75 = "def75";
	public static final String DEF76 = "def76";
	public static final String DEF77 = "def77";
	public static final String DEF78 = "def78";
	public static final String DEF79 = "def79";
	public static final String DEF80 = "def80";
	// XBX新增100个自定义项
	public static final String DEF81 = "def81";
	public static final String DEF82 = "def82";
	public static final String DEF83 = "def83";
	public static final String DEF84 = "def84";
	public static final String DEF85 = "def85";
	public static final String DEF86 = "def86";
	public static final String DEF87 = "def87";
	public static final String DEF88 = "def88";
	public static final String DEF89 = "def89";
	public static final String DEF90 = "def90";
	public static final String DEF91 = "def91";
	public static final String DEF92 = "def92";
	public static final String DEF93 = "def93";
	public static final String DEF94 = "def94";
	public static final String DEF95 = "def95";
	public static final String DEF96 = "def96";
	public static final String DEF97 = "def97";
	public static final String DEF98 = "def98";
	public static final String DEF99 = "def99";
	public static final String DEF100 = "DEF100";
	public static final String DEF101 = "DEF101";
	public static final String DEF102 = "DEF102";
	public static final String DEF103 = "DEF103";
	public static final String DEF104 = "DEF104";
	public static final String DEF105 = "DEF105";
	public static final String DEF106 = "DEF106";
	public static final String DEF107 = "DEF107";
	public static final String DEF108 = "DEF108";
	public static final String DEF109 = "DEF109";
	public static final String DEF110 = "DEF110";
	public static final String DEF111 = "DEF111";
	public static final String DEF112 = "DEF112";
	public static final String DEF113 = "DEF113";
	public static final String DEF114 = "DEF114";
	public static final String DEF115 = "DEF115";
	public static final String DEF116 = "DEF116";
	public static final String DEF117 = "DEF117";
	public static final String DEF118 = "DEF118";
	public static final String DEF119 = "DEF119";
	public static final String DEF120 = "DEF120";
	public static final String DEF121 = "DEF121";
	public static final String DEF122 = "DEF122";
	public static final String DEF123 = "DEF123";
	public static final String DEF124 = "DEF124";
	public static final String DEF125 = "DEF125";
	public static final String DEF126 = "DEF126";
	public static final String DEF127 = "DEF127";
	public static final String DEF128 = "DEF128";
	public static final String DEF129 = "DEF129";
	public static final String DEF130 = "DEF130";
	public static final String DEF131 = "DEF131";
	public static final String DEF132 = "DEF132";
	public static final String DEF133 = "DEF133";
	public static final String DEF134 = "DEF134";
	public static final String DEF135 = "DEF135";
	public static final String DEF136 = "DEF136";
	public static final String DEF137 = "DEF137";
	public static final String DEF138 = "DEF138";
	public static final String DEF139 = "DEF139";
	public static final String DEF140 = "DEF140";
	public static final String DEF141 = "DEF141";
	public static final String DEF142 = "DEF142";
	public static final String DEF143 = "DEF143";
	public static final String DEF144 = "DEF144";
	public static final String DEF145 = "DEF145";
	public static final String DEF146 = "DEF146";
	public static final String DEF147 = "DEF147";
	public static final String DEF148 = "DEF148";
	public static final String DEF149 = "DEF149";
	public static final String DEF150 = "DEF150";
	public static final String DEF151 = "DEF151";
	public static final String DEF152 = "DEF152";
	public static final String DEF153 = "DEF153";
	public static final String DEF154 = "DEF154";
	public static final String DEF155 = "DEF155";
	public static final String DEF156 = "DEF156";
	public static final String DEF157 = "DEF157";
	public static final String DEF158 = "DEF158";
	public static final String DEF159 = "DEF159";
	public static final String DEF160 = "DEF160";
	public static final String DEF161 = "DEF161";
	public static final String DEF162 = "DEF162";
	public static final String DEF163 = "DEF163";
	public static final String DEF164 = "DEF164";
	public static final String DEF165 = "DEF165";
	public static final String DEF166 = "DEF166";
	public static final String DEF167 = "DEF167";
	public static final String DEF168 = "DEF168";
	public static final String DEF169 = "DEF169";
	public static final String DEF170 = "DEF170";
	public static final String DEF171 = "DEF171";
	public static final String DEF172 = "DEF172";
	public static final String DEF173 = "DEF173";
	public static final String DEF174 = "DEF174";
	public static final String DEF175 = "DEF175";
	public static final String DEF176 = "DEF176";
	public static final String DEF177 = "DEF177";
	public static final String DEF178 = "DEF178";
	public static final String DEF179 = "DEF179";
	public static final String DEF180 = "DEF180";

	public String getPk_gatherbill() {
		return this.pk_gatherbill;
	}

	public void setPk_gatherbill(String newPk_gatherbill) {
		this.pk_gatherbill = newPk_gatherbill;
	}

	public String getParentPKFieldName() {
		return "pk_gatherbill";
	}

	public String getPk_gatheritem() {
		return this.pk_gatheritem;
	}

	public void setPk_gatheritem(String pkGatheritem) {
		this.pk_gatheritem = pkGatheritem;
	}

	public String getPKFieldName() {
		return "pk_gatheritem";
	}

	public String getTableName() {
		return "ar_gatheritem";
	}

	public IVOMeta getMetaData() {
		IVOMeta meta = VOMetaFactory.getInstance().getVOMeta("arap.gatheritem");
		return meta;
	}

	public String getChildrenPK() {
		return this.pk_gatheritem;
	}

	public String getParentPK() {
		return this.pk_gatherbill;
	}

	public void setChildrenPK(String pk_gatheritem) {
		this.pk_gatheritem = pk_gatheritem;
	}

	public void setParentPK(String pk_gatherbill) {
		this.pk_gatherbill = pk_gatherbill;
	}

	public GatheringBillItemVO() {
		setBillclass("sk");
	}

	public String getPayreason() {
		return this.payreason;
	}

	public void setPayreason(String payreason) {
		this.payreason = payreason;
	}
}
