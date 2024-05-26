package nc.plugin.erm.bxarap.pushI8;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.bs.trade.business.HYPubBO;
import nc.itf.arap.gathering.IArapGatheringBillQueryService;
import nc.itf.arap.pay.IArapPayBillQueryService;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.vo.arap.gathering.AggGatheringBillVO;
import nc.vo.arap.gathering.GatheringBillVO;
import nc.vo.arap.pay.AggPayBillVO;
import nc.vo.arap.pay.PayBillVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;
import nc.ws.intf.HttpClient;
import nc.ws.intf.I8LogVO;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

// 付款单、收款单、报销单定时任务查询调用I8系统接口更新状态
@SuppressWarnings("unused")
public class PushI8Plugin implements IBackgroundWorkPlugin {
	public PushI8Plugin() {
	}

	public PreAlertObject executeTask(BgWorkingContext bgwc)
			throws BusinessException {
		// Step1 获取需推送数据
		AggPayBillVO[] fkvos = getFKVosBywhere();// 付款单
		// AggGatheringBillVO[] skvos = getSKVosBywhere();// 收款单
		List<JKBXVO> bxvos = getBXVosBywhere();// 报销单
		// Step2 封装接口参数,调用接口
		// 付款
		if (fkvos.length > 0) {
			for (AggPayBillVO fkvo : fkvos) {
				String sendata = getFKParams(fkvo);
				// Step3 调用接口
				Map<String, String> headers = new HashMap<>();
				String rtndt = HttpClient.httpPostRaw(getUrl(), sendata,
						headers, "utf-8");
				// Step3 保存接口日志
				PayBillVO headvo = (PayBillVO) fkvo.getParentVO();// 表头VO
				String mes = SaveI8Log(headvo.getBillno(),
						headvo.getPrimaryKey(), headvo.getPk_tradetype(),
						rtndt, headvo.getDef65(), sendata);
				if (StringUtils.isNotEmpty(mes)) {
					throw new BusinessException("更新付款状态["
							+ fkvo.getParentVO().getPrimaryKey() + "]保存日志出错："
							+ mes);
				}
				// Step5 修改付款单状态[def46]为已推送，I8-Y
				JSONObject json = JSONArray.parseObject(rtndt); // 将返回数据转成JSONObject对象
				String status = json.getString("status"); // 成功success，失败error
				if ("success".equals(status)) {
					String sqlmes = updateFKBillState(fkvo.getPrimaryKey());
					if (StringUtils.isNotEmpty(sqlmes)) {
						throw new BusinessException(sqlmes);
					}
				}
			}
		}
		// 收款
//		if (skvos.length > 0) {
//			for (AggGatheringBillVO skvo : skvos) {
//				String sendata = getSKParams(skvo);
//				// Step3 调用接口
//				Map<String, String> headers = new HashMap<>();
//				String rtndt = HttpClient.httpPostRaw(getUrl(), sendata,
//						headers, "utf-8");
//				// Step3 保存接口日志
//				GatheringBillVO headvo = (GatheringBillVO) skvo.getParentVO();// 表头VO
//				String mes = SaveI8Log(headvo.getBillno(),
//						headvo.getPrimaryKey(), headvo.getPk_tradetype(),
//						rtndt, headvo.getDef65(), sendata);
//				if (StringUtils.isNotEmpty(mes)) {
//					throw new BusinessException("更新收款状态["
//							+ headvo.getPrimaryKey() + "]保存日志出错：" + mes);
//				}
//				// Step5 修改付款单状态[def46]为已推送，I8-Y
//				JSONObject json = JSONArray.parseObject(rtndt); // 将返回数据转成JSONObject对象
//				String status = json.getString("status"); // 成功success，失败error
//				if ("success".equals(status)) {
//					String sqlmes = updateSKBillState(skvo.getPrimaryKey());
//					if (StringUtils.isNotEmpty(sqlmes)) {
//						throw new BusinessException(sqlmes);
//					}
//				}
//			}
//		}
//		// 报销
		if (bxvos.size() > 0) {
			for (JKBXVO bxvo : bxvos) {
				String sendata = getBXParams(bxvo);
				// Step3 调用接口
				Map<String, String> headers = new HashMap<>();
				String rtndt = HttpClient.httpPostRaw(getBXUrl(), sendata,
						headers, "utf-8");
				// Step3 保存接口日志
				JKBXHeaderVO headvo = (JKBXHeaderVO) bxvo.getParentVO();// 表头VO
				String mes = SaveI8Log(headvo.getDjbh(),
						headvo.getPrimaryKey(), headvo.getDjlxbm(), rtndt,
						headvo.getZyx29(), sendata);
				if (StringUtils.isNotEmpty(mes)) {
					throw new BusinessException("更新报销状态["
							+ headvo.getPrimaryKey() + "]保存日志出错：" + mes);
				}
				// Step5 修改付款单状态[zyx29]为已推送，I8-Y
				JSONObject json = JSONArray.parseObject(rtndt); // 将返回数据转成JSONObject对象
				String status = json.getString("status"); // 成功success，失败error
				if ("success".equals(status)) {
					String sqlmes = updateBXBillState(headvo.getPrimaryKey());
					if (StringUtils.isNotEmpty(sqlmes)) {
						throw new BusinessException(sqlmes);
					}
				}
			}
		}
		return null;
	}

