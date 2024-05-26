package nc.ui.ic.general.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import nc.ui.ic.general.deal.GenAfterNumEditProcessor;
import nc.ui.ic.general.deal.GenUIProcessorInfo;
import nc.ui.ic.general.model.ICGenBizEditorModel;
import nc.ui.ic.material.query.InvInfoUIQuery;
import nc.ui.ic.org.query.OrgInfoUIQuery;
import nc.ui.ic.pub.handler.card.ICCardEditEventHandler;
import nc.ui.ic.pub.model.ICBizEditorModel;
import nc.ui.ic.pub.util.UIBusiCalculator;
import nc.ui.pubapp.pub.scale.UIScaleUtils;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
import nc.vo.ic.location.ICLocationVO;
import nc.vo.ic.material.define.InvBasVO;
import nc.vo.ic.material.define.InvMeasKey;
import nc.vo.ic.material.define.InvMeasVO;
import nc.vo.ic.pub.calc.PriceAndMoneyCalculator;
import nc.vo.ic.pub.calc.PriceAndMoneyCalculator.MnyCalcType;
import nc.vo.ic.pub.define.ICPubMetaNameConst;
import nc.vo.ic.pub.util.NCBaseTypeUtils;
import nc.vo.ic.pub.util.StringUtil;
import nc.vo.ic.pub.util.ValueCheckUtil;
import nc.vo.pub.lang.UFDouble;

/**
 * <p>
 * <b>本类主要完成以下功能：主数量编辑事件处理</b>
 * <ul>
 * <li>
 * </ul>
 * <p>
 * <p>
 * 
 * @version 6.0
 * @since 6.0
 * @author liuzy
 * @time 2010-3-26 上午10:03:47
 */
public class MainNumHandler extends ICCardEditEventHandler {

  @Override
  public void afterCardBodyEdit(CardBodyAfterEditEvent event) {

    if (event.getAfterEditEventState() == CardBodyAfterEditEvent.BATCHCOPYEND) {
      // Atl+shift多选会走到
      this.handleMultiRow(event);

    }
    else if (event.getAfterEditEventState() != CardBodyAfterEditEvent.BATCHCOPYBEGIN
        && event.getAfterEditEventState() != CardBodyAfterEditEvent.BATCHCOPING) {
      this.handleSingleRow(event, null);
    }

  }

  public void handleForAutoPick(CardBodyAfterEditEvent event, InvBasVO invbasevo) {
    this.handleSingleRow(event, invbasevo);
  }

  public boolean getFixFlag(int rownum) {
    return ((ICGenBizEditorModel) this.getEditorModel()).isFixFlag(rownum);
  }

  private void handleSingleRow(CardBodyAfterEditEvent event, InvBasVO invbasevo) {
	  int row = event.getRow();
	  UFDouble aaa =
		        (UFDouble) ((ICGenBizEditorModel) this.getEditorModel())
		            .getCardPanelWrapper().getBodyValueAt(row,
		                ICPubMetaNameConst.NNUM);
    
    this.processSingleRowNumAndMny(event);
    aaa =
	        (UFDouble) ((ICGenBizEditorModel) this.getEditorModel())
	            .getCardPanelWrapper().getBodyValueAt(row,
	                ICPubMetaNameConst.NNUM);
    
    int rownum = event.getRow();
    
    // 修改了数量会导致重算主数量，重算了主数量就要重新计算金额
    PriceAndMoneyCalculator pcalc =
        new PriceAndMoneyCalculator(UIScaleUtils.getScaleUtils(),
            OrgInfoUIQuery.getInstance().getOrgInfoQuery());
    pcalc.calcPriceMny(
        ((ICGenBizEditorModel) this.getEditorModel()).getICUIBillEntity(),
        MnyCalcType.Num, rownum);

    pcalc.calcPlannedPriceMny(
        ((ICGenBizEditorModel) this.getEditorModel()).getICUIBillEntity(),
        MnyCalcType.Num, rownum);
    
    GenAfterNumEditProcessor afterEditProcessor =
        ((GenUIProcessorInfo) this.getEditorModel().getContext()
            .getUiprocesorinfo()).getGenAfterNumEditProcessor();
    // 数量编辑后处理
    if (invbasevo == null) {
      // 非拣货时处理
      afterEditProcessor.process(rownum, this.getEditorModel());
    }
    else {
      // 拣货时处理，提高效率，上游一次查询所有物料信息，减少远程调用 -zhangyan3
      afterEditProcessor
          .processForAutoPick(rownum, getEditorModel(), invbasevo);
    }
    this.syncNumToSN(new Integer[] {
      rownum
    });
    this.clearBodySNInfo(rownum);
  }
  
