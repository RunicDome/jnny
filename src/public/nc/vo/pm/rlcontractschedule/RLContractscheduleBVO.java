package nc.vo.pm.rlcontractschedule;
//多遍码进度款表体
import nc.vo.pub.*;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> 此处简要描述此类功能 </b>
 * <p>
 *   此处添加类的描述信息
 * </p>
 *  创建日期:2021-9-22
 * @author developer
 * @version NCPrj ??
 */
public class RLContractscheduleBVO extends nc.vo.pub.SuperVO{
	
    private java.lang.String pk_rl_contr_sche;
    private java.lang.String pk_rl_contr_sche_b;
    private java.lang.String pk_org;
    private java.lang.String pk_org_v;
    private java.lang.String pk_group;
    private java.lang.String pk_project;
    private java.lang.String pk_material;
    private java.lang.String pk_material_v;
    private java.lang.String pk_cbsnode;
    private java.lang.String pk_measdoc;
    private java.lang.String memo;
    private java.lang.String hdef1;
    private java.lang.String hdef2;
    private java.lang.String hdef3;
    private java.lang.String hdef4;
    private java.lang.String hdef5;
    private java.lang.String hdef6;
    private java.lang.String hdef7;
    private java.lang.String hdef8;
    private java.lang.String hdef9;
    private java.lang.String hdef10;
    private java.lang.String hdef11;
    private java.lang.String hdef12;
    private java.lang.String hdef13;
    private java.lang.String hdef14;
    private java.lang.String hdef15;
    private java.lang.String hdef16;
    private java.lang.String hdef17;
    private java.lang.String hdef18;
    private java.lang.String hdef19;
    private java.lang.String hdef20;
    private java.lang.String rowno;
    private java.lang.String tsourcebodyts;
    private java.lang.String tsourceheadts;
    private java.lang.String csourcebillbid;
    private nc.vo.pub.lang.UFDate csourcebilldate;
    private java.lang.String csourcebillhid;
    private java.lang.String csourcetype;
    private java.lang.String cfirstbillbid;
    private nc.vo.pub.lang.UFDate cfirstbilldate;
    private java.lang.String cfirstbillhid;
    private java.lang.String cfirsttranstype;
    private java.lang.String cfirsttype;
    private nc.vo.pub.lang.UFDouble protocontractmoney;
    private nc.vo.pub.lang.UFDouble paymoney;
    private nc.vo.pub.lang.UFDouble examinationvalue;
    private nc.vo.pub.lang.UFDouble addpaymoney;
    private java.lang.String clearingstate;
    private nc.vo.pub.lang.UFDouble contractalter_mny;
    private nc.vo.pub.lang.UFDouble contractbalance_mny;
    private nc.vo.pub.lang.UFDouble notaxprice;
    private nc.vo.pub.lang.UFDouble notaxmoney;
    private nc.vo.pub.lang.UFDouble taxprice;
    private nc.vo.pub.lang.UFDouble currentpaymentamount;
    private nc.vo.pub.lang.UFDouble taxrate;
    private nc.vo.pub.lang.UFDouble taxamount;
    private nc.vo.pub.lang.UFDouble currentpaymentamounta;
    private nc.vo.pub.lang.UFDouble money;
    private nc.vo.pub.lang.UFDouble unitmoney;
    private nc.vo.pub.lang.UFDouble num;
    private java.lang.Integer dr = 0;
    private nc.vo.pub.lang.UFDateTime ts;    
	
	
    public static final String PK_RL_CONTR_SCHE = "pk_rl_contr_sche";
    public static final String PK_RL_CONTR_SCHE_B = "pk_rl_contr_sche_b";
    public static final String PK_ORG = "pk_org";
    public static final String PK_ORG_V = "pk_org_v";
    public static final String PK_GROUP = "pk_group";
    public static final String PK_PROJECT = "pk_project";
    public static final String PK_MATERIAL = "pk_material";
    public static final String PK_MATERIAL_V = "pk_material_v";
    public static final String PK_CBSNODE = "pk_cbsnode";
    public static final String PK_MEASDOC = "pk_measdoc";
    public static final String MEMO = "memo";
    public static final String HDEF1 = "hdef1";
    public static final String HDEF2 = "hdef2";
    public static final String HDEF3 = "hdef3";
    public static final String HDEF4 = "hdef4";
    public static final String HDEF5 = "hdef5";
    public static final String HDEF6 = "hdef6";
    public static final String HDEF7 = "hdef7";
    public static final String HDEF8 = "hdef8";
    public static final String HDEF9 = "hdef9";
    public static final String HDEF10 = "hdef10";
    public static final String HDEF11 = "hdef11";
    public static final String HDEF12 = "hdef12";
    public static final String HDEF13 = "hdef13";
    public static final String HDEF14 = "hdef14";
    public static final String HDEF15 = "hdef15";
    public static final String HDEF16 = "hdef16";
    public static final String HDEF17 = "hdef17";
    public static final String HDEF18 = "hdef18";
    public static final String HDEF19 = "hdef19";
    public static final String HDEF20 = "hdef20";
    public static final String ROWNO = "rowno";
    public static final String TSOURCEBODYTS = "tsourcebodyts";
    public static final String TSOURCEHEADTS = "tsourceheadts";
    public static final String CSOURCEBILLBID = "csourcebillbid";
    public static final String CSOURCEBILLDATE = "csourcebilldate";
    public static final String CSOURCEBILLHID = "csourcebillhid";
    public static final String CSOURCETYPE = "csourcetype";
    public static final String CFIRSTBILLBID = "cfirstbillbid";
    public static final String CFIRSTBILLDATE = "cfirstbilldate";
    public static final String CFIRSTBILLHID = "cfirstbillhid";
    public static final String CFIRSTTRANSTYPE = "cfirsttranstype";
    public static final String CFIRSTTYPE = "cfirsttype";
    public static final String PROTOCONTRACTMONEY = "protocontractmoney";
    public static final String PAYMONEY = "paymoney";
    public static final String EXAMINATIONVALUE = "examinationvalue";
    public static final String ADDPAYMONEY = "addpaymoney";
    public static final String CLEARINGSTATE = "clearingstate";
    public static final String CONTRACTALTER_MNY = "contractalter_mny";
    public static final String CONTRACTBALANCE_MNY = "contractbalance_mny";
    public static final String NOTAXPRICE = "notaxprice";
    public static final String NOTAXMONEY = "notaxmoney";
    public static final String TAXPRICE = "taxprice";
    public static final String CURRENTPAYMENTAMOUNT = "currentpaymentamount";
    public static final String TAXRATE = "taxrate";
    public static final String TAXAMOUNT = "taxamount";
    public static final String CURRENTPAYMENTAMOUNTA = "currentpaymentamounta";
    public static final String MONEY = "money";
    public static final String UNITMONEY = "unitmoney";
    public static final String NUM = "num";

