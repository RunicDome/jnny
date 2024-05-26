package nc.ws.intf;

import nc.vo.pub.SuperVO;

// 银行余额视图VO
public class YHYEBALLVO extends SuperVO {

	private static final long serialVersionUID = 1L;
	public static final String ZH = "zh";
	public static final String HM = "hm";
	public static final String ZHMC = "zhmc";
	public static final String YHLBBM = "yhlbbm";
	public static final String YHLB = "yhlb";
	public static final String PK_ORG = "pk_org";
	public static final String KHDWBM = "khdwbm";
	public static final String KHDW = "khdw";
	public static final String YE = "ye";
	public static final String YERQ = "yerq";
	public static final String BZBM = "bzbm";
	public static final String BZMC = "bzmc";
	public static final String ZLZT = "zlzt";
	public static final String SSJT = "ssjt";
	public static final String SSZZ = "sszz";
	public static final String ZJ = "zj";
	public static final String DR = "dr";

	private String zh;
	private String hm;
	private String zhmc;
	private String yhlbbm;
	private String yhlb;
	private String pk_org;
	private String khdwbm;
	private String khdw;
	private double ye;
	private String yerq;
	private String bzbm;
	private String bzmc;
	private String zlzt;
	private String ssjt;
	private String sszz;
	private String zj;
	private int dr = 0;

	public String getZh() {
		return zh;
	}

	public void setZh(String zh) {
		this.zh = zh;
	}

	public String getHm() {
		return hm;
	}

	public void setHm(String hm) {
		this.hm = hm;
	}

	public String getZhmc() {
		return zhmc;
	}

	public void setZhmc(String zhmc) {
		this.zhmc = zhmc;
	}

	public String getYhlbbm() {
		return yhlbbm;
	}

	public void setYhlbbm(String yhlbbm) {
		this.yhlbbm = yhlbbm;
	}

	public String getYhlb() {
		return yhlb;
	}

	public void setYhlb(String yhlb) {
		this.yhlb = yhlb;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public String getKhdwbm() {
		return khdwbm;
	}

	public void setKhdwbm(String khdwbm) {
		this.khdwbm = khdwbm;
	}

	public String getKhdw() {
		return khdw;
	}

	public void setKhdw(String khdw) {
		this.khdw = khdw;
	}

	public double getYe() {
		return ye;
	}

	public void setYe(double ye) {
		this.ye = ye;
	}

	public String getYerq() {
		return yerq;
	}

	public void setYerq(String yerq) {
		this.yerq = yerq;
	}

	public String getBzbm() {
		return bzbm;
	}

	public void setBzbm(String bzbm) {
		this.bzbm = bzbm;
	}

	public String getBzmc() {
		return bzmc;
	}

	public void setBzmc(String bzmc) {
		this.bzmc = bzmc;
	}

	public String getZlzt() {
		return zlzt;
	}

	public void setZlzt(String zlzt) {
		this.zlzt = zlzt;
	}

	public String getSsjt() {
		return ssjt;
	}

	public void setSsjt(String ssjt) {
		this.ssjt = ssjt;
	}

	public String getSszz() {
		return sszz;
	}

	public void setSszz(String sszz) {
		this.sszz = sszz;
	}

	public String getZj() {
		return zj;
	}

	public void setZj(String zj) {
		this.zj = zj;
	}

	public String getTableName() {
		return "v_erp_yhyeb_all";
	}

	public int getDr() {
		return dr;
	}

	public void setDr(int dr) {
		this.dr = dr;
	}

}
