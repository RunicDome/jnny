package nc.ui.pcm.feebalance.action;

import java.awt.event.ActionEvent;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.oa.web.GetDBMHTVO;
import nc.ui.pcm.feebalance.util.GenSaveUtils;
import nc.ui.pcm.utils.GetDao;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.trade.business.HYPubBO_Client;
import nc.vo.bd.cbs.CBSNodeVO;
import nc.vo.pcm.feebalance.FeeBalanceBillVO;
import nc.vo.pcm.feebalance.FeeBalanceBodyVO;
import nc.vo.pcm.feebalance.FeeBalanceHeadVO;
import nc.vo.pim.project.ProjectHeadVO;
import nc.vo.pmpub.projecttype.ProjectTypeHeadVO;

@SuppressWarnings("restriction")
public class DBMHTSaveAndCommitAction extends saveAndCommitAction {
	private static final long serialVersionUID = 1L;
	GenSaveUtils genSaveUtils = new GenSaveUtils();

	public void doAction(ActionEvent e) throws Exception {
		FeeBalanceBillVO fee = null;
		fee = (FeeBalanceBillVO) editor.getValue();
		if (fee.getPrimaryKey() == null) {
			fee = (FeeBalanceBillVO) getModel().getSelectedData();
		}
		GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
		FeeBalanceHeadVO headVO = (FeeBalanceHeadVO) fee.getParentVO();// 多编码表头VO
		FeeBalanceBodyVO[] bodyVO = (FeeBalanceBodyVO[]) fee.getChildrenVO();// 多编码表体VO
		String ifblht = headVO.getDef4() + "";
		String flag = "true";// 是否可保存标识
		String pk_feebalance = headVO.getPrimaryKey();// 单据主键
		String pk_supplier = headVO.getPk_supplier();// 供应商主键
		/* 公共变量 */
		String pk_contracttype = headVO.getDef10();// 合同类型
		String wbs_name = genSaveUtils.getQDWbsName(pk_contracttype);// 对应任务名称

		for (int i = 0; i < bodyVO.length; i++) {
			FeeBalanceBodyVO itemvo = bodyVO[i];
			String pk_project = itemvo.getPk_project();// 项目主键
			String pk_cbsnode = itemvo.getPk_cbsnode();// CBS主键
			String pk_cbsnode_name = "";// CBS名称
			CBSNodeVO cbsVO = (CBSNodeVO) HYPubBO_Client.queryByPrimaryKey(
					CBSNodeVO.class, itemvo.getPk_cbsnode() + ""); //
			if (cbsVO != null) {
				pk_cbsnode_name = cbsVO.getName();
			}
			// XBX0930新增
			String type_name = "";// 项目类型名称
			ProjectHeadVO projectHeadVO = (ProjectHeadVO) HYPubBO_Client
					.queryByPrimaryKey(ProjectHeadVO.class, pk_project + "");
			if (projectHeadVO != null) {
				ProjectTypeHeadVO lxVO = (ProjectTypeHeadVO) HYPubBO_Client
						.queryByPrimaryKey(ProjectTypeHeadVO.class,
								projectHeadVO.getPk_projectclass() + "");// 项目类型VO
				if (lxVO != null) {
					type_name = lxVO.getType_name() + "";// 项目类型名称
				}
			}
			String sqltp = "SELECT NAME FROM RL_CBSNAME WHERE NAME LIKE '%"
					+ type_name + "%' AND TYPE = 'QDFBHT'";
			System.out.println("sqltp===" + sqltp);
			List<Object[]> ls = getDao.query(sqltp);
			if (ls != null && ls.size() > 0) {
				// ----------------------1是否完成招标结果登记----------------------
				// 查询招标结果登记
				String sql = "SELECT DISTINCT A.PK_FEEBALANCE_B,B.PK_FEEBALANCE FROM PM_FEEBALANCE_B A LEFT JOIN PM_FEEBALANCE B ON A.PK_FEEBALANCE = "
						+ "B.PK_FEEBALANCE WHERE B.DR = 0 AND A.DR = 0 AND B.BILL_STATUS = 1 AND "
						+ "B.TRANSI_TYPE = '4D83-Cxx-04' AND B.PK_GROUP = '0001A1100000000001QS' AND A.DEF1 = '"
						+ pk_supplier
						+ "' AND "
						+ "A.PK_PROJECT = '"
						+ pk_project
						+ "' AND A.PK_CBSNODE = '"
						+ pk_cbsnode
						+ "'";
				List<Object[]> zbjgdjls = getDao.query(sql);
				if (zbjgdjls.size() == 0) {
					flag = "false";
					MessageDialog.showErrorDlg(null, "提示", "第" + (i + 1)
							+ "行未完成招标结果登记！");
					return;
				}
				// 查询清单发包合同已有数量
				String selqdsql = "SELECT DISTINCT A.PK_CONTR_WORKS,B.PK_CONTR FROM PM_CONTR_WORKS A LEFT JOIN PM_CONTR B ON A.PK_CONTR = B.PK_CONTR "
						+ "WHERE B.DR = 0 AND B.PK_PROJECT = '"
						+ pk_project
						+ "' AND B.PK_SUPPLIER = '"
						+ pk_supplier
						+ "' AND A.PK_CBSNODE = '" + pk_cbsnode + "'";
				// 查询多编码合同已有数量
				String seldbmsql = "SELECT DISTINCT A.PK_FEEBALANCE_B,B.PK_FEEBALANCE FROM PM_FEEBALANCE_B A LEFT JOIN PM_FEEBALANCE B ON A.PK_FEEBALANCE = B.PK_FEEBALANCE WHERE B.DR = 0 AND B.TRANSI_TYPE = '4D83-Cxx-04' AND A.PK_PROJECT = '"
						+ pk_project
						+ "'"
						+ " AND B.PK_SUPPLIER = '"
						+ pk_supplier
						+ "' AND A.PK_CBSNODE = '"
						+ pk_cbsnode
						+ "' AND A.PK_FEEBALANCE_B <> '" + pk_feebalance + "'";
				int zbnum = zbjgdjls.size();
				List<Object[]> qdls = getDao.query(selqdsql);
				List<Object[]> dbmls = getDao.query(seldbmsql);
				if (qdls.size() + dbmls.size() >= zbnum) {
					MessageDialog.showErrorDlg(null, "提示", "第" + (i + 1)
							+ "行超出招标结果登记数量限制，总数为" + zbnum + "!");
					flag = "false";
					return;
				}
				// ----------------------1是否完成招标结果登记判断结束----------------------
				// ----------------------2是否完成预算录入----------------------
				String ysql = "SELECT DISTINCT A.PK_BUDGETCBS,B.PK_BUDGET FROM PM_BUDGETCBS A LEFT JOIN  PM_BUDGET B ON A.PK_BUDGET = B.PK_BUDGET WHERE "
						+ "B.DR = 0 AND B.PK_GROUP = '0001A1100000000001QS' AND B.LAST_V_FLAG = 'Y' AND B.BILL_STATUS IN ('1','9') AND B.TRANSI_TYPE "
						+ "= '4D18-01' AND B.PK_PROJECT = '"
						+ pk_project
						+ "' AND A.PK_CBSNODE = '" + pk_cbsnode + "'";
				List<Object[]> ysls = getDao.query(ysql);
				if (ysls.size() <= 0) {
					MessageDialog.showErrorDlg(null, "提示", "第" + (i + 1)
							+ "行未完成预算录入!");
					flag = "false";
					return;
				}
				// ----------------------2是否完成预算录入判断结束----------------------
			}
			// ----------------------3是否有派工单----------------------
			String cbsql = "SELECT NAME FROM RL_CBSNAME WHERE NAME = '"
					+ pk_cbsnode_name + "'";
			List<Object[]> cbsls = getDao.query(cbsql);
			if (cbsls != null && cbsls.size() > 0) {
				String pgdsql = "SELECT PK_PROJECTPROPOSAL FROM PM_PROJECTPROPOSAL WHERE BILL_STATUS = '1' AND PK_GROUP = '0001A1100000000001QS' "
						+ "AND TRANSI_TYPE = '4D15-Cxx-008' AND DR = 0 AND PK_PROJECT = '"
						+ pk_project
						+ "' AND AUDITTIME >= '2021-03-26 00:00:00'";
				List<Object[]> pgdls = getDao.query(pgdsql);
				if (pgdls != null && pgdls.size() > 0) {
					MessageDialog.showErrorDlg(null, "提示", "第" + (i + 1)
							+ "行项目已完成开工单!");
					flag = "false";
					return;
				}
			}
			// ----------------------3是否有派工单判断结束----------------------
		}
		if ("true".equals(flag)) {
			if (wbs_name != null && !"".equals(wbs_name)) {
				String transType = headVO.getTransi_type();// 交易类型
				String pkOrg = headVO.getPk_org();// 组织
				FeeBalanceBillVO newBillVO = new FeeBalanceBillVO();// 费用结算单聚合VO（修改后）
				String zbtype = headVO.getDef1() + "";// 招标结果登记的招标方式
				int orgFlag = genSaveUtils.checkOrg(pkOrg);
				if (orgFlag == 0) {
					// 如果没有则跳过
				} else if (orgFlag == 1) {
					/* 3===================第三步验证WBS任务 */
					FeeBalanceBodyVO[] newBodyVO = genSaveUtils.checkWBS(pkOrg,
							transType, bodyVO, zbtype, wbs_name);
					if (newBodyVO != null && newBodyVO.length > 0) {
						newBillVO.setParentVO(headVO);
						newBillVO.setChildrenVO(newBodyVO);
						this.editor.setValue(newBillVO);
					}
				}
			}
			super.doAction(e);
			if ("1001A2100000000B68C1".equals(ifblht)) {
			} else {
				//提交OA
				GetDBMHTVO.getgetDBMHTVOMsg(fee);
			}
		}

	}
}
