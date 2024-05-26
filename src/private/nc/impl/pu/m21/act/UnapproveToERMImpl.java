package nc.impl.pu.m21.act;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.trade.business.HYPubBO;
import nc.itf.pu.m21.IOrderQuery;
import nc.itf.pu.m21.act.UnapproveToERM;
import nc.ui.pcm.utils.GetDao;
import nc.vo.pu.m21.entity.OrderHeaderVO;
import nc.vo.pu.m21.entity.OrderVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;


public class UnapproveToERMImpl implements UnapproveToERM {

	@Override
	public void unApprove(String pk_order) throws BusinessException {
		// TODO Auto-generated method stub
		String[] ids = { pk_order };
		IOrderQuery service = NCLocator.getInstance().lookup(IOrderQuery.class);
		OrderVO[] aggvos = service.queryOrderVOsByIds(ids, UFBoolean.TRUE);
		if(aggvos != null && aggvos.length > 0){
			OrderVO aggvo = aggvos[0];
			String pk_defdoclist = (String) new HYPubBO().findColValue(
					"bd_defdoclist", "pk_defdoclist ",
					"nvl(dr,0) = 0 and code = 'ERMORG'");
			if (pk_defdoclist != null) {
				OrderHeaderVO hvo = (OrderHeaderVO) aggvo.getParentVO();
				// 采购订单取消审批校验二维码是否可取消审批
				int orgFlag = checkOrg(hvo.getPk_org(), pk_defdoclist);
				if (orgFlag == 1) {// 该组织需要校验
					// 调用二维码接口
					getERMOrderStatus(pk_order);
				}
			}
		}
	}
	// 验证组织是否校验
	public int checkOrg(String pkOrg, String pk_defdoclist) throws DAOException {
		int flag = 0;// 返回 0则doAction====1则继续
		String fg = "0";// 是否有当前组织
		String orgSql = "SELECT CODE FROM BD_DEFDOC WHERE PK_DEFDOCLIST = '"
				+ pk_defdoclist + "' and enablestate = '2'";
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		List<Object[]> resultList = getDao.query(orgSql);
		if (resultList.size() > 0 && resultList.get(0)[0] != null) {
			for (int i = 0; i < resultList.size(); i++) {
				Object[] item = resultList.get(i);
				if (pkOrg.equals(item[0])) {
					fg = "1";
				}
			}
		}
		if ("1".equals(fg)) {
			flag = 1;
			System.out.println("<1>当前组织[" + pkOrg + "]在验证范围之内！");
		}

		return flag;
	}
	// 调用接口
	private void getERMOrderStatus(String pk_order) throws BusinessException {
		String url = (String) new HYPubBO().findColValue("sys_config",
				"config_value", "config_key='ermurl_order'");
		Logger.error("url==" + url);
		if (StringUtils.isNotEmpty(url)) {
			Map<String, String> headers = new HashMap<>();
			/*
			 * headers.put("Content-Type", "application/x-www-form-urlencoded");
			 */
			headers.put("id", pk_order);
			Logger.error("发送ERM参数：" + headers.toString());
			String back = doGet(url, headers);
			Logger.error("ERM返回参数：" + back);
			if (back == null || "".equals(back)) {
				throw new BusinessException("二维码返回空信息");
			}
			JSONObject res = JSONObject.fromObject(back);
			if(res.getBoolean("success")){
				JSONObject data = JSONObject.fromObject(res.getString("data"));
				String ORDER_NO = data.getString("ORDER_NO");// 订单主键
				if (pk_order.equals(ORDER_NO)) {
					int ORDER_STATUS = Integer.parseInt(data
							.getString("ORDER_STATUS"));// 状态
					if (ORDER_STATUS == 1) {
						throw new BusinessException("二维码返回信息[该订单已制作二维码送货单不能取消审批，如需操作请联系二维码厂商！]");
					} else if (ORDER_STATUS == 2) {
						throw new BusinessException("二维码返回信息[该订单已在二维码系统中扫码待审核不能取消审批，如需操作请联系二维码厂商！]");
					} else if (ORDER_STATUS == 3) {
						throw new BusinessException("二维码返回信息[该订单已在二维码系统中完成审核不能取消审批，如需操作请联系二维码厂商！]");
					}
				}
			}else{
				throw new BusinessException("二维码返回提示["+res.getString("msg")+"]");
			}
		}
	}
	/**
     * 发送get请求
     * @param url 请求URL
     * @param param 请求参数 key:value url携带参数 或者无参可不填
     * @return
     */
    public static String doGet(String url, Map<String, String> param) {

        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();

        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            URI uri = builder.build();
            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);
// 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }
	/**
	 * 解析HttpEntity
	 * 
	 * @param entity
	 * @return
	 * @throws IOException
	 */
	private String entityToString(HttpEntity entity) throws IOException {
		String result = null;
		InputStreamReader inputStreamReader = null;
		try {
			if (entity != null) {
				long contentLength = entity.getContentLength();
				if (contentLength != -1L && contentLength < 2048L) {
					result = EntityUtils.toString(entity, "UTF-8");
				} else {
					inputStreamReader = new InputStreamReader(
							entity.getContent(), "UTF-8");
					CharArrayBuffer charArrayBuffer = new CharArrayBuffer(2048);
					char[] chars = new char[1024];

					int index;
					while ((index = inputStreamReader.read(chars)) != -1) {
						charArrayBuffer.append(chars, 0, index);
					}

					result = charArrayBuffer.toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			inputStreamReader.close();
		}

		return result;
	}
}