  /**
   * 该方法用来处理平台公共算法处理的算量和价的逻辑，单独提出来是因为采购入库单
   * 这部分逻辑已经在其他的处理类处理过了，不需要处理该逻辑
   * 
   * @param event
   */
  protected void processSingleRowNumAndMny(CardBodyAfterEditEvent event){
    int rownum = event.getRow();
    boolean isFixFlag = this.getFixFlag(rownum);
    UFDouble bbb =
            (UFDouble) ((ICGenBizEditorModel) this.getEditorModel())
                .getCardPanelWrapper().getBodyValueAt(rownum,
                    ICPubMetaNameConst.NNUM);
    UFDouble mainnum =
        (UFDouble) ((ICGenBizEditorModel) this.getEditorModel())
            .getCardPanelWrapper().getBodyValueAt(rownum,
                ICPubMetaNameConst.NNUM);
    UFDouble assistnum =
        (UFDouble) ((ICGenBizEditorModel) this.getEditorModel())
            .getCardPanelWrapper().getBodyValueAt(rownum,
                ICPubMetaNameConst.NASSISTNUM);
    // 保存变更前辅数量
    this.setOldassnum(assistnum);

    // 浮动换算率，实收/发主辅数量一方录入0时的处理问题 N57处理规则如下
    // 1、数量录入0时，录入主数量（包含0），不根据主数量换算换算率（换算率不变）；
    if (!isFixFlag && mainnum != null
        && NCBaseTypeUtils.isEquals(assistnum, UFDouble.ZERO_DBL)) {
      this.getEditorModel().getContext().setRecalculate(false);
      return;
    }
    UIBusiCalculator calc = new UIBusiCalculator();
    // 是否计算金额
    boolean bCalcMny =
        ((GenUIProcessorInfo) this.getEditorModel().getContext()
            .getUiprocesorinfo()).isBCalcMny();
    bbb =
            (UFDouble) ((ICGenBizEditorModel) this.getEditorModel())
                .getCardPanelWrapper().getBodyValueAt(rownum,
                    ICPubMetaNameConst.NNUM);
    if (bCalcMny) {
      // 计算数量及金额
      calc.calc(event.getBillCardPanel(), ICPubMetaNameConst.NNUM, rownum);
    }
    else {
      // 计算数量
      calc.calcNum(event.getBillCardPanel(), ICPubMetaNameConst.NNUM, rownum);
    }
    bbb =
            (UFDouble) ((ICGenBizEditorModel) this.getEditorModel())
                .getCardPanelWrapper().getBodyValueAt(rownum,
                    ICPubMetaNameConst.NNUM);
    // 主数量变动后会导致换算率变化（非固定换算率），所以要计算一下应收发数量
    if (!isFixFlag && mainnum != null) {
      // 计算应收发数量
      calc.calcShouldNum(event.getBillCardPanel(),
          ICPubMetaNameConst.VCHANGERATE, rownum);
    }


  }

  private void handleMultiRow(CardBodyAfterEditEvent event) {
    List<Integer> rows = event.getAfterEditIndexList();
    if (rows == null) {
      return;
    }
    Map<Integer, Boolean> fixMap = this.getEditorModel().isFixFlags(rows);

    List<Integer> rowTocalNum = this.getRowToCalNum(rows, fixMap);

    this.processMultiRowsNumAndMny(rowTocalNum, fixMap);
    
    this.calculateCostMny(rowTocalNum);
    this.processAfterNumEdit(rowTocalNum);
    this.syncNumToSN(rows.toArray(new Integer[rows.size()]));
  }
  