	/**
	 * 属性 pk_rl_contr_sche的Getter方法.属性名：parentPK
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_rl_contr_sche () {
		return pk_rl_contr_sche;
	}   
	/**
	 * 属性pk_rl_contr_sche的Setter方法.属性名：parentPK
	 * 创建日期:2021-9-22
	 * @param newPk_rl_contr_sche java.lang.String
	 */
	public void setPk_rl_contr_sche (java.lang.String newPk_rl_contr_sche ) {
	 	this.pk_rl_contr_sche = newPk_rl_contr_sche;
	} 	 
	
	/**
	 * 属性 pk_rl_contr_sche_b的Getter方法.属性名：分包合同进度款子表主键
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_rl_contr_sche_b () {
		return pk_rl_contr_sche_b;
	}   
	/**
	 * 属性pk_rl_contr_sche_b的Setter方法.属性名：分包合同进度款子表主键
	 * 创建日期:2021-9-22
	 * @param newPk_rl_contr_sche_b java.lang.String
	 */
	public void setPk_rl_contr_sche_b (java.lang.String newPk_rl_contr_sche_b ) {
	 	this.pk_rl_contr_sche_b = newPk_rl_contr_sche_b;
	} 	 
	
	/**
	 * 属性 pk_org的Getter方法.属性名：项目组织
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_org () {
		return pk_org;
	}   
	/**
	 * 属性pk_org的Setter方法.属性名：项目组织
	 * 创建日期:2021-9-22
	 * @param newPk_org java.lang.String
	 */
	public void setPk_org (java.lang.String newPk_org ) {
	 	this.pk_org = newPk_org;
	} 	 
	
	/**
	 * 属性 pk_org_v的Getter方法.属性名：项目组织版本
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_org_v () {
		return pk_org_v;
	}   
	/**
	 * 属性pk_org_v的Setter方法.属性名：项目组织版本
	 * 创建日期:2021-9-22
	 * @param newPk_org_v java.lang.String
	 */
	public void setPk_org_v (java.lang.String newPk_org_v ) {
	 	this.pk_org_v = newPk_org_v;
	} 	 
	
	/**
	 * 属性 pk_group的Getter方法.属性名：集团
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_group () {
		return pk_group;
	}   
	/**
	 * 属性pk_group的Setter方法.属性名：集团
	 * 创建日期:2021-9-22
	 * @param newPk_group java.lang.String
	 */
	public void setPk_group (java.lang.String newPk_group ) {
	 	this.pk_group = newPk_group;
	} 	 
	
