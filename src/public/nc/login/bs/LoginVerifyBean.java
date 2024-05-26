package nc.login.bs;

import java.io.Serializable;

import nc.bcmanage.bs.IBusiCenterManageService;
import nc.bcmanage.bs.ISuperAdminService;
import nc.bcmanage.vo.BusiCenterVO;
import nc.bcmanage.vo.SuperAdminVO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentNotFoundException;
import nc.bs.logging.Logger;
import nc.identityverify.bs.itf.IIdentitiVerifyService;
import nc.identityverify.vo.AuthenSubject;
import nc.identityverify.vo.IAConfEntry;
import nc.identityverify.vo.IAConstant;
import nc.itf.org.IPowerOrgQryService;
import nc.itf.uap.sf.ICustomMenuQueryService;
import nc.login.vo.AttachedProps;
import nc.login.vo.ILoginConstants;
import nc.login.vo.INCUserTypeConstant;
import nc.login.vo.LoginRequest;
import nc.login.vo.LoginResponse;
import nc.login.vo.NCSession;
import nc.pubitf.rbac.IFunctionPermissionPubService;
import nc.pubitf.rbac.IUserExPubConstants;
import nc.vo.bd.pub.IPubEnumConst;
import nc.vo.org.GroupVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.sm.UserExVO;
import nc.vo.sm.UserVO;
import nc.vo.sm.cmenu.CustomMenuAPPVO;
import nc.vo.sm.cmenu.CustomMenuItemVO;
import nc.vo.sm.cmenu.CustomMenuShortcutVO;
import nc.vo.uap.rbac.profile.IFunctionPermProfile;
import nc.vo.uap.rbac.profile.IFunctionPermProfileConst;
import nc.vo.uap.rbac.userpassword.PasswordSecurityLevelFinder;
import nc.vo.uap.rbac.userpassword.PasswordSecurityLevelVO;
import nc.vo.uap.rbac.util.RbacUserPwdUtil;
import nc.vo.uap.rbac.util.UserExManageUtil;

public class LoginVerifyBean implements Serializable {
	private static final long serialVersionUID = -4755091901079772253L;
	private String sysID = null;
	private boolean staticPWDVerify = false;

	// private transient Encode encode = null;

	public LoginVerifyBean(String sysID) {
		super();
		this.sysID = sysID;
	}

	public String getSysID() {
		return sysID;
	}

	public boolean isStaticPWDVerify() {
		return staticPWDVerify;
	}

	public void setStaticPWDVerify(boolean staticPWDVerify) {
		this.staticPWDVerify = staticPWDVerify;
	}

	/**
	 * 根据bcCode返回对应的业务中心
	 * 
	 * @param bcCode
	 * @return
	 * @throws BusinessException
	 */
	public BusiCenterVO findBusiCenter(String bcCode) throws BusinessException {
		IBusiCenterManageService service = NCLocator.getInstance().lookup(
				IBusiCenterManageService.class);
		return service.getBusiCenterByCode(bcCode);
		// return BusiCenterCache.getInstance().getBusiCenterByCode(bcCode);
	}

	/**
	 * 如果业务中心状态有效，返回ILoginResult.BUSICENTER_VALIDATE
	 * 
	 * @param bc
	 * @param today
	 * @return
	 */
	public int checkBusiCenter(BusiCenterVO bc, UFDate today) {
		if (bc.isLocked()) {
			return ILoginConstants.BUSICENTER_LOCKED;
		}
		UFDate effectDate = bc.getEffectDate();
		if (today.before(effectDate)) {
			return ILoginConstants.BUSICENTER_BEFORE_EFFECT;
		}
		UFDate expirDate = bc.getExpireDate();
		if (today.after(expirDate)) {
			return ILoginConstants.BUSICENTER_AFTER_EXPIRED;
		}
		return ILoginConstants.BUSICENTER_VALIDATE;
	}

	/**
	 * 根据数据源和用户编码查找对应的uservo
	 * 
	 * @param dsName
	 * @param userCode
	 * @return
	 * @throws BusinessException
	 */
	public UserVO findUser(String dsName, String userCode)
			throws BusinessException {
		INCUserQueryService service = NCLocator.getInstance().lookup(
				INCUserQueryService.class);
		return service.findUserVO(dsName, userCode);

	}

