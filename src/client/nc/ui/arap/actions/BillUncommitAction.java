/*     */ package nc.ui.arap.actions;
/*     */ 
/*     */ import java.awt.event.ActionEvent;

import nc.bs.arap.util.ArapFlowUtil;
import nc.ui.arap.actions.interceptor.CompositeActionInterceptor;
import nc.ui.arap.actions.interceptor.OperPowerActionInterceptor;
import nc.ui.arap.bill.ArapBillUIUtil;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.actions.ActionInterceptor;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.arap.basebill.BaseAggVO;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.arap.basebill.BaseItemVO;
import nc.vo.arap.billstatus.ARAPBillStatus;
import nc.vo.arap.pub.BillEnumCollection;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pmpub.common.utils.ArrayUtils;
import nc.vo.pub.AggregatedValueObject;

import org.apache.tools.ant.taskdefs.Java;
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
public class BillUncommitAction
/*     */   extends ProgressNCAction
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  32 */   private BillForm editor = null;
/*  33 */   private AbstractAppModel model = null;
/*     */   private OperPowerActionInterceptor interceptor;
/*     */   
/*     */   public BillUncommitAction()
/*     */   {
/*  38 */     ActionInitializer.initializeAction(this, "RECALL");
/*     */   }
/*     */   
/*     */   public void doAction(ActionEvent e) throws Exception {
/*  42 */     Object value = this.editor.getValue();
/*  43 */     if (null != value) {
/*  44 */       this.editor.getModel().setUiState(UIState.NOT_EDIT);
/*  45 */       BaseAggVO aggVO = (BaseAggVO)value;
/*  46 */       getOpPowerInterceptor().isUserHasPermissionForUI(aggVO, "UNCOMMIT");
/*  47 */       BaseBillVO headVO = aggVO.getHeadVO();
/*  48 */       AggregatedValueObject billvo = ArapBillUIUtil.executeBatchPM(ArapFlowUtil.getUnCommitActionCode(headVO.getPk_org(), headVO.getPk_billtype()), getModel().getContext().getEntranceUI(), aggVO);
/*     */       
/*  50 */       if (billvo != null) {
/*  51 */         getModel().directlyUpdate(billvo);
/*     */       }
/*     */     }
/*  54 */     ShowStatusBarMsgUtil.showStatusBarMsg(NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0", "02006pub-0687"), 
/*     */     
/*  56 */       getModel().getContext());
/*     */   }
/*     */   
/*     */   protected boolean isActionEnable()
/*     */   {
/*  61 */     if (((getModel().getUiState() == UIState.NOT_EDIT) || (getModel().getUiState() == UIState.DISABLE)) && (null != this.model.getSelectedData())) {
/*  62 */       AggregatedValueObject tmp = (AggregatedValueObject)this.model.getSelectedData();
/*  63 */       BaseBillVO parent = (BaseBillVO)tmp.getParentVO();
/*     */       
/*  65 */       if (ArrayUtils.isEmpty(tmp.getChildrenVO())) {
/*  66 */         return false;
/*     */       }
/*     */       
/*  69 */       if ((parent.getBillstatus().intValue() == ARAPBillStatus.TEMPSAVE.VALUE.intValue()) || (parent.getBillstatus().intValue() == ARAPBillStatus.UNCOMFIRM.VALUE.intValue())) {
/*  70 */         return false;
/*     */       }
/*  72 */       for (BaseItemVO item : (BaseItemVO[])tmp.getChildrenVO()) {
/*  73 */         if ((item.getPausetransact() != null) && (item.getPausetransact().booleanValue())) {
/*  74 */           return false;
/*     */         }
/*     */       }
/*  77 */       if (parent.getApprovestatus().intValue() == BillEnumCollection.ApproveStatus.COMMIT.VALUE.intValue()) {
/*  78 */         return true;
/*     */       }
/*     */     }
/*  81 */     return false;
/*     */   }
/*     */   
/*     */   public BillForm getEditor() {
/*  85 */     return this.editor;
/*     */   }
/*     */   
/*     */   public void setEditor(BillForm editor) {
/*  89 */     this.editor = editor;
/*     */   }
/*     */   
/*     */   public AbstractAppModel getModel() {
/*  93 */     return this.model;
/*     */   }
/*     */   
/*     */   public void setModel(AbstractAppModel model) {
/*  97 */     this.model = model;
/*  98 */     model.addAppEventListener(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private OperPowerActionInterceptor getOpPowerInterceptor()
/*     */   {
/* 107 */     if (this.interceptor != null) {
/* 108 */       return this.interceptor;
/*     */     }
/* 110 */     CompositeActionInterceptor interceptors = (CompositeActionInterceptor)getInterceptor();
/* 111 */     for (ActionInterceptor i : interceptors.getInterceptors()) {
/* 112 */       if ((i instanceof OperPowerActionInterceptor)) {
/* 113 */         this.interceptor = ((OperPowerActionInterceptor)i);
/*     */       }
/*     */     }
/* 116 */     if (this.interceptor != null) {
/* 117 */       return this.interceptor;
/*     */     }
/* 119 */     this.interceptor = new OperPowerActionInterceptor();
/* 120 */     return this.interceptor;
/*     */   }
/*     */ }