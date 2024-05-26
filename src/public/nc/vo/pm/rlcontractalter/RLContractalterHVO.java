package nc.vo.pm.rlcontractalter;
//多遍码补充协议表头
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
public class RLContractalterHVO extends nc.vo.pub.SuperVO{
	
    private java.lang.String pk_contr_alter;
    private java.lang.String auditor;
    private java.lang.String check_opinion;
    private nc.vo.pub.lang.UFDateTime audittime;
    private java.lang.String bill_code;
    private java.lang.Integer bill_status;
    private java.lang.String bill_type;
    private java.lang.String billmaker;
    private nc.vo.pub.lang.UFDate billmaketime;
    private nc.vo.pub.lang.UFDate creationtime;
    private java.lang.String creator;
    private nc.vo.pub.lang.UFDateTime modifiedtime;
    private java.lang.String modifier;
    private java.lang.String pk_group;
    private java.lang.String pk_org;
    private java.lang.String pk_org_v;
    private java.lang.String pk_project;
    private java.lang.String pk_transitype;
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
    private java.lang.String pk_supplier_name;
    private java.lang.String pk_dkdept;
    private java.lang.String pk_dkdept_v;
    private java.lang.String pk_qydept;
    private java.lang.String pk_qydept_v;
    private nc.vo.pub.lang.UFDouble contractmoney;
    private nc.vo.pub.lang.UFDate signdate;
    private java.lang.String contracttype;
    private java.lang.String pk_lead_name;
    private java.lang.String pk_manager_name;
    private java.lang.String zbmethod;
    private nc.vo.pub.lang.UFDate startdate;
    private nc.vo.pub.lang.UFDate enddate;
    private java.lang.String pk_signer;
    private java.lang.String pk_signer_name;
    private java.lang.String vmemo;
    private nc.vo.pub.lang.UFDouble protocontractmoney;
    private nc.vo.pub.lang.UFDouble paymoney;
    private nc.vo.pub.lang.UFDouble addpaymoney;
    private nc.vo.pub.lang.UFDate dbilldate;
    private java.lang.String bill_name;
    private java.lang.String pk_contr;
    private java.lang.Integer dr = 0;
    private nc.vo.pub.lang.UFDateTime ts;    
    private java.lang.String pk_billtype;
    public static final String PK_BILLTYPE = "pk_billtype";
	
    private nc.vo.pm.rlcontractalter.RLContractalterBVO[] pk_contr_alter_b;
	
    public static final String PK_CONTR_ALTER = "pk_contr_alter";
    public static final String AUDITOR = "auditor";
    public static final String CHECK_OPINION = "check_opinion";
    public static final String AUDITTIME = "audittime";
    public static final String BILL_CODE = "bill_code";
    public static final String BILL_STATUS = "bill_status";
    public static final String BILL_TYPE = "bill_type";
    public static final String BILLMAKER = "billmaker";
    public static final String BILLMAKETIME = "billmaketime";
    public static final String CREATIONTIME = "creationtime";
    public static final String CREATOR = "creator";
    public static final String MODIFIEDTIME = "modifiedtime";
    public static final String MODIFIER = "modifier";
    public static final String PK_GROUP = "pk_group";
    public static final String PK_ORG = "pk_org";
    public static final String PK_ORG_V = "pk_org_v";
    public static final String PK_PROJECT = "pk_project";
    public static final String PK_TRANSITYPE = "pk_transitype";
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
    public static final String PK_SUPPLIER_NAME = "pk_supplier_name";
    public static final String PK_DKDEPT = "pk_dkdept";
    public static final String PK_DKDEPT_V = "pk_dkdept_v";
    public static final String PK_QYDEPT = "pk_qydept";
    public static final String PK_QYDEPT_V = "pk_qydept_v";
    public static final String CONTRACTMONEY = "contractmoney";
    public static final String SIGNDATE = "signdate";
    public static final String CONTRACTTYPE = "contracttype";
    public static final String PK_LEAD_NAME = "pk_lead_name";
    public static final String PK_MANAGER_NAME = "pk_manager_name";
    public static final String ZBMETHOD = "zbmethod";
    public static final String STARTDATE = "startdate";
    public static final String ENDDATE = "enddate";
    public static final String PK_SIGNER = "pk_signer";
    public static final String PK_SIGNER_NAME = "pk_signer_name";
    public static final String VMEMO = "vmemo";
    public static final String PROTOCONTRACTMONEY = "protocontractmoney";
    public static final String PAYMONEY = "paymoney";
    public static final String ADDPAYMONEY = "addpaymoney";
    public static final String DBILLDATE = "dbilldate";
    public static final String BILL_NAME = "bill_name";
    public static final String PK_CONTR = "pk_contr";

