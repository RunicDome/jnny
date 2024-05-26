package nc.ui.pcm.contract.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.funcnode.ui.FuncletInitData;
import nc.funcnode.ui.FuncletWindowLauncher;
import nc.itf.pcm.contract.pvt.IContractImport;
import nc.itf.uap.pf.IPfExchangeService;
import nc.sfbase.client.ClientToolKit;
import nc.ui.pcm.utils.GetDao;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.uif2.NCAction;
import nc.vo.arap.pay.AggPayBillVO;
import nc.vo.arap.pay.PayBillItemVO;
import nc.vo.arap.pay.PayBillVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pcm.contract.ContrHeadVO;
import nc.vo.pcm.contract.ContrWorksVO;
import nc.vo.pcm.contract.ContractBillVO;
import nc.vo.pcm.contract.ContractStatusEnum;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.sm.funcreg.FuncRegisterVO;

@SuppressWarnings({ "restriction" })
public class GenaratePayBillByContract extends NCAction {
	private BillManageModel model;
	private BillForm billForm;

	public BillManageModel getModel() {
		return this.model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	// 清单合同Excel导入
	public GenaratePayBillByContract() {
		// TODO Auto-generated constructor stub
		setCode("importQDHT");
		setBtnName("工程付款单");
	}

	@Override
	public void setEnabled(boolean newValue) {
		// TODO Auto-generated method stub
		if (getModel() == null || getModel().getSelectedData() == null) {
			super.setEnabled(Boolean.FALSE);
		} else {
			super.setEnabled(newValue);
		}
	}

	private static final long serialVersionUID = -1261891773744296055L;
	GetDao getDao = NCLocator.getInstance().lookup(GetDao.class);
	IContractImport htdr = NCLocator.getInstance()
			.lookup(IContractImport.class);

	@Override
	public void doAction(ActionEvent arg0) throws BusinessException {
		// 获取当前订单发包合同AggVO
		ContractBillVO aggvo = (ContractBillVO) getModel().getSelectedData();
		if (null == aggvo) {
			MessageDialog.showHintDlg(
					getModel().getContext().getEntranceUI(),
					NCLangRes4VoTransl.getNCLangRes().getStrByID("4020003_0",
							"04020003-0056"), NCLangRes4VoTransl.getNCLangRes()
							.getStrByID("4020003_0", "04020003-0059"));
			return;
		}
		// 清单发包合同转付款单
		AggPayBillVO fkd = getPaybillByContract(aggvo);
		// 打开付款单录入界面
		openPayBillDialog(fkd);
	}

	private void openPayBillDialog(AggPayBillVO fkd) {
		// TODO Auto-generated method stub
		/*
		 * FuncletInitData initData = null; initData = new FuncletInitData();
		 * initData.setInitType(0); initData.setInitData(fkd); FuncRegisterVO
		 * funvo = WorkbenchEnvironment.getInstance()
		 * .getFuncRegisterVO("20080EBR400"); if (null == funvo) {
		 * ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl
		 * .getNCLangRes().getStrByID("4001001_0", "04001001-0076")); return; }
		 * Dimension size = ClientToolKit.getUserClientSize();
		 * size.setSize(size.width * 0.9D, size.height * 0.7D);
		 * FuncletWindowLauncher.openFuncNodeDialog(WorkbenchEnvironment
		 * .getInstance().getWorkbench(), funvo, initData, null, true, true,
		 * size, true);
		 */
		FuncletInitData initData = new FuncletInitData();
		initData.setInitType(0);
		initData.setInitData(fkd);
		FuncRegisterVO funvo = WorkbenchEnvironment.getInstance()
				.getFuncRegisterVO("20080EBR400");
		if (null == funvo) {
			ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl
					.getNCLangRes().getStrByID("4001001_0", "04001001-0076"));
			return;
		}
		Dimension size = ClientToolKit.getUserClientSize();
		size.setSize(size.width * 0.9D, size.height * 0.7D);
		FuncletWindowLauncher.openFuncNodeDialog(WorkbenchEnvironment
				.getInstance().getWorkbench(), funvo, initData, null, true,
				true, size, true);
	}

