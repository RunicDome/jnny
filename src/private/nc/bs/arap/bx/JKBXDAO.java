package nc.bs.arap.bx;

import java.sql.SQLException;

import java.util.ArrayList;

import java.util.Collection;

import java.util.HashMap;

import java.util.List;

import java.util.Map;

import java.util.StringTokenizer;

import java.util.Vector;

import javax.naming.NamingException;

import nc.bs.dao.BaseDAO;

import nc.bs.dao.DAOException;

import nc.bs.er.pub.IRSChecker;

import nc.bs.er.pub.PubDAO;

import nc.bs.er.pub.PubMethods;

import nc.bs.er.util.SqlUtils;

import nc.bs.framework.common.InvocationInfoProxy;

import nc.bs.framework.common.NCLocator;

import nc.bs.pub.pf.CheckStatusCallbackContext;

import nc.bs.pub.pf.ICheckStatusCallback;
import nc.bs.trade.business.HYPubBO;

import nc.itf.org.IDeptQryService;

import nc.itf.uap.IUAPQueryBS;

import nc.jdbc.framework.processor.ColumnProcessor;

import nc.pubitf.org.IOrgUnitPubService;

import nc.pubitf.rbac.IDataPermissionPubService;

import nc.pubitf.rbac.IRolePubService;

import nc.uap.rbac.core.dataperm.DataPermConfig;

import nc.uap.rbac.core.dataperm.SpecialPermissionConfig;

import nc.vo.ep.bx.BXBusItemVO;

import nc.vo.ep.bx.BXHeaderVO;

import nc.vo.ep.bx.BxcontrastVO;

import nc.vo.ep.bx.JKBXHeaderVO;

import nc.vo.ep.bx.JKBXVO;

import nc.vo.ep.bx.JKVO;

import nc.vo.ep.bx.JsConstrasVO;

import nc.vo.ep.bx.VOFactory;

import nc.vo.ep.dj.DjCondVO;

import nc.vo.er.util.StringUtils;

import nc.vo.erm.costshare.CShareDetailVO;

import nc.vo.erm.costshare.CostShareVO;

import nc.vo.erm.mapping.Er_jkbx_initVOMeta;

import nc.vo.erm.mapping.Er_jsconstrasVOMeta;

import nc.vo.erm.mapping.IBXArapMappingMeta;

import nc.vo.fipub.billcode.FinanceBillCodeInfo;

import nc.vo.fipub.billcode.FinanceBillCodeUtils;

import nc.vo.fipub.exception.ExceptionHandler;

import nc.vo.fipub.mapping.IArapMappingMeta;

import nc.vo.ml.AbstractNCLangRes;

import nc.vo.ml.NCLangRes4VoTransl;

import nc.vo.org.DeptVO;

import nc.vo.org.OrgManagerVO;

import nc.vo.org.OrgVO;

import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.VOStatus;

import nc.vo.pub.BusinessException;

import nc.vo.pub.CircularlyAccessibleValueObject;

import nc.vo.pub.SuperVO;

import nc.vo.pub.lang.UFDateTime;

import nc.vo.pub.lang.UFDouble;

import nc.vo.uap.rbac.role.RoleVO;

// ERM默认银行
@SuppressWarnings({ "deprecation", "unused", "unchecked", "rawtypes"})
public class JKBXDAO extends BXSuperDAO implements ICheckStatusCallback {
	public String getPkField() {
		return new BXHeaderVO().getPKFieldName();
	}

	public JKBXDAO() throws NamingException {
	}

	private void getBillNo(JKBXVO bxvo) throws BusinessException {
		JKBXHeaderVO parent = bxvo.getParentVO();
		FinanceBillCodeInfo info = new FinanceBillCodeInfo("djdl", "djbh",
				"pk_group", "pk_org", parent.getTableName(), "djlxbm",
				parent.getPk_billtype());

		FinanceBillCodeUtils util = new FinanceBillCodeUtils(info);
		util.createBillCode(new AggregatedValueObject[] { bxvo });
	}

