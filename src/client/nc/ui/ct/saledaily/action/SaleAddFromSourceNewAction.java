/*     */ package nc.ui.ct.saledaily.action;
/*     */ 
/*     */ import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import nc.bs.trade.business.HYPubBO;
import nc.itf.uap.pf.busiflow.PfButtonClickContext;
import nc.ui.ct.model.CTModel;
import nc.ui.ct.util.CardEditorHelper;
import nc.ui.ct.util.SalePayTermUtil;
import nc.ui.ct.view.CtUIState;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.pf.PfUtilClient;
import nc.ui.pubapp.uif2app.actions.AbstractReferenceAction;
import nc.ui.pubapp.uif2app.funcnode.trantype.TrantypeFuncUtils;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.ct.business.entity.BusinessSetVO;
import nc.vo.ct.saledaily.entity.AggCtSaleVO;
import nc.vo.ct.saledaily.entity.CtSaleBVO;
import nc.vo.ct.util.CtTransBusitypes;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.SalesOrgVO;
import nc.vo.org.StockOrgVO;
import nc.vo.pf.change.ChangeVOAdjustContext;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.scmpub.res.billtype.CTBillType;

import org.apache.commons.lang.ArrayUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SaleAddFromSourceNewAction
/*     */   extends AbstractReferenceAction
/*     */ {
/*     */   private static final long serialVersionUID = -1749494587109459620L;
/*     */   private ShowUpableBillForm editor;
/*     */   private AbstractAppModel model;
/*     */   
/*     */   public void SaleAddFromSourceAction()
/*     */   {
	setSourceBillName("物资服务申请单");
	setBtnName("物资服务申请单");
/*  48 */     putValue("AcceleratorKey", KeyStroke.getKeyStroke(75, 8));
/*     */     
/*  50 */     putValue("ShortDescription", getBtnName());
/*     */   }
/*     */   
/*     */   public void doAction(ActionEvent e)
/*     */     throws Exception
/*     */   {
/*  56 */     PfUtilClient.childButtonClickedNew(createPfButtonClickContext());
/*  57 */     if (PfUtilClient.isCloseOK()) {
/*  58 */       AggCtSaleVO[] vos = (AggCtSaleVO[])PfUtilClient.getRetVos();
/*  59 */       if (ArrayUtils.isEmpty(vos)) {
/*  60 */         return;
/*     */       }
/*     */       
/*  63 */       CTModel ctModel = (CTModel)getModel();
/*  64 */       ctModel.setCtUIState(CtUIState.CTADD);
/*     */       
/*  66 */       getTransferViewProcessor().processBillTransfer(vos);
/*     */       
/*  68 */       BillCardPanel cardPanel = getTransferViewProcessor().getBillForm().getBillCardPanel();
/*     */       
/*  70 */       setPayTermTabVisible(cardPanel);
/*     */     }
/*     */   }
/*     */   
/*     */   public ShowUpableBillForm getEditor()
/*     */   {
/*  76 */     return this.editor;
/*     */   }
/*     */   
/*     */   public AbstractAppModel getModel() {
/*  80 */     return this.model;
/*     */   }
/*     */   
/*     */   public void setEditor(ShowUpableBillForm editor) {
/*  84 */     this.editor = editor;
/*     */   }
/*     */   
/*     */   public void setModel(AbstractAppModel model) {
/*  88 */     this.model = model;
/*     */   }
/*     */   
/*     */   private PfButtonClickContext createPfButtonClickContext() {
/*  92 */     PfButtonClickContext context = new PfButtonClickContext();
/*  93 */     context.setParent(getModel().getContext().getEntranceUI());
/*  94 */     context.setSrcBillType(getSourceBillType());
/*  95 */     context.setPk_group(getModel().getContext().getPk_group());
/*  96 */     context.setUserId(getModel().getContext().getPk_loginUser());
/*     */     
/*  98 */     String vtrantype = TrantypeFuncUtils.getTrantype(getModel().getContext());
/*     */     
/* 100 */     if (StringUtil.isEmptyWithTrim(vtrantype)) {
/* 101 */       context.setCurrBilltype(CTBillType.SaleDaily.getCode());
/*     */     }
/*     */     else {
/* 104 */       context.setCurrBilltype(vtrantype);
/*     */     }
/* 106 */     context.setUserObj(null);
/* 107 */     context.setSrcBillId(null);
/* 108 */     context.setBusiTypes(getBusitypes());
/*     */     
/*     */ 
/* 111 */     context.setTransTypes(getTranstypes());
/*     */     
/*     */ 
/* 114 */     context.setClassifyMode(1);
/* 115 */     return context;
/*     */   }
/*     */   
/*     */   private void setPayTermTabVisible(BillCardPanel cardPanel)
/*     */   {
/* 120 */     CardEditorHelper util = CardEditorHelper.getInstance(cardPanel);
/* 121 */     String ctrantypeid = util.getHeadStringValue("ctrantypeid");
/* 122 */     if (ctrantypeid == null) {
/* 123 */       cardPanel.setTabEnabled(1, "pk_ct_sale_payterm", false);
/*     */     }
/*     */     else
/*     */     {
/* 127 */       BusinessSetVO businessVO = CtTransBusitypes.getBusinessSetVO(ctrantypeid);
/* 128 */       SalePayTermUtil.setTabVisible(businessVO, cardPanel);
/*     */     }
/*     */   }
/*    */   public AggregatedValueObject[] batchAdjustAfterChange(AggregatedValueObject[] srcVOs, AggregatedValueObject[] destVOs, ChangeVOAdjustContext adjustContext)
/*    */     throws BusinessException
/*    */   {
				for(AggregatedValueObject temp:destVOs){
					HYPubBO hyPubBo = new HYPubBO();
					AggCtSaleVO aggVO=(AggCtSaleVO) temp;
					String pk_stock_org = aggVO.getParentVO().getPk_org();
					StockOrgVO stockOrgVO = (StockOrgVO) hyPubBo.queryByPrimaryKey(StockOrgVO.class, pk_stock_org);
					SalesOrgVO[]  salesOrgVOs = (SalesOrgVO[]) hyPubBo.queryByCondition(StockOrgVO.class, "nvl(dr,0) = 0 and pk_org = '" + stockOrgVO.getPk_org() + "'");
					String pk_org = salesOrgVOs[0].getPk_salesorg();
					String pk_org_v = salesOrgVOs[0].getPk_vid();
					aggVO.getParentVO().setPk_org(pk_org);
				
					CtSaleBVO[] bos=aggVO.getCtSaleBVO();
					 for(CtSaleBVO vos :bos){
						String pk_stock_org1 =vos.getPk_org();
						StockOrgVO stockOrgBVO = (StockOrgVO) hyPubBo.queryByPrimaryKey(StockOrgVO.class, pk_stock_org1);
						SalesOrgVO[]  salesOrgBVOs = (SalesOrgVO[]) hyPubBo.queryByCondition(StockOrgVO.class, "nvl(dr,0) = 0 and pk_org = '" + stockOrgBVO.getPk_org() + "'");
						String pk_org1 = salesOrgBVOs[0].getPk_salesorg();
						String pk_org_v1 = salesOrgBVOs[0].getPk_vid();
						aggVO.getParentVO().setPk_org(pk_org1);
					}
				}
	return destVOs;

/*    */   }
/*    */   

/*     */ }

/* Location:          
 * Qualified Name:     nc.ui.ct.saledaily.action.SaleAddFromSourceAction
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.0.1
 */