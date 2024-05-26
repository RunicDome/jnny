/**
 * $文件说明$
 * 
 * @author linsf
 * @version 6.0
 * @see
 * @since 6.0
 * @time 2010-1-26 下午06:38:43
 */
package nc.vo.pu.m20.entity;

import nc.vo.pu.m21.entity.OrderItemVO;
import nc.vo.pu.pub.constant.PUEntity;
import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <p>
 * <b>请购单表体VO</b>
 * <ul>
 * <li>
 * </ul>
 * <p>
 * <p>
 * 
 * @version 6.0
 * @since 6.0
 * @author linsf
 * @time 2010-1-26 下午06:38:43
 */
public class PraybillItemVO extends SuperVO {
	
  /** 累计合同主数量  2017**/
  public static final String TOTALCONTRACTNUM = "totalcontractnum";
	
  /** 采购组织可编辑 **/
  public static final String BCANPURCHASEORGEDIT = "bcanpurchaseorgedit";

  /** 是否固定换算率 **/
  public static final String BFIXEDRATE = "bfixedrate";

  /** 已安排 **/
  public static final String BISARRANGE = "bisarrange";

  /** 已生成总括订单 */
  public static final String BISGENSAORDER = "bisgensaorder";

  /** 发布到电子商务 **/
  public static final String BPUBLISHTOEC = "bpublishtoec";

  /** 行关闭 **/
  public static final String BROWCLOSE = "browclose";

  /** 客户 */
  public static final String CASSCUSTID = "casscustid";

  /** 单位 **/
  public static final String CASTUNITID = "castunitid";

  /** 特征码 **/
  public static final String CFFILEID = "cffileid";

  /** 源头单据分录标识 **/
  public static final String CFIRSTBID = "cfirstbid";

  /** 源头单据标识 **/
  public static final String CFIRSTID = "cfirstid";

  /** 源头单据类型 **/
  public static final String CFIRSTTYPECODE = "cfirsttypecode";

  /** 订单类型 **/
  public static final String CORDERTRANTYPECODE = "cordertrantypecode";

  /** 生产厂商 **/
  public static final String CPRODUCTORID = "cproductorid";

  /** 项目 **/
  public static final String CPROJECTID = "cprojectid";

  /** 项目任务 */
  public static final String CPROJECTTASKID = "cprojecttaskid";

  /** 行号 **/
  public static final String CROWNO = "crowno";

  /** 来源单据分录标识 **/
  public static final String CSOURCEBID = "csourcebid";

  /** 来源单据标识 **/
  public static final String CSOURCEID = "csourceid";

  /** 来源单据类型 **/
  public static final String CSOURCETYPECODE = "csourcetypecode";

  /** 主计量单位 **/
  public static final String CUNITID = "cunitid";

  /** 请购日期 **/
  public static final String DBILLDATE = "dbilldate";

  /** 需求日期 **/
  public static final String DREQDATE = "dreqdate";

  /** 建议订货日期 **/
  public static final String DSUGGESTDATE = "dsuggestdate";

  /** 累计订货数量 **/
  public static final String NACCUMULATENUM = "naccumulatenum";

  /** 数量 **/
  public static final String NASTNUM = "nastnum";

  /** 生成合同次数 **/
  public static final String NGENCT = "ngenct";

  /** 主数量 **/
  public static final String NNUM = "nnum";

  /** 生成价格审批单次数 **/
  public static final String NPRICEAUDITBILL = "npriceauditbill";

  /** 生成询报价单次数 **/
  public static final String NQUOTEBILL = "nquotebill";

  /** 本币价税合计 **/
  public static final String NTAXMNY = "ntaxmny";

  /** 主本币含税单价 **/
  public static final String NTAXPRICE = "ntaxprice";

  /** 批次档案 **/
  public static final String PK_BATCHCODE = "pk_batchcode";

  /** 采购员 **/
  public static final String PK_EMPLOYEE = "pk_employee";

  /** 所属集团 **/
  public static final String PK_GROUP = "pk_group";

  /** 物料(VID) **/
  public static final String PK_MATERIAL = "pk_material";

  /** 库存组织 **/
  public static final String PK_ORG = "pk_org";

  /** 库存组织版本 **/
  public static final String PK_ORG_V = "pk_org_v";

  /** 请购单主表_主键 **/
  public static final String PK_PRAYBILL = "pk_praybill";

  /** 请购单行ID **/
  public static final String PK_PRAYBILL_B = "pk_praybill_b";

  /** 产品 **/
  public static final String PK_PRODUCT = "pk_product";

  /** 产品版本 **/
  public static final String PK_PRODUCT_V = "pk_product_v";

  /** 采购组织 **/
  public static final String PK_PURCHASEORG = "pk_purchaseorg";

  /** 采购组织版本 **/
  public static final String PK_PURCHASEORG_V = "pk_purchaseorg_v";

  /** 需求部门 **/
  public static final String PK_REQDEPT = "pk_reqdept";

  /** 需求部门版本 **/
  public static final String PK_REQDEPT_V = "pk_reqdept_v";

  /** 原始需求库存组织最新版本 */
  public static final String PK_REQSTOORG = "pk_reqstoorg";

  /** 原始需求库存组织 */
  public static final String PK_REQSTOORG_V = "pk_reqstoorg_v";

