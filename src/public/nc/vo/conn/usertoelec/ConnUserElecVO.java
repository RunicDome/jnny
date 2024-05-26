package nc.vo.conn.usertoelec;

import nc.vo.pub.*;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> 此处简要描述此类功能 </b>
 * <p>
 *   此处添加类的描述信息
 * </p>
 *  创建日期:2022-7-14
 * @author yonyouBQ
 * @version NCPrj ??
 */
public class ConnUserElecVO extends nc.vo.pub.SuperVO{
	    
	
	

	
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2022-7-14
	  * @return java.lang.String
	  */
	public java.lang.String getParentPKFieldName() {
	    return null;
	}   
    
	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2022-7-14
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
			
		return "pk_usertoelec";
	}
    
	/**
	 * <p>返回表名称
	 * <p>
	 * 创建日期:2022-7-14
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "conn_user_elec";
	}    
	
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2022-7-14
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "conn_user_elec";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2022-7-14
	  */
     public ConnUserElecVO() {
		super();	
	}    
	
	
	@nc.vo.annotation.MDEntityInfo(beanFullclassName = "nc.vo.conn.usertoelec.ConnUserElecVO" )
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("uap.ConnUserElecVO");
		
   	}
     
}