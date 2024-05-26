package nc.impl.arap.ysyf;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
import nc.itf.arap.pub.IArapBillService;
import nc.itf.arap.ysyf.IArapServiceForSC;
import nc.itf.uap.pf.IplatFormEntry;
import nc.vo.arap.basebill.BaseAggVO;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.ws.intf.HttpClient;
import nc.ws.intf.I8LogVO;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
// import nc.impl.fct.ap.action.ApDeleteAction;

public class ArapServiceForSCImpl implements IArapServiceForSC {
	private HYPubBO hyPubBO;
	private String scUrl;

	public HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	public void setHyPubBO(HYPubBO hyPubBO) {
		this.hyPubBO = hyPubBO;
	} 

	// 调用生产系统驳回接口，NC删除单据
	@Override
	public String rollAggBillVO(String billid, String reason) throws BusinessException {
		// TODO Auto-generated method stub
		IArapBillService cs = (IArapBillService) NCLocator.getInstance()
				.lookup(IArapBillService.class);
		BaseAggVO[] arapvos = cs.queryArapBillByPKs(
				new String[] { billid }, "F1");
		
		if(arapvos == null){
			return "主键："+billid+"对应单据已删除！";
		}
		BaseBillVO hvo = arapvos[0].getHeadVO();
		if(hvo.getApprovestatus() == 1){
			return "单据已审批完成，不允许驳回！";
		}
		// 获取生产系统Url
		JSONObject urlJson = getSCUrl();
		if(urlJson != null && "Y".equals(urlJson.getString("success"))){
			scUrl = urlJson.getString("url");
		}else{
			return urlJson.getString("errinfo");
		}
		// 获取Token
		String scToken = getToken(scUrl);
		if(scToken != null){
			JSONObject tokenJson = JSONObject.fromObject(scToken);
			if("200".equals(tokenJson.getString("code"))){
				// 生产系统Token
				String token = tokenJson.getString("data");
				if(StringUtils.isNotEmpty(token)){
					token = JSONObject.fromObject(token).getString("token");
				}
				Map<String, String> headers = new HashMap<>();
				headers.put("Authorization", token);
				// 拼装发送参数
				JSONObject json = new JSONObject();
				json.put("commandTime", hvo.getDef65()); // 报表时间--对方单据号
				json.put("reportStatus", "2"); // 报表状态1成功2退回删除
				OrgVO orgVO = (OrgVO) getHyPubBO().queryByPrimaryKey(
						OrgVO.class, hvo.getPk_org());
				json.put("subcompany_id", orgVO.getCode()); // 公司编号
				// 调用生产系统删除接口
				String mes = HttpClient.httpPostRaw(scUrl+"/ncController/ncResult", json.toString(), headers,
						"utf-8");
				if (StringUtils.isEmpty(mes)) {
					return "调用生产系统驳回单据接口返回数据为空，请检查！";
				}
				// 保存日志
				SaveSCLog(hvo,reason,json.toString(),mes);
				JSONObject res = JSONObject.fromObject(mes);
				if(res.getInt("code") != 200){
					return "调用生产系统驳回单据接口返回提示："+res.getString("msg");
				}
			}else{
				return "获取生产系统Token失败："+tokenJson.getString("msg");
			}
		}
		// 删除单据
		HashMap<String, Object> eParam = new HashMap<String, Object>();
		eParam.put("notechecked", "notechecked");
		try {
			NCLocator.getInstance().lookup(IplatFormEntry.class).processAction("DELETE", "F1", null, arapvos[0],
					null, eParam);
		} catch (BusinessException e) {
			// TODO: handle exception
			return "删除单据出错："+e.getMessage();
		}
		
		return null;
	}

	// 获取URL
	private JSONObject getSCUrl() throws BusinessException {
		JSONObject json = new JSONObject();
        String scurl = (String) getHyPubBO().findColValue(
                "bd_defdoc",
                "name",
                "nvl(dr,0) = 0 and code = 'SCURL' and pk_defdoclist in " +
                        "(select pk_defdoclist from bd_defdoclist where code = 'THIRDRELA' and nvl(dr,0) = 0)");
        if (StringUtils.isEmpty(scurl)) {
            json.put("success", "N");
            json.put("errinfo", "请先配置接口地址自定义档案（自定义档案-集团，编码[THIRDRELA]，档案编码[SCURL]）！");
            return json;
        }
        json.put("success", "Y");
        json.put("url",scurl);
        return json;
	}

	// 获取Token
	private String getToken(String url) {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		json.put("userName", "ctsj");
		json.put("password", "UQHRplvWRGyxJwAOs1/5/nJSC2lZ4oxw9dAS0vv+0Jk4Va/VKAcQJnGEF4YBmoVFJsOWfr0L0sluY1ATSCeFmw==");
		Map<String, String> headers = new HashMap<>();
		String data = HttpClient.httpPostRaw(url + "/sbts/getToken", json.toString(), headers, "utf-8");
		return data;
	}
	// 保存记录
	private String SaveSCLog(BaseBillVO hvo, String reason, String rdtdata, 
			String sendata) throws BusinessException {
		// TODO Auto-generated method stub
		String mes = "";
		I8LogVO vo = new I8LogVO();
		vo.setBill_code(hvo.getBillno());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		vo.setCreationtime(df.format(new Date()));// 创建时间
		vo.setPk_bill(hvo.getPrimaryKey());// 单据主键
		vo.setTransi_type("SCXT"); // 生产系统
		vo.setUserid(hvo.getCreator());// 原单据创建人
		vo.setReason(reason);// 驳回原因
		vo.setRdtdata(rdtdata);// 生产系统返回数据
		vo.setDef1(sendata);// 发送生产系统数据
		vo.setDef2("驳回");// 类型
		vo.setPk_org(hvo.getPk_org());// 组织主键
		vo.setPk_group(hvo.getPk_group());// 集团主键
		try {
			getHyPubBO().insert(vo);
		} catch (BusinessException e) {
			// TODO: handle exception
			return e.getMessage();
		}
		return mes;
	}
}
