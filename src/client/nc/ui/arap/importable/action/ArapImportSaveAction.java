package nc.ui.arap.importable.action;

import java.awt.event.ActionEvent;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.pf.pub.PfDataCache;
import nc.bs.uap.bd.notetype.INotetypeConst;
import nc.itf.arap.fieldmap.IBillFieldGet;
import nc.itf.uap.pf.IplatFormEntry;
import nc.ui.arap.bill.ArapBillUIUtil;
import nc.ui.arap.importable.ArapBillImportForm;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.actions.SaveAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BillForm;
import nc.vo.arap.basebill.BaseAggVO;
import nc.vo.arap.basebill.BaseItemVO;
import nc.vo.arap.bill.util.BillEventHandlerUtil;
import nc.vo.arap.pub.BillEnumCollection;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pubapp.pflow.PfUserObject;
import nc.vo.uap.pf.PfProcessBatchRetObject;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("restriction")
public abstract class ArapImportSaveAction extends SaveAction{

	private static final long serialVersionUID = 1L;
	private PfUserObject userObj;
	
	protected abstract void beforeImportSave();
	
	protected abstract AggregatedValueObject save(AggregatedValueObject bill) throws BusinessException ;
	@Override
	public void doAction(ActionEvent e) throws Exception {
       List<AggregatedValueObject>  list =((ArapBillImportForm)getEditor()).getImpList();
       for(AggregatedValueObject abill :list){
    	   BaseAggVO   bill = (BaseAggVO)abill;
	   		validate(bill);
			BaseItemVO[] children = (BaseItemVO[]) bill.getChildrenVO();
			if (null == children || children.length == 0) {
				throw ExceptionHandler.createException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0","02006pub-0012")/*@res " 表格不能为空,保存失败 !"*/);
			}
			for (BaseItemVO item : children) {
				if (StringUtils.isNotEmpty(item.getChecktype())
						&& INotetypeConst.NOTETYPE_CLASS_BUSIPO != BillEventHandlerUtil
						.getNoteClassByPK(item.getChecktype())
						&& StringUtils.isEmpty(item.getCheckno())) {
					throw ExceptionHandler.createException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006arappub0523_0","02006arappub0523-0000")/*@res "票据类型空票票据必须录入空票号"*/);
				}
			}
			bill.getHeadVO().setBillstatus(BillEnumCollection.BillSatus.Save.VALUE);   
       }
		Object executePM =null;
		AggregatedValueObject[] aggVOS = list.toArray(new AggregatedValueObject[0] );
		if (this.getModel().getUiState() == UIState.ADD) {
			ArapBillUIUtil.setNewClientDateAndUser(aggVOS);			
			try {
//				 executePM = ArapBillUIUtil.executeBatchPM(ArapFlowUtil.getCommitActionCode(((BaseAggVO)aggVOS[0]).getHeadVO().getPk_org(), ((BaseAggVO)aggVOS[0]).getHeadVO().getPk_billtype())
//						 , getModel().getContext()
//						.getEntranceUI(), aggVOS, getUserObj());
				IplatFormEntry action = (IplatFormEntry)NCLocator.getInstance().lookup(IplatFormEntry.class.getName());
				PfUserObject[] userObjAry = new PfUserObject[aggVOS.length];
				for (int i = 0; i < aggVOS.length; i++) {
				  userObjAry[i] = userObj;
				}
				PfProcessBatchRetObject PfProcessBatchRetObject = (nc.vo.uap.pf.PfProcessBatchRetObject) action.processBatch("SAVEBASE", ((BaseAggVO)aggVOS[0]).getHeadVO().getPk_billtype(), null, aggVOS, userObjAry , null);
				executePM = PfProcessBatchRetObject.getRetObj();
							
			}catch (Exception exception) {
				ExceptionHandler.createAndThrowException(exception.getMessage());
			}
			//批量保存，按照返回的顺序，判断导入失败的行号，批量保存提示弹框提示
			int i=1;
			StringBuffer errRow =new StringBuffer();
			if(executePM !=null){
				for(Object o: (Object[]) executePM){
					if(o==null){
						errRow.append(i+"#");
					}
					i++;
				}	
			}
			if(errRow.length()>0){
				String err =errRow.toString().substring(0, errRow.length()-1);
				ExceptionHandler.createAndThrowException(err);
				return ;	
			}
		} 
		
		BillCardPanel billCardPanel = ((nc.ui.pubapp.uif2app.view.BillForm) getEditor()).getBillCardPanel();
		billCardPanel.setBillValueVO((AggregatedValueObject)((Object[]) executePM)[0]);
		this.getModel().setUiState(UIState.NOT_EDIT);
		this.getModel().getContext().setPk_org(null);
		showSuccessInfo();

		// 显示预算控制信息
//		ArapBillUIUtil.showTbbMessage(new AggregatedValueObject[] { aggVO }, this.getModel()
//			.getContext());
		
	
		this.getModel().setUiState(UIState.NOT_EDIT);
		ShowStatusBarMsgUtil.showStatusBarMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0","02006pub-0011",null,new String[]{getBtnName()})/*@res "操作成功"*/, getModel().getContext());
	}



	public PfUserObject getUserObj() {
		if (userObj == null) {
			userObj = new PfUserObject();
		}
		return userObj;
	}

	public void setUserObj(PfUserObject userObj) {
		this.userObj = userObj;
	}
	
	public String getBillTypeid(String tradetype) {
		String pkBilltypeid = "";
		if (PfDataCache.getBillType(tradetype) != null) {
			pkBilltypeid = PfDataCache.getBillType(tradetype).getPk_billtypeid();
		} else {
			BillCardPanel panel = ((BillForm)this.getEditor()).getBillCardPanel();
			String tradeTypeName = panel.getHeadItem(IBillFieldGet.PK_TRADETYPE).getName();
			throw new BusinessRuntimeException(tradeTypeName + NCLangRes4VoTransl.getNCLangRes().getStrByID("2006rec_0", "02006rec-0003"));
		}
		return pkBilltypeid;
	}
}
