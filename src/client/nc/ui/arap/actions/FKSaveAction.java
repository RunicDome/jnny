package nc.ui.arap.actions;

import java.awt.event.ActionEvent;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.pcm.utils.GetDao;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.IEditor;
import nc.vo.arap.basebill.BaseAggVO;
import nc.vo.arap.pay.PayBillItemVO;
import nc.vo.arap.pay.PayBillVO;

@SuppressWarnings("restriction")
public class FKSaveAction extends CreditCheckDecoratorAction {
	public FKSaveAction(NCAction arg0) {
		// TODO 自动生成的构造函数存根
		super(arg0);
		setBtnName("提交");
	}

	private static final long serialVersionUID = 1L;
	private IEditor editor;
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);

	@SuppressWarnings("unused")
	@Override
	public void doAction(ActionEvent e) throws Exception {
		// TODO 自动生成的方法存根
		// super.doAction(e);
		IUAPQueryBS bs = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		BaseAggVO billVO = (BaseAggVO) this.editor.getValue();
		System.out.println("主键：" + billVO.getPrimaryKey());
		// 取主表数据
		if (billVO == null) {
			billVO = (BaseAggVO) this.getModel().getSelectedData();
		}
		PayBillVO mainVO = (PayBillVO) billVO.getParentVO();
		// System.out.println(mainVO.getLocal_money());
		PayBillItemVO[] ChildrenCtPuBVOs = (PayBillItemVO[]) billVO
				.getChildrenVO();
		PayBillItemVO itemVO = (PayBillItemVO) ChildrenCtPuBVOs[0];
		String mhtno = itemVO.getContractno() + "";// 合同号
		String mgyspk = itemVO.getSupplier() + "";// 供应商
		String ctsql = "SELECT COUNT(PK_CT_PU) AS SL FROM CT_PU WHERE VBILLCODE = '"
				+ mhtno + "' AND DR = 0";
		List<Object[]> ctls = getDao.query(ctsql);
		if (ctls != null && ctls.size() > 0 && ctls.get(0) != null) {
			int sl = Integer.parseInt((ctls.get(0)[0] + ""));
			if (sl > 0) {
				// 如果是热力集团的，判断
				if ("0001A110000000000HYQ".equals(mainVO.getPk_fiorg())
						|| "0001A21000000000IYIU".equals(mainVO.getPk_fiorg())
						|| "0001A21000000000YHPX".equals(mainVO.getPk_fiorg())) {
					// AND ZB.PK_TRADETYPE = 'D3'
					String fksql = "SELECT DISTINCT ZB.BILLNO,MX.SUPPLIER,ZB.APPROVESTATUS FROM AP_PAYBILL ZB "
							+ "LEFT JOIN AP_PAYITEM MX ON ZB.PK_PAYBILL = MX.PK_PAYBILL WHERE ZB.DR = 0 AND MX.DR = 0 "
							+ "AND MX.CONTRACTNO = '"
							+ mhtno
							+ "' AND MX.SUPPLIER = '"
							+ mgyspk
							+ "' AND ZB.PK_TRADETYPE = 'D3' ";
					if (mainVO.getPrimaryKey() != null
							&& !"".equals(mainVO.getPrimaryKey())) {
						fksql += "AND ZB.PK_PAYBILL <> '"
								+ mainVO.getPrimaryKey() + "'";
					}
					System.out.println("查询付款SQL：" + fksql);
					List<Object[]> fkls = getDao.query(fksql);
					if (fkls != null && fkls.size() > 0 && fkls.get(0) != null) {
						for (int fki = 0; fki < fkls.size(); fki++) {
							String spzt = fkls.get(fki)[2] + "";// 单据状态
							if (!"1".equals(spzt)) {
								MessageDialog.showErrorDlg(null, "提示", "付款单号["
										+ fkls.get(fki)[0] + "" + "]的单据未审批通过！");
								return;
							}
						}
					}
				}
			}
		}

		for (int j = 0; j < ChildrenCtPuBVOs.length; j++) {
			PayBillItemVO conWorkVO = (PayBillItemVO) ChildrenCtPuBVOs[j];
			// System.out.println(conWorkVO);
			String htno = conWorkVO.getContractno() + "";// 合同号
			String gyspk = conWorkVO.getSupplier() + "";// 供应商
			String sql = "SELECT PK_CONTR AS HTPK FROM PM_CONTR WHERE BILL_CODE = '"
					+ htno
					+ "' AND PK_SUPPLIER = '"
					+ gyspk
					+ "' AND DR = 0  AND (BILL_STATUS = 1 OR BILL_STATUS = 9)"
					+ "UNION ALL SELECT PK_CT_PU AS HTPK FROM CT_PU WHERE VBILLCODE = '"
					+ htno
					+ "' AND CVENDORID = '"
					+ gyspk
					+ "' AND DR = 0 AND (FSTATUSFLAG = 1 OR FSTATUSFLAG = 3)"
					+ "UNION ALL SELECT PK_FEEBALANCE AS HTPK FROM PM_FEEBALANCE WHERE DEF2 = '"
					+ htno
					+ "' AND PK_SUPPLIER = '"
					+ gyspk
					+ "' AND DR = 0 AND BILL_STATUS = 1 UNION ALL "
					+ "SELECT PK_FCT_AP FROM FCT_AP WHERE VBILLCODE = '"
					+ htno
					+ "' AND CVENDORID = '" + gyspk + "' AND DR = 0 UNION ALL "
					+ "SELECT PM_FEEBALANCE FROM PM_FEEBALANCE_CT WHERE BILL_CODE = '"
					+ htno
					+ "' AND PK_SUPPLIER_NAME = '" + gyspk + "' AND DR = 0 ";
			Object htpk = bs.executeQuery(sql, new ColumnProcessor());
			if ((htpk != null && !"".equals(htpk + "") && !"~"
					.equals(htpk + "")) || "无合同".equals(htno)) {
				// XBX20210204新增
				double yfmon = 0.00;// NC发票挂账金额
				double fkmon = 0.00;// NC累计付款金额
				double qkmon = 0.00;// 欠款
				String yfdsql = "SELECT YF.CONTRACTNO,MX.SUPPLIER,SUM(YF.LOCAL_MONEY) AS FPJE "
						+ "FROM AP_PAYABLEBILL YF LEFT JOIN (SELECT DISTINCT PK_PAYABLEBILL,SUPPLIER "
						+ "FROM AP_PAYABLEITEM WHERE DR = 0) MX ON YF.PK_PAYABLEBILL = MX.PK_PAYABLEBILL "
						+ "WHERE YF.CONTRACTNO = '"
						+ htno
						+ "' AND MX.SUPPLIER = '"
						+ gyspk
						+ "' AND YF.DR = 0 "
						+ "AND YF.APPROVESTATUS = '1' AND YF.PK_TRADETYPE = 'D1' GROUP BY YF.CONTRACTNO,MX.SUPPLIER";
				Logger.error("查询NC发票挂账金额：" + yfdsql);
				List<Object[]> yfls = getDao.query(yfdsql);
				if (yfls != null && yfls.size() > 0 && yfls.get(0) != null) {
					yfmon = Double.parseDouble(yfls.get(0)[2].toString());
				}
				//20210720新增 采购合同  VDEF13
				String cthtsql = "SELECT VDEF13 FROM CT_PU WHERE VBILLCODE = '"+htno+"' AND DR = 0 AND BLATEST = 'Y'";
				List<Object[]> cghtls = getDao.query(cthtsql);
				if (cghtls != null && cghtls.size() > 0 && cghtls.get(0) != null&& cghtls.get(0)[0] != null) {
					yfmon += Double.parseDouble(cghtls.get(0)[0].toString());
				}
				Logger.error("查询采购合同金额：" + cthtsql);
				System.out.println("查询采购合同金额：" + cthtsql);
				String fksql = "";
				if (billVO.getPrimaryKey() == null) {
					fksql = "SELECT MX.CONTRACTNO,MX.SUPPLIER,SUM(FK.LOCAL_MONEY) AS FPJE FROM "
							+ "AP_PAYBILL FK LEFT JOIN (SELECT DISTINCT CONTRACTNO,PK_PAYBILL,SUPPLIER "
							+ "FROM AP_PAYITEM WHERE DR = 0) MX ON FK.PK_PAYBILL = MX.PK_PAYBILL "
							+ "WHERE MX.CONTRACTNO = '"
							+ htno
							+ "' AND MX.SUPPLIER = '"
							+ gyspk
							+ "' AND FK.DR = 0 "
							+ "AND FK.APPROVESTATUS = '1' AND FK.PK_TRADETYPE = 'D3' GROUP BY MX.CONTRACTNO,MX.SUPPLIER";
				} else {
					fksql = "SELECT MX.CONTRACTNO,MX.SUPPLIER,SUM(FK.LOCAL_MONEY) AS FPJE FROM "
							+ "AP_PAYBILL FK LEFT JOIN (SELECT DISTINCT CONTRACTNO,PK_PAYBILL,SUPPLIER "
							+ "FROM AP_PAYITEM WHERE DR = 0) MX ON FK.PK_PAYBILL = MX.PK_PAYBILL "
							+ "WHERE MX.CONTRACTNO = '"
							+ htno
							+ "' AND MX.SUPPLIER = '"
							+ gyspk
							+ "' AND FK.DR = 0 "
							+ "AND FK.APPROVESTATUS = '1' AND FK.PK_TRADETYPE = 'D3' AND FK.PK_PAYBILL <> '"
							+ billVO.getPrimaryKey()
							+ "'"
							+ " GROUP BY MX.CONTRACTNO,MX.SUPPLIER";
				}
				Logger.error("查询NC累计付款金额：" + fksql);
				List<Object[]> fkls = getDao.query(fksql);
				if (fkls != null && fkls.size() > 0 && fkls.get(0) != null) {
					fkmon = Double.parseDouble(fkls.get(0)[2].toString())
							+ mainVO.getLocal_money().toDouble();
				} else {
					fkmon += mainVO.getLocal_money().toDouble();
				}
				qkmon = yfmon - fkmon;// 欠款
				System.out.println("发票：" + yfmon + "付款：" + fkmon + "欠款："
						+ qkmon);
				if (qkmon < 0) {
					int yn = MessageDialog.showOkCancelDlg(null, "提示",
							"NC发票挂账金额小于NC累计付款金额，是否确认保存？");
					// 确认=1 ，取消=2
					if (yn == 1) {
						super.doAction(e);
						System.out.println(111);
						BaseAggVO afVO = (BaseAggVO) this.editor.getValue();
						System.out.println(afVO);
						// 取主表数据
						if (afVO == null) {
							afVO = (BaseAggVO) this.getModel()
									.getSelectedData();
						}
						System.out.println("保存后主键：" + afVO.getPrimaryKey());
						String updsql = "UPDATE AP_PAYBILL SET DEF42 = '"
								+ yfmon + "',DEF43 = " + "'" + fkmon
								+ "',DEF44 = '" + qkmon
								+ "' WHERE PK_PAYBILL = '"
								+ afVO.getPrimaryKey() + "'";
						getDao.executeUpdate(updsql);
						// this.getModel().directlyUpdate(aggvo);
					} else {
						return;
					}
				} else {
					super.doAction(e);
				}
			} else {
				MessageDialog.showErrorDlg(null, "提示", "暂无该合同，请重新录入！");
				return;
			}
		}
	}

	public IEditor getEditor() {
		return this.editor;
	}

	public void setEditor(IEditor editor) {
		this.editor = editor;
	}
}