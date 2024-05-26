package nc.vo.conn.orgtoelec;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> �˴���Ҫ�������๦�� </b>
 * <p>
 *   �˴�����۵�������Ϣ
 * </p>
 *  ��������:2022-7-14
 * @author yonyouBQ
 * @version NCPrj ??
 */
 
public class ConnOrgElecVO extends SuperVO {
	
	/**
	  * <p>ȡ�ø�VO�����ֶ�.
	  * <p>
	  * ��������:2022-6-29
	  * @return java.lang.String
	  */
	public java.lang.String getParentPKFieldName() {
	    return null;
	}   
   
	/**
	  * <p>ȡ�ñ�����.
	  * <p>
	  * ��������:2022-6-29
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
			
		return "pk_orgtoelec";
	}
   
	/**
	 * <p>���ر�����
	 * <p>
	 * ��������:2022-6-29
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "conn_org_elec";
	}    
	
	/**
	 * <p>���ر�����.
	 * <p>
	 * ��������:2022-6-29
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "conn_org_elec";
	}    
   
   /**
	  * ����Ĭ�Ϸ�ʽ����������.
	  *
	  * ��������:2022-6-29
	  */
    public ConnOrgElecVO() {
		super();	
	}    
	
	
	@nc.vo.annotation.MDEntityInfo(beanFullclassName = "nc.vo.conn.orgtoelec.ConnOrgElecVO" )
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("uap.ConnOrgElecVO");
		
  	}
	
   }
    