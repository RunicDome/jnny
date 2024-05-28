package nc.ws.intf.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
import nc.ct.servlet.CtPriceCalculator;
import nc.itf.ct.purdaily.IPurdailyMaintain;
import nc.itf.pubapp.pub.smart.IBillQueryService;
import nc.itf.uap.pf.IPfExchangeService;
import nc.vo.bd.taxcode.TaxrateVO;
import nc.vo.ct.purdaily.entity.AggCtPuVO;
import nc.vo.ct.purdaily.entity.CtPuBVO;
import nc.vo.ct.purdaily.entity.CtPuVO;
import nc.vo.pu.m20.entity.PraybillItemVO;
import nc.vo.pu.m20.entity.PraybillVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.sm.UserVO;
import nc.vo.uap.busibean.exception.BusiBeanException;
import nc.ws.intf.Result;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * 供应商平台请购单生成采购合同
 *
 * @author jor
 */
public class SupplierCtPuService extends BarcodeService {
	private HYPubBO hyPubBO;

	public HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	/**
	 * json: {"funCode":"saveCtPu","billType":"SupplierCtPu","billType2":"",
	 * "condition"
	 * :"","condition2":"","map":{"userId":"200021","headVO":{"cemployeeid"
	 * :"20200001"
	 * ,"corigcurrencyid":"CNY"},"bodyVOS":[{"pk_praybill_b":"305","price"
	 * :"MTR","nastnum":30.5}]}}
	 **/
	public Object saveCtPu(Object head, Object body, Object map)
			throws Exception {
		JSONObject map2 = (JSONObject) map;
		String cuserId = map2.has("userId") ? map2.getString("userId")
				: "1001A110000000000HV8";
		JSONArray bodyVOS = map2.getJSONArray("bodyVOS");
		JSONObject headVO = map2.getJSONObject("headVO");
		InvocationInfoProxy.getInstance().setUserId(cuserId);
		/* <step-1>根据子表中请购单子表主键查询请购单 */
		String[] pk_praybill_bs = new String[bodyVOS.length()];
		Map<String, PraybillVO> prayMap = new HashMap<>();
		for (int i = 0; i < bodyVOS.length(); i++) {
			JSONObject bodyVo = bodyVOS.getJSONObject(i);
			pk_praybill_bs[i] = bodyVo.getString("pk_praybill_b");
			PraybillItemVO itemvo = (PraybillItemVO) getHyPubBO()
					.queryByPrimaryKey(PraybillItemVO.class, pk_praybill_bs[i]);
			if (itemvo != null) {
				IBillQueryService billquery = NCLocator.getInstance().lookup(
						IBillQueryService.class);
				PraybillVO praybillVO = (PraybillVO) billquery
						.querySingleBillByPk(PraybillVO.class,
								itemvo.getPk_praybill());
				if (!prayMap.containsKey(praybillVO.getPrimaryKey())) {
					prayMap.put(praybillVO.getPrimaryKey(), praybillVO);
				}
			}
		}
		/* <step-2>根据单据转换规则生成采购合同 */
		IPfExchangeService pf = NCLocator.getInstance().lookup(
				IPfExchangeService.class);
		AggCtPuVO[] aggCtPuVOs = (AggCtPuVO[]) pf.runChangeDataAryNeedClassify(
				"20",
				"Z2-01",
				(PraybillVO[]) prayMap.values().toArray(
						new PraybillVO[prayMap.size()]), null, 1);
		AggCtPuVO aggCtPuVO = aggCtPuVOs[0];
		/* <step-3>循环采购合同子表，匹配json子表请购单子表主键，处理采购合同子表数量和含税单价字段，计算采购合同 */
		CtPuVO ctHeadVO = aggCtPuVO.getParentVO();
		// 业务员
		if (!headVO.has("cemployeeid")) {
			UserVO uservo = (UserVO) getHyPubBO().queryByPrimaryKey(
					UserVO.class, headVO.getString("cemployeeid"));
			ctHeadVO.setPersonnelid(uservo.getPk_psndoc());
		}
		/*数据校验*/
		checkData(headVO);
		/* 修改表头数据 */
		ctHeadVO.setCtname(headVO.getString("ctname"));
		ctHeadVO.setValdate(new UFDate(headVO.getString("valdate")));
		ctHeadVO.setInvallidate(new UFDate(headVO.getString("invallidate")));
		ctHeadVO.setCtrantypeid("0001A21000000000PFYT"); // 采购合同通用类型
		ctHeadVO.setBbracketOrder(new UFBoolean("N"));
		ctHeadVO.setVtrantypecode("Z2-01");
		ctHeadVO.setCvendorid(headVO.getString("pk_supplier"));// 供应商
		ctHeadVO.setVdef52(headVO.getString("vbillcode"));// 供应商平台采购合同号
		/* 修改明细数据 */
		Map<String, CtPuBVO> newBodys = new HashMap<String, CtPuBVO>();
		for (int i = 0; i < aggCtPuVO.getCtPuBVO().length; i++) {
			CtPuBVO bvo = aggCtPuVO.getCtPuBVO()[i];// 采购合同明细
			for (int qgi = 0; qgi < bodyVOS.length(); qgi++) {
				JSONObject qgmxJson = (JSONObject) bodyVOS.get(qgi);
				if (qgmxJson.getString("pk_praybill_b").equals(
						bvo.getPk_praybill_b())) {
					UFDouble nastnum = new UFDouble(qgmxJson.getString("nastnum"));// 数量
					UFDouble norigtaxprice = new UFDouble(qgmxJson.getString("price"));// 含税单价
					UFDouble ntaxrate = qgmxJson.has("ntaxrate") ? new UFDouble(
							qgmxJson.getDouble("ntaxrate")) : null;
					bvo.setNnum(nastnum);
					bvo.setNastnum(nastnum);
					bvo.setNqtunitnum(nastnum);
					bvo.setNqtorigtaxprice(norigtaxprice);
					bvo.setNorigtaxmny(norigtaxprice.multiply(nastnum));
					bvo.setCrowno((i + 1) * 10 + "");
					if (ntaxrate == null) {
						// 查找材料对应的税码，税率
						String pk_mattaxes = (String) getHyPubBO()
								.findColValue(
										"bd_material",
										"pk_mattaxes",
										"nvl(dr,0) = 0 and pk_material='"
												+ bvo.getPk_material() + "'");
						String pk_taxcode = (String) getHyPubBO().findColValue(
								"bd_taxcode",
								"pk_taxcode",
								"nvl(dr,0) = 0 and mattaxes='" + pk_mattaxes
										+ "'");
						bvo.setCtaxcodeid(pk_taxcode);
						TaxrateVO[] taxrateVO = (TaxrateVO[]) getHyPubBO()
								.queryByCondition(
										TaxrateVO.class,
										"nvl(dr,0) = 0 and pk_taxcode='"
												+ pk_taxcode + "'");
						if (null != taxrateVO && taxrateVO.length >= 1) {
							UFDouble taxrate = taxrateVO[0].getTaxrate();
							bvo.setNtaxrate(taxrate);
						}
					}else{
						bvo.setNtaxrate(ntaxrate);
					}
					// 税码
					if(qgmxJson.has("ctaxcodeid")){
						bvo.setCtaxcodeid(qgmxJson.getString("ctaxcodeid"));
					}
					CtPriceCalculator cpcalt = new CtPriceCalculator();
					cpcalt.relationCalculatePrice(bvo);
					bvo.setNorigmny(bvo.getNorigprice().multiply(nastnum)); // 无税金额
					bvo.setNtax(bvo.getNorigtaxmny().sub(bvo.getNorigmny())); // 税额
					bvo.setNtaxmny(bvo.getNorigtaxmny()); // 本币价税合计
					bvo.setNqtprice(bvo.getNorigprice()); // 报价本币无税单价
					bvo.setNqttaxprice(bvo.getNorigtaxprice()); // 报价本币含税单价
					bvo.setNmny(bvo.getNorigmny()); // 本币无税金额
					bvo.setNcalcostmny(bvo.getNorigmny()); // 记成本金额
					bvo.setNcaltaxmny(bvo.getNorigmny()); // 计税金额
					bvo.setFbuysellflag(2); // 购销类型： 默认：2 国内采购
					bvo.setCsendcountryid("0001Z010000000079UJJ"); // 发货国家/地区
																	// 默认：0001Z010000000079UJJ
																	// 中国
					newBodys.put(qgmxJson.getString("pk_praybill_b"), bvo);
				}
			}
		}
		List<CtPuBVO> ctPuBVOs = new ArrayList<CtPuBVO>();
		for (Map.Entry<String, CtPuBVO> entry : newBodys.entrySet()) {
			ctPuBVOs.add(entry.getValue());
		}
		CtPuBVO[] newBodyVOs = ctPuBVOs.toArray(new CtPuBVO[ctPuBVOs.size()]);
		aggCtPuVO.setChildrenVO(newBodyVOs);
		/* <step-4>保存采购合同 */
		AggCtPuVO[] ctsavevos = NCLocator.getInstance()
				.lookup(IPurdailyMaintain.class)
				.save(new AggCtPuVO[] { aggCtPuVO }, null, null);
		return Result.success("ok", ctsavevos[0].getParentVO().getVbillcode()
				+ "," + ctsavevos[0].getPrimaryKey());
	}

	private void checkData(JSONObject headVO) throws BusinessException, JSONException {
		// TODO Auto-generated method stub
		/* 合同号 */
		if (!headVO.has("vbillcode")) {
			throw new BusiBeanException("合同号不能为空！");
		}
		/* 合同名称 */
		if (!headVO.has("ctname")) {
			throw new BusiBeanException("合同名称不能为空！");
		}
		/* 计划生效日期 */
		if (!headVO.has("valdate")) {
			throw new BusiBeanException("计划生效日期不能为空！");
		}
		/* 计划终止日期 */
		if (!headVO.has("invallidate")) {
			throw new BusiBeanException("计划终止日期不能为空！");
		}
		/* 供应商 */
		if(!headVO.has("pk_supplier")){
			throw new BusiBeanException("供应商不能为空！");
		}
		/* 合同是否重复*/
		String vbillcode = headVO.getString("vbillcode");
		CtPuVO[] ctpuvos = (CtPuVO[]) getHyPubBO()
				.queryByCondition(CtPuVO.class, " nvl(dr,0) = 0 and vdef52 = '"+vbillcode+"'");
		if(ctpuvos != null && ctpuvos.length > 0){
			throw new BusiBeanException("合同已推送，请勿重复推送！");
		}
	}

}
