package nc.ui.ct.purdaily.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.funcnode.ui.FuncletInitData;
import nc.funcnode.ui.FuncletWindowLauncher;
import nc.itf.ct.purdaily.ICtPayPlanQuery;
import nc.itf.ct.purdaily.IPurdailyMaintain;
import nc.itf.scmpub.reference.uap.group.SysInitGroupQuery;
import nc.itf.uap.bbd.func.IFuncRegisterQueryService;
import nc.sfbase.client.ClientToolKit;
import nc.ui.ct.purdaily.dialog.QualityDialog;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.scmpub.action.SCMActionInitializer;
import nc.ui.uif2.NCAction;
import nc.vo.ct.enumeration.CtFlowEnum;
import nc.vo.ct.purdaily.entity.AggCtPuVO;
import nc.vo.ct.purdaily.entity.AggPayPlanVO;
import nc.vo.ct.purdaily.entity.CtPuBVO;
import nc.vo.ct.purdaily.entity.CtPuVO;
import nc.vo.ct.purdaily.entity.PayPlanVO;
import nc.vo.ct.purdaily.entity.PayPlanViewVO;
import nc.vo.ct.rule.PayPlanDataUtil;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.pub.MathTool;
import nc.vo.sm.funcreg.FuncRegisterVO;

import org.apache.commons.lang.ArrayUtils;

public class PuPayPlanNewAction extends NCAction {
	private static final long serialVersionUID = 3205738793876707779L;
	private BillForm billForm;
	private BillManageModel model;

	public PuPayPlanNewAction() {
		SCMActionInitializer.initializeAction(this, "PayPlan");
		setBtnName("项目付款计划");
	}

	@SuppressWarnings("restriction")
	public void doAction(ActionEvent e) throws Exception {
		AggCtPuVO aggCtPuVO = (AggCtPuVO)getModel().getSelectedData();
		QualityDialog dialog = new QualityDialog(aggCtPuVO);
		dialog.showModal();
		if (dialog.getResult() == 1) {
			return ;
		}
	}

	public BillForm getBillForm() {
		return this.billForm;
	}

	public BillManageModel getModel() {
		return this.model;
	}

	public void setBillForm(BillForm billForm) {
		this.billForm = billForm;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	private void checkBracketOrder(AggCtPuVO aggvo) {
		UFBoolean bBracketOrder = aggvo.getParentVO().getBbracketOrder();
		if (bBracketOrder.booleanValue()) {
			ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl
					.getNCLangRes().getStrByID("4020003_0", "04020003-0305"));
		}
	}

	private void editOpen() throws Exception {
		AggCtPuVO vo = (AggCtPuVO) getBillForm().getBillCardPanel()
				.getBillValueVO(AggCtPuVO.class.getName(),
						CtPuVO.class.getName(), CtPuBVO.class.getName());

		CtPuVO headVO = vo.getParentVO();
		if (null == headVO) {
			MessageDialog.showHintDlg(
					getBillForm(),
					NCLangRes4VoTransl.getNCLangRes().getStrByID("4020003_0",
							"04020003-0056"),
					NCLangRes4VoTransl.getNCLangRes().getStrByID("4020003_0",
							"04020003-0057"));

			return;
		}

		if (ArrayUtils.isEmpty(vo.getCtPuBVO())) {
			MessageDialog.showHintDlg(
					getBillForm(),
					NCLangRes4VoTransl.getNCLangRes().getStrByID("4020003_0",
							"04020003-0056"),
					NCLangRes4VoTransl.getNCLangRes().getStrByID("4020003_0",
							"04020003-0058"));
			return;
		}

		checkBracketOrder(vo);
		UFDouble tmny = UFDouble.ZERO_DBL;
		for (CtPuBVO bodyVO : vo.getCtPuBVO()) {
			tmny = MathTool.add(tmny, bodyVO.getNorigtaxmny());
		}
		vo.getParentVO().setNtotalorigmny(tmny);
		AggCtPuVO[] vos = { vo };		
		//TODO create by zwh
		//1.获取表体VO
		CtPuBVO[] ctPuBVO = (CtPuBVO[]) vo.getChildrenVO();
		Map<String, UFDouble> cbprojectidMaps = new HashMap<String, UFDouble>();
		Map<String, PayPlanVO> payPlanVOMaps = new HashMap<String, PayPlanVO>();
		for(int i = 0;i < ctPuBVO.length; i++){
			String cbprojectid = ctPuBVO[i].getCbprojectid();
			PayPlanVO payPlanVO = new PayPlanVO();
			if(null != payPlanVOMaps.get(cbprojectid)){
//				payPlanVO.set
//				UFDouble a = cbprojectidMaps.get(cbprojectid);
				UFDouble b = ctPuBVO[i].getNorigmny();
				payPlanVOMaps.put(cbprojectid, payPlanVO);
			}else{
				payPlanVOMaps.put(cbprojectid, payPlanVO);
			}
			if(null != cbprojectidMaps.get(cbprojectid)){
				UFDouble a = cbprojectidMaps.get(cbprojectid);
				UFDouble b = ctPuBVO[i].getNorigmny();
				cbprojectidMaps.put(cbprojectid, a.add(b));
			}else{
				cbprojectidMaps.put(cbprojectid, ctPuBVO[i].getNorigmny());
			}
		}
		
		//2.根据表体VO生成付款计划
		//TODO end
		PayPlanVO[] payplanVOs = PayPlanDataUtil.getPayPlanData(vos);
		PayPlanViewVO[] views = new PayPlanViewVO[payplanVOs.length];
		for (int i = 0; i < payplanVOs.length; i++) {
			views[i] = new PayPlanViewVO();
			views[i].setVO(headVO);
			views[i].setVO(payplanVOs[i]);
		}
		openPayPlanDLG(views);
	}

