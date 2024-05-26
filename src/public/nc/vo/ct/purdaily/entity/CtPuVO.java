package nc.vo.ct.purdaily.entity;

import nc.vo.ct.entity.CtAbstractVO;
import nc.vo.ct.enumeration.CtEntity;
import nc.vo.pub.IVOMeta;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;
import nc.vo.pubapp.res.NCModule;

public class CtPuVO extends CtAbstractVO {
	public static final String BBRACKETORDER = "bbracketorder";
	public static final String BPROTSUPPLY = "bprotsupply";
	public static final String BPUBLISH = "bpublish";
	public static final String BSHOWLATEST = "bshowlatest";
	public static final String BSRCECMCT = "bsrcecmct";
	public static final String CVENDORID = "cvendorid";
	public static final String IPRICETYPE = "ipricetype";
	public static final String IRESPSTATUS = "irespstatus";
	public static final String MODIFYSTATUS = "modifystatus";
	public static final String PK_CT_PU = "pk_ct_pu";
	public static final String PK_PUBPSN = "pk_pubpsn";
	public static final String PK_PURCORP = "pk_purcorp";
	public static final String PK_RESPPSN = "pk_resppsn";
	public static final String TPUBTIME = "tpubtime";
	public static final String TRESPTIME = "tresptime";
	public static final String VREASON = "vreason";
	private static final String VDEF21 = "vdef21";
	private static final String VDEF22 = "vdef22";
	private static final String VDEF23 = "vdef23";
	private static final String VDEF24 = "vdef24";
	private static final String VDEF25 = "vdef25";
	private static final String VDEF26 = "vdef26";
	private static final String VDEF27 = "vdef27";
	private static final String VDEF28 = "vdef28";
	private static final String VDEF29 = "vdef29";
	private static final String VDEF30 = "vdef30";
	private static final String VDEF31 = "vdef31";
	private static final String VDEF32 = "vdef32";
	private static final String VDEF33 = "vdef33";
	private static final String VDEF34 = "vdef34";
	private static final String VDEF35 = "vdef35";
	private static final String VDEF36 = "vdef36";
	private static final String VDEF37 = "vdef37";
	private static final String VDEF38 = "vdef38";
	private static final String VDEF39 = "vdef39";
	private static final String VDEF40 = "vdef40";
	// 41~80
	private static final String VDEF41 = "vdef41";
	private static final String VDEF42 = "vdef42";
	private static final String VDEF43 = "vdef43";
	private static final String VDEF44 = "vdef44";
	private static final String VDEF45 = "vdef45";
	private static final String VDEF46 = "vdef46";
	private static final String VDEF47 = "vdef47";
	private static final String VDEF48 = "vdef48";
	private static final String VDEF49 = "vdef49";
	private static final String VDEF50 = "vdef50";
	private static final String VDEF51 = "vdef51";
	private static final String VDEF52 = "vdef52";
	private static final String VDEF53 = "vdef53";
	private static final String VDEF54 = "vdef54";
	private static final String VDEF55 = "vdef55";
	private static final String VDEF56 = "vdef56";
	private static final String VDEF57 = "vdef57";
	private static final String VDEF58 = "vdef58";
	private static final String VDEF59 = "vdef59";
	private static final String VDEF60 = "vdef60";
	private static final String VDEF61 = "vdef61";
	private static final String VDEF62 = "vdef62";
	private static final String VDEF63 = "vdef63";
	private static final String VDEF64 = "vdef64";
	private static final String VDEF65 = "vdef65";
	private static final String VDEF66 = "vdef66";
	private static final String VDEF67 = "vdef67";
	private static final String VDEF68 = "vdef68";
	private static final String VDEF69 = "vdef69";
	private static final String VDEF70 = "vdef70";
	private static final String VDEF71 = "vdef71";
	private static final String VDEF72 = "vdef72";
	private static final String VDEF73 = "vdef73";
	private static final String VDEF74 = "vdef74";
	private static final String VDEF75 = "vdef75";
	private static final String VDEF76 = "vdef76";
	private static final String VDEF77 = "vdef77";
	private static final String VDEF78 = "vdef78";
	private static final String VDEF79 = "vdef79";
	private static final String VDEF80 = "vdef80";
	private static final long serialVersionUID = -6057905148775268085L;

