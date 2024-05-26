package nc.impl.arap.ysyf;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.trade.business.HYPubBO;
import nc.itf.arap.ysyf.IArapServiceForI8;
import nc.itf.uap.pf.IplatFormEntry;
import nc.uif.pub.exception.UifException;
import nc.vo.arap.gathering.AggGatheringBillVO;
import nc.vo.arap.gathering.GatheringBillItemVO;
import nc.vo.arap.gathering.GatheringBillVO;
import nc.vo.arap.pay.AggPayBillVO;
import nc.vo.arap.pay.PayBillItemVO;
import nc.vo.arap.pay.PayBillVO;
import nc.vo.arap.payable.AggPayableBillVO;
import nc.vo.arap.payable.PayableBillItemVO;
import nc.vo.arap.payable.PayableBillVO;
import nc.vo.arap.receivable.AggReceivableBillVO;
import nc.vo.arap.receivable.ReceivableBillItemVO;
import nc.vo.arap.receivable.ReceivableBillVO;
import nc.vo.pub.BusinessException;
import nc.vo.sm.UserVO;
import nc.ws.intf.HttpClient;
import nc.ws.intf.I8LogVO;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
// import nc.bs.framework.common.InvocationInfoProxy;

// 驳回I8系统接口 FOR ARAP
public class ArapServiceForI8Impl implements IArapServiceForI8 {

