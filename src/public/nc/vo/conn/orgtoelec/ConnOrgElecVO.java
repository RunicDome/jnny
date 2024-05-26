package nc.vo.conn.orgtoelec;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> 此处简要描述此类功能 </b>
 * <p>
 *   此处添加累的描述信息
 * </p>
 *  创建日期:2022-7-14
 * @author yonyouBQ
 * @version NCPrj ??
 */
 
public class ConnOrgElecVO extends SuperVO {
	
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2022-6-29
	  * @return java.lang.String
	  */
	public java.lang.String getParentPKFieldName() {
	    return null;
	}   
   
	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2022-6-29
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
			
		return "pk_orgtoelec";
	}
   
	/**
	 * <p>返回表名称
	 * <p>
	 * 创建日期:2022-6-29
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "conn_org_elec";
	}    
	
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2022-6-29
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "conn_org_elec";
	}    
   
   /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2022-6-29
	  */
    public ConnOrgElecVO() {
		super();	
	}    
	
	
	@nc.vo.annotation.MDEntityInfo(beanFullclassName = "nc.vo.conn.orgtoelec.ConnOrgElecVO" )
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("uap.ConnOrgElecVO");
		
  	}
	
   }
    