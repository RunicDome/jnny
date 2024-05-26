package nc.ui.ct.purdaily.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.funcnode.ui.FuncletInitData;
import nc.funcnode.ui.FuncletWindowLauncher;
import nc.itf.ct.purdaily.ICtPayPlanQuery;
import nc.itf.scmpub.reference.uap.pf.PfServiceScmUtil;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.sfbase.client.ClientToolKit;
import nc.ui.ml.NCLangRes;
import nc.ui.mmf.framework.dialog.general.GeneralDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.component.ButtonPanel;
import nc.ui.pubapp.bill.BillCardPanel;
import nc.ui.pubapp.util.CardPanelValueUtils;
import nc.ui.scmf.payplan.action.AggPayBillVOUtils;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.arap.pay.AggPayBillVO;
import nc.vo.arap.pay.PayBillItemVO;
import nc.vo.arap.pay.PayBillVO;
import nc.vo.ct.purdaily.entity.AggCtPuVO;
import nc.vo.ct.purdaily.entity.AggPayPlanVO;
import nc.vo.ct.purdaily.entity.CtPuBVO;
import nc.vo.ct.purdaily.entity.PayPlanViewVO;
import nc.vo.fct.uitl.MathUtils;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pmpub.project.ProjectHeadVO;
import nc.vo.pu.pub.util.ArrayUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.pub.MathTool;
import nc.vo.scmpub.payterm.pay.AbstractPayPlanViewVO;
import nc.vo.scmpub.res.billtype.CTBillType;
import nc.vo.sm.funcreg.FuncRegisterVO;

/**
 * 工艺要求dialog
 * 
 * @author chengkh
 * 
 */
