package nc.bs.pu.m422x.pfxx;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pfxx.ISwapContext;
import nc.bs.pfxx.plugin.AbstractPfxxPlugin;
import nc.impl.pu.m422x.action.StoreReqAppInsertAction;
import nc.itf.uap.pf.IplatFormEntry;
import nc.vo.pfxx.auxiliary.AggxsysregisterVO;
import nc.vo.pfxx.util.PfxxPluginUtils;
import nc.vo.pu.m422x.entity.StoreReqAppHeaderVO;
import nc.vo.pu.m422x.entity.StoreReqAppVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

/**
 * @author XBX
 *	物资需求申请单XML导入功能
 */
@SuppressWarnings("restriction")
public class M422XPfxxPlugin extends AbstractPfxxPlugin {
	public M422XPfxxPlugin() {
	}
	
	@Override
	protected Object processBill(Object vo, ISwapContext swapContext,
			AggxsysregisterVO aggxsysvo) throws BusinessException {
		// TODO Auto-generated method stub
		if (vo == null) {
			return null;
		}

		StoreReqAppVO aggvo = null;

		if ((vo instanceof StoreReqAppVO)) {
			aggvo = (StoreReqAppVO) vo;
		}

		if (aggvo != null) {
			String vopk = PfxxPluginUtils.queryBillPKBeforeSaveOrUpdate(
					swapContext.getBilltype(), swapContext.getDocID());
			if (vopk != null) {
				ExceptionUtils.wrappBusinessException("暂不支持修改功能！");
			} else {
				Logger.info("如果此单据没有导入过,保存新单据...");
				String creator = ((StoreReqAppHeaderVO)aggvo.getParentVO()).getCreator();
				if(creator != null){
					InvocationInfoProxy.getInstance().setUserId(creator);// 设置默认操作员主键
				}else{
					InvocationInfoProxy.getInstance().setUserId("1001A110000000000HV8");// 设置默认操作员主键
				}
				aggvo.getParentVO().setStatus(VOStatus.NEW);
				StoreReqAppVO[] wzvo = new StoreReqAppInsertAction().insert(new StoreReqAppVO[]{(StoreReqAppVO) vo});

				StoreReqAppHeaderVO hvo = (StoreReqAppHeaderVO) wzvo[0].getParentVO();// 表头VO
				
				// 神思推送单据直接提交
				if("SS".equals(hvo.getVdef8())){
					IplatFormEntry iplatFormEntry = NCLocator.getInstance().lookup(
							IplatFormEntry.class);
					iplatFormEntry.processAction("SAVE", "422X", null, wzvo[0],
							null, null);
				}
				vopk = hvo.getPrimaryKey();// 单据主键
				PfxxPluginUtils.addDocIDVsPKContrast(swapContext.getBilltype(),
						swapContext.getDocID(), vopk);
			}
			return vopk;
		} else {
			return null;
		}
	}
}
