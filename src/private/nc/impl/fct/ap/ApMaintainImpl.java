package nc.impl.fct.ap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nc.bs.erm.util.ErBudgetUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.trade.business.HYPubBO;
import nc.impl.fct.ap.action.ApDeleteAction;
import nc.impl.fct.ap.action.ApInsertAction;
import nc.impl.fct.ap.action.ApUpdateAction;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.BillQuery;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.itf.tb.control.IBudgetControl;
import nc.itf.tb.control.ILinkQuery;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MatterAppYsControlVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.fct.ap.entity.AggCtApVO;
import nc.vo.fct.ap.entity.CtApBVO;
import nc.vo.fct.ap.entity.CtApVO;
import nc.vo.fct.uitl.ValueUtil;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.pub.SqlBuilder;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;
import nc.vo.tb.control.DataRuleVO;
import nc.vo.tb.obj.NtbParamVO;

// 付款合同保存校验
@SuppressWarnings({ "restriction", "unchecked", "rawtypes" })
public class ApMaintainImpl implements nc.itf.fct.ap.IApMaintain {
	private HYPubBO hyPubBO;

	public HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	public void setHyPubBO(HYPubBO hyPubBO) {
		this.hyPubBO = hyPubBO;
	}

	public ApMaintainImpl() {
	}

	public String[] queryBillPksByWhere(IQueryScheme queryScheme) {
		AggCtApVO[] bills = null;
		try {
			QuerySchemeProcessor qrySchemeProcessor = new QuerySchemeProcessor(
					queryScheme);

			String headTableName = qrySchemeProcessor.getMainTableAlias();
			qrySchemeProcessor.appendWhere(" and " + headTableName
					+ ".blatest= 'Y' ");

			qrySchemeProcessor.appendCurrentGroup();

			qrySchemeProcessor.appendFuncPermissionOrgSql();

			BillLazyQuery<AggCtApVO> qry = new BillLazyQuery(AggCtApVO.class);

			bills = (AggCtApVO[]) qry.query(queryScheme, null);
		} catch (Exception e) {
			nc.vo.fipub.exception.ExceptionHandler.handleRuntimeException(e);
		}
		if ((bills != null) && (bills.length > 0)) {
			String[] pks = new String[bills.length];
			int i = 0;
			for (AggCtApVO vo : bills) {
				pks[i] = vo.getParentVO().getPrimaryKey();
				i++;
			}
			return pks;
		}
		return null;
	}

	public void deleteAp(AggCtApVO[] bills) throws BusinessException {
		try {
			new ApDeleteAction().delete(bills);
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
		}
	}

