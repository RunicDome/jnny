package nc.uap.portal.login;

import java.util.HashMap;
import java.util.Map;

import nc.uap.lfw.login.vo.LfwSessionBean;
import nc.uap.portal.deploy.vo.PtSessionBean;
import nc.uap.portal.login.authfield.ExtAuthField;
import nc.uap.portal.login.authfield.PasswordExtAuthField;
import nc.uap.portal.login.authfield.UserIdExtAuthField;
import nc.uap.portal.login.itf.AbstractLfwIntegrateProvider;
import nc.uap.portal.login.itf.ILfwIntegrationHandler;
import nc.uap.portal.login.itf.ILoginHandler;
import nc.uap.portal.login.itf.LoginHelper;
import nc.uap.portal.login.itf.LoginInterruptedException;
import nc.uap.portal.login.vo.AuthenticationUserVO;
import nc.uap.portal.login.vo.LfwFunNodeVO;
import nc.uap.portal.login.vo.LfwTreeFunNodeVO;
import nc.uap.portal.user.impl.PortalLoginHandler;
import uap.lfw.core.ml.LfwResBundle;

/**
 * Portal登陆产生者
 * @author licza
 *
 */
public class PortalLoginProvider extends AbstractLfwIntegrateProvider{

	@Override
	public ExtAuthField[] getAuthFields() {
		ExtAuthField[] fields = new ExtAuthField[2];
		fields[0] = new UserIdExtAuthField(LfwResBundle.getInstance().getStrByID("pserver", "PortalLoginProvider-000000")/*用户*/, "userid", true);
		fields[1] = new PasswordExtAuthField(LfwResBundle.getInstance().getStrByID("pserver", "PortalLoginProvider-000001")/*密码*/, "password", false);
		return fields;
	}

	@Override
	public LfwTreeFunNodeVO[] getFunNodes() {
		return null;
	}

	@Override
	public LfwFunNodeVO[] getFunNodes(Map<String, String> param) {
		return null;
	}

	@Override
	public LoginHelper<? extends LfwSessionBean> getLoginHelper() {
		LoginHelper<PtSessionBean> helper = new LoginHelper<PtSessionBean>() {
			@Override
			public ILoginHandler<PtSessionBean> createLoginHandler() {
				ILoginHandler<PtSessionBean> handler = new PortalLoginHandler();
				return handler;
			}

			@Override
			public ILfwIntegrationHandler createIntegrationHandler() {
				ILfwIntegrationHandler handler = new ILfwIntegrationHandler(){
					@Override
					public AuthenticationUserVO getSsoAuthenticateVO(Map<String, String> param)	throws LoginInterruptedException {
						AuthenticationUserVO userVO = new AuthenticationUserVO();
						userVO.setUserID(param.get("userid"));
						Map<String, String> extMap = new HashMap<String, String>();
						if(null!=param.get("needverifypasswd") && "N".equals(param.get("needverifypasswd"))){
							extMap.put("needverifypasswd","N");
						}else{
							userVO.setPassword(param.get("password"));
						}
						extMap.put("p_language", "simpchn");
						extMap.put("p_maxwin", "N");
						userVO.setExtInfo(extMap);
						return userVO;
					}

//					@Override
//					public void verify(AuthenticationUserVO userVO)	throws LoginInterruptedException {
//						
//					}
				};
				return handler;
			}
			
		};
		return helper;
	}

}
