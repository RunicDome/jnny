package nc.ui.arap.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nc.bs.arap.util.ArapBillType2TableMapping;
import nc.bs.arap.util.CheckException;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.fieldmap.IBillFieldGet;
import nc.itf.arap.payable.IArapPayableBillQueryService;
import nc.itf.cmp.IApplyService;
import nc.ui.arap.bill.ArapBillUIUtil;
import nc.ui.arap.model.ArapBillManageModel;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.pubapp.uif2app.view.BillListView;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.DeleteAction;
import nc.ui.uif2.components.CommonConfirmDialogUtils;
import nc.ui.uif2.editor.IEditor;
import nc.vo.arap.basebill.BaseAggVO;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.arap.basebill.BaseItemVO;
import nc.vo.arap.billstatus.ARAPBillStatus;
import nc.vo.arap.pub.BillEnumCollection;
import nc.vo.arap.pub.BillEnumCollection.ApproveStatus;
import nc.vo.arap.pub.BillEnumCollection.FromSystem;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.IAttributeMeta;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pubapp.pflow.PfUserObject;

import org.apache.commons.lang.ArrayUtils;

/**
 * 删除单据
 * 
 * @see
 * @author guodw
 * @version V6.0
 * @since V6.0 创建时间：2009-8-26 上午08:37:54
 */
public class BillDeleteAction extends DeleteAction implements IUserObjGetter {

	private static final long serialVersionUID = 1L;
	List<String> deltwicedlg = Arrays.asList(new String[] { "20080EBM", "20080PBM", "20080PBR", "20080EBR" });
	BillListView listView = null;

	public BillDeleteAction() {
		super();
	}

