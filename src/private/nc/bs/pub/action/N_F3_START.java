package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
import nc.data.param.CommonParam;
import nc.itf.arap.forthird.IArapForDGSWService;
import nc.ui.pcm.utils.GetDao;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.arap.pay.PayBillItemVO;
import nc.vo.arap.pay.PayBillVO;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.org.FinanceOrgVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.sm.UserVO;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.OaWorkFlowVO;
import nc.ws.intf.WorkFlowBill;
import nc.ws.intf.oadata.util.F3OaUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "rawtypes", "unchecked", "restriction"})
public class N_F3_START extends N_F3_SAVE {

    private static String RL_WORKFLOW_ID_CLFk_TABLE_NAME = "材料付款单";
    private static String RL_WORKFLOW_ID_GCFk_TABLE_NAME = "工程付款单";
    private static String RL_WORKFLOW_ID_FYFk_TABLE_NAME = "费用付款单";
    private static String RL_WORKFLOW_ID_SDRFk_TABLE_NAME = "水电燃付款审批单";
    private static String RL_WORKFLOW_ID_MTWGRFk_TABLE_NAME = "煤炭外购热付款审批单";
    private static String RL_WORKFLOW_ID_ZCGZFK_TABLE_NAME = "资产购置付款单";
    private static String RL_WORKFLOW_ID_RZHK_TABLE_NAME = "融资还款单";
    private static String RL_WORKFLOW_ID_SFFK_TABLE_NAME = "税费付款审批单";
    private static String RL_WORKFLOW_ID_XCZF_TABLE_NAME = "薪酬付款审批单（宏景）";

    public N_F3_START() {
    }

    public Object runComClass(PfParameterVO pfparametervo)
            throws BusinessException {
        AggregatedValueObject[] obj = (AggregatedValueObject[]) super
                .runComClass(pfparametervo);
        for (AggregatedValueObject temp : obj) {
            BaseBillVO hvo = (BaseBillVO) temp.getParentVO();
            PayBillVO hvo1 = (PayBillVO) hvo;
            PayBillItemVO[] mxVOs = (PayBillItemVO[]) temp.getChildrenVO();
            String supplier = mxVOs[0].getSupplier();
            String recaccount = mxVOs[0].getRecaccount();
            String payaccount = mxVOs[0].getPayaccount();
            String pu_deptid = mxVOs[0].getPu_deptid();
            String pk_deptid = mxVOs[0].getPk_deptid();
            if ("4".equals(getDef2(hvo1.getPk_org()))) {
                String type = hvo1.getPk_tradetype();
                // Map<String, String> billTypeMap = billTypeMap();
                /*
                 * if (billTypeMap.containsKey(type)) { }
                 */
                OAFlowVO oaVo = OaWorkFlowUtil.getOAFlowInfo(hvo1.getPk_org(),
                        type);
                if (oaVo != null && oaVo.getIsdr() == 0) {
                    String workFlowId = oaVo.getFlowid();
                    workFlowId = OaWorkFlowUtil.getOAFlowID(workFlowId);
                    String tableName = oaVo.getTablename();
                    String billTypename = oaVo.getBilltypename();
                    // 获取主表数据
                    JSONArray headData = getNewMainMap(hvo1, supplier,
                            recaccount, payaccount, pu_deptid);
                    // 获取子表数据
                    JSONArray bodyData = getNewDtaileDataMap(
                            (PayBillItemVO[]) temp.getChildrenVO(),
                            (PayBillVO) temp.getParentVO(), tableName, type);
                    // 构造workflow信息
                    WorkFlowBill bill = getFlowBill(hvo1, "F3", workFlowId,
                            billTypename);
                    // 调用OA工具类同步数据至OA
                    OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
                }
            } else if ("1".equals(getDef2(hvo1.getPk_org()))) {
                String type = hvo1.getPk_tradetype();
                if ("F3-Cxx-RZHK".equals(hvo1.getPk_tradetype())) {
                    // 融资还款单##@@
                    JSONArray headData = getRZMainMap(
                            (PayBillVO) temp.getParentVO(), supplier);
                    JSONArray bodyData = getRZDtaileDataMap(
                            (PayBillItemVO[]) temp.getChildrenVO(),
                            (PayBillVO) temp.getParentVO());
                    /*
                     * step-1 调用OA工具类同步数据至OA
                     */
                    OaWorkFlowUtil.sendOaData(headData, bodyData,
                            getWorkFlowBill(temp));
                } else if (hvo1.getPk_tradetype().contains("D3")) {
                    // 材料付款单##@@
                    JSONArray headData = F3OaUtil.getMainMap1(
                            (PayBillVO) temp.getParentVO(), supplier);
                    JSONArray bodyData = F3OaUtil.getDtaileDataMap(temp);
                    /*
                     * step-1 调用OA工具类同步数据至OA
                     */
                    OaWorkFlowUtil.sendOaData(headData, bodyData,
                            getWorkFlowBill(temp));
                } else if ("F3-Cxx-01".equals(hvo1.getPk_tradetype())) {
                    // 工程付款单###@@
                    JSONArray headData = F3OaUtil.getMainMap2(
                            (PayBillVO) temp.getParentVO(), supplier);
                    JSONArray bodyData = F3OaUtil.getDtaileDataMap(temp);
                    /*
                     * step-1 调用OA工具类同步数据至OA
                     */
                    OaWorkFlowUtil.sendOaData(headData, bodyData,
                            getWorkFlowBill(temp));
                } else if ("F3-Cxx-FYFKSPD".equals(hvo1.getPk_tradetype())
                        || "F3-Cxx-SDRFKSPD".equals(hvo1.getPk_tradetype())) {
                    // 费用付款单//水电付款单@@
                    JSONArray headData = F3OaUtil.getMainMap5(
                            (PayBillVO) temp.getParentVO(), supplier);
                    JSONArray bodyData = F3OaUtil.getDtaileDataMap(temp);
                    /*
                     * step-1 调用OA工具类同步数据至OA
                     */
                    OaWorkFlowUtil.sendOaData(headData, bodyData,
                            getWorkFlowBill(temp));
                } else if ("F3-Cxx-MTWGRFKSPD".equals(hvo1.getPk_tradetype()) || "F3-Cxx-WGRFKSPD".equals(hvo1.getPk_tradetype())) {
                    // 煤炭付款单
                    JSONArray headData = F3OaUtil.getMainMap5(
                            (PayBillVO) temp.getParentVO(), supplier);
                    JSONArray bodyData = F3OaUtil.getDtaileDataMapMT(temp);
                    /*
                     * step-1 调用OA工具类同步数据至OA
                     */
                    OaWorkFlowUtil.sendOaData(headData, bodyData,
                            getWorkFlowBill(temp));
                } else if ("F3-Cxx-GDZC".equals(hvo1.getPk_tradetype())) {
                    // 资产购置付款单
                    JSONArray headData = F3OaUtil
                            .getMainMap6((PayBillVO) temp.getParentVO(),
                                    supplier, pk_deptid);
                    JSONArray bodyData = F3OaUtil.getDtaileDataMapZC(temp);
                    /*
                     * step-1 调用OA工具类同步数据至OA
                     */
                    OaWorkFlowUtil.sendOaData(headData, bodyData,
                            getWorkFlowBill(temp));
                } else if ("F3-Cxx-SFFKSPD".equals(hvo1.getPk_tradetype())) {
                    // 税费付款审批单
                    String tableName = "formtable_main_437";
                    // 获取主表数据
                    JSONArray headData = getNewMainMap(hvo1, supplier,
                            recaccount, payaccount, pu_deptid);
                    // 获取子表数据
                    JSONArray bodyData = getNewDtaileDataMap(
                            (PayBillItemVO[]) temp.getChildrenVO(),
                            (PayBillVO) temp.getParentVO(), tableName, type);
                    // 构造workflow信息
                    WorkFlowBill bill = getWorkFlowBill(temp);
                    // 调用OA工具类同步数据至OA
                    OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
                } else if ("F3-Cxx-XCFKSPD".equals(hvo1.getPk_tradetype())) {
                    // 薪酬付款审批单（宏景）
                    String tableName = "formtable_main_439";
                    // 获取主表数据
                    JSONArray headData = getNewMainMap(hvo1, supplier,
                            recaccount, payaccount, pu_deptid);
                    // 获取子表数据
                    JSONArray bodyData = getNewDtaileDataMap(
                            (PayBillItemVO[]) temp.getChildrenVO(),
                            (PayBillVO) temp.getParentVO(), tableName, type);
                    // 构造workflow信息
                    WorkFlowBill bill = getWorkFlowBill(temp);
                    // 调用OA工具类同步数据至OA
                    OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
                }
            } else if ("HR".equals(getDef2(hvo1.getPk_org()))) {
                if ("F3-Cxx-XCFKSPD".equals(hvo1.getPk_tradetype())) {
                    // 薪酬付款审批单（宏景）
                    String tableName = "formtable_main_439";
                    // 获取主表数据
                    JSONArray headData = getNewMainMap(hvo1, supplier,
                            recaccount, payaccount, pu_deptid);
                    // 获取子表数据
                    JSONArray bodyData = getNewDtaileDataMap(
                            (PayBillItemVO[]) temp.getChildrenVO(),
                            (PayBillVO) temp.getParentVO(), tableName,
                            hvo1.getPk_tradetype());
                    // 构造workflow信息
                    WorkFlowBill bill = getWorkFlowBill(temp);
                    // 调用OA工具类同步数据至OA
                    OaWorkFlowUtil.sendOaData(headData, bodyData, bill);
                }
            }
            // 调用东港税务系统同步单据状态
            IArapForDGSWService util = (IArapForDGSWService) NCLocator
                    .getInstance().lookup(IArapForDGSWService.class);
            // 单据号，组织主键，事件类型，单据类型
            JSONObject res = util.sendBillByNCBill(hvo.getPrimaryKey(),
                    hvo.getPk_org(), CommonParam.COMMIT, hvo.getPk_billtype());
            if (!"Y".equals(res.getString("success"))) {
                throw new BusinessException(res.getString("errinfo"));
            }
        }
        return obj;
    }

