package nc.obm.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.bs.framework.adaptor.IHttpServletAdaptor;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.security.token.ISecurityTokenCache;
import nc.bs.framework.server.ISecurityTokenCallback;
import nc.bs.trade.business.HYPubBO;
import nc.vo.bd.bankaccount.BankAccSubVO;
import nc.vo.bd.bankaccount.BankAccbasVO;
import nc.vo.bd.banktype.BankTypeVO;
import nc.vo.logging.Debug;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.ws.intf.YHYEBVO;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

// CBS调用接口，获取NC银行余额信息
@SuppressWarnings({ "restriction" })
public class BankYHYEForCBSServlet extends HttpServlet implements
        IHttpServletAdaptor {
    private static final long serialVersionUID = 1L;

    // 实现IHttpServletAdaptor
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // 实现post请求，调用IHttpServletAdaptor下的doAction方法
        doAction(req, res);
    }

    @Override
    public void doAction(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        ISecurityTokenCallback sc = NCLocator.getInstance().lookup(
                ISecurityTokenCallback.class);
        byte[] token = sc.token("NCSystem".getBytes(), "pfxx".getBytes());
        // token存入NC系统的缓存
        ISecurityTokenCache tokencache = (ISecurityTokenCache) NCLocator
                .getInstance().lookup(ISecurityTokenCache.class);
        tokencache.isContain(token);
        req.setCharacterEncoding("utf-8");// 设置编码规则
        BufferedReader br = req.getReader();// 读取HttpServletRequest对象里的数据
        String body = "";
        String line = null;
        while ((line = br.readLine()) != null) {
            body += line;
        }
        Debug.error("=============CBS getBankYHYE msg ：" + body);
        res.setCharacterEncoding("utf-8");
        res.setStatus(200);
        JSONObject jsonObject = null;
        String request = "";
        try {
            jsonObject = (JSONObject) JSONObject.parse(body);
        } catch (JSONException e) {
            request = createMsg(null, "-1", "JSON解析失败！" + e.getMessage());
        }
        // 封装银行数据
        try {
            request = getYHYEInfo(jsonObject);
        } catch (BusinessException e) {
            // TODO Auto-generated catch block
            request = createMsg(null, "-1", "异常！" + e.getMessage());
        }
        System.out.println("=================" + request);
        res.getWriter().write(request);// 输出body
        return;
    }

    private String getYHYEInfo(JSONObject body) throws BusinessException {

        InvocationInfoProxy.getInstance().setGroupId("0001A1100000000001QS");
        /* 返回数据 */
        return toDataJson();
    }

    // 查询需推送CBS系统的银行单据
    private BankAccbasVO getBankVOBywhere(String accnum)
            throws BusinessException {
        BankAccbasVO[] bankVOs = (BankAccbasVO[]) new HYPubBO()
                .queryByCondition(BankAccbasVO.class,
                        "nvl(dr,0) = 0 and accnum = '" + accnum
                                + "' and accclass = 2 and enablestate = 2 ");
        return bankVOs == null || bankVOs.length <= 0 ? null : bankVOs[0];
    }

    // 查询需推送CBS系统的银行列表单据
    private YHYEBVO[] getYHYEVOBywhere() throws BusinessException {
        YHYEBVO[] yevos = (YHYEBVO[]) new HYPubBO().queryByCondition(
                YHYEBVO.class, "1 = 1 ");
        if (yevos == null) {
            return null;
        }
        return yevos;
    }

    private String toDataJson() {
        JSONObject data = new JSONObject();
        JSONArray arr = new JSONArray();
        try {
            YHYEBVO[] yhyebvos = getYHYEVOBywhere();
            for (YHYEBVO yhyebvo : yhyebvos) {
                BankAccbasVO yhvo = getBankVOBywhere(yhyebvo.getZh());
                if (yhvo != null && yhvo.getAccnum() != null) {
                    String sqlwhere = "nvl(dr,0) = 0 and pk_bankaccbas = '"
                            + yhvo.getPrimaryKey() + "'";
                    BankAccSubVO[] zhvos = (BankAccSubVO[]) new HYPubBO()
                            .queryByCondition(BankAccSubVO.class, sqlwhere);
                    BankAccSubVO zhvo = zhvos[0];// 银行账户1对1银行账户子户
                    JSONObject listJson = new JSONObject();
                    /* 银行账号 */
                    listJson.put("accountNo", yhvo.getAccnum());
                    /* 账户名称 */
                    listJson.put("accountName", yhvo.getAccname());
                    /* 币种 */
                    listJson.put("currency", "10");
                    /* 币种名称 */
                    listJson.put("currencyName", "人民币");
                    /* 银行类别 */
                    BankTypeVO typeVO = (BankTypeVO) new HYPubBO()
                            .queryByPrimaryKey(BankTypeVO.class,
                                    yhvo.getPk_banktype());
                    listJson.put("bankType", typeVO.getMnecode());
                    /* 银行类别名称 */
                    listJson.put("bankTypeName", typeVO.getName());
                    /* 账户余额 */
                    listJson.put("accountBalance", yhyebvo.getYe());
                    /* 外币账户余额 */
                    listJson.put("accountBalanceCny", yhyebvo.getYe());
                    /* 可用金额 */
                    listJson.put("availableBalance", yhyebvo.getYe());
                    /* 外币可用余额 */
                    listJson.put("availableBalanceCny", yhyebvo.getYe());
                    /* 冻结金额 */
                    listJson.put("frozenBalance", zhvo.getFronzenmny());
                    /* 存款类型列表 */
                    String depositType = "";
                    if (zhvo.getAcctype() != null) {
                        if (zhvo.getAcctype() == 0) {
                            depositType = "A";// 活期
                        } else if (zhvo.getAcctype() == 1) {
                            depositType = "F";// 定期
                        } else if (zhvo.getAcctype() == 2) {
                            depositType = "N";// 通知
                        }
                    }
                    if (zhvo.getIsconcerted() != null) {
                        if (zhvo.getIsconcerted().booleanValue()) {
                            depositType = "C";// 协定
                        }
                    }
                    listJson.put("depositType", depositType);
                    /* 余额更新时间 */
                    listJson.put("balanceUpdateTime", yhyebvo.getYerq());

                    /* 单位编码 */
                    OrgVO orgvo = (OrgVO) new HYPubBO().queryByPrimaryKey(
                            OrgVO.class, yhvo.getPk_org());
                    listJson.put("unitCode", orgvo.getCode());
                    /* 单位名称 */
                    listJson.put("unitName", orgvo.getName());
                    arr.add(listJson);
                    data.put("list", arr);
                }
            }
            // data.put("endRow", 1);
            // data.put("hasNextPage", Boolean.FALSE);
            // data.put("hasPreviousPage", Boolean.FALSE);
            // data.put("isFirstPage", Boolean.TRUE);
            // data.put("isLastPage", Boolean.TRUE);

            // data.put("navigateFirstPage", 1);
            // data.put("navigateLastPage", 8);
            // 所有导航页号list
            // JSONArray navigatepageNumsArr = new JSONArray();
            // navigatepageNumsArr.add(1);
            // data.put("navigatepageNums", navigatepageNumsArr);
            // data.put("nextPage", 0);
            // data.put("pageNum", 1);
            // data.put("pageSize", 20);
            // data.put("pages", 1);
            // data.put("prePage", 0);
            // data.put("size", 1);
            // data.put("startRow", 1);
            data.put("total", arr.size());
        } catch (BusinessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return createMsg(null, "-1", "查询异常" + e.getMessage());
        }
        return createMsg(data, "0", "分页查询账户实时余额成功");
    }

    /**
     * 生成返回信息统一方法
     *
     * @param data
     * @param code
     * @param msg
     * @return
     */
    private String createMsg(JSONObject data, String code, String msg) {
        Map<String, Object> msgMap = new HashMap<String, Object>();
        msgMap.put("data", data);
        msgMap.put("code", code);
        msgMap.put("msg", msg);
        return JSON.toJSONString(msgMap);
    }
}
