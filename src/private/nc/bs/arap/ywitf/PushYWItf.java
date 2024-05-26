package nc.bs.arap.ywitf;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.bs.trade.business.HYPubBO;
import nc.itf.arap.pay.IArapPayBillQueryService;
import nc.vo.arap.pay.AggPayBillVO;
import nc.vo.arap.pay.PayBillVO;
import nc.vo.pub.BusinessException;
import nc.ws.intf.HttpClient;
import nc.ws.intf.OALogVO;

// 热电付款单定时任务查询调用域外系统接口
public class PushYWItf implements IBackgroundWorkPlugin {
	public PushYWItf() {
	}

	public PreAlertObject executeTask(BgWorkingContext bgwc)
			throws BusinessException {
		// Step1 获取需推送付款单
		AggPayBillVO[] fkvos = getFKVosBywhere();
		// Step2 封装接口参数
		for (AggPayBillVO fkvo : fkvos) {
			String pushdata = getParams(fkvo);
			// Step3 调用接口
			Map<String, String> headers = new HashMap<>();
			headers.put("apikey", "qzmBbMfxBX7q3httQkC4B4PE4NWTNScf");
			String rtndt = HttpClient.httpPostRaw(getUrl(), pushdata, headers,
					"utf-8");
			// Step4 保存接口日志
			String mes = saveYWLog(rtndt, pushdata, fkvo);
			if (StringUtils.isNotEmpty(mes)) {
				bgwc.setLogStr("付款单主键[" + fkvo.getParentVO().getPrimaryKey()
						+ "]保存日志出错：" + mes);
			}
			// Step5 修改付款单状态[def65]为已推送，RD-Y
			String sqlmes = updateBillState(fkvo,rtndt);
			if(StringUtils.isNotEmpty(sqlmes)){
				throw new BusinessException(sqlmes);
			}
		}

		return null;
	}

	// 修改付款单状态[def65]为已推送，RD-Y
	private String updateBillState(AggPayBillVO fkvo, String rtndt) throws BusinessException {
		// TODO Auto-generated method stub
		JSONObject json = JSONArray.parseObject(rtndt); // 将返回数据转成JSONObject对象
		PayBillVO hvo = (PayBillVO) fkvo.getParentVO(); // 表头VO
		boolean sus = json.getBoolean("ok"); // 成功true，失败false
		String msg = json.getString("msg"); // 提示信息
		String updatesql = "update ap_paybill set def46 = 'RD-Y' where pk_paybill = '"+hvo.getPrimaryKey()+"'";
		if(!sus){
			updatesql = "update ap_paybill set def46 = '"+msg+"' where pk_paybill = '"+hvo.getPrimaryKey()+"'";
		}
		try {
			new BaseDAO().executeUpdate(updatesql);
		} catch (BusinessException e) {
			// TODO: handle exception
			return "更新付款状态[" + updatesql + "]出错："
					+ e.getMessage();
		}
		return null;
	}

	// 保存接口日志
	private String saveYWLog(String rtndt, String pushdata, AggPayBillVO fkvo) {
		// TODO Auto-generated method stub
		String mes = null;// 保存VO成功标识
		OALogVO oavo = new OALogVO();
		oavo.setPk_bill(fkvo.getPrimaryKey());// 付款单主键
		oavo.setSend_data(pushdata);// 发送参数
		oavo.setTransi_type("YW");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		oavo.setCreationtime(df.format(new Date()));// 创建时间
		oavo.setDef1(rtndt);// 返回参数
		oavo.setDr(0);
		try {
			new HYPubBO().insert(oavo);
		} catch (BusinessException e) {
			// TODO: handle exception
			return e.getMessage();
		}
		return mes;
	}

	// 封装接口参数
	private String getParams(AggPayBillVO fkvo) {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		PayBillVO hvo = (PayBillVO) fkvo.getParentVO(); // 表头VO
		int requestid = Integer.parseInt(hvo.getDef45());// 域外请求id
		Double fkmon = hvo.getLocal_money().toDouble();// 付款金额
		json.put("requestid", requestid);// 域外 请求id
		json.put("applysum", fkmon);// 域外 付款金额
		return json.toString();
	}

	// 查询需推送域外系统的付款单据
	private AggPayBillVO[] getFKVosBywhere() throws BusinessException {
		IArapPayBillQueryService cs = (IArapPayBillQueryService) NCLocator
				.getInstance().lookup(IArapPayBillQueryService.class);
		AggPayBillVO[] fkvos = cs
				.queryBillsByWhereSQL(" def45 <> '~' and def46 <> 'RD-Y' and dr = 0 and settleflag = 1");
		return fkvos;
	}

	// 获取OA接口
	private String getUrl() throws BusinessException {
		String url = (String) new HYPubBO().findColValue("sys_config",
				"config_value", "config_key='YW-URL'");
		if (url == null) {
			throw new BusinessException("域外接口URL地址为空,请配置SYS_CONFIG数据表！");
		}
		return url;
	}
}