  /** 需求仓库 **/
  public static final String PK_REQSTOR = "pk_reqstor";

  /** 物料(OID) **/
  public static final String PK_SRCMATERIAL = "pk_srcmaterial";

  /** 建议供应商 **/
  public static final String PK_SUGGESTSUPPLIER = "pk_suggestsupplier";

  /** 来源单据行TS */
  public static final String SOURCEBTS = "sourcebts";

  /** 来源单据TS */
  public static final String SOURCETS = "sourcets";

  /** 时间戳 **/
  public static final String TS = "ts";

  /** 批次号 **/
  public static final String VBATCHCODE = "vbatchcode";

  /** 自定义项1 **/
  public static final String VBDEF1 = "vbdef1";

  /** 自定义项10 **/
  public static final String VBDEF10 = "vbdef10";

  /** 自定义项11 **/
  public static final String VBDEF11 = "vbdef11";

  /** 自定义项12 **/
  public static final String VBDEF12 = "vbdef12";

  /** 自定义项13 **/
  public static final String VBDEF13 = "vbdef13";

  /** 自定义项14 **/
  public static final String VBDEF14 = "vbdef14";

  /** 自定义项15 **/
  public static final String VBDEF15 = "vbdef15";

  /** 自定义项16 **/
  public static final String VBDEF16 = "vbdef16";

  /** 自定义项17 **/
  public static final String VBDEF17 = "vbdef17";

  /** 自定义项18 **/
  public static final String VBDEF18 = "vbdef18";

  /** 自定义项19 **/
  public static final String VBDEF19 = "vbdef19";

  /** 自定义项2 **/
  public static final String VBDEF2 = "vbdef2";

  /** 自定义项20 **/
  public static final String VBDEF20 = "vbdef20";

  /** 自定义项3 **/
  public static final String VBDEF3 = "vbdef3";

  /** 自定义项4 **/
  public static final String VBDEF4 = "vbdef4";

  /** 自定义项5 **/
  public static final String VBDEF5 = "vbdef5";

  /** 自定义项6 **/
  public static final String VBDEF6 = "vbdef6";

  /** 自定义项7 **/
  public static final String VBDEF7 = "vbdef7";

  /** 自定义项8 **/
  public static final String VBDEF8 = "vbdef8";

  /** 自定义项9 **/
  public static final String VBDEF9 = "vbdef9";

  /** 备注 **/
  public static final String VBMEMO = "vbmemo";

  /** 换算率 **/
  public static final String VCHANGERATE = "vchangerate";

  /** 源头单据号 **/
  public static final String VFIRSTCODE = "vfirstcode";

  /** 源头单据行号 **/
  public static final String VFIRSTROWNO = "vfirstrowno";

  /** 源头交易类型 **/
  public static final String VFIRSTTRANTYPE = "vfirsttrantype";

  /** 自由辅助属性1 **/
  public static final String VFREE1 = "vfree1";

  /** 自由辅助属性10 **/
  public static final String VFREE10 = "vfree10";

  /** 自由辅助属性2 **/
  public static final String VFREE2 = "vfree2";

  /** 自由辅助属性3 **/
  public static final String VFREE3 = "vfree3";

  /** 自由辅助属性4 **/
  public static final String VFREE4 = "vfree4";

  /** 自由辅助属性5 **/
  public static final String VFREE5 = "vfree5";

  /** 自由辅助属性6 **/
  public static final String VFREE6 = "vfree6";

  /** 自由辅助属性7 **/
  public static final String VFREE7 = "vfree7";

  /** 自由辅助属性8 **/
  public static final String VFREE8 = "vfree8";

  /** 自由辅助属性9 **/
  public static final String VFREE9 = "vfree9";

  /** 来源单据号 **/
  public static final String VSOURCECODE = "vsourcecode";

  /** 来源单据行号 **/
  public static final String VSOURCEROWNO = "vsourcerowno";

  /** 来源交易类型 **/
  public static final String VSRCTRANTYPECODE = "vsrctrantypecode";

  private static final long serialVersionUID = -3432997440833423530L;

  /**
   * 获取累计合同主数量,2017
   */
  public UFDouble getTotalcontractnum() {
    return (UFDouble) this
        .getAttributeValue(PraybillItemVO.TOTALCONTRACTNUM);
  }
  
  /** 采购组织可编辑 **/
  public UFBoolean getBcanpurchaseorgedit() {
    return (UFBoolean) this
        .getAttributeValue(PraybillItemVO.BCANPURCHASEORGEDIT);
  }

  /** 是否固定换算率 **/
  public UFBoolean getBfixedrate() {
    return (UFBoolean) this.getAttributeValue(PraybillItemVO.BFIXEDRATE);
  }

  /** 已安排 **/
  public UFBoolean getBisarrange() {
    return (UFBoolean) this.getAttributeValue(PraybillItemVO.BISARRANGE);
  }

  /** 已生成总括订单 **/
  public UFBoolean getBisgensaorder() {
    return (UFBoolean) this.getAttributeValue(PraybillItemVO.BISGENSAORDER);
  }

  /** 发布到电子商务 **/
  public UFBoolean getBpublishtoec() {
    return (UFBoolean) this.getAttributeValue(PraybillItemVO.BPUBLISHTOEC);
  }

