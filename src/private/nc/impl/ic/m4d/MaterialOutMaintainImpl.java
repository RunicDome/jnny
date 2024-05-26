package nc.impl.ic.m4d;

import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.ic.m4d.MaterialOutEquipCardProcess;
import nc.bs.trade.business.HYPubBO;
import nc.impl.ic.m4d.action.CancelSignAction;
import nc.impl.ic.m4d.action.DeleteAction;
import nc.impl.ic.m4d.action.InsertAction;
import nc.impl.ic.m4d.action.RatioOutSaveAction;
import nc.impl.ic.m4d.action.SignAction;
import nc.impl.ic.m4d.action.UpdateAction;
import nc.itf.ic.m4d.IMaterialOutMaintain;
import nc.itf.pbm.materialplan.pvt.IMaterialPlanCommonService;
import nc.ui.pcm.utils.GetDao;
import nc.uif.pub.exception.UifException;
import nc.vo.ic.m4d.entity.MaterialOutBodyVO;
import nc.vo.ic.m4d.entity.MaterialOutHeadVO;
import nc.vo.ic.m4d.entity.MaterialOutVO;
import nc.vo.org.OrgVO;
import nc.vo.pbm.materialplan.MaterialPlanBillVO;
import nc.vo.pcm.materialacc.MaterialaccHeadVO;
import nc.vo.pim.project.ProjectHeadVO;
import nc.vo.pmpub.projecttype.ProjectTypeHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import org.apache.commons.lang.StringUtils;

// 材料出库保存限制 XBX 2021-11-15
@SuppressWarnings("restriction")
public class MaterialOutMaintainImpl implements IMaterialOutMaintain {
	private HYPubBO hyPubBO;

	public HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	public MaterialOutVO[] cancelSign(MaterialOutVO[] bills)
			throws BusinessException {
		try {
			return new CancelSignAction().cancelSign(bills);
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
		}
		return new MaterialOutVO[0];
	}

	public void delete(MaterialOutVO[] bills) throws BusinessException {
		try {
			new DeleteAction().delete(bills);
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
		}
	}

	public MaterialOutVO[] insert(MaterialOutVO[] bills)
			throws BusinessException {
		try {
			// 保存前校验
			beforecheck(bills[0]);
			return new InsertAction().insert(bills);
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
		}
		return new MaterialOutVO[0];
	}

	public MaterialOutVO[] sign(MaterialOutVO[] bills) throws BusinessException {
		try {
			return new SignAction().sign(bills);
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
		}
		return new MaterialOutVO[0];
	}

	public MaterialOutVO[] ratioOutSave(MaterialOutVO[] bills, boolean isNum)
			throws BusinessException {
		try {
			return new RatioOutSaveAction().pushsave(bills, isNum);
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
		}
		return new MaterialOutVO[0];
	}

	public MaterialOutVO[] cancelGenerateEquipCard(MaterialOutVO[] bills)
			throws BusinessException {
		try {
			return (MaterialOutVO[]) new MaterialOutEquipCardProcess()
					.cancelGenerateEquipCard(bills);
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
		}
		return new MaterialOutVO[0];
	}

	public MaterialOutVO[] generateEquipCard(MaterialOutVO[] bills)
			throws BusinessException {
		try {
			return (MaterialOutVO[]) new MaterialOutEquipCardProcess()
					.generateEquipCard(bills);
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
		}
		return new MaterialOutVO[0];
	}

	public MaterialOutVO[] update(MaterialOutVO[] bills,
			MaterialOutVO[] originBills) throws BusinessException {
		try {
			// 保存前校验
			beforecheck(bills[0]);
			return new UpdateAction().update(bills, originBills);
		} catch (Exception ex) {
			ExceptionUtils.marsh(ex);
		}
		return new MaterialOutVO[0];
	}

