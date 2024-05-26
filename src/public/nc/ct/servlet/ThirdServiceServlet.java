package nc.ct.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONObject;

import com.alibaba.fastjson.JSON;

import nc.bs.framework.adaptor.IHttpServletAdaptor;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.security.token.ISecurityTokenCache;
import nc.bs.framework.server.ISecurityTokenCallback;
import nc.bs.pub.pf.PfUtilTools;
import nc.bs.trade.business.HYPubBO;
import nc.itf.ct.purdaily.IPurdailyMaintain;
import nc.vo.bd.taxcode.TaxrateVO;
import nc.vo.ct.purdaily.entity.AggCtPuVO;
import nc.vo.ct.purdaily.entity.CtPuBVO;
import nc.vo.ct.purdaily.entity.CtPuVO;
import nc.vo.logging.Debug;
import nc.vo.pu.m20.entity.PraybillHeaderVO;
import nc.vo.pu.m20.entity.PraybillItemVO;
import nc.vo.pu.m20.entity.PraybillVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

@SuppressWarnings("restriction")
public class ThirdServiceServlet extends HttpServlet implements IHttpServletAdaptor{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static String BODYTABLE = "po_praybill_b";

	private static String HEADTABLE = "po_praybill";
	
	private static HYPubBO hypubBo = new HYPubBO();
	//继承HttpServlet
	//实现IHttpServletAdaptor
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	//实现post请求，调用IHttpServletAdaptor下的doAction方法
		doAction(req, res);
	}

	@Override
	public void doAction(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		ISecurityTokenCallback sc = NCLocator.getInstance().lookup(ISecurityTokenCallback.class); 
		byte[] token = sc.token("NCSystem".getBytes(), "pfxx".getBytes());
		// token存入NC系统的缓存
		ISecurityTokenCache tokencache = (ISecurityTokenCache)NCLocator.getInstance().lookup(ISecurityTokenCache.class);
		tokencache.isContain(token);
		req.setCharacterEncoding("utf-8");//设置编码规则
		BufferedReader br = req.getReader();//读取HttpServletRequest对象里的数据
		String body="";
		String line=null;
		while((line=br.readLine())!=null) {
			body+=line;
		}
		Debug.error("=============Diancai  To  NC 电采上传采购合同报文信息：" + body);
		String request = createAggCtSaleVOByPraybillVO(body);
		res.setCharacterEncoding("utf-8");
		System.out.println("================="+request);
		res.setStatus(200);
		res.getWriter().write(request);//输出body
	}

	private String createAggCtSaleVOByPraybillVO(String body) {
		String errorMsg = "";
		try {
			InvocationInfoProxy.getInstance().setGroupId("0001A1100000000001QS");
			List<Map> paramList = new ArrayList<Map>();
			JSONObject jsonObj = new JSONObject(body);
			String arr = jsonObj.getString("data");
			paramList =  JSON.parseArray(arr, Map.class);
			String ctcodeMsg = "";
			for (Map<String, Object> map : paramList) {
				if ((!map.containsKey(HEADTABLE))
						|| (!map.containsKey(BODYTABLE))) {
					errorMsg = "合同数据格式不正确，请正确按照报文格式传输数据！";
					Debug.error(errorMsg);
					return createMsg(false, "-1", errorMsg);
//					throw new BusinessException("数据格式不正确");
				}
				// 表头
				Map<String, String> headInfo = (Map) map.get(HEADTABLE);
				// 请购单号
				String vbillcode = headInfo.get("vbillcode");
				if(isEmpty(vbillcode)) {
					errorMsg = "表头vbillcode（请购单号）不允许为空！";
					return createMsg(false, "-1", errorMsg);
				}
				String ctname = headInfo.get("ctname");
				if(isEmpty(ctname)) {
					errorMsg = "表头ctname（合同名称）不允许为空！";
					return createMsg(false, "-1", errorMsg);
				}
				String ctcode = headInfo.get("ctcode");
				if(isEmpty(ctcode)) {
					errorMsg = "表头ctcode（合同编码）不允许为空！";
					return createMsg(false, "-1", errorMsg);
				}
				// 对合同进行唯一性验证
				String unic_pk_ct_pu = (String) hypubBo.findColValue("ct_pu", "pk_ct_pu", "nvl(dr,0) = 0 and vbillcode = '" + ctcode + "'");
				if(!isEmpty(unic_pk_ct_pu)) {
					errorMsg = "合同编号："+ctcode+"已经存在，不允许重复提交！";
					return createMsg(false, "-1", errorMsg);
				}
				String pk_org = headInfo.get("pk_org");
				if(isEmpty(pk_org)) {
					errorMsg = "表头pk_org（采购组织电采ID）不允许为空！";
					return createMsg(false, "-1", errorMsg);
				}
				// 通过对照表获取NC对应组织
				String conn_pk_org = (String) hypubBo.findColValue("conn_group_elec", "pk_org", "nvl(dr,0) = 0 and eleccode = '" + pk_org + "'");
				if(isEmpty(conn_pk_org)) {
					errorMsg = "表头pk_org（"+pk_org+"）在NC对照表中未获取到数据，请保证此pk_org已在NC对照表中配置！";
					return createMsg(false, "-1", errorMsg);
				}
				String creator = headInfo.get("creator");
				if(isEmpty(creator)) {
					errorMsg = "表头creator（创建人电采ID）不允许为空！";
					return createMsg(false, "-1", errorMsg);
				}
				String conn_creator = (String) hypubBo.findColValue("conn_user_elec", "pk_user", "nvl(dr,0) = 0 and eleccode = '" + creator + "'");
				if(isEmpty(conn_creator)) {
					errorMsg = "表头creator（"+creator+"）在NC对照表中未获取到数据，请保证此creator已在NC对照表中配置！";
					return createMsg(false, "-1", errorMsg);
				}
				// 从sm_user获取用户cuserid
				String cuserid_creator = (String) hypubBo.findColValue("SM_USER", "cuserid", "nvl(dr,0) = 0 and pk_psndoc = '" + conn_creator + "'");
				if(isEmpty(cuserid_creator)) {
					errorMsg = "表头creator（"+creator+"）在NC--SM-USER表中未获取到数据！";
					return createMsg(false, "-1", errorMsg);
				}
				String billmaker = headInfo.get("billmaker");
				if(isEmpty(billmaker)) {
					errorMsg = "表头billmaker（制单人电采ID）不允许为空！";
					return createMsg(false, "-1", errorMsg);
				}
				String conn_billmaker = (String) hypubBo.findColValue("conn_user_elec", "pk_user", "nvl(dr,0) = 0 and eleccode = '" + billmaker + "'");
				if(isEmpty(conn_creator)) {
					errorMsg = "表头billmaker（"+billmaker+"）在NC对照表中未获取到数据，请保证此billmaker已在NC对照表中配置！";
					return createMsg(false, "-1", errorMsg);
				}
				// 从sm_user获取用户cuserid
				String cuserid_billmaker = (String) hypubBo.findColValue("SM_USER", "cuserid", "nvl(dr,0) = 0 and pk_psndoc = '" + conn_billmaker + "'");
				if(isEmpty(cuserid_billmaker)) {
					errorMsg = "表头billmaker（"+billmaker+"）在NC--SM-USER表中未获取到数据！";
					return createMsg(false, "-1", errorMsg);
				}
				String cvendorid = headInfo.get("cvendorid");
				if(isEmpty(cvendorid)) {
					errorMsg = "表头cvendorid（供应商）不允许为空！";
					return createMsg(false, "-1", errorMsg);
				}
				// 从bd_supplier获取供应商
				String pk_supplier = (String) hypubBo.findColValue("bd_supplier", "pk_supplier", "nvl(dr,0) = 0 and code = '" + cvendorid + "'");
				if(isEmpty(pk_supplier)) {
					errorMsg = "表头cvendorid（"+cvendorid+"）在NC--bd_supplier表中未获取到数据！";
					return createMsg(false, "-1", errorMsg);
				}
				String subscribedate = headInfo.get("subscribedate");
				if(isEmpty(subscribedate)) {
					errorMsg = "表头subscribedate（合同签订日期）不允许为空！";
					return createMsg(false, "-1", errorMsg);
				}
				String valdate = headInfo.get("valdate");
				if(isEmpty(valdate)) {
					errorMsg = "表头valdate（计划生效日期）不允许为空！";
					return createMsg(false, "-1", errorMsg);
				}
				String invallidate = headInfo.get("invallidate");
				if(isEmpty(invallidate)) {
					errorMsg = "表头invallidate（计划终止日期）不允许为空！";
					return createMsg(false, "-1", errorMsg);
				}
				String depid = headInfo.get("depid");
				if(isEmpty(depid)) {
					errorMsg = "表头depid（部门）不允许为空！";
					return createMsg(false, "-1", errorMsg);
				}
				String conn_depid = (String) hypubBo.findColValue("conn_org_elec", "pk_org", "nvl(dr,0) = 0 and eleccode = '" + depid + "'");
				if(isEmpty(conn_depid)) {
					errorMsg = "表头depid（"+depid+"）在NC对照表中未获取到数据，请保证此depid已在NC对照表中配置！";
					return createMsg(false, "-1", errorMsg);
				}
				// 获取部门版本信息
				String conn_depid_v = (String) hypubBo.findColValue("ORG_DEPT", "pk_vid", "nvl(dr,0) = 0 and pk_dept = '" + conn_depid + "'");
				String personnelid = headInfo.get("personnelid");
				if(isEmpty(personnelid)) {
					errorMsg = "表头personnelid（人员）不允许为空！";
					return createMsg(false, "-1", errorMsg);
				}
				String conn_personnelid = (String) hypubBo.findColValue("conn_user_elec", "pk_user", "nvl(dr,0) = 0 and eleccode = '" + personnelid + "'");
				if(isEmpty(conn_personnelid)) {
					errorMsg = "表头personnelid（"+personnelid+"）在NC对照表中未获取到数据，请保证此personnelid已在NC对照表中配置！";
					return createMsg(false, "-1", errorMsg);
				}
				// 表体
				List<Map<String, String>> bodyInfo = (List) map.get(BODYTABLE);
				
				Map<String, String> praybillPkMap = new HashMap<String, String>();
				// 根据请求报文构造请求单VO信息
				PraybillVO orgPraybillVO;
				List<PraybillVO> orgPraybillList = new ArrayList<PraybillVO>();
				PraybillHeaderVO[] praybillHVOs;
				PraybillItemVO[] praybillItemVOs;
				PraybillItemVO itemVO;
 				String[] vbillcodeArr = vbillcode.split(",");
				String where = " nvl(dr,0) = 0 ";
				for (int i = 0; i < vbillcodeArr.length; i++) {
					if(!vbillcodeArr[i].equals("")) {
						// 找到请购单信息
						praybillHVOs = (PraybillHeaderVO[]) hypubBo.queryByCondition(PraybillHeaderVO.class, 
								where + "and vbillcode = '" + vbillcodeArr[i] + "' AND BISLATEST = 'Y'");
						if(null == praybillHVOs || praybillHVOs.length < 1) {
							errorMsg = "未找到请购单，请购单号：" + vbillcodeArr[i] +"！";
							return createMsg(false, "-1", errorMsg);
						}
						// 找到请购单详细信息
						praybillItemVOs = (PraybillItemVO[]) hypubBo.queryByCondition(PraybillItemVO.class, 
								where + " and pk_praybill='"+ praybillHVOs[0].getPk_praybill() +"'");
						orgPraybillVO = new PraybillVO();
						orgPraybillVO.setHVO(praybillHVOs[0]);
						for (int j = 0; j < praybillItemVOs.length; j++) {
							itemVO = praybillItemVOs[j];
							praybillPkMap.put(praybillHVOs[0].getPk_praybill()+"_"+itemVO.getCrowno(), itemVO.getPk_praybill_b());
						}
						orgPraybillVO.setBVO(praybillItemVOs);
						orgPraybillList.add(orgPraybillVO);
					}
				}
				// 调用方法将请购单自动转采购合同
				AggCtPuVO[] aggCtSaleVOs = (AggCtPuVO[]) PfUtilTools.runChangeDataAry("20", "Z2", orgPraybillList.toArray(new PraybillVO[0]));
				if( null == aggCtSaleVOs || aggCtSaleVOs.length < 1 ) {
					errorMsg = "上传失败！系统转换单据后采购合同为空，单据号："+vbillcode;
					return createMsg(false, "-1", errorMsg);
				}
  				AggCtPuVO aggCtSaleVO;
				for (int i = 0; i < aggCtSaleVOs.length; i++) {
					aggCtSaleVO = aggCtSaleVOs[i];
					CtPuBVO[] bvos = aggCtSaleVO.getCtPuBVO();
					List<CtPuBVO> newCtSaleBvos = new ArrayList<CtPuBVO>();
					for(int j=0;j<bvos.length;j++){
						CtPuBVO bvo = bvos[j];
						for (int k = 0; k < bodyInfo.size(); k++) {
							Map<String, String> bodyInfoItem = bodyInfo.get(k);
							if(bvo.getPk_praybill_b().equals(bodyInfoItem.get("pk_praybill_b"))) {
								UFDouble nastnum = new UFDouble(bodyInfoItem.get("nastnum"));
								UFDouble ntaxmny = new UFDouble(bodyInfoItem.get("ntaxmny"));
								bvo.setNnum(nastnum);
								bvo.setNastnum(nastnum);
								bvo.setNqtunitnum(nastnum);
								bvo.setNorigtaxmny(ntaxmny);
								bvo.setNqtorigtaxprice(ntaxmny.div(nastnum));
								bvo.setCrowno((j+1) * 10 + "");
								if(bvo.getNtaxrate() == null) {
									// 查找材料对应的税码，税率
									String pk_mattaxes = (String) hypubBo.findColValue("bd_material", "pk_mattaxes", "nvl(dr,0) = 0 and pk_material='"
											+ bvo.getPk_material() + "'");
									String pk_taxcode = (String) hypubBo.findColValue("bd_taxcode", "pk_taxcode", "nvl(dr,0) = 0 and mattaxes='"
											+ pk_mattaxes + "'");
									bvo.setCtaxcodeid(pk_taxcode);
									TaxrateVO[] taxrateVO = (TaxrateVO[]) hypubBo.queryByCondition(TaxrateVO.class, "nvl(dr,0) = 0 and pk_taxcode='"+pk_taxcode+"'");
									if(null != taxrateVO && taxrateVO.length >= 1) {
										UFDouble taxrate = taxrateVO[0].getTaxrate();
										bvo.setNtaxrate(taxrate);
									}
									
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
								bvo.setCsendcountryid("0001Z010000000079UJJ"); // 发货国家/地区 默认：0001Z010000000079UJJ     中国
								
								bvo.setVbdef1(bodyInfoItem.get("vbdef1"));
								bvo.setVbdef2(bodyInfoItem.get("vbdef2"));
								bvo.setVbdef3(bodyInfoItem.get("vbdef3"));
								bvo.setVbdef4(bodyInfoItem.get("vbdef4"));
								bvo.setVbdef5(bodyInfoItem.get("vbdef5"));
								bvo.setVbdef6(bodyInfoItem.get("vbdef6"));
								bvo.setVbdef7(bodyInfoItem.get("vbdef7"));
								bvo.setVbdef8(bodyInfoItem.get("vbdef8"));
								bvo.setVbdef9(bodyInfoItem.get("vbdef9"));
								bvo.setVbdef10(bodyInfoItem.get("vbdef10"));
								bvo.setVbdef11(bodyInfoItem.get("vbdef11"));
								bvo.setVbdef12(bodyInfoItem.get("vbdef12"));
								bvo.setVbdef13(bodyInfoItem.get("vbdef13"));
								bvo.setVbdef14(bodyInfoItem.get("vbdef14"));
								bvo.setVbdef15(bodyInfoItem.get("vbdef15"));
								bvo.setVbdef16(bodyInfoItem.get("vbdef16"));
								bvo.setVbdef17(bodyInfoItem.get("vbdef17"));
								bvo.setVbdef18(bodyInfoItem.get("vbdef18"));
								bvo.setVbdef19(bodyInfoItem.get("vbdef19"));
								bvo.setVbdef20(bodyInfoItem.get("vbdef20"));
								newCtSaleBvos.add(bvo);
							}
						}
					}
					
					if(newCtSaleBvos.size() <=0 ) {
						continue;
					}
			        CtPuBVO[] ctpuBvo = newCtSaleBvos.toArray(new CtPuBVO[0]);
					aggCtSaleVO.setCtPuBVO(ctpuBvo);
					
					// 表头
					CtPuVO ctPuVO = aggCtSaleVO.getParentVO();
					ctPuVO.setCtname(ctname);
					ctPuVO.setVbillcode(ctcode);
					ctPuVO.setCvendorid(pk_supplier);
					ctPuVO.setCreator(cuserid_creator);
					ctPuVO.setBillmaker(cuserid_billmaker);
					ctPuVO.setPk_org(conn_pk_org);
					// 合同签订日期
					ctPuVO.setSubscribedate(new UFDate(subscribedate));
					ctPuVO.setValdate(new UFDate(valdate));
					ctPuVO.setInvallidate(new UFDate(invallidate));
					ctPuVO.setDepid(conn_depid);
					ctPuVO.setDepid_v(conn_depid_v);
					ctPuVO.setPersonnelid(conn_personnelid);
					ctPuVO.setVdef1("1"); // 保质期（年）
					ctPuVO.setCtrantypeid("0001A21000000000PFYT"); // 采购合同通用类型
					ctPuVO.setBbracketOrder(new UFBoolean("N"));
					ctPuVO.setVtrantypecode("Z2-01");
					
					aggCtSaleVO.setParentVO(ctPuVO);
					NCLocator.getInstance().lookup(IPurdailyMaintain.class).save(new AggCtPuVO[] {aggCtSaleVO}, null, null);
					
					ctcodeMsg += ctcode + ";";
				}
				
				
				// 表体
//				List<Map<String, String>> bodyInfo = (List) map.get(BODYTABLE);
//				
//				String[] ids = {bodyInfo.get(0).get("pk_praybill_b")};
//				PraybillVO[] praybillVO = (PraybillVO[]) NCLocator.getInstance().lookup(IQueryPrayBill.class).queryVOByBids(ids);
//				if(null != praybillVO && praybillVO.length == 1){
//					AggCtPuVO aggCtSaleVO = (AggCtPuVO) PfUtilTools.runChangeData("20", "Z2", praybillVO[0], null);
////					AggCtPuVO[] aggCtSaleVOs = (AggCtPuVO[]) PfUtilTools.runChangeDataAry("20", "Z2", praybillVO);
//					CtPuBVO[] bvos = aggCtSaleVO.getCtPuBVO();
//					List<CtPuBVO> newCtSaleBvos = new ArrayList<CtPuBVO>();
//					for(int i=0;i<bvos.length;i++){
//						CtPuBVO bvo = bvos[i];
//						Map<String, String> bodyInfoItem = bodyInfo.get(i);
//						UFDouble nastnum = new UFDouble(bodyInfoItem.get("nastnum"));
//						UFDouble ntaxmny = new UFDouble(bodyInfoItem.get("ntaxmny"));
//						bvo.setNnum(nastnum);
//						bvo.setNastnum(nastnum);
//						bvo.setNqtunitnum(nastnum);
//						bvo.setNorigtaxmny(ntaxmny);
//						bvo.setNqtorigtaxprice(ntaxmny.div(nastnum));
//						bvo.setCrowno((i+1) * 10 + "");
//						CtPriceCalculator cpcalt = new CtPriceCalculator();
//						cpcalt.relationCalculatePrice(bvo);
//						bvo.setNorigmny(bvo.getNorigprice().multiply(nastnum));
//						newCtSaleBvos.add(bvo);
//					}
//			        CtPuBVO[] ctpuBvo = newCtSaleBvos.toArray(new CtPuBVO[0]);
//					aggCtSaleVO.setCtPuBVO(ctpuBvo);
//					
//					// 表头
//					CtPuVO ctPuVO = aggCtSaleVO.getParentVO();
//					ctPuVO.setCtname(ctname);
//					ctPuVO.setVbillcode(ctcode);
//					ctPuVO.setCvendorid(cvendorid);
//					ctPuVO.setCreator(conn_creator);
//					ctPuVO.setBillmaker(conn_billmaker);
//					ctPuVO.setPk_org(conn_pk_org);
//					// 合同签订日期
//					ctPuVO.setSubscribedate(new UFDate(subscribedate));
//					ctPuVO.setValdate(new UFDate(valdate));
//					ctPuVO.setInvallidate(new UFDate(invallidate));
//					ctPuVO.setDepid(conn_depid);
//					ctPuVO.setPersonnelid(conn_personnelid);
//					ctPuVO.setVdef1("1"); // 保质期（年）
//					ctPuVO.setCtrantypeid("0001A21000000000PFYT"); // 采购合同通用类型
//					ctPuVO.setBbracketOrder(new UFBoolean("N"));
//					
//					aggCtSaleVO.setParentVO(ctPuVO);
////					calc.calcMny(aggsAggCtPuVOs, MetaNameConst.NORIGTAXMNY);
////					calc.calcNum(aggsAggCtPuVOs, "nnum");
//					NCLocator.getInstance().lookup(IPurdailyMaintain.class).save(new AggCtPuVO[] {aggCtSaleVO}, null, null);
//					errorMsg = "上传成功！合同编号："+ctcode;
//					return createMsg(true, "0", errorMsg);
//				}else{
//					throw new BusinessException("未查询到请购单数据");
//				}
			}
			errorMsg = "上传成功！合同编号：" + ctcodeMsg;
			return createMsg(true, "0", errorMsg);
		} catch (Exception e) {
			System.out.println(e);
			errorMsg = "上传失败！系统出现内部错误！错误信息："+e.getMessage();
			return createMsg(false, "-1", errorMsg);
		}
	}
	
	/**
	 * 生成返回信息统一方法
	 * @param isSuccess
	 * @param code
	 * @param msg
	 * @return
	 */
	private String createMsg(boolean isSuccess, String code, String msg) {
		Map<String, Object> msgMap = new HashMap<String, Object>();
		msgMap.put("success", isSuccess);
		msgMap.put("code", code);
		msgMap.put("msg", msg);
		Debug.error("电采上传采购合同返回结果：" + isSuccess + "; 返回信息：" + msg);
		return JSON.toJSONString(msgMap);
	}
	
	private static boolean isEmpty(String str) { 
		return (str == null || str.length() == 0 || "null".equals(str)); 
	}
	
}

