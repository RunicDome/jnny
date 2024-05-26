package nc.bs.pcm.plugin;

import java.util.HashMap;
import java.util.Map;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pfxx.ISwapContext;
import nc.bs.pfxx.plugin.AbstractPfxxPlugin;
import nc.itf.pcm.contract.pvt.IContract;
import nc.vo.pcm.contract.ContractBillVO;
import nc.vo.pfxx.auxiliary.AggxsysregisterVO;
import nc.vo.pfxx.util.PfxxPluginUtils;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

// 清单发包合同XML导入-XBX
public class ContractPfxxPlugin extends AbstractPfxxPlugin {
	private IContract service = null;

	@Override
	protected Object processBill(Object vo, ISwapContext swapContext,
			AggxsysregisterVO aggxsysvo) throws BusinessException {
		String pk = null;
		String bill_code = null;
		if (vo == null) {
			return null;
		}
		try {
			ContractBillVO aggVO = (ContractBillVO) vo;
			pk = PfxxPluginUtils.queryBillPKBeforeSaveOrUpdate(
					swapContext.getBilltype(), swapContext.getDocID());

			if (StringUtils.isBlank(pk)) {
				ContractBillVO[] insvos = getService().insertContr(
						new ContractBillVO[] { aggVO });
				bill_code = insvos[0].getParentVO().getBill_code();
				pk = insvos[0].getPrimaryKey();
				Map<String, String> res = new HashMap<>();
				res.put("pk", pk);
				res.put("billCode", bill_code);
				return res;
			} else {
				throw new BusinessException("暂不支持修改功能！");
			}
		} catch (Exception ex) {
			Logger.error(ex.getMessage(), ex.getCause());
			throw new BusinessException(ex.getMessage(), ex.getCause());
		}
	}

	private IContract getService() {
		if (this.service == null) {
			this.service = ((IContract) NCLocator.getInstance().lookup(
					IContract.class));
		}
		return this.service;
	}
}
