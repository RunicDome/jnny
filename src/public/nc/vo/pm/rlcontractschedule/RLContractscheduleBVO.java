package nc.vo.pm.rlcontractschedule;
//�������ȿ����
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
	 * ���� pk_rl_contr_sche��Getter����.��������parentPK
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_rl_contr_sche () {
		return pk_rl_contr_sche;
	}   
	/**
	 * ����pk_rl_contr_sche��Setter����.��������parentPK
	 * ��������:2021-9-22
	 * @param newPk_rl_contr_sche java.lang.String
	 */
	public void setPk_rl_contr_sche (java.lang.String newPk_rl_contr_sche ) {
	 	this.pk_rl_contr_sche = newPk_rl_contr_sche;
	} 	 
	
	/**
	 * ���� pk_rl_contr_sche_b��Getter����.���������ְ���ͬ���ȿ��ӱ�����
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_rl_contr_sche_b () {
		return pk_rl_contr_sche_b;
	}   
	/**
	 * ����pk_rl_contr_sche_b��Setter����.���������ְ���ͬ���ȿ��ӱ�����
	 * ��������:2021-9-22
	 * @param newPk_rl_contr_sche_b java.lang.String
	 */
	public void setPk_rl_contr_sche_b (java.lang.String newPk_rl_contr_sche_b ) {
	 	this.pk_rl_contr_sche_b = newPk_rl_contr_sche_b;
	} 	 
	
	/**
	 * ���� pk_org��Getter����.����������Ŀ��֯
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_org () {
		return pk_org;
	}   
	/**
	 * ����pk_org��Setter����.����������Ŀ��֯
	 * ��������:2021-9-22
	 * @param newPk_org java.lang.String
	 */
	public void setPk_org (java.lang.String newPk_org ) {
	 	this.pk_org = newPk_org;
	} 	 
	
	/**
	 * ���� pk_org_v��Getter����.����������Ŀ��֯�汾
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_org_v () {
		return pk_org_v;
	}   
	/**
	 * ����pk_org_v��Setter����.����������Ŀ��֯�汾
	 * ��������:2021-9-22
	 * @param newPk_org_v java.lang.String
	 */
	public void setPk_org_v (java.lang.String newPk_org_v ) {
	 	this.pk_org_v = newPk_org_v;
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
	 * ���� pk_material��Getter����.������������
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_material () {
		return pk_material;
	}   
	/**
	 * ����pk_material��Setter����.������������
	 * ��������:2021-9-22
	 * @param newPk_material java.lang.String
	 */
	public void setPk_material (java.lang.String newPk_material ) {
	 	this.pk_material = newPk_material;
	} 	 
	
	/**
	 * ���� pk_material_v��Getter����.������������
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_material_v () {
		return pk_material_v;
	}   
	/**
	 * ����pk_material_v��Setter����.������������
	 * ��������:2021-9-22
	 * @param newPk_material_v java.lang.String
	 */
	public void setPk_material_v (java.lang.String newPk_material_v ) {
	 	this.pk_material_v = newPk_material_v;
	} 	 
	
	/**
	 * ���� pk_cbsnode��Getter����.��������CBS
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_cbsnode () {
		return pk_cbsnode;
	}   
	/**
	 * ����pk_cbsnode��Setter����.��������CBS
	 * ��������:2021-9-22
	 * @param newPk_cbsnode java.lang.String
	 */
	public void setPk_cbsnode (java.lang.String newPk_cbsnode ) {
	 	this.pk_cbsnode = newPk_cbsnode;
	} 	 
	
	/**
	 * ���� pk_measdoc��Getter����.����������λ
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getPk_measdoc () {
		return pk_measdoc;
	}   
	/**
	 * ����pk_measdoc��Setter����.����������λ
	 * ��������:2021-9-22
	 * @param newPk_measdoc java.lang.String
	 */
	public void setPk_measdoc (java.lang.String newPk_measdoc ) {
	 	this.pk_measdoc = newPk_measdoc;
	} 	 
	
	/**
	 * ���� memo��Getter����.����������ע
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getMemo () {
		return memo;
	}   
	/**
	 * ����memo��Setter����.����������ע
	 * ��������:2021-9-22
	 * @param newMemo java.lang.String
	 */
	public void setMemo (java.lang.String newMemo ) {
	 	this.memo = newMemo;
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
	 * ���� rowno��Getter����.���������к�
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getRowno () {
		return rowno;
	}   
	/**
	 * ����rowno��Setter����.���������к�
	 * ��������:2021-9-22
	 * @param newRowno java.lang.String
	 */
	public void setRowno (java.lang.String newRowno ) {
	 	this.rowno = newRowno;
	} 	 
	
	/**
	 * ���� tsourcebodyts��Getter����.����������Դ����ʱ���
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getTsourcebodyts () {
		return tsourcebodyts;
	}   
	/**
	 * ����tsourcebodyts��Setter����.����������Դ����ʱ���
	 * ��������:2021-9-22
	 * @param newTsourcebodyts java.lang.String
	 */
	public void setTsourcebodyts (java.lang.String newTsourcebodyts ) {
	 	this.tsourcebodyts = newTsourcebodyts;
	} 	 
	
	/**
	 * ���� tsourceheadts��Getter����.����������Դ��ͷʱ���
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getTsourceheadts () {
		return tsourceheadts;
	}   
	/**
	 * ����tsourceheadts��Setter����.����������Դ��ͷʱ���
	 * ��������:2021-9-22
	 * @param newTsourceheadts java.lang.String
	 */
	public void setTsourceheadts (java.lang.String newTsourceheadts ) {
	 	this.tsourceheadts = newTsourceheadts;
	} 	 
	
	/**
	 * ���� csourcebillbid��Getter����.����������Դ���ݱ�������
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getCsourcebillbid () {
		return csourcebillbid;
	}   
	/**
	 * ����csourcebillbid��Setter����.����������Դ���ݱ�������
	 * ��������:2021-9-22
	 * @param newCsourcebillbid java.lang.String
	 */
	public void setCsourcebillbid (java.lang.String newCsourcebillbid ) {
	 	this.csourcebillbid = newCsourcebillbid;
	} 	 
	
	/**
	 * ���� csourcebilldate��Getter����.����������Դ��������
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDate
	 */
	public nc.vo.pub.lang.UFDate getCsourcebilldate () {
		return csourcebilldate;
	}   
	/**
	 * ����csourcebilldate��Setter����.����������Դ��������
	 * ��������:2021-9-22
	 * @param newCsourcebilldate nc.vo.pub.lang.UFDate
	 */
	public void setCsourcebilldate (nc.vo.pub.lang.UFDate newCsourcebilldate ) {
	 	this.csourcebilldate = newCsourcebilldate;
	} 	 
	
	/**
	 * ���� csourcebillhid��Getter����.����������Դ���ݱ�ͷ����
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getCsourcebillhid () {
		return csourcebillhid;
	}   
	/**
	 * ����csourcebillhid��Setter����.����������Դ���ݱ�ͷ����
	 * ��������:2021-9-22
	 * @param newCsourcebillhid java.lang.String
	 */
	public void setCsourcebillhid (java.lang.String newCsourcebillhid ) {
	 	this.csourcebillhid = newCsourcebillhid;
	} 	 
	
	/**
	 * ���� csourcetype��Getter����.����������Դ��������
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getCsourcetype () {
		return csourcetype;
	}   
	/**
	 * ����csourcetype��Setter����.����������Դ��������
	 * ��������:2021-9-22
	 * @param newCsourcetype java.lang.String
	 */
	public void setCsourcetype (java.lang.String newCsourcetype ) {
	 	this.csourcetype = newCsourcetype;
	} 	 
	
	/**
	 * ���� cfirstbillbid��Getter����.��������Դͷ���ݱ�������
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getCfirstbillbid () {
		return cfirstbillbid;
	}   
	/**
	 * ����cfirstbillbid��Setter����.��������Դͷ���ݱ�������
	 * ��������:2021-9-22
	 * @param newCfirstbillbid java.lang.String
	 */
	public void setCfirstbillbid (java.lang.String newCfirstbillbid ) {
	 	this.cfirstbillbid = newCfirstbillbid;
	} 	 
	
	/**
	 * ���� cfirstbilldate��Getter����.��������Դͷ��������
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDate
	 */
	public nc.vo.pub.lang.UFDate getCfirstbilldate () {
		return cfirstbilldate;
	}   
	/**
	 * ����cfirstbilldate��Setter����.��������Դͷ��������
	 * ��������:2021-9-22
	 * @param newCfirstbilldate nc.vo.pub.lang.UFDate
	 */
	public void setCfirstbilldate (nc.vo.pub.lang.UFDate newCfirstbilldate ) {
	 	this.cfirstbilldate = newCfirstbilldate;
	} 	 
	
	/**
	 * ���� cfirstbillhid��Getter����.��������Դͷ���ݱ�ͷ����
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getCfirstbillhid () {
		return cfirstbillhid;
	}   
	/**
	 * ����cfirstbillhid��Setter����.��������Դͷ���ݱ�ͷ����
	 * ��������:2021-9-22
	 * @param newCfirstbillhid java.lang.String
	 */
	public void setCfirstbillhid (java.lang.String newCfirstbillhid ) {
	 	this.cfirstbillhid = newCfirstbillhid;
	} 	 
	
	/**
	 * ���� cfirsttranstype��Getter����.��������Դͷ��������
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getCfirsttranstype () {
		return cfirsttranstype;
	}   
	/**
	 * ����cfirsttranstype��Setter����.��������Դͷ��������
	 * ��������:2021-9-22
	 * @param newCfirsttranstype java.lang.String
	 */
	public void setCfirsttranstype (java.lang.String newCfirsttranstype ) {
	 	this.cfirsttranstype = newCfirsttranstype;
	} 	 
	
	/**
	 * ���� cfirsttype��Getter����.��������Դͷ��������
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getCfirsttype () {
		return cfirsttype;
	}   
	/**
	 * ����cfirsttype��Setter����.��������Դͷ��������
	 * ��������:2021-9-22
	 * @param newCfirsttype java.lang.String
	 */
	public void setCfirsttype (java.lang.String newCfirsttype ) {
	 	this.cfirsttype = newCfirsttype;
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
	 * ���� examinationvalue��Getter����.����������ֵ
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getExaminationvalue () {
		return examinationvalue;
	}   
	/**
	 * ����examinationvalue��Setter����.����������ֵ
	 * ��������:2021-9-22
	 * @param newExaminationvalue nc.vo.pub.lang.UFDouble
	 */
	public void setExaminationvalue (nc.vo.pub.lang.UFDouble newExaminationvalue ) {
	 	this.examinationvalue = newExaminationvalue;
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
	 * ���� clearingstate��Getter����.������������״̬
	 *  ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getClearingstate () {
		return clearingstate;
	}   
	/**
	 * ����clearingstate��Setter����.������������״̬
	 * ��������:2021-9-22
	 * @param newClearingstate java.lang.String
	 */
	public void setClearingstate (java.lang.String newClearingstate ) {
	 	this.clearingstate = newClearingstate;
	} 	 
	
	/**
	 * ���� contractalter_mny��Getter����.���������ۼƽ���
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getContractalter_mny () {
		return contractalter_mny;
	}   
	/**
	 * ����contractalter_mny��Setter����.���������ۼƽ���
	 * ��������:2021-9-22
	 * @param newContractalter_mny nc.vo.pub.lang.UFDouble
	 */
	public void setContractalter_mny (nc.vo.pub.lang.UFDouble newContractalter_mny ) {
	 	this.contractalter_mny = newContractalter_mny;
	} 	 
	
	/**
	 * ���� contractbalance_mny��Getter����.���������ۼƽ���
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getContractbalance_mny () {
		return contractbalance_mny;
	}   
	/**
	 * ����contractbalance_mny��Setter����.���������ۼƽ���
	 * ��������:2021-9-22
	 * @param newContractbalance_mny nc.vo.pub.lang.UFDouble
	 */
	public void setContractbalance_mny (nc.vo.pub.lang.UFDouble newContractbalance_mny ) {
	 	this.contractbalance_mny = newContractbalance_mny;
	} 	 
	
	/**
	 * ���� notaxprice��Getter����.����������˰����
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getNotaxprice () {
		return notaxprice;
	}   
	/**
	 * ����notaxprice��Setter����.����������˰����
	 * ��������:2021-9-22
	 * @param newNotaxprice nc.vo.pub.lang.UFDouble
	 */
	public void setNotaxprice (nc.vo.pub.lang.UFDouble newNotaxprice ) {
	 	this.notaxprice = newNotaxprice;
	} 	 
	
	/**
	 * ���� notaxmoney��Getter����.����������˰���
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getNotaxmoney () {
		return notaxmoney;
	}   
	/**
	 * ����notaxmoney��Setter����.����������˰���
	 * ��������:2021-9-22
	 * @param newNotaxmoney nc.vo.pub.lang.UFDouble
	 */
	public void setNotaxmoney (nc.vo.pub.lang.UFDouble newNotaxmoney ) {
	 	this.notaxmoney = newNotaxmoney;
	} 	 
	
	/**
	 * ���� taxprice��Getter����.����������˰����
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getTaxprice () {
		return taxprice;
	}   
	/**
	 * ����taxprice��Setter����.����������˰����
	 * ��������:2021-9-22
	 * @param newTaxprice nc.vo.pub.lang.UFDouble
	 */
	public void setTaxprice (nc.vo.pub.lang.UFDouble newTaxprice ) {
	 	this.taxprice = newTaxprice;
	} 	 
	
	/**
	 * ���� currentpaymentamount��Getter����.�����������θ�����
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getCurrentpaymentamount () {
		return currentpaymentamount;
	}   
	/**
	 * ����currentpaymentamount��Setter����.�����������θ�����
	 * ��������:2021-9-22
	 * @param newCurrentpaymentamount nc.vo.pub.lang.UFDouble
	 */
	public void setCurrentpaymentamount (nc.vo.pub.lang.UFDouble newCurrentpaymentamount ) {
	 	this.currentpaymentamount = newCurrentpaymentamount;
	} 	 
	
	/**
	 * ���� taxrate��Getter����.��������˰��
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getTaxrate () {
		return taxrate;
	}   
	/**
	 * ����taxrate��Setter����.��������˰��
	 * ��������:2021-9-22
	 * @param newTaxrate nc.vo.pub.lang.UFDouble
	 */
	public void setTaxrate (nc.vo.pub.lang.UFDouble newTaxrate ) {
	 	this.taxrate = newTaxrate;
	} 	 
	
	/**
	 * ���� taxamount��Getter����.��������˰��
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getTaxamount () {
		return taxamount;
	}   
	/**
	 * ����taxamount��Setter����.��������˰��
	 * ��������:2021-9-22
	 * @param newTaxamount nc.vo.pub.lang.UFDouble
	 */
	public void setTaxamount (nc.vo.pub.lang.UFDouble newTaxamount ) {
	 	this.taxamount = newTaxamount;
	} 	 
	
	/**
	 * ���� currentpaymentamounta��Getter����.�����������θ�����(�ϼƽ��)
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getCurrentpaymentamounta () {
		return currentpaymentamounta;
	}   
	/**
	 * ����currentpaymentamounta��Setter����.�����������θ�����(�ϼƽ��)
	 * ��������:2021-9-22
	 * @param newCurrentpaymentamounta nc.vo.pub.lang.UFDouble
	 */
	public void setCurrentpaymentamounta (nc.vo.pub.lang.UFDouble newCurrentpaymentamounta ) {
	 	this.currentpaymentamounta = newCurrentpaymentamounta;
	} 	 
	
	/**
	 * ���� money��Getter����.�����������
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getMoney () {
		return money;
	}   
	/**
	 * ����money��Setter����.�����������
	 * ��������:2021-9-22
	 * @param newMoney nc.vo.pub.lang.UFDouble
	 */
	public void setMoney (nc.vo.pub.lang.UFDouble newMoney ) {
	 	this.money = newMoney;
	} 	 
	
	/**
	 * ���� unitmoney��Getter����.������������
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getUnitmoney () {
		return unitmoney;
	}   
	/**
	 * ����unitmoney��Setter����.������������
	 * ��������:2021-9-22
	 * @param newUnitmoney nc.vo.pub.lang.UFDouble
	 */
	public void setUnitmoney (nc.vo.pub.lang.UFDouble newUnitmoney ) {
	 	this.unitmoney = newUnitmoney;
	} 	 
	
	/**
	 * ���� num��Getter����.������������
	 *  ��������:2021-9-22
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getNum () {
		return num;
	}   
	/**
	 * ����num��Setter����.������������
	 * ��������:2021-9-22
	 * @param newNum nc.vo.pub.lang.UFDouble
	 */
	public void setNum (nc.vo.pub.lang.UFDouble newNum ) {
	 	this.num = newNum;
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
		return "pk_rl_contr_sche";
	}   
    
	/**
	  * <p>ȡ�ñ�����.
	  * <p>
	  * ��������:2021-9-22
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
			
		return "pk_rl_contr_sche_b";
	}
    
	/**
	 * <p>���ر�����
	 * <p>
	 * ��������:2021-9-22
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "pm_rlcontractschedule_ct_b";
	}    
	
	/**
	 * <p>���ر�����.
	 * <p>
	 * ��������:2021-9-22
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "pm_rlcontractschedule_ct_b";
	}    
    
    /**
	  * ����Ĭ�Ϸ�ʽ����������.
	  *
	  * ��������:2021-9-22
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