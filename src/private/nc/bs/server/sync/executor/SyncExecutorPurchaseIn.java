package nc.bs.server.sync.executor;

import java.util.ArrayList;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.server.sync.AbstractDataInfoSyncExecutor;
import nc.pubitf.pu.m21.api.IOrderQueryAPI;
import nc.ui.querytemplate.operator.EqOperator;
import nc.vo.ic.general.define.MetaNameConst;
import nc.vo.ic.m45.api.IPurchaseInVO;
import nc.vo.ic.m45.entity.PurchaseInBodyVO;
import nc.vo.ic.m45.entity.PurchaseInHeadVO;
import nc.vo.ic.m45.entity.PurchaseInVO;
import nc.vo.ic.pub.calc.BusiCalculator;
import nc.vo.ic.pub.util.VOEntityUtil;
import nc.vo.pfxx.util.ArrayUtils;
import nc.vo.pu.m21.entity.OrderHeaderVO;
import nc.vo.pu.m21.entity.OrderItemVO;
import nc.vo.pu.m21.entity.OrderVO;
import nc.vo.pu.m21.rule.api.IOrderVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.scmpub.util.QuerySchemeBuilder;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yonyou.iuap.system.utils.MessageResult;

/**
 * 送仓库
 * 
 * @author xuwjl@yonyou.com
 * @date 2021年4月26日下午5:09:51
 */
public class SyncExecutorPurchaseIn extends AbstractDataInfoSyncExecutor {

	@Override
	public MessageResult syncExecutor(JSONObject paramsJson) throws Exception {
		// String action = paramsJson.getString("action");//1
		
		PurchaseInVO purchaseInVO = generatePurchaseIn(paramsJson);
		Object save = iplatFormEntry.processAction("WRITE", "45", null, purchaseInVO, null, null);
		PurchaseInVO[] savevos = (PurchaseInVO[]) save;
		if (ArrayUtils.isEmpty(savevos)) {
			return new MessageResult(MessageResult.STATUS_ERROR, "返回值有误，保存采购入库单失败!");
		}
		// XBX能投不签字
//		String pk_org = purchaseInVO.getParentVO().getPk_org();// 组织
//		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
//				pk_org);
//		if(!"0001A21000000003U6L6".equals(orgVO.getPk_fatherorg()) && !"0001A21000000003U6L6".equals(pk_org)){
//			String project_code = (String) new HYPubBO()
//					.findColValue(
//							"sm_user",
//							"project_code ",
//							"nvl(dr,0) = 0 and pk_project ='"
//									+ pk_project + "'");
//			InvocationInfoProxy.getInstance().setUserId("1001A2100000000142HX");// 设置默认操作员主键 
//			iplatFormEntry.processAction("SIGN", "45", null, savevos[0], null, null);
//		}
		String primaryKey = savevos[0].getPrimaryKey();
		JSONObject json = new JSONObject();
		json.put(IPurchaseInVO.CGENERALHID, primaryKey);
		json.put(IPurchaseInVO.VBILLCODE, savevos[0].getHead().getVbillcode());
		JSONArray jsa = new JSONArray();
		for(PurchaseInBodyVO purchaseInBodyVO : savevos[0].getBodys()){
			JSONObject jsonb = new JSONObject();
			jsonb.put("cgeneralbid", purchaseInBodyVO.getPrimaryKey());
			jsonb.put("nnum", purchaseInBodyVO.getNnum().toDouble());
			jsa.add(jsonb);
		}
		json.put("body", jsa);
		return new MessageResult(MessageResult.STATUS_SUCCESS, "操作完成",json);
	}

