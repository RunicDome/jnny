package nc.impl.erm.matterapp;

import nc.bs.erm.matterapp.ErmMatterAppBO;
import nc.bs.framework.common.NCLocator;
import nc.data.param.CommonParam;
import nc.itf.arap.forthird.IArapForDGSWService;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.pub.BusinessException;
import net.sf.json.JSONObject;

// 费用申请单删除
@SuppressWarnings("restriction")
public class ErmMatterAppBillManageImpl implements
		nc.pubitf.erm.matterapp.IErmMatterAppBillManage {
	public ErmMatterAppBillManageImpl() {
	}

	public AggMatterAppVO insertVO(AggMatterAppVO vo) throws BusinessException {
		return new ErmMatterAppBO().insertVO(vo);
	}

	public AggMatterAppVO updateVO(AggMatterAppVO vo) throws BusinessException {
		return new ErmMatterAppBO().updateVO(vo);
	}

	public void deleteVOs(AggMatterAppVO[] vos) throws BusinessException {
		new ErmMatterAppBO().deleteVOs(vos);
		for (AggMatterAppVO vo : vos) { // 调用东港税务系统同步单据状态
			IArapForDGSWService util = (IArapForDGSWService) NCLocator.getInstance().lookup(
					IArapForDGSWService.class);
			MatterAppVO hvo = vo.getParentVO();
			JSONObject res = util.sendBillByNCBill(hvo.getPrimaryKey(),
					hvo.getPk_org(), CommonParam.DELETE, hvo.getPk_billtype());
			if (!"Y".equals(res.getString("success"))) {
				throw new BusinessException(res.getString("errinfo"));
			}
		}
	}

	public AggMatterAppVO tempSave(AggMatterAppVO vo) throws BusinessException {
		return new ErmMatterAppBO().tempSave(vo);
	}

	public AggMatterAppVO invalidBill(AggMatterAppVO vo)
			throws BusinessException {
		return new ErmMatterAppBO().invalidBill(vo);
	}

	public nc.vo.erm.matterapp.MatterAppVO updatePrintInfo(
			nc.vo.erm.matterapp.MatterAppVO vo) throws BusinessException {
		return new ErmMatterAppBO().updatePrintInfo(vo);
	}

	public AggMatterAppVO updateVOforWeb(AggMatterAppVO vo)
			throws BusinessException {
		return new ErmMatterAppBO().updateVOforWeb(vo);
	}
}
