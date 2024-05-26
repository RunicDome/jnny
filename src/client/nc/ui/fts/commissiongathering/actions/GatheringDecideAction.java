package nc.ui.fts.commissiongathering.actions;

import java.awt.event.ActionEvent;

import nc.bs.logging.Logger;
import nc.ui.fts.commission.state.CommissionAppUiState;
import nc.ui.fts.commissiongathering.util.GatheringDefaultValueUtil;
import nc.ui.fts.pub.handler.IBillDateHandler;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.tm.tbb.TMNtbInfoUtil;
import nc.ui.tm.workflow.model.WorkFlowBillManageModel;
import nc.ui.tmpub.action.CommonActionInitializer;
import nc.ui.tmpub.field.affect.util.FieldEventUIUtil;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.fts.commissiongathering.AggGatheringVO;
import nc.vo.fts.commissiongathering.GatherFundVO;
import nc.vo.fts.commissiongathering.GatheringVO;
import nc.vo.fts.commissiongathering.util.AggGatheringVOUtil;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.tmpub.util.ArrayUtil;
import nc.vo.tmpub.util.VOUtil;
import nc.vo.uif2.LoginContext;

//委托收款经办保存默认收款银行
@SuppressWarnings({ "restriction", "unused", "deprecation" })
public class GatheringDecideAction extends GatheringBaseAction {
	private static final long serialVersionUID = -2082985038459343864L;
	private IBillDateHandler<AggGatheringVO> billDateHandler;
	private BillForm fundBillForm;

	public GatheringDecideAction() {
		CommonActionInitializer.initializeAction(this, "Decide");
	}

	public void doAction(ActionEvent e) throws Exception {
		VOUtil.printMessage(NCLangRes4VoTransl.getNCLangRes().getStrByID(
				"3630commission_0", "03630commission-0010"));
		setIgnoreInterceptor(true);
		Object[] validatedDatas = getSelectedDatas();
		if (!ArrayUtil.isNull(validatedDatas)) {
			if (isSelectedManyRows()) {
				Object retObj = getWorkFlowModel().signalWorkFlow(
						validatedDatas);
				TMNtbInfoUtil.showNtbMessage(retObj, getModel().getContext());
			} else {
				AggGatheringVO selectedAggVO = (AggGatheringVO) getModel()
						.getSelectedData();
				GatheringVO hVO = (GatheringVO) selectedAggVO.getParentVO();
				
				if (getBillDateHandler().setActionDateWithWarning(
						getFundBillForm(), selectedAggVO,
						new String[] { "decidedate" })) {
					System.out.println("当前组织："+hVO.getPk_org());
					Logger.error("当前组织："+hVO.getPk_org());
					// 固定组织301 工程集团
					if(hVO.getPk_org().equals("0001A110000000000KYH")){
						String[] s = selectedAggVO.getTableCodes();
						GatherFundVO[] zjvos = (GatherFundVO[]) selectedAggVO.getTableVO("gatheringfund");
						System.out.println("资金信息子表条数："+zjvos.length);
						Logger.error("资金信息子表条数："+zjvos.length);
						for(int i = 0;i < zjvos.length;i++){
							// zjvos[i].setPk_bankaccount_r("1001A1100000001CKAE2");// 设置收款银行
							getFundBillForm().getBillCardPanel().setBodyValueAt("1001A1100000001CKAE2", i, "pk_bankaccount_r", "gatheringfund");
						}
					}
					
					getFundBillForm().getBillCardPanel().setTailItem(
							"decideuser",
							getModel().getContext().getPk_loginUser());
					getWorkFlowModel().setAppUiState(
							CommissionAppUiState.DECIDE);
					getWorkFlowModel().setWorkFlowModelStatus(0);
					setIgnoreInterceptor(false);

					FieldEventUIUtil.fireUILoadComplete(getWorkFlowModel());
				}
				
			}
		}
		VOUtil.printMessage(NCLangRes4VoTransl.getNCLangRes().getStrByID(
				"3630commission_0", "03630commission-0011"));
	}

	protected Object setInfoBeforeSelected(Object obj) {
		AggGatheringVO modelAggVO = (AggGatheringVO) obj;
		AggGatheringVO cloneAggVO = AggGatheringVOUtil.clone(modelAggVO);
		GatheringVO parentVO = (GatheringVO) cloneAggVO.getParentVO();
		String loginUser = getModel().getContext().getPk_loginUser();

		parentVO.setDecideuser(loginUser);

		parentVO.setModifier(loginUser);
		parentVO.setCurroperator(loginUser);
		GatheringDefaultValueUtil.setAbstractBillBusiDate(cloneAggVO,
				modelAggVO);
		
		return cloneAggVO;
	}

	public IBillDateHandler<AggGatheringVO> getBillDateHandler() {
		return this.billDateHandler;
	}

	public void setBillDateHandler(
			IBillDateHandler<AggGatheringVO> billDateHandler) {
		this.billDateHandler = billDateHandler;
	}

	public BillForm getFundBillForm() {
		return this.fundBillForm;
	}

	public void setFundBillForm(BillForm fundBillForm) {
		this.fundBillForm = fundBillForm;
	}
}
