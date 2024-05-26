package nc.bs.pcm.plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pfxx.ISwapContext;
import nc.bs.pfxx.plugin.AbstractPfxxPlugin;
import nc.bs.trade.business.HYPubBO;
import nc.itf.pcm.marketcontract.pvt.IMarketContract;
import nc.itf.uap.pf.IWorkflowMachine;
import nc.itf.uap.pf.IplatFormEntry;
import nc.vo.org.OrgVO;
import nc.vo.pcm.marketcontract.ContractBillVO;
import nc.vo.pcm.marketcontract.ContractHeadVO;
import nc.vo.pfxx.auxiliary.AggxsysregisterVO;
import nc.vo.pfxx.util.PfxxPluginUtils;
import nc.vo.pm.proxy.PMProxy;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import org.json.JSONException;

/**
 * @author XBX 项目销售合同XML导入功能
 */
@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class McontrPfxxPlugin extends AbstractPfxxPlugin {
	public McontrPfxxPlugin() {
	}

	@Override
	protected Object processBill(Object vo, ISwapContext swapContext,
			AggxsysregisterVO aggxsysvo) throws BusinessException {
		// TODO Auto-generated method stub
		if (vo == null) {
			return null;
		}

		ContractBillVO aggvo = null;

		if ((vo instanceof ContractBillVO)) {
			aggvo = (ContractBillVO) vo;
		}
		if (aggvo != null) {
			String vopk = PfxxPluginUtils.queryBillPKBeforeSaveOrUpdate(
					swapContext.getBilltype(), swapContext.getDocID());
			String vbillcode = "";
			if (vopk != null) {
				ExceptionUtils.wrappBusinessException("暂不支持修改功能！");
			} else {
				Logger.info("如果此单据没有导入过,保存新单据...");

				// aggvo.getParentVO().setPk_transitype("0001A21000000000PG0O");// 交易类型写死
				// aggvo.getParentVO().setStatus(VOStatus.NEW);
				aggvo.getParentVO().setStatus(VOStatus.NEW);
				IMarketContract contrService = (IMarketContract) PMProxy
						.lookup(IMarketContract.class);
				// XBX 设置为制单人
				InvocationInfoProxy.getInstance().setGroupId("0001A1100000000001QS");
				String billmaker = (String) aggvo.getParentVO()
						.getAttributeValue("billmaker");
				InvocationInfoProxy.getInstance().setUserId(billmaker);
				ContractBillVO[] xmxshtvo = contrService
						.insertContr(new ContractBillVO[] { aggvo });

				ContractHeadVO hvo = (ContractHeadVO) xmxshtvo[0].getParentVO();// 表头VO

				vopk = hvo.getPrimaryKey();// 单据主键
				vbillcode = hvo.getBill_code(); // 单据编码
				PfxxPluginUtils.addDocIDVsPKContrast(swapContext.getBilltype(),
						swapContext.getDocID(), vopk);
				// 热力的话提交（会自动审批）
				OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(
						OrgVO.class, hvo.getPk_org());
				if (orgVO.getPk_org().equals("0001A110000000000HYQ")
						|| orgVO.getPk_fatherorg().equals(
								"0001A110000000000HYQ")) {
					HashMap<String, String> paramMap = new HashMap();
					paramMap.put("silently", "silently");
					WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
							.getInstance().lookup(IWorkflowMachine.class))
							.checkWorkFlow("SAVE", "4D60",
									aggvo, paramMap);
					AggregatedValueObject vos = ((AggregatedValueObject[]) getIplatFormEntry()
							.processAction("SAVE", "4D60", worknoteVO, aggvo,
									null, null))[0];
					vopk = vos.getParentVO().getPrimaryKey();				
					HashMap hmPfExParams = new HashMap();
					WorkflownoteVO worknotenVO = ((IWorkflowMachine) NCLocator
							.getInstance().lookup(IWorkflowMachine.class))
							.checkWorkFlow("APPROVE", "4D60", vos, hmPfExParams);
					if (worknotenVO != null) {
						worknotenVO.setChecknote("批准");
						worknotenVO.setApproveresult("Y");
					}
					HashMap<String, Object> eParam = new HashMap<String, Object>();
					eParam.put("notechecked", "notechecked");
					try {
						getIplatFormEntry().processAction("APPROVE", "4D60",
								worknotenVO, vos, null, eParam);
					} catch (BusinessException e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			}
			return vopk;
		} else {
			return null;
		}
	}

	private IplatFormEntry getIplatFormEntry() {
		return NCLocator.getInstance().lookup(IplatFormEntry.class);
	}
}
