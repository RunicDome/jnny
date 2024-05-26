package nc.impl.ic.m45;

import nc.bs.ic.m45.genequipcard.PurchaseInGenerateEquipCard;
import nc.bs.ic.m45.util.Bill45FixedAssetProcess;
import nc.bs.trade.business.HYPubBO;
import nc.impl.ic.m45.action.CancelSignAction;
import nc.impl.ic.m45.action.DeleteAction;
import nc.impl.ic.m45.action.InsertAction;
import nc.impl.ic.m45.action.SignAction;
import nc.impl.ic.m45.action.UpdateAction;
import nc.itf.ic.m45.self.IPurchaseInMaintain;
import nc.uif.pub.exception.UifException;
import nc.vo.ic.m45.entity.PurchaseInBodyVO;
import nc.vo.ic.m45.entity.PurchaseInVO;
import nc.vo.pim.project.ProjectHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

// 采购入库接口
@SuppressWarnings({ "unused" })
public class PurchaseInMaintainImpl implements IPurchaseInMaintain {
	private HYPubBO hyPubBO;

	public HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	public PurchaseInMaintainImpl() {
	}

	public PurchaseInVO[] cancelGenerateEquipCard(PurchaseInVO[] billVOs)
			throws BusinessException {
		try {
			PurchaseInGenerateEquipCard tool = new PurchaseInGenerateEquipCard();
			return (PurchaseInVO[]) tool.cancelGenerateEquipCard(billVOs);
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
		}
		return null;
	}

	public PurchaseInVO[] cancelSign(PurchaseInVO[] billVOs)
			throws BusinessException {
		PurchaseInVO[] resultVOs = null;
		try {
			CancelSignAction action = new CancelSignAction();
			return action.cancelSign(billVOs);
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
		}
		return null;
	}

	public void delete(PurchaseInVO[] billVOs) throws BusinessException {
		try {
			DeleteAction action = new DeleteAction();
			action.delete(billVOs);
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
		}
	}

	public PurchaseInVO[] deleteFixedAsset(PurchaseInVO[] billVOs)
			throws BusinessException {
		try {
			return (PurchaseInVO[]) new Bill45FixedAssetProcess()
					.deleteFixedAsset(billVOs);
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
		}
		return new PurchaseInVO[0];
	}

	public PurchaseInVO[] generateEquipCard(PurchaseInVO[] billVOs)
			throws BusinessException {
		try {
			PurchaseInGenerateEquipCard tool = new PurchaseInGenerateEquipCard();
			return (PurchaseInVO[]) tool.generateEquipCard(billVOs);
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
		}
		return null;
	}

	public PurchaseInVO[] generateFixedAsset(PurchaseInVO[] billVOs)
			throws BusinessException {
		try {
			return (PurchaseInVO[]) new Bill45FixedAssetProcess()
					.generateFixedAsset(billVOs);
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
		}
		return new PurchaseInVO[0];
	}

	public PurchaseInVO[] insert(PurchaseInVO[] billVOs)
			throws BusinessException {
		PurchaseInVO[] resultVO = null;
		try {
			InsertAction action = new InsertAction();
			// XBX校验
			beforecheck(billVOs);
			return action.insert(billVOs);
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
		}
		return null;
	}

	// 保存前校验
	private void beforecheck(PurchaseInVO[] billVOs) throws BusinessException {
		// TODO Auto-generated method stub
		for (PurchaseInVO purchaseInVO : billVOs) {
			PurchaseInBodyVO[] mxvos = (PurchaseInBodyVO[]) purchaseInVO
					.getChildren(PurchaseInBodyVO.class);
			String pk_org = purchaseInVO.getHead().getPk_org();
			for (PurchaseInBodyVO purchaseInBodyVO : mxvos) {
				String pk_project = purchaseInBodyVO.getCprojectid();// 项目主键
				if (pk_project != null) {
					ProjectHeadVO xmvo = (ProjectHeadVO) getHyPubBO()
							.queryByPrimaryKey(ProjectHeadVO.class,
									pk_project + "");// 项目VO
					String project_code = xmvo.getProject_code();// 项目名称
					// 保存时检查该项目是否完成对账，已完成对账不允许保存。（是否完成对账档案：是、否；需要区分组织）
					Object conforg = (Object) getHyPubBO()
							.findColValue(
									"bd_defdoc",
									"code",
									"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
											+ "FROM BD_DEFDOCLIST WHERE CODE = 'HTSAVEORG')"
											+ " and code = '" + pk_org + "'");// 组织
					if (conforg != null) {
						// 已完成对账不允许保存
						if ("1001A2100000000B68C1".equals(xmvo.getDef17())) {
							throw new BusinessException("项目编码["
									+ xmvo.getProject_code() + "]已完成对账，不允许保存！");
						}
					}
				}
			}
		}
	}

	public PurchaseInVO[] sign(PurchaseInVO[] billVOs) throws BusinessException {
		PurchaseInVO[] resultVOs = null;
		try {
			SignAction action = new SignAction();
			return action.sign(billVOs);
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
		}
		return null;
	}

	public PurchaseInVO[] update(PurchaseInVO[] billVOs,
			PurchaseInVO[] originBills) throws BusinessException {
		PurchaseInVO[] resultVOs = null;
		try {
			UpdateAction action = new UpdateAction();
			// XBX校验
			beforecheck(billVOs);
			return action.update(billVOs, originBills);
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
		}
		return null;
	}
}
