package nc.bs.er.utils.template;

import javax.servlet.http.HttpServletRequest;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.bs.logging.Logger;
import nc.bs.pf.pub.PfDataCache;
import nc.itf.uap.billtemplate.IBillTemplateQry;
import nc.itf.uap.rbac.IUserManageQuery;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillOperaterEnvVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pubapp.AppContext;
import nc.vo.sm.UserVO;
import nc.web.erm.bill.pub.WebBillTypeFactory;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings({ "unchecked", "restriction"})
public class ERMTemplateQueryUtil {
	private static IBillTemplateQry iBillTemplateQry = null;
    private static IBillTemplateQry getIBillTemplateQry()
    		throws ComponentException {
		if (iBillTemplateQry == null)
		    iBillTemplateQry = (IBillTemplateQry)  NCLocator
		    		.getInstance().lookup(IBillTemplateQry.class.getName());
		return iBillTemplateQry;
	} 
	public static BillOperaterEnvVO getBillOperaterEnvVO(HttpServletRequest request) throws BusinessException{ 

    	String pk_user = request.getParameter("pk_user"); 
    	UserVO userVo = (UserVO) request.getSession().getAttribute("loginUser"); 
    	if(userVo==null||!userVo.getPk_customer().equals(pk_user)){ 
    	userVo = NCLocator
    	.getInstance().lookup(IUserManageQuery.class).getUser(pk_user); 
    	} 

    	String pk_group = AppContext.getInstance().getPkGroup(); 
    	request.getSession().setAttribute("loginUser", userVo); 
    	String billType = request.getParameter("billType"); 
    	if(StringUtils.isEmpty(billType)){
			   throw new BusinessException("单据类型或交易类型参数传递不能为空值");
		}
    	BillOperaterEnvVO envvo=new BillOperaterEnvVO(); 
    	envvo.setNodekey(billType); 
    	envvo.setBilltype("2647".equals(billType)?"20110RB":WebBillTypeFactory.getWebBillType(billType).getNodeCode()); 
    	envvo.setOperator(pk_user); 

    	envvo.setCorp(pk_group); 

    	return envvo; 
    	}

    public static BillTempletVO findBillTempletDatas(BillOperaterEnvVO envvo) throws BusinessException{
    	
    	BillTempletVO billTempletVO  = getIBillTemplateQry().findBillTempletData(envvo);
    	
    	if (billTempletVO == null) {
    		String billType = envvo.getNodekey();
			Logger.info("加载模板失败：" + envvo.getBilltype()
							+ ":" + envvo.getNodekey() + ":"
							+ envvo.getOperator() + ":" + envvo.getCorp() + ":"+"查询预置模板");
			BilltypeVO billtypeVO = PfDataCache.getBillType(billType);
			if (billtypeVO == null) {
				throw new BusinessException("交易类型查询出错:" + billType + "-"
						+ InvocationInfoProxy.getInstance().getGroupId());
			}
			billType = billtypeVO.getPk_billtypecode();
			UFBoolean istransaction = billtypeVO.getIstransaction();
			if (istransaction.booleanValue()) {
				billType = billtypeVO.getParentbilltype();
				envvo.setBilltype("2647".equals(billType)?"20110RB":WebBillTypeFactory.getWebBillType(billType).getNodeCode());
				envvo.setNodekey(billType);

				billTempletVO = findBillTempletDatas(envvo);
				if (billTempletVO == null) {
					throw new BusinessException("加载模板失败：" + envvo.getBilltype()
							+ ":" + envvo.getNodekey() + ":"
							+ envvo.getOperator() + ":" + envvo.getCorp() + ":");
				}

			}

		}
    	return billTempletVO;
    }

	public static BillTempletVO getTemplateVO(String pk_group, String pk_user,
			String billType) throws BusinessException {
		BillOperaterEnvVO envvo = new BillOperaterEnvVO();
		envvo.setBilltype("2647".equals(billType)?"20110RB":WebBillTypeFactory.getWebBillType(billType).getNodeCode());
		envvo.setNodekey(billType);
		envvo.setOperator(pk_user);
		envvo.setCorp(pk_group);

		BillTempletVO billTempletVO = findBillTempletDatas(envvo);

		if (billTempletVO == null) {
			Logger.info("加载模板失败：" + envvo.getBilltype()
							+ ":" + envvo.getNodekey() + ":"
							+ envvo.getOperator() + ":" + envvo.getCorp() + ":"+"查询预置模板");
			BilltypeVO billtypeVO = PfDataCache.getBillType(billType);
			if (billtypeVO == null) {
				throw new BusinessException("交易类型查询出错:" + billType + "-"
						+ InvocationInfoProxy.getInstance().getGroupId());
			}
			billType = billtypeVO.getPk_billtypecode();
			UFBoolean istransaction = billtypeVO.getIstransaction();
			if (istransaction.booleanValue()) {
				billType = billtypeVO.getParentbilltype();
				envvo = new BillOperaterEnvVO();
				envvo.setBilltype("2647".equals(billType)?"20110RB":WebBillTypeFactory.getWebBillType(billType).getNodeCode());
				envvo.setNodekey(billType);
				envvo.setOperator(pk_user);
				envvo.setCorp(pk_group);

				billTempletVO = findBillTempletDatas(envvo);
				if (billTempletVO == null) {
					throw new BusinessException("加载模板失败：" + envvo.getBilltype()
							+ ":" + envvo.getNodekey() + ":"
							+ envvo.getOperator() + ":" + envvo.getCorp() + ":");
				}

			}

		}
		return billTempletVO;
	}
}
