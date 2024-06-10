package nc.itf.bd.material.servlet;

import com.alibaba.fastjson.JSONObject;
import nc.bs.framework.adaptor.IHttpServletAdaptor;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.security.token.ISecurityTokenCache;
import nc.bs.framework.server.ISecurityTokenCallback;
import nc.bs.trade.business.HYPubBO;
import nc.itf.bd.material.baseinfo.IMaterialBaseInfoService;
import nc.vo.bd.material.MaterialVO;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.ws.intf.BillLogVO;
import nc.ws.intf.Result;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MaterialServlet extends HttpServlet implements
        IHttpServletAdaptor {
    private static final long serialVersionUID = 1L;
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doAction(req, res);
    }
    public void doAction(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // 设置Token
        ISecurityTokenCallback sc = (ISecurityTokenCallback) NCLocator
                .getInstance().lookup(ISecurityTokenCallback.class);
        byte[] token = sc.token("NCSystem".getBytes(), "pfxx".getBytes());

        ISecurityTokenCache tokencache = (ISecurityTokenCache) NCLocator
                .getInstance().lookup(ISecurityTokenCache.class);
        tokencache.isContain(token);
        req.setCharacterEncoding("utf-8");
        // 获取请求参数
        BufferedReader br = req.getReader();
        String body = "";
        String line = null;
        while ((line = br.readLine()) != null) {
            body = body + line;
        }
        Debug.error("=============material disabled msg ：" + body);
        // 处理请求
        String request = disableMaterial(body);
        try {
            request += SaveBillLog(body, request);// 保存日志
        } catch (BusinessException e) {
            e.printStackTrace();
            request += "保存日志报错：" + e.getMessage();
        }
        res.setCharacterEncoding("utf-8");
        System.out.println("=================" + request);
        res.setStatus(200);
        res.getWriter().write(request);
    }

    /*处理请求，停用物料*/
    private String disableMaterial(String body) {
        try {
            JSONObject data = JSONObject.parseObject(body);
            if(data.containsKey("pk_material")) {
                /*物料主键*/
                String pk_material = data.getString("pk_material");
                MaterialVO materialVO = (MaterialVO) new HYPubBO()
                        .queryByPrimaryKey(MaterialVO.class, pk_material);
                if(materialVO != null){
                    IMaterialBaseInfoService materialBaseInfoService = (IMaterialBaseInfoService) NCLocator
                            .getInstance().lookup(IMaterialBaseInfoService.class.getName());
                    materialBaseInfoService.disableMaterial(new MaterialVO[]{materialVO});
                    return Result.success("物料编码："+materialVO.getCode()+"停用操作成功！");
                }
            }
            return Result.error("物料主键不能为空！");
        }catch (BusinessException e){
            return Result.error("物料停用接口异常："+e.getMessage());
        }
    }

    /*保存日志*/
    private String SaveBillLog(String recdata, String rdtdata)
            throws BusinessException {
        // TODO Auto-generated method stub
        BillLogVO vo = new BillLogVO();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        vo.setCreationtime(df.format(new Date()));// 创建时间
        vo.setTransi_type("Material"); // 交易类型
        if (recdata.length() <= 4000) {
            vo.setRecdata(recdata);// 接收参数
        }
        vo.setRdtdata(rdtdata);// 返回参数
        vo.setPk_group("0001A1100000000001QS");
        try {
            new HYPubBO().insert(vo);
        } catch (BusinessException e) {
            // TODO: handle exception
            return "生成物料停用日志表保存失败：" + e.getMessage();
        }
        return "";
    }

}
