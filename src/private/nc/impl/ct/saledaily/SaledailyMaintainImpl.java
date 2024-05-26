/*     */package nc.impl.ct.saledaily;

/*     */
/*     */import nc.bs.ct.saledaily.query.RecPlanVOQueryBP;
import nc.bs.trade.business.HYPubBO;
/*     */
import nc.impl.ct.saledaily.action.MakePaybillAction;
/*     */
import nc.impl.ct.saledaily.action.SaledailyDeleteAction;
/*     */
import nc.impl.ct.saledaily.action.SaledailyInsertAction;
/*     */
import nc.impl.ct.saledaily.action.SaledailyUpdateAction;
/*     */
import nc.impl.pubapp.pattern.data.bill.BillQuery;
/*     */
import nc.itf.ct.saledaily.ISaledailyMaintain;
/*     */
import nc.vo.ct.saledaily.entity.AggCtSaleVO;
import nc.vo.ct.saledaily.entity.CtSaleBVO;
/*     */
import nc.vo.ct.saledaily.entity.CtSaleVO;
/*     */
import nc.vo.ct.uitl.ValueUtil;
import nc.vo.pu.m422x.entity.StoreReqAppItemVO;
/*     */
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDouble;
/*     */
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

/*     */
/*     */public class SaledailyMaintainImpl implements ISaledailyMaintain
/*     */{
	/*     */public SaledailyMaintainImpl() {
	}

	/*     */
	/*     */public void deleteSaledaily(AggCtSaleVO[] bills)
			throws BusinessException
	/*     */{
		/*     */try
		/*     */{
			/* 24 */new SaledailyDeleteAction().delete(bills);
			rewrite(bills);
			/*     */}
		/*     */catch (Exception ex) {
			/* 27 */ExceptionUtils.marsh(ex);
			/*     */}
		/*     */}

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
	/*     */public nc.vo.pub.AggregatedValueObject[] makePaybill(
			AggCtSaleVO[] srcVos)
	/*     */throws BusinessException
	/*     */{
		/*     */try
		/*     */{
			/* 53 */return new MakePaybillAction().makePaybill(srcVos);
			/*     */}
		/*     */catch (Exception ex) {
			/* 56 */ExceptionUtils.marsh(ex);
		}
		/* 57 */return null;
		/*     */}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */public AggCtSaleVO[] queryCtApVoByIds(String[] ids)
	/*     */throws BusinessException
	/*     */{
		/* 68 */if (ValueUtil.isEmpty(ids)) {
			/* 69 */return null;
			/*     */}
		/* 71 */AggCtSaleVO[] bills = null;
		/*     */try {
			/* 73 */BillQuery<AggCtSaleVO> queryVO = new BillQuery(
					AggCtSaleVO.class);
			/*     */
			/* 75 */bills = (AggCtSaleVO[]) queryVO.query(ids);
			/*     */}
		/*     */catch (Exception e) {
			/* 78 */ExceptionUtils.marsh(e);
			/*     */}
		/*     */
		/* 81 */return bills;
		/*     */}

	/*     */
	/*     */
	/*     */
	/*     */public nc.vo.ct.saledaily.entity.RecvPlanVO[] queryRecPlanVO(
			String pk, String vbillcode)
	/*     */throws BusinessException
	/*     */{
		/*     */try
		/*     */{
			/* 91 */return new RecPlanVOQueryBP().queryRecPlanVO(pk, vbillcode);
			/*     */}
		/*     */catch (Exception e) {
			/* 94 */ExceptionUtils.marsh(e);
		}
		/* 95 */return null;
		/*     */}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */public AggCtSaleVO[] save(AggCtSaleVO[] bills,
			AggCtSaleVO[] originBills)
	/*     */throws BusinessException
	/*     */{
		AggCtSaleVO[] aggs = null;
		/*     */try
		/*     */{

			/* 108 */CtSaleVO ctSaleVO = bills[0].getParentVO();
			/* 109 */if (ctSaleVO.getStatus() == 1) {
				/* 110 */aggs = new SaledailyUpdateAction().update(bills,
						originBills);
				/*     */}else{
			aggs = new SaledailyInsertAction().insert(bills);}
			/*     */}
		/*     */catch (Exception ex) {
			/* 115 */ExceptionUtils.marsh(ex);
			/*     */}
		rewrite(aggs);
		/* 117 */return aggs;
		/*     */}

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
			if (null != temp.getCtSaleBVO()[0].getVsrctype()) {
				String billtypecode = (String) new HYPubBO().findColValue(
						"bd_billtype",
						"parentbilltype",
						"nvl(dr,0) = 0 and pk_billtypeid ='"
								+ temp.getCtSaleBVO()[0].getVsrctype() + "'");
				if ("422X".equals(billtypecode)) {
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
							if (nastnum.intValue() != 0 && num.compareTo(nastnum) < 0) {
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
		}
	}
}