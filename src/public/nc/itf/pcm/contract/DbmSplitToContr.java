package nc.itf.pcm.contract;

import java.io.IOException;

import nc.vo.pm.feebalance.AggPmFeebalance;
import nc.vo.pm.rlcontractalter.AggRLContractalterHVO;
import nc.vo.pub.BusinessException;

// 多编码合同拆清单发包合同接口itf
public interface DbmSplitToContr {
	public String SplitToContr(final AggPmFeebalance aggPmFeebalance)
			throws BusinessException, IOException;

	public String SplitAlterToContr(
			final AggRLContractalterHVO aggRLContractalterHVO)
			throws BusinessException, IOException;
}