    private WorkFlowBill getFlowBill(PayBillVO hvo, String billCode,
                                     String workFlowId, String flowName) throws BusinessException {
        OaWorkFlowVO workFlowVO = new OaWorkFlowVO();
        workFlowVO.setPrimaryKey(hvo.getPrimaryKey());
        workFlowVO.setPkGroup(hvo.getPk_group());
        workFlowVO.setPkOrg(hvo.getPk_org());
        workFlowVO.setBillMaker(hvo.getCreator());
        workFlowVO.setCreator(hvo.getCreator());
        workFlowVO.setBillCode(billCode);
        // 接口获取
        workFlowVO.setWorkflowId(workFlowId);
        // workFlowVO.setWorkflowId(OaWorkFlowUtil.getOAFlowID(workFlowId));
        workFlowVO.setWorkflowName(flowName);
        WorkFlowBill bill = OaWorkFlowUtil.getWorkFlowBill(workFlowVO);
        bill.setDef3("ZT");
        bill.setDef4(hvo.getBillno());// 单据编号
        // 制单人身份证号
        UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
                hvo.getCreator());
        PsndocVO psndoc = (PsndocVO) getHyPubBO().queryByPrimaryKey(
                PsndocVO.class, userVO.getPk_psndoc());
        bill.setDef5(psndoc.getId());
        return bill;
    }

    private JSONArray getNewDtaileDataMap(PayBillItemVO[] childrenVO,
                                          PayBillVO parentVO, String tableName, String type)
            throws BusinessException {
        // 获取详细信息
        // List dtlist = new ArrayList();
        Map bodyMap = getBody(childrenVO, parentVO, tableName, type);
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        JSONArray dtlistString = JSONArray.fromObject(bodyMap, jsonConfig);
        return dtlistString;
    }

    private Map getBody(PayBillItemVO[] childrenVO, PayBillVO parentVO,
                        String tableName, String type) throws BusinessException {
        Map dtMap = new HashMap();
        // 接口获取
        dtMap.put("tableDBName", tableName + "_dt1");
        // dtMap.put("tableDBName", tableName);
        List workflowRequestTableRecords = new ArrayList();
        for (PayBillItemVO temp : childrenVO) {
            // 构造数据
            List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
                    .transBean2Map(temp);
            if (temp.getProject() != null && !"F3-Cxx-GDZC".equals(type)) {
                // 项目编码
                String project_code = (String) getHyPubBO().findColValue(
                        "bd_project",
                        "project_code",
                        "nvl(dr,0) = 0 and pk_project = '" + temp.getProject()
                                + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "project_code", project_code));
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "xmbm", project_code));
                // 项目名称
                String project_name = (String) getHyPubBO().findColValue(
                        "bd_project",
                        "project_name",
                        "nvl(dr,0) = 0 and pk_project = '" + temp.getProject()
                                + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "project_name", project_name));
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("xm",
                        project_name));
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "xmmc", project_name));
            }
            // 是否进成本
            if (temp.getDef102() != null) {
                String sfjcb = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef102()
                                + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "sfjcb", sfjcb));
            }
            // 预算类别
            String yslbname = (String) getHyPubBO().findColValue(
                    "bd_inoutbusiclass",
                    "name",
                    "nvl(dr,0) = 0 and pk_inoutbusiclass = '"
                            + temp.getPk_subjcode() + "'");
            workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("yslb",
                    yslbname));
            workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("szxm",
                    yslbname));
            // 资金计划项目
            String zjjhxm = (String) getHyPubBO().findColValue(
                    "bd_fundplan",
                    "name",
                    "nvl(dr,0) = 0 and pk_fundplan = '"
                            + temp.getBankrollprojet() + "'");
            workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("zjjhxm",
                    zjjhxm));
            // 供应商
            if (temp.getSupplier() != null) {
                String pk_supplier_name = (String) getHyPubBO().findColValue(
                        "bd_supplier",
                        "name",
                        "nvl(dr,0) = 0 and pk_supplier = '"
                                + temp.getSupplier() + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "skdw", pk_supplier_name));
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("gys",
                        pk_supplier_name));
            }
            // 收款银行账户
            if (temp.getRecaccount() != null) {
                String skyhzh = (String) getHyPubBO().findColValue(
                        "bd_bankaccsub",
                        "accnum",
                        "nvl(dr,0) = 0 and pk_bankaccsub = '"
                                + temp.getRecaccount() + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "skyhzh", skyhzh));
            }
            /* 换热站 */
            if (parentVO.getDef23() != null) {
                String hrz = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef23()
                                + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("hrz",
                        hrz));
            }
            /* 线别 */
            if (parentVO.getDef71() != null) {
                String xb = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef71()
                                + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("xb",
                        xb));
            }
            // 付款性质
            if (temp.getPrepay() != null) {
                int fkxzint = temp.getPrepay();
                String fkxz = "";
                if (fkxzint == 0) {
                    fkxz = "应付款";
                } else if (fkxzint == 1) {
                    fkxz = "预付款";
                }
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "fkxz", fkxz));
            }
            // 票据类型
            if (null != temp.getDef30()) {
                String pjlx = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef30()
                                + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "pjlx", pjlx));
            }
            // 付款款银行账户
            if (temp.getPayaccount() != null) {
                String fkyhzh = (String) getHyPubBO().findColValue(
                        "bd_bankaccsub",
                        "accnum",
                        "nvl(dr,0) = 0 and pk_bankaccsub = '"
                                + temp.getPayaccount() + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "fkyhzh", fkyhzh));
            }
            // 资金类别
            if (temp.getDef20() != null) {
                String zjlb = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef20()
                                + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "zjlb", zjlb));
            }
            // 现金流量项目
            if (temp.getCashitem() != null) {
                String xjllxm = (String) getHyPubBO().findColValue(
                        "bd_cashflow",
                        "name",
                        "nvl(dr,0) = 0 and pk_cashflow  = '"
                                + temp.getCashitem() + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "xjllxm", xjllxm));
            }
            // 费用发生部门
            if (temp.getPu_deptid() != null && !"F3-Cxx-TSSXSPD".equals(type)) {
                String fyfsbmname = (String) getHyPubBO().findColValue(
                        "org_dept",
                        "name",
                        "nvl(dr,0) = 0 and pk_dept = '" + temp.getPu_deptid()
                                + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "fyfsbm", fyfsbmname));
                // 费用申请部门
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "fysqbm", fyfsbmname));
            }
            // 审计报告挂账
            if (temp.getDef14() != null) {
                String sjbggz = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef14()
                                + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "sjbggz", sjbggz));
            }
            // 能投
            if ("F3-Cxx-RZHK".equals(type)) {
                // 融资还款单
                // 费用发生部门
                if (null != temp.getPu_deptid()) {
                    String fyfsbmname = (String) getHyPubBO().findColValue(
                            "org_dept",
                            "name",
                            "nvl(dr,0) = 0 and  pk_dept = '"
                                    + temp.getPu_deptid() + "'");
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "fyfsbm", fyfsbmname));
                }
                // 费用发生部门
                if (null != temp.getPk_deptid()) {
                    String fyfsbmname = (String) getHyPubBO().findColValue(
                            "org_dept",
                            "name",
                            "nvl(dr,0) = 0 and  pk_dept = '"
                                    + temp.getPk_deptid() + "'");
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "fyfsbm", fyfsbmname));
                }
                // 合同号
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "contractno", temp.getContractno()));
                // 合同号
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("hth",
                        temp.getContractno()));
                // 合同金额
                if (temp.getDef1() != null) {
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "def1", temp.getDef1()));
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "htje", temp.getDef1()));
                }
                // 本次付款金额
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "local_money_de", temp.getLocal_money_de() + ""));
                /* 发票类型 */
                if (null != temp.getDef27()) {
                    String fplx = (String) getHyPubBO().findColValue(
                            "bd_defdoc",
                            "name",
                            "nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef27()
                                    + "'");
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "fplx", fplx));
                }

                // 部门
                String bmname = (String) getHyPubBO().findColValue(
                        "org_dept",
                        "name",
                        "nvl(dr,0) = 0 and pk_dept = '" + temp.getPk_deptid()
                                + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bm",
                        bmname));
                // 已付金额
                if (temp.getDef3() != null) {
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "def3", temp.getDef3()));
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "yfkje", temp.getDef3()));
                }
                // 总付款比例
                if (temp.getDef8() != null) {
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "zfkbl", temp.getDef8()));
                }
                // 不含税金额
                if (temp.getDef15() != null) {
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "def15", temp.getDef15()));
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "bhsje", temp.getDef15()));
                }
                // 折让金额
                if (temp.getDef28() != null) {
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "def28", temp.getDef28()));
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "zrje", temp.getDef28()));
                }

                if (temp.getProject() != null) {
                    /* 项目编码 */
                    String code = (String) getHyPubBO().findColValue(
                            "bd_project",
                            "project_code",
                            "nvl(dr,0) = 0 and pk_project = '"
                                    + temp.getProject() + "'");
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "bproject_code", code));

                    /* 项目名称 */
                    String name = (String) getHyPubBO().findColValue(
                            "bd_project",
                            "project_name",
                            "nvl(dr,0) = 0 and pk_project  = '"
                                    + temp.getProject() + "'");
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "bproject_name", name));
                }
                // 资金来源
                if (temp.getDef21() != null) {
                    String zjly = (String) getHyPubBO().findColValue(
                            "bd_defdoc",
                            "name",
                            "nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef21()
                                    + "'");
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "zjly", zjly));
                }
                // 资金类别
                if (temp.getDef20() != null) {
                    String zjlb = (String) getHyPubBO().findColValue(
                            "bd_defdoc",
                            "name",
                            "nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef20()
                                    + "'");
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "zjlb", zjlb));
                }
                // 备注
                if (temp.getDef13() != null) {
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "def13", temp.getDef13()));
                }
            } else if ("D3".equals(type) || "F3-Cxx-01".equals(type)
                    || "F3-Cxx-FYFKSPD".equals(type)
                    || "F3-Cxx-SDRFKSPD".equals(type)) {
                // 材料付款单 工程付款单 费用打开审批单 水电燃付款单
                // 换热站
                if (null != temp.getDef23()) {
                    String hrz = (String) getHyPubBO().findColValue(
                            "bd_defdoc",
                            "name",
                            "nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef23()
                                    + "'");
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "hrz", hrz));
                }
                // 发票类型
                if (null != temp.getDef27()) {
                    String fplx = (String) getHyPubBO().findColValue(
                            "bd_defdoc",
                            "name",
                            "nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef27()
                                    + "'");
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "fplx", fplx));
                }
                // 不含税金额
                if (temp.getDef15() != null) {
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "def15", temp.getDef15()));
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "bhsje", temp.getDef15()));
                }

                // 票据类型
                if (null != temp.getDef30()) {
                    String pjlx = (String) getHyPubBO().findColValue(
                            "bd_defdoc",
                            "name",
                            "nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef30()
                                    + "'");
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "pjlx", pjlx));
                }
                /* 发票金额 */
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "ykfpje", temp.getDef9()));

                // 合同号
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "contractno", temp.getContractno()));
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("hth",
                        temp.getContractno()));
                // 合同金额
                if (temp.getDef1() != null) {
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "def1", temp.getDef1()));
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "htje", temp.getDef1()));
                }
                /* 往来对象3=业务员，2=部门，1=供应商 */
                Map wldx = OaWorkFlowUtil.listAddObj("wldx", temp.getObjtype()
                        + "");
                workflowRequestTableFields.add(wldx);

                /* 本次付款金额 */
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "bcfkje", temp.getLocal_money_de() + ""));

                /* 发票号 */
                if (null != temp.getInvoiceno()) {
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "fph", temp.getInvoiceno()));
                }

                /* 已付款金额 */
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "yfkje", temp.getDef3()));

                /* 已付款比例 */
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "yfkbl", temp.getDef6()));

                /* 本次付款比例 */
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "bcfkbl", temp.getDef7()));

                /* 总付款比例 */
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "zfkbl", temp.getDef8()));

                /* 审计金额 */
                if (null != temp.getDef2()) {
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "sjje", temp.getDef2()));
                }

                /* 折让金额28 */
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "zrje",
                        new UFDouble(temp.getDef28() == null ? "0.00" : temp
                                .getDef28())
                                .setScale(2, UFDouble.ROUND_HALF_UP)
                                + ""));

                /* 审计报告挂账14 */
                if (null != temp.getDef14()) {
                    String sjbggz = (String) getHyPubBO().findColValue(
                            "bd_defdoc",
                            "name",
                            "nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef14()
                                    + "'");
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "sjbggz", sjbggz));
                }

                /* 本次挂账金额35 */
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "bcgzje", temp.getDef35()));

                /* 累计挂账金额36 */
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "ljgzje", temp.getDef36()));

                /* 付款单行标识 */
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "fkdxbs", temp.getPk_payitem()));
            } else if ("F3-Cxx-MTWGRFKSPD".equals(type) || "F3-Cxx-WGRFKSPD".equals(type)) {
                // 煤炭外购热
                // 不含税金额
                if (temp.getDef15() != null) {
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "bhsje", temp.getDef15()));
                }
                /* 票据类型 */
                if (null != temp.getDef27()) {
                    String fplx = (String) getHyPubBO().findColValue(
                            "bd_defdoc",
                            "name",
                            "nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef27()
                                    + "'");
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "fplx", fplx));
                }
                // 合同号
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "contractno", temp.getContractno()));
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("hth",
                        temp.getContractno()));
                // 本次付款金额
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "local_money_de", temp.getLocal_money_de() + ""));
                // 本次付款金额
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "bcfkje", temp.getLocal_money_de() + ""));
            } else if ("F3-Cxx-GDZC".equals(type)) {
                // 资产购置付款单
                // 合同号
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "contractno", temp.getContractno()));
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("hth",
                        temp.getContractno()));
                // 合同金额
                if (temp.getDef1() != null) {
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "htje", temp.getDef1()));
                }
                // 项目
                if (temp.getDef31() != null) {
                    String project_name = (String) getHyPubBO().findColValue(
                            "bd_defdoc",
                            "name",
                            "nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef31()
                                    + "'");
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "xm", project_name));
                }
                /* 本次付款金额 */
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "bcfkje", temp.getLocal_money_de() + ""));
                /* 资产类别 */
                if (temp.getDef23() != null) {
                    String zclbname = (String) getHyPubBO().findColValue(
                            "tb_budgetsub",
                            "objname",
                            "nvl(dr,0) = 0 and pk_obj  = '" + temp.getDef23()
                                    + "'");
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "zclb", zclbname));
                }
                /* 发票号 */
                if (null != temp.getInvoiceno()) {
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "fph", temp.getInvoiceno()));
                }
                /* 发票类型 */
                if (null != temp.getDef27()) {
                    String fplx = (String) getHyPubBO().findColValue(
                            "bd_defdoc",
                            "name",
                            "nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef27()
                                    + "'");
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "fplx", fplx));
                }
                // 已付金额
                if (temp.getDef3() != null) {
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "def3", temp.getDef3()));
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "yfkje", temp.getDef3()));
                }
                // 总付款比例
                if (temp.getDef8() != null) {
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "zfkbl", temp.getDef8()));
                }
                // 不含税金额
                if (temp.getDef15() != null) {
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "def15", temp.getDef15()));
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "bhsje", temp.getDef15()));
                }
                // 折让金额
                if (temp.getDef28() != null) {
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "def28", temp.getDef28()));
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "zrje", temp.getDef28()));
                }
                /* 付款单行标识 */
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "fkdxbs", temp.getPk_payitem()));
                /* 项目档案 */
                if (temp.getDef24() != null) {
                    String xmda = (String) getHyPubBO().findColValue(
                            "bd_project",
                            "project_name",
                            "nvl(dr,0) = 0 and pk_project  = '"
                                    + temp.getDef24() + "'");
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "xmda", xmda));
                }
            } else if ("F3-Cxx-TSSXSPD".equals(type)) {
                // 特殊事项付款审批
                // 合同号
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("hth",
                        temp.getContractno()));
                // 合同金额
                if (temp.getDef1() != null) {
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "htje", temp.getDef1()));
                }
                /* 本次付款金额 */
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "bcfkje", temp.getLocal_money_de() + ""));
                /* 发票类型 */
                if (null != temp.getDef27()) {
                    String fplx = (String) getHyPubBO().findColValue(
                            "bd_defdoc",
                            "name",
                            "nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef27()
                                    + "'");
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "fplx", fplx));
                }
                /* 已付款金额 */
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "yfkje", temp.getDef3()));

                /* 总付款比例 */
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "zfkbl", temp.getDef8()));
                // 不含税金额
                if (temp.getDef15() != null) {
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "def15", temp.getDef15()));
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "bhsje", temp.getDef15()));
                }
                // 折让金额
                if (temp.getDef28() != null) {
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "def28", temp.getDef28()));
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "zrje", temp.getDef28()));
                }
                /* 备注 */
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bz",
                        temp.getDef13()));
            }
            // 税率 税额
            if ("D3".equals(type) || "F3-Cxx-01".equals(type)) {
                /* 税率 */
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sl",
                        temp.getDef4()));

                /* 税额 */
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("se",
                        temp.getDef5()));
            } else {
                // if ("F3-Cxx-SDRFKSPD".equals(type)
                // || "F3-Cxx-FYFKSPD".equals(type)
                // || "F3-Cxx-GDZC".equals(type)
                // || "F3-Cxx-MTWGRFKSPD".equals(type)
                // || "F3-Cxx-TSSXSPD".equals(type)
                // || "F3-Cxx-RZHK".equals(type))
                /* 税率 */
                if (null != temp.getDef17()) {
                    String sl = (String) getHyPubBO().findColValue(
                            "bd_defdoc",
                            "name",
                            "nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef17()
                                    + "'");
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "sl", sl));
                }
                /* 税额 */
                if (null != temp.getDef19()) {
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "se", temp.getDef19()));
                }
                /* 发票类型 */
                if (null != temp.getDef27()) {
                    String fplx = (String) getHyPubBO().findColValue(
                            "bd_defdoc",
                            "name",
                            "nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef27()
                                    + "'");
                    workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                            "fplx", fplx));
                }
            }
            // 财政性资金
            if (null != temp.getDef49()) {
                String czxzj = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef49()
                                + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "czxzj", czxzj));
            }
            // 其他字段------end
            Map workflowRequestTableFieldsMap = new HashMap();
            workflowRequestTableFieldsMap.put("recordOrder", "0");
            workflowRequestTableFieldsMap.put("workflowRequestTableFields",
                    workflowRequestTableFields);
            workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
        }
        dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
        return dtMap;
    }

    private JSONArray getNewMainMap(PayBillVO parentVO, String supplier,
                                    String recaccount, String payaccount, String pu_deptid)
            throws BusinessException {
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        // 构造数据
        List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
        // ---------其他字段begin
        // 付款方式
        list.add(OaWorkFlowUtil.listAddObj("local_money", parentVO
                .getLocal_money().toString()));
        list.add(OaWorkFlowUtil.listAddObj("yfje", parentVO.getLocal_money()
                .toString()));
        if (parentVO.getDef35() != null) {
            String fkfs = (String) getHyPubBO().findColValue(
                    "bd_defdoc",
                    "name",
                    "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef35()
                            + "'");
            list.add(OaWorkFlowUtil.listAddObj("fkfs", fkfs));
        }
        String fyfsbmname = (String) getHyPubBO().findColValue("org_dept",
                "name", "nvl(dr,0) = 0 and pk_dept = '" + pu_deptid + "'");
        list.add(OaWorkFlowUtil.listAddObj("fyfsbm", fyfsbmname));
        // 代控部门
        if (parentVO.getPk_deptid() != null) {
            if (!"D3".equals(parentVO.getPk_tradetype())
                    && !"F3-Cxx-01".equals(parentVO.getPk_tradetype())) {
                list.add(OaWorkFlowUtil.listAddObj("pk_deptid", parentVO
                        .getPk_deptid().toString()));
                list.add(OaWorkFlowUtil.listAddObj("dkbmzj", parentVO
                        .getPk_deptid().toString()));
                String dkbmbm = (String) getHyPubBO().findColValue(
                        "org_dept",
                        "code",
                        "nvl(dr,0) = 0 and pk_dept ='"
                                + parentVO.getPk_deptid() + "'");
                list.add(OaWorkFlowUtil.listAddObj("dkbmbm", dkbmbm));
                String dkbm = (String) getHyPubBO().findColValue(
                        "org_dept",
                        "name",
                        "nvl(dr,0) = 0 and pk_dept ='"
                                + parentVO.getPk_deptid() + "'");
                list.add(OaWorkFlowUtil.listAddObj("dkbm", dkbm));
                list.add(OaWorkFlowUtil.listAddObj("bm", dkbm));
                list.add(OaWorkFlowUtil.listAddObj("dkbmmc", dkbm));
            }
        }
        FinanceOrgVO orgVO = (FinanceOrgVO) getHyPubBO().queryByPrimaryKey(
                FinanceOrgVO.class, parentVO.getPk_org());
        // 组织名称
        list.add(OaWorkFlowUtil.listAddObj("zzmc", orgVO.getName()));
        list.add(OaWorkFlowUtil.listAddObj("szgs", orgVO.getName()));
        list.add(OaWorkFlowUtil.listAddObj("fkcwzz", orgVO.getName()));
        // 申请部门
        if (parentVO.getDef76() != null) {
            String bm = (String) getHyPubBO()
                    .findColValue(
                            "org_dept",
                            "name",
                            "nvl(dr,0) = 0 and pk_dept = '"
                                    + parentVO.getDef76() + "'");
            list.add(OaWorkFlowUtil.listAddObj("sqbm", bm));
        }

        UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
                parentVO.getCreator());
        // 合同现用名
        if (null != parentVO.getDef4()) {
            String htxym = (String) getHyPubBO().findColValue(
                    "bd_supplier",
                    "name",
                    "nvl(dr,0) = 0 and pk_supplier  = '" + parentVO.getDef4()
                            + "'");
            list.add(OaWorkFlowUtil.listAddObj("htxym", htxym));
        }
        // 交易类型
        String billTypeName = (String) getHyPubBO().findColValue(
                "bd_billtype",
                "billtypename",
                "nvl(dr,0) = 0 and pk_billtypeid  = '"
                        + parentVO.getPk_tradetypeid() + "'");
        list.add(OaWorkFlowUtil.listAddObj("typename", billTypeName));
        /* 往来对象3=业务员，2=部门，1=供应商 */
        if (parentVO.getObjtype() != null) {
            int wldxint = parentVO.getObjtype();
            String wldx = "";
            if (wldxint == 1) {
                wldx = "供应商";
            } else if (wldxint == 2) {
                wldx = "部门";
            } else if (wldxint == 3) {
                wldx = "业务员";
            }
            list.add(OaWorkFlowUtil.listAddObj("wldx", wldx));
        }
        // 供应商
        if (supplier != null && !"".equals(supplier)) {
            String pk_supplier_name = (String) getHyPubBO().findColValue(
                    "bd_supplier", "name",
                    "nvl(dr,0) = 0 and pk_supplier = '" + supplier + "'");
            list.add(OaWorkFlowUtil.listAddObj("pk_supplier_name",
                    pk_supplier_name));
            list.add(OaWorkFlowUtil.listAddObj("gys", pk_supplier_name));
        }
        // 收款银行账户
        String skyhzh = (String) getHyPubBO().findColValue("bd_bankaccsub",
                "accnum",
                "nvl(dr,0) = 0 and pk_bankaccsub = '" + recaccount + "'");
        list.add(OaWorkFlowUtil.listAddObj("skyhzh", skyhzh));
        list.add(OaWorkFlowUtil.listAddObj("skyxzh", skyhzh));
        // 部门
        String bm = (String) getHyPubBO().findColValue(
                "org_dept_v",
                "name",
                "nvl(dr,0) = 0 and pk_vid = '" + parentVO.getPk_deptid_v()
                        + "'");
        list.add(OaWorkFlowUtil.listAddObj("bm", bm));
        // 业务员
        if (null != parentVO.getPk_psndoc()) {
            String ywy = (String) getHyPubBO().findColValue(
                    "bd_psndoc",
                    "name",
                    "nvl(dr,0) = 0 and pk_psndoc = '" + parentVO.getPk_psndoc()
                            + "'");
            list.add(OaWorkFlowUtil.listAddObj("ywy", ywy));
        }
        // 付款银行账户
        String fkyhname = (String) getHyPubBO().findColValue("bd_bankaccsub",
                "accnum",
                "nvl(dr,0) = 0 and pk_bankaccsub = '" + payaccount + "'");
        list.add(OaWorkFlowUtil.listAddObj("fkyhzh", fkyhname));
        // 现金流量项目
        String xname = (String) getHyPubBO().findColValue(
                "bd_cashflow",
                "name",
                "nvl(dr,0) = 0 and pk_cashflow = '" + parentVO.getCashitem()
                        + "'");
        list.add(OaWorkFlowUtil.listAddObj("xjllxm", xname));
        // 结算方式
        String jsfs = (String) getHyPubBO().findColValue(
                "bd_balatype",
                "name",
                "nvl(dr,0) = 0 and pk_balatype = '" + parentVO.getPk_balatype()
                        + "'");
        list.add(OaWorkFlowUtil.listAddObj("jsfs", jsfs));
        // 付款类别
        if (null != parentVO.getDef23()) {
            String fklb = (String) getHyPubBO().findColValue(
                    "bd_defdoc",
                    "name",
                    "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef23()
                            + "'");
            list.add(OaWorkFlowUtil.listAddObj("fklb", fklb));
        }
        // 单据类型
        if (null != parentVO.getDef22()) {
            String djlx = (String) getHyPubBO().findColValue(
                    "bd_defdoc",
                    "name",
                    "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef22()
                            + "'");
            list.add(OaWorkFlowUtil.listAddObj("djlx", djlx));
        }
        // 资金类别
        if (parentVO.getDef77() != null) {
            String zjlb = (String) getHyPubBO().findColValue(
                    "bd_defdoc",
                    "name",
                    "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef77()
                            + "'");
            list.add(OaWorkFlowUtil.listAddObj("zjlb", zjlb));
        }
        // 是否报批财务总监
        if (null != parentVO.getDef52()) {
            String sfbpcwzj = (String) getHyPubBO().findColValue(
                    "bd_defdoc",
                    "name",
                    "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef52()
                            + "'");
            list.add(OaWorkFlowUtil.listAddObj("sfbpcwzj", sfbpcwzj));
        }
        // 申请人
        list.add(OaWorkFlowUtil.listAddObj("sqr", userVO.getUser_name()));
        // ---固定字段
        // 制单人用户编码
        list.add(OaWorkFlowUtil.listAddObj("usercode", userVO.getUser_code()));
        // 制单人名称
        list.add(OaWorkFlowUtil.listAddObj("username", userVO.getUser_name()));
        // 制单人身份证号
        String idCard = (String) getHyPubBO().findColValue("bd_psndoc", "id",
                "nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
        if (null == idCard) {
            throw new BusinessException("制单人身份证号码未维护");
        }
        list.add(OaWorkFlowUtil.listAddObj("userid", idCard));
        // 能投代码
        /* 凭证号 */
        if (null != parentVO.getDef79()) {
            list.add(OaWorkFlowUtil.listAddObj("pzh", parentVO.getDef79()));
        }

        /* 支付日期 */
        if (null != parentVO.getDef33()) {
            list.add(OaWorkFlowUtil.listAddObj("zfrq", parentVO.getDef33()));
        }

        /* CBS状态 */
        if (null != parentVO.getDef34()) {
            list.add(OaWorkFlowUtil.listAddObj("cbszt", parentVO.getDef34()));
        }
        /* 付款单标识 */
        list.add(OaWorkFlowUtil.listAddObj("fkdbs", parentVO.getPk_paybill()));
        // 单据号
        list.add(OaWorkFlowUtil.listAddObj("djh", parentVO.getBillno()));
        // 单据号
        list.add(OaWorkFlowUtil.listAddObj("billno", parentVO.getBillno()));
        // 单据日期
        list.add(OaWorkFlowUtil.listAddObj("djrq", parentVO.getBilldate() + ""));
        // 单据日期
        list.add(OaWorkFlowUtil.listAddObj("billdate", parentVO.getBilldate()
                + ""));
        if ("F3-Cxx-RZHK".equals(parentVO.getPk_tradetype())) {

            list.add(OaWorkFlowUtil.listAddObj("sqdw", orgVO.getName()));
            // 申请人
            list.add(OaWorkFlowUtil.listAddObj("sqr", userVO.getUser_name()));
            /* 转账银行 */
            if (null != parentVO.getDef24()) {
                String zzyh = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef24()
                                + "'");
                list.add(OaWorkFlowUtil.listAddObj("zzyx", zzyh));
                list.add(OaWorkFlowUtil.listAddObj("zzyh", zzyh));
            }
            if (null != parentVO.getDef3()) {
                String fklx = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc  ='" + parentVO.getDef3()
                                + "'");
                list.add(OaWorkFlowUtil.listAddObj("fklx", fklx));
            }
            /* 承兑银行 */
            if (null != parentVO.getDef25()) {
                String cdyh = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef25()
                                + "'");
                list.add(OaWorkFlowUtil.listAddObj("cdyx", cdyh));
                list.add(OaWorkFlowUtil.listAddObj("cdyh", cdyh));
            }
            // 承兑金额
            if (null != parentVO.getDef27()) {
                list.add(OaWorkFlowUtil.listAddObj("def27", parentVO.getDef27()));
            }
            // 附件张数
            if (null != parentVO.getAccessorynum()) {
                list.add(OaWorkFlowUtil.listAddObj("accessorynum",
                        parentVO.getAccessorynum() + ""));
            }
        } else if ("D3".equals(parentVO.getPk_tradetype())
                || "F3-Cxx-01".equals(parentVO.getPk_tradetype())) {
            // 开户行
            list.add(OaWorkFlowUtil.listAddObj("khh",
                    getGYSKHH(parentVO.getPk_tradetype(), recaccount)));
            /* 核算人员 */
            if (null != parentVO.getDef13()) {
                String hsry = (String) getHyPubBO().findColValue(
                        "bd_psndoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_psndoc  = '"
                                + parentVO.getDef13() + "'");
                list.add(OaWorkFlowUtil.listAddObj("hsry", hsry));
            }
            /* 付款类型 */
            if (null != parentVO.getPk_tradetypeid()) {
                String fkname = (String) getHyPubBO().findColValue(
                        "bd_billtype",
                        "billtypename",
                        "nvl(dr,0) = 0 and pk_billtypeid  = '"
                                + parentVO.getPk_tradetypeid() + "'");
                list.add(OaWorkFlowUtil.listAddObj("fklx", fkname));
            }
            /* 承兑金额 */
            if (null != parentVO.getDef31()) {
                list.add(OaWorkFlowUtil.listAddObj(
                        "cdje",
                        new UFDouble(parentVO.getDef31() == null ? "0.00"
                                : parentVO.getDef31()).setScale(2,
                                UFDouble.ROUND_HALF_UP)
                                + ""));
            }
            /* 付款类别 */
            if (null != parentVO.getDef23()) {
                String fklb = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef23()
                                + "'");
                list.add(OaWorkFlowUtil.listAddObj("fklb", fklb));
            }
            /* 分管领导 */
            if (null != parentVO.getDef21()) {
                String fgld = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef21()
                                + "'");
                list.add(OaWorkFlowUtil.listAddObj("fgld", fgld));
            }
            // 代控部门
            if (parentVO.getDef20() != null) {
                list.add(OaWorkFlowUtil.listAddObj("pk_deptid", parentVO
                        .getDef20().toString()));
                list.add(OaWorkFlowUtil.listAddObj("dkbmzj", parentVO
                        .getDef20().toString()));
                String dkbmbm = (String) getHyPubBO().findColValue(
                        "org_dept",
                        "code",
                        "nvl(dr,0) = 0 and pk_dept ='" + parentVO.getDef20()
                                + "'");
                list.add(OaWorkFlowUtil.listAddObj("dkbmbm", dkbmbm));
                String dkbm = (String) getHyPubBO().findColValue(
                        "org_dept",
                        "name",
                        "nvl(dr,0) = 0 and pk_dept ='" + parentVO.getDef20()
                                + "'");
                list.add(OaWorkFlowUtil.listAddObj("dkbm", dkbm));
                ;
            }
            /* 合同总金额 */
            if (null != parentVO.getDef70()) {
                list.add(OaWorkFlowUtil.listAddObj("htzje", parentVO.getDef70()));
            }

            /* 实付金额 */
            if (null != parentVO.getDef26()) {
                list.add(OaWorkFlowUtil.listAddObj("sfje", parentVO.getDef26()));
            }
            /* 本次付款比例 */
            if (null != parentVO.getDef37()) {
                list.add(OaWorkFlowUtil.listAddObj("bcfkbl",
                        parentVO.getDef37()));
            }

            /* 已付款金额 */
            if (null != parentVO.getDef71()) {
                list.add(OaWorkFlowUtil.listAddObj("yfkje", parentVO.getDef71()));
            }
            /* 已付款比例 */
            if (null != parentVO.getDef38()) {
                list.add(OaWorkFlowUtil.listAddObj("yfkbl", parentVO.getDef38()));
            }
            /* 累计付款金额 */
            if (null != parentVO.getDef40()) {
                list.add(OaWorkFlowUtil.listAddObj("ljfkje",
                        parentVO.getDef40()));
            }
            if (null != parentVO.getDef39()) {
                /* 累计付款比例 */
                list.add(OaWorkFlowUtil.listAddObj("ljfkbl",
                        parentVO.getDef39()));
            }
            /* 已开发票金额 */
            if (null != parentVO.getDef72()) {
                list.add(OaWorkFlowUtil.listAddObj("ykfpje",
                        parentVO.getDef72()));
            }
            /* 户号/编码 */
            if (null != parentVO.getDef69()) {
                list.add(OaWorkFlowUtil.listAddObj("hhbm", parentVO.getDef69()));
            }
            /* 倍率（电） */
            if (null != parentVO.getDef70()) {
                list.add(OaWorkFlowUtil.listAddObj("bl", parentVO.getDef70()));
            }
            /* 转账银行 */
            if (null != parentVO.getDef24()) {
                String zzyh = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef24()
                                + "'");
                list.add(OaWorkFlowUtil.listAddObj("zzyh", zzyh));
                list.add(OaWorkFlowUtil.listAddObj("zzyx", zzyh));
            }
            /* 承兑银行 */
            if (null != parentVO.getDef25()) {
                String cdyh = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef25()
                                + "'");
                list.add(OaWorkFlowUtil.listAddObj("cdyh", cdyh));
                list.add(OaWorkFlowUtil.listAddObj("cdyx", cdyh));
            }
            /* 折让金额 */
            if (null != parentVO.getDef32()) {
                list.add(OaWorkFlowUtil.listAddObj("zrje", parentVO.getDef32()));
            }
            /* 付款事由 */
            if (null != parentVO.getDef30()) {
                list.add(OaWorkFlowUtil.listAddObj("fksy", parentVO.getDef30()));
            }
            /* 附件张数 */
            if (null != parentVO.getAccessorynum()) {
                list.add(OaWorkFlowUtil.listAddObj("fjzs",
                        parentVO.getAccessorynum() + ""));
            }

            if ("D3".equals(parentVO.getPk_tradetype())) {
                /* 付款合同 */
                if (null != parentVO.getPk_contractno()) {
                    list.add(OaWorkFlowUtil.listAddObj("fkht",
                            parentVO.getPk_contractno()));
                }

                /* NC发票挂账金额 */
                if (null != parentVO.getDef42()) {
                    list.add(OaWorkFlowUtil.listAddObj("ncfpgzje",
                            parentVO.getDef42()));
                }
                /* NC累计付款金额 */
                if (null != parentVO.getDef43()) {
                    list.add(OaWorkFlowUtil.listAddObj("ncljfkje",
                            parentVO.getDef43()));
                }
                /* NC欠款金额 */
                if (null != parentVO.getDef44()) {
                    list.add(OaWorkFlowUtil.listAddObj("ncqkje",
                            parentVO.getDef44()));
                }
                /* 付款财务组织 */
                if (null != orgVO.getName()) {
                    list.add(OaWorkFlowUtil.listAddObj("fkcwzz",
                            orgVO.getName()));
                }
                // 是否原OA单据
                if (parentVO.getDef69() != null) {
                    String sfyoadj = (String) getHyPubBO().findColValue(
                            "bd_defdoc",
                            "name",
                            "nvl(dr,0) = 0 and pk_defdoc  = '"
                                    + parentVO.getDef69() + "'");
                    list.add(OaWorkFlowUtil.listAddObj("sfyoadj", sfyoadj));
                }
            } else {
                /* 审计金额 */
                if (null != parentVO.getDef41()) {
                    list.add(OaWorkFlowUtil.listAddObj("sjje",
                            parentVO.getDef41()));
                }
            }
            /* 工程类别 */
            if (null != parentVO.getDef75()) {
                String gclb = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef75()
                                + "'");
                list.add(OaWorkFlowUtil.listAddObj("gclb", gclb));
            }
        } else if ("F3-Cxx-FYFKSPD".equals(parentVO.getPk_tradetype())
                || "F3-Cxx-SDRFKSPD".equals(parentVO.getPk_tradetype())
                || "F3-Cxx-MTWGRFKSPD".equals(parentVO.getPk_tradetype())
                || "F3-Cxx-SWZRFKSPD".equals(parentVO.getPk_tradetype())
                || "F3-Cxx-WGRFKSPD".equals(parentVO.getPk_tradetype())) {
            // 费用付款单// 水电付款单 // 煤炭付款单 //生物质燃料
            // 开户行
            list.add(OaWorkFlowUtil.listAddObj("khh",
                    getGYSKHH(parentVO.getPk_tradetype(), recaccount)));
            /* 转账银行 */
            if (null != parentVO.getDef24()) {
                String zzyh = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef24()
                                + "'");
                list.add(OaWorkFlowUtil.listAddObj("zzyx", zzyh));
                list.add(OaWorkFlowUtil.listAddObj("zzyh", zzyh));
            }
            /* 承兑银行 */
            if (null != parentVO.getDef25()) {
                String cdyh = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef25()
                                + "'");
                list.add(OaWorkFlowUtil.listAddObj("cdyx", cdyh));
                list.add(OaWorkFlowUtil.listAddObj("cdyh", cdyh));
            }
            /* 承兑金额 */
            if (null != parentVO.getDef27()) {
                list.add(OaWorkFlowUtil.listAddObj(
                        "cdje",
                        new UFDouble(parentVO.getDef27() == null ? "0.00"
                                : parentVO.getDef27()).setScale(2,
                                UFDouble.ROUND_HALF_UP)
                                + ""));
            }
            /* 付款事由 */
            if (null != parentVO.getDef2()) {
                list.add(OaWorkFlowUtil.listAddObj("fksy", parentVO.getDef2()));
            }
            if (null != orgVO.getName()) {
                list.add(OaWorkFlowUtil.listAddObj("sqdw", orgVO.getName()));
            }
            // 费用付款单
            if ("F3-Cxx-FYFKSPD".equals(parentVO.getPk_tradetype())) {
                /* 实付金额 */
                if (null != parentVO.getDef26()) {
                    list.add(OaWorkFlowUtil.listAddObj(
                            "sfje",
                            new UFDouble(parentVO.getDef26() == null ? "0.00"
                                    : parentVO.getDef26()).setScale(2,
                                    UFDouble.ROUND_HALF_UP)
                                    + ""));
                }
                /* 折让金额 */
                if (null != parentVO.getDef31()) {
                    list.add(OaWorkFlowUtil.listAddObj(
                            "zrje",
                            new UFDouble(parentVO.getDef31() == null ? "0.00"
                                    : parentVO.getDef31()).setScale(2,
                                    UFDouble.ROUND_HALF_UP)
                                    + ""));
                }
                // 能投 费用付款审批单 新增收支项目 关联自定义档案
                if (parentVO.getDef80() != null) {
                    /* 收支项目 */
                    String szxm = (String) getHyPubBO().findColValue(
                            "bd_defdoc",
                            "name",
                            "nvl(dr,0) = 0 and pk_defdoc = '"
                                    + parentVO.getDef80() + "'");
                    list.add(OaWorkFlowUtil.listAddObj("szxm", szxm + ""));
                }
                if (null != parentVO.getAccessorynum()) {
                    list.add(OaWorkFlowUtil.listAddObj("fjzs",
                            parentVO.getAccessorynum() + ""));
                }
            }
            // 水电燃付款单
            if ("F3-Cxx-SDRFKSPD".equals(parentVO.getPk_tradetype())) {
                /* 支付金额 */
                if (null != parentVO.getLocal_money()) {
                    list.add(OaWorkFlowUtil.listAddObj("zfje",
                            parentVO.getLocal_money() + ""));
                }
                /* 备注 */
                list.add(OaWorkFlowUtil.listAddObj("bz", parentVO.getDef2()));
                // 付款类型
                if (null != parentVO.getDef3()) {
                    String fklx = (String) getHyPubBO().findColValue(
                            "bd_defdoc",
                            "name",
                            "nvl(dr,0) = 0 and pk_defdoc = '"
                                    + parentVO.getDef3() + "'");
                    list.add(OaWorkFlowUtil.listAddObj("fklx", fklx));
                }
                /* 承兑金额 */
                if (null != parentVO.getDef27()) {
                    list.add(OaWorkFlowUtil.listAddObj(
                            "cdje",
                            new UFDouble(parentVO.getDef27() == null ? "0.00"
                                    : parentVO.getDef27()).setScale(2,
                                    UFDouble.ROUND_HALF_UP)
                                    + ""));
                }
                /* 付款事由 */
                if (null != parentVO.getDef2()) {
                    list.add(OaWorkFlowUtil.listAddObj("fksy",
                            parentVO.getDef2()));
                }
                /* 转账银行 */
                if (null != parentVO.getDef24()) {
                    String zzyh = (String) getHyPubBO().findColValue(
                            "bd_defdoc",
                            "name",
                            "nvl(dr,0) = 0 and pk_defdoc = '"
                                    + parentVO.getDef24() + "'");
                    list.add(OaWorkFlowUtil.listAddObj("zzyx", zzyh));
                    list.add(OaWorkFlowUtil.listAddObj("zzyh", zzyh));
                }
                /* 承兑银行 */
                if (null != parentVO.getDef25()) {
                    String cdyh = (String) getHyPubBO().findColValue(
                            "bd_defdoc",
                            "name",
                            "nvl(dr,0) = 0 and pk_defdoc = '"
                                    + parentVO.getDef25() + "'");
                    list.add(OaWorkFlowUtil.listAddObj("cdyx", cdyh));
                    list.add(OaWorkFlowUtil.listAddObj("cdyh", cdyh));
                }

            }
            // 煤炭外热购
            if ("F3-Cxx-MTWGRFKSPD".equals(parentVO.getPk_tradetype()) || "F3-Cxx-WGRFKSPD".equals(parentVO.getPk_tradetype())) {
                /* 合同名称 */
                if (null != parentVO.getDef80()) {
                    list.add(OaWorkFlowUtil.listAddObj("htmc",
                            parentVO.getDef80()));
                }
                /*
                 * 金额大写 Map jedx = OaWorkFlowUtil.listAddObj("jedx",
                 * ConvertUpMoney .toChinese(parentVO.getLocal_money() + ""));
                 * list.add(jedx);
                 */

                /* 合同编码 */
                if (null != parentVO.getDef3()) {
                    list.add(OaWorkFlowUtil.listAddObj("htbm",
                            parentVO.getDef3()));
                }
                /* 应付总金额 */
                if (null != parentVO.getDef7()) {
                    list.add(OaWorkFlowUtil.listAddObj(
                            "yfzje",
                            new UFDouble(parentVO.getDef7() == null ? "0.00"
                                    : parentVO.getDef7()).setScale(2,
                                    UFDouble.ROUND_HALF_UP)
                                    + ""));
                }

                /* 发票总金额 */
                if (null != parentVO.getDef8()) {
                    list.add(OaWorkFlowUtil.listAddObj(
                            "fpzje",
                            new UFDouble(parentVO.getDef8() == null ? "0.00"
                                    : parentVO.getDef8()).setScale(2,
                                    UFDouble.ROUND_HALF_UP)
                                    + ""));
                }

                /* 已付金额 */
                if (null != parentVO.getDef12()) {
                    list.add(OaWorkFlowUtil.listAddObj(
                            "yfje1",
                            new UFDouble(parentVO.getDef12() == null ? "0.00"
                                    : parentVO.getDef12()).setScale(2,
                                    UFDouble.ROUND_HALF_UP)
                                    + ""));
                }
                /* 已付款比例 */
                if (null != parentVO.getDef14()) {
                    list.add(OaWorkFlowUtil.listAddObj("yfkbl",
                            parentVO.getDef14()));
                }

                /* 转账金额 */
                if (null != parentVO.getDef26()) {
                    list.add(OaWorkFlowUtil.listAddObj(
                            "zzje",
                            new UFDouble(parentVO.getDef26() == null ? "0.00"
                                    : parentVO.getDef26()).setScale(2,
                                    UFDouble.ROUND_HALF_UP)
                                    + ""));
                }
                /* 支付金额 */
                if (null != parentVO.getLocal_money()) {
                    list.add(OaWorkFlowUtil.listAddObj("zfje",
                            parentVO.getLocal_money() + ""));
                }
                /* 附件张数 */
                if (null != parentVO.getAccessorynum()) {
                    list.add(OaWorkFlowUtil.listAddObj("fjzs",
                            parentVO.getAccessorynum() + ""));
                }
                /* 付款财务组织 */
                if (null != orgVO.getName()) {
                    list.add(OaWorkFlowUtil.listAddObj("fkcwzz",
                            orgVO.getName()));
                }

            }
        } else if ("F3-Cxx-GDZC".equals(parentVO.getPk_tradetype())) {
            // 资产购置付款单
            list.add(OaWorkFlowUtil.listAddObj("sqdw", orgVO.getName()));
            // 开户行
            if (null != parentVO.getRecaccount()) {
                list.add(OaWorkFlowUtil.listAddObj("khh",
                        getGYSKHH(parentVO.getPk_tradetype(), recaccount)));
            }
            /* 承兑金额 */
            if (null != parentVO.getDef27()) {
                list.add(OaWorkFlowUtil.listAddObj(
                        "cdje",
                        new UFDouble(parentVO.getDef27() == null ? "0.00"
                                : parentVO.getDef27()).setScale(2,
                                UFDouble.ROUND_HALF_UP)
                                + ""));
            }

            /* 付款事由 */
            if (null != parentVO.getDef2()) {
                list.add(OaWorkFlowUtil.listAddObj("fksy", parentVO.getDef2()));
            }

            /* 折让金额 */
            if (null != parentVO.getDef31()) {
                list.add(OaWorkFlowUtil.listAddObj(
                        "zrje",
                        new UFDouble(parentVO.getDef31() == null ? "0.00"
                                : parentVO.getDef31()).setScale(2,
                                UFDouble.ROUND_HALF_UP)
                                + ""));
            }
            /* 实付金额 */
            if (null != parentVO.getDef26()) {
                list.add(OaWorkFlowUtil.listAddObj(
                        "sfje",
                        new UFDouble(parentVO.getDef26() == null ? "0.00"
                                : parentVO.getDef26()).setScale(2,
                                UFDouble.ROUND_HALF_UP)
                                + ""));
            }
            /* 转账银行 */
            if (null != parentVO.getDef24()) {
                String zzyh = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef24()
                                + "'");
                list.add(OaWorkFlowUtil.listAddObj("zzyx", zzyh));
                list.add(OaWorkFlowUtil.listAddObj("zzyh", zzyh));
            }
            /* 承兑银行 */
            if (null != parentVO.getDef25()) {
                String cdyh = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef25()
                                + "'");
                list.add(OaWorkFlowUtil.listAddObj("cdyh", cdyh));
                list.add(OaWorkFlowUtil.listAddObj("cdyx", cdyh));
            }
            /* 附件张数 */
            if (null != parentVO.getAccessorynum()) {
                list.add(OaWorkFlowUtil.listAddObj("fjzs",
                        parentVO.getAccessorynum() + ""));
            }
        } else if ("F3-Cxx-TSSXSPD".equals(parentVO.getPk_tradetype())) {
            // 特殊事项审批付款单
            // 开户行
            if (null != parentVO.getRecaccount()) {
                list.add(OaWorkFlowUtil.listAddObj("khh",
                        getGYSKHH(parentVO.getPk_tradetype(), recaccount)));
            }
            list.add(OaWorkFlowUtil.listAddObj("sqdw", orgVO.getName()));
            // 承兑金额
            if (null != parentVO.getDef27()) {
                list.add(OaWorkFlowUtil.listAddObj(
                        "cdje",
                        new UFDouble(parentVO.getDef27() == null ? "0.00"
                                : parentVO.getDef27()).setScale(2,
                                UFDouble.ROUND_HALF_UP)
                                + ""));
            }
            /* 付款事由 */
            if (null != parentVO.getDef2()) {
                list.add(OaWorkFlowUtil.listAddObj("fksy", parentVO.getDef2()));
            }
            /* 折让金额 */
            if (null != parentVO.getDef31()) {
                list.add(OaWorkFlowUtil.listAddObj(
                        "zrje",
                        new UFDouble(parentVO.getDef31() == null ? "0.00"
                                : parentVO.getDef31()).setScale(2,
                                UFDouble.ROUND_HALF_UP)
                                + ""));
            }
            /* 实付金额 */
            if (null != parentVO.getDef26()) {
                list.add(OaWorkFlowUtil.listAddObj(
                        "sfje",
                        new UFDouble(parentVO.getDef26() == null ? "0.00"
                                : parentVO.getDef26()).setScale(2,
                                UFDouble.ROUND_HALF_UP)
                                + ""));
            }
            /* 转账银行 */
            if (null != parentVO.getDef24()) {
                String zzyh = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef24()
                                + "'");
                list.add(OaWorkFlowUtil.listAddObj("zzyh", zzyh));
                list.add(OaWorkFlowUtil.listAddObj("zzyx", zzyh));
            }
            /* 承兑银行 */
            if (null != parentVO.getDef25()) {
                String cdyh = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef25()
                                + "'");
                list.add(OaWorkFlowUtil.listAddObj("cdyh", cdyh));
                list.add(OaWorkFlowUtil.listAddObj("cdyx", cdyh));
            }
            /* 附件张数 */
            if (null != parentVO.getAccessorynum()) {
                list.add(OaWorkFlowUtil.listAddObj("fjzs",
                        parentVO.getAccessorynum() + ""));
            }
            // 付款类型
            if (null != parentVO.getDef55()) {
                String fklx = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef55()
                                + "'");
                list.add(OaWorkFlowUtil.listAddObj("fklx", fklx));
            }

        }
        // 财政性资金
        /*
         * if (null != parentVO.getDef62()) { String czxzj = (String)
         * getHyPubBO().findColValue( "bd_defdoc", "name",
         * "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef62() + "'");
         * list.add(OaWorkFlowUtil.listAddObj("czxzj", czxzj)); }
         */
        // 资金性质
        if (null != parentVO.getDef63()) {
            String zjxz = (String) getHyPubBO().findColValue(
                    "bd_defdoc",
                    "name",
                    "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef63()
                            + "'");
            list.add(OaWorkFlowUtil.listAddObj("zjxz", zjxz));
        }
        // 区县
        if (null != parentVO.getDef64()) {
            String qx = (String) getHyPubBO().findColValue(
                    "bd_defdoc",
                    "name",
                    "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef64()
                            + "'");
            list.add(OaWorkFlowUtil.listAddObj("qx", qx));
        }
        // 事由填写规范
        if (null != parentVO.getDef48()) {
            String sytxgf = (String) getHyPubBO().findColValue(
                    "bd_defdoc",
                    "name",
                    "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef48()
                            + "'");
            list.add(OaWorkFlowUtil.listAddObj("sytxgf", sytxgf));
        }
        // 业务板块
        if (null != parentVO.getDef49()) {
            String ywbk = (String) getHyPubBO().findColValue(
                    "bd_defdoc",
                    "name",
                    "nvl(dr,0) = 0 and pk_defdoc = '" + parentVO.getDef49()
                            + "'");
            list.add(OaWorkFlowUtil.listAddObj("ywbk", ywbk));
        }
        // ---------其他字段end
        // JSONArray arr = JSONArray.fromObject(list);
        JSONArray arr = JSONArray.fromObject(list, jsonConfig);
        return arr;
    }

    // 获取供应商开户行
    private String getGYSKHH(String transi_type, String account)
            throws BusinessException {
        String gyskhh = "";
        String sql = (String) getHyPubBO().findColValue("sys_config",
                "config_value",
                "nvl(dr,0) = 0 and config_key = '" + transi_type + "'");
        sql += " '" + account + "'))";
        List<Object[]> ls = NCLocator.getInstance().lookup(GetDao.class)
                .query(sql);
        if (ls != null && ls.size() > 0 && ls.get(0) != null) {
            gyskhh = ls.get(0)[0] + "";
        }
        return gyskhh;
    }

    private Map<String, String> billTypeMap() {
        Map<String, String> typeMap = new HashMap<String, String>();

        String JH_CLFK = "D3";
        typeMap.put(JH_CLFK, "材料付款单");

        String JH_GCFK = "F3-Cxx-01";
        typeMap.put(JH_GCFK, "工程付款单");

        String JH_FYFK = "F3-Cxx-FYFKSPD";
        typeMap.put(JH_FYFK, "费用付款审批单");
        return typeMap;
    }

    private JSONArray getRZDtaileDataMap(PayBillItemVO[] bvos, PayBillVO headVO)
            throws BusinessException {
        Map dtMap = new HashMap();
        if ("2".equals(getDef2(headVO.getPk_org()))) {
            dtMap.put("tableDBName", "formtable_main_625_dt1");
        } else {
            dtMap.put("tableDBName", "formtable_main_383_dt1");
        }
        List workflowRequestTableRecords = new ArrayList();
        for (PayBillItemVO temp : bvos) {
            List workflowRequestTableFields = new ArrayList();
            Map workflowRequestTableFieldsMap = new HashMap();
            // 预算类别
            String yslbname = (String) getHyPubBO().findColValue(
                    "bd_inoutbusiclass",
                    "name",
                    "nvl(dr,0) = 0 and pk_inoutbusiclass = '"
                            + temp.getPk_subjcode() + "'");
            workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("yslb",
                    yslbname));
            // 费用发生部门
            String fyfsbmname = (String) getHyPubBO().findColValue(
                    "org_dept",
                    "name",
                    "nvl(dr,0) = 0 and  pk_dept = '" + temp.getPu_deptid()
                            + "'");
            workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("fyfsbm",
                    fyfsbmname));
            // 合同号
            workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                    "contractno", temp.getContractno()));
            // 合同金额
            if (temp.getDef1() != null) {
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "def1", temp.getDef1()));
            }
            // 本次付款金额
            workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                    "local_money_de", temp.getLocal_money_de() + ""));
            /* 发票类型 */
            if (null != temp.getDef27()) {
                String fplx = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef27()
                                + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "fplx", fplx));
            }

            // 部门
            String bmname = (String) getHyPubBO()
                    .findColValue(
                            "org_dept",
                            "name",
                            "nvl(dr,0) = 0 and pk_dept = '"
                                    + temp.getPk_deptid() + "'");
            workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("bm",
                    bmname));
            // 已付金额
            if (temp.getDef3() != null) {
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "def3", temp.getDef3()));
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "yfkje", temp.getDef3()));
            }
            // 总付款比例
            if (temp.getDef8() != null) {
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "zfkbl", temp.getDef8()));
            }
            /* 税率 */
            if (null != temp.getDef17()) {
                String sl = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef17()
                                + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sl",
                        sl));
            }

            // 税额
            if (temp.getDef19() != null) {
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "def19", temp.getDef19()));
            }
            // 不含税金额
            if (temp.getDef15() != null) {
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "def15", temp.getDef15()));
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "bhsje", temp.getDef15()));
            }
            // 折让金额
            if (temp.getDef28() != null) {
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "def28", temp.getDef28()));
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "zrje", temp.getDef28()));
            }

            if (temp.getProject() != null) {
                /* 项目编码 */
                String code = (String) getHyPubBO().findColValue(
                        "bd_project",
                        "project_code",
                        "nvl(dr,0) = 0 and pk_project = '" + temp.getProject()
                                + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "bproject_code", code));

                /* 项目名称 */
                String name = (String) getHyPubBO().findColValue(
                        "bd_project",
                        "project_name",
                        "nvl(dr,0) = 0 and pk_project  = '" + temp.getProject()
                                + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "bproject_name", name));
            }
            // 资金来源
            if (temp.getDef21() != null) {
                String zjly = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef21()
                                + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "zjly", zjly));
            }
            // 资金类别
            if (temp.getDef20() != null) {
                String zjlb = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef20()
                                + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "zjlb", zjlb));
            }
            // 备注
            if (temp.getDef13() != null) {
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "def13", temp.getDef13()));
            }
            workflowRequestTableFieldsMap.put("recordOrder", "0");
            workflowRequestTableFieldsMap.put("workflowRequestTableFields",
                    workflowRequestTableFields);
            workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
        }
        dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
        JSONArray dtlistString = JSONArray.fromObject(dtMap);
        return dtlistString;
    }

    // 融资还款主表
    private JSONArray getRZMainMap(PayBillVO headVO, String supplier)
            throws BusinessException {
        List list = new ArrayList();
        UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
                headVO.getBillmaker());
        // 制单人用户编码
        list.add(OaWorkFlowUtil.listAddObj("usercode", userVO.getUser_code()));
        // 制单人名称
        list.add(OaWorkFlowUtil.listAddObj("username", userVO.getUser_name()));
        // 制单人身份证号
        String idCard = (String) getHyPubBO().findColValue("bd_psndoc", "id",
                "nvl(dr,0) = 0 and pk_psndoc='" + userVO.getPk_psndoc() + "'");
        if (null == idCard) {
            throw new BusinessException("制单人身份证号码未维护");
        }
        list.add(OaWorkFlowUtil.listAddObj("userid", idCard));
        OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(OrgVO.class,
                headVO.getPk_org());
        // 组织名称
        list.add(OaWorkFlowUtil.listAddObj("zzmc", orgVO.getName()));
        // 单据号
        list.add(OaWorkFlowUtil.listAddObj("billno", headVO.getBillno()));
        // 单据日期
        list.add(OaWorkFlowUtil.listAddObj("billdate", headVO.getBilldate()
                + ""));
        // 单据日期
        list.add(OaWorkFlowUtil.listAddObj("billdate", headVO.getBilldate()
                + ""));
        // 申请单位
        String dw = (String) getHyPubBO().findColValue(
                "org_financeorg",
                "name",
                "nvl(dr,0) = 0 and pk_financeorg   = '" + headVO.getPk_org()
                        + "'");
        list.add(OaWorkFlowUtil.listAddObj("sqdw", dw));
        // list.add(OaWorkFlowUtil.listAddObj("pk_org", dw));

        // 申请部门
        String bm = (String) getHyPubBO().findColValue("org_dept", "name",
                "nvl(dr,0) = 0 and pk_dept  = '" + headVO.getDef76() + "'");
        list.add(OaWorkFlowUtil.listAddObj("sqbm", bm));
        // 申请人
        list.add(OaWorkFlowUtil.listAddObj("sqr", userVO.getUser_name()));

        /* 代控部门编码 */
        String dkbmbmstr = (String) getHyPubBO().findColValue("org_dept",
                "code",
                "nvl(dr,0) = 0 and pk_dept  = '" + headVO.getPk_deptid() + "'");
        Map dkbmbm = OaWorkFlowUtil.listAddObj("dkbmbm", dkbmbmstr);
        list.add(dkbmbm);

        /* 代控部门 */
        String dk = (String) getHyPubBO().findColValue("org_dept", "name",
                "nvl(dr,0) = 0 and pk_dept  = '" + headVO.getPk_deptid() + "'");
        Map dkbm = OaWorkFlowUtil.listAddObj("dkbmmc", dk);
        list.add(dkbm);

        /* 供应商 */
        String name = "0";
        if (null != headVO.getSupplier()) {
            name = (String) getHyPubBO().findColValue(
                    "bd_supplier",
                    "name",
                    "nvl(dr,0) = 0 and  pk_supplier  = '"
                            + headVO.getSupplier() + "'");
        }
        list.add(OaWorkFlowUtil.listAddObj("pk_supplier_name", name));
        /* 收款银行账户 */
        String yhname = (String) getHyPubBO().findColValue(
                "bd_bankaccsub",
                "accnum",
                "nvl(dr,0) = 0 and pk_bankaccsub  = '" + headVO.getRecaccount()
                        + "'");
        Map skyxzh = OaWorkFlowUtil.listAddObj("skyhzh", yhname);
        list.add(skyxzh);

        // 合同现用名
        String def4 = "0";
        if (null != headVO.getDef4()) {
            def4 = (String) getHyPubBO().findColValue(
                    "bd_supplier",
                    "name",
                    "nvl(dr,0) = 0 and pk_supplier  = '" + headVO.getDef4()
                            + "'");
        }
        list.add(OaWorkFlowUtil.listAddObj("htxym", def4));
        /* 付款方式 */
        String def35 = "0";
        if (null != headVO.getDef35()) {
            String strWhere = "pk_defdoc = '" + headVO.getDef35()
                    + "' and nvl(dr,0) = 0 ";
            DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
                    DefdocVO.class, strWhere);
            if (null != defdoc && defdoc.length == 1) {
                def35 = defdoc[0].getName();
            }
        }
        list.add(OaWorkFlowUtil.listAddObj("fkfs", def35));
        // 应付金额
        list.add(OaWorkFlowUtil.listAddObj("local_money",
                headVO.getLocal_money() + ""));
        // 折让金额
        list.add(OaWorkFlowUtil.listAddObj("def31", headVO.getDef31()));
        // 实付金额
        list.add(OaWorkFlowUtil.listAddObj("def26", headVO.getDef26()));
        /* 转账银行 */
        if (null != headVO.getDef24()) {
            String where = " pk_defdoc = '"
                    + headVO.getDef25()
                    + "' and pk_defdoclist in (select pk_defdoclist from bd_defdoclist where code = 'YH' and nvl(dr," +
                    "0) = 0) and nvl(dr,0) = 0";
            String def24 = "0";
            if (null != (String) getHyPubBO().findColValue("bd_defdoc", "name",
                    where)) {
                def24 = (String) getHyPubBO().findColValue("bd_defdoc", "name",
                        where);
            }
            list.add(OaWorkFlowUtil.listAddObj("zzyh", def24));
        }
        // 实付金额大写
        // if (null != headVO.getDef26() && !"~".equals(headVO.getDef26()) ) {
        // // list.add(OaWorkFlowUtil.listAddObj("def30", headVO.getDef30()));
        // 1107修改金额大写
        // list.add(OaWorkFlowUtil.listAddObj("def30", ConvertUpMoney
        // .toChinese(headVO.getDef26() + "")));
        // }
        /* 承兑银行 */
        String def25 = "0";
        if (null != headVO.getDef25()) {
            String strWhere = "pk_defdoc = '" + headVO.getDef25()
                    + "' and nvl(dr,0) = 0 ";
            DefdocVO[] defdoc = (DefdocVO[]) getHyPubBO().queryByCondition(
                    DefdocVO.class, strWhere);
            if (null != defdoc && defdoc.length == 1) {
                def25 = defdoc[0].getName();
            }
        }
        list.add(OaWorkFlowUtil.listAddObj("cdyh", def25));
        // 承兑金额
        list.add(OaWorkFlowUtil.listAddObj("def27", headVO.getDef27()));
        // 附件张数
        list.add(OaWorkFlowUtil.listAddObj("accessorynum",
                headVO.getAccessorynum() + ""));
        /* 付款款银行账户 */
        String fkyhname = (String) getHyPubBO().findColValue(
                "bd_bankaccsub",
                "accnum",
                "nvl(dr,0) = 0 and pk_bankaccsub = '" + headVO.getPayaccount()
                        + "'");
        list.add(OaWorkFlowUtil.listAddObj("fkyhzh", fkyhname));
        /* 现金流量项目 */
        String xname = (String) getHyPubBO().findColValue(
                "bd_cashflow",
                "name",
                "nvl(dr,0) = 0 and pk_cashflow  = '" + headVO.getCashitem()
                        + "'");
        list.add(OaWorkFlowUtil.listAddObj("xjllxm", xname));
        // 付款事由
        list.add(OaWorkFlowUtil.listAddObj("def2", headVO.getDef2()));
        // 支付日期
        list.add(OaWorkFlowUtil.listAddObj("def33", headVO.getDef33()));
        // CBS状态
        list.add(OaWorkFlowUtil.listAddObj("def34", headVO.getDef34()));

        JSONArray arr = JSONArray.fromObject(list);
        return arr;
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

    /**
     * 材料付款单//工程付款单/费用付款//水电//煤炭 能投 ：特殊事项付款单
     *
     * @param temp
     * @return
     * @throws BusinessException
     */
    private WorkFlowBill getWorkFlowBill(AggregatedValueObject temp)
            throws BusinessException {
        String where = " nvl(dr,0) = 0 and pk_bill = '"
                + ((PayBillVO) temp.getParentVO()).getPk_paybill() + "'";
        WorkFlowBill[] workFlowBills = (WorkFlowBill[]) getHyPubBO()
                .queryByCondition(WorkFlowBill.class, where);
        WorkFlowBill workFlowBill = new WorkFlowBill();
        if (null != workFlowBills && workFlowBills.length > 1) {
            throw new BusinessException("查询到多条流程记录");
        }
        PayBillVO hvo = (PayBillVO) temp.getParentVO();
        if (null != workFlowBills && workFlowBills.length == 1) {
            workFlowBill = workFlowBills[0];
            if (hvo.getPk_tradetype().equals("D3")) {
                workFlowBill.setWorkflowName(RL_WORKFLOW_ID_CLFk_TABLE_NAME);
            } else if (hvo.getPk_tradetype().equals("F3-Cxx-01")) {
                workFlowBill.setWorkflowName(RL_WORKFLOW_ID_GCFk_TABLE_NAME);
            } else if (hvo.getPk_tradetype().equals("F3-Cxx-FYFKSPD")) {
                workFlowBill.setWorkflowName(RL_WORKFLOW_ID_FYFk_TABLE_NAME);
            } else if (hvo.getPk_tradetype().equals("F3-Cxx-SDRFKSPD")) {
                workFlowBill.setWorkflowName(RL_WORKFLOW_ID_SDRFk_TABLE_NAME);
            } else if (hvo.getPk_tradetype().equals("F3-Cxx-MTWGRFKSPD")) {
                workFlowBill.setWorkflowName(RL_WORKFLOW_ID_MTWGRFk_TABLE_NAME);
            } else if (hvo.getPk_tradetype().equals("F3-Cxx-GDZC")) {
                workFlowBill.setWorkflowName(RL_WORKFLOW_ID_ZCGZFK_TABLE_NAME);
            } else if (hvo.getPk_tradetype().equals("F3-Cxx-RZHK")) {
                workFlowBill.setWorkflowName(RL_WORKFLOW_ID_RZHK_TABLE_NAME);
            } else if (hvo.getPk_tradetype().equals("F3-Cxx-SFFKSPD")) {
                workFlowBill.setWorkflowName(RL_WORKFLOW_ID_SFFK_TABLE_NAME);
            } else if (hvo.getPk_tradetype().equals("F3-Cxx-XCFKSPD")) {
                workFlowBill.setWorkflowName(RL_WORKFLOW_ID_XCZF_TABLE_NAME);
            }

        } else {
            UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(
                    UserVO.class,
                    ((PayBillVO) temp.getParentVO()).getBillmaker());
            workFlowBill.setDef5(userVO.getUser_code());
            workFlowBill.setPk_group(((PayBillVO) temp.getParentVO())
                    .getPk_group());
            workFlowBill
                    .setPk_org(((PayBillVO) temp.getParentVO()).getPk_org());
            workFlowBill.setPk_bill(((PayBillVO) temp.getParentVO())
                    .getPk_paybill());
            workFlowBill.setUser_name(userVO.getUser_name());
            workFlowBill.setUser_code(userVO.getUser_code());
            workFlowBill.setCreationtime(new UFDate().toStdString());
            workFlowBill.setBill_status("1");
            workFlowBill.setCreator(((PayBillVO) temp.getParentVO())
                    .getBillmaker());
            if (hvo.getPk_tradetype().equals("D3")) {
                workFlowBill.setBill_code("F3");
                workFlowBill.setWorkflowId("75");
                workFlowBill.setWorkflowName(RL_WORKFLOW_ID_CLFk_TABLE_NAME);
            } else if (hvo.getPk_tradetype().equals("F3-Cxx-01")) {
                workFlowBill.setBill_code("F3");
                workFlowBill.setWorkflowId("78");
                workFlowBill.setWorkflowName(RL_WORKFLOW_ID_GCFk_TABLE_NAME);
            } else if (hvo.getPk_tradetype().equals("F3-Cxx-FYFKSPD")) {
                workFlowBill.setBill_code("F3");
                workFlowBill.setWorkflowId("77");
                workFlowBill.setWorkflowName(RL_WORKFLOW_ID_FYFk_TABLE_NAME);
            } else if (hvo.getPk_tradetype().equals("F3-Cxx-SDRFKSPD")) {
                workFlowBill.setBill_code("F3");
                workFlowBill.setWorkflowId("81");
                workFlowBill.setWorkflowName(RL_WORKFLOW_ID_SDRFk_TABLE_NAME);
            } else if (hvo.getPk_tradetype().equals("F3-Cxx-MTWGRFKSPD")) {
                workFlowBill.setBill_code("F3");
                workFlowBill.setWorkflowId("103");
                workFlowBill.setWorkflowName(RL_WORKFLOW_ID_MTWGRFk_TABLE_NAME);
            } else if (hvo.getPk_tradetype().equals("F3-Cxx-GDZC")) {
                workFlowBill.setBill_code("F3");
                workFlowBill.setWorkflowId("119");
                workFlowBill.setWorkflowName(RL_WORKFLOW_ID_ZCGZFK_TABLE_NAME);
            } else if (hvo.getPk_tradetype().equals("F3-Cxx-RZHK")) {
                // 融资还款单
                workFlowBill.setBill_code("F3");
                workFlowBill.setWorkflowId("277");
                workFlowBill.setWorkflowName(RL_WORKFLOW_ID_RZHK_TABLE_NAME);
            } else if (hvo.getPk_tradetype().equals("F3-Cxx-SFFKSPD")) {
                // 税费付款审批单
                workFlowBill.setBill_code("F3");
                String workFlowId = "340";
                workFlowId = OaWorkFlowUtil.getOAFlowID(workFlowId);
                workFlowBill.setWorkflowId(workFlowId);
                workFlowBill.setWorkflowName(RL_WORKFLOW_ID_SFFK_TABLE_NAME);
            } else if (hvo.getPk_tradetype().equals("F3-Cxx-XCFKSPD")) {
                // 薪酬付款审批单（宏景）
                workFlowBill.setBill_code("F3");
                String workFlowId = "342";
                workFlowId = OaWorkFlowUtil.getOAFlowID(workFlowId);
                workFlowBill.setWorkflowId(workFlowId);
                workFlowBill.setWorkflowName(RL_WORKFLOW_ID_XCZF_TABLE_NAME);
            }
        }
        return workFlowBill;
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
}
