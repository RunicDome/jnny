/*     */ package nc.bs.ct.saledaily.insert.rule;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
/*     */ import nc.impl.pubapp.bill.rewrite.BillRewriter;
/*     */ import nc.impl.pubapp.bill.rewrite.ItemKeyMapping;
/*     */ import nc.impl.pubapp.bill.rewrite.RewritePara;
/*     */ import nc.impl.pubapp.pattern.rule.ICompareRule;
/*     */ import nc.pubitf.so.salequotation.ct.ISaleContractCallBack;
import nc.uif.pub.exception.UifException;
/*     */ import nc.vo.ct.saledaily.entity.AggCtSaleVO;
import nc.vo.ct.saledaily.entity.CtSaleBVO;
import nc.vo.pu.m422x.entity.StoreReqAppItemVO;
/*     */ import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDouble;
/*     */ import nc.vo.pubapp.pattern.exception.ExceptionUtils;
/*     */ import nc.vo.scmpub.res.billtype.SOBillType;
/*     */ import nc.vo.so.salequotation.entity.QuatationRewritePara;
/*     */ import nc.vo.so.salequotation.entity.SalequotationBVO;
/*     */ import nc.vo.so.salequotation.entity.SalequotationHVO;
/*     */ import org.apache.commons.lang.ArrayUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SaleWriteBackRule
/*     */   implements ICompareRule<AggCtSaleVO>
/*     */ {
/*     */   public SaleWriteBackRule() {}
/*     */   
/*     */   public void process(AggCtSaleVO[] vos, AggCtSaleVO[] originVOs)
/*     */   {
/*  35 */     try {
					writeback(vos, originVOs);
				} catch (BusinessException e) {
				}
/*     */   }
/*     */   
/*     */   private QuatationRewritePara[] getWriteBackParas(List<RewritePara> reParas) {
/*  39 */     List<QuatationRewritePara> writebacklist = new ArrayList();
/*     */     
/*  41 */     for (RewritePara para : reParas)
/*  42 */       if ((null != para.getCsrcid()) && (null != para.getCsrcbid()))
/*     */       {
/*     */ 
/*  45 */         QuatationRewritePara vo = new QuatationRewritePara();
/*  46 */         vo.setPk_salequobill(para.getCsrcid());
/*  47 */         vo.setPk_salequobill_b(para.getCsrcbid());
/*  48 */         vo.setNnum(para.getNnum());
/*  49 */         vo.setOperateFlag(Integer.valueOf(para.getStatus()));
/*  50 */         writebacklist.add(vo);
/*     */       }
/*  52 */     return (QuatationRewritePara[])writebacklist.toArray(new QuatationRewritePara[writebacklist.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private ItemKeyMapping setItemKeyMapping()
/*     */   {
/*  63 */     ItemKeyMapping mapping = new ItemKeyMapping();
/*  64 */     mapping.setVsrctypeKey("vsrctype");
/*     */     
/*  66 */     mapping.setCsrcidKey("csrcid");
/*     */     
/*  68 */     mapping.setCsrcbidKey("csrcbid");
/*  69 */     mapping.setSrcTSKey("sourcets");
/*  70 */     mapping.setSrcbTSKey("sourcebts");
/*  71 */     mapping.setNnumKey("nnum");
/*  72 */     return mapping;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void writeback(AggCtSaleVO[] vos, AggCtSaleVO[] originVOs) throws BusinessException
/*     */   {
/*  83 */     ItemKeyMapping mapping = setItemKeyMapping();
/*     */     
/*  85 */     BillRewriter tool = new BillRewriter(mapping);
/*  86 */     tool.addSRCHeadClazz(SOBillType.SaleQuotation.getCode(), SalequotationHVO.class);
/*     */     
/*  88 */     tool.addSRCItemClazz(SOBillType.SaleQuotation.getCode(), SalequotationBVO.class);
/*     */     
/*  90 */     Map<String, List<RewritePara>> rwParaMap = null;
/*  91 */     if (ArrayUtils.isEmpty(vos)) {
/*  92 */       rwParaMap = tool.splitForDelete(originVOs);
/*     */     }
/*  94 */     else if (ArrayUtils.isEmpty(originVOs)) {
/*  95 */       rwParaMap = tool.splitForInsert(vos);
/*     */     }
/*     */     else {
/*  98 */       rwParaMap = tool.splitForUpdate(vos, originVOs);
/*     */     }
/*     */     
		//TODO 判断来源单据类型是否为物质及服务申请单 start
		String billtypecode = "test";
		if(null != originVOs){
			if(null != originVOs[0].getCtSaleBVO()[0].getVsrctype()){
				billtypecode = (String) new HYPubBO().findColValue("bd_billtype", "parentbilltype", "nvl(dr,0) = 0 and pk_billtypeid ='" + originVOs[0].getCtSaleBVO()[0].getVsrctype() + "'");
			}
		}
		if(null != vos){
			if(null != vos[0].getCtSaleBVO()[0].getVsrctype()){
				billtypecode = (String) new HYPubBO().findColValue("bd_billtype", "parentbilltype", "nvl(dr,0) = 0 and pk_billtypeid ='" + vos[0].getCtSaleBVO()[0].getVsrctype() + "'");
			}
		}
		if("422X".equals(billtypecode)){

		}else{


/* 101 */     for (Map.Entry<String, List<RewritePara>> entity : rwParaMap.entrySet()) {
/* 102 */       if (!((List)entity.getValue()).isEmpty())
/*     */       {
/*     */ 
/* 105 */         QuatationRewritePara[] quatationvos = getWriteBackParas((List)entity.getValue());
/*     */         
/* 107 */         if (!ArrayUtils.isEmpty(quatationvos))
/*     */         {
/*     */ 
/* 110 */           writeBackQuatation(quatationvos); }
/*     */       }
/*     */     }
/*     */   }
		}

private HYPubBO hyPubBO;

public HYPubBO getHyPubBO() {
	if (null == hyPubBO) {
		hyPubBO = new HYPubBO();
	}

	return hyPubBO;
}

public void setHyPubBO(HYPubBO hyPubBO) {
	this.hyPubBO = hyPubBO;
}

private void rewrite(AggCtSaleVO[] aggvo) throws BusinessException {
	for (AggCtSaleVO temp : aggvo) {
		CtSaleBVO[] bvos = temp.getCtSaleBVO();
		for (CtSaleBVO bvo : bvos) {
			if (null != bvo.getCsrcbid()) {

				StoreReqAppItemVO itemVO = (StoreReqAppItemVO) getHyPubBO()
						.queryByPrimaryKey(StoreReqAppItemVO.class,
								bvo.getCsrcbid());
				UFDouble nastnum = UFDouble.ZERO_DBL;
				CtSaleBVO[] oldVos = (CtSaleBVO[]) getHyPubBO()
						.queryByCondition(
								CtSaleBVO.class,
								"csrcbid='" + bvo.getCsrcbid()
										+ "' and nvl(dr,0) = 0");
				if (oldVos.length > 0) {
					for (CtSaleBVO vos : oldVos) {
						nastnum = nastnum.add(vos.getNastnum());
					}
				}

				UFDouble num = itemVO.getNastnum();
				if (num.compareTo(nastnum) < 0) {
					throw new BusinessException("参照物资需求单数量超出需求单数量");
				} else {
					if (null != itemVO) {
						itemVO.setVbdef20(nastnum.toString());
						itemVO.setStatus(VOStatus.UPDATED);
						hyPubBO.update(itemVO);
					}
				}
			}
		}
	}
}
//TODO 判断来源单据类型是否为物质及服务申请单  end
/*     */   
/*     */   private void writeBackQuatation(QuatationRewritePara[] quatationvos) {
/*     */     try {
/* 117 */       ISaleContractCallBack service = (ISaleContractCallBack)NCLocator.getInstance().lookup(ISaleContractCallBack.class);
/*     */       
/* 119 */       service.saleContractCallBack(quatationvos);
/*     */     }
/*     */     catch (BusinessException e) {
/* 122 */       ExceptionUtils.wrappException(e);
/*     */     }
/*     */   }
/*     */ }
