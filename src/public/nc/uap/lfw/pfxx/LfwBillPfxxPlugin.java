package nc.uap.lfw.pfxx;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.pfxx.ISwapContext;
import nc.bs.pfxx.plugin.AbstractPfxxPlugin;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.uap.lfw.crud.itf.ILfwCudService;
import nc.vo.pfxx.auxiliary.AggxsysregisterVO;
import nc.vo.pfxx.util.PfxxPluginUtils;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.pub.PubAppTool;

@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
public class LfwBillPfxxPlugin extends AbstractPfxxPlugin {
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

	public List<IRule<AggregatedValueObject>> getCheckers() {
		List<IRule<AggregatedValueObject>> rules = new ArrayList();

		return null;
	}
	protected Object processBill(Object vo, ISwapContext swapContext,
			AggxsysregisterVO aggxsysvo) throws BusinessException {
		AggregatedValueObject resvo = (AggregatedValueObject) vo;
		String vopk = PfxxPluginUtils.queryBillPKBeforeSaveOrUpdate(
				swapContext.getBilltype(), swapContext.getDocID());

		AggregatedValueObject returnVO = null;

		checkBill(resvo);
		
		if (PubAppTool.isNull(vopk)) {
			resvo.getParentVO().setStatus(2);
			returnVO = insert(resvo);
			
		} /*else {

			returnVO = update(resvo, vopk);
		}*/
		PfxxPluginUtils.addDocIDVsPKContrast(swapContext.getBilltype(),
				swapContext.getDocID(), vopk);

		return vopk;
	}
	private void checkBill(AggregatedValueObject vo) {
		if (getCheckers() != null) {
			for (IRule<AggregatedValueObject> checker : getCheckers()) {
				checker.process(new AggregatedValueObject[] { vo });
			}
		}
	}
	protected AggregatedValueObject insert(AggregatedValueObject vo) {
		try {
			NCLocator.getInstance().lookup(ILfwCudService.class)
					.saveAggVos(new AggregatedValueObject[] { vo });

		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return vo;
	}

	/*protected AggregatedValueObject update(AggregatedValueObject vo, String vopk) {
		try {
			NCLocator.getInstance().lookup(ILfwCudService.class)
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}*/
}
