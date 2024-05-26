package nc.itf.portal.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.holders.StringHolder;

import nc.bs.dao.DAOException;
import nc.bs.framework.adaptor.IHttpServletAdaptor;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.comn.NetStreamContext;
import nc.bs.framework.server.ISecurityTokenCallback;
import nc.bs.logging.Logger;
import nc.bs.trade.business.HYPubBO;
import nc.impl.arap.bx.ArapBXBillPrivateImp;
import nc.itf.arap.pay.IArapPayBillQueryService;
import nc.itf.arap.payable.IArapPayableBillQueryService;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.ui.pcm.utils.GetDao;
import nc.uif.pub.exception.UifException;
import nc.vo.arap.pay.AggPayBillVO;
import nc.vo.arap.pay.PayBillItemVO;
import nc.vo.arap.pay.PayBillVO;
import nc.vo.arap.payable.AggPayableBillVO;
import nc.vo.arap.payable.PayableBillItemVO;
import nc.vo.arap.payable.PayableBillVO;
import nc.vo.arap.receivable.ReceivableBillItemVO;
import nc.vo.arap.receivable.ReceivableBillVO;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.material.MaterialVO;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.hrss.pub.FileNodeVO;
import nc.vo.org.FinanceOrgVO;
import nc.vo.org.OrgVO;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.sm.UserVO;
import nc.ws.intf.HttpClient;
import nc.ws.intf.OAFlowVO;
import nc.ws.intf.OaWorkFlowUtil;
import nc.ws.intf.WorkFlowBill;
import nc.ws.intf.oadata.util.F1OaUtil;
import nc.ws.intf.oadata.util.F3OaUtil;
import nc.ws.intf.oadata.util.JKBXOaUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import uap.pub.fs.client.FileStorageClient;

import com.tchzt.tims.webservice.ContentInfoServiceSoapBindingStub;
import com.tchzt.tims.webservice.ContentInfoService_ServiceLocator;

@SuppressWarnings({"restriction", "unused", "rawtypes", "unchecked"})
public class GetPortalDataImpl implements IHttpServletAdaptor {
    GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
    // 借款报销单单据类型===
    /* 员工借款单 */
    private static String RL_YGJE_001 = "263X-Cxx-YGJKD";

    /* 差旅费借款单 */
    private static String RL_CLFJK_002 = "2631";

    /* 员工费用报销单 */
    private static String RL_YGFYBX_003 = "264X-Cxx-YGFYBXD";

    /* 配套及热计量退费审 */
    private static String RL_RJLTF_004 = "264X-Cxx-PTFJRJLTF";

    /* 差旅费报销单 */
    private static String RL_CLFBX_005 = "2641";

    /* 还款单 */
    private static String RL_HKD_006 = "2647";

    /* 加班餐费报销单 */
    private static String RL_JBCFBX_007 = "264X-Cxx-JBCFBX";

    /* 采暖费退费审批单 */
    private static String RL_CNFTUSP_008 = "264X-Cxx-CNFTFSPD";

    /* 薪酬付款审批单（宏景） */
    private static String RL_XCFKSPD_009 = "F3-Cxx-XCFKSPD";

    private static String YXURL = "http://172.18.128.94:2333/webShowImage/";

    // 结束=====

