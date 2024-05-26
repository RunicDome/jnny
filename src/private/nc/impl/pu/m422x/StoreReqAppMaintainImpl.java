// 物资需求申请单
package nc.impl.pu.m422x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pu.m422x.action.StoreReqAppDeleteAction;
import nc.impl.pu.m422x.action.StoreReqAppInsertAction;
import nc.impl.pu.m422x.action.StoreReqAppUpdateAction;
import nc.itf.arap.forthird.IArapForDGSWService;
import nc.itf.pim.project.prv.IProjectQuery;
import nc.itf.pmpub.projecttype.pub.IProjectTypeQueryService;
import nc.itf.pu.m422x.IStoreReqAppMaintain;
import nc.ui.pcm.utils.GetDao;
import nc.vo.pim.project.ProjectBillVO;
import nc.vo.pim.project.ProjectHeadVO;
import nc.vo.pmpub.projecttype.ProjectTypeHeadVO;
import nc.vo.pu.m422x.entity.StoreReqAppHeaderVO;
import nc.vo.pu.m422x.entity.StoreReqAppItemVO;
import nc.vo.pu.m422x.entity.StoreReqAppVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import net.sf.json.JSONObject;

import org.apache.commons.lang.ArrayUtils;

// 物资需求申请单
@SuppressWarnings({ "rawtypes", "unchecked" })
public class StoreReqAppMaintainImpl implements IStoreReqAppMaintain {
	public void delete(StoreReqAppVO[] vos) throws BusinessException {
		try {
			new StoreReqAppDeleteAction().delete(vos);
			for (StoreReqAppVO vo : vos) {
				StoreReqAppHeaderVO headvo = (StoreReqAppHeaderVO) vo
						.getParentVO();
				if ("SS".equals(headvo.getVdef8())) {
					IArapForDGSWService util = (IArapForDGSWService) NCLocator
							.getInstance().lookup(IArapForDGSWService.class);
					JSONObject hrres = util.sendSSBillByNCBill(
							headvo.getPrimaryKey(), 1);
					if (!"Y".equals(hrres.getString("success"))) {
						throw new BusinessException(hrres.getString("errinfo"));
					}
				}
			}
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	public StoreReqAppVO[] save(StoreReqAppVO[] vos) throws BusinessException {
		try {
			StoreReqAppVO[] insertVOs = pickupInsertVOs(vos);
			StoreReqAppVO[] updateVOs = pickupUpdateVOs(vos);

			if (!ArrayUtils.isEmpty(insertVOs)) {
				// 保存前校验
				beforecheck(insertVOs[0]);
				return new StoreReqAppInsertAction().insert(insertVOs);
			}

			if (!ArrayUtils.isEmpty(updateVOs)) {
				// 保存前校验
				beforecheck(updateVOs[0]);
				return new StoreReqAppUpdateAction().update(updateVOs);
			}
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public void beforecheck(StoreReqAppVO billVO) throws BusinessException {
		// TODO Auto-generated method stub
		String fg = "0";
		String user = InvocationInfoProxy.getInstance().getUserId();// 当前用户主键
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		// 自制控制
		String querySql = "select c.cuserid, a.pk_role, a.role_name,c.user_name from sm_role a"
				+ " left join sm_user_role b  on a.pk_role=b.pk_role "
				+ " left join sm_user c on c.cuserid=b.cuserid  where a.role_code = 'WZXQ001'";
		List<Object[]> resultList = getDao.query(querySql);
		System.out.println("sql===" + querySql);
		if (resultList.size() > 0) {
			for (int i = 0; i < resultList.size(); i++) {
				Object[] item = resultList.get(i);
				if (user.equals(item[0])) {
					fg = "1";
				}
			}
		}
		String flag = "0";// 是否可保存标志
		int index = 0;// 行索引
		StoreReqAppHeaderVO headvo = (StoreReqAppHeaderVO) billVO.getParentVO();
		StoreReqAppItemVO[] bodyVOs = (StoreReqAppItemVO[]) billVO
				.getChildrenVO();// 表体VO
		String pk_org = headvo.getPk_org();// 组织
		for (int i = 0; i < bodyVOs.length; i++) {
			StoreReqAppItemVO itemvo = bodyVOs[i];
			String pk_project = itemvo.getCprojectid() + "";// 项目主键
			IProjectQuery xmiq = (IProjectQuery) NCLocator.getInstance()
					.lookup(IProjectQuery.class);
			ProjectBillVO[] ss = xmiq
					.queryProjectHeadVOsBypks(new String[] { pk_project });
			if (null != ss && ss.length >= 0) {

				/**
				 * XBX 1110 物资需求申请单维护保存时校验该项目是否在(取当前系统时间)年度投资计划中，不在年度投资计划
				 * 中不允许保存。取当前系统时间为年度计划年度
				 */
				Object conforg = (Object) new HYPubBO()
						.findColValue(
								"bd_defdoc",
								"code",
								"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
										+ "FROM BD_DEFDOCLIST WHERE CODE = 'HTSAVEORG')"
										+ " and code = '" + pk_org + "'");// 组织
				if (conforg != null) {
					ProjectHeadVO xmvo = ss[0].getParentVO();
					// EPC字段勾选后不允许提报
					if ("Y".equals(xmvo.getHdef56())) {
						throw new BusinessException("项目编码["
								+ xmvo.getProject_code() + "]为EPC项目，不允许保存");
					}
					IProjectTypeQueryService xmtpiq = (IProjectTypeQueryService) NCLocator
							.getInstance().lookup(
									IProjectTypeQueryService.class);
					ProjectTypeHeadVO typeVO = xmtpiq
							.queryProjectTypeHeadVOsByHeadPK(xmvo
									.getPk_projectclass());// 项目类型VO
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
					// 已完成对账不允许保存
					if ("1001A2100000000B68C1".equals(xmvo.getDef17())) {
						throw new BusinessException("项目编码["
								+ xmvo.getProject_code() + "]已完成对账，不允许保存！");
					}
					// 项目类型自定义档案，如果当前项目的项目类型在项目类型自定义档案里，则校验，否则不校验
					Object xmlx = (Object) new HYPubBO()
							.findColValue(
									"bd_defdoc",
									"code",
									"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
											+ "FROM BD_DEFDOCLIST WHERE CODE = 'XMLXDA')"
											+ " and code = '"
											+ xmvo.getProject_code() + "'");
					if (xmlx != null) {
						String year = getCurrentYear();// 当前年份
						String getndjhsql = "SELECT MAX(TO_NUMBER(CASE WHEN MX.DEF26 = '~' THEN '0' ELSE MX.DEF26 END)) AS ZDZC FROM PM_YEARPLAN ZB INNER "
								+ "JOIN PM_YEARPLAN_B MX ON ZB.PK_YEARPLAN = MX.PK_YEARPLAN WHERE ZB.DR = 0 "
								+ "AND MX.DR = 0 AND ZB.PLAN_YEAR = '"
								+ year
								+ "' AND "
								+ "MX.PK_PROJECT = '"
								+ pk_project
								+ "'";
						System.out.println("查询年度投资计划SQL：" + getndjhsql);
						List<Object[]> ndjels = getDao.query(getndjhsql);
						if (ndjels != null && ndjels.size() > 0
								&& ndjels.get(0) != null
								&& ndjels.get(0)[0] != null
								&& !"".equals(ndjels.get(0)[0])
								&& !"null".equals(ndjels.get(0)[0])) {
							double ndtzjhzd = Double
									.parseDouble(ndjels.get(0)[0] + "");
							if (ndtzjhzd <= 0) {
								throw new BusinessException("项目编码["
										+ xmvo.getProject_code()
										+ "],费用项：主材 没有年度投资计划值，请添加后在进行操作");
							}
						} else {
							throw new BusinessException("项目编码["
									+ xmvo.getProject_code() + "]无年度投资计划，不允许保存");
						}
					}
				}
			}
		}
		if ("1".equals(flag)) {
			throw new BusinessException("第" + index + "行不允许自制！");
		}
	}

	private StoreReqAppVO[] pickupInsertVOs(StoreReqAppVO[] vos) {
		List<StoreReqAppVO> insertList = new ArrayList();

		for (StoreReqAppVO vo : vos) {
			if (2 == vo.getHVO().getStatus()) {
				insertList.add(vo);
			}
		}

		if (insertList.size() > 0) {
			return (StoreReqAppVO[]) insertList
					.toArray(new StoreReqAppVO[insertList.size()]);
		}
		return null;
	}

	private StoreReqAppVO[] pickupUpdateVOs(StoreReqAppVO[] vos) {
		List<StoreReqAppVO> updateList = new ArrayList();

		for (StoreReqAppVO vo : vos) {
			if (2 != vo.getHVO().getStatus()) {
				updateList.add(vo);
			}
		}

		if (updateList.size() > 0) {
			return (StoreReqAppVO[]) updateList
					.toArray(new StoreReqAppVO[updateList.size()]);
		}
		return null;
	}

	public static String getCurrentYear() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		Date date = new Date();
		return sdf.format(date);
	}
}
