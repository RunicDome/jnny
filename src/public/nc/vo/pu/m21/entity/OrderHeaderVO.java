package nc.vo.pu.m21.entity;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

// 采购订单表头VO（扩自定义项后）
public class OrderHeaderVO extends SuperVO {
	public static final String APPROVER = "approver";
	public static final String BCOOPTOSO = "bcooptoso";
	public static final String BDIRECT = "bdirect";
	public static final String BFINALCLOSE = "bfinalclose";
	public static final String BFROZEN = "bfrozen";
	public static final String BILLMAKER = "billmaker";
	public static final String BISLATEST = "bislatest";
	public static final String BISREPLENISH = "bisreplenish";
	public static final String BPUBLISH = "bpublish";
	public static final String BREFWHENRETURN = "brefwhenreturn";
	public static final String BRELEASEDOVER = "breleasedover";
	public static final String BRETURN = "breturn";
	public static final String BSOCOOPTOME = "bsocooptome";
	public static final String CCONTRACTTEXTPATH = "ccontracttextpath";
	public static final String CEMPLOYEEID = "cemployeeid";
	public static final String CORIGCURRENCYID = "corigcurrencyid";
	public static final String CREATIONTIME = "creationtime";
	public static final String CREATOR = "creator";
	public static final String CREVISEPSN = "crevisepsn";
	public static final String CTRADEWORDID = "ctradewordid";
	public static final String CTRANTYPEID = "ctrantypeid";
	public static final String DBILLDATE = "dbilldate";
	public static final String DCLOSEDATE = "dclosedate";
	public static final String DMAKEDATE = "dmakedate";
	public static final String DR = "dr";
	public static final String FHTAXTYPEFLAG = "fhtaxtypeflag";
	public static final String FORDERSTATUS = "forderstatus";
	public static final String IPRINTCOUNT = "iprintcount";
	public static final String IRESPSTATUS = "irespstatus";
	public static final String MODIFIEDTIME = "modifiedtime";
	public static final String MODIFIER = "modifier";
	public static final String NHTAXRATE = "nhtaxrate";
	public static final String NORGPREPAYLIMIT = "norgprepaylimit";
	public static final String NTOTALASTNUM = "ntotalastnum";
	public static final String NTOTALORIGMNY = "ntotalorigmny";
	public static final String NTOTALPIECE = "ntotalpiece";
	public static final String NTOTALVOLUME = "ntotalvolume";
	public static final String NTOTALWEIGHT = "ntotalweight";
	public static final String NVERSION = "nversion";
	public static final String PK_BALATYPE = "pk_balatype";
	public static final String PK_BANKDOC = "pk_bankdoc";
	public static final String PK_BUSITYPE = "pk_busitype";
	public static final String PK_DELIVERADD = "pk_deliveradd";
	public static final String PK_DEPT = "pk_dept";
	public static final String PK_DEPT_V = "pk_dept_v";
	public static final String PK_FREECUST = "pk_freecust";
	public static final String PK_FREEZEPSNDOC = "pk_freezepsndoc";
	public static final String PK_GROUP = "pk_group";
	public static final String PK_INVCSUPLLIER = "pk_invcsupllier";
	public static final String PK_ORDER = "pk_order";
	public static final String PK_ORG = "pk_org";
	public static final String PK_ORG_V = "pk_org_v";
	public static final String PK_PAYTERM = "pk_payterm";
	public static final String PK_PROJECT = "pk_project";
	public static final String PK_PUBPSN = "pk_pubpsn";
	public static final String PK_RECVCUSTOMER = "pk_recvcustomer";
	public static final String PK_RESPPSN = "pk_resppsn";
	public static final String PK_SUPPLIER = "pk_supplier";
	public static final String PK_TRANSPORTTYPE = "pk_transporttype";
	public static final String TAUDITTIME = "taudittime";
	public static final String TFREEZETIME = "tfreezetime";
	public static final String TPUBTIME = "tpubtime";
	public static final String TRESPTIME = "tresptime";
	public static final String TREVISIONTIME = "trevisiontime";
	public static final String TS = "ts";
	public static final String VBANKACCOUNT = "vbankaccount";
	public static final String VBILLCODE = "vbillcode";
	public static final String VCOOPORDERCODE = "vcoopordercode";
	public static final String VDEF1 = "vdef1";
	public static final String VDEF10 = "vdef10";
	public static final String VDEF11 = "vdef11";
	public static final String VDEF12 = "vdef12";
	public static final String VDEF13 = "vdef13";
	public static final String VDEF14 = "vdef14";
	public static final String VDEF15 = "vdef15";
	public static final String VDEF16 = "vdef16";
	public static final String VDEF17 = "vdef17";
	public static final String VDEF18 = "vdef18";
	public static final String VDEF19 = "vdef19";
	public static final String VDEF2 = "vdef2";
	public static final String VDEF20 = "vdef20";
	public static final String VDEF3 = "vdef3";
	public static final String VDEF4 = "vdef4";
	public static final String VDEF5 = "vdef5";
	public static final String VDEF6 = "vdef6";
	public static final String VDEF7 = "vdef7";
	public static final String VDEF8 = "vdef8";
	public static final String VDEF9 = "vdef9";
	// 20~40
	public static final String VDEF21 = "vdef21";
	public static final String VDEF22 = "vdef22";
	public static final String VDEF23 = "vdef23";
	public static final String VDEF24 = "vdef24";
	public static final String VDEF25 = "vdef25";
	public static final String VDEF26 = "vdef26";
	public static final String VDEF27 = "vdef27";
	public static final String VDEF28 = "vdef28";
	public static final String VDEF29 = "vdef29";
	public static final String VDEF30 = "vdef30";
	public static final String VDEF31 = "vdef31";
	public static final String VDEF32 = "vdef32";
	public static final String VDEF33 = "vdef33";
	public static final String VDEF34 = "vdef34";
	public static final String VDEF35 = "vdef35";
	public static final String VDEF36 = "vdef36";
	public static final String VDEF37 = "vdef37";
	public static final String VDEF38 = "vdef38";
	public static final String VDEF39 = "vdef39";
	public static final String VDEF40 = "vdef40";
	// 41~60
	public static final String VDEF41 = "vdef41";
	public static final String VDEF42 = "vdef42";
	public static final String VDEF43 = "vdef43";
	public static final String VDEF44 = "vdef44";
	public static final String VDEF45 = "vdef45";
	public static final String VDEF46 = "vdef46";
	public static final String VDEF47 = "vdef47";
	public static final String VDEF48 = "vdef48";
	public static final String VDEF49 = "vdef49";
	public static final String VDEF50 = "vdef50";
	public static final String VDEF51 = "vdef51";
	public static final String VDEF52 = "vdef52";
	public static final String VDEF53 = "vdef53";
	public static final String VDEF54 = "vdef54";
	public static final String VDEF55 = "vdef55";
	public static final String VDEF56 = "vdef56";
	public static final String VDEF57 = "vdef57";
	public static final String VDEF58 = "vdef58";
	public static final String VDEF59 = "vdef59";
	public static final String VDEF60 = "vdef60";
	public static final String VFROZENREASON = "vfrozenreason";
	public static final String VMEMO = "vmemo";
	public static final String VREASON = "vreason";
	public static final String VTRANTYPECODE = "vtrantypecode";
	private static final long serialVersionUID = -7001523181741213828L;

