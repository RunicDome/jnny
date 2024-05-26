package nc.vo.pmbd.eps;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class EpsVO extends SuperVO {
	private static final long serialVersionUID = 1L;
	private String pk_eps;
	private String eps_code;
	private String eps_name;
	private String eps_name2;
	private String eps_name3;
	private String eps_name4;
	private String eps_name5;
	private String eps_name6;
	private Integer enablestate;
	private String pk_parent;
	private Integer eps_level;
	private String creator;
	private UFDateTime creationtime;
	private String modifier;
	private UFDateTime modifiedtime;
	private String pk_group;
	private String pk_org;
	private String innercode;
	private String def1;
	private String def2;
	private String def3;
	private String def4;
	private String def5;
	private String def6;
	private String def7;
	private String def8;
	private String def9;
	private String def10;
	private String def11;
	private String def12;
	private String def13;
	private String def14;
	private String def15;
	private String def16;
	private String def17;
	private String def18;
	private String def19;
	private String def20;
	private String def21;
	private String def22;
	private String def23;
	private String def24;
	private String def25;
	private String def26;
	private String def27;
	private String def28;
	private String def29;
	private String def30;
	
	
	private UFBoolean upload_flag;
	private Integer dr = Integer.valueOf(0);

	private UFDateTime ts;

	public static final String PK_EPS = "pk_eps";

	public static final String EPS_CODE = "eps_code";

	public static final String EPS_NAME = "eps_name";

	public static final String EPS_NAME2 = "eps_name2";
	public static final String EPS_NAME3 = "eps_name3";
	public static final String EPS_NAME4 = "eps_name4";
	public static final String EPS_NAME5 = "eps_name5";
	public static final String EPS_NAME6 = "eps_name6";
	public static final String ENABLESTATE = "enablestate";
	public static final String PK_PARENT = "pk_parent";
	public static final String EPS_LEVEL = "eps_level";
	public static final String CREATOR = "creator";
	public static final String CREATIONTIME = "creationtime";
	public static final String MODIFIER = "modifier";
	public static final String MODIFIEDTIME = "modifiedtime";
	public static final String PK_GROUP = "pk_group";
	public static final String PK_ORG = "pk_org";
	public static final String INNERCODE = "innercode";
	public static final String UPLOAD_FLAG = "upload_flag";

	public static final String DEF1 = "def1";
	public static final String DEF2 = "def2";
	public static final String DEF3 = "def3";
	public static final String DEF4 = "def4";
	public static final String DEF5 = "def5";
	public static final String DEF6 = "def6";
	public static final String DEF7 = "def7";
	public static final String DEF8 = "def8";
	public static final String DEF9 = "def9";
	public static final String DEF10 = "def10";
	public static final String DEF11 = "def11";
	public static final String DEF12 = "def12";
	public static final String DEF13 = "def13";
	public static final String DEF14 = "def14";
	public static final String DEF15 = "def15";
	public static final String DEF16 = "def16";
	public static final String DEF17 = "def17";
	public static final String DEF18 = "def18";
	public static final String DEF19 = "def19";
	public static final String DEF20 = "def20";
	public static final String DEF21 = "def21";
	public static final String DEF22 = "def22";
	public static final String DEF23 = "def23";
	public static final String DEF24 = "def24";
	public static final String DEF25 = "def25";
	public static final String DEF26 = "def26";
	public static final String DEF27 = "def27";
	public static final String DEF28 = "def28";
	public static final String DEF29 = "def29";
	public static final String DEF30 = "def30";

	public String getDef11() {
		return def11;
	}

	public void setDef11(String def11) {
		this.def11 = def11;
	}

	public String getDef12() {
		return def12;
	}

	public void setDef12(String def12) {
		this.def12 = def12;
	}

	public String getDef13() {
		return def13;
	}

	public void setDef13(String def13) {
		this.def13 = def13;
	}

	public String getDef14() {
		return def14;
	}

	public void setDef14(String def14) {
		this.def14 = def14;
	}

	public String getDef15() {
		return def15;
	}

	public void setDef15(String def15) {
		this.def15 = def15;
	}

	public String getDef16() {
		return def16;
	}

	public void setDef16(String def16) {
		this.def16 = def16;
	}

	public String getDef17() {
		return def17;
	}

	public void setDef17(String def17) {
		this.def17 = def17;
	}

	public String getDef18() {
		return def18;
	}

	public void setDef18(String def18) {
		this.def18 = def18;
	}

	public String getDef19() {
		return def19;
	}

	public void setDef19(String def19) {
		this.def19 = def19;
	}

	public String getDef20() {
		return def20;
	}

	public void setDef20(String def20) {
		this.def20 = def20;
	}

	public String getDef21() {
		return def21;
	}

	public void setDef21(String def21) {
		this.def21 = def21;
	}

	public String getDef22() {
		return def22;
	}

	public void setDef22(String def22) {
		this.def22 = def22;
	}

	public String getDef23() {
		return def23;
	}

	public void setDef23(String def23) {
		this.def23 = def23;
	}

	public String getDef24() {
		return def24;
	}

	public void setDef24(String def24) {
		this.def24 = def24;
	}

	public String getDef25() {
		return def25;
	}

	public void setDef25(String def25) {
		this.def25 = def25;
	}

	public String getDef26() {
		return def26;
	}

	public void setDef26(String def26) {
		this.def26 = def26;
	}

	public String getDef27() {
		return def27;
	}

	public void setDef27(String def27) {
		this.def27 = def27;
	}

	public String getDef28() {
		return def28;
	}

	public void setDef28(String def28) {
		this.def28 = def28;
	}

	public String getDef29() {
		return def29;
	}

	public void setDef29(String def29) {
		this.def29 = def29;
	}

	public String getDef30() {
		return def30;
	}

	public void setDef30(String def30) {
		this.def30 = def30;
	}

	public String getPk_eps() {
		return this.pk_eps;
	}

	public void setPk_eps(String newPk_eps) {
		this.pk_eps = newPk_eps;
	}

	public String getEps_code() {
		return this.eps_code;
	}

	public void setEps_code(String newEps_code) {
		this.eps_code = newEps_code;
	}

	public String getEps_name() {
		return this.eps_name;
	}

	public void setEps_name(String newEps_name) {
		this.eps_name = newEps_name;
	}

	public String getEps_name2() {
		return this.eps_name2;
	}

	public void setEps_name2(String newEps_name2) {
		this.eps_name2 = newEps_name2;
	}

	public String getEps_name3() {
		return this.eps_name3;
	}

	public void setEps_name3(String newEps_name3) {
		this.eps_name3 = newEps_name3;
	}

	public String getEps_name4() {
		return this.eps_name4;
	}

	public void setEps_name4(String newEps_name4) {
		this.eps_name4 = newEps_name4;
	}

	public String getEps_name5() {
		return this.eps_name5;
	}

	public void setEps_name5(String newEps_name5) {
		this.eps_name5 = newEps_name5;
	}

	public String getEps_name6() {
		return this.eps_name6;
	}

	public void setEps_name6(String newEps_name6) {
		this.eps_name6 = newEps_name6;
	}

	public Integer getEnablestate() {
		return this.enablestate;
	}

	public void setEnablestate(Integer newEnablestate) {
		this.enablestate = newEnablestate;
	}

	public String getPk_parent() {
		return this.pk_parent;
	}

	public void setPk_parent(String newPk_parent) {
		this.pk_parent = newPk_parent;
	}

	public Integer getEps_level() {
		return this.eps_level;
	}

	public void setEps_level(Integer newEps_level) {
		this.eps_level = newEps_level;
	}

	public String getCreator() {
		return this.creator;
	}

	public void setCreator(String newCreator) {
		this.creator = newCreator;
	}

	public UFDateTime getCreationtime() {
		return this.creationtime;
	}

	public void setCreationtime(UFDateTime newCreationtime) {
		this.creationtime = newCreationtime;
	}

	public String getModifier() {
		return this.modifier;
	}

	public void setModifier(String newModifier) {
		this.modifier = newModifier;
	}

	public UFDateTime getModifiedtime() {
		return this.modifiedtime;
	}

	public void setModifiedtime(UFDateTime newModifiedtime) {
		this.modifiedtime = newModifiedtime;
	}

	public String getPk_group() {
		return this.pk_group;
	}

	public void setPk_group(String newPk_group) {
		this.pk_group = newPk_group;
	}

	public String getPk_org() {
		return this.pk_org;
	}

	public void setPk_org(String newPk_org) {
		this.pk_org = newPk_org;
	}

	public String getInnercode() {
		return this.innercode;
	}

	public void setInnercode(String newInnercode) {
		this.innercode = newInnercode;
	}

	public UFBoolean getUpload_flag() {
		return this.upload_flag;
	}

	public void setUpload_flag(UFBoolean newUpload_flag) {
		this.upload_flag = newUpload_flag;
	}

	public Integer getDr() {
		return this.dr;
	}

	public void setDr(Integer newDr) {
		this.dr = newDr;
	}

	public UFDateTime getTs() {
		return this.ts;
	}

	public void setTs(UFDateTime newTs) {
		this.ts = newTs;
	}

	public String getDef1() {
		return def1;
	}

	public void setDef1(String def1) {
		this.def1 = def1;
	}

	public String getDef2() {
		return def2;
	}

	public void setDef2(String def2) {
		this.def2 = def2;
	}

	public String getDef3() {
		return def3;
	}

	public void setDef3(String def3) {
		this.def3 = def3;
	}

	public String getDef4() {
		return def4;
	}

	public void setDef4(String def4) {
		this.def4 = def4;
	}

	public String getDef5() {
		return def5;
	}

	public void setDef5(String def5) {
		this.def5 = def5;
	}

	public String getDef6() {
		return def6;
	}

	public void setDef6(String def6) {
		this.def6 = def6;
	}

	public String getDef7() {
		return def7;
	}

	public void setDef7(String def7) {
		this.def7 = def7;
	}

	public String getDef8() {
		return def8;
	}

	public void setDef8(String def8) {
		this.def8 = def8;
	}

	public String getDef9() {
		return def9;
	}

	public void setDef9(String def9) {
		this.def9 = def9;
	}

	public String getDef10() {
		return def10;
	}

	public void setDef10(String def10) {
		this.def10 = def10;
	}

	public String getParentPKFieldName() {
		return null;
	}

	public String getPKFieldName() {
		return "pk_eps";
	}

	public String getTableName() {
		return "pm_eps";
	}

	public static String getDefaultTableName() {
		return "pm_eps";
	}

	public EpsVO() {
	}

	public IVOMeta getMetaData() {
		IVOMeta meta = VOMetaFactory.getInstance().getVOMeta("pmbd.EpsVO");
		return meta;
	}
}
