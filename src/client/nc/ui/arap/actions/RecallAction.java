package nc.ui.arap.actions;

import java.awt.event.ActionEvent;

import javax.swing.JComponent;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.validation.IValidationService;
import nc.funcnode.ui.AbstractFunclet;
import nc.itf.uap.busibean.ISysInitQry;
import nc.itf.uap.pf.IplatFormEntry;
import nc.ui.arap.bill.ArapBillUIUtil;
import nc.ui.arap.view.ArapBillCardForm;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.IEditor;
import nc.ui.uif2.model.AbstractAppModel;
import nc.uif2.annoations.MethodType;
import nc.uif2.annoations.ModelMethod;
import nc.uif2.annoations.ModelType;
import nc.uif2.annoations.ViewMethod;
import nc.uif2.annoations.ViewType;
import nc.vo.arap.basebill.BaseAggVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.para.SysInitVO;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.pubapp.pflow.PfUserObject;
import nc.vo.pubapp.util.NCPfServiceUtils;

public class RecallAction extends NCAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PfUserObject userObj;
	private String refNode;
	private AbstractAppModel model;
	private IEditor editor;
	
	public RecallAction(){
		super.setCode("RecallAction");
		super.setBtnName("收回");
	}
	
    @ModelMethod(modelType=ModelType.AbstractAppModel, methodType=MethodType.SETTER)
    public void setModel(AbstractAppModel model) {
      this.model = model;
      model.addAppEventListener(this);
    }
    
    
	@Override
	public void doAction(ActionEvent paramActionEvent) throws Exception {
		BaseAggVO billvo = (BaseAggVO) this.getEditor().getValue();
		
//		Object[] returnvos  = (Object[]) NCPfServiceUtils.processBatch(
//				ArapBillUIUtil.RECALL, billvo.getHeadVO()
//				.getPk_tradetype(), new BaseAggVO[] { billvo },
//				null, new WorkflownoteVO());
		IplatFormEntry action = (IplatFormEntry)NCLocator.getInstance().lookup(IplatFormEntry.class.getName());
		Object returnvos  = action.processAction("RECALL", billvo.getHeadVO().getPk_billtype(), null, billvo, userObj, null);
		BaseAggVO[] aggVOs = (BaseAggVO[]) returnvos;
		
		refreshUI(aggVOs[0], aggVOs[0]);

	} 
	private void refreshUI(BaseAggVO bill, AggregatedValueObject aggVO) {
//		if (this.getModel().getUiState() == UIState.EDIT) {
			getModel().directlyUpdate(aggVO);
//		} else if (this.getModel().getUiState() == UIState.ADD) {
//			getModel().directlyAdd(aggVO);
//		}

		this.getModel().setUiState(UIState.NOT_EDIT);
		JComponent entranceUI = this.getModel().getContext().getEntranceUI();
		ArapBillCardForm.resetMenuAction(entranceUI);

		// @see BillOrgPanel.fireChangedEvent
		// this.getModel().getContext().setPk_org(newPkOrg);
		// 需要在单据保存或者取消的时候将context中的pk_org设置为空或者默认组织

//		WorkbenchEnvironment.getInstance().putClientCache(
//				IBillFieldGet.ARAP_DEFAULT_ORG
//						+ aggVO.getParentVO().getAttributeValue(
//								IBillFieldGet.PK_BILLTYPE),
//				aggVO.getParentVO().getAttributeValue(IBillFieldGet.PK_ORG));
		this.getModel().getContext().setPk_org(null);
		((AbstractFunclet) this.getModel().getContext().getEntranceUI())
				.fireFuncletLinkEvent(bill, 0);
		ShowStatusBarMsgUtil.showStatusBarMsg("收回成功", getModel().getContext());

		// 显示预算控制信息
		// 2015-7-30注销，因为在CreditCheckDecoratorAction.showSuccessInfo()会覆盖下面提示的信息
		// by chenshuaia
		// ArapBillUIUtil.showTbbMessage(new AggregatedValueObject[] { aggVO },
		// this.getModel().getContext());
	}
    
    @Override
	protected boolean isActionEnable() {
		BaseAggVO bill = (BaseAggVO) this.getEditor().getValue();
		Integer approvestatus = (Integer) bill.getParentVO().getAttributeValue("approvestatus");
		
		return (model.getUiState() == UIState.NOT_EDIT) && approvestatus==3;
	}




	@ViewMethod(viewType=ViewType.IEditor, methodType=MethodType.SETTER)
    public void setEditor(IEditor editor) {
      this.editor = editor;
    }
    
    @ModelMethod(modelType=ModelType.AbstractAppModel, methodType=MethodType.GETTER)
    public AbstractAppModel getModel() {
      return model;
    }
    
    @ViewMethod(viewType=ViewType.IEditor, methodType=MethodType.GETTER)
    public IEditor getEditor() {
      return editor;
    }

}
