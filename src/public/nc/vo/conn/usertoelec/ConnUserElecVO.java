package nc.vo.conn.usertoelec;

import nc.vo.pub.*;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> �˴���Ҫ�������๦�� </b>
 * <p>
 *   �˴�������������Ϣ
 * </p>
 *  ��������:2022-7-14
 * @author yonyouBQ
 * @version NCPrj ??
 */
public class ConnUserElecVO extends nc.vo.pub.SuperVO{
	    
	
	

	
	/**
	  * <p>ȡ�ø�VO�����ֶ�.
	  * <p>
	  * ��������:2022-7-14
	  * @return java.lang.String
	  */
	public java.lang.String getParentPKFieldName() {
	    return null;
	}   
    
	/**
	  * <p>ȡ�ñ�����.
	  * <p>
	  * ��������:2022-7-14
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
			
		return "pk_usertoelec";
	}
    
	/**
	 * <p>���ر�����
	 * <p>
	 * ��������:2022-7-14
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "conn_user_elec";
	}    
	
	/**
	 * <p>���ر�����.
	 * <p>
	 * ��������:2022-7-14
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "conn_user_elec";
	}    
    
    /**
	  * ����Ĭ�Ϸ�ʽ����������.
	  *
	  * ��������:2022-7-14
	  */
     public ConnUserElecVO() {
		super();	
	}    
	
	
	@nc.vo.annotation.MDEntityInfo(beanFullclassName = "nc.vo.conn.usertoelec.ConnUserElecVO" )
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("uap.ConnUserElecVO");
		
   	}
     
}