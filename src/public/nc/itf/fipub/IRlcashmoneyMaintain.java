package nc.itf.fipub;

import nc.itf.pubapp.pub.smart.ISmartService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.fipub.rlcashmoney.CashMoneyVO;

public interface IRlcashmoneyMaintain extends ISmartService{

	 public CashMoneyVO[] query(IQueryScheme queryScheme)
      throws BusinessException, Exception;
}