package nc.bs.pub.action;

import nc.bs.am.actionscript.BaseCommitActionScript;
import nc.bs.framework.common.NCLocator;
import nc.itf.aim.prv.IEquip;
import nc.vo.aim.equip.EquipVO;
import nc.vo.aim.equip.MappedTabbedVO;
import nc.vo.aim.equip.MeppedTabbedVoUtils;
import nc.vo.am.common.util.ArrayUtils;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;

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
        EquipVO[] vos = (EquipVO[]) ArrayUtils.convertArrayType(
                getVos(), EquipVO.class);

        return ((IEquip) NCLocator.getInstance().lookup(IEquip.class))
                .insertEquipVos(null, vos);
    }

    protected Object doUpdateAction(PfParameterVO parameterVo)
            throws BusinessException {
        setParameter("loginContext", null);
        setParameter("vo", getVo());
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
}