	/**
	 * 属性 pk_project的Getter方法.属性名：项目
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_project () {
		return pk_project;
	}   
	/**
	 * 属性pk_project的Setter方法.属性名：项目
	 * 创建日期:2021-9-22
	 * @param newPk_project java.lang.String
	 */
	public void setPk_project (java.lang.String newPk_project ) {
	 	this.pk_project = newPk_project;
	} 	 
	
	/**
	 * 属性 pk_material的Getter方法.属性名：编码
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_material () {
		return pk_material;
	}   
	/**
	 * 属性pk_material的Setter方法.属性名：编码
	 * 创建日期:2021-9-22
	 * @param newPk_material java.lang.String
	 */
	public void setPk_material (java.lang.String newPk_material ) {
	 	this.pk_material = newPk_material;
	} 	 
	
	/**
	 * 属性 pk_material_v的Getter方法.属性名：物料
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_material_v () {
		return pk_material_v;
	}   
	/**
	 * 属性pk_material_v的Setter方法.属性名：物料
	 * 创建日期:2021-9-22
	 * @param newPk_material_v java.lang.String
	 */
	public void setPk_material_v (java.lang.String newPk_material_v ) {
	 	this.pk_material_v = newPk_material_v;
	} 	 
	
	/**
	 * 属性 pk_cbsnode的Getter方法.属性名：CBS
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_cbsnode () {
		return pk_cbsnode;
	}   
	/**
	 * 属性pk_cbsnode的Setter方法.属性名：CBS
	 * 创建日期:2021-9-22
	 * @param newPk_cbsnode java.lang.String
	 */
	public void setPk_cbsnode (java.lang.String newPk_cbsnode ) {
	 	this.pk_cbsnode = newPk_cbsnode;
	} 	 
	
	/**
	 * 属性 pk_measdoc的Getter方法.属性名：单位
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_measdoc () {
		return pk_measdoc;
	}   
	/**
	 * 属性pk_measdoc的Setter方法.属性名：单位
	 * 创建日期:2021-9-22
	 * @param newPk_measdoc java.lang.String
	 */
	public void setPk_measdoc (java.lang.String newPk_measdoc ) {
	 	this.pk_measdoc = newPk_measdoc;
	} 	 
	
	/**
	 * 属性 memo的Getter方法.属性名：备注
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getMemo () {
		return memo;
	}   
	/**
	 * 属性memo的Setter方法.属性名：备注
	 * 创建日期:2021-9-22
	 * @param newMemo java.lang.String
	 */
	public void setMemo (java.lang.String newMemo ) {
	 	this.memo = newMemo;
	} 	 
	
	/**
	 * 属性 hdef1的Getter方法.属性名：自定义项1
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef1 () {
		return hdef1;
	}   
	/**
	 * 属性hdef1的Setter方法.属性名：自定义项1
	 * 创建日期:2021-9-22
	 * @param newHdef1 java.lang.String
	 */
	public void setHdef1 (java.lang.String newHdef1 ) {
	 	this.hdef1 = newHdef1;
	} 	 
	
	/**
	 * 属性 hdef2的Getter方法.属性名：自定义项2
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef2 () {
		return hdef2;
	}   
	/**
	 * 属性hdef2的Setter方法.属性名：自定义项2
	 * 创建日期:2021-9-22
	 * @param newHdef2 java.lang.String
	 */
	public void setHdef2 (java.lang.String newHdef2 ) {
	 	this.hdef2 = newHdef2;
	} 	 
	
	/**
	 * 属性 hdef3的Getter方法.属性名：自定义项3
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef3 () {
		return hdef3;
	}   
	/**
	 * 属性hdef3的Setter方法.属性名：自定义项3
	 * 创建日期:2021-9-22
	 * @param newHdef3 java.lang.String
	 */
	public void setHdef3 (java.lang.String newHdef3 ) {
	 	this.hdef3 = newHdef3;
	} 	 
	
	/**
	 * 属性 hdef4的Getter方法.属性名：自定义项4
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef4 () {
		return hdef4;
	}   
	/**
	 * 属性hdef4的Setter方法.属性名：自定义项4
	 * 创建日期:2021-9-22
	 * @param newHdef4 java.lang.String
	 */
	public void setHdef4 (java.lang.String newHdef4 ) {
	 	this.hdef4 = newHdef4;
	} 	 
	
	/**
	 * 属性 hdef5的Getter方法.属性名：自定义项5
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef5 () {
		return hdef5;
	}   
	/**
	 * 属性hdef5的Setter方法.属性名：自定义项5
	 * 创建日期:2021-9-22
	 * @param newHdef5 java.lang.String
	 */
	public void setHdef5 (java.lang.String newHdef5 ) {
	 	this.hdef5 = newHdef5;
	} 	 
	
