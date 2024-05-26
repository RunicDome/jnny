package nc.vo.ic.pub.calc;

import java.util.ArrayList;
import java.util.List;

import nc.vo.ic.pub.define.ICPubMetaNameConst;
import nc.vo.ic.pub.util.CollectionUtils;
import nc.vo.ic.pub.util.ValueCheckUtil;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pubapp.calculator.data.IDataSetForCal;
import nc.vo.pubapp.calculator.data.IRelationForItems;
import nc.vo.pubapp.calculator.data.VODataSetForCal;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;

/**
 * <p>
 * <b>计算数据创建器</b>
 * <p>
 * 
 * @version v60
 * @since v60
 * @author yangb
 * @time 2010-7-11 下午09:42:22
 */
public class DataSetForCalCreator {

  // 报价原币含税净价
  // TODO 遗留问题，后续调整元模型时统一修改
  public static final String NQTORIGTAXNETPRICE = "nqtorigtaxnetprice";

  protected IRelationForItems iRelationForItems;

  // 应收数量的计算关系
  protected IRelationForItems iShouldNumItems;

  /**
   * 
   */
  public IDataSetForCal[] getIDataSetForCal(AbstractBill[] bills) {
    if (ValueCheckUtil.isNullORZeroLength(bills)) {
      return null;
    }
    List<IDataSetForCal> ldatas = new ArrayList<IDataSetForCal>();
    SuperVO head = null;
    SuperVO[] bodys = null;
    for (AbstractBill bill : bills) {
      head = (SuperVO) bill.getParent();
      bodys = (SuperVO[]) bill.getChildrenVO();
      if (ValueCheckUtil.isNullORZeroLength(bodys)) {
        continue;
      }
      for (SuperVO body : bodys) {
        ldatas.add(new ICBillDataSetForCal(head, body, this
            .getRelationForItems()));
      }
    }
    if (ldatas.size() <= 0) {
      return null;
    }
    return CollectionUtils.listToArray(ldatas);
  }
  
  /**
   * 
   */
  public IDataSetForCal getIDataSetForCal(CircularlyAccessibleValueObject vo) {
    if (vo == null) {
      return null;
    }
    return new VODataSetForCal(vo, this.getRelationForItems());
  }
  
  /**
   * 
   */
  public IDataSetForCal[] getIDataSetForCal(
      CircularlyAccessibleValueObject[] vos) {
    if (ValueCheckUtil.isNullORZeroLength(vos)) {
      return null;
    }
    IDataSetForCal[] datas = new IDataSetForCal[vos.length];
    for (int i = 0; i < vos.length; i++) {
      datas[i] = this.getIDataSetForCal(vos[i]);
    }
    return datas;
  }  
  
  /**
   * 
   */
  public IDataSetForCal getIDataSetForMnyCal(CircularlyAccessibleValueObject vo) {
    if (vo == null) {
      return null;
    }
    return new ICVODataSetForMnyCal(vo, this.getRelationForItems());
  }
  
  /**
   * 
   */
  public IDataSetForCal[] getIDataSetForMnyCal(
      CircularlyAccessibleValueObject[] vos) {
    if (ValueCheckUtil.isNullORZeroLength(vos)) {
      return null;
    }
    IDataSetForCal[] datas = new IDataSetForCal[vos.length];
    for (int i = 0; i < vos.length; i++) {
      datas[i] = this.getIDataSetForMnyCal(vos[i]);
    }
    return datas;
  }
  
  public IDataSetForCal[] getIDataSetForMnyCal(AbstractBill[] bills) {
    if (ValueCheckUtil.isNullORZeroLength(bills)) {
      return null;
    }
    List<IDataSetForCal> ldatas = new ArrayList<IDataSetForCal>();
    SuperVO head = null;
    SuperVO[] bodys = null;
    for (AbstractBill bill : bills) {
      head = (SuperVO) bill.getParent();
      bodys = (SuperVO[]) bill.getChildrenVO();
      if (ValueCheckUtil.isNullORZeroLength(bodys)) {
        continue;
      }
      for (SuperVO body : bodys) {
        ldatas.add(new ICBillDataSetForMnyCal(head, body, this
            .getRelationForItems()));
      }
    }
    if (ldatas.size() <= 0) {
      return null;
    }
    return CollectionUtils.listToArray(ldatas);
  }

  public IDataSetForCal[] getIDataSetForOnlyMnyCal(AbstractBill[] bills) {
	    if (ValueCheckUtil.isNullORZeroLength(bills)) {
	      return null;
	    }
	    List<IDataSetForCal> ldatas = new ArrayList<IDataSetForCal>();
	    SuperVO head = null;
	    SuperVO[] bodys = null;
	    for (AbstractBill bill : bills) {
	      head = (SuperVO) bill.getParent();
	      bodys = (SuperVO[]) bill.getChildrenVO();
	      if (ValueCheckUtil.isNullORZeroLength(bodys)) {
	        continue;
	      }
	      for (SuperVO body : bodys) {
	        ldatas.add(new ICBillDataSetForOnlyMnyCal(head, body, this
	            .getRelationForItems()));
	      }
	    }
	    if (ldatas.size() <= 0) {
	      return null;
	    }
	    return CollectionUtils.listToArray(ldatas);
	  }
  /**
   * @return iRelationForItems
   */
  public IRelationForItems getRelationForItems() {
    if (this.iRelationForItems == null) {
      this.iRelationForItems = new ICRelationItemForCal();
      this.iRelationForItems
          .setNqtorigtaxnetprcKey(DataSetForCalCreator.NQTORIGTAXNETPRICE);
    }
    return this.iRelationForItems;
  }

  /**
   * 
   */
  public IDataSetForCal getShouldNumIDataSetForCal(
      CircularlyAccessibleValueObject vo) {
    if (vo == null) {
      return null;
    }
    return new VODataSetForCal(vo, this.getShouldNumItems());
  }

  /**
   * 
   */
  public IDataSetForCal[] getShouldNumIDataSetForCal(
      CircularlyAccessibleValueObject[] vos) {
    if (ValueCheckUtil.isNullORZeroLength(vos)) {
      return null;
    }
    IDataSetForCal[] datas = new IDataSetForCal[vos.length];
    for (int i = 0; i < vos.length; i++) {
      datas[i] = this.getShouldNumIDataSetForCal(vos[i]);
    }
    return datas;
  }

  /**
   * @return iRelationForItems
   */
  public IRelationForItems getShouldNumItems() {
    if (this.iShouldNumItems == null) {
      this.iShouldNumItems = new ICRelationItemForCal();
      this.iShouldNumItems
          .setNassistnumKey(ICPubMetaNameConst.NSHOULDASSISTNUM);
      this.iShouldNumItems.setnumKey(ICPubMetaNameConst.NSHOULDNUM);

      // 应发数量变化后，不需要计算报价单位、报价数量、报价换算率
      this.iShouldNumItems.setCqtunitidKey("unknow");
      this.iShouldNumItems.setNqtunitnumKey("unknow");
      this.iShouldNumItems.setNqtunitrateKey("unknow");
    }
    return this.iShouldNumItems;
  }

  /**
   * @param relationForItems
   *          要设置的 iRelationForItems
   */
  public void setRelationForItems(ICRelationItemForCal relationForItems) {
    this.iRelationForItems = relationForItems;
  }

}