	public void beforecheck(MaterialOutVO materialOutVO)
			throws BusinessException {
		// TODO Auto-generated method stub
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		MaterialOutHeadVO headVO = (MaterialOutHeadVO) materialOutVO
				.getParentVO();// 材料出库表头
		String pk_org = headVO.getPk_org();
		MaterialOutBodyVO[] mxvos = (MaterialOutBodyVO[]) materialOutVO
				.getChildren(MaterialOutBodyVO.class);
		for (MaterialOutBodyVO mxvo : mxvos) {
			String pk_project = mxvo.getCprojectid();// 项目主键
			if (pk_project != null) {
				ProjectHeadVO xmvo = (ProjectHeadVO) getHyPubBO()
						.queryByPrimaryKey(ProjectHeadVO.class, pk_project + "");// 项目VO
				String project_code = xmvo.getProject_code();// 项目名称
				// 保存时检查该项目是否完成对账，已完成对账不允许保存。（是否完成对账档案：是、否；需要区分组织）
				Object conforg = (Object) getHyPubBO()
						.findColValue(
								"bd_defdoc",
								"code",
								"nvl(dr,0) = 0 and pk_defdoclist in (SELECT PK_DEFDOCLIST "
										+ "FROM BD_DEFDOCLIST WHERE CODE = 'HTSAVEORG')"
										+ " and code = '" + pk_org + "'");// 组织
				if (conforg != null) {
					// 已完成对账不允许保存
					if ("1001A2100000000B68C1".equals(xmvo.getDef17())) {
						throw new BusinessException("项目编码["
								+ xmvo.getProject_code() + "]已完成对账，不允许保存！");
					}
					// EPC字段勾选后不允许提报
					if ("Y".equals(xmvo.getHdef56())) {
						throw new BusinessException("项目编码["
								+ xmvo.getProject_code() + "]为EPC项目，不允许保存");
					}
					checkBodyMate(mxvos,pk_org);// 检查出库单表体在项目下不存在物资需求单的物料行
				}
				// 20221012 材料结算 施工单位+项目是否有 组织为03和0308
				if ("0001A21000000001ZBF8".equals(pk_org)
						|| "0001A11000000007P8FS".equals(pk_org)) {
					MaterialaccHeadVO[] cljsvo = (MaterialaccHeadVO[]) getHyPubBO()
							.queryByCondition(
									MaterialaccHeadVO.class,
									"pk_project = '"
											+ pk_project
											+ "' and pk_supplier = '"
											+ headVO.getCconstructvendorid()
											+ "' and dr = 0 and bill_status = 1");
					if (cljsvo != null && cljsvo.length > 0
							&& cljsvo[0] != null
							&& cljsvo[0].getBill_code() != null) {
						throw new BusinessException("项目编码[" + project_code
								+ "]已做材料结算[" + cljsvo[0].getBill_code() + "]");
					}
				}
				// 材料结算 施工单位+项目是否有结束
				String pk_projectclass = xmvo.getPk_projectclass() + "";// 项目类型主键

				String fg = "";
				if (pk_projectclass != null
						&& ("1001A21000000005SCHW".equals(pk_projectclass) || "1001A21000000005NM9B"
								.equals(pk_projectclass))) {
					String getndjhsql = "SELECT COUNT(ZB.PK_YEARPLAN) AS SL FROM PM_YEARPLAN ZB LEFT JOIN PM_YEARPLAN_B MX ON ZB.PK_YEARPLAN = MX.PK_YEARPLAN WHERE ZB.DR = 0 AND MX.DR = 0 AND ZB.PLAN_YEAR > '2020' AND MX.PK_PROJECT = '"
							+ pk_project + "'";
					System.out.println("查询年度投资计划SQL：" + getndjhsql);
					List<Object[]> tzls = getDao.query(getndjhsql);
					int sl = Integer.parseInt(tzls.get(0)[0] + "");
					if (sl > 0) {
						fg = "1";
					}
				} else {
					System.out.println("项目类型：" + pk_projectclass);
				}
				if ("1".equals(fg)) {
					// 校验是否能投或下属子公司
					int ifcheck = 0;
					OrgVO orgVO = (OrgVO) new HYPubBO().queryByPrimaryKey(
							OrgVO.class, pk_org);
					if ("0001A21000000003U6L6".equals(pk_org)
							|| "0001A21000000003U6L6".equals(orgVO
									.getPk_fatherorg())) {
						ifcheck = 1;
					}
					if (ifcheck == 0
							&& StringUtils.isNotEmpty(orgVO.getPk_fatherorg())) {
						OrgVO fatherorgVO = (OrgVO) new HYPubBO()
								.queryByPrimaryKey(OrgVO.class, pk_org);// 上级组织
						if (StringUtils.isNotEmpty(fatherorgVO
								.getPk_fatherorg())
								&& ("0001A21000000003U6L6".equals(fatherorgVO
										.getPk_fatherorg()))) {
							ifcheck = 1;
						}
					}
					// String project_name =
					// projectHeadVO.getProject_name();//项目名称

					ProjectTypeHeadVO typeVO = (ProjectTypeHeadVO) getHyPubBO()
							.queryByPrimaryKey(ProjectTypeHeadVO.class,
									pk_projectclass + "");// 项目类型VO
					if (typeVO != null && ifcheck == 1) {
						String kgrqsql = "SELECT COUNT(PK_PROJECTPROPOSAL) AS SL FROM PM_PROJECTPROPOSAL WHERE BILL_STATUS = '1' AND PK_GROUP = '0001A1100000000001QS' "
								+ "AND TRANSI_TYPE = '4D15-Cxx-011' AND DR = 0 AND PK_PROJECT = '"
								+ pk_project + "'";
						List<Object[]> rqls = getDao.query(kgrqsql);
						System.out.println("查询开工容缺受理单sql：" + kgrqsql);
						if (Integer.parseInt(rqls.get(0)[0] + "") <= 0) {
							String type_name = typeVO.getType_name();// 项目类型名称
							String sql = "SELECT NAME FROM RL_CBSNAME WHERE NAME LIKE '%"
									+ type_name + "%' AND TYPE = 'XMTYPE'";
							System.out.println("sql===" + sql);
							List<Object[]> ls = getDao.query(sql);
							if (ls != null && ls.size() > 0) {
								String pgdsql = "SELECT PK_PROJECTPROPOSAL FROM PM_PROJECTPROPOSAL WHERE BILL_STATUS = '1' AND PK_GROUP = '0001A1100000000001QS' "
										+ "AND TRANSI_TYPE = '4D15-Cxx-008' AND DR = 0 AND PK_PROJECT = '"
										+ pk_project + "'";
								List<Object[]> pgdls = getDao.query(pgdsql);
								if (pgdls == null || pgdls.size() <= 0) {
									throw new BusinessException("项目编码["
											+ project_code + "]未完成开工单！");
								}
							}
						}
					}
				}
			}
		}
	}