	/**
	 * 属性 hdef6的Getter方法.属性名：自定义项6
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef6 () {
		return hdef6;
	}   
	/**
	 * 属性hdef6的Setter方法.属性名：自定义项6
	 * 创建日期:2021-9-22
	 * @param newHdef6 java.lang.String
	 */
	public void setHdef6 (java.lang.String newHdef6 ) {
	 	this.hdef6 = newHdef6;
	} 	 
	
	/**
	 * 属性 hdef7的Getter方法.属性名：自定义项7
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef7 () {
		return hdef7;
	}   
	/**
	 * 属性hdef7的Setter方法.属性名：自定义项7
	 * 创建日期:2021-9-22
	 * @param newHdef7 java.lang.String
	 */
	public void setHdef7 (java.lang.String newHdef7 ) {
	 	this.hdef7 = newHdef7;
	} 	 
	
	/**
	 * 属性 hdef8的Getter方法.属性名：自定义项8
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef8 () {
		return hdef8;
	}   
	/**
	 * 属性hdef8的Setter方法.属性名：自定义项8
	 * 创建日期:2021-9-22
	 * @param newHdef8 java.lang.String
	 */
	public void setHdef8 (java.lang.String newHdef8 ) {
	 	this.hdef8 = newHdef8;
	} 	 
	
	/**
	 * 属性 hdef9的Getter方法.属性名：自定义项9
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef9 () {
		return hdef9;
	}   
	/**
	 * 属性hdef9的Setter方法.属性名：自定义项9
	 * 创建日期:2021-9-22
	 * @param newHdef9 java.lang.String
	 */
	public void setHdef9 (java.lang.String newHdef9 ) {
	 	this.hdef9 = newHdef9;
	} 	 
	
	/**
	 * 属性 hdef10的Getter方法.属性名：自定义项10
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef10 () {
		return hdef10;
	}   
	/**
	 * 属性hdef10的Setter方法.属性名：自定义项10
	 * 创建日期:2021-9-22
	 * @param newHdef10 java.lang.String
	 */
	public void setHdef10 (java.lang.String newHdef10 ) {
	 	this.hdef10 = newHdef10;
	} 	 
	
	/**
	 * 属性 hdef11的Getter方法.属性名：自定义项11
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef11 () {
		return hdef11;
	}   
	/**
	 * 属性hdef11的Setter方法.属性名：自定义项11
	 * 创建日期:2021-9-22
	 * @param newHdef11 java.lang.String
	 */
	public void setHdef11 (java.lang.String newHdef11 ) {
	 	this.hdef11 = newHdef11;
	} 	 
	
	/**
	 * 属性 hdef12的Getter方法.属性名：自定义项12
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef12 () {
		return hdef12;
	}   
	/**
	 * 属性hdef12的Setter方法.属性名：自定义项12
	 * 创建日期:2021-9-22
	 * @param newHdef12 java.lang.String
	 */
	public void setHdef12 (java.lang.String newHdef12 ) {
	 	this.hdef12 = newHdef12;
	} 	 
	
	/**
	 * 属性 hdef13的Getter方法.属性名：自定义项13
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef13 () {
		return hdef13;
	}   
	/**
	 * 属性hdef13的Setter方法.属性名：自定义项13
	 * 创建日期:2021-9-22
	 * @param newHdef13 java.lang.String
	 */
	public void setHdef13 (java.lang.String newHdef13 ) {
	 	this.hdef13 = newHdef13;
	} 	 
	
	/**
	 * 属性 hdef14的Getter方法.属性名：自定义项14
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef14 () {
		return hdef14;
	}   
	/**
	 * 属性hdef14的Setter方法.属性名：自定义项14
	 * 创建日期:2021-9-22
	 * @param newHdef14 java.lang.String
	 */
	public void setHdef14 (java.lang.String newHdef14 ) {
	 	this.hdef14 = newHdef14;
	} 	 
	
	/**
	 * 属性 hdef15的Getter方法.属性名：自定义项15
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef15 () {
		return hdef15;
	}   
	/**
	 * 属性hdef15的Setter方法.属性名：自定义项15
	 * 创建日期:2021-9-22
	 * @param newHdef15 java.lang.String
	 */
	public void setHdef15 (java.lang.String newHdef15 ) {
	 	this.hdef15 = newHdef15;
	} 	 
	