	public CtPuVO() {
	}

	public UFBoolean getBbracketOrder() {
		return (UFBoolean) getAttributeValue("bbracketorder");
	}

	public UFBoolean getBprotsupply() {
		return (UFBoolean) getAttributeValue("bprotsupply");
	}

	public UFBoolean getBpublish() {
		return (UFBoolean) getAttributeValue("bpublish");
	}

	public UFBoolean getBsc() {
		return (UFBoolean) getAttributeValue("bsc");
	}

	public UFBoolean getBshowLatest() {
		return (UFBoolean) getAttributeValue("bshowlatest");
	}

	public UFBoolean getBsrcecmct() {
		return (UFBoolean) getAttributeValue("bsrcecmct");
	}

	public String getCvendorid() {
		return (String) getAttributeValue("cvendorid");
	}

	public Integer getIpricetype() {
		return (Integer) getAttributeValue("ipricetype");
	}

	public Integer getIrespstatus() {
		return (Integer) getAttributeValue("irespstatus");
	}

	public Integer getBill_status() {
		return (Integer) getAttributeValue("fstatusflag");
	}

	public IVOMeta getMetaData() {
		IVOMeta meta = VOMetaFactory.getInstance().getVOMeta(
				NCModule.CT.getName().toLowerCase() + "."
						+ CtEntity.ct_pu.name());

		return meta;
	}

	public Integer getModifyStatus() {
		return (Integer) getAttributeValue("modifystatus");
	}

	public String getPk_ct_pu() {
		return (String) getAttributeValue("pk_ct_pu");
	}

	public String getPk_pubpsn() {
		return (String) getAttributeValue("pk_pubpsn");
	}

	public String getPk_purcorp() {
		return (String) getAttributeValue("pk_purcorp");
	}

	public String getPk_resppsn() {
		return (String) getAttributeValue("pk_resppsn");
	}

	public UFDateTime getTpubtime() {
		return (UFDateTime) getAttributeValue("tpubtime");
	}

	public UFDateTime getTresptime() {
		return (UFDateTime) getAttributeValue("tresptime");
	}

	public String getVreason() {
		return (String) getAttributeValue("vreason");
	}

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

	// 41~80
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

	public String getVdef61() {
		return (String) getAttributeValue("vdef61");
	}

	public String getVdef62() {
		return (String) getAttributeValue("vdef62");
	}

	public String getVdef63() {
		return (String) getAttributeValue("vdef63");
	}

	public String getVdef64() {
		return (String) getAttributeValue("vdef64");
	}

	public String getVdef65() {
		return (String) getAttributeValue("vdef65");
	}

	public String getVdef66() {
		return (String) getAttributeValue("vdef66");
	}

	public String getVdef67() {
		return (String) getAttributeValue("vdef67");
	}

	public String getVdef68() {
		return (String) getAttributeValue("vdef68");
	}

	public String getVdef69() {
		return (String) getAttributeValue("vdef69");
	}

	public String getVdef70() {
		return (String) getAttributeValue("vdef70");
	}

	public String getVdef71() {
		return (String) getAttributeValue("vdef71");
	}

	public String getVdef72() {
		return (String) getAttributeValue("vdef72");
	}

	public String getVdef73() {
		return (String) getAttributeValue("vdef73");
	}

	public String getVdef74() {
		return (String) getAttributeValue("vdef74");
	}

	public String getVdef75() {
		return (String) getAttributeValue("vdef75");
	}

	public String getVdef76() {
		return (String) getAttributeValue("vdef76");
	}

	public String getVdef77() {
		return (String) getAttributeValue("vdef77");
	}

	public String getVdef78() {
		return (String) getAttributeValue("vdef78");
	}

	public String getVdef79() {
		return (String) getAttributeValue("vdef79");
	}

	public String getVdef80() {
		return (String) getAttributeValue("vdef80");
	}

