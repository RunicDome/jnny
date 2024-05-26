/**
 * $文件说明$
 * 
 * @author GGR
 * @version 6.0
 * @see
 * @since 6.0
 * @time 2010-2-1 上午10:57:07
 */
package nc.bs.pu.m20.query;

import nc.bs.pu.m20.query.logicutil.CondTOWhereUtil;
import nc.vo.pu.m20.entity.PraybillHeaderVO;
import nc.vo.pu.m20.entity.PraybillVO;
import nc.vo.pu.pub.enumeration.POEnumBillStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pubapp.query2.sql.process.QueryCondition;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;
import nc.vo.scmpub.res.billtype.CTBillType;
import nc.vo.scmpub.res.billtype.POBillType;

/**
 * <p>
 * <b>本类主要完成以下功能：</b>
 * <ul>
 * <li>请购单为采购合同提供查询服务的查询BP
 * </ul>
 * <p>
 * <p>
 * 
 * @version 6.0
 * @since 6.0
 * @author GGR
 * @time 2010-5-12 上午11:34:23
 */
public class QueryForZ2BP extends AbstractRefQueryBP {

  public QueryForZ2BP(QuerySchemeProcessor psor) {
    super(psor);
    // 增加接口定义的交易类型过滤条件-采购合同拉请购单
    this.psor.appendRefTrantypeWhere(POBillType.PrayBill.getCode(),
        CTBillType.PurDaily.getCode(), PraybillHeaderVO.VTRANTYPECODE);

  }

  @Override
  public StringBuffer makeGetPKSql() {
    // 没必要使用DISTINCT，也会影响效率
    StringBuffer sql = new StringBuffer();
    sql.append(" select " + this.headtb + ".pk_praybill,");
    sql.append(this.itemtb + ".pk_praybill_b ");
    sql.append(this.psor.getFinalFromWhere());
    sql.append(" and " + this.headtb + ".bislatest = 'Y'");
    sql.append(" and " + this.headtb + ".fbillstatus = "
        + POEnumBillStatus.APPROVE.value());
    sql.append(" and " + this.headtb + ".dr = 0");
    sql.append(" and " + this.headtb + ".pk_praybill = " + this.itemtb
        + ".pk_praybill");
    sql.append(" and " + this.itemtb + ".browclose = 'N'");
    sql.append(" and " + this.itemtb + ".bpublishtoec = 'N'");
    sql.append(" and " + this.itemtb + ".dr = 0");
    
    //begin-请购单表体累计合同主数量控制生成合同数量，参照的时候过滤生成合同主数量等于请购单主数量的数据
    sql.append(" and " + this.itemtb + ".nnum - isnull(" + this.itemtb + ".totalcontractnum,0) > 0");
    //end-请购单表体累计合同主数量控制生成合同数量，参照的时候过滤生成合同主数量等于请购单主数量的数据
    // 处理是否已经生成询报价单
    CondTOWhereUtil.buildIsngenct(this.psor, sql, this.itemtb);

    // 处理是否过滤未购辅数量为0的请购单行
    CondTOWhereUtil.buildFilterzeroflag(this.psor, sql, this.itemtb);

    // 是否已经生成合同处理
    CondTOWhereUtil.buildIsngenct(this.psor, sql, this.itemtb);

    // 请购安排处理
    CondTOWhereUtil.buildIsarrange(sql, this.headtb, this.itemtb);

    // 按照采购岗过滤物料
    FilterForPosUtil.filterMaterialByPos(this.psor, sql);

    return sql;
  }

  /**
   * <b>本类主要完成以下功能：</b>
   * <ul>
   * <li>处理“已经生成订单量 作为合同执行”
   * <li>是因为如下场景：
   * <li>请购单与采购订单已经执行过多次，比如请购单数量为100，订单执行为 80
   * <li>采购合同拉请购单，在查询条件中，并且“已经生成订单量 作为合同执行”选“是”
   * <li>请购单的表头属性 已经生成订单量 作为合同执行 属性 为 true;
   * <li>如果采购合同表头的 “已经生成订单量 作为合同执行” 为true
   * <li>采购合同在生效的时候，与采购订单回写。
   */
  @Override
  protected PraybillVO[] processQueryResult(PraybillVO[] queryResult) {
    QueryCondition con =
        this.psor.getQueryCondition(PraybillHeaderVO.BORDERNUMEXEC);
    if (con != null && con.getValues() != null) {
      Object bExe = con.getValues()[0];
      if (UFBoolean.TRUE.toString().equals(bExe)) {
        for (PraybillVO bill : queryResult) {
          PraybillHeaderVO header = bill.getHVO();
          header.setBordernumexec(UFBoolean.TRUE);
        }
      }
    }
    return queryResult;
  }

}