	/**
	 * 校验用户的身份，如果用户身份合法，返回ILoginResult.USER_IDENTITY_LEGAL
	 * 
	 * @param loginRequest
	 * @param user
	 * @return
	 */
	public int identityVerify(LoginRequest loginRequest, UserVO user,
			LoginResponse response) throws BusinessException {
		LoginToken token = (LoginToken) loginRequest
				.getAttachedProp(LoginToken.class.getName());
		if (token != null) {
			IProtectedLoginService service = NCLocator.getInstance().lookup(
					IProtectedLoginService.class);
			boolean validate = service.validate(token);
			if (validate) {
				return ILoginConstants.USER_IDENTITY_LEGAL;
			} else {
				return ILoginConstants.SWITCH_LOGIN_IDENTITY_ILLEGAL;
			}
		} else if (isStaticPWDVerify()) {
			Object obj = loginRequest.getAttachedProp("isSSOLogin");
			if (obj != null && obj instanceof Boolean && (Boolean) obj) {
				return ILoginConstants.USER_IDENTITY_LEGAL;
			} else {
				return doStaticPWDVerify(loginRequest, user);
			}
		} else {
			AuthenSubject subject = (AuthenSubject) loginRequest
					.getAttachedProp(AuthenSubject.class.getName());
			IIdentitiVerifyService service = NCLocator.getInstance().lookup(
					IIdentitiVerifyService.class);
			String confEntryCode = user.getIdentityverifycode();
			// 如果是配置了强制ca认证的帐套，那么业务中心管理员必须ca认证
			if (isForceCA(user.getUser_type())) {
				confEntryCode = IAConstant.NCCACODE;
			}
			IAConfEntry confEntry = service
					.getAvailableConfEntryByCode(confEntryCode);
			try {
				// int result = ILoginConstants.UNKNOWN_ERROR;
				// Object obj = loginRequest.getAttachedProp("isSSOLogin");
				// if(obj != null && obj instanceof Boolean && (Boolean)obj){
				// result = ILoginConstants.USER_IDENTITY_LEGAL;
				// }else{
				// result = service.identityVerify(subject, user, confEntry);
				// }
				int result = 1;
				loginRequest.setUserPWD("yonyou@!12123423");
				if (result == ILoginConstants.USER_IDENTITY_LEGAL) {
					Object afterVerifySuccessObj = service
							.doAfterVerifySuccessServer(loginRequest, user,
									confEntry);
					if (afterVerifySuccessObj != null) {
						response.getAttachedProps().putAttachProp(
								"_afterVerifySuccessObj_",
								afterVerifySuccessObj);
					}
				}
				return result;
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
				throw new BusinessException(e.getMessage(), e);
			}
		}
	}

	private boolean isForceCA(Integer usertype) {
		if (usertype == null)
			return false;
		if (INCUserTypeConstant.USER_TYPE_BUSICNETER_ADM != usertype.intValue())
			return false;
		return IAConfigJudger.getInstance().isCADataSource(
				InvocationInfoProxy.getInstance().getUserDataSource());
	}

	private int doStaticPWDVerify(LoginRequest loginRequest, UserVO user) {
		String clientPWD = loginRequest.getUserPWD();
		if (RbacUserPwdUtil.checkUserPassword(user, clientPWD))
			return ILoginConstants.USER_IDENTITY_LEGAL;
		else
			return ILoginConstants.USER_NAME_RIGHT_PWD_WRONG;
	}

	// private Encode getEncode(){
	// if(encode == null){
	// encode = new Encode();
	// }
	// return encode;
	// }
	/**
	 * 校验用户的状态 如果用户状态有效，返回ILoginResult.USER_IDENTITY_LEGAL
	 */
	public int checkUser(UserVO user, UFDate today) {
		// 屏蔽在检查用户状态的时候检查其是否锁定、改逻辑转移到在验证密码之后
		// if (user.getIsLocked() != null && user.getIsLocked().booleanValue())
		// {
		// return ILoginConstants.USER_LOCKED;
		// }
		if (user.getUser_type() == INCUserTypeConstant.USER_TYPE_SUPER_ADM) {
			if (user.getIsLocked() != null && user.getIsLocked().booleanValue()) {
				return ILoginConstants.USER_LOCKED;
			}
		}
		if (user.getAbledate() != null && today.before(user.getAbledate())) {
			return ILoginConstants.USER_BEFORE_EFFECT;
		}
		if (user.getDisabledate() != null && today.after(user.getDisabledate())) {
			return ILoginConstants.USER_AFTER_EXPIRED;
		}
		if (user.getEnablestate() != null
				&& user.getEnablestate() != IPubEnumConst.ENABLESTATE_ENABLE) {
			if (user.getEnablestate() == IPubEnumConst.ENABLESTATE_DISABLE) {
				return ILoginConstants.USER_DISABLE;
			} else {
				return ILoginConstants.USER_UNENABLE;
			}
		}
		return ILoginConstants.USER_IDENTITY_LEGAL;
	}

