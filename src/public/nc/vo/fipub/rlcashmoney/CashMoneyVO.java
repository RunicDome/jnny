package nc.vo.fipub.rlcashmoney;

import nc.vo.pub.*;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> �˴���Ҫ�������๦�� </b>
 * <p>
 *   �˴�������������Ϣ
 * </p>
 *  ��������:2022-7-2
 * @author yonyouBQ
 * @version NCPrj ??
 */
public class CashMoneyVO extends nc.vo.pub.SuperVO{
	    
	
	

	
	/**
	  * <p>ȡ�ø�VO�����ֶ�.
	  * <p>
	  * ��������:2022-7-2
	  * @return java.lang.String
	  */
	public java.lang.String getParentPKFieldName() {
	    return null;
	}   
    
	/**
	  * <p>ȡ�ñ�����.
	  * <p>
	  * ��������:2022-7-2
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
			
		return "pk_cashmoney";
	}
    
	/**
	 * <p>���ر�����
	 * <p>
	 * ��������:2022-7-2
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "rl_cashmoney";
	}    
	
	/**
	 * <p>���ر�����.
	 * <p>
	 * ��������:2022-7-2
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "rl_cashmoney";
	}    
    
    /**
	  * ����Ĭ�Ϸ�ʽ����������.
	  *
	  * ��������:2022-7-2
	  */
     public CashMoneyVO() {
		super();	
	}    
	
	
	@nc.vo.annotation.MDEntityInfo(beanFullclassName = "nc.vo.fipub.rlcashmoney.CashMoneyVO" )
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("fipub.CashMoneyVO");
		
   	}
     
}