  /** 行关闭 **/
  public UFBoolean getBrowclose() {
    return (UFBoolean) this.getAttributeValue(PraybillItemVO.BROWCLOSE);
  }

  /** 客户 getter 方法 */
  public String getCasscustid() {
    return (String) this.getAttributeValue(PraybillItemVO.CASSCUSTID);
  }

  /** 单位 **/
  public String getCastunitid() {
    return (String) this.getAttributeValue(PraybillItemVO.CASTUNITID);
  }

  /** 特征码 **/
  public String getCffileid() {
    return (String) this.getAttributeValue(PraybillItemVO.CFFILEID);
  }

  /** 源头单据分录标识 **/
  public String getCfirstbid() {
    return (String) this.getAttributeValue(PraybillItemVO.CFIRSTBID);
  }

  /** 源头单据标识 **/
  public String getCfirstid() {
    return (String) this.getAttributeValue(PraybillItemVO.CFIRSTID);
  }

  /** 源头单据类型 **/
  public String getCfirsttypecode() {
    return (String) this.getAttributeValue(PraybillItemVO.CFIRSTTYPECODE);
  }

  /** 订单类型 **/
  public String getCordertrantypecode() {
    return (String) this.getAttributeValue(PraybillItemVO.CORDERTRANTYPECODE);
  }

  /** 生产厂商 **/
  public String getCproductorid() {
    return (String) this.getAttributeValue(PraybillItemVO.CPRODUCTORID);
  }

  /** 项目 **/
  public String getCprojectid() {
    return (String) this.getAttributeValue(PraybillItemVO.CPROJECTID);
  }

  /** 项目任务 getter 方法 */
  public String getCprojecttaskid() {
    return (String) this.getAttributeValue(PraybillItemVO.CPROJECTTASKID);
  }

  /** 行号 **/
  public String getCrowno() {
    return (String) this.getAttributeValue(PraybillItemVO.CROWNO);
  }

  /** 来源单据分录标识 **/
  public String getCsourcebid() {
    return (String) this.getAttributeValue(PraybillItemVO.CSOURCEBID);
  }

  /** 来源单据标识 **/
  public String getCsourceid() {
    return (String) this.getAttributeValue(PraybillItemVO.CSOURCEID);
  }

  /** 来源单据类型 **/
  public String getCsourcetypecode() {
    return (String) this.getAttributeValue(PraybillItemVO.CSOURCETYPECODE);
  }

  /** 主计量单位 **/
  public String getCunitid() {
    return (String) this.getAttributeValue(PraybillItemVO.CUNITID);
  }

  /** 请购日期 **/
  public UFDate getDbilldate() {
    return (UFDate) this.getAttributeValue(PraybillHeaderVO.DBILLDATE);
  }

  /** 需求日期 **/
  public UFDate getDreqdate() {
    return (UFDate) this.getAttributeValue(PraybillItemVO.DREQDATE);
  }

  /** 建议订货日期 **/
  public UFDate getDsuggestdate() {
    return (UFDate) this.getAttributeValue(PraybillItemVO.DSUGGESTDATE);
  }