	/**
	 * 属性 pk_contr_alter的Getter方法.属性名：合同变更单主键
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_contr_alter () {
		return pk_contr_alter;
	}   
	/**
	 * 属性pk_contr_alter的Setter方法.属性名：合同变更单主键
	 * 创建日期:2021-9-22
	 * @param newPk_contr_alter java.lang.String
	 */
	public void setPk_contr_alter (java.lang.String newPk_contr_alter ) {
	 	this.pk_contr_alter = newPk_contr_alter;
	} 	 
	
	/**
	 * 属性 pk_contr_alter_b的Getter方法.属性名：合同基本变更主键
	 *  创建日期:2021-9-22
	 * @return nc.vo.pm.rlcontractalter.RLContractalterBVO[]
	 */
	public nc.vo.pm.rlcontractalter.RLContractalterBVO[] getPk_contr_alter_b () {
		return pk_contr_alter_b;
	}   
	/**
	 * 属性pk_contr_alter_b的Setter方法.属性名：合同基本变更主键
	 * 创建日期:2021-9-22
	 * @param newPk_contr_alter_b nc.vo.pm.rlcontractalter.RLContractalterBVO[]
	 */
	public void setPk_contr_alter_b (nc.vo.pm.rlcontractalter.RLContractalterBVO[] newPk_contr_alter_b ) {
	 	this.pk_contr_alter_b = newPk_contr_alter_b;
	} 	 
	
	/**
	 * 属性 auditor的Getter方法.属性名：审批人
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getAuditor () {
		return auditor;
	}   
	/**
	 * 属性auditor的Setter方法.属性名：审批人
	 * 创建日期:2021-9-22
	 * @param newAuditor java.lang.String
	 */
	public void setAuditor (java.lang.String newAuditor ) {
	 	this.auditor = newAuditor;
	} 	 
	
	/**
	 * 属性 check_opinion的Getter方法.属性名：审批意见
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getCheck_opinion () {
		return check_opinion;
	}   
	/**
	 * 属性check_opinion的Setter方法.属性名：审批意见
	 * 创建日期:2021-9-22
	 * @param newCheck_opinion java.lang.String
	 */
	public void setCheck_opinion (java.lang.String newCheck_opinion ) {
	 	this.check_opinion = newCheck_opinion;
	} 	 
	
	/**
	 * 属性 audittime的Getter方法.属性名：审批日期
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getAudittime () {
		return audittime;
	}   
	/**
	 * 属性audittime的Setter方法.属性名：审批日期
	 * 创建日期:2021-9-22
	 * @param newAudittime nc.vo.pub.lang.UFDateTime
	 */
	public void setAudittime (nc.vo.pub.lang.UFDateTime newAudittime ) {
	 	this.audittime = newAudittime;
	} 	 
	
	/**
	 * 属性 bill_code的Getter方法.属性名：补充协议号
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getBill_code () {
		return bill_code;
	}   
	/**
	 * 属性bill_code的Setter方法.属性名：补充协议号
	 * 创建日期:2021-9-22
	 * @param newBill_code java.lang.String
	 */
	public void setBill_code (java.lang.String newBill_code ) {
	 	this.bill_code = newBill_code;
	} 	 
	