	// ---
	public void setBbracketOrder(UFBoolean bbracketorder) {
		setAttributeValue("bbracketorder", bbracketorder);
	}

	public void setBprotsupply(UFBoolean bprotsupply) {
		setAttributeValue("bprotsupply", bprotsupply);
	}

	public void setBpublish(UFBoolean bpublish) {
		setAttributeValue("bpublish", bpublish);
	}

	public void setBsc(UFBoolean bsc) {
		setAttributeValue("bsc", bsc);
	}

	public void setBshowLatest(UFBoolean bshowlatest) {
		setAttributeValue("bshowlatest", bshowlatest);
	}

	public void setBsrcecmct(UFBoolean bsrcecmct) {
		setAttributeValue("bsrcecmct", bsrcecmct);
	}

	public void setCvendorid(String cvendorid) {
		setAttributeValue("cvendorid", cvendorid);
	}

	public void setIpricetype(Integer ipricetype) {
		setAttributeValue("ipricetype", ipricetype);
	}

	public void setIrespstatus(Integer irespstatus) {
		setAttributeValue("irespstatus", irespstatus);
	}

	public void setModifyStatus(Integer modifystatus) {
		setAttributeValue("modifystatus", modifystatus);
	}

	public void setPk_ct_pu(String pk_ct_pu) {
		setAttributeValue("pk_ct_pu", pk_ct_pu);
	}

	public void setPk_pubpsn(String pk_pubpsn) {
		setAttributeValue("pk_pubpsn", pk_pubpsn);
	}

	public void setPk_purcorp(String pk_purcorp) {
		setAttributeValue("pk_purcorp", pk_purcorp);
	}

	public void setPk_resppsn(String pk_resppsn) {
		setAttributeValue("pk_resppsn", pk_resppsn);
	}

	public void setTpubtime(UFDateTime tpubtime) {
		setAttributeValue("tpubtime", tpubtime);
	}

	public void setTresptime(UFDateTime tresptime) {
		setAttributeValue("tresptime", tresptime);
	}

	public void setVreason(String vreason) {
		setAttributeValue("vreason", vreason);
	}

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
	
	// 41~80
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
	
	public void setVdef61(String vdef61) {
		setAttributeValue("vdef61", vdef61);
	}

	public void setVdef62(String vdef62) {
		setAttributeValue("vdef62", vdef62);
	}

	public void setVdef63(String vdef63) {
		setAttributeValue("vdef63", vdef63);
	}

	public void setVdef64(String vdef64) {
		setAttributeValue("vdef64", vdef64);
	}

	public void setVdef65(String vdef65) {
		setAttributeValue("vdef65", vdef65);
	}

	public void setVdef66(String vdef66) {
		setAttributeValue("vdef66", vdef66);
	}

	public void setVdef67(String vdef67) {
		setAttributeValue("vdef67", vdef67);
	}

	public void setVdef68(String vdef68) {
		setAttributeValue("vdef68", vdef68);
	}

	public void setVdef69(String vdef69) {
		setAttributeValue("vdef69", vdef69);
	}

	public void setVdef70(String vdef70) {
		setAttributeValue("vdef70", vdef70);
	}

	public void setVdef71(String vdef71) {
		setAttributeValue("vdef71", vdef71);
	}

	public void setVdef72(String vdef72) {
		setAttributeValue("vdef72", vdef72);
	}

	public void setVdef73(String vdef73) {
		setAttributeValue("vdef73", vdef73);
	}

	public void setVdef74(String vdef74) {
		setAttributeValue("vdef74", vdef74);
	}

	public void setVdef75(String vdef75) {
		setAttributeValue("vdef75", vdef75);
	}

	public void setVdef76(String vdef76) {
		setAttributeValue("vdef76", vdef76);
	}

	public void setVdef77(String vdef77) {
		setAttributeValue("vdef77", vdef77);
	}

	public void setVdef78(String vdef78) {
		setAttributeValue("vdef78", vdef78);
	}

	public void setVdef79(String vdef79) {
		setAttributeValue("vdef79", vdef79);
	}

	public void setVdef80(String vdef80) {
		setAttributeValue("vdef80", vdef80);
	}
}