	/**
	 * 属性 hdef16的Getter方法.属性名：自定义项16
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef16 () {
		return hdef16;
	}   
	/**
	 * 属性hdef16的Setter方法.属性名：自定义项16
	 * 创建日期:2021-9-22
	 * @param newHdef16 java.lang.String
	 */
	public void setHdef16 (java.lang.String newHdef16 ) {
	 	this.hdef16 = newHdef16;
	} 	 
	
	/**
	 * 属性 hdef17的Getter方法.属性名：自定义项17
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef17 () {
		return hdef17;
	}   
	/**
	 * 属性hdef17的Setter方法.属性名：自定义项17
	 * 创建日期:2021-9-22
	 * @param newHdef17 java.lang.String
	 */
	public void setHdef17 (java.lang.String newHdef17 ) {
	 	this.hdef17 = newHdef17;
	} 	 
	
	/**
	 * 属性 hdef18的Getter方法.属性名：自定义项18
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef18 () {
		return hdef18;
	}   
	/**
	 * 属性hdef18的Setter方法.属性名：自定义项18
	 * 创建日期:2021-9-22
	 * @param newHdef18 java.lang.String
	 */
	public void setHdef18 (java.lang.String newHdef18 ) {
	 	this.hdef18 = newHdef18;
	} 	 
	
	/**
	 * 属性 hdef19的Getter方法.属性名：自定义项19
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef19 () {
		return hdef19;
	}   
	/**
	 * 属性hdef19的Setter方法.属性名：自定义项19
	 * 创建日期:2021-9-22
	 * @param newHdef19 java.lang.String
	 */
	public void setHdef19 (java.lang.String newHdef19 ) {
	 	this.hdef19 = newHdef19;
	} 	 
	
	/**
	 * 属性 hdef20的Getter方法.属性名：自定义项20
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef20 () {
		return hdef20;
	}   
	/**
	 * 属性hdef20的Setter方法.属性名：自定义项20
	 * 创建日期:2021-9-22
	 * @param newHdef20 java.lang.String
	 */
	public void setHdef20 (java.lang.String newHdef20 ) {
	 	this.hdef20 = newHdef20;
	} 	 
	
	/**
	 * 属性 rowno的Getter方法.属性名：行号
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getRowno () {
		return rowno;
	}   
	/**
	 * 属性rowno的Setter方法.属性名：行号
	 * 创建日期:2021-9-22
	 * @param newRowno java.lang.String
	 */
	public void setRowno (java.lang.String newRowno ) {
	 	this.rowno = newRowno;
	} 	 
	
	/**
	 * 属性 tsourcebodyts的Getter方法.属性名：来源表体时间戳
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getTsourcebodyts () {
		return tsourcebodyts;
	}   
	/**
	 * 属性tsourcebodyts的Setter方法.属性名：来源表体时间戳
	 * 创建日期:2021-9-22
	 * @param newTsourcebodyts java.lang.String
	 */
	public void setTsourcebodyts (java.lang.String newTsourcebodyts ) {
	 	this.tsourcebodyts = newTsourcebodyts;
	} 	 
	
	/**
	 * 属性 tsourceheadts的Getter方法.属性名：来源表头时间戳
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getTsourceheadts () {
		return tsourceheadts;
	}   
	/**
	 * 属性tsourceheadts的Setter方法.属性名：来源表头时间戳
	 * 创建日期:2021-9-22
	 * @param newTsourceheadts java.lang.String
	 */
	public void setTsourceheadts (java.lang.String newTsourceheadts ) {
	 	this.tsourceheadts = newTsourceheadts;
	} 	 
	
	/**
	 * 属性 csourcebillbid的Getter方法.属性名：来源单据表体主键
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getCsourcebillbid () {
		return csourcebillbid;
	}   
	/**
	 * 属性csourcebillbid的Setter方法.属性名：来源单据表体主键
	 * 创建日期:2021-9-22
	 * @param newCsourcebillbid java.lang.String
	 */
	public void setCsourcebillbid (java.lang.String newCsourcebillbid ) {
	 	this.csourcebillbid = newCsourcebillbid;
	} 	 
	
	/**
	 * 属性 csourcebilldate的Getter方法.属性名：来源单据日期
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDate
	 */
	public nc.vo.pub.lang.UFDate getCsourcebilldate () {
		return csourcebilldate;
	}   
	/**
	 * 属性csourcebilldate的Setter方法.属性名：来源单据日期
	 * 创建日期:2021-9-22
	 * @param newCsourcebilldate nc.vo.pub.lang.UFDate
	 */
	public void setCsourcebilldate (nc.vo.pub.lang.UFDate newCsourcebilldate ) {
	 	this.csourcebilldate = newCsourcebilldate;
	} 	 
	
