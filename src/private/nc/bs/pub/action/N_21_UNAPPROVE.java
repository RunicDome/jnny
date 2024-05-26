package nc.bs.pub.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.bs.trade.business.HYPubBO;
import nc.itf.pu.m21.IOrderApprove;
import nc.ui.pcm.utils.GetDao;
import nc.vo.pu.m21.entity.OrderHeaderVO;
import nc.vo.pu.m21.entity.OrderVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.uap.pf.PFBusinessException;
import nc.ws.intf.HttpClient;
import net.sf.json.JSONObject;

/**
 * 采购订单取消审批校验二维码是否可取消审批
 * @author Xbx
 */
public class N_21_UNAPPROVE extends AbstractCompiler2 {
	private HYPubBO hyPubBO;
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
	
	public HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}
	public void setHyPubBO(HYPubBO hyPubBO) {
		this.hyPubBO = hyPubBO;
	} 
	
	public N_21_UNAPPROVE() {
	}

	public String getCodeRemark() {
		return "\tnc.vo.pu.m21.entity.OrderVO[] inObject  =(nc.vo.pu.m21.entity.OrderVO[])getVos ();\nObject retValue=nc.bs.framework.common.NCLocator.getInstance().lookup(nc.itf.pu.m21.IOrderApprove.class).unapprove(inObject, this);\nreturn retValue;\n";
	}

	public Object runComClass(PfParameterVO vo) throws BusinessException {
		try {
			this.m_tmpVo = vo;
			OrderVO[] inObject = (OrderVO[]) getVos();
			for(OrderVO aggvo : inObject){
				String pk_defdoclist = (String) getHyPubBO().findColValue("bd_defdoclist",
						"pk_defdoclist ", "nvl(dr,0) = 0 and code = 'ERMORG'");
				if(pk_defdoclist != null){
					OrderHeaderVO hvo = (OrderHeaderVO) aggvo.getParentVO();
					// 采购订单取消审批校验二维码是否可取消审批
					int orgFlag = checkOrg(hvo.getPk_org(),pk_defdoclist);
					if (orgFlag == 1) {// 该组织需要校验
						String pk_order = hvo.getPrimaryKey();// 单据主键
						// 调用二维码接口
						getERMOrderStatus(pk_order);
					}
				}
			}
			
			return ((IOrderApprove) NCLocator.getInstance().lookup(
					IOrderApprove.class)).unapprove(inObject, this);

		} catch (Exception ex) {

			if ((ex instanceof BusinessException)) {
				throw ((BusinessException) ex);
			}

			throw new PFBusinessException(ex.getMessage(), ex);
		}
	}
	private void getERMOrderStatus(String pk_order) throws BusinessException {
		String url = (String) new HYPubBO().findColValue("sys_config",
				"config_value", "config_key='ermurl_order'");
		Logger.error("url==" + url);
		if(StringUtils.isNotEmpty(url)){
			Map<String, String> headers = new HashMap<>();
			/*headers.put("Content-Type",
					"application/x-www-form-urlencoded");*/
			headers.put("id", pk_order);
			Logger.error("发送ERM参数："+headers.toString());
			String back = HttpClient.httpGet(url, headers, null);
			Logger.error("ERM返回参数："+back);
			JSONObject res = JSONObject.fromObject(back);
			JSONObject data = JSONObject.fromObject(res.getString("data"));
			String ORDER_NO = data.getString("ORDER_NO");// 订单主键
			if(pk_order.equals(ORDER_NO)){
				int ORDER_STATUS = Integer.parseInt(data.getString("ORDER_STATUS"));// 状态
				if(ORDER_STATUS == 1){
					throw new BusinessException("二维码返回提示[该订单已制作二维码送货单不能删除]");
				}else if(ORDER_STATUS == 2){
					throw new BusinessException("二维码返回提示[该订单已扫码待审核不能删除]");
				}else if(ORDER_STATUS == 3){
					throw new BusinessException("二维码返回提示[该订单已完成审核不能删除]");
				}
			}
		}
	}
	//  验证组织是否校验
	public int checkOrg(String pkOrg,String pk_defdoclist) throws DAOException {
		int flag = 0;// 返回 0则doAction====1则继续
		String fg = "0";// 是否有当前组织
		String orgSql = "SELECT NAME FROM BD_DEFDOC WHERE PK_DEFDOCLIST = '"+pk_defdoclist+"' and enablestate = '2'";
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
		}

		System.out.println("<1>当前组织[" + pkOrg + "]在验证范围之内！");
		return flag;
	}
}