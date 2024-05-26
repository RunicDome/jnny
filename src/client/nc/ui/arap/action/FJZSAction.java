package nc.ui.arap.action;

import java.awt.event.ActionEvent;
import java.util.List;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.md.data.access.NCObject;
import nc.ui.arap.model.ArapBillManageModel;
import nc.ui.pcm.utils.GetDao;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.IEditor;
import nc.vo.arap.pay.AggPayBillVO;
import nc.vo.arap.pay.PayBillVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;

@SuppressWarnings({ "restriction", "unused" })
public class FJZSAction extends NCAction {

	private IEditor editor;
	private ArapBillManageModel model;
	private static final long serialVersionUID = 1L;
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);

	public FJZSAction() {
		super.setBtnName("附件张数");
	}

	@Override
	protected boolean isActionEnable() {
		// TODO Auto-generated method stub
		boolean fg = true;
		if (this.model.getSelectedData() == null) {
			fg = false;
		} else {
			AggPayBillVO aggvo = (AggPayBillVO) this.model.getSelectedData();
			PayBillVO billvo = (PayBillVO) aggvo.getParentVO();
			if (billvo.getApprovestatus() == 1) {
				fg = true;
			} else {
				fg = false;
			}
		}
		return fg;
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		// TODO 自动生成的方法存根
		String userid = InvocationInfoProxy.getInstance().getUserId();// 当前登录人主键
		String querySql = "SELECT COUNT(YHJS.CUSERID) AS SL FROM SM_USER_ROLE YHJS LEFT JOIN SM_ROLE JS ON YHJS.PK_ROLE = JS.PK_ROLE WHERE JS.ROLE_CODE = 'FJZS' AND YHJS.CUSERID = '"
				+ userid + "'";
		List<Object[]> jsls = getDao.query(querySql);
		if (jsls != null
				&& Integer.parseInt(jsls.get(0)[0] + "") > 0) {
			if (this.model.getSelectedData() == null) {
				throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("1054002_0", "01054002-0003"));
			} else {
				AggPayBillVO aggvo = (AggPayBillVO) this.model.getSelectedData();
				PayBillVO billvo = (PayBillVO) aggvo.getParentVO();
				Object value = MessageDialog.showInputDlg(null, "提示", "请输入附件张数",
						billvo.getAccessorynum());
				if (value == null) {
					return;
				} else if (isNumeric(value + "")) {
					int sl = Integer.parseInt(value + "");
					String sql = getsql(billvo.getPrimaryKey());
					List<Object[]> bxls = getDao.query(sql);
					if (bxls != null && bxls.size() > 0 && bxls.get(0) != null && bxls.get(0)[3] != null) {
						System.out.println("凭证审核人：" + bxls.get(0)[3] + "");
						MessageDialog.showErrorDlg(null, "提示",
								"该单据对应凭证已审批，不允许修改附件张数");
					} else {
						if (bxls.size() > 0) {
							String pk_voucher = bxls.get(0)[0] + "";
							System.out.println("凭证主键：" + pk_voucher);
							updatepzfjnum(sl + "", pk_voucher);
						}else{
							MessageDialog.showErrorDlg(null, "提示",
									"该单据未生成凭证，不允许修改附件张数");
							return ;
						}
						billvo.setAccessorynum(sl);
						aggvo.setParentVO(billvo);
						MessageDialog.showErrorDlg(null, "提示", "修改成功！");
						// 刷新卡片界面
						this.getModel().directlyUpdate(aggvo);
						updatefkfjnum(sl+"", billvo.getPrimaryKey());
					}
				} else {
					MessageDialog.showErrorDlg(null, "提示", "请输入整数");
				}
				System.out.println("输入的附件张数：" + value);
			}
		} else {
			MessageDialog.showErrorDlg(null, "提示", "暂无权限！");
		}
		
	}

	public String getsql(String pk_paybill) {
		String sql = "SELECT PZ.PK_VOUCHER,PZ.NUM,PZ.ATTACHMENT,PZ.PK_CHECKED FROM AP_PAYBILL FK LEFT "
				+ "JOIN FIP_RELATION GL ON GL.SRC_FREEDEF1 = FK.BILLNO LEFT JOIN GL_VOUCHER PZ ON "
				+ "GL.DES_RELATIONID = PZ.PK_VOUCHER WHERE FK.PK_PAYBILL = '"
				+ pk_paybill + "' " + "AND PZ.DISCARDFLAG = 'N'";
		return sql;
	}

	public void updatefkfjnum(String num, String pk_primary) throws BusinessException {
		String sql = "UPDATE AP_PAYBILL SET ACCESSORYNUM = '" + num
				+ "' WHERE PK_PAYBILL = '" + pk_primary + "';";
		getDao.executeUpdate(sql);
	}

	public void updatepzfjnum(String num, String pk_primary)
			throws DAOException {
		String pzsql = "UPDATE GL_VOUCHER SET ATTACHMENT = '" + num
				+ "' WHERE PK_VOUCHER = '" + pk_primary + "';";
		getDao.executeUpdate(pzsql);
	}

	public IEditor getEditor() {
		return editor;
	}

	public void setEditor(IEditor editor) {
		this.editor = editor;
	}

	public ArapBillManageModel getModel() {
		return model;
	}

	public void setModel(ArapBillManageModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
