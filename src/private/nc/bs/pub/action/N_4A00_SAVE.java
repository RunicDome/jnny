package nc.bs.pub.action;

import java.util.ArrayList;
import java.util.List;

import nc.bs.am.actionscript.BaseCommitActionScript;
import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
import nc.itf.aim.prv.IEquip;
import nc.vo.aim.equip.EquipHeadVO;
import nc.vo.aim.equip.EquipVO;
import nc.vo.aim.equip.MappedTabbedVO;
import nc.vo.aim.equip.MeppedTabbedVoUtils;
import nc.vo.am.common.util.ArrayUtils;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;

import org.apache.commons.lang.StringUtils;

// 设备卡片接口类
public class N_4A00_SAVE extends BaseCommitActionScript {
	public N_4A00_SAVE() {
	}

	public Object runComClass(PfParameterVO parameterVo)
			throws BusinessException {
		return super.runComClass(parameterVo);
	}

	protected Object doCommitAction(PfParameterVO parameterVo)
			throws BusinessException {
		setParameter("abstractCompiler", this);
		setParameter("parameterVo", parameterVo);

		Object cardStatus = getVo().getParentVO().getAttributeValue(
				"card_status");
		if (Integer.valueOf(3).equals(cardStatus)) {
			EquipVO[] vos = (EquipVO[]) getVos();

			return vos;
		}

		return ((IEquip) NCLocator.getInstance().lookup(IEquip.class))
				.commitBillVOs((EquipVO[]) ArrayUtils.convertArrayType(
						getVos(), EquipVO.class));
	}

	protected Object doInsertAction(PfParameterVO parameterVo)
			throws BusinessException {
		setParameter("loginContext", null);
		setParameter("vos", getVos());

		/* 校验SN码是否重复 */
		EquipVO[] vos = (EquipVO[]) ArrayUtils.convertArrayType(
				getVos(), EquipVO.class);
		for(EquipVO vo : vos){
			ifSaveBySNRepeat(vo,1);
		}
		return ((IEquip) NCLocator.getInstance().lookup(IEquip.class))
				.insertEquipVos(null, vos);
	}

	protected Object doUpdateAction(PfParameterVO parameterVo)
			throws BusinessException {
		setParameter("loginContext", null);
		setParameter("vo", getVo());
		/* 校验SN码是否重复 */
		ifSaveBySNRepeat((EquipVO) getVo(),2);
		return ((IEquip) NCLocator.getInstance().lookup(IEquip.class))
				.updateEquipVo(null, (EquipVO) getVo());
	}

	protected void synParameterData(AggregatedValueObject[] billVos,
			Object retObj) {
		if (retObj != null) {
			if ((retObj instanceof MappedTabbedVO)) {
				MeppedTabbedVoUtils.setValuesToVo((EquipVO) billVos[0],
						(MappedTabbedVO) retObj);
			} else if ((retObj instanceof MappedTabbedVO[])) {
				MappedTabbedVO[] mtbVos = (MappedTabbedVO[]) retObj;
				for (int i = 0; i < mtbVos.length; i++) {
					MeppedTabbedVoUtils.setValuesToVo((EquipVO) billVos[i],
							mtbVos[i]);
				}
			}
		}
	}

	private void ifSaveBySNRepeat(EquipVO equipvo, int type)
			throws BusinessException {
		String SN = equipvo.getParentVO().getDef31();
		if (StringUtils.isNotEmpty(SN)) {
			String wheresql = " nvl(dr,0) = 0  and def31 = '" + SN + "'";
			if(type == 2){
				wheresql += " and pk_equip <> '"+equipvo.getParentVO().getPk_equip()+"'";
			}
			EquipHeadVO[] sbvos = (EquipHeadVO[]) new HYPubBO()
					.queryByCondition(EquipHeadVO.class, wheresql);
			if (sbvos != null && sbvos.length > 0) {
				List<String> codels = new ArrayList<>();
				for (EquipHeadVO hvo : sbvos) {
					codels.add(hvo.getEquip_code());
				}
				throw new BusinessException("SN码重复：" + codels.toString());
			}
		}
	}
}
