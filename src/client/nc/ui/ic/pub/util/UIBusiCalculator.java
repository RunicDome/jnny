package nc.ui.ic.pub.util;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.ic.material.query.InvInfoUIQuery;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.calculator.data.BillCardPanelDataSet;
import nc.ui.pubapp.pub.scale.UIScaleUtils;
import nc.vo.ic.param.ICSysParam;
import nc.vo.ic.pub.calc.BusiCalculator;
import nc.vo.ic.pub.calc.CalcContext;
import nc.vo.pubapp.calculator.data.IDataSetForCal;

/**
 * <p>
 * <b>客户端的业务数据计算器</b>
 * <p>
 * 
 * @version v60
 * @since v60
 * @author yangb
 * @time 2010-7-11 下午08:28:31
 */
public class UIBusiCalculator extends BusiCalculator {

  /**
   * UIBusiCalculator 的构造子
   */
  public UIBusiCalculator() {
    super(UIScaleUtils.getScaleUtils());
    CalcContext cont =
        new CalcContext(new ICSysParam(WorkbenchEnvironment.getInstance()
            .getGroupVO().getPk_group()), InvInfoUIQuery.getInstance()
            .getInvInfoQuery());
    this.setCalcContext(cont);
    this.setDataSetCreator(new UIDataSetForCalCreator());
  }

  /**
   * 方法功能描述：计算数量、单价及金额
   * 
   */
  public void calc(BillCardPanel card, String calckey, int row) {

    this.calc(this.getUIDataSetCreator().getIDataSetForCal(card, row), calckey);
  }

  /**
   * 方法功能描述：计算数量、单价及金额
   * 
   */
  public void calc(BillCardPanel card, String calckey, int... rows) {
    this.calcs(card, calckey, rows);
  }

  /**
   * 方法功能描述：仅计算数量
   * 
   */
  public void calcNum(BillCardPanel card, String calckey, int row) {
    this.calcNum(this.getUIDataSetCreator().getIDataSetForCal(card, row),
        calckey);
  }

  public void calcOnlyMny(BillCardPanel card, String calckey, int row) {
    this.calc(this.getUIDataSetCreator().getIDataSetForCal4OnlyMny(card, row),
        calckey);
  }

  public void calcMny(BillCardPanel card, String calckey, int... rows) {
    for (int row : rows) {
      this.calc(this.getUIDataSetCreator().getIDataSetForCalMny(card, row),
          calckey);
    }
  }

  /**
   * 方法功能描述：仅计算数量
   * 
   */
  public void calcNum(BillCardPanel card, String calckey, int... rows) {
    this.calcNums(card, calckey, rows);
  }

  /**
   * 方法功能描述：仅计算数量
   * 
   */
  public void calcNums(BillCardPanel card, String calckey, int[] rows) {

    IDataSetForCal[] datas = new IDataSetForCal[rows.length];
    for (int i = 0; i < rows.length; i++) {
      datas[i] = this.getUIDataSetCreator().getIDataSetForCal(card, rows[i]);
    }
    this.calcNum(datas, calckey);
  }

  /**
   * 方法功能描述：计算数量、单价及金额
   * 
   */
  public void calcs(BillCardPanel card, String calckey, int[] rows) {

    IDataSetForCal[] datas = new BillCardPanelDataSet[rows.length];
    for (int i = 0; i < rows.length; i++) {
      datas[i] = this.getUIDataSetCreator().getIDataSetForCal(card, rows[i]);
    }
    this.calc(datas, calckey);
  }

  /**
   * 根据应收主数量计算应收业务数量
   * 
   * @param card
   * @param calckey
   * @param row
   */
  public void calcShouldAstNum(BillCardPanel card, String calckey, int row) {

    this.calcShouldAstNum(this.getUIDataSetCreator()
        .getShouldNumIDataSetForCal(card, row), calckey);
  }

  /**
   * 方法功能描述：仅计算应收发数量
   * 
   */
  public void calcShouldNum(BillCardPanel card, String calckey, int row) {

    this.calcNum(
        this.getUIDataSetCreator().getShouldNumIDataSetForCal(card, row),
        calckey);
  }

  /**
   * 方法功能描述：仅计算应收发数量
   * 
   */
  public void calcShouldNums(BillCardPanel card, String calckey, int[] rows) {

    IDataSetForCal[] datas = new IDataSetForCal[rows.length];
    for (int i = 0; i < rows.length; i++) {
      datas[i] =
          this.getUIDataSetCreator().getShouldNumIDataSetForCal(card, rows[i]);
    }
    this.calcNum(datas, calckey);
  }

  /**
   * @return dataSetCreator
   */
  public UIDataSetForCalCreator getUIDataSetCreator() {
    return (UIDataSetForCalCreator) super.getDataSetCreator();
  }

}