	private AggPayBillVO getPaybillByContract(ContractBillVO aggvo)
			throws BusinessException {
		// 清单发包合同表头VO
		ContrHeadVO hvo = aggvo.getParentVO();
		if (hvo.getBill_status() != ContractStatusEnum.validate) {
			throw new BusinessException("合同未生效！");
		}
		// 清单发包合同 -> 合同基本 明细数据
		ContrWorksVO[] bvos = (ContrWorksVO[]) aggvo
				.getChildren(ContrWorksVO.class);
		if (bvos == null || bvos.length <= 0) {
			throw new BusinessException("合同基本数据为空！");
		}
		// 付款单VO
		IPfExchangeService pf = NCLocator.getInstance().lookup(
				IPfExchangeService.class);
		AggPayBillVO fkd = (AggPayBillVO) pf.runChangeData(hvo.getBill_type(),
				"F3", aggvo, null);
		// 付款单表头VO
		PayBillVO parentVO = (PayBillVO) fkd.getParentVO();
		// 付款单明细VO
		PayBillItemVO[] mxVOs = (PayBillItemVO[]) fkd.getChildrenVO();
		// 修改付款单表头VO数据，重新赋值金额等数据
		Object[] htmoney = getHTMoney(hvo.getBill_code(), hvo.getPk_org());
		if (htmoney != null) {
			String sdz = htmoney[1].toString();// 审定值
			String htzje = getNumValue(htmoney[2].toString());// 合同总金额
			// String ncyfkje = htmoney[3].toString();// NC已付款金额
			// String yyfkje = htmoney[4].toString();// 原已付款金额
			String htje = getNumValue(htmoney[5].toString());// 合同金额
			String ljfkje = getNumValue(htmoney[6].toString());// 累计付款金额
			String ljpfje = htmoney[7].toString();// 累计发票金额
			parentVO.setDef17(htje);// 原合同金额
			// 热力模板
			parentVO.setDef70(htzje);// 合同总金额
			parentVO.setDef71(ljfkje);// 已付款金额
			parentVO.setDef41(sdz);// 审计金额
			if (StringUtils.isNotEmpty(sdz) && Double.parseDouble(sdz) > 0) {
				parentVO.setDef38((Double.parseDouble(ljfkje) / Double
						.parseDouble(sdz)) * 100 + "");// 已付款比例
			} else {
				parentVO.setDef38((Double.parseDouble(ljfkje) / Double
						.parseDouble(htzje)) * 100 + "");// 已付款比例
			}
			parentVO.setDef72(ljpfje);// 已开发票金额
			parentVO.setStatus(VOStatus.NEW);
			fkd.setParentVO(parentVO);
			/*
			 * for (int i = 0; i < mxVOs.length; i++) { // ContrWorksVO
			 * htChildvo = bvos[i];// 合同基本 PayBillItemVO mxVO = mxVOs[i];//
			 * 付款单明细 mxVO.setDef1(htje);// 合同金额 mxVO.setDef3(ljfkje);// 已付款金额
			 * mxVO.setDef9(ljfkje);// 已开发票金额 mxVO.setDef35(ljfkje);// 本次开发票金额
			 * mxVO.setStatus(VOStatus.NEW); mxVOs[i] = mxVO; }
			 */
			PayBillItemVO mxVO = mxVOs[0];// 付款单明细
			mxVO.setDef1(htje);// 合同金额
			mxVO.setDef3(ljfkje);// 已付款金额
			mxVO.setDef6(parentVO.getDef38());// 已付款比例
			mxVO.setDef9(ljpfje);// 已开发票金额
			mxVO.setDef2(sdz);// 审计金额
			// mxVO.setDef35(ljfkje);// 本次开发票金额
			mxVO.setStatus(VOStatus.NEW);
			PayBillItemVO[] newmxVOs = new PayBillItemVO[] { mxVO };
			fkd.setChildrenVO(newmxVOs);
		}

		return fkd;
	}

	private String getNumValue(String code) {
		if (StringUtils.isEmpty(code) || "null".equals(code)
				|| "~".equals(code)) {
			code = "0";
		}
		return code;
	}

	private Object[] getHTMoney(String bill_code, String pk_org)
			throws DAOException {
		// TODO Auto-generated method stub
		StringBuffer str = new StringBuffer();
		str.append("select * from view_gchtbyht_qd where htbm = '" + bill_code
				+ "' and pk_org = '" + pk_org + "'");
		List<Object[]> bxls = getDao.query(str.toString());
		if (bxls != null && bxls.size() > 0 && bxls.get(0) != null) {
			return bxls.get(0);
		}
		return null;
	}

	public BillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(BillForm billForm) {
		this.billForm = billForm;
	}
}