	public OrderHeaderVO() {
	}

	public String getApprover() {
		return (String) getAttributeValue("approver");
	}

	public UFBoolean getBcooptoso() {
		return (UFBoolean) getAttributeValue("bcooptoso");
	}

	public UFBoolean getBdirect() {
		return (UFBoolean) getAttributeValue("bdirect");
	}

	public UFBoolean getBfinalclose() {
		return (UFBoolean) getAttributeValue("bfinalclose");
	}

	public UFBoolean getBfrozen() {
		return (UFBoolean) getAttributeValue("bfrozen");
	}

	public String getBillmaker() {
		return (String) getAttributeValue("billmaker");
	}

	public UFBoolean getBislatest() {
		return (UFBoolean) getAttributeValue("bislatest");
	}

	public UFBoolean getBisreplenish() {
		return (UFBoolean) getAttributeValue("bisreplenish");
	}

	public UFBoolean getBpublish() {
		return (UFBoolean) getAttributeValue("bpublish");
	}

	public UFBoolean getBrefwhenreturn() {
		return (UFBoolean) getAttributeValue("brefwhenreturn");
	}

	public UFBoolean getBreleasedover() {
		return (UFBoolean) getAttributeValue("breleasedover");
	}

	public UFBoolean getBreturn() {
		return (UFBoolean) getAttributeValue("breturn");
	}

