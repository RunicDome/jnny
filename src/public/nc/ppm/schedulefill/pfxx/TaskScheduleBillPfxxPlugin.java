package nc.ppm.schedulefill.pfxx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pfxx.ISwapContext;
import nc.bs.pfxx.plugin.AbstractPfxxPlugin;
import nc.bs.trade.business.HYPubBO;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.itf.aim.prv.IEquip;
import nc.itf.ppm.schedulefill.IShceduleFillInterface;
import nc.itf.uap.pf.IWorkflowMachine;
import nc.itf.uap.pf.IplatFormEntry;
import nc.pubimpl.so.pfxx.AbstractSOPfxxPlugin;
import nc.vo.aim.equip.EquipVO;
import nc.vo.pfxx.auxiliary.AggxsysregisterVO;
import nc.vo.pfxx.util.PfxxPluginUtils;
import nc.vo.ppm.schedulefill.TaskScheduleBillVO;
import nc.vo.ppm.schedulefill.TaskScheduleBodyVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.pubapp.pattern.pub.PubAppTool;

@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
public class TaskScheduleBillPfxxPlugin extends AbstractPfxxPlugin {
	public TaskScheduleBillPfxxPlugin() {
	}
	private HYPubBO hyPubBO;

	public HYPubBO getHyPubBO() {
		if (null == hyPubBO) {
			hyPubBO = new HYPubBO();
		}
		return hyPubBO;
	}

	public void setHyPubBO(HYPubBO hyPubBO) {
		this.hyPubBO = hyPubBO;
	}

	public List<IRule<AggregatedValueObject>> getCheckers() {
		List<IRule<AggregatedValueObject>> rules = new ArrayList();

		return null;
	}

	protected Object processBill(Object vo, ISwapContext swapContext,
			AggxsysregisterVO aggxsysvo) throws BusinessException {
		AggregatedValueObject resvo = (AggregatedValueObject) vo;

		String vopk = PfxxPluginUtils.queryBillPKBeforeSaveOrUpdate(
				swapContext.getBilltype(), swapContext.getDocID());

		AggregatedValueObject returnVO = null;

		checkBill(resvo);
		TaskScheduleBodyVO[] bodyvos = (TaskScheduleBodyVO[]) resvo.getChildrenVO();
	   for(TaskScheduleBodyVO temp:bodyvos){
		   Logger.error("比较值：" + temp.getPrefinishpercent().compareTo(temp.getTaskpercent()) + "");
		   if(temp.getPrefinishpercent().compareTo(temp.getTaskpercent()) >=0){
			   throw new BusinessException("上期实际完成不能小于等于累计完成！");
		   }
		   if(temp.getTaskpercent().toDouble() > 100 || temp.getTaskpercent().toDouble() == 0){
			   throw new BusinessException("实际完成不能大于100或者等于0！");
		   }
		   if(temp.getPk_wbsstate() == null){
			   throw new BusinessException("任务状态不能为空！");
		   }
	   }
		if (PubAppTool.isNull(vopk)) {
			resvo.getParentVO().setStatus(2);
			returnVO = insert(resvo);
			
		} else {

			returnVO = update(resvo, vopk);
		}

		vopk = returnVO.getParentVO().getPrimaryKey();
		TaskScheduleBillVO[] vos = { (TaskScheduleBillVO) returnVO };
		TaskScheduleBillVO[] comvos = NCLocator.getInstance().lookup(IShceduleFillInterface.class)
		.commitTaskScheduleBillVOS(vos);
		String adopter = (String) getHyPubBO().findColValue(
				"sys_config", "config_value",
				" config_key='oaadopt_cuser'");
		InvocationInfoProxy.getInstance().setUserId(adopter);
		HashMap hmPfExParams = new HashMap();
		WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator
				.getInstance().lookup(IWorkflowMachine.class))
				.checkWorkFlow("APPROVE", "4D24", comvos[0], hmPfExParams);
		if (worknoteVO != null) {
			worknoteVO.setChecknote("批准");
			worknoteVO.setApproveresult("Y");
		}
		getIplatFormEntry().processAction("APPROVE", "4D24",
				worknoteVO, comvos[0], null, getEparam());
/*		NCLocator.getInstance().lookup(IShceduleFillInterface.class)
		.approveTaskScheduleBillVOS(comvos, null);*/
		PfxxPluginUtils.addDocIDVsPKContrast(swapContext.getBilltype(),
				swapContext.getDocID(), vopk);

		return vopk;
	}

	private void checkBill(AggregatedValueObject vo) {
		if (getCheckers() != null) {
			for (IRule<AggregatedValueObject> checker : getCheckers()) {
				checker.process(new AggregatedValueObject[] { vo });
			}
		}
	}

	protected AggregatedValueObject insert(AggregatedValueObject vo) {
		TaskScheduleBillVO[] insertvo = { (TaskScheduleBillVO) vo };

		try {
			NCLocator.getInstance().lookup(IShceduleFillInterface.class)
					.insertTaskScheduleBillVOS(insertvo);

		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return vo;
	}

	protected AggregatedValueObject update(AggregatedValueObject vo, String vopk) {
		TaskScheduleBillVO[] updatevo = { (TaskScheduleBillVO) vo };
		try {
			NCLocator.getInstance().lookup(IShceduleFillInterface.class)
					.updateTaskScheduleBillVOS(null, updatevo);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private IplatFormEntry getIplatFormEntry() {
		return NCLocator.getInstance().lookup(IplatFormEntry.class);
	}

	private HashMap<String, Object> getEparam() {
		HashMap<String, Object> eParam = new HashMap<String, Object>();
		eParam.put("notechecked", "notechecked");
		return eParam;
	}
}