	public JKBXVO[] save(JKBXVO[] vos) throws SQLException, BusinessException {
		List<BxcontrastVO> contrasts = new ArrayList();
		List<JKBXHeaderVO> parentList = new ArrayList(vos.length);
		for (int i = 0; i < vos.length; i++) {
			JKBXVO bxvo = vos[i];
			BXBusItemVO[] items = bxvo.getBxBusItemVOS();
			JKBXHeaderVO parentVO = bxvo.getParentVO();
			// 默认付款银行账户
			if (parentVO != null
					&& ("264X".equals(parentVO.getPk_billtype()) || "263X"
							.equals(parentVO.getPk_billtype()))
					&& parentVO.getFkyhzh() == null) {
				String fkyhzh = (String) new HYPubBO().findColValue(
						"bd_bankaccbas",
						"pk_bankaccbas",
						"nvl(dr,0) = 0 and controlorg ='"
								+ parentVO.getPk_org() + "' and def10 = 'Y'");
				if (fkyhzh != null) {
					String fkyhzhzh = (String) new HYPubBO()
							.findColValue("bd_bankaccsub", "pk_bankaccsub",
									"nvl(dr,0) = 0 and PK_BANKACCBAS ='"
											+ fkyhzh + "'");
					if (fkyhzhzh != null) {
						parentVO.setFkyhzh(fkyhzhzh);
						parentVO.setStatus(VOStatus.NEW);
					}
				}
			}
			parentList.add(parentVO);

			if (!parentVO.isInit()) {
				getBillNo(bxvo);
				String billcode = bxvo.getParentVO().getDjbh();
				if (billcode.length() > 30) {
					String errMsg = NCLangRes4VoTransl.getNCLangRes()
							.getStrByID(
									"2011v61013_0",
									"02011v61013-0024",
									null,
									new String[] {
											String.valueOf(billcode.length()),
											String.valueOf(30) });

					ExceptionHandler.handleException(getClass(),
							new BusinessException(errMsg));
				}
			}

			if (parentVO.getPk_jkbx() != null) {
				this.baseDao.insertVOWithPK(parentVO);
			} else {
				this.baseDao.insertVO(parentVO);
			}

			String bxpk = parentVO.getPk_jkbx();

			if (items != null) {
				for (int j = 0; j < items.length; j++) {
					items[j].setPk_jkbx(bxpk);
					items[j].setRowno(Integer.valueOf(j + 1));

					dealBusitemDefitem(items[j]);
				}
			}

			Map<BXBusItemVO, String> cmpMap = new HashMap();
			if (items != null) {
				for (BXBusItemVO item : items) {
					cmpMap.put(
							item,
							item.getPrimaryKey() == null ? "" : item
									.getPrimaryKey());
				}
				for (int j = 0; j < items.length; j++) {
					items[j].setPk_jkbx(bxpk);
					items[j].setPk_busitem(null);
				}
			}
			if ((items != null) && (items.length > 0)) {
				this.baseDao.insertVOArray(items);
			}

			Map<String, String> idMap = new HashMap();
			if (items != null) {
				for (BXBusItemVO item : items) {
					idMap.put(
							((String) cmpMap.get(item)).length() == 0 ? item
									.getPrimaryKey() : (String) cmpMap
									.get(item), item.getPrimaryKey());
				}
			} else {
				idMap.put("ER_SET_HTEMPK_", bxpk);
			}

			if ((bxvo.getContrastVO() != null)
					&& (bxvo.getContrastVO().length != 0)) {
				List<BxcontrastVO> newContrasts = ErContrastUtil
						.dealContrastForNew(parentVO, bxvo.getContrastVO(),
								items);
				bxvo.setContrastVO((BxcontrastVO[]) newContrasts
						.toArray(new BxcontrastVO[0]));
				contrasts.addAll(newContrasts);
			}

			bxvo.setCmpIdMap(idMap);
		}

		new ContrastBO().saveContrast(contrasts, null);

		new BxVerifyAccruedBillBO().saveVerifyVOs(vos);

		addTsToBXVOs((SuperVO[]) parentList.toArray(new JKBXHeaderVO[parentList
				.size()]));

		return vos;
	}

	private void dealBusitemDefitem(BXBusItemVO busItemVO) {
		String[] attributeNames = busItemVO.getAttributeNames();
		for (String attr : attributeNames) {
			if ((attr.indexOf("defitem") == 0)
					&& (busItemVO.getAttributeValue(attr) != null)
					&& ((busItemVO.getAttributeValue(attr) instanceof UFDouble))) {
				busItemVO.setAttributeValue(attr,
						busItemVO.getAttributeValue(attr).toString());
			}
		}
	}

	public void delete(JKBXHeaderVO[] vos) throws BusinessException {
		this.baseDao.deleteVOArray(vos);
	}

