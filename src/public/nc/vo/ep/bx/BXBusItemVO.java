package nc.vo.ep.bx;

import java.util.ArrayList;

import nc.vo.pub.BeanHelper;
import nc.vo.pub.IVOMeta;
import nc.vo.pub.NullFieldException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * 借款报销表体业务信息VO
 * 
 * @author twei modified by chendya since v6.1
 *         将财务行VO删除，将财务行所有有用字段移植到业务行(报销单早些时候的版本是没有表体行的，所以特意构造了财务行金额传结算，会计平台，预算等)
 * 
 *         nc.vo.ep.bx.BXBusItemVO
 */
public class BXBusItemVO extends SuperVO {

	private static final long serialVersionUID = -5576693230695877687L;

	private static String getErromsg() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
			"UPP2011-000281")/*
							 * @res "表体下列字段不能为空:\n"
							 */;
    }
	
	private String pk_fprelation;
	
	private String fpdm;
	
	private String fphm;

	// ---------------------------------begin added by lvhj
	/**
	 * 回写费用申请单-冲销明细数据包装使用，冲销行中的借款单明细pk
	 */
	private String jk_busitemPK;
	/**
	 * 回写费用申请单-冲销明细数据包装使用，冲销行中的报销单明细pk
	 */
	private String bx_busitemPK;
	

	// 增加项目管理、责任会计支持的固定业务字段---------------------------------

	public static final String PK_FPRELATION = "pk_fprelation";
	
	public static final String PK_FPDM = "fpdm";
	
	public static final String PK_FPHM = "fphm";
	
	/**
	 * 成本中心
	 */
	public static final String PK_RESACOSTCENTER = "pk_resacostcenter";
	/**
	 * 核算要素
	 */
	public static final String PK_CHECKELE = "pk_checkele";
	/**
	 * 利润中心
	 */
	public static final String PK_PCORG = "pk_pcorg";
	/**
	 * 利润中心版本化
	 */
	public static final String PK_PCORG_V = "pk_pcorg_v";
	/**
	 * 项目
	 */
	public static final String JOBID = "jobid";
	/**
	 * 项目任务
	 */
	public static final String PROJECTTASK = "projecttask";
	/**
	 * 费用申请单
	 */
	public static final String PK_ITEM = "pk_item";
	/**
	 * 费用申请单明细
	 */
	public static final String PK_MTAPP_DETAIL = "pk_mtapp_detail";
	/**
	 * 来源交易类型
	 */
	public static final String SRCBILLTYPE = "srcbilltype";
	/**
	 * 来源类型，默认为费用申请单
	 */
	public static final String SRCTYPE = "srctype";
	
	public static final String FCTNO = "fctno";
	private String pk_resacostcenter;// 成本中心
	private String pk_checkele; // 核算要素
	private String pk_pcorg; // 利润中心
	private String pk_pcorg_v;// 利润中心版本化
	private String projecttask;// 项目任务
	//ehp2新增字段
	private String dwbm;//报销人单位
	private String deptid; //报销人部门
	//private String bxr; //报销人
	public Integer paytarget; //收款对象
	private String receiver;//收款人
	private String skyhzh ;//个人银行帐户
	private String hbbm ; //供应商
	private String customer ;//客户
	private String custaccount;//客商银行帐户
	private String freecust ; //散户
	private String freeaccount;//散户银行帐户
	private String fctno;//合同号
	
	
	// CRM使用，用来控制生成的申请单
	private java.lang.String pk_crmdetail;
	
	public static final String PK_CRMDETAIL = "pk_crmdetail";
	
	public String getFctno(){
		return fctno;
	}
	
	public void setFctno(String fctno){
		this.fctno = fctno;
	}

	public Integer getPaytarget() {
		return paytarget;
	}

	public void setPaytarget(Integer paytarget) {
		this.paytarget = paytarget;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getSkyhzh() {
		return skyhzh;
	}

	public void setSkyhzh(String skyhzh) {
		this.skyhzh = skyhzh;
	}

	public String getHbbm() {
		return hbbm;
	}

	public void setHbbm(String hbbm) {
		this.hbbm = hbbm;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getCustaccount() {
		return custaccount;
	}

	public void setCustaccount(String custaccount) {
		this.custaccount = custaccount;
	}

	public String getFreecust() {
		return freecust;
	}

	public void setFreecust(String freecust) {
		this.freecust = freecust;
	}

	public String getFreeaccount() {
		return freeaccount;
	}

	public void setFreeaccount(String freeaccount) {
		this.freeaccount = freeaccount;
	}

	public String getDwbm() {
		return dwbm;
	}

	public void setDwbm(String dwbm) {
		this.dwbm = dwbm;
	}

	public String getDeptid() {
		return deptid;
	}

	public void setDeptid(String deptid) {
		this.deptid = deptid;
	}

	public String getPk_bxcontrast() {
		return pk_bxcontrast;
	}

	public void setPk_bxcontrast(String pkBxcontrast) {
		pk_bxcontrast = pkBxcontrast;
	}

	private String pk_item;
	private String pk_mtapp_detail;
	/**
	 * 来源单据类型
	 */
	private String srcbilltype;
	private String srctype;
	// 拉单相关的字段
	public static final String[] MTAPP_FIELDS = new String[] { PK_ITEM, SRCBILLTYPE, SRCTYPE };

	private String pk_bxcontrast;// 冲销行pk

	// ---------------------------------begin added by chendya
	// 移植财务行---------------------------------
	public static final String[] MONEY_FIELDS = new String[] { "ybje", "bbje", "ybye", "bbye", "hkybje", "hkbbje",
			"zfybje", "zfbbje", "cjkybje", "cjkbbje" };
	public static final String YJYE = "yjye";
	public static final String YBYE = "ybye";
	public static final String BBYE = "bbye";
	public static final String YBJE = "ybje";
	public static final String BBJE = "bbje";

	public static final String CJKYBJE = "cjkybje";
	public static final String CJKBBJE = "cjkbbje";

	public static final String ZFYBJE = "zfybje";
	public static final String ZFBBJE = "zfbbje";

	public static final String HKYBJE = "hkybje";
	public static final String HKBBJE = "hkbbje";

	public static final String FYYBJE = "fyybje";
	public static final String FYBBJE = "fybbje";

	
	//65-ehp1新增加字段
	public static final String TAX_AMOUNT = "tax_amount";
	public static final String VAT_AMOUNT = "vat_amount";
	public static final String TNI_AMOUNT = "tni_amount";
	public static final String ORGTAX_AMOUNT = "orgtax_amount";
	public static final String ORGVAT_AMOUNT = "orgvat_amount";
	public static final String ORGTNI_AMOUNT = "orgtni_amount";
	public static final String GROUPTAX_AMOUNT = "grouptax_amount";
	public static final String GROUPVAT_AMOUNT = "groupvat_amount";
	public static final String GROUPTNI_AMOUNT = "grouptni_amount";
	public static final String GLOBALTAX_AMOUNT = "globaltax_amount";
	public static final String GLOBALVAT_AMOUNT = "globalvat_amount";
	public static final String GLOBALTNI_AMOUNT = "globaltni_amount";
	
	/**
	 * 全局本币金额
	 */
	public static final String GLOBALBBJE = "globalbbje";
	/**
	 * 全局本币余额
	 */
	public static final String GLOBALBBYE = "globalbbye";
	/**
	 * 全局还款本币金额
	 */
	public static final String GLOBALHKBBJE = "globalhkbbje";
	/**
	 * 全局支付本币金额
	 */
	public static final String GLOBALZFBBJE = "globalzfbbje";
	/**
	 * 全局冲借款本币金额
	 */
	public static final String GLOBALCJKBBJE = "globalcjkbbje";
	/**
	 * 集团本币金额
	 */
	public static final String GROUPBBJE = "groupbbje";
	/**
	 * 集团本币余额
	 */
	public static final String GROUPBBYE = "groupbbye";
	/**
	 * 集团还款本币金额
	 */
	public static final String GROUPHKBBJE = "grouphkbbje";
	/**
	 * 集团支付本币金额
	 */
	public static final String GROUPZFBBJE = "groupzfbbje";
	/**
	 * 集团冲借款本币金额
	 */
	public static final String GROUPCJKBBJE = "groupcjkbbje";

	/**
	 * 结算信息表体前缀
	 */
	public static String SETTLE_BODY_PREFIX = "fb.";
	
	/**
	 * 结算信息表体前缀
	 */
	public static String SELECTED = "selected";

	public String cashproj;
	public String jkbxr;
	public String jobid;
	public String cashitem;
	public String pk_proline;
	
	public String getPk_proline() {
		return pk_proline;
	}

	public void setPk_proline(String pkProline) {
		pk_proline = pkProline;
	}

	public String getPk_brand() {
		return pk_brand;
	}

	public void setPk_brand(String pkBrand) {
		pk_brand = pkBrand;
	}

	public String pk_brand;

	public UFDouble hkbbje;
	public UFDouble hkybje;

	public UFDouble cjkbbje;
	public UFDouble cjkybje;

	public UFDouble zfybje;
	public UFDouble zfbbje;

	public UFDouble ybye;
	public UFDouble bbye;

	/**
	 * 借款单使用的预计余额
	 */
	public UFDouble yjye;

	// 新增计算属性
	public UFBoolean selected;

	public UFBoolean getSelected() {
		return selected;
	}

	public void setSelected(UFBoolean selected) {
		this.selected = selected;
	}

	/**
	 * 原币金额
	 */
	public UFDouble ybje;
	/**
	 * 本币金额
	 */
	public UFDouble bbje;
	
	
	//65-ehp1新增加字段
	public UFDouble tax_amount;//税金金额
	public UFDouble vat_amount;//含税金额
	public UFDouble tni_amount;//不含税金额
	public UFDouble orgtax_amount;//组织税金本币金额
	public UFDouble orgvat_amount;//组织含税本币金额
	public UFDouble orgtni_amount;//组织不含税本币金额
	public UFDouble grouptax_amount;//集团税金本币金额
	public UFDouble groupvat_amount;//集团含税本币金额
	public UFDouble grouptni_amount;//集团不含税本币金额
	public UFDouble globaltax_amount;//全局税金本币金额
	public UFDouble globalvat_amount;//全局含税本币金额
	public UFDouble globaltni_amount;//全局不含税本币金额

	// v6.0 增加全局，集团币种金额
	public UFDouble globalbbje;
	public UFDouble globalbbye;
	public UFDouble globalhkbbje;
	public UFDouble globalzfbbje;
	public UFDouble globalcjkbbje;
	public UFDouble groupbbje;
	public UFDouble groupbbye;
	public UFDouble grouphkbbje;
	public UFDouble groupzfbbje;
	public UFDouble groupcjkbbje;

	/**
	 * 结算信息表体VO
	 */
	private SuperVO settleBodyVO = null;

	public SuperVO getSettleBodyVO() {
		return settleBodyVO;
	}

	public void setSettleBodyVO(SuperVO settleBodyVO) {
		this.settleBodyVO = settleBodyVO;
	}

	public UFDouble getBbje() {
		return bbje;
	}

	public void setBbje(UFDouble bbje) {
		this.bbje = bbje;
	}

	public UFDouble getBbye() {
		return bbye;
	}

	public void setBbye(UFDouble bbye) {
		this.bbye = bbye;
	}

	public UFDouble getCjkbbje() {
		return cjkbbje;
	}

	public void setCjkbbje(UFDouble cjkbbje) {
		this.cjkbbje = cjkbbje;
	}

	public UFDouble getCjkybje() {
		return cjkybje;
	}

	public void setCjkybje(UFDouble cjkybje) {
		this.cjkybje = cjkybje;
	}

	public UFDouble getHkbbje() {
		return hkbbje;
	}

	public void setHkbbje(UFDouble hkbbje) {
		this.hkbbje = hkbbje;
	}

	public UFDouble getHkybje() {
		return hkybje;
	}

	public void setHkybje(UFDouble hkybje) {
		this.hkybje = hkybje;
	}

	public String getJkbxr() {
		return jkbxr;
	}

	public void setJkbxr(String jkbxr) {
		this.jkbxr = jkbxr;
	}

	public String getJobid() {
		return jobid;
	}

	public void setJobid(String jobid) {
		this.jobid = jobid;
	}

	public UFDouble getYbje() {
		return ybje;
	}

	public void setYbje(UFDouble ybje) {
		this.ybje = ybje;
	}

	public UFDouble getYbye() {
		return ybye;
	}

	public void setYbye(UFDouble ybye) {
		this.ybye = ybye;
	}

	public UFDouble getYjye() {
		return yjye;
	}

	public void setYjye(UFDouble yjye) {
		this.yjye = yjye;
	}

	public UFDouble getZfbbje() {
		return zfbbje;
	}

	public void setZfbbje(UFDouble zfbbje) {
		this.zfbbje = zfbbje;
	}

	public UFDouble getZfybje() {
		return zfybje;
	}

	public void setZfybje(UFDouble zfybje) {
		this.zfybje = zfybje;
	}

	public String getCashproj() {
		return cashproj;
	}

	public void setCashproj(String cashproj) {
		this.cashproj = cashproj;
	}

	/**
	 * 属性globalbbje的Getter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGlobalbbje() {
		return globalbbje;
	}

	/**
	 * 属性globalbbje的Setter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @param newGlobalbbje
	 *            UFDouble
	 */
	public void setGlobalbbje(UFDouble newGlobalbbje) {
		this.globalbbje = newGlobalbbje;
	}

	/**
	 * 属性globalbbye的Getter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGlobalbbye() {
		return globalbbye;
	}

	/**
	 * 属性globalbbye的Setter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @param newGlobalbbye
	 *            UFDouble
	 */
	public void setGlobalbbye(UFDouble newGlobalbbye) {
		this.globalbbye = newGlobalbbye;
	}

	/**
	 * 属性globalhkbbje的Getter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGlobalhkbbje() {
		return globalhkbbje;
	}

	/**
	 * 属性globalhkbbje的Setter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @param newGlobalhkbbje
	 *            UFDouble
	 */
	public void setGlobalhkbbje(UFDouble newGlobalhkbbje) {
		this.globalhkbbje = newGlobalhkbbje;
	}

	/**
	 * 属性globalzfbbje的Getter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGlobalzfbbje() {
		return globalzfbbje;
	}

	/**
	 * 属性globalzfbbje的Setter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @param newGlobalzfbbje
	 *            UFDouble
	 */
	public void setGlobalzfbbje(UFDouble newGlobalzfbbje) {
		this.globalzfbbje = newGlobalzfbbje;
	}

	/**
	 * 属性globalcjkbbje的Getter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGlobalcjkbbje() {
		return globalcjkbbje;
	}

	/**
	 * 属性globalcjkbbje的Setter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @param newGlobalcjkbbje
	 *            UFDouble
	 */
	public void setGlobalcjkbbje(UFDouble newGlobalcjkbbje) {
		this.globalcjkbbje = newGlobalcjkbbje;
	}

	/**
	 * 属性groupbbje的Getter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGroupbbje() {
		return groupbbje;
	}

	/**
	 * 属性groupbbje的Setter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @param newGroupbbje
	 *            UFDouble
	 */
	public void setGroupbbje(UFDouble newGroupbbje) {
		this.groupbbje = newGroupbbje;
	}

	/**
	 * 属性groupbbye的Getter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGroupbbye() {
		return groupbbye;
	}

	/**
	 * 属性groupbbye的Setter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @param newGroupbbye
	 *            UFDouble
	 */
	public void setGroupbbye(UFDouble newGroupbbye) {
		this.groupbbye = newGroupbbye;
	}

	/**
	 * 属性grouphkbbje的Getter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGrouphkbbje() {
		return grouphkbbje;
	}

	/**
	 * 属性grouphkbbje的Setter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @param newGrouphkbbje
	 *            UFDouble
	 */
	public void setGrouphkbbje(UFDouble newGrouphkbbje) {
		this.grouphkbbje = newGrouphkbbje;
	}

	/**
	 * 属性groupzfbbje的Getter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGroupzfbbje() {
		return groupzfbbje;
	}

	/**
	 * 属性groupzfbbje的Setter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @param newGroupzfbbje
	 *            UFDouble
	 */
	public void setGroupzfbbje(UFDouble newGroupzfbbje) {
		this.groupzfbbje = newGroupzfbbje;
	}

	/**
	 * 属性groupcjkbbje的Getter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGroupcjkbbje() {
		return groupcjkbbje;
	}

	/**
	 * 属性groupcjkbbje的Setter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @param newGroupcjkbbje
	 *            UFDouble
	 */
	public void setGroupcjkbbje(UFDouble newGroupcjkbbje) {
		this.groupcjkbbje = newGroupcjkbbje;
	}

	// ---------------------------------end by chendya
	// 移植财务行---------------------------------

	private static String getFieldName(String field) {
		if (field.equals(AMOUNT))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000280")/*
																								 * @
																								 * res
																								 * "原币金额"
																								 */;
		else if (field.equals(YBJE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000280")/*
																								 * @
																								 * res
																								 * "原币金额"
																								 */;
		return field;
	}

	public String defitem8;

	public String defitem2;

	public String defitem21;

	public String defitem36;

	public String defitem40;

	public String defitem23;

	public String defitem12;

	public String defitem31;

	public String defitem34;

	public String defitem3;

	public String tablecode;

	public String defitem11;

	public String defitem17;

	public String defitem28;

	public String defitem33;

	public String defitem48;

	public String pk_jkbx;

	public String defitem45;

	public String defitem1;

	public String defitem50;

	public String defitem42;

	public String defitem35;

	public String defitem30;

	public String defitem41;

	public String defitem9;

	public String defitem29;

	public String defitem5;

	public String defitem19;

	public String defitem14;

	public String defitem49;

	public String defitem26;

	public String defitem43;

	public String defitem46;

	public String defitem4;

	public String defitem24;

	public String defitem16;

	public String defitem38;

	public String defitem27;

	public String defitem22;

	public String defitem6;

	public String pk_busitem;

	public String defitem39;

	public String defitem47;

	public String defitem25;

	public String defitem44;

	public String defitem18;

	public String defitem10;

	public String defitem32;

	public String defitem37;

	public String defitem15;

	public String defitem20;

	public String defitem13;

	public String defitem7;

	public Integer rowno;

	public static final String DEFITEM8 = "defitem8";

	public static final String DEFITEM2 = "defitem2";

	public static final String DEFITEM21 = "defitem21";

	public static final String DEFITEM36 = "defitem36";

	public static final String DEFITEM40 = "defitem40";

	public static final String DEFITEM23 = "defitem23";

	public static final String DEFITEM12 = "defitem12";

	public static final String DEFITEM31 = "defitem31";

	public static final String DEFITEM34 = "defitem34";

	public static final String DEFITEM3 = "defitem3";

	public static final String TABLECODE = "tablecode";

	public static final String DEFITEM11 = "defitem11";

	public static final String DEFITEM17 = "defitem17";

	public static final String DEFITEM28 = "defitem28";

	public static final String DEFITEM33 = "defitem33";

	public static final String DEFITEM48 = "defitem48";

	public static final String PK_JKBX = "pk_jkbx";

	public static final String DEFITEM45 = "defitem45";

	public static final String DEFITEM1 = "defitem1";

	public static final String DEFITEM50 = "defitem50";

	public static final String DEFITEM42 = "defitem42";

	public static final String DEFITEM35 = "defitem35";

	public static final String DEFITEM30 = "defitem30";

	public static final String DEFITEM41 = "defitem41";

	public static final String DEFITEM9 = "defitem9";

	public static final String DEFITEM29 = "defitem29";

	public static final String DEFITEM5 = "defitem5";

	public static final String DEFITEM19 = "defitem19";

	public static final String DEFITEM14 = "defitem14";

	public static final String DEFITEM49 = "defitem49";

	public static final String DEFITEM26 = "defitem26";

	public static final String DEFITEM43 = "defitem43";

	public static final String DEFITEM46 = "defitem46";

	public static final String DEFITEM4 = "defitem4";

	public static final String DEFITEM24 = "defitem24";

	public static final String DEFITEM16 = "defitem16";

	public static final String DEFITEM38 = "defitem38";

	public static final String DEFITEM27 = "defitem27";

	public static final String DEFITEM22 = "defitem22";

	public static final String DEFITEM6 = "defitem6";

	public static final String PK_BUSITEM = "pk_busitem";

	public static final String DEFITEM39 = "defitem39";

	public static final String DEFITEM47 = "defitem47";

	public static final String DEFITEM25 = "defitem25";

	public static final String DEFITEM44 = "defitem44";

	public static final String DEFITEM18 = "defitem18";

	public static final String DEFITEM10 = "defitem10";

	public static final String DEFITEM32 = "defitem32";

	public static final String DEFITEM37 = "defitem37";

	public static final String DEFITEM15 = "defitem15";

	public static final String DEFITEM20 = "defitem20";

	public static final String DEFITEM13 = "defitem13";

	public static final String DEFITEM7 = "defitem7";

	public static final String AMOUNT = "amount";

	public static final String SZXMID = "szxmid";

	public static final String ROWNO = "rowno";
	
	public static final String PK_PROLINE = "pk_proline";
	public static final String PK_BRAND = "pk_brand";
	public static final String DWBM = "dwbm";
	public static final String DEPTID = "deptid";
	public static final String JKBXR	 = "jkbxr";
	public static final String PAYTARGET = "paytarget";
	public static final String RECEIVER = "receiver";
	public static final String SKYHZH = "skyhzh";
	public static final String HBBM = "hbbm";
	public static final String CUSTOMER = "customer";
	public static final String CUSTACCOUNT = "custaccount";
	public static final String FREECUST = "freecust";
	public static final String FREEACCOUNT= "freeaccount";

	public static final String SZXMMC = "szxmmc";
	public static final String PK_EXPENSETYPE = "pk_expensetype";
	public static final String PK_REIMTYPE = "pk_reimtype";
	private UFDateTime ts;
	private Integer dr;
	private String szxmid;
	private String pk_reimtype;

	public String getPk_reimtype() {
		return pk_reimtype;
	}

	public void setPk_reimtype(String pk_reimtype) {
		this.pk_reimtype = pk_reimtype;
	}

	public String getSzxmid() {
		return szxmid;
	}

	public void setSzxmid(String szxmid) {
		this.szxmid = szxmid;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public UFDateTime getTs() {
		return ts;
	}

	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	public boolean isNullItem() {
		boolean isNull = true;
		String[] attributeNames = getAttributeNames();
		for (String name : attributeNames) {
			if (getAttributeValue(name) != null) {
				isNull = false;
				break;
			}
		}
		return isNull;
	}

	/**
	 * 原币金额
	 */
	public UFDouble amount;

	/**
	 * 属性defitem8的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public String getDefitem8() {
		return defitem8;
	}

	/**
	 * 属性defitem8的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem8
	 *            String
	 */
	public void setDefitem8(String newDefitem8) {

		defitem8 = newDefitem8;
	}

	/**
	 * 属性defitem2的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem2() {
		return defitem2;
	}

	/**
	 * 属性defitem2的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem2
	 *            String
	 */
	public void setDefitem2(String newDefitem2) {

		defitem2 = newDefitem2;
	}

	/**
	 * 属性defitem21的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem21() {
		return defitem21;
	}

	/**
	 * 属性defitem21的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem21
	 *            String
	 */
	public void setDefitem21(String newDefitem21) {
		defitem21 = newDefitem21;
	}

	/**
	 * 属性defitem36的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem36() {
		return defitem36;
	}

	/**
	 * 属性defitem36的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem36
	 *            String
	 */
	public void setDefitem36(String newDefitem36) {
		defitem36 = newDefitem36;
	}

	/**
	 * 属性defitem40的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem40() {
		return defitem40;
	}

	/**
	 * 属性defitem40的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem40
	 *            String
	 */
	public void setDefitem40(String newDefitem40) {

		defitem40 = newDefitem40;
	}

	/**
	 * 属性defitem23的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem23() {
		return defitem23;
	}

	/**
	 * 属性defitem23的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem23
	 *            String
	 */
	public void setDefitem23(String newDefitem23) {
		defitem23 = newDefitem23;
	}

	/**
	 * 属性defitem12的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem12() {
		return defitem12;
	}

	/**
	 * 属性defitem12的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem12
	 *            String
	 */
	public void setDefitem12(String newDefitem12) {

		defitem12 = newDefitem12;
	}

	/**
	 * 属性defitem31的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem31() {
		return defitem31;
	}

	/**
	 * 属性defitem31的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem31
	 *            String
	 */
	public void setDefitem31(String newDefitem31) {
		defitem31 = newDefitem31;
	}

	/**
	 * 属性defitem34的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem34() {
		return defitem34;
	}

	/**
	 * 属性defitem34的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem34
	 *            String
	 */
	public void setDefitem34(String newDefitem34) {
		defitem34 = newDefitem34;
	}

	/**
	 * 属性defitem3的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem3() {
		return defitem3;
	}

	/**
	 * 属性defitem3的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem3
	 *            String
	 */
	public void setDefitem3(String newDefitem3) {
		defitem3 = newDefitem3;
	}

	/**
	 * 属性tablecode的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getTablecode() {
		return tablecode;
	}

	/**
	 * 属性tablecode的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newTablecode
	 *            String
	 */
	public void setTablecode(String newTablecode) {
		tablecode = newTablecode;
	}

	/**
	 * 属性defitem11的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem11() {
		return defitem11;
	}

	/**
	 * 属性defitem11的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem11
	 *            String
	 */
	public void setDefitem11(String newDefitem11) {
		defitem11 = newDefitem11;
	}

	/**
	 * 属性defitem17的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem17() {
		return defitem17;
	}

	/**
	 * 属性defitem17的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem17
	 *            String
	 */
	public void setDefitem17(String newDefitem17) {
		defitem17 = newDefitem17;
	}

	/**
	 * 属性defitem28的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem28() {
		return defitem28;
	}

	/**
	 * 属性defitem28的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem28
	 *            String
	 */
	public void setDefitem28(String newDefitem28) {
		defitem28 = newDefitem28;
	}

	/**
	 * 属性defitem33的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem33() {
		return defitem33;
	}

	/**
	 * 属性defitem33的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem33
	 *            String
	 */
	public void setDefitem33(String newDefitem33) {
		defitem33 = newDefitem33;
	}

	/**
	 * 属性defitem48的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem48() {
		return defitem48;
	}

	/**
	 * 属性defitem48的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem48
	 *            String
	 */
	public void setDefitem48(String newDefitem48) {
		defitem48 = newDefitem48;
	}

	/**
	 * 属性pk_jkbxzb的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getPk_jkbx() {
		return pk_jkbx;
	}

	/**
	 * 属性pk_jkbxzb的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newPk_jkbxzb
	 *            String
	 */
	public void setPk_jkbx(String newPk_jkbxzb) {
		pk_jkbx = newPk_jkbxzb;
	}

	/**
	 * 属性defitem45的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem45() {
		return defitem45;
	}

	/**
	 * 属性defitem45的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem45
	 *            String
	 */
	public void setDefitem45(String newDefitem45) {
		defitem45 = newDefitem45;
	}

	/**
	 * 属性defitem1的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem1() {
		return defitem1;
	}

	/**
	 * 属性defitem1的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem1
	 *            String
	 */
	public void setDefitem1(String newDefitem1) {
		defitem1 = newDefitem1;
	}

	/**
	 * 属性defitem50的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem50() {
		return defitem50;
	}

	/**
	 * 属性defitem50的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem50
	 *            String
	 */
	public void setDefitem50(String newDefitem50) {
		defitem50 = newDefitem50;
	}

	/**
	 * 属性defitem42的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem42() {
		return defitem42;
	}

	/**
	 * 属性defitem42的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem42
	 *            String
	 */
	public void setDefitem42(String newDefitem42) {
		defitem42 = newDefitem42;
	}

	/**
	 * 属性defitem35的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem35() {
		return defitem35;
	}

	/**
	 * 属性defitem35的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem35
	 *            String
	 */
	public void setDefitem35(String newDefitem35) {
		defitem35 = newDefitem35;
	}

	/**
	 * 属性defitem30的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem30() {
		return defitem30;
	}

	/**
	 * 属性defitem30的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem30
	 *            String
	 */
	public void setDefitem30(String newDefitem30) {
		defitem30 = newDefitem30;
	}

	/**
	 * 属性defitem41的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem41() {
		return defitem41;
	}

	/**
	 * 属性defitem41的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem41
	 *            String
	 */
	public void setDefitem41(String newDefitem41) {
		defitem41 = newDefitem41;
	}

	/**
	 * 属性defitem9的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem9() {
		return defitem9;
	}

	/**
	 * 属性defitem9的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem9
	 *            String
	 */
	public void setDefitem9(String newDefitem9) {
		defitem9 = newDefitem9;
	}

	/**
	 * 属性defitem29的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem29() {
		return defitem29;
	}

	/**
	 * 属性defitem29的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem29
	 *            String
	 */
	public void setDefitem29(String newDefitem29) {
		defitem29 = newDefitem29;
	}

	/**
	 * 属性defitem5的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem5() {
		return defitem5;
	}

	/**
	 * 属性defitem5的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem5
	 *            String
	 */
	public void setDefitem5(String newDefitem5) {
		defitem5 = newDefitem5;
	}

	/**
	 * 属性defitem19的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem19() {
		return defitem19;
	}

	/**
	 * 属性defitem19的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem19
	 *            String
	 */
	public void setDefitem19(String newDefitem19) {
		defitem19 = newDefitem19;
	}

	/**
	 * 属性defitem14的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem14() {
		return defitem14;
	}

	/**
	 * 属性defitem14的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem14
	 *            String
	 */
	public void setDefitem14(String newDefitem14) {
		defitem14 = newDefitem14;
	}

	/**
	 * 属性defitem49的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem49() {
		return defitem49;
	}

	/**
	 * 属性defitem49的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem49
	 *            String
	 */
	public void setDefitem49(String newDefitem49) {
		defitem49 = newDefitem49;
	}

	/**
	 * 属性defitem26的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem26() {
		return defitem26;
	}

	/**
	 * 属性defitem26的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem26
	 *            String
	 */
	public void setDefitem26(String newDefitem26) {
		defitem26 = newDefitem26;
	}

	/**
	 * 属性defitem43的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem43() {
		return defitem43;
	}

	/**
	 * 属性defitem43的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem43
	 *            String
	 */
	public void setDefitem43(String newDefitem43) {
		defitem43 = newDefitem43;
	}

	/**
	 * 属性defitem46的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem46() {
		return defitem46;
	}

	/**
	 * 属性defitem46的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem46
	 *            String
	 */
	public void setDefitem46(String newDefitem46) {
		defitem46 = newDefitem46;
	}

	/**
	 * 属性defitem4的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem4() {
		return defitem4;
	}

	/**
	 * 属性defitem4的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem4
	 *            String
	 */
	public void setDefitem4(String newDefitem4) {
		defitem4 = newDefitem4;
	}

	/**
	 * 属性defitem24的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem24() {
		return defitem24;
	}

	/**
	 * 属性defitem24的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem24
	 *            String
	 */
	public void setDefitem24(String newDefitem24) {
		defitem24 = newDefitem24;
	}

	/**
	 * 属性defitem16的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem16() {
		return defitem16;
	}

	/**
	 * 属性defitem16的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem16
	 *            String
	 */
	public void setDefitem16(String newDefitem16) {
		defitem16 = newDefitem16;
	}

	/**
	 * 属性defitem38的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem38() {
		return defitem38;
	}

	/**
	 * 属性defitem38的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem38
	 *            String
	 */
	public void setDefitem38(String newDefitem38) {
		defitem38 = newDefitem38;
	}

	/**
	 * 属性defitem27的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem27() {
		return defitem27;
	}

	/**
	 * 属性defitem27的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem27
	 *            String
	 */
	public void setDefitem27(String newDefitem27) {
		defitem27 = newDefitem27;
	}

	/**
	 * 属性defitem22的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem22() {
		return defitem22;
	}

	/**
	 * 属性defitem22的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem22
	 *            String
	 */
	public void setDefitem22(String newDefitem22) {
		defitem22 = newDefitem22;
	}

	/**
	 * 属性defitem6的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem6() {
		return defitem6;
	}

	/**
	 * 属性defitem6的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem6
	 *            String
	 */
	public void setDefitem6(String newDefitem6) {
		defitem6 = newDefitem6;
	}

	/**
	 * 属性pk_bxbusitem的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getPk_busitem() {
		return pk_busitem;
	}

	/**
	 * 属性pk_bxbusitem的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newPk_bxbusitem
	 *            String
	 */
	public void setPk_busitem(String newPk_bxbusitem) {
		pk_busitem = newPk_bxbusitem;
	}

	/**
	 * 属性defitem39的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem39() {
		return defitem39;
	}

	/**
	 * 属性defitem39的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem39
	 *            String
	 */
	public void setDefitem39(String newDefitem39) {

		defitem39 = newDefitem39;
	}

	/**
	 * 属性defitem47的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem47() {
		return defitem47;
	}

	/**
	 * 属性defitem47的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem47
	 *            String
	 */
	public void setDefitem47(String newDefitem47) {
		defitem47 = newDefitem47;
	}

	/**
	 * 属性defitem25的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem25() {
		return defitem25;
	}

	/**
	 * 属性defitem25的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem25
	 *            String
	 */
	public void setDefitem25(String newDefitem25) {
		defitem25 = newDefitem25;
	}

	/**
	 * 属性defitem44的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem44() {
		return defitem44;
	}

	/**
	 * 属性defitem44的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem44
	 *            String
	 */
	public void setDefitem44(String newDefitem44) {
		defitem44 = newDefitem44;
	}

	/**
	 * 属性defitem18的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem18() {
		return defitem18;
	}

	/**
	 * 属性defitem18的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem18
	 *            String
	 */
	public void setDefitem18(String newDefitem18) {
		defitem18 = newDefitem18;
	}

	/**
	 * 属性defitem10的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem10() {
		return defitem10;
	}

	/**
	 * 属性defitem10的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem10
	 *            String
	 */
	public void setDefitem10(String newDefitem10) {
		defitem10 = newDefitem10;
	}

	/**
	 * 属性defitem32的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem32() {
		return defitem32;
	}

	/**
	 * 属性defitem32的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem32
	 *            String
	 */
	public void setDefitem32(String newDefitem32) {
		defitem32 = newDefitem32;
	}

	/**
	 * 属性defitem37的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem37() {
		return defitem37;
	}

	/**
	 * 属性defitem37的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem37
	 *            String
	 */
	public void setDefitem37(String newDefitem37) {
		defitem37 = newDefitem37;
	}

	/**
	 * 属性defitem15的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem15() {
		return defitem15;
	}

	/**
	 * 属性defitem15的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem15
	 *            String
	 */
	public void setDefitem15(String newDefitem15) {
		defitem15 = newDefitem15;
	}

	/**
	 * 属性defitem20的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem20() {
		return defitem20;
	}

	/**
	 * 属性defitem20的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem20
	 *            String
	 */
	public void setDefitem20(String newDefitem20) {
		defitem20 = newDefitem20;
	}

	/**
	 * 属性defitem13的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem13() {
		return defitem13;
	}

	/**
	 * 属性defitem13的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem13
	 *            String
	 */
	public void setDefitem13(String newDefitem13) {
		defitem13 = newDefitem13;
	}

	/**
	 * 属性defitem7的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDefitem7() {
		return defitem7;
	}

	/**
	 * 属性defitem7的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem7
	 *            String
	 */
	public void setDefitem7(String newDefitem7) {
		defitem7 = newDefitem7;
	}

	/**
	 * 验证对象各属性之间的数据逻辑正确性.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @exception nc.vo.pub.ValidationException
	 *                如果验证失败,抛出 ValidationException,对错误进行解释.
	 */
	public void validate() throws ValidationException {
		ArrayList<String> notNullFieldNames = new ArrayList<String>(); // errFields
		ArrayList<String> notNullFieldKeys = new ArrayList<String>(); // errFields
		notNullFieldKeys.add(BXBusItemVO.AMOUNT);
		notNullFieldKeys.add(BXBusItemVO.YBJE);

		for (String field : notNullFieldKeys) {
			if (getAttributeValue(field) == null)
				notNullFieldNames.add(getFieldName(field));
		}

		if (notNullFieldNames.size() > 0) {
			StringBuffer errorMsg = new StringBuffer();
			errorMsg.append(getErromsg());//vaildate是长调用的方法，应避免耗时的代码
			String[] temp = notNullFieldNames.toArray(new String[0]);
			errorMsg.append(temp[0]);
			for (int i = 1; i < temp.length; i++) {
				errorMsg.append(",");
				errorMsg.append(temp[i]);
			}
			throw new NullFieldException(errorMsg.toString());
		}
	}

	/**
	 * <p>
	 * 取得父VO主键字段.
	 * <p>
	 * 创建日期:2007-6-13
	 * 
	 * @return java.lang.Object
	 */
	public java.lang.String getParentPKFieldName() {
		return "pk_jkbx";
	}

	/**
	 * <p>
	 * 取得表主键.
	 * <p>
	 * 创建日期:2007-6-13
	 * 
	 * @return java.lang.Object
	 */
	public java.lang.String getPKFieldName() {
		return "pk_busitem";
	}

	/**
	 * <p>
	 * 返回表名称.
	 * <p>
	 * 创建日期:2007-6-13
	 * 
	 * @return java.lang.Object
	 */
	public java.lang.String getTableName() {
		return getDefaultTableName();
	}

	public static java.lang.String getDefaultTableName() {
		return "er_busitem";
	}
	
	@Override
    public IVOMeta getMetaData() {
      IVOMeta meta = VOMetaFactory.getInstance().getVOMeta("erm.er_busitem"); 
      return meta;
    }
	
	@Override
	public void setAttributeValue(String name, Object value) {
		if (BeanHelper.getMethod(this, name) != null) {
			try {
				BeanHelper.setProperty(this, name, value);
			} catch (IllegalArgumentException e) {
				super.setAttributeValue(name, value);
			} catch (ClassCastException e) {
				super.setAttributeValue(name, value);
			}
		} else {
			super.setAttributeValue(name, value);
		}
	}
	
	//shiwla 手机端进行vo转换用
	public void setJsonAttributeValue(String name, Object value) {
		super.setAttributeValue(name, value);
	}
	
	@Override
	public Object getAttributeValue(String key) {
		// 如果包含结算信息字段，从结算vo中取值
		if (key.startsWith(SETTLE_BODY_PREFIX)) {
			if (getSettleBodyVO() != null && key.indexOf(SETTLE_BODY_PREFIX) == 0) {
				String atrr = key.substring(key.indexOf(SETTLE_BODY_PREFIX) + SETTLE_BODY_PREFIX.length());
				return getSettleBodyVO().getAttributeValue(atrr);
			}
		}
		
		Object result = null;
		if(BeanHelper.getMethod(this, key) != null){
			result = BeanHelper.getProperty(this, key);
		}else{
			result = super.getAttributeValue(key);
		}
		
		return result;
	}

	/**
	 * 按照默认方式创建构造子.
	 * 
	 * 创建日期:2007-6-13
	 */
	public BXBusItemVO() {
		super();
	}

	/**
	 * 使用主键进行初始化的构造子.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newPk_bxbusitem
	 *            主键值
	 */
	public BXBusItemVO(String newPk_bxbusitem) {
		// 为主键字段赋值:
		pk_busitem = newPk_bxbusitem;
	}

	/**
	 * 返回对象标识,用来唯一定位对象.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public String getPrimaryKey() {
		return pk_busitem;
	}

	/**
	 * 设置对象标识,用来唯一定位对象.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newPk_bxbusitem
	 *            Object
	 */
	public void setPrimaryKey(String newPk_bxbusitem) {
		pk_busitem = newPk_bxbusitem;
	}

	/**
	 * 返回数值对象的显示名称.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return java.lang.Object 返回数值对象的显示名称.
	 */
	public String getEntityName() {
		return "er_busitem";
	}

	public UFDouble getAmount() {
		return amount;
	}

	public void setAmount(UFDouble amount) {
		this.amount = amount;
	}

	public Integer getRowno() {
		return rowno;
	}

	public void setRowno(Integer rowno) {
		this.rowno = rowno;
	}

	/**
	 * 返回表体集团本币金额字段
	 * 
	 * @author chendya
	 * @return
	 */
	public static String[] getBodyGroupBbjeField() {
		return new String[] { GROUPCJKBBJE, GROUPZFBBJE, GROUPHKBBJE, GROUPBBJE, GROUPBBYE ,GROUPTAX_AMOUNT,GROUPVAT_AMOUNT,GROUPTNI_AMOUNT};
	}

	/**
	 * 
	 * 返回表体全局本币金额字段
	 * 
	 * @author chendya
	 * @return
	 */
	public static String[] getBodyGlobalBbjeField() {
		return new String[] { GLOBALCJKBBJE, GLOBALZFBBJE, GLOBALHKBBJE, GLOBALBBJE, GLOBALBBYE ,GLOBALTAX_AMOUNT,GLOBALVAT_AMOUNT,GLOBALTNI_AMOUNT};
	}

	/**
	 * 
	 * 返回表体组织本币金额字段
	 * 
	 * @author chendya
	 * @return
	 */
	public static String[] getBodyOrgBbjeField() {
		return new String[] { CJKBBJE, ZFBBJE, HKBBJE, BBJE,BBYE,ORGTAX_AMOUNT,ORGVAT_AMOUNT,ORGTNI_AMOUNT};
	}

	/**
	 * 返回原币金额字段
	 * 
	 * @return
	 */
	public static String[] getYbjeField() {
		return new String[] { CJKYBJE, ZFYBJE, HKYBJE, YBJE, YBYE, FYYBJE, AMOUNT,YJYE,TAX_AMOUNT,VAT_AMOUNT,TNI_AMOUNT };
	}

	/**
	 * 为精度设置操作提供的方法，返回表体金额字段的key值
	 * 
	 * @return
	 */
	public static String[] getBodyJeFieldForDecimal() {
		return new String[] {TAX_AMOUNT,VAT_AMOUNT,TNI_AMOUNT,ORGTAX_AMOUNT,ORGVAT_AMOUNT,ORGTNI_AMOUNT,GROUPTAX_AMOUNT,GROUPVAT_AMOUNT,GROUPTNI_AMOUNT,GLOBALTAX_AMOUNT,GLOBALVAT_AMOUNT,GLOBALTNI_AMOUNT, AMOUNT, YBJE, YBYE, CJKYBJE, ZFYBJE, HKYBJE, FYYBJE, BBJE, BBYE, CJKBBJE, ZFBBJE, HKBBJE, GLOBALBBJE, GLOBALBBYE,
				GLOBALCJKBBJE, GLOBALZFBBJE, GLOBALHKBBJE, GROUPBBJE, GROUPBBYE, GROUPCJKBBJE, GROUPZFBBJE, GROUPHKBBJE };
	}

	public String getPk_resacostcenter() {
		return pk_resacostcenter;
	}

	public void setPk_resacostcenter(String pk_resacostcenter) {
		this.pk_resacostcenter = pk_resacostcenter;
	}

	public String getPk_checkele() {
		return pk_checkele;
	}

	public void setPk_checkele(String pk_checkele) {
		this.pk_checkele = pk_checkele;
	}

	public String getPk_pcorg() {
		return pk_pcorg;
	}

	public void setPk_pcorg(String pk_pcorg) {
		this.pk_pcorg = pk_pcorg;
	}

	public String getPk_pcorg_v() {
		return pk_pcorg_v;
	}

	public void setPk_pcorg_v(String pk_pcorg_v) {
		this.pk_pcorg_v = pk_pcorg_v;
	}

	public String getProjecttask() {
		return projecttask;
	}

	public void setProjecttask(String projecttask) {
		this.projecttask = projecttask;
	}

	public String getPk_item() {
		return pk_item;
	}

	public void setPk_item(String pkItem) {
		pk_item = pkItem;
	}

	public String getSrcbilltype() {
		return srcbilltype;
	}

	public void setSrcbilltype(String srcbilltype) {
		this.srcbilltype = srcbilltype;
	}

	public String getSrctype() {
		return srctype;
	}

	public void setSrctype(String srctype) {
		this.srctype = srctype;
	}

	public String getJk_busitemPK() {
		return jk_busitemPK;
	}

	public void setJk_busitemPK(String jk_busitemPK) {
		this.jk_busitemPK = jk_busitemPK;
	}

	public String getBx_busitemPK() {
		return bx_busitemPK;
	}

	public void setBx_busitemPK(String bx_busitemPK) {
		this.bx_busitemPK = bx_busitemPK;
	}

	public String getPk_mtapp_detail() {
		return pk_mtapp_detail;
	}

	public void setPk_mtapp_detail(String pk_mtapp_detail) {
		this.pk_mtapp_detail = pk_mtapp_detail;
	}

	public java.lang.String getPk_crmdetail() {
		return pk_crmdetail;
	}

	public void setPk_crmdetail(java.lang.String pk_crmdetail) {
		this.pk_crmdetail = pk_crmdetail;
	}

	public UFDouble getTax_amount() {
		return tax_amount;
	}

	public void setTax_amount(UFDouble tax_amount) {
		this.tax_amount = tax_amount;
	}

	public UFDouble getVat_amount() {
		return vat_amount;
	}

	public void setVat_amount(UFDouble vat_amount) {
		this.vat_amount = vat_amount;
	}

	public UFDouble getTni_amount() {
		return tni_amount;
	}

	public void setTni_amount(UFDouble tni_amount) {
		this.tni_amount = tni_amount;
	}

	public UFDouble getOrgtax_amount() {
		return orgtax_amount;
	}

	public void setOrgtax_amount(UFDouble orgtax_amount) {
		this.orgtax_amount = orgtax_amount;
	}

	public UFDouble getOrgvat_amount() {
		return orgvat_amount;
	}

	public void setOrgvat_amount(UFDouble orgvat_amount) {
		this.orgvat_amount = orgvat_amount;
	}

	public UFDouble getOrgtni_amount() {
		return orgtni_amount;
	}

	public void setOrgtni_amount(UFDouble orgtni_amount) {
		this.orgtni_amount = orgtni_amount;
	}

	public UFDouble getGrouptax_amount() {
		return grouptax_amount;
	}

	public void setGrouptax_amount(UFDouble grouptax_amount) {
		this.grouptax_amount = grouptax_amount;
	}

	public UFDouble getGroupvat_amount() {
		return groupvat_amount;
	}

	public void setGroupvat_amount(UFDouble groupvat_amount) {
		this.groupvat_amount = groupvat_amount;
	}

	public UFDouble getGrouptni_amount() {
		return grouptni_amount;
	}

	public void setGrouptni_amount(UFDouble grouptni_amount) {
		this.grouptni_amount = grouptni_amount;
	}

	public UFDouble getGlobaltax_amount() {
		return globaltax_amount;
	}

	public void setGlobaltax_amount(UFDouble globaltax_amount) {
		this.globaltax_amount = globaltax_amount;
	}

	public UFDouble getGlobalvat_amount() {
		return globalvat_amount;
	}

	public void setGlobalvat_amount(UFDouble globalvat_amount) {
		this.globalvat_amount = globalvat_amount;
	}

	public UFDouble getGlobaltni_amount() {
		return globaltni_amount;
	}

	public void setGlobaltni_amount(UFDouble globaltni_amount) {
		this.globaltni_amount = globaltni_amount;
	}
	
	//SSC新增字段
	private String bzbm;//币种
	
	private UFDouble bbhl;//本币汇率
	
	private UFDouble groupbbhl;//集团本币汇率
	
	private UFDouble globalbbhl;//全局本币汇率
	
	private UFDouble tax_rate;//税率
	
	private String sfcb;//是否超标
	
	public static final String BZBM = "bzbm";//币种编码
	
	public static final String BBHL = "bbhl";//本币汇率
	
	public static final String GROUPBBHL = "groupbbhl";//集团本币汇率
	
	public static final String GLOBALBBHL = "globalbbhl";//全局本币汇率

	public static final String TAX_RATE = "tax_rate";//税率
	
	public static final String SFCB = "sfcb";//是否超标
	
	public String getBzbm() {
		return bzbm;
	}

	public void setBzbm(String bzbm) {
		this.bzbm = bzbm;
	}

	public UFDouble getBbhl() {
		return bbhl;
	}

	public void setBbhl(UFDouble bbhl) {
		this.bbhl = bbhl;
	}

	public UFDouble getGroupbbhl() {
		return groupbbhl;
	}

	public void setGroupbbhl(UFDouble groupbbhl) {
		this.groupbbhl = groupbbhl;
	}

	public UFDouble getGlobalbbhl() {
		return globalbbhl;
	}

	public void setGlobalbbhl(UFDouble globalbbhl) {
		this.globalbbhl = globalbbhl;
	}

	public UFDouble getTax_rate() {
		return tax_rate;
	}

	public void setTax_rate(UFDouble tax_rate) {
		this.tax_rate = tax_rate;
	}

	public String getSfcb() {
		return sfcb;
	}

	public void setSfcb(String sfcb) {
		this.sfcb = sfcb;
	}

	public String getPk_fprelation() {
		return pk_fprelation;
	}

	public void setPk_fprelation(String pk_fprelation) {
		this.pk_fprelation = pk_fprelation;
	}

	public String getFpdm() {
		return fpdm;
	}

	public void setFpdm(String fpdm) {
		this.fpdm = fpdm;
	}

	public String getFphm() {
		return fphm;
	}

	public void setFphm(String fphm) {
		this.fphm = fphm;
	}
	
	public static final String DEFITEM51 = "defitem51";
	public static final String DEFITEM52 = "defitem52";
	public static final String DEFITEM53 = "defitem53";
	public static final String DEFITEM54 = "defitem54";
	public static final String DEFITEM55 = "defitem55";
	public static final String DEFITEM56 = "defitem56";
	public static final String DEFITEM57 = "defitem57";
	public static final String DEFITEM58 = "defitem58";
	public static final String DEFITEM59 = "defitem59";
	public static final String DEFITEM60 = "defitem60";
	public static final String DEFITEM61 = "defitem61";
	public static final String DEFITEM62 = "defitem62";
	public static final String DEFITEM63 = "defitem63";
	public static final String DEFITEM64 = "defitem64";
	public static final String DEFITEM65 = "defitem65";
	public static final String DEFITEM66 = "defitem66";
	public static final String DEFITEM67 = "defitem67";
	public static final String DEFITEM68 = "defitem68";
	public static final String DEFITEM69 = "defitem69";
	public static final String DEFITEM70 = "defitem70";
	public static final String DEFITEM71 = "defitem71";
	public static final String DEFITEM72 = "defitem72";
	public static final String DEFITEM73 = "defitem73";
	public static final String DEFITEM74 = "defitem74";
	public static final String DEFITEM75 = "defitem75";
	public static final String DEFITEM76 = "defitem76";
	public static final String DEFITEM77 = "defitem77";
	public static final String DEFITEM78 = "defitem78";
	public static final String DEFITEM79 = "defitem79";
	public static final String DEFITEM80 = "defitem80";
	
	public static final String DEFITEM81 = "defitem81";
	public static final String DEFITEM82 = "defitem82";
	public static final String DEFITEM83 = "defitem83";
	public static final String DEFITEM84 = "defitem84";
	public static final String DEFITEM85 = "defitem85";
	public static final String DEFITEM86 = "defitem86";
	public static final String DEFITEM87 = "defitem87";
	public static final String DEFITEM88 = "defitem88";
	public static final String DEFITEM89 = "defitem89";
	public static final String DEFITEM90 = "defitem90";
	public static final String DEFITEM91 = "defitem91";
	public static final String DEFITEM92 = "defitem92";
	public static final String DEFITEM93 = "defitem93";
	public static final String DEFITEM94 = "defitem94";
	public static final String DEFITEM95 = "defitem95";
	public static final String DEFITEM96 = "defitem96";
	public static final String DEFITEM97 = "defitem97";
	public static final String DEFITEM98 = "defitem98";
	public static final String DEFITEM99 = "defitem99";
	public static final String DEFITEM100 = "defitem100";
	public static final String DEFITEM101 = "defitem101";
	public static final String DEFITEM102 = "defitem102";
	public static final String DEFITEM103 = "defitem103";
	public static final String DEFITEM104 = "defitem104";
	public static final String DEFITEM105 = "defitem105";
	public static final String DEFITEM106 = "defitem106";
	public static final String DEFITEM107 = "defitem107";
	public static final String DEFITEM108 = "defitem108";
	public static final String DEFITEM109 = "defitem109";
	public static final String DEFITEM110 = "defitem110";
	
	public String defitem51;
	public String defitem52;
	public String defitem53;
	public String defitem54;
	public String defitem55;
	public String defitem56;
	public String defitem57;
	public String defitem58;
	public String defitem59;
	public String defitem60;
	public String defitem61;
	public String defitem62;
	public String defitem63;
	public String defitem64;
	public String defitem65;
	public String defitem66;
	public String defitem67;
	public String defitem68;
	public String defitem69;
	public String defitem70;
	public String defitem71;
	public String defitem72;
	public String defitem73;
	public String defitem74;
	public String defitem75;
	public String defitem76;
	public String defitem77;
	public String defitem78;
	public String defitem79;
	public String defitem80;
	
	public String defitem81;
	public String getDefitem81() {
		return defitem81;
	}

	public void setDefitem81(String defitem81) {
		this.defitem81 = defitem81;
	}

	public String getDefitem82() {
		return defitem82;
	}

	public void setDefitem82(String defitem82) {
		this.defitem82 = defitem82;
	}

	public String getDefitem83() {
		return defitem83;
	}

	public void setDefitem83(String defitem83) {
		this.defitem83 = defitem83;
	}

	public String getDefitem84() {
		return defitem84;
	}

	public void setDefitem84(String defitem84) {
		this.defitem84 = defitem84;
	}

	public String getDefitem85() {
		return defitem85;
	}

	public void setDefitem85(String defitem85) {
		this.defitem85 = defitem85;
	}

	public String getDefitem86() {
		return defitem86;
	}

	public void setDefitem86(String defitem86) {
		this.defitem86 = defitem86;
	}

	public String getDefitem87() {
		return defitem87;
	}

	public void setDefitem87(String defitem87) {
		this.defitem87 = defitem87;
	}

	public String getDefitem88() {
		return defitem88;
	}

	public void setDefitem88(String defitem88) {
		this.defitem88 = defitem88;
	}

	public String getDefitem89() {
		return defitem89;
	}

	public void setDefitem89(String defitem89) {
		this.defitem89 = defitem89;
	}

	public String getDefitem90() {
		return defitem90;
	}

	public void setDefitem90(String defitem90) {
		this.defitem90 = defitem90;
	}

	public String getDefitem91() {
		return defitem91;
	}

	public void setDefitem91(String defitem91) {
		this.defitem91 = defitem91;
	}

	public String getDefitem92() {
		return defitem92;
	}

	public void setDefitem92(String defitem92) {
		this.defitem92 = defitem92;
	}

	public String getDefitem93() {
		return defitem93;
	}

	public void setDefitem93(String defitem93) {
		this.defitem93 = defitem93;
	}

	public String getDefitem94() {
		return defitem94;
	}

	public void setDefitem94(String defitem94) {
		this.defitem94 = defitem94;
	}

	public String getDefitem95() {
		return defitem95;
	}

	public void setDefitem95(String defitem95) {
		this.defitem95 = defitem95;
	}

	public String getDefitem96() {
		return defitem96;
	}

	public void setDefitem96(String defitem96) {
		this.defitem96 = defitem96;
	}

	public String getDefitem97() {
		return defitem97;
	}

	public void setDefitem97(String defitem97) {
		this.defitem97 = defitem97;
	}

	public String getDefitem98() {
		return defitem98;
	}

	public void setDefitem98(String defitem98) {
		this.defitem98 = defitem98;
	}

	public String getDefitem99() {
		return defitem99;
	}

	public void setDefitem99(String defitem99) {
		this.defitem99 = defitem99;
	}

	public String getDefitem100() {
		return defitem100;
	}

	public void setDefitem100(String defitem100) {
		this.defitem100 = defitem100;
	}

	public String getDefitem101() {
		return defitem101;
	}

	public void setDefitem101(String defitem101) {
		this.defitem101 = defitem101;
	}

	public String getDefitem102() {
		return defitem102;
	}

	public void setDefitem102(String defitem102) {
		this.defitem102 = defitem102;
	}

	public String getDefitem103() {
		return defitem103;
	}

	public void setDefitem103(String defitem103) {
		this.defitem103 = defitem103;
	}

	public String getDefitem104() {
		return defitem104;
	}

	public void setDefitem104(String defitem104) {
		this.defitem104 = defitem104;
	}

	public String getDefitem105() {
		return defitem105;
	}

	public void setDefitem105(String defitem105) {
		this.defitem105 = defitem105;
	}

	public String getDefitem106() {
		return defitem106;
	}

	public void setDefitem106(String defitem106) {
		this.defitem106 = defitem106;
	}

	public String getDefitem107() {
		return defitem107;
	}

	public void setDefitem107(String defitem107) {
		this.defitem107 = defitem107;
	}

	public String getDefitem108() {
		return defitem108;
	}

	public void setDefitem108(String defitem108) {
		this.defitem108 = defitem108;
	}

	public String getDefitem109() {
		return defitem109;
	}

	public void setDefitem109(String defitem109) {
		this.defitem109 = defitem109;
	}

	public String getDefitem110() {
		return defitem110;
	}

	public void setDefitem110(String defitem110) {
		this.defitem110 = defitem110;
	}

	public String defitem82;
	public String defitem83;
	public String defitem84;
	public String defitem85;
	public String defitem86;
	public String defitem87;
	public String defitem88;
	public String defitem89;
	public String defitem90;
	public String defitem91;
	public String defitem92;
	public String defitem93;
	public String defitem94;
	public String defitem95;
	public String defitem96;
	public String defitem97;
	public String defitem98;
	public String defitem99;
	public String defitem100;
	public String defitem101;
	public String defitem102;
	public String defitem103;
	public String defitem104;
	public String defitem105;
	public String defitem106;
	public String defitem107;
	public String defitem108;
	public String defitem109;
	public String defitem110;

	public String getDefitem51() {
		return defitem51;
	}

	public void setDefitem51(String defitem51) {
		this.defitem51 = defitem51;
	}

	public String getDefitem52() {
		return defitem52;
	}

	public void setDefitem52(String defitem52) {
		this.defitem52 = defitem52;
	}

	public String getDefitem53() {
		return defitem53;
	}

	public void setDefitem53(String defitem53) {
		this.defitem53 = defitem53;
	}

	public String getDefitem54() {
		return defitem54;
	}

	public void setDefitem54(String defitem54) {
		this.defitem54 = defitem54;
	}

	public String getDefitem55() {
		return defitem55;
	}

	public void setDefitem55(String defitem55) {
		this.defitem55 = defitem55;
	}

	public String getDefitem56() {
		return defitem56;
	}

	public void setDefitem56(String defitem56) {
		this.defitem56 = defitem56;
	}

	public String getDefitem57() {
		return defitem57;
	}

	public void setDefitem57(String defitem57) {
		this.defitem57 = defitem57;
	}

	public String getDefitem58() {
		return defitem58;
	}

	public void setDefitem58(String defitem58) {
		this.defitem58 = defitem58;
	}

	public String getDefitem59() {
		return defitem59;
	}

	public void setDefitem59(String defitem59) {
		this.defitem59 = defitem59;
	}

	public String getDefitem60() {
		return defitem60;
	}

	public void setDefitem60(String defitem60) {
		this.defitem60 = defitem60;
	}

	public String getDefitem61() {
		return defitem61;
	}

	public void setDefitem61(String defitem61) {
		this.defitem61 = defitem61;
	}

	public String getDefitem62() {
		return defitem62;
	}

	public void setDefitem62(String defitem62) {
		this.defitem62 = defitem62;
	}

	public String getDefitem63() {
		return defitem63;
	}

	public void setDefitem63(String defitem63) {
		this.defitem63 = defitem63;
	}

	public String getDefitem64() {
		return defitem64;
	}

	public void setDefitem64(String defitem64) {
		this.defitem64 = defitem64;
	}

	public String getDefitem65() {
		return defitem65;
	}

	public void setDefitem65(String defitem65) {
		this.defitem65 = defitem65;
	}

	public String getDefitem66() {
		return defitem66;
	}

	public void setDefitem66(String defitem66) {
		this.defitem66 = defitem66;
	}

	public String getDefitem67() {
		return defitem67;
	}

	public void setDefitem67(String defitem67) {
		this.defitem67 = defitem67;
	}

	public String getDefitem68() {
		return defitem68;
	}

	public void setDefitem68(String defitem68) {
		this.defitem68 = defitem68;
	}

	public String getDefitem69() {
		return defitem69;
	}

	public void setDefitem69(String defitem69) {
		this.defitem69 = defitem69;
	}

	public String getDefitem70() {
		return defitem70;
	}

	public void setDefitem70(String defitem70) {
		this.defitem70 = defitem70;
	}

	public String getDefitem71() {
		return defitem71;
	}

	public void setDefitem71(String defitem71) {
		this.defitem71 = defitem71;
	}

	public String getDefitem72() {
		return defitem72;
	}

	public void setDefitem72(String defitem72) {
		this.defitem72 = defitem72;
	}

	public String getDefitem73() {
		return defitem73;
	}

	public void setDefitem73(String defitem73) {
		this.defitem73 = defitem73;
	}

	public String getDefitem74() {
		return defitem74;
	}

	public void setDefitem74(String defitem74) {
		this.defitem74 = defitem74;
	}

	public String getDefitem75() {
		return defitem75;
	}

	public void setDefitem75(String defitem75) {
		this.defitem75 = defitem75;
	}

	public String getDefitem76() {
		return defitem76;
	}

	public void setDefitem76(String defitem76) {
		this.defitem76 = defitem76;
	}

	public String getDefitem77() {
		return defitem77;
	}

	public void setDefitem77(String defitem77) {
		this.defitem77 = defitem77;
	}

	public String getDefitem78() {
		return defitem78;
	}

	public void setDefitem78(String defitem78) {
		this.defitem78 = defitem78;
	}

	public String getDefitem79() {
		return defitem79;
	}

	public void setDefitem79(String defitem79) {
		this.defitem79 = defitem79;
	}

	public String getDefitem80() {
		return defitem80;
	}

	public void setDefitem80(String defitem80) {
		this.defitem80 = defitem80;
	}
	
}