package nc.ws.intf;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import weaver.rsa.security.RSA;

public class OaTest {
	// OA中台
	public static String ZTAPPID = "EEAA5436-7577-4BEO-8c6c-89E9D88805EA";
	public static void main(String[] args) {
		String tokenBack = getRegist("http://172.18.130.150:8081/");
		JSONObject object = JSONObject.fromObject(tokenBack);
		String secrit = object.getString("secrit");
		String spk = object.getString("spk");
		String bak2 = applytoken(secrit, spk, "http://172.18.130.150:8081/", "ZT");
		System.out.println(bak2);
	}

	/**
	 * 注册
	 * 
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static String getRegist(String url) {
		Map<String, String> heads = new HashMap<String, String>();
		String cpk = new RSA().getRSA_PUB();
		heads.put("appid", ZTAPPID);
		heads.put("cpk", cpk);
		String data = HttpClient.httpPostForm(url + "/api/ec/dev/auth/regist",
				null, heads, "utf-8");
		return data;
	}
	/**
	 * 获取token
	 * 
	 * @param secrit
	 * @param spk
	 * @return
	 */
	public static String applytoken(String secrit, String spk, String url,
			String type) {
		Map<String, String> heads = new HashMap<String, String>();
		RSA rsa = new RSA();
		String secret_2 = rsa.encrypt(null, secrit, null, "utf-8", spk, false);
		if ("ZT".equals(type)) {
			heads.put("appid", ZTAPPID);
		}
		heads.put("secret", secret_2);
		String data = HttpClient.httpPostForm(url
				+ "/api/ec/dev/auth/applytoken", null, heads, "utf-8");
		return data;
	}
}