	public JKBXVO[] update(JKBXVO[] vos) throws SQLException, BusinessException {
		List<BxcontrastVO> contrasts = new ArrayList();

		Vector<JKBXHeaderVO> headVos = new Vector();

		Vector<JKBXHeaderVO> bxdvos = new Vector();

		List<JKBXHeaderVO> voList = new ArrayList();

		for (int i = 0; i < vos.length; i++) {
			JKBXVO bxvo = vos[i];

			// 默认付款银行账户
			JKBXHeaderVO parentVO = bxvo.getParentVO();
			if (parentVO != null
					&& ("264X".equals(parentVO.getPk_billtype()) || "263X"
							.equals(parentVO.getPk_billtype()))
					&& parentVO.getFkyhzh() == null) {
				String fkyhzh = (String) new HYPubBO().findColValue(
						"bd_bankaccbas",
						"pk_bankaccbas",
						"nvl(dr,0) = 0 and controlorg ='"
								+ parentVO.getPk_org() + "' and def10 = 'Y'");
				if (fkyhzh != null) {
					String fkyhzhzh = (String) new HYPubBO()
							.findColValue("bd_bankaccsub", "pk_bankaccsub",
									"nvl(dr,0) = 0 and pk_bankaccbas ='"
											+ fkyhzh + "'");
					if (fkyhzhzh != null) {
						parentVO.setFkyhzh(fkyhzhzh);
						parentVO.setStatus(VOStatus.UPDATED);
					}
				}
			}
			BXBusItemVO[] childrenVO = bxvo.getBxBusItemVOS();
			JKBXVO bxoldvo = bxvo.getBxoldvo();
			BXBusItemVO[] childrenVO_old = null;
			if (bxoldvo != null) {
				childrenVO_old = bxoldvo.getBxBusItemVOS();
			}
			Map<String, BXBusItemVO> oldBusiMap = new HashMap();

			if (childrenVO_old != null) {
				for (BXBusItemVO vo : childrenVO_old) {
					oldBusiMap.put(vo.getPrimaryKey(), vo);
				}
			}

			Vector<BXBusItemVO> newBusItem = new Vector();
			BXBusItemVO[] delBusItem = null;
			Vector<BXBusItemVO> updBusItem = new Vector();

			if (childrenVO != null) {
				int rowno = 0;
				for (BXBusItemVO child : childrenVO) {
					rowno++;

					child.setRowno(Integer.valueOf(rowno));

					dealBusitemDefitem(child);

					if ((StringUtils.isNullWithTrim(child.getPrimaryKey()))
							|| (child.getPrimaryKey()
									.startsWith("ER_SET_TEMPK_"))) {

						child.setPk_jkbx(bxvo.getParentVO().getPk_jkbx());
						newBusItem.add(child);
					} else if (oldBusiMap.containsKey(child.getPrimaryKey())) {
						updBusItem.add(child);
						oldBusiMap.remove(child.getPrimaryKey());
					}
				}
			}

			delBusItem = (BXBusItemVO[]) oldBusiMap.values().toArray(
					new BXBusItemVO[0]);

			Map<BXBusItemVO, String> cmpMap = new HashMap();
			if (childrenVO != null) {
				for (BXBusItemVO item : childrenVO) {
					cmpMap.put(
							item,
							item.getPrimaryKey() == null ? "" : item
									.getPrimaryKey());
				}
			}

			if (newBusItem != null)
				this.baseDao.insertVOArray((SuperVO[]) newBusItem
						.toArray(new BXBusItemVO[0]));
			if (updBusItem != null)
				this.baseDao.updateVOArray((SuperVO[]) updBusItem
						.toArray(new BXBusItemVO[0]));
			if (delBusItem != null) {
				this.baseDao.deleteVOArray(delBusItem);
			}
			headVos.add(bxvo.getParentVO());

			if (bxvo.getParentVO().getDjdl().equals("bx")) {
				bxdvos.add(bxvo.getParentVO());
				if ((bxvo.getContrastVO() != null)
						&& (bxvo.getContrastVO().length != 0)) {
					List<BxcontrastVO> newContrasts = ErContrastUtil
							.dealContrastForNew(bxvo.getParentVO(),
									bxvo.getContrastVO(), childrenVO);

					bxvo.setContrastVO((BxcontrastVO[]) newContrasts
							.toArray(new BxcontrastVO[0]));
					contrasts.addAll(newContrasts);
				}

				if ((!bxvo.isContrastUpdate())
						&& (bxvo.getParentVO()
								.getCjkybje()
								.compareTo(
										bxvo.getBxoldvo().getParentVO()
												.getCjkybje()) != 0)) {
					throw new BusinessException(NCLangRes4VoTransl
							.getNCLangRes()
							.getStrByID("2011", "UPP2011-000389"));
				}
			}

			Map<String, String> idMap = new HashMap();
			if (childrenVO != null) {
				for (BXBusItemVO item : childrenVO) {
					idMap.put(
							((String) cmpMap.get(item)).length() == 0 ? item
									.getPrimaryKey() : (String) cmpMap
									.get(item), item.getPrimaryKey());
				}
			} else {
				idMap.put(bxvo.getParentVO().getPk_jkbx(), bxvo.getParentVO()
						.getPk_jkbx());
			}

			voList.add(bxvo.getParentVO());

			bxvo.setCmpIdMap(idMap);
		}

		this.baseDao.updateVOArray((SuperVO[]) voList
				.toArray(new JKBXHeaderVO[voList.size()]));

		JKBXHeaderVO[] toArray = (JKBXHeaderVO[]) headVos
				.toArray(new JKBXHeaderVO[0]);

		new ContrastBO().saveContrast(contrasts, bxdvos);

		new BxVerifyAccruedBillBO().saveVerifyVOs(vos);

		addTsToBXVOs(toArray);

		return vos;
	}

