package nc.uap.portal.user.chain;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.servlet.http.HttpServletRequest;
import sun.misc.BASE64Encoder;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.log.LfwLogger;
import nc.uap.portal.login.itf.LoginInterruptedException;
import nc.uap.portal.login.vo.AuthenticationUserVO;

public class SimpleMKeyLoginChain extends AbstractVerifyChain {

@Override
public void doVerify(HttpServletRequest request, VerifyAtomChain chain) {
 String authKey = request.getParameter("AuthKey");
 String userid = request.getParameter("userid");
 String mKey = LfwRuntimeEnvironment.getServerConfig().get("MKEY");
 String ret = buildAuthKey(userid, System.currentTimeMillis() , mKey);
 if(ret.equals(authKey)){
  AuthenticationUserVO userVO = new AuthenticationUserVO();
  userVO.setUserID(userid);
  try {
   doLogin(userVO);
  } catch (LoginInterruptedException e) {
   LfwLogger.error(e.getMessage(), e);
  }
  if (hasLogin())
   chain.doFilter(request);
 }
}

@Override
public int compareTo(IUserVerifyChain o) {
 return 0;
}

public static String buildAuthKey(String userid, long timeSpan, String mKey){
 try {
  String time = "0000";//(timeSpan + "").substring(0, 9);
  String key = userid + time + mKey;
  byte[] codes = MessageDigest.getInstance("SHA-1").digest(key.getBytes("UTF-8"));
  return new BASE64Encoder().encode(codes).replace("+", "");
 } catch (Exception e) {
  LfwLogger.error(e);
 }
 return "";
}
}
