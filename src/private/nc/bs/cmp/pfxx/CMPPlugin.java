package nc.bs.cmp.pfxx;

import java.util.HashMap;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pfxx.ISwapContext;
import nc.bs.pfxx.plugin.AbstractPfxxPlugin;
import nc.bs.trade.business.HYPubBO;
import nc.cmp.utils.DataUtil;
import nc.cmp.utils.InterfaceLocator;
import nc.cmp.utils.SettleUtils;
import nc.itf.org.IOrgUnitQryService;
import nc.itf.uap.pf.IWorkflowMachine;
import nc.itf.uap.pf.IplatFormEntry;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.bankaccount.BankAccSubVO;
import nc.vo.cmp.bill.BillAggVO;
import nc.vo.cmp.bill.BillDetailVO;
import nc.vo.cmp.bill.ChangeBillAggVO;
import nc.vo.cmp.bill.RecBillAggVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.org.OrgVO;
import nc.vo.pfxx.auxiliary.AggxsysregisterVO;
import nc.vo.pfxx.util.PfxxPluginUtils;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;

import org.apache.commons.lang.StringUtils;

public class CMPPlugin extends AbstractPfxxPlugin {
	public CMPPlugin() {
	}

	@SuppressWarnings("rawtypes")
	protected Object processBill(Object vo, ISwapContext swapContext,
			AggxsysregisterVO aggxsysvo) throws BusinessException {
		if (vo == null) {
			return null;
		}

		AbstractBill aggvo = null;

		if ((vo instanceof BillAggVO)) {
			aggvo = (BillAggVO) vo;
			BillDetailVO[] bodyVOs = (BillDetailVO[]) aggvo.getChildrenVO();
			for (int i = 0; i < bodyVOs.length; i++) {
				BillDetailVO body = bodyVOs[i];
				String pk_trader = null;
				Integer objecttype = bodyVOs[i].getObjecttype();
				String accnum = bodyVOs[i].getPk_account();
				String pk_currtype = bodyVOs[i].getPk_currtype();
				if (!StringUtils.isEmpty(body.getPk_supplier())) {
					pk_trader = body.getPk_supplier();
				} else if (!StringUtils.isEmpty(body.getPk_customer())) {
					pk_trader = body.getPk_customer();
				} else if (!StringUtils.isEmpty(body.getPk_busiman())) {
					pk_trader = body.getPk_busiman();
				}

				if (StringUtils.isEmpty(pk_trader)) {
					throw new BusinessException("交易对方不能为空！");
				}
				if (objecttype == null) {
					throw new BusinessException("交易对方类型不能为空！");
				}

				if (StringUtils.isEmpty(accnum)) {
					throw new BusinessException("对方银行账户不能为空！");
				}

				if (StringUtils.isEmpty(pk_currtype)) {
					throw new BusinessException("币种不能不能为空！");
				}

				if (objecttype != 4) {
					String pk_account = getOppAccount(accnum, pk_trader,
							objecttype, pk_currtype);
					if (StringUtils.isEmpty(pk_account)) {
						throw new BusinessException("根据对方信息币种找不到相应的账户档案！");
					}
					body.setPk_account(pk_account);
					body.setStatus(VOStatus.NEW);
				}
			}
		} else if ((vo instanceof RecBillAggVO)) {
			aggvo = (RecBillAggVO) vo;
		} else if ((vo instanceof ChangeBillAggVO)) {
			aggvo = (ChangeBillAggVO) vo;
		}

		setPk_org_v(aggvo);

		String vopk = PfxxPluginUtils.queryBillPKBeforeSaveOrUpdate(
				swapContext.getBilltype(), swapContext.getDocID());

		clearNo(aggvo);

		if (vopk != null) {
			Logger.info("如果单据已经导入过则进行单据更新...");
			if (swapContext.getReplace().equalsIgnoreCase("N")) {
				throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("3607mng_0", "03607mng-0359"));
			}
			String pk_tradetype = null;
			String pk_group = null;
			AggregatedValueObject[] vos = null;
			pk_tradetype = (String) aggvo.getParentVO().getAttributeValue(
					"trade_type");
			pk_group = (String) aggvo.getParentVO().getAttributeValue(
					"pk_group");

			fillNoteDate(aggvo);

			vos = (AggregatedValueObject[]) doWorkFlow("EDIT", aggvo,
					pk_tradetype, pk_group);

			vopk = vos[0].getParentVO().getPrimaryKey();
		} else {
			Logger.info("如果此单据没有导入过,保存新单据...");

			String pk_tradetype = null;
			String pk_group = null;
			AggregatedValueObject[] vos = null;
			pk_tradetype = (String) aggvo.getParentVO().getAttributeValue(
					"trade_type");
			pk_group = (String) aggvo.getParentVO().getAttributeValue(
					"pk_group");
			String billmaker = (String) aggvo.getParentVO().getAttributeValue(
					"billmaker");
			fillNoteDate(aggvo);
			// XBX 设置为制单人
			InvocationInfoProxy.getInstance().setUserId(billmaker);
			vos = (AggregatedValueObject[]) doWorkFlow("SAVE", aggvo,
					"F5", pk_group);
			// 付款结算审批单据
			// InvocationInfoProxy.getInstance().setUserId(getAdopter());
			HashMap hmPfExParams = new HashMap();
			WorkflownoteVO worknoteVO = ((IWorkflowMachine) NCLocator.getInstance()
					.lookup(IWorkflowMachine.class)).checkWorkFlow("APPROVE", "F5",
							vos[0], hmPfExParams);
			if (worknoteVO != null) {
				worknoteVO.setChecknote("批准");
				worknoteVO.setApproveresult("Y");
			}
			getIplatFormEntry().processAction("APPROVE", "F5", worknoteVO, aggvo,
					null, getEparam());
			vopk = vos[0].getParentVO().getPrimaryKey();

			PfxxPluginUtils.addDocIDVsPKContrast(swapContext.getBilltype(),
					swapContext.getDocID(), vopk);
		}
		// 付款结算提交单据

