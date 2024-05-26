package nc.itf.arap.forthird;

import java.io.UnsupportedEncodingException;

import nc.vo.arap.pay.PayBillVO;
import nc.vo.pub.BusinessException;
import net.sf.json.JSONObject;

// ermweb包 对接第三方
public interface IArapForDGSWService {
	// 拼装URL，打开税务系统影像上传界面
	public JSONObject genSWUrl(String billType, String pk_primarykey)
			throws BusinessException, UnsupportedEncodingException;

	// 单据状态同步税务系统
	public JSONObject sendBillByNCBill(String billno, String pk_org,
			String eventType, String billType) throws BusinessException;

	// 单据状态同步宏景系统
	public JSONObject sendHJBillByNCBill(PayBillVO hvo, String state,
			String reason) throws BusinessException;

	// 单据状态同步神思系统
	public JSONObject sendSSBillByNCBill(String pk_primarykey, int state)
			throws BusinessException;
}
