package nc.vo.pm.rlcontractalter;
//����벹��Э���ͷ
import nc.vo.pub.*;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> �˴���Ҫ�������๦�� </b>
 * <p>
 *   �˴�������������Ϣ
 * </p>
 *  ��������:2021-9-22
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
	 * ���� pk_contr_alter��Getter����.����������ͬ���������
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_contr_alter () {
		return pk_contr_alter;
	}   
	/**
	 * ����pk_contr_alter��Setter����.����������ͬ���������
	 * ��������:2021-9-22
	 * @param newPk_contr_alter java.lang.String
	 */
	public void setPk_contr_alter (java.lang.String newPk_contr_alter ) {
	 	this.pk_contr_alter = newPk_contr_alter;
	} 	 
	
	/**
	 * ���� pk_contr_alter_b��Getter����.����������ͬ�����������
	 *  ��������:2021-9-22
	 * @return nc.vo.pm.rlcontractalter.RLContractalterBVO[]
	 */
	public nc.vo.pm.rlcontractalter.RLContractalterBVO[] getPk_contr_alter_b () {
		return pk_contr_alter_b;
	}   
	/**
	 * ����pk_contr_alter_b��Setter����.����������ͬ�����������
	 * ��������:2021-9-22
	 * @param newPk_contr_alter_b nc.vo.pm.rlcontractalter.RLContractalterBVO[]
	 */
	public void setPk_contr_alter_b (nc.vo.pm.rlcontractalter.RLContractalterBVO[] newPk_contr_alter_b ) {
	 	this.pk_contr_alter_b = newPk_contr_alter_b;
	} 	 
	
	/**
	 * ���� auditor��Getter����.��������������
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getAuditor () {
		return auditor;
	}   
	/**
	 * ����auditor��Setter����.��������������
	 * ��������:2021-9-22
	 * @param newAuditor java.lang.String
	 */
	public void setAuditor (java.lang.String newAuditor ) {
	 	this.auditor = newAuditor;
	} 	 
	
	/**
	 * ���� check_opinion��Getter����.���������������
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getCheck_opinion () {
		return check_opinion;
	}   
	/**
	 * ����check_opinion��Setter����.���������������
	 * ��������:2021-9-22
	 * @param newCheck_opinion java.lang.String
	 */
	public void setCheck_opinion (java.lang.String newCheck_opinion ) {
	 	this.check_opinion = newCheck_opinion;
	} 	 
	
	/**
	 * ���� audittime��Getter����.����������������
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getAudittime () {
		return audittime;
	}   
	/**
	 * ����audittime��Setter����.����������������
	 * ��������:2021-9-22
	 * @param newAudittime nc.vo.pub.lang.UFDateTime
	 */
	public void setAudittime (nc.vo.pub.lang.UFDateTime newAudittime ) {
	 	this.audittime = newAudittime;
	} 	 
	
	/**
	 * ���� bill_code��Getter����.������������Э���
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getBill_code () {
		return bill_code;
	}   
	/**
	 * ����bill_code��Setter����.������������Э���
	 * ��������:2021-9-22
	 * @param newBill_code java.lang.String
	 */
	public void setBill_code (java.lang.String newBill_code ) {
	 	this.bill_code = newBill_code;
	} 	 
	
	/**
	 * ���� bill_status��Getter����.������������״̬
	 *  ��������:2021-9-22
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getBill_status () {
		return bill_status;
	}   
	/**
	 * ����bill_status��Setter����.������������״̬
	 * ��������:2021-9-22
	 * @param newBill_status java.lang.Integer
	 */
	public void setBill_status (java.lang.Integer newBill_status ) {
	 	this.bill_status = newBill_status;
	} 	 
	
	/**
	 * ���� bill_type��Getter����.����������������
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getBill_type () {
		return bill_type;
	}   
	/**
	 * ����bill_type��Setter����.����������������
	 * ��������:2021-9-22
	 * @param newBill_type java.lang.String
	 */
	public void setBill_type (java.lang.String newBill_type ) {
	 	this.bill_type = newBill_type;
	} 	 
	
	/**
	 * ���� billmaker��Getter����.���������Ƶ���
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getBillmaker () {
		return billmaker;
	}   
	/**
	 * ����billmaker��Setter����.���������Ƶ���
	 * ��������:2021-9-22
	 * @param newBillmaker java.lang.String
	 */
	public void setBillmaker (java.lang.String newBillmaker ) {
	 	this.billmaker = newBillmaker;
	} 	 
	
	/**
	 * ���� billmaketime��Getter����.���������Ƶ�����
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDate
	 */
	public nc.vo.pub.lang.UFDate getBillmaketime () {
		return billmaketime;
	}   
	/**
	 * ����billmaketime��Setter����.���������Ƶ�����
	 * ��������:2021-9-22
	 * @param newBillmaketime nc.vo.pub.lang.UFDate
	 */
	public void setBillmaketime (nc.vo.pub.lang.UFDate newBillmaketime ) {
	 	this.billmaketime = newBillmaketime;
	} 	 
	
	/**
	 * ���� creationtime��Getter����.������������ʱ��
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDate
	 */
	public nc.vo.pub.lang.UFDate getCreationtime () {
		return creationtime;
	}   
	/**
	 * ����creationtime��Setter����.������������ʱ��
	 * ��������:2021-9-22
	 * @param newCreationtime nc.vo.pub.lang.UFDate
	 */
	public void setCreationtime (nc.vo.pub.lang.UFDate newCreationtime ) {
	 	this.creationtime = newCreationtime;
	} 	 
	
	/**
	 * ���� creator��Getter����.��������������
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getCreator () {
		return creator;
	}   
	/**
	 * ����creator��Setter����.��������������
	 * ��������:2021-9-22
	 * @param newCreator java.lang.String
	 */
	public void setCreator (java.lang.String newCreator ) {
	 	this.creator = newCreator;
	} 	 
	
	/**
	 * ���� modifiedtime��Getter����.������������޸�ʱ��
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getModifiedtime () {
		return modifiedtime;
	}   
	/**
	 * ����modifiedtime��Setter����.������������޸�ʱ��
	 * ��������:2021-9-22
	 * @param newModifiedtime nc.vo.pub.lang.UFDateTime
	 */
	public void setModifiedtime (nc.vo.pub.lang.UFDateTime newModifiedtime ) {
	 	this.modifiedtime = newModifiedtime;
	} 	 
	
	/**
	 * ���� modifier��Getter����.������������޸���
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getModifier () {
		return modifier;
	}   
	/**
	 * ����modifier��Setter����.������������޸���
	 * ��������:2021-9-22
	 * @param newModifier java.lang.String
	 */
	public void setModifier (java.lang.String newModifier ) {
	 	this.modifier = newModifier;
	} 	 
	
	/**
	 * ���� pk_group��Getter����.������������
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_group () {
		return pk_group;
	}   
	/**
	 * ����pk_group��Setter����.������������
	 * ��������:2021-9-22
	 * @param newPk_group java.lang.String
	 */
	public void setPk_group (java.lang.String newPk_group ) {
	 	this.pk_group = newPk_group;
	} 	 
	
	/**
	 * ���� pk_org��Getter����.����������Ŀ��֯���°汾
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_org () {
		return pk_org;
	}   
	/**
	 * ����pk_org��Setter����.����������Ŀ��֯���°汾
	 * ��������:2021-9-22
	 * @param newPk_org java.lang.String
	 */
	public void setPk_org (java.lang.String newPk_org ) {
	 	this.pk_org = newPk_org;
	} 	 
	
	/**
	 * ���� pk_org_v��Getter����.����������Ŀ��֯
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_org_v () {
		return pk_org_v;
	}   
	/**
	 * ����pk_org_v��Setter����.����������Ŀ��֯
	 * ��������:2021-9-22
	 * @param newPk_org_v java.lang.String
	 */
	public void setPk_org_v (java.lang.String newPk_org_v ) {
	 	this.pk_org_v = newPk_org_v;
	} 	 
	
	/**
	 * ���� pk_project��Getter����.����������Ŀ
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_project () {
		return pk_project;
	}   
	/**
	 * ����pk_project��Setter����.����������Ŀ
	 * ��������:2021-9-22
	 * @param newPk_project java.lang.String
	 */
	public void setPk_project (java.lang.String newPk_project ) {
	 	this.pk_project = newPk_project;
	} 	 
	
	/**
	 * ���� pk_transitype��Getter����.����������������
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_transitype () {
		return pk_transitype;
	}   
	/**
	 * ����pk_transitype��Setter����.����������������
	 * ��������:2021-9-22
	 * @param newPk_transitype java.lang.String
	 */
	public void setPk_transitype (java.lang.String newPk_transitype ) {
	 	this.pk_transitype = newPk_transitype;
	} 	 
	
	/**
	 * ���� hdef1��Getter����.���������Զ�����1
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef1 () {
		return hdef1;
	}   
	/**
	 * ����hdef1��Setter����.���������Զ�����1
	 * ��������:2021-9-22
	 * @param newHdef1 java.lang.String
	 */
	public void setHdef1 (java.lang.String newHdef1 ) {
	 	this.hdef1 = newHdef1;
	} 	 
	
	/**
	 * ���� hdef2��Getter����.���������Զ�����2
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef2 () {
		return hdef2;
	}   
	/**
	 * ����hdef2��Setter����.���������Զ�����2
	 * ��������:2021-9-22
	 * @param newHdef2 java.lang.String
	 */
	public void setHdef2 (java.lang.String newHdef2 ) {
	 	this.hdef2 = newHdef2;
	} 	 
	
	/**
	 * ���� hdef3��Getter����.���������Զ�����3
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef3 () {
		return hdef3;
	}   
	/**
	 * ����hdef3��Setter����.���������Զ�����3
	 * ��������:2021-9-22
	 * @param newHdef3 java.lang.String
	 */
	public void setHdef3 (java.lang.String newHdef3 ) {
	 	this.hdef3 = newHdef3;
	} 	 
	
	/**
	 * ���� hdef4��Getter����.���������Զ�����4
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef4 () {
		return hdef4;
	}   
	/**
	 * ����hdef4��Setter����.���������Զ�����4
	 * ��������:2021-9-22
	 * @param newHdef4 java.lang.String
	 */
	public void setHdef4 (java.lang.String newHdef4 ) {
	 	this.hdef4 = newHdef4;
	} 	 
	
	/**
	 * ���� hdef5��Getter����.���������Զ�����5
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef5 () {
		return hdef5;
	}   
	/**
	 * ����hdef5��Setter����.���������Զ�����5
	 * ��������:2021-9-22
	 * @param newHdef5 java.lang.String
	 */
	public void setHdef5 (java.lang.String newHdef5 ) {
	 	this.hdef5 = newHdef5;
	} 	 
	
	/**
	 * ���� hdef6��Getter����.���������Զ�����6
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef6 () {
		return hdef6;
	}   
	/**
	 * ����hdef6��Setter����.���������Զ�����6
	 * ��������:2021-9-22
	 * @param newHdef6 java.lang.String
	 */
	public void setHdef6 (java.lang.String newHdef6 ) {
	 	this.hdef6 = newHdef6;
	} 	 
	
	/**
	 * ���� hdef7��Getter����.���������Զ�����7
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef7 () {
		return hdef7;
	}   
	/**
	 * ����hdef7��Setter����.���������Զ�����7
	 * ��������:2021-9-22
	 * @param newHdef7 java.lang.String
	 */
	public void setHdef7 (java.lang.String newHdef7 ) {
	 	this.hdef7 = newHdef7;
	} 	 
	
	/**
	 * ���� hdef8��Getter����.���������Զ�����8
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef8 () {
		return hdef8;
	}   
	/**
	 * ����hdef8��Setter����.���������Զ�����8
	 * ��������:2021-9-22
	 * @param newHdef8 java.lang.String
	 */
	public void setHdef8 (java.lang.String newHdef8 ) {
	 	this.hdef8 = newHdef8;
	} 	 
	
	/**
	 * ���� hdef9��Getter����.���������Զ�����9
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef9 () {
		return hdef9;
	}   
	/**
	 * ����hdef9��Setter����.���������Զ�����9
	 * ��������:2021-9-22
	 * @param newHdef9 java.lang.String
	 */
	public void setHdef9 (java.lang.String newHdef9 ) {
	 	this.hdef9 = newHdef9;
	} 	 
	
	/**
	 * ���� hdef10��Getter����.���������Զ�����10
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef10 () {
		return hdef10;
	}   
	/**
	 * ����hdef10��Setter����.���������Զ�����10
	 * ��������:2021-9-22
	 * @param newHdef10 java.lang.String
	 */
	public void setHdef10 (java.lang.String newHdef10 ) {
	 	this.hdef10 = newHdef10;
	} 	 
	
	/**
	 * ���� hdef11��Getter����.���������Զ�����11
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef11 () {
		return hdef11;
	}   
	/**
	 * ����hdef11��Setter����.���������Զ�����11
	 * ��������:2021-9-22
	 * @param newHdef11 java.lang.String
	 */
	public void setHdef11 (java.lang.String newHdef11 ) {
	 	this.hdef11 = newHdef11;
	} 	 
	
	/**
	 * ���� hdef12��Getter����.���������Զ�����12
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef12 () {
		return hdef12;
	}   
	/**
	 * ����hdef12��Setter����.���������Զ�����12
	 * ��������:2021-9-22
	 * @param newHdef12 java.lang.String
	 */
	public void setHdef12 (java.lang.String newHdef12 ) {
	 	this.hdef12 = newHdef12;
	} 	 
	
	/**
	 * ���� hdef13��Getter����.���������Զ�����13
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef13 () {
		return hdef13;
	}   
	/**
	 * ����hdef13��Setter����.���������Զ�����13
	 * ��������:2021-9-22
	 * @param newHdef13 java.lang.String
	 */
	public void setHdef13 (java.lang.String newHdef13 ) {
	 	this.hdef13 = newHdef13;
	} 	 
	
	/**
	 * ���� hdef14��Getter����.���������Զ�����14
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef14 () {
		return hdef14;
	}   
	/**
	 * ����hdef14��Setter����.���������Զ�����14
	 * ��������:2021-9-22
	 * @param newHdef14 java.lang.String
	 */
	public void setHdef14 (java.lang.String newHdef14 ) {
	 	this.hdef14 = newHdef14;
	} 	 
	
	/**
	 * ���� hdef15��Getter����.���������Զ�����15
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef15 () {
		return hdef15;
	}   
	/**
	 * ����hdef15��Setter����.���������Զ�����15
	 * ��������:2021-9-22
	 * @param newHdef15 java.lang.String
	 */
	public void setHdef15 (java.lang.String newHdef15 ) {
	 	this.hdef15 = newHdef15;
	} 	 
	
	/**
	 * ���� hdef16��Getter����.���������Զ�����16
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef16 () {
		return hdef16;
	}   
	/**
	 * ����hdef16��Setter����.���������Զ�����16
	 * ��������:2021-9-22
	 * @param newHdef16 java.lang.String
	 */
	public void setHdef16 (java.lang.String newHdef16 ) {
	 	this.hdef16 = newHdef16;
	} 	 
	
	/**
	 * ���� hdef17��Getter����.���������Զ�����17
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef17 () {
		return hdef17;
	}   
	/**
	 * ����hdef17��Setter����.���������Զ�����17
	 * ��������:2021-9-22
	 * @param newHdef17 java.lang.String
	 */
	public void setHdef17 (java.lang.String newHdef17 ) {
	 	this.hdef17 = newHdef17;
	} 	 
	
	/**
	 * ���� hdef18��Getter����.���������Զ�����18
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef18 () {
		return hdef18;
	}   
	/**
	 * ����hdef18��Setter����.���������Զ�����18
	 * ��������:2021-9-22
	 * @param newHdef18 java.lang.String
	 */
	public void setHdef18 (java.lang.String newHdef18 ) {
	 	this.hdef18 = newHdef18;
	} 	 
	
	/**
	 * ���� hdef19��Getter����.���������Զ�����19
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef19 () {
		return hdef19;
	}   
	/**
	 * ����hdef19��Setter����.���������Զ�����19
	 * ��������:2021-9-22
	 * @param newHdef19 java.lang.String
	 */
	public void setHdef19 (java.lang.String newHdef19 ) {
	 	this.hdef19 = newHdef19;
	} 	 
	
	/**
	 * ���� hdef20��Getter����.���������Զ�����20
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getHdef20 () {
		return hdef20;
	}   
	/**
	 * ����hdef20��Setter����.���������Զ�����20
	 * ��������:2021-9-22
	 * @param newHdef20 java.lang.String
	 */
	public void setHdef20 (java.lang.String newHdef20 ) {
	 	this.hdef20 = newHdef20;
	} 	 
	
	/**
	 * ���� pk_supplier_name��Getter����.����������Ӧ������
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_supplier_name () {
		return pk_supplier_name;
	}   
	/**
	 * ����pk_supplier_name��Setter����.����������Ӧ������
	 * ��������:2021-9-22
	 * @param newPk_supplier_name java.lang.String
	 */
	public void setPk_supplier_name (java.lang.String newPk_supplier_name ) {
	 	this.pk_supplier_name = newPk_supplier_name;
	} 	 
	
	/**
	 * ���� pk_dkdept��Getter����.�����������ز���
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_dkdept () {
		return pk_dkdept;
	}   
	/**
	 * ����pk_dkdept��Setter����.�����������ز���
	 * ��������:2021-9-22
	 * @param newPk_dkdept java.lang.String
	 */
	public void setPk_dkdept (java.lang.String newPk_dkdept ) {
	 	this.pk_dkdept = newPk_dkdept;
	} 	 
	
	/**
	 * ���� pk_dkdept_v��Getter����.�����������ز��Ű汾��Ϣ
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_dkdept_v () {
		return pk_dkdept_v;
	}   
	/**
	 * ����pk_dkdept_v��Setter����.�����������ز��Ű汾��Ϣ
	 * ��������:2021-9-22
	 * @param newPk_dkdept_v java.lang.String
	 */
	public void setPk_dkdept_v (java.lang.String newPk_dkdept_v ) {
	 	this.pk_dkdept_v = newPk_dkdept_v;
	} 	 
	
	/**
	 * ���� pk_qydept��Getter����.��������ǩԼ����
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_qydept () {
		return pk_qydept;
	}   
	/**
	 * ����pk_qydept��Setter����.��������ǩԼ����
	 * ��������:2021-9-22
	 * @param newPk_qydept java.lang.String
	 */
	public void setPk_qydept (java.lang.String newPk_qydept ) {
	 	this.pk_qydept = newPk_qydept;
	} 	 
	
	/**
	 * ���� pk_qydept_v��Getter����.��������ǩԼ���Ű汾��Ϣ
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_qydept_v () {
		return pk_qydept_v;
	}   
	/**
	 * ����pk_qydept_v��Setter����.��������ǩԼ���Ű汾��Ϣ
	 * ��������:2021-9-22
	 * @param newPk_qydept_v java.lang.String
	 */
	public void setPk_qydept_v (java.lang.String newPk_qydept_v ) {
	 	this.pk_qydept_v = newPk_qydept_v;
	} 	 
	
	/**
	 * ���� contractmoney��Getter����.����������ͬ���
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getContractmoney () {
		return contractmoney;
	}   
	/**
	 * ����contractmoney��Setter����.����������ͬ���
	 * ��������:2021-9-22
	 * @param newContractmoney nc.vo.pub.lang.UFDouble
	 */
	public void setContractmoney (nc.vo.pub.lang.UFDouble newContractmoney ) {
	 	this.contractmoney = newContractmoney;
	} 	 
	
	/**
	 * ���� signdate��Getter����.��������ǩԼ����
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDate
	 */
	public nc.vo.pub.lang.UFDate getSigndate () {
		return signdate;
	}   
	/**
	 * ����signdate��Setter����.��������ǩԼ����
	 * ��������:2021-9-22
	 * @param newSigndate nc.vo.pub.lang.UFDate
	 */
	public void setSigndate (nc.vo.pub.lang.UFDate newSigndate ) {
	 	this.signdate = newSigndate;
	} 	 
	
	/**
	 * ���� contracttype��Getter����.����������ͬ����
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getContracttype () {
		return contracttype;
	}   
	/**
	 * ����contracttype��Setter����.����������ͬ����
	 * ��������:2021-9-22
	 * @param newContracttype java.lang.String
	 */
	public void setContracttype (java.lang.String newContracttype ) {
	 	this.contracttype = newContracttype;
	} 	 
	
	/**
	 * ���� pk_lead_name��Getter����.���������ֹ��쵼����
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_lead_name () {
		return pk_lead_name;
	}   
	/**
	 * ����pk_lead_name��Setter����.���������ֹ��쵼����
	 * ��������:2021-9-22
	 * @param newPk_lead_name java.lang.String
	 */
	public void setPk_lead_name (java.lang.String newPk_lead_name ) {
	 	this.pk_lead_name = newPk_lead_name;
	} 	 
	
	/**
	 * ���� pk_manager_name��Getter����.����������Ŀ��������
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_manager_name () {
		return pk_manager_name;
	}   
	/**
	 * ����pk_manager_name��Setter����.����������Ŀ��������
	 * ��������:2021-9-22
	 * @param newPk_manager_name java.lang.String
	 */
	public void setPk_manager_name (java.lang.String newPk_manager_name ) {
	 	this.pk_manager_name = newPk_manager_name;
	} 	 
	
	/**
	 * ���� zbmethod��Getter����.���������б귽ʽ
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getZbmethod () {
		return zbmethod;
	}   
	/**
	 * ����zbmethod��Setter����.���������б귽ʽ
	 * ��������:2021-9-22
	 * @param newZbmethod java.lang.String
	 */
	public void setZbmethod (java.lang.String newZbmethod ) {
	 	this.zbmethod = newZbmethod;
	} 	 
	
	/**
	 * ���� startdate��Getter����.����������ʼ����
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDate
	 */
	public nc.vo.pub.lang.UFDate getStartdate () {
		return startdate;
	}   
	/**
	 * ����startdate��Setter����.����������ʼ����
	 * ��������:2021-9-22
	 * @param newStartdate nc.vo.pub.lang.UFDate
	 */
	public void setStartdate (nc.vo.pub.lang.UFDate newStartdate ) {
	 	this.startdate = newStartdate;
	} 	 
	
	/**
	 * ���� enddate��Getter����.����������������
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDate
	 */
	public nc.vo.pub.lang.UFDate getEnddate () {
		return enddate;
	}   
	/**
	 * ����enddate��Setter����.����������������
	 * ��������:2021-9-22
	 * @param newEnddate nc.vo.pub.lang.UFDate
	 */
	public void setEnddate (nc.vo.pub.lang.UFDate newEnddate ) {
	 	this.enddate = newEnddate;
	} 	 
	
	/**
	 * ���� pk_signer��Getter����.��������ǩԼ������
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_signer () {
		return pk_signer;
	}   
	/**
	 * ����pk_signer��Setter����.��������ǩԼ������
	 * ��������:2021-9-22
	 * @param newPk_signer java.lang.String
	 */
	public void setPk_signer (java.lang.String newPk_signer ) {
	 	this.pk_signer = newPk_signer;
	} 	 
	
	/**
	 * ���� pk_signer_name��Getter����.��������ǩԼ������
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_signer_name () {
		return pk_signer_name;
	}   
	/**
	 * ����pk_signer_name��Setter����.��������ǩԼ������
	 * ��������:2021-9-22
	 * @param newPk_signer_name java.lang.String
	 */
	public void setPk_signer_name (java.lang.String newPk_signer_name ) {
	 	this.pk_signer_name = newPk_signer_name;
	} 	 
	
	/**
	 * ���� vmemo��Getter����.����������ע
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getVmemo () {
		return vmemo;
	}   
	/**
	 * ����vmemo��Setter����.����������ע
	 * ��������:2021-9-22
	 * @param newVmemo java.lang.String
	 */
	public void setVmemo (java.lang.String newVmemo ) {
	 	this.vmemo = newVmemo;
	} 	 
	
	/**
	 * ���� protocontractmoney��Getter����.��������ԭ��ͬ���
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getProtocontractmoney () {
		return protocontractmoney;
	}   
	/**
	 * ����protocontractmoney��Setter����.��������ԭ��ͬ���
	 * ��������:2021-9-22
	 * @param newProtocontractmoney nc.vo.pub.lang.UFDouble
	 */
	public void setProtocontractmoney (nc.vo.pub.lang.UFDouble newProtocontractmoney ) {
	 	this.protocontractmoney = newProtocontractmoney;
	} 	 
	
	/**
	 * ���� paymoney��Getter����.���������Ѹ�����
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getPaymoney () {
		return paymoney;
	}   
	/**
	 * ����paymoney��Setter����.���������Ѹ�����
	 * ��������:2021-9-22
	 * @param newPaymoney nc.vo.pub.lang.UFDouble
	 */
	public void setPaymoney (nc.vo.pub.lang.UFDouble newPaymoney ) {
	 	this.paymoney = newPaymoney;
	} 	 
	
	/**
	 * ���� addpaymoney��Getter����.���������ۼƸ�����
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getAddpaymoney () {
		return addpaymoney;
	}   
	/**
	 * ����addpaymoney��Setter����.���������ۼƸ�����
	 * ��������:2021-9-22
	 * @param newAddpaymoney nc.vo.pub.lang.UFDouble
	 */
	public void setAddpaymoney (nc.vo.pub.lang.UFDouble newAddpaymoney ) {
	 	this.addpaymoney = newAddpaymoney;
	} 	 
	
	/**
	 * ���� dbilldate��Getter����.����������������
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDate
	 */
	public nc.vo.pub.lang.UFDate getDbilldate () {
		return dbilldate;
	}   
	/**
	 * ����dbilldate��Setter����.����������������
	 * ��������:2021-9-22
	 * @param newDbilldate nc.vo.pub.lang.UFDate
	 */
	public void setDbilldate (nc.vo.pub.lang.UFDate newDbilldate ) {
	 	this.dbilldate = newDbilldate;
	} 	 
	
	/**
	 * ���� bill_name��Getter����.����������ͬ����
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getBill_name () {
		return bill_name;
	}   
	/**
	 * ����bill_name��Setter����.����������ͬ����
	 * ��������:2021-9-22
	 * @param newBill_name java.lang.String
	 */
	public void setBill_name (java.lang.String newBill_name ) {
	 	this.bill_name = newBill_name;
	} 	 
	
	/**
	 * ���� pk_contr��Getter����.����������ͬ����
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_contr () {
		return pk_contr;
	}   
	/**
	 * ����pk_contr��Setter����.����������ͬ����
	 * ��������:2021-9-22
	 * @param newPk_contr java.lang.String
	 */
	public void setPk_contr (java.lang.String newPk_contr ) {
	 	this.pk_contr = newPk_contr;
	} 	 
	
	/**
	 * ���� dr��Getter����.��������dr
	 *  ��������:2021-9-22
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getDr () {
		return dr;
	}   
	/**
	 * ����dr��Setter����.��������dr
	 * ��������:2021-9-22
	 * @param newDr java.lang.Integer
	 */
	public void setDr (java.lang.Integer newDr ) {
	 	this.dr = newDr;
	} 	 
	
	/**
	 * ���� ts��Getter����.��������ts
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getTs () {
		return ts;
	}   
	/**
	 * ����ts��Setter����.��������ts
	 * ��������:2021-9-22
	 * @param newTs nc.vo.pub.lang.UFDateTime
	 */
	public void setTs (nc.vo.pub.lang.UFDateTime newTs ) {
	 	this.ts = newTs;
	} 	 
	
	
	/**
	  * <p>ȡ�ø�VO�����ֶ�.
	  * <p>
	  * ��������:2021-9-22
	  * @return java.lang.String
	  */
	public java.lang.String getParentPKFieldName() {
	    return null;
	}   
    
	/**
	  * <p>ȡ�ñ�����.
	  * <p>
	  * ��������:2021-9-22
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
			
		return "pk_contr_alter";
	}
    
	/**
	 * <p>���ر�����
	 * <p>
	 * ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "pm_rlcontractalter_ct";
	}    
	
	/**
	 * <p>���ر�����.
	 * <p>
	 * ��������:2021-9-22
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "pm_rlcontractalter_ct";
	}    
    
    /**
	  * ����Ĭ�Ϸ�ʽ����������.
	  *
	  * ��������:2021-9-22
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