	@Override
	public String rollAggBillVO(String pk, String billtype, String reason)
			throws BusinessException {
		// TODO Auto-generated method stub
		if (StringUtils.isEmpty(pk) || StringUtils.isEmpty(billtype)) {
			return "请检查前端数据传输，pk[" + pk + "] billtype["
					+ billtype + "] ";
		}
		IplatFormEntry ipf = NCLocator.getInstance().lookup(
				IplatFormEntry.class);
		HYPubBO hyPubBO = new HYPubBO();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String ts = df.format(new Date());// 操作时间
		// 应收单
		if ("F0".equals(billtype)) {
			// 将数据库中数据改为自由态
			String sql = "UPDATE AR_RECBILL SET APPROVESTATUS = '-1',BILLSTATUS = '-1',TS = '"+ts+"' WHERE "
					+ "PK_RECBILL = '" + pk	+ "'";
			new BaseDAO().executeUpdate(sql);
			String[] userObj = { AggReceivableBillVO.class.getName(),
					ReceivableBillVO.class.getName(),
					ReceivableBillItemVO.class.getName() };
			AggReceivableBillVO aggVO = (AggReceivableBillVO) hyPubBO
					.queryBillVOByPrimaryKey(userObj, pk);
			if (aggVO != null) {
				ReceivableBillVO headvo = (ReceivableBillVO) aggVO
						.getParentVO();
				if (headvo.getDef65() == null) {
					return "应收单I8单据号为空！";
				}
				Logger.error("headvo：" + headvo.getBillno() + pk
						+ headvo.getPk_tradetype() + headvo.getCreator()
						+ reason);
				//terminate(headvo.getPrimaryKey(), headvo.getPk_billtype(),headvo.getBillno());
				try {
					Map<String,String> mes = sendRollBack(headvo.getDef65(), headvo.getPk_tradetype(),reason ,headvo.getBillno(),headvo.getDef89());
					if((mes.get("mes")!= null && !"".equals(mes.get("mes"))) || (mes.get("i8nomes")!= null && !"".equals(mes.get("i8nomes")))){
						return mes.get("mes");
					}
					String logmes = SaveI8Log(headvo.getBillno(),pk,headvo.getPk_tradetype(),headvo.getCreator(),reason, mes.get("i8mes"),headvo.getDef65(),mes.get("sendata"),headvo.getPk_org());
					if(!"".equals(logmes)){
						return logmes;
					}
					// NC端调用删除动作删除单据
					ipf.processAction("DELETE", "F0", null, aggVO, null, null);
					delssc(headvo.getBillno());// 删除共享单据
					// ipf.processBatch("DELETE", "F0", null, aggVOs, null, null);
				} catch (BusinessException e) {
					e.printStackTrace();
					return e.getMessage();
					// TODO: handle exception
				}
			} else {
				return "应收单获取VO错误！";
			}

		} else if ("F1".equals(billtype)) {// 应付单
			// 将数据库中数据改为自由态
			String sql = "UPDATE AP_PAYABLEBILL SET APPROVESTATUS = '-1',BILLSTATUS = '-1',TS = '"+ts+"' WHERE "
					+ "PK_PAYABLEBILL = '" + pk	+ "'";
			new BaseDAO().executeUpdate(sql);
			String[] userObj = { AggPayableBillVO.class.getName(),
					PayableBillVO.class.getName(),
					PayableBillItemVO.class.getName() };
			AggPayableBillVO aggVO = (AggPayableBillVO) hyPubBO
					.queryBillVOByPrimaryKey(userObj, pk);
			if (aggVO != null) {
				PayableBillVO headvo = (PayableBillVO) aggVO.getParentVO();
				if (headvo.getDef65() == null) {
					return "应付单I8单据号为空！";
					// throw new BusinessException("应付单I8单据号为空！");
				}
				Logger.error("headvo：" + headvo.getBillno() + pk
						+ headvo.getPk_tradetype() + headvo.getCreator()
						+ reason);
				// terminate(headvo.getPrimaryKey(), headvo.getPk_billtype(),headvo.getBillno());
				try {
					Map<String,String> mes = sendRollBack(headvo.getDef65(), headvo.getPk_tradetype(),reason ,headvo.getBillno(),headvo.getDef89());
					if((mes.get("mes")!= null && !"".equals(mes.get("mes"))) || (mes.get("i8nomes")!= null && !"".equals(mes.get("i8nomes")))){
						return mes.get("mes");
					}
					String logmes = SaveI8Log(headvo.getBillno(),pk,headvo.getPk_tradetype(),headvo.getCreator(),reason, mes.get("i8mes"),headvo.getDef65(),mes.get("sendata"),headvo.getPk_org());
					if(!"".equals(logmes)){
						return logmes;
					}
					// NC端调用删除动作删除单据
					ipf.processAction("DELETE", "F1", null, aggVO, null, null);
					delssc(headvo.getBillno());// 删除共享单据
					// AggPayableBillVO[] aggVOs = { aggVO };
					// ipf.processBatch("DELETE", "F1", null, aggVOs, null, null);
				}catch(BusinessException e) {
					System.out.println("删除单据报错："+e.getMessage());
					return "删除单据报错："+e.getMessage();
					// throw new BusinessException("删除单据报错："+e.getMessage());
				}
			} else {
				return "应付单获取VO错误！";
				// throw new BusinessException("应付单获取VO错误！");
			}
		} else if ("F2".equals(billtype)) {// 收款单
			// 将数据库中数据改为自由态
			String sql = "UPDATE AR_GATHERBILL SET APPROVESTATUS = '-1',BILLSTATUS = '-1',TS = '"+ts+"' WHERE "
					+ "PK_GATHERBILL = '" + pk	+ "'";
			new BaseDAO().executeUpdate(sql);
			String[] userObj = { AggGatheringBillVO.class.getName(),
					GatheringBillVO.class.getName(),
					GatheringBillItemVO.class.getName() };
			AggGatheringBillVO aggVO = (AggGatheringBillVO) hyPubBO
					.queryBillVOByPrimaryKey(userObj, pk);
			if (aggVO != null) {
				GatheringBillVO headvo = (GatheringBillVO) aggVO.getParentVO();
				if (headvo.getDef65() == null) {
					return "收款单I8单据号为空！";
				}
				Logger.error("headvo：" + headvo.getBillno() + pk
						+ headvo.getPk_tradetype() + headvo.getCreator()
						+ reason);
				//terminate(headvo.getPrimaryKey(), headvo.getPk_billtype(),headvo.getBillno());
				try {
					Map<String,String> mes = sendRollBack(headvo.getDef65(), headvo.getPk_tradetype(),reason ,headvo.getBillno(),headvo.getDef89());
					if((mes.get("mes")!= null && !"".equals(mes.get("mes"))) || (mes.get("i8nomes")!= null && !"".equals(mes.get("i8nomes")))){
						return mes.get("mes");
					}
					String logmes = SaveI8Log(headvo.getBillno(),pk,headvo.getPk_tradetype(),headvo.getCreator(),reason, mes.get("i8mes"),headvo.getDef65(),mes.get("sendata"),headvo.getPk_org());
					if(!"".equals(logmes)){
						return logmes;
					}
					// NC端调用删除动作删除单据
					ipf.processAction("DELETE", "F2", null, aggVO, null, null);
					delssc(headvo.getBillno());// 删除共享单据
					// AggGatheringBillVO[] aggVOs = { aggVO };
					// ipf.processBatch("DELETE", "F2", null, aggVOs, null, null);
				}catch(BusinessException e) {
					System.out.println("删除单据报错："+e.getMessage());
					return "删除单据报错："+e.getMessage();
					// throw new BusinessException("删除单据报错："+e.getMessage());
				}
			} else {
				return "收款单获取VO错误！";
				// throw new BusinessException("收款单获取VO错误！");
			}
		} else if ("F3".equals(billtype)) {// 付款单
			// 将数据库中数据改为自由态
			String sql = "UPDATE AP_PAYBILL SET APPROVESTATUS = '-1',BILLSTATUS = '-1',TS = '"+ts+"' WHERE "
					+ "PK_PAYBILL = '" + pk	+ "'";
			new BaseDAO().executeUpdate(sql);
			String[] userObj = { AggPayBillVO.class.getName(),
					PayBillVO.class.getName(), PayBillItemVO.class.getName() };
			AggPayBillVO aggVO = (AggPayBillVO) hyPubBO
					.queryBillVOByPrimaryKey(userObj, pk);
			if (aggVO != null) {
				PayBillVO headvo = (PayBillVO) aggVO.getParentVO();
				if (headvo.getDef65() == null) {
					return "应付单I8单据号为空！";
				}
				Logger.error("headvo：" + headvo.getBillno() + pk
						+ headvo.getPk_tradetype() + headvo.getCreator()
						+ reason);
				// terminate(headvo.getPrimaryKey(), headvo.getPk_billtype(),headvo.getBillno());
				try {
					Map<String,String> mes = sendRollBack(headvo.getDef65(), headvo.getPk_tradetype(),reason ,headvo.getBillno(),headvo.getDef89());
					if((mes.get("mes")!= null && !"".equals(mes.get("mes"))) || (mes.get("i8nomes")!= null && !"".equals(mes.get("i8nomes")))){
						return mes.get("mes");
					}
					String logmes = SaveI8Log(headvo.getBillno(),pk,headvo.getPk_tradetype(),headvo.getCreator(),reason, mes.get("i8mes"),headvo.getDef65(),mes.get("sendata"),headvo.getPk_org());
					if(!"".equals(logmes)){
						return logmes;
					}
					ipf.processAction("DELETE", "F3", null, aggVO, null, null);
					delssc(headvo.getBillno());// 删除共享单据
					// AggPayBillVO[] aggVOs = { aggVO };
					// ipf.processBatch("DELETE", "F3", null, aggVOs, null, null);
				}catch(BusinessException e) {
					System.out.println("删除单据报错："+e.getMessage());
					return "删除单据报错："+e.getMessage();
				}
			} else {
				return "付款单获取VO错误！";
			}
		}
		return "";
	}

