package nc.ui.arap.actions;

import java.awt.event.ActionEvent;
import nc.bs.framework.common.NCLocator;
import nc.ui.pcm.utils.GetDao;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.IEditor;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.pub.beans.MessageDialog;
import nc.vo.arap.basebill.BaseAggVO;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.arap.pay.PayBillItemVO;
import nc.vo.pub.CircularlyAccessibleValueObject;

@SuppressWarnings("restriction")
public class GCFKSaveAction extends CreditCheckDecoratorAction {
	public GCFKSaveAction(NCAction arg0) {
		// TODO 自动生成的构造函数存根
		super(arg0);
		setBtnName("提交");
	}

	private static final long serialVersionUID = 1L;
	private IEditor editor;
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);

	@Override
	public void doAction(ActionEvent e) throws Exception {
		// TODO 自动生成的方法存根
		IUAPQueryBS bs = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		BaseAggVO billVO = (BaseAggVO)getEditor().getValue();
		BaseBillVO headVO = billVO.getHeadVO();	
		// 取主表数据
		if(headVO==null){
			billVO = (BaseAggVO)this.getModel().getSelectedData();
			headVO = billVO.getHeadVO();
		}
		//子表数据
		CircularlyAccessibleValueObject[] ChildrenCtPuBVOs = billVO.getTableVO("bodys");
		for (int j = 0; j < ChildrenCtPuBVOs.length; j++) {
			PayBillItemVO conWorkVO = (PayBillItemVO) ChildrenCtPuBVOs[j];	
			String htno = conWorkVO.getContractno()+"";//合同号
			String gyspk = conWorkVO.getSupplier() +"";//供应商
			String sql = "SELECT PK_CONTR AS HTPK FROM PM_CONTR WHERE BILL_CODE = '"+htno+"' AND PK_SUPPLIER = '"+gyspk+"' AND DR = 0  AND (BILL_STATUS = 1 OR BILL_STATUS = 9)"
					+ "UNION ALL SELECT PK_CT_PU AS HTPK FROM CT_PU WHERE VBILLCODE = '"+htno+"' AND CVENDORID = '"+gyspk+"' AND DR = 0 AND (FSTATUSFLAG = 1 OR FSTATUSFLAG = 3)"
					+ "UNION ALL SELECT PK_FEEBALANCE AS HTPK FROM PM_FEEBALANCE WHERE DEF2 = '"+htno+"' AND PK_SUPPLIER = '"+gyspk+"' AND DR = 0 AND BILL_STATUS = 1 UNION ALL "
					+ "SELECT PK_FCT_AP FROM FCT_AP WHERE VBILLCODE = '"+htno+"' AND CVENDORID = '"+gyspk+"' AND DR = 0 ";
			Object htpk = bs.executeQuery(sql, new ColumnProcessor());
			if((htpk!=null&&!"".equals(htpk+"")&&!"~".equals(htpk+""))||"无合同".equals(htno)){
				super.doAction(e);
			}else{
				MessageDialog.showErrorDlg(null, "提示", "暂无该合同，请重新录入！");
				return ;
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