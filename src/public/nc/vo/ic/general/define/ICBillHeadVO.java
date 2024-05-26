package nc.vo.ic.general.define;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;

public abstract class ICBillHeadVO extends SuperVO implements ICBillHeadEntity {
	private static final long serialVersionUID = 7561380251124521833L;

	public ICBillHeadVO() {
	}

	public UFBoolean getHasbalanced() {
		return (UFBoolean) getAttributeValue("hasbalanced");
	}

	public void setHasbalanced(UFBoolean hasBalanced) {
		setAttributeValue("hasbalanced", hasBalanced);
	}

	public String getApprover() {
		return (String) getAttributeValue("approver");
	}

	public String getCbizid() {
		return (String) getAttributeValue("cbizid");
	}

	public String getCcustomerid() {
		return (String) getAttributeValue("ccustomerid");
	}

	public String getCdptid() {
		return (String) getAttributeValue("cdptid");
	}

	public String getCdptvid() {
		return (String) getAttributeValue("cdptvid");
	}

	public String getCgeneralhid() {
		return (String) getAttributeValue("cgeneralhid");
	}

	public String getCorpoid() {
		return (String) getAttributeValue("corpoid");
	}

	public String getCorpvid() {
		return (String) getAttributeValue("corpvid");
	}

	public String getCostregionid() {
		return (String) getAttributeValue("costregionid");
	}

	public String getCothercalbodyoid() {
		return (String) getAttributeValue("cothercalbodyoid");
	}

	public String getCothercalbodyvid() {
		return (String) getAttributeValue("cothercalbodyvid");
	}

	public String getCotherwhid() {
		return (String) getAttributeValue("cotherwhid");
	}

	public UFDateTime getCreationtime() {
		return (UFDateTime) getAttributeValue("creationtime");
	}

	public String getCreator() {
		return (String) getAttributeValue("creator");
	}

	public String getCwarehouseid() {
		return (String) getAttributeValue("cwarehouseid");
	}

	public String getCwhsmanagerid() {
		return (String) getAttributeValue("cwhsmanagerid");
	}

	public UFDate getDbilldate() {
		return (UFDate) getAttributeValue("dbilldate");
	}

	public Integer getFbillflag() {
		return (Integer) getAttributeValue("fbillflag");
	}

	public Integer getIprintcount() {
		return nc.vo.ic.pub.util.NCBaseTypeUtils
				.toInteger(getAttributeValue("iprintcount"));
	}

	public UFDateTime getModifiedtime() {
		return (UFDateTime) getAttributeValue("modifiedtime");
	}

	public String getModifier() {
		return (String) getAttributeValue("modifier");
	}