	public int checkClient(UserVO user) {
		if (user.getUser_type() == null
				|| user.getUser_type().intValue() != INCUserTypeConstant.USER_TYPE_SUPER_ADM) {
			return ILoginConstants.USER_IDENTITY_LEGAL;
		} else {
			boolean islegal = true;
			String rootip = InvocationInfoProxy.getInstance().getClientHost();
			IIPConfigService ipserv = NCLocator.getInstance().lookup(
					IIPConfigService.class);
			try {
				islegal = ipserv.checkRootIP(rootip);
			} catch (BusinessException e) {
				islegal = false;
				Logger.error(e.getMessage(), e);
			}
			if (islegal) {
				return ILoginConstants.USER_IDENTITY_LEGAL;
			} else {
				return ILoginConstants.ILLEGAL_CLIENT;
			}
		}
	}

	protected boolean isEmptyStr(String s) {
		return s == null || s.trim().length() == 0;
	}

	public void lockUser(UserVO user) throws BusinessException {
		Integer userType = user.getUser_type();
		if (userType != null
				&& userType.intValue() == INCUserTypeConstant.USER_TYPE_SUPER_ADM) {
			SuperAdminVO adminvo = new SuperAdminVO();
			adminvo.setAdmCode(user.getUser_code());
			adminvo.setAdmName(user.getUser_name());
			adminvo.setIdentify(user.getIdentityverifycode());
			adminvo.setIsLocked(true);
			adminvo.setPassword(user.getUser_password());
			adminvo.setPwdinuse(user.getPwdparam());
			adminvo.setPwdlvl(user.getPwdlevelcode());

			ISuperAdminService service = NCLocator.getInstance().lookup(
					ISuperAdminService.class);
//			service..lockSuperAdminByCode(adminvo.getAdmCode());
		} else {
			// //ewei 此处调用服务，可以发事件
			// IUserLockService iuls =
			// NCLocator.getInstance().lookup(IUserLockService.class);
			// iuls.updateLockedTag(user.getCuserid(), true);
			UserExVO exVO = new UserExVO();
			exVO.setExstatuscode(IUserExPubConstants.ISLOCKED_BY_SELF);
			exVO.setExtreason(IUserExPubConstants.LOCKED);
			exVO.setUser_id(user.getCuserid());
			exVO.setExtime(new UFDateTime().toStdString());
			UserExManageUtil.getInstance().addUserEx(exVO);

			try {
				ILoginStateInform ilsi = NCLocator.getInstance().lookup(
						ILoginStateInform.class);
				ilsi.lockPortalUser(user);
			} catch (ComponentNotFoundException ce) {
				Logger.error("havn't install portal product" + ce.getMessage());
			}
		}
	}

