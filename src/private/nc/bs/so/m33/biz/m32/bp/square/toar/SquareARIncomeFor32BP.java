package nc.bs.so.m33.biz.m32.bp.square.toar;

import nc.bs.so.m33.biz.m32.rule.toar.GetNewARorgVidFor32Rule;
import nc.bs.so.m33.biz.m32.rule.toar.SquareARCloseFor32Rule;
import nc.bs.so.m33.biz.m32.rule.toar.ToARCheckFor32Rule;
import nc.bs.so.m33.maintain.m32.InsertSquare32DetailBP;
import nc.bs.so.m33.maintain.m32.rule.detail.RewriteARIncomeFor32Rule;
import nc.bs.so.m33.plugin.BPPlugInPoint;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.arap.receivable.AggReceivableBillVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pubapp.pattern.pub.MapList;
import nc.vo.scmpub.res.billtype.SOBillType;
import nc.vo.so.m33.m32.entity.SquareInvDetailVO;
import nc.vo.so.m33.m32.entity.SquareInvVO;
import nc.vo.so.m33.m32.entity.SquareInvVOUtils;
import nc.vo.so.m33.pub.exchange.ExchangeBillUtils;
import nc.vo.so.m33.pub.util.ARBillUtil;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class SquareARIncomeFor32BP {
	public SquareARIncomeFor32BP() {
	}

	public void square(SquareInvVO[] sqvos) {
		if ((sqvos == null) || (sqvos.length == 0)) {
			return;
		}
		AroundProcesser<SquareInvVO> processer = new AroundProcesser(
				BPPlugInPoint.SquareARIncome);

		addBeforeRule(processer);

		addAfterRule(processer);

		SquareInvDetailVO[] bills = SquareInvVOUtils.getInstance()
				.changeSQVOtoSQDVOForAR(sqvos);

		processer.before(sqvos);

		saveDetail(sqvos, bills);

		toAR(sqvos);

		processer.after(sqvos);
	}

	private void addAfterRule(AroundProcesser<SquareInvVO> processer) {
		IRule<SquareInvVO> rule = new SquareARCloseFor32Rule();
		processer.addAfterRule(rule);
	}

	private void addBeforeRule(AroundProcesser<SquareInvVO> processer) {
		IRule<SquareInvVO> rule = new GetNewARorgVidFor32Rule();
		processer.addBeforeRule(rule);

		rule = new ToARCheckFor32Rule();
		processer.addBeforeRule(rule);
	}

	private void saveDetail(SquareInvVO[] sqvos, SquareInvDetailVO[] bills) {
		AroundProcesser<SquareInvDetailVO> processer = new AroundProcesser(
				BPPlugInPoint.SquareARIncomeDetail);

		new InsertSquare32DetailBP().insert(sqvos, bills);

		IRule<SquareInvDetailVO> rule = new RewriteARIncomeFor32Rule();
		processer.addAfterRule(rule);
		processer.after(bills);
	}

	private void toAR(SquareInvVO[] sqvos) {
		String srcBillType = SOBillType.Invoice.getCode();
		String destBillType = "F0";
		String squareBillType = SOBillType.SquareInvoice.getCode();
		AggReceivableBillVO[] arapvos = (AggReceivableBillVO[]) new ExchangeBillUtils(
				SquareInvVO.class).exchangeBill(sqvos, squareBillType,
				srcBillType, destBillType);

		MapList<String, AggReceivableBillVO> arapvoMapList = ARBillUtil
				.getInstance().splitArapVO(arapvos);

		for (String pk_org : arapvoMapList.keySet()) {
			// 原
			/*nc.itf.scmpub.reference.uap.pf.PfServiceScmUtil.processBatch(
					nc.bs.arap.util.ArapFlowUtil.getCommitActionCode(pk_org,
							destBillType), destBillType,
					(AggregatedValueObject[]) nc.vo.so.pub.util.ListUtil
							.toArray(arapvoMapList.get(pk_org)), null, null);*/
			nc.itf.scmpub.reference.uap.pf.PfServiceScmUtil.processBatch("SAVE", destBillType,
					(AggregatedValueObject[]) nc.vo.so.pub.util.ListUtil
							.toArray(arapvoMapList.get(pk_org)), null, null);
		}
	}
}