	public UFDouble getNtotalnum() {
		return (UFDouble) getAttributeValue("ntotalnum");
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

	public String getBillmaker() {
		return (String) getAttributeValue("billmaker");
	}

	public String getPk_group() {
		return (String) getAttributeValue("pk_group");
	}

	public String getPk_measware() {
		return (String) getAttributeValue("pk_measware");
	}

	public String getPk_org() {
		return (String) getAttributeValue("pk_org");
	}

	public String getPk_org_v() {
		return (String) getAttributeValue("pk_org_v");
	}

	public Integer getPseudoColumn() {
		return (Integer) getAttributeValue("pseudocolumn");
	}

	public UFDate getTaudittime() {
		return (UFDate) getAttributeValue("taudittime");
	}

	public UFDateTime getTs() {
		return (UFDateTime) getAttributeValue("ts");
	}

	public String getVbillcode() {
		return (String) getAttributeValue("vbillcode");
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

	// 21~60
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

	public String getVnote() {
		return (String) getAttributeValue("vnote");
	}

	public String getVtrantypecode() {
		return (String) getAttributeValue("vtrantypecode");
	}

	public String getCtrantypeid() {
		return (String) getAttributeValue("ctrantypeid");
	}

	public void setApprover(String approver) {
		setAttributeValue("approver", approver);
	}

	public void setCbizid(String cbizid) {
		setAttributeValue("cbizid", cbizid);
	}

	public void setCcustomerid(String ccustomerid) {
		setAttributeValue("ccustomerid", ccustomerid);
	}

	public void setCdptid(String cdptid) {
		setAttributeValue("cdptid", cdptid);
	}

	public void setCdptvid(String cdptvid) {
		setAttributeValue("cdptvid", cdptvid);
	}

	public void setCgeneralhid(String cgeneralhid) {
		setAttributeValue("cgeneralhid", cgeneralhid);
	}

	public void setCorpoid(String corpoid) {
		setAttributeValue("corpoid", corpoid);
	}

	public void setCorpvid(String corpvid) {
		setAttributeValue("corpvid", corpvid);
	}

	public void setCostregionid(String costregionid) {
		setAttributeValue("costregionid", costregionid);
	}

	public void setCothercalbodyoid(String cothercalbodyoid) {
		setAttributeValue("cothercalbodyoid", cothercalbodyoid);
	}

	public void setCothercalbodyvid(String cothercalbodyvid) {
		setAttributeValue("cothercalbodyvid", cothercalbodyvid);
	}

	public void setCotherwhid(String cotherwhid) {
		setAttributeValue("cotherwhid", cotherwhid);
	}

	public void setCreationtime(UFDateTime creationtime) {
		setAttributeValue("creationtime", creationtime);
	}

	public void setCreator(String creator) {
		setAttributeValue("creator", creator);
	}

	public void setCwarehouseid(String cwarehouseid) {
		setAttributeValue("cwarehouseid", cwarehouseid);
	}

	public void setCwhsmanagerid(String cwhsmanagerid) {
		setAttributeValue("cwhsmanagerid", cwhsmanagerid);
	}

	public void setDbilldate(UFDate dbilldate) {
		setAttributeValue("dbilldate", dbilldate);
	}

	public void setFbillflag(Integer fbillflag) {
		setAttributeValue("fbillflag", fbillflag);
	}

	public void setIprintcount(Integer iprintcount) {
		setAttributeValue("iprintcount", iprintcount);
	}

	public void setModifiedtime(UFDateTime modifiedtime) {
		setAttributeValue("modifiedtime", modifiedtime);
	}

	public void setModifier(String modifier) {
		setAttributeValue("modifier", modifier);
	}

	public void setNtotalnum(UFDouble ntotalnum) {
		setAttributeValue("ntotalnum", ntotalnum);
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

	public void setBillmaker(String billmaker) {
		setAttributeValue("billmaker", billmaker);
	}

	public void setPk_group(String pk_group) {
		setAttributeValue("pk_group", pk_group);
	}

	public void setPk_measware(String pk_measware) {
		setAttributeValue("pk_measware", pk_measware);
	}

	public void setPk_org(String pk_org) {
		setAttributeValue("pk_org", pk_org);
	}

	public void setPk_org_v(String pk_org_v) {
		setAttributeValue("pk_org_v", pk_org_v);
	}

	public void setPseudoColumn(Integer value) {
		setAttributeValue("pseudocolumn", value);
	}

	public void setTaudittime(UFDate taudittime) {
		setAttributeValue("taudittime", taudittime);
	}

	public void setTs(UFDateTime ts) {
		setAttributeValue("ts", ts);
	}

	public void setVbillcode(String vbillcode) {
		setAttributeValue("vbillcode", vbillcode);
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

	// 21~60
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

	public void setVnote(String vnote) {
		setAttributeValue("vnote", vnote);
	}

	public void setVtrantypecode(String vtrantypecode) {
		setAttributeValue("vtrantypecode", vtrantypecode);
	}

	public void setCtrantypeid(String ctrantypeid) {
		setAttributeValue("ctrantypeid", ctrantypeid);
	}

	public void setDmakedate(UFDate dmakedate) {
		setAttributeValue("dmakedate", dmakedate);
	}

	public UFDate getDmakedate() {
		return (UFDate) getAttributeValue("dmakedate");
	}
}
