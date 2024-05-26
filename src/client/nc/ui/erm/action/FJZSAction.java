package nc.ui.erm.action;

import java.awt.event.ActionEvent;
import java.util.List;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.itf.uap.pf.metadata.IFlowBizItf;
import nc.md.data.access.NCObject;
import nc.ui.uif2.model.BillManageModel;
// import nc.ui.ecpubapp.uif2app.query.model.ModelDataRefresher;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.pcm.utils.GetDao;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.uif2.NCAction;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKHeaderVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;

public class FJZSAction extends NCAction {
	private static final long serialVersionUID = 1L;
	private BillManageModel model;
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);

	public FJZSAction() {
		super.setCode("fjzs");
		super.setBtnName("附件张数");
	}

	@Override
	protected boolean isActionEnable() {
		// TODO Auto-generated method stub
		boolean isena = false;
		JKBXVO villvo = (JKBXVO) this.getModel().getSelectedData();
		if (villvo == null)
			return isena;
		if (villvo.getParentVO().getSpzt() == 1)
			isena = true;
		return isena;
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		// TODO 自动生成的方法存根

		if (this.model.getSelectedData() == null) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("1054002_0", "01054002-0003"));
		} else {
			ErmBillBillManageModel mode = (ErmBillBillManageModel) this
					.getModel();
			NCObject ncObj = NCObject.newInstance(mode.getSelectedData());
			IFlowBizItf itf = (IFlowBizItf) ncObj
					.getBizInterface(IFlowBizItf.class);
			String userid = InvocationInfoProxy.getInstance().getUserId();// 当前登录人主键
			String querySql = "SELECT COUNT(YHJS.CUSERID) AS SL FROM SM_USER_ROLE YHJS LEFT JOIN SM_ROLE JS ON YHJS.PK_ROLE = JS.PK_ROLE WHERE JS.ROLE_CODE = 'FYFJZS' AND YHJS.CUSERID = '"
					+ userid + "'";
			List<Object[]> jsls = getDao.query(querySql);
			if (jsls != null && Integer.parseInt(jsls.get(0)[0] + "") > 0) {
				String user_code = InvocationInfoProxy.getInstance()
						.getUserCode();// 当前登录人编码
				String pk_primarykey = itf.getBillId() + "";// 单据主键
				// String billmaker = itf.getBillMaker() + "";// 制单人
				String transi_type = itf.getTranstype();
				int lx = 0;// 0为报销 1 为借款
				if (transi_type.length() >= 4) {
					if ("263".equals(transi_type.substring(0, 3)))
						lx = 1;
				}
				System.out.println("单据主键：" + pk_primarykey);
				System.out.println("当前登录人主键：" + userid);
				System.out.println("当前登录人编码：" + user_code);
				if (lx == 0) {
					// 0为报销
					bxfj();
				} else {
					// 1为借款
					jkfj();
				}
			} else {
				MessageDialog.showErrorDlg(null, "提示", "暂无权限！");
			}
		}
	}

	// 借款附件
	public void jkfj() throws DAOException {
		JKVO JKVO = (JKVO) this.model.getSelectedData();
		JKHeaderVO billvo = (JKHeaderVO) JKVO.getParentVO();
		Object value = MessageDialog.showInputDlg(null, "提示", "请输入附件张数",
				billvo.getFjzs());
		if (value == null) {
			return;
		} else if (isNumeric(value + "")) {
			int sl = Integer.parseInt(value + "");
			String sql = getjksql(billvo.getPrimaryKey());
			List<Object[]> JKls = getDao.query(sql);
			System.out.println("凭证审核人：" + JKls.get(0)[3] + "");
			if (JKls != null && JKls.get(0)[3] != null) {
				MessageDialog.showErrorDlg(null, "提示", "该单据对应凭证已审批，不允许修改附件张数");
			} else {
				String pk_voucher = JKls.get(0)[0] + "";
				System.out.println("凭证主键：" + pk_voucher);
				// updatejkfjnum(sl + "", billvo.getPrimaryKey());
				// billvo.setFjzs(sl);
				// JKVO.setParentVO(billvo);
				updatepzfjnum(sl + "", pk_voucher);
				MessageDialog.showErrorDlg(null, "提示", "修改成功！");
//				try {
//					this.getModel().update(JKVO);
//					new ModelDataRefresher(this.getModel()).refreshData();
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		} else {
			MessageDialog.showErrorDlg(null, "提示", "请输入整数");
		}
	}

	// 报销附件
	public void bxfj() throws Exception {
		BXVO bxVO = (BXVO) this.model.getSelectedData();
		BXHeaderVO billvo = (BXHeaderVO) bxVO.getParentVO();
		Object value = MessageDialog.showInputDlg(null, "提示", "请输入附件张数",
				billvo.getFjzs());
		if (value == null) {
			return;
		} else if (isNumeric(value + "")) {
			int sl = Integer.parseInt(value + "");
			String sql = getbxsql(billvo.getPrimaryKey());
			List<Object[]> bxls = getDao.query(sql);
			System.out.println("凭证审核人：" + bxls.get(0)[3] + "");
			if (bxls != null && bxls.get(0)[3] != null) {
				MessageDialog.showErrorDlg(null, "提示", "该单据对应凭证已审批，不允许修改附件张数");
			} else {
				String pk_voucher = bxls.get(0)[0] + "";
				System.out.println("凭证主键：" + pk_voucher);
				// updatebxfjnum(sl + "", billvo.getPrimaryKey());
				// billvo.setFjzs(sl);
				// bxVO.setParentVO(billvo);
				updatepzfjnum(sl + "", pk_voucher);
				MessageDialog.showErrorDlg(null, "提示", "修改成功！");
				// this.getModel().directlyUpdate(null);
				// this.getModel().update(bxVO);
			}
		} else {
			MessageDialog.showErrorDlg(null, "提示", "请输入整数");
		}
	}

	// 借款
	public String getjksql(String pk_paybill) {
		String sql = "SELECT PZ.PK_VOUCHER,PZ.NUM,PZ.ATTACHMENT,PZ.PK_CHECKED FROM ER_JKZB JK LEFT "
				+ "JOIN FIP_RELATION GL ON GL.SRC_FREEDEF1 = JK.DJBH LEFT JOIN GL_VOUCHER PZ ON "
				+ "GL.DES_RELATIONID = PZ.PK_VOUCHER WHERE JK.PK_JKBX = '"
				+ pk_paybill + "' " + "AND PZ.DISCARDFLAG = 'N'";
		return sql;
	}

	// 借款
	public String getbxsql(String pk_paybill) {
		String sql = "SELECT PZ.PK_VOUCHER,PZ.NUM,PZ.ATTACHMENT,PZ.PK_CHECKED FROM ER_BXZB BX LEFT "
				+ "JOIN FIP_RELATION GL ON GL.SRC_FREEDEF1 = BX.DJBH LEFT JOIN GL_VOUCHER PZ ON "
				+ "GL.DES_RELATIONID = PZ.PK_VOUCHER WHERE BX.PK_JKBX = '"
				+ pk_paybill + "' " + "AND PZ.DISCARDFLAG = 'N'";
		return sql;
	}

	// 修改报销附件张数
	public void updatebxfjnum(String num, String pk_primary)
			throws DAOException {
		String sql = "UPDATE ER_BXZB SET FJZS = '" + num
				+ "' WHERE PK_JKBX = '" + pk_primary + "';";
		getDao.executeUpdate(sql);
	}

	// 修改借款附件张数
	public void updatejkfjnum(String num, String pk_primary)
			throws DAOException {
		String sql = "UPDATE ER_JKZB SET FJZS = '" + num
				+ "' WHERE PK_JKBX = '" + pk_primary + "';";
		getDao.executeUpdate(sql);
	}

	public void updatepzfjnum(String num, String pk_primary)
			throws DAOException {
		String pzsql = "UPDATE GL_VOUCHER SET ATTACHMENT = '" + num
				+ "' WHERE PK_VOUCHER = '" + pk_primary + "';";
		getDao.executeUpdate(pzsql);
	}

	public static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

}