	public List<JKBXHeaderVO> queryHeadersByWhereSql(String sql, String djdl)
			throws DAOException {
		PubDAO pudao = new PubDAO();

		List<JKBXHeaderVO> ret = new ArrayList();

		if ((djdl == null) || ((!djdl.equals("bx")) && (!djdl.equals("jk")))) {
			djdl = "bx";
			String sqlNew = getJKBXHeadSQL(BXVOUtil.getMetaData(djdl)) + sql;
			ret.addAll((List) pudao.queryVOsBySql(BXVOUtil.getMetaData(djdl)
					.getMetaClass(), BXVOUtil.getMetaData(djdl), sqlNew));

			djdl = "jk";
			sqlNew = getJKBXHeadSQL(BXVOUtil.getMetaData(djdl)) + sql;
			ret.addAll((List) pudao.queryVOsBySql(BXVOUtil.getMetaData(djdl)
					.getMetaClass(), BXVOUtil.getMetaData(djdl), sqlNew));
		} else {
			sql = getJKBXHeadSQL(BXVOUtil.getMetaData(djdl)) + sql;

			ret = (List) pudao.queryVOsBySql(BXVOUtil.getMetaData(djdl)
					.getMetaClass(), BXVOUtil.getMetaData(djdl), sql);
		}

		return ret;
	}

	public List<JKBXHeaderVO> queryHeaders(Integer start, Integer count,
			DjCondVO condVO) throws DAOException {
		PubDAO pudao = new PubDAO();

		List<JKBXHeaderVO> ret = new ArrayList();

		IArapMappingMeta meta = null;

		if (condVO.isInit) {
			meta = new Er_jkbx_initVOMeta();
			String sql = genBxSql(condVO, meta);
			ret = (List) pudao.queryVOsBySql(BXVOUtil.getMetaData(condVO.djdl)
					.getMetaClass(), meta, sql, start.intValue(), count
					.intValue());
		} else if ((condVO.djdl == null) || (condVO.djdl.trim().length() == 0)) {
			condVO.djdl = "bx";
			String sql = genBxSql(condVO);
			meta = BXVOUtil.getMetaData(condVO.djdl);
			ret.addAll((List) pudao.queryVOsBySql(
					BXVOUtil.getMetaData(condVO.djdl).getMetaClass(), meta,
					sql, start.intValue(), count.intValue(),
					getRsChecker(condVO)));

			if (ret.size() == count.intValue()) {
				return ret;
			}

			int bxcount = querySize(condVO);
			condVO.djdl = "jk";
			sql = genBxSql(condVO);
			meta = BXVOUtil.getMetaData(condVO.djdl);
			ret.addAll((List) pudao.queryVOsBySql(
					BXVOUtil.getMetaData(condVO.djdl).getMetaClass(), meta,
					sql, start.intValue() - bxcount, count.intValue(),
					getRsChecker(condVO)));
		} else {
			String sql = genBxSql(condVO);
			ret = (List) pudao.queryVOsBySql(BXVOUtil.getMetaData(condVO.djdl)
					.getMetaClass(), BXVOUtil.getMetaData(condVO.djdl), sql,
					start.intValue(), count.intValue(), getRsChecker(condVO));
		}

		return ret;
	}

	private IRSChecker getRsChecker(DjCondVO condVO) {
		if ((condVO.isLinkPz) || (condVO.VoucherFlags != null)) {
			return new VoucherRsChecker(condVO.isLinkPz, condVO.VoucherFlags);
		}
		return null;
	}