	private String SaveI8Log(String bllno, String pk, String tradetype,
			String creator, String reason,String rdtdata,String i8djh, String sendata,String pk_org) throws BusinessException {
		// TODO Auto-generated method stub
		String mes = "";
		I8LogVO vo = new I8LogVO();
		vo.setBill_code(bllno);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		vo.setCreationtime(df.format(new Date()));// 创建时间
		vo.setPk_bill(pk);// 单据主键
		vo.setTransi_type(tradetype); // 交易类型
		vo.setUserid(creator);// 创建人
		vo.setReason(reason);// 驳回原因
		vo.setRdtdata(rdtdata);// I8返回数据
		vo.setI8djh(i8djh);// I8单据号
		vo.setDef1(sendata);// 发送I8数据
		vo.setDef2("驳回");// 类型
		vo.setPk_org(pk_org);// 组织主键
		vo.setPk_group("0001A1100000000001QS");// 集团主键
		try {
			new HYPubBO().insert(vo);
		} catch (BusinessException e) {
			// TODO: handle exception
			return e.getMessage();
		}
		return mes;
	}

	// billid==单据主键 billtype==单据大类 billno==单据号
	/*public void terminate(String billid, String billtype, String billno)
			throws BusinessException {
		// InvocationInfoProxy.getInstance().setUserId("1001A11000000026YI1E");// 设置默认操作员主键
		int iWorkflowtype = 4;
		IWorkflowAdmin admin = NCLocator.getInstance().lookup(
				IWorkflowAdmin.class);
		try {
			Logger.error("流程：" + billid+billtype+billno+iWorkflowtype);
			admin.terminateWorkflow(billid, billtype, billno, iWorkflowtype);
		} catch (BusinessException e) {
			Logger.error("中止流程出错：" + e.getMessage());
			throw new BusinessException("中止流程出错：" + e.getMessage());
		}
	}*/
	// 调用I8驳回接口
	private Map<String,String> sendRollBack(String i8no, String djlxbm,String reason,String billno,String def89) throws UifException,
			BusinessException {
		String userID = InvocationInfoProxy.getInstance().getUserId();//获取当前系统登录用户
		UserVO userVO = (UserVO) new HYPubBO().queryByPrimaryKey(UserVO.class,
				userID);
		Map<String,String> map = new HashMap<>();
		String url = (String) new HYPubBO().findColValue("sys_config",
				"config_value", "config_key='i8-url'");
		if(url != null){
			String bill_tag = (String) new HYPubBO().findColValue("bd_defdoc",
					"code", "name='" + djlxbm + "' and shortname <> '~'");
			if(def89 != null){
				bill_tag = def89;
			}
			if(bill_tag == null || "".equals(bill_tag)){
				map.put("mes", "根据["+ djlxbm +"]未获取到I8业务标识！");
				return map;
			}
			JSONObject json = new JSONObject();
			json.put("bill_no", i8no);
			// json.put("user_ncbillcode", billno);// NC单据号
			json.put("user_ncrejectmsg", userVO+reason);// 驳回原因
			json.put("bill_status", "3");// 状态 目前传 3 驳回
			json.put("bill_tag", bill_tag);
			Map<String, String> headers = new HashMap<>();
			// headers.put("Zoomkey-Auth-Token", zoomkeyAuthToken);
			Logger.error("发送I8数据："+json.toString());
			map.put("sendata", json.toString());
			String mes = HttpClient.httpPostRaw(url, json.toString(), headers, "utf-8");
			if(mes == null || "".equals(mes)){
				map.put("mes", "调用I8系统接口返回数据为空，请检查！");
				return map;
			}
			map.put("i8mes", mes);
			Logger.error("I8返回数据："+mes);
			JSONObject res = JSONObject.fromObject(mes);
			String status = res.getString("status");
			if (!"S".equals(status)) {
				map.put("mes", res.getString("message"));
				// return res.getString("message");
				// throw new BusinessException("调用I8系统接口出错:" + info);
			}
		}else{
			map.put("mes",  "URL地址为空,请配置SYS_CONFIG数据表！");
			return map;
			// throw new BusinessException("URL地址为空,请配置SYS_CONFIG数据表！");
		}
		return map;
	}
	// 删除共享单据
	public String delssc(String billno){
		BaseDAO dao = new BaseDAO();
		String mes = null;// 报错信息
		String sql1 = "delete from ssc_activetask where pk_ssctask in( select pk_ssctask from ssc_ssctask where billcode='"+billno+"')";
		String sql2 = "delete from ssc_persontask where pk_ssctask in( select pk_ssctask from ssc_ssctask where billcode='"+billno+"')";
		String sql3 = "delete from ssc_posttask where pk_ssctask in( select pk_ssctask from ssc_ssctask where billcode='"+billno+"')";
		String sql4 = "delete from ssc_ssctasklog where pk_ssctask in( select pk_ssctask from ssc_ssctask where billcode='"+billno+"')";
		String sql5 = "delete from ssc_ssctask where billcode='"+billno+"'";
		try {
			dao.executeUpdate(sql1);
			dao.executeUpdate(sql2);
			dao.executeUpdate(sql3);
			dao.executeUpdate(sql4);
			dao.executeUpdate(sql5);
		} catch (BusinessException e) {
			// TODO: handle exception
			e.printStackTrace();
			mes = e.getMessage();
		}
		return mes;
	}
}
