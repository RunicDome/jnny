package nc.ui.pu.m25.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.logging.Logger;
import nc.bs.framework.common.NCLocator;
import nc.itf.pubapp.pub.smart.IBillQueryService;
import nc.itf.uap.pf.busiflow.PfButtonClickContext;
import nc.ui.pcm.utils.GetDao;
import nc.ui.pu.pub.editor.CardEditorHelper;
import nc.ui.pu.pub.util.BusiDateSetter;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.pf.PfUtilClient;
import nc.ui.pubapp.billref.dest.TransferViewProcessor;
import nc.ui.pubapp.uif2app.actions.AbstractReferenceAction;
import nc.ui.pubapp.uif2app.funcnode.trantype.TrantypeFuncUtils;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.IBillCardPanelEditor;
import nc.vo.ct.purdaily.entity.AggCtPuVO;
import nc.vo.ct.purdaily.entity.CtPuVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pu.m25.entity.InvoiceHeaderVO;
import nc.vo.pu.m25.entity.InvoiceItemVO;
import nc.vo.pu.m25.entity.InvoiceVO;
import nc.vo.pu.pub.util.AggVOUtil;
import nc.vo.pu.pub.util.CirVOUtil;
import nc.vo.pu.pub.util.ListUtil;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.log.Log;
import nc.vo.pubapp.util.VORowNoUtils;
import nc.vo.scmpub.res.billtype.ICBillType;
import nc.vo.scmpub.res.billtype.POBillType;
import nc.vo.scmpub.res.billtype.SCBillType;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public abstract class InvoiceRefAddAction extends AbstractReferenceAction
{
  private static final long serialVersionUID = -7167526730230052116L;
  private IBillCardPanelEditor editor;
  private BillManageModel model;
  GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
  
  public void doAction(ActionEvent e)
    throws Exception
  {
    PfUtilClient.childButtonClickedNew(createPfButtonClickContext());
    
    if (PfUtilClient.isCloseOK()) {
      InvoiceVO[] retvos = (InvoiceVO[])(InvoiceVO[])PfUtilClient.getRetVos();
      if ((retvos == null) || (retvos.length <= 0)) {
        return;
      }

      BusiDateSetter busidateSetter = new BusiDateSetter();
      busidateSetter.setHeaderBusiDate(retvos, "dbilldate");

      busidateSetter.setHeaderBusiDate(retvos, "darrivedate");
      //XBX新增合同联查
      IBillQueryService billquery = NCLocator.getInstance().lookup(
				IBillQueryService.class);
      String ctcode = "";//采购合同编码
      int flag = 0;
      InvoiceVO[] nfpvo = new InvoiceVO[retvos.length];
      double ljfpje = 0.0;
      for(int n = 0;n < retvos.length;n++){
    	  InvoiceVO cgfpVOy = retvos[n];
    	  InvoiceItemVO[] mxVOy = cgfpVOy.getChildrenVO();
    	  if(mxVOy != null && mxVOy.length > 0){
    		  for(int m=0;m<mxVOy.length;m++){
    			  InvoiceItemVO imvo = mxVOy[m];
    			  if(imvo != null && imvo.getNorigtaxmny() != null){
    				  ljfpje += imvo.getNorigtaxmny().toDouble();
    			  }
    		  }
    	  }
      }
      
      for(int i = 0;i < retvos.length;i++){
    	  InvoiceVO cgfpVO = retvos[i];
    	  InvoiceVO ncgfpVO = cgfpVO;
    	  InvoiceHeaderVO fpHeadVO = cgfpVO.getParentVO();
    	  InvoiceHeaderVO nfpHeadVO = fpHeadVO;
    	  InvoiceItemVO[] mxVO = cgfpVO.getChildrenVO();
    	  if(mxVO != null && mxVO.length > 0){
    		  ctcode = mxVO[0].getVctcode();
    		  System.out.println("采购合同编码："+ctcode);
    		  System.out.println("供应商："+fpHeadVO.getPk_supplier());
    		  
    		  if(ctcode != null && !"".equals(ctcode) && !"null".equals(ctcode)){
    			  String ctsql = "SELECT PK_CT_PU FROM CT_PU WHERE VBILLCODE = '"+ctcode+"' AND CVENDORID = '"+fpHeadVO.getPk_supplier()+"' AND DR = 0 AND VERSION = '1'";
        	      Logger.error("查询合同SQL："+ctsql);
    			  List<Object[]> ctls = getDao.query(ctsql);
        	      if(ctls != null && ctls.size() > 0){
        	    	  String pk_primaryKey = ctls.get(0)[0]+"";
        	    	  Logger.error("pk_primaryKey=="+pk_primaryKey);
        	    	  AggCtPuVO aggCtPuVO = billquery.querySingleBillByPk(
        						AggCtPuVO.class, pk_primaryKey + "");
        	    	  CtPuVO ctVO = aggCtPuVO.getParentVO();
        	    	  nfpHeadVO.setVdef4(ctVO.getVbillcode());//合同编码
        	    	  nfpHeadVO.setVdef7(ctVO.getCtname());//合同名称
        	    	  nfpHeadVO.setVdef9(ctVO.getNtotalorigmny()+"");//加税合计
        	    	  double ykfpje = 0.0;
        	    	  String ykfpsql = "SELECT SUM(NVL(NORIGTAXMNY,0)) AS JSHJ FROM PO_INVOICE_B WHERE VCTCODE = '"+ctcode+"' AND PK_INVOICE <> '"+fpHeadVO.getPrimaryKey()+"'";
        	    	  List<Object[]> ykfpls = getDao.query(ykfpsql);
            	      if(ykfpls != null && ykfpls.size() > 0 && ykfpls.get(0) != null && ykfpls.get(0).length > 0 && ykfpls.get(0)[0] !=null){
            	    	  if(ykfpls.get(0)[0] != null){
            	    		  ykfpje = Double.parseDouble(ykfpls.get(0)[0]+"");
            	    	  }
            	      }
            	      nfpHeadVO.setVdef12(ykfpje+"");//已开发票金额
            	      nfpHeadVO.setVdef14((ykfpje+ljfpje)+"");//累计开发票金额
        	    	  ncgfpVO.setParentVO(nfpHeadVO);
        	    	  nfpvo[i] = ncgfpVO;
        	      }
    		  }else{
    			  flag = 1;
    			  Logger.error("合同编码为空！");
    		  }
    	  }
      }
      if(flag == 0 ){
    	  getTransferViewProcessor().processBillTransfer(nfpvo);
      }else{
    	  getTransferViewProcessor().processBillTransfer(retvos);
      }
    }
  }

  public IBillCardPanelEditor getEditor() {
    return this.editor;
  }

  public BillManageModel getModel()
  {
    return this.model;
  }

  public void setEditor(IBillCardPanelEditor editor) {
    this.editor = editor;
  }

  public void setModel(BillManageModel model)
  {
    this.model = model;
    model.addAppEventListener(this);
  }

  private void changeNotNullKey2OID(String[] notNullKey)
  {
    if (ArrayUtils.isEmpty(notNullKey)) {
      return;
    }
    for (int i = 0; i < notNullKey.length; ++i)
      if ((notNullKey[i] != null) && ("pk_org_v".equals(notNullKey[i]))) {
        notNullKey[i] = "pk_org";
        return;
      }
  }

  private boolean checkBusiType(String pk_busitype, String srcBilltype)
  {
    if (StringUtils.isEmpty(pk_busitype)) {
      return false;
    }
    String msg = "";
    if (POBillType.Order.getCode().equals(srcBilltype)) {
      msg = NCLangRes4VoTransl.getNCLangRes().getStrByID("4004050_0", "04004050-0118");
    }
    else if (ICBillType.PurchaseIn.getCode().equals(srcBilltype)) {
      msg = NCLangRes4VoTransl.getNCLangRes().getStrByID("4004050_0", "04004050-0119");
    }
    else if (ICBillType.SubContinIn.getCode().equals(srcBilltype)) {
      msg = NCLangRes4VoTransl.getNCLangRes().getStrByID("4004050_0", "04004050-0131");
    }
    else if (POBillType.InitEstimate.getCode().equals(srcBilltype)) {
      msg = NCLangRes4VoTransl.getNCLangRes().getStrByID("4004050_0", "04004050-0132");
    }
    else if (ICBillType.VmiSum.getCode().equals(srcBilltype)) {
      msg = NCLangRes4VoTransl.getNCLangRes().getStrByID("4004050_0", "04004050-0133");
    }
    else if (SCBillType.Order.getCode().equals(srcBilltype)) {
      msg = NCLangRes4VoTransl.getNCLangRes().getStrByID("4004050_0", "04004050-0130");
    }

    int rowCount = getEditor().getBillCardPanel().getRowCount();
    String sourcetypecode = null;
    for (int i = 0; i < rowCount; ++i) {
      Object bodyValueAt = getEditor().getBillCardPanel().getBodyValueAt(i, "csourcetypecode");

      sourcetypecode = (bodyValueAt == null) ? null : (String)bodyValueAt;
      if (null != sourcetypecode) {
        break;
      }
    }
    if ((null == sourcetypecode) || (sourcetypecode.equals(srcBilltype))) {
      return true;
    }
    ExceptionUtils.wrappBusinessException(msg);
    return false;
  }

  private PfButtonClickContext createAddRowPfButtonClickContext(List<String> busiType)
  {
    PfButtonClickContext context = new PfButtonClickContext();
    context.setParent(getModel().getContext().getEntranceUI());
    context.setSrcBillType(getSrcBillTypeCode());
    context.setPk_group(getModel().getContext().getPk_group());
    context.setUserId(getModel().getContext().getPk_loginUser());

    String vtrantype = TrantypeFuncUtils.getTrantype(getModel().getContext());

    if (StringUtil.isEmptyWithTrim(vtrantype)) {
      context.setCurrBilltype(POBillType.Invoice.getCode());
    }
    else {
      context.setCurrBilltype(vtrantype);
    }
    context.setUserObj(null);
    context.setSrcBillId(null);
    context.setBusiTypes(busiType);

    if ((!ListUtil.isEmpty(getTranstypes())) && (!getTranstypes().contains("")))
    {
      context.setTransTypes(getTranstypes());
    }

    context.setClassifyMode(2);
    return context;
  }

  private PfButtonClickContext createPfButtonClickContext() {
    PfButtonClickContext context = new PfButtonClickContext();
    context.setParent(getModel().getContext().getEntranceUI());
    context.setSrcBillType(getSourceBillType());
    context.setPk_group(getModel().getContext().getPk_group());
    context.setUserId(getModel().getContext().getPk_loginUser());

    String vtrantype = TrantypeFuncUtils.getTrantype(getModel().getContext());

    if (StringUtil.isEmptyWithTrim(vtrantype)) {
      context.setCurrBilltype(POBillType.Invoice.getCode());
    }
    else {
      context.setCurrBilltype(vtrantype);
    }
    context.setUserObj(null);
    context.setSrcBillId(null);
    context.setBusiTypes(getBusitypes());

    if ((!ListUtil.isEmpty(getTranstypes())) && (!getTranstypes().contains("")))
    {
      context.setTransTypes(getTranstypes());
    }

    context.setClassifyMode(2);
    return context;
  }

  protected void doRefAddRowAction()
    throws Exception
  {
    Object curVo = getEditor().getValue();
    if (curVo == null) {
      return;
    }
    InvoiceVO invoiceVO = (InvoiceVO)curVo;

    String pk_busitype = invoiceVO.getParentVO().getPk_busitype();
    if (StringUtils.isBlank(pk_busitype)) {
      ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("4004050_0", "04004050-0005"));
    }

    if (!checkBusiType(pk_busitype, getSrcBillTypeCode())) {
      return;
    }
    List busiType = new ArrayList();
    busiType.add(pk_busitype);
    PfUtilClient.childButtonClickedNew(createAddRowPfButtonClickContext(busiType));

    if (PfUtilClient.isCloseOK()) {
      InvoiceVO[] retvos = (InvoiceVO[])(InvoiceVO[])PfUtilClient.getRetVos();
      if (ArrayUtils.isEmpty(retvos)) {
        return;
      }

      CardEditorHelper helper = new CardEditorHelper(getEditor().getBillCardPanel());

      InvoiceVO curVO = (InvoiceVO)invoiceVO.clone();
      InvoiceHeaderVO curHeaderVO = curVO.getParentVO();

      String[] notNullKey = helper.getNotNullHeadItemKeys();

      changeNotNullKey2OID(notNullKey);
      for (String key : notNullKey) {
        Log.debug(" InvoiceRefAddAction:not null key =" + key);
        Log.debug(" \n");
      }

      boolean existDifferItem = CirVOUtil.existDifferNotNullItems(retvos, curHeaderVO, notNullKey, false);

      if (existDifferItem) {
        ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("4004050_0", "04004050-0006"));
      }

      InvoiceItemVO[] combinItemVOs = (InvoiceItemVO[])AggVOUtil.getCombinItemVOs(retvos, invoiceVO.getChildrenVO());

      curVO.setChildrenVO(combinItemVOs);
      VORowNoUtils.setVOsRowNoByRule(new InvoiceVO[] { curVO }, "crowno");

      getEditor().setValue(curVO);
    }
  }

  protected String getSrcBillTypeCode() {
    return null;
  }

  protected boolean isActionEnable()
  {
    return UIState.NOT_EDIT == getModel().getUiState();
  }

  protected boolean isBusiness()
  {
    return true;
  }
}