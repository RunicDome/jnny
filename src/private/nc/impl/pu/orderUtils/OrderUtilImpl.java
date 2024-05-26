package nc.impl.pu.orderUtils;

import nc.bs.trade.business.HYPubBO;
import nc.impl.pu.m422x.action.StoreReqAppInsertAction;
import nc.itf.pu.orderUtils.OrderUtil;
import nc.vo.pu.m20.entity.PraybillVO;
import nc.vo.pu.m422x.entity.StoreReqAppVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.ws.intf.Result;

import com.alibaba.fastjson.JSONException;

// 请购单生成物资需求申请单
@SuppressWarnings("restriction")
public class OrderUtilImpl implements OrderUtil {
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

	// 请购单生成物资需求申请单
	@Override
	public String orderToStoreReqApp(PraybillVO praybillVO) throws BusinessException {
		// TODO Auto-generated method stub
		String obj = null;
		try {
			// 请购单
			StoreReqAppVO aggVO = (StoreReqAppVO) nc.bs.pub.pf.PfUtilTools
					.runChangeData("20", "422X", praybillVO);
			aggVO.getParentVO().setStatus(VOStatus.NEW);
			StoreReqAppVO[] wzvo = new StoreReqAppInsertAction().insert(new StoreReqAppVO[]{(StoreReqAppVO) aggVO});
		} catch (JSONException e) {
			obj = Result.error(e.getMessage());
		} catch (BusinessException e) {
			obj = Result.error(e.getMessage());
		} catch (Exception e) {
			obj = Result.error(e.getMessage());
		}
		return obj;
	}

}