	private PurchaseInVO generatePurchaseIn(JSONObject paramsJson) throws BusinessException {
		OrderVO orderVO = this.getOrderVO(paramsJson.getString("pk_order"));
		String creator = paramsJson.getString("creator");
		if (StringUtils.isEmpty(creator)) {
			ExceptionUtils.wrappBusinessException("creator不能为空!");
		}
		InvocationInfoProxy.getInstance().setUserId(creator);
		PurchaseInVO purchaseInVO = (PurchaseInVO) iPfExchangeService.runChangeData("21", "45", orderVO, null);// 采购订单→采购入库

		PurchaseInHeadVO head = purchaseInVO.getHead();
		head.setStatus(VOStatus.NEW);
		String cwarehouseid = paramsJson.getString("cwarehouseid");// 仓库
		if (StringUtils.isEmpty(cwarehouseid)) {
			ExceptionUtils.wrappBusinessException("cwarehouseid不能为空!");
		}
		
		head.setCwarehouseid(cwarehouseid);
		head.setVtrantypecode("45-01");
		
		head.setBillmaker(creator);
		head.setCreator(creator);
		// XBX 20240325 是否二维码出入库
		head.setVdef7("Y");
		JSONArray jsonArray = paramsJson.getJSONArray("body");
		PurchaseInBodyVO[] bodys = purchaseInVO.getBodys();
		BusiCalculator calc = BusiCalculator.getBusiCalculatorAtBS();
		
		ArrayList<PurchaseInBodyVO> list = new ArrayList<PurchaseInBodyVO>();
		for (PurchaseInBodyVO purchaseInBodyVO : bodys) {
			for (Object jsonObject : jsonArray) {
				JSONObject jsonBody = (JSONObject) jsonObject;
				String pk_order_b = jsonBody.getString("pk_order_b");// 采购订单子表主键
				String csourcebillbid = purchaseInBodyVO.getCsourcebillbid();// 采购入库来源单据子表主键
				if (StringUtils.equals(csourcebillbid, pk_order_b)) {
					String nnumStr = jsonBody.getString("nnum");// 实收数量
					if (StringUtils.isEmpty(nnumStr)) {
						ExceptionUtils.wrappBusinessException("nnum不能为空!");
					}
					UFDouble nnum = new UFDouble(nnumStr);

					purchaseInBodyVO.setNnum(nnum);// 实收主数量
					purchaseInBodyVO.setNassistnum(nnum);// 实收数量
					purchaseInBodyVO.setNqtunitnum(nnum);// 报价数量

					OrderItemVO[] orderItemVOs = (OrderItemVO[]) VOEntityUtil.filterVOWhenFieldEquals(orderVO.getBVO(), new String[] { "pk_order_b" }, new String[] { pk_order_b });
					if (ArrayUtils.isNotEmpty(orderItemVOs)) {
						purchaseInBodyVO.setNshouldnum(orderItemVOs[0].getNastnum());// 应收主数量
						purchaseInBodyVO.setNshouldassistnum(orderItemVOs[0].getNastnum());// 应收数量
					}
					purchaseInBodyVO.setNvolume(UFDouble.ZERO_DBL);// 体积
					purchaseInBodyVO.setNweight(UFDouble.ZERO_DBL);// 重量

					purchaseInBodyVO.setCqtunitid(purchaseInBodyVO.getCastunitid());// 报价单位
					purchaseInBodyVO.setCfanaceorgoid((String) purchaseInVO.getParentVO().getAttributeValue("cfanaceorgoid"));// 结算财务组织
					purchaseInBodyVO.setPk_batchcode(null);
					purchaseInBodyVO.setVbatchcode(null);

					purchaseInBodyVO.setCSnunitid(null);// 序列号单位
					purchaseInBodyVO.setCbodywarehouseid(cwarehouseid);// 库存仓库
					purchaseInBodyVO.setDbizdate(purchaseInVO.getParentVO().getDbilldate());// 入库日期
					calc.calcMny(new PurchaseInBodyVO[] { purchaseInBodyVO }, MetaNameConst.NNUM);
					list.add(purchaseInBodyVO);
				}
			}
		}
		PurchaseInBodyVO[] purchaseInBodyVOs = list.toArray(new PurchaseInBodyVO[list.size()]);
		purchaseInVO.setChildrenVO(purchaseInBodyVOs);
		return purchaseInVO;
	}

	private OrderVO getOrderVO(String pk_order) throws BusinessException {
		if (StringUtils.isEmpty(pk_order)) {
			ExceptionUtils.wrappBusinessException("pk_order不能为空!");
		}
		IOrderQueryAPI query = NCLocator.getInstance().lookup(IOrderQueryAPI.class);
		QuerySchemeBuilder builder = QuerySchemeBuilder.buildByFullClassName(OrderHeaderVO.class.getName());
		builder.append(IOrderVO.PK_ORDER, EqOperator.getInstance(), new String[] { pk_order });
		OrderVO[] vos = query.queryVOByScheme(builder.create());
		if (ArrayUtils.isEmpty(vos)) {
			ExceptionUtils.wrappBusinessException("根据" + pk_order + "查不到采购订单!");
		}
		return vos[0];
	}

}
