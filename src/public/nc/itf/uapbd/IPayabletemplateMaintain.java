package nc.itf.uapbd;

import nc.itf.pubapp.pub.smart.ISmartService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.uap.payabletemplate.PayableTemplate;

public interface IPayabletemplateMaintain extends ISmartService{

	 public PayableTemplate[] query(IQueryScheme queryScheme)
      throws BusinessException, Exception;
}