	public UFBoolean getBsocooptome() {
		return (UFBoolean) getAttributeValue("bsocooptome");
	}

	public String getCcontracttextpath() {
		return (String) getAttributeValue("ccontracttextpath");
	}

	public String getCemployeeid() {
		return (String) getAttributeValue("cemployeeid");
	}

	public String getCorigcurrencyid() {
		return (String) getAttributeValue("corigcurrencyid");
	}

	public UFDateTime getCreationtime() {
		return (UFDateTime) getAttributeValue("creationtime");
	}

	public String getCreator() {
		return (String) getAttributeValue("creator");
	}

	public String getCrevisepsn() {
		return (String) getAttributeValue("crevisepsn");
	}

	public String getCtradewordid() {
		return (String) getAttributeValue("ctradewordid");
	}

	public String getCtrantypeid() {
		return (String) getAttributeValue("ctrantypeid");
	}

	public UFDate getDbilldate() {
		return (UFDate) getAttributeValue("dbilldate");
	}

	public UFDate getDclosedate() {
		return (UFDate) getAttributeValue("dclosedate");
	}

	public UFDate getDmakedate() {
		return (UFDate) getAttributeValue("dmakedate");
	}

	public Integer getDr() {
		return (Integer) getAttributeValue("dr");
	}

	public Integer getFhtaxtypeflag() {
		return (Integer) getAttributeValue("fhtaxtypeflag");
	}

	public Integer getForderstatus() {
		return (Integer) getAttributeValue("forderstatus");
	}

	public Integer getIprintcount() {
		return (Integer) getAttributeValue("iprintcount");
	}

