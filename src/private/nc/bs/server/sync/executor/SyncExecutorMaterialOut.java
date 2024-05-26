package nc.bs.server.sync.executor;

import java.util.ArrayList;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.server.sync.AbstractDataInfoSyncExecutor;
import nc.bs.trade.business.HYPubBO;
import nc.pubitf.ic.m45.api.IPurchaseInQueryAPI;
import nc.pubitf.pu.m21.api.IOrderQueryAPI;
import nc.ui.querytemplate.operator.EqOperator;
import nc.vo.ic.general.define.MetaNameConst;
import nc.vo.ic.m45.entity.PurchaseInBodyVO;
import nc.vo.ic.m45.entity.PurchaseInHeadVO;
import nc.vo.ic.m45.entity.PurchaseInVO;
import nc.vo.ic.m4d.api.IMaterialOutVO;
import nc.vo.ic.m4d.entity.MaterialOutBodyVO;
import nc.vo.ic.m4d.entity.MaterialOutVO;
import nc.vo.ic.pub.calc.BusiCalculator;
import nc.vo.ic.pub.util.VOEntityUtil;
import nc.vo.org.OrgVO;
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
 * OA调用NC，生成材料出库单
 * 送现场：生成采购入库单，材料出库单参照入库单生成 (要和采购订单有关联关系)
 * @author xuwjl@yonyou.com
 * @date 2021年4月28日下午2:11:03
 */
public class SyncExecutorMaterialOut extends AbstractDataInfoSyncExecutor {

	@Override
	public MessageResult syncExecutor(JSONObject paramsJson) throws Exception {
		// String action = paramsJson.getString("action");

		PurchaseInVO purchaseInVO = generatePurchaseIn(paramsJson);
		Object save = iplatFormEntry.processAction("WRITE", "45", null, purchaseInVO, null, null);
		PurchaseInVO[] savevos = (PurchaseInVO[]) save;//保存后的采购入库单
		if (ArrayUtils.isEmpty(savevos)) {
			return new MessageResult(MessageResult.STATUS_ERROR, "返回值有误，保存采购入库单失败!");
		}
		JSONObject json = new JSONObject();
		
		JSONObject jsonIn = new JSONObject();
		jsonIn.put(IMaterialOutVO.CGENERALHID, savevos[0].getPrimaryKey());
		jsonIn.put(IMaterialOutVO.VBILLCODE, savevos[0].getHead().getVbillcode());
		JSONArray jsaIn = new JSONArray();
		for( PurchaseInBodyVO purchaseInBodyVO : savevos[0].getBodys()){
			JSONObject jsonbIn = new JSONObject();
			jsonbIn.put("cgeneralbid ", purchaseInBodyVO.getPrimaryKey());
			jsonbIn.put("nnum", purchaseInBodyVO.getNnum().toDouble());
			jsaIn.add(jsonbIn);
		}
		jsonIn.put("body", jsaIn);
		
		String primaryKey = savevos[0].getHead().getPrimaryKey();

		iplatFormEntry.processAction("SIGN", "45", null, savevos[0], null, null);

		IPurchaseInQueryAPI iPurchaseInQueryAPI = NCLocator.getInstance().lookup(IPurchaseInQueryAPI.class);
		PurchaseInVO[] purchaseInVOs = iPurchaseInQueryAPI.queryVOByIDs(new String[] { primaryKey });

		MaterialOutVO materialOutVO = this.getMaterialOutVO(purchaseInVOs[0],paramsJson);

		Object write = iplatFormEntry.processAction("WRITE", "4D", null, materialOutVO, null, null);//保存后的材料出库单
		MaterialOutVO[] materialOutVOs = (MaterialOutVO[])write;
		// XBX能投不签字
		String pk_org = materialOutVO.getParentVO().getPk_org();// 组织
		OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(OrgVO.class,
				pk_org);
		if(!"0001A21000000003U6L6".equals(orgVO.getPk_fatherorg()) && !"0001A21000000003U6L6".equals(pk_org)){
			iplatFormEntry.processAction("SIGN", "4D", null, materialOutVOs[0], null, null);
		}
		
		JSONObject jsonOut = new JSONObject();
		jsonOut.put(IMaterialOutVO.CGENERALHID, materialOutVOs[0].getPrimaryKey());
		jsonOut.put(IMaterialOutVO.VBILLCODE, materialOutVOs[0].getHead().getVbillcode());
		JSONArray jsaOut = new JSONArray();
		for(MaterialOutBodyVO materialOutBodyVO : materialOutVOs[0].getBodys()){
			JSONObject jsonbOut = new JSONObject();
			jsonbOut.put("cgeneralbid ", materialOutBodyVO.getPrimaryKey());
			jsonbOut.put("nnum", materialOutBodyVO.getNnum().toDouble());
			jsaOut.add(jsonbOut);
		}
		jsonOut.put("body", jsaOut);
		json.put("materialout", jsonOut);
		json.put("purchasein", jsonIn);
		return new MessageResult(MessageResult.STATUS_SUCCESS, "操作完成",json);
	}

	private MaterialOutVO getMaterialOutVO(PurchaseInVO purchaseInVO, JSONObject paramsJson) throws BusinessException {
		MaterialOutVO materialOutVO = (MaterialOutVO) iPfExchangeService.runChangeData("45", "4D", purchaseInVO, null);// 采购入库 → 材料出库
		materialOutVO.getHead().setCtrantypeid("1001A21000000005UPYB");
		materialOutVO.getHead().setVtrantypecode("4D-Cxx-01");
		
		materialOutVO.getHead().setCdptid(paramsJson.getString("cdptid"));
		materialOutVO.getHead().setCbizid(paramsJson.getString("cbizid"));
		materialOutVO.getHead().setCconstructvendorid(paramsJson.getString("cconstructvendorid"));//外部施工单位
		
		for (MaterialOutBodyVO materialOutBodyVO : materialOutVO.getBodys()) {
			String cmaterialoid = materialOutBodyVO.getCmaterialoid();
			materialOutBodyVO.setCsrcmaterialoid(cmaterialoid);
			materialOutBodyVO.setCsrcmaterialvid(cmaterialoid);
			UFDouble nassistnum = materialOutBodyVO.getNassistnum();// 实发数量
			UFDouble ncostprice = materialOutBodyVO.getNcostprice();// 单价
			materialOutBodyVO.setNcostmny(nassistnum.multiply(ncostprice).setScale(2, UFDouble.ROUND_UP));// 金额
		}
		return materialOutVO;
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