	// 修改付款单状态[def46]为已推送，I8-Y
	private String updateFKBillState(String pk_primarykey)
			throws BusinessException {
		String updatesql = "update ap_paybill set def46 = 'I8-Y' where pk_paybill = '"
				+ pk_primarykey + "'";
		try {
			new BaseDAO().executeUpdate(updatesql);
		} catch (BusinessException e) {
			// TODO: handle exception
			return "更新付款状态[" + updatesql + "]出错：" + e.getMessage();
		}
		return null;
	}

	// 修改收款单状态[def46]为已推送，I8-Y
	private String updateSKBillState(String pk_primarykey)
			throws BusinessException {
		String updatesql = "update ar_gatherbill set def46 = 'I8-Y' where pk_gatherbill = '"
				+ pk_primarykey + "'";
		try {
			new BaseDAO().executeUpdate(updatesql);
		} catch (BusinessException e) {
			// TODO: handle exception
			return "更新收款状态[" + updatesql + "]出错：" + e.getMessage();
		}
		return null;
	}

	// 修改报销单状态[zyx29]为已推送，I8-Y
	private String updateBXBillState(String pk_primarykey)
			throws BusinessException {
		String updatesql = "update er_bxzb set zyx89 = 'I8-Y' where pk_jkbx = '"
				+ pk_primarykey + "'";
		try {
			new BaseDAO().executeUpdate(updatesql);
		} catch (BusinessException e) {
			// TODO: handle exception
			return "更新收款状态[" + updatesql + "]出错：" + e.getMessage();
		}
		return null;
	}

	// 保存调用I8系统接口记录
	private String SaveI8Log(String bllno, String primarykey, String tradetype,
			String rdtdata, String i8djh, String sendata)
			throws BusinessException {
		// TODO Auto-generated method stub
		String mes = "";
		I8LogVO vo = new I8LogVO();
		vo.setBill_code(bllno);
		vo.setDef1(sendata);// 发送数据
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		vo.setCreationtime(df.format(new Date()));// 创建时间
		vo.setPk_bill(primarykey);// 单据主键
		vo.setTransi_type(tradetype); // 交易类型
		vo.setRdtdata(rdtdata);// I8返回数据
		vo.setI8djh(i8djh);// I8单据号
		vo.setDef2("更新付款状态");// 类型
		try {
			new HYPubBO().insert(vo);
		} catch (BusinessException e) {
			// TODO: handle exception
			return e.getMessage();
		}
		return mes;
	}