	// 检查出库单表体在项目下不存在物资需求单的物料行
	private void checkBodyMate(MaterialOutBodyVO[] billVOs,String pk_org)
			throws BusinessException {
		// 获取表体所有项目
		for (MaterialOutBodyVO scmVO : billVOs) {
			try {
				if (scmVO.getCprojectid() != null) {
					ProjectHeadVO xmvo = (ProjectHeadVO) getHyPubBO()
							.queryByPrimaryKey(ProjectHeadVO.class,
									scmVO.getCprojectid() + ""); // 项目档案VO
					String def5 = xmvo.getDef5() + "";// 是否完工=====新增
					if (def5 == null || "".equals(def5) || "~".equals(def5)
							|| "null".equals(def5)) {

					} else {
						if ("1001A2100000000B68C1".equals(def5)) {
							// 如果为是，则不允许保存
							throw new BusinessException("行号为"
									+ scmVO.getCrowno() + "行项目档案已完工，不允许保存！");
						}
					}
					// 20230817
					ProjectTypeHeadVO typeVO = (ProjectTypeHeadVO) getHyPubBO()
							.queryByPrimaryKey(ProjectTypeHeadVO.class,
									xmvo.getPk_projectclass() + "");// 项目类型VO
					String type_name = typeVO.getType_name();// 项目类型名称
					String sql = "SELECT NAME FROM RL_CBSNAME WHERE NAME LIKE '%"
							+ type_name + "%' AND TYPE = 'CLCK'";
					System.out.println("sql===" + sql);
					GetDao getDao = NCLocator.getInstance().lookup(
							GetDao.class);
					List<Object[]> ls = getDao.query(sql);
					if (ls != null && ls.size() > 0) {
						MaterialPlanBillVO wzvo = NCLocator
								.getInstance()
								.lookup(IMaterialPlanCommonService.class)
								.queryMaterialPlanBillVOByPkProject(
										scmVO.getCprojectid() + "");// 物资及服务需求单VO
						if (wzvo == null || !wzvo.getParentVO().getPk_org().equals(pk_org)) {
							throw new BusinessException("行号为"
									+ scmVO.getCrowno() + "行请先做物资需求单！");
						}
					}
				}
				return;
			} catch (UifException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
	}
}