	/**
	 * 属性 csourcebillhid的Getter方法.属性名：来源单据表头主键
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getCsourcebillhid () {
		return csourcebillhid;
	}   
	/**
	 * 属性csourcebillhid的Setter方法.属性名：来源单据表头主键
	 * 创建日期:2021-9-22
	 * @param newCsourcebillhid java.lang.String
	 */
	public void setCsourcebillhid (java.lang.String newCsourcebillhid ) {
	 	this.csourcebillhid = newCsourcebillhid;
	} 	 
	
	/**
	 * 属性 csourcetype的Getter方法.属性名：来源单据类型
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getCsourcetype () {
		return csourcetype;
	}   
	/**
	 * 属性csourcetype的Setter方法.属性名：来源单据类型
	 * 创建日期:2021-9-22
	 * @param newCsourcetype java.lang.String
	 */
	public void setCsourcetype (java.lang.String newCsourcetype ) {
	 	this.csourcetype = newCsourcetype;
	} 	 
	
	/**
	 * 属性 cfirstbillbid的Getter方法.属性名：源头单据表体主键
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getCfirstbillbid () {
		return cfirstbillbid;
	}   
	/**
	 * 属性cfirstbillbid的Setter方法.属性名：源头单据表体主键
	 * 创建日期:2021-9-22
	 * @param newCfirstbillbid java.lang.String
	 */
	public void setCfirstbillbid (java.lang.String newCfirstbillbid ) {
	 	this.cfirstbillbid = newCfirstbillbid;
	} 	 
	
	/**
	 * 属性 cfirstbilldate的Getter方法.属性名：源头单据日期
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDate
	 */
	public nc.vo.pub.lang.UFDate getCfirstbilldate () {
		return cfirstbilldate;
	}   
	/**
	 * 属性cfirstbilldate的Setter方法.属性名：源头单据日期
	 * 创建日期:2021-9-22
	 * @param newCfirstbilldate nc.vo.pub.lang.UFDate
	 */
	public void setCfirstbilldate (nc.vo.pub.lang.UFDate newCfirstbilldate ) {
	 	this.cfirstbilldate = newCfirstbilldate;
	} 	 
	
	/**
	 * 属性 cfirstbillhid的Getter方法.属性名：源头单据表头主键
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getCfirstbillhid () {
		return cfirstbillhid;
	}   
	/**
	 * 属性cfirstbillhid的Setter方法.属性名：源头单据表头主键
	 * 创建日期:2021-9-22
	 * @param newCfirstbillhid java.lang.String
	 */
	public void setCfirstbillhid (java.lang.String newCfirstbillhid ) {
	 	this.cfirstbillhid = newCfirstbillhid;
	} 	 
	
	/**
	 * 属性 cfirsttranstype的Getter方法.属性名：源头交易类型
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getCfirsttranstype () {
		return cfirsttranstype;
	}   
	/**
	 * 属性cfirsttranstype的Setter方法.属性名：源头交易类型
	 * 创建日期:2021-9-22
	 * @param newCfirsttranstype java.lang.String
	 */
	public void setCfirsttranstype (java.lang.String newCfirsttranstype ) {
	 	this.cfirsttranstype = newCfirsttranstype;
	} 	 
	
	/**
	 * 属性 cfirsttype的Getter方法.属性名：源头单据类型
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getCfirsttype () {
		return cfirsttype;
	}   
	/**
	 * 属性cfirsttype的Setter方法.属性名：源头单据类型
	 * 创建日期:2021-9-22
	 * @param newCfirsttype java.lang.String
	 */
	public void setCfirsttype (java.lang.String newCfirsttype ) {
	 	this.cfirsttype = newCfirsttype;
	} 	 
	
	/**
	 * 属性 protocontractmoney的Getter方法.属性名：原合同金额
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getProtocontractmoney () {
		return protocontractmoney;
	}   
	/**
	 * 属性protocontractmoney的Setter方法.属性名：原合同金额
	 * 创建日期:2021-9-22
	 * @param newProtocontractmoney nc.vo.pub.lang.UFDouble
	 */
	public void setProtocontractmoney (nc.vo.pub.lang.UFDouble newProtocontractmoney ) {
	 	this.protocontractmoney = newProtocontractmoney;
	} 	 
	
	/**
	 * 属性 paymoney的Getter方法.属性名：已付款金额
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getPaymoney () {
		return paymoney;
	}   
	/**
	 * 属性paymoney的Setter方法.属性名：已付款金额
	 * 创建日期:2021-9-22
	 * @param newPaymoney nc.vo.pub.lang.UFDouble
	 */
	public void setPaymoney (nc.vo.pub.lang.UFDouble newPaymoney ) {
	 	this.paymoney = newPaymoney;
	} 	 
	
