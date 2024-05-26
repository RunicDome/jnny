package nc.ui.erm.action;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.itf.uap.pf.metadata.IFlowBizItf;
import nc.md.data.access.NCObject;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.pcm.utils.GetDao;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;

@SuppressWarnings("restriction")
public class YXBSAction extends NCAction {
	private static final long serialVersionUID = 1L;
	private AbstractUIAppModel model;
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);

	public YXBSAction() {
		super.setBtnName("影像补扫");
	}
	
	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		// TODO 自动生成的方法存根

		if (this.model.getSelectedData() == null) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("1054002_0", "01054002-0003"));
		} else {
			ErmBillBillManageModel mode = (ErmBillBillManageModel) this.getModel();
			NCObject ncObj = NCObject
					.newInstance(mode.getSelectedData());
			IFlowBizItf itf = (IFlowBizItf) ncObj
					.getBizInterface(IFlowBizItf.class);
			String user_code = InvocationInfoProxy.getInstance().getUserCode();// 当前登录人编码
			String userid = InvocationInfoProxy.getInstance().getUserId();// 当前登录人主键
			String pk_primarykey = itf.getBillId() + "";//单据主键
			String billmaker = itf.getBillMaker() + "";//制单人
			String transi_type = itf.getTranstype();
			System.out.println("单据主键：" + pk_primarykey);
			System.out.println("当前登录人主键：" + userid);
			System.out.println("当前登录人编码：" + user_code);
			// if(itf.getApproveStatus() == 1){
				if("264X-Cxx-CNFTFSPD".equals(transi_type)){
					String querySql = "SELECT COUNT(YHJS.CUSERID) AS SL FROM SM_USER_ROLE YHJS LEFT JOIN SM_ROLE JS ON YHJS.PK_ROLE = JS.PK_ROLE WHERE JS.ROLE_CODE = 'YXBSCNF' AND YHJS.CUSERID = '"
							+ userid + "'";
					List<Object[]> bxls = getDao.query(querySql);
					if (bxls != null && Integer.parseInt(bxls.get(0)[0] + "") > 0) {
						openYXBS(pk_primarykey, userid, user_code);
					} else {
						MessageDialog.showErrorDlg(null, "提示", "暂无权限！");
					}
				}else{
					if (billmaker.equals(userid)) {
						openYXBS(pk_primarykey, userid, user_code);
					} else {
						String querySql = "SELECT COUNT(YHJS.CUSERID) AS SL FROM SM_USER_ROLE YHJS LEFT JOIN SM_ROLE JS ON YHJS.PK_ROLE = JS.PK_ROLE WHERE JS.ROLE_CODE = 'YXBS' AND YHJS.CUSERID = '"
								+ userid + "'";
						List<Object[]> bxls = getDao.query(querySql);
						if (bxls != null && Integer.parseInt(bxls.get(0)[0] + "") > 0) {
							openYXBS(pk_primarykey, userid, user_code);
						} else {
							MessageDialog.showErrorDlg(null, "提示", "暂无权限！");
						}
					}
				}
//			}else{
//				MessageDialog.showErrorDlg(null, "提示", "流程未结束！");
//			}
		}
	}
	//打开影像补扫页面
	public void openYXBS(String pk_primarykey,String userid,String user_code) throws IOException{
		String url = "http://60.208.72.37:2333/H5/ImagesScan.html?scantype=1&BusinessSerialNo="
				+ pk_primarykey
				+ "&userid="
				+ userid
				+ "&departnum=0001A110000000000HYQ&billtypename=1&systemcode=NC&SaveType=2&userNo="
				+ user_code;
		Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);// 使用默认浏览器打开url
	}
	public AbstractUIAppModel getModel() {
		return model;
	}

	public void setModel(AbstractUIAppModel model) {
		this.model = model;
	}

}
