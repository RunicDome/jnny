package nc.bs.pub.action;

import nc.bs.cdm.repayreceiptbankcredit.ace.bp.AceRePayReceiptBankCreditSendApproveBP;
import nc.bs.cdm.repayreceiptbankcredit.plugin.bpplugin.RePayReceiptBankCreditPluginPoint;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.repay.rule.ContractStatusCheckRule;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.CommitStatusCheckRule;
import nc.bs.trade.business.HYPubBO;
import nc.data.param.CommonParam;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.arap.forthird.IArapForDGSWService;
import nc.vo.cdm.repayreceiptbankcredit.AggRePayReceiptBankCreditVO;
import nc.vo.cdm.repayreceiptbankcredit.RePayReceiptBankCreditVO;
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

public class N_36FF_SAVE extends AbstractPfAction<AggRePayReceiptBankCreditVO> {
    private static String WorkFId;// OA WorkflowId 流程ID
    private static String TableName;// OA TableName 表名

    public N_36FF_SAVE() {
    }

    protected CompareAroundProcesser<AggRePayReceiptBankCreditVO> getCompareAroundProcesserWithRules(Object userObj) {
        CompareAroundProcesser<AggRePayReceiptBankCreditVO> processor = new CompareAroundProcesser(RePayReceiptBankCreditPluginPoint.SEND_APPROVE);
        IRule<AggRePayReceiptBankCreditVO> rule = new CommitStatusCheckRule();
        IRule<AggRePayReceiptBankCreditVO> contractStatusCheckRule = new ContractStatusCheckRule();
        processor.addBeforeRule(rule);
        processor.addBeforeRule(contractStatusCheckRule);
        return processor;
    }

    protected AggRePayReceiptBankCreditVO[] processBP(Object userObj, AggRePayReceiptBankCreditVO[] clientFullVOs, AggRePayReceiptBankCreditVO[] originBills) {
        AggRePayReceiptBankCreditVO[] bills = (new AceRePayReceiptBankCreditSendApproveBP()).sendApprove(clientFullVOs, originBills);
        for (AggRePayReceiptBankCreditVO temp : bills) {
            //获取表头VO
            RePayReceiptBankCreditVO hvo = temp.getParentVO();
            String pkBilltypecode = hvo.getPk_billtypecode();
            try {
                // 检查组织类型，并执行相应的流程
                if ("4".equals(OaWorkFlowUtil.getDef2(hvo.getPk_org())) || "1".equals(OaWorkFlowUtil.getDef2(hvo.getPk_org()))) {
                    // 获取OA流程相关信息
                    OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hvo.getPk_org(), pkBilltypecode);
                    if (oaVo != null && "0".equals(String.valueOf(oaVo.getIsdr()))) {
                        String WorkFId = oaVo.getFlowid();
                        String TableName = oaVo.getTablename();
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
                        workFlowVO.setWorkflowId(WorkFId);
//                        workFlowVO.setWorkflowId(OaWorkFlowUtil.getOAFlowID(WorkFId)); // 接口获取OA流程ID
                        workFlowVO.setWorkflowName(oaVo.getBilltypename());
                        WorkFlowBill bill = OaWorkFlowUtil.getWorkFlowBill(workFlowVO);
                        bill.setDef4(hvo.getVbillno()); // 单据编号
                        // 根据组织类型区分字段设置
                        if ("4".equals(OaWorkFlowUtil.getDef2(hvo.getPk_org()))) {
                            bill.setDef3("ZT");
                            // 制单人身份证号
                            UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class, hvo.getBillmaker());
                            String idCard = (String) getHyPubBO().findColValue("bd_psndoc", "id", "nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
                            bill.setDef5(idCard);
                        } else if ("1".equals(OaWorkFlowUtil.getDef2(hvo.getPk_org()))) {
                            // 制单人用户代码
                            UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class, hvo.getBillmaker());
                            bill.setDef5(userVO.getUser_code());
                        }
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
    private JSONArray getNewMainMap(RePayReceiptBankCreditVO parentVO)
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
    private Map<String, Object> getBody(RePayReceiptBankCreditVO[] bvo) {
        Map<String, Object> dtMap = new HashMap<String, Object>();
        dtMap.put("tableDBName", TableName + "_dt1");
        List<Map<String, Object>> workflowRequestTableRecords = new ArrayList<Map<String, Object>>();
        for (RePayReceiptBankCreditVO temp : bvo) {
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
    private JSONArray getNewDtaileDataMap(AggRePayReceiptBankCreditVO temp)
            throws BusinessException {
        // 获取详细信息
        RePayReceiptBankCreditVO[] bvos = (RePayReceiptBankCreditVO[]) temp
                .getChildrenVO();
        Map<String, Object> bodyMap = getBody(bvos);
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
        return dtlistString;
    }
    private HYPubBO hyPubBO;

    public HYPubBO getHyPubBO() {
        if (null == hyPubBO) {
            hyPubBO = new HYPubBO();
        }
        return hyPubBO;
    }
}