	/**
	 * 属性 examinationvalue的Getter方法.属性名：审定值
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getExaminationvalue () {
		return examinationvalue;
	}   
	/**
	 * 属性examinationvalue的Setter方法.属性名：审定值
	 * 创建日期:2021-9-22
	 * @param newExaminationvalue nc.vo.pub.lang.UFDouble
	 */
	public void setExaminationvalue (nc.vo.pub.lang.UFDouble newExaminationvalue ) {
	 	this.examinationvalue = newExaminationvalue;
	} 	 
	
	/**
	 * 属性 addpaymoney的Getter方法.属性名：累计付款金额
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getAddpaymoney () {
		return addpaymoney;
	}   
	/**
	 * 属性addpaymoney的Setter方法.属性名：累计付款金额
	 * 创建日期:2021-9-22
	 * @param newAddpaymoney nc.vo.pub.lang.UFDouble
	 */
	public void setAddpaymoney (nc.vo.pub.lang.UFDouble newAddpaymoney ) {
	 	this.addpaymoney = newAddpaymoney;
	} 	 
	
	/**
	 * 属性 clearingstate的Getter方法.属性名：结算状态
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getClearingstate () {
		return clearingstate;
	}   
	/**
	 * 属性clearingstate的Setter方法.属性名：结算状态
	 * 创建日期:2021-9-22
	 * @param newClearingstate java.lang.String
	 */
	public void setClearingstate (java.lang.String newClearingstate ) {
	 	this.clearingstate = newClearingstate;
	} 	 
	
	/**
	 * 属性 contractalter_mny的Getter方法.属性名：累计结算
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getContractalter_mny () {
		return contractalter_mny;
	}   
	/**
	 * 属性contractalter_mny的Setter方法.属性名：累计结算
	 * 创建日期:2021-9-22
	 * @param newContractalter_mny nc.vo.pub.lang.UFDouble
	 */
	public void setContractalter_mny (nc.vo.pub.lang.UFDouble newContractalter_mny ) {
	 	this.contractalter_mny = newContractalter_mny;
	} 	 
	
	/**
	 * 属性 contractbalance_mny的Getter方法.属性名：累计进度
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getContractbalance_mny () {
		return contractbalance_mny;
	}   
	/**
	 * 属性contractbalance_mny的Setter方法.属性名：累计进度
	 * 创建日期:2021-9-22
	 * @param newContractbalance_mny nc.vo.pub.lang.UFDouble
	 */
	public void setContractbalance_mny (nc.vo.pub.lang.UFDouble newContractbalance_mny ) {
	 	this.contractbalance_mny = newContractbalance_mny;
	} 	 
	
	/**
	 * 属性 notaxprice的Getter方法.属性名：无税单价
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getNotaxprice () {
		return notaxprice;
	}   
	/**
	 * 属性notaxprice的Setter方法.属性名：无税单价
	 * 创建日期:2021-9-22
	 * @param newNotaxprice nc.vo.pub.lang.UFDouble
	 */
	public void setNotaxprice (nc.vo.pub.lang.UFDouble newNotaxprice ) {
	 	this.notaxprice = newNotaxprice;
	} 	 
	
	/**
	 * 属性 notaxmoney的Getter方法.属性名：无税金额
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getNotaxmoney () {
		return notaxmoney;
	}   
	/**
	 * 属性notaxmoney的Setter方法.属性名：无税金额
	 * 创建日期:2021-9-22
	 * @param newNotaxmoney nc.vo.pub.lang.UFDouble
	 */
	public void setNotaxmoney (nc.vo.pub.lang.UFDouble newNotaxmoney ) {
	 	this.notaxmoney = newNotaxmoney;
	} 	 
	
	/**
	 * 属性 taxprice的Getter方法.属性名：含税单价
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getTaxprice () {
		return taxprice;
	}   
	/**
	 * 属性taxprice的Setter方法.属性名：含税单价
	 * 创建日期:2021-9-22
	 * @param newTaxprice nc.vo.pub.lang.UFDouble
	 */
	public void setTaxprice (nc.vo.pub.lang.UFDouble newTaxprice ) {
	 	this.taxprice = newTaxprice;
	} 	 
	
	/**
	 * 属性 currentpaymentamount的Getter方法.属性名：本次付款金额
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getCurrentpaymentamount () {
		return currentpaymentamount;
	}   
	/**
	 * 属性currentpaymentamount的Setter方法.属性名：本次付款金额
	 * 创建日期:2021-9-22
	 * @param newCurrentpaymentamount nc.vo.pub.lang.UFDouble
	 */
	public void setCurrentpaymentamount (nc.vo.pub.lang.UFDouble newCurrentpaymentamount ) {
	 	this.currentpaymentamount = newCurrentpaymentamount;
	} 	 
	