  /**
   * 该方法用来处理平台公共算法处理的算量和价的逻辑，单独提出来是因为采购入库单
   * 这部分逻辑已经在其他的处理类处理过了，不需要处理该逻辑
   * 
   * @param event
   */
  protected void processMultiRowsNumAndMny(List<Integer> rowTocalNum,
      Map<Integer, Boolean> fixMap) {
    if (ValueCheckUtil.isNullORZeroLength(rowTocalNum)) {
      this.getEditorModel().getContext().setRecalculate(false);
      return;
    }
    this.calculateNum(rowTocalNum);
    List<Integer> rowToCalShould = this.getRowToCalShould(rowTocalNum, fixMap);
    this.calculaterShould(rowToCalShould);

  }

  /**
   * 同步物料行的主数量、数量到对应的单品信息中
   * 
   * @param rows
   */
  private void syncNumToSN(Integer[] rows) {
    for (Integer row : rows) {
      ICLocationVO[] locationVOs =
          this.getEditorModel().getBodyEditDetailData(row);
      // 若不是一个货位信息，则不是由物料自动带出，不处理
      if (locationVOs == null || locationVOs.length != 1) {
        continue;
      }
      ICLocationVO locationVO = locationVOs[0];
      // 若当前货位信息中主数量信息为空，则带表体行数量
      if (NCBaseTypeUtils.isNullOrZero(locationVO.getNnum())) {
        UFDouble mainnum =
            (UFDouble) ((ICGenBizEditorModel) this.getEditorModel())
                .getCardPanelWrapper().getBodyValueAt(row,
                    ICPubMetaNameConst.NNUM);
        UFDouble assistnum =
            (UFDouble) ((ICGenBizEditorModel) this.getEditorModel())
                .getCardPanelWrapper().getBodyValueAt(row,
                    ICPubMetaNameConst.NASSISTNUM);

        locationVO.setNnum(mainnum);
        locationVO.setNassistnum(assistnum);
      }
    }
  }

  private void processAfterNumEdit(List<Integer> rowTocalNum) {

    Map<InvMeasKey, InvMeasVO> measMap =
        this.getMeasVOMap(rowTocalNum.toArray(new Integer[rowTocalNum.size()]));

    for (Integer row : rowTocalNum) {
      // 数量编辑后处理
      ((GenUIProcessorInfo) this.getEditorModel().getContext()
          .getUiprocesorinfo()).getGenAfterNumEditProcessor().process(
          row.intValue(), this.getEditorModel(), measMap);
    }
  }

  private Map<InvMeasKey, InvMeasVO> getMeasVOMap(Integer[] rows) {
    if (null == rows || rows.length == 0) {
      return Collections.emptyMap();
    }

    List<String> marvids = new ArrayList<String>();
    List<String> meadocs = new ArrayList<String>();

    for (int i = 0; i < rows.length; i++) {

      String marvid =
          this.getEditorModel().getCardPanelWrapper()
              .getBodyValueAt_String(rows[i], ICPubMetaNameConst.CMATERIALVID);
      if (StringUtil.isSEmptyOrNull(marvid)) {
        continue;
      }

      String meadoc =
          this.getEditorModel().getCardPanelWrapper()
              .getBodyValueAt_String(rows[i], ICPubMetaNameConst.CASTUNITID);

      marvids.add(marvid);
      meadocs.add(meadoc);
    }

    if (ValueCheckUtil.isNullORZeroLength(marvids)) {
      return Collections.emptyMap();
    }

    return InvInfoUIQuery
        .getInstance()
        .getInvInfoQuery()
        .getInvMeasVOs(marvids.toArray(new String[0]),
            meadocs.toArray(new String[0]));

  }

