package nc.vo.fipub.rlcashmoney;

import nc.vo.pub.*;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> 此处简要描述此类功能 </b>
 * <p>
 *   此处添加类的描述信息
 * </p>
 *  创建日期:2022-7-2
 * @author yonyouBQ
 * @version NCPrj ??
 */
public class CashMoneyVO extends nc.vo.pub.SuperVO{
	    
	
	

	
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2022-7-2
	  * @return java.lang.String
	  */
	public java.lang.String getParentPKFieldName() {
	    return null;
	}   
    
	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2022-7-2
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
			
		return "pk_cashmoney";
	}
    
	/**
	 * <p>返回表名称
	 * <p>
	 * 创建日期:2022-7-2
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "rl_cashmoney";
	}    
	
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2022-7-2
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "rl_cashmoney";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2022-7-2
	  */
     public CashMoneyVO() {
		super();	
	}    
	
	
	@nc.vo.annotation.MDEntityInfo(beanFullclassName = "nc.vo.fipub.rlcashmoney.CashMoneyVO" )
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("fipub.CashMoneyVO");
		
   	}
     
}