	/**
	 * 属性 bill_status的Getter方法.属性名：单据状态
	 *  创建日期:2021-9-22
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getBill_status () {
		return bill_status;
	}   
	/**
	 * 属性bill_status的Setter方法.属性名：单据状态
	 * 创建日期:2021-9-22
	 * @param newBill_status java.lang.Integer
	 */
	public void setBill_status (java.lang.Integer newBill_status ) {
	 	this.bill_status = newBill_status;
	} 	 
	
	/**
	 * 属性 bill_type的Getter方法.属性名：单据类型
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getBill_type () {
		return bill_type;
	}   
	/**
	 * 属性bill_type的Setter方法.属性名：单据类型
	 * 创建日期:2021-9-22
	 * @param newBill_type java.lang.String
	 */
	public void setBill_type (java.lang.String newBill_type ) {
	 	this.bill_type = newBill_type;
	} 	 
	
	/**
	 * 属性 billmaker的Getter方法.属性名：制单人
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getBillmaker () {
		return billmaker;
	}   
	/**
	 * 属性billmaker的Setter方法.属性名：制单人
	 * 创建日期:2021-9-22
	 * @param newBillmaker java.lang.String
	 */
	public void setBillmaker (java.lang.String newBillmaker ) {
	 	this.billmaker = newBillmaker;
	} 	 
	
	/**
	 * 属性 billmaketime的Getter方法.属性名：制单日期
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDate
	 */
	public nc.vo.pub.lang.UFDate getBillmaketime () {
		return billmaketime;
	}   
	/**
	 * 属性billmaketime的Setter方法.属性名：制单日期
	 * 创建日期:2021-9-22
	 * @param newBillmaketime nc.vo.pub.lang.UFDate
	 */
	public void setBillmaketime (nc.vo.pub.lang.UFDate newBillmaketime ) {
	 	this.billmaketime = newBillmaketime;
	} 	 
	
	/**
	 * 属性 creationtime的Getter方法.属性名：创建时间
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDate
	 */
	public nc.vo.pub.lang.UFDate getCreationtime () {
		return creationtime;
	}   
	/**
	 * 属性creationtime的Setter方法.属性名：创建时间
	 * 创建日期:2021-9-22
	 * @param newCreationtime nc.vo.pub.lang.UFDate
	 */
	public void setCreationtime (nc.vo.pub.lang.UFDate newCreationtime ) {
	 	this.creationtime = newCreationtime;
	} 	 
	
	/**
	 * 属性 creator的Getter方法.属性名：创建人
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getCreator () {
		return creator;
	}   
	/**
	 * 属性creator的Setter方法.属性名：创建人
	 * 创建日期:2021-9-22
	 * @param newCreator java.lang.String
	 */
	public void setCreator (java.lang.String newCreator ) {
	 	this.creator = newCreator;
	} 	 
	
	/**
	 * 属性 modifiedtime的Getter方法.属性名：最后修改时间
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getModifiedtime () {
		return modifiedtime;
	}   
	/**
	 * 属性modifiedtime的Setter方法.属性名：最后修改时间
	 * 创建日期:2021-9-22
	 * @param newModifiedtime nc.vo.pub.lang.UFDateTime
	 */
	public void setModifiedtime (nc.vo.pub.lang.UFDateTime newModifiedtime ) {
	 	this.modifiedtime = newModifiedtime;
	} 	 
	