	/**
	 * 属性 taxrate的Getter方法.属性名：税率
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getTaxrate () {
		return taxrate;
	}   
	/**
	 * 属性taxrate的Setter方法.属性名：税率
	 * 创建日期:2021-9-22
	 * @param newTaxrate nc.vo.pub.lang.UFDouble
	 */
	public void setTaxrate (nc.vo.pub.lang.UFDouble newTaxrate ) {
	 	this.taxrate = newTaxrate;
	} 	 
	
	/**
	 * 属性 taxamount的Getter方法.属性名：税额
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getTaxamount () {
		return taxamount;
	}   
	/**
	 * 属性taxamount的Setter方法.属性名：税额
	 * 创建日期:2021-9-22
	 * @param newTaxamount nc.vo.pub.lang.UFDouble
	 */
	public void setTaxamount (nc.vo.pub.lang.UFDouble newTaxamount ) {
	 	this.taxamount = newTaxamount;
	} 	 
	
	/**
	 * 属性 currentpaymentamounta的Getter方法.属性名：本次付款金额(合计金额)
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getCurrentpaymentamounta () {
		return currentpaymentamounta;
	}   
	/**
	 * 属性currentpaymentamounta的Setter方法.属性名：本次付款金额(合计金额)
	 * 创建日期:2021-9-22
	 * @param newCurrentpaymentamounta nc.vo.pub.lang.UFDouble
	 */
	public void setCurrentpaymentamounta (nc.vo.pub.lang.UFDouble newCurrentpaymentamounta ) {
	 	this.currentpaymentamounta = newCurrentpaymentamounta;
	} 	 
	
	/**
	 * 属性 money的Getter方法.属性名：金额
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getMoney () {
		return money;
	}   
	/**
	 * 属性money的Setter方法.属性名：金额
	 * 创建日期:2021-9-22
	 * @param newMoney nc.vo.pub.lang.UFDouble
	 */
	public void setMoney (nc.vo.pub.lang.UFDouble newMoney ) {
	 	this.money = newMoney;
	} 	 
	
	/**
	 * 属性 unitmoney的Getter方法.属性名：单价
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getUnitmoney () {
		return unitmoney;
	}   
	/**
	 * 属性unitmoney的Setter方法.属性名：单价
	 * 创建日期:2021-9-22
	 * @param newUnitmoney nc.vo.pub.lang.UFDouble
	 */
	public void setUnitmoney (nc.vo.pub.lang.UFDouble newUnitmoney ) {
	 	this.unitmoney = newUnitmoney;
	} 	 
	
	/**
	 * 属性 num的Getter方法.属性名：数量
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getNum () {
		return num;
	}   
	/**
	 * 属性num的Setter方法.属性名：数量
	 * 创建日期:2021-9-22
	 * @param newNum nc.vo.pub.lang.UFDouble
	 */
	public void setNum (nc.vo.pub.lang.UFDouble newNum ) {
	 	this.num = newNum;
	} 	 
	
	/**
	 * 属性 dr的Getter方法.属性名：dr
	 *  创建日期:2021-9-22
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.属性名：dr
	 * 创建日期:2021-9-22
	 * @param newDr java.lang.Integer
	 */
	public void setDr (java.lang.Integer newDr ) {
	 	this.dr = newDr;
	} 	 
	
	/**
	 * 属性 ts的Getter方法.属性名：ts
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.属性名：ts
	 * 创建日期:2021-9-22
	 * @param newTs nc.vo.pub.lang.UFDateTime
	 */
	public void setTs (nc.vo.pub.lang.UFDateTime newTs ) {
	 	this.ts = newTs;
	} 	 
	
	
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2021-9-22
	  * @return java.lang.String
	  */
	public java.lang.String getParentPKFieldName() {		
		return "pk_rl_contr_sche";
	}   
    
	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2021-9-22
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
			
		return "pk_rl_contr_sche_b";
	}
    
	/**
	 * <p>返回表名称
	 * <p>
	 * 创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "pm_rlcontractschedule_ct_b";
	}    
	
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "pm_rlcontractschedule_ct_b";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2021-9-22
	  */
     public RLContractscheduleBVO() {
		super();	
	}    
	
	
	@nc.vo.annotation.MDEntityInfo(beanFullclassName = "nc.vo.pm.rlcontractschedule.RLContractscheduleBVO" )
	public IVOMeta getMetaData() {
		
		IVOMeta meta = VOMetaFactory.getInstance().getVOMeta("pm" + "." + "pm_rlcontractschedule_ct_b");
   		return meta;
		
   	}
     
}