  private void calculateNum(List<Integer> rowTocalNum) {
    UIBusiCalculator calc = new UIBusiCalculator();
    // 是否计算金额
    boolean bCalcMny =
        ((GenUIProcessorInfo) this.getEditorModel().getContext()
            .getUiprocesorinfo()).isBCalcMny();
    if (bCalcMny) {
      // 计算数量及金额
      calc.calc(this.getEditorModel().getCardPanelWrapper().getBillCardPanel(),
          ICPubMetaNameConst.NNUM, this.getRowArray(rowTocalNum));
    }
    else {
      // 计算数量
      calc.calcNums(this.getEditorModel().getCardPanelWrapper()
          .getBillCardPanel(), ICPubMetaNameConst.NNUM,
          this.getRowArray(rowTocalNum));
    }

  }

  private void calculaterShould(List<Integer> rowToCalShould) {
    UIBusiCalculator calc = new UIBusiCalculator();
    // 计算应收发数量
    calc.calcShouldNums(this.getEditorModel().getCardPanelWrapper()
        .getBillCardPanel(), ICPubMetaNameConst.VCHANGERATE,
        this.getRowArray(rowToCalShould));
  }

  private void calculateCostMny(List<Integer> rowTocalnum) {
    PriceAndMoneyCalculator pcalc =
        new PriceAndMoneyCalculator(UIScaleUtils.getScaleUtils(),
            OrgInfoUIQuery.getInstance().getOrgInfoQuery());
    pcalc.calcPriceMny(this.getEditorModel().getCardPanelWrapper()
        .getHeadRefID(ICPubMetaNameConst.PK_ORG),
        ((ICGenBizEditorModel) this.getEditorModel()).getICUIBillEntity()
            .getBody(this.getRowArray(rowTocalnum)), MnyCalcType.Num);

    pcalc.calcPlannedPriceMny(
        ((ICGenBizEditorModel) this.getEditorModel()).getICUIBillEntity(),
        MnyCalcType.Num, this.getRowArray(rowTocalnum));
  }

  private List<Integer> getRowToCalNum(List<Integer> rows,
      Map<Integer, Boolean> fixMap) {
    List<Integer> rowTocalNum = new ArrayList<Integer>();
    for (Integer rownum : rows) {
      if (!this.isNeedToCalNum(rownum.intValue(), fixMap.get(rownum)
          .booleanValue())) {
        continue;
      }
      rowTocalNum.add(rownum);
    }
    return rowTocalNum;
  }

  private List<Integer> getRowToCalShould(List<Integer> rowTocalNum,
      Map<Integer, Boolean> fixMap) {
    List<Integer> rowsToCalShould = new ArrayList<Integer>();
    for (Integer row : rowTocalNum) {
      if (!fixMap.get(row).booleanValue()) {
        rowsToCalShould.add(row);
      }
    }
    return rowsToCalShould;

  }

  private boolean isNeedToCalNum(int rownum, boolean isFixFlag) {
    UFDouble mainnum =
        (UFDouble) ((ICGenBizEditorModel) this.getEditorModel())
            .getCardPanelWrapper().getBodyValueAt(rownum,
                ICPubMetaNameConst.NNUM);
    UFDouble assistnum =
        (UFDouble) ((ICGenBizEditorModel) this.getEditorModel())
            .getCardPanelWrapper().getBodyValueAt(rownum,
                ICPubMetaNameConst.NASSISTNUM);
    // 浮动换算率，实收/发主辅数量一方录入0时的处理问题 N57处理规则如下
    // 1、数量录入0时，录入主数量（包含0），不根据主数量换算换算率（换算率不变）；
    if (!isFixFlag && mainnum != null
        && NCBaseTypeUtils.isEquals(assistnum, UFDouble.ZERO_DBL)) {
      return false;
    }
    return true;

  }

  private int[] getRowArray(List<Integer> rows) {
    int[] ret = new int[rows.size()];
    for (int i = 0; i < rows.size(); i++) {
      ret[i] = rows.get(i).intValue();
    }
    return ret;
  }

}