	/**
	 * 属性 modifier的Getter方法.属性名：最后修改人
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getModifier () {
		return modifier;
	}   
	/**
	 * 属性modifier的Setter方法.属性名：最后修改人
	 * 创建日期:2021-9-22
	 * @param newModifier java.lang.String
	 */
	public void setModifier (java.lang.String newModifier ) {
	 	this.modifier = newModifier;
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
	 * 属性 pk_org的Getter方法.属性名：项目组织最新版本
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_org () {
		return pk_org;
	}   
	/**
	 * 属性pk_org的Setter方法.属性名：项目组织最新版本
	 * 创建日期:2021-9-22
	 * @param newPk_org java.lang.String
	 */
	public void setPk_org (java.lang.String newPk_org ) {
	 	this.pk_org = newPk_org;
	} 	 
	
	/**
	 * 属性 pk_org_v的Getter方法.属性名：项目组织
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_org_v () {
		return pk_org_v;
	}   
	/**
	 * 属性pk_org_v的Setter方法.属性名：项目组织
	 * 创建日期:2021-9-22
	 * @param newPk_org_v java.lang.String
	 */
	public void setPk_org_v (java.lang.String newPk_org_v ) {
	 	this.pk_org_v = newPk_org_v;
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
	 * 属性 pk_transitype的Getter方法.属性名：交易类型
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_transitype () {
		return pk_transitype;
	}   
	/**
	 * 属性pk_transitype的Setter方法.属性名：交易类型
	 * 创建日期:2021-9-22
	 * @param newPk_transitype java.lang.String
	 */
	public void setPk_transitype (java.lang.String newPk_transitype ) {
	 	this.pk_transitype = newPk_transitype;
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
	 * 属性 pk_supplier_name的Getter方法.属性名：供应商名称
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_supplier_name () {
		return pk_supplier_name;
	}   
	/**
	 * 属性pk_supplier_name的Setter方法.属性名：供应商名称
	 * 创建日期:2021-9-22
	 * @param newPk_supplier_name java.lang.String
	 */
	public void setPk_supplier_name (java.lang.String newPk_supplier_name ) {
	 	this.pk_supplier_name = newPk_supplier_name;
	} 	 
	
	/**
	 * 属性 pk_dkdept的Getter方法.属性名：代控部门
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_dkdept () {
		return pk_dkdept;
	}   
	/**
	 * 属性pk_dkdept的Setter方法.属性名：代控部门
	 * 创建日期:2021-9-22
	 * @param newPk_dkdept java.lang.String
	 */
	public void setPk_dkdept (java.lang.String newPk_dkdept ) {
	 	this.pk_dkdept = newPk_dkdept;
	} 	 
	
	/**
	 * 属性 pk_dkdept_v的Getter方法.属性名：代控部门版本信息
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_dkdept_v () {
		return pk_dkdept_v;
	}   
	/**
	 * 属性pk_dkdept_v的Setter方法.属性名：代控部门版本信息
	 * 创建日期:2021-9-22
	 * @param newPk_dkdept_v java.lang.String
	 */
	public void setPk_dkdept_v (java.lang.String newPk_dkdept_v ) {
	 	this.pk_dkdept_v = newPk_dkdept_v;
	} 	 
	
	/**
	 * 属性 pk_qydept的Getter方法.属性名：签约部门
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_qydept () {
		return pk_qydept;
	}   
	/**
	 * 属性pk_qydept的Setter方法.属性名：签约部门
	 * 创建日期:2021-9-22
	 * @param newPk_qydept java.lang.String
	 */
	public void setPk_qydept (java.lang.String newPk_qydept ) {
	 	this.pk_qydept = newPk_qydept;
	} 	 
	
	/**
	 * 属性 pk_qydept_v的Getter方法.属性名：签约部门版本信息
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_qydept_v () {
		return pk_qydept_v;
	}   
	/**
	 * 属性pk_qydept_v的Setter方法.属性名：签约部门版本信息
	 * 创建日期:2021-9-22
	 * @param newPk_qydept_v java.lang.String
	 */
	public void setPk_qydept_v (java.lang.String newPk_qydept_v ) {
	 	this.pk_qydept_v = newPk_qydept_v;
	} 	 
	
	/**
	 * 属性 contractmoney的Getter方法.属性名：合同金额
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getContractmoney () {
		return contractmoney;
	}   
	/**
	 * 属性contractmoney的Setter方法.属性名：合同金额
	 * 创建日期:2021-9-22
	 * @param newContractmoney nc.vo.pub.lang.UFDouble
	 */
	public void setContractmoney (nc.vo.pub.lang.UFDouble newContractmoney ) {
	 	this.contractmoney = newContractmoney;
	} 	 
	
	/**
	 * 属性 signdate的Getter方法.属性名：签约日期
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDate
	 */
	public nc.vo.pub.lang.UFDate getSigndate () {
		return signdate;
	}   
	/**
	 * 属性signdate的Setter方法.属性名：签约日期
	 * 创建日期:2021-9-22
	 * @param newSigndate nc.vo.pub.lang.UFDate
	 */
	public void setSigndate (nc.vo.pub.lang.UFDate newSigndate ) {
	 	this.signdate = newSigndate;
	} 	 
	
	/**
	 * 属性 contracttype的Getter方法.属性名：合同类型
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getContracttype () {
		return contracttype;
	}   
	/**
	 * 属性contracttype的Setter方法.属性名：合同类型
	 * 创建日期:2021-9-22
	 * @param newContracttype java.lang.String
	 */
	public void setContracttype (java.lang.String newContracttype ) {
	 	this.contracttype = newContracttype;
	} 	 
	
	/**
	 * 属性 pk_lead_name的Getter方法.属性名：分管领导名称
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_lead_name () {
		return pk_lead_name;
	}   
	/**
	 * 属性pk_lead_name的Setter方法.属性名：分管领导名称
	 * 创建日期:2021-9-22
	 * @param newPk_lead_name java.lang.String
	 */
	public void setPk_lead_name (java.lang.String newPk_lead_name ) {
	 	this.pk_lead_name = newPk_lead_name;
	} 	 
	
	/**
	 * 属性 pk_manager_name的Getter方法.属性名：项目经理名称
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_manager_name () {
		return pk_manager_name;
	}   
	/**
	 * 属性pk_manager_name的Setter方法.属性名：项目经理名称
	 * 创建日期:2021-9-22
	 * @param newPk_manager_name java.lang.String
	 */
	public void setPk_manager_name (java.lang.String newPk_manager_name ) {
	 	this.pk_manager_name = newPk_manager_name;
	} 	 
	
	/**
	 * 属性 zbmethod的Getter方法.属性名：招标方式
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getZbmethod () {
		return zbmethod;
	}   
	/**
	 * 属性zbmethod的Setter方法.属性名：招标方式
	 * 创建日期:2021-9-22
	 * @param newZbmethod java.lang.String
	 */
	public void setZbmethod (java.lang.String newZbmethod ) {
	 	this.zbmethod = newZbmethod;
	} 	 
	
	/**
	 * 属性 startdate的Getter方法.属性名：开始日期
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDate
	 */
	public nc.vo.pub.lang.UFDate getStartdate () {
		return startdate;
	}   
	/**
	 * 属性startdate的Setter方法.属性名：开始日期
	 * 创建日期:2021-9-22
	 * @param newStartdate nc.vo.pub.lang.UFDate
	 */
	public void setStartdate (nc.vo.pub.lang.UFDate newStartdate ) {
	 	this.startdate = newStartdate;
	} 	 
	
	/**
	 * 属性 enddate的Getter方法.属性名：结束日期
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDate
	 */
	public nc.vo.pub.lang.UFDate getEnddate () {
		return enddate;
	}   
	/**
	 * 属性enddate的Setter方法.属性名：结束日期
	 * 创建日期:2021-9-22
	 * @param newEnddate nc.vo.pub.lang.UFDate
	 */
	public void setEnddate (nc.vo.pub.lang.UFDate newEnddate ) {
	 	this.enddate = newEnddate;
	} 	 
	
	/**
	 * 属性 pk_signer的Getter方法.属性名：签约人主键
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_signer () {
		return pk_signer;
	}   
	/**
	 * 属性pk_signer的Setter方法.属性名：签约人主键
	 * 创建日期:2021-9-22
	 * @param newPk_signer java.lang.String
	 */
	public void setPk_signer (java.lang.String newPk_signer ) {
	 	this.pk_signer = newPk_signer;
	} 	 
	
	/**
	 * 属性 pk_signer_name的Getter方法.属性名：签约人名称
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_signer_name () {
		return pk_signer_name;
	}   
	/**
	 * 属性pk_signer_name的Setter方法.属性名：签约人名称
	 * 创建日期:2021-9-22
	 * @param newPk_signer_name java.lang.String
	 */
	public void setPk_signer_name (java.lang.String newPk_signer_name ) {
	 	this.pk_signer_name = newPk_signer_name;
	} 	 
	
	/**
	 * 属性 vmemo的Getter方法.属性名：备注
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getVmemo () {
		return vmemo;
	}   
	/**
	 * 属性vmemo的Setter方法.属性名：备注
	 * 创建日期:2021-9-22
	 * @param newVmemo java.lang.String
	 */
	public void setVmemo (java.lang.String newVmemo ) {
	 	this.vmemo = newVmemo;
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
	 * 属性 dbilldate的Getter方法.属性名：单据日期
	 *  创建日期:2021-9-22
	 * @return nc.vo.pub.lang.UFDate
	 */
	public nc.vo.pub.lang.UFDate getDbilldate () {
		return dbilldate;
	}   
	/**
	 * 属性dbilldate的Setter方法.属性名：单据日期
	 * 创建日期:2021-9-22
	 * @param newDbilldate nc.vo.pub.lang.UFDate
	 */
	public void setDbilldate (nc.vo.pub.lang.UFDate newDbilldate ) {
	 	this.dbilldate = newDbilldate;
	} 	 
	
	/**
	 * 属性 bill_name的Getter方法.属性名：合同名称
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getBill_name () {
		return bill_name;
	}   
	/**
	 * 属性bill_name的Setter方法.属性名：合同名称
	 * 创建日期:2021-9-22
	 * @param newBill_name java.lang.String
	 */
	public void setBill_name (java.lang.String newBill_name ) {
	 	this.bill_name = newBill_name;
	} 	 
	
	/**
	 * 属性 pk_contr的Getter方法.属性名：合同编码
	 *  创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_contr () {
		return pk_contr;
	}   
	/**
	 * 属性pk_contr的Setter方法.属性名：合同编码
	 * 创建日期:2021-9-22
	 * @param newPk_contr java.lang.String
	 */
	public void setPk_contr (java.lang.String newPk_contr ) {
	 	this.pk_contr = newPk_contr;
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
	    return null;
	}   
    
	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2021-9-22
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
			
		return "pk_contr_alter";
	}
    
	/**
	 * <p>返回表名称
	 * <p>
	 * 创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "pm_rlcontractalter_ct";
	}    
	
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2021-9-22
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "pm_rlcontractalter_ct";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2021-9-22
	  */
     public RLContractalterHVO() {
		super();	
	}    
	
	
	@nc.vo.annotation.MDEntityInfo(beanFullclassName = "nc.vo.pm.rlcontractalter.RLContractalterHVO" )
	public IVOMeta getMetaData() {
		IVOMeta meta = VOMetaFactory.getInstance().getVOMeta("pm" + "." + "pm_rlcontractalter_ct");
		return meta;
		
   	}
	public java.lang.String getPk_billtype() {
		return pk_billtype;
	}
	public void setPk_billtype(java.lang.String pk_billtype) {
		this.pk_billtype = pk_billtype;
	}
     
}