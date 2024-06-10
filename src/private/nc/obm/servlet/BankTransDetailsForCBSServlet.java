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
import nc.vo.bd.bankaccount.BankAccbasVO;
import nc.vo.bd.bankdoc.BankdocVO;
import nc.vo.bd.banktype.BankTypeVO;
import nc.vo.logging.Debug;
import nc.vo.obm.ebankdzd.EbankDzdVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

// CBS调用接口，获取NC银行交易明细信息
@SuppressWarnings({ "restriction" })
public class BankTransDetailsForCBSServlet extends HttpServlet implements
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
        Debug.error("=============CBS getBankTransDetails msg ：" + body);
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
            request = getYHTransDetailsInfo(jsonObject);
        } catch (BusinessException e) {
            // TODO Auto-generated catch block
            request = createMsg(null, "-1", "异常！" + e.getMessage());
        }
        System.out.println("=================" + request);
        res.getWriter().write(request);// 输出body
    }

    private String getYHTransDetailsInfo(JSONObject body)
            throws BusinessException {

        InvocationInfoProxy.getInstance().setGroupId("0001A1100000000001QS");
        /* 通过传入参数-银行账号获取银行余额数据 */
        EbankDzdVO[] dzdvos = getEbankDzdVOBywhere(body);
        /* 返回数据 */
        return toDataJson(dzdvos, body);
    }

    // 查询需推送CBS系统的银行单据
    private EbankDzdVO[] getEbankDzdVOBywhere(JSONObject body)
            throws BusinessException {
        /* 开始日期 格式为yyyy-mm-dd */
        String startDate = body.getString("startDate");
        /* 结束日期 格式为yyyy-mm-dd */
        String endDate = body.getString("endDate");
        String wheresql = " trans_date >= '" + startDate
                + "' and trans_date <= '" + endDate + "' ";
        if (body.containsKey("accountNoList")) {
            JSONArray accountNoList = body.getJSONArray("accountNoList");
            if (accountNoList != null && accountNoList.size() > 0) {
                wheresql += " and curacc in (";
                for (Object accnumObj : accountNoList) {
                    wheresql += "'" + accnumObj + "',";
                }
                wheresql = wheresql.substring(0, wheresql.length() - 1);
                wheresql += ")";
            }
        }
        // TODO正式删除
        wheresql += " and curacc = '376150100100003460'";
        EbankDzdVO[] bankVOs = (EbankDzdVO[]) new HYPubBO().queryByCondition(
                EbankDzdVO.class, wheresql);
        return bankVOs;
    }

    private String toDataJson(EbankDzdVO[] dzdvos, JSONObject body)
            throws BusinessException {
        if (dzdvos == null || dzdvos.length <= 0) {
            return createMsg(null, "-1", "false");
        }
        int currentPage = body.getIntValue("currentPage");
        int pageSize = body.getIntValue("pageSize");
        if (dzdvos.length <= pageSize) {
            pageSize = dzdvos.length;
        }
        EbankDzdVO[] needVOs = new EbankDzdVO[pageSize];
        int startIndex = (currentPage - 1) * pageSize;
        System.arraycopy(dzdvos, startIndex, needVOs, 0, needVOs.length);
        JSONObject data = new JSONObject();
        JSONArray arr = new JSONArray();
        for (EbankDzdVO dzdvo : needVOs) {
            BankAccbasVO yhvo = getBankVOBywhere(dzdvo.getCuracc());
            // BankAccSubVO zhvo = yhvo.getBankaccsub()[0];// 银行账户1对1银行账户子户
            // data.put("endRow", 1);
            // data.put("hasNextPage", Boolean.FALSE);
            // data.put("hasPreviousPage", Boolean.FALSE);
            // data.put("isFirstPage", Boolean.TRUE);
            // data.put("isLastPage", Boolean.TRUE);
            JSONObject listJson = new JSONObject();
            /* 银行账号 */
            listJson.put("accountNo", yhvo.getAccnum());
            /* 账户名称 */
            listJson.put("accountName", yhvo.getAccname());
            /* 银行类别 */
            BankTypeVO typeVO = (BankTypeVO) new HYPubBO().queryByPrimaryKey(
                    BankTypeVO.class, yhvo.getPk_banktype());
            listJson.put("bankType", typeVO.getMnecode());
            /* 开户银行 */
            BankdocVO khh = (BankdocVO) new HYPubBO().queryByPrimaryKey(
                    BankdocVO.class, yhvo.getPk_bankdoc());
            listJson.put("openBank", khh.getName());
            /* 交易日期 */
            listJson.put("bankTransactionDate", dzdvo.getTrans_date()
                    .toStdString());
            /* 交易流水号 */
            listJson.put("transactionSerialNumber", dzdvo.getTxseqid());
            /* 币种 */
            listJson.put("currency", "10");
            /* 币种名称 */
            listJson.put("currencyName", "人民币");

            /* 付款->贷，1-借;2-贷 */
            if (dzdvo.getDbtrsamt() != null
                    && dzdvo.getDbtrsamt().toDouble() > 0) {
                listJson.put("loanType", "2");
                /* 发生额 */
                listJson.put("incurredAmount", dzdvo.getDbtrsamt().toDouble());
                /* 交易后余额 */
                listJson.put("accountBalance", dzdvo.getDbtbalance().toDouble());
            } else {
                listJson.put("loanType", "1");
                /* 发生额 */
                listJson.put("loanType", dzdvo.getCrtrsamt().toDouble());
                /* 交易后余额 */
                listJson.put("accountBalance", dzdvo.getCrtbalance().toDouble());
            }
            /* 用途 */
            listJson.put("purpose", dzdvo.getNusage());
            /* 摘要 */
            listJson.put("digest", dzdvo.getTrans_abstr());
            /* 对方账号 */
            listJson.put("oppositeAccount", dzdvo.getOppacc());
            /* 对方户名 */
            listJson.put("oppositeName", dzdvo.getOppname());
            /* 账户状态 */
            listJson.put("accountStatus", "0");

            /* 单位编码 */
            OrgVO orgvo = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
                    yhvo.getPk_org());
            listJson.put("unitCode", orgvo.getCode());
            /* 单位名称 */
            listJson.put("unitName", orgvo.getName());
            arr.add(listJson);
            // data.put("navigateFirstPage", 1);
            // data.put("navigateLastPage", 8);
            // 所有导航页号list
            // JSONArray navigatepageNumsArr = new JSONArray();
            // navigatepageNumsArr.add(1);
            // data.put("navigatepageNums", navigatepageNumsArr);
            data.put("nextPage", currentPage + 1);
            data.put("pageNum", currentPage);
            data.put("pageSize", pageSize);
            data.put("pages", (dzdvos.length + pageSize - 1) / pageSize);
            data.put("prePage", currentPage - 1);
            data.put("size", pageSize);
            data.put("startRow", startIndex + 1);
            data.put("total", dzdvos.length);
        }
        data.put("list", arr);
        return createMsg(data, "0", "ok");
    }

    // 查询需推送CBS系统的银行单据
    private BankAccbasVO getBankVOBywhere(String accnum)
            throws BusinessException {
        BankAccbasVO[] bankVOs = (BankAccbasVO[]) new HYPubBO()
                .queryByCondition(BankAccbasVO.class,
                        " nvl(dr,0) = 0 and accnum = '" + accnum + "'");
        return bankVOs == null ? null : bankVOs[0];
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
