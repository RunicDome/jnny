/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
\***************************************************************/
package nc.vo.pu.m20trantype.entity;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 * 在此处添加此类的描述信息
 * </p>
 * 创建日期:
 * 
 * @author
 * @version NCPrj ??
 */
@SuppressWarnings("serial")
public class BuyrTranTypeVO extends SuperVO {

  /**
   * 是否控制合同数量，2017
   */
  public static final String ISCONTROCONTRACT = "iscontrocontract";
  
  /**
   * 需要请购安排
   */
  public static final String BNEEDARRANGE = "bneedarrange";

  /**
   * 交易类型
   */
  public static final String CTRANTYPEID = "ctrantypeid";

  /**
   * 请购单交易类型
   */
  public static final String PK_BUYRTRANTYPE = "pk_buyrtrantype";

  /**
   * 所属集团
   */
  public static final String PK_GROUP = "pk_group";

  /**
   * 时间戳
   */
  public static final String TS = "ts";

  /**
   * 交易类型编码
   */
  public static final String VTRANTYPECODE = "vtrantypecode";

  /**
   * 获取需要请购安排
   * 
   * @return 需要请购安排
   */
  public UFBoolean getBneedprayarrange() {
    return (UFBoolean) this.getAttributeValue(BuyrTranTypeVO.BNEEDARRANGE);
  }
  
  /**
   * 获取是否控制合同数量,2017
   * 
   * @return 是否控制合同数量
   */
  public UFBoolean getIscontrocontract() {
    return (UFBoolean) this.getAttributeValue(BuyrTranTypeVO.ISCONTROCONTRACT);
  }

  /**
   * 获取交易类型
   * 
   * @return 交易类型
   */
  public String getCtrantypeid() {
    return (String) this.getAttributeValue(BuyrTranTypeVO.CTRANTYPEID);
  }

  @Override
  public IVOMeta getMetaData() {
    IVOMeta meta = VOMetaFactory.getInstance().getVOMeta("pu.po_buyrtrantype");
    return meta;
  }

  /**
   * 获取请购单交易类型
   * 
   * @return 请购单交易类型
   */
  public String getPk_buyrtrantype() {
    return (String) this.getAttributeValue(BuyrTranTypeVO.PK_BUYRTRANTYPE);
  }

  /**
   * 获取所属集团
   * 
   * @return 所属集团
   */
  public String getPk_group() {
    return (String) this.getAttributeValue(BuyrTranTypeVO.PK_GROUP);
  }

  /**
   * 获取时间戳
   * 
   * @return 时间戳
   */
  public UFDateTime getTs() {
    return (UFDateTime) this.getAttributeValue(BuyrTranTypeVO.TS);
  }

  /**
   * 获取交易类型编码
   * 
   * @return 交易类型编码
   */
  public String getVtrantypecode() {
    return (String) this.getAttributeValue(BuyrTranTypeVO.VTRANTYPECODE);
  }

  /**
   * 设置需要请购安排
   * 
   * @param bneedprayarrange 需要请购安排
   */
  public void setBneedprayarrange(UFBoolean bneedarrange) {
    this.setAttributeValue(BuyrTranTypeVO.BNEEDARRANGE, bneedarrange);
  }
  
  /**
   * 设置是否控制合同数量，2017 
   * 
   * @param iscontrocontract 是否控制合同数量
   */
  public void setIscontrocontract(UFBoolean iscontrocontract) {
    this.setAttributeValue(BuyrTranTypeVO.ISCONTROCONTRACT, iscontrocontract);
  }

  /**
   * 设置交易类型
   * 
   * @param ctrantypeid 交易类型
   */
  public void setCtrantypeid(String ctrantypeid) {
    this.setAttributeValue(BuyrTranTypeVO.CTRANTYPEID, ctrantypeid);
  }

  /**
   * 设置请购单交易类型
   * 
   * @param pk_buyrtrantype 请购单交易类型
   */
  public void setPk_buyrtrantype(String pk_buyrtrantype) {
    this.setAttributeValue(BuyrTranTypeVO.PK_BUYRTRANTYPE, pk_buyrtrantype);
  }

  /**
   * 设置所属集团
   * 
   * @param pk_group 所属集团
   */
  public void setPk_group(String pk_group) {
    this.setAttributeValue(BuyrTranTypeVO.PK_GROUP, pk_group);
  }

  /**
   * 设置时间戳
   * 
   * @param ts 时间戳
   */
  public void setTs(UFDateTime ts) {
    this.setAttributeValue(BuyrTranTypeVO.TS, ts);
  }

  /**
   * 设置交易类型编码
   * 
   * @param vtrantypecode 交易类型编码
   */
  public void setVtrantypecode(String vtrantypecode) {
    this.setAttributeValue(BuyrTranTypeVO.VTRANTYPECODE, vtrantypecode);
  }

}