	public String genBxSql(DjCondVO condVO, IArapMappingMeta meta)
			throws DAOException {
		String sql = null;

		sql = getJKBXSelectSQL(meta);
		try {
			sql = sql + getWhereSql(condVO, true);
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
		return sql;
	}

	public String genBxSql(DjCondVO condVO) throws DAOException {
		String sql = null;

		sql = getJKBXSelectSQL(BXVOUtil.getMetaData(condVO.djdl));
		try {
			sql = sql + getWhereSql(condVO, true);
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
		return sql;
	}

	public static String getWhereSql(DjCondVO condVO, boolean needOrder)
			throws Exception {
		StringBuffer sb = new StringBuffer();

		String whereSql = condVO.defWhereSQL == null ? "" : condVO.defWhereSQL;

		StringTokenizer st = new StringTokenizer(whereSql, "@");

		if (st.hasMoreTokens()) {
			sb.append(st.nextToken());

			while (st.hasMoreTokens()) {
				sb.append("  fb.tablecode = '");
				sb.append(st.nextToken());
				sb.append("' and fb.");
				sb.append(st.nextToken());
				sb.append(st.nextToken());
			}
		}
		String whereClause = new PubMethods().getBillQuerySubSql_Jkbx(
				condVO.m_NorCondVos, sb.toString());

		String whereStr = null;

		if (condVO.isInit) {
			if ((condVO.pk_org != null) && (condVO.pk_org.length != 0)
					&& (condVO.pk_org[0] != null)
					&& (condVO.pk_org[0].length() != 0)) {
				whereStr = " (zb.pk_org='" + condVO.pk_org[0] + "' ) ";
			}

			if ((condVO.pk_group != null) && (condVO.pk_group.length != 0)
					&& (condVO.pk_group[0] != null)
					&& (condVO.pk_group[0].length() != 0)) {
				if (whereStr != null) {
					whereStr = whereStr + " and ";
					whereStr = whereStr + " (zb.pk_group='"
							+ condVO.pk_group[0] + "' ) ";
				} else {
					whereStr = " (zb.pk_group='" + condVO.pk_group[0] + "' ) ";
				}

			}
		} else if ((condVO.nodecode != null)
				&& ("20110BMLB".equalsIgnoreCase(condVO.nodecode))) {

			whereStr = getDirectionPerm(condVO);

			whereClause = " left join pub_workflownote wf on zb.pk_jkbx=wf.billid and wf.checkman= '"
					+ condVO.operator
					+ "' and wf.dr=0  and wf.actiontype <> 'MAKEBILL'  "
					+ whereClause;
		} else {
			IRolePubService roleQuery = (IRolePubService) NCLocator
					.getInstance().lookup(IRolePubService.class.getName());
			RoleVO[] roleVOs = new RoleVO[0];
			String user = condVO.operator;
			try {
				roleVOs = roleQuery.queryRoleByUserID(user, null);
			} catch (BusinessException e1) {
				ExceptionHandler.consume(e1);
			}
			List<String> roles = new ArrayList();

			for (RoleVO roleVO : roleVOs) {
				roles.add(roleVO.getPk_role());
			}

			String nodecode = condVO.nodecode;
			if ((!"20110BO".equals(nodecode))
					&& (!"20110BQLB".equals(nodecode))) {
				whereStr = " (zb.jkbxr='"
						+ condVO.psndoc
						+ "' or zb.operator in(select pk_user from er_indauthorize  where "
						+ SqlUtils.getInStr("pk_roler",
								(String[]) roles.toArray(new String[0]),
								new boolean[0]) + ") or zb.operator='"
						+ condVO.operator + "' )";
			}
		}

		if (whereStr != null) {
			if (StringUtils.isNullWithTrim(whereClause)) {
				whereClause = " where " + whereStr;
			} else {
				whereClause = whereClause + " and " + whereStr;
			}
		}

		if ((condVO.getDataPowerSql() != null)
				&& (condVO.getDataPowerSql().trim().length() > 0)) {
			whereClause = whereClause + " and (" + condVO.getDataPowerSql()
					+ ")";
		}

		if (whereClause != null) {
			whereClause = whereClause + " and zb.pk_group='"
					+ InvocationInfoProxy.getInstance().getGroupId() + "'";
		}

		if (needOrder) {
			whereClause = whereClause + " order by zb.djrq desc,zb.djbh desc ";
		}
		return whereClause;
	}

	private static String getDirectionPerm(DjCondVO condVO)
			throws BusinessException, SQLException {
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();

		String cuserid = condVO.operator;
		String djdl = condVO.djdl;
		String resourceCode = null;
		if ((djdl == null) || (djdl.equals("bx"))) {
			resourceCode = "ermexpenseservice";
		} else if (djdl.equals("jk")) {
			resourceCode = "ermloanservice";
		}

		String whereStr = " (zb.approver='" + condVO.operator
				+ "' or wf.billid is not null ) ";

		if ((djdl != null) && (resourceCode != null)) {
			DataPermConfig config = ((IDataPermissionPubService) NCLocator
					.getInstance().lookup(IDataPermissionPubService.class))
					.queryDataPermConfig(cuserid, resourceCode, pk_group, true);

			if (config != null) {
				boolean isStartDirectorPerm = config
						.getSpecialPermissionConfig().isEnableDirectorPerm();
				if (isStartDirectorPerm) {
					String where = "";
					if (condVO.psndoc != null) {
						where = getDirectorWherePart(condVO, pk_group);
					}
					whereStr = "(zb.approver='" + condVO.operator
							+ "' or wf.billid is not null  " + where + ") ";
				}
			}
		}
		return whereStr;
	}

	private static String getDirectorWherePart(DjCondVO condVO, String pk_group)
			throws BusinessException, SQLException {
		String whereStr = "";

		DeptVO[] deptVOs = ((IDeptQryService) NCLocator.getInstance().lookup(
				IDeptQryService.class)).queryAllDeptVOSByGroupID(pk_group);
		ArrayList<String> deptFields = new ArrayList();

		String whereSql = "pk_group='" + pk_group + "'";

		Collection rs = ((IUAPQueryBS) NCLocator.getInstance().lookup(
				IUAPQueryBS.class)).retrieveByClause(OrgManagerVO.class,
				whereSql);
		OrgManagerVO[] orgMgrVOs = (OrgManagerVO[]) rs
				.toArray(new OrgManagerVO[0]);

		HashMap<String, OrgManagerVO> map = new HashMap();
		if ((orgMgrVOs != null) && (orgMgrVOs.length > 0)) {
			for (int i = 0; i < orgMgrVOs.length; i++) {
				String key = orgMgrVOs[i].getPk_org();
				if (!map.containsKey(key)) {
					map.put(key, orgMgrVOs[i]);
				}
			}
		}
		for (DeptVO vo : deptVOs) {
			String key = vo.getPk_dept();
			if ((map.get(key) != null)
					&& (((OrgManagerVO) map.get(key)).getCuserid() != null)
					&& (((OrgManagerVO) map.get(key)).getCuserid()
							.equals(condVO.operator))) {
				deptFields.add(key);
			}
		}

		OrgVO[] orgVOs = ((IOrgUnitPubService) NCLocator.getInstance().lookup(
				IOrgUnitPubService.class)).getAllOrgVOSByGroupIDAndOrgTypes(
				pk_group, new String[] { "FINANCEORGTYPE000000" });

		ArrayList<String> orgFields = new ArrayList();

		for (OrgVO vo : orgVOs) {
			String key = vo.getPk_org();
			if ((map.get(key) != null)
					&& (((OrgManagerVO) map.get(key)).getCuserid() != null)
					&& (((OrgManagerVO) map.get(key)).getCuserid()
							.equals(condVO.operator))) {
				orgFields.add(key);
			}
		}

		whereStr = " or  "
				+ SqlUtils.getInStr("zb.deptid",
						(String[]) deptFields.toArray(new String[0]),
						new boolean[] { true })
				+ " or "
				+ SqlUtils.getInStr("zb.pk_org",
						(String[]) orgFields.toArray(new String[0]),
						new boolean[] { true });

		return whereStr;
	}

	public String getCountSelectSQL(String tablename) {
		StringBuffer buf = new StringBuffer(
				"SELECT count(distinct zb.pk_jkbx) ");
		return buf.toString() + " from " + tablename
				+ " zb left outer join er_busitem fb on zb.pk_jkbx=fb.pk_jkbx ";
	}

	public String getJKBXSelectSQL(IArapMappingMeta meta) {
		StringBuffer buf = new StringBuffer("SELECT DISTINCT ");
		int i = 0;
		for (int size = meta.getColumns().length; i < size; i++) {
			if (i != 0) {
				buf.append(",");
			}
			buf.append(" zb.").append(meta.getColumns()[i]);
		}
		return buf.toString() + " from " + meta.getTableName()
				+ " zb left outer join er_busitem fb on zb.pk_jkbx=fb.pk_jkbx ";
	}

	public String getJKBXHeadSQL(IArapMappingMeta meta) {
		StringBuffer buf = new StringBuffer("SELECT DISTINCT ");
		int i = 0;
		for (int size = meta.getColumns().length; i < size; i++) {
			if (i != 0) {
				buf.append(",");
			}
			buf.append(meta.getColumns()[i]);
		}
		return buf.toString() + " from " + meta.getTableName() + " zb ";
	}

	public boolean checkGoing(AggregatedValueObject vo, String ApproveId,
			String ApproveDate, String checkNote) throws Exception {
		new BXZbBO().compareTs(new JKBXVO[] { VOFactory
				.createVO((JKBXHeaderVO) vo.getParentVO()) });

		JKBXHeaderVO headerVO = (JKBXHeaderVO) vo.getParentVO();

		JKBXHeaderVO headerVO2 = (JKBXHeaderVO) headerVO.clone();

		headerVO2.setDjzt(Integer.valueOf(1));

		update(new JKBXHeaderVO[] { headerVO2 }, new String[] { "spzt",
				"approver", "shrq" });

		headerVO.setIsunAudit((headerVO.getDjzt().equals(Integer.valueOf(2)))
				|| (headerVO.getDjzt().equals(Integer.valueOf(3))));

		headerVO.setTs(headerVO2.getTs());
		headerVO.setSpzt(Integer.valueOf(2));
		headerVO.setDjzt(Integer.valueOf(1));
		headerVO.setApprover(ApproveId);
		headerVO.setShrq(new UFDateTime(ApproveDate));
		return false;
	}

	public boolean checkNoPass(AggregatedValueObject vo, String ApproveId,
			String ApproveDate, String checkNote) throws Exception {
		new BXZbBO().compareTs(new JKBXVO[] { VOFactory
				.createVO((JKBXHeaderVO) vo.getParentVO()) });

		JKBXHeaderVO headerVO = (JKBXHeaderVO) vo.getParentVO();

		if (headerVO.getDjzt().intValue() != 1) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("expensepub_0", "02011002-0070"));
		}

		JKBXHeaderVO headerVO2 = (JKBXHeaderVO) headerVO.clone();
		headerVO2.setSpzt(Integer.valueOf(0));
		headerVO2.setDjzt(Integer.valueOf(1));
		headerVO2.setApprover("");
		headerVO2.setShrq(new UFDateTime(ApproveDate));

		update(new JKBXHeaderVO[] { headerVO2 }, new String[] { "spzt",
				"approver", "shrq" });
		headerVO.setTs(headerVO2.getTs());
		headerVO.setSpzt(Integer.valueOf(0));
		headerVO.setDjzt(Integer.valueOf(1));
		headerVO.setApprover("");
		headerVO.setShrq(new UFDateTime(ApproveDate));
		return false;
	}