	// 封装接口参数
	private String getFKParams(AggPayBillVO fkvo) {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		PayBillVO hvo = (PayBillVO) fkvo.getParentVO(); // 表头VO
		String bill_no = hvo.getDef65();
		String pay_date = hvo.getSigndate().toStdString();
		json.put("bill_no", bill_no);// I8单据号
		json.put("pay_status", "1");// "付款状态  默认值1"
		json.put("pay_date", pay_date);// "付款日期"
		return json.toString();
	}

	// 封装收款接口参数
	private String getSKParams(AggGatheringBillVO skvo) {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		GatheringBillVO hvo = (GatheringBillVO) skvo.getParentVO(); // 表头VO
		String bill_no = hvo.getDef65();
		String pay_date = hvo.getSigndate().toStdString();
		json.put("bill_no", bill_no);// I8单据号
		json.put("pay_status", "1");// "付款状态  默认值1"
		json.put("pay_date", pay_date);// "付款日期"
		return json.toString();
	}

	// 封装报销接口参数
	private String getBXParams(JKBXVO bxvo) {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		JKBXHeaderVO hvo = (JKBXHeaderVO) bxvo.getParentVO(); // 表头VO
		String bill_no = hvo.getZyx29();
		String pay_date = hvo.getJsrq().toStdString();
		json.put("bill_no", bill_no);// I8单据号
		json.put("pay_status", "1");// "付款状态  默认值1"
		json.put("pay_date", pay_date);// "付款日期"
		return json.toString();
	}

	// 查询需推送I8系统的付款单据
	private AggPayBillVO[] getFKVosBywhere() throws BusinessException {
		IArapPayBillQueryService cs = (IArapPayBillQueryService) NCLocator
				.getInstance().lookup(IArapPayBillQueryService.class);
		AggPayBillVO[] fkvos = cs
				.queryBillsByWhereSQL(" nvl(dr,0) = 0 and def65 <> '~' and def65 is not null and def46 <> 'I8-Y' and settleflag = 1");
		return fkvos;
	}

	// 查询需推送I8系统的收款单据
	private AggGatheringBillVO[] getSKVosBywhere() throws BusinessException {
		IArapGatheringBillQueryService cs = (IArapGatheringBillQueryService) NCLocator
				.getInstance().lookup(IArapGatheringBillQueryService.class);
		AggGatheringBillVO[] skvos = cs
				.queryVOsByWhere(" nvl(dr,0) = 0 and def65 <> '~' and def65 is not null and def46 <> 'I8-Y' and settleflag = 1");
		return skvos;
	}

	// 查询需推送I8系统的报销单据
	private List<JKBXVO> getBXVosBywhere() throws BusinessException {
		IBXBillPrivate cs = (IBXBillPrivate) NCLocator.getInstance().lookup(
				IBXBillPrivate.class);
		List<JKBXVO> bxvos = cs
				.queryVOsByWhereSql(
						"where djlxbm = '264X-Cxx-gcgxbx-xmbx' and nvl(dr,0) = 0 and zyx29 <> '~' and zyx29 is not null and zyx89 <> 'I8-Y' and payflag = 3",
						"bx");
		return bxvos;
	}

	// 获取OA接口
	private String getUrl() throws BusinessException {
		String url = (String) new HYPubBO().findColValue("sys_config",
				"config_value", "config_key='I8FKZTURL'");
		if (url == null) {
			throw new BusinessException("I8付款状态回填接口地址为空,请配置SYS_CONFIG数据表[72]！");
		}
		return url;
	}
	
	// 获取OA接口
	private String getBXUrl() throws BusinessException {
		String url = (String) new HYPubBO().findColValue("sys_config",
				"config_value", "config_key='I8BXZTURL'");
		if (url == null) {
			throw new BusinessException("I8报销状态回填接口地址为空,请配置SYS_CONFIG数据表[73]！");
		}
		return url;
	}
}