	@SuppressWarnings("restriction")
	private void notEditOpen() throws Exception {
		AggCtPuVO vo = (AggCtPuVO) getModel().getSelectedData();
		if (null == vo) {
			MessageDialog.showHintDlg(
					getModel().getContext().getEntranceUI(),
					NCLangRes4VoTransl.getNCLangRes().getStrByID("4020003_0",
							"04020003-0056"), NCLangRes4VoTransl.getNCLangRes()
							.getStrByID("4020003_0", "04020003-0059"));
			return;
		}
		checkBracketOrder(vo);
		String pk_ct = vo.getParentVO().getPk_ct_pu();
		ICtPayPlanQuery service = (ICtPayPlanQuery) NCLocator.getInstance()
				.lookup(ICtPayPlanQuery.class);
		AggPayPlanVO[] payplanVOs = service
				.queryPayPlanVOs(new String[] { pk_ct });
		//TODO create by zwh
		//1.获取表体VO
		CtPuBVO[] ctPuBVO = (CtPuBVO[]) vo.getChildrenVO();
		Map<String, UFDouble> cbprojectidMaps = new HashMap<String, UFDouble>();
		Map<String, PayPlanVO> payPlanVOMaps = new HashMap<String, PayPlanVO>();
		for(int i = 0;i < ctPuBVO.length; i++){
			String cbprojectid = ctPuBVO[i].getCbprojectid();
			PayPlanVO payPlanVO = new PayPlanVO();
			payPlanVO.setCcurrencyid("1002Z0100000000001K1");//本币币种
			payPlanVO.setCorigcurrencyid("1002Z0100000000001K1");//币种
			payPlanVO.setDr(0);//
			payPlanVO.setNaccumpaymny(UFDouble.ZERO_DBL);//累计付款本币金额 
			payPlanVO.setNaccumpayorgmny(UFDouble.ZERO_DBL);//累计付款金额
			payPlanVO.setNexchangerate(UFDouble.ONE_DBL);//折本汇率 
			payPlanVO.setNmny(UFDouble.ZERO_DBL);//本币金额 
			payPlanVO.setNrate(UFDouble.ZERO_DBL);//比率（%）
			payPlanVO.setPk_ct_pu(ctPuBVO[i].getPk_ct_pu());//本币金额 
			payPlanVO.setPk_financeorg(ctPuBVO[i].getPk_financeorg());//应付财务组织原始版  
			payPlanVO.setPk_financeorg_v(ctPuBVO[i].getPk_financeorg_v());//应付财务组织  
			payPlanVO.setPk_group(ctPuBVO[i].getPk_group());//集团
			if(null != payPlanVOMaps.get(cbprojectid)){
				UFDouble a = payPlanVOMaps.get(cbprojectid).getNorigmny();
				UFDouble b = ctPuBVO[i].getNorigmny();
				payPlanVO.setNorigmny(a.add(b).setScale(2,UFDouble.ROUND_CEILING));
				payPlanVO.setNtotalorigmny(UFDouble.ZERO_DBL);//付款金额 
			}else{
				payPlanVO.setCrowno((i+1)*10 +"");//行号
				payPlanVO.setNorigmny(ctPuBVO[i].getNorigmny());//金额 norigmny 
				payPlanVO.setNtotalorigmny(UFDouble.ZERO_DBL);//付款金额 
			}
			payPlanVOMaps.put(cbprojectid, payPlanVO);
		}
		PayPlanVO[] payPlanVOs= new PayPlanVO[payPlanVOMaps.size()];
		int index = 0;
		for(PayPlanVO value : payPlanVOMaps.values()){
			value.setCrowno(((index+1) *10) +"");
			payPlanVOs[index] = value;
			index++;
		}
		payplanVOs[0].setChildrenVO(payPlanVOs);
		//2.根据表体VO生成付款计划
		//TODO end
		PayPlanViewVO[] views = AggPayPlanVO.getPayPlanViewVO(payplanVOs);
		openPayPlanDLG(views);
		if (CtFlowEnum.VALIDATE.value().equals(
				vo.getParentVO().getFstatusflag())) {
			IPurdailyMaintain orderquery = (IPurdailyMaintain) NCLocator
					.getInstance().lookup(IPurdailyMaintain.class);
			this.billForm.getModel().directlyUpdate(
					orderquery.queryCtPuVoByIds(new String[] { pk_ct }));
		}
	}

