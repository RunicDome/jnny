
package nc.bs.pub.action;

import nc.bs.cdm.handlingfeesreceipt.ace.bp.AceHandlingFeesReceiptSendApproveBP;
import nc.bs.cdm.handlingfeesreceipt.plugin.bpplugin.HandlingFeesReceiptPluginPoint;
import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.CommitStatusCheckRule;
import nc.bs.trade.business.HYPubBO;
import nc.data.param.CommonParam;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.arap.forthird.IArapForDGSWService;
import nc.vo.cdm.handlingfeesreceipt.AggHandlingFeesReceiptVO;
import nc.vo.cdm.handlingfeesreceipt.HandlingFeesReceiptBVO;
import nc.vo.cdm.handlingfeesreceipt.HandlingFeesReceiptVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class N_36FI_SAVE extends AbstractPfAction<AggHandlingFeesReceiptVO> {
    private static String WorkFId;// OA WorkflowId 流程ID
    private static String TableName;// OA TableName 表名

    public N_36FI_SAVE() {
    }

    protected CompareAroundProcesser<AggHandlingFeesReceiptVO> getCompareAroundProcesserWithRules(Object userObj) {
        CompareAroundProcesser<AggHandlingFeesReceiptVO> processor = new CompareAroundProcesser(HandlingFeesReceiptPluginPoint.SEND_APPROVE);
        IRule<AggHandlingFeesReceiptVO> rule = new CommitStatusCheckRule();
        processor.addBeforeRule(rule);
        return processor;
    }

    protected AggHandlingFeesReceiptVO[] processBP(Object userObj, AggHandlingFeesReceiptVO[] clientFullVOs, AggHandlingFeesReceiptVO[] originBills) {
        AggHandlingFeesReceiptVO[] bills = (new AceHandlingFeesReceiptSendApproveBP()).sendApprove(clientFullVOs, originBills);
        for (AggHandlingFeesReceiptVO temp : bills) {
            //获取表头VO
            HandlingFeesReceiptVO hvo = temp.getParentVO();
            String pkBilltypecode = hvo.getPk_billtypecode();
            try {
                if ("4".equals(getDef2(hvo.getPk_org()))) {
                    OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hvo.getPk_org(),
                            pkBilltypecode);
                    if (oaVo != null && oaVo.getIsdr() == 0) {
                        WorkFId = oaVo.getFlowid();
                        TableName = oaVo.getTablename();
                        // 获取主表数据
                        JSONArray headData = getNewMainMap(hvo);
                        // 获取子表数据
                        JSONArray bodyData = getNewDtaileDataMap(temp);
                        // 构造workflow信息
                        OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
                        workFlowVO.setPrimaryKey(hvo.getPrimaryKey());
                        workFlowVO.setPkGroup(hvo.getPk_group());
                        workFlowVO.setPkOrg(hvo.getPk_org());
                        workFlowVO.setBillMaker(hvo.getBillmaker());
                        workFlowVO.setCreator(hvo.getCreator());
                        workFlowVO.setBillCode(pkBilltypecode);
                        // 接口获取oa流程ID
                        workFlowVO.setWorkflowId(OaWorkFlowUtil
                                .getOAFlowID(WorkFId));
//                        workFlowVO.setWorkflowId(WorkFId);
                        workFlowVO.setWorkflowName(oaVo.getBilltypename());
                        WorkFlowBill bill = OaWorkFlowUtil
                                .getWorkFlowBill(workFlowVO);
                        bill.setDef3("ZT");
                        bill.setDef4(hvo.getVbillno());// 单据编号
                        // 制单人身份证号
                        UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
                                UserVO.class, hvo.getBillmaker());
                        String idCard = (String) getHyPubBO().findColValue(
                                "bd_psndoc",
                                "id",
                                "nvl(dr,0) = 0 and pk_psndoc='"
                                        + userVO.getPk_psndoc() + "'");
                        bill.setDef5(idCard);
                        // 调用OA工具类同步数据至OA
                        OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
                    }
                }else if ("1".equals(getDef2(hvo.getPk_org()))) {
                    OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hvo.getPk_org(),pkBilltypecode);
                    if(oaVo != null && oaVo.getIsdr() == 0){
                        // 热力
                        WorkFId = oaVo.getFlowid();
                        TableName = oaVo.getTablename();
                        // 获取主表数据
                        JSONArray headData = getNewMainMap(hvo);
                        // 获取子表数据
                        JSONArray bodyData = getNewDtaileDataMap(temp);
                        // 构造workflow信息
                        OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
                        workFlowVO.setPrimaryKey(hvo.getPrimaryKey());
                        workFlowVO.setPkGroup(hvo.getPk_group());
                        workFlowVO.setPkOrg(hvo.getPk_org());
                        workFlowVO.setBillMaker(hvo.getBillmaker());
                        workFlowVO.setCreator(hvo.getCreator());
                        workFlowVO.setBillCode(pkBilltypecode);
                        // 接口获取oa流程ID
                        workFlowVO.setWorkflowId(OaWorkFlowUtil
                                .getOAFlowID(WorkFId));
//                        workFlowVO.setWorkflowId(WorkFId);
                        workFlowVO.setWorkflowName(oaVo.getBilltypename());
                        WorkFlowBill bill = OaWorkFlowUtil
                                .getWorkFlowBill(workFlowVO);
                        bill.setDef4(hvo.getVbillno());// 单据编号
                        // 制单人身份证号
                        UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
                                UserVO.class, hvo.getBillmaker());
                        bill.setDef5(userVO.getUser_code());
                        // 调用OA工具类同步数据至OA
                        OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
                    }
                }
                // 调用东港税务系统同步单据状态
                IArapForDGSWService util = (IArapForDGSWService) NCLocator
                        .getInstance().lookup(IArapForDGSWService.class);
                // 单据号，组织主键，事件类型，单据类型
                JSONObject res = util.sendBillByNCBill(hvo.getPrimaryKey(),
                        hvo.getPk_org(), CommonParam.COMMIT, hvo.getPk_billtypecode());
                if (!"Y".equals(res.getString("success"))) {
                    throw new BusinessException(res.getString("errinfo"));
                }
            } catch (BusinessException e) {
                throw new RuntimeException(e);
            }
        }
        return bills;
    }

    private JSONArray getNewMainMap(HandlingFeesReceiptVO parentVO)
            throws BusinessException {
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        // 构造数据
        List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
        // ---------其他字段begin

        // ---------其他字段end
        JSONArray arr = JSONArray.fromObject(list, jsonConfig);
        return arr;
    }

    private Map<String, Object> getBody(HandlingFeesReceiptBVO[] bvo)
            throws BusinessException {
        Map<String, Object> dtMap = new HashMap<String, Object>();
        dtMap.put("tableDBName", TableName + "_dt1");
        List<Map<String, Object>> workflowRequestTableRecords = new ArrayList<Map<String, Object>>();
        for (HandlingFeesReceiptBVO temp : bvo) {
            // 构造数据
            List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
                    .transBean2Map(temp);
            // 其他字段------begin

            // 其他字段------end
            Map<String, Object> workflowRequestTableFieldsMap = new HashMap<String, Object>();
            workflowRequestTableFieldsMap.put("recordOrder", "0");
            workflowRequestTableFieldsMap.put("workflowRequestTableFields",
                    workflowRequestTableFields);
            workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
        }
        dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
        return dtMap;
    }


    private JSONArray getNewDtaileDataMap(AggHandlingFeesReceiptVO temp)
            throws BusinessException {
        // 获取详细信息
        HandlingFeesReceiptBVO[] bvos = (HandlingFeesReceiptBVO[]) temp
                .getChildrenVO();
        Map<String, Object> bodyMap = getBody(bvos);
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
        return dtlistString;
    }

    public String getDef2(String pk_org) throws BusinessException {
        String def2 = "";
        OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
                pk_org);
        if (null != orgVO) {
            def2 = orgVO.getDef2();
        }
        return def2;
    }

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
}
