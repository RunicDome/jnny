//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package nc.impl.aim.equip.validator;

import nc.bs.trade.business.HYPubBO;
import nc.bs.uif2.validation.ValidationFailure;
import nc.uif.pub.exception.UifException;
import nc.vo.aim.equip.EquipHeadVO;
import nc.vo.aim.equip.EquipVO;
import nc.vo.am.common.util.EqualsBuilder;
import nc.vo.ml.NCLangRes4VoTransl;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class EquipOrgValidator extends BaseValidator {
    public EquipOrgValidator() {
    }

    public ValidationFailure validate(Object obj) {
        ValidationFailure validationFailure = null;
        StringBuilder msg = null;
        EquipVO equipVO = this.convertVO(obj);
        msg = this.validateEquipVO(equipVO);
        if (msg != null && msg.length() > 0) {
            validationFailure = new ValidationFailure();
            validationFailure.setMessage(msg.toString());
        }

        return validationFailure;
    }

    private StringBuilder validateEquipVO(EquipVO equipVO) {
        StringBuilder msg = new StringBuilder();
        String pk_org = equipVO.getParentVO().getPk_org();
        String pk_ownerorg = equipVO.getParentVO().getPk_ownerorg();
        String pk_usedorg = equipVO.getParentVO().getPk_usedorg();
        /* 校验SN码是否重复 */
        String SN = equipVO.getParentVO().getDef31();
        if (StringUtils.isNotEmpty(SN)) {
            String wheresql = " nvl(dr,0) = 0  and def31 = '" + SN + "'";
            wheresql += " and pk_equip <> '" + equipVO.getParentVO().getPk_equip() + "'";
            try {
                EquipHeadVO[] sbvos = (EquipHeadVO[]) new HYPubBO()
                        .queryByCondition(EquipHeadVO.class, wheresql);
                if (sbvos != null && sbvos.length > 0) {
                    List<String> codels = new ArrayList<>();
                    for (EquipHeadVO hvo : sbvos) {
                        codels.add(hvo.getEquip_code());
                    }
                    msg.append("SN码重复：" + codels.toString());
                    return msg;
                }
            } catch (UifException e) {
                return msg.append("SN校验异常：" + e.getMessage());
            }

        }
        if (!EqualsBuilder.isEquals(pk_org, pk_ownerorg) && !EqualsBuilder.isEquals(pk_org, pk_usedorg)) {
            msg.append(NCLangRes4VoTransl.getNCLangRes().getStrByID("equip_0", "04510004-0299"));
            return msg;
        } else {
            return msg;
        }
    }
}