  @Override
  public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta(PUEntity.M20_B);
  }

  /** 累计订货数量 **/
  public UFDouble getNaccumulatenum() {
    return (UFDouble) this.getAttributeValue(PraybillItemVO.NACCUMULATENUM);
  }

  /** 数量 **/
  public UFDouble getNastnum() {
    return (UFDouble) this.getAttributeValue(PraybillItemVO.NASTNUM);
  }

  /** 生成合同次数 **/
  public Integer getNgenct() {
    return (Integer) this.getAttributeValue(PraybillItemVO.NGENCT);
  }

  /** 主数量 **/
  public UFDouble getNnum() {
    return (UFDouble) this.getAttributeValue(PraybillItemVO.NNUM);
  }

  /** 生成价格审批单次数 **/
  public Integer getNpriceauditbill() {
    return (Integer) this.getAttributeValue(PraybillItemVO.NPRICEAUDITBILL);
  }

  /** 生成询报价单次数 **/
  public Integer getNquotebill() {
    return (Integer) this.getAttributeValue(PraybillItemVO.NQUOTEBILL);
  }

  /** 本币价税合计 **/
  public UFDouble getNtaxmny() {
    return (UFDouble) this.getAttributeValue(PraybillItemVO.NTAXMNY);
  }

  /** 主本币含税单价 **/
  public UFDouble getNtaxprice() {
    return (UFDouble) this.getAttributeValue(PraybillItemVO.NTAXPRICE);
  }

  /** 批次档案 **/
  public String getPk_batchcode() {
    return (String) this.getAttributeValue(PraybillItemVO.PK_BATCHCODE);
  }

  /** 采购员 **/
  public String getPk_employee() {
    return (String) this.getAttributeValue(PraybillItemVO.PK_EMPLOYEE);
  }

  /** 所属集团 **/
  public String getPk_group() {
    return (String) this.getAttributeValue(PraybillItemVO.PK_GROUP);
  }

  /** 物料(VID) **/
  public String getPk_material() {
    return (String) this.getAttributeValue(PraybillItemVO.PK_MATERIAL);
  }

  /** 库存组织 **/
  public String getPk_org() {
    return (String) this.getAttributeValue(PraybillItemVO.PK_ORG);
  }

  /** 库存组织版本 **/
  public String getPk_org_v() {
    return (String) this.getAttributeValue(PraybillItemVO.PK_ORG_V);
  }

  /** 请购单主表_主键 **/
  public String getPk_praybill() {
    return (String) this.getAttributeValue(PraybillItemVO.PK_PRAYBILL);
  }

  /** 请购单行ID **/
  public String getPk_praybill_b() {
    return (String) this.getAttributeValue(PraybillItemVO.PK_PRAYBILL_B);
  }

  /** 产品 **/
  public String getPk_product() {
    return (String) this.getAttributeValue(PraybillItemVO.PK_PRODUCT);
  }

  /** 产品版本 **/
  public String getPk_product_v() {
    return (String) this.getAttributeValue(PraybillItemVO.PK_PRODUCT_V);
  }

  /** 采购组织 **/
  public String getPk_purchaseorg() {
    return (String) this.getAttributeValue(PraybillItemVO.PK_PURCHASEORG);
  }

  /** 采购组织版本 **/
  public String getPk_purchaseorg_v() {
    return (String) this.getAttributeValue(PraybillItemVO.PK_PURCHASEORG_V);
  }

  /** 需求部门 **/
  public String getPk_reqdept() {
    return (String) this.getAttributeValue(PraybillItemVO.PK_REQDEPT);
  }

  /** 需求部门版本 **/
  public String getPk_reqdept_v() {
    return (String) this.getAttributeValue(PraybillItemVO.PK_REQDEPT_V);
  }

  /** 原始需求库存组织最新版本 **/
  public String getPk_reqstoorg() {
    return (String) this.getAttributeValue(PraybillItemVO.PK_REQSTOORG);
  }

  /** 原始需求库存组织 **/
  public String getPk_reqstoorg_v() {
    return (String) this.getAttributeValue(PraybillItemVO.PK_REQSTOORG_V);
  }

  /** 需求仓库 **/
  public String getPk_reqstor() {
    return (String) this.getAttributeValue(PraybillItemVO.PK_REQSTOR);
  }

  /** 物料(OID) **/
  public String getPk_srcmaterial() {
    return (String) this.getAttributeValue(PraybillItemVO.PK_SRCMATERIAL);
  }

  /** 建议供应商 **/
  public String getPk_suggestsupplier() {
    return (String) this.getAttributeValue(PraybillItemVO.PK_SUGGESTSUPPLIER);
  }

  /** 来源单据行TS getter 方法 */
  public UFDateTime getSourcebts() {
    return (UFDateTime) this.getAttributeValue(OrderItemVO.SOURCEBTS);
  }

  /** 来源单据TS getter 方法 */
  public UFDateTime getSourcets() {
    return (UFDateTime) this.getAttributeValue(OrderItemVO.SOURCETS);
  }

  /** 时间戳 **/
  public UFDateTime getTs() {
    return (UFDateTime) this.getAttributeValue(PraybillItemVO.TS);
  }

  /** 批次号 **/
  public String getVbatchcode() {
    return (String) this.getAttributeValue(PraybillItemVO.VBATCHCODE);
  }

  /** 自定义项1 **/
  public String getVbdef1() {
    return (String) this.getAttributeValue(PraybillItemVO.VBDEF1);
  }

  /** 自定义项10 **/
  public String getVbdef10() {
    return (String) this.getAttributeValue(PraybillItemVO.VBDEF10);
  }

  /** 自定义项11 **/
  public String getVbdef11() {
    return (String) this.getAttributeValue(PraybillItemVO.VBDEF11);
  }

  /** 自定义项12 **/
  public String getVbdef12() {
    return (String) this.getAttributeValue(PraybillItemVO.VBDEF12);
  }

  /** 自定义项13 **/
  public String getVbdef13() {
    return (String) this.getAttributeValue(PraybillItemVO.VBDEF13);
  }

  /** 自定义项14 **/
  public String getVbdef14() {
    return (String) this.getAttributeValue(PraybillItemVO.VBDEF14);
  }

  /** 自定义项15 **/
  public String getVbdef15() {
    return (String) this.getAttributeValue(PraybillItemVO.VBDEF15);
  }

  /** 自定义项16 **/
  public String getVbdef16() {
    return (String) this.getAttributeValue(PraybillItemVO.VBDEF16);
  }

  /** 自定义项17 **/
  public String getVbdef17() {
    return (String) this.getAttributeValue(PraybillItemVO.VBDEF17);
  }

  /** 自定义项18 **/
  public String getVbdef18() {
    return (String) this.getAttributeValue(PraybillItemVO.VBDEF18);
  }

  /** 自定义项19 **/
  public String getVbdef19() {
    return (String) this.getAttributeValue(PraybillItemVO.VBDEF19);
  }

  /** 自定义项2 **/
  public String getVbdef2() {
    return (String) this.getAttributeValue(PraybillItemVO.VBDEF2);
  }

  /** 自定义项20 **/
  public String getVbdef20() {
    return (String) this.getAttributeValue(PraybillItemVO.VBDEF20);
  }

  /** 自定义项3 **/
  public String getVbdef3() {
    return (String) this.getAttributeValue(PraybillItemVO.VBDEF3);
  }

  /** 自定义项4 **/
  public String getVbdef4() {
    return (String) this.getAttributeValue(PraybillItemVO.VBDEF4);
  }

  /** 自定义项5 **/
  public String getVbdef5() {
    return (String) this.getAttributeValue(PraybillItemVO.VBDEF5);
  }

  /** 自定义项6 **/
  public String getVbdef6() {
    return (String) this.getAttributeValue(PraybillItemVO.VBDEF6);
  }

  /** 自定义项7 **/
  public String getVbdef7() {
    return (String) this.getAttributeValue(PraybillItemVO.VBDEF7);
  }

  /** 自定义项8 **/
  public String getVbdef8() {
    return (String) this.getAttributeValue(PraybillItemVO.VBDEF8);
  }

  /** 自定义项9 **/
  public String getVbdef9() {
    return (String) this.getAttributeValue(PraybillItemVO.VBDEF9);
  }

  /** 备注 **/
  public String getVbmemo() {
    return (String) this.getAttributeValue(PraybillItemVO.VBMEMO);
  }

  /** 换算率 **/
  public String getVchangerate() {
    return (String) this.getAttributeValue(PraybillItemVO.VCHANGERATE);
  }

  /** 源头单据号 **/
  public String getVfirstcode() {
    return (String) this.getAttributeValue(PraybillItemVO.VFIRSTCODE);
  }

  /** 源头单据行号 **/
  public String getVfirstrowno() {
    return (String) this.getAttributeValue(PraybillItemVO.VFIRSTROWNO);
  }

  /** 源头交易类型 **/
  public String getVfirsttrantype() {
    return (String) this.getAttributeValue(PraybillItemVO.VFIRSTTRANTYPE);
  }

  /** 自由辅助属性1 **/
  public String getVfree1() {
    return (String) this.getAttributeValue(PraybillItemVO.VFREE1);
  }

  /** 自由辅助属性10 **/
  public String getVfree10() {
    return (String) this.getAttributeValue(PraybillItemVO.VFREE10);
  }

  /** 自由辅助属性2 **/
  public String getVfree2() {
    return (String) this.getAttributeValue(PraybillItemVO.VFREE2);
  }

  /** 自由辅助属性3 **/
  public String getVfree3() {
    return (String) this.getAttributeValue(PraybillItemVO.VFREE3);
  }

  /** 自由辅助属性4 **/
  public String getVfree4() {
    return (String) this.getAttributeValue(PraybillItemVO.VFREE4);
  }

  /** 自由辅助属性5 **/
  public String getVfree5() {
    return (String) this.getAttributeValue(PraybillItemVO.VFREE5);
  }

  /** 自由辅助属性6 **/
  public String getVfree6() {
    return (String) this.getAttributeValue(PraybillItemVO.VFREE6);
  }

  /** 自由辅助属性7 **/
  public String getVfree7() {
    return (String) this.getAttributeValue(PraybillItemVO.VFREE7);
  }

  /** 自由辅助属性8 **/
  public String getVfree8() {
    return (String) this.getAttributeValue(PraybillItemVO.VFREE8);
  }

  /** 自由辅助属性9 **/
  public String getVfree9() {
    return (String) this.getAttributeValue(PraybillItemVO.VFREE9);
  }

  /** 来源单据号 **/
  public String getVsourcecode() {
    return (String) this.getAttributeValue(PraybillItemVO.VSOURCECODE);
  }

  /** 来源单据行号 **/
  public String getVsourcerowno() {
    return (String) this.getAttributeValue(PraybillItemVO.VSOURCEROWNO);
  }

  /** 来源交易类型 **/
  public String getVsrctrantypecode() {
    return (String) this.getAttributeValue(PraybillItemVO.VSRCTRANTYPECODE);
  }

  /**
   * 设置累计合同主数量,2017
   * @param totalcontractnum
   */
  public void setTotalcontractnum(UFDouble totalcontractnum) {
    this.setAttributeValue(PraybillItemVO.TOTALCONTRACTNUM,
    		totalcontractnum);
  }
  
  /** 采购组织可编辑 **/
  public void setBcanpurchaseorgedit(UFBoolean bcanpurchaseorgedit) {
    this.setAttributeValue(PraybillItemVO.BCANPURCHASEORGEDIT,
        bcanpurchaseorgedit);
  }

  /** 是否固定换算率 **/
  public void setBfixedrate(UFBoolean bfixedrate) {
    this.setAttributeValue(PraybillItemVO.BFIXEDRATE, bfixedrate);
  }

  /** 已安排 **/
  public void setBisarrange(UFBoolean bisarrange) {
    this.setAttributeValue(PraybillItemVO.BISARRANGE, bisarrange);
  }

  /** 已生成总括订单 **/
  public void setBisgensaorder(UFBoolean bisgensaorder) {
    this.setAttributeValue(PraybillItemVO.BISGENSAORDER, bisgensaorder);
  }

  /** 发布到电子商务 **/
  public void setBpublishtoec(UFBoolean bpublishtoec) {
    this.setAttributeValue(PraybillItemVO.BPUBLISHTOEC, bpublishtoec);
  }

  /** 行关闭 **/
  public void setBrowclose(UFBoolean browclose) {
    this.setAttributeValue(PraybillItemVO.BROWCLOSE, browclose);
  }

  /** 客户 setter 方法 */
  public void setCasscustid(String casscustid) {
    this.setAttributeValue(PraybillItemVO.CASSCUSTID, casscustid);
  }

  /** 单位 **/
  public void setCastunitid(String castunitid) {
    this.setAttributeValue(PraybillItemVO.CASTUNITID, castunitid);
  }

  /** 特征码 **/
  public void setCffileid(String cffileid) {
    this.setAttributeValue(PraybillItemVO.CFFILEID, cffileid);
  }

  /** 源头单据分录标识 **/
  public void setCfirstbid(String cfirstbid) {
    this.setAttributeValue(PraybillItemVO.CFIRSTBID, cfirstbid);
  }

  /** 源头单据标识 **/
  public void setCfirstid(String cfirstid) {
    this.setAttributeValue(PraybillItemVO.CFIRSTID, cfirstid);
  }

  /** 源头单据类型 **/
  public void setCfirsttypecode(String cfirsttypecode) {
    this.setAttributeValue(PraybillItemVO.CFIRSTTYPECODE, cfirsttypecode);
  }

  /** 订单类型 **/
  public void setCordertrantypecode(String cordertrantypecode) {
    this.setAttributeValue(PraybillItemVO.CORDERTRANTYPECODE,
        cordertrantypecode);
  }

  /** 生产厂商 **/
  public void setCproductorid(String cproductorid) {
    this.setAttributeValue(PraybillItemVO.CPRODUCTORID, cproductorid);
  }

  /** 项目 **/
  public void setCprojectid(String cprojectid) {
    this.setAttributeValue(PraybillItemVO.CPROJECTID, cprojectid);
  }

  /** 项目任务 setter 方法 */
  public void setCprojecttaskid(String cprojecttaskid) {
    this.setAttributeValue(PraybillItemVO.CPROJECTTASKID, cprojecttaskid);
  }

  /** 行号 **/
  public void setCrowno(String crowno) {
    this.setAttributeValue(PraybillItemVO.CROWNO, crowno);
  }

  /** 来源单据分录标识 **/
  public void setCsourcebid(String csourcebid) {
    this.setAttributeValue(PraybillItemVO.CSOURCEBID, csourcebid);
  }

  /** 来源单据标识 **/
  public void setCsourceid(String csourceid) {
    this.setAttributeValue(PraybillItemVO.CSOURCEID, csourceid);
  }

  /** 来源单据类型 **/
  public void setCsourcetypecode(String csourcetypecode) {
    this.setAttributeValue(PraybillItemVO.CSOURCETYPECODE, csourcetypecode);
  }

  /** 主计量单位 **/
  public void setCunitid(String cunitid) {
    this.setAttributeValue(PraybillItemVO.CUNITID, cunitid);
  }

  /** 请购日期 **/
  public void setDbilldate(UFDate dbilldate) {
    this.setAttributeValue(PraybillHeaderVO.DBILLDATE, dbilldate);
  }

  /** 需求日期 **/
  public void setDreqdate(UFDate dreqdate) {
    this.setAttributeValue(PraybillItemVO.DREQDATE, dreqdate);
  }

  /** 建议订货日期 **/
  public void setDsuggestdate(UFDate dsuggestdate) {
    this.setAttributeValue(PraybillItemVO.DSUGGESTDATE, dsuggestdate);
  }

  /** 累计订货数量 **/
  public void setNaccumulatenum(UFDouble naccumulatenum) {
    this.setAttributeValue(PraybillItemVO.NACCUMULATENUM, naccumulatenum);
  }

  /** 数量 **/
  public void setNastnum(UFDouble nastnum) {
    this.setAttributeValue(PraybillItemVO.NASTNUM, nastnum);
  }

  /** 生成合同次数 **/
  public void setNgenct(Integer ngenct) {
    this.setAttributeValue(PraybillItemVO.NGENCT, ngenct);
  }

  /** 主数量 **/
  public void setNnum(UFDouble nnum) {
    this.setAttributeValue(PraybillItemVO.NNUM, nnum);
  }

  /** 生成价格审批单次数 **/
  public void setNpriceauditbill(Integer npriceauditbill) {
    this.setAttributeValue(PraybillItemVO.NPRICEAUDITBILL, npriceauditbill);
  }

  /** 生成询报价单次数 **/
  public void setNquotebill(Integer nquotebill) {
    this.setAttributeValue(PraybillItemVO.NQUOTEBILL, nquotebill);
  }

  /** 本币价税合计 **/
  public void setNtaxmny(UFDouble ntaxmny) {
    this.setAttributeValue(PraybillItemVO.NTAXMNY, ntaxmny);
  }

  /** 主本币含税单价 **/
  public void setNtaxprice(UFDouble ntaxprice) {
    this.setAttributeValue(PraybillItemVO.NTAXPRICE, ntaxprice);
  }

  /** 批次档案 **/
  public void setPk_batchcode(String pk_batchcode) {
    this.setAttributeValue(PraybillItemVO.PK_BATCHCODE, pk_batchcode);
  }

  /** 采购员 **/
  public void setPk_employee(String pk_employee) {
    this.setAttributeValue(PraybillItemVO.PK_EMPLOYEE, pk_employee);
  }

  /** 所属集团 **/
  public void setPk_group(String pk_group) {
    this.setAttributeValue(PraybillItemVO.PK_GROUP, pk_group);
  }

  /** 物料(VID) **/
  public void setPk_material(String pk_material) {
    this.setAttributeValue(PraybillItemVO.PK_MATERIAL, pk_material);
  }

  /** 库存组织 **/
  public void setPk_org(String pk_org) {
    this.setAttributeValue(PraybillItemVO.PK_ORG, pk_org);
  }

  /** 库存组织版本 **/
  public void setPk_org_v(String pk_org_v) {
    this.setAttributeValue(PraybillItemVO.PK_ORG_V, pk_org_v);
  }

  /** 请购单主表_主键 **/
  public void setPk_praybill(String pk_praybill) {
    this.setAttributeValue(PraybillItemVO.PK_PRAYBILL, pk_praybill);
  }

  /** 请购单行ID **/
  public void setPk_praybill_b(String pk_praybill_b) {
    this.setAttributeValue(PraybillItemVO.PK_PRAYBILL_B, pk_praybill_b);
  }

  /** 产品 **/
  public void setPk_product(String pk_product) {
    this.setAttributeValue(PraybillItemVO.PK_PRODUCT, pk_product);
  }

  /** 产品版本 **/
  public void setPk_product_v(String pk_product_v) {
    this.setAttributeValue(PraybillItemVO.PK_PRODUCT_V, pk_product_v);
  }

  /** 采购组织 **/
  public void setPk_purchaseorg(String pk_purchaseorg) {
    this.setAttributeValue(PraybillItemVO.PK_PURCHASEORG, pk_purchaseorg);
  }

  /** 采购组织版本 **/
  public void setPk_purchaseorg_v(String pk_purchaseorg_v) {
    this.setAttributeValue(PraybillItemVO.PK_PURCHASEORG_V, pk_purchaseorg_v);
  }

  /** 需求部门 **/
  public void setPk_reqdept(String pk_reqdept) {
    this.setAttributeValue(PraybillItemVO.PK_REQDEPT, pk_reqdept);
  }

  /** 需求部门版本 **/
  public void setPk_reqdept_v(String pk_reqdept_v) {
    this.setAttributeValue(PraybillItemVO.PK_REQDEPT_V, pk_reqdept_v);
  }

  /** 原始需求库存组织最新版本 **/
  public void setPk_reqstoorg(String pk_reqstoorg) {
    this.setAttributeValue(PraybillItemVO.PK_REQSTOORG, pk_reqstoorg);
  }

  /** 原始需求库存组织 **/
  public void setPk_reqstoorg_v(String pk_reqstoorg_v) {
    this.setAttributeValue(PraybillItemVO.PK_REQSTOORG_V, pk_reqstoorg_v);
  }

  /** 需求仓库 **/
  public void setPk_reqstor(String pk_reqstor) {
    this.setAttributeValue(PraybillItemVO.PK_REQSTOR, pk_reqstor);
  }

  /** 物料(OID) **/
  public void setPk_srcmaterial(String pk_srcmaterial) {
    this.setAttributeValue(PraybillItemVO.PK_SRCMATERIAL, pk_srcmaterial);
  }

  /** 建议供应商 **/
  public void setPk_suggestsupplier(String pk_suggestsupplier) {
    this.setAttributeValue(PraybillItemVO.PK_SUGGESTSUPPLIER,
        pk_suggestsupplier);
  }

  /** 来源单据行TS setter 方法 */
  public void setSourcebts(UFDateTime sourcebts) {
    this.setAttributeValue(OrderItemVO.SOURCEBTS, sourcebts);
  }

  /** 来源单据TS setter 方法 */
  public void setSourcets(UFDateTime sourcets) {
    this.setAttributeValue(OrderItemVO.SOURCETS, sourcets);
  }

  /** 时间戳 **/
  public void setTs(UFDateTime ts) {
    this.setAttributeValue(PraybillItemVO.TS, ts);
  }

  /** 批次号 **/
  public void setVbatchcode(String vbatchcode) {
    this.setAttributeValue(PraybillItemVO.VBATCHCODE, vbatchcode);
  }

  /** 自定义项1 **/
  public void setVbdef1(String vbdef1) {
    this.setAttributeValue(PraybillItemVO.VBDEF1, vbdef1);
  }

  /** 自定义项10 **/
  public void setVbdef10(String vbdef10) {
    this.setAttributeValue(PraybillItemVO.VBDEF10, vbdef10);
  }

  /** 自定义项11 **/
  public void setVbdef11(String vbdef11) {
    this.setAttributeValue(PraybillItemVO.VBDEF11, vbdef11);
  }

  /** 自定义项12 **/
  public void setVbdef12(String vbdef12) {
    this.setAttributeValue(PraybillItemVO.VBDEF12, vbdef12);
  }

  /** 自定义项13 **/
  public void setVbdef13(String vbdef13) {
    this.setAttributeValue(PraybillItemVO.VBDEF13, vbdef13);
  }

  /** 自定义项14 **/
  public void setVbdef14(String vbdef14) {
    this.setAttributeValue(PraybillItemVO.VBDEF14, vbdef14);
  }

  /** 自定义项15 **/
  public void setVbdef15(String vbdef15) {
    this.setAttributeValue(PraybillItemVO.VBDEF15, vbdef15);
  }

  /** 自定义项16 **/
  public void setVbdef16(String vbdef16) {
    this.setAttributeValue(PraybillItemVO.VBDEF16, vbdef16);
  }

  /** 自定义项17 **/
  public void setVbdef17(String vbdef17) {
    this.setAttributeValue(PraybillItemVO.VBDEF17, vbdef17);
  }

  /** 自定义项18 **/
  public void setVbdef18(String vbdef18) {
    this.setAttributeValue(PraybillItemVO.VBDEF18, vbdef18);
  }

  /** 自定义项19 **/
  public void setVbdef19(String vbdef19) {
    this.setAttributeValue(PraybillItemVO.VBDEF19, vbdef19);
  }

  /** 自定义项2 **/
  public void setVbdef2(String vbdef2) {
    this.setAttributeValue(PraybillItemVO.VBDEF2, vbdef2);
  }

  /** 自定义项20 **/
  public void setVbdef20(String vbdef20) {
    this.setAttributeValue(PraybillItemVO.VBDEF20, vbdef20);
  }

  /** 自定义项3 **/
  public void setVbdef3(String vbdef3) {
    this.setAttributeValue(PraybillItemVO.VBDEF3, vbdef3);
  }

  /** 自定义项4 **/
  public void setVbdef4(String vbdef4) {
    this.setAttributeValue(PraybillItemVO.VBDEF4, vbdef4);
  }

  /** 自定义项5 **/
  public void setVbdef5(String vbdef5) {
    this.setAttributeValue(PraybillItemVO.VBDEF5, vbdef5);
  }

  /** 自定义项6 **/
  public void setVbdef6(String vbdef6) {
    this.setAttributeValue(PraybillItemVO.VBDEF6, vbdef6);
  }

  /** 自定义项7 **/
  public void setVbdef7(String vbdef7) {
    this.setAttributeValue(PraybillItemVO.VBDEF7, vbdef7);
  }

  /** 自定义项8 **/
  public void setVbdef8(String vbdef8) {
    this.setAttributeValue(PraybillItemVO.VBDEF8, vbdef8);
  }

  /** 自定义项9 **/
  public void setVbdef9(String vbdef9) {
    this.setAttributeValue(PraybillItemVO.VBDEF9, vbdef9);
  }

  /** 备注 **/
  public void setVbmemo(String vmemo) {
    this.setAttributeValue(PraybillItemVO.VBMEMO, vmemo);
  }

  /** 换算率 **/
  public void setVchangerate(String vchangerate) {
    this.setAttributeValue(PraybillItemVO.VCHANGERATE, vchangerate);
  }

  /** 源头单据号 **/
  public void setVfirstcode(String vfirstcode) {
    this.setAttributeValue(PraybillItemVO.VFIRSTCODE, vfirstcode);
  }

  /** 源头单据行号 **/
  public void setVfirstrowno(String vfirstrowno) {
    this.setAttributeValue(PraybillItemVO.VFIRSTROWNO, vfirstrowno);
  }

  /** 源头交易类型 **/
  public void setVfirsttrantype(String vfirsttrantype) {
    this.setAttributeValue(PraybillItemVO.VFIRSTTRANTYPE, vfirsttrantype);
  }

  /** 自由辅助属性1 **/
  public void setVfree1(String vfree1) {
    this.setAttributeValue(PraybillItemVO.VFREE1, vfree1);
  }

  /** 自由辅助属性10 **/
  public void setVfree10(String vfree10) {
    this.setAttributeValue(PraybillItemVO.VFREE10, vfree10);
  }

  /** 自由辅助属性2 **/
  public void setVfree2(String vfree2) {
    this.setAttributeValue(PraybillItemVO.VFREE2, vfree2);
  }

  /** 自由辅助属性3 **/
  public void setVfree3(String vfree3) {
    this.setAttributeValue(PraybillItemVO.VFREE3, vfree3);
  }

  /** 自由辅助属性4 **/
  public void setVfree4(String vfree4) {
    this.setAttributeValue(PraybillItemVO.VFREE4, vfree4);
  }

  /** 自由辅助属性5 **/
  public void setVfree5(String vfree5) {
    this.setAttributeValue(PraybillItemVO.VFREE5, vfree5);
  }

  /** 自由辅助属性6 **/
  public void setVfree6(String vfree6) {
    this.setAttributeValue(PraybillItemVO.VFREE6, vfree6);
  }

  /** 自由辅助属性7 **/
  public void setVfree7(String vfree7) {
    this.setAttributeValue(PraybillItemVO.VFREE7, vfree7);
  }

  /** 自由辅助属性8 **/
  public void setVfree8(String vfree8) {
    this.setAttributeValue(PraybillItemVO.VFREE8, vfree8);
  }

  /** 自由辅助属性9 **/
  public void setVfree9(String vfree9) {
    this.setAttributeValue(PraybillItemVO.VFREE9, vfree9);
  }

  /** 来源单据号 **/
  public void setVsourcecode(String vsourcecode) {
    this.setAttributeValue(PraybillItemVO.VSOURCECODE, vsourcecode);
  }

  /** 来源单据行号 **/
  public void setVsourcerowno(String vsourcerowno) {
    this.setAttributeValue(PraybillItemVO.VSOURCEROWNO, vsourcerowno);
  }

  /** 来源交易类型 **/
  public void setVsrctrantypecode(String vsrctrantypecode) {
    this.setAttributeValue(PraybillItemVO.VSRCTRANTYPECODE, vsrctrantypecode);
  }

}
