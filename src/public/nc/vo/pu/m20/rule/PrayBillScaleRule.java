/**
 * $文件说明$
 * 
 * @author tianft
 * @version 6.0
 * @see
 * @since 6.0
 * @time 2010-10-22 下午02:38:15
 */
package nc.vo.pu.m20.rule;

import nc.vo.pu.m20.entity.PraybillHeaderVO;
import nc.vo.pu.m20.entity.PraybillItemVO;
import nc.vo.pu.pub.rule.IScaleProcessor;
import nc.vo.pubapp.scale.BillScaleProcessor;
import nc.vo.pubapp.scale.PosEnum;
import nc.vo.pubapp.scale.TotalValueScale;

/**
 * <p>
 * <b>本类主要完成以下功能：</b>
 * <ul>
 * <li>请购单精度处理类
 * </ul>
 * <p>
 * <p>
 * 
 * @version 6.0
 * @since 6.0
 * @author tianft
 * @time 2010-10-22 下午02:38:15
 */
public class PrayBillScaleRule implements IScaleProcessor {

  /**
   * 方法功能描述：
   * <p>
   * <b>参数说明</b>
   * 
   * @param scale
   * @param total <p>
   * @since 6.0
   * @author gaogr
   * @time 2010-10-22 下午02:44:17
   */
  @Override
  public void setScale(BillScaleProcessor scale, TotalValueScale total) {
    if (scale != null) {
      this.setBodyScale(scale);
    }
    if (total != null) {
      this.setHeadScale(total);
    }
  }

  /**
   * 外部平台导入时需要的精度处理，不处理换算率精度
   * 
   * @param scale
   */
  public void setScaleForCheck(BillScaleProcessor scale) {
    if (scale != null) {
      this.setScale(scale, PosEnum.body, true);
    }
  }

  @Override
  public void setScaleForSingleTable(BillScaleProcessor scale,
      TotalValueScale totalScale) {
    if (scale != null) {
      this.setScale(scale, PosEnum.head, false);
    }
    if (totalScale != null) {
      this.setHeadScale(totalScale);
    }
  }

  /**
   * 方法功能描述：
   * <p>
   * <b>参数说明</b>
   * 
   * @param scale <p>
   * @since 6.0
   * @author gaogr
   * @time 2010-10-22 下午02:42:26
   */
  private void setBodyScale(BillScaleProcessor scale) {
    this.setScale(scale, PosEnum.body, false);
  }

  /**
   * 方法功能描述：
   * <p>
   * <b>参数说明</b>
   * 
   * @param total <p>
   * @since 6.0
   * @author gaogr
   * @time 2010-10-22 下午02:42:45
   */
  private void setHeadScale(TotalValueScale total) {
    // 合计信息精度控制器
    total.setHeadTailKeys(new String[] {
      PraybillHeaderVO.NTOTALASTNUM
    });
  }

  private void setScale(BillScaleProcessor scale, PosEnum posEnum,
      boolean forScaleCheck) {
    // 换算率
    String[] changeRates = new String[] {
      PraybillItemVO.VCHANGERATE,
    };
    // 本币金额
    String[] mnykeys = new String[] {
      PraybillItemVO.NTAXMNY
    };
    // 表头合计金额
    String[] headMnykeys = new String[] {
      PraybillHeaderVO.NTOTALTAXMNY
    };
    // 业务单位数量
    String[] assistNumkeys = new String[] {
      PraybillItemVO.NASTNUM
    };
    // 主数量
    // 累计合同数量按照主单位精度来处理，2017
    String[] numkeys = new String[] {
      PraybillItemVO.NNUM, PraybillItemVO.NACCUMULATENUM,
      PraybillItemVO.TOTALCONTRACTNUM
    };
    // 价格
    String[] pricekeys = new String[] {
      PraybillItemVO.NTAXPRICE
    };
    if (!forScaleCheck) {
      // 换算率精度
      scale.setHslCtlInfo(changeRates, posEnum, null);

      // 表头合计金额－根据公共需求2011.9.7走模板精度
      // 2013-5-9，表头合计数量的精度钟老大和孙宝前已经确定抹零处理（金额合计一般都会按币种处理，特殊需要抹零的请各个确认）。
      // 外部导入不检查了吧，是在后台加的。
      scale.setMnyCtlInfo(headMnykeys, PosEnum.head, null,
          PraybillHeaderVO.CCURRENCYID, PosEnum.head, null);
    }
    // 业务单位数量精度
    scale.setNumCtlInfo(assistNumkeys, posEnum, null,
        PraybillItemVO.CASTUNITID, posEnum, null);
    // 主单位数量精度
    scale.setNumCtlInfo(numkeys, posEnum, null, PraybillItemVO.CUNITID,
        posEnum, null);
    // 本币金额精度
    scale.setMnyCtlInfo(mnykeys, posEnum, null, PraybillHeaderVO.CCURRENCYID,
        PosEnum.head, null);
    // 单价精度
    scale.setPriceCtlInfo(pricekeys, posEnum, null,
        PraybillHeaderVO.CCURRENCYID, PosEnum.head, null);
    // 进行计算
    scale.process();
  }
}