@SuppressWarnings({ "serial", "restriction" })
public class QualityDialog extends GeneralDialog implements ActionListener,
		BillEditListener {

	private BillCardPanel billCardPanel;
	private UIPanel contentPanel = null;
	private ButtonPanel buttonPanel;
	private AggCtPuVO aggCtPuVO;
	private OnhandVO[] onhandVOsBak;

	public QualityDialog(AggCtPuVO aggCtPuVO) {
		this.setAggCtPuVO(aggCtPuVO);
		initUI();
	}

	public void initUI() {
		setTitle("项目付款");
		setLayout(new BorderLayout());
		this.getContentPane().add(getContentPanel(), BorderLayout.CENTER);
		this.getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
		setSize(1380, 650);
		initListener();
		this.setResizable(true);
		setHeadTailData();
		setBodyData();
		this.getBillCardPanel().setBodyMenuShow(true);
	}

	private void setHeadTailData() {
		this.getBillCardPanel().getHeadTailItem("vbillcode").setValue(aggCtPuVO.getParentVO().getVbillcode());
		this.getBillCardPanel().getHeadTailItem("subscribedate").setValue(aggCtPuVO.getParentVO().getSubscribedate());
		this.getBillCardPanel().getHeadTailItem("cvendorid").setValue(aggCtPuVO.getParentVO().getCvendorid());
		
		String primaryKey = aggCtPuVO.getPrimaryKey();
		String sql = " select case when sum(LJFKJE) is null then 0.00 else sum(LJFKJE) end ljfkje  from v_cghtwh_xmfkjh where pk_ct_pu = '"+primaryKey+"' ";
		IUAPQueryBS iuapQueryBS = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		try {
			Object ljfkje = iuapQueryBS.executeQuery(sql, new ColumnProcessor());
			this.getBillCardPanel().getHeadTailItem("hvdef1").setValue(ljfkje);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		this.getBillCardPanel().getHeadItem("payment_amount").setEdit(true);
	}

	private void setBodyData() {
		// TODO create by zwh
		// 1.获取表体VO
		CtPuBVO[] ctPuBVO = (CtPuBVO[]) aggCtPuVO.getChildrenVO();
		String hvdef17 = aggCtPuVO.getParentVO().getVdef17();
		UFDouble ntotaltaxmny = aggCtPuVO.getParentVO().getNtotaltaxmny();// 表头价税合计
		UFDouble bvdef17 = UFDouble.ONE_DBL;
		if (null != hvdef17) {
			bvdef17 = new UFDouble(hvdef17).div(ntotaltaxmny);
		}
		Map<String, OnhandVO> onhandVOMaps = new HashMap<String, OnhandVO>();
		// 合同金额判断取价税合计还是自订义20
		boolean flag = getFlag(ctPuBVO);
		for (int i = 0; i < ctPuBVO.length; i++) {
			String cbprojectid = ctPuBVO[i].getCbprojectid();
			OnhandVO onhandVO = new OnhandVO();
			onhandVO.setPk_financeorg_v(ctPuBVO[i].getPk_financeorg_v());
			onhandVO.setPk_ct_pu(ctPuBVO[i].getPk_ct_pu());
			onhandVO.setPk_ct_pu_b(ctPuBVO[i].getPk_ct_pu_b());
			onhandVO.setCbprojectid(ctPuBVO[i].getCbprojectid());
			try {
				ProjectHeadVO projectHeadVO = (ProjectHeadVO) HYPubBO_Client
						.queryByPrimaryKey(ProjectHeadVO.class,
								ctPuBVO[i].getCbprojectid());
				onhandVO.setCbprojectid_code(projectHeadVO.getProject_code());
				onhandVO.setCbprojectid_name(projectHeadVO.getProject_name());
			} catch (UifException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			onhandVO.setThis_nmny(UFDouble.ZERO_DBL);// 付款金额
			if (null != onhandVOMaps.get(cbprojectid)) {
				UFDouble a = onhandVOMaps.get(cbprojectid).getNorigmny() == null ? new UFDouble(0.0) : onhandVOMaps.get(cbprojectid).getNorigmny();
				UFDouble b = UFDouble.ZERO_DBL;
				if (!flag) {
					b = ctPuBVO[i].getNorigtaxmny() == null ? new UFDouble(0.0) : ctPuBVO[i].getNorigtaxmny();
				} else {
					b = new UFDouble(ctPuBVO[i].getVbdef20() == null ? "0.0" : ctPuBVO[i].getVbdef20());
				}
				onhandVO.setNorigmny(a.add(b).setScale(2,
						UFDouble.ROUND_CEILING));
				onhandVO.setThis_nmny(UFDouble.ZERO_DBL);// 付款金额
				if (null != onhandVOMaps.get(cbprojectid).getAll_nmny()) {
					if (null != ctPuBVO[i].getVbdef2()) {
						onhandVO.setAll_nmny(onhandVOMaps.get(cbprojectid)
								.getAll_nmny()
								.add(new UFDouble(ctPuBVO[i].getVbdef2())));
					}
				}
			} else {
				if (!flag) {
					onhandVO.setNorigmny(ctPuBVO[i].getNorigtaxmny());// 金额
																		// norigmny
				} else {
					onhandVO.setNorigmny(new UFDouble(ctPuBVO[i].getVbdef20()));// 金额
																				// norigmny
				}
				if (null != ctPuBVO[i].getVbdef2()) {
					onhandVO.setAll_nmny(new UFDouble(ctPuBVO[i].getVbdef2()));
				}
			}
			onhandVOMaps.put(cbprojectid, onhandVO);
		}
		OnhandVO[] onhandVOs = new OnhandVO[onhandVOMaps.size()];
		setOnhandVOsBak(onhandVOs);
		int index = 0;
		for (OnhandVO value : onhandVOMaps.values()) {
			value.setCrowno(((index + 1) * 10) + "");
			onhandVOs[index] = value;
			index++;
			if (null != value.getAll_nmny()) {
				value.setNo_nmny(value.getNorigmny().sub(value.getAll_nmny()));
			} else {
				value.setNo_nmny(value.getNorigmny());
			}
			value.setBdev1(value.getNorigmny().multiply(bvdef17).setScale(2, UFDouble.ROUND_HALF_UP));
		}
		UFDouble weiCha = UFDouble.ZERO_DBL;
		for (OnhandVO value : onhandVOMaps.values()) {
			weiCha = weiCha.add(value.getBdev1()).setScale(2, UFDouble.ROUND_HALF_UP);
		}
		if (null != hvdef17) {
			if(new UFDouble(hvdef17).compareTo(weiCha) > 0){
				onhandVOMaps.entrySet().iterator().next().getValue().setBdev1(onhandVOMaps.entrySet().iterator().next().getValue().getBdev1().add(
					new UFDouble(hvdef17).sub(weiCha)).setScale(2, UFDouble.ROUND_HALF_UP));
			}
		}
		this.getBillCardPanel().getBillModel().setBodyDataVO(onhandVOs);
	}

	private boolean getFlag(CtPuBVO[] ctPuBVO) {
		for (int i = 0; i < ctPuBVO.length; i++) {
			if (null == ctPuBVO[i].getVbdef20() || Double.parseDouble(ctPuBVO[i].getVbdef20() + "") == ctPuBVO[i].getNastnum().toDouble()) {
				return false;
			}
		}
		return true;
	}

	private UIPanel getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new UIPanel(new BorderLayout());
			contentPanel.add(getBillCardPanel(), BorderLayout.CENTER);
		}
		billCardPanel.getBillModel().loadLoadRelationItemValue();
		return billCardPanel;
	}

	private BillCardPanel getBillCardPanel() {
		if (billCardPanel == null) {
			billCardPanel = new BillCardPanel();
			String tempVO = "";
			try {
				tempVO = (String) HYPubBO_Client.findColValue(
						"pub_billtemplet", "pk_billtemplet",
						"bill_templetname='40203017'");
				// System.out.println(tempVO);
			} catch (UifException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			billCardPanel.loadTemplet(tempVO);
		}
		billCardPanel.getBillModel().loadLoadRelationItemValue();
		return billCardPanel;
	}

	/**
	 * 初始化监听
	 */
	private void initListener() {
		getButtonPanel().getBtnOK().addActionListener(this);
		getButtonPanel().getBtnCancel().addActionListener(this);
		getBillCardPanel().addBillEditListenerHeadTail(this);
		getBillCardPanel().getBodyPanel().addEditListener(this);
	}

	@Override
	public void closeOK() {
		try {
			String[] userObj = { AggPayBillVO.class.getName(),
					PayBillVO.class.getName(), PayBillItemVO.class.getName() };
			AggPayPlanVO[] aggPayPlanVO = getPuPayPlanVo();
			PayPlanViewVO[] views = AggPayPlanVO.getPayPlanViewVO(aggPayPlanVO);
			List<Object> list = new ArrayList();
			boolean isAllZero = setNcanpayMny(views[0], list);
			AggregatedValueObject[] destVOs = getDestVOs(list
					.toArray(new Object[list.size()]));
			AggregatedValueObject[] newVOs = formatAggVo(destVOs);
			openPayDlg(newVOs);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		super.closeOK();
	}

	private AggregatedValueObject[] formatAggVo(
			AggregatedValueObject[] aggregatedValueObject) {
		PayBillItemVO[] bvos = (PayBillItemVO[]) aggregatedValueObject[0]
				.getChildrenVO();
		OnhandVO[] onhandVOs = (OnhandVO[]) getBillCardPanel().getBillModel()
				.getBodyValueVOs(OnhandVO.class.getName());
		PayBillItemVO[] newBvos = new PayBillItemVO[onhandVOs.length];
		for (int i = 0; i < onhandVOs.length; i++) {
			PayBillItemVO temp = new PayBillItemVO();
			temp = (PayBillItemVO) bvos[0].clone();
			// temp.setSrc_itemid(onhandVOs[i].getPk_ct_pu_b());
			temp.setProject(onhandVOs[i].getCbprojectid());
			temp.setMoney_de(onhandVOs[i].getThis_nmny());
			temp.setLocal_money_de(onhandVOs[i].getThis_nmny());
			// temp.setDef1(onhandVOs[i].getNorigmny().toString());
			temp.setDef1((onhandVOs[i].getBdev1() == null ? UFDouble.ZERO_DBL:onhandVOs[i].getBdev1()) + "");
			temp.setDef3((onhandVOs[i].getBdev2() == null ? UFDouble.ZERO_DBL:onhandVOs[i].getBdev2()) +"");
			temp.setDef9((onhandVOs[i].getBdev3() == null ? UFDouble.ZERO_DBL:onhandVOs[i].getBdev3()) + "");
			newBvos[i] = temp;
		}
		aggregatedValueObject[0].setChildrenVO(newBvos);
		return aggregatedValueObject;
	}

	protected AggregatedValueObject[] getDestVOs(Object[] objs) {
		PayPlanViewVO[] views = (PayPlanViewVO[]) ArrayUtil
				.convertArrayType(objs);
		AggPayPlanVO[] vos = PayPlanViewVO.getAggPayPlanVO(views);
		AggregatedValueObject[] destVOs = null;
		destVOs = PfServiceScmUtil.exeVOChangeByBillItfDef(
				CTBillType.PurDaily.getCode(), "D3", vos);
		destVOs[0].getParentVO().setAttributeValue("def29", UFBoolean.TRUE);
		return convertVOs(destVOs);
	}

	protected AggregatedValueObject[] convertVOs(AggregatedValueObject[] destVOs) {
		if (null == destVOs) {
			return null;
		}
		return AggPayBillVOUtils.convertAggPayBillVOs(destVOs);
	}

	private boolean setNcanpayMny(AbstractPayPlanViewVO vo, List<Object> list) {
		if ((UFDouble.ZERO_DBL.equals(vo.getNorigmny()))
				|| (MathTool.greaterThan(UFDouble.ZERO_DBL, vo.getNorigmny()))) {
			String rows = vo.getCrowno();
			ExceptionUtils.wrappBusinessException(NCLangRes.getInstance()
					.getStrByID("4001001_0", "04001001-0266", null,
							new String[] { rows }));
		}

		UFDouble ncanpayorgmny = MathTool.sub(vo.getNorigmny(),
				vo.getNaccumpayorgmny());

		if (MathTool.compareTo(ncanpayorgmny, UFDouble.ZERO_DBL) <= 0) {
			// return false;
		}
		vo.setNorigmny(ncanpayorgmny);
		vo.setNmny(MathTool.sub(vo.getNmny(), vo.getNaccumpaymny()));
		list.add(vo);
		return true;
	}

	private AggPayPlanVO[] getPuPayPlanVo() {
		String pk_ct = aggCtPuVO.getParentVO().getPk_ct_pu();
		ICtPayPlanQuery service = (ICtPayPlanQuery) NCLocator.getInstance()
				.lookup(ICtPayPlanQuery.class);
		try {
			AggPayPlanVO[] payplanVOs = service
					.queryPayPlanVOs(new String[] { pk_ct });
			return payplanVOs;
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void openPayDlg(AggregatedValueObject[] destVOs) {
		FuncletInitData initData = null;
		initData = new FuncletInitData();
		initData.setInitType(0);
		Logger.error("destVOs长度："+destVOs.length);
		double bcfkje = 0.0;
		for(int i=0;i<destVOs.length;i++){
			AggregatedValueObject vo = destVOs[i];
			double htzje = 0;//合同总金额
    		double yfkje = 0;//已付款金额
    		double ykfpje = 0;//已开发票金额
			PayBillVO ctHeadVO = (PayBillVO) vo.getParentVO();
			PayBillItemVO[] itemVOs = (PayBillItemVO[]) vo.getChildrenVO();
			PayBillItemVO[] newitemVOs = new PayBillItemVO[itemVOs.length];
			double yfje = 0.0;
			for(int j=0;j<itemVOs.length;j++){
				PayBillItemVO itemVO = itemVOs[j];
				PayBillItemVO newitemVO = itemVO;
				String szje = itemVO.getDef1();
    			String syfk = itemVO.getDef3();
    			String sykfp = itemVO.getDef9();
    			
    			if(szje != null && !"".equals(szje) && !"null".equals(szje)){
    				htzje += Double.parseDouble(szje);
    			}
				if(syfk != null && !"".equals(syfk) && !"null".equals(syfk)){
					yfkje += Double.parseDouble(syfk);	
    			}
				if(sykfp != null && !"".equals(sykfp) && !"null".equals(sykfp)){
					ykfpje += Double.parseDouble(sykfp);
				}
				
				yfje += itemVO.getLocal_money_de().toDouble();
				//本次付款比例
				bcfkje += itemVO.getLocal_money_de().toDouble();
				Logger.error("本次付款金额："+itemVO.getLocal_money_de().toDouble());
				Logger.error("getDef1："+itemVO.getDef1());
				if(itemVO.getDef1()==null){
					Logger.error("合同金额为空！");
					return ;
				}
				newitemVO.setDef7((itemVO.getLocal_money_de().toDouble()/Double.parseDouble(itemVO.getDef1())*100)+"");
				//Def6==已付款比例
				newitemVO.setDef6((Double.parseDouble(itemVO.getDef3())/Double.parseDouble(itemVO.getDef1())*100) + "");
				//总付款比例
				if((Double.parseDouble(itemVO.getDef7())+Double.parseDouble(itemVO.getDef6()))>=100){
					newitemVO.setDef8("100");
				}else{
					newitemVO.setDef8((Double.parseDouble(itemVO.getDef7())+Double.parseDouble(itemVO.getDef6()))+"");
				}
				newitemVOs[j] = newitemVO;
			}
			Logger.error("合同总额："+htzje+"已付款金额："+yfkje+"已开发票金额："+ykfpje);
			ctHeadVO.setDef70(htzje + "");
			ctHeadVO.setDef71(yfkje + "");
			ctHeadVO.setDef72(ykfpje + "");
			ctHeadVO.setDef37(newitemVOs[0].getDef7());//本次付款比例
			ctHeadVO.setDef38(newitemVOs[0].getDef6());//已付款比例
			ctHeadVO.setDef39(newitemVOs[0].getDef8());//累计付款比例
			double yf = 0.0;
			if(ctHeadVO.getDef71()==null || "".equals(ctHeadVO.getDef71()) || "null".equals(ctHeadVO.getDef71())){
				yf = 0.0;
			}else{
				yf = Double.parseDouble(ctHeadVO.getDef71());
			}	
			Logger.error("yf=="+yf+"bcfkje=="+bcfkje);
			ctHeadVO.setDef40((bcfkje+yf)+"");//累计付款金额
			ctHeadVO.setLocal_money(new UFDouble(yfje));//应付金额
			
			vo.setParentVO(ctHeadVO);
			vo.setChildrenVO(newitemVOs);
			destVOs[i] = vo;
		}
		initData.setInitData(destVOs);
		FuncRegisterVO funvo = WorkbenchEnvironment.getInstance()
				.getFuncRegisterVO("20080EBR");
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

	@Override
	public void afterEdit(BillEditEvent arg0) {
		String key = arg0.getKey();
		if ("payment_amount".equals(key)) {
			paymentAmountChange(arg0);
			hvdef1Change(arg0);
		} else if ("proportion".equals(key)) {
			proportionChange(arg0);
		} else if ("this_nmny".equals(key)) {
			thisNmnyChange(arg0);
		} else if ("hvdef1".equals(key)) {
			//hvdef1Change(arg0);
		} else if ("hvdef2".equals(key)) {
			hvdef2Change(arg0);
		}
	}

	private void hvdef1Change(BillEditEvent arg0) {
		CardPanelValueUtils cardUtil = new CardPanelValueUtils(getBillCardPanel());
		UFDouble hvdef1 = cardUtil.getHeadTailUFDoubleValue("hvdef1");
		
		//UFDouble value = (UFDouble) arg0.getValue();
		
		UFDouble value =  hvdef1;
		UFDouble allMoney = UFDouble.ZERO_DBL;
		OnhandVO[] onhandVOs = (OnhandVO[]) getBillCardPanel().getBillModel().getBodyValueVOs(OnhandVO.class.getName());
		for (int i = 0; i < onhandVOs.length; i++) {
			allMoney = MathUtils.add(allMoney, onhandVOs[i].getBdev1());
		}
		UFDouble bili = value.div(allMoney, 6);
		UFDouble weiCha = UFDouble.ZERO_DBL;
		int i = 0;
		for (; i < onhandVOs.length; i++) {
			if (value.compareTo(allMoney) > 0) {
				// 付款金额大于实付金额
				getBillCardPanel().getHeadTailItem("hvdef1").setValue(UFDouble.ZERO_DBL);
				onhandVOs[i].setBdev2(UFDouble.ZERO_DBL);
			} else {
				weiCha = weiCha.add(onhandVOs[i].getBdev1().multiply(bili, 2));
				if(onhandVOs.length == 1){
					onhandVOs[i].setBdev2(hvdef1);
				}else{
					onhandVOs[i].setBdev2(bili.multiply(onhandVOs[i].getBdev1()));
				}
			}
		}
		if (value.compareTo(weiCha) > 0 && value.compareTo(allMoney) < 0) {
			onhandVOs[i - 1].setBdev2(onhandVOs[i - 1].getBdev2().add(value.sub(weiCha)));
		}
		getBillCardPanel().getBillModel().setBodyDataVO(onhandVOs);
		getBillCardPanel().updateUI();
	}

	private void hvdef2Change(BillEditEvent arg0) {
		UFDouble value = (UFDouble) arg0.getValue();
		UFDouble allMoney = UFDouble.ZERO_DBL;
		OnhandVO[] onhandVOs = (OnhandVO[]) getBillCardPanel().getBillModel()
				.getBodyValueVOs(OnhandVO.class.getName());
		for (int i = 0; i < onhandVOs.length; i++) {
			allMoney = MathUtils.add(allMoney, onhandVOs[i].getBdev1());
		}
		UFDouble bili = value.div(allMoney, 6);
		UFDouble weiCha = UFDouble.ZERO_DBL;
		int i = 0;
		for (; i < onhandVOs.length; i++) {
			if (value.compareTo(allMoney) > 0) {
				// 付款金额大于实付金额
				getBillCardPanel().getHeadTailItem("hvdef2").setValue(
						UFDouble.ZERO_DBL);
				onhandVOs[i].setBdev3(UFDouble.ZERO_DBL);
			} else {
				weiCha = weiCha.add(onhandVOs[i].getBdev1()
						.multiply(bili, 2));
				onhandVOs[i].setBdev3(bili.multiply(onhandVOs[i].getBdev1()));
			}
		}
		if (value.compareTo(weiCha) > 0 && value.compareTo(allMoney) < 0) {
			onhandVOs[i - 1].setBdev3(onhandVOs[i - 1].getBdev3().add(
					value.sub(weiCha)));
		}
		getBillCardPanel().getBillModel().setBodyDataVO(onhandVOs);
		getBillCardPanel().updateUI();
	}

	private void proportionChange(BillEditEvent arg0) {
		int row = arg0.getRow();
		UFDouble value = (UFDouble) arg0.getValue();
		UFDouble norigmny = (UFDouble) getBillCardPanel().getBodyValueAt(row,
				"norigmny");
		UFDouble no_amount = (UFDouble) getBillCardPanel().getBodyValueAt(row,
				"no_nmny");
		if (norigmny.multiply(value).div(new UFDouble(100))
				.compareTo(no_amount) > 0) {
			// 付款金额大于实付金额
			getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, row,
					"proportion");
		} else {
			getBillCardPanel().setBodyValueAt(
					norigmny.multiply(value).div(new UFDouble(100)), row,
					"this_nmny");
			// 计算总金额
			UFDouble weiCha = UFDouble.ZERO_DBL;
			int count = getBillCardPanel().getBillModel().getRowCount();
			for (int i = 0; i < count; i++) {
				UFDouble val = (UFDouble) getBillCardPanel().getBodyValueAt(i,
						"this_nmny");
				if (null != val) {
					weiCha = weiCha.add(val);
				}
			}
			getBillCardPanel().getHeadTailItem("payment_amount").setValue(
					weiCha);
		}
		getBillCardPanel().updateUI();
	}

	private void thisNmnyChange(BillEditEvent arg0) {
		int row = arg0.getRow();
		UFDouble value = (UFDouble) arg0.getValue();
		UFDouble norigmny = (UFDouble) getBillCardPanel().getBodyValueAt(row,
				"norigmny");
		UFDouble this_nmny = (UFDouble) getBillCardPanel().getBodyValueAt(row,
				"this_nmny");
		UFDouble no_amount = (UFDouble) getBillCardPanel().getBodyValueAt(row,
				"no_nmny");
		if (this_nmny.compareTo(no_amount) > 0) {
			// 付款金额大于实付金额
			getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, row,
					"proportion");
			getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, row,
					"this_nmny");
		} else {
			getBillCardPanel().setBodyValueAt(
					this_nmny.div(norigmny).multiply(new UFDouble(100)), row,
					"proportion");
			// 计算总金额
			UFDouble weiCha = UFDouble.ZERO_DBL;
			int count = getBillCardPanel().getBillModel().getRowCount();
			for (int i = 0; i < count; i++) {
				UFDouble val = (UFDouble) getBillCardPanel().getBodyValueAt(i,
						"this_nmny");
				if (null != val) {
					weiCha = weiCha.add(val);
				}
			}
			getBillCardPanel().getHeadTailItem("payment_amount").setValue(
					weiCha);
		}
	}

	private void paymentAmountChange(BillEditEvent arg0) {
		UFDouble value = (UFDouble) arg0.getValue();
		UFDouble allMoney = UFDouble.ZERO_DBL;
		OnhandVO[] onhandVOs = (OnhandVO[]) getBillCardPanel().getBillModel()
				.getBodyValueVOs(OnhandVO.class.getName());
		for (int i = 0; i < onhandVOs.length; i++) {
			allMoney = MathUtils.add(allMoney, onhandVOs[i].getNorigmny());
		}
		UFDouble bili = value.div(allMoney, 6);
		UFDouble weiCha = UFDouble.ZERO_DBL;
		int i = 0;
		for (; i < onhandVOs.length; i++) {
			if (onhandVOs[i].getNorigmny().multiply(bili, 2)
					.compareTo(onhandVOs[i].getNo_nmny()) > 0) {
				// 付款金额大于实付金额
				getBillCardPanel().getHeadTailItem("payment_amount").setValue(
						UFDouble.ZERO_DBL);
			} else {
				onhandVOs[i].setThis_nmny(onhandVOs[i].getNorigmny().multiply(
						bili, 2));
				weiCha = weiCha.add(onhandVOs[i].getNorigmny()
						.multiply(bili, 2));
				onhandVOs[i].setProportion(bili.multiply(new UFDouble(100)));
			}
		}
		if (onhandVOs[i - 1].getThis_nmny().add(value.sub(weiCha))
				.compareTo(onhandVOs[i - 1].getNo_nmny()) > 0) {
			getBillCardPanel().getHeadTailItem("payment_amount").setValue(
					UFDouble.ZERO_DBL);
		} else {
			onhandVOs[i - 1].setThis_nmny(onhandVOs[i - 1].getThis_nmny().add(
					value.sub(weiCha)));
		}
		getBillCardPanel().getBillModel().setBodyDataVO(onhandVOs);
		getBillCardPanel().updateUI();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == getButtonPanel().getBtnOK()) {
			closeOK();
		} else if (e.getSource() == getButtonPanel().getBtnCancel()) {
			closeCancel();
		}
	}

	public void setBillCardPanel(BillCardPanel billCardPanel) {
		this.billCardPanel = billCardPanel;
	}

	public void setContentPanel(UIPanel contentPanel) {
		this.contentPanel = contentPanel;
	}

	@Override
	public int showModal() {
		return super.showModal();
	}

	public ButtonPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new ButtonPanel();
		}
		return buttonPanel;
	}

	@Override
	public void bodyRowChange(BillEditEvent arg0) {
	}

	public AggCtPuVO getAggCtPuVO() {
		return aggCtPuVO;
	}

	public void setAggCtPuVO(AggCtPuVO aggCtPuVO) {
		this.aggCtPuVO = aggCtPuVO;
	}

	public OnhandVO[] getOnhandVOsBak() {
		return onhandVOsBak;
	}

	public void setOnhandVOsBak(OnhandVO[] onhandVOsBak) {
		this.onhandVOsBak = onhandVOsBak;
	}

}
