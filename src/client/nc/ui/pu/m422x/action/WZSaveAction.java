package nc.ui.pu.m422x.action;

import java.awt.event.ActionEvent;
import java.util.List;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
import nc.ui.pcm.utils.GetDao;
import nc.ui.trade.business.HYPubBO_Client;
import nc.vo.bc.pmpub.project.ProjectHeadVO;
import nc.vo.pmpub.projecttype.ProjectTypeHeadVO;
import nc.vo.pu.m422x.entity.StoreReqAppHeaderVO;
import nc.vo.pu.m422x.entity.StoreReqAppItemVO;
import nc.vo.pu.m422x.entity.StoreReqAppVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

@SuppressWarnings("restriction")
public class WZSaveAction extends SaveAction {
	private static final long serialVersionUID = -224173013411231508L;
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
	@Override
	public void doAction(ActionEvent e) throws Exception {
		// 验证物资服务需求申请单保存 po_storereq
		String fg = "0";
		String user = InvocationInfoProxy.getInstance().getUserId();//当前用户主键
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		String querySql = "select c.cuserid, a.pk_role, a.role_name,c.user_name from sm_role a left join sm_user_role b  on a.pk_role=b.pk_role "
				+ " left join sm_user c on c.cuserid=b.cuserid  where a.role_code = 'WZXQ001'";
		List<Object[]> resultList = getDao.query(querySql);
		System.out.println("sql==="+querySql);
		if (resultList.size() > 0) {
			for (int i = 0; i < resultList.size(); i++) {
				Object[] item = resultList.get(i);
				if(user.equals(item[0])){
					fg = "1";
				}
			}
		}
		String flag = "0";// 是否可保存标志
		int index = 0;// 行索引
		StoreReqAppVO billVO = (StoreReqAppVO) this.editor.getValue();// 物资及服务需求申请单VO
		StoreReqAppHeaderVO headvo = (StoreReqAppHeaderVO) billVO.getParentVO();
		StoreReqAppItemVO[] bodyVOs = (StoreReqAppItemVO[]) billVO
				.getChildrenVO();// 表体VO
		for (int i = 0; i < bodyVOs.length; i++) {
			StoreReqAppItemVO itemvo = bodyVOs[i];
			String pk_project = itemvo.getCprojectid() + "";// 项目主键
			ProjectHeadVO xmvo = (ProjectHeadVO) HYPubBO_Client
					.queryByPrimaryKey(ProjectHeadVO.class, pk_project + ""); // 项目档案VO
			ProjectTypeHeadVO typeVO = (ProjectTypeHeadVO) HYPubBO_Client
					.queryByPrimaryKey(ProjectTypeHeadVO.class,
							xmvo.getPk_projectclass() + "");// 项目类型VO
			String type_name = typeVO.getType_name();// 项目类型名称
			String sql = "SELECT NAME FROM RL_CBSNAME WHERE NAME LIKE '%"
					+ type_name + "%' AND TYPE = 'WZFWSQ'";
			System.out.println("sql===" + sql);
			List<Object[]> ls = getDao.query(sql);
			if (ls != null && ls.size() > 0 && "0".equals(fg)) {
				String sourceid = itemvo.getCsourceid() + "";// 来源单据主键
				// 如果没有来源单据，则不允许保存
				if (!"~".equals(sourceid) && "null".equals(sourceid)
						&& !"".equals(sourceid) && sourceid != null) {
					flag = "1";
					index = i + 1;
				}
			}
		}
		String pk_org = headvo.getPk_org();
		String zzsql = "SELECT COUNT(NAME) AS SL FROM RL_CBSNAME WHERE NAME = '"
				+ pk_org + "' AND TYPE = 'WZFWSQZZ'";
		System.out.println("zzsql===" + zzsql);
		List<Object[]> zzls = getDao.query(zzsql);
		if (zzls != null && zzls.size() > 0 && Integer.parseInt(zzls.get(0)[0]+"") == 1) {
			flag = "0";
		}
		if ("0".equals(flag)) {
			super.doAction(e);
		} else if ("1".equals(flag)) {
			ExceptionUtils.wrappBusinessException("第" + index + "行不允许自制！");
		}
		// XBX 20211213新增项目完工
		for (int i = 0; i < bodyVOs.length; i++) {
			StoreReqAppItemVO itemvo = bodyVOs[i];
			String pk_project = itemvo.getCprojectid() + "";// 项目主键
			ProjectHeadVO xmvo = (ProjectHeadVO) HYPubBO_Client
					.queryByPrimaryKey(ProjectHeadVO.class, pk_project + ""); // 项目档案VO
			/*
			 * 保存时检查该项目是否完成对账，已完成对账不允许保存。（是否完成对账档案：是、否；需要区分组织）
			 */
			String sqxmorg = (String) new HYPubBO().findColValue("sys_config",
					"config_value", "config_key='CLCK_ORG'");
			String errmsg = "";
			if(sqxmorg.contains(pk_org)){
				// 已完成对账不允许保存
				if("1001A2100000000B68C1".equals(xmvo.getDef17())){
					errmsg += "项目编码["+xmvo.getProject_code()+"]已完成对账，不允许保存！\n";
				}
			}
			if(!"".equals(errmsg)){
				throw new BusinessException(errmsg);
			}
		}
	}
}