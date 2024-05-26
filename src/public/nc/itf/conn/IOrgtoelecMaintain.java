package nc.itf.conn;

import nc.itf.pubapp.pub.smart.ISmartService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.conn.orgtoelec.ConnOrgElecVO;

public interface IOrgtoelecMaintain extends ISmartService{

	 public ConnOrgElecVO[] query(IQueryScheme queryScheme)
      throws BusinessException, Exception;
}