	@Override
	protected boolean isActionEnable() {
		boolean enable = true;
		if (null == model.getSelectedData()) {
			enable = false;
		}

		if ((model.getUiState() == UIState.NOT_EDIT || model.getUiState() == UIState.DISABLE) && null != model.getSelectedData()) {
//			Object[] tmps = ((ArapBillManageModel) model).getSelectedOperaDatas();
//			if(ArrayUtils.isEmpty(tmps)){
//				tmps = new Object[]{model.getSelectedData()};
//			}
			//modify by baoxina  ((ArapBillManageModel) model).getSelectedOperaDatas()这种取数方式不适用于 preLineAction，取的不是将要跳转到的数据
			Object[] tmps = new Object[]{model.getSelectedData()};
			if (null != tmps) {
				AggregatedValueObject[] aggs = new AggregatedValueObject[tmps.length];
				System.arraycopy(tmps, 0, aggs, 0, aggs.length);
				for (AggregatedValueObject tmp : aggs) {
					BaseBillVO parent = (BaseBillVO) tmp.getParentVO();
					if ((parent.getBillstatus().equals(ARAPBillStatus.AUDIT.VALUE) && parent.getIsinit().equals(UFBoolean.FALSE))) {
						enable = false;
					}
					if (!approveState.contains(parent.getApprovestatus())) {
						enable = false;
					}
					if (parent.getApprovestatus() == 3) {
						enable = false;
					}
					CircularlyAccessibleValueObject[] childrenVO = tmp.getChildrenVO();
					if (ArrayUtils.isEmpty(childrenVO)) {
						enable = false;
					}

					BaseItemVO itemVO = (BaseItemVO) childrenVO[0];
					boolean isforce = parent.getIsforce() != null && parent.getIsforce().booleanValue();
					boolean isfromindependent = parent.getIsfromindependent() != null && parent.getIsfromindependent().booleanValue();
					if (isforce && !isfromindependent && IBillFieldGet.FK.equals(parent.getBillclass())
							&& BillEnumCollection.CommissionPayType.ForceCommPayALL.VALUE.equals(itemVO.getCommpaytype())) {
						enable = false;
					} else if (isforce && isfromindependent && IBillFieldGet.FK.equals(parent.getBillclass())
							&& !BillEnumCollection.CommissionPayStatus.UnCommissionPay.VALUE.equals(itemVO.getCommpaystatus())) {
						enable = false;
					}
					UFBoolean sddreversalflag = (UFBoolean) parent.getAttributeValue(IBillFieldGet.SDDREVERSALFLAG);
					if (sddreversalflag != null && sddreversalflag.booleanValue()) {
						enable = false;
					}
					if (!enable) {
						return enable;
					}
				}
			}

		}
		if (getModel().getUiState() == UIState.ADD || getModel().getUiState() == UIState.EDIT) {
			enable = false;
		}
		return enable;
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		Object[] selectedOperaDatas = null;
		if (listView != null && listView.isShowing()) {
			selectedOperaDatas = ((BillManageModel) getModel()).getSelectedOperaDatas();
		} else {
			selectedOperaDatas = new Object[] { ((BillManageModel) getModel()).getSelectedData() };
		}
		CheckException.checkArgument(selectedOperaDatas == null, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0", "02006pub-0008")/* @res "请选择需要删除的单据" */);

		//提交态的单据，判断财务组织是否上共享，若上共享则不允许删除。
		NCLocator.getInstance().lookup(IArapPayableBillQueryService.class).checkCommitBillDelete(((BillManageModel) getModel()).getSelectedOperaDatas(),"删除");
		
		String funcode = this.getModel().getContext().getFuncInfo().getFuncode();
		
		if (deltwicedlg.contains(funcode)) {
			delDatas(selectedOperaDatas);
		} else {
			if (UIDialog.ID_YES == CommonConfirmDialogUtils.showConfirmDeleteDialog(model.getContext().getEntranceUI())) {
				delDatas(selectedOperaDatas);
			} else {
				ShowStatusBarMsgUtil.showStatusBarMsg(IShowMsgConstant.getCancelInfo(), getModel().getContext());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void delDatas(Object[] selectedOperaDatas) throws BusinessException, Exception {
		checkTopBilltype(Arrays.copyOf(selectedOperaDatas, selectedOperaDatas.length, BaseAggVO[].class));//来自付款申请轻量级的单据可以删除
		BaseAggVO[] bills = ArapBillUIUtil.convertObjetcsToAggs(selectedOperaDatas);
		this.checkOtherSystemBill(bills);
		ArapBillUIUtil.setDeleteClientDateAndUser(bills);
		ArapBillUIUtil.executeBatchPM(ArapBillUIUtil.DELETE, getModel().getContext().getEntranceUI(), bills);
		((ArapBillManageModel) getModel()).directlyDelete(bills);
		List<Object> data = ((ArapBillManageModel) getModel()).getData();
		if (data.size() == 0 && getEditor() != null) {
			BillCardPanel bcp = ((BillForm) getEditor()).getBillCardPanel();
			if(bcp == null){
				return;
			}
			Class<ISuperVO> vo = null;
			if (FromSystem.AR.VALUE.equals(bcp.getTailItem(IBillFieldGet.SRC_SYSCODE).getValueObject())) {
				vo = ArapBillType2TableMapping.getParentVOByBilltype(IBillFieldGet.F0);
			} else {
				vo = ArapBillType2TableMapping.getParentVOByBilltype(IBillFieldGet.F1);
			}

			if (vo != null) {
				String tableCode = (bcp.getBillData().getBodyTableCodes())[0];
				for (IAttributeMeta meta : vo.newInstance().getMetaData().getAttributes()) {
					bcp.setHeadItem(meta.getName(), null);
				}

				int[] array = new int[bcp.getRowCount()];
				for (int row = 0; row < bcp.getRowCount(); row++) {
					array[row] = row;
				}
				bcp.getBillModel(tableCode).delLine(array);

			}
		}

		ShowStatusBarMsgUtil.showStatusBarMsg(IShowMsgConstant.getDelSuccessInfo(), getModel().getContext());
	}

	static List<String> top_syscodes = Arrays.asList("3", "4", "16", "19", "7");// 销售3
																				// 采购4
																				// 内部交易16
																				// 库存19
																				// 合同20排程7

	static List<String> special_billtype = Arrays.asList("30", "35", "21", "Z2", "Z3", "Z4", "Z5", "Z1",
			"4B36", "4A24", "4A18", "4A04", "4A27", "4A49", "4A28", "4A25", "4A23", "4A05", "2201","36D1","5801","5811","5720");// 例外单据

	private void checkTopBilltype(BaseAggVO[] bills) throws BusinessException {
		//自付款申请轻量级的单据可以删除---碧桂园需求
		//wangyl7 2018年1月17日20:22:44 修改Critical问题  begin
		ArrayList<String> ids = new ArrayList<String>();
		for(AggregatedValueObject bill:bills){
			CircularlyAccessibleValueObject[] childrenVOs = bill.getChildrenVO();
			for(CircularlyAccessibleValueObject childrenVO:childrenVOs){
				Object top_billtype = childrenVO.getAttributeValue(IBillFieldGet.TOP_BILLTYPE);
				Object top_billId = childrenVO.getAttributeValue(IBillFieldGet.TOP_BILLID);
				if (null != top_billtype && top_billtype.equals("36D1") && null != top_billId){
					ids.add(top_billId.toString());
					
				}
			}
		}
		String[] top_billID = ids.toArray(new String[ids.size()]); 
		IApplyService applyService = NCLocator.getInstance().lookup(IApplyService.class);
		if(applyService.isHasOpsrctype(top_billID)){
			throw new BusinessException("来自付款申请轻量级的单据不可删除!");
		} 
		//end
		
		for (BaseAggVO bill : bills) {
			Object top_syscode = bill.getParentVO().getAttributeValue(IBillFieldGet.SRC_SYSCODE);
			Object top_billtype = bill.getChildrenVO()[0].getAttributeValue(IBillFieldGet.TOP_BILLTYPE);
			Object billtype = bill.getParentVO().getAttributeValue(IBillFieldGet.PK_BILLTYPE);

			if (null == top_billtype)
				continue;
			if (null != top_billtype && special_billtype.contains(top_billtype))
				continue;
			if (null != top_syscode) {
				CheckException.checkArgument(top_syscodes.contains(top_syscode.toString()),nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0","02006pub-0041")/*
														 * @res
														 * "来源于外系统的单据不允许直接删除!"
														 */);
			}
			if (IBillFieldGet.F0.equals(billtype) || IBillFieldGet.F1.equals(billtype)){
				String jckSyscode="113";//进出口来源系统 
				CheckException.checkArgument(jckSyscode.equals(top_syscode.toString()),nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0","02006pub-0041")/*
						 * @res
						 * "来源于外系统的单据不允许直接删除!"
						 */);
			}
			if ("36U5".equals(top_billtype) || "36UA".equals(top_billtype) || "5795".equals(top_billtype)){
				CheckException.checkArgument(true,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0","02006pub-0041")/*
						 * @res
						 * "来源于外系统的单据不允许直接删除!"
						 */);
			}

		}
	}

	private void checkOtherSystemBill(AggregatedValueObject[] bills) {
		try {
//			Integer syscode = ((BaseBillVO)bills[0].getParentVO()).getSyscode();
//			List<BillDeleteChecker> pluginChecks = ArapBusiPluginCenter.getAllBillDeleteCheckPlugins(syscode);
			
			for (AggregatedValueObject bill : bills) {
				// 先处理扩展校验
//				for (BillDeleteChecker billDeleteChecker : pluginChecks) {
//					if(billDeleteChecker.isMatch((BaseAggVO)bill)){
//						boolean candelete = billDeleteChecker.canDelete((BaseAggVO)bill);
//						if(!candelete){
//							throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0", "02006pub-0042")/* @res "来源于外系统的单据不能进行删除操作！" */);
//						}
//					}
//				}
				BaseBillVO billvo = (BaseBillVO) bill.getParentVO();
				Integer srcSyscode = billvo.getSrc_syscode();
				UFBoolean isflowbill = billvo.getIsflowbill();
				if (((BaseAggVO) bill).getIsOtherModuleOriginate().booleanValue()) {
					continue;
				}
				if (isflowbill.booleanValue()) {
					continue;
				}
				if (srcSyscode.intValue() == BillEnumCollection.FromSystem.AR.VALUE.intValue() || srcSyscode.intValue() == BillEnumCollection.FromSystem.AP.VALUE.intValue()
						|| srcSyscode.intValue() == BillEnumCollection.FromSystem.CMP.VALUE.intValue()
						|| srcSyscode.intValue() == BillEnumCollection.FromSystem.WBJHPT.VALUE.intValue()
						|| srcSyscode.intValue() == BillEnumCollection.FromSystem.XTDJ.VALUE.intValue()) {
					continue;
				}
				Object top_billtype = bill.getChildrenVO()[0].getAttributeValue(IBillFieldGet.TOP_BILLTYPE);

				if (null == top_billtype || special_billtype.contains(top_billtype)){
					continue;
				}
				if(top_billtype!=null){
					// XBX 清单发包合同推送的工程付款单，自由态可以删除
					if("FCT1".equals(top_billtype.toString().trim()) || "FCT2".equals(top_billtype.toString().trim())
							|| "4D42".equals(top_billtype.toString().trim()) ){
						continue;
					}
				}
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0", "02006pub-0042")/* @res "来源于外系统的单据不能进行删除操作！" */);
			}
		} catch (BusinessException e) {
			throw new BusinessRuntimeException(e.getMessage(), e);
		}
	}


	private PfUserObject userObj;

	private List<Integer> approveState = Arrays.asList(new Integer[] { ApproveStatus.NOSTATE.VALUE, ApproveStatus.COMMIT.VALUE });
	private IEditor editor;

	public BillListView getListView() {
		return listView;
	}

	public void setListView(BillListView listView) {
		this.listView = listView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nc.ui.arap.actions.IUserObjGetter#getUserObj()
	 */
	public PfUserObject getUserObj() {
		if (userObj == null) {
			userObj = new PfUserObject();
		}
		return userObj;
	}

	public void setUserObj(PfUserObject userObj) {
		this.userObj = userObj;
	}
	public IEditor getEditor() {
		return editor;
	}
	
	public void setEditor(IEditor editor) {
		this.editor = editor;
	}
	
}