	public void callCheckStatus(CheckStatusCallbackContext cscc)
			throws BusinessException {
		JKBXVO billvo = (JKBXVO) cscc.getBillVo();

		if ((billvo instanceof JKVO)) {
			update(new JKBXHeaderVO[] { billvo.getParentVO() }, new String[] {
					"approver", "shrq", "spzt" });
		} else {
			update(new JKBXHeaderVO[] { billvo.getParentVO() }, new String[] {
					"approver", "shrq", "spzt", "flexible_flag" });
		}
	}

	public boolean checkPass(AggregatedValueObject vo, String ApproveId,
			String ApproveDate, String checkNote) throws Exception {
		JKBXHeaderVO headerVO = (JKBXHeaderVO) vo.getParentVO();

		if (headerVO.getDjzt().intValue() != 1) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("expensepub_0", "02011002-0070"));
		}

		headerVO.setApprover(ApproveId);
		headerVO.setShrq(new UFDateTime(ApproveDate));

		return false;
	}

	@Deprecated
	public void backGoing(AggregatedValueObject vo, String approveId,
			String approveDate, String backNote) throws Exception {
		new BXZbBO().compareTs(new JKBXVO[] { VOFactory
				.createVO((JKBXHeaderVO) vo.getParentVO()) });

		JKBXHeaderVO headerVO = (JKBXHeaderVO) vo.getParentVO();
		JKBXHeaderVO headerVO2 = (JKBXHeaderVO) headerVO.clone();
		headerVO2.setDjzt(Integer.valueOf(1));
		headerVO2.setSxbz(Integer.valueOf(0));
		headerVO2.setSpzt(Integer.valueOf(2));
		headerVO2.setApprover(approveId);
		headerVO2.setShrq(new UFDateTime(approveDate));

		update(new JKBXHeaderVO[] { headerVO2 }, new String[] { "spzt", "djzt",
				"sxbz", "approver", "shrq" });

		headerVO.setTs(headerVO2.getTs());
		headerVO.setSpzt(Integer.valueOf(2));
		headerVO.setApprover(approveId);
		headerVO.setShrq(new UFDateTime(approveDate));
	}

	public void backNoState(AggregatedValueObject vo, String approveId,
			String approveDate, String backNote) throws Exception {
		new BXZbBO().compareTs(new JKBXVO[] { VOFactory
				.createVO((JKBXHeaderVO) vo.getParentVO()) });

		JKBXHeaderVO headerVO = (JKBXHeaderVO) vo.getParentVO();
		JKBXHeaderVO headerVO2 = (JKBXHeaderVO) headerVO.clone();
		headerVO2.setApprover(null);
		headerVO2.setShrq(null);
		headerVO2.setSpzt(Integer.valueOf(-1));

		update(new JKBXHeaderVO[] { headerVO2 }, new String[] { "spzt",
				"approver", "shrq" });

		headerVO.setIsunAudit((headerVO.getDjzt().equals(Integer.valueOf(2)))
				|| (headerVO.getDjzt().equals(Integer.valueOf(3))));

		headerVO.setTs(headerVO2.getTs());
		headerVO.setApprover(null);
		headerVO.setSpzt(Integer.valueOf(-1));
	}

	public Collection<BxcontrastVO> retrieveContrastByClause(String sql)
			throws DAOException {
		return this.baseDao.retrieveByClause(BxcontrastVO.class, sql);
	}

	public Collection<JsConstrasVO> retrieveJsContrastByClause(String sql)
			throws DAOException {
		return this.baseDao.retrieveByClause(JsConstrasVO.class, sql);
	}

	public Collection<JsConstrasVO> queryJsContrastByWhereSql(
			String fromWhereSql) throws DAOException {
		PubDAO pubdao = new PubDAO();
		Er_jsconstrasVOMeta meta = new Er_jsconstrasVOMeta();
		String selectSQL = PubDAO.getSelectSQL(meta, fromWhereSql);
		return (List) pubdao.queryVOsBySql(JsConstrasVO.class, meta, selectSQL);
	}

	public int querySize(DjCondVO condVO) throws DAOException {
		String selectFromsql = "";
		String whereSql;
		try {
			whereSql = getWhereSql(condVO, false);
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

		if (condVO.isInit) {
			selectFromsql = getCountSelectSQL("er_jkbx_init");
			Object count = this.baseDao.executeQuery(selectFromsql + whereSql,
					new ColumnProcessor());
			return new Integer(count.toString()).intValue();
		}
		if ((condVO.djdl == null)
				|| ((!condVO.djdl.equals("bx")) && (!condVO.djdl.equals("jk")))) {
			condVO.djdl = "bx";
			selectFromsql = getCountSelectSQL("er_bxzb");
			try {
				whereSql = getWhereSql(condVO, false);
			} catch (Exception e) {
				throw new DAOException(e.getMessage(), e);
			}
			Object count1 = this.baseDao.executeQuery(selectFromsql + whereSql,
					new ColumnProcessor());
			condVO.djdl = "jk";
			selectFromsql = getCountSelectSQL("er_jkzb");
			try {
				whereSql = getWhereSql(condVO, false);
			} catch (Exception e) {
				throw new DAOException(e.getMessage(), e);
			}
			Object count2 = this.baseDao.executeQuery(selectFromsql + whereSql,
					new ColumnProcessor());
			return new Integer(count1.toString()).intValue()
					+ new Integer(count2.toString()).intValue();
		}
		if (condVO.djdl.equals("bx")) {
			selectFromsql = getCountSelectSQL("er_bxzb");
		} else {
			selectFromsql = getCountSelectSQL("er_jkzb");
		}
		Object count = this.baseDao.executeQuery(selectFromsql + whereSql,
				new ColumnProcessor());
		return new Integer(count.toString()).intValue();
	}

	public CircularlyAccessibleValueObject[] queryAllBodyData(String key)
			throws BusinessException {
		return queryAllBodyData(key, null);
	}

	public CircularlyAccessibleValueObject[] queryAllBodyData(String key,
			String whereString) throws BusinessException {
		if ((whereString == null) || (whereString.trim().length() == 0)) {
			whereString = "";
		} else {
			whereString = " and " + whereString;
		}
		return (CircularlyAccessibleValueObject[]) new BaseDAO()
				.retrieveByClause(
						BXBusItemVO.class,
						new BXBusItemVO().getParentPKFieldName() + "='" + key
								+ "' and dr=0 " + whereString).toArray(
						new BXBusItemVO[0]);
	}

	public Map<String, String> getTsByPrimaryKeys(String[] key,
			String tableName, String pk_field) throws DAOException,
			SQLException {
		List<JKBXHeaderVO> headers = queryHeadersByWhereSql(" where "
				+ SqlUtils.getInStr(pk_field, key, new boolean[0]),
				tableName.equals("er_bxzb") ? "bx" : "jk");

		Map<String, String> hashMap = new HashMap();
		if (headers != null) {
			for (JKBXHeaderVO head : headers) {
				hashMap.put(head.getPrimaryKey(), head.getTs().toString());
			}
		}
		return hashMap;
	}

	public Collection<CShareDetailVO> retrieveCShareVoByClause(String sql)
			throws DAOException {
		return this.baseDao.retrieveByClause(CShareDetailVO.class, sql);
	}

	public Collection<CostShareVO> retrieveCostShareVoByClause(String sql)
			throws DAOException {
		return this.baseDao.retrieveByClause(CostShareVO.class, sql);
	}

}