	public AggCtApVO[] maintainQuery(IQueryScheme queryScheme)
			throws BusinessException {
		AggCtApVO[] bills = null;
		try {
			QuerySchemeProcessor qrySchemeProcessor = new QuerySchemeProcessor(
					queryScheme);

			String headTableName = qrySchemeProcessor.getMainTableAlias();
			qrySchemeProcessor.appendWhere(" and " + headTableName
					+ ".blatest= 'Y' ");

			qrySchemeProcessor.appendCurrentGroup();

			qrySchemeProcessor.appendFuncPermissionOrgSql();

			SqlBuilder order = new SqlBuilder();
			order.append("order by ");
			order.append(headTableName);
			order.append(".vbillcode");

			BillLazyQuery<AggCtApVO> qry = new BillLazyQuery(AggCtApVO.class);

			bills = (AggCtApVO[]) qry.query(queryScheme, order.toString());
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return bills;
	}

	public AggCtApVO[] queryCtApVoByIds(String[] ids) throws BusinessException {
		if (ValueUtil.isEmpty(ids)) {
			return null;
		}
		AggCtApVO[] bills = null;
		try {
			DataAccessUtils utils = new DataAccessUtils();

			IRowSet rowset = utils.query(createWholeSql(ids));
			String[] cbillids = rowset.toOneDimensionStringArray();
			if (ValueUtil.isEmpty(cbillids)) {
				return null;
			}
			BillQuery<AggCtApVO> queryVO = new BillQuery(AggCtApVO.class);
			bills = (AggCtApVO[]) queryVO.query(cbillids);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}

		return bills;
	}

	public AggCtApVO[] save(AggCtApVO[] bills) throws BusinessException {
		try {
			CtApVO ctApVO = bills[0].getParentVO();
			// 保存校验
			// beforeCheck(bills[0]);
			if (null != ctApVO.getPk_fct_ap()) {
				return new ApUpdateAction().update(bills);
			}
			return new ApInsertAction().insert(bills);
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
		}
		return null;
	}

	// 付款合同保存校验
	private void beforeCheck(AggCtApVO aggCtApVO) throws Exception,
			BusinessException {
		// TODO Auto-generated method stub
		CtApVO ctApVO = aggCtApVO.getParentVO();
		Object conforg = (Object) getHyPubBO().findColValue(
				"bd_defdoc",
				"code",
				"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
						+ "FROM BD_DEFDOCLIST WHERE CODE = 'HTSAVEORG')"
						+ " and code = '" + ctApVO.getPk_org() + "'");
		Object fkjylx = (Object) getHyPubBO().findColValue(
				"bd_defdoc",
				"code",
				"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
						+ "FROM BD_DEFDOCLIST WHERE CODE = 'FKHTSAVEJYLX')"
						+ " and code = '" + ctApVO.getVtrantypecode() + "'");
		// ctApVO.get
		if (conforg != null && fkjylx != null) {		
			CtApBVO[] bvos = aggCtApVO.getCtApBVO();
			Set<String> szxms = new HashSet<>();
			for (CtApBVO bvo : bvos) {
				int status = bvo.getStatus();
				if (status != VOStatus.DELETED) {
					szxms.add(bvo.getInoutcome());// 收支项目(预算类别)
				}
			}
			for (String szxm : szxms) {
				double htmoney = new Double(0);// 当前收支项目的合同金额合计值
				for (int i = 0; i < bvos.length; i++) {
					CtApBVO ctApBVO = bvos[i];
					int status = ctApBVO.getStatus();
					if (status != VOStatus.DELETED) {
						if (szxm.equals(ctApBVO.getInoutcome())) {
							htmoney += ctApBVO.getNtaxmny().toDouble();
						}
					}
				}
				// 费用申请单
				AggMatterAppVO selectvo = (AggMatterAppVO) ((IErmMatterAppBillQuery) NCLocator
						.getInstance().lookup(IErmMatterAppBillQuery.class))
						.queryBillByPK("0001A1100000001L1VY4").clone();
				MatterAppVO hvo = selectvo.getParentVO();
				hvo.setPk_group(ctApVO.getPk_group());// 集团
				hvo.setPk_org(ctApVO.getPk_org());// 组织
				hvo.setApply_dept(ctApVO.getVdef10());// 申请部门->代控部门
				selectvo.setParentVO(hvo);
				// 费用申请单明细
				MtAppDetailVO[] fymxvos = selectvo.getChildrenVO();
				MtAppDetailVO fymxvo = fymxvos[0];
				fymxvo.setPk_iobsclass(szxm);// 收支项目->预算类别
				fymxvos[0] = fymxvo;
				selectvo.setChildrenVO(fymxvos);
				// 判断
				String actionCode = "SAVE";
				List<MatterAppYsControlVO> items = new ArrayList();

				DataRuleVO[] ruleVos = ((IBudgetControl) NCLocator
						.getInstance().lookup(IBudgetControl.class))
						.queryControlTactics(selectvo.getParentVO()
								.getPk_tradetype(), actionCode, false);
				YsControlVO[] controlVos = null;
				if ((ruleVos != null) && (ruleVos.length > 0)) {
					MatterAppVO headvo = selectvo.getParentVO();
					CircularlyAccessibleValueObject[] dtailvos = selectvo
							.getChildrenVO();

					if (dtailvos != null) {
						for (int j = 0; j < dtailvos.length; j++) {
							MatterAppYsControlVO controlVo = new MatterAppYsControlVO(
									headvo, (MtAppDetailVO) dtailvos[j]);
							items.add(controlVo);
						}
					}
					controlVos = ErBudgetUtil.getCtrlVOs((IFYControl[]) items
							.toArray(new MatterAppYsControlVO[0]), true,
							ruleVos);
				}
				if ((controlVos == null) || (controlVos.length == 0)) {
					throw new BusinessException(NCLangRes4VoTransl
							.getNCLangRes().getStrByID("201212_0",
									"0201212-0015"));
				}
				NtbParamVO[] vos = ((ILinkQuery) NCLocator.getInstance()
						.lookup(ILinkQuery.class.getName()))
						.getLinkDatas(controlVos);
				if (vos != null) {
					NtbParamVO ntbParamVO = vos[0];
					double kzje = ntbParamVO.getCtrlData().toDouble();// 控制金额
					if (htmoney > kzje) {
						String yslbstr = (String) getHyPubBO().findColValue(
								"bd_inoutbusiclass",
								"name",
								"nvl(dr,0) = 0 and pk_inoutbusiclass ='" + szxm
										+ "'");
						throw new BusinessException("收支项目[" + yslbstr + "]合计值："
								+ String.format("%.2f", htmoney) + "，超出预算金额："
								+ String.format("%.2f", kzje) + "!");
					}
				}
			}
		}
	}

	private String createWholeSql(String[] ids) {
		SqlBuilder sql = new SqlBuilder();
		sql.append("select distinct fct_ap.pk_fct_ap from fct_ap where ");
		sql.append(" fct_ap.pk_fct_ap", ids);
		sql.append(" and fct_ap.dr = 0 ");
		sql.append(" and fct_ap.blatest = 'Y' ");
		return sql.toString();
	}
}
