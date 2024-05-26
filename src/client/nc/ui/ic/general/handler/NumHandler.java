/**
 * $文件说明$
 * 
 * @author liuzy
 * @version 6.0
 * @see
 * @since 6.0
 * @time 2010-3-30 下午03:37:58
 */
package nc.ui.ic.general.handler;

import java.util.List;

import nc.ui.ic.general.deal.GenUIProcessorInfo;
import nc.ui.ic.general.model.ICGenBizEditorModel;
import nc.ui.ic.org.query.OrgInfoUIQuery;
import nc.ui.ic.pub.handler.card.ICCardEditEventHandler;
import nc.ui.ic.pub.util.UIBusiCalculator;
import nc.ui.pubapp.pub.scale.UIScaleUtils;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
import nc.vo.ic.pub.calc.PriceAndMoneyCalculator;
import nc.vo.ic.pub.calc.PriceAndMoneyCalculator.MnyCalcType;
import nc.vo.ic.pub.define.ICPubMetaNameConst;
import nc.vo.pub.lang.UFDouble;

/**
 * <p>
 * <b>本类主要完成以下功能：数量编辑事件处理</b>
 * <ul>
 * <li>
 * </ul>
 * <p>
 * <p>
 * 
 * @version 6.0
 * @since 6.0
 * @author liuzy
 * @time 2010-3-30 下午03:37:58
 */
public class NumHandler extends ICCardEditEventHandler {

  @Override
  public void afterCardBodyEdit(CardBodyAfterEditEvent event) {
    if (event.getAfterEditEventState() == CardBodyAfterEditEvent.BATCHCOPYEND) {
      // Atl+shift多选会走到
      this.handleMultiRow(event);
    }
    else if(
        event.getAfterEditEventState()!= CardBodyAfterEditEvent.BATCHCOPYBEGIN
        && event.getAfterEditEventState()!=CardBodyAfterEditEvent.BATCHCOPING) {
      this.handleSingleRow(event);
    }
  }
  
  public void handleForAutoPick(CardBodyAfterEditEvent event){
    this.handleSingleRow(event);
  }

  private void handleSingleRow(CardBodyAfterEditEvent event) {
    int rownum = event.getRow();

    this.processSingRowNumAndMny(event);
    
    // 修改了数量会导致重算主数量，重算了主数量就要重新计算金额
    PriceAndMoneyCalculator pcalc =
        new PriceAndMoneyCalculator(UIScaleUtils.getScaleUtils(),
            OrgInfoUIQuery.getInstance().getOrgInfoQuery());
    pcalc.calcPriceMny(
        ((ICGenBizEditorModel) this.getEditorModel()).getICUIBillEntity(),
        MnyCalcType.Num, rownum);
    // 计算计划金额
    pcalc.calcPlannedPriceMny(
        ((ICGenBizEditorModel) this.getEditorModel()).getICUIBillEntity(),
        MnyCalcType.Num, rownum);

    // 数量编辑后处理
    ((GenUIProcessorInfo) this.getEditorModel().getContext()
        .getUiprocesorinfo()).getGenAfterNumEditProcessor().process(rownum,
        this.getEditorModel());
    // 是否清空序列号信息
    this.clearBodySNInfo(rownum);
  }
  
  /**
   * 该方法用来处理平台公共算法处理的算量和价的逻辑，单独提出来是因为采购入库单
   * 这部分逻辑已经在其他的处理类处理过了，不需要处理该逻辑
   * 
   * @param event
   */
  protected void processSingRowNumAndMny(CardBodyAfterEditEvent event) {
    int rownum = event.getRow();
    // 保存变更前辅数量
    this.setOldassnum((UFDouble) event.getOldValue());

    UIBusiCalculator calc = new UIBusiCalculator();
    // 是否计算金额
    boolean bCalcMny =
        ((GenUIProcessorInfo) this.getEditorModel().getContext()
            .getUiprocesorinfo()).isBCalcMny();
    if (bCalcMny) {
      // 计算数量及金额
      calc.calc(event.getBillCardPanel(), ICPubMetaNameConst.NASSISTNUM, rownum);
    }
    else {
      // 计算数量
      calc.calcNum(event.getBillCardPanel(), ICPubMetaNameConst.NASSISTNUM,
          rownum);
    }
  }

  private void handleMultiRow(CardBodyAfterEditEvent event) {
    this.processMultiRowsNumAndMny(event);
    
    List<Integer> rows = event.getAfterEditIndexList();
    
    this.calculateCostMny(rows);
    this.processAfterAstNum(rows);
    this.clearBodySNsInfo(rows);

  }
  
  /**
   * 该方法用来处理平台公共算法处理的算量和价的逻辑，单独提出来是因为采购入库单
   * 这部分逻辑已经在其他的处理类处理过了，不需要处理该逻辑
   * 
   * @param event
   */
  protected void processMultiRowsNumAndMny(CardBodyAfterEditEvent event){
    this.calculateAstNum(event.getAfterEditIndexList());
  }

  private void calculateAstNum(List<Integer> rows) {
    UIBusiCalculator calc = new UIBusiCalculator();
    // 是否计算金额
    boolean bCalcMny =
        ((GenUIProcessorInfo) this.getEditorModel().getContext()
            .getUiprocesorinfo()).isBCalcMny();
    if (bCalcMny) {
      // 计算数量及金额
      calc.calc(getEditorModel().getCardPanelWrapper().getBillCardPanel(),
          ICPubMetaNameConst.NASSISTNUM, getRowArray(rows));
    }
    else {
      // 计算数量
      calc.calcNums(getEditorModel().getCardPanelWrapper().getBillCardPanel(),
          ICPubMetaNameConst.NASSISTNUM, getRowArray(rows));
    }

  }

  private void calculateCostMny(List<Integer> rows) {
    PriceAndMoneyCalculator pcalc =
        new PriceAndMoneyCalculator(UIScaleUtils.getScaleUtils(),
            OrgInfoUIQuery.getInstance().getOrgInfoQuery());
    pcalc.calcPriceMny(
        getEditorModel().getCardPanelWrapper().getHeadRefID(
            ICPubMetaNameConst.PK_ORG), ((ICGenBizEditorModel) this
            .getEditorModel()).getICUIBillEntity().getBody(getRowArray(rows)),
        MnyCalcType.Num);

    pcalc.calcPlannedPriceMny(
        ((ICGenBizEditorModel) this.getEditorModel()).getICUIBillEntity(),
        MnyCalcType.Num, getRowArray(rows));
  }

  private void processAfterAstNum(List<Integer> rows) {
    for(Integer row : rows){
      // 数量编辑后处理
      ((GenUIProcessorInfo) this.getEditorModel().getContext()
          .getUiprocesorinfo()).getGenAfterNumEditProcessor().process(
              row.intValue(),
          this.getEditorModel());
    }
  }

  private int[] getRowArray(List<Integer> rows) {
    int[] ret = new int[rows.size()];
    for (int i = 0; i < rows.size(); i++) {
      ret[i] = rows.get(i).intValue();
    }
    return ret;
  }

}
