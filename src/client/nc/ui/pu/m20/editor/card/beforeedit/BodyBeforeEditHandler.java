package nc.ui.pu.m20.editor.card.beforeedit;

import java.util.Map;

import nc.ui.pu.m20.editor.card.beforeedit.body.Astunitid;
import nc.ui.pu.m20.editor.card.beforeedit.body.BatchCode;
import nc.ui.pu.m20.editor.card.beforeedit.body.Cffileid;
import nc.ui.pu.m20.editor.card.beforeedit.body.Dreqdate;
import nc.ui.pu.m20.editor.card.beforeedit.body.Employee;
import nc.ui.pu.m20.editor.card.beforeedit.body.Material;
import nc.ui.pu.m20.editor.card.beforeedit.body.Nastnum;
import nc.ui.pu.m20.editor.card.beforeedit.body.Nchangerate;
import nc.ui.pu.m20.editor.card.beforeedit.body.Nnum;
import nc.ui.pu.m20.editor.card.beforeedit.body.Ntaxmny;
import nc.ui.pu.m20.editor.card.beforeedit.body.Ntaxprice;
import nc.ui.pu.m20.editor.card.beforeedit.body.Projectid;
import nc.ui.pu.m20.editor.card.beforeedit.body.Purchaseorg;
import nc.ui.pu.m20.editor.card.beforeedit.body.Reqdept;
import nc.ui.pu.m20.editor.card.beforeedit.body.Reqstor;
import nc.ui.pu.m20.editor.card.beforeedit.body.Suggestsupplier;
import nc.ui.pu.m20.editor.card.beforeedit.body.Trantypecode;
import nc.ui.pu.m20.editor.card.beforeedit.body.Unitid;
import nc.ui.pu.m23.editor.card.beforeedit.body.NeverEditBodyItem;
import nc.ui.pu.pub.editor.card.beforeedit.Casscustid;
import nc.ui.pu.pub.editor.card.beforeedit.ProjectTaskId;
import nc.ui.pu.pub.editor.card.handler.AbstractCardBodyBeforeEditEventHandler;
import nc.ui.pu.pub.editor.card.listener.ICardBodyBeforeEditEventListener;
import nc.vo.pu.m20.entity.PraybillItemVO;

/**
 * <p>
 * <b>请购单表体编辑前事件的代理处理类，本类主要完成以下功能：</b>
 * <ul>
 * <li>处理编辑前事件
 * </ul>
 * <p>
 * <p>
 * 
 * @version 6.0
 * @since 6.0
 * @author GGR
 * @time 2010-1-24 下午05:00:09
 */
