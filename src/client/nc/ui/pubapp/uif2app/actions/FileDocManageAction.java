package nc.ui.pubapp.uif2app.actions;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Map;

import nc.bs.uif2.BusinessExceptionAdapter;
import nc.bs.uif2.IActionCode;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.md.MDBaseQueryFacade;
import nc.md.data.access.NCObject;
import nc.md.model.IBean;
import nc.md.model.IBusinessEntity;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.filesystem.FileManageUI;
import nc.ui.pub.filesystem.FileManageUIFactory;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.bd.meta.IBDObject;
import nc.vo.bd.meta.NCObject2BDObjectAdapter;
import nc.vo.ct.purdaily.entity.AggCtPuVO;
import nc.vo.ct.saledaily.entity.AggCtSaleVO;
import nc.vo.fct.ap.entity.AggCtApVO;
import nc.vo.fct.ar.entity.AggCtArVO;
import nc.vo.pcm.contract.ContractBillVO;
import nc.vo.pu.m21.entity.OrderVO;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;

public class FileDocManageAction extends NCAction {

  /**
   *
   */
  private static final long serialVersionUID = 4674049033047277460L;

  private AbstractUIAppModel model;

  public FileDocManageAction() {
    super();
    ActionInitializer.initializeAction(this, IActionCode.FILE);
  }

  public IBDObject createBDObject(Object obj) {
    if (obj == null) {
      return null;
    }

    if (obj instanceof String) {
      return null;
    }

    // 元数据
    try {
      String className = null;
      if (obj instanceof AbstractBill) {
        className = ((AbstractBill) obj).getParentVO().getClass().getName();
      }
      else {
        className = obj.getClass().getName();
      }
      // TODO: 递归搜索父类所对应的元数据，以支持继承
      IBean bean =
          MDBaseQueryFacade.getInstance().getBeanByFullClassName(className);
      if (bean == null) {
        return null;
      }
      Map<String, String> name_path_map =
          ((IBusinessEntity) bean).getBizInterfaceMapInfo(IBDObject.class
              .getName());
      return new NCObject2BDObjectAdapter(NCObject.newInstance(obj),
          name_path_map);
    }
    catch (nc.md.model.MetaDataException e) {
      throw new BusinessExceptionAdapter(e);
    }
  }

  @Override
  public void doAction(ActionEvent e) throws Exception {
    // TODO Auto-generated method stub
    Object selectedData = this.model.getSelectedData();
    IBDObject target = this.createBDObject(selectedData);
    if (target != null) {
      String rootPath = (String) target.getId();
      String userCode =
          WorkbenchEnvironment.getInstance().getLoginUser().getUser_code();

      String funcode = this.model.getContext().getNodeCode();
      String pk_group = this.model.getContext().getPk_group();
      FileManageUI ui =
          FileManageUIFactory.getFileManagePnl(rootPath, userCode, funcode,
              pk_group);
      //采购、销售合同提交后 附件不能修改--------begin
      Integer fbillstatus = 0;
      Integer forderstatus = 0 ;
      Integer qdstatus = 99;
      Integer sfkstatus = 99;
      if(selectedData instanceof AggCtSaleVO){
    	  AggCtSaleVO vo = (AggCtSaleVO) selectedData;
    	  fbillstatus = vo.getParentVO().getFstatusflag();
      }else if(selectedData instanceof AggCtPuVO){
    	  AggCtPuVO vo = (AggCtPuVO) selectedData;
    	  fbillstatus = vo.getParentVO().getFstatusflag();
      }else if(selectedData instanceof OrderVO){
    	  OrderVO vo = (OrderVO) selectedData;
          //获取采购订单单据状态
    	  forderstatus = vo.getHVO().getForderstatus();
          //单据状态为审批通过态不能进行删除上传附件
      }else if(selectedData instanceof ContractBillVO){
    	  ContractBillVO vo = (ContractBillVO) selectedData;
          //获取采购订单单据状态
    	  qdstatus = vo.getParentVO().getBill_status();
          //单据状态为审批通过态不能进行删除上传附件
      }else if(selectedData instanceof AggCtApVO){
    	  AggCtApVO vo = (AggCtApVO) selectedData;
    	  sfkstatus = vo.getParentVO().getFstatusflag();
      }else if(selectedData instanceof AggCtArVO){
    	  AggCtArVO vo = (AggCtArVO) selectedData;
    	  sfkstatus = vo.getParentVO().getFstatusflag();
      }
	  //生效、正在审批、审批通过
	  if(fbillstatus == 1 || fbillstatus == 2 || fbillstatus == 3 || forderstatus == 3 || 
			  (qdstatus != -1 && qdstatus != 99) || sfkstatus == 1 || sfkstatus == 3){
		  ui.setUploadFileEnable(false);
	      ui.setDeleteNodeEnable(false);
	  }
      //采购、销售合同提交后 附件不能修改--------begin
      ui.setTreeRootVisible(false);
      UIDialog dlg =
          new UIDialog(this.model.getContext().getEntranceUI(), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("pubapp_0","0pubapp-0129")/*@res "附件管理"*/);
      dlg.getContentPane().setLayout(new BorderLayout());
      dlg.getContentPane().add(ui, BorderLayout.CENTER);
      dlg.setResizable(true);
      dlg.setSize(810, 668);
      dlg.showModal();
    }
  }

  public AbstractUIAppModel getModel() {
    return this.model;
  }

  public void setModel(AbstractUIAppModel model) {
    this.model = model;
    this.model.addAppEventListener(this);
  }

  @Override
  protected boolean isActionEnable() {
	  boolean ret = this.model.getSelectedData() != null 
	  	&& this.model.getUiState() !=  UIState.EDIT
	  	&& this.model.getUiState() !=  UIState.ADD;
    return ret;
  }

}