		// getIplatFormEntry().processAction("EDITTEMPSAVE", "F5	", null, aggvo,
		// null, null);


		return vopk;
	}

	private String getAdopter() throws UifException {
		HYPubBO hyPubBo = new HYPubBO();
		String adopter = (String) hyPubBo.findColValue("sys_config",
				"config_value", " config_key='oaadopt_cuser'");
		return adopter;
	}

	private HashMap<String, Object> getEparam() {
		HashMap<String, Object> eParam = new HashMap<String, Object>();
		eParam.put("notechecked", "notechecked");
		return eParam;
	}

	private IplatFormEntry getIplatFormEntry() {
		return NCLocator.getInstance().lookup(IplatFormEntry.class);
	}

	private void fillNoteDate(AbstractBill aggvo) {
		for (CircularlyAccessibleValueObject detail : aggvo.getChildrenVO()) {
			Object blanknote_no = detail.getAttributeValue("blanknote_no");
			if (blanknote_no != null) {

				detail.setAttributeValue("note_no", blanknote_no);
			}
		}
	}

	private String getOppAccount(String accnum, String pk_trader,
			Integer objecttype, String pk_currtype) throws BusinessException {
		String whereSql = " and 1=1 ";
		HYPubBO bo = new HYPubBO();
		BankAccSubVO[] subVOs = null;
		// 0=客户，1=供应商，2=部门，3=人员，4=散户
		if (objecttype == 0 || objecttype == 1) {
			whereSql = " exists (select 1 from bd_custbank where bd_custbank.pk_bankaccsub = bd_bankaccsub.pk_bankaccsub and pk_cust ='"
					+ pk_trader
					+ "') and bd_bankaccsub.pk_currtype='"
					+ pk_currtype
					+ "' and (bd_bankaccsub.accnum='"
					+ accnum
					+ "' or bd_bankaccsub.pk_bankaccsub = '" + accnum + "')";
			subVOs = (BankAccSubVO[]) bo.queryByCondition(BankAccSubVO.class,
					whereSql);
		} else if (objecttype == 2 || objecttype == 3) {
			whereSql = " exists (select 1 from bd_psnbankacc where bd_psnbankacc.pk_bankaccsub = bd_bankaccsub.pk_bankaccsub and pk_psndoc ='"
					+ pk_trader
					+ "') and bd_bankaccsub.pk_currtype='"
					+ pk_currtype
					+ "' and (bd_bankaccsub.accnum='"
					+ accnum
					+ "' or bd_bankaccsub.pk_bankaccsub = '" + accnum + "')";
			subVOs = (BankAccSubVO[]) bo.queryByCondition(BankAccSubVO.class,
					whereSql);
		}

		if (subVOs != null && subVOs.length > 0) {
			return subVOs[0].getPk_bankaccsub();
		}
		return null;

	}

	private void clearNo(AbstractBill aggvo) {
		aggvo.getParentVO().setAttributeValue("bill_no", null);
		for (CircularlyAccessibleValueObject detail : aggvo.getChildrenVO()) {
			detail.setAttributeValue("bill_no", null);
		}
	}

	private void setPk_org_v(AbstractBill aggvo) throws BusinessException {
		if (aggvo == null) {
			return;
		}
		OrgVO orgVO = null;
		CircularlyAccessibleValueObject head = aggvo.getParentVO();

		orgVO = ((IOrgUnitQryService) NCLocator.getInstance().lookup(
				IOrgUnitQryService.class)).getOrg((String) head
				.getAttributeValue("pk_org"));

		if (orgVO == null) {
			return;
		}

		head.setAttributeValue("pk_org_v", orgVO.getPk_vid());

		for (CircularlyAccessibleValueObject child : aggvo.getChildrenVO()) {
			orgVO = ((IOrgUnitQryService) NCLocator.getInstance().lookup(
					IOrgUnitQryService.class)).getOrg((String) child
					.getAttributeValue("pk_org"));

			child.setAttributeValue("pk_org_v", orgVO.getPk_vid());
		}
	}

	private Object doWorkFlow(String action, AggregatedValueObject aggvo,
			String pk_tradetype, String pk_group) throws BusinessException {
		IplatFormEntry iplatFormEntry = InterfaceLocator.getIplatFormEntry();
		HashMap<String, String> paramMap = new HashMap();
		paramMap.put("silently", "silently");
		return iplatFormEntry.processAction(action + DataUtil.getCurrentUser(),
				SettleUtils.getbilltype(pk_tradetype, pk_group), null, aggvo,
				null, paramMap);
	}
}