	public Integer getIrespstatus() {
		return (Integer) getAttributeValue("irespstatus");
	}

	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("pu.po_order");
	}

	public UFDateTime getModifiedtime() {
		return (UFDateTime) getAttributeValue("modifiedtime");
	}

	public String getModifier() {
		return (String) getAttributeValue("modifier");
	}

	public UFDouble getNhtaxrate() {
		return (UFDouble) getAttributeValue("nhtaxrate");
	}

	public UFDouble getNorgprepaylimit() {
		return (UFDouble) getAttributeValue("norgprepaylimit");
	}

	public UFDouble getNtotalastnum() {
		return (UFDouble) getAttributeValue("ntotalastnum");
	}

	public UFDouble getNtotalorigmny() {
		return (UFDouble) getAttributeValue("ntotalorigmny");
	}

	public UFDouble getNtotalpiece() {
		return (UFDouble) getAttributeValue("ntotalpiece");
	}

	public UFDouble getNtotalvolume() {
		return (UFDouble) getAttributeValue("ntotalvolume");
	}

	public UFDouble getNtotalweight() {
		return (UFDouble) getAttributeValue("ntotalweight");
	}

	public Integer getNversion() {
		return (Integer) getAttributeValue("nversion");
	}

	public String getPk_balatype() {
		return (String) getAttributeValue("pk_balatype");
	}

	public String getPk_bankdoc() {
		return (String) getAttributeValue("pk_bankdoc");
	}

	public String getPk_busitype() {
		return (String) getAttributeValue("pk_busitype");
	}

	public String getPk_deliveradd() {
		return (String) getAttributeValue("pk_deliveradd");
	}

	public String getPk_dept() {
		return (String) getAttributeValue("pk_dept");
	}

	public String getPk_dept_v() {
		return (String) getAttributeValue("pk_dept_v");
	}

	public String getPk_freecust() {
		return (String) getAttributeValue("pk_freecust");
	}

	public String getPk_freezepsndoc() {
		return (String) getAttributeValue("pk_freezepsndoc");
	}

	public String getPk_group() {
		return (String) getAttributeValue("pk_group");
	}

	public String getPk_invcsupllier() {
		return (String) getAttributeValue("pk_invcsupllier");
	}

	public String getPk_order() {
		return (String) getAttributeValue("pk_order");
	}

	public String getPk_org() {
		return (String) getAttributeValue("pk_org");
	}

	public String getPk_org_v() {
		return (String) getAttributeValue("pk_org_v");
	}

	public String getPk_payterm() {
		return (String) getAttributeValue("pk_payterm");
	}

	public String getPk_project() {
		return (String) getAttributeValue("pk_project");
	}

	public String getPk_pubpsn() {
		return (String) getAttributeValue("pk_pubpsn");
	}

	public String getPk_recvcustomer() {
		return (String) getAttributeValue("pk_recvcustomer");
	}

	public String getPk_resppsn() {
		return (String) getAttributeValue("pk_resppsn");
	}

	public String getPk_supplier() {
		return (String) getAttributeValue("pk_supplier");
	}

	public String getPk_transporttype() {
		return (String) getAttributeValue("pk_transporttype");
	}

	public UFDate getTaudittime() {
		return (UFDate) getAttributeValue("taudittime");
	}

	public UFDate getTfreezetime() {
		return (UFDate) getAttributeValue("tfreezetime");
	}

	public UFDate getTpubtime() {
		return (UFDate) getAttributeValue("tpubtime");
	}

	public UFDate getTresptime() {
		return (UFDate) getAttributeValue("tresptime");
	}

	public UFDate getTrevisiontime() {
		return (UFDate) getAttributeValue("trevisiontime");
	}

	public UFDateTime getTs() {
		return (UFDateTime) getAttributeValue("ts");
	}

	public String getVbillcode() {
		return (String) getAttributeValue("vbillcode");
	}

	public String getVcoopordercode() {
		return (String) getAttributeValue("vcoopordercode");
	}

	public String getVdef1() {
		return (String) getAttributeValue("vdef1");
	}

	public String getVdef10() {
		return (String) getAttributeValue("vdef10");
	}

	public String getVdef11() {
		return (String) getAttributeValue("vdef11");
	}

	public String getVdef12() {
		return (String) getAttributeValue("vdef12");
	}

	public String getVdef13() {
		return (String) getAttributeValue("vdef13");
	}

	public String getVdef14() {
		return (String) getAttributeValue("vdef14");
	}

	public String getVdef15() {
		return (String) getAttributeValue("vdef15");
	}

	public String getVdef16() {
		return (String) getAttributeValue("vdef16");
	}

	public String getVdef17() {
		return (String) getAttributeValue("vdef17");
	}

	public String getVdef18() {
		return (String) getAttributeValue("vdef18");
	}

	public String getVdef19() {
		return (String) getAttributeValue("vdef19");
	}

	public String getVdef2() {
		return (String) getAttributeValue("vdef2");
	}

	public String getVdef20() {
		return (String) getAttributeValue("vdef20");
	}

	// 21~40
	public String getVdef21() {
		return (String) getAttributeValue("vdef21");
	}

	public String getVdef22() {
		return (String) getAttributeValue("vdef22");
	}

	public String getVdef23() {
		return (String) getAttributeValue("vdef23");
	}

	public String getVdef24() {
		return (String) getAttributeValue("vdef24");
	}

	public String getVdef25() {
		return (String) getAttributeValue("vdef25");
	}

	public String getVdef26() {
		return (String) getAttributeValue("vdef26");
	}

	public String getVdef27() {
		return (String) getAttributeValue("vdef27");
	}

	public String getVdef28() {
		return (String) getAttributeValue("vdef28");
	}

	public String getVdef29() {
		return (String) getAttributeValue("vdef29");
	}

	public String getVdef30() {
		return (String) getAttributeValue("vdef30");
	}

	public String getVdef31() {
		return (String) getAttributeValue("vdef31");
	}

	public String getVdef32() {
		return (String) getAttributeValue("vdef32");
	}

	public String getVdef33() {
		return (String) getAttributeValue("vdef33");
	}

	public String getVdef34() {
		return (String) getAttributeValue("vdef34");
	}

	public String getVdef35() {
		return (String) getAttributeValue("vdef35");
	}

	public String getVdef36() {
		return (String) getAttributeValue("vdef36");
	}

	public String getVdef37() {
		return (String) getAttributeValue("vdef37");
	}

	public String getVdef38() {
		return (String) getAttributeValue("vdef38");
	}

	public String getVdef39() {
		return (String) getAttributeValue("vdef39");
	}

	public String getVdef40() {
		return (String) getAttributeValue("vdef40");
	}
	// 41~60
	public String getVdef41() {
		return (String) getAttributeValue("vdef41");
	}

	public String getVdef42() {
		return (String) getAttributeValue("vdef42");
	}

	public String getVdef43() {
		return (String) getAttributeValue("vdef43");
	}

	public String getVdef44() {
		return (String) getAttributeValue("vdef44");
	}

	public String getVdef45() {
		return (String) getAttributeValue("vdef45");
	}

	public String getVdef46() {
		return (String) getAttributeValue("vdef46");
	}

	public String getVdef47() {
		return (String) getAttributeValue("vdef47");
	}

	public String getVdef48() {
		return (String) getAttributeValue("vdef48");
	}

	public String getVdef49() {
		return (String) getAttributeValue("vdef49");
	}

	public String getVdef50() {
		return (String) getAttributeValue("vdef50");
	}

	public String getVdef51() {
		return (String) getAttributeValue("vdef51");
	}

	public String getVdef52() {
		return (String) getAttributeValue("vdef52");
	}

	public String getVdef53() {
		return (String) getAttributeValue("vdef53");
	}

	public String getVdef54() {
		return (String) getAttributeValue("vdef54");
	}

	public String getVdef55() {
		return (String) getAttributeValue("vdef55");
	}

	public String getVdef56() {
		return (String) getAttributeValue("vdef56");
	}

	public String getVdef57() {
		return (String) getAttributeValue("vdef57");
	}

	public String getVdef58() {
		return (String) getAttributeValue("vdef58");
	}

	public String getVdef59() {
		return (String) getAttributeValue("vdef59");
	}

	public String getVdef60() {
		return (String) getAttributeValue("vdef60");
	}

	public String getVdef3() {
		return (String) getAttributeValue("vdef3");
	}

	public String getVdef4() {
		return (String) getAttributeValue("vdef4");
	}

	public String getVdef5() {
		return (String) getAttributeValue("vdef5");
	}

	public String getVdef6() {
		return (String) getAttributeValue("vdef6");
	}

	public String getVdef7() {
		return (String) getAttributeValue("vdef7");
	}

	public String getVdef8() {
		return (String) getAttributeValue("vdef8");
	}

	public String getVdef9() {
		return (String) getAttributeValue("vdef9");
	}

	public String getVfrozenreason() {
		return (String) getAttributeValue("vfrozenreason");
	}

	public String getVmemo() {
		return (String) getAttributeValue("vmemo");
	}

	public String getVreason() {
		return (String) getAttributeValue("vreason");
	}

	public String getVtrantypecode() {
		return (String) getAttributeValue("vtrantypecode");
	}

	public void setApprover(String approver) {
		setAttributeValue("approver", approver);
	}

	public void setBcooptoso(UFBoolean bcooptoso) {
		setAttributeValue("bcooptoso", bcooptoso);
	}

	public void setBdirect(UFBoolean bdirect) {
		setAttributeValue("bdirect", bdirect);
	}

	public void setBfinalclose(UFBoolean bfinalclose) {
		setAttributeValue("bfinalclose", bfinalclose);
	}

	public void setBfrozen(UFBoolean bfrozen) {
		setAttributeValue("bfrozen", bfrozen);
	}

	public void setBillmaker(String billmaker) {
		setAttributeValue("billmaker", billmaker);
	}

	public void setBislatest(UFBoolean bislatest) {
		setAttributeValue("bislatest", bislatest);
	}

	public void setBisreplenish(UFBoolean bisreplenish) {
		setAttributeValue("bisreplenish", bisreplenish);
	}

	public void setBpublish(UFBoolean bpublish) {
		setAttributeValue("bpublish", bpublish);
	}

	public void setBrefwhenreturn(UFBoolean brefwhenreturn) {
		setAttributeValue("brefwhenreturn", brefwhenreturn);
	}

	public void setBreleasedover(UFBoolean breleasedover) {
		setAttributeValue("breleasedover", breleasedover);
	}

	public void setBreturn(UFBoolean breturn) {
		setAttributeValue("breturn", breturn);
	}

	public void setBsocooptome(UFBoolean bsocooptome) {
		setAttributeValue("bsocooptome", bsocooptome);
	}

	public void setCcontracttextpath(String ccontracttextpath) {
		setAttributeValue("ccontracttextpath", ccontracttextpath);
	}

	public void setCemployeeid(String cemployeeid) {
		setAttributeValue("cemployeeid", cemployeeid);
	}

	public void setCorigcurrencyid(String corigcurrencyid) {
		setAttributeValue("corigcurrencyid", corigcurrencyid);
	}

	public void setCreationtime(UFDateTime creationtime) {
		setAttributeValue("creationtime", creationtime);
	}

	public void setCreator(String creator) {
		setAttributeValue("creator", creator);
	}

	public void setCrevisepsn(String crevisepsn) {
		setAttributeValue("crevisepsn", crevisepsn);
	}

	public void setCtradewordid(String ctradewordid) {
		setAttributeValue("ctradewordid", ctradewordid);
	}

	public void setCtrantypeid(String ctrantypeid) {
		setAttributeValue("ctrantypeid", ctrantypeid);
	}

	public void setDbilldate(UFDate dbilldate) {
		setAttributeValue("dbilldate", dbilldate);
	}

	public void setDclosedate(UFDate dclosedate) {
		setAttributeValue("dclosedate", dclosedate);
	}

	public void setDmakedate(UFDate dmakedate) {
		setAttributeValue("dmakedate", dmakedate);
	}

	public void setDr(Integer dr) {
		setAttributeValue("dr", dr);
	}

	public void setFhtaxtypeflag(Integer fhtaxtypeflag) {
		setAttributeValue("fhtaxtypeflag", fhtaxtypeflag);
	}

	public void setForderstatus(Integer forderstatus) {
		setAttributeValue("forderstatus", forderstatus);
	}

	public void setIprintcount(Integer iprintcount) {
		setAttributeValue("iprintcount", iprintcount);
	}

	public void setIrespstatus(Integer irespstatus) {
		setAttributeValue("irespstatus", irespstatus);
	}

	public void setModifiedtime(UFDateTime modifiedtime) {
		setAttributeValue("modifiedtime", modifiedtime);
	}

	public void setModifier(String modifier) {
		setAttributeValue("modifier", modifier);
	}

	public void setNhtaxrate(UFDouble nhtaxrate) {
		setAttributeValue("nhtaxrate", nhtaxrate);
	}

	public void setNorgprepaylimit(UFDouble norgprepaylimit) {
		setAttributeValue("norgprepaylimit", norgprepaylimit);
	}

	public void setNtotalastnum(UFDouble ntotalastnum) {
		setAttributeValue("ntotalastnum", ntotalastnum);
	}

	public void setNtotalorigmny(UFDouble ntotalorigmny) {
		setAttributeValue("ntotalorigmny", ntotalorigmny);
	}

	public void setNtotalpiece(UFDouble ntotalpiece) {
		setAttributeValue("ntotalpiece", ntotalpiece);
	}

	public void setNtotalvolume(UFDouble ntotalvolume) {
		setAttributeValue("ntotalvolume", ntotalvolume);
	}

	public void setNtotalweight(UFDouble ntotalweight) {
		setAttributeValue("ntotalweight", ntotalweight);
	}

	public void setNversion(Integer nversion) {
		setAttributeValue("nversion", nversion);
	}

	public void setPk_balatype(String pk_balatype) {
		setAttributeValue("pk_balatype", pk_balatype);
	}

	public void setPk_bankdoc(String pk_bankdoc) {
		setAttributeValue("pk_bankdoc", pk_bankdoc);
	}

	public void setPk_busitype(String pk_busitype) {
		setAttributeValue("pk_busitype", pk_busitype);
	}

	public void setPk_deliveradd(String pk_deliveradd) {
		setAttributeValue("pk_deliveradd", pk_deliveradd);
	}

	public void setPk_dept(String pk_dept) {
		setAttributeValue("pk_dept", pk_dept);
	}

	public void setPk_dept_v(String pk_dept_v) {
		setAttributeValue("pk_dept_v", pk_dept_v);
	}

	public void setPk_freecust(String pk_freecust) {
		setAttributeValue("pk_freecust", pk_freecust);
	}

	public void setPk_freezepsndoc(String pk_freezepsndoc) {
		setAttributeValue("pk_freezepsndoc", pk_freezepsndoc);
	}

	public void setPk_group(String pk_group) {
		setAttributeValue("pk_group", pk_group);
	}

	public void setPk_invcsupllier(String pk_invcsupllier) {
		setAttributeValue("pk_invcsupllier", pk_invcsupllier);
	}

	public void setPk_order(String pk_order) {
		setAttributeValue("pk_order", pk_order);
	}

	public void setPk_org(String pk_org) {
		setAttributeValue("pk_org", pk_org);
	}

	public void setPk_org_v(String pk_org_v) {
		setAttributeValue("pk_org_v", pk_org_v);
	}

	public void setPk_payterm(String pk_payterm) {
		setAttributeValue("pk_payterm", pk_payterm);
	}

	public void setPk_project(String pk_project) {
		setAttributeValue("pk_project", pk_project);
	}

	public void setPk_pubpsn(String pk_pubpsn) {
		setAttributeValue("pk_pubpsn", pk_pubpsn);
	}

	public void setPk_recvcustomer(String pk_recvcustomer) {
		setAttributeValue("pk_recvcustomer", pk_recvcustomer);
	}

	public void setPk_resppsn(String pk_resppsn) {
		setAttributeValue("pk_resppsn", pk_resppsn);
	}

	public void setPk_supplier(String pk_supplier) {
		setAttributeValue("pk_supplier", pk_supplier);
	}

	public void setPk_transporttype(String pk_transporttype) {
		setAttributeValue("pk_transporttype", pk_transporttype);
	}

	public void setTaudittime(UFDate taudittime) {
		setAttributeValue("taudittime", taudittime);
	}

	public void setTfreezetime(UFDate tfreezetime) {
		setAttributeValue("tfreezetime", tfreezetime);
	}

	public void setTpubtime(UFDate tpubtime) {
		setAttributeValue("tpubtime", tpubtime);
	}

	public void setTresptime(UFDate tresptime) {
		setAttributeValue("tresptime", tresptime);
	}

	public void setTrevisiontime(UFDate trevisiontime) {
		setAttributeValue("trevisiontime", trevisiontime);
	}

	public void setTs(UFDateTime ts) {
		setAttributeValue("ts", ts);
	}

	public void setVbillcode(String vbillcode) {
		setAttributeValue("vbillcode", vbillcode);
	}

	public void setVcoopordercode(String vcoopordercode) {
		setAttributeValue("vcoopordercode", vcoopordercode);
	}

	public void setVdef1(String vdef1) {
		setAttributeValue("vdef1", vdef1);
	}

	public void setVdef10(String vdef10) {
		setAttributeValue("vdef10", vdef10);
	}

	public void setVdef11(String vdef11) {
		setAttributeValue("vdef11", vdef11);
	}

	public void setVdef12(String vdef12) {
		setAttributeValue("vdef12", vdef12);
	}

	public void setVdef13(String vdef13) {
		setAttributeValue("vdef13", vdef13);
	}

	public void setVdef14(String vdef14) {
		setAttributeValue("vdef14", vdef14);
	}

	public void setVdef15(String vdef15) {
		setAttributeValue("vdef15", vdef15);
	}

	public void setVdef16(String vdef16) {
		setAttributeValue("vdef16", vdef16);
	}

	public void setVdef17(String vdef17) {
		setAttributeValue("vdef17", vdef17);
	}

	public void setVdef18(String vdef18) {
		setAttributeValue("vdef18", vdef18);
	}

	public void setVdef19(String vdef19) {
		setAttributeValue("vdef19", vdef19);
	}

	public void setVdef2(String vdef2) {
		setAttributeValue("vdef2", vdef2);
	}

	public void setVdef20(String vdef20) {
		setAttributeValue("vdef20", vdef20);
	}

	// 21~40
	public void setVdef21(String vdef21) {
		setAttributeValue("vdef21", vdef21);
	}

	public void setVdef22(String vdef22) {
		setAttributeValue("vdef22", vdef22);
	}

	public void setVdef23(String vdef23) {
		setAttributeValue("vdef23", vdef23);
	}

	public void setVdef24(String vdef24) {
		setAttributeValue("vdef24", vdef24);
	}

	public void setVdef25(String vdef25) {
		setAttributeValue("vdef25", vdef25);
	}

	public void setVdef26(String vdef26) {
		setAttributeValue("vdef26", vdef26);
	}

	public void setVdef27(String vdef27) {
		setAttributeValue("vdef27", vdef27);
	}

	public void setVdef28(String vdef28) {
		setAttributeValue("vdef28", vdef28);
	}

	public void setVdef29(String vdef29) {
		setAttributeValue("vdef29", vdef29);
	}

	public void setVdef30(String vdef30) {
		setAttributeValue("vdef30", vdef30);
	}

	public void setVdef31(String vdef31) {
		setAttributeValue("vdef31", vdef31);
	}

	public void setVdef32(String vdef32) {
		setAttributeValue("vdef32", vdef32);
	}

	public void setVdef33(String vdef33) {
		setAttributeValue("vdef33", vdef33);
	}

	public void setVdef34(String vdef34) {
		setAttributeValue("vdef34", vdef34);
	}

	public void setVdef35(String vdef35) {
		setAttributeValue("vdef35", vdef35);
	}

	public void setVdef36(String vdef36) {
		setAttributeValue("vdef36", vdef36);
	}

	public void setVdef37(String vdef37) {
		setAttributeValue("vdef37", vdef37);
	}

	public void setVdef38(String vdef38) {
		setAttributeValue("vdef38", vdef38);
	}

	public void setVdef39(String vdef39) {
		setAttributeValue("vdef39", vdef39);
	}

	public void setVdef40(String vdef40) {
		setAttributeValue("vdef40", vdef40);
	}
	// 41~60
	public void setVdef41(String vdef41) {
		setAttributeValue("vdef41", vdef41);
	}

	public void setVdef42(String vdef42) {
		setAttributeValue("vdef42", vdef42);
	}

	public void setVdef43(String vdef43) {
		setAttributeValue("vdef43", vdef43);
	}

	public void setVdef44(String vdef44) {
		setAttributeValue("vdef44", vdef44);
	}

	public void setVdef45(String vdef45) {
		setAttributeValue("vdef45", vdef45);
	}

	public void setVdef46(String vdef46) {
		setAttributeValue("vdef46", vdef46);
	}

	public void setVdef47(String vdef47) {
		setAttributeValue("vdef47", vdef47);
	}

	public void setVdef48(String vdef48) {
		setAttributeValue("vdef48", vdef48);
	}

	public void setVdef49(String vdef49) {
		setAttributeValue("vdef49", vdef49);
	}

	public void setVdef50(String vdef50) {
		setAttributeValue("vdef50", vdef50);
	}

	public void setVdef51(String vdef51) {
		setAttributeValue("vdef51", vdef51);
	}

	public void setVdef52(String vdef52) {
		setAttributeValue("vdef52", vdef52);
	}

	public void setVdef53(String vdef53) {
		setAttributeValue("vdef53", vdef53);
	}

	public void setVdef54(String vdef54) {
		setAttributeValue("vdef54", vdef54);
	}

	public void setVdef55(String vdef55) {
		setAttributeValue("vdef55", vdef55);
	}

	public void setVdef56(String vdef56) {
		setAttributeValue("vdef56", vdef56);
	}

	public void setVdef57(String vdef57) {
		setAttributeValue("vdef57", vdef57);
	}

	public void setVdef58(String vdef58) {
		setAttributeValue("vdef58", vdef58);
	}

	public void setVdef59(String vdef59) {
		setAttributeValue("vdef59", vdef59);
	}

	public void setVdef60(String vdef60) {
		setAttributeValue("vdef60", vdef60);
	}

	public void setVdef3(String vdef3) {
		setAttributeValue("vdef3", vdef3);
	}

	public void setVdef4(String vdef4) {
		setAttributeValue("vdef4", vdef4);
	}

	public void setVdef5(String vdef5) {
		setAttributeValue("vdef5", vdef5);
	}

	public void setVdef6(String vdef6) {
		setAttributeValue("vdef6", vdef6);
	}

	public void setVdef7(String vdef7) {
		setAttributeValue("vdef7", vdef7);
	}

	public void setVdef8(String vdef8) {
		setAttributeValue("vdef8", vdef8);
	}

	public void setVdef9(String vdef9) {
		setAttributeValue("vdef9", vdef9);
	}

	public void setVfrozenreason(String vfrozenreason) {
		setAttributeValue("vfrozenreason", vfrozenreason);
	}

	public void setVmemo(String vmemo) {
		setAttributeValue("vmemo", vmemo);
	}

	public void setVreason(String vreason) {
		setAttributeValue("vreason", vreason);
	}

	public void setVtrantypecode(String vtrantypecode) {
		setAttributeValue("vtrantypecode", vtrantypecode);
	}
}
