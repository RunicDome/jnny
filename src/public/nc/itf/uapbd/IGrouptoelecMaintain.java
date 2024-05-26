package nc.itf.uapbd;

import nc.itf.pubapp.pub.smart.ISmartService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.conn.grouptoelec.ConnGroupElecVO;

public interface IGrouptoelecMaintain extends ISmartService{

	 public ConnGroupElecVO[] query(IQueryScheme queryScheme)
      throws BusinessException, Exception;
}