    @Override
    public void doAction(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        String ds = "design";
        InvocationInfoProxy.getInstance().setUserDataSource(ds);
        InvocationInfoProxy.getInstance().setGroupId("0001A1100000000001QS");// 设置默认的集团主键
        InvocationInfoProxy.getInstance().setUserCode("liuli");// 设置接口默认操作员编码
        InvocationInfoProxy.getInstance().setUserId("1001A110000000000HV8");// 设置接口默认操作员主键
        ISecurityTokenCallback tc = NCLocator.getInstance().lookup(
                ISecurityTokenCallback.class);
        byte[] token = tc.token("NCSystem".getBytes(), "pfxx".getBytes());
        NetStreamContext.setToken(token);
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        req.setCharacterEncoding("utf-8");
        // 获取传输数据：合同主键（contractPk）
        // String billpk = req.getParameter("billpk");// 单据主键
        JSONObject jsonMain = new JSONObject();
        JSONObject json = new JSONObject();
        JSONArray headData = null;// 主表数据拼接
        JSONArray bodyData = null;// 主子表数据拼接
        String requestid = req.getParameter("requestid");// requestid
        String gxsql = "SELECT REQUESTID,WORKFLOWID,BILL_CODE,PK_BILL FROM PM_WORKFLOW_BILL WHERE REQUESTID = "
                + requestid
                + " and (pk_org in (SELECT "
                + "ZZ.PK_ORG FROM	ORG_ORGS ZZ WHERE ZZ.ENABLESTATE = '2' AND isbusinessunit = 'Y'"
                + " START WITH ZZ.PK_FATHERORG IN ( '0001A110000000000HYQ' ) CONNECT BY PRIOR "
                + "ZZ.PK_ORG = ZZ.PK_FATHERORG) or pk_org = '0001A110000000000HYQ' OR PK_ORG IN "
                + "(SELECT PK_ORG FROM ORG_ORGS WHERE DEF2 = '1'))";
        try {
            List<Object[]> ls = getDao.query(gxsql);
            if (ls != null && ls.size() > 0) {
                String bill_type = ls.get(0)[2] + "";// 单据类型
                String PK_BILL = ls.get(0)[3] + "";// 单据主键
                if ("F0".equals(bill_type)) {// 应收单
                    headData = getF0Main(PK_BILL);// 获取主表数据
                    bodyData = getF0Body(PK_BILL);// 获取子表数据
                } else if ("F3".equals(bill_type)) {// 付款单
                    IArapPayBillQueryService cs = (IArapPayBillQueryService) NCLocator
                            .getInstance().lookup(
                                    IArapPayBillQueryService.class);
                    AggPayBillVO[] wq = cs
                            .queryBillsByWhereSQL(" PK_PAYBILL = '" + PK_BILL
                                    + "'");
                    if (wq.length > 0) {
                        AggPayBillVO temp = wq[0];
                        PayBillVO hvo1 = (PayBillVO) temp.getParentVO();// 主表VO
                        PayBillItemVO[] mxVOs = (PayBillItemVO[]) temp
                                .getChildrenVO();
                        String supplier = mxVOs[0].getSupplier();
                        String recaccount = mxVOs[0].getRecaccount();
                        String payaccount = mxVOs[0].getPayaccount();
                        String pu_deptid = mxVOs[0].getPu_deptid();
                        String pk_deptid = mxVOs[0].getPk_deptid();
                        if ("D3".equals(hvo1.getPk_tradetype())) {
                            // 材料付款单
                            headData = F3OaUtil.getMainMap1(hvo1, supplier);
                            bodyData = F3OaUtil.getDtaileDataMap(temp);
                        } else if ("F3-Cxx-01".equals(hvo1.getPk_tradetype())) {
                            // 工程付款单
                            headData = F3OaUtil.getMainMap2(hvo1, supplier);
                            bodyData = F3OaUtil.getDtaileDataMap(temp);
                        } else if ("F3-Cxx-FYFKSPD".equals(hvo1
                                .getPk_tradetype())
                                || "F3-Cxx-SDRFKSPD".equals(hvo1
                                .getPk_tradetype())) {
                            // 费用付款单//水电付款单
                            headData = F3OaUtil.getMainMap5(hvo1, supplier);
                            bodyData = F3OaUtil.getDtaileDataMap(temp);
                        } else if ("F3-Cxx-MTWGRFKSPD".equals(hvo1
                                .getPk_tradetype())) {
                            // 煤炭付款单
                            headData = F3OaUtil.getMainMap5(hvo1, supplier);
                            bodyData = F3OaUtil.getDtaileDataMapMT(temp);
                        } else if ("F3-Cxx-GDZC".equals(hvo1.getPk_tradetype())) {
                            // 资产购置付款单
                            headData = F3OaUtil.getMainMap6(hvo1, supplier, pk_deptid);
                            bodyData = F3OaUtil.getDtaileDataMapZC(temp);

                        } else if ("F3-Cxx-RZHK".equals(hvo1.getPk_tradetype())) {
                            // 融资还款单
                            headData = getRZMainMap(hvo1, supplier);
                            bodyData = getRZDtaileDataMap(
                                    (PayBillItemVO[]) temp.getChildrenVO(),
                                    hvo1);

                        } else if ("F3-Cxx-SFFKSPD".equals(hvo1
                                .getPk_tradetype())) {
                            // 税费付款审批单
                            String tableName = "formtable_main_437";
                            // 获取主表数据
                            headData = getNewMainMap(hvo1, supplier,
                                    recaccount, payaccount, pu_deptid);
                            // 获取子表数据
                            bodyData = getNewDtaileDataMap(
                                    (PayBillItemVO[]) temp.getChildrenVO(),
                                    (PayBillVO) temp.getParentVO(), tableName,
                                    "F3-Cxx-SFFKSPD");
                        } else if ("F3-Cxx-XCFKSPD".equals(hvo1
                                .getPk_tradetype())) {
                            // 薪酬付款审批单（宏景）
                            String tableName = "formtable_main_439";
                            // 获取主表数据
                            headData = getNewMainMap(hvo1, supplier,
                                    recaccount, payaccount, pu_deptid);
                            // 获取子表数据
                            bodyData = getNewDtaileDataMap(
                                    (PayBillItemVO[]) temp.getChildrenVO(),
                                    (PayBillVO) temp.getParentVO(), tableName,
                                    "F3-Cxx-XCFKSPD");
                        }
                    } else {
                        resp.setStatus(500);
                        PrintWriter writer = resp.getWriter();
                        jsonMain.put("status", false);
                        jsonMain.put("message", "获取付款单据失败，主键为：" + PK_BILL);
                        writer.write(jsonMain.toString());
                    }
                } else if ("F1".equals(bill_type)) {// 应付单
                    IArapPayableBillQueryService cs = (IArapPayableBillQueryService) NCLocator
                            .getInstance().lookup(
                                    IArapPayableBillQueryService.class);
                    AggPayableBillVO[] wq = cs
                            .queryVOsByWhere(" PK_PAYABLEBILL = '" + PK_BILL
                                    + "'");
                    if (wq != null && wq.length > 0) {
                        AggPayableBillVO temp = wq[0];
                        PayableBillVO hvo1 = (PayableBillVO) temp.getParentVO();// 主表VO
                        PayableBillItemVO[] mxVOs = (PayableBillItemVO[]) temp
                                .getChildrenVO();
                        String supplier = mxVOs[0].getSupplier();
                        if ("D1".equals(hvo1.getPk_tradetype())) {
                            // 材料应付单
                            headData = F1OaUtil.getMainMap3(
                                    (PayableBillVO) temp.getParentVO(),
                                    supplier);
                            bodyData = F1OaUtil.getYfdDtaileDataMap(temp);
                        }

                        if ("F1-Cxx-01".equals(hvo1.getPk_tradetype())) {
                            // 工程应付单
                            headData = F1OaUtil.getMainMap4(
                                    (PayableBillVO) temp.getParentVO(),
                                    supplier);
                            bodyData = F1OaUtil.getYfdDtaileDataMap(temp);
                        }
                        if ("F1-Cxx-FYFPHZBZD".equals(hvo1.getPk_tradetype())) {
                            // 费用发票总账汇报单
                            headData = F1OaUtil.getMainMapFyfp(
                                    (PayableBillVO) temp.getParentVO(),
                                    supplier);
                            bodyData = F1OaUtil.getDtaileDataMapFyfp(temp);
                        }
                        if ("F1-Cxx-ZCBZ".equals(hvo1.getPk_tradetype())) {
                            // 资产应付单
                            headData = F1OaUtil.getMainMapFyfp(
                                    (PayableBillVO) temp.getParentVO(),
                                    supplier);
                            bodyData = F1OaUtil.getZCdDtaileDataMap(temp);
                        }
                    } else {
                        resp.setStatus(500);
                        PrintWriter writer = resp.getWriter();
                        jsonMain.put("status", false);
                        jsonMain.put("message", "获取应付单据失败，主键为：" + PK_BILL);
                        writer.write(jsonMain.toString());
                    }
                } else if ("263X".equals(bill_type) || "264X".equals(bill_type)) {// 借款报销单
                    String djdl = "";
                    if ("263X".equals(bill_type)) {
                        djdl = "jk";
                    } else if ("264X".equals(bill_type)) {
                        djdl = "bx";
                    }
                    IBXBillPrivate queryservice = (IBXBillPrivate) NCLocator
                            .getInstance().lookup(IBXBillPrivate.class);
                    List<JKBXVO> temps = queryservice.queryVOsByPrimaryKeys(
                            new String[]{PK_BILL}, djdl);

                    /*
                     * ArapBXBillPrivateImp queryservice =
                     * (ArapBXBillPrivateImp) NCLocator
                     * .getInstance().lookup(ArapBXBillPrivateImp.class);
                     * List<JKBXVO> temps = queryservice.queryVOsByPrimaryKeys(
                     * new String[]{PK_BILL}, bill_type);
                     */

                    if (temps != null && temps.size() > 0) {
                        JKBXVO temp = temps.get(0);
                        String transi_type = temp.getParentVO()
                                .getPk_billtype();
                        String type = temp.getParentVO().getDjlxbm();
                        // String dl = temp.getParentVO().getDjdl();
                        // JKVO jkVO = (JKVO)temp;
                        if (RL_CLFJK_002.equals(type)) {
                            // 差旅费借款单
                            JKVO jkVO = (JKVO) temp;
                            headData = JKBXOaUtil
                                    .getMainMap(jkVO.getParentVO());
                            bodyData = JKBXOaUtil.getDtaileDataMap2(jkVO);
                        }
                        if (RL_YGJE_001.equals(type)) {
                            JKVO jkVO = (JKVO) temp;
                            // 员工借款单
                            headData = JKBXOaUtil
                                    .getMainMap(jkVO.getParentVO());
                            bodyData = JKBXOaUtil.getDtaileDataMap1(jkVO);
                        }

                        if (RL_YGFYBX_003.equals(type)) {
                            // 员工费用报销单
                            BXVO bxVO = (BXVO) temp;
                            headData = JKBXOaUtil.getMainMap3(bxVO
                                    .getParentVO());
                            bodyData = JKBXOaUtil.getDtaileDataMap3(bxVO);
                        }
                        if (RL_RJLTF_004.equals(type)) {
                            // 配套及热计量退费审
                            BXVO bxVO = (BXVO) temp;
                            headData = JKBXOaUtil.getMainMap4(bxVO
                                    .getParentVO());
                            bodyData = JKBXOaUtil.getDtaileDataMap4(bxVO);
                        }
                        if (RL_CLFBX_005.equals(type)) {
                            // *差旅费报销单
                            BXVO bxVO = (BXVO) temp;
                            headData = JKBXOaUtil.getMainMap5(bxVO
                                    .getParentVO());
                            bodyData = JKBXOaUtil.getDtaileDataMap5(bxVO);
                        }
                        if (RL_HKD_006.equals(type)) {
                            // 还款单
                            BXVO bxVO = (BXVO) temp;
                            headData = JKBXOaUtil.getMainMap6(bxVO
                                    .getParentVO());
                            bodyData = JKBXOaUtil.getDtaileDataMap6(bxVO);

                        }
                        if (RL_JBCFBX_007.equals(type)) {
                            // 加班餐费报销单
                            BXVO bxVO = (BXVO) temp;
                            headData = JKBXOaUtil.getMainMap7(bxVO
                                    .getParentVO());
                            bodyData = JKBXOaUtil.getDtaileDataMap7(bxVO);
                        }
                        if (RL_CNFTUSP_008.equals(type)) {
                            // 采暖费退费审批单
                            BXVO bxVO = (BXVO) temp;
                            headData = JKBXOaUtil.getMainMap8(temp
                                    .getParentVO());
                            bodyData = JKBXOaUtil.getDtaileDataMap8(temp);
                        }
                    } else {
                        resp.setStatus(500);
                        PrintWriter writer = resp.getWriter();
                        jsonMain.put("status", false);
                        jsonMain.put("message", "获取借款报销单据失败，主键为：" + PK_BILL);
                        writer.write(jsonMain.toString());
                        return;
                    }
                }
                getFiles(headData, PK_BILL);
                json.put("headData", headData);
                json.put("bodyData", bodyData);
                jsonMain.put("status", true);
                jsonMain.put("message", "");
                jsonMain.put("data", json);
                resp.setStatus(200);
                PrintWriter writer = resp.getWriter();
                writer.write(jsonMain.toString());
            } else {
                resp.setStatus(500);
                PrintWriter writer = resp.getWriter();
                jsonMain.put("status", false);
                jsonMain.put("message", "获取流程单据失败，REQUESTID为：" + requestid);
                writer.write(jsonMain.toString());
            }
        } catch (DAOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BusinessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
        list.add(OaWorkFlowUtil.listAddObj("khh", skyhzh));
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

    public String getDef2(String pk_org) throws BusinessException {
        String def2 = "";
        OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
                pk_org);
        if (null != orgVO) {
            def2 = orgVO.getDef2();
        }
        return def2;
    }

    /**
     * 获取附件列表
     *
     * @param pk_bill
     */
    @SuppressWarnings("deprecation")
    private static void getFiles(JSONArray headData, String pk_bill)
            throws UifException {
        String strWhere = "filepath like '" + pk_bill + "/%';";
        FileNodeVO[] fileNodeVOs = (FileNodeVO[]) new HYPubBO()
                .queryByCondition(FileNodeVO.class, strWhere);
        Map<String, Object> djzj = new HashMap<String, Object>();
        List<Map<?, ?>> list = new ArrayList<Map<?, ?>>();
        djzj.put("fieldName", "fj");
        djzj.put("fieldValue", list);
        if (null != fileNodeVOs && fileNodeVOs.length > 0) {
            /* 主表主键 */
            for (FileNodeVO temp : fileNodeVOs) {
                Map<String, String> file = new HashMap<String, String>();
                file.put("filePath", FileStorageClient.getInstance()
                        .getDownloadURL(null, temp.getPk_doc()));
                file.put(
                        "fileName",
                        temp.getFilepath().substring(
                                temp.getFilepath().lastIndexOf("/") + 1));
                list.add(file);
            }
        }
        /* 获取影像附件 */
        try {
            getFileList(list, pk_bill);
        } catch (Exception e) {

        }
        if (list.size() > 0) {
            djzj.put("fieldValue", list);
            headData.add(djzj);
        }
    }

    /**
     * 获取影像附件
     *
     * @param list
     * @param pk_bill
     * @throws BusinessException
     */
    private static void getFileList(List<Map<?, ?>> list, String pk_bill)
            throws BusinessException {
        String url = YXURL + pk_bill;
        String back = HttpClient.httpGet(url, null, "utf-8");
        if (StringUtils.isNotEmpty(back)) {
            JSONObject json = JSONObject.fromObject(back);
            if (json.get("data") != null
                    && StringUtils.isNotEmpty(json.getString("data"))) {
                JSONObject data = json.getJSONObject("data");
                JSONArray files = data.getJSONArray("files");
                if (files != null && files.size() > 0) {
                    for (Object curfile : files) {
                        Map<Object, Object> file = new HashMap<Object, Object>();
                        JSONObject evefile = (JSONObject) curfile;
                        String filePath = "http://172.18.128.94:2333"
                                + evefile.getString("url");// 正式
                        String filename = "影像-" + evefile.getString("fileName");
                        file.put("filePath", filePath);
                        file.put("fileName", filename);
                        list.add(file);
                    }
                }
            }
        } else {
            throw new BusinessException("获取影像附件失败，请联系管理员！");
        }
    }

    private static String getdocString(String pk) {
        Document document = DocumentHelper.createDocument();
        // 创建根节点
        Element root = document.addElement("CMDATA");
        // 添加子节点
        Element username = root.addElement("TRADETYPE");
        Element password = root.addElement("CIP");
        Element SYSTEM_CODE = root.addElement("SYSTEM_CODE");
        Element BRANCH_NO = root.addElement("BRANCH_NO");
        Element USER_NO = root.addElement("USER_NO");
        Element BUSI_SERIAL_NO = root.addElement("BUSI_SERIAL_NO");
        BUSI_SERIAL_NO.addText(pk);
        Element BATCH = root.addElement("BATCH");
        Element BATCHID = BATCH.addElement("BATCHID");
        Element DOCUMENTS = BATCH.addElement("DOCUMENTS");
        Element DOCUMENT = DOCUMENTS.addElement("DOCUMENT");
        Element DOCNAME = DOCUMENT.addElement("DOCNAME");
        Element DESC = DOCUMENT.addElement("DESC");
        Element FILES = DOCUMENT.addElement("FILES");
        Element FILE = FILES.addElement("FILE");
        Element VERSION = FILE.addElement("VERSION");
        Element FILE_SEQ = FILE.addElement("FILE_SEQ");
        Element FILE_TYPE = FILE.addElement("FILE_TYPE");
        Element FILE_NAME = FILE.addElement("FILE_NAME");
        Element FILE_FORMAT = FILE.addElement("FILE_FORMAT");
        Element FILE_SIZE = FILE.addElement("FILE_SIZE");
        Element FILE_MD5 = FILE.addElement("FILE_MD5");
        return document.asXML();
    }

    // 获取F0应收单子表数据
    private JSONArray getF0Main(String pk_bill) throws BusinessException {
        ReceivableBillVO parentVO = (ReceivableBillVO) getHyPubBO()
                .queryByPrimaryKey(ReceivableBillVO.class, pk_bill);
        ReceivableBillItemVO[] bvos = (ReceivableBillItemVO[]) getHyPubBO()
                .queryByCondition(ReceivableBillItemVO.class,
                        "nvl(dr,0) = 0 and pk_recbill = '" + pk_bill + "'");
        String customer = bvos[0].getCustomer();

        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        // 构造数据
        List<Map<String, Object>> list = OaWorkFlowUtil.transBean2Map(parentVO);
        // ---------其他字段begin
        // 应收财务组织
        String stockName = (String) getHyPubBO().findColValue(
                "org_financeorg",
                "name",
                "nvl(dr,0) = 0 and pk_financeorg = '" + parentVO.getPk_org()
                        + "'");
        list.add(OaWorkFlowUtil.listAddObj("zzmc", stockName));
        // 申请人
        UserVO userVO = (UserVO) getHyPubBO().queryByPrimaryKey(UserVO.class,
                parentVO.getBillmaker());
        // 客户名称
        String custName = (String) getHyPubBO().findColValue("bd_customer",
                "name", "nvl(dr,0) = 0 and pk_customer = '" + customer + "'");
        list.add(OaWorkFlowUtil.listAddObj("custname", custName));
        // 部门
        String sqbmName = (String) getHyPubBO().findColValue(
                "org_dept_v",
                "name",
                "nvl(dr,0) = 0 and pk_vid = '" + parentVO.getPk_deptid_v()
                        + "'");
        list.add(OaWorkFlowUtil.listAddObj("bm", sqbmName));
        // 业务员
        String ywy = (String) getHyPubBO().findColValue(
                "bd_psndoc",
                "name",
                "nvl(dr,0) = 0 and pk_psndoc = '" + parentVO.getPk_psndoc()
                        + "'");
        list.add(OaWorkFlowUtil.listAddObj("ywy", ywy));
        // 申请部门
        String sqbm = (String) getHyPubBO()
                .findColValue(
                        "org_dept",
                        "name",
                        "nvl(dr,0) = 0 and pk_dept = '"
                                + parentVO.getPk_deptid() + "'");
        list.add(OaWorkFlowUtil.listAddObj("sqbm", sqbm));
        // 责任部门
        String zrbm = (String) getHyPubBO().findColValue(
                "org_dept",
                "name",
                "nvl(dr,0) = 0 and pk_dept = '" + parentVO.getPk_deptid_res()
                        + "'");
        list.add(OaWorkFlowUtil.listAddObj("zrbm", zrbm));
        // 申请人
        list.add(OaWorkFlowUtil.listAddObj("sqr", userVO.getUser_name()));
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
        // 代控部门
        if (parentVO.getSo_deptid() != null) {
            /* 代控部门主键 */
            list.add(OaWorkFlowUtil.listAddObj("dkbmzj",
                    parentVO.getSo_deptid()));
            /* 代控部门编码 */
            String dkbmbm = (String) getHyPubBO().findColValue(
                    "org_dept",
                    "code",
                    "nvl(dr,0) = 0 and pk_dept = '" + parentVO.getSo_deptid()
                            + "'");
            list.add(OaWorkFlowUtil.listAddObj("dkbmbm", dkbmbm));

            /* 代控部门 */
            String dkbm = (String) getHyPubBO().findColValue(
                    "org_dept",
                    "name",
                    "nvl(dr,0) = 0 and pk_dept = '" + parentVO.getSo_deptid()
                            + "'");
            list.add(OaWorkFlowUtil.listAddObj("dkbm", dkbm));
        }
        // 备注
        if (null != parentVO.getDef12()) {
            list.add(OaWorkFlowUtil.listAddObj("bz", parentVO.getDef12()));
        }
        if (parentVO.getScomment() != null) {
            list.add(OaWorkFlowUtil.listAddObj("scomment",
                    parentVO.getScomment()));
        }
        // 合同现用名
        if (null != parentVO.getDef4()) {
            String htxym = (String) getHyPubBO().findColValue(
                    "bd_supplier",
                    "name",
                    "nvl(dr,0) = 0 and pk_supplier = '" + parentVO.getDef4()
                            + "'");
            list.add(OaWorkFlowUtil.listAddObj("htxym", htxym));
        }
        if (parentVO.getCashitem() != null) {
            // 现金流量项目
            String xjllxm = (String) getHyPubBO().findColValue(
                    "bd_cashflow",
                    "name",
                    "nvl(dr,0) = 0 and pk_cashflow ='" + parentVO.getCashitem()
                            + "'");
            list.add(OaWorkFlowUtil.listAddObj("xjllxm", xjllxm));
        }
        // 发票类型
        if (parentVO.getDef31() != null) {
            String fplx = (String) getHyPubBO().findColValue(
                    "bd_defdoc",
                    "name",
                    "nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getDef31()
                            + "'");
            list.add(OaWorkFlowUtil.listAddObj("fplx", fplx));
        }
        // 合同类别
        if (parentVO.getDef80() != null) {
            list.add(OaWorkFlowUtil.listAddObj("htlb", parentVO.getDef80()));
        }
        Logger.error("local_money：" + parentVO.getLocal_money());
        // 金额
        if (parentVO.getLocal_money() != null) {
            list.add(OaWorkFlowUtil.listAddObj("local_money",
                    parentVO.getLocal_money() + ""));
        }
        // 调整模板后新增字段
        // 本次申请发票类型
        if (parentVO.getDef81() != null) {
            String bcsqfplx = (String) getHyPubBO().findColValue(
                    "bd_defdoc",
                    "name",
                    "nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getDef81()
                            + "'");
            list.add(OaWorkFlowUtil.listAddObj("bcsqfplx", bcsqfplx));
        }
        // 收款业务
        if (parentVO.getDef84() != null) {
            String skyw = (String) getHyPubBO().findColValue(
                    "bd_defdoc",
                    "name",
                    "nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getDef84()
                            + "'");
            list.add(OaWorkFlowUtil.listAddObj("skyw", skyw));
        }
        // 应收类型
        if (parentVO.getDef88() != null) {
            String yslx = (String) getHyPubBO().findColValue(
                    "bd_defdoc",
                    "name",
                    "nvl(dr,0) = 0 and pk_defdoc ='" + parentVO.getDef88()
                            + "'");
            list.add(OaWorkFlowUtil.listAddObj("yslx", yslx));
        }
        // ---------其他字段end
        JSONArray arr = JSONArray.fromObject(list, jsonConfig);
        return arr;
    }

    // 获取F0应收单主表数据
    private JSONArray getF0Body(String pk_bill) throws BusinessException {
        // TODO Auto-generated method stub
        ReceivableBillItemVO[] bvo = (ReceivableBillItemVO[]) getHyPubBO()
                .queryByCondition(ReceivableBillItemVO.class,
                        "nvl(dr,0) = 0 and pk_recbill = '" + pk_bill + "'");
        Map<String, Object> dtMap = new HashMap<String, Object>();
        List<Map<String, Object>> workflowRequestTableRecords = new ArrayList<Map<String, Object>>();
        dtMap.put("tableDBName", "formtable_main_197_dt1");
        for (ReceivableBillItemVO temp : bvo) {
            // 构造数据
            List<Map<String, Object>> workflowRequestTableFields = OaWorkFlowUtil
                    .transBean2Map(temp);
            // 其他字段------begin
            if (null != temp.getProject()) {
                // 项目
                ProjectHeadVO projectVO = (ProjectHeadVO) getHyPubBO()
                        .queryByPrimaryKey(ProjectHeadVO.class,
                                temp.getProject());
                // 项目编码
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "project_code", projectVO.getProject_code()));
                // 项目名称
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "project_name", projectVO.getProject_name()));
            }
            if (null != temp.getMaterial()) {
                MaterialVO materialVO = (MaterialVO) getHyPubBO()
                        .queryByPrimaryKey(MaterialVO.class, temp.getMaterial());
                // 物料编码
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "material_code", materialVO.getCode()));
                // 物料名称
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "material_name", materialVO.getName()));
                // 规格
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "materialspec", materialVO.getMaterialspec()));
                // 型号
                String str4 = "";
                if (null != materialVO.getMaterialtype()) {
                    str4 = materialVO.getMaterialtype();
                }
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "materialtype", str4));
            }
            // 税码
            if (temp.getTaxcodeid() != null) {
                String sm = (String) getHyPubBO().findColValue(
                        "bd_taxcode",
                        "code",
                        "nvl(dr,0) = 0 and pk_taxcode = '"
                                + temp.getTaxcodeid() + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sm",
                        sm));
            }
            // 税率
            if (temp.getDef17() != null) {
                String sl = (String) getHyPubBO().findColValue(
                        "bd_defdoc",
                        "name",
                        "nvl(dr,0) = 0 and pk_defdoc = '" + temp.getDef17()
                                + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("sl",
                        sl));
            }
            // 结算方式
            if (temp.getPk_balatype() != null) {
                String jsfsbm = (String) getHyPubBO().findColValue(
                        "bd_balatype",
                        "code",
                        "nvl(dr,0) = 0 and pk_balatype = '"
                                + temp.getPk_balatype() + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "jsfsbm", jsfsbm));
                String jsfs = (String) getHyPubBO().findColValue(
                        "bd_balatype",
                        "name",
                        "nvl(dr,0) = 0 and pk_balatype = '"
                                + temp.getPk_balatype() + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "jsfs", jsfs));
            }
            // 申请部门
            if (temp.getPk_deptid() != null) {
                String fysqbm = (String) getHyPubBO().findColValue(
                        "org_dept",
                        "name",
                        "nvl(dr,0) = 0 and pk_dept = '" + temp.getPk_deptid()
                                + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "sqbm", fysqbm));
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "fsbm", fysqbm));
            }
            // 收支项目
            if (temp.getPk_subjcode() != null) {
                String szxm = (String) getHyPubBO().findColValue(
                        "bd_inoutbusiclass",
                        "name",
                        "nvl(dr,0) = 0 and pk_inoutbusiclass  ='"
                                + temp.getPk_subjcode() + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "szxm", szxm));
            }
            // 代控部门
            if (temp.getSo_deptid() != null) {
                /* 代控部门主键 */
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "dkbmzj", temp.getSo_deptid()));
                /* 代控部门编码 */
                String dkbmbm = (String) getHyPubBO().findColValue(
                        "org_dept",
                        "code",
                        "nvl(dr,0) = 0 and pk_dept = '" + temp.getSo_deptid()
                                + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "dkbmbm", dkbmbm));
                /* 代控部门 */
                String dkbm = (String) getHyPubBO().findColValue(
                        "org_dept",
                        "name",
                        "nvl(dr,0) = 0 and pk_dept = '" + temp.getSo_deptid()
                                + "'");
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj(
                        "dkbm", dkbm));
            }

            // 其他字段------end
            Map<String, Object> workflowRequestTableFieldsMap = new HashMap<String, Object>();
            workflowRequestTableFieldsMap.put("recordOrder", "0");
            workflowRequestTableFieldsMap.put("workflowRequestTableFields",
                    workflowRequestTableFields);
            workflowRequestTableRecords.add(workflowRequestTableFieldsMap);
        }
        dtMap.put("workflowRequestTableRecords", workflowRequestTableRecords);
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        JSONArray dtlistString = JSONArray.fromObject(dtMap, jsonConfig);
        return dtlistString;
    }

    /**
     * List<Map<String, Object>> 去出重复的key
     *
     * @param str Map<String, Object> 中对比的key
     * @return
     */
    public static List<Map<String, Object>> getListMap(
            List<Map<String, Object>> oldList, String str) {

        List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < oldList.size(); i++) {
            Map<String, Object> oldMap = oldList.get(i);
            if (newList.size() > 0) {
                boolean isContain = false;
                for (int j = 0; j < newList.size(); j++) {
                    Map<String, Object> newMap = newList.get(j);
                    if (newMap.get(str) != null && oldMap.get(str) != null) {
                        if (newMap.get(str).equals(oldMap.get(str))) {
                            for (String key : oldMap.keySet()) {
                                newMap.put(key, oldMap.get(key));
                            }
                            isContain = true;
                            break;
                        }
                    }
                }

                if (!isContain) {
                    newList.add(oldMap);
                }

            } else {
                newList.add(oldMap);
            }
        }
        return newList;
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
        if (null != headVO.getDef30()) {
            list.add(OaWorkFlowUtil.listAddObj("def30", headVO.getDef30()));
        }
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
            /* 税额 */
            if (null != temp.getDef19()) {
                workflowRequestTableFields.add(OaWorkFlowUtil.listAddObj("se",
                        temp.getDef19()));
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

    private JSONArray getRZDtaileDataMap(PayBillItemVO[] bvos, PayBillVO headVO)
            throws BusinessException {
        Map dtMap = new HashMap();
        dtMap.put("tableDBName", "formtable_main_383_dt1");
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
}