public class BodyBeforeEditHandler extends
    AbstractCardBodyBeforeEditEventHandler {

  @Override
  public void registerEventListener(
      Map<String, ICardBodyBeforeEditEventListener> listenerMap) {
    // 物料
    listenerMap.put(PraybillItemVO.PK_MATERIAL, new Material());
    // 数量
    listenerMap.put(PraybillItemVO.NASTNUM, new Nastnum());
    // 换算率
    listenerMap.put(PraybillItemVO.VCHANGERATE, new Nchangerate());
    // 主数量
    listenerMap.put(PraybillItemVO.NNUM, new Nnum());
    // 本币价税合计
    listenerMap.put(PraybillItemVO.NTAXMNY, new Ntaxmny());
    // 主本币含税单价
    listenerMap.put(PraybillItemVO.NTAXPRICE, new Ntaxprice());
    // 单位
    listenerMap.put(PraybillItemVO.CASTUNITID, new Astunitid());
    // 主单位
    listenerMap.put(PraybillItemVO.CUNITID, new Unitid());

    // 订单类型
    listenerMap.put(PraybillItemVO.CORDERTRANTYPECODE, new Trantypecode());

    // 采购组织
    listenerMap.put(PraybillItemVO.PK_PURCHASEORG, new Purchaseorg());
    listenerMap.put(PraybillItemVO.PK_PURCHASEORG_V, new Purchaseorg());

    // 采购员
    listenerMap.put(PraybillItemVO.PK_EMPLOYEE, new Employee());

    // 供应商
    listenerMap.put(PraybillItemVO.PK_SUGGESTSUPPLIER, new Suggestsupplier());

    // 需求仓库
    listenerMap.put(PraybillItemVO.PK_REQSTOR, new Reqstor());

    // 需求部门
    listenerMap.put(PraybillItemVO.PK_REQDEPT, new Reqdept());
    listenerMap.put(PraybillItemVO.PK_REQDEPT_V, new Reqdept());

    // 客户
    listenerMap.put(PraybillItemVO.CASSCUSTID, new Casscustid());

    // 项目
    listenerMap.put(PraybillItemVO.CPROJECTID, new Projectid());

    // 项目任务
    listenerMap.put(PraybillItemVO.CPROJECTTASKID, new ProjectTaskId());

    // 批次号
    listenerMap.put(PraybillItemVO.VBATCHCODE, new BatchCode());
    // 需求日期
    listenerMap.put(PraybillItemVO.DREQDATE, new Dreqdate());

    // 不允许编辑字段
    NeverEditBodyItem neverEditItem = new NeverEditBodyItem();
    listenerMap.put(PraybillItemVO.BPUBLISHTOEC, neverEditItem);
    listenerMap.put(PraybillItemVO.BCANPURCHASEORGEDIT, neverEditItem);
    listenerMap.put(PraybillItemVO.BFIXEDRATE, neverEditItem);
    listenerMap.put(PraybillItemVO.BROWCLOSE, neverEditItem);
    listenerMap.put(PraybillItemVO.CUNITID, neverEditItem);
    listenerMap.put(PraybillItemVO.PK_GROUP, neverEditItem);
    listenerMap.put(PraybillItemVO.PK_ORG, neverEditItem);
    listenerMap.put(PraybillItemVO.PK_PRAYBILL, neverEditItem);
    listenerMap.put(PraybillItemVO.PK_PRAYBILL_B, neverEditItem);
    listenerMap.put(PraybillItemVO.NACCUMULATENUM, neverEditItem);
    listenerMap.put(PraybillItemVO.NGENCT, neverEditItem);
    listenerMap.put(PraybillItemVO.NPRICEAUDITBILL, neverEditItem);
    listenerMap.put(PraybillItemVO.NQUOTEBILL, neverEditItem);
    listenerMap.put(PraybillItemVO.CSOURCEBID, neverEditItem);
    listenerMap.put(PraybillItemVO.CSOURCEID, neverEditItem);
    listenerMap.put(PraybillItemVO.CSOURCETYPECODE, neverEditItem);
    listenerMap.put(PraybillItemVO.VSOURCECODE, neverEditItem);
    listenerMap.put(PraybillItemVO.VSOURCEROWNO, neverEditItem);
    listenerMap.put(PraybillItemVO.VSRCTRANTYPECODE, neverEditItem);
    listenerMap.put(PraybillItemVO.CFIRSTBID, neverEditItem);
    listenerMap.put(PraybillItemVO.CFIRSTID, neverEditItem);
    listenerMap.put(PraybillItemVO.CFIRSTTYPECODE, neverEditItem);
    listenerMap.put(PraybillItemVO.VFIRSTCODE, neverEditItem);
    listenerMap.put(PraybillItemVO.VFIRSTROWNO, neverEditItem);
    listenerMap.put(PraybillItemVO.VFIRSTTRANTYPE, neverEditItem);
    // 表体“累计合同数量”不允许编辑，2017
    listenerMap.put(PraybillItemVO.TOTALCONTRACTNUM, neverEditItem);
    // 特征码
    listenerMap.put(PraybillItemVO.CFFILEID, new Cffileid());
    // 已安排
    listenerMap.put(PraybillItemVO.BISARRANGE, neverEditItem);
  }

}
