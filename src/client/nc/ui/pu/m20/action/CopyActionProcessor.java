package nc.ui.pu.m20.action;

import nc.ui.pubapp.uif2app.actions.intf.ICopyActionProcessor;
import nc.vo.pu.m20.entity.PraybillHeaderVO;
import nc.vo.pu.m20.entity.PraybillItemVO;
import nc.vo.pu.m20.entity.PraybillVO;
import nc.vo.pu.m20.enumeration.EnumPraySource;
import nc.vo.pu.pub.enumeration.POEnumBillStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pubapp.AppContext;
import nc.vo.uif2.LoginContext;

/**
 * <p>
 * <b>本类主要完成以下功能：</b>
 * <ul>
 * <li>复制ActionProcessor
 * </ul>
 * <p>
 * <p>
 * 
 * @version 6.0
 * @since 6.0
 * @author GGR
 * @time 2010-4-13 下午06:06:48
 */
public class CopyActionProcessor<E extends PraybillVO> implements
    ICopyActionProcessor<E> {

  /**
   * 父类方法重写
   * 
   * @see nc.ui.pubapp.uif2app.actions.intf.ICopyActionProcessor#processVOAfterCopy(nc.vo.pubapp.pattern.model.entity.bill.AbstractBill,
   *      nc.vo.uif2.LoginContext)
   */
  @Override
  public void processVOAfterCopy(PraybillVO billVO, LoginContext context) {
    // 设定表头
    this.setHeadValue(billVO, context);

    // 设定表体
    this.setBodyValue(billVO);

  }

  /**
   * 设定表体值。
   * <p>
   * <b>参数说明</b>
   * 
   * @param billVO
   *          <p>
   * @since 6.0
   * @author GGR
   * @time 2010-4-13 下午06:07:59
   */
  private void setBodyValue(PraybillVO billVO) {
    PraybillItemVO[] itemVOs = billVO.getBVO();

    for (int i = 0; i < itemVOs.length; ++i) {
      // 请购单行
      itemVOs[i].setPk_praybill_b(null);
      // 请购单表头_主键
      itemVOs[i].setPk_praybill(null);
      // 累计订货数量
      itemVOs[i].setNaccumulatenum(null);
      // 生成合同次数
      itemVOs[i].setNgenct(null);
      // 生成价格审批单次数
      itemVOs[i].setNpriceauditbill(null);
      // 生成询报价单次数
      itemVOs[i].setNquotebill(null);
      // 行关闭
      itemVOs[i].setBrowclose(UFBoolean.FALSE);
      // 发布到电子商务
      itemVOs[i].setBpublishtoec(UFBoolean.FALSE);
      // 来源单据号
      itemVOs[i].setVsourcecode(null);
      // 来源单据ID
      itemVOs[i].setCsourceid(null);
      // 来源单据行ID
      itemVOs[i].setCsourcebid(null);
      // 来源单据类型
      itemVOs[i].setCsourcetypecode(null);
      // 来源交易类型
      itemVOs[i].setVsrctrantypecode(null);
      // 来源单据行号
      itemVOs[i].setVsourcerowno(null);
      // 源头单据ID
      itemVOs[i].setCfirstid(null);
      // 上层来源单据行ID
      itemVOs[i].setCfirstbid(null);
      // 源头单据类型
      itemVOs[i].setCfirsttypecode(null);
      // 源头单据号
      itemVOs[i].setVfirstcode(null);
      // 源头单据行号
      itemVOs[i].setVfirstrowno(null);
      // 源头交易类型
      itemVOs[i].setVfirsttrantype(null);
      // ts
      itemVOs[i].setTs(null);
      // 复制时清空表体“累计合同数量”,2017
      itemVOs[i].setTotalcontractnum(null);
      // 已生成总括订单
      itemVOs[i].setBisgensaorder(UFBoolean.FALSE);
      // 请购安排
      itemVOs[i].setBisarrange(UFBoolean.FALSE);
      // 请购日期
      UFDate busidate = AppContext.getInstance().getBusiDate();
      itemVOs[i].setDbilldate(busidate);
      // 特征码
//      itemVOs[i].setCffileid(null);
    }
  }

  /**
   * 设定表头值。
   * <p>
   * <b>参数说明</b>
   * 
   * @param billVO
   *          请购单
   * @param context
   *          <p>
   * @since 6.0
   * @author GGR
   * @time 2010-4-13 下午06:07:40
   */
  private void setHeadValue(PraybillVO billVO, LoginContext context) {
    PraybillHeaderVO headerVO = billVO.getHVO();

    // 请购单
    headerVO.setPk_praybill(null);
    // 直运
    headerVO.setBdirecttransit(null);
    // 请购单编号
    headerVO.setVbillcode(null);
    // 请购单日期
    UFDate busidate = AppContext.getInstance().getBusiDate();
    headerVO.setDbilldate(busidate);
    // 请购单来源
    headerVO.setFpraysource(EnumPraySource.MANUAL.toInteger());
    // 请购单状态
    headerVO.setFbillstatus(POEnumBillStatus.FREE.toInteger());
    // 版本信息
    headerVO.setNversion(Integer.valueOf(1));
    // 打印次数
    headerVO.setIprintcount(null);
    // ts
    headerVO.setTs(null);
    // 制单人
    headerVO.setBillmaker(context.getPk_loginUser());
    // 创建时间
    headerVO.setCreationtime(null);
    // 修改人
    headerVO.setModifier(null);
    // 修改时间
    headerVO.setModifiedtime(null);
    // 修订人
    headerVO.setCreviseoperid(null);
    // 修订时间
    headerVO.setTrevisiontime(null);
    // 审批人
    headerVO.setApprover(null);
    // 审批时间
    headerVO.setTaudittime(null);
    // 请购日期
    headerVO.setDmakedate(null);
  }

}