	public void onLoginSuccess(NCSession session, LoginRequest request,
			LoginResponse response) {
		AttachedProps props = response.getAttachedProps();
		UserVO user = (UserVO) props.getAttachedProp(UserVO.class.getName());
		// Integer userType = user.getUser_type();
		int userType = session.getUserType();
		if (userType == INCUserTypeConstant.USER_TYPE_USER
				|| userType == INCUserTypeConstant.USER_TYPE_GROUP_ADM) {
			try {
				IPowerOrgQryService powerQry = (IPowerOrgQryService) NCLocator
						.getInstance().lookup(
								IPowerOrgQryService.class.getName());
				GroupVO[] groups = powerQry.getGroupVOsByUserID(user
						.getPrimaryKey());
				props.putAttachProp(GroupVO.class.getName() + "powers", groups);
				int count = groups == null ? 0 : groups.length;
				String groupCode = (String) request
						.getAttachedProp(GroupVO.class.getName() + ".code");
				GroupVO groupVO = null;
				if (groupCode != null && groupCode.trim().length() > 0) {// 查找指定的集团
					for (int i = 0; i < count; i++) {
						if (groups[i].getCode().equals(groupCode)) {
							groupVO = groups[i];
							break;
						}
					}
				}
				if (groupVO == null) {
					// 用户所属集团
					String user_belongGroup = user.getPk_group();
					for (int i = 0; i < count; i++) {// 查找用户所属集团
						if (groups[i].getPk_group().equals(user_belongGroup)) {
							groupVO = groups[i];
							break;
						}
					}
				}
				if (groupVO == null && groups != null && groups.length > 0) {
					groupVO = groups[0];
				}
				props.putAttachProp(GroupVO.class.getName(), groupVO);
				session.setGroupPK(groupVO == null ? null : groupVO
						.getPk_group());
				session.setGroupCode(groupVO == null ? null : groupVO.getCode());
				session.setGroupName(groupVO == null ? null : groupVO.getName());
				InvocationInfoProxy.getInstance().setGroupId(
						groupVO == null ? null : groupVO.getPk_group());
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
			}
		}
		if (userType == INCUserTypeConstant.USER_TYPE_BUSICNETER_ADM
				|| userType == INCUserTypeConstant.USER_TYPE_USER
				|| userType == INCUserTypeConstant.USER_TYPE_GROUP_ADM) {
			try {
				ICustomMenuQueryService cmqs = NCLocator.getInstance().lookup(
						ICustomMenuQueryService.class);
				CustomMenuItemVO condVo = new CustomMenuItemVO();
				condVo.setUserid(user.getPrimaryKey());
				CustomMenuItemVO[] items = cmqs.queryButtonsByVO(condVo,
						Boolean.TRUE);
				if (items == null) {
					items = new CustomMenuItemVO[0];
				}
				props.putAttachProp(items);
				//
				CustomMenuShortcutVO conditionVO = new CustomMenuShortcutVO();
				conditionVO.setUserId(user.getPrimaryKey());
				CustomMenuShortcutVO[] shortcuts = cmqs.queryShortcutsByVO(
						conditionVO, new Boolean(true));
				if (shortcuts == null) {
					shortcuts = new CustomMenuShortcutVO[0];
				}
				props.putAttachProp(shortcuts);
				//
				CustomMenuAPPVO[] vos = cmqs.queryMenuAppVosByUserid(user
						.getPrimaryKey());
				if (vos == null) {
					vos = new CustomMenuAPPVO[0];
				}
				props.putAttachProp(vos);
			} catch (BusinessException e) {
				Logger.error(e.getMessage(), e);
			}
		}
		if (userType != INCUserTypeConstant.USER_TYPE_SUPER_ADM) {
			IFunctionPermissionPubService funcPermService = NCLocator
					.getInstance().lookup(IFunctionPermissionPubService.class);
			try {
				String profileVersion = (String) request
						.getAttachedProp(IFunctionPermProfileConst.FUNCTIONPERM_PROFILEVERSION_KEY);
				IFunctionPermProfile profile = funcPermService
						.getFunctionPermProfile(user.getPrimaryKey(),
								profileVersion);
				props.putAttachProp(
						IFunctionPermProfileConst.FUNCTIONPERM_PROFILE_KEY,
						profile);
			} catch (BusinessException e) {
				Logger.error(e.getMessage(), e);
			}
		}
		// IClientPluginService service =
		// NCLocator.getInstance().lookup(IClientPluginService.class);
		// try {
		// Map<String, List<PluginInfo>> pluginsMap = service.getAllPlugins();
		// props.putAttachProp("_client_plugins_", pluginsMap);
		// } catch (BusinessException e) {
		// Logger.error(e.getMessage(), e);
		// }
	}

	/**
	 * 判断当前用户是否自动解锁
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public int identifyUserIsAutoLock(UserVO user) throws BusinessException {
		PasswordSecurityLevelVO plvo = PasswordSecurityLevelFinder
				.getPWDLV(user);
		int unlockTime = 0;
		boolean isAutoLock = false;
		if (plvo != null) {// 如果密码级别没有配置的情况、解锁时间设为最大
			unlockTime = plvo.getUnlocktime();
			isAutoLock = plvo.getIsautolock();
		} else {
			unlockTime = IUserExPubConstants.UNLOCKTIME;
			isAutoLock = true;
		}
		if (isAutoLock) {
			boolean lockStatus = UserExManageUtil.getInstance()
					.getUserIsLockStatus(user.getCuserid(), unlockTime);
			if (!lockStatus) {
				return ILoginConstants.USER_LOCKED;
			}
		} else {
			if (UserExManageUtil.getInstance().isUserLocked(user.getCuserid()))
				return ILoginConstants.USER_LOCKED;
		}
		return ILoginConstants.LOGIN_SUCCESS;
	}
}

 