	@SuppressWarnings("restriction")
	private void openPayPlanDLG(PayPlanViewVO[] views) {
		if (ArrayUtils.isEmpty(views)) {
			MessageDialog.showHintDlg(
					getModel().getContext().getEntranceUI(),
					NCLangRes4VoTransl.getNCLangRes().getStrByID("4020003_0",
							"04020003-0056"), NCLangRes4VoTransl.getNCLangRes()
							.getStrByID("4020003_0", "04020003-0060"));
			return;
		}
		if (!SysInitGroupQuery.isAPEnabled()) {
			MessageDialog.showHintDlg(
					getModel().getContext().getEntranceUI(),
					NCLangRes4VoTransl.getNCLangRes().getStrByID("4020003_0",
							"04020003-0056"), NCLangRes4VoTransl.getNCLangRes()
							.getStrByID("4020003_0", "04020003-0061"));
			return;
		}

		FuncletInitData initData = new FuncletInitData();
		initData.setInitData(views);
		initData.setInitType(21);

		IFuncRegisterQueryService service = (IFuncRegisterQueryService) NCLocator
				.getInstance().lookup(IFuncRegisterQueryService.class);

		FuncRegisterVO funvo = null;
		try {
			funvo = service.queryFunctionByCode("40203016");
		} catch (BusinessException e) {
			ExceptionUtils.wrappException(e);
		}
		Dimension size = ClientToolKit.getUserClientSize();
		size.setSize(size.width * 0.9D, size.height * 0.7D);
		FuncletWindowLauncher.openFuncNodeDialog(WorkbenchEnvironment
				.getInstance().getWorkbench(), funvo, initData, null, true,
				true, size, true);
	}

	protected boolean isActionEnable() {
		AggCtPuVO vo = (AggCtPuVO) this.model.getSelectedData();
		if (null == vo) {
			return false;
		}

		if ((((Integer) CtFlowEnum.VALIDATE.value()).equals(vo.getParentVO()
				.getFstatusflag()))
				|| (((Integer) CtFlowEnum.FROZEN.value()).equals(vo
						.getParentVO().getFstatusflag()))
				|| (((Integer) CtFlowEnum.TERMINATE.value()).equals(vo
						.getParentVO().getFstatusflag()))) {

			return true;
		}

		UFBoolean bBracketOrder = vo.getParentVO().getBbracketOrder();
		if ((null != bBracketOrder) && (bBracketOrder.booleanValue())) {
			return false;
		}
		return false;
	}
}