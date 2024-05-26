package nc.itf.uapbd;

import nc.itf.pubapp.pub.smart.ISmartService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.conn.usertoelec.ConnUserElecVO;

public interface IUsertoelecMaintain extends ISmartService{

	 public ConnUserElecVO[] query(IQueryScheme queryScheme)
      throws